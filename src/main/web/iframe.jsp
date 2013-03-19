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

<!DOCTYPE HTML>
<%@ page contentType="text/html; charset=UTF8" %>
<%@page import="org.infoscoop.request.ProxyRequest"%>
<%@page import="java.util.Date"%>

<%
response.setHeader("Pragma","no-cache");
response.setHeader("Cache-Control", "no-cache");

String url = ( String )request.getParameter("url");
if( url == null ) {
	out.println("Must specify url. Contact to System Administrator.");
} else {
	String scrolling = ( String )request.getParameter("scrolling");
	if( scrolling == null ) {
		scrolling = "";
	}
%>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<script>
		window.onload = function(){
			var iframe = document.getElementById("ifrm");
			//iframe.src = "about:blank";
			iframe.src = "<%= ProxyRequest.escapeURL(url) %>";
			iframe.style.display = "";
		};
	</script>
</head>
<body style="margin:0px;padding:0px;">

<iframe id="ifrm" frameBorder="0" style="display:none;margin:0px;padding:0px;width:100%;height:100%;"
src="./blank.html"
scrolling="<%=scrolling%>"></iframe>

<%
}
%>
</body>
</html>
