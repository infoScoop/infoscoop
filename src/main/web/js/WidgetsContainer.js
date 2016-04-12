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

widgetOrderReverse = typeof widgetOrderReverse != "undefined" ? widgetOrderReverse : false;
IS_Portal.numStatic = 0;
IS_Portal.isSliderReset = true;
IS_Portal.parentWidgetMap = {};
IS_Portal.subWidgetMap = {};
IS_Portal.subWidgetIds = [];
IS_Portal.loadWidgetQueue = {};
IS_Portal.loadingWidgetCount = 0;
var IS_WidgetsContainer = IS_Class.create();
IS_WidgetsContainer.prototype.classDef = function() {
	this.initialize = function(srvName) {
		var opt = {
		    method: 'get' ,
		    asynchronous:true,
		    onSuccess: displayWidgets,
		    on404: function(t) {

				msg.error(IS_R.getResource(IS_R.ms_containerLoadon404, [hostPrefix]));
				
		        IS_WidgetsContainer.loadFailed = true;
		    },
		    onFailure: function(t) {
				alert("Getting user's custormize information failed. Please try again later.");
				msg.error('Getting user\'s custormize information failed. : ' + t.status + ' -- ' + t.statusText);
				
		        IS_WidgetsContainer.loadFailed = true;
		    },
		    onException:function(r,t){
				alert(IS_R.lb_containerLoginonException);
				msg.error(IS_R.getResource(IS_R.lb_containerLoadonException, [getText(t)]));
				console.log(t);
				
		        IS_WidgetsContainer.loadFailed = true;
		    }
		};

		if(typeof srvName == "undefined"){
			var srvName = "widsrv" + ( (typeof displayTabOrder != "undefined") ? "?tabOrder=" + displayTabOrder : "" );
		}
		AjaxRequest.invoke(hostPrefix + "/" + srvName, opt);
	}
	
	function buildStaticPanel(tabId, widgets, isBuild) {
		for(var i in widgets) {
			if(widgets[i].id) {
				var container = $(widgets[i].id);
				if(container) {
					try {
						if('Ranking'==widgets[i].type)
							IS_Widget.Ranking.buildCommandBar( widgets[i].id );
						
						container.id = "s_" + widgets[i].id;
						var widget = new IS_Widget(false, widgets[i]);
						widget.panelType = "StaticPanel";
						widget.containerId = container.id;
						widget.tabId = tabId;
						var widgetConf = IS_Widget.getConfiguration(widget.widgetType);
						if(isBuild || widgetConf.forceBuild){
							widget.build();
				    		container.appendChild(widget.elm_widget);
				    	}
						IS_Portal.addWidget(widget, tabId);
					} catch(e) {
						alert(getText(e));
						IS_Portal.widgetLists[tabId][widgets[i].id] = {panelType:"StaticPanel", id:widgets[i].id, widgetConf:widgets[i]};
					}
				}else{
					if(!widgets[i].property) widgets[i].property = new Object();
					IS_Portal.widgetLists[tabId][widgets[i].id] = {panelType:"StaticPanel", id:widgets[i].id, widgetConf:widgets[i]};
				}
			}
		}
	}
	
	function buildDynamicPanel(tabId, widgets, isBuild) {
		var subWidgets = [];
		
		try {
			var deletes = [];
			
			$H( widgets ).values().findAll( function( widgetConf ) {
				return !widgetConf.type;
			}).each( function( widgetConf ) {
				//No type can be seen only if there is default widget in custermize area at initial login.
				//Synchronous load if the loading of menu is not finished.
				if ( displayTopMenu && IS_SiteAggregationMenu.topMenuIdList.length == 0 )
					new IS_SiteAggregationMenu(true);
				
				if ( displaySideMenu &&( !IS_SidePanel.topMenuIdList || IS_SidePanel.topMenuIdList.length == 0 ) )
					new IS_SidePanel.SiteMap(true);
				
				var menuItem = IS_TreeMenu.findMenuItem(widgetConf.menuId? widgetConf.menuId : widgetConf.id.substring(2));
				if( !menuItem)
					return;
				
				widgetConf.type = menuItem.type;
				widgetConf.title = menuItem.title;
				if(!!menuItem.href) widgetConf.href = menuItem.href;
				
				var originalId = widgetConf.id;
				var invalid = false;
				
				var isMulti = false;
				var children = [];
				if( widgetConf.property && widgetConf.property.children ) {
					children = eval( widgetConf.property.children ).collect( function( child ) {
						return menuItem.children.find( function( menuChild ) {
							return menuChild.id == child;
						});
					}).compact();
					
					isMulti = !children.find( function( menuItem ) {
						return !/^(?:Multi)?RssReader$/.test( menuItem.type );
					});
				}
				
				if( isMulti ) {
					widgetConf.type = "MultiRssReader";
					widgetConf.property = Object.clone( menuItem.properties );
					widgetConf.property.children = menuItem.children.collect( function( menuItem ) {
						return menuItem.id;
					});
					widgetConf.id = "p_"+widgetConf.id.substring(2);
					
					var empty = true;
					var lastChild;
					children.findAll( function( child ) {
						return !IS_Portal.isChecked( child );
					}).each( function( child ) {
						var subWidgetConf = IS_WidgetsContainer.WidgetConfiguration.getFeedConfigurationJSONObject(
							"RssReader", "w_" + child.id, child.title, child.href, "false", child.properties);
						subWidgetConf.parentId = widgetConf.id;
						
						var widgetObj = IS_Widget.getWidgetConfJSONString( Object.extend( subWidgetConf,{
							isDefault: function() { return false; }
						}) );
						
						widgets[subWidgetConf.id] = subWidgetConf;
						var siblingId = ( lastChild ? "w_"+lastChild.id : "");
						IS_Request.CommandQueue.addCommand( new IS_Commands.AddWidgetCommand( tabId.substring(3),{ id: subWidgetConf.id },
							String(0),siblingId,widgetObj,widgetConf.id,subWidgetConf.id.substring(2) ) );
						IS_Request.CommandQueue.addCommand( new IS_Commands.UpdateWidgetLocationCommand( tabId.substring(3),{ id: subWidgetConf.id },
							String(0),siblingId,widgetConf ));
						
						lastChild = child;
						empty = false;
					});
					
					if( empty )
						invalid = true;
				} else {
					widgetConf.property = Object.extend( {},menuItem.properties );
					
					if(widgetConf.type && widgetConf.type == "Gadget"){
						var url = widgetConf.property["url"];
						if(url) {
							widgetConf.type = "g_" + url;
							widgetConf.property["url"] = null;
						} else {
							msg.error("Gadget requires url property.");
						}
					}
					
				}
				
				if( !invalid ) {
					widgetConf.needUpdate = true;
					IS_Request.CommandQueue.addCommand( new IS_Commands.UpdateWidgetLocationCommand( tabId.substring(3),{ id: widgetConf.id },
						String( widgetConf.column || 1 ),"" ));
				} else {
					deletes.push( originalId );
				}
			});
			
			deletes.each( function( id ) {
				var widgetConf = widgets[id];
				delete widgets[id];
				
				IS_Request.CommandQueue.addCommand( new IS_Commands.EmptyWidgetCommand( widgetConf,tabId.substring(3) ));
			});
		} catch( ex ) {
			throw ex;
		}
		
		for(var i in widgets) {
			if(widgets[i].id) {
				try{
					setTabId( tabId, widgets[i] );
					
					var widget = new IS_Widget(true, widgets[i]);
					widget.tabId = tabId;
					
					if(widget.widgetConf.needUpdate){
						IS_Widget.updateWidgetCommand(widget);
					}
					
					if(widgets[i].parentId){
						widgets[i].tabId = tabId;
						subWidgets.push(widgets[i]);
						widget.isSubWidget = true;
					}
					IS_Portal.addWidget(widget, tabId);
				} catch(e) {
					console.log(e);
					IS_Portal.widgetLists[tabId][widgets[i].id] = {panelType:"DynamicPanel", widgetConf:widgets[i]};
					throw e;
				}
			}
		}
		
		for(var i=0;i<subWidgets.length;i++){
			var parentId = subWidgets[i].parentId;
			IS_Portal.addSubWidget(parentId, subWidgets[i].id, subWidgets[i].tabId);
		}
		
		for(i in IS_Portal.widgetLists[tabId]){
			var widget = IS_Portal.getWidget(i, tabId);
			
			if(widget.panelType == "DynamicPanel"){
				if (!widget.isSubWidget) {
					var subWidgetList = IS_Portal.getSubWidgetList(widget.id, tabId);
					for(j = 0; j < subWidgetList.length; j++){
						subWidgetList[j].parent = widget;
					}
				}
			}
			var widgetConf = IS_Widget.getConfiguration(widget.widgetType);
			if(widget.panelType == "DynamicPanel" && (isBuild || widgetConf.forceBuild)){
				var columnObj = IS_Portal.columnsObjs[tabId]["col_dp_" + widget.column];
				if(!columnObj) {
					columnObj = IS_Portal.columnsObjs[tabId]["col_dp_" + IS_Portal.tabs[tabId].numCol];
					widgets[i].column = IS_Portal.tabs[tabId].numCol;
				}
				if(!widget.isSubWidget){
					/*
					var subWidgetList = IS_Portal.getSubWidgetList(widget.id, tabId);
					for(j = 0; j < subWidgetList.length; j++){
						subWidgetList[j].parent = widget;
					}
					*/
					widget.build();
					if(widgetOrderReverse) {
						if (columnObj.hasChildNodes()) {
							columnObj.insertBefore(widget.elm_widget, columnObj.firstChild);
						}else {
							columnObj.appendChild(widget.elm_widget);
						}
					} else {
						var end = $("columns"+tabId.substring(3)+"_end_" + widget.column);
						if(end) {
							columnObj.insertBefore(widget.elm_widget, end);
						} else {
							columnObj.appendChild(widget.elm_widget);
						}
					}
				}
			}
		}
	}
	
	function displayWidgets(response) {
		try{
			var widgetConfList = eval("(" + response.responseText + ")");

			var sortFunc = function( a, b ){
				if( (a.tabType && (a.tabType=="static"))
					&& !(b.tabType && (b.tabType=="static")) ) return -1;
				if( !(a.tabType && (a.tabType=="static"))
					&& (b.tabType && (b.tabType=="static")) ) return 1;
	
				// Show them in order of displayed number if tabType has the same value
				if(a.tabNumber && !b.tabNumber) return -1;
				if(!a.tabNumber && b.tabNumber) return 1;
				if(a.tabNumber && b.tabNumber){
					if(parseInt(a.tabNumber) < parseInt(b.tabNumber)) return -1;
					if(parseInt(b.tabNumber) < parseInt(a.tabNumber)) return 1;
				}
	
				// Show them in order of ID if there is no displayed number
				if(!b.tabId) return -1;
				if(!a.tabId) return 1;
				if(parseInt(a.tabId) <= parseInt(b.tabId) ) return -1;
				if(parseInt(b.tabId) < parseInt(a.tabId) ) return 1;
			}
			widgetConfList.sort( sortFunc );
			
			var tabOrder = -1;
			var buildTargetTabIds = [];
			for(var tabId = 0; tabId < widgetConfList.length; tabId++){
				if(widgetConfList[tabId].buildVersion){
					IS_Portal.buildVersion = widgetConfList[tabId].buildVersion;
				}else if( useTab || tabId == 0 ){
					var id = widgetConfList[tabId].tabId;
					if(!id){
						if(tabId == 0){
							id = 0;
						}else{
							id = parseInt( IS_Portal.tabList[ tabId-1 ].id.substring(3) ) + 1;
						}
						widgetConfList[tabId].tabId = id;
					}

					var tabName = (widgetConfList[tabId].tabName)? widgetConfList[tabId].tabName : IS_R.lb_newTab;
					var numCol = (widgetConfList[tabId].property.numCol)? widgetConfList[tabId].property.numCol : 3;
					if(widgetConfList[tabId].faild){
						IS_Portal.lastSaveFaild = true;
					}
					
					var columnsWidth = null;
					try{
						columnsWidth = widgetConfList[tabId].property.columnsWidth;
						columnsWidth = eval( "(" + columnsWidth + ")");
					}catch(e){
						console.log(e);
					}
					
					var tabType = (widgetConfList[tabId].tabType)? widgetConfList[tabId].tabType : "dynamic";
					if(tabType == "static"){
						IS_Portal.numStatic++;
					}
					
					var disabledDynamicPanel = widgetConfList[tabId].disabledDynamicPanel;
					if(widgetConfList[tabId].isTrashDynamicPanelWidgets){
						var msgListDiv = $('message-list');
						msgListDiv.appendChild(
							$.DIV(
								{id:'message-newmsg'},
								$.IMG(
									{
										style:'position:relative;top:2px;paddingRight:2px',
										src:imageURL+"information.gif"
									}
								),
								IS_R.getResource(IS_R.ms_changeToFixedTab, [tabName])
							)
						);
						$('message-bar').style.display = "";
						IS_EventDispatcher.newEvent("adjustedMessageBar");
					}
					
					IS_Portal.addTab( id, tabName, tabType, numCol, columnsWidth, disabledDynamicPanel, true, widgetConfList[tabId].isTabAdmin, widgetConfList[tabId].property);
					
					buildTargetTabIds.push(id);
					
					if(!useTab && IS_Portal.tabList.length > 0){
						if(widgetConfList[0].tabNumber){
							IS_Portal.tabs["tab0"].tabNumber = widgetConfList[0].tabNumber;
						}
						var tabs = $('tabs');
						IS_Portal.tabList[0].style.display = "none";
					}
				}
			}
			
			// get targetTabId from location hash
            var targetTabId = "tab" + is_getParameterFromHash("tabId");
            targetTabId = IS_Portal.tabs[targetTabId] ? targetTabId : IS_Portal.defaultTabId;
			
			if( useTab ) {
			    IS_Portal.controlTabs = new Control.Tabs("tabs",{
					linkSelector: "li", // selector bug ?
					activeClassName: "selected",
					beforeChange: function (oldTab,newTab){
						var tabNumber = newTab.id.substring("panel".length);
						IS_Portal.changeActiveTab( $("tab"+tabNumber ),!oldTab );
                        window.location.href = "#tabId=" + tabNumber;
					},
					defaultTab: IS_Portal.tabs[targetTabId].tab
				});
                Event.observe(window, "hashchange" , function(){
                    var activeTabId = IS_Portal.controlTabs.activeContainer.id;
                    activeTabId = "tab" + activeTabId.substring("panel".length);
                    var targetTabId = "tab" + is_getParameterFromHash("tabId");
                    
                    if(IS_Portal.tabs[targetTabId] && (activeTabId != targetTabId)){
                        IS_Portal.controlTabs.setActiveTab( IS_Portal.tabs[targetTabId].tab );
                    }
                }, false);
			}
			
			//Holiday information
			IS_Holiday = new IS_Widget.Calendar.iCalendar(hostPrefix + "/holidaysrv");
			IS_Holiday.noProxy = true;
			IS_Holiday.load({asynchronous:false});
			freshDays = IS_Portal.getFreshDays(IS_Portal.freshDays);
	
			var goHome = $("portal-go-home");
			if(goHome){
				var topPageDiv = document.createElement("div");
				topPageDiv.className = "command toppage";

				topPageDiv.appendChild(document.createTextNode(IS_R.lb_toTopPage));
				goHome.appendChild(topPageDiv);
				if(goHome.parentNode && topPageDiv.offsetWidth)
					goHome.parentNode.style.width = (parseInt(topPageDiv.offsetWidth) + (Browser.isIE ? 0 : 16)) + "px" ;
				Event.observe(goHome, "click" , IS_Portal.goHome, false);
			}
			//Show ranking in command bar
			
			if($("messageIcon")) $("messageIcon").title = IS_R.lb_messageConsole;
			
			for(var num = 0; num < widgetConfList.length; num++){
				if(!isTabView && !buildTargetTabIds.contains(widgetConfList[num].tabId))
					continue;
				var isBuild = (widgetConfList[num].tabId == IS_Portal.currentTabId.substring(3))? true:false;
				var id = "tab"+widgetConfList[num].tabId;
				if(widgetConfList[num].staticPanel){
					
					var adjustToWindowHeight = widgetConfList[num].adjustToWindowHeight;
					var widgets = widgetConfList[num].staticPanel;
					buildStaticPanel(id, widgets, isBuild);
				}
				if(!widgetConfList[num].disabledDynamicPanel && widgetConfList[num].dynamicPanel){
					var widgets = widgetConfList[num].dynamicPanel;
					buildDynamicPanel(id, widgets, isBuild);
				}
				if(!useTab) break;
			}
			
			if(!isTabView)
			  IS_Portal.CommandBar.init();
			
			var loadWidgets = IS_Portal.getLoadWidgets(targetTabId);
			
			// Add commandBar gadgets to loadWidgets
            for(var id in IS_Portal.CommandBar.commandbarWidgets){
                var commandbarWidget = IS_Portal.CommandBar.commandbarWidgets[id];
                if(typeof commandbarWidget == "function") continue;
                
                var container = $("s_"+commandbarWidget.id);
                if(container && !commandbarWidget.isBuilt){
                    commandbarWidget.build();
                    container.appendChild(commandbarWidget.elm_widget);
                    loadWidgets.push(commandbarWidget);
                }
            }
			
			if(loadWidgets.length > 0) {
				var eventTargetList = loadWidgets.collect( function( loadWidget ) {
					return {type:"loadComplete", id:loadWidget.id}
				});
				IS_EventDispatcher.addComplexListener( eventTargetList,function() {
					console.log("Sitemap is auto loaded on startup.");
					new IS_SidePanel.SiteMap();
					IS_Portal.adjustStaticWidgetHeight();
				},null,true);
			}
			
			setTimeout(loadContents, 1);
			
			if(IS_Portal.lastSaveFailed)
				msg.warn(IS_R.ms_lastlogoutSavingfailure);
			
			//Check new messages
			//This line should be here as IS_Portal.msgLastViewTime is needed
			if(IS_Widget.Message)
				IS_Widget.Message.checkNewMsgRepeat();
			
			if(fixedPortalHeader) 
				IS_Portal.adjustPanelHeight(null);
		}finally{
			//refs#3864 stop indicator anyway when widgets are end of load.
			IS_Portal.endIndicator();
			IS_Portal.tabs[IS_Portal.currentTabId].isBuilt = true;
			
			if(IS_Portal.isFirstLogin)
			    IS_GuidanceInstance = new IS_Guidance();
			
			if(window["IS_Notification"])
			    IS_Notification = new IS_Notification();
			
			IS_EventDispatcher.newEvent("portalLoaded");
		}
	}
	
	function setTabId(tabId, widgetConf) {
		if(/MultiRssReader/.test(widgetConf.type)){
			var children = widgetConf.property.children;
			if(children && children.length > 0){
				// Attach tub ID to Multi id; Multi is the type of working with menu
				widgetConf.id = tabId+"_"+widgetConf.id;
			}
		}
	}
	
	function getConfiguration(response) {
		var widgetConfs = eval("(" + response.responseText + ")");
		for(var i in widgetConfs){
			var typeConf = widgetConfs[i];
			if(typeof typeConf == "function") continue;
			if(typeConf) {
				for(var i in typeConf.WidgetPref){
					if(typeof typeConf.WidgetPref[i] == "function") continue;
					var datatype = typeConf.WidgetPref[i].datatype;
					if(datatype && datatype == "json"){
						typeConf.WidgetPref[i].value = eval("(" + typeConf.WidgetPref[i].content + ")");
					}
				}
			}
		}
		IS_WidgetConfiguration = widgetConfs;
	}

	function loadContents(){
        // load commandbar widgets
        var commandbarWidgets = IS_Portal.CommandBar.commandbarWidgets;
        for(var id in commandbarWidgets){
            var commandbarWidget = commandbarWidgets[id];
            if(!commandbarWidget.loadContents || typeof commandbarWidget == "function") continue;
            
            commandbarWidget.loadContents();
        }
        
	    for(id in IS_Portal.widgetLists[IS_Portal.currentTabId]){
			var widget = IS_Portal.getWidget(id);
			if(widget.loadContents && !widget.isSubWidget && !IS_Portal.CommandBar.isCommandBarWidget(widget)) {
				widget.loadContents();
			}
		}
	}
}
IS_Portal.getLoadWidgets = function(tabId, isAllReload){
	var loadWidgets = [];
	for(id in IS_Portal.widgetLists[tabId]){
		var widget = IS_Portal.getWidget(id, tabId);
		if(widget && !(widget instanceof Function) && !widget.isSubWidget && !IS_Portal.CommandBar.isCommandBarWidget(widget)){
			if(!widget.isBuilt){
				if(widget.panelType == "StaticPanel"){
					var container = $("s_"+widget.id);
					if(container){
						widget.build();
						container.appendChild(widget.elm_widget);
					}
				}else{
					widget.build();
					var columnObj = IS_Portal.columnsObjs[tabId]["col_dp_" + widget.column];
					if(!columnObj) {
						columnObj = IS_Portal.columnsObjs[tabId]["col_dp_" + IS_Portal.tabs[tabId].numCol];
						widget.column = IS_Portal.tabs[tabId].numCol;
					}
					var end = $("columns"+tabId.substring(3)+"_end_" + widget.column);
					if(end) {
						var siblingId = widget.widgetConf.siblingId;
						var sibling = $( siblingId ) || $( IS_Portal.currentTabId +"_" +siblingId );
						if( !siblingId ) {
							Element.insert( columnObj,{ top: widget.elm_widget } );
						} else if( sibling ) {
							Element.insert( sibling, { after: widget.elm_widget } );
						} else {
							columnObj.insertBefore(widget.elm_widget,end );
						}
					} else {
						columnObj.appendChild(widget.elm_widget);
					}
				}
				if(widget.isBuilt){
					loadWidgets.push(widget);
				}
			}else {
				if(!widget.isSuccess && !widget.isLoading)
					loadWidgets.push(widget);
				
				if(isAllReload || widget.onTabChangeReload){
					loadWidgets.push(widget);
					widget.onTabChangeReload = false;
				} else if (widget.onTabChangeAdjustIFrameHeight) {
					IS_Portal.adjustGadgetHeight( widget );
					delete widget.onTabChangeAdjustIFrameHeight;
				}
			}
		}
	}
	return loadWidgets;
}
IS_Portal.buildContents = function( tabId , isAllReload){
	var tabObj = IS_Portal.tabs[tabId];
	if( !tabObj.isColumnBuilt && !tabObj.disabledDynamicPanel) return;
	if( tabObj.isBuilding ) return;
	tabObj.isBuilding = true;
	
	var loadWidgets = IS_Portal.getLoadWidgets(tabId, isAllReload);
	
	if(loadWidgets.length > 0) {
		var eventTargetList = loadWidgets.collect( function( loadWidget ) {
				return {type:"loadComplete", id:loadWidget.id}
			});
		IS_EventDispatcher.addComplexListener( eventTargetList,function() {
				IS_EventDispatcher.newEvent("tabLoadCompleted",tabId );
				IS_Portal.tabs[tabId].isBuilding = false;
				IS_Portal.adjustStaticWidgetHeight();
		},null,true);
		
		for(var i = 0; i < loadWidgets.length; i++) {
			var widget = loadWidgets[i];
			setTimeout(widget.loadContents.bind(widget), 10);
		}
		
		IS_Portal.adjustSiteMenuHeight(null);
		IS_Portal.adjustIS_PortalStyle();
	} else {
		IS_EventDispatcher.newEvent("tabLoadCompleted",tabId );
		tabObj.isBuilding = false;
	}
	tabObj.isBuilt = true;
}
IS_Portal.buildAllTabsContents = function(){
	if(IS_Portal.isBuildingAllTabs) return;
	IS_Portal.isBuildingAllTabs = true;
	for(var i in IS_Portal.tabs){
		var tab = IS_Portal.tabs[i];
		if(typeof tab == "function") continue;
		var tabId = tab.id;
		var loadWidgets = IS_Portal.getLoadWidgets(tabId, true);
		
		var eventTargetList = loadWidgets.collect( function( loadWidget ) {
			return {type:"loadComplete", id:loadWidget.id}
		});
		IS_EventDispatcher.addComplexListener( eventTargetList,function() {
				IS_EventDispatcher.newEvent("tabLoadCompleted",this );
			}.bind(tabId),null,true);
		
		tab.isBuilt = true;
		for(var i = 0; i < loadWidgets.length; i++) {
			var widget = loadWidgets[i];
			IS_Portal.loadWidgetQueue[widget.id] = widget;
		}
	}
	IS_Portal.loadWidgetCount = $H( IS_Portal.loadWidgetQueue ).size();
	IS_Portal.loadingWidgetCount = 0;
	IS_Portal.completeWidgetCount = 0;
	IS_Portal.loadStartTabIds = {};
	$("tabsRefresh").style.display = "none";
	$("tabsRefreshStop").style.display = "";
	
	IS_Portal.displayMsgBar("tabReloadProgress", "<table style='width:100%'><tr><td width='17'>" 
		+ "<div id='tabLoadStopIcon'></div>"
		+ "</td><td>"
		+ "<div id='tabLoadProgress'><div id='tabLoadProgressBar'>"
		+ "<div id='tabLoadProgressCount'><span id='completeWidgetCount'>0</span>/" + IS_Portal.loadWidgetCount + "</div>"
		+ "</div></div>"
		+ "</td></tr></table>"
	);
	Event.observe($("tabLoadStopIcon"),"click",IS_Portal.stopLoadWidgets );
	IS_Portal.processLoadWidget();
	IS_Portal.adjustSiteMenuHeight(null);
	IS_Portal.adjustIS_PortalStyle();
}
IS_Portal.stopLoadWidgets = function() {
	IS_Portal.loadWidgetQueue = {};
	
	for (var i in IS_Portal.tabs) {
		var tab = IS_Portal.tabs[i];
		if (typeof tab == "function") continue;
		
		IS_EventDispatcher.newEvent("tabLoadCompleted",tab.id );
	}
	setTimeout( IS_Portal.endBuildAllTabsContents,500 );
}
IS_Portal.processLoadWidget = function() {
	var widget = (function(){
		for (var i in IS_Portal.loadWidgetQueue) {
			if(typeof IS_Portal.loadWidgetQueue[i] != "function")
				return IS_Portal.loadWidgetQueue[i];
		}
	}());
	if( !widget ) return;
	delete IS_Portal.loadWidgetQueue[widget.id];
	
	var loadCompleteListener = function( widget ){
		if(IS_Portal.loadWidgetQueue[widget.id])
			delete IS_Portal.loadWidgetQueue[widget.id];
		
		IS_Portal.loadingWidgetCount--;
		IS_Portal.completeWidgetCount++;
		IS_Portal.processLoadWidget();
		
		if( !$("msgBar_tabReloadProgress" ) ) return;
		
		var progress = IS_Portal.completeWidgetCount/IS_Portal.loadWidgetCount;
		$("tabLoadProgressBar").style.width = Math.round( progress *100) + "%";
		$("completeWidgetCount").innerHTML = IS_Portal.completeWidgetCount;
		
		if( progress == 1 && $("msgBar_tabReloadProgress" )) 
			setTimeout( IS_Portal.endBuildAllTabsContents,500 );
		
		if(/^g_/.test( widget.widgetType ))
			widget.onTabChangeAdjustIFrameHeight = true;
	}.bind( null,widget );
	widget.addLoadCompleteListener( loadCompleteListener,true );
	
	IS_Portal.loadingWidgetCount++;
	if (!IS_Portal.loadStartTabIds[widget.tabId]) {
		IS_Portal.loadStartTabIds[widget.tabId] = true;
		IS_EventDispatcher.newEvent("tabLoadStart", widget.tabId);
	}
	
	if (!IS_Portal.tabs[widget.tabId]) {
		loadCompleteListener(widget);
	}else{
		setTimeout(widget.loadContents.bind(widget), 10);
	}
	
	var loadingLimit = 4;
	if( IS_Portal.loadingWidgetCount < loadingLimit )
		IS_Portal.processLoadWidget();
}
IS_Portal.endBuildAllTabsContents = function(){
	IS_Portal.isBuildingAllTabs = false;
	IS_Portal.unDisplayMsgBar("tabReloadProgress");
	
	$("tabsRefresh").style.display = "";
	$("tabsRefreshStop").style.display = "none";
}
	
