package com.psh.query.bean;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFRow;

import com.psh.query.model.CityModel;
import com.psh.query.model.ProvModel;
import com.psh.query.model.SuperModel;

public class ExcelBean extends SuperModel {

	public String category;
	public String group;
	
	public String screen_name;
	public String city;
	public String province;
	public String gender;
	public String emotion;
	public String year;
	public String month;
	public String day;
	public String description;
	public String birthday_value;
	public String tags;
	
	public String getBirthday_value() {
		return birthday_value;
	}

	public void setBirthday_value(String birthday_value) {
		this.birthday_value = birthday_value;
	}

	public HSSFRow getRow() {
		return row;
	}

	public void setRow(HSSFRow row) {
		this.row = row;
	}
	public HSSFRow row;
	
	/**
	 * 传入row，用以提取数据
	 * @param row
	 */
	public ExcelBean(HSSFRow row){
		this.row = row;
		
		if(this.row != null){
			init();
		}
		
	}
	
	public void init(){
		// 分类
		category = row.getCell(0).getStringCellValue();
		// 分组
		group = row.getCell(1).getStringCellValue();
		// 昵称
		screen_name = row.getCell(2).getStringCellValue();
		// 省份
		province = String.valueOf((int)row.getCell(3).getNumericCellValue());
		int pid = Integer.parseInt(province);
		ProvModel provmodel = new ProvModel();
		province = provmodel.getProvNameByID(pid);
		
		// 城市
		city = String.valueOf((int)row.getCell(4).getNumericCellValue());
		int cid = Integer.parseInt(city);
		CityModel citymodel = new CityModel();
		city = citymodel.getProvNameByID(cid, pid);
		
		// 性别
		gender = row.getCell(5).getStringCellValue();
		// 情感状况
		emotion = row.getCell(6).getStringCellValue();
		// 生日公式
		birthday_value = row.getCell(7).getStringCellValue();
		// 简介
		description = row.getCell(8).getStringCellValue();
		// 标签
		tags = row.getCell(9).getStringCellValue();
		
		birthday_value = randomDate(birthday_value);
		
		
	}
	/**
	 * 生成范围随机数，从m~n的随机数
	 * @param m
	 * @param n
	 * @return
	 */
	public static int randomInt(int m, int n){
		int r = (int)(m+(n+1-m)*Math.random());
		
		return r;
	}
	/**
	 * 封装format
	 * @param rStr
	 * @return
	 */
	public static String randomDate(String rStr){
		return randomDate(rStr, "yyyy-MM-dd");
	}
	/**
	 * 随机生成时间
	 * @param rStr X~Y,X+,X-,X=
	 * @return
	 */
	public static String randomDate(String rStr, String format){
		
		String result = null;
		int start = 0;
		int end = 0;
		if(rStr.contains("~")){
			String[] split = rStr.split("~");
			if(split.length != 2){
				return null;
			}
			
			start = Integer.parseInt(split[0]);
			end = Integer.parseInt(split[1]);
			
			
		}else if(rStr.contains("=")){
			start = Integer.parseInt(rStr.substring(0, rStr.indexOf("=")));
			end = start;
			
			
		}else if(rStr.contains("+")){
			start = Integer.parseInt(rStr.substring(0, rStr.indexOf("+")));
			end = 100;
			
		}else if(rStr.contains("-")){
			start = 1;
			end = Integer.parseInt(rStr.substring(0, rStr.indexOf("-")));
		}
		
		
		Calendar startCalendar = Calendar.getInstance();
		// 随机年份
		startCalendar.set(Calendar.YEAR, startCalendar.get(Calendar.YEAR) - randomInt(start, end));
		// 随机月份
		startCalendar.set(Calendar.MONTH, startCalendar.get(Calendar.MONTH) - randomInt(0, 12));
		// 随机日
		startCalendar.set(Calendar.DATE, startCalendar.get(Calendar.DATE) - randomInt(0, startCalendar.get(Calendar.DATE)));
		// 随机小时
		startCalendar.set(Calendar.HOUR, startCalendar.get(Calendar.HOUR) - randomInt(0, startCalendar.get(Calendar.HOUR)));
		// 随机分钟
		startCalendar.set(Calendar.MINUTE, startCalendar.get(Calendar.MINUTE) - randomInt(0, startCalendar.get(Calendar.MINUTE)));
		// 随机秒
		startCalendar.set(Calendar.SECOND, startCalendar.get(Calendar.SECOND) - randomInt(0, startCalendar.get(Calendar.SECOND)));
												
		
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		result = sdf.format(startCalendar.getTime());
		
		return result;
	}
	
	

	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getScreen_name() {
		return screen_name;
	}
	public void setScreen_name(String screen_name) {
		this.screen_name = screen_name;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getEmotion() {
		return emotion;
	}
	public void setEmotion(String emotion) {
		this.emotion = emotion;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
	
	
	
	
}
