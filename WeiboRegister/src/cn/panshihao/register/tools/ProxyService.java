package cn.panshihao.register.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import cn.panshihao.desktop.commons.HtmlTools;
import cn.panshihao.desktop.commons.Log;
import cn.panshihao.register.dao.wb_proxyDAO;
import cn.panshihao.register.model.wb_proxyModel;

public class ProxyService {

	
	public static final long ProxyDelay = 300000;
	
	/**
	 * 代理服务器数据
	 */
	private Map<Long, wb_proxyModel> ProxyData = new HashMap<Long, wb_proxyModel>();
	
	/**
	 * 被封杀的代理服务器
	 */
	private List<wb_proxyModel> blockData = new ArrayList<wb_proxyModel>();
	
	
	
	
	/**
	 * 从数据库中加载代理服务器数据
	 */
	public void loadProxyData(){
		wb_proxyDAO dao = new wb_proxyDAO();
		List<wb_proxyModel> data = dao.selectByAvailable();
		
		for(int i = 0 ; i < data.size() ; i ++){
			ProxyData.put(System.currentTimeMillis() - ProxyDelay + i, data.get(i));
		}
		
	}
	/**
	 * 从网络上搜索代理服务器数据
	 * 经过验证可用后加入到数据库中，同时写入内存中
	 * @return total 搜索到的数量
	 */
	public int searchProxyData(){
		
		
		
		
		
		return 0;
	}
	/**
	 * 获取xici_wt的http代理
	 * url:http://www.xici.net.co/wt
	 * @return
	 * @throws IOException 
	 */
	private List<wb_proxyModel> get_xici_wt() {
		
		ExecutorService service = Executors.newFixedThreadPool(100);
		
		for(int j = 1 ; j < 11 ; j ++){
			URL url = null;
			try {
				url = new URL("http://www.xici.net.co/wt/"+j);
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
			Elements elements = doc.select("tr");
			
			for(int i = 0 ; i < elements.size() ; i ++){
				Element e = elements.get(i);
				if(!e.hasAttr("class")){
					continue;
				}
				
				Elements ee = e.select("td");
				
				final String ip = ee.get(1).text();
				final int port = Integer.parseInt(ee.get(2).text());
				
				service.execute(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(validationProxy(ip, port)){
							wb_proxyDAO dao = new wb_proxyDAO();
							wb_proxyModel model = new wb_proxyModel();
							model.setChecktime(System.currentTimeMillis());
							model.setIp(ip);
							model.setPort(port);
							
							dao.insert(model);
							
						}
					}
				});
				
				
				
				
				
			}
		}
		
		
		service.shutdown();
		
