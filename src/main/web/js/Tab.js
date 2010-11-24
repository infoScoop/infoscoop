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

/******************************
 * Related to tab
 */
IS_Portal.widgetLists = new Object();
//IS_Portal.subWidgetLists = new Object();
IS_Portal.columnsObjs = new Object();

// List of Tab object
IS_Portal.tabs = new Object();
// Tab Div list in order of displaying screen
IS_Portal.tabList = new Array();

IS_Portal.currentTabId = null;
IS_Portal.addTabDiv;

IS_Portal.buildTabs = function(){
	var tabsDiv = document.getElementById("tabs");
	
	if(!useTab) return;
	
	var tabsContainer = $('tab-container');
	var tabsDiv = document.createElement("div");
	tabsDiv.id = "tabs";
	
	var tabsUl = document.createElement("ul");
	tabsUl.id = "tabsUl";
	tabsDiv.appendChild(tabsUl);
	
	// Adding tab
	var addTab = document.createElement("div");
	IS_Portal.addTabDiv = addTab;
	addTab.noWrap = "-1";
	addTab.className = "tablink addatab";
	addTab.id = "addTab";
	var addA = document.createElement("a");
	addA.innerHTML = IS_R.lb_addTabLink;
	addTab.appendChild(addA);
	var addTabWithBuildColumns = function(){
		
		var addNumber = IS_Portal.getNextTabNumber();
		var tabObj = IS_Portal.addTab( addNumber, IS_R.lb_newTab, "dynamic", null, 3);

		var tabId = tabObj.id;
//		IS_WidgetsContainer.rebuildColumns( tabId, 3 );
//		IS_WidgetsContainer.adjustColumnWidth();
		
		IS_Portal.controlTabs.addTab( tabObj.tab );
		IS_Portal.controlTabs.setActiveTab( tabObj.tab );
	};
	Event.observe(addA, 'click', addTabWithBuildColumns, false);
	
	tabsUl.appendChild(addTab);
	tabsContainer.appendChild(tabsDiv);
}

/**
	Get the id number of tab that is added next.
*/
IS_Portal.getNextTabNumber = function(){
	// +1 to the largest tab number in use.
	if( IS_Portal.tabList.length == 0 ) return 0;
	
	// 0 is fixed. Arrage number from 1 for DynamicPanel.
	var tabNumberList = [0];
	for(var i=0; i < IS_Portal.tabList.length; i++){
		if("static" != IS_Portal.tabs[IS_Portal.tabList[i].id].type){
			// staticPanel is not included(Became original ID)
			tabNumberList.push( parseInt( IS_Portal.tabList[i].id.substring(3) ) );
		}
	}
	tabNumberList.sort( function(a,b){ return (a-b); } );
	
	var tabNumber = tabNumberList[ tabNumberList.length-1 ] + 1;
	return tabNumber;
};

/**
	Adding tab
	@param selected Make active the tab adding. Active:true/Not active:false
	@param idNumber id number of the tab adding.
	@param name Name of tab adding.
	@param type Panel type of the tab adding (Include static panel:static/Exclude:dynamic)
	@param numCol Column number of the tab adding.
	@disabledDynamicPanel disable dynamic panel.
*/
IS_Portal.addTab = function( idNumber, name, type, layout, numCol, columnsWidth, disabledDynamicPanel, adjustStaticHeight, isInitialize){
	/**
		For managing tab information object
		@param id id of tab
		@param name Displaying name of tab
		@param type Panel type of tab(Include static panel:static/Exclude:dynamic)
		@param tab Div element of tab
		@param panel Div element of panel that correspond tab
		@param tabNumber Number of displaying order of tab.
	*/
	var Tab = function(id, name, type, tab, panel, tabNumber, columnsWidth, disabledDynamicPanel, adjustStaticHeight){
		this.id = id;
		this.name = name;
		this.type = type;
		this.tab = tab;
		this.panel = panel;
		this.tabNumber = tabNumber;
		this.numCol = 0;
		this.isColumnBuilt = false;
		if(columnsWidth)
			this.columnsWidth = columnsWidth;
		this.disabledDynamicPanel = disabledDynamicPanel;
		this.adjustStaticHeight = adjustStaticHeight;
			
		this.refresh = function(e){
			Element.addClassName( tab,"loading");
			IS_Portal.buildContents(this.id, true);
			return false;
		}
		this.close = function(){
			if(IS_Portal.tabList.length == 1){
				alert(IS_R.ms_cannotDeleteLastTab);
				return false;
			}
			if( window.confirm(IS_R.getResource(IS_R.ms_deleteTabConfirm,[this.name]))){
				IS_Portal.deleteTab(this.tab);
			}
			
			IS_Portal.behindIframe.hide();
			return false;
		}
		this.rename = function(newname){
			var nowText = newname.replace(/　| /g, "");
			if ( nowText.length == 0 )
				return false;
			
			titleValue = newname;
			
			//Send to Server
			IS_Widget.setTabPreferenceCommand(tab.id, "tabName", titleValue);
			
//			var titleSpan = $( this.id ).childNodes[1];
			var titleTd = $( this.id + "_title" );
			
			titleTd.removeChild(titleTd.childNodes[0]);
			titleTd.appendChild(document.createTextNode(titleValue));
			if (IS_Portal.tabs[tab.id]) {
				IS_Portal.tabs[tab.id].name = titleValue;
			}
			
//			tab.replaceChild(titleSpan, titleEditorFormDiv);
//			IS_Portal.isDisplayTabTitleEditor = false;

			return true;
		}
		this.changeColumnNum = function(numCol){
			clearTimeout(this.changeColumnNumTimer);
			this.changeColumnNumTimer = setTimeout(function(){
				IS_WidgetsContainer.rebuildColumns(this.id, parseInt(numCol));
				// Save here becuase rebuildColumns does not include saving processing
				IS_Widget.setTabPreferenceCommand( this.id, "numCol", numCol);
			}.bind(this), 100);
			return false;
		}
		this.resetColumnWidth = function(){
			IS_WidgetsContainer.rebuildColumns(this.id, this.numCol, false, true);
			return false;
		}
	}
	
	var addTabDiv = document.getElementById("addTab");
	var tabDiv = IS_Portal.buildTab( idNumber, name, disabledDynamicPanel);
	
	if(useTab){
//		var tabsContiner = addTabDiv.previousSibling;
//		tabsContiner.appendChild(tabDiv);
		var tabsContiner = $("tabsUl");
		tabsContiner.insertBefore(tabDiv, addTabDiv);
	}
	
	
	var panelDiv = IS_Portal.buildPanel( idNumber,  type, layout);
	var panels = $("panels");
	panels.appendChild( panelDiv );
	
	IS_Portal.widgetLists[tabDiv.id] = new Object();
//	IS_Portal.subWidgetLists[tabDiv.id] = new Object();
	IS_Portal.columnsObjs[tabDiv.id] = {};
	
	var tabObj = new Tab(tabDiv.id, name, type, tabDiv, panelDiv, IS_Portal.tabList.length, columnsWidth, disabledDynamicPanel, adjustStaticHeight);
	IS_Portal.tabs[tabObj.id] = tabObj;
	
	IS_Portal.tabList.push(tabDiv);
	
	//Send to Server
	if (!isInitialize) {
		IS_Widget.addTabCommand(tabDiv.id, name, type, numCol);
		IS_Widget.setTabPreferenceCommand(tabDiv.id, "tabNumber", IS_Portal.tabList.length);
	}
	
	if(useTab && maxTabs <= IS_Portal.tabList.length){
		// Hide "Add tab" link if the tab is added by maximum.
		addTabDiv.style.display = "none";
	}
	IS_WidgetsContainer.rebuildColumns(tabObj.id, numCol, columnsWidth, false, isInitialize);
	return tabObj;
}

