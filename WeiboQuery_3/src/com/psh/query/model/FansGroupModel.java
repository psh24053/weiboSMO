package com.psh.query.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.psh.base.util.PshLogger;
import com.psh.base.util.SQLConn;
import com.psh.query.bean.SuperModel;

public class FansGroupModel extends SuperModel {

	/**
	 * 获取分组角色的BOSS
	 * @param gid
	 * @return
	 */
	public long getFansGroupBoss(int gid){
		return 0;
	}
	/**
	 * 获取分组中的组长的列表
	 * @param gid
	 * @return
	 */
	public long[] getFansGroupMaster(int gid){
		return null;
	}
	/**
	 * 获取分组中某个组长的成员
	 * @param gid
	 * @param uid
	 * @return
	 */
	public long[] getFansGroupByMaster(int gid, int uid){
		return null;
	}
	
	/**
	 * 设置分组角色（Boss），一个分组只能有1个boss
	 * @param uid
	 * @param gid
	 * @return
	 */
	public boolean setFansGroupBoss(long uid, int gid){
		return false;
	}
	/**
	 * 设置分组角色（Master），一个分组只能有9个master
	 * @param uid
	 * @param gid
	 * @return
	 */
	public boolean setFansGroupMaster(long uid, int gid){
		return false;
	}
	/**
	 * 自动将一个人数达到1000的分组进行角色化
	 * 1个BOSS，领导9个组长，每个组长领导110名组员
	 * 
	 * 如果分组人数没有达到1000，则无法进行操作
	 * @param gid
	 * @return
	 */
	public boolean setFansGroupAuto(int gid){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = -1;
		
		
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return false;
		}
		
		try {
			pstmt = conn.prepareStatement("select * from wb_group where gid = ?");
			pstmt.setInt(1, gid);
			rs = pstmt.executeQuery();
			
			if(!rs.next()){
				return false;
			}
			
			pstmt = conn.prepareStatement("select count(*) as count from wb_account where gid = ?");
			pstmt.setInt(1, gid);
			rs = pstmt.executeQuery();
			
			if(!rs.next()){
				return false;
			}
			
			int groupcount = rs.getInt("count");
			// 分组人数不够1000
			if(groupcount != 1000){
				return false;
			}
			
			pstmt = conn.prepareStatement("select * from wb_account where gid = ? order by rand()");
			pstmt.setInt(1, gid);
			rs = pstmt.executeQuery();
			
			int index = 0;
			long boss = 0;
			Map<Long, Object> masters = new HashMap<Long, Object>(); 
			
			
			
			while(rs.next()){
				
				if(boss == 0){
					boss = rs.getLong("uid");
				}else if(masters.size() < 9){
					long temp = rs.getLong("uid");
					List<Long> fans = new ArrayList<Long>();
					while(rs.next() && fans.size() < 110){
						fans.add(rs.getLong("uid"));
					}
					masters.put(temp, fans);
					
				}
				
				
				index++;
			}
			
			
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
	 * 执行内部互粉操作
	 * @param gid
	 * @return
	 */
	public boolean FansGroupExecute(int gid){
		return false;
	}
	/**
	 * 执行两个分组之间的互粉操作
	 * @param agid
	 * @param bgid
	 * @return
	 */
	public boolean FansGroupExecuteByGroup(int agid, int bgid){
		return false;
	}
	
}
