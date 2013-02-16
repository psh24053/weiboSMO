/**
*初始化发送微博的toolbar
*/
function initSendMessage_Toolbar(){
	
	var toolbar = $('#t_tabs_2_table');
	
	toolbar.css('height','30px');
	
	var currentGroup = $('<span></span>').addClass('currentGroup').css('margin-left','5px');
	
	if(localStorage.ModifyGroup == 'none'){
		currentGroup.text('请选择分组');
	}else{
		currentGroup.text('当前分组：'+localStorage.ModifyGroupName);
		currentGroup.attr('gid',localStorage.ModifyGroupGid);
	}
	toolbar.append(currentGroup);
	
	
	var selectGroup = $('<button></button>').text('选择分组').button().attr('title','选择一个分组，对其中的账号进行更新资料操作');
//	selectGroup.click(onClick_selectGroup_sendmsg);
	toolbar.append(selectGroup);

	var loadAccount = $('<button></button>').text('加载账号').button().attr('title','将当前分组中所有账号读取到表格中');
//	loadAccount.click(onClick_loadAccount_sendmsg);
	toolbar.append(loadAccount);
	
	var randomContent = $('<button></button>').text('随机获取内容库').button().attr('title','从内容数据库为每一个账号随机分配发送信息内容');
//	randomContent.click(onClick_getRandom_sendmsg);
	toolbar.append(randomContent);
	
	var sendSource = $('<button></button>').text('获得发送对象数据源').button().attr('title','从数据库搜索指定条件的发送对象数据源');
//	sendSource.click(onClick_sendSource_sendmsg);
	toolbar.append(sendSource);
	
	
}

/**
*对表格中的账号执行更新操作
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
			if(row[key] == '' && key != 'dataStore'){
				alert('第 '+(i+1)+' 行，有字段为空!');
				return;
			}
		}
	}
	
	$(this).parent().find('button').button('disable');
	var p_this = $(this);
	var size = 0;
	UpdateInfo(size, rowData, p_this);
	
}