IS_WidgetsContainer.adjustColumnWidth = function( tabId, columnsWidth, isInitialize ) {
	var adjustTabId = (/^tab/.test(tabId))? tabId : IS_Portal.currentTabId;
	var container = $('panels');
	var width = container.offsetWidth-6;
	if(width < 0){
		return;
	}
	var columns = $("columns"+adjustTabId.substring(3)).childNodes;
	var numCol = IS_Portal.tabs[adjustTabId].numCol;
	var isClear = false;	// Flag to judge whether information about width is cleared
	if(!columnsWidth || columnsWidth.length != numCol){
		isClear = true;
		IS_Portal.tabs[adjustTabId].columnsWidth = new Array();
	}
	
	if(columns.length == 1){
		columns[0].style.width = "100%";
		isClear = true;
	}else{
		var sumWidth = 0;
		var loopCount = 0;
		for (var i=0; i < columns.length; i++ ) {
			if(!columns[i] || columns[i].nodeType != 1 || columns[i].className != "column") continue;
			if(isClear){
				var _coefficient = (Browser.isSafari) ? 100 : 99.5;
				var width = ((_coefficient - (numCol-1))/numCol) + "%";
				columns[i].style.width = width;
				IS_Portal.tabs[adjustTabId].columnsWidth.push(width);
			}else{
				if(i == columns.length-1){
					columns[i].style.width = (100 - sumWidth) + "%";
				}else if(columnsWidth[loopCount]){
					columns[i].style.width = columnsWidth[loopCount];
					sumWidth += parseFloat(columnsWidth[loopCount])+1;
				}
			}
			loopCount++;
		}
	}

	if(isClear){
		//Send to Server
		if(!isInitialize){
			IS_Widget.setTabPreferenceCommand(adjustTabId, "columnsWidth", Object.toJSON(IS_Portal.tabs[adjustTabId].columnsWidth));
		}
	}
}

