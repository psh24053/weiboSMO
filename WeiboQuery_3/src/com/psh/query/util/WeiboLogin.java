package com.psh.query.util;

import java.io.*;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

public class WeiboLogin {
	
	private static final String LOGINURL = "http://login.sina.com.cn/sso/login.php?";
	private String email = "287540517@qq.com";
	private String password = "penglang7456";
	HttpClient client=new HttpClient();
	
	public HttpClient login(){
		
		PostMethod method = new PostMethod(LOGINURL);
		NameValuePair emailpair = new NameValuePair("username", email);
		NameValuePair passwordpair = new NameValuePair("password", password);
		method.addParameters(new NameValuePair[]{emailpair,passwordpair});
		
		int statuscode = 0;
		
		try {
			statuscode = client.executeMethod(method);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(statuscode == HttpStatus.SC_OK)
		{
     		try {
				method.releaseConnection();
				GetMethod get_method = new GetMethod("http://t.sina.com.cn/");
				client.executeMethod(get_method);
				
				InputStream in1 = get_method.getResponseBodyAsStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in1, "utf-8"));
				String s;
				while((s =br.readLine())!=null)
				{
					System.out.println(s);
				}
				
				get_method.releaseConnection();
		        
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			method.releaseConnection();
			return client;
		}
		
		method.releaseConnection();
		
		if(statuscode == HttpStatus.SC_MOVED_TEMPORARILY || statuscode == HttpStatus.SC_MOVED_TEMPORARILY)
		{
			Header head = method.getResponseHeader("location");
			String headvalue = head.getValue();
			GetMethod getmethod = new GetMethod(headvalue);
			try {
				int statuscodel = client.executeMethod(getmethod);
				if(statuscodel== HttpStatus.SC_OK)
					return client;
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
		
	}
	
	public static void main(String[] args) {
		WeiboLogin wl = new WeiboLogin();
		if(wl.login() == null){
			System.out.println("**************************");
		}else{
			System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		}
	}
	
}
