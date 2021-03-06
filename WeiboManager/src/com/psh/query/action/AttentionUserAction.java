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
import com.psh.query.service.ModifyService;
import com.psh.query.service.WeiboLoginService;


public class AttentionUserAction extends PshAction{
	
	public AttentionUserAction(){
		
		super.code = 3027;
		super.name = "AttentionUserAction";
		
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
		long myuid = -1;
		
		try {
			myuid = parameter.getLong("myuid");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"myuid\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		long attuid = -1;
		
		try {
			attuid = parameter.getLong("attuid");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"attuid\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		JSONObject payload = new JSONObject();
		AccountModel accountmodel = new AccountModel();
		AccountBean account = accountmodel.getAccount(myuid);
		WeiboLoginService weiboLogin = new WeiboLoginService(account);
		
		if(!weiboLogin.Login()){
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE, 
					"error, reason: 关注失败,登录失败");
		}
		
		
		if(!weiboLogin.attention(attuid)){
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE, 
					"error, reason: 关注失败");
		}
		
		return generator.toSuccess(parser, payload);
	}
	
	
}
