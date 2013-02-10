package com.psh.query.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.psh.base.json.JSONException;
import com.psh.base.json.JSONObject;
import com.psh.query.bean.ProxyBean;
import com.psh.query.util.HtmlTools;
import com.psh.query.util.PinCode;

public class LoginService {

	private HttpClient httpClient;
	private HttpResponse httpResponse;
	private String sid;
	private String gsid;
	private String cookie_gsid;
	private String uid;
	private String st;
	private String wm;
	private String vt;
	public static LoginService login;
	
	private LoginService(){}
	
	/**
	 * 登陆新浪微博，返回LoginService实例，失败将返回null
	 * @param wb_username 微博账号
	 * @param wb_password 微博密码
	 * @return LoginService 
	 */
	public static LoginService Login_3G_Sina(String wb_username, String wb_password){
		
		return Login_3G_Sina(wb_username, wb_password, null);
	}
	/**
	 * 登陆新浪微博，返回LoginService实例，失败将返回null，使用proxy连接
	 * @param wb_username 微博账号
	 * @param wb_password 微博密码
	 * @param proxy http代理对象
	 * @return LoginService 
	 */
	public static LoginService Login_3G_Sina(String wb_username, String wb_password, ProxyBean proxy){
		if(wb_username == null || wb_password == null){
			System.out.println("账号密码不能为null");
			return null;
		}
		LoginService loginService = new LoginService();
		
		
		// httpClient 连接池
		PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
		connectionManager.setMaxTotal(2000);
		connectionManager.setDefaultMaxPerRoute(1000);
		
		
		// httpClient
		loginService.httpClient = new DefaultHttpClient(connectionManager);
		
		//伪装成iPhone4 Safari, 
		loginService.httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1); 
		loginService.httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY); //
		loginService.httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,"UTF-8"); //这个是和目标网站的编码有关；
		loginService.httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_ELEMENT_CHARSET,"UTF-8"); 
		loginService.httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(30000)); 
		loginService.httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  new Integer(30000) ); 
		loginService.httpClient.getParams().setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, new Long(30000)); // second;
		loginService.httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_0 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7");
		List<BasicHeader> headerList = new ArrayList<BasicHeader>(); 
		headerList.add(new BasicHeader("Accept", "*/*")); 
		headerList.add(new BasicHeader("Connection", "keep-alive"));
		
		if(proxy != null){
			loginService.httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(proxy.getIp(), proxy.getPort()));
		}
		
		loginService.httpClient.getParams().setParameter(ClientPNames.DEFAULT_HEADERS, headerList);
		loginService.httpClient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
		
		
		// 起始连接
		HttpGet httpGet = new HttpGet("http://3g.sina.com.cn/prog/wapsite/sso/login.php");
		loginService.httpResponse = null;
		
		
		// 执行连接
		try {
			loginService.httpResponse = loginService.httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		if(loginService.httpResponse == null){
			System.out.println("httpResponse is NULL");
			return null;
		}
		
		String loginHtml = null;
		try {
			loginHtml = HtmlTools.getHtmlByBr(loginService.httpResponse.getEntity());
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		if(loginHtml == null){
			System.out.println("Login Html is null");
			return null;
		}
		
		
		Document doc = Jsoup.parse(loginHtml);
		
		Elements forms = doc.select("form");
		if(forms.size() == 0){
			System.out.println("Form load error");
			return null;
		}
		
		Element form = forms.get(0);
		
		// form的action连接
		String actionUrl = form.attr("action");
		
		List<NameValuePair> formPairs = new ArrayList<NameValuePair>();
		
		
		
		// 从form中取出登陆所需表单
		Elements inputs = form.select("input");
		
		for(int i = 0 ; i < inputs.size() ; i ++){
			Element item = inputs.get(i);
			
			String type = item.attr("type");
			String name = item.attr("name");
			String value = item.val();
			
			if(type.equals("hidden") || type.equals("submit")){
				formPairs.add(new BasicNameValuePair(name, value));
			}
			
			if(type.equals("password")){
				formPairs.add(new BasicNameValuePair(name, wb_password));
			}
			
			if(name.equals("mobile")){
				formPairs.add(new BasicNameValuePair(name, wb_username));
			}
			
		}
		
		// 发送登陆表单
		HttpPost httpPost = new HttpPost("http://3g.sina.com.cn/prog/wapsite/sso/"+actionUrl);
		httpPost.addHeader("Referer","http://3g.sina.com.cn/prog/wapsite/sso/login.php");
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(formPairs,"utf-8"));
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		// 执行连接
		try {
			loginService.httpResponse = loginService.httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		if(loginService.httpResponse == null){
			System.out.println("httpResponse is null");
			return null;
		}
		
		// 获取发送表单后，第一次跳转的Location
		Header location = loginService.httpResponse.getFirstHeader("Location");
		
		if(location == null){
			// 第一次跳转的location为null，有两种可能，请求失败或者跳转到另一个登陆页继续登陆
			String locationhtml = null;
			try {
				locationhtml = HtmlTools.getHtmlByBr(loginService.httpResponse.getEntity());
			} catch (UnsupportedEncodingException e1) {
				System.out.println(e1.getMessage());
				return null;
			} catch (IllegalStateException e1) {
				System.out.println(e1.getMessage());
				return null;
			} catch (IOException e1) {
				System.out.println(e1.getMessage());
				return null;
			}
			if(locationhtml == null){
				System.out.println("location is null");
				return null;
			}
			
			
			return reLogin(loginService, locationhtml, wb_username, wb_password);
		}
		
		
		// 准备跳转至第一个Location
		String login_succUrl = location.getValue();
		
		httpGet = new HttpGet(login_succUrl);
		httpGet.addHeader("Referer", "http://3g.sina.com.cn/prog/wapsite/sso/login.php");
		
		
		try {
			loginService.httpResponse = loginService.httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		if(loginService.httpResponse == null){
			return null;
		}
		
		String sina_succHtml = null;
		try {
			sina_succHtml = HtmlTools.getHtmlByBr(loginService.httpResponse.getEntity());
		} catch (UnsupportedEncodingException e1) {
			System.out.println(e1.getMessage());
			return null;
		} catch (IllegalStateException e1) {
			System.out.println(e1.getMessage());
			return null;
		} catch (IOException e1) {
			System.out.println(e1.getMessage());
			return null;
		}
		
		
		doc = Jsoup.parse(sina_succHtml);
		Elements a = doc.select("a");
		
		// 判断a标签的数量
		if(a.size() == 0 || a.size() > 1){
			return null;
		}
		
		// 准备进行第二次跳转
		String hrefUrl = a.get(0).attr("href");
		
		httpGet = new HttpGet(hrefUrl);
		
		try {
			loginService.httpResponse = loginService.httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		if(loginService.httpResponse == null){
			return null;
		}
		
		// 获取三大ID
		String sinaMainHtml = null;
		try {
			sinaMainHtml = HtmlTools.getHtmlByBr(loginService.httpResponse.getEntity());
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		if(sinaMainHtml == null){
			return null;
		}
		
		doc = Jsoup.parse(sinaMainHtml);
		
		Elements scripts = doc.select("script");
		
		if(scripts.size() == 0){
			return null;
		}
		
		String scriptContent = scripts.get(0).html();
		
		if(!scriptContent.contains("var gsid")){
			return null;
		}
		String[] vars = scriptContent.split("\n");
		
		for(int i = 0 ; i < vars.length ; i ++){
			String var = vars[i];
			
			if(loginService.sid == null && var.contains("sid")){
				loginService.sid = var.substring(var.indexOf("'")+1, var.lastIndexOf("'"));
				continue;
			}
			
			if(loginService.gsid == null && var.contains("gsid")){
				loginService.gsid = var.substring(var.indexOf("'")+1, var.lastIndexOf("'"));
				continue;
			}
			
			if(loginService.uid == null && var.contains("uid")){
				loginService.uid = var.substring(var.indexOf("'")+1, var.lastIndexOf("'"));
				continue;
			}
			
		}
		
		// 验证三大ID 是否获取成功
		if(loginService.sid == null || loginService.gsid == null || loginService.uid == null){
			return null;
		}
		
		
		String weiboUrl = doc.select("#wb_cnt").attr("href");
		
		Map<String, String> weiboUrlParamster = HtmlTools.parseURLParamster(weiboUrl);
		
		loginService.vt = weiboUrlParamster.get("vt");
		
		httpGet = new HttpGet(weiboUrl);
		httpGet.addHeader("Referer", hrefUrl);
		
		try {
			loginService.httpResponse = loginService.httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		if(loginService.httpResponse == null){
			return null;
		}else{
			location = loginService.httpResponse.getFirstHeader("Location");
			if(location != null){
				System.out.println(location);
			}
			
//			System.out.println(HtmlTools.getHtmlByBr(loginService.httpResponse));
			
			
		}
		
		
		httpGet = new HttpGet("http://m.weibo.cn/scriptConfig");
		
		try {
			loginService.httpResponse = loginService.httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		if(loginService.httpResponse == null){
			return null;
		}
		
		String scriptConfig = HtmlTools.getHtmlByBr(loginService.httpResponse);
		
		if(scriptConfig == null){
			return null;
		}
		
		
		String JSVar = scriptConfig.substring(0, scriptConfig.indexOf("userInfo"));
		String[] JSVarsplit = JSVar.split(",");
		
		for(int i = 0 ; i < JSVarsplit.length ; i ++){
			String[] items = JSVarsplit[i].trim().split("=");
			if(items.length != 2){
				continue;
			}
			
			String key = items[0].replace("var", "").trim();
			String value = items[1].trim().replace("'", "");
			
			if(key.equals("gsid")){
				loginService.gsid = value;
			}
			if(key.equals("st")){
				loginService.st = value;
			}
			if(key.equals("WM")){
				loginService.wm = value;
			}
			
			
		}
		
		
		
//		
//		// 设置cookie
//		DefaultHttpClient h = (DefaultHttpClient) loginService.getHttpClient();
//		
//		CookieStore store = h.getCookieStore();
//		
//		BasicClientCookie gsid_CTandWM = new BasicClientCookie("gsid_CTandWM",loginService.cookie_gsid == null ? loginService.gsid : loginService.cookie_gsid);
//		gsid_CTandWM.setExpiryDate(new Date(System.currentTimeMillis() + 2592000000l));
//		gsid_CTandWM.setPath("/");
//		gsid_CTandWM.setDomain(".weibo.cn");
//		gsid_CTandWM.setSecure(false);
//		
//		BasicClientCookie USER_LAST_LOGIN_NAME = new BasicClientCookie("USER_LAST_LOGIN_NAME", wb_username);
//		USER_LAST_LOGIN_NAME.setExpiryDate(new Date(System.currentTimeMillis() + 1800000l));
//		USER_LAST_LOGIN_NAME.setPath("/");
//		USER_LAST_LOGIN_NAME.setDomain(".weibo.cn");
//		USER_LAST_LOGIN_NAME.setSecure(false);
//		
//		BasicClientCookie login = new BasicClientCookie("login", "true");
//		login.setExpiryDate(new Date(System.currentTimeMillis() + 2592000000l));
//		login.setPath("/");
//		login.setDomain(".m.weibo.cn");
//		
//		store.addCookie(gsid_CTandWM);
//		store.addCookie(USER_LAST_LOGIN_NAME);
//		store.addCookie(login);
		
		return loginService;
	}
	

	
	/**
	 * 用于继续登陆的情况
	 * @param loginService
	 * @param html
	 * @return
	 */
	private static LoginService reLogin(LoginService loginService, String html, String wb_username, String wb_password){
		if(html.length() < 3500){
			return null;
		}
		
		
		Document doc = Jsoup.parse(html);
		
		String title = doc.select("title").text();
		
		if(title == null){
			return null;
		}
		// 代表不是继续登陆的界面
		if(!title.equals("手机新浪网-登录")){
			return null;
		}
		
		Elements forms = doc.select("form");
		if(forms.size() == 0){
			System.out.println("Form load error");
			return null;
		}
		
		Element form = forms.get(0);
		
		// form的action连接
		String actionUrl = form.attr("action");
		
		List<NameValuePair> formPairs = new ArrayList<NameValuePair>();
		
		
		// 从form中取出登陆所需表单
		Elements inputs = form.select("input");
		
		for(int i = 0 ; i < inputs.size() ; i ++){
			Element item = inputs.get(i);
			
			String type = item.attr("type");
			String name = item.attr("name");
			String value = item.val();
			
			if(type.equals("hidden") || type.equals("submit")){
				formPairs.add(new BasicNameValuePair(name, value));
			}
			
			if(type.equals("password")){
				formPairs.add(new BasicNameValuePair(name, wb_password));
			}
			
			if(name.equals("mobile")){
				formPairs.add(new BasicNameValuePair(name, wb_username));
			}
			
			if(name.equals("code")){
				//代表这是一个需求验证码的请求
				Elements imgs = form.select("img");
				String codeImgUrl = null;
				String capId = form.select("input[name=capId]").val();
				// 获取验证码url
				for(int j = 0 ; j < imgs.size() ; j ++){
					Element img = imgs.get(j);
					if(img.hasClass("v_inline")){
						codeImgUrl = img.attr("src");
						break;
					}
				}
				// 创建验证码类
				PinCode pincode = new PinCode(codeImgUrl);
				
				// 如果验证码拉取失败，则返回
			 	String codeValue = pincode.getCode(loginService.httpClient, capId, codeImgUrl);
				
				if(codeValue == null){
					return null;
				}
				
				formPairs.add(new BasicNameValuePair("code", codeValue));
			}
			
			
		}
		
		// 发送登陆表单
		HttpPost httpPost = new HttpPost("http://3g.sina.com.cn/prog/wapsite/sso/"+actionUrl);
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(formPairs,"utf-8"));
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		try {
			loginService.httpResponse = loginService.httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		
		if(loginService.httpResponse == null){
			System.out.println("httpResponse is null");
			return null;
		}
		
		Header location = loginService.httpResponse.getFirstHeader("Location");
		
		if(location == null){
			System.out.println("location is null");
			return null;
		}
		
		HttpGet httpGet = new HttpGet(location.getValue());
		try {
			loginService.httpResponse = loginService.httpClient.execute(httpGet);
		} catch (ClientProtocolException e1) {
			System.out.println(e1.getMessage());
			return null;
		} catch (IOException e1) {
			System.out.println(e1.getMessage());
			return null;
		}
		
		
		if(loginService.httpResponse == null){
			return null;
		}
		
		
		String sina_succHtml = null;
		try {
			sina_succHtml = HtmlTools.getHtmlByBr(loginService.httpResponse.getEntity());
		} catch (UnsupportedEncodingException e1) {
			System.out.println(e1.getMessage());
			return null;
		} catch (IllegalStateException e1) {
			System.out.println(e1.getMessage());
			return null;
		} catch (IOException e1) {
			System.out.println(e1.getMessage());
			return null;
		}
		
		
		doc = Jsoup.parse(sina_succHtml);
		Elements a = doc.select("a");
		
		// 判断a标签的数量
		if(a.size() == 0 || a.size() > 1){
			return null;
		}
		
		// 准备进行第二次跳转
		String hrefUrl = a.get(0).attr("href");
		
		httpGet = new HttpGet(hrefUrl);
		
		try {
			loginService.httpResponse = loginService.httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		if(loginService.httpResponse == null){
			return null;
		}
		
		// 获取三大ID
		String sinaMainHtml = null;
		try {
			sinaMainHtml = HtmlTools.getHtmlByBr(loginService.httpResponse.getEntity());
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		if(sinaMainHtml == null){
			return null;
		}
		
		doc = Jsoup.parse(sinaMainHtml);
		
		Elements scripts = doc.select("script");
		
		if(scripts.size() == 0){
			return null;
		}
		
		String scriptContent = scripts.get(0).html();
		
		if(!scriptContent.contains("var gsid")){
			return null;
		}
		String[] vars = scriptContent.split("\n");
		
		for(int i = 0 ; i < vars.length ; i ++){
			String var = vars[i];
			
			if(loginService.sid == null && var.contains("sid")){
				loginService.sid = var.substring(var.indexOf("'")+1, var.lastIndexOf("'"));
				continue;
			}
			
			if(loginService.gsid == null && var.contains("gsid")){
				loginService.gsid = var.substring(var.indexOf("'")+1, var.lastIndexOf("'"));
				continue;
			}
			
			if(loginService.uid == null && var.contains("uid")){
				loginService.uid = var.substring(var.indexOf("'")+1, var.lastIndexOf("'"));
				continue;
			}
			
		}
		
		// 验证三大ID 是否获取成功
		if(loginService.sid == null || loginService.gsid == null || loginService.uid == null){
			return null;
		}
		
		String weiboUrl = doc.select("#wb_cnt").attr("href");
		Map<String, String> weiboUrlParamster = HtmlTools.parseURLParamster(weiboUrl);
		
		loginService.vt = weiboUrlParamster.get("vt");
		
		httpGet = new HttpGet(weiboUrl);
		httpGet.addHeader("Referer", hrefUrl);
		
		try {
			loginService.httpResponse = loginService.httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		if(loginService.httpResponse == null){
			return null;
		}else{
			location = loginService.httpResponse.getFirstHeader("Location");
			if(location != null){
				System.out.println(location);
			}
			
			System.out.println(HtmlTools.getHtmlByBr(loginService.httpResponse));
			
			
		}
		
		
		httpGet = new HttpGet("http://m.weibo.cn/scriptConfig");
		
		try {
			loginService.httpResponse = loginService.httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		if(loginService.httpResponse == null){
			return null;
		}
		
		String scriptConfig = HtmlTools.getHtmlByBr(loginService.httpResponse);
		
		if(scriptConfig == null){
			return null;
		}
		
		String JSVar = scriptConfig.substring(0, scriptConfig.indexOf("userInfo"));
		String[] JSVarsplit = JSVar.split(",");
		
		for(int i = 0 ; i < JSVarsplit.length ; i ++){
			String[] items = JSVarsplit[i].trim().split("=");
			if(items.length != 2){
				continue;
			}
			
			String key = items[0].replace("var", "").trim();
			String value = items[1].trim().replace("'", "");
			
			if(key.equals("gsid")){
				loginService.gsid = value;
			}
			if(key.equals("st")){
				loginService.st = value;
			}
			if(key.equals("WM")){
				loginService.wm = value;
			}
			
		}
		
		
//		// 设置cookie
//		DefaultHttpClient h = (DefaultHttpClient) loginService.getHttpClient();
//		
//		CookieStore store = h.getCookieStore();
//		
//		BasicClientCookie gsid_CTandWM = new BasicClientCookie("gsid_CTandWM",loginService.cookie_gsid == null ? loginService.gsid : loginService.cookie_gsid);
//		gsid_CTandWM.setExpiryDate(new Date(System.currentTimeMillis() + 2592000000l));
//		gsid_CTandWM.setPath("/");
//		gsid_CTandWM.setDomain(".weibo.cn");
//		gsid_CTandWM.setSecure(false);
//		
//		BasicClientCookie USER_LAST_LOGIN_NAME = new BasicClientCookie("USER_LAST_LOGIN_NAME", wb_username);
//		USER_LAST_LOGIN_NAME.setExpiryDate(new Date(System.currentTimeMillis() + 1800000l));
//		USER_LAST_LOGIN_NAME.setPath("/");
//		USER_LAST_LOGIN_NAME.setDomain(".weibo.cn");
//		USER_LAST_LOGIN_NAME.setSecure(false);
//		
//		BasicClientCookie login = new BasicClientCookie("login", "true");
//		login.setExpiryDate(new Date(System.currentTimeMillis() + 2592000000l));
//		login.setPath("/");
//		login.setDomain(".m.weibo.cn");
//		
//		store.addCookie(gsid_CTandWM);
//		store.addCookie(USER_LAST_LOGIN_NAME);
//		store.addCookie(login);
		
		return loginService;
	}
	
	
	/**
	 * 
	 * 执行请求，返回String，失败将返回null
	 * url请不要包含gsid以及时间戳
	 * @param url 请求地址
	 * @return String 响应内容
	 */
	public String execute(String url, boolean isgsid, String referer){
		
		String requestURL = url;
		// 加入时间戳和gsid
		if(isgsid){
			if(!requestURL.contains("&gsid=")){
				requestURL += "&gsid="+gsid;
			}
			if(!requestURL.contains("&_=")){
				requestURL += "&_="+System.currentTimeMillis();
			}
			
		}else{
			
			if(!requestURL.contains("&st")){
				requestURL += "&st="+st+"&";
			}
		}
		
		// 执行请求
		HttpGet httpGet = new HttpGet(requestURL);
		if(referer != null){
			httpGet.addHeader("Referer", referer);
		}
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		if(httpResponse == null){
			return null;
		}
		
		try {
			return HtmlTools.getHtmlByBr(httpResponse.getEntity());
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
	} 
	
	/**
	 * 
	 * 执行请求，返回String，失败将返回null，使用proxy连接
	 * url请不要包含gsid以及时间戳
	 * @param url 请求地址
	 * @param proxy http代理对象
	 * @return String 响应内容
	 */
	public String execute(String url, ProxyBean proxy, boolean isgsid, String referer){
		String requestURL = url;
		// 加入时间戳和gsid
		if(isgsid){
			if(!requestURL.contains("&gsid=")){
				requestURL += "&gsid="+gsid;
			}
			if(!requestURL.contains("&_=")){
				requestURL += "&_="+System.currentTimeMillis();
			}
			
		}else{
			
			if(!requestURL.contains("&st")){
				requestURL += "&st="+st+"&";
			}
		}
		
		
		// 执行请求
		HttpGet httpGet = new HttpGet(requestURL);
		if(referer != null){
			httpGet.addHeader("Referer", referer);
		}
		if(proxy != null){
			httpGet.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(proxy.getIp(), proxy.getPort()));
		}
		
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		if(httpResponse == null){
			return null;
		}
		
		try {
			return HtmlTools.getHtmlByBr(httpResponse.getEntity());
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
	} 
	/**
	 * 执行请求，返回JsonObject，如果不能被转换为JSON,则返回NULL，执行失败也将返回null
	 * url请不要包含gsid以及时间戳
	 * @param url 请求地址
	 * @return JSONObject 响应内容,JSONObject
	 */
	public JSONObject executeJSON(String url, ProxyBean proxy, boolean isgsid, String referer){
		String requestURL = url;
		// 加入时间戳和gsid
		if(isgsid){
			if(!requestURL.contains("&gsid=")){
				requestURL += "&gsid="+gsid;
			}
			if(!requestURL.contains("&_=")){
				requestURL += "&_="+System.currentTimeMillis();
			}
			
		}else{
			
			if(!requestURL.contains("&st")){
				requestURL += "&st="+st+"&";
			}
		}
		
		// 执行请求
		HttpGet httpGet = new HttpGet(requestURL);
		if(referer != null){
			httpGet.addHeader("Referer", referer);
		}
		if(proxy != null){
			httpGet.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(proxy.getIp(), proxy.getPort()));
		}
		
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		if(httpResponse == null){
			return null;
		}
		
		try {
			return new JSONObject(HtmlTools.getHtml(httpResponse.getEntity()));
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (JSONException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	/**
	 * 执行请求，返回JsonObject，如果不能被转换为JSON,则返回NULL，执行失败也将返回null
	 * url请不要包含gsid以及时间戳
	 * @param url 请求地址
	 * @return JSONObject 响应内容,JSONObject
	 */
	public JSONObject executeJSON(String url, boolean isgsid, String referer){
		String requestURL = url;
		// 加入时间戳和gsid
		if(isgsid){
			if(!requestURL.contains("&gsid=")){
				requestURL += "&gsid="+gsid;
			}
			if(!requestURL.contains("&_=")){
				requestURL += "&_="+System.currentTimeMillis();
			}
			
		}else{
			
			if(!requestURL.contains("&st")){
				requestURL += "&st="+st+"&";
			}
		}
		
		
		// 执行请求
		HttpGet httpGet = new HttpGet(requestURL);
		if(referer != null){
			httpGet.addHeader("Referer", referer);
		}
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		if(httpResponse == null){
			return null;
		}
		
		try {
			return new JSONObject(HtmlTools.getHtml(httpResponse.getEntity()));
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (JSONException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}


	public void showCookieStore(){
		DefaultHttpClient h = (DefaultHttpClient) getHttpClient();
		
		CookieStore store = h.getCookieStore();
		
		for(int i = 0 ; i < store.getCookies().size() ; i ++){
			System.out.println(store.getCookies().get(i));
		}
	}



	public HttpClient getHttpClient() {
		return httpClient;
	}
	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}
	public HttpResponse getHttpResponse() {
		return httpResponse;
	}
	public void setHttpResponse(HttpResponse httpResponse) {
		this.httpResponse = httpResponse;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getGsid() {
		return gsid;
	}
	public void setGsid(String gsid) {
		this.gsid = gsid;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	
	public String getCookie_gsid() {
		return cookie_gsid;
	}

	public void setCookie_gsid(String cookie_gsid) {
		this.cookie_gsid = cookie_gsid;
	}

	public String getSt() {
		return st;
	}

	public void setSt(String st) {
		this.st = st;
	}

	public String getWm() {
		return wm;
	}

	public void setWm(String wm) {
		this.wm = wm;
	}

	public String getVt() {
		return vt;
	}

	public void setVt(String vt) {
		this.vt = vt;
	}

	public static void main(String[] args) {
//		String str = HtmlTools.getFileContent(new File("e:\\a.txt"));
//		
//		String content = str.substring(0, str.indexOf("isClient"));
//		String[] split = content.split(",");
//		
//		for(int i = 0 ; i < split.length ; i ++){
//			String[] items = split[i].trim().split("=");
//			
//			String key = items[0].replace("var", "").trim();
//			String value = items[1].trim().replace("'", "");
//			
//			System.out.println(key+","+value);
//		}
		
		String str = "http://m.weibo.cn/?s2w=index_wbrk_0_wz_dh&wz=dh&from=index&wm=ig_0001_index&from=index&pos=108&vt=4&gsid=3_58a34843ff34b65583867d06c2972051b2846553#";
		
		
		System.out.println(HtmlTools.parseURLParamster(str));
		
	}
	
	
	
}
