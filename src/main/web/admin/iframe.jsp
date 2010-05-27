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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html; charset=UTF8" %>
<%@page import="org.infoscoop.request.ProxyRequest"%>
<%@page import="java.util.Date"%>

<%
response.setHeader("Pragma","no-cache");
response.setHeader("Cache-Control", "no-cache");
%>
<html>
<body style="margin:0;padding:0;">
<%
String url = ( String )request.getParameter("url");
if( url == null ) {
	out.println("Must specify url. Contact to System Administrator.");
} else {
	String scrolling = ( String )request.getParameter("scrolling");
	if( scrolling == null ) {
		scrolling = "";
	}
%>
<iframe frameBorder="0" style="margin:0;padding:0;width:100%;height:100%;"
src="<%= ProxyRequest.escapeURL(url) %>"
scrolling="<%=scrolling%>"></iframe>
<%
}
%>
</body>
</html>
