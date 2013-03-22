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


IS_Widget.MaximizeWidget = null;

//IS_MaximizeWidget = IS_Class.extend( IS_Widget );
IS_Widget.Maximize = {}
IS_Widget.Maximize.createMaximizeWidget = function( wigetObj ) {
	var widget = wigetObj;
	
	var originalWidget = widget.parent ? widget.parent : widget;
	
	var widgetType = originalWidget.widgetType;
	var widgetConf = IS_WidgetConfiguration[widgetType];
	
	var maximize = widgetConf.Maximize;
	
	var maximizeType = IS_Widget.Maximize.getMaximizeType( widgetType );
	if( !IS_WidgetConfiguration[ maximizeType ] )
		IS_WidgetConfiguration[ maximizeType ] = IS_Widget.Maximize.getMaximizeConfiguration( widgetConf );
	
	var mazimizeId = "__Maximize__"+originalWidget.id;
	var maximizeXml = IS_WidgetsContainer.WidgetConfiguration.getConfigurationJSONObject(
		maximizeType,mazimizeId,
		originalWidget.column,originalWidget.title,originalWidget.title_url,{
			url : originalWidget.getUserPref("url")
		});
	var maximizeWidgetClass = IS_Class.extend( IS_Widget );
	maximizeWidgetClass.prototype.classDef = function() {
		this.initialize = function(draggable, widgetsXml, parent) {
			this.tabId = originalWidget.tabId;
			this.baseId = widget.id;
			this.baseWidget = widget;
			this.originalId = originalWidget.id;
			this.originalWidget = originalWidget;
			this.resourceUrl = originalWidget.resourceUrl;
			
			this.keybind = new IS_Widget.Maximize.Keybind( this );
			
			IS_Event.observe( document,'keydown',this.keybind.processKeyEvent.bind( this.keybind ),false,this.closeId );
			//this._super.initialize( draggable,widgetsXml,parent );
		}
		this.initUserPref = function( key,value ) {
			this.originalWidget.initUserPref( key,value );
		}
		this.getUserPref = function( key ) {
			return this.originalWidget.getUserPref( key );
		}
		this.getUserPrefKeys = function() {
			return this.originalWidget.getUserPrefKeys();
		}
		this.setUserPref = function( key,value ) {
			this.originalWidget.setUserPref( key,value );
		}
		this._super.loadHtmlIfram = this.loadHtmlIfram;
		this.loadHtmlIfram = function( url ) {
			var url = widgetType.substring(2);
			if(/^upload/.test( url ) )
				url = hostPrefix + "/gadgetsrv/" + url;
			
			if( /^g_/.test(this.widgetType) ) {
				this._super.loadHtmlIfram.apply( this, [url,"Maximize"] );
			} else {
				this._super.loadHtmlIfram.apply( {
					iframe: this.iframe,
					widgetType: this.originalWidget.widgetType,
					id: this.id
				});
			}
		}
		
		this.changeMaximize = function( baseWidget ) {
			this.baseWidget = baseWidget || this.originalWidget;
			
			this.view_params = this.baseWidget.view_params;
			
			IS_Widget.Maximize.setupMaximizeView();
			
			var isBuilt = this.isBuilt;
			if( !isBuilt ) {
				this.build();
				
				//this.elm_widget.style.overflow = "hidden";
//				this.headerContent.headerIconTable.parentNode.style.width = "auto";
				
				["mouseup","keyup"].each( function( eventName ) {
					IS_Event.observe( this.elm_widget,eventName,this.handleUserAction.bind( this ),true,this.closeId );
				}.bind( this ));
			}
			
			$( this.elm_widgetContent ).addClassName("maximize");
			
			var maximizePanel = $("maximize-panel");
			maximizePanel.appendChild( this.elm_widget );
			this.headerContent.applyAllIconStyle();
			IS_Widget.MaximizeWidget = this;
			if( !isBuilt ) {
				this.loadContents();
				
				if( this.isGadget() ) {
					IS_Event.observe( this.iframe,"load",
						IS_Widget.Maximize.defaultAdjustMaximizeHeight.bind( null,this ),false,this.closeId );
				}
			}
			
			if( this.content && this.content.maximize ) {
				this.content.maximize();
			} else if( isBuilt && this.isGadget() ) {
				this.refresh();
			}
			this.keybind.enable = true;
			
			this.title = this.originalWidget.title;
			
			while( this.elm_title.firstChild )
				this.elm_title.removeChild( this.elm_title.firstChild );
			this.elm_title.appendChild( document.createTextNode( this.headerContent.getTitle()) );
			
			this.adjustMaximizeHeight();
			this.adjustMaximizeWidth();
			
			IS_Portal.widgetDisplayUpdated();
			IS_EventDispatcher.newEvent("WidgetMaximize");
		}
		
		this.turnbackMaximize = function() {
			IS_Widget.Maximize.restoreMaximizeView();
			
			this.keybind.enable = false;
			
			if( this.content && this.content.turnbackMaximize ) {
				this.content.turnbackMaximize();
			} else {
				this.originalWidget.refresh();
			}
			
			//Refresh immediately if automatic refresh occurs in maximized window
			IS_Portal.refresh.cancel();
			IS_Portal.refresh.resume();
		}
		
		this.adjustMaximizeHeight = function() {
			if( this.adjustTimerY ) return;
			this.adjustTimerY = setTimeout( this._adjustMaximizeHeight.bind( this ),500 );
		}
		this._adjustMaximizeHeight = function() {
			this.adjustTimerY = undefined;
			if( this.elm_widget ) {
				var height = getWindowHeight() -findPosY( this.elm_widget ) -( Browser.isIE ? 5:60 );
				if( !isNaN( height ) && height >= 0 )
					this.elm_widget.style.height = height + 'px';
			}
			
			if( this.content && this.content.adjustMaximizeHeight ) {
				this.content.adjustMaximizeHeight();
			} else if( this.isGadget() ) {
				IS_Widget.Maximize.defaultAdjustMaximizeHeight( this );
			}
		}
		this.adjustMaximizeWidth = function() {
			if( this.adjustTimerX ) return;
			this.adjustTimerX = setTimeout( this._adjustMaximizeWidth.bind( this ),500 );
		}
		this._adjustMaximizeWidth = function() {
			this.adjustTimerX = undefined;
			if( this.content && this.content.adjustMaximizeWidth )
				this.content.adjustMaximizeWidth();
		}
		this.handleUserAction = function() {
			// Reset Timer for every operation; Restart automatic updating if no operation occurs for five minutes
			if( this.autoReloadResumeTimeout )
				clearTimeout( this.autoReloadResumeTimeout );
			
			if( !this.resumeStart )
				this.resumeStart = new Date().getTime();
			
			IS_Portal.refresh.cancel();
			clearTimeout( IS_Portal.refresh.timer );
			
			var interval = 5 *60 *1000;
			this.autoReloadResumeTimeout = setTimeout( this.restartAutoRefresh.bind( this ),interval );
		}
		this.restartAutoRefresh = function() {
			if( this.autoReloadResumeTimeout )
				clearTimeout( this.autoReloadResumeTimeout );
			
			var resumeTime = new Date().getTime() -this.resumeStart;
			var interval = ( refreshInterval *60 *1000 ) -resumeTime;
			if( interval < 0 ) interval = 0;
			
			this.autoReloadResumeTimeout = setTimeout( IS_Portal.refresh.resume,interval );
			this.resumeStart = false;
		}
	}
	
	return new maximizeWidgetClass( false,maximizeXml );
}
IS_Widget.Maximize.getMaximizeType = function( originalType ) {
	var maximizeType = "__Maximize__"+originalType;
	if( originalType.indexOf("g_") == 0 )
		maximizeType = "g__Maximize__"+originalType.substring(2); 
	
	return maximizeType;
}
IS_Widget.Maximize.disableIconTypes = ["close","minimize","turnBack","systemIconMinimize","turnbackMinimize",
	"maximize","edit"];
