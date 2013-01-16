package cn.panshihao.weiboyx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class test {

	public static void main(String[] args) throws IOException {
		
		URL url = new URL("http://127.0.0.1:8080/weiboyx/search.html");
		
		
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
		
		byte[] readByte = new byte[1024];
		int readCount = -1;
		
		String result = "";
		String item = null;
		while((item = in.readLine()) != null){
			result += item;
		}
		
		in.close();
		
		System.out.println(result);
	}
	
}
