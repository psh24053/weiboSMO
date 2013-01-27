package cn.panshihao.desktop.commons;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//读取配置文件
public class ConfigHandler {
	
	private String nowSeparator = File.separator;
	
	/**
	 * @author penglang
	 * @param configName(配置文件名称),keyName(要获取的字段名)
	 * @return String(需要获取的字段名对应的值)
	 */
	public String getConfig(String configName,String keyName){
		
		String keyValue = "";
		
		Log.log.debug("Get config porperties info,config file name = " + configName + ",keyname=" + keyName);
		
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("cn" + nowSeparator +
				"panshihao" + nowSeparator + "desktop" + nowSeparator + "commons" + nowSeparator + configName);    
		
		Properties properties = new Properties();    
		
		try {    
			
			properties.load(inputStream);    
		
		} catch (IOException e) {    
			Log.log.error(e.getMessage());
		}    
		
		keyValue = properties.getProperty(keyName);
		  
		return keyValue;
		
	}
	

}
