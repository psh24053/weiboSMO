package cn.panshihao.register.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.panshihao.desktop.commons.Log;
import cn.panshihao.desktop.commons.RandomString;
import cn.panshihao.desktop.commons.SQLConn;
import cn.panshihao.register.dao.wb_accountDAO;
import cn.panshihao.register.model.wb_accountModel;

public class RegisterService {

	public static final String EmailServerDomain = "uhomeu.com";
	
	
	/**
	 * 随机生成的注册数据
	 */
	private List<wb_accountModel> randomData;
	
	/**
	 * 等待激活的注册数据
	 */
	private List<wb_accountModel> waitActivationData;
	
	/**
	 * 失败的注册数据
	 */
	private List<wb_accountModel> faildData;
	
	/**
	 * 数据库中所有已存在的账号的邮箱
	 */
	private Map<String, String> key;
	
	/**
	 * 线程池服务
	 */
	private ExecutorService executorService;
	
	
	/**
	 * 构造方法
	 */
	public RegisterService(){
		
	}
	
	/**
	 * 初始化注册服务
	 * 1.从数据库读取所有已存在的账号的邮箱
	 */
	private void init(){
		
		wb_accountDAO dao = new wb_accountDAO();
		key = dao.selectEmail();
		
		
		waitActivationData = new ArrayList<wb_accountModel>();
		faildData = new ArrayList<wb_accountModel>();
		
		
		Log.log.debug("Load keyEmail size -> "+key.size());
		
	}
	
	/**
	 * 开始注册
	 * @param regcount 注册数量
	 * @param threadSize 线程数量
	 */
	public void startRegister(final int regcount, int threadSize){
		if(randomData == null){
			GenerateRandomData(regcount);
		}
		Log.log.debug("startRegister regCount("+regcount+") threadSize("+threadSize+")");
		
		if(executorService != null){
			executorService.shutdown();
		}
		executorService = Executors.newFixedThreadPool(threadSize);
		
		final ProxyService proxyService = new ProxyService();
		proxyService.loadProxyData();
		
		System.out.println("load proxy complete");
		for(int i = 0 ; i < threadSize ; i ++){
			executorService.execute(new RegisterRunnble(this, proxyService));
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				boolean bool = true;
				long time = System.currentTimeMillis();
				while(bool){
					long cur = System.currentTimeMillis();
					if(cur - time > ProxyService.refreshDelay){
						proxyService.loadProxyData();
						time = System.currentTimeMillis();
					}
					
				}
			}
		}).start(); 
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				boolean bool = true;
				while(bool){
					
					if(randomData.size() == 0 && (waitActivationData.size() + faildData.size()) == regcount){
						
						Log.log.debug("Register Complete!");
						Log.log.debug("Target Count "+regcount);
						Log.log.debug("waitActivation Count "+waitActivationData.size());
						Log.log.debug("faildData Count "+faildData.size());
						bool = false;
						executorService.shutdown();
					}
					
				}
			}
		}).start();
		
		
	}
	/**
	 * 停止注册
	 */
	public void stopRegister(){
		executorService.shutdown();
		Log.log.debug("stopRegister !");
	}
	/**
	 * 生成随机注册数据
	 * @param count
	 */
	public void GenerateRandomData(int count){
		init();
		Log.log.debug("GenerateRandomData count("+count+") start!");
		randomData = new ArrayList<wb_accountModel>();
		
		for(int i = 0 ; i < count ; i ++){
			wb_accountModel model = new wb_accountModel();
			model.setEmail(RandomString.getMD5ForUUIDIndex(12, 22)+"@"+EmailServerDomain);
			model.setNickname("pan"+RandomString.getMD5ForUUIDIndex(2, 20)+"shihao");
			model.setPassword(RandomString.getMD5ForUUIDIndex(12, 22));
			
			randomData.add(model);
			
		}
		
		
		Log.log.debug("GenerateRandomData count("+count+") complete!");
		
	}
	/**
	 * 从随机数据中获取一个wb_accountModel对象
	 * @return
	 */
	public synchronized wb_accountModel getAccountModelFromRandomData(){
		if(randomData == null){
			return null;
		}
		if(randomData.size() == 0){
			return null;
		}
		
		return randomData.remove(0);
	}

	
	
	
	
	public List<wb_accountModel> getRandomData() {
		return randomData;
	}

	public void setRandomData(List<wb_accountModel> randomData) {
		this.randomData = randomData;
	}

	public List<wb_accountModel> getWaitActivationData() {
		return waitActivationData;
	}

	public void setWaitActivationData(List<wb_accountModel> waitActivationData) {
		this.waitActivationData = waitActivationData;
	}

	public List<wb_accountModel> getFaildData() {
		return faildData;
	}

	public void setFaildData(List<wb_accountModel> faildData) {
		this.faildData = faildData;
	}

	public Map<String, String> getKey() {
		return key;
	}

	public void setKey(Map<String, String> key) {
		this.key = key;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}
	
	
	
	
	
	
	
	
	
}
