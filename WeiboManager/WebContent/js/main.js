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
		// 初始化LocalStorage目前的状态
		localStorage.ModifyGroup = 'none';
		localStorage.SendWeiboGroup = 'none';
		localStorage.ForwardGroup = 'none';
		localStorage.AttentionGroup = 'none';
		localStorage.TomeGroup = 'none';
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
	$('#tabs').tabs({
		activate: function(event, ui){
			
			switch (ui.newPanel.selector) {
			case '#tabs_7':
				if($(ui.newPanel).find('iframe').size() == 0){
					$(ui.newPanel).html('<iframe src="activation.jsp" width="100%" height="300px" style="border: none;"></iframe>');
				}
				
				break;
			case '#tabs_8':	
				if($(ui.newPanel).find('iframe').size() == 0){
					$(ui.newPanel).html('<iframe src="sourceManager.jsp" width="100%" height="300px" style="border: none;"></iframe>');
				}
				break;

			default:
				break;
			}
			
		}
	});
}
/**
 * 初始化表格
 */
function initTable(){
	// 更新资料
	$('#tabs_1_table').jqGrid({
		datatype: "local",
		height: 420,
		colNames:['操作','uid','邮箱','昵称','省份','城市','性别','生日','简介','标签','状态','dataStore'],
	   	colModel:[
	   	    {name:'actions',index:'actions', width:60, align:'center', sortable:false},
	   		{name:'uid',index:'uid', width:40, align:'center', sortable:false},
	   		{name:'email',index:'email', width:80, align:'center', sortable:false},
	   		{name:'nck',index:'nck', width:40, align:'center', sortable:false,editable:true},
	   		{name:'prov',index:'prov', width:40, align:"right",sortable:false},		
	   		{name:'city',index:'city', width:40,align:"right",sortable:false},		
	   		{name:'gender',index:'gender', width:40, align:'center',sortable:false,editable:true},
	   		{name:'birthday',index:'birthday', width:50,sortable:false,editable:true},
	   		{name:'description',index:'description', width:100,sortable:false, align:'center',editable:true},
	   		{name:'tags',index:'tags', width:100,sortable:false, align:'center',editable:true},
	   		{name:'status',index:'status', width:40,sortable:false, align:'center'},
	   		{name:'dataStore',index:'dataStore',hidden:true}
	   	],
	   	rownumbers:true,
   		rowNum: 50,
   	   	pager: "#tabs_1_pager",
   	    editurl: 'main.html',
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
		colNames:['uid','邮箱','目标','微博内容','状态','dataStore'],
	   	colModel:[
	   		{name:'uid',index:'uid', width:20, align:'center', sortable:false},
	   		{name:'email',index:'email', width:40, align:'center', sortable:false},
	   		{name:'target',index:'target', width:40 , sortable:false},
	   		{name:'content',index:'content', width:40,sortable:false},		
	   		{name:'status',index:'status', width:40,align:"right",sortable:false},		
	   		{name:'dataStore',index:'dataStore',hidden:true}
	   	],
	   	rownumbers:true,
   		rowNum: 50,
   	   	pager: "#tabs_2_pager",
   	 	viewrecords: true,
   	   	width: $('#tabs_1').width(),
   	 	toolbar: [true,"top"],
   	 	loadComplete: function(){
   	 		initSendMessage_Toolbar();
   	 	}
	});
	
	// 转发消息
	$('#tabs_3_table').jqGrid({
		datatype: "local",
		height: 420,
		colNames:['uid','邮箱','转发目标','转发理由','状态','dataStore'],
	   	colModel:[
	   		{name:'uid',index:'uid', width:20, align:'center', sortable:false},
	   		{name:'email',index:'email', width:40, align:'center', sortable:false},
	   		{name:'target',index:'target', width:40 , sortable:false},
	   		{name:'content',index:'content', width:40,sortable:false},		
	   		{name:'status',index:'status', width:40,align:"right",sortable:false},		
	   		{name:'dataStore',index:'dataStore',hidden:true}
	   	],
	   	rownumbers:true,
   		rowNum: 50,
   	   	pager: "#tabs_3_pager",
   	 	viewrecords: true,
   	   	width: $('#tabs_1').width(),
   	 	toolbar: [true,"top"],
   	 	loadComplete: function(){
   	 		initForward_Toolbar();
   	 	}
	});
	
	// 关注他人
	$('#tabs_4_table').jqGrid({
		datatype: "local",
		height: 420,
		colNames:['uid','邮箱','关注目标','状态','dataStore'],
	   	colModel:[
	   		{name:'uid',index:'uid', width:20, align:'center', sortable:false},
	   		{name:'email',index:'email', width:40, align:'center', sortable:false},
	   		{name:'target',index:'target', width:40 , sortable:false},
	   		{name:'status',index:'status', width:40,align:"right",sortable:false},		
	   		{name:'dataStore',index:'dataStore',hidden:true}
	   	],
	   	rownumbers:true,
   		rowNum: 50,
   	   	pager: "#tabs_4_pager",
   	 	viewrecords: true,
   	   	width: $('#tabs_1').width(),
   	 	toolbar: [true,"top"],
   	 	loadComplete: function(){
   	 		initAttention_Toolbar();
   	 	}
	});
	
	// 回复功能
	$('#tabs_5_table').jqGrid({
		datatype: "local",
		height: 420,
		colNames:['uid','邮箱','最新消息','状态','lastmid','cache'],
	   	colModel:[
	   		{name:'uid',index:'uid', width:20, align:'center', sortable:false},
	   		{name:'email',index:'email', width:40, align:'center', sortable:false},
	   		{name:'newmsg',index:'newmsg', width:40 , sortable:false},
	   		{name:'status',index:'status', width:40,align:"right",sortable:false},		
	   		{name:'lastmid',index:'lastmid',hidden:true},
	   		{name:'cache',index:'cache',hidden:true}
	   	],
	   	rownumbers:true,
   		rowNum: 50,
   	   	pager: "#tabs_5_pager",
   	 	viewrecords: true,
   	   	width: $('#tabs_1').width(),
   	 	toolbar: [true,"top"],
   	 	loadComplete: function(){
   	 		initTome_Toolbar();
   	 	}
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
				  
				  var action_modify = $('<a gid="'+item.gid+'" class="group_action_modify" href="javascript:void(0)">改</a>');
				  var action_delete = $('<a gid="'+item.gid+'" class="group_action_delete" href="javascript:void(0)">删</a>');
				  var action_adduser = $('<a gid="'+item.gid+'" class="group_action_adduser" href="javascript:void(0)">导入</a>');
				  var action_manager = $('<a gid="'+item.gid+'" class="group_action_manager" href="javascript:void(0)">管理</a>');
				     
				  action_manager.click(onClick_GroupUserManager);
				  
				  
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
				  
				  
				  
				  action_adduser.click(function(){
					  $('.dialog_addusergroup').dialog({
						  	modal: true,
							title: '向分组中添加用户',
							resizable: false,
							width: $(document).width() * 0.3,
						    height: $(document).height() * 0.5,
						    open: function(){
						    	$('.dialog_addusergroup').find('.group_name').text(item.name);
						    },
						    buttons: {
						    	'添加': function(){
						    		var uid = $('.dialog_addusergroup').find('.uid').val();
						    		var email = $('.dialog_addusergroup').find('.email').val();
						    		var password = $('.dialog_addusergroup').find('.password').val();
						    		var gid = item.gid;
						    		
						    		weibo.Action_3025_AddUserByGroup({
						    			uid: uid,
						    			email: email,
						    			password: password,
						    			gid: gid
						    		},function(data){
						    			if(data.res){
						    				alert('添加成功');
						    				$('.dialog_addusergroup').dialog('close');
						    				loadCategoryInfo();
						    			}else{
						    				alert('添加失败');
						    				$('.dialog_addusergroup').dialog('close');
						    			}
						    		},function(err){
						    			alert('添加失败');
						    			console.debug(err);
						    		});
						    		
						    	}
						    }
					  });
				  });
				  
				  group_action.append(action_delete).append(action_manager);
				  
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
			  
			  var group_item = $('<div></div>').addClass('group_item').data('item',item);
			  
			  var group_name = $('<span></span>').text(item.name).addClass('group_name');
			  var group_status = $('<span></span>').text('('+item.status+')').addClass('group_status');
			  var group_usercount = $('<span></span>').text('用户:'+item.count).addClass('group_usercount');
			  var group_action = $('<span></span>').addClass('group_action');
			  
			  var action_modify = $('<a gid="'+item.gid+'" class="group_action_modify" href="javascript:void(0)">改</a>');
			  var action_delete = $('<a gid="'+item.gid+'" class="group_action_delete" href="javascript:void(0)">删</a>');
			  var action_adduser = $('<a gid="'+item.gid+'" class="group_action_adduser" href="javascript:void(0)">导入</a>');
			  var action_manager = $('<a gid="'+item.gid+'" class="group_action_manager" href="javascript:void(0)">管理</a>');
			     
			  action_manager.click(onClick_GroupUserManager);
			  
			  
			  
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
			  
			  action_adduser.click(function(){
				  $('.dialog_addusergroup').dialog({
					  	modal: true,
						title: '向分组中添加用户',
						resizable: false,
						width: $(document).width() * 0.3,
					    height: $(document).height() * 0.5,
					    open: function(){
					    	$('.dialog_addusergroup').find('.group_name').text(item.name);
					    },
					    buttons: {
					    	'添加': function(){
					    		var uid = $('.dialog_addusergroup').find('.uid').val();
					    		var email = $('.dialog_addusergroup').find('.email').val();
					    		var password = $('.dialog_addusergroup').find('.password').val();
					    		var gid = item.gid;
					    		
					    		weibo.Action_3025_AddUserByGroup({
					    			uid: uid,
					    			email: email,
					    			password: password,
					    			gid: gid
					    		},function(data){
					    			if(data.res){
					    				alert('添加成功');
					    				$('.dialog_addusergroup').dialog('close');
					    				loadCategoryInfo();
					    			}else{
					    				alert('添加失败');
					    				$('.dialog_addusergroup').dialog('close');
					    			}
					    		},function(err){
					    			alert('添加失败');
					    			console.debug(err);
					    		});
					    		
					    	}
					    }
				  });
			  });
			  
			  group_action.append(action_delete).append(action_manager);
			 
			  group_item.append(group_name).append(group_status).append(group_usercount).append(group_action);
			  
			  first_div.append(group_item);
			  height += group_item.height();
		  }
		  first_div.css('height','auto');
	  }
	  
	  
	  
	  
    });
}
/**
 * 分组用户管理
 */
