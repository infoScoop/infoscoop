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
<%
	PropertiesService propService = PropertiesService.getHandle();
	%>
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<meta http-equiv="Pragma" content="no-cache">
	<meta http-equiv="Cache-Control" content="no-cache">

	<title>infoscoop %{alb_administration} - %{alb_defaultPanel}</title>


	<link rel="stylesheet" type="text/css" href="../../skin/admin.css">
	<link rel="stylesheet" type="text/css" href="../../skin/admintreemenu.css">

	<!--start styles css-->
	<link rel="stylesheet" type="text/css" href="../../skin/styles.css">
	<link rel="stylesheet" type="text/css" href="../../skin/siteaggregationmenu.css">
	<link rel="stylesheet" type="text/css" href="../../skin/treemenu.css">
	<link rel="stylesheet" type="text/css" href="../../skin/calendar.css">
	<link rel="stylesheet" type="text/css" href="../../skin/pulldown.css">
	<link rel="stylesheet" type="text/css" href="../../skin/calendarinput.css">
	<link rel="stylesheet" type="text/css" href="../../skin/mySiteMap.css">
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
	<link rel="stylesheet" type="text/css" href="../../js/lib/jquery-ui/jquery-ui-1.8.13.custom.css">

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
	ajaxRequestRetryCount=2;
	freshDays = 1;
	rssMaxCount = 100;
	commandQueueWait = 15;
	menuAutoRefresh = false;
	menuAutoRefresh = false;
	gadgetProxyURL = localhostPrefix + "/gadgetsrv";

	IS_R['getResource'] = function(s){return s;}
	var displayTabOrder = "0";
	IS_Portal = {
		tabs: {},
		fontSize: '14px',
		isItemDragging: false,
		columnsObjs: {},
		rssSearchBoxList: {},
		isChecked: function(){return false},
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
	ISA_SiteAggregationMenu = opener.ISA_SiteAggregationMenu;
	var msg = opener.msg;
	
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
	
	<script src="../../js/lib/jquery-1.6.1.min.js"></script>
	<script src="../../js/lib/jquery-ui-1.8.13.custom.min.js"></script>
	
	<script type="text/javascript" class="source">
	jQuery.noConflict();
	$jq = jQuery;

	var openerPanel = ISA_DefaultPanel.defaultPanel;

	var areaType = 0;
	var jsonRole;
	
	IS_SidePanel.adjustPosition = function(){};
	IS_Request.CommandQueue = {
		addCommand: function(){}
	}
	
	// override 
	IS_Portal.addTab = function(idNumber, name, type, numCol, columnsWidth, disabledDynamicPanel, isInitialize){
		IS_Portal.widgetLists["tab"+idNumber] = new Object();
		IS_Portal.columnsObjs["tab"+idNumber] = {};
//		IS_Portal.tabs["tab"+idNumber] = {"numCol":0,type:"static",adjustStaticHeight:adjustStaticHeight};
		IS_Portal.tabs["tab"+idNumber] = {"numCol":0,type:"static"};
		var panels = $("panels");
		if(panels){
			var panelDiv = IS_Portal.buildPanel( idNumber, type );
			panelDiv.style.display = "";
			panels.appendChild( panelDiv );
			IS_Portal.tabs["tab"+idNumber].panel = panelDiv;
			IS_WidgetsContainer.rebuildColumns("tab"+idNumber, numCol, columnsWidth, false, isInitialize);
		}
		adjustStaticWidgetHeight();
		prepareStaticArea();
	}
	
	// override 
	IS_Portal.addWidget = function(widget, tabId){
		if(!tabId) tabId = IS_Portal.currentTabId;
		var widgetId = IS_Portal.getTrueId(widget.id);
		
		IS_Portal.widgetLists[tabId][widgetId] = widget;
		
		if(widget.widgetConf.parentId)
			widget.draggable = false;
		IS_EventDispatcher.addListener("closeWidget", widget.id.substring(2), saveDynamicPanel, true);
	}
	
	// override
	IS_Widget.RssReader.dropGroup.add = function(){};
	
	// override
	IS_Widget.Message.checkNewMsg = function(){};
	
	IS_Customization = {"commandbar":"<table cellpadding=\"0\" cellspacing=\"3\" width=\"100%\">\r\n\t<tr>\r\n\t\t<td width=\"100%\"><div id=\"p_1_w_4\"><\/div><\/td>\r\n\t\t<td><div id=\"p_1_w_6\"><\/div><\/td>\r\n\t\t<td><div id=\"portal-go-home\"><\/div><\/td>\r\n\t\t<td><div id=\"disabled_portal-change-fontsize\" disabledCommand=\"true\"><!--&lt;div id=\"portal-change-fontsize\"&gt;&lt;/div&gt;--><\/div><\/td>\r\n\t\t<td><div id=\"portal-trash\"><\/div><\/td>\r\n\t\t<td><div id=\"portal-preference\"><div class=\"allPreference\"><\/div><\/div><\/td>\r\n\t\t<td><div id=\"disabled_portal-credential-list\" disabledCommand=\"true\"><!--&lt;div id=\"portal-credential-list\"&gt;&lt;/div&gt;--><\/div><\/td>\r\n\t\t<td><div id=\"portal-admin-link\"><\/div><\/td>\r\n\t\t<td><div id=\"disabled_portal-logout\" disabledCommand=\"true\"><!--&lt;div id=\"portal-logout\"&gt;&lt;/div&gt;--><\/div><\/td>\r\n\t<\/tr>\r\n<\/table>\r\n","staticPanel0":"<DIV>\r\n\t<DIV style=\"FLOAT: left; WIDTH: 74.5%\">\r\n\t\t<DIV style=\"HEIGHT: 178px\">\r\n\t\t\t<DIV style=\"FLOAT: left; WIDTH: 100%; HEIGHT: 100%\">\r\n\t\t\t\t<DIV id=\"p_1_w_1\" class=\"static_column\" style=\"MARGIN-LEFT: 2px; HEIGHT: 170px\"><\/DIV>\r\n\t\t\t<\/DIV>\r\n\t\t<\/DIV>\r\n\t<\/DIV>\r\n\t<DIV style=\"FLOAT: right; WIDTH: 25%; HEIGHT: 178px\">\r\n\t\t<DIV id=\"p_1_w_5\" class=\"static_column\" style=\"MARGIN-LEFT: 2px; HEIGHT: 170px\"><\/DIV>\r\n\t<\/DIV>\r\n<\/DIV>\r\n<DIV style=\"CLEAR: both; display:none;\"/>\r\n","contentFooter":[{"type":"mail"},{"type":"message"}],"css":"/* Custom CSS code is described here.  */","header":"<table width=\"100%\" height=\"53px\" cellspacing=\"0\" cellpadding=\"0\" style=\"background:url(skin/imgs/head_blue.png)\">\r\n\t<tbody>\r\n\t\t<tr>\r\n\t\t\t<td><a href=\"javascript:void(0)\" onclick=\"javascript:IS_Portal.goHome();return false;\"><img src=\"skin/imgs/infoscoop.gif\" alt=\"infoScoop\" border=\"0\" style=\"margin:0 0 0 20px;\" height=\"45\"/><\/a>\r\n\t\t\t<\/td>\r\n\t\t\t<td>\r\n\t\t\t\t<form name=\"searchForm\" onsubmit=\"javascript:IS_Portal.SearchEngines.buildSearchTabs(document.getElementById('searchTextForm').value);return false;\">\r\n\t\t\t\t<div style=\"float:right;margin-right:5px\">\r\n\t\t\t\t\t<table>\r\n\t\t\t\t\t\t<tbody>\r\n\t\t\t\t\t\t\t<tr>\r\n\t\t\t\t\t\t\t\t<td colspan=\"2\" align=\"right\" style=\"font-size:80%;\">\r\n\t\t\t\t\t\t\t\t\tWelcome,admin-\r\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<\/td>\r\n\t\t\t\t\t\t\t<\/tr>\r\n\t\t\t\t\t\t\t<tr>\r\n\t\t\t\t\t\t\t\t<td>\r\n\t\t\t\t\t\t\t\t\t<input id=\"searchTextForm\" type=\"text\" style=\"width:200px;height:23px;float:left;\"/>\r\n\t\t\t\t\t\t\t\t\t<input type=\"submit\" value=\"Search\" style=\"padding:0 0.4em;\"/>\r\n\t\t\t\t\t\t\t\t\t<span id=\"editsearchoption\">Search options<\/span>\r\n\t\t\t\t\t\t\t\t<\/td>\r\n\t\t\t\t\t\t\t<\/tr>\r\n\t\t\t\t\t\t<\/tbody>\r\n\t\t\t\t\t<\/table>\r\n\t\t\t\t<\/div>\r\n\t\t\t\t<\/form>\r\n\t\t\t<\/td>\r\n\t\t<\/tr>\r\n\t<\/tbody>\r\n<\/table>","title":"infoScoop"};
	IS_Portal.CommandBar = {
	  init:function(){}
	}

	function isHidePanel(){
		return false;
	}
	// mock for search engines
	IS_Portal.searchWidgetAndFeedNode = function(){};
	IS_Portal.SearchEngines = {
		matchRssSearch : function(){return false;},
		loadConf : function(){}
	};
	// mock for behindIframe
	IS_Portal.behindIframe = {
		show:function(){},
		hide:function(){}
	}
	IS_forbiddenURLs = {};
	
	// mock for msgbar
	IS_Portal.closeMsgBar = function(){};
	
	var ISA_Principals = window.opener.ISA_Principals;
	
	function prepareStaticArea(){
		var tabId = IS_Portal.currentTabId.replace("tab","");
		$jq('#staticAreaContainer .static_column').each(function(j){
			var containerId = $jq(this).attr("id");
			div = $jq(this).data("containerId", $jq(this).attr("id"));
			
			var widgetJSON = jsonRole.staticPanel[containerId];
			if(!widgetJSON)
				widgetJSON = {type:"notAvailable", id: containerId};
			var editorFormObj =	new ISA_CommonModals.EditorForm(div.get(0), function(widgetJSON){
				
				var selectType = ISA_CommonModals.EditorForm.getSelectType();
				if( widgetJSON.type != selectType )
				widgetJSON.properties = {};

				var oldId = widgetJSON.id;
				widgetJSON.id = "w_"+new Date().getTime();
				widgetJSON.type = ISA_CommonModals.EditorForm.getSelectType();
				widgetJSON.properties = ISA_CommonModals.EditorForm.getProperty(widgetJSON);
				widgetJSON.ignoreHeader = ISA_CommonModals.EditorForm.isIgnoreHeader();
				if(!widgetJSON.ignoreHeader) delete widgetJSON.ignoreHeader;
				widgetJSON.noBorder = ISA_CommonModals.EditorForm.isNoBorder();
				if(!widgetJSON.noBorder) delete widgetJSON.noBorder;

				widgetJSON.title = ISA_Admin.trim($("formTitle").value);
				widgetJSON.href =  $("formHref").value;

				delete jsonRole.staticPanel[oldId];
				jsonRole.staticPanel[widgetJSON.id] = widgetJSON;
				jsonRole.layout = jsonRole.layout.replace( escapeHTMLEntity( oldId ),widgetJSON.id );
				$jq("#" + oldId).attr("id", widgetJSON.id);
				$jq("#s_" + oldId).attr("id", "s_" + widgetJSON.id);
				
				displayStaticGadget(
					{
						id: widgetJSON.id,
						tabId : jsonRole.tabId,
						href : $("formHref").value,
						title : ISA_Admin.trim($("formTitle").value),
						siblingId :"",
						ignoreHeader : ISA_CommonModals.EditorForm.isIgnoreHeader(),
						noBorder : ISA_CommonModals.EditorForm.isNoBorder(),
						type : ISA_CommonModals.EditorForm.getSelectType(),
						property : ISA_CommonModals.EditorForm.getProperty(widgetJSON)
					});
					
				if( Control.Modal.current ) {
					Control.Modal.close();
				} else {
					Control.Modal.container.hide();
				}
				
				openerPanel.isUpdated = true;
				ISA_Admin.isUpdated = true;
			},{
				menuFieldSetLegend:ISA_R.alb_widgetHeaderSettings,
				setDefaultValue: false,
				disableMiniBrowserHeight: true,
				showIgnoreHeaderForm:true,
				showNoBorderForm:true,
				displayACLFieldSet:false,
				disableDisplayRadio:true,
				omitTypeList:['Ranking','Ticker','MultiRssReader']
			});
			
			var edit_cover = $jq("<div></div>")
				.attr("id", "edit_div_" + j)
				.addClass("edit_static_gadget")
				.hide()
				.click(function(e){
					return function(){
						editorFormObj.showEditorForm(e.value);
					}
				}({value:widgetJSON})).appendTo(div);
			div.mouseover(function(){
				var $this = $jq(this);
				edit_cover
					.text(($this.attr("id") == $this.data("containerId")) ? "New" : "Edit")
					.show();
			})
			.mouseout(function(){
				edit_cover.hide();
			});
		});
		$jq("#layout").val($jq("#staticAreaContainer").html());
	};

	function adjustStaticWidgetHeight(){
		if(areaType != 2) return;
		IS_Portal.adjustStaticWidgetHeight();
		var columns = $$("#staticAreaContainer .static_column");
		var windowHeight = getWindowSize(false) - findPosY($("staticAreaContainer")) - 36;
		for(var i =0; i < columns.length; i++){
			columns[i].style.height = windowHeight;
		}
	}

	function init() {
		areaType = jsonRole.disabledDynamicPanel ? 1 : 0;
		areaType = jsonRole.adjustToWindowHeight ? 2 : areaType;
		
		IS_Portal.currentTabId = "tab" + jsonRole.tabId;
		IS_Portal.trueTabId = "tab" + jsonRole.tabId;
		
		//hide userprefs
		
		
		//create princapalType
		/*
		var selectPrincipal = document.createElement("select");
		selectPrincipal.id = selectPrincipal.name = "principalType";
		*/
		var principalMap = ISA_Principals.get();
		var principalLength = principalMap.length;

		for(var i = 0; i < principalLength; i++) {
			/*
			var opt = document.createElement("option");
			opt.value = principalMap[i].type;
			opt.innerHTML = principalMap[i].displayName;
			selectPrincipal.appendChild( opt );
			*/
			if(principalMap[i].type == jsonRole.principalType){
				$jq("#principalTypeDiv").text(principalMap[i].displayName);
				break;
			}
		}
//		$jq("#principalTypeDiv").append($jq(selectPrincipal));
		
		//init params
		$jq("#roleName").text(jsonRole.roleName);
		$jq("#role").text(jsonRole.role);
		/*
		$jq("#roleName").val(jsonRole.roleName)
			.change(function(e){
				var nowText = ISA_Admin.trim( this.value );
				if(nowText.length == 0) {
					this.value = $jq(this).data("beforeNameText");
					this.focus();
					return false;
				}
				var error = IS_Validator.validate(nowText, {maxBytes:256, label:ISA_R.alb_roleName});
				if(error){
					alert(error);
					this.select();
					return false;
				}
				openerPanel.setNewValue("roleName", nowText);
				var roleEl = opener.document.getElementById("tab_"+openerPanel.displayTabId+"_role_" + openerPanel.displayRoleOrder);
				var roleNameDiv = roleEl.getElementsByTagName('td')[1];
				roleNameDiv.firstChild.innerHTML = nowText;
			})
			.focus(function (e){
				$jq(this).data("beforeNameText", this.value);
			});
		
		$jq("#principalType").val(jsonRole.principalType)
			.change(function (e){
				openerPanel.setNewValue("principalType", this.value);
				var roleEl = opener.document.getElementById("tab_"+openerPanel.displayTabId+"_role_" + openerPanel.displayRoleOrder);
				var roleTypeDiv = roleEl.getElementsByTagName('select')[0];
				roleTypeDiv.value = this.value;
			});
		
		$jq("#role").val(jsonRole.role)
			.change(function(e){
				var nowText = ISA_Admin.trim( this.value );
				if(nowText.length == 0) {
					this.value = $jq(this).data("beforeNameText");
					this.focus();
					return false;
				}
				var error = IS_Validator.validate(nowText, {format:'regexp', label:ISA_R.alb_regularExpression});
				if(error){
					alert(error);
					this.select();
					return false;
				}
				openerPanel.setNewValue("role", this.value);
				var roleEl = opener.document.getElementById("tab_"+openerPanel.displayTabId+"_role_" + openerPanel.displayRoleOrder);
				var roleNameDiv = roleEl.getElementsByTagName('td')[3];
				roleNameDiv.firstChild.innerHTML = this.value;
			})
			.focus(function(e){
				$jq(this).data("beforeNameText", this.value);
			});
		*/
		
		$jq("#tabName").val(jsonRole.tabName)
			.change(function(e){
				var nowText = ISA_Admin.trim( this.value );
				if(nowText.length == 0) {
					this.value = $jq(this).data("beforeNameText");
					this.focus();
					return false;
				}
				var error = IS_Validator.validate(nowText, {maxBytes:256, label:ISA_R.alb_tabName});
				if(error){
					alert(error);
					this.select();
					return false;
				}
				openerPanel.setNewValue("tabName", nowText);
			})
			.focus(function(e){
				$jq(this).data("beforeNameText", this.value);
			});
		
		$jq("#areaType").val(areaType);
		$jq("#numberOfColumns")
			.val(String( jsonRole.columnsArray.length ))
			.change(function(){
				IS_WidgetsContainer.rebuildColumns(IS_Portal.currentTabId, parseInt(jQuery(this).val()));

				var colNumber = this.value;
				// A column width is the number of columns devided by 100
				var colWidth = parseInt(100 / colNumber * 10) / 10;
				var colArray = [];
				var colWidthSum = 0;
				for(var i = 1; i <= colNumber; i++) {
					var width = colWidth;
					// Fraction goes to the last column -> Because it must go to 100 if it is added
					// Subtract from 1000 because the width gets ten times, then devided by 10.
					if(i == colNumber) {
						width = 1000 - colWidthSum;
						width /= 10;
					}
					// Decuple and cast to integer to prevent from rounding error.
					colWidthSum += width * 10;
					colArray.push( String(width) + "%" );
				}
				
				jsonRole.columnsWidth = Object.toJSON(colArray);
				openerPanel.setColumnsArray(jsonRole);
				
				openerPanel.isUpdated = true;
				ISA_Admin.isUpdated = true;
			});
		
		//set static container
		$jq("#staticAreaContainer").html(jsonRole.layout);
		
		$jq(".submit_button").click(function(){window.close();});
//		$jq(".cancel_button").click(function(){window.close();});

		//Holiday information
		IS_Holiday = new IS_Widget.Calendar.iCalendar(localhostPrefix + "/holidaysrv");
		IS_Holiday.noProxy = true;
		IS_Holiday.load(false);

		if(areaType == 0) {
			$("infoscoop").addClassName("areaType0");
			$("customizedArea").show();
			new IS_SiteAggregationMenu(true, true, true);
			new IS_SidePanel.SiteMap(true, true, true);
		}
		new IS_WidgetsContainer("/manager/defaultpanel/widsrv?tabId=" + jsonRole.tabId + "&roleOrder=" + jsonRole.roleOrder);

		//menuItem to panel
		var panelBody = document.body;
		var widopt = {
		  accept: function(element, widgetType, classNames){
			  return (classNames.detect( 
				  function(v) { return ["widget"].include(v) } ) &&
					  (widgetType != "mapWidget") );
		  },
		  onHover: function(element, dropElement, dragMode, point) {
			var x = point[0] - element.boxLeftDiff;
			var y = point[1];//Leave y axis as getNearDropTarget

			var min = 10000000;
			var nearGhost = null;// widget near widget ghost
			var widgetGhost = IS_Draggable.ghost;
			widgetGhost.style.display = "block";//for Safari
			var scrollOffset = IS_Portal.tabs[IS_Portal.currentTabId].panel.scrollTop;//IS_Portal.columnsObjs must be in the panel.
			for ( var i=1; i <= IS_Portal.tabs[IS_Portal.currentTabId].numCol; i++ ) {
				var col = IS_Portal.columnsObjs[IS_Portal.currentTabId]["col_dp_" + i];
				for (var j=0; j<col.childNodes.length; j++ ) {
					var div = col.childNodes[j];
					if (div == widgetGhost) {
						continue;
					}
					
					var left = div.posLeft;//Coordinate exclude ghost
					var top = div.posTop - scrollOffset;
					
					var tmp = Math.sqrt(Math.pow(x-left,2)+ Math.pow(y-top,2));
					if (isNaN(tmp)) {
						continue;
					}
					
					if ( tmp < min ) {
						min = tmp;
						nearGhost = div;
						nearGhost.col = col;
					}
					
				}
			}
			
			if (nearGhost != null && widgetGhost.nextSibling != nearGhost) {
				widgetGhost.style.display = "block";
				nearGhost.parentNode.insertBefore(widgetGhost,nearGhost);
				widgetGhost.col = nearGhost.col;
			}
		  },
		  onDrop : function(element, lastActiveElement, widget, event) {
			if(!IS_Portal.canAddWidget()) return;
			var widgetGhost = IS_Draggable.ghost;
			if( !Browser.isSafari ||( widgetGhost && widgetGhost.style.display != "none")){
				var ghostColumnNum = (widgetGhost.col)? widgetGhost.col.getAttribute("colNum"):1;
				widget.widgetConf.column = ghostColumnNum;
				widgetGhost.col.replaceChild(element, widgetGhost);
			} else {
			    widgetGhost.col.removeChild( widgetGhost );
			}
			element.style.position = "";

			var tmpParent =false;
			if(widget.parent){// If sub widget is dropped on the panel.
				element.className="widget";
				widget.tabId = IS_Portal.currentTabId;

				tmpParent = widget.parent;
				IS_Portal.removeSubWidget(widget.parent.id, widget.id);
				//widget.parent.content.removeRssReader(widget.id, false, true);
				//The item deleted from Multi is not removeWidget but setWidgetLocationCommand.
				IS_EventDispatcher.newEvent("applyIconStyle", widget.id);
				IS_EventDispatcher.newEvent("changeConnectionOfWidget", widget.id);
				IS_EventDispatcher.newEvent("applyIconStyle", tmpParent.id);
				
				tmpParent.content.mergeRssReader.isComplete = false;
			}
			
			//Send to Server
			IS_Widget.setWidgetLocationCommand(widget);

			if(tmpParent)
				tmpParent.content.checkAllClose(true);
			
			// TODO: Processing of removing edit tip of title. Processing should be within WidgetHeader
			if(widget.headerContent && widget.headerContent.titleEditBox){
				widget.headerContent.titleEditBox.style.display = "none";
			}
			
			if( widget.isGadget()) {
				if( Browser.isIE ) {
					IS_Portal.adjustGadgetHeight( widget,true );
				} else {
					widget.loadContents();
				}
			}
			
			IS_EventDispatcher.newEvent("moveWidget", widget.id);
			saveDynamicPanel();
		  }
		}
		IS_Droppables.add(panelBody, widopt);
		
		var menuopt = {};
		menuopt.accept = "menuItem";
		menuopt.onDrop = function(element, lastActiveElement, menuItem, event) {
			if(!IS_Portal.canAddWidget()) return;
			
			var widgetGhost = IS_Draggable.ghost;
			var ghostColumnNum = (widgetGhost.col)? widgetGhost.col.getAttribute("colNum"):1;
			
			var parentItem = menuItem.parent;
			var p_id;
			var divParent;
			
			if(parentItem){
				p_id = IS_Portal.currentTabId+"_p_" + parentItem.id;
				divParent = $(p_id);
			}

			var widgetConf;
			var subWidgetConf;
			if(/MultiRssReader/.test(menuItem.type)){
				if(!divParent){
					// TODO: Processing of cooperative menu
					var parentItem = menuItem.parent;
					var w_id = IS_Portal.currentTabId + "_p_" + parentItem.id;

					var childMenuList = [];
					var children = parentItem.children;
					for(var i = 0; i < children.length ;i++){
						var feedNode = children[i];
						if(feedNode.type && /MultiRssReader/.test(feedNode.type)){
							childMenuList.push(feedNode.id);
						}
					}
					if(!parentItem.properties)parentItem.properties = [];
					parentItem.properties.children = childMenuList;

					parentItem.properties["itemDisplay"] = parentItem["display"];
					widgetConf = IS_WidgetsContainer.WidgetConfiguration.getConfigurationJSONObject(
						"MultiRssReader", w_id, ghostColumnNum, parentItem.title, parentItem.href, parentItem.properties);
					
					subWidgetConf = IS_WidgetsContainer.WidgetConfiguration.getFeedConfigurationJSONObject(
									"RssReader", "w_" + menuItem.id, menuItem.title, menuItem.href, "false", menuItem.properties);
					subWidgetConf.menuId = menuItem.id;
					subWidgetConf.parentId = "p_" + menuItem.parentId;
				}
			}else{
				/* Recreate config everytime because menu can be changed */
				// Create JSONObject from menuItem
				widgetConf = IS_SiteAggregationMenu.getConfigurationFromMenuItem(menuItem, ghostColumnNum);
			}
			
			var widget;
			if(/MultiRssReader/.test(menuItem.type) && divParent){//Drop cooperative menu from menu
				var parentItem = menuItem.parent;
	//			var targetWidget = IS_Portal.widgetLists[IS_Portal.currentTabId][p_id];
				var targetWidget = IS_Portal.getWidget(p_id, IS_Portal.currentTabId);
				
				// Head at order display of time.
				var siblingId;
				var nextSiblingId;
				if(targetWidget.getUserPref("displayMode") == "time"){
					siblingId = "";
					nextSiblingId = "";
				}else{
					siblingId = (widgetGhost.previousSibling) ? widgetGhost.previousSibling.id : "";
					nextSiblingId = (widgetGhost.nextSibling) ? widgetGhost.nextSibling.id : "";
				}
				var w_id = "w_" + menuItem.id;
	//			menuItem.type="RssReader";
				var widgetConf = IS_SiteAggregationMenu.getConfigurationFromMenuItem(menuItem, ghostColumnNum);
				widgetConf.type = "RssReader";
				
				// subWidget in the same tab is always built
				var currentTabId = IS_Portal.currentTabId;
				if( Browser.isSafari1 && targetWidget.content.isTimeDisplayMode())
					IS_Portal.currentTabId = "temp";
				
				widgetConf.parentId = "p_" + menuItem.parentId;
				widget = IS_WidgetsContainer.addWidget( currentTabId, widgetConf , true, function(w){
					w.elm_widget.className = "subWidget";
					widgetGhost.parentNode.replaceChild(w.elm_widget, widgetGhost);
				});//TODO: The way of passing sub widget.
				
				IS_Portal.widgetDropped( widget );
				
				if( Browser.isSafari1 && targetWidget.content.isTimeDisplayMode())
					IS_Portal.currentTabId = currentTabId;
				
	//			IS_Portal.subWidgetMap[targetWidget.id].push(widget.id);
				IS_Portal.addSubWidget(targetWidget.id, widget.id);
				targetWidget.content.addSubWidget(widget, nextSiblingId);
				if(widget.isBuilt)widget.blink();
				
				//Send to Server
				IS_Widget.setWidgetLocationCommand(widget);

				if( targetWidget.content.isTimeDisplayMode() ) {
					IS_EventDispatcher.addListener("loadComplete",targetWidget.id,function() {
						targetWidget.elm_widgetBox.className = "widgetBox";
						targetWidget.headerContent.applyAllIconStyle();
					},null,true );
					
					targetWidget.loadContents();
				}
			}else{
				addWidgetFunc( IS_Portal.currentTabId,widgetGhost );
			}
			
			function addWidgetFunc( tabId,target ) {
				widget = IS_WidgetsContainer.addWidget( tabId, widgetConf , false, function(w){
						target.parentNode.replaceChild( w.elm_widget,target );
					}, (subWidgetConf)? [subWidgetConf] : null);//TODO: The way of passing sub widget.
				
				//Send to Server
				IS_Widget.setWidgetLocationCommand(widget); //Add SiblingId
				
				var menuId;
				if(/MultiRssReader/.test(menuItem.type)){
					var subWidgets = IS_Portal.getSubWidgetList(widget.id);
					for (var i=0; i < subWidgets.length; i++){
						var feedWidget = subWidgets[i];
						if(feedWidget)
							IS_Portal.widgetDropped( feedWidget );
					}
				}else{
					IS_Portal.widgetDropped( widget );
				}
			}
		}
		menuopt.onHover = function(element, dropElement, dragMode, point) {
			var widgetGhost = IS_Draggable.ghost;
			if(widgetGhost.menuItem && /MultiRssReader/.test(widgetGhost.menuItem.type)){
				var parentItem = widgetGhost.menuItem.parent;
				var p_id = IS_Portal.currentTabId+"_p_" + parentItem.id;
				var divParent = $(p_id);
				if( divParent ) return;
			}
			
			var x = point[0] - element.boxLeftDiff;
			var y = point[1] - element.boxTopDiff;

			var min = 10000000;
			var nearGhost = null;
			var scrollOffset = IS_Portal.tabs[IS_Portal.currentTabId].panel.scrollTop;//IS_Portal.columnsObjs must be in the panel.
			for ( var i=1; i <= IS_Portal.tabs[IS_Portal.currentTabId].numCol; i++ ) {
				var col = IS_Portal.columnsObjs[IS_Portal.currentTabId]["col_dp_" + i];
				for (var j=0; j<col.childNodes.length; j++ ) {
					var div = col.childNodes[j];
					if (div == widgetGhost) {
						continue;
					}
					
					if(dragMode == "menu"){
						var left = findPosX(div);
						var top = findPosY(div) - scrollOffset;
					}else{
						var left = div.posLeft;
						var top = div.posTop - scrollOffset;
					}
					
					var tmp = Math.sqrt(Math.pow(x-left,2)+ Math.pow(y-top,2));
					if (isNaN(tmp)) {
						continue;
					}
					
					if ( tmp < min ) {
						min = tmp;
						nearGhost = div;
						nearGhost.col = col;
					}
					
				}
			}
			if (nearGhost != null && widgetGhost.nextSibling != nearGhost) {
				widgetGhost.style.height = 20;
				widgetGhost.style.display = "block";
				nearGhost.parentNode.insertBefore(widgetGhost,nearGhost);
				widgetGhost.col = nearGhost.col;
			}
		}
		IS_Droppables.add(panelBody, menuopt);
		
		$jq("#select_layout_link").click(function(){
			$jq("#select_layout_modal").dialog({
				modal:true,
				width:600,
				height:500,
				open: function(){
					var dialog = $jq(this);
					if(dialog.data("init")) return;
					$jq("#staticLayouts"+(areaType != 2 ? "AdjustHeight":"")).hide();
					$jq("#select_layout_modal .staticLayout"+(areaType == 2 ? "AdjustHeight":""))
						.mouseover(function(){$jq(this).css("background-color","#9999cc")})
						.mouseout(function(){$jq(this).css("background-color","")})
						.click(function(){
							if(!confirm(ISA_R.alb_destroyOldSettings))
								return;
							jsonRole.layout = setIdentifier($jq(this).html());
							$jq("#staticAreaContainer").html(jsonRole.layout);
							prepareStaticArea();
							
							openerPanel.isUpdated = true;
							ISA_Admin.isUpdated = true;
							
							reloadStaticGadgets();
							adjustStaticWidgetHeight();
							dialog.dialog("close");
						});
					$jq("#select_layout_cancel").click(function(){
						dialog.dialog("close");
					});
					dialog.data("init", true);
				}
			});
		});
		
		$jq("#edit_layout_link").click(function(){
			$jq("#edit_layout_modal").dialog({
				modal:true,
				width:580,
				height:400,
				open:function(){
					$jq("#edit_layout_textarea").val(jsonRole.layout);
					$jq("#edit_layout_ok").click(function(){
						jsonRole.layout = $jq("#edit_layout_textarea").val();
						$jq('#staticAreaContainer').html(layout);
						prepareStaticArea();
						
						openerPanel.isUpdated = true;
						ISA_Admin.isUpdated = true;

						reloadStaticGadgets();
						dialog.dialog("close");
					});
					var dialog = $jq(this);
					if(dialog.data("init")) return;
					$jq("#edit_layout_cancel").click(function(){
						dialog.dialog("close");
					});
					dialog.data("init", true);
				}
			});
		});
		
		//handle areaType
		Event.observe($("areaType"), 'change', function(){
			//change areaType from static and personalized area to only static area.
			if(areaType != this.value){
				if(!confirm("表示エリアを変更するにはリロードする必要があります。よろしいですか？")){
					$jq("#areaType option[value="+ areaType.toString() +"]").attr("selected","selected");
					return;
				}
				
				switch(this.value){
					case "0" :
						jsonRole.disabledDynamicPanel = false;
						jsonRole.adjustToWindowHeight = false;
						break;
					case "1" :
						jsonRole.disabledDynamicPanel = true;
						jsonRole.adjustToWindowHeight = false;
						break;
					case "2" :
						jsonRole.disabledDynamicPanel = true;
						jsonRole.adjustToWindowHeight = true;
						break;
				}
				location.reload();
			}
		});
	};
	
	function setIdentifier(html){
		var datetime = new Date().getTime();
		var idPrefix = "p_" + datetime + "_w_";
		//Give id attribute to HTML
		//TODO Is class="column" suite for Widget?
		var regexp = new RegExp("class=\"static_column\"");
		var newhtml = "";
		var s = html;
		var cnt = 0;
		for(cnt=0;s.match(regexp);cnt++){
			newhtml += RegExp.leftContext;
			newhtml += RegExp.lastMatch;
			newhtml += " id=\"" + idPrefix + cnt + "\"";
			s = RegExp.rightContext;
		}
		newhtml += RegExp.rightContext;
		if(!newhtml) newhtml = html;
		
		return newhtml;
	}
	
	function isTemp(flag){
		return flag == 1;
	}

	IS_Portal.widgetDropped = function( widget ) {
		if( IS_TreeMenu.isMenuItem( widget.id ) )
			IS_EventDispatcher.newEvent( IS_Widget.DROP_WIDGET, IS_TreeMenu.getMenuId( widget.id ) );
		
		saveDynamicPanel();
	}

	IS_WidgetConfiguration = <jsp:include page="/widconf" flush="true" />;
	IS_WidgetIcons = <jsp:include page="/gadgeticon" flush="true" />;

	function displayStaticGadget(widgetOpt){
		var containerId = widgetOpt.id;
		var container = $(containerId);
		if(!container) {
			IS_Portal.widgetLists[IS_Portal.currentTabId][containerId] = false;
			return;
		}
		var realContainer = $("s_"+containerId);
		if(realContainer) {
			container.parentNode.removeChild(container);
			container = realContainer;
		}else{
			container.id = "s_" + containerId;
		}
		var widget = new IS_Widget(false, widgetOpt);
		widget.panelType = "StaticPanel";
		widget.containerId = containerId;
		widget.build();
		container.appendChild(widget.elm_widget);
		
		widget.loadContents();
		IS_Portal.widgetLists[IS_Portal.currentTabId][widget.id] = widget;
	}
	function reloadStaticGadgets(){
		var widgets = IS_Portal.widgetLists[IS_Portal.currentTabId];
		for(var id in widgets){
			var widget = widgets[id];
			if(widget.panelType != "StaticPanel") return;
			displayStaticGadget(widget.widgetConf);
		}
	}
	
	var timeout = false;;
	function saveDynamicPanel(){
		if(timeout)	clearTimeout(timeout);
		timeout = setTimeout(_saveDynamicPanel, 100);
	}
	
	function _saveDynamicPanel(){
		var newDynamicPanel = {};
		
		var numCol = $jq("numberOfColumns").val();
		
		var columns = $jq("#dynamic-portal-widgets" + jsonRole.tabId + " .column");
		$jq.each(columns, function(index, column){
			
			$jq.each($jq(".widget", column), function(index, widgetEl){
				var wid = IS_Portal.getTrueId(widgetEl.id);
				var widget = IS_Portal.getWidget(wid, IS_Portal.currentTabId);
				var menuId = (widget.widgetConf.menuId)? widget.widgetConf.menuId : wid.substring(2);
				
				var menuItem = IS_TreeMenu.types.topmenu.menuItemList[menuId];
				if(!menuItem)
					menuItem = IS_TreeMenu.types.sidemenu.menuItemList[menuId];
				
				var widgetJSON = {
					id : "w_" + menuItem.id,
					column : new String(widget.widgetConf.column),
					type : menuItem.type? menuItem.type : widget.widgetType,
					properties: (menuItem.properties && typeof menuItem.properties == "Object" )?
									menuItem.properties : {}
				};
				
				if(/MultiRssReader/.test( widgetJSON.type )) {
					widgetJSON.id = "p_"+widgetJSON.id.substring(2);
					var subWidgets = IS_Portal.getSubWidgetList(widgetJSON.id);
					
					var childrenList = [];
					for(var i=0;i<subWidgets.length;i++){
						childrenList.push(subWidgets[i].id.substring(2));
					}
					widgetJSON.properties.children = Object.toJSON(childrenList);
					delete widgetJSON.properties.url;
				}
				
				if(menuItem.title){
					widgetJSON.title = menuItem.title;
				}else{
					var _title = ISA_SiteAggregationMenu.widgetConfs[ widgetJSON.type ].title;
					if(_title){
						widgetJSON.title = _title;
					}else{
						widgetJSON.title = widgetJSON.type;
					}
				}
				widgetJSON.href = ("MiniBrowser" == widgetJSON.type) ? 
					widgetJSON.properties.url : (menuItem.href) ? menuItem.href : "";

				delete widgetJSON.properties.title;
				delete widgetJSON.properties.href;
				
				newDynamicPanel[widgetJSON.id] = widgetJSON
			});
		});
		
		jsonRole.dynamicPanel = newDynamicPanel;
		openerPanel.isUpdated = true;
		ISA_Admin.isUpdated = true;
	}

	$jq(function(){
		var id = "<%= request.getParameter("id") %>";
		jsonRole = openerPanel.displayRoleJsons[id];
		init();
	});
	
	function updatePanel(){
		openerPanel.isUpdated = true;
		openerPanel.updatePanel(true);
	}
	Event.observe(window, 'beforeunload', updatePanel);
	
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
