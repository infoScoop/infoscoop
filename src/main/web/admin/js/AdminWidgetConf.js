var ISA_WidgetConf = IS_Class.create();

ISA_WidgetConf.widgetConf = false;
ISA_WidgetConf.widgetConfList = false;
ISA_WidgetConf.gadgetList = false;

ISA_WidgetConf.prototype.classDef = function() {
	var self = this;
	var container;
	var loadingMessage;
	var uploadedGadgetType = false;
	
	this.initialize = function() {
		this.id = "_adminWidgetConf";
	};
	
	this.displayWidgets = function() {
		
		var widgetConfDiv = document.createElement("div");
		widgetConfDiv.id = "adminWidgetConfContent";

		var widgetConfMenuDiv = document.createElement("div");
		var addGadget = ISA_Admin.createIconButton(ISA_R.alb_addGadget, ISA_R.alb_addGadget, "add.gif", "right");
		widgetConfMenuDiv.appendChild(addGadget);
		widgetConfDiv.appendChild( widgetConfMenuDiv );

		widgetConfDiv.appendChild( self.buildWidgetConfContainer() );
		IS_Event.observe(addGadget, 'click', function(){
			if(!ISA_Admin.checkUpdated())return;
			this.rebuildGadgetUploadPanel();
			this.editUpdateButton.style.display = this.editDeleteButton.style.display = this.editOKButton.style.display = this.editCancelButton.style.display = "none";
			Element.hide('widgetConfEditPanel');
			Element.show('widgetConfGadgetUpload');
		}.bind(this), false, "_adminMenu");

		container.replaceChild(widgetConfDiv,loadingMessage);
	}
	
	/**
		Make Contents
	*/
	this.buildWidgetConfContainer = function(){
		
		var containerDiv = document.createElement("div");
		containerDiv.style.clear = "both";
		
		var table = document.createElement("table");
		table.id = "adminWidgetConfContentTable";
		table.tableLayout = "fixed";
		//table.style.border = "1px solid #555555";
		containerDiv.appendChild( table );
		var tbody = document.createElement("tbody");
		table.appendChild( tbody );
		
		var editWidgetsTr = document.createElement("tr");
		tbody.appendChild( editWidgetsTr );
		var editWidgetsTd = document.createElement("td");
		editWidgetsTd.style.width = "280px";
		editWidgetsTd.style.height = '100%';
		editWidgetsTd.style.verticalAlign = "top";

		this.widgetConfPanel = document.createElement("div");
		this.widgetConfPanel.id = "widgetList_div";
		this.widgetConfPanel.className ="gadgetListDiv";
		
		this.gadgetPanel = document.createElement("div");
		this.gadgetPanel.id = "gadgetList_div";
		this.gadgetPanel.className ="gadgetListDiv";
		
		// start create accordion
		var gadgetAcc = document.createElement("ul");
		gadgetAcc.id = "gadgetAcc";
		gadgetAcc.className = "acc";
		
		var accHeader1 = document.createElement("li");
		accHeader1.className = "accHeader"
		
		var gadgetListLabel1 = document.createElement("div");
		gadgetListLabel1.id = "gadgetListLabel";
		gadgetListLabel1.className = "opened";
		
		var gadgetListLabelText1 = document.createElement("a");
		gadgetListLabelText1.appendChild(document.createTextNode(ISA_R.alb_gadgetsList));
		
		var gadgetList = document.createElement("div");
		gadgetList.id = "gadgetList";
		gadgetList.className = "accContent";
		
		accHeader1.appendChild(gadgetListLabel1);
		gadgetListLabel1.appendChild(gadgetListLabelText1);
		accHeader1.appendChild(gadgetList);
		
		var accHeader2 = document.createElement("li");
		accHeader2.className = "accHeader"
		
		var gadgetListLabel2 = document.createElement("div");
		gadgetListLabel2.id = "gadgetListLabel";
		gadgetListLabel2.className = "closed";
		Element.setStyle(gadgetListLabel2, { 
			top: -1,
			position: "relative"
		});
		
		var gadgetListLabelText2 = document.createElement("a");
		gadgetListLabelText2.appendChild(document.createTextNode(ISA_R.ams_widgetList));
		var buildInGadgetList = document.createElement("div");
		buildInGadgetList.id = "buildInGadgetList";
		buildInGadgetList.className = "accContent";
		buildInGadgetList.style.display = "none";
		
		accHeader2.appendChild(gadgetListLabel2);
		gadgetListLabel2.appendChild(gadgetListLabelText2);
		accHeader2.appendChild(buildInGadgetList);
		
		gadgetAcc.appendChild(accHeader1);
		gadgetAcc.appendChild(accHeader2);
		
		gadgetList.appendChild(this.gadgetPanel);
		buildInGadgetList.appendChild(this.widgetConfPanel);
		// end create accordion
		
		editWidgetsTd.appendChild(gadgetAcc);
		
		editWidgetsTr.appendChild( editWidgetsTd );
		var displayEditTd = document.createElement("td");
		displayEditTd.style.verticalAlign = "top";
		displayEditTd.appendChild( self._buildEditWidgetPanel() );
		editWidgetsTr.appendChild( displayEditTd );
		
		displayEditTd.appendChild( self._buildGadgetUploadPanel() );
		
		return containerDiv;
	};
	
	function sortConf(a, b){
		if( !b.type ) return -1;
		if( !a.type ) return 1;
		if( a.type >= b.type ) return 1;
		if( a.type < b.type ) return -1;
	}
	
	this.buildWidgetConfItems = function() {
		var widgetConfList = [];
		for(var i in ISA_WidgetConf.widgetConfList){
			if( !(ISA_WidgetConf.widgetConfList[i] instanceof Function) ){
				var widgetConf = ISA_WidgetConf.widgetConfList[i];
				// Show except for maximize and notAvailable
				if( !(/true/i.test(widgetConf.systemWidget)) ){
					if(!widgetConf.type)widgetConf.type = i;//Gadget has no type
					widgetConfList.push( widgetConf );
				}
			}
		}
		widgetConfList.sort(sortConf);
		
		while( self.widgetConfPanel.firstChild )
			self.widgetConfPanel.removeChild( self.widgetConfPanel.firstChild );
		
		for(var i = 0; i < widgetConfList.length; i++){
			self.widgetConfPanel.appendChild(self._buildWidgetConfToPanel( widgetConfList[i] ) );
		}

	}
	this.buildGadgetItems = function() {
		var widgetConfList = [];
		for(var i in ISA_WidgetConf.gadgetList){
			if( !(ISA_WidgetConf.gadgetList[i] instanceof Function) ){
				var widgetConf = ISA_WidgetConf.gadgetList[i];
				// Show except for maximize and notAvailable
				if( !(/true/i.test(widgetConf.systemWidget)) ){
					if(!widgetConf.type)widgetConf.type = i;//Gadget has no type
					widgetConfList.push( widgetConf );
				}
			}
		}
		
		while( self.gadgetPanel.firstChild )
			self.gadgetPanel.removeChild( self.gadgetPanel.firstChild );
		
		for(var i = 0; i < widgetConfList.length; i++){
			self.gadgetPanel.appendChild(self._buildWidgetConfToPanel( widgetConfList[i] ));
		}
	}
	
	this._buildWidgetConfToPanel = function( conf ) {
		var title;
		var isGadget = false;
		if(conf.ModulePrefs){
			isGadget = true;
			title = conf.ModulePrefs.directory_title || conf.ModulePrefs.title || conf.type;
		}
		else
		  title = (conf.title && 0 < conf.title.length)? conf.title : conf.type;
		
		var itemDiv = document.createElement("div");
		itemDiv.id = "editWidgetConfItem_"+conf.type;
		IS_Event.observe(itemDiv, 'mouseover', function(){
			if(this.className != "selectedEditConf" && this.className != "selectEditConf")
				Element.addClassName(this, "selectEditConf");
		}.bind(itemDiv), false, this.id);
		IS_Event.observe(itemDiv, 'mouseout', function(){
			if(this.className != "selectedEditConf")
				Element.removeClassName(this, "selectEditConf");
		}.bind(itemDiv), false, this.id);
		
		var itemTable = document.createElement("table");
//		itemTable.className = "editConfListTable";
		itemTable.cellSpacing = "0";
		itemTable.cellPadding = "0";
		itemDiv.appendChild( itemTable );
		var tbody = document.createElement("tbody");
		itemTable.appendChild( tbody );
		
		var tr = document.createElement("tr");
//		tr.className = "editConfListTr";
		
		var widgetNameTd = document.createElement("td");
		widgetNameTd.className = "editConfListTitleTd";
		var widgetNameSpan = document.createElement("span");
		widgetNameTd.appendChild( widgetNameSpan );
		
		var nameText = document.createElement("a");
		nameText.href = "javascript:void(0);";
		nameText.id = "editConf_"+conf.type+"_title";
		nameText.className = "editConfListTitleText";
		nameText.title = title;
		nameText.appendChild( document.createTextNode(title) );
		widgetNameSpan.appendChild( nameText );
		tr.appendChild( widgetNameTd );

		tbody.appendChild( tr );
		
		// Add Events
//		IS_Event.observe(widgetNameTd, 'click', this.displayEditWidgetConf.bind(this, conf.type), false, this.id);
		IS_Event.observe(itemDiv, 'click', this.displayEditWidgetConf.bind(this, conf.type), false, this.id);
		return itemDiv;
	}
	
	
	this._buildEditWidgetPanel = function() {
		var content = document.createElement("div");
		content.id = "widgetConfEditPanel";
		content.style.padding = "5px";
		this.widgetEditWidgetPanel = content;
		
		var editPanel = document.createElement("div");
		this.editConfContent = editPanel;
		editPanel.innerHTML = ISA_R.alb_setWidgetDefailt+"<br>"+ISA_R.alb_selectWidgetToSet;
		content.appendChild( editPanel );
		
		var buttonDiv = document.createElement("div");
		buttonDiv.style.textAlign = "center";
		content.appendChild( buttonDiv );
		
		
		var okButton = document.createElement("input");
		okButton.type = "button";
		okButton.value = ISA_R.alb_saveSettings;
		okButton.style.display = "none";
		this.editOKButton = okButton;
		buttonDiv.appendChild( okButton );
		var cancelButton = document.createElement("input");
		cancelButton.type = "button";
		cancelButton.value = ISA_R.alb_cancel;
		cancelButton.style.display = "none";
		this.editCancelButton = cancelButton;
		buttonDiv.appendChild( cancelButton );
		
		buttonDiv.appendChild( document.createTextNode(' ') );
		
		var updateButton = document.createElement("input");
		updateButton.type = "button";
		updateButton.value = ISA_R.alb_updateGadget;
		updateButton.style.display = "none";
		this.editUpdateButton = updateButton;
		buttonDiv.appendChild( updateButton );
		
		var deleteButton = document.createElement("input");
		deleteButton.type = "button";
		deleteButton.value = ISA_R.alb_deleteGadget;
		deleteButton.style.display = "none";
		this.editDeleteButton = deleteButton;
		buttonDiv.appendChild( deleteButton );
		
		IS_Event.observe( updateButton,"click",this._updateGadget.bind(this), false, this.id);
		IS_Event.observe( deleteButton, 'click', this._deleteGadget.bind(this), false, this.id);
		IS_Event.observe( okButton, 'click', this.saveEditWidgetConf.bind(this), false, this.id);
		IS_Event.observe( cancelButton, 'click', this.cancelEditWidgetConf.bind(this), false, this.id);
		
		return content;
	}
	
	this._buildGadgetUploadPanel = function(){
		var gadgetUploadFieldSet = document.createElement('div');
		gadgetUploadFieldSet.id = "widgetConfGadgetUpload";
		gadgetUploadFieldSet.style.display = 'none';
		
		return gadgetUploadFieldSet;
	}
	
	this.rebuildGadgetUploadPanel = function( type ){
		IS_Event.unloadCache(this.id + "_gadget");
		var gadgetUpload = $('widgetConfGadgetUpload');
		while( gadgetUpload.firstChild )
			gadgetUpload.removeChild( gadgetUpload.firstChild );
		
		gadgetUpload.appendChild( ISA_Admin.buildInputBundleForm( type,this ));
		//self.widgetConfPanel.appendChild(gadgetUploadFieldSet);
	}
	
	this.displayEditWidgetConf = function(type, e) {
		Element.show('widgetConfEditPanel');
		Element.hide('widgetConfGadgetUpload');
		
		if(!this.editConfContent) return;
		this.resetEditContent();
		
		var widgetConf = ISA_WidgetConf.widgetConfList[type];
		var isGadget = false;
		if(!widgetConf){
			isGadget = true;
			widgetConf = ISA_WidgetConf.gadgetList[type];
			if(!widgetConf)
			  return;
		}
		
		this.selectWidgetConf(type);
		
		var isBuiltEdit = ISA_WidgetConf.EditWidgetConf.render( this.editConfContent, type, widgetConf);
		
		this.editWidgetType = String( type );
		this.editConfContent.style.display = "";
		if(isGadget) {
			$("gadgetList").show();
			this.editUpdateButton.style.display = this.editDeleteButton.style.display = "";
		}
		if(isBuiltEdit){
			this.editOKButton.style.display = "";
			this.editCancelButton.style.display = "";
		}
		if(e) Event.stop(e);
	}
	
	/**
		Highlight listed items whose type are specified
	*/
	this.selectWidgetConf = function(type){
		this.clearSelectWidgetConf();
		
		var widgetConf = this._getConf(type);
		if(!widgetConf) return;
		
		var editConfItem = $("editWidgetConfItem_"+type);
		if(editConfItem){
			this.selectedConfItem = editConfItem;
			editConfItem.className = "selectedEditConf";
		}
	}
	
	/**
		Remove Highlighting of listed items
	*/
	this.clearSelectWidgetConf = function(){
		if(this.selectedConfItem){
			this.selectedConfItem.className = "";
			this.selectedConfItem = null;
		}
	}
	
	this._updateGadget = function(){
		if(!ISA_Admin.checkUpdated())
			return;
		
		this.rebuildGadgetUploadPanel( this.editWidgetType );
		Element.hide('widgetConfEditPanel');
		Element.show('widgetConfGadgetUpload');
	};
	this._deleteGadget = function(type){
//		if( !confirm("Is it OK to deleting the gadget?"))
		if( !confirm( ISA_R.ams_deleteGadgetConfirm ))
			return;
		
		var url = adminHostPrefix + "/services/gadget/deleteGadget";
		var opt = {
		  method: 'post',
		  contentType: "application/json",
		  postBody: Object.toJSON([ String( this.editWidgetType ) ]),
		  asynchronous:true,
		  onSuccess: function(response){
			  ISA_Admin.isUpdated = false;
			  this.resetEditContent();
			  this.clearSelectWidgetConf();
			  this.editConfContent.style.display = "";
			  this.editConfContent.innerHTML = ISA_R.ams_deleteGadget;
			  this.build();
		  }.bind(this),
		  onFailure: function(t) {
			  alert(ISA_R.ams_failedDeleteGadget );
			  msg.error(ISA_R.ams_failedDeleteGadget + t.status + " - " + t.statusText);
		  }
		}
		AjaxRequest.invoke(url, opt);
	}

	this._getConf = function(type){
		var widgetConf = ISA_WidgetConf.widgetConfList[type];
		if(!widgetConf)
			widgetConf = ISA_WidgetConf.gadgetList[type];
		return widgetConf;
	}
	
	/**
		Save edits of widgetConf
	*/
	this.saveEditWidgetConf = function(){
		if(!this.editWidgetType) return;
		ISA_Admin.isUpdated = false;
		var widgetConf = this._getConf(this.editWidgetType);
		
		ISA_WidgetConf.EditWidgetConf.save( this.editWidgetType, widgetConf, this.displaySaveEditComplete.bind(this), null );
		if(widgetConf.ModulePrefs){
			var titleSpan = $("editConf_"+widgetConf.type+"_title");
			var title = widgetConf.ModulePrefs.directory_title || widgetConf.ModulePrefs.title;
			titleSpan.replaceChild(document.createTextNode(title), titleSpan.firstChild);
		}
	}
	
	/**
		Show messages when the edit of widgetConf is completely saved 
	*/
	this.displaySaveEditComplete = function(){
		if(!this.editConfContent) return;
		
		this.resetEditContent();
		this.clearSelectWidgetConf();
		this.editConfContent.style.display = "";
		this.editConfContent.innerHTML = ISA_R.ams_changeUpdatedPeriod;
	}
	
	/**
		Cancel the edit of widgetConf
	*/
	this.cancelEditWidgetConf = function(){
		ISA_Admin.isUpdated = false;
		this.editWidgetType = null;
		this.resetEditContent();
		this.clearSelectWidgetConf();
	}
	
	/**
		Hide the edit of widgetConf
	*/
	this.clearEditConf = function(){
		IS_Event.unloadCache("editWidgetConf");
		this.editConfContent.style.display = "none";
		this.editConfContent.innerHTML = "";
	}
	
	/**
		Hide the section of whole preview 
	*/
	this.resetEditContent = function(){
		this.clearEditConf();
		this.editWidgetType = null;

		this.editUpdateButton.style.display = this.editDeleteButton.style.display = "none";
		this.editOKButton.style.display = "none";
		this.editCancelButton.style.display = "none";
	}

	this.build = function() {
		container = document.getElementById("widgetConf");
		
		/**
		 * remove trashes if they remain
		 */
		var len = container.childNodes.length;
		for(var i = 0; i < len; i++) {
			container.removeChild(container.lastChild);
		}
		
		loadingMessage = document.createElement('div');
		loadingMessage.innerHTML = "Loading...";
		loadingMessage.style.clear = "both";
		loadingMessage.style.cssFloat = "left";
		container.appendChild(loadingMessage);
		
		self.displayWidgets();
		
		this.buildWidgetConfs();
		this.buildGadgetConfs();

		$jq("#gadgetAcc .accHeader").each(function(idx, li){
			$jq(li).click(function(){
				$jq(".accContent", $jq(this)).toggle();
				var accContent = $jq(".accContent", $jq(this));
				if(accContent.is(":visible")){
					$jq("#gadgetListLabel", $jq(this)).removeClass("closed").addClass("opened");
				}else {
					$jq("#gadgetListLabel", $jq(this)).removeClass("opened").addClass("closed");
				}
			});
		}); 
	}
	
	this.buildWidgetConfs = function() {
		var url = adminHostPrefix + "/services/widgetConf/getWidgetConfJson";
		var opt = {
			method: 'get' ,
			asynchronous:true,
			onSuccess: function(response){
				ISA_WidgetConf.widgetConfList = eval("(" + response.responseText + ")");
				self.buildWidgetConfItems();
			},
			on404: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_widgetNotFound+"</span>";
				msg.error(ISA_R.ams_widgetNotFound + t.status + " - " + t.statusText);
			},
			onFailure: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingWidget+"</span>";
				msg.error(ISA_R.ams_failedLoadingWidget + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingWidget+"</span>";
				msg.error(ISA_R.ams_failedLoadingWidget + getErrorMessage(t));
			},
			onComplete: function(req, obj){
				ISA_Admin.requestComplete = true;
			},
			onRequest: function() {
				ISA_Admin.requestComplete = false;
			}
		};
		AjaxRequest.invoke(url, opt);
	}
	
	this.buildGadgetConfs = function( callback ) {
		var url = adminHostPrefix + "/services/gadget/getGadgetJson";
		var opt = {
		  method: 'get' ,
		  asynchronous:true,
		  onSuccess: function(response){
			  ISA_WidgetConf.gadgetList = eval("(" + response.responseText + ")");
			  self.buildGadgetItems();
			  if( callback )
			  	callback();
		  },
		  on404: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_widgetNotFound+"</span>";
			  msg.error(ISA_R.ams_widgetNotFound + t.status + " - " + t.statusText);
		  },
		  onFailure: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
			  container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingWidget+"</span>";
			  msg.error(ISA_R.ams_failedLoadingWidget + t.status + " - " + t.statusText);
		  },
		  onException: function(r, t){
				if(!container.firstChild) container.appendChild(document.createElement("div"));
			  container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingWidget+"</span>";
			  msg.error(ISA_R.ams_failedLoadingWidget + getErrorMessage(t));
		  },
		  onComplete: function(req, obj){
			  ISA_Admin.requestComplete = true;
		  },
		  onRequest: function() {
			  ISA_Admin.requestComplete = false;
		  }
		};
		AjaxRequest.invoke(url, opt);
	};
}
