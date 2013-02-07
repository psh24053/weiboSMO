package com.psh.base.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import com.psh.query.bean.ProxyBean;
import com.psh.query.model.ProxyModel;
import com.psh.query.util.ProxyManager;

//获得数据库连接 
public class SQLConn {
	
	private static SQLConn instance = null;
	
	private static String databaseUrl = "jdbc:mysql://127.0.0.1/wbdb?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false&maxReconnects=5";
	
	private static String driverClassName = "com.mysql.jdbc.Driver";
	
	private static String databaseUserName = "root";
	
	private static String databaseUserPassword = "root";
	
	private static DataSource datasource = null;
	
	public static synchronized SQLConn getInstance() {
		if ( null == instance) {
			instance = new SQLConn();
		}
		return instance;
	}

	private SQLConn() {
		
		initProxy();

	}
	
	private void initProxy(){
		
		PoolProperties p = new PoolProperties();
		p.setUrl(databaseUrl);
		p.setDriverClassName(driverClassName);
	    p.setUsername(databaseUserName);
	    p.setPassword(databaseUserPassword);
	    p.setJmxEnabled(true);
	    p.setTestWhileIdle(false);
	    p.setTestOnBorrow(true);
	    p.setValidationQuery("SELECT 1");
	    p.setTestOnReturn(false);
	    p.setValidationInterval(30000);
	    p.setTimeBetweenEvictionRunsMillis(1000);
	    p.setMaxActive(500);
	    p.setInitialSize(50);
	    p.setMaxWait(20);
	    p.setRemoveAbandonedTimeout(60);
	    p.setMinEvictableIdleTimeMillis(3000);
	    p.setMinIdle(50);
	    datasource = new DataSource();
	    datasource.setPoolProperties(p);
		
	}
	
	public Connection getConnection(){
		
		Connection conn = null;
		
		try {
			
			conn = datasource.getConnection();
		
		} catch (SQLException e) {

			PshLogger.logger.error(e.getMessage());
			
		}
		
		return conn;
		
	}
	
}
