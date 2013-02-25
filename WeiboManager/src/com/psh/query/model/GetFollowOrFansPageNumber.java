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

public class GetFollowOrFansPageNumber {
	
	//获得粉丝或者关注的页数
	public int getFollowOrFansPage(String url){
		
		System.out.println("进入查找用户的页数");
		
		PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
		connectionManager.setMaxTotal(2000);
		connectionManager.setDefaultMaxPerRoute(1000);
		
		HttpClient httpClient_page = new DefaultHttpClient(connectionManager);
		
		//伪装成Firefox 5, 
		httpClient_page.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1); 
		httpClient_page.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY); // 一定要有，否则会生成多个Cookie header送给web server 
		httpClient_page.getParams().setParameter(CookieSpecPNames.SINGLE_COOKIE_HEADER, true); //
		httpClient_page.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,"UTF-8"); //这个是和目标网站的编码有关；
		httpClient_page.getParams().setParameter(CoreProtocolPNames.HTTP_ELEMENT_CHARSET,"UTF-8"); 
		List<BasicHeader> headerList_page = new ArrayList<BasicHeader>(); 
		
		headerList_page.add(new BasicHeader("Accept", "*/*")); 
		headerList_page.add(new BasicHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3"));
		headerList_page.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8"));
		headerList_page.add(new BasicHeader("Cache-Control", "no-cache")); 
		headerList_page.add(new BasicHeader("Connection", "keep-alive"));
		headerList_page.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));
		headerList_page.add(new BasicHeader("X-Requested-With", "XMLHttpRequest"));
		headerList_page.add(new BasicHeader("Cookie", CookieData.CookieNow));
		
		httpClient_page.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(300000)); 
		httpClient_page.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  new Integer(300000) ); 
		httpClient_page.getParams().setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, new Long(300000)); // second;
		//伪装成Firefox
		httpClient_page.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/17.0 Firefox/17.0");
		
		
		headerList_page.add(new BasicHeader("Host", "weibo.com"));
		headerList_page.add(new BasicHeader("Origin", "weibo.com"));
		
			
			headerList_page.add(new BasicHeader("Referer", url));
			httpClient_page.getParams().setParameter(ClientPNames.DEFAULT_HEADERS, headerList_page);
			
			//设置代理对象 ip/代理名称,端口     
//			ProxyManager proxyManager = ProxyManager.getInstance();
//			ProxyBean proxy = new ProxyBean();
//			proxy = proxyManager.getOneProxy();
//			System.out.println("拿到代理IP" + proxy.getIp());
//			httpClient_page.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(proxy.getIp(), proxy.getPort()));
			
			HttpPost httpPost_page = new HttpPost(url);
			
			HttpResponse httpResponsePage = null;
			try {
				httpResponsePage = httpClient_page.execute(httpPost_page);
			} catch (ClientProtocolException e1) {
				PshLogger.logger.error(e1.getMessage());
			} catch (IOException e1) {
				PshLogger.logger.error(e1.getMessage());

				System.out.println("重新执行找关注");
				return getFollowOrFansPage(url);
			}
			
			if(httpResponsePage == null){
				return getFollowOrFansPage(url);
			}
			
			HttpEntity httpEntityPage = httpResponsePage.getEntity();
			
			if(httpEntityPage == null){
				return getFollowOrFansPage(url);
			}
			
			BufferedReader in_page = null;
			try {
				in_page = new BufferedReader(new InputStreamReader(httpEntityPage.getContent(), "UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				PshLogger.logger.error(e1.getMessage());
			} catch (IllegalStateException e1) {
				PshLogger.logger.error(e1.getMessage());
			} catch (IOException e1) {
				PshLogger.logger.error(e1.getMessage());
			}
			
			String result_page = "";
			String item_page = "";
			
			try {
				while((item_page = in_page.readLine()) != null){
					if(item_page.trim().indexOf("W_pages W_pages_comment") != -1){
						System.out.println("找到一行html符合");
						result_page = item_page.trim();
					}
				}
			} catch (IOException e1) {
				PshLogger.logger.error(e1.getMessage());
			}finally{
				try {
					in_page.close();
				} catch (IOException e) {
					PshLogger.logger.error(e.getMessage());
				}
			}
			
			if(result_page.trim().equals("")){
				
				System.out.println("新浪微博提示行为异常follow");
				//换代理IP重新获取该页
				return getFollowOrFansPage(url);
				
			}
			
			System.out.println("已获取到 html_page");
			
			result_page = result_page.substring(result_page.indexOf("<div"),result_page.lastIndexOf("/div>") + 5);
			result_page = result_page.replace('\\','`');
			result_page = result_page.replaceAll("`n", "");
			result_page = result_page.replaceAll("`t", "");
			result_page = result_page.replaceAll("`r", "");
			result_page = result_page.replaceAll("`", "");
			result_page = "<html><body>" + result_page + "</body></html>";
			
			Document doc_page = Jsoup.parse(result_page);
			
			Elements elements_page = doc_page.getElementsByAttributeValue("class", "page S_bg1");
			System.out.println(elements_page.size());
			
			String pageNumber = elements_page.get(elements_page.size() - 1).text();
			System.out.println("##################关注或粉丝有" + pageNumber + "页##############");
			
			return Integer.parseInt(pageNumber);
		
	}
	
	public static void main(String[] args) {
		
		GetFollowOrFansPageNumber gfofpn = new GetFollowOrFansPageNumber();
		System.out.println(gfofpn.getFollowOrFansPage("http://weibo.com/1778742953/follow"));
		
	}

}
