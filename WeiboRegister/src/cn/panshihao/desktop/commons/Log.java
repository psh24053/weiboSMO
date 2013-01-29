package cn.panshihao.desktop.commons;

import java.io.File;

import org.apache.log4j.*;

public class Log {

	public static Logger log = Logger.getLogger("log4j.properties");
	
	private static String nowSeparator = File.separator;
	
	//加载配置文件
	static {
		PropertyConfigurator.configure("log4j.properties");
	}
	
	private Log() {
		
	}
	
}
