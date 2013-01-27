package cn.panshihao.desktop.commons;


public abstract class AsyncHandler<Params, Progress, Result> {

	private SuperWindow window;
	private Params[] params;
	private Progress progress;
	private Result result;
	/**
	 * 必须传入一个superWindow
	 * @param window
	 */
	public AsyncHandler(SuperWindow window){
		this.window = window;
	}
	
	/**
	 * 在非UI线程中运行
	 * @param params
	 * @return
	 */
	public abstract Result doInBackground(Params... params);
	
	/**
	 * 当doInBackground执行完成之后，在UI线程中运行
	 * @param result
	 */
	public void onComplete(Result result){
		
	}
	/**
	 * 在doInBackground执行之前，在UI线程中运行
	 */
	public void onBefore(){
		
	}
	/**
	 * 调用upProgress后触发，在UI线程中执行
	 * @param progress
	 */
	public void onProgress(Progress progress){
		
	}
	/**
	 * 可以在doInBackground中调用，会触发onProgress在UI线程中运行
	 * @param progress
	 */
	public void upProgress(Progress pro){
		this.progress = pro;
		
		window.getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				onProgress(progress);
			}
		});
		
	}
	/**
	 * 开始Handler
	 * @param params
	 */
	public void start(Params... par){
		this.params = par;
		
		onBefore();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				result = doInBackground(params);
				
				window.getDisplay().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						onComplete(result);
					}
				});
				
			}
		}).start();
		
		
	}
}
