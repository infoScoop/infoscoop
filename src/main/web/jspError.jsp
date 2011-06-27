<%--
# infoScoop OpenSource
# Copyright (C) 2010 Beacon IT Inc.
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Lesser General Public License version 3
# as published by the Free Software Foundation.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Lesser General Public License for more details.
# 
# You should have received a copy of the GNU Lesser General Public
# License along with this program.  If not, see
# <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page contentType="text/html; charset=UTF8" isErrorPage="true" %>
<%@page import="java.io.StringWriter" %>
<%@page import="java.io.PrintWriter" %>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<title>jspError.sorry</title>
		<style type="text/css">
			body{
				margin-top:0;
				padding-top:50px;
				text-align:center;
				background-color:#E6F3FE;
				color:#666;
			}
			div.msg{
				margin-top: 20px;
				font-size: 20px;
				font-weight: bold;
			}
			div.detail{
				margin-top: 20px;
			}
		</style>
	</head>
	<body>
		<div><img src="/portal/skin/imgs/error_face.gif"></div>
		<div class="msg">jspError.sorry</div>
		<div class="detail">
		<%
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			exception.printStackTrace(pw);
			out.println(sw.toString());
		 %>
		 </div>
	</body>
</html>
