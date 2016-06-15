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

/* CommandQueue */
IS_Request = {};
IS_Request.asynchronous = true;
IS_Request.Queue = function (servName, time, disabled, checkDuplicate){
	this.url=hostPrefix + servName;
	var queued=new Array();
	var sent=new Array();

	var isLoading = false;
	
	this.STATUS_QUEUED=-1;
	this.STATE_UNINITIALIZED=0;
	this.STATE_LOADING=1;
	this.STATE_LOADED=2;
	this.STATE_INTERACTIVE=3;
	this.STATE_COMPLETE=4;
	this.STATE_PROCESSED=5;

	this.PRIORITY_NORMAL=0;
	this.PRIORITY_IMMEDIATE=1;


	this.addCommand = function(command){
		if(disabled) return;
		if (this.isCommand(command)){
			if( ["AddLog","UpdateRssMeta"].contains( command.type ) && accessLogEntry === false ) {
				return;
			}
			
			queued.push(command);
			if (command.priority==this.PRIORITY_IMMEDIATE || time <= 0){
				this.fireRequest();
			}
		}
	}

	this.fireRequest = function(){
		//if (queued.length==0 || isLoading || this.freeze || disabled){
		if ( isLoading || this.freeze || disabled){
			return;
		}
		isLoading = true;
		var data="<?xml version=\"1.0\"?><commands buildVersion=\"" + IS_Portal.buildVersion + "\">";
		var registeredCmd = new Object();
		var cmdList = new Array();
		for(var i = queued.length; i > 0;  i--){
			var cmd=queued[(i - 1)];
			if (this.isCommand(cmd)){
				if(!checkDuplicate || !registeredCmd[cmd.id]){
					registeredCmd[cmd.id]=cmd;
					cmdList.push(cmd);
				}
			}
		}
		for(var i = cmdList.length; i > 0; i--){
			var cmd=cmdList[(i - 1)];
			data+=cmd.toRequestString();
			
			sent[cmd.id]=cmd;
		}
		data +="</commands>";
		
		queued = new Array();
//		IS_Request.send(this.url,"POST",this.onload,data,true);
//		IS_Request.send(this.url,"POST",this.onload.bind(this),data,true);
		function postError(){
			for(var i = 0; i < cmdList.length; i++){
				var cmd = cmdList[i];
				if(!cmd.firstFailed){
					cmd.firstFailed = true;
					queued.push(cmd);
				}else{

					msg.error(IS_R.getResource(IS_R.ms_doubleEntryFailure, [cmd.id]));

				}
			}
			isLoading = false;
		}
				
		var opt = {
			method: 'post',
			postBody: data,
			asynchronous: IS_Request.asynchronous,
			requestHeaders: is_userId ? ["Uid", is_userId] : false,
			contentType: "application/xml",
			onSuccess: this.onload.bind(this),
			onFailure: function(t) {

				msg.warn(IS_R.getResource(IS_R.ms_commandonFailure, [t.status,t.statusText]));
				postError();
			},
			onException: function(r, t){

				msg.warn(IS_R.getResource(IS_R.ms_commandonException, [t]));
				postError();
			}
		};
		
		//new Ajax.Request(this.url, opt);
		AjaxRequest.invoke(this.url, opt);
	}

	this.isCommand = function(obj){
		return (
			obj.id
			&& obj.toRequestString
			&& obj.parseResponse
			&& (obj.id.indexOf("previewWidget_") < 0)
		);
	}

	this.repeat = function(freq){
		if(disabled) return;
		this.unrepeat();
		if (freq>0){
			this.freq=freq;
//			this.repeater=setInterval('this.fireRequest()',freq*1000);
			this.repeater=setInterval(this.fireRequest.bind(this),freq*1000);
		}
	}

	this.unrepeat = function(){
		if (this.repeater){
			clearInterval(this.repeater);
		}
		this.repeater=null;
	}

	this.onload=function(req){
		var xmlDoc=req.responseXML;
		var elDocRoot=xmlDoc.getElementsByTagName("responses")[0];
		try{
    		if (elDocRoot){
    			for(var i=0;i<elDocRoot.childNodes.length && !this.freeze;i++){
    				elChild=elDocRoot.childNodes[i];
    				if (elChild.nodeName=="command"){
    					var attrs=elChild.attributes;
    					var id=attrs.getNamedItem("id").value;
    					var status=attrs.getNamedItem("status").value;
    					
    					if(status == "updated"){
                            this.freeze = true;
                            displayRelaodMessage(IS_R.lb_server_updated_reload, "information");
                            return;
    					}
    					else if(id == '' || status != 'ok') {
    						var reason = attrs.getNamedItem("message").value;
    						displayRelaodMessage(IS_R.ms_custmizeFailedReload + " - " + reason, "warn");
    						this.freeze = true;
                            return;
    					} else {
    						var command=sent[id];
    						if (command){
    							command.parseResponse(elChild);
    						}
    					}
    				}
    			}
    		}
		}catch(e){
			msg.warn(getText(e));
		}
		isLoading = false;
	}

	this.onerror=function(){
	  alert("problem sending the data to the server");
	}

	this.repeat(time);
	
	function displayRelaodMessage(msg, level){
        var msgListDiv = $jq("#error-msg-bar").empty().show();
        var msgDiv = $jq("<div>").addClass("msg-item " + level).prependTo(msgListDiv);
        $jq("<span>").text(msg).appendTo(msgDiv);
        $jq("<input>").attr("type", "button").val(IS_R.lb_reload)
            .click(function(){
                location.reload(true);
            })
            .appendTo(msgDiv);
        IS_EventDispatcher.newEvent("adjustedMessageBar");
	}
}

