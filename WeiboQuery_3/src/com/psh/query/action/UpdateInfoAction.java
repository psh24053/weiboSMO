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


public class UpdateInfoAction extends PshAction{
	
	public UpdateInfoAction(){
		
		super.code = 3021;
		super.name = "UpdateInfoAction";
		
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
		String nck = null;
		
		try {
			nck = parameter.getString("nck");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"nck\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		String prov = null;
		
		try {
			prov = parameter.getString("prov");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"prov\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		String city = null;
		
		try {
			city = parameter.getString("city");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"city\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		String gender = null;
		
		try {
			gender = parameter.getString("gender");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"gender\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		String birthday = null;
		
		try {
			birthday = parameter.getString("birthday");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"birthday\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		String info = null;
		
		try {
			info = parameter.getString("info");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"info\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		String tags = null;
		
		try {
			tags = parameter.getString("tags");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"tags\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		JSONObject payload = new JSONObject();
		ModifyService modify = new ModifyService();
		AccountModel accountmodel = new AccountModel();
		AccountBean account = accountmodel.getAccount(uid);
		
		account.setBirthday(birthday);
		account.setCity(city);
		account.setInfo(info);
		account.setNickname(nck);
		account.setProv(prov);
		account.setSex(gender);
		account.setTags(tags);
		account.setUid(uid);
		
		if(!modify.modify(account)){
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE, 
					"error, reason: 更新资料失败");
		}
		
		
		return generator.toSuccess(parser, payload);
	}
	
	
}
