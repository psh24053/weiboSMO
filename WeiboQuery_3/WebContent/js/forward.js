/**
 * 初始化转发消息的toolbar
 */
function initForward_Toolbar(){
	
	var toolbar = $('#t_tabs_3_table');
	
	toolbar.css('height','30px');
	
	var currentGroup = $('<span></span>').addClass('currentGroup').css('margin-left','5px');
	
	if(!localStorage.ForwardGroup || localStorage.ForwardGroup == 'none'){
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
	    		if(val.length > 140){
	    			alert("转发理由内容数量过长，请减少 "+(val.length - 140)+" 个字符");
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
	var mids = JSON.parse(localStorage.ForwardMidArray);
	if(!mids || mids.length == 0){
		alert('请选择转发目标');
		return;
	}
	
	
	// 验证是否存在空字段
	for(var i = 0 ; i < rowData.length ; i ++){
		var row = rowData[i];
		var contentLength = row.content.substring(row.content.lastIndexOf('>')+1).length;
		if(contentLength > 140 ){
			alert("第 "+i+" 行的转发理由的字符过长，请减少 "+(contentLength - 140)+" 个字符");
			return;
		}
		
	}
	
	$(this).parent().find('button').button('disable');
	var p_this = $(this);
	var size = 0;
	var m_index = 0;
	var mid = mids[m_index];
	
	
	
//	for(var m = 0 ; m < mids.length ; m ++){
//		var mid = mids[m];
//		$(this).text('正在转发 mid: '+mid);
//		
//		for(var i = 0 ; i < rowData.length ; i ++){
//			var item = rowData[i];
//			$('#tabs_3_table').jqGrid('setRowData',item.uid, {'status':'正在操作...'});
//			weibo.Action_3026_ForwardMessage({
//				uid: item.uid,
//				mid: mid,
//				content: item.content.substring(item.content.lastIndexOf('>')+1)
//			}, function(data){
//				if(data.res){
//					$('#tabs_3_table').jqGrid('setRowData',item.uid, {
//						'status': '转发成功'
//					});
//					
//				}else{
//					$('#tabs_3_table').jqGrid('setRowData',item.uid, {'status':'转发失败'});
//				}
//			},function(){
//				$('#tabs_3_table').jqGrid('setRowData',item.uid, {'status':'发送失败'});
//			});
//			
//		}
//		
//	}
//	$(this).parent().find('button').button('enable');
//	$(this).text('开始转发');
	$(this).find('span').text('正在转发 mid: '+mid);
	Forward(size, rowData, p_this, mids, m_index, mid);
}
/**
 * 发送微博的递归
 */
function Forward(size, rowData, p_this, mids, m_index, mid){
	var i = size;
	var item = rowData[i];
	$('#tabs_3_table').jqGrid('setRowData',item.uid, {'status':'正在操作...'});
	weibo.Action_3026_ForwardMessage({
		uid: item.uid,
		mid: mid,
		content: item.content.substring(item.content.lastIndexOf('>')+1)
	}
	, function(data){
		if(data.res){
			$('#tabs_3_table').jqGrid('setRowData',item.uid, {
				'status': '转发成功'
			});
			
		}else{
			$('#tabs_3_table').jqGrid('setRowData',item.uid, {'status':'转发失败'});
		}
		if(rowData.length == size+1){
			if(mids.length == m_index + 1){
				p_this.parent().find('button').button('enable');
				p_this.find('span').text('开始转发');
				alert('发送命令执行完毕！');
			}else{
				size = 0;
				m_index ++;
				mid = mids[m_index];
				p_this.find('span').text('正在转发 mid: '+mid);
				Forward(size, rowData, p_this, mids, m_index, mid);
			}
		}else{
			size++;
			Forward(size, rowData, p_this, mids, m_index, mid);
		}
	},function(){
		$('#tabs_3_table').jqGrid('setRowData',item.uid, {'status':'发送失败'});
		if(rowData.length == size+1){
			if(mids.length == m_index + 1){
				p_this.parent().find('button').button('enable');
				p_this.find('span').text('开始转发');
				alert('发送命令执行完毕！');
			}else{
				size = 0;
				m_index ++;
				mid = mids[m_index];
				p_this.find('span').text('正在转发 mid: '+mid);
				Forward(size, rowData, p_this, mids, m_index, mid);
			}
		}else{
			size++;
			Forward(size, rowData, p_this, mids, m_index, mid);
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
	    		colNames:['mid','内容','时间'],
	    	   	colModel:[
	    	   		{name:'mid',index:'mid', width:30, align:'center', sortable:false},
	    	   		{name:'content',index:'content', width:50, align:'center', sortable:false},
	    	   		{name:'time',index:'time', width:30 , align:'center', sortable:false}
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
	    		var selarrrow = $('#dialog_getforwardsource_table').jqGrid('getGridParam','selarrrow');
	    		
	    		$('.dialog_getforwardsource').dialog('close');
	    		
	    		localStorage.ForwardMidArray = JSON.stringify(selarrrow);
	    		
	    		var target = "";
	    		for(var i = 0 ; i < selarrrow.length ; i ++){
	    			var mid = selarrrow[i];
	    			var rowItem = $('#dialog_getforwardsource_table').jqGrid('getRowData',mid);
	    			
	    			if(i == selarrrow.length - 1){
	    				target += rowItem.mid;
	    			}else{
	    				target += rowItem.mid+",";
	    			}
	    			
	    		}
	    		
	    		var rowData = $('#tabs_3_table').jqGrid('getRowData');
	    		for(var i = 0 ; i < rowData.length ; i ++){
	    			var rowItem = rowData[i];
	    			$('#tabs_3_table').jqGrid('setRowData',rowItem.uid,{target: target,content:'<button onclick="onClick_editorContent_forward(this,'+rowItem.uid+');">编辑</button>'});
	    		}
	    		
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
	var uid = $('<input type="text" autocomplete="on" class="source_uid" />');
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
				$('#dialog_getforwardsource_table').jqGrid('addRowData', 'mid', list);
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
