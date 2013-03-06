package com.psh.query.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;

import com.psh.base.common.ErrorCode;
import com.psh.base.util.PshLogger;
import com.psh.query.bean.AccountBean;
import com.psh.query.model.GetFansUser;
import com.psh.query.model.GetFollowUser;
import com.psh.query.service.WeiboLoginService;

public class QueryNumberManager {
	
	private static QueryNumberManager instance = null;
	
//	private int queryNowNumber = -1;
	
//	private int queryMaxNumber = 10;
	private WeiboLoginService weiboLogin = null;
	
	public static synchronized QueryNumberManager getInstance() {
		if ( null == instance) {
			instance = new QueryNumberManager();
		}
		return instance;
	}

	private QueryNumberManager() {
		
		AccountBean account = new AccountBean();
		account.setEmail("psh24053@yahoo.cn");
		account.setPassword("caicai520");
		account.setUid(1661461070);
		weiboLogin = new WeiboLoginService(account);
		
		if(!weiboLogin.Login()){
			PshLogger.logger.error("login error");
			return;
		}
		
		
//		queryNowNumber = 2;
		
	}
	
	//进行一次关注和粉丝的遍历
	public void executeFollowAndFans(String uid){
		
		Set<String> folUidList = new HashSet<String>();
		Set<String> fansUidList = new HashSet<String>();
				
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		folUidList = weiboLogin.getFollowUserByUid_Login(Long.parseLong(uid), 1);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fansUidList = weiboLogin.getFansUserByUid_Login(Long.parseLong(uid), 1);
		
		folUidList.addAll(fansUidList);
				
		this.executQuery(folUidList);
		
	}
	
	public void executQuery(Set<String> uidSet){
		
		Set<String> folUidSet = new HashSet<String>();
		Set<String> fansUidSet = new HashSet<String>();
		
		Iterator<String> iterator=uidSet.iterator();

		while(iterator.hasNext()){
			
			String uid = iterator.next();
			folUidSet = weiboLogin.getFollowUserByUid_Login(Long.parseLong(uid), 1);
			fansUidSet = weiboLogin.getFansUserByUid_Login(Long.parseLong(uid), 1);
			folUidSet.addAll(fansUidSet);
			this.executQuery_2(folUidSet);
		}
		
		
	}
	
	
	public void executQuery_2(Set<String> uidSet){
		
		Set<String> folUidSet = new HashSet<String>();
		Set<String> fansUidSet = new HashSet<String>();
		
		Iterator<String> iterator=uidSet.iterator();
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(iterator.hasNext()){
			
			String uid = iterator.next();
			folUidSet = weiboLogin.getFollowUserByUid_Login(Long.parseLong(uid), 1);
			fansUidSet = weiboLogin.getFansUserByUid_Login(Long.parseLong(uid), 1);
			folUidSet.addAll(fansUidSet);
			this.executQuery_3(folUidSet);
		}
		
		
	}
	
	public void executQuery_3(Set<String> uidSet){
		
		Iterator<String> iterator=uidSet.iterator();

		while(iterator.hasNext()){
			
			String uid = iterator.next();
			weiboLogin.getFollowUserByUid_Login(Long.parseLong(uid), 1);
			weiboLogin.getFansUserByUid_Login(Long.parseLong(uid), 1);
		}
		
		
	}

}