/**
 * Adjust width between colums
 */
IS_WidgetsContainer.adjustColumns = {
	start : function(adjustDiv, e){
		IS_Portal.showDragOverlay(Element.getStyle(adjustDiv, "cursor"));
		IS_WidgetsContainer.adjustColumns.isDragging = true;
		
		var targetEl1 = adjustDiv.previousSibling;
		var targetEl2 = adjustDiv.nextSibling;
		
		IS_WidgetsContainer.adjustColumns.targetEl1 = targetEl1;
		IS_WidgetsContainer.adjustColumns.targetEl2 = targetEl2;
		IS_WidgetsContainer.adjustColumns.totalWidth = targetEl1.offsetWidth + targetEl2.offsetWidth;
		IS_WidgetsContainer.adjustColumns.targetEl1_offsetWidth = targetEl1.offsetWidth;
		
		IS_WidgetsContainer.adjustColumns.startX = Event.pointerX(e);
		
		Event.observe(document, "mousemove", IS_WidgetsContainer.adjustColumns.move, false);
		Event.observe(document, "mouseup", IS_WidgetsContainer.adjustColumns.end, false);
		
		// Prevent event from being passed to upper level
		Event.stop(e);
	},
	move : function(e){
		if(IS_WidgetsContainer.adjustColumns.isChanging) return;
		
		IS_WidgetsContainer.adjustColumns.moved = true;
		
		// effect
		if(IS_WidgetsContainer.adjustColumns.timer){
			clearTimeout(IS_WidgetsContainer.adjustColumns.timer);
		}
		
		IS_WidgetsContainer.adjustColumns.endX = Event.pointerX(e);
		IS_WidgetsContainer.adjustColumns.changeWidth();
		// Prevent event from being passed to upper level
		Event.stop(e);
	},
	end : function(e){
		if(IS_WidgetsContainer.adjustColumns.timer){
			clearTimeout(IS_WidgetsContainer.adjustColumns.timer);
		}
		IS_Portal.hideDragOverlay();
		Event.stopObserving(document, "mousemove", IS_WidgetsContainer.adjustColumns.move, false);
		Event.stopObserving(document, "mouseup", IS_WidgetsContainer.adjustColumns.end, false);
		IS_WidgetsContainer.adjustColumns.isDragging = false;
		
		if(!IS_WidgetsContainer.adjustColumns.moved)
		    return;
		
		IS_WidgetsContainer.adjustColumns.moved = false;
		
		// Change to '%'
		var targetEl1 = IS_WidgetsContainer.adjustColumns.targetEl1;
		var targetEl2 = IS_WidgetsContainer.adjustColumns.targetEl2;
		var parentWidth = IS_WidgetsContainer.adjustColumns.parentWidth = (!Browser.isIE)? targetEl1.parentNode.offsetWidth : $("dynamic-panel" + IS_Portal.currentTabId.substring(3)).parentNode.offsetWidth-2;
		
		var numCol = IS_Portal.tabs[IS_Portal.currentTabId].numCol;
		
		var p = ( targetEl1.offsetWidth / parentWidth ) * 100;
		targetEl1.style.width = p + "%";
		
		var columnsWidth = [];
		var columns = targetEl1.parentNode.childNodes;
		var sumWidth = 0;
		var targetEl2colnum = targetEl2.getAttribute("colnum");
		for(var i=0;i<columns.length;i++){
			if(columns[i].className != "column") continue;
			if(columns[i].getAttribute("colnum") == targetEl2colnum){
				columnsWidth.push(0);
				continue;
			}
			columnsWidth.push(columns[i].style.width);
		}
		targetEl2.style.width = ( targetEl2.offsetWidth / parentWidth ) * 100 + "%";
		columnsWidth[parseInt(targetEl2colnum)-1] = targetEl2.style.width;
		IS_Portal.tabs[IS_Portal.currentTabId].columnsWidth = columnsWidth;
		
		IS_Portal.adjustGadgetHeight();
		
		IS_WidgetsContainer.adjustColumns.hideAdjustDivs(targetEl1.parentNode);
		IS_Portal.widgetDisplayUpdated();

		//Send to Server
		IS_Widget.setTabPreferenceCommand(IS_Portal.currentTabId, "columnsWidth", Object.toJSON(IS_Portal.tabs[IS_Portal.currentTabId].columnsWidth));
		
		IS_EventDispatcher.newEvent("adjustedColumnWidth");
        IS_Widget.adjustDescWidth();
	},
	changeWidth : function(e){
		IS_WidgetsContainer.adjustColumns.isChanging = true;
		
		var targetEl1 = IS_WidgetsContainer.adjustColumns.targetEl1;
		var targetEl2 = IS_WidgetsContainer.adjustColumns.targetEl2;
		var totalWidth = IS_WidgetsContainer.adjustColumns.totalWidth;
		var startx = IS_WidgetsContainer.adjustColumns.startX;
		var endx = IS_WidgetsContainer.adjustColumns.endX;
		var startOffsetWidth = IS_WidgetsContainer.adjustColumns.targetEl1_offsetWidth;
		
		var setWidth = (endx - startx);
		var targetEl1Width;
		if(startOffsetWidth + setWidth < totalWidth-10 && startOffsetWidth + setWidth > 0){
			targetEl1Width = (startOffsetWidth + setWidth);
		}else{
			var wid = (startOffsetWidth + setWidth > 0)? (totalWidth-10) : 10;
			targetEl1Width = wid;
		}
		targetEl1.style.width = targetEl1Width + 'px';
		
		var setWidth2 = (totalWidth - targetEl1Width);
		if(totalWidth - setWidth2 > 0){
			targetEl2.style.width = setWidth2 - 1 + 'px';
		}
		
		IS_WidgetsContainer.adjustColumns.isChanging = false;
	},
	showAdjustDivs : function(columns, e){
		var childs = columns.childNodes;
		for(var i=0;i<childs.length;i++){
			if(childs[i].className == "adjustBarOut") childs[i].className = "adjustBarOver";
		}
	},
	hideAdjustDivs : function(columns, e){
		if(IS_WidgetsContainer.adjustColumns.isDragging) return;
		var childs = columns.childNodes;
		for(var i=0;i<childs.length;i++){
			if(childs[i].className == "adjustBarOver") childs[i].className = "adjustBarOut";
		}
	}
}

