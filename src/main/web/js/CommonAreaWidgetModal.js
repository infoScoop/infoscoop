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

IS_CommonAreaWidgetModal = Class.create();
IS_CommonAreaWidgetModal.prototype = {
	initialize: function(){
	    this.content = $jq("#is-commonarea-widgetmodal");
	    this.previewPanel = $jq(".commonarea-widgetmodal-preview", this.content);
	    this.previewIndicator = $jq(".preview-indicator", this.content);
	    this.previewWidgetList = [];
		this.caWidgetModal = new Control.Modal($('is-commonarea-widgetmodal'), {
			overlayOpacity: 0.55,
			className: 'is-commonarea-widgetmodal',
			width: getWindowSize(true) - 200,
			fade: true,
			afterClose: function(){
                this.finish();
	            if(this.tabs)
	                this.tabs.tabs("option", "active", 0);
			}.bind(this)
		});

		this.loadWidgetModal();
		
		// init WebSiteURL Form
		$jq(".preview-button", this.content).click(this.previewButtonClicked.bind(this));
		$jq(".preview-form", this.content).keypress(function(e){
		    if ( e.which == 13 ){
		        this.previewButtonClicked();
		        return false;
		    }
		}.bind(this));
		
		 IS_EventDispatcher.addListener('windowResized', null, this.resizeModal.bind(this));
	},
	
   previewButtonClicked: function(){
        
        this.authType = false;
        this.authUid = "";
        this.authPassword = "";
        this.authCredentialId = "";
        
        var prevURL = $jq(".preview-form", this.content).val().trim();
        if(prevURL.length == 0){
            alert( IS_R.ms_urlNoInput );
            return;
        }
        $jq(".preview-form", this.content).val(prevURL);
        
        if (!new RegExp("^(http://|https://|ftp://)").test(prevURL)) {
            prevURL = (prevURL.indexOf("/") === 0) ?
                    findHostURL(true) + prevURL : hostPrefix + "/" + prevURL;
        }

        var encodedURL = encodeURIComponent( prevURL );
        if( encodedURL.length > 2000 ) {
            alert( IS_R.ms_urlTooLong );
            return;
        }
        
        this.previewPanel.hide();
        this.previewIndicator.show();
        
        is_processUrlContents(prevURL, this.handleDetect.bind(this, prevURL, false), function(){});
    },
	

    /**
     * Scope can be invaild
     */ 
    handleDetect : function(url, isRetry, response){
        try {
            this._authType = response.getResponseHeader("MSDPortal-AuthType");
        } catch(ex){}
        
        this.resetPreview();
        
        var dataList;
        try {
            dataList = eval("("+response.responseText+")");
        } catch( ex ) {
            dataList = [{
                type: "MiniBrowser",
                url: url
            }];
        }
        
        this.displayPreview( dataList, url, isRetry );
    },
    
    displayPreview: function( dataList, url, isRetry ){
        
        //Copy from Widget.js
        if(this._authType){
            this.authType = this._authType;
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
                            if(typeof this._authUid == 'undefined') {
                                this.previewIndicator.hide();
                                return;
                            }
                            this.authUid = (this._authUid) ? this._authUid : " ";
                            this.authPassword = (this._authPassword) ? this._authPassword : " ";
                            is_processUrlContents(url, this.handleDetect.bind(this, url, true), function(){}, ["authType", this.authType, "authuserid",this.authUid,"authpassword",this.authPassword], method);
                        }.bind(this),
                        true, //isModal
                        authErrorMsg
                    );
                }
                var opt = {
                  method: 'post',
                  parameters: { command: "try",authType: this.authType,url: url },
                  onSuccess:function(req, obj){
                      var _credentialId = req.responseText;
                      if(new RegExp("[0-9]+").test(_credentialId)){
                          this.authCredentialId = _credentialId;
                          is_processUrlContents(url, handleDetect.bind(this, url, true), function(){}, ["authCredentialId",this.authCredentialId], method);
                      }else{
                          showCredentialForm();
                      }
                  }
                }
                AjaxRequest.invoke(hostPrefix + "/credsrv", opt);
                return;
            }
        }
        
        var length = dataList.length;
        
        // permit mini-browser to use server-root-path or relative-path
        if (!new RegExp("^(http://|https://|ftp://)").test(url)) {
            var miniBrowser = null;
            for (var i = 0; i < length; i++) {
                var data = dataList[i];
                if (data.type === "MiniBrowser") {
                    miniBrowser = data;
                    miniBrowser.url = url;
                    break;
                }
            }
            dataList = (miniBrowser) ? [ miniBrowser ] : [ { type : "MiniBrowser", url : null } ];
            length = dataList.length;
        }

        for(var i = 0; i < length; i++){
            var previewItem = $jq("<div>").addClass("preview-item");
            $jq(".preview-list", this.previewPanel).append( previewItem );
            this.displayPreviewWidgets(i, previewItem, dataList[i] );
        }
        
        this.previewIndicator.hide();
        this.previewPanel.show();
    },
    
    displayPreviewWidgets: function(index, _previewItem, previewData ){
        var type = previewData.type;
        var url = previewData.url;
        var title = previewData.title;// refs 1878
        var href = previewData.href;
        var feedType = previewData.feedType;
        
        if( title && title.length > 80 )
            title = title.substring( 0,80 );
        
        if( href && href.length > 256 )
            href = is_getTruncatedString( href,1024 );
        
        var widgetId = false;
        var properties = new Object();
        
        var id = "designPanel_previewWidget_"+ index;
        if(type == "Gadget"){
            type = "g_" + url;
        }else{
            properties["url"] = url;
        }
        if(this.authType && requiredFormAuthTypes.indexOf(this.authType) != -1){
            if(this.authCredentialId){
                properties["previewAuthCredentialId"] = this.authCredentialId;
            }else{
                properties["previewAuthType"] = this.authType;
                properties["previewAuthUserId"] = this.authUid;
                properties["previewAuthPasswd"] = this.authPassword;
                delete properties["authType"];
            }
        }
        
        var widget;
        var widgetJSON = {
            id : id,
            type : type,
            title : title,
            href : href,
            property: Object.extend({},properties)
        }
        try{
            widget = new IS_Widget(false, widgetJSON);
            widget.build();
        }catch(e){
            msg.error(IS_R.ms_widgetBuildError + e);
            widget = false;
        }
        
        var currentTabId = IS_Portal.currentTabId;
        
        widget.tabId = currentTabId;
        IS_Portal.widgetLists[currentTabId][widget.id] = widget;
        
        var previewDiv = $jq("<div>").addClass("preview-widget-container");
        var widgetBody;
        if(widget){
            widgetBody = $jq(widget.elm_widget);
            this.previewWidgetList.push( widget );
        }else{
            widgetBody = $jq("<div>").addClass("errorPreview").text(IS_R.lb_previewError)
        }
        previewDiv.append( widgetBody );
        _previewItem.append( previewDiv );
        
        if(widget){
            var addButtonDiv = $jq("<div>").addClass("apply-button").appendTo(_previewItem);
            var addButton = $jq("<input>").attr("type", "button").addClass("is-button").val(IS_R.lb_do_save).appendTo(addButtonDiv);
            
            addButton.click(function(widgetJSON){
                widgetJSON.properties = widgetJSON.property;
                this.applyGadgetSetting(widgetJSON);
                this.finish();
            }.bind(this, widgetJSON));
        }
        
        if(widget)
            this.freezeGadget(widget);
    },
    
    adjustWidgetATag: function( widget ){
        if(!(widget && widget.elm_widget)) return;
        var aTags = widget.elm_widget.getElementsByTagName( "a" );
        if(!aTags) return;
        for(var i=0; i < aTags.length; i++){
            aTags[i].target = "_blank";
        }
    },
    
    freezeGadget: function(widget){
        IS_EventDispatcher.addListener('loadComplete', widget.id, function(){
            IS_Event.unloadCache(widget.id);
            IS_Event.unloadCache(widget.closeId);
            this.adjustWidgetATag(widget);
            if(!widget.iframe){
                IS_Event.observe(widget.elm_widgetContent, 'click', function(e){Event.stop(e);return false;},true);
                IS_Event.observe(widget.elm_widgetContent, 'mousedown', function(e){Event.stop(e);return false;},true);
                IS_Event.observe(widget.elm_widgetContent, 'mouseup', function(e){Event.stop(e);return false;},true);
                delete IS_Portal.widgetLists[IS_Portal.currentTabId][widget.id];
            }
        
        }.bind(this), null, true);
        
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
                
                delete IS_Portal.widgetLists[IS_Portal.currentTabId][widget.id];
            }, false);
        }
    },
    
    resetPreview: function(){
        if(this.previewWidgetList.length > 0){
            this.previewWidgetList.each(function(previewWidget){
                if( previewWidget.headerContent )
                    previewWidget.headerContent.close({}, true);
            });
        }
        this.previewWidgetList = [];
        $jq(".preview-list", this.content).empty();
        this.previewPanel.hide();
    },
    
	start: function(targetWidgetId, widgetJSON, callbackFunc, isNew) {
	    this.callbackFunc = callbackFunc;
	    this.targetWidgetId = targetWidgetId;
	    this.widgetJSON = widgetJSON;
	    
		if(this.caWidgetModal) {
			this.caWidgetModal.open();
			if(this.tabs)
			    this.tabs.tabs("option", "active", isNew? 0 : 1);
		}
	},

	finish: function() {
        this.callbackFunc = null;
        this.targetWidgetId = null;
        this.widgetJSON = null;
        this.initSettingPanel = false;
        
	    if(this.caWidgetModal)
			this.caWidgetModal.close();
	},

    applyGadgetSetting: function(widgetJSON, form){
        widgetJSON.id = this.targetWidgetId;
        if(this.callbackFunc)
            this.callbackFunc(widgetJSON, form);
    },
    
    resizeModal: function(){
        if(!this.caWidgetModal.isOpen)
            return;

        this.caWidgetModal.container.style.width = getWindowSize(true) - 200 + "px";
        this.caWidgetModal.position();
    },

	loadWidgetModal: function(){
		// header
		$jq("#commonarea-widgetmodal-cancel-image").on('click', this.finish.bind(this));

		this.tabs = $jq("#commonarea-widgetmodal-contents").tabs().addClass( "ui-tabs-vertical ui-helper-clearfix" );
		$jq("#commonarea-widgetmodal-contents li").removeClass( "ui-corner-top" ).addClass( "ui-corner-left" );
		
		this.tabs.on( "tabsactivate", function( event, ui ) {
		    if(this.initSettingPanel)
		        return;
		    
		    if("commonarea-widgetmodal-item-2" == $jq(ui.newPanel).attr("id")){
		        var settingFrame = $jq(".gadget-settings", $jq(ui.newPanel));
		        var settingFrameContent = settingFrame[0].contentWindow;
		        settingFrameContent.ISA_GadgetSettings.gadgetSettings.displayContents(this.widgetJSON, this.applyGadgetSetting.bind(this));
		        this.caWidgetModal.position();
		        this.initSettingPanel = true;
		    }
		}.bind(this));
	}
}