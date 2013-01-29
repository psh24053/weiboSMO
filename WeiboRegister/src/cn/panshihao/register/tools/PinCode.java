package cn.panshihao.register.tools;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.message.BasicNameValuePair;


import cn.panshihao.desktop.commons.Log;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.WString;

public class PinCode {

	public static final String YZM_USER = "psh24053";
	public static final String YZM_PASS = "2227976";
	
	public static File tempDir = null;
	static{
		tempDir = new File("/temp");
		tempDir.mkdirs();
	}
	
	private String pincode;
	private String anthor;
	private String sinaId;
	private String regtime;
	private int error_total = 0;
	
	public PinCode(String sinaId, String regtime){
		this.sinaId = sinaId;
		this.regtime = regtime;
	}

	/**
	 * 拉取验证码，成功返回true，失败返回false
	 * @return
	 */
	public boolean loadPinCode(){
		if(error_total == 3){
			return false;
		}
		
		// 拉取验证码
		URL url = null;
		try {
			url = new URL("http://www.weibo.com/signup/v5/pincode/pincode.php?lang=zh&sinaId="+sinaId+"&r="+regtime+"");
		} catch (MalformedURLException e) {
			Log.log.error(e.getMessage(), e);
			return false;
		}
		
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(new File(tempDir, sinaId+".jpg"));
		} catch (FileNotFoundException e) {
			Log.log.error(e.getMessage(), e);
			return false;
		}
		
		InputStream pincodeIn = null;
		try {
			pincodeIn = url.openStream();
		} catch (IOException e) {
			Log.log.error(e.getMessage(), e);
			return false;
		}
		byte[] readByte = new byte[1024];
		int readCount = -1;
		
		
		try {
			while((readCount = pincodeIn.read(readByte, 0, 1024)) != -1){
				out.write(readByte);
			}
			out.flush();
			out.close();
			pincodeIn.close();
		} catch (IOException e) {
			Log.log.error(e.getMessage(), e);
			return false;
		}
		String result = null;
		try {
			result = FastVerCode.INSTANCE.RecYZM("\\temp\\"+sinaId+".jpg", "psh24053", "2227976");
		} catch(UnsatisfiedLinkError e){
			Log.log.error(e.getMessage(), e);
			return false;
		}
		
		if(result == null){
			return false;
		}
		
		Log.log.debug("pincode result -> "+result);
		
		String[] strArr = result.replace("|", "").split("!");
		
		if(strArr == null || strArr.length != 2){
			error_total ++;
			return loadPinCode();
		}
		
		Log.log.debug("sinaId -> "+sinaId);
		
		pincode = strArr[0];
		anthor = strArr[1];
		if(pincode.length() != 4){
			ReportError(anthor);
			error_total ++;
			return loadPinCode(); 
		}
		return true;
	}
	/**
	 * 报告打码错误
	 * @param source
	 */
	public void ReportError(String source){
		WString user = new WString("psh24053");
		WString anthor = new WString(source);
		FastVerCode.INSTANCE.ReportError(user, anthor);
	}
	
	

	

	public String getPincode() {
		return pincode;
	}
	public void setPincode(String pincode) {
		this.pincode = pincode;
	}
	public String getAnthor() {
		return anthor;
	}
	public void setAnthor(String anthor) {
		this.anthor = anthor;
	}
	
	
	
	
	
	
}
