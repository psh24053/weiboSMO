package com.psh.query.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.psh.base.json.JSONArray;
import com.psh.base.json.JSONException;
import com.psh.base.json.JSONObject;
import com.psh.base.util.PshLogger;
import com.psh.base.util.SQLConn;
import com.psh.query.bean.AccountBean;
import com.psh.query.bean.ProxyBean;
import com.psh.query.model.AccountModel;
import com.psh.query.model.CityModel;
import com.psh.query.model.ProvModel;

public class InfoService {

	private AccountBean account;
	private LoginService login;
	
	public InfoService(){
		
	}
	/**
	 * 根据uid,从数据库获取账号密码，然后从新浪获取最新数据，并且将其内容同步到数据库
	 * @param uid
	 * @return
	 */
	public boolean synchronizeInfoFromSina(long uid){
		AccountModel model = new AccountModel();
		account = model.getAccount(uid);
		
		if(account == null){
			PshLogger.logger.error("uid not found!");
			return false;
		}
//		ProxyService proxyService = new ProxyService();
//		proxyService.loadProxyDataStatic(1);
		
		login = LoginService.Login_3G_Sina(account.getEmail(), account.getPassword());
		
		if(login == null){
			PshLogger.logger.error("login error");
			return false;
		}
		/*
		 * userInfoSetting包含了：
		 * tags -> tags.usertags
		 * birthday -> userInfo.data.birthday
		 * city -> userInfo.data.city
		 * description -> userInfo.data.description
		 * gender -> userInfo.data.gender
		 * nickname -> userInfo.data.screen_name
		 * prov -> userInfo.data.province
		 * 
		 */
		JSONObject userInfoSetting = login.executeJSON("http://m.weibo.cn/setting/userInfoSetting?uid="+login.getUid(), false, "http://m.weibo.cn/users/"+login.getUid(),"get");
		
		if(userInfoSetting == null){
			PshLogger.logger.error("get userInfoSetting fail.");
			return false;
		}
		
		try {
			JSONObject userInfo = userInfoSetting.getJSONObject("userInfo");
			JSONObject data = userInfo.getJSONObject("data");
			if(data.has("error")){
				return false;
			}
			JSONObject tags = userInfoSetting.getJSONObject("tags");
			String tagString = "";
			if(!tags.getString("msg").equals("您还没添加任何标签呢")){
				JSONArray t = tags.getJSONArray("usertags");
				
				for(int i = 0 ; i < t.length() ; i ++){
					JSONObject tag = t.getJSONObject(i);
					
					tagString += tag.getString("name");
				}
				
			}
			
			account.setNickname(data.getString("screen_name"));
			account.setBirthday(data.getString("birthday"));
			account.setInfo(data.getString("description"));
			account.setSex(data.getString("gender").equals("m")? "男":"女");
			account.setTags(tagString);
			
			int city = data.getInt("city");
			int prov = data.getInt("province");
			
			CityModel citymodel = new CityModel();
			ProvModel provmodel = new ProvModel();
			
			String cityName = citymodel.getProvNameByID(city, prov);
			String provName = provmodel.getProvNameByID(prov);
			
			account.setCity(cityName);
			account.setProv(provName);
			
		} catch (JSONException e) {
			PshLogger.logger.error(e.getMessage());
			return false;
		}
		
		return model.UpdateAccount(account);
	}
	public static void main(String[] args) {
		InfoService s = new InfoService();
		s.synchronizeInfoFromSina(3177594532l);
	}
	/**
	 * 根据UID从数据库中获取数据，并且将其内容同步到新浪
	 * @param uid
	 * @return
	 */
	public boolean synchronizeInfoFromDB(int uid){
		return true;
	}
	public AccountBean getAccount() {
		return account;
	}
	public void setAccount(AccountBean account) {
		this.account = account;
	}
	public LoginService getLogin() {
		return login;
	}
	public void setLogin(LoginService login) {
		this.login = login;
	}

	
	
}
