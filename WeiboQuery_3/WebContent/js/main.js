var AjaxData = {}; 
$(document).ready(function(){
	
	
	initStyle();
	initStatus();
	initEvent();
	initTable();
	loadCategoryInfo();
});
/**
 * 初始化状态
 */
function initStatus(){
	
	if(localStorage.weibo == undefined || localStorage.weibo == null){
		localStorage.weibo = new Date();
		// 初始化更新资料目前的状态
		localStorage.ModifyGroup = 'none';
		
		return;
	}
	
	
}
/**
 * 初始化界面样式
 */
function initStyle(){
	
	$('button').button();
	$('.list_menu').accordion({
	      icons: {
		      header: "ui-icon-circle-arrow-e",
		      activeHeader: "ui-icon-circle-arrow-s"
		  }
    });
	$('#tabs').tabs();
}
/**
 * 初始化表格
 */
function initTable(){
	// 更新资料
	$('#tabs_1_table').jqGrid({
		datatype: "local",
		height: 420,
		colNames:['uid','邮箱','昵称','省份','城市','性别','生日','简介','标签','状态','dataStore'],
	   	colModel:[
	   		{name:'uid',index:'uid', width:40, align:'center', sortable:false},
	   		{name:'email',index:'email', width:80, align:'center', sortable:false},
	   		{name:'nck',index:'nck', width:40, align:'center', sortable:false},
	   		{name:'prov',index:'prov', width:40, align:"right",sortable:false},		
	   		{name:'city',index:'city', width:40,align:"right",sortable:false},		
	   		{name:'gender',index:'gender', width:40, align:'center',sortable:false},
	   		{name:'birthday',index:'birthday', width:50,sortable:false},
	   		{name:'description',index:'description', width:100,sortable:false, align:'center'},
	   		{name:'tags',index:'tags', width:100,sortable:false, align:'center'},
	   		{name:'status',index:'status', width:40,sortable:false, align:'center'},
	   		{name:'dataStore',index:'dataStore',hidden:true}
	   	],
	   	multiselect: true,
   		rowNum: 50,
   	   	pager: "#tabs_1_pager",
   	 	viewrecords: true,
   	   	width: $('#tabs_1').width(),
   	 	toolbar: [true,"top"],
   	 	loadComplete: function(){
   	 		initModifyInfo_Toolbar();
   	 	}
	});
	
	// 发送微博
	$('#tabs_2_table').jqGrid({
		datatype: "local",
		height: 420,
		colNames:['uid','邮箱','分组','目标','微博内容','状态','dataStore'],
	   	colModel:[
	   		{name:'uid',index:'uid', width:40, align:'center', sortable:false},
	   		{name:'email',index:'email', width:40, align:'center', sortable:false},
	   		{name:'group',index:'group', width:60, align:'center', sortable:false},
	   		{name:'target',index:'target', width:40, align:'center', sortable:false},
	   		{name:'content',index:'content', width:40, align:"right",sortable:false},		
	   		{name:'status',index:'status', width:40,align:"right",sortable:false},		
	   		{name:'dataStore',index:'dataStore',hidden:true}
	   	],
	   	multiselect: true,
   		rowNum: 50,
   	   	pager: "#tabs_2_pager",
   	 	viewrecords: true,
   	   	width: $('#tabs_1').width(),
   	 	toolbar: [true,"top"],
   	 	loadComplete: function(){
   	 		
   	 	}
	});
	
}
/**
 * 初始化更新资料的toolbar
 */
function initModifyInfo_Toolbar(){
	
	var toolbar = $('#t_tabs_1_table');
	
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
	toolbar.append(execute);
}
/**
 * 上传excel资料文件
 */
