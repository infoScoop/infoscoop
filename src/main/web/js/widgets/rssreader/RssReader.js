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

IS_Widget.RssReader = IS_Class.create();
IS_Widget.RssReader.prototype.classDef = function() {
	var widget;
	var self;
	var isStatic;
	var isDroppable;
	
	var pageSize = is_getPropertyInt(rssPageSize, -1);
	
	this.initialize = function(widgetObj){
		self = this;
		widget = widgetObj;
		isStatic = widget.panelType == "StaticPanel" && widget.isStaticHeight;
		isDroppable = (isStatic || !(widget.id.indexOf("previewWidget_") < 0));
		
		// Flag for EditGridReader
		this.isRssReader = true;
		
		if(isStatic){
//			setTimeout(widget._setStaticWidgetHeight, 10);
			widget.initUserPref("scrollMode", "scroll");
		}
		
		this.maxItemLength = 100;
		this.rssItemLength = 0;
		
		if(!widget.latestDatetime || widget.latestDatetime == ""){
			widget.latestDatetime = "";
		}
		
		if(!widget.widgetPref.dateTimeFormat){
			widget.widgetPref.dateTimeFormat = new Object();
			widget.widgetPref.dateTimeFormat.name = "dateTimeFormat";
			widget.widgetPref.dateTimeFormat.datatype = "string";
			widget.widgetPref.dateTimeFormat.value = "";
		}
		if(!widget.widgetPref.dateTimeFormat.value) widget.widgetPref.dateTimeFormat.value = "";
		
		this.titleFilter = widget.getUserPref('titleFilter');
		this.creatorFilter = widget.getUserPref('creatorFilter');
		this.categoryFilter = widget.getUserPref('categoryFilter');
		
		//Load custom icons for every item
		if(widget.widgetPref.rssItemCustomIcons && widget.widgetPref.rssItemCustomIcons.value){
			try{
				if(Browser.isIE){
					var rssItemCustomIconsFunc = eval("[" + widget.widgetPref.rssItemCustomIcons.value + "]");
					this.rssItemCustomIcons = rssItemCustomIconsFunc[0];
				} else {
					var rssItemCustomIconsFunc = eval("("+widget.widgetPref.rssItemCustomIcons.value+")");
					this.rssItemCustomIcons = rssItemCustomIconsFunc;
				}
			}catch(e){
				msg.error("failed to create custom icons in "+ widget.widgetType +":" + getErrorMessage(e));
			}
		}
		
		//Temporary
		if(!widget.parent) {
			var scrolling = IS_WidgetConfiguration[widget.widgetType].scrolling;
			if(scrolling){
				if ( /true/.test(scrolling) || /TRUE/.test(scrolling) ) {
					scrolling = true;	
				}else {
					scrolling = false;
				}
				if(scrolling) {
					var contents = widget.elm_widgetContent ;
					if(contents)
					  contents.style.width= "auto";
				}
			}
		}
		
		var this_ = this;
		this.rssContent = new IS_Widget.RssReader.RssContent( widget, pageSize,{
				getRequestOption : function( pageNo ) {
					this_.loadContentsOption.preLoad();
					
					var opt =  Object.extend({
						
						//requestHeaders: this_.loadContentsOption.requestHeaders.concat( ["X-IS-PAGE",pageNo ] )
						requestHeaders: [] // nazo
					},this_.loadContentsOption );
					opt.requestHeaders = this_.loadContentsOption.requestHeaders.concat( ["X-IS-PAGE",pageNo ] );
					
					return opt
				}
			},{
				onLoadPageCompleted : this.handleLoadPageCompleted.bind( this )
			});

		IS_EventDispatcher.addListener("adjustedColumnWidth", null, this.repaintIfCurrentTab.bind(this,false,false,true ) );
		
		IS_EventDispatcher.addListener("dragWidget", widget.id,this.repaint.bind( this ) );
		
		this.droppableOption = {};
		
		//Process of dragging
		var opt = {
//			accept : ["widget", "subWidget"],
			accept : function(element, widgetType, classNames){
				if (widget.tabId != IS_Portal.currentTabId) {
					return false;
				}else {
					return (classNames.detect(function(v){
						return ["widget", "subWidget"].include(v)
					}) &&
					(widgetType == "MultiRssReader" || widgetType == "RssReader") &&
					!IS_Draggables.keyEvent.isPressing.ctrl &&
					!widget.parent &&
					( element.id && ( element.id != widget.id )));
				}
			},
			onHover: function(element, dropElement, dragMode, point) {
				if(!this.targetWidget)
//					this.targetWidget = IS_Portal.widgetLists[IS_Portal.currentTabId][dropElement.id];
					this.targetWidget = IS_Portal.getWidget(dropElement.id);
				if(!this.targetWidget || this.targetWidget.parent
				   || element.id == this.targetWidget.id //for Safari
				   ) return false;
				this.targetWidget.elm_widgetBox.className = "dropToParent";
				var widgetGhost = IS_Draggable.ghost;
				widgetGhost.style.display = "none";
			},
			outHover: function(element, dropElement, point) {
				if(!this.targetWidget) return false;
				this.targetWidget.elm_widgetBox.className = "widgetBox";
				this.targetWidget = null;
				var widgetGhost = IS_Draggable.ghost;
				widgetGhost.style.display = "block";
			}
		};
		
		this.droppableOption.onWidgetDrop = function(element, lastActiveElement, draggedWidget, event, modalOption) {
			if(!this.targetWidget) this.targetWidget = widget;
			if(!this.targetWidget) return false;
			
			if (!IS_Droppables.mergeConfirm.call(self, element, lastActiveElement, draggedWidget, event, self.droppableOption.onWidgetDrop, function(){
				widget.elm_widgetBox.className = "widgetBox";
			}, widget.title, modalOption)) {
				return false;
			}
			element.style.display = "";
			
			// Check for merging
			var targetWidget = this.targetWidget;
			if(!targetWidget.parent){
				IS_EventDispatcher.newEvent("changeConnectionOfWidget", targetWidget.id);
			}
			
			var widgetGhost = IS_Draggable.ghost;
			if(widgetGhost){
				widgetGhost.parentNode.removeChild(widgetGhost);
			}
			
			var oldParentWidget = draggedWidget.parent;
			var newWidget = IS_Widget.RssReader.createMultiRssReader(draggedWidget, targetWidget);
			targetWidget.elm_widgetBox.className = "widgetBox";
			// Show title edit
//			newWidget.headerContent.showTitleEditorForm();

			
			IS_EventDispatcher.newEvent("applyIconStyle", widget.id );
			IS_EventDispatcher.newEvent("applyIconStyle", draggedWidget.id );
			
			if(oldParentWidget){
				// In case of subCategory, remove the source before moving
//					oldParentWidget.content.removeRssReader(widget.id, false, true);
				IS_Portal.removeSubWidget(oldParentWidget.id , draggedWidget.id);
				
				if(oldParentWidget.content)
					oldParentWidget.content.checkAllClose(true);
			}else{
				IS_EventDispatcher.newEvent("changeConnectionOfWidget", draggedWidget.id);
			}
			
			IS_EventDispatcher.newEvent("applyIconStyle", newWidget.id );
			this.targetWidget = null;
		}
		opt.onDrop = this.droppableOption.onWidgetDrop;
		opt.marginBottom = "10px";
		
		if(!widget.isMulti && !isDroppable)
		  IS_Widget.RssReader.dropGroup.add(widget.elm_widget, opt);
		
		var menuOpt = {}
		menuOpt = Object.extend(menuOpt, opt);
		menuOpt.accept = function(element, widgetType, classNames){
			if (widget.tabId != IS_Portal.currentTabId) {
				return false;
			}else {
				return (classNames.detect(function(v){
					return ["menuItem"].include(v)
				}) &&
				(widgetType == "MultiRssReader" || widgetType == "RssReader") &&
				!IS_Draggables.keyEvent.isPressing.ctrl &&
				IS_Draggables.activeDraggable &&
				!IS_Draggables.activeDraggable.options.syncId &&
				!widget.parent);
			}
		}
		this.droppableOption.onMenuDrop = function(element, lastActiveElement, menuItem, event, modalOption){
			var widgetGhost = IS_Draggable.ghost;
			var ghostColumnNum = (widgetGhost.col)? widgetGhost.col.getAttribute("colNum"):1;
			var widgetConf = IS_SiteAggregationMenu.getConfigurationFromMenuItem(menuItem, ghostColumnNum);
			widgetConf.type = "RssReader";
			
			// Check for merging
			if(!IS_Droppables.mergeConfirm.call(self, element, lastActiveElement, menuItem, event, self.droppableOption.onMenuDrop, function(){
					widget.elm_widgetBox.className = "widgetBox";
				}, widget.title, modalOption))
				return;
			
			var newWidget = IS_WidgetsContainer.addWidget( IS_Portal.currentTabId, widgetConf, false );
			
			IS_Portal.widgetDropped( newWidget );
			
			self.droppableOption.onWidgetDrop.call(self, element,lastActiveElement, newWidget, event, modalOption);
		}
		menuOpt.onDrop = this.droppableOption.onMenuDrop;
		menuOpt.marginBottom = "10px";
		
		if(!widget.isMulti && !isDroppable)
		  IS_Widget.RssReader.dropGroup.add(widget.elm_widget, menuOpt);

		IS_EventDispatcher.addListener('closeWidget', widget.id.substring(2),
			function(){
				IS_Widget.RssReader.dropGroup.remove(widget.elm_widget);
				IS_EventDispatcher.removeListenerList('reloadWidget', widget.getUserPref("url"), handleReloadWidgetEvent);
			}.bind(this), true
		);
		
		if(widget.getBoolUserPref('isMessageList')){
			IS_EventDispatcher.addListener('reloadWidget', widget.getUserPref("url"), handleReloadWidgetEvent);
		}
	}
	
	function handleReloadWidgetEvent(newUrl){
		if(newUrl) {
			var oldUrl = widget.getUserPref("url");
			IS_EventDispatcher.removeListenerList('reloadWidget', oldUrl);
			widget.setUserPref('url', newUrl);
			IS_EventDispatcher.addListener('reloadWidget', newUrl, handleReloadWidgetEvent);
		}
		widget.loadContents();
	}
	
	this.setupLoadContentsOption = function( loadContentsOption ) {
		var headers = []
		
		if( widget.widgetPref.dateTimeFormat ){
			var dateTimeFormat = eval( '('+ Object.toJSON(widget.widgetPref.dateTimeFormat) +')' );
			headers.push(["X-IS-DATETIMEFORMAT", encodeURIComponent( dateTimeFormat.value)]);
		}
		
		// Get the latest time when refreshed; turn around if the time is newer than it
		if( this.rssContent.rssItems.length > 0 && this.rssContent.rssItems[0].rssDate )
			widget.latestDatetime = this.rssContent.rssItems[0].rssDate.getTime();
		
		var nowDate = new Date();
		var logoffDatetime = parseInt( IS_Portal.logoffDateTime );
		
		// If logoffDatetime has initial value, Login initially or login as guest user
		// No need for calculating previous logout
		var freshTime = (logoffDatetime <= 0 || isNaN( logoffDatetime ))? 0 : nowDate.getTime() - logoffDatetime;
		var freshDaysTime = freshDays *24 *60 *60 *1000
		
		headers.push(["X-IS-FRESHTIME",nowDate.getTime() -Math.max( freshTime,freshDaysTime ) ]);
		
		if( rssMaxCount !== undefined )
			headers.push(["X-IS-RSSMAXCOUNT", encodeURIComponent(rssMaxCount)]);
		
		if( pageSize !== undefined )
			headers.push(["X-IS-PAGESIZE", encodeURIComponent( pageSize )]);

		var titleFilter = widget.getUserPref('titleFilter');
		if(titleFilter)
			headers.push(["X-IS-TITLEFILTER", encodeURIComponent(titleFilter)]);
		var creatorFilter = widget.getUserPref('creatorFilter');
		if(creatorFilter)
			headers.push(["X-IS-CREATORFILTER", encodeURIComponent(creatorFilter)]);
		var categoryFilter = widget.getUserPref('categoryFilter');
		if(categoryFilter)
			headers.push(["X-IS-CATEGORYFILTER", encodeURIComponent(categoryFilter)]);
		
		loadContentsOption.requestHeaders = headers.flatten();
		if( widget.widgetType == "MultiRssReader") {
			this.setupMergeRssLoadContentsOption( loadContentsOption );
		} else {
			this.setupRssLoadContentsOption( loadContentsOption );
		}
	}
	this.setupRssLoadContentsOption = function( loadContentsOption ) {
		loadContentsOption.url = is_getProxyUrl( widget.getUserPref("url"),"RssReader");
		loadContentsOption.method = "get";
	}
	this.setupMergeRssLoadContentsOption= function( loadContentsOption ) {
		loadContentsOption.url = "mltproxy";
		loadContentsOption.method = "post";
		loadContentsOption.contentType = "application/xml";
		
		if( this.getRssReaders ) {
			loadContentsOption.timeout = ajaxRequestTimeout *this.getRssReaders().length;
		} else {
			loadContentsOption.timeout = ajaxRequestTimeout * 3;
		}
		
		loadContentsOption.postBody = IS_Widget.MultiRssReader.getMultiProxyPostBody( widget )
	}
	this.buildRssItems = function(response) { 
		if( widget.parent && widget.isSuccess ){
			widget.parent.content.mergeRssReader.isComplete = false;
		}
		//var start = new Date();
		//Fix bug: no error occurs in spite of invalid rss format with userProxy=false, and it shows message of "no new information" 
		var rss = IS_Widget.parseRss( response );
		rss.isIntranet = /true/.test(response.getResponseHeader('X-IS-INTRANET'));
		
		var allError = false;
		if (rss) {
			this.rss = rss;
			this.rssContent.setRss(rss);
			
			// Get title and link from RSS if they are empty
			if(widget.headerContent && widget.isNoTitle && !widget.title_url){
				widget.title = rss.title;
				widget.title_url = rss.link;
				
				if(widget.title)
					IS_Widget.setWidgetPrefernceCommand(widget, "title", widget.title);
				if(widget.title_url)
					IS_Widget.setWidgetPrefernceCommand(widget, "href", widget.title_url);
				
				widget.headerContent.buildTitle();
			}
			
			if (rss.cacheHeaders) {
				if (!widget.cacheHeaders) 
					widget.cacheHeaders = {};
				
				$H(rss.cacheHeaders).each(function(siteCacheHeaderObj){
					var siteCacheHeader = {};
					$H(siteCacheHeaderObj.value).each(function(cacheHeader){
						siteCacheHeader[cacheHeader.key] = cacheHeader.value;
					});
					
					widget.cacheHeaders[siteCacheHeaderObj.key] = siteCacheHeader;
				});
			}
			
			if( widget.parent && widget.parent.content ) {
				rss.errorUrls = [];
				if( rss.errors ) {
					allError = true;
					rss.errorUrls = $H( rss.errors ).keys();
					widget.parent.content.getRssReaders().each( function( rssReader ) {
						var url = rssReader.getUserPref("url");
						if( !rss.errors[ url ])
							allError = false;
					});
				}
			}
		}
		
		
		// Add this line for error-handling of MergeRSS on Multi
		this.rss = rss;
		
		// Prepare to specify max item length
		//self.rssItemLength = (self.maxItemLength < rssItems.length)? self.maxItemLength : rssItems.length;
		var rssItemLength = (rss)? this.rssContent.rssItems.length : 0;
		
		//if( !widget.latestDatetime ){
		//	widget.latestDatetime = new Date().getTime();
		//}
		
		if( rssItemLength == 0) {
			IS_Event.unloadCache(widget.id);
			
			var contents = document.createElement("div");
			contents.className = "rssItem";
			
			if( widget.parent && widget.parent.content && widget.parent.content.mergeRssReader &&
				widget.parent.content.mergeRssReader.content == this && allError ) {
				// Only if it is MergeRssReader
				

				contents.innerHTML = IS_R.lb_noDiplayItem;
			} else {

				contents.innerHTML = IS_R.lb_noNewInfo;
			}
			
			this.elm_rssContent = contents;
			if(widget.elm_widgetContent.firstChild){
				widget.elm_widgetContent.replaceChild(contents, widget.elm_widgetContent.firstChild);
			}else{
				widget.elm_widgetContent.appendChild(contents);
			}
			if(! this.isOpenWidget()){
				widget.elm_widgetContent.style.display = "none";
			}

			if( isStatic ) {
				if(Browser.isIE){
					setTimeout(this.setStaticErrorHeight.bind(this, contents ), 0);
				}else{
					this.setStaticErrorHeight( contents );
				}
			}
		} else /*if( this.isOpenWidget() )*/{ 
			this.displayContents();
		}
		self.setLatestMark();
		
		if(this.accessStatsIcon && widget.widgetPref.useAccessStat && /true/i.test( widget.widgetPref.useAccessStat.value ) )
			this.accessStatsIcon.style.display = "block";
	};
	
	this.handleLoadPageCompleted = function( pageNo ) {
		if( pageNo > 0 && this.rssContentView &&
			this.rssContentView.elm_viewport.offsetHeight !== undefined ) {
			this.repaint( true,true );
			
			if( widget.maximizeRender && widget.maximizeRender.rssContentView )
				widget.maximizeRender.handleLoadPageCompleted()
		}
	}

	this.displayContents = function() {
		if( this.isError())
			return;
		
		IS_Event.unloadCache(widget.id);
		
		//IS_Widget.RssReader.RssItemRender.hideWidgetRssDesc(widget.id);
		
		/*
		if( widget.elm_widgetContent.firstChild )
			widget.elm_widgetContent.removeChild( widget.elm_widgetContent.firstChild );
		*/
		while( widget.elm_widgetContent.firstChild )
			widget.elm_widgetContent.removeChild( widget.elm_widgetContent.firstChild );
		
		
		if( widget.parent )
			widget.parent.content.initRssReader( widget );
		
		widget.elm_widgetContent.style.verticalAlign = "top";
		
		if( this.rssContentView )
			this.rssContentView.unloadCache();
		
		if( isStatic )
			this.renderItemsCountPanel();
		
		var render = IS_Widget.RssReader.RssItemRender;
		var renderContext = {
				doLineFeed : this.doLineFeed.bind( this ),
				showDatetime : this.showDatetime.bind( this )
			}
		this.rssContentView = new IS_Widget.RssReader.RssContentView( widget,this.rssContent,{
			height :  this.getHeight() + 'px',
			scrollable: !this.isNoneScrollMode(),
			render: render,
			renderContext: renderContext
		} );
		
		this.rssContentView.contentHeightChangeListener = this.handleRssContentViewContentHeightChange.bind( this );
		
		if( this.isNoneScrollMode() )
			this.rssContentView.elm_viewport.style.overflowY = "hidden";
		
		widget.elm_widgetContent.appendChild( this.rssContentView.elm_root );
		
		IS_Portal.widgetDisplayUpdated();
		
		if( this.isShowLatestNews() ) {
			this.showAllLatestItems();
		} else if( !this.isOpenWidget() || this.getItemsnum() == 0 ) {
			this.hideContent();
		} else {
			this.showContent();
		}
		
		this.rssContentView.view();
		this.rssContentView.onContentHeightChange();
	}

	this.setStaticErrorHeight = function( content ) {
		if( 0 < widget.staticWidgetHeight ){
			var widgetHeight = widget.staticWidgetHeight;
			if( this.itemsCountPanel && 0 < this.itemsCountPanel.offsetHeight ) {
				widgetHeight -= this.itemsCountPanel.offsetHeight +(Browser.isIE ? 1 : 0);
			} else {
				setTimeout( this.setStaticErrorHeight.bind( this,content ),100 );
			}
			
			if ( content.offsetHeight != widgetHeight)
				content.style.height = widgetHeight + 'px';
			
			return;
		} else {
			height = 1;
			
			setTimeout( this.setStaticErrorHeight.bind( this,content ),100 );
		}
	}
	this.renderItemsCountPanel = function() {
		if( !this.itemsCountPanel ) {
			this.itemsCountPanel = document.createElement("div");
			this.itemsCountPanel.id = widget.id+"_itemsCountPanel";
			this.itemsCountPanel.className = "RssReader_itemsCountPanel";
		} else {
			if( this.itemsCountPanel.parentNode )
				this.itemsCountPanel.parentNode.removeChild( this.itemsCountPanel );
			
			while( this.itemsCountPanel.firstChild )
				this.itemsCountPanel.removeChild( this.itemsCountPanel.firstChild );
		}
		
		var itemsCount = 0;
		if( this.rssContent && this.rssContent.rssItems )
			itemsCount = this.rssContent.rssItems.length;
		
		/*if( itemsCount > 0 ) {
			//Items count:{0}
			var itemsCountLabel = IS_R.getResource( IS_R.lb_itemCount,[ itemsCount ]);
			this.itemsCountPanel.appendChild( document.createTextNode(itemsCountLabel));
			this.itemsCountPanel.title = itemsCountLabel;
		}*/
		//Items count:{0}
		var itemsCountDiv = document.createElement('div');
		itemsCountDiv.className = 'RssReader_itemsCount';
		var itemsCountLabel = IS_R.getResource( IS_R.lb_itemCount,[ itemsCount ]);
		itemsCountDiv.appendChild( document.createTextNode(itemsCountLabel));
		itemsCountDiv.title = itemsCountLabel;
		
		//Filtering
		var filterDiv = document.createElement('div');
		filterDiv.className = 'RssReader_filter';
		var filterDesc = "";
		var titleFilter = widget.getUserPref('titleFilter');
		if(titleFilter)
			filterDesc += IS_R.lb_title+':"'+titleFilter+'" ';
		var creatorFilter = widget.getUserPref('creatorFilter');
		if(creatorFilter)
			filterDesc += IS_R.lb_creator+':"'+creatorFilter+'" ';
		var categoryFilter = widget.getUserPref('categoryFilter')
		if(categoryFilter)
			filterDesc += IS_R.lb_category + ':"'+categoryFilter+'" ';
		if(filterDesc) {
			filterDiv.appendChild( document.createTextNode(filterDesc));
			filterDiv.title = filterDesc;
			this.itemsCountPanel.appendChild(filterDiv);
		}
		this.itemsCountPanel.appendChild(itemsCountDiv);
		
		widget.elm_widgetContent.appendChild( this.itemsCountPanel );
	}
	
	this.postEdit = function(){
		var prevTitleFilter = this.titleFilter;
		var prevCreatorFilter = this.creatorFilter;
		var prevCategoryFilter = this.categoryFilter;
		
		this.titleFilter = widget.getUserPref('titleFilter');
		this.creatorFilter = widget.getUserPref('creatorFilter');
		this.categoryFilter = widget.getUserPref('categoryFilter');
		
		//If some changes happen in filter, go to loadContents
		if((prevTitleFilter != null && prevTitleFilter != this.titleFilter) ||
			(prevCreatorFilter != null && prevCreatorFilter != this.creatorFilter) ||
			(prevCategoryFilter != null && prevCategoryFilter != this.categoryFilter)) {
			widget.clearCache = true;
			widget.loadContents();
		} else {
			widget.clearCache = false;
		}
		
		this.displayContents();
	}
	
	this.close = function (e, notAddTrash) {
		/* 
		var defaultUserPref = IS_WidgetConfiguration["RssReader"].UserPref;
		for(var i in defaultUserPref) {
			if(typeof(defaultUserPref[i]) != "function")
				widget.setUserPref(i, defaultUserPref[i].defaultValue);
		}
		this.setLatestMark();
		*/
		if( this.rssContentView )
			this.rssContentView.unloadCache();
		
		var parent = widget.parent;
		if( parent ) {
			IS_Portal.deleteCacheByUrl(widget.parent.id + widget.getUserPref("url"));
			parent.content.mergeRssReader.isComplete = false;
			IS_Portal.removeSubWidget( parent,widget,parent.tabId );
			IS_EventDispatcher.newEvent("applyIconStyle", parent.id);
		}
		
		if(IS_Portal.rssSearchBoxList[widget.id]){
			if(IS_Portal.rssSearchBoxList[widget.id].parentNode){
				IS_Portal.rssSearchBoxList[widget.id].parentNode.removeChild( IS_Portal.rssSearchBoxList[widget.id] );
			}
			delete IS_Portal.rssSearchBoxList[widget.id];
		}
		
		if( parent ) {
			parent.content.checkAllClose(notAddTrash);
		}
	};
	
	this.increaseItem = function(){
		if( this.isShowLatestNews() )
			widget.setUserPref("showLatestNews", false);
		
		var itemsnum = this.getItemsnum();
		if( !this.isOpenWidget()){
			widget.setUserPref("openWidget", true);
			if( widget.headerContent )
				widget.headerContent.changeTurnbkIcon();
			
			itemsnum = 1;
		} else if( itemsnum < this.rssContent.rssItems.length ) {
			itemsnum += 1;
		}
		
		widget.setUserPref("itemsnum",itemsnum );
		
		if( this.isNoneScrollMode() ) {
			this.adjustNoneScrollModeHeight();
		} else if( this.rssContentView ){
			this.rssContentView.setViewportHeight( this.getHeight() );
		}
		
		if( itemsnum == 1 || this.isError() ) {
			this.showContent();
		} else {
			this.repaint( false,true,true );
		}
	}

	this.decreaseItem = function(){
		if( this.isError() )
			return;
		
		if( !this.isOpenWidget() )
			return;
		
		if( this.isShowLatestNews() )
			widget.setUserPref("showLatestNews", false);
		
		var itemsnum = this.getItemsnum();
		if( !( itemsnum < this.rssContent.rssItems.length ) )
			itemsnum = this.rssContent.rssItems.length;
		
		if( itemsnum > 0 )
			itemsnum -= 1;
		
		widget.setUserPref("itemsnum",itemsnum );
		
		if( this.isNoneScrollMode() ) {
			this.adjustNoneScrollModeHeight();
		} else if( this.rssContentView ){
			this.rssContentView.setViewportHeight( this.getHeight() );
		}
		
		if( itemsnum == 0 )
			this.hideContent();
		
		//IS_Widget.RssReader.RssItemRender.adjustRssDesc();
	}
	this.isOpenWidget = function() {
		return widget.getBoolUserPref("openWidget");
	}
	this.getItemsnum = function() {
		var itemsnum = widget.getUserPref("itemsnum");
		if( isNaN( itemsnum ))
			itemsnum = parseInt( itemsnum );
		
		// Use defaultValue if the value is invalid
		if( !itemsnum || itemsnum < 0 || isNaN( itemsnum ))
		//	itemsnum = IS_Widget.getConfiguration("RssReader").UserPref["itemsnum"].defaultValue;
			itemsnum = 0;
		
		return parseInt( itemsnum );
	}
	this.getHeightChangeUnit = function() {
		if( this.rssContentView )
			return this.rssContentView.render.getDefaultHeight( this.rssContentView.renderContext );
		
		return 44;
	}
	this.getHeight = function() {
		var itemsnum = this.getItemsnum();
		if( ( this.rssContentView && this.rssContent && this.rssContent.rssItems.length <= itemsnum ))
			itemsnum = this.rssContent.rssItems.length;
		
		var height = itemsnum *this.getHeightChangeUnit();
		
		return height;
	}
	this.getUrl = function() {
		return widget.getUserPref("url");
	}
	this.doLineFeed = function() {
		return widget.getBoolUserPref("doLineFeed");
	}
	this.showDatetime = function() {
		return widget.getBoolUserPref("showDatetime");
	}
	this.isShowLatestNews = function() {
		return widget.getBoolUserPref("showLatestNews");
	}
	
	this.isNoneScrollMode = function() {
		return ( widget.getUserPref("scrollMode") == "none")
	}
	
	this.displayItemHeader = function( itemCount,latestCount ) {
		if( ( this.isOpenWidget() && this.getItemsnum() > 0 )&&
			!( this.getLatestItemCount() == 0 && this.isShowLatestNews() ) )
			return;
		
		var itemCount = this.rssContent.rssItems.length;
		var latestCount = getLatestItemsLength();
		
		var header = widget.elm_latestMark;
		while( header.firstChild )
			header.removeChild( header.firstChild );
		
		if( this.rssContent.rssItems.length == 0 )
			return;
		
		header.style.fontSize = "14px";
		header.appendChild( document.createTextNode("["));
		if( latestCount > 0 ) {
			header.className = "latestMark";
			var latestMark = document.createElement("img");
			latestMark.src = imageURL +( isHot() ? "sun_blink.gif":"sun.gif");
			

			header.title = IS_R.lb_showAllLatestItems;
			
			header.appendChild( latestMark );
			
			header.appendChild( document.createTextNode( latestCount +"/" ));
		} else {
			header.className = "";
			header.title = "";
		}
		
		header.appendChild( document.createTextNode( itemCount +"]"));
		
		header.style.display = "block";
	}
	
	this.stopLatestMarkRotate = function(){
		msg.debug( ( /MultiRssReader/.test( widget.widgetType )? widget.parent.title : widget.title )
			+"( "+( /MultiRssReader/.test( widget.widgetType )? widget.parent.id : widget.id )+" )"
			+" is not modified.");
		
		var imgs = widget.elm_widgetContent.getElementsByTagName( "img" );
		// content
		for(var i=0; i<imgs.length; i++){
			if(/sun_blink.gif$/.test( imgs[i].src )) {
				imgs[i].src = imageURL+"sun.gif";
			}
		}
		// header
		this.setLatestMark();
		
		// Disable isHot
		var rssItems = this.rssContent.rssItems;
		for(var i=0;i<rssItems.length;i++){
			rssItems[i].isHot = false;
		}
	}

	this.setLatestMark = function(){
		if ( (!this.isOpenWidget() || this.getItemsnum() == 0 )||
			( this.getLatestItemCount() == 0 && this.isShowLatestNews() ) ) {
			this.displayItemHeader();
			
			if( this.itemsCountPanel )
				this.itemsCountPanel.style.display = "none";
		} else {
			widget.elm_latestMark.style.display = "none";
			
			if( this.itemsCountPanel )
				this.itemsCountPanel.style.display = "";
			
			this.renderItemsCountPanel();
		}
	}
	
	this.getLatestItemCount = function() {
		return this.rssContent.latestItemLength;
	}
	function getLatestItemsLength() {
		return self.rssContent.latestItemLength;
	};
	
	function isHot(){
		var rssItems = self.rssContent.rssItems;
		for(var i = 0; i < rssItems.length; i++) {
			var isHot = IS_Widget.RssReader.isHotNews( widget.latestDatetime,rssItems[i] );
			if( isHot )
				return true;
		}
		return false;
	};
	
	this.showAllLatestItems = function(e){
//		if( !( this.getLatestItemCount() > 0 ))
//			return;
		
		widget.setUserPref("showLatestNews",true );
		
		var latestCount = this.getLatestItemCount();
		if( latestCount != 0 ) {
			widget.setUserPref("openWidget", true);
			if( widget.headerContent )
				widget.headerContent.changeTurnbkIcon();
			
			this.showContent();
			
			this.rssContentView.elm_viewport.scrollTop = 0;
			this.rssContentView.clearContents();
			this.rssContentView.renderItems( 0,latestCount -1 );
			this.rssContentView.onContentHeightChange();
			
			widget.setUserPref("itemsnum",latestCount );
		} else {
			this.hideContent();
		}
		
		//this.adjustShowLatestNewsHeight();
		
		if( e )
			IS_Event.stopBubbling( e );
	};
	this.handleRssContentViewContentHeightChange = function() {
		this.adjustHeight();
	}
	this.adjustHeight = function() {
		if( this.isShowLatestNews() ) {
			this.adjustShowLatestNewsHeight();
		} else if( this.isNoneScrollMode() ){
			this.adjustNoneScrollModeHeight();
		} else if( this.rssContent.rssItems.length <= this.getItemsnum() ) {
			this.adjustAllItemsHeight();
		}
	}
	this.adjustAllItemsHeight = function() {
		if( !this.rssContentView )
			return;
		
		this.rssContentView.setViewportHeight( this.getHeight() );
	}
	this.adjustShowLatestNewsHeight = function() {
		if( !this.rssContentView )
			return;
		
		this.rssContentView.setViewportHeight(
			this.rssContentView.getItemPosition( this.getLatestItemCount() ));
	}
	this.adjustNoneScrollModeHeight = function() {
		if( !this.rssContentView )
			return;
		
		var itemsnum = this.getItemsnum();
		if( !( itemsnum <= this.rssContent.rssItems.length ))
			itemsnum = this.rssContent.rssItems.length;
		
		var height = this.rssContentView.getItemPosition( itemsnum );
		
		this.rssContentView.setViewportHeight( height );
	}
	
	this.repaintIfCurrentTab = function(){
		if(widget.tabId == IS_Portal.currentTabId)
			this.repaint.apply(this, arguments);
	};
	
	this.repaint = function( keepContentHeight,keepContents,keepViewportHeight ) {
		if( widget.tabId != IS_Portal.currentTabId )
			return;
		
		if( !this.rssContentView )
			return;
		
		this.setLatestMark();
		
		if( this.isNoneScrollMode() && this.rssContentView.elm_viewport.style.overflowY != "hidden" )
			this.rssContentView.elm_viewport.style.overflowY = "hidden";
		
		if( !keepViewportHeight )
			this.rssContentView.setViewportHeight( this.getHeight() );
		
		if( !keepContents )
			this.rssContentView.clearContents();
		
		this.rssContentView.view();
		if( !keepContentHeight )
			this.rssContentView.onContentHeightChange();
		
		//this.adjustHeight();
	}
	
	if( Browser.isSafari1 ) {
	    this.repaint = ( function() {
	        var repaint = this.repaint;
	        
	        return function() {
				if( !widget.elm_widgetContent.offsetHeight || !( widget.elm_widgetContent.offsetHeight > 0 ))
	            	return;
	            
	            repaint.apply( this,$A( arguments ));
	        }
	    }).apply( this );
	}
	
	this.minimize = function () {
		widget.setUserPref("showLatestNews", false);
		
		this.hideContent();
	};
	
	this.turnBack = function() {
		this.showContent();
	};
	this.showContent = function() {
		if( !this.isError()&&( this.getItemsnum() == 0 )&& !( this.getLatestItemCount() == 0 && this.isShowLatestNews() ))
			return;
		widget.elm_widgetContent.style.display = "";
		
		if( !this.isError() ) {
			if( !this.rssContentView ) {
				widget.elm_widgetContent.innerHTML = "";
				
				this.displayContents();
			}
			
			widget.elm_latestMark.style.display = "none";
			this.repaint( false,false );
		}
	}
	this.hideContent = function() {
		widget.elm_widgetContent.style.display = "none";
		
		if( !this.isError() ) {
			this.setLatestMark();
		}
	}

	this.getRssItems = function () {
		return this.rssContent.rssItems;
	}
	
	this.isError = function () {
		return (this.rssContent.rssItems.length == 0)
	}
	
	this.hideErrorMsg = function(){
		if(widget.isError && !this.isOpenWidget()){
			widget.elm_widgetContent.style.display = "none";
		}
	}
	this.handleTimeout = function() {
		widget.elm_widgetContent.innerHTML = "<span style='font-size:90%;padding:5px;'>"+

			IS_R.ms_getdatafailed +"</span>";
		
		this.hideErrorMsg();
	}

	this.updateRssMetaRefresh = function() {
		var convUrl = escapeXMLEntity(widget.getUserPref("url"));
		var autoRefreshCount = IS_Portal.autoRefCountList[convUrl];
		if(!autoRefreshCount){
			autoRefreshCount = 0;
		}
		IS_Portal.autoRefCountList[convUrl] = ++autoRefreshCount;
		
		var cmd = new IS_Commands.UpdateRssMetaRefreshCommand("1",widget.getUserPref("url"),widget.title);
		IS_Request.LogCommandQueue.addCommand(cmd);
	}
	
	this.autoReloadContents = function (req, obj) {
		self.updateRssMetaRefresh();
		this.buildRssItems(req, obj);
	}
	
	this.updateLog = function(){
		var rssUrl = widget.getUserPref("url");
		if (rssUrl && !widget.isAuthenticationFailed() ) {
			IS_Widget.updateLog("2", rssUrl, rssUrl);
		}
	}
	
	this.loadContentsOption = {
		preLoad : function() {
			this.setupLoadContentsOption( this.loadContentsOption );
			
			return true;
		}.bind( this ),
		request : true,
		unloadCache : false,
//		onSuccess : this.buildRssItems.bind(this),
	  onSuccess : function(response){
			this.buildRssItems(response);
		}.bind(this),
//		on304 : this.stopLatestMarkRotate.bind(this),
		on304 : function(){
			this.stopLatestMarkRotate();
		}.bind(this),
		on404 : this.hideErrorMsg.bind(this),	
		on403 : this.hideErrorMsg.bind(this),
		on10408 : this.handleTimeout.bind(this),
		onFailure : this.hideErrorMsg.bind(this),
		onException : this.hideErrorMsg.bind(this)
	};
	
	this.autoReloadContentsOption = {
		preLoad : function() {
			this.setupLoadContentsOption( this.autoReloadContentsOption );
			
			return true;
		}.bind( this ),
		request : true,
		unloadCache : false,
		onSuccess : this.autoReloadContents.bind(this),
		on304 : function() {
			this.stopLatestMarkRotate();
			this.updateRssMetaRefresh();
		}.bind( this )
	};
	
	// This is called when header icon is pushed
	this.refresh = function() {
		widget.loadContents();
		this.updateLog();
	}
	
	this.buildEdit = function(form){
		var this_ = widget.content;
		
		if(widget.parent){
			var autoBuildTable = form.getElementsByTagName('table')[0];
			var removeTr1 = autoBuildTable.getElementsByTagName('tr')[1];//Get line break
			var removeTr2 = autoBuildTable.getElementsByTagName('tr')[2];//Show date and time
			var removeTr3 = autoBuildTable.getElementsByTagName('tr')[4];//Scroll mode
			var removeTr4 = autoBuildTable.getElementsByTagName('tr')[5];//Show in description mode
			removeTr1.parentNode.removeChild(removeTr1);
			removeTr2.parentNode.removeChild(removeTr2);
			removeTr3.parentNode.removeChild(removeTr3);
			removeTr4.parentNode.removeChild(removeTr4);
		}
		
		this_.buildRssLink(form, widget.getUserPref("url"));
		
		this_._lastItemsnum = this_.getItemsnum(); // bimyo-
	}
	this.saveEdit = function( widget,form ) {
		if( !form.itemsnum )
			return;
		
		var this_ = widget.content;
		var currentItemsnum = form.itemsnum.value;
		if( this_.isShowLatestNews() &&( this_._lastItemsnum != currentItemsnum ))
			widget.setUserPref("showLatestNews",false );
		
		if( currentItemsnum == "notChange")
			widget.setUserPref("itemsnum",this_._lastItemsnum );
	}
	
	this.buildRssLink = function(form, url) {
		if(!url) return;
		var aBlank = (Browser.isIE)? "" : "&nbsp;&nbsp;&nbsp;&nbsp;"; //for Firefox
		var rssLinkDiv = document.createElement("div");
		rssLinkDiv.style.clear = "both";
		
		rssLinkDiv.innerHTML += '<a href="' + escapeHTMLEntity(url) +
			'" title="' + escapeHTMLEntity(url) + '" target="_blank" class="rssUrl_Icon">'+ aBlank +'</a>&nbsp;';
		rssLinkDiv.innerHTML += '<a href="' + escapeHTMLEntity(url) +

			'" title="' + escapeHTMLEntity(url) + '" target="_blank">' + IS_R.lb_displayRSS + '</a>';
		
		form.appendChild(rssLinkDiv);
	}
	
	this.applyChildIconStyle = function( div ) {
		if( widget.parent ){
			div.style.display = "none";
		}else if(div.style.display == "none"){
			div.style.display = "block";
		}
	}
	
    this.switchShowDatetime = function(){
    	widget.toggleBoolUserPref("showDatetime");
    	
	    if( this.getHeight() > 0 && this.rssContent.rssItems.length > 0)
	    	this.displayContents();
    }
	
	//Set detailed time
	this.dateIconHandler = function (e) {
		try{
			IS_Event.stopBubbling(e);
			this.switchShowDatetime();
		}catch(error){

			msg.error(IS_R.getResource(IS_R.ms_datecreatorChangeFailed,[widget.id, error]));
		}
		IS_Portal.widgetDisplayUpdated();
	};
	
	this.dateApplyIconStyle = this.applyChildIconStyle.bind( this );

	this.switchLineBreak = function () {
		widget.toggleBoolUserPref("doLineFeed",true );
		
	    if( this.getHeight() > 0 && this.rssContent.rssItems.length > 0)
	    	this.displayContents();
	};
	
	//Set Line feed
	this.lineFeedIconHandler = function (e) {
		try{
			IS_Event.stopBubbling(e);
			this.switchLineBreak();
		}catch(error){

			msg.error(IS_R.getResource(IS_R.ms_lineChangeFailure,[widget.id, error]));
		}
		IS_Portal.widgetDisplayUpdated();
	};
	
	this.lineFeedApplyIconStyle = this.applyChildIconStyle.bind( this );
	
	this.widgetRssDownIconHandler = function(e) {
		try{
			IS_Event.stopBubbling(e);
			this.increaseItem();
			IS_Portal.widgetDisplayUpdated();
		}catch(error){
			console.error( error );
		}
	}
	
	this.widgetRssUpIconHandler = function(e) {
		try{
			IS_Event.stopBubbling(e);
			this.decreaseItem();
			IS_Portal.widgetDisplayUpdated();
		}catch(error){
			console.error( error );
		}
	}
	
	this.minimizeIconHandler = function (e) {
		try{
			this.minimize();
			IS_Portal.widgetDisplayUpdated();
		}catch(error){

			msg.error(IS_R.getResource(IS_R.ms_minimizeFailure,[widget.id, error]));
		}
	};

	this.turnBackIconHandler =  function (e) {
		this.turnBack();
		
		IS_Portal.widgetDisplayUpdated();
	};
	
	this.searchBuildMenuContent = function(){
		return IS_Widget.RssReader.searchBuildMenuContent(widget);
	}
	
	this.searchDisable = function(){
		IS_Widget.RssReader.searchDisable(widget);
	}
	
	this.searchEnable = function(){
		IS_Widget.RssReader.searchEnable(widget);
	}
	
	this.searchApplyIconStyle = function(div){
		div.style.display = "none";
		if(IS_Portal.SearchEngines.isLoaded) {
			if( IS_Portal.SearchEngines.matchRssSearch( widget.getUserPref("url")) )
			  div.style.display = "block";
		} else {
			IS_Portal.SearchEngines.loadConf();
			setTimeout(this.searchApplyIconStyle.bind(this, div), 200);
		}
	};

	this.accessStatsApplyIconStyle = function(div){
		this.accessStatsIcon = div;
		
		div.style.display = "none";
		if( widget.isComplete ) {
			if(widget.isSuccess && widget.widgetPref.useAccessStat && /true/i.test( widget.widgetPref.useAccessStat.value ) ) {
				div.style.display = "block";
			}
		} else {
			setTimeout( this.accessStatsApplyIconStyle.bind( this,div ),100 );
		}
	};
	
	this.accessStatsIconHandler = function(div){
		IS_Widget.RssReader.showAccessStats(widget);
		widget.headerContent.hiddenMenu.hide();
	};
}

