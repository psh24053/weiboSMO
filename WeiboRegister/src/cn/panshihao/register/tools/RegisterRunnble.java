package cn.panshihao.register.tools;

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
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.cookie.params.CookieSpecPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import cn.panshihao.desktop.commons.HtmlTools;
import cn.panshihao.desktop.commons.Log;
import cn.panshihao.register.model.wb_accountModel;
import cn.panshihao.register.model.wb_proxyModel;
import cn.panshihao.register.tools.PinCode.FastVerCode;

public class RegisterRunnble implements Runnable {

	private RegisterService registerService;
	private ProxyService proxyService;
	
	public RegisterRunnble(RegisterService service){
		this.registerService = service;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		wb_accountModel account = null;
		wb_proxyModel proxy = null;
		
		
		while((account = registerService.getAccountModelFromRandomData()) != null){
			
			proxy = proxyService.getRandomProxyModel();
			
			boolean register = runRegister(account, proxy);
			
			// 将已经使用过的proxyModel归还到内存中
			proxyService.revertProxyModel(proxy, System.currentTimeMillis());
			
		}
		
		
		
	}
	/**
	 * 注册
	 * @param account
	 * @param proxy
	 * @return 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public boolean runRegister(wb_accountModel account, wb_proxyModel proxy) {
		
		Log.log.debug("wb_accountModel -> "+account.toString()+" ,wb_proxyModel -> "+proxy);
		
		if(account == null || proxy == null){
			Log.log.error("account or proxy is null!");
			return false;
		}
		
		
		long startTime = System.currentTimeMillis();
		
		/**
		 * 创建多线程连接器
		 */
		PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
		connectionManager.setMaxTotal(2000);
		connectionManager.setDefaultMaxPerRoute(1000);
		
