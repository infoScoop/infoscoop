gadgets.rpc.register("resize_iframe",function( height, heightAutoCalculated ) {
	var widget = IS_Portal.getWidget( this.mid,this.tid );
	if( widget.authToken != this.t ) return;
	if(widget.isStaticHeight) return;
	
	if( IS_Widget.MaximizeWidget == widget )
		return;
	
	if( !widget.getBoolUserPref("openWidget")) return;
	
	var iframe = document.getElementById("ifrm_"+this.mid );
	
	if( height <= 0 )
		height = 1;
	
	widget.adjustHeightAuto = heightAutoCalculated;
	
	iframe.style.height = height;
});
gadgets.rpc.register("set_pref",function( ifpctok ) {
	if( /^previewWidget/.test( this.mid )) return;
	
	var widget = IS_Portal.getWidget( this.mid,this.tid );
	if( widget.authToken != this.t ) return;
	
	var length = arguments.length || 0, args = new Array(length);
	while (length--) args[length] = arguments[length];
	args.shift();
	
	widget.setUserPrefs.apply( widget,args );
});
gadgets.rpc.register("get_prefs",function() {
	var widget = IS_Portal.getWidget( this.mid,this.tid );
	if( widget.authToken != this.t ) return;
	
	var defaultPrefs = {};
	if( !/^previewWidget/.test( this.mid ) ) {
		var keys = widget.getUserPrefKeys();
		for( var i=0;i<keys.length;i++ ) if( !( keys[i] instanceof Function ))
			defaultPrefs[keys[i]] = widget.getUserPref( keys[i] );
	}
	
	return defaultPrefs;
});
gadgets.rpc.register("set_title",function( title ) {
	var widget = IS_Portal.getWidget( this.mid,this.tid );
	if( widget.authToken != this.t ) return;
	
	title = title.replace(/^\s*(.+)\s*$/,"$1");
	if(title.length > 80)
		title = title.substring(0, 80);
	
	if( title != widget.title ){
		widget.title = title;
		widget.widgetConf.title = widget.title;
		if( widget.headerContent )
			widget.headerContent.buildTitle();
		
		IS_Widget.setWidgetPrefernceCommand(widget, "title", widget.title);
	}
});

gadgets.rpc.register("requestNavigateTo",function( view, opt_params, opt_ownerId ) {
	if( typeof opt_params === "string") {
		if( IS_Portal.isInlineUrl( opt_params )) {
			var aTag = {
				nodeName: "a",
				href: opt_params
			};
			IS_Portal.buildIFrame( aTag );
			
			if( aTag.target == "ifrm")
				return document.getElementById("ifrm").src = opt_params;
		}
		
		return window.open( opt_params );
	}
	
	var widget = IS_Portal.getWidget( this.mid,this.tid );
	if( widget.authToken != this.t ) return;
	widget.view_params = opt_params;
	
	if(/home|profile|preview|dashboard/i.test( view ) && IS_Widget.MaximizeWidget ) {
		widget.turnbackMaximize();
	} else if( /canvas|full_page|popup/i.test( view ) && !IS_Widget.MaximizeWidget ) {
		widget.changeMaximize();
	}
});

gadgets.pubsubrouter.init( function( f ) {
	return f;
});

gadgets.rpc.register("log",function( level,message ) {
	var levels = [false,msg.info,msg.warn,msg.error,msg.debug];
	if( !levels[parseInt(level)] )
		level = 4;
	
	levels[parseInt(level)].apply( msg,[message] );
});

gadgets.rpc.register("is_get_login_uid",function() {
	var widget = IS_Portal.getWidget( this.mid,this.tid );
	if( widget.authToken != this.t ) return;
	
	return is_userId;
});

gadgets.rpc.register("is_add_widget_to_panel",function(type, url, title, href) {
	var widgetId = "w_" + new Date().getTime();
	
	function dropWidget(title, href){
		var properties = {
		  url: url
		};
		var widgetConf = IS_WidgetsContainer.WidgetConfiguration.getConfigurationJSONObject(type, widgetId, 1, title, href, properties);
		var widget = IS_WidgetsContainer.addWidget( IS_Portal.currentTabId, widgetConf );
		IS_Widget.setWidgetLocationCommand(widget);
		IS_Portal.widgetDropped( widget );
	}
	
	if(!title){
		var getTitleFunc = function(response){
			try{
				var _authType = response.getResponseHeader("MSDPortal-AuthType");
			}catch(e){}
			if(_authType){
				msg.error("Authentication failed on try retrieving title.");
				dropWidget("no title");
			}

			try{
				var dataList = eval("("+response.responseText+")");
				var length = dataList.length;
				if(length > 0 && !/true/.test(dataList[0].isError)){
					var title
					  if(dataList[0].title && 0 < dataList[0].title.length){
						  title = (""+dataList[0].title).substring(0,80);
					  }else{
						  title = "no title";
					  }
					var href = dataList[0].href || "";
					if( href.length > 256 )
					  href = is_getTruncatedString( href,1024 );

					dropWidget(title, href);
				}
			}catch(e){msg.error(e)}
			document.getElementById("indicatorMini").style.visibility = "hidden";
		};
		is_processUrlContents(url, getTitleFunc );
	}else{
		dropWidget(title, href);
	}
	return widgetId;
});

gadgets.rpc.register("is_open_portal_iframe",function(url) {
	IS_Portal.openIframe(url);
});

gadgets.rpc.register("is_change_background",function(opt) {
	IS_Portal.theme.changeBackground(opt);
});

gadgets.rpc.register("is_refresh",function() {
	var widget = IS_Portal.getWidget( this.mid,this.tid );
	if( widget.authToken != this.t ) return;
	
	widget.refresh();
});

gadgets.rpc.register("is_set_relay_url",function( relayUrl ) {
	var widget = IS_Portal.getWidget( this.mid,this.tid );
	if( widget.authToken != this.t ) return;
	
	gadgets.rpc.setRelayUrl( widget.iframe.name,relayUrl );
});