IS_Portal.rebuilding = new Object();
IS_WidgetsContainer.rebuildColumns = function( tabId, numCol, columnsWidth, isReset, isInitialize ) {
	if(IS_Portal.tabs[tabId].disabledDynamicPanel
	   || (!isReset && (IS_Portal.tabs[tabId].numCol == numCol || IS_Portal.rebuilding[tabId] == true))){
		IS_Portal.rebuilding[tabId] = false;
		return;
	}
	var isFirstLoading = IS_Portal.tabs[tabId].numCol == 0;
	IS_Portal.rebuilding[tabId] = true;
	var columns = $("columns"+tabId.substring(3));
	if (numCol > maxColumnNum){
		numCol = maxColumnNum;
		IS_Widget.setTabPreferenceCommand(IS_Portal.currentTabId || tabId, "numCol", numCol);
	}
	if(IS_Portal.tabs[tabId].numCol < numCol) {
		for(var i = parseInt(IS_Portal.tabs[tabId].numCol) + 1; i <= numCol; i++){
			var div = document.createElement("div");
			div.style.minHeight = "1px";
			div.className = "column";
			div.setAttribute("colNum", i);
			
			var divEnd = document.createElement("div");
			divEnd.setAttribute("id",columns.id + "_end_" + i);
			divEnd.dataObj={id:columns.id + "_end" + i};
			div.appendChild(divEnd);
			columns.appendChild(div);
			IS_Portal.columnsObjs[tabId]["col_dp_" + i] = div;
			
			// adjust bar
			if(i != 1){
				var adjustDiv = document.createElement("div");
				div.parentNode.insertBefore(adjustDiv, div);
				adjustDiv.className = "adjustBarOut";
				IS_Event.observe(adjustDiv, 'mousedown', IS_WidgetsContainer.adjustColumns.start.bind(adjustDiv, adjustDiv), false, IS_Portal.currentTabId);
				IS_Event.observe(adjustDiv, 'mouseover', IS_WidgetsContainer.adjustColumns.showAdjustDivs.bind(adjustDiv, columns), false, IS_Portal.currentTabId);
				IS_Event.observe(adjustDiv, 'mouseout', IS_WidgetsContainer.adjustColumns.hideAdjustDivs.bind(adjustDiv, columns), false, IS_Portal.currentTabId);
			}
		}
	} else {
		var column = columns.childNodes;
		var columnsArray = new Array();
		for(var i=0;i<column.length;i++){
			if(column[i].className == "column")
				columnsArray.push(column[i]);
		}
		var lastColumn = columnsArray[numCol - 1];
		
		var lastColumnEnd = $(columns.id + "_end_" + numCol);
		if(lastColumn) {
			for(var i = numCol; i < columnsArray.length; i++) {
 				while(0 < columnsArray[i].childNodes.length) {
					try{
						if(columnsArray[i].childNodes.length <= 1) break;
						var widgetDiv = columnsArray[i].childNodes[0];
						var id = widgetDiv.id;
						var widget = IS_Portal.getWidget(id, tabId);
						lastColumn.insertBefore(widgetDiv, lastColumnEnd);
						widget.widgetConf.column = numCol;
						IS_Widget.setWidgetLocationCommand(widget);
						
						IS_EventDispatcher.newEvent("moveWidget",widget.id );
					}catch(e){

						msg.warn(IS_R.getResource(IS_R.lb_changeColnumFailure, [(i + 1), getText(e)]));
					}
				}
			}
			
			while(numCol + (numCol-1) < column.length) {
				columns.removeChild(column[numCol + (numCol-1)]);
			}
		}
	}
	
	IS_Portal.tabs[tabId].numCol = numCol;
	IS_Portal.tabs[tabId].isColumnBuilt = true;
	IS_WidgetsContainer.adjustColumnWidth(tabId, columnsWidth, isInitialize);
	IS_Portal.rebuilding[tabId] = false;
	if(!isFirstLoading){//Not adjust in case of first loading
		IS_Portal.widgetDisplayUpdated();
		IS_Widget.adjustDescWidth();
		IS_Widget.WidgetHeader.adjustHeaderWidth();
		IS_Portal.adjustGadgetHeight();
		
		IS_EventDispatcher.newEvent("adjustedColumnWidth");
	}
}

