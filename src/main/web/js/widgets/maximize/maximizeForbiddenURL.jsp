<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
	<title></title>
</head>

<body>

<% 
String url = request.getParameter("url");
%> 
%{ms_forbiddenurlPage}<br>
<a href="<%= url %>" target="_blank">%{lb_openNewWindow}</a>
</body>
</html>