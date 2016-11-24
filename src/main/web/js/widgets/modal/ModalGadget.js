IS_Widget.ModalView = null;

//IS_MaximizeWidget = IS_Class.extend( IS_Widget );
IS_Widget.Modal = {}
IS_Widget.Modal.createModalGadget = function( wigetObj ) {
	var widget = wigetObj;
	
	var originalWidget = widget.parent ? widget.parent : widget;
	
	var widgetType = originalWidget.widgetType;
	var widgetConf = IS_WidgetConfiguration[widgetType];
	
	var modalType = IS_Widget.Modal.getModalType( widgetType );
	if( !IS_WidgetConfiguration[ modalType ] )
		IS_WidgetConfiguration[ modalType ] = IS_Widget.Modal.getModalConfiguration( widgetConf );
	
	var modalId = "__Modal__"+originalWidget.id;
	var modalXml = IS_WidgetsContainer.WidgetConfiguration.getConfigurationJSONObject(
		modalType,
		modalId,
		originalWidget.column,
		originalWidget.title,
		originalWidget.title_url,
		{
			url : originalWidget.getUserPref("url")
		});
	
	var modalWidgetClass = IS_Class.extend( IS_Widget );
	
	modalWidgetClass.prototype.classDef = function() {
		this.initialize = function(draggable, widgetsXml, parent) {
			this.tabId = originalWidget.tabId;
			this.baseId = widget.id;
			this.baseWidget = widget;
			this.originalId = originalWidget.id;
			this.originalWidget = originalWidget;
			this.resourceUrl = originalWidget.resourceUrl;
			this.panelType = "Modal";
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
				this._super.loadHtmlIfram.apply( this, [url,"modal"] );
			} else {
				this._super.loadHtmlIfram.apply( {
					iframe: this.iframe,
					widgetType: this.originalWidget.widgetType,
					id: this.id
				});
			}
		}
		
		this.closeModalView = function(){
			if(this.modalDialog)
				$jq(this.modalDialog).dialog("destroy");
			IS_Widget.ModalView = undefined;
		}
		
		this.showModalView = function( baseWidget ) {
			this.baseWidget = baseWidget || this.originalWidget;
			
			this.view_params = this.baseWidget.view_params;
			if(!this.view_params)
				this.view_params = {};
			
			var isBuilt = this.isBuilt;
			if( !isBuilt )
				this.build();
			
			$jq( this.elm_widgetContent ).addClass("modal");
			
			var modalContent = $jq("<div>")
				.append(this.elm_widget)
				.attr({title: IS_Widget.WidgetHeader.getTitle(this)});
			
			this.modalDialog = modalContent.dialog({
				modal: true,
				dialogClass: "modal-view",
				width: this.view_params.width,
				height: this.view_params.height,
				closeText: IS_R.lb_close,
				draggable: false,
				resizable: false,
				zIndex: 10000,
				open: function(){
					// modal close if overlay clicked
					$jq(".ui-widget-overlay").one( "click", function(e){
						this.closeModalView();
					}.bind(this));
				}.bind(this)
			});
			
			IS_EventDispatcher.addListener("loadComplete", this.id, function() {
				var modalHeight = $jq(this.modalDialog).height();
				var headerHeight = $jq(this.elm_widgetHeader).height() -3;
				var height = this.headerContent ? modalHeight - headerHeight : modalHeight;
				if(height <= 0) return;
	            
				if(this.iframe)
					$jq(this.iframe).height(height);
				$jq(this.elm_widgetContent).height(height);
			}.bind(this), null, true);
			
			this.headerContent.applyAllIconStyle();
			
			IS_Widget.ModalView = this;
			
			if( !isBuilt ) {
				this.loadContents();
			}
			if( isBuilt && this.isGadget() ) {
				this.refresh();
			}
			
			this.title = this.originalWidget.title;
			
			IS_EventDispatcher.newEvent("WidgetModal");
		}
	}
	
	return new modalWidgetClass( false, modalXml );
}

IS_Widget.Modal.getModalType = function( originalType ) {
	var maximizeType = "__Modal__"+originalType;
	if( originalType.indexOf("g_") == 0 )
		maximizeType = "g__Modal__"+originalType.substring(2); 
	
	return maximizeType;
}

IS_Widget.Modal.disableIconTypes = ["close","minimize","turnBack","systemIconMinimize","turnbackMinimize", "maximize"];

IS_Widget.Modal.getModalConfiguration = function( widgetConf ) {
	var modalType = IS_Widget.Modal.getModalType( widgetConf.type );
	
	var modalConf = Object.extend( {},widgetConf );
	modalConf.type = modalType;
	modalConf.Content = widgetConf.Content;
	
	var modalHeader = {
		refresh: widgetConf.Header.refresh,
		icon: []
	};
	
	modalHeader.icon.push({
		type:  "edit",
	    imgUrl: "edit.png",
	    alt: IS_R.lb_setting
	});

	if( modalHeader.refresh != 'off'){
		modalHeader.icon.push({
		  type:	"refresh",
		  imgUrl:	"refresh.png",
		  alt: IS_R.lb_refreshKey
		});
	}
	
	modalHeader.icon.push({
			type:	"closeModalView",
			imgUrl:	"times-circle.png",
			alt: IS_R.lb_close
		  });
	
	modalConf.Header = modalHeader;
	
	var disableTypes = IS_Widget.Modal.disableIconTypes;
	modalHeader.icon = modalHeader.icon.reject( function( icon,index ) {
		return !( disableTypes.indexOf( icon.type ) < 0 );
	});
	
	return modalConf;
}
