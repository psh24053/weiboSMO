package com.psh.base.common;

import java.io.InputStream;
import java.util.HashMap;

import com.psh.base.json.JSONException;
import com.psh.base.json.JSONObject;
import com.psh.base.util.PshLogger;


public class ResponseMessageGenerator {
	
	public final static int RESPONSE_TYPE_JSON = 1;
	public final static int RESPONSE_TYPE_BINARY = 2;
	public final static int RESPONSE_INPUT_STREAM = 3;

	public final static int RESPONSE_TYPE_DEFAULT = RESPONSE_TYPE_JSON;
	
	
	private JSONObject reponseObject = null;
	
	private HashMap<String, String> httpResponseHeader = null;

	private int responseType = RESPONSE_TYPE_DEFAULT;
	
	
	// 
	private InputStream responseInputStream = null;
	
	public ResponseMessageGenerator() {
		httpResponseHeader = new HashMap<String, String>();
	}

	public ResponseMessageGenerator(JSONObject responseObject) {
		httpResponseHeader = new HashMap<String, String>();
		setResponseObject(responseObject);
	}
	
	public void addHttpResponseHeader (String header, String headerContent) {
		httpResponseHeader.put(header, headerContent);
	}
	
	public HashMap<String, String> getHttpResponseHeader() {
		return httpResponseHeader;
	}
	
	public int getResponseType() {
		return responseType;
	}
	
	public void setResponseType(int responseType) {
		this.responseType = responseType;
	}
	
	public InputStream getResponseInputStream() {
		return responseInputStream;
	}
	
	public void setResponseInputStream(InputStream reponseInputStream) {
		this.responseInputStream = reponseInputStream;
	}
	
	public ResponseMessageGenerator generateSuccessResponse(int actionCode, JSONObject payload) {
		
		JSONObject response = new JSONObject();

		try {
			response.put(PshMessageConstant.Message_CODE, actionCode);
			response.put(PshMessageConstant.Message_RESULT, true);
			if (payload != null) {
				response.put(PshMessageConstant.Message_PAYLOAD, payload);
			}
		}
		catch (JSONException e) {
			PshLogger.logger.error("Generate JSON success response failed.");
			PshLogger.logger.error(e.getMessage());
			return null;
		}
		
		setResponseObject(response);
		
		return this;		
	}	
	
	public ResponseMessageGenerator generateSuccessResponse(RequestMessageParser parser, JSONObject payload) {
		
		return generateSuccessResponse(parser.getActionCode(), payload);
		
	}
	
	public ResponseMessageGenerator toSuccess(RequestMessageParser parser) {

		return generateSuccessResponse(parser, null);
	
	}

	public ResponseMessageGenerator toSuccess(RequestMessageParser parser, JSONObject payload) {

		return generateSuccessResponse(parser, payload);
		
	}
	
	public ResponseMessageGenerator generateErrorResponse(int actionCode, int errorCode, String moreInfo) {

		JSONObject response = new JSONObject();
		
		JSONObject error = new JSONObject();
		try {
			error.put(PshMessageConstant.Message_ERROR_CODE, errorCode);
			if (moreInfo != null && ! moreInfo.trim().isEmpty()) {
				error.put(PshMessageConstant.Message_INFO, moreInfo);
			}
		}
		catch (JSONException e) {
			PshLogger.logger.error("Generate JSON error payload failed.");
			PshLogger.logger.error(e.getMessage());
		}
		
		try {
			response.put(PshMessageConstant.Message_CODE, actionCode);
			response.put(PshMessageConstant.Message_RESULT, false);
			response.put(PshMessageConstant.Message_PAYLOAD, error);
		}
		catch (JSONException e) {
			PshLogger.logger.error("Generate JSON error response failed.");
			PshLogger.logger.error(e.getMessage());
			return null;
		}
		
		setResponseObject(response);
		
		return this;		
		
	}
	
	public ResponseMessageGenerator generateErrorResponse(RequestMessageParser parser, int errorCode, String moreInfo) {

		return generateErrorResponse(parser.getActionCode(), errorCode, moreInfo);
		
	}
	
	public ResponseMessageGenerator toError(RequestMessageParser parser, int errorCode) {
		
		return generateErrorResponse(parser, errorCode, null);
		
	}
	
	public ResponseMessageGenerator toError(RequestMessageParser parser, int errorCode, String moreInfo) {
		
		return generateErrorResponse(parser, errorCode, moreInfo);
		
	}
	
	public void setResponseObject (JSONObject responseObject) {
		this.reponseObject = responseObject;
	}
	
	public JSONObject getResponseObject() {
		return this.reponseObject;
	}
	
	public String generate() {
		return reponseObject.toString();
	}
}