/**
	Create div element of tab
	@param selected Create a tab in active mode:true/inactive:false
	@param tabNumber ID numebr of creating tab
	@param name Name of creating tab
*/
IS_Portal.buildTab = function( tabNumber, name, disabledDynamicPanel){
	// Creating tab
	var tab = document.createElement("li");
	tab.id = "tab"+tabNumber;
	tab.setAttribute("href",( tab.href = "#panel"+tabNumber ));
	tab.className = "tab";
	
	var outerSpan = document.createElement('span');
	outerSpan.className = 'outer';
	var innerSpan = document.createElement('span');
	innerSpan.className = 'inner';
	
	IS_EventDispatcher.addListener("tabLoadCompleted",tab.id,function() {
			Element.removeClassName( tab,"loading");
		},null,false );
	IS_EventDispatcher.addListener("tabLoadStart",tab.id,function() {
			Element.addClassName( tab,"loading");
		},null,false );
	
	var tabTr = $.TR();
	
	tabTr.appendChild(
		$.TD({},
			$.IMG({src:imageURL +"indicator.gif", id:tab.id+"_loadingIcon", className:"tabLoadingIcon"})
		)
	);
	
	if(disabledDynamicPanel){
		tabTr.appendChild(
			$.TD({width:6},
				$.IMG({src:imageURL+"pin-small.gif", className:"fixedTab", title:IS_R.ms_thisIsFixedTab})
			)
		);
	}
	
	tabTr.appendChild($.TD({},
		$.SPAN({id:tab.id + "_title", className:"tabTitle"}, name)
	));
	
	if(disabledDynamicPanel){
		var refreshImg = $.IMG({id:tab.id+"_selectMenu", src:imageURL+"refresh.gif", className:"selectMenu"});
		tabTr.appendChild($.TD({}, refreshImg));
		IS_Event.observe(refreshImg, 'click', function(e){
			var tabObj = IS_Portal.tabs[this.id];
			tabObj.refresh();
		}.bindAsEventListener(tab), false, tab.id);
	} else {
		var selectMenuImg = $.IMG({id:tab.id+"_selectMenu", src:imageURL+"bullet_arrow_down.gif", className:"selectMenu"});
		tabTr.appendChild($.TD({}, selectMenuImg));
		IS_Event.observe(selectMenuImg, 'click', IS_Portal.showTabMenu.bind(selectMenuImg, tab), false, tab.id);
		IS_Event.observe(selectMenuImg, 'mousedown', function(e){Event.stop(e);}, false, tab.id);
	}
	
	var tabOnMousedown = function(e){
		IS_Portal.tabDrag(e, tab);
	}
	IS_Event.observe(tab, 'mousedown', tabOnMousedown, false, tab.id);
	
	innerSpan.appendChild($.TABLE({cellPadding:0, cellSpacing:0}, $.TBODY({}, tabTr)));
	
	outerSpan.appendChild(innerSpan);
	tab.appendChild(outerSpan);
	
	IS_Portal.setTabDroppable(tab);
	
	return tab;
}

IS_Portal.showTabMenu = function(tabElement, e){
	var tabMenu = $(tabElement.id + "_menu");
	var tabObj = IS_Portal.tabs[tabElement.id];
	var isInit = false;
	if(!tabMenu){
		tabMenu = buildTabMenu(tabElement,tabObj);
		isInit = true;
	}
	var tabMenuOverlay = $(tabElement.id + "_overlay");
	if (!isInit && tabMenu.style.display != "none") {
		tabMenu.style.display = "none";
		IS_Portal.behindIframe.hide();
	} else {
		tabMenu.style.display = "";
		
		//Calculate far left side of menu
		var offset= findPosX(tabElement.firstChild);
		var winX = getWindowSize(true) - 25;
		if( (offset + tabMenu.offsetWidth ) > winX ){//If the width of whole menu is larger than the distance between the left edge of top menu and the right edge of window.
			offset = (winX  - tabMenu.offsetWidth );
		}
		
		tabMenu.style.left = offset;
		tabMenu.style.top = (findPosY(tabElement) + tabElement.firstChild.offsetHeight);

		IS_Portal.behindIframe.show(tabMenu);
	}
	Event.stop(e);
	
	function buildTabMenu(tabElement,tabObj ){
		var menuDiv = document.createElement("ul");
		menuDiv.id = (tabElement.id + "_menu");
		menuDiv.className = "tabMenu";
		
		// Update
//		var refreshDiv = createAnchorItem("refresh","Reload",tabObj.refresh.bind( tabObj ));
		var refreshDiv = createItem({
			anchor: true,
			className: "refresh",
			label: IS_R.lb_reload,
			handler: tabObj.refresh.bind( tabObj )
		});
		refreshDiv.id = tabObj.id +"_menu_refresh";
		menuDiv.appendChild( refreshDiv );
		
		if (tabObj.type == 'dynamic') {
			// Delete
//			var closeDiv = createAnchorItem("close","Delete",tabObj.close.bind( tabObj ));
			var closeDiv = createItem({
				anchor:true,
				className:"close",
				label: IS_R.lb_delete,
				handler:tabObj.close.bind( tabObj )
			});
			closeDiv.id = tabObj.id +"_menu_close";
			menuDiv.appendChild( closeDiv );
			
			// Change name of tab
			
			titleDiv = document.createElement("nobr");
			titleDiv.appendChild(document.createTextNode( IS_R.lb_rename ));
			var nameInput = document.createElement("input");
			nameInput.className = "nameInput"
			nameInput.id = tabObj.id + "_menu_rename_input";
			nameInput.maxLength = 80;
			Event.observe(nameInput, "blur", function(){
				commitRename( tabObj,nameInput );
			}, false, tabObj.id);
			Event.observe(nameInput, "keyup", function(e) {
				if( e.keyCode != 13 )
					return;
				
				commitRename( tabObj,nameInput );
			}, false, tabObj.id);
			function commitRename( tabObj,nameInput ) {
				if( !tabObj.rename( nameInput.value ))
					nameInput.value = tabObj.name;
			}
			
			nameInput.value = tabObj.name;
			Event.observe(nameInput, "focus", function(){
				nameInput.select();
			}, false,tabObj.id)
			titleDiv.appendChild(nameInput);
			
			var renameDiv = createItem( { className:"rename",content: titleDiv });
			renameDiv.id = tabObj.id +"_menu_rename";
			menuDiv.appendChild( renameDiv );
		}
		
		// Change number of column
		titleDiv = document.createElement("div");
		titleDiv.style.cursor = "normal";
		titleDiv.appendChild( document.createTextNode( IS_R.lb_changeColumn ));
		var select = document.createElement("select");
		select.id = tabObj.id +"_menu_changeColumnNum_select";
		select.className = "columnNumSelect";
		for(var i=1;i<=maxColumnNum;i++){
			var option = document.createElement("option");
			option.innerHTML = i;
			option.value = i;
			if(i == tabObj.numCol)
				option.selected = true;
			select.appendChild(option);
		}
		var changeColumnNumTimer;
		Event.observe(select, "change", function(){
			if( changeColumnNumTimer ) clearTimeout( changeColumnNumTimer );
			changeColumnNumTimer = setTimeout( tabObj.changeColumnNum.bind( tabObj,select.value ),300 );
		}, false, tabObj.id );
		titleDiv.appendChild(select);
		
		var changeColumnNumDiv = createItem({ className:"changeColumnNum",content: titleDiv });
		changeColumnNumDiv.id = tabObj.id +"_menu_changeColumnNum";
		menuDiv.appendChild( changeColumnNumDiv );
		
		// Reset the width of column
		var resetColumnWidthDiv = createItem({
			anchor:true,
			className: "resetColumnWidth",
			label: IS_R.lb_resetColumnWidth,
			handler: tabObj.resetColumnWidth.bind( tabObj )
		});
		resetColumnWidthDiv.id = tabObj.id +"_menu_resetColumnWidthDiv";
		menuDiv.appendChild( resetColumnWidthDiv );

		var initTab = function(tabId){
			if( !confirm( IS_R.ms_clearTabConfigurationConfirm ))
				return;
			
			IS_Request.asynchronous = false;
			IS_Request.CommandQueue.fireRequest();
			
			var opt = {
				method: 'get' ,
				asynchronous:false,
				onSuccess: function(req){
					window.location.reload( true );
				},
				onFailure: function(t) {
					var msg = IS_R.ms_clearConfigurationFailed;
					alert( msg );
					msg.error( msg );
				}
			};
			AjaxRequest.invoke(hostPrefix + "/widsrv?reset=true&tabId=" + tabId, opt);
		}.bind(this, tabObj.id);

		if(tabObj.type == 'static' && !tabObj.disabledDynamicPanel){
			var initTabDiv = createItem({
			  anchor:true,
			  className:"initialize",
			  label: IS_R.lb_initTab,
			  handler: initTab
			});
			initTabDiv.id = tabObj.id +"_menu_initTab";
			menuDiv.appendChild( initTabDiv );
		}
		function createItem( opt ) {
			var className = opt.className || "";
			
			var borderDiv = document.createElement("li");
			borderDiv.className = "borderDiv";
			
			var itemDiv = document.createElement( opt.anchor ? "a":"div");
			itemDiv.className = className + " item";
			itemDiv.id = tabObj.id+"_menu_"+className;
			if( opt.anchor )
				itemDiv.href = "javascript:void(0)";
			
			var content = document.createElement("div");
			content.className = "content";
			itemDiv.appendChild( content );
			
			if( opt.label ) 
				content.appendChild( document.createTextNode( opt.label ));
			
			if( opt.content )
				content.appendChild( opt.content );
			
			if( opt.handler )
				IS_Event.observe( itemDiv,"click",opt.handler,false,tabObj.id );
			
			borderDiv.appendChild( itemDiv );
			
			return borderDiv;
		}
		
		document.body.appendChild(menuDiv);
		
		var handleHideTabMenu = hideTabMenu.bind( tabObj );
		IS_Event.observe(document.body, 'click',handleHideTabMenu, true, tabObj.id);
		IS_Event.observe(document.body, 'mousedown',handleHideTabMenu, true, tabObj.id);
		
		return menuDiv;
	}
}

