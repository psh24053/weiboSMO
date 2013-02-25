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
import com.psh.query.model.FansGroupModel;
import com.psh.query.model.GetFirstQueryPageNumber;
import com.psh.query.model.GetFirstQueryUser;
import com.psh.query.model.GroupModel;
import com.psh.query.model.ProvModel;
import com.psh.query.model.QueryTaskModel;
import com.psh.query.model.UserQueryTaskModel;


public class FansGroupAutoExecuteAction extends PshAction{
	
	public FansGroupAutoExecuteAction(){
		
		super.code = 3035;
		super.name = "FansGroupAutoExecuteAction";
		
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
		
		JSONObject payload = new JSONObject();
		FansGroupModel model = new FansGroupModel();
		
		
		if(!model.setFansGroupAuto(gid)){
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE, 
					"error, reason: 粉丝分组初始化失败");
		}
		
		return generator.toSuccess(parser, payload);
	}
	
	
}
