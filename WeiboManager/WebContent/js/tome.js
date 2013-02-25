/**
 * 初始化转发消息的toolbar
 */
function initTome_Toolbar(){
	
	var toolbar = $('#t_tabs_5_table');
	
	toolbar.css('height','30px');
	
	var currentGroup = $('<span></span>').addClass('currentGroup').css('margin-left','5px');
	
	if(!localStorage.TomeGroup || localStorage.TomeGroup == 'none'){
		currentGroup.text('请选择分组');
	}else{
		currentGroup.text('当前分组：'+localStorage.TomeGroupName);
		currentGroup.attr('gid',localStorage.TomeGroupGid);
	}
	toolbar.append(currentGroup);
	
	
	var selectGroup = $('<button></button>').text('选择分组').button().attr('title','选择一个分组，对其中的账号进行更新资料操作');
	selectGroup.click(onClick_selectGroup_Tome);
	toolbar.append(selectGroup);

	var loadAccount = $('<button></button>').text('加载账号').button().attr('title','将当前分组中所有账号读取到表格中');
	loadAccount.click(onClick_loadAccount_Tome);
	toolbar.append(loadAccount);
	
	var sendSource = $('<button></button>').text('检查新消息').button().attr('title','从新浪服务器上读取回复数据到本地');
	sendSource.click(onClick_Refresh_Tome);
	toolbar.append(sendSource);
	
	
}
/**
 * 刷新状态
 */
function onClick_Refresh_Tome(){
	if(localStorage.TomeGroup != 'run'){
		alert('请选选择分组!');
		return;
	}
	var rowData = $('#tabs_5_table').jqGrid('getRowData');
	if(rowData.length == 0){
		alert('没有加载账号信息或该分组没有账号数据！');
		return;
	}
	
	$(this).parent().find('button').button('disable');
	var p_this = $(this);
	var size = 0;
	Refresh(size, rowData, p_this);
}
/**
 * 更新界面结果
 * @param uid
 * @param data
 */
function updateRowRefreshResult(uid, data){
	//更新lastmid
	//更新row
	var rowItem = $('#tabs_5_table').jqGrid('getRowData',uid);
	var newmsg = "";
	var lastmid = "";
	if(data.length == 0){
		newmsg = '<a href="#" onclick="onClick_showreply_tome(\''+uid+'\')" title="查看详情">(查看历史记录)</a>';
	}else{
		var last = data[data.length - 1];
		newmsg = '<a href="#" onclick="onClick_showreply_tome(\''+uid+'\')" title="查看详情">(<font color="red">'+data.length+'条新消息</font>)'+last.con+'</a>';
		lastmid = last.mid;
	}
	
	$('#tabs_5_table').jqGrid('setRowData',uid, {
		'lastmid': lastmid,
		'newmsg': newmsg,
		'cache': JSON.stringify(data)
	});
	
	
}
/**
 * 查看详情的方法
 * @param uid
 */
