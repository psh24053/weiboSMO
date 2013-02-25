package com.psh.query.util;

import java.util.ArrayList;
import java.util.List;

import com.psh.base.util.PshLogger;
import com.psh.query.bean.ProxyBean;
import com.psh.query.model.ProxyModel;

public class ProxyManager {
	
	private static ProxyManager instance = null;
	
	private List<ProxyBean> proxyList = null;
	
	private static int proxyListIndex = 0;
	
	private static int proxyCount = 0;
	
	public static synchronized ProxyManager getInstance() {
		if ( null == instance) {
			instance = new ProxyManager();
		}
		return instance;
	}

	private ProxyManager() {
		
		proxyList = new ArrayList<ProxyBean>();
		initProxy();

	}
	
	private synchronized void initProxy(){
		
		System.out.println("加载代理IP");
		ProxyModel proxyModel = new ProxyModel();
		proxyList = proxyModel.getAllProxy();
		proxyCount = proxyModel.getCount();
		if(proxyList == null){
			
			PshLogger.logger.error("init proxy fail");
			
		}
		
	}
	
	//得到一个代理IP
	public ProxyBean getOneProxy(){
		ProxyBean pb = new ProxyBean();
		if(proxyListIndex > proxyCount - 2){
			proxyListIndex = 0;
		}else{
			
			proxyListIndex++;
		}
		pb = proxyList.get(proxyListIndex);
		return pb;
		
	}

}
