<%--
# infoScoop OpenSource
# Copyright (C) 2010 Beacon IT Inc.
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Lesser General Public License version 3
# as published by the Free Software Foundation.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Lesser General Public License for more details.
# 
# You should have received a copy of the GNU Lesser General Public
# License along with this program.  If not, see
# <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
--%>

<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8" %>
<%@page import="org.infoscoop.service.PropertiesService" %>
<%@page import="org.infoscoop.util.RSAKeyManager"%>
<%@page import="org.infoscoop.service.ForbiddenURLService" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%
	String staticContentURL = PropertiesService.getHandle().getProperty("staticContentURL");
	if(!staticContentURL.matches("^(http|https)://.*"))
		staticContentURL = "../..";
%>
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<meta http-equiv="Pragma" content="no-cache">
	<meta http-equiv="Cache-Control" content="no-cache">

	<title>infoscoop %{alb_administration} - %{alb_defaultPanel}</title>
	
	<link rel="stylesheet" type="text/css" href="../../skin/admin.css">
	
	<!--start styles css-->
    <link rel="stylesheet" type="text/css" href="../../skin/styles.css">
    <link rel="stylesheet" type="text/css" href="../../skin/siteaggregationmenu.css">
    <link rel="stylesheet" type="text/css" href="../../skin/treemenu.css">
    <link rel="stylesheet" type="text/css" href="../../skin/calendar.css">
    <link rel="stylesheet" type="text/css" href="../../skin/pulldown.css">
    <link rel="stylesheet" type="text/css" href="../../skin/calendarinput.css">
    <link rel="stylesheet" type="text/css" href="../../skin/mySiteMap.css">
    <link rel="stylesheet" type="text/css" href="../../skin/treemenu.css">
    <link rel="stylesheet" type="text/css" href="../../skin/commandbar.css">
    <link rel="stylesheet" type="text/css" href="../../skin/tab.css">
    <link rel="stylesheet" type="text/css" href="../../skin/widget.css">
    <link rel="stylesheet" type="text/css" href="../../skin/groupsettingmodal.css">

    <link rel="stylesheet" type="text/css" href="../../skin/message.css">
    <link rel="stylesheet" type="text/css" href="../../skin/minibrowser.css">
    <link rel="stylesheet" type="text/css" href="../../skin/ranking.css">
    <link rel="stylesheet" type="text/css" href="../../skin/widgetranking.css">
    <link rel="stylesheet" type="text/css" href="../../skin/rssreader.css">
    <link rel="stylesheet" type="text/css" href="../../skin/maximizerssreader.css">
    <link rel="stylesheet" type="text/css" href="../../skin/information.css">
    <link rel="stylesheet" type="text/css" href="../../skin/ticker.css">
	<!--end styles css-->
	
	<link rel="stylesheet" type="text/css" href="../../skin/editrole.css">

	<script src="../../js/resources/resourceBundle.jsp"></script>
	<script src="../../admin/js/resources/resourceBundle.jsp"></script>
	<script src="../../js/gadget/features/core:rpc:pubsub:pubsub-2:infoscoop.js?c=1"></script>

    <script>
		<jsp:include page="/prpsrv" flush="true" />
		var isTabView = false;
		var is_sessionId = null;
		
		var IS_forbiddenURLs = <%= ForbiddenURLService.getHandle().getForbiddenURLsJSON() %>;
		
		function getInfoScoopURL() {
			var currentUrl = location.href;
			return currentUrl.replace(/\/manager\/.*/, "");
		}
		var localhostPrefix = infoURL;
		
		var infoURL=getInfoScoopURL();
		hostPrefix = infoURL;
		proxyServerURL = hostPrefix + "/proxy";
		imageURL = hostPrefix + "/skin/imgs/";
		maxColumnNum=10;
		fixedPortalHeader = false;
		useTab = false;
		is_userId = null;
		refreshInterval=-1;
		rssPageSize = 25;
		ajaxRequestRetryCount=1;
		freshDays = 1;
		rssMaxCount = 100;
		commandQueueWait = 15;
		menuAutoRefresh = false;
		widgetRefreshInterval = 36000;
		if(/^\.\/(.+)$/.test( gadgetProxyURL ) )
			gadgetProxyURL = hostPrefix +"/" +RegExp.$1;
		
		var displayTabOrder = "0";
		IS_Portal = {
			tabs: {},
			fontSize: '14px',
			isItemDragging: false,
			columnsObjs: {},
			rssSearchBoxList: {},
			showDragOverlay: function(){},
			hideDragOverlay: function() {},
			displayMsgBar: function(){},
			unDisplayMsgBar: function(){},
			adjustPanelHeight: function(){},
			adjustIframeHeight: function(){},
			adjustGadgetHeight: function(){},
			adjustMsgBar: function(){},
			deleteCacheByUrl:function(){},
			endIndicator: function(){},
			widgetDisplayUpdated: function(){},
			Trash:{
				add:function(){}
			},
			getFreshDays:function(){
				return 1;
			},
			buildIFrame:function(aTag){
				aTag.target = "_blank";
				return;
			}
		};
		
		IS_WidgetConfiguration = <jsp:include page="/widconf" flush="true" />;
		IS_WidgetIcons = <jsp:include page="/gadgeticon" flush="true" />;

		ISA_DefaultPanel = opener.ISA_DefaultPanel;
		IS_Validator = opener.IS_Validator;
		ISA_Admin = opener.ISA_Admin;
		ISA_SiteAggregationMenu = opener.ISA_SiteAggregationMenu;
	</script>
	
	<!--start script-->
	<!-- lib -->
	<script src="../../js/lib/prototype-1.7.1.js"></script>
	<script src="../../js/lib/scriptaculous-js-1.9.0/effects.js"></script>
	<script src="../../js/lib/scriptaculous-js-1.9.0/dragdrop.js"></script>
	<script src="../../js/lib/syntacticx-livepipe-ui/livepipe.js"></script>
	<script src="../../js/lib/syntacticx-livepipe-ui/resizable.js"></script>
	<script src="../../js/lib/syntacticx-livepipe-ui/window.js"></script>
	<script src="../../js/lib/date/date.js"></script>
	<script src="../../js/lib/rsa/jsbn.js"></script>
	<script src="../../js/lib/rsa/prng4.js"></script>
	<script src="../../js/lib/rsa/rng.js"></script>
	<script src="../../js/lib/rsa/rsa.js"></script>
	<script src="../../js/lib/extras-array.js"></script>
	<!-- From main utils javascript -->
	<script src="../../js/utils/utils.js"></script>
	<script src="../../js/utils/domhelper.js"></script>
	<script src="../../js/utils/msg.js"></script>
	<script src="../../js/utils/EventDispatcher.js"></script>
	<script src="../../js/utils/ajaxpool/ajax.js"></script>
	<script src="../../js/utils/ajax304.js"></script>
	<script src="../../js/utils/CalendarInput.js"></script>
	<script src="../../js/utils/Request.js"></script>
	<script src="../../js/utils/groupSettingModal.js"></script>
	<!-- From main javascript -->
	<script src="../../js/commands/UpdatePropertyCommand.js"></script>
	<script src="../../js/Tab.js"></script>	
	<script src="../../js/WidgetsContainer.js"></script>
	<script src="../../js/DragWidget.js"></script>
	<script src="../../js/widgets/Widget.js"></script>
	<script src="../../js/widgets/WidgetHeader.js"></script>
	<script src="../../js/widgets/WidgetEdit.js"></script>
	<script src="../../js/SiteAggregationMenu.js"></script>
	<script src="../../js/SiteMap.js"></script>
	<script src="../../js/TreeMenu.js"></script>
	<script src="../../js/widgets/rssreader/RssReader.js"></script> 
	<script src="../../js/widgets/MultiRssReader/MultiRssReader.js"></script> 
	<script src="../../js/widgets/rssreader/RssItemRender.js"></script>
	<script src="../../js/widgets/calendar/Calendar.js"></script>
	<script src="../../js/widgets/calendar/iCalendar.js"></script>
	<script src="../../js/widgets/MiniBrowser/MiniBrowser.js"></script>
	<script src="../../js/widgets/MiniBrowser/FragmentMiniBrowser.js"></script>
	<script src="../../js/widgets/information/Information.js"></script>
	<script src="../../js/widgets/information/Information2.js"></script>
	<script src="../../js/widgets/ticker/Ticker.js"></script>
	<script src="../../js/widgets/ranking/Ranking.js"></script>
	<script src="../../js/widgets/ranking/RankingRender.js"></script>
	<script src="../../js/widgets/WidgetRanking/WidgetRanking.js"></script>
	<script src="../../js/widgets/Message/Message.js"></script>
	<!-- From admin javascript -->
	<script src="../../admin/js/lib/popupmenu.js"></script>
	<script src="../../admin/js/AdminWidgetConf.js"></script>
	<script src="../../admin/js/AdminEditWidgetConf.js"></script>
	<script src="../../admin/js/AdminHTMLFragment.js"></script>
	<script src="../../admin/js/AdminCommonModals.js"></script>
	<script src="../../admin/js/AdminEditRole.js"></script>
	<!--end script-->
	
	<script src="../../js/lib/jquery-1.9.1.min.js"></script>
	<link rel="stylesheet" type="text/css" href="../../js/lib/jquery-ui/jquery-ui-1.8.13.custom.css">
	<link rel="stylesheet" type="text/css" href="../../js/lib/jquery-ui/jquery.ui.button.css">
	<script src="../../js/lib/jquery-ui-1.8.13.custom.min.js"></script>
	<script src="../../js/lib/jquery-ui/jquery.ui.button.js"></script>
	
	<script type="text/javascript">
		jQuery.noConflict();
		$jq = jQuery;
		
		var rsaPK = new RSAKey();
		rsaPK.setPublic("<%= RSAKeyManager.getInstance().getModulus() %>", "<%= RSAKeyManager.getInstance().getPublicExponent() %>");
		
		var editRoleScreen = true;
	</script>

	<script type="text/javascript">
		$jq(function(){
			var id = "<%= request.getParameter("id") %>";
			jsonRole = openerPanel.displayRoleJsons[id];
			init();
		});
	</script>

</head>

<body class="infoScoop">

<c:import url="/WEB-INF/jsp/admin/defaultpanel/_formTab.jsp"/>

<div style="display:none" id='edit_layout_modal' title="%{alb_editHTML}">
	<div>%{alb_editHTMLandOk}</div>
	<div style="text-align:center;"><textarea id='edit_layout_textarea' rows='20' style='width:90%'></textarea></div>
	<center>
		<input id='edit_layout_ok' type="button" value="%{alb_ok}"/>
		<input id='edit_layout_cancel' type="button" value="%{alb_cancel}"/>
	</center>
</div>
<div style="display:none" id='select_layout_modal' title="%{alb_selectLayout}">
	<div style="height:25px;">
		<div style="float:left;padding:3;">%{alb_number_of_gadgets}</div>
		<div style="font-size:70%;" id="gadgetsnum_buttonset"></div>
	</div>
	<c:import url="/WEB-INF/jsp/admin/defaultpanel/_layoutTemplates.jsp"/>
	
	<div style="clear:both;text-align:center;">
		<input id='select_layout_cancel' type="button" value="%{alb_cancel}"/>
	</div>
</div>
</body>
</html>
