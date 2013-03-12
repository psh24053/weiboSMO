package cn.panshihao.register.window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import cn.panshihao.desktop.commons.SuperWindow;

public class MainWindow extends SuperWindow {

	private Shell main_shell;
	private String main_title = "注册激活系统";
	
	/*
	 * 按钮区域
	 */
	private Composite main_buttons = null;
	private Button main_buttons_start = null;
	private Button main_buttons_end = null;
	
	/*
	 * 选项卡区域
	 */
	private TabFolder main_tabfolder = null;
	private TabItem main_tabfolder_register = null;
	private TabItem main_tabfolder_activation = null;
	private TabItem main_tabfolder_Humanbehavior = null;
	private TabItem main_tabfolder_DayTasks = null;
	private TabItem main_tabfolder_proxy = null;
	
	/*
	 * 注册区域的内容
	 */
	private Composite main_content_register = null;
	
	/*
	 * 激活区域的内容
	 */
	private Composite main_content_activation = null;
	/*
	 * 人类行为区域的内容
	 */
	private Composite main_content_humanbehavior = null;
	/*
	 * 每日任务区域的内容
	 */
	private Composite main_content_daytasks = null;
	/*
	 * 代理状态区域的内容
	 */
	private Composite main_content_proxy = null;
	
	
	public MainWindow(Display display) {
		super(display);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		initBase();
		
		initTabFolder();
		
		ShellOpen();
		ShellListenerClose();
	}
	/**
	 * 初始化选项卡
	 */
	private void initTabFolder(){
		
		main_tabfolder = new TabFolder(main_shell, SWT.NONE);
		
		int tabWidth = main_shell.getBounds().width - (int)(marginWidthValue * 2.5);
		int tabHeight = marginHeightValue * 83;
		
		main_tabfolder.setBounds(marginWidthValue, marginHeightValue , tabWidth, tabHeight);
		
		
		main_tabfolder_register = new TabItem(main_tabfolder, SWT.NONE);
		main_tabfolder_register.setText("注册账号(未启动)");
		initRegisterContent();
		
		main_tabfolder_activation = new TabItem(main_tabfolder, SWT.NONE);
		main_tabfolder_activation.setText("激活账号(未启动)");
		initActivationContent();
		
		main_tabfolder_Humanbehavior = new TabItem(main_tabfolder, SWT.NONE);
		main_tabfolder_Humanbehavior.setText("人类行为(未启动)");
		initHumanBehavioerContent();
		
		main_tabfolder_DayTasks = new TabItem(main_tabfolder, SWT.NONE);
		main_tabfolder_DayTasks.setText("每日任务(未启动)");
		initDayTasksContent();
		
		main_tabfolder_proxy = new TabItem(main_tabfolder, SWT.NONE);
		main_tabfolder_proxy.setText("代理状态(可用：0)");
		initProxyContent();
		
	}
	
	/**
	 * 初始化注册选项卡的内容
	 */
	private Composite register_buttons = null;
	private Button register_buttons_start = null;
	private Button register_buttons_end = null;
	private Button register_buttons_export = null;
	private Group register_buttons_systeminfo = null;
	
	
	private void initRegisterContent(){
		main_content_register = new Composite(main_tabfolder, SWT.NONE);
		main_tabfolder_register.setControl(main_content_register);
		
		register_buttons = new Composite(main_content_register, SWT.NONE);
		register_buttons.setBounds(marginWidthValue, marginHeightValue, main_content_register.getBounds().width - marginWidthValue * 2, marginHeightValue * 5);
//		register_buttons.setBackground(Color.win32_new(getDisplay(), 0xff000000));
		
		register_buttons_start = new Button(register_buttons, SWT.NONE);
		register_buttons_start.setText("启动注册服务");
		register_buttons_start.setBounds(marginWidthValue, marginHeightValue, marginWidthValue * 8, marginHeightValue * 3);
		
		register_buttons_end = new Button(register_buttons, SWT.NONE);
		register_buttons_end.setText("停止注册服务");
		register_buttons_end.setBounds(register_buttons_start.getBounds().width + register_buttons_start.getBounds().x + marginWidthValue * 3, register_buttons_start.getBounds().y, marginWidthValue * 8, marginHeightValue * 3);
		
		register_buttons_export = new Button(register_buttons, SWT.NONE);
		register_buttons_export.setText("导出已注册账号");
		register_buttons_export.setBounds(register_buttons_end.getBounds().width + register_buttons_end.getBounds().x + marginWidthValue * 3, register_buttons_end.getBounds().y, marginWidthValue * 8, marginHeightValue * 3);
		
		register_buttons_systeminfo = new Group(main_content_register, SWT.NONE);
		register_buttons_systeminfo.setText("系统信息");
		register_buttons_systeminfo.setBounds(marginWidthValue, register_buttons.getBounds().height + register_buttons.getBounds().y + marginHeightValue, main_content_register.getBounds().width - marginWidthValue * 2, marginHeightValue * 15);
		
		
		
	}
	/**
	 * 初始化激活选项卡的内容
	 */
	private void initActivationContent(){
		
	}
	/**
	 * 初始化人类行为选项卡的内容
	 */
	private void initHumanBehavioerContent(){
		
	}
	/**
	 * 初始化每日任务选项卡的内容
	 */
	private void initDayTasksContent(){
		
	}
	/**
	 * 初始化代理选项卡的内容
	 */
	private void initProxyContent(){
		
	}
	
	/**
	 * 初始化界面基本数据
	 */
	private void initBase(){
		main_shell = new Shell(getDisplay(), SWT.CLOSE | SWT.MIN);
		
		main_shell.setText(main_title);
		main_shell.setSize(marginWidthValue * 90, marginHeightValue * 90);
		main_shell.setLocation(getCenterX(main_shell), getCenterY(main_shell));
		
		
	}
	
	
	@Override
	protected Shell getShell() {
		// TODO Auto-generated method stub
		return this.main_shell;
	}

}
