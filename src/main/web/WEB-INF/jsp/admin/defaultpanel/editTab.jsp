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
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:insertDefinition name="defaultpanel.side.definition" flush="true">
	<tiles:putAttribute name="type" value="generallayout" />
	<tiles:putAttribute name="title" value="alb_defaultPanel"/>
	<tiles:putAttribute name="side_body" type="string">

<!-- <div id="command-bar"></div> -->
<span class="errorMessage">${errorMessage}</span>
<div id="defaultPanel">
	<div class="refreshAll">
		<div style="float: left;text-align: left;padding-top:10px;padding-left:5px;">
			<h2><c:choose>
				<c:when test="${isNew == true}">%{alb_newTab}</c:when>
				<c:when test="${commandbarView == true}">%{alb_commandBar}</c:when>
				<c:when test="${portalHeaderView == true}">%{alb_portalHeader}</c:when>
				<c:otherwise>%{alb_tab}${displayTabOrder}</c:otherwise>
			</c:choose></h2>
			<c:if test="${commandbarView != true && portalHeaderView != true}">
			<div style="padding-top:10px;padding-left:5px;">
				<div style="font-size:70%;">%{alb_tabDesc}:</div>
				<textarea id="tabDesc" style="width:350px; height:3.5em;" maxlength="255">${tabDesc}</textarea>
			</div>
			</c:if>
		</div>
		<!--
		<a class="iconButton" style="float: right;" title="%{alb_previewTop}" href="#">
			<img src="../../skin/imgs/page_world.gif" style="position: relative; top: 2px; margin: 0pt 5px 0pt 0pt;">%{alb_previewTop}</a>
		-->
		<a id="changeApply" class="iconButton" style="float: right;" title="%{alb_changeApply}" href="#">
			<img src="../../skin/imgs/database_save.gif" style="position: relative; top: 2px; margin: 0px 5px 0px 0px;">%{alb_changeApply}</a>
		<a class="iconButton" style="float: right; " title="%{alb_backTabList}" href="index" id="backToTabList">
			<img src="../../skin/imgs/arrow_undo.gif" style="position: relative; top: 2px; margin: 0px 5px 0px 0px; ">%{alb_backTabList}</a>
	</div>
</div>

<div id='select_layout_modal' title="%{alb_selectLayout}" style="display:none;">
	<c:import url="/WEB-INF/jsp/admin/defaultpanel/_layoutTemplates.jsp"/>
	<div style="clear:both;text-align:center;">
		<input id='select_layout_cancel' type="button" value="%{alb_cancel}"/>
	</div>
</div>

<script>
	window.editRoleScreen = true;
	var displayTabId = "${tabId}";
	var displayTabNumber = "${tabNumber}";
	var defaultPanelJson = ${defaultPanelJson};
	var displayTabOrder = "${displayTabOrder}";
	$jq(function(){
		var checkUpdated = function(){
			if(!ISA_Admin.checkUpdated())
				return false;
		}
		$jq("#backToTabList").click(checkUpdated);
		$jq("#tabDesc").change(function(){
			ISA_Admin.isUpdated = true;
		});

		ISA_DefaultPanel.defaultPanel = new ISA_DefaultPanel();
		IS_SiteAggregationMenu.init();
		ISA_loadProperties(ISA_DefaultPanel.defaultPanel.build);
	});
	
</script>
	</tiles:putAttribute>
</tiles:insertDefinition>