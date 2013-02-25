package com.psh.base.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Properties;

public final class PshConfigManager {

	static public final String SHNTEC_CONF_FOLDER = "config";
	static public final String SHNTEC_PROPERTIES_FILE = "weibo.properties";
	
	static private PshConfigManager instance = null;
	static private Properties shntecProperties = null;
	
	static public LinkedList<String> platformActionPackage = null;;
	// Application action package
	static public LinkedList<String> applicationActionPackage = null;
	
	static public synchronized PshConfigManager getInstance() {

		if (null == instance ) {
			instance = new PshConfigManager();
		}
		
		return instance;
	}
	
	private PshConfigManager() {
		init();
		loadConfig();
	}

	private void init() {
	
		platformActionPackage = new LinkedList<String>();
		applicationActionPackage = new LinkedList<String>();
		
	}
	
	// Read configuration from default properties file.
	private void loadConfig() {
		// Find properties file from class path
		String shntecPropertiesFilePath = getConfPath() + File.separator + SHNTEC_PROPERTIES_FILE;
		
		PshLogger.logger.debug("Start to read golbal properties file: " + shntecPropertiesFilePath);
		
		try {
			FileInputStream shntecPropertiesFileStream = new FileInputStream(shntecPropertiesFilePath);
			shntecProperties = new Properties();
			shntecProperties.load(shntecPropertiesFileStream);
			shntecPropertiesFileStream.close();
		} catch (IOException e) {
			PshLogger.logger.error("Read global properties file: " + shntecPropertiesFilePath + " failed!");
			PshLogger.logger.error(e.getMessage());
			return;
		}
		// Base platform action package
		String platformActionPackageArray[] = shntecProperties.getProperty("PlatformActionPackage", "com.shntec.bp.action").split(":");
		for (int i=0; i<platformActionPackageArray.length; ++i) {
			String actionPackage = platformActionPackageArray[i].trim();
			if (!actionPackage.isEmpty()) {
				platformActionPackage.push(actionPackage);
			}
		}
		
		// Application action package
		String applicationActionPackageArray[] = shntecProperties.getProperty("ApplicationActionPackage", "").split(":");
		for (int i=0; i<applicationActionPackageArray.length; ++i) {
			String actionPackage = applicationActionPackageArray[i].trim();
			if (!actionPackage.isEmpty()) {
				applicationActionPackage.push(actionPackage);
			}
		}
		PshLogger.logger.debug("Read golbal properties file: " + shntecPropertiesFilePath + " finished");
	}
	
	// Return path looks like "/var/www/webapp/appname/conf"
	public static String getConfPath () {

		String confPath = null;

		URL propertiesURL = PshConfigManager.class.getClassLoader().getResource(SHNTEC_PROPERTIES_FILE);
		
		if (propertiesURL != null) {
			String propertiesPath = propertiesURL.getPath();
			confPath = propertiesPath.substring(0, propertiesPath.lastIndexOf("/"));
		}
		else {
			confPath = CommonUtil.getWebappRoot() + File.separator + "WEB-INF" + 
					File.separator + SHNTEC_CONF_FOLDER;
		}
	
		return confPath;
	} 
	
}
