package cn.panshihao.desktop.commons;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TransDate {
	
	public static String convertTime(long time){
		if(time == 0){
			return "";
		}
		
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
		
	}
	
	public static String convertTime(long time, String format){
		if(time == 0){
			return "";
		}
		
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
	
}
