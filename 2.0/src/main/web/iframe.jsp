<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
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
	<script>
		window.onload = function(){
			var iframe = document.getElementById("ifrm");
			//iframe.src = "about:blank";
			iframe.src = "<%= ProxyRequest.escapeURL(url) %>";
			iframe.style.display = "";
		};
	</script>
</head>
<body style="margin:0;padding:0;">

<iframe id="ifrm" frameBorder="0" style="display:none;margin:0;padding:0;width:100%;height:100%;"
src="./blank.html"
scrolling="<%=scrolling%>"></iframe>

<%
}
%>
</body>
</html>