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

IS_Widget.FragmentMiniBrowser = IS_Class.extend(IS_Widget.MiniBrowser);
IS_Widget.FragmentMiniBrowser.prototype.classDef = function() {
	var widget;
	var self = this;
	var adjustBar;
	
	var fragmentServerURL = hostPrefix + '/proxy';
	
	var isStatic;
	
	this.initialize = function(widgetObj){
		widget = widgetObj;
		IS_EventDispatcher.addListener("adjustedColumnWidth", null, this.setHeight.bind(this), widget, false);
		IS_EventDispatcher.addListener("adjustedSiteMap", null, this.setHeight.bind(this), widget, false);
		IS_EventDispatcher.addListener("changeTab", null, this.setHeight.bind(this), widget, false);
		IS_EventDispatcher.addListener("moveWidget", widget.id, this.setHeight.bind(this), widget, false);
		IS_Event.observe( window,"resize", this.setHeight.bind(this), false, widget.closeId);
		
		isStatic = widget.panelType == "StaticPanel" && widget.isStaticHeight;
	}
	
	this.refresh = function() {
		widget.loadContents();
		
		if( widget.originalWidget )
			this.adjustMaximizeHeight();
	}

	this.loadContentsOption = {
		asynchronous : true,
		request : true,
		unloadCache : false,
		method: 'post',
		preLoad: function(){
			var url = widget.getUserPref("url");
			if( !url || url == "") {
				url = widget.widgetConf.href;
			}
			
			if( url && url != "" && !/^\s*((?:http)|(?:https)|(?:ftp)):\/\//i.test( url ) ) {

				widget.elm_widgetContent.innerHTML = IS_R.ms_invalidURL;
				
				IS_EventDispatcher.newEvent('loadComplete', widget.id, null);
				return;
			}
						
			var url = fragmentServerURL
			this.loadContentsOption.url = url;
			this.loadContentsOption.parameters = "filter=HTMLFragment&url=" + encodeURIComponent(widget.getUserPref("url")) + "&additional_css=" + encodeURIComponent(widget.getUserPref("additional_css"));
			this.loadContentsOption.requestHeaders = ["fragment-xpath", widget.getUserPref("xPath")];
			if(widget.getUserPref("charset")){
				this.loadContentsOption.requestHeaders.push("fragment-charset");
				this.loadContentsOption.requestHeaders.push(widget.getUserPref("charset"));
			}
			if(widget.getUserPref("cacheID")){
				this.loadContentsOption.requestHeaders.push("fragment-chacheID");
				this.loadContentsOption.requestHeaders.push(widget.getUserPref("cacheID"));
			}else{
				this.loadContentsOption.requestHeaders.push("MSDPortal-Cache");
				this.loadContentsOption.requestHeaders.push("Cache-NoResponse");
			}
			
			if(widget.getUserPref("cacheLifeTime")){
				this.loadContentsOption.requestHeaders.push("fragment-cacheLifeTime");
				this.loadContentsOption.requestHeaders.push(widget.getUserPref("cacheLifeTime"));
			}
			return true;
		}.bind(this),
		onSuccess : function(req){
			var cacheID = req.getResponseHeader("MSDPortal-Cache-ID");
			
			self.initIframe();
			if(widget.panelType == "DynamicPanel")
				self.buildAdjustHeightBar();
			self.displayContents();

			var fragmentCacheURL = hostPrefix + "/cacsrv?id=" + cacheID;
			if( widget.iframe.src && widget.iframe.src == fragmentCacheURL ) {
				widget.iframe.contentWindow.location.reload( true );
			} else {
				widget.iframe.src = fragmentCacheURL;
			}
		},
		onComplete : function(req){
		}
	};
	
	this.initIframe = function() {
		if(!widget.iframe){
			widget.initIframe();
			var iframe = widget.iframe;
			
			Event.observe(iframe, "load", this._setPrefs.bind(this), false);
		}
		
		if( !isStatic )
			widget.iframe.style.height = "100%";
		
		widget.iframe.style.width = "100%";
	};

	this._setPrefs = function(){
		var iframeDoc = Browser.isIE ? widget.iframe.contentWindow.document : widget.iframe.contentDocument;
		
		// Not run process when loading; judge forcibly
		var urlNode = iframeDoc.getElementById("fragmentURL");
		var xPathNode = iframeDoc.getElementById("fragmentXPath");
		if(urlNode &&xPathNode) return;

		this.setHeight();
		
		function replaceTarget(ifLinks, listener){
			for(var i = 0; i < ifLinks.length; i++) {
				if(!ifLinks[i].target || ifLinks[i].target == "_self" 
					|| ifLinks[i].target == "_top" || ifLinks[i].target == "_parent") {
					var linkClick = function(aTag) {
						var itemDisplay = widget.getUserPref("itemDisplay");
						if(itemDisplay){
							if(itemDisplay == "newwindow")
								aTag.target = "_blank";
							else if(itemDisplay == "inline")
								aTag.target = "ifrm";
							else
								aTag.target = "";
						}
						
						if(itemDisplay != "newwindow")
							IS_Portal.buildIFrame(aTag);
					}
					IS_Event.observe(ifLinks[i], listener, linkClick.bind(this, ifLinks[i]), false, widget.id);
				}
			}
		}
		// Apply itemDisplay
		if(iframeDoc) {
			var ifLinks = iframeDoc.documentElement.getElementsByTagName("a");
			if(ifLinks)
				replaceTarget(ifLinks, 'click');
			if(iframeDoc.forms)
				replaceTarget(iframeDoc.forms, 'submit');
		}
	}
	
	this.setHeight = function(){
		// disable setHeight when it is minimized
		if(!getBooleanValue(widget.getUserPref("openWidget")) || !widget.iframe )
			return;
		
		// disable setHeight when it is maximized
		if( widget.originalWidget || IS_Widget.MaximizeWidget == widget )
			return this.adjustMaximizeHeight();
		
		if( isStatic )
			return;
		
		var iframeDoc;
		try{
			iframeDoc = Browser.isIE ? widget.iframe.contentWindow.document : widget.iframe.contentDocument;
		}catch(e){
			msg.warn(getText(e));
			return;
		}
		
		var height = widget.getUserPref("height");
		var isAuto = (height == "auto" || height == "");
		height = parseInt(height);
		var offset = 20;
		
		if(Browser.isIE){
			if(iframeDoc && iframeDoc.body){
				widget.iframe.style.height = ((isAuto)? (iframeDoc.body.scrollHeight) + offset : height) + "px";
			}
		} else {
			if(iframeDoc && iframeDoc.body){
				widget.iframe.style.height = 0;
				widget.iframe.style.height =
					((isAuto)? IS_Widget.FragmentMiniBrowser.getMiniBrowserHeight(iframeDoc) + offset: height) + "px";
			}
		}

	}
	
	this.showIframe = function () {
		widget.elm_widgetContent.style.display = "";
		if(widget.iframe){
			this.setHeight();
			if(!isStatic) {
				widget.iframe.style.overflow = "";
			}
		}
		if(adjustBar)
			adjustBar.style.display = "block";
	};
	
	this._IS_Validate = {
		height: IS_Widget.FragmentMiniBrowser.validateUserPref.height
	}
	
	this.adjustMaximizeHeight = function() {
		if(widget.iframe)
			widget.iframe.style.height = (getWindowSize(false) - findPosY( widget.elm_widgetContent ) -6) + "px";
	}
	
	this.turnBackIconHandler = function (e) {
		if(Browser.isFirefox)
		  this.refresh();
		this.showIframe();
	};
	
	this.turnbackMaximize = function() {console.info("turnbackMaximize Fra!!");
		var adjustBar = $( widget.id + "_heightAdjustBar" );
		if( adjustBar ) adjustBar.style.display = "";
		if(Browser.isFirefox) setTimeout(this.setHeight, 100);
	}
}

