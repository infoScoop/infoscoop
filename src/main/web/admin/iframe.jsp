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