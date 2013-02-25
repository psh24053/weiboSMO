package com.psh.query.servlet;

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

import org.apache.poi.hssf.record.FnGroupCountRecord;

import com.psh.base.util.PshLogger;
import com.psh.base.util.SQLConn;

/**
 * Servlet implementation class ExportActivation
 */
public class ExportActivation extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ExportActivation() {
        super();
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
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain;charset=utf-8"); 
		
		String type = request.getParameter("type");
		
		StringBuffer fileContent = new StringBuffer();
		
		fileContent.append("email\tpassowrd\tnickname\r\n");
		
		if(type.equals("1")){
			// 导出所有可用的账号
			
			Connection conn = SQLConn.getInstance().getConnection();
			ResultSet rs = null;
			PreparedStatement pstmt = null;
			try {
				pstmt = conn.prepareStatement("SELECT a.email AS email,a.`password` AS PASSWORD,a.`nickname` AS nickname FROM wb_activation ac,wb_reg_account a WHERE a.`aid` = ac.`aid` AND (ac.status = 91 OR ac.status = 89 OR ac.status = 90)");
				
				rs = pstmt.executeQuery();
				while(rs.next()){
					String item = rs.getString("email") + "\t";
					item += rs.getString("password") + "\t";
					item += rs.getString("nickname") + "\t";
					item += "\r";
					fileContent.append(item + "\n");
				}
				
				
			} catch (SQLException e) {
				PshLogger.logger.error(e.getMessage());
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
			
			
		}else if(type.equals("2")){
			// 待激活账号
			
			Connection conn = SQLConn.getInstance().getConnection();
			ResultSet rs = null;
			PreparedStatement pstmt = null;
			try {
				pstmt = conn.prepareStatement("SELECT a.email AS email,a.`password` AS PASSWORD,a.`nickname` AS nickname FROM wb_activation ac,wb_reg_account a WHERE a.`aid` = ac.`aid` AND ac.status = 88");
				
				rs = pstmt.executeQuery();
				while(rs.next()){
					String item = rs.getString("email") + "\t";
					item += rs.getString("password") + "\t";
					item += rs.getString("nickname") + "\t";
					item += "\r";
					fileContent.append(item + "\n");
				}
				
				
			} catch (SQLException e) {
				PshLogger.logger.error(e.getMessage());
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
			
			
		}else if(type.equals("3")){
			// 老版账号
			Connection conn = SQLConn.getInstance().getConnection();
			ResultSet rs = null;
			PreparedStatement pstmt = null;
			try {
				pstmt = conn.prepareStatement("SELECT a.email AS email,a.`password` AS PASSWORD,a.`nickname` AS nickname FROM wb_activation ac,wb_reg_account a WHERE a.`aid` = ac.`aid` AND (ac.status = 1 OR ac.status = 3 OR ac.status = 4 OR ac.status = 11 OR ac.status = 33 OR ac.status = 44)");
				
				rs = pstmt.executeQuery();
				while(rs.next()){
					String item = rs.getString("email") + "\t";
					item += rs.getString("password") + "\t";
					item += rs.getString("nickname") + "\t";
					item += "\r";
					fileContent.append(item + "\n");
				}
				
				
			} catch (SQLException e) {
				PshLogger.logger.error(e.getMessage());
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
			
		}else if(type.equals("4")){
			// 所有在注册成功的账号
			Connection conn = SQLConn.getInstance().getConnection();
			ResultSet rs = null;
			PreparedStatement pstmt = null;
			try {
				pstmt = conn.prepareStatement("SELECT a.email AS email,a.`password` AS PASSWORD,a.`nickname` AS nickname FROM wb_reg_account a");
				
				rs = pstmt.executeQuery();
				while(rs.next()){
					String item = rs.getString("email") + "\t";
					item += rs.getString("password") + "\t";
					item += rs.getString("nickname") + "\t";
					item += "\r";
					fileContent.append(item + "\n");
				}
				
				
			} catch (SQLException e) {
				PshLogger.logger.error(e.getMessage());
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
		}
		
		
		
		PrintWriter out = response.getWriter();
		
		response.setHeader("Content-Transfer-Encoding","binary");   
		response.setHeader("Cache-Control","must-revalidate;post-check=0;pre-check=0");        
		response.setHeader("Pragma", "public"); 

		response.addHeader("Content-disposition", "attachment;filename=account.txt"); 
		out = response.getWriter(); 
		out.write(fileContent.toString()); 
		out.flush();
		out.close();
		
	}

}
