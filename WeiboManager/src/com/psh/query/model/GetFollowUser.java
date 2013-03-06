package com.psh.query.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.cookie.params.CookieSpecPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.psh.base.util.PshLogger;
import com.psh.query.bean.ProxyBean;
import com.psh.query.bean.QueryTaskBean;
import com.psh.query.bean.UserBean;
import com.psh.query.bean.UserQueryTaskBean;
import com.psh.query.util.CheckFit;
import com.psh.query.util.CookieData;
import com.psh.query.util.ProxyManager;
import com.psh.query.util.QueryNumberManager;

public class GetFollowUser {
	
	//根据用户ID找该用户的关注对象
	public Set<String> getFollowUserByUid(long uid,int pageNumber){
		
		System.out.println("进入查找用户的关注用户");
		
		PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
		connectionManager.setMaxTotal(2000);
		connectionManager.setDefaultMaxPerRoute(1000);
		
		HttpClient httpClient_follow = new DefaultHttpClient(connectionManager);
		
		//伪装成Firefox 5, 
		httpClient_follow.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1); 
		httpClient_follow.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY); // 一定要有，否则会生成多个Cookie header送给web server 
		httpClient_follow.getParams().setParameter(CookieSpecPNames.SINGLE_COOKIE_HEADER, true); //
		httpClient_follow.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,"UTF-8"); //这个是和目标网站的编码有关；
		httpClient_follow.getParams().setParameter(CoreProtocolPNames.HTTP_ELEMENT_CHARSET,"UTF-8"); 
		List<BasicHeader> headerList_follow = new ArrayList<BasicHeader>(); 
		
		headerList_follow.add(new BasicHeader("Accept", "*/*")); 
		headerList_follow.add(new BasicHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3"));
		headerList_follow.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8"));
		headerList_follow.add(new BasicHeader("Cache-Control", "no-cache")); 
		headerList_follow.add(new BasicHeader("Connection", "keep-alive"));
		headerList_follow.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));
		headerList_follow.add(new BasicHeader("X-Requested-With", "XMLHttpRequest"));
		headerList_follow.add(new BasicHeader("Cookie", CookieData.CookieNow));
		
		httpClient_follow.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(300000)); 
		httpClient_follow.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  new Integer(300000) ); 
		httpClient_follow.getParams().setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, new Long(300000)); // second;
		//伪装成Firefox
		httpClient_follow.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/17.0 Firefox/17.0");
		
		
		headerList_follow.add(new BasicHeader("Host", "weibo.com"));
		headerList_follow.add(new BasicHeader("Origin", "weibo.com"));
		
		GetFollowOrFansPageNumber page = new GetFollowOrFansPageNumber();
		int number = page.getFollowOrFansPage("http://weibo.com/" + uid + "/follow");
		
		Set<String> uidList = new HashSet<String>();
		
