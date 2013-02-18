package com.psh.query.model;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.psh.base.util.PshLogger;
import com.psh.base.util.SQLConn;
import com.psh.query.bean.UserBean;

public class UserModel {
	
	private Connection conn = null;
	private PreparedStatement ps = null;
	private ResultSet rs = null;
	
	//增加用户
	public synchronized boolean addUser(UserBean user){
		conn = SQLConn.getInstance().getConnection();
		boolean isSuccess = false;
		
		try {

			ps = conn.prepareStatement("insert into wb_user values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			ps.setString(1, user.getUid());
			ps.setString(2, user.getUck());
			if(user.getProv() != null){
				
				ps.setString(3, user.getProv());
				if(user.getCity() != null){
					
					ps.setString(4, user.getCity());
				}else{
					ps.setString(4,"");
				}
			}else{
				ps.setString(3, "");
				ps.setString(4, "");
			}
			ps.setString(5, user.getSex());
			if(user.getEmo() != null){
				ps.setString(6, user.getEmo());
				
			}else{
				ps.setString(6, "");
			}
			if(user.getDate() != null){
				ps.setString(7, user.getDate());
				
			}else{
				ps.setString(7,"");
			}
			if(user.getBlo() != null){
				
				ps.setString(8, user.getBlo());
			}else{
				ps.setString(8, "");
			}
			if(user.getTag() != null){
				ps.setString(9, user.getTag());
				
			}else{
				ps.setString(9, "");
			}
			ps.setString(10, user.getFans());
			ps.setString(11, user.getFol());
			if(user.getInfo() != null){
				
				ps.setString(12, user.getInfo());
			}else{
				ps.setString(12,"");
			}
			
			if(user.getCom() != null){
				
				ps.setString(13, user.getCom());
			}else{
				ps.setString(13,"");
			}
			
			if(user.getStu() != null){
				
				ps.setString(14, user.getStu());
			}else{
				ps.setString(14,"");
			}
			
			int result = ps.executeUpdate();
			
			if(result > 0){
				
				isSuccess = true;
				
			}
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		}finally{
			this.closeConnection();
		}
		
		return isSuccess;
		
	}
	
	//查找用户是否存在
	public synchronized boolean checkUserIsExsit(String uid){
		System.out.println("判断用户是否存在");
		conn = SQLConn.getInstance().getConnection();
		boolean isSuccess = false;
		
		try {
			ps = conn.prepareStatement("select count(*) from wb_user where uid='" + uid + "'");
			
			rs = ps.executeQuery();
			if(rs == null){
				
				return isSuccess;
				
			}
			if(rs.next()){
				
				if(rs.getInt(1) > 0 ){
					System.out.println("用户存在");
					isSuccess = true;
					
				}
				
			}else{
				return isSuccess;
			}
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		}finally{
			
			this.closeConnection();
		}
		
		return isSuccess;
		
		
	}
	
	//查找指定地区的用户数量
	public int getUserCountByProv(String provName){
			
		int count = -1;
		
			conn = SQLConn.getInstance().getConnection();
			
			try {
				ps = conn.prepareStatement("select count(*) from wb_user where prov='" + provName + "'");
				
				rs = ps.executeQuery();
				if(rs == null){
					
					return -1;
					
				}
				if(rs.next()){
					
					count = rs.getInt(1);
					
				}else{
					return -1;
				}
				
			} catch (SQLException e) {
				PshLogger.logger.error(e.getMessage());
			}finally{
				
				this.closeConnection();
			}
			
			return count;
			
			
		}
	
	//获取总数
	public int getAllCount(){
		
		int count = -1;
		
			conn = SQLConn.getInstance().getConnection();
			
			try {
				ps = conn.prepareStatement("select count(*) from wb_user");
				
				rs = ps.executeQuery();
				if(rs == null){
					
					return -1;
					
				}
				if(rs.next()){
					
					count = rs.getInt(1);
					
				}else{
					return -1;
				}
				
			} catch (SQLException e) {
				PshLogger.logger.error(e.getMessage());
			}finally{
				
				this.closeConnection();
			}
			
			return count;
			
			
		}
	
	//修改用户
	public synchronized boolean updateUser(UserBean user){
		
		conn = SQLConn.getInstance().getConnection();
		boolean isSuccess = false;
		
		try {
			
			ps = conn.prepareStatement("update wb_user set nck=?,prov=?,city=?,sex=?,emo=?,date=?,blo=?,tag=?,fans=?,fol=?,info=?,com=?,stu=? where uid=?");
			
			ps.setString(1, user.getUck());
			if(user.getProv() != null){
				
				ps.setString(2, user.getProv());
				if(user.getCity() != null){
					
					ps.setString(3, user.getCity());
				}else{
					ps.setString(3,"");
				}
			}else{
				ps.setString(2, "");
				ps.setString(3, "");
			}
			ps.setString(4, user.getSex());
			if(user.getEmo() != null){
				ps.setString(5, user.getEmo());
				
			}else{
				ps.setString(5, "");
			}
			if(user.getDate() != null){
				ps.setString(6, user.getDate());
				
			}else{
				ps.setString(6,"");
			}
			if(user.getBlo() != null){
				
				ps.setString(7, user.getBlo());
			}else{
				ps.setString(7, "");
			}
			if(user.getTag() != null){
				ps.setString(8, user.getTag());
				
			}else{
				ps.setString(8, "");
			}
			ps.setString(9, user.getFans());
			ps.setString(10, user.getFol());
			if(user.getInfo() != null){
				
				ps.setString(11, user.getInfo());
			}else{
				ps.setString(11,"");
			}
			
			if(user.getCom() != null){
				
				ps.setString(12, user.getCom());
			}else{
				ps.setString(12,"");
			}
			
			if(user.getStu() != null){
				
				ps.setString(13, user.getStu());
			}else{
				ps.setString(13,"");
			}
			
			ps.setString(14, user.getUid());
			int result = ps.executeUpdate();
			
			if(result > 0){
				
				isSuccess = true;
				
			}
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		}finally{
			this.closeConnection();
		}
		
		return isSuccess;
		
	}
	
	//指定条件搜索本地用户结果数量
	public int getUserLocalQuery(Map<String, Object> conditions){
		
		int count = -1;
		
		conn = SQLConn.getInstance().getConnection();
		
		String sql = "select count(*) from wb_user where ";
		
		if(conditions.containsKey("nck")){
			sql += "nck like '%" + conditions.get("nck").toString() + "%' ";
		}
		
		if(conditions.containsKey("prov")){
			sql += "and prov='" + conditions.get("prov").toString() + "' ";
		}
		
		if(conditions.containsKey("city")){
			sql += "and city='" + conditions.get("city").toString() + "' ";
		}
		
		if(conditions.containsKey("sex")){
			sql += "and sex='" + conditions.get("sex").toString() + "' ";
		}
		
		if(conditions.containsKey("emo")){
			sql += "and emo like '%" + conditions.get("emo").toString() + "%' ";
		}
		
		//年龄
		if(conditions.containsKey("date")){
			
		}
		
		if(conditions.containsKey("blo")){
			sql += "and blo='" + conditions.get("blo").toString() + "' ";
		}
		
		if(conditions.containsKey("tag")){
			sql += "and tag like '%" + conditions.get("tag") + "%' ";
		}
		
		//粉丝>=<?大于?
		if(conditions.containsKey("fans")){
			sql += "and fans=" + (int)conditions.get("fans") + " ";
		}
		
		//关注>=<?大于?
		if(conditions.containsKey("fol")){
			sql += "and fol=" + (int)conditions.get("fol") + " ";
		}
		
		if(conditions.containsKey("info")){
			sql += "and info like '%" + conditions.get("info").toString() + "%' ";
		}
		
		if(conditions.containsKey("com")){
			sql += "and com like '%" + conditions.get("com").toString() + "%' ";
		}
		
		if(conditions.containsKey("stu")){
			sql += "and stu like '%" + conditions.get("stu").toString() + "%' ";
		}
		
		try {
			ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();
			
			if(rs == null){
				
				count = rs.getInt(1);
				
			}
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		}
		
		return count;
		
		
	}
	
	
	//指定条件搜索本地获得@的用户名
	public List<String> getUserNameLocalQuery(Map<String, Object> conditions,int count){

		List<String> userNameList = new ArrayList<String>();
		
		conn = SQLConn.getInstance().getConnection();
		
		String sql = "select nck from wb_user where ";
		
		if(conditions.containsKey("nck")){
			sql += "and nck like '%" + conditions.get("nck").toString() + "%' ";
		}
		
		if(conditions.containsKey("prov")){
			sql += "and prov='" + conditions.get("prov").toString() + "' ";
		}
		
		if(conditions.containsKey("city")){
			sql += "and city='" + conditions.get("city").toString() + "' ";
		}
		
		if(conditions.containsKey("sex")){
			sql += "and sex='" + conditions.get("sex").toString() + "' ";
		}
		
		if(conditions.containsKey("emo")){
			sql += "and emo like '%" + conditions.get("emo").toString() + "%' ";
		}
		
		//年龄
		if(conditions.containsKey("date")){
			String dateString = (String)conditions.get("date");
			if(dateString.indexOf("+") != -1){
				
				sql += "and ((2013 - LEFT(DATE,LOCATE('年', DATE)-1)) > " + Integer.parseInt(dateString.substring(0,dateString.indexOf("+")-1)) + ") ";
			}else if(dateString.indexOf("-") != -1){
				sql += "and ((2013 - LEFT(DATE,LOCATE('年', DATE)-1)) < " + Integer.parseInt(dateString.substring(0,dateString.indexOf("-")-1)) + ") ";
			}else if(dateString.indexOf("~") != -1){
				
				sql += "and ((2013 - LEFT(DATE,LOCATE('年', DATE)-1)) between " + Integer.parseInt(dateString.substring(0,dateString.indexOf("~")-1)) + " and " + Integer.parseInt(dateString.substring(dateString.indexOf("~")+1,dateString.length())) + ") ";
				
			}else if(dateString.indexOf("=") != -1){
				
				sql += "and ((2013 - LEFT(DATE,LOCATE('年', DATE)-1)) = " + Integer.parseInt(dateString.substring(0,dateString.indexOf("=")-1)) + ") ";
				
			}
		}
		
		if(conditions.containsKey("blo")){
			sql += "and blo='" + conditions.get("blo").toString() + "' ";
		}
		
		if(conditions.containsKey("tag")){
			sql += "and tag like '%" + conditions.get("tag") + "%' ";
		}
		
		//粉丝>=<?大于?
		if(conditions.containsKey("fans")){
			String fansString = (String)conditions.get("fans");
			if(fansString.indexOf("~") != -1){
				
				sql += "and (fans between " + Integer.parseInt(fansString.substring(0, fansString.indexOf("~")-1)) + " and " + Integer.parseInt(fansString.substring(fansString.indexOf("~") + 1, fansString.length())) + ") ";
				
			}else if(fansString.indexOf("+") != -1){
				
				sql +="and fans > " + Integer.parseInt(fansString.substring(0, fansString.indexOf("+")-1)) + " ";
				
			}else if(fansString.indexOf("-") != -1){
				
				sql +="and fans < " + Integer.parseInt(fansString.substring(0, fansString.indexOf("-")-1)) + " ";
				
			}
		}
		
		//关注>=<?大于?
		if(conditions.containsKey("fol")){

			String fansString = (String)conditions.get("fol");
			if(fansString.indexOf("~") != -1){
				
				sql += "and (fol between " + Integer.parseInt(fansString.substring(0, fansString.indexOf("~")-1)) + " and " + Integer.parseInt(fansString.substring(fansString.indexOf("~") + 1, fansString.length())) + ") ";
				
			}else if(fansString.indexOf("+") != -1){
				
				sql +="and fol > " + Integer.parseInt(fansString.substring(0, fansString.indexOf("+")-1)) + " ";
				
			}else if(fansString.indexOf("-") != -1){
				
				sql +="and fol < " + Integer.parseInt(fansString.substring(0, fansString.indexOf("-")-1)) + " ";
				
			}
			
		}
		
		if(conditions.containsKey("info")){
			sql += "and info like '%" + conditions.get("info").toString() + "%' ";
		}
		
		if(conditions.containsKey("com")){
			sql += "and com like '%" + conditions.get("com").toString() + "%' ";
		}
		
		if(conditions.containsKey("stu")){
			sql += "and stu like '%" + conditions.get("stu").toString() + "%' ";
		}
		
		sql += " limit 0," + count;
		
		sql = sql.replaceFirst("and", "");
		
		System.out.println(sql);
		
		try {
			ps = conn.prepareStatement(sql);
			
			rs = ps.executeQuery();
			
			if(rs == null){
				
				return null;
				
			}
			
			while(rs.next()){
				
				userNameList.add(rs.getString("nck"));
			}
			
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		}
		
		return userNameList;
		
		
	}
	
	private synchronized void closeConnection(){
		
		if(this.rs != null){
			
			try{
				rs.close();
				rs = null;
			}catch(SQLException e){
				PshLogger.logger.error(e.getMessage());
			}
			
		}
		
		if (this.ps != null) {

			try {
				ps.close();
				ps = null;
			} catch (SQLException e) {
				PshLogger.logger.error(e.getMessage());
			}

		}

		try {
			if (this.conn != null || !this.conn.isClosed()) {

				try {
					conn.close();
					conn = null;
				} catch (SQLException e) {
					PshLogger.logger.error(e.getMessage());
				}
			}
		} catch (SQLException e) {
			PshLogger.logger.error(e.getMessage());
		}
		
	}
	
}
