package com.psh.query.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
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

public class GetPersonInfo_2 {
	
	
	//获得用户信息
	public UserBean getUserInfoFromWeibo(String uid,String fans,String follow){
		
		UserBean user = new UserBean();
		user.setFans(fans);
		user.setFol(follow);
		user.setUid(uid);
		
		String url = "http://weibo.com/" + uid + "/info";
		
		PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
		connectionManager.setMaxTotal(2000);
		connectionManager.setDefaultMaxPerRoute(1000);
		
		// 访问 http://weibo.com/signup/mobile.php;
		HttpClient httpClient = new DefaultHttpClient(connectionManager);
		
		
		//伪装成Firefox 5, 
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1); 
		httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY); // 一定要有，否则会生成多个Cookie header送给web server 
		httpClient.getParams().setParameter(CookieSpecPNames.SINGLE_COOKIE_HEADER, true); //
		httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,"UTF-8"); //这个是和目标网站的编码有关；
		httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_ELEMENT_CHARSET,"UTF-8"); 
		List<BasicHeader> headerList = new ArrayList<BasicHeader>(); 
		
		headerList.add(new BasicHeader("Accept", "*/*")); 
		headerList.add(new BasicHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3"));
		headerList.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8"));
		headerList.add(new BasicHeader("Cache-Control", "no-cache")); 
		headerList.add(new BasicHeader("Connection", "keep-alive"));
		headerList.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));
		headerList.add(new BasicHeader("X-Requested-With", "XMLHttpRequest"));
		headerList.add(new BasicHeader("Cookie", CookieData.CookieNow));
		
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(300000)); 
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  new Integer(300000) ); 
		httpClient.getParams().setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, new Long(300000)); // second;
		//伪装成Firefox
		httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/17.0 Firefox/17.0");
//		httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost("109.232.32.199", 8080));
		
		headerList.add(new BasicHeader("Host", "weibo.com"));
		headerList.add(new BasicHeader("Origin", "weibo.com"));
		headerList.add(new BasicHeader("Referer", url));
		httpClient.getParams().setParameter(ClientPNames.DEFAULT_HEADERS, headerList);
		//连接超时、sockete超时和从connectionmanager中获取connection的超时设置，计算单位都是微秒；
		
		//设置代理对象 ip/代理名称,端口     
