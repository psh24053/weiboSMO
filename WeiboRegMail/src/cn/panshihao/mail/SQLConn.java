package cn.panshihao.mail;

import java.beans.PropertyVetoException;


import com.mchange.v2.c3p0.ComboPooledDataSource;


//获得数据库连接 
public class SQLConn {
	
	public static ComboPooledDataSource db;
	
	static{
		db = new ComboPooledDataSource();
		try {
			db.setDriverClass("com.mysql.jdbc.Driver");
		} catch (PropertyVetoException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	db.setJdbcUrl("jdbc:mysql://localhost:3306/wb_reg?useUnicode=true&characterEncoding=UTF-8");
    	db.setUser("root");
    	db.setPassword("root");
    	db.setMaxPoolSize(100);
    	db.setInitialPoolSize(5);
    	db.setMaxIdleTime(60);
		
		
	}
	
}
