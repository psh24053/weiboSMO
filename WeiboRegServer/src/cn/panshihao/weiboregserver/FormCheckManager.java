package cn.panshihao.weiboregserver;

/**
 * 表单检查管理器
 * @author shihao
 *
 */
public class FormCheckManager {

	
	/**
	 * 判断nickname是否存在，存在返回true，不存在返回false
	 * @return
	 */
	public boolean checkNickNameExist(String nickname){
		return false;
	}
	/**
	 * 判断Email是否存在，存在返回true，不存在返回false
	 * @param email
	 * @return
	 */
	public boolean checkEmailExist(String email){
		return true;
	}
	
}
