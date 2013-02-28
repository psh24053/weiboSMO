package com.psh.query.action;

import com.psh.base.common.PshAction;
import com.psh.base.common.RequestMessageParser;
import com.psh.base.common.ResponseMessageGenerator;
import com.psh.base.common.ErrorCode;
import com.psh.base.json.JSONException;
import com.psh.base.json.JSONObject;
import com.psh.base.util.PshLogger;
import com.psh.query.bean.AccountBean;
import com.psh.query.bean.CategoryBean;
import com.psh.query.bean.GroupBean;
import com.psh.query.bean.QueryTaskBean;
import com.psh.query.bean.UserBean;
import com.psh.query.model.AccountModel;
import com.psh.query.model.CategoryModel;
import com.psh.query.model.CityModel;
import com.psh.query.model.GetFirstQueryPageNumber;
import com.psh.query.model.GetFirstQueryUser;
import com.psh.query.model.GroupModel;
import com.psh.query.model.ProvModel;
import com.psh.query.model.QueryTaskModel;
import com.psh.query.model.UserQueryTaskModel;


public class AddUserByGroupOnlyPushAction extends PshAction{
	
	public AddUserByGroupOnlyPushAction(){
		
		super.code = 3039;
		super.name = "AddUserByGroupOnlyPushAction";
		
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
		int count = -1;
		
		try {
			count = parameter.getInt("count");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"count\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		JSONObject payload = new JSONObject();
		GroupModel model = new GroupModel();
		
		int result = model.setGroupUserByOnlyPush(gid, count);
		
		if(result == -1){
			return generator.toError(parser, ErrorCode.ERROR_CODE, "向分组添加用户失败");
		}else{
			try {
				payload.put("total", result);
			} catch (JSONException e) {
				PshLogger.logger.error("JSONException failed.");
				PshLogger.logger.error(e.getMessage());
				return generator.toError(parser, 
						ErrorCode.ERROR_CODE, 
						"JSONException error, reason: " + e.getMessage());
			}
			return generator.toSuccess(parser, payload);
		}
		
		
	}
	
	
}