function onClick_GroupUserManager(){
	var p_This = $(this);
	var gid = $(this).attr('gid');
	var group = $(this).parent().parent().data('item');
	$('.dialog_groupusermanager').dialog({
		modal: true,
		title: '分组用户管理 ID:'+gid,
		resizable: false,
		width: $(document).width() * 0.7,
	    height: $(document).height() * 0.7,
	    create: function(){
	    	$('#dialog_groupusermanager_table').jqGrid({
	    		datatype: "local",
	    		height: $(document).height() * 0.5,
	    		colNames:['操作','uid','邮箱','昵称','状态'],
	    	   	colModel:[
	   	            {name:'actions',index:'actions', width:30, align:'center', sortable:false},
	    	   		{name:'uid',index:'uid', width:30, align:'center', sortable:false},
	    	   		{name:'email',index:'email', width:30, align:'center', sortable:false},
	    	   		{name:'nck',index:'nck', width:30, align:'center', sortable:false},
	    	   		{name:'status',index:'status', width:30, align:'center', sortable:false}
	    	   	],
	    	   	rownumbers:true,
	       		rowNum: 50,
	       	   	pager: "#dialog_groupusermanager_pager",
	       	 	viewrecords: true,
	       	   	width: $(document).width() * 0.67,
	       	   	toolbar: [true,'top'],
	       	 	loadComplete: function(){
	       	 		var toolbar = $('#t_dialog_groupusermanager_table');
	       	 		toolbar.css('height','auto').css('padding','5px').css('padding-right','-5px');
	       	 		
		       	 	var setGroupUser = $('<button></button>').text('设置成员').button().attr('title','设置该分组的成员数量');
		       	 	setGroupUser.click(function(){
		       	 		onClick_setGroupUser(gid, group);
		       	 	});
		       		toolbar.append(setGroupUser);
	       	 		
	       	 	}
	    	});
	    },
	    open: function(){
	    	var wait = new weibo.WaitAlert('正在加载...');
	    	wait.show();
	    	weibo.Action_3011_GetGroupUserList(gid, function(data){
	    		if(data.res){
	    			var list = data.pld.list;
	    			for(var i = 0 ; i < list.length ; i ++){
	    				var item = list[i];
	    				item.description = item.info;
	    			}
	    			$('#dialog_groupusermanager_table').jqGrid('clearGridData');
	    			$('#dialog_groupusermanager_table').jqGrid('addRowData', 'uid', list);
	    		}
	    		wait.close();
	    		
	    		console.debug(data);
	    	},function(){
	    		wait.close();
	    	});
	    	
	    	
	    }
	});
	
	
}
/**
 * 设置分组用户
 * @param gid
 * @param group
 */
function onClick_setGroupUser(gid, group){
    weibo.Propmt('请输入要设置的用户数量',group.count,'设置用户数量',function(data){
    	var value = 0;
	  	try {
		value = parseInt(data);
			
	  	} catch (e) {
			alert('请输入数字');
		}	
		weibo.Action_3010_SetGroupUser(gid, value, function(result){
			if(result.res){
				alert('设置用户数量成功');
				loadCategoryInfo();
				var wait = new weibo.WaitAlert('正在加载...');
		    	wait.show();
		    	weibo.Action_3011_GetGroupUserList(gid, function(data){
		    		if(data.res){
		    			var list = data.pld.list;
		    			for(var i = 0 ; i < list.length ; i ++){
		    				var item = list[i];
		    				item.description = item.info;
		    			}
		    			$('#dialog_groupusermanager_table').jqGrid('clearGridData');
		    			$('#dialog_groupusermanager_table').jqGrid('addRowData', 'uid', list);
		    		}
		    		wait.close();
		    		
		    		console.debug(data);
		    	},function(){
		    		wait.close();
		    	});
			}
		});
  });
}