IS_WidgetsContainer.addWidget = function (tabId, widgetConf, isBuild, appendFunc, subWidgetConfList) {
	if(!IS_Portal.canAddWidget(tabId)) return;
	if(!widgetConf.column) widgetConf.column = 1; 
	if( IS_Portal.tabs[tabId].numCol < widgetConf.column ){
		widgetConf.column = IS_Portal.tabs[tabId].numCol;
	}
	var columnObj = IS_Portal.columnsObjs[tabId]["col_dp_" + widgetConf.column];
	var widget = new IS_Widget(true, widgetConf);
	widget.tabId = tabId;
	IS_Portal.addWidget(widget, tabId);
	
	if(subWidgetConfList){
		for (var i = 0; i < subWidgetConfList.length; i++) {
			if(!subWidgetConfList[i]) continue;
			
			var subWidgetConf = subWidgetConfList[i];
			var child = new IS_Widget(true, subWidgetConf, widget);
			child.tabId = tabId;
			child.isSubWidget = true;
			IS_Portal.addWidget(child, tabId);
			IS_Portal.addSubWidget(widget.id, child.id, tabId);
		}
		
		IS_Widget.addMultiWidgetCommand(widget);
	}else{
		IS_Widget.addWidgetCommand(widget);
	}
	
	if(tabId == IS_Portal.currentTabId || ( isBuild && !Browser.isSafari1 ) ){
		widget.build();
		if(appendFunc){
			appendFunc(widget);
		}else{
		    if (columnObj.hasChildNodes()) {
		    	columnObj.insertBefore(widget.elm_widget, columnObj.firstChild);
		    }else {
		    	columnObj.appendChild(widget.elm_widget);
		    }
	    }
		widget.loadContents();
    }
	
	return widget;
}

