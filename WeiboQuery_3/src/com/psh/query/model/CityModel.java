package com.psh.query.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.psh.base.util.PshLogger;
import com.psh.base.util.SQLConn;

public class CityModel {
	
	
	private Connection conn = null;
	private PreparedStatement ps = null;
	private ResultSet rs = null;
	
	//根据ID获得城市名
	public synchronized String getProvNameByID(int cityID,int pid){
		
		conn = SQLConn.getInstance().getConnection();
		
		String cityName = "";
		
		try {
			ps  = conn.prepareStatement("select * from wb_city where pid=" + pid + " and cityid=" + cityID);
			
			rs = ps.executeQuery();
			
			if(rs == null){
				
				PshLogger.logger.error("database error");
				return null;
				
			}
			
			if(rs.next()){
				
				cityName = rs.getString("city");
				
			}
		
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		}finally{
			this.closeConnection();
		}
		
		return cityName;
		
		
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
