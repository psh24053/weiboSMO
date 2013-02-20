/**
 * 初始化转发消息的toolbar
 */
function initForward_Toolbar(){
	
	var toolbar = $('#t_tabs_3_table');
	
	toolbar.css('height','30px');
	
	var currentGroup = $('<span></span>').addClass('currentGroup').css('margin-left','5px');
	
	if(localStorage.ForwardGroup == 'none'){
		currentGroup.text('请选择分组');
	}else{
		currentGroup.text('当前分组：'+localStorage.ForwardGroupName);
		currentGroup.attr('gid',localStorage.ForwardGroupGid);
	}
	toolbar.append(currentGroup);
	
	
	var selectGroup = $('<button></button>').text('选择分组').button().attr('title','选择一个分组，对其中的账号进行更新资料操作');
	selectGroup.click(onClick_selectGroup_forward);
	toolbar.append(selectGroup);

	var loadAccount = $('<button></button>').text('加载账号').button().attr('title','将当前分组中所有账号读取到表格中');
	loadAccount.click(onClick_loadAccount_forward);
	toolbar.append(loadAccount);
	
	var sendSource = $('<button></button>').text('获取转发目标').button().attr('title','从新浪搜索转发目标并读取到表格中');
	sendSource.click(onClick_getTargetForward_forward);
	toolbar.append(sendSource);
	
	var executeContent = $('<button></button>').text('开始转发').button().attr('title','开始发送微博');
	executeContent.click(onClick_execute_forward);
	toolbar.append(executeContent);
	
}
/**
 * 编辑内容
 */
function onClick_editorContent_forward(obj, uid){
	
	var rowItem = $('#tabs_3_table').jqGrid('getRowData',uid);
	var con = $(obj).parent().text().substring(2);
	
	
	$('.dialog_editorcontent').dialog({
		modal: true,
		title: '编辑文本内容',
		resizable: false,
		width: $(document).width() * 0.4,
	    height: $(document).height() * 0.5,
	    open: function(event, ui){
	    	$('.dialog_editorcontent').find('.editorcontent').val(con);
	    },
	    buttons:{
	    	'保存': function(){
	    		var val = $('.dialog_editorcontent').find('.editorcontent').val();
	    		if(rowItem.target.length + val.length > 140){
	    			alert("@目标用户+微博内容数量过长，请减少 "+(rowItem.target.length + val.length - 140)+" 个字符");
	    			return;
	    		}
	    		
	    		$('#tabs_3_table').jqGrid('setRowData',uid,{content: '<button onclick="onClick_editorContent_forward(this,'+uid+');">编辑</button>'+val});
	    		$('.dialog_editorcontent').dialog('close');
	    		
	    	}
	    }
	});
	
}
/**
 * 执行发送微博操作
 */
function onClick_execute_forward(){
	if(localStorage.ForwardGroup != 'run'){
		alert('请选选择分组!');
		return;
	}
	var rowData = $('#tabs_3_table').jqGrid('getRowData');
	if(rowData.length == 0){
		alert('没有加载账号信息或该分组没有账号数据！');
		return;
	}
	
	// 验证是否存在空字段
	for(var i = 0 ; i < rowData.length ; i ++){
		var row = rowData[i];
		if(row.content.length < 3){
			alert("第 "+i+" 行的内容为空");
			return;
		}
		var contentLength = row.content.substring(row.content.lastIndexOf('>')+1).length;
		if(contentLength + row.target.length > 140 ){
			alert("第 "+i+" 行的微博内容和@目标用户的字符过长，请减少 "+(contentLength + row.target.length - 140)+" 个字符");
			return;
		}
		
	}
	
	$(this).parent().find('button').button('disable');
	var p_this = $(this);
	var size = 0;
	Forward(size, rowData, p_this);
}
/**
 * 发送微博的递归
 */