IS_WidgetsContainer.WidgetConfiguration = {
	getConfigurationJSONObject: function(type, id, column, title, title_url, properties, refreshInterval){
		var widgetConf = new Object();
		widgetConf.id = id;
		widgetConf.column = column;
		widgetConf.type = type;
		widgetConf.title = title;
		if(refreshInterval)
		    widgetConf.refreshInterval = refreshInterval;
		if(title_url)
			widgetConf.href = title_url;
		
		if(properties)
			widgetConf.property = new Object();
		
	    if(properties){
			for(i in properties){
				if(properties[i] && !(properties[i] instanceof Function)){
					try{
						if( !( type.indexOf('g_') == 0 && i=='url') )
						  widgetConf.property[i] = properties[i];
					}catch(e){}
				}
			}
		}

		return widgetConf;
	},
	getFeedConfigurationJSONObject: function(type, id, title, title_url, isChecked, properties){
		var widgetConf = new Object();
		widgetConf.id = id;
		widgetConf.type = (type) ? type : "RssReader";
		widgetConf.title = title;
		if(title_url)
			widgetConf.href = title_url;
		
		if(isChecked != ""){
			widgetConf.isChecked = isChecked;
		}
		
		if(properties)
			widgetConf.property = new Object();
		
		for(i in properties){
			if(properties[i] && !(properties[i] instanceof Function)){
				try{
					widgetConf.property[i] = properties[i];
				}catch(e){}
			}
		}

		return widgetConf;
	}
};

