package cn.panshihao.activationclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.net.telnet.TelnetClient;
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

import cn.panshihao.activationclient.mail.MailSenderInfo;
import cn.panshihao.activationclient.mail.SimpleMailSender;

public class NewActivationService {

	public static long startTime ;
	public static int complete = 0;
	
	/**
	 * 开始激活
	 */
	public boolean runActivation(wb_activationModel model, wb_proxyModel proxy){
		
		wb_activationDAO dao = new wb_activationDAO();
		
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

		if(proxy != null){
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(proxy.getIp(), proxy.getPort()));
		}
		
		
		httpClient.getParams().setParameter(ClientPNames.DEFAULT_HEADERS, headerList);
		httpClient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
		
		String result = toLocation(httpClient, model.getUrl(), model.getEmail(), model.getAid());
		
		if(result == null){
			System.out.println("["+model.getAid()+"] "+"注册失败");
			return false;
		}
		
		if(result.equals("{SendMail}")){
			// 激活需要发送一封邮件到新浪才能完成
			System.out.println("["+model.getAid()+"] "+"已经发送一封邮件到新浪");
			// status为89代表账号激活异常，但已经发送激活邮件到新浪
			model.setStatus(89);
			dao.update(model);
			return true;
		}
		
		if(result.equals("{Success}")){
			System.out.println("["+model.getAid()+"] "+"激活，资料完善并且发送一条微博完成！");
		}
		
		
		
