<%@page import="com.psh.query.model.AccountModel"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% 
AccountModel model = new AccountModel();

int status_88 = model.getUserCountActivation(88);
int status_89 = model.getUserCountActivation(89);
int status_90 = model.getUserCountActivation(90);
int status_91 = model.getUserCountActivation(91);
int status_old = model.getUserCountActivation(1) + model.getUserCountActivation(3) + model.getUserCountActivation(4) + model.getUserCountActivation(11) + model.getUserCountActivation(33) + model.getUserCountActivation(44); 
int accountCount = model.getRegAccount();

int dbusercount = model.getDBUserCount();
int dbgroupusercount = model.getDBGroupUserCount();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<!-- JQuery UI JSLib -->
<script type="text/javascript" src="json2.js"></script>
<script type="text/javascript" src="jquery-1.8.1.min.js"></script>
<script type="text/javascript" src="jqueryui/js/jquery-ui-1.9.2.custom.min.js"></script>
<!-- JqGrid lib -->
<script type="text/javascript" src="js/ajax.js"></script>

<link rel="stylesheet" type="text/css" href="jqueryui/css/smoothness/jquery-ui-1.9.2.custom.min.css" />
<link type="text/css" rel="stylesheet" href="css/ui.jqgrid.css" />

<script type="text/javascript" src="js/weibo.Dialog.js"></script>
<script type="text/javascript">
window.onload = function(){
	var refresh = document.getElementById("refresh");
	refresh.onclick = function(){
		location.reload();
	};
	var update = document.getElementById("update");
	update.onclick = function(){
		if(confirm('你确定要同步吗？这可能需要几秒或者是几分钟！')){
			var wait = new weibo.WaitAlert('正在同步...');
			wait.show();
			weibo.Action_3022_AccountSynchronization(function(){
				wait.close();
				alert('同步成功,点击确定后刷新界面');
				location.reload();
			},function(){
				wait.close();
				alert('同步失败');
			});
		}
	};
};

</script>
</head>
<body>

	<ul>
		<li>激活成功账号：<%=status_91+status_89+status_90 %>  <a href="ExportActivation?type=1">导出</a></li>
		<li>等待激活账号：<%=status_88 %>  <a href="ExportActivation?type=2">导出</a></li>
		<li>老版账号（大部分被新浪封锁）：<%=status_old %>  <a href="ExportActivation?type=3">导出</a></li>
		<li>注册阶段成功的账号（大部分被新浪过滤）：<%=accountCount %>  <a href="ExportActivation?type=4">导出</a></li>
	</ul>

	<div>可用于分组的账号：<%=dbusercount+dbgroupusercount %>(当账号激活成功过，不会自动变为可用于分组的账号，需要手动同步)</div>
	<div>未分组账号：<%=dbusercount %></div>
	<div>已分组账号：<%=dbgroupusercount %></div>
	<button id="refresh">刷新</button>
	<button id="update">同步账号</button>
</body>
</html>