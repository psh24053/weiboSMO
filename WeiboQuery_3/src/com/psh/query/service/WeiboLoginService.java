package com.psh.query.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.cookie.CookiePolicy;
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
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.log4j.PropertyConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import com.psh.base.json.JSONArray;
import com.psh.base.json.JSONException;
import com.psh.base.json.JSONObject;
import com.psh.base.util.PshConfigManager;
import com.psh.base.util.PshLogger;
import com.psh.query.bean.AccountBean;
import com.psh.query.bean.MsgBean;
import com.psh.query.bean.ProxyBean;
import com.psh.query.model.AccountModel;
import com.psh.query.model.CityModel;
import com.psh.query.model.ProvModel;
import com.psh.query.util.HtmlTools;

public class WeiboLoginService {
	
	private AccountBean account;
	private DefaultHttpClient httpClient;
	private CookieStore cookieStore;
	public static Map<String, File> fileCache = new HashMap<String, File>();
	public static final File cookieDir = new File("e:\\cookiedirs");
	private ProxyBean proxy;
	/**
	 * PreLogin.php实体类
	 * @author Administrator
	 *
	 */
	private class PreLoginInfo{
		public String nonce;
		public String rsakv;
		public long servertime;
		public String pubkey;
		public int retcode;
		public String pcid;
		public int showpin;
		public int execitme;
		
	}
	/**
	 * 重新登录返回值对象
	 * @author Administrator
	 *
	 */
	private class PayloadInfo{
		public String responseString;
		public boolean success;
	}
	
	
	public WeiboLoginService(AccountBean account){
		this.account = account;
		if(!cookieDir.exists()){
			cookieDir.mkdir();
		}
		
	}
	public WeiboLoginService(AccountBean account, ProxyBean proxy){
		this.account = account;
		if(!cookieDir.exists()){
			cookieDir.mkdir();
		}
		this.proxy = proxy;
		
	}
	/**
	 * 删除一个cookiestore
	 * @param key
	 * @return
	 */
	public boolean removeCookieStore(String key){
		if(hasCookieStore(key)){
			File removeFile = fileCache.get(key);
//			Log.i(TAG, "cacheManager.removeObjectCache -> " +key);
			// 如果removeFile存在并且是个文件并且可以写，则删除它，并返回删除结果
			if(removeFile.exists() && removeFile.isFile() && removeFile.canWrite()){
				return removeFile.delete();
			}
		}else{
			return false;
		}
		
		return false;
	}
	/**
	 * 判断对象缓存中是否存在key
	 * @param key
	 * @return
	 */
	public boolean hasCookieStore(String key){

//		PshLogger.logger.debug("hasObjectCache -> " +key);
		
		//得到文件数组
		File[] files = cookieDir.listFiles();
		//遍历文件数组
		for(int i = 0 ; i < files.length ; i ++){
			File itemFile = files[i];
			//判断文件是否存在，文件是否是一个文件，文件是否能读，是否能写，并且文件的名字与key相同
			//则代表对象缓存中存在这个key
			if(itemFile.exists() && itemFile.isFile() && itemFile.canRead() && itemFile.canWrite() && itemFile.getName().equals(key)){
				//将这个itemFile加入到内存中，为近期使用做准备
//				PshLogger.logger.debug("hasCookieStore -> " +key + " -> true");
				fileCache.put(key, itemFile);
				return true;
			}
		}
		
//		PshLogger.logger.debug("hasCookieStore -> " +key + " -> false");
		return false;
	}
	/**
	 * 保存一个CookieStore，传入一个序列化对象
	 * @param key
	 * @param object
	 * @throws IOException 
	 */
	public synchronized boolean SaveCookieStore(String key, Serializable object) throws IOException{

		//如果rom可用空间少于minRomSize，则不进行IO操作
		
		//创建输出文件对象
		File outFile = new File(cookieDir, key);

//		PshLogger.logger.debug("SaveCookieStore -> " +key+" -> "+object.toString());
		// 创建文件输出流
		FileOutputStream fos = new FileOutputStream(outFile);
		// 创建对象输出流，传入文件输出流
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		// 写出object，并关闭流
		oos.writeObject(object);
		oos.flush();
		oos.close();
		fos.close();
		
		// 将这个文件加入到cookieDir中
		return true;
		
	}
	/**
	 * 读取一个文件缓存，根据key
	 * @param key
	 * @return
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public CookieStore readCookieStore(String key) throws IOException, ClassNotFoundException{
		File readFile = null;
		//如果fileCache中存在这个key，则使用这个file
		//否则将从cacheDir中遍历读取
//		PshLogger.logger.debug("readCookieStore -> " +key);
		
		if(fileCache.containsKey(key)){
			readFile = fileCache.get(key);
		}else{
			//直接调用hasFilesCache方法来判断文件是否存在，如果不存在则返回false
			if(!hasCookieStore(key)){
//				PshLogger.logger.debug("readCookieStore -> " +key+" -> null");
				return null;
			}else{
				readFile = fileCache.get(key);
			}
			
		}
		
		// 创建文件输入流
		FileInputStream fis = new FileInputStream(readFile);
		ObjectInputStream ois = new ObjectInputStream(fis);
		// 返回反序列化后的对象，并关闭IO流
		Object o = ois.readObject();
		ois.close();
		fis.close();
		
		
//		PshLogger.logger.debug("readCookieStore -> " +key+" -> "+o.toString());
		return (CookieStore) o;
	}
	/**
	 * 获取PreLogin实体
	 * @param httpClient
	 * @param username
	 * @return
	 */
	private PreLoginInfo RunPreLogin(DefaultHttpClient httpClient, String username){
		HttpGet httpGet = new HttpGet("http://login.sina.com.cn/sso/prelogin.php?entry=weibo&callback=sinaSSOController.preloginCallBack&su="+eSU(username)+"&rsakt=mod&checkpin=1&client=ssologin.js(v1.4.5)&_="+System.currentTimeMillis());
		HttpResponse httpResponse = null;
		
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return null;
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return null;
		}
		
		if(httpResponse == null){
			PshLogger.logger.error("[RunPreLogin] httpResponse is null");
			return null;
		}
		String html = HtmlTools.getHtmlByBr(httpResponse);
		
		html = html.substring(html.indexOf("(")+1, html.lastIndexOf(")"));
		PreLoginInfo prelogin = new PreLoginInfo();
		try {
			JSONObject json = new JSONObject(html);
			prelogin.nonce = json.getString("nonce");
			prelogin.pubkey = json.getString("pubkey");
			prelogin.rsakv = json.getString("rsakv");
			prelogin.servertime = json.getLong("servertime");
			prelogin.execitme = json.getInt("exectime");
			prelogin.pcid = json.getString("pcid");
			prelogin.retcode = json.getInt("retcode");
			prelogin.showpin = json.getInt("showpin");
			
		} catch (JSONException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return null;
		}
		
