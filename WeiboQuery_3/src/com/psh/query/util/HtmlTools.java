package com.psh.query.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.params.CookieSpecPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;

import com.psh.base.json.JSONException;
import com.psh.base.json.JSONTokener;

public class HtmlTools {

	public static void main(String[] args) {
		
		System.out.println(decodeUnicode("u68eeu548cu7b54u5929u4e0bu7f51u5546u4e3bu7f16u8bb8u7ef4"));
		
	}
	/**
	 * 将unicode转换为汉字
	 * @param str
	 * @return
	 */
	public static String decodeUnicode(String str){
		
		if(str.matches('\\'+'\\'+"u\\w{4}")){
			try {
				return new JSONTokener("'"+str+"'").nextValue().toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			
			Pattern p = Pattern.compile("u\\w{4}");
			Matcher m = p.matcher(str);
			String result = "";
			while(m.find()){
				try {
					result += new JSONTokener("'\\"+m.group()+"'").nextValue().toString();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			return result;
		}
		return null;
		
	}
	/**
	 * 传入url，获取html内容
	 * @param url
	 * @return
	 */
	public static String getHtml(String url, String charset){
		
		HttpClient httpclient = new DefaultHttpClient();
		
		HttpGet httpGet = new HttpGet(url);
		
		HttpResponse httpResponse = null;
		
		try {
			httpResponse = httpclient.execute(httpGet);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(httpResponse != null){
			try {
				return getHtml(httpResponse.getEntity(), charset);
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
		}
		
		return null;
	}
	/**
	 * 根据传入的file，获取file内容
	 * @param file
	 * @return
	 */
	public static String getFileContent(File file){
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String temp = "";
		String content = "";
		try {
			while((temp = reader.readLine()) != null){
				content += temp;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return content;
	}
	/**
	 * 传入Url,获取html内容
	 * @param url
	 * @return
	 */
	public static String getHtmlByHttpClient(String url){
		HttpClient httpClient = new DefaultHttpClient();
		//伪装成Firefox 5, 
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1); 
		httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY); // 一定要有，否则会生成多个Cookie header送给web server 
		httpClient.getParams().setParameter(CookieSpecPNames.SINGLE_COOKIE_HEADER, true); //
		httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,"UTF-8"); //这个是和目标网站的编码有关；
		httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_ELEMENT_CHARSET,"UTF-8"); 
		List<BasicHeader> headerList = new ArrayList<BasicHeader>(); 
		headerList.add(new BasicHeader("Accept", "*/*")); 
		headerList.add(new BasicHeader("Connection", "keep-alive"));
		httpClient.getParams().setParameter(ClientPNames.DEFAULT_HEADERS, headerList);
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(30000)); 
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  new Integer(30000) ); 
		httpClient.getParams().setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, new Long(30000)); // second;
		httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/17.0 Firefox/17.0");
		
		HttpGet httpGet = new HttpGet(url);
		
		HttpResponse httpResponse = null;
		
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		try {
			return getHtmlByBr(httpResponse.getEntity());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return null;
	}
	/**
	 * 传入url，获取html内容
	 * @param url
	 * @return
	 */
	public static String getHtmlByBr(String url){
		
		HttpClient httpclient = new DefaultHttpClient();
		
		HttpGet httpGet = new HttpGet(url);
		
		HttpResponse httpResponse = null;
		
		try {
			httpResponse = httpclient.execute(httpGet);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(httpResponse != null){
			try {
				return getHtmlByBr(httpResponse.getEntity());
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
		}
		
		return null;
	}
	/**
	 * 传入url，获取html内容
	 * @param url
	 * @return
	 */
	public static String getHtml(String url){
		
		HttpClient httpclient = new DefaultHttpClient();
		
		HttpGet httpGet = new HttpGet(url);
		
		HttpResponse httpResponse = null;
		
		try {
			httpResponse = httpclient.execute(httpGet);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(httpResponse != null){
			try {
				return getHtml(httpResponse.getEntity());
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
		}
		
		return null;
	}
	
	/**
	 * 获取html，传入inputstream
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static String getHtml(InputStream input) throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(input,"UTF-8"));
		
		String temp = "";
		String result = "";
		
		while((temp = in.readLine()) != null){
			result += temp.trim();
		}
		
		in.close();
		
		return result;
	}
	/**
	 * 获取html
	 * @param entity
	 * @return
	 * @throws IOException 
	 * @throws IllegalStateException 
	 * @throws UnsupportedEncodingException 
	 */
	public static String getHtml(HttpEntity entity, String charset) throws UnsupportedEncodingException, IllegalStateException, IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent(),charset));
		
		String temp = "";
		String result = "";
		
		while((temp = in.readLine()) != null){
			result += temp.trim();
		}
		
		in.close();
		
		return result;
	}
	/**
	 * 获取html
	 * @param entity
	 * @return
	 * @throws IOException 
	 * @throws IllegalStateException 
	 * @throws UnsupportedEncodingException 
	 */
	public static String getHtml(HttpEntity entity) throws UnsupportedEncodingException, IllegalStateException, IOException{
		if(entity == null){
			return null;
		}
		
		BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent(),"UTF-8"));
		
		String temp = "";
		String result = "";
		
		while((temp = in.readLine()) != null){
			result += temp.trim();
		}
		
		in.close();
		
		return result;
	}
	/**
	 * 获取html,String支持换行
	 * @param entity
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static String getHtmlByBr(HttpEntity entity) throws UnsupportedEncodingException, IllegalStateException, IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent(),"UTF-8"));
		
		String temp = "";
		String result = "";
		
		while((temp = in.readLine()) != null){
			result += temp.trim() + "\n";
		}
		
		in.close();
		
		return result;
	}
	/**
	 * 获取html,String支持换行
	 * @param entity
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static String getHtmlByBr(HttpResponse httpResponse, String charset){
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(),charset));
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
		
		String temp = "";
		String result = "";
		
		try {
			while((temp = in.readLine()) != null){
				result += temp.trim() + "\n";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			return null;
		}
		
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	/**
	 * 获取html,String支持换行
	 * @param entity
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static String getHtmlByBr(HttpResponse httpResponse, boolean a, String str){
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(),"UTF-8"));
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
		
		String temp = "";
		String result = "";
		
		try {
			while((temp = in.readLine()) != null){
				if(temp.trim().indexOf(str) != -1){
					result = temp.trim();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			return null;
		}
		
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	/**
	 * 获取html,String支持换行
	 * @param entity
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static String getHtmlByBr(HttpResponse httpResponse){
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(),"UTF-8"));
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
		
		String temp = "";
		String result = "";
		
		try {
			while((temp = in.readLine()) != null){
				result += temp.trim() + "\n";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			return null;
		}
		
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	/**
	 * 获取html,String支持换行
	 * @param entity
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static String getHtmlByBr(HttpEntity entity, String charset) throws UnsupportedEncodingException, IllegalStateException, IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent(),charset));
		
		String temp = "";
		String result = "";
		
		while((temp = in.readLine()) != null){
			result += temp.trim() + "\n";
		}
		
		in.close();
		
		return result;
	}
	
	/**
	 * 获取html,String支持换行
	 * @param entity
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static String getHtmlByBr(InputStream input) throws UnsupportedEncodingException, IllegalStateException, IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(input,"UTF-8"));
		
		String temp = "";
		String result = "";
		
		while((temp = in.readLine()) != null){
			result += temp.trim() + "\n";
		}
		
		in.close();
		
		return result;
	}
	
	
	public static Map<String, String> parseURLParamster(String url){
		Map<String, String> map = new HashMap<String, String>();
		
		String[] s = url.split("\\?");
		
		if(s.length < 2){
			return null;
		}
		String[] prms = s[1].split("&"); 
		
		for(int i = 0 ; i < prms.length ; i ++){
			String[] content = prms[i].split("=");
			if(content.length < 2){
				continue;
			}
			String key = content[0];
			String value = content[1];
			map.put(key, value);
		}
		
		
		return map;
	}
	
}

