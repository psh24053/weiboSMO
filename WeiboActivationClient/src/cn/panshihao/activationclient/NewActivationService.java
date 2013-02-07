package cn.panshihao.activationclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;

import cn.panshihao.activationclient.mail.MailSenderInfo;
import cn.panshihao.activationclient.mail.SimpleMailSender;

public class NewActivationService {

	public static ProxyService proxyService;
	/**
	 * 开始激活
	 * @param aid
	 * @param email
	 * @param url
	 */
	public static void runActivation(int aid, String email, String url, wb_proxyModel proxy){
		
		PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
		connectionManager.setMaxTotal(2000);
		connectionManager.setDefaultMaxPerRoute(1000);
		
		HttpClient httpClient = new DefaultHttpClient(connectionManager);
		
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

		httpClient.getParams().setParameter(ClientPNames.DEFAULT_HEADERS, headerList);
		httpClient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
		
		String result = toLocation(httpClient, url, email, 0);
		
		if(result == null){
			System.out.println("注册失败");
			return;
		}
		
		if(result.equals("{SendMail}")){
			// 激活需要发送一封邮件到新浪才能完成
			System.out.println("已经发送一封邮件到新浪");
		}
		
		
		
		return;
	}
	
	public static String toLocation(HttpClient httpClient, String url, String email, int index){
		
		index ++;
		
		// 准备开始访问激活连接
		HttpGet httpGet = new HttpGet(url);
		
		HttpResponse httpResponse = null;
		
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
			System.out.println("访问激活连接失败，更换代理重试");
//					proxyService.getTimeOutData().add(proxy);
//					runActivation(aid, email, url, proxyService.getRandomProxyModel());
			return null;
		}
		
		// 第一次访问激活连接，从响应中取得location
		Header location = httpResponse.getFirstHeader("Location");
		
		if(location == null){
			System.out.println("访问激活连接失败，可能是跳转到其他地方去？");
			
			String responseString = null;
			
			try {
				responseString = HtmlTools.getHtmlByBr(httpResponse.getEntity());
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
			
			if(responseString == null){
				return null;
			}
			
			// 如果包含这个字符串，表示这个账号激活出现问题，需要发一个邮件到新浪的邮箱去验证
			if(responseString.contains("发任意内容邮件")){
				return toSendMail(httpClient, url, email, index);
			}
			
			
			return null;
		}
		
		
		String activeUrl = location.getValue();
		System.out.println("["+index+"] "+activeUrl);
		
		if(activeUrl == null){
			System.out.println("这次激活是失败的，原因很可能是账号被停封");
			return null;
		}
		// 判断Url是否包含http
		if(!(activeUrl.charAt(0) == 'h' || activeUrl.charAt(0) == 'H')){
			activeUrl = "http://weibo.com" + activeUrl;
		}
		
		return toLocation(httpClient, activeUrl, email, index);
	}
	
	public static String toSendMail(HttpClient httpClient, String url, String email, int index){
		
		   //这个类主要是设置邮件   
	      MailSenderInfo mailInfo = new MailSenderInfo();    
	      mailInfo.setMailServerHost("ksgym.com");    
	      mailInfo.setMailServerPort("25");    
	      mailInfo.setValidate(true);    
	      mailInfo.setUserName(email.substring(0, email.indexOf("@")));    
	      mailInfo.setPassword(email.substring(0, email.indexOf("@")));//您的邮箱密码    
	      mailInfo.setFromAddress(email);    
	      mailInfo.setToAddress("ixgsoft@163.com");    
	      mailInfo.setSubject(RandomString.getMD5(System.currentTimeMillis()+""));    
	      mailInfo.setContent("激活我的账号啊" + RandomString.getMD5(System.currentTimeMillis()+""));    
	         //这个类主要来发送邮件   
	      SimpleMailSender sms = new SimpleMailSender();   
	      sms.sendTextMail(mailInfo);//发送文体格式    
		
		return "{SendMail}";
	}
	
	
	
	public static void main(String[] args) {
		
		runActivation(37997, "2c6fc662e5@ksgym.com", "http://weibo.com/signup/v5/active?username=2c6fc662e5@ksgym.com&rand=a50cd08540d41c6163f80d06b69142df&sinaid=314de8dfe18a60cf69c83d7295716b2e&inviteCode=&invitesource=0&lang=zh-cn&entry=&backurl=", null);
		
		
	}
}
