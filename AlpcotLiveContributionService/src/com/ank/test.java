package com.ank;

import java.io.File;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(System.getProperty("file.encoding"));
	     System.out.println(System.getProperty("sun.jnu.encoding"));

	    String filePath = "C:/Logs/техт.txt";
	     System.out.println("File Path" + filePath);

	         File file = new File(filePath);
	         try {
	            if(file.exists())
	                System.out.println("length: " + file.length());
	            else{
	                System.out.println("file not found");
	            }
	        } catch (Exception e) {
	            System.out.println("inside exception");
	        }

	}

}
