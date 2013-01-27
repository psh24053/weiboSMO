package cn.panshihao.weiboregserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.panshihao.weiboregserver.model.wb_accountModel;

/**
 * 缓存管理器
 * @author shihao
 *
 */
public class CacheManager {
	
	
	public static CacheManager instance = new CacheManager();
	
	/**
	 * 已存在邮箱缓存
	 */
	private Map<String, String> emailCache = new HashMap<String, String>();
	
	/**
	 * 等待激活账号数量
	 */
	private int waitCount = 0;
	/**
	 * 已激活账号数量
	 */
	private int activationCount = 0;
	/**
	 * 目标账号数量
	 */
	private int targetCount = 0;
	/**
	 * 注册失败数量
	 */
	private int faildCount = 0;
	/**
	 * 数据库中账号总数
	 */
	private int DBCount = 0;
	
	
	/**
	 * 减少等待激活账号数量
	 * @param value
	 */
	public synchronized void minusWaitCount(int value){
		this.waitCount -= value;
	}
	/**
	 * 增加等待激活账号数量
	 * @param value
	 */
	public synchronized void plusWaitCount(int value){
		this.waitCount += value;
	}
	/**
	 * 减少已激活账号数量
	 * @param value
	 */
	public synchronized void minusActivationCount(int value){
		this.activationCount -= value;
	}
	/**
	 * 增加已激活账号数量
	 * @param value
	 */
	public synchronized void plusActivationCount(int value){
		this.activationCount += value;
	}
	/**
	 * 减少失败账号数量
	 * @param value
	 */
	public synchronized void minusFaildCount(int value){
		this.faildCount -= value;
	}
	/**
	 * 增加失败账号数量
	 * @param value
	 */
	public synchronized void plusFaildCount(int value){
		this.faildCount += value;
	}
	
	public int getWaitCount() {
		return waitCount;
	}
	public synchronized void setWaitCount(int waitCount) {
		this.waitCount = waitCount;
	}
	public int getActivationCount() {
		return activationCount;
	}
	public synchronized void setActivationCount(int activationCount) {
		this.activationCount = activationCount;
	}
	public int getTargetCount() {
		return targetCount;
	}
	public synchronized void setTargetCount(int targetCount) {
		this.targetCount = targetCount;
	}
	public int getFaildCount() {
		return faildCount;
	}
	public synchronized void setFaildCount(int faildCount) {
		this.faildCount = faildCount;
	}
	public int getDBCount() {
		return DBCount;
	}
	public void setDBCount(int dBCount) {
		DBCount = dBCount;
	}


	public Map<String, String> getEmailCache() {
		return emailCache;
	}
	public void setEmailCache(Map<String, String> emailCache) {
		this.emailCache = emailCache;
	}
	/**
	 * 随机生成数据，会根据emailCache来自动去重
	 * @param count 数量
	 */
	public Set<wb_accountModel> randomGenerateData(int count){
		return null;
		
	}
	
}
