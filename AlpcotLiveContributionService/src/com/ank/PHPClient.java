package com.ank;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.RijndaelEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.paddings.ZeroBytePadding;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PHPClient {
	public static Logger log = Logger.getLogger(PHPClient.class);
	public static String FILE_NAME = "C:/script/test.txt";
	public static String FILE_NAME_URL = "C:/script/testUrl.txt";
	public static String sKy = "bbee9a3e8e44e28edb4539186d182a24";
	public static String sIV = "131a68dc13160766f37dc931d7e518b2";
	public static String ENCRIPT_TAIL = "_ALP(HB)_AAGRF ETF_SEK";

	public static void main(java.lang.String args[]) {
		PHPClient stub = new PHPClient();
		stub.update();
	}

	public PHPClient() {
	}

	public void update() {
		String data = null;
		String dataUrl = null;

		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(FILE_NAME));
			data = bufferedReader.readLine();
		} catch (FileNotFoundException e) {
			log.error("data file not found", e);
		} catch (IOException e) {
			log.error("data file error", e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					log.info(e);
				}
			}
		}

		bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(FILE_NAME_URL));
			dataUrl = bufferedReader.readLine();
		} catch (FileNotFoundException e) {
			log.error("data file not found", e);
		} catch (IOException e) {
			log.error("data file error", e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					log.info(e);
				}
			}
		}
		
		try {

			String[] dataParts = data.split(" ", 2);

			byte[] sessionKey = sKy.getBytes();
			byte[] iv = sIV.getBytes();
			byte[] plaintext = (dataParts[0] + ENCRIPT_TAIL).getBytes();
			
			PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(
					new CBCBlockCipher(new RijndaelEngine(256)), new ZeroBytePadding());

			int keySize = 256 / 8;

			CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(sessionKey, 0, keySize), iv, 0, keySize);

			cipher.init(true, ivAndKey);
			byte[] encrypted = new byte[cipher.getOutputSize(plaintext.length)];
			int oLen = cipher.processBytes(plaintext, 0, plaintext.length, encrypted, 0);
			cipher.doFinal(encrypted, oLen);
			System.out.println(Arrays.toString(encrypted));
			String encoded = Base64.encodeBase64String(encrypted);
			System.out.println(encoded);
			HttpClient httpclient = new DefaultHttpClient();

			HttpPost httppost = new HttpPost(dataUrl);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("raw", dataParts[0]));
			nameValuePairs.add(new BasicNameValuePair("time", dataParts[1]));
			nameValuePairs.add(new BasicNameValuePair("key", encoded));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");

			 HttpResponse response = httpclient.execute(httppost);
			 log.error("Responce: " + response);
		} catch (IOException | DataLengthException | IllegalStateException
				| InvalidCipherTextException e) {
			log.error("data send error", e);
		}

	}

}
