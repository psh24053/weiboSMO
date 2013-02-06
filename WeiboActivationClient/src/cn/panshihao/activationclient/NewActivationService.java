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
		
		toLocation(httpClient, url, 0);
		
		
		
		return;
	}
	
	public static String toLocation(HttpClient httpClient, String url, int index){
		
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
			
			try {
				System.out.println(HtmlTools.getHtmlByBr(httpResponse.getEntity()));
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
		
		return toLocation(httpClient, activeUrl, index);
	}
	
	
	
	public static void main(String[] args) {
		
//		proxyService = new ProxyService();
//		proxyService.loadProxyData();
		
//		runActivation(37997, "2c6fc662e5@ksgym.com", "http://weibo.com/signup/v5/active?username=2c6fc662e5@ksgym.com&rand=a50cd08540d41c6163f80d06b69142df&sinaid=314de8dfe18a60cf69c83d7295716b2e&inviteCode=&invitesource=0&lang=zh-cn&entry=&backurl=", null);
		
		 BufferedReader m_reader;  
	        OutputStreamWriter m_writer;  
	        TelnetClient m_telnetClient = new TelnetClient();  
	        try {  
	            //设置Telnet超时  
	            m_telnetClient.setDefaultTimeout(100000);  
	            //设置Telnet服务器地址及端口  
	            m_telnetClient.connect("uhomeu.com", 4555);  
	            //创建读取缓冲对象  
	            m_reader = new BufferedReader(new InputStreamReader(m_telnetClient  
	                    .getInputStream()));  
	            //创建用于发送Telnet命令对象  
	            m_writer = new OutputStreamWriter(m_telnetClient.getOutputStream());  
	            //不断接收登陆成功的信号，超时抛出异常，则跳至一下条代码执行  
	            try {  
	                for (;;) {  
	                    System.out.println(m_reader.readLine());  
	                    if(m_reader.readLine() != null){
	                    	break;
	                    }
	                }  
	            } catch (Exception e) {  
	            	System.out.println("..");
	            }  
	            //输入James服务器用户名,此为管理员用户名，而非普通用户，默认为root  
	            m_writer.write("psh24053" + LINE_SEPARATOR);  
	            m_writer.flush();  
	            System.out.println(m_reader.readLine());  
	            //输入root用户密码  
	            m_writer.write("caicai520" + LINE_SEPARATOR);  
	            m_writer.flush();  
	            System.out.println(m_reader.readLine());  
	            //输入Telnet命令添加用户  
	            m_writer.write("adduser helloman 881213" + LINE_SEPARATOR);  
	            m_writer.flush();  
	            System.out.println(m_reader.readLine());  
	            //输出用户列表  
	            m_writer.write("listusers" + LINE_SEPARATOR);  
	            m_writer.flush();  
	            //显示用户列表  
	            try {  
	                for (;;) {  
	                    System.out.println(m_reader.readLine());  
	                    if(m_reader.readLine() != null){
	                    	break;
	                    }
	                }  
	            } catch (Exception e) {  
	            }  
	        } catch (SocketException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        }  
	}
	public static final String LINE_SEPARATOR = System.getProperties()  
            .getProperty("line.separator");  
	
}
