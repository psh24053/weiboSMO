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
import com.psh.query.bean.CategoryBean;
import com.psh.query.bean.MsgBean;
import com.psh.query.bean.QueryTaskBean;
import com.psh.query.model.CategoryModel;
import com.psh.query.model.CityModel;
import com.psh.query.model.GetFirstQueryPageNumber;
import com.psh.query.model.GetFirstQueryUser;
import com.psh.query.model.ProvModel;
import com.psh.query.model.QueryTaskModel;
import com.psh.query.model.ReplyModel;
import com.psh.query.model.UserQueryTaskModel;


public class UpdateMidMarkAction extends PshAction{
	
	public UpdateMidMarkAction(){
		
		super.code = 3033;
		super.name = "UpdateMidMarkAction";
		
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
		
	
		JSONObject payload = new JSONObject();
		ReplyModel model = new ReplyModel(); 
		
		if(model.updateMark(mid, uid)){
			return generator.toSuccess(parser, payload);
		}else{
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE, 
					"error, reason: 更新mark失败");
		}
		
		
		
	}
	
	
}
