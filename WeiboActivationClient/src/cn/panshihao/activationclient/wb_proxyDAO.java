package cn.panshihao.activationclient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class wb_proxyDAO {

	/**
	 * 返回所有wb_proxyModel对象,符合checktime条件的
	 * 
	 * @return
	 */
	public List<wb_proxyModel> selectByAvailable(){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<wb_proxyModel> data = null;
		
		
		try {
			conn = Tools.getMysqlConn();
			pstmt = conn.prepareStatement("select * from wb_proxy_cn where checktime != 10001 order by rand()");
			
			rs = pstmt.executeQuery();
			data = new ArrayList<wb_proxyModel>();
			
			
			
			while(rs.next()){
				wb_proxyModel model = new wb_proxyModel();
				
				model.setChecktime(rs.getLong("checktime"));
				model.setIp(rs.getString("ip"));
				model.setPort(rs.getInt("port"));
				model.setProxyid(rs.getInt("proxyid"));
				
				
				data.add(model);
			}
			
			
			
		} catch (SQLException e) {
			Tools.log.error(e.getMessage(), e);
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					Tools.log.error(e.getMessage(), e);
				}
			}
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					Tools.log.error(e.getMessage(), e);
				}
			}
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					Tools.log.error(e.getMessage(), e);
				}
			}
			
			
		}
		
		if(data != null){
			Tools.log.debug("【Success】Select Available wb_proxy Size -> "+data.size());
		}else{
			Tools.log.debug("【Faild】Select Available wb_proxy");
		}
		
		
		return data;
	}
	
	
	/**
	 * 返回所有wb_proxyModel对象
	 * @return
	 */
	public List<wb_proxyModel> selectALL(){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<wb_proxyModel> data = null;
		
		
		try {
			conn = Tools.getMysqlConn();
			pstmt = conn.prepareStatement("select * from wb_proxy_cn order by rand()");
			rs = pstmt.executeQuery();
			data = new ArrayList<wb_proxyModel>();
			
			
			
			while(rs.next()){
				wb_proxyModel model = new wb_proxyModel();
				
				model.setChecktime(rs.getLong("checktime"));
				model.setIp(rs.getString("ip"));
				model.setPort(rs.getInt("port"));
				model.setProxyid(rs.getInt("proxyid"));
				
				
				data.add(model);
			}
			
			
			
		} catch (SQLException e) {
			Tools.log.error(e.getMessage(), e);
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					Tools.log.error(e.getMessage(), e);
				}
			}
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					Tools.log.error(e.getMessage(), e);
				}
			}
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					Tools.log.error(e.getMessage(), e);
				}
			}
			
			
		}
		
		if(data != null){
			Tools.log.debug("【Success】Select all wb_proxy Size -> "+data.size());
		}else{
			Tools.log.debug("【Faild】Select all wb_proxy");
		}
		
		
		return data;
	}
	
	
	/**
	 * 插入一个wb_proxyModel对象
	 * @param model
	 * @return
	 */
	public synchronized boolean insert(wb_proxyModel model){
		Connection conn = null;
		PreparedStatement pstmt = null;
		int resultCount = 0;
		
		if(model == null){
			System.out.println("insert wb_proxyModel is null!");
			return false;
		}
		
		try {
			conn = Tools.getMysqlConn();
			pstmt = conn.prepareStatement("insert into wb_proxy_cn(ip, port, checktime) values(?,?,?)");
			
			pstmt.setString(1, model.getIp());
			pstmt.setInt(2, model.getPort());
			pstmt.setLong(3, model.getChecktime());
			
			resultCount = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			Tools.log.error(e.getMessage(), e);
		} finally {
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					Tools.log.error(e.getMessage(), e);
				}
			}
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					Tools.log.error(e.getMessage(), e);
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
	public synchronized boolean update(wb_proxyModel model){
		Connection conn = null;
		PreparedStatement pstmt = null;
		int resultCount = 0;
		
		if(model == null){
			Tools.log.error("insert wb_proxyModel is null!");
			return false;
		}
		if(model.getProxyid() == 0){
			Tools.log.error("insert wb_proxyModel Model Proxyid is 0!");
			return false;
		}
		
		
		
		try {
			conn = Tools.getMysqlConn();
			pstmt = conn.prepareStatement("update wb_proxy_cn set ip = ? , port = ? , checktime = ? where proxyid = ?");
			
			pstmt.setString(1, model.getIp());
			pstmt.setInt(2, model.getPort());
			pstmt.setLong(3, model.getChecktime());
			pstmt.setInt(4, model.getProxyid());
			
			
			resultCount = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			Tools.log.error(e.getMessage(), e);
		} finally {
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					Tools.log.error(e.getMessage(), e);
				}
			}
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					Tools.log.error(e.getMessage(), e);
				}
			}
			
			
		}
		
		if(resultCount > 0){
			Tools.log.debug("【Success】update "+model.toString());
		}else{
			Tools.log.debug("【Faild】update "+model.toString());
		}
		
		return resultCount > 0;
	}
	
	
}
