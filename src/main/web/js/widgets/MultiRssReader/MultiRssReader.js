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

IS_Widget.MultiRssReader = IS_Class.create();
IS_Widget.MultiRssReader.prototype.classDef = function() {
	var widget;
	var self = this;
	var widgetConfXml;
	var id;
	var isStatic;

	//Judge whether reloading is needed or not when 'OK' is clicked on edit panel
	var needsReloadContents = false;
	
//	var reloadContents = false;delete at 20081017 by endoh, because this val is not used.
//	var retry = 0; delete at 20081017 by endoh, because this val is not used.
	var contentInCategory = document.createElement("div");
	var contentInTime = document.createElement("div");
	var sortedTotalItems = [];
//	var rssReaders = [];

	var titleFilter,creatorFilter,categoryFilter;
	
	this.initialize = function(widgetObj){
		widget = widgetObj;
		widgetConfXml = widget.widgetConf;
		isStatic = widget.panelType == "StaticPanel";
		
		if(!widgetConfXml.feed) widgetConfXml.feed = new Array();
		id = widget.id;
		
		widget.elm_widgetContent.style.display = "";
		if ( this.isCategoryDisplayMode()) {
			contentInTime.style.display = "none";
			contentInCategory.style.display = "block";
		} else {
			contentInCategory.style.display="none";
			contentInTime.style.display = "block";
			
			if( this.getItemsnum() == 0 || !this.isOpenWidget())
				contentInTime.style.display = "none";
		}
		
		this.scrollType = widget.getUserPref("scrollMode");
		this.isMouseWheelAvailable = false;
		
		this.heightChangeUnit = 50;
		
		titleFilter = widget.getUserPref('titleFilter');
		creatorFilter = widget.getUserPref('creatorFilter');
		categoryFilter = widget.getUserPref('categoryFilter');
		
		this.maxItemLength = 100;
		this.rssItemLength = 0;
		this.isSubcategoryDragging = false;
		this.buildRssReaders();
		
		this.setDroppable(widgetObj.elm_widget);
		
		IS_EventDispatcher.addListener("dragWidget",widget.id,this.repaint.bind( this ) );
		
		$( widget.elm_widgetContent ).addClassName("RssReader");
	}
	
	this.droppableOption = {};
	
	this.setDroppable = function(droppableElement){
		var dragMode;
		var opt = {};
//		opt.accept = ["widget", "subWidget"];
		opt.accept = function(element, widgetType, classNames){
			if (widget.tabId != IS_Portal.currentTabId) {
				return false;
			}else {
				return (classNames.detect(function(v){
					return ["widget", "subWidget"].include(v)
				}) &&
				(widgetType == "MultiRssReader" || widgetType == "RssReader") &&
				!IS_Draggables.keyEvent.isPressing.ctrl &&
				( element.id && ( element.id != widget.id )));
			}
		}
		opt.onHover = function(element, dropElement, dragMode, point, overlap){
			if(self.isTimeDisplayMode()){
			    if( widget.elm_widgetBox.oldClassName === undefined )
					widget.elm_widgetBox.oldClassName = widget.elm_widgetBox.className;
				
				widget.elm_widgetBox.className = "dropToParent";
				IS_Draggable.ghost.style.display = "none";
			}else{
				IS_Droppables.replaceLocation(element, widget, point[0], point[1]);
			}
		};
		opt.outHover = function(element, dropElement, dragMode, point, overlap){
			if(self.isTimeDisplayMode()){
				widget.elm_widgetBox.className = widget.elm_widgetBox.oldClassName;
				widget.elm_widgetBox.oldClassName = undefined;
				IS_Draggable.ghost.style.display = "";
			}
		};
		this.droppableOption.onWidgetDrop = function(element, lastActiveElement, draggedWidget, event, modalOption) {// Top, in case of showing in time ordering
			var widgetGhost = IS_Draggable.ghost;
			
			// Check merging
			if((!draggedWidget.parent || (draggedWidget.parent && draggedWidget.parent.id != widget.id))
				&& !IS_Droppables.mergeConfirm.call(self, element, lastActiveElement, draggedWidget, event, self.droppableOption.onWidgetDrop, function(){
					if(widget.elm_widgetBox.className  == "dropToParent")
						widget.elm_widgetBox.className = "widgetBox";
				}, widget.title, modalOption))
				return;
			
			draggedWidget.elm_widget.style.display = "";
			var nextSiblingId;
			if( self.isTimeDisplayMode()){
				nextSiblingId = "";
			}else{
				nextSiblingId = (widgetGhost.nextSibling) ? widgetGhost.nextSibling.id : null;
			}

			if(draggedWidget.widgetType == "MultiRssReader"){// && dragMode == "widget"){
				var rssReaders = draggedWidget.content.getRssReaders();
				
				if(widgetGhost.parentNode){
					widgetGhost.parentNode.removeChild(widgetGhost);
				}
				
				for(var i=0;i<rssReaders.length;i++){
					IS_Portal.addSubWidget(widget.id, rssReaders[i].id, widget.tabId);
					self.addSubWidget(rssReaders[i], nextSiblingId);
					IS_Widget.setWidgetLocationCommand(rssReaders[i]);
					
					IS_Portal.removeSubWidget(draggedWidget.id, rssReaders[i].id, draggedWidget.tabId);
					
					if( !rssReaders[i].isComplete )
						rssReaders[i].loadContents();
					
					if( self.initRssReader( rssReaders[i] ) && rssReaders[i].isBuilt && self.isCategoryDisplayMode())
						rssReaders[i].content.displayContents();
					
					rssReaders[i].blink();
				}
				
				draggedWidget.headerContent.close("notCloseFeeds", true);//Delete the original of 'Multi' that is Dragged, and 'notCloseFeeds' is flag that makes menu remain in grey out
				
				IS_Portal.removeWidget(draggedWidget.id, draggedWidget.tabId);
			}else{//widget or subWidget or Menu
				var nextSiblingId = (widgetGhost.nextSibling) ? widgetGhost.nextSibling.id : "";
				if( !Browser.isSafari1 ||( widgetGhost && widgetGhost.style.display != "none") ) {
					widgetGhost.parentNode.replaceChild(draggedWidget.elm_widget, widgetGhost);
				} else {
				    widgetGhost.parentNode.removeChild( widgetGhost );
				}
				
				if(draggedWidget.isBuilt){
					draggedWidget.elm_widget.className = "subWidget";
				}
				/*
				else{
					self.addSubWidget(draggedWidget, nextSiblingId);
				}
				*/
				var oldParent = draggedWidget.parent;
				self.addSubWidget(draggedWidget, nextSiblingId);
				
				IS_Portal.addSubWidget(widget.id, draggedWidget.id, widget.tabId);
				IS_Widget.setWidgetLocationCommand(draggedWidget);
				
				if( self.initRssReader( draggedWidget ) && draggedWidget.isBuilt && self.isCategoryDisplayMode())
					draggedWidget.content.displayContents();
				
				draggedWidget.blink();

				if( oldParent && widget.id != oldParent.id){
					IS_Portal.removeSubWidget( oldParent, draggedWidget, oldParent.tabId);
					IS_EventDispatcher.newEvent("applyIconStyle", oldParent.id );
					
					if( oldParent.content.isTimeDisplayMode()) {
						oldParent.loadContents();
					} else {
						oldParent.content.mergeRssReader.isComplete = false;
					}
					oldParent.content.checkAllClose(true);
				}
				
				if(draggedWidget.widgetType == "RssReader" && dragMode == "widget"){
					IS_EventDispatcher.newEvent("changeConnectionOfWidget", draggedWidget.id);
				}
				
				if(dragMode == "widget"){
					IS_EventDispatcher.newEvent("moveWidget", draggedWidget.id);
				}
			}
			if(self.mergeRssReader)
			   self.mergeRssReader.isComplete = false;

			if( self.isTimeDisplayMode() ) {
				IS_EventDispatcher.addListener("loadComplete",widget.id,function() {
					widget.elm_widgetBox.className = "widgetBox";
					if( widget.headerContent )
						widget.headerContent.applyAllIconStyle();
				},null,true );
				
				widget.loadContents();
			} else {
				if( widget.headerContent )
					widget.headerContent.applyAllIconStyle();
			}
		};
		opt.onDrop = this.droppableOption.onWidgetDrop;
		opt.marginBottom = 10;
		
		opt = Object.extend(IS_DroppableOptions, opt);
		
		if(!isStatic) IS_Widget.RssReader.dropGroup.add(droppableElement, opt);
		
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
				(!IS_Draggables.activeDraggable.options.syncId || IS_Draggables.activeDraggable.options.syncId == widget.id));
			}
		}
		
		this.droppableOption.onMenuDrop = function(element, lastActiveElement, menuItem, event, modalOption){
			var widgetGhost = IS_Draggable.ghost;
			var ghostColumnNum = (widgetGhost.col)? widgetGhost.col.getAttribute("colNum"):1;
			var widgetConf = IS_SiteAggregationMenu.getConfigurationFromMenuItem(menuItem, ghostColumnNum);
			widgetConf.type = "RssReader";
			
//			var divWidgetDummy = element.dummy;
//			if (divWidgetDummy) {
//				element = divWidgetDummy.parentNode.replaceChild(element, divWidgetDummy);
//				element.style.top = "0px";
//				element.style.width = "auto";
//			}
			// Check merging
			if(menuItem.parentId != IS_Portal.getTrueId(widget.id).substring(2)){
				if(!IS_Droppables.mergeConfirm.call(self, element, lastActiveElement, menuItem, event, self.droppableOption.onMenuDrop, function(){
					if(widget.elm_widgetBox.className  == "dropToParent")
						widget.elm_widgetBox.className = "widgetBox";
				}, widget.title, modalOption)){
					return;
				}
			}
			/*
			if((menuItem.parentId != IS_Portal.getTrueId(widget.id).substring(2))
				&& !IS_Droppables.mergeConfirm(element, lastActiveElement, menuItem, event, this.onDrop, function(){}, widget.title, modalOption))
				return;
			*/
			
			var newWidget = IS_WidgetsContainer.addWidget( IS_Portal.currentTabId, widgetConf, false );
			IS_Portal.addSubWidget(widget, newWidget, widget.tabId);
			
			IS_Portal.widgetDropped( newWidget );
			
			self.droppableOption.onWidgetDrop.call(self, element,lastActiveElement, newWidget, event, modalOption);
		}
		menuOpt.onDrop = this.droppableOption.onMenuDrop;
		menuOpt.marginBottom = 10;
		
		if(!isStatic) IS_Widget.RssReader.dropGroup.add(droppableElement, menuOpt);

		IS_EventDispatcher.addListener('closeWidget', widget.id.substring(2),
			function(){
				IS_Widget.RssReader.dropGroup.remove(droppableElement);
			}.bind(this), true
		);
	};
	
	this.buildRssReaders = function(){
		widget.clearContents();
		
		contentInCategory.innerHTML = "";
		contentInTime.innerHTML = "";
		var errorPanel = document.createElement("div");
		contentInTime.appendChild( errorPanel );
		this.errorPanel = errorPanel;
		var mergePanel = document.createElement("div");
		mergePanel.innerHTML = "Loading...";
		contentInTime.appendChild( mergePanel );
		
		widget.elm_widgetContent.appendChild(contentInCategory);
		widget.elm_widgetContent.appendChild(contentInTime);
		
//		var subWidgets = IS_Portal.subWidgetMap[widget.id];
		var tabId = (widget.tabId)? widget.tabId : IS_Portal.currentTabId;
		var subWidgets = IS_Portal.getSubWidgetList(widget.id, tabId);
		for ( i=0; i < subWidgets.length; i++){
			var feedWidget = subWidgets[i]
			if(feedWidget){
				this.addSubWidget(feedWidget);
			}
		}
		var divEnd = document.createElement("div");
		divEnd.id = "end_" + widget.id;
		contentInCategory.appendChild(divEnd);
		
		this.mergeRssReader = this.createMergeRssReader( mergePanel );
		
		this.mergeRssReader.content.titleFilter = titleFilter;
		this.mergeRssReader.content.creatorFilter = creatorFilter;
		this.mergeRssReader.content.categoryFilter = categoryFilter;
	};
	this.createMergeRssReader = function( widgetContent ) {
		var elements = {};[
			"elm_widget","elm_widgetBox","elm_widgetContent",
			"elm_widgetHeader","elm_favoriteIcon","elm_indicator","elm_latestMark","elm_title",
			"elm_editForm","elm_widgetEditHeader"
		].each( function( elementName ) {
			elements[ elementName ] = document.createElement("div");
		});
		
		var rssReader = Object.extend( Object.extend( Object.extend( {},widget ),elements ),{
			id : widget.id,
			elm_latestMark : widget.elm_latestMark,
			elm_widgetContent : widgetContent,
			isMulti : true,

			title : IS_R.lb_mergeView,
			isComplete : false,
			parent : widget,
			loadContents : widget.loadContents
		});
		
		rssReader.elm_widgetContent.innerHTML = "Loading...";
		
		rssReader.content = new IS_Widget.RssReader( rssReader );
		rssReader.content.getRssReaders = this.getRssReaders.bind( this );
		
		rssReader.setUserPref = function(name, value){
			widget.setUserPref(name, value);
		};
		rssReader.getUserPref = function(name){
			return widget.getUserPref(name);
		};
		
		//widget.isSuccess = true; // hissu
		var errorHandlers = {
			on404 : this.handleMerge404.bind(this),
			on403 : this.handleMerge403.bind(this),
			onFailure : this.handleMergeFailureOrException.bind(this),
			onException : this.handleMergeFailureOrException.bind(this)
		}
		var option = rssReader.content.loadContentsOption;
		rssReader.content.loadContentsOption = Object.extend(
			Object.extend( Object.extend({},option ),errorHandlers ),{
				onSuccess : function() {
					this.handleMergeSuccess.apply( this,$A( arguments ))
					
					if( this.isSuccess ) {
						this.updateMergeLog();
					} else {
						this.isSuccess = true;
					}
				}.bind( this ),
				on304 : function() {
					this.handleMerge304.apply( this,$A( arguments ));
					this.updateMergeLog();
				}.bind( this ),
				on10408: this.handleMergeTimeout.bind( this,option )
			});
		option = rssReader.content.autoReloadContentsOption;
		rssReader.content.autoReloadContentsOption = Object.extend(
			Object.extend( Object.extend({},option ),errorHandlers ),{
				onSuccess : this.handleMergeSuccess.bind( this ),
				on304 : this.handleMerge304.bind( this ),
				on10408: this.handleMergeTimeout.bind(this,option )
			});
		
		rssReader.content.rssContent.onLoadPageCompletedListeners.push(
			this.handleMergeRssReaderLoadPageComplete.bind( this ));

		return rssReader;
	}
	this.addSubWidget = function(feedWidget, nextSiblingId){
		if(!feedWidget.isBuilt){
			feedWidget.build();
		}
		feedWidget.elm_widget.className = "subWidget";
		
		if(this.isCategoryDisplayMode()){
			if(!nextSiblingId) nextSiblingId = "end_" + widget.id;
			var sibling = $(nextSiblingId);
			if(sibling) {
				if(contentInCategory){
					contentInCategory.insertBefore(feedWidget.elm_widget, sibling);
				}else{
					sibling.parentNode.insertBefore(feedWidget.elm_widget, sibling);
				}
			} else {
				var end = $("end_" + IS_Portal.getTrueId(widget.id));
				if (end) {
					contentInCategory.insertBefore(feedWidget.elm_widget, end);
				}else{
					contentInCategory.appendChild(feedWidget.elm_widget);
				}
			}
		}else{
			var nextSiblingId = "end_" + widget.id;
			var sibling = $(nextSiblingId);
			contentInCategory.insertBefore(feedWidget.elm_widget, sibling);
		}
		//rssReaders.push(feedWidget);
	}
	this.initRssReader = function( rssReader ) {
		if( rssReader.isBuilt ) {
			var header = rssReader.headerContent;
			if( header ) {
				if( header.widgetEdit )
					header.widgetEdit.cancel();
				
// fix 31
				if( header.opened )
//					header.hideTools();
					header.applyAllIconStyle();
			}
		}
		
		var prefChanged = ([
			"detailDisplayMode",
			"scrollMode",
			"doLineFeed",
			"showDatetime"
		].findAll( function( prefName ) {
			var v = widget.getUserPref( prefName );
			if( v != rssReader.getUserPref( prefName )) {
				rssReader.setUserPref( prefName,v );
				
				return true;
			}
			
			return false;
		}).length > 0 );
		
		return prefChanged;
	}
	
	//var stopEvent = function(e){ Event.stop(e) };
	
	this.displayContents = function(){
		if ( this.isTimeDisplayMode()) {
			this.mergeRssReader.content.displayContents();
		} else {
			var rssReaders = this.getRssReaders();
			for(var i = 0;i <rssReaders.length;i++){
				if ( rssReaders[i].content.getRssItems().length > 0) {
					try{
						rssReaders[i].content.displayContents();
					}catch(error){
					}
				}
			}
		}
	};
	
	this.postEdit = function(){
		var prevTitleFilter = titleFilter;
		var prevCreatorFilter = creatorFilter;
		var prevCategoryFilter = categoryFilter;
		
		titleFilter = widget.getUserPref('titleFilter');
		creatorFilter = widget.getUserPref('creatorFilter');
		categoryFilter = widget.getUserPref('categoryFilter');
		if(this.mergeRssReader){
			this.mergeRssReader.content.titleFilter = titleFilter;
			this.mergeRssReader.content.creatorFilter = creatorFilter;
			this.mergeRssReader.content.categoryFilter = categoryFilter;
		}
		
		//LoadContents, if any changes occurs in filter
		if((prevTitleFilter != null && prevTitleFilter != titleFilter) ||
			(prevCreatorFilter != null && prevCreatorFilter != creatorFilter) ||
			(prevCategoryFilter != null && prevCategoryFilter != categoryFilter)) {
			widget.clearCache = true;
			widget.loadContents();
		} else {
			widget.clearCache = false;
		}
		
		this.displayContents();
	}
	
	this.isOpenWidget = function() {
		return widget.getBoolUserPref("openWidget");
	}
	this.getItemsnum = function() {
		var itemsnum = widget.getUserPref("itemsnum");
		if( isNaN( itemsnum ))
			itemsnum = parseInt( itemsnum );
		
		// Use 'defaultValue' when the value is invalid
		if( !itemsnum || itemsnum < 0 || isNaN( itemsnum ))
		//	itemsnum = IS_Widget.getConfiguration("RssReader").UserPref["itemsnum"].defaultValue;
			itemsnum = 0;
		
		return itemsnum;
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
	
	this.isCategoryDisplayMode = function() {
		return ( widget.getUserPref("displayMode") == "category")
	}
	this.isTimeDisplayMode = function() {
		return ( widget.getUserPref("displayMode") == "time")
	}
	
	this.increaseItem = function () {
		var rssReaders = this.getRssReaders();
		
		if ( this.isCategoryDisplayMode() ) {
			for (var i=0; i<rssReaders.length; i++) {
//				if (rssReaders[i].widgetConf.isChecked && rssReaders[i].content) rssReaders[i].content.increaseItem();
				if (rssReaders[i].content) rssReaders[i].content.increaseItem();
			}
		} else {
			var opend = this.isOpenWidget();
			this.mergeRssReader.content.increaseItem();
			if( !opend && widget.headerContent )
				widget.headerContent.changeTurnbkIcon();
			
			widget.elm_widgetContent.style.display = contentInTime.style.display =
				this.mergeRssReader.elm_widgetContent.style.display;
		}
	};
	this.decreaseItem = function () {
		if ( this.isCategoryDisplayMode() ) {
			var rssReaders = this.getRssReaders();
			for (var i=0; i<rssReaders.length; i++) {
//				if (rssReaders[i].widgetConf.isChecked && rssReaders[i].content) rssReaders[i].content.decreaseItem();
				if (rssReaders[i].content) rssReaders[i].content.decreaseItem();
			}
		} else {
			this.mergeRssReader.content.decreaseItem();
			widget.elm_widgetContent.style.display = contentInTime.style.display =
				this.mergeRssReader.elm_widgetContent.style.display;
		}
	};
	
	this.hideAllLatestItems = function () {
		
		widget.elm_widgetContent.style.display = "none";
		widget.setUserPref("showLatestNews", false);
		
		this.mergeRssReader.content.hideContent();
	};
	
	this.showAllLatestItems = function(e){
		if( !this.isTimeDisplayMode() )
			return;
		
		var opend = this.isOpenWidget();
		this.mergeRssReader.content.showAllLatestItems( e );
		widget.elm_widgetContent.style.display = contentInTime.style.display =
			this.mergeRssReader.elm_widgetContent.style.display;
		
		if( !opend && widget.headerContent )
			widget.headerContent.changeTurnbkIcon();
	};
	
	this.switchLineBreak = function () {
		widget.toggleBoolUserPref("doLineFeed");
		var doLineFeed = this.doLineFeed();
		
		var isTimeDisplayMode = this.isTimeDisplayMode();
		this.getRssReaders().findAll( function( rssReader ) {
			return rssReader.content.doLineFeed() != doLineFeed
		}).each( function( rssReader ) {
			rssReader.content.switchLineBreak();
			if( !isTimeDisplayMode )
				rssReader.content.repaint();
		});
		
		if( isTimeDisplayMode )
			this.mergeRssReader.content.displayContents();
	};

    this.switchShowDatetime = function(){
		widget.toggleBoolUserPref("showDatetime");
		var showDatetime = this.showDatetime();
		
		var isTimeDisplayMode = this.isTimeDisplayMode();
		this.getRssReaders().findAll( function( rssReader ) {
			return rssReader.content.showDatetime() != showDatetime
		}).each( function( rssReader ) {
			rssReader.content.switchShowDatetime();
			if( !isTimeDisplayMode )
				rssReader.content.repaint();
		});
		
		if( isTimeDisplayMode )
			this.mergeRssReader.content.displayContents();
	}
	
	this.minimize = function () {
		changeTurnbkIcon();
		if ( this.isCategoryDisplayMode()) {
			var rssReaders = this.getRssReaders();
			for ( var i=0; i< rssReaders.length;i++ ) {
				if(rssReaders[i].headerContent )
					rssReaders[i].headerContent.minimize();
			}
		} else {
			this.hideAllLatestItems();
		}
	};

	this.hideErrorMsg = function(){
		if(  !this.isOpenWidget() ){
			widget.elm_widgetContent.style.display = "none";
		}
	}

	this.turnBack = function () {
		if ( this.isCategoryDisplayMode() ) {
			var rssReaders = this.getRssReaders();
			for ( var i=0; i< rssReaders.length;i++ ) {
				if( rssReaders[i].headerContent)
					rssReaders[i].headerContent.turnBack();
			}
		} else {
			this.widgetRssTurnBack();
		}
	};

	this.widgetRssTurnBack = function(e) {
		if(widget.elm_widgetContent.style.display == "none")
			widget.elm_widgetContent.style.display = "block"
		
		if( this.isTimeDisplayMode() ) {
			contentInTime.style.display = "";
			contentInTime.style.visibility = "visible";
			this.mergeRssReader.content.showContent();
		}
	};

	function changeTurnbkIcon(){
		var divTurnBack = document.getElementById("hi_" + widget.id + "_turnBack");
		var divMinimize = document.getElementById("hi_" + widget.id + "_minimize");
		if(divTurnBack){
			if( !self.isOpenWidget() && !self.isCategoryDisplayMode() ){
				divTurnBack.style.display = "block";
			}else{
				divTurnBack.style.display = "none";
			}
		}
		if(divMinimize){
			if( self.isOpenWidget() || self.isCategoryDisplayMode() ){
				divMinimize.style.display = "block";
			}else{
				divMinimize.style.display = "none";
			}
		}
	}
	
	//Show time
	this.sort = function (divSort, divCategory) {
		widget.setUserPref("displayMode", "time");
		divSort.style.display = "none";
		divCategory.style.display = "block";
		
		while( this.errorPanel.firstChild )
			this.errorPanel.removeChild( this.errorPanel.firstChild );
		
		if( !this.isOpenWidget() )
			widget.elm_widgetContent.style.display = "none";

		contentInCategory.style.display = "none";
		contentInTime.style.display = "";
		contentInTime.style.visibility = "visible";
		
		var originalItemsCount = widget.getUserPref("itemsnum");
		
		var newItemsCount = 0;
		var rssReaders = IS_Portal.getSubWidgetList(widget.id);
		for(var i = 0; i < rssReaders.length; i++){
			var rssReader = rssReaders[i];
			if( rssReader.content.isOpenWidget()) {
				var itemsnum = rssReaders[i].getUserPref("itemsnum");
				if(itemsnum)newItemsCount += parseInt(itemsnum);
			}
		}
		if(originalItemsCount != newItemsCount+""){
			widget.setUserPref("itemsnum", newItemsCount);
		}
		//this.timeDisplayModeLoadContents( true );
		widget.setUserPref("showLatestNews","false");
		//widget.setUserPref("openWidget","true");
		
		this.mergeRssReader.loadContents();
		
		if( newItemsCount == 0 )
			this.mergeRssReader.content.hideContent();
		
		changeTurnbkIcon();
		
		//IS_EventDispatcher.newEvent("applyIconStyle",widget.id );
	};
	
	//Show category
	this.category = function (divSort, divCategory) {
		if(widget.elm_widgetContent.style.display == "none")
			widget.elm_widgetContent.style.display = "block";
		
		widget.setUserPref("displayMode", "category");
		divSort.style.display = "block";
		divCategory.style.display = "none";
		
		changeTurnbkIcon();
		
		contentInTime.style.display = "none";
		contentInTime.style.visibility = "hidden";
		contentInCategory.style.display = "";
		
		this.categoryDisplayModeLoadContents( false,false );
		
		widget.elm_latestMark.style.display = "none";
		
		//widget.setUserPref("openWidget", true);
		IS_EventDispatcher.newEvent("applyIconStyle",widget.id );
	};

	var processingClose = false;
	
	// Check for deletig all widgets
	this.checkAllClose = function(notAddTrash){
		
		if(processingClose) return; //Prevent it from being called from RssReader when close button of parent is selected
		
		var allClose = true;
		var subWidgets = IS_Portal.getSubWidgetList(widget.id, widget.tabId);
		for(var i = 0;i <subWidgets.length;i++){
			if(subWidgets[i]){
				allClose = false;
				break;
			}
		}
		if (allClose) {
			if (widget.id) {
				widget.headerContent.close(false, notAddTrash)
			  }
			else {
				var parentWidget = IS_Portal.getWidget(widget.id, widget.tabId);
				if(parentWidget)
				  parentWidget.headerContent.close(false, notAddTrash);
			}
		}
	}
		
	this.close = function(e) {
		if(e && e == "notCloseFeeds")
			return;
		
		var subWidgets = IS_Portal.getSubWidgetList(widget.id, widget.tabId);
		if (subWidgets) {
			processingClose = true;
			var loop = subWidgets.length;
			var deleteDate = null;
			for (var i = 0; i < loop; i++) {
				if(!subWidgets[i]) continue;
				//Set equal value in all 'deleteDate' of subWidgets
//				subWidgets[i].widgetConf.deleteDate = widget.widgetConf.deleteDate;
				if(deleteDate)
					subWidgets[i].widgetConf.deleteDate = deleteDate;
				subWidgets[i].headerContent.close();
				if(!deleteDate)
					deleteDate = subWidgets[i].widgetConf.deleteDate;
			}
			processingClose = false;
			widget.widgetConf.deleteDate = deleteDate;
		}
		
		if(IS_Portal.rssSearchBoxList[widget.id]){
			if(IS_Portal.rssSearchBoxList[widget.id].parentNode){
				IS_Portal.rssSearchBoxList[widget.id].parentNode.removeChild( IS_Portal.rssSearchBoxList[widget.id] );
			}
			delete IS_Portal.rssSearchBoxList[widget.id];
		}
	}
	
	/**
	 * unused
	 */
	this.dropChildWidgets = function(){
		var rssReaders = this.getRssReaders();
		for(var i = 0; i < rssReaders.length; i++){
			IS_EventDispatcher.newEvent('dropWidget', rssReaders[i].id.substring(2), null);
		}
	}
	
	this.buildEdit = function(form){
		self.buildFeedsCheckbox(form);
	}
	
	this.saveEdit = function(_widget, editForm){
		var isTimeDisplayMode = this.isTimeDisplayMode();
		
		if(needsReloadContents){
			widget.loadContents();
			if( isTimeDisplayMode ){
				while( this.errorPanel.firstChild )
				  this.errorPanel.removeChild( self.errorPanel.firstChild );
			}
			needsReloadContents = false;
		}
		
		var rssReaders = this.getRssReaders();
		for( var i=0;i<rssReaders.length;i++ ) {
			var rssReader = rssReaders[i];
			
			rssReader.setUserPref("scrollMode",widget.getUserPref("scrollMode"))
			rssReader.setUserPref("detailDisplayMode",widget.getUserPref("detailDisplayMode"))
			
			if( !isTimeDisplayMode )
				rssReader.content.repaint();
		}
		
		if( isTimeDisplayMode )
			this.mergeRssReader.content.repaint();
	}
	
	this.getRssReaders = function(){
		return IS_Portal.getSubWidgetList(widget.id, widget.tabId);
//		return rssReaders;
	};
	
	this.isError = function () {
		return (self.rssItemLength == 0)
	}

	this.getDisplayRssReaders = function(){
		var rssReaders = this.getRssReaders();
		var dispRssReaders = new Array();
		for(var i=0;i<rssReaders.length;i++){
			dispRssReaders.push(rssReaders[i]);
		}
		return dispRssReaders;
	};

	this.gc = function(){
		var rssReaders = this.getRssReaders();
		for(var i = 0; i < rssReaders; i++){
			rssReaders[i].content = null;
			rssReaders[i] = null;
		}
	}
	
	this.handleMergeRssReaderLoadComplete = function() {
		this.mergeRssReader.isSuccess = true;
		this.mergeRssReader.isComplete = true;
		if( this.isShowLatestNews())
			this.showAllLatestItems();
		
		this.mergeRssReader.content.adjustHeight();
	}
	
	this.handleMergeRssReaderLoadPageComplete = function( pageNo,page ) {
		this.mergeRssReader.content.adjustHeight();
	}
	
	this.repaint = function() {
		if( this.isTimeDisplayMode() ) {
			while( this.errorPanel.firstChild )
				this.errorPanel.removeChild( this.errorPanel.firstChild );
			
			var rss = this.mergeRssReader.content.rss;
			if( rss && rss.errors ) {
				this.displayMergeErrors( rss.errors );
			}
			
			if( this.mergeRssReader && this.mergeRssReader.content )
				this.mergeRssReader.content.repaint();
		} else {
			this.getRssReaders().each( function( rssReader )  {
				if( rssReader.content )
					rssReader.content.repaint();
			});
		}
	}
	this.mergeRssReaderLoadContents = function( isAutoReload ) {
		if( this.mergeRssReader.isLoading ) {

			msg.debug(IS_R.getResource(IS_R.ms_autoReloadCancel, [widget.id]));
			
			IS_EventDispatcher.newEvent("loadComplete",widget.id );
			return;
		}
		IS_EventDispatcher.addListener('loadComplete', widget.id,
			this.handleMergeRssReaderLoadComplete.bind(this), null, true);
		
		var opt = ( !isAutoReload? this.mergeRssReader.content.loadContentsOption :
				this.mergeRssReader.content.autoReloadContentsOption );
		widget.processLoadContentsOption( opt );
	}
	
	this.categoryDisplayModeLoadContents = function( notCompletedOnly,isAutoReload ) {
		var rssReaders = this.getRssReaders();
		if ( rssReaders.length > 0 )  {
			// Create eventTargetList
			var eventTargetList = rssReaders.findAll( function( rssReader ) {
				return ( !notCompletedOnly || !rssReader.isComplete );
			}).collect( function( rssReader ) {
				return {type:"loadComplete", id:rssReader.id};
			});
			IS_EventDispatcher.combineEvent('loadComplete', widget.id, eventTargetList, true);
			
			var sec = 0;
			for( var i = 0;i <rssReaders.length;i++ ){
				var rssReader = rssReaders[i];
				/*
				var loadFunc = ( isAutoReload ? rssReader.autoReloadContents : rssReader.loadContents )
				loadFunc = ( function() {
					var _loadFunc = loadFunc;
					
					return function() {
						var closeListener = function( w ) {
							
							IS_EventDispatcher.newEvent("loadComplete",this.id );
						}.bind( this );
						
						var loadCompleteListener = function( w ) {
							setTimeout( this.removeCloseListener.bind(this, closeListener ), 100 );
						}.bind( this );
						
						this.addCloseListener( closeListener );
						this.addLoadCompleteListener( loadCompleteListener,true );
						
						return _loadFunc.apply( this,arguments );
					}
				})();
				*/
				var loadFunc = ( isAutoReload ? rssReader.autoReloadContents : rssReader.loadContents )
				
				if( Browser.isIE ) {
					setTimeout(loadFunc.bind( rssReader ), sec * subWidgetRefreshInterval);
					sec++;
				} else {
					loadFunc.apply( rssReader );
				}
			}
		} else{
			IS_EventDispatcher.newEvent('loadComplete', widget.id, null);
		}
	}
	
	this.loadContents = function ( isAutoReload ) {
		
		if( this.getRssReaders().length > 0 ){
			if( widget.elm_widgetContent.firstChild != contentInCategory )
				this.buildRssReaders();
			
			if ( this.isTimeDisplayMode()) {
				this.mergeRssReader.isComplete = false;
				
				contentInCategory.style.display = "none";
				//if( !this.mergeRssReader.isComplete ) {
					//this.mergeRssReader.loadContents();
				this.mergeRssReaderLoadContents( isAutoReload );
				//} else {
					//IS_EventDispatcher.addListener('loadComplete', this.mergeRssReader.id, this.mergeRssReader.postLoaded.bind(this.mergeRssReader), this.mergeRssReader, true);
					
				//	this.mergeRssReader.content.repaint();
				//}
				//this.timeDisplayModeLoadContents();
			
			} else {
				this.categoryDisplayModeLoadContents( false,isAutoReload );
			}
		}else{

			// When Multi is promoted, empty Multi is created for a moment; then if widgetContent.innerHTML is used, system does not work properly
//			widget.elm_widgetContent.innerHTML = IS_R.lb_noDiplayItem;
			
			widget.headerContent.close(false, true);
			IS_EventDispatcher.newEvent('loadComplete', widget.id, null);
		}
	};
	
	this.autoReloadContents = function () {
		var rssReaders = this.getRssReaders();
		for(var i in rssReaders){
//			if(rssReaders[i].widgetConf && rssReaders[i].widgetConf.isChecked){
			if(rssReaders[i].widgetConf ){
				var convUrl = escapeXMLEntity(rssReaders[i].getUserPref("url"));
				var autoRefreshCount = IS_Portal.autoRefCountList[convUrl];
				if(!autoRefreshCount){
					autoRefreshCount = 0;
				}
				IS_Portal.autoRefCountList[convUrl] = ++autoRefreshCount;
				
				var cmd = new IS_Commands.UpdateRssMetaRefreshCommand("1",rssReaders[i].getUserPref("url"),rssReaders[i].title);
				IS_Request.LogCommandQueue.addCommand(cmd);
			}
		}
		this.loadContents( true );
	}
	
	this.handleMergeSuccess = function( req,obj ) {
		this.mergeRssReader.content.buildRssItems( req );
		
		while( this.errorPanel.firstChild )
			this.errorPanel.removeChild( this.errorPanel.firstChild );
		
		var rss = this.mergeRssReader.content.rss;
		if( rss && rss.errors ) {
			this.displayMergeErrors( rss.errors );
		}
	}
	this.displayMergeErrors = function( errors,withoutConsoleError ) {
		var this_ = this;
		
		while( this_.errorPanel.firstChild )
			this_.errorPanel.removeChild( this_.errorPanel.firstChild );
		
		var eventId = widget.id+"_errorPanel";
		IS_Event.unloadCache( eventId );
		
		$H( errors ).each( function( error ) {
			var url = error.key;
			var title = this_.getRssReaders().findAll( function( rssReader ) {
				//console.info( rssReader.getUserPref("url")+"?"+url )
				return ( rssReader.getUserPref("url") == url )
			}).shift().title;
			var errorCode = error.value;
			
			var message;
			switch( errorCode ) {
			case 401:
			case 403:

				message = IS_R.ms_mergeCategoryError403; break;
			case 404:

				message = IS_R.ms_mergeCategoryError404; break;
			case 10408:

				message = IS_R.ms_mergeCategoryError10408; break;
			case 10550:

				message = IS_R.ms_mergeCategoryError10550; break;
			case 500:
			default:

				message = IS_R.ms_mergeCategoryError; break;
			}
			
			if( !this_.notFirst &&( !this_.mergeRssReader.isSuccess || !this_.mergeRssReader.isComplete )) {
				var div = document.createElement("div");
				div.style.backgroundColor = "#FDD"
				div.style.borderBottom = "1px solid #CCC"
				div.style.fontSize = "12px"
				div.style.paddingLeft = div.style.paddingRight = 2;
				
				var messageDiv = document.createElement("span");
				messageDiv.innerHTML = IS_R.getResource( message,[title]);
				messageDiv.style.color = "#666";
				div.appendChild( messageDiv );
				
				var closeAnchor = document.createElement("a");

				closeAnchor.innerHTML = IS_R.lb_bracketClose;
				closeAnchor.href = "javascript:void(0)"
				closeAnchor.style.color = "#77C"
				//closeDiv.style.fontSize = "75%";
				div.appendChild( closeAnchor );
				
				IS_Event.observe( div,"click",this_.closeDiv.bind( this_,div ),false,eventId );
				
				this_.errorPanel.appendChild( div );
			}
			
			if( !withoutConsoleError )
				msg.error( IS_R.getResource( message,[title]));
		});
		
		this_.notFirst = true;
	}
	this.closeDiv = function( div ) {
		if( div.parentNode )
			div.parentNode.removeChild( div );
	}
	this.handleMerge304 = function( req,obj ) {
		this.mergeRssReader.content.stopLatestMarkRotate();
	}
	this.updateMergeLog = function() {
		var errorUrls = $H( this.mergeRssReader.content.rss.errors ||{}).keys();
		this.getRssReaders().each( function( rssReader ) {
			var url = rssReader.content.getUrl();
			if( !errorUrls.contains( url ) && !rssReader.isAuthenticationFailed()) {
				IS_Widget.updateLog("2",url,url );
			}
		});
	}
	this.handleMerge404 = function( req,obj ) {
		if( !this.mergeRssReader.isSuccess ) {
			this.mergeRssReader.elm_widgetContent.innerHTML =
				"<span style='font-size:90%;padding:5px;'>" + IS_R.lb_notfound +"</span>";
		}

		msg.error(IS_R.getResource(IS_R.ms_infoNotFoundAt,
			[widget.widgetType, widget.title]));
	}
	this.handleMerge403 = function( req,obj ) {
		if( !this.mergeRssReader.isSuccess ) {
			this.mergeRssReader.elm_widgetContent.innerHTML = 
				"<span style='font-size:90%;padding:5px;'>" + IS_R.ms_noPermission + "</span>";
		}
		
		msg.error(IS_R.getResource(IS_R.ms_noPermissionAt,
			[widget.widgetType, widget.title]));
	}
	this.handleMergeTimeout = function( option ) {
		option.on10408();
		
		this.mergeRssReader.isSuccess = false;
		this.mergeRssReader.isError = true;
		
		this.mergeRssReader.postLoaded();
	}
	this.handleMergeFailureOrException = function( req,obj ) {
		if( !this.mergeRssReader.isSuccess ) {
			this.mergeRssReader.elm_widgetContent.innerHTML =
				"<span style='font-size:90%;padding:5px;'>"+ IS_R.ms_getdatafailed + "</span>";
		}
		
		msg.error(IS_R.getResource(IS_R.ms_widgetonFailureAt,
			[widget.widgetType, widget.title,( req ? req.status:""),( req ? req.statusText : "")]));
	}

	this.loadContentsOption = {
		unloadCache : false,
		onSuccess : this.loadContents.bind(this)
	};
	
	this.autoReloadContentsOption = {
		onSuccess : this.autoReloadContents.bind(this)
	};
	
	this.buildFeedsCheckbox = function(form) {
		var subWidgetDivs = contentInCategory.childNodes;
		var feeds = [];
		
		for(var i = 0; i < subWidgetDivs.length; i++){
			if(subWidgetDivs[i].id.indexOf("end") < 0 ){
				feeds.push( subWidgetDivs[i].id.substring(2) );
			}
		}
		
		var divEditCheckbox = document.createElement("div");
		widget.elm_editCheckbox = divEditCheckbox;
		divEditCheckbox.id = "check_" + widget.id; 
		divEditCheckbox.className = "widgetCheckbox"; 
		
		var eachbox = '';
				
		form.appendChild(divEditCheckbox);
		var itemDisplay = widget.getUserPref("itemDisplay");
		for ( var i=0; i<feeds.length; i++ ) {
			var feedId = feeds[i];
			
			var aBlank = (Browser.isIE)? "" : "&nbsp;&nbsp;&nbsp;&nbsp;"; //for Firefox
			var vstyle = (Browser.isIE)? "" : "vertical-align:middle;";
			var rssReader = IS_Portal.getWidget('w_' + feedId);
			var eachbox = document.createElement("div");
			eachbox.style.verticalAlign = "middle";
			
			var rssIcon = document.createElement("a");
			rssIcon.href = rssReader.getUserPref('url');
			rssIcon.title = rssReader.getUserPref('url');
			rssIcon.target = "_blank";
			rssIcon.className = "rssUrl_Icon";
			rssIcon.innerHTML = aBlank;
			rssIcon.style.position = "relative";
			rssIcon.style.top = (Browser.isIE)? "-3" : "0";
			
			var titleA;
			if(rssReader.title_url) {
				titleA = document.createElement("a");
				titleA.href = rssReader.title_url;
			} else {
				titleA = document.createElement("span");
			}
			titleA.style.position = "relative";
			titleA.style.top = (Browser.isIE)? "-3" : "0";

			titleA.appendChild(document.createTextNode(rssReader.title));
			if (rssReader.title_url) {
				IS_Event.observe(titleA, 'click', function(itemDisplay){
					var aTag = this;
					if (itemDisplay == 'newwindow') {
						aTag.target = "_blank";
					}
					else {
						if (itemDisplay == "inline") 
							aTag.target = "ifrm";
						else 
							aTag.target = "";
						IS_Portal.buildIFrame(aTag);
					}
				}.bind(titleA, itemDisplay), false, widget.id);
			}
			
			eachbox.appendChild(rssIcon);
			eachbox.appendChild(titleA);

			var authType = (rssReader) ? rssReader.getUserPref("authType") : false;
			if(authType && !/PortalCredential/.test( authType )){

				var authInfoBtn = $.INPUT({
					'type':'button',
					'style':"color:#666699;font: bold 90% 'trebuchet ms',helvetica,sans-serif;backgroundColor:#eee;border:solid 1px gray;marginLeft:5px;cursor:pointer;",
					'value':IS_R.lb_setAuth
				  });
				  
				eachbox.appendChild(authInfoBtn);
				
				IS_Event.observe(authInfoBtn, "click", function(){
					var rssReader = this;
					IS_Request.createModalAuthFormDiv(
							IS_R.lb_setAuth,
							authInfoBtn,
							function (_authUid, _authPassword){
								if(typeof _authUid == 'undefined') {
									return;
								}
								var _authType = rssReader.getUserPref("authType");
								//Can be shared because the paragraph below is copied from Widgets.js
								var _authUrl = rssReader.getUserPref('url');
								var opt = {
								  method: 'post',
								  asynchronous: true,
								  parameters: {
								  	command:	"add",
								  	authType:	_authType,
								  	authUid:	_authUid,
								  	authPasswd:	_authPassword,
								  	authDomain:	"",
								  	url:		_authUrl
								  },
								  onSuccess:function(req, obj){
									  var credentialId = req.responseText;
									  if(new RegExp("[0-9]+").test(credentialId)){
										  rssReader.setUserPref('authCredentialId', req.responseText);
										  IS_EventDispatcher.newEvent("resetAuthCredential", "resetAuthCredential");
										  if( self.isTimeDisplayMode() ){
											  needsReloadContents = true;
										  }else{
											  rssReader.loadContents();
										  }
									  }else{
										  alert(IS_R.ms_errorSetAuthInfo);
									  }
								  },
								  onException:function(req, obj){
									  console.log(obj);
								  }
								}
								AjaxRequest.invoke(hostPrefix + "/credsrv", opt, self.id);
							},
							true //isModal
					);
				}.bind(rssReader) 
				);//TODO:unloadCache;
			}
			
			divEditCheckbox.appendChild(eachbox);
		}
		form.appendChild(divEditCheckbox);
	}

	function getRssReader(feedId){
		return IS_Portal.getWidget("w_" + feedId);
	}

	this.searchBuildMenuContent = function(){
		this.checkEnableRssSearch();
		return IS_Widget.RssReader.searchBuildMenuContent(widget, this.disabledSearchList);
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
			if( this.checkEnableRssSearch() )
			  div.style.display = "block";
		} else {
			IS_Portal.SearchEngines.loadConf();
			setTimeout(this.searchApplyIconStyle.bind(this, div), 100);
			return;
		}

		var disabledTitleList = [];
		var rssReaders = this.getRssReaders();
		for(var i = 0; i < rssReaders.length;i++){
			if(!IS_Portal.SearchEngines.matchRssSearch(rssReaders[i].getUserPref("url"))){
				disabledTitleList.push(rssReaders[i].title);
			}
		}
		var msgDiv = div.getElementsByClassName("rssSearchMsg")[0];

		if(disabledTitleList.length > 0) {
			msgDiv.style.display = "block";
			//					var msg = "※";
			var msg = IS_R.lb_attention;
			for(var i = 0; i < disabledTitleList.length; i++) {
				if(i > 0) msg += ", ";
				msg += "[" + disabledTitleList[i] + "]";
			}

			msg += IS_R.ms_notCoverage;
			msgDiv.innerHTML = msg;
		} else {
			msgDiv.innerHTML = "";
			msgDiv.style.display = "none";
		}
	};
	
	this.checkEnableRssSearch = function() {
		var enabled = false;
		
		var subWidgets = IS_Portal.getSubWidgetList(widget.id,widget.tabId );
		for (var i=0; i < subWidgets.length; i++){
			if(IS_Portal.SearchEngines.matchRssSearch(subWidgets[i].getUserPref("url"))){
				enabled = true;
			}
		}
		return enabled;
	};
	
	//Set detailed time
	this.dateIconHandler = function (e) {
		try{
			this.switchShowDatetime();
		}catch(error){

			msg.error(IS_R.getResource(IS_R.ms_datecreatorChangeFailed ,[widget.id, error]));
		}
		IS_Portal.widgetDisplayUpdated();
	};

	//Set line break
	this.lineFeedIconHandler = function (e) {
		try{
			this.switchLineBreak();
		}catch(error){

			msg.error(IS_R.getResource(IS_R.ms_lineChangeFailure ,[widget.id, error]));
		}
		IS_Portal.widgetDisplayUpdated();
	};
	
	this.widgetRssDownIconHandler = function(e) {
		try{
			this.increaseItem();
		}catch(error){
			console.error( error );
		}
		IS_Portal.widgetDisplayUpdated();
	}
	
	this.widgetRssUpIconHandler = function(e) {
		try{
			this.decreaseItem();
		}catch(error){
			console.error( error );
		}
		IS_Portal.widgetDisplayUpdated();
	}
	
	this.sortIconHandler = function () {
		var sortIconDiv = document.getElementById("hm_" + widget.id + "_sort");
		if(!sortIconDiv) sortIconDiv = document.getElementById("hi_" + widget.id + "_sort");
		
		var categoryIconDiv = document.getElementById("hm_" + widget.id + "_category");
		if(!categoryIconDiv) categoryIconDiv = document.getElementById("hi_" + widget.id + "_category");
		
		this.sort(sortIconDiv, categoryIconDiv);
		IS_Portal.widgetDisplayUpdated();
		
		if( widget.headerContent && widget.headerContent.hiddenMenu )
			widget.headerContent.hiddenMenu.hide();
	}
	
	this.categoryIconHandler = function () {
		var sortIconDiv = document.getElementById("hm_" + widget.id + "_sort");
		if(!sortIconDiv) sortIconDiv = document.getElementById("hi_" + widget.id + "_sort");
		
		var categoryIconDiv = document.getElementById("hm_" + widget.id + "_category");
		if(!categoryIconDiv) categoryIconDiv = document.getElementById("hi_" + widget.id + "_category");
		
		this.category(sortIconDiv, categoryIconDiv);
		IS_Portal.widgetDisplayUpdated();
		
		if( widget.headerContent && widget.headerContent.hiddenMenu )
			widget.headerContent.hiddenMenu.hide();
	}
	
	this.sortApplyIconStyle = function(div){
		if( !this.isTimeDisplayMode() ){
			div.style.display = "block";
		}else{
			div.style.display = "none";
		}
	}
	
	this.categoryApplyIconStyle = function(div){
		if( !this.isCategoryDisplayMode() ){
			div.style.display = "block";
		}else{
			div.style.display = "none";
		}
	}
	
	this.minimizeIconHandler = function (e) {
		try{
			this.minimize();
		}catch(error){

			msg.error(IS_R.getResource(IS_R.ms_minimizeFailure ,[widget.id, error]));
		}
		IS_Portal.widgetDisplayUpdated();
	};

	this.turnBackIconHandler =  function (e) {
		if (this.getRssReaders) {
			this.turnBack();
		} else {
			this.widgetRssTurnBack();
		}
		IS_Portal.widgetDisplayUpdated();
	};
	
	this.minimizeApplyIconStyle = function(div){
		var categoryAllMinimized = !( this.getRssReaders().findAll( function( rssReader ) {
			return rssReader.content.isOpenWidget();
		}).length > 0 );
		
		if( ( this.isTimeDisplayMode()&& this.isOpenWidget() )||
			( this.isCategoryDisplayMode()&& !categoryAllMinimized )){
			div.style.display = "block";
			
			widget.setUserPref("openWidget",true );
		}else{
			div.style.display = "none";
		}
	}
	
	this.turnBackApplyIconStyle = function(div){
		var categoryAllMinimized = !( this.getRssReaders().findAll( function( rssReader ) {
			return rssReader.content.isOpenWidget();
		}).length > 0 );
		
		if( ( this.isTimeDisplayMode()&& !this.isOpenWidget() )||
			( this.isCategoryDisplayMode()&& categoryAllMinimized )){
			div.style.display = "block";
			
			widget.setUserPref("openWidget",false );
		}else{
			div.style.display = "none";
		}
	}
	
	this.refresh = function() {
		var rssReaders = this.getRssReaders();
		if( rssReaders.length > 0 ) {
			if( this.isTimeDisplayMode() )
				rssReaders = [this.mergeRssReader];
			
			rssReaders.each( function( rssReader ) {
				if( rssReader.content.refresh ) {
					rssReader.content.refresh();
				} else {
					rssReader.loadContents();
				}
			});
		}
	}

	this.resetCredential = function(){
		var isReload = false;
		var subWidgets = IS_Portal.getSubWidgetList(widget.id,widget.tabId );
		for (var i=0; i < subWidgets.length; i++){
			if(subWidgets[i].getUserPref("authType")){
				isReload = true;
			}
		}
		if( subWidgets.length > 0 && isReload) {
			
			if( this.isTimeDisplayMode() ) {
				widget.loadContents();
			}else{
				this.loadContents();
			}
		}
	}
	
	this.accessStatsApplyIconStyle = function( div ) {
		div.style.display = "none";
		if( this.isTimeDisplayMode() &&
			widget.widgetPref.useAccessStat && /true/i.test( widget.widgetPref.useAccessStat.value ) ) {
			if( this.mergeRssReader.content && this.mergeRssReader.content.rss &&
				this.getRssReaders().find( function( rssReader ) {
				var errorUrls = $H(this.mergeRssReader.content.rss.errors ||{}).keys();
				
				return !errorUrls.contains( rssReader.getUserPref("url") );
			}.bind( this )) ) {
				div.style.display = "";
			} else {
				setTimeout( this.accessStatsApplyIconStyle.bind( this,div ),100 );
			}
		}
	}
	this.accessStatsIconHandler = function( div ) {
		IS_Widget.RssReader.showAccessStats(widget);
		widget.headerContent.hiddenMenu.hide();
	}
};

