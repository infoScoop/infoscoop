<%--
# infoScoop Calendar
# Copyright (C) 2010 Beacon IT Inc.
# 
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see
# <http://www.gnu.org/licenses/old-licenses/gpl-2.0.html>.
--%>

<!DOCTYPE HTML>
<%@ page contentType="text/html; charset=UTF8" %>
<%@ page import="org.infoscoop.service.PortalLayoutService, org.infoscoop.util.*" %>
<%@page import="org.infoscoop.service.PropertiesService"%>
<%
	String pageTitle = PortalLayoutService.getHandle().getPortalLayout("title");
	pageTitle = I18NUtil.resolve(I18NUtil.TYPE_LAYOUT, pageTitle, request.getLocale());
	I18NUtil i18n = new I18NUtil(I18NUtil.TYPE_JS, request.getLocale());
	String staticContentURL = PropertiesService.getHandle().getProperty("staticContentURL");
%>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
	    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
		<title><%= pageTitle %> <%= i18n.msg("lb_error") %></title>
		<style type="text/css">
			body{
				margin-top:0px;
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
		<div><img src="<%=staticContentURL%>/skin/imgs/error_face.gif"></div>
		<div class="msg"><%=i18n.msg("ms_sorry")%></div>
		<div class="detail"><%= i18n.msg((String)request.getAttribute("error_msg_id")) %></div>
	</body>
</html>