/*
 * Form where you enter userID and password for Authentication Widget
 */
IS_Request.createAuthForm = function(elmId, authFormSubmitFunc, isFirstAuth){

	var authDiv = document.createElement('div');
	
	if(!isFirstAuth)
		authDiv.innerHTML = "<div style='color:red;font-size:90%;padding:2px;'>" + IS_R.ms_noPermission + "</div>";
	authDiv.innerHTML += "<div style='font-size:90%;padding:2px;'>" + IS_R.ms_inputCredential + "</div>";
	
	var authFormDiv = document.createElement('div');

	authFormDiv.style.textAlign = 'center';
	authFormDiv.style.padding = '3px';
	var authFormTable = document.createElement('table');
	var authFormTbody = document.createElement('tbody');

	var tr = document.createElement('tr');
	var td = document.createElement('td');
	td.style.textAlign = 'right';
	//User ID
	td.appendChild(document.createTextNode(IS_R.lb_userID));
	tr.appendChild(td);

	td = document.createElement('td');
	td.style.textAlign = 'left';
	var uidInput = document.createElement('input');
	uidInput.id = elmId + "_authUid";
	uidInput.maxLength = 100;
	td.appendChild(uidInput);
	tr.appendChild(td);
	authFormTbody.appendChild(tr);
	
	var tr = document.createElement('tr');
	var td = document.createElement('td');
	td.style.textAlign = 'right';
	//Password
	td.appendChild(document.createTextNode(IS_R.lb_password));
	tr.appendChild(td);

	var td = document.createElement('td');
	var passwdInput = document.createElement("input");
	passwdInput.id = elmId + "_authPasswd";
	passwdInput.type = 'password';
	passwdInput.maxLength = 50;
	td.appendChild(passwdInput);
	tr.appendChild(td);
	authFormTbody.appendChild(tr);
	authFormTable.appendChild(authFormTbody);
	authFormDiv.appendChild(authFormTable);
	
	
	var okInput = document.createElement("input");
	okInput.type = "button";
	//Register
	okInput.value = IS_R.lb_entry;
	IS_Event.observe(okInput, "click", function(){ 
		if(!uidInput.value){
			//Enter user ID
			alert(IS_R.ms_userIdEmpty);
			return;
		}
		IS_Event.unloadCache(elmId + "_authSubmitBtn"); 
		authFormSubmitFunc(); 
	}, false, elmId + "_authSubmitBtn");
	authFormDiv.appendChild(okInput);

	authDiv.appendChild(authFormDiv);
	return authDiv;
}