//    httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost("190.90.36.8", 8000));
//		ProxyManager proxyManager = ProxyManager.getInstance();
//		ProxyBean proxy = new ProxyBean();
//		proxy = proxyManager.getOneProxy();
//		System.out.println("拿到代理IP" + proxy.getIp());
//		httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(proxy.getIp(), proxy.getPort()));
		
		HttpPost httpPost = new HttpPost(url);
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (ClientProtocolException e1) {
			System.out.println("访问被拒绝,重新访问");
			user = getUserInfoFromWeibo(uid, fans, follow);
			return user;
			// TODO Auto-generated catch block
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			user = getUserInfoFromWeibo(uid, fans, follow);
			return user;
		}
		
		if(httpResponse == null){
			user = getUserInfoFromWeibo(uid, fans, follow);
			return user;
		}
		
		HttpEntity httpEntity = httpResponse.getEntity();
		
		if(httpEntity == null){
			user = getUserInfoFromWeibo(uid, fans, follow);
			return user;
		}
		
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(httpEntity.getContent(), "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			user = getUserInfoFromWeibo(uid, fans, follow);
			return user;
		}
		
		String result = "";
		String item = "";
		
		try {
			while((item = in.readLine()) != null){
				if(item.trim().indexOf("infoblock") != -1){
					
					
					if(item.trim().indexOf("基本信息") != -1){
						//获取基本信息
						result = item.trim();
						System.out.println("已获取到 html---基本信息");
						result = result.substring(result.indexOf("<div"),result.lastIndexOf("/div>") + 5);
						result = result.replace('\\','`');
						result = result.replaceAll("`n", "");
						result = result.replaceAll("`t", "");
						result = result.replaceAll("`r", "");
						result = result.replaceAll("`", "");
						result = "<html><body>" + result + "</body></html>";
						
						Document doc = Jsoup.parse(result);
						
						Elements elements = doc.getElementsByAttributeValue("class", "pf_item clearfix");
						System.out.println(elements.size());
						
						for(int i = 0 ; i < elements.size() ; i++){
							
							if((elements.get(i).getElementsByAttributeValue("class", "label S_txt2").text()).equals("昵称")){
								
								user.setUck(elements.get(i).getElementsByAttributeValue("class", "con").text());
								
							}else if((elements.get(i).getElementsByAttributeValue("class", "label S_txt2").text()).equals("所在地")){
								
								String address[] = elements.get(i).getElementsByAttributeValue("class", "con").text().split(" ");
								if(address.length > 0){
									
									user.setProv(address[0]);
								}
								if(address.length == 2){
									
									user.setCity(address[1]);
								}
								
							}else if((elements.get(i).getElementsByAttributeValue("class", "label S_txt2").text()).equals("性别")){
								
								user.setSex(elements.get(i).getElementsByAttributeValue("class", "con").text());
								
							}else if((elements.get(i).getElementsByAttributeValue("class", "label S_txt2").text()).equals("简介")){
								
								user.setInfo(elements.get(i).getElementsByAttributeValue("class", "con").text());
								
							}else if((elements.get(i).getElementsByAttributeValue("class", "label S_txt2").text()).equals("感情状况")){
								
								user.setEmo(elements.get(i).getElementsByAttributeValue("class", "con").text());
								
							}else if((elements.get(i).getElementsByAttributeValue("class", "label S_txt2").text()).equals("生日")){
								
								user.setDate(elements.get(i).getElementsByAttributeValue("class", "con").text());
								
							}else if((elements.get(i).getElementsByAttributeValue("class", "label S_txt2").text()).equals("血型")){
								
								user.setBlo(elements.get(i).getElementsByAttributeValue("class", "con").text());
								
							}
							
						}
						
						
					}else if(item.trim().indexOf("标签信息") != -1){
						result = item.trim();
						//获取基本信息
						System.out.println("已获取到 html---标签");
						result = result.substring(result.indexOf("<div"),result.lastIndexOf("/div>") + 5);
						result = result.replace('\\','`');
						result = result.replaceAll("`n", "");
						result = result.replaceAll("`t", "");
						result = result.replaceAll("`r", "");
						result = result.replaceAll("`", "");
						result = "<html><body>" + result + "</body></html>";
						
						Document doc = Jsoup.parse(result);
						
						Elements elements = doc.getElementsByAttributeValue("class", "S_func1");
						System.out.println(elements.size());
						
						String tag = "";
						
						for(int i = 0 ; i < elements.size() ; i++){
							
							tag += elements.get(i).text() + ",";
							
						}
						
						user.setTag(tag);
						
						
					}else if(item.trim().indexOf("工作信息") != -1){
						
						result = item.trim();
						System.out.println("已获取到 html---工作信息");
						result = result.substring(result.indexOf("<div"),result.lastIndexOf("/div>") + 5);
						result = result.replace('\\','`');
						result = result.replaceAll("`n", "");
						result = result.replaceAll("`t", "");
						result = result.replaceAll("`r", "");
						result = result.replaceAll("`", "");
						result = "<html><body>" + result + "</body></html>";
						
						Document doc = Jsoup.parse(result);
						
						Elements elements = doc.getElementsByAttributeValue("class", "pf_item clearfix");
						System.out.println(elements.size());
						
						for(int i = 0 ; i < elements.size() ; i++){
							
							if((elements.get(i).getElementsByAttributeValue("class", "label S_txt2").text()).equals("公司")){
								
								user.setCom(elements.get(i).getElementsByAttributeValue("target", "_blank").text());
								
							}
						}
						
					}else if(item.trim().indexOf("教育信息") != -1){
						
						result = item.trim();
						System.out.println("已获取到 html---教育信息");
						result = result.substring(result.indexOf("<div"),result.lastIndexOf("/div>") + 5);
						result = result.replace('\\','`');
						result = result.replaceAll("`n", "");
						result = result.replaceAll("`t", "");
						result = result.replaceAll("`r", "");
						result = result.replaceAll("`", "");
						result = "<html><body>" + result + "</body></html>";
						
						Document doc = Jsoup.parse(result);
						
						Elements elements = doc.getElementsByAttributeValue("class", "pf_item clearfix");
						System.out.println(elements.size());
						
						for(int i = 0 ; i < elements.size() ; i++){
							
							if((elements.get(i).getElementsByAttributeValue("class", "label S_txt2").text()).equals("大学")){
								
								user.setStu(elements.get(i).getElementsByAttributeValue("target", "_blank").text());
								
							}
						}
						
					}
				}
			}
			
			if(user.getUck() == null){
				
				user =  getUserInfoFromWeibo(uid, fans, follow);
				return user;
				
			}
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			PshLogger.logger.error(e1.getMessage());
			user = getUserInfoFromWeibo(uid, fans, follow);
			return user;
		}finally{
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return user;
		
	}
	
	public static void main(String[] args) {
		GetPersonInfo_2 gpi = new GetPersonInfo_2();
		gpi.getUserInfoFromWeibo("1973632934", "50", "100");
	}

}
