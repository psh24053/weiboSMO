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
import com.psh.query.bean.QueryTaskBean;
import com.psh.query.model.CategoryModel;
import com.psh.query.model.CityModel;
import com.psh.query.model.GetFirstQueryPageNumber;
import com.psh.query.model.GetFirstQueryUser;
import com.psh.query.model.ProvModel;
import com.psh.query.model.QueryTaskModel;
import com.psh.query.model.UserQueryTaskModel;


public class GetCategoryListAction extends PshAction{
	
	public GetCategoryListAction(){
		
		super.code = 3003;
		super.name = "GetCategoryList";
		
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
		
		
	
		JSONObject payload = new JSONObject();
		
		CategoryModel model = new CategoryModel();
		List<CategoryBean> data = model.getCategoryList();
		
		JSONArray list = new JSONArray();
		
		
		
		try {
			for(int i = 0 ; i < data.size() ; i ++){
				JSONObject item = new JSONObject();
				CategoryBean bean = data.get(i);
				
				item.put("cid", bean.getCid());
				item.put("name", bean.getName());
				item.put("desc", bean.getDesc());
				
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
