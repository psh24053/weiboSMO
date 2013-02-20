package com.psh.query.action;

import java.util.ArrayList;
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
import com.psh.query.bean.MsgBean;
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


public class SearchWeiboByUidAction extends PshAction{
	
	public SearchWeiboByUidAction(){
		
		super.code = 3028;
		super.name = "SearchWeiboByUidAction";
		
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
		AccountBean account = new AccountBean();
		account.setEmail("psh24053@yahoo.cn");
		account.setPassword("caicai520");
		account.setUid(1661461070);
		WeiboLoginService weiboLogin = new WeiboLoginService(account);
		
		if(!weiboLogin.Login()){
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE, 
					"error, reason: 读取资料失败,登录失败");
		}
		
		List<MsgBean> data = new ArrayList<MsgBean>();
		
		for(int i = 0 ; i < 1000 ; i++){
			
			List<MsgBean> data_1 = weiboLogin.searchUid(uid,i+1);
			if(data_1 == null){
				//页数已超过用户
				break;
			}
			if(data_1.size() == 0){
				continue;
			}
			data.addAll(data_1);
			if(data.size() >= count){
				break;
			}
		}
		
		
		data = data.subList(0, count);
		
		JSONArray list = new JSONArray();
		
		try {
			for(int i = 0 ; i < data.size() ; i ++){
				JSONObject json = new JSONObject();
				json.put("mid", data.get(i).getMid());
				json.put("time", data.get(i).getTime());
				json.put("content", data.get(i).getCon());
				
				list.put(json);
			}
			payload.put("list", list);
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
