package cn.panshihao.weiboregserver;

import cn.panshihao.weiboregserver.model.StatusModel;

/**
 * 注册管理器
 * @author shihao
 *
 */
public class RegManager {

	public static RegManager instance = new RegManager();
	
	/**
	 * 注册新浪微博
	 * @param email
	 * @param password
	 * @param nickname
	 * @return
	 */
	public boolean registerWeibo(String email, String password, String nickname){
		return true;
	}
	/**
	 * 激活新浪微博
	 * @param email
	 * @param url
	 * @return
	 */
	public boolean activationWeibo(String email, String url){
		return true;
	}
	/**
	 * 获取当前注册状态
	 * @return
	 */
	public StatusModel getRegStatus(){
		return null;
	}
	
}
