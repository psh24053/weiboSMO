package com.psh.query.action;

import com.psh.base.common.PshAction;
import com.psh.base.common.RequestMessageParser;
import com.psh.base.common.ResponseMessageGenerator;
import com.psh.base.common.ErrorCode;
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


public class AddCategoryAction extends PshAction{
	
	public AddCategoryAction(){
		
		super.code = 3001;
		super.name = "AddCategory";
		
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
		String name = "";
		
		try {
			name = parameter.getString("name");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"name\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		String desc = "";
		
		try {
			desc = parameter.getString("desc");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"desc\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
	
		CategoryBean category = new CategoryBean();
		category.setName(name);
		category.setDesc(desc);
		
		CategoryModel model = new CategoryModel();
		boolean result = model.addCategory(category);
		
		JSONObject payload = new JSONObject();
		
		if(result){
			return generator.toSuccess(parser, payload);
		}
		
		return generator.toError(parser, ErrorCode.ERROR_CODE, "增加分类失败");
		
	}
	
	
}