		return true;
	}
	
	/**
	 * 连接跳转递归方法
	 * @param httpClient
	 * @param url
	 * @param email
	 * @param aid
	 * @return
	 */
	public String toLocation(HttpClient httpClient, String url, String email, int aid){
		
		
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
			System.out.println("["+aid+"] "+"访问激活连接失败，更换代理重试");
			return null;
		}
		
		// 第一次访问激活连接，从响应中取得location
		Header location = httpResponse.getFirstHeader("Location");
		
		if(location == null){
			System.out.println("["+aid+"] "+"访问激活连接失败，可能是跳转到其他地方去？ 127行");
			
			String responseString = null;
			
			try {
				responseString = HtmlTools.getHtmlByBr(httpResponse.getEntity(), "UTF-8");
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
				System.out.println("["+aid+"] "+"这个账号激活出现问题，需要发一个邮件到新浪的邮箱去验证 150行");
				return toSendMail(httpClient, url, email, aid);
			}
			// 如果包含这个字符串，表示貌似能够成功注册？
			if(responseString.contains("crossdomain2") && responseString.contains("location.replace")){
				System.out.println("["+aid+"] "+"另外一个激活分支？ 155行");
				return toCrossDomain(httpClient, url, email, responseString, aid);
			}
			
			System.out.println(responseString);
			
			return null;
		}
		
		
		String activeUrl = location.getValue();
		System.out.println("["+aid+"] "+activeUrl);
		
		if(activeUrl == null){
			System.out.println("["+aid+"] "+"这次激活是失败的，原因很可能是账号被停封 169行");
			return null;
		}
		// 判断Url是否包含http
		if(!(activeUrl.charAt(0) == 'h' || activeUrl.charAt(0) == 'H')){
			activeUrl = "http://weibo.com" + activeUrl;
		}
		
		return toLocation(httpClient, activeUrl, email, aid);
	}
	/**
	 * 响应CrossDomain事件的方法
	 * @param httpClient
	 * @param url
	 * @param email
	 * @param html
	 * @param aid
	 * @return
	 */
	public String toCrossDomain(HttpClient httpClient, String url, String email, String html, int aid){
		
		Document doc = Jsoup.parse(html);
		Elements scripts = doc.select("script");
		
		if(scripts.size() == 0){
			System.out.println("["+aid+"] "+"这什么情况？script标签都没有？靠。186行");
			return null;
		}
		
		String scriptHtml = scripts.html().trim();
		
		String crossDomainUrl = scriptHtml.substring(scriptHtml.indexOf("(")+2, scriptHtml.indexOf(")")-1);
		
		// 判断Url是否包含http
		if(!(crossDomainUrl.charAt(0) == 'h' || crossDomainUrl.charAt(0) == 'H')){
			crossDomainUrl = "http://weibo.com" + crossDomainUrl;
		}
				
		
		HttpGet httpGet = new HttpGet(crossDomainUrl);
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
			System.out.println("["+aid+"] "+"我擦，httpResponse是null? 214行");
			return null;
		}
		
		Header location = httpResponse.getFirstHeader("Location");
		
		if(location == null){
			String crossDomainHtml = null;
			try {
				crossDomainHtml = HtmlTools.getHtmlByBr(httpResponse.getEntity());
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
			
			if(crossDomainHtml.contains("crossDomainAction") && crossDomainHtml.contains("ssologin.js")){
				System.out.println("["+aid+"] "+"这代表，这个激活分支貌似是正确的！");
				return toCrossDomainSuccess(httpClient, crossDomainUrl, email, crossDomainHtml, aid);
			}
			
			
			return null;
		}
		
		// 这个地方，貌似不可能进来了。
		crossDomainUrl = location.getValue();
		System.out.println("["+aid+"] "+"这个地方，貌似不可能进来了。 246行 ");
		System.out.println(crossDomainUrl); 
		
		
		return null;
	}
	/**
	 * 响应跨域成功的方法
	 * @param httpClient
	 * @param url
	 * @param email
	 * @param html
	 * @param aid
	 * @return
	 */
	public String toCrossDomainSuccess(HttpClient httpClient, String url, String email, String html, int aid){
	
		
		String replaceUrl = html.substring(html.indexOf("location.replace('") + 18, html.lastIndexOf("'"));
		
		
		HttpGet httpGet = new HttpGet(replaceUrl);
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
			System.out.println("["+aid+"] "+"坑爹了，最后一步啊 272行");
			return null;
		}
		
		Header location = httpResponse.getFirstHeader("Location");
		
		if(location == null){
			// 这个地方，貌似不可能进来了。
			String crossDomainSuccessHtml = HtmlTools.getHtmlByBr(httpResponse);
			System.out.println("["+aid+"] "+"这个地方，貌似不可能进来了。 286行");
			System.out.println(crossDomainSuccessHtml);
			
			return null;
		}
		
		// 代表 激活已经完成！准备跳转到基本资料填写向导界面
		String crossDomainSuccessUrl = location.getValue();
		
		return toGonguide(httpClient, crossDomainSuccessUrl, email, html, aid);
	}
	/**
	 * 响应资料向导的方法
	 * @param httpClient
	 * @param url
	 * @param email
	 * @param html
	 * @param aid
	 * @return
	 */
	public String toGonguide(HttpClient httpClient, String url, String email, String html, int aid){
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
			System.out.println("["+aid+"] "+"神马意思？干 349行");
			return null;
		}
		
		Header location = httpResponse.getFirstHeader("Location");
		
		if(location == null){
			
			String nguideHtml = HtmlTools.getHtmlByBr(httpResponse);
			
			if(nguideHtml == null){
				System.out.println("["+aid+"] "+"nguide界面拉取失败");
				return null;
			}
			
			if(nguideHtml.contains("自我介绍") && nguideHtml.contains("自我介绍一下")){
				System.out.println("["+aid+"] "+"开始nguide的第一步啦 360行");
				System.out.println("["+aid+"] "+"将从界面中获取的uid等信息更新到数据库 361行");
				toUpdateUid(httpClient, url, email, nguideHtml, aid);
				
				return toCompleteActivation(httpClient, url, email, nguideHtml, aid);
				
			}
			System.out.println("["+aid+"] "+"这里出问题了 328行");
			System.out.println(nguideHtml);
			
			return null;
		}
		// 这个地方，貌似不可能进来了。
		String nguideLocationUrl = location.getValue();
		System.out.println("["+aid+"] "+"这个地方，貌似不可能进来了。325行");
		System.out.println(nguideLocationUrl);
		
		
		return null;
	}
	/**
	 * 更新uid资料
	 * @param httpClient
	 * @param url
	 * @param email
	 * @param html
	 * @param aid
	 * @return
	 */
	public String toUpdateUid(HttpClient httpClient, String url, String email, String html, int aid){
		Document doc = Jsoup.parse(html);
		
		Elements scripts = doc.select("script");
		
		if(scripts.size() == 0){
			System.out.println("["+aid+"] "+"坑爹了没有script标签。 347行");
			return null;
		}
		
		String UidScriptHtml = scripts.get(0).html();
		
		if(UidScriptHtml.contains("$CONFIG") && UidScriptHtml.contains("uid")){
			
			String jsonString = UidScriptHtml.substring(UidScriptHtml.indexOf("{"), UidScriptHtml.indexOf("}")+1);
			JSONObject json = null;
			String uid = null;
			String domain = null;
			
			try {
				json = new JSONObject(jsonString);
				uid = json.getString("uid");
				domain = json.getString("domain");
			} catch (JSONException e) {
				System.out.println(e.getMessage());
				return null;
			}
			
			if(uid != null && domain != null){
				if(updateUid(aid, uid, domain)){
					System.out.println("["+aid+"] "+"更新账号uid等基本信息成功。 374行");
				}else{
					System.out.println("["+aid+"] "+"更新账号uid等基本信息失败。 376行");
				}
				return null;
			}
			
		}else{
			System.out.println("["+aid+"] "+"没有找到含有$CONFIG和uid的script。 382行");
		}
		
		
		
		return null;
	}
	/**
	 * 完成注册的方法
	 * @param httpClient
	 * @param url
	 * @param email
	 * @param html
	 * @param aid
	 * @return
	 */
	public String toCompleteActivation(HttpClient httpClient, String url, String email, String html, int aid){
		
		String AjNguideUrl = "http://weibo.com/nguide/aj/register?__rnd="+System.currentTimeMillis();
		
		HttpPost httpPost = new HttpPost(AjNguideUrl);
		
		List<NameValuePair> formlist = new ArrayList<NameValuePair>();
		
		Document doc = Jsoup.parse(html);
		
		Elements inputs = doc.select("input[name=password],input[name=time]");
		
		String password = inputs.select("[name=password]").val();
		String time = inputs.select("[name=time]").val();
		
		Elements scripts = doc.select("script");
		
		if(scripts.size() < 2){
			System.out.println("["+aid+"] "+"坑爹了没有script标签。 461行");
			return null;
		}
		
		Element valueScript = scripts.get(1);
		
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
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		HttpResponse httpResponse = null;
		
		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		if(httpResponse == null){
			System.out.println("["+aid+"] "+"httpResponse 是null。 516行");
			return null;
		}
		
		Header location = httpResponse.getFirstHeader("Location");
		
		if(location == null){
			
			String completeHtml = HtmlTools.getHtmlByBr(httpResponse);
			
			if(completeHtml == null || (completeHtml != null && completeHtml.equals(""))){
				System.out.println("["+aid+"] "+"天啊，到这里居然什么都没返回。 526行");
				return null;
			}
			JSONObject completeJson = null;
			try {
				completeJson = new JSONObject(completeHtml);
			} catch (JSONException e) {
				System.out.println(e.getMessage());
				return null;
			}
			
			if(completeJson == null){
				System.out.println("["+aid+"] "+"不能构造json。 538行 "+completeHtml);
				return null;
			}
			
			String dataUrl = null;
			
			try {
				if(completeJson.getInt("code") == 100000 && completeJson.getString("msg").equals("操作成功")){
					dataUrl = completeJson.getString("data");
				}
			} catch (JSONException e) {
				System.out.println(e.getMessage());
				return null;
			}
			
			
			// 进入向导的第二页
			HttpGet httpGet = new HttpGet(dataUrl);
			
			try {
				httpResponse = httpClient.execute(httpGet);
			} catch (ClientProtocolException e) {
				System.out.println(e.getMessage());
				return null;
			} catch (IOException e) {
				System.out.println(e.getMessage());
				return null;
			}
			
			location = httpResponse.getFirstHeader("Location");
			
			if(location == null){
				
				String secondHtml = HtmlTools.getHtmlByBr(httpResponse);
				
				if(secondHtml == null){
					System.out.println("["+aid+"] "+"错误的html。 574行 ");
					return null;
				}
				
				if(secondHtml.contains("找到朋友") && secondHtml.contains("关注好友")){
					System.out.println("["+aid+"] "+"进入资料向导第二页了！。 579行 ");
					
					httpPost = new HttpPost("http://weibo.com/nguide/aj/stepstatus/relationstep?_t=0&__rnd="+System.currentTimeMillis());
					
					try {
						httpResponse = httpClient.execute(httpPost);
					} catch (ClientProtocolException e) {
						System.out.println(e.getMessage());
						return null;
					} catch (IOException e) {
						System.out.println(e.getMessage());
						return null;
					}
					
					if(httpResponse == null){
						System.out.println("["+aid+"] "+"第二页请求返回为null。 594行 ");
						return null;
					}
					
					location = httpResponse.getFirstHeader("Location");
					
					if(location == null){
						
						String threeHtml = HtmlTools.getHtmlByBr(httpResponse);
						
						if(threeHtml == null){
							System.out.println("["+aid+"] "+"错误的html。 605行 ");
							return null;
						}
						JSONObject threeJson = null;
						try {
							threeJson = new JSONObject(threeHtml);
						} catch (JSONException e) {
							System.out.println(e.getMessage());
							return null;
						}
						
						if(threeJson == null){
							System.out.println("["+aid+"] "+"构造json失败。 617行 ");
							return null;
						}
						
						String threeDataUrl = null;
						try {
							if(threeJson.getInt("code") == 100000 && threeJson.getString("msg").equals("操作成功")){
								threeDataUrl = threeJson.getString("data");
							}
						} catch (JSONException e) {
							System.out.println(e.getMessage());
							return null;
						}
						
						httpGet = new HttpGet(threeDataUrl);
						
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
							System.out.println("["+aid+"] "+"第三页请求返回为null。 594行 ");
							return null;
						}
						
						location = httpResponse.getFirstHeader("Location");
						
						if(location == null){
							
							String fourHtml = HtmlTools.getHtmlByBr(httpResponse);
							
							String data_3 = "http://weibo.com/nguide/aj/finish?num=0&interestnum=0&interesttype=1&user_tag=0&_t=0&__rnd="+System.currentTimeMillis();
							
							//第三步
							httpPost = new HttpPost(data_3);
							
							try {
								httpResponse = httpClient.execute(httpPost);
							} catch (ClientProtocolException e) {
								System.out.println(e.getMessage());
								return null;
							} catch (IOException e) {
								System.out.println(e.getMessage());
								return null;
							}
							
							//第四步
							String data_4 = "http://weibo.com/?uut=fin&from=reg";
							httpGet = new HttpGet(data_4);
							
							try {
								httpResponse = httpClient.execute(httpGet);
							} catch (ClientProtocolException e) {
								System.out.println(e.getMessage());
								return null;
							} catch (IOException e) {
								System.out.println(e.getMessage());
								return null;
							}
							
							location = httpResponse.getFirstHeader("Location");
							
							if(location == null){
								System.out.println("["+aid+"] "+"跳转到微博个人首页失败。 686行 ");
								return null;
								
							}
							
							String data_5 = "http://www.weibo.com"+location.getValue();
							
							httpGet = new HttpGet(data_5);
							
							try {
								httpResponse = httpClient.execute(httpGet);
							} catch (ClientProtocolException e) {
								System.out.println(e.getMessage());
								return null;
							} catch (IOException e) {
								System.out.println(e.getMessage());
								return null;
							}
							
							System.out.println("["+aid+"] "+"开始发微博，并更新数据库。 705行 ");
							
							
							return toSendWeibo(httpClient, threeDataUrl, email, fourHtml, aid);
						}
						
						
						System.out.println(threeJson);
						
						
						return null;
					}
					
					// 这个地方，貌似不可能进来了。
					String completeLocationUrl = location.getValue();
					System.out.println("["+aid+"] "+"这个地方，貌似不可能进来了。575行");
					System.out.println(completeLocationUrl);
					
					return null;
				}
				
				System.out.println(secondHtml);
			
				return null;
			}
			
			// 这个地方，貌似不可能进来了。
			String completeLocationUrl = location.getValue();
			System.out.println("["+aid+"] "+"这个地方，貌似不可能进来了。575行");
			System.out.println(completeLocationUrl);
			
			return null;
		}
		
		// 这个地方，貌似不可能进来了。
		String completeLocationUrl = location.getValue();
		System.out.println("["+aid+"] "+"这个地方，貌似不可能进来了。580行");
		System.out.println(completeLocationUrl);
		
		return null;
	}
	/**
	 * 最后发送一条微博
	 * @param httpClient
	 * @param url
	 * @param email
	 * @param html
	 * @param aid
	 * @return
	 */
	public String toSendWeibo(HttpClient httpClient, String url, String email, String html, int aid){
		
		
		HttpPost httpPost = new HttpPost("http://weibo.com/aj/mblog/add?_wv=5&__rnd="+System.currentTimeMillis());
		
		List<NameValuePair> formPairs = new ArrayList<NameValuePair>();
		
		formPairs.add(new BasicNameValuePair("_surl", ""));
		formPairs.add(new BasicNameValuePair("_t", "0"));
		formPairs.add(new BasicNameValuePair("hottopicid", ""));
		formPairs.add(new BasicNameValuePair("location", "home"));
		formPairs.add(new BasicNameValuePair("module", "stissue"));
		formPairs.add(new BasicNameValuePair("pic_id", ""));
		formPairs.add(new BasicNameValuePair("rank", "0"));
		formPairs.add(new BasicNameValuePair("rankid", ""));
		formPairs.add(new BasicNameValuePair("text", "hello 大家好！~~~ "+System.currentTimeMillis()));
		formPairs.add(new BasicNameValuePair("_surl", ""));
		
		HttpResponse httpResponse = null;
		
		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
//		System.out.println(httpResponse.getFirstHeader("Location"));
//		System.out.println(HtmlTools.getHtmlByBr(httpResponse));
		
		
			
		
		return updateStatusForSuccess(aid);
	}
	/**
	 * 根据aid更新账号的状态
	 * @param aid
	 * @return
	 */
	public String updateStatusForSuccess(int aid){
		Connection conn = Tools.getMysqlConn();
		PreparedStatement pstmt = null;
		int result = -1;
		if(conn != null){
			
			try {
				pstmt = conn.prepareStatement("update wb_account set status = ?  where aid = ?");
				pstmt.setInt(1, 11);
				pstmt.setInt(2, aid);
				result = pstmt.executeUpdate();
				
				pstmt = conn.prepareStatement("update wb_activation set status = ? where aid = ?");
				pstmt.setInt(1, 91);
				pstmt.setInt(2, aid);
				
				result = pstmt.executeUpdate();
				
				
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			} finally {
				if(pstmt != null){
					try {
						pstmt.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			return "{Success}";
			
		}
		
		return null;
	}
	/**
	 * 更新账号的uid数据到db，status 90和10代表账号处于已更新uid状态
	 * @param aid
	 * @param uid
	 * @param domain
	 * @return
	 */
	public boolean updateUid(int aid, String uid, String domain){
		Connection conn = Tools.getMysqlConn();
		PreparedStatement pstmt = null;
		int result = -1;
		if(conn != null){
			
			try {
				pstmt = conn.prepareStatement("update wb_account set uid = ? , domain = ? , status = 10 where aid = ?");
				pstmt.setLong(1, Long.parseLong(uid));
				pstmt.setString(2, domain);
				pstmt.setInt(3, aid);
				
				result = pstmt.executeUpdate();
				
				pstmt = conn.prepareStatement("update wb_activation set status = ? where aid = ?");
				pstmt.setInt(1, 90);
				pstmt.setInt(2, aid);
				
				result = pstmt.executeUpdate();
				
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			} finally {
				if(pstmt != null){
					try {
						pstmt.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			return result != -1;
			
		}
		
		return false;
	}
	/**
	 * 发送一个邮件到新浪
	 * @param httpClient
	 * @param url
	 * @param email
	 * @param index
	 * @return
	 */
	public String toSendMail(HttpClient httpClient, String url, String email, int index){
		
		   //这个类主要是设置邮件   
	      MailSenderInfo mailInfo = new MailSenderInfo();    
	      mailInfo.setMailServerHost("localhost");    
	      mailInfo.setMailServerPort("25");    
	      mailInfo.setValidate(true);    
	      mailInfo.setUserName(email.substring(0, email.indexOf("@")));    
	      mailInfo.setPassword(email.substring(0, email.indexOf("@")));//您的邮箱密码    
	      mailInfo.setFromAddress(email);    
	      mailInfo.setToAddress("sinaweibo@vip.sina.com");    
	      mailInfo.setSubject(RandomString.getMD5(System.currentTimeMillis()+""));    
	      mailInfo.setContent("激活我的账号啊" + RandomString.getMD5(System.currentTimeMillis()+""));    
	         //这个类主要来发送邮件   
	      SimpleMailSender sms = new SimpleMailSender();   
	      sms.sendTextMail(mailInfo);//发送文体格式    
		
		return "{SendMail}";
	}
	
	public synchronized static void pulsComplete(){
		complete ++;
	}
	
	
	public static void main(String[] args) {
		wb_activationDAO dao = new wb_activationDAO();
		final ProxyService proxyService = new ProxyService();
		proxyService.loadProxyData();
		
		
		List<wb_activationModel> data  = dao.selectActivation();
		
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		
		startTime = System.currentTimeMillis();
		System.out.println("开始账号激活");
		while(true){
			// 当目标数量已经与完成数量相等，则重新开始逻辑
			if(data != null && complete == data.size() && data.size() > 0){
				complete = 0;
				data = dao.selectActivation();
				System.out.println("待激活账号 "+data.size());
				
			}
			
			if(complete == 0){
				for(int i = 0 ; i < data.size() ; i ++){
					final wb_activationModel model = data.get(i);
					
					executorService.execute(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							NewActivationService s = new NewActivationService();
							wb_proxyModel proxy = proxyService.getRandomProxyModel();
							if(s.runActivation(model, proxy)){
								proxyService.revertProxyModel(proxy, System.currentTimeMillis());
							}
							pulsComplete();
							long curTime = System.currentTimeMillis();
							
							if(curTime - startTime > 3600000){
								startTime = System.currentTimeMillis();
								proxyService.loadProxyData();
							}
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
}