//		QueryNumberManager queryNumber = QueryNumberManager.getInstance();
		
		for(int u = pageNumber ; u < number ; u++){
			
			System.out.println("关注第" + (u + 1) + "页");
			String url_follow = "http://weibo.com/" + uid + "/follow" +"?page=" + (u + 1);
			System.out.println(url_follow);
			
			headerList_follow.add(new BasicHeader("Referer", url_follow));
			httpClient_follow.getParams().setParameter(ClientPNames.DEFAULT_HEADERS, headerList_follow);
			
//			//设置代理对象 ip/代理名称,端口     
//			ProxyManager proxyManager = ProxyManager.getInstance();
//			ProxyBean proxy = new ProxyBean();
//			proxy = proxyManager.getOneProxy();
//			System.out.println("拿到代理IP" + proxy.getIp());
//			httpClient_follow.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(proxy.getIp(), proxy.getPort()));
			
			HttpPost httpPost_follow = new HttpPost(url_follow);
			
			HttpResponse httpResponseFollow = null;
			try {
				httpResponseFollow = httpClient_follow.execute(httpPost_follow);
			} catch (ClientProtocolException e1) {
				PshLogger.logger.error(e1.getMessage());
			} catch (IOException e1) {
				PshLogger.logger.error(e1.getMessage());
				System.out.println("重新执行找关注");
				return getFollowUserByUid(uid,u);
			}
			
			if(httpResponseFollow == null){
				return getFollowUserByUid(uid,u+1);
			}
			
			HttpEntity httpEntityFollow = httpResponseFollow.getEntity();
			
			if(httpEntityFollow == null){
				return getFollowUserByUid(uid,u);
			}
			
			BufferedReader in_follow = null;
			try {
				in_follow = new BufferedReader(new InputStreamReader(httpEntityFollow.getContent(), "UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				PshLogger.logger.error(e1.getMessage());
			} catch (IllegalStateException e1) {
				PshLogger.logger.error(e1.getMessage());
			} catch (IOException e1) {
				PshLogger.logger.error(e1.getMessage());
			}
			
			String result_follow = "";
			String item_follow = "";
			
			try {
				while((item_follow = in_follow.readLine()) != null){
					if(item_follow.trim().indexOf("cnfList") != -1){
						System.out.println("找到一行html符合");
						result_follow = item_follow.trim();
					}
				}
			} catch (IOException e1) {
				PshLogger.logger.error(e1.getMessage());
			}finally{
				try {
					in_follow.close();
				} catch (IOException e) {
					PshLogger.logger.error(e.getMessage());
				}
			}
			
			if(result_follow.trim().equals("")){
				
				System.out.println("新浪微博提示行为异常follow");
				//换代理IP重新获取该页
				return getFollowUserByUid(uid,u);
				
			}
			
			System.out.println("已获取到 html_follow");
			
			try {
				
				result_follow = result_follow.substring(result_follow.indexOf("<div"),result_follow.lastIndexOf("/div>") + 5);
				result_follow = result_follow.replace('\\','`');
				result_follow = result_follow.replaceAll("`n", "");
				result_follow = result_follow.replaceAll("`t", "");
				result_follow = result_follow.replaceAll("`r", "");
				result_follow = result_follow.replaceAll("`", "");
				result_follow = "<html><body>" + result_follow + "</body></html>";
			} catch (Exception e) {
				PshLogger.logger.error(e.getMessage());
				return getFollowUserByUid(uid,u+1);
			}
			
			Document doc_follow = Jsoup.parse(result_follow);
			
			Elements elements_follow = doc_follow.getElementsByAttributeValue("class", "W_f14 S_func1");
			System.out.println(elements_follow.size());
			
			for(int x = 0 ; x < elements_follow.size() ; x ++){
				System.out.println("遍历关注第" + (u + 1) + "页，第" + (x + 1) + "个用户");
				String uidString = elements_follow.get(x).attr("usercard");
				System.out.println("uridString------"+uidString);
				String uid_follow = uidString.substring(3);
				System.out.println(uid_follow);
				
				UserModel userModel = new UserModel();
				
				if(userModel.checkUserIsExsit(uid_follow)){
					continue;
				}
				
				//获取该用户详细信息
				UserBean user = new UserBean();
				GetPersonInfo personInfo = new GetPersonInfo();
				Elements element_followNum = elements_follow.get(x).parent().parent().getElementsByAttributeValue("href", "/" + uid_follow + "/follow");
				Elements element_fansNum = elements_follow.get(x).parent().parent().getElementsByAttributeValue("href", "/" + uid_follow + "/fans");
				String fans = "";
				String follow = "";
				for(int y = 0 ; y < element_followNum.size(); y++){
					follow = element_followNum.get(y).text();
				}
				for(int y = 0 ; y < element_fansNum.size(); y++){
					fans = element_fansNum.get(y).text();
				}
				//将该用户信息加入数据库
				user = personInfo.getUserInfoFromWeibo(uid_follow, fans, follow);
				if(user == null){
					continue;
				}
				System.out.println("用户查找完" + user.getUck());
				
				//进行2级遍历
				uidList.add(uid_follow);
				
			}
		}
		
//		queryNumber.executeFollowAndFans(uidList, queryTaskID,level);
		return uidList;
		
	}

}
