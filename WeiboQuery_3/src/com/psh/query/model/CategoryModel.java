package com.psh.query.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.psh.base.util.PshLogger;
import com.psh.base.util.SQLConn;
import com.psh.query.bean.CategoryBean;

public class CategoryModel extends SuperModel {

	/**
	 * 增加分类
	 * @param category
	 * @return
	 */
	public boolean addCategory(CategoryBean category){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		int result = -1;
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return false;
		}
		
		try {
			pstmt = conn.prepareStatement("insert into wb_category(`name`,`desc`) values(?,?)");
			
			pstmt.setString(1, category.getName());
			pstmt.setString(2, category.getDesc());
			
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
	 * 获取分类列表
	 * @return
	 */
	public List<CategoryBean> getCategoryList(){
		Connection conn = SQLConn.getInstance().getConnection();
		PreparedStatement pstmt = null;
		List<CategoryBean> data = new ArrayList<CategoryBean>();
		ResultSet rs = null;
		int result = -1;
		
		if(conn == null){
			PshLogger.logger.error("Get SQL Connection error");
			return data;
		}
		
		try {
			pstmt = conn.prepareStatement("select * from wb_category");
			
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				CategoryBean bean = new CategoryBean();
				bean.setCid(rs.getInt("cid"));
				bean.setName(rs.getString("name"));
				bean.setDesc(rs.getString("desc"));
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
