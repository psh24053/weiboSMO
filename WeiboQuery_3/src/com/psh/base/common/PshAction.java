package com.psh.base.common;

public abstract class PshAction {

	protected int code = 0;
	
	public int getActionCode() {
		
		return code;
		
	}
	
	protected String name = null;
	
	public String getActionName() {
		
		return name;
		
	}
	
	public abstract ResponseMessageGenerator handleAction( RequestMessageParser parser );

	
	
	public ResponseMessageGenerator actionExecute(RequestMessageParser parser) {
		
		return handleAction(parser);
	}
}
