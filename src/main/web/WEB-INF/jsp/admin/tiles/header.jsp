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
<%@ page import="org.infoscoop.properties.InfoScoopProperties"%>
<%@ page import="org.infoscoop.service.SquareService"%>
<%@page import="org.infoscoop.context.UserContext"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% 
    boolean useMultitenantMode = Boolean.valueOf(InfoScoopProperties.getInstance().getProperty("useMultitenantMode"));
    request.setAttribute("useMultitenantMode", useMultitenantMode);
    String squareId = UserContext.instance().getUserInfo().getCurrentSquareId();
    String squareName = SquareService.getHandle().getSquareName(squareId);
%>

	<a id="admin-header-title" style="float:left;cursor:pointer;" href="../home/index">
	<c:choose>
	    <c:when test="${useMultitenantMode}"><%= squareName %></c:when>
	    <c:otherwise><!--start of product name-->infoScoop<!--end of product name--></c:otherwise>
	</c:choose>
		%{alb_administration}
	</a>
<!-- 	<div id="admin-message-icon"> -->
<!-- 		<img id="messageIcon" title="%{lb_messageConsole}" src="../../skin/imgs/information3.gif" style="cursor:pointer;"> -->
<!-- 	</div> -->