IS_WidgetsContainer.getWidgetListJSONString = function( makePanels ){
	var jsonStr = "{";
	for( var count=0; count < IS_Portal.tabList.length; count++){
		var id = IS_Portal.tabList[count].id;
		var widgetList = IS_Portal.widgetLists[id];
		if(widgetList) {
			if(0 < count) jsonStr += ',';
			
			jsonStr += count+":";
			jsonStr += IS_WidgetsContainer.getTabJSONString( IS_Portal.tabList[count], makePanels );
		}
	}
	jsonStr += '}';
	return jsonStr;
}

IS_WidgetsContainer.getTabJSONString = function( tab, makePanels ){
	var jsonStr = "{";
	var id = tab.id;
	var widgetList = IS_Portal.widgetLists[id];
	if(widgetList) {
		var jsonContent = new Object();
		if(makePanels){
			jsonContent = {"StaticPanel":{isFirst:true, json:" "}, "DynamicPanel":{isFirst:true, json:" "}};
			for(var i in widgetList){
				if(!widgetList[i] || typeof widgetList[i] != 'object') continue;
				var panel = jsonContent[widgetList[i].panelType];
				if(panel){
					var widgetConf = widgetList[i].widgetConf;
					widgetConf.siblingId = getSiblingId(widgetList[i].elm_widget);
					if(widgetConf) {
						if(!panel.isFirst) panel.json += ',';
						else panel.isFirst = false;
						var confId = widgetConf.id;
						widgetConf.id = IS_Portal.getTrueId(widgetConf.id, widgetList[i].widgetType);
						panel.json += '"' + widgetConf.id + '":';
						panel.json += Object.toJSON(widgetConf);
						widgetConf.id = confId;
					}
				}
			}
		}

		var tabNumber = IS_Portal.tabs[id].tabNumber;
		var name = Object.toJSON(IS_Portal.tabs[id].name);
		name = name.substring(1, name.length-1);	// Remove "" at both ends
		var type = IS_Portal.tabs[id].type;
		jsonStr += "numCol:" + IS_Portal.tabs[id].numCol;
		
		if(IS_Portal.tabs[id].columnsWidth)
			jsonStr += ', columnsWidth:' + Object.toJSON(IS_Portal.tabs[id].columnsWidth) + '';
		
		jsonStr += ', tabId:"'+id.substring(3)+'", tabName:"'+name+'", tabType:"'+type+'", tabNumber:"'+tabNumber+'",';
		if(IS_Portal.tabs[id].logoffDateTime)
			jsonStr += 'logoffDateTime:"' + IS_Portal.tabs[id].logoffDateTime + '",';
		if(IS_Portal.tabs[id].fontSize)
			jsonStr += 'fontSize:"' + IS_Portal.tabs[id].fontSize + '",';
		if(IS_Portal.tabs[id].thema)
			jsonStr += 'thema:"' + IS_Portal.tabs[id].thema + '",';
		jsonStr += "panel: {";
		var isFirst = true;
		for(var i in jsonContent){
			if(jsonContent[i].json) {
				if(!isFirst) jsonStr += ',';
				else isFirst = false;
				jsonStr += i + ':{' + jsonContent[i].json + '}';
			}
		}
		jsonStr += '}}';
	}
	
	return jsonStr;
	
	function getSiblingId(div){
		if(widgetOrderReverse) {
			if(div && div.nextSibling && div.nextSibling.firstChild) {
				return IS_Portal.getTrueId(div.nextSibling.firstChild.firstChild.id);
			}
		} else {
			if(div && div.previousSibling) {
				return IS_Portal.getTrueId(div.previousSibling.id);
			}
		}
	}
}
IS_WidgetsContainer.getWidgetJSONString = function( tab, makePanels ){
	var jsonStr = '{ 0:';
	jsonStr += IS_WidgetsContainer.getTabJSONString( tab, makePanels );
	jsonStr += '}';
	return jsonStr;
}

/**
	Remove "tab[tubid]_" with id firstly attached
*/
IS_Portal.getTrueId = function( id, widgetType ){
	if(!id.replace)return;
	return id.replace(/^tab[0-9]+_/, "");
	/*var index = id.indexOf("p_");
	if(/MultiRssReader/.test(widgetType) && 0<index){
		return id.substring( index );
	}else{
		return id;
	}*/
}
/**
 * <p>Judge whether widget and menuItem have the same widget type</p>
 * <p>Gadget is judged to have the same widget type
 * when widget.widgetType equals Type.substring(2)</p>
 * <p>menuItem with MultiRssReader set is judged to have the same widget type
 * when widget.widgetType equals MultiRssReader or RssReader</p>
 * @param {Object} widget
 * @param {Object} menuItem
 * @return true if menuItem has the same widget type, or false if not
 */
IS_Portal.isMenuType = function( widget,menuItem ) {
	if( widget.widgetType == menuItem.type )
		return true;
	
	if(/MultiRssReader/.test( menuItem.type )&& /RssReader/.test( widget.widgetType ) )
		return true;
	
	if(/Gadget/.test( menuItem.type ) && widget.widgetType.indexOf("g_") == 0 &&
		menuItem.properties && menuItem.properties.url == widget.widgetType.substring(2) )
		return true;
	
	return false;
}

/**
 * Add widget to Portal
 * @param {Object} widgetId
 * @param {Object} tabId
 */
IS_Portal.addWidget = function(widget, tabId){
	if(!tabId) tabId = IS_Portal.currentTabId;
	var widgetId = IS_Portal.getTrueId(widget.id);
	
	IS_Portal.widgetLists[tabId][widgetId] = widget;
}

