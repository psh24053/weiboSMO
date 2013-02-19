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
import com.psh.query.model.AccountModel;
import com.psh.query.model.CategoryModel;
import com.psh.query.model.CityModel;
import com.psh.query.model.GetFirstQueryPageNumber;
import com.psh.query.model.GetFirstQueryUser;
import com.psh.query.model.GroupModel;
import com.psh.query.model.ProvModel;
import com.psh.query.model.QueryTaskModel;
import com.psh.query.model.UserQueryTaskModel;


public class AddUserByGroupAction extends PshAction{
	
	public AddUserByGroupAction(){
		
		super.code = 3025;
		super.name = "AddUserByGroupAction";
		
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
		String email = "";
		try {
			email = parameter.getString("email");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"email\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		String password = "";
		try {
			password = parameter.getString("password");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"password\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
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
		AccountModel model = new AccountModel();
		AccountBean account = new AccountBean();
		
		account.setUid(uid);
		account.setEmail(email);
		account.setPassword(password);
		
		
		
		
		boolean result = model.InsertAccountByGid(account, gid);
		
		if(result){
			return generator.toSuccess(parser, payload);
		}
		
		return generator.toError(parser, ErrorCode.ERROR_CODE, "添加用户失败");
	}
	
	
}
