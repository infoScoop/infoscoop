IS_TreeMenu = Class.create();
IS_TreeMenu.types = {};
IS_TreeMenu.primaryTypeNames = [];
IS_TreeMenu.alertSettings = {};

IS_TreeMenu.getTypeNames = function() {
	var typeNames = $H( IS_TreeMenu.types ).keys();
	
	return IS_TreeMenu.primaryTypeNames.concat(
			typeNames.without.apply( typeNames,IS_TreeMenu.primaryTypeNames ).reverse());
}
IS_TreeMenu.getMenuId = function( widgetId,widgetType ) {
	return IS_Portal.getTrueId( widgetId,widgetType ).substring(2);
}
IS_TreeMenu.isMenuItem = function( widgetId ) {
	var menuId = IS_TreeMenu.getMenuId( widgetId );
	
	return !( !IS_TreeMenu.findMenuItem( menuId ) );
}
IS_TreeMenu.findMenuItem = function( menuId ) {
	return IS_TreeMenu.getTypeNames().collect( function( t ) {
		var menu = IS_TreeMenu.types[ t ];
		
		if( menu ) {
			return menu.menuItemList[ menuId ];
		}
	}).compact().shift();
}
IS_TreeMenu.findMenuItemByURL = function( type,url ) {
	return IS_TreeMenu.getTypeNames().collect( function( t ) {
		return IS_TreeMenu.types[ t ];
	}).uniq().collect( function( menu ){
		if( menu && menu.isSuccess ) {
			return menu.findMenuItemByURL( type,url );
		}
	}).flatten();
}
IS_TreeMenu.findMenuItemByType = function(type) {
	return IS_TreeMenu.getTypeNames().collect( function( t ) {
		return IS_TreeMenu.types[ t ];
	}).uniq().collect( function( menu ){
		if( menu && menu.isSuccess ) {
			return menu.findMenuItemByType(type);
		}
	}).flatten();
}

IS_TreeMenu.loadMenu = function( type,url,opt ) {
	opt = Object.extend( {
		method: 'get',
		asynchronous: true,
		requestHeaders: [],
		onFailure: function(t){}
	},opt );
	var option = Object.clone( opt );
	
	option.requestHeaders = ["menuType",type].concat( opt.requestHeaders );
	
	option.onSuccess = function( response ) {
		var evalResult = eval( response.responseText );
		
		if( opt.onSuccess ) opt.onSuccess( evalResult );
	}
	option.on404 = function(t){
		msg.error(IS_R.getResource(IS_R.ms_menuNotFound2, [url]));
		if( opt.on404 ) {
			opt.on404(t);
		} else {
			opt.onFailure(t);
		}
	}
	option.on10408 = function(req, obj){
		msg.error(IS_R.ms_menuLoadon10408);
		if( opt.on10408 ) {
			opt.on10408( req,obj );
		} else {
			opt.onFailure(req);
		}
	}
	option.onFailure = function(t){
		msg.error(IS_R.getResource(IS_R.ms_menuLoadOnUnSuccess, [t.status, t.statusText]));
		opt.onFailure(t);
	}
	option.onException = function(r, t){
		msg.error(IS_R.getResource(IS_R.ms_menuLoadonException, [getText(t)]));
		if( opt.onException ) {
			opt.onException(r,t);
		} else {
			opt.onFailure(t);
		}
	}

	AjaxRequest.invoke( (url) ? is_getProxyUrl( url, "MakeMenu") : hostPrefix + "/mnusrv/" + type, option);
}
IS_TreeMenu.loadServiceMenuItems = function( type,serviceMenuItems,opt ) {
	var eventTargets = serviceMenuItems.collect( function( menuItem ) {
		return { type:"loadMenuComplete", id:menuItem.serviceURL };
	});
	
	IS_EventDispatcher.combineEvent("loadMenuComplete",type,eventTargets,true );
	
	serviceMenuItems.each( function( menuItem ) {
		menuItem.loadServiceMenu( opt );
	});
}
IS_TreeMenu.isLoaded = function() {
	return !$H( this.types ).keys().find( function( type ) {
		return this.types[ type ] && !this.types[ type ].isLoaded();
	}.bind( this ));
}

// Method for loading in series the menus that not loaded yet
IS_TreeMenu.waitForLoadMenu = function( handler ) {
	if( IS_TreeMenu.isLoaded())
		return;
	
	var notLoadedType = $H( this.types ).keys().find( function( type ) {
		return  IS_TreeMenu.types[type] && !IS_TreeMenu.types[type].isLoaded();
	});
	if( !notLoadedType )
		return;
	
	var menu = IS_TreeMenu.types[notLoadedType];
	if( !menu.loadMenu( {
			includeServiceMenu: true,
			onComplete: handler
		})) {
		IS_EventDispatcher.addListener("loadMenuComplete",menu.type,handler,false,true );
	}
}

IS_TreeMenu.addMenuItem = function(menuItem){
	if( IS_Widget.MaximizeWidget && IS_Widget.MaximizeWidget.headerContent )
		IS_Widget.MaximizeWidget.headerContent.turnbackMaximize();
	  
	// TODO: Copy from onDrop in Portal.js for processing of MultiRssReader. Should be function.
	var widget;
	if (/MultiRssReader/.test(menuItem.type)) {
		var divParent;
		var subWidgetConf;
		var parentItem = menuItem.parent;
		var w_id = IS_Portal.currentTabId + "_p_" + parentItem.id;
		var divParent = $(w_id);
		
		if(!divParent){
			// Create MultiReader that includes only the self; no target exists
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
				"MultiRssReader", w_id, 0, parentItem.title, parentItem.href, parentItem.properties);
			
			subWidgetConf = IS_WidgetsContainer.WidgetConfiguration.getFeedConfigurationJSONObject(
							"RssReader", "w_" + menuItem.id, menuItem.title, menuItem.href, "false", menuItem.properties);
			subWidgetConf.menuId = menuItem.id;
			subWidgetConf.parentId = "p_" + menuItem.parentId;
	
			var multiWidget = IS_WidgetsContainer.addWidget( IS_Portal.currentTabId, widgetConf , false, false, [subWidgetConf]);
			IS_Widget.setWidgetLocationCommand(multiWidget);
			
			widget = multiWidget.content.getRssReaders()[0];
		}else{
			// Adding itself to existing cooperativ Multi
			var targetWidget = IS_Portal.getWidget(divParent.id, IS_Portal.currentTabId);
			
			// Head at order display of time
			var siblingId;
			var nextSiblingId;
			if(targetWidget.getUserPref("displayMode") == "time"){
				siblingId = "";
				nextSiblingId = "";
			}else{
				siblingId = (widgetGhost.previousSibling) ? widgetGhost.previousSibling.id : "";
				nextSiblingId = (widgetGhost.nextSibling) ? widgetGhost.nextSibling.id : "";
			}
			var widgetConf = IS_SiteAggregationMenu.getConfigurationFromMenuItem(menuItem, 0);
			widgetConf.type = "RssReader";
			
			// subWidget in the same tab is always built
			var currentTabId = IS_Portal.currentTabId;
			if( Browser.isSafari1 && targetWidget.content.isTimeDisplayMode())
				IS_Portal.currentTabId = "temp";
			widgetConf.parentId = "p_" + menuItem.parentId;
			widget = IS_WidgetsContainer.addWidget( currentTabId, widgetConf , true, function(w){
				w.elm_widget.className = "subWidget";
			});//TODO: The way of passing subWidget
			
			if( Browser.isSafari1 && targetWidget.content.isTimeDisplayMode())
				IS_Portal.currentTabId = currentTabId;
			
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
		}
	}else {
		widgetConf = IS_SiteAggregationMenu.getConfigurationFromMenuItem(menuItem, 0);
		widget = IS_WidgetsContainer.addWidget( IS_Portal.currentTabId, widgetConf );
	 	IS_Widget.setWidgetLocationCommand(widget);
	}

//							  var widgetConf = IS_SiteAggregationMenu.getConfigurationFromMenuItem(menuItem, 0);
//							  var widget = IS_WidgetsContainer.addWidget( IS_Portal.currentTabId, widgetConf );
	
	IS_Portal.widgetDropped( widget );
	return widget;
}

