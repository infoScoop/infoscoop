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
<%@page import="java.util.regex.*"%>
<%@page import="org.infoscoop.service.PropertiesService"%>
<%
String staticContentURL = PropertiesService.getHandle().getProperty("staticContentURL"); 
staticContentURL = Pattern.compile("^http(s)?://.*").matcher(staticContentURL.trim()).matches() ? staticContentURL : "../..";
String uid = (String) session.getAttribute("Uid");
String keyword = request.getParameter("keyword");
if(keyword != null)
	keyword = new String(keyword.getBytes("iso-8859-1"), "UTF-8");
String windowId = request.getParameter("windowid");
%>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<meta http-equiv="Pragma" content="no-cache">
		<meta http-equiv="Cache-Control" content="no-cache">
		<meta http-equiv="Expires" content="Thu, 01 Dec 1994 16:00:00 GMT">
		<link rel="stylesheet" type="text/css" href="<%= staticContentURL %>/skin/styles.css">
		<style>
			body {
				overflow: auto;
			}
		</style>
		<script>
		IS_Portal = {};
		var imageURL = "<%= staticContentURL %>/skin/imgs/";
		</script>
		<script src="../resources/resourceBundle.jsp"></script>
		<!--start script-->
		<script src="<%= staticContentURL %>/js/lib/prototype-1.6.0.3.js"></script>
		<script src="<%= staticContentURL %>/js/lib/syntacticx-livepipe-ui/livepipe.js"></script>
		<script src="<%= staticContentURL %>/js/lib/syntacticx-livepipe-ui/tabs.js"></script>
		<script src="<%= staticContentURL %>/js/utils/utils.js"></script>
		<script src="<%= staticContentURL %>/js/utils/ajax304.js"></script>
		<script src="<%= staticContentURL %>/js/utils/ajaxpool/ajax.js"></script>
		<script src="<%=staticContentURL%>/js/utils/Request.js"></script>
		<script src="<%= staticContentURL %>/js/utils/domhelper.js"></script>
		<script src="<%=staticContentURL%>/js/utils/msg.js"></script>
		<script src="<%= staticContentURL %>/js/search/SearchEngine.js"></script>
		<script src="<%= staticContentURL %>/js/utils/EventDispatcher.js"></script>
		<script src="<%=staticContentURL%>/js/commands/UpdatePropertyCommand.js"></script>
		<!--end script-->
		<script>
		<jsp:include page="/prpsrv" flush="true" />
		var localhostPrefix = "<%=request.getScheme()%>://localhost:<%=request.getServerPort()%><%=request.getContextPath()%>";
		var hostPrefix = findHostURL(false) + "../../..";
		var proxyServerURL = hostPrefix + "/proxy";
		var searchEngineURL = searchEngineURL || localhostPrefix+"/schsrv";
		var ajaxRequestTimeout = is_getPropertyInt(ajaxRequestTimeout, 15000);
		var ajaxRequestRetryCount = is_getPropertyInt(ajaxRequestRetryCount,2);
		IS_Request.LogCommandQueue = opener.IS_Request.LogCommandQueue;
		function search(){
			IS_Portal.SearchEngines.searchOption = opener.IS_Portal.SearchEngines.searchOption;
			var urllist = opener.IS_Portal.SearchEngines._searchResultsWindowInfo['<%= windowId %>'].urllist;
			IS_Portal.SearchEngines.init();
			IS_Portal.SearchEngines.buildSearchTabs('<%= keyword %>',urllist);
		}
		</script>
	</head>
	<body onload="search();" class="infoScoop">
		<div id="search-iframe"></div>
	</body>
</html>
