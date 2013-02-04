package cn.panshihao.activationclient;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.http.client.HttpClient;

public class UpdateService {

	private ExecutorService executorService;
	private ProxyService proxyService;
	private HttpClient httpClient;
	
	/**
	 * 识别未知状态
	 * 
	 * 判断status 为1的账号是已激活状态还是完成注册状态，并更新数据库
	 * 判断status 为4的账号是已激活状态还是激活失败状态，并更新数据库
	 * 
	 */
	public void startUpdate(){
		proxyService = new ProxyService();
		proxyService.loadProxyData();
		System.out.println("Load Proxy Data Complete! "+proxyService.getProxyData().size());
		wb_activationDAO dao = new wb_activationDAO();
		
		while(true){
			
			List<wb_activationModel> data = dao.selectActivationFor1_4();
			
			System.out.println("Load ActivationModel "+data.size());
			
			if(data != null && data.size() > 0){
				
				for(int i = 0 ; i < data.size() ; i ++){
					wb_activationModel model = data.get(i);
					System.out.println("run "+model);
					wb_proxyModel proxy = proxyService.getRandomProxyModel();
					
					
					
				}
				
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		
	}
	
}
