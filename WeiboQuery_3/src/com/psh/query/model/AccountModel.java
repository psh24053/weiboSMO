package com.psh.query.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.psh.base.json.JSONException;
import com.psh.base.json.JSONObject;
import com.psh.base.util.PshLogger;
import com.psh.base.util.SQLConn;
import com.psh.query.bean.AccountBean;
import com.psh.query.bean.AccountBean;

public class AccountModel extends SuperModel {

	public boolean updateRegAccountStatus(int aid, int status){
		
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = -1;
		
		
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return false;
		}
		
		try {
			pstmt = conn.prepareStatement("update wb_reg_account set status = ? where aid = ?");
			pstmt.setInt(1, status);
			pstmt.setInt(2, aid);
			result = pstmt.executeUpdate();
			
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		} finally {
			closeSQL(rs);
			closeSQL(pstmt);
			closeSQL(conn);
		}
		
		
		return result != -1;
	}
	
	/**
	 * 获取所有reg_account
	 * @return
	 */
	public List<AccountBean> getRegAccountAll(){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<AccountBean> data = new ArrayList<AccountBean>();
		int result = -1;
		
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return null;
		}
		
		try {
			pstmt = conn.prepareStatement("select * from wb_reg_account");
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				AccountBean bean = new AccountBean();
				bean.putValue("aid", rs.getInt("aid"));
				bean.setEmail(rs.getString("email"));
				bean.setPassword(rs.getString("password"));
				data.add(bean);
			
			}
			
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		} finally {
			closeSQL(rs);
			closeSQL(pstmt);
			closeSQL(conn);
		}
		
		
		return data;
	}
	
	
	public int getRegAccount(){
		
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = -1;
		
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return -1;
		}
		
		try {
			pstmt = conn.prepareStatement("select count(*) as c from wb_reg_account");
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				return rs.getInt("c");
			}
			
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		} finally {
			closeSQL(rs);
			closeSQL(pstmt);
			closeSQL(conn);
		}
		
		
		return -1;
	}
	
	/**
	 * 根据status获取用户数量
	 * @param status
	 * @return
	 */
	public int getUserCountActivation(int status){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = -1;
		
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return -1;
		}
		
		try {
			pstmt = conn.prepareStatement("select count(*) as c from wb_activation where status = ?");
			pstmt.setInt(1, status);
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				return rs.getInt("c");
			}
			
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		} finally {
			closeSQL(rs);
			closeSQL(pstmt);
			closeSQL(conn);
		}
		
		
		return -1;
	}
	/**
	 * 更新account对象
	 * @param ttid
	 * @return
	 */
	public boolean UpdateAccount(AccountBean bean){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = -1;
		
		
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return false;
		}
		if(bean == null || bean.getUid() == 0){
			PshLogger.logger.error("AccountBean uid is 0");
			return false;
		}
		
		try {
			pstmt = conn.prepareStatement("update wb_account set nickname = ? , prov = ? , city = ? , sex = ? , emotion = ? , birthday = ? , blood = ? , info = ? , fans = ? , weibo = ? , att = ? , school = ? , company = ? , tags = ? , tags_map = ? where uid = ?");
			pstmt.setString(1, bean.getNickname());
			pstmt.setString(2, bean.getProv());
			pstmt.setString(3, bean.getCity());
			pstmt.setString(4, bean.getSex());
			pstmt.setString(5, bean.getEmotion());
			pstmt.setString(6, bean.getBirthday());
			if(bean.getBlood() != null && bean.getBlood().length() > 3){
				pstmt.setString(7, "");
			}else{
				pstmt.setString(7, bean.getBlood());
			}
			pstmt.setString(8, bean.getInfo());
			pstmt.setInt(9, bean.getFans());
			pstmt.setInt(10, bean.getWeibo());
			pstmt.setInt(11, bean.getAtt());
			pstmt.setString(12, bean.getSchool());
			pstmt.setString(13, bean.getCompany());
			pstmt.setString(14, bean.getTags());
			pstmt.setString(15, bean.getTagsMap().toString());
			pstmt.setLong(16, bean.getUid());
			result = pstmt.executeUpdate();
			
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		} finally {
			closeSQL(rs);
			closeSQL(pstmt);
			closeSQL(conn);
		}
		
		
		return result != -1;
	}
	/**
	 * 插入account对象
	 * @param ttid
	 * @return
	 */
	public boolean InsertAccountByGid(AccountBean bean, int gid){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = -1;
		
		
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return false;
		}
		if(bean == null || bean.getUid() == 0){
			PshLogger.logger.error("AccountBean uid is 0");
			return false;
		}
		
		try {
			pstmt = conn.prepareStatement("insert into wb_account(uid,email,password,gid) values(?,?,?,?)");
			pstmt.setLong(1, bean.getUid());
			pstmt.setString(2, bean.getEmail());
			pstmt.setString(3, bean.getPassword());
			pstmt.setInt(4, gid);
			
			result = pstmt.executeUpdate();
			
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		} finally {
			closeSQL(rs);
			closeSQL(pstmt);
			closeSQL(conn);
		}
		
		
		return result != -1;
	}
	/**
	 * 根据uid，获取用户对象
	 * @param ttid
	 * @return
	 */
	public AccountBean getAccount(long uid){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = -1;
		
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return null;
		}
		
		try {
			pstmt = conn.prepareStatement("select * from wb_account where uid = ?");
			pstmt.setLong(1, uid);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				AccountBean bean = new AccountBean();
				bean.setAtt(rs.getInt("att"));
				bean.setBirthday(rs.getString("birthday"));
				bean.setBlood(rs.getString("blood"));
				bean.setCity(rs.getString("city"));
				bean.setCompany(rs.getString("company"));
				bean.setDomain(rs.getString("domain"));
				bean.setEmail(rs.getString("email"));
				bean.setEmotion(rs.getString("emotion"));
				bean.setFans(rs.getInt("fans"));
				bean.setGid(rs.getInt("gid"));
				bean.setInfo(rs.getString("info"));
				bean.setNickname(rs.getString("nickname"));
				bean.setPassword(rs.getString("password"));
				bean.setProv(rs.getString("prov"));
				bean.setSchool(rs.getString("school"));
				bean.setSex(rs.getString("sex"));
				bean.setTags(rs.getString("tags"));
				bean.setUid(rs.getLong("uid"));
				bean.setWeibo(rs.getInt("weibo"));
				
				String tags_map = rs.getString("tags_map");
				if(tags_map != null){
					try {
						JSONObject json = new JSONObject(tags_map);
						Iterator<String> i = json.keys();
						
						while(i.hasNext()){
							String key = i.next();
							bean.getTagsMap().put(key, json.getString(key));
						}
						
						
					} catch (JSONException e) {
					}
					
				}
				
				return bean;
			}
			
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		} finally {
			closeSQL(rs);
			closeSQL(pstmt);
			closeSQL(conn);
		}
		
		
		return null;
	}
	/**
	 * 获取分组用户列表
	 * @param ttid
	 * @return
	 */
	public List<AccountBean> getGroupUserList(int gid){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		List<AccountBean> data = new ArrayList<AccountBean>();
		ResultSet rs = null;
		int result = -1;
		
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return data;
		}
		
		try {
			pstmt = conn.prepareStatement("select * from wb_account where gid = ?");
			pstmt.setInt(1, gid);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				AccountBean bean = new AccountBean();
				bean.setAtt(rs.getInt("att"));
				bean.setBirthday(rs.getString("birthday"));
				bean.setBlood(rs.getString("blood"));
				bean.setCity(rs.getString("city"));
				bean.setCompany(rs.getString("company"));
				bean.setDomain(rs.getString("domain"));
				bean.setEmail(rs.getString("email"));
				bean.setEmotion(rs.getString("emotion"));
				bean.setFans(rs.getInt("fans"));
				bean.setGid(rs.getInt("gid"));
				bean.setInfo(rs.getString("info"));
				bean.setNickname(rs.getString("nickname"));
				bean.setPassword(rs.getString("password"));
				bean.setProv(rs.getString("prov"));
				bean.setSchool(rs.getString("school"));
				bean.setSex(rs.getString("sex"));
				bean.setTags(rs.getString("tags"));
				bean.setUid(rs.getLong("uid"));
				bean.setWeibo(rs.getInt("weibo"));
				String tags_map = rs.getString("tags_map");
				if(tags_map != null){
					try {
						JSONObject json = new JSONObject(tags_map);
						Iterator<String> i = json.keys();
						
						while(i.hasNext()){
							String key = i.next();
							bean.getTagsMap().put(key, json.getString(key));
						}
						
						
					} catch (JSONException e) {
					}
					
				}
				data.add(bean);
			}
			
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		} finally {
			closeSQL(rs);
			closeSQL(pstmt);
			closeSQL(conn);
		}
		
		
		return data;
	}
	
	/**
	 * 同步账号
	 * @return
	 */
	public boolean AccountSynchronization(){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		int result = -1;
		
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return false;
		}
		
		try {
			pstmt = conn.prepareStatement("INSERT INTO wb_account(uid,email,PASSWORD,nickname,domain) SELECT uid,email,PASSWORD,nickname,domain FROM wb_reg_account WHERE wb_reg_account.uid != 0 AND wb_reg_account.STATUS = 11 AND NOT EXISTS(SELECT * FROM wb_account WHERE wb_account.uid = wb_reg_account.uid)");
			result = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		} finally {
			closeSQL(pstmt);
			closeSQL(conn);
		}
		
		
		return result != -1;
	}

	/**
	 * 获取数据库中已被分组的用户数量
	 * 
	 * @return
	 */
	public int getDBGroupUserCount(){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = -1;
		
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return -1;
		}
		
		try {
			pstmt = conn.prepareStatement("SELECT COUNT(*) as count FROM wb_account WHERE gid != 'null'");
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				return rs.getInt("count");
			}
			
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		} finally {
			closeSQL(rs);
			closeSQL(pstmt);
			closeSQL(conn);
		}
		
		
		return -1;
	}
	/**
	 * 获取数据库中可用的用户数量
	 * 
	 * @return
	 */
	public int getDBUserCount(){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = -1;
		
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return -1;
		}
		
		try {
			pstmt = conn.prepareStatement("select count(*) as count from wb_account where gid is null");
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				return rs.getInt("count");
			}
			
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		} finally {
			closeSQL(rs);
			closeSQL(pstmt);
			closeSQL(conn);
		}
		
		
		return -1;
	}
	
	
	
}
