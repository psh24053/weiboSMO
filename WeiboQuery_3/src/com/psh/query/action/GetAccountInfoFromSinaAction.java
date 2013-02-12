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
import com.psh.query.bean.QueryTaskBean;
import com.psh.query.bean.TextBean;
import com.psh.query.model.AccountModel;
import com.psh.query.model.CityModel;
import com.psh.query.model.GetFirstQueryPageNumber;
import com.psh.query.model.GetFirstQueryUser;
import com.psh.query.model.GroupModel;
import com.psh.query.model.ProvModel;
import com.psh.query.model.QueryTaskModel;
import com.psh.query.model.TextModel;
import com.psh.query.model.UserQueryTaskModel;
import com.psh.query.service.InfoService;


public class GetAccountInfoFromSinaAction extends PshAction{
	
	public GetAccountInfoFromSinaAction(){
		
		super.code = 3019;
		super.name = "GetAccountInfoFromSinaAction";
		
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
		long uid = -1;
		
		try {
			uid = parameter.getLong("uid");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"uid\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
	
		JSONObject payload = new JSONObject();
		
		InfoService info = new InfoService();
		
		if(info.synchronizeInfoFromSina(uid)){
			
			try {
				payload.put("nck", info.getAccount().getNickname());
				payload.put("prov", info.getAccount().getProv());
				payload.put("city", info.getAccount().getCity());
				payload.put("gender", info.getAccount().getSex());
				payload.put("birthday", info.getAccount().getBirthday());
				payload.put("info", info.getAccount().getInfo());
				payload.put("tags", info.getAccount().getTags());
			} catch (JSONException e) {
				PshLogger.logger.error("JSONException failed.");
				PshLogger.logger.error(e.getMessage());
				return generator.toError(parser, 
						ErrorCode.ERROR_CODE, 
						"JSONException error, reason: " + e.getMessage());
			}
		}else{
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE, 
					"error, reason: 读取资料失败");
		}
		
		
		return generator.toSuccess(parser, payload);
	}
	
	
}
