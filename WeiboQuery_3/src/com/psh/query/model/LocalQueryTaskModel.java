package com.psh.query.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.psh.base.util.PshLogger;
import com.psh.base.util.SQLConn;
import com.psh.query.bean.LocalQueryTaskBean;

public class LocalQueryTaskModel {
	
	private Connection conn = null;
	private PreparedStatement ps = null;
	private ResultSet rs = null;
	
	//增加搜索任务
	public int addLocalQueryTask(LocalQueryTaskBean localQuery){
		conn = SQLConn.getInstance().getConnection();
		int insertID = -1;
		
		try {
			ps = conn.prepareStatement("insert into wb_local_querytask(lqnck,lqtag,lqsch,lqcom,lqprov,lqcity,lqage,lqsex,lqdate,lfans,lfol) values(?,?,?,?,?,?,?,?,?,?,?)");
			ps.setString(1, localQuery.getLqnck());
			ps.setString(2, localQuery.getLqtag());
			ps.setString(3, localQuery.getLqsch());
			ps.setString(4, localQuery.getLqcom());
			ps.setString(6, localQuery.getLqprov());
			ps.setString(7, localQuery.getLqcity());
			ps.setString(8, localQuery.getLqage());
			ps.setString(9, localQuery.getLqsex());
			ps.setLong(10, localQuery.getLqdate());
			ps.setInt(11, localQuery.getLfans());
			ps.setInt(12, localQuery.getLfol());
			
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
	public LocalQueryTaskBean getLocalQueryTaskInfoByID(int localQueryTaskID) {
		
		conn = SQLConn.getInstance().getConnection();
		
		LocalQueryTaskBean query = new LocalQueryTaskBean();
		
		try {
			ps = conn.prepareStatement("select * from wb_local_querytask where lqtid=" + localQueryTaskID);
	
			rs = ps.executeQuery();
			
			if(rs == null){
				
				return null;
				
			}
			
			if(rs.next()){
				
				query.setLqnck(rs.getString("lqnck"));
				query.setLqtag(rs.getString("lqtag"));
				query.setLqprov(rs.getString("lqprov"));
				query.setLqcity(rs.getString("lqcity"));
				query.setLqage(rs.getString("lqage"));
				query.setLqsex(rs.getString("lqsex"));
				query.setLqcom(rs.getString("lqcom"));
				query.setLqsch(rs.getString("lqsch"));
				query.setLqdate(rs.getLong("lqdate"));
				query.setLfans(rs.getInt("lfans"));
				query.setLfol(rs.getInt("lfol"));
				
			}
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		}finally{
			this.closeConnection();
		}
		
		
		return query;
	}
	
	//获取所有的本地搜索任务
	public List<Integer> getLocalAllQueryTask() {
		
		conn = SQLConn.getInstance().getConnection();
		
		List<Integer> localQueryTaskID = new ArrayList<Integer>();
		
		try {
			ps = conn.prepareStatement("select lqtid from wb_local_querytask");
	
			rs = ps.executeQuery();
			
			if(rs == null){
				
				return null;
				
			}
			
			while(rs.next()){
				
				localQueryTaskID.add(rs.getInt("lqtid"));
				
			}
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		}finally{
			this.closeConnection();
		}
		
		
		return localQueryTaskID;
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
