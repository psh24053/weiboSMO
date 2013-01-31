package cn.panshihao.register.tools;


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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import cn.panshihao.desktop.commons.Log;

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
			Log.log.error(e.getMessage());
			return false;
		} catch (IOException e) {
			Log.log.error(e.getMessage());
			return false;
		} 
		
		
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(new File(tempDir, sinaId+".jpg"));
		} catch (FileNotFoundException e) {
			Log.log.error(e.getMessage());
			return false;
		}
		try {
			httpresponse.getEntity().writeTo(out);
			out.flush();
			out.close();
		} catch (IOException e) {
			Log.log.error(e.getMessage());
			return false;
		}
		
		
		String result = null;
		try {
			result = FastVerCode.INSTANCE.RecYZM("\\temp\\"+sinaId+".jpg", "psh24053", "2227976");
		} catch(UnsatisfiedLinkError e){
			Log.log.error(e.getMessage());
			return false;
		}
		
		if(result == null){
			return false;
		}
		
		Log.log.debug("pincode result -> "+result);
		
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
			Log.log.error("ReportError Exception Message: "+e.getMessage());
		}
		
		try {
			httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			Log.log.error(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.log.error(e.getMessage());
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
	
	
}
