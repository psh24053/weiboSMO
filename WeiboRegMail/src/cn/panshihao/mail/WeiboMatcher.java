package cn.panshihao.mail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
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
import org.apache.http.cookie.params.CookieSpecPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.james.transport.matchers.GenericRegexMatcher;
import org.apache.mailet.GenericMatcher;
import org.apache.mailet.GenericRecipientMatcher;
import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class WeiboMatcher extends GenericMatcher {

	public static final String DOMAIN = "uhomeu.com";
	
	@Override
	public Collection match(Mail mail) throws MessagingException {
		
		
		
		
		//必须是来自service.weibo.com邮件才接收
		if(mail.getSender().getHost().equals("service.weibo.com")){
			ReciveMail reciveMail = new ReciveMail(mail.getMessage());
			try {
				reciveMail.getMailContent(mail.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(reciveMail.getBodyText().indexOf("48小时") != -1){
				
				Document doc = Jsoup.parse(reciveMail.getBodyText());
				
				Elements elements = doc.select("a");
				
				String url = elements.get(0).attr("href");
				
				MailAddress recipient = (MailAddress) mail.getRecipients().toArray()[0];
				
				System.out.println(url);
				
				connectURL(url);
//				updateDB(recipient.getUser()+"@"+DOMAIN);
			}
			
			
			
			
		}
		
		
		
		return null;
	}
	/**
	 * 点击Url
	 * @param url
	 */
	public void connectURL(String url){
		
		String[] params = url.split("?")[1].split("&");
		
		System.out.println(params.length);
		
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		
		for(int i = 0 ; i < params.length ; i ++){
			String[] item = params[i].split("=");
			
			String key = item[0];
			String value = item.length > 1 ? item[1] : "";
			
			formParams.add(new BasicNameValuePair(key, value));
		}
		
		
		
		
		/**
		 * 创建多线程连接器
		 */
		PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
		connectionManager.setMaxTotal(2000);
		connectionManager.setDefaultMaxPerRoute(1000);
		
		HttpClient httpClient = new DefaultHttpClient(connectionManager);
		
		//伪装成Firefox 5, 
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1); 
		httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.IGNORE_COOKIES); //
		httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,"UTF-8"); //这个是和目标网站的编码有关；
		httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_ELEMENT_CHARSET,"UTF-8"); 
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(30000)); 
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  new Integer(30000) ); 
		httpClient.getParams().setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, new Long(30000)); // second;
		httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/17.0 Firefox/17.0");
		
		
        // 请求注册界面，获取表单必须参数
        HttpPost httpPost = new HttpPost("http://weibo.com/signup/v5/active");
        try {
			httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
//			Log.log.error(e.getMessage(), e);
		} catch (IOException e) {
//			Log.log.error(e.getMessage(), e);
		}
		
		
		
		
		
		try {
			System.out.println(toLocation(httpClient, httpResponse));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println("已点击连接 "+url);
		
	}
	
	/**
	 * 手动location，并且一路上手动设置cookie
	 * @param httpClient
	 * @param httpResponse
	 * @return
	 * @throws IOException 
	 * @throws IllegalStateException 
	 * @throws UnsupportedEncodingException 
	 */
	public String toLocation(HttpClient httpClient, HttpResponse httpResponse) throws UnsupportedEncodingException, IllegalStateException, IOException{
		Header[] headers = httpResponse.getAllHeaders();
		
		System.out.println(HtmlTools.getHtmlByBr(httpResponse.getEntity()));
		
		String location = null;
		
		for(int i = 0 ; i < headers.length ; i ++){
			String name = headers[i].getName();
			String value = headers[i].getValue();
			
			System.out.println("name: "+name+" ,value: "+value);
			
			if(name.equals("Location")){
				location = value;
			}
			if(name.equals("Set-Cookie")){
				CookieStore cookieStore = ((DefaultHttpClient)httpClient).getCookieStore();
				
				String[] cookies = value.split(";");
				
				BasicClientCookie cookieClient = null;
				
				for(int j = 0 ; j < cookies.length ; j ++){
					String[] cookie = cookies[j].trim().split("=");
					if(j == 0){
						cookieClient = new BasicClientCookie(cookie[0], cookie[1]);
					}
					
					if(cookie[0].equals("expires")){
						cookieClient.setExpiryDate(new Date(cookie[1]));
					}
					if(cookie[0].equals("path")){
						cookieClient.setPath(cookie[1]);
					}
					if(cookie[0].equals("domain")){
						cookieClient.setDomain(cookie[1]);
					}
					
				}
				if(cookieClient != null){
					cookieStore.addCookie(cookieClient);
					((DefaultHttpClient)httpClient).setCookieStore(cookieStore);
				}
			}
			
		}
		
		if(location == null){
			
			return HtmlTools.getHtmlByBr(httpResponse.getEntity());
			
		}else{
			toLocation(httpClient, httpClient.execute(new HttpGet("http://weibo.com"+location)));
		}
		
		return null;
		
	}
	
	
	/**
	 * 更新数据库
	 * @param email
	 */
	public void updateDB(String email){
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = SQLConn.db.getConnection();
			pstmt = conn.prepareStatement("update wb_account set status = 1 where email = ?");
			
			pstmt.setString(1, email);
			
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
		
		System.out.println("已更新数据库 "+email);
		
	}
	

	public String getContent(InputStream input, String encoding){
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(input, encoding));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String temp = "";
		String result = "";
		
		try {
			while((temp = in.readLine()) != null){
				result += temp.trim() + "\n";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	


}
