package cn.panshihao.register.window;

import org.eclipse.swt.widgets.Display;

import cn.panshihao.register.tools.RegisterService;

public class main {

	public static void main(String[] args) {
//		RegisterService service = new RegisterService();
//		service.startRegister(1, 1);
		
		
		MainWindow mainWindow = new MainWindow(Display.getDefault());
		
		mainWindow.show();
	}
	
}
