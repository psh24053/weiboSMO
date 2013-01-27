package cn.panshihao.desktop.commons;

import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

public class RandomString {

	/**
	 * 获取MD5，生成一个UUID并且计算MD5以及截取
	 * @param begin
	 * @param end
	 * @return
	 */
	public static String getMD5ForUUIDIndex(int begin, int end){
		return getMD5ForIndex(UUID.randomUUID().toString(), begin, end);
	}
	
	/**
	 * 获取MD5，截取，传入开始位置和结束位置
	 * @param str
	 * @param begin
	 * @param end
	 * @return
	 */
	public static String getMD5ForIndex(String str, int begin, int end){
		return DigestUtils.md5Hex(str).substring(begin, end);
	}
	
	/**
	 * 获取MD5，传入字符串
	 * @param str
	 * @return
	 */
	public static String getMD5(String str){
		return DigestUtils.md5Hex(str);
	}
	/**
	 * 获取MD5，传入字节数组
	 * @param bytes
	 * @return
	 */
	public static String getMD5(byte[] bytes){
		return DigestUtils.md5Hex(bytes);
	}

	public static void main(String[] args) {
		System.out.println(RandomString.getMD5ForUUIDIndex(12, 22));
	}
	
	
}

