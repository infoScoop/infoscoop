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

IS_Widget.MiniBrowser = IS_Class.create();
IS_Widget.MiniBrowser.prototype.classDef = function() {
	var widget;
	var self = this;
	var isStatic;
	var staticWidgetHeight;
	var isForbiddenURL = isInvalidURL = false;
	
	this.initialize = function(widgetObj){
		widget = widgetObj;
		
		isStatic = widget.panelType == "StaticPanel";
		
		if( Browser.isIE ) {
			IS_EventDispatcher.addListener("moveWidget",widget.id,function() {
				this.handleMoveWidget();
			}.bind( this ) );
		}
	}
	
	this.displayContents = function () {
		if(!getBooleanValue(widget.getUserPref("openWidget"))){
			this.hideIframe();
		}else{
			this.showIframe();
		}
	};
	
	this.postEdit = this.displayContents;
	
	this.minimize = function (e) {
		if (widget.iframe || widget.isError || isForbiddenURL || isInvalidURL)
			this.hideIframe();
		
		if(window.event){
			window.event.cancelBubble = true;
		}
		if(e && e.stopPropagation){
			e.stopPropagation();
		}
	};

	this.turnBackIconHandler = function (e) {
		this.showIframe();
	};

	this.minimizeIconHandler = function (e) {
		try{
			this.minimize(e);
			IS_Portal.widgetDisplayUpdated();
		}catch(error){

			msg.error( IS_R.getResource( IS_R.ms_minimizeFailure,[widget.id,error]));
		}
	};

	this.loadUrl = function() {
		// In case of adding at first
		if(!widget.getUserPref("url") || widget.getUserPref("url").length == 0){
			widget.setUserPref("url", widget.widgetConf.href);
		}
		var url = widget.replaceUserPref(widget.getUserPref("url"));
		var iframeSrc = widget.iframe.src;
		if(iframeSrc != "./blank.html" && iframeSrc != hostPrefix + "/blank.html"){
			widget.iframe.contentWindow.location.reload( true );
		}else{
			widget.iframe.src = "./iframe.jsp?url="+encodeURIComponent( url )+ "&scrolling=auto";
		}
	};
	this.refresh = function() {
		if(isForbiddenURL || isInvalidURL) return;
		widget.preLoad();
		IS_EventDispatcher.addListener('loadComplete', widget.id, widget.postLoaded.bind(widget), widget, true);
		widget.iframe.contentWindow.location.reload( false );
	}

	this.initIframe = function() {
		
		if( !widget.iframe )
			widget.initIframe( true );
	};

	this.hideIframe = function () {
		if(widget.iframe) {
			widget.iframe.style.height = "0";
		} else {
			widget.elm_widgetContent.style.display = "none";
		}
		var adjustBar = $( widget.id + "_heightAdjustBar" );
		if(adjustBar)
			adjustBar.style.display = "none";
	};

	this.showIframe = function () {
		if( widget.elm_widgetContent.style.display == "none")
			widget.elm_widgetContent.style.display = "";
		
		if(widget.iframe){
			if( widget.staticWidgetHeight > 0 ) {
				widget.iframe.style.height = widget.staticWidgetHeight;
			} else if(widget.getUserPref("height")) {
				widget.iframe.style.height = widget.getUserPref("height");
			}
			widget.iframe.style.overflow = "";
		} else {
			widget.elm_widgetContent.style.height = widget.staticWidgetHeight > 0 ? widget.staticWidgetHeight : widget.getUserPref("height");
			widget.elm_widgetContent.style.overflow = "auto";
		}
		var adjustBar = $( widget.id + "_heightAdjustBar" );
		if(adjustBar)
			adjustBar.style.display = "block";
	};

	this.buildAdjustHeightBar = function () {
		var barId = widget.id + "_heightAdjustBar";
		var adjustBar = $(barId);
		if(!adjustBar ){
			adjustBar = document.createElement("div");
			adjustBar.id = barId;
			widget.elm_widgetShade.appendChild(adjustBar);
			adjustBar.className = "adjustMiniBrowserBarOut";
			
			if( !/^previewWidget/.test( widget.id ) ) {
				var handle_side_bar = document.createElement("div");
				handle_side_bar.className = "handleSideBar";
				adjustBar.appendChild( handle_side_bar );
				var handle = document.createElement("div");
				handle.className = "adjustMiniBrowserBarHandle";
				adjustBar.appendChild( handle );
			}
		}
		
		IS_Event.observe(adjustBar, 'mousedown', IS_Widget.MiniBrowser.adjustHeight.start.bind(adjustBar, adjustBar, widget), false, widget.id);
		IS_Event.observe(adjustBar, 'mouseover', IS_Widget.MiniBrowser.adjustHeight.showBar.bind(adjustBar, adjustBar), false, widget.id);
		IS_Event.observe(adjustBar, 'mouseout', IS_Widget.MiniBrowser.adjustHeight.hideBar.bind(adjustBar, adjustBar), false, widget.id);
	};

	this.loadContents = function () {
		var url = widget.getUserPref("url");
		if( !url || url == "") {
			url = widget.widgetConf.href;
		}
		
		if( !url || url == "" || /^\s*$/.test( url ) ) {
			isInvalidURL = true;
			

			widget.elm_widgetContent.innerHTML = '<span class="errorMsg">' + IS_R.ms_invalidURL + '</span>';
			
			if( !widget.getBoolUserPref("openWidget") && widget.headerContent )
				widget.headerContent.turnBack();
			
			this.showIframe();
		}else if(IS_Widget.MiniBrowser.isForbiddenURL( url )) {
			isForbiddenURL = true;

			widget.elm_widgetContent.innerHTML = '<span class="errorMsg">' + IS_R.ms_forbiddenURL + '</span>';
			
			if( !widget.getBoolUserPref("openWidget") && widget.headerContent )
				widget.headerContent.turnBack();
			
			this.showIframe();
		}else{
			this.initIframe();
			
			if(widget.panelType == "DynamicPanel")
				this.buildAdjustHeightBar();
			console.log("before loadUrl");
			this.loadUrl();
			this.displayContents();
		}
		//changeTurnbkIcon();
		IS_EventDispatcher.newEvent('loadComplete', widget.id, null);
	};

	this.loadContentsOption = {
		request : false,
		onSuccess : self.loadContents.bind(self)
	};

	/*this.errorCheck = function(){
		var form = widget.elm_editForm;
		if(form && form.height){
			var height = form.height.value;
			var error = IS_Widget.MiniBrowser.validateUserPref.height(height);
			if(error) {
				alert(error);
				return false;
			}
		}
		return true;
	};*/
	this._IS_Validate = {
		height: IS_Widget.MiniBrowser.validateUserPref.height
	}

	this.handleMoveWidget = function() {
	//Return to the default URL
		is_swapIFrameSRC( widget.iframe );
	}
	
	this.maximize = function() {
		var adjustBar = $( widget.id + "_heightAdjustBar" );
		if( adjustBar ) adjustBar.style.display = "none";
	}
	
	this.turnbackMaximize = function() {
		var adjustBar = $( widget.id + "_heightAdjustBar" );
		if( adjustBar ) adjustBar.style.display = "";
	}
}
/**
 * Adjust height
 */
