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
<%@ page import="org.infoscoop.service.PropertiesService"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%
	String staticContentURL = PropertiesService.getHandle().getProperty("staticContentURL");
	String imageURL = staticContentURL + "/skin/imgs/";
	request.setAttribute("imageURL", imageURL);
%>
<tiles:insertDefinition name="defaultpanel.side.definition" flush="true">
	<tiles:putAttribute name="type" value="generallayout" />
	<tiles:putAttribute name="title" value="alb_defaultPanel"/>
	<tiles:putAttribute name="side_body" type="string">
	

<script src="../../js/lib/jquery-ui/jquery-ui-1.10.2.custom.min.js"></script>
<script src="../../admin/js/AdminTabList.js"></script>
<link rel="stylesheet" type="text/css" href="../../js/lib/jquery-ui/jquery-ui-1.10.2.custom.css">
<style>
	.ui-autocomplete {
        max-height: 100px;
        overflow-y: auto;
        overflow-x: hidden;
        z-index: 20000;
    }
    /* IE 6 doesn't support max-height
     * we use height instead, but this forces the menu to always be this tall
     */
    * html .ui-autocomplete {
        height: 100px;
    }
</style>

<c:if test="${requestScope.errorMessage != null}">
<span class="errorMessage">%{${requestScope.errorMessage}}</span>
</c:if>
<div id="defaultPanel-div" <c:if test="${requestScope.isDefaultPanelAdmin==false}">class="tabAdmin"</c:if>>
	<div class="refreshAll">
		<h2  style="float: left;">%{alb_tabList}</h2>
		<c:if test="${requestScope.isDefaultPanelAdmin}">
		<a id="previewTop" class="iconButton" style="float: right;" title="%{alb_previewTop}" href="#">
			<img src="../../skin/imgs/minibrowser.gif" style="position: relative; top: 2px; margin: 0pt 5px 0pt 0pt;">%{alb_previewTop}</a>
		<a id="changeApply" class="iconButton" style="float: right;" title="%{alb_changeApply}" href="#">
			<img src="../../skin/imgs/database_save.gif" style="position: relative; top: 2px; margin: 0pt 5px 0pt 0pt;">%{alb_changeApply}</a>
		<a id="clearConfigurationButton" class="iconButton" style="float: right;" title="%{alb_clearConfigurationDesc}" href="#">
			<img src="../../skin/imgs/database_refresh.gif" style="position: relative; top: 2px; margin: 0pt 5px 0pt 0pt;">%{alb_clearConfigurationButton}</a>
		</c:if>
	</div>
	
	<div class="tabListButtons">
		<c:if test="${requestScope.isDefaultPanelAdmin}">
		<div style="display:inline;">
			<a class="iconButton" id="addTab" style="margin: 3px;" title="%{alb_addTab}" href="#">
				<img src="../../skin/imgs/add.gif" style="position: relative; top: 2px; margin: 0pt 5px 0pt 0pt;">%{alb_addTab}</a>
		</div>
		<div style="display:inline;width:80%;">
			<a class="iconButton" style="margin: 3px;" title="%{alb_commandBar}%{alb_edit}" href="commandbar" id="toCommandbar">
				<img src="../../skin/imgs/edit.gif" style="position: relative; top: 2px; margin: 0pt 5px 0pt 0pt;">%{alb_commandBar}%{alb_edit}</a>
		</div>
		<div style="display:inline;width:80%;">
			<a class="iconButton" style="margin: 3px;" title="%{alb_portalHeader}%{alb_edit}" href="portalHeader" id="toPortalHeader">
				<img src="../../skin/imgs/edit.gif" style="position: relative; top: 2px; margin: 0pt 5px 0pt 0pt;">%{alb_portalHeader}%{alb_edit}</a>
		</div>
		</c:if>
	</div>
	
	<div id="tabList"></div>
	
	<!-- hidden -->
	<div style="display:none;">
		<form id="addTabForm" action="addTab" method="post">
			<input type="hidden" id="addTabJson" name="addTabJson"></input>
		</form>
		<form id="commitForm" action="commitTab" method="post">
			<input type="hidden" id="updateDataJson" name="updateDataJson"></input>
		</form>
		<div id="defaultPanel"></div>
		<div id='select_layout_modal'>
			<c:import url="/WEB-INF/jsp/admin/defaultpanel/_layoutTemplates.jsp"/>
		</div>
	</div>
</div>


<script>
	var isDefaultPanelAdmin = ${requestScope.isDefaultPanelAdmin};
	var tabListJSON = ${requestScope.tabListJSON};
	var tabAdminList = ${requestScope.tabAdminsJSON};
	$jq(function(){
		var checkUpdated = function(){
			if(!ISA_Admin.checkUpdated())
				return false;
		}
		$jq("#toCommandbar").click(checkUpdated);
		$jq("#toPortalHeader").click(checkUpdated);

		ISA_DefaultPanel.defaultPanel = new ISA_DefaultPanel();
		$jq("#defaultPanel").ISA_TabList();
	});
</script>
	</tiles:putAttribute>
</tiles:insertDefinition>