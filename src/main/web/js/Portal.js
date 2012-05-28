/* infoScoop OpenSource
 * Copyright (C) 2010 Beacon IT Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 */

//Default value settings of private property
var useTab = is_getPropertyBoolean(useTab, true);

//Default value settings of public property
var commandQueueWait = is_getPropertyInt(commandQueueWait, 30);
var logCommandQueueWait = is_getPropertyInt(logCommandQueueWait, 3600);
var freshDays = is_getPropertyInt(freshDays, 1);
var refreshInterval = is_getPropertyInt(refreshInterval, 10);
var messagePriority = is_getPropertyInt(messagePriority, 1);
var portalStartDelayTime = is_getPropertyInt(portalStartDelayTime, 0);
var ajaxRequestTimeout = is_getPropertyInt(ajaxRequestTimeout, 15000);
var ajaxRequestRetryCount = is_getPropertyInt(ajaxRequestRetryCount,2);
var displayInlineHost = is_toUserPrefArray(is_getPropertyString(displayInlineHost, ""));
var accessLogEntry = is_getPropertyBoolean(accessLogEntry, false);

var sideMenuTabs = is_toUserPrefArray(is_getPropertyString(sideMenuTabs, "sidemenu|addContent|mySiteMap"));
var defaultTheme = is_getPropertyString(defaultTheme, false);

var hostPrefix = (isTabView)? findHostURL(false).replace(/\/tab.*/, "") : findHostURL(false);
var proxyServerURL = hostPrefix + "/proxy";

var searchEngineURL = searchEngineURL ? is_getProxyUrl( searchEngineURL, "NoOperation") : hostPrefix+"/schsrv";

IS_Customization = false;
IS_WidgetConfiguration = [];
IS_Portal.logoffDateTime = -1;
IS_Portal.fontSize = "";
IS_Portal.msgLastViewTime = -1;

IS_Portal.freshDays = freshDays;
//IS_Portal.sidePanel = new Object();
IS_Portal.buildVersion = "";
IS_Portal.lastSaveFailed = false;

IS_Portal.autoRefCountList = [];
IS_Request.CommandQueue = new IS_Request.Queue("/comsrv", commandQueueWait, !is_userId);
IS_Request.LogCommandQueue = new IS_Request.Queue("/logsrv", logCommandQueueWait, false, true);
IS_Portal.imageController = {};//For image thumbnail. Refer to RssItemRender.js
IS_Portal.iframeToolBarIconsTable;
var IS_User = new Object();

IS_Portal.defaultFontSize = "100%";

IS_Portal.start = function() {
	var self = this;

	if(defaultTheme){
		try{
			IS_Portal.theme.defaultTheme = eval( '(' + defaultTheme + ')' );
		}catch(e){
			msg.error('The defaultTheme property is invalid, please contact to administrator:' + e);
		}
	}
	IS_Portal.theme.setTheme(IS_Portal.theme.currentTheme);
	
	IS_Portal.startIndicator('portal-maincontents-table');
	
	var fontSize = getActiveStyle( document.body, "font-size");
	IS_Portal.defaultFontSize = (fontSize.charAt(fontSize.length-1)=="%" ? fontSize : Math.round(parseInt(fontSize)/16*100) + "%" );
	IS_Portal.setFontSize(null, true);
	
	var opt = {
	  method: 'get' ,
	  asynchronous:false,
	  onSuccess: function(response){
		  eval(response.responseText);
	  },
	  onFailure: function(t) {
		  alert('Retrieving customization info failed. ' + t.status + ' -- ' + t.statusText);
	  },
	  onException: function(t) {
		  alert('Retrieving customization info failed. ' + t);
	  }
	};	
	AjaxRequest.invoke(hostPrefix +  "/customization", opt);
	
	document.title = IS_Customization.title;

	var header = document.getElementById("portal-header");
	header.innerHTML = IS_Customization.header;
	if(IS_Customization.header.length == 0)
		header.style.display = "none";
	
	IS_Portal.SearchEngines.init();

	var command = document.getElementById("portal-command");
	command.innerHTML = IS_Customization.commandbar;
	IS_Portal.buildLogo();
	IS_Portal.buildFontSelectDiv();
	IS_Portal.buildGlobalSettingModal();
	IS_Portal.Trash.initialize();
	IS_Portal.buildAdminLink();
	IS_Portal.buildCredentialList();
	IS_Portal.buildLogout();
	
	IS_Portal.buildTabs();
	new IS_WidgetsContainer();
	new IS_SiteAggregationMenu();
	IS_Portal.sidePanel = new IS_SidePanel();
	IS_Portal.refresh = new IS_AutoReload();
	
	if(fixedPortalHeader) {
		Event.observe(window, 'resize', IS_Portal.adjustPanelHeight, false);
		IS_EventDispatcher.addListener("adjustedMessageBar","",IS_Portal.adjustPanelHeight);
		IS_EventDispatcher.addListener("adjustedMessageBar","",IS_Portal.adjustIframeHeight);
		IS_EventDispatcher.addListener("changeTab","",IS_Portal.adjustPanelHeight);
	}
	Event.observe(window, 'resize', IS_Portal.adjustSiteMenuHeight, false);
	Event.observe(window, 'resize', IS_Portal.adjustIframeHeight, false);
	Event.observe(window, 'resize', IS_Portal.adjustGadgetHeight , false);
	Event.observe(window, 'resize', IS_Portal.adjustStaticWidgetHeight, false);
	IS_EventDispatcher.addListener("adjustedMessageBar","",IS_Portal.adjustStaticWidgetHeight);

	var messageBarDiv = $('message-bar-controles');
	var messageMoreBtn = document.createElement('input');
	messageMoreBtn.id = 'message-list-more-btn';
	messageMoreBtn.type='button';
	messageMoreBtn.style.display = 'none';
	messageMoreBtn.value = IS_R.lb_messageMore;
	messageBarDiv.appendChild(messageMoreBtn);
	Event.observe(messageMoreBtn, 'click', IS_Portal.moreMsgBar, false);
	var messageBarCloseBtn = document.createElement('input');
	messageBarCloseBtn.type='button';
	messageBarCloseBtn.value = IS_R.lb_messageClose;
	messageBarDiv.appendChild(messageBarCloseBtn);
	Event.observe(messageBarCloseBtn, 'click', IS_Portal.closeMsgBar, false);

	IS_Portal.behindIframe.init();
	
	Event.observe($("ifrm"), "load", IS_Portal.iFrameOnLoad, false);
		
	Event.observe( document.body, 'mousedown', IS_Widget.RssReader.RssItemRender.checkHideRssDesc, false );
	
	var panelBody = document.body;
	
	if(fixedPortalHeader) {
		Element.addClassName(panelBody, "fixedPortalHeader");
	}
	
	IS_Portal.droppableOption = {};
	
	//widget to panel
	var widopt = {};
//	widopt.accept = ["widget", "subWidget"];
	widopt.accept = function(element, widgetType, classNames){
		return (classNames.detect( 
          function(v) { return ["widget", "subWidget"].include(v) } ) &&
 			(widgetType != "mapWidget") );
	};
	widopt.onHover = IS_DroppableOptions.onHover;
	widopt.onDrop = function(element, lastActiveElement, widget, event) {
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
	};
	IS_Droppables.add(panelBody, widopt);
	
	//menuItem to panel
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
			/* Recreate config everytime becasue menu can be changed */
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
			
			/*
			var dummyWidget = {};
			dummyWidget.widgetConf = widgetConf;
			
			widget = targetWidget.content.addRssReader(dummyWidget, siblingId);
			*/
//			var divWidgetDummy = element.dummy;
//			element = divWidgetDummy.parentNode.replaceChild(element, divWidgetDummy);
//			element.style.top = "0px";
//			element.style.width = "auto";
			
			/*
			//Send to Server
			IS_Widget.setWidgetLocationCommand(widget);
			*/
			
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
			
			//Send to Server
			//IS_Widget.addWidgetCommand(widget);
			//IS_Widget.setWidgetLocationCommand(widget);
			
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
//			var divWidgetDummy = element.dummy;
//			element = divWidgetDummy.parentNode.replaceChild(element, divWidgetDummy);
//			element.style.top = "0px";
//			element.style.width = "auto";
			
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
			widgetGhost.style.display = "block";
			nearGhost.parentNode.insertBefore(widgetGhost,nearGhost);
			widgetGhost.col = nearGhost.col;
		}

	}
	
	IS_Droppables.add(panelBody, menuopt);
	
	// multidrop to panel
	var multimenuopt = {};
	multimenuopt.accept = function(element, widgetType, classNames){
		return (classNames.detect( 
          function(v) { return ["menuGroup", "multiDropHandle"].include(v) } ) &&
 			(widgetType != "mapWidget") );
	};
	multimenuopt.onHover = IS_DroppableOptions.onHover;
	
	IS_Portal.droppableOption.onMultiMenuDrop = function(element, lastActiveElement, menuItem, event, originFunc, modalOption){
		if(!IS_Portal.canAddWidget()) return;
		var confs = IS_SiteAggregationMenu.createMultiDropConf.call(self, element, lastActiveElement, menuItem, event, IS_Portal.droppableOption.onMultiMenuDrop, modalOption);
		
		var widgetGhost = IS_Draggable.ghost;
		element.style.display = "none";
//		var divWidgetDummy = element.dummy;
//		element.dummy = false;
//		if(divWidgetDummy && divWidgetDummy.parentNode){
//			element = divWidgetDummy.parentNode.replaceChild(element, divWidgetDummy);
//			element.style.top = "0px";
//			element.style.width = "auto";
//		}
		
		if( !isUndefined("siteAggregationMenuURL")&& menuItem.owner == IS_TreeMenu.types.topmenu ) {
			IS_SiteAggregationMenu.closeMenu();
			IS_SiteAggregationMenu.resetMenu();
		}
		
		if(confs){
			var widgetConf = confs[0];
			var subWidgetConfList = confs[1];
			var otherWidgets = confs[2];
			var sibling = widgetGhost;
			
			if(widgetConf && subWidgetConfList.length > 0) {
				var widget = IS_WidgetsContainer.addWidget( IS_Portal.currentTabId, widgetConf , false, function(w){
						widgetGhost.col.replaceChild(w.elm_widget, widgetGhost);
					},subWidgetConfList);
				
	//			var hasCurrentTab = IS_Portal.widgetLists[IS_Portal.currentTabId][IS_Portal.currentTabId + "_p_" + menuItem.id];
				var hasCurrentTab = IS_Portal.getWidget(IS_Portal.currentTabId + "_p_" + menuItem.id);
				if(hasCurrentTab && modalOption == IS_SiteAggregationMenu.MergeMode.remain && widget.headerContent){
	//				widget.headerContent.showTitleEditorForm();
	//TODO !! No editor
				}
				
				//Send to Server
				IS_Widget.setWidgetLocationCommand(widget);
				//var menuId;
				if(widget.content && widget.content.getRssReaders){
					var rssReaders = widget.content.getRssReaders();
					for(var i = 0; i < rssReaders.length; i++){
						//if(widget.content.isDisplay(rssReaders[i])){
							menuId = IS_Portal.getTrueId(rssReaders[i].id, rssReaders[i].widgetType).substring(2);
							if(!IS_Portal.isChecked(menuId))
								IS_Portal.widgetDropped( rssReaders[i] );
						//}
					}
				}else{
					//menuId = IS_Portal.getTrueId(widget.id, widget.widgetType).substring(2);
					IS_Portal.widgetDropped( widget );
				}
				sibling = widget.elm_widget;
			}
			otherWidgets.each(function(otherMenuItem){
				var ghostColumnNum = (widgetGhost.col)? widgetGhost.col.getAttribute("colNum"):1;
				var otherWidgetConf = IS_SiteAggregationMenu.getConfigurationFromMenuItem(otherMenuItem, ghostColumnNum);
				var target = widgetGhost;
				if(sibling != widgetGhost) {
					target = document.createElement("div");
					sibling.parentNode.insertBefore(target, sibling.nextSibling ? sibling.nextSibling : sibling);
				}
				addWidgetFunc( IS_Portal.currentTabId, target );
				function addWidgetFunc( tabId,target ) {
					var otherWidget = IS_WidgetsContainer.addWidget( tabId, otherWidgetConf , false, function(w){
							target.parentNode.replaceChild( w.elm_widget,target );
						}, null);
					IS_Widget.setWidgetLocationCommand(otherWidget); //Add SiblingId
					IS_Portal.widgetDropped( otherWidget );
					sibling = otherWidget.elm_widget;
				}
			});
		}
	}
	multimenuopt.onDrop = IS_Portal.droppableOption.onMultiMenuDrop;
	
	IS_Droppables.add(panelBody, multimenuopt);
	
	IS_Draggables.keyEvent.keyPressed = false;
	IS_Draggables.keyEvent.addKeyDownEvent(
		function(e){
			if(IS_Draggables.keyEvent.isPressing.ctrl){
				var widgetGhost = IS_Draggable.ghost;
				
//				if (IS_Draggables.activeDraggable && !IS_Draggables.keyEvent.keyPressed) {
//					IS_Droppables.show(IS_Draggables._lastPointer, IS_Draggables.activeDraggable.element);
//					IS_Draggables.keyEvent.keyPressed = true;
//				}
				
				if (IS_Draggables.activeDraggable && IS_Draggables.activeDraggable.element){
					var element = IS_Draggables.activeDraggable.element;
					IS_Droppables.findDroppablesPos(element);
					IS_Droppables.show(IS_Draggables._lastPointer, element);
				}

				if (widgetGhost) {
					//widgetGhost.oldBorderStyle = getStyleValue("#widgetGhost", "border");
					Element.addClassName( widgetGhost,"noMergeMode");
				}
			}
		}
	);
	
	IS_Draggables.keyEvent.addKeyUpEvent(
		function(e){
			if(!IS_Draggables.keyEvent.isPressing.ctrl){
				if (IS_Draggables.activeDraggable && IS_Draggables.activeDraggable.element)
					IS_Droppables.findDroppablesPos(IS_Draggables.activeDraggable.element);
				var widgetGhost = IS_Draggable.ghost;
				if(widgetGhost) {
				//	widgetGhost.style.border = "2px dashed #F00";
					Element.removeClassName( widgetGhost,"noMergeMode");
				}
// 				IS_Draggables.keyEvent.keyPressed = false;
			}
		}
	);
	//IS_Portal.startDetectFontResized();
	
	if(is_userId)
		IS_Portal.checkSystemMsg();
}