		return prelogin;
	}
	/**
	 * 连接到一个URL，什么都不干，只是接收这个url所返回的cookie
	 * @param httpClient
	 * @param url
	 */
	private void executeCookie(DefaultHttpClient httpClient, String url, String method){
		if(method == null){
			return;
		}
		
		if(method.equals("post")){
			HttpPost httpPost = new HttpPost(url);
			try {
				httpClient.execute(httpPost);
			} catch (ClientProtocolException e) {
				PshLogger.logger.error("executeCookie error "+e.getMessage());
				return;
			} catch (IOException e) {
				PshLogger.logger.error("executeCookie error "+e.getMessage());
				return;
			}
		}else if(method.equals("get")){
			HttpGet HttpGet = new HttpGet(url);
			try {
				httpClient.execute(HttpGet);
			} catch (ClientProtocolException e) {
				PshLogger.logger.error("executeCookie error "+e.getMessage());
				return;
			} catch (IOException e) {
				PshLogger.logger.error("executeCookie error "+e.getMessage());
				return;
			}
		}
		
		
		
	}
	
	/**
	 * 执行登陆操作，成功返回true，失败返回false
	 * @return
	 */
	public boolean Login(){
		
		PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
		connectionManager.setMaxTotal(2000);
		connectionManager.setDefaultMaxPerRoute(1000);
		httpClient = new DefaultHttpClient(connectionManager);
		
		//伪装成Firefox 5, 构造httpClient
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1); 
		httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY); //
		httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,"UTF-8"); //这个是和目标网站的编码有关；
		httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_ELEMENT_CHARSET,"UTF-8"); 
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(30000)); 
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  new Integer(30000) ); 
		httpClient.getParams().setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, new Long(30000)); // second;
		List<BasicHeader> headerList = new ArrayList<BasicHeader>(); 
		headerList.add(new BasicHeader("Accept", "*/*")); 
		headerList.add(new BasicHeader("Connection", "keep-alive"));
		headerList.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64; rv:18.0) Gecko/20100101 Firefox/18.0"));
		httpClient.getParams().setParameter(ClientPNames.DEFAULT_HEADERS, headerList);
		httpClient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
		
		if(proxy != null){
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(proxy.getIp(), proxy.getPort()));
		}
		
		// 判断cookiestore是否存在，如果存在则取出
		if(hasCookieStore(account.getEmail())){
			try {
				cookieStore = readCookieStore(account.getEmail());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			httpClient.setCookieStore(cookieStore);
			return true;
		}
		
		cookieStore = httpClient.getCookieStore();
		
		
		// 开始登录逻辑
		HttpResponse httpResponse = null;
		// 首先访问weibo.com 获取必要cookie
		HttpGet httpGet = new HttpGet("http://weibo.com/");
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		
		// 获取prelogin实体类
		// 访问prelogin，获取账号验证码状态和pubkey
		PreLoginInfo prelogin = RunPreLogin(httpClient, account.getEmail());
		
		String pin = null;
		
		// 如果showpin 为1则代表需要输入验证码
		if(prelogin.showpin == 1){
			
			PinCode pincode = new PinCode("");
			pin = pincode.getCode(httpClient, prelogin.pcid, "http://login.sina.com.cn/cgi/pin.php?s=0&p="+prelogin.pcid);
			// 获取验证码
		}
		
		// 使用Rsa2算法计算sp
		String pwdString = prelogin.servertime + "\t" + prelogin.nonce + "\n" + account.getPassword();
		String sp = null;
		try {
			sp = rsaCrypt(prelogin.pubkey, "10001", pwdString);
		} catch (InvalidKeyException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		} catch (IllegalBlockSizeException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		} catch (BadPaddingException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		} catch (NoSuchAlgorithmException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		} catch (InvalidKeySpecException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		} catch (NoSuchPaddingException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		} catch (UnsupportedEncodingException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		
		// 获取某些cookie
		executeCookie(httpClient, "http://beacon.sina.com.cn/a.gif?V%3d2.2.1%26CI%3dsz%3a1366x768%7cdp%3a24%7cac%3aMozilla%7can%3aNetscape%7ccpu%3aWindows%2520NT%25206.2%3b%2520WOW64%7cpf%3aWin32%7cjv%3a1.3%7cct%3aunkown%7clg%3azh-CN%7ctz%3a-8%7cfv%3a11%7cja%3a1%26PI%3dpid%3a0-9999-0-0-1%7cst%3a0%7cet%3a2%7cref%3a%7chp%3aunkown%7cPGLS%3a%7cZT%3a%7cMT%3a%7ckeys%3a%7cdom%3a121%7cifr%3a0%7cnld%3a%7cdrd%3a%7cbp%3a0%7curl%3a%26UI%3dvid%3a1073891338073.4949.1360844317333%7csid%3a1073891338073.4949.1360844317333%7clv%3a%3a1%3a1%3a1%7cun%3a%7cuo%3a%7cae%3a%26EX%3dex1%3aWEIBO-V5%7cex2%3a%26gUid_"+System.currentTimeMillis(), "get");
		
		// 发起登录请求
		HttpPost httpPost = new HttpPost("http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.5)");
		List<NameValuePair> formlist = new ArrayList<NameValuePair>();
		
		formlist.add(new BasicNameValuePair("encoding", "UTF-8"));
		formlist.add(new BasicNameValuePair("entry", "weibo"));
		formlist.add(new BasicNameValuePair("from", ""));
		formlist.add(new BasicNameValuePair("gateway", "1"));
		formlist.add(new BasicNameValuePair("nonce", prelogin.nonce));
		formlist.add(new BasicNameValuePair("pagerefer", ""));
		formlist.add(new BasicNameValuePair("prelt", "121"));
		formlist.add(new BasicNameValuePair("pwencode", "rsa2"));
		formlist.add(new BasicNameValuePair("returntype", "META"));
		formlist.add(new BasicNameValuePair("rsakv", prelogin.rsakv));
		formlist.add(new BasicNameValuePair("savestate", "0"));
		formlist.add(new BasicNameValuePair("servertime", prelogin.servertime+""));
		formlist.add(new BasicNameValuePair("service", "miniblog"));
		formlist.add(new BasicNameValuePair("sp", sp));
		formlist.add(new BasicNameValuePair("su", encodeUserName(account.getEmail())));
		formlist.add(new BasicNameValuePair("url", "http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"));
		formlist.add(new BasicNameValuePair("useticket", "1"));
		formlist.add(new BasicNameValuePair("vsnf", "1"));
		
		if(pin != null){
			formlist.add(new BasicNameValuePair("pcid", prelogin.pcid));
			formlist.add(new BasicNameValuePair("door", pin));
		}
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(formlist,"utf-8"));
		} catch (UnsupportedEncodingException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}

		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		
		if(httpResponse == null){
			System.out.println("[277] httpResponse is null");
			return false;
		}
		
		// 获取location
		String location = getHeaderLocation(httpResponse);
		if(location != null){
			System.out.println("[284] "+location);
		}
		
		// 获取第一次ssologin的结果，判断是哪一种走向
		String htmlAjaxLogin = HtmlTools.getHtmlByBr(httpResponse);
		Document doc = Jsoup.parse(htmlAjaxLogin);
		Elements scripts = doc.select("script");
		int scriptSize = scripts.size();
		
		if(scriptSize == 0){
			System.out.println("script size = 0");
			return false;
		}else if(scriptSize == 1){
			// 这代表未知状态
			System.out.println("script size = 1");
			
			// 得到location准备跳转
			String url = getScriptLocationReplace(htmlAjaxLogin);
			System.out.println(url);
			httpGet = new HttpGet(url);
			try {
				httpResponse = httpClient.execute(httpGet);
			} catch (ClientProtocolException e) {
				PshLogger.logger.error(e.getMessage(),e);
				return false;
			} catch (IOException e) {
				PshLogger.logger.error(e.getMessage(),e);
				return false;
			}
			if(httpResponse == null){
//				PshLogger.logger.error("[300] httpResponse is null");
				System.out.println("[300] httpResponse is null");
				return false;
			}
			System.out.println(HtmlTools.getHtmlByBr(httpResponse));
			
			// 准备跳转
			url = getHeaderLocation(httpResponse);
			System.out.println(url);
			httpGet = new HttpGet(url);
			try {
				httpResponse = httpClient.execute(httpGet);
			} catch (ClientProtocolException e) {
				PshLogger.logger.error(e.getMessage(),e);
				return false;
			} catch (IOException e) {
				PshLogger.logger.error(e.getMessage(),e);
				return false;
			}
			
			if(httpResponse == null){
				PshLogger.logger.error("[319] httpResponse is null");
				System.out.println("[319] httpResponse is null");
				return false;
			}
			
			String content = HtmlTools.getHtmlByBr(httpResponse,"GBK");
			JSONObject Successjson = null;
			String userdomain = null;
			try {
				Successjson = new JSONObject(content.substring(content.indexOf("(")+1, content.indexOf(")")));
				userdomain = Successjson.getJSONObject("userinfo").getString("userdomain");
			} catch (JSONException e) {
				PshLogger.logger.error(e.getMessage(),e);
				return false;
			} 
			
			// 判断Url是否包含http
			if(!(userdomain.charAt(0) == 'h' || userdomain.charAt(0) == 'H')){
				userdomain = "http://weibo.com/" + userdomain;
			}
			System.out.println(userdomain);
			httpGet = new HttpGet(userdomain);
			try {
				httpResponse = httpClient.execute(httpGet);
			} catch (ClientProtocolException e) {
				PshLogger.logger.error(e.getMessage(),e);
				return false;
			} catch (IOException e) {
				PshLogger.logger.error(e.getMessage(),e);
				return false;
			}
			if(httpResponse == null){
				PshLogger.logger.error("[350] httpResponse is null");
				System.out.println("[350] httpResponse is null");
				return false;
			}
			userdomain = getHeaderLocation(httpResponse);
			
			// 代表该账号已经被表示为不正常，需要手机验证
			if(userdomain.contains("unfreeze")){
				PshLogger.logger.error("[489] account is unfreeze");
				System.out.println("[489] account is unfreeze");
				return false;
			}
			
			
			// 判断Url是否包含http
			if(!(userdomain.charAt(0) == 'h' || userdomain.charAt(0) == 'H')){
				userdomain = "http://weibo.com/" + userdomain;
			}
			System.out.println(userdomain);
			httpGet = new HttpGet(userdomain);
			try {
				httpResponse = httpClient.execute(httpGet);
			} catch (ClientProtocolException e) {
				PshLogger.logger.error(e.getMessage(),e);
				return false;
			} catch (IOException e) {
				PshLogger.logger.error(e.getMessage(),e);
				return false;
			}
			if(httpResponse == null){
				PshLogger.logger.error("[371] httpResponse is null");
				System.out.println("[371] httpResponse is null");
				return false;
			}
			
			// 到这里代表登录成功，接下来开始做cookie持久化
			System.out.println(cookieStore.getCookies().size());
			
			try {
				SaveCookieStore(account.getEmail(), (Serializable) cookieStore);
			} catch (IOException e) {
				PshLogger.logger.error(e.getMessage(),e);
				return false;
			}
			return true;
		}else if(scriptSize == 2){
			System.out.println("script size = 2");
			// 拥有两个script标签，代表可以跳转
			String SSOLocationReplace = scripts.get(1).html();
			String SSOUrl = SSOLocationReplace.substring(SSOLocationReplace.indexOf("replace('")+9, SSOLocationReplace.lastIndexOf("'"));
			
			httpGet = new HttpGet(SSOUrl);
			try {
				httpResponse = httpClient.execute(httpGet);
			} catch (ClientProtocolException e) {
				PshLogger.logger.error(e.getMessage(),e);
				return false;
			} catch (IOException e) {
				PshLogger.logger.error(e.getMessage(),e);
				return false;
			}
			if(httpResponse == null){
				PshLogger.logger.error("httpResponse is null");
				return false;
			}
			// 两个script的情况下，应该是选择location进行跳转
			
			String SSOHeaderLocation = getHeaderLocation(httpResponse);
			
			httpGet = new HttpGet(SSOHeaderLocation);
			try {
				httpResponse = httpClient.execute(httpGet);
			} catch (ClientProtocolException e) {
				PshLogger.logger.error(e.getMessage(),e);
				return false;
			} catch (IOException e) {
				PshLogger.logger.error(e.getMessage(),e);
				return false;
			}
			if(httpResponse == null){
				PshLogger.logger.error("httpResponse is null");
				return false;
			}
			// 这里应该开始获取Userinfo
			String content = HtmlTools.getHtmlByBr(httpResponse,"GBK");
			JSONObject Successjson = null;
			String userdomain = null;
			try {
				Successjson = new JSONObject(content.substring(content.indexOf("(")+1, content.indexOf(")")));
				userdomain = Successjson.getJSONObject("userinfo").getString("userdomain");
			} catch (JSONException e) {
				PshLogger.logger.error(e.getMessage(),e);
				return false;
			} 
			
			// 判断Url是否包含http
			if(!(userdomain.charAt(0) == 'h' || userdomain.charAt(0) == 'H')){
				userdomain = "http://weibo.com/" + userdomain;
			}
			// 到这里已经进入微博的主页了，但是还需要等待自由验证
			httpGet = new HttpGet(userdomain);
			try {
				httpResponse = httpClient.execute(httpGet);
			} catch (ClientProtocolException e) {
				PshLogger.logger.error(e.getMessage(),e);
				return false;
			} catch (IOException e) {
				PshLogger.logger.error(e.getMessage(),e);
				return false;
			}
			if(httpResponse == null){
				PshLogger.logger.error("httpResponse is null");
				return false;
			}
			String freeLocation = getHeaderLocation(httpResponse);
			if(freeLocation.contains("unfreeze")){
				// 悲剧，这个账号已经被判断为机器人账号了
				PshLogger.logger.error("account -> "+account.getUid() +" is unfreeze");
				return false;
			}
			// 到这里代表登录成功，接下来开始做cookie持久化
			System.out.println(cookieStore.getCookies().size());
			
			
			try {
				SaveCookieStore(account.getEmail(), (Serializable) cookieStore);
			} catch (IOException e) {
				PshLogger.logger.error(e.getMessage(),e);
				return false;
			}
			return true;
		}
		
		
		return true;
	}
	/**
	 * 发送微博，成功返回true，失败返回false
	 * @param content
	 * @return
	 */
	public boolean SendWeibo(String content){
		
		HttpPost httpPost = new HttpPost("http://weibo.com/aj/mblog/add?_wv=5&__rnd="+System.currentTimeMillis());
		httpPost.addHeader("Referer", "http://weibo.com/u/"+account.getUid());
		
		List<NameValuePair> formNames = new ArrayList<NameValuePair>();
		
		formNames.add(new BasicNameValuePair("_surl", ""));
		formNames.add(new BasicNameValuePair("_t", "0"));
		formNames.add(new BasicNameValuePair("hottopicid", ""));
		formNames.add(new BasicNameValuePair("location", "home"));
		formNames.add(new BasicNameValuePair("module", "stissue"));
		formNames.add(new BasicNameValuePair("pic_id", ""));
		formNames.add(new BasicNameValuePair("rank", "0"));
		formNames.add(new BasicNameValuePair("rankid", ""));
		formNames.add(new BasicNameValuePair("text", content));
		
		HttpResponse httpResponse = null;
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(formNames,"utf-8"));
			httpResponse = httpClient.execute(httpPost);
		} catch (UnsupportedEncodingException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		} catch (ClientProtocolException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		
		if(httpResponse == null){
			PshLogger.logger.error("[SendWeibo] httpResponse is null");
			return false;
		}
		
		String location = getHeaderLocation(httpResponse);
		PayloadInfo payload = new PayloadInfo();
		if(location != null && location.length() > 0){
			if(!reLogin(location, payload)){
				return false;
			}
		}else{
			payload.responseString = HtmlTools.getHtmlByBr(httpResponse);
		}
		
		return payload.responseString.contains("\"code\":\"100000\"");
			
		
	}
	/**
	 * 登录超时后调用
	 * @param location
	 * @return
	 */
	public boolean reLogin(String location, PayloadInfo payload){
		HttpGet httpGet = new HttpGet(location);
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		if(httpResponse == null){
			PshLogger.logger.error("[reLogin] httpResponse is null");
			return false;
		}
		String html = HtmlTools.getHtmlByBr(httpResponse);
		
		//到这里代表需要重新登录了
		if(html.contains("<title>新浪微博登录 </title>")){
			removeCookieStore(account.getEmail());
			return Login();
		}
		
		// 这里代表 还处在重新登录的状态中
		location = getScriptLocationReplace(html);
		System.out.println("[reLogin] url "+location);
		httpGet = new HttpGet(location);
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		if(httpResponse == null){
			PshLogger.logger.error("[reLogin] httpResponse is null");
			return false;
		}
		String locationStr = getHeaderLocation(httpResponse);
		if(locationStr != null){
			System.out.println("[reLogin] "+locationStr);
			// 判断Url是否包含http
			if(!(locationStr.charAt(0) == 'h' || locationStr.charAt(0) == 'H')){
				locationStr = "http://weibo.com" + locationStr;
			}
			
			httpGet = new HttpGet(locationStr);
			try {
				httpResponse = httpClient.execute(httpGet);
			} catch (ClientProtocolException e) {
				PshLogger.logger.error(e.getMessage(),e);
				return false;
			} catch (IOException e) {
				PshLogger.logger.error(e.getMessage(),e);
				return false;
			}
			if(httpResponse == null){
				PshLogger.logger.error("[reLogin] httpResponse is null");
				return false;
			}
			payload.responseString = HtmlTools.getHtmlByBr(httpResponse);
			
		}else{
			payload.responseString = HtmlTools.getHtmlByBr(httpResponse);
			
		}
		System.out.println("[reLogin] "+getHeaderLocation(httpResponse));
		System.out.println("[reLogin] "+HtmlTools.getHtmlByBr(httpResponse));
		
		// 重新写出cookie
		try {
			SaveCookieStore(account.getEmail(), (Serializable) httpClient.getCookieStore());
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		return true;
	}
	/**
	 * 重新登录
	 * @return
	 */
	public boolean reLoginFormSendWeibo(String url, String content){
		HttpGet httpGet = new HttpGet(url);
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		if(httpResponse == null){
			PshLogger.logger.error("[reLogin] httpResponse is null");
			return false;
		}
		String html = HtmlTools.getHtmlByBr(httpResponse);
		
		//到这里代表需要重新登录了
		if(html.contains("<title>新浪微博登录 </title>")){
			removeCookieStore(account.getEmail());
			if(Login()){
				return SendWeibo(content);
			}else{
				return false;
			}
		}
		
		// 这里代表 还处在重新登录的状态中
		url = getScriptLocationReplace(html);
		System.out.println("url "+url);
		httpGet = new HttpGet(url);
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		if(httpResponse == null){
			PshLogger.logger.error("[reLogin] httpResponse is null");
			return false;
		}
		
		// 重新写出cookie
		try {
			SaveCookieStore(account.getEmail(), (Serializable) httpClient.getCookieStore());
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		
		
		String responseString = HtmlTools.getHtmlByBr(httpResponse);
		return responseString.contains("\"code\":\"100000\"");
			
		
	}
	/**
	 * 发送评论，成功返回true，失败返回false
	 * @param content
	 * @param mid
	 * @return
	 */
	public boolean SendComment(String content, String mid){
		return false;
	}
	/**
	 * 发送回复，成功返回true，失败返回false
	 * @param content
	 * @param cid
	 * @param mid
	 * @param ouid
	 * @return
	 */
	public boolean SendReply(String content, String cid, String mid, long ouid){
		return false;
	}
	/**
	 * 发送私信，成功返回true，失败返回false，必须是别人先私信我之后，才能发送私信
	 * @param content
	 * @param uid
	 * @return
	 */
	public boolean SendLetter(String content, long uid){
		return false;
	}
	/**
	 * 转发
	 * @param content
	 * @param mid
	 * @return
	 */
	public boolean forward(String content, String mid){
		HttpPost httpPost = new HttpPost("http://s.weibo.com/ajax/mblog/forward?__rnd="+System.currentTimeMillis());
		httpPost.addHeader("Referer", "http://s.weibo.com/weibo/");
		
		HttpResponse httpResponse = null;
		List<NameValuePair> formslist = new ArrayList<NameValuePair>();
		
		formslist.add(new BasicNameValuePair("appkey", ""));
		formslist.add(new BasicNameValuePair("mid", mid));
		formslist.add(new BasicNameValuePair("style_type", "1"));
		formslist.add(new BasicNameValuePair("reason", content));
		formslist.add(new BasicNameValuePair("location", ""));
		formslist.add(new BasicNameValuePair("_t", "0"));
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(formslist,"utf-8"));
		} catch (UnsupportedEncodingException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		
		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		
		if(httpResponse == null){
			return false;
		}
		String location = getHeaderLocation(httpResponse);
		PayloadInfo payload = new PayloadInfo();

		if(location != null && location.length() > 0){
			if(reLogin(location, payload)){
				return forward(content, mid);
			}else{
				return false;
			}
		}else{
			payload.responseString = HtmlTools.getHtmlByBr(httpResponse);
		}
		System.out.println(payload.responseString);
		if(payload.responseString.contains("\"code\":\"100000\"")){
			return true;
		}
		
		return false;
	}
	/**
	 * 从新浪读取账号信息
	 * @param syn 是否同步到db，true代表同步，false代表不同步
	 * @return
	 */
	public AccountBean readInfo(boolean syn){
		HttpGet httpGet = new HttpGet("http://weibo.com/"+account.getUid()+"/info?from=profile&wvr=5&loc=tabinf#profile_tab");
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return null;
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return null;
		}
		
		if(httpResponse == null){
			System.out.println("[readInfo] httpResponse is null");
			PshLogger.logger.error("[readInfo] httpResponse is null");
			return null;
		}
		String location = getHeaderLocation(httpResponse);
		PayloadInfo payload = new PayloadInfo();
		if(location != null && location.length() > 0){
			if(reLogin(location, payload)){
				return readInfo(syn);
			}else{
				return null;
			}
		}else{
			payload.responseString = HtmlTools.getHtmlByBr(httpResponse);
		}
		String html = payload.responseString;
		if(html == null){
			System.out.println("[readInfo] html is null");
			PshLogger.logger.error("[readInfo] html is null");
			return null;
		}
		
		/*
		 * profile_tab 解析
		 * pl_profile_photo -> fans,att,weibo
		 * http://account.weibo.com/set/iframe?skin=skin000 -> birthday,city,prov,nickname,company,school,sex,tags,blood,emotion
		 */
		
		
		// 获取关注数，粉丝数，微博数
		Document doc = Jsoup.parse(html);
		Elements scripts = doc.select("script");
		for(int i = 0 ; i < scripts.size() ; i ++){
			Element script = scripts.get(i);
			String htmlStr = script.html();
			if(htmlStr.contains("pl_profile_photo")){
				
				htmlStr = htmlStr.substring(htmlStr.indexOf("(")+1, htmlStr.lastIndexOf(")"));
				JSONObject htmlJson = null;
				String src = null;
				try {
					htmlJson = new JSONObject(htmlStr);
					src = htmlJson.getString("html");
				} catch (JSONException e) {
					PshLogger.logger.error(e.getMessage(),e);
					return null;
				}
				Document document = Jsoup.parse(src);
				account.setAtt(Integer.valueOf(document.select("strong[node-type=follow]").text()));
				account.setFans(Integer.valueOf(document.select("strong[node-type=fans]").text()));
				account.setWeibo(Integer.valueOf(document.select("strong[node-type=weibo]").text()));
				
				break;
			}
			
		}
		
		// 获取其他详细信息
		httpGet = new HttpGet("http://account.weibo.com/set/iframe?skin=skin000");
		httpGet.addHeader("Referer", "http://weibo.com/"+account.getUid()+"/info");
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return null;
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return null;
		}
		
		if(httpResponse == null){
			PshLogger.logger.error("[readInfo] httpResponse is null");
			return null;
		}
		
		html = HtmlTools.getHtmlByBr(httpResponse);
	
		if(html == null){
			PshLogger.logger.error("[readInfo] html is null");
			return null;
		}
		
		doc = Jsoup.parse(html);
		
		account.setNickname(doc.select(".con[node-type=nickname_view]").text());
		account.setProv(doc.select(".con[node-type=city_view]").text().split(" ")[0]);
		account.setCity(doc.select(".con[node-type=city_view]").text().split(" ")[1]);
		account.setSex(doc.select(".con[node-type=sex_view]").text());
		account.setEmotion(doc.select(".con[node-type=love_view]").text().trim());
		account.setBirthday(doc.select(".con[node-type=birth_view]").text());
		account.setBlood(doc.select(".con[node-type=blood_view]").text());
		account.setInfo(doc.select(".con[node-type=desc_view]").text());
		
		
		// company
		// school
		account.getTagsMap().clear();
		Elements tags = doc.select("div[node-type=tag_item]");
		String tagStr = "";
		for(int i = 0 ; i < tags.size() ; i ++){
			Element tag = tags.get(i);
			String data = tag.attr("data");
			String value = data.split("&")[0];
			String tagid = data.split("&")[1];
			value = value.substring(value.indexOf("=")+1);
			tagid = tagid.substring(tagid.indexOf("=")+1);
			
			account.getTagsMap().put(tagid, value);
			tagStr += value;
			if(i+1 != tags.size()){
				tagStr += ",";
			}
		}
		
		account.setTags(tagStr);
		
		if(syn){
			AccountModel model = new AccountModel();
			model.UpdateAccount(account);
		}
		
		
		return account;
	}
	/**
	 * 修改资料
	 * @param account
	 * @return
	 */
	public boolean modifyInfo(AccountBean acc){
		HttpPost httpPost = new HttpPost("http://account.weibo.com/set/aj/iframe/editinfo");
		httpPost.addHeader("Referer", "http://account.weibo.com/set/iframe?skin=skin000");
		HttpResponse httpResponse = null;
		List<NameValuePair> formslist = new ArrayList<NameValuePair>();
		
		String[] birthday = acc.getBirthday().split("-");
		ProvModel provmodel = new ProvModel();
		int prov = provmodel.getProvIDByName(acc.getProv());
		
		CityModel citymodel = new CityModel();
		int city = citymodel.getCityIDByName(acc.getCity(), prov);
		
		formslist.add(new BasicNameValuePair("Date_Year", birthday[0]));
		formslist.add(new BasicNameValuePair("birthday_d", birthday[2]));
		formslist.add(new BasicNameValuePair("birthday_m", birthday[1]));
		formslist.add(new BasicNameValuePair("blog", ""));
		formslist.add(new BasicNameValuePair("blood", acc.getBlood().toUpperCase()));
		formslist.add(new BasicNameValuePair("city", city + ""));
		formslist.add(new BasicNameValuePair("gender", acc.getSex().equals("男")?"m":"f"));
		formslist.add(new BasicNameValuePair("love", "1"));
		formslist.add(new BasicNameValuePair("mydesc", acc.getInfo()));
		formslist.add(new BasicNameValuePair("nickname", acc.getNickname()));
		formslist.add(new BasicNameValuePair("oldnick", this.account.getNickname()));
		formslist.add(new BasicNameValuePair("province", prov + ""));
		formslist.add(new BasicNameValuePair("pub_birthday", "3"));
		formslist.add(new BasicNameValuePair("pub_blog", "2"));
		formslist.add(new BasicNameValuePair("pub_love", "1"));
		formslist.add(new BasicNameValuePair("pub_name", "0"));
		formslist.add(new BasicNameValuePair("pub_sextrend", "1"));
		formslist.add(new BasicNameValuePair("realname", ""));
		
		String rid_result = getSetting_rid(httpClient);
		
		formslist.add(new BasicNameValuePair("setting_rid", rid_result));
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(formslist,"utf-8"));
		} catch (UnsupportedEncodingException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		
		
		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		
		if(httpResponse == null){
			return false;
		}
		
		String baseInfoResponse = HtmlTools.getHtmlByBr(httpResponse);
		JSONObject baseInfoJson = null;
		try {
			baseInfoJson = new JSONObject(baseInfoResponse);
			System.out.println(baseInfoJson);
			if(!baseInfoJson.getString("code").equals("100000")){
				return false;
			}
		} catch (JSONException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		
		
		for(String key : this.account.getTagsMap().keySet()){
			long tagid = Long.valueOf(key);
			if(!delTag(tagid)){
				return false;
			}
		}
		String[] tags_map = acc.getTags().split(",");
		for(int i = 0 ; i < tags_map.length ; i ++){
			if(!addTag(tags_map[i])){
				return false;
			}
		}
		
		return true;
	}
	/**
	 * 检查uid是否存在
	 * @param uid
	 * @return
	 */
	public boolean checkUid(long uid){
		HttpGet httpGet = new HttpGet("http://weibo.com/u/"+uid);
		httpGet.addHeader("Referer", "http://weibo.com/");
		
		HttpResponse httpResponse = null;
	
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		
		if(httpResponse == null){
			PshLogger.logger.error("[checkUid] httpResponse is null");
			return false;
		}
		String location = getHeaderLocation(httpResponse);
		System.out.println(location);
		
		if(location.contains("login") && location.contains("sso")){
			PayloadInfo payload = new PayloadInfo();
			if(reLogin(location, payload)){
				return checkUid(uid);
			}else{
				return false;
			}
		}else if(location.contains("usernotexists") || location.contains("pagenotfound")){
			return false;
			// 判断Url是否包含http
		}
		
		return true;
	}
	/**
	 * 上传头像
	 * @param file
	 * @return
	 */
	public boolean UploadFace(File file, String setting_rid){
		FileBody filebody = new FileBody(file);
		
		MultipartEntity mulEntity = new MultipartEntity();
		try {
			mulEntity.addPart("setting_rid", new StringBody(setting_rid));
			mulEntity.addPart("rawpic", new StringBody("1"));
			mulEntity.addPart("Filedata",filebody);
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
			return false;
		}
		
		HttpPost httpPost = new HttpPost("http://account.weibo.com/aj4/settings/myface_postjs");
		httpPost.addHeader("Referer", "http://account.weibo.com/set/photo");
		httpPost.setEntity(mulEntity);
		
		HttpResponse httpResponse = null;
		
		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		
		if(httpResponse == null){
			System.out.println("httpresponse is null");
			return false;
		}
		
		return true;
	}
	/**
	 * 获取setting_rid
	 * @param httpClient
	 * @return
	 */
	public String getSetting_rid(DefaultHttpClient httpClient){
		HttpGet httpGet = new HttpGet("http://account.weibo.com/set/index");
		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return null;
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return null;
		}
		String location = getHeaderLocation(httpResponse);
		PayloadInfo payload = new PayloadInfo();
		if(location != null && location.length() > 0){
			if(reLogin(location, payload)){
				return getSetting_rid(httpClient);
			}else{
				return null;
			}
		}else{
			payload.responseString = HtmlTools.getHtmlByBr(httpResponse);
		}
		String setting_rid_html = payload.responseString;
		Document doc = Jsoup.parse(setting_rid_html);
		Elements scripts = doc.select("script");
		String rid_Result = null;
		for(int i = 0 ; i < scripts.size() ; i ++){
			Element element = scripts.get(i);
			String h = element.html();
			if(h != null && h.length() > 10 && h.contains("$CONFIG.setting_rid = '")){
				rid_Result = h.substring(h.indexOf("rid")+7,h.lastIndexOf("'"));
				rid_Result = rid_Result.replace("'", "").trim();
			}
		}
		return rid_Result;
	}
	/**
	 * 增加tag
	 * @param tag
	 * @return
	 */
	public boolean addTag(String tag){
		HttpPost httpPost = new HttpPost("http://account.weibo.com/set/aj/iframe/tagadd?__rnd="+System.currentTimeMillis());
		httpPost.addHeader("Referer", "	http://account.weibo.com/set/iframe?skin=skin000");
		HttpResponse httpResponse = null;
		
		List<NameValuePair> formslist = new ArrayList<NameValuePair>();
		
		formslist.add(new BasicNameValuePair("_t", "0"));
		formslist.add(new BasicNameValuePair("tag", tag));
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(formslist,"utf-8"));
		} catch (UnsupportedEncodingException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		String tagInfo = HtmlTools.getHtmlByBr(httpResponse);
		JSONObject baseInfoJson = null;
		try {
			baseInfoJson = new JSONObject(tagInfo);
			if(!baseInfoJson.getString("code").equals("100000")){
				return false;
			}
			
			JSONArray data = baseInfoJson.getJSONArray("data");
			for(int i = 0 ; i < data.length() ; i ++){
				JSONObject item = data.getJSONObject(i);
				account.getTagsMap().put(item.getString("tagid"), item.getString("tag"));
			}
			
		} catch (JSONException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		
		
		
		return true;
	}
	/**
	 * 删除tag
	 * @param tagid
	 * @return
	 */
	public boolean delTag(long tagid){
		HttpPost httpPost = new HttpPost("http://account.weibo.com/set/aj/iframe/tagdel?__rnd="+System.currentTimeMillis());
		httpPost.addHeader("Referer", "	http://account.weibo.com/set/iframe?skin=skin000");
		HttpResponse httpResponse = null;
		
		List<NameValuePair> formslist = new ArrayList<NameValuePair>();
		
		formslist.add(new BasicNameValuePair("_t", "0"));
		formslist.add(new BasicNameValuePair("tagid", tagid+""));
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(formslist,"utf-8"));
		} catch (UnsupportedEncodingException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		String tagInfo = HtmlTools.getHtmlByBr(httpResponse);
		JSONObject baseInfoJson = null;
		try {
			baseInfoJson = new JSONObject(tagInfo);
			if(!baseInfoJson.getString("code").equals("100000")){
				return false;
			}
		} catch (JSONException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		
		
		
		return true;
	}
	/**
	 * 关注别人
	 * @param uid
	 * @return
	 */
	public boolean attention(long uid){
		HttpPost httpPost = new HttpPost("http://s.weibo.com/ajax/user/follow?__rnd="+System.currentTimeMillis());
		httpPost.addHeader("Referer", "http://s.weibo.com/user/");
		
		HttpResponse httpResponse = null;
		List<NameValuePair> formslist = new ArrayList<NameValuePair>();
		
		formslist.add(new BasicNameValuePair("uid", uid+""));
		formslist.add(new BasicNameValuePair("type", "followed"));
		formslist.add(new BasicNameValuePair("wforce", "0"));
		formslist.add(new BasicNameValuePair("_t", "0"));
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(formslist,"utf-8"));
		} catch (UnsupportedEncodingException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		
		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return false;
		}
		
		if(httpResponse == null){
			return false;
		}
		
		String location = getHeaderLocation(httpResponse);
		PayloadInfo payload = new PayloadInfo();
		if(location != null && location.length() > 0){
			if(reLogin(location, payload)){
				return attention(uid);
			}else{
				return false;
			}
		}else{
			payload.responseString = HtmlTools.getHtmlByBr(httpResponse);
		}
		
		
		if(payload.responseString.contains("\"code\":\"100000\"")){
			return true;
		}
		
		return false;
	}
	/**
	 * 获取@我的微博
	 * @param mid 从这个Mid开始寻找，为Null则查找1天以内的，为all则查找全部
	 * @return
	 */
	public List<MsgBean> getToMeWeibo(String mid){
		return null;
	}
	/**
	 * 获取@我的评论
	 * @param mid 从这个Mid开始寻找，为Null则查找1天以内的，为all则查找全部
	 * @return
	 */
	public List<MsgBean> getToMeComment(String mid){
		return null;
	}
	/**
	 * 获取收到的评论
	 * @param mid 从这个Mid开始寻找，为Null则查找1天以内的，为all则查找全部
	 * @return
	 */
	public List<MsgBean> getReceiveComment(String mid){
		return null;
	}
	/**
	 * 获取私信
	 * @param mid 从这个Mid开始寻找，为Null则查找1天以内的，为all则查找全部
	 * @return
	 */
	public List<MsgBean> getLetter(String mid){
		return null;
	}
	
	/**
	 * 根据关键字搜索内容的页数
	 */
	public int searchKeywordPageNumber(String keyword){
		
		int pageNumber = -1;
		
		HttpPost httpPost = new HttpPost("http://s.weibo.com/weibo/" + keyword + "&Refer=index");
		httpPost.addHeader("Referer", "http://s.weibo.com/?topnav=1&wvr=5");
		HttpResponse httpResponse = null;
		
		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return pageNumber;
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return pageNumber;
		}
		
		if(httpResponse == null){
			PshLogger.logger.error("searchUid httpResponse is null");
			return -1;
		}
				
		String result = HtmlTools.getHtmlByBr(httpResponse, false, "search_page clearfix");
		
		if(result == null || result.equals("")){
			return -1;
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
		
		Elements elements = doc.getElementsByAttributeValue("class", "search_page_M");
		System.out.println(elements.size());
		
		String resultStringPage = "-1";
		
		if(elements.size() > 0){
			
			System.out.println(elements.get(0).getElementsByTag("li").size());
			Elements elements_1 = elements.get(0).getElementsByTag("li");
			
			resultStringPage = elements_1.get(elements_1.size() - 2).text();
			System.out.println(resultStringPage);
			
		}
		
		return Integer.parseInt(resultStringPage);
		
	}
	
	/**
	 * 根据关键字搜索内容列表
	 * @param keyword
	 * @return
	 */
	public List<MsgBean> searchKeyword(String keyword, int count){
		
		List<MsgBean> list = new ArrayList<MsgBean>();
		
		int pageNumber = searchKeywordPageNumber(keyword);
		
		for(int i = 0 ; i < pageNumber; i++){
			
			HttpPost httpPost = new HttpPost("http://s.weibo.com/weibo/" + keyword + "&Refer=index&page=" + (i + 1));
			httpPost.addHeader("Referer", "http://s.weibo.com/?topnav=1&wvr=5");
			HttpResponse httpResponse = null;
			
			try {
				httpResponse = httpClient.execute(httpPost);
			} catch (ClientProtocolException e) {
				PshLogger.logger.error(e.getMessage(),e);
				return list;
			} catch (IOException e) {
				PshLogger.logger.error(e.getMessage(),e);
				return list;
			}
			
			if(httpResponse == null){
				PshLogger.logger.error("searchUid httpResponse is null");
				return list;
			}
			
			String result = HtmlTools.getHtmlByBr(httpResponse, false, "feed_lists W_linka W_texta");
			
			if(result == null || result.equals("")){
				return list;
			}
			
			result = result.substring(result.indexOf("<div"),result.lastIndexOf("/div>") + 5);
			result = result.replace('\\','`');
			result = result.replaceAll("`n", "");
			result = result.replaceAll("`t", "");
			result = result.replaceAll("`r", "");
			result = result.replaceAll("`", "");
			result = "<html><body>" + result + "</body></html>";
			System.out.println(result);
			
			Document doc = Jsoup.parse(result,"UTF-8");
			
			Elements elements = doc.getElementsByAttributeValue("class", "feed_list");
			System.out.println(elements.size());
			
			List<MsgBean> msgList = new ArrayList<MsgBean>();
			//遍历每页的用户
			for(int j = 0 ; j < elements.size() ; j ++){
				
				MsgBean msg = new MsgBean();
				msg.setMid(elements.get(j).attr("mid"));
				System.out.println(msg.getMid());
				if(elements.get(j).attr("isforward") == null || elements.get(j).attr("isforward").equals("")){
					
				}else{
					msg.setType("1");
				}
				String usercard = elements.get(j).getElementsByAttribute("nick-name").get(0).attr("usercard");
				msg.setUid(Long.parseLong(usercard.substring(usercard.indexOf("=")+1, usercard.indexOf("&"))));
				System.out.println(msg.getUid());
				msg.setCon(elements.get(j).getElementsByTag("em").get(0).text());
				System.out.println(msg.getCon());
				if(elements.get(j).getElementsByClass("bigcursor").size() > 0){
					
					msg.setImage(elements.get(j).getElementsByClass("bigcursor").get(0).attr("src"));
					System.out.println(msg.getImage());
				}
				
				msg.setTime(elements.get(j).getElementsByClass("date").get(0).text());
				System.out.println(msg.getTime());
				msgList.add(msg);
			}
			
			list.addAll(msgList);
			if(list.size() >= count){
				
				return list.subList(0, count);
				
			}
			
		}
		
		
		return null;
	}
	/**
	 * 根据uid搜索，对应用户的所有微博
	 * @param uid
	 * @return
	 */
	public List<MsgBean> searchUid(long uid, int page){
		String contentUrl = null;
		if(account.getUid() == uid){
			contentUrl = "http://weibo.com/"+uid+"/profile?page=" + page;
		}else{
			contentUrl = "http://weibo.com/u/"+uid +"?page=" + page;
		}
		
		HttpGet HttpGet = new HttpGet(contentUrl);
		HttpGet.addHeader("Referer", "http://weibo.com/");
		HttpResponse httpResponse = null;
		List<MsgBean> list = new ArrayList<MsgBean>();
		
		try {
			httpResponse = httpClient.execute(HttpGet);
		} catch (ClientProtocolException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return list;
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return list;
		}
		
		if(httpResponse == null){
			PshLogger.logger.error("searchUid httpResponse is null");
			return list;
		}
				
		String location = getHeaderLocation(httpResponse);
		
		String result = null;
		
		if(location != null){
			
			if(location.contains("login") && location.contains("sso")){
				PayloadInfo payload = new PayloadInfo();
				if(location != null && location.length() > 0){
					if(reLogin(location, payload)){
						return searchUid(uid, page);
					}else{
						return list;
					}
				}else{
					payload.responseString = HtmlTools.getHtmlByBr(httpResponse, false, "WB_feed");
				}		
				result = payload.responseString;
			}else{
				// 判断Url是否包含http
				if(!(location.charAt(0) == 'h' || location.charAt(0) == 'H')){
					location = "http://weibo.com" + location;
				}
				HttpGet = new HttpGet(location);
				try {
					httpResponse = httpClient.execute(HttpGet);
				} catch (ClientProtocolException e) {
					PshLogger.logger.error(e.getMessage(),e);
					return list;
				} catch (IOException e) {
					PshLogger.logger.error(e.getMessage(),e);
					return list;
				}
				
				if(httpResponse == null){
					PshLogger.logger.error("searchUid httpResponse is null");
					return list;
				}
				result = HtmlTools.getHtmlByBr(httpResponse);
			}
		}else{
			result = HtmlTools.getHtmlByBr(httpResponse);
		}
		
		
		if(result == null || result.equals("")){
			return null;
		}
		
		result = result.substring(result.indexOf("<div"),result.lastIndexOf("/div>") + 5);
		result = result.replace('\\','`');
		result = result.replaceAll("`n", "");
		result = result.replaceAll("`t", "");
		result = result.replaceAll("`r", "");
		result = result.replaceAll("`", "");
		result = "<html><body>" + result + "</body></html>";
		
		Document doc = Jsoup.parse(result,"UTF-8");
		Elements elements = doc.getElementsByAttribute("mid");
		System.out.println(elements.size());
		
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
				
				String contentChinese = elements.get(i).getElementsByAttributeValue("node-type", "feed_list_content").get(0).text();
				
				contentChinese = HtmlTools.decodeUnicode(contentChinese);
				
				msg.setCon(contentChinese);
			}
			System.out.println("contentsss :" + msg.getCon());
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
			list.add(msg);
		}
		List<MsgBean> freshList_1 = new ArrayList<MsgBean>();
		
		freshList_1 = getMsgMouseRollEvent(uid, 0,page);
		
		if(freshList_1 == null || freshList_1.size() == 0){
			System.out.println("fresh1 is null____________");
			return list;
		}
		
		list.addAll(freshList_1);
		
		List<MsgBean> freshList_2 = new ArrayList<MsgBean>();
		
		freshList_2 = getMsgMouseRollEvent(uid, 1,page);
		
		
		if(freshList_2 == null || freshList_2.size() == 0){
			System.out.println("fresh2 is null____________");
			return list;
		}
		
		list.addAll(freshList_2);
		
		System.out.println("in list size *************" + list.size());
		return list;
	}
	
	/**
	 * 获取刷新的
	 * @param uid
	 * @return
	 */
	public List<MsgBean> getMsgMouseRollEvent(long uid,int pageBar,int page){
		
		HttpPost httpPost = new HttpPost("http://weibo.com/aj/mblog/mbloglist?_wv=5&page=" + page + "&count=15&pre_page=1&pagebar=" + pageBar + "&_k=13612872994886" + pageBar + "&uid=" + uid + "&_t=0");
		httpPost.addHeader("Referer", "http://weibo.com/u/" + uid);
		HttpResponse httpResponse = null;
		List<MsgBean> list = new ArrayList<MsgBean>();
		
		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return list;
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage(),e);
			return list;
		}
		
		if(httpResponse == null){
			PshLogger.logger.error("searchUid httpResponse is null");
			return list;
		}
				
		String JsonResult = HtmlTools.getHtmlByBr(httpResponse);
		
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
		
		Document doc = Jsoup.parse(result);
		
		Elements elements = doc.getElementsByAttribute("mid");
		System.out.println(elements.size());
		
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
			list.add(msg);
		}
		
		return list;
	}
	
	/**
	 * @param args
	 * @throws JSONException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchAlgorithmException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws InvalidKeyException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void main(String[] args) throws JSONException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, ClientProtocolException, IOException {
		
//		System.out.println(getHeaderLocation(httpResponse));
//		System.out.println(HtmlTools.getHtmlByBr(httpResponse.getEntity()));
		
//		http://account.weibo.com/set/iframe?skin=skin000	
//		http://weibo.com/1661461070/info
		
//		AccountModel model = new AccountModel();
//		List<AccountBean> data = model.getRegAccountAll();
//		
//		for(int i = 0 ; i < data.size() ; i ++){
//			AccountBean bean = data.get(i);
//			WeiboLoginService l = new WeiboLoginService(bean);		
//			if(l.Login()){
//				model.updateRegAccountStatus((int)bean.getValue("aid"), 66);
//			}
//			
//		}
		AccountBean account = new AccountBean();
		account.setEmail("psh24053@yahoo.cn");
		account.setPassword("caicai520");
		account.setUid(1661461070);
//		
		WeiboLoginService l = new WeiboLoginService(account);
		l.Login();
//		l.searchUid(2363715054l, 1);
//		l.searchKeywordPageNumber("http://s.weibo.com/weibo/哈哈&Refer=index");
//		l.searchKeyword("哈哈", 10);
		l.getMsgMouseRollEvent(2363715054l, 0,1);
//		l.attention(3154924132l);
//		l.forward("转发一个试试", "3547483110422351");
//		l.modifyInfo(null);
//		l.SendWeibo("今天真J8累啊");
		
		
//		
//		formslist.add(new BasicNameValuePair("Date_Year", "1991"));
//		formslist.add(new BasicNameValuePair("birthday_d", "24"));
//		formslist.add(new BasicNameValuePair("birthday_m", "11"));
//		formslist.add(new BasicNameValuePair("blog", ""));
//		formslist.add(new BasicNameValuePair("blood", "A"));
//		formslist.add(new BasicNameValuePair("city", "1"));
//		formslist.add(new BasicNameValuePair("gender", "m"));
//		formslist.add(new BasicNameValuePair("love", "2"));
//		formslist.add(new BasicNameValuePair("mydesc", "descript"));
//		formslist.add(new BasicNameValuePair("nickname", "西瓜哥_ixgsoft"));
//		formslist.add(new BasicNameValuePair("oldnick", "西瓜哥_ixgsoft"));
//		formslist.add(new BasicNameValuePair("province", "51"));
//		formslist.add(new BasicNameValuePair("pub_birthday", "3"));
//		formslist.add(new BasicNameValuePair("pub_blog", "2"));
//		formslist.add(new BasicNameValuePair("pub_love", "1"));
//		formslist.add(new BasicNameValuePair("pub_name", "0"));
//		formslist.add(new BasicNameValuePair("pub_sextrend", "1"));
//		formslist.add(new BasicNameValuePair("realname", ""));
//		formslist.add(new BasicNameValuePair("setting_rid", "pOFM6XuIwfJG9hBEahelyTtTmUA="));
//		cHNoMjQwNTMlNDB5YWhvby5jbg==
//		pOFM6XuIwfJG9hBEahelyTtTmUA=
//		System.out.println(encodeUserName("weibo.com/u/1661461070"));
		
//		getUserInfo(l.httpClient, "http://weibo.com/1661461070/info");
	}
	
	public static void Stt(String a){
		a = "asdkljasd";
	}
	
	public static void getUserInfo(HttpClient httpClient, String referer) throws ClientProtocolException, IOException{
		HttpGet HttpGet = new HttpGet("http://account.weibo.com/set/iframe?skin=skin000");
		HttpGet.addHeader("Referer", referer);
		
		HttpResponse httpResponse = httpClient.execute(HttpGet);
		
		System.out.println(HtmlTools.getHtmlByBr(httpResponse));
		
	}
	
	public static void sendWeibo(HttpClient httpClient, String str, String referer) throws ClientProtocolException, IOException{
		HttpPost httpPost = new HttpPost("http://weibo.com/aj/mblog/add?_wv=5&__rnd="+System.currentTimeMillis());
		httpPost.addHeader("Referer", referer);
		
		List<NameValuePair> formNames = new ArrayList<NameValuePair>();
		
		formNames.add(new BasicNameValuePair("_surl", ""));
		formNames.add(new BasicNameValuePair("_t", "0"));
		formNames.add(new BasicNameValuePair("hottopicid", ""));
		formNames.add(new BasicNameValuePair("location", "home"));
		formNames.add(new BasicNameValuePair("module", "stissue"));
		formNames.add(new BasicNameValuePair("pic_id", ""));
		formNames.add(new BasicNameValuePair("rank", "0"));
		formNames.add(new BasicNameValuePair("rankid", ""));
		formNames.add(new BasicNameValuePair("text", str));
		
		
		httpPost.setEntity(new UrlEncodedFormEntity(formNames,"utf-8"));
		HttpResponse httpResponse = httpClient.execute(httpPost);
		
		System.out.println(HtmlTools.getHtmlByBr(httpResponse));
		
	}
	
	public static String getScriptLocationReplace(String html){
	 	Document doc = Jsoup.parse(html);
	 	Elements scripts = doc.select("script");
	 	
	 	String script = scripts.get(0).html();
		return script.substring(script.indexOf("\"")+1, script.lastIndexOf("\""));
	}
	
	public static String getHeaderLocation(HttpResponse httpResponse){
		Header location = httpResponse.getFirstHeader("Location");
				
		if(location != null){
			return location.getValue();
		}
		return null;
	}
	public static JSONObject getPreLogin(DefaultHttpClient httpClient, String username){
		HttpGet httpGet = new HttpGet("http://login.sina.com.cn/sso/prelogin.php?entry=weibo&callback=sinaSSOController.preloginCallBack&su="+eSU(username)+"&rsakt=mod&checkpin=1&client=ssologin.js(v1.4.5)&_="+System.currentTimeMillis());
		HttpResponse httpResponse = null;
		
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(httpResponse == null){
			return null;
		}
		
		String html = HtmlTools.getHtmlByBr(httpResponse);
		
		html = html.substring(html.indexOf("(")+1, html.lastIndexOf(")"));
		
		try {
			JSONObject json = new JSONObject(html);
			return json;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	public static String rsaCrypt(String modeHex, String exponentHex, String messageg) throws IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchPaddingException,
			InvalidKeyException, UnsupportedEncodingException {
		KeyFactory factory = KeyFactory.getInstance("RSA");
		
		BigInteger m = new BigInteger(modeHex, 16); /* public exponent */
		BigInteger e = new BigInteger(exponentHex, 16); /* modulus */
		RSAPublicKeySpec spec = new RSAPublicKeySpec(m, e);
		
		RSAPublicKey pub = (RSAPublicKey) factory.generatePublic(spec);
		Cipher enc = Cipher.getInstance("RSA");
		enc.init(Cipher.ENCRYPT_MODE, pub);
		
		byte[] encryptedContentKey = enc.doFinal(messageg.getBytes("GB2312"));
		
		return new String(Hex.encodeHex(encryptedContentKey));
	}
	public static String eSU(String username){
		
		return URLEncoder.encode(encodeUserName(username));
	}
	
	public static String encodeUserName(String email) {
		email = email.replaceFirst("@", "%40");// MzM3MjQwNTUyJTQwcXEuY29t
		email = Base64.encodeBase64String(email.getBytes());
		return email;
	}
}
