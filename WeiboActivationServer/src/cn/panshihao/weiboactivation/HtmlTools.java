package cn.panshihao.weiboactivation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class HtmlTools {

	public static void main(String[] args) {
		
		
		System.out.println(getHtml("http://weibo.com/nguide/relation"));
		
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
	public static String getHtml(HttpEntity entity) throws UnsupportedEncodingException, IllegalStateException, IOException{
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
	
}