function onClick_showreply_tome(uid){
	$('#tabs_5_table').jqGrid('setRowData',uid, {
		'newmsg': '<a href="#" onclick="onClick_showreply_tome(\''+uid+'\')" title="查看详情">(查看历史记录)</a>'
	});
	$('.dialog_showreply').dialog({
		modal: true,
		title: '查看回复详情',
		resizable: false,
		width: $(document).width() * 0.7,
	    height: $(document).height() * 0.7,
	    create: function(){
	    	$('#dialog_showreply_table').jqGrid({
	    		datatype: "local",
	    		height: $(document).height() * 0.5,
	    		colNames:['操作','mid','类型','发表时间','作者uid','昵称','内容','图片Url','源uid','源昵称','源内容','源时间','源mid'],
	    	   	colModel:[
	   	            {name:'actions',index:'actions', width:30, align:'center', sortable:false},
	    	   		{name:'mid',index:'mid', width:30, align:'center', sortable:false},
	    	   		{name:'type',index:'type', width:30, align:'center', sortable:false},
	    	   		{name:'time',index:'time', width:30, align:'center', sortable:false},
	    	   		{name:'uid',index:'uid', width:30, align:'center', sortable:false},
	    	   		{name:'nck',index:'nck', width:30, align:'center', sortable:false},
	    	   		{name:'con',index:'con', width:30, align:'center', sortable:false},
	    	   		{name:'image',index:'image', width:30, align:'center', sortable:false},
	    	   		{name:'ouid',index:'ouid', width:30, align:'center', sortable:false},
	    	   		{name:'onck',index:'onck', width:30, align:'center', sortable:false},
	    	   		{name:'ocon',index:'ocon', width:30, align:'center', sortable:false},
	    	   		{name:'otime',index:'otime', width:30, align:'center', sortable:false},
	    	   		{name:'omid',index:'omid', width:30, align:'center', sortable:false}
	    	   	],
	    	   	rownumbers:true,
	       		rowNum: 50,
	       	   	pager: "#dialog_showreply_pager",
	       	 	viewrecords: true,
	       	   	width: $(document).width() * 0.67,
	       	 	loadComplete: function(){
	       	 		
	       	 	}
	    	});
	    	
	    	
	    },
	    open: function(){
	    	var wait = new weibo.WaitAlert('正在加载...');
	    	wait.show();
	    	weibo.Action_3032_GetReplyListByUid(uid,function(data){
	    		if(data.res){
	    			var list = data.pld.list;
	    			$('#dialog_showreply_table').jqGrid('clearGridData');
	    			for(var i = 0 ; i < list.length ; i ++){
	    				if(list[i].mark == 0){
	    					if(list[i].type.indexOf('@我的微博') != -1){
	    						list[i].actions = '<button onclick="onClick_forwardMsg_tome(\''+list[i].mid+'\','+uid+')">转发</button>';
	    						list[i].actions += '<button onclick="onClick_commentMsg_tome(\''+list[i].mid+'\','+uid+')">评论</button>';
	    					}else{
	    						list[i].actions = '<button onclick="onClick_commentMsg_tome(\''+list[i].mid+'\','+uid+')">评论</button>';
	    					}
	    					
	    				}else{
	    					list[i].actions = '已回复';
	    				}
	    			}
	    			
	    			$('#dialog_showreply_table').jqGrid('addRowData','mid',data.pld.list);
	    		}
	    		wait.close();
	    	},function(err){
	    		wait.close();
	    	});
	    }
	    
	});
	
	
	
	
}
/**
 * 转发目标msg
 * @param mid
 * @param uid
 */
function onClick_forwardMsg_tome(mid, uid){
	var rowItem = $('#dialog_showreply_table').jqGrid('getRowData',mid);
	
	$('.dialog_forwardMsg').dialog({
		modal: true,
		title: '转发目标msg',
		resizable: false,
		width: $(document).width() * 0.5,
	    height: $(document).height() * 0.65,
	    open: function(){
	    	$('.dialog_forwardMsg').find('.target').text('('+rowItem.type+') '+rowItem.con);
	    	$('.dialog_forwardMsg').find('.content');
	    },
	    buttons: {
	    	'转发': function(){
	    		var val = $('.dialog_forwardMsg').find('.content').val();
	    		if(val && val.length > 140){
	    			alert('转发内容不能大于140个字，转发内容可以为空');
	    			return;
	    		}
	    		var wait = new weibo.WaitAlert('正在转发...');
	    		wait.show();
	    		weibo.Action_3026_ForwardMessage({
	    			uid: uid,
	    			mid: mid,
	    			content: !val ? '':val
	    		},function(data){
	    			wait.close();
	    			if(data.res){
	    				alert('转发成功！');
	    				$('#dialog_showreply_table').jqGrid('setRowData',mid,{actions:'已回复'});
	    				weibo.Action_3033_UpdateMidMark({
	    					uid: uid,
	    					mid: mid
	    				});
	    				$('.dialog_forwardMsg').dialog('close');
	    			}else{
	    				alert('转发失败！');
	    			}
	    		},function(err){
	    			wait.close();
	    			alert('转发失败！');
	    		});
	    	}
	    }
	});
	
	
}
/**
 * 评论目标Msg
 * @param mid
 * @param uid
 */