/**
 * Remove feed from unbuilt MultiRssReader, or remove check mark
 */
/*
IS_Widget.removeAiryRssReader = function(widget, removeId){
	var feeds = widget.widgetConf.feed;
	var allClose = true;
	for(var i = 0;i <feeds.length;i++){
		if (feeds[i].id == removeId) {
			if(feeds[i].property.relationalId != IS_Portal.getTrueId(widget.id, widget.widgetType)){
				// A case of not working together

				msg.debug(IS_R.getResource(IS_R.ms_multiRssReaderDeleteSubcategory, [widget.title, feeds[i].title]));
				
				feeds.remove(feeds[i]);
			}else{
				feeds[i].isChecked = false;
			}
			break;
		}
	}

	// Check for removing the whole widget
	for(var i = 0;i <feeds.length;i++){
		if(isDisplay(feeds[i])) allClose = false;
	}
	
	function isDisplay(feed){
		return (getBooleanValue(feed.isChecked) || feed.property.relationalId != IS_Portal.getTrueId(widget.id, widget.widgetType));
	}
	
	return allClose;
}
*/

/**
 * Add feed to unbuilt MultiRssReader, or insert check mark
 */
/*
IS_Widget.addAiryRssReader = function(widget, subWidgetConf, siblingId, tabId){
	// Check whether it is type of working with menu
	var isRelatedItem = false;
	
	if(subWidgetConf.property.relationalId == IS_Portal.getTrueId(widget.id, widget.widgetType)){
		isRelatedItem = true;
	}
	
	var subWidgetList = widget.widgetConf.feed;
	if(isRelatedItem){
		for(var i=0;i<subWidgetList.length;i++){
			if(subWidgetList[i].id == subWidgetConf.id){
				subWidgetList[i].isChecked = true;
				
				// Passing parameter
				subWidgetList[i].title = subWidgetConf.title;
				var orgProps = subWidgetConf.property;
				for(var k in orgProps){
					if(typeof orgProps[k] == "function") continue;
					subWidgetList[i].property[k] = orgProps[k];
				}
				
				subWidgetConf = subWidgetList[i];
				
				// Remove
				subWidgetList.splice( i, 1 );
				break;
			}
		}
	}
	
	if(siblingId == ""){
		subWidgetList.splice(0, 0, subWidgetConf);
	}else{
		for(var i=0;i<subWidgetList.length;i++){
			if(subWidgetList[i].id == siblingId){
				subWidgetList.splice(i+1, 0, subWidgetConf);
				break;
			}
		}
	}
	
	if(tabId != widget.tabId){
//		IS_Portal.widgetLists[tabId][subWidgetConf.id] = null;
		IS_Portal.removeWidget(subWidgetConf.id, tabId);
	}
	
	return subWidgetConf;
}
*/

