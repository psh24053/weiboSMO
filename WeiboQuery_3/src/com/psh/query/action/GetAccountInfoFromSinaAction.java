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
import com.psh.query.service.ProxyService;
import com.psh.query.service.WeiboLoginService;


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
		AccountModel model = new AccountModel();
		AccountBean account = model.getAccount(uid);
		
		if(account == null){
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE, 
					"error, reason: 读取资料失败,uid不存在");
		}
//		ProxyService proxyService = new ProxyService();
		
		WeiboLoginService weiboLogin = new WeiboLoginService(account);
		
		if(weiboLogin.Login()){
			account = weiboLogin.readInfo(true);
			System.out.println(account);
			if(account != null){
				try {
					payload.put("nck", account.getNickname());
					payload.put("prov", account.getProv());
					payload.put("city", account.getCity());
					payload.put("gender", account.getSex());
					payload.put("birthday", account.getBirthday());
					payload.put("info", account.getInfo());
					payload.put("tags", account.getTags());
				} catch (JSONException e) {
					PshLogger.logger.error("JSONException failed.");
					PshLogger.logger.error(e.getMessage(),e);
					return generator.toError(parser, 
							ErrorCode.ERROR_CODE, 
							"JSONException error, reason: " + e.getMessage());
				}
			}else{
				return generator.toError(parser, 
						ErrorCode.ERROR_CODE, 
						"error, reason: 读取资料失败");
			}
			
		}else{
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE, 
					"error, reason: 读取资料失败,登录失败");
		}
		
		
		
		
		return generator.toSuccess(parser, payload);
	}
	
	
}
