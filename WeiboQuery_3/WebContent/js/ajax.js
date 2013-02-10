var weibo = {
	
	Action_3003_GetCategoryList:function(Success, Error, Before){
		
		new Ajax({
			ActionCode:3003,
			Success:Success,
			Error:Error
		}).send(Before);
		
	},
	Action_3004_GetGroupList:function(cid, Success, Error, Before){
		
		new Ajax({
			ActionCode:3004,
			prm:{
				cid: cid
			},
			Success:Success,
			Error:Error
		}).send(Before);
		
	},
	Action_3002_AddGroup:function(cid, name, Success, Error, Before){
		
		new Ajax({
			ActionCode:3002,
			prm:{
				cid: parseInt(cid),
				name: name
			},
			Success:Success,
			Error:Error
		}).send(Before);
		
	},
	Action_3001_AddCategory:function(name, desc, Success, Error, Before){
		
		new Ajax({
			ActionCode:3001,
			prm:{
				desc: desc,
				name: name
			},
			Success:Success,
			Error:Error
		}).send(Before);
		
	},
	Action_3010_SetGroupUser:function(gid, count, Success, Error, Before){
		
		new Ajax({
			ActionCode:3010,
			prm:{
				gid: gid,
				count: count
			},
			Success:Success,
			Error:Error
		}).send(Before);
		
	},
	Action_3018_deleteGroup:function(gid, Success, Error, Before){
		
		new Ajax({
			ActionCode:3018,
			prm:{
				gid: gid
			},
			Success:Success,
			Error:Error
		}).send(Before);
		
	}
	
		
		
		
		
};


/**
 * Ajax工具类
 */
function Ajax(Settings){
	
	this.Servlet = "Main";
	this.ActionCode = " ";
	this.Method = "post";
	this.prm = {};
	this.Success = function(){};
	this.Error = function(){};
	this.Complete = function(){};
	this.dataType = "json";
	this.data = null;
	
	if(Settings){
		for(var key in Settings){
			if(this[key]){
				this[key] = Settings[key];
			}
		}
	}
	for(var key in this.prm){
		if(this.prm[key] == 'null'){
			delete this.prm.key;
		}
	}
	this.RequestJson = {
		cod: this.ActionCode,
		prm: this.prm
	};
	
	var p_this = this;
	
	this.send = function(run){
		if(run != undefined){
			run();
		}
		if(this.Method == "post"){
			$.post(this.Servlet,
			JSON.stringify(this.RequestJson),
			function(data){
				this.data = data;

				p_this.Success(data);
				
			},this.dataType).error(this.Error).complete(this.Complete);
		}
		if(this.Method == "get"){
			$.get(this.Servlet,{
				json:JSON.stringify(this.RequestJson)
			},function(data){
				this.data = data;

				p_this.Success(data);

			},this.dataType).error(this.Error).complete(this.Complete);
		}
	};
	this.set = function(key,value){
		if(this[key]){
			this[key] = value;
		}
	};
	
	
}

