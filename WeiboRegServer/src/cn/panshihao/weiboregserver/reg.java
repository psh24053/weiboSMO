package cn.panshihao.weiboregserver;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;



public class reg {

	
	
	
	public static void main(String[] args) throws IOException {
		
		/**
		 * 注册逻辑：
		 * 1.首先在VPN环境下访问 http://weibo.com/signup/mobile.php;
		 * 2.从该页面上得到各种表单项;
		 * 3.拉取验证码图片进行打码验证
		 * 4.发送注册表单
		 */
		
		
		
		// 访问 http://weibo.com/signup/mobile.php;
		HttpClient httpClient = new DefaultHttpClient();
		
		HttpPost httpPost = new HttpPost("http://weibo.com/signup/mobile.php");
		
		HttpResponse httpResponse = httpClient.execute(httpPost);
		
		HttpEntity httpEntity = httpResponse.getEntity();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(httpEntity.getContent(), "UTF-8"));
		
		String result = "";
		String item = "";
		while((item = in.readLine()) != null){
			result += item.trim()+"\n";
		}
		
		
		// 从该页面上得到各种表单项
		/*
		 * 12个随机码
		 * appsrc			{input hidden}
		 * backurl			{input hidden}
  		 * callback			{input hidden}
  		 * inviteCode		{input hidden}
  		 * invitesource		{input hidden}
  		 * lang				{input hidden}
  		 * mbk				{input hidden}
  		 * mcode			{input hidden}
  		 * nickname			{input text *昵称}
  		 * page				{input hidden}		
  		 * passport			{input text 护照}
  		 * passwd			{input text *密码}
  		 * pincode			{input text *验证码}
  		 * realname_PP		{input text 姓名}
  		 * regtime			{input hidden}
  		 * rejectFake		{default clickCount=7&subBtnClick=0&keyPress=43&menuClick=0&mouseMove=732&checkcode=0&subBtnPosx=545&subBtnPosy=240&subBtnDelay=94&keycode=0,0,0,0,0,0,0,0,0,0,0,0,0,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,0&winWidth=1366&winHeight=336&userAgent=Mozilla/5.0 (Windows NT 6.2; WOW64; rv:18.0) Gecko/20100101 Firefox/18.0}
  		 * salttime			{input hidden}
  		 * showlogo			{input hidden}
  		 * sinaid			{input hidden}
  		 * username			{input text *邮箱}
  		 * 
		 */
		
		
		System.out.println("已获取到 html");
		
		Document doc = Jsoup.parse(result);
		
		
		Elements elements = doc.getElementsByAttributeValue("type", "hidden");
		
		httpPost = new HttpPost("http://weibo.com/signup/v5/reg");
		
		HttpParams params = new BasicHttpParams();
		
		for(int i = 0 ; i < elements.size() ; i ++){
			String name = elements.get(i).attr("name");
			String value = elements.get(i).attr("value");
			params.setParameter(name, value);
			System.out.println("name -> "+name+" ,value -> "+value);
		}
		params.setParameter("nickname", "panshihaoooo2");
		params.setParameter("passwd", "caicai520");
		params.setParameter("username", "yun@uhomeu.com");
		params.setParameter("rejectFake", "clickCount=7&subBtnClick=0&keyPress=43&menuClick=0&mouseMove=732&checkcode=0&subBtnPosx=545&subBtnPosy=240&subBtnDelay=94&keycode=0,0,0,0,0,0,0,0,0,0,0,0,0,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,0&winWidth=1366&winHeight=336&userAgent=Mozilla/5.0 (Windows NT 6.2; WOW64; rv:18.0) Gecko/20100101 Firefox/18.0");
		
		// 拉取验证码
		
		URL url = new URL("http://weibo.com/signup/v5/pincode/pincode.php?lang=zh&sinaId="+params.getParameter("sinaid")+"&r="+params.getParameter("regtime")+"");
		
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		
		InputStream pincodeIn = url.openStream();
		byte[] readByte = new byte[1024];
		int readCount = -1;
		
		while((readCount = pincodeIn.read(readByte, 0, 1024)) != -1){
			byteOut.write(readByte);
		}
		
		String pincode = FastVerCode.INSTANCE.RecByte(byteOut.toByteArray(), url.openConnection().getContentLength(), "psh24053", "2227976");
		params.setParameter("pincode", pincode);
		System.out.println("已获取到验证码 -> "+pincode);
		
		
		
		
		// 发送表单
		httpPost.setParams(params);
		
		httpResponse = httpClient.execute(httpPost);
		
		httpEntity = httpResponse.getEntity();
		
		StringEntity entity = (StringEntity) httpEntity;
		
		System.out.println("最终返回值 -> "+entity);
		
	}
	
	
	
	
}
