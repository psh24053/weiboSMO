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
 * 编辑内容
 */
function onClick_editorContent_sendmsg(obj, uid){
	
	var rowItem = $('#tabs_2_table').jqGrid('getRowData',uid);
	var con = $(obj).parent().text().substring(2);
	
//	if(!rowItem.target){
//		alert('请先指定@目标用户!');
//		return;
//	}
	
	
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
	    		$('#tabs_2_table').jqGrid('setRowData',uid,{content: '<button onclick="onClick_editorContent_sendmsg(this,'+uid+');">编辑</button>'+val});
	    		$('.dialog_editorcontent').dialog('close');
	    		
	    	}
	    }
	});
	
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
	$('#tabs_2_table').jqGrid('setRowData',item.uid, {'status':'正在操作...'});
	weibo.Action_3023_SendWeibo({
		uid: item.uid,
		content: item.nck,
	}, function(data){
		if(data.res){
			$('#tabs_1_table').jqGrid('setRowData',item.uid, {
				'status': '更新成功'
			});
			
		}else{
			$('#tabs_1_table').jqGrid('setRowData',item.uid, {'status':'更新失败'});
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
	
	var dialog = $('.dialog_randomsendweibocontent').dialog({
		modal: true,
		title: '随机获取发言内容',
		resizable: false,
		width: $(document).width() * 0.2,
	    height: $(document).height() * 0.3,
	    open: function(event, ui){
	    	weibo.Action_3009_GetTextTypeList(function(data){
	    		if(data.res){
	    			$('.dialog_randomsendweibocontent').find('.texttype_name option').remove();
	    			var list = data.pld.list;
	    			for(var i = 0 ; i < list.length ; i ++){
	    				var option = $('<option></option>').text(list[i].name).val(list[i].ttid);
	    				$('.dialog_randomsendweibocontent').find('.texttype_name').append(option);
	    			}
	    			
	    		}
	    		
	    	},function(err){
	    		console.debug(err);
	    	});
	    },
	    buttons:{
	    	'随机获取': function(){
	    		var ttid = $('.dialog_randomsendweibocontent').find('.texttype_name').val();
	    		var count = $('#tabs_2_table').jqGrid('getRowData').length;
	    		var wait = new weibo.WaitAlert('正在获取...');
	    		wait.show();
	    		weibo.Action_3008_GetTextList(ttid, count, function(data){
	    			if(data.res){
	    				var list = data.pld.list;
	    				var rowData = $('#tabs_2_table').jqGrid('getRowData');
	    				for(var i = 0 ; i < list.length ; i ++){
	    					$('#tabs_2_table').jqGrid('setRowData',rowData[i].uid,{content: '<button onclick="onClick_editorContent_sendmsg(this,'+rowData[i].uid+');">编辑</button>'+list[i]});
	    				}
	    			}
	    			wait.close();
	    			$('.dialog_randomsendweibocontent').dialog('close');
	    			alert('获取成功！');
	    		},function(err){
	    			wait.close();
	    			$('.dialog_randomsendweibocontent').dialog('close');
	    			alert('获取失败！');
	    		});
	    	}
	    }
	});
	
	
	
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
	$('.dialog_searchtargetuser').dialog({
		modal: true,
		title: '获取@目标用户',
		resizable: false,
		width: $(document).width() * 0.4,
	    height: $(document).height() * 0.7,
	    open: function(){
	    },
	    buttons:{
	    	'搜索':function(){
	    		var dom = $('.dialog_searchtargetuser');
	    		var nickname = !dom.find('.nickname').val() ? '':dom.find('.nickname').val();
	    		var info = !dom.find('.info').val() ? '':dom.find('.info').val();
	    		var company = !dom.find('.company').val() ? '':dom.find('.company').val();
	    		var school = !dom.find('.school').val() ? '':dom.find('.school').val();
	    		var tags = !dom.find('.tags').val() ? '':dom.find('.tags').val();
	    		var prov = dom.find('.prov option:checked').text() == '不限' ? '': dom.find('.prov option:checked').text();
	    		var city = dom.find('.city option:checked').text() == '不限' ? '': dom.find('.city option:checked').text();
	    		var sex = dom.find('.sex').val();
	    		var age = !dom.find('.age').val() ? '':dom.find('.age').val();
	    		var fol = !dom.find('.fol').val() ? '':dom.find('.fol').val();
	    		var fans = !dom.find('.fans').val() ? '':dom.find('.fans').val();
	    		var count = dom.find('.count').val();
	    		
	    		var wait = new weibo.WaitAlert('正在搜索...');
	    		wait.show();
	    		
	    		weibo.Action_3024_SearchTargetUser({
	    			nickName: nickname,
	    			tag: tags,
					school: school,
					company: company,
					prov: prov,
					city: city,
					age: age,
					sex: sex,
					info: info,
					fol: fol,
					fans: fans,
					count: count
	    		},function(data){
	    			console.debug(data);
	    			
	    			
	    			wait.close();
	    		},function(err){
	    			wait.close();
	    		});
	    		
	    		
	    	}
	    }
	});
	
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

function changeCity(){
	
	if($(".dialog_searchtargetuser").find('.prov').val() == 34){
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">合肥</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">芜湖</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">蚌埠</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">淮南</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">马鞍山</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">淮北</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">铜陵</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="8">安庆</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="10">黄山</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="11">滁州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="12">阜阳</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="13">宿州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="14">巢湖</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="15">六安</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="16">亳州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="17">池州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="18">宣城</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 11){
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">东城区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">西城区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">崇文区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">宣武区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">朝阳区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">丰台区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">石景山区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="8">海淀区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="9">门头沟区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="11">房山区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="12">通州区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="13">顺义区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="14">昌平区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="15">大兴区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="16">怀柔区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="17">平谷区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="28">密云县</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="29">延庆县</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 50){
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">万州区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">涪陵区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">渝中区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">大渡口区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">江北区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">沙坪坝区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">九龙坡区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="8">南岸区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="9">北碚区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="10">万盛区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="11">双桥区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="12">渝北区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="13">巴南区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="14">黔江区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="15">长寿区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="22">綦江县</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="23">潼南县</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="24">铜梁县</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="25">大足县</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="26">荣昌县</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="27">璧山县</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="28">梁平县</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="29">城口县</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="30">丰都县</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="31">垫江县</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="32">武隆县</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="33">忠县</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="34">开县</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="35">云阳县</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="36">奉节县</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="37">巫山县</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="38">巫溪县</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="40">石柱土家族自治县</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="41">秀山土家族苗族自治县</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="42">酉阳土家族苗族自治县</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="43">彭水苗族土家族自治县</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="81">江津区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="82">合川市</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="83">永川区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="84">南川市</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 35){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">福州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">厦门</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">莆田</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">三明</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">泉州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">漳州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">南平</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="8">龙岩</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="9">宁德</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 62){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">兰州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">嘉峪关</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">金昌</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">白银</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">天水</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">武威</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">张掖</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="8">平凉</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="9">酒泉</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="10">庆阳</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="24">定西</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="26">陇南</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="29">临夏</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="30">甘南</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 44){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">广州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">韶关</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">深圳</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">珠海</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">汕头</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">佛山</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">江门</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="8">湛江</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="9">茂名</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="12">肇庆</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="13">惠州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="14">梅州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="15">汕尾</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="16">河源</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="17">阳江</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="18">清远</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="19">东莞</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="20">中山</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="51">潮州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="52">揭阳</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="53">云浮</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 45){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">南宁</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">柳州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">桂林</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">梧州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">北海</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">防城港</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">钦州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="8">贵港</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="9">玉林</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="10">百色</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="11">贺州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="12">河池</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="13">来宾</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="14">崇左</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 52){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">贵阳</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">六盘水</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">遵义</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">安顺</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="22">铜仁</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="23">黔西南</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="24">毕节</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="26">黔东南</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="27">黔南</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 46){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">海口</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">三亚</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="90">其他</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 13){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">石家庄</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">唐山</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">秦皇岛</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">邯郸</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">邢台</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">保定</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">张家口</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="8">承德</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="9">沧州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="10">廊坊</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="11">衡水</option>');
		
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 23){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">哈尔滨</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">齐齐哈尔</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">鸡西</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">鹤岗</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">双鸭山</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">大庆</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">伊春</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="8">佳木斯</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="9">七台河</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="10">牡丹江</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="11">黑河</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="12">绥化</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="27">大兴安岭</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 41){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">郑州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">开封</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">洛阳</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">平顶山</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">安阳</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">鹤壁</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">新乡</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="8">焦作</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="9">濮阳</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="10">许昌</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="11">漯河</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="12">三门峡</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="13">南阳</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="14">商丘</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="15">信阳</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="16">周口</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="17">驻马店</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 42){
		
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">武汉</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">黄石</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">十堰</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">宜昌</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">襄阳</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">鄂州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="8">荆门</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="9">孝感</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="10">荆州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="11">黄冈</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="12">咸宁</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="13">随州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="28">恩施土家族苗族自治州</option>');
		
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 43){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">长沙</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">株洲</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">湘潭</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">衡阳</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">邵阳</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">岳阳</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">常德</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="8">张家界</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="9">益阳</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="10">郴州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="11">永州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="12">怀化</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="13">娄底</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="31">湘西土家族苗族自治州</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 15){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">呼和浩特</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">包头</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">乌海</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">赤峰</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">通辽</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">鄂尔多斯</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">呼伦贝尔</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="22">兴安盟</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="25">锡林郭勒盟</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="26">乌兰察布盟</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="28">巴彦淖尔盟</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="29">阿拉善盟</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 32){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">南京</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">无锡</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">徐州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">常州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">苏州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">南通</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">连云港</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="8">淮安</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="9">盐城</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="10">扬州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="11">镇江</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="12">泰州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="13">宿迁</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 36){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">南昌</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">景德镇</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">萍乡</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">九江</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">新余</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">鹰潭</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">赣州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="8">吉安</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="9">宜春</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="10">抚州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="11">上饶</option>');
		
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 22){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">长春</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">吉林</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">四平</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">辽源</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">通化</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">白山</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">松原</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="8">白城</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="24">延边朝鲜族自治州</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 21){
		
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">沈阳</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">大连</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">鞍山</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">抚顺</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">本溪</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">丹东</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">锦州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="8">营口</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="9">阜新</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="10">辽阳</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="11">盘锦</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="12">铁岭</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="13">朝阳</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="14">葫芦岛</option>');
		
		
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 64){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">银川</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">石嘴山</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">吴忠</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">固原</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">中卫</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 63){
		
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">西宁</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="21">海东</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="22">海北</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="23">黄南</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="25">海南</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="26">果洛</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="27">玉树</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="28">海西</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 14){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">太原</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">大同</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">阳泉</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">长治</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">晋城</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">朔州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">晋中</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="8">运城</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="9">忻州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="10">临汾</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="23">吕梁</option>');
		
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 37){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">济南</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">青岛</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">淄博</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">枣庄</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">东营</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">烟台</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">潍坊</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="8">济宁</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="9">泰安</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="10">威海</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="11">日照</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="12">莱芜</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="13">临沂</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="14">德州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="15">聊城</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="16">滨州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="17">菏泽</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 31){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">黄浦区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">卢湾区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">徐汇区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">长宁区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">静安区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">普陀区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="8">闸北区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="9">虹口区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="10">杨浦区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="12">闵行区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="13">宝山区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="14">嘉定区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="15">浦东新区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="16">金山区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="17">松江区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="18">青浦区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="19">南汇区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="20">奉贤区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="30">崇明县</option>');
		
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 51){
		
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">成都</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">自贡</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">攀枝花</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">泸州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">德阳</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">绵阳</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="8">广元</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="9">遂宁</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="10">内江</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="11">乐山</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="13">南充</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="14">眉山</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="15">宜宾</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="16">广安</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="17">达州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="18">雅安</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="19">巴中</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="20">资阳</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="32">阿坝</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="33">甘孜</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="34">凉山</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 12){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">和平区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">河东区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">河西区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">南开区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">河北区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">红桥区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">塘沽区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="8">汉沽区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="9">大港区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="10">东丽区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="11">西青区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="12">津南区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="13">北辰区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="14">武清区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="15">宝坻区</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="21">宁河县</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="23">静海县</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="25">蓟县</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 54){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">拉萨</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="21">昌都</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="22">山南</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="23">日喀则</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="24">那曲</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="25">阿里</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="26">林芝</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 65){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">乌鲁木齐</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">克拉玛依</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="21">吐鲁番</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="22">哈密</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="23">昌吉</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="27">博尔塔拉</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="28">巴音郭楞</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="29">阿克苏</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="30">克孜勒苏</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="31">喀什</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="32">和田</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="40">伊犁</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="42">塔城</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="43">阿勒泰</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="44">石河子</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 53){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">昆明</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">曲靖</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">玉溪</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">保山</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">昭通</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="23">楚雄</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="25">红河</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="26">文山</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="27">思茅</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="28">西双版纳</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="29">大理</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="31">德宏</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="32">丽江</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="33">怒江</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="34">迪庆</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="35">临沧</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 33){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">杭州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">宁波</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">温州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">嘉兴</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">湖州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">绍兴</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">金华</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="8">衢州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="9">舟山</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="10">台州</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="11">丽水</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 61){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">西安</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">铜川</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">宝鸡</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">咸阳</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">渭南</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">延安</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">汉中</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="8">榆林</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="9">安康</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="10">商洛</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 71){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">台北</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">高雄</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">基隆</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">台中</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">台南</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">新竹</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">嘉义</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="90">其他</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 81){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">香港</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 82){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">澳门</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 400){
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1">美国</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="2">英国</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="3">法国</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="4">俄罗斯</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="5">加拿大</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="6">巴西</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="7">澳大利亚</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="8">印尼</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="9">泰国</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="10">马来西亚</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="11">新加坡</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="12">菲律宾</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="13">越南</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="14">印度</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="15">日本</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="16">其他</option>');
		
	}else if($(".dialog_searchtargetuser").find('.prov').val() == 100){
		
		
		$(".dialog_searchtargetuser").find('.city').html("");
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		$(".dialog_searchtargetuser").find('.city').append('<option value="1000">不限</option>');
		
	}
	
	
}