IS_Portal.getFreshDays = function(_freshDays){
	// Find out business day
	var cal = new Date();
	for(var i=0;i<_freshDays;i++){
		cal.setDate(cal.getDate() - 1);
		if(isHoliday(cal) || cal.getDay() == 0	|| cal.getDay() == 6){
			_freshDays++;
		}
		
		if(_freshDays > 20) break;
	}
	
	function isHoliday(cal) {
		year = cal.getFullYear();
		month = cal.getMonth() + 1;
		IS_Holiday.computeEvents(year, month);
		var ev = IS_Holiday.getEvent(cal);
		
		return ev.length > 0;
	}

	return _freshDays;
}

IS_Portal.closeIFrame = function () {
	if(!(Element.visible('portal-iframe') || Element.visible('search-iframe')))return;
	
	var iframeTag = $("ifrm");
	if(iframeTag) iframeTag.src = "";
	var divIFrame = $("portal-iframe");
	if ( divIFrame ) {
		divIFrame.style.display = "none";
	}
	
	var divIFrame = $("search-iframe");
	if ( divIFrame ) {
		divIFrame.style.display = "none";
	}
	
	var ifrmURL = $("portal-iframe-url");
	ifrmURL.style.display = "none";
	
	var divIS_PortalWidgets = document.getElementById("panels");
	if ( divIS_PortalWidgets) {
		divIS_PortalWidgets.style.display="";
	}
	var iframeToolBar = document.getElementById("iframe-tool-bar");
	iframeToolBar.style.display = "none";
	
	IS_Event.unloadCache("_search");
	
	//Clear iframe in IS_Portal.searchEngines
	IS_Portal.SearchEngines.clearIFrames();
	
//	IS_WidgetsContainer.adjustColumnWidth();
	IS_Widget.adjustDescWidth();
	IS_Widget.Information2.adjustDescWidth();
	//Display ifame at link icon in only IE and layout is broke up if go back.
	//TODO Should be removed
	IS_Widget.Maximize.adjustMaximizeWidth();
	IS_Widget.WidgetHeader.adjustHeaderWidth();
	IS_Portal.adjustPanelHeight(null);
	
	IS_Portal.SearchEngines.clearTemp();
	
	//Refresh immidiately if auto refresh is worked while iframe is displayed
	IS_Portal.refresh.resume();
	
//	IS_Portal.setFontSize();
	IS_Portal.adjustIS_PortalStyle();
};

if( Browser.isSafari1 ) {
	IS_Portal.closeIFrame = ( function() {
		var closeIFrame = IS_Portal.closeIFrame;
		
		return function() {
			if( IS_Portal.currentTabId.indexOf("_") != 0 || IS_Widget.MaximizeWidget )
				return;
			
			IS_Portal.currentTabId = IS_Portal.currentTabId.substring(1);
			closeIFrame.apply( this,$A( arguments ));
			
			IS_Portal.enableCommandBar();
			IS_Portal.adjustCurrentTabSize();
		}
	})();
}

IS_Portal.goHome = function(){
	IS_Portal.closeIFrame();
	IS_Portal.CommandBar.changeDefaultView();
}

//TODO:This code depend to MultiRssReader.
IS_Portal.isChecked = function(menuItem){
	isChecked = false;
	
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
}

/**
 * Search and return Widget existing in portal by MenuID(no w_)
 * Return null if not existing.
 * 
 * @param {Object} widgetId
 */
IS_Portal.searchWidgetAndFeedNode = function(menuId){
	var widget = null;
	
	tabLoop:
	for(var tabId in IS_Portal.widgetLists){
		var widgetList = IS_Portal.widgetLists[tabId];
		for(var i in widgetList){
			if(!widgetList[i] || !widgetList[i].id) continue;
			
			if(widgetList[i].id.substring(2) == menuId){
				widget = widgetList[i];
				break tabLoop;
			}
		}
	}
	return widget;
}

IS_Portal.getProperty = function(properties, attrName) {
	var value;
	var prop = getChildrenByTagName(properties, "property");
	for ( i=0; i<prop.length; i++ ) {
		var property = prop[i];
		var name = property.getAttribute("name");
		var value = (property.firstChild) ? property.firstChild.nodeValue : "";
		if (name == attrName) {
			break;
		}
	}
	return value;
}

IS_Portal.getPropertys = function(properties, feed) {
	var prop = getChildrenByTagName(properties, "property");
	for ( i=0; i<prop.length; i++ ) {
		var property = prop[i];
		var name = property.getAttribute("name");
		feed[name] = (property.firstChild) ? property.firstChild.nodeValue : "";
	}
}

IS_Portal.adjustPanelHeight = function(e){
	if(!fixedPortalHeader) return;
	if(IS_Widget.MaximizeWidget) return;//Fixed Issue 149: Fragment Minibrowser shows a little off from the position whrere it should be when it maximized.
	var panels = $("panels");
	if(!panels.visible) return;
	var adjustHeight = getWindowSize(false) - findPosY($("panels")) - $("tab-container").getHeight() - 5;
	if(Browser.isIE) adjustHeight -= 3;
	if(IS_Portal.tabs[IS_Portal.currentTabId])
		IS_Portal.tabs[IS_Portal.currentTabId].panel.style.height = adjustHeight + "px";
}

IS_Portal.adjustSiteMenuHeight = function(e, siteManuObj) {
	var siteManuObj = document.getElementById("portal-maincontents-table");
	if(siteManuObj) {
		var adjustHeight = getWindowSize(false) - findPosY(siteManuObj) - 5;
		if (Browser.isIE) adjustHeight -= 15;
		if (adjustHeight>=0) siteManuObj.style.height = adjustHeight + "px";
		return;
	}
}

