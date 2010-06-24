ISA_Authentication = {
	_deletedConsumerList:[],
	
	build: function(){
		
		var container = document.getElementById("authentication");
		var tabUl = $.UL({id:"oauth_setting_tabs", className:"subsection_tabs tabs"},
			 $.LI({},
				  $.A({id:"oauth_setting_tabs_consumer",href:"#oauth_consumer", className:"tab"},
					  $.SPAN({className:"title"},ISA_R.alb_oauthConsumerSettings))
					),
			 $.LI({},
				  $.A({id:"oauth_setting_tabs_rsa",href:"#oauth_container", className:"tab"},
					  $.SPAN({className:"title"},ISA_R.alb_oauthContainerCertificate))
					)
			   );
		container.appendChild(tabUl);

		container.appendChild(
			$.DIV({style:"clear:both;padding:5px;"},
				  $.DIV({id:"oauth_consumer" }),
				  $.DIV({id:"oauth_container"})));
		
		this.controlTabs = new Control.Tabs("oauth_setting_tabs", {
			beforeChange: function(old_container, new_container){
			}.bind(this)
		});
		this._displayConsumer();
		this._displayContainerCert();
		this.controlTabs.setActiveTab("oauth_consumer");

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
			var gadgetUrl = $F(tr.id + '_gadget_url');
			var serviceName = $F(tr.id + '_service_name');
			var consumerKey = $F(tr.id + '_consumer_key');
			var consumerSecret = $F(tr.id + '_consumer_secret');
			var signatureMethod = $F(tr.id + '_signature_method');
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
			postBody: Object.toJSON([Object.toJSON(consumerList), Object.toJSON(this._deletedConsumerList)]),
			onSuccess: function( resp ) {
				ISA_Admin.isUpdated = false;
				this.currentModal.update(ISA_R.ams_changeUpdated);
				setTimeout( function() {
					this.currentModal.close();
				},500 );
				
				this._renderConsumer();
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
			}
		};
		this.currentModal.open();
		AjaxRequest.invoke(url, opt);
		
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
				alert(ISA_R.ams_failedToUpdateOAuthSettings);
				msg.error(ISA_R.ams_failedToGetOAuthSettings + t.status + " - " + t.statusText);
			},
			onException: function(r,t){
				alert(ISA_R.ams_failedToUpdateOAuthSettings);
				msg.error(ISA_R.ams_failedToGetOAuthSettings + getErrorMessage(t));
			},
			onComplete: function(){
				this.currentModal.close();
			}
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
		IS_Event.observe(refreshDiv, "click", this.build.bind(this), false, "_adminAuthentication");
		
		container.appendChild(controlDiv);
		
		var addButton = ISA_Admin.createIconButton(ISA_R.alb_add, ISA_R.alb_add, "add.gif", "left");
		addButton.style.textAlign = "left"
		
		IS_Event.observe(addButton, "click", this._addConsumerSetting.bind(this), false, "_adminAuthentication");
		
		container.appendChild(addButton);
	},
	
	_renderConsumer: function(oauthConsumerList){
		this._deletedConsumerList = [];
		IS_Event.unloadCache("_adminAuthentication");
		
		var container = document.getElementById("oauth_consumer");
		while(container.firstChild)
		  container.removeChild( container.firstChild );
		
		this._renderHeader( container );
		
		var table = ISA_Admin.buildTableHeader(
			[ ISA_R.alb_gadgetUrl, ISA_R.alb_oauthServiceName, ISA_R.alb_oauthConsumerKey, ISA_R.alb_oauthConsumerSecret, ISA_R.alb_delete],
			[ '30%', '15%', "25%", "25%", "5%"]
			);
		table.id = "authentication_contentTable";
		table.className = "proxyConfigList";
		table.style.tableLayout = "fixed"
		//TODO:Function for generating table needs to be arranged
		table.style.borderLeft = "1px solid #EEEEEE";
		table.style.width = "100%";
		
		var tbody = table.firstChild;
		var this_ = this;
		oauthConsumerList.each( function( consumer, index ) {
			tbody.appendChild( this._createRow( consumer, index ));
		}.bind(this));
		
		container.appendChild( table );
	},
	
	_createTextbox: function(id, value){
		return $.INPUT({id: id, value: value, className:'portalAdminInput',style:"width:100%;",
		  onchange:{handler: function(){ ISA_Admin.isUpdated = true; }},
		  onfocus:{handler: function(){} },
		  onblur:{handler: function(){}}
		});
	},
	
	_createRow: function( consumer, index ){
		var elementId = 'oauth_consumer_setting_' + index;
		function getEditPrivateKeyBGColor(signatureMethod){
			if(signatureMethod == 'HMAC-SHA1')
			  return "#EEEEEE";
			else
			  return "#FFFFFF";
		}
		var deleteIcon = ISA_Admin.createIconButton("", ISA_R.alb_delete, "trash.gif");
		var tr = $.TR({id:elementId },
					$.TD({}, this._createTextbox(elementId + '_gadget_url', consumer['gadget_url']) ),
					$.TD({}, this._createTextbox(elementId + '_service_name', consumer['service_name']) ),
					$.TD({}, this._createTextbox(elementId + '_consumer_key', consumer['consumer_key'] ) ),
					$.TD({}, this._createTextbox(elementId + '_consumer_secret', consumer['consumer_secret'] ) ),
					$.TD({style:"textAlign:center;"}, deleteIcon )
			   );
		IS_Event.observe( deleteIcon,"click",function(tr, delObj){ tr.parentNode.removeChild(tr); this._deletedConsumerList.push(delObj);}.bind(this, tr, {gadgetUrl:consumer['gadget_url'],serviceName:consumer['service_name']}),true,"_adminAuthentication" );
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
				
				this._renderContainerCert();
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
				alert(ISA_R.ams_failedToUpdateOAuthSettings);
				msg.error(ISA_R.ams_failedToGetOAuthSettings + t.status + " - " + t.statusText);
			},
			onException: function(r,t){
				alert(ISA_R.ams_failedToUpdateOAuthSettings);
				msg.error(ISA_R.ams_failedToGetOAuthSettings + getErrorMessage(t));
			},
			onComplete: function(){
				this.currentModal.close();
			}
		};
		AjaxRequest.invoke(url, opt);
		
	},
	
	_renderContainerCert: function(certificate){
		this._deletedConsumerList = [];
		IS_Event.unloadCache( "_adminAuthenticationContainer");
		
		var container = document.getElementById("oauth_container");
		while(container.firstChild)
		  container.removeChild( container.firstChild );
		
		var controlDiv = document.createElement("div");
		controlDiv.style.textAlign = "right";
		
		var commitDiv = ISA_Admin.createIconButton(ISA_R.alb_changeApply, ISA_R.alb_changeApply, "database_save.gif", "right");
		controlDiv.appendChild(commitDiv);
		IS_Event.observe(commitDiv, "click", this._saveContainerCert.bind(this), false, "_adminAuthenticationContainer");
		
		var refreshDiv = ISA_Admin.createIconButton(ISA_R.alb_refresh, ISA_R.alb_reloadWithourSaving, "refresh.gif", "right");
		controlDiv.appendChild(refreshDiv);
		IS_Event.observe(refreshDiv, "click", this.build.bind(this), false, "_adminAuthenticationContainer");
		
		container.appendChild(controlDiv);
		

		var forms = $.DIV(
			{id:'oauthContainerCertForm'},
			$.UL({},
				 $.LI({},
					  $.LABEL({}, ISA_R.alb_oauthConsumerKey),
					  $.INPUT({id:'oauth_container_consumer_key',value:certificate.consumerKey})
						),
				 $.LI({},
					  $.LABEL({}, ISA_R.alb_oauthPrivateKey),
					  $.TEXTAREA({id:'oauth_container_private_key',value:certificate.privateKey})
						),
				 $.LI({},
					  $.LABEL({}, ISA_R.alb_oauthCertificate),
					  $.TEXTAREA({id:'oauth_container_certificate',value:certificate.certificate})
						)
				   ) );
		
		container.appendChild( forms );
	}
	
}