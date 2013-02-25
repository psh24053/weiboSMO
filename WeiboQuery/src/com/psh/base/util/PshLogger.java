package com.psh.base.util;

import java.io.File;

import org.apache.log4j.*;

public class PshLogger {

	public static final String LOG4J_PROPERTIES_FILE = "log4j.properties";
	
	public static final String loggerName = "com.psh.base";
	
	public static Logger logger = Logger.getLogger(loggerName);
	
	static {
		String log4jProperties = PshConfigManager.getConfPath() + File.separator + LOG4J_PROPERTIES_FILE;
		PropertyConfigurator.configure(log4jProperties);
	}
	
	public PshLogger() {
		
	}
	
	public Logger logger() {
		return Logger.getLogger("com.psh.base");
	}
	
	// TODO: detailed log configuration
}
