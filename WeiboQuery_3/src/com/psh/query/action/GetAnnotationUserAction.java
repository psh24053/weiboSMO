package com.psh.query.action;

import java.util.HashMap;
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
import com.psh.query.model.CityModel;
import com.psh.query.model.GetFirstQueryPageNumber;
import com.psh.query.model.GetFirstQueryUser;
import com.psh.query.model.ProvModel;
import com.psh.query.model.QueryTaskModel;
import com.psh.query.model.UserQueryTaskModel;


public class GetAnnotationUserAction extends PshAction{
	
	public GetAnnotationUserAction(){
		
		super.code = 3023;
		super.name = "GetAnnotationUserAction";
		
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
		
		String school = "";
		
		try {
			school = parameter.getString("school");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"school\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		String company = "";
		
		try {
			company = parameter.getString("company");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"company\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		String prov = "0";
		
		try {
			prov = parameter.getString("prov");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"prov\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		String city = "1000";
		
		try {
			city = parameter.getString("city");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"city\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		String age = "all";
		
		try {
			age = parameter.getString("age");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"age\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		String info = "";
		
		try {
			info = parameter.getString("info");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"info\"" );
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
		
		String sex = "";
		
		try {
			sex = parameter.getString("sex");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing:\"sex\"" );
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
		
		if(school != null && !school.equals("")){
			
			condiMap.put("sch", school);
		}
		
		if(company != null && !company.equals("")){
			
			condiMap.put("com", company);
			
		}
		
		if(!prov.equals("0")){
			
			ProvModel provModel = new ProvModel();
			String provName = provModel.getProvNameByID(Integer.parseInt(prov));
			
			if(!city.equals(1000)){
				
//				url += "&region=custom:" + prov + ":" + city;
//				
//				CityModel cityModel = new CityModel();
//				String cityName = cityModel.getProvNameByID(Integer.parseInt(city), Integer.parseInt(prov));
//				
//				query.setQprov(provName);
//				query.setQcity(cityName);
			}else{
//				
//				url += "&region=custom:" + prov + ":1000";
//				query.setQprov(provName);
//				query.setQcity("");
				
			}
			
		}else{
			
//			query.setQprov("");
//			query.setQcity("");
			
		}
		
		if(age != null && !age.equals("") && !age.equals("all")){
			
//			url += "&age=" + age;
//			query.setQage(age);
		}else{
//			query.setQage("");
		}
		
		if(sex != null && !sex.equals("")){
			
//			url += "&gender=" + sex;
//			query.setQsex(sex);
		}else{
//			query.setQsex("");
		}
		
		JSONObject payload = new JSONObject();
		//查找该次搜索的数据源数量
		
		
		JSONArray array = new JSONArray();
		for(int i = 0 ; i < count ; i++){
			
			array.put("非常开心" + i);
		}
		
		try {
			payload.put("list", array);
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
