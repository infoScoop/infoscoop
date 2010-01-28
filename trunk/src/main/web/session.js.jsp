
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