function onClick_uploadExcel_modify(){
	
	$('.dialog_uploadexcel').find('form').attr('action','UploadExcel?idx='+random_char(8));
	$('.dialog_uploadexcel').find('input[type=button]').click(function(){
		if($('.dialog_uploadexcel input[type=file]').val() != ''){
			var wait = new weibo.WaitAlert('正在上传');
			wait.show();
			$('.dialog_uploadexcel').data('wait',wait);
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
 * 生产随机字符串
 * @param l
 * @returns {String}
 */
function  random_char(l)  {
	var  x="0123456789qwertyuioplkjhgfdsazxcvbnm";
	var  tmp="";
	for(var  i=0;i<  l;i++)  {
	  tmp  +=  x.charAt(Math.ceil(Math.random()*100000000)%x.length);
	}
	
	return  tmp;
}
/**
 * 上传完成后触发
 * @param idx
 */
function onUploadExcelComplete(idx){
	$('.dialog_uploadexcel').dialog('close');
	$('.dialog_uploadexcel').data('wait').close();
	
	weibo.Action_3020_GetExcelContent(idx,function(data){
		
		if(data.res){
			
			var list = data.pld.list;
			var rowData = $('#tabs_1_table').jqGrid('getRowData');
			for(var i = 0 ; i < rowData.length ; i ++){
				var item = list[i];
				if(item == undefined){
					continue;
				}
				$('#tabs_1_table').jqGrid('setRowData',i, {
					'status': '准备就绪',
					'nck': item.nck,
					'prov': item.prov,
					'gender': item.gender,
					'birthday': item.birthday,
					'description': item.info,
					'tags': item.tags
				});
			}
			
		}else{
			alert('解析Excel失败');
		}
		
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
		$('#tabs_1_table').jqGrid('setRowData',i, {'status':''});
	}
	
	
	readInfo(size, rowData, p_this);
	
//	var interval = setInterval(function(){
//		var i = size;
//		var item = rowData[i];
//		$('#tabs_1_table').jqGrid('setRowData',i, {'status':'正在读取...'});
//		weibo.Action_3019_GetAccountInfoFromSina(item.uid, function(data){
//			if(data.res){
//				$('#tabs_1_table').jqGrid('setRowData',i, {
//					'status': '读取成功',
//					'nck': data.pld.nck,
//					'prov': data.pld.prov,
//					'gender': data.pld.gender,
//					'birthday': data.pld.birthday,
//					'description': data.pld.info,
//					'tags': data.pld.tags
//				});
//				
//			}else{
//				$('#tabs_1_table').jqGrid('setRowData',i, {'status':'读取失败'});
//			}
//		},function(){
//			$('#tabs_1_table').jqGrid('setRowData',i, {'status':'读取失败'});
//		});
//		
//		
//		size ++;
//		if(rowData.length == size){
//			clearInterval(interval);
//			p_this.parent().find('button').button('enable');
//		}
//	}, 1000);
	
	
}
function readInfo(size,rowData,p_this){
		var i = size;
		var item = rowData[i];
		$('#tabs_1_table').jqGrid('setRowData',i, {'status':'正在读取...'});
		weibo.Action_3019_GetAccountInfoFromSina(item.uid, function(data){
			if(data.res){
				$('#tabs_1_table').jqGrid('setRowData',i, {
					'status': '读取成功',
					'nck': data.pld.nck,
					'prov': data.pld.prov,
					'gender': data.pld.gender,
					'birthday': data.pld.birthday,
					'description': data.pld.info,
					'tags': data.pld.tags
				});
				
			}else{
				$('#tabs_1_table').jqGrid('setRowData',i, {'status':'读取失败'});
			}
			if(rowData.length == size+1){
				p_this.parent().find('button').button('enable');
			}else{
				size++;
				readInfo(size, rowData, p_this);
			}
		},function(){
			$('#tabs_1_table').jqGrid('setRowData',i, {'status':'读取失败'});
			if(rowData.length == size+1){
				p_this.parent().find('button').button('enable');
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
				$('#tabs_1_table').jqGrid('addRowData', i, item);
				
			}
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
	    		
	    		cgroup.text('当前分组：'+group.text()).attr('gid',group.val());
	    		localStorage.ModifyGroup = 'run';
	    		localStorage.ModifyGroupName = group.text();
	    		localStorage.ModifyGroupGid = group.val();
	    		
	    		$(this).dialog('close');
	    	}
	    }
	    
	});
	
	
}
/**
 * 初始化事件
 */
function initEvent(){
	$('.list_header_addGroup').click(function(){
		addGroup();
	});
	$('.list_header_addCategory').click(function(){
		addCategory();
	});
}
/**
 * 添加分类
 */
function addCategory(){
	$('.dialog_addCategory').dialog({
		modal: true,
		title: '添加分组',
		resizable: false,
		width: $(document).width() * 0.3,
	    height: $(document).height() * 0.5,
	    close: function(event, ui){
	    },
	    buttons: {
	    	'添加': function(){
	    		var name = $('.dialog_addCategory .category_name').val();
	    		var desc = $('.dialog_addCategory .category_desc').val();
	    		weibo.Action_3001_AddCategory(name, desc, function(data){
	    			
	    			if(data.res){
	    				
	    				alert('添加分类成功！');
	    				loadCategoryInfo();
	    			}
	    			$('.dialog_addCategory').dialog('close');
	    		});
	    		
	    	}
	    }
	});
}
/**
 * 添加分组
 */
function addGroup(){
	weibo.Action_3003_GetCategoryList(function(data){
		if(data.res){
			var list = data.pld.list;
			var select = $('.dialog_addGroup .category_select');
			
			for(var i = 0 ; i < list.length ; i ++){
				var item = list[i];
				var option = '<option value="'+item.cid+'">'+item.name+'</option>';
				select.append(option);
			}
			
			
			$('.dialog_addGroup').dialog({
				modal: true,
				title: '添加分组',
				resizable: false,
				width: $(document).width() * 0.3,
			    height: $(document).height() * 0.5,
			    close: function(event, ui){
			    	var select = $('.dialog_addGroup .category_select');
					select.html('');
			    },
			    buttons: {
			    	'添加': function(){
			    		var cid = $('.dialog_addGroup .category_select').val();
			    		var name = $('.dialog_addGroup .group_name').val();
			    		
			    		weibo.Action_3002_AddGroup(cid, name, function(data){
			    			
			    			if(data.res){
			    				
			    				alert('添加分组成功！');
			    				loadCategoryInfo();
			    			}
			    			$('.dialog_addGroup').dialog('close');
			    			
			    			
			    		});
			    	}
			    }
			});
			
		}
		
		
	});
	
	
	
}


/**
 * 加载分类信息
 */
function loadCategoryInfo(){
	weibo.Action_3003_GetCategoryList(function(data){
		
		if(data.res){
			$('.list_menu').html('');
			var list = data.pld.list;
			AjaxData.categoryList = list;
			
			for(var i = 0 ; i < list.length ; i ++){
				var item = list[i];
				
				var h3 = $('<h3></h3>').attr('cid',item.cid).text(item.name).data('data',item);
				var div = $('<div></div>').css('padding','5px').attr('cid',item.cid);
				
				$('.list_menu').append(h3).append(div);
			}
			
			// 重载list_menu
			reloadList();
		}
		
		
	},function(err){
		alert(err);
	});
	
	
}
/**
 * 重载list组件
 */
function reloadList(){
	$('.list_menu').accordion("destroy");
	$('.list_menu').accordion({
	      icons: {
		      header: "ui-icon-circle-arrow-e",
		      activeHeader: "ui-icon-circle-arrow-s"
		  },
		  create: function(){
			  loadFirstH3();
			  
		  },
		  activate: function(event, ui){
			  
			  loadH3Content($(ui.newHeader), $(ui.newPanel),$(ui.newHeader).attr('cid'));
		  }
    });
}
/**
 * 加载指定cid的h3
 * @param cid
 */
function loadH3Content(h3, div, cid){
	weibo.Action_3004_GetGroupList(cid, function(data){
		  
		  if(data.res){
			  
			  var list = data.pld.list;
			  div.html('');
			  var height = 0;
			  for(var i = 0 ; i < list.length ; i ++){
				  var item = list[i];
				  
				  var group_item = $('<div></div>').attr('gid',item.gid).data('data',item).addClass('group_item');
				  
				  var group_name = $('<span></span>').text(item.name).addClass('group_name');
				  var group_status = $('<span></span>').text('('+item.status+')').addClass('group_status');
				  var group_usercount = $('<span></span>').text('用户:'+item.count).addClass('group_usercount');
				  var group_action = $('<span></span>').addClass('group_action');
				  
				  var action_modify = $('<a gid="'+item.gid+'" class="group_action_add" href="javascript:void(0)">改</a>');
				  var action_delete = $('<a gid="'+item.gid+'" class="group_action_delete" href="javascript:void(0)">删</a>');
				  
				  action_modify.click(function(){
					  var p_this = $(this);
					  weibo.Propmt('请输入要设置的用户数量',item.count,'设置用户数量',function(data){
						  var value = 0;
						  try {
							value = parseInt(data);
							
						} catch (e) {
							alert('请输入数字');
						}
						weibo.Action_3010_SetGroupUser(p_this.attr('gid'), value, function(result){
							if(result.res){
								alert('设置用户数量成功');
								
								loadCategoryInfo();
								
							}
						});
					  });
				  });
				  
				  action_delete.click(function(){
					  weibo.Confirm('是否要删除这个分组？',null,function(result){
						  if(result){
							  weibo.Action_3018_deleteGroup(item.gid,function(r){
								  if(r.res){
										alert('删除分组成功');
										
										loadCategoryInfo();
										
									}
							  });
							  
						  }
					  });
				  });
				  
				  
				  group_action.append(action_modify).append(action_delete);
				  
				  group_item.append(group_name).append(group_status).append(group_usercount).append(group_action);
				  
				  div.append(group_item);
				  height += group_item.height();
			  }
			  div.css('height','auto');
		  }
		  
		  
		  
		  
	    });
	
}

/**
 * 加载第一个list元素的内容
 */
function loadFirstH3(){
	var first_h3 = $('.list_menu h3:first');
	var cid = first_h3.data('data').cid;
	var first_div = $('.list_menu div[cid='+cid+']');
    weibo.Action_3004_GetGroupList(cid, function(data){
	  
	  if(data.res){
		  
		  var list = data.pld.list;
		  first_div.html('');
		  var height = 0;
		  for(var i = 0 ; i < list.length ; i ++){
			  var item = list[i];
			  
			  var group_item = $('<div></div>').addClass('group_item');
			  
			  var group_name = $('<span></span>').text(item.name).addClass('group_name');
			  var group_status = $('<span></span>').text('('+item.status+')').addClass('group_status');
			  var group_usercount = $('<span></span>').text('用户:'+item.count).addClass('group_usercount');
			  var group_action = $('<span></span>').addClass('group_action');
			  
			  var action_modify = $('<a gid="'+item.gid+'" class="group_action_add" href="javascript:void(0)">改</a>');
			  var action_delete = $('<a gid="'+item.gid+'" class="group_action_delete" href="javascript:void(0)">删</a>');
			  
			  action_modify.click(function(){
				  var p_this = $(this);
				  weibo.Propmt('请输入要设置的用户数量',item.count,'设置用户数量',function(data){
					  var value = 0;
					  try {
						value = parseInt(data);
						
					} catch (e) {
						alert('请输入数字');
					}
					weibo.Action_3010_SetGroupUser(p_this.attr('gid'), value, function(result){
						if(result.res){
							alert('设置用户数量成功');
							
							loadCategoryInfo();
							
						}
					});
				  });
			  });
			  
			  action_delete.click(function(){
				  weibo.Confirm('是否要删除这个分组？',null,function(result){
					  if(result){
						  weibo.Action_3018_deleteGroup(item.gid,function(r){
							  if(r.res){
									alert('删除分组成功');
									
									loadCategoryInfo();
									
								}
						  });
						  
					  }
				  });
			  });
			  
			  
			  group_action.append(action_modify).append(action_delete);
			  
			  group_item.append(group_name).append(group_status).append(group_usercount).append(group_action);
			  
			  first_div.append(group_item);
			  height += group_item.height();
		  }
		  first_div.css('height','auto');
	  }
	  
	  
	  
	  
    });
}