IS_Widget.Maximize.getMaximizeConfiguration = function( widgetConf ) {
	var maximizeType = IS_Widget.Maximize.getMaximizeType( widgetConf.type );
	
	var maximize = widgetConf.Maximize;
	var maximizeConf = Object.extend( {},widgetConf );
	maximizeConf.type = maximizeType;
	maximizeConf.Content = widgetConf.Maximize.Content || widgetConf.Content;
	
	if( widgetConf.type.indexOf("g_") == 0 )
		maximizeConf.scrolling = "true";
	
	var maximizeHeader = {
		refresh: ( maximize.Header ? maximize.Header.refresh : widgetConf.Header.refresh ),
		icon: ( maximize.Header ? maximize.Header.icon || [] : [] )
	};
	
	if( maximizeHeader.refresh != 'off'){
		maximizeHeader.icon.push({
		  type:	"refresh",
		  imgUrl:	"refresh.gif",
			
		  alt: IS_R.lb_refreshKey
		});
	}
	maximizeHeader.icon.push(
		  {
			type:	"turnbackMaximize",
			imgUrl:	"turnback.gif",

			alt: IS_R.lb_turnback
		  });
	
	maximizeConf.Header = maximizeHeader;
	
	var disableTypes = IS_Widget.Maximize.disableIconTypes;
	maximizeHeader.icon = maximizeHeader.icon.reject( function( icon,index ) {
		return !( disableTypes.indexOf( icon.type ) < 0 );
	});
	
	return maximizeConf;
}

