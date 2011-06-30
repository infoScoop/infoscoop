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
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Lesser General Public License for more details.
# 
# You should have received a copy of the GNU Lesser General Public
# License along with this program. If not, see
# <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
--%>

<!doctype HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html; charset=UTF-8" errorPage="/jspError.jsp" %>

<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
	String uid = (String) session.getAttribute("Uid");
	if(uid == null || uid.length() == 0) {
		response.sendRedirect("./login.jsp");
	}
%>
<tiles:useAttribute name="title" scope="request"/>
<tiles:useAttribute name="type" scope="request"/>
<html>
	<tiles:insertAttribute name="page_head" />
	<body class="infoScoop admin">
		<div id="admin-menu-navigator"></div>
		
		<div id="admin-header">
			<tiles:insertAttribute name="header" />
		</div>
		<div id="admin-tabs">
			<tiles:insertAttribute name="menu" />
		</div>
		<div id="properties"></div>
		<div id="admin-body" >
			<tiles:insertAttribute name="body" />
		</div>
	</body>
</html>