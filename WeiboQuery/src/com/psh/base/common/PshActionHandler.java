package com.psh.base.common;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.psh.base.util.PshConfigManager;
import com.psh.base.util.PshLogger;

public class PshActionHandler {

	private static PshActionHandler instance = null;
	
	// Hashmap: <Anction code, Class file>
	private HashMap<Integer, String> pshActionList = null;
	
	public static synchronized PshActionHandler getInstance() {
		if ( null == instance) {
			instance = new PshActionHandler();
		}
		return instance;
	}

	private PshActionHandler() {
		pshActionList = new HashMap<Integer, String>();
		loadActionList();
	}

	
	private PshAction getActionInstance(String fullClassName) {

		PshAction actionInstance = null;
		Class<?> actionClass = null;
		
		try {
			actionClass = Class.forName(fullClassName);
		} catch ( ClassNotFoundException e ) {
			PshLogger.logger.error("Can not find the implementation file of class: " + fullClassName);
			PshLogger.logger.error(e.getMessage());
			return actionInstance;
		}
		
		int newActionCode = 0;
		String newActionName = null;
		String newActionDescription = null;
		
		try {
			actionInstance = (PshAction) actionClass.newInstance();
			newActionCode = actionInstance.getActionCode();
			newActionName = actionInstance.getActionName();
			PshLogger.logger.debug("Instance action: " + fullClassName + 
					" action code: " + newActionCode + 
					", action name: " + newActionName + 
					", action description: " + newActionDescription);
		} catch (InstantiationException e) {
			PshLogger.logger.error("Action class： " + fullClassName + " initialization failed.");
			PshLogger.logger.error(e.getMessage());
			return null;
		} catch (IllegalAccessException e) {
			PshLogger.logger.error("Action calss： " + fullClassName + " illegal access error.");
			PshLogger.logger.error(e.getMessage());
			return null;
		}
		catch (Exception e) {
			PshLogger.logger.error("Action calss： " + fullClassName + " unknown error.");
			PshLogger.logger.error(e.getMessage());
			return null;
		}
		
		return actionInstance;
	}
	
	private int validateActionClass(String fullClassName) {
		
		PshAction action = getActionInstance(fullClassName);
		int actionCode = 0;
		
		// TODO: add more validation process
		
		if (null != action) {
			actionCode = action.getActionCode();
			if (actionCode == 0) {
				PshLogger.logger.error("Action class: " + fullClassName + " validation failed.");
			}
		}
		

		return actionCode;
	}

	private boolean loadActionList() {

		PshLogger.logger.debug("Start to load base platformaction  package.");
		int i = 0;
		for (i=0; i<PshConfigManager.platformActionPackage.size(); ++i){
			loadActionPackage(PshConfigManager.platformActionPackage.get(i));
		}
		PshLogger.logger.debug("Load " + i + " base platformaction  package.");
		
		int j = 0;
		PshLogger.logger.debug("Start to load application platformaction  package.");
		for (j=0; j<PshConfigManager.applicationActionPackage.size(); ++j){
			loadActionPackage(PshConfigManager.applicationActionPackage.get(j));
		}
		
		PshLogger.logger.debug("Load " + j + " application platformaction  package.");

		return true;
	}
	
	
	public boolean loadActionPackage (String actionPackage) {
		
		PshLogger.logger.debug("Start to load action package: " + actionPackage);
		// First find the path of action package from the 
		URL packageUrl = PshActionHandler.class.getClassLoader().
				getResource(actionPackage.replace(".", "/"));
		if (packageUrl == null) {
			PshLogger.logger.error("Does not find package: " + actionPackage);
			return false;
		}
		
		String packagePath = packageUrl.getPath();
		// Iterate whole action package folder to find action class and load it
		File packageFolder = new File(packagePath);
		File actionClasses[] = packageFolder.listFiles();
		int counter = 0;
		for (int i=0; i<actionClasses.length; ++i) {
			if (actionClasses[i].getName().toLowerCase().endsWith("class")) {
				String className = actionClasses[i].getName().split(".class")[0];
				String fullClassName = actionPackage + "." + className;
				if (addAction(fullClassName)) {
					++counter;
				}
			}
		}
		
		PshLogger.logger.debug("Load " + counter + " actions from action package: " + actionPackage);
		
		return true;
	}

	public boolean addAction(String fullClassName) {
		
		// Check whether it is valid action class
		int actionCode = validateActionClass(fullClassName);
		
		// Check whether action code is duplicated
		if (pshActionList.containsKey(actionCode)) {
			String existActionClass = pshActionList.get(actionCode);
			PshLogger.logger.error("Action code already exists, actionCode=" 
					+ actionCode + ", existActionClass=" + existActionClass
					+ ", to be added action class:" + fullClassName);
			return false;
		}
		
		if (actionCode != 0) {
			PshLogger.logger.debug("Add new action: " + fullClassName + " into action list.");
			pshActionList.put(actionCode, fullClassName);
			return true;
		}

		PshLogger.logger.debug("Action: " + fullClassName + " is NOT added into action list.");

		return false;
	}
	
	public PshAction getAction(int actionCode) {
		
		PshAction newAction = null;

		if (pshActionList.containsKey(actionCode)) {
			
			String fullClassName = pshActionList.get(actionCode);
			newAction = getActionInstance(fullClassName);

			if ( null != newAction ) {
				PshLogger.logger.debug("Get action with action code: " + newAction.getActionCode() 
					+ ", action name: " + newAction.getActionName());
			}
		}
		else {
			PshLogger.logger.error("Action with action code: " + actionCode + " is not found.");
		}
		
		return newAction;
	}
	
	public ResponseMessageGenerator handleAction(RequestMessageParser parser) {
	
		ResponseMessageGenerator generator = null;
		
		PshAction action = getAction(parser.getActionCode());
		
		if (null != action){
			PshLogger.logger.info("Start to execute: " + action.getActionName());
			generator = action.actionExecute(parser);
		}
		else {
			PshLogger.logger.error("Dose not find action: " + parser.getActionCode());
			generator = new ResponseMessageGenerator().toError(parser, ErrorCode.ERROR_CODE);
		}
		
		return generator;
	}
	
	public LinkedList<PshAction> getActionList(){
		
		LinkedList<PshAction> actionList = new LinkedList<PshAction>();
		
		Iterator<Integer> iter = pshActionList.keySet().iterator();
		
		while (iter.hasNext()) {
			int actionCode = iter.next();
			PshAction action = getAction(actionCode);
			int i=0;
			for (;i<actionList.size(); ++i) {
				if (action.getActionCode() <= actionList.get(i).getActionCode()) {
					break;
				}
			}
			actionList.add(i, action);
		}

		return actionList;
	}
	
}
