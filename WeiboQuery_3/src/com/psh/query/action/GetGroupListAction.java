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
import com.psh.query.bean.GroupBean;
import com.psh.query.bean.QueryTaskBean;
import com.psh.query.model.CityModel;
import com.psh.query.model.GetFirstQueryPageNumber;
import com.psh.query.model.GetFirstQueryUser;
import com.psh.query.model.GroupModel;
import com.psh.query.model.ProvModel;
import com.psh.query.model.QueryTaskModel;
import com.psh.query.model.UserQueryTaskModel;


public class GetGroupListAction extends PshAction{
	
	public GetGroupListAction(){
		
		super.code = 3004;
		super.name = "GetGroupListAction";
		
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
		int cid = -1;
		
		try {
			cid = parameter.getInt("cid");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"cid\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
	
		JSONObject payload = new JSONObject();
		
		GroupModel model = new GroupModel();
	 	List<GroupBean> data = model.getGroupList(cid);
		
		JSONArray list = new JSONArray();
		
		
		
		try {
			for(int i = 0 ; i < data.size() ; i ++){
				JSONObject item = new JSONObject();
				GroupBean bean = data.get(i);
				item.put("gid", bean.getGid());
				item.put("name", bean.getName());
				item.put("status", bean.getStatus());
				item.put("cid", bean.getCid());
				list.put(item);
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
