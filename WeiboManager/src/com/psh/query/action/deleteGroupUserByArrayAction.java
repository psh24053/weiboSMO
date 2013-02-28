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
import com.psh.query.model.GetFirstQueryPageNumber;
import com.psh.query.model.GetFirstQueryUser;
import com.psh.query.model.GroupModel;
import com.psh.query.model.ProvModel;
import com.psh.query.model.QueryTaskModel;
import com.psh.query.model.UserQueryTaskModel;


public class deleteGroupUserByArrayAction extends PshAction{
	
	public deleteGroupUserByArrayAction(){
		
		super.code = 3040;
		super.name = "deleteGroupUserByArrayAction";
		
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
		JSONArray uidarray = null;
		
		try {
			uidarray = parameter.getJSONArray("uidarray");
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"uidarray\"" );
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
		GroupModel model = new GroupModel();
		
		long[] uids = new long[uidarray.length()];
		
		for(int i = 0 ; i < uidarray.length() ; i ++){
			try {
				uids[i] = uidarray.getLong(i);
			} catch (JSONException e) {
				PshLogger.logger.error("JSONException failed.");
				PshLogger.logger.error(e.getMessage());
				return generator.toError(parser, 
						ErrorCode.ERROR_CODE, 
						"JSONException error, reason: " + e.getMessage());
			}
		}
		
		if(model.deleteGroupUserByUidArray(gid, uids) != 0){
			return generator.toSuccess(parser, payload); 
		}else{
			return generator.toError(parser, ErrorCode.ERROR_CODE, "删除分组用户失败");
		}
		
		
		
	}
	
	
}
