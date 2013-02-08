package cn.panshihao.activationclient;


public class ProxyBean extends SuperModel {

	public int proxyid;
	public String ip;
	public int port;
	public long checktime;
	
	public int getProxyid() {
		return proxyid;
	}
	public void setProxyid(int proxyid) {
		this.proxyid = proxyid;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public long getChecktime() {
		return checktime;
	}
	public void setChecktime(long checktime) {
		this.checktime = checktime;
	}
	
}
