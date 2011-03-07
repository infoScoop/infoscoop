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


IS_SidePanel.SiteMap = IS_Class.create();

if( displaySideMenu ) {
	if( displaySideMenu == 'reference_top_menu' ) {
		IS_TreeMenu.types.sidemenu = IS_TreeMenu.types.topmenu;
	} else {
		IS_TreeMenu.types.sidemenu = new IS_TreeMenu("sidemenu");
		IS_TreeMenu.types.sidemenu.title = IS_R.lb_siteMap;
		//var alertSetting = is_getPropertyInt(sideMenuAlertSetting, 1);
		//IS_TreeMenu.alertSettings[sideMenuURL] =  ( alertSetting < 3 ) ? alertSetting : 1;
	}
	
	IS_SidePanel.setMenu = function(url,a,b,c){
		IS_TreeMenu.types.sidemenu.setMenu( url,a,b,c,true );
	}
	IS_SidePanel.setServiceMenu = function(url, a, c, b){
		return IS_TreeMenu.types.sidemenu.setMenu( url, a,b,c,false );
	}
	
	IS_SidePanel.SiteMap.menuItemList = IS_TreeMenu.types.sidemenu.menuItemList;
	IS_SidePanel.SiteMap.topMenuIdList = IS_TreeMenu.types.sidemenu.topMenuIdList;
	IS_SidePanel.SiteMap.menuItemTreeMap = IS_TreeMenu.types.sidemenu.menuItemTreeMap;
	IS_SidePanel.SiteMap.serviceMenuMap = IS_TreeMenu.types.sidemenu.serviceMenuMap;
} else {
	IS_TreeMenu.types.sidemenu = IS_TreeMenu.types.topmenu;
}

