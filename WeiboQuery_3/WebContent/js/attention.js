/**
 * 初始化转发消息的toolbar
 */
function initAttention_Toolbar(){
	
	var toolbar = $('#t_tabs_4_table');
	
	toolbar.css('height','30px');
	
	var currentGroup = $('<span></span>').addClass('currentGroup').css('margin-left','5px');
	
	if(!localStorage.AttentionGroup || localStorage.AttentionGroup == 'none'){
		currentGroup.text('请选择分组');
	}else{
		currentGroup.text('当前分组：'+localStorage.AttentionGroupName);
		currentGroup.attr('gid',localStorage.AttentionGroupGid);
	}
	toolbar.append(currentGroup);
	
	
	var selectGroup = $('<button></button>').text('选择分组').button().attr('title','选择一个分组，对其中的账号进行更新资料操作');
	selectGroup.click(onClick_selectGroup_Attention);
	toolbar.append(selectGroup);

	var loadAccount = $('<button></button>').text('加载账号').button().attr('title','将当前分组中所有账号读取到表格中');
	loadAccount.click(onClick_loadAccount_Attention);
	toolbar.append(loadAccount);
	
	var sendSource = $('<button></button>').text('获取关注目标').button().attr('title','从新浪搜索关注目标并读取到表格中');
	sendSource.click(onClick_getTargetAttention_Attention);
	toolbar.append(sendSource);
	
	var executeContent = $('<button></button>').text('开始关注').button().attr('title','开始关注别人');
	executeContent.click(onClick_execute_Attention);
	toolbar.append(executeContent);
	
}
/**
 * 执行关注操作
 */
function onClick_execute_Attention(){
	if(localStorage.AttentionGroup != 'run'){
		alert('请选选择分组!');
		return;
	}
	var rowData = $('#tabs_4_table').jqGrid('getRowData');
	if(rowData.length == 0){
		alert('没有加载账号信息或该分组没有账号数据！');
		return;
	}
	var uids = JSON.parse(localStorage.AttentionUidArray);
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
	Attention(size, rowData, p_this, uids, u_index, uid);
}
/**
 * 发送微博的递归
 */
function Attention(size, rowData, p_this, uids, u_index, uid){
	var i = size;
	var item = rowData[i];
	$('#tabs_4_table').jqGrid('setRowData',item.uid, {'status':'正在操作...'});
	weibo.Action_3027_AttentionUser({
		myuid: item.uid,
		attuid: uid
	}, function(data){
		if(data.res){
			$('#tabs_4_table').jqGrid('setRowData',item.uid, {
				'status': '关注成功'
			});
			
		}else{
			$('#tabs_4_table').jqGrid('setRowData',item.uid, {'status':'关注失败'});
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
				Attention(size, rowData, p_this, uids,u_index, uid);
			}
		}else{
			size++;
			Attention(size, rowData, p_this, uids, u_index, uid);
		}
	},function(){
		$('#tabs_4_table').jqGrid('setRowData',item.uid, {'status':'发送失败'});
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
				Attention(size, rowData, p_this, uids, u_index, uid);
			}
		}else{
			size++;
			Attention(size, rowData, p_this, uids, u_index, uid);
		}
	});
} 
/**
 * 获取关注目标
 */