IS_Request.createModalAuthFormDiv = function(label, element, _callback, isModal, errorMsg){
	var credentialFormDiv = document.createElement('div');
	credentialFormDiv.id = 'credentialFormDiv';
	if(errorMsg)credentialFormDiv.innerHTML =  "<div style='color:red;font-size:90%;padding:2px;'>" + errorMsg + "</div>";
	var credentialFieldSet = document.createElement('fieldSet');
	var credentialLegend = document.createElement('legend');
	//Enter authentication information：
	credentialLegend.appendChild(document.createTextNode(IS_R.ms_inputCredential));
	credentialFieldSet.appendChild(credentialLegend);
	
	var credentialFormTable = document.createElement('table');
	credentialFormTbody = document.createElement('tbody');
	userNameTr = document.createElement('tr');
	userNameLabelTd = document.createElement('td');
	//User ID
	userNameLabelTd.appendChild(document.createTextNode(IS_R.lb_userID));
	userNameTr.appendChild(userNameLabelTd);
	
	userNameInputTd = document.createElement('td');
	var userNameInput = document.createElement("input");
	userNameInput.id = "previewUserNameForm";
	userNameInputTd.appendChild(userNameInput);
	userNameInput.maxLength = 100;
	userNameTr.appendChild(userNameInputTd);
	
	credentialFormTbody.appendChild(userNameTr);
	
	passwordTr = document.createElement('tr');
	passwordLabelTd = document.createElement('td');
	//Password
	passwordLabelTd.appendChild(document.createTextNode(IS_R.lb_password));
	passwordTr.appendChild(passwordLabelTd);
	
	passwordInputTd = document.createElement('td');
	var passwordInput = document.createElement("input");
	passwordInput.id = "previewPasswordForm";
	passwordInput.type = "password";
	passwordInput.maxLength = 50;
	passwordInputTd.appendChild(passwordInput);
	passwordTr.appendChild(passwordInputTd);
	
	credentialFormTbody.appendChild(passwordTr);
	credentialFormTable.appendChild(credentialFormTbody);
	
	credentialFieldSet.appendChild(credentialFormTable);

	var credentialFormBtn = document.createElement("input");
	credentialFormBtn.type = "button";
	credentialFormBtn.value = label;
	credentialFieldSet.appendChild(credentialFormBtn);
	
	var cancelBtn = document.createElement("input");
	cancelBtn.type = "button";
	//Cancel
	cancelBtn.value = IS_R.lb_cancel;
	credentialFieldSet.appendChild(cancelBtn);

	var modal;
	credentialFormDiv.appendChild(credentialFieldSet);
	IS_Event.observe( cancelBtn, 'click', function(){
		if(isModal){
			modal.close();
			//_callback();
		}else{
			element.parentNode.removeChild( credentialFormDiv.parentNode );
			_callback();
		}
		IS_Event.unloadCache("_credentialForm");
	}, false, "_credentialForm");

	var isOK = false;
	IS_Event.observe( credentialFormBtn, 'click', function(){
		isOK = true;
		if(isModal){
			modal.close();
		}else{
			element.parentNode.removeChild( credentialFormDiv.parentNode );
		}
		var authUid = userNameInput.value;
		var authPassword = passwordInput.value;
		if(authPassword)
		  authPassword = rsaPK.encrypt(authPassword);
		_callback(authUid, authPassword);
		//is_processUrlContents(url, displayPreview.bind(this, url), function(){}, ["authType", authType, "authuserid",authUid,"authpassword",authPassword]);
		IS_Event.unloadCache("_credentialForm");
	} , false, "_credentialForm");

	if(isModal){
		function afterCloseFunc(){
			if(!isOK)
				_callback();
		}
		modal = new Control.Modal('', {
			width: 300,
			afterClose: afterCloseFunc
		});
		modal.container.update(credentialFormDiv);
		modal.open();
		element.onclick = function(){};//stopObserving does not listen
	}else{
		var div = document.createElement("span");
		div.style.position = "relative";
		div.style.height = 0;
		div.style.width = 0;
		if( element.nextSibling ) {
			element.parentNode.insertBefore( div,element.nextSibling );
		} else {
			element.parentNode.appendChild( div );
		}
		
		credentialFormDiv.style.position = 'absolute';
		credentialFormDiv.style.backgroundColor = '#eeeeee';
		credentialFormDiv.style.top = -element.offsetHeight -4;
		credentialFormDiv.style.right = 0;
		credentialFormDiv.style.zIndex = 10000;
		
		div.appendChild( credentialFormDiv );
		IS_Event.observe( window.document, 'click', function(e){
			var mouseX = Event.pointerX(e);
			var mouseY = Event.pointerY(e);
			if(!isInObject(mouseX,mouseY,'credentialFormDiv')){
				_callback();
				if( element.parentNode )
					element.parentNode.removeChild( credentialFormDiv.parentNode );
				IS_Event.unloadCache("_credentialForm");
			}
		}, false, "_credentialForm");
	}
	userNameInput.focus();
};

