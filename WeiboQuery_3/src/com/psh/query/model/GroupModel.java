package com.psh.query.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.psh.base.util.PshLogger;
import com.psh.base.util.SQLConn;
import com.psh.query.bean.GroupBean;
import com.psh.query.bean.GroupBean;

public class GroupModel extends SuperModel {

	/**
	 * 获取分组对象
	 * @return
	 */
	public GroupBean getGroup(int gid){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return null;
		}
		
		try {
			pstmt = conn.prepareStatement("select * from wb_group where gid = ?");
			
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				GroupBean bean = new GroupBean();
				bean.setCid(rs.getInt("cid"));
				bean.setGid(rs.getInt("gid"));
				bean.setName(rs.getString("name"));
				bean.setStatus(rs.getString("status"));
				
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
	 * 获取分组数量
	 * @return
	 */
	public int getGroupUserCount(int gid){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return 0;
		}
		
		try {
			pstmt = conn.prepareStatement("select count(*) as count from wb_account where gid = ?");
			pstmt.setInt(1, gid);
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
		
		
		return 0;
	}
	
	/**
	 * 增加分组
	 * @param group
	 * @return
	 */
	public boolean addGroup(GroupBean group){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		int result = -1;
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return false;
		}
		
		try {
			pstmt = conn.prepareStatement("insert into wb_group(cid,name,status) values(?,?,?)");
			
			pstmt.setInt(1, group.getCid());
			pstmt.setString(2, group.getName());
			pstmt.setString(3, "空闲");
			
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
	 * 更新分组
	 * @param bean
	 * @return
	 */
	public boolean updateGroup(GroupBean bean){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		int result = -1;
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return false;
		}
		if(bean.getGid() == 0){
			PshLogger.logger.error("Bean primkey is null");
			return false;
		}
		
		
		try {
			pstmt = conn.prepareStatement("update wb_group set cid = ? , name = ? , status = ? where gid = ?");
			
			pstmt.setInt(1, bean.getCid());
			pstmt.setString(2, bean.getName());
			pstmt.setString(3, bean.getStatus());
			pstmt.setInt(4, bean.getGid());
			
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
	 * 删除分组
	 * @param gid
	 * @return 
	 */
	public int removeGroup(int gid){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		int result = -1;
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return -1;
		}
		
		
		try {
			pstmt = conn.prepareStatement("update wb_account set gid = null WHERE gid = ?");
			pstmt.setInt(1, gid);
			result = pstmt.executeUpdate();
			
			pstmt = conn.prepareStatement("delete from wb_group where gid = ?");
			pstmt.setInt(1, gid);
			
			result = pstmt.executeUpdate();
			
			
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		} finally {
			closeSQL(pstmt);
			closeSQL(conn);
		}
		
		
		return result;
		
	}
	/**
	 * 设置分组用户数量
	 * @param gid
	 * @param count
	 * @return 返回实际更新数量
	 */
	public int setGroupUser(int gid, int count){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = -1;
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return -1;
		}
		
		
		try {
			pstmt = conn.prepareStatement("select count(*) as count from wb_account where gid = ?");
			pstmt.setInt(1, gid);
			rs = pstmt.executeQuery();
			int gidcount = 0;
			if(rs.next()){
				gidcount = rs.getInt("count");
			}
			if(gidcount < count){
				gidcount = count - gidcount;
			}else if(gidcount > count){
				pstmt = conn.prepareStatement("UPDATE wb_account SET gid = null WHERE gid = ?");
				pstmt.setInt(1, gid);
				pstmt.executeUpdate();
			}
			
			
			pstmt = conn.prepareStatement("UPDATE wb_account SET gid = ? WHERE gid IS NULL LIMIT ?");
			pstmt.setInt(1, gid);
			pstmt.setInt(2, gidcount);
			
			result = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		} finally {
			closeSQL(rs);
			closeSQL(pstmt);
			closeSQL(conn);
		}
		
		
		return result;
		
	}
	/**
	 * 获取分组列表
	 * @return
	 */
	public List<GroupBean> getGroupList(int cid){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		List<GroupBean> data = new ArrayList<GroupBean>();
		ResultSet rs = null;
		int result = -1;
		
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return data;
		}
		
		try {
			pstmt = conn.prepareStatement("select * from wb_group where cid = ?");
			pstmt.setInt(1, cid);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				GroupBean bean = new GroupBean();
				bean.setGid(rs.getInt("gid"));
				bean.setCid(rs.getInt("cid"));
				bean.setName(rs.getString("name"));
				bean.setStatus(rs.getString("status"));
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
	
}
