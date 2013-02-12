package com.psh.query.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
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

import com.psh.base.util.PshLogger;
import com.psh.query.bean.ProxyBean;
import com.psh.query.model.wb_proxyDAO;
import com.psh.query.util.HtmlTools;





public class ProxyService {

	
	public static final long ProxyDelay = 300000;
	
	/**
	 * 代理服务器数据
	 */
	private Map<Long, ProxyBean> ProxyData = new HashMap<Long, ProxyBean>();
	
	/**
	 * 被封杀的代理服务器
	 */
	private List<ProxyBean> blockData = new ArrayList<ProxyBean>();
	
	
	private List<ProxyBean> timeOutData = new ArrayList<ProxyBean>();
	
	public void loadProxyDataStatic(int count){
		
		
		String html = HtmlTools.getHtmlByBr("http://cn.yunproxy.com/apilist/uid/910/api_format/1/country/CN,US,CA,MX,CR,PA,CU,JM,HT,PR,GB,FR,DE,RU,FI,SE,NO,IS,DK,EE,LT,UA,CZ,SK,AT,CH,IE,NL,BE,RO,BG,GR,SI,HR,IT,ES,PT,PL,JP,KR,KP,IN,TR,IL,MN,AF,KH,ID,LA,MM,MY,PH,SG,TH,VN,SY,MV,PK,IR,KZ,UZ,BH,KW,QA,SA,AE,IQ,AU,NZ,BR,AR,CL,UY,PY,CO,VE,EC,PE,ZA,CG,LR,CM,SO,EG,LY,MA,ET,DZ/");
		String[] hosts = html.split("\n");
		
		System.out.println("Yun Proxy Count "+hosts.length);
		ProxyData.clear();
		
		for(int i = 0 ; i < count ; i ++){
			String host = hosts[i];
			final String ip = host.split(":")[0];
			final int port = Integer.parseInt(host.split(":")[1]);
			final int index = i;
			ProxyBean item = new ProxyBean();
			item.setIp(ip);
			item.setPort(port);
			long time = System.currentTimeMillis() - ProxyDelay + index;
			item.setChecktime(time);
			if(validationProxy(ip, port)){
				System.out.println("add Proxy "+ip +" , "+port);
				ProxyData.put(time, item);
			}
			
		}
		if(ProxyData.size() != count){
			loadProxyDataStatic(count);
			return;
		}
		
		
		
		blockData.clear();
		timeOutData.clear();
	}
	
	
	public void loadProxyData(){
//		ProxyData.clear();
//		blockData.clear();
//		timeOutData.clear();
//		
//		wb_proxyDAO dao = new wb_proxyDAO();
//		List<ProxyBean> data = dao.selectByAvailable();
//		
//		for(int i = 0 ; i < data.size() ; i ++){
//			ProxyData.put(System.currentTimeMillis() - ProxyDelay + i, data.get(i));
//		}
		
		
		String html = HtmlTools.getHtmlByBr("http://cn.yunproxy.com/apilist/uid/910/api_format/1/country/CN/");
		String[] hosts = html.split("\n");
		
		System.out.println("Yun Proxy Count "+hosts.length);
		ProxyData.clear();
		ExecutorService s = Executors.newFixedThreadPool(100);
		
		for(int i = 0 ; i < hosts.length ; i ++){
			String host = hosts[i];
			final String ip = host.split(":")[0];
			final int port = Integer.parseInt(host.split(":")[1]);
			final int index = i;
			s.execute(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ProxyBean item = new ProxyBean();
					item.setIp(ip);
					item.setPort(port);
					long time = System.currentTimeMillis() - ProxyDelay + index;
					item.setChecktime(time);
					if(validationProxy(ip, port)){
						System.out.println("add Proxy "+ip +" , "+port);
						ProxyData.put(time, item);
					}
				}
			});
			
		}
		
		
		
		blockData.clear();
		timeOutData.clear();
	}
	
