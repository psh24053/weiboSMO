package cn.panshihao.proxysearch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.cookie.params.CookieSpecPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class ProxySearch {

	
	public static String get_Myproxy(){
		return null;
	}
	
	/**
	 * 爱优
	 * http://vipiu.net
	 */
	public static void loadVipiu_net(){
		
	}
	/**
	 * http://proxy.linktool.org/
	 */
	public static void loadProxy_LinkTool(){
		ExecutorService service = Executors.newFixedThreadPool(100);
		URL url = null;
		try {
			url = new URL("http://proxy.linktool.org/");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String html = null;
		try {
			html = HtmlTools.getHtml(url.openStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Document doc = Jsoup.parse(html);
		Elements elements = doc.select("dd");
		
		for(int i = 0 ; i < elements.size() ; i ++){
			Element e = elements.get(i);
			String host = e.text();
			
			final String ip = host.split(":")[0];
			final int port = Integer.parseInt(host.split(":")[1]);
			service.execute(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("ip -> "+ip+" ,port -> "+port);
				if(validationCountry(ipAddress(ip))){
					if(validationProxy(ip, port)){
						wb_proxyDAO dao = new wb_proxyDAO();
						wb_proxyModel model = new wb_proxyModel();
						model.setChecktime(System.currentTimeMillis());
						model.setIp(ip);
						model.setPort(port);
						
						dao.insert(model);
						
					}
				}
			}
			});
			
			
		}
		
		service.shutdown();
	}
	/**
	 * http://www.samair.ru/proxy/ip-address-01.htm
	 */
	public static void loadwww_samair_ru(){
		
		ExecutorService service = Executors.newFixedThreadPool(100);
		
		for(int j = 1 ; j < 16 ; j ++){
			String index = j+"";
			if(j < 10){
				index = "0"+j;
			}
			
			URL url = null;
			try {
				url = new URL("http://www.samair.ru/proxy/ip-address-"+index+".htm");
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				continue;
			}
			
			String html = null;
			try {
				html = HtmlTools.getHtml(url.openStream());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				continue;
			}
			
			Document doc = Jsoup.parse(html);
			Elements elements = doc.select("pre");
			
			System.out.println(elements.text());
			
			File file = new File("E:\\proxys\\samair_ru\\"+index+".txt");
			
			try {
				FileOutputStream out = new FileOutputStream(file);
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "utf-8"));
				
				writer.write(elements.text());
				writer.flush();
				writer.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//			for(int i = 0 ; i < elements.size() ; i ++){
//				Element e = elements.get(i);
//				if(!e.hasAttr("class")){
//					continue;
//				}
//				
//				Elements ee = e.select("td");
//				
//				final String ip = ee.get(1).text();
//				final int port = Integer.parseInt(ee.get(2).text());
//				
//				service.execute(new Runnable() {
//					
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						if(validationProxy(ip, port)){
//							wb_proxyDAO dao = new wb_proxyDAO();
//							wb_proxyModel model = new wb_proxyModel();
//							model.setChecktime(System.currentTimeMillis());
//							model.setIp(ip);
//							model.setPort(port);
//							
//							dao.insert(model);
//							
//						}
//					}
//				});
//				
//				
//				
//				
//				
//			}
		}
		
		
		service.shutdown();
		
		
		
	}
	/**
	 * 根据ip来获取地址
	 * @param ip
	 * @return
	 */
	public static String ipAddress(String ip){
		String html = HtmlTools.getHtml("http://ip138.com/ips138.asp?ip="+ip,"GBK");
		
		Document doc = Jsoup.parse(html);
		
		String address = doc.select(".ul1 li").get(0).html();
		
		
		return address.substring(address.indexOf("：")+1);
	}
	
	
	/**
	 * 根据ip和port验证代理服务器是否可用
	 * @param ip
	 * @param port
	 * @return
	 */
	public static boolean validationProxy(String ip, int port){
		if(port > 65535){
			return false;
		}
		
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
		httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(ip, port));
        
        // 请求注册界面，获取表单必须参数
        HttpPost httpPost = new HttpPost("http://www.weibo.com/signup/mobile.php");
		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
//			Log.log.error(e.getMessage(), e);
			return false;
		} catch (IOException e) {
//			Log.log.error(e.getMessage(), e);
			return false;
		}
		HttpEntity httpEntity = httpResponse.getEntity();
		
		String html = null;
		try {
			html = HtmlTools.getHtml(httpEntity);
		} catch (UnsupportedEncodingException e) {
//			Log.log.error(e.getMessage(), e);
			return false;
		} catch (IllegalStateException e) {
//			Log.log.error(e.getMessage(), e);
			return false;
		} catch (IOException e) {
//			Log.log.error(e.getMessage(), e);
			return false;
		}
		
//		Log.log.debug("getHtml "+html);
		
		// 如果html为null，或者html的长度小于8000，则代表获取html失败
		if(html == null || html.length() < 8000){
			Tools.log.error("html get error!");
			return false;
		}
		
		
		return true;
	}
	/**
	 * 判断国家是否为中国，为中国返回false
	 * @param address
	 * @return
	 */
	public static boolean validationCountry(String address){
		URL url = null;
		try {
			url = new URL("http://maps.googleapis.com/maps/api/geocode/json?address="+URLEncoder.encode(address)+"&sensor=true");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			return false;
		}
		String html = null;
		
		try {
			html = HtmlTools.getHtmlByBr(url.openStream());
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
		if(html == null || html.contains("中国") || html.contains("CN")){
			return false;
		}
		
		
		
		return true;
	}
}
