package cn.panshihao.weiboactivation;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
		
		String email = request.getParameter("email");
		String url = request.getParameter("url");

		
		
		if(email == null || url == null){
			Tools.log.error("Paramster Error");
			return;
		}
		email = URLDecoder.decode(email);
		url = URLDecoder.decode(url);
		
		Tools.log.debug("doPost email -> "+email+" ,url -> "+url);
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int aid = -1;
		try {
			conn = Tools.getMysqlConn();
			if(conn == null){
				Tools.log.error("get mysql conn error");
				return;
			}
			pstmt = conn.prepareStatement("SELECT aid FROM wb_account WHERE email = ?");
			pstmt.setString(1, email);
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				aid = rs.getInt("aid");
				Tools.log.debug("aid -> "+aid);
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
			Tools.log.error(e.getMessage(), e);
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
		
		Tools.log.debug("已插入数据库   aid: "+aid+" ,email: "+email+" ,url: "+url);

		
		// 启动激活线程
//		new ActivationService(aid, email, url).start();
		
		
		
	}

}
