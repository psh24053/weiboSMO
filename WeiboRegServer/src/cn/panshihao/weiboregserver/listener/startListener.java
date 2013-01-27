package cn.panshihao.weiboregserver.listener;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import cn.panshihao.weiboregserver.CacheManager;
import cn.panshihao.weiboregserver.tools.SqlHandler;

/**
 * Application Lifecycle Listener implementation class startListener
 *
 */
@WebListener
public class startListener implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public startListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
    	SqlHandler.dataSource = new ComboPooledDataSource();
    	
    	try {
			SqlHandler.dataSource.setDriverClass("com.mysql.jdbc.Driver");
		} catch (PropertyVetoException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	SqlHandler.dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/wb_reg?useUnicode=true&characterEncoding=UTF-8");
    	SqlHandler.dataSource.setUser("root");
    	SqlHandler.dataSource.setPassword("root");
    	SqlHandler.dataSource.setMaxPoolSize(100);
    	SqlHandler.dataSource.setInitialPoolSize(5);
    	SqlHandler.dataSource.setMaxIdleTime(60);
    	
    	
    	CacheManager cachemanager = CacheManager.instance;
    	
    	Connection conn = null;
		try {
			conn = SqlHandler.dataSource.getConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

    	/*
    	 * 获取数据库中已存在的账号数量
    	 */
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	try {
    		pstmt = conn.prepareStatement("select email from wb_account");
    		rs = pstmt.executeQuery();
    		
    		while(rs.next()){
    			cachemanager.getEmailCache().put(rs.getString("email"), "");
    		}
    		
    		cachemanager.setDBCount(cachemanager.getEmailCache().size());
    		System.out.println("已从服务器中加载账号 -> "+cachemanager.getEmailCache().size());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				rs.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	
    	
    	
    	
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
    }
	
}
