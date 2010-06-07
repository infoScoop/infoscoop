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

<!doctype HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html; charset=UTF8" %>
<%@ page import="org.infoscoop.util.RSAKeyManager"%>
<%@page import="org.infoscoop.service.ForbiddenURLService" %>
<%@page import="org.infoscoop.service.PortalAdminsService" %>
<%
	String uid = (String) session.getAttribute("Uid");
	if(uid == null || uid.length() == 0) {
		response.sendRedirect("./login.jsp");
	}
%>
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<meta http-equiv="Pragma" content="no-cache">
	<meta http-equiv="Cache-Control" content="no-cache">

	<title><!--start of product name-->infoScoop<!--end of product name-->%{alb_administration}</title>
	<link rel="stylesheet" type="text/css" href="../skin/admin.css">
	<link rel="stylesheet" type="text/css" href="../skin/admintreemenu.css">
	
	<!--start styles css-->
    <link rel="stylesheet" type="text/css" href="../skin/styles.css">
    <link rel="stylesheet" type="text/css" href="../skin/siteaggregationmenu.css">
    <link rel="stylesheet" type="text/css" href="../skin/treemenu.css">
    <link rel="stylesheet" type="text/css" href="../skin/calendar.css">
    <link rel="stylesheet" type="text/css" href="../skin/pulldown.css">
    <link rel="stylesheet" type="text/css" href="../skin/calendarinput.css">
    <link rel="stylesheet" type="text/css" href="../skin/mySiteMap.css">
    <link rel="stylesheet" type="text/css" href="../skin/commandbar.css">
    <link rel="stylesheet" type="text/css" href="../skin/tab.css">
    <link rel="stylesheet" type="text/css" href="../skin/widget.css">
    
    <link rel="stylesheet" type="text/css" href="../skin/groupsettingmodal.css">
    <link rel="stylesheet" type="text/css" href="../skin/message.css">
    <link rel="stylesheet" type="text/css" href="../skin/minibrowser.css">
    <link rel="stylesheet" type="text/css" href="../skin/ranking.css">
    <link rel="stylesheet" type="text/css" href="../skin/widgetranking.css">
    <link rel="stylesheet" type="text/css" href="../skin/rssreader.css">
    <link rel="stylesheet" type="text/css" href="../skin/maximizerssreader.css">
    <link rel="stylesheet" type="text/css" href="../skin/information.css">
    <link rel="stylesheet" type="text/css" href="../skin/ticker.css">
	<!--end styles css-->
	
    <script>
		<jsp:include page="/prpsrv" flush="true" />
		var isTabView = false;
		staticContentURL = /^(http|https):\/\//.test(staticContentURL) ? staticContentURL : '..';
		var imageURL = staticContentURL + "/skin/imgs/";
		
		var IS_Portal = {};
		var is_userId = <%=uid != null ? "\"" + uid + "\"" : "null" %>;

		var IS_forbiddenURLs = <%= ForbiddenURLService.getHandle().getForbiddenURLsJSON() %>;
		
		var localhostPrefix = "<%=request.getScheme()%>://localhost:<%=request.getServerPort()%><%=request.getContextPath()%>";
	</script>
	
	<script src="../js/resources/resourceBundle.jsp"></script>
	<script src="./js/resources/resourceBundle.jsp"></script>
    <script src="../js/gadget/features/core:rpc:pubsub:infoscoop.js?c=1"></script>
	
	<!--start script-->
    <script src="../js/lib/prototype-1.6.0.3.js"></script>
	<script src="../js/lib/scriptaculous-js-1.8.2/effects.js"></script>
	<script src="../js/lib/scriptaculous-js-1.8.2/dragdrop.js"></script>
	<script src="../js/lib/syntacticx-livepipe-ui/livepipe.js"></script>
	<script src="../js/lib/syntacticx-livepipe-ui/tabs.js"></script>
	<script src="./js/lib/popupmenu.js"></script>

	<script src="../js/utils/utils.js"></script>
	<script src="../js/utils/domhelper.js"></script>
	<script src="../js/utils/ajaxpool/ajax.js"></script>
	<script src="../js/utils/ajax304.js"></script>
	<script src="../js/lib/control.modal.js"></script>
	<script src="../js/lib/date/date.js"></script>
	<script src="../js/lib/rsa/jsbn.js"></script>
	<script src="../js/lib/rsa/prng4.js"></script>
	<script src="../js/lib/rsa/rng.js"></script>
	<script src="../js/lib/rsa/rsa.js"></script>
	<script src="../js/lib/extras-array.js"></script>
	<script src="../js/utils/msg.js"></script>
	<script src="../js/utils/EventDispatcher.js"></script>
	<script src="../js/utils/CalendarInput.js"></script>
	<script src="../js/utils/Request.js"></script>
	<script src="../js/utils/Validator.js"></script>
	<script src="../js/utils/groupSettingModal.js"></script>

	
	<script src="../js/commands/UpdatePropertyCommand.js"></script>
	<script src="../js/widgets/Widget.js"></script>
	<script src="../js/widgets/WidgetHeader.js"></script>
	<script src="../js/widgets/WidgetEdit.js"></script>
	<script src="../js/DragWidget.js"></script>
	<script src="../js/widgets/rssreader/RssReader.js"></script>
	<script src="../js/widgets/rssreader/RssItemRender.js"></script>
	<script src="../js/widgets/MultiRssReader/MultiRssReader.js"></script>
    <script src="../js/widgets/information/Information.js"></script>
    <script src="../js/widgets/information/Information2.js"></script>
    <script src="../js/widgets/calendar/Calendar.js"></script>
    <script src="../js/widgets/calendar/iCalendar.js"></script>
    <script src="../js/widgets/MiniBrowser/MiniBrowser.js"></script>
    <script src="../js/widgets/MiniBrowser/FragmentMiniBrowser.js"></script>
    <script src="../js/widgets/WidgetRanking/WidgetRanking.js"></script>
    <script src="../js/widgets/Message/Message.js"></script>
	
	<script src="js/Admin.js"></script>
	<script src="js/AdminDragDrop.js"></script>
	<script src="js/AdminInstantEdit.js"></script>
	<script src="js/AdminSiteAggregationMenu.js"></script>
	<script src="js/AdminSearchEngine.js"></script>
	<script src="js/AdminProperties.js"></script>
	<script src="js/AdminProxyConf.js"></script>
	<script src="js/AdminI18N.js"></script>
	<script src="js/AdminCommonModals.js"></script>
	<script src="js/AdminDefaultPanel.js"></script>
	<script src="js/AdminDefaultPanelModals.js"></script>
	<script src="js/AdminPortalLayout.js"></script>
	<script src="js/AdminWidgetConf.js"></script>
	<script src="js/AdminEditWidgetConf.js"></script>
	<script src="js/AdminHTMLFragment.js"></script>
	<script src="js/AdminPortalAdmins.js"></script>
	<script src="js/AdminMenuExplorer.js"></script>
	<script src="js/AdminForbiddenURL.js"></script>
	<script src="js/AdminGadgetUploadForm.js"></script>
	<script src="js/AdminInformation.js"></script>
	<!--end script-->
	
	<script>
		IS_Holiday = new IS_Widget.Calendar.iCalendar(localhostPrefix + "/holidaysrv");
		IS_Holiday.load(false);
	</script>
   	<script type="text/javascript">
		var rsaPK = new RSAKey();
		rsaPK.setPublic("<%= RSAKeyManager.getInstance().getModulus() %>", "<%= RSAKeyManager.getInstance().getPublicExponent() %>");
		
		IS_WidgetConfiguration = <jsp:include page="/widconf" flush="true" />;
		IS_WidgetIcons = <jsp:include page="/gadgeticon" flush="true" />;

		<% PortalAdminsService adminService = PortalAdminsService.getHandle(); %>
		ISA_Admin.permissions = {
			information: true
		};
		<%if( adminService.isPermitted("menu")){%>
			ISA_Admin.permissions.menu = true;
		<%} else if( adminService.isPermitted("menu_tree") ){%>
			ISA_Admin.permissions.menuTree = true;
		<%}%>
			
