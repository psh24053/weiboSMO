package com.psh.query.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.psh.base.common.PshAction;
import com.psh.base.common.RequestMessageParser;
import com.psh.base.common.ResponseMessageGenerator;
import com.psh.base.common.ErrorCode;
import com.psh.base.json.JSONArray;
import com.psh.base.json.JSONException;
import com.psh.base.json.JSONObject;
import com.psh.base.util.PshLogger;
import com.psh.query.bean.QueryTaskBean;
import com.psh.query.bean.UserBean;
import com.psh.query.model.CityModel;
import com.psh.query.model.GetFirstQueryPageNumber;
import com.psh.query.model.GetFirstQueryUser;
import com.psh.query.model.ProvModel;
import com.psh.query.model.QueryTaskModel;
import com.psh.query.model.UserModel;
import com.psh.query.model.UserQueryTaskModel;


public class GeLocalUserAction extends PshAction{
	
	public GeLocalUserAction(){
		
		super.code = 3030;
		super.name = "GeLocalUserAction";
		
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
		String nickName = "";
		
		try {
			nickName = parameter.getString("nickName");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"nickName\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		String tag = "";
		
		try {
			tag = parameter.getString("tag");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"tag\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		
		String fans = "";
		
		try {
			fans = parameter.getString("fans");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"fans\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		String fol = "";
		
		try {
			fol = parameter.getString("fol");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"fol\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		int count = -1;
		
		try {
			count = parameter.getInt("count");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing:\"count\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		Map<String, Object> condiMap = new HashMap<String, Object>();
		
		if(nickName != null && !nickName.equals("")){
			
			condiMap.put("nck", nickName);
			
		}
		
		if(tag != null && !tag.equals("")){
			
			condiMap.put("tag", tag);
			
		}
		
		if(fans != null && !fans.equals("")){
			
			condiMap.put("fans", fans);
		}
		
		if(fol != null && !fol.equals("")){
			
			condiMap.put("fol", fol);
		}
		
		JSONObject payload = new JSONObject();
		//查找该次搜索的数据源数量
		
		UserModel userModel = new UserModel();
		List<UserBean> userNameList = userModel.getUserLocalQuery(condiMap, count);
//		
//		JSONArray array = new JSONArray();
//		for(int i = 0 ; i < count ; i++){
//			
//			array.put("非常开心" + i);
//		}
		
		JSONArray array = new JSONArray();
		
		try {
			for(int i = 0 ; i < userNameList.size() ; i ++){
				UserBean b = userNameList.get(i);
				JSONObject item = new JSONObject();
				item.put("uid", b.getUid());
				item.put("nck", b.getUck());
				array.put(item);
			}
			
			payload.put("list", array);
			payload.put("count", array.length());
//			payload.put("array", array);
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
