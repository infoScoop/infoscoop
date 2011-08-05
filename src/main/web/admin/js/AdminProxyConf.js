var ISA_ProxyConf = IS_Class.create();

ISA_ProxyConf.proxyConf = false;
ISA_ProxyConf.caseList = false;
ISA_ProxyConf.defaultConf = false;

// Called by the value returned from server
ISA_ProxyConf.setProxyConf = function(_caseList, _default) {
	ISA_ProxyConf.caseList = _caseList;
	ISA_ProxyConf.defaultConf = _default;
}
ISA_ProxyConf.defaultSendHedaers = ["Host", "Content-Length"];

ISA_ProxyConf.prototype.classDef = function() {
	var self = this;
	var container;
	var loadingMessage;

	this.initialize = function() {
		container = document.getElementById("proxy");

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

	this.displayProxyConfig = function() {

		var proxyConfigDiv = document.createElement("div");
//		proxyConfigDiv.style.width = "1000px";

		container.replaceChild(proxyConfigDiv,loadingMessage);
		
		var refreshAllDiv = document.createElement("div");
		refreshAllDiv.className = "refreshAll";
//		refreshAllDiv.style.textAlign = "right";
//		refreshAllDiv.style.width = "1000px";

		var commitDiv = ISA_Admin.createIconButton(ISA_R.alb_changeApply, ISA_R.alb_changeApply, "database_save.gif", "right");
		refreshAllDiv.appendChild(commitDiv);
		var currentModal = new Control.Modal(
			commitDiv,
			{
				contents: ISA_R.ams_applyingChanges,
				opacity: 0.2,
				containerClassName:"commitDialog",
				overlayCloseOnClick:false
			}
		);
		IS_Event.observe(commitDiv, 'click', commitProxyConfig.bind(this, currentModal), "_adminProxy");

		var refreshDiv = ISA_Admin.createIconButton(ISA_R.alb_refresh, ISA_R.alb_reloadWithourSaving, "refresh.gif", "right");
		refreshAllDiv.appendChild(refreshDiv);
		IS_Event.observe(refreshDiv, 'click', function(){
			if( !ISA_Admin.checkUpdated())
				return;
			
			ISA_Admin.isUpdated = false;
			ISA_ProxyConf.proxyConf = new ISA_ProxyConf();
			ISA_ProxyConf.proxyConf.build();
		}, false, "_adminProxy");

//		var titleDiv1 = document.createElement("div");
//		titleDiv1.id = "proxyCaseTitle";
//		titleDiv1.className = "proxyTitle";
//		titleDiv1.appendChild(document.createTextNode(ISA_R.alb_proxyAndURLSettings));

		var titleDiv2 = document.createElement("div");
		titleDiv2.id = "proxyDefaultTitle";
		titleDiv2.className = "proxyTitle";
		titleDiv2.appendChild(document.createTextNode(ISA_R.alb_defaultProxy));

		var lineDiv = document.createElement("div");
		lineDiv.style.width = "1000px";

		var lineHr = document.createElement("hr");
		lineDiv.appendChild(lineHr);
		proxyConfigDiv.appendChild(refreshAllDiv);
//		proxyConfigDiv.appendChild(titleDiv1);
		proxyConfigDiv.appendChild(self.buildTableControl());
		proxyConfigDiv.appendChild(self.buildTableHeader());

		caseListDiv = document.createElement("div");
		caseListDiv.id = "caseProxyConfigList";
		for(var i in ISA_ProxyConf.caseList){
			if( !(ISA_ProxyConf.caseList[i] instanceof Function) )
				caseListDiv.appendChild(self.buildCaseProxyConfigList(ISA_ProxyConf.caseList[i]));
		}
		proxyConfigDiv.appendChild(caseListDiv);

		defaultListDiv = document.createElement("div");
		defaultListDiv.id = "defaultProxyConfigList";
//		defaultListDiv.style.width = "900px";
		defaultListDiv.style.width = $("proxyConfigHeader").offsetWidth;
		for(var i in ISA_ProxyConf.defaultConf){
			if( !(ISA_ProxyConf.defaultConf[i] instanceof Function) )
				defaultListDiv.appendChild(self.buildCaseProxyConfigList(ISA_ProxyConf.defaultConf[i], true));
		}
		proxyConfigDiv.appendChild(defaultListDiv);

		proxyConfigDiv.appendChild(document.createElement("br"));

//		container.replaceChild(proxyConfigDiv,loadingMessage);

		// Drag&Drop
		this.dnd = new ISA_DragDrop.ProxyConfigDragDrop("caseProxyConfigList");
	}

	function commitProxyConfig(currentModal) {
		var url = adminHostPrefix + "/services/proxyConf/commitProxyConf";
		var opt = {
			method: 'get' ,
			asynchronous:true,
			onSuccess: function(response){
				ISA_Admin.isUpdated = false;
				currentModal.update(ISA_R.ams_changeUpdated);
			},
			onFailure: function(t) {
				alert(ISA_R.ams_failedCommitProxy);
				msg.error(ISA_R.ams_failedCommitProxy + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				alert(ISA_R.ams_failedCommitProxy);
				msg.error(ISA_R.ams_failedCommitProxy + getErrorMessage(t));
			},
			onComplete: function(){
				setTimeout(function(){
					currentModal.close();
				},500);
			}
		};
		AjaxRequest.invoke(url, opt);
	}

	function addProxyConfig(proxyConf) {
		var url = adminHostPrefix + "/services/proxyConf/addProxyConf";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			postBody: Object.toJSON([
				ISA_Admin.replaceUndefinedValue(proxyConf.elementName),
				ISA_Admin.replaceUndefinedValue(String(proxyConf.id)),
				ISA_Admin.replaceUndefinedValue(String(proxyConf.cacheLifeTime)),
				ISA_Admin.replaceUndefinedValue(proxyConf.type),
				ISA_Admin.replaceUndefinedValue(proxyConf.pattern),
				ISA_Admin.replaceUndefinedValue(proxyConf.replacement),
				ISA_Admin.replaceUndefinedValue(proxyConf.host),
				ISA_Admin.replaceUndefinedValue(proxyConf.port),
				ISA_Admin.replaceUndefinedValue(proxyConf.headers),
			]),
			asynchronous:true,
			onSuccess: function(response){},
			onFailure: function(t) {
				alert(ISA_R.ams_failedAddProxy);
				msg.error(ISA_R.ams_failedAddProxy + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				alert(ISA_R.ams_failedAddProxy);
				msg.error(ISA_R.ams_failedAddProxy + getErrorMessage(t));
			}
		};
		AjaxRequest.invoke(url, opt);
	}

	this.buildTableHeader = function(){

		var caseConfigTable = document.createElement("table");
		caseConfigTable.id = "proxyConfigHeader";
		caseConfigTable.className = "configTableHeader";
//		caseConfigTable.style.clear = "both";
		caseConfigTable.cellSpacing = "0";
		caseConfigTable.cellPadding = "0";
//		caseConfigTable.style.width = "900px";
		var caseConfigTbody = document.createElement("tbody");
		caseConfigTable.appendChild(caseConfigTbody);
		var caseConfigTr;
		caseConfigTr = document.createElement("tr");
		caseConfigTbody.appendChild(caseConfigTr);
		caseConfigTr2 = document.createElement("tr");
		caseConfigTbody.appendChild(caseConfigTr2);

		var caseConfigTh;
		var caseConfigTd;

		caseConfigTh = document.createElement("td");
		caseConfigTh.className = "configTableHeaderTd";
		caseConfigTh.rowSpan = 2;
//		caseConfigTh.style.whiteSpace = "nowrap";
		caseConfigTh.style.width = "200px";
		caseConfigTh.appendChild(document.createTextNode(ISA_R.alb_adressPattern));
		caseConfigTr.appendChild(caseConfigTh);

		caseConfigTh = document.createElement("td");
		caseConfigTh.className = "configTableHeaderTd";
		caseConfigTh.colSpan = 4;
//		caseConfigTh.style.whiteSpace = "nowrap";
		caseConfigTh.style.width = "230px";
		caseConfigTh.appendChild(document.createTextNode(ISA_R.alb_proxy));
		caseConfigTr.appendChild(caseConfigTh);

		caseConfigTh = document.createElement("td");
		caseConfigTh.className = "configTableHeaderTd";
		caseConfigTh.style.width = "50px";
//		caseConfigTh.style.whiteSpace = "nowrap";
		caseConfigTh.appendChild(document.createTextNode(ISA_R.alb_enable));
		caseConfigTr2.appendChild(caseConfigTh);

		caseConfigTh = document.createElement("td");
		caseConfigTh.className = "configTableHeaderTd";
//		caseConfigTh.style.whiteSpace = "nowrap";
		caseConfigTh.style.width = "80px";
		caseConfigTh.appendChild(document.createTextNode(ISA_R.alb_host));
		caseConfigTr2.appendChild(caseConfigTh);

		caseConfigTh = document.createElement("td");
		caseConfigTh.className = "configTableHeaderTd";
//		caseConfigTh.style.whiteSpace = "nowrap";
		caseConfigTh.style.width = "50px";
		caseConfigTh.appendChild(document.createTextNode(ISA_R.alb_port));
		caseConfigTr2.appendChild(caseConfigTh);

		caseConfigTh = document.createElement("td");
		caseConfigTh.className = "configTableHeaderTd";
//		caseConfigTh.style.whiteSpace = "nowrap";
		caseConfigTh.style.width = "50px";
		caseConfigTh.appendChild(document.createTextNode(ISA_R.alb_authentication));
		caseConfigTr2.appendChild(caseConfigTh);

		caseConfigTh = document.createElement("td");
		caseConfigTh.className = "configTableHeaderTd";
		caseConfigTh.rowSpan = 2;
//		caseConfigTh.style.whiteSpace = "nowrap";
		caseConfigTh.style.width = "220px";
		caseConfigTh.appendChild(document.createTextNode(ISA_R.alb_substitutionURL));
		caseConfigTr.appendChild(caseConfigTh);

		caseConfigTh = document.createElement("td");
		caseConfigTh.className = "configTableHeaderTd";
		caseConfigTh.rowSpan = 2;
//		caseConfigTh.style.whiteSpace = "nowrap";
		caseConfigTh.style.width = "50px";
		caseConfigTh.appendChild(document.createTextNode(ISA_R.alb_headerTransmission));
		caseConfigTr.appendChild(caseConfigTh);
		
		caseConfigTh = document.createElement("td");
		caseConfigTh.className = "configTableHeaderTd";
		caseConfigTh.rowSpan = 2;
		caseConfigTh.style.width = "50px";
		caseConfigTh.appendChild(document.createTextNode(ISA_R.alb_intranet));
		caseConfigTr.appendChild(caseConfigTh);
		
		caseConfigTh = document.createElement("td");
		caseConfigTh.className = "configTableHeaderTd";
		caseConfigTh.colSpan = 2;
//		caseConfigTh.style.whiteSpace = "nowrap";
		caseConfigTh.style.width = "120px";
		caseConfigTh.appendChild(document.createTextNode(ISA_R.alb_cacheSeetings));
		caseConfigTr.appendChild(caseConfigTh);
		
		caseConfigTh = document.createElement("td");
		caseConfigTh.className = "configTableHeaderTd";
		caseConfigTh.style.width = "50px";
//		caseConfigTh.style.whiteSpace = "nowrap";
		caseConfigTh.appendChild(document.createTextNode(ISA_R.alb_enable));
		caseConfigTr2.appendChild(caseConfigTh);
		
		caseConfigTh = document.createElement("td");
		caseConfigTh.className = "configTableHeaderTd";
//		caseConfigTh.style.whiteSpace = "nowrap";
		caseConfigTh.style.width = "70px";
		caseConfigTh.appendChild(document.createTextNode(ISA_R.alb_durationMinute));
		caseConfigTr2.appendChild(caseConfigTh);

		caseConfigTh = document.createElement("td");
		caseConfigTh.className = "configTableHeaderTd";
		caseConfigTh.rowSpan = 2;
//		caseConfigTh.style.whiteSpace = "nowrap";
		caseConfigTh.style.width = "50px";
		caseConfigTh.appendChild(document.createTextNode(ISA_R.alb_delete));
		caseConfigTr.appendChild(caseConfigTh);
		return caseConfigTable;
	}

	this.buildTableControl = function(){
		var caseConfigDiv = document.createElement("div");
		var addCaseDiv = ISA_Admin.createIconButton(ISA_R.alb_add, ISA_R.alb_add, "add.gif", "left");
		addCaseDiv.id = "addCaseProxyConfig";
		addCaseDiv.style.cssFloat = "left";
		var addAClick = function(e){
			ISA_Admin.isUpdated = true;
			var jsonObj = {
				elementName : "case",
				id : String( new Date().getTime()),
				cacheLifeTime:"",
				type : "direct",
				pattern : "^http://",
				replacement : "",
				host : null,
				port : null,
				headers : $A(ISA_ProxyConf.HeaderConfigPane.presets.headers).select(function(value){ return !ISA_ProxyConf.defaultSendHedaers.include(value); })
			};
			addProxyConfig(jsonObj);
			$('caseProxyConfigList').appendChild(self.buildCaseProxyConfigList(jsonObj));

			// Update to public object
			ISA_ProxyConf.caseList[jsonObj.id] = jsonObj;
			// Rebuild Drag&Drop
			self.dnd = new ISA_DragDrop.ProxyConfigDragDrop("caseProxyConfigList");
		}
		IS_Event.observe(addCaseDiv, 'click', addAClick.bind(addCaseDiv), false, "_adminProxy");

		caseConfigDiv.appendChild(addCaseDiv);
		//addCaseDiv.style.width = "400px";

		var annotateDiv = document.createElement("div");
		annotateDiv.style.cssFloat = "right";
		annotateDiv.style.styleFloat = "right";
		annotateDiv.style.textAlign = "right";
		var font = document.createElement("font");
		font.size = "-1";
		font.color = "#ff0000";
		font.appendChild(document.createTextNode(ISA_R.alb_matchingFromTop));
		annotateDiv.appendChild(font);

		caseConfigDiv.appendChild(annotateDiv);

		annotateDiv.style.width = "400px";
		return caseConfigDiv;
	}

	/**
	 * caseConfigItem.id
	 * caseConfigItem.type
	 * caseConfigItem.pattern
	 * caseConfigItem.replacement
	 * caseConfigItem.host
	 * caseConfigItem.port
	 */
	this.buildCaseProxyConfigList = function(caseConfigItem, isDefault) {
		caseConfigItem.elementName = isDefault ?  "default" : "case";
		var configDiv = document.createElement("div");
		configDiv.id = "row_" + caseConfigItem.id;
		configDiv.className = "configTableDiv";

		var configTable = document.createElement("table");
//		configTable.className = "configTable";
		configTable.className = "configTableHeader";
		configTable.id = caseConfigItem.id;
//		configTable.width = "900px";
		configTable.style.width = $("proxyConfigHeader").offsetWidth;
		configTable.style.tableLayout = "fixed";
		configTable.cellSpacing = "0";
		configTable.cellPadding = "0";
		configTable.style.margin = "0";
		configDiv.appendChild(configTable);

		var configTbody = document.createElement("tbody");
		configTable.appendChild(configTbody);

		var configTr = document.createElement("tr");
		//configTr.style.height = "20px";
		configTbody.appendChild(configTr);
		var configTd;
		var contentDiv;

		// Icon for dragging
		configTd = document.createElement("td");
		configTd.className = "configTableTd"
		configTd.style.width = "20px";
		contentDiv = document.createElement("div");
		if(!isDefault){
			contentDiv.className = "handle";
			var configA = document.createElement("a");
			configA.style.cursor = "move";
			var configImg = document.createElement("img");
			configImg.src = imageURL + "drag.gif";
			configA.appendChild(configImg);
			contentDiv.appendChild(configA);
		}else{
			contentDiv.appendChild(document.createTextNode('　'));
		}
		configTd.appendChild(contentDiv);
		configTr.appendChild(configTd);

		// Address Pattern
		configTd = document.createElement("td");
		configTd.className = "configTableTd"
		configTd.style.width = "179";
		configTr.appendChild(configTd);
		contentDiv = document.createElement("div");
		contentDiv.className = "contentsProxyConfig";
		contentDiv.style.width="95%";
		contentDiv.id = "url_" + caseConfigItem.id;
		if(!isDefault){
			contentDiv.appendChild(document.createTextNode(replaceUndefinedValue(caseConfigItem.pattern, "　", true)));
			new ISA_InstantEdit(contentDiv, "proxyConf","updateProxyConf", function( value ) {
				ISA_Admin.isUpdated = true;
				return [caseConfigItem.elementName,caseConfigItem.id,"pattern",value];
			}, 512, {format:"regexp",required:true,label:ISA_R.alb_adressPattern});	// Enable input by clicking
		}else{
			contentDiv.appendChild(document.createTextNode(ISA_R.alb_defaultSettings));
		}
		configTd.appendChild(contentDiv);

		// Type
		configTd = document.createElement("td");
		configTd.className = "configTableTd"
		configTd.style.width = "50px";
		configTd.style.textAlign = "center";
		contentDiv = document.createElement("div");
		//contentDiv.className = "contentsProxyConfig";
		contentDiv.id = "typ_" + caseConfigItem.id;
		var useProxyCheckBox = ISA_Admin.createBaseCheckBox(
			"typeCheckbox_" + caseConfigItem.id,	
			("proxy" == ISA_Admin.replaceUndefinedValue(caseConfigItem.type) ),
			false);
		if(Browser.isIE){
			IS_Event.observe(useProxyCheckBox, 'click', function(){
				self.dnd.proxyCheckMap[caseConfigItem.id] = useProxyCheckBox.checked;
			}, false, "_adminProxy");
		}
		configTd.appendChild(useProxyCheckBox);
		var submitProxyAuthForm = function(form, caseConfigItem){
			var url = adminHostPrefix + "/services/proxyConf/updateProxyConf";
			var opt = {
			  method: 'post' ,
			  contentType: "application/json",
			  postBody: Object.toJSON([caseConfigItem.elementName,caseConfigItem.id,{
					username: ISA_Admin.trim($("proxyAuthUserNameForm").value),
					password: ISA_Admin.trim($("proxyAuthPasswordForm").value),
					domaincontroller: ISA_Admin.trim($("proxyAuthHostForm").value),
					domain: ISA_Admin.trim($("proxyAuthDomainForm").value)
			  }]),
			  asynchronous:true,
			  onSuccess: function(response){
			  	caseConfigItem.username = $("proxyAuthUserNameForm").value;
			  	caseConfigItem.password = $("proxyAuthPasswordForm").value;
			  	caseConfigItem.domaincontroller = $("proxyAuthHostForm").value;
			  	caseConfigItem.domain = $("proxyAuthDomainForm").value;
			  },
			  onFailure: function(t) {
				  alert(ISA_R.ams_failedSavingValueEdit);
				  msg.error(ISA_R.ams_failedSavingValueEdit + t.status + " - " + t.statusText);
			  },
			  onException: function(r, t){
				  alert(ISA_R.ams_failedSavingValueEdit);
				  msg.error(ISA_R.ams_failedSavingValueEdit + getErrorMessage(t));
			  },
			  onComplete: function(){
				  Control.Modal.close();
			  }
			};
			AjaxRequest.invoke(url, opt);
			return false;
		};

		var showProxyAuthEdit = function(caseConfigItem){
			var proxyAuthFormDiv = document.createElement('div');
			proxyAuthFormDiv.className = "modalConfigSet";
			proxyAuthFormDiv.id = "proxyAuthFormDiv";
			proxyAuthFormDiv.style.textAlign = 'center';
			var proxyAuthFormLegend = document.createElement('p');
			proxyAuthFormLegend.className = "modalConfigSetHeader";
			proxyAuthFormLegend.appendChild(document.createTextNode(ISA_R.alb_proxyAuthSettings));
			proxyAuthFormDiv.appendChild(proxyAuthFormLegend);
			var proxyAuthForm = document.createElement("form");

			var proxyAuthFormTable = new ISA_RightLeftAlignTable();

			var proxyAuthUserNameForm = document.createElement("input");
			proxyAuthUserNameForm.id = "proxyAuthUserNameForm";
			if(caseConfigItem.username)proxyAuthUserNameForm.value = caseConfigItem.username;
			proxyAuthFormTable.addRow(ISA_R.alb_userName, proxyAuthUserNameForm);

			var proxyAuthPasswordForm = document.createElement("input");
			proxyAuthPasswordForm.type = 'password';
			proxyAuthPasswordForm.id = "proxyAuthPasswordForm";
			if(caseConfigItem.password)proxyAuthPasswordForm.value = caseConfigItem.password;
			proxyAuthFormTable.addRow(ISA_R.alb_password, proxyAuthPasswordForm);

			var proxyAuthHostForm = document.createElement("input");
			proxyAuthHostForm.id = "proxyAuthHostForm";
			if(caseConfigItem.domaincontroller)proxyAuthHostForm.value = caseConfigItem.domaincontroller;
			proxyAuthFormTable.addRow(ISA_R.alb_authServerColon, proxyAuthHostForm);

			var proxyAuthDomainForm = document.createElement("input");
			proxyAuthDomainForm.id = "proxyAuthDomainForm";
			if(caseConfigItem.domain)proxyAuthDomainForm.value = caseConfigItem.domain;
			proxyAuthFormTable.addRow(ISA_R.alb_domain, proxyAuthDomainForm);

			proxyAuthForm.appendChild(proxyAuthFormTable.build());

			var proxyAuthFormSubmit = document.createElement("input");
			proxyAuthFormSubmit.type = "button";
			proxyAuthFormSubmit.value = ISA_R.alb_apply;
			proxyAuthForm.appendChild(proxyAuthFormSubmit);
			proxyAuthFormDiv.appendChild(proxyAuthForm);

			IS_Event.observe( proxyAuthFormSubmit, 'click', function(){
				 ISA_Admin.isUpdated = true;
				submitProxyAuthForm(proxyAuthForm, caseConfigItem)} , false, "_adminProxy");
			return proxyAuthFormDiv;
		};
		// Only at initialzing
		var radioEdit = new ISA_InstantEdit(contentDiv, "proxyConf","updateProxyConf", function( value ) {
				return [caseConfigItem.elementName,caseConfigItem.id,"type",value];
			}, 32, null, true);
		var radioClick = function() {
			ISA_Admin.isUpdated = true;
			var radioElement = document.getElementsByName("typeCheckbox_" + caseConfigItem.id)[0];
			if(radioElement.checked) {
				radioEdit.commitValue("proxy");

				var contentDiv = $("hst_" + caseConfigItem.id);
				new ISA_InstantEdit(contentDiv, "proxyConf","updateProxyConf", function( value ) {
					return [caseConfigItem.elementName,caseConfigItem.id,"host",value];
				}, 128);
				contentDiv.parentNode.style.backgroundColor= '#ffffff';

				var contentDiv = $("prt_" + caseConfigItem.id);
				new ISA_InstantEdit(contentDiv, "proxyConf","updateProxyConf", function( value ) {
					return [caseConfigItem.elementName,caseConfigItem.id,"port",value];
				}, 16, {regex:'^[0-9]*$', regexMsg:IS_R.ms_gtZeroNum, label:ISA_R.alb_port});
				contentDiv.parentNode.style.backgroundColor= '#ffffff';

				var contentDiv = $("proxyAuthEdit_"+ caseConfigItem.id);
				contentDiv.style.cursor = "pointer";
				caseConfigItem.modal = new Control.Modal(contentDiv,
							  {
								contents: showProxyAuthEdit.bind(this, caseConfigItem),
								opacity: 0.5,
								position: 'relative',
								width: '300',
								height:'200'
							  }
							  );
				contentDiv.parentNode.style.backgroundColor= '#ffffff';

			}else{
				$("hst_"+ caseConfigItem.id).style.cursor = "default";
				$("prt_" + caseConfigItem.id).style.cursor = "default";
				
				var url = adminHostPrefix + "/services/proxyConf/updateProxyConf";
				var opt = {
				  method: 'post' ,
				  contentType: "application/json",
				  postBody: Object.toJSON([caseConfigItem.elementName,caseConfigItem.id,{
				  	type: "direct",
				  	host: "",
				  	port: "",
				  	username: "",
				  	password: "",
				  	domaincontroller: "",
				  	domain: ""
				  }]),
				  asynchronous:true,
				  onSuccess: function(response){
					  //TODO:
					  var orgContentDiv = $("hst_" + caseConfigItem.id);
					  var contentDiv = orgContentDiv.cloneNode(true);
					  orgContentDiv.parentNode.replaceChild(contentDiv, orgContentDiv);
					  contentDiv.innerHTML = "　";
					  contentDiv.parentNode.style.backgroundColor= '#eeeeee';
					  //TODO:
					  var orgContentDiv = $("prt_" + caseConfigItem.id);
					  var contentDiv = orgContentDiv.cloneNode(true);
					  orgContentDiv.parentNode.replaceChild(contentDiv, orgContentDiv);
					  contentDiv.innerHTML = "　";
					  contentDiv.parentNode.style.backgroundColor= '#eeeeee';
					  //TODO:
					  var contentDiv = $("proxyAuthEdit_"+ caseConfigItem.id);
					  contentDiv.style.cursor = "defualt";
					  contentDiv.parentNode.style.backgroundColor= '#eeeeee';
					  caseConfigItem.proxy  = null;
					  caseConfigItem.port = null;
					  caseConfigItem.username = null;
					  caseConfigItem.password = null;
					  caseConfigItem.domaincontroller = null;
					  caseConfigItem.domain = null;
					  var contentDiv = $("proxyAuthEdit_"+ caseConfigItem.id);
					  if(contentDiv)contentDiv.onclick = function(){return false;};
				  },
				  onFailure: function(t) {
					  alert(ISA_R.ams_failedSavingValueEdit);
					  msg.error(ISA_R.ams_failedSavingValueEdit + t.status + " - " + t.statusText);
				  },
				  onException: function(r, t){
					  alert(ISA_R.ams_failedSavingValueEdit);
					  msg.error(ISA_R.ams_failedSavingValueEdit + getErrorMessage(t));
				  },
				  onComplete: function(){
					  Control.Modal.close();
				  }
				};
				AjaxRequest.invoke(url, opt);
			}
		};
		IS_Event.observe(useProxyCheckBox, 'click', radioClick.bind(this), false, "_adminProxy");
		configTd.appendChild(contentDiv);
		configTr.appendChild(configTd);

		// Host
		configTd = document.createElement("td");
		configTd.className = "configTableTd"
		configTd.style.width = "80px";
		configTr.appendChild(configTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "95%";
		contentDiv.className = "contentsProxyConfig";
		contentDiv.id = "hst_" + caseConfigItem.id;
		contentDiv.appendChild(document.createTextNode(replaceUndefinedValue(caseConfigItem.host, "　", true)));
		if("proxy" == ISA_Admin.replaceUndefinedValue(caseConfigItem.type) ){
			new ISA_InstantEdit(contentDiv,"proxyConf", "updateProxyConf", function( value ) {
				ISA_Admin.isUpdated = true;
				return [caseConfigItem.elementName,caseConfigItem.id,"host",value];
			}, 128);	// Enable input by clicking
		}else{
			configTd.style.backgroundColor= '#eeeeee';
		}
		configTd.appendChild(contentDiv);
		// Port
		configTd = document.createElement("td");
		configTd.className = "configTableTd";
		configTd.style.width = "50px";
		configTr.appendChild(configTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "95%";
		contentDiv.className = "contentsProxyConfig";
		contentDiv.id = "prt_" + caseConfigItem.id;
		contentDiv.appendChild(document.createTextNode(replaceUndefinedValue(caseConfigItem.port, "　", true)));
		if("proxy" == ISA_Admin.replaceUndefinedValue(caseConfigItem.type) ){
			new ISA_InstantEdit(contentDiv,"proxyConf", "updateProxyConf", function( value ) {
				ISA_Admin.isUpdated = true;
				return [caseConfigItem.elementName,caseConfigItem.id,"port",value];
			}, 16, {regex:'^[0-9]*$', regexMsg:IS_R.ms_gtZeroNum, label:ISA_R.alb_port});	// Enable input by clicking
		}else{
			configTd.style.backgroundColor= '#eeeeee';
		}
		configTd.appendChild(contentDiv);

		// Authentication
		configTd = document.createElement("td");
		configTd.className = "configTableTd"
		configTd.style.width = "50px";
		configTd.style.textAlign = "center";
		configTr.appendChild(configTd);
		var editIconSpan = document.createElement('span');
		editIconSpan.id ="proxyAuthEdit_" + caseConfigItem.id;
		var editIcon = document.createElement('img');
		editIcon.src = imageURL + "edit.gif";
		editIconSpan.appendChild(editIcon);
		if("proxy" == ISA_Admin.replaceUndefinedValue(caseConfigItem.type) ){

			editIconSpan.style.cursor = "pointer";
			caseConfigItem.modal = new Control.Modal(editIconSpan,
							  {
								contents: showProxyAuthEdit.bind(this, caseConfigItem),
								opacity: 0.5,
								position: 'relative',
								width: '300',
								height: '200'
							  }
							  );
		}else{
			configTd.style.backgroundColor= '#eeeeee';
		}
		configTd.appendChild(editIconSpan);

		// Replacement
		configTd = document.createElement("td");
		configTd.className = "configTableTd";
		configTd.style.width = "220px";
		
		configTr.appendChild(configTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "95%";
		contentDiv.className = "contentsProxyConfig";
		contentDiv.id = "rep_" + caseConfigItem.id;
		if(!isDefault){
			contentDiv.appendChild(document.createTextNode(replaceUndefinedValue(caseConfigItem.replacement, "　", true)));
			postJSON = {ELEMNAME:caseConfigItem.elementName, ID:caseConfigItem.id, ATTRIBUTENAME:"replacement"};
			new ISA_InstantEdit(contentDiv,"proxyConf", "updateProxyConf", function( value ) {
				ISA_Admin.isUpdated = true;
				return [caseConfigItem.elementName,caseConfigItem.id,"replacement",value];
			}, 512);	// Enable input by clicking
		}else{
			contentDiv.appendChild(document.createTextNode(ISA_R.alb_notSet));
		}
		configTd.appendChild(contentDiv);


		// Header
		configTd = document.createElement("td");
		configTd.className = "configTableTd";
		configTd.style.textAlign = "center";
		configTd.style.width = "50px";
		configTr.appendChild(configTd);

		var headersIconSpan = document.createElement('span');
		headersIconSpan.id ="proxyAuthEdit_" + caseConfigItem.id;
		var headersIcon = document.createElement('img');
		headersIcon.src = imageURL + "edit.gif";
		headersIconSpan.appendChild(headersIcon);
		headersIconSpan.style.cursor = "pointer";
		caseConfigItem.headersModal = new Control.Modal(headersIconSpan,{
				contents: ISA_ProxyConf.HeaderConfigPane.showModal.bind( false,caseConfigItem ),
				opacity: 0.5,
				width: 600
			} );

		configTd.appendChild( headersIconSpan );

		// intranet
		configTr.appendChild(
			$.TD({className:"configTableTd",style:"textAlign:center;width:50px;"},
				 $.SPAN({},$.INPUT({type:'checkbox',defaultChecked:/true/.test(caseConfigItem.intranet) ,onchange:{handler:function(e){
					var targetElement = (Browser.isIE)? e.srcElement : this;
					
					ISA_Admin.isUpdated = true;
					ISA_ProxyConf.updateProxyConfAttr( caseConfigItem, "intranet", ""+targetElement.checked );
				 }}}))
				   )
			);

		//Cache settings
		configTd = document.createElement("td");
		configTd.className = "configTableTd";
		configTd.style.width = "50px";
		configTd.style.textAlign = "center";
		configTr.appendChild(configTd);
		contentDiv = document.createElement("div");
		contentDiv.className = "contentsProxyConfig";
		contentDiv.id = "cache_" + caseConfigItem.id;
		var useCacheCheckBox = ISA_Admin.createBaseCheckBox("cacheCheckbox_" + caseConfigItem.id,(!(""==ISA_Admin.replaceUndefinedValue(caseConfigItem.cacheLifeTime))),false);
		configTd.appendChild(useCacheCheckBox);
		
		var cacheClick = function() {
			ISA_Admin.isUpdated = true;
			
			var cacheElement = document.getElementsByName("cacheCheckbox_" + caseConfigItem.id)[0];
			var cacheLifeTime = cacheElement.checked ? "10":"";
			
			var url = adminHostPrefix + "/services/proxyConf/updateProxyConf";
			var opt = {
				method: 'post' ,
				contentType: "application/json",
				postBody: Object.toJSON([caseConfigItem.elementName,caseConfigItem.id,{
					cacheLifeTime: cacheElement.checked ? String(10):""
				}]),
				asynchronous:true,
				onSuccess: function(response){
			  		displayCacheLifeTime( cacheLifeTime );
				},
				onFailure: function(t) {
					alert(ISA_R.ams_failedSavingValueEdit);
					msg.error(ISA_R.ams_failedSavingValueEdit + t.status + " - " + t.statusText);
				},
				onException: function(r, t){
					alert(ISA_R.ams_failedSavingValueEdit);
					msg.error(ISA_R.ams_failedSavingValueEdit + getErrorMessage(t));
				},
				onComplete: function(){
					Control.Modal.close();
				}
			};
			AjaxRequest.invoke(url, opt);
		};
		IS_Event.observe(useCacheCheckBox, 'click', cacheClick.bind(this), false, "_adminProxy");

		configTd = document.createElement("td");
		configTd.className = "configTableTd"
		configTd.style.width = "70px";
		configTd.id = "cacheDurationTd_" + caseConfigItem.id;
		configTr.appendChild(configTd);
		
		function displayCacheLifeTime( cacheLifeTime ) {
			configTd = $("cacheDurationTd_" + caseConfigItem.id ) || configTd;
			configTd.innerHTML = "";
			
			contentDiv = document.createElement("div");
			contentDiv.style.height = "100%";
			contentDiv.style.width = "95%";
			contentDiv.className = "contentsProxyConfig";
			contentDiv.id = "cacheDuration_" + caseConfigItem.id;
			configTd.appendChild(contentDiv);
			
			contentDiv.appendChild(document.createTextNode( cacheLifeTime ));
			if ("" != cacheLifeTime) {
				new ISA_InstantEdit(
						contentDiv,
						"proxyConf",
						"updateProxyConf",
						function(value) {
							ISA_Admin.isUpdated = true;
							return [ caseConfigItem.elementName,
									caseConfigItem.id, "cacheLifeTime", value ];
						}, 4, {
							regex : '^[1-9][0-9]*$',
							maxInt :1440,
							regexMsg : IS_R.ms_gtOneNum,
							label : ISA_R.alb_durationMinute,
							required : true
						}); // Enable input by clicking
				configTd.style.backgroundColor = "white";
			}else{
				contentDiv.innerHTML = "&nbsp;";
				configTd.style.backgroundColor = '#eeeeee';
			}
		}
		displayCacheLifeTime( replaceUndefinedValue(caseConfigItem.cacheLifeTime, "", true) );
		
		// Delete icon
		configTd = document.createElement("td");
		configTd.className = "configTableTd"
		configTd.style.textAlign = "center";
		configTd.style.width = "50px";
		configTr.appendChild(configTd);
		contentDiv = document.createElement("div");
		if(!isDefault){
			contentDiv.className = "contentsProxyConfig";
			var deleteImg = document.createElement("img");
			deleteImg.src = imageURL + "trash.gif";
			deleteImg.style.cursor = "pointer";
			deleteImg.alt = ISA_R.alb_deleting;
			var deleteImgClick = function(e){
				ISA_Admin.isUpdated = true;
				self.removeProxyConf(caseConfigItem);
				configDiv.parentNode.removeChild(configDiv);
				// Delete from public object
				delete ISA_ProxyConf.caseList[caseConfigItem.id];
			}
			IS_Event.observe(deleteImg, 'click', deleteImgClick.bind(deleteImg), false, "_adminProxy");
			contentDiv.appendChild(deleteImg);
		}else{
			contentDiv.appendChild(document.createTextNode("　"));

		}
		configTd.appendChild(contentDiv);

		return configDiv;
	}

	this.build = function() {
		var url = adminHostPrefix + "/services/proxyConf/getProxyConfJson";
		var opt = {
			method: 'get' ,
			asynchronous:true,
			onSuccess: function(response){
				eval(response.responseText);
				self.displayProxyConfig();
			},
			on404: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_proxyNotFound+"</span>";
				msg.error(ISA_R.ams_proxyNotFound + t.status + " - " + t.statusText);
			},
			onFailure: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingProxy+"</span>";
				msg.error(ISA_R.ams_failedLoadingProxy + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingProxy+"</span>";
				msg.error(ISA_R.ams_failedLoadingProxy + getErrorMessage(t));
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

	this.removeProxyConf = function( proxyConf) {
		var url = adminHostPrefix + "/services/proxyConf/removeProxyConf";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			postBody: Object.toJSON([
				ISA_Admin.replaceUndefinedValue(proxyConf.elementName),
				ISA_Admin.replaceUndefinedValue(proxyConf.id)
			]),
			asynchronous:true,
			onSuccess: function(response){
			},
			onFailure: function(t) {
				alert(ISA_R.ams_failedSaveProxy);
				msg.error(ISA_R.ams_failedSaveProxy + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				alert(ISA_R.ams_failedSaveProxy);
				msg.error(ISA_R.ams_failedSaveProxy + getErrorMessage(t));
			}
		};
		AjaxRequest.invoke(url, opt);
	}

	function replaceUndefinedValue(val, rep, flg) {
		var value = val;
		value = (value != undefined ? value : rep);
		value = ((!flg || value != "") ? value : rep);
		return value;
	}
};
ISA_ProxyConf.updateProxyConfAttr = function( caseConfigItem,attrName,attrValue ) {
	var url = adminHostPrefix + "/services/proxyConf/updateProxyConf";
	var opt = {
		method: 'post',
		contentType: "application/json",
		postBody: Object.toJSON([
			caseConfigItem.elementName,
			caseConfigItem.id,
			attrName,
			attrValue
		]),
		asynchronous:true,
		onSuccess: function(response){
		},
		onFailure: function(t) {
			alert(ISA_R.ams_failedSaveProxy);
			msg.error(ISA_R.ams_failedSaveProxy + t.status + " - " + t.statusText);
		},
		onException: function(r, t){
			alert(ISA_R.ams_failedSaveProxy);
			msg.error(ISA_R.ams_failedSaveProxy + getErrorMessage(t));
		}
	};
	AjaxRequest.invoke(url, opt);
}
ISA_ProxyConf.updateProxyConfHeaders = function( proxyConf ) {
	var url = adminHostPrefix + "/services/proxyConf/updateProxyConfHeaders";
	var opt = {
		method: 'post' ,
		contentType: "application/json",
		postBody: Object.toJSON([
			ISA_Admin.replaceUndefinedValue( proxyConf.id ),
			proxyConf.headers,
			proxyConf.sendingcookies
		]),
		asynchronous:true,
		onSuccess: function(response){
		},
		onFailure: function(t) {
			alert(ISA_R.ams_failedSaveProxy);
			msg.error(ISA_R.ams_failedSaveProxy + t.status + " - " + t.statusText);
		},
		onException: function(r, t){
			alert(ISA_R.ams_failedSaveProxy);
			msg.error(ISA_R.ams_failedSaveProxy + getErrorMessage(t));
		}
	};
	AjaxRequest.invoke(url, opt);
}
ISA_ProxyConf.HeaderConfigPane = Class.create();
ISA_ProxyConf.HeaderConfigPane.showModal = function( caseConfig ) {
	var container = document.createElement("div");
	container.paddingTop = container.marginTop = "1em";

	var headerConfigPane = new ISA_ProxyConf.HeaderConfigPane( caseConfig );
	container.appendChild( headerConfigPane.build() );
	var div = document.createElement("div");
	div.style.textAlign = "center";

	var okButton = document.createElement("input");
	okButton.type = "button";
	okButton.value = ISA_R.alb_save;
	div.appendChild( okButton );

	Event.observe( okButton,"click",
		ISA_ProxyConf.HeaderConfigPane.closeModal.bind( false,caseConfig,headerConfigPane ));

	var cancelButton = document.createElement("input");
	cancelButton.type = "button";
	cancelButton.value = ISA_R.alb_cancel;
	div.appendChild( cancelButton );

	Event.observe( cancelButton,"click",caseConfig.headersModal.close.bind( caseConfig.headersModal ));

	container.appendChild( div );

	return container;
}
ISA_ProxyConf.HeaderConfigPane.closeModal = function( caseConfig,headerConfigPane ) {
	ISA_Admin.isUpdated = true;
	var allowAllHeader = headerConfigPane.isAllowAll();
	caseConfig.headers = ( allowAllHeader ? null : headerConfigPane.getResults() );
	if((headerConfigPane.allCookies||headerConfigPane.getSendingCookies().length>0)&&caseConfig.headers!=null){
		caseConfig.headers.push("Cookie");
	}
	caseConfig.sendingcookies=headerConfigPane.getSendingCookies();
	caseConfig.sendallcookies = headerConfigPane.getAllCookies();
	ISA_ProxyConf.updateProxyConfHeaders( caseConfig );

	caseConfig.headersModal.close();
}
ISA_ProxyConf.HeaderConfigPane.prototype = {
	initialize: function( caseConfig ) {
		this.caseConfig = caseConfig;
	},
	build: function() {
		var container = this.container = document.createElement("div");

		var allowAllRadio = this.allowAllRadio = ISA_Admin.createBaseRadio("allowAll");
		allowAllRadio.id = "allow_all";
		Event.observe( allowAllRadio,"click",this.onAllowModeChanged.bind( this ));
		container.appendChild( this.wrapLabel("span",allowAllRadio,ISA_R.alb_sendAllHeader,{"padding-right":"10px"}) );

		var detailRadio = this.detailRadio = ISA_Admin.createBaseRadio("allowAll");
		detailRadio.id = "allow_detail";
		Event.observe( detailRadio,"click",this.onAllowModeChanged.bind( this))

		var allowAll = !this.caseConfig.headers;
		if( allowAll ) {
			allowAllRadio.checked = allowAllRadio.defaultChecked = true;
		} else {
			detailRadio.checked = detailRadio.defaultChecked = true;
		}

		var detailDiv = this.wrapLabel("span",detailRadio,ISA_R.alb_selectHeader,{});
		detailDiv.style.marginTop = detailDiv.style.marginBottom = "1em"
		container.appendChild( detailDiv );

		var detailPane = this.detailPane = document.createElement("div");
		detailPane.id="allow_detail_pane";
		detailPane.style.margin = "1em";
		detailDiv.appendChild( detailPane );
		var headers = ( this.caseConfig.headers ||[] ).concat([]);
		var sendingcookies = ( this.caseConfig.sendingcookies ||[] ).concat([]);
		var allcookies = ((headers.indexOf("Cookie")>=0)&& (sendingcookies.length<=0));
		for( var i=0;i<ISA_ProxyConf.HeaderConfigPane.presets.length;i++ ) {
			var category = ISA_ProxyConf.HeaderConfigPane.presets[i];

			detailPane.appendChild( this.buildHeadersCategoryPane( headers,category,allowAll ));
			headers = headers.without( category.headers );
		}
		this.sendingCookies=sendingcookies;
		this.allCookies =allcookies;
		detailPane.appendChild( this.buildSendingCookiesCustomizePane(sendingcookies,allcookies ));
		detailPane.appendChild( this.buildHeadersCustomizePane( headers ));
		this.renderCheckControl( detailPane );
		this.onAllowModeChanged( );


		return container;
	},
	wrapLabel: function( tag,input,text,textStyles ) {
		var laber = $( document.createElement("label"));
		laber.setAttribute("for",input.id );
		laber.appendChild( document.createTextNode( text ));
		laber.setStyle( textStyles || {} );

		var c = document.createElement( tag );
		c.appendChild( input );
		c.appendChild( laber );

		return c;
	},
	isAllowAll: function() {
		return this.allowAllRadio.checked && !this.detailRadio.checked;
	},
	onAllowModeChanged: function() {
		var allowAll = this.isAllowAll();
		var detailDiv = this.detailPane;
		var presets = ISA_ProxyConf.HeaderConfigPane.presets.headers;
		$A( detailDiv.getElementsByTagName("input")).each( function( input ) {
			input.disabled = allowAll;
			if( allowAll && input.headerName ) {
				if( !presets.contains( input.headerName )) {
					this.removeHeader( input.headerName );
				} else {
					input.checked = true;
				}
			} else if( ISA_ProxyConf.defaultSendHedaers.contains( input.headerName ) ){
				input.disabled = true;
				input.checked = false;
			}
			if(input.id.indexOf("cookie_")==0){
				if(allowAll){
					if(input.id=="cookie_sendall") input.checked= input.defaultChecked =true;
					this.sendAllCookie(this.sendingCookies);
				}else{
					if(this.allCookies){
						if(input.id=="cookie_sendall") input.checked=input.defaultChecked =true;
						this.sendAllCookie(this.sendingCookies);
					}else if(this.sendingCookies.length>0){
						if(input.id=="cookie_specify") input.checked=input.defaultChecked =true;
						this.specifySendingCookies();
					}else{
						if(input.id=="cookie_notsending") input.checked=input.defaultChecked =true;
						this.sendNoCookie(this.sendingCookies);
					}
				}
			}
		}.bind( this ));
	},
	renderCheckControl: function( container ) {
		var checkControl = document.createElement("div");
		checkControl.style.textAlign = "center";

		var checkAllButton = document.createElement("input");
		checkAllButton.type = "button";
		checkAllButton.value = ISA_R.alb_checkAllItems;
		checkControl.appendChild( checkAllButton );
		Event.observe( checkAllButton,"click",this.checkAll.bind( this ));

		var uncheckAllButton = document.createElement("input");
		uncheckAllButton.type = "button";
		uncheckAllButton.value = ISA_R.alb_uncheckAllItems;
		checkControl.appendChild( uncheckAllButton );
		Event.observe( uncheckAllButton,"click",this.uncheckAll.bind( this ));

		container.appendChild( checkControl );
	},
	headerStyle: {
		padding: 0,
		margin: 0,
		width: "15em",
		cssFloat: "left",
		styleFloat: "left"
	},
	categoryStyle: {
		listStyleImage: "none",
		listStylePosition: "outside",
		listStyleType: "none",
		margin: 3,
		padding: 0
	},
	lastLiStyle: {
		padding: 0,
		margin: 0,
		width: "15em",
		clear: "both"
	},
	buildHeadersCategoryPane: function( headers,category,defaultChecked ) {
		var div = document.createElement("div");
		div.style.width = "100%";
		div.style.height = "auto";
		var ul = $( document.createElement("ul"));
		ul.setStyle( this.categoryStyle );

		var headerNames = category.headers;

		headerNames.sort();
		for( var i=0;i<headerNames.length;i++){
			var header = headerNames[i];
			var checked = !( headers.indexOf( header ) < 0 ) || defaultChecked;
			if( checked )
				headers.remove( header );
			
			if(!(header.toLowerCase()=="cookie")){
				var li = $( document.createElement("li"));
				if(i == headerNames.length-1){
					li.setStyle(this.lastLiStyle);
				}else{
					li.setStyle( this.headerStyle );
				}
				ul.appendChild( li );
				var checkBox = this.buildHeaderCheck( li,header,checked,false );
				
			}
		}
		div.appendChild(ul);
		return this.wrapFieldSet( category.name,div );
	},
	wrapFieldSet: function( text,contents ) {
		var fieldSet = document.createElement("div");
		fieldSet.className = "modalConfigSet";

		var legend = document.createElement("p");
		legend.className = "modalConfigSetHeader";
		legend.appendChild( document.createTextNode( text ));
		fieldSet.appendChild( legend );

		
		if( !contents.length ) contents = [contents];
		for( var i=0;i<contents.length;i++ )
			fieldSet.appendChild( contents[i] );
		return fieldSet;
	},
	buildHeaderCheck: function( container,header,checked,cookie ) {
		var checkbox = document.createElement("input");
		if(cookie){
			checkbox.id = "sending_cookiename_"+header.toLowerCase();
		}else checkbox.id = "proxyConf_headers_"+header.toLowerCase();

		checkbox.type = "checkbox";
		checkbox.defaultChecked = checked;
		checkbox.headerName = header;
		if(ISA_ProxyConf.defaultSendHedaers.contains( header ) ){
			checkbox.disabled = true;
			checkbox.defaultChecked = false;
		}

		var laber = document.createElement("label");
		laber.appendChild( document.createTextNode( header ) );
		laber.setAttribute("for",checkbox.id );
	
		container.appendChild( checkbox );
		container.appendChild( laber );
		return checkbox;
	},
	buildHeadersCustomizePane: function( headers ) {
		var ul = this.customizeHeaderUl = $( document.createElement("ul") );
		ul.setStyle( this.categoryStyle );

		headers.sort();
		for( var i=0;i<headers.length;i++ ) {
			this.addHeader( headers[i] );
		}

		var div = document.createElement("div");
		div.style.clear = "both";

		var input = this.customizeHeaderInput = document.createElement("input");
		div.appendChild( input );
		var addButton = document.createElement("input");
		addButton.type = "button";
		addButton.value = ISA_R.alb_add;
		div.appendChild( addButton );

		Event.observe( addButton,"click",this.onAddButtonClicked.bind( this ));

		return this.wrapFieldSet( ISA_R.alb_originalHTTPHeader,[ul,div] );
	},
	buildSendingCookiesCustomizePane: function(sendingcookies,allcookies ) {
		var ul2 = this.sendingCookieUl = $( document.createElement("ul") );
		ul2.setStyle( this.categoryStyle );
		var allCookieRadio = this.allCookieRadio = ISA_Admin.createBaseRadio("sending_cookie");
		allCookieRadio.id = "cookie_sendall";
		ul2.appendChild( this.wrapLabel("span",allCookieRadio,ISA_R.alb_sendAllCookies,{"padding-right":"10px"}) );
		
		var noCookieRadio = this.noCookieRadio = ISA_Admin.createBaseRadio("sending_cookie");
		noCookieRadio.id = "cookie_notsending";
		ul2.appendChild( this.wrapLabel("span",noCookieRadio,ISA_R.alb_sendNoCookie,{"padding-right":"10px"}) );
		
		var specifyCookieRadio = this.specifyCookieRadio = ISA_Admin.createBaseRadio("sending_cookie");
		specifyCookieRadio.id = "cookie_specify";
		ul2.appendChild( this.wrapLabel("span",specifyCookieRadio,ISA_R.alb_specifySendingCookies,{"padding-right":"10px"}) );
		Event.observe( noCookieRadio,"click",this.sendNoCookie.bind(this,this.sendingCookies));
		Event.observe( allCookieRadio,"click",this.sendAllCookie.bind(this,this.sendingCookies));	
		Event.observe( specifyCookieRadio,"click",this.specifySendingCookies.bind(this));		
		sendingcookies.sort();
		for( var i=0;i<sendingcookies.length;i++ ) {
			this.addSendingCookie( sendingcookies[i] );
		}

		var div2 = this.div2 =document.createElement("div");
		div2.style.clear = "both";

		var input2 = this.customizeHeaderInput2 = document.createElement("input");
		input2.id="cookie_add_sending";
		div2.appendChild( input2 );
		var addButton2 = document.createElement("input");
		addButton2.id="cookie_add_sending_button";
		addButton2.type = "button";
		addButton2.value = ISA_R.alb_add;
		div2.appendChild( addButton2 );
		Event.observe( addButton2,"click",this.onAddCookieButtonClicked.bind( this ));

		
		return this.wrapFieldSet( "Cookie",[ul2,div2] );
	},
	onAddButtonClicked: function( e ) {
		var header = String( this.customizeHeaderInput.value ).strip();
		if( !header || header.length == 0 )
			return;

		for( var i=0;i<header.length;i++ ) {
			var c = header.charAt(i);
			var code = c.charCodeAt(0);
			if( ( !( 31 < code && code < 127 )&&( c != "\t" && c != " ") ) ||
				/[\(\)\<\>\@\,\;\:\\"\/\[\]\?\=\{\}\x00-\x1f\x20\x7f]/.test( c ) ) {
				alert(ISA_R.ams_invalidCharHeader+"\n"+ISA_R.ams_invalidCharHeader2+
					ISA_R.getResource(ISA_R.ams_invalidCharHeader3, ["()<>@,;:\\\"/[]?={}"]));
				return;
			}
		}

		this.customizeHeaderInput.value = "";

		this.addHeader( header );
	},
	onAddCookieButtonClicked: function( e ) {
		var cookie = String( this.customizeHeaderInput2.value ).strip();
		if( !cookie || cookie.length == 0 )
			return;

		for( var i=0;i<cookie.length;i++ ) {
			var c = cookie.charAt(i);
			var code = c.charCodeAt(0);
			if( ( !( 31 < code && code < 127 )&&( c != "\t" && c != " ") ) ||
				/[\(\)\<\>\@\,\;\:\\"\/\[\]\?\=\{\}\x00-\x1f\x20\x7f]/.test( c ) ) {
				alert(ISA_R.ams_invalidCharHeader+"\n"+ISA_R.ams_invalidCharHeader2+
					ISA_R.getResource(ISA_R.ams_invalidCharHeader3, ["()<>@,;:\\\"/[]?={}"]));
				return;
			}
		}

		this.customizeHeaderInput2.value = "";
		this.sendingCookies.push(cookie);
		
		this.addSendingCookie( cookie );
	},
	addHeader: function( header ) {
		var c = $("proxyConf_headers_"+header.toLowerCase() );
		if(ISA_ProxyConf.defaultSendHedaers.any(function(d){return d.toLowerCase() == header.toLowerCase()})){
			return;
		}
		if( c )  return c.checked = true;

		var li = $( document.createElement("li") );
		li.setStyle( this.headerStyle );
		this.customizeHeaderUl.appendChild( li );


		var checkbox = this.buildHeaderCheck( li,header,true,false );
		checkbox.style.visibility = "hidden";
		var icon = $( document.createElement("a") );
		icon.setStyle({
			display: "inline-block",
			width: "16px",
			height: "16px",
			marginLeft: "4px",
			background: "url("+imageURL+"trash.gif)"
		});
		icon.href = "javascript:void(0)";

		Event.observe( icon,"click",this.removeHeader.bind( this,header ));

		li.appendChild( icon );
	},
	addSendingCookie: function( cookie ) {
		var c = $("sending_cookiename_"+cookie.toLowerCase() );
		if(ISA_ProxyConf.defaultSendHedaers.any(function(d){return d.toLowerCase() == cookie.toLowerCase()})){
			return;
		}
		if( c )  return c.checked = true;

		var li = $( document.createElement("li") );
		li.setStyle( this.headerStyle );
		this.sendingCookieUl.appendChild( li );


		var checkbox = this.buildHeaderCheck( li,cookie,true,true );
		checkbox.style.visibility = "hidden";
		var icon = $( document.createElement("a") );
		icon.setStyle({
			display: "inline-block",
			width: "16px",
			height: "16px",
			marginLeft: "4px",
			background: "url("+imageURL+"trash.gif)"
		});
		icon.href = "javascript:void(0)";

		Event.observe( icon,"click",this.removeSendingCookie.bind( this,cookie ));

		li.appendChild( icon );
	},
	removeHeader: function( header, e ) {
		if(e) Event.stop(e);
		
		var c = $("proxyConf_headers_"+header.toLowerCase() );
		if( !c )  return;

		c.parentNode.parentNode.removeChild( c.parentNode );
	},
	removeSendingCookie: function( cookie, e ) {
		if(e) Event.stop(e);
		
		var c = $("sending_cookiename_"+cookie.toLowerCase() );
		if( !c )  return;

		c.parentNode.parentNode.removeChild( c.parentNode );
		this.sendingCookies.without(cookie);
	},
	getAllCheckbox: function( ignoreDisabled ) {
		return $A( this.container.getElementsByTagName("input")).findAll( function( input ) {
			if(!ISA_ProxyConf.defaultSendHedaers.contains( input.headerName ))
				return ( input.type == "checkbox")&&( !ignoreDisabled || !input.disabled );
		})
	},
	getResults: function() {
		return this.getAllCheckbox().collect( function( input ) {
			if( input.checked && (input.id.match("proxyConf_headers_")))
				return input.headerName;
		}).compact();
	},
	getSendingCookies: function() {
		return this.getAllCheckbox().collect( function( input ) {
			if( input.checked && (input.id.match("sending_cookiename_")))
				return input.headerName;
		}).compact();
	},
	checkAll: function() {
		this.getAllCheckbox().each( function( input ) {
			input.checked = true;
		});	
		var a = document.getElementById("cookie_sendall");
		a.checked = true;
		this.sendAllCookie(this.sendingCookies);
	},
	uncheckAll: function() {
		this.getAllCheckbox().each( function( input ) {
			input.checked = false;
		});

		var presets = ISA_ProxyConf.HeaderConfigPane.presets.headers;
		$A( this.detailPane.getElementsByTagName("input")).each( function( input ) {
			if( input.headerName && !presets.contains( input.headerName ) )
				this.removeHeader( input.headerName );
		}.bind( this ));
		var a = document.getElementById("cookie_notsending");
		a.checked = true;
		this.sendNoCookie(this.sendingCookies);
	},
	deleteAllCookies: function(deleteCookie){
		$A( this.div2.getElementsByTagName("input")).each( function( input ) {
			input.disabled = true;
		}.bind( this ));
		if(!deleteCookie) return;
		for( var i=0;i<deleteCookie.length;i++ ) {
			this.removeSendingCookie(deleteCookie[i]);
		}
	},
	sendAllCookie: function(deleteCookie){
		this.allCookies = true;
		this.deleteAllCookies(deleteCookie);
	},
	sendNoCookie: function(deleteCookie){
		this.allCookies = false;
		this.deleteAllCookies(deleteCookie);
	},
	specifySendingCookies: function(){
		$A( this.div2.getElementsByTagName("input")).each( function( input ) {
			input.disabled = false;
		}.bind( this ));
		this.allCookies = false;
	},
	getAllCookies: function(){
		return this.allCookies;
	},
	sendingCookies:[],
	allCookies:null

}
ISA_ProxyConf.HeaderConfigPane.presets = [];

( function() {
	var presets = ISA_ProxyConf.HeaderConfigPane.presets;

	presets.push( {
		name: "HTTP",
		headers: [
	    	"Connection",
	    	"Date",
	    	"Upgrade",
	    	"Via",
	    	"Expect",
	    	"From",
	    	"Max-Forwards",
	    	"Range",
	    	"Referer",
	    	"Te",
	    	"Host",
	    	"Authorization",
	    	"Proxy-Authorization",
	    	"Cookie"
		]
	});
	presets.push( {
		name: "MIME",
		headers: [
	    	"Content-Disposition",
			"Content-Type",
			"Content-Length",
			"Content-Encoding",
			"Transfer-Encoding"
		]
	});
	presets.push( {
		name: "Client",
		headers: [
	    	"Accept",
	    	"Accept-Charset",
	    	"Accept-Language",
	    	"User-Agent"
		]
	});
	presets.push( {
		name: "Cache",
		headers: [
	    	"If-Range",
	    	"If-Unmodified-Since",
	    	"Pragma",
			"Cache-Control",
	    	"If-Match",
	    	"If-Modified-Since",
	    	"If-None-Match"
		]
	});

	var headers = presets.headers = [];
	ISA_ProxyConf.HeaderConfigPane.presets.each( function( category ) {
		for( var i in category.headers ) if( category.headers.hasOwnProperty( i )) {
			headers.push( category.headers[i] );
		}
	});
})();
