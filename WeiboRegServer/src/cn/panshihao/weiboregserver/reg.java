package cn.panshihao.weiboregserver;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
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
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;



public class reg {

	
	
	
	public static void main(String[] args) throws IOException {
		
		/**
		 * 注册逻辑：
		 * 1.首先在VPN环境下访问 http://weibo.com/signup/mobile.php;
		 * 2.从该页面上得到各种表单项;
		 * 3.拉取验证码图片进行打码验证
		 * 4.发送注册表单
		 */
		PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
		connectionManager.setMaxTotal(2000);
		connectionManager.setDefaultMaxPerRoute(1000);
		
		
		// 访问 http://weibo.com/signup/mobile.php;
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
		headerList.add(new BasicHeader("Host", "weibo.com"));
		headerList.add(new BasicHeader("Origin", "http://weibo.com"));
		headerList.add(new BasicHeader("Referer", "http://weibo.com/signup/mobile.php"));
		
		
		
		httpClient.getParams().setParameter(ClientPNames.DEFAULT_HEADERS, headerList);
		//连接超时、sockete超时和从connectionmanager中获取connection的超时设置，计算单位都是微秒；
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(30000)); 
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  new Integer(30000) ); 
		httpClient.getParams().setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, new Long(30000)); // second;
		//伪装成Firefox
		httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/17.0 Firefox/17.0");
		
		
		
		HttpPost httpPost = new HttpPost("http://weibo.com/signup/mobile.php");
		
		HttpResponse httpResponse = httpClient.execute(httpPost);
		
		HttpEntity httpEntity = httpResponse.getEntity();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(httpEntity.getContent(), "UTF-8"));
		
		String result = "";
		String item = "";
		while((item = in.readLine()) != null){
			result += item.trim()+"\n";
		}
		
		
		// 从该页面上得到各种表单项
		/*
		 * 12个随机码
		 * appsrc			{input hidden}
		 * backurl			{input hidden}
  		 * callback			{input hidden}
  		 * inviteCode		{input hidden}
  		 * invitesource		{input hidden}
  		 * lang				{input hidden}
  		 * mbk				{input hidden}
  		 * mcode			{input hidden}
  		 * nickname			{input text *昵称}
  		 * page				{input hidden}		
  		 * passport			{input text 护照}
  		 * passwd			{input text *密码}
  		 * pincode			{input text *验证码}
  		 * realname_PP		{input text 姓名}
  		 * regtime			{input hidden}
  		 * rejectFake		{default clickCount=7&subBtnClick=0&keyPress=43&menuClick=0&mouseMove=732&checkcode=0&subBtnPosx=545&subBtnPosy=240&subBtnDelay=94&keycode=0,0,0,0,0,0,0,0,0,0,0,0,0,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,0&winWidth=1366&winHeight=336&userAgent=Mozilla/5.0 (Windows NT 6.2; WOW64; rv:18.0) Gecko/20100101 Firefox/18.0}
  		 * salttime			{input hidden}
  		 * showlogo			{input hidden}
  		 * sinaid			{input hidden}
  		 * username			{input text *邮箱}
  		 * 
		 */
		
		
		System.out.println("已获取到 html");
		
		Document doc = Jsoup.parse(result);
		
		
		Elements elements = doc.getElementsByAttributeValue("type", "hidden");
		
		
//		// 发送验证用户名的请求
//		httpPost = new HttpPost("http://weibo.com/signup/v5/formcheck?type=email&value="+URLEncoder.encode("yun@uhomeu.com")+"&__rnd="+System.currentTimeMillis());
//		httpClient.execute(httpPost);
//		
//		// 发送验证昵称的请求
//		httpPost = new HttpPost("http://weibo.com/signup/v5/formcheck?type=nickname&value=panshihaoooo2&__rnd="+System.currentTimeMillis());
//		httpClient.execute(httpPost);
		
		
		
		
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
			System.out.println("name -> "+name+" ,value -> "+value);
		}
		formParams.add(new BasicNameValuePair("nickname", "panshihaoooo2")); 
		formParams.add(new BasicNameValuePair("passwd", "caicai520")); 
		formParams.add(new BasicNameValuePair("username", "yun@uhomeu.com")); 
		formParams.add(new BasicNameValuePair("rejectFake", "clickCount=7&subBtnClick=0&keyPress=43&menuClick=0&mouseMove=732&checkcode=0&subBtnPosx=545&subBtnPosy=240&subBtnDelay=94&keycode=0,0,0,0,0,0,0,0,0,0,0,0,0,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,0&winWidth=1366&winHeight=336&userAgent=Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/17.0 Firefox/17.0")); 
		
		
		// 拉取验证码
		
		URL url = new URL("http://weibo.com/signup/v5/pincode/pincode.php?lang=zh&sinaId="+sinaId+"&r="+regtime+"");
		
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		
		InputStream pincodeIn = url.openStream();
		byte[] readByte = new byte[1024];
		int readCount = -1;
		
		while((readCount = pincodeIn.read(readByte, 0, 1024)) != -1){
			byteOut.write(readByte);
		}
		
		String pincode = FastVerCode.INSTANCE.RecByte(byteOut.toByteArray(), url.openConnection().getContentLength(), "psh24053", "2227976");
		formParams.add(new BasicNameValuePair("pincode", pincode)); 
		
		System.out.println("已获取到验证码 -> "+pincode);
		
		
		
		
		// 发送表单
		httpPost = new HttpPost("http://weibo.com/signup/v5/reg");
		httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
		
		System.out.println("requestLine -> "+httpPost.getRequestLine());
		
		httpResponse = httpClient.execute(httpPost);
		
		httpEntity = httpResponse.getEntity();
		
		BufferedReader endIn = new BufferedReader(new InputStreamReader(httpEntity.getContent(),"UTF-8"));
		
		
		String tempStr = "";
		String tempMain = "";
		while((tempStr = endIn.readLine()) != null){
			tempMain += tempStr;
		}
		
		
		
		System.out.println("最终返回值 -> "+tempMain);
		
	}
	
	
	
	
}
