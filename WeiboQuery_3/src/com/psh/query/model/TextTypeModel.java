package com.psh.query.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.psh.base.util.PshLogger;
import com.psh.base.util.SQLConn;
import com.psh.query.bean.SuperModel;
import com.psh.query.bean.TextTypeBean;
import com.psh.query.bean.TextTypeBean;

public class TextTypeModel extends SuperModel {

	/**
	 * 获取内容分类列表
	 * 
	 * @return
	 */
	public List<TextTypeBean> getTextTypeList(){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		List<TextTypeBean> data = new ArrayList<TextTypeBean>();
		ResultSet rs = null;
		int result = -1;
		
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return data;
		}
		
		try {
			pstmt = conn.prepareStatement("select * from wb_texttype ");
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				TextTypeBean bean = new TextTypeBean();
				bean.setTtid(rs.getInt("ttid"));
				bean.setName(rs.getString("name"));
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