/**
 * <multiRss widgetId = "">
 *   <rss method="get" url="http://hoge/hoge.rss"/>
 *   <rss method="post" url="http://hage/hage.rss">
 *     <header name="head">atama</header>
 *     <body><![CDATA[atama dekkati]]></body>
 *   </rss>
 * </multiRss>
 */
IS_Widget.MultiRssReader.getMultiProxyPostBody = function( widget ) {
	var postBody = '<?xml version="1.0" encoding="UTF-8"?>';
	if( !widget.content.getRssReaders )
		return postBody +'<multiRss widgetId="'+widget.id+'"/>';
	
	var rssReaders = widget.content.getRssReaders();
	var checker = rssReaders.collect( function( rssReader ) {
		return rssReader.id+"@"+rssReader.isSuccess;
	}).join("|");
	var cacheEnable = widget.isComplete && (widget.isSuccess || widget.isError )&&
		( widget.cacheHeaders && widget.cacheHeadersChecker )&&
		( widget.cacheHeadersChecker == checker );
	
	postBody += '<multiRss widgetId="'+widget.id+'"'+( !cacheEnable ?' clearCache="true"':'')+'>';
	
	widget.cacheHeadersChecker = checker;
	rssReaders.each( function( rssReader ) {
		var url = rssReader.getUserPref("url");
		var method = "get";
		
		var authType = rssReader.getUserPref("authType");
		if( authType && !/basic/i.test( authType ))
			method = "post";
		
		postBody += '<rss method="'+method+'" widgetId="' +  rssReader.id + '" url="' + escapeXMLEntity(url) + '">';
		var headers = [];
		if( cacheEnable && widget.cacheHeaders[url] ) {
			$H( widget.cacheHeaders[url] ).each( function( cacheHeader ) {
				var headerName = cacheHeader.key;
				if( headerName == "etag") {
					headerName = "If-None-Match";
				} else if( headerName == "last-modified") {
					headerName = "If-Modified-Since"
				}
				headers.push('<header name="'+headerName+'">'+cacheHeader.value+'</header>')
			});
		}

		var authCredentialId = rssReader.getUserPref("authCredentialId");
		if(authCredentialId){
			headers.push('<header name="authCredentialId">'+authCredentialId+'</header>');
		}
		if(authType){
			headers.push('<header name="authType">'+authType+'</header>');
			
			var authUserId = rssReader.getUserPref("authUserId");
			var authPassword = rssReader.getUserPref("authPassword");
			if( authUserId &&  authPassword){
				headers.push('<header name="authUserId">'+authUserId+'</header>');
				headers.push('<header name="authPassword">'+authPassword+'</header>');
			}
		}
		postBody += headers.join("");
		
		postBody += '</rss>';
	});
	
	postBody += "</multiRss>";
	
	return postBody;
}

