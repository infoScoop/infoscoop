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

IS_Widget.WidgetHeader = IS_Class.create();
IS_Widget.WidgetHeader.prototype.classDef = function() {
	var self = this;
	var widget;
	var isStatic;
	this.hiddenIcons = [];
	this.visibleIcons = [];
	
	//this.opened=false;
	var onMove=false;
	var effect=null;
	
	var observeEventsFunc;

	var headerDiv;
	var titleHeaderDiv;
	
	var maximizeHeaderDiv;
	var maximizeHeaderTitleTd;
	
	var maximizeIcons = [
		{
		  type:	"refresh",
		  imgUrl:	"refresh.gif",

		  alt: IS_R.lb_refresh
		},
		{
		  type:	"turnbackMaximize",
		  imgUrl:	"turnback.gif",

		  alt: IS_R.lb_turnback
		}
		];
	
	this.initialize = function (widgetObj) {
		observeEventsFunc = observeEvents.bind(widgetObj);
		widget = widgetObj;
		isStatic = (widget.panelType == "StaticPanel");
		this.buildContents();
	}

	var enableInLoadingTypes = ["showTools", "close","turnbackMaximize"];
	function isEnableLoading(type){
		return enableInLoadingTypes.contains( type );
	}
	var commonTypes = ["close","refresh","edit","systemIconMinimize","turnbackMinimize",
					   "maximize","turnBack","minimize","turnbackMaximize","showTools","hideTools"];
	function isCommonType(type){
		return  commonTypes.indexOf( type ) >= 0;
	}
	this.buildContents = function () {
		headerDiv = document.createElement("div");
		headerDiv.style.width = "100%";
		headerDiv.style.position = "relative";
		headerDiv.style.height = "19px";
		headerDiv.style.overflow = "hidden";
		widget.elm_widgetHeader.appendChild(headerDiv);
		
		IS_Event.observe( headerDiv, "mouseover", observeEventsFunc, false, widget.closeId);
		
		titleHeaderDiv = document.createElement("div");
		//titleHeaderDiv.style.marginTop = "2px";
		
		headerDiv.appendChild(titleHeaderDiv);
		
		var widgetConf  = IS_WidgetConfiguration[widget.widgetType];
		var header = widgetConf.Header;
		//build title
		//var titleHeader = header.titleHeader;
		//if(titleHeader.option) {
			//for(var i in titleHeader.option) {
			//	if(!( titleHeader.option[i] instanceof Function ))
					self.buildTitleHeader();
			//}
		//}
		
		//build rightHeader
		var headerIconDiv = document.createElement("div");
		headerIconDiv.className = "rightHeader"
		
		var visibleCount = 0;
		
		var hiddens = header.menu;
		
		var hiddenIcons = [];
		if(isBuildEdit()){
			hiddenIcons.push(
				{
				  type:  "edit",
				  imgUrl: "edit.gif",
				  alt: IS_R.lb_setting
				}
				);
		}
		
		if(hiddens)
		  hiddenIcons = hiddenIcons.concat(hiddens);

		if(!isStatic)
		  hiddenIcons.push(
			  {
				type:  "close",
				imgUrl: "trash.gif",
				alt: IS_R.lb_delete
			  }
			  );
		
		this.hiddenIcons = hiddenIcons;
		this.hiddenMenu = new IS_Widget.WidgetHeader.MenuPullDown(this.elm_showTool, widget.id, widget.closeId);
		for ( var j=0; j<hiddenIcons.length; j++ ) {
			var iconType = hiddenIcons[j].type;
			if(this.isHiddenIcon(iconType)) continue;
			var staticDisabled = getBooleanValue(hiddenIcons[j].staticDisabled);
			if(isStatic && staticDisabled) continue;
			var alt = hiddenIcons[j].alt;
			var imgUrl = hiddenIcons[j].imgUrl;
			var iconDiv = this.createMenuDiv(iconType, alt, imgUrl, "");
			
			var div = iconDiv;
				
			var handler;
			if (isCommonType(iconType)) {
				handler = this.common.bind(this, this[iconType].bind(this), isEnableLoading(iconType), div);
			}else {
				handler = this.common.bind(this, handleIconClick.bind(null, iconType, div), false, div);
			}

			var buildMenuFunc = widget.getContentFunction( iconType +"BuildMenuContent");
			this.hiddenMenu.addMenu({
			  type:iconType,
			  anchor: (buildMenuFunc) ? false : true,
			  label: alt,
			  icon: div,
			  handler: (buildMenuFunc) ? false : handler,
			  buildMenuFunc: buildMenuFunc
			});

		}
		
		var visibles = header.icon;

		var visibleIcons = (visibles) ? [].concat(visibles) : [];
		if(!widget.originalWidget){
			if(header.refresh != 'off'){
			  visibleIcons.push({
				type:	"refresh",
				imgUrl:	"refresh.gif",
				  
				alt: IS_R.lb_refresh
			  });
			}
			if(!isStatic && header.minimize != 'off'){
				visibleIcons.push(
					{
					  type:  "minimize",
					  imgUrl: "_.gif",
					  alt: IS_R.lb_minimize
					});
				
				visibleIcons.push(
					{
					  type:  "turnBack",
					  imgUrl: "turnback.gif",
					  alt: IS_R.lb_turnback
					}
					);
			}
			if(header.maximize != 'off'){
				visibleIcons.push(
					{
					  type:  "maximize",
					  imgUrl: "maximum.gif",
					  alt: IS_R.lb_maximize
					});
			}
		}
		this.visibleIcons = visibleIcons;
		for ( var j=0; j<visibleIcons.length; j++ ) {
			var iconType = visibleIcons[j].type;
			if(this.isHiddenIcon(iconType)) continue;
			var staticDisabled = getBooleanValue(visibleIcons[j].staticDisabled);
			if(isStatic && staticDisabled) continue;
			
			var alt = visibleIcons[j].alt;
			var imgUrl = visibleIcons[j].imgUrl;
			
			// Not show edit icon if UserPref has nothing to show
			if(iconType == "edit" && !isBuildEdit()) continue;
			var iconDiv = this.createIconDiv(iconType, alt, imgUrl, "block");
			if( !iconDiv ) {

				console.error(IS_R.getResource(IS_R.ms_invalidIconType,[widget.widgetType,iconType,alt,imgUrl]));
			} else {
				var div = document.createElement("div");
				$(div).setStyle({"float":"left"});
				div.appendChild(iconDiv);
				headerIconDiv.appendChild(div);
				this.stockEvents(iconType, iconDiv);
				visibleCount++;
			}
		}
		
		if(!header.disableMenu && (!widget.originalWidget && hiddenIcons && hiddenIcons.length > 0)){
			//showToolsButton
			var div =  this.createIconDiv("showTools", "", "show_hidden_icons.gif", "block");
			$(div).setStyle({"float":"left"});
			headerIconDiv.appendChild( div );
			this.stockEvents("showTools", div);
			
			visibleCount++;
		}
		titleHeaderDiv.style.marginRight = ((visibleCount - 1) * 16 + 10) + "px";//Minus 1 for maximizing and minimizing, and not consider 'search' and 'access statics' as they are set in menu
		headerDiv.appendChild(headerIconDiv);
		self.stockEvent(headerIconDiv, 'mousedown', this.common.bind(this, this.dummy, false, headerIconDiv), false, widget.closeId);
		
		//this.applyAllIconStyle();
	}

	
	this.switchToMaximizeHeader = function(){
		if(!maximizeHeaderDiv){
			
			maximizeHeaderDiv = document.createElement("div");
			maximizeHeaderDiv.style.width = "100%";
			maximizeHeaderDiv.style.cursor = "default";
			maximizeHeaderDiv.style.position = "relative";
			
			maximizeHeaderTitleDiv = document.createElement("div");
			maximizeHeaderTitleDiv.className = "widgetTitle";
			maximizeHeaderTitleDiv.style.sccFloat = 'left';
			maximizeHeaderDiv.appendChild(maximizeHeaderTitleDiv);
			
			var headerIconDiv = document.createElement("div");
			headerIconDiv.className = "rightHeader"
			
			for ( var j=0; j<maximizeIcons.length; j++ ) {
				var iconType = maximizeIcons[j].type;
				var alt = maximizeIcons[j].alt;
				var imgUrl = maximizeIcons[j].imgUrl;
				
				var iconDiv = this.createIconDiv(iconType, alt, imgUrl, "block");
				if( !iconDiv ) {

					console.error(IS_R.getResource(IS_R.ms_invalidIconType,[widget.widgetType,iconType,alt,imgUrl]));
				}else{
					var div = document.createElement("div");
					$(div).setStyle({"float":"left"});
					div.appendChild(iconDiv);
					headerIconDiv.appendChild(div);
					IS_Event.observe(div, 'mousedown', this.iconDown.bind(this, div), false, widget.closeId);
					IS_Event.observe(div, 'mouseup', this.iconUp.bind(this, div), false, widget.closeId);
					IS_Event.observe(div, 'mouseout', this.iconUp.bind(this, div), false, widget.closeId);
					IS_Event.observe(div, 'mouseup', this.common.bind(this, this[iconType].bind(this), isEnableLoading(iconType), div), false, widget.closeId);
				}
			}
			maximizeHeaderDiv.appendChild(headerIconDiv);
		}
		if (maximizeHeaderTitleDiv.firstChild) {
			maximizeHeaderTitleDiv.replaceChild(document.createTextNode(this.getTitle()), maximizeHeaderTitleDiv.firstChild);
		}else {
			maximizeHeaderTitleDiv.appendChild(document.createTextNode(this.getTitle()));
		}
		widget.elm_widgetHeader.replaceChild(maximizeHeaderDiv, headerDiv);
	}
	
	this.switchFromMaximizeHeader = function(){
		widget.elm_widgetHeader.replaceChild(headerDiv, maximizeHeaderDiv);
	}
	
	this.isHiddenIcon = function(iconType){
		if(widget.panelType == "StaticPanel"){
			switch(iconType){
				case "close":
				case "minimize":
				case "turnBack":
				case "systemIconMinimize":
				case "turnbackMinimize":
					return true;
			}
		}
		return false;
	}

	this.dummy = function (){};
	
	this.buildTitleHeader = function(){
		while(titleHeaderDiv.firstChild){
			titleHeaderDiv.removeChild(titleHeaderDiv.firstChild);
		}
		var titleHeaderTable = document.createElement("table");
		titleHeaderTable.cellSpacing = 0;
		titleHeaderTable.cellPadding = 0;
		var titleHeaderTBody =document.createElement("tbody");
		titleHeaderTable.appendChild(titleHeaderTBody);
		var titleHeaderTr = document.createElement("tr");
		titleHeaderTBody.appendChild(titleHeaderTr);
		
		var indicatorDiv = document.createElement("td");
		
		indicatorDiv.appendChild(widget.elm_indicator);
		titleHeaderTr.appendChild(indicatorDiv);
		
		if(widget.elm_favoriteIcon){
			var favoriteIconDiv = document.createElement("td");
			//$(favoriteIconDiv).setStyle({"float":"left"});
			favoriteIconDiv.appendChild(widget.elm_favoriteIcon);
			titleHeaderTr.appendChild(favoriteIconDiv);
		}
		
		var titleTd = document.createElement("td");
		widget.elm_title.id = widget.id + "_widgetTitle";
		widget.elm_title.className = "widgetTitle";
		//$(widget.elm_title).setStyle({"float":"left"});
		titleTd.appendChild(widget.elm_title);
		titleHeaderTr.appendChild(titleTd);
		
		this.buildTitle();


		widget.elm_latestMark.id = "m_" + widget.id;
		
		widget.eventTargetList.push({element:widget.elm_latestMark});
		self.stockEvent(widget.elm_latestMark, 'mousedown', this.showLatestNews.bind( this ), true, widget.closeId);
		
		var latestMarkTd = document.createElement("td");
		latestMarkTd.appendChild(widget.elm_latestMark);
		titleHeaderTr.appendChild(latestMarkTd);
		titleHeaderDiv.appendChild(titleHeaderTable);
	}
	
	this.buildTitle = function(){
		var displayTitle = this.getTitle();
		widget.elm_title.title = displayTitle;
		
		var atags = widget.elm_title.getElementsByTagName("a");
		var aTag = false;
		if(atags.length > 0){
			aTag = atags[0];
		}
		
		if ( widget.title_url &&
			(( widget.panelType == "DynamicPanel")||( widget.panelType == "StaticPanel")) ) {
				
			if (!aTag) {
				aTag = document.createElement('a');
				
				aTag.href = widget.title_url;
				aTag.appendChild(document.createTextNode(displayTitle));//To show HTML: escape more
				//aTag.target = "ifrm";
				
				self.stockEvent(aTag, 'mousedown', this.common.bind(this, this.dummy, false, aTag), false, widget.closeId);
				var isGadget = /^g_/.test(widget.widgetType);
				
				var aTagOnclick = function(e){
					var itemDisplay = widget.getUserPref("itemDisplay");

					if(/^javascript:/i.test( widget.title_url )){
						eval( widget.title_url );
						Event.stop(e);
					}
					else if (itemDisplay == 'newwindow') {
						aTag.target = "_blank";
					} else {
						if(itemDisplay == "inline")
							aTag.target = "ifrm";
						else
							aTag.target = "";
						IS_Portal.buildIFrame(aTag);
					}
				}
				self.stockEvent(aTag, "click", aTagOnclick, false, widget.closeId);
				if (widget.elm_title.firstChild) {
					widget.elm_title.replaceChild(aTag, widget.elm_title.firstChild);
				} else {
					widget.elm_title.appendChild(aTag);
				}
			}else{
				aTag.href = widget.title_url;
				if (aTag.firstChild) {
					aTag.replaceChild(document.createTextNode(displayTitle), aTag.firstChild);//To show HTML: escape more
				}else{
					aTag.appendChild(document.createTextNode(displayTitle));//To show HTML: escape more
				}
			}
		} else {
			if (widget.elm_title.firstChild) {
				widget.elm_title.replaceChild(document.createTextNode(displayTitle), widget.elm_title.firstChild);
			}else{
				widget.elm_title.appendChild(document.createTextNode(displayTitle));
			}
			
		}
		
		if( maximizeHeaderDiv ) {
			if (maximizeHeaderTitleDiv.firstChild) {
				maximizeHeaderTitleDiv.replaceChild(document.createTextNode(this.getTitle()), maximizeHeaderTitleDiv.firstChild);
			}else {
				maximizeHeaderTitleDiv.appendChild(document.createTextNode(this.getTitle()));
			}
		}
	}
	
	if( Browser.isSafari1 ) {
		this.buildTitle = (function() {
			var buildTitle = this.buildTitle;
			
			return function( type ) {
				buildTitle.apply( this,[type] );
				
				if( !widget.title_url ) {
					widget.elm_title.style.position = "relative";
					var dragHandle = document.createElement("div");
					dragHandle.style.position = "absolute"
					dragHandle.style.top = dragHandle.style.left = '0px';
					dragHandle.style.width = dragHandle.style.height = "100%";
					widget.elm_title.appendChild( dragHandle );
				}
			}
		}).apply( this );
	}

	this.getTitle = function() {
		return IS_Widget.WidgetHeader.getTitle(widget);
	}
	
	this.updateTitle = function() {
		var container = widget.elm_title;
		if( !container.firstChild )
			return;
		
		if( container.firstChild.firstChild ) // elm_title/A/#text
			container = container.firstChild;
		
		var displayTitle = IS_Widget.WidgetHeader.getTitle( widget );
		var title = document.createTextNode(displayTitle);
		container.replaceChild( title,container.firstChild );
		container.title = displayTitle;
	}
	
	this.createIconDiv = function(type, alt, imgUrl, disp) {
		var isCommon = isCommonType(type);
		if( !isCommon && !hasIconHandler( type ) && !widget.isGadget() ){
			return null;
		}
		
		var div = this.createIcon( type,alt,imgUrl);
		div.style.display = disp;

		if( isCommon ) {
			switch(type){
			  case 'close':
				div.style.verticalAlign = 'bottom';
				break;
			  case 'refresh':
				this.elm_refresh = div;
				break;
			  case 'systemIconMinimize':
				alert('systemIconMinimize');
			  case 'minimize':
				if( !widget.getBoolUserPref("openWidget"))
				  div.style.display = "none";
				break;
			  case 'turnBack':
				if( widget.getBoolUserPref("openWidget"))
					div.style.display = "none";
				break;
			  case 'showTools':
				this.elm_showTool = div;

				div.title = IS_R.lb_openMenu;
				break;
			  case 'maximize':
			  case 'turnbackMaximize':
			  case 'edit':
			  default:
				//do nothing
			}
		}
		
		return div;
	}
	
	this.createMenuDiv = function(type, alt, imgUrl, disp) {
		
		var div = this.createIcon( type,alt,imgUrl, true);
		div.style.display = disp;
		
		return div;
	}
	
	this.createIcon = function(type, alt, imgUrl, omitApplyIconStyle) {
		var div = document.createElement("img");
		div.border = "0";
		div.className = 'headerIcon';
		div.id = "hi_" + widget.id + "_" + type;
		div.title = alt;
		if(imgUrl){
			var url = '';
			if(/__IS_GADGET_BASE_URL__/.test(imgUrl) && widget.gadgetType){
				imgUrl = imgUrl.replace("__IS_GADGET_BASE_URL__", hostPrefix + '/gadget/' + widget.gadgetType)
			}
			if(/http[s]?:\/\//.test(imgUrl)){
				url = imgUrl;
			}else{
				url = (!isCommonType(type) && widget.resourceUrl ? widget.resourceUrl : imageURL) + imgUrl;
			}
			div.src = url;
		}
		

		if( type == "turnbackMaximize") {
			div.style.margin = "0px";
			
			var labelDiv = document.createElement("div");
			labelDiv.className = 'headerIcon_turnbackMaximize';
			labelDiv.href = "javascript:void(0)";
			
			labelDiv.appendChild( div );
			
			var labelText = document.createElement("span");
			labelText.appendChild(document.createTextNode(

				IS_R.lb_turnbackMaximize ));
				
			labelText.style.position = "relative";
			labelText.style.top = "3px";
			
			labelDiv.appendChild(labelText);
			
			div = labelDiv;
		}
		
		if(!isEnableLoading(type)) {
			widget.eventTargetList.push({type:type, element:div});
		}
		
		if(!omitApplyIconStyle &&  hasApplyIconStyle( type ) ){
			( getApplyIconStyle( type ))(div);
			
			IS_EventDispatcher.addListener("applyIconStyle", widget.id,applyIconStyle.bind( null,type ));
		}
		
		return div;
	}

	this.stockEvents = function(type, div){
		
		self.stockEvent(div, 'mousedown', this.iconDown.bind(this, div), false, widget.closeId);
		self.stockEvent(div, 'mouseup', this.iconUp.bind(this, div), false, widget.closeId);
		self.stockEvent(div, 'mouseout', this.iconUp.bind(this, div), false, widget.closeId);
		
		try {
			if(isCommonType(type)) {
				self.stockEvent(div, 'mouseup', this.common.bind(this, this[type].bind(this), isEnableLoading(type), div), false, widget.closeId);
			} else {
				self.stockEvent(div, 'mouseup', this.common.bind(this, handleIconClick.bind( null,type, div), false, div), false, widget.closeId);
			}
		} catch( ex ) {
			msg.error( ex );
		}
	}
	
	
	this.stockedEvents = [];
	this.stockEvent = function(element, name, observer, useCapture, id){
		this.stockedEvents.push({"element":element, "name":name, "observer":observer, "useCapture":useCapture, "id":id});
		//IS_Event.observe(element, name, observer, useCapture, id);
	}
	
	function observeEvents(){
//		console.log("self.stockedEvents.length="+self.stockedEvents.length);
		for(var i=0;i<self.stockedEvents.length;i++){
			var ebObj = self.stockedEvents[i];
			if(ebObj) IS_Event.observe(ebObj.element, ebObj.name, ebObj.observer, ebObj.useCapture, ebObj.id);
		}
		this.stockedEvents = [];
		
	    if (widget.draggable) {
			this.draggble = new IS_Draggable(widget.elm_widget, {
				handle: headerDiv,
				revert: false,
				ghosting: true,
				move: true,
				dragMode: "widget",
				widgetType: widget.widgetType,
				widget: widget,
				onStart: function(draggble, e){
					var element = draggble.element;
					
					element.beforeParent = element.parentNode;
					element.beforeNextSibling = element.nextSibling;
					
					if (IS_Portal.isSubWidget(widget.id) && !Browser.isSafari1 ) {
						document.body.appendChild(element);
					}
					
					//Delete description when moving
					IS_Widget.RssReader.RssItemRender.hideRssDesc();
					IS_EventDispatcher.newEvent("dragWidget", widget.id);
				},
				onEnd: function(draggable, e){
				// Adjust description when drag finishes
				//IS_Widget.processAdjustRssDesc();
				},
				getDropObject: function(){
					return widget;
				},
				getDummy: function(element){
					return IS_Widget.getDragDummy(element, this);
				}.bind(widget),
				startDroppableElement: function(){
					return widget.parent ? widget.parent.elm_widget : document.body;
				}
			});
			
			IS_EventDispatcher.addListener('closeWidget', widget.id.substring(2), function(){
				widget.draggble.destroy();
			}.bind(widget), true);
		}

		//IS_Event.stopObserving( headerTable, "mouseover", observeEventsFunc, false);
		IS_Event.stopObserving( headerDiv, "mouseover", observeEventsFunc, false);
	}
	
	function applyIconStyle( type ){
		var div = $( "hi_" + widget.id + "_" + type);
		
		if( !div || !hasApplyIconStyle( type ) )
			return;
		
		( getApplyIconStyle( type ))(div);
		
	}
		
	function handleIconClick( type,div ) {
		var handler = getIconHandler( type );
		if( handler )
			handler( div );
		
		if( Browser.isSafari1 ) self.adjustWidth();
	}
	
	function getIconHandler( type ) {
		return widget.getContentFunction( type+"IconHandler" );
	}
	function hasIconHandler( type ) {
		return widget.hasContentFunction( type+"IconHandler" );
	}
	function getApplyIconStyle( type ) {
		return widget.getContentFunction( type +"ApplyIconStyle");
	}
	function hasApplyIconStyle( type ) {
		return widget.hasContentFunction( type+"ApplyIconStyle" );
	}
	
	this.iconUp = function (div, e) {
		div.style.marginTop = "2px";
		div.style.marginBottom = "1px";
	}
	
	this.iconDown = function (div, e) {
		div.style.marginTop = "3px";
		div.style.marginBottom = "0px";
		
		IS_Event.stop(e);
		
		IS_Widget.RssReader.RssItemRender.hideRssDesc();
	}

	this.common = function (func, enableInLoading, div, e) {
		IS_Event.stop( e );
		if(div.disabled) return;
		if (widget.enableEvent || enableInLoading) {
			func(e);
		}
	}

	//Start to edit
	this.edit = function (e) {
		
		this.hiddenMenu.hide();
		if(!this.widgetEdit)
			this.widgetEdit = new IS_Widget.WidgetEdit(widget);
		this.widgetEdit.displayContents();
		widget.elm_widgetEditHeader.style.display="block";
		if(isStatic)
			IS_Portal.behindIframe.show(widget.elm_widgetEditHeader);
		
		IS_Widget.adjustEditPanelsTextWidth();
	};
	
	this.close = function (e, notAddTrash) {
		var tabId = (widget.tabId)? widget.tabId : IS_Portal.currentTabId;

		this.hiddenMenu.destroy();
		
		if(widget.content && widget.content.close) {
			widget.content.close(e, notAddTrash);
		}
		//Add to trash box
		if(!notAddTrash)
			IS_Portal.Trash.add(widget);
		
		IS_EventDispatcher.newEvent("loadComplete", widget.id );//Refresh icon does not stop if it is deleted during loading
		IS_EventDispatcher.newEvent('closeWidget', widget.id.substring(2), null);
		
		var url = widget.getUserPref("url");
//		if( url ) IS_EventDispatcher.newEvent( IS_Widget.CLOSE_URL,url,widget );
		
		if(widget.parent)
			IS_Portal.removeSubWidget(widget.parent.id, widget.id, widget.parent.tabId);
//		IS_Portal.widgetLists[tabId][widget.id] = null;
		IS_Portal.removeWidget(widget.id, tabId);
//		IS_Portal.subWidgetLists[tabId][widget.id] = null;
		try{AjaxRequest.cancel(widget.id);}catch(ex){msg.error(ex);}
		try{IS_Event.unloadCache(widget.id);}catch(ex){msg.error(ex);}
		try{IS_Event.unloadCache(widget.closeId);}catch(ex){msg.error(ex);}
		if(widget.elm_widget.parentNode){
			widget.elm_widget.parentNode.removeChild(widget.elm_widget);
			widget.isBuilt = false;
		}
		if(self.titleEditBox && self.titleEditBox.parentNode){
			self.titleEditBox.parentNode.removeChild( self.titleEditBox );
		}
		
		IS_Portal.widgetDisplayUpdated();
		
		//Send to Server
		if (notAddTrash) {
			var cmd = new IS_Commands.EmptyWidgetCommand(widget.widgetConf, widget.tabId.substring(3));
			IS_Request.CommandQueue.addCommand(cmd);
		} else {
			IS_Widget.removeWidgetCommand(widget);
		}
	}

	this.changeTurnbkIcon = function(){
		var openWidget = widget.getBoolUserPref("openWidget");
		//console.log("chageTurnbkIcon:"+ openWidget);
		var divTurnBack = $("hi_" + widget.id + "_turnBack");
		if(divTurnBack){
			if( !openWidget ){
				divTurnBack.style.display = "block";
			}else{
				divTurnBack.style.display = "none";
			}
		}
		
		var divMinimize = $("hi_" + widget.id + "_minimize");
		if(divMinimize){
			if( openWidget ){
				divMinimize.style.display = "block";
			}else{
				divMinimize.style.display = "none";
			}
		}
		
		if( widget.parent ) {
			IS_EventDispatcher.newEvent("applyIconStyle", widget.parent.id,"minimize" );
			IS_EventDispatcher.newEvent("applyIconStyle", widget.parent.id,"turnBack" );
		}
	}

	this.minimize = function( e ){
		if( e ) Event.stop( e );
		
		//console.log("call minimize");
		var openWidget = widget.getBoolUserPref("openWidget");
		if( !openWidget )
			return;
		
		widget.setUserPref("openWidget", false);
		
		this.changeTurnbkIcon();
		
		if(widget.content && widget.content.minimizeIconHandler){
			//console.log("call content function");
			widget.content.minimizeIconHandler(e);
		} else {
			widget.elm_widgetContent.style.display = "none";
		}
	}
	this.turnBack = function( e ){
		if( e ) Event.stop( e );
		
		//console.log("call turnback");
		if( widget.getBoolUserPref("openWidget") )
			return;
		
		widget.setUserPref("openWidget", true);
		
		this.changeTurnbkIcon();
		
		if(widget.content && widget.content.turnBackIconHandler){
			//console.log("call content turnback function");
			widget.content.turnBackIconHandler(e);
		} else {
			widget.elm_widgetContent.style.display = "block";
		}
		
		if(/^g_/.test( widget.widgetType)) // Adjust gadget height if it is set to do it automatically
			IS_Portal.adjustGadgetHeight( widget );
	}
	
	this.maximize = function() {
		widget.changeMaximize();
	}
	this.turnbackMaximize = function() {
		widget.turnbackMaximize();
		//Fixed Issue 149: Fragment Minibrowser shows a little off from the position whrere it should be when it maximized.
		if(fixedPortalHeader)
			IS_Portal.adjustPanelHeight();
	}
	
	this.showLatestNews = function(e) {
		try{
			widget.content.showAllLatestItems(e);
		}catch(error){
			msg.error(error);
		}
	}
	
	this.refresh = function (e) {
		widget.refresh();
	}
	
	this.showTools = function (e){
		this.hiddenMenu.show( Event.element(e) );
		this.hiddenIcons.each( function( icon, index ){
			applyMenuStyle( icon.type );
		});
		Event.stop(e);
	}
	
	function applyMenuStyle( type){
		var menuDiv = $( "hm_"+widget.id+"_"+type);
		if( !menuDiv || !hasApplyIconStyle( type ) )
			return;
		( getApplyIconStyle( type ))(menuDiv);
		
	}
	
	this.applyAllIconStyle = function(){
		this.visibleIcons.each( function( icon,index ){
			applyIconStyle( icon.type );
		});
	};
	
	function isBuildEdit(){
		var editNode = IS_WidgetConfiguration[widget.widgetType];
		var userPref = editNode.UserPref;
		
		var titleEdit = widget.widgetPref && widget.widgetPref.titleEdit && getBooleanValue(widget.widgetPref.titleEdit.value) ||
			getBooleanValue( widget.getUserPref("titleEdit"));
		if( titleEdit ) return true;
		
		for( id in userPref ){
			if(!(userPref[id] instanceof Function)){
				var pref = userPref[id];
				if((isStatic && pref.name == "height") ||  pref.datatype == "hidden"){
				}else{
					return true;
				}
			}
		}
		return false
	}
}