IS_TreeMenu.prototype = {
	initialize: function( type ) {
		this.type = type;
		
		this.menuItemList = {};
		this.topMenuIdList = [];
		this.menuItemTreeMap = {};
		this.serviceMenuMap = {};
	},
	setMenu: function( url,a,b,c,clear ) {
		var this_ = this;
		if( clear ) {
			$H( this.menuItemList ).keys().each( function( key ) {
				delete this_.menuItemList[ key ];
			});
			this.topMenuIdList.clear();
			$H( this.menuItemTreeMap ).keys().each( function( key ) {
				delete this_.menuItemTreeMap[ key ];
			});
		}

		$H( a ).values().collect( function( menuObj ) {
			this_.menuItemList[ menuObj.id ] = new IS_TreeMenu.MenuItem( menuObj );
		});
		if( clear ) {
			b.each( function( topMenuId ) {
				this_.topMenuIdList.push( topMenuId );
			});
		}
		$H( c ).each( function( entry ) {
			this_.menuItemTreeMap[ entry.key ] = entry.value;
		});
		
		var menuItems = [];
		$H( this.menuItemList ).each( function( entry ) {
			menuItems.push( entry.value );
		});
		this.menuItems = menuItems;
		
		menuItems.each( function( menuItem ) {
			menuItem.setOwner( this_ );
		});

		var aArray = [];
		for(i in a){
			aArray.push(i);
		}
		var data = aArray.join(",");
		var option ={
		  method: 'post' ,
		  asynchronous:true,
		  postBody: data,
		  contentType: "application/json",
		  onSuccess:function(req){
			  var newIds = eval(req.responseText);
			  if(!newIds || !newIds.length){ return;}
			  
			  this_.reloadGadgetIcons();
			  
			  var msgListDiv = $('message-list');
			  
			  var newMenuCnt = 0;
			  for(var i = 0; i < newIds.length; i++){
				  if(newMenuCnt == 5){
					  msgListDiv = $('message-list-more');
					  $('message-list-more-btn').show();
				  }
				  
				  var menuItem = this_.menuItemList[newIds[i]];
				  var alertSetting = (typeof menuItem.alert != "undefined") ? menuItem.alert : IS_TreeMenu.alertSettings[url];
				  var messageText = IS_R.getResource(IS_R.ms_menuadded,[menuItem.getPaths().join("/")]);
				  msg.info(messageText);
				  if(alertSetting && !menuItem.serviceURL){
					  newMenuCnt++;
					  var newMenuItemMsgSpan = document.createElement('div');
					  
					  var infoImg = document.createElement('img');
					  infoImg.style.position ="relative";
					  infoImg.style.top = "2px";
					  infoImg.style.paddingRight = "2px";
					  infoImg.src = imageURL+"information.gif";
					  newMenuItemMsgSpan.appendChild(infoImg);
					
					  newMenuItemMsgSpan.appendChild(document.createTextNode(messageText));
					  
					  if(menuItem.type){
						  var addWidgetButton = document.createElement('input');
						  addWidgetButton.type = 'button';
						  addWidgetButton.value = IS_R.lb_add;
						  newMenuItemMsgSpan.appendChild(addWidgetButton);
						  var addMenuItemFunc = function(menuItem){
							if( IS_Portal.isChecked(menuItem) ){
								  this.disabled = true;
								  this.value = IS_R.lb_added;
								  return;
							}
							IS_TreeMenu.addMenuItem(menuItem);
							this.disabled = true;
							this.value = IS_R.lb_added;
						  }
						  if(alertSetting == 2){
							  setTimeout(addMenuItemFunc.bind(addWidgetButton, menuItem),1000);
						  }else{
							  IS_Event.observe(addWidgetButton, 'click',  addMenuItemFunc.bind(addWidgetButton, menuItem), false, 'msgBar');
						  }
					  }
					  msgListDiv.appendChild(newMenuItemMsgSpan);
					  $('message-bar').style.display = "";
				  }
			  }
			  if( IS_SidePanel ) IS_SidePanel.adjustPosition();
			  
			  IS_EventDispatcher.newEvent("adjustedMessageBar");
		  },
		  onFailure: function(r,e) {
			msg.error("Check menu failed: "+r.status+","+r.statusText );
		  },
		  onException: function(r, e){
			msg.error("Check menu failed: "+getErrorMessage(e) );
		  }
		}
		AjaxRequest.invoke(hostPrefix + "/mnuchksrv?url=" + encodeURIComponent(url) , option);
		return b.collect( function( topMenuId ) {
			return this_.menuItemList[ topMenuId ];
		});

	},
	reloadGadgetIcons: function(){
		var opt = {
			onSuccess:function(req){
				IS_WidgetIcons = req.responseText.evalJSON();
			},
			onFailure:function(r, e){
				msg.error(IS_R.ms_failedLoadIcons+r.status+"-"+r.statusText);
			},
			onException:function(r, e){
				msg.error(IS_R.ms_failedLoadIcons+getErrorMessage(e));
			}
			
		};
		AjaxRequest.invoke(hostPrefix + "/gadgeticon" , opt);
	},
	findMenuItemByURL: function( type,url ) {
		return this.menuItems.findAll( function( menuItem ) {
			if( !menuItem.properties ) {
				return;
			}
			
			var menuType = menuItem.type;
			if( type != menuType && /MultiRssReader/.test( menuType ) )
				menuType = "RssReader";
			
			return ( menuType == type && menuItem.properties.url == url );
		});
	},
	findMenuItemByType: function(type) {
		return this.menuItems.findAll( function( menuItem ) {
			var menuType = menuItem.type;
			if( type != menuType && /MultiRssReader/.test( menuType ) )
				menuType = "RssReader";
			
			return ( menuType == type );
		});
	},
	loadMenu: function( opt ) {
		if( this.loading )
			return false;
		
		this.loading = true;
		
		var serviceMenuLoading = false;
		var option = Object.clone( opt || {} );
		option.onSuccess = function( evalResult ) {
			var this_ = this;
			var serviceMenuItems = this.getServiceMenuItems();
			serviceMenuLoading = ( option.includeServiceMenu && serviceMenuItems.length > 0 )
			
			if( serviceMenuLoading ) {
				IS_EventDispatcher.addListener("loadMenuComplete",this.type,function() {
					this.loading = false;
					this.isComplete = true;
					
					if( !this.isSuccess ) this.isSuccess = true;
					if( opt.onSuccess ) opt.onSuccess();
					if( opt.onComplete ) opt.onComplete();
				}.bind( this ),false,true );
				
				IS_TreeMenu.loadServiceMenuItems( this.type,serviceMenuItems,{
					asynchronous: opt.asynchronous,
					includeServiceMenu: true
				} );
			} else {
				if( !this.isSuccess ) this.isSuccess = true;
				if( opt.onSuccess ) opt.onSuccess();
			}
		}.bind( this );
		option.onComplete = function() {
			this.isComplete = true;
			if( !serviceMenuLoading && this.loading) {
				this.loading = false;
				
				IS_EventDispatcher.newEvent("loadMenuComplete",this.type );
				if( opt.onComplete ) opt.onComplete();
			}
		}.bind( this );
		
		//var url = "menusrv/" + this.type ;//( this.type == "topmenu"? siteAggregationMenuURL : sideMenuURL );
		
		if( (this.type == "topmenu" && !displayTopMenu ) || (this.type=="sidemenu" && !displaySideMenu ) ) {
			setTimeout( function() {
				option.onSuccess();
				option.onComplete();
			} ,10 );
		} else {
			IS_TreeMenu.loadMenu( this.type, false ,option );
		}
		
		return true;
	},
	getServiceMenuItems: function() {
		var this_ = this;
		
		return this.topMenuIdList.findAll( function( topMenuId ) {
			return !( !this_.menuItemList[ topMenuId ].serviceURL );
		}).collect( function( topMenuId ) {
			return this_.menuItemList[ topMenuId ];
		});
	},
	/** Return true if itself and service is loaded at least once.*/
	isLoaded: function() {
		return this.isComplete && !(this.getServiceMenuItems().find( function( serviceMenu ) {
				return !serviceMenu.isComplete;
			}) );
	}
};

IS_TreeMenu.MenuItem = Class.create();
IS_TreeMenu.MenuItem.prototype = {
	initialize: function( menuObj ) {
		for( var i in menuObj ) {
			if( menuObj[i] instanceof Function ) continue;
			this[i] = menuObj[i];
		}
	},
	setOwner: function( owner ) {
		this.owner = owner;
		
		if( this.parentId )
			this.parent = owner.menuItemList[ this.parentId ];
		
		if( owner.menuItemTreeMap[ this.id ] ) {
			this.children = owner.menuItemTreeMap[ this.id ].collect( function( childId ) {
				return owner.menuItemList[ childId ];
			});
		} else {
			this.children = [];
		}
	},
	getPaths: function() {
		var paths = [this.directoryTitle || this.title];
		var parent = this.parent;
		while( parent ) {
			paths.push( parent.directoryTitle || parent.title );
			parent = parent.parent;
		}
		
		paths.push( this.owner.title );
		
		return paths.reverse();
	},
	loadServiceMenu: function( opt ) {
		if( this.loading )
			return false;
		
		this.loading = true;
		
		opt = Object.extend( {
			requestHeaders: []
		},opt || {} );
		var option = Object.clone( opt );
		option.requestHeaders = ["siteTopId","_"].concat( opt.requestHeaders );
		option.onSuccess = function( evalResult ) {
			this.menuTopItems = evalResult;
			
			if( !this.isSuccess ) this.isSuccess = true;
			if( opt.onSuccess ) opt.onSuccess( evalResult );
			
			/*if( option.includeServiceMenu ) {
			} else {
				IS_EventDispatcher.newEvent("loadMenuComplete",this.serviceURL );
			}*/
		}.bind( this );
		option.onComplete = function() {
			this.loading = false;
			this.isComplete = true;
			
			if( opt.onComplete ) opt.onComplete();
			IS_EventDispatcher.newEvent("loadMenuComplete",this.serviceURL );
		}.bind( this );
		
		IS_TreeMenu.loadMenu( this.owner.type,this.serviceURL,option );
		
		return true;
	}
};

var IS_SiteAggregationMenu = IS_Class.create();

if( displayTopMenu ) {
	IS_TreeMenu.types.topmenu = new IS_TreeMenu("topmenu");
	IS_TreeMenu.types.topmenu.title = IS_R.lb_topMenu;
	//var alertSetting = is_getPropertyInt(siteAggregationMenuAlertSetting, 1);
	//IS_TreeMenu.alertSettings[siteAggregationMenuURL] = ( alertSetting < 3 ) ? alertSetting : 1;
	
	//Call return value of MakeMenuFilter
	IS_SiteAggregationMenu.setMenu = function(url, a,b,c){
		IS_TreeMenu.types.topmenu.setMenu(url, a,b,c,true );
	};
	IS_SiteAggregationMenu.setServiceMenu = function(url, a, c, b){
		return IS_TreeMenu.types.topmenu.setMenu(url, a,b,c,false );
	};
	
	IS_SiteAggregationMenu.menuItemList = IS_TreeMenu.types.topmenu.menuItemList;
	IS_SiteAggregationMenu.topMenuIdList = IS_TreeMenu.types.topmenu.topMenuIdList;
	IS_SiteAggregationMenu.menuItemTreeMap = IS_TreeMenu.types.topmenu.menuItemTreeMap;
	IS_SiteAggregationMenu.serviceMenuMap = IS_TreeMenu.types.topmenu.serviceMenuMap;

}

