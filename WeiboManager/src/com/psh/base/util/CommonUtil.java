/**
 * 
 */
package com.psh.base.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 1
 *
 */
public final class CommonUtil {

	final public static Long MILLISECOND_IN_DAY = (Long)(24*60*60*1000L);
	
	public static byte[] readBinaryRequestContent (HttpServletRequest request) {
		
		byte[] content = new byte[request.getContentLength()];
		
		try {
			// Read complete request content
			InputStream ins = request.getInputStream();
			
			int ch = 0;
			int i = 0;
			while ((ch = ins.read()) != -1 ) {
				content[i++] =  ((byte)ch);
			}
			
			ins.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return content;
	}
	
	public static String readUtf8RequestContent (HttpServletRequest request) {

		char content[] = new char[request.getContentLength()];
		
		try {
			request.setCharacterEncoding("utf8");
			BufferedReader in = request.getReader();
			in.read(content);
			in.close();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return new String(content);
	}
	
	public static void writeUtf8ResponseContent (HttpServletResponse response, String responseContent) {
		
		PrintWriter out = null;

		try {
			response.setCharacterEncoding("utf8");
			out = response.getWriter();
			out.write(responseContent);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			out.close();
		}
	}
	
	// Return path looks like "/var/www/webapp/appname"
	public static String getWebappRoot() {
		
		String webappRoot = null;
		
		// /XXXX/WEB-INF/classes
		String pathOfClasses = CommonUtil.class.getClassLoader().getResource("/").getPath();;
		
		webappRoot = pathOfClasses.substring(0, pathOfClasses.indexOf("/WEB-INF/classes"));
		
		return webappRoot;
		
	}
	
	public static boolean isEmail(String text) {
		
		boolean isEmail = false;
		
		String regex ="^[a-zA-Z0-9]+([-_+.a-zA-Z0-9])*@[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$";
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		
		if(matcher.matches()) {
			isEmail = true;
		}
		
		return isEmail;
		
	}
	
	public static boolean isMobilePhoneNumber(String text){
		
		boolean isPhoneNumber = false;

		// 13812345678
		// +8613812345678
		// +86-13812345678
		// 86-13812345678
		String regex ="^[+]{0,1}[0-9]{0,3}[-]{0,1}[0-9]{11}$";
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		
		if(matcher.matches()) {
			isPhoneNumber = true;
		}
		
		
		return isPhoneNumber;
		
	}

	/* 
	 * Return : 
	 *   1 (>0): if date1 after date2
	 *   0 (=0): if date1 equal date2
	 *  -1 (<0): if date1 before date2
	 */
	public static int compareDate (Date date1, Date date2) {
		
		int ret = 0;
		
		if (date1 == null && date2 == null) {
			return ret;
		}
		
		if (date1 != null) {
			if (date2 == null) {
				ret = 1;
			}
			else {
				if (date1.after(date2)) {
					ret = 1;
				}
				else if (date1.before(date2)) {
					ret = -1;
				}
				else {
					ret = 0;
				}
			}
		}
		else {
			ret = -1;
		}
		
		return ret;
	}
	
	/* 
	 * Only compare day, without hour minute second
	 * 
	 * Return : 
	 *   1 (>0): if date1 after date2
	 *   0 (=0): if date1 equal date2
	 *  -1 (<0): if date1 before date2
	 */
	public static int compareDate1 (Date date1, Date date2) {

		int ret = 0;
		
		if (date1 == null && date2 == null) {
			return ret;
		}
		
		if (date1 != null) {
			if (date2 == null) {
				ret = 1;
			}
			else {
				
				if (timeRoundToDay(date1).after(timeRoundToDay(date2))) {
					ret = 1;
				}
				else if (timeRoundToDay(date1).before(timeRoundToDay(date2))) {
					ret = -1;
				}
				else {
					ret = 0;
				}
			}
		}
		else {
			ret = -1;
		}
		
		return ret;
	}

	/*
	 * Drop Hour, minute and second
	 **/
	public static Date timeRoundToDay (Date time) {

		Date timeInDay = null;

		Calendar cal = Calendar.getInstance();
		TimeZone timeZone = cal.getTimeZone();

		// Time in millisecond adjust by time zone offset
		Long timeAfterOffset = time.getTime() + timeZone.getRawOffset();
		// Time in millisecond after by drop hour, minute, second
		Long timeAfterRound = (MILLISECOND_IN_DAY) * (timeAfterOffset / MILLISECOND_IN_DAY);
		// Adjust time back to current time zone
		cal.setTimeInMillis(timeAfterRound - timeZone.getRawOffset());

		timeInDay = cal.getTime();
		
		return timeInDay;
	}
	
	/**
	 * 获取日期信息，接受索引信息，如果为0则代表今天，1则代表昨天，2代表前天，以此类推。
	 * 当然，如果输入负数，就是往后的天数
	 * @param index
	 * @return
	 */
	public static String getDate(int index){
		
		Calendar calendar = Calendar.getInstance(); 
		
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - index);
		
		int date = calendar.get(Calendar.DATE);
		int month = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);
		
		return year+"-"+month+"-"+date;
	}

}