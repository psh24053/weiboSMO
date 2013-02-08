package com.psh.query.action;

import java.util.List;

import com.psh.base.common.PshAction;
import com.psh.base.common.RequestMessageParser;
import com.psh.base.common.ResponseMessageGenerator;
import com.psh.base.common.ErrorCode;
import com.psh.base.json.JSONArray;
import com.psh.base.json.JSONException;
import com.psh.base.json.JSONObject;
import com.psh.base.util.PshLogger;
import com.psh.query.bean.AccountBean;
import com.psh.query.bean.GroupBean;
import com.psh.query.bean.QueryTaskBean;
import com.psh.query.model.AccountModel;
import com.psh.query.model.CityModel;
import com.psh.query.model.GetFirstQueryPageNumber;
import com.psh.query.model.GetFirstQueryUser;
import com.psh.query.model.ProvModel;
import com.psh.query.model.QueryTaskModel;
import com.psh.query.model.UserQueryTaskModel;


public class GetUserListInfoAction extends PshAction{
	
	public GetUserListInfoAction(){
		
		super.code = 3017;
		super.name = "GetUserListInfoAction";
		
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
		
	
		JSONObject payload = new JSONObject();
		
		AccountModel model = new AccountModel();
		
		int status_88 = model.getUserCount(88);
		int status_89 = model.getUserCount(89);
		int status_90 = model.getUserCount(90);
		int status_91 = model.getUserCount(91);
		int status_old = model.getUserCount(1) + model.getUserCount(3) + model.getUserCount(4) + model.getUserCount(11) + model.getUserCount(33) + model.getUserCount(44); 
		
		try {
			payload.put("88", status_88);
			payload.put("89", status_89);
			payload.put("90", status_90);
			payload.put("91", status_91);
			payload.put("old", status_old);
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