function hideTabMenu( event ) {
	var tabMenu = $( this.id+"_menu");
	var selectMenu = $( this.id+"_selectMenu");
	var changeColumnSelect = $(this.id +"_menu_change_column_select");
	
	if( event ) {
		var element = Event.element( event );
		if( Element.childOf( element,tabMenu ) ||
			element === selectMenu || //for FF
			Element.childOf( element,selectMenu ) || //for IE
			tabMenu.style.display == "none") return;
	}
	if( tabMenu ) tabMenu.style.display = "none";
	IS_Portal.behindIframe.hide();
	
	// Focus is left in IE.
	if( Browser.isIE )
		document.body.focus();
//	Event.stop( event );
}

/**
	Drop to the tab
*/
IS_Portal.getWidget2TabDroppableOpt = function(tab){
	var dropTarget = tab.firstChild;
	// widget to tab
	var widgetDropOpt = {};
	widgetDropOpt.accept = function(element, widgetType, classNames){
		return (
			!IS_Portal.tabs[tab.id].disabledDynamicPanel &&
			classNames.detect( 
          function(v) { return ["widget", "subWidget"].include(v) } ) &&
 			(tab.id != IS_Portal.currentTabId) && widgetType != "mapWidget");
	}
	widgetDropOpt.onDrop = function(element, lastActiveElement, widget, event) {
		if(!IS_Portal.canAddWidget(tab.id)) return;
		var process = function(){
			var widgetGhost = IS_Draggable.ghost;
			
			if( /MultiRssReader/.test(widget.widgetType) && (widget.id.indexOf("_p_") != -1) ){
				// Move Multi(Except new BOX)
				
				var multiId = tab.id + widget.widgetConf.id.substring( widget.widgetConf.id.indexOf("_p_") );
				var createMulti = false;
				var targetWidget;
				var siblingId = "";
				if(!IS_Portal.isWidgetInTab( tab.id, multiId )){
					// Move the content to the new empty Multi if there is no multi in the tab moving ahead.(For the id by tab)
					var widgetConf = eval('('+ Object.toJSON(widget.widgetConf) +')');//TODO:Is eval necessary?
					widgetConf.id = multiId;
					
					createMulti = true;
//					targetWidget = IS_WidgetsContainer.addWidget(tab.id, widgetConf, true);

					var subWidgetConfList = [];
					var subWidgets = IS_Portal.getSubWidgetList(widget.id, widget.tabId);
					for(var i=0;i<subWidgets.length;i++){
						subWidgetConfList.push(subWidgets[i].widgetConf);
						IS_Portal.removeSubWidget(widget, subWidgets[i], widget.tabId);
						IS_Portal.removeWidget(subWidgets[i].id, widget.tabId);
					}
					targetWidget = IS_WidgetsContainer.addWidget(tab.id, widgetConf, true, null, subWidgetConfList);
					IS_Widget.setWidgetLocationCommand(targetWidget);
				}else{
					targetWidget = IS_Portal.getWidget(multiId, tab.id);
					var rssReaders = IS_Portal.getSubWidgetList(widget.id, widget.tabId);
					
					// Make order
					if(widget.isBuilt){
						var orderList = [];
						var tempRssReaders = [];
						var nodelist = widget.elm_widgetContent.firstChild.childNodes;
						for(var i=0;i<nodelist.length;i++){
							orderList.push(IS_Portal.getTrueId(nodelist[i].id));
						}
						for(var i=0;i<orderList.length;i++){
							for (var j = 0; j < rssReaders.length; j++) {
								if(rssReaders[j].id == orderList[i]){
									tempRssReaders.push(rssReaders[j]);
									break;
								}
							}
						}
						rssReaders = tempRssReaders;
					}
					
					// Move each widget individually if there is Multi already.
					var nextSiblingId = (this.subCategoryNextSiblingId)?
						this.subCategoryNextSiblingId : "";
					
					for(var i=0;i<rssReaders.length;i++){
						if(!rssReaders[i]) continue;
						
						if (targetWidget.isBuilt) {
							IS_Portal.addSubWidget(targetWidget, rssReaders[i], targetWidget.tabId);
							targetWidget.content.addSubWidget(rssReaders[i], nextSiblingId);
							rssReaders[i].blink();
							
							if (!rssReaders[i].isComplete) {
								rssReaders[i].loadContents();
							}
							
							if( rssReaders[i].headerContent )
								rssReaders[i].headerContent.applyAllIconStyle();
							if( targetWidget.content.initRssReader( rssReaders[i] ) && targetWidget.content.isCategoryDisplayMode())
								rssReaders[i].content.displayContents();
							
							IS_EventDispatcher.newEvent("applyIconStyle", targetWidget.id );
							IS_EventDispatcher.newEvent("changeConnectionOfWidget", rssReaders[i].id);
							IS_EventDispatcher.newEvent("moveWidget", rssReaders[i].id);
						}else{
							IS_Portal.addSubWidget(targetWidget, rssReaders[i], targetWidget.tabId);
							
							// Disable if the widget move to the tab not yet built.
							if (rssReaders[i].isBuilt) {
								if (rssReaders[i].elm_widget.parentNode) 
									rssReaders[i].elm_widget.parentNode.removeChild(rssReaders[i].elm_widget);
								rssReaders[i].isBuilt = false;
								if (rssReaders[i].iframe) 
									rssReaders[i].iframe = false;
								rssReaders[i].isComplete = false;
								IS_Event.unloadCache(rssReaders[i].id);
							}
						}
						
						IS_Widget.setWidgetTabLocationCommand(rssReaders[i], rssReaders[i].tabId, targetWidget.tabId);
						IS_Widget.setWidgetLocationCommand(rssReaders[i]);
					}

					// Movement of child category.
					IS_Portal.replaceSubWidgetList(widget.id, widget.tabId, tab.id);
				}
				// Movement of child category.
//				IS_Portal.replaceSubWidgetList(widget.id, widget.tabId, tab.id);
				
				if(widget.headerContent){
					widget.headerContent.close("notCloseFeeds", true);
				} else {
					IS_Request.CommandQueue.addCommand(
						new IS_Commands.EmptyWidgetCommand(widget.widgetConf, widget.tabId.substring(3)));
				}
				
				IS_Portal.removeWidget(widget.id, widget.tabId);
				IS_EventDispatcher.newEvent("moveWidget", targetWidget.id);
			}else{
				// Ordinal movement
				var beforeRowIdx = (widget.parent)? IS_Widget.getSubWidgetRowIdx(widget) : IS_Widget.getWidgetRowIdx(widget, IS_Portal.currentTabId);
				var parent = null;
				
				if( element.className == "subWidget" ){
					if(widget.parent){ //No paraent
						parent = widget.parent;
						
						widget.widgetConf.column = widget.parent.widgetConf.column;
						IS_Portal.removeSubWidget(parent, widget.id);
						IS_EventDispatcher.newEvent("applyIconStyle", widget.id);
						IS_EventDispatcher.newEvent("changeConnectionOfWidget", widget.id);
						IS_EventDispatcher.newEvent("applyIconStyle", parent.id);
					}
					
//					IS_Portal.subWidgetLists[IS_Portal.currentTabId][widget.id] = null;
					
					//Send to Server
//					IS_Widget.removeWidgetCommand(widget, parent);
					
					widget.elm_widget.className = "widget";
				}else{
					if(widget.elm_widget.parentNode){
						widget.elm_widget.parentNode.removeChild(widget.elm_widget);
					}

					//Send to Server
//					IS_Widget.removeWidgetCommand(widget);
					/*
					IS_Portal.removeWidget(widget.id, widget.tabId);
					if (/MultiRssReader/.test(widget.widgetType)) {
						// Movement of child category
						IS_Portal.replaceSubWidgetList(widget.id, widget.tabId, tab.id);
					}*/
				}
				
				/*
				if(widget.tabId){
					widget.tabId = tab.id;
				}
				*/
				if( IS_Portal.tabs[tab.id].numCol < widget.widgetConf.column ){
					widget.widgetConf.column = IS_Portal.tabs[tab.id].numCol;
				}
				var columnObj = IS_Portal.columnsObjs[tab.id]["col_dp_" + widget.widgetConf.column];
			    if (columnObj.hasChildNodes()) {
			    	columnObj.insertBefore(widget.elm_widget, columnObj.firstChild);
			    }else {
			    	columnObj.appendChild(widget.elm_widget);
			    }
				
				var fromTabId = widget.tabId;
				
				//Send to Server
//				IS_Widget.addWidgetCommand(widget);
				IS_Widget.setWidgetTabLocationCommand(widget, widget.tabId, tab.id);
				IS_Widget.setWidgetLocationCommand(widget);
				
				if (/MultiRssReader/.test(widget.widgetType)) {
					// Movement of child category
					IS_Portal.replaceSubWidgetList(widget.id, fromTabId, tab.id);
					
					var subWidgets = IS_Portal.getSubWidgetList(widget.id, widget.tabId);
					for( var i=0;i<subWidgets.length;i++ ) {
						IS_Widget.setWidgetTabLocationCommand(subWidgets[i], fromTabId, tab.id);
					}
				}
				
				if (element.className != "subWidget")
					IS_Portal.removeWidget(widget.id, fromTabId);
				
				if(parent){
					if(parent.content.mergeRssReader)parent.content.mergeRssReader.isComplete = false;
					parent.content.checkAllClose(true);
				}
				
				if( widget.isGadget() ) {
					widget.tabId = tab.id;
					gadgets.rpc.call( widget.iframe.name,"tabChanged",false,widget.tabId );
					widget.onTabChangeReload = true;
				}
				
				IS_EventDispatcher.newEvent("moveWidget", widget.id);
			}
			
			element.style.position = "";
			
			if( /FragmentMiniBrowser/.test(widget.widgetType) && widget.isBuilt && !Browser.isIE){
				widget.onTabChangeReload = true;
			}
			
			
			element.style.display = "block";
			if(widgetGhost && widgetGhost.parentNode){
				widgetGhost.parentNode.removeChild(widgetGhost);
			}
			
			Element.removeClassName(dropTarget, "Tab_onHover");
			
			IS_Portal.widgetDisplayUpdated();
			IS_Widget.adjustDescWidth();
			
			if (widget.widgetType.indexOf("g_") == 0 )
				widget.onTabChangeAdjustIFrameHeight = true;
		};
		IS_Portal.moveToTab(element,dropTarget,process.bind(this));
	}
	widgetDropOpt.onHover = function(element, dropElement, dragMode, point){
		Element.addClassName(dropTarget, "Tab_onHover");
		var widgetGhost = IS_Draggable.ghost;
		widgetGhost.style.display = 'none';
	}
	widgetDropOpt.outHover = function(element, dropElement, dragMode, point){
		Element.removeClassName(dropTarget, "Tab_onHover");
		var widgetGhost = IS_Draggable.ghost;
		widgetGhost.style.display = 'block';
	}	
	
	return widgetDropOpt;
}

