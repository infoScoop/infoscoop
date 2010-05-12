<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html; charset=UTF8" %>
<%@page import="org.infoscoop.service.PropertiesService"%>
<%String staticContentURL = PropertiesService.getHandle().getProperty("staticContentURL"); %>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<meta http-equiv="Pragma" content="no-cache">
		<meta http-equiv="Cache-Control" content="no-cache">
		<meta http-equiv="Expires" content="Thu, 01 Dec 1994 16:00:00 GMT">
		<link rel="stylesheet" type="text/css" href="../../skin/styles.css">
		<script>
		IS_Portal = {};
		</script>
		<script src="../lib/prototype-1.6.0.3.js"></script>
		<script src="../lib/syntacticx-livepipe-ui/livepipe.js"></script>
		<script src="../lib/syntacticx-livepipe-ui/tabs.js"></script>
		<script src="../utils/utils.js"></script>
		<script src="../utils/ajax304.js"></script>
		<script src="../utils/ajaxpool/ajax.js"></script>
		<script src="../utils/domhelper.js"></script>
		<script src="../resources/resourceBundle.jsp"></script>
		<script src="./SearchEngine.js"></script>
		<script src="../utils/EventDispatcher.js"></script>
		<script>
		var localhostPrefix = "<%=request.getScheme()%>://localhost:<%=request.getServerPort()%><%=request.getContextPath()%>";
		var hostPrefix = findHostURL(false) + "../../..";
		var proxyServerURL = hostPrefix + "/proxy";
		var imageURL = "../../skin/imgs/";
		var searchEngineURL = searchEngineURL || localhostPrefix+"/schsrv";
		var ajaxRequestTimeout = is_getPropertyInt(ajaxRequestTimeout, 15000);
		var ajaxRequestRetryCount = is_getPropertyInt(ajaxRequestRetryCount,2);
		function search(){
			IS_Portal.SearchEngines.init();
			IS_Portal.SearchEngines.buildSearchTabs('<%= request.getParameter("keyword") %>');
		}
		</script>
	</head>
	<body onload="search();">
		<div id="search-iframe"></div>
	</body>
</html>