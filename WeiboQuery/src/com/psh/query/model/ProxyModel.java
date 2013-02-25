package com.psh.query.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.psh.base.util.PshLogger;
import com.psh.base.util.SQLConn;
import com.psh.query.bean.ProxyBean;

public class ProxyModel {
	
	private Connection conn = null;
	private PreparedStatement ps = null;
	private ResultSet rs = null;
	
	//获取所有代理对象
	public List<ProxyBean> getAllProxy(){
		
		conn = SQLConn.getInstance().getConnection();
		
		List<ProxyBean> proxyList = new ArrayList<ProxyBean>();
		
		try {
			ps = conn.prepareStatement("select * from wb_proxy");
			
			rs = ps.executeQuery();
			
			if(rs == null){
				
				PshLogger.logger.error("have no proxy in database");
				return null;
				
			}
			
			while(rs.next()){
				
				ProxyBean pb = new ProxyBean();
				pb.setIp(rs.getString("ip"));
				pb.setPort(rs.getInt("port"));
				proxyList.add(pb);
			}
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		}finally{
			this.closeConnection();
		}
		
		
		return proxyList;
		
	}
	
	//获取代理总数量
	public int getCount(){
		
		conn = SQLConn.getInstance().getConnection();
		int count = -1;
		try {
			ps = conn.prepareStatement("select count(*) from wb_proxy");
			
			rs = ps.executeQuery();
			
			if(rs == null){
				
				PshLogger.logger.error("have no proxy in database");
				return -1;
				
			}
			
			while(rs.next()){
				
				count = rs.getInt(1);
				
			}
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		}finally{
			this.closeConnection();
		}
		
		
		return count;
		
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