IS_Widget.MiniBrowser.adjustHeight = {
	start: function(barDiv, widget, e) {
		IS_Portal.showDragOverlay(Element.getStyle(barDiv, "cursor"));
		IS_Widget.MiniBrowser.adjustHeight.isDragging = true;
		
		IS_Widget.MiniBrowser.adjustHeight.widget = widget;
		IS_Widget.MiniBrowser.adjustHeight.barDiv = barDiv;
		
		IS_Widget.MiniBrowser.adjustHeight.startY = Event.pointerY(e);
		
		Event.observe(document, "mousemove", IS_Widget.MiniBrowser.adjustHeight.move, false);
		Event.observe(document, "mouseup", IS_Widget.MiniBrowser.adjustHeight.end, false);
		
		// Prevent event from being passed to upper level
		Event.stop(e);
	},
	move: function(e) {
		if(IS_Widget.MiniBrowser.adjustHeight.isChanging) return;
		
		// effect
		if(IS_Widget.MiniBrowser.adjustHeight.scrollTimer){
			clearTimeout(IS_Widget.MiniBrowser.adjustHeight.scrollTimer);
		}
		if(IS_Widget.MiniBrowser.adjustHeight.timer){
			clearTimeout(IS_Widget.MiniBrowser.adjustHeight.timer);
		}
		
		IS_Widget.MiniBrowser.adjustHeight.endY = Event.pointerY(e);
		
		if(!Browser.isIE) {
			IS_Widget.MiniBrowser.adjustHeight.autoScroll();
			IS_Widget.MiniBrowser.adjustHeight.changeHeight();
		} else {
			IS_Widget.MiniBrowser.adjustHeight.scrollTimer = setTimeout(IS_Widget.MiniBrowser.adjustHeight.autoScroll, 5);
			IS_Widget.MiniBrowser.adjustHeight.timer = setTimeout(IS_Widget.MiniBrowser.adjustHeight.changeHeight, 5);
		}
		
		// Prevent event from being passed to upper level
		Event.stop(e);
	},
	end: function(e) {
		if(IS_Widget.MiniBrowser.adjustHeight.scrollTimer){
			clearTimeout(IS_Widget.MiniBrowser.adjustHeight.scrollTimer);
		}
		if(IS_Widget.MiniBrowser.adjustHeight.timer){
			clearTimeout(IS_Widget.MiniBrowser.adjustHeight.timer);
		}
		IS_Portal.hideDragOverlay();
		Event.stopObserving(document, "mousemove", IS_Widget.MiniBrowser.adjustHeight.move, false);
		Event.stopObserving(document, "mouseup", IS_Widget.MiniBrowser.adjustHeight.end, false);
		
		IS_Widget.MiniBrowser.adjustHeight.isDragging = false;
		
		IS_Widget.MiniBrowser.adjustHeight.hideBar(IS_Widget.MiniBrowser.adjustHeight.barDiv);
		IS_Portal.widgetDisplayUpdated();
	},
	changeHeight: function(e) {
		IS_Widget.MiniBrowser.adjustHeight.isChanging = true;
		
		var startY = IS_Widget.MiniBrowser.adjustHeight.startY;
		var endY = IS_Widget.MiniBrowser.adjustHeight.endY;
		var iframeY = IS_Widget.MiniBrowser.adjustHeight.widget.iframe.offsetHeight;
		var setHeight = iframeY + (endY - startY);
		
		// Set location of START again
		if(setHeight < 1) setHeight = 1;
		
		IS_Widget.MiniBrowser.adjustHeight.widget.iframe.style.height = setHeight;
		IS_Widget.MiniBrowser.adjustHeight.isChanging = false;
		// Set location of START again
		IS_Widget.MiniBrowser.adjustHeight.startY = endY;
		
		// Set in UserPref
		IS_Widget.MiniBrowser.adjustHeight.widget.setUserPref("height", setHeight);
	},
	autoScroll: function(e) {
		// Automatic scroll
		var endY = IS_Widget.MiniBrowser.adjustHeight.endY;
		var scrollY = document.body.scrollTop;
		var offset = 0;
		if(endY - scrollY < 50) {
			offset = -20;
		} else if (endY - scrollY > document.body.clientHeight - 50) {
			offset = 20;
		}
		if(offset != 0){
			scrollY += offset;
			scrollTo(0, scrollY);
		}
	},
	showBar: function(barDiv, e) {
		if(barDiv.className == "adjustMiniBrowserBarOut")
			barDiv.className = "adjustMiniBrowserBarOver";
	},
	hideBar: function(barDiv, e) {
		if(IS_Widget.MiniBrowser.adjustHeight.isDragging) return;
		if(barDiv.className == "adjustMiniBrowserBarOver")
			barDiv.className = "adjustMiniBrowserBarOut";
	}
}