//	/**
//	 * 从数据库中加载代理服务器数据
//	 */
//	public void loadProxyData(){
//		
//		wb_proxyDAO dao = new wb_proxyDAO();
//		
//		List<ProxyBean> data = dao.selectByAvailable();
//		
//		ProxyData.clear();
//		
//		for(int i = 0 ; i < data.size() ; i ++){
//			ProxyData.put(System.currentTimeMillis() - ProxyDelay + i, data.get(i));
//		}
//		
//		
//		
//		
//		blockData.clear();
//		timeOutData.clear();
//		
//	}
	/**
	 * 验证代理数据，将可用的留下
	 */
	public void ValidationProxyData(){
		
		for(Long key : ProxyData.keySet()){
			ProxyBean model = ProxyData.get(key);
			
			if(!validationProxy(model.getIp(), model.getPort())){
				model.setChecktime(110);
			}
			
		}
		
		
		System.out.println("Validation Proxy Data Success!");
		
	}
	
	public void loadYunProxyCN(){
		ExecutorService executorService = Executors.newFixedThreadPool(100);
		
		String html = HtmlTools.getHtmlByBr("http://cn.yunproxy.com/apilist/uid/910/api_format/1/country/US,CA,MX,CR,PA,CU,JM,HT,PR,GB,FR,DE,RU,FI,SE,NO,IS,DK,EE,LT,UA,CZ,SK,AT,CH,IE,NL,BE,RO,BG,GR,SI,HR,IT,ES,PT,PL,CN,JP,KR,KP,IN,TR,IL,MN,AF,KH,ID,LA,MM,MY,PH,SG,TH,VN,SY,MV,PK,IR,KZ,UZ,BH,KW,QA,SA,AE,IQ,AU,NZ,BR,AR,CL,UY,PY,CO,VE,EC,PE,ZA,CG,LR,CM,SO,EG,LY,MA,ET,DZ/");
		String[] hosts = html.split("\n");
		
		PshLogger.logger.debug("Yun Proxy Count "+hosts.length);
		for(int i = 0 ; i < hosts.length ; i ++){
			String host = hosts[i];
			final String ip = host.split(":")[0];
			final int port = Integer.parseInt(host.split(":")[1]);
			
			executorService.execute(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(validationProxy(ip, port)){
						wb_proxyDAO dao = new wb_proxyDAO();
						ProxyBean model = new ProxyBean();
						model.setChecktime(System.currentTimeMillis());
						model.setIp(ip);
						model.setPort(port);
						
						dao.insert(model);
						
					}
				}
			});
		}
		
	}
	
	/**
	 * 获取xici_wt的http代理
	 * url:http://www.xici.net.co/wt
	 * @return
	 * @throws IOException 
	 */
	private List<ProxyBean> get_xici_wt() {
		
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
							ProxyBean model = new ProxyBean();
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
	 * 获取xici_nn的http代理
	 * url:http://www.xici.net.co/nn
	 * @return
	 * @throws IOException 
	 */
	private List<ProxyBean> get_xici_nn() {
		
		ExecutorService service = Executors.newFixedThreadPool(100);
		
		for(int j = 1 ; j < 76 ; j ++){
			URL url = null;
			try {
				url = new URL("http://www.xici.net.co/nn/"+j);
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
							ProxyBean model = new ProxyBean();
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
	 * 获取xici_nt的http代理
	 * url:http://www.xici.net.co/nt
	 * @return
	 * @throws IOException 
	 */
	private List<ProxyBean> get_xici_nt() {
		
		ExecutorService service = Executors.newFixedThreadPool(100);
		
		for(int j = 1 ; j < 3 ; j ++){
			URL url = null;
			try {
				url = new URL("http://www.xici.net.co/nt/"+j);
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
							ProxyBean model = new ProxyBean();
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
	private List<ProxyBean> get_xici_wn() {
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
							ProxyBean model = new ProxyBean();
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
	private List<ProxyBean> get_51daili_fast() {
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
			
			service.execute(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(validationProxy(ip, port)){
						wb_proxyDAO dao = new wb_proxyDAO();
						ProxyBean model = new ProxyBean();
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
	private List<ProxyBean> get_51daili_anonymous(){
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
						ProxyBean model = new ProxyBean();
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
	private List<ProxyBean> get_51daili_non_anonymous() {
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
			
			if(ee.size() < 2){
				continue;
			}
			
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
						ProxyBean model = new ProxyBean();
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
	private List<ProxyBean> get_cnproxy(){
		return null;
	}
	/**
	 * 获取sitedigger的http代理数据
	 * url:http://www.site-digger.com/html/articles/20110516/proxieslist.html
	 * @return
	 */
	private List<ProxyBean> get_sitedigger(){
		
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
			
			if(ee.size() < 2){
				continue;
			}
			
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
						ProxyBean model = new ProxyBean();
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
	 * 获取cz88的http代理数据
	 * url:http://www.cz88.net/proxy/index.aspx
	 * @return
	 */
	private List<ProxyBean> get_cz88(){
		return null;
	}
	/**
	 * 获取veryhuo的http代理数据
	 * url:http://www.veryhuo.com/res/ip/
	 * @return
	 */
	private List<ProxyBean> get_veryhuo(){
		return null;
	}
	/**
	 * 获取56ads的http代理数据
	 * url:http://www.56ads.com/proxyip/
	 * @return
	 */
	private List<ProxyBean> get_56ads(){
		return null;
	}
	/**
	 * 获取5753的http代理
	 * url:http://www.5753.net/proxy/
	 * @return
	 */
	private List<ProxyBean> get_5753(){
		return null;
	}
	/**
	 * 获取CZ88 BBS的本地文件数据
	 * @param filename
	 * @return
	 */
	private List<ProxyBean> getCZ88BBS_LOCAL_FILE(String filename){
		
		File file = new File(filename);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return null;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			return null;
		}
		
		String temp = "";
		
		try {
			while((temp = reader.readLine()) != null){
				
				String[] hostContent = temp.split("#");
				
				final String address = hostContent[1];
				
				String hostInfo = hostContent[0];
				
				String host = hostInfo.substring(0, hostInfo.indexOf("$"));
				
				String[] hostMain = host.split(":");
				
				final String ip = hostMain[0];
				final int port = Integer.parseInt(hostMain[1]);
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(validationCountry(address)){
							
							if(validationProxy(ip, port)){
								wb_proxyDAO dao = new wb_proxyDAO();
								ProxyBean model = new ProxyBean();
								model.setChecktime(System.currentTimeMillis());
								model.setIp(ip);
								model.setPort(port);
								
								dao.insert(model);
								
							}
							
							
						}
						
					}
				}).start();
				
				
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		}
		
		
		
		
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
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(3000)); 
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  new Integer(3000) ); 
		httpClient.getParams().setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, new Long(3000)); // second;
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
			PshLogger.logger.error("html get error!");
			return false;
		}
		
		
		return true;
	}
	/**
	 * 判断国家是否为中国，为中国返回false
	 * @param address
	 * @return
	 */
	public boolean validationCountry(String address){
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
	/**
	 * http://www.cz88.net/proxy/index.aspx
	 */
	public void loadCZ88(){
		for(int i = 1 ; i < 11 ; i ++){
			String html = null;
			if(i == 1){
				html = HtmlTools.getHtml("http://www.cz88.net/proxy/index.aspx");
			}else{
				html = HtmlTools.getHtml("http://www.cz88.net/proxy/http_"+i+".aspx");
			}
			
			Document doc = Jsoup.parse(html);
			
			Elements trs = doc.select("tbody tr");
			
			for(int j = 1 ; j < trs.size() ; j ++){
				Element tr = trs.get(j);
				Elements tds = tr.select("td");
				
				if(tds.size() < 2){
					continue;
				}
				
				final String ip = tds.get(0).text();
				final int port = Integer.parseInt(tds.get(1).text());
				final String address = tds.get(4).text();
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(validationCountry(address)){
							if(validationProxy(ip, port)){
								wb_proxyDAO dao = new wb_proxyDAO();
								ProxyBean model = new ProxyBean();
								model.setChecktime(System.currentTimeMillis());
								model.setIp(ip);
								model.setPort(port);
								
								dao.insert(model);
								
							}
						}
						
						
					}
				}).start();
				
				
			}
			
			
			
		}
	}
	
	
//	/**
//	 * http://www.freeproxylists.net/zh/?page=1
//	 */
//	public void loadFreeProxyLists(){
//		for(int i = 1 ; i < 36 ; i ++){
//			String html = HtmlTools.getFileContent(new File("freeproxylists",i+".htm"));
//			Document doc = Jsoup.parse(html);
//			Elements trs = doc.select(".DataGrid tbody tr");
//			
//			
//			for(int j = 1 ; j < trs.size() ; j ++){
//				Element tr = trs.get(j);
//				Elements tds = tr.select("td");
//				
//				if(tds.size() < 2){
//					continue;
//				}
//				
//				final String ip = tds.get(0).text();
//				final int port = Integer.parseInt(tds.get(1).text());
//				
//				String address = tds.get(4).text();
//				if(address.equals("中国")){
//					continue;
//				}
//				
//				new Thread(new Runnable() {
//					
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						if(validationProxy(ip, port)){
//							wb_proxyDAO dao = new wb_proxyDAO();
//							ProxyBean model = new ProxyBean();
//							model.setChecktime(System.currentTimeMillis());
//							model.setIp(ip);
//							model.setPort(port);
//							
//							dao.insert(model);
//							
//						}
//					}
//				}).start();
//				
//			}
//			
//		}
//	}
	
	/**
	 * http://www.cool-proxy.net/proxies/http_proxy_list/page:1/sort:ip/direction:asc
	 */
	public void loadCoolProxy(){
		for(int i = 1 ; i < 21 ; i ++){
			String html = HtmlTools.getHtml("http://www.cool-proxy.net/proxies/http_proxy_list/page:"+i+"/sort:ip/direction:asc");
			
			Document doc = Jsoup.parse(html);
			Elements trs = doc.select("tbody tr");
			
			for(int j = 1 ; j < trs.size() ; j ++){
				Element tr = trs.get(j);
				Elements tds = tr.select("td");
				
				if(tds.size() < 2){
					continue;
				}
				
				Element iptd = tds.get(0);
				Element porttd = tds.get(1);
				
				Elements spans = iptd.getElementsByAttribute("class");
				
				final String ip = spans.get(0).text() + "."+spans.get(1).text()+ "."+spans.get(2).text()+ "."+spans.get(3).text();
				final int port = Integer.parseInt(porttd.text());
				
				String address = tds.get(3).text();
				
				if(address.equals("China")){
					continue;
				}
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(validationProxy(ip, port)){
							wb_proxyDAO dao = new wb_proxyDAO();
							ProxyBean model = new ProxyBean();
							model.setChecktime(System.currentTimeMillis());
							model.setIp(ip);
							model.setPort(port);
							
							dao.insert(model);
							
						}
					}
				}).start();
				
				
			}
			
			
		}
		
		
	}
	/**
	 * http://www.site-digger.com/html/articles/20110516/proxieslist.html
	 */
	public void loadSitedigger(){
		String html = HtmlTools.getHtml("http://www.site-digger.com/html/articles/20110516/proxieslist.html");
		
		Document doc = Jsoup.parse(html);
		
		Elements trs = doc.select("tbody tr");
		
		for(int i = 0 ; i < trs.size() ; i ++){
			Element tr = trs.get(i);
			
			Elements tds = tr.select("td");
			
			String host = tds.get(0).text();
			
			String address = tds.get(2).text().trim();
			
			if(address.equals("China")){
				continue;
			}
			
			final String ip = host.split(":")[0];
			final int port = Integer.parseInt(host.split(":")[1]);
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(validationProxy(ip, port)){
						wb_proxyDAO dao = new wb_proxyDAO();
						ProxyBean model = new ProxyBean();
						model.setChecktime(System.currentTimeMillis());
						model.setIp(ip);
						model.setPort(port);
						
						dao.insert(model);
						
					}
				}
			}).start();
			
		}
		
		
		
	}
