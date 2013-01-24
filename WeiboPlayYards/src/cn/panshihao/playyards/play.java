package cn.panshihao.playyards;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.WString;

/**
 * 
 * @author cqz
 *         QQ:6199401
 *         email:cheqinzho@qq.com
 * 
 */
public class play
{
	public static final String	USERNAME	= "psh24053";
	public static final String	PASSWORD	= "2227976";
	public static final String	DLLPATH		= "FastVerCode";
	public static final String	IMGPATH		= "e:\\pincode.jpg";

	public interface FastVerCode extends Library
	{
		FastVerCode	INSTANCE	= (FastVerCode) Native.loadLibrary(DLLPATH, FastVerCode.class);

		public String GetUserInfo(String UserName, String passWord);

		public String RecByte(byte[] imgByte, int len, String username, String password);

		public String RecYZM(String path, String UserName, String passWord);

		public void ReportError(WString UserName, WString passWord);

		public int Reglz(String userName, String passWord, String email, String qq, String dlId, String dlAccount);

	}

	public static void main(String[] args) throws Exception
	{

		// System.out.println("GetUserInfo:" + FastVerCode.INSTANCE.GetUserInfo(USERNAME, PASSWORD));
		// System.out.println("Reglz:" + FastVerCode.INSTANCE.Reglz("6199401", "6199401", "6199401@qq.com", "6199401", "ww", "ww"));
		
		long startTime = System.currentTimeMillis();
		
		System.out.println("RecYZM:" + FastVerCode.INSTANCE.RecYZM(IMGPATH, USERNAME, PASSWORD));
		// getCodeByRecByte();
		System.out.println("用时: "+ (System.currentTimeMillis() - startTime)+" ms");
		
	}

	public static void getCodeByRecByte() throws Exception
	{
		System.out.println("正在获取验证码........");
		byte[] b = toByteArrayFromFile(IMGPATH);
		System.out.println("RecByte:" + FastVerCode.INSTANCE.RecByte(b, b.length, USERNAME, PASSWORD));

	}

	public static byte[] toByteArray(File imageFile) throws Exception
	{
		BufferedImage img = ImageIO.read(imageFile);
		ByteArrayOutputStream buf = new ByteArrayOutputStream((int) imageFile.length());
		try
		{
			ImageIO.write(img, "jpg", buf);
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return buf.toByteArray();
	}

	public static byte[] toByteArrayFromFile(String imageFile) throws Exception
	{
		InputStream is = null;

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try
		{
			is = new FileInputStream(imageFile);

			byte[] b = new byte[1024];

			int n;

			while ((n = is.read(b)) != -1)
			{

				out.write(b, 0, n);

			}// end while

		} catch (Exception e)
		{
			throw new Exception("System error,SendTimingMms.getBytesFromFile", e);
		} finally
		{

			if (is != null)
			{
				try
				{
					is.close();
				} catch (Exception e)
				{}// end try
			}// end if

		}// end try
		return out.toByteArray();
	}
}

