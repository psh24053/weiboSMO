package cn.panshihao.weiboactivation;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Servlet implementation class ActivationServlet
 */
@WebServlet("/ActivationServlet")
public class ActivationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static int total = 0;
	
	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		
//		if(Tools.db == null){
//			
//			Tools.db = new ComboPooledDataSource();
//			try {
//				Tools.db.setDriverClass("com.mysql.jdbc.Driver");
//			} catch (PropertyVetoException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			Tools.db.setJdbcUrl("jdbc:mysql://localhost:3306/wb_reg?useUnicode=true&characterEncoding=UTF-8");
//			Tools.db.setUser("root");
//			Tools.db.setPassword("root");
//			Tools.db.setMaxPoolSize(100);
//			Tools.db.setInitialPoolSize(5);
//			Tools.db.setMaxIdleTime(60);
//		}
		
//		if(Tools.proxyService == null){
//			
//			Tools.proxyService = new ProxyService();
//			Tools.proxyService.loadProxyData();
//		}
		
		if(Tools.executorService == null){
			Tools.executorService = Executors.newCachedThreadPool();
		}
		
    	
		
	}
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
		if(Tools.executorService == null){
			Tools.executorService.shutdown();
		}
	}
	
    /**
     * Default constructor. 
     */
    public ActivationServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		total++;
		System.out.println("当前接收到url: "+total);
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		String email = request.getParameter("email");
		String url = request.getParameter("url");
		
		if(email == null || url == null){
			Tools.log.error("Paramster Error");
			return;
		}
		response.flushBuffer();
		
		
		/*
		 * 执行插入到数据库的逻辑 
		 */
		insertDB(email, url, total);


	}
	/**
	 * 插入到数据库。
	 * @param email
	 * @param url
	 */
	public void insertDB(String email, String url, int index){
		
		email = URLDecoder.decode(email);
		url = URLDecoder.decode(url);
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int aid = -1;
		try {
			conn = Tools.getMysqlConn();
			if(conn == null){
				System.out.println("get mysql conn error");
				return;
			}
			pstmt = conn.prepareStatement("SELECT aid FROM wb_account WHERE email = ?");
			pstmt.setString(1, email);
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				aid = rs.getInt("aid");
				System.out.println("aid -> "+aid);
			}else{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				insertDB(email, url, index);
				return;
			}
			
			
			pstmt = conn.prepareStatement("insert into wb_activation(aid,email,url,status) values(?,?,?,?)");
			
			pstmt.setInt(1, aid);
			pstmt.setString(2, email);
			pstmt.setString(3, url);
			pstmt.setInt(4, 0);
			
			pstmt.executeUpdate();
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			return;
		} finally {
			
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		wb_proxyModel proxy = Tools.proxyService.getAvailableProxyModel();		
		Tools.executorService.execute(new ActivationService(aid, email, url, proxy));
		
		System.out.println("已插入数据库   aid: "+aid+" ,email: "+email+" ,index: "+index);
		
	}
	
	
}
