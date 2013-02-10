package com.psh.query.bean;

import org.apache.poi.hssf.usermodel.HSSFRow;

public class ExcelBean {

	private String category;
	private String group;
	
	private String screen_name;
	private String city;
	private String province;
	private String gender;
	private String emotion;
	private String year;
	private String month;
	private String day;
	private String description;
	private String birthday_value;
	private String tags;
	
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
	private HSSFRow row;
	
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
	
	private void init(){
		// 分类
		category = row.getCell(0).getStringCellValue();
		// 分组
		group = row.getCell(1).getStringCellValue();
		// 昵称
		screen_name = row.getCell(2).getStringCellValue();
		// 省份
		province = row.getCell(3).getStringCellValue();
		// 城市
		city = row.getCell(4).getStringCellValue();
		// 性别
		gender = row.getCell(5).getStringCellValue().equals("男") ? "m":"f";
		// 情感状况
		emotion = row.getCell(6).getStringCellValue();
		// 生日公式
		birthday_value = row.getCell(7).getStringCellValue();
		// 简介
		description = row.getCell(8).getStringCellValue();
		// 标签
		tags = row.getCell(9).getStringCellValue();
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
	
	
	
	
}
