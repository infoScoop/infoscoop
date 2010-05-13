<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html; charset=UTF8" %>
<%@page import="java.util.regex.*"%>
<%@page import="org.infoscoop.service.PropertiesService"%>
<%@page import="org.infoscoop.service.PreferenceService" %>
<%
String staticContentURL = PropertiesService.getHandle().getProperty("staticContentURL"); 
staticContentURL = Pattern.compile("^http(s)?://.*").matcher(staticContentURL.trim()).matches() ? staticContentURL : "../..";
String uid = (String) session.getAttribute("Uid");
%>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<meta http-equiv="Pragma" content="no-cache">
		<meta http-equiv="Cache-Control" content="no-cache">
		<meta http-equiv="Expires" content="Thu, 01 Dec 1994 16:00:00 GMT">
		<link rel="stylesheet" type="text/css" href="<%= staticContentURL %>/skin/styles.css">
		<script>
		IS_Portal = {};
		</script>
		<script src="../resources/resourceBundle.jsp"></script>
		<!--start script-->
		<script src="<%= staticContentURL %>/js/lib/prototype-1.6.0.3.js"></script>
		<script src="<%= staticContentURL %>/js/lib/syntacticx-livepipe-ui/livepipe.js"></script>
		<script src="<%= staticContentURL %>/js/lib/syntacticx-livepipe-ui/tabs.js"></script>
		<script src="<%= staticContentURL %>/js/utils/utils.js"></script>
		<script src="<%= staticContentURL %>/js/utils/ajax304.js"></script>
		<script src="<%= staticContentURL %>/js/utils/ajaxpool/ajax.js"></script>
		<script src="<%= staticContentURL %>/js/utils/domhelper.js"></script>
		<script src="<%=staticContentURL%>/js/utils/msg.js"></script>
		<script src="<%= staticContentURL %>/js/search/SearchEngine.js"></script>
		<script src="<%= staticContentURL %>/js/utils/EventDispatcher.js"></script>
		<!--end script-->
		<script>
		var localhostPrefix = "<%=request.getScheme()%>://localhost:<%=request.getServerPort()%><%=request.getContextPath()%>";
		var hostPrefix = findHostURL(false) + "../../..";
		var proxyServerURL = hostPrefix + "/proxy";
		var imageURL = "<%= staticContentURL %>/skin/imgs/";
		var searchEngineURL = searchEngineURL || localhostPrefix+"/schsrv";
		var ajaxRequestTimeout = is_getPropertyInt(ajaxRequestTimeout, 15000);
		var ajaxRequestRetryCount = is_getPropertyInt(ajaxRequestRetryCount,2);
		var preference = <%= PreferenceService.getHandle().getPreferenceJSON(uid) %>
		if(preference.property){
			IS_Portal.SearchEngines.searchOption = preference.property.searchOption ? eval('(' + preference.property.searchOption+ ')') : {};
		}
		function search(){
			IS_Portal.SearchEngines.init();
			IS_Portal.SearchEngines.buildSearchTabs('<%= request.getParameter("keyword") %>');
		}
		</script>
	</head>
	<body onload="search();" class="infoScoop">
		<div id="search-iframe"></div>
	</body>
</html>