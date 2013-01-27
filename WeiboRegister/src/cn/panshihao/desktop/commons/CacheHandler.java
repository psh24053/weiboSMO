package cn.panshihao.desktop.commons;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;



public class CacheHandler {
	
	
	/**
	 * 重新实现cacheHandler，cacheHandler存在的目的是为了储存model对象
	 * 以及普通基本类型数据。图片和文件的缓存，将使用imageManager进行管理
	 * cachaManager所储存的每个缓存都不会超过1M，每个储存的model对象以文
	 * 件的形式储存在cacheDir目录下，文件名为key，文件内容为序列化之后的字
	 * 符串。而基本类型数据，则使用XML文件进行储存，对应的XML文件名为Config.BaseXMLName
	 * 在cacheHandler中能够对这两种缓存进行CRUD操作。
	 */
	
	
	private static CacheHandler cacheHandler = new CacheHandler();

	private CacheHandler() {}
	private Map<String, File> fileCache = new HashMap<String, File>();
	private File objectDir = null;
	private File filesDir = null;
	private Map<String, Object> baseMap = null;
	private String runtimePath = System.getProperty("user.dir"); 
	
	public static CacheHandler getInstance() {
		cacheHandler.objectDir = new File(cacheHandler.runtimePath,"/object/");
		cacheHandler.filesDir = new File(cacheHandler.runtimePath,"/files/");
		
		if(!cacheHandler.objectDir.exists()){
			cacheHandler.objectDir.mkdirs();
		}
		if(!cacheHandler.filesDir.exists()){
			cacheHandler.filesDir.mkdirs();
		}
		
		//如果BaseMap存在，则读取，否则为其初始化
		if(cacheHandler.hasObjectCache("BaseMap")){
			try {
				cacheHandler.baseMap = (Map<String, Object>) cacheHandler.readObjectCache("BaseMap");
			} catch (StreamCorruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			cacheHandler.baseMap = new HashMap<String, Object>();
		}
		
		
		//如果文件夹不存在则创建
		if(!cacheHandler.objectDir.exists()){
			cacheHandler.objectDir.mkdir();
		}
		if(!cacheHandler.filesDir.exists()){
			cacheHandler.filesDir.mkdir();
		}
		return cacheHandler;
	}

	/**
	 * 获取缓存占用总大小，单位是字节
	 * @return
	 */
	public long getCacheTotalSize(){
		long cacheTotal = 0;
		//得到对象缓存文件夹的文件数组
		File[] cacheFiles = objectDir.listFiles();
		for(File file : cacheFiles){
			cacheTotal += file.length();
		}
		//得到文件缓存ROM文件夹的文件数组
		File[] fileFiles = filesDir.listFiles();
		for(File file : fileFiles){
			cacheTotal += file.length();
		}
		
		return cacheTotal;
	}
	

	/**
	 * 清理所有对象缓存
	 */
	public void ClearObjectCache(){
		
		//得到文件数组
		File[] files = objectDir.listFiles();
		for(int i = 0 ; i < files.length ; i ++){
			File itemFile = files[i];
			//删除文件
			if(itemFile.exists() && itemFile.isFile() && itemFile.canWrite()){
				itemFile.delete();
			}
		}
	}
	/**
	 * 清理所有对象缓存，传入例外对象名数组
	 * @param filenames
	 */
	public void ClearObjectCacheExcept(String[] filenames){
		
		File[] files = objectDir.listFiles();
		for(int i = 0 ; i < files.length ; i ++){
			File itemFile = files[i];
			// 例外判断
			boolean clear = true;
			
			for(String name : filenames){
				if(itemFile.getName().equals(name)){
					clear = false;
					break;
				}
			}
			
			
			//删除文件
			if(clear && itemFile.exists() && itemFile.isFile() && itemFile.canWrite()){
				itemFile.delete();
			}
		}
	}

	/**
	 * 清理所有文件缓存，内存和SD卡一块儿清
	 */
	public void ClearFilesCache(){
		
		//得到文件数组，得到内存文件缓存目录
		File[] files = filesDir.listFiles();
		for(int i = 0 ; i < files.length ; i ++){
			File itemFile = files[i];
			//删除文件
			if(itemFile.exists() && itemFile.isFile() && itemFile.canWrite()){
				itemFile.delete();
			}
		}
		
		
	}
	
	
	/**
	 * 清理所有文件缓存，内存和SD卡一块儿清
	 * 传入例外文件名数组
	 */
	public void ClearFilesCacheExcept(String[] filenames){
		
		//得到文件数组，得到内存文件缓存目录
		File[] files = filesDir.listFiles();
		for(int i = 0 ; i < files.length ; i ++){
			File itemFile = files[i];
			// 例外判断
			boolean clear = true;
			
			for(String name : filenames){
				if(itemFile.getName().equals(name)){
					clear = false;
					break;
				}
			}
			
			
			//删除文件
			if(clear && itemFile.exists() && itemFile.isFile() && itemFile.canWrite()){
				itemFile.delete();
			}
		}
		
	}
	/**
	 * 清除所有BaseMap缓存
	 */
	public void ClearBaseCache(){
		baseMap.clear();
		
		try {
			SaveObjectCache("BaseMap", (Serializable) baseMap);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 判断指定key是否存在,BaseMap
	 * @param key
	 * @return
	 */
	public boolean hasBaseCache(String key){
		
		// 如果key在baseMap中存在，则返回true，否则false
		return baseMap.containsKey(key);
	}
	/**
	 * 判断文件缓存中是否存在key
	 * @param key
	 * @return
	 */
	public boolean hasFilesCache(String key){
		// 得到文件数组
		File[] files = filesDir.listFiles();
		
		//遍历文件数组
		for(int i = 0 ; i < files.length ; i ++){
			File itemFile = files[i];
			//判断文件是否存在，文件是否是一个文件，文件是否能读，是否能写，并且文件的名字与key相同
			//则代表对象缓存中存在这个key
			if(itemFile.exists() && itemFile.isFile() && itemFile.canRead() && itemFile.canWrite() && itemFile.getName().equals(key)){
				//将这个itemFile加入到内存中，为近期使用做准备
				Log.log.debug("cacheHandler.hasFilesCache -> "+key+" -> true");
				fileCache.put(key, itemFile);
				return true;
			}
		}
		
		
		
		return false;
	}
	/**
	 * 判断对象缓存中是否存在key
	 * @param key
	 * @return
	 */
	public boolean hasObjectCache(String key){

		Log.log.debug("cacheHandler.hasObjectCache -> "+key);
		
		//得到文件数组
		File[] files = objectDir.listFiles();
		//遍历文件数组
		for(int i = 0 ; i < files.length ; i ++){
			File itemFile = files[i];
			//判断文件是否存在，文件是否是一个文件，文件是否能读，是否能写，并且文件的名字与key相同
			//则代表对象缓存中存在这个key
			if(itemFile.exists() && itemFile.isFile() && itemFile.canRead() && itemFile.canWrite() && itemFile.getName().equals(key)){
				//将这个itemFile加入到内存中，为近期使用做准备
				Log.log.debug("cacheHandler.hasObjectCache -> "+key+" -> true");
				fileCache.put(key, itemFile);
				return true;
			}
		}
		Log.log.debug("cacheHandler.hasObjectCache -> "+key+" -> false");
		return false;
	}
	
	
	/**
	 * 保存文件缓存，根据key
	 * @param key
	 * @param is
	 * @return
	 * @throws IOException 
	 */
	public synchronized boolean SaveFilesCache(String key, InputStream is) throws IOException{
		
					
		//创建输出文件对象
		File outFile = new File(filesDir, key);

		Log.log.debug("cacheHandler.SaveFilesCache -> "+key);
		// 创建缓冲输入流
		BufferedInputStream bis = new BufferedInputStream(is);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		byte[] readByte = new byte[1024];
		int readCount = -1;
		// 读取输入流
		while((readCount = bis.read(readByte, 0, 1024)) != -1){
			baos.write(readByte, 0, readCount);
		}
		
		bis.close();
		// 创建文件输出流
		FileOutputStream fos = new FileOutputStream(outFile);
		fos.write(baos.toByteArray());
		fos.flush();
		fos.close();
		baos.close();
		
		// 将这个文件加入到FileCache中
		Log.log.debug("cacheHandler.SaveFilesCache -> "+key+" -> true");
		fileCache.put(key, outFile);
		return true;
					
			
			
			
			
	
	}
	/**
	 * 保存一个缓存，传入一个序列化对象
	 * @param key
	 * @param object
	 * @throws IOException 
	 */
	public synchronized boolean SaveObjectCache(String key, Serializable object) throws IOException{

		
		//创建输出文件对象
		File outFile = new File(objectDir, key);

		Log.log.debug("cacheHandler.SaveObjectCache -> "+key+" -> "+object.toString());
		// 创建文件输出流
		FileOutputStream fos = new FileOutputStream(outFile);
		// 创建对象输出流，传入文件输出流
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		// 写出object，并关闭流
		oos.writeObject(object);
		oos.flush();
		oos.close();
		fos.close();
		
		// 将这个文件加入到FileCache中
		Log.log.debug("cacheHandler.SaveObjectCache -> "+key+" -> "+object.toString()+" -> true");
		fileCache.put(key, outFile);
		return true;
		
		
	}
	/**
	 * 保存BaseMap到硬盘上
	 * @return
	 */
	public boolean CommitBaseCache(){
		try {
			SaveObjectCache("BaseMap", (Serializable)baseMap);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	/**
	 * 向BaseMap中添加String，需要调用CommitBaseCache才能写入到硬盘
	 * @param key
	 * @param value
	 */
	public CacheHandler putString(String key, String value){
		baseMap.put(key, value);
		return this;
	}
	/**
	 * 向BaseMap中添加Int，需要调用CommitBaseCache才能写入到硬盘
	 * @param key
	 * @param value
	 */
	public CacheHandler putInt(String key, int value){
		baseMap.put(key, value);
		return this;
	}
	/**
	 * 向BaseMap中添加float，需要调用CommitBaseCache才能写入到硬盘
	 * @param key
	 * @param value
	 */
	public CacheHandler putFloat(String key, float value){
		baseMap.put(key, value);
		return this;
	}
	/**
	 * 向BaseMap中添加double，需要调用CommitBaseCache才能写入到硬盘
	 * @param key
	 * @param value
	 */
	public CacheHandler putDouble(String key, double value){
		baseMap.put(key, value);
		return this;
	}
	/**
	 * 向BaseMap中添加boolean，需要调用CommitBaseCache才能写入到硬盘
	 * @param key
	 * @param value
	 */
	public CacheHandler putBoolean(String key, boolean value){
		baseMap.put(key, value);
		return this;
	}
	/**
	 * 从BaseMap中获取String，如果不存在则返回defaultValue
	 * @param key
	 * @return
	 */
	public String getBaseString(String key, String defaultValue){
		if(baseMap.containsKey(key)){
			return (String) baseMap.get(key);
		}
		return defaultValue;
	}
	/**
	 * 从BaseMap中获取int，如果不存在则返回defaultValue
	 * @param key
	 * @return
	 */
	public int getBaseInt(String key, int defaultValue){
		if(baseMap.containsKey(key)){
			return (int) baseMap.get(key);
		}
		return defaultValue;
	}
	/**
	 * 从BaseMap中获取float，如果不存在则返回defaultValue
	 * @param key
	 * @return
	 */
	public float getBaseFloat(String key, float defaultValue){
		if(baseMap.containsKey(key)){
			return (float) baseMap.get(key);
		}
		return defaultValue;
	}
	/**
	 * 从BaseMap中获取double，如果不存在则返回defaultValue
	 * @param key
	 * @return
	 */
	public double getBaseDouble(String key, double defaultValue){
		if(baseMap.containsKey(key)){
			return (double) baseMap.get(key);
		}
		return defaultValue;
	}
	/**
	 * 从BaseMap中获取boolean，如果不存在则返回defaultValue
	 * @param key
	 * @return
	 */
	public boolean getBaseBoolean(String key, boolean defaultValue){
		if(baseMap.containsKey(key)){
			return (boolean) baseMap.get(key);
		}
		return defaultValue;
	}
	/**
	 * 读取一个文件缓存，根据key
	 * @param key
	 * @return
	 * @throws FileNotFoundException 
	 */
	public InputStream readFilesCache(String key) throws FileNotFoundException{
		File readFile = null;
		//如果fileCache中存在这个key，则使用这个file
		//否则将从cacheDir中遍历读取
		
		Log.log.debug("cacheHandler.readFilesCache -> "+key);
		if(fileCache.containsKey(key)){
			readFile = fileCache.get(key);
		}else{
			//直接调用hasFilesCache方法来判断文件是否存在，如果不存在则返回false
			if(!hasFilesCache(key)){
				Log.log.debug("cacheHandler.readFilesCache -> "+key+" -> null");
				return null;
			}else{
				readFile = fileCache.get(key);
			}
			
		}
		
		// 创建文件输入流
		FileInputStream fis = new FileInputStream(readFile);
		Log.log.debug("cacheHandler.readFilesCache -> "+key+" -> true");
		return fis;
	}
	/**
	 * 读取一个文件缓存，根据key
	 * @param key
	 * @return
	 * @throws FileNotFoundException 
	 */
	public File FileFilesCache(String key) throws FileNotFoundException{
		File readFile = null;
		//如果fileCache中存在这个key，则使用这个file
		//否则将从cacheDir中遍历读取
		Log.log.debug("cacheHandler.readFilesCache -> "+key);
		if(fileCache.containsKey(key)){
			readFile = fileCache.get(key);
		}else{
			//直接调用hasFilesCache方法来判断文件是否存在，如果不存在则返回false
			if(!hasFilesCache(key)){
				Log.log.debug("cacheHandler.readFilesCache -> "+key+" -> null");
				return null;
			}else{
				readFile = fileCache.get(key);
			}
			
		}
		
		Log.log.debug("cacheHandler.readFilesCache -> "+key+" -> true");
		return readFile;
	}

	/**
	 * 根据key，读取一个对象缓存，如果没有读到，则返回null
	 * @param key
	 * @return
	 * @throws IOException 
	 * @throws StreamCorruptedException 
	 * @throws ClassNotFoundException 
	 */
	public Object readObjectCache(String key) throws StreamCorruptedException, IOException, ClassNotFoundException{
		File readFile = null;
		//如果fileCache中存在这个key，则使用这个file
		//否则将从cacheDir中遍历读取
		Log.log.debug("cacheHandler.readObjectCache -> "+key);
		if(fileCache.containsKey(key)){
			readFile = fileCache.get(key);
		}else{
			//直接调用hasObjectCache方法来判断文件是否存在，如果不存在则返回false
			if(!hasObjectCache(key)){
				Log.log.debug("cacheHandler.readObject -> "+key+" -> null");
				return null;
			}else{
				readFile = fileCache.get(key);
			}
			
		}
		
		// 创建文件输入流
		FileInputStream fis = new FileInputStream(readFile);
		// 创建对象输入流
		ObjectInputStream ois = new ObjectInputStream(fis);
		// 返回反序列化后的对象，并关闭IO流
		Object o = ois.readObject();
		ois.close();
		fis.close();
		
		Log.log.debug("cacheHandler.readObjectCache -> "+key+" -> "+o.toString());
		return o;
		
		
	}
	/**
	 * 根据key，删除一个Base缓存
	 * @param key
	 * @return
	 */
	public CacheHandler removeBaseCache(String key){
		baseMap.remove(key);
		return this;
	}
	/**
	 * 根据key，删除一个对象缓存
	 * @param key
	 * @return
	 */
	public boolean removeObjectCache(String key){

		if(hasObjectCache(key)){
			File removeFile = fileCache.get(key);
			Log.log.debug("cacheHandler.removeObjectCache -> "+key);
			// 如果removeFile存在并且是个文件并且可以写，则删除它，并返回删除结果
			if(removeFile.exists() && removeFile.isFile() && removeFile.canWrite()){
				return removeFile.delete();
			}
		}else{
			return false;
		}
		
		return false;
	}

	/**
	 * 删除一个文件缓存，根据key
	 * @param key
	 * @return
	 */
	public boolean removeFilesCache(String key){
		if(hasFilesCache(key)){
			File removeFile = fileCache.get(key);
			Log.log.debug("cacheHandler.removeFilesCache -> "+key);
			// 如果removeFile存在并且是个文件并且可以写，则删除它，并返回删除结果
			if(removeFile.exists() && removeFile.isFile() && removeFile.canWrite()){
				return removeFile.delete();
			}
		}else{
			return false;
		}
		
		return false;
	}

	/**
	 * 根据Key，来获取对象缓存的File对象
	 * @param key
	 * @return
	 */
	public File getObjectCacheFile(String key){
		if(hasObjectCache(key)){
			return fileCache.get(key);
		}
		
		return null;
	}
	/**
	 * 根据Key，来获取文件缓存的File对象
	 * @param key
	 * @return
	 */
	public File getFilesCacheFile(String key){
		if(hasFilesCache(key)){
			return fileCache.get(key);
		}
		return null;
	}
	

	/**
	 * 获取文件的md5
	 * @param by
	 * @return
	 */
	public String getFileMD5(byte[] by) {
		if (by == null) {
			return null;
		}
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.update(by);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16);
	}


	public File getCacheDir() {
		return objectDir;
	}

	public void setCacheDir(File cacheDir) {
		this.objectDir = cacheDir;
	}

	public File getFilesDir() {
		return filesDir;
	}

	public void setFilesDir(File filesDir) {
		this.filesDir = filesDir;
	}

	
	
	
	

}
