package com.psh.query.service;

import java.io.IOException;
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
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.psh.base.json.JSONException;
import com.psh.base.json.JSONObject;
import com.psh.query.bean.AccountBean;
import com.psh.query.bean.MsgBean;
import com.psh.query.util.HtmlTools;

public class WeiboLoginService {

	public static final String username = "psh24053@yahoo.cn";
	public static final String password = "caicai520";
	
	
	private AccountBean account;
	
	
	public WeiboLoginService(AccountBean account){
		this.account = account;
	}
	/**
	 * 执行登陆操作，成功返回true，失败返回false
	 * @return
	 */
	public boolean Login(){
		return false;
	}
	/**
	 * 发送微博，成功返回true，失败返回false
	 * @param content
	 * @return
	 */
	public boolean SendWeibo(String content){
		return false;
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
	 * 从新浪读取账号信息
	 * @param syn 是否同步到db，true代表同步，false代表不同步
	 * @return
	 */
	public AccountBean readInfo(boolean syn){
		return null;
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
		
		PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
		connectionManager.setMaxTotal(2000);
		connectionManager.setDefaultMaxPerRoute(1000);
		DefaultHttpClient httpClient = new DefaultHttpClient(connectionManager);
		
		
		//伪装成Firefox 5, 
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1); 
		httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY); //
		httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,"UTF-8"); //这个是和目标网站的编码有关；
		httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_ELEMENT_CHARSET,"UTF-8"); 
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(30000)); 
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  new Integer(30000) ); 
		httpClient.getParams().setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, new Long(30000)); // second;
		httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/17.0 Firefox/17.0");
		List<BasicHeader> headerList = new ArrayList<BasicHeader>(); 
		headerList.add(new BasicHeader("Accept", "*/*")); 
		headerList.add(new BasicHeader("Connection", "keep-alive"));
		headerList.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64; rv:18.0) Gecko/20100101 Firefox/18.0"));
		
		
		httpClient.getParams().setParameter(ClientPNames.DEFAULT_HEADERS, headerList);
		httpClient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
		
	 
		HttpResponse httpResponse = null;
		
		HttpGet httpGet = new HttpGet("http://weibo.com/");
		httpResponse = httpClient.execute(httpGet);

		JSONObject prelogin = getPreLogin(httpClient, username);
		String nonce = prelogin.getString("nonce");
		String rsakv = prelogin.getString("rsakv");
		long servertime = prelogin.getLong("servertime");
		String pubkey = prelogin.getString("pubkey");
		String pwdString = servertime + "\t" + nonce + "\n" + password;
		String sp = rsaCrypt(pubkey, "10001", pwdString);
		
		httpGet = new HttpGet("http://beacon.sina.com.cn/a.gif?V%3d2.2.1%26CI%3dsz%3a1366x768%7cdp%3a24%7cac%3aMozilla%7can%3aNetscape%7ccpu%3aWindows%2520NT%25206.2%3b%2520WOW64%7cpf%3aWin32%7cjv%3a1.3%7cct%3aunkown%7clg%3azh-CN%7ctz%3a-8%7cfv%3a11%7cja%3a1%26PI%3dpid%3a0-9999-0-0-1%7cst%3a0%7cet%3a2%7cref%3a%7chp%3aunkown%7cPGLS%3a%7cZT%3a%7cMT%3a%7ckeys%3a%7cdom%3a121%7cifr%3a0%7cnld%3a%7cdrd%3a%7cbp%3a0%7curl%3a%26UI%3dvid%3a1073891338073.4949.1360844317333%7csid%3a1073891338073.4949.1360844317333%7clv%3a%3a1%3a1%3a1%7cun%3a%7cuo%3a%7cae%3a%26EX%3dex1%3aWEIBO-V5%7cex2%3a%26gUid_1360844317420");
		httpClient.execute(httpGet);
		
		HttpPost httpPost = new HttpPost("http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.5)");
		List<NameValuePair> formlist = new ArrayList<NameValuePair>();
		
		formlist.add(new BasicNameValuePair("encoding", "UTF-8"));
		formlist.add(new BasicNameValuePair("entry", "weibo"));
		formlist.add(new BasicNameValuePair("from", ""));
		formlist.add(new BasicNameValuePair("gateway", "1"));
		formlist.add(new BasicNameValuePair("nonce", nonce));
		formlist.add(new BasicNameValuePair("pagerefer", ""));
		formlist.add(new BasicNameValuePair("prelt", "121"));
		formlist.add(new BasicNameValuePair("pwencode", "rsa2"));
		formlist.add(new BasicNameValuePair("returntype", "META"));
		formlist.add(new BasicNameValuePair("rsakv", rsakv));
		formlist.add(new BasicNameValuePair("savestate", "0"));
		formlist.add(new BasicNameValuePair("servertime", servertime+""));
		formlist.add(new BasicNameValuePair("service", "miniblog"));
		formlist.add(new BasicNameValuePair("sp", sp));
		formlist.add(new BasicNameValuePair("su", encodeUserName(username)));
		formlist.add(new BasicNameValuePair("url", "http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"));
		formlist.add(new BasicNameValuePair("useticket", "1"));
		formlist.add(new BasicNameValuePair("vsnf", "1"));
		
		httpPost.setEntity(new UrlEncodedFormEntity(formlist,"utf-8"));

		httpResponse = httpClient.execute(httpPost);
		
		String result = HtmlTools.getHtmlByBr(httpResponse);
		
		String url = getScriptLocationReplace(result);
		httpGet = new HttpGet(url);
		
		
		httpResponse = httpClient.execute(httpGet);
		
		url = getHeaderLocation(httpResponse);
		
		httpGet = new HttpGet(url);
		
		httpResponse = httpClient.execute(httpGet);
		
		
		String content = HtmlTools.getHtmlByBr(httpResponse.getEntity(),"GBK");
		
		
		JSONObject j = new JSONObject(content.substring(content.indexOf("(")+1, content.indexOf(")"))); 
		
		
		String userdomain = j.getJSONObject("userinfo").getString("userdomain");
		
		// 判断Url是否包含http
		if(!(userdomain.charAt(0) == 'h' || userdomain.charAt(0) == 'H')){
			userdomain = "http://weibo.com/" + userdomain;
		}
		
		httpGet = new HttpGet(userdomain);
		httpResponse = httpClient.execute(httpGet);
		
		userdomain = getHeaderLocation(httpResponse);
		
		// 判断Url是否包含http
		if(!(userdomain.charAt(0) == 'h' || userdomain.charAt(0) == 'H')){
			userdomain = "http://weibo.com/" + userdomain;
		}
		
		httpGet = new HttpGet(userdomain);
		httpResponse = httpClient.execute(httpGet);
		
		
//		sendWeibo(httpClient, "二逼 ！~ @彭琅琅琅琅", userdomain);
		
		getUserInfo(httpClient, "http://weibo.com/1661461070/info");
		
//		System.out.println(getHeaderLocation(httpResponse));
//		System.out.println(HtmlTools.getHtmlByBr(httpResponse.getEntity()));
		
//		http://account.weibo.com/set/iframe?skin=skin000	
//		http://weibo.com/1661461070/info
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
	 	
	 	String script = scripts.get(1).html();
	 	
	 	
		return script.substring(script.indexOf("replace")+9, script.lastIndexOf("'"));
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
