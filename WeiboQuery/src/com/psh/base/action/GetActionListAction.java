package com.psh.base.action;

import java.util.Iterator;
import java.util.LinkedList;

import com.psh.base.common.RequestMessageParser;
import com.psh.base.common.ResponseMessageGenerator;
import com.psh.base.common.PshActionHandler;
import com.psh.base.common.PshAction;
import com.psh.base.common.ErrorCode;
import com.psh.base.json.JSONArray;
import com.psh.base.json.JSONException;
import com.psh.base.json.JSONObject;
import com.psh.base.util.PshLogger;

public class GetActionListAction extends PshAction {

	public GetActionListAction() {
		super.code = 1001;
		super.name = "GetActionListAction";
	}

	@Override
	public ResponseMessageGenerator handleAction(RequestMessageParser parser) {
		
		PshActionHandler handler = PshActionHandler.getInstance();

		LinkedList<PshAction> actionList = handler.getActionList();
		
		JSONArray actionArray = new JSONArray();
		Iterator<PshAction> iter = actionList.iterator();
		
		while (iter.hasNext()) {
			PshAction actionObject = iter.next();
			JSONArray action = new JSONArray();
			action.put(actionObject.getActionCode());
			action.put(actionObject.getActionName());
			actionArray.put(action);
		}
		
		ResponseMessageGenerator generator = new ResponseMessageGenerator();
		JSONObject payload = new JSONObject();
		try {
			payload.put("ActionList", actionArray);
		} catch (JSONException e) {
			PshLogger.logger.error("Generate JSON response failed.");
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE, 
					"Generate JSON response error, reason: " + e.getMessage());
		}
		
		return generator.toSuccess(parser, payload);
	}

}