		HttpClient httpClient = new DefaultHttpClient(connectionManager);
		
		
		//伪装成Firefox 5, 
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1); httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY); // 一定要有，否则会生成多个Cookie header送给web server 
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
		headerList.add(new BasicHeader("Host", "www.weibo.com"));
		headerList.add(new BasicHeader("Origin", "http://www.weibo.com"));
		headerList.add(new BasicHeader("Referer", "http://www.weibo.com/signup/mobile.php"));
		httpClient.getParams().setParameter(ClientPNames.DEFAULT_HEADERS, headerList);
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(30000)); 
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  new Integer(30000) ); 
		httpClient.getParams().setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, new Long(30000)); // second;
		httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/17.0 Firefox/17.0");
		
		//设置代理对象 ip/代理名称,端口     
		httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(proxy.getIp(), proxy.getPort()));
        
        // 请求注册界面，获取表单必须参数
        HttpPost httpPost = new HttpPost("http://www.weibo.com/signup/mobile.php");
		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			Log.log.error(e.getMessage(), e);
			return false;
		} catch (IOException e) {
			Log.log.error(e.getMessage(), e);
			return false;
		}
		HttpEntity httpEntity = httpResponse.getEntity();
		
		String html = null;
		try {
			html = HtmlTools.getHtml(httpEntity);
		} catch (UnsupportedEncodingException e) {
			Log.log.error(e.getMessage(), e);
			return false;
		} catch (IllegalStateException e) {
			Log.log.error(e.getMessage(), e);
			return false;
		} catch (IOException e) {
			Log.log.error(e.getMessage(), e);
			return false;
		}
		
		Log.log.debug("getHtml "+html);
		
		// 如果html为null，或者html的长度小于8000，则代表获取html失败
		if(html == null || html.length() < 8000){
			Log.log.error("html get error!");
			return false;
		}
		
		// 使用Jsoup解析html
		Document doc = Jsoup.parse(html);
		Elements elements = doc.getElementsByAttributeValue("type", "hidden");
        
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		
		String sinaId = null;
		String regtime = null;
		
		for(int i = 0 ; i < elements.size() ; i ++){
			String name = elements.get(i).attr("name");
			String value = elements.get(i).attr("value");
			
			if(name.equals("sinaid")){
				sinaId = value;
			}
			if(name.equals("regtime")){
				regtime = value;
			}
			formParams.add(new BasicNameValuePair(name, value)); 
		}
		formParams.add(new BasicNameValuePair("nickname", account.getNickname())); 
		formParams.add(new BasicNameValuePair("passwd", account.getPassword())); 
		formParams.add(new BasicNameValuePair("username", account.getEmail())); 
		formParams.add(new BasicNameValuePair("rejectFake", "clickCount=7&subBtnClick=0&keyPress=43&menuClick=0&mouseMove=732&checkcode=0&subBtnPosx=545&subBtnPosy=240&subBtnDelay=94&keycode=0,0,0,0,0,0,0,0,0,0,0,0,0,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,0&winWidth=1366&winHeight=336&userAgent=Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/17.0 Firefox/17.0")); 
		
		
		//拉取验证码
		PinCode pincode = new PinCode(sinaId, regtime);
		// 若拉取验证码失败（内部已经三次失败），
		if(!pincode.loadPinCode()){
			Log.log.error("get pincode error!");
			return false;
		}
		
		formParams.add(new BasicNameValuePair("pincode", pincode.getPincode())); 
		Log.log.debug("pincode -> "+pincode.getPincode());
		
		// 发送表单
		httpPost = new HttpPost("http://www.weibo.com/signup/v5/reg");
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			Log.log.error(e.getMessage(), e);
			return false;
		}
		
		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			Log.log.error(e.getMessage(), e);
			return false;
		} catch (IOException e) {
			Log.log.error(e.getMessage(), e);
			return false;
		}
		
		httpEntity = httpResponse.getEntity();
		
		
		// 分析最终返回值
		String register_response = null;
		try {
			register_response = HtmlTools.getHtmlByBr(httpEntity);
		} catch (UnsupportedEncodingException e) {
			Log.log.error(e.getMessage(), e);
			return false;
		} catch (IllegalStateException e) {
			Log.log.error(e.getMessage(), e);
			return false;
		} catch (IOException e) {
			Log.log.error(e.getMessage(), e);
			return false;
		}
		
		Log.log.debug("final response -> "+register_response);
		
		JSONObject json = null;
		try {
			json = new JSONObject(register_response);
		} catch (JSONException e) {
			Log.log.error(e.getMessage(), e);
			return false;
		}
		
		if(json == null){
			Log.log.error("parse json error!");
			return false;
		}
		
		if(json.has("code")){
			
			try {
				int code = json.getInt("code");
				
				switch (code) {
				case 100000:
					// 注册成功
					registerService.getWaitActivationData().add(account);
					Log.log.debug("【Register Success】 "+account.toString());
					
					
					break;
				case 600001:
					// 验证码错误，或账号昵称重复
					registerService.getFaildData().add(account);
					
					String msg = json.getJSONObject("data").getJSONObject("verifycode").getString("msg");
					
					if(msg != null && msg.equals("\u9a8c\u8bc1\u7801\u8f93\u5165\u6709\u8bef")){
						pincode.ReportError(pincode.getAnthor());
					}
					Log.log.debug("【Register Faild】 Pincode Error or other.");
					
					
					break;
				case 100001:
					// 该IP注册次数过多被墙了
					registerService.getFaildData().add(account);
					proxyService.getBlockData().add(proxy);
					Log.log.debug("【Register Faild】 proxy ip Blocked. "+proxy);
					break;
				default:
					break;
				}
				
				
				
			} catch (JSONException e) {
				Log.log.error(e.getMessage(), e);
				return false;
			}
			
			
			
		}else{
			Log.log.error("json is not 'code' fiald!");
			return false;
		}
		
		
		
		long endTime = System.currentTimeMillis();
		
		
		Log.log.debug("用时 "+ (endTime - startTime)+" ms");
		
		return true;
	}
	
	
	

}
