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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.params.ConnRoutePNames;
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

import com.psh.base.json.JSONException;
import com.psh.base.json.JSONObject;
import com.psh.base.util.PshConfigManager;
import com.psh.base.util.PshLogger;
import com.psh.query.bean.AccountBean;
import com.psh.query.bean.MsgBean;
import com.psh.query.bean.ProxyBean;
import com.psh.query.model.AccountModel;
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
	 * 判断对象缓存中是否存在key
	 * @param key
	 * @return
	 */
	public boolean hasCookieStore(String key){

		PshLogger.logger.debug("hasObjectCache -> " +key);
		
		//得到文件数组
		File[] files = cookieDir.listFiles();
		//遍历文件数组
		for(int i = 0 ; i < files.length ; i ++){
			File itemFile = files[i];
			//判断文件是否存在，文件是否是一个文件，文件是否能读，是否能写，并且文件的名字与key相同
			//则代表对象缓存中存在这个key
			if(itemFile.exists() && itemFile.isFile() && itemFile.canRead() && itemFile.canWrite() && itemFile.getName().equals(key)){
				//将这个itemFile加入到内存中，为近期使用做准备
				PshLogger.logger.debug("hasCookieStore -> " +key + " -> true");
				fileCache.put(key, itemFile);
				return true;
			}
		}
		
		PshLogger.logger.debug("hasCookieStore -> " +key + " -> false");
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

		PshLogger.logger.debug("SaveCookieStore -> " +key+" -> "+object.toString());
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
		PshLogger.logger.debug("readCookieStore -> " +key);
		
		if(fileCache.containsKey(key)){
			readFile = fileCache.get(key);
		}else{
			//直接调用hasFilesCache方法来判断文件是否存在，如果不存在则返回false
			if(!hasCookieStore(key)){
				PshLogger.logger.debug("readCookieStore -> " +key+" -> null");
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
		
		
		PshLogger.logger.debug("readCookieStore -> " +key+" -> "+o.toString());
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
		
		System.out.println(account);
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
		
		System.out.println("proLogin complete!");
		
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
		System.out.println("executeCookie complete!");
		
		// 发起登录请求
		System.out.println("http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.5)");
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
		System.out.println("ssologin complete");
		
		// 获取location
		String location = getHeaderLocation(httpResponse);
		if(location != null){
			System.out.println("[284] "+location);
		}
		
		System.out.println("[450]");
		
		// 获取第一次ssologin的结果，判断是哪一种走向
		String htmlAjaxLogin = HtmlTools.getHtmlByBr(httpResponse);
		Document doc = Jsoup.parse(htmlAjaxLogin);
		Elements scripts = doc.select("select");
		int scriptSize = scripts.size();
		
		if(scriptSize == 0){
			System.out.println("script size = 0");
			return false;
		}else if(scriptSize == 1){
			// 这代表未知状态
			System.err.println(htmlAjaxLogin);
			return false;
		}else if(scriptSize == 2){
			// 拥有两个script标签，代表可以跳转
			String SSOLocationReplace = scripts.get(1).html();
			
		}
		
		
		
		
		System.out.println(htmlAjaxLogin);
		
		// 得到location准备跳转
		String url = getScriptLocationReplace(htmlAjaxLogin);
		System.out.println("[458]");
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
//			PshLogger.logger.error("[300] httpResponse is null");
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
//			PshLogger.logger.error(e.getMessage(),e);
//			return false;
			e.printStackTrace();
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
//			PshLogger.logger.error(e.getMessage(),e);
//			return false;
			e.printStackTrace();
		} catch (IOException e) {
//			PshLogger.logger.error(e.getMessage(),e);
//			return false;
			e.printStackTrace();
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
//			PshLogger.logger.error(e.getMessage(),e);
//			return false;
			e.printStackTrace();
		} catch (IOException e) {
//			PshLogger.logger.error(e.getMessage(),e);
//			return false;
			e.printStackTrace();
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
//			PshLogger.logger.error(e.getMessage(),e);
//			return false;
			e.printStackTrace();
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
		
		String html = HtmlTools.getHtmlByBr(httpResponse);
	
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
			if(i != tags.size() - 1){
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
	public boolean modifyInfo(AccountBean account){
		return false;
	}
	/**
	 * 关注别人
	 * @param uid
	 * @return
	 */
	public boolean attention(long uid){
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
	 * 根据关键字搜索内容列表
	 * @param keyword
	 * @return
	 */
	public List<MsgBean> searchKeyword(String keyword){
		return null;
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
		
		AccountModel model = new AccountModel();
		List<AccountBean> data = model.getRegAccountAll();
		
		for(int i = 0 ; i < data.size() ; i ++){
			AccountBean bean = data.get(i);
			WeiboLoginService l = new WeiboLoginService(bean);		
			if(l.Login()){
				model.updateRegAccountStatus((int)bean.getValue("aid"), 66);
			}
			
		}
		
		
		
//		getUserInfo(l.httpClient, "http://weibo.com/1661461070/info");
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
	 	System.out.println(script);
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
