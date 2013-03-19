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
<%@page import="org.infoscoop.admin.web.PreviewImpersonationFilter"%>
<%@page import="org.infoscoop.service.PropertiesService"%>
<%@page import="org.infoscoop.service.ForbiddenURLService" %>
<%@page import="org.infoscoop.util.RSAKeyManager"%>
<%String staticContentURL = PropertiesService.getHandle().getProperty("staticContentURL"); %>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Cache-Control" content="no-cache">
    <meta http-equiv="Expires" content="Thu, 01 Dec 1994 16:00:00 GMT">
	<title></title>
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/styles.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/calendar.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/schedule.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/pulldown.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/calendarinput.css">
	<!-- prototype-window -->
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/js/lib/prototype-window-1.3/themes/default.css"> 
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/js/lib/prototype-window-1.3/themes/alphacube.css">
    
    <script src="./js/resources/resourceBundle.jsp"></script>
	<script src="<%=staticContentURL%>/js/gadget/features/core:rpc:pubsub.js?c=1"></script>
    <script src="<%=staticContentURL%>/js/gadget/container.js"></script>
	<%
		String uid = (String) session.getAttribute("Uid");
		Boolean isAdmin = (Boolean) request.getAttribute("isAdministrator");
	%>
    <script>
		<jsp:include page="/prpsrv" flush="true" />
		var isTabView = true;
		
		var imageURL = staticContentURL + "/skin/imgs/";
		
		var IS_Portal = {
			lang : "<%=request.getLocale().getLanguage() %>",
			japaneseOnly : false
		};
		var is_userId = <%=uid != null ? "\"" + uid + "\"" : "null" %>;
		var is_isAdministrator = <%=isAdmin != null ? isAdmin.booleanValue() : false%>;
		//dojo.require("dojo.dom");
		
		var localhostPrefix = "<%=request.getScheme()%>://localhost:<%=request.getServerPort()%><%=request.getContextPath()%>"

		var IS_forbiddenURLs = <%= ForbiddenURLService.getHandle().getForbiddenURLsJSON() %>;
	</script>

	<!--start script-->
    <script src="<%=staticContentURL%>/js/lib/prototype-1.7.1.js"></script>
    <script src="<%=staticContentURL%>/js/lib/control.modal.js"></script>
    <script src="<%=staticContentURL%>/js/utils/ajax304.js"></script>
    
	<script src="<%=staticContentURL%>/js/lib/scriptaculous-js-1.9.0/effects.js"></script>
	
	
	<script src="<%=staticContentURL%>/js/lib/date/date.js"></script>
	<script src="<%=staticContentURL%>/js/lib/rsa/jsbn.js"></script>
	<script src="<%=staticContentURL%>/js/lib/rsa/prng4.js"></script>
	<script src="<%=staticContentURL%>/js/lib/rsa/rng.js"></script>
	<script src="<%=staticContentURL%>/js/lib/rsa/rsa.js"></script>
    <script src="<%=staticContentURL%>/js/lib/extras-array.js"></script>
    <script src="<%=staticContentURL%>/js/utils/utils.js"></script>
    <script src="<%=staticContentURL%>/js/utils/ajaxpool/ajax.js"></script>
    <script src="<%=staticContentURL%>/js/utils/Request.js"></script>
    <script src="<%=staticContentURL%>/js/utils/msg.js"></script>
    <script src="<%=staticContentURL%>/js/Portal.js"></script>
    <script src="<%=staticContentURL%>/js/DragWidget.js"></script>
    <!--script src="<%=staticContentURL%>/js/lib/xpath.js"></script-->
    <script src="<%=staticContentURL%>/js/utils/CalendarInput.js"></script>
    <script src="<%=staticContentURL%>/js/commands/UpdatePropertyCommand.js"></script>
    <script src="<%=staticContentURL%>/js/utils/EventDispatcher.js"></script>
    <script src="<%=staticContentURL%>/js/Tab.js"></script>	
    
    <script src="<%=staticContentURL%>/js/WidgetsContainer.js"></script>
    <script src="<%=staticContentURL%>/js/AutoReload.js"></script>
	
    <script src="<%=staticContentURL%>/js/widgets/Widget.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/WidgetHeader.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/WidgetEdit.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/maximize/Maximize.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/maximize/MaximizeKeybind.js"></script>
    <script src="<%=staticContentURL%>/js/ContentFooter.js"></script>
    
	
    <script src="<%=staticContentURL%>/js/widgets/rssreader/RssReader.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/rssreader/RssParser.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/rssreader/RssItemRender.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/MultiRssReader/MultiRssReader.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/maximize/MaximizeRssReader.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/maximize/MaximizeRssItemRender.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/maximize/MaximizeRssItemSelection.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/maximize/MaximizeRssCategory.js"></script>
   <script src="<%=staticContentURL%>/js/widgets/information/Information.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/information/Information2.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/calendar/Calendar.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/calendar/iCalendar.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/schedule/schedule.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/schedule/groupSettingModal.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/maximize/MaximizeSchedule.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/ticker/Ticker.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/ranking/Ranking.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/ranking/RankingRender.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/MiniBrowser/MiniBrowser.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/MiniBrowser/FragmentMiniBrowser.js"></script>
    <!-- slider -->
    <!--end script-->
    <script src="customization"></script>
    <script src="jssrv"></script>
   	<script type="text/javascript">
		var rsaPK = new RSAKey();
		rsaPK.setPublic("<%= RSAKeyManager.getInstance().getModulus() %>", "<%= RSAKeyManager.getInstance().getPublicExponent() %>");
		
		IS_WidgetConfiguration = <jsp:include page="/widconf" flush="true" />;
	</script>
    
	<script type="text/javascript">
	IS_Portal.currentTabId = "tab0";
	function tabInit(){
		new IS_WidgetsContainer();
	}
	IS_Portal.widgetLists = new Object();
	IS_Portal.columnsObjs = new Object();
	IS_Portal.tabs = new Object();
	
	var displayTabOrder = "<%= request.getAttribute("tabOrder") %>";
	
	IS_Portal.addTab = function(selected, idNumber, name, type, numCol, columnsWidth, isInitialize, tabOrder){
//		if(tabOrder != displayTabOrder) return;
		if(selected)
			IS_Portal.currentTabId = ("tab" + idNumber);
		
		var panelDiv = IS_Portal.buildPanel( idNumber, type );
		var panels = $("panels");
		panelDiv.style.display = "";
		panels.appendChild( panelDiv );
		IS_Portal.widgetLists["tab"+idNumber] = new Object();
		IS_Portal.columnsObjs["tab"+idNumber] = {};
		IS_Portal.tabs["tab"+idNumber] = {"numCol":0};
		IS_WidgetsContainer.rebuildColumns("tab"+idNumber, numCol, columnsWidth, false, isInitialize);
	}
    </script>
 </head>
  
	<body style="margin-top:0;padding-top:0;" onload="tabInit();">
		<div id="portal-body">
		<div id="error-msg-bar" style="display:none;"></div>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tbody>
				<tr>
					<td><div id="portal-command"></div></td>
					<td width="16px"><img id="messageIcon" src="<%=staticContentURL%>/skin/imgs/information.gif" style="cursor:pointer;" onclick="javascript:msg.showPopupDialog();"/></td>
				</tr>
			</tbody>
		</table>
		<div id="portal-iframe-url"></div>
			<div id="panels" style="display:;">
				<div id="maximize-panel" style="display:none;"></div>
			</div>
			<div id="portal-iframe" style="display:none;">
				<iframe id="ifrm" name="ifrm" src="about:blank" FrameBorder="0" style="width:100%;height768px;border:none;scrolling:auto;"></iframe>
			</div>
			<div id="iframe-tool-bar"></div>
			<div id="search-iframe" style="display:none;"></div>
		</div>
	</body>
</html>
