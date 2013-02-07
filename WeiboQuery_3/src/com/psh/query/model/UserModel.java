package com.psh.query.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.psh.base.util.PshLogger;
import com.psh.base.util.SQLConn;
import com.psh.query.bean.UserBean;

public class UserModel {
	
	private Connection conn = null;
	private PreparedStatement ps = null;
	private ResultSet rs = null;
	
	//增加用户
	public synchronized boolean addUser(UserBean user){
		conn = SQLConn.getInstance().getConnection();
		boolean isSuccess = false;
		
		try {

			ps = conn.prepareStatement("insert into wb_user values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			ps.setString(1, user.getUid());
			ps.setString(2, user.getUck());
			if(user.getProv() != null){
				
				ps.setString(3, user.getProv());
				if(user.getCity() != null){
					
					ps.setString(4, user.getCity());
				}else{
					ps.setString(4,"");
				}
			}else{
				ps.setString(3, "");
				ps.setString(4, "");
			}
			ps.setString(5, user.getSex());
			if(user.getEmo() != null){
				ps.setString(6, user.getEmo());
				
			}else{
				ps.setString(6, "");
			}
			if(user.getDate() != null){
				ps.setString(7, user.getDate());
				
			}else{
				ps.setString(7,"");
			}
			if(user.getBlo() != null){
				
				ps.setString(8, user.getBlo());
			}else{
				ps.setString(8, "");
			}
			if(user.getTag() != null){
				ps.setString(9, user.getTag());
				
			}else{
				ps.setString(9, "");
			}
			ps.setString(10, user.getFans());
			ps.setString(11, user.getFol());
			if(user.getInfo() != null){
				
				ps.setString(12, user.getInfo());
			}else{
				ps.setString(12,"");
			}
			
			if(user.getCom() != null){
				
				ps.setString(13, user.getCom());
			}else{
				ps.setString(13,"");
			}
			
			if(user.getStu() != null){
				
				ps.setString(14, user.getStu());
			}else{
				ps.setString(14,"");
			}
			
			int result = ps.executeUpdate();
			
			if(result > 0){
				
				isSuccess = true;
				
			}
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		}finally{
			this.closeConnection();
		}
		
		return isSuccess;
		
	}
	
	//查找用户是否存在
	public synchronized boolean checkUserIsExsit(String uid){
		System.out.println("判断用户是否存在");
		conn = SQLConn.getInstance().getConnection();
		boolean isSuccess = false;
		
		try {
			ps = conn.prepareStatement("select count(*) from wb_user where uid='" + uid + "'");
			
			rs = ps.executeQuery();
			if(rs == null){
				
				return isSuccess;
				
			}
			if(rs.next()){
				
				if(rs.getInt(1) > 0 ){
					System.out.println("用户存在");
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
	
	//修改用户
	public synchronized boolean updateUser(UserBean user){
		
		conn = SQLConn.getInstance().getConnection();
		boolean isSuccess = false;
		
		try {
			
			ps = conn.prepareStatement("update wb_user set nck=?,prov=?,city=?,sex=?,emo=?,date=?,blo=?,tag=?,fans=?,fol=?,info=?,com=?,stu=? where uid=?");
			
			ps.setString(1, user.getUck());
			if(user.getProv() != null){
				
				ps.setString(2, user.getProv());
				if(user.getCity() != null){
					
					ps.setString(3, user.getCity());
				}else{
					ps.setString(3,"");
				}
			}else{
				ps.setString(2, "");
				ps.setString(3, "");
			}
			ps.setString(4, user.getSex());
			if(user.getEmo() != null){
				ps.setString(5, user.getEmo());
				
			}else{
				ps.setString(5, "");
			}
			if(user.getDate() != null){
				ps.setString(6, user.getDate());
				
			}else{
				ps.setString(6,"");
			}
			if(user.getBlo() != null){
				
				ps.setString(7, user.getBlo());
			}else{
				ps.setString(7, "");
			}
			if(user.getTag() != null){
				ps.setString(8, user.getTag());
				
			}else{
				ps.setString(8, "");
			}
			ps.setString(9, user.getFans());
			ps.setString(10, user.getFol());
			if(user.getInfo() != null){
				
				ps.setString(11, user.getInfo());
			}else{
				ps.setString(11,"");
			}
			
			if(user.getCom() != null){
				
				ps.setString(12, user.getCom());
			}else{
				ps.setString(12,"");
			}
			
			if(user.getStu() != null){
				
				ps.setString(13, user.getStu());
			}else{
				ps.setString(13,"");
			}
			
			ps.setString(14, user.getUid());
			int result = ps.executeUpdate();
			
			if(result > 0){
				
				isSuccess = true;
				
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
