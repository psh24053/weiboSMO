/**
 * 
 */
package com.psh.query.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.psh.base.common.ResponseMessageGenerator;
import com.psh.base.common.PshActionHandler;
import com.psh.base.exception.PshException;

import com.psh.base.impl.PshBpEntry;
import com.psh.base.util.CommonUtil;

import com.psh.base.util.PshLogger;
import com.psh.base.util.PshConfigManager;
import com.psh.base.util.SQLConn;
import com.psh.query.service.ExcelService;
import com.psh.query.service.ProxyService;
import com.psh.query.util.ProxyManager;

/**
 * @author 1
 *
 */
public class Main extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public void init() throws ServletException {
		
		PshLogger.logger.debug("Enter Main servlet initialization process ...");
		SQLConn.getInstance();
//		ProxyManager.getInstance();
		PshConfigManager.getInstance();
		PshActionHandler.getInstance();
		PshLogger.logger.debug("Leave Main servlet initialization process ...");
		
//		ProxyService proxyService = new ProxyService();
//		proxyService.loadProxyR();
		
	}
	
	public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		
		PshLogger.logger.debug("Enter Main servlet doPost() ...");

		// Read complete request content
		String requestMessage = CommonUtil.readUtf8RequestContent(request);
		ResponseMessageGenerator responseGenerator = null;
		int responseStatus = HttpServletResponse.SC_OK;

		PshLogger.logger.info("Received request message: " + requestMessage);

		PshBpEntry mainEntry = new PshBpEntry();

		try {
			responseGenerator = mainEntry.processRequest(requestMessage, request);
		}
		catch (PshException e) {
			PshLogger.logger.error(e.getMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		// Send back response after finishing action handle
		response.setStatus(responseStatus);
		HashMap<String, String> httpResponseHeader = responseGenerator.getHttpResponseHeader();
		if ( httpResponseHeader != null) {
			Iterator<Entry<String, String>> iter = httpResponseHeader.entrySet().iterator();
			while(iter.hasNext()) {
				Entry<String, String> mapEntry = iter.next(); 
				response.addHeader(mapEntry.getKey(), mapEntry.getValue());
			}
		}
		if (responseGenerator.getResponseType() == ResponseMessageGenerator.RESPONSE_TYPE_JSON) {
			PshLogger.logger.info("Response message to be sent:" + responseGenerator.generate());
			CommonUtil.writeUtf8ResponseContent(response, responseGenerator.generate());
		}
		else if (responseGenerator.getResponseType() == ResponseMessageGenerator.RESPONSE_INPUT_STREAM){
			InputStream is = responseGenerator.getResponseInputStream();
			OutputStream os =  response.getOutputStream();
			
			if (is == null) {
				PshLogger.logger.error("Get input stream from response genrator failed");
				responseStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
				return;
			}
			// read and write
			int ch = 0;

			while( -1 != ( ch = is.read()) ){
				os.write(ch);
			} 
			
			is.close();
			os.close();
		}
		
		PshLogger.logger.debug("Leave Main servlet doPost() ...");
	}
	
	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

		PshLogger.logger.debug("Enter Main servlet doGet() ...");
		
		doPost(request, response);
		
		PshLogger.logger.debug("Leave Main servlet doGet() ...");
		
	}

}
