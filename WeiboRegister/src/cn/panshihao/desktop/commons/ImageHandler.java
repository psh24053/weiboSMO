package cn.panshihao.desktop.commons;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

public class ImageHandler {

	private Display display;
	private String resPath;
	private int imageType;
	private int imageWidth;
	private int imageHeight;
	private float scaling;
	
	/**
	 * 传入Display和resPath，构造一个基本的图片对象
	 * @param display
	 * @param resPath
	 */
	public ImageHandler(Display display, String resPath){
		this.display = display;
		this.resPath = resPath;
		this.imageType = SWT.IMAGE_PNG;
		this.scaling = 1.0f;
	} 
	/**
	 * 传入图片的大小来规定生成的图片大小
	 * @param display
	 * @param resPath
	 * @param width
	 * @param height
	 */
	public ImageHandler(Display display, String resPath, int width, int height){
		this.display = display;
		this.resPath = resPath;
		this.imageType = SWT.IMAGE_JPEG;
		this.imageWidth = width;
		this.imageHeight = height;
		this.scaling = 1.0f;
	}
	/**
	 * 传入图片大小的缩放比例来规定生成的图片的大小
	 * @param display
	 * @param resPath
	 * @param scaling
	 */
	public ImageHandler(Display display, String resPath, float scaling){
		this.display = display;
		this.resPath = resPath;
		this.imageType = SWT.IMAGE_JPEG;
		this.scaling = scaling;
	}
	
	/**
	 * 根据当前的各种条件，获取Image对象
	 * @return
	 */
	public Image getImage(){
		ImageData data = new ImageData(resPath);
		
		data.type = this.imageType;
		if(this.imageWidth == 0 && this.imageHeight == 0){
			data = data.scaledTo((int)(data.width * this.scaling), (int)(data.height * this.scaling));
		}else{
			data = data.scaledTo(imageWidth, imageHeight);
		}
		
		return new Image(display, data);
	}

	public Display getDisplay() {
		return display;
	}

	public String getResPath() {
		return resPath;
	}

	public int getImageType() {
		return imageType;
	}

	public void setImageType(int imageType) {
		this.imageType = imageType;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public float getScaling() {
		return scaling;
	}

	public void setScaling(float scaling) {
		this.scaling = scaling;
	}
	
}