function Forward(size, rowData, p_this){
	var i = size;
	var item = rowData[i];
	$('#tabs_3_table').jqGrid('setRowData',item.uid, {'status':'正在操作...'});
	weibo.Action_3023_Forward(
		parseInt(item.uid),
		item.content.substring(item.content.lastIndexOf('>')+1) + item.target
	, function(data){
		if(data.res){
			$('#tabs_3_table').jqGrid('setRowData',item.uid, {
				'status': '发送成功'
			});
			
		}else{
			$('#tabs_3_table').jqGrid('setRowData',item.uid, {'status':'发送失败'});
		}
		if(rowData.length == size+1){
			p_this.parent().find('button').button('enable');
			alert('发送命令执行完毕！');
		}else{
			size++;
			Forward(size, rowData, p_this);
		}
	},function(){
		$('#tabs_3_table').jqGrid('setRowData',item.uid, {'status':'发送失败'});
		if(rowData.length == size+1){
			p_this.parent().find('button').button('enable');
			alert('发送命令执行完毕！');
		}else{
			size++;
			Forward(size, rowData, p_this);
		}
	});
} 
/**
 * 随即获取内容库
 */
function onClick_getRandom_forward(){
	if(localStorage.ForwardGroup != 'run'){
		alert('请选选择分组!');
		return;
	}
	var rowData = $('#tabs_3_table').jqGrid('getRowData');
	if(rowData.length == 0){
		alert('没有加载账号信息或该分组没有账号数据！');
		return;
	}
	
	var dialog = $('.dialog_randomForwardcontent').dialog({
		modal: true,
		title: '随机获取发言内容',
		resizable: false,
		width: $(document).width() * 0.2,
	    height: $(document).height() * 0.3,
	    open: function(event, ui){
	    	weibo.Action_3009_GetTextTypeList(function(data){
	    		if(data.res){
	    			$('.dialog_randomForwardcontent').find('.texttype_name option').remove();
	    			var list = data.pld.list;
	    			for(var i = 0 ; i < list.length ; i ++){
	    				var option = $('<option></option>').text(list[i].name).val(list[i].ttid);
	    				$('.dialog_randomForwardcontent').find('.texttype_name').append(option);
	    			}
	    			
	    		}
	    		
	    	},function(err){
	    		console.debug(err);
	    	});
	    },
	    buttons:{
	    	'随机获取': function(){
	    		var ttid = $('.dialog_randomForwardcontent').find('.texttype_name').val();
	    		var count = $('#tabs_3_table').jqGrid('getRowData').length;
	    		var wait = new weibo.WaitAlert('正在获取...');
	    		wait.show();
	    		weibo.Action_3008_GetTextList(ttid, count, function(data){
	    			if(data.res){
	    				var list = data.pld.list;
	    				var rowData = $('#tabs_3_table').jqGrid('getRowData');
	    				for(var i = 0 ; i < list.length ; i ++){
	    					$('#tabs_3_table').jqGrid('setRowData',rowData[i].uid,{content: '<button onclick="onClick_editorContent_forward(this,'+rowData[i].uid+');">编辑</button>'+list[i]});
	    				}
	    			}
	    			wait.close();
	    			$('.dialog_randomForwardcontent').dialog('close');
	    			alert('获取成功！');
	    		},function(err){
	    			wait.close();
	    			$('.dialog_randomForwardcontent').dialog('close');
	    			alert('获取失败！');
	    		});
	    	}
	    }
	});
	
	
	
}
/**
 * 获取转发目标
 */