IS_Portal.adjustIframeHeight = function(e, iframeObj) {
	var iframe = iframeObj;
	if(!iframe){
		var searchResult = document.getElementById("search-result");
		if(searchResult){
			var searchIframes = searchResult.getElementsByTagName("iframe");
			for ( i = 0; i < searchIframes.length; i++) {
				var disp = getDisplay(searchIframes[i]);
				if(disp) {
					iframe = searchIframes[i];
					break;
				}
			}
		}
		
		if(!iframe){
			var contentIframe = document.getElementById("ifrm");
			if(contentIframe && getDisplay(contentIframe)){
				iframe = contentIframe;
			}
		}
	}
	
	if(iframe && iframe.id) {
		try{
//			var adjustHeight = getWindowSize(false) - findPosY(iframe) - 10;
			var iframeToolBar = document.getElementById("iframe-tool-bar");
			var offset = ( iframeToolBar && iframeToolBar.style.display != "none") ? iframeToolBar.offsetHeight : 0;
			var adjustHeight = getWindowSize(false) - findPosY(iframe) - offset - 10 -(Browser.isFirefox ? 10:0);

			iframe.style.height = adjustHeight + "px";
		}catch(e){
			msg.warn(IS_R.getResource(IS_R.ms_errorOnWindowResize,[e]));
		}
		return;
	}
	
	//TODO:Function name is not obvious
	function getDisplay(obj) {
		if(obj.style && obj.style.display == 'none')
			return false;
		else if(obj.parentNode)
			return getDisplay(obj.parentNode);
		return true;
	}
}

IS_Portal.deleteCache = function() {
	var opt = {
		method: 'get' ,
		asynchronous:false,
		onSuccess: function(req){},
		on1223: function(req){},
		onFailure: function(t) {
			msg.warn(IS_R.getResource(IS_R.ms_cacheDeleteFailure,[t.status,t.statusText]));
		}
	};
	AjaxRequest.invoke(hostPrefix +  "/cacsrv?delete=", opt);
}

IS_Portal.deleteCacheByUrl = function(url) {
	var opt = {
		method: 'post' ,
		postBody: "delete=&url=" + encodeURIComponent(url),
		asynchronous:true,
		onSuccess: function(req){},
		on1223: function(req){},
		onFailure: function(t) {
			msg.warn(IS_R.getResource(IS_R.ms_cacheDeleteFailure,[t.status,t.statusText]));
		}
	};	
	AjaxRequest.invoke(hostPrefix +  "/cacsrv", opt);
}

IS_Portal.processLogoff = function(){
	var cmd = new IS_Commands.ExecLogoffProcessCommand();
	IS_Request.CommandQueue.addCommand(cmd);
}

var widgetGhost = document.createElement("div");
widgetGhost.id = "widgetGhost";

var debugConsol = document.getElementById("debugConsole");

if(!isTabView){
	Event.observe(window, 'load', function() {
		if(portalStartDelayTime > 0){
			$('portal-site-aggregation-menu').innerHTML = IS_R.ms_infosccopInitializing;
			setTimeout(IS_Portal.start, portalStartDelayTime);
		}else{
			IS_Portal.start();
		}
		
		if( Browser.isSafari1 )
		  IS_Portal.deleteCache();//TODO:Should be delted at calling index.jsp
	});
}

Event.observe(window, 'beforeunload',  windowBeforeUnload );

function windowBeforeUnload() {
	IS_Request.asynchronous = false;

	try{
		IS_Portal.processLogoff();
	}catch(e){
		alert(IS_R.getResource(IS_R.ms_logofftimeSavingfailure,[getText(e)]));
	}

	try{
		IS_Request.LogCommandQueue.fireRequest();
	}catch(e){}
	
	try{
		IS_Request.CommandQueue.fireRequest();
	}catch(e){
		alert(IS_R.getResource(IS_R.ms_customizeSavingFailure1,[getText(e)]));
	}
	
}

Event.observe(window, Browser.isIE ? 'beforeunload' : 'unload',  windowUnload );

function windowUnload() {
	IS_Request.asynchronous = false;

	//Send to Server
	/*
	try{
		IS_Portal.processLogoff();
	}catch(e){
		alert(IS_R.getResource(IS_R.ms_logofftimeSavingfailure,[getText(e)]));
	}
	*/
	
	//Event.unloadCache();
	// Cache is deleted on loading
	if( !Browser.isSafari1 )
		IS_Portal.deleteCache();
	
	for ( var id in IS_Portal.widgetLists){
		for ( var i in IS_Portal.widgetLists[id] ) {
//			IS_Portal.widgetLists[id][i] = null;
			IS_Portal.removeWidget(i, id);
		}
	}
	for ( var i in IS_Widget) {
		IS_Widget[i] = null;
	}
	for(var i  in IS_EventDispatcher.eventListenerList){
		IS_EventDispatcher.eventListenerList[i] = null;
	}
}

IS_Portal.currentLink = {};
IS_Portal.iFrameOnLoad = function() {
	var divUrl = document.getElementById("iframeUrl");
	window.scroll(0,0);
	try{
		var url = window.ifrm.location.href;
		if(url == "about:blank") return;
		
		divUrl.value = url;
		if(IS_Portal.currentLink.url) {
			try{
				var nodeName = IS_Portal.currentLink.element.nodeName.toLowerCase();
				if(nodeName == "a")
					IS_Portal.currentLink.element.href = IS_Portal.currentLink.url;
				else if(nodeName == "form")
					IS_Portal.currentLink.element.action = IS_Portal.currentLink.url;
			}catch(e){
			}
		}
		IS_Portal.currentLink.element = null;
		
		if($("iframe-tool-bar").style.display == "none"){
			$("iframe-tool-bar").style.display = "block";
			IS_Portal.adjustIframeHeight();
		}
		
		try{
			if(IS_User.IframeOnload) IS_User.IframeOnload(divUrl.value);
		}catch(e){}
	}catch(e){
//		divUrl.className = "iframeUrlError";
		var ifrmToolBar = document.getElementById("iframe-tool-bar");
		ifrmToolBar.style.display = "none";
		IS_Portal.currentLink = {};
		IS_Portal.adjustIframeHeight();
	}
}


/**
 * iframeToolBar
 */
IS_Portal.buildIframeToolBar = function() {
	var iframeToolBar = document.getElementById("iframe-tool-bar");
	iframeToolBar.style.display = "none";
	
	if( IS_Portal.iframeToolBar )
		return;
	
	iframeToolBar.style.backgroundColor = "#EEEEEE";
	
	IS_Portal.iframeToolBar = new IS_Portal.ContentFooter({
		id: "iframe-tool-bar",
		isDisplay: function() {
			return true; // ?
		},
		getTitle: function() {
			try {
				return window.ifrm.document.title;
			} catch( ex ) {
				msg.warn( ex );
			}
			
			return "";
		},
		getUrl: function() {
			try {
				return document.getElementById("iframeUrl").value;
			} catch( ex ) {
				msg.warn( ex );
			}
			
			return "";
		},icons: [
			{
				html: '<table width="100%"><tr><td><input readOnly="readOnly" id="iframeUrl"></td></tr></table>'
			}
		]
	});
	
	IS_Portal.iframeToolBar.displayContents();
	
	IS_Portal.iframeToolBar.elm_toolBar.style.width = "100%";
	
	iframeToolBar.appendChild( IS_Portal.iframeToolBar.elm_toolBar );
	
	$("iframeUrl").parentNode.parentNode.parentNode.parentNode.parentNode.parentNode.style.width = "100%";
}

IS_Portal.isInlineUrl = function(url){
	if(!url || !displayInlineHost) return false;
	if(!url.match(/\w+:[\/]+([^\/]*)/)) return false;
	var host = RegExp.$1;
	for(var i=0;i<displayInlineHost.length;i++){
		if(displayInlineHost[i] == '*' || displayInlineHost[i] == host){
			return true;
		}
	}
	return false;
}

IS_Portal.showIframe = function(url){
	var iframeToolBar = $("iframe-tool-bar");
	if(iframeToolBar.innerHTML == "")
		IS_Portal.buildIframeToolBar();
	
	var divIFrame = $("search-iframe");
	if ( divIFrame ) {
		divIFrame.style.display = "none";
	}
	
	var divIFrame = $("portal-iframe");

	var divIS_PortalWidgets = document.getElementById("panels");
	divIS_PortalWidgets.style.display="none";
	IS_Portal.widgetDisplayUpdated();

	divIFrame.style.display="";
	
	IS_Portal.CommandBar.changeIframeView();
	
	var iframe = $("ifrm");
	iframe.src = url? url : "";
	setTimeout(IS_Portal.adjustIframeHeight.bind(iframe, null, iframe), 1);

	IS_Portal.refresh.cancel();
}

IS_Portal.openIframe = function(url){
	if(IS_Widget.MiniBrowser.isForbiddenURL(url)){
		window.open("", url);
		return;
	}
	IS_Portal.showIframe(url);
}

IS_Portal.buildIFrame = function (aTag) {
	if(aTag) {
		if(/^notes:\/\//i.test( aTag.href )){
			aTag.target = "_self";
			return;
		}
		
		if(!aTag.target)
			aTag.target = IS_Portal.isInlineUrl(aTag.href) ? "ifrm" : "_blank";
		if(aTag.target != "ifrm"){ 
			return;
		}else if(IS_Widget.MiniBrowser.isForbiddenURL(aTag.href)){
			//It is displayed in new window as aTag.href is limited to display in portal frame by administrator.
			alert(IS_R.getResource(IS_R.ms_iframeForbiddenURL,[aTag.href]));
			aTag.target = "_blank";
			return;
		}
	}
	if(Browser.isSafari1 && IS_Portal.isTabLoading())
		return;
	
	if(aTag && aTag.nodeName) {
		IS_Portal.currentLink = {element:aTag};
		
		var nodeName = aTag.nodeName.toLowerCase();
		if(nodeName == "a") {
			IS_Portal.currentLink.url = aTag.href;
//			aTag.href = proxyServerURL + "URLReplace/" + aTag.href;
		} else if(nodeName == "form"){
			IS_Portal.currentLink.url = aTag.action;
//			aTag.action = proxyServerURL + "URLReplace/" + aTag.action;
		}
	}
			
	IS_Portal.showIframe();
};

if( Browser.isSafari1 ){
	IS_Portal.buildIFrame = ( function() {
		var buildIFrame = IS_Portal.buildIFrame;
		
		return function( aTag ) {
			if( IS_Widget.MaximizeWidget )
				IS_Widget.MaximizeWidget.turnbackMaximize();
			
			buildIFrame.apply( this,[aTag]);
			if( aTag.target != "ifrm")
				return;
			
			IS_Portal.disableCommandBar();
			IS_Portal.currentTabId = "_"+IS_Portal.currentTabId;
		}
	})();
}

