package cn.panshihao.activationclient;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Random;
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
			return DriverManager.getConnection("jdbc:mysql://localhost:3306/wbdb?useUnicode=true&characterEncoding=UTF-8", "root", "root");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return null;
		}
		
	}
	
	public static String RandomChineseChar() {

       String str = null;

       int hightPos, lowPos; // 定义高低位

       Random random = new Random();

       hightPos = (176 + Math.abs(random.nextInt(39)));//获取高位值

       lowPos = (161 + Math.abs(random.nextInt(93)));//获取低位值

       byte[] b = new byte[2];

       b[0] = (new Integer(hightPos).byteValue());

       b[1] = (new Integer(lowPos).byteValue());

    	try {
			str = new String(b, "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//转成中文

       return str;

    }
	
	public static void main(String[] args) {
		
		System.out.println(RandomChineseChar());
		
	}
	
	
	
}
