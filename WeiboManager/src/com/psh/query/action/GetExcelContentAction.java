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
import com.psh.query.bean.ExcelBean;
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
import com.psh.query.service.ExcelService;
import com.psh.query.service.InfoService;


public class GetExcelContentAction extends PshAction{
	
	public GetExcelContentAction(){
		
		super.code = 3020;
		super.name = "GetExcelContentAction";
		
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
		String idx = null;
		
		try {
			idx = parameter.getString("idx");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"idx\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
	
		JSONObject payload = new JSONObject();
		
		ExcelService excelService = ExcelService.excelData.get(idx);
		List<ExcelBean> data = excelService.parseExcel();
		JSONArray list = new JSONArray();
		
		if(data == null){
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE, 
					"error, reason: 解析Excel失败");
		}
		
		
		try {
			for(int i = 0 ; i < data.size() ; i ++){
				ExcelBean bean = data.get(i);
				JSONObject obj = new JSONObject();
				obj.put("nck", bean.getScreen_name());
				obj.put("prov", bean.getProvince());
				obj.put("city", bean.getCity());
				obj.put("gender", bean.getGender());
				obj.put("birthday", bean.getBirthday_value());
				obj.put("info", bean.getDescription());
				obj.put("tags", bean.getTags());
				list.put(obj);
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
