package com.psh.query.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.psh.base.util.PshLogger;
import com.psh.base.util.SQLConn;
import com.psh.query.bean.TextBean;
import com.psh.query.bean.TextBean;

public class TextModel extends SuperModel {

	/**
	 * 获取随机内容列表
	 * @param ttid
	 * @return
	 */
	public List<TextBean> getTextList(int ttid, int count){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		List<TextBean> data = new ArrayList<TextBean>();
		ResultSet rs = null;
		int result = -1;
		
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return data;
		}
		
		try {
			pstmt = conn.prepareStatement("SELECT * FROM wb_text WHERE ttid = ? ORDER BY RAND() LIMIT ?");
			pstmt.setInt(1, ttid);
			pstmt.setInt(2, count);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				TextBean bean = new TextBean();
				bean.setTid(rs.getInt("tid"));
				bean.setTtid(rs.getInt("ttid"));
				bean.setText(rs.getString("text"));
				bean.setImg(rs.getString("img"));
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
	 * 获取内容
	 * @param tid
	 * @return
	 */
	public TextBean getTextContent(int tid){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = -1;
		
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return null;
		}
		
		try {
			pstmt = conn.prepareStatement("select * from wb_text where tid = ?");
			pstmt.setInt(1, tid);
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				TextBean bean = new TextBean();
				bean.setTid(rs.getInt("tid"));
				bean.setTtid(rs.getInt("ttid"));
				bean.setText(rs.getString("text"));
				bean.setImg(rs.getString("img"));
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
	
	
	
}
