package com.ank;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

public class ADBClientTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out.println("====TESTING======");
			ADBClient adbClient = new ADBClient();
			adbClient.updateWithDelay(0);
		} catch (AxisFault e) {
			Logger.getLogger(ADBClientTest.class).error("AxisFault error", e);
		}

	}


}
