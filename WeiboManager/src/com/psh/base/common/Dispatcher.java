package com.psh.base.common;

import com.psh.base.util.PshLogger;

public class Dispatcher {

	// Indicate whether need to check license

	public ResponseMessageGenerator dispatcherHandler(RequestMessageParser parser) {

		PshLogger.logger.debug("Enter NormalRequestDispatcher.dispatcherHandler() ...");
		
		ResponseMessageGenerator generator = null;
		
		// Find action handler and execute action
		try {
			PshLogger.logger.debug("Handle action by shntec action handler.");				
			// Handled by real action handler
			PshActionHandler shntecActionHandler = PshActionHandler.getInstance();
			generator = shntecActionHandler.handleAction(parser);
		} catch (Exception e) {
			PshLogger.logger.error("Execute action: " + parser.getActionCode() + " failed.");
			PshLogger.logger.error(e.getMessage());
			generator = null;
		}
		
		return generator;
	}
	
}
