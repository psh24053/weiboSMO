package com.psh.query.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Wrapper;
import java.util.HashMap;

public class SuperModel implements Serializable {

	public Serializable data = new HashMap();

	public Object getValue(Object key){
		return ((HashMap)data).get(key);
	}
	
	public void putValue(Object key, Object value){
		((HashMap)data).put(key, value);
	}
	
	public void closeSQL(Connection conn){
		if(conn != null){
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void closeSQL(PreparedStatement pstmt){
		if(pstmt != null){
			try {
				pstmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void closeSQL(ResultSet rs){
		if(rs != null){
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String str = "";
		Field[] fields = getClass().getDeclaredFields();
		
		for(Field f : fields){
			try {
				str += f.getName()+" ( "+f.get(this)+" )  ,";
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
		return super.toString()+"   "+str;
	}
	
	
}
