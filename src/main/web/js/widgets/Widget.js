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

var IS_Widget = IS_Class.create();

IS_Widget.DROP_WIDGET = "dropWidget";
IS_Widget.CLOSE_WIDGET = "closeWidget";
IS_Widget.DROP_URL = "dropUrl";
IS_Widget.CLOSE_URL = "closeUrl";
IS_Widget.DEFAULT_VIEW = "home";
IS_Widget.DEFAULT_MAXIMIZE_VIEW = "canvas";

IS_Widget.prototype.classDef = function() {
	//var self = this;
	var self = null;
	var contentsDef;
	var contentsType;
	var typeConf;
	var userPref = {};
	
	this.initialize = function(draggable, widgetsXml){
		self = this;
		
		//For a case that it is set as property of menu
		if(widgetsXml.type && widgetsXml.type == "Gadget"){
			var url = widgetsXml.property["url"];
			if(url) {
				widgetsXml.type = "g_" + url;
				delete widgetsXml.property["url"];
			} else {
				msg.error("Gadget requires url property.");
			}
		}
		
		this.panelType = "DynamicPanel";
		this.widgetConf = widgetsXml;

		this.preUserPref = Object.extend({}, widgetsXml.property);
		this.id = widgetsXml.id;
		this.closeId = this.id + "_close";
		this.column = widgetsXml.column;
		this.widgetType = widgetsXml.type;
		this.draggable = (isTabView)? false : draggable;
		this.refreshInterval = widgetsXml.refreshInterval;
		
		//List and status for event
		this.eventTargetList = [];
		this.enableEvent = true;
		
		this.isBuilt = false;

		typeConf = IS_Widget.getConfiguration(this.widgetType);
		if(!typeConf || (typeConf && typeConf.type == "notAvailable")){
			typeConf = IS_Widget.getConfiguration("notAvailable");
			this.widgetType = "notAvailable";
			this.title = typeConf.title;

			msg.error(IS_R.getResource(IS_R.ms_configNotFound, [this.id]));
		}else{
			var _contentsDef = typeConf.Content;
			var _contentsType;
			//if(this.widgetType.indexOf("g_") == 0){
			if(this.isGadget()){
				switch(_contentsDef.type){
				  case 'html':
					_contentsType = 'htmlIframe';
					break;
				  case 'html-inline':
					_contentsType = 'html';
					break;
				  case 'url':
					_contentsType = 'url';
					break;
				  case 'javascript':
					_contentsType = 'javascript';
					break;
				}
				if(this.widgetType.match(/^g_upload__(.*)\/gadget/)){
					this.gadgetType = RegExp.$1;
				}
				else if(this.widgetType.match(/^g__Maximize__upload__(.*)\/gadget/)){
					this.gadgetType = RegExp.$1;
				}
			}else{
				_contentsType = (_contentsDef) ? _contentsDef.type : "javascript";
			}
			if(_contentsType == "javascript") {
				if(this.widgetType.match(/^g_upload__(.*)\/gadget/)){
					this.resourceUrl = typeConf.resource_url || ('.' + '/gadget/'+this.gadgetType+'/');
					if(typeof IS_Widget[_contentsDef.className] == "undefined"){
						var head = $$('head')[0];
						head.appendChild(
							$.SCRIPT({
							  type:'text/javascript',
							  src:this.resourceUrl + _contentsDef.className+'.js'
							})
							);
						var cssUrl =  this.resourceUrl + _contentsDef.className+'.css';
						head.appendChild(
							$.LINK({
								'rel':'stylesheet',
								'type':'text/css',
								'href':cssUrl
							  }));
					}
				}else if(typeof IS_Widget[_contentsDef.className] == "undefined"){
					typeConf = IS_Widget.getConfiguration("notAvailable");
					this.widgetType = "notAvailable";

					msg.error(IS_R.getResource(IS_R.ms_classNotFounf, [_contentsDef.className]));
				}
			}
			contentsDef = _contentsDef;
			contentsType = _contentsType;
			//this.title = typeConf.title; //refs 1878
			this.title_url = typeConf.href;
			this.scrolling = getBooleanValue(typeConf.scrolling || (typeConf.ModulePrefs && typeConf.ModulePrefs.scrolling));

			userPref = IS_Widget.mergePreference(typeConf, widgetsXml);
			if(isTabView)
				this.setLiteModePreference();
			
			this.widgetPref = typeConf.WidgetPref;
			if(!this.widgetPref) this.widgetPref = new Object();
		}
		
		//userPref["openWidget"] = this.getBoolUserPref("openWidget");
		//Default value of openWidget is true
		if(typeof this.getUserPref("openWidget") == "undefined")
			userPref["openWidget"] = true;
		
		var ar = typeConf.autoRefresh;
		// upload gadget
		if (!ar && typeConf.ModulePrefs) {
			ar = typeConf.ModulePrefs.autoRefresh;
		}
		this.autoRefresh = (ar &&  ( /true/.test(ar) || /TRUE/.test(ar) ) ) ? true: false;
		
		//Use widgetConfiguration.xml if title cannot be found in widget.xml
		if(widgetsXml.title)
			this.title = widgetsXml.title;
		else {
			if(!this.title) this.title = "";
			widgetsXml.title = this.title;
		}

		if(typeConf.ModulePrefs && typeConf.ModulePrefs.height)
		  typeConf.height = typeConf.ModulePrefs.height;
		  
		this.hasMaximizeView = ( typeConf.Maximize != undefined);
		
		if(widgetsXml.href)
			this.title_url = widgetsXml.href;
		this._build();
	}
	
	this.getParent = function(){
		IS_Portal.getWidget(this.widgetConf.parentId);
	}
	
	this.getDefaultUserPref = function(name){
		var typeConf = IS_Widget.getConfiguration( this.widgetType );
		if(typeConf && typeConf.UserPref && typeConf.UserPref[name]){
			return typeConf.UserPref[name].default_value;
		}
		return null;
	}
	
	this.getUserPref = function(name) {
		if(!userPref) return null;
		
		if( this.widgetConf.longProperty &&
			this.widgetConf.longProperty.contains( name ) && !userPref[name]) {
			$H( this.fetchUserPrefs( name )).each( function( entry ){
				userPref[entry.key] = entry.value;
				this.widgetConf.longProperty.remove( entry.key );
			},this );
		}
		
		return userPref[name];// This statement should be (userPref[name] || "")
	}
	
	this.getBoolUserPref = function(name){
		return getBooleanValue(this.getUserPref(name));
	}
	
	this.getJSONUserPref = function(name){
		var obj;
		try{
			obj = eval(this.getUserPref(name));
		}catch(e){
			obj = eval('(' + this.getUserPref(name) + ')');
		}
		return obj;
	}
	
	this.getUserPrefKeys = function(){
		var keys = [];
		for(var k in userPref){
			if(typeof userPref[k] == "function") continue;
			keys.push(k);
		}
		return keys;
	}
	
	this.initUserPref = function(name, value) {
		if(typeof value == "undefined") return false;
		
		var temppref = userPref[name];
		
		var tempvalue = value;
		if(typeof temppref == "boolean"){
			temppref = temppref.toString();
		}else if(typeof temppref == "object" && temppref){
			temppref = Object.toJSON(temppref);
		}
		if(typeof tempvalue == "boolean"){
			tempvalue = tempvalue.toString();
		}else if(typeof temppref == "object" && temppref){
			tempvalue = Object.toJSON(tempvalue);
		}
		
		if(temppref && temppref == tempvalue) return false;
		
		userPref[name] = value;
		return true;
	}
	
	this.setUserPref = function(name, value) {
//		console.log(["setUserPref", name, value]);
		var isModified = this.initUserPref(name, value);
		if(!isModified) return;
		
		if(typeof value == "object" && value){
			value = Object.toJSON( value );
		}
		this.widgetConf.cancelDefault(name);
		var cmd = new IS_Commands.UpdateWidgetPropertyCommand(this.tabId.substring(3), this, name, value);
		IS_Request.CommandQueue.addCommand(cmd);
	}
	
	this.setUserPrefs = function() {
		var typeConf = IS_Widget.getConfiguration(this.widgetType);
		
		var args = $A( arguments );
		while( args.length > 1 ) {
			var name = String( args.shift());
			var value = args.shift();
			if( !name ) continue;
			
			var pref = typeConf.UserPref[name];
			if( !pref || pref == "")
				continue;
			
			this.setUserPref( name, value );
		}
		
		if( this.headerContent )
			this.headerContent.updateTitle();
	}

	this.removeUserPref = function(name){
		//console.log(["removeUserPref", name]);
		delete userPref[name];
		var cmd = new IS_Commands.RemoveWidgetPropertyCommand(this.tabId.substring(3), this, name);
		IS_Request.CommandQueue.addCommand(cmd);
	}
	
	this.toggleBoolUserPref = function(name) {
		var value = this.getUserPref(name);
		if(typeof value == "string") {
			if (/true/.test(value) || /TRUE/.test(value)) {
				value = true;
			} else if(/false/.test(value) || /FALSE/.test(value)) {
				value = false;
			} else {
				throw new Error("");
			}
		} else if(typeof value != "boolean") {
			throw new Error("");
		}
		this.setUserPref(name, !value);
	}
	
	this.fetchUserPrefs = function( names ) {
		if( !( names instanceof Array ) )
			names = [names];
		
		var userPrefs = {};
		var opt = {
			method: "get",
			asynchronous: false,
			parameters: $H({
				tabId: this.tabId.substring(3),
				widgetId: this.id,
				up: names
			}).toQueryString(),
			onSuccess: function( response ) {
				userPrefs = eval("("+response.responseText+")");
			},
			onFailure: function( resp,obj ) {
				msg.error(IS_R.getResource(IS_R.ms_widgetonFailureAt, [self.widgetType, self.title,req.status,req.statusText]));
			},
			onException: function( resp,obj ) {
				msg.error(IS_R.getResource(IS_R.ms_widgetonExceptionAt, [self.widgetType, self.title,getText(obj)]));
			}
		}
		AjaxRequest.invoke("upsrv",opt );
		
		return userPrefs;
	}
	
	this.getContentObject = function(objName){
		var obj = null;
		try {
			if ( this.content) {
				obj = this.content[objName];
			} else if( this.iframe && this.iframe.contentWindow) {
				obj = this.iframe.contentWindow[objName];
			}
		} catch( ex ) {
			// ignore
		}
		
		return obj;
	}
	
	this.getContentFunction = function( funcName ) {
		var func = null;
		try {
			if ( this.content && this.content[funcName]) {
				func = this.content[funcName].bind( this.content );
			} else if( this.iframe && this.iframe.contentWindow && this.iframe.contentWindow[ funcName ]) {
//				func = this.iframe.contentWindow[funcName].bind( this.iframe.contentWindow );
				func = function(iframe, funcName){
					return function(){
						iframe.contentWindow[funcName]();
					}
				}(this.iframe, funcName);
			}
		} catch( ex ) {
			// ignore
		}
		
		return func;
	}
	
	this.hasContentFunction = function( funcName ) {
		return this.getContentFunction( funcName ) ? true : false;
	}
	
	this.isGadget = function() {
		return this.widgetType.indexOf("g_") == 0
			&& !(contentsDef && contentsDef.type == "javascript");
	}
	this.isUploadGadget = function() {
		return this.widgetType.indexOf("g_upload") == 0
			&& !(contentsDef && contentsDef.type == "javascript");
	}
	
	this._setStaticWidgetHeight = function(){
		var container = $("s_" + this.id);
		if(self.elm_widgetHeader.offsetHeight || (!self.headerContent && container.offsetHeight) ){
			var widgetHeight = parseInt(container.style.height);
			var headerHeight = ( typeConf.Header )? parseInt(self.elm_widgetHeader.offsetHeight) : 0;
			self.elm_widgetContent.style.height = widgetHeight - headerHeight + 'px';
			self.elm_widgetContent.style.overflow = "auto";//Autocomplete misalignment problem occurs if "overflow=hidden" is set in IE
			if(!Browser.isIE)self.elm_widgetContent.style.overflowX = "hidden";//Fix 484
			self.staticWidgetHeight = widgetHeight - headerHeight;
		}else{
			self.elm_widgetContent.style.height = "50px";
			setTimeout(this._setStaticWidgetHeight.bind(this), 10);
		}
	}
	
	this.getUrlByCredentialId = function(_credentialId){
		if(this.content && this.content.getUrlByCredentialId){
			return this.content.getUrlByCredentialId(_credentialId);
		}else{
			var authCredentialId = this.getUserPref('authCredentialId');
			var urlList = [];
			if(_credentialId == authCredentialId){
				urlList.push(this.getUserPref('url'));
			}
			return {urlList: urlList};
			
		}
	}
	
	this.isAuthenticationFailed = function(){
		var authType = this.getUserPref('authType');
		if(!authType || /^(postPortalCredential|sendPortalCredentialHeader).*$/.test(authType))
			return false;
		var authCredentialId = this.getUserPref('authCredentialId');
		return !authCredentialId;
	}
	
	var isStatic;
	this.isStaticHeight = false;
	var isMaximize;

	this.build = function() {
		this.isBuilt = true;
		isStatic = (this.panelType == "StaticPanel");
		isMaximize = (this.id.indexOf("__Maximize__") == 0 );
		
		var divWidget = document.createElement("div");
		this.elm_widget = divWidget;
		divWidget.className = "widget";
		divWidget.id = self.id;
		if(isStatic) divWidget.style.margin = 0;
		
		var divWidgetShade = document.createElement("div");
		this.elm_widgetShade = divWidgetShade;
		divWidget.appendChild(divWidgetShade);
		if(!isStatic)divWidgetShade.className = "widgetShade";
	   	
		var divWidgetBox = document.createElement("div");
		this.elm_widgetBox = divWidgetBox;
		//divWidgetBox.className = "widgetbox";
		divWidgetBox.className = "widgetBoxNoHeader";
	   
		//Header
		var divWidgetHeader = document.createElement("div");
		this.elm_widgetHeader = divWidgetHeader;
		divWidgetHeader.className = "widgetHeader";
		if (!self.draggable) {	   
			divWidgetHeader.style.cursor = "auto";
		}
		
		//Create edit header
		var divWidgetEditHeader = document.createElement("div");
		this.elm_widgetEditHeader = divWidgetEditHeader;
		divWidgetEditHeader.className = "widgetEditHeader";
		divWidgetEditHeader.style.display = "none";
	   
		//Edit Form [frm_<widgetId>]
		var editForm = document.createElement("form");
		this.elm_editForm = editForm;
		editForm.name = "frm_" + self.id;
		editForm.id = "frm_" + self.id;
		editForm.style.margin = "1px";
		IS_Event.observe(editForm, "submit", function(){
			return false;
		}, false, this.closeId);
		
		var divTitle = document.createElement("div");
		this.elm_title = divTitle;
	   	
	   	var divLatestMark = document.createElement("div");
	   	this.elm_latestMark = divLatestMark;
		divLatestMark.style.display = "none";

		var indicator = document.createElement("img");
		indicator.src = imageURL+"indicator.gif";
	   	this.elm_indicator = indicator;
		indicator.id = self.id + "_widgetIndicator";
		indicator.className = "widgetIndicator";
		indicator.style.display = "none";
		
	   	var divWidgetContent = document.createElement("div");
	   	this.elm_widgetContent = divWidgetContent;
	   	divWidgetContent.className = "widgetContent";
	   	divWidgetContent.innerHTML = "Loading...";
	   	
	   	this.elm_widgetContent.className += " "+( function( widget ) {
	   		if( !widget.isGadget())
	   			return widget.widgetType;
	   		
	   		return "";
	   	})( this.originalWidget ? this.originalWidget : this );
		
		divWidgetBox.appendChild(divWidgetContent);
	    divWidgetShade.appendChild(divWidgetBox);
		
		if(this.isBuildPending) this._build();
		IS_EventDispatcher.newEvent("buildWidget", this.id);
	};
	
	//Wait for loading script when 'js' is loaded from uploaded gadget
	this.waitLoadScript = function(){
		if(contentsType =="javascript" && this.widgetType != "notAvailable"){
			if(typeof IS_Widget[contentsDef.className] == "undefined"){
				if(typeof this.startLoadScriptTime == "undefined")
					this.startLoadScriptTime = new Date().getTime();
				else if(new Date().getTime() - this.startLoadScriptTime > 10000){
					typeConf = IS_Widget.getConfiguration("notAvailable");
					this.widgetType = "notAvailable";
					msg.error(IS_R.getResource(IS_R.ms_classNotFounf, [contentsDef.className]));
					return true;
				}
				setTimeout(this._build.bind(this), 500);
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	this._build = function(){
		if(this.waitLoadScript()){
			return;
		}
		if(!this.isBuilt) {
			this.isBuildPending = true;
			return;
		}
		var start = new Date();
		
		var divWidget = this.elm_widget;
		var divWidgetContent = this.elm_widgetContent;
		var divWidgetShade =  this.elm_widgetShade;
		var divWidgetBox =  this.elm_widgetBox;
		var divWidgetHeader = this.elm_widgetHeader;
		var divWidgetEditHeader = this.elm_widgetEditHeader;
		
		var icon = IS_Widget.getIcon(this.widgetType, {defaultNull:true});
		if(icon){
			var favoriteIconDiv = document.createElement("div");
			favoriteIconDiv.className = "gadget-icon-container";
			this.elm_favoriteIcon = favoriteIconDiv;
			var favoriteIconImg = document.createElement("img");
			favoriteIconImg.style.width = "16px";
			favoriteIconImg.style.height = "16px";
			favoriteIconImg.style.border = "0px";
			favoriteIconImg.src = icon;
			favoriteIconDiv.appendChild(favoriteIconImg);
		}
	
		if(!isStatic && !isMaximize && !this.getBoolUserPref("openWidget"))
			divWidgetContent.style.display = "none";
		
		divWidgetContent.style.width="100%";
		
		var widgetBackgroundColor = typeConf.backgroundColor;
		if(widgetBackgroundColor){
			try {
				divWidgetContent.style.backgroundColor = widgetBackgroundColor;
			} catch(e) {
				divWidgetContent.style.backgroundColor = "white";
				msg.error(IS_R.getResource(IS_R.ms_widgetBkgcolorError, [self.widgetType, self.title]));
			}
		}
		
		var container = $("s_" + this.id);
		self.isStaticHeight = !!(isStatic && container && container.style.height);
		
		//Instantiate implemented class only if contents type of widget is javascript
		if(contentsType =="javascript" && this.widgetType != "notAvailable"){
			var className = contentsDef.className;
			msg.debug(className);
			this.content = new IS_Widget[className] (self);
			//alert('className : '+className+'proxy:'+content.proxy);
		}
		
		var header = typeConf.Header;
		if(header && !getBooleanValue(this.widgetConf.ignoreHeader)){
		try{
			this.headerContent = new IS_Widget.WidgetHeader(self);
			
		}catch(e){console.error(e);}
			divWidgetBox.className = "widgetBox";
			divWidgetBox.insertBefore(divWidgetHeader, divWidgetContent);
		}
		
		var hasBorder = typeConf.border;
		if((hasBorder && /false/.test(hasBorder) || /FALSE/.test(hasBorder)) || getBooleanValue(this.widgetConf.noBorder)) {
			divWidgetBox.style.border = "0";
		}
		
		//Add edit header
		divWidgetBox.insertBefore(divWidgetEditHeader, divWidgetContent);
		
		var end = new Date();
		msg.debug(this.id + " build time: " + (end - start));
		
		var widgetHeight = typeConf.height;
		
		if(isStatic && self.isStaticHeight && (!this.content || !this.content.disableSetSaticWidgetHeight)){
			if(container){
				self._setStaticWidgetHeight();
			}
		}else if(widgetHeight && widgetHeight != "200"){
			if( parseInt( widgetHeight ) < 1 )
				widgetHeight = 1;
			
			divWidgetContent.style.height = setUnitOfLength(widgetHeight);
		}
		
		//Use 'resetAuthCredential' only if there is loginCredentialAuthType
		if(self.getUserPref("loginCredentialAuthType")) {
			var resetAuthCredential = function(){
				var authType = self.getUserPref("authType");
				var authCredential = self.getUserPref("authCredentialId");
				if(this.content && this.content.resetCredential){
					this.content.resetCredential();
				}else if(authType && !authCredential){
					this.loadContents();
				}
			}.bind(this);
			IS_EventDispatcher.addListener(
					'resetAuthCredential', 
					'resetAuthCredential',
					resetAuthCredential
					, true
				);
			this.addCloseListener(function(){
				IS_EventDispatcher.removeListener(
					'resetAuthCredential',
					'resetAuthCredential',
					resetAuthCredential);
			});
		}
		
		if(this.refreshInterval){
            this.addCloseListener(this.stopAutoRefresh.bind(this));
		}
		
		//'isLoadPending' stands if loadContents is called before building is completed
		if(this.isLoadPending){
			this.isLoadPending = false;
			this.loadContents();
		}
	}
	
	function replaceVariable(content){
		content = content.replace(/__WIDGET_ID__/g, self.id);
		content = content.replace(/__WIDGET__/g, "self");
//		content = content.replace(/__USERPREF__/g, "self.userPref");
		content = content.replace(/__WIDGETBODY__/g, "self.elm_widgetContent");
		return content;
	}
	this.getGadgetParameters = function( url,viewType ) {
		var moduleId = ( this.originalWidget ? this.originalWidget.id : this.id );
		var tabId = ( this.originalWidget ? this.originalWidget.tabId : this.tabId );
		var parameters = {
			mid: moduleId,
			__MODULE_ID__: moduleId,
			__TAB_ID__: tabId,
			__HOST_PREFIX__: hostPrefix,
			__STATIC_CONTENT_URL__: staticContentURL == '..' ? '.' : staticContentURL,//fix #3699
			view:  viewType == "Maximize" ? IS_Widget.DEFAULT_MAXIMIZE_VIEW : viewType || IS_Widget.DEFAULT_VIEW,
			lang: IS_Portal.lang,
			country: IS_Portal.country || "jp",
			ifpctok: this.authToken,
			url: this.getGadgetUrl(),
			parent: hostPrefix
		};
		
		if( this.view_params )
			parameters["view-params"] = $H( this.view_params ).toJSON();
		
		return parameters;
	}
	this.getGadgetUserPrefParameters = function( limit ) {
		var keys = this.getUserPrefKeys();
		var orderedKeys = [];
		if( typeConf.UserPref ) {
			for( var i in typeConf.UserPref ) if( typeConf.UserPref.hasOwnProperty( i )) {
				if( keys.member( i ))
					orderedKeys.push( i );
			}
		}
		
		orderedKeys = orderedKeys.concat( keys.findAll( function( key ) {
			return !orderedKeys.member( key );
		}));
		
		var upParams = "";
		for( var i=0;i<orderedKeys.length;i++ ) {
			var upKey = orderedKeys[i];
			var upValue = this.getUserPref( upKey );
			if( upValue === undefined )
				continue;
			
			upValue = encodeURIComponent( upValue );
			
			var urlparam;
			if( typeConf.UserPref && typeConf.UserPref[upKey] && typeConf.UserPref[upKey].urlparam ) {
				urlparam = typeConf.UserPref[upKey].urlparam;
			} else {
				urlparam = "up_"+upKey;
			}
			urlparam = encodeURIComponent( urlparam );
			
			if( upParams.length +( urlparam.length +upValue.length +2 ) < limit )
				upParams += "&"+urlparam +"=" +upValue;
		}
		
		return upParams;
	}
	this.getGadgetUrl = function() {
		return ( !/^g__Maximize__/.test( this.widgetType )?
				 this.widgetType.substring(2) : this.widgetType.substring( 13 ));
	}
	this.loadHtmlIfram = function( url, viewType ){
		var form = $("postGadgetSrvForm");
		if( !form ) {
			form = document.createElement("form");
			form.style.margin = 0;
			form.id = "postGadgetSrvForm";
			form.method = "POST";
			document.body.appendChild( form );
		}
		
		while( form.firstChild )
			form.removeChild( form.firstChild );
		
		var gadgetParams = $H( this.getGadgetParameters( url,viewType ));
		form.action = this.gadgetProxyUrl +"?"+gadgetParams.toQueryString();
		
		var url = this.getGadgetUrl();
		var params = {
			filter: "GadgetView",
			url: url,
			method: "get"
		};
		
		this.getUserPrefKeys().each( function( key ) {
			if(this.getUserPref( key ))
			  params["up_"+key] = this.getUserPref( key );
		},this );

		$H( params ).each( function( entry ) {
			var input = document.createElement("input");
			input.type = "hidden";
			input.setAttribute("name", entry.key );
			input.setAttribute("value",entry.value );
			
			form.appendChild( input );
		});
		
		form.target = "ifrm_"+this.id;
		form.submit();
		
		form.target = "";
		form.action = "";
		
		while( form.firstChild )
			form.removeChild( form.firstChild );
	};
	
	this.loadUrl = function() {
		var url = self.replaceUserPref(contentsDef.href);
		
		var moduleUrl = self.getGadgetUrl();
		if(!/^\w+:\/\//.test( url ) && /^(\w+:\/\/)/.test( moduleUrl )) {
			/^(\w+):\/\/(\w+(?:\.\w+)*)(\:\d+)?([^?]*)(\?.+)?$/.test( moduleUrl );
			var protocol = RegExp.$1;
			var host = RegExp.$2;
			var port = RegExp.$3;
			var path = RegExp.$4;
			var query = RegExp.$5;
			
			var baseUrl = protocol+"://"+host+port;
			if( url.charAt(0) != "/")
				baseUrl += path.substring( 0,path.lastIndexOf("/"));
			
			if( baseUrl.charAt( baseUrl.length -1 ) != "/")
				baseUrl += "/";
			
			url = baseUrl +url;
		}
		
		var parameters = self.getGadgetParameters( url,false );
		
		var libBase = hostPrefix+"/js/gadget/features/";
		var features = ["core","core-io","rpc"];
		if( typeConf.ModulePrefs && typeConf.ModulePrefs.Require ) {
			var requires = typeConf.ModulePrefs.Require;
			for( var i in requires ) if( requires.hasOwnProperty( i )) {
				features.push( i );
			}
		}
		features.push("infoscoop");
		parameters["libs"] = libBase +features.join(":")+".js";
		
		url += ( url.indexOf("?") > 0 ? "&" : "?" ) +$H( parameters ).toQueryString();
		url += self.getGadgetUserPrefParameters( 2080 -url.length );
		
		// omit the URL over the limits in IE by force
		if(url.length > 2080)
			url = url.substring(0, 2080);
		
		self.iframe.src = url;
	}
	
	this.replaceUserPref = function(s) {
		for(var i in userPref) {
			if(typeof userPref[i] == "function") continue;
			var prefValue = userPref[i] ? userPref[i] : "";
			s = s.replace("__UP_" + i + "__", prefValue);
		}
		return s;
	}
	
	this.gadgetProxyUrlRegexp = new RegExp("https?://\*.+");
	this.getGadgetProxyUrl = function(){
		if(this.gadgetProxyUrlRegexp.test( gadgetProxyURL )){
			return gadgetProxyURL.replace(/\*/, 's'+this.id.substr(2));
		}else
		  return gadgetProxyURL;
	}
	this.initIframe = function( isOuter ) {
		self.elm_widgetContent.innerHTML = "";
		
		self.iframe = document.createElement('iframe');
		self.iframe.id = "ifrm_" + self.id;
		self.iframe.name = "ifrm_" + self.id;

		if(this.hasFeature("pubsub-2") && self.id.indexOf("previewWidget_") < 0){
			// for IframeContainer callback
			if(!window["__gadgetOnLoad"])
				window["__gadgetOnLoad"] = function(gadgetUrl){};
			
			var oaIframeContainer = new OpenAjax.hub.IframeContainer(
				gadgets.pubsub2router.hub,
				"ifrm_" + self.id,
				{
					Container: {
						onSecurityAlert: function(source, alertType) {
							msg.error(['Security error for container ', source.getClientID(), ' : ', alertType].join(''));
							source.getIframe().src = 'about:blank';
						},
						onConnect: function(container) {
							msg.info(['connected: ', container.getClientID()].join('')); 
						}
					},
					IframeContainer: {
						parent: self.elm_widgetContent,
						uri: "about:blank",
						iframeAttrs: {},
						onGadgetLoad: "__gadgetOnLoad"	// If this property is not specified, an error occurred. (iframe onload)
					}
				}
			);
			self.iframe = oaIframeContainer.getIframe();
			IS_Event.observe(this.iframe, "load", function(){ IS_EventDispatcher.newEvent('loadComplete', this.id, null);}.bind(this), false, this.closeId);
			
			IS_EventDispatcher.addListener('closeWidget',self.id.substring(2),function(){
				gadgets.pubsub2router.hub.removeContainer(this);
			}.bind(oaIframeContainer), this, true);
		}
		
		self.iframe.frameBorder = 0;
		self.iframe.src = "about:blank";
		
		var scrolling = self.scrolling;
		if(/FragmentMiniBrowser/.test(self.widgetType ) )
			scrolling = true;
		
		self.iframe.scrolling = ( isOuter || !scrolling ?"no":"auto");
		
		if(isStatic && self.isStaticHeight) {
			if( !isOuter )
				self.iframe.scrolling = "auto";
			
			this.setStaticIframeHeight();
		} else if( typeConf.height ) {
			var iframeHeight = parseInt(typeConf.height) + 10;
			if( iframeHeight < 1 )
				iframeHeight = 1;
			self.iframe.style.height = setUnitOfLength(iframeHeight);
			self.elm_widgetContent.style.height = "auto";
		}
		
		self.elm_widgetContent.appendChild(self.iframe);
		
		if( this.isGadget() ) {
			if( this.originalWidget ) {
				this.authToken = this.originalWidget.authToken;
			} else {
				this.authToken = Math.ceil( Math.random() * new Date().getTime() );
			}
			this.gadgetProxyUrl = this.isUploadGadget() ? proxyServerURL : this.getGadgetProxyUrl();
			if(contentsType == 'url') {
				var relayUrl = contentsDef.href.substring( 0,contentsDef.href.lastIndexOf("/") +1 );
				gadgets.rpc.setupReceiver(self.iframe.id,relayUrl+"rpc_relay.html",this.authToken);
			}
			else if(!this.isUploadGadget()) {
				var relayUrl = this.gadgetProxyUrl.substring( 0,this.gadgetProxyUrl.lastIndexOf("/") +1 );
				gadgets.rpc.setupReceiver(self.iframe.id,relayUrl+"rpc_relay.html",this.authToken);
			} else {
				gadgets.rpc.setupReceiver(self.iframe.id,hostPrefix+"/rpc_relay.html",this.authToken);
			}
		}
		
		IS_Event.observe(self.iframe, "load", function(){ IS_EventDispatcher.newEvent('loadComplete', this.id, null);}.bind(self), false, self.closeId);
		//self.iframeDoc = self.iframe.contentDocument ? self.iframe.contentDocument : self.iframe.Document;
	}
	
	this.hasFeature = function(feature){
		var params = ["Require", "Optional"];
		
		for(var i=0;i<params.length;i++){
			var param = typeConf.ModulePrefs ? typeConf.ModulePrefs[params[i]] : typeConf[params[i]];;
			if(param && (param[feature] || param.feature == feature))
				return true;
		}
		return false;
	}
	
	this.setStaticIframeHeight = function() {
		if( !this.staticWidgetHeight){
			self.iframe.style.height = 0 + "px";
			
			return setTimeout( this.setStaticIframeHeight.bind( this ),100 );
		}
		
		self.iframe.style.height = this.staticWidgetHeight + "px";
		self.elm_widgetContent.style.height = "auto";
	}

	this.loadContents = function () {
		if(!self.content && contentsType == "javascript" ){
			this.isLoadPending = true;
			return;
		}
		
		if(self.isLoading) {

			msg.debug(IS_R.getResource(IS_R.ms_loadCancel, [self.id]));
			return;
		}
		
		if( !isReadyContents() ) {
			this.elm_widgetContent.innerHTML = IS_R.lb_setupUnfinished;
			this.elm_widgetContent.style.fontSize = "14px";
			if( !this.getBoolUserPref("openWidget") && this.headerContent )
				this.headerContent.turnBack();
			
			this.elm_widgetContent.style.display = "block";
			if( this.isGadget() && !self.isStaticHeight)
				this.elm_widgetContent.style.height = "auto";
			
			IS_EventDispatcher.newEvent('loadComplete', self.id, null);
			
			return;
		}
		
		self.isLoading = true;
		if(self.headerContent) IS_Widget.disableIcon({element:self.headerContent.elm_refresh});
		IS_EventDispatcher.addListener('loadComplete', self.id, self.postLoaded.bind(self), self, true);
		try{
			if(contentsType =="javascript" && self.content.loadContentsOption){
				self.processLoadContentsOption(self.content.loadContentsOption);
			} else {
				self.preLoad();
				if(contentsType == "url"){
					if(!self.iframe)
						self.initIframe( !self.isGadget() );
					
					self.loadUrl();
				}else if( contentsType =="html"){
					self.elm_widgetContent.innerHTML = contentsDef.content;
					IS_EventDispatcher.newEvent('loadComplete', self.id, null);
				}else if(contentsType == "htmlWithScript"){
					self.elm_widgetContent.innerHTML = replaceVariable(contentsDef.html.content);
					eval(replaceVariable(contentsDef.script.content));
					IS_EventDispatcher.newEvent('loadComplete', self.id, null);
				}else if(contentsType == "htmlIframe") {
					if(!self.iframe)
						self.initIframe();
					
					self.loadHtmlIfram();
				}else{

					self.elm_widgetContent.innerHTML = IS_R.ms_invalidWidget;
					IS_EventDispatcher.newEvent('loadComplete', self.id, null);
				}
				self.isSuccess = true;
				self.isComplete = true;
			}
		}catch(e){
			IS_EventDispatcher.newEvent('loadComplete', self.id, null);

			msg.error(IS_R.getResource(IS_R.ms_widgetonExceptionAt, [self.widgetType, self.title,getText(e)]));
			console.log(e);

			self.elm_widgetContent.innerHTML = IS_R.ms_invalidWidget;
			//throw e;
		}
	}

	this.autoReloadContents = function () {
		if(self.isLoading) {

			msg.debug(IS_R.getResource(IS_R.ms_autoReloadCancel, [self.id]));
			return;
		}
		if( !isReadyContents() ) {
//			this.elm_widgetContent.innerHTML = 

			this.elm_widgetContent.innerHTML = IS_R.lb_setupUnfinished;
			this.elm_widgetContent.style.fontSize = "14px";
			this.elm_widgetContent.style.display = "block";
			if( this.isGadget() && !self.isStaticHeight)
				this.elm_widgetContent.style.height = "auto";
			
			return;
		}
		
		self.isLoading = true;
		if(self.headerContent) IS_Widget.disableIcon({element:self.headerContent.elm_refresh});
		console.log("autoReload : " + self.id + " on " + new Date());
		IS_EventDispatcher.addListener('loadComplete', self.id, self.postLoaded.bind(self), self, true);
		try{
			if(contentsType =="javascript"){
				var opt = self.content.autoReloadContentsOption ? self.content.autoReloadContentsOption : self.content.loadContentsOption;
				self.processLoadContentsOption(opt);
			}else {
				self.preLoad();
				if(contentsType == "url"){
//					self.elm_widgetContent.innerHTML = '<iframe src="'  + contentsDef.href + '" style="font-size:70%;border:none;width:100%;height:200px;"/>';
					self.loadUrl();
				}else if(contentsType =="html"){
					self.elm_widgetContent.innerHTML = contentsDef.content;
					IS_EventDispatcher.newEvent('loadComplete', self.id, null);
				}else if(contentsType == "htmlWithScript"){
					self.elm_widgetContent.innerHTML = replaceVariable(contentsDef.html.content);
					eval(replaceVariable(contentsDef.script.content));
					IS_EventDispatcher.newEvent('loadComplete', self.id, null);
				}else if(contentsType == "htmlIframe") {
					self.loadHtmlIfram();
				}else{

					self.elm_widgetContent.innerHTML = IS_R.ms_invalidWidget;
					IS_EventDispatcher.newEvent('loadComplete', self.id, null);
				}
				//self.postLoaded();
			}
		}catch(e){
			IS_EventDispatcher.newEvent('loadComplete', self.id, null);
			//self.postLoaded();

			msg.error(IS_R.getResource(IS_R.ms_widgetonExceptionAt, [self.widgetType, self.title,getText(e)]));

//			self.elm_widgetContent.innerHTML = IS_R.getResource(IS_R.ms_widgetonExceptionAt_invalid, [self.widgetType, self.title, e]);
			self.elm_widgetContent.innerHTML = IS_R.ms_invalidWidget;
			
			throw e;
		}
	}
	
	var isFirstAuth = true;
	
	this.processLoadContentsOption = function (contentOpt) {
		if(contentOpt.request) {
			if(contentOpt.preLoad && !contentOpt.preLoad()) {
				IS_EventDispatcher.newEvent('loadComplete', self.id, null);
				self.postLoaded();
				return;
			}
			function showAuthenticationForm(authType,_isFirstAuth){
				var _overflow;
				var authForm = IS_Request.createAuthForm(
					self.id,
					function(){
						isFirstAuth = false;
						var authUid = $(self.id + "_authUid").value;
						var authPassword = $(self.id + "_authPasswd").value;
						if(authPassword){
							authPassword = rsaPK.encrypt(authPassword);
						}
						if(self.id.indexOf("adminPreviewWidget") == 0 ){
							self.removeUserPref("authType");
							self.setUserPref("previewAuthType", authType);
							self.setUserPref("previewAuthUserId", authUid);
							self.setUserPref("previewAuthPasswd", authPassword);
							self.loadContents();
							return;
						}
						var authUrl = self.getUserPref('url');
						var opt = {
						  method: 'post',
						  asynchronous: true,
						  parameters: {
						  	command:	"add",
						  	authType:	authType,
						  	authUid:	authUid,
						  	authPasswd:	authPassword,
						  	authDomain:	"",
						  	url:		authUrl
						  },
						  onSuccess:function(req, obj){
							  var credentialId = req.responseText;
							  if(new RegExp("[0-9]+").test(credentialId)){
								  self.setUserPref('authCredentialId', req.responseText);
								  if(self.getUserPref('loginCredentialAuthType'))
								  	IS_EventDispatcher.newEvent("resetAuthCredential", "resetAuthCredential");
							  }
							  if(self.parent && self.parent.content && self.parent.content.isTimeDisplayMode
							  	&& self.parent.content.isTimeDisplayMode()){
								  self.parent.loadContents();
							  }else{
								  self.loadContents();
							  }
						  }.bind(self),
						  onException:function(req, obj){
							  console.log(obj);
						  },
						  onFailure:function(req, obj){
								if(!self.isSuccess) {
									self.elm_widgetContent.innerHTML = "<span style='font-size:90%;padding:5px;'>"+ IS_R.ms_getdatafailed + "</span>";
								}
								msg.error(IS_R.getResource(IS_R.ms_widgetonFailureAt, [self.widgetType, self.title,req.status,req.statusText]));
						  }
						}
						AjaxRequest.invoke(hostPrefix + "/credsrv", opt, self.id);
						self.elm_widgetContent.style.overflow = _overflow;
						self.elm_widgetContent.innerHTML = "Loading...";
					}.bind(self),
					  _isFirstAuth);
				self.elm_widgetContent.replaceChild(authForm, self.elm_widgetContent.firstChild);
				
				_overflow = self.elm_widgetContent.style.overflow;
				self.elm_widgetContent.style.overflow = 'auto';
			}
			
			function applyAuthCredential(_authType){
				var upAuthType = self.getUserPref("authType");
				if(!upAuthType)upAuthType = _authType;
				var authType = upAuthType.split(' ')[0];
				if(self.id.indexOf("adminPreviewWidget") == 0 ){
					showAuthenticationForm((authType) ? authType : _authType);
				}else{
					self.setUserPref('authType', _authType);
					if(self.getUserPref('authCredentialId')){
						var oldAuthCredentialId = self.getUserPref('authCredentialId');
						self.removeUserPref('authCredentialId');
						IS_Request.removeCredential(oldAuthCredentialId);
					}
					var authUrl = self.getUserPref('url');
					var opt = {
					  method: 'post',
					  asynchronous: true,
					  parameters: {
					  	command:	"try",
					  	url:		authUrl
					  },
					  onSuccess:function(req, obj){
						var credentialId = req.responseText;
						if(new RegExp("[0-9]+").test(credentialId)){
							self.setUserPref('authCredentialId', credentialId);
							if(self.parent && self.parent.content.isTimeDisplayMode()){
								self.parent.loadContents();
							}else{
								self.loadContents();
							}
						}else{
							showAuthenticationForm((authType) ? authType : _authType, isFirstAuth);
						}
					  }.bind(self),
					  onException:function(req, obj){
						console.log(obj);
					  }
					}
					if(authType) opt.parameters.authType = authType;
					
					AjaxRequest.invoke(hostPrefix + "/credsrv", opt, self.id);
				}
			}
			
			var opt = {
			    method: contentOpt.method ? contentOpt.method : 'get' ,
			    asynchronous: (typeof contentOpt.asynchronous == "boolean") ? contentOpt.asynchronous : true,
			    key : contentOpt.key ? contentOpt.key : self.id,
			    ifModified : true,
			    clearCache : (self.clearCache) ?  self.clearCache : !self.isComplete,
			    timeout : contentOpt.timeout || ajaxRequestTimeout,
			    retryCount : contentOpt.retryCount || ajaxRequestRetryCount,
			    onSuccess: function(req, obj) {
					var _authType = req.getResponseHeader("MSDPortal-AuthType");
					if(_authType){
						self.iframe = false;
						self.clearCache = true;
						applyAuthCredential(_authType);
						self.isError = true;
						return;
					}
			    	
			    	
					if(contentOpt.unloadCache != false) {
						IS_Event.unloadCache(self.id);
					}
			    	contentOpt.onSuccess(req, obj);
			    	self.isSuccess = true;
			    	self.isError = false;
			    },
			    on304: function(req, obj) {
				    if(contentOpt.on304) contentOpt.on304(req, obj);
					//do nothing
			    	self.isSuccess = true;
			    	self.isError = false;
			    },
				on1223: function(req, obj) {
					if(contentOpt.unloadCache != false) {
						IS_Event.unloadCache(self.id);
					}
			    	contentOpt.onSuccess(req, obj);
			    	self.isSuccess = true;
			    	self.isError = false;
			    },
			    on404: function(req, obj) {
					self.isError = true;
			    	if(contentOpt.on404) contentOpt.on404(req, obj);
					if(!self.isSuccess) {

						self.elm_widgetContent.innerHTML = "<span style='font-size:90%;padding:5px;'>" + IS_R.lb_notfound +"</span>";
					}

					msg.error(IS_R.getResource(IS_R.ms_infoNotFoundAt, [self.widgetType, self.title]));
			    },
				on403: function(req, obj){
					self.isError = true;
					var authType = self.getUserPref("authType");
					if(authType){
						applyAuthCredential(authType);
					}else if(self.getUserPref('authCredentialId')){
						var oldAuthCredentialId = self.getUserPref('authCredentialId');
						self.removeUserPref('authCredentialId');
						IS_Request.removeCredential(oldAuthCredentialId);
					}
					
					if(!self.isSuccess) {

						self.elm_widgetContent.innerHTML = "<span style='font-size:90%;padding:5px;'>" + IS_R.ms_noPermission + "</span>";
						//if(self.getUserPref("authType")){
						//	showAuthenticationForm();
						//}
					}
			    	if(contentOpt.on403) contentOpt.on403(req, obj);

					msg.error(IS_R.getResource(IS_R.ms_noPermissionAt, [self.widgetType, self.title]));
				},
				on10408: function(req,obj) {
					self.isError = true;
					
					if( contentOpt.on10408 ) {
						 contentOpt.on10408( req,obj );
						 

						msg.error(IS_R.getResource(IS_R.ms_loadtimeoutAt, [self.widgetType, self.title]));
					} else {
						this.onException( req,

							IS_R.ms_loadTimeout );
					}
				},
			    onFailure: function(req, obj) {
					self.isError = true;
			    	if(contentOpt.onFailure) contentOpt.onFailure(req, obj);
			    	if(!self.isSuccess) {

						self.elm_widgetContent.innerHTML = "<span style='font-size:90%;padding:5px;'>"+ IS_R.ms_getdatafailed + "</span>";
					}

					msg.error(IS_R.getResource(IS_R.ms_widgetonFailureAt, [self.widgetType, self.title,req.status,req.statusText]));
			    },
				onException: function(req, obj){
					self.isError = true;
			    	if(contentOpt.onException) contentOpt.onException(req, obj);
			    	if(!self.isSuccess) {

						self.elm_widgetContent.innerHTML = "<span style='font-size:90%;padding:5px;'>"+ IS_R.ms_getdatafailed + "</span>";
					}

					msg.error(IS_R.getResource(IS_R.ms_widgetonExceptionAt, [self.widgetType, self.title,getText(obj)]));
				},
				onComplete: function(req, obj){
					try{
						if(contentOpt.onComplete) contentOpt.onComplete(req, obj);
					}catch(e){

						msg.error(IS_R.getResource(IS_R.ms_widgetonExceptionAt, [self.widgetType, self.title,getText(e)]));
					}
					IS_EventDispatcher.newEvent('loadComplete', self.id, null);
					self.isComplete = true;
				},
				onRequest: function() {
					if(self.isComplete)	self.preLoad();
				}
			};
			for(var i in contentOpt) {
				if(typeof contentOpt[i] != "function" && !opt[i]) {
					if(i == 'headers')continue;//Setting 'contentOpt.requestHeaders' makes system unworkable
					opt[i] = contentOpt[i];
				}
			}
			
			if(contentOpt.headers){
				if(!opt.requestHeaders)opt.requestHeaders = new Array();
				for(var i = 0; i < contentOpt.headers.length; i++){
					opt.requestHeaders.push(contentOpt.headers[i]);
				}
			}
			
			var authCredentialId = self.getUserPref("authCredentialId");
			if(authCredentialId){
				if(!opt.requestHeaders)opt.requestHeaders = new Array();
				opt.requestHeaders.push("authCredentialId");
				opt.requestHeaders.push(authCredentialId);
				
			}

			var authType;
			var authParameNames;
			var _authType = self.getUserPref("authType");
			if(_authType){
				authType = _authType.split(' ')[0];
				authParameNames = _authType.split(' ')[1];
			}
			//No need for the branch below because log is sent only in 'onSuccess'
			if( authType && (authType == "basic" || authType == "ntlm" || authType.indexOf("postCredential") == 0) && (!authCredentialId || "" == authCredentialId) ){
				applyAuthCredential(_authType);
				self.isError = true;
				IS_EventDispatcher.newEvent('loadComplete', self.id, null);
				return;
			}

			//Move authentication information in preview
			var preveiwAuthType = self.getUserPref("previewAuthType");
			var previewAuthCredentialId = self.getUserPref("previewAuthCredentialId");
			if(preveiwAuthType){
				if(!opt.requestHeaders)opt.requestHeaders = new Array();
				var authUserId = self.getUserPref("previewAuthUserId");
				var authPassword = self.getUserPref("previewAuthPasswd");
				opt.requestHeaders.push("authType");
				opt.requestHeaders.push(preveiwAuthType);
				opt.requestHeaders.push("authUserid");
				opt.requestHeaders.push(authUserId);
				opt.requestHeaders.push("authPassword");
				opt.requestHeaders.push(authPassword);
				if(preveiwAuthType.indexOf("post") == 0)
					opt.method = 'post';
			}else if(previewAuthCredentialId){
				if(!opt.requestHeaders)opt.requestHeaders = new Array();
				opt.requestHeaders.push("authCredentialId");
				opt.requestHeaders.push(previewAuthCredentialId);
			}
			
			if(authType && authType.indexOf("post") == 0)
				opt.method = 'post';
			if(authType && (authType.indexOf("PortalCredential") >= 0 || authType.indexOf("sign") >= 0)){
				if(!opt.requestHeaders)opt.requestHeaders = new Array();
				opt.requestHeaders.push("authType");
				opt.requestHeaders.push(authType);
			}

			if(authParameNames){
				var uidParamName = authParameNames.split(':')[0];
				var passwdParamName = authParameNames.split(':')[1];
				if(uidParamName){
					opt.requestHeaders.push("_authUidParamName");
					opt.requestHeaders.push(decodeURIComponent(uidParamName));
				}
				if(passwdParamName){
					opt.requestHeaders.push("_authPasswdParamName");
					opt.requestHeaders.push(decodeURIComponent(passwdParamName));
				}
			}
			
			if(!self.isComplete) self.preLoad();
			AjaxRequest.invoke(opt.url, opt, self.id);
		} else {
			try {
				self.preLoad();
				if(contentOpt.unloadCache != false) {
					IS_Event.unloadCache(self.id);
				}
				contentOpt.onSuccess();
				self.isSuccess = true;
		    	self.isError = false;
			} catch(e) {
		    	if(contentOpt.onException) contentOpt.onException(null, e);
				IS_EventDispatcher.newEvent('loadComplete', self.id, null);
		    	if(!self.isSuccess) {

					self.elm_widgetContent.innerHTML = "<span style='font-size:90%;padding:5px;'>" + IS_R.ms_getdatafailed + "</span>";
				}

				msg.error(IS_R.getResource(IS_R.ms_widgetonExceptionAt, [self.widgetType, self.title,getText(e)]));
				self.isError = true;
			}
			self.isComplete = true;
		}
	};
	
	function isReadyContents() {
		// Ready at all the time if gadget is not chosen
		//if( !( self.widgetType.indexOf("g_") == 0 ) )
		if(!self.isGadget())
			return true;
		
		for( var key in typeConf.UserPref ) {
			var pref = typeConf.UserPref[key];
			if( pref instanceof Function ||
				!pref.required || /false/i.test( pref.required ))
				continue;
			
			var value = self.getUserPref( pref.name );
			if( value == undefined || value == "" ||(
				( pref.inputType == "list" || pref.inputType == "gadgetlist")&&
					value == "[]") ) {
				return false;
			}
		}
		
		return true;
	}
	this.clearContents = function () {
		this.elm_widgetContent.innerHTML ="";
	}	
	
	this.startIndicator = function () {
		this.elm_indicator.style.display = "";
		if (this.elm_favoriteIcon) 
			this.elm_favoriteIcon.style.display = "none";
	}
	this.stopIndicator = function () {
		this.elm_indicator.style.display = "none";
		if (this.elm_favoriteIcon) 
			this.elm_favoriteIcon.style.display = "";
	}	
	
	var onBlink = false;
	this.blinkEffect = null;
	var oldStyle;
	this.blink = function () {
		var headerEl = this.elm_widgetHeader;
		if(headerEl && !onBlink){
			onBlink = true;
			
			Element.addClassName( headerEl,"blink");
			
			setTimeout(this.blinkEffectFinished.bind(this), 6000);
		}
	}
	this.blinkEffectFinished = function (){
		Element.removeClassName( this.elm_widgetHeader,"blink");
		
		onBlink = false;
	}

	this.preLoad = function() {
		self.startIndicator();
		
		self.enableEvent = false;
		for (var i = 0; i<self.eventTargetList.length; i++) {
			if (self.eventTargetList[i]) {
				IS_Widget.disableIcon(self.eventTargetList[i], self);
			}
		}
		
		if(/^g_/.test( self.widgetType )) {
			self.addLoadCompleteListener( function() {
				if( self.iframe && self.iframe.offsetHeight <= 1 )
					self.onTabChangeAdjustIFrameHeight = true;
			},true );
		}
	}
	
	this.postLoaded = function() {
		self.isLoading = false;
		
		self.stopIndicator();
		
		self.enableEvent = true;
		for (var i = 0; i<self.eventTargetList.length; i++) {
			if (self.eventTargetList[i]) {
				IS_Widget.enableIcon(self.eventTargetList[i], self);
			}
		}
		
		self.startAutoRefresh();
	}
	
	this.setLiteModePreference = function(){
		this.initUserPref("openWidget", true);
	}
	
	this.changeMaximize = function( baseWidget ) {
		var authFailed = this.isAuthenticationFailed();
		if( authFailed ){

			alert(IS_R.ms_needsAuthentication);
			return;
		}
		if( Browser.isSafari1 && IS_Portal.isTabLoading() )return;
		
		if( this.content && this.content.getRssReaders ) {
			var rssReaders = this.content.getRssReaders();
			var allCategoriesError = rssReaders.findAll( function( rssReader ) {
					return ( !rssReader.isSuccess && rssReader.isError ) || rssReader.isAuthenticationFailed()
	//					||( rssReader.content && rssReader.content.rssContent.rssItems.length == 0 );
				}).length == rssReaders.length;
			var allMergeError = Try.these(
				function() { // Retun true, if 'url' of all category is error
					var errorUrls = $H( this.content.mergeRssReader.content.rss.errors ).keys();
					
					return !rssReaders.find( function( rssReader ) {
						return !errorUrls.contains( rssReader.getUserPref("url") );
					})
				},
				function() { return false }
			);
			if( allCategoriesError || allMergeError ) {

				alert( IS_R.lb_noInfoOnMaximize );
				return;
			}
		} else if(this.disableMaximizeView || (!this.isSuccess && this.isError) ) {

			alert( IS_R.lb_noInfoOnMaximize );
			return;
		}
		
		if(!this.hasMaximizeView){
			if( !this.iframe ) {

				alert( IS_R.lb_noInfoOnMaximize );
				return;
			}
			
			this.changeDefaultMaximize();
			
			if( this.headerContent )
				this.headerContent.switchToMaximizeHeader();
			
			if( this.content && this.content.maximize )
				this.content.maximize();
			
			IS_Widget.MaximizeWidget = this;
		}else if( this.parent ) {
			this.parent.changeMaximize( this );
		} else {
			if( !this.maximize ||
				//( widget.parent && widget.maximize.originalWidget != widget.parent )||
				( !this.parent && this.maximize.originalWidget != this ))
				this.maximize = IS_Widget.Maximize.createMaximizeWidget( this );
			
			this.maximize.changeMaximize(baseWidget);
		}
	}

	this.adjustMaximizeHeight = function()  {
		var height = getWindowSize(false) - findPosY(this.elm_widgetBox) - (Browser.isFirefox ? 28 : 32);
		if (height < 0) {
			height = 0;
		}
		this.elm_widgetContent.style.height = this.iframe.style.height = height + "px";
	}
	
	this.adjustMaximize = function(){
		Position.prepare();
		var panelsDiv = $('panels');
		
		//Fixed Issue 149: Fragment Minibrowser shows a little off from the position whrere it should be when it maximized.
		if(fixedPortalHeader && Browser.isIE)
			IS_Portal.tabs[IS_Portal.currentTabId].panel.style.height = "auto";
		
		var pos = Position.cumulativeOffset(panelsDiv);
		this.elm_widget.style.top = pos[1] + "px";
		this.elm_widget.style.left = pos[0] + "px";
		this.elm_widget.style.width = parseInt(panelsDiv.offsetWidth) + "px";
		
		var tabPanel = $('panel' + this.tabId.substr(3));
		var widgetDivList = tabPanel.getElementsByClassName('widgetBox');
		for(i = 0; i < widgetDivList.length; i++){
			if(this.elm_widgetBox !== widgetDivList[i])
			  widgetDivList[i].style.height = '10px';
		}
		this.adjustMaximizeHeight();
	}
	this._adjustMaximize = this.adjustMaximize.bind( this );
	
	this.changeDefaultMaximize = function(){
		if( !this.getBoolUserPref("openWidget")) {
			this.turnbackWithMinimize = true;
			if( this.headerContent )
				this.headerContent.turnBack();
		}
		
		if( this.headerContent && this.headerContent.widgetEdit )
			this.headerContent.widgetEdit.cancel();
		
		scrollTo(0, 0);
		
		this.tempIFrameHeight = this.iframe.style.height;
		this.elm_widget.style.position = "absolute";
		this.elm_widget.style.zIndex = 1000;
		//widget.elm_widgetContent.style.width=document.body.offsetWidth;
		

		if( Browser.isFirefox ) {
			var widgetList = IS_Portal.widgetLists[this.tabId];
			for( var i in widgetList ) if( widgetList.hasOwnProperty( i )) { 
				var w = widgetList[i];
				if( w == this || !w.iframe ) continue;
				
				w.iframe.style.visibility = "hidden";
			}
		}
		
		setTimeout( this._adjustMaximize, 100);
		
		IS_Event.observe( window, 'resize', this._adjustMaximize, false,this.closeId );
		IS_EventDispatcher.addListener("adjustedSiteMap","",this._adjustMaximize );
		IS_EventDispatcher.addListener("adjustedMessageBar","",this._adjustMaximize );
	}
	
	this.turnbackMaximize = function() {
		if( this.maximize && this.maximize.turnbackMaximize ) {
			this.maximize.turnbackMaximize();
		} else if(!this.hasMaximizeView){
			this.defaultTurnbackMaximize();
		}
	}
	
	this.defaultTurnbackMaximize = function(){
		var tabPanel = $('panel' + this.tabId.substr(3));
		var widgetDivList = tabPanel.getElementsByClassName('widgetBox');
		for(i = 0; i < widgetDivList.length; i++){
			if(this.elm_widgetBox !== widgetDivList[i])
			  widgetDivList[i].style.height = '';
		}
		
		this.elm_widget.style.position = "";
		this.elm_widget.style.zIndex="";
		this.elm_widget.style.top="";
		this.elm_widget.style.left="";
		this.elm_widget.style.width="";
		this.elm_widgetContent.style.height = '';
		this.iframe.style.height=this.tempIFrameHeight;
		
		if( Browser.isFirefox ) {
			var widgetList = IS_Portal.widgetLists[this.tabId];
			for( var i in widgetList ) if( widgetList.hasOwnProperty( i )) { 
				var w = widgetList[i];
				if( w == this || !w.iframe ) continue;
				
				w.iframe.style.visibility = "visible";
			}
		}
		
		if( this.headerContent )
			this.headerContent.switchFromMaximizeHeader();
		
		if( this.content && this.content.turnbackMaximize )
			this.content.turnbackMaximize();
		
		if( this.turnbackWithMinimize ) {
			this.turnbackWithMinimize = false;
			
			if( this.headerContent )
				this.headerContent.minimize();
		}
		
		IS_Widget.MaximizeWidget = undefined;
		
		IS_Event.stopObserving( window, 'resize', this._adjustMaximize, false,this.closeId )
		IS_EventDispatcher.removeListener("adjustedSiteMap","",this._adjustMaximize );
		IS_EventDispatcher.removeListener("adjustedMessageBar","",this._adjustMaximize );
	}
	
	this.refresh = function() {
		
		if( !this.content || !this.content.refresh ) {
			this.loadContents();
		} else if(this.content && this.content.refresh) {
			this.content.refresh();
		}
	}
	
	this.startAutoRefresh = function(){
	    if(!this.refreshInterval)
	        return;
	    
	    this.stopAutoRefresh();
	    this.autoRefreshTimer = setTimeout(function(){
	        console.log("autoReload : " + this.id + " on " + new Date() + " interval : " + this.refreshInterval);
	        this.refresh();
	    }.bind(this), this.refreshInterval * 1000 * 60);
	}
	
	this.stopAutoRefresh = function(){
        if(this.autoRefreshTimer){
            clearTimeout(this.autoRefreshTimer);
        }
	}
}

IS_Widget.prototype.addCloseListener = function( listener ) {
	IS_EventDispatcher.addListener('closeWidget',this.id.substring(2),listener,this,true );
}
IS_Widget.prototype.removeCloseListener = function( listener, isTemp ) {
	IS_EventDispatcher.removeListener('closeWidget',this.id.substring(2),listener, isTemp);
}
IS_Widget.prototype.addLoadCompleteListener = function( listener,isTemp ) {
	IS_EventDispatcher.addListener('loadComplete',this.id,listener,this,isTemp );
}
IS_Widget.prototype.removeLoadCompleteListener = function( listener ) {
	IS_EventDispatcher.removeListener('loadComplete',this.id,listener );
}

IS_Widget.disableIcon = function(icon, widget) {
	if(!icon) return;
	if(icon.element){
		icon.element.disabled = true;
		icon.element.style.filter = "alpha(opacity=50)";
		icon.element.style.opacity = 0.5;
		//icon.element.MozOpacity = 0.5;
	}
	if(icon.type){
		var disableFunc = widget.getContentFunction(icon.type +"Disable");
		if(disableFunc) disableFunc();
	}
}

IS_Widget.enableIcon = function(icon, widget) {
	if(!icon) return;
	if(icon.element){
		icon.element.disabled = false;
		icon.element.style.filter = "1.0";
		icon.element.style.opacity = "1.0";
		//icon.element.MozOpacity = "1.0";
	}
	if(icon.type){
		var enableFunc = widget.getContentFunction(icon.type +"Enable");
		if(enableFunc) enableFunc();
	}
}

IS_Widget.mergePreference = function(typeConfigXml, widgetsXml) {
	if(!widgetsXml.property) widgetsXml.property = {};
	var property = widgetsXml.property;
	var defaultProperty = {};
	for(var i in typeConfigXml.UserPref) {
		var defaultPref = typeConfigXml.UserPref[i];
		if(property[i] == null || (typeof property[i] == "undefined")) {
			property[i] = defaultPref.default_value;
			defaultProperty[i] = true;
		}
		if(defaultPref.inputType == "checkbox") {
			property[i] = getBooleanValue(property[i]);
		}
	}
	widgetsXml.isDefault = function(name){
		return defaultProperty[name];
	}
	widgetsXml.cancelDefault = function(name){
		delete defaultProperty[name];
	}
	return property;
}

IS_Widget.contentClicked = function (url, rssUrl, title, pubDate, aTag) {
	IS_Portal.buildIFrame(aTag);
	IS_Widget.updateLog("0",url,rssUrl);
	IS_Widget.updateRssMeta("0",url,rssUrl,title,pubDate);
};

IS_Widget.mergeContentClicked = function (rssItem,aTag) {
	IS_Portal.buildIFrame(aTag);
	for(var i=0; i<rssItem.rssUrls.length ;i++){
		IS_Widget.updateLog("0",rssItem.link,rssItem.rssUrls[i]);
		
		var startDateTime = (rssItem.rssDate)? rssItem.rssDate.getTime() : "";
		IS_Widget.updateRssMeta("0",rssItem.link,rssItem.rssUrls[i],rssItem.title,startDateTime);
	}
	
};

IS_Widget.updateLog = function (logType,url,rssUrl){
	if( !url )
		return;
	
	var cmd = new IS_Commands.AddLogCommand(logType, url, rssUrl);
	IS_Request.LogCommandQueue.addCommand(cmd);
}

IS_Widget.updateRssMeta = function (contentType, url, rssUrl, title, pubDate){
	if(!url )
		return;
	
	var cmd = new IS_Commands.UpdateRssMetaCommand(contentType, url, rssUrl, title, pubDate);
	IS_Request.LogCommandQueue.addCommand(cmd);
}

IS_Widget.parseRss = function(response) {
	var jsonObj;
	var txt = response.responseText;
	try {
		if (txt=="") return null;
		jsonObj = eval("(" + txt + ")");
		if(jsonObj.statusCode != 0) {
			throw jsonObj.message;
		}
		
		if( jsonObj.items ) {
			$A( jsonObj.items ).each( function( item ) {
				if( item.dateLong && Number( item.dateLong ) != 0 ) {
					item.rssDate = new Date();
					item.rssDate.setTime( item.dateLong );
				}
			});
		}
	} catch (e) {
		throw e;
	}
	return jsonObj;
}

IS_Widget.getRssUrl = function(widget, url) {
	var url = url || widget.getUserPref("url");
	url = is_getProxyUrl(url, "RssReader");
	
	var headersArray = [];
	putValue("X-IS-DATETIMEFORMAT", encodeURIComponent(widget.widgetPref.dateTimeFormat.value), headersArray);
	
	var nowDate = new Date();
	var logoffDatetime = parseInt( IS_Portal.logoffDateTime );

	// If logoffDatetime has initial value, login initially or login as guest user; no need to calculate previous logout
	var freshTime = (logoffDatetime <= 0 || isNaN( logoffDatetime ))? 0 : nowDate.getTime() - logoffDatetime;
	var freshDaysTime = freshDays *24 *60 *60 *1000
	
	putValue("X-IS-FRESHTIME",nowDate.getTime() -Math.max( freshTime,freshDaysTime ),headersArray );
	
	if(typeof rssMaxCount != "undefined")
		putValue("X-IS-RSSMAXCOUNT", encodeURIComponent(rssMaxCount), headersArray);
	
	widget.content.loadContentsOption.headers = headersArray;
	if(widget.content.autoReloadContentsOption){
		widget.content.autoReloadContentsOption.headers = headersArray;
	}
	
	function putValue(name, value, array){
		if(value) array.push(name, value);
	}
	
	return url;
}

/**
 * Return widgetConfiguration of Widget
 */
IS_Widget.getConfiguration = function( widgetType ){
	var typeConf = IS_WidgetConfiguration[widgetType];
	if( widgetType &&( !typeConf || ( (widgetType.indexOf("g_") == 0 && !typeConf.Content ) ) ) ){
		var opt = {
			method: 'get',
//			parameters: 'type=' + encodeURIComponent(widgetType) + "&reload",
//			parameters: 'type=' + widgetType,
			parameters: 'type=' + encodeURIComponent(widgetType) + "&hostPrefix=" + encodeURIComponent(hostPrefix),
			asynchronous: false,
			timeout: ajaxRequestTimeout,
			onSuccess: function(response){
				typeConf = eval("(" + response.responseText + ")");
				if(!typeConf || !typeConf.ModulePrefs && !typeConf.type) typeConf = null;
				if(typeConf) {
					if(typeConf.ModulePrefs) {
						for( i in typeConf.ModulePrefs)
						  typeConf[i] = typeConf.ModulePrefs[i];
						typeConf.type = widgetType;
					}
					IS_WidgetConfiguration[widgetType] = typeConf;
				}
			},
			on404: function(t) {
				msg.error('Error 404: location "' + hostPrefix + "/widconf" + '" was not found.');
			},
			onFailure: function(t) {
				msg.error('Failed to get widget configuration file ' + widgetType + ".\n" + t.status + ' -- ' + t.statusText);
			},
			onException:function(r,t){
				msg.error(getErrorMessage(t));
			}
		}
		var url = hostPrefix + "/widconf";
		AjaxRequest.invoke(url, opt);
	}
	
	return typeConf;
}
/* delete by endoh on 20080725.
IS_Widget.getDisplayTypeConfiguration = function( flag ){
	//var widgetConf = IS_WidgetConfiguration[list];
	var widgetConf = null;
	//if(!typeList){
		var opt = {
			method: 'get',
			asynchronous: false,
			parameters: 'displayFlag=' + flag,
			onSuccess: getConfig,
			on404: function(t) {
				msg.error('Error 404: location "' + hostPrefix + "/widconf" + '" was not found.');
			},
			onFailure: function(t) {
				msg.error('Error ' + t.status + ' -- ' + t.statusText);
			},
			onException:function(r,t){
				msg.error(getErrorMessage(t));
				throw t;
			}
		}
		AjaxRequest.invoke(hostPrefix + "/widconf", opt);
		//widgetConf = IS_WidgetConfiguration[list];
	//}
	function getConfig(response) {
		widgetConf = eval("(" + response.responseText + ")");
		//IS_WidgetConfiguration[list] = widgetConf;
	}
	return widgetConf;
}
*/

/*This is not used
IS_Widget.createProperty = function(url){
//	var propertyConf = "<properties>"
//		+ "<property>" + url + "</property>"
//		+ "</properties>";
//	var property = dojo.dom.createDocumentFromText(propertyConf).documentElement;
	var property = new Object();
	property.url = url;
	return property;
}*/

IS_Widget.getWidgetRowIdx = function(widget, tabId){
	var columnObj = IS_Portal.columnsObjs[tabId]["col_dp_" + widget.widgetConf.column];
	for(var rowIdx=0; rowIdx < columnObj.childNodes.length; rowIdx++){
		if(columnObj.childNodes[rowIdx] == widget.elm_widget){
			break;
		}
	}
	return rowIdx;
}

IS_Widget.getSubWidgetRowIdx = function(subWidget){
	var rssReaders = subWidget.parent.content.getDisplayRssReaders();
	for(var rowIdx=0;rowIdx<rssReaders.length;rowIdx++){
		if(rssReaders[rowIdx].id == subWidget.id){
			break;
		}
	}
	return rowIdx;
}

IS_Widget.insertWidget = function(widget, tabId, colnum, rowIdx){
	if(IS_Portal.tabs[tabId].numCol < colnum) {
		widget.widgetConf.column = IS_Portal.tabs[tabId].numCol;
		IS_Widget.insertWidget(widget, tabId, IS_Portal.tabs[tabId].numCol, rowIdx)
		return;
	}
	var columnObj = IS_Portal.columnsObjs[tabId]["col_dp_" + colnum];
	
	if(columnObj.childNodes.length-1 < rowIdx){
		// Pattern added at last
		var end = $("columns"+tabId.substring(3)+"_end_" + colnum);
		
		if(end) {
			columnObj.insertBefore(widget.elm_widget, end);
		} else {
			columnObj.appendChild(widget.elm_widget);
		}
	}else{
		//dojo.dom.insertBefore(widget.elm_widget, columnObj.childNodes[rowIdx]);
		columnObj.insertBefore(widget.elm_widget, columnObj.childNodes[rowIdx]);
	}
	
	widget.tabId = tabId;
//	IS_Portal.widgetLists[tabId][widget.id] = widget;
	IS_Portal.addWidget(widget, tabId);
}

/**
 * Move to unbuilt tub; Use it only in WidgetMap
 * Reorganize WidgetList
 */
IS_Widget.insertAiryWidget = function(orgTabId, targetTabId, widget, siblingId){
	// Delete the self
	if(targetTabId != orgTabId){
		// Move to unbuilt tub from current tub
		if(widget.isBuilt){
			if(widget.elm_widget.parentNode)
				widget.elm_widget.parentNode.removeChild(widget.elm_widget);
			widget.isBuilt = false;
			widget.isBuildPending = true;//Flag this to keep 'old build' working till the last
			if(widget.iframe) widget.iframe = false;
			widget.isComplete = false;
			IS_Event.unloadCache(widget.id);
		}
		if(orgTabId)
//			IS_Portal.widgetLists[orgTabId][widget.id] = null;
			IS_Portal.removeWidget(widget.id, orgTabId);
	}
	
	var widgetList = IS_Portal.widgetLists[targetTabId];
	widgetList[IS_Portal.getTrueId(widget.id)] = null;
	
	var newWidgetList = new Array();
	var added = false;
	
	if(siblingId == ""){
		newWidgetList[IS_Portal.getTrueId(widget.id)] = widget;
		widget.tabId = targetTabId;
		added = true;
	}
	
	for(var i in widgetList){
		if(widgetList[i] && typeof widgetList[i] != "function"){
			newWidgetList[i] = widgetList[i];
		}
		
		if(widgetList[i] && typeof widgetList[i] != "function" && widgetList[i].id == siblingId && !added){
			newWidgetList[IS_Portal.getTrueId(widget.id)] = widget;
			widget.tabId = targetTabId;
			added = true;
		}
	}
	
	IS_Portal.widgetLists[targetTabId] = newWidgetList;
}

/**
 * Remove the widget with specified ID; Confirm before removing in confirm dialogue
 * 
 * @param menuId: Menu ID whose prefix such as 'p_' is removed
 * @param displayConfirm
 * 				true : Show 'confirm' to remove
 * 				false: Show 'confirm' to remove
 * @param message: Message that is shown in dialogue when displayConfirm is true
 * @param notAddTrash: if this is true, do not add to trash bin
 * @return true: Complete remove, or does not exsist the target to remove
 * 			false: Remove canceled
 */
IS_Widget.deleteWidget = function( menuId, displayConfirm, message , notAddTrash){
	// Delete widget if it is already dropped
	// Check whether marked items are shown in other tubs
	var menuItem = IS_TreeMenu.findMenuItem( menuId );
	var existWidget = false;
	
	tabLoop:
	for(var num=0; num < IS_Portal.tabList.length; num++){
		var tab = IS_Portal.tabList[num];
		
//		if(tab.id == IS_Portal.currentTabId) continue;
		
		var widgetList = IS_Portal.widgetLists[tab.id];
		for( var id in widgetList ){
			if( widgetList[id] && !(widgetList[id] instanceof Function) ){
				if(menuItem.id == widgetList[id].id.substring(2)){
					existTab = IS_Portal.tabs[tab.id];
					existWidget = widgetList[id];
					break tabLoop;
				}
			}
		}
		
		if(existWidget) break;
	}
	
	// This function is simplified because it is hardly used: rev 1.423
	if( !existWidget)
		return;
	
	if( existWidget.isBuilt && existWidget.headerContent ){
		existWidget.headerContent.close(false, notAddTrash);
	}else if (existWidget.parent) {
		var parentWidget = existWidget.parent;
		IS_Portal.removeSubWidget(parentWidget.id, existWidget.id, parentWidget.tabId);
		
		IS_Request.CommandQueue.addCommand(
			new IS_Commands.EmptyWidgetCommand(existWidget.widgetConf, existWidget.tabId.substring(3)));
		
		// Check for remove of the whole widget
		if ( !( IS_Portal.getSubWidgetList(parentWidget.id,parentWidget.tabId ).length > 0 )) {
			IS_Portal.removeWidget(parentWidget.id, parentWidget.tabId);
			
			IS_Request.CommandQueue.addCommand(
				new IS_Commands.EmptyWidgetCommand(parentWidget.widgetConf, parentWidget.tabId.substring(3)));
		}
	}else{
		IS_Portal.removeWidget(existWidget.id, existTab.id);
		
		IS_Request.CommandQueue.addCommand(
			new IS_Commands.EmptyWidgetCommand(existWidget.widgetConf, existWidget.tabId.substring(3)));
	}
}


IS_Widget.setPropertyCommand = function(owner, field, value){
	var cmd = new IS_Commands.UpdateWidgetPropertyCommand(owner.tabId.substring(3), owner, field, value);
	IS_Request.CommandQueue.addCommand(cmd);
}

IS_Widget.addWidgetCommand = function(owner){
	var sibling = "";
	if(owner.isBuilt){
		sibling = (owner.elm_widget.previousSibling)? owner.elm_widget.previousSibling.id : "";
	}
	var widgetConf = owner.widgetConf;
	
	var cmd = new IS_Commands.AddWidgetCommand(owner.tabId.substring(3), owner, owner.widgetConf.column, sibling, IS_Widget.getWidgetConfJSONString(widgetConf), widgetConf.parentId, widgetConf.menuId);
	IS_Request.CommandQueue.addCommand(cmd);
	
	if(owner.parent){
//		IS_Portal.subWidgetLists[owner.tabId][owner.id] = owner;
		IS_Portal.addSubWidget(owner.parent.id, owner.id, owner.tabId);
	}else{
//		IS_Portal.widgetLists[owner.tabId][owner.id] = owner;
		IS_Portal.addWidget(owner, owner.tabId);
	}
}

IS_Widget.updateWidgetCommand = function(owner){
	var sibling = "";
	if(owner.isBuilt){
		sibling = (owner.elm_widget.previousSibling)? owner.elm_widget.previousSibling.id : "";
	}
	
	var widgetConf = owner.widgetConf;
	var cmd = new IS_Commands.UpdateWidgetCommand(owner.tabId.substring(3), owner, owner.widgetConf.column, sibling, IS_Widget.getWidgetConfJSONString(widgetConf), widgetConf.parentId);
	IS_Request.CommandQueue.addCommand(cmd);
}

IS_Widget.addMultiWidgetCommand = function(owner){
	var sibling = "";
	if(owner.isBuilt){
		sibling = (owner.elm_widget.previousSibling)? owner.elm_widget.previousSibling.id : "";
	}
	var widgetConf = owner.widgetConf;
	IS_Portal.addWidget(owner, owner.tabId);
	
	var subWidgetList = IS_Portal.getSubWidgetList(owner.id, owner.tabId);
	var subWidgetConfList = [];
	for(var i=0;i<subWidgetList.length;i++){
		if(!subWidgetList[i]) continue;
		subWidgetConfList.push(IS_Widget.getWidgetConfJSONString(subWidgetList[i].widgetConf));
		IS_Portal.addSubWidget(subWidgetList[i].parent.id, subWidgetList[i].id, owner.tabId);
	}
	
	var cmd = new IS_Commands.AddMultiWidgetCommand(owner.tabId.substring(3), owner, owner.widgetConf.column, sibling, IS_Widget.getWidgetConfJSONString(widgetConf), Object.toJSON(subWidgetConfList), widgetConf.parentId, widgetConf.menuId);
	IS_Request.CommandQueue.addCommand(cmd);
}
/*
IS_Widget.updateWidgetCommand = function(owner){
	var sibling = "";
	if(owner.isBuilt){
		sibling = (owner.elm_widget.previousSibling)? owner.elm_widget.previousSibling.id : "";
	}
	var widgetConf = owner.widgetConf;
//	var cmd = new IS_Commands.UpdateWidgetCommand(owner.tabId.substring(3), owner, owner.widgetConf.column, sibling, widgetConf.toJSONString());
	var cmd = new IS_Commands.UpdateWidgetCommand(owner.tabId.substring(3), owner, owner.widgetConf.column, sibling, IS_Widget.getWidgetConfJSONString(widgetConf));
	IS_Request.CommandQueue.addCommand(cmd);
}*/

IS_Widget.setWidgetPrefernceCommand = function(owner, field, value){
	var cmd = new IS_Commands.UpdateWidgetPrefernceCommand(owner.tabId.substring(3), owner, field, value);
	IS_Request.CommandQueue.addCommand(cmd);
}

IS_Widget.addFeedCommand = function(owner, feed, sibling, parent){var copyConf = {};
	var cmd = new IS_Commands.AddWidgetCommand(owner.tabId.substring(3), feed, owner.widgetConf.column, sibling, IS_Widget.getWidgetConfJSONString(feed), parent, owner.widgetConf.menuId);
	IS_Request.CommandQueue.addCommand(cmd);
//	IS_Portal.subWidgetLists[owner.tabId][feed.id] = feed;
	IS_Portal.addSubWidget(parent.id , feed.id);
}

/*
IS_Widget.updateCheckedFeedCommand = function(owner){
	var cmd = new IS_Commands.UpdateCheckedFeedCommand(owner.tabId.substring(3), owner, owner.widgetConf.feed);
	IS_Request.CommandQueue.addCommand(cmd);
}
*/

IS_Widget.setWidgetLocationCommand = function(owner){
	if(!owner) return;
	var sibling = "";
	if(owner.isBuilt){
		sibling = (owner.elm_widget.previousSibling)? IS_Portal.getTrueId(owner.elm_widget.previousSibling.id) : "";
	} else {
		var ownerId = IS_Portal.getTrueId( owner.id );
		var widList = IS_Portal.widgetLists[owner.tabId];
		var prevId = null;
		for(var i in widList){
			if(typeof widList[i] == "function")
				continue;
			if(i == ownerId){
				sibling = prevId;
				break;
			}
			prevId = i;
		}
	}
	var widgetConf = owner.widgetConf;
	
	var cmd = new IS_Commands.UpdateWidgetLocationCommand(owner.tabId.substring(3), owner, owner.widgetConf.column, sibling, owner.parent);
	IS_Request.CommandQueue.addCommand(cmd);
	
	if(owner.parent){
//		IS_Portal.subWidgetLists[owner.tabId][owner.id] = owner;
		IS_Portal.addSubWidget(owner.parent.id, owner.id, owner.parent.tabId);
	}else{
//		IS_Portal.widgetLists[owner.tabId][owner.id] = owner;
		IS_Portal.addWidget(owner, owner.tabId);
	}
}

IS_Widget.setWidgetTabLocationCommand = function(owner, tabIdFrom, tabIdTo){
	var cmd = new IS_Commands.UpdateWidgetTabLocationCommand(tabIdFrom.substring(3), tabIdTo.substring(3), owner);
	IS_Request.CommandQueue.addCommand(cmd);
	owner.tabId = tabIdTo;
}

IS_Widget.removeWidgetCommand = function(owner, parent){
	if(!owner)return;
	
	var targetParent = (parent)? parent : owner.parent;
	
	var cmd = new IS_Commands.RemoveWidgetCommand(owner.tabId.substring(3), owner, targetParent);
	IS_Request.CommandQueue.addCommand(cmd);
	
	if(targetParent){
//		IS_Portal.subWidgetLists[owner.tabId][owner.id] = null;
		IS_Portal.removeSubWidget(targetParent.id, owner.id, targetParent.tabId);
	}else{
//		IS_Portal.widgetLists[owner.tabId][owner.id] = null;
		IS_Portal.removeWidget(owner.id, owner.tabId);
	}
}

IS_Widget.setTabPreferenceCommand = function(tabId, field, value){
	var cmd = new IS_Commands.UpdateTabPreferenceCommand(tabId.substring(3), field, value);
	IS_Request.CommandQueue.addCommand(cmd);
}

IS_Widget.setPreferenceCommand = function(field, value){
	var cmd = new IS_Commands.UpdatePreferenceCommand(field, value);
	IS_Request.CommandQueue.addCommand(cmd);
}

IS_Widget.addTabCommand = function(tabId, name, tabType, numCol){
	var cmd = new IS_Commands.AddTabCommand(tabId.substring(3), name, tabType, numCol);
	IS_Request.CommandQueue.addCommand(cmd);
}

IS_Widget.removeTabCommand = function(tabId){
	var cmd = new IS_Commands.RemoveTabCommand(tabId.substring(3));
	IS_Request.CommandQueue.addCommand(cmd);
}

IS_Widget.getWidgetConfJSONString = function(widconf) {
	if(!widconf) return "{}";
	
	var isFirst = true;
	var jsonStr = "{";
	for(var i in widconf){
		if(typeof widconf[i] == "function") continue;
		if(!isFirst) jsonStr += ',';
		else isFirst = false;
		
		if(i == "id"){
				jsonStr += "id:" + Object.toJSON(IS_Portal.getTrueId(widconf[i]));
		}else if(i == "property"){
			jsonStr += Object.toJSON(i) + ":{";
			var isPropFirst = true;
			for(var j in widconf[i]){
				if( widconf.isDefault && !widconf.isDefault(j)){
					//f = toJSONString[typeof widconf[i][j]];	
					//if(f){
					v = Object.toJSON(widconf[i][j]);
					if(!isPropFirst)
					  jsonStr += ',';
					else
					  isPropFirst = false;
					jsonStr += Object.toJSON(j) + ":" + v;
					//}
				}
			}
			jsonStr += "}";
		}else if(i == "defaultProperty"){
			isFirst = true;
		}else{
			//f = toJSONString[typeof widconf[i]];
			//if(f){
				v = Object.toJSON(widconf[i]);
				jsonStr += Object.toJSON(i) + ":" + v;
			//}else{
			//	isFirst = true;
			//}
		}
	}
	jsonStr += "}";
	return jsonStr;
};

IS_Widget.getDragDummy = function(element, widget){
	  var dragElement = document.createElement("div");
	  dragElement.className = element.className;
	  var shade = document.createElement("div");
	  shade.className = "widgetShade";
	  shade.style.height = "100%";
	  dragElement.appendChild( shade );
	  
	  var box = document.createElement("div");
	  box.className = "widgetBox";
	  box.style.height = "100%";
	  shade.appendChild( box );
	  var header = document.createElement("div");
	  header.className = "widgetHeader";
	  header.style.fontWeight = "bold";
	  
	  var titleEl = document.createElement("div");
	  titleEl.className = "widgetTitle";
	  titleEl.appendChild( document.createTextNode( IS_Widget.WidgetHeader.getTitle(widget) ));
	  titleEl.style.padding = "2px";
	  
	  header.appendChild( titleEl);
	  
	  box.appendChild( header );
	  
	  if (widget.getBoolUserPref("openWidget")) {
	    var content = document.createElement("div");
	    box.appendChild( content );
	    content.style.height = "100%";
	    content.style.width = "100%";
	  }else{
	  	dragElement.style.height = element.offsetHeight;
	  }
	  
	  return dragElement;
}

IS_Widget.getDisplayName = function(widgetType, prefName){
	var name = null;
	try{
		name = IS_WidgetConfiguration[widgetType].UserPref[prefName].display_name;
	}catch(e){
	}
	return name ? name : prefName;
}

IS_Widget.getIcon = function(widgetType, opt){
	if( !widgetType )
		return null;
	
	var widConf = IS_WidgetConfiguration[widgetType];
	if(widConf && widConf.icon) {
		if(!/http[s]?:\/\//.test(widConf.icon))
			return imageURL + widConf.icon;
		return widConf.icon;
	}
	var isUploadGadget = widgetType.match(/^g_upload__(.*)\/gadget/);
	if(isUploadGadget){
		var gadgetType = RegExp.$1;
		var icon = IS_WidgetIcons[gadgetType];
		if(icon) {
			/*
			if(!/^http[s]?:\/\//.test(icon)){
				return hostPrefix + '/gadget/' + gadgetType + '/' + icon;
			}
			return icon;
			*/
			return icon.replace("__IS_GADGET_BASE_URL__", hostPrefix + '/gadget/' + gadgetType);
		} else if(widConf && widConf.ModulePrefs && widConf.ModulePrefs.Icon){
			icon = widConf.ModulePrefs.Icon.content;
			/*
			if(/http[s]?:\/\//.test()){
				return icon;
			} else if(widConf.ModulePrefs.resource_url){
				return widConf.ModulePrefs.resource_url + icon;
			} else {
				return hostPrefix + '/gadget/' + gadgetType + '/' + icon;
			}
			*/
			if(widConf.ModulePrefs.resource_url){
				return widConf.ModulePrefs.resource_url + icon;
			} else {
				return icon;
			}
		}
	}
	var isGadget = widgetType == 'Gadget' || widgetType.startsWith('g_');
	if((isUploadGadget && typeof icon == 'undefined')
		|| (!isGadget && !widConf)){
		return imageURL + 'not_available.gif';
	}
	if(opt){
		if(opt.multi)
			return imageURL + 'widget_add_multi.gif';
		if(opt.defaultNull)
			return null;
	}
	return imageURL + 'widget_add.gif';
}

IS_Widget.setIcon = function(div, widgetType, opt){
	div.style.backgroundImage = 'url(' + IS_Widget.getIcon(widgetType, opt) + ')';
}