IS_Widget.RssReader.searchBuildMenuContent = function(widget){
	var div = document.createElement("span");
	div.id = widget.id + "_rssSearchBox";
	//div.className = "rssSearchBox";
	var input = document.createElement("input");
	input.type = "text";
	input.style.width = "100px";
	div.appendChild(input);
	var button = document.createElement("input");
	button.type = "button";

	button.value = IS_R.lb_execute;
	div.appendChild(button);

	var msgDiv = document.createElement("span");
	msgDiv.className = "rssSearchMsg";
	msgDiv.style.display = "none";
	div.appendChild(msgDiv);
	
	function listUrl(){

		var urllist = [];
		if(widget.content.getRssReaders){
			var rssReaders = widget.content.getRssReaders();
			for(var i = 0; i < rssReaders.length;i++)
			  urllist[i] = {
				  'url':rssReaders[i].getUserPref("url"),
				  'title':rssReaders[i].title
				}
		}else{
			urllist[0] = {
				'url':widget.getUserPref("url"),
				'title':widget.title
			  }
		}
		return urllist;
	}

	var enterKeyPressed = function(e) {
		if(e.keyCode == Event.KEY_RETURN) {
			var keyword = input.value;
			IS_Portal.SearchEngines.buildSearchTabs(keyword, listUrl());
			//div.style.display = "none";
			widget.headerContent.hiddenMenu.hide();
			return false;
		}
	};
	Event.observe(input, "keypress", enterKeyPressed, false);
	var buttonClicked = function() {
		var keyword = input.value;
		IS_Portal.SearchEngines.buildSearchTabs(keyword, listUrl());
		//div.style.display = "none";
		widget.headerContent.hiddenMenu.hide();
	};
	Event.observe(button, "click", buttonClicked, false);
	document.body.appendChild(div);
	IS_Portal.rssSearchBoxList[widget.id] = div;

	if(widget.isLoading) IS_Widget.RssReader.searchDisable(widget);
	else IS_Widget.RssReader.searchEnable(widget);
	
	return div;
}
	
