$(document).ready(function(){
	
	
	initStyle();
	initEvent();
	loadCategoryInfo();
});
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
	    height: $(document).height() * 0.27,
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
			    height: $(document).height() * 0.7,
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
