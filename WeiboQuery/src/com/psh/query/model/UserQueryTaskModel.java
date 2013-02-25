package com.psh.query.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.psh.base.util.PshLogger;
import com.psh.base.util.SQLConn;
import com.psh.query.bean.QueryTaskBean;
import com.psh.query.bean.UserBean;
import com.psh.query.bean.UserQueryTaskBean;

public class UserQueryTaskModel {
	
	private Connection conn = null;
	private PreparedStatement ps = null;
	private ResultSet rs = null;
	
	//增加记录
	public synchronized boolean addUserQueryTask(UserQueryTaskBean uqt){
		System.out.println("添加中间表");
		conn = SQLConn.getInstance().getConnection();
		boolean isSuccess = false;
		
		try {
			ps = conn.prepareStatement("insert into wb_user_querytask(uid,qtid) values(?,?)");
			ps.setString(1, uqt.getUid());
			ps.setInt(2, uqt.getQtid());
			
			int result = ps.executeUpdate();
			
			if(result > 0){
				System.out.println("添加中间表成功");
				isSuccess = true;
				
			}
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		}finally{
			this.closeConnection();
		}
		
		return isSuccess;
		
	}
	
	//获取指定搜索任务数据源数量
	public int getCountByQueryID(int taskID){
		
		conn = SQLConn.getInstance().getConnection();
		int count = 0;
		
		try {
			ps = conn.prepareStatement("select count(*) from wb_user_querytask where qtid=" + taskID);
		
			rs = ps.executeQuery();
			
			if(rs == null){
				PshLogger.logger.error("find task error");
				return count;
			}
			
			if(rs.next()){
				
				count = rs.getInt(1);
				
			}
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		}finally{
			this.closeConnection();
		}
		
		return count;
		
		
	}
	
	//检查一个uid是否在一个指定任务中
	public synchronized boolean checkUidIsExsitByTaskID(String uid,int taskID){
		
		conn = SQLConn.getInstance().getConnection();
		boolean isSuccess = false;
		
		try {
			ps = conn.prepareStatement("select count(*) from wb_user_querytask where uid='" + uid + "' and qtid=" + taskID);
			
			rs = ps.executeQuery();
			if(rs == null){
				
				return isSuccess;
				
			}
			if(rs.next()){
				
				if(rs.getInt(1) > 0 ){
					isSuccess = true;
					
				}
				
			}else{
				return isSuccess;
			}
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		}finally{
			
			this.closeConnection();
		}
		
		return isSuccess;
		
		
		
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