IS_SidePanel.SiteMap.prototype.classDef = function () {
	
	var container = document.getElementById("portal-tree-menu");
	var openedMenuSet = [];
	var level = [];
	var proxy = proxyServerURL + "MakeMenu/";
	var self = this;
	var refreshButton;
	var showAll;
	var hideAll;
	var content;
	var loadingMessage;
	
	function replaceContent(element){
		content.innerHTML = "";
		content.appendChild(element);
	}
		
	this.initialize = function(synchronous) {
		if(!container) return;
		
		// fix #305
		//container.innerHTML = "";
		
		if(!displaySideMenu) {
			container.style.display = "none";
			return;
		}
		
		if(!IS_SidePanel.SiteMap.isInitialize) {
			refreshButton = document.createElement("div");
			refreshButton.className = "menuRefresh";
			container.appendChild( refreshButton );
			Event.observe(refreshButton,'click',IS_SidePanel.SiteMap.refreshTreeMenu,false,"_sidemenu" );
			
			showAll = document.createElement("div");
			showAll.className = "showAll";
			showAll.appendChild(document.createTextNode(IS_R.lb_deployAll));
			
			hideAll = document.createElement("div");
			hideAll.className = "hideAll";
			hideAll.appendChild(document.createTextNode(IS_R.lb_closeAll));
			hideAll.style.display = "none";
			
			container.appendChild(showAll);
			container.appendChild(hideAll);
			
			Event.observe(showAll, 'click', function() { attachShowHideEventHandler(true, content.firstChild ); },false,"_sidemenu");
			Event.observe(hideAll, 'click', function() { attachShowHideEventHandler(false, content.firstChild ); },false,"_sidemenu");
			
			content = document.createElement("div");
			container.appendChild(content);
		
			loadingMessage = document.createElement('div');
			loadingMessage.innerHTML = "Loading...";
			loadingMessage.style.clear = "both";
			loadingMessage.style.cssFloat = "left";
			content.appendChild(loadingMessage);
			
			IS_SidePanel.SiteMap.elements = {
				refreshButton : refreshButton,
				showAll: showAll,
				hideAll: hideAll,
				content: content,
				loadingMessage: loadingMessage
			}
			
			IS_SidePanel.SiteMap.isInitialize = true;
		} else {
			refreshButton = IS_SidePanel.SiteMap.elements.refreshButton;
			showAll = IS_SidePanel.SiteMap.elements.showAll;
			hideAll = IS_SidePanel.SiteMap.elements.hideAll;
			content = IS_SidePanel.SiteMap.elements.content;
			loadingMessage = IS_SidePanel.SiteMap.elements.loadingMessage;
		}
		
		function buildErrorMessage(msg){
			var errorMessageDiv = document.createElement('div');
			errorMessageDiv.style.fontSize = '90%';
			errorMessageDiv.style.color = 'red';
			errorMessageDiv.style.padding = '5px';
			errorMessageDiv.style.clear = "both";
			errorMessageDiv.innerHTML = msg;
			return errorMessageDiv;
		}
		
		var opt = {
		  asynchronous:!synchronous,
		  includeServiceMenu: synchronous,
		  onSuccess: function(response){ displayMenu(); },
		  on10408: function(t) {
			if(!IS_SidePanel.SiteMap.isSuccess)
				replaceContent(buildErrorMessage(IS_R.ms_menuLoadon10408));
		  },
		  on404: function(t) {
			  if(!IS_SidePanel.SiteMap.isSuccess)
			  	replaceContent(buildErrorMessage(IS_R.ms_menuNotFound));
		  },
		  onFailure: function(t) {
			  if(!IS_SidePanel.SiteMap.isSuccess)
			  	replaceContent(buildErrorMessage(IS_R.ms_menuLoadonFailure));
		  },
		  onException: function(t, e) {
			  if(!IS_SidePanel.SiteMap.isSuccess)
			  	replaceContent(buildErrorMessage(IS_R.ms_menuLoadonFailure));
		  }
		}
		
		if( !IS_TreeMenu.types.sidemenu.isSuccess ) {
			if( !IS_TreeMenu.types.sidemenu.loadMenu( opt ) ) {
				//Loading
				IS_EventDispatcher.addListener("loadMenuComplete",IS_TreeMenu.types.sidemenu.type,function() {
					if( IS_TreeMenu.types.sidemenu.isSuccess )
						this.initialize( synchronous );
				}.bind( this ),false,true );
			}
		} else {
			displayMenu();
		}
	}
	
	function displayMenu() {
		//var start = new Date();
		//var startTime = start.getSeconds() * 1000 + start.getMilliseconds();
		
		var treeMenuDiv = document.createElement("div");
		treeMenuDiv.style.clear = "both";

		var menuTop = document.createElement('div');
		menuTop.className = 'ygtvchildren';
		menuTop.id = 'ygtvc0';
		for(var i = 0; i < IS_TreeMenu.types.sidemenu.topMenuIdList.length; i++){
			var menuItem = IS_TreeMenu.types.sidemenu.menuItemList[IS_TreeMenu.types.sidemenu.topMenuIdList[i]];
			menuItem.depth = 0;
			if( i == IS_TreeMenu.types.sidemenu.topMenuIdList.length -1) menuItem.isLast = true;
			
			if( menuItem.serviceURL ) {
				menuTop.appendChild( getMenuService( menuItem ) );
			} else {
				menuTop.appendChild(buildMenuTree(menuItem));
			}
		}
	
		treeMenuDiv.appendChild(menuTop);
			
		//container.replaceChild(treeMenuDiv,loadingMessage);
		replaceContent(treeMenuDiv);
		
		//if( refreshMenu )
		//	Event.observe(refreshButton,'click',IS_SidePanel.SiteMap.refreshTreeMenu,false,"_sidemenu" )
		
		var div = document.createElement("div");
		div.style.visibility = "hidden";
		div.style.width = "100px";
		container.appendChild(div);

		//var end = new Date();
		//var endTime = end.getSeconds() * 1000 + end.getMilliseconds();
		//msg.debug("Tree init duration: " + (endTime - startTime));
		IS_SidePanel.SiteMap.isSuccess = true;
	}
	
	function attachShowHideEventHandler( doShow, treeMenuDiv ) {
		if( !IS_TreeMenu.types.sidemenu.isSuccess ) return;
		
		treeMenuDiv.style.display = 'none';
		treeMenuDiv.parentNode.appendChild(loadingMessage);
		var execAllShowHide = function(){
			for ( var i=0;i<IS_TreeMenu.types.sidemenu.menuItems.length;i++) {
				var menuItem = IS_TreeMenu.types.sidemenu.menuItems[i]
				var menuGroupId = "tg_" + menuItem.id;
				var node = $(menuGroupId);
				if (node) {
					var mgi = $("i_" + menuItem.id);
					if (doShow) {
						subMenuOpen(mgi, menuItem);
						showAll.style.display = "none";
						hideAll.style.display = "block";
					} else {
						subMenuClose(mgi);
						showAll.style.display = "block";
						hideAll.style.display = "none";
					}
				}
			}
			loadingMessage.parentNode.removeChild(loadingMessage);
			treeMenuDiv.style.display = 'block';
			
			IS_SidePanel.adjustPosition();
		}
		setTimeout(execAllShowHide,1);
	}
	

	/**
	 * Copy From SiteAggregationMenu.getMenuService
	 * 
	 * Get external menu service
	 * @param menuItem Top menu object
	 */
	
	// The content is changed to error message if the external service can not be loaded.
	function getMenuService(menuItem) {
		IS_TreeMenu.alertSettings[menuItem.serviceURL] = (typeof menuItem.alert != 'undefined') ? menuItem.alert : 1;
		var topLi = makeMenu(menuItem);
		topLi.id = "menuService_topLi_"+menuItem.id;
		topLoadingDiv = document.createElement("div");
		topLoadingDiv.appendChild(document.createTextNode("Loading..."));
		topLi.appendChild(topLoadingDiv);
		var opt = {
		  method: menuItem.serviceAuthType ? 'post':'get',
		  requestHeaders:["siteTopId",menuItem.id],
		  onSuccess: function(response) {
			displayServiceMenu();
		  },
		  on404: function(t) {
			  var topLi = $("menuService_topLi_"+menuItem.id);
			  if( !topLi ) return;
			  
			  topLi.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+
			  	IS_R.ms_menuNotFound + "</span>";
		  },
		  on10408: function( req,obj ) {
			  var topLi = $("menuService_topLi_"+menuItem.id);
			  if( !topLi ) return;
			  
			  topLi.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>" +
			  	IS_R.ms_menuLoadon10408 + "</span>";
		  },
		  onFailure: function(t) {
			  var topLi = $("menuService_topLi_"+menuItem.id);
			  if( !topLi ) return;
			  
			  topLi.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+
			  	IS_R.ms_menuLoadonFailure + "</span>";
		  },
		  onException: function(r, t){
			  var topLi = $("menuService_topLi_"+menuItem.id);
			  if( !topLi ) return;
			  
			  topLi.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>" +
			  	IS_R.ms_menuLoadonFailure + "</span>";
		  }
		};
		if(menuItem.serviceAuthType){
			var serviceAuthType = menuItem.serviceAuthType.split(' ')[0];
			opt.requestHeaders.push("authType");
			opt.requestHeaders.push(serviceAuthType);
			var serviceUidParamName = menuItem.serviceAuthType.split(' ')[1];
			if(serviceUidParamName){
				opt.requestHeaders.push("_authUidParamName");
				opt.requestHeaders.push(decodeURIComponent(serviceUidParamName));
			}
		}
		
		function displayServiceMenu() {
			var topLi = $("menuService_topLi_"+menuItem.id);
			if( !topLi ) return;
			
			var tempMenuItem = menuItem;
			var menuTopItems = menuItem.menuTopItems;
			var topContainer =  topLi.parentNode;
			if( !topContainer ) return;
			
			for(var i=0;i<menuTopItems.length;i++){
				var menuTopItem = menuTopItems[i];
				menuTopItem.isLast = tempMenuItem.isLast;
				menuTopItem.depth = tempMenuItem.depth;
			  	var newTopLi = buildMenuTree(menuTopItem);
				topContainer.insertBefore(newTopLi, topLi);
			}
			topContainer.removeChild(topLi);
			
			var index = IS_TreeMenu.types.sidemenu.topMenuIdList.indexOf(menuItem.id);
			if( index < 0 )
				return;
			
			var args = [index, 1];
			for(var i=0;i<menuTopItems.length;i++)
				args.push(menuTopItems[i].id);
			
			menuItem.owner.topMenuIdList.splice.apply( menuItem.owner.topMenuIdList, args);
		}
		
		if( !menuItem.isSuccess ) {
			if( !menuItem.loadServiceMenu( opt )) {
				IS_EventDispatcher.addListener("loadMenuComplete",menuItem.serviceURL,function() {
					displayServiceMenu();
				},false,true );
			}
		} else {
			setTimeout( displayServiceMenu,10 );
		}
		
		return topLi;
	}
	
	function buildMenuTree(menuItem){
		var li =  makeMenu(menuItem);
		var childList = menuItem.children;
		if(childList.length > 0){
			var childrenDiv = document.createElement('div');
			childrenDiv.id = "tg_" + menuItem.id;
			childrenDiv.className = 'ygtvchildren';
			
			for(var j = 0; j < childList.length;j++){
				var child = childList[j];
				if( j == childList.length - 1 ) child.isLast = true;
				child.isChildrenBuildSiteMap = false;
			}
			
			li.appendChild(childrenDiv);
		}
		return li;
	}
	
	
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
	
	function buildMenuTreeChild(menuItem){
		var childList = menuItem.children;
		if(childList){
			var childrenDiv = document.createElement('div');
			childrenDiv.className = 'ygtvchildren';
			
			for(var j = 0; j < childList.length;j++){
				var child = childList[j];
				child.depth = menuItem.depth + 1;
				childrenDiv.appendChild(buildMenuTree(child));
			}
			return childrenDiv;
		}
	}
	
	function buildMenuTreeAll(menuItem){
		var menuDiv =  makeMenu(menuItem);
		var childList = menuItem.children;
		if(childList){
			var childrenDiv = document.createElement('div');
			childrenDiv.className = 'ygtvchildren';
			for(var j = 0; j < childList.length;j++){
				var child = childList[j];
				menuDiv.appendChild(buildMenuTreeAll(child));
			}
			menuDiv.appendChild(childrenDiv);
		}
		return menuDiv;
	}
	
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
	
	
	
	/**
     * Create menu item of sitemap
     * t_: Prefix of ID of menu Box
     * tc_: Prefix of Div ID displays menu and menu contener
     * ti_: Prefix of ID for DIV including icon set on handler for dropping. Menu icon for short.
     */
	function makeMenu(menuItem) {
		var menuDiv = document.createElement('div');
		menuDiv.className = 'ygtvitem';
		var menuTable = document.createElement('table');
		menuTable.cellPadding = 0;
		menuTable.cellSpacing = 0;
		menuDiv.appendChild(menuTable);
		var menuTbody = document.createElement('tbody');
		menuTable.appendChild(menuTbody);
		var menuTr = document.createElement('tr');
		menuTbody.appendChild(menuTr);

		var parents = [menuItem.depth];
		var parentMenu = menuItem;
		for(var i = menuItem.depth; i > 0; i--){
			parentMenu = parentMenu.parent;
			parents[i-1] = parentMenu;
		}
		if(menuItem.depth > 0){
			for(var i = 0; i < menuItem.depth; i++){
				var depthTd = document.createElement('td');
				if(parents[i].isLast  ){
					depthTd.className = 'ygtvblankdepthcell';
				}else{
					depthTd.className = 'ygtvdepthcell';
				}
				menuTr.appendChild(depthTd);
				var depthDiv = document.createElement('div');
				depthDiv.className = 'ygtvspacer';
				depthTd.appendChild(depthDiv);
			}
		}
		var lineTd = document.createElement('td');
		lineTd.id = "i_" + menuItem.id;
		var lineDiv = document.createElement('div');
		lineDiv.className = 'ygtvspacer';
		
		var hasChilds = false;
		lineTd.appendChild(lineDiv);
		menuTr.appendChild(lineTd);
		
		//var menuLi = document.createElement("li");

		var itemTd = document.createElement('td');
		itemTd.style.width = "100%";
		menuTr.appendChild(itemTd);
		
		var divMenuItem = document.createElement("div");
		divMenuItem.id = "tc_" + menuItem.id;
		if ( menuItem.children.length > 0) {
			if(menuItem.isLast ){
				lineTd.className = 'ygtvlp';
			}else{
				lineTd.className = 'ygtvtp';
			}
			Event.observe(lineTd, 'click', getClickHandler(lineTd, menuItem), false,"_sidemenu");
			hasChilds = true;
		} else {
			if(menuItem.isLast){
				lineTd.className = 'ygtvln';
			}else{
				lineTd.className = 'ygtvtn';
			}
		}
		var divMenuItemIcon = document.createElement("div");
		divMenuItemIcon.setAttribute("id", "ti_" + menuItem.id);
		if ( menuItem.type ){
//			var handler = IS_SiteAggregationMenu.menuDragInit(menuItem, divMenuItemIcon, divMenuItem);
			var handler = IS_SiteAggregationMenu.getDraggable(menuItem, divMenuItemIcon, divMenuItem,
				false,false,container.parentNode.parentNode );
			divMenuItemIcon.handler = handler;
			
			IS_Event.observe(itemTd, "mousedown", function(e){
				Event.stop(e);
			}, false, "_sidemenu");	

			var returnToMenuFunc = IS_SiteAggregationMenu.getReturnToMenuFuncHandler( divMenuItemIcon, menuItem.id, handler );
			var displayTabName = IS_SiteAggregationMenu.getDisplayTabNameHandler( divMenuItemIcon, menuItem.id, handler, returnToMenuFunc, "_sidemenu" );
			
			divMenuItemIcon.className = "menuItemIcon";
			itemTd.style.cursor = "move";
			IS_Widget.setIcon(divMenuItemIcon, menuItem.type, {multi:menuItem.multi});
			
			if(IS_Portal.isChecked(menuItem) && !/true/.test(menuItem.multi)){
				divMenuItemIcon.handler.destroy();
				Element.addClassName(divMenuItemIcon, 'menuItemIcon_dropped');
				IS_Event.observe(divMenuItemIcon, 'mouseover', displayTabName, false, "_sidemenu");
				itemTd.style.cursor = "default";
			}
			
			// 200-300millsec can be lost as addListener executes new Array.
			function getPostDragHandler(menuItemId, handler){
				if( Browser.isSafari1 ) {
					// Fix for display of item dropped by clicking
					return function() {
						var dummyDiv = menuDiv.cloneNode( true );
						var parentNode = menuDiv.parentNode;
						parentNode.replaceChild( dummyDiv,menuDiv );
						postDragHandler( menuItemId );
						
						setTimeout( function() {
							parentNode.replaceChild( menuDiv,dummyDiv )
						},10 );
					}
				} else {
					return function(){ postDragHandler(menuItemId, handler);};
				}
			}
			function postDragHandler(menuItemId, handler){
				//fix 209 Sometimes the widget that can be dropped plurally is dropped to a tab can not be dropped.
				if( /true/i.test( menuItem.multi ) )
					return;
				
//				Event.stopObserving(itemTd, "mousedown", handler, false);
				divMenuItemIcon.handler.destroy();
				//$("ti_" + menuItemId).className = (/MultiRssReader/.test(menuItem.type)) ? "menuItemIcon_multi_rss_gray" : "menuItemIcon_rss_gray";

				Element.addClassName(divMenuItemIcon, 'menuItemIcon_dropped');
				
//				divMenuItemIcon.className = (/MultiRssReader/.test(menuItem.type)) ? "menuItemIcon_multi_rss_gray" : "menuItemIcon_rss_gray";
				itemTd.style.cursor = "default";
				
				var divMenuItemParent = divMenuItem.parentNode;
				var divMenuItemNextSibling = divMenuItem.nextSibling;
				divMenuItemParent.removeChild( divMenuItem );
				if( divMenuItemNextSibling ) {
					divMenuItemParent.insertBefore( divMenuItem,divMenuItemNextSibling );
				} else {
					divMenuItemParent.appendChild( divMenuItem );
				}
				
				IS_Event.observe(divMenuItemIcon, 'mouseover', displayTabName, false, "_sidemenu");
			};
			IS_EventDispatcher.addListener('dropWidget', menuItem.id, getPostDragHandler(menuItem.id), true);
			if( menuItem.properties && menuItem.properties.url ) {
				var url = menuItem.properties.url;
				IS_EventDispatcher.addListener( IS_Widget.DROP_URL,url,( function( menuItem,handler ) {
						return function( widget ) {
							if( !IS_Portal.isMenuType( widget,menuItem )) return;
							
							postDragHandler(menuItem.id, handler);
						}
					})( menuItem,handler ) );
			}
			
			function getCloseWidgetHandler(menuItemId){
				if( Browser.isSafari1 ) {
					return function() {
						menuDiv.style.display = "none"
						closeWidgetHandler( menuItemId );
						menuDiv.style.display = "block"
					}
				} else {
					return function(){ closeWidgetHandler(menuItemId);};
				}
			}
			function closeWidgetHandler(menuItemId){
//				IS_Event.observe(itemTd, "mousedown", handler, false, "_menu");
				/*
				Event.observe(handler.handle, "mousedown", handler.eventMouseDown,"_sidemenu");
				IS_Draggables.register(handler);
				*/
				divMenuItemIcon.handler = IS_SiteAggregationMenu.getDraggable(menuItem, divMenuItemIcon, divMenuItem,
					false,false,container.parentNode.parentNode );
				
				//$("ti_" + menuItemId).className = (/MultiRssReader/.test(menuItem.type))? "menuItemIcon_multi_rss" : "menuItemIcon_rss";

				Element.removeClassName(divMenuItemIcon, 'menuItemIcon_dropped');
				
//				divMenuItemIcon.className = (/MultiRssReader/.test(menuItem.type))? "menuItemIcon_multi_rss" : "menuItemIcon_rss";
				itemTd.style.cursor = "move";
				
				divMenuItemIcon.title = "";
				IS_Event.stopObserving(divMenuItemIcon, 'mouseover', displayTabName, false, "_menu");
			}
			IS_EventDispatcher.addListener('closeWidget', menuItem.id, getCloseWidgetHandler(menuItem.id, handler), true);
			if( menuItem.properties && menuItem.properties.url ) {
				var url = menuItem.properties.url;
				IS_EventDispatcher.addListener( IS_Widget.CLOSE_URL,url,( function( menuItem,handler ) {
						return function( widget ) {
							if( !IS_Portal.isMenuType( widget,menuItem )) return;
							
							closeWidgetHandler(menuItem.id, handler);
						}
					})( menuItem,handler ) );
			}
		}else{
			divMenuItemIcon.className = "treemenuItemIcon_blank";
		}
		
		divMenuItem.appendChild(divMenuItemIcon);
		
		var divMenuTitle = document.createElement("div");
		divMenuTitle.id = "t_" + menuItem.id;
		divMenuTitle.className = "treeMenuTitle";
		
		var title = menuItem.directoryTitle || menuItem.title;
		if (menuItem.href && !menuItem.linkDisabled) {
			var aTag = document.createElement('a');
			aTag.href = menuItem.href;
			aTag.appendChild(document.createTextNode(title));
			
			if(/^javascript:/i.test( menuItem.href )){
				var aTagOnClick = function(e) {
					eval( menuItem.href );
					Event.stop(e);
				}
				IS_Event.observe(aTag, "click", aTagOnClick, false, "_menu");
			}else if(menuItem.display == "self") {
				aTag.target = "_self";
			} else if(menuItem.display == "newwindow"){
				aTag.target = "_blank";
			} else {
				if(menuItem.display == "inline")
					aTag.target="ifrm";
				var aTagOnClick = function(e){
					IS_Portal.buildIFrame(aTag);
				}
//				IS_Event.observe(aTag, "mousedown", function(e){Event.stop(e);}, false, "_sidemenu");
				IS_Event.observe(aTag, "click", aTagOnClick, false, "_sidemenu");
			}
			IS_Event.observe(aTag, "mousedown", function(e){Event.stop(e);}, false, "_sidemenu");
			divMenuTitle.appendChild(aTag);
		}else{
			divMenuTitle.appendChild(document.createTextNode(title));
		}
		divMenuTitle.title = title;
		
		if ( Browser.isIE ) {
			divMenuItem.appendChild(divMenuTitle);
			divMenuItem.style.width = "100%";
//			divMenuItem.style.whiteSpace = "nowrap";
			divMenuItem.style.wordBreak = "break-all";
		} else {
			divMenuTitle.style.height = "auto";
			var menuItemTable = document.createElement("table");
			menuItemTable.cellSpacing = "0";
			menuItemTable.cellPadding = "0";
//			menuItemTable.style.width = "100%";
//			menuItemTable.style.whiteSpace = "nowrap";
			var tr = document.createElement("tr");
			var td = document.createElement("td");
			td.appendChild(divMenuTitle);
			
			tr.appendChild(td);
			menuItemTable.appendChild(tr);
			divMenuItem.appendChild(menuItemTable);
		}
		itemTd.appendChild(divMenuItem);
		
		if(hasChilds){
			var childList = menuItem.children;
			var hasWidget = false;
			for(var i = 0; i < childList.length ;i++){
				var childItem = childList[i];
				if(childItem.type){
					hasWidget = true;
					break;
				}
			}
			
			if(hasWidget){
				var folderFeedContainer = document.createElement("div");
				
//				if(Browser.isIE) // The item is set in wrong position in IE if it is not in between BRs
//					folderFeedContainer.appendChild(document.createElement("br"));
				
				folderFeedContainer.className = "multiDropHandle";
				folderFeedContainer.style.display = "none";
				
				var headerTable = document.createElement("table");
				headerTable.cellSpacing = 0;
				headerTable.cellPadding = 0;
				headerTable.style.width = "100%";
				
				if(Browser.isIE) headerTable.style.marginBottom = "2px";
				
				var headerTbody = document.createElement("tbody");
				var headerTr = document.createElement("tr");
				var closeTd = document.createElement("td");
				headerTable.appendChild(headerTbody);
				headerTbody.appendChild(headerTr);
				folderFeedContainer.appendChild(headerTable);
				
				var folderIconTd = document.createElement("td");
				folderIconTd.className = "menufolderfeed";
				
				var folderIcon = document.createElement("img"); 
				folderIcon.src = imageURL + "drop_all.gif";
				folderIconTd.appendChild(folderIcon);
				
				var folderFeedTitleTd = document.createElement("td");
				folderFeedTitleTd.className = "menufolderfeed";
				folderFeedTitleTd.style.width = "100%";
				var folderFeedTitle = document.createElement("div"); 
				
				folderFeedTitle.className = "menufolderfeedTitle";
				folderFeedTitle.innerHTML = IS_R.lb_dropAll;
				folderFeedTitleTd.appendChild(folderFeedTitle);
				
				headerTr.appendChild(folderIconTd);
				headerTr.appendChild(folderFeedTitleTd);
//				var dragHandler = IS_SiteAggregationMenu.menuDragInit(menuItem, folderFeedContainer, folderFeedContainer, true, true);
				IS_SiteAggregationMenu.getMultiDropDraggable(folderFeedContainer, menuItem);
				
				itemTd.appendChild(folderFeedContainer);
//				IS_Event.observe(folderFeedContainer,"mousedown", dragHandler, false, "_menu");
				/*
				IS_Event.observe(itemTd,"mouseover", getTreeOverHandler(folderFeedContainer, lineTd), false, "_sidemenu");
				IS_Event.observe(itemTd,"mouseout", getTreeOutHandler(folderFeedContainer), false, "_sidemenu");
				*/
				
				IS_Event.observe(itemTd,"mouseover", function(){
					clearTimeout(folderFeedContainer.overTimeout);
					clearTimeout(folderFeedContainer.outTimeout);
					if(lineTd.className == 'ygtvtp' || lineTd.className == 'ygtvlp'){
					}else {
						delayDisplay.call(itemTd, folderFeedContainer);
					}
				}.bind(itemTd), false, "_sidemenu");
				
				IS_Event.observe(itemTd,"mouseout", function(){
					clearTimeout(folderFeedContainer.overTimeout);
					clearTimeout(folderFeedContainer.outTimeout);
					if(!IS_Portal.isItemDragging){
						delayDisplayNone.call(itemTd, folderFeedContainer);
					}
				}, false, "_sidemenu");
				
				function delayDisplay( div ){
					div.overTimeout = setTimeout(function(){
						div.style.display = "";
					}.bind(div), 150);
				}
				function delayDisplayNone( div ){
					div.outTimeout = setTimeout(function(){
						div.style.display = "none";
					}.bind(div), 150);
				}
			}
		}
		
		return menuDiv;
	}
	
	function getClickHandler(icon, menuItem){
		return function(e){ subMenuOpenClose(e, icon, menuItem);} ;
	}
	
	function subMenuOpenClose(e, icon, menuItem) {
		var el = window.event ? icon : e ? e.currentTarget : null;
		
		if (!el) return;
		
		if(el.className == 'ygtvtp' || el.className == 'ygtvlp'){
			subMenuOpen(el, menuItem);
		}else{
			subMenuClose(el);
		}
		
	}
	
	function subMenuOpen(el, menuItem){
		if(el.className == "ygtvtm" || el.className == 'ygtvlm') return;
		
		if(el.className == 'ygtvtp'){
			el.className = 'ygtvtm';
		}else{
			el.className = 'ygtvlm';
		}
		
		var menuDiv = el.parentNode.parentNode.parentNode.parentNode;

		if (!menuItem.isChildrenBuildSiteMap) {
			var childUl = buildMenuTreeChild(menuItem);
			menuDiv.appendChild(childUl);
			menuItem.isChildrenBuildSiteMap = true;
		}
		
		for (var i = 0; i < menuDiv.childNodes.length; i++) {
			var node = menuDiv.childNodes[i];
			if (node.nodeName.toLowerCase() == 'div') {
				node.style.display = 'block';
			}
		}
		IS_SidePanel.adjustPosition();
	}
	
	function subMenuClose(el){
		if(el.className == 'ygtvtp' || el.className == 'ygtvlp') return;
		
		if(el.className == 'ygtvtm'){
			el.className = "ygtvtp";
		}else{
			el.className = 'ygtvlp';
		}
		var menuDiv = el.parentNode.parentNode.parentNode.parentNode;
		
		for (var i = 0; i < menuDiv.childNodes.length; i++) {
			var node = menuDiv.childNodes[i];
			if (node.nodeName.toLowerCase() == 'div') {
				node.style.display = 'none';
			}
		}
	}
	
};

IS_SidePanel.SiteMap.refreshTreeMenu = function() {
	if(IS_Portal.isItemDragging) {
		setTimeout( IS_SidePanel.SiteMap.refreshTreeMenu, 500);
	} else {
		//TODO:The message of SiteAggregationMenu is disappeared if sideMenuURL != siteAggregationMenuURL as well.
		IS_Portal.closeMsgBar();
		if( displaySideMenu == 'reference_top_menu' ) {
			IS_SiteAggregationMenu.refreshMenu();
		} else if( displaySideMenu ) {
			// fix #305
//			$("portal-tree-menu").innerHTML = "";
			IS_TreeMenu.types.sidemenu.isSuccess = false;
			IS_Portal.treeMenuObject = new IS_SidePanel.SiteMap();
		}
	}
}
