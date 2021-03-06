package cn.panshihao.activationclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.Header;
import org.apache.http.HttpHost;
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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;




public class ActivationService {

	private ExecutorService executorService;
	private ProxyService proxyService;
	private HttpClient httpClient;
	private int complete = 0;
	public static void main(String[] args) {
		ActivationService service = new ActivationService();
		service.startActivation();
		
		
		
	}
	/**
	 * 开始激活
	 */
	public void startActivation(){
		proxyService = new ProxyService();
		proxyService.loadProxyData();
		System.out.println("Load Proxy Data Complete! "+proxyService.getProxyData().size());
		executorService = Executors.newFixedThreadPool(10);
		final wb_activationDAO dao = new wb_activationDAO();
		/*
		 * 激活逻辑：
		 * 1.从wb_activation表中找出status为0的记录；
		 * 2.访问激活url，根据状态来完成操作
		 * 
		 * wb_Activation:
		 * status为0，代表未注册状态（多半是刚接收到注册邮件的，也有可能是之前版本遗留的已激活类型）
		 * status为1，代表已激活状态，等待设置基本资料（也包含大部分已经注册成功的）
		 * status为2，代表注册完成
		 * status为3，代表激活失败
		 * status为4，代表未知状态（可能是已激活，也可能是激活失败）
		 * 
		 */
		
		
		List<wb_activationModel> data = dao.selectActivation();
		System.out.println("Load ActivationModel "+data.size());
		
		
		
		while(true){
			
			// 当目标数量已经与完成数量相等，则重新开始逻辑
			if(data != null && complete == data.size() && data.size() > 0){
				complete = 0;
				data = dao.selectActivation();
				
			}
			
			if(complete == 0){
				// 循环
				for(int i = 0 ; i < data.size() ; i ++){
					final wb_activationModel model = data.get(i);
					// 启动线程
					executorService.execute(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							System.out.println("run "+model);
							ProxyBean proxy = proxyService.getRandomProxyModel();
							
							// 首先执行注册URL点击
							String html = runActivation(model, proxy);
							
							if(html == null){
								//执行失败，将目标model的status设置为3
								model.setStatus(33);
								dao.update(model);
							}else{
								// 代表是验证账号错误，该种错误无法自动确定是否注册成功
								// 需要手动验证，将目标model的status设置为4
								if(html.equals("{Error:01}")){
									System.out.println(model.getAid()+" "+html);
									model.setStatus(44);
									dao.update(model);
								} else if(html.equals("{Reg:01}")){
									System.out.println(model.getAid()+" [3] Activation Success! wait Update Uid");
									model.setStatus(11);
									dao.update(model);
									
								} else {
									System.out.println(model.getAid()+" [6] html length "+html.length());
									if(updateUid(model.getAid(), html)){
										//执行填写资料操作
										if(runModifyInfo(model, proxy, httpClient, html)){
											System.out.println(model.getAid()+" [8] ModifyInfo Success!");
											model.setStatus(22);
											dao.update(model);
										}else{
											System.out.println(model.getAid()+" [8] ModifyInfo Error, but activation Success!");
											model.setStatus(11);
											dao.update(model);
										}
										
									}else{
										System.out.println(model.getAid()+" [7] Update Uid Error, but activation Success!");
										model.setStatus(11);
										dao.update(model);
										
									}
								}
								
							}
							
							
							
							
							proxyService.revertProxyModel(proxy, System.currentTimeMillis());
							plusComplete();
						}
					});
					

				}
			}
			
			
			
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	/**
	 * 报告完成
	 */
	public synchronized void plusComplete(){
		complete ++;
	}
	/**
	 * 更新uid和其他基本信息
	 * @param aid
	 * @param html
	 * @return
	 */
	public boolean updateUid(int aid, String html){
		Document doc = Jsoup.parse(html);
		
		Elements scripts = doc.getElementsByTag("script");
		
		if(scripts.size() == 0){
			return false;
		}
		
		Element el = scripts.get(0);
		
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
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = -1;
		try {
			conn = Tools.getMysqlConn();
			if(conn == null){
				return updateUid(aid, html);
			}
			pstmt = conn.prepareStatement("update wb_account set uid = ? , domain = ? , status = 1 where aid = ?");
			pstmt.setLong(1, uid);
			pstmt.setString(2, domain);
			pstmt.setInt(3, aid);
			
			result = pstmt.executeUpdate();
			
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
		
		if(result == -1){
			return false;
		}
		System.out.println(aid+" [7] Update Uid Success!");
		return true;
	}
	
	/**
	 * 执行激活的操作
	 * @param model
	 * @return
	 */
	public String runActivation(wb_activationModel model, ProxyBean proxy){
		
		PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
		connectionManager.setMaxTotal(2000);
		connectionManager.setDefaultMaxPerRoute(1000);
		
		httpClient = new DefaultHttpClient(connectionManager);
		
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
		
		if(proxy != null){
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(proxy.getIp(), proxy.getPort()));
		}
		
		httpClient.getParams().setParameter(ClientPNames.DEFAULT_HEADERS, headerList);
		httpClient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
		
		
		HttpGet httpGet = new HttpGet(model.getUrl());
		
		HttpResponse httpResponse = null;
		
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			httpClient.getConnectionManager().shutdown();
			proxyService.getTimeOutData().add(proxy);
			proxy = proxyService.getRandomProxyModel();
			return runActivation(model, proxy);
			
			
		} catch (IllegalStateException e){
			System.out.println(e.getMessage());
			httpClient.getConnectionManager().shutdown();
			proxyService.getTimeOutData().add(proxy);
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			httpClient.getConnectionManager().shutdown();
			proxyService.getTimeOutData().add(proxy);
			proxy = proxyService.getRandomProxyModel();
			return runActivation(model, proxy);
		}
		
