package cn.panshihao.weiboregserver;

import cn.panshihao.weiboregserver.model.wb_accountModel;

/**
 * 注册逻辑线程
 * @author shihao
 *
 */
public class RegisterThread extends Thread {

	
	private wb_accountModel model;
	
	public RegisterThread(wb_accountModel model){
		this.model = model;
	}
	
	@Override
	public void run() {
		// 启动注册
		
		RegManager regmanager = new RegManager();
		
		regmanager.registerWeibo(model.getEmail(), model.getPassword(), model.getNickname());
		
	}
}
