package com.ank;

import org.apache.log4j.Logger;

public class PHPClientTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out.println("====TESTING CONTRIBUTION======");
			PHPClient phpClient = new PHPClient();
			phpClient.update();
		} catch (Exception e) {
			Logger.getLogger(PHPClientTest.class).error("Error", e);
		}

	}


}
