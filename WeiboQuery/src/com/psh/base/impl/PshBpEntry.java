package com.psh.base.impl;

import javax.servlet.http.HttpServletRequest;

import com.psh.base.common.Dispatcher;
import com.psh.base.common.RequestMessageParser;
import com.psh.base.common.ResponseMessageGenerator;
import com.psh.base.common.ErrorCode;
import com.psh.base.exception.PshException;
import com.psh.base.util.PshLogger;


public class PshBpEntry {
	
	
	// Based on JSON message
	public ResponseMessageGenerator processRequest(String jsonRequest, HttpServletRequest request) throws PshException {
		
		PshLogger.logger.debug("Enter ShntecBpEntry processRequest() ... ");
		
		// Parse received request message, check JSON message format  
		RequestMessageParser parser = new RequestMessageParser();
		
		if (!parser.parse(jsonRequest)) {
			throw new PshException(ErrorCode.ERROR_CODE);
		}
		
		parser.setRequest(request);
		
		Dispatcher  dispatcher = null;
		ResponseMessageGenerator generator = null;
		
		dispatcher = new Dispatcher();
		generator = dispatcher.dispatcherHandler(parser);
		
		if (null == generator) {
			PshLogger.logger.error("JSON request message process failed.");
			throw new PshException(ErrorCode.ERROR_CODE);
		}
		
		
		//jsonResponse = generator.generate();
		
		PshLogger.logger.debug("Leave ShntecBpEntry processRequest() ... ");

		return generator;
	}

}
