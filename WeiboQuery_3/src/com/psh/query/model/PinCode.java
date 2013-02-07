package com.psh.query.model;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;



import com.psh.query.util.FastVerCode;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.WString;

public class PinCode {

	public static final String YZM_USER = "psh24053";
	public static final String YZM_PASS = "2227976";
	
	public static File tempDir = null;
	static{
		tempDir = new File("/temp");
		tempDir.mkdirs();
	}
	
	private String pincode;
	private String anthor;
	private String sinaId;
	private String regtime;
	private int error_total = 0;
	
	
	private String url;
	
	public PinCode(String sinaId, String regtime){
		this.sinaId = sinaId;
		this.regtime = regtime;
	}

	public PinCode(String url){
		this.url = url;
	}
	
	public String getCode(HttpClient httpClient, String capId, String url){
		
		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpresponse = null;
		try {
			httpresponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		} 
		
		File file = new File(tempDir, capId+".jpg");
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			return null;
		}
		try {
			httpresponse.getEntity().writeTo(out);
			out.flush();
			out.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		FileBody filebody = new FileBody(file);
		
		MultipartEntity mulEntity = new MultipartEntity();
		try {
			mulEntity.addPart("info[lz_user]", new StringBody("psh24053"));
			mulEntity.addPart("info[lz_pass]", new StringBody("2227976"));
			mulEntity.addPart("pesubmit", new StringBody(""));
			mulEntity.addPart("imagepath",filebody);
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		HttpPost httpPost = new HttpPost("http://api.yzmbuy.com/index.php/demo");
		httpPost.setEntity(mulEntity);
		
		try {
			httpresponse = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		if(httpresponse == null){
			return null;
		}
		
		String demoHtml = null;
		try {
			demoHtml = HtmlTools.getHtml(httpresponse.getEntity());
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
		if(demoHtml == null){
			return null;
		}
		
		String id = demoHtml.substring(demoHtml.indexOf("demo/") + 5, demoHtml.indexOf("demo/") + 13);
		String result = null;

		while(true){
			URL u = null;
			try {
				u = new URL("http://api.yzmbuy.com/index.php?mod=demo&act=result&id="+id);
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
			
			
			try {
				result = HtmlTools.getHtml(u.openStream());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			result = decodeUnicode(result);
			String code = null;
			if (result.indexOf("打码成功") != -1)
			{
				code = result.substring(result.indexOf("\"result\":") + 10, result.indexOf(",\"damaworker\"") - 1);
				return code;
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	
	
	public boolean loadPinCodeBy3G(HttpClient httpClient, String capId){
		if(error_total == 3){
			return false;
		}
		
		
		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpresponse = null;
		try {
			httpresponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			return false;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return false;
		} 
		
		
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(new File(tempDir, capId+".jpg"));
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			return false;
		}
		try {
			httpresponse.getEntity().writeTo(out);
			out.flush();
			out.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return false;
		}
		
		
		String result = null;
		try {
			result = FastVerCode.INSTANCE.RecYZM("\\temp\\"+capId+".jpg", "psh24053", "2227976");
		} catch(UnsatisfiedLinkError e){
			System.out.println(e.getMessage());
			return false;
		}
		
		if(result == null){
			return false;
		}
		
		System.out.println("pincode result -> "+result);
		
		String[] strArr = result.replace("|", "").split("!");
		
		if(strArr == null || strArr.length != 2){
			error_total ++;
			return loadPinCode(httpClient);
		}
		
		pincode = strArr[0];
		anthor = strArr[1];
		if(pincode.length() != 5){
			ReportError(anthor);
			error_total ++;
			return loadPinCode(httpClient); 
		}
		
		return true;
	}
	
	/**
	 * 拉取验证码，成功返回true，失败返回false
	 * @return
	 */
	public boolean loadPinCode(HttpClient httpclient){
		if(error_total == 3){
			return false;
		}
		HttpPost httpget = null;
		
		// 拉取验证码
		if(url != null){
			Map<String, String> maps = URLRequest(url);
			
			sinaId = maps.get("sinaid");
			regtime = maps.get("r");
			
			httpget = new HttpPost("http://www.weibo.com"+url);
			
			
		}else{
			httpget = new HttpPost("http://www.weibo.com/signup/v5/pincode/pincode.php?lang=zh&sinaId="+sinaId+"&r="+regtime);
		}
		
		
		HttpResponse httpresponse = null;
		try {
			httpresponse = httpclient.execute(httpget);
		} catch (ClientProtocolException e) {
			System.out.println(e.getMessage());
			return false;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return false;
		} 
		
		
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(new File(tempDir, sinaId+".jpg"));
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			return false;
		}
		try {
			httpresponse.getEntity().writeTo(out);
			out.flush();
			out.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return false;
		}
		
		
		String result = null;
		try {
			result = FastVerCode.INSTANCE.RecYZM("\\temp\\"+sinaId+".jpg", "psh24053", "2227976");
		} catch(UnsatisfiedLinkError e){
			System.out.println(e.getMessage());
			return false;
		}
		
		if(result == null){
			return false;
		}
		
		System.out.println("pincode result -> "+result);
		
		String[] strArr = result.replace("|", "").split("!");
		
		if(strArr == null || strArr.length != 2){
			error_total ++;
			return loadPinCode(httpclient);
		}
		
		pincode = strArr[0];
		anthor = strArr[1];
		if(pincode.length() != 4){
			ReportError(anthor);
			error_total ++;
			return loadPinCode(httpclient); 
		}
		
		return true;
	}
	/**
	 * 报告打码错误
	 * @param source
	 */
	public void ReportError(String source){
		/*WString user = new WString("psh24053");
		WString anthor = new WString(source);
		FastVerCode.INSTANCE.ReportError(user, anthor);*/
		
		
		HttpClient httpClient = new DefaultHttpClient();
		
		HttpPost httpPost = new HttpPost("http://dama3.yzmbuy.com/lz_yzmphp/update_err.php");
		
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		
		formParams.add(new BasicNameValuePair("worker", source));
		formParams.add(new BasicNameValuePair("username", "psh24053"));
		formParams.add(new BasicNameValuePair("submit", "Ìí ¼Ó"));
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(formParams,"utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			System.out.println("ReportError Exception Message: "+e.getMessage());
		}
		
		try {
			httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} 
		httpClient.getConnectionManager().shutdown();
		
		
	}
	
	

	

	public String getPincode() {
		return pincode;
	}
	public void setPincode(String pincode) {
		this.pincode = pincode;
	}
	public String getAnthor() {
		return anthor;
	}
	public void setAnthor(String anthor) {
		this.anthor = anthor;
	}
	
	
	
	/**
     * 去掉url中的路径，留下请求参数部分
     * @param strURL url地址
     * @return url请求参数部分
     */
    private static String TruncateUrlPage(String strURL)
    {
    String strAllParam=null;
      String[] arrSplit=null;
      
      strURL=strURL.trim().toLowerCase();
      
      arrSplit=strURL.split("[?]");
      if(strURL.length()>1)
      {
          if(arrSplit.length>1)
          {
                  if(arrSplit[1]!=null)
                  {
                  strAllParam=arrSplit[1];
                  }
          }
      }
      
    return strAllParam;    
    }
	/**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     * @param URL  url地址
     * @return  url请求参数部分
     */
    public static Map<String, String> URLRequest(String URL)
    {
    Map<String, String> mapRequest = new HashMap<String, String>();
    
      String[] arrSplit=null;
      
    String strUrlParam= TruncateUrlPage(URL);
    if(strUrlParam==null)
    {
        return mapRequest;
    }
      //每个键值为一组
    arrSplit=strUrlParam.split("[&]");
    for(String strSplit:arrSplit)
    {
          String[] arrSplitEqual=null;          
          arrSplitEqual= strSplit.split("[=]"); 
          
          //解析出键值
          if(arrSplitEqual.length>1)
          {
              //正确解析
              mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
              
          }
          else
          {
              if(arrSplitEqual[0]!="")
              {
              //只有参数没有值，不加入
              mapRequest.put(arrSplitEqual[0], "");        
              }
          }
    }    
    return mapRequest;    
    }
	
    /**
	 * unicode 转换成 中文
	 * 
	 * @param theString
	 * @return
	 */
	public static String decodeUnicode(String theString)
	{
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;)
		{
			aChar = theString.charAt(x++);
			if (aChar == '\\')
			{
				aChar = theString.charAt(x++);
				if (aChar == 'u')
				{
					int value = 0;
					for (int i = 0; i < 4; i++)
					{
						aChar = theString.charAt(x++);
						switch (aChar)
						{
							case '0':
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
								value = (value << 4) + aChar - '0';
								break;
							case 'a':
							case 'b':
							case 'c':
							case 'd':
							case 'e':
							case 'f':
								value = (value << 4) + 10 + aChar - 'a';
								break;
							case 'A':
							case 'B':
							case 'C':
							case 'D':
							case 'E':
							case 'F':
								value = (value << 4) + 10 + aChar - 'A';
								break;
							default:
								throw new IllegalArgumentException("Malformed      encoding.");
						}

					}
					outBuffer.append((char) value);
				} else
				{
					if (aChar == 't')
					{
						aChar = '\t';
					} else if (aChar == 'r')
					{
						aChar = '\r';
					} else if (aChar == 'n')
					{
						aChar = '\n';
					} else if (aChar == 'f')
					{
						aChar = '\f';
					}
					outBuffer.append(aChar);
				}
			} else
			{
				outBuffer.append(aChar);
			}

		}
		return outBuffer.toString();

	}
}