IS_Portal.setTabDroppable = function(tab){
	var dropTarget = tab.firstChild;
	tab.droppableOption = {};
	
	var widgetDropOpt = IS_Portal.getWidget2TabDroppableOpt(tab);
	widgetDropOpt = Object.extend(IS_DroppableOptions, widgetDropOpt);
	IS_Droppables.add(dropTarget, widgetDropOpt);
	
	//menuItem to tab
	var menuDropOpt = {};
	menuDropOpt.accept = function(element, widgetType, classNames){
		return (
			!IS_Portal.tabs[tab.id].disabledDynamicPanel &&
			classNames.detect( 
          function(v) { return ["menuItem"].include(v) } ) &&
 			(tab.id != IS_Portal.currentTabId)
			&& !IS_Portal.menuOver
			&& IS_Draggables.activeDraggable && !IS_Draggables.activeDraggable.options.syncId);
	}
	menuDropOpt.onDrop = function(element, lastActiveElement, menuItem, event) {
		var process = function(){
			var widgetGhost = IS_Draggable.ghost;
			
			var ghostColumnNum = (widgetGhost.col)? widgetGhost.col.getAttribute("colNum"):1;
			
			var parentItem = menuItem.parent;
			var multiId = tab.id+"_p_" + parentItem.id;
			var widgetConf;
			var subWidgets = [];
			if(/MultiRssReader/.test(menuItem.type) && !IS_Portal.isWidgetInTab( tab.id, multiId )){
				// TODO: Processing of cooperative menu
				var parentItem = menuItem.parent;
				
				var childList = parentItem.children.findAll( function( child ) {
					return ( child.type && /MultiRssReader/.test( child.type ));
				});
				var childMenuList = childList.collect( function( child ) {
					return child.id;
				});
				if(!parentItem.properties)parentItem.properties = [];
				parentItem.properties.children = childMenuList;
				
				var w_id = tab.id + "_p_" + parentItem.id;
				widgetConf = IS_WidgetsContainer.WidgetConfiguration.getConfigurationJSONObject(
					"MultiRssReader", w_id, ghostColumnNum, parentItem.title, parentItem.href, parentItem.properties);
				
				childList.each( function( child ) {
					if( child.id == menuItem.id){
						var feedConf = IS_WidgetsContainer.WidgetConfiguration.getFeedConfigurationJSONObject(
							"RssReader", "w_" + child.id, child.title, child.href, "true", child.properties);
						feedConf.menuId = child.id;
						subWidgets.push(feedConf);
					}
				});
			}else{
				/* Rebuild config everytime because menu can be changed.*/
				// Create JSONObject from menuItem.
				widgetConf = IS_SiteAggregationMenu.getConfigurationFromMenuItem(menuItem, ghostColumnNum);
			}
			menuItem.widgetConf = widgetConf;
			
			var siblingId = "";
			var widget;
			if( (/MultiRssReader/.test(widgetConf.type)) && IS_Portal.isWidgetInTab( tab.id, multiId ) ){
				var targetWidget = IS_Portal.getWidget(multiId, tab.id);
				menuItem.widgetConf.type = "RssReader";
				widget = IS_WidgetsContainer.addWidget( tab.id, menuItem.widgetConf, IS_Portal.tabs[tab.id].isBuilt, false);
				
				// Each widget is moved indivually if there is multi already.
				var nextSiblingId = "";
				
				if (targetWidget.isBuilt ) {
					IS_Portal.addSubWidget(targetWidget.id, widget.id, targetWidget.tabId);
					nextSiblingId = IS_Widget.getSubCategoryNextSibling(targetWidget, widget.id);
					targetWidget.content.addSubWidget(widget, nextSiblingId);
					widget.blink();
					
					if( targetWidget.content.isTimeDisplayMode())
						targetWidget.onTabChangeReload = true;
				}else{
					IS_Portal.addSubWidget(targetWidget.id, widget.id, targetWidget.tabId, true);
				}
				
				IS_Widget.setWidgetTabLocationCommand(widget, widget.tabId, targetWidget.tabId);
				IS_Widget.setWidgetLocationCommand(widget);
				
				// Call loadContents if the widget is dropped from menu(To display default)
				var menuWidget = IS_Portal.getWidget(widget.id, tab.id);
				if(menuWidget && menuWidget.isBuilt) menuWidget.loadContents();
				
				IS_Portal.widgetDropped( widget );
			}else{
				widgetConf.column = 1;
				widget = IS_WidgetsContainer.addWidget( tab.id, widgetConf, IS_Portal.tabs[tab.id].isBuilt, false, subWidgets.length>0? subWidgets : false);
				
				IS_Portal.widgetDropped( widget );
				for( var i=0;i<subWidgets.length;i++ ) {
					var sw = IS_Portal.getWidget( subWidgets[i].id,tab.id );
					if( sw ) {
						IS_Portal.widgetDropped( sw );
					}
				}
				
				if(!IS_Portal.tabs[tab.id].isBuilt){
					IS_Widget.insertAiryWidget(null, tab.id, widget, "");
				}
			}
			
			/*
			tab.style.border = '1px solid #999999';
			tab.style.borderWidth = '1px 1px 0 1px';
			*/
			Element.removeClassName(dropTarget, "Tab_onHover");
			
			IS_Portal.widgetDisplayUpdated();
			IS_Widget.adjustDescWidth();
			
		};
		IS_Portal.moveToTab(element,dropTarget,process.bind(this));
	}
	menuDropOpt.onHover = function(element, dropElement, dragMode, point){
		/*
		tab.style.border = '2px dashed #FF5533';
		tab.style.borderWidth = '1px 1px 0 1px';
		*/
		Element.addClassName(dropTarget, "Tab_onHover");
		var widgetGhost = IS_Draggable.ghost;
		widgetGhost.style.display = 'none';
	}
	menuDropOpt.outHover = function(element, dropElement, dragMode, point){
		/*
		tab.style.border = '1px solid #999999';
		tab.style.borderWidth = '1px 1px 0 1px';
		*/
		Element.removeClassName(dropTarget, "Tab_onHover");
		var widgetGhost = IS_Draggable.ghost;
		widgetGhost.style.display = 'block';
	}
	menuDropOpt = Object.extend(IS_DroppableOptions, menuDropOpt);
	IS_Droppables.add(dropTarget, menuDropOpt);
	
	var multimenuopt = {}
	multimenuopt = Object.extend(multimenuopt, widgetDropOpt);
//	multimenuopt.accept = "menuGroup";
	multimenuopt.accept = function(element, widgetType, classNames){
		return (
			!IS_Portal.tabs[tab.id].disabledDynamicPanel &&
			classNames.detect( 
          function(v) { return ["menuGroup", "multiDropHandle"].include(v) } ) &&
 			(tab.id != IS_Portal.currentTabId) );
	}
	
	tab.droppableOption.onMultiMenuDrop = function(element, lastActiveElement, menuItem, event, originFunc, modalOption){
		if(!IS_Portal.canAddWidget(tab.id)) return;
		var process = function(){
			var confs = IS_SiteAggregationMenu.createMultiDropConf.call(tab, element, lastActiveElement, menuItem, event, tab.droppableOption.onMultiMenuDrop, modalOption, tab);
			
			var widgetGhost = IS_Draggable.ghost;
			element.style.display = "none";
//			var divWidgetDummy = element.dummy;
//			if(divWidgetDummy.parentNode){
//				element = divWidgetDummy.parentNode.replaceChild(element, divWidgetDummy);
//				element.style.top = "0px";
//				element.style.width = "auto";
//			}
			
			if( !isUndefined("siteAggregationMenuURL")&& menuItem.owner == IS_TreeMenu.types.topmenu ) {
				IS_SiteAggregationMenu.closeMenu();
				IS_SiteAggregationMenu.resetMenu();
			}

			if(confs){
				var widgetConf = confs[0];
				var subWidgetConfList = confs[1];
				var otherWidgets = confs[2];
				var siblingId = "";
				
				if(widgetConf && subWidgetConfList.length > 0) {
					widgetConf.column = 1;
					widget = IS_WidgetsContainer.addWidget(tab.id, widgetConf, IS_Portal.tabs[tab.id].isBuilt, function(w){
						var columnObj = IS_Portal.columnsObjs[tab.id]["col_dp_" + widgetConf.column];
						columnObj.appendChild(w.elm_widget);
					}, subWidgetConfList);
					siblingId = widget.id;
					
					if(!IS_Portal.tabs[tab.id].isBuilt){
						IS_Widget.insertAiryWidget(null, tab.id, widget, "");
					}
					
					//Send to Server
					IS_Widget.setWidgetLocationCommand(widget);
					
					var menuId;
					if(widget.widgetConf){
	//					var rssReaders = widget.widgetConf.feed;
						var rssReaders = IS_Portal.getSubWidgetList(widget.id, tab.id);
						for(var i = 0; i < rssReaders.length; i++){
							if(!rssReaders[i]) continue;
							menuId = IS_Portal.getTrueId(rssReaders[i].id, rssReaders[i].type).substring(2);
							
							if(!IS_Portal.isChecked(menuId))
								IS_Portal.widgetDropped( rssReaders[i] );
						}
					}
				}
				otherWidgets.each(function(otherMenuItem){
					var otherWidgetConf = IS_SiteAggregationMenu.getConfigurationFromMenuItem(otherMenuItem, 1);
					var otherWidget = IS_WidgetsContainer.addWidget( tab.id, otherWidgetConf, IS_Portal.tabs[tab.id].isBuilt, function(w){
						var columnObj = IS_Portal.columnsObjs[tab.id]["col_dp_" + otherWidgetConf.column];
						columnObj.appendChild(w.elm_widget);
					}, false);
					IS_Portal.widgetDropped( otherWidget );
					if(!IS_Portal.tabs[tab.id].isBuilt){
						IS_Widget.insertAiryWidget(null, tab.id, otherWidget, siblingId);
					}
					siblingId = otherWidget.id;
				});
			}
			/*
			tab.style.border = '1px solid #999999';
			tab.style.borderWidth = '1px 1px 0 1px';
			*/
			Element.removeClassName(tab.firstChild, "Tab_onHover");
			
			if(widgetGhost && widgetGhost.parentNode){
				widgetGhost.parentNode.removeChild(widgetGhost);
			}
			
			IS_Portal.widgetDisplayUpdated();
			IS_Widget.adjustDescWidth();
		}
		IS_Portal.moveToTab(element,dropTarget,process.bind(this));
	}
	multimenuopt.onDrop = tab.droppableOption.onMultiMenuDrop;
	
	IS_Droppables.add(tab, multimenuopt);
}

