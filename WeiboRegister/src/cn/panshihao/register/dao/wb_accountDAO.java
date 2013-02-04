package cn.panshihao.register.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.panshihao.desktop.commons.Log;
import cn.panshihao.desktop.commons.SQLConn;
import cn.panshihao.desktop.commons.Tools;
import cn.panshihao.register.model.wb_accountModel;


public class wb_accountDAO {

	/**
	 * 返回所有wb_accountModel对象的email
	 * @return
	 */
	public Map<String, String> selectEmail(){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Map<String, String> data = null;
		
		
		try {
			conn = Tools.getMysqlConn();
			pstmt = conn.prepareStatement("select email,nickname from wb_account");
			rs = pstmt.executeQuery();
			data = new HashMap<String, String>();
			
			
			
			while(rs.next()){
				
				data.put(rs.getString("email"), rs.getString("nickname"));
			}
			
			
			
		} catch (SQLException e) {
			Log.log.error(e.getMessage(), e);
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					Log.log.error(e.getMessage(), e);
				}
			}
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.log.error(e.getMessage(), e);
				}
			}
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					Log.log.error(e.getMessage(), e);
				}
			}
			
			
		}
		
		if(data != null){
			Log.log.debug("【Success】Select email wb_account Size -> "+data.size());
		}else{
			Log.log.debug("【Faild】Select email wb_account");
		}
		
		
		return data;
	}
	/**
	 * 返回所有wb_accountModel对象
	 * @return
	 */
	public synchronized List<wb_accountModel> selectALL(){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<wb_accountModel> data = null;
		
		
		try {
			conn = Tools.getMysqlConn();
			pstmt = conn.prepareStatement("select * from wb_account");
			rs = pstmt.executeQuery();
			data = new ArrayList<wb_accountModel>();
			
			
			
			while(rs.next()){
				wb_accountModel model = new wb_accountModel();
				
				model.setAid(rs.getInt("aid"));
				model.setDomain(rs.getString("domain"));
				model.setEmail(rs.getString("email"));
				model.setNickname(rs.getString("nickname"));
				model.setPassword(rs.getString("password"));
				model.setStatus(rs.getInt("status"));
				model.setUid(rs.getInt("uid"));
				
				data.add(model);
			}
			
			
			
		} catch (SQLException e) {
			Log.log.error(e.getMessage(), e);
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					Log.log.error(e.getMessage(), e);
				}
			}
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.log.error(e.getMessage(), e);
				}
			}
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					Log.log.error(e.getMessage(), e);
				}
			}
			
			
		}
		
		if(data != null){
			Log.log.debug("【Success】Select all wb_account Size -> "+data.size());
		}else{
			Log.log.debug("【Faild】Select all wb_account");
		}
		
		
		return data;
	}
	/**
	 * 根据状态返回wb_accountModel对象
	 * @param status
	 * @return
	 */
	public List<wb_accountModel> selectByStatus(int status){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<wb_accountModel> data = null;
		
		
		try {
			conn = Tools.getMysqlConn();
			pstmt = conn.prepareStatement("select * from wb_account where status = ?");
			
			pstmt.setInt(1, status);
			
			
			rs = pstmt.executeQuery();
			data = new ArrayList<wb_accountModel>();
			
			
			
			while(rs.next()){
				wb_accountModel model = new wb_accountModel();
				
				model.setAid(rs.getInt("aid"));
				model.setDomain(rs.getString("domain"));
				model.setEmail(rs.getString("email"));
				model.setNickname(rs.getString("nickname"));
				model.setPassword(rs.getString("password"));
				model.setStatus(rs.getInt("status"));
				model.setUid(rs.getInt("uid"));
				
				data.add(model);
			}
			
			
			
		} catch (SQLException e) {
			Log.log.error(e.getMessage(), e);
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					Log.log.error(e.getMessage(), e);
				}
			}
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.log.error(e.getMessage(), e);
				}
			}
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					Log.log.error(e.getMessage(), e);
				}
			}
			
			
		}
		
		if(data != null){
			Log.log.debug("【Success】Select status is "+status+" wb_account Size -> "+data.size());
		}else{
			Log.log.debug("【Faild】Select status is "+status+" wb_account ");
		}
		
		return data;
	}
	/**
	 * 插入一个wb_AccountModel对象
	 * @param model
	 * @return
	 */
	public synchronized boolean insert(wb_accountModel model){
		Connection conn = null;
		PreparedStatement pstmt = null;
		int resultCount = 0;
		
		if(model == null){
			Log.log.error("insert wb_accountModel is null!");
			return false;
		}
		
		try {
			conn = Tools.getMysqlConn();
			pstmt = conn.prepareStatement("insert into wb_account(uid,email,password,nickname,domain,status) values(?,?,?,?,?,?)");
			
			pstmt.setInt(1, model.getUid());
			pstmt.setString(2, model.getEmail());
			pstmt.setString(3, model.getPassword());
			pstmt.setString(4, model.getNickname());
			pstmt.setString(5, model.getDomain());
			pstmt.setInt(6, model.getStatus());
			
			resultCount = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			Log.log.error(e.getMessage(), e);
		} finally {
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.log.error(e.getMessage(), e);
				}
			}
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					Log.log.error(e.getMessage(), e);
				}
			}
			
			
		}
		
		if(resultCount > 0){
			Log.log.debug("【Success】 insert "+model.toString());
		}else{
			Log.log.debug("【Faild】insert "+model.toString());
		}
		
		
		return resultCount > 0;
	}
	/**
	 * 删除指定主键的wb_AccountModel对象
	 * @param aid
	 * @return
	 */
	public synchronized boolean delete(int aid){
		Connection conn = null;
		PreparedStatement pstmt = null;
		int resultCount = 0;
		
		try {
			conn = Tools.getMysqlConn();
			pstmt = conn.prepareStatement("delete from wb_account where aid = ?");
			
			pstmt.setInt(1, aid);
			
			resultCount = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			Log.log.error(e.getMessage(), e);
		} finally {
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.log.error(e.getMessage(), e);
				}
			}
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					Log.log.error(e.getMessage(), e);
				}
			}
			
			
		}
		
		if(resultCount > 0){
			Log.log.debug("【Success】 delete aid is "+aid+" wb_account");
		}else{
			Log.log.debug("【Faild】 delete aid is "+aid+" wb_account");
		}
		
		return resultCount > 0;
	}
	/**
	 * 更新传入的wb_accountModel对象
	 * @param model
	 * @return
	 */
	public synchronized boolean update(wb_accountModel model){
		Connection conn = null;
		PreparedStatement pstmt = null;
		int resultCount = 0;
		
		if(model == null){
			Log.log.error("insert wb_accountModel is null!");
			return false;
		}
		if(model.getAid() == 0){
			Log.log.error("insert wb_accountModel Model aid is 0!");
			return false;
		}
		
		
		
		try {
			conn = Tools.getMysqlConn();
			pstmt = conn.prepareStatement("update wb_account set uid = ? , email = ? , password = ? , nickname = ? , domain = ? , status = ? where aid = ?");
			
			pstmt.setInt(1, model.getUid());
			pstmt.setString(2, model.getEmail());
			pstmt.setString(3, model.getPassword());
			pstmt.setString(4, model.getNickname());
			pstmt.setString(5, model.getDomain());
			pstmt.setInt(6, model.getStatus());
			pstmt.setInt(7, model.getAid());
			
			resultCount = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			Log.log.error(e.getMessage(), e);
		} finally {
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					Log.log.error(e.getMessage(), e);
				}
			}
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					Log.log.error(e.getMessage(), e);
				}
			}
			
			
		}
		
		if(resultCount > 0){
			Log.log.debug("【Success】update "+model.toString());
		}else{
			Log.log.debug("【Faild】update "+model.toString());
		}
		
		return resultCount > 0;
	}
	
	
	public static void main(String[] args) {
		wb_accountDAO dao = new wb_accountDAO();
		
		wb_accountModel test = new wb_accountModel();
		test.setDomain("wo domain");
		test.setEmail("wo email");
		test.setNickname("wo nickname");
		test.setPassword("wo password");
		test.setStatus(1);
		test.setUid(10);
		
		System.out.println(dao.insert(test));
		
		
	}
}
