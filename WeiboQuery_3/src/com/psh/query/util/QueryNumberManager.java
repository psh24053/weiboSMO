package com.psh.query.util;

import com.psh.query.model.GetFansUser;
import com.psh.query.model.GetFollowUser;

public class QueryNumberManager {
	
	private static QueryNumberManager instance = null;
	
	private int queryNowNumber = -1;
	
	private int queryMaxNumber = 100;
	
	public static synchronized QueryNumberManager getInstance() {
		if ( null == instance) {
			instance = new QueryNumberManager();
		}
		return instance;
	}

	private QueryNumberManager() {
		
		queryNowNumber = 2;
		
	}
	
	//进行一次关注和粉丝的遍历
	public void executeFollowAndFans(String uid,int queryTaskID){
		
		if(queryNowNumber <= queryMaxNumber){
			
			queryNowNumber++;
			GetFollowUser follow = new GetFollowUser();
			follow.getFollowUserByUid(uid, 1, queryTaskID);
			GetFansUser fans = new GetFansUser();
			fans.getFansUserByUid(uid, 1, queryTaskID);
			
		}
		
	}
	

}
