package com.psh.query.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.psh.base.util.PshLogger;
import com.psh.base.util.SQLConn;

public class ProvModel {
	
	private Connection conn = null;
	private PreparedStatement ps = null;
	private ResultSet rs = null;
	
	//根据ID获得省名
	public synchronized String getProvNameByID(int provID){
		
		conn = SQLConn.getInstance().getConnection();
		
		String provName = "";
		
		try {
			ps  = conn.prepareStatement("select * from wb_prov where pid=" + provID);
			
			rs = ps.executeQuery();
			
			if(rs == null){
				
				PshLogger.logger.error("database error");
				return null;
				
			}
			
			if(rs.next()){
				
				provName = rs.getString("prov");
				
			}
		
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		}finally{
			this.closeConnection();
		}
		
		return provName;
		
		
	}
	
	//获得所有省
	public List<String> getAllProv(){
		
		List<String> provNameList = new ArrayList<String>();
		
		conn = SQLConn.getInstance().getConnection();
		
		try {
			ps  = conn.prepareStatement("select * from wb_prov");
			
			rs = ps.executeQuery();
			
			if(rs == null){
				
				PshLogger.logger.error("database error");
				return null;
				
			}
			
			while(rs.next()){
				
				if(rs.getInt("pid") != 0){
					
					provNameList.add(rs.getString("prov"));
					
				}
				
			}
		
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		}finally{
			this.closeConnection();
		}
		
		return provNameList;
		
		
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