function onClick_commentMsg_tome(mid, uid){
	var rowItem = $('#dialog_showreply_table').jqGrid('getRowData',mid);
	
	$('.dialog_commentMsg').dialog({
		modal: true,
		title: '评论目标msg',
		resizable: false,
		width: $(document).width() * 0.5,
	    height: $(document).height() * 0.65,
	    open: function(){
	    	$('.dialog_commentMsg').find('.target').text('('+rowItem.type+') '+rowItem.con);
	    	$('.dialog_commentMsg').find('.content');
	    },
	    buttons: {
	    	'评论': function(){
	    		var val = $('.dialog_forwardMsg').find('.content').val();
	    		if(!val && val.length > 140){
	    			alert('评论内容不能大于140个字,评论内容不能为空');
	    			return;
	    		}
	    		var wait = new weibo.WaitAlert('正在转发...');
	    		wait.show();
	    		weibo.Action_3034_CommentMsg({
	    			uid: uid,
	    			mid: mid,
	    			con: !val ? '':val
	    		},function(data){
	    			wait.close();
	    			if(data.res){
	    				alert('评论成功！');
	    				$('#dialog_showreply_table').jqGrid('setRowData',mid,{actions:'已回复'});
	    				weibo.Action_3033_UpdateMidMark({
	    					uid: uid,
	    					mid: mid
	    				});
	    				$('.dialog_commentMsg').dialog('close');
	    			}else{
	    				alert('评论失败！');
	    			}
	    		},function(err){
	    			wait.close();
	    			alert('评论失败！');
	    		});
	    		
	    	}
	    }
	});
}
/**
 * 执行刷新的递归
 */
function Refresh(size, rowData, p_this){
	var i = size;
	var item = rowData[i];
	$('#tabs_5_table').jqGrid('setRowData',item.uid, {'status':'正在操作...'});
	weibo.Action_3031_RefreshByLastMid(
		parseInt(item.uid),
		!item.lastmid ? '':item.lastmid
	, function(data){
		if(data.res){
			$('#tabs_5_table').jqGrid('setRowData',item.uid, {
				'status': '刷新成功'
			});
			updateRowRefreshResult(item.uid, data.pld.list);
			
		}else{
			$('#tabs_5_table').jqGrid('setRowData',item.uid, {'status':'刷新失败'});
		}
		if(rowData.length == size+1){
			p_this.parent().find('button').button('enable');
			alert('刷新命令执行完毕！');
		}else{
			size++;
			Refresh(size, rowData, p_this);
		}
	},function(){
		$('#tabs_5_table').jqGrid('setRowData',item.uid, {'status':'刷新失败'});
		if(rowData.length == size+1){
			p_this.parent().find('button').button('enable');
			alert('刷新命令执行完毕！');
		}else{
			size++;
			Refresh(size, rowData, p_this);
		}
	});
}


/**
 * 执行关注操作
 */
function onClick_execute_Tome(){
	if(localStorage.TomeGroup != 'run'){
		alert('请选选择分组!');
		return;
	}
	var rowData = $('#tabs_5_table').jqGrid('getRowData');
	if(rowData.length == 0){
		alert('没有加载账号信息或该分组没有账号数据！');
		return;
	}
	var uids = JSON.parse(localStorage.TomeUidArray);
	if(!uids || uids.length == 0){
		alert('请选择关注目标');
		return;
	}
	
	
	$(this).parent().find('button').button('disable');
	var p_this = $(this);
	var size = 0;
	var u_index = 0;
	var uid = uids[u_index];
	
	
	$(this).find('span').text('正在关注 uid: '+uid);
	Tome(size, rowData, p_this, uids, u_index, uid);
}
/**
 * 发送微博的递归
 */
