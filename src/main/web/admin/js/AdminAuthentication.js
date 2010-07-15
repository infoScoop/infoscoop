ISA_Authentication = {
	build: function(){
		
		var container = document.getElementById("authentication");
		var tabUl =
		  $.DIV({id:"tabContainer"},
				$.UL({id:"oauth_setting_tabs", className:"subsection_tabs tabs"},
					 $.LI({},
						  $.A({id:"oauth_consumer_tab",href:"#oauth_consumer", className:"tab"},
							  $.SPAN({className:"title"},ISA_R.alb_oauthConsumerSettings))
							),
					 $.LI({},
						  $.A({id:"oauth_container_tab",href:"#oauth_container", className:"tab"},
							  $.SPAN({className:"title"},ISA_R.alb_oauthContainerCertificate))
							)
					   )
				  );
		container.appendChild(tabUl);

		container.appendChild(
			$.DIV({style:"clear:both;padding:5px;"},
				  $.DIV({id:"oauth_consumer" }),
				  $.DIV({id:"oauth_container"})));
		
		this.controlTabs = new Control.Tabs("oauth_setting_tabs", {
			beforeChange: function(old_container, new_container){
				Element.removeClassName(old_container.id + "_tab","selected");
				Element.addClassName(new_container.id+ "_tab", "selected");
			}.bind(this)
		});
		this._displayConsumer();
		this._displayContainerCert();

		this.currentModal = new Control.Modal( false, {
			contents: ISA_R.ams_applyingChanges,
			opacity: 0.2,
			overlayCloseOnClick: false
		});
	},
	
	_addConsumerSetting:function(){
		
		var authenticationContentTable = $("authentication_contentTable");
		authenticationContentTable.firstChild.appendChild(
			this._createRow({'gadget_url':'http://','service_name':'','consumer_key':'','consumer_secret':'','signature_method':'HMAC-SHA1'}, new Date().getTime())
			);
	},

	_saveConsumerList:function(){
		var trList = $("authentication_contentTable").getElementsByTagName('tr');

		var consumerList = [];
		for(var i = 1; i < trList.length; i++){
			var tr = trList[i];
			if(this._validateUrl(i, tr.id))return;
			if(this._validateServiceName(i, tr.id))return;
			var gadgetUrl = $F(tr.id + '_gadget_url');
			var serviceName = $F(tr.id + '_service_name');
			var consumerKey = $F(tr.id + '_consumer_key');
			var consumerSecret = $F(tr.id + '_consumer_secret');
			var signatureMethod = $F(tr.id + '_signature_method');
			if(signatureMethod == 'HMAC-SHA1'){
				if(this._validateConsumerKey(i, tr.id))return;
				if(this._validateConsumerSecret(i, tr.id))return;
			}
			consumerList.push({
				gadgetUrl:gadgetUrl,
				serviceName:serviceName,
				consumerKey:consumerKey,
				consumerSecret:consumerSecret,
				signatureMethod:signatureMethod
			})
		}

		var url = findHostURL() + "/services/authentication/saveOAuthConsumerList";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			asynchronous:true,
			postBody: Object.toJSON([Object.toJSON(consumerList)]),
			onSuccess: function( resp ) {
				ISA_Admin.isUpdated = false;
				this.currentModal.update(ISA_R.ams_changeUpdated);
				setTimeout( function() {
					this.currentModal.close();
				}.bind( this ),500 );
				
				this._displayConsumer();
			}.bind( this ),
			onFailure: function(r, t) {
				alert(ISA_R.ams_failedToUpdateOAuthSettings);
				msg.error(ISA_R.ams_failedToUpdateOAuthSettings + t.status + " - " + t.statusText);
			},
			onException: function(r,t){
				alert(ISA_R.ams_failedToUpdateOAuthSettings);
				msg.error(ISA_R.ams_failedToUpdateOAuthSettings + getErrorMessage(t));
			},
			onComplete: function(){
				this.currentModal.close();
			}.bind(this)
		};
		this.currentModal.open();
		AjaxRequest.invoke(url, opt);
		
	},
	
	_validateUrl: function(index, elementId) {
		var urlInput = $(elementId + '_gadget_url');
		var newUrl = urlInput.value;
		var error = false;
		if( newUrl.length == 0 ) {
			error = ISA_R.ams_requiredItem;
		} else if(error = IS_Validator.validate(newUrl, {maxBytes:1024, regex:'^http(s)?://.*'})){
		}
		if(error)
		  alert(ISA_R.getResource(ISA_R.ams_invalidOAuthSetting, [index, ISA_R.alb_gadgetUrl]) + error);
		return error;
	},
	_validateServiceName: function(index, elementId) {
		var urlInput = $(elementId + '_service_name');
		var serviceName = urlInput.value;
		var error = false;
		if( serviceName.length == 0 ) {
			error = ISA_R.ams_requiredItem;
		} else if(error = IS_Validator.validate(serviceName, {maxBytes:255})){
		}
		if(error)
		  alert(ISA_R.getResource(ISA_R.ams_invalidOAuthSetting, [index, ISA_R.alb_oauthServiceName]) + error);
		return error;
	},
	_validateConsumerKey: function(index, elementId) {
		var urlInput = $(elementId + '_consumer_key');
		var serviceName = urlInput.value;
		var error = false;
		if( serviceName.length == 0 ) {
			error = ISA_R.ams_requiredItem;
		} else if(error = IS_Validator.validate(serviceName, {maxBytes:255})){
		}
		if(error)
		  alert(ISA_R.getResource(ISA_R.ams_invalidOAuthSetting, [index, ISA_R.alb_oauthConsumerKey]) + error);
		return error;
	},
	_validateConsumerSecret: function(index, elementId) {
		var urlInput = $(elementId + '_consumer_secret');
		var serviceName = urlInput.value;
		var error = false;
		if( serviceName.length == 0 ) {
			error = ISA_R.ams_requiredItem;
		} else if(error = IS_Validator.validate(serviceName, {maxBytes:255})){
		}
		if(error)
		  alert(ISA_R.getResource(ISA_R.ams_invalidOAuthSetting, [index, ISA_R.alb_oauthConsumerSecret]) + error);
		return error;
	},
	
	_displayConsumer: function(){
		var url = findHostURL() + "/services/authentication/getOAuthConsumerListJson";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			asynchronous:true,
			onSuccess: function( resp ) {
				ISA_Admin.isUpdated = false;
				this._renderConsumer(eval( resp.responseText ));
			}.bind( this ),
			onFailure: function(r, t) {
				alert(ISA_R.ams_failedToGetOAuthSettings);
				msg.error(ISA_R.ams_failedToGetOAuthSettings + t.status + " - " + t.statusText);
			},
			onException: function(r,t){
				alert(ISA_R.ams_failedToGetOAuthSettings);
				msg.error(ISA_R.ams_failedToGetOAuthSettings + getErrorMessage(t));
			},
			onComplete: function(){
				this.currentModal.close();
			}.bind(this)
		};
		AjaxRequest.invoke(url, opt);
		
	},
	
	_renderHeader: function(container){
		var controlDiv = document.createElement("div");
		controlDiv.style.textAlign = "right";
		
		var commitDiv = ISA_Admin.createIconButton(ISA_R.alb_changeApply, ISA_R.alb_changeApply, "database_save.gif", "right");
		controlDiv.appendChild(commitDiv);
		IS_Event.observe(commitDiv, "click", this._saveConsumerList.bind(this), false, "_adminAuthentication");
		
		var refreshDiv = ISA_Admin.createIconButton(ISA_R.alb_refresh, ISA_R.alb_reloadWithourSaving, "refresh.gif", "right");
		controlDiv.appendChild(refreshDiv);
		IS_Event.observe(refreshDiv, "click", this._displayConsumer.bind(this), false, "_adminAuthentication");
		
		container.appendChild(controlDiv);
		
		var addButton = ISA_Admin.createIconButton(ISA_R.alb_add, ISA_R.alb_add, "add.gif", "left");
		addButton.style.textAlign = "left"
		
		IS_Event.observe(addButton, "click", this._addConsumerSetting.bind(this), false, "_adminAuthentication");
		
		container.appendChild(addButton);
	},
	
	_renderConsumer: function(oauthConsumerList){
		IS_Event.unloadCache("_adminAuthentication");
		
		var container = document.getElementById("oauth_consumer");
		while(container.firstChild)
		  container.removeChild( container.firstChild );
		
		this._renderHeader( container );
		
		var table = ISA_Admin.buildTableHeader(
			[ ISA_R.alb_gadgetUrl, ISA_R.alb_oauthServiceName, ISA_R.alb_oauthSignatureAlgorithm, ISA_R.alb_oauthConsumerKey, ISA_R.alb_oauthConsumerSecret, ISA_R.alb_delete],
			[ '30%', '10%', "10%", "20%", "25%", "5%"]
			);
		table.id = "authentication_contentTable";
		table.className = "proxyConfigList";
		table.style.tableLayout = "fixed"
		//TODO:Function for generating table needs to be arranged
		table.style.borderLeft = "1px solid #EEEEEE";
		table.style.width = "100%";
		
		container.appendChild( table );
		
		var tbody = table.firstChild;
		var this_ = this;
		oauthConsumerList.each( function( consumer, index ) {
			tbody.appendChild( this._createRow( consumer, index ));
			this._displayConsumerKeySecret('oauth_consumer_setting_' + index);
		}.bind(this));
		
	},
	
	_createTextbox: function(id, value){
		return $.INPUT({id: id, value: value, className:'portalAdminInput',style:"width:100%;",
		  onchange:{handler: function(){ ISA_Admin.isUpdated = true; }}
		});
	},

	_displayConsumerKeySecret:function(elementId){
		var signatureMethod = $F(elementId + '_signature_method');
		var element1 = $(elementId + '_consumer_key');
		var element2 = $(elementId + '_consumer_secret');
		if(signatureMethod == 'HMAC-SHA1'){
			element1.disabled = element2.disabled = false;
			element1.style.borderColor = element2.style.borderColor= "#54ce43";
		}else{
			element1.disabled = element2.disabled = true;
			element1.style.borderColor = element2.style.borderColor= "#EEE";
			element1.value = element2.value = '';
		}
	},
	
	_createRow: function( consumer, index ){
		var elementId = 'oauth_consumer_setting_' + index;
		var deleteIcon = ISA_Admin.createIconButton("", ISA_R.alb_delete, "trash.gif");
		var tr = $.TR({id:elementId },
					$.TD({}, this._createTextbox(elementId + '_gadget_url', consumer['gadget_url']) ),
					$.TD({}, this._createTextbox(elementId + '_service_name', consumer['service_name']) ),
					$.TD({},
						 $.SELECT(
							 {id: elementId + '_signature_method',
							   onchange:{handler:this._displayConsumerKeySecret.bind(this, elementId),
								 value:consumer['signature_method']}
							 },
							 $.OPTION({value:'HMAC-SHA1', selected: ('HMAC-SHA1' == consumer['signature_method']) }, 'HMAC-SHA1'),
							 $.OPTION({value:'RSA-SHA1', selected: ('RSA-SHA1' == consumer['signature_method'])}, 'RSA-SHA1')
							   )),
					$.TD({}, this._createTextbox(elementId + '_consumer_key', consumer['consumer_key'] ) ),
					$.TD({}, this._createTextbox(elementId + '_consumer_secret', consumer['consumer_secret'] ) ),
					$.TD({style:"textAlign:center;"}, deleteIcon )
			   );
		IS_Event.observe( deleteIcon,"click",function(tr, delObj){ tr.parentNode.removeChild(tr);}.bind(this, tr, {gadgetUrl:consumer['gadget_url'],serviceName:consumer['service_name']}),true,"_adminAuthentication" );
		return tr;
	},

	_saveContainerCert:function(){
		var consumerKey = $F('oauth_container_consumer_key');
		var privateKey = $F('oauth_container_private_key');
		var certificate = $F('oauth_container_certificate');
		
		var url = findHostURL() + "/services/authentication/saveContainerCertificate";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			asynchronous:true,
			postBody: Object.toJSON([consumerKey, privateKey, certificate]),
			onSuccess: function( resp ) {
				ISA_Admin.isUpdated = false;
				this.currentModal.update(ISA_R.ams_changeUpdated);
				setTimeout( function() {
					this.currentModal.close();
				}.bind(this),500 );
				
				this._displayContainerCert();
			}.bind( this ),
			onFailure: function(r, t) {
				alert(ISA_R.ams_failedToUpdateOAuthSettings);
				msg.error(ISA_R.ams_failedToUpdateOAuthSettings + t.status + " - " + t.statusText);
			},
			onException: function(r,t){
				alert(ISA_R.ams_failedToUpdateOAuthSettings);
				msg.error(ISA_R.ams_failedToUpdateOAuthSettings + getErrorMessage(t));
			},
			onComplete: function(){
				this.currentModal.close();
			}.bind(this)
		};
		this.currentModal.open();
		AjaxRequest.invoke(url, opt);
	},
	
	_displayContainerCert: function(){
		var url = findHostURL() + "/services/authentication/getContainerCertificateJson";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			asynchronous:true,
			onSuccess: function( resp ) {
				ISA_Admin.isUpdated = false;
				this._renderContainerCert(eval( '('+ resp.responseText+')' ));
			}.bind( this ),
			onFailure: function(r, t) {
				alert(ISA_R.ams_failedToGetOAuthSettings);
				msg.error(ISA_R.ams_failedToGetOAuthSettings + t.status + " - " + t.statusText);
			},
			onException: function(r,t){
				alert(ISA_R.ams_failedToGetOAuthSettings);
				msg.error(ISA_R.ams_failedToGetOAuthSettings + getErrorMessage(t));
			},
			onComplete: function(){
				this.currentModal.close();
			}.bind(this)
		};
		AjaxRequest.invoke(url, opt);
		
	},
	
	_renderContainerCert: function(certificate){
		IS_Event.unloadCache( "_adminAuthenticationCert");
		
		var container = document.getElementById("oauth_container");
		while(container.firstChild)
		  container.removeChild( container.firstChild );
		
		var controlDiv = document.createElement("div");
		controlDiv.style.textAlign = "right";
		
		var commitDiv = ISA_Admin.createIconButton(ISA_R.alb_changeApply, ISA_R.alb_changeApply, "database_save.gif", "right");
		controlDiv.appendChild(commitDiv);
		IS_Event.observe(commitDiv, "click", this._saveContainerCert.bind(this), false, "_adminAuthenticationCert");
		
		var refreshDiv = ISA_Admin.createIconButton(ISA_R.alb_refresh, ISA_R.alb_reloadWithourSaving, "refresh.gif", "right");
		controlDiv.appendChild(refreshDiv);
		IS_Event.observe(refreshDiv, "click", this._displayContainerCert.bind(this), false, "_adminAuthenticationCert");
		
		container.appendChild(controlDiv);
		
		var consumerKeyNote = $.P({className:"note"});
		consumerKeyNote.innerHTML = ISA_R.ams_oauthConsumerKeyNote;
		var privateKeyNote = $.P({className:"note"});
		privateKeyNote.innerHTML = ISA_R.ams_oauthPrivateKeyNote;
		var certificateNote = $.P({className:"note"});
		var publicKeyUrl = hostPrefix + "/opensocial/certificates/public.cer";
		certificateNote.innerHTML = ISA_R.getResource(ISA_R.ams_oauthCertificateNote, [publicKeyUrl, publicKeyUrl]);
		var forms = $.DIV(
			{id:'oauthContainerCertForm'},
			$.UL({},
				 $.LI({},
					  $.LABEL({}, ISA_R.alb_oauthConsumerKey),
					  $.INPUT({id:'oauth_container_consumer_key',value:certificate.consumerKey}),
					  consumerKeyNote
						),
				 $.LI({},
					  $.LABEL({}, ISA_R.alb_oauthPrivateKey),
					  $.TEXTAREA({id:'oauth_container_private_key',value:certificate.privateKey}),
					  privateKeyNote
						),
				 $.LI({},
					  $.LABEL({}, ISA_R.alb_oauthCertificate),
					  $.TEXTAREA({id:'oauth_container_certificate',value:certificate.certificate}),
					  certificateNote
						)
			) );
		
		container.appendChild( forms );
	}
	
}