IS_Widget.getSubCategoryNextSibling = function(widget, id){
	var id = IS_Portal.getTrueId(id);
	var orderList = [];
	
	if (widget.isBuilt) {
		var nodelist = widget.elm_widgetContent.firstChild.childNodes;
		for(var i=0;i<nodelist.length;i++){
			orderList.push(IS_Portal.getTrueId(nodelist[i].id));
		}
	}else{
		var subList = IS_Portal.getSubWidgetList(widget, widget.tabId);
		for(var i=0;i<subList.length;i++){
			orderList.push(IS_Portal.getTrueId(subList[i].id));
		}
	}
	
	for(var i=0;i<orderList.length;i++){
		if(id == orderList[i]){
			return (orderList.length > i+1)? orderList[i+1] : "";
		}
	}
	
	return "";
}

IS_Widget.getDisplayOrderList = function(widget, tabId){
	var rssReaders = IS_Portal.getSubWidgetList(widget.id, widget.tabId);
	if(!rssReaders || rssReaders.length == 0) return rssReaders;
	
	// Change order properly
	if(widget.isBuilt){
		var orderList = [];
		var tempRssReaders = [];
		var nodelist = widget.elm_widgetContent.firstChild.childNodes;
		for(var i=0;i<nodelist.length;i++){
			if(nodelist[i].id)
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
	return rssReaders;
}
IS_Widget.MultiRssReader.validateUserPref = IS_Widget.RssReader.validateUserPref;
