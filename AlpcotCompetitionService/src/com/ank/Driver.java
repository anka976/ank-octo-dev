package com.ank;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

public class Driver {
	
	private static int DELAY_CLIENT = 15 * 60 * 1000;
	private static int DELAY_FEEDER = 3 * 60 * 1000;

    private static Logger log = Logger.getLogger(ADBClient.class);
    private static boolean stop = false;
    
    private static ADBClient adbClient;

    public static void start(String[] args) {
        while (!stop) {
        	if (adbClient == null) {
        		try {
					adbClient = new ADBClient();
				} catch (AxisFault e) {
					log.error("error creating client", e);
				}
        	}
        	if (adbClient != null) {
        		adbClient.updateWithDelay(DELAY_CLIENT);
        	}
            try {
                Thread.sleep(DELAY_FEEDER);
            } catch (InterruptedException e) {
            }
            log.debug("running");
        }
    }

    public static void stop(String[] args) {
        log.info("stop");
        stop = true;
    }

    public static void main(String args[]) {
        String mode = args[0];
        switch (mode) {
            case "start":
                start(args);
                break;
            case "stop":
                stop(args);
                break;
        }
    }
}
