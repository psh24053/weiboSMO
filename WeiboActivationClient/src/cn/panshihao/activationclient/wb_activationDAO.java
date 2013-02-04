package cn.panshihao.activationclient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class wb_activationDAO {

	
	/**
	 * 获取Status为1或者4的activation
	 * @return
	 */
	public List<wb_activationModel> selectActivationFor1_4(){
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<wb_activationModel> data = null;
		
		
		try {
			conn = Tools.getMysqlConn();
			pstmt = conn.prepareStatement("select * from wb_activation where status = 1 or status = 4");
			
			rs = pstmt.executeQuery();
			data = new ArrayList<wb_activationModel>();
			
			
			
			while(rs.next()){
				wb_activationModel model = new wb_activationModel();
				model.setAid(rs.getInt("aid"));
				model.setEmail(rs.getString("email"));
				model.setUrl(rs.getString("url"));
				model.setStatus(rs.getInt("status"));
				
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
	 * 获取所有activation
	 * @return
	 */
	public List<wb_activationModel> selectActivation(){
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<wb_activationModel> data = null;
		
		
		try {
			conn = Tools.getMysqlConn();
			pstmt = conn.prepareStatement("select * from wb_activation where status = 0 ");
			
			rs = pstmt.executeQuery();
			data = new ArrayList<wb_activationModel>();
			
			
			
			while(rs.next()){
				wb_activationModel model = new wb_activationModel();
				model.setAid(rs.getInt("aid"));
				model.setEmail(rs.getString("email"));
				model.setUrl(rs.getString("url"));
				model.setStatus(rs.getInt("status"));
				
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
	 * 更新传入的wb_proxyModel对象
	 * @param model
	 * @return
	 */
	public synchronized boolean update(wb_activationModel model){
		Connection conn = null;
		PreparedStatement pstmt = null;
		int resultCount = 0;
		
		if(model == null){
			Tools.log.error("insert wb_activationModel is null!");
			return false;
		}
		if(model.getAid() == 0){
			Tools.log.error("insert wb_activationModel Model Proxyid is 0!");
			return false;
		}
		
		
		
		try {
			conn = Tools.getMysqlConn();
			if(conn == null){
				return update(model);
			}
			pstmt = conn.prepareStatement("update wb_activation set status = ? where aid = ?");
			
			pstmt.setInt(1, model.getStatus());
			pstmt.setInt(2, model.getAid());
			
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