function onClick_getTargetForward_forward(){
	if(localStorage.ForwardGroup != 'run'){
		alert('请选选择分组!');
		return;
	}
	var rowData = $('#tabs_3_table').jqGrid('getRowData');
	if(rowData.length == 0){
		alert('没有加载账号信息或该分组没有账号数据！');
		return;
	}
	$('.dialog_getforwardsource').dialog({
		modal: true,
		title: '获取转发目标',
		resizable: false,
		width: $(document).width() * 0.5,
	    height: $(document).height() * 0.85,
	    open: function(){
	    },
	    create: function(){
	    	$('#dialog_getforwardsource_table').jqGrid({
	    		datatype: "local",
	    		height: 420,
	    		colNames:['mid','内容','时间','dataStore'],
	    	   	colModel:[
	    	   		{name:'mid',index:'mid', width:20, align:'center', sortable:false},
	    	   		{name:'content',index:'content', width:40, align:'center', sortable:false},
	    	   		{name:'time',index:'time', width:40 , sortable:false},
	    	   		{name:'dataStore',index:'dataStore',hidden:true}
	    	   	],
	    	   	rownumbers:true,
	    	   	multiselect: true,
	       		rowNum: 50,
	       	   	pager: "#dialog_getforwardsource_pager",
	       	 	viewrecords: true,
	       	   	width: $(document).width() * 0.45,
	       	 	toolbar: [true,"top"],
	       	 	loadComplete: function(){
	       	 		initGetTargetForwardSourceToolbar();
	       	 	}
	    	});
	    	
	    	
	    },
	    buttons:{
	    	'确定':function(){
	    		
	    		
	    	}
	    }
	});
	
}
/**
 * 初始化获取转发目标的toolbar
 */
function initGetTargetForwardSourceToolbar(){
	var toolbar = $('#t_dialog_getforwardsource_table');
	
	toolbar.css('padding-top','5px').css('padding-bottom','5px').css('height','auto');
	
	var labeluid = $('<label></label>').text('请输入目标uid').css('margin-left','5px');
	var uid = $('<input type="text" class="source_uid" />');
	var labelcount = $('<label></label>').text('数量').css('margin-left','5px');
	var count = $('<input type="number" class="source_count" />');
	var search = $('<button>搜索</button>').button().click(function(){
		var uidVal = $(this).parent().find('.source_uid').val();
		var countVal = $(this).parent().find('.source_count').val();
		if(uid == null || uid == undefined || uid.length == 0){
			alert('uid不能为空');
			return;
		}
		if(countVal == null || countVal == undefined || countVal < 1){
			alert('数量不能为空且不能小于1');
			return;
		}
		
		var wait =new weibo.WaitAlert('正在搜索...');
		wait.show();
		
		weibo.Action_3028_SearchWeiboByUid({
			uid: uidVal,
			count: countVal
		},function(data){
			console.debug(data);
			if(data.res){
				var list = data.pld.list;
				$('#dialog_getforwardsource_table').jqGrid('clearGridData');
				
				
				
			}
			
			wait.close();
		},function(err){
			alert('搜索失败');
			wait.close();
		});
		
		
	});
	
	toolbar.append(labeluid).append(uid).append(labelcount).append(count).append(search);
	
}
/**
 * 加载账号的事件
 */
function onClick_loadAccount_forward(){
	if(localStorage.ForwardGroup != 'run'){
		alert('请选选择分组!');
		return;
	}
	
	var wait = new weibo.WaitAlert('正在加载账号信息...');
	wait.show();
	weibo.Action_3011_GetGroupUserList(localStorage.ForwardGroupGid, function(data){
		if(data.res){
			var list = data.pld.list;
			localStorage['GroupAccount_'+localStorage.ForwardGroupGid] = JSON.stringify(list);
			for(var i = 0 ; i < list.length ; i ++){
				var item = list[i];
				item.description = item.info;
			}
			$('#tabs_3_table').jqGrid('clearGridData');
			$('#tabs_3_table').jqGrid('addRowData', 'uid', list);
			wait.close();
			
			
		}
		
		console.debug(data);
	});
	
}
/**
 * 选择分组的事件
 */
function onClick_selectGroup_forward(){
	
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
		    	
	    		var cgroup = $('#t_tabs_3_table .currentGroup');
	    		
	    		cgroup.text('当前分组：'+group.find('option:checked').text()).attr('gid',group.val());
	    		localStorage.ForwardGroup = 'run';
	    		localStorage.ForwardGroupName = group.find('option:checked').text();
	    		localStorage.ForwardGroupGid = group.val();
	    		
	    		$(this).dialog('close');
	    	}
	    }
	    
	});
	
	
}
