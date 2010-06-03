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

IS_MySiteMap = Class.create();
IS_MySiteMap.prototype = {
	initialize: function() {
		this.id = "mySiteMap_" +new Date().getTime();
	},
	build: function( container ) {
		var root = document.createElement("div");
		root.className = "mySiteMap";
		container.appendChild( root );
		
		root.innerHTML = "<table><tr><td></td></tr><td></td></tr></table>";
		var table = root.firstChild;
		table.className = "rootTable";
		table.cellPadding = "0";
		table.cellSpacing = "0";
		
		this.renderShowHideAll( table.firstChild.childNodes[0].firstChild );
		table.firstChild.childNodes[1].firstChild.className = "rootTableContent";
		this.renderTabList( table.firstChild.childNodes[1].firstChild );
		
		this.root = root;
	},
	rebuild: function() {
		if( !this.root ) return;
		
		var p = this.root.parentNode;
		p.removeChild( this.root );
		
		this.emptyTabs.clear();
		
		this.build( p );
	},
	attachShowHideEventHandler: function( doShow, showAll, hideAll ){
		if (doShow) {
			showAll.style.display = "none";
			hideAll.style.display = "block";
		}
		else {
			showAll.style.display = "block";
			hideAll.style.display = "none";
		}

		for (var i = 0; i < IS_Portal.tabList.length; i++) {
			var tab = IS_Portal.tabs[IS_Portal.tabList[i].id];
			if (!tab || this.emptyTabs.contains(tab.id)) 
				continue;
			
			var ul = $( this.id+"_tab_"+tab.id+"_list" );
			if(!ul) continue;
			
			this.changeTab(ul, tab, doShow);
		}
	},
	renderShowHideAll: function( container ) {
		
		var refresh = document.createElement("div");
		this.renderRefreshButton( refresh );
		container.appendChild( refresh );
				
		var showAll = document.createElement("div");
		showAll.className = "showAll";
		showAll.appendChild(document.createTextNode(IS_R.lb_deployAll));
		showAll.style.display = "none";
		container.appendChild( showAll );
		
		var hideAll = document.createElement("div");
		hideAll.className = "hideAll";
		hideAll.appendChild(document.createTextNode(IS_R.lb_closeAll));
		container.appendChild( hideAll );
		
		Event.observe(showAll, 'click', this.attachShowHideEventHandler.bind(this, true, showAll, hideAll),false,"_widgetsMap");
		Event.observe(hideAll, 'click', this.attachShowHideEventHandler.bind(this, false, showAll, hideAll),false,"_widgetsMap");
	},
	renderRefreshButton: function( container ) {
		var button = document.createElement("div");
		button.className = "menuRefresh";

		button.title = IS_R.lb_refresh;
		
		Event.observe( button,"click",this.rebuild.bind( this ));
		
		container.appendChild( button );
	},
	emptyTabs : [],
	renderTabList: function( container ) {
		var ol = document.createElement("ol");
		ol.id = this.id+"_root";
		ol.className = "tabList";
		
		for(var i=0;i<IS_Portal.tabList.length;i++){
			var tab = IS_Portal.tabs[IS_Portal.tabList[i].id];
			if( !tab ) continue;
			
			var li = document.createElement("li");
			li.id = this.id+"_tab_"+tab.id;
			li.className = "tabListItem openTab";
			ol.appendChild( li );
			
			this.renderTab( li,tab );
		}
		
		container.appendChild( ol );
	},
	renderTab: function( container,tab ) {
		var title = document.createElement("div");
		title.className = "tabName";
		container.appendChild( title );
		
		var titleLabel = document.createElement("a");
		titleLabel.className = "label";
		titleLabel.id = this.id+"_tab_"+tab.id+"_title";
		titleLabel.title = IS_R.ms_showTabByClick;
		titleLabel.appendChild( document.createTextNode( tab.name ));
		titleLabel.href = "javascript:void(0)";
		title.appendChild( titleLabel );
		Event.observe( titleLabel,"click",function( e ) {
			Event.stop( e );
			
			this.focusTab( tab )
		}.bind( this ) );
		
//		Event.observe( title,"click",this.toggleTab.bind( this,tab ));
		
		container.appendChild( title );
		
		var ul = document.createElement("ul");
		ul.id = this.id+"_tab_"+tab.id+"_list";
		ul.className = "tabWidgetList";
		ul.title = IS_R.ms_showWidgetByClick;
//		if( tab.id != IS_Portal.currentTabId )
//			ul.style.display = "none";
		
		container.appendChild( ul );
		
		var widgetsList = IS_Portal.widgetLists[tab.id];
		
		var isEmptyTab = true;
		for( var i in widgetsList ) {
			var widget = widgetsList[i];
			if( !widget || !widget.id || widget.widgetConf.parentId || IS_Portal.CommandBar.isCommandBarWidget(widget)) continue;
			
			var li = document.createElement("li");
			li.id = this.id+"_widget_"+widget.id;
			li.className = "tabWidgetListItem";
			
			if((/MultiRssReader/.test(widget.widgetType))){
				var typeConf = IS_Widget.getConfiguration( widget.widgetType );
				var widgetBackgroundColor = typeConf.backgroundColor;
				if(widgetBackgroundColor){
					try {
						li.style.backgroundColor = widgetBackgroundColor;
					} catch(e) {
						divWidgetContent.style.backgroundColor = "white";
						msg.error(IS_R.getResource(IS_R.ms_widgetBkgcolorError, [self.widgetType, self.title]));
					}
				}
					
			}
			
			ul.appendChild( li );
			
			this.renderWidget( li,tab,widget );
			isEmptyTab = false;
		}
		
		if (isEmptyTab) {
			this.emptyTabs.push(tab.id);
			container.className = "tabListItem emptyTab";
		}else{
			Event.observe( title,"click",this.toggleTab.bind( this,tab ));
		}
	},
	renderWidget: function( container,tab,widget ) {
		var div = document.createElement("div");
		div.className = "widgetName label";
		
		var divIcon = $.DIV({Class:'menuItemIcon menuItemIcon_dropped'});
		IS_Widget.setIcon(divIcon, widget.widgetType);
		div.appendChild(divIcon);

		var widgetTitle = IS_Widget.WidgetHeader.getTitle(widget);
		div.appendChild( document.createTextNode( widgetTitle || widget.widgetConf.title || IS_R.lb_notitle ) );
		
		Event.observe( div,"click",this.focusWidget.bind( this,tab,widget ));
		
		container.appendChild( div );
		
		var ul = document.createElement("ul");
		ul.className = "tabSubWidgetList";
		
		var subWidgets = IS_Portal.getSubWidgetList( widget.id,tab.id );
		var hasChild = false;
		for( var i in subWidgets ) {
			var subWidget = subWidgets[i];
			if( !subWidget || !subWidget.id ) continue;
			
			var li = document.createElement("li");
			li.className = "tabWidgetListItem";
			ul.appendChild( li );
			hasChild = true;
			
			this.renderWidget( li,tab,subWidget );
		}
		
		if(hasChild)
			container.appendChild( ul );
	},
	changeTab: function(ul, tab, display){
		ul.style.display = display ? "":"none";
		var li = $( this.id+"_tab_"+tab.id );
		li.addClassName( display ?"openTab":"closeTab");
		li.removeClassName( !display ?"openTab":"closeTab");
	},
	toggleTab: function( tab ) {
		var ul = $( this.id+"_tab_"+tab.id+"_list" );
		if( !ul ) return;
		
		var display = (ul.style.display == "none");
		this.changeTab(ul, tab, display);
	},
	focusWaiting: function() {
		if( this.focusWait )
			return true;
		
		this.focusWait = true;
		setTimeout( this.clearFocusWait.bind( this ),500 );
		
		return false;
	},
	clearFocusWait: function() {
		this.focusWait = false;
	},
	focusTab: function( tab ) {
		if( this.focusWaiting())
			return;
		
		var tabDiv = IS_Portal.tabList.find( function( tabDiv ) {
			return tabDiv.id == tab.id;
		});
		
		if( !tabDiv )
			return this.rebuild();
		
		if( IS_Widget.MaximizeWidget && IS_Widget.MaximizeWidget.headerContent )
			IS_Widget.MaximizeWidget.headerContent.turnbackMaximize();
		
		IS_Portal.controlTabs.setActiveTab( tabDiv );
	},
	focusWidget: function( tab,widget ) {
		if( !widget.isBuilt || !widget.elm_widget || tab.id != IS_Portal.currentTabId ) {
			this.focusTab( tab );
			
			setTimeout( function() {
				if( !widget.isBuilt ) return;
				
				this._focusWidget( tab,widget );
			}.bind( this ),1000 );
			
			return;
		}
		
		this._focusWidget( tab,widget );
	},
	_focusWidget: function( tab,widget ) {
		if( this.focusWaiting())
			return;
		
		if( !widget.elm_widget || !widget.elm_widget.parentNode )
			return this.rebuild();
		
		if( IS_Widget.MaximizeWidget && IS_Widget.MaximizeWidget.headerContent )
			IS_Widget.MaximizeWidget.headerContent.turnbackMaximize();
		
		widget.blink();
		
		var y = findPosY( widget.elm_widget );
		var height = widget.elm_widget.offsetHeight;
		var wh = getWindowHeight();
		
		if( height < wh ) {
			document.body.scrollTop = y -( wh -height )/2;
		} else {
			document.body.scrollTop = y;
		}
	}
}