IS_SiteAggregationMenu.displayTopLi;
IS_SiteAggregationMenu.isMenuRefreshed = false;
IS_SiteAggregationMenu.prototype.classDef = function () {
	var container = document.getElementById("portal-site-aggregation-menu");
	var refreshIcon = document.getElementById("portal-site-aggregation-menu-refresh");
	var indicatorIcon = document.getElementById("portal-site-aggregation-menu-indicator");
	
	var openedMenuSet = [];
	var level = [];
	var self = this;
	
	this.initialize = function(synchronous) {
		if(!container) return;
		
		if(!displayTopMenu){
			container.style.display = "none";
			return;
		}
		
		Event.observe(window, "resize",  IS_SiteAggregationMenu.resetMenu, false);

		//TODO: loadMenu(opt) in common should be called. by endoh 20090526
		var url = hostPrefix + "/mnusrv/topmenu";//is_getProxyUrl(siteAggregationMenuURL, "MakeMenu");
		var serviceMenuLoading = false;
		var opt = {
		    method: 'get' ,
			requestHeaders:["menuType","topmenu"],
		    asynchronous:!synchronous,
		    onRequest : function(){ self.start = new Date(); },
		    onSuccess: function( response ) {
				eval(response.responseText);
				
				var serviceMenuItems = IS_TreeMenu.types.topmenu.getServiceMenuItems();
				if( serviceMenuItems.length == 0 ) {
					IS_TreeMenu.types.topmenu.isSuccess = true;
				} else {
					serviceMenuLoading = true;
					var eventTargets = serviceMenuItems.collect( function( menuItem ) {
						return { type:"loadMenuComplete", id:menuItem.serviceURL };
					});
					
					IS_EventDispatcher.addComplexListener( eventTargets,function() {
						IS_TreeMenu.types.topmenu.isSuccess = true;
						IS_TreeMenu.types.topmenu.isComplete = true;
						IS_TreeMenu.types.topmenu.loading = false;
						
						IS_EventDispatcher.newEvent("loadMenuComplete","topmenu");
					},false,true );
				}
				
				displayMenu( response );
			},
		    on404: function(t) {
		        msg.error(IS_R.getResource(IS_R.ms_menuNotFound2,[url]));
		        if(!IS_SiteAggregationMenu.isMenuRefreshed){
		        	container.firstChild.innerHTML = "<span style='white-space:nowrap;font-size:90%;color:red;padding:5px;'>" + IS_R.ms_menuNotFound + "</span>";
		        }
		    },
		    on10408: function(req,obj) {
				msg.error(IS_R.ms_menuLoadon10408 );
				if(!IS_SiteAggregationMenu.isMenuRefreshed){
					container.firstChild.innerHTML = "<span style='white-space:nowrap;font-size:90%;color:red;padding:5px;'>" +
						IS_R.ms_menuLoadon10408
							+ "</span>";
				}
		    },
		    onFailure: function(t) {
				msg.error(IS_R.getResource(IS_R.ms_menuLoadOnUnSuccess,[t.status,t.statusText]));
				if(!IS_SiteAggregationMenu.isMenuRefreshed){
					container.firstChild.innerHTML = "<span style='white-space:nowrap;font-size:90%;color:red;padding:5px;'>" + IS_R.ms_menuLoadonFailure + "</span>";
				}
		    },
			onException: function(r, t){
				msg.error(IS_R.getResource(IS_R.ms_menuLoadonException,[getText(t)] ));
				if(!IS_SiteAggregationMenu.isMenuRefreshed){
					container.firstChild.innerHTML = "<span style='white-space:nowrap;font-size:90%;color:red;padding:5px;'>" + IS_R.ms_menuLoadonFailure + "</span>";
				}
			},
		    onComplete: function(t) {
				if( !serviceMenuLoading ) {
					IS_TreeMenu.types.topmenu.isComplete = true;
					IS_TreeMenu.types.topmenu.loading = false;
					IS_EventDispatcher.newEvent("loadMenuComplete",IS_TreeMenu.types.topmenu.type );
				}
		    }
		};
		if(indicatorIcon && refreshIcon) {
			refreshIcon.style.display = "none";
			indicatorIcon.style.display = "block";
		} else {
			var msgDiv = document.createElement("div");
			msgDiv.innerHTML = "Loading...";
			msgDiv.style.cssFloat = "left";
			container.appendChild(msgDiv);
	//		container.innerHTML = "Loading...";
		}
		
		IS_EventDispatcher.addListener("loadMenuComplete",IS_TreeMenu.types.topmenu.type,handleLoadComplete,false,true );
		IS_TreeMenu.types.topmenu.loading = true;
		
		AjaxRequest.invoke(url, opt);
	}
	function handleLoadComplete() {
		var end = new Date();
		msg.debug("SiteAggrigationMenu initialize duration: " + (end - self.start));

		if(indicatorIcon && refreshIcon) {
			refreshIcon.style.display = "block";
			indicatorIcon.style.display = "none";
		}else{
			createMenuRefreshIcon();
			indicatorIcon.style.display = "none";
		}
	}
	
	function createMenuRefreshIcon(){
		if(!refreshIcon) {
			refreshIcon = createRefreshIcon();
		}
		container.appendChild(refreshIcon);
		
		if(!indicatorIcon) {
			indicatorIcon = createIndicatorIcon();
		}
		container.appendChild(indicatorIcon);

		function createRefreshIcon(){
			refreshIcon = document.createElement("div");
			refreshIcon.className = "menuRefresh";
			refreshIcon.title = IS_R.lb_refreshMenu;
			refreshIcon.id = "portal-site-aggregation-menu-refresh";
			refreshIcon.style.display = 'none';
			Event.observe(refreshIcon, 'mousedown', IS_SiteAggregationMenu.refreshMenu, false);
			return refreshIcon;
		}
		
		function createIndicatorIcon(){
			indicatorIcon = document.createElement("img");
			indicatorIcon.src = imageURL +"indicator.gif";
			indicatorIcon.className = "menuIndicator";
			indicatorIcon.id = "portal-site-aggregation-menu-indicator";
			return indicatorIcon;
		}
	}
	
	function displayMenu() {
		var menuUl = document.createElement("ul");
		
		container.innerHTML = "";
		container.appendChild(menuUl);
		
		for(var i = 0; i < IS_SiteAggregationMenu.topMenuIdList.length; i++){
			var menuItem = IS_SiteAggregationMenu.menuItemList[IS_SiteAggregationMenu.topMenuIdList[i]];
			var topLi;
			if(menuItem.serviceURL){
				topLi = getMenuService(menuItem);
			}else{
				topLi = createTopMenu(menuItem);
			}
			menuUl.appendChild(topLi);
			if(Browser.isIE){
				topLi.style.width = topLi.firstChild.offsetWidth + "px";
				topLi.firstChild.style.height = "1.75em";
			}
		}
		createMenuRefreshIcon();
		
		if(IS_SiteAggregationMenu.isMenuRefreshed &&
			( displaySideMenu == 'reference_top_menu' )&& IS_SidePanel.siteMapOpened ){
			// fix #305
//			document.getElementById("portal-tree-menu").innerHTML = "";
			IS_Portal.treeMenuObject = new IS_SidePanel.SiteMap();
		}
		
		if(Browser.isIE)
//			Event.observe(document, "click", IS_SiteAggregationMenu.closeMenu, true);
			Event.observe("portal-maincontents-table", "click", IS_SiteAggregationMenu.closeMenu, true);
		
	}
	
	/**
	 * Obtain external menu service
	 * @param menuItem top menu object
	 */
	function getMenuService(menuItem) {
		var url = is_getProxyUrl(menuItem.serviceURL, "MakeMenu");
		IS_TreeMenu.alertSettings[menuItem.serviceURL] = (typeof menuItem.alert != 'undefined') ? menuItem.alert : 1;
		var topLi = document.createElement("li");
		topLi.id = menuItem.id;
		topLi.className = "topMenuLi";
		topLi.style.height = "1.75em";//IE
		topLoadingDiv = document.createElement("div");
		topLoadingDiv.appendChild(document.createTextNode("Loading..."));
		topLi.appendChild(topLoadingDiv);
		var menuTopItems = null;
		var opt = {
		  method: menuItem.serviceAuthType ? 'post' : 'get' ,
		  asynchronous:true,
		  requestHeaders:["siteTopId",menuItem.id,"menuType","topmenu"],
		  onSuccess: function(response) {
		  	var menuTopItemsText = response.responseText;
		  	menuTopItems = eval(menuTopItemsText);
			IS_SiteAggregationMenu.serviceMenuMap[menuItem.id] = menuTopItemsText;
		  },
		  on404: function(t) {
			  msg.error(IS_R.getResource(IS_R.ms_menuNotFound2,[url]));
			  if(!IS_SiteAggregationMenu.serviceMenuMap[menuItem.id])
				  topLi.innerHTML = "<span style='white-space:nowrap;font-size:90%;color:red;padding:5px;'>" + IS_R.ms_menuNotFound + "</span>";
		  },
		  on10408: function( req,obj ) {
			  msg.error(IS_R.ms_menuLoadon10408);
			  if(!IS_SiteAggregationMenu.serviceMenuMap[menuItem.id]){
				  topLi.innerHTML = "<span style='white-space:nowrap;font-size:90%;color:red;padding:5px;'>" +
				  	IS_R.ms_menuLoadon10408 + "</span>";
				}
		  },
		  onFailure: function(t) {
			  msg.error(IS_R.getResource(IS_R.ms_menuLoadOnUnSuccess,[t.status,t.statusText]));
			  if(!IS_SiteAggregationMenu.serviceMenuMap[menuItem.id])
				  topLi.innerHTML = "<span style='white-space:nowrap;font-size:90%;color:red;padding:5px;'>" + IS_R.ms_menuLoadonFailure + "</span>";
		  },
		  onException: function(r, t){
			  msg.error(IS_R.getResource(IS_R.ms_menuLoadonException,[getText(t)] ));
			  if(!IS_SiteAggregationMenu.serviceMenuMap[menuItem.id])
				  topLi.innerHTML = "<span style='white-space:nowrap;font-size:90%;color:red;padding:5px;'>" + IS_R.ms_menuLoadonFailure + "</span>";
		  },
		  onComplete: function(t) {
				try{
					if(!menuTopItems && IS_SiteAggregationMenu.serviceMenuMap[menuItem.id])
						menuTopItems = eval(IS_SiteAggregationMenu.serviceMenuMap[menuItem.id]);
				  	var topContainer = topLi.parentNode;
					if(menuTopItems && topContainer) {
						var index = IS_SiteAggregationMenu.topMenuIdList.indexOf(menuItem.id);
						var args = [index, 1];
						for(var i=0;i<menuTopItems.length;i++){
							args.push(menuTopItems[i].id);
							var newTopLi = createTopMenu(menuTopItems[i]);
							topContainer.insertBefore(newTopLi, topLi);
							if(Browser.isIE){
								newTopLi.style.width = newTopLi.firstChild.offsetWidth + "px";
								newTopLi.firstChild.style.height = "1.75em";
							}
						}
						IS_SiteAggregationMenu.topMenuIdList.splice.apply(IS_SiteAggregationMenu.topMenuIdList, args);
						topContainer.removeChild(topLi);
						
						IS_SiteAggregationMenu.resetMenu();
					}
				}catch(t){
					msg.error(IS_R.getResource(IS_R.ms_menuLoadonException,[getText(t)] ));
					topLi.innerHTML = "<span style='white-space:nowrap;font-size:90%;color:red;padding:5px;'>" + IS_R.ms_menuLoadonFailure + "</span>";
				}
			
			  var end = new Date();
			  msg.debug("SiteAggrigationMenu initialize duration: " + (end - self.start));
			  
			  if(indicatorIcon && refreshIcon) {
				  refreshIcon.style.display = "block";
				  indicatorIcon.style.display = "none";
			  }else{
				  createMenuRefreshIcon();
				  indicatorIcon.style.display = "none";
			  }
			  
			  if( IS_SidePanel ) IS_SidePanel.adjustPosition();
			  menuItem.isComplete = true;
			  IS_EventDispatcher.newEvent("loadMenuComplete",menuItem.serviceURL );
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
		
		if( IS_SidePanel ) IS_SidePanel.adjustPosition();
		AjaxRequest.invoke(url, opt);
		return topLi;
	}
	
	/**
     * Create top menu list of site aggregation menu.
     * @param menuItem Top menu object
     */  
	function createTopMenu(menuItem){
		var topLi = document.createElement("li");
		topLi.id = menuItem.id;
		topLi.className = "topMenuLi";
		topLi.style.height = "1.75em";//IE
		var titleA =document.createElement("a");
		titleA.className = "topMenuItem";
		titleA.title = menuItem.title;
		if(menuItem.href){
			titleA.href = menuItem.href;
			if(menuItem.display == "self") {
				titleA.target = "_self";
			} else if(menuItem.display == "newwindow"){
				titleA.target = "_blank";
			} else {
				if(menuItem.display == "inline")
					titleA.target="ifrm";
				var titleAOnclick = function(e){
					IS_Portal.buildIFrame(titleA);
				}
//				IS_Event.observe(titleA, "click", titleAOnclick, false, menuItem.id);
				IS_Event.observe(titleA, "click", titleAOnclick, false, "_menu");
			}
		}else{
			titleA.href = "#";
			titleA.style.cursor ="default";
		}
		
		titleA.appendChild(document.createTextNode(menuItem.title));
		topLi.appendChild(titleA);
		IS_Event.observe(topLi,"blur", getTopMenuItemMOutHandler(topLi), false, "_menu");
		IS_Event.observe(topLi,"mouseout", getTopMenuItemMOutHandler(topLi), false, "_menu");
		IS_Event.observe(topLi, "focus",  getTopMenuMOverHandler(topLi, menuItem), false, "_menu");
		IS_Event.observe(topLi, "mouseover",  getTopMenuMOverHandler(topLi, menuItem), false, "_menu");
		return topLi;
	}

	this.initMenuOverlay = function(){
		var iframe = document.createElement("iframe");
		iframe.className = "menuOverlay";
		iframe.border = "0";
		iframe.frameborder = "0";
		iframe.style.display = "none";
		document.body.appendChild(iframe);
		Event.observe(iframe, "mouseover", function(){this.style.display = "none";}.bind(iframe), false);
		this.overlay = iframe;
	}
	
	if( Browser.isSafari1 ) {
		// fix 327 Can not operate after displaying menu
		this.initMenuOverlay = ( function() {
			var initMenuOverlay = this.initMenuOverlay;
			return function() {
				var result = initMenuOverlay.apply( this );
				Event.observe( document.body,"mousemove",function(e) {
						if( !this.overlay.style.display == "none")
							return;
						
						if( Element.childOf( Event.element( e ),$("container") ) )
							return;
						
						this.overlay.style.display = "none";
					}.bind( this ),true );
				return result;
			}
		}).apply( this );
	}

	/**
     * Obtain mouse over handler
     * @param node li tag of Top menu
     * @parame menuItem Top menu object
     */
	function getTopMenuMOverHandler(node, menuItem){
		return function(e){ topMenuMOver(e, node, menuItem); };
	}
	
	function topMenuMOver(e, parent, parentMenuItem){
		var parent = window.event ? parent : e ? e.currentTarget : null;
		if (!parent) return;
		
		clearTimeout(parent.outTimeout);
		clearTimeout(parent.overTimeout);
		
		var childs = parent.childNodes;
		for(var i = 0; i < childs.length; i++){
			if(childs[i].nodeName.toUpperCase() == "A"){
				//childs[i].style.backgroundImage = "url(" + imageURL + "lev0_bg2.png)";
			}
		}
		
		if(!self.overlay) self.initMenuOverlay();
//		var offsetY = findPosY(parent) + parent.offsetHeight;
		var offsetY = findPosY(container) + container.offsetHeight;
		
		var overlayStyle = self.overlay.style;
		overlayStyle.top = offsetY;
		overlayStyle.width = Math.max(document.body.scrollWidth, document.body.clientWidth) - 5;
		overlayStyle.height = Math.max(document.body.scrollHeight, document.body.clientHeight) - offsetY - 5;
		overlayStyle.display = "";
		parent.overTimeout = setTimeout(function() { topMenuMOver2(e, parent, parentMenuItem); }, 150);
		
	}

	var overUlId = false;
	/**
     * Child menu is displayed if mouse cursor is over top menu.
     * Handling multi level display
     * @parame e Event object
     * @param node li tag of top menu
     * @parame menuItem Top menu object
     */
	function topMenuMOver2(e, parent, parentMenuItem){
		IS_SiteAggregationMenu.displayTopLi = parent;
		var childList = IS_SiteAggregationMenu.menuItemTreeMap[parent.id];
		var hasChilds = (childList && childList.length > 0) ? true : false;
		
		if(hasChilds && !parentMenuItem.isChildrenBuild){
			var height = (Browser.isIE) ? 23 : 21;//parseInt(document.getElementById("dummymenu").offsetHeight) ;
			var windowY = getWindowSize(false) - (findPosY(parent) + parent.offsetHeight + 20);
			
			var num = windowY / height;
			num = Math.floor(num);
			
			var colList = new Array();
			var tempList = new Array();
			var firstCol = true;
			var secondColFirst = false;
			for(var i = 0;i < childList.length;i++){
				if( (firstCol && i > 0 && (i % num) == 0 ) || (!firstCol && i > secondColFirst && ( (i + 1) % num) == 0) ){
					colList.push(tempList);
					tempList = new Array();
					if(firstCol){
						secondColFirst = i + 1;
						num++;
						firstCol = false;
					}
				}
				tempList.push(childList[i]);
			}
			colList.push(tempList);
			var childUls = getChildrenByTagName(parent, 'ul');
			
			if(!childUls || childUls.length == 0){
				parentMenuItem.isChildrenBuild = true;
				//Generate menu HTML
				for(var i = 0; i < colList.length; i++){
						
					var ul = document.createElement("ul");
					ul.className = "menuGroup";
					ul.id = "mg_" + parentMenuItem.id + "_" + i;
					if(i == 0){
						var headerDiv = createMenuHeader(ul, parentMenuItem, true);
						ul.appendChild(headerDiv);
					}
					for(var j = 0; j < colList[i].length;j++){
						var menuItem = IS_SiteAggregationMenu.menuItemList[colList[i][j]];
						ul.appendChild( makeMenu(menuItem) );
					}
					parent.appendChild(ul);
					ul.style.display = "block";
					IS_Event.observe(ul, "focus",  getUlMOverFor(ul), false, "_menu");
					IS_Event.observe(ul, "mouseover",  getUlMOverFor(ul), false, "_menu");
					IS_Event.observe(ul, "mouseover",  function(){IS_Portal.menuOver = true;}, false, "_menu");
					IS_Event.observe(ul, "mouseout",  function(){
						if(!IS_Portal.isItemDragging)
							IS_Portal.menuOver = false;
					}, false, "_menu");
				}
				
				childUls = getChildrenByTagName(parent, 'ul');
			}else if(!parentMenuItem.isChildrenBuild){
		
				parentMenuItem.isChildrenBuild = true;
				
				var childLiMap = {};
				for(var i = 0; i < childUls.length; i++){
					var lis = getChildrenByTagName(childUls[i], "li");
					for(var j = 0; j < lis.length; j++){
						childLiMap[lis[j].id] = lis[j];
					}
				}
				
				while(childUls[0]){
					parent.removeChild(childUls.pop());
				}
				overUlId = false;
				for(var i = 0; i < colList.length; i++){
						
					var ul = document.createElement("ul");
					ul.className = "menuGroup";
					ul.id = "mg_" + parentMenuItem.id + "_" + i;
					if(i == 0){
						var headerDiv = createMenuHeader(ul, parentMenuItem, true);
						ul.appendChild(headerDiv);
					}
					for(var j = 0; j < colList[i].length;j++){
						ul.appendChild( childLiMap[colList[i][j]] );
					}
					parent.appendChild(ul);
					ul.style.display = "block";
					IS_Event.observe(ul, "focus",  getUlMOverFor(ul), false, "_menu");
					IS_Event.observe(ul, "mouseover",  getUlMOverFor(ul), false, "_menu");
					IS_Event.observe(ul, "mouseover",  function(){IS_Portal.menuOver = true;}, false, "_menu");
					IS_Event.observe(ul, "mouseout",  function(){
						if(!IS_Portal.isItemDragging)
							IS_Portal.menuOver = false;
					}, false, "_menu");
				}
				
				childUls = getChildrenByTagName(parent, 'ul');
			}
			
			//Obtain max width of title DIV
			var ulWidth = 100;
			for(var i = 0 ; i < childUls.length; i++){
				childUls[i].style.display ="block";
				var lis = getChildrenByTagName(childUls[i], 'li');
				for(var j = 0; j < lis.length; j++){
					var divs = getChildrenByTagName(lis[j].firstChild, "div");
					for(var k = 0; k < divs.length; k++){
						if(divs[k].className=="menuTitle"){
							var titleWidth = divs[k].offsetWidth;
							var liWidth = titleWidth + 30;
							if(ulWidth < liWidth){
								ulWidth = liWidth;
							}
						}
					}
				}
			}
			var tables = ul.getElementsByTagName('table');
			for(var i=0;i<tables.length;i++){
				var tableWidth = tables[i].offsetWidth + 10;
				if(ulWidth < tableWidth){
					ulWidth = tableWidth;
				}
			}
			//Calculate the far left of menu
			var offset= findPosX(parent);
			var winX = getWindowSize(true) - 25;
			if( (ulWidth * colList.length + 7) > winX){//If the width of whole menu is larger than window size.
				offset = 7;
			}else if( (offset + (ulWidth * colList.length) ) > winX ){//If the width of whole menu is larger than the distance between far left and far right of top menu
				offset = (winX  - (ulWidth * colList.length) );
			}

			//Set the width and far left of menu.
			//var childUls =  getChildrenByTagName(parent, 'ul');
			for(var i = 0; i < childUls.length; i++){
				var ul = childUls[i];
				ul.style.width = ulWidth + "px";
				ul.style.left = offset + "px";
				if(Browser.isIE){
					ul.style.height = childUls[0].offsetHeight + "px";
				}else{
					ul.style.height = (childUls[0].offsetHeight - 4) + "px";
				}
				offset = offset + ulWidth;
			}
			parentMenuItem.isChildrenBuild = true;
		}

		var childs = parent.childNodes;
		for(var i = 0; i < childs.length; i++){
			if(childs[i].nodeName.toUpperCase() == "UL"){
				childs[i].style.display ="block";
				childs[i].style.visibility = 'visible';
			}
		}
	}

	function getTopMenuItemMOutHandler(node){
		return function(e){ topMenuItemMOut(e, node); };
	}
	function topMenuItemMOut(e, targetElement) {
		var el = window.event ? targetElement : e ? e.currentTarget : null;
		if (!el || IS_Portal.isItemDragging) return;
		
		clearTimeout(el.overTimeout);
		clearTimeout(el.outTimeout);
		
		var childs = el.childNodes;
		for(var i = 0; i < childs.length; i++){
			if(childs[i].nodeName.toUpperCase() == "A"){
				///childs[i].style.backgroundImage = "url(" + imageURL + "lev0_bg1.png)";
			}
		}
		
		el.outTimeout = setTimeout(function() { topMenuItemMOut2(el); }, 100);
	}
	function topMenuItemMOut2(el){
		for (var i = 0; i < el.childNodes.length; i++) {
			var node = el.childNodes[i];
			if (node.nodeName.toLowerCase() == 'ul') { 
				node.style.display = 'none';
				node.style.visibility = 'hidden';
			}
		}
		IS_SiteAggregationMenu.displayTopLi = false;
	}
	
	function getUlMOverFor(ul){
		return function(e){ ulMOver(e, ul); };
	}
	function ulMOver(e, ul){
		var ul = window.event ? ul : e ? e.currentTarget : null;
		if (!ul) return;
		if(ul.id == overUlId)return;
		overUlId = ul.id;
		ul.style.zIndex = 1200;
		var sibUls =  getChildrenByTagName(ul.parentNode, 'ul');
		for(var i = 0; i < sibUls.length; i++){
			if(sibUls[i].id != ul.id){
				sibUls[i].style.zIndex = 1100;
			}
		}
	}

	/**
     * Generate HTML of menu
     * @parame menuItem Top menu object
     * @return li tag
     */
	function makeMenu(menuItem){
		var menuLi = document.createElement("li");
		menuLi.className = "menuItem";
		menuLi.id = menuItem.id;
		var divMenuItem = document.createElement("div");
		//TODO:mc_
		divMenuItem.id = "mc_" + menuItem.id;
	
		var divMenuIcon = document.createElement("div");
		//TODO:mi_
		divMenuIcon.id = "mi_" + menuItem.id;
		divMenuIcon.className = "menuItemIcon_blank";
		
		divMenuItem.appendChild(divMenuIcon);
		
		var title = menuItem.directoryTitle || menuItem.title;
		var divMenuTitle = document.createElement("div");
		divMenuTitle.id = "m_" + menuItem.id;
		if (menuItem.href && !menuItem.linkDisabled) {
			var aTag = document.createElement('a');

			aTag.href = menuItem.href;
			aTag.appendChild(document.createTextNode(title));
			if(menuItem.display == "self") {
				aTag.target = "_self";
			} else if(menuItem.display == "newwindow"){
				aTag.target = "_blank";
			} else {
				if(menuItem.display == "inline")
					aTag.target="ifrm";
				var aTagOnClick = function(e) {
					IS_Portal.buildIFrame(aTag);
				}
				IS_Event.observe(aTag, "click", aTagOnClick, false, "_menu");
			}
			IS_Event.observe(aTag, "mousedown", function(e){Event.stop(e);}, false, "_menu");
			divMenuTitle.appendChild(aTag);
		}else{
			divMenuTitle.appendChild(document.createTextNode(title));
		}
		divMenuTitle.className = "menuTitle";
		
		divMenuItem.appendChild(divMenuTitle);
		
		if ( menuItem.type ){
//			var handler = IS_SiteAggregationMenu.menuDragInit(menuItem, divMenuIcon, divMenuItem);
			var handler = IS_SiteAggregationMenu.getDraggable(menuItem, divMenuIcon, divMenuItem);

			IS_Event.observe(menuLi, "mousedown", function(e){
				Event.stop(e);
			}, false, "_menu");	

			var returnToMenuFunc = IS_SiteAggregationMenu.getReturnToMenuFuncHandler( divMenuIcon, menuItem.id, handler );
			var displayTabName = IS_SiteAggregationMenu.getDisplayTabNameHandler( divMenuIcon, menuItem.id, handler, returnToMenuFunc, "_menu" );
			
			divMenuIcon.className = "menuItemIcon";
			menuLi.style.cursor = "move";
			IS_Widget.setIcon(divMenuIcon, menuItem.type, {multi:menuItem.multi});
			
			if(IS_Portal.isChecked(menuItem) && !/true/.test(menuItem.multi)){
				handler.destroy();
				Element.addClassName(divMenuIcon, 'menuItemIcon_dropped');
				IS_Event.observe(divMenuIcon, 'mouseover', displayTabName, false, "_menu");
				menuLi.style.cursor = "default";
			}

			//The time of 200 to 300millsec is lost because addListener execute new Array
			function getPostDragHandler(menuItemId, handler){
				return function(){ postDragHandler(menuItemId, handler);};
			}
			function postDragHandler(menuItemId, handler){
				//fix 209 The widget can not be dropped to a tab sometimes if it is allowed to be dropped plurally.
				if( /true/i.test( menuItem.multi ) )
					return;
				
				try{
//					Event.stopObserving(menuLi, "mousedown", handler, false);
					handler.destroy();
					
					Element.addClassName(divMenuIcon, 'menuItemIcon_dropped');
					
					$("mc_" + menuItemId).parentNode.style.background = "#F6F6F6";
					//$("m_" + menuItemId).style.color = "#5286bb";
					
					IS_Event.observe(divMenuIcon, 'mouseover', displayTabName, false, "_menu");
				}catch(e){
					msg.debug(IS_R.getResource(IS_R.ms_menuIconException,[menuItemId,e]));
				}

				menuLi.style.cursor = "default";
			}
			IS_EventDispatcher.addListener('dropWidget', menuItem.id, getPostDragHandler(menuItem.id,handler), true);
			if( menuItem.properties && menuItem.properties.url ) {
				var url = menuItem.properties.url;
				IS_EventDispatcher.addListener( IS_Widget.DROP_URL,url,( function( menuItem,handler ) {
						return function( widget ) {
							if( !IS_Portal.isMenuType( widget,menuItem )) return;
							
							postDragHandler(menuItem.id, handler);
						}
					})( menuItem,handler ) );
			}
			
			function getCloseWidgetHandler(menuItemId, handler){
				return function(){ closeWidgetHandler(menuItemId, handler);};
			}
			function closeWidgetHandler(menuItemId, handler){
				try{
//					IS_Event.observe(menuLi, "mousedown", handler, false, "_menu");
					Event.observe(handler.handle, "mousedown", handler.eventMouseDown);
					IS_Draggables.register(handler);
					
					Element.removeClassName(divMenuIcon, 'menuItemIcon_dropped');
					
//					divMenuIcon.className = (/MultiRssReader/.test(menuItem.type))? "menuItemIcon_multi_rss" : "menuItemIcon_rss";
					menuLi.style.cursor = "move"
					
					divMenuIcon.title = "";
					IS_Event.stopObserving(divMenuIcon, 'mouseover', displayTabName, false, "_menu");
				}catch(e){
					msg.debug(IS_R.getResource(IS_R.ms_menuIconException,[menuItemId,e]));
				}
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
		}
	
		if ( IS_SiteAggregationMenu.menuItemTreeMap[menuItem.id] && IS_SiteAggregationMenu.menuItemTreeMap[menuItem.id].length > 0) {
			var divSubMenuIcon = document.createElement("div");
			divSubMenuIcon.className = "subMenuIcon";
			divMenuItem.appendChild(divSubMenuIcon);
		}
	
		menuLi.appendChild(divMenuItem);
		IS_Event.observe(menuLi,"mouseout", getMenuItemMOutHandler(menuLi), false, "_menu");
		IS_Event.observe(menuLi,"mouseover", getMenuItemMoverFor(menuLi, menuItem), false, "_menu");
		return menuLi;
	}

	function getMenuItemMoverFor(node, menuItem){
		return function(e){
			menuItemMouseOver(e, node, menuItem);
		};
	}

	function getMenuItemMOutHandler(node){
		return function(e){ menuItemMOut(e, node); };
	}
	function menuItemMOut(e, targetElement) {
		var el = window.event ? targetElement : e ? e.currentTarget : null;
		if (!el || IS_Portal.isItemDragging) return;
		
		clearTimeout(el.outTimeout);
		clearTimeout(el.overTimeout);
		el.style.background = "#F6F6F6";
		//el.style.color = "#5286BB";
		el.outTimeout = setTimeout(function() { menuItemMOut2(el); }, 150);
	}
	var scrollers = [];
	function menuItemMOut2(el){
		for (var i = 0; i < el.childNodes.length; i++) {
			var node = el.childNodes[i];
			if (node.nodeName.toLowerCase() == 'ul') { 
				node.style.display = 'none';
				node.style.visibility = 'hidden';
			}
		}
		
		// Processing for creating child menu that has scroll controller everytime
		if(scrollers[el.id]) {
			// Delete all child menus
			var childUls = getChildrenByTagName(el, 'ul');
			for(var i = 0; i < childUls.length;i++){
				if(childUls[i].parentNode)
					childUls[i].parentNode.removeChild(childUls[i]);
			}
			// Make child menu to not built status.
			IS_SiteAggregationMenu.menuItemList[el.id].isChildrenBuild = false;
			// 
			currentDisplayParentItem = undefined;
			// Delete from array
			delete scrollers[el.id];
		}
	}

	function getCloseMenuFor(ul){
		return function(e){ closeMenuSet(e, ul);}
	}
	function  closeMenuSet(e, ul){
		var uls = getChildrenByTagName(ul.parentNode, "ul");
		for(var i = 0; i < uls.length; i++){
			uls[i].style.display = 'none';
			uls[i].style.visibility = 'hidden';
		}
	}
	
	function menuItemMouseOver(e, parent, parentMenuItem) {
		var parent = window.event ? parent : e ? e.currentTarget : null;
		if (!parent) return;
		clearTimeout(parent.outTimeout);
		clearTimeout(parent.overTimeout);
		parent.style.background = "#6495ED";
		parent.overTimeout = setTimeout(function() {menuItemMouseOver2(e, parent, parentMenuItem)}, 150);
	}
	
	var currentDisplayParentItem;
	var currentDisplayFlag = false;	// Flag for obtaining currentDisplayParentItem that used for only menuItemMouseOver2
	function menuItemMouseOver2(e, parent, parentMenuItem) {
		if(Browser.isIE){
			IS_Portal.setMouseMoveEvent();
			
			// Delete child menu that is displayed until just before / Do not delete if child menu of itself
			if(currentDisplayParentItem && (parentMenuItem.id != currentDisplayParentItem.id)){
				if(!currentDisplayFlag){
					var isDelete = true;
					var displayedItemChildList = IS_SiteAggregationMenu.menuItemTreeMap[currentDisplayParentItem.id];
					if(displayedItemChildList){
						for(var i=0;i<displayedItemChildList.length;i++){
							if(parentMenuItem.id == displayedItemChildList[i]){
								// Do not delte if displayed item is child of it.
								isDelete = false;
								break;
							}
						}
					}
					if(isDelete){
						var el = $(currentDisplayParentItem.id);
						el.style.background = "#F6F6F6";
						if(el) menuItemMOut2(el);
					}
					currentDisplayFlag = true;
					if(IS_SiteAggregationMenu.menuItemTreeMap[parent.id])
						currentDisplayParentItem = parentMenuItem;
				}
			}else if(!currentDisplayParentItem){
				currentDisplayParentItem = parentMenuItem;
			}else{
				currentDisplayFlag = true;
			}
			var parentParentItem = IS_SiteAggregationMenu.menuItemList[parentMenuItem.parentId];
			if(!parentParentItem.parentId){
				currentDisplayFlag = false;
			}
		}
		
		var childList = IS_SiteAggregationMenu.menuItemTreeMap[parent.id];
		if(!childList || childList.length == 0)return;
		
		if(!parentMenuItem.isChildrenBuild){
			parentMenuItem.isChildrenBuild = true;
			var ul = document.createElement("ul");
			ul.className = "menuGroup";
		
			var childUls = getChildrenByTagName(parent, 'ul');
			if(!childUls || childUls.length == 0){
				for(var j = 0; j < childList.length;j++){
					if(j == 0){
						var headerDiv = createMenuHeader(ul, parentMenuItem, false);
						if(headerDiv) {
							//#2759 Drop icon in the second level of tree and icon of next menu is stick together
							if(Browser.isFirefox) headerDiv.style.marginBottom = '4px';
							ul.appendChild(headerDiv);
						}
					}
					
					var menuItem = IS_SiteAggregationMenu.menuItemList[childList[j]];
					ul.appendChild(makeMenu(menuItem));
				}
				parent.appendChild(ul);
			}
			//Adjusting width
			var ulWidth = 100;
			var lis = getChildrenByTagName(ul, 'li');
			for(var j = 0; j < lis.length; j++){
				var divs = getChildrenByTagName(lis[j].firstChild, "div");
				for(var k = 0; k < divs.length; k++){
					if(divs[k].className=="menuTitle"){
						var titleWidth = divs[k].offsetWidth;
						var liWidth = titleWidth + 30;
						if(ulWidth < liWidth){
							ulWidth = liWidth;
						}
					}
				}
			}
			var tables = ul.getElementsByTagName('table');
			for(var i=0;i<tables.length;i++){
				var tableWidth = tables[i].offsetWidth + 10;
				if(ulWidth < tableWidth){
					ulWidth = tableWidth;
				}
			}
			
			ul.style.width = ulWidth + "px";
		}
		var childUls = getChildrenByTagName(parent, "ul");
		
		for(var i = 0; i < childUls.length; i++){
			// Adjusting Y axis
			setChildY( childUls[i], parent );
		}
	}
	
	function setChildY( childUl, parent ) {
		var offset = Browser.isIE ? 22 : 18;//Handling side scroll display.
		//var windowY = getWindowSize(false) - findPosY($("portal-maincontents-table")) + offset;
		var windowY = getWindowSize(false) - findPosY(IS_SiteAggregationMenu.displayTopLi) - IS_SiteAggregationMenu.displayTopLi.offsetHeight - offset;
		var parentTop = findPosY(parent.parentNode) - findPosY(IS_SiteAggregationMenu.displayTopLi) - IS_SiteAggregationMenu.displayTopLi.offsetHeight;
		
		childUl.style.display ="block";
		childUl.style.visibility = 'visible';
		
		// Back to the original place.
		childUl.style.left = (parent.offsetLeft + parent.offsetWidth);
		
		if((findPosX(childUl) + childUl.offsetWidth ) > (getWindowSize(true) - 25) ){
			childUl.style.left = (parent.offsetLeft - childUl.offsetWidth);
		}
		
		//Adjusting Y axis
		var ulHeight = childUl.offsetHeight;
//		var liTop = findPosY(parent);
		var liTop = parent.offsetTop;
		if( ulHeight > windowY && !scrollers[parent.id]){
			// Fit to maximum if the item does not fit to screen.
//			childUl.style.top = (findPosY(parent.parentNode.parentNode) * -1) + "px";
//			childUl.style.top = (findPosY(IS_SiteAggregationMenu.displayTopLi) - IS_SiteAggregationMenu.displayTopLi.offsetHeight) - parentTop;
			scrollers[parent.id] = new IS_SiteAggregationMenu.Scroller(childUl);
			//The height of childUl get lower if Scroller is newed.
			//Use same calculation of getting height as it placed under the if statement when the mouse is overed again
			childUl.style.top = (windowY - childUl.offsetHeight) - parentTop;
//			setChildY( childUl, parent );
		}else if( (liTop + ulHeight) > windowY ){
			// Adjust bottom of menu if it does not fit to bottom of browser.
//			childUl.style.top = (windowY - ulHeight) + "px";
			childUl.style.top = (windowY - ulHeight) - parentTop;
		}else{
			// Ordinally(Adjust Top to the place of parent
			childUl.style.top = liTop + "px";
		}
	}
	
	function createMenuHeader( ul, parentMenuItem, createCloseIcon){
		var headerDiv = document.createElement("div");
		headerDiv.className = "menuHeader";
		
		var headerTable = document.createElement("table");
		headerTable.cellSpacing = 0;
		headerTable.cellPadding = 0;
		headerTable.style.width = "100%";
		headerTable.style.marginTop = "2px";
		if(Browser.isIE) headerTable.style.marginBottom = "4px";
		var headerTbody = document.createElement("tbody");
		var headerTr = document.createElement("tr");
		var closeTd = document.createElement("td");
		headerTable.appendChild(headerTbody);
		headerTbody.appendChild(headerTr);
		headerDiv.appendChild(headerTable);
		
		var appended = createMultiDragHandle(headerTr, ul, parentMenuItem, headerTable );
		
		if(createCloseIcon){
			var closeIcon = document.createElement("img");
			closeIcon.className = "closeMenu";
			closeIcon.src = imageURL+"x.gif";
	
			if(!appended){
				var blankTd = document.createElement("td");
				blankTd.style.width = "100%";
				headerTr.appendChild(blankTd);
			}
			
			headerTr.appendChild(closeTd);
			closeTd.appendChild(closeIcon);
			IS_Event.observe(closeIcon,"mouseover", getCloseMenuFor(ul), false, "_menu");
		}
		
		if(!appended && !createCloseIcon) return null;
		
		return headerDiv;

		function createMultiDragHandle( tr, ul, parentMenuItem, handle ){
			var childList = IS_SiteAggregationMenu.menuItemTreeMap[parentMenuItem.id];
			var hasWidget = false;
			for(var i = 0; i < childList.length ;i++){
				var childItem = IS_SiteAggregationMenu.menuItemList[childList[i]];
				if(childItem.type){
					hasWidget = true;
					break;
				}
			}
			if(!hasWidget) return false;
			
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
			
			tr.appendChild(folderIconTd);
			tr.appendChild(folderFeedTitleTd);
			
//			var dragHandler = IS_SiteAggregationMenu.menuDragInit(parentMenuItem, ul, ul, true);
/*
			IS_Event.observe(folderIconTd,"mousedown", dragHandler, false, "_menu");
			IS_Event.observe(folderFeedTitleTd,"mousedown", dragHandler, false, "_menu");
*/			
			IS_SiteAggregationMenu.getMultiDropDraggable(ul, parentMenuItem, handle);
			
			return true;
		}
	}
};

IS_SiteAggregationMenu.getMultiDropDraggable = function(dragElement, menuItem, handle){
	return new IS_Draggable(dragElement,{
			handle:handle,
			scroll:window,
			revert:false,
			ghosting: true,
			dragMode: "menu",
			widgetType: menuItem.type,
			menuItem: menuItem,
			click: true,
			zindex: 10000,
			onStart: function(){
//				IS_SiteAggregationMenu.closeMenu();
				setTimeout(IS_SiteAggregationMenu.closeMenu, 100);
			},
			/*onStart: function(draggble, e){
				var element = draggble.element;
				
//				IS_Droppables.findWizPos(element);
				
				var divWidgetDummy = document.createElement("div");
				element.dummy = divWidgetDummy;
				
				// Create dummy for menu
				divWidgetDummy.style.display = "none";
				element.parentNode.insertBefore(divWidgetDummy, element);
				
				element = document.body.appendChild(element);
				
				var widgetGhost = IS_Draggable.ghost;
				if(widgetGhost.col.firstChild){
					widgetGhost.col.insertBefore(widgetGhost,widgetGhost.col.firstChild);
				}else{
					widgetGhost.col.appendChild(widgetGhost);
				}
			},*/
			getDropObject: function(){
				return this.menuItem;
			},
			getDummy: function(){
				var dummyNode = this.cloneNode(true);
				dummyNode.style.left = 0;
				dummyNode.style.top = 0;
				return dummyNode;
			}.bind(dragElement),
			startDroppableElement: document.body
		});
}

IS_SiteAggregationMenu.MergeMode = {
	merge		:	1,
	remain		:	2,
	remainNew	:	3
}

IS_SiteAggregationMenu.createMultiDropConf = function(element, lastActiveElement, menuItem, event, originFunc, modalOption, dropTab){
	var widgetGhost = IS_Draggable.ghost;
	var ghostColumnNum = (widgetGhost.col)? widgetGhost.col.getAttribute("colNum"):1;
	var tabId = (dropTab)? dropTab.id : IS_Portal.currentTabId;
	
	// Drop all
	var parentItem = menuItem;
	
	var children = parentItem.children;
	
	var isExistMulti = false;
	var existsItemList = {};
	var existsItemTitleArray = [];	// Take the title away from menu as the name of Widget can be changed.
	var oldParents = [];
	for(var i = 0; i < children.length ;i++){
		var feedNode = children[i];
		
		var tempFeed = IS_Portal.searchWidgetAndFeedNode(feedNode.id);
		//if(tempFeed &&( /RssReader/.test( feedNode.type )|| /MultiRssReader/.test( feedNode.type )) ){
		if(tempFeed){
			existsItemList[feedNode.id] = tempFeed;
			existsItemTitleArray.push( feedNode.title );
			
			oldParents[feedNode.id] = tempFeed.parent;
		}
		if(/MultiRssReader/.test(feedNode.type)){
			isExistMulti = true;
		}
	}
	
	var confs = new Array(2);
	
	var w_id;
	if(isExistMulti){
		w_id = tabId+"_p_" + menuItem.id;
	}else{
		w_id = "p_" + (new Date()).getTime();
	}
	var widgetConf = IS_WidgetsContainer.WidgetConfiguration.getConfigurationJSONObject(
		"MultiRssReader", w_id, ghostColumnNum, parentItem.title, parentItem.href, parentItem.properties);
	
	var subWidgets = [];
	var otherWidgets = [];
	confs[0] = widgetConf;
	confs[1] = subWidgets;
	confs[2] = otherWidgets;
	
	var hasCurrentTab = false;	// Whether the widget of same ID is existing at droppting ahead.
	var hasOtherTab = false;
	for(var num=0; num < IS_Portal.tabList.length; num++){
		var tab = IS_Portal.tabList[num];
		/*
		var widgetList = IS_Portal.widgetLists[tab.id];
		if( widgetList[ IS_Portal.getTrueId( w_id )] ) {
		*/
		if(IS_Portal.getWidget(w_id, tab.id)){
			if( tab.id == tabId ) {
				hasCurrentTab = true;
			} else {
				hasOtherTab = true;
			}
		}
	}
	
	var MergeMode = IS_SiteAggregationMenu.MergeMode;
	
	if( !modalOption ) {
		if( existsItemTitleArray.length > 0 ) {
			// Ask if it is marged in other cases.
			
			var ghostParent = widgetGhost.parentNode;
			var ghostNextSibling = widgetGhost.nextSibling;
//			widgetGhost.parentNode.removeChild(widgetGhost);
			element.style.display = "none";
			
			var contentPane = document.createElement("div");
			Element.addClassName( contentPane,"preference");
			
			var modalElement = document.createElement("div");
			var modal = new Control.Modal( modalElement,{
				contents: contentPane,
				containerClassName:"preference",
				overlayCloseOnClick:false
			});
			
			var dialogPane = document.createElement("div");
			contentPane.appendChild( dialogPane );
			
			var messagePane = document.createElement("div");
			messagePane.style.padding = "5px";
			dialogPane.appendChild( messagePane );
			
			var message = document.createElement("h3");
			message.innerHTML = IS_R.ms_gatheringWidgets;
			messagePane.appendChild( message );
			
			var description = document.createElement("div");
			description.style.margin = "5px";
			
			var descStr;
			if( existsItemTitleArray.length < 4 ) {
				descStr = "["+existsItemTitleArray.join(",")+"]";
			} else {
				descStr = "["+existsItemTitleArray.slice(0,3).join(",")+",etc ... ]";
			}
			description.appendChild( document.createTextNode( descStr ));
			messagePane.appendChild( description );
			
			var opt = new Object();
			opt[ IS_R.lb_gathering ] = MergeMode.merge;
			opt[ IS_R.lb_notGathering ] = MergeMode.remain;
			var options = $H( opt );
			var optionPane = document.createElement("div");
			optionPane.style.textAlign = "center";
			dialogPane.appendChild( optionPane );
			
			var optionList = document.createElement("div");
			optionPane.appendChild( optionList );
			options.each( function( entry,index ) {
				var option = document.createElement("input");
				option.type = "button";
				option.value = entry.key;
				option.style.margin = "5px";
				
				optionList.appendChild( option );
				
				function handleClick( event ) {
					var clickedElement = Event.element( event );
					
					modal.close();
//					IS_Portal.isItemDragging = true;
					if( ghostNextSibling ) {
						ghostParent.insertBefore( widgetGhost,ghostNextSibling );
					} else {
						ghostParent.appendChild( widgetGhost );
					}
					
					modalOption = entry.value;
					originFunc( element, lastActiveElement, menuItem, event, originFunc, modalOption );
					
					IS_Event.stopObserving( clickedElement,'click',handleClick );
				}
				IS_Event.observe( option,'click',handleClick, false );
			});
			
			modal.open();
			
			return;
		}
		/*
		else if(IS_Portal.isWidgetInTab( tabId, w_id )){
			originFunc( element, lastActiveElement, menuItem, event, originFunc, MergeMode.remain );
			return;
		}
		*/
		
	} // end of (!modalOption)
	
	if( hasCurrentTab && modalOption == MergeMode.remain ) {
		modalOption = MergeMode.remainNew;
	}
	
	var trueId = IS_Portal.getTrueId(w_id, "MultiRssReader");
	for(var i = 0; i < children.length ;i++){
		var feedNode = children[i];
		
		if(feedNode.type){
			if(/RssReader/.test( feedNode.type )) {
				//The feed or merge that is not checked is child
				var check = ( modalOption == MergeMode.merge )|| !IS_Portal.isChecked( feedNode );
				var addConf = true;
				var isRelated = true;
				var isChecked = true;
				var preCheck = check;
				
				if( check ) {
					IS_Widget.deleteWidget(feedNode.id, false, false, true);
					
					preCheck = check;
					isRelated = check && !/^RssReader/.test( feedNode.type );
				}else if (modalOption != MergeMode.merge){
					// The checked items 
					isChecked = false;
					
					// None cooperative menu is not necessary to be added to FEED
					var tempFeed = existsItemList[feedNode.id];
					if(tempFeed && tempFeed.widgetConf)
						addConf = (tempFeed.widgetConf.property.relationalId == trueId);
					
					isRelated = addConf;
				}
				
				if(addConf){
					var feedConf = IS_WidgetsContainer.WidgetConfiguration.getFeedConfigurationJSONObject(
						"RssReader", "w_" + feedNode.id, feedNode.title, feedNode.href, isChecked?"true":"false", feedNode.properties);
					feedConf.preCheck = preCheck;
					feedConf.widgetType = feedNode.type;
					feedConf.parentId = IS_Portal.getTrueId(w_id);
					feedConf.menuId = feedNode.id;
					subWidgets.push(feedConf);
				}
			} else {
				var addConf = true;
				var tempFeed = existsItemList[feedNode.id];
				if(modalOption != MergeMode.merge) {
					//The dropped widget is not included in the list if it is not marged.
					addConf = !(tempFeed && tempFeed.widgetConf);
				} else if(tempFeed && tempFeed.widgetConf){
					//Delete the dropped widget if it is merged.
					IS_Widget.deleteWidget(feedNode.id, false, false, true);
				}
				if(addConf){
					feedNode.menuId = feedNode.id;
					otherWidgets.push(feedNode);
				}
			}
		}
	}
	
	if( subWidgets.length > 0 ) {
		if( modalOption == MergeMode.remainNew ) {
			var newWidgetConf = IS_WidgetsContainer.WidgetConfiguration.getConfigurationJSONObject(
				"MultiRssReader","w_"+new Date().getTime(),ghostColumnNum, IS_R.lb_newBox, "", null);
			
			var newSubWidgets = [];
			
			$A( subWidgets ).each( function( feedConf ){
				var type = feedConf.widgetType;
				if( type && type.match("RssReader$")) {
					if( feedConf.preCheck ) {
						newSubWidgets.push( feedConf );
					}
				}
			});
			
			confs[0] = newWidgetConf;
			confs[1] = newSubWidgets;
		}
		// If the cooperative menu is remained.
		else if( MergeMode.merge && IS_Portal.isWidgetInTab( tabId, w_id )){
			// Move the property and title by creating empty MultiWidget.
			var tempTargetWidget = IS_Portal.getWidget( w_id, tabId );
			var tempWidgetConf = eval('('+ tempTargetWidget.widgetConf.toJSON() +')');//TODO:Is eval necesassry?
			tempWidgetConf.id = "p_" + new Date().getTime();
			
			var newExistWidget = IS_WidgetsContainer.addWidget(tabId, tempWidgetConf, tempTargetWidget.isBuilt);
			var tempRssReaders = IS_Widget.getDisplayOrderList(tempTargetWidget, tabId);
			
			var childMenuIdList = menuItem.owner.menuItemTreeMap[IS_Portal.getTrueId(w_id)];
			if(!childMenuIdList) childMenuIdList = [];
			
			var tempSiblingId = "";
			for(var i=0;i<tempRssReaders.length;i++){
				// Determine cooperative menu or not.
				var tempRssReaderId = IS_Portal.getTrueId(tempRssReaders[i].id);
				var menuId = tempRssReaderId.substring(2);
				var isMenuChild = childMenuIdList.contains(menuId);
				
				if( !isMenuChild ) {
					if(tempTargetWidget.isBuilt)
						newExistWidget.content.addSubWidget(tempRssReaders[i]);
						
					IS_Portal.removeSubWidget(tempTargetWidget.id, tempRssReaders[i].id, tabId);
					IS_Portal.addSubWidget(newExistWidget.id, tempRssReaders[i].id, tabId);
					
					IS_Widget.setWidgetLocationCommand(tempRssReaders[i]);
				}
			}
			if (tempTargetWidget.isBuilt) {
				tempTargetWidget.headerContent.close("notCloseFeeds");
			}else{
				IS_Portal.removeWidget(w_id, tabId);
			}
			
			if( newExistWidget.getUserPref("displayMode") == "time") {
				if( IS_Portal.currentTabId == tabId ) {
					newExistWidget.loadContents();
				} else {
					newExistWidget.onTabChangeReload = true;
				}
			}
			
			IS_EventDispatcher.newEvent("applyIconStyle", newExistWidget.id );
		}

		/*
		else if( IS_Portal.isWidgetInTab( tabId, w_id )){
			// Move property and title by creating empty MultiWidget
			var tempTargetWidget = IS_Portal.getWidget( w_id )
			var tempWidgetConf = eval('('+tempTargetWidget.widgetConf.toJSONString()+')');
			tempWidgetConf.feed = new Array();
			tempWidgetConf.id = "p_" + new Date().getTime();
			var newExistWidget = IS_WidgetsContainer.addWidget(targetTabId, tempWidgetConf, tempTargetWidget.isBuilt);
			
			if(tempTargetWidget.isBuilt){
				var tempRssReaders = tempTargetWidget.content.getRssReaders();
				var tempSiblingId = "";
				for(var i=0;i<tempRssReaders.length;i++){
					if(tempRssReaders[i].widgetConf.property.relationalId != IS_Portal.getTrueId(w_id, "MultiRssReader")){
						newExistWidget.content.addRssReader(tempRssReaders[i], tempSiblingId);
						tempSiblingId = tempRssReaders[i].id;
					}
				}
				tempTargetWidget.headerContent.close("notCloseFeeds");
			}else{
				// Not built
				var tempFeeds = tempTargetWidget.widgetConf.feed;
				var tempSiblingId = "";
				for(var i=0;i<tempFeeds.length;i++){
					if(tempFeeds[i].property.relationalId != IS_Portal.getTrueId(w_id, "MultiRssReader")){
						IS_Widget.addAiryRssReader(newExistWidget, tempFeeds[i], tempSiblingId, targetTabId);
						tempSiblingId = tempFeeds[i].id;
					}
				}
//				IS_Portal.widgetLists[targetTabId][w_id] = null;
				IS_Portal.removeWidget(w_id, targetTabId);
			}
		}
		*/
	}
	
	var isCreate = false;
	// Check if there is subWidget or not.
	/*
	if(confs[1].length > 0){
		for(var i=0;i<confs[1].length;i++){
//			if(getBooleanValue(confs[1][i].isChecked)){
				isCreate = true;
				break;
//			}
		}
	}
	*/
	
	if( confs[1].length == 0 && confs[2].length == 0) {
		//Finish if there is no feed to be displayed.
		widgetGhost.parentNode.removeChild(widgetGhost);
		return null;
	}else{
		var multiId = confs[0].id;
		var childList = menuItem.owner.menuItemTreeMap[IS_Portal.getTrueId(multiId).substring(2)];
		
		if(childList){
			var childMenuList = [];
			for(var i = 0; i < childList.length ;i++){
				var feedNode = IS_TreeMenu.findMenuItem( childList[i] );
				if(feedNode.type && /MultiRssReader/.test(feedNode.type)){
					childMenuList.push(feedNode.id);
					
					var oldParent = oldParents[ feedNode.id];
					if( oldParent && oldParent.content && oldParent.content.isTimeDisplayMode() ) {
						oldParent.loadContents();
					}
				}
			}
			
			confs[0].property = {};
			confs[0].property.children = childMenuList;
		}
	}
	return confs;
}

IS_SiteAggregationMenu.refreshMenu = function  () {
	if(IS_Portal.isItemDragging) {
		setTimeout(IS_SiteAggregationMenu.refreshMenu, 500);
	} else {
		//document.getElementById("portal-site-aggregation-menu").innerHTML = "";
		IS_SiteAggregationMenu.isMenuRefreshed = true;
		IS_Event.unloadCache("_menu");
		//TODO:The message of SideMenu is disappered if sideMenuURL != siteAggregationMenuURL as well.
		IS_Portal.closeMsgBar();
		
		// Delete event that registered in EventDispatcher
		for(i in IS_SiteAggregationMenu.menuItemList){
			if(IS_SiteAggregationMenu.menuItemList[i] && IS_SiteAggregationMenu.menuItemList[i].id){
				IS_EventDispatcher.removeListenerList("closeWidget", IS_SiteAggregationMenu.menuItemList[i].id);
				IS_EventDispatcher.removeListenerList("dropWidget",  IS_SiteAggregationMenu.menuItemList[i].id);
			}
		}
		
		new IS_SiteAggregationMenu();
		
		if( displaySideMenu == 'reference_top_menu' ) {
			IS_Portal.treeMenuObject = false;
		}
	}
};

IS_SiteAggregationMenu.draggable = false;
IS_SiteAggregationMenu.tempReaders = {};
/*
IS_SiteAggregationMenu.menuDragInit = function(menuItem, menuIconDiv, menuItemDiv, isMultiMenu, isTree){
//	return function(e){ menuItemDragStart(e, menuItem, menuIconDiv, menuItemDiv); };
	
	menuItem.menuIconDiv = menuIconDiv;
	menuItem.menuItemDiv = menuItemDiv;
	
	var dragObject = new Object();
	for(var i in menuItem){
		dragObject[i] = menuItem[i];
	}
	dragObject.menuIconDiv = menuIconDiv;
	dragObject.menuItemDiv = menuItemDiv;
	dragObject.isMultiMenu = isMultiMenu;
	
	if(isTree){
		dragObject.isTree = true;
	}
	
	var drag = new IS_DragWidget(dragObject, "menu");
	return drag.start;
};
*/

IS_SiteAggregationMenu.getConfigurationFromMenuItem = function(menuItem, columnNum){
	var w_id;
	if(/true/i.test(menuItem.multi)){
		w_id = "w_" + (new Date()).getTime();
	}else{
		w_id = "w_" + menuItem.id;
	}
	var widgetConf;
	widgetConf = IS_WidgetsContainer.WidgetConfiguration.getConfigurationJSONObject(
		menuItem.type, w_id, columnNum, menuItem.title, menuItem.href, menuItem.properties);
	widgetConf.menuId = menuItem.id;
	
	return widgetConf;
}

IS_SiteAggregationMenu.getDraggable = function(menuItem, menuIconDiv, menuItemDiv, isMultiMenu, isTree,viewport ){
	menuItemDiv.className = "menuItem";
	
	return new IS_Draggable(menuItemDiv,{
		scroll:window,
		viewport: viewport,
		revert:false,
		ghosting: true,
		dragMode: "menu",
		widgetType: menuItem.type,
		menuItem: menuItem,
		click: true,
		zindex: 10000,
		dummyWidth: "30%",
		ignoreDisplayPanel: true,
		onStart: function(draggble, e){
			if( IS_Widget.MaximizeWidget && IS_Widget.MaximizeWidget.headerContent )
				IS_Widget.MaximizeWidget.headerContent.turnbackMaximize();
			
			var element = draggble.element;
			
//			var divWidgetDummy = element.cloneNode(true);
//			element.dummy = divWidgetDummy;
			
			this.menuUL = element.parentNode.parentNode;
			
			// Handling that same widget is dropped plurally.
			//if (this.menuItem.multi) this.menuItem.id = this.menuItem.type+ "_" + new Date().getTime();
			
//			//Create dummy for menu.
//			divWidgetDummy.id = "divWidgetDummy_" + this.menuItem.id;
//			divWidgetDummy.style.position = "";
//			element.parentNode.insertBefore(divWidgetDummy, element);
//			
//			divWidgetDummy.style.display = "";
//			element = document.body.appendChild(element);
//			element.style.width = "30%";
			
			var widgetGhost = IS_Draggable.ghost;
			if(/MultiRssReader/.test(this.menuItem.type)){
				var parentItem = this.menuItem.parent;
				var p_id = IS_Portal.currentTabId+"_p_" + parentItem.id;
				var divParent = $(p_id);
				if ( divParent ) {
					// Execurte at coordinating menu.
//					var targetWidget = IS_Portal.widgetLists[IS_Portal.currentTabId][p_id];
					var targetWidget = IS_Portal.getWidget(p_id, IS_Portal.currentTabId);
					
					this.syncId = targetWidget.elm_widget.id;
					if (targetWidget.content.isTimeDisplayMode()) {
						targetWidget.elm_widgetBox.oldClassName = targetWidget.elm_widgetBox.className;
						targetWidget.elm_widgetBox.className = "dropToParent";
					}
					
					// Adding ghost to the head of targetWidget
					var subCol = targetWidget.elm_widgetContent.firstChild;
					subCol.insertBefore(widgetGhost, subCol.firstChild);
					
					widgetGhost.menuItem = this.menuItem;
				}
			}
/*				}else {
					if(widgetGhost.col.firstChild){
						widgetGhost.col.insertBefore(widgetGhost,widgetGhost.col.firstChild);
					}else{
						widgetGhost.col.appendChild(widgetGhost);
					}
				}
			}else{
				if(widgetGhost.col.firstChild){
					widgetGhost.col.insertBefore(widgetGhost,widgetGhost.col.firstChild);
				}else{
					widgetGhost.col.appendChild(widgetGhost);
				}
			}*/
		},
		onDrag: function(draggble, e){
			if(!e) return;
			
			var mouseX = Event.pointerX(e);
			var mouseY = Event.pointerY(e);
			
			// Check if mouse is out of menu
			if(IS_Portal.menuOver){
				var menuX = findPosX( this.menuUL );	// Menu left top x axis
				var menuY = findPosY( this.menuUL );	// Menu left top y axis
				var menuX2 = menuX + this.menuUL.offsetWidth;	// Menu right bottom x axis
				var menuY2 = menuY + this.menuUL.offsetHeight;	// Menu right bottom y axis
				if( (mouseX < menuX || menuX2 < mouseX) || (mouseY < menuY || menuY2 < mouseY)){
					IS_SiteAggregationMenu.closeMenu();
					IS_Portal.menuOver = false;
				}
			}
		},
		onEnd: function(draggble, e){
			this.syncId = false;
			var p_id = IS_Portal.currentTabId+"_p_" + this.menuItem.parent.Id;
			var targetWidget = IS_Portal.getWidget(p_id, IS_Portal.currentTabId);
			if (targetWidget && targetWidget.getUserPref("displayMode") == "time") {
				targetWidget.elm_widgetBox.className = targetWidget.elm_widgetBox.oldClassName;
			}
		},
		getDropObject: function(){
			return this.menuItem;
		},
		getGhost : function(){
			var widgetGhost = document.createElement("div");
			widgetGhost.id = "widgetGhost";
			widgetGhost.col = IS_Portal.columnsObjs[IS_Portal.currentTabId]["col_dp_1"];
			widgetGhost.style.height = "1.1em";
			return widgetGhost;
		},
		startDroppableElement: document.body
	});
}

if( Browser.isSafari1 ) {
	// The fix for the error if the menu and sitemap is dropped by clicking
	IS_SiteAggregationMenu.getDraggable = ( function() {
		var getDraggable = IS_SiteAggregationMenu.getDraggable;
		
		return function() {
			var draggableObj = getDraggable.apply( this,$A( arguments ) );
			var onEnd = draggableObj.options.onEnd;
			
			draggableObj.options.onEnd = function( draggable,e ) {
				onEnd.apply( this,$A( arguments ) );
				
				var ele = draggable.element;
				var styleObj = ele.style;
				styleObj.top = styleObj.left = styleObj.bottom = styleObj.right = 0;
				
				var dummyDiv = document.createElement("div");
				var parentNode = ele.parentNode;
				parentNode.replaceChild( dummyDiv,ele );
				document.createElement("div").appendChild( ele );
				
				setTimeout( function() {
					parentNode.replaceChild( ele,dummyDiv )
				},10 );
			}
			
			return draggableObj;
		}
	})();
}

IS_SiteAggregationMenu.closeBubble = function(el) {
	for (var i = 0; i < el.childNodes.length; i++) {
		var node = el.childNodes[i];
		var nodeName = node.nodeName.toLowerCase();
		if (nodeName == 'ul') {
			node.style.display = 'none';
			node.style.visibility = 'hidden';
		} else if(nodeName == 'li' && node.className == 'menuItem') {
			node.style.background = "#F6F6F6";
			node.style.color = "#5286BB";
		} else if(nodeName == 'a' && el.className == "topMenuLi") {
			//node.style.backgroundImage = "url(" + imageURL + "lev0_bg1.png)";
		}
		IS_SiteAggregationMenu.closeBubble(node);
	}
}

IS_SiteAggregationMenu.closeMenu = function() {
	if(IS_SiteAggregationMenu.displayTopLi) IS_SiteAggregationMenu.closeBubble(IS_SiteAggregationMenu.displayTopLi);
}

IS_SiteAggregationMenu.resetMenu = function(){
	for(var i = 0; i < IS_SiteAggregationMenu.topMenuIdList.length; i++){
		var menuItem = IS_SiteAggregationMenu.menuItemList[IS_SiteAggregationMenu.topMenuIdList[i]];
		menuItem.isChildrenBuild  = false;
		resetMenu2(menuItem);
		
	}
	function resetMenu2(parent){
		var childIds = IS_SiteAggregationMenu.menuItemTreeMap[parent.id];
		if(!childIds)return;
		for(var i = 0; i < childIds.length; i++){
			var menuItem = IS_SiteAggregationMenu.menuItemList[childIds[i]];
			menuItem.isChildrenBuild = false;
			resetMenu2(menuItem);
		}
	}
}

IS_SiteAggregationMenu.Scroller = IS_Class.create();
IS_SiteAggregationMenu.Scroller.prototype.classDef = function() {
	var self = this;
	
	this.initialize = function(ulElement) {
		this.element = {
			me: ulElement,
			posY: findPosY(ulElement),
			posX: findPosX(ulElement),
			offsetWidth: ulElement.offsetWidth,
			offsetHeight: ulElement.offsetHeight,
			childLis: getChildrenByTagName(ulElement, "li")
		};
		this.init();
	}
	
	this.init = function() {
		// Scroll is not necessary if there is no li tag(child).
		if(this.element.childLis.length == 0) return;
		
		this.calcCapacity();
		this.displayUpButton();
		this.displayDownButton();
		this.rebuildContents();
	}
	
	this.rebuildContents = function() {
		this.startPos = 0;
		this.displayChildLis();
		// For disabling the button above.
		this.scrollUp();
	}
	
	/*
		Calculate how many li tag can be inserted.
	*/
	this.calcCapacity = function() {
		this.capacity = 0;
		
		// The height of a li tag.
		var liHeight = this.element.childLis[0].offsetHeight;
		if(Browser.isIE)
			liHeight += 2;
		
		// Distance between the first li tag to teh bottom of browser.
		var distance = getWindowSize(false) - findPosY(this.element.childLis[0]);
		
		// Calculate
		var liCapacity = 0;
		while(0 < distance){
			distance -= liHeight;
			liCapacity++;
		}
		
		// Minus considering btottun
		var tmpCapacity = (liCapacity - 3);
		
		if(tmpCapacity < 0){
			this.capacity = liCapacity;
		}else{
			this.capacity = tmpCapacity;
		}
	}
	
	/*
		Display li tag for Capacity
	*/
	this.displayChildLis = function() {
		// Delete a li tag that is displayed.
		removeChildLis();
		
		var liCapacity = this.capacity;
		var count = -1;
		while(0 < liCapacity){
			count++;
			if(count < this.startPos) continue;
			
			// Insert before DOWN button.
			if(this.element.childLis[count])
				this.element.me.insertBefore(this.element.childLis[count], this.element.me.lastChild);
			liCapacity--;
		}
		
		function removeChildLis() {
			var lis = getChildrenByTagName(self.element.me, "li");
			for(var i=0; i<lis.length; i++)
				self.element.me.removeChild(lis[i]);
		}
	}
	
	/*
		Display UP button
	*/
	this.displayUpButton = function() {
		if(this.upButton != null){
			var existUpButton = document.getElementsByClassName("menuUpButton", this.element.me );
			if(this.element.me.firstChild && existUpButton.length == 0){
				this.element.me.insertBefore(this.upButton, this.element.me.firstChild);
			}
		}else{
			this.buildUpButton();
			this.displayUpButton();
		}
	}
	
	/*
		Craete UP button
	*/
	this.buildUpButton = function() {
		var buttonDiv = document.createElement("div");
		buttonDiv.id = this.element.childLis[0].id + "_menuUpButton";
		buttonDiv.className = "menuUpButtonDisabled";
		buttonDiv.style.width = "100%";
		
		var button = document.createElement("button");
		buttonDiv.appendChild(button);
		button.style.width = "100%";
		button.style.fontSize = "80%";
		
		IS_Event.observe(button, "mousedown", mouseDownHandler, false, "_menu");
		IS_Event.observe(button, "mouseup", clearTimer, false, "_menu");
		IS_Event.observe(button, "blur", clearTimer, false, "_menu");
		IS_Event.observe(button, "mouseover", mouseOverHandler, false, "_menu");
		IS_Event.observe(button, "mouseout", mouseOutHandler, false, "_menu");
		
		this.upButton = buttonDiv;
		
		var scrollUpTimer;
		function mouseDownHandler(e) {
			mousedownFunc(e);
			scrollUpHandler();
		}
		function mouseOverHandler(e) {
			setBackgroundColor(button, "#ccc");
		}
		function mouseOutHandler(e) {
			setBackgroundColor(button, "transparent");
			clearTimer();
		}
		function scrollUpHandler() {
			self.scrollUp();
			scrollUpTimer = setTimeout(scrollUpHandler, 100);
		}
		function clearTimer() {
			if(scrollUpTimer)
				clearTimeout(scrollUpTimer);
		}
	}
	
	/*
		Display DOWN button
	*/
	this.displayDownButton = function() {
		if(this.downButton != null){
			var existDownButton = document.getElementsByClassName("menuDownButton", this.element.me);
			if(existDownButton.length == 0){
				this.element.me.appendChild(this.downButton);
			}
		}else{
			this.buildDownButton();
			this.displayDownButton();
		}
	}
	
	/*
		Create Down button
	*/
	this.buildDownButton = function() {
		var buttonDiv = document.createElement("div");
		buttonDiv.id = this.element.childLis[0].id + "_menuDownButton";
		buttonDiv.className = "menuDownButton";
		buttonDiv.style.width = "100%";
		
		var button = document.createElement("button");
		buttonDiv.appendChild(button);
		button.style.width = "100%";
		button.style.fontSize = "80%";
		
		IS_Event.observe(button, "mousedown", mouseDownHandler, false, "_menu");
		IS_Event.observe(button, "mouseup", clearTimer, false, "_menu");
		IS_Event.observe(button, "blur", clearTimer, false, "_menu");
		IS_Event.observe(button, "mouseover", mouseOverHandler, false, "_menu");
		IS_Event.observe(button, "mouseout", mouseOutHandler, false, "_menu");
		
		this.downButton = buttonDiv;
		
		var scrollDownTimer;
		function mouseDownHandler(e) {
			mousedownFunc(e);
			scrollDownHandler();
		}
		function mouseOverHandler(e) {
			setBackgroundColor(button, "#ccc");
		}
		function mouseOutHandler(e) {
			setBackgroundColor(button, "transparent");
			clearTimer();
		}
		function scrollDownHandler() {
			self.scrollDown();
			scrollDownTimer = setTimeout(scrollDownHandler, 100);
		}
		
		function clearTimer() {
			if(scrollDownTimer)
				clearTimeout(scrollDownTimer);
		}
	}
	
	/*
		Change back color
	*/
	function setBackgroundColor(elem, color) {
		elem.style.backgroundColor = color;
	}
	
	/*
		Scroll to top
	*/
	this.scrollUp = function() {
		$(this.element.childLis[0].id + "_menuDownButton").className = "menuDownButton";
		if(this.startPos == 0){
			$(this.element.childLis[0].id + "_menuUpButton").className = "menuUpButtonDisabled";
			return;
		}
		this.startPos -= 1;
		this.displayChildLis();
	}
	
	/*
		Scroll to down
	*/
	this.scrollDown = function() {
		$(this.element.childLis[0].id + "_menuUpButton").className = "menuUpButton";
		if((this.startPos + this.capacity) == this.element.childLis.length){
			$(this.element.childLis[0].id + "_menuDownButton").className = "menuDownButtonDisabled";
			return;
		}
		this.startPos += 1;
		this.displayChildLis();
	}
	
	/*
		Prevent from transmitting event.
	*/
	function mousedownFunc(e){
		if(window.event){
			window.event.cancelBubble = true;
		}
		if(e && e.stopPropagation){
			e.stopPropagation();
		}
	}
}

IS_SiteAggregationMenu.getReturnToMenuFuncHandler = function(iconElement, menuId, handler){
	var func = function(menuItemId, handler){
		//closeWidgetHandler(menuItemId, handler);
		var closeWidget = IS_Portal.searchWidgetAndFeedNode(menuItemId);
		if(closeWidget){
			var p = closeWidget.parent;
			if(closeWidget.isBuilt) {
				closeWidget.headerContent.close();
			} else{
				if(closeWidget.content && closeWidget.content.close) {
					closeWidget.content.close(e, notAddTrash);
				}
				
				closeWidget.widgetConf.deleteDate = new Date().getTime();
				
				//Send to Server
				IS_Widget.removeWidgetCommand(closeWidget);
				
				//Add to trash box
				IS_Portal.Trash.add(closeWidget);
				
				IS_EventDispatcher.newEvent("loadComplete", closeWidget.id );//Update icon of parent does not stop if the item is deleted while loading
				IS_EventDispatcher.newEvent('closeWidget', closeWidget.id.substring(2), null);
				try{AjaxRequest.cancel(closeWidget.id);}catch(ex){msg.error(ex);}
				try{IS_Event.unloadCache(closeWidget.id);}catch(ex){msg.error(ex);}
				try{IS_Event.unloadCache(closeWidget.closeId);}catch(ex){msg.error(ex);}
			
				if(p && IS_Portal.getSubWidgetList(p.id, p.tabId)){
					p.widgetConf.deleteDate = closeWidget.widgetConf.deleteDate;
					IS_Portal.Trash.add(p);
					IS_EventDispatcher.newEvent("loadComplete", p.id );//Update icon of parent does not stop if the item is deleted while loading
					IS_EventDispatcher.newEvent('closeWidget', p.id.substring(2), null);
					try{AjaxRequest.cancel(p.id);}catch(ex){msg.error(ex);}
					try{IS_Event.unloadCache(p.id);}catch(ex){msg.error(ex);}
					try{IS_Event.unloadCache(p.closeId);}catch(ex){msg.error(ex);}
					IS_Widget.removeWidgetCommand(p);
				}
			}
			
			if( p && /MultiRssReader/.test( p.widgetType ) &&
				p.isBuilt && p.content.isTimeDisplayMode() &&
				p.content.getRssReaders().length > 0 ) {
				if( IS_Portal.currentTabId == p.tabId ) {
					p.loadContents();
				} else {
					p.onTabChangeReload = true;
				}
			}
		}
	}.bind(iconElement, menuId, handler);
	
	return func;
}

IS_SiteAggregationMenu.getDisplayTabNameHandler = function(iconElement, menuId, handler, closeHandler, eventKey){
	var func = function(menuItemId, handler){
		var closeWidget = IS_Portal.searchWidgetAndFeedNode(menuItemId);
		if(closeWidget){
			this.title = IS_R.getResource(IS_R.ms_revertWidget,[IS_Portal.tabs[closeWidget.tabId].name]);
			IS_Event.observe(this, 'click', closeHandler, false, eventKey);
		}
	}.bind(iconElement, menuId, handler);
	
	return func;
}