IS_Portal.moveToTab = function(element, tab, process){
	var effectGhost = document.createElement("div");
	effectGhost.style.position = 'absolute';
	effectGhost.style.border = "1px dashed #F00";
	/*
	effectGhost.style.top = element.style.top;
	effectGhost.style.left = element.style.left;
	effectGhost.style.width = element.offsetWidth;
	effectGhost.style.height = element.offsetHeight;
	*/
	var dragElement = (IS_Draggable.dummyElement)? IS_Draggable.dummyElement : element;
	
	effectGhost.style.top = dragElement.style.top;
	effectGhost.style.left = dragElement.style.left;
	effectGhost.style.width = dragElement.offsetWidth;
	effectGhost.style.height = dragElement.offsetHeight;
	
	effectGhost.style.display = 'block';
	
	document.body.appendChild( effectGhost );
	
	var removeGhost = function(){
		if(effectGhost.parentNode){
			effectGhost.parentNode.removeChild( effectGhost );
		}
	}
	setTimeout(IS_Portal.shrink.bind(this,effectGhost,tab,50,10,removeGhost.bind(this),0),1);
	
	process();
}

IS_Portal.shrink = function(src,dest,aa, ab, callback, marginLeft) {
	var srcX = parseInt(findPosX(src));
	var srcY = parseInt(findPosY(src));
	var incX = (findPosX(dest)-srcX-marginLeft) / ab;
	var incY = (findPosY(dest)-srcY) / ab;
	var srcW = src.offsetWidth;
	var srcH = src.offsetHeight;
	var incW = (dest.offsetWidth-srcW) / ab;
	var incH = (dest.offsetHeight-srcH) / ab;
	src.style.width = srcW;
	src.style.height = srcH;
	
	var id = setInterval(function () {
		if (ab < 1) {
			clearInterval(id);
			callback();
			return;
		}
		ab--;
		srcX += incX;
		srcY += incY;
		src.style.left = parseInt(srcX) + "px";
		src.style.top = parseInt(srcY) + "px";
		srcW += incW;
		srcH += incH;
		src.style.width = srcW;
		src.style.height = srcH;
		
	}, aa / ab)
}

