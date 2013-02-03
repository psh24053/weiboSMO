package cn.panshihao.proxysearch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;


public class Tools {

	public static Logger log = Logger.getLogger(Tools.class);
	
	
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