		return null;
	}
	
	
	/**
	 * 获取xici_wn的http代理
	 * url:http://www.xici.net.co/wn
	 * @return
	 * @throws IOException 
	 */
	private List<wb_proxyModel> get_xici_wn() {
		ExecutorService service = Executors.newFixedThreadPool(100);
		
		for(int j = 1 ; j < 48 ; j ++){
			URL url = null;
			try {
				url = new URL("http://www.xici.net.co/wn/"+j);
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
			Elements elements = doc.select("tr");
			
			for(int i = 0 ; i < elements.size() ; i ++){
				Element e = elements.get(i);
				if(!e.hasAttr("class")){
					continue;
				}
				
				Elements ee = e.select("td");
				
				final String ip = ee.get(1).text();
				final int port = Integer.parseInt(ee.get(2).text());
				
				service.execute(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(validationProxy(ip, port)){
							wb_proxyDAO dao = new wb_proxyDAO();
							wb_proxyModel model = new wb_proxyModel();
							model.setChecktime(System.currentTimeMillis());
							model.setIp(ip);
							model.setPort(port);
							
							dao.insert(model);
							
						}
					}
				});
				
				
				
				
				
			}
		}
		
		
		service.shutdown();
		
		return null;
	}
	
	/**
	 * 获取51daili_fast的http代理数据
	 * url:http://51dai.li/http_fast.html
	 * @return
	 * @throws IOException 
	 */
	private List<wb_proxyModel> get_51daili_fast() {
		ExecutorService service = Executors.newFixedThreadPool(100);
		
		URL url;
		try {
			url = new URL("http://51dai.li/http_fast.html");
		} catch (MalformedURLException e2) {
			// TODO Auto-generated catch block
			return null;
		}
		
		String html = null;
		try {
			html = HtmlTools.getHtml(url.openStream());
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			return null;
		}
		
		Document doc = Jsoup.parse(html);
		Elements elements = doc.select("tr");
		System.out.println("get_51daili_fast -> "+elements.size());
		for(int i = 0 ; i < elements.size() ; i ++){
			Element e = elements.get(i);
			if(e.select("th").size() > 0){
				continue;
			}
			
			
			Elements ee = e.select("td");
			
			final String ip = ee.get(1).text();
			final int port = Integer.parseInt(ee.get(2).text());
			String county = ee.get(3).text();
			
			if(county != null && county.equals("CN")){
				continue;
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			service.execute(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(validationProxy(ip, port)){
						wb_proxyDAO dao = new wb_proxyDAO();
						wb_proxyModel model = new wb_proxyModel();
						model.setChecktime(System.currentTimeMillis());
						model.setIp(ip);
						model.setPort(port);
						
						dao.insert(model);
						
					}
				}
			});
			
			
			
			
			
		}
		
		
		return null;
	}
	/**
	 * 获取51daili_anonymous的http代理数据
	 * url:http://51dai.li/http_anonymous.html
	 * @return
	 * @throws IOException 
	 */
	private List<wb_proxyModel> get_51daili_anonymous(){
		ExecutorService service = Executors.newFixedThreadPool(100);
		
		URL url;
		try {
			url = new URL("http://51dai.li/http_anonymous.html");
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			return null;
		}
		
		String html = null;
		try {
			html = HtmlTools.getHtml(url.openStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			return null;
		}
		
		Document doc = Jsoup.parse(html);
		Elements elements = doc.select("tr");
		System.out.println("get_51daili_anonymous -> "+elements.size());
		for(int i = 0 ; i < elements.size() ; i ++){
			Element e = elements.get(i);
			if(e.select("th").size() > 0){
				continue;
			}
			
			
			Elements ee = e.select("td");
			
			final String ip = ee.get(1).text();
			final int port = Integer.parseInt(ee.get(2).text());
			String county = ee.get(3).text();
			
			if(county != null && county.equals("CN")){
				continue;
			}
			
			service.execute(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(validationProxy(ip, port)){
						wb_proxyDAO dao = new wb_proxyDAO();
						wb_proxyModel model = new wb_proxyModel();
						model.setChecktime(System.currentTimeMillis());
						model.setIp(ip);
						model.setPort(port);
						
						dao.insert(model);
						
					}
				}
			});
			
			
			
			
			
		}
		
		
		return null;
	}
	/**
	 * 获取51daili_non_anonymous的http代理数据
	 * url:http://51dai.li/http_non_anonymous.html
	 * @return
	 * @throws IOException 
	 */
	private List<wb_proxyModel> get_51daili_non_anonymous() {
		ExecutorService service = Executors.newFixedThreadPool(100);
		
		URL url = null;
		try {
			url = new URL("http://51dai.li/http_non_anonymous.html");
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			return null;
		}
		
		String html = null;
		try {
			html = HtmlTools.getHtml(url.openStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			return null;
		}
		
		Document doc = Jsoup.parse(html);
		Elements elements = doc.select("tr");
		System.out.println("get_51daili_non_anonymous -> "+elements.size());
		for(int i = 0 ; i < elements.size() ; i ++){
			Element e = elements.get(i);
			if(e.select("th").size() > 0){
				continue;
			}
			
			
			Elements ee = e.select("td");
			
			final String ip = ee.get(1).text();
			final int port = Integer.parseInt(ee.get(2).text());
			String county = ee.get(3).text();
			
			if(county != null && county.equals("CN")){
				continue;
			}
			
			service.execute(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(validationProxy(ip, port)){
						wb_proxyDAO dao = new wb_proxyDAO();
						wb_proxyModel model = new wb_proxyModel();
						model.setChecktime(System.currentTimeMillis());
						model.setIp(ip);
						model.setPort(port);
						
						dao.insert(model);
						
					}
				}
			});
			
			
			
			
			
		}
		
		return null;
	}
	
	
	
	
	
	
	
	/**
	 * 获取cnproxy的http代理数据
	 * url:http://www.cnproxy.com/proxy1.html
	 * @return
	 */
	private List<wb_proxyModel> get_cnproxy(){
		return null;
	}
	/**
	 * 获取sitedigger的http代理数据
	 * url:http://www.site-digger.com/html/articles/20110516/proxieslist.html
	 * @return
	 */
	private List<wb_proxyModel> get_sitedigger(){
		return null;
	}
	/**
	 * 获取cz88的http代理数据
	 * url:http://www.cz88.net/proxy/index.aspx
	 * @return
	 */
	private List<wb_proxyModel> get_cz88(){
		return null;
	}
	/**
	 * 获取veryhuo的http代理数据
	 * url:http://www.veryhuo.com/res/ip/
	 * @return
	 */
	private List<wb_proxyModel> get_veryhuo(){
		return null;
	}
	/**
	 * 获取56ads的http代理数据
	 * url:http://www.56ads.com/proxyip/
	 * @return
	 */
	private List<wb_proxyModel> get_56ads(){
		return null;
	}
	/**
	 * 获取5753的http代理
	 * url:http://www.5753.net/proxy/
	 * @return
	 */
	private List<wb_proxyModel> get_5753(){
		return null;
	}
	
	
	
	
	
	
	/**
	 * 根据ip和port验证代理服务器是否可用
	 * @param ip
	 * @param port
	 * @return
	 */
	public boolean validationProxy(String ip, int port){
		
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
			Log.log.error("html get error!");
			return false;
		}
		
		
		return true;
	}
	
	
	public static void main(String[] args) {
		ProxyService s = new ProxyService();
		
		s.get_51daili_fast();
		s.get_51daili_anonymous();
		s.get_51daili_non_anonymous();
		s.get_xici_wn();
		s.get_xici_wt();
		
	}
	
	
	/**
	 * 获取一个代理服务器对象
	 * 每一个代理服务器的使用间隔为30秒
	 * @return
	 */
	public synchronized wb_proxyModel getRandomProxyModel(){
		if(ProxyData.size() == 0){
			return null;
		}
		
		for(Long key : ProxyData.keySet()){
			long curTime = System.currentTimeMillis();
			
			if(curTime - key > ProxyDelay){
				return ProxyData.remove(key);
			}
			
		}
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Log.log.error(e.getMessage(), e);
		}
		
		return getRandomProxyModel();

	}

	/**
	 * 将使用过的proxymodel归还到内存中
	 * @param model
	 * @param time
	 */
	public synchronized void revertProxyModel(wb_proxyModel model, long time){
		
		ProxyData.put(time, model);
		
	}
	/**
	 * 指定ip已经被新浪所禁止
	 * @param model
	 */
	public synchronized void proxyOnBlocked(wb_proxyModel model){
		wb_proxyDAO dao = new wb_proxyDAO();
		
		model.setChecktime(10001);
		
		dao.update(model);
		
	}
	
	

	public Map<Long, wb_proxyModel> getProxyData() {
		return ProxyData;
	}
	public void setProxyData(Map<Long, wb_proxyModel> proxyData) {
		ProxyData = proxyData;
	}

	public List<wb_proxyModel> getBlockData() {
		return blockData;
	}

	public void setBlockData(List<wb_proxyModel> blockData) {
		this.blockData = blockData;
	}
	
	
	
}
