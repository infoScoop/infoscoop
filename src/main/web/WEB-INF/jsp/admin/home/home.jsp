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

<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="org.infoscoop.service.PortalAdminsService" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	PortalAdminsService adminService = PortalAdminsService.getHandle();
%>
<div id="home-menus">
	<p id="" class="homeTitle">%{alb_adminMenu}</p>
		<!-- Menus -->
		<%if(adminService.isPermitted("menu")){%>
		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<a href="../menu/index" class="home-menu-icon-link"><img src="../../skin/imgs/home_icons/kmenuedit.gif"></a>
			</div>
			<div class="home-menu-content">
				<a href="../menu/index" class="home-menu-header">%{alb_menu}</a>
				<p class="home-menu-definition">%{alb_menuDescription}</p>
			</div>
		</div>
		<%} else if(adminService.isPermitted("menu_tree")){ %>
		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<a href="../menutree/index" class="home-menu-icon-link"><img src="../../skin/imgs/home_icons/kmenuedit.gif"></a>
			</div>
			<div class="home-menu-content">
				<a href="../menutree/index" class="home-menu-header">%{alb_menu}</a>
				<p class="home-menu-definition">%{alb_menuDescription}</p>
			</div>
		</div>
		<%}%>
		<%if(adminService.isPermitted("search")){%>
		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<a href="../search/index"><img src="../../skin/imgs/home_icons/search.gif"></a>
			</div>
			<div class="home-menu-content">
				<a href="../search/index" class="home-menu-header">%{alb_searchForm}</a>
				<p class="home-menu-definition">%{alb_searchFormDescription}</p>
			</div>
		</div>
		<%}%>
		<%if( adminService.isPermitted("widget") ){%>
		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<a href="../gadget/index"><img src="../../skin/imgs/home_icons/kpersonalizer.gif"></a>
			</div>
			<div class="home-menu-content">
				<a href="../gadget/index" class="home-menu-header">%{alb_widget}</a>
				<p class="home-menu-definition">%{alb_widgetDescription}</p>
			</div>
		</div>
		<%}%>
		<%if( adminService.isPermitted("defaultPanel") || adminService.isPermitted("portalLayout")){%>
		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<a href="../defaultpanel/index"><img src="../../skin/imgs/home_icons/mycomputer.gif"></a>
			</div>
			<div class="home-menu-content">
				<a href="../generallayout/index" class="home-menu-header">%{alb_generalLayout}</a>
				<p class="home-menu-definition">%{alb_defaultPanelDesription}</p>
			</div>
		</div>
		<%}%>
		<%if( adminService.isPermitted("i18n") ){%>
		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<a href="../i18n/index"><img src="../../skin/imgs/home_icons/i18n.gif"></a>
			</div>
			<div class="home-menu-content">
				<a href="../i18n/index" class="home-menu-header">%{alb_i18n}</a>
				<p class="home-menu-definition">%{alb_i18nDescription}</p>
			</div>
		</div>
		<%}%>
		<%if( adminService.isPermitted("properties") ){%>
		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<a href="../properties/index"><img src="../../skin/imgs/home_icons/advancedsettings.gif"></a>
			</div>
			<div class="home-menu-content">
				<a href="../properties/index" class="home-menu-header">%{alb_properties}</a>
				<p class="home-menu-definition">%{alb_propertiesDescription}</p>
			</div>
		</div>
		<%}%>
		<%if( adminService.isPermitted("proxy") ){%>
		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<a href="../proxy/index"><img src="../../skin/imgs/home_icons/network_local.gif"></a>
			</div>
			<div class="home-menu-content">
				<a href="../proxy/index" class="home-menu-header">%{alb_proxy}</a>
				<p class="home-menu-definition">%{alb_proxyDescription}</p>
			</div>
		</div>
		<%}%>
		<%if( adminService.isPermitted("admins") ){%>
		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<a href="../administrator/index"><img src="../../skin/imgs/home_icons/kdmconfig.gif"></a>
			</div>
			<div class="home-menu-content">
				<a href="../administrator/index" class="home-menu-header">%{alb_admin}</a>
				<p class="home-menu-definition">%{alb_adminDescription}</p>
			</div>
		</div>
		<%}%>
		<%if( adminService.isPermitted("forbiddenURL") ){%>
		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<a href="../forbiddenurl/index"><img src="../../skin/imgs/home_icons/cnrdelete-all.gif"></a>
			</div>
			<div class="home-menu-content">
				<a href="../forbiddenurl/index" class="home-menu-header">%{alb_forbiddenURL}</a>
				<p class="home-menu-definition">%{alb_forbiddenURLDescription}</p>
			</div>
		</div>
		<%}%>
		<%if( adminService.isPermitted("authentication")){%>
		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<a href="../authentication/index"><img src="../../skin/imgs/home_icons/unlock.gif"></a>
			</div>
			<div class="home-menu-content">
				<a href="../authentication/index" class="home-menu-header">OAuth</a>
				<p class="home-menu-definition">%{alb_OAuthDescription}</p>
			</div>
		</div>
		<%}%>

	<p id="" class="homeTitle">%{alb_infoscoopInfo}</p>
		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<img src="../../skin/imgs/home_icons/goto.gif">
			</div>
			<div class="home-menu-content">
				<div id="information"></div>
			</div>
		</div>

		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<img src="../../skin/imgs/home_icons/info.gif">
			</div>
			<div class="home-menu-content">
			<div id="version">
				<table class="configTableHeader" cellspacing="0" cellpadding="0" style="display:inline; margin-left: 0px;">
					<tbody>
						<tr>
							<td class="configTableHeaderTd" colspan=2>%{alb_versionInformation}</td>
						</tr>
						<tr>
							<td class="configTableTd" width="150px" style="padding: 3 0 3 0.5em; text-align:left;">
								%{alb_productName}
							</td>
							<td class="configTableTd" width="150px" style="padding:0 0.5em 0 0; text-align:right;">
								@PRODUCT.NAME@
							</td>
						</tr>
						<tr>
							<td class="configTableTd" width="150px" style="padding: 3 0 3 0.5em; text-align:left;">
								%{alb_versionNum}
							</td>
							<td class="configTableTd" width="150px" style="padding:0 0.5em 0 0; text-align:right;">
								@BUILD.VERSION@
							</td>
						</tr>
					<tbody>
				</table>
			</div>
			</div>
		</div>
</div>
<script>
	
	$jq(function() {
		ISA_PortalAdmins.information = new ISA_Information();
		ISA_PortalAdmins.information.build();
	});
	
</script>