IS_Widget.WidgetHeader.adjustHeaderWidth = function() {
}

IS_Widget.WidgetHeader.getTitle = function(widget){
	var displayTitle = widget.title;
	if(!displayTitle) {
		displayTitle = IS_R.lb_notitle;
		widget.title = displayTitle;
		widget.isNoTitle = true;
	}
	
	if( displayTitle.indexOf("__UP_") > -1 ){
		var buf = "";
		var pos = -1;
		var regexp = /__UP_(\w+?)__/;
		var useDirectoryTitle = true;
		while( ( pos = displayTitle.search( regexp )) >= 0 ) {
			var prefName = regexp.exec( displayTitle )[1];
			var prefValue = widget.getUserPref( prefName );
			if( !prefValue )
				prefValue = "";
			else
				useDirectoryTitle = false;
			
			buf += displayTitle.substring( 0,pos )+prefValue;
			displayTitle = displayTitle.substring( pos +prefName.length+7 );
		}
		
		buf += displayTitle.substring( pos );
		displayTitle = buf;
		
		if( useDirectoryTitle ) {
			var typeConf = IS_Widget.getConfiguration(widget.widgetType);
			if( typeConf && typeConf.ModulePrefs && typeConf.ModulePrefs.directory_title){
				var directory_title = typeConf.ModulePrefs.directory_title;
				if( ( !displayTitle || displayTitle == "")&& directory_title ) {
					return directory_title;
				}
			}
		}
	}
	
	return displayTitle;
}