IS_Widget.RssReader.searchDisable = function(widget){
	var div = IS_Portal.rssSearchBoxList[widget.id];
	if(!div) return;
	var inputs = Element.getElementsBySelector(div, 'input');
	inputs.each(function(input){
		input.disabled = true;
	});
}
	
IS_Widget.RssReader.searchEnable = function(widget){
	var div = IS_Portal.rssSearchBoxList[widget.id];
	if(!div) return;
	var inputs = Element.getElementsBySelector(div, 'input');
	inputs.each(function(input){
		input.disabled = false;
	});
}

IS_Widget.RssReader.RssContent = IS_Class.create();
IS_Widget.RssReader.RssContent.prototype = {
	initialize : function( widget, pageSize,requestContext,opt ) {
		this.widget = widget;
		this.pageSize = pageSize;
		this.requestContext = requestContext;
		
		this.onLoadPageCompletedListeners = [];
		if( opt ) {
			this.onLoadPageCompletedListeners.push( opt.onLoadPageCompleted );
		}
		
		this.rssItems = [];
		this.loadingPages = [];
		
		this.latestItemLength = 0;
	},
	setRss : function( rss ) {
		this.latestItemLength = rss.latestItemCount;
		
		this.pageCount = rss.pageCount || 1;
		for( var i=rss.items.length;i<rss.itemCount;i++ )
			rss.items.push( false );
		
		this.rssItems = [];
		this.loadingPages = [];
		this.sources = [rss.itemCount];
		
		this.loadPageComplete( 0,rss );
	},
	
	setFilter : function( filter ) {
		this.filter = filter;
		this.rssItems.clear();
		this.loadPage(0);
		//this.handleLoadPageCompleted( 0,{ items: this.delegator.rssItems });
	},
	
	loadPage : function( itemNo ) {
		if( this.filter ) {
			for( var i=this.sources.length-1;i>=0;i-- ) {
				if( !this.sources[i]) {
					itemNo = i;
				} else {
					break;
				}
			}
		}
		
		var pageNo = Math.floor( itemNo /this.pageSize );
		
		var rssItems = this.rssItems;
		if( !( pageNo < this.pageCount )|| this.loadingPages[pageNo] || rssItems[itemNo] ) {
			
			return;
		}
		this.loadingPages[pageNo] = true;
		
		var this_ = this;
		
		var opt = this.requestContext.getRequestOption( pageNo );
		AjaxRequest.invoke( opt.url,{
			method : opt.method || "get",
			contentType : opt.contentType, 
			requestHeaders: opt.requestHeaders || [],
			asynchronous : opt.asynchronous || true,
			postBody : opt.postBody || undefined,
			onComplete : function(response) {
				try {
					this_.loadPageComplete( pageNo,IS_Widget.parseRss( response ) );
				} catch( ex ) {
					this_.loadingPages[pageNo] = false;
					
					throw response.status + " - " + response.statusText + " : " + opt.url;
				}
			},
			on1223	: function() {return;
				try {
					this_.loadPageComplete( pageNo,{
						items : []
					} );
				} catch( ex ) {
					this_.loadingPages[pageNo] = false;
					
					throw response.status + " - " + response.statusText + " : " + opt.url;
				}
			},
			onFailure : function(response) {
				this_.loadingPages[pageNo] = false;
				
				throw response.status + " - " + response.statusText + " : " + opt.url;
			},
			onException : function(response, e){
				this_.loadingPages[pageNo] = false;
				
				throw getErrorMessage(e);
			}
		});
	},
	loadPageComplete : function( pageNo,page ) {
		var rssItems = this.rssItems;
		if( !this.filter ) {
			var offset = pageNo *this.pageSize;
			for( var i=0;i<page.items.length;i++ ) {
				var rssItem = rssItems[ offset +i ] = page.items[i];
				if(this.widget.parent && page.items[i].rssUrlIndex){
					rssItem.rssUrls = [];
					var rssReaders = this.widget.parent.content.getRssReaders();
					for(var j = 0; j < page.items[i].rssUrlIndex.length; j++){
						var rssReader = rssReaders[page.items[i].rssUrlIndex[j]];
						if( rssReader ) rssItem.rssUrls.push(rssReader.getUserPref("url"));
					}
				}
			}
		}else{
			var offset = pageNo *this.pageSize;
			for( var i=0;i<page.items.length;i++ )
				this.sources[ offset +i ] = page.items[i];
			
			this.rssItems.pop();
			var pageItems = page.items.findAll( function( rssItem ) {
				return !(!rssItem);
			}).findAll( this.filter );
			
			if( pageItems.length > 0 ) {
				for( var i=0;i<pageItems.length;i++ ) {
					this.rssItems.push( pageItems[i] );
				}
			} else if( !this.sourceIsLoadPageCompleted() ) {
				// Load until at least one entry is found
				// Load unintentionally in case of impossible filter
				this.loadPage( Number.MAX_VALUE );
			}
			
			if( !this.sourceIsLoadPageCompleted())
				this.rssItems.push( null );
		}
		
		for( var i=0;i<this.onLoadPageCompletedListeners.length;i++ ) 
			this.onLoadPageCompletedListeners[i]( pageNo,page );
		
		this.loadingPages[pageNo] = false;
	},
	isLoadPageCompleted : function() {
		for( var i=0;i<this.rssItems.length;i++ ) {
			if( !this.rssItems[i] )
				return false;
		}
		
		return true;
	},
	sourceIsLoadPageCompleted: function () {
		for( var i=0;i<this.sources.length;i++ ) {
			if( !this.sources[i] )
				return false;
		}
		
		return true;
	}
}
IS_Widget.RssReader.RssContentView = IS_Class.create();
IS_Widget.RssReader.RssContentView.prototype.classDef = function() {
	var self;
	
	var DEBUG = false;
	var DEFAULT_HEIGHT = 50;
	
	this.initialize = function( widget,rssContent,opt ){
		self = this;
		this.widget = widget;
		this.isStatic = widget.panelType == "StaticPanel" && widget.isStaticHeight;
		this.isCanvas = opt.isCanvas;
		this.rssContent = rssContent;
		
		this.itemList = [];
		this.stopAutoPageLoad = false;
		
		var viewport = document.createElement("div");
		viewport.style.position = "relative";
		viewport.style.width = "100%";
		
		this.scrollable = opt.scrollable || ( opt.scrollable === undefined )
		if( this.scrollable) {
			if( !Browser.isSafari1 ) {
				viewport.style.overflowX = "hidden";
				viewport.style.overflowY = "scroll";
			} else {
				viewport.style.overflow = "auto";
			}
		} else {
			viewport.style.overflow = "hidden";
		}
		this.elm_viewport = viewport;
		if( opt.height || opt.height == 0 )
			this.setViewportHeight( opt.height );
		
		this.render = opt.render || IS_Widget.RssReader.RssItemRender;
		this.renderContext = Object.extend( opt.renderContext || {},{ widget: widget } );
		
		var content = document.createElement("div");
		viewport.appendChild( content );
		this.elm_content = content;
		
		var topSpacer = document.createElement("div");
		content.appendChild( topSpacer );
		this.elm_top = topSpacer;
		
		if( this.render.tableRowRender ) {
			var table = document.createElement("table");
			table.style.width = "100%";
			table.style.padding = 0;
			table.style.margin = 0;
			table.style.borderCollapse = "collapse";
			content.appendChild( table );
			
			var tbody = document.createElement("tbody");
			table.appendChild( tbody );
			
			this.elm_container = tbody;
		} else {
			var container = document.createElement("div");
			content.appendChild( container );
			
			this.elm_container = container;
		}
		
		this.elm_root = viewport;
		
		this.eventId = widget.id+"_rssContentView";
		IS_Event.observe( viewport,"scroll",this.handleScroll.bind( this ),false,this.eventId );
		//IS_Event.observe( viewport,"mouseup",this.onContentHeightChange.bind( this ),false,widget.id );
		
		if( Browser.isFirefox ) {
			IS_Event.observe( viewport,"mouseup",this.handleMouseUp.bind( this ),false,this.eventId );
			// Recalculate height by wheel scroll
			IS_Event.observe( viewport,"DOMMouseScroll",
				this.handleMouseUp.bind( this ),false,this.eventId );
			// Recalculate height by keyup
			IS_Event.observe( document,"keyup",
				this.handleMouseUp.bind( this ),true,this.elventId );
		}
		
		this.onContentHeightChange(); //init itemList
		viewport.scrollTop = 0;
	};
	this.unloadCache = function() {
		IS_Event.unloadCache( this.eventId );
		
		this.clearContents();
	}
	this.setViewportHeight = function( height ) {
		if(this.isStatic && !this.isCanvas ) {
			if( 0 < this.widget.staticWidgetHeight ){
				var widgetHeight = this.widget.staticWidgetHeight;
				if( this.widget.content.itemsCountPanel ) {
					if( 0 < this.widget.content.itemsCountPanel.offsetHeight ) {
						widgetHeight -= this.widget.content.itemsCountPanel.offsetHeight;
					} else {
						setTimeout( this.setViewportHeight.bind( this,height ),100 );
					}
				}
				
				if (this.elm_viewport.offsetHeight != widgetHeight)
					this.elm_viewport.style.height = widgetHeight + 'px';
	
				this.widget.elm_widgetContent.style.overflowY = "hidden";
				
				return;
			} else {
				height = 1;
				
				setTimeout( this.setViewportHeight.bind( this,height ),100 );
			}
		}
		if( height == 0 && Browser.isIE ) {
			height = 1;
		}
		
		try {
			this.elm_viewport.style.height = height + 'px';
		} catch( ex ) {
			msg.error( ex );
		}
	}
	
	//ToDo
	if( Browser.isSafari1 ) {
		this.setViewportHeight = ( function() {
			var setViewportHeight = this.setViewportHeight;
			
			return function( height ) {
				if( this.scrollable && 10 < height && height < 64 ) {
				// Scroll bar is unshown if there is no minimum height
					height = 64;
				}
				
				setViewportHeight.apply( this,[height] );
			}
		}).apply( this );
	}
	
	this.handleScroll = function( e ) {
		//if( this.repaintTimeout )
		  clearTimeout( this.repaintTimeout );
		
		//var time = new Date().getTime();
		//if( this.lastScrollTime && time -this.lastScrollTime < 1000/10 )
		//	return;
		
		//this.lastScrollTime = time;
		var y = this.elm_viewport.scrollTop;
		this.repaintTimeout = setTimeout( function(y){
			this.view( y );
			IS_Widget.RssReader.RssItemRender.hideRssDesc();
			this.scrolled = true;
			
			// Set time-out in 0.5 second after continuous scroll
			if( Browser.isIE || Browser.isSafari1 )
				this.repaintTimeout = setTimeout( this.handleMouseUp.bind( this ),500 );
		}.bind(this, y), 100);
		/*
		if( !this.rendering ) {
			this.view( y );
			this.scrolled = true;
			
			if( Browser.isIE )
				this.repaintTimeout = setTimeout( this.handleMouseUp.bind( this ),100 );
		}
		
		IS_Widget.RssReader.RssItemRender.hideRssDesc();
		*/
	}
	
	this.handleMouseUp = function( e ) {
		if( this.scrolled ) {
			this.scrolled = false;
			this.onContentHeightChange();
			this.view();
		}
	}
	this.getContentHeight = function( renew ) {
		if( renew )
			this.contentHeight = null;
		
		if( !this.contentHeight )
			this.contentHeight = this.getItemPosition( this.rssContent.rssItems.length );
		
		return this.contentHeight;
	}
	this.getItemHeight = function( item ) {
		if( item && item.height )
			return item.height;
		
		if( this.render.getDefaultHeight )
			return this.render.getDefaultHeight( this.renderContext );
		
		return DEFAULT_HEIGHT;
	}
	this.getItemPosition = function( no ) {
		var c = 0;
		for( var i=0;i<no;i++ ) {
			var item = this.itemList[i];
			c += this.getItemHeight( item );
		}
		
		return c;
	}
	this.resetContentHeight = function() {
		var this_ = this;
		this.itemList.each( function( item ){
			item.height = this_.getItemHeight( item );
		});
	}
	/**
	 * Recalculate height of contents because scroll bar changes
	 * Do not Calculate during drag, but do often
	 */
	this.onContentHeightChange = function() {
		var rssItems = this.rssContent.rssItems;
		
		var oldContentHeight = this.contentHeight;
		var viewportHeight = this.elm_viewport.offsetHeight;
		var contentHeight = this.getContentHeight( true );
		var current = 0;
		var this_ = this;
		rssItems.each( function( rss,i ) {
			var item = this_.itemList[i];
			var itemHeight = this_.getItemHeight( item );
			var itemWeight = itemHeight /contentHeight;
			if( current > 1 || i == rssItems.length -1 )
				current = 1 -itemWeight;
			
			//if(DEBUG)console.info( i+" = "+Math.ceil( current *100 )+","+Math.ceil( (current+itemWeight) *100 ));
			this_.itemList[i] = {
				index: i,
				rss: rss,
				height: itemHeight,
				position: current,
				weight: itemWeight
			};
			
			current += itemWeight;
		});
		
		this.elm_content.style.height = contentHeight + 'px';
		
		if( this.contentHeightChangeListener )
			this.contentHeightChangeListener();
	}
	/**
	 * Show RssItem in upper area of contents
	 * Roll up in case of less than 0 or more than contentHeight and -viewportHeight
	 */
	this.view = function( pos ) {
		if( Browser.isSafari1 && !this.elm_root.offsetHeight )
			return;
		
		var rssItems = this.rssContent.rssItems;
		
		var container = this.elm_container;
		var viewport = this.elm_viewport;
		var viewportHeight = viewport.offsetHeight;
		if( viewportHeight == 0 ) {
			try {
				viewportHeight = parseInt( viewport.style.height );
			} catch( ex ) {
				// ignore
			}
		}
		var contentHeight = this.getContentHeight();
		
		if( pos === undefined ) {
			/*while( container.firstChild )
				this.render.releaseInstance( container.firstChild );
			
			this.renderingHead = this.renderingTail = undefined;*/
			
			pos = this.elm_viewport.scrollTop;
		} else if( pos +viewportHeight > contentHeight ) {
			pos = contentHeight -viewportHeight;
		}
		
		if( pos < 0 ) {
			pos = 0;
		}
		
		this.pos = pos;
		
		var current = 0//,offset =0;
		var head=-Number.MAX_VALUE,tail=-Number.MAX_VALUE;
		var hy,ty;
		this.itemList.each( function( item,i ) {
			if( head < i && current <= pos ) {
				head = i;if(DEBUG)hy=current;
				
				//offset = current -pos;
			}
			
			if( tail < i && current <= ( pos +viewportHeight )) {
				tail = i;if(DEBUG)ty=current;
			}
			
			current += item.height;
		});
		
		// Keep large size in case for flicker in upper direction
		var margin = Browser.isIE ? 5 : 2;
		for( var i=0;i<margin&&head>0;i++)
			head--;
		
		for( var i=0;i<margin&&tail<rssItems.length -1;i++)
			tail++;
		
		if( DEBUG ) {
		console.info("view("+pos+") / "+contentHeight+" @"+viewportHeight )
		console.info( pos+"/"+head+"-"+tail+"/"+( pos +viewportHeight ));
		}
		
		// fix #788,946
		if( tail == -Number.MAX_VALUE )
			return;
		
		this.renderItems( head,tail );
		
		/**
		 * Keep on drawing if contents gets smaller size
		 */
		//var newContentHeight = 0;
		//while( ( this.elm_root.offsetHeight != 0 )&&
		//	( newContentHeight = this.getContentHeight( true ))-contentHeight < 0 ) {
		//	if(DEBUG)console.info("content re rendering: "+newContentHeight -contentHeight );
		//	this.view( pos );
		//	
		//	contentHeight = newContentHeight;
		//}
	}
	
	/**
	 * Command to draw in index-based; ofset is for position adjustment
	 */
	this.renderItems = function( head,tail ) {
		var rssItems = this.rssContent.rssItems;
		if( head < 0 )
			head = 0;
		
		if( !( tail < rssItems.length ) )
			tail = rssItems.length -1;
		
		if( tail < head ) {
			var buf = head;
			head = tail;
			tail = buf;
		}
		
		if( !this.stopAutoPageLoad ) {
			for( var i=tail;head<=i;i-- ) {
				if( !rssItems[i] ) {
					//console.info("#"+i+" load from render!  "+head+" ... "+tail )
					this.rssContent.loadPage( i );
					tail = i -1;
				}
			}
			
			var pageSize = this.rssContent.pageSize;
			var prevPage = Math.round( head /pageSize )*pageSize;
			if( head -prevPage < pageSize /2 && !rssItems[prevPage]) {
				this.rssContent.loadPage( prevPage );
			}
			
			var nextPage = Math.ceil( tail /pageSize )*pageSize;
			if( tail -nextPage > pageSize /2 && !rssItems[nextPage]) {
				this.rssContent.loadPage( nextPage );
			}
		}
		this.rendering = true;
		
		if( tail < head ) {
			var buf = head;
			head = tail;
			tail = buf;
		}
		
		var container = this.elm_container;
		
		var foward = true;
		var removes = container.childNodes.length;
		var headGap = head -this.renderingHead;
		var tailGap = this.renderingTail -tail;
		
		var contentHeight = this.getContentHeight()/* -(this.rssContent.rssItems.length /2 )*/;
		var topHeight = this.getItemPosition( head );
		this.elm_content.style.height = contentHeight + 'px';
		if( this.render.tableRowRender ) {
			if( this.elm_content.style.overflow == "hidden") {
				this.elm_content.style.overflow = "visible"
			} else {
				this.elm_content.style.overflow = "hidden";
			}
		}
		
		if( topHeight == 0 ) {
			this.elm_top.style.display = "none";
		} else {
			this.elm_top.style.display = "";
		}
		this.elm_top.style.height = topHeight + 'px';
		//this.elm_bottom.style.height = bottomHeight;
		
		for( var i=0;i<headGap && container.firstChild;i++ ) {
			if( DEBUG )console.info("remove: "+ container.firstChild.id );
			this.render.releaseInstance( container.firstChild );
		}
		
		for( var i=0;i<tailGap && container.firstChild;i++ ) {
			if( DEBUG )console.info("remove: "+ container.lastChild.id );
			this.render.releaseInstance(  container.lastChild );
		}
		
		var start = head;
		var renders = tail -head +1;//DEBUG =1;
		if( head < this.renderingHead && this.renderingTail < tail ) {
			// Rendering again
			if( DEBUG )console.info("outer")
			while( this.elm_container.firstChild )
				this.elm_container.removeChild( this.elm_container.firstChild )
		} else if( this.renderingHead < head && tail < this.renderingTail ) {
			// contain nothing
			if( DEBUG )
			console.info("("+this.renderingHead+","+this.renderingTail+") -> ("+head+","+tail+") contain")
			renders = 0;
		} else if( this.renderingHead <= head && head <= this.renderingTail ) {
			// foward
			if( DEBUG )console.info("foward:"+tailGap )
			
			start = this.renderingTail +1;
			renders = -tailGap;
		} else if( this.renderingHead <= tail && tail <= this.renderingTail ) {
			// back
			if( DEBUG )console.info("back"+headGap );
			
			start = this.renderingHead -1;
			renders = -headGap;
			foward = false;
		} else {
			if( DEBUG )console.info("other");
		}
		if( DEBUG )console.info( this.renderingHead +" ... "+this.renderingTail +" | "+head +" ... "+tail+"/"+rssItems.length )
		//console.info( ( !foward ? "head":"tail")+": "+renders );
		//DEBUG =0;
		//console.info("renderItems("+head+","+tail+")/"+rssItems.length);
		
		//renders += 5;
		
		//container.style.display = "none";
		var fragment = document.createDocumentFragment();
		var renderItems = [];
		for( var i=0;i<renders;i++ ) {
			var index = start +( foward ? i : -i );
			//console.info("renderItem #"+i+"/"+renders+" idx["+index+"]" );
			if( index < 0 || index >= rssItems.length ) continue;
			
			var rssItem = rssItems[index]
			var item = this.itemList[index];
			
			var renderInst = this.render.getInstance();
			var tr;
			if( rssItem ) {
				tr = renderInst.render( this.renderContext,rssItem,index );
				/* Lightweight version
				tr = document.createElement("div");
				tr.className = "rssItem";
				
				var aTag = document.createElement('a');
				aTag.href = rssItem.link;
				IS_Event.observe(aTag, "click", IS_Portal.buildIFrame.bind(this, aTag));
				aTag.appendChild(document.createTextNode( rssItem.title ));
				tr.appendChild(aTag);
				
				if(IS_Widget.RssReader.isLatestNews(item.rss.rssDate)){
					var latestMark = document.createElement("img");
					latestMark.className = "latestMark";
					var isHotNews = IS_Widget.RssReader.isHotNews( self.widget.latestDatetime,rssItem );
					latestMark.src = imageURL +( isHotNews ? "sun_blink.gif":"sun.gif");
					tr.appendChild( latestMark );
				}
				*/
			} else {
				tr = document.createElement("div");
				tr.style.margin = '4px';
				tr.style.textAlign = "center"
				tr.style.height = this.getItemHeight( item ) + 'px';
				tr.appendChild( document.createTextNode("Now Loading ..."));
			}
			
			if( foward || !fragment.firstChild ) {
				fragment.appendChild( tr );
			} else {
				fragment.insertBefore( tr,fragment.firstChild )
			}
			
			if( renderInst.postRender )
				renderInst.postRender( this.renderContext,rssItem,index );
			
			if( rssItem ) {
				renderItems.push( { index:index,element:tr } );
			}
		}
		
		if( foward ) {
			container.appendChild( fragment );
		} else {
			container.insertBefore( fragment,container.firstChild )
		}
		var this_ = this;
		renderItems.each( function( renderItem ) {
			var item = this_.itemList[renderItem.index];
			var itemHeight = renderItem.element.offsetHeight;
			if( itemHeight != undefined && itemHeight > 0 && item )
				item.height = itemHeight;
		});
		
		this.renderingHead = head;
		this.renderingTail = tail;
		
		this.rendering = false;
		
		//container.style.top = offset;
	}
	this.clearContents = function( light ) {
		while( this.elm_container.firstChild )
			this.render.releaseInstance( this.elm_container.firstChild );
		
		this.renderingHead = this.renderingTail = undefined;
		
		if( !light ) {
			this.pos = 0;
			this.elm_content.style.height = this.elm_top.style.height = "auto";
		}
	}
}
IS_Widget.RssReader.isHotNews = function( latestDatetime,rssItem ) {
	if( !latestDatetime || !rssItem || !rssItem.rssDate || !rssItem.rssDate.getTime )
		return false;
	
	if( rssItem.isHot === false )
		return false;
	
	var rssDate = rssItem.rssDate;
	
	var latestTime = parseInt( latestDatetime );
	if( isNaN( latestTime ))
		return false;
	
	return ( latestTime < rssDate.getTime() )
}
IS_Widget.RssReader.isLatestNews = function(rssDate) {
	if (!rssDate) {
		return false;
	}
	var nowDate = new Date();
	var logoffDatetime = parseInt( IS_Portal.logoffDateTime );
	var freshTime = ( logoffDatetime > 0 ) ? nowDate.getTime() - logoffDatetime : 0;
	var freshDaysTime = freshDays *24 *60 *60 *1000;

	return ( rssDate.getTime() > nowDate.getTime() -Math.max( freshTime,freshDaysTime ))
}