IS_Widget.MiniBrowser.isForbiddenURL = function( forbiddenURL ) {
	
	for(i in IS_forbiddenURLs){
		if(IS_forbiddenURLs[i].url){
			//var url = decodeURIComponent(IS_forbiddenURLs[i].url);
			var url = IS_forbiddenURLs[i].url;
			try {
				var regexp = new RegExp( url,"i" );
				if( regexp.test( forbiddenURL ) )
					return true;
			} catch( ex ) {
				msg.warn("reguar expression: ["+url+"] is not valid. cause: "+ex );
				continue;
			}
		}
	}
	return false;
}

IS_Widget.MiniBrowser.validateUserPref = {
	height:function(value){
		return IS_Validator.validate(value, {

			label: IS_Widget.getDisplayName('MiniBrowser', 'height'),
			required: true,
			regex: '^(([1-9][0-9]*)|([1-9]))$',

			regexMsg: IS_R.ms_gtZeroNum
		});
	},
	url:function(value){
		return IS_Validator.validate(value, {
//			label: 'URL',
			label: IS_Widget.getDisplayName('MiniBrowser', 'url'),
			required: true,
			regex: '^((?:http)|(?:https)|(?:ftp))://',

			regexMsg: IS_R.ms_invalidURL,
			maxBytes: 1024
		});
	}
}