//		<!--start remove for SuiteFront1-->
		<%if(adminService.isPermitted("search")){%>
			ISA_Admin.permissions.searchEngine = true;
		<%}%>
//		<!--end remove for SuiteFront1-->
		
		<%if(adminService.isPermitted("widget")){%>
			ISA_Admin.permissions.widgetConf = true;
		<%}%>
		
//		<!--start remove for SuiteFront2-->
		<%if( adminService.isPermitted("defaultPanel")){%>
			ISA_Admin.permissions.defaultPanel = true;
		<%}%><%if( adminService.isPermitted("portalLayout")){%>
			ISA_Admin.permissions.portalLayout = true;
		<%}%><%if( adminService.isPermitted("i18n")){%>
			ISA_Admin.permissions.i18n = true;
		<%}%><%if( adminService.isPermitted("properties")){%>
			ISA_Admin.permissions.properties = true;
		<%}%>
//		<!--end remove for SuiteFront2-->
			
		<%if( adminService.isPermitted("proxy")){%>
			ISA_Admin.permissions.proxy = true;
		<%}%><%if( adminService.isPermitted("admins")){%>
			ISA_Admin.permissions.portalAdmin = true;
		<%}%><%if( adminService.isPermitted("forbiddenURL")){%>
			ISA_Admin.permissions.forbiddenURL = true;
		<%}%>
	</script>
</head>
<body class="infoScoop admin">
	<div id="admin-leftbox">
		<div id="admin-leftbox-title" style="float:left;cursor:pointer;" onclick="ISA_Admin.AdminTabs.setActiveTab('information');">
			<!--start of product name-->infoScoop<!--end of product name-->%{alb_administration}
		</div>
		<div style="clear:both"></div>
		<div style="float:right;padding-top:5px;">
			<img id="messageIcon" title="%{lb_messageConsole}" src="../skin/imgs/information.gif" style="cursor:pointer;" onclick="javascript:msg.showPopupDialog();">
		</div>
		<ul id="admin-leftbox-navigator" class="tabs">
		</ul>
		<div style="clear:both"></div>
	</div>
	<div id="admin-body-wrap">
		<div id="admin-body">
			<div id="admin-main-container">
				<div id="information"/>
			</div>
		</div>
	</div>
	<div id="admin-menu-navigator"></div>
	<iframe id="upLoadDummyFrame" name="upLoadDummyFrame"></iframe>
</body>
</html>
