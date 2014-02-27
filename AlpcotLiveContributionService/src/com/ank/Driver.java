package com.ank;

import org.apache.log4j.Logger;

public class Driver {
	
	private static int DELAY_FEEDER = 1 * 60 * 1000;

    private static Logger log = Logger.getLogger(PHPClient.class);
    private static boolean stop = false;
    
    private static PHPClient phpClient;

    public static void start(String[] args) {
        while (!stop) {
        	if (phpClient == null) {
					phpClient = new PHPClient();
        	}
        	if (phpClient != null) {
        		phpClient.update();
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
