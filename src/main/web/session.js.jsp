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
<%@page import="org.infoscoop.admin.web.PreviewImpersonationFilter"%>
<%@page import="org.w3c.util.UUID"%>
<%@page import="org.infoscoop.service.SessionService"%>
<%@ page contentType="text/javascript; charset=UTF8" %>
<%
response.setHeader("Pragma","no-cache");
response.setHeader("Cache-Control", "no-cache");
String isPreviewStr = request.getParameter("isPreview");
Boolean isPreview = null;
if( isPreviewStr != null && !"null".equals( isPreviewStr ))
	isPreview = Boolean.valueOf( isPreviewStr );

String uid = ( String )session.getAttribute("Uid");
%>
is_sessionId = "<%=
	( uid == null ||( isPreview != null && isPreview.booleanValue()) ) ?
			new UUID().toString() : 
			SessionService.getHandle().newSessionId( uid )
	%>";