//Trash start
//TODO: The tarsh handling Safari should be considered as it is hard to maintain.
IS_Portal.Trash = new function() {
	var self = this;
	this.initialize = function() {
		this.tempWidgets = [];

		var trashIconContainer = $("portal-trash");
		if(!trashIconContainer) return;
		var trashIcon = document.createElement("a");
		this.trashIcon = trashIcon;
		trashIcon.className = "trashIcon portal-user-menu-link";
		trashIcon.href = '#';
		trashIcon.title = IS_R.lb_trashBox;
		trashIcon.appendChild(
			$.DIV({className:'portal-user-menu-item-label', id: 'trash-icon'}
				, IS_R.lb_trashBox
			)
		);
		trashIconContainer.appendChild(trashIcon);
		
		IS_Event.observe(trashIcon, "mouseover", this.addIconEventListener.bind(this), false, "_trashInit");
	}
	this.addIconEventListener = function(){
		IS_Event.unloadCache("_trashInit");
		IS_Event.observe(this.trashIcon, "mousedown", this.displayModal.bind(this), false);
		this.modal = new Window({
			className: "alphacube",
			title: IS_R.lb_trashBox,
			width:600,
			height:350,
			minimizable: false,
			maximizable: false,
			resizable: true,
			showEffect: Element.show,
			hideEffect: Element.hide,
			recenterAuto: false,
			//destroyOnClose: true,
			onClose:function(){
				var trashContext = $("trashContext");
				if(trashContext) trashContext.style.display = "none";
			},
			zIndex: 10000 
		});
	}
	this.add = function(widget){
		var widgetConf = widget.widgetConf;
		if(!widgetConf.deleteDate) {//deleteDate must be set ahead in asynchronous.
			if(widget.oldParent && widget.oldParent.widgetConf.deleteDate) {
				widgetConf.deleteDate = widget.oldParent.widgetConf.deleteDate;
			}else{
				widgetConf.deleteDate = new Date().getTime();
			}
		}
		if(!this.widgets && !this.isLoading)
			this.loadTrashWidgets(true);
		this._add(widget);
		if(this.modal && this.modal.element.style.display != "none")
			this.displayModal();
	}
	this._add = function(widget){
		var widgetConf = widget.widgetConf;
		widgetConf.id = IS_Portal.getTrueId(widget.id);
		if(!widgetConf.tabId)
			widgetConf.tabId = widget.tabId ? widget.tabId : IS_Portal.currentTabId;
		if(widget.oldParent)
			widgetConf.parentId = IS_Portal.getTrueId(widget.oldParent.id);
		if(!widgetConf.title)//Processing for RssReader that is added from LINK tag of HTML.
		  widgetConf.title = widget.title;
		if(!this.widgets && this.isLoading){
			this.tempWidgets.unshift(widgetConf);
		} else {
			this.widgets.unshift(widgetConf);
		}
	}
	this.loadTrashWidgets = function(asynchronous){
		this.isLoading = true;
		AjaxRequest.invoke("trashwidget", {		
			method: 'post' ,
			asynchronous: asynchronous ? true : false,
			onSuccess: function(res){
				var widgets = eval("("+res.responseText+")");
				self.widgets = self.tempWidgets;
				for(var i=0;i<widgets.length;i++){
					self.widgets.push(widgets[i]);
				}
				delete self.tempWidgets;
			},
			onFailure: function(t) {
				msg.error(IS_R.getResource(IS_R.ms_trashLoadonFailure,[t.status,t.statusText]));
			},
			onException: function(r, t){
				msg.error(IS_R.getResource(IS_R.ms_trashonException,[getText(t)]));
			},
			onComplete: function(){
				self.isLoading = false;
			}
		});
	}
	function restoreWidget(widget){
		return function(e){
			Event.stop(e);
			if(existWidget(widget.id, widget.type)){
				alert(IS_R.getResource(IS_R.ms_widgetDuplicateWarn, [widget.id]));
				hideContextMenu();
				return;
			}
			var newSubWidgets = [];
			var widgets = self.widgets;
			for(var i=0;i<widgets.length;i++){
				if(isParent(widgets[i], widget)){
					if(existWidget(widgets[i].id)){
						alert(IS_R.getResource(IS_R.ms_widgetDuplicateWarn, [widgets[i].id]));
						return;
					}
					var tempsubw = Object.extend({}, widgets[i]);
					delete tempsubw.deleteDate;
					newSubWidgets.push(tempsubw);
				}
			}
			newSubWidgets.reverse();
			
			var newwidget = Object.extend({}, widget);
			if( /MultiRssReader/.test( newwidget.type ) && /^p_/.test( newwidget.id ))
				newwidget.id = IS_Portal.currentTabId+"_"+newwidget.id;
			
			if( IS_Widget.MaximizeWidget && IS_Widget.MaximizeWidget.headerContent )
				IS_Widget.MaximizeWidget.headerContent.turnbackMaximize();
			
			delete newwidget.deleteDate;
			var w = IS_WidgetsContainer.addWidget(IS_Portal.currentTabId, newwidget, false, false, newSubWidgets);
			IS_Widget.setWidgetLocationCommand(w);
			emptyWidget(widget)();
			
			IS_Portal.widgetDropped( w );
			
			newSubWidgets.each( function( subWidget ){
				var sw = IS_Portal.getWidget( subWidget.id,IS_Portal.curentTabId );
				IS_Portal.widgetDropped( sw );
			});
			
			if( newwidget.parentId )
				delete newwidget.parentId;
		}
	}
	function existWidget(widgetId, type){
		if(type && /MultiRssReader/.test(type) && /^p_/.test(widgetId)){
			var widget = IS_Portal.getWidget(widgetId, IS_Portal.currentTabId);
			if(widget) return true;
			return false;
		}
		for(var i=0; i < IS_Portal.tabList.length; i++){
			var widget = IS_Portal.getWidget(widgetId, IS_Portal.tabList[i].id);
			if(widget) return true;
		}
		return false;
	}
	function isParent(widget, parent){
		if(widget.parentId
			&& widget.parentId == parent.id
			&& widget.deleteDate == parent.deleteDate)
			return true;
		return false;
	}
	function emptyAllWidget(){
		self.widgets = [];
		
		//FIXME
		if( Browser.isSafari1 ) {
			setTimeout( self.displayModal.bind( self ),100 );
		} else {
			self.displayModal();
		}
		var cmd = new IS_Commands.EmptyAllWidgetCommand();
		IS_Request.CommandQueue.addCommand(cmd);
	}
	function emptyWidget(widget){
		return function(){
			var widgets = self.widgets;
			for(var i=0;i<widgets.length;i++){
				if(widgets[i].id == widget.id && widgets[i].deleteDate == widget.deleteDate){
					widgets.splice(i,1);
					break;
				}
			}
			//Delete sub widget
			var widgets = self.widgets;
			for(var i=0;i<widgets.length;i++){
				if(isParent(widgets[i], widget)){
					widgets.splice(i,1);
					i--;
				}
			}
			hideContextMenu();
			self.displayModal();
			var cmd = new IS_Commands.EmptyWidgetCommand(widget);
			IS_Request.CommandQueue.addCommand(cmd);
		}
	}
	function showContextMenu(widget, titleTd){
		return function(e){
			try {
				Event.stop(e);
			} catch( ex ) {
				//ignore
			}
			
			if(self.selectedWidgetTd)
				self.selectedWidgetTd.className = "";
			var trashContext = $("trashContext");
			var style = null;
			var table = null;
			if(!trashContext) {
				trashContext = document.createElement("div");
				trashContext.id = "trashContext";
				style = trashContext.style;
				style.position = "absolute";
				style.zIndex = "99999";
				table = createElm("table");
				trashContext.appendChild(table);
				document.body.appendChild(trashContext);
			} else {
				style = trashContext.style;
				table = trashContext.getElementsByTagName("table")[0];
			}
			var tbody = document.createElement("tbody");
			var restoreOpt = {
				listeners:[
					{event:"mousedown",listener:restoreWidget(widget)},
					{event:"mouseover",listener:moverContextMenu},
					{event:"mouseout",listener:moutContextMenu}
				]
			};
			tbody.appendChild(createElm("tr",createElm("td",IS_R.lb_turnback,restoreOpt)));
			var emptyOpt = {
				listeners:[
					{event:"mousedown",listener:emptyWidget(widget)},
					{event:"mouseover",listener:moverContextMenu},
					{event:"mouseout",listener:moutContextMenu}
				]
			};
			tbody.appendChild(createElm("tr",createElm("td",IS_R.lb_delete, emptyOpt)));
			var cancelOpt = {
				listeners:[
					{event:"mousedown",listener:hideContextMenu},
					{event:"mouseover",listener:moverContextMenu},
					{event:"mouseout",listener:moutContextMenu}
				]
			};
			tbody.appendChild(createElm("tr",createElm("td",IS_R.lb_cancel, cancelOpt)));
			if(table.firstChild)
				table.replaceChild(tbody, table.firstChild);
			else
				table.appendChild(tbody);
			style.top = Event.pointerY(e);
			style.left = Event.pointerX(e);
			style.display = "block";
			titleTd.className = "trashSelectedWidget";
			self.selectedWidgetTd = titleTd;
		}
	}
	function hideContextMenu(){
		var trashContext = $("trashContext");
		if(trashContext)
			trashContext.style.display = "none";
		if(self.selectedWidgetTd)
			self.selectedWidgetTd.className = "";
	}
	function moverContextMenu(e){
		Event.element(e).className = "trashSelectedMenu";
	}
	function moutContextMenu(e){
		Event.element(e).className = "";
	}
	this.displayModal = function(e){
		IS_Event.unloadCache("_trash");
		var trashTable = createElm("table", false, {className:"trashContainer"});
		var trashBody = createElm("tbody", false, {className:"trashHeader"});
		trashTable.appendChild(trashBody);
		var headerTr = createElm("tr");
		trashBody.appendChild(headerTr);
		headerTr.appendChild(createElm("td",createElm("a", IS_R.lb_bracketTrashEmptyAll, {
			className:"trashHeaderMenu",
			listeners:{event:"mousedown",listener:emptyAllWidget}
		}), {style:{textAlign:"right"}}));
		trashBody.appendChild(createElm("tr",createElm("td", IS_R.ms_trashContextExplanation)));
		var table = document.createElement("table");
		table.className = "trashTable";
		var thead = document.createElement("thead");
		table.appendChild(thead);
		thead.appendChild(createElm("tr", [
			createElm("th",IS_R.lb_title, {style:{width:"40%"}}),
			createElm("th",IS_R.lb_type, {style:{width:"30%"}}),
			createElm("th",IS_R.lb_deleteDate, {style:{width:"30%"}})
		]));
		var tbody = document.createElement("tbody");
		table.appendChild(tbody);
		if(!this.widgets)
			this.loadTrashWidgets();
		
		if( Browser.isSafari1 )
			var iframe = document.createElement("iframe");
		
		var widgets = this.widgets;
		for(var i = 0; i < widgets.length; i++){
			if(existParent(widgets[i])) continue;
			var tr = document.createElement("tr");
			var icon = createIcon(widgets[i].type);
			icon.style.cursor = "auto";
			//IS_Event.observe(icon, "mousedown", restoreWidget(widgets[i]), false, "_trash");
			if( !widgets[i].title ) continue;
			var titleTd = createElm("td",[icon, widgets[i].title]);
			
			var contextmenuHandler = showContextMenu(widgets[i], titleTd);
			if( Browser.isSafari1 ) {
				
				contextmenuHandler = ( function() {
					var handler = contextmenuHandler;
					
					return function(e) {
						IS_Event.stop(e);
						
						var eventObj = Object.extend( Object.extend({},e),{
							pageX: e.pageX +findPosX( iframe ),
							pageY: e.pageY +findPosY( iframe )
						});
						
						handler.apply( this,[eventObj]);
					}
				})();
			}
			IS_Event.observe(titleTd, "contextmenu",contextmenuHandler, false, "_trash");
			
			tr.appendChild(titleTd);
			
			var type = widgets[i].type;
			var typeConf = IS_Widget.getConfiguration( type );
			if(/^g_/.test( type )) {
				typeName = IS_R.lb_gadget;
			} else {
				typeName = typeConf && typeConf.title ? typeConf.title : type;
			}
			tr.appendChild(createElm("td",typeName));
			var deleteDate = new Date(widgets[i].deleteDate);
			tr.appendChild(createElm("td", formatDate(deleteDate, "yyyy/MM/dd HH:mm:ss")));
			tbody.appendChild(tr);
		}
		var listTd = createElm("td", table);
		listTd.colSpan = "3";
		trashBody.appendChild(createElm("tr",listTd));
		IS_Event.observe(trashTable, "mousedown", hideContextMenu, false, "_trash");
		//this.modal.update(trashTable);
		if( Browser.isSafari1 ) {
			iframe.frameborder = 0;
			iframe.style.width = iframe.style.height = "100%";
			
			var content = document.createElement("div");
			content.style.height = "100%";
			content.appendChild( trashTable )
			IS_Event.observe( iframe,"load",function() {
				var doc = iframe.document || iframe.contentWindow.document;
				
				doc.body.innerHTML = "";
				doc.body.appendChild( content );
			} );
			//FIXME blank.html may help...
			iframe.src = "./iframe.jsp"
			
			trashTable = iframe;
		}
		
		this.modal.setContent(trashTable);
		
		if(e) {//Event is passed only if trash icon is clicked.
			this.modal.showCenter();
			if( Position.cumulativeOffset($(this.modal.element)).top<0)
				this.modal.setLocation('0', Position.cumulativeOffset($(this.modal.element)).left);
		} else {
			this.modal.centered = false;
			this.modal.show();
		}
		
//		this.modal.updateWidth();
	}
	function existParent(widget){
		if(!widget.parentId) return false;
		var widgets = self.widgets;
		for(var i = 0; i < widgets.length; i++){
			if(widgets[i].id == widget.parentId && widgets[i].deleteDate == widget.deleteDate){
				return true;
			}
		}
	}
	function createElm(tagName, childNodes, option){
		var element = document.createElement(tagName);
		if(childNodes) {
			if(typeof childNodes == "object" && childNodes.concat){
				for(var i = 0; i < childNodes.length;i++){
					appendChild(element, childNodes[i]);
				}
			} else {
				appendChild(element, childNodes);
			}
		}
		if(!option) return element;
		if(option.className)
			element.className = option.className;
		if(option.listeners){
			var listeners = option.listeners;
			if(typeof listeners == "object" && listeners.concat){
				for(var i = 0; i < listeners.length;i++){
					var listener = listeners[i];
					addListner(element, listener.event, listener.listener);
				}
			} else {
				addListner(element, listeners.event, listeners.listener);
			}
		}
		if(option.style){
			var style = option.style;
			var elmStyle = element.style;
			for(var i in style){
				if(typeof style[i] == "function") continue;
				elmStyle[i] = style[i];
			}
		}
		return element;
	}
	function appendChild(parent, child){
		if(typeof child == "string")
			parent.appendChild(document.createTextNode(child));
		else
			parent.appendChild(child);
	}
	function addListner(element, event, listener){
		IS_Event.observe(element, event, listener, false, "_trash");
	}
	function createIcon(type){
		var icon = document.createElement("div");
		icon.className = "menuItemIcon";
		IS_Widget.setIcon(icon, type);
		return icon;
	}
}
//End of Trash

