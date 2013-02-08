package com.psh.query.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.psh.base.util.PshLogger;
import com.psh.base.util.SQLConn;
import com.psh.query.bean.AccountBean;
import com.psh.query.bean.AccountBean;

public class AccountModel extends SuperModel {

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
				bean.setEmation(rs.getString("emation"));
				bean.setFans(rs.getInt("fans"));
				bean.setGid(rs.getInt("gid"));
				bean.setInfo(rs.getString("info"));
				bean.setNickname(rs.getString("nickname"));
				bean.setPassword(rs.getString("password"));
				bean.setProv(rs.getString("prov"));
				bean.setSchool(rs.getString("school"));
				bean.setSex(rs.getString("sex"));
				bean.setTags(rs.getString("tags"));
				bean.setUid(rs.getInt("uid"));
				bean.setWeibo(rs.getInt("weibo"));
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
			pstmt = conn.prepareStatement("select count(*) from wb_account where gid is null");
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
