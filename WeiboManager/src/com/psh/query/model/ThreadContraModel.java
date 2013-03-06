package com.psh.query.model;

import com.psh.query.util.QueryNumberManager;

public class ThreadContraModel extends Thread{
	
	public String uid = "";
//	public int queryTaskID = -1;
	
	public ThreadContraModel(String uid){
		
		this.uid = uid;
//		this.queryTaskID = queryTaskID;
		
	}
	
	
	public void run(){
		
		//查找关注
//		System.out.println("开始查找关注uid=" + uid);
//		GetFollowUser followUser = new GetFollowUser();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		QueryNumberManager.getInstance().executeFollowAndFans(uid);
//		followUser.getFollowUserByUid(uid,1,queryTaskID);
//		System.out.println("查找关注uid=" + uid + "结束");
//		//查找粉丝
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		GetFansUser fansUser = new GetFansUser();
//		System.out.println("开始查找粉丝uid=" + uid);
//		fansUser.getFansUserByUid(uid, 1, queryTaskID);
//		System.out.println("查找粉丝uid=" + uid + "结束");
		
	}

}
