/**
 * 初始化更新资料的toolbar
 */
function initModifyInfo_Toolbar(){
	
	var toolbar = $('#t_tabs_1_table');
	
	toolbar.css('height','30px');
	
	var currentGroup = $('<span></span>').addClass('currentGroup').css('margin-left','5px');
	
	if(!localStorage.ModifyGroup || localStorage.ModifyGroup == 'none'){
		currentGroup.text('请选择分组');
	}else{
		currentGroup.text('当前分组：'+localStorage.ModifyGroupName);
		currentGroup.attr('gid',localStorage.ModifyGroupGid);
	}
	toolbar.append(currentGroup);
	
	
	var selectGroup = $('<button></button>').text('选择分组').button().attr('title','选择一个分组，对其中的账号进行更新资料操作');
	selectGroup.click(onClick_selectGroup_modify);
	toolbar.append(selectGroup);

	var loadAccount = $('<button></button>').text('加载账号').button().attr('title','将当前分组中所有账号读取到表格中');
	loadAccount.click(onClick_loadAccount_modify);
	toolbar.append(loadAccount);
	
	var synInfo = $('<button></button>').text('读取资料').button().attr('title','从新浪将对应账号的资料读取出来，并且更新到数据库中');
	synInfo.click(onClick_loadInfo_modify);
	toolbar.append(synInfo);

	var uploadExcel = $('<button></button>').text('导入Excel资料文件').button().attr('title','上传excel文件，更改账号的资料');
	uploadExcel.click(onClick_uploadExcel_modify);
	toolbar.append(uploadExcel);

	var execute = $('<button></button>').text('执行更新').button().attr('title','根据表格中的资料数据，更新账号资料');
	execute.click(onClick_execute_modify);
	toolbar.append(execute);
}
/**
 * 对表格中的账号执行更新操作
 */
function onClick_execute_modify(){
	if(localStorage.ModifyGroup != 'run'){
		alert('请选选择分组!');
		return;
	}
	var rowData = $('#tabs_1_table').jqGrid('getRowData');
	if(rowData.length == 0){
		alert('没有加载账号信息或该分组没有账号数据！');
		return;
	}
	
	// 验证是否存在空字段
	for(var i = 0 ; i < rowData.length ; i ++){
		var row = rowData[i];
		for(var key in row){
			if(row[key] == '' && key != 'dataStore' && key != 'status'){
				alert('第 '+(i+1)+' 行，有字段为空!');
				return;
			}
				
			if(row['description'].length > 120){
				alert('第 '+(i+1)+' 行，个人简介的长度不能超过120个字符!请减少 '+(row['description'].length - 120)+' 个字符');
				return;
			}
		}
	}
	
	$(this).parent().find('button').button('disable');
	var p_this = $(this);
	var size = 0;
	UpdateInfo(size, rowData, p_this);
	
}
/**
 * 执行更新操作的递归
 */
