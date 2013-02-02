package cn.panshihao.weiboactivation;

import java.beans.PropertyVetoException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Application Lifecycle Listener implementation class ServerStartListener
 *
 */
@WebListener
public class ServerStartListener implements ServletContextListener {

	
	
    /**
     * Default constructor. 
     */
    public ServerStartListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
//    	Tools.db = new ComboPooledDataSource();
//		try {
//			Tools.db.setDriverClass("com.mysql.jdbc.Driver");
//		} catch (PropertyVetoException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		Tools.db.setJdbcUrl("jdbc:mysql://localhost:3306/wb_reg?useUnicode=true&characterEncoding=UTF-8");
//		Tools.db.setUser("root");
//		Tools.db.setPassword("root");
//		Tools.db.setMaxPoolSize(100);
//		Tools.db.setInitialPoolSize(5);
//		Tools.db.setMaxIdleTime(60);
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
    	Tools.db.close();
    }
	
}
