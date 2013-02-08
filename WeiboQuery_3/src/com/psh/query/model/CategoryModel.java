package com.psh.query.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.psh.base.util.PshLogger;
import com.psh.base.util.SQLConn;
import com.psh.query.bean.CategoryBean;

public class CategoryModel {

	/**
	 * 增加分类
	 * @param category
	 * @return
	 */
	public boolean addCategory(CategoryBean category){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return false;
		}
		
		try {
			pstmt = conn.prepareStatement("");
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		}
		
		
		
		
		
		
		return false;
	}
	
	
	
}