/**
	Delete specified tab
	@param deleteTab Div element of deleting tab.
*/
IS_Portal.deleteTab = function( deleteTab ){
	if($(deleteTab.id + "_titleEdit"))
		$(deleteTab.id + "_titleEdit").onblur = null;
	
	// delete
	var deleteId = deleteTab.id;
	// Get tabList number of deleting tab.
	var deleteTabListNumber = null;
	for(var i = 0; i < IS_Portal.tabList.length; i++){
		if( IS_Portal.tabList[i].id == deleteId ){
			deleteTabListNumber = i;
			break;
		}
	}
	if(deleteTabListNumber == null){
		msg.error(IS_R.getResource(IS_R.ms_deletaTabFailed,[IS_Portal.tabs[deleteId].name]));
		return;
	}
//	portalSaveWidget.fireDeleteRequest( deleteTab );
	
	// Deleting cache
	var deleteWidgetList = IS_Portal.widgetLists[deleteId];
	for(var id in deleteWidgetList){
		if( !(deleteWidgetList[id] instanceof Function) ){
			var widget = deleteWidgetList[id];
			if(widget){
				/*
				if(widget.content && widget.content.close) {
					widget.content.close();
				}
				IS_Event.unloadCache(widget.id);
				IS_Event.unloadCache(widget.closeId);
				*/
				if(widget.headerContent && widget.headerContent.close) {
					widget.headerContent.close();
				}
			}
		}
	}
	IS_Event.unloadCache(deleteId);
	
	// Ask for the next active tab.
	var nextCurrentTab;
	if( (deleteTabListNumber + 1) == IS_Portal.tabList.length ){
		nextCurrentTab = IS_Portal.tabList[deleteTabListNumber-1];
	}else{
		nextCurrentTab = IS_Portal.tabList[deleteTabListNumber+1];
	}
	IS_Portal.currentTabId = null;
	IS_Portal.controlTabs.setActiveTab( nextCurrentTab );
	
	// Deleting panel and tab.
	var deletePanel = IS_Portal.tabs[deleteId].panel;
	deletePanel.parentNode.removeChild( deletePanel );
	IS_Portal.tabs[deleteId].panel = null;
	
	//Send to Server
	IS_Widget.removeTabCommand(deleteId);
	
	deleteTab.parentNode.removeChild( deleteTab );
	
	//Deleting tab menu.
	var deleteTabMenu = $(deleteTab.id + "_menu");
	if( deleteTabMenu )
		deleteTabMenu.parentNode.removeChild(deleteTabMenu);
	
	var tabMenuOverlay = $( deleteId +"_overlay");
	if( tabMenuOverlay )
		tabMenuOverlay.parentNode.removeChild( tabMenuOverlay );
	
	IS_Portal.tabs[deleteId].tab = null;
	
	// Delete from list
	delete IS_Portal.widgetLists[deleteId];
//	delete IS_Portal.subWidgetLists[deleteId];
	delete IS_Portal.columnsObjs[deleteId];

	delete IS_Portal.tabs[deleteId];
	IS_Portal.tabList.splice(deleteTabListNumber,1);
	IS_Portal.refreshTabNumber();
	
	IS_Portal.updateAllTabNumber();
	
//	portalSaveWidget.fireRequest();
	
	// Display "Add a tab" link
	var addTabDiv = $("addTab");
	if(addTabDiv && IS_Portal.tabList.length < maxTabs){
		addTabDiv.style.display = Browser.isIE? "inline" : "inline-block";
	}
}

