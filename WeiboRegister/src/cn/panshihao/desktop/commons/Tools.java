package cn.panshihao.desktop.commons;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class Tools {

	
	public static Connection getMysqlConn(){
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			return null;
		}
		
		try {
			return DriverManager.getConnection("jdbc:mysql://localhost:3306/wbdb?useUnicode=true&characterEncoding=UTF-8", "root", "root");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return null;
		}
		
	}
	
}
