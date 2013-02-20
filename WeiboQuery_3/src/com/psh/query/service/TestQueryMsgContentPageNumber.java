package com.psh.query.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.methods.GetMethod;
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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.psh.base.util.PshLogger;
import com.psh.query.bean.MsgBean;
import com.psh.query.bean.ProxyBean;
import com.psh.query.bean.UserBean;
import com.psh.query.bean.UserQueryTaskBean;
import com.psh.query.util.ProxyManager;

public class TestQueryMsgContentPageNumber {
	
	public void getFirstQueryNumber(){
		
		PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
		connectionManager.setMaxTotal(2000);
		connectionManager.setDefaultMaxPerRoute(1000);
		
		HttpClient httpClient = new DefaultHttpClient(connectionManager);
		
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1); 
		httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY); 
		httpClient.getParams().setParameter(CookieSpecPNames.SINGLE_COOKIE_HEADER, true); 
		httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,"UTF-8"); 
		httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_ELEMENT_CHARSET,"UTF-8"); 
		List<BasicHeader> headerList = new ArrayList<BasicHeader>(); 
		
		headerList.add(new BasicHeader("Accept", "*/*")); 
		headerList.add(new BasicHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3"));
		headerList.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8"));
		headerList.add(new BasicHeader("Cache-Control", "no-cache")); 
		headerList.add(new BasicHeader("Connection", "keep-alive"));
		headerList.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));
		headerList.add(new BasicHeader("X-Requested-With", "XMLHttpRequest"));
		headerList.add(new BasicHeader("Cookie", "UOR=www.3lian.com,widget.weibo.com,#login.sina.com.cn; SINAGLOBAL=1322215552923.9622.1360159098496; ULV=1361362154446:14:7:3:1294072819005.5784.1361362154402:1361279381846; myuid=2885381611; un=jiexixiwang@sina.com; ALF=1361544517; wvr=5; SUS=SID-1286098561-1361362548-GZ-ze43v-472f49a1fb835ed6bc76070d2c0256ca; SUE=es%3D5a6312d5d162db3abe510650bd3138a8%26ev%3Dv1%26es2%3D848eb6385a92684d41088d49a7d6cb2e%26rs0%3DP9PDncgpTD427%252FbOz6HTChgZ1I6B9mvvnC6VSaQKj5PDp6evPjZM4tDA6ehtycw9FDsfQeqA3YQm%252B6ZLZOXJasKuEAxtnmlxTiBmNfb2zvUZc448oZt0IfC4u2psn0I9zA3D%252F3kbnVqPwVyIT9RixrT0TdRk%252FAXVPl2N%252BW1SJUI%253D%26rv%3D0; SUP=cv%3D1%26bt%3D1361362548%26et%3D1361448948%26d%3Dc909%26i%3Dbbc6%26us%3D1%26vf%3D0%26vt%3D0%26ac%3D4%26uid%3D1286098561%26user%3Djiexixiwang%26ag%3D2%26name%3Djiexixiwang%2540sina.com%26nick%3Djiexixiwang%26fmp%3D%26lcp%3D2012-06-10%252019%253A13%253A06; SSOLoginState=1361362548; v=5; _s_tentry=login.sina.com.cn; Apache=1294072819005.5784.1361362154402; USRHAWB=usrmdins21094; WBStore=84b1a0f0478cbbde|"));
		
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(300000)); 
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  new Integer(300000) ); 
		httpClient.getParams().setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, new Long(300000)); // second;
		httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/17.0 Firefox/17.0");
		
		//0级遍历
			headerList.add(new BasicHeader("Host", "s.weibo.com"));
			headerList.add(new BasicHeader("Origin", "s.weibo.com"));
			headerList.add(new BasicHeader("Referer", "	http://s.weibo.com/?topnav=1&wvr=5"));
			httpClient.getParams().setParameter(ClientPNames.DEFAULT_HEADERS, headerList);
			
			//设置代理对象 ip/代理名称,端口     
//			ProxyManager proxyManager = ProxyManager.getInstance();
//			ProxyBean proxy = new ProxyBean();
//			proxy = proxyManager.getOneProxy();
//			System.out.println("拿到代理IP" + proxy.getIp());
//			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(proxy.getIp(), proxy.getPort()));
			
			HttpPost httpPost = new HttpPost("http://s.weibo.com/weibo/%25E5%2593%2588%25E5%2593%2588&Refer=index");
			
			HttpResponse httpResponse = null;
			try {
				httpResponse = httpClient.execute(httpPost);
			} catch (ClientProtocolException e1) {
				PshLogger.logger.error(e1.getMessage());
			} catch (IOException e1) {
				PshLogger.logger.error(e1.getMessage());
			}
			
			HttpEntity httpEntity = httpResponse.getEntity();
			
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(httpEntity.getContent(), "UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				PshLogger.logger.error(e1.getMessage());
			} catch (IllegalStateException e1) {
				PshLogger.logger.error(e1.getMessage());
			} catch (IOException e1) {
				PshLogger.logger.error(e1.getMessage());
			}
			
			String result = "";
			String item = "";
			
			try {
				while((item = in.readLine()) != null){
					if(item.trim().indexOf("search_page clearfix") != -1){
						result = item.trim();
					}
				}
			} catch (IOException e1) {
				PshLogger.logger.error(e1.getMessage());
			}finally{
				try {
					in.close();
				} catch (IOException e) {
					PshLogger.logger.error(e.getMessage());
				}
			}
			
			if(result.equals("")){
				
				System.out.println("新浪微博提示行为异常");
				//换代理IP重新获取该页

			}
			
			System.out.println("已获取到 html");
			result = result.substring(result.indexOf("<div"),result.lastIndexOf("/div>") + 5);
			result = result.replace('\\','`');
			result = result.replaceAll("`n", "");
			result = result.replaceAll("`t", "");
			result = result.replaceAll("`r", "");
			result = result.replaceAll("`", "");
			result = "<html><body>" + result + "</body></html>";
			System.out.println(result);
			
			Document doc = Jsoup.parse(result);
			
			Elements elements = doc.getElementsByAttributeValue("class", "search_page_M");
			System.out.println(elements.size());
			
			if(elements.size() > 0){
				
				System.out.println(elements.get(0).getElementsByTag("li").size());
				Elements elements_1 = elements.get(0).getElementsByTag("li");
				
				System.out.println(elements_1.get(elements_1.size() - 2).text());
				
			}
			
			//遍历每页的用户
	}
	
	public static void main(String[] args) {
		TestQueryMsgContentPageNumber test = new TestQueryMsgContentPageNumber();
		test.getFirstQueryNumber();
	}
	
}
