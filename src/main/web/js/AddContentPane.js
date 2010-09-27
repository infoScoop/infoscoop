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



IS_SidePanel.buildAddContents = function() {
	var authType;
	var authUid;
	var authPassword;
	var authCredentialId;
	
	var _authType;
	var requiredFormAuthTypes = ['basic','ntlm','postCredential'];
	
	var rssSearch = document.getElementById("portal-rss-search");
	var container = document.createElement("div");
	Element.addClassName( container,"SidePanel_AddContents");
	rssSearch.appendChild( container );
	
	var addContentsText = document.createElement("div");
	addContentsText.appendChild( document.createTextNode( IS_R.ms_inputURL ) );
	container.appendChild(addContentsText);

	var inputURLBoxDiv = document.createElement("div");
	var inputURLBox = document.createElement("input");
	inputURLBox.type = "text";
	inputURLBox.style.width = "92%";
	inputURLBox.maxLength = 2000;
	inputURLBoxDiv.appendChild(inputURLBox);
	container.appendChild(inputURLBoxDiv);
	
	var previewButtonDiv = document.createElement("div");
	previewButtonDiv.style.textAlign = 'right';
	var previewIndicator = document.createElement("img");
	previewIndicator.src = imageURL+"ajax-loader.gif";
	previewIndicator.style.display = "none";
	previewButtonDiv.appendChild( previewIndicator );
	
	var previewButton = document.createElement("input");
	previewButton.type = "button";
	previewButton.value = IS_R.lb_preview;
	previewButtonDiv.appendChild( previewButton );
	previewButtonDiv.style.width = "92%";
	container.appendChild(previewButtonDiv);

	//Example of URL input：
	//RSS:http://www.beacon-it.co.jp/rss/Beacon-IT_rss.xml
	//Web site：http://www.beacon-it.co.jp/
	//Google gadget：http://infoscoopdemo.beacon-it.co.jp/HelloGadget.xml
	var exampleDiv = document.createElement("div");
	exampleDiv.style.overflow = "hidden";
	exampleDiv.style.color = "black";
	exampleDiv.appendChild( document.createTextNode(IS_R.lb_urlExample) );
	
	function addExample(label ,url){
		var div = document.createElement("div");
		div.style.width = "92%";
		div.appendChild(document.createTextNode(label));
		var input = document.createElement("input");
		input.value = url;
		input.className = 'addContentsEx';
		input.readOnly = true;
		Event.observe(input, "click", function(){input.select();},false);
		div.appendChild(input);
		exampleDiv.appendChild(div);
	}
	addExample(IS_R.lb_rssColon,IS_R.lb_addContentsExRSS);
	addExample(IS_R.lb_webSiteColon,IS_R.lb_addContentsExWebSite);
	addExample(IS_R.lb_gadgetColon,IS_R.lb_addContentsExGadget);
	
	container.appendChild(exampleDiv);
	
	var addContentPanel = document.createElement("div");
	addContentPanel.style.display = "none";
	addContentPanel.style.marginTop = "5px";
	addContentPanel.style.width = "92%";
	container.appendChild( addContentPanel );
	
	var addContents = document.createElement("div");
	addContentPanel.appendChild( addContents );
	
	IS_Event.observe(previewButton, "click", previewButtonClicked.bind(this), false);
	IS_Event.observe(inputURLBox, "keydown", inputBoxOnKeyDown.bind(this), false);
	
	function inputBoxOnKeyDown(e){
		if (Browser.isIE) {	/* for IE */
			e = event;
		}
		if(e.keyCode == 13){
			previewButtonClicked();
		}
	}
	
	var currentTabId = IS_Portal.currentTabId;
	IS_SidePanel.previewWidgetList = [];
	
	function previewButtonClicked(){
		
		authType = false;
		authUid = "";
		authPassword = "";
		authCredentialId = "";
		  
		var inputURL = inputURLBox.value;
		var trimText = inputURL.replace(/ |　/g, "");
		if(trimText.length == 0){
			alert( IS_R.ms_urlNoInput );
			return;
		}
		
		if(!/^(http|https|ftp):\/\//.test(inputURL)){
			inputURL = "http://"+inputURL;
			inputURLBox.value = inputURL;
		}
		
		var encodedURL = encodeURIComponent( inputURL );
		if( encodedURL.length > 2000 ) {
			alert( IS_R.ms_urlTooLong );
			return;
		}
				
		hideAddContentPanel();
		previewIndicator.style.display = "";
		
		is_processUrlContents(inputURL, handleDetect.bind(this, inputURL, false), function(){});
		
	}

	/**
     * Scope can be invaild
     */ 
	function handleDetect(url, isRetry, response){
		try {
			_authType = response.getResponseHeader("MSDPortal-AuthType");
		} catch(ex){}
		
		IS_Event.unloadCache("_otherContents");
		resetPreviewWidgetList();
//		addContents.innerHTML = "";
		while(addContents.firstChild != null){
			addContents.removeChild(addContents.firstChild);
		}
		var dataList;
		try {
			dataList = eval("("+response.responseText+")");
		} catch( ex ) {
			dataList = [{
				type: "MiniBrowser",
				url: url
			}];
		}
		
		displayPreview( dataList, url, isRetry );
	}
	
	function displayPreview( dataList, url, isRetry ){
		if( !IS_TreeMenu.isLoaded() ) {
			return IS_TreeMenu.waitForLoadMenu( function() {
				displayPreview.apply( this,[dataList, url, isRetry] );
			}.bind( this ) );
		}
		
		dataList = dataList.collect( function( data ) {
			var menuItems = IS_TreeMenu.findMenuItemByURL( data.type,data.url );
			if( menuItems.length > 0 ) {
				return menuItems.collect( function( menuItem ) {
					var previewData = Object.clone( data );
					previewData.menuItem = menuItem;
					
					return previewData;
				});
			} else {
				return data;
			}
		}).flatten();
		
		//#2048 Handle if there is only one corresponding menu
		//Other logic is necessary for UI if multiple items are matched
		var length = dataList.length;
		if(!isRetry && length == 1 && dataList[0].menuItem){
			_authType = dataList[0].menuItem.properties.authType;
		}
		
		//Copy from Widget.js
		try{
		if(_authType){
			authType = _authType;
			if(requiredFormAuthTypes.indexOf(authType) != -1){
				var authErrorMsg = false;
				if(authPassword){
					authErrorMsg = IS_R.ms_authFailure;
				}
				var method = authType.indexOf("post") == 0 ? 'post' : 'get';
				function showCredentialForm(){
					IS_Request.createModalAuthFormDiv(
						IS_R.lb_preview,
						previewButtonDiv,
						function (_authUid, _authPassword){
							if(typeof _authUid == 'undefined') {
								previewIndicator.style.display = "none";
								return;
							}
							authUid = (_authUid) ? _authUid : " ";
							authPassword = (_authPassword) ? _authPassword : " ";
							is_processUrlContents(url, handleDetect.bind(this, url, true), function(){}, ["authType", authType, "authuserid",authUid,"authpassword",authPassword], method);
						},
						true, //isModal
						authErrorMsg
					);
				}
				var opt = {
				  method: 'post',
				  parameters: { command: "try",authType: authType,url: url },
				  onSuccess:function(req, obj){
					  var _credentialId = req.responseText;
					  if(new RegExp("[0-9]+").test(_credentialId)){
						  authCredentialId = _credentialId;
						  is_processUrlContents(url, handleDetect.bind(this, url, true), function(){}, ["authCredentialId",authCredentialId], method);
					  }else{
						  showCredentialForm();
					  }
				  },
				  onException:function(req, obj){
					  console.log(obj);
				  }
				}
				AjaxRequest.invoke(hostPrefix + "/credsrv", opt);
				return;
			}
		}
		}catch(e){
			console.error(e);
//			return;
		}
		
		for(var i = 0; i < length; i++){
			var data = dataList[i];
			var previewItem = document.createElement("div");
			addContents.appendChild( previewItem );
			
			addPreviewWidget(i, previewItem, data );
		}
		
		previewIndicator.style.display = "none";
		addContentPanel.style.display = "";
		
		function addPreviewWidget(index, _previewItem, previewData ){
			var type = previewData.type;
			var url = previewData.url;
//			var title = (previewData.title && (previewData.title.replace(/\s/g, "").length > 0))? previewData.title : IS_R.lb_notitle;
			var title = previewData.title;// refs 1878
			var href = previewData.href;
			var feedType = previewData.feedType;
			var menuItem = previewData.menuItem;
			
			if( title && title.length > 80 )
				title = title.substring( 0,80 );
			
			if( href && href.length > 256 )
				href = is_getTruncatedString( href,1024 );
			
			var previewContext = {};
			
			var addContentText = document.createElement("div");
			addContentText.appendChild( document.createTextNode( IS_R.lb_canBeAddContents  + ( (length > 1) ? "#" + (index + 1) : "") + ":" ) );
			_previewItem.appendChild( addContentText );
			
			if (feedType) {
				var feedTypeDiv = document.createElement("div");
				feedTypeDiv.appendChild(document.createTextNode(feedType));
				_previewItem.appendChild(feedTypeDiv);
			}
			
			var widgetId = false;
			var isDropMenuItem = false;
			var menuId;
			var multi;
			
			var properties = new Object();
			if( menuItem ) {
				menuId = menuItem.id;
				widgetId = "w_" + menuItem.id;
				title = menuItem.title;
				href = menuItem.href;
				isDropMenuItem = true;
				multi = /true/i.test( menuItem.multi );
				authType = menuItem.properties.authType;
				
				var menuPathDiv = document.createElement("div");
				menuPathDiv.style.fontSize = "9pt";
				menuPathDiv.style.color = "black";
				menuPathDiv.title = menuItem.getPaths().join("/");
				menuPathDiv.appendChild(document.createTextNode( IS_R.ms_previewMenuPath));
				_previewItem.appendChild(menuPathDiv);
				Object.extend(properties, menuItem.properties);
			}
			
			var id = "previewWidget_"+ index;
			if(type == "Gadget"){
				type = "g_" + url;
			}else{
				properties["url"] = url;
			}
			if(authType && requiredFormAuthTypes.indexOf(authType) != -1){
				if(authCredentialId){
					properties["previewAuthCredentialId"] = authCredentialId;
				}else{
					properties["previewAuthType"] = authType;
					properties["previewAuthUserId"] = authUid;
					properties["previewAuthPasswd"] = authPassword;
					delete properties["authType"];
				}
			}
			
			var widgetObj = {
			  id : id,
			  type : type,
			  title : title,
			  href : href
			};
			widgetObj.property = Object.extend({},properties );
			var widget = buildWidget(widgetObj);
//			this.previewWidget = widget;
			previewContext.previewWidget = widget;
			
			IS_Portal.widgetLists[currentTabId][widget.id] = widget;
			widget.tabId = currentTabId;
		
			adjustWidgetATag(widget);
		
			var previewDiv = document.createElement("div");
			var widgetBody;
			if(widget){
				previewDiv.style.fontSize = "100%";
				previewDiv.style.color = "#000";
				widget.elm_widgetHeader.style.fontSize = "16px";
				widgetBody = widget.elm_widget;
				IS_SidePanel.previewWidgetList.push( widget );
			}else{
				widgetBody = document.createElement("div");
				widgetBody.className = "errorPreview";
				widgetBody.innerHTML = IS_R.lb_previewError;
			}
			previewDiv.appendChild( widgetBody );
			_previewItem.appendChild( previewDiv );
			
			if(widget){
				var addButtonDiv = document.createElement("div");
				addButtonDiv.style.textAlign = 'right';
				addButtonDiv.style.position = "relative";
				addButtonDiv.style.top = "-5px";
				var addButton = document.createElement("input");
				addButton.type = "button";
				addButton.value = IS_R.lb_add;
				addButtonDiv.appendChild( addButton );
				_previewItem.appendChild( addButtonDiv );
				
				var dropListener = function() {
					// The item can not be synchronized though old item is displayed if menu is updated
					if( multi ) return;
					
					addButton.value = IS_R.lb_added;
					addButton.disabled = true;
				};
				var closeListener = function() {
					addButton.value = IS_R.lb_add;
					addButton.disabled = false;
				};
				IS_Event.observe(addButton, "click",function() {
					if( addButton.disabled )
						return;
					
					addWidget.apply( this,[widgetId, type, title, href, properties, isDropMenuItem, multi])
					
					if( isDropMenuItem ) {
						IS_EventDispatcher.removeListener('dropWidget',menuId,dropListener );
						IS_EventDispatcher.removeListener('closeWidget',menuId,closeListener );
					}
				}.bind( previewContext ),false, "_otherContents");
				
				if( isDropMenuItem ) {
					for( var i in IS_Portal.widgetLists ){
						if( IS_Portal.widgetLists[i][widgetId] ) {
							dropListener();
							break;
						}
					}
					
					IS_EventDispatcher.addListener('dropWidget',menuId,dropListener );
					IS_EventDispatcher.addListener('closeWidget',menuId,closeListener );
				}
			}
			
			if(widget){
				IS_EventDispatcher.addListener('loadComplete', widget.id, function(){
					IS_Event.unloadCache(widget.id);
					IS_Event.unloadCache(widget.closeId);
					adjustWidgetATag(widget);
					if(!widget.iframe){
						IS_Event.observe(widget.elm_widgetContent, 'click', function(e){Event.stop(e);return false;},true);
						IS_Event.observe(widget.elm_widgetContent, 'mousedown', function(e){Event.stop(e);return false;},true);
						IS_Event.observe(widget.elm_widgetContent, 'mouseup', function(e){Event.stop(e);return false;},true);
						delete IS_Portal.widgetLists[currentTabId][widget.id];
					}
				
				}, null, true);
				
				widget.loadContents();
				
				IS_Event.unloadCache(widget.id);
				IS_Event.unloadCache(widget.closeId);
				
				if(widget.iframe){
					IS_Event.observe(widget.iframe, 'load', function(){
						IS_EventDispatcher.newEvent('loadComplete',widget.id );
						
						try { // Error can be occured on Gadget 
							var previewIFrame = widget.iframe.contentDocument ? widget.iframe.contentDocument : widget.iframe.contentWindow.document;
							
							IS_Event.observe(previewIFrame.documentElement, 'click', function(e){Event.stop(e);return false;},true);
							IS_Event.observe(previewIFrame.documentElement, 'mousedown', function(e){Event.stop(e);return false;},true);
							IS_Event.observe(previewIFrame.documentElement, 'mouseup', function(e){Event.stop(e);return false;},true);
						} catch( ex ) {}
						
						delete IS_Portal.widgetLists[currentTabId][widget.id];
					}, false);
				}
			}
		}
	}
	
	
	function adjustWidgetATag( widget ){
		if(!(widget && widget.elm_widget)) return;
		var aTags = widget.elm_widget.getElementsByTagName( "a" );
		if(!aTags) return;
		for(var i=0; i < aTags.length; i++){
			aTags[i].target = "_blank";
		}
	}
	
	function buildWidget(widgetObj){
		try{
			var widget = new IS_Widget(false, widgetObj);
			widget.build();
			
			return widget;
		}catch(e){
			msg.error(IS_R.ms_widgetBuildError + e);
		}
		return null;
	}
	
	function addWidget(widgetId, type, title, href, properties, isDropMenuItem, isMulti){
		var addFeed = true;
		if(isDropMenuItem){
			for( var i in IS_Portal.widgetLists){
				if(IS_Portal.widgetLists[i][widgetId]){
					alert(IS_R.lb_doubleAdd);
					addFeed = false;
				}
				if(!addFeed)break;
			}
		}
		if(!addFeed) return;
		
		if(!widgetId || isMulti){
			widgetId = "w_" + new Date().getTime();
		}

		for(i in properties){
			if(i.indexOf && i.indexOf("preview") == 0){
				//console.log(properties[i]);
				delete properties[i];
			}
		}
		

		function applyAddWidget(){
			hideAddContentPanel();
			IS_Event.unloadCache("_otherContents");
			resetPreviewWidgetList();
			//		addContents.innerHTML = "";
			while(addContents.firstChild != null){
				addContents.removeChild(addContents.firstChild);
			}
			
			if( IS_Widget.MaximizeWidget && IS_Widget.MaximizeWidget.headerContent )
				IS_Widget.MaximizeWidget.headerContent.turnbackMaximize();
			
			var widgetConf = IS_WidgetsContainer.WidgetConfiguration.getConfigurationJSONObject(
				type, widgetId, 1, title, href, properties);
			var widget = IS_WidgetsContainer.addWidget( IS_Portal.currentTabId, widgetConf );
			IS_Widget.setWidgetLocationCommand(widget);
			
			IS_Portal.widgetDropped( widget );
			
			if(widget.getUserPref('loginCredentialAuthType'))
				IS_EventDispatcher.newEvent("resetAuthCredential","resetAuthCredential");
		}
		
		if(authType && requiredFormAuthTypes.indexOf(authType) != -1){
			var opt = {
			  method: 'post',
			  asynchronous: true,
			  parameters: { command:"try",authType: authType,url: properties["url"] },
			  onSuccess:function(req, obj){
				  var authCredentialId = req.responseText;
				  if(new RegExp("[0-9]+").test(authCredentialId)){
					  //widgetConf.property['authCredentialId'] = authCredentialId;
					  applyAddWidget();
				  }else{
					  var authUrl = properties["url"];
					  var _opt = {
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
								var authCredentialId = req.responseText;
								properties['authType'] = authType;
								properties['authCredentialId'] = authCredentialId;
								applyAddWidget();
							}else{
								alert("Fatal error");
							}
						},
						onException:function(req, obj){
							console.log(obj);
						}
					  }
					  AjaxRequest.invoke(hostPrefix + "/credsrv", _opt, "previewWidget");
				  }
			  },
			  onException:function(req, obj){
				  console.log(obj);
			  }
			}
			AjaxRequest.invoke(hostPrefix + "/credsrv", opt, "previewWidget");
		}else{
			applyAddWidget();
		}
		
	}
	
	function resetPreviewWidgetList(){
		if(IS_SidePanel.previewWidgetList)
			IS_SidePanel.previewWidgetList.each(function(previewWidget){
				if( previewWidget.headerContent )
					previewWidget.headerContent.close({}, true);
			});
		currentTabId = IS_Portal.currentTabId;
		IS_SidePanel.previewWidgetList = [];
	}
	
	IS_EventDispatcher.addListener('closeSiteMap',"portal-side-menu",hideAddContentPanel );
	
	IS_EventDispatcher.addListener('closeSiteMap',"portal-side-menu",clearInputURLBox );
	function clearInputURLBox(){
		inputURLBox.value = "";
	}
	
	function hideAddContentPanel( event ) {
		if( Element.visible( addContentPanel ))
			Element.hide( addContentPanel );
	}
}
