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


public class wb_proxyDAO {

	/**
	 * 返回所有wb_proxyModel对象,符合checktime条件的
	 * 
	 * @return
	 */
	public List<ProxyBean> selectByAvailable(){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<ProxyBean> data = null;
		
		
		try {
			conn = SQLConn.getInstance().getConnection();
			pstmt = conn.prepareStatement("select * from wb_proxy where checktime != 10001 order by rand()");
			
			rs = pstmt.executeQuery();
			data = new ArrayList<ProxyBean>();
			
			
			
			while(rs.next()){
				ProxyBean model = new ProxyBean();
				
				model.setChecktime(rs.getLong("checktime"));
				model.setIp(rs.getString("ip"));
				model.setPort(rs.getInt("port"));
				model.setProxyid(rs.getInt("proxyid"));
				
				
				data.add(model);
			}
			
			
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage(), e);
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					PshLogger.logger.error(e.getMessage(), e);
				}
			}
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					PshLogger.logger.error(e.getMessage(), e);
				}
			}
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					PshLogger.logger.error(e.getMessage(), e);
				}
			}
			
			
		}
		
		if(data != null){
			PshLogger.logger.debug("【Success】Select Available wb_proxy Size -> "+data.size());
		}else{
			PshLogger.logger.debug("【Faild】Select Available wb_proxy");
		}
		
		
		return data;
	}
	
	
	/**
	 * 返回所有wb_proxyModel对象
	 * @return
	 */
	public List<ProxyBean> selectALL(){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<ProxyBean> data = null;
		
		
		try {
			conn = SQLConn.getInstance().getConnection();
			pstmt = conn.prepareStatement("select * from wb_proxy order by rand()");
			rs = pstmt.executeQuery();
			data = new ArrayList<ProxyBean>();
			
			
			
			while(rs.next()){
				ProxyBean model = new ProxyBean();
				
				model.setChecktime(rs.getLong("checktime"));
				model.setIp(rs.getString("ip"));
				model.setPort(rs.getInt("port"));
				model.setProxyid(rs.getInt("proxyid"));
				
				
				data.add(model);
			}
			
			
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage(), e);
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					PshLogger.logger.error(e.getMessage(), e);
				}
			}
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					PshLogger.logger.error(e.getMessage(), e);
				}
			}
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					PshLogger.logger.error(e.getMessage(), e);
				}
			}
			
			
		}
		
		if(data != null){
			PshLogger.logger.debug("【Success】Select all wb_proxy Size -> "+data.size());
		}else{
			PshLogger.logger.debug("【Faild】Select all wb_proxy");
		}
		
		
		return data;
	}
	
	
	/**
	 * 插入一个wb_proxyModel对象
	 * @param model
	 * @return
	 */
	public synchronized boolean insert(ProxyBean model){
		Connection conn = null;
		PreparedStatement pstmt = null;
		int resultCount = 0;
		
		if(model == null){
			System.out.println("insert wb_proxyModel is null!");
			return false;
		}
		
		try {
			conn = SQLConn.getInstance().getConnection();
			pstmt = conn.prepareStatement("insert into wb_proxy(ip, port, checktime) values(?,?,?)");
			
			pstmt.setString(1, model.getIp());
			pstmt.setInt(2, model.getPort());
			pstmt.setLong(3, model.getChecktime());
			
			resultCount = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage(), e);
		} finally {
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					PshLogger.logger.error(e.getMessage(), e);
				}
			}
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					PshLogger.logger.error(e.getMessage(), e);
				}
			}
			
			
		}
		
		if(resultCount > 0){
			System.out.println("【Success】 insert "+model.toString());
		}else{
			System.out.println("【Faild】insert "+model.toString());
		}
		
		
		return resultCount > 0;
	}
	
	/**
	 * 更新传入的wb_proxyModel对象
	 * @param model
	 * @return
	 */
	public synchronized boolean update(ProxyBean model){
		Connection conn = null;
		PreparedStatement pstmt = null;
		int resultCount = 0;
		
		if(model == null){
			PshLogger.logger.error("insert wb_proxyModel is null!");
			return false;
		}
		if(model.getProxyid() == 0){
			PshLogger.logger.error("insert wb_proxyModel Model Proxyid is 0!");
			return false;
		}
		
		
		
		try {
			conn = SQLConn.getInstance().getConnection();
			pstmt = conn.prepareStatement("update wb_proxy set ip = ? , port = ? , checktime = ? where proxyid = ?");
			
			pstmt.setString(1, model.getIp());
			pstmt.setInt(2, model.getPort());
			pstmt.setLong(3, model.getChecktime());
			pstmt.setInt(4, model.getProxyid());
			
			
			resultCount = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage(), e);
		} finally {
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					PshLogger.logger.error(e.getMessage(), e);
				}
			}
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					PshLogger.logger.error(e.getMessage(), e);
				}
			}
			
			
		}
		
		if(resultCount > 0){
			PshLogger.logger.debug("【Success】update "+model.toString());
		}else{
			PshLogger.logger.debug("【Faild】update "+model.toString());
		}
		
		return resultCount > 0;
	}
	
	
}
