package com.psh.query.util;

import java.util.ArrayList;
import java.util.List;

import com.psh.base.json.JSONArray;
import com.psh.base.json.JSONException;
import com.psh.base.json.JSONObject;
import com.psh.base.util.PshLogger;
import com.psh.query.bean.UserBean;

public class QueryResultAnalysis {
	
	//解析得到的用户列表
	public List<UserBean> getUserList(JSONObject userListJson){
		
		
		List<UserBean> uidList = new ArrayList<UserBean>();
		
		try {
			if(userListJson.has("data")){
				
				JSONArray data = (JSONArray)userListJson.get("data");
				for(int i = 0; i < data.length() ; i++){
					UserBean user = new UserBean();
					JSONObject oneData = (JSONObject) data.get(i);
					user.setUid(oneData.getString("uid"));
					user.setFans(oneData.getInt("fans") + "");
					user.setFol(oneData.getInt("friends_count") + "");
					
					uidList.add(user);
					
				}
			}
			
			
		} catch (JSONException e) {
			PshLogger.logger.error("返回值为Html");
			return null;
		}
		
		return uidList;
		
	}
	
	//解析一个用户的详细信息
	public UserBean getUserInfo(JSONObject userJson){
		
		UserBean user = new UserBean();
		
		try {
			
			JSONObject userInfoDetail = userJson.getJSONObject("userInfoDetail");
			JSONObject basicInfo = userInfoDetail.getJSONObject("basicInfo");
			
			if(basicInfo.has("birthday")){
				if(basicInfo.getString("birthday") != null && !basicInfo.getString("birthday").equals("")){
					user.setDate(basicInfo.getString("birthday"));
					
				}
				
			}
			
			if(basicInfo.has("description")){
				
				if(basicInfo.getString("description") != null && !basicInfo.getString("description").equals("")){
					user.setInfo(basicInfo.getString("description"));
					
				}
			}
			
			if(basicInfo.getString("gender") != null && !basicInfo.getString("gender").equals("")){
				if(basicInfo.getString("gender").equals("m")){
					user.setSex("男");
				}else if(basicInfo.getString("gender").equals("f")){
					user.setSex("女");
				}
				
			}
			
			if(basicInfo.has("location")){
				
				if(basicInfo.getString("location") != null && !basicInfo.getString("location").equals("")){
					String[] address = basicInfo.getString("location").split(" ");
					if(address.length == 2){
						user.setProv(address[0]);
						user.setCity(address[1]);
					}else if(address.length == 1){
						user.setProv(address[0]);
					}
					
				}
			}
			
			if(basicInfo.has("name")){
				
				if(basicInfo.getString("name") != null && !basicInfo.getString("name").equals("")){
					user.setUck(basicInfo.getString("name"));
					
				}
			}
			
			//学校信息
			if(userInfoDetail.has("editInfo")){
				
				JSONArray editInfo = userInfoDetail.getJSONArray("editInfo");
				
				if(editInfo != null){
					String school = "";
					for(int i = 0 ; i < editInfo.length() ; i++){
						school += ((JSONObject)editInfo.get(i)).getString("school") + ",";
					}
					
					user.setStu(school);
					
				}
			}
			
			//公司信息
			
			if(userInfoDetail.has("careerInfo")){
				
				JSONArray careerInfo = userInfoDetail.getJSONArray("careerInfo");
				
				if(careerInfo != null){
					
					String company = "";
					for(int i = 0 ; i < careerInfo.length() ; i++){
						company += ((JSONObject)careerInfo.get(i)).getString("company") + ",";
					}
					
					user.setCom(company);
					
				}
				
			}
			
			if(userJson.has("tags")){
				
				JSONObject tags = userJson.getJSONObject("tags");
				
				if(tags.has("usertags") && tags.getInt("ok") == 1){
					
					JSONArray usertags = tags.getJSONArray("usertags");
					
					if(usertags != null){
						String userTotalTags = "";
						
						for(int i = 0 ; i < usertags.length() ; i++){
							
							userTotalTags += ((JSONObject)usertags.get(i)).getString("name") + ",";
							
						}
						
						user.setTag(userTotalTags);
						
					}
				}
			}
			
		} catch (JSONException e) {
			PshLogger.logger.error("返回值为html");
			return null;
		}
		
		return user;
		
	}
	
	//获得0级页数
	public int getFirstQueryPageNumber(JSONObject json){
		int pageNumber = -1;
		
		try {
			if(json.has("maxPage")){
				
				pageNumber = json.getInt("maxPage");
			}
		} catch (JSONException e) {
			PshLogger.logger.error(e.getMessage());
		}
		
		return pageNumber;
	}
	
	//获得0+级页数
	public int getQueryPageNumber(JSONObject json){
		int pageNumber = -1;
		
		try {
			if(json.has("maxPage")){
				
				pageNumber = json.getInt("maxPage");
			}
		} catch (JSONException e) {
			PshLogger.logger.error("返回的页数为html");
			return -1;
		}
		
		return pageNumber;
	}
	
}