//	/**
//	 * http://proxy.linktool.org/
//	 */
//	public void loadProxy_LinkTool(){
//		ExecutorService service = Executors.newFixedThreadPool(100);
//		URL url = null;
//		try {
//			url = new URL("http://proxy.linktool.org/");
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		String html = null;
//		try {
//			html = HtmlTools.getHtml(url.openStream());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		Document doc = Jsoup.parse(html);
//		Elements elements = doc.select("dd");
//		
//		for(int i = 0 ; i < elements.size() ; i ++){
//			Element e = elements.get(i);
//			String host = e.text();
//			
//			final String ip = host.split(":")[0];
//			final int port = Integer.parseInt(host.split(":")[1]);
//			service.execute(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				if(validationCountry(ipAddress(ip))){
//					if(validationProxy(ip, port)){
//						wb_proxyDAO dao = new wb_proxyDAO();
//						ProxyBean model = new ProxyBean();
//						model.setChecktime(System.currentTimeMillis());
//						model.setIp(ip);
//						model.setPort(port);
//						
//						dao.insert(model);
//						
//					}
//				}
//			}
//			});
//			
//			
//		}
//		
//		service.shutdown();
//	}
	
//	/**
//	 * 根据ip来获取地址
//	 * @param ip
//	 * @return
//	 */
//	public String ipAddress(String ip){
//		String html = HtmlTools.getHtml("http://ip138.com/ips138.asp?ip="+ip,"GBK");
//		
//		Document doc = Jsoup.parse(html);
//		
//		Elements es = doc.select(".ul1 li");
//		
//		if(es.size() < 1){
//			return "未知";
//		}
//		
//		String address = es.get(0).html();
//		
//		
//		return address.substring(address.indexOf("：")+1);
//	}
	
	public static void main(String[] args) {
		ProxyService s = new ProxyService();
		
		s.loadYunProxyCN();
		s.get_xici_nn();
		s.get_xici_nt();
		s.get_51daili_fast();
		s.get_51daili_anonymous();
		s.get_51daili_non_anonymous();
		s.get_xici_wn();
		s.get_xici_wt();
		s.getCZ88BBS_LOCAL_FILE("proxy.txt");
		
		s.loadSitedigger();
		s.loadCoolProxy();
	}
	
	/**
	 * 获取一个可用的proxy
	 * @return
	 */
	public ProxyBean getAvailableProxyModel(){
		ProxyBean model = getRandomProxyModel();
		
		if(validationProxy(model.getIp(), model.getPort())){
			return model;
		}
		getTimeOutData().add(model);
		
		return getAvailableProxyModel();
	}
	/**
	 * 获取一个代理服务器对象
	 * 每一个代理服务器的使用间隔为30秒
	 * @return
	 */
	public synchronized ProxyBean getRandomProxyModelForStatic(){
		if(ProxyData.size() == 0){
			return null;
		}
		
		for(Long key : ProxyData.keySet()){
			long curTime = System.currentTimeMillis();
			ProxyBean model = ProxyData.get(key);
			if(model.getChecktime() == 110){
				continue;
			}
			
			if(curTime - key > ProxyDelay){
				return ProxyData.remove(key);
			}
			
		}
		
		if(timeOutData.size() > 0){
			long curTime = System.currentTimeMillis();
			long key = timeOutData.get(0).getChecktime();
			if(curTime - key > ProxyDelay){
				return timeOutData.remove(0);
			}
		}
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			PshLogger.logger.error(e.getMessage(), e);
		}
		
		return getRandomProxyModel();

	}
	/**
	 * 获取一个代理服务器对象
	 * 每一个代理服务器的使用间隔为30秒
	 * @return
	 */
	public synchronized ProxyBean getRandomProxyModel(){
		if(ProxyData.size() == 0){
			loadProxyData();
			return getRandomProxyModel();
		}
		
		for(Long key : ProxyData.keySet()){
			long curTime = System.currentTimeMillis();
			ProxyBean model = ProxyData.get(key);
			if(model.getChecktime() == 110){
				continue;
			}
			
			if(curTime - key > ProxyDelay){
				return ProxyData.remove(key);
			}
			
		}
		
		if(timeOutData.size() > 0){
			long curTime = System.currentTimeMillis();
			long key = timeOutData.get(0).getChecktime();
			if(curTime - key > ProxyDelay){
				return timeOutData.remove(0);
			}
		}
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			PshLogger.logger.error(e.getMessage(), e);
		}
		
		return getRandomProxyModel();

	}

	/**
	 * 将使用过的proxymodel归还到内存中
	 * @param model
	 * @param time
	 */
	public synchronized void revertProxyModel(ProxyBean model, long time){
		
		ProxyData.put(time, model);
		
	}
	/**
	 * 指定ip已经被新浪所禁止
	 * @param model
	 */
	public synchronized void proxyOnBlocked(ProxyBean model){
		wb_proxyDAO dao = new wb_proxyDAO();
		
		model.setChecktime(10001);
		
		dao.update(model);
		
	}
	
	

	public Map<Long, ProxyBean> getProxyData() {
		return ProxyData;
	}
	public void setProxyData(Map<Long, ProxyBean> proxyData) {
		ProxyData = proxyData;
	}

	public List<ProxyBean> getBlockData() {
		return blockData;
	}

	public void setBlockData(List<ProxyBean> blockData) {
		this.blockData = blockData;
	}
	public List<ProxyBean> getTimeOutData() {
		return timeOutData;
	}
	public void setTimeOutData(List<ProxyBean> timeOutData) {
		this.timeOutData = timeOutData;
	}
	
	
	
}
