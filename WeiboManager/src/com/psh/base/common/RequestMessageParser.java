package com.psh.base.common;

import javax.servlet.http.HttpServletRequest;

import com.psh.base.json.JSONException;
import com.psh.base.json.JSONObject;
import com.psh.base.util.PshLogger;


public class RequestMessageParser {

	// Success
	final static public int ERROR_SUCCESS = 0;
	// JSON message format is not correct
	final static public int ERROR_INVALID_FORMAT = 1;
	// JSON message can not be parsed as defined object
	final static public int ERROR_INVALID_OBJECT = 2;

	//
	private int actionCode = 0;
	//
	private String actionAuthentication = null;
	
	private int errorCode = ERROR_SUCCESS;

	// Request JSON object
	private JSONObject requestJsonObject = null;
	
	// Parameter JSON object
	private JSONObject parameterJsonObject = null;
	
	// Original JSON message text
	private String requestMessage = null;
	
	// Original request object
	private HttpServletRequest request = null;
	
	public RequestMessageParser () {
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	
	public int getActionCode() {
		return actionCode;
	}
	
	public String getActionAuthentication () {
		return actionAuthentication;
	}
	
	public JSONObject getParameterJsonObject() {
		return parameterJsonObject;
	}

	public JSONObject getRequestJsonObject() {
		return requestJsonObject;
	}
	
	public String getRequestMessage ( ) {
		
		return requestMessage;
	
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public boolean parse(String RequestMessage) {
	
		boolean isSuccess = false;

		try {
			requestJsonObject = new JSONObject(RequestMessage); 
		}
		catch(JSONException ex) {
			PshLogger.logger.error("Parse input JSON message failed.");
			PshLogger.logger.error(ex.toString());
			errorCode = ERROR_INVALID_FORMAT;
			return isSuccess;
		}

		// Parse fixed element (header)

		// Parse action code
		try {
			actionCode = requestJsonObject.getInt(PshMessageConstant.Message_CODE);
		}
		catch (JSONException ex) {
			PshLogger.logger.error("Parse action code element failed.");
			PshLogger.logger.error(ex.toString());
			errorCode = ERROR_INVALID_OBJECT;
			return isSuccess;
		}

		// Parse action authentication ( optional )
		try {
			actionAuthentication = requestJsonObject.getString(PshMessageConstant.Message_AUTHENTICATION);
		}
		catch(JSONException ex) {
			// If action authentication missed, set it to null;
			actionAuthentication = null;
		}

		// Parse request parameter ( optional )		
		try {
			parameterJsonObject = requestJsonObject.getJSONObject(PshMessageConstant.Message_PARAMETER);
		}
		catch (JSONException ex) {
			// If action has no parameter, set it to null;
			parameterJsonObject = null;
		}
		
		// All parse is successful
		this.requestMessage = RequestMessage;
		
		isSuccess = true;
		
		return isSuccess;		
	}
	
}
