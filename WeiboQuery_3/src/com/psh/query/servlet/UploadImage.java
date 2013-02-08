package com.psh.query.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FilenameUtils;

import com.psh.base.common.ErrorCode;
import com.psh.base.common.ResponseMessageGenerator;
import com.psh.base.util.PshLogger;


public class UploadImage extends HttpServlet {

	/**
	 * Upload news image to server at specific position.
	 * 
	 * Action code: 3011
	 */
	private static final long serialVersionUID = 3521665148897876708L;

	private static final int ACTION_CODE_UPLOAD_IMAGE = 3025;
	
	private static String generateResponse(String message) {
		return "<script>parent.on_upload_completed(eval(\"(\"+ '" + message + "' +\")\"));</script>";
	}
	
	public UploadImage() {
		// TODO Auto-generated constructor stub
	}
	
	public void init() throws ServletException {
		
	}
	
	public void destroy() {
		
	}

	public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

		ResponseMessageGenerator  generator = new ResponseMessageGenerator();

		response.setCharacterEncoding("UTF-8");
		
		String imageFileID = null;
		
		// Handle type parameter, 1: upload event background image; 2: upload news images 
		String typeString = null;
		int typeInteger = 0;
		
		typeString = request.getParameter("type");
		if (typeString == null) {
			generator.generateErrorResponse(ACTION_CODE_UPLOAD_IMAGE, 
					ErrorCode.ERROR_CODE, "缺少所需的参数: \"type\"");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(generateResponse(generator.generate()));
			return;
		}
		
		try {
			typeInteger = Integer.parseInt(typeString);
			if (typeInteger != 1 && typeInteger != 2) {
				throw new NumberFormatException();
			}
		}
		catch (NumberFormatException e){
			generator.generateErrorResponse(ACTION_CODE_UPLOAD_IMAGE, 
					ErrorCode.ERROR_CODE, "参数\"type\"包含无效参数值: \"" + typeString +"\"");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(generateResponse(generator.generate()));
			return;
		}
		
		// Handle parameter idx
		String idx = request.getParameter("idx");
		
		// Handle updated image according to type
		ServletFileUpload upload = new ServletFileUpload();		
		try {
			FileItemIterator  iter = upload.getItemIterator(request);
			while (iter.hasNext()) {
				FileItemStream  item = iter.next();
			    String name = item.getFieldName();
			    String fileName = new String(item.getName().getBytes(), "UTF-8");
			    if (fileName != null) {
			        fileName = FilenameUtils.getName(fileName);
			    }
			    if (item.isFormField()) {
			        PshLogger.logger.debug("Form field " + name + " with value detected.");
			    } else {
			        PshLogger.logger.debug("File field " + name + " with file name "
			            + fileName + " detected.");
			        
			        // Generate target file path according to type
					String targetFilePath= null;
					if (typeInteger == 1) {
						String eventID = request.getParameter("eid");
						if ( null == eventID ) {
							generator.generateErrorResponse(ACTION_CODE_UPLOAD_IMAGE, 
									ErrorCode.ERROR_CODE, 
									"缺少所需的参数: \"eid\"");
							response.setStatus(HttpServletResponse.SC_OK);
							response.getWriter().write(generateResponse(generator.generate()));
							return;
						}
						
						NewsEventObject event = 
								NewsEventManager.getInstance().getNewsEvent(eventID);
						
						if (event == null) {
							generator.generateErrorResponse(ACTION_CODE_UPLOAD_IMAGE, 
									XMMServerErrorCode.XMMSERVER_ERROR_CODE_EVENT_NOT_EXIST, 
									"Event ID: " + eventID);
							response.setStatus(HttpServletResponse.SC_OK);
							response.getWriter().write(generateResponse(generator.generate()));
							return;
						}
						
						String eventImageFolder = event.getEventImageStoragePath();
						
						targetFilePath = eventImageFolder + File.separator + fileName;
						
					}
					else if (typeInteger == 2) {
						String newsID = request.getParameter("nid");
						if ( null == newsID ) {
							generator.generateErrorResponse(ACTION_CODE_UPLOAD_IMAGE, 
									ErrorCode.ERROR_CODE, 
									"缺少所需的参数: \"nid\"");
							response.setStatus(HttpServletResponse.SC_OK);
							response.getWriter().write(generateResponse(generator.generate()));
							return;
						}
					    String newsImageFolder = NewsEventStorageHelper.getNewsImageFolder(newsID);
					    if (newsImageFolder == null) {
							generator.generateErrorResponse(ACTION_CODE_UPLOAD_IMAGE, 
									ErrorCode.SHNTEC_ERROR_CODE_SYSTEM_ERROR, "获取新闻存储目录错误，新闻ID: " + newsID);
							response.setStatus(HttpServletResponse.SC_OK);
							response.getWriter().write(generateResponse(generator.generate()));
							return;
					    }
					    targetFilePath = newsImageFolder + File.separator + fileName;
					}
					else {
						generator.generateErrorResponse(ACTION_CODE_UPLOAD_IMAGE, 
								ErrorCode.SHNTEC_ERROR_CODE_SYSTEM_ERROR, "不支持的\"type\"参数的值: " + typeInteger);
						response.setStatus(HttpServletResponse.SC_OK);
						response.getWriter().write(generateResponse(generator.generate()));
						return;
					}

					// Copy file content to target file path
		        	Streams.copy(
			        		new BufferedInputStream(item.openStream()),
			        		new BufferedOutputStream(new FileOutputStream(
			        				new File(targetFilePath))), 
			        		true);
		        	
		        	// Create file index into database
			        imageFileID = FileStorageManager.getInstance().allocateFileStorage(targetFilePath,
			        		fileName,
			        		null,
			        		new File(targetFilePath).length());
			        
			        // Generate success response payload
			        JSONObject payload = new JSONObject();

					try {
						payload.put("fid", imageFileID);
						payload.put("idx", idx);
					} catch (JSONException e) {
						PshLogger.logger.error("Generate JSON response failed.");
						PshLogger.logger.error(e.getMessage());
						generator.generateErrorResponse(ACTION_CODE_UPLOAD_IMAGE, 
								ErrorCode.SHNTEC_ERROR_CODE_SYSTEM_ERROR, 
								"Generate JSON response error, reason: " + e.getMessage());
						response.setStatus(HttpServletResponse.SC_OK);
						response.getWriter().write(generateResponse(generator.generate()));
						return;
						
					}

					generator.generateSuccessResponse(ACTION_CODE_UPLOAD_IMAGE, payload);
					response.setStatus(HttpServletResponse.SC_OK);
					response.getWriter().write(generateResponse(generator.generate()));
			   
			    }
			}
		}
		catch(Exception e){
			generator.generateErrorResponse(ACTION_CODE_UPLOAD_IMAGE, 
					ErrorCode.SHNTEC_ERROR_CODE_SYSTEM_ERROR, "处理文件上传请求错误。");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(generateResponse(generator.generate()));
			return;		
		} 
		
	}
	
	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

		PshLogger.logger.debug("Enter FileUpload servlet doGet() ...");
		
		doPost(request, response);
		
		PshLogger.logger.debug("Leave FileUpload servlet doGet() ...");
		
	}
}
