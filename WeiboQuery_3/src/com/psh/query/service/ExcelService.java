package com.psh.query.service;

import java.util.HashMap;
import java.util.Map;

import com.psh.query.bean.ExcelBean;

public class ExcelService {

	public static Map<String, ExcelService> excelData = new HashMap<String, ExcelService>();
	
	private ExcelBean excelBean;
	private String idx;
	private int gid;
	private ExcelService(){}
	
	public static ExcelService createExcel(String idx, int gid){
		return null;
	}

	public ExcelBean getExcelBean() {
		return excelBean;
	}

	public void setExcelBean(ExcelBean excelBean) {
		this.excelBean = excelBean;
	}

	public String getIdx() {
		return idx;
	}

	public void setIdx(String idx) {
		this.idx = idx;
	}

	public int getGid() {
		return gid;
	}

	public void setGid(int gid) {
		this.gid = gid;
	}
	
	
	
}
