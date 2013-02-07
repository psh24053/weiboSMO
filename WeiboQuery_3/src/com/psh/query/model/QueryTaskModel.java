package com.psh.query.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.psh.base.util.PshLogger;
import com.psh.base.util.SQLConn;
import com.psh.query.bean.QueryTaskBean;

public class QueryTaskModel {
	
	private Connection conn = null;
	private PreparedStatement ps = null;
	private ResultSet rs = null;
	
	//增加搜索任务
	public int addQueryTask(QueryTaskBean query){
		conn = SQLConn.getInstance().getConnection();
		int insertID = -1;
		
		try {
			ps = conn.prepareStatement("insert into wb_querytask(qnck,qtag,qsch,qcom,qutype,qprov,qcity,qage,qsex,qdate) values(?,?,?,?,?,?,?,?,?,?)");
			ps.setString(1, query.getQnck());
			ps.setString(2, query.getQtag());
			ps.setString(3, query.getQsch());
			ps.setString(4, query.getQcom());
			ps.setString(5, query.getQutype());
			ps.setString(6, query.getQprov());
			ps.setString(7, query.getQcity());
			ps.setString(8, query.getQage());
			ps.setString(9, query.getQsex());
			ps.setLong(10, query.getQdate());
			
			int result = ps.executeUpdate();
			
			if(result > 0){
				
				PreparedStatement ps_1 = conn.prepareStatement("SELECT @@IDENTITY;");
				ResultSet queryResult = ps_1.executeQuery();
				if (null != queryResult && queryResult.next()) {
					insertID = queryResult.getInt(1);
				}
				
			}
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		}finally{
			this.closeConnection();
		}
		
		return insertID;
		
		
	}
	
	
	//根据ID获取一条查询任务信息
	public synchronized QueryTaskBean getQueryTaskInfoByID(int queryTaskID) {
		
		conn = SQLConn.getInstance().getConnection();
		
		QueryTaskBean query = new QueryTaskBean();
		
		try {
			ps = conn.prepareStatement("select * from wb_querytask where qtid=" + queryTaskID);
	
			rs = ps.executeQuery();
			
			if(rs == null){
				
				return null;
				
			}
			
			if(rs.next()){
				
				query.setQnck(rs.getString("qnck"));
				query.setQtag(rs.getString("qtag"));
				query.setQprov(rs.getString("qprov"));
				query.setQcity(rs.getString("qcity"));
				query.setQage(rs.getString("qage"));
				query.setQsex(rs.getString("qsex"));
				
			}
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		}finally{
			this.closeConnection();
		}
		
		
		return query;
	}
	
	private synchronized void closeConnection(){
		
		if(this.rs != null){
			
			try{
				rs.close();
				rs = null;
			}catch(SQLException e){
				PshLogger.logger.error(e.getMessage());
			}
			
		}
		
		if (this.ps != null) {

			try {
				ps.close();
				ps = null;
			} catch (SQLException e) {
				PshLogger.logger.error(e.getMessage());
			}

		}

		try {
			if (this.conn != null || !this.conn.isClosed()) {

				try {
					conn.close();
					conn = null;
				} catch (SQLException e) {
					PshLogger.logger.error(e.getMessage());
				}
			}
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		}
		
	}

}