/**
 * Remove widget from portal
 * @param {Object} widgetId
 * @param {Object} tabId
 */
IS_Portal.removeWidget = function(_widgetId, tabId){
	if(!tabId) tabId = IS_Portal.currentTabId;
	var widgetId = (_widgetId.id)? IS_Portal.getTrueId(_widgetId.id) : IS_Portal.getTrueId(_widgetId);
	
	delete IS_Portal.widgetLists[tabId][widgetId];
}

/**
 * Get WidgetObject that is placed in portal
 */
IS_Portal.getWidget = function(_widgetId, tabId){
	if(/^previewWidget_\d+/.test( _widgetId ) && IS_SidePanel.previewWidgetList ) {
		return IS_SidePanel.previewWidgetList.find( function( widget ) {
			return widget.id == _widgetId;
		});
	}
	
	if(!tabId) tabId = IS_Portal.currentTabId;
	var widgetId = IS_Portal.getTrueId(_widgetId);
	
	var widget = IS_Portal.widgetLists[tabId][widgetId];
	return widget;
}

/**
 * Return sub Widget list of the specified WidgetId
 */
IS_Portal.getSubWidgetList = function(_widgetId, tabId){
	if(!tabId) tabId = IS_Portal.currentTabId;
	if(!IS_Portal.subWidgetMap[tabId]) IS_Portal.subWidgetMap[tabId] = {};
	
	var widgetId = IS_Portal.getTrueId(_widgetId);
	var subWidgets = [];
	var subWidgetIds = IS_Portal.subWidgetMap[tabId][widgetId];
	
	if (subWidgetIds) {
		for (var i = 0; i < subWidgetIds.length; i++) {
			var subWidget = IS_Portal.getWidget(subWidgetIds[i], tabId);
			if(subWidget) subWidgets.push(subWidget);
		}
		return subWidgets;
	}
	return [];
}

IS_Portal.findWidgetByType = function(type){
	var tabIds = $H(IS_Portal.tabs).keys();
	for(var i=0;i<tabIds.length;i++){
		var widget = $H(IS_Portal.widgetLists[tabIds[i]]).values().detect(function(w){
			return w.widgetType == type;
		})
		if(widget) return widget;
	}
}

/**
 * Move sub Widget list of the specified WidgetId to the selected tub
 * _widgetId: ID for Parent widget
 * fromTabId: ID for tub before moving
 * toTabId  : ID for tub after moving
 */
IS_Portal.replaceSubWidgetList = function(_widgetId, fromTabId, toTabId){
	if(fromTabId == toTabId) return;
	
	var widgetId = IS_Portal.getTrueId(_widgetId);
	var subWidgets = IS_Portal.getSubWidgetList(widgetId, fromTabId);
	
	var newParentWidget;
	for(var i=0;i<subWidgets.length;i++){
		newParentWidget = IS_Portal.getWidget(widgetId, toTabId);
		
		IS_Portal.removeSubWidget(
			IS_Portal.getWidget(widgetId, fromTabId),
			IS_Portal.getWidget(subWidgets[i].id, fromTabId),
			fromTabId);
		IS_Portal.addSubWidget(
			newParentWidget,
			IS_Portal.getWidget(subWidgets[i].id, fromTabId),
			toTabId);
		IS_Portal.removeWidget(
			subWidgets[i].id,
			fromTabId);
		
		subWidgets[i].tabId = toTabId;
		subWidgets[i].parent = newParentWidget;
		IS_Portal.addWidget(subWidgets[i], toTabId);
	}
}

/**
 * Add subWidget to the Widget that has the specified WidgetId
 */
IS_Portal.addSubWidget = function(_widget, _subWidget, tabId, isUnshift){
	if(!tabId) tabId = IS_Portal.currentTabId;
	
	var widgetId = IS_Portal.getTrueId((_widget.id)? _widget.id : _widget);
	var subWidgetId = IS_Portal.getTrueId((_subWidget.id)? _subWidget.id : _subWidget);
	
	if(!IS_Portal.subWidgetMap[tabId]) IS_Portal.subWidgetMap[tabId] = {};
	if(!IS_Portal.subWidgetMap[tabId][widgetId]) IS_Portal.subWidgetMap[tabId][widgetId] = [];
	
	if (!IS_Portal.subWidgetMap[tabId][widgetId].contains(subWidgetId)) {
		isUnshift? IS_Portal.subWidgetMap[tabId][widgetId].unshift(subWidgetId)
						: IS_Portal.subWidgetMap[tabId][widgetId].push(subWidgetId);
	}
	
	if (!IS_Portal.subWidgetIds.contains(subWidgetId)) {
		IS_Portal.subWidgetIds.push(subWidgetId);
	}
		
	IS_Portal.parentWidgetMap[subWidgetId] = widgetId;
	
	var widget = (_widget.id)? _widget : IS_Portal.getWidget(widgetId, tabId);
	var subWidget = (_subWidget.id)? _subWidget : IS_Portal.getWidget(subWidgetId, tabId);
	
	if(subWidget && widget) subWidget.parent = widget;
	
	if (IS_Portal.currentTabId != tabId && subWidget) {
		IS_Portal.addWidget(subWidget, tabId);
	}
	
	if(subWidget && widget ) subWidget.widgetConf.parentId = widget.id;
}

/**
 * Remove subWidget from the Widget that has the specified WidgetId
 */
IS_Portal.removeSubWidget = function(_widget, _subWidget, tabId){
	if(!tabId) tabId = IS_Portal.currentTabId;
	
	var widgetId = IS_Portal.getTrueId((_widget.id)? _widget.id : _widget);
	var subWidgetId = IS_Portal.getTrueId((_subWidget.id)? _subWidget.id : _subWidget);
	
	IS_Portal.subWidgetMap[tabId][widgetId].remove(subWidgetId);
	delete IS_Portal.subWidgetIds[subWidgetId];
	delete IS_Portal.parentWidgetMap[subWidgetId]
	
	var subWidget = (_subWidget.id)? _subWidget : IS_Portal.getWidget(subWidgetId, tabId);
	if(subWidget
		&& subWidget.parent
		&& IS_Portal.getTrueId(subWidget.parent.id) == widgetId) {
		subWidget.oldParent = subWidget.parent;
		subWidget.parent = null;
		if( subWidget.widgetConf )
			delete subWidget.widgetConf.parentId;
	}
	
	if(IS_Portal.currentTabId != tabId){
		IS_Portal.removeWidget(subWidget, tabId);
	}
}

/**
 * Return the value; whether the Widget that has specified WidgetId is subWidget or not
 * true: it is subWidget
 * false: it is not subWidget
 */
IS_Portal.isSubWidget = function(widgetId){
	return IS_Portal.subWidgetIds.include(widgetId);
}


IS_Portal.isTabLoading = function(){
	if( !Browser.isSafari1 ) 
		return false;
	
	var widgetList = IS_Portal.widgetLists[IS_Portal.currentTabId];
	for(i in widgetList){
		if( widgetList[i].isLoading) {
			if( Browser.isSafari1 /*|| ( !widgetList[i].isComplete && widgetList[i].panelType == "StaticPanel")*/)
				return true;
		}
	}
	return false;
}