IS_Portal.buildFontSelectDiv = function(){
	var fontEl = $("portal-change-fontsize");
	if(!fontEl)
		return;

	//font select for user menu (select box ver.)
	if(!fontEl.getAttribute('outside')){
		Element.setStyle(fontEl, {
			width: '135px'
		});
		
		fontEl.appendChild(
			$.DIV({className:'portal-user-menu-item-label', id :'font-change-div'}
			, IS_R.lb_resizeFont
			)
		);
		
		var defaultFontSize = parseInt(IS_Portal.defaultFontSize), fontSize = parseInt(IS_Portal.fontSize);
		var selected = fontSize > defaultFontSize ? "option-large" :  fontSize < defaultFontSize ? "option-small" : "option-normal";
		
		var fontSizeSelect = $.SELECT({id:'font-size-select'});
		for(var i=0;i<3;i++){
			var opt;
			switch (i){
				case 0:
					opt = $.OPTION({id:'option-small'}, IS_R.lb_resizeFontSmaller);
					break;
				case 1:
					opt = $.OPTION({id:'option-normal'}, IS_R.lb_resizeFontNormal);
					break;
				case 2:
					opt = $.OPTION({id:'option-large'}, IS_R.lb_resizeFontLarger);
					break;
			}
			if(opt.id == selected)
				opt.selected = 'true';
			
			fontSizeSelect.appendChild(opt);
		}
		
		fontEl.appendChild(fontSizeSelect);
		fontEl.title = IS_R.lb_resizeFont;
		
		IS_Event.observe(fontSizeSelect, "change", function(){
			var index = fontSizeSelect.selectedIndex;
			var size;		
			switch (index){
				case 0:
					size = parseInt(IS_Portal.defaultFontSize) - 20 + "%";
					break;
				case 1:
					size = parseInt(IS_Portal.defaultFontSize) + "%";
					break;
				case 2:
					size = parseInt(IS_Portal.defaultFontSize) + 20 + "%";
					break;
				default:
					size = parseInt(IS_Portal.defaultFontSize) + "%";
					break;
			}
			IS_Portal.applyFontSize(size);
			$('portal-user-menu-body').hide();
			$('userMenuCloser').hide();
		}, false, "_fontchange");
		
		IS_Event.observe(fontSizeSelect, "click", function(e){
			if(window.event){
				window.event.cancelBubble = true;
			}
			if(e && e.stopPropagation){
				e.stopPropagation();
			}
		});

	//font change for outside (3 icon ver.)
	}else{
		fontEl.className = 'commandbar-item fontChangeDiv';
		
		var fontChangeDivDel = document.createElement("div");
		fontChangeDivDel.id = "fontChange_small";
		fontChangeDivDel.className = 'portal-user-menu-item-label';
		fontChangeDivDel.title = IS_R.lb_resizeFont +' '+ IS_R.lb_resizeFontSmaller;
		
		var fontChangeDivSta = document.createElement("div");
		fontChangeDivSta.id = "fontChange_standard";
		fontChangeDivSta.className = 'portal-user-menu-item-label';
		fontChangeDivSta.title = IS_R.lb_resizeFont +' '+ IS_R.lb_resizeFontNormal;
		
		var fontChangeDivAdd = document.createElement("div");
		fontChangeDivAdd.id = "fontChange_large";
		fontChangeDivAdd.className = 'portal-user-menu-item-label';
		fontChangeDivAdd.title = IS_R.lb_resizeFont +' '+ IS_R.lb_resizeFontLarger;
		
		var fontChangeDivDelA = $.A({className:'portal-user-menu-link fontChange', href:'#'}, fontChangeDivDel);
		var fontChangeDivStaA = $.A({className:'portal-user-menu-link fontChange', href:'#'}, fontChangeDivSta);
		var fontChangeDivAddA = $.A({className:'portal-user-menu-link fontChange', href:'#'}, fontChangeDivAdd);

		fontChangeDivDelA.width = fontChangeDivDelA.offsetWidth;
		
		fontEl.appendChild(fontChangeDivDelA);
		fontEl.appendChild(fontChangeDivStaA);
		fontEl.appendChild(fontChangeDivAddA);
		
		IS_Event.observe(fontChangeDivAdd, "mouseup", function(){
				IS_Portal.applyFontSize((parseInt(IS_Portal.defaultFontSize) + 20) + "%");
			}, false, "_fontchange");
		IS_Event.observe(fontChangeDivSta, "mouseup", function(){
				IS_Portal.applyFontSize((parseInt(IS_Portal.defaultFontSize)) + "%");
			}, false, "_fontchange");
		IS_Event.observe(fontChangeDivDel, "mouseup", function(){
				IS_Portal.applyFontSize((parseInt(IS_Portal.defaultFontSize) - 20) + "%");
			}, false, "_fontchange");
		
		//Setting width of command bar
		if(fontEl.parentNode && fontEl.offsetWidth && !Browser.isSafari){
			Element.setStyle(fontEl, {width: fontEl.offsetWidth * 3});
			Element.setStyle(fontEl.parentNode, {width: fontEl.style.width});
		}else{
			Element.setStyle(fontEl, {width: fontEl.offsetWidth +1});
			Element.setStyle(fontEl.parentNode, {width: fontEl.style.width});
		}
	}
};

IS_Portal.fontChangeFlg = false;
IS_Portal.applyFontSize = function(fontSize) {
	IS_Portal.fontChangeFlg = true;
	if(IS_Portal.fontSize == fontSize)
		return;
//	if(fontSize == "normal")
//		IS_Portal.fontSize = IS_Portal.defaultFontSize;
//	else
		IS_Portal.fontSize = fontSize;
	
	IS_Portal.setFontSize();
}

/**
 * Fix style broken by chaning font size.
 * 
 */
