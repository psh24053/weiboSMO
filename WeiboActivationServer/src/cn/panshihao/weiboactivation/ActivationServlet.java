package cn.panshihao.weiboactivation;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Servlet implementation class ActivationServlet
 */
@WebServlet("/ActivationServlet")
public class ActivationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

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
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter out = response.getWriter();
		
		String email = request.getParameter("email");
		String url = request.getParameter("url");
		
		if(email == null || url == null){
			out.write("Paramster error!");
			out.flush();
			out.close();
			return;
		}
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int aid = -1;
		System.out.println(email +" , "+url);
		try {
			conn = db.getConnection();
			pstmt = conn.prepareStatement("SELECT aid FROM wb_account WHERE email = ?");
			pstmt.setString(1, email);
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				aid = rs.getInt("aid");
				System.out.println(aid +" , ");
			}else{
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
			e.printStackTrace();
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
		
		out.write("Insert Success!");
		out.flush();
		out.close();
		System.out.println("已插入数据库   aid: "+aid+" ,email: "+email);

		
		// 启动激活线程
		new ActivationService(aid, email, url).start();
		
		
		
	}

}
