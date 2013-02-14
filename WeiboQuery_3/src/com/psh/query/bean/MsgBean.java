package com.psh.query.bean;

import com.psh.query.model.SuperModel;

public class MsgBean extends SuperModel {

	private String mid;
	private String type;
	private String time;
	private long uid;
	private long ouid;
	private String onck;
	
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public long getOuid() {
		return ouid;
	}
	public void setOuid(long ouid) {
		this.ouid = ouid;
	}
	public String getOnck() {
		return onck;
	}
	public void setOnck(String onck) {
		this.onck = onck;
	}
	
	
	
}