/**
	Make active the specified tab.
	@param changeTab Tab element to be active
*/
IS_Portal.changeActiveTab = function( changeTab, isInitialize ){
	
	//changeTab.className = "tab selectedtab";
	var lastTabId = IS_Portal.currentTabId;
	
	// check apply preference
	if(IS_Portal.tabs[changeTab.id].applyPreference)
		IS_Portal.applyPreference(changeTab.id, true);
	
	IS_Portal.startChangeTab();
	
	IS_Portal.currentTabId = changeTab.id;
	
	if(IS_Portal.tabs[changeTab.id].disabledDynamicPanel){
		$("siteMenuOpenTd").hide();
		$("siteMenu").hide();
	} else {
		$("siteMenuOpenTd").show();
		var siteMenuElm = $("siteMenu");
		siteMenuElm.style.width = 0;
		siteMenuElm.show();
		IS_SidePanel.adjustPosition();
	}
	
	IS_Widget.RssReader.RssItemRender.adjustRssDesc();
	
	IS_Portal.adjustStaticWidgetHeight();
	
	var changePanel = function(e){
		try{
			if(lastTabId != null){
				var lastTab = IS_Portal.tabs[ lastTabId ];
//				lastTab.tab.className = 'tab unselected';
//				lastTab.panel.style.display = "none";
//				portalSaveWidget.fireRequest.delay(1000)(lastTab);
				if(lastTab.type == "static")
				  lastTab.tab.firstChild.style.cursor = 'pointer';
			}
			
//			IS_Portal.tabs[ changeTab.id ].panel.style.display = "block";
//			changeTab.className = 'tab selected';
			if(IS_Portal.tabs[ changeTab.id ].type == "static")
			  changeTab.firstChild.style.cursor = 'default';

			//Redraw RssReader
			if(IS_Widget.RssReader.needToRepaint[changeTab.id])
				IS_Widget.RssReader.repaint(changeTab.id);

			if(! isInitialize ){
				IS_Portal.buildContents(changeTab.id,false );
				setTimeout(IS_Portal.endChangeTab, 1);
			}
			IS_Widget.adjustDescWidth();
		}catch(e){
			setTimeout(IS_Portal.endChangeTab, 1);
			msg.error(IS_R.getResource(IS_R.ms_changeTabFailed,[getText(e)]));
		}
	}
	changePanel.delay(0.001);
}

/**
  Cover div at switching tab
  
*/
IS_Portal.tabChanging = false;
IS_Portal.startChangeTab = function(e){
	IS_Portal.startIndicator();
}

IS_Portal.endChangeTab = function(e){
	IS_Portal.endIndicator();
	IS_EventDispatcher.newEvent("changeTab");
}

IS_Portal.adjustStaticWidgetHeight = function(){
	var currentTab = IS_Portal.tabs[IS_Portal.currentTabId];
	if(currentTab.type != "static")return;
	
	var tabNumber = IS_Portal.currentTabId.substr(3);
	var adjustToWindowHeight = currentTab.adjustStaticHeight;
	if(!adjustToWindowHeight)return;
	var widgets = IS_Portal.widgetLists[IS_Portal.currentTabId];
	var isReady = false;
	var adjustHeight = false;
	for(widgetId in widgets){
		var widget = widgets[widgetId];
		if(!widget.isBuilt)break;
		if(widget.panelType == "StaticPanel" && widget.widgetType != 'Ticker' && widget.widgetType != 'Ranking'){
			if(!adjustHeight)
				adjustHeight = getWindowSize(false) - findPosY(widget.elm_widgetContent) -36;
			var height = widget.headerContent ? adjustHeight : adjustHeight + 22;
			if(widget.widgetConf && widget.widgetConf.noBorder) height += 2;
			if(widget.iframe)
				widget.iframe.style.height = height + "px";
			widget.elm_widgetContent.style.height = height + "px";
			widget.staticWidgetHeight =  height ;

			if(widget.widgetType == 'RssReader' && widget.content.rssContentView){
				widget.content.rssContentView.setViewportHeight( height );
			}
		}
		isReady = true;
	}
	if(!isReady)setTimeout(IS_Portal.adjustStaticWidgetHeight,300);
}

/**
	Create panel 
	@param panelNumber ID number of panel.
	@param type The type of adding panel(Include static panel:static/Exclude:dynamic)
*/
IS_Portal.buildPanel = function(panelNumber, type, layout){
	// Create panel
	var panel = document.createElement("div");
	panel.id = "panel"+panelNumber;
	panel.className = "panel";
	panel.style.display = "none";
	panel.style.clear = "both";
	
	var table = document.createElement("table");
	panel.appendChild(table);
	table.style.width = "100%";
	table.style.cellpadding = "0";
	table.style.cellspacing = "0";
//	table.style.tableLayout = "fixed";
	var tbody = document.createElement("tbody");
	table.appendChild(tbody);
	
	var tr = document.createElement("tr");
	tbody.appendChild( tr );
	var td = document.createElement("td");
	td.style.width = "100%";
	tr.appendChild( td );
	
	// static
	if(layout){
		var staticPanel = document.createElement("div");
		
		staticPanel.id = "static-panel"+panelNumber;
		var staticDiv = document.createElement("div");
		staticPanel.appendChild(staticDiv);
		staticDiv.id = "static-portal-widgets"+panelNumber;
		
		if(type == "static"){
			staticPanel.innerHTML = layout;
			
			td.appendChild(staticPanel);
		}
	}
	// dynamic
	var dynamicPanel = document.createElement("div");
	dynamicPanel.style.clear = "both";
	td.appendChild(dynamicPanel);
	dynamicPanel.id = "dynamic-panel"+panelNumber;
	var dynamicDiv = document.createElement("div");
	dynamicPanel.appendChild(dynamicDiv);
	dynamicDiv.id = "dynamic-portal-widgets"+panelNumber;
	
	var columns = document.createElement("div");
	columns.id = "columns"+panelNumber;
	dynamicDiv.appendChild(columns);
	
	return panel;
}

/**
	Check if the specified id of widget is displayed on the specified tab.
	@param tabId The tab ID to be checked
	@param widgetId Widget ID to be checked
	@return Displayed:true/Not displayed:false
*/
IS_Portal.isWidgetInTab = function( tabId, widgetId ){
	var isChecked = false;
	var widgetList = IS_Portal.widgetLists[tabId];
	if(widgetList){
		var widget = widgetList[IS_Portal.getTrueId(widgetId)];
		if(widget && widget != null){
			isChecked = true;
		}
	}
	return isChecked;
}