function Tome(size, rowData, p_this, uids, u_index, uid){
	var i = size;
	var item = rowData[i];
	$('#tabs_5_table').jqGrid('setRowData',item.uid, {'status':'正在操作...'});
	weibo.Action_3027_TomeUser({
		myuid: item.uid,
		attuid: uid
	}, function(data){
		if(data.res){
			$('#tabs_5_table').jqGrid('setRowData',item.uid, {
				'status': '关注成功'
			});
			
		}else{
			$('#tabs_5_table').jqGrid('setRowData',item.uid, {'status':'关注失败'});
		}
		if(rowData.length == size+1){
			if(uids.length == u_index + 1){
				p_this.parent().find('button').button('enable');
				p_this.find('span').text('开始关注');
				alert('关注命令执行完毕！');
			}else{
				size = 0;
				u_index ++;
				uid = uids[u_index];
				p_this.find('span').text('正在关注 uid: '+uid);
				Tome(size, rowData, p_this, uids,u_index, uid);
			}
		}else{
			size++;
			Tome(size, rowData, p_this, uids, u_index, uid);
		}
	},function(){
		$('#tabs_5_table').jqGrid('setRowData',item.uid, {'status':'发送失败'});
		if(rowData.length == size+1){
			if(uids.length == u_index + 1){
				p_this.parent().find('button').button('enable');
				p_this.find('span').text('开始关注');
				alert('关注命令执行完毕！');
			}else{
				size = 0;
				u_index ++;
				uid = uids[u_index];
				p_this.find('span').text('正在关注 uid: '+uid);
				Tome(size, rowData, p_this, uids, u_index, uid);
			}
		}else{
			size++;
			Tome(size, rowData, p_this, uids, u_index, uid);
		}
	});
} 
/**
 * 获取关注目标
 */
