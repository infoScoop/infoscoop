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
var ISA_ExtApps = IS_Class.create();

ISA_ExtApps.extApps = false;
ISA_ExtApps.appList = false;
ISA_ExtApps.selectedExtApps = false;

ISA_ExtApps.prototype.classDef = function() {
	var self = this;
	var container;
	var loadingMessage;

	this.initialize = function() {
		this.id = "_adminExtApps";
	};

	this.displayExtApps = function() {
		
		var extAppsDiv = document.createElement("div");
		extAppsDiv.id = "adminExtAppsContent";

		var extAppsHeaderDiv = document.createElement("div");
		extAppsHeaderDiv.id = "adminExtAppsHeader";
		var addExtApps = ISA_Admin.createIconButton(ISA_R.alb_addExtApps, ISA_R.alb_addExtApps, "add.gif", "right");
		extAppsHeaderDiv.appendChild(addExtApps);
		extAppsDiv.appendChild( extAppsHeaderDiv );
		extAppsDiv.appendChild( self.buildExtAppsContainer() );

		IS_Event.observe(addExtApps, 'click', function(){
			ISA_Admin.isUpdated = this.isEdited();
			if(!ISA_Admin.checkUpdated())return;
			// this.rebuildGadgetUploadPanel();
			// this.editUpdateButton.style.display = this.editDeleteButton.style.display = this.editOKButton.style.display = this.editCancelButton.style.display = "none";
			this.buildExtAppsDetails();
		}.bind(this),false,this.id);

		container.replaceChild(extAppsDiv,loadingMessage);
	}

	this.selectExtApps = function(obj) {
		this.clearSelectExtApps();
		
		// var widgetConf = this._getConf(type);
		// if(!widgetConf) return;
		
		var editItem = $jq("#editExtAppsItem_"+obj.clientId);
		if(editItem){
			this.selectedExtApps = obj;
			this.selectedItem = editItem;
			editItem.addClass("selectedEditConf");
		}
	}

	this.clearSelectExtApps = function(){
		if(this.selectedItem){
			this.selectedItem.removeClass();
			this.selectedItem = null;
			this.selectedExtApps = false;
		}
	}

	this.buildExtAppsContainer = function(){
		var containerDiv = document.createElement("div");
		containerDiv.id = "adminExtAppsBody";
		containerDiv.className = "extAppsBody"
		
		var menuDiv = document.createElement("div");
		menuDiv.id = "adminExtAppsMenu";
		menuDiv.className = "extAppsContent";
		menuDiv.style.width = "25%";
		containerDiv.appendChild(menuDiv);

		// start create ExtApps menu contents
		this.extAppsPanel = document.createElement("div");
		this.extAppsPanel.id = "extAppsList_div";
		this.extAppsPanel.className ="gadgetListDiv";
		// end create ExtApps menu contents

		// start create ExtApps Menu outline
		var extAppsAcc = document.createElement("ul");
		extAppsAcc.id = "gadgetAcc";
		extAppsAcc.className = "acc";
		
		var accHeader = document.createElement("li");
		accHeader.className = "accHeader"		
		var extAppsListLabel = document.createElement("div");
		extAppsListLabel.id = "gadgetListLabel";
		extAppsListLabel.style.cursor = "auto";
		var extAppsListLabelText = document.createElement("span");
		extAppsListLabelText.appendChild(document.createTextNode(ISA_R.alb_extAppsList));
		extAppsListLabelText.style.marginLeft = "10px";
		
		var extAppsList = document.createElement("div");
		extAppsList.id = "gadgetList";
		extAppsList.className = "accContent";
		
		accHeader.appendChild(extAppsListLabel);
		extAppsListLabel.appendChild(extAppsListLabelText);
		accHeader.appendChild(extAppsList);
		extAppsAcc.appendChild(accHeader);
		menuDiv.appendChild(extAppsAcc);
 		extAppsList.appendChild(this.extAppsPanel);
 		// end create ExtApps Menu Outline

		var detailDiv = document.createElement("div");
		detailDiv.id = "adminExtAppsDetails";
		detailDiv.className = "extAppsContent"
		detailDiv.style.width = "74%";
		var initMessage = document.createElement("div");
		initMessage.style.padding = "5px";
		initMessage.innerHTML = ISA_R.alb_setExtAppsDefault+"<br>"+ISA_R.alb_selectExtAppsToSet;
		detailDiv.appendChild(initMessage);
		containerDiv.appendChild(detailDiv);
		
		return containerDiv;
	};

	this.buildExtAppsItems = function() {
		$jq('#extAppsList_div').empty();
		for(var i = 0; i < ISA_ExtApps.appList.length; i++){
			var obj = ISA_ExtApps.appList[i];
			var itemDiv = document.createElement("div");
			itemDiv.id = "editExtAppsItem_"+obj.clientId;
			itemDiv.title = obj.appName;
			itemDiv.appendChild(document.createTextNode(obj.appName));

			IS_Event.observe(itemDiv, 'mouseover', function(){
				if(this.className != "selectedEditConf" && this.className != "selectEditExtApps")
					Element.addClassName(this, "selectEditExtApps");
			}.bind(itemDiv), false, this.id);
			IS_Event.observe(itemDiv, 'mouseout', function(){
				if(this.className != "selectedEditConf")
					Element.removeClassName(this, "selectEditExtApps");
			}.bind(itemDiv), false, this.id);

			IS_Event.observe(itemDiv, 'click', function(obj){
				ISA_Admin.isUpdated = this.isEdited();
				if(!ISA_Admin.checkUpdated())return;
				this.buildExtAppsDetails(obj);
			}.bind(this, ISA_ExtApps.appList[i]),false,this.id);
			this.extAppsPanel.appendChild(itemDiv);
		}
	}
	
	this.buildExtAppsDetails = function(obj) {
		var baseDiv = $jq('#adminExtAppsDetails');
		baseDiv.empty();

		var editDiv = document.createElement("div");
		editDiv.className = "configSet";
		var editLabel = document.createElement("p");
		editLabel.className = "configSetHeader";
		editLabel.appendChild(document.createTextNode(ISA_R.alb_editExtAppsSettings));
		editDiv.appendChild(editLabel);
		editDiv.appendChild(this.buildEditConf(obj));

		var opsDiv = document.createElement("div");
		opsDiv.style.textAlign = "center";
		var submitBtn = document.createElement("button");
		submitBtn.appendChild(document.createTextNode(ISA_R.alb_saveSettings));
		opsDiv.appendChild(submitBtn)
		var cancelBtn = document.createElement("button");
		cancelBtn.appendChild(document.createTextNode(ISA_R.alb_cancel));
		opsDiv.appendChild(cancelBtn);

		IS_Event.observe(submitBtn, 'click', this.submitEdit.bind(this),false,this.id);
		IS_Event.observe(cancelBtn, 'click', function(){
			ISA_Admin.isUpdated = this.isEdited();
			if(!ISA_Admin.checkUpdated())return;			
			this.cancelEdit();
		}.bind(this),false,this.id);

		baseDiv.append(editDiv);
		baseDiv.append(opsDiv);

		if(obj){
			var deleteBtn = document.createElement("button");
			deleteBtn.style.marginLeft = '5px';
			deleteBtn.appendChild(document.createTextNode(ISA_R.alb_deleteExtApps));
			opsDiv.appendChild(deleteBtn);
			IS_Event.observe(deleteBtn, 'click', function(){
				if( !confirm( ISA_R.ams_deleteExtAppsConfirm ))return;
				this.deleteExtApps();
			}.bind(this),false,this.id);

			this.selectExtApps(obj);
			// type check
			$jq('input[name="extAppType"]').val([obj.grantType]);
		}else{
			this.clearSelectExtApps();
		}
	}

	this.buildEditConf = function(obj) {
		var editDiv = document.createElement("div");
		editDiv.id = "adminExtAppsDetailsConf";

		// app name
		var extAppsNameDiv = document.createElement("div");
		extAppsNameDiv.id = "adminExtAppsName";
		var nameField = document.createElement("input");
		nameField.id = "appName";
		nameField.type = "text";
		nameField.style.marginLeft = "8px";
		nameField.style.width = "400px";
		nameField.placeholder = ISA_R.alb_extAppsName;
		extAppsNameDiv.appendChild(nameField);

		// client id
		var extAppsClientIdDiv = document.createElement("div");
		extAppsClientIdDiv.id = "adminExtAppsClientId";
		var clientIdLabel = document.createElement("div");
		clientIdLabel.className = "extAppsDetailsLabel";
		clientIdLabel.appendChild(document.createTextNode(ISA_R.alb_extAppsClientId));
		var clientId = document.createElement("div");
		clientId.id = "clientId";
		clientId.className = "extAppsDetailsInput";
		extAppsClientIdDiv.appendChild(clientIdLabel);
		extAppsClientIdDiv.appendChild(clientId);

		// client secret
		var extAppsClientSecretDiv = document.createElement("div");
		extAppsClientSecretDiv.id = "adminExtAppsClientSecret";
		var clientSecretLabel = document.createElement("div");
		clientSecretLabel.className = "extAppsDetailsLabel";
		clientSecretLabel.appendChild(document.createTextNode(ISA_R.alb_extAppsClientSecret));
		var clientSecret = document.createElement("div");
		clientSecret.id = "clientSecret";
		clientSecret.className = "extAppsDetailsInput";
		extAppsClientSecretDiv.appendChild(clientSecretLabel);
		extAppsClientSecretDiv.appendChild(clientSecret);

		// redirect url
		var extAppsRedirectUrlDiv = document.createElement("div");
		extAppsRedirectUrlDiv.id = "adminExtAppsRedirectUrl";
		var redirectUrlLabel = document.createElement("div");
		redirectUrlLabel.className = "extAppsDetailsLabel";
		redirectUrlLabel.appendChild(document.createTextNode(ISA_R.alb_extAppsRedirectUrl));
		var redirectUrl = document.createElement("div");
		redirectUrl.className = "extAppsDetailsInput";
		var urlField = document.createElement("input");
		urlField.id = "redirectUrl";
		urlField.type = "text";
		urlField.placeholder = "http://";
		urlField.style.width = "400px";
		redirectUrl.appendChild(urlField);
		extAppsRedirectUrlDiv.appendChild(redirectUrlLabel);
		extAppsRedirectUrlDiv.appendChild(redirectUrl);

		// app type
		var extAppsTypeDiv = document.createElement("div");
		extAppsTypeDiv.id = "adminExtAppsType";
		var typeLabel = document.createElement("div");
		typeLabel.className = "extAppsDetailsLabel";
		typeLabel.appendChild(document.createTextNode(ISA_R.alb_extAppsGrantType));
		extAppsTypeDiv.appendChild(typeLabel);

		var appTypeSet = document.createElement("div");
		appTypeSet.className = "extAppsDetailsInput"
		var typeField1Div = document.createElement("div");
		var typeField1Label = document.createElement("label")
		typeField1Label.htmlFor = "extAppsWeb";
		var typeField1 = document.createElement("input");
		typeField1.type = "radio";
		typeField1.name = "extAppType";
		typeField1.value = "web";
		typeField1.id = "extAppsWeb";
		typeField1.checked = true;
		typeField1.style.marginRight = "5px";
		typeField1Label.appendChild(typeField1);
		typeField1Label.appendChild(document.createTextNode(ISA_R.alb_extAppsGrantTypeWeb));
		typeField1Div.appendChild(typeField1Label);

		var typeField2Div = document.createElement("div");
		var typeField2Label = document.createElement("label")
		typeField2Label.htmlFor = "extAppNative"
		var typeField2 = document.createElement("input");
		typeField2.type = "radio";
		typeField2.name = "extAppType";
		typeField2.value = "native";
		typeField2.id = "extAppNative";
		typeField2.style.marginRight = "5px";
		typeField2Label.appendChild(typeField2);
		typeField2Label.appendChild(document.createTextNode(ISA_R.alb_extAppsGrantTypeNative));
		typeField2Div.appendChild(typeField2Label);
		appTypeSet.appendChild(typeField1Div);
		appTypeSet.appendChild(typeField2Div);
		extAppsTypeDiv.appendChild(appTypeSet);

		// explain
		var extAppsExplainDiv = document.createElement("div");
		extAppsExplainDiv.id = "adminExtAppsExplain";
		var explainLabel = document.createElement("div");
		explainLabel.className = "extAppsDetailsLabel";
		explainLabel.appendChild(document.createTextNode(ISA_R.alb_extAppsExplain));
		extAppsExplainDiv.appendChild(explainLabel);
		var explain = document.createElement("div");
		explain.className = "extAppsDetailsInput"
		var explainField = document.createElement("textarea");
		explainField.style.width = "400px";
		explainField.style.height = "80px";
		explainField.id = "explain";
		explain.appendChild(explainField);
		extAppsExplainDiv.appendChild(explain);

		var extAppsDetailInfoDiv = document.createElement("div");
		extAppsDetailInfoDiv.id = "adminExtAppsDetailInfo";
		extAppsDetailInfoDiv.appendChild(extAppsClientIdDiv);
		extAppsDetailInfoDiv.appendChild(extAppsClientSecretDiv);
		extAppsDetailInfoDiv.appendChild(extAppsRedirectUrlDiv);
		extAppsDetailInfoDiv.appendChild(extAppsTypeDiv);
		extAppsDetailInfoDiv.appendChild(extAppsExplainDiv);

		if(obj){
			nameField.value = obj.appName;
			clientId.appendChild(document.createTextNode(obj.clientId));
			clientSecret.appendChild(document.createTextNode(obj.clientSecret));
			clientSecret.appendChild(this.buildResetLink());

			if(obj.redirectUrl) urlField.value = obj.redirectUrl;
			if(obj.explain) explainField.value = obj.explain;
		}

		editDiv.appendChild(extAppsNameDiv);
		editDiv.appendChild(extAppsDetailInfoDiv);

		return editDiv;
	}

	this.buildResetLink = function() {
		var resetClientSecretSpan = document.createElement("span");
		resetClientSecretSpan.className = "clientSecretResetBase";
		var resetClientSecretLink = document.createElement("span");
		resetClientSecretLink.appendChild(document.createTextNode(ISA_R.alb_resetClientSecret));

		resetClientSecretSpan.appendChild(document.createTextNode("( "));
		resetClientSecretSpan.appendChild(resetClientSecretLink);
		resetClientSecretSpan.appendChild(document.createTextNode(" )"));

		IS_Event.observe(resetClientSecretLink, 'mouseover', function(){
			Element.addClassName(this, "clientSecretReset");
		}.bind(resetClientSecretLink), false, this.id);
		IS_Event.observe(resetClientSecretLink, 'mouseout', function(){
			Element.removeClassName(this, "clientSecretReset");
		}.bind(resetClientSecretLink), false, this.id);
		IS_Event.observe(resetClientSecretLink, 'click', function(){
			if( !confirm( ISA_R.ams_confirmResetClientSecret ))return;
			this.resetClientSecret();
		}.bind(this),false,this.id);

		return resetClientSecretSpan;
	}

	this.build = function() {
		container = document.getElementById("extApps");

		var len = container.childNodes.length;
		for(var i = 0; i < len; i++) {
			container.removeChild(container.lastChild);
		}
		
		loadingMessage = document.createElement('div');
		loadingMessage.innerHTML = "Loading...";
		loadingMessage.style.clear = "both";
		loadingMessage.style.cssFloat = "left";
		container.appendChild(loadingMessage);
		
		self.displayExtApps();

		this.buildExtAppsConfs();
	}

	this.buildExtAppsConfs = function() {
		var url = adminHostPrefix + "/services/extApps/getExtAppsList";
		var opt = {
		  method: 'get' ,
		  asynchronous:true,
		  onSuccess: function(response){
		  	ISA_ExtApps.appList = eval("(" + response.responseText + ")");
			self.buildExtAppsItems();
		  },
		  on404: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_extAppsNotFound+"</span>";
			  msg.error(ISA_R.ams_extAppsNotFound + t.status + " - " + t.statusText);
		  },
		  onFailure: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
			  container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingExtApps+"</span>";
			  msg.error(ISA_R.ams_failedLoadingExtApps + t.status + " - " + t.statusText);
		  },
		  onException: function(r, t){
				if(!container.firstChild) container.appendChild(document.createElement("div"));
			  container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingExtApps+"</span>";
			  msg.error(ISA_R.ams_failedLoadingExtApps + getErrorMessage(t));
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

	this.submitEdit = function() {
		var postObj = {
			appName:$jq('#appName').val(),
		  	clientId:$jq('#clientId').text(),
		  	clientSecret:$jq('#clientSecret').text(),
		  	grantType:$jq('input[name="extAppType"]:checked').val(),
		  	redirectUrl:$jq('#redirectUrl').val(),
		  	explain:$jq('#explain').val()
		};
		var url = adminHostPrefix + "/services/extApps/saveExtApps";
		var opt = {
		  method: 'post' ,
		  asynchronous:true,
		  contentType: "application/json",
		  postBody:Object.toJSON([Object.toJSON(postObj)]),
		  onSuccess: function(response){
		  	var json = eval("(" + response.responseText + ")");
		  	ISA_ExtApps.appList = json.list;
			self.buildExtAppsItems();
			self.buildExtAppsDetails(json.self);
		  },
		  onFailure: function(t) {
			if(!container.firstChild) container.appendChild(document.createElement("div"));
			container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingExtApps+"</span>";
		 	msg.error(ISA_R.ams_failedLoadingExtApps + t.status + " - " + t.statusText);
		  },
		  onException: function(r, t){
			if(!container.firstChild) container.appendChild(document.createElement("div"));
			container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingExtApps+"</span>";
			msg.error(ISA_R.ams_failedLoadingExtApps + getErrorMessage(t));
		  }
		};
		AjaxRequest.invoke(url, opt);
	}

	this.resetClientSecret = function() {
		var postObj = {
		  	clientId:$jq('#clientId').text()
		};
		var url = adminHostPrefix + "/services/extApps/resetClientSecret";
		var opt = {
		  method: 'put',
		  asynchronous:true,
		  contentType: "application/json",
		  postBody:Object.toJSON([Object.toJSON(postObj)]),
		  onSuccess: function(response){
		  	ISA_Admin.isUpdated = true;
		  	var jsonObj = eval("(" + response.responseText + ")");
		  	var clientSecretDiv = $jq("#clientSecret");
		  	clientSecretDiv.empty().append(jsonObj.clientSecret);
		  	clientSecretDiv.append(self.buildResetLink());
			for(var i = 0; i < ISA_ExtApps.appList.length; i++){
				var obj = ISA_ExtApps.appList[i];
				if(self.selectedExtApps.clientId == obj.clientId){
					obj.clientSecret = jsonObj.clientSecret;
					break;
				}
			}
		  },
		  onFailure: function(t) {
			if(!container.firstChild) container.appendChild(document.createElement("div"));
			container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingExtApps+"</span>";
			msg.error(ISA_R.ams_failedLoadingExtApps + t.status + " - " + t.statusText);
		  },
		  onException: function(r, t){
			if(!container.firstChild) container.appendChild(document.createElement("div"));
			container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingExtApps+"</span>";
			msg.error(ISA_R.ams_failedLoadingExtApps + getErrorMessage(t));
		  }
		};
		AjaxRequest.invoke(url, opt);
	}

	this.deleteExtApps = function() {
		var postObj = {
		  	clientId:$jq('#clientId').text()
		};
		var url = adminHostPrefix + "/services/extApps/deleteExtApps";
		var opt = {
		  method: 'delete',
		  asynchronous:true,
		  contentType: "application/json",
		  postBody:Object.toJSON([Object.toJSON(postObj)]),
		  onSuccess: function(response){
		  	$jq("#editExtAppsItem_"+self.selectedExtApps.clientId).remove();
		  	self.clearSelectExtApps();

		  	var deleteMessage = document.createElement("div");
		  	deleteMessage.style.padding = "5px";
		  	deleteMessage.innerHTML = ISA_R.ams_deleteExtApps;
		  	$jq("#adminExtAppsDetails").empty().append(deleteMessage);
		  },
		  onFailure: function(t) {
			if(!container.firstChild) container.appendChild(document.createElement("div"));
			container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingExtApps+"</span>";
			msg.error(ISA_R.ams_failedLoadingExtApps + t.status + " - " + t.statusText);
		  },
		  onException: function(r, t){
			if(!container.firstChild) container.appendChild(document.createElement("div"));
			container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingExtApps+"</span>";
			msg.error(ISA_R.ams_failedLoadingExtApps + getErrorMessage(t));
		  }
		};
		AjaxRequest.invoke(url, opt);
	}

	this.cancelEdit = function() {
		if(this.selectedExtApps){
			$jq('#appName').val(this.selectedExtApps.appName);
			$jq('#clientId').text(this.selectedExtApps.clientId);
			$jq('#clientSecret').text(this.selectedExtApps.clientSecret);
			(this.selectedExtApps.redirectUrl)? $jq('#redirectUrl').val(this.selectedExtApps.redirectUrl):$jq('#redirectUrl').val("");
			(this.selectedExtApps.explain)? $jq('#explain').val(this.selectedExtApps.explain):$jq('#explain').val("");
			$jq('input[name="extAppType"]').val([this.selectedExtApps.grantType]);
		}else{
			$jq('#appName').val("");
			$jq('#redirectUrl').val("");
			$jq('#explain').val("");
			$jq('input[name="extAppType"]').val(["web"]);
		}
	}

	this.isEdited = function() {
		if(this.selectedExtApps){
			var redirectUrl = '';
			var explain = '';
			if(this.selectedExtApps.redirectUrl) redirectUrl = this.selectedExtApps.redirectUrl;
			if(this.selectedExtApps.explain) explain = this.selectedExtApps.explain;
			if($jq('#appName').val() != this.selectedExtApps.appName 
				|| $jq('#redirectUrl').val() != redirectUrl
				|| $jq('#explain').val() != explain
				|| $jq('input[name="extAppType"]:checked').val() != this.selectedExtApps.grantType)
				return true;
		}else{
			if($jq('#appName').length){
				if($jq('#appName').val() != '' 
					|| $jq('#redirectUrl').val() != ''
					|| $jq('#explain').val() != ''
					|| $jq('input[name="extAppType"]:checked').val() != 'web')
					return true;
			}
		}
		return false;
	}
}