/**
	Dragging specified tab
	@param e Event generated at dragging
	@param dragObj Div element of tab dragging
*/
IS_Portal.isTabDragging = false;
IS_Portal.tabDrag = function( e, dragObj ){
	var insertObj = dragObj;
	var targetObj = dragObj;
	var insertNumber = IS_Portal.tabs[ insertObj.id ].tabNumber;
	var targetNumber;
	var insertMarkObj;
	var isStatic = (IS_Portal.tabs[ insertObj.id ].type == "static")? true : false;
	
	var tabObj = IS_Portal.tabs[ IS_Portal.currentTabId ];
	if( tabObj )
		hideTabMenu.apply( tabObj );
	
	function makeMarkObj(){
		insertMarkObj = document.createElement('li');
		var markObj = document.createElement('sapn');
		markObj.className = 'tabInsertMark';
		markObj.innerHTML = "&nbsp;";
		insertMarkObj.appendChild( markObj );
	}
	makeMarkObj();
	
	var dragged = false;
	var dragStartX = 0;
	var dragStartY = 0;
	start(e);
	
	function start(e){
		if ( IS_Portal.isTabDragging ) return;
		dragStartX = Event.pointerX(e);
		dragStartY = Event.pointerY(e);
		
		Event.observe(document, "mousemove", dragging, false);
		Event.observe(document, "mouseup", dragEnd, false);
		
		IS_Portal.isTabDragging = true;
		
		for(var i=0; i<IS_Portal.tabList.length; i++){
			if(IS_Portal.tabList[i] == insertObj){
				insertNumber = i;
				targetNumber = i;
			}
		}
		
		// Prevent from transmitting to upper class of event
		Event.stop(e);
		
	}
	
	function dragging(e) {
		if ( !IS_Portal.isTabDragging ) return;
		
		// Detect place to insert.
		var mouseX = Event.pointerX(e);
		var mouseY = Event.pointerY(e);
		// Prevent from transmitting to upper class of event
		// Stop just after using because reversing character can not prevent if it is stopped at last of function.
		Event.stop(e);
		
		if(!dragged) {
			var dragX = Math.abs(dragStartX - mouseX);
			var dragY = Math.abs(dragStartY - mouseY);
			if(dragX < 16 && dragY < 16) return;
			dragged = true;
		}
		
		if( isStatic ) return;
		
		dragObj.firstChild.style.border = '1px dashed #FF5533';
		dragObj.firstChild.style.borderWidth = '1px 1px 0 1px';
		dragObj.firstChild.style.marginBottom = -1;
		
		var calcObj = ( targetObj ? targetObj : insertObj ).firstChild;
		// Get Y axis of current tab.
		var targetObjY = findPosY(calcObj);
		var targetObjH = calcObj.offsetHeight;
		
		var distanceListY = new Array();
		var distanceList = new Array();
		var startNo = null;
		for(var i=0; i<IS_Portal.tabList.length; i++){
			var tab = IS_Portal.tabList[i];
			var tabX = findPosX(tab.firstChild);
			var tabY = findPosY(tab.firstChild);
			
			var tabW = tab.offsetWidth;
			var tabH = tab.offsetHeight;
			var centerX = tabX + (tabW / 2);
			var centerY = tabY + (tabH / 2);
			var distanceX = mouseX - centerX;
			var distanceY = mouseY - centerY;
			
			// Distance between mouse and center of each tabs(square)
			var distance;
			if(IS_Portal.tabs[ tab.id ].type == "static"){
				// staticTab is not target
				distance = 9999999;
			}else{
				distance = distanceX*distanceX + distanceY*distanceY;
			}
			
			// J-active tub is 1px lower; judge with up-down 2px
			if((tabY >= targetObjY - 2) && (tabY <= targetObjY + 2)){
				if(!startNo) startNo = i;
				// Tab list in the same column
				distanceListY[i] = distance;
			}
			distanceList.push( distance );
		}
		
		var targetList;
		if(targetObjY < mouseY && (targetObjY + targetObjH) > mouseY){
			// If the mouse pointer is in the same line
			targetList = distanceListY;
		}else{
			startNo = 0;
			targetList = distanceList;
		}

		var min = startNo;
		for(var i=1; i<distanceList.length; i++){
			if(targetList[i]){
				if(targetList[i] < targetList[min]){
					min = i;
				}
			}
		}
		
		targetNumber = min;
		// The nearest tab
		var insertPoint = IS_Portal.tabList[targetNumber];
		var centerX = findPosX( insertPoint.firstChild ) + (insertPoint.firstChild.offsetWidth / 2);
		if(centerX < mouseX){

			targetNumber++;
			if(targetNumber == IS_Portal.tabList.length){
				insertPoint = false;
			}else{
				insertPoint = IS_Portal.tabList[targetNumber];
			}
		}
		
		targetObj = insertPoint;
		if(insertMarkObj.parentNode){
			insertMarkObj.parentNode.removeChild( insertMarkObj );
		}
		
		if(targetNumber != insertNumber && targetNumber != (insertNumber + 1)){
			if( targetObj ) {
				$("tabsUl").insertBefore( insertMarkObj, targetObj );
			} else {
				$("tabsUl").insertBefore( insertMarkObj, IS_Portal.addTabDiv );
			}
		}
	}
	
	function dragEnd(e) {
		Event.stopObserving(document, "mousemove", dragging, false);
		Event.stopObserving(document, "mouseup", dragEnd, false);
		
		if ( !IS_Portal.isTabDragging ) return;
		
		if(!dragged){
			// Double click normally if it is not dragged
			if(dragObj.id == IS_Portal.currentTabId){
				/*
				if(!IS_Portal.isDisplayTabTitleEditor){
					IS_Portal.showTabTitleEditorForm( dragObj );
				}
				*/
			}else{
				// Change active status if the none active tab is clicked
				IS_Portal.controlTabs.setActiveTab( dragObj );
			}
		}else if(!isStatic){
			
			dragObj.firstChild.style.border = 'none'
			dragObj.firstChild.style.borderWidth = '0px';
			dragObj.firstChild.style.marginBottom = 0;
			
			if(insertMarkObj.parentNode){
				insertMarkObj.parentNode.removeChild( insertMarkObj );
			}
			
			// Change the position of tab
			if(targetNumber != insertNumber && targetNumber != (insertNumber + 1)){
				if( targetObj ) {
					$("tabsUl").insertBefore( insertObj, targetObj );
				} else {
					$("tabsUl").insertBefore( insertObj, IS_Portal.addTabDiv );
				}
				
				if(insertNumber < targetNumber){
					IS_Portal.tabList.splice(insertNumber, 1);
					IS_Portal.tabList.splice(targetNumber-1, 0, insertObj);
				}else{
					IS_Portal.tabList.splice(insertNumber, 1);
					IS_Portal.tabList.splice(targetNumber, 0, insertObj);
				}
				IS_Portal.refreshTabNumber();
//				portalSaveWidget.fireTabNoRequest.delay(1000)();
				
				// Update tabNumber of all tabs
				/*
				for( var count=0; count < IS_Portal.tabList.length; count++){
					var tabId = IS_Portal.tabList[count].id;
					var tabNumber = IS_Portal.tabs[tabId].tabNumber;;
					IS_Widget.setTabPreferenceCommand(tabId, "tabNumber", tabNumber);
				}
				*/
				IS_Portal.updateAllTabNumber();
				
				//alert(IS_Portal.numStatic+' move '+insertNumber+':'+insertObj.id+' to '+targetNumber+' : '+targetObj.id);
			}
		}
		
		IS_Portal.isTabDragging = false;
	}
}

IS_Portal.updateAllTabNumber = function(){
	for( var count=0; count < IS_Portal.tabList.length; count++){
		var tabId = IS_Portal.tabList[count].id;
		var tabNumber = IS_Portal.tabs[tabId].tabNumber;;
		IS_Widget.setTabPreferenceCommand(tabId, "tabNumber", tabNumber);
	}
}

/**
	Update the order number of displaying tab object 
*/
IS_Portal.refreshTabNumber = function(){
	if(useTab){
		for(var i=0; i<IS_Portal.tabList.length; i++){
			var tabId = IS_Portal.tabList[i].id;
			IS_Portal.tabs[ tabId ].tabNumber = i;
		}
	}
}

IS_Portal.canAddWidget = function(tabId, alertOff){
	if(IS_Portal.tabs[tabId || IS_Portal.currentTabId].disabledDynamicPanel){
		if(!alertOff)
			alert(IS_R.ms_cannotAddGadgetToThisTab);
		return false;
	}
	return true;
}
