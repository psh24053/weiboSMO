<%@page import="com.psh.query.model.AccountModel"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% 
AccountModel model = new AccountModel();

int status_88 = model.getUserCount(88);
int status_89 = model.getUserCount(89);
int status_90 = model.getUserCount(90);
int status_91 = model.getUserCount(91);
int status_old = model.getUserCount(1) + model.getUserCount(3) + model.getUserCount(4) + model.getUserCount(11) + model.getUserCount(33) + model.getUserCount(44); 
int accountCount = model.getAccount();


%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

	<ul>
		<li>激活成功账号：<%=status_91+status_89+status_90 %>  <a href="ExportActivation?type=1">导出</a></li>
		<li>等待激活账号：<%=status_88 %>  <a href="ExportActivation?type=2">导出</a></li>
		<li>老版账号（大部分被新浪封锁）：<%=status_old %>  <a href="ExportActivation?type=3">导出</a></li>
		<li>注册阶段成功的账号（大部分被新浪过滤）：<%=accountCount %>  <a href="ExportActivation?type=4">导出</a></li>
	</ul>



</body>
</html>