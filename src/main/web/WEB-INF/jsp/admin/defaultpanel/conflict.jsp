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

<div id="defaultPanel-div">
	<div class="refreshAll">
		<a class="iconButton" style="float: right;" title="%{alb_forceEdit}" href="forceEdit?tabId=${tabId}">
			<img src="../../skin/imgs/database_save.gif" style="position: relative; top: 2px; margin: 0pt 5px 0pt 0pt;">%{alb_forceEdit}</a>
		<a class="iconButton" style="float: right;" title="%{alb_backTabList}" href="index">
			<img src="../../skin/imgs/arrow_undo.gif" style="position: relative; top: 2px; margin: 0pt 5px 0pt 0pt;">%{alb_backTabList}</a>
	</div>
	
	<h2><c:choose>
		<c:when test="${commandbarView == true}">%{alb_commandBar}</c:when>
		<c:when test="${portalHeaderView == true}">%{alb_portalHeader}</c:when>
		<c:otherwise>%{alb_tab}${displayTabOrder}</c:otherwise>
	</c:choose></h2>
	
	<div style="margin-top:10px;" id="conflictMessage"></div>
</div>

<script>
	$jq = jQuery;

	$jq(function(){
		var conflictMsg = IS_R.getResource(ISA_R.alb_editByOtherUser, ["${lockingUid}"])
			+ "<br/>"
			+ ISA_R.alb_multiUserEdit2;
		$jq("#conflictMessage").html(conflictMsg);
	});
</script>

	</tiles:putAttribute>
</tiles:insertDefinition>