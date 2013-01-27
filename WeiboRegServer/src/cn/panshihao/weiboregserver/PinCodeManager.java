package cn.panshihao.weiboregserver;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.WString;

/**
 * 验证码管理器
 * @author shihao
 *
 */
public class PinCodeManager {

	
	public interface FastVerCode extends Library
	{
		FastVerCode	INSTANCE = (FastVerCode) Native.loadLibrary("FastVerCode", FastVerCode.class);

		public String GetUserInfo(String UserName, String passWord);

		public String RecByte(byte[] imgByte, int len, String username, String password);

		public String RecYZM(String path, String UserName, String passWord);

		public void ReportError(WString UserName, WString passWord);

		public int Reglz(String userName, String passWord, String email, String qq, String dlId, String dlAccount);

	}
	
	
	
	
}
