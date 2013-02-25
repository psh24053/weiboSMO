package com.psh.query.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.psh.base.util.PshLogger;
import com.psh.base.util.SQLConn;
import com.psh.query.bean.AccountBean;
import com.psh.query.bean.MsgBean;
import com.psh.query.bean.SuperModel;

public class ReplyModel extends SuperModel {

	/**
	 * 更新指定uid下的指定mid的mark为1
	 * @param mid
	 * @param uid
	 * @return
	 */
	public boolean updateMark(String mid, long uid){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = -1;
		
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return false;
		}
		
		try {
			pstmt = conn.prepareStatement("update wb_reply set mark = 1 where fuid = ? and mid = ?");
			pstmt.setLong(1, uid);
			pstmt.setString(2, mid);
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
	 * 获取最后一个lastMsg
	 * @param uid
	 * @return
	 */
	public MsgBean getLastMsg(long uid){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = -1;
		

		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return null;
		}
		
		try {
			pstmt = conn.prepareStatement("select * from wb_reply where fuid = ? order by time desc limit 1");
			pstmt.setLong(1, uid);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				MsgBean msg = new MsgBean();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				msg.setCon(rs.getString("con"));
				msg.setFuid(rs.getLong("fuid"));
				msg.setImage(rs.getString("image"));
				msg.setMark(rs.getInt("mark"));
				msg.setMid(rs.getString("mid"));
				msg.setNck(rs.getString("nck"));
				msg.setOcon(rs.getString("ocon"));
				msg.setOmid(rs.getString("omid"));
				msg.setOnck(rs.getString("onck"));
				msg.setOtime(sdf.format(new Date(rs.getLong("otime"))));
				msg.setOuid(rs.getLong("ouid"));
				msg.setTime(sdf.format(new Date(rs.getLong("time"))));
				msg.setType(rs.getString("type"));
				msg.setUid(rs.getLong("uid"));
				return msg;
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
	 * 批量插入
	 * @param data
	 * @return
	 */
	public int batchInsert(List<MsgBean> data){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int[] result = null;
		

		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return 0;
		}
		
		try {
			conn.setAutoCommit(false);
			String sql = "insert into wb_reply(mid,fuid,type,time,uid,nck,con,image,ouid,onck,ocon,otime,omid,mark) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			pstmt = conn.prepareStatement(sql);
			
			for(int i = 0 ; i < data.size() ; i ++){
				MsgBean msg = data.get(i);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				
				pstmt.setString(1, msg.getMid());
				pstmt.setLong(2, msg.getFuid());
				pstmt.setString(3, msg.getType());
				try {
					if(msg.getTime() != null){
						pstmt.setLong(4, sdf.parse(msg.getTime()).getTime());
					}else{
						pstmt.setLong(4, 0);
					}
					if(msg.getOtime() != null){
						pstmt.setLong(12, sdf.parse(msg.getOtime()).getTime());
					}else{
						pstmt.setLong(12, 0);
					}
				} catch (ParseException e) {
					PshLogger.logger.error(e.getMessage(),e);
				}
				pstmt.setLong(5, msg.getUid());
				pstmt.setString(6, msg.getNck());
				pstmt.setString(7, msg.getCon());
				pstmt.setString(8, msg.getImage());
				pstmt.setLong(9, msg.getOuid());
				pstmt.setString(10, msg.getOnck());
				pstmt.setString(11, msg.getOcon());
				pstmt.setString(13, msg.getOmid());
				pstmt.setInt(14, 0);
				pstmt.addBatch();
				
			}
			
			result = pstmt.executeBatch();
			conn.commit();
			
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		} finally {
			closeSQL(rs);
			closeSQL(pstmt);
			closeSQL(conn);
		}
		
		
		return result.length;
	}
	
	/**
	 * 获取回复列表
	 * @param uid
	 * @return
	 */
	public List<MsgBean> getReplyListByLastMid(long uid){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<MsgBean> data = new ArrayList<MsgBean>();
		int result = -1;
		

		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return null;
		}
		
		try {
			pstmt = conn.prepareStatement("select * from wb_reply where fuid = ? order by time desc");
			pstmt.setLong(1, uid);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				MsgBean msg = new MsgBean();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				msg.setCon(rs.getString("con"));
				msg.setFuid(rs.getLong("fuid"));
				msg.setImage(rs.getString("image"));
				msg.setMark(rs.getInt("mark"));
				msg.setMid(rs.getString("mid"));
				msg.setNck(rs.getString("nck"));
				msg.setOcon(rs.getString("ocon"));
				msg.setOmid(rs.getString("omid"));
				msg.setOnck(rs.getString("onck"));
				long otime = rs.getLong("otime");
				if(otime != 0){
					msg.setOtime(sdf.format(new Date(otime)));
				}
				msg.setOuid(rs.getLong("ouid"));
				long time = rs.getLong("time");
				if(time != 0){
					msg.setTime(sdf.format(new Date(time)));
				}
				msg.setType(rs.getString("type"));
				msg.setUid(rs.getLong("uid"));
				data.add(msg);
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