IS_Portal.adjustIS_PortalStyle = function(){
	if(IS_Portal.fontChangeFlg){
		IS_Portal.setFontSize();
		IS_Portal.fontChangeFlg = false;
	}
}

IS_Portal.setFontSize = function(e, isInitialize) {
	is_addCssRule("body", "font-size:" + IS_Portal.fontSize);
	is_addCssRule("th", "font-size:" + IS_Portal.fontSize);
	is_addCssRule("td", "font-size:" + IS_Portal.fontSize);
	
//	is_addCssRule("table", "font-size:" + IS_Portal.fontSize);
	IS_Portal.widgetDisplayUpdated();
	
	if(!isInitialize){
		IS_Widget.Maximize.adjustMaximizeWidth();
		IS_Widget.Information2.adjustDescWidth();
		IS_Portal.adjustIframeHeight();
		IS_Portal.adjustSiteMenuHeight();
		IS_Widget.Ticker.adjustTickerWidth();
		IS_Widget.WidgetHeader.adjustHeaderWidth();
//		IS_WidgetsContainer.adjustColumnWidth();

		//Send to Server
		IS_Widget.setPreferenceCommand("fontSize", IS_Portal.fontSize);
		
		IS_EventDispatcher.newEvent('fontSizeChanged');
	}
}

/**
 * Start detecting change of fant size.
 * Detect the size change of both browser and portal.
 */
IS_Portal.startDetectFontResized = function(){
	var portalBody = $('portal-body');
	var currentSize;
	if(portalBody){
		var detectNode = document.createElement("span");
		detectNode.id = 'fontSizeChangeDetector';
		detectNode.innerHTML='&nbsp;';
		detectNode.style.zIndex = '-100';
		detectNode.style.vizibility = 'hidden';
		portalBody.appendChild( detectNode );
		
		currentSize = detectNode.offsetHeight;
	}
	
	function checkFontSize(){
		var fontSize = detectNode.offsetHeight;
		if( currentSize != fontSize ){
			IS_Portal.onFontResized();
			currentSize = fontSize;
		}
	}
	
	var id = setInterval( checkFontSize, 1000);
}

/**
 * Processing at changing font size.
 */
IS_Portal.onFontResized = function(){
	IS_Portal.widgetDisplayUpdated();
}

IS_Portal.rssSearchBoxList = new Object();
/**
 * Hiding all of search boxes.
 */
/*
IS_Portal.hideRssSearchBox = function(){
	for(var id in IS_Portal.rssSearchBoxList){
		if(!(IS_Portal.rssSearchBoxList[id] instanceof Function)){
			IS_Portal.rssSearchBoxList[id].style.display = "none";
		}
	}
}
*/

/**
 * Processing at changing display position or size of widget
 */
IS_Portal.widgetDisplayUpdated = function(){
//	IS_Widget.adjustDescWidth();
	IS_Widget.processAdjustRssDesc();
	IS_Widget.adjustEditPanelsTextWidth();
//	IS_Portal.hideRssSearchBox();
}

if( Browser.isSafari1 ) {
	IS_Portal.adjustCurrentTabSize =  function() {
		IS_Widget.processAdjustRssDesc();
		IS_Widget.RssReader.RssItemRender.adjustRssDesc();
		IS_Widget.Information2.adjustDescWidth();
		
		IS_Portal.adjustSiteMenuHeight();
		IS_Portal.adjustIframeHeight();
		IS_Portal.adjustGadgetHeight();
	}
}

IS_Portal.windowOverlay = function(id, tag){
	var overlay = document.createElement(tag);
	overlay.className = "windowOverlay";
	overlay.id = id;
	if(tag == 'iframe')overlay.src = './blank.html';
	document.body.appendChild(overlay);
	
	this.show = function(cursorType){
		overlay.style.width = Math.max(document.body.scrollWidth, document.body.clientWidth);
		overlay.style.height = Math.max(document.body.scrollHeight, document.body.clientHeight);
		
		if(cursorType)
			overlay.style.cursor = cursorType;
		else
			overlay.style.cursor = "move";
			
		overlay.style.display = "";
	};
	
	this.hide = function(){
		overlay.style.display = "none";
	};
}
/**
 * Overlay genarated at dragging.
 * As mousemove event is not occued on Iframe, this is used for snvoiding.
 */
IS_Portal.getDragOverlay = function() {
	if(!IS_Portal.dragOverlay)
		IS_Portal.dragOverlay = new IS_Portal.windowOverlay('dragOverlay', 'div');
	return IS_Portal.dragOverlay;
}
IS_Portal.showDragOverlay = function(cursorType) {
	IS_Portal.getDragOverlay().show(cursorType);
}
IS_Portal.hideDragOverlay = function() {
	IS_Portal.getDragOverlay().hide();
}
//#2713 Some frash can not diplayed properly if the winodw is scrolled.
IS_Portal.getIfrmOverlay = function() {
	if(!IS_Portal.ifrmOverlay)
		IS_Portal.ifrmOverlay = new IS_Portal.windowOverlay('ifrmOverlay', 'iframe');
	return IS_Portal.ifrmOverlay;
}

//Adjusting heigght of Gadget.
IS_Portal.adjustGadgetHeight = function( gadget,swap ){
	setTimeout( function() {
		IS_Portal._adjustGadgetHeight( gadget,swap );
	},100 );
}
IS_Portal._adjustGadgetHeight = function( gadget,swap ){
	var widgets;
	if( gadget ) {
		widgets = {};
		widgets[gadget.id] = gadget;
	}
	
	widgets = widgets || IS_Portal.widgetLists[IS_Portal.currentTabId]
	if( !widgets )
		return;
	
	$H( widgets ).values().findAll( function( widget ) {
		return widget.isGadget && widget.isGadget();
	}).each( function( gadget ) {
		try {
			if( swap ) gadgets.rpc.call( gadget.iframe.name,"ieSwapIFrame");
			
			var module = IS_Widget.getConfiguration( gadget.widgetType );
			if( module && module.ModulePrefs && module.ModulePrefs.Require &&
				module.ModulePrefs.Require["dynamic-height"] &&
				gadget.adjustHeightAuto ) {
				return gadgets.rpc.call( gadget.iframe.name,"adjustHeight");
			} else {
				return;
			}
		} catch( ex ) { }
		
		try {
			return gadget.loadContents();
		} catch( ex ) { console.log( ex )}
	});
}

IS_Portal.moreMsgBar = function() {
	$('message-list-more').show();
	$('message-list-more-btn').hide();
	if(IS_SidePanel.adjustPosition) IS_SidePanel.adjustPosition();
	
	IS_EventDispatcher.newEvent("adjustedMessageBar");
}
IS_Portal.closeMsgBar = function(){
	$('message-bar').hide();
	$('message-list-more').hide();
	$('message-list-more').innerHTML = '';
	$('message-list').innerHTML = '';
	IS_Event.unloadCache('msgBar');
	if(IS_SidePanel.adjustPosition) IS_SidePanel.adjustPosition();
	
	IS_EventDispatcher.newEvent("adjustedMessageBar");
}
/*
IS_Portal.logout = function(){
	IS_Request.asynchronous = false;
	window.onunload();
	window.onunload = null;
	location.href = "authsrv/logout";
}
*/
IS_Portal.setMouseMoveTimer;
IS_Portal.setMouseMoveEvent = function(){
	if(IS_Portal.setMouseMoveTimer) clearTimeout(IS_Portal.setMouseMoveTimer);
	var execFunc = function(){
		IS_Portal.unsetMouseMoveEvent();
		
		var portalTable = $("portal-maincontents-table");
		IS_Event.observe(portalTable, 'mousemove', function(){
			IS_Portal.closeIS_PortalObjects();
			IS_Portal.unsetMouseMoveEvent();
		}, false, "_portalclose");
	}
	IS_Portal.setMouseMoveTimer = setTimeout(execFunc, 100);
}

IS_Portal.unsetMouseMoveEvent = function(){
	IS_Event.unloadCache("_portalclose");
}

IS_Portal.closeIS_PortalObjects = function(){
	if(Browser.isIE) IS_SiteAggregationMenu.closeMenu();
}

IS_Portal.buildCredentialList = function(){
	var portalCredentialListDiv = $("portal-credential-list");
	if(!portalCredentialListDiv) return;

	var credentialListIcon = $.A({
		id: 'authCredentialListIcon'
		, className: 'portal-user-menu-link'
		, href: '#'
		, title: IS_R.lb_credentialList
	});
	portalCredentialListDiv.appendChild(credentialListIcon);
	
	credentialListIcon.appendChild(
		$.DIV({className:'portal-user-menu-item-label', id:'authCredentialListDiv'}
			, IS_R.lb_credentialList
		)
	);
	
	IS_Event.observe(credentialListIcon, 'mouseover', function(){
		IS_Event.unloadCache('_portalCredentialListInit');
		IS_Request.showCredentialList();
	}, false, '_portalCredentialListInit');

}