// If the function cannot get body height, return available child height
IS_Widget.FragmentMiniBrowser.getMiniBrowserHeight = function( element ) {
	var height = element.documentElement.offsetHeight;
	if (!(!isNaN(height) && height > 0)) {
		element = element.body? element.body : element;
		while (element != null) {
			height = parseInt(element.offsetHeight) ? parseInt(element.offsetHeight) : parseInt(element.scrollHeight);
			
			if (height > 0) 
				break;
			
			element = element.firstChild;
		}
	}
	return height;
}

IS_Widget.FragmentMiniBrowser.validateUserPref = Object.extend(IS_Widget.FragmentMiniBrowser.validateUserPref, {
	height:function(value){
		return IS_Validator.validate(value, {

			label: IS_Widget.getDisplayName('FragmentMiniBrowser', 'height'),
			required: true,
			regex: '^((([1-9][0-9]*)|([1-9]))|(auto))$',

			regexMsg: IS_R.ms_gtZeroOrAuto
		});
	},
	charset:function(value){
		return IS_Validator.validate(value, {

			label: IS_Widget.getDisplayName('FragmentMiniBrowser', 'charset'),
			format: 'charset'
		});
	},
	cacheLifeTime:function(value){
		return IS_Validator.validate(value, {

			label: IS_Widget.getDisplayName('FragmentMiniBrowser', 'cacheLifeTime'),
			regex: '^(([1-9][0-9]*)|([1-9]))$',

			regexMsg: IS_R.ms_gtOneNum
		});
	},
	xPath:function(value){
		return IS_Validator.validate(value, {
			label: IS_Widget.getDisplayName('FragmentMiniBrowser', 'xPath'),
			required: true
		});
	}
});