		// 第一次访问的location
		Header locationHeader = httpResponse.getFirstHeader("Location");
		
		if(locationHeader == null){
			return null;
			
		}
		
		String firstUrl = locationHeader.getValue();
		
		System.out.println(model.getAid()+" [1] "+firstUrl);
		
		httpGet = new HttpGet("http://www.weibo.com"+locationHeader.getValue());
		httpGet.addHeader("Referer", model.getUrl());
		
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			httpClient.getConnectionManager().shutdown();
			proxyService.getTimeOutData().add(proxy);
			return null;
		} catch (IllegalStateException e){
			System.out.println(e.getMessage());
			httpClient.getConnectionManager().shutdown();
			proxyService.getTimeOutData().add(proxy);
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			httpClient.getConnectionManager().shutdown();
			proxyService.getTimeOutData().add(proxy);
			return null;
		}
		
		// 在这里失败，通常是网络错误，重新获取一个代理继续访问
		if(httpResponse == null){
			httpClient.getConnectionManager().shutdown();
			proxyService.getTimeOutData().add(proxy);
			proxy = proxyService.getRandomProxyModel();
			return runActivation(model, proxy);
		}
		
		// 第二次访问的location
		locationHeader = httpResponse.getFirstHeader("Location");
		
		if(locationHeader == null){
			return null;
		}
		
		String secondUrl = locationHeader.getValue();
		System.out.println(model.getAid()+" [2] "+secondUrl);
		
		// 包含error则代表这个账号验证错误，返回特殊标记
		if(secondUrl.indexOf("/signup/v5/error") != -1){
			return "{Error:01}";
		}
		
		
		httpGet = new HttpGet(locationHeader.getValue());
		httpGet.addHeader("Referer", "http://www.weibo.com"+firstUrl);
		
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			httpClient.getConnectionManager().shutdown();
			proxyService.getTimeOutData().add(proxy);
			proxy = proxyService.getRandomProxyModel();
			return runActivation(model, proxy);
		} catch (IllegalStateException e){
			httpClient.getConnectionManager().shutdown();
			proxyService.getTimeOutData().add(proxy);
			proxy = proxyService.getRandomProxyModel();
			return runActivation(model, proxy);
		} catch (IOException e) {
			httpClient.getConnectionManager().shutdown();
			proxyService.getTimeOutData().add(proxy);
			proxy = proxyService.getRandomProxyModel();
			return runActivation(model, proxy);
		}
		
			
		String html = null;
		
		try {
			html = HtmlTools.getHtmlByBr(httpResponse.getEntity());
		} catch (UnsupportedEncodingException e) {
			httpClient.getConnectionManager().shutdown();
			proxyService.getTimeOutData().add(proxy);
			proxy = proxyService.getRandomProxyModel();
			return runActivation(model, proxy);
		} catch (IllegalStateException e) {
			httpClient.getConnectionManager().shutdown();
			proxyService.getTimeOutData().add(proxy);
			proxy = proxyService.getRandomProxyModel();
			return runActivation(model, proxy);
		} catch (IOException e) {
			httpClient.getConnectionManager().shutdown();
			proxyService.getTimeOutData().add(proxy);
			proxy = proxyService.getRandomProxyModel();
			return runActivation(model, proxy);
		}
		
		if(html != null){
			
			Document doc = Jsoup.parse(html);
			
			Elements e = doc.getElementsByTag("script");
			
			if(e.size() == 0){
				return null;
			}
			
			Element element = e.get(0);
			
			String content = element.html().trim();
			
			String replaceUrl = content.substring(18, content.length() - 3);
			System.out.println(model.getAid()+" [3] "+replaceUrl);
			if(replaceUrl.indexOf("getElementById") != -1 || replaceUrl.indexOf("$CONFIG") != -1){
//				System.out.println(html);
				return null;
			}
			
			httpGet = new HttpGet(replaceUrl);
			httpGet.addHeader("Referer", secondUrl);
			
			try {
				httpResponse = httpClient.execute(httpGet);
			} catch (ClientProtocolException e1) {
				System.out.println(e1.getMessage());
				httpClient.getConnectionManager().shutdown();
				proxyService.getTimeOutData().add(proxy);
				return null;
			} catch (IllegalStateException e1){
				System.out.println(e1.getMessage());
				httpClient.getConnectionManager().shutdown();
				proxyService.getTimeOutData().add(proxy);
				return null;
			} catch (IOException e1) {
				System.out.println(e1.getMessage());
				httpClient.getConnectionManager().shutdown();
				proxyService.getTimeOutData().add(proxy);
				return null;
			}
			
			String ssoHtml = null;
			
			try {
				ssoHtml = HtmlTools.getHtmlByBr(httpResponse.getEntity());
			} catch (UnsupportedEncodingException e1) {
				System.out.println(e1.getMessage());
				httpClient.getConnectionManager().shutdown();
				proxyService.getTimeOutData().add(proxy);
				return null;
			} catch (IllegalStateException e1) {
				System.out.println(e1.getMessage());
				httpClient.getConnectionManager().shutdown();
				proxyService.getTimeOutData().add(proxy);
				return null;
			} catch (IOException e1) {
				System.out.println(e1.getMessage());
				httpClient.getConnectionManager().shutdown();
				proxyService.getTimeOutData().add(proxy);
				return null;
			}
			
			if(ssoHtml != null){
				
				Document ssoDoc = Jsoup.parse(ssoHtml);
				
				Elements scripts = ssoDoc.getElementsByTag("script");
				
				if(scripts.size() == 0){
					return null;
				}
				
				Element ssoE = scripts.get(1);
				
				String ssoJS = ssoE.html();
				
				if(ssoJS.indexOf("location.replace") == -1){
					return null;
				}
				
				String ssoUrl = ssoJS.substring(ssoJS.indexOf("location.replace")+18, ssoJS.lastIndexOf("'"));
				
				if(ssoUrl.indexOf("function") != -1){
					return null;
				}
				
				System.out.println(model.getAid()+" [4] "+ssoUrl);
				httpGet = new HttpGet(ssoUrl);
				httpGet.addHeader("Referer", replaceUrl);
				
				try {
					httpResponse = httpClient.execute(httpGet);
				} catch (ClientProtocolException e1) {
					System.out.println(e1.getMessage());
					httpClient.getConnectionManager().shutdown();
					proxyService.getTimeOutData().add(proxy);
					return null;
				} catch (IllegalStateException e1) {
					System.out.println(e1.getMessage());
					httpClient.getConnectionManager().shutdown();
					proxyService.getTimeOutData().add(proxy);
					return null;
				}  catch (IOException e1) {
					System.out.println(e1.getMessage());
					httpClient.getConnectionManager().shutdown();
					proxyService.getTimeOutData().add(proxy);
					return null;
				}
				
				Header locat = httpResponse.getFirstHeader("Location");
				
				if(locat == null){
					return null;
				}
				
				String ssoLocation = locat.getValue();
				System.out.println(model.getAid()+" [5] "+ssoLocation);
				
				httpGet = new HttpGet(ssoLocation);
				httpGet.setHeader("Referer", ssoUrl);
				
				try {
					httpResponse = httpClient.execute(httpGet);
				} catch (ClientProtocolException e1) {
					System.out.println(e1.getMessage());
					httpClient.getConnectionManager().shutdown();
					proxyService.getTimeOutData().add(proxy);
					return null;
				} catch (IllegalStateException e1) {
					System.out.println(e1.getMessage());
					httpClient.getConnectionManager().shutdown();
					proxyService.getTimeOutData().add(proxy);
					return null;
				}  catch (IOException e1) {
					System.out.println(e1.getMessage());
					httpClient.getConnectionManager().shutdown();
					proxyService.getTimeOutData().add(proxy);
					return null;
				}
				
				
				String finalHtml = null;
					
				try {
					finalHtml = HtmlTools.getHtmlByBr(httpResponse.getEntity());
				} catch (UnsupportedEncodingException e2) {
					System.out.println(e2.getMessage());
					httpClient.getConnectionManager().shutdown();
					proxyService.getTimeOutData().add(proxy);
					return null;
				} catch (IllegalStateException e2) {
					System.out.println(e2.getMessage());
					httpClient.getConnectionManager().shutdown();
					proxyService.getTimeOutData().add(proxy);
					return null;
				} catch (IOException e2) {
					System.out.println(e2.getMessage());
					httpClient.getConnectionManager().shutdown();
					proxyService.getTimeOutData().add(proxy);
					return null;
				}
				
				
				return finalHtml;
				
			}
			
			
		}
			
		
		
		
		
		return null;
	}
	/**
	 * 执行更新资料的逻辑
	 * @param model
	 * @return
	 */
	public boolean runModifyInfo(wb_activationModel model, ProxyBean proxy, HttpClient httpClient, String html){
		
		String url = "http://weibo.com/nguide/aj/register?__rnd="+System.currentTimeMillis();
		
		HttpPost httpPost = new HttpPost(url);
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
			httpClient.getConnectionManager().shutdown();
			proxyService.getTimeOutData().add(proxy);
			return false;
		} catch (IllegalStateException e1){
			httpClient.getConnectionManager().shutdown();
			proxyService.getTimeOutData().add(proxy);
			return false;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			httpClient.getConnectionManager().shutdown();
			proxyService.getTimeOutData().add(proxy);
			return false;
		}
		String nguide_1_html = null;
		try {
			nguide_1_html = HtmlTools.getHtmlByBr(httpResponse.getEntity());
		} catch (UnsupportedEncodingException e2) {
			httpClient.getConnectionManager().shutdown();
			proxyService.getTimeOutData().add(proxy);
			return false;
		} catch (IllegalStateException e2) {
			httpClient.getConnectionManager().shutdown();
			proxyService.getTimeOutData().add(proxy);
			return false;
		} catch (IOException e2) {
			httpClient.getConnectionManager().shutdown();
			proxyService.getTimeOutData().add(proxy);
			return false;
		}
		
		JSONObject nguide_1_json = null;
		if(nguide_1_html != null){
			try {
				nguide_1_json = new JSONObject(nguide_1_html);
			} catch (JSONException e1) {
				httpClient.getConnectionManager().shutdown();
				proxyService.getTimeOutData().add(proxy);
				return false;
			}
		}
		int code = -1;
		if(nguide_1_json != null){
			try {
				code = nguide_1_json.getInt("code");
			} catch (JSONException e1) {
				httpClient.getConnectionManager().shutdown();
				proxyService.getTimeOutData().add(proxy);
				return false;
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
				httpClient.getConnectionManager().shutdown();
				proxyService.getTimeOutData().add(proxy);
				return false;
			} catch (IllegalStateException e2) {
				httpClient.getConnectionManager().shutdown();
				proxyService.getTimeOutData().add(proxy);
				return false;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				httpClient.getConnectionManager().shutdown();
				proxyService.getTimeOutData().add(proxy);
				return false;
			}
			
			String data_2 = "http://weibo.com/nguide/aj/stepstatus/relationstep?_t=0&__rnd="+System.currentTimeMillis();
			
			//第二步
			httpPost = new HttpPost(data_2);
			httpPost.addHeader("Referer", data.replace("\\", ""));
			
			try {
				httpResponse = httpClient.execute(httpPost);
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				httpClient.getConnectionManager().shutdown();
				proxyService.getTimeOutData().add(proxy);
				return false;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				httpClient.getConnectionManager().shutdown();
				proxyService.getTimeOutData().add(proxy);
				return false;
			}
			
			String data_3 = "http://weibo.com/nguide/aj/finish?num=0&interestnum=0&interesttype=1&user_tag=0&_t=0&__rnd="+System.currentTimeMillis();
			
			//第三步
			httpPost = new HttpPost(data_3);
			httpPost.addHeader("Referer", data_2);
			
			try {
				httpResponse = httpClient.execute(httpPost);
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				httpClient.getConnectionManager().shutdown();
				proxyService.getTimeOutData().add(proxy);
				return false;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				httpClient.getConnectionManager().shutdown();
				proxyService.getTimeOutData().add(proxy);
				return false;
			}
			
			//第四步
			String data_4 = "http://weibo.com/?uut=fin&from=reg";
			httpPost = new HttpPost(data_4);
			httpPost.addHeader("Referer", data_3);
			
			try {
				httpResponse = httpClient.execute(httpPost);
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				httpClient.getConnectionManager().shutdown();
				proxyService.getTimeOutData().add(proxy);
				return false;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				httpClient.getConnectionManager().shutdown();
				proxyService.getTimeOutData().add(proxy);
				return false;
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
					httpClient.getConnectionManager().shutdown();
					proxyService.getTimeOutData().add(proxy);
					return false;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					httpClient.getConnectionManager().shutdown();
					proxyService.getTimeOutData().add(proxy);
					return false;
				}
				
				
			}
			
			
			break;

		default:
			break;
		}
		
		return true;
	}
	
	
}
