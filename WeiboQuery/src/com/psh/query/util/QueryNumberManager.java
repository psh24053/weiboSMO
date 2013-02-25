package com.psh.query.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;

import com.psh.query.model.GetFansUser;
import com.psh.query.model.GetFollowUser;

public class QueryNumberManager {
	
	private static QueryNumberManager instance = null;
	
//	private int queryNowNumber = -1;
	
//	private int queryMaxNumber = 10;
	
	public static synchronized QueryNumberManager getInstance() {
		if ( null == instance) {
			instance = new QueryNumberManager();
		}
		return instance;
	}

	private QueryNumberManager() {
		
//		queryNowNumber = 2;
		
	}
	
	//进行一次关注和粉丝的遍历
	public void executeFollowAndFans(String uid){
		
		Set<String> folUidList = new HashSet<String>();
		Set<String> fansUidList = new HashSet<String>();
				
		GetFollowUser follow = new GetFollowUser();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		folUidList = follow.getFollowUserByUid(uid, 1);
		GetFansUser fans = new GetFansUser();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fansUidList = fans.getFansUserByUid(uid, 1);
		
		folUidList.addAll(fansUidList);
				
		this.executQuery(folUidList);
		
	}
	
	public void executQuery(Set<String> uidSet){
		
		Set<String> folUidSet = new HashSet<String>();
		Set<String> fansUidSet = new HashSet<String>();
		
		Iterator<String> iterator=uidSet.iterator();

		while(iterator.hasNext()){
			
			String uid = iterator.next();
			GetFollowUser follow = new GetFollowUser();
			folUidSet = follow.getFollowUserByUid(uid, 1);
			GetFansUser fans = new GetFansUser();
			fansUidSet = fans.getFansUserByUid(uid, 1);
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
			GetFollowUser follow = new GetFollowUser();
			folUidSet = follow.getFollowUserByUid(uid, 1);
			GetFansUser fans = new GetFansUser();
			fansUidSet = fans.getFansUserByUid(uid, 1);
			folUidSet.addAll(fansUidSet);
			this.executQuery_3(folUidSet);
		}
		
		
	}
	
	public void executQuery_3(Set<String> uidSet){
		
		Iterator<String> iterator=uidSet.iterator();

		while(iterator.hasNext()){
			
			String uid = iterator.next();
			GetFollowUser follow = new GetFollowUser();
			follow.getFollowUserByUid(uid, 1);
			GetFansUser fans = new GetFansUser();
			fans.getFansUserByUid(uid, 1);
		}
		
		
	}

}
