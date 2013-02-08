package cn.panshihao.activationclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class Import {

	public static void main(String[] args) throws IOException {
		File file = new File("e:\\内容库.txt");
		
		String content = HtmlTools.getFileContent(file);
		content.replace("\t", "{@}");
		
		
		FileOutputStream out = new FileOutputStream(new File("e:\\aa.txt"));
		out.write(content.getBytes());
		out.flush();
		out.close();
		
		
//		Connection conn = Tools.getMysqlConn();
//		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
//		Map<String, String> data = new HashMap<String, String>();
//		
//		
//		String temp = "";
//		while((temp = reader.readLine()) != null){
//			System.out.println(temp);
////			String[] items = temp.split("\t");
////			data.put(items[0].trim(), "");
//			if(temp.length() < 4){
//				continue;
//			}
//			data.put(temp.substring(0, 4), "");
//		}
//		
//		
//		for(String key : data.keySet()){
//			
//			System.out.println("insert into wb_texttype(name) values('"+key+"')");
//			
//		}
		
		
//		String line = reader.readLine();
//		
//		String[] items = line.split("\t");
//		
//		System.out.println(items.length);
//		
//		System.out.println(items[0].trim());
//		System.out.println(items[1].trim());
//		System.out.println(items[2].trim());
		
		
//		
//		if(conn != null){
//			
//			for(int i = 0 ; i < lines.length ; i ++){
//				String line = lines[i];
//				
//				
//				
//				
//			}
//			
//			
//			
//			
//		}
		
		
		
	}
	
}
