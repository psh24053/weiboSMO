package com.psh.query.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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

public class GetFansUser {
	
	//根据用户ID找该用户的粉丝对象
	public void getFansUserByUid(String uid,int pageNumber,int queryTaskID){
		
		System.out.println("进入查找用户的粉丝用户");
		
		
		PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
		connectionManager.setMaxTotal(2000);
		connectionManager.setDefaultMaxPerRoute(1000);
		
		HttpClient httpClient_fans = new DefaultHttpClient(connectionManager);
		
		//伪装成Firefox 5, 
		httpClient_fans.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1); 
		httpClient_fans.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY); // 一定要有，否则会生成多个Cookie header送给web server 
		httpClient_fans.getParams().setParameter(CookieSpecPNames.SINGLE_COOKIE_HEADER, true); //
		httpClient_fans.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,"UTF-8"); //这个是和目标网站的编码有关；
		httpClient_fans.getParams().setParameter(CoreProtocolPNames.HTTP_ELEMENT_CHARSET,"UTF-8"); 
		List<BasicHeader> headerList_fans = new ArrayList<BasicHeader>(); 
		
		headerList_fans.add(new BasicHeader("Accept", "*/*")); 
		headerList_fans.add(new BasicHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3"));
		headerList_fans.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8"));
		headerList_fans.add(new BasicHeader("Cache-Control", "no-cache")); 
		headerList_fans.add(new BasicHeader("Connection", "keep-alive"));
		headerList_fans.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));
		headerList_fans.add(new BasicHeader("X-Requested-With", "XMLHttpRequest"));
		headerList_fans.add(new BasicHeader("Cookie", CookieData.CookieNow));
		
		httpClient_fans.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(300000)); 
		httpClient_fans.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  new Integer(300000) ); 
		httpClient_fans.getParams().setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, new Long(300000)); // second;
		//伪装成Firefox
		httpClient_fans.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/17.0 Firefox/17.0");
		
		
		headerList_fans.add(new BasicHeader("Host", "weibo.com"));
		headerList_fans.add(new BasicHeader("Origin", "weibo.com"));
		
		GetFollowOrFansPageNumber page = new GetFollowOrFansPageNumber();
		int number = page.getFollowOrFansPage("http://weibo.com/" + uid + "/fans");
		
		for(int u = pageNumber ; u < number ; u++){
			System.out.println("粉丝第" + (u + 1) + "页");
			String url_fans = "http://weibo.com/" + uid + "/fans" +"?page=" + (u + 1);
			System.out.println(url_fans);
			
			headerList_fans.add(new BasicHeader("Referer", url_fans));
			httpClient_fans.getParams().setParameter(ClientPNames.DEFAULT_HEADERS, headerList_fans);
			
			//设置代理对象 ip/代理名称,端口     
//			ProxyManager proxyManager = ProxyManager.getInstance();
//			ProxyBean proxy = new ProxyBean();
//			proxy = proxyManager.getOneProxy();
//			System.out.println("拿到代理IP" + proxy.getIp());
//			httpClient_fans.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(proxy.getIp(), proxy.getPort()));
			
			HttpPost httpPost_fans = new HttpPost(url_fans);
			
			HttpResponse httpResponseFans = null;
			try {
				httpResponseFans = httpClient_fans.execute(httpPost_fans);
			} catch (ClientProtocolException e1) {
				PshLogger.logger.error(e1.getMessage());
			} catch (IOException e1) {
				PshLogger.logger.error(e1.getMessage());
				System.out.println("重新执行找关注");
				getFansUserByUid(uid,u,queryTaskID);
			}
			
			HttpEntity httpEntityFans = httpResponseFans.getEntity();
			
			BufferedReader in_fans= null;
			try {
				in_fans = new BufferedReader(new InputStreamReader(httpEntityFans.getContent(), "UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				PshLogger.logger.error(e1.getMessage());
			} catch (IllegalStateException e1) {
				PshLogger.logger.error(e1.getMessage());
			} catch (IOException e1) {
				PshLogger.logger.error(e1.getMessage());
			}
			
			String result_fans = "";
			String item_fans = "";
			
			try {
				while((item_fans = in_fans.readLine()) != null){
					if(item_fans.trim().indexOf("cnfList") != -1){
						System.out.println("找到一行html符合");
						result_fans = item_fans.trim();
					}
				}
			} catch (IOException e1) {
				PshLogger.logger.error(e1.getMessage());
			}finally{
				try {
					in_fans.close();
				} catch (IOException e) {
					PshLogger.logger.error(e.getMessage());
				}
			}
			
			if(result_fans.trim().equals("")){
				
				System.out.println("新浪微博提示行为异常follow");
				//换代理IP重新获取该页
				getFansUserByUid(uid,u,queryTaskID);
				
			}
			
			System.out.println("已获取到 html_fans");
			
			result_fans = result_fans.substring(result_fans.indexOf("<div"),result_fans.lastIndexOf("/div>") + 5);
			result_fans = result_fans.replace('\\','`');
			result_fans = result_fans.replaceAll("`n", "");
			result_fans = result_fans.replaceAll("`t", "");
			result_fans = result_fans.replaceAll("`r", "");
			result_fans = result_fans.replaceAll("`", "");
			result_fans = "<html><body>" + result_fans + "</body></html>";
			
			Document doc_fans = Jsoup.parse(result_fans);
			
			Elements elements_fans = doc_fans.getElementsByAttributeValue("class", "W_f14 S_func1");
			System.out.println(elements_fans.size());
			
			for(int x = 0 ; x < elements_fans.size() ; x ++){
				System.out.println("遍历粉丝第" + (u + 1) + "页，第" + (x + 1) + "个用户");
				String uidString = elements_fans.get(x).attr("usercard");
				System.out.println("uridString------"+uidString);
				String uid_fans = uidString.substring(3);
				System.out.println(uid_fans);
				
				//获取该用户详细信息
				UserBean user = new UserBean();
				GetPersonInfo personInfo = new GetPersonInfo();
				Elements element_followNum = elements_fans.get(x).parent().parent().getElementsByAttributeValue("href", "/" + uid_fans + "/follow");
				Elements element_fansNum = elements_fans.get(x).parent().parent().getElementsByAttributeValue("href", "/" + uid_fans + "/fans");
				String fans = "";
				String follow = "";
				for(int y = 0 ; y < element_followNum.size(); y++){
					follow = element_followNum.get(y).text();
				}
				for(int y = 0 ; y < element_fansNum.size(); y++){
					fans = element_fansNum.get(y).text();
				}
				//将该用户信息加入数据库
				user = personInfo.getUserInfoFromWeibo(uid_fans, fans, follow,queryTaskID);
				System.out.println("用户查找完" + user.getUck());
				
				
				//进行2级遍历
				QueryNumberManager queryNumber = QueryNumberManager.getInstance();
				queryNumber.executeFollowAndFans(uid_fans, queryTaskID);
				
			}
		}
		
	}

}
