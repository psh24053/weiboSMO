package cn.panshihao.desktop.commons;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;


public abstract class SuperWindow {

	private Display display;
	private SuperWindow parent;
	private boolean root;
	public int marginWidthValue;
	public int marginHeightValue;
	public Rectangle superRectangle;
	public static Map<String, Object> cacheMap = new HashMap<String, Object>();
	public static CacheHandler cacheHandler = CacheHandler.getInstance();
	
	public SuperWindow(SuperWindow parent){
		this.parent = parent;
		this.display = parent.getDisplay();
		this.root = false;
		loadValue();
		
	}
	
	public SuperWindow(Display display){
		this.parent = null;
		this.display = display;
		if(this.display == null){
			this.display = Display.getDefault();
		}
		this.root = true;
		loadValue();
	}
	/**
	 * 根据shell，计算该shell如果出现在屏幕中央，那么需要的X坐标
	 * @param shell
	 * @return
	 */
	public int getCenterX(Shell shell){
		if(shell == null){
			return -1;
		}
		Rectangle shellBounds = shell.getBounds();
		int x = superRectangle.x + (superRectangle.width - shellBounds.width)>>1;
		return x;
	}
	/**
	 * 根据shell，计算该shell如果出现在屏幕中央，那么需要的Y坐标
	 * @param shell
	 * @return
	 */
	public int getCenterY(Shell shell){
		if(shell == null){
			return -1;
		}
		Rectangle shellBounds = shell.getBounds();
		int y = superRectangle.y + (superRectangle.height - shellBounds.height)>>1;
		return y;
	}
	
	/**
	 * 载入各种值
	 */
	private void loadValue(){
		superRectangle = display.getPrimaryMonitor().getBounds();
		
		
		marginWidthValue = superRectangle.width / 100;
		marginHeightValue = superRectangle.height / 100;
		
		
	}
	/**
	 * 打开alert对话框
	 * @param shell
	 * @param title
	 * @param msg
	 */
	public void alert(Shell shell, String title, String msg){
		if(shell == null || shell.isDisposed()){
			return;
		}
		MessageBox box = new MessageBox(shell);
		box.setText(title);
		box.setMessage(msg);
		box.open();
	}
	
	/**
	 * 打开confirm对话框
	 * @param shell
	 * @param title
	 * @param msg
	 */
	public boolean confirm(Shell shell, String title, String msg){
		if(shell == null || shell.isDisposed()){
			return false;
		}
		MessageBox box = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		box.setText(title);
		box.setMessage(msg);
		int result = box.open();
		return result == SWT.YES;
	}
	
	/**
	 * 初始化方法
	 */
	protected abstract void init();
	protected abstract Shell getShell();
	/**
	 * 显示窗口
	 */
	public void show(){
		init();
	}
	
	/**
	 * 获取Display
	 * @return
	 */
	public Display getDisplay() {
		return display;
	}
	/**
	 * 获取父窗口
	 * @return
	 */
	public SuperWindow getParent() {
		return parent;
	}
	/**
	 * 判断当前窗口是否是主窗口
	 * @return
	 */
	public boolean isRoot() {
		return root;
	}
	/**
	 * 判断当前窗口是否被关闭
	 * @return
	 */
	public boolean isDisposed(){
		return getShell().isDisposed();
	}
	
	public SuperWindow This(){
		return this;
	}
	
	public int getShellWidth(){
		// 减去左右边框区域
		return getShell().getBounds().width - getShell().getBorderWidth();
	}
	
	public int getShellHeight(){
		// 减去Shell的头部和底部区域
		return getShell().getBounds().height - getShell().getBorderWidth() - marginHeightValue * 3;
	}
	
	/**
	 * 根据传入的datetime组件，返回其时间的Long形态
	 * @param datetime
	 * @return
	 */
	public long ExtractDatetimeLong(DateTime datetime){
		if(datetime == null){
			return System.currentTimeMillis();
		}
		
		
		Calendar c = Calendar.getInstance(); 
		c.set(c.YEAR, datetime.getYear());
		c.set(c.MONTH, datetime.getMonth());
		c.set(c.DATE, datetime.getDay());
		
		
		return c.getTimeInMillis();
	}
	
	/**
	 * 结果回调接口
	 * @author Administrator
	 *
	 * @param <Result>
	 */
	public interface onResultListener<Result>{
		public void onResult(Result result);
	}
}
