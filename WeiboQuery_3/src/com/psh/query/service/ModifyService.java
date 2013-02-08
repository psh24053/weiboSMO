package com.psh.query.service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

import com.psh.base.json.JSONObject;
import com.psh.base.util.PshLogger;
import com.psh.query.bean.AccountBean;
import com.psh.query.bean.GroupBean;
import com.psh.query.model.AccountModel;
import com.psh.query.model.GroupModel;

public class ModifyService {

	public static Map<String, ModifyService> ModifyData = new HashMap<String, ModifyService>();
	
	
	private String idx;
	private int gid;
	private List<String> uidCache = new ArrayList<String>();
	private GroupBean groupBean;
	private List<AccountBean> users;
	
	private ModifyService(){}
	
	public static ModifyService createModify(int gid, String idx){
		ModifyService modifyService = new ModifyService();
		
		modifyService.setGid(gid);
		modifyService.setIdx(idx);
		
		
		
		return ModifyData.put(idx, modifyService);
	}
	/**
	 * 开始修改账号服务
	 * @return
	 */
	public boolean startModifyService(){
		GroupModel groupModel = new GroupModel();
		AccountModel accountModel = new AccountModel();
		
		// 如果分组下的用户数不为0，则开始修改账号逻辑
		if(groupModel.getGroupUserCount(gid) == 0){
			PshLogger.logger.error("Group "+gid+" user count is 0");
			return false;
		}
		
		// 修改分组状态，并获取用户列表
		groupBean = groupModel.getGroup(gid);
		users = accountModel.getGroupUserList(gid);
		groupBean.setStatus("修改资料中");
		groupModel.updateGroup(groupBean);
		
		ProxyService proxyService = new ProxyService();
		PshLogger.logger.debug("正在加载代理...");
		proxyService.loadProxyData();
		
		
		// 遍历开始
		for(int i = 0 ; i < users.size() ; i ++){
			AccountBean user = users.get(i);
			LoginService login = LoginService.Login_3G_Sina(user.getEmail(), user.getPassword(), proxyService.getRandomProxyModel());
			
			if(login == null){
				continue;
			}
			
//			JSONObject response = login.executeJSON("http://m.weibo.cn/settingDeal/tagAdd?tag="+URLEncoder.encode("西瓜哥威武"));
//			
//			System.out.println(response);
			
			
		}
		
		
		
		
		return false;
	}

	public static void main(String[] args) {
		
		LoginService login = LoginService.Login_3G_Sina("psh24053@yahoo.cn", "caicai520");
		
		if(login == null){
			return;
		}
				
				
				
//		String response = login.execute("http://m.weibo.cn/settingDeal/tagAdd?tag="+URLEncoder.encode("西瓜哥威武"), false, "http://m.weibo.cn/users/"+login.getUid()+"?");
		
		String response = login.execute("http://m.weibo.cn/settingDeal/inforSave?screen_name=IxgSoft6&province=51&city=3&gender=f&year=1989&month=4&day=2&description=aaaaa2323", false, "http://m.weibo.cn/users/"+login.getUid()+"?vt="+login.getVt()+"&wm="+login.getWm()+"&gsid="+login.getGsid());
		
		System.out.println(response);
		
	}
	
	
	public String getIdx() {
		return idx;
	}
	public void setIdx(String idx) {
		this.idx = idx;
	}
	public int getGid() {
		return gid;
	}
	public void setGid(int gid) {
		this.gid = gid;
	}

	public List<String> getUidCache() {
		return uidCache;
	}
	public void setUidCache(List<String> uidCache) {
		this.uidCache = uidCache;
	}
	
	
	
	
}