IS_Widget.WidgetHeader.MenuPullDown = function(element, widgetId, eventKey){
	this.targetElement = element;
	this.eventKey = eventKey;
	this.menuOptList = [];
	
	var isInit = false;
	
	this.addMenu = function(opt){
		this.menuOptList.push(opt);
	}
	
	this.build = function(){
		var menuDiv = document.createElement("div");
		menuDiv.id = (this.eventKey + "_menu");
		menuDiv.className = "widgetMenu";
		menuDiv.style.display = "none";
		for(var i=0;i<this.menuOptList.length;i++){
			var itemDiv = createItem(this.eventKey, this.menuOptList[i]);
			itemDiv.id = "hm_" + widgetId + "_" + this.menuOptList[i].type;
			menuDiv.appendChild( itemDiv );
		}
		menuDiv.style.top = '0px';
		document.body.appendChild(menuDiv);
		
		this.elm_menu = menuDiv;
		isInit = true;
		
		function createItem( eventKey, opt ) {
			var className = opt.className || "";
			
			var borderDiv = document.createElement("div");
			borderDiv.style.borderBottom = '1px solid #EEE';
			
			var itemDiv = document.createElement( opt.anchor ? "a":"span");
			itemDiv.className = className + " item";
			itemDiv.style.borderBottom = 'none';
			//itemDiv.style.position = "relative";
			
			if( opt.anchor )
				itemDiv.href = "javascript:void(0)";
			
			if (opt.icon) {
				itemDiv.appendChild( opt.icon );
				/*
				$(opt.icon).setStyle({
					position: "absolute",
					top: 0,
					left: 0
				});
				*/
			}
			
			var content = document.createElement("span");
			content.style.whiteSpace = 'nowrap';
			content.style.paddingLeft = '2px';
			content.style.position = "relative";
			content.style.top = '-2px';
			
			if (opt.label) {
				var labelSpan = document.createElement("s");
				content.appendChild(document.createTextNode(opt.label));
			}
			
			if( opt.buildMenuFunc )
			  content.appendChild(opt.buildMenuFunc());
			 
			if( opt.content )
				content.appendChild( opt.content );
			
			itemDiv.appendChild( content );
			
			if( opt.handler )
				IS_Event.observe( itemDiv, "click", opt.handler, false, this.eventKey );
			
			borderDiv.appendChild( itemDiv );
			
			return borderDiv;
		}
		
		var closer = document.createElement("div");
		closer.id = (this.eventKey + "_closer");
		closer.className = "widgetMenuCloser";
		closer.style.display = "none";
		document.body.appendChild( closer );
		
		var handleHideMenu = this.hide.bind( this );
		IS_Event.observe(closer, 'click',handleHideMenu, true, this.eventKey);
	}
	
	this.show = function(element){
		if(!isInit) {
			this.build();
			
			// Because IE freezes and FF cannot get the proper position
			return setTimeout( this.show.bind( this,element ),10 );
		}
		
		var winX = Math.max(document.documentElement.scrollWidth, document.documentElement.clientWidth);
		var winY = Math.max(document.documentElement.scrollHeight, document.documentElement.clientHeight);

		
		var closer = $(this.eventKey + "_closer");
		
		closer.style.width = winX + 'px';
		closer.style.height = winY + 'px';
		closer.style.display = "";
		
		if (!isInit && this.elm_menu.style.display != "none") {
			this.elm_menu.style.display = "none";
			IS_Portal.behindIframe.hide();
		} else {
			this.elm_menu.style.visibility = "hidden";
			this.elm_menu.style.display = "block";
			//calculate far left on menu
			Position.prepare();
			var showToolsDiv = element;
			var xy = Position.cumulativeOffset(showToolsDiv);
			if(fixedPortalHeader)
				xy[1] -= IS_Portal.tabs[IS_Portal.currentTabId].panel.scrollTop;
			
			var offsetX= xy[0];
			if( (offsetX + this.elm_menu.offsetWidth ) > winX ){//if the width of the whole menu is bigger than the distance between the left end of top menu and the right end of window
				//offsetX = (winX  - this.elm_menu.offsetWidth) - 10;
				this.elm_menu.style.left = "auto";
				this.elm_menu.style.right = fixedPortalHeader ? '16px':'10px';
			}else{
				this.elm_menu.style.right = "auto";
				this.elm_menu.style.left = offsetX+'px';
			}
			var offsetY = xy[1] + showToolsDiv.offsetHeight;
			if((offsetY + this.elm_menu.offsetHeight) > winY){
				offsetY = winY - this.elm_menu.offsetHeight;
			}
			this.elm_menu.style.top = offsetY + 'px';
			this.elm_menu.style.visibility = "visible";
			
			IS_Portal.behindIframe.show(this.elm_menu);
						
			Position.prepare();
			var tail = Position.cumulativeOffset( showToolsDiv )[1] + this.elm_menu.offsetHeight;
			var limit = getWindowHeight() +document.body.scrollTop;
			
			if( !( tail < limit )){
				var scrollTop = document.body.scrollTop + tail -limit +16;
				document.body.scrollTop = scrollTop + 'px';
			}
		}
		IS_Event.observe(window, 'resize', this.handleHideMenu, false, this.eventKey);
	}
	
	this.hide = function(e) {
		var menu = this.elm_menu;
		var selectMenu = this.targetElement;
		var changeColumnSelect = $(this.id +"_menu_change_column_select");
		var closer = $(this.eventKey + "_closer");
		
		IS_Event.stopObserving( window,'resize',this.handleHideMenu );
		/*
		if( e ) {
			var element = Event.element( e );
			if( element && (Element.childOf( element, menu ) || selectMenu && Element.childOf( element,selectMenu ) ||
				menu.style.display == "none")) return;
		}
		*/
		if( menu ) menu.style.display = "none";
		if( closer ) closer.style.display = "none";
		
		IS_Portal.behindIframe.hide();

		// Forcus remains when using IE
		var targetNode = false;
		try{
			targetNode = Event.element(e);
		}catch(e){
			// ignore
		}
		if( Browser.isIE && e && targetNode && targetNode.type != 'input')
			document.body.focus();
	}
	
	this.handleHideMenu = this.hide.bind( this );
	
	this.destroy = function(){
		var menu = this.elm_menu;
		var closer = $(this.eventKey + "_closer");
		if(menu) Element.remove(menu);
		if(closer) Element.remove(closer);
		IS_Portal.behindIframe.hide();
		
		if( Browser.isIE )
			document.body.focus();
	}
}
