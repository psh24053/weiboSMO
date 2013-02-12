package com.psh.query.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
import org.jsoup.select.Elements;

import com.psh.base.json.JSONException;
import com.psh.base.json.JSONObject;
import com.psh.base.util.PshLogger;
import com.psh.query.bean.ProxyBean;
import com.psh.query.bean.UserBean;
import com.psh.query.bean.UserQueryTaskBean;
import com.psh.query.service.LoginService;
import com.psh.query.util.CookieData;
import com.psh.query.util.ProxyManager;
import com.psh.query.util.QueryResultAnalysis;
import com.psh.query.util.TotalPageNumber;

public class GetFirstQueryPageNumber{
	
	public int getFirstPageNumber(String url){
		
		if(LoginService.login == null){
			LoginService.login = LoginService.Login_3G_Sina("287540517@qq.com", "penglang7456");
		}
		
		if(LoginService.login == null){
			return -1;
		}
		
			
		JSONObject json = LoginService.login.executeJSON(url, false, null,"get");
		
		if(json == null){
			return -1;
		}
		
		
			
		QueryResultAnalysis analysis = new QueryResultAnalysis();
		return analysis.getFirstQueryPageNumber(json);
					
			
	}
	
	public static void main(String[] args) {
		GetFirstQueryPageNumber gfqpn = new GetFirstQueryPageNumber();
		System.out.println("最终页数" + gfqpn.getFirstPageNumber("http://m.weibo.cn/searchs/user?q=%E5%9B%9B%E5%B7%9D%E6%88%90%E9%83%BD&page=1"));
	}
	
}
