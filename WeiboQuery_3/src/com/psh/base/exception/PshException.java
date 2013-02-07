/**
 * 
 */
package com.psh.base.exception;

import com.psh.base.common.ErrorCode;

/**
 * @author 1
 *
 */
public class PshException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String moreInformation = null;
	
	private int errorCode = 6999;

	/**
	 * 
	 */
	public PshException() {
		// TODO Auto-generated constructor stub
	}
	
	public PshException(int errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @param arg0
	 */
	public PshException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public PshException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public PshException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	
	
	public void setErrorCode (int errorCode) {
		this.errorCode = errorCode;
	}
	public int getErrorCode () {
		return errorCode;
	}
	
	public void setMoreInformation(String moreInformation) {
		this.moreInformation = moreInformation;
	}
	
	public String getMoreInformation() {
		return moreInformation;
	}

}
