package cn.panshihao.weiboregserver;

import org.apache.http.HttpHost;

/**
 * 代理服务器管理器
 * @author shihao
 *
 */
public class ProxyServerManager {
	
	/**
	 * 随机获取一个可用的代理服务器
	 * @return
	 */
	public HttpHost getProxyServer(){
		return null;
	}
	
	/**
	 * 搜索可用代理服务器
	 */
	public void SearchAvailableProxyServer(){
		
	}
	/**
	 * 验证代理服务器是否可用，可用返回true，不可用返回false
	 * @param ip
	 * @param port
	 * @return
	 */
	public boolean ValidationProxyServer(String ip, int port){
		return true;
	}
	/**
	 * 插入代理器服务器
	 * @param ip
	 * @param port
	 * @return
	 */
	public boolean insertProxyServer(String ip, int port){
		return true;
	}
	
}
