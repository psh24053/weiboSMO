package cn.panshihao.weiboactivation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
import org.apache.http.client.params.CookiePolicy;
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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ActivationService extends Thread {

	
	private int aid;
	private String email;
	private String url;
	private long start;
	
	
	public ActivationService(int aid, String email, String url){
		this.aid = aid;
		this.email = email;
		this.url = url;
	}
	
	@Override
	public void run() {
		
		start = System.currentTimeMillis();
		
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
		
		
		HttpGet httpGet = new HttpGet(url);
		
		HttpResponse httpResponse = null;
		
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			return;
		} catch (IOException e) {
			return;
		}
		
		// 第一次访问的location
		Header locationHeader = httpResponse.getFirstHeader("Location");
		System.out.println("name: "+locationHeader.getName()+" , value: "+locationHeader.getValue());
		
		String firstUrl = locationHeader.getValue();
		
		httpGet = new HttpGet("http://www.weibo.com"+locationHeader.getValue());
		httpGet.addHeader("Referer", url);
		
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			return;
		} catch (IOException e) {
			return;
		}
		
		// 第二次访问的location
		locationHeader = httpResponse.getFirstHeader("Location");
		System.out.println("name: "+locationHeader.getName()+" , value: "+locationHeader.getValue());
		
		String secondUrl = locationHeader.getValue();
		
		httpGet = new HttpGet(locationHeader.getValue());
		httpGet.addHeader("Referer", "http://www.weibo.com"+firstUrl);
		System.out.println("Referer url -> http://www.weibo.com"+firstUrl);
		
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			return;
		} catch (IOException e) {
			return;
		}
		
		// 第三次访问的location
		locationHeader = httpResponse.getFirstHeader("Location");
		
			
		String html = null;
		
		try {
			html = HtmlTools.getHtmlByBr(httpResponse.getEntity());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		if(html != null){
			System.out.println(html);
			
			Document doc = Jsoup.parse(html);
			
			Elements e = doc.getElementsByTag("script");
			
			Element element = e.get(0);
			
			String content = element.html().trim();
			
			String replaceUrl = content.substring(18, content.length() - 3);
			
			httpGet = new HttpGet(replaceUrl);
			httpGet.addHeader("Referer", secondUrl);
			
			try {
				httpResponse = httpClient.execute(httpGet);
			} catch (ClientProtocolException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			String ssoHtml = null;
			
			try {
				ssoHtml = HtmlTools.getHtmlByBr(httpResponse.getEntity());
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			System.out.println(ssoHtml);
			
			if(ssoHtml != null){
				
				Document ssoDoc = Jsoup.parse(ssoHtml);
				
				Elements scripts = ssoDoc.getElementsByTag("script");
				
				Element ssoE = scripts.get(1);
				
				String ssoJS = ssoE.html();
				
				String ssoUrl = ssoJS.substring(ssoJS.indexOf("location.replace")+18, ssoJS.lastIndexOf("'"));
				
				
				httpGet = new HttpGet(ssoUrl);
				httpGet.addHeader("Referer", replaceUrl);
				
				try {
					httpResponse = httpClient.execute(httpGet);
				} catch (ClientProtocolException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				Header locat = httpResponse.getFirstHeader("Location");
				
				String ssoLocation = locat.getValue();
				
				System.out.println(locat.getValue());
				
				httpGet = new HttpGet(ssoLocation);
				httpGet.setHeader("Referer", ssoUrl);
				
				try {
					httpResponse = httpClient.execute(httpGet);
				} catch (ClientProtocolException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				
				String finalHtml = null;
				try {
					finalHtml = HtmlTools.getHtmlByBr(httpResponse.getEntity());
				} catch (UnsupportedEncodingException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (IllegalStateException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				if(updateUid(aid, finalHtml)){
					
					writeInfo(httpClient, finalHtml, ssoLocation, aid);
					
				}
					
				
				
				
				
			}
			
			
		}
			
		
		
		
		
		
		
		
	}
	/**
	 * 更新Uid
	 * @param aid
	 * @param html
	 * @return
	 */
	public boolean updateUid(int aid,String html){
		
		System.out.println("updateUid "+aid);
		
		Document doc = Jsoup.parse(html);
		Element el = doc.getElementsByTag("script").get(0);
		
		String eHtml = el.html();
		
		String jsonString = eHtml.substring(eHtml.indexOf("{"));
		
		JSONObject json = null;
		try {
			json = new JSONObject(jsonString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String domain = null;
		long uid = -1;
		try {
			domain = json.getString("domain");
			uid = json.getLong("uid");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Connection conn = Tools.getMysqlConn();
		if(conn == null){
			return false;
		}
		PreparedStatement pstmt = null;
		int result = -1;
		try {
			pstmt = conn.prepareStatement("update wb_account set uid = ? , domain = ? , status = 1 where aid = ?");
			pstmt.setLong(1, uid);
			pstmt.setString(2, domain);
			pstmt.setInt(3, aid);
			
			result = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(result == -1){
			return false;
		}
		
		Tools.log.debug("update Uid Success: "+uid);
		
		return true;
	}
	/**
	 * 填写资料信息
	 * @param httpClient
	 * @return
	 */
	public boolean writeInfo(HttpClient httpClient, String html, String referer, int aid){
		
		String url = "http://weibo.com/nguide/aj/register?__rnd="+System.currentTimeMillis();
		
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("Referer", referer);
		List<NameValuePair> formlist = new ArrayList<NameValuePair>();
		
		Document doc = Jsoup.parse(html);
		
		Elements e = doc.select("input[name=password],input[name=time]");
		
		String password = e.select("[name=password]").val();
		String time = e.select("[name=time]").val();
		
		Element valueScript = doc.select("script").get(1);
		
		String configHtml = valueScript.html();
		String[] str = configHtml.split(";");
		String city = str[0].substring(str[0].indexOf("'")+1,str[0].length() - 1);
		String province = str[1].substring(str[1].indexOf("'")+1,str[1].length() - 1);

		
		formlist.add(new BasicNameValuePair("_t", "0"));
		formlist.add(new BasicNameValuePair("company", ""));
		formlist.add(new BasicNameValuePair("company_visible", "0"));
		formlist.add(new BasicNameValuePair("department", "其他"));
		formlist.add(new BasicNameValuePair("department_id", "-1"));
		formlist.add(new BasicNameValuePair("gender", "m"));
		formlist.add(new BasicNameValuePair("msn", ""));
		formlist.add(new BasicNameValuePair("msn_visible", "1"));
		formlist.add(new BasicNameValuePair("password", password));
		formlist.add(new BasicNameValuePair("province", province));
		formlist.add(new BasicNameValuePair("qq", ""));
		formlist.add(new BasicNameValuePair("qq_visible", "1"));
		formlist.add(new BasicNameValuePair("school_id", "250623"));
		formlist.add(new BasicNameValuePair("school_name", "中国科学院研究生院"));
		formlist.add(new BasicNameValuePair("school_type", ""));
		formlist.add(new BasicNameValuePair("school_visible", "0"));
		formlist.add(new BasicNameValuePair("school_year", "2010"));
		formlist.add(new BasicNameValuePair("single", "2"));
		formlist.add(new BasicNameValuePair("single_visible", "1"));
		formlist.add(new BasicNameValuePair("time", time));
		formlist.add(new BasicNameValuePair("city", city));
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(formlist, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		HttpResponse httpResponse = null;
		
		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String nguide_1_html = null;
		try {
			nguide_1_html = HtmlTools.getHtmlByBr(httpResponse.getEntity());
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IllegalStateException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		JSONObject nguide_1_json = null;
		if(nguide_1_html != null){
			try {
				nguide_1_json = new JSONObject(nguide_1_html);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		int code = -1;
		if(nguide_1_json != null){
			try {
				code = nguide_1_json.getInt("code");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		switch (code) {
		case 100000:
			// 第一步操作完成
			String data = null;
			try {
				data = nguide_1_json.getString("data");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			httpPost = new HttpPost(data.replace("\\", ""));
			httpPost.addHeader("Referer", url);
			
			
			try {
				httpResponse = httpClient.execute(httpPost);
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String data_2 = "http://weibo.com/nguide/aj/stepstatus/relationstep?_t=0&__rnd="+System.currentTimeMillis();
			
			//第二步
			httpPost = new HttpPost(data_2);
			httpPost.addHeader("Referer", data.replace("\\", ""));
			
			try {
				httpResponse = httpClient.execute(httpPost);
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String data_3 = "http://weibo.com/nguide/aj/finish?num=0&interestnum=0&interesttype=1&user_tag=0&_t=0&__rnd="+System.currentTimeMillis();
			
			//第三步
			httpPost = new HttpPost(data_3);
			httpPost.addHeader("Referer", data_2);
			
			try {
				httpResponse = httpClient.execute(httpPost);
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			//第四步
			String data_4 = "http://weibo.com/?uut=fin&from=reg";
			httpPost = new HttpPost(data_4);
			httpPost.addHeader("Referer", data_3);
			
			try {
				httpResponse = httpClient.execute(httpPost);
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			Header location = httpResponse.getFirstHeader("Location");
			
			if(location != null){
				String data_5 = "http://www.weibo.com"+location.getValue();
				
				httpPost = new HttpPost(data_5);
				httpPost.addHeader("Referer", data_4);
				
				try {
					httpResponse = httpClient.execute(httpPost);
				} catch (ClientProtocolException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				try {
					System.out.println("data_5 : "+data_5);
					System.out.println(HtmlTools.getHtmlByBr(httpResponse.getEntity()));
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IllegalStateException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			
			
			break;

		default:
			break;
		}
		
		httpClient.getConnectionManager().shutdown();
		
		Connection conn = Tools.getMysqlConn();
		
		if(conn != null){
			
			try {
				PreparedStatement pstmt = conn.prepareStatement("update wb_account set status = 2 where aid = ?");
				pstmt.setInt(1, aid);
				
				pstmt.executeUpdate();
				
				
				pstmt = conn.prepareStatement("update wb_activation set status = 1 where aid = ?");
				pstmt.setInt(1, aid);
				
				pstmt.executeUpdate();
				
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
				try {
					conn.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		}
		
		Tools.log.debug("Activation Success! aid -> "+aid+" , 耗时 "+(System.currentTimeMillis() - start)+" ms");
		
		return true;
	}
	
	public static void main(String[] args) throws IOException {
		
		new ActivationService(15, "ae7e7694cf@uhomeu.com", "http://weibo.com/signup/v5/active?username=ae7e7694cf@uhomeu.com&rand=b34a0537b73151bd19cfafaa80f7e31a&sinaid=d9d11f830d5283741e8ea9c3b27af129&inviteCode=&invitesource=0&lang=zh-cn&entry=&backurl=").start();
		
		
//		System.out.println(testHtml());
		
	}
	
	public static String testHtml() throws IOException{
		File file = new File("d:\\html.txt");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		
		String temp = "";
		String html = "";
		
		while((temp = reader.readLine()) != null){
			html += temp;
		}
		
		Document doc = Jsoup.parse(html);
		
		Elements e = doc.select("input[name=password],input[name=time]");
		
		System.out.println(e.select("[name=password]").val().trim());
		
		Element valueScript = doc.select("script").get(1);
		
		String configHtml = valueScript.html();
		String[] str = configHtml.split(";");
		System.out.println(str[0].substring(str[0].indexOf("'")+1,str[0].length() - 1));
		System.out.println(str[1].substring(str[1].indexOf("'")+1,str[1].length() - 1));
		
		
		return e.get(0).val();
	}

}
