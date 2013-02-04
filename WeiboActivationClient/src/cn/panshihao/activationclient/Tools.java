package cn.panshihao.activationclient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class Tools {

	public static ComboPooledDataSource db;
	public static Logger log = Logger.getLogger(Tools.class);
	public static ProxyService proxyService;
	public static ExecutorService executorService;
	
	
	public static Connection getMysqlConn(){
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			return null;
		}
		
		try {
			return DriverManager.getConnection("jdbc:mysql://localhost:3306/wb_reg?useUnicode=true&characterEncoding=UTF-8", "root", "root");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return null;
		}
		
	}
	
}