function onClick_getTargetTome_Tome(){
	if(localStorage.TomeGroup != 'run'){
		alert('请选选择分组!');
		return;
	}
	var rowData = $('#tabs_5_table').jqGrid('getRowData');
	if(rowData.length == 0){
		alert('没有加载账号信息或该分组没有账号数据！');
		return;
	}
	$('.dialog_getTomesource').dialog({
		modal: true,
		title: '获取关注目标',
		resizable: false,
		width: $(document).width() * 0.5,
	    height: $(document).height() * 0.85,
	    open: function(){
	    },
	    create: function(){
	    	$('#dialog_getTomesource_table').jqGrid({
	    		datatype: "local",
	    		height: 350,
	    		colNames:['uid','操作'],
	    	   	colModel:[
	    	   		{name:'uid',index:'uid', width:30, align:'center', sortable:false},
	    	   		{name:'actions',index:'actions', width:30, align:'center', sortable:false}
	    	   	],
	    	   	rownumbers:true,
	       		rowNum: 50,
	       	   	pager: "#dialog_getTomesource_pager",
	       	 	viewrecords: true,
	       	   	width: $(document).width() * 0.45,
	       	 	loadComplete: function(){
	       	 		
	       	 	}
	    	});
	    	$('.dialog_getTomesource_search .search_sina').click(function(){
	    		var uid = $(this).parent().find('.uid').val();
	    		if(!uid || uid < 100000){
	    			alert('没有输入uid或uid格式不正确');
	    			return;
	    		}
	    		var rowData = $('#dialog_getTomesource_table').jqGrid('getRowData');
	    		for(var i = 0 ; i < rowData.length ; i ++){
	    			var rowItem = rowData[i];
	    			if(rowItem.uid == uid){
	    				alert('该uid已存在');
		    			return;
	    			}
	    		}
	    		
	    		var wait = new weibo.WaitAlert('正在搜索...');
	    		wait.show();
	    		weibo.Action_3029_CheckUid(uid, function(data){
	    			if(data.res){
	    				alert('uid: '+uid+' 状态正常！');
	    				var act = '<a href="#" onclick="onClick_deleteSearchUidByTable(\''+uid+'\')">删除</a>';
	    				$('#dialog_getTomesource_table').jqGrid('addRowData','uid',[{uid: uid,actions: act}]);
	    			}else{
	    				alert('uid: '+uid+' 账号不存在或搜索错误！');
	    			}
	    			wait.close();
	    		},function(err){
	    			alert('uid: '+uid+' 账号不存在或搜索错误！');
	    			wait.close();
	    		});
	    		
	    	});
	    	$('.dialog_getTomesource_search .search_local').click(function(){
	    		$('.dialog_Tomesearchtargetuser').dialog({
	    			modal: true,
	    			title: '本地库搜索',
	    			resizable: false,
	    			width: $(document).width() * 0.4,
	    		    height: $(document).height() * 0.5,
	    		    buttons: {
	    		    	'搜索': function(){
	    		    		var dom = $('.dialog_Tomesearchtargetuser');
	    		    		var nickname = !dom.find('.nickname').val() ? '':dom.find('.nickname').val();
	    		    		var tags = !dom.find('.tags').val() ? '':dom.find('.tags').val();
	    		    		var fol = !dom.find('.fol').val() ? '':dom.find('.fol').val();
	    		    		var fans = !dom.find('.fans').val() ? '':dom.find('.fans').val();
	    		    		var count = dom.find('.count').val();
	    		    		
	    		    		var wait = new weibo.WaitAlert('正在搜索...');
	    		    		wait.show();
	    		    		
	    		    		weibo.Action_3030_GetLocalUser({
	    		    			nickName: nickname,
	    		    			tag: tags,
	    						school: '',
	    						company: '',
	    						prov: '',
	    						city: '',
	    						age: '',
	    						sex: '',
	    						info: '',
	    						fol: fol,
	    						fans: fans,
	    						count: count
	    		    		},function(data){
	    		    			if(data.res){
	    		    				var list = data.pld.list;
	    		    				var s_count = data.pld.count;
	    		    				wait.close();
	    		    				alert('搜索到 '+s_count+' 条目标用户');
	    		    				$('.dialog_Tomesearchtargetuser').dialog('close');
	    		    				if(s_count == 0){
	    		    					return;
	    		    				}
	    		    				for(var i = 0 ; i < list.length ; i ++){
	    		    					var item = list[i];
	    		    					var act = '<a href="#" onclick="onClick_deleteSearchUidByTable(\''+item.uid+'\')">删除</a>';
	    		    					item.actions = act;
	    		    					
	    		    				}
	    		    				$('#dialog_getTomesource_table').jqGrid('addRowData','uid',list);
	    		    				
	    		    			}
	    		    			
	    		    			
	    		    		},function(err){
	    		    			wait.close();
	    		    			$('.dialog_Tomesearchtargetuser').dialog('close');
	    		    		});
	    		    	}
	    		    }
	    		});
	    	});
	    	
	    },
	    buttons:{
	    	'确定':function(){
	    		var rowData = $('#dialog_getTomesource_table').jqGrid('getRowData');
	    		$('.dialog_getTomesource').dialog('close');
	    		var uids = [];
	    		var target = "";
	    		for(var i = 0 ; i < rowData.length ; i ++){
	    			var rowItem = rowData[i];
	    			uids.push(rowItem.uid);
	    			if(i == rowData.length - 1){
	    				target += rowItem.uid;
	    			}else{
	    				target += rowItem.uid+",";
	    			}
	    		}
	    		localStorage.TomeUidArray = JSON.stringify(uids);
	    		var rowData_Tab4 = $('#tabs_5_table').jqGrid('getRowData');
	    		for(var i = 0 ; i < rowData_Tab4.length ; i ++){
	    			var rowItem = rowData_Tab4[i];
	    			$('#tabs_5_table').jqGrid('setRowData',rowItem.uid,{target: target});
	    		}
	    		
	    		
	    	}
	    }
	});
	
}
/**
 * 删除搜索出来的uid
 * @param uid
 */
