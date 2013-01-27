package cn.panshihao.register.model;

import cn.panshihao.desktop.commons.SuperModel;

public class wb_managerModel extends SuperModel {

	protected int mid;
	protected String username;
	protected String password;
	protected int grade;
	
	public int getMid() {
		return mid;
	}
	public void setMid(int mid) {
		this.mid = mid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getGrade() {
		return grade;
	}
	public void setGrade(int grade) {
		this.grade = grade;
	}
	
}