IS_Request.showCredentialList = function(e){
	IS_Event.unloadCache("_authCredentialList");
	
	var window = new Window({
	  className: "alphacube",
	  
	  title: IS_R.lb_credentialList,
	  width:600,
	  height:350,
	  minimizable: false,
	  maximizable: false,
	  resizable: true,
	  showEffect: Element.show,
	  hideEffect: Element.hide,
	  recenterAuto: false,
		//destroyOnClose: true,
	  onClose:function(){
	  },
	  zIndex: 10000
	});

	function getAuthUrlList(_credentialId){
		var authUrlList = [];
		for(tabId in IS_Portal.widgetLists ){
			for(widgetId in IS_Portal.widgetLists[tabId] ) {
				if(IS_Portal.widgetLists[tabId][widgetId].getUrlByCredentialId){
					var result = IS_Portal.widgetLists[tabId][widgetId].getUrlByCredentialId(_credentialId);
					authUrlList = result.urlList.concat(authUrlList);
				}
			}
		}
		return authUrlList;
	}
	
	function getGadgetTitleByServiceName(serviceName, urls){
		var titleList = [];
		for(var i=0;i<urls.length;i++){
			for(tabId in IS_Portal.widgetLists ){
				for(widgetId in IS_Portal.widgetLists[tabId] ) {
					var widget = IS_Portal.widgetLists[tabId][widgetId];
					if(widget.widgetType == "g_" + urls[i])
						titleList.push(IS_Widget.WidgetHeader.getTitle(widget));
				}
			}
		}
		return titleList;
	}
	
	var opt = {
	  method: 'get',
	  asynchronous: true,
	  contentType: "application/xml",
	  onSuccess: function(req, obj){
		  var credentialList = eval(req.responseText);
		  var credentialListDiv = document.createElement('div');
		  credentialListDiv.className = 'authCredentialInfoList';
		  
		  for(var i = 0; i < credentialList.length; i++){
			  
			  var table = document.createElement('table');
			  table.className = 'authCredentialInfoTable';
			  var tbody = document.createElement('tbody');
			  
			  var tr = document.createElement('tr');
			  var th = document.createElement('th');
			  th.colSpan = 2;
			  var headerLeft = document.createElement('span');
			  headerLeft.style.cssFloat = 'left';
			  headerLeft.style.styleFloat = 'left';
			  headerLeft.className = 'authCredentialInfoTitle';
			  //Authentication information
			  headerLeft.appendChild(document.createTextNode(IS_R.lb_authInfo + (i + 1) ));
			  th.appendChild(headerLeft);

			  tr.appendChild(th);
			  
			  tbody.appendChild(tr);
			  
			  var tr = document.createElement('tr');
			  tr.style.clear = 'both';
			  var td = document.createElement('td');
			  td.className = 'authCredentialListLightTd';
			  //Authentication type
			  td.appendChild(document.createTextNode(IS_R.lb_authType));
			  tr.appendChild(td);
			  var td = document.createElement('td');
			  td.appendChild(document.createTextNode(
				  credentialList[i].authType
				  ));
			  tr.appendChild(td);
			  tbody.appendChild(tr);
			  
			  if(credentialList[i].authType == "OAuth"){
				  var tr = document.createElement('tr');
				  var td = document.createElement('td');
				  td.width = '30%';
				  td.className = 'authCredentialListLightTd';
				  
				  td.appendChild(document.createTextNode(IS_R.lb_oauthServiceName));
				  tr.appendChild(td);
				  var td = document.createElement('td');
				  td.appendChild(document.createTextNode(credentialList[i].service_name));
				  tr.appendChild(td);
				  
				  var deleteDiv = document.createElement('div');
				  deleteDiv.className = "authCredentialDelete";
				  var deleteIcon = document.createElement('img');
				  deleteIcon.src = imageURL + 'trash.gif';
				  deleteDiv.appendChild(deleteIcon);
				  th.appendChild(deleteDiv);
				  var deleteLabel = document.createElement("span");
				  deleteLabel.appendChild(document.createTextNode(IS_R.lb_delete));
				  deleteDiv.appendChild(deleteLabel);

				  IS_Event.observe(deleteDiv, "click", function(serviceName, authTable){
					if(confirm(IS_R.getResource(IS_R.ms_confirmOAuthDelete, [serviceName]))){
						
						IS_Request.removeOAuthToken(serviceName, function(authTable){
							Element.remove($(authTable));
						}.bind(this, authTable));
					}
				  }.bind(this, credentialList[i].service_name, table), false, "_authCredentialList");
				  
				  tbody.appendChild(tr);
				  
				  var tr = document.createElement('tr');
				  var td = document.createElement('td');
				  td.className = 'authCredentialListLightTd';
				  
				  td.appendChild(document.createTextNode(IS_R.lb_description));
				  tr.appendChild(td);
				  var td = document.createElement('td');
				  var description = escapeHTMLEntity(credentialList[i].description);
				  td.innerHTML = description.replace(/(\r\n|\n|\r)/g, '<br/>');
				  tr.appendChild(td);
				  tbody.appendChild(tr);
				  
				  var tr = document.createElement('tr');
				  var td = document.createElement('td');
				  td.className = 'authCredentialListLightTd';
				  td.appendChild(document.createTextNode(IS_R.lb_gadgetsList));
				  tr.appendChild(td);
				  tbody.appendChild(tr);
				  
				  var titleList = getGadgetTitleByServiceName(credentialList[i].service_name, credentialList[i].gadget_urls);
				  
				  if(titleList.length == 0){
				  	  IS_Request.removeOAuthToken(credentialList[i].service_name);
					  continue;
				  }
				  
				  td.rowSpan = (titleList.length == 0) ? 1 : titleList.length;
				  for(var j = 0; j < titleList.length;j++){
					  var td = document.createElement('td');
					  td.appendChild(document.createTextNode(titleList[j]));
					  tr.appendChild(td);
					  tbody.appendChild(tr);
					  var tr = document.createElement('tr');
				  }
			  }else{
				  var tr = document.createElement('tr');
				  var td = document.createElement('td');
				  td.width = '30%';
				  td.className = 'authCredentialListLightTd';
				  //User ID
				  td.appendChild(document.createTextNode(IS_R.lb_userID));
				  tr.appendChild(td);
				  var td = document.createElement('td');
				  td.appendChild(document.createTextNode(
					  credentialList[i].authUid
					  ));
				  tr.appendChild(td);
				  tbody.appendChild(tr);
				  
				  var tr = document.createElement('tr');
				  var td = document.createElement('td');
				  td.className = 'authCredentialListLightTd';
				  //Password
				  td.appendChild(document.createTextNode(IS_R.lb_password));
				  tr.appendChild(td);
				  var td = document.createElement('td');
				  td.appendChild(document.createTextNode("*******"));
	
				  tr.appendChild(td);				  

				  tbody.appendChild(tr);
				  
				  var tr = document.createElement('tr');
				  var td = document.createElement('td');
				  td.className = 'authCredentialListLightTd';
				  td.appendChild(document.createTextNode(IS_R.lb_urlList));
				  tr.appendChild(td);
				  tbody.appendChild(tr);
				  
				  var authUrlList = getAuthUrlList(credentialList[i].id);
				  authUrlList = authUrlList.uniq();
				  
				  if(credentialList[i].sysNum == 0 && authUrlList.length == 0){
					  IS_Request.removeCredential(credentialList[i].id);
					  continue;
				  }
				  
				  td.rowSpan = (authUrlList.length == 0) ? 1 : authUrlList.length;
				  for(var j = 0; j < authUrlList.length;j++){
					  var td = document.createElement('td');
					  td.appendChild(document.createTextNode(authUrlList[j]));
					  tr.appendChild(td);
					  tbody.appendChild(tr);
					  var tr = document.createElement('tr');
				  }
			  }
			  
			  table.appendChild(tbody);
			  credentialListDiv.appendChild(table);

		  }
		  window.setContent(credentialListDiv);
	  },
	  onException: function(r, t){
		  console.log(t);
	  }
	};
	
	IS_Event.observe($('authCredentialListIcon'), 'click', function(e){
		
		
		AjaxRequest.invoke(hostPrefix + "/credsrv?command=list", opt);
		
		if(e) {//Event is passed and comes only when pressing recycle bin icon
			window.showCenter();
		} else {
			window.centered = false;
			window.show();
		}
	}, true, "_authCredentialList");
	
}

IS_Request.removeCredential = function(authCredentialId){
	var opt = {
	  method: 'post',
	  asynchronous: true,
	  postBody: "command=del&id=" + authCredentialId,
	  onSuccess:function(req, obj){
		  msg.info("delete authCredential " + authCredentialId + " from " + self.id);
	  },
	  onException:function(req, obj){
		  msg.error(["Error:",obj]);
	  },
	  onComplete:function(){}
	}
	AjaxRequest.invoke(hostPrefix + "/credsrv", opt, self.id);
}

IS_Request.removeOAuthToken = function(serviceName, callback){
	callback = callback? callback : function(){};
	
	var opt = {
		method: 'post',
		asynchronous: true,
		postBody: "command=del_oauth&service_name=" + serviceName,
		onSuccess:callback,
		onException:function(req, obj){
			console.log(["Error:",obj]);
		}
	}
	AjaxRequest.invoke(hostPrefix + "/credsrv", opt);
}
