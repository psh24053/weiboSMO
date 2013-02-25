package com.psh.query.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.cookie.params.CookieSpecPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.psh.base.json.JSONException;
import com.psh.base.json.JSONObject;
import com.psh.base.util.PshLogger;
import com.psh.query.bean.ProxyBean;
import com.psh.query.bean.UserBean;
import com.psh.query.bean.UserQueryTaskBean;
import com.psh.query.util.CookieData;
import com.psh.query.util.HtmlTools;
import com.psh.query.util.PinCode;
import com.psh.query.util.ProxyManager;
import com.psh.query.util.QueryResultAnalysis;
import com.psh.query.util.TotalPageNumber;

public class GetFirstQueryUser extends Thread{
	
	public String url = "";
	public int pageNumber = -1;
//	public int queryTaskID = -1;
	
	public GetFirstQueryUser(String url,int pageNumber){
		
		this.url = url;
		this.pageNumber = pageNumber;
//		this.queryTaskID = queryTaskID;
		
	}
	
	public void run(){
		
		int status = runFirstQuery();
		if(status == -1){
			
			runFirstQuery();
			
		}
		
	}
	/**
	 * 手动设置cookie
	 * @param httpClient
	 * @param httpResponse 
	 **/
	public void setCookie(HttpClient httpClient, HttpResponse httpResponse){
		Header[] headers = httpResponse.getAllHeaders();
		
		for(int i = 0 ; i < headers.length ; i ++){
			String name = headers[i].getName();
			String value = headers[i].getValue();
			
			System.out.println("name: "+name+" ,value: "+value);
			
			if(name.equals("Set-Cookie")){
				CookieStore cookieStore = ((DefaultHttpClient)httpClient).getCookieStore();
				
				String[] cookies = value.split(";");
				
				BasicClientCookie cookieClient = null;
				
				for(int j = 0 ; j < cookies.length ; j ++){
					String[] cookie = cookies[j].trim().split("=");
					if(j == 0){
						cookieClient = new BasicClientCookie(cookie[0], cookie[1]);
					}
					
					if(cookie[0].equals("expires")){
						cookieClient.setExpiryDate(new Date(cookie[1]));
					}
					if(cookie[0].equals("path")){
						cookieClient.setPath(cookie[1]);
					}
					if(cookie[0].equals("domain")){
						cookieClient.setDomain(cookie[1]);
					}
					
				}
				if(cookieClient != null){
					cookieStore.addCookie(cookieClient);
					((DefaultHttpClient)httpClient).setCookieStore(cookieStore);
				}
			}
			
		}
	}
	
	
	
	public int runFirstQuery(){
		
		if(LoginService.login == null){
			LoginService.login = LoginService.Login_3G_Sina("287540517@qq.com", "penglang7456");
		}
		
		if(LoginService.login == null){
			return -1;
		}
		
			
		JSONObject json = LoginService.login.executeJSON(url+pageNumber);
		
		if(json == null){
			return -1;
		}
			
			List<UserBean> userList = new ArrayList<UserBean>();
			
			QueryResultAnalysis analysis = new QueryResultAnalysis();
			userList = analysis.getUserList(json);
			System.out.println("first----------" + json);
			
			if(userList == null){
				
				return -1;
				
			}
			
			System.out.println(userList.size());
			for(int t = 0 ; t < userList.size() ; t++){
				
				System.out.println("uid" + userList.get(t).getUid() + ",t=" + t);
				UserBean user = new UserBean();
				
				user = userList.get(t);
				
				//获得个人信息
				UserBean user_info = new UserBean();
				GetPersonInfo_2 person_2 = new GetPersonInfo_2();
				user_info = person_2.getUserInfoFromWeibo(user.getUid(), user.getFans(), user.getFans());
				
				if(user_info == null){
					continue;
				}
				
				user_info.setUid(user.getUid());
				user_info.setFans(user.getFans());
				user_info.setFol(user.getFol());
				
				//将用户信息添加到数据库
				if(user_info.getProv().equals("四川")){
					
					UserModel userModel = new UserModel();
					if(userModel.checkUserIsExsit(user_info.getUid())){
						System.out.println("更新用户");
						//用户存在更新数据库
						userModel.updateUser(user_info);
						
					}else{
						System.out.println("添加用户");
						//用户不存在,添加到数据库
						userModel.addUser(user_info);
						
					}
					
					// 将该用户和搜索任务联系起来
//					UserQueryTaskModel userQuery = new UserQueryTaskModel();
//					UserQueryTaskBean userQueryBean = new UserQueryTaskBean();
//					userQueryBean.setUid(user_info.getUid());
//					userQueryBean.setQtid(queryTaskID);
//					userQuery.addUserQueryTask(userQueryBean);
				}
				
				ThreadContraModel threadContra = new ThreadContraModel(user_info.getUid());
				threadContra.start();
				
			}
				
				
			return 0;
			
	}
	
	public static void main(String[] args) {
		
		for(int i = 0;i <50 ;i++){
			
			GetFirstQueryUser first = new GetFirstQueryUser("http://m.weibo.cn/searchs/user?q=%E5%9B%9B%E5%B7%9D%E6%88%90%E9%83%BD&page=", i+1);
			first.runFirstQuery();
			
		}
		
	}
	
}
