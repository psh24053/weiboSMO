package com.psh.query.util;

public class TotalPageNumber {
	
	private static int FIRST_TOTAL_PAGE_NUMBER = -1;
	
	private static TotalPageNumber instance = null;
	
	public static synchronized TotalPageNumber getInstance() {
		if ( null == instance) {
			instance = new TotalPageNumber();
		}
		return instance;
	}

	private TotalPageNumber() {
		
	}
	
	//获得0级搜索的总页数
	public int getFirstPageNumber(){
		
		return FIRST_TOTAL_PAGE_NUMBER;
		
	}
	
	public void setFirstPageNumber(int number){
		FIRST_TOTAL_PAGE_NUMBER = number;
	}

}