IS_Widget.Maximize.setupMaximizeView = function() {
	IS_Widget.RssReader.RssItemRender.hideRssDesc();
	
	var panels = [$("tab-container"),$("panel"+IS_Portal.currentTabId.substring(3))];
	
	for( var i=0;i<panels.length;i++ ) {
		var panel = panels[i];
		if( panel ) {
	    	panel.style.display = "none";
	    	panel.style.visibility = "hidden";
		}
	}
	//$("panels").style.display = "none";
	
	$("maximize-panel").style.display = "block";
	
	setTimeout( IS_Widget.Maximize.adjustMaximizeHeight.bind( IS_Widget.Maximize ),100);
	setTimeout( IS_Widget.Maximize.adjustMaximizeWidth.bind( IS_Widget.Maximize ),100);
    
    scrollTo(0, 0);
}
IS_Widget.Maximize.restoreMaximizeView = function() {
	var maximizePanel = $("maximize-panel");
	if( maximizePanel.firstChild )
		maximizePanel.removeChild( maximizePanel.firstChild );
	
	maximizePanel.style.display = "none";
	
	var panels = [$("tab-container"),$("panel"+IS_Portal.currentTabId.substring(3))];
	
	for( var i=0;i<panels.length;i++ ) {
		var panel = panels[i];
		if( panel ) {
	    	panel.style.display = "block";
	    	panel.style.visibility = "visible";
		}
	}
	//$("panels").style.display = "";
	
//	IS_Portal.setFontSize();
	IS_Portal.adjustIS_PortalStyle();
	
	IS_Widget.adjustDescWidth();
	IS_Widget.Information2.adjustDescWidth();
	
	IS_Widget.WidgetHeader.adjustHeaderWidth();
	
	IS_Widget.MaximizeWidget = null;
}

//For Safari: Not process descriptions except for the current tub
//Set handler that replaces currentTabId
if( Browser.isSafari1 ) {
	IS_Widget.Maximize.setupMaximizeView = ( function() {
		var setup = IS_Widget.Maximize.setupMaximizeView;
		
		return function() {
			IS_Portal.disableCommandBar();
			setup.apply( IS_Widget.Maximize,$A( arguments ));
			IS_Portal.currentTabId = "_"+IS_Portal.currentTabId;
		}
	})();
	IS_Widget.Maximize.restoreMaximizeView = ( function() {
		var restore = IS_Widget.Maximize.restoreMaximizeView;
		
		return function() {
			IS_Portal.currentTabId = IS_Portal.currentTabId.substring(1);
			restore.apply( IS_Widget.Maximize,$A( arguments ));
			IS_Portal.enableCommandBar();
			
			IS_Portal.adjustCurrentTabSize();
		}
	})();
}

IS_Widget.MaximizeWidget = null;
IS_Widget.Maximize.adjustMaximizeHeight = function() {
	var maximizeWidget = IS_Widget.MaximizeWidget;
	
	if( maximizeWidget && maximizeWidget.adjustMaximizeHeight )
		maximizeWidget.adjustMaximizeHeight();
}
IS_Widget.Maximize.defaultAdjustMaximizeHeight = function( widget ) {
	var height = getWindowSize(false) - findPosY( widget.elm_widgetContent ) -25;
	if( !isNaN( height ) && height >= 0 )
		widget.iframe.style.height = height + 'px';
}
IS_Widget.Maximize.adjustMaximizeWidth = function() {
	var maximizeWidget = IS_Widget.MaximizeWidget;
	
	if( maximizeWidget && maximizeWidget.adjustMaximizeWidth )
		maximizeWidget.adjustMaximizeWidth();
}
IS_Widget.Maximize.adjustMaximizeSize = function() {
	if( !IS_Widget.MaximizeWidget )
		return;
	
	IS_Widget.Maximize.adjustMaximizeHeight();
	IS_Widget.Maximize.adjustMaximizeWidth();
}

Event.observe(window, 'resize', IS_Widget.Maximize.adjustMaximizeSize, false);
IS_EventDispatcher.addListener("adjustedMessageBar","",IS_Widget.Maximize.adjustMaximizeSize );

IS_Widget.Maximize.Keybind = function( widgetObj ) {
	this.widget = widgetObj;
	this.keyFuncArray = [];
	this.enable = false;
}
IS_Widget.Maximize.Keybind.prototype.processKeyEvent = function(e) {
	if( !this.enable || !this.widget.enableEvent)
		return;
	
	var eventNode = Event.element(e);
	if(eventNode &&
		(eventNode.nodeName == "INPUT" || eventNode.nodeName == "TEXTAREA" ))
		return;
	
	var keyEvent = {
		source:	this.widget,
		element:	Event.element(e),
		DOMEvent:	e
	};
	// Mozilla(Firefox, NN) and Opera 
	if (!Browser.isIE) { 
		keyEvent.keycode = e.which; 
		keyEvent.ctrl = typeof e.modifiers == 'undefined' ? e.ctrlKey : e.modifiers & Event.CONTROL_MASK; 
		keyEvent.shift = typeof e.modifiers == 'undefined' ? e.shiftKey : e.modifiers & Event.SHIFT_MASK; 
	} else { 
		keyEvent.keycode = event.keyCode; 
		keyEvent.ctrl = event.ctrlKey; 
		keyEvent.shift = event.shiftKey; 
	} 
	// Get string of key code 
	keyEvent.keychar = String.fromCharCode(keyEvent.keycode).toUpperCase(); 
	
	var execFunc = this.keyFuncArray[keyEvent.keycode];
	//	console.info( keyEvent.keycode+"/"+keyEvent.keychar)//+" : "+execFunc );
	if( !execFunc )
		return;
	
	IS_Event.stop(e);
	
	execFunc.apply( this,[keyEvent]);
}
