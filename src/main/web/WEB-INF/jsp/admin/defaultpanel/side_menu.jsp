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
# <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>;.
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="org.infoscoop.service.PortalAdminsService" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
	PortalAdminsService adminService = PortalAdminsService.getHandle();
	boolean isTreeAdminUser = !adminService.isPermitted("menu") && adminService.isPermitted("menu_tree");
%>

<div id="defaultPanel-side-bar" class="side-bar">
<ul>
<%if( adminService.isPermitted("defaultPanel") ){%>
	<li>
		<a href="../defaultpanel/index" class="sideBarTab-ui <c:if test="${type == 'defaultPanel_tabs'}">active</c:if>"><span class="title">%{alb_tab}</span></a>
	</li>
	<li>
		<a href="../defaultpanel/commandBar_index" class="sideBarTab-ui <c:if test="${type == 'defaultPanel_commandBar'}">active</c:if>"><span class="title">%{alb_commandBar}</span></a>
	</li>
<%}%>
<%if( adminService.isPermitted("portalLayout") ){%>
	<li>
		<a href="../defaultpanel/portalLayout_index" class="sideBarTab-ui <c:if test="${type == 'defaultPanel_portalLayout'}">active</c:if>"><span class="title">%{alb_otherLayout}</span></a>
	</li>
<%}%>

</ul>
</div>