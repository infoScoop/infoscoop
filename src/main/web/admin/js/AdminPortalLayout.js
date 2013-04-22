var ISA_PortalLayout = IS_Class.create();

ISA_PortalLayout.portalLayout = false;
ISA_PortalLayout.portalLayoutList = false;

// Called by returned value from server
ISA_PortalLayout.setPortalLayouts = function(_layoutList) {
	ISA_PortalLayout.portalLayoutList = _layoutList;
}

ISA_PortalLayout.prototype.classDef = function() {
	var self = this;
	var container;
	var loadingMessage;
	var controlModal;		// Apply Change dialog
	
	this.initialize = function() {
		container = document.getElementById("portalLayout");
		var len = container.childNodes.length;
		for(var i = 0; i < len; i++) {
			container.removeChild(container.lastChild);
		}
		
		loadingMessage = document.createElement('div');
		loadingMessage.innerHTML = "Loading...";
		loadingMessage.style.clear = "both";
		loadingMessage.style.cssFloat = "left";
		container.appendChild(loadingMessage);
	};
	
	this.displayPortalLayout = function() {
		// Layout ID currently displayed
		this.displayLayoutId = false;
		
		var portalLayoutDiv = document.createElement("div");
		
		var refreshAllDiv = document.createElement("div");
		refreshAllDiv.className = "refreshAll";
		refreshAllDiv.style.textAlign = "right";
		
		var commitDiv = ISA_Admin.createIconButton(ISA_R.alb_changeApply, ISA_R.alb_changeApply, "database_save.gif", "right");
		refreshAllDiv.appendChild(commitDiv);
		IS_Event.observe(commitDiv, 'click', this.commitPortalLayout.bind(this), "_adminPortal");
		
		var refreshDiv = ISA_Admin.createIconButton(ISA_R.alb_refresh, ISA_R.alb_reloadWithourSaving, "refresh.gif", "right");
		refreshAllDiv.appendChild(refreshDiv);
		IS_Event.observe(refreshDiv, 'click', function(){
			if( !ISA_Admin.checkUpdated() )
				return;
			
			ISA_Admin.isUpdate = false;
			ISA_PortalLayout.portalLayout = new ISA_PortalLayout();
			ISA_PortalLayout.portalLayout.build();
		}, false, "_adminPortal");
		
		portalLayoutDiv.appendChild(refreshAllDiv);
		portalLayoutDiv.appendChild(self.buildPortalLayouts());
		
		container.replaceChild(portalLayoutDiv, loadingMessage);
		
		// The first item is selected at the initial display
		for(var i in ISA_PortalLayout.portalLayoutList) {
			if( (ISA_PortalLayout.portalLayoutList[i] instanceof Function) ) continue;
			
			this.displayLayoutId = i;
			break;
		}
		this.changeLayout();
	}
	
	this.buildPortalLayouts = function() {
		var portalLayoutsDiv = document.createElement("div");
		portalLayoutsDiv.id = "portalLayouts";
		
		var portalLayoutsTable = document.createElement("table");
		portalLayoutsTable.id = "portalLayoutsTable";
		portalLayoutsTable.style.width = "100%";
		portalLayoutsTable.style.tableLayout = "fixed"
		
		var portalLayoutsTbody = document.createElement("tbody");
		portalLayoutsTable.appendChild(portalLayoutsTbody);
		
		var portalLayoutsTr = document.createElement("tr");
		portalLayoutsTbody.appendChild(portalLayoutsTr);
		
		var portalLayoutsTdLeft = document.createElement("td");
		portalLayoutsTdLeft.id = "layoutListTd";
		portalLayoutsTdLeft.style.width = "200px";
		portalLayoutsTdLeft.style.verticalAlign = "top";
		portalLayoutsTr.appendChild(portalLayoutsTdLeft);
		
		var portalLayoutsTdRight = document.createElement("td");
		portalLayoutsTdRight.id = "layoutEditTd";
//		portalLayoutsTdRight.style.width = "70%";
		portalLayoutsTdRight.style.verticalAlign = "top";
		portalLayoutsTr.appendChild(portalLayoutsTdRight);
		
		var listDiv = this.buildLayoutList();
		portalLayoutsTdLeft.appendChild(listDiv);
		
		var editDiv = this.buildEditArea();
		portalLayoutsTdRight.appendChild(editDiv);
		
		portalLayoutsDiv.appendChild(portalLayoutsTable);
		
		return portalLayoutsDiv;
	}
	
	/**
		Create layout list
	*/
	this.buildLayoutList = function() {
		var layoutListDiv = document.createElement("div");
		
		var layoutGroupDiv = document.createElement("div");
		layoutGroupDiv.id = "layoutGroup";
		layoutListDiv.appendChild(layoutGroupDiv);
		
		for(var i in ISA_PortalLayout.portalLayoutList) {
			if( (ISA_PortalLayout.portalLayoutList[i] instanceof Function) ) continue;
			
			var div = this.buildLayout(i);
			layoutGroupDiv.appendChild(div);
		}
		
		return layoutListDiv;
	}
	
	/**
		Create layout items
	*/
	this.buildLayout = function(layoutId) {
		var jsonLayout = ISA_PortalLayout.portalLayoutList[layoutId];
		var layoutDiv = document.createElement("div");
		layoutDiv.id = "layout_" + jsonLayout.name;
		
		var table = document.createElement("table");
		table.className = "portalLayoutTable";
		layoutDiv.appendChild(table);
		
		var tbody = document.createElement("tbody");
		table.appendChild(tbody);
		
		var tr = document.createElement("tr");
		tr.className = "portalLayoutTr";
		tbody.appendChild(tr);
		
		var layoutNameTd = document.createElement("td");
		layoutNameTd.className = "portalLayoutTd";
		layoutNameTd.style.width = "100%";
		tr.appendChild(layoutNameTd);
		var layoutNameDiv = document.createElement("div");
		layoutNameTd.appendChild(layoutNameDiv);
		layoutNameDiv.appendChild(document.createTextNode(jsonLayout.name));
		
		var changeLayoutHandler = function(e){
			self.displayLayoutId = layoutId;
			self.changeLayout();
		};
		IS_Event.observe(layoutNameTd, "click", changeLayoutHandler, false, "_adminPortal");
		
		return layoutDiv;
	}
	
	/**
		Switch layout
	*/
	this.changeLayout = function() {
		var jsonLayout = ISA_PortalLayout.portalLayoutList[this.displayLayoutId];
		
		this.buildEditArea();
		
		var layoutElement = document.getElementById("portalLayoutTextarea");
		if(layoutElement)
			layoutElement.value = ISA_Admin.replaceUndefinedValue(jsonLayout.layout);
		
		this.selectLayout();
	}
	
	/**
		Select edit
	*/
	var currentSelectedLayout = false;
	this.selectLayout = function() {
		if(currentSelectedLayout)
			currentSelectedLayout.className = "";
		currentSelectedLayout = document.getElementById("layout_" + this.displayLayoutId);
		currentSelectedLayout.className = "portalLayoutSelected";
	}
	
	/**
		Create edit area
	*/
	var displayEditAreaDiv = false;
	this.buildEditArea = function() {
		if(!displayEditAreaDiv)
			displayEditAreaDiv = document.createElement("div");
		
		if(!displayEditAreaDiv.firstChild)
			displayEditAreaDiv.appendChild(this.buildEditLayout());
		else
			displayEditAreaDiv.replaceChild(this.buildEditLayout(), displayEditAreaDiv.firstChild);
		
		return displayEditAreaDiv;
	}
	
	/**
		Create edit setting area
	*/
	this.buildEditLayout = function() {
		var layoutName = "";
		if(this.displayLayoutId)
			layoutName = ISA_PortalLayout.portalLayoutList[this.displayLayoutId].name;
		
		var editLayoutDiv = document.createElement("div");
		
		var fieldset = document.createElement("div");
		fieldset.className = "modalConfigSet";
		editLayoutDiv.appendChild(fieldset);
		
		var legend = document.createElement("p");
		legend.className = "modalConfigSetHeader";
		fieldset.appendChild(legend);
		legend.appendChild(document.createTextNode(ISA_R.alb_editSettings));
		
		var editLayoutTextareaDiv = document.createElement("div");
		fieldset.appendChild(editLayoutTextareaDiv);
		
		var editLayoutTextarea;
		switch (String(layoutName).toLowerCase()) {
			case "title":
				editLayoutTextarea = document.createElement("input");
				editLayoutTextarea.style.width = "99%";
				editLayoutTextarea.style.margin = "5px";
				editLayoutTextarea.type = "text";
				break;
			default:
				editLayoutTextarea = document.createElement("textarea");
				editLayoutTextarea.rows = "20";
				editLayoutTextarea.style.width = "99%";
				editLayoutTextarea.style.margin = "5px";
				editLayoutTextarea.setAttribute('wrap', 'off');
				break;
		}
		editLayoutTextarea.id = "portalLayoutTextarea";
		editLayoutTextareaDiv.appendChild(editLayoutTextarea);
		var changeLayoutHandler = function(e) {
			ISA_Admin.isUpdated = true;
			ISA_PortalLayout.portalLayoutList[self.displayLayoutId].layout = editLayoutTextarea.value;
		};
		IS_Event.observe(editLayoutTextarea, 'change', changeLayoutHandler, false, "_adminPortal");
		
		return editLayoutDiv;
	}
	
	this.commitPortalLayout = function() {
		var portalLayouts = {};
		for(var i in ISA_PortalLayout.portalLayoutList) {
			if( (ISA_PortalLayout.portalLayoutList[i] instanceof Function) ) continue;
			// Obtain value
			var layout = ISA_Admin.trim(ISA_PortalLayout.portalLayoutList[i].layout);
			// Check with input

			// PostData
			var name = ISA_PortalLayout.portalLayoutList[i].name;
			ISA_Admin.isUpdated = false;
			portalLayouts[ name ] = layout;
		}
		
		// Update to DB
		if(!controlModal){
			controlModal = new Control.Modal('',{
				className:"commitDialog",
				closeOnClick:false
			});		
		}
		controlModal.container.update(ISA_R.ams_applyingChanges);
		controlModal.open();
		this.updatePortalLayout(portalLayouts);
	}
	
	this.updatePortalLayout = function(portalLayouts) {
		var url = adminHostPrefix + "/services/portalLayout/updatePortalLayout";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			postBody: Object.toJSON([portalLayouts]),
			asynchronous:true,
			onSuccess: function(response){
				controlModal.container.update(ISA_R.ams_changeUpdated);
			},
			onFailure: function(t) {
				var errormsg = t.responseText && typeof t.responseText == "string" ? t.responseText.substr(0, 100) : "";
				alert(ISA_R.ams_failedUpdateOtherPotal+'\n' + errormsg );
				msg.error(ISA_R.ams_failedUpdateOtherPotal + t.status + " - " +" | " + t.statusText + errormsg );
			},
			onException: function(r, t){
				alert(ISA_R.ams_failedUpdateOtherPotal);
				msg.error(ISA_R.ams_failedUpdateOtherPotal + getErrorMessage(t));
			},
			onComplete: function(){
				setTimeout(function(){
					Control.Modal.close();
				},500);
			}
		};
		AjaxRequest.invoke(url, opt);
	}
	
	this.build = function() {
		var url = adminHostPrefix + "/services/portalLayout/getPortalLayoutJson";
		var opt = {
			method: 'get' ,
			asynchronous:true,
			onSuccess: function(response){
				eval(response.responseText);
				self.displayPortalLayout();
			},
			onFailure: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedToloadPortalLayout+"</span>";
				msg.error(ISA_R.ams_failedToloadPortalLayout + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedToloadPortalLayout+"</span>";
				msg.error(ISA_R.ams_failedToloadPortalLayout + getErrorMessage(t));
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

};