/**
 *  Create MultiRssReader
 */
IS_Widget.RssReader.createMultiRssReader = function(widget, targetWidget){
	var subWidgets = [];
	
	var w_id = "p_" + new Date().getTime();
	var widgetConf = IS_WidgetsContainer.WidgetConfiguration.getConfigurationJSONObject(
		"MultiRssReader", w_id, targetWidget.widgetConf.column, targetWidget.title + " & " + widget.title , "", null);
	
	// Create empty MultiRssReader
	var newWidget = IS_WidgetsContainer.addWidget( IS_Portal.currentTabId, widgetConf, false, function(w){
		targetWidget.elm_widget.parentNode.replaceChild(w.elm_widget,targetWidget.elm_widget);
	});
	IS_Widget.setWidgetLocationCommand(newWidget);
	
	newWidget.content.addSubWidget(targetWidget);
	IS_Portal.addSubWidget(newWidget, targetWidget, newWidget.tabId);
	IS_Widget.setWidgetLocationCommand(targetWidget);
	subWidgets.push(targetWidget);
	
	// Add it to empty MultiRssReader
	if(widget.widgetType == "MultiRssReader"){
		var rssReaders = IS_Portal.getSubWidgetList(widget.id);
		
		for(var i=0;i<rssReaders.length;i++){
			var rssReader = rssReaders[i];
			newWidget.content.addSubWidget( rssReader );
			if (!rssReader.isBuilt) {
				rssReader.build();
				rssReader.loadContents();
			} else if(!rssReader.isComplete) {
				rssReader.loadContents();
			}
			IS_Portal.addSubWidget(newWidget, rssReader, newWidget.tabId);
			IS_Widget.setWidgetLocationCommand( rssReader);
			subWidgets.push( rssReader );
		}
		
		var removeIdAry = new Array();
		for(var i=0;i<rssReaders.length;i++){
			if(rssReaders[i]) removeIdAry.push(rssReaders[i].id);
		}
		for(var i=0;i<removeIdAry.length;i++){
			if(removeIdAry[i]){
				IS_Portal.removeSubWidget(widget.id, removeIdAry[i], widget.tabId);
			}
		}
		widget.headerContent.close("notCloseFeeds", true);
		
		IS_Portal.removeWidget(widget.id, IS_Portal.currentTabId);
	}else{
		newWidget.content.addSubWidget(widget);
		IS_Portal.addSubWidget(newWidget, widget, newWidget.tabId);
		IS_Widget.setWidgetLocationCommand(widget);
		subWidgets.push(widget);
	}
	
	// Reflect initial setting of MultiRssReder
	for(var i=0;i<subWidgets.length;i++){
		var addedWidget = subWidgets[i];
		if(subWidgets[i] && newWidget.content.initRssReader( addedWidget )
				&& addedWidget.isBuilt && newWidget.content.isCategoryDisplayMode())
			addedWidget.content.displayContents();
	}
	
	return newWidget;
}

