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
import com.psh.query.model.ReplyModel;
import com.psh.query.model.TextModel;
import com.psh.query.model.UserQueryTaskModel;
import com.psh.query.service.InfoService;
import com.psh.query.service.ModifyService;
import com.psh.query.service.WeiboLoginService;


public class RefreshByLastMidAction extends PshAction{
	
	public RefreshByLastMidAction(){
		
		super.code = 3031;
		super.name = "RefreshByLastMidAction";
		
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
		String mid = null;
		
		try {
			mid = parameter.getString("mid");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"mid\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		ReplyModel reply = new ReplyModel();
		if(mid.equals("")){
			
			MsgBean m = reply.getLastMsg(uid);
			if(m != null){
				mid = m.getMid();
			}else{
				mid = null;
			}
			
		}
		
		JSONObject payload = new JSONObject();
		AccountModel accountmodel = new AccountModel();
		AccountBean account = accountmodel.getAccount(uid);
		WeiboLoginService weiboLogin = new WeiboLoginService(account);
		
		if(!weiboLogin.Login()){
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE, 
					"error, reason: 检查新信息失败,登录失败");
		}
		// 如果mid为null，则获取7天之内的所有msg
		List<MsgBean> tomedata = weiboLogin.getToMeWeibo(mid);
		
		for(int i = 0 ; i < tomedata.size() ; i ++){
			MsgBean tome = tomedata.get(i);
			tome.setType("@我的微博 "+tome.getType());
		}
		
		JSONArray list = new JSONArray();
		
		try {
			for(int i = tomedata.size() - 1 ; i >= 0  ; i --){
				tomedata.get(i).setFuid(uid);
				list.put(tomedata.get(i).toJSON());
			}
			
			payload.put("list", list);
		} catch (JSONException e) {
			PshLogger.logger.error("JSONException failed.");
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE, 
					"JSONException error, reason: " + e.getMessage());
		}
		
		
		reply.batchInsert(tomedata);
		
		
		return generator.toSuccess(parser, payload);
	}
	
	
}
