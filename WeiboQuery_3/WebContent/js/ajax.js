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
		
	},
	Action_3011_GetGroupUserList:function(gid, Success, Error, Before){
		
		new Ajax({
			ActionCode:3011,
			prm:{
				gid: gid
			},
			Success:Success,
			Error:Error
		}).send(Before);
		
	},
	Action_3019_GetAccountInfoFromSina:function(uid, Success, Error, Before){
		
		new Ajax({
			ActionCode:3019,
			prm:{
				uid: uid
			},
			Success:Success,
			Error:Error
		}).send(Before);
		
	},
	Action_3020_GetExcelContent:function(idx, Success, Error, Before){
		
		new Ajax({
			ActionCode:3020,
			prm:{
				idx: idx
			},
			Success:Success,
			Error:Error
		}).send(Before);
		
	},
	Action_3021_UpdateInfo:function(opts, Success, Error, Before){
		
		new Ajax({
			ActionCode:3021,
			prm:{
				uid: opts.uid,
				nck: opts.nck,
				prov: opts.prov,
				city: opts.city,
				gender: opts.gender,
				birthday: opts.birthday,
				info: opts.info,
				tags: opts.tags
			},
			Success:Success,
			Error:Error
		}).send(Before);
		
	},
	Action_3022_AccountSynchronization:function(Success, Error, Before){
		
		new Ajax({
			ActionCode:3022,
			Success:Success,
			Error:Error
		}).send(Before);
		
	},
	Action_3023_SendWeibo:function(uid, content, Success, Error, Before){
		
		new Ajax({
			ActionCode:3023,
			Success:Success,
			prm:{
				uid: uid,
				content: content
			},
			Error:Error
		}).send(Before);
		
	},
	Action_3009_GetTextTypeList:function(Success, Error, Before){
		
		new Ajax({
			ActionCode:3009,
			Success:Success,
			Error:Error
		}).send(Before);
		
	},
	Action_3008_GetTextList:function(ttid, count, Success, Error, Before){
		
		new Ajax({
			ActionCode:3008,
			Success:Success,
			prm:{
				ttid: ttid,
				count: count
			},
			Error:Error
		}).send(Before);
		
	},
	Action_3024_SearchTargetUser:function(opts, Success, Error, Before){
		
		new Ajax({
			ActionCode:3024,
			prm:{
				nickName: opts.nickName,
				tag: opts.tag,
				school: opts.school,
				company: opts.company,
				prov: opts.prov,
				city: opts.city,
				age: opts.age,
				sex: opts.sex,
				info: opts.info,
				fol: opts.fol,
				fans: opts.fans,
				count: opts.count
			},
			Success:Success,
			Error:Error
		}).send(Before);
		
	},
	Action_3025_AddUserByGroup:function(opts, Success, Error, Before){
		
		new Ajax({
			ActionCode:3025,
			Success:Success,
			prm:{
				uid: opts.uid,
				email: opts.email,
				password: opts.password,
				gid: opts.gid
			},
			Error:Error
		}).send(Before);
		
	},
	Action_3026_ForwardMessage:function(opts, Success, Error, Before){
		
		new Ajax({
			ActionCode:3025,
			Success:Success,
			prm:{
				uid: opts.uid,
				mid: opts.mid,
				content: opts.content
			},
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

