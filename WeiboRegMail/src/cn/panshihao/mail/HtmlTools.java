package cn.panshihao.mail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;

public class HtmlTools {

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

