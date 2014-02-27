package com.ank;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.TimeZone;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.log4j.Logger;

import com.ank.beans.StockUpload;
import com.ank.clients.adb.AlpcotServiceStub;
import com.ank.clients.adb.AlpcotServiceStub.Stock;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ADBClient{
	public static Logger log = Logger.getLogger(ADBClient.class);
	public static String FILE_NAME = "C:/script/competition-feed.txt";
	public static String FILE_NAME_URL = "C:/script/competition-feedUrl.txt";
	private AlpcotServiceStub stub;
	private Gson gson;
	private Deque<StockUpload> stockStack = new ArrayDeque<>();
	private static final int ALLOWED_DIFFERENCE = 9 * 60 * 1000;
	private static final TimeZone timeZone = TimeZone.getTimeZone("CET");
	
    public static void main(java.lang.String args[]){
        try{
        	ADBClient stub =
                new ADBClient();
        	stub.update();

        } catch(Exception e){
        	log.error(e.getMessage(), e);
        }
    }
    
    public ADBClient() throws AxisFault {
    	BufferedReader bufferedReader = null;
    	String dataUrl = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(FILE_NAME_URL));
			dataUrl = bufferedReader.readLine();
		} catch (FileNotFoundException e) {
			log.info("data file not found", e);
		} catch (IOException e) {
			log.info("data file error", e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					log.info(e);
				}
			}
		}
		
		if (dataUrl != null) {
			log.debug("data url: " + dataUrl);
			stub = new AlpcotServiceStub(dataUrl);
		} else {
			stub = new AlpcotServiceStub();
		}
    	Options options = stub._getServiceClient().getOptions();
    	options.setProperty(HTTPConstants.CHUNKED, false);
    	
    	OMFactory omFactory = OMAbstractFactory.getOMFactory();
    	OMElement omSecurityElement = omFactory.createOMElement(new QName( "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security", "wsse"), null);


    	OMElement omusertoken = omFactory.createOMElement(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "UsernameToken", "wsu"), null);

    	OMElement omuserName = omFactory.createOMElement(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Username", "wsse"), null);
    	omuserName.setText("webServicesUser");

    	OMElement omPassword = omFactory.createOMElement(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Password", "wsse"), null);
    	omPassword.addAttribute("Type","http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText",null );
    	omPassword.setText("abc123");

    	omusertoken.addChild(omuserName);
    	omusertoken.addChild(omPassword);
    	omSecurityElement.addChild(omusertoken);
    	stub._getServiceClient().addHeader(omSecurityElement);
    	
    	gson =  new GsonBuilder().setDateFormat("dd.MM.yyyy HH:mm").create();
    }

    public  void update(){
    	updateWithDelay(0);
    }
    
    public  void updateWithDelay(int delayMins){
        try{
        	StockUpload book = null;
        	StockUpload currentBook = null;
        	 try {
                 BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_NAME));
                 currentBook = gson.fromJson(bufferedReader, StockUpload.class);

             } catch (IOException e) {
            	 log.error("could not read file: "+FILE_NAME, e);
             }
        	 
        	 boolean uploadTimeValid = true;
        	 
			if (currentBook != null && currentBook.getTime() != null && 
					currentBook.getStock() != null && !currentBook.getStock().isEmpty()) {
				
				Date nowDate = new Date();
				long now = nowDate.getTime();
				
				if (delayMins > 0) {
					stockStack.offer(currentBook);
					
					if (now - stockStack.peek().getTime().getTime() >= delayMins) {
						book = stockStack.poll();
					}
				} else {
					book = currentBook;
				}
				
				//if data is too old
				if (book != null && now - book.getTime().getTime() > delayMins + ALLOWED_DIFFERENCE) {
					log.debug("Data is too old, now: " + nowDate + ", data time: " + book.getTime() );
					uploadTimeValid = false;
				}
			}
        	 
        	if (uploadTimeValid && book != null && book.getStock() != null && !book.getStock().isEmpty()) {
        		
        		AlpcotServiceStub.ProvideStocksE reqE = new AlpcotServiceStub.ProvideStocksE();
            	AlpcotServiceStub.ProvideStocks req = new AlpcotServiceStub.ProvideStocks();
            	Stock stock;
            	
            	for (com.ank.beans.Stock bookedStock : book.getStock()) {
            		Double value = null;
            		
            		try{
            			value = Double.valueOf(bookedStock.getValue().replace(',', '.'));
            		} catch (NumberFormatException e){
            			log.debug(e);
            			continue;
            		}
            		
            		stock = new Stock();
                	stock.setName(bookedStock.getName());
                	
                	stock.setPrice(new BigDecimal(value));
                	
                	//CET timezone
    				Calendar cal = Calendar.getInstance();
                	cal.setTimeInMillis(book.getTime().getTime());
                	cal.setTimeZone(timeZone);
                	stock.setTimestamp(cal);
                    req.addArg0(stock);
            	}
            	reqE.setProvideStocks(req);
                stub.provideStocks(reqE);
        	} else {
        		log.debug("Data is either invalid or too old. Upload was not done");
        	}
        } catch(Exception e){
            log.error("Error updating", e);
        }
    }

}
