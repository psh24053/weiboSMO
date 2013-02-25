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


public class GetGroupUserListAction extends PshAction{
	
	public GetGroupUserListAction(){
		
		super.code = 3011;
		super.name = "GetGroupUserListAction";
		
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
		List<AccountBean> data = model.getGroupUserList(gid);
		
		JSONArray list = new JSONArray();
		
		
		
		try {
			for(int i = 0 ; i < data.size() ; i ++){
				JSONObject item = new JSONObject();
				AccountBean bean = data.get(i);
				item.put("uid", bean.getUid());
				item.put("nck", bean.getNickname());
				item.put("email", bean.getEmail());
				item.put("prov", bean.getProv());
				item.put("city", bean.getCity());
				item.put("gender", bean.getSex());
				item.put("birthday", bean.getBirthday());
				item.put("info", bean.getInfo());
				item.put("tags", bean.getTags());
				list.put(item);
			}
			payload.put("list", list);
			payload.put("total", data.size());
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