// Commit change
IS_Portal.applyPreference = function(tabId, isReRender, isAllRefresh){
	
	var _applyPreference = function(){
		var widgetList = IS_Portal.widgetLists[tabId];
		var modifiedWidgetList;
		
		for(var widgetId in widgetList){
			modifiedWidgetList = [];
			
			if(typeof widgetList[widgetId] == "function") continue;
			var isModified = false;
			var widget = widgetList[widgetId];
			if(!widget) continue;
			
			var added = false;
			for(var name in IS_Portal.prefsObj){
				if(typeof IS_Portal.prefsObj[name] == "function") continue;
				
				if(checkValue(widget, IS_Portal.prefsObj, name)){
					widget.setUserPref(name, IS_Portal.prefsObj[name]);
					modifiedWidgetList[widget.id] = widget;
				}
				else if((typeof widget.getUserPref(name) != "undefined") && isAllRefresh && !added){
					modifiedWidgetList[widget.id] = widget;
					added = true;
				}
				
				// Apply in unit of sub category about content display mode.
				// TODO: Processing against the tab not built yet can be cut if building all tabs
				if(/MultiRssReader/.test(widget.widgetType) && name == "itemDisplay" && IS_Portal.prefsObj[name] != ""){
//					if(widget.content){
//						var rssReaders = widget.content.getRssReaders();
						var rssReaders = IS_Portal.getSubWidgetList(widget.id, widget.tabId);
						for(var i=0;i<rssReaders.length;i++){
//							if(widget.content.isDisplay(rssReaders[i]) && checkValue(rssReaders[i], IS_Portal.prefsObj, name)){
								rssReaders[i].setUserPref(name, IS_Portal.prefsObj[name]);
								modifiedWidgetList[rssReaders[i].id] = rssReaders[i];
//							}
						}
//					}
					/*
					else{
						var feeds = widget.widgetConf.feed;
						for(var i=0;i<feeds.length;i++){
							var isDisplay = (getBooleanValue(feeds[i].isChecked) || feeds[i].property.relationalId != IS_Portal.getTrueId(widget.id, widget.widgetType));
							if(isDisplay && IS_Portal.prefsObj[name] != ""){
								feeds[i].property[name] = IS_Portal.prefsObj[name];
								
								//Send to Server
								var cmd = new IS_Commands.UpdateWidgetPropertyCommand(tabId.substring(3), feeds[i], name, IS_Portal.prefsObj[name]);
								IS_Request.CommandQueue.addCommand(cmd);
							}
						}
					}
					*/
					
				}
			}
			
			// Redraw
			if(widget.content && isReRender){
				for(var id in modifiedWidgetList){
					var modWidget = modifiedWidgetList[id];
					if(!modWidget.content || typeof modWidget == "function") continue;
					
					var type = modWidget.widgetType;
					if( isAllRefresh && modWidget.content.isRssReader && modWidget.isComplete) {
						var cacheKey = modWidget.id;
						Ajax.Request.lastModified[cacheKey] = null;
						Ajax.Request.etag[cacheKey] = null;
						Ajax.Request.cache[modWidget.content.getUrl()] = null;
						
						modWidget.loadContents();
						if( modWidget.maximize && !modWidget.parent )
							modWidget.maximize.loadContents();
					} else if( isAllRefresh && /MultiRssReader/.test( modWidget.widgetType ) &&
						modWidget.isComplete ) {
						modWidget.content.mergeRssReader.cacheHeaders = false;
						
						if( modWidget.content.isTimeDisplayMode() ) {
							modWidget.loadContents();
						} else {
							modWidget.content.mergeRssReader.isComplete = false;
						}
						
						if( modWidget.maximize ) {
							var maximize = modWidget.maximize;
							maximize.content.getRssReaders().each( function( rssReader ) {
								var cacheKey = rssReader.originalWidget.id;
								Ajax.Request.lastModified[cacheKey] = null;
								Ajax.Request.etag[cacheKey] = null;
								Ajax.Request.cache[rssReader.originalWidget.content.getUrl()] = null;
								
								rssReader.content.ignore304 = true;
							});
							
							maximize.loadContents()
						}
					} else if(modWidget.content.displayContents) {
					// displayContents must be implemented in widaget for rendering again
						modWidget.content.displayContents();
					}
				}
				IS_Portal.tabs[tabId].applyPreference = false;
			}
			
		}
		setTimeout(IS_Portal.endChangeTab, 1);
		
		function checkValue(widget, prefsObj, name){
			if((typeof widget.getUserPref(name) != "undefined") && prefsObj[name] != ""){
				return true;
			}
			return false;
		}
	}
	Control.Modal.close();
	// Display image of loading
	IS_Portal.startChangeTab();
	setTimeout(_applyPreference, 1);
}

//Create link for "To Management Page"
IS_Portal.buildAdminLink = function(){
	var adminLink = $("portal-admin-link");
	if(!adminLink)
		return;
	// if preview, not show admin link
	if(typeof window.IS_Preview != 'undefined'){
		adminLink.hide();
		return;
	}
	if(!is_isAdministrator) {
		// hide div if not an admin
		adminLink.hide();
		return;
	}
	
	var adminLabel = $.DIV({
		id:'admin-link'
		, className:'portal-user-menu-item-label'}
		, IS_R.lb_adminLink
	);
	var adminLinkDiv =$.A({className:'portal-user-menu-link', href:'javascript:void(0);', title:IS_R.lb_adminLink}, adminLabel);
	adminLink.appendChild(adminLinkDiv);

	Event.observe( adminLink, "click", function( e ) {
		window.open("admin");
		$('portal-user-menu-body').hide();
		Event.stop( e )
	});
};

IS_Portal.buildLogout = function() {
	var logout = $("portal-logout");
	if( !logout ) return;
	// if preview, not show logout
	if(typeof window.IS_Preview != 'undefined'){
		logout.hide();
		return;
	}
	// don't display "Logout", while no user logged in.
	if( !is_userId ){
		logout.hide();
		return;
	}
	
	var logoutLabel = $.DIV({id:'logout', className:'portal-user-menu-item-label'}, IS_R.lb_logout);
	
	var logoutDiv = $.A({className:'portal-user-menu-link', href:'#', title:IS_R.lb_logout}
		, logoutLabel);

	Event.observe( logout,"click",function( e ) {
		if( window.IS_Preview ) return Event.stop( e );
		
		Event.stopObserving( window,"unload",windowUnload );
		windowUnload();
		location.href = "logout";
	});
	logout.appendChild(logoutDiv);
};

IS_Portal.buildLogo = function() {
	var logo = $("portal-logo");
	if( !logo ) return;
	
	var logoA = logo.getElementsByTagName("a");
	if(logoA.length > 0){
		Event.observe( logoA[0],"click",function( e ) {
			IS_Portal.goHome();
			Event.stop( e );
		});
	}
};

// Log at dropping and drop processing
/*IS_Portal.menuDropped = function( id, rssUrl, title ){
	IS_EventDispatcher.newEvent('dropWidget', id, null);
	
	if(rssUrl && rssUrl.length != 0){
		var cmd = new IS_Commands.UpdateRssMetaCommand("1", rssUrl, rssUrl, title, "");
		IS_Request.LogCommandQueue.addCommand(cmd);
	}
}*/

IS_Portal.widgetDropped = function( widget ) {
	if( IS_TreeMenu.isMenuItem( widget.id ) )
		IS_EventDispatcher.newEvent( IS_Widget.DROP_WIDGET, IS_TreeMenu.getMenuId( widget.id ) );
	
//	var url = widget.getUserPref("url");
//	if( url ) {
//		IS_EventDispatcher.newEvent( IS_Widget.DROP_URL,url,widget );
//	}
}

// create message bar element.
IS_Portal.initMsdBar = function(){
	var msgbarDiv = document.createElement("div");
	msgbarDiv.id = "portal_msgbar";
	document.body.appendChild(msgbarDiv);
	IS_Portal.setDisplayMsgBarPosition();
	IS_Event.observe(window,"scroll", IS_Portal.setDisplayMsgBarPosition, false, "_msgbar");
	IS_Event.observe(window,"resize", IS_Portal.setDisplayMsgBarPosition, false, "_msgbar");
}

// set message bar position.
IS_Portal.setDisplayMsgBarPosition = function(){
	if($("portal_msgbar").style.display == "none") return;
	var scrollTop = parseInt(document.body.scrollTop);
	var innerHeight = getWindowHeight();
	var offset = parseInt($("portal_msgbar").offsetHeight);
	if(!Browser.isIE) offset += 1;
	
	$("portal_msgbar").style.top = (scrollTop + innerHeight) - offset;
}

// display message bar.
IS_Portal.displayMsgBar = function(id, msg){
	if(!$("portal_msgbar")) IS_Portal.initMsdBar();
	var msgBar = $("portal_msgbar");
	var msgLine = $("msgBar_" + id);
	if (!msgLine) {
		msgLine = document.createElement("div");
		msgLine.id = "msgBar_" + id;
		msgBar.appendChild(msgLine);
	}
	msgLine.innerHTML = msg;
	msgBar.style.display = "";
	IS_Portal.setDisplayMsgBarPosition();
}

// undisplay message bar.
IS_Portal.unDisplayMsgBar = function(id){
	var msgLine = $("msgBar_" + id);
	if(msgLine && msgLine.parentNode)
		msgLine.parentNode.removeChild(msgLine);
	else
		return;
	IS_Portal.setDisplayMsgBarPosition();
	var msgBar = $("portal_msgbar");
	if(msgBar.childNodes.length == 0)
		msgBar.style.display = "none";
}

IS_Portal.behindIframe = {
	init:function(){
		//if(!Browser.isIE)return;
		this.behindIframe = $(document.createElement('iframe'));
		this.behindIframe.border = 0;
		this.behindIframe.style.margin = 0;
		this.behindIframe.style.padding = 0;
		this.behindIframe.id = "is_portal_behind_iframe";
		this.behindIframe.frameBorder = 0;
		this.behindIframe.style.position = "absolute";
		this.behindIframe.src = "./blank.html";
		document.getElementsByTagName('body')[0].appendChild(this.behindIframe);
		this.behindIframe.hide();
	},
	
	show:function(element){
		//if(!Browser.isIE)return;
		Position.prepare();
		var pos = Position.cumulativeOffset(element);
		this.behindIframe.style.top = pos[1] + "px";
		this.behindIframe.style.left = pos[0] + "px";
		this.behindIframe.style.width = element.offsetWidth;
		this.behindIframe.style.height = element.offsetHeight;
		if(element.style.zIndex)
			this.behindIframe.style.zIndex = element.style.zIndex -1;
		else
			this.behindIframe.style.zIndex = 0;
		this.behindIframe.show();
		
		this.current = element;
	},
	
	hide:function(){
		//if(!Browser.isIE)return;
		this.behindIframe.style.left = 0 + "px";
		this.behindIframe.style.top = 0 + "px";
		this.behindIframe.style.width = 0;
		this.behindIframe.style.height = 0;
		this.behindIframe.hide();
	}
}