function onClick_deleteSearchUidByTable(uid){
	$('#dialog_getTomesource_table').jqGrid('delRowData',uid);
//	var rowData = $('#dialog_getTomesource_table').jqGrid('getRowData');
//	for(var i = 0 ; i < rowData.length ; i ++){
//		if(rowData[i].uid == uid){
//			$('#dialog_getTomesource_table').jqGrid('delRowData',i);
//		}
//	}
	
}
/**
 * 加载账号的事件
 */
function onClick_loadAccount_Tome(){
	if(localStorage.TomeGroup != 'run'){
		alert('请选选择分组!');
		return;
	}
	
	var wait = new weibo.WaitAlert('正在加载账号信息...');
	wait.show();
	weibo.Action_3011_GetGroupUserList(localStorage.TomeGroupGid, function(data){
		if(data.res){
			var list = data.pld.list;
			localStorage['GroupAccount_'+localStorage.TomeGroupGid] = JSON.stringify(list);
			for(var i = 0 ; i < list.length ; i ++){
				var item = list[i];
				item.description = item.info;
			}
			$('#tabs_5_table').jqGrid('clearGridData');
			$('#tabs_5_table').jqGrid('addRowData', 'uid', list);
			wait.close();
			
			
		}
		
		console.debug(data);
	});
	
}
/**
 * 选择分组的事件
 */
function onClick_selectGroup_Tome(){
	
	$('.dialog_selectgroup').dialog({
		modal: true,
		title: '选择分组',
		resizable: false,
		width: $(document).width() * 0.2,
	    height: $(document).height() * 0.3,
	    create: function(event, ui){
	    	var category = $('.dialog_selectgroup .category_name');
	    	var group = $('.dialog_selectgroup .group_name');
	    	
	    	category.find('option').remove();
	    	group.find('option').remove();
	    	
	    	for(var i = 0 ; i < AjaxData.categoryList.length ; i ++){
	    		var item = AjaxData.categoryList[i];
	    		var option = $('<option></option>').val(item.cid).text(item.name);
	    		category.append(option);
	    	}
	    	
	    	category.change(function(){
	    		var value = $(this).val();
	    		var wait = new weibo.WaitAlert('正在加载分组信息...');
	    		wait.show();
	    		weibo.Action_3004_GetGroupList(value, function(data){
	    			
	    			if(data.res){
	    				var list = data.pld.list;
	    				group.find('option').remove();
	    				for(var j = 0 ; j < list.length ; j ++){
	    					var item = list[j];
	    					if(item.status != '空闲'){
	    						continue;
	    					}
	    					var option = $('<option></option>').val(item.gid).text(item.name);
	    					group.append(option);
	    				}
	    				
	    				
	    			}
	    			
	    			
	    			wait.close();
	    		},function(){
	    			wait.close();
	    		});
	    		
	    	});
	    	category.change();
	    	
	    },
	    close: function(event, ui){
	    },
	    buttons:{
	    	'确认': function(){
	    		var category = $('.dialog_selectgroup .category_name');
		    	var group = $('.dialog_selectgroup .group_name');
		    	
		    	if(group.val() == null || group.val() == undefined){
		    		alert('没有选择分组');
		    		return;
		    	}
		    	
	    		var cgroup = $('#t_tabs_5_table .currentGroup');
	    		
	    		cgroup.text('当前分组：'+group.find('option:checked').text()).attr('gid',group.val());
	    		localStorage.TomeGroup = 'run';
	    		localStorage.TomeGroupName = group.find('option:checked').text();
	    		localStorage.TomeGroupGid = group.val();
	    		
	    		$(this).dialog('close');
	    	}
	    }
	    
	});
	
	
}
