/**
 * Alert组件
 */
weibo.Alert = function(str, tit, onResult){
	
	if(tit == null || tit == undefined){
		tit = '提示';
	}
	
	
	$('<div></div>').css('text-align','center').css('width','100%').css('margin-top','10px').html(str).dialog({
	      modal: true,
	      buttons: {
	        确定: function() {
	          $( this ).dialog( "close" );
	          if(onResult != null){
	        	  onResult();
	          }
	        }
	      },
	      title: tit,
	      resizable: false
	});
	
};
/**
 * Confirm组件
 */
weibo.Confirm = function(str, tit, onResult){
	
	if(tit == null || tit == undefined){
		tit = '询问';
	}
	
	
	$('<div></div>').css('text-align','center').css('width','100%').css('margin-top','10px').html(str).dialog({
	      modal: true,
	      buttons: {
	        确定: function() {
	          $( this ).dialog( "close" );
	          if(onResult != null){
	        	  onResult(true);
	          }
	        },
	        取消: function() {
		      $( this ).dialog( "close" );
		      if(onResult != null){
	        	  onResult(false);
	          }
		    }
	      },
	      title: tit,
	      resizable: false
	});
	
};
/**
 * 等待窗口组件
 */
weibo.WaitAlert = function(str, tit, id){
	
	
	if(tit == null || tit == undefined){
		tit = '请稍候';
	}
	if(str == null || str == undefined){
		str = '正在加载...';
	}
	
	this.dialog = null;
	var date = new Date();
	var value = ""+date.getFullYear()+(date.getMonth()+1)+date.getDate()+date.getHours()+date.getSeconds()+date.getMinutes()+date.getMilliseconds();
	
	this.times = value;
	var p_this = this;
	this.dom = $('<div></div>').addClass('waitAlert').data('close',function(){
		$('.waitAlert').dialog('close');
		$('.waitAlert').remove();
	});
	
	if(id != null && id != undefined){
		this.dom.attr('id',id);
	}
	
	this.show = function(){
		
		this.dom.attr('times',this.times).css('text-align','center').css('width','100%').css('margin-top','10px').html(str+"<br /><br /><img src='Image/ajax-loader.gif'>").dialog({
			modal: true,
			title: tit,
			resizable: false,
			closeOnEscape: false
		});
		
		$('.ui-dialog-title:contains('+tit+')').parent().remove();
	};
	
	this.setText = function(txt){
		this.dom.text(txt);
	};
	
	this.close = function(){
		
		$('div[times='+this.times+']').dialog( "close" );
		$('div[times='+this.times+']').remove();
	};
};
/**
 * Propmt组件
 */
weibo.Propmt = function(text, hint, tit, onResult){
	
	if(hint == null || hint == undefined){
		hint = '';
	}
	if(tit == null || tit == undefined){
		tit = '请输入';
	}
	
	var jqdom = $('<div></div>').css('text-align','center').css('width','100%').css('margin-top','10px');
	jqdom.append($('<div></div>').html(text));
	jqdom.append($('<input type="text" />').val(hint));
	
	jqdom.dialog({
	      modal: true,
	      buttons: {
	        确定: function() {
	          $( this ).dialog( "close" );
	          var value = jqdom.find('input').val();
	          
	          if(onResult != null){
	        	  onResult(value);
	          }
	        },
	        取消: function() {
		      $( this ).dialog( "close" );
		      
		    }
	      },
	      title: tit,
	      resizable: false
	});
	
};

