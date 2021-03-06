package com.psh.query.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.psh.base.util.PshLogger;
import com.psh.query.bean.ExcelBean;

public class ExcelService {

	public static final String UploadExcelDIR = "e:\\Uploads";
	
	public static Map<String, ExcelService> excelData = new HashMap<String, ExcelService>();
	
	private ExcelBean excelBean;
	private String idx;
	private int gid;
	private HSSFWorkbook hssfworkbook;
	
	
	private ExcelService(){}
	
	
	public static ExcelService createExcel(String idx){
		ExcelService excel = new ExcelService();
		excel.setIdx(idx);
		
		return excelData.put(idx, excel);
	}

	/**
	 * 解析对应idx的excel文件，并返回excelbean的List
	 * @return
	 */
	public List<ExcelBean> parseExcel(){
		File excelFile = new File(UploadExcelDIR, idx);
		FileInputStream in = null;
		try {
			in = new FileInputStream(excelFile);
			hssfworkbook = new HSSFWorkbook(in);
			
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage());
			return null;
		}
		
		if(hssfworkbook == null){
			PshLogger.logger.error("Parse Excel Error idx: "+idx);
			return null;
		}
		
		// 
		
		int sheetCount = hssfworkbook.getNumberOfSheets();
		
		if(sheetCount == 0){
			PshLogger.logger.error("Excel sheet is 0");
			return null;
		}
		HSSFSheet sheet = hssfworkbook.getSheetAt(0);
		
		List<ExcelBean> list = null;
		
		for(int j = 1 ; j <= sheet.getLastRowNum() ; j ++){
			if(list == null){
				list = new ArrayList<ExcelBean>();
			}
			HSSFRow row = sheet.getRow(j);
			if(row.getCell(0).getStringCellValue() == null){
				continue;
			}
			ExcelBean bean = new ExcelBean(row);
			list.add(bean);
		}
		
		try {
			in.close();
		} catch (IOException e) {
			PshLogger.logger.error(e.getMessage());
			return null;
		}
		return list;
	}
	
	public static void main(String[] args) throws IOException {
		FileInputStream in = new FileInputStream(new File("C:\\Users\\shihao\\Desktop\\微博账好资料-90后.xls"));
		
		HSSFWorkbook hssfworkbook = new HSSFWorkbook(in);
		
		int sheetCount = hssfworkbook.getNumberOfSheets();
		
		for(int i = 0 ; i < sheetCount ; i ++){
			HSSFSheet sheet = hssfworkbook.getSheetAt(i);
			
			for(int j = 0 ; j <= sheet.getLastRowNum() ; j ++){
				HSSFRow row = sheet.getRow(j);
				ExcelBean bean = new ExcelBean(row);
			}
			
			
		}
		
		
		System.out.println(sheetCount);
		
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