function onClick_getTargetAttention_Attention(){
	if(localStorage.AttentionGroup != 'run'){
		alert('请选选择分组!');
		return;
	}
	var rowData = $('#tabs_4_table').jqGrid('getRowData');
	if(rowData.length == 0){
		alert('没有加载账号信息或该分组没有账号数据！');
		return;
	}
	$('.dialog_getattentionsource').dialog({
		modal: true,
		title: '获取关注目标',
		resizable: false,
		width: $(document).width() * 0.5,
	    height: $(document).height() * 0.85,
	    open: function(){
	    },
	    create: function(){
	    	$('#dialog_getattentionsource_table').jqGrid({
	    		datatype: "local",
	    		height: 350,
	    		colNames:['uid','操作'],
	    	   	colModel:[
	    	   		{name:'uid',index:'uid', width:30, align:'center', sortable:false},
	    	   		{name:'actions',index:'actions', width:30, align:'center', sortable:false}
	    	   	],
	    	   	rownumbers:true,
	       		rowNum: 50,
	       	   	pager: "#dialog_getattentionsource_pager",
	       	 	viewrecords: true,
	       	   	width: $(document).width() * 0.45,
	       	 	loadComplete: function(){
	       	 		
	       	 	}
	    	});
	    	$('.dialog_getattentionsource_search .search_sina').click(function(){
	    		var uid = $(this).parent().find('.uid').val();
	    		if(!uid || uid < 100000){
	    			alert('没有输入uid或uid格式不正确');
	    			return;
	    		}
	    		var rowData = $('#dialog_getattentionsource_table').jqGrid('getRowData');
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
	    				$('#dialog_getattentionsource_table').jqGrid('addRowData','uid',[{uid: uid,actions: act}]);
	    			}else{
	    				alert('uid: '+uid+' 账号不存在或搜索错误！');
	    			}
	    			wait.close();
	    		},function(err){
	    			alert('uid: '+uid+' 账号不存在或搜索错误！');
	    			wait.close();
	    		});
	    		
	    	});
	    	$('.dialog_getattentionsource_search .search_local').click(function(){
	    		$('.dialog_attentionsearchtargetuser').dialog({
	    			modal: true,
	    			title: '本地库搜索',
	    			resizable: false,
	    			width: $(document).width() * 0.4,
	    		    height: $(document).height() * 0.5,
	    		    buttons: {
	    		    	'搜索': function(){
	    		    		var dom = $('.dialog_attentionsearchtargetuser');
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
	    		    				$('.dialog_attentionsearchtargetuser').dialog('close');
	    		    				if(s_count == 0){
	    		    					return;
	    		    				}
	    		    				for(var i = 0 ; i < list.length ; i ++){
	    		    					var item = list[i];
	    		    					var act = '<a href="#" onclick="onClick_deleteSearchUidByTable(\''+item.uid+'\')">删除</a>';
	    		    					item.actions = act;
	    		    					
	    		    				}
	    		    				$('#dialog_getattentionsource_table').jqGrid('addRowData','uid',list);
	    		    				
	    		    			}
	    		    			
	    		    			
	    		    		},function(err){
	    		    			wait.close();
	    		    			$('.dialog_attentionsearchtargetuser').dialog('close');
	    		    		});
	    		    	}
	    		    }
	    		});
	    	});
	    	
	    },
	    buttons:{
	    	'确定':function(){
	    		var rowData = $('#dialog_getattentionsource_table').jqGrid('getRowData');
	    		$('.dialog_getattentionsource').dialog('close');
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
	    		localStorage.AttentionUidArray = JSON.stringify(uids);
	    		var rowData_Tab4 = $('#tabs_4_table').jqGrid('getRowData');
	    		for(var i = 0 ; i < rowData_Tab4.length ; i ++){
	    			var rowItem = rowData_Tab4[i];
	    			$('#tabs_4_table').jqGrid('setRowData',rowItem.uid,{target: target});
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
	$('#dialog_getattentionsource_table').jqGrid('delRowData',uid);
//	var rowData = $('#dialog_getattentionsource_table').jqGrid('getRowData');
//	for(var i = 0 ; i < rowData.length ; i ++){
//		if(rowData[i].uid == uid){
//			$('#dialog_getattentionsource_table').jqGrid('delRowData',i);
//		}
//	}
	
}
/**
 * 加载账号的事件
 */
function onClick_loadAccount_Attention(){
	if(localStorage.AttentionGroup != 'run'){
		alert('请选选择分组!');
		return;
	}
	
	var wait = new weibo.WaitAlert('正在加载账号信息...');
	wait.show();
	weibo.Action_3011_GetGroupUserList(localStorage.AttentionGroupGid, function(data){
		if(data.res){
			var list = data.pld.list;
			localStorage['GroupAccount_'+localStorage.AttentionGroupGid] = JSON.stringify(list);
			for(var i = 0 ; i < list.length ; i ++){
				var item = list[i];
				item.description = item.info;
			}
			$('#tabs_4_table').jqGrid('clearGridData');
			$('#tabs_4_table').jqGrid('addRowData', 'uid', list);
			wait.close();
			
			
		}
		
		console.debug(data);
	});
	
}
/**
 * 选择分组的事件
 */
function onClick_selectGroup_Attention(){
	
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
		    	
	    		var cgroup = $('#t_tabs_4_table .currentGroup');
	    		
	    		cgroup.text('当前分组：'+group.find('option:checked').text()).attr('gid',group.val());
	    		localStorage.AttentionGroup = 'run';
	    		localStorage.AttentionGroupName = group.find('option:checked').text();
	    		localStorage.AttentionGroupGid = group.val();
	    		
	    		$(this).dialog('close');
	    	}
	    }
	    
	});
	
	
}
