package com.psh.query.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.psh.base.json.JSONException;
import com.psh.base.json.JSONObject;
import com.psh.base.util.PshLogger;
import com.psh.base.util.SQLConn;
import com.psh.query.bean.AccountBean;
import com.psh.query.model.AccountModel;

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
	public boolean synchronizeInfoFromSina(int uid){
//		AccountModel model = new AccountModel();
//		account = model.getAccount(uid);
//		
//		if(account == null){
//			PshLogger.logger.error("uid not found!");
//			return false;
//		}
		
//		login = LoginService.Login_3G_Sina(account.getEmail(), account.getPassword());
		
		login = LoginService.Login_3G_Sina("135de084d3@ksgym.com", "2f628b0835");
		
		if(login == null){
			PshLogger.logger.error("login error");
			return false;
		}
		
		System.out.println(login.getUid());
		JSONObject userInfoSetting = login.executeJSON("http://m.weibo.cn/setting/userInfoSetting?uid="+login.getUid(), false, "http://m.weibo.cn/users/"+login.getUid());
		
		if(userInfoSetting == null){
			PshLogger.logger.error("get actToken fail.");
			return false;
		}
		
		System.out.println(userInfoSetting);
		
		
		try {
			JSONObject userInfo = userInfoSetting.getJSONObject("userInfo");
			JSONObject data = userInfo.getJSONObject("data");
		} catch (JSONException e) {
			PshLogger.logger.error(e.getMessage());
			return false;
		}
		
		
		
		
		return true;
	}
	public static void main(String[] args) {
		InfoService s = new InfoService();
		s.synchronizeInfoFromSina(1);
	}
	/**
	 * 根据UID从数据库中获取数据，并且将其内容同步到新浪
	 * @param uid
	 * @return
	 */
	public boolean synchronizeInfoFromDB(int uid){
		return true;
	}

	
	
}