IS_Widget.RssReader.dropGroup = new IS_DropGroup({
	getDroppables:function(element){
		if(IS_Draggables.keyEvent.isPressing.ctrl) return;
		var droppables = [];
		var widgetLists = IS_Portal.widgetLists[IS_Portal.currentTabId];
		var dragWidget = widgetLists[IS_Portal.getTrueId(element.id)];
		var parentElement = dragWidget && dragWidget.parent ? dragWidget.parent.elm_widget : false;
		for ( var i=1; i<=IS_Portal.tabs[IS_Portal.currentTabId].numCol; i++ ) {
			var myHeight = 0;
			var columnObj = IS_Portal.columnsObjs[IS_Portal.currentTabId]["col_dp_"  + i];
			for ( var j=0; j<columnObj.childNodes.length; j++ ) {
				var wiz = columnObj.childNodes[j];
				var widget = widgetLists[IS_Portal.getTrueId(wiz.id)];
				if(widget && /^RssReader|MultiRssReader$/.test(widget.widgetType)){
					if (wiz == element){
						//myHeight = IS_Draggable.ghost.offsetHeight;
						myHeight = element.offsetHeight;
//						continue;
}
					var drops = this.getDropObjByElement(widget.elm_widget);
					if(drops)
						for(var k =0; k<drops.length;k++){
							droppables.push(drops[k]);
						}
				}
				//Process IS_Droppables.findWizPos here
				//for performance upgrade
				wiz.posLeft = findPosX(wiz);
				wiz.posTop = findPosY(wiz) - myHeight;
				if( parentElement && (wiz == parentElement))
//					myHeight = IS_Draggable.ghost.offsetHeight;
					myHeight = element.offsetHeight;
			}
		}
		return droppables;
	}
});

