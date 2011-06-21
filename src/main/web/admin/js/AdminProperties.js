var ISA_Properties = IS_Class.create();

ISA_Properties.properties = false;
ISA_Properties.propertiesList = false;

// Called by the value returned from server
ISA_Properties.setProperties = function(_propertiesList) {
	ISA_Properties.propertiesList = _propertiesList;
}

ISA_Properties.prototype.classDef = function() {
	var self = this;
	var container;
	var loadingMessage;
	var CATEGORY_LIST = $H({
		menu: ISA_R.alb_menu,
		ajax: ISA_R.alb_ajaxRequest,
		rss: ISA_R.alb_widgetWithRSS,
		customize: ISA_R.alb_userCustomization,
		log: ISA_R.alb_userOperationLog,
		layout: ISA_R.alb_layout,
		session: ISA_R.alb_loginSessionSetting,
		system: ISA_R.alb_systemSetup
	});
	
	this.initialize = function() {
		container = document.getElementById("properties");
		
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
	
	this.displayProperties = function() {
		
		var propertiesDiv = document.createElement("div");
		propertiesDiv.style.clear = "both";
		
		var refreshAllDiv = document.createElement("div");
		refreshAllDiv.className = "refreshAll";
		//refreshAllDiv.style.textAlign = "right";
		//refreshAllDiv.style.width = "80%";
		
		var commitDiv = ISA_Admin.createIconButton(ISA_R.alb_changeApply, ISA_R.alb_changeApply, "database_save.gif", "right");
		refreshAllDiv.appendChild(commitDiv);
		var currentModal = new Control.Modal(
			false,
			{
				contents: ISA_R.ams_applyingChanges,
				opacity: 0.2,
				containerClassName:"commitDialog",
				overlayCloseOnClick:false
			}
		);
		IS_Event.observe(commitDiv, 'click', self.commitProperties.bind(this, currentModal), false, "_adminProperties");
		
		var refreshDiv = ISA_Admin.createIconButton(ISA_R.alb_refresh, ISA_R.alb_reloadWithourSaving, "refresh.gif", "right");
		refreshAllDiv.appendChild(refreshDiv);
		IS_Event.observe(refreshDiv, 'click', function(){
			if( !ISA_Admin.checkUpdated() )
				return;
			
			ISA_Admin.isUpdate = false;
			ISA_Admin.TabBuilders.properties.build();
		}, false, "_adminProperties");
		
		var advancedDiv = ISA_Admin.createIconButton(ISA_R.alb_advancedSettings, ISA_R.alb_advancedSettings, "wrench.gif", "right");
		refreshAllDiv.appendChild(advancedDiv);
		IS_Event.observe(advancedDiv, 'click', function(){
			$$(".advancedProperty").each(function(el){
				Element.toggle(el);
			});
		}, false, "_adminProperties");
		
//		var titleDiv = document.createElement("div");
//		titleDiv.id = "propertiesTitle";
//		titleDiv.className = "propertiesTitle";
//		titleDiv.appendChild(document.createTextNode(ISA_R.alb_propertiesList));
		
		var advancedMsg = document.createElement("div");
		advancedMsg.className = "advancedProperty";
		advancedMsg.style.display = "none";
		advancedMsg.innerHTML = ISA_R.alb_advancedSettingsMessage;
		
		propertiesDiv.appendChild(refreshAllDiv);
//		propertiesDiv.appendChild(titleDiv);
		propertiesDiv.appendChild(advancedMsg);
		propertiesDiv.appendChild(self.buildProperties());
		
		container.replaceChild(propertiesDiv,loadingMessage);
	}
	
	this.buildProperties = function() {
		var propertiesDiv = document.createElement("div");
		propertiesDiv.id = "properties";
		//propertiesDiv.style.width = "80%";
		
		var categoryTables = {};
		// PropertiesList build
		CATEGORY_LIST.each(function(category){
			var categoryElm = document.createElement("fieldSet");
			var categoryLabel = document.createElement("legend");
			categoryLabel.appendChild(document.createTextNode(category.value));
			categoryElm.appendChild(categoryLabel);

			var propertiesTable = document.createElement("table");
			propertiesTable.border = "1";
			propertiesTable.cellSpacing = "1";
			propertiesTable.cellPadding = "1";
			propertiesTable.className = "propertiesGroup";
			propertiesTable.style.width = "100%";

			var propertiesTbody = document.createElement("tbody");
			propertiesTbody.id = "propertiesTbody";
			propertiesTable.appendChild(propertiesTbody);

			var propertiesTr = document.createElement("tr");
			propertiesTbody.appendChild(propertiesTr);

			var propertiesTd;
			propertiesTd = document.createElement("td");
			propertiesTd.className = "headerProperties";
			propertiesTd.style.whiteSpace = "nowrap";
			propertiesTd.style.width = "30%";
			propertiesTd.appendChild(document.createTextNode(ISA_R.alb_porpety));
			propertiesTr.appendChild(propertiesTd);

			propertiesTd = document.createElement("td");
			propertiesTd.className = "headerProperties";
			propertiesTd.style.whiteSpace = "nowrap";
			propertiesTd.style.width = "30%";
			propertiesTd.appendChild(document.createTextNode(ISA_R.alb_value));
			propertiesTr.appendChild(propertiesTd);

			propertiesTd = document.createElement("td");
			propertiesTd.className = "headerProperties";
			propertiesTd.style.whiteSpace = "nowrap";
			propertiesTd.style.width = "40%";
			propertiesTd.appendChild(document.createTextNode(ISA_R.alb_description));
			propertiesTr.appendChild(propertiesTd);

			categoryElm.appendChild(propertiesTable);
			propertiesDiv.appendChild(categoryElm);

			categoryTables[category.key] = propertiesTbody;
		});

		for(var i in ISA_Properties.propertiesList){
			if( !(ISA_Properties.propertiesList[i] instanceof Function) ) {
				var property = ISA_Properties.propertiesList[i];
				if(categoryTables[property.category] && property.datatype != 'hidden')
					categoryTables[property.category].appendChild(self.buildPropertiesList(property));
			}
		}

		return propertiesDiv;
	}
	
	this.buildPropertiesList = function(property) {
		
		var tr = document.createElement("tr");
		
		if(!property) return tr;
		
		if(property.advanced){
			tr.className = "advancedProperty";
			tr.style.display = "none";
		}
		
		var td;
		td = document.createElement("td");
		td.style.whiteSpace = "nowrap";
		td.style.paddingLeft = "5px";
		
		td.appendChild(document.createTextNode(ISA_Admin.replaceUndefinedValue(property.id)));
		tr.appendChild(td);
		
		td = document.createElement("td");
		td.style.whiteSpace = "nowrap";
		td.style.paddingLeft = "5px";
		
		var prefConf = property;
		prefConf.name = property.id;
		prefConf.label = property.id;
		prefConf.maxBytes = 1024;
		if (property.enumValue) {
			try {
				prefConf.EnumValue = property.enumValue.evalJSON();
			}catch(e){
				msg.warn(getErrorMessage(e));
			}
		}
		var inputForm = ISA_WidgetConf.makeForm('input', prefConf);
		Element.addClassName(inputForm, prefConf.datatype);
		td.appendChild(inputForm);
		tr.appendChild(td);
		
		// TODO: 1.1.1 Temporary fix maxColumnNum
		/*if("maxColumnNum" == property.id || "ajaxRequestTimeout" == property.id || "sideMenuTabs" == property.id)
			propertyValueInput.disabled = true;*/
		
		td = document.createElement("td");
		//td.style.whiteSpace = "nowrap";
		td.style.paddingLeft = "5px";
		td.style.fontSize = "90%";
		td.appendChild(document.createTextNode(ISA_R["alb_desc_"+property.id]+ "　"));
		tr.appendChild(td);
		
		return tr;
	}
	
	this.commitProperties = function(modal) {
		var errorMsgs = [];
		for(var id in ISA_Properties.propertiesList) {
			if( (ISA_Properties.propertiesList[id] instanceof Function) )
				continue;
			
			var property = ISA_Properties.propertiesList[id];
			// Obtain value
			var inputValue = ISA_WidgetConf.getFormValue('input', property);
			if(inputValue == null)
				continue;
			inputValue = '' + inputValue;
			
			var errorMsg = [];
			var propertyInput = $('input_'+property.id);
			if (property.regex || property.required || property.maxLength || property.maxBytes) {
				var error = IS_Validator.validate(inputValue, property);
				if(error){
					propertyInput.style.borderColor = "red";
					propertyInput.style.backgroundColor = "mistyrose";
					propertyInput.title = error;
					errorMsgs.push(error);
					continue;
				}
			}
			
			var jsonObj = Object.clone(property);
			jsonObj.value = inputValue;
			// Update to public object 
			ISA_Properties.propertiesList[id] = jsonObj;
		}
		if(errorMsgs.length > 0){
			alert(errorMsgs.join('\n'));
			return;
		}
		
		// Update to DB
		self.updateProperties(modal);
	}
	
	this.updateProperties = function(modal) {
		modal.open();
		
		var url = adminHostPrefix + "/services/properties/updateProperties";
		var properties = {};
		for(var id in ISA_Properties.propertiesList) {
			if( (ISA_Properties.propertiesList[id] instanceof Function) )
				continue;
			var key = ISA_Properties.propertiesList[id].id;
			var value = ISA_Properties.propertiesList[id].value;
			
			properties[key] = value;
		}
		
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			postBody: Object.toJSON([properties]),
			contentType: "text/plain",
			asynchronous:true,
			onSuccess: function(response){
				ISA_Admin.isUpdated = false;
				modal.update(ISA_R.ams_changeUpdated);
			},
			onFailure: function(t) {
				alert(ISA_R.ams_failedUpdateProperties);
				msg.error(ISA_R.ams_failedUpdateProperties + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				alert(ISA_R.ams_failedUpdateProperties);
				msg.error(ISA_R.ams_failedUpdateProperties + getErrorMessage(t));
				setTimeout(function(){
					modal.close();
				},500);
				throw t;
			},
			onComplete: function(){
				setTimeout(function(){
					modal.close();
				},500);
			}
		};
		AjaxRequest.invoke(url, opt);
	}
	
	this.build = function() {
		var url = adminHostPrefix + "/services/properties/getPropertiesJson";
		var opt = {
			method: 'get' ,
			asynchronous:true,
			onSuccess: function(response){
				eval("ISA_Properties.setProperties("+response.responseText+");");
				self.displayProperties();
			},
			on404: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_propertyNotFound+"</span>";
				msg.error(ISA_R.ams_propertyNotFound + t.status + " - " + t.statusText);
			},
			onFailure: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingProperty+"</span>";
				msg.error(ISA_R.ams_failedLoadingProperty + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingProperty+"</span>";
				msg.error(ISA_R.ams_failedLoadingProperty + getErrorMessage(t));
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