function UpdateInfo(size, rowData, p_this){
	var i = size;
	var item = rowData[i];
	$('#tabs_1_table').jqGrid('setRowData',item.uid, {'status':'正在操作...'});
	weibo.Action_3021_UpdateInfo({
		uid: item.uid,
		nck: item.nck,
		prov: item.prov,
		city: item.city,
		gender: item.gender,
		birthday: item.birthday,
		info: item.description.substring(item.description.lastIndexOf('>')+1),
		tags: item.tags
	}, function(data){
		if(data.res){
			$('#tabs_1_table').jqGrid('setRowData',item.uid, {
				'status': '更新成功'
			});
			
		}else{
			$('#tabs_1_table').jqGrid('setRowData',item.uid, {'status':'更新失败:'+data.pld.info});
		}
		if(rowData.length == size+1){
			p_this.parent().find('button').button('enable');
			alert('更新命令执行完毕！');
		}else{
			size++;
			UpdateInfo(size, rowData, p_this);
		}
	},function(){
		$('#tabs_1_table').jqGrid('setRowData',item.uid, {'status':'更新失败'});
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
 * 上传excel资料文件
 */
function onClick_uploadExcel_modify(){
	if(localStorage.ModifyGroup != 'run'){
		alert('请选选择分组!');
		return;
	}
	var rowData = $('#tabs_1_table').jqGrid('getRowData');
	if(rowData.length == 0){
		alert('没有加载账号信息或该分组没有账号数据！');
		return;
	}
	var idx = random_char(8);
	$('.dialog_uploadexcel').find('form').attr('action','UploadExcel?idx='+idx);
	$('.dialog_uploadexcel').find('input[type=button]').click(function(){
		if($('.dialog_uploadexcel input[type=file]').val() != ''){
			var wait = new weibo.WaitAlert('正在上传');
			wait.show();
			$('.dialog_uploadexcel').data(idx,wait);
			$('.dialog_uploadexcel').find('form').submit();
		}else{
			alert('请选择文件！');
		}
	});
	$('.dialog_uploadexcel').dialog({
		modal: true,
		title: '上传excel',
		resizable: false,
		width: $(document).width() * 0.5,
	    height: $(document).height() * 0.2
	});
	
	
}
/**
 * 上传完成后触发
 * @param idx
 */
function onUploadExcelComplete(idx){
	$('.dialog_uploadexcel').dialog('close');
	$('.waitAlert').data('close')();
	
	var rowData = $('#tabs_1_table').jqGrid('getRowData');
	for(var i = 0 ; i < rowData.length ; i ++){
		$('#tabs_1_table').jqGrid('setRowData',i, {'status':'','nck':'','prov':'','gender':'','birthday':'','description':'','city':'','tags':''});
	}
	
	var wait = new weibo.WaitAlert('正在加载Excel资料...');
	wait.show();
	weibo.Action_3020_GetExcelContent(idx,function(data){
		
		if(data.res){
			
			var list = data.pld.list;
			var rowData = $('#tabs_1_table').jqGrid('getRowData');
			for(var i = 0 ; i < rowData.length ; i ++){
				var item = list[i];
				var rowItem = rowData[i];
				if(item == undefined){
					continue;
				}
				$('#tabs_1_table').jqGrid('setRowData',rowItem.uid, {
					'status': '准备就绪',
					'nck': item.nck,
					'prov': item.prov,
					'city': item.city,
					'gender': item.gender,
					'birthday': item.birthday,
					'description': item.info,
					'tags': item.tags
				});
			}
			
		}else{
			alert('解析Excel失败');
		}
		wait.close();
	},function(){
		wait.close();
	});
	
	
	
}
/**
 * 读取资料的事件
 */
function onClick_loadInfo_modify(){
	if(localStorage.ModifyGroup != 'run'){
		alert('请选选择分组!');
		return;
	}
	var rowData = $('#tabs_1_table').jqGrid('getRowData');
	if(rowData.length == 0){
		alert('没有加载账号信息或该分组没有账号数据！');
		return;
	}
	
	$(this).parent().find('button').button('disable');
	
	var p_this = $(this);
	var size = 0;
	for(var i = 0 ; i < rowData.length ; i ++){
		var item = rowData[i];
		$('#tabs_1_table').jqGrid('setRowData',item.uid, {'status':'','nck':'','prov':'','gender':'','birthday':'','description':'','city':'','tags':''});
	}
	
	
	readInfo(size, rowData, p_this);
	
	
}
/**
 * 递归读取资料
 * @param size
 * @param rowData
 * @param p_this
 */
function readInfo(size,rowData,p_this){
		var i = size;
		var item = rowData[i];
		$('#tabs_1_table').jqGrid('setRowData',item.uid, {'status':'正在读取...'});
		weibo.Action_3019_GetAccountInfoFromSina(item.uid, function(data){
			if(data.res){
				$('#tabs_1_table').jqGrid('setRowData',item.uid, {
					'status': '读取成功',
					'nck': data.pld.nck,
					'prov': data.pld.prov,
					'city': data.pld.city,
					'gender': data.pld.gender,
					'birthday': data.pld.birthday,
					'description': data.pld.info,
					'tags': data.pld.tags
				});
				
			}else{
				$('#tabs_1_table').jqGrid('setRowData',item.uid, {'status':'读取失败'});
			}
			if(rowData.length == size+1){
				p_this.parent().find('button').button('enable');
				alert('读取命令执行完毕！');
			}else{
				size++;
				readInfo(size, rowData, p_this);
			}
		},function(){
			$('#tabs_1_table').jqGrid('setRowData',item.uid, {'status':'读取失败'});
			if(rowData.length == size+1){
				p_this.parent().find('button').button('enable');
				alert('读取命令执行完毕！');
			}else{
				size++;
				readInfo(size, rowData, p_this);
			}
		});
		
		
		
}
/**
 * 加载账号的事件
 */
function onClick_loadAccount_modify(){
	if(localStorage.ModifyGroup != 'run'){
		alert('请选选择分组!');
		return;
	}
	
	var wait = new weibo.WaitAlert('正在加载账号信息...');
	wait.show();
	weibo.Action_3011_GetGroupUserList(localStorage.ModifyGroupGid, function(data){
		if(data.res){
			var list = data.pld.list;
			localStorage['GroupAccount_'+localStorage.ModifyGroupGid] = JSON.stringify(list);
			for(var i = 0 ; i < list.length ; i ++){
				var item = list[i];
				item.description =  item.info;
				item.actions = '<button onclick="$(\'#tabs_1_table\').editRow(\''+item.uid+'\')">编辑</button>';
				item.actions += '<button  onclick="$(\'#tabs_1_table\').saveRow(\''+item.uid+'\')">保存</button>';
				item.actions += '<button onclick="$(\'#tabs_1_table\').restoreRow(\''+item.uid+'\')">取消</button>';
			}
			$('#tabs_1_table').jqGrid('clearGridData');
			$('#tabs_1_table').jqGrid('addRowData', 'uid', list);
			wait.close();
			
		}
		
		console.debug(data);
	});
	
}

/**
 * 选择分组的事件
 */
function onClick_selectGroup_modify(){
	
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
		    	
	    		var cgroup = $('#t_tabs_1_table .currentGroup');
	    		
	    		cgroup.text('当前分组：'+group.find('option:checked').text()).attr('gid',group.val());
	    		localStorage.ModifyGroup = 'run';
	    		localStorage.ModifyGroupName = group.find('option:checked').text();
	    		localStorage.ModifyGroupGid = group.val();
	    		
	    		$(this).dialog('close');
	    	}
	    }
	    
	});
	
	
}