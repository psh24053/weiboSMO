package com.psh.query.model;

public class ThreadContraModel extends Thread{
	
	public String uid = "";
	public int queryTaskID = -1;
	
	public ThreadContraModel(String uid,int queryTaskID){
		
		this.uid = uid;
		this.queryTaskID = queryTaskID;
		
	}
	
	
	public void run(){
		
		//查找关注
		System.out.println("开始查找关注uid=" + uid);
		GetFollowUser followUser = new GetFollowUser();
		followUser.getFollowUserByUid(uid,1,queryTaskID);
		System.out.println("查找关注uid=" + uid + "结束");
		//查找粉丝
		GetFansUser fansUser = new GetFansUser();
		System.out.println("开始查找粉丝uid=" + uid);
		fansUser.getFansUserByUid(uid, 1, queryTaskID);
		System.out.println("查找粉丝uid=" + uid + "结束");
		
	}

}