// Access statistics
IS_Widget.RssReader.showAccessStats = function(widget){
	var statsId = 'accessStats_' + widget.id;
	if($(statsId)) {
		var win = Windows.getWindow(statsId);
		win.toFront();
		return;
	}
	var win = new Window(statsId, {
		className: "alphacube",

		title: IS_R.getResource(IS_R.lb_accessStatsTitle, [widget.title]),
		width:'600px',
		height:'350px',
		minimizable: false,
		maximizable: false,
		resizable: false,
		showEffect: Element.show,
		hideEffect: Element.hide,
		destroyOnClose: true,
		zIndex: 10000
	});
	win.showCenter();
	
	if(!/MultiRssReader/.test( widget.widgetType )) {
		var rssUrl = widget.getUserPref("url");
		win.setURL("accessstats?rssUrl=" + encodeURIComponent(rssUrl));
	} else {
		var multiAccessStatContent = document.createElement("div");
		win.setContent( multiAccessStatContent );
		
		IS_Widget.RssReader.buildMultiAccessStatContent( widget,multiAccessStatContent );
		win.setSize( multiAccessStatContent.offsetWidth +24+'px',multiAccessStatContent.offsetHeight +16+'px' );
	}
}

IS_Widget.RssReader.buildMultiAccessStatContent = function( widget,root ) {
	var statsId = 'accessStats_' + widget.id;
	
	var tabs = document.createElement("ul");
	tabs.className = "accessStat_tabs tabs";
	
	var contents = document.createElement("div");
	contents.className = "accessStat_tabs_content";
	
//	var root = document.createElement("div");
	root.appendChild( tabs );
	root.appendChild( contents );
	
	var currentTab;
	
	var errorUrls = widget.content.mergeRssReader.content.rss.errorUrls;
	widget.content.getRssReaders().each( function( rssReader ) {
		var url = rssReader.getUserPref("url");
		if( errorUrls.contains( url ) )
			return;
		
		var tab = document.createElement("li");
		tab.id = "accessStatTab_"+rssReader.id;
		
		var tabAnchor = document.createElement("a");
		tabAnchor.id = "tab_"+encodeURIComponent( url );
		tabAnchor.href = "#"+encodeURIComponent( url );
		tabAnchor.className = "tab";
		tab.appendChild( tabAnchor );
		
		var title = document.createElement("span");
		title.className = "title";
		title.appendChild( document.createTextNode( rssReader.title ));
		tabAnchor.appendChild( title );
		
		tabs.appendChild( tab );
		
		var content = document.createElement("div");
		content.id = encodeURIComponent( url );
		contents.appendChild( content );
		
		var iframe = document.createElement("iframe");
		iframe.frameBorder = 0;
		iframe.style.width = iframe.style.height = "100%";
		iframe.src = "./blank.html";
		content.appendChild( iframe );
	});
	
	new Control.Tabs( tabs,{
		beforeChange: function( old_container,new_container ){
			Element.removeClassName( $("tab_"+old_container.id ),"selected");
			Element.addClassName( $("tab_"+new_container.id ),"selected");
			
			var iframe = new_container.firstChild;
			if( !iframe.src || /\/blank.html$/.test(iframe.src))
				iframe.src = "accessstats?rssUrl=" +new_container.id;
		}
	})
	
	return root;
}