IS_Portal.CommandBar = {
	commandbarWidgetDivs : [],
	commandbarWidgets : [],
	init : function(){
		this.elm_commandbar = $('portal-command');
		if(Browser.isIE){
			this.elm_commandbar.childNodes[0].cellSpacing = '0';
		}
		var portalUserMenu = $('portal-user-menu');
		var portalUserMenuLabel = $('portal-user-menu-label');
		//IE: if user name is long, limit user menu width 150
		if(Browser.isIE && portalUserMenuLabel.offsetWidth > 150){
			Element.setStyle(portalUserMenu, {width: '150px'});
			Element.setStyle(portalUserMenuLabel, {width: '140px'});
		}
		
		var commandBarItems = $$("#portal-command .commandbar-item");
		var portalUserMenuBody = $.DIV({id:'portal-user-menu-body', style:'display:none;'});
		
		Event.observe(portalUserMenuBody, "click", function(e){
			$(this).hide();
			$('userMenuCloser').hide();
			Event.stop(e);
		}.bind(portalUserMenuBody));
		
		portalUserMenu.parentNode.appendChild(portalUserMenuBody);
		for(var i = 0; i < commandBarItems.length; i++){
			var itemDiv = commandBarItems[i];
			if(!/^disabled/.test(itemDiv.id)){
				this.hasCommandBar = true;
			}else{
				$(itemDiv).hide();
			}
			var itemId = itemDiv.id.replace(/^s_/, "");
			
			var cmdBarWidget = IS_Portal.getWidget(itemId, IS_Portal.currentTabId);
			if(cmdBarWidget){
				this.commandbarWidgets[itemId] = cmdBarWidget;
			}
			this.commandbarWidgetDivs[itemId] = itemDiv;
			
			// re-form user added link menu
			if(/w_1$/.test(itemDiv.id) && itemDiv.getAttribute('type') == 'link'){
				var userLinkA = itemDiv.childNodes[0];
				userLinkA.className = 'portal-user-menu-link user-link';
				var userLinkLabel = $.DIV({className: 'portal-user-menu-item-label'}, "");
				userLinkLabel.innerHTML = userLinkA.innerHTML;
				userLinkA.innerHTML = '';
				userLinkA.appendChild(userLinkLabel);
			}
			// put into portal user menu
			if(!itemDiv.getAttribute("outside") && !itemDiv.getAttribute('disabledCommand')){
				// hide empty td
				if(!Browser.isIE)
					$(itemDiv.parentNode).hide();
				
				itemDiv.className = 'portal-user-menu-item';
				portalUserMenuBody.appendChild(itemDiv);
				
				// stop event propergation if the item is a link that user added
				if(/w_1$/.test(itemDiv.id)){
					Event.observe(itemDiv, 'click', function(e){
						if(window.event){
							window.event.cancelBubble = true;
						}
						if(e && e.stopPropagation){
							e.stopPropagation();
						}
						portalUserMenuBody.hide();
						$("userMenuCloser").hide();
					});
				}
			}
			// remove label from command bar menu if a menu is displayed outside (except for user added link)
			else if(itemDiv.getElementsByTagName('a')[0] && itemDiv.id != 'portal-logo' && itemDiv.id != 'portal-change-fontsize' && itemDiv.id != 'portal-searchform' && itemDiv.id != 's_p_1_w_4'){
				if(/w_1$/.test(itemDiv.id)){
					// for IE layout
					Element.setStyle(itemDiv, {width: itemDiv.offsetWidth});
				}
				else{
					var labelA =itemDiv.getElementsByTagName('a')[0].childNodes[0];
					labelA.innerHTML = '';
					Element.setStyle(itemDiv, {width:'30px'});
					Element.setStyle(labelA, {backgroundPosition : 'center center'});
				}
			}
		}

		if(!this.hasCommandBar){
			$("command-bar").hide();
		}
		else{
			IS_Portal.CommandBar.show();
		}
		if(portalUserMenuBody.childNodes.length != 0){
			portalUserMenu.title = is_userName? is_userName : "";
			Element.setStyle(portalUserMenu, {
				background: 'url(./skin/imgs/user_menu_collapse.gif) no-repeat center right'
				, cursor: 'pointer'
			});
			
			//loginID mouseover and mouseout
			Event.observe(portalUserMenu, "mouseover", function(){
				// change background color to white
				if($("portal-user-menu-body").style.display != 'none') return;
				//TODO write in css if possible
				Element.setStyle($("portal-user-menu"),{
					backgroundColor : '#5286BB'
					, color : '#fff'
				});
			});
			
			var menu_mouseout = function(){
				// change background color to normal
				Element.setStyle($("portal-user-menu"),{
					backgroundColor : ''
					, color : ''
				});
			};
			Event.observe(portalUserMenu, "mouseout", menu_mouseout);
			
			// when not login.
			if($("portal-loginLink")){
				var loginLink = $("portal-loginLink");
				Event.observe(loginLink, "mouseover", function(e){
					menu_mouseout();
					Event.stop(e);
				});
				Event.observe(loginLink, 'click', function(e){
					if(window.event){
						window.event.cancelBubble = true;
					}
					if(e && e.stopPropagation){
						e.stopPropagation();
					}
				});
			}

			var closeMenu = function(e){
				IS_Widget.Ranking.hide(false, true);
				
				$("portal-user-menu-body").hide();
				$("userMenuCloser").hide();
				Event.stop( e );
				Element.setStyle($("portal-user-menu").parentNode, {backgroundColor: ''});
			};
			// loginID clicked
			IS_Event.observe(portalUserMenu, "click", function(e){
				$("portal-user-menu-body").show();
				// set width for IE only (do not set width for FF and Webkit to prevent unnecessary gap)
				if(Browser.isIE){
					Element.setStyle($("portal-user-menu-body"), {width: $("portal-user-menu-body").offsetWidth});
				}
				var targetPosition = Position.page($("portal-user-menu"));
				Element.setStyle($("portal-user-menu-body"), {
					left: targetPosition[0] - $("portal-user-menu-body").offsetWidth + $("portal-user-menu").offsetWidth
					, top: targetPosition[1] + $("portal-user-menu").offsetHeight
				});
				if(!$('userMenuCloser')){
					var winX = Math.max(document.body.scrollWidth, document.body.clientWidth);
					var winY = Math.max(document.body.scrollHeight, document.body.clientHeight);
					var closer = $.DIV({
						id:'userMenuCloser'
						, className:'widgetMenuCloser'
					});
					document.body.appendChild( closer );
					Element.setStyle(closer, {
						width: winX,
						height: winY,
						display: ''
					});
					
					IS_Event.observe(closer, 'mousedown', closeMenu, true);
					IS_Event.observe($("portal-user-menu-body"), "mouseover", function(){
						// change background color to normal
						$("portal-user-menu").parentNode.style.backgroundColor = $('portal-user-menu').style.color = '';
					}, "_portalUserMenu");
					Event.observe(window, 'resize', closeMenu, true);
				}else{
					$("userMenuCloser").show();
				}
			});
		}
	},
	show : function(){
		IS_Widget.Ticker.adjustTickerWidth();
		$("portal-command").setStyle({
			visibility: 'visible',
			position: '',
			left: "0px"
		});
		if(IS_SidePanel.adjustPosition) IS_SidePanel.adjustPosition();
	},
	changeDefaultView : function(){
		var goHome = $("portal-go-home");
		$(goHome.parentNode).hide();
		IS_Widget.Ticker.adjustTickerWidth();
		if(IS_SidePanel.adjustPosition) IS_SidePanel.adjustPosition();
	},
	changeIframeView : function(){
		var go_home = $("portal-go-home");
		$(go_home.parentNode).show();
		IS_Widget.Ticker.adjustTickerWidth();
		if(IS_SidePanel.adjustPosition) IS_SidePanel.adjustPosition();
	},
	isIframeViewHiddenWidget : function(itemId){
		// Judge commandbar widget hidden at displaying frmae in portal
		if(itemId == "portal-admin-link" || itemId == "portal-logo" || itemId == "portal-searchform")
			return false;
		
		var cmdBarWidget = this.commandbarWidgets[itemId];
		if(cmdBarWidget && (cmdBarWidget.widgetType == "Ticker" || cmdBarWidget.widgetType == "Ranking"))
			return false;
		
		return true;
	},
	isCommandBarWidget : function(widget){
		// Create for removing commandbar widget in tab list
		var itemId = (widget.id)? widget.id : widget;
		return (this.commandbarWidgets[itemId])? true : false;
	}
};

IS_Portal.checkSystemMsg = function(){
	var opt = {
	  method:'get',
	  asynchronous:true,
	  onSuccess:function(req){
		  var results = req.responseText.evalJSON();
		  if(!results || results.length == 0) return;
		  var systemMessageVar = $('system-message-var');
		  
		  if(!systemMessageVar){
			  var msgListDiv = $('message-list');
			  systemMessageVar = $.DIV(
				  {
					id:'message-newmsg'
				  });
			  msgListDiv.appendChild(systemMessageVar);
		  }else{
			  systemMessageVar.innerHTML = "";
		  }
		  results.each(function(msg){
			  if(msg.resourceId){

				  systemMessageVar.appendChild(
					  $.DIV(
						  {},
						  $.IMG(
							  {
								style:'position:relative;top:2px;paddingRight:2px',
								src:imageURL+"information.gif"
							  }
							  ),
						  IS_R.getResource(IS_R[msg.resourceId], msg.replaceValues)
						  )
					  );

			  }else{
				  console.log("non implementation");
			  }
		  });
		  $('message-bar').style.display = "";
		  IS_EventDispatcher.newEvent("adjustedMessageBar");
	  },
	  onFailure: function(t) {
		  msg.error(IS_R.getResource( IS_R.lb_getSystemMessageFailure +'{0} -- {1}',[t.status, t.statusText]));
	  },
	  onException: function(r, t){
		  msg.error(IS_R.getResource( IS_R.lb_getSystemMessageFailure +'{0}',[getErrorMessage(t)]));
	  }
	};
	AjaxRequest.invoke(hostPrefix + '/sysmsg', opt);
}

IS_Portal.getPortalOverlay = function() {
	if(!IS_Portal.portalOverlay)
		IS_Portal.portalOverlay = new IS_Portal.windowOverlay('portalOverlay', 'div');
	return IS_Portal.portalOverlay;
}

IS_Portal.startIndicator = function(target){
	var divOverlay = $("divOverlay");
	var panel = $(target? target : "panels");
	if(!divOverlay){
		divOverlay = document.createElement("img");
		divOverlay.src = imageURL +"indicator_verybig.gif";
		divOverlay.id = "divOverlay";
		divOverlay.className = "tabLoading";
		document.body.appendChild(divOverlay);
	}else{
		divOverlay.style.display = "block";
	}
	if(panel.offsetWidth > 0){
		divOverlay.style.top = findPosY(panel) + 200;
		divOverlay.style.left = findPosX(panel) + panel.offsetWidth/2 - divOverlay.offsetWidth/2;
	}
	IS_Portal.getPortalOverlay().show("default");
}

IS_Portal.endIndicator = function(e){
	var divOverlay = $("divOverlay");
	if(divOverlay){
		divOverlay.style.display = "none";
	}
	IS_Portal.widgetDisplayUpdated();
	
	IS_Portal.getPortalOverlay().hide();
}
