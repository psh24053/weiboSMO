package com.psh.query.action;

import com.psh.base.common.PshAction;
import com.psh.base.common.RequestMessageParser;
import com.psh.base.common.ResponseMessageGenerator;
import com.psh.base.common.ErrorCode;
import com.psh.base.json.JSONArray;
import com.psh.base.json.JSONException;
import com.psh.base.json.JSONObject;
import com.psh.base.util.PshLogger;
import com.psh.query.bean.QueryTaskBean;
import com.psh.query.model.CityModel;
import com.psh.query.model.GetFirstQueryPageNumber;
import com.psh.query.model.GetFirstQueryUser;
import com.psh.query.model.ProvModel;
import com.psh.query.model.QueryTaskModel;
import com.psh.query.model.UserQueryTaskModel;


public class StartModifyAction extends PshAction{
	
	public StartModifyAction(){
		
		super.code = 3006;
		super.name = "StartModifyAction";
		
	}
	
	@Override
	public ResponseMessageGenerator handleAction(RequestMessageParser parser) {
		// TODO Auto-generated method stub
		ResponseMessageGenerator generator = new ResponseMessageGenerator();
		
		JSONObject parameter = parser.getParameterJsonObject();
		
		if (parameter == null) {
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		// Start to retrieve required parameters from request
		int gid = -1;
		
		try {
			gid = parameter.getInt("gid");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"gid\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		String idx = null;
		
		try {
			idx = parameter.getString("idx");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"idx\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
	
		JSONObject payload = new JSONObject();
		
		JSONArray list = new JSONArray();
		
		
		
//		try {
//			for(int i = 0 ; i < 3 ; i ++){
//				JSONObject item = new JSONObject();
//				item.put("gid", i+1);
//				item.put("name", i);
//				item.put("status", "正在修改资料中");
//				list.put(item);
//			}
//			payload.put("list", list);
//		} catch (JSONException e) {
//			PshLogger.logger.error("JSONException failed.");
//			PshLogger.logger.error(e.getMessage());
//			return generator.toError(parser, 
//					ErrorCode.ERROR_CODE, 
//					"JSONException error, reason: " + e.getMessage());
//		}
		
		return generator.toSuccess(parser, payload);
	}
	
	
}