//Manage all repaint at once in resizing window and text
//Draw again when tub is switched, if IS_Widget.RssReader.needToRepaint[tabId]is true
//Reserve
//Resize text: Draw hidden tub because IS_Portal.adjustIS_PortalStyle works
//Not Reserve display
IS_Widget.RssReader.needToRepaint = {};
IS_Widget.RssReader.contentChangingTimer = false;
IS_Widget.RssReader.onContentChange = function(e, reserveOtherTabRepaint){
	for(var tabId in IS_Portal.widgetLists){
		if(typeof IS_Portal.widgetLists[tabId] == "function") continue;
		
		if(tabId != IS_Portal.currentTabId){
			if(reserveOtherTabRepaint)
				IS_Widget.RssReader.needToRepaint[tabId] = true;
		} else {
			IS_Widget.RssReader.repaint(tabId);
		}
	}
}
//Enable the listener called at last because listener is called very often in risizing window
IS_Widget.RssReader.onContentChangeLater = function(e){
	function _onContentChange(){
		IS_Widget.RssReader.onContentChange(e, true);
		IS_Widget.RssReader.contentChangingTimer = false;
	}
	if(IS_Widget.RssReader.contentChangingTimer){
		clearTimeout(IS_Widget.RssReader.contentChangingTimer);
		IS_Widget.RssReader.contentChangingTimer = false;
	}
	IS_Widget.RssReader.contentChangingTimer = setTimeout(_onContentChange, 100);
};
IS_Event.observe( window,"resize", IS_Widget.RssReader.onContentChangeLater);
IS_EventDispatcher.addListener('fontSizeChanged',null,IS_Widget.RssReader.onContentChange);
IS_EventDispatcher.addListener("adjustedSiteMap", null, IS_Widget.RssReader.onContentChange.bindAsEventListener(IS_Widget.RssReader, true));
IS_Widget.RssReader.repaint = function(tabId){
	var widgets = IS_Portal.widgetLists[tabId];
	for(var i in widgets){
		if(widgets[i].widgetType == "RssReader"
		   && widgets[i].content
		   && widgets[i].content.repaint){
			widgets[i].content.repaint(false,false,true);
		}
	}
	if(IS_Widget.RssReader.needToRepaint[tabId])
		delete IS_Widget.RssReader.needToRepaint[tabId];
}
IS_Widget.RssReader.validateUserPref = {
	url:function(value){
		return IS_Validator.validate(value, {
			label: IS_Widget.getDisplayName('RssReader', 'url'),
			required: true,
			regex: '^((?:http)|(?:https)|(?:ftp))://',
			regexMsg: IS_R.ms_invalidURL,
			maxBytes: 1024
		});
	},
	dateTimeFormat:function(value){
		return IS_Validator.validate(value, {
			label: IS_Widget.getDisplayName('RssReader', 'dateTimeFormat'),
			format: 'datefmt'
		});
	}
}
