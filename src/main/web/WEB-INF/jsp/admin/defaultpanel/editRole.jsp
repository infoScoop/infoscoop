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

<!doctype HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page contentType="text/html; charset=UTF-8" %>
<%@page import="org.infoscoop.service.PropertiesService" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<meta http-equiv="Pragma" content="no-cache">
	<meta http-equiv="Cache-Control" content="no-cache">

	<title>infoscoop %{alb_administration} - %{alb_defaultPanel}</title>

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

	<script type="text/javascript" src="../../js/lib/prototype-1.6.0.3.js"></script>
	<script src="../../js/lib/scriptaculous-js-1.8.2/effects.js"></script>
	<script src="../../js/lib/scriptaculous-js-1.8.2/dragdrop.js"></script>
	<script src="../../js/lib/scriptaculous-js-1.8.2/controls.js"></script>

	<script src="../../js/resources/resourceBundle.jsp"></script>
	<script src="../../admin/js/resources/resourceBundle.jsp"></script>
	<script src="../../js/gadget/features/core:rpc:pubsub:infoscoop.js?c=1"></script>
	<script>

	<jsp:include page="/prpsrv" flush="true" />
	
	function getInfoScoopURL() {
		var currentUrl = location.href;
		return currentUrl.replace(/\/manager\/.*/, "");
	}
	var infoURL=getInfoScoopURL();
	ajaxRequestTimeout=15000;
	messagePriority = 4;
	localhostPrefix = infoURL;
	hostPrefix = infoURL;
	proxyServerURL = hostPrefix + "/proxy";
	imageURL = infoURL + "/skin/imgs/";
	maxColumnNum=10;
	fixedPortalHeader = false;
	useTab = false;
	isTabView = false;
	is_userId = null;
	refreshInterval=-1;
	rssPageSize = 25;
	ajaxRequestRetryCount=1;
	freshDays = 1;
	rssMaxCount = 100;
	commandQueueWait = 15;
	menuAutoRefresh = false;
	widgetRefreshInterval = 36000;
	gadgetProxyURL = localhostPrefix + "/gadgetsrv";

	IS_R['getResource'] = function(s){return s;}
	var displayTabOrder = "0";
	IS_Portal = {
		tabs: {},
		fontSize: '14px',
		isItemDragging: false,
		columnsObjs: {},
		rssSearchBoxList: {},
		isChecked: function(menuItem){
			var isChecked = false;
			
			for(var tabId in IS_Portal.widgetLists){
				var widgetList = IS_Portal.widgetLists[tabId];
				for(var i in widgetList){
					if(!widgetList[i] || !widgetList[i].id) continue;
					
					if (/MultiRssReader/.test(widgetList[i].widgetType)) {
						if(!widgetList[i].isBuilt){
							// Judge subWidget by refering inside the feed if not build yet.
							var feed = widgetList[i].widgetConf.feed;
							for(var j in feed){
								var check = (feed[j].id && (feed[j].id.substring(2) == menuItem.id)
										&& (feed[j].property.relationalId != IS_Portal.getTrueId(widgetList[i].id) || feed[j].isChecked));
								if(/true/i.test(check)){
									isChecked = true;
									break;
								}
							}
						}
					}else{
						if(widgetList[i].id.substring(2) == menuItem.id){
							isChecked = true;
							break;
						}
					}
				}
			}
			return isChecked;
		},
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
		getFreshDays:function(){}
	}
	ISA_DefaultPanel = opener.ISA_DefaultPanel;
	IS_Validator = opener.IS_Validator;
	ISA_Admin = opener.ISA_Admin;
	
	ISA_Admin.createBaseRadio = function(name, isChecked, isDisabled) {
		var radio = document.createElement("input");
		radio.type = "radio";
		radio.name = name;
		if(isChecked)
			radio.checked = String(isChecked);
		if(isDisabled)
			radio.disabled = String(isDisabled);
		
		if(Browser.isIE) {
			var inputElement = "";
			inputElement += "<";
			inputElement += "input type='radio' name='" + name + "'";
			if(isChecked)
				inputElement += " checked";
			if(isDisabled)
				inputElement += " disabled";
			inputElement += ">";
			radio = document.createElement(inputElement);
		}
		return radio;
	};

	ISA_Admin.createBaseCheckBox = function(name, isChecked, isDisabled) {
		var checkbox = document.createElement("input");
		checkbox.type = "checkbox";
		checkbox.name = name;
		if(isChecked)
			checkbox.checked = String(isChecked);
		if(isDisabled)
			checkbox.disabled = String(isDisabled);
		
		if(Browser.isIE) {
			var inputElement = "";
			inputElement += "<";
			inputElement += "input type='checkbox' name='" + name + "'";
			if(isChecked)
				inputElement += " checked";
			if(isDisabled)
				inputElement += " disabled";
			inputElement += ">";
			checkbox = document.createElement(inputElement);
		}
		return checkbox;
	};
	
	ISA_SiteAggregationMenu = opener.ISA_SiteAggregationMenu;
	
	IS_WidgetConfiguration = <jsp:include page="/widconf" flush="true" />;
	IS_WidgetIcons = <jsp:include page="/gadgeticon" flush="true" />;
	</script>
	
	<!--start script-->
	<script src="../../js/utils/utils.js"></script>
	<script src="../../js/utils/ajax304.js"></script>
	<script src="../../js/lib/control.modal.js"></script>
	<script src="../../js/lib/extras-array.js"></script>
	<script src="../../js/lib/date/date.js"></script>
	<script src="../../js/utils/domhelper.js"></script>
	<script src="../../js/SiteAggregationMenu.js"></script>
	<script src="../../js/SiteMap.js"></script>
	<script src="../../js/TreeMenu.js"></script>
	<script src="../../js/utils/EventDispatcher.js"></script>
    <script src="../../js/utils/groupSettingModal.js"></script>
	<script src="../../js/Tab.js"></script>	
	<script src="../../js/WidgetsContainer.js"></script>
	<script src="../../js/utils/ajaxpool/ajax.js"></script>
	<script src="../../js/utils/Request.js"></script>
	<script src="../../js/utils/msg.js"></script>
	<script src="../../js/DragWidget.js"></script>
	<script src="../../js/widgets/Widget.js"></script>
	<script src="../../js/widgets/WidgetHeader.js"></script>
	<script src="../../js/widgets/WidgetEdit.js"></script>
	<script src="../../js/commands/UpdatePropertyCommand.js"></script>
    <script src="../../js/utils/CalendarInput.js"></script>

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
    <script src="../../js/widgets/Message/MaximizeMessage.js"></script>
	
	<script src="../../admin/js/AdminWidgetConf.js"></script>
	<script src="../../admin/js/AdminEditWidgetConf.js"></script>
	<script src="../../admin/js/AdminCommonModals.js"></script>
	<!--end script-->
	
	<script src="../../js/lib/jquery-1.5.2.min.js"></script>
	<link rel="stylesheet" type="text/css" href="../../js/lib/jquery-ui/jquery-ui-1.8.13.custom.css">
	<script src="../../js/lib/jquery-ui-1.8.13.custom.min.js"></script>
	
	<script type="text/javascript">
		jQuery.noConflict();
		$jq = jQuery;
	</script>
	<script src="../../admin/js/AdminEditRole.js"></script>

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
	<c:import url="/WEB-INF/jsp/admin/defaultpanel/_layoutTemplates.jsp"/>
	<div style="clear:both;text-align:center;">
		<input id='select_layout_cancel' type="button" value="%{alb_cancel}"/>
	</div>
</div>
</body>
</html>
