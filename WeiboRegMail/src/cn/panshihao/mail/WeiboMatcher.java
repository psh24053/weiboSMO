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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
				
				insertDB(recipient.getUser()+"@"+DOMAIN, url);
				
//				connectURL(url);
//				updateDB(recipient.getUser()+"@"+DOMAIN);
			}
			
			
			
			
		}
		
		
		
		return null;
	}
	/**
	 * 插入
	 * @param email
	 * @param url
	 */
	public void insertDB(String email, String url){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int aid = -1;
		try {
			conn = SQLConn.db.getConnection();
			
			pstmt = conn.prepareStatement("select aid from wb_account where email = ?");
			pstmt.setString(1, email);
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				aid = rs.getInt("aid");
			}else{
				return;
			}
			
			
			
			pstmt = conn.prepareStatement("insert into wb_activation(aid,email,url,status) values(?,?,?,?)");
			
			pstmt.setInt(1, aid);
			pstmt.setString(2, email);
			pstmt.setString(3, url);
			pstmt.setInt(4, 0);
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
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
		
		
		System.out.println("已插入数据库   aid: "+aid+" ,email: "+email);
	}
	
	
	/**
	 * 点击Url
	 * @param url
	 */
	public void connectURL(String url){
		
		
		Map<String, String> params = URLRequest(url);
		
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		
		for(String key : params.keySet()){
			System.out.println("key: "+key+" ,value: "+params.get(key));
			formParams.add(new BasicNameValuePair(key, params.get(key)));
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
		httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY); //
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
		
		System.out.println(HtmlTools.getHtmlByBr(httpResponse.getEntity()).length());
		
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
	/**
     * 去掉url中的路径，留下请求参数部分
     * @param strURL url地址
     * @return url请求参数部分
     */
    private static String TruncateUrlPage(String strURL)
    {
    String strAllParam=null;
      String[] arrSplit=null;
      
      strURL=strURL.trim().toLowerCase();
      
      arrSplit=strURL.split("[?]");
      if(strURL.length()>1)
      {
          if(arrSplit.length>1)
          {
                  if(arrSplit[1]!=null)
                  {
                  strAllParam=arrSplit[1];
                  }
          }
      }
      
    return strAllParam;    
    }
	/**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     * @param URL  url地址
     * @return  url请求参数部分
     */
    public static Map<String, String> URLRequest(String URL)
    {
    Map<String, String> mapRequest = new HashMap<String, String>();
    
      String[] arrSplit=null;
      
    String strUrlParam= TruncateUrlPage(URL);
    if(strUrlParam==null)
    {
        return mapRequest;
    }
      //每个键值为一组
    arrSplit=strUrlParam.split("[&]");
    for(String strSplit:arrSplit)
    {
          String[] arrSplitEqual=null;          
          arrSplitEqual= strSplit.split("[=]"); 
          
          //解析出键值
          if(arrSplitEqual.length>1)
          {
              //正确解析
              mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
              
          }
          else
          {
              if(arrSplitEqual[0]!="")
              {
              //只有参数没有值，不加入
              mapRequest.put(arrSplitEqual[0], "");        
              }
          }
    }    
    return mapRequest;    
    }

}
