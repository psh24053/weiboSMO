package com.psh.query.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
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
	
	public void closeSQL(){
		
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
