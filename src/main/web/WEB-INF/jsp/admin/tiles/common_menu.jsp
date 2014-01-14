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
<%@ page import="org.infoscoop.service.PortalAdminsService" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
	PortalAdminsService adminService = PortalAdminsService.getHandle();
	boolean isTreeAdminUser = !adminService.isPermitted("menu") && adminService.isPermitted("menu_tree");
%>

	<!-- TODO ACL control and create tabs dynamically -->
	<ul class="tabs" >

		<li>
			<a href="../home/index" class="tab <c:if test="${type == 'home'}">active</c:if>" id="tab_information"><span>%{alb_home}</span></a>
		</li>
		
		<%if( adminService.isPermitted("menu") ){%>
		<li>
			<a href="../menu/index" class="tab <c:if test="${type == 'menu'}">active</c:if>" id="tab_menu"><span>%{alb_menu}</span></a>
		</li>
		<%} else if( adminService.isPermitted("menu_tree") ){%>
		<li>
			<a href="../menutree/index" class="tab <c:if test="${type == 'menu'}">active</c:if>" id="tab_menu"><span>%{alb_menu}</span></a>
		</li>
		<%}%>
		
		<%if(adminService.isPermitted("search")){%>
		<li>
			<a href="../search/index" class="tab <c:if test="${type == 'search'}">active</c:if>" id="tab_searchEngine"><span>%{alb_searchForm}</span></a>
		</li>
		<%}%>
		
		<%if(adminService.isPermitted("widget")){%>
		<li>
			<a href="../gadget/index" class="tab <c:if test="${type == 'gadget'}">active</c:if>" id="tab_widgetConf"><span>%{alb_widget}</span></a>
		</li>
		<%}%>
		
		<%if( adminService.isPermitted("defaultPanel") || adminService.isPermitted("portalLayout") ||  adminService.isPermitted("tabAdmin") ){%>
		<li>
			<a href="../generallayout/index" class="tab <c:if test="${type == 'generallayout'}">active</c:if>" id="tab_defaultPanel"><span>%{alb_generalLayout}</span></a>
			
		</li>
		<%}%>
		
		<%if( adminService.isPermitted("i18n")){%>
		<li>
			<a href="../i18n/index" class="tab <c:if test="${type == 'i18n'}">active</c:if>" id="tab_i18n"><span>%{alb_i18n}</span></a>
		</li>
		<%}%>
		
		<%if( adminService.isPermitted("properties")){%>
		<li>
			<a href="../properties/index" class="tab <c:if test="${type == 'properties'}">active</c:if>" id="tab_properties"><span>%{alb_properties}</span></a>
		</li>
		<%}%>
		
		<%if( adminService.isPermitted("proxy")){%>
		<li>
			<a href="../proxy/index" class="tab <c:if test="${type == 'proxy'}">active</c:if>" id="tab_proxy"><span>%{alb_proxy}</span></a>
		</li>
		<%}%>
		
		<%if( adminService.isPermitted("admins")){%>
		<li>
			<a href="../administrator/index" class="tab <c:if test="${type == 'administrator'}">active</c:if>" id="tab_portalAdmin"><span>%{alb_admin}</span></a>
		</li>
		<%}%>
		
		<%if( adminService.isPermitted("forbiddenURL")){%>
		<li>
			<a href="../forbiddenurl/index" class="tab <c:if test="${type == 'forbiddenurl'}">active</c:if>" id="tab_forbiddenURL"><span>%{alb_forbiddenURL}</span></a>
		</li>
		<%}%>
		
		<%if( adminService.isPermitted("authentication")){%>
		<li>
			<a href="../authentication/index" class="tab <c:if test="${type == 'authentication'}">active</c:if>" id="tab_authentication"><span>OAuth</span></a>
		</li>
		<%}%>
		<%if( adminService.isPermitted("extapps")){%>
		<li>
			<a href="../extapps/index" class="tab <c:if test="${type == 'extapps'}">active</c:if>" id="tab_extapps"><span>%{alb_extapps}</span></a>
		</li>
		<%}%>
		<li>
			<div id="admin-message-icon">
				<img id="messageIcon" title="%{alb_messageConsole}" src="../../skin/imgs/information3.gif" style="cursor:pointer;">
			</div>
		</li>
		
	</ul>
	<div style="clear:both"></div>
