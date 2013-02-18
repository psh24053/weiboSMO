/**
*初始化发送微博的toolbar
*/
function initSendMessage_Toolbar(){
	
	var toolbar = $('#t_tabs_2_table');
	
	toolbar.css('height','30px');
	
	var currentGroup = $('<span></span>').addClass('currentGroup').css('margin-left','5px');
	
	if(localStorage.SendWeiboGroup == 'none'){
		currentGroup.text('请选择分组');
	}else{
		currentGroup.text('当前分组：'+localStorage.SendWeiboGroupName);
		currentGroup.attr('gid',localStorage.SendWeiboGroupGid);
	}
	toolbar.append(currentGroup);
	
	
	var selectGroup = $('<button></button>').text('选择分组').button().attr('title','选择一个分组，对其中的账号进行更新资料操作');
	selectGroup.click(onClick_selectGroup_sendmsg);
	toolbar.append(selectGroup);

	var loadAccount = $('<button></button>').text('加载账号').button().attr('title','将当前分组中所有账号读取到表格中');
	loadAccount.click(onClick_loadAccount_sendmsg);
	toolbar.append(loadAccount);
	
	var sendSource = $('<button></button>').text('获取@目标用户').button().attr('title','从数据库搜索指定条件的发送对象数据源');
	sendSource.click(onClick_getTargetUser_sendmsg);
	toolbar.append(sendSource);
	
	var randomContent = $('<button></button>').text('随机获取内容库').button().attr('title','从内容数据库为每一个账号随机分配发送信息内容');
	randomContent.click(onClick_getRandom_sendmsg);
	toolbar.append(randomContent);
	
	var executeContent = $('<button></button>').text('开始发送').button().attr('title','开始发送微博');
	executeContent.click(onClick_execute_sendmsg);
	toolbar.append(executeContent);
	
}
/**
 * 保存对于grid的修改
 */
function onClick_savecontent_sendmsg(){
	
}
/**
 * sendmsg jqgrid 双击事件
 */
function sendmsg_jqgrid_ondblclick(rowid){
	$('#tabs_2_table').jqGrid('');
	
}

/**
 * 执行发送微博操作
 */
function onClick_execute_sendmsg(){
	if(localStorage.SendWeiboGroup != 'run'){
		alert('请选选择分组!');
		return;
	}
	var rowData = $('#tabs_2_table').jqGrid('getRowData');
	if(rowData.length == 0){
		alert('没有加载账号信息或该分组没有账号数据！');
		return;
	}
	
	// 验证是否存在空字段
	for(var i = 0 ; i < rowData.length ; i ++){
		var row = rowData[i];
		for(var key in row){
			if(row[key] == '' && key != 'dataStore'){
				alert('第 '+(i+1)+' 行，有字段为空!');
				return;
			}
		}
	}
	
	$(this).parent().find('button').button('disable');
	var p_this = $(this);
	var size = 0;
	SendWeibo(size, rowData, p_this);
}
/**
 * 发送微博的递归
 */
function SendWeibo(size, rowData, p_this){
	var i = size;
	var item = rowData[i];
	$('#tabs_2_table').jqGrid('setRowData',i, {'status':'正在操作...'});
	weibo.Action_3023_SendWeibo({
		uid: item.uid,
		content: item.nck,
	}, function(data){
		if(data.res){
			$('#tabs_1_table').jqGrid('setRowData',i, {
				'status': '更新成功'
			});
			
		}else{
			$('#tabs_1_table').jqGrid('setRowData',i, {'status':'更新失败'});
		}
		if(rowData.length == size+1){
			p_this.parent().find('button').button('enable');
			alert('更新命令执行完毕！');
		}else{
			size++;
			UpdateInfo(size, rowData, p_this);
		}
	},function(){
		$('#tabs_1_table').jqGrid('setRowData',i, {'status':'更新失败'});
		if(rowData.length == size+1){
			p_this.parent().find('button').button('enable');
			alert('更新命令执行完毕！');
		}else{
			size++;
			UpdateInfo(size, rowData, p_this);
		}
	});
} 
/**
 * 随即获取内容库
 */
function onClick_getRandom_sendmsg(){
	if(localStorage.SendWeiboGroup != 'run'){
		alert('请选选择分组!');
		return;
	}
	var rowData = $('#tabs_2_table').jqGrid('getRowData');
	if(rowData.length == 0){
		alert('没有加载账号信息或该分组没有账号数据！');
		return;
	}
}
/**
 * 获取@目标用户
 */
function onClick_getTargetUser_sendmsg(){
	if(localStorage.SendWeiboGroup != 'run'){
		alert('请选选择分组!');
		return;
	}
	var rowData = $('#tabs_2_table').jqGrid('getRowData');
	if(rowData.length == 0){
		alert('没有加载账号信息或该分组没有账号数据！');
		return;
	}
}
/**
 * 加载账号的事件
 */
function onClick_loadAccount_sendmsg(){
	if(localStorage.SendWeiboGroup != 'run'){
		alert('请选选择分组!');
		return;
	}
	
	var wait = new weibo.WaitAlert('正在加载账号信息...');
	wait.show();
	weibo.Action_3011_GetGroupUserList(localStorage.SendWeiboGroupGid, function(data){
		if(data.res){
			var list = data.pld.list;
			localStorage['GroupAccount_'+localStorage.SendWeiboGroupGid] = JSON.stringify(list);
			for(var i = 0 ; i < list.length ; i ++){
				var item = list[i];
				item.description = item.info;
			}
			$('#tabs_2_table').jqGrid('clearGridData');
			$('#tabs_2_table').jqGrid('addRowData', 'uid', list);
			wait.close();
			
			
		}
		
		console.debug(data);
	});
	
}
/**
 * 选择分组的事件
 */
function onClick_selectGroup_sendmsg(){
	
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
		    	
	    		var cgroup = $('#t_tabs_2_table .currentGroup');
	    		
	    		cgroup.text('当前分组：'+group.find('option:checked').text()).attr('gid',group.val());
	    		localStorage.SendWeiboGroup = 'run';
	    		localStorage.SendWeiboGroupName = group.find('option:checked').text();
	    		localStorage.SendWeiboGroupGid = group.val();
	    		
	    		$(this).dialog('close');
	    	}
	    }
	    
	});
	
	
}
