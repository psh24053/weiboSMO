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

import com.psh.base.json.JSONException;
import com.psh.base.json.JSONObject;
import com.psh.base.util.PshLogger;
import com.psh.query.bean.MsgBean;
import com.psh.query.bean.ProxyBean;
import com.psh.query.bean.UserBean;
import com.psh.query.bean.UserQueryTaskBean;
import com.psh.query.util.ProxyManager;

public class TestQueryMsgContentByUidFresh {
	
	public void getQuery(){
		
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
		headerList.add(new BasicHeader("Cookie", "	UOR=www.3lian.com,widget.weibo.com,#login.sina.com.cn; SINAGLOBAL=1322215552923.9622.1360159098496; ULV=1361279381846:13:6:2:5750746400609.567.1361279381842:1361197525113; myuid=2885381611; un=jiexixiwang@sina.com; ALF=1361544517; wvr=5; SinaRot/u/1286098561%3Fwvr%3D5%26wvr%3D5%26lf%3Dreg=88; USRUG=usrmdins1540_111; SUS=SID-1286098561-1361279614-GZ-ab8dr-f69d471576b9c362f84bddfeb24856ca; SUE=es%3D3af95b1ca080b5d61aa865cb6e1e2067%26ev%3Dv1%26es2%3D37646aac58ad13646fdb8de3f0d0a79a%26rs0%3DGzN4CxwtPkM%252BsraXZTz6ZrHcqHmZYU7tUE8sCF8J0PRaBWNGSTrSZocjw1%252Bq1BnPJREk0nNBsUMpkb7QueCr2ToUgH3tKGqRx4cXGDU%252BDAqRc44%252Bhb6tgg8aV8WXiQRby8GykvzgYQcmQWSybiAokHc6TsWufzlJOxng8u3lfjc%253D%26rv%3D0; SUP=cv%3D1%26bt%3D1361279614%26et%3D1361366014%26d%3Dc909%26i%3Dffbe%26us%3D1%26vf%3D0%26vt%3D0%26ac%3D4%26uid%3D1286098561%26user%3Djiexixiwang%26ag%3D2%26name%3Djiexixiwang%2540sina.com%26nick%3Djiexixiwang%26fmp%3D%26lcp%3D2012-06-10%252019%253A13%253A06; SSOLoginState=1361279614; v=5; USRHAWB=usrmdins540_96; _s_tentry=weibo.com; Apache=5750746400609.567.1361279381842; SinaRot/u/1286098561%3Ftopnav%3D1%26wvr%3D5=12"));
		
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(300000)); 
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  new Integer(300000) ); 
		httpClient.getParams().setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, new Long(300000)); // second;
		httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/17.0 Firefox/17.0");
		
		//0级遍历
		for(int j = 0 ; j < 1 ; j++){
			
			
			headerList.add(new BasicHeader("Host", "weibo.com"));
			headerList.add(new BasicHeader("Origin", "weibo.com"));
			headerList.add(new BasicHeader("Referer", "http://weibo.com/u/2363715054"));
			httpClient.getParams().setParameter(ClientPNames.DEFAULT_HEADERS, headerList);
			
			//设置代理对象 ip/代理名称,端口     
//			ProxyManager proxyManager = ProxyManager.getInstance();
//			ProxyBean proxy = new ProxyBean();
//			proxy = proxyManager.getOneProxy();
//			System.out.println("拿到代理IP" + proxy.getIp());
//			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(proxy.getIp(), proxy.getPort()));
			
			HttpPost httpPost = new HttpPost("http://weibo.com/aj/mblog/mbloglist?_wv=5&page=1&count=15&pre_page=1&pagebar=1&_k=136128729948861&uid=2363715054&_t=0");
			
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
			
			String JsonResult = "";
			String item = "";
			
			try {
				while((item = in.readLine()) != null){
					JsonResult += item.trim();
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
			
			if(JsonResult.equals("")){
				
				System.out.println("新浪微博提示行为异常");
				//换代理IP重新获取该页

			}
			
			System.out.println("已获取到 html");
			JSONObject json = null;
			try {
				json = new JSONObject(JsonResult);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String result = "";
			try {
				result = json.getString("data").toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			result = result.substring(result.indexOf("<div"),result.lastIndexOf("/div>") + 5);
			result = result.replace('\\','`');
			result = result.replaceAll("`n", "");
			result = result.replaceAll("`t", "");
			result = result.replaceAll("`r", "");
			result = result.replaceAll("`", "");
			result = "<html><body>" + result + "</body></html>";
			System.out.println(result);
			
			Document doc = Jsoup.parse(result);
			
			Elements elements = doc.getElementsByAttribute("mid");
			System.out.println(elements.size());
			
			List<MsgBean> msgList = new ArrayList<MsgBean>();
			//遍历每页的用户
			for(int i = 0 ; i < elements.size() ; i ++){
				
				MsgBean msg = new MsgBean();
				if(elements.get(i).attr("class").equals("WB_handle")){
					continue;
				}
				msg.setMid(elements.get(i).attr("mid"));
				if(msg.getMid().equals("") || msg.getMid() == null){
					continue;
				}
				System.out.println("mid :" + msg.getMid());
				if(elements.get(i).attr("isforward") == null || elements.get(i).attr("isforward").equals("")){
					
				}else{
					msg.setType("1");
					System.out.println("type :" + msg.getType());
				}
				if(elements.get(i).getElementsByAttributeValue("node-type", "feed_list_content").size() > 0){
					
					msg.setCon(elements.get(i).getElementsByAttributeValue("node-type", "feed_list_content").get(0).text());
				}
				System.out.println("content :" + msg.getCon());
				if(elements.get(i).getElementsByAttributeValue("node-type", "feed_list_media_bgimg").size() > 0){
					
					msg.setImage(elements.get(i).getElementsByAttributeValue("node-type", "feed_list_media_bgimg").get(0).attr("src"));
					System.out.println("Image :" + msg.getImage());
				}
				
				if(elements.get(i).getElementsByAttribute("date").size() > 0){
					
					msg.setTime(elements.get(i).getElementsByAttribute("date").get(0).text());
				}else if(elements.get(i).getElementsByAttributeValue("node-type", "feed_list_item_date").size() > 0){
					msg.setTime(elements.get(i).getElementsByAttributeValue("node-type", "feed_list_item_date").get(0).text());
				}
				System.out.println("time :" + msg.getTime());
				msgList.add(msg);
			}
			
			
		}
		
	}
	
	public static void main(String[] args) {
		TestQueryMsgContentByUidFresh test = new TestQueryMsgContentByUidFresh();
		test.getQuery();
	}
	
}
