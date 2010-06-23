ISA_Authentication = {
	_deletedConsumerList:[],
	
	build: function(){
		var url = findHostURL() + "/services/authentication/getOAuthConsumerListJson";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			asynchronous:true,
			onSuccess: function( resp ) {
				ISA_Admin.isUpdated = false;
				this._display(eval( resp.responseText ));
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
				currentModal.close();
			}
		};
		AjaxRequest.invoke(url, opt);
		
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
		var currentModal = new Control.Modal( false, {
			contents: ISA_R.ams_applyingChanges,
			opacity: 0.2,
			overlayCloseOnClick: false
		});

		var url = findHostURL() + "/services/authentication/saveOAuthConsumerList";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			asynchronous:true,
			postBody: Object.toJSON([Object.toJSON(consumerList), Object.toJSON(this._deletedConsumerList)]),
			onSuccess: function( resp ) {
				ISA_Admin.isUpdated = false;
				currentModal.update(ISA_R.ams_changeUpdated);
				setTimeout( function() {
					currentModal.close();
				},500 );
				
				this.build();
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
				currentModal.close();
			}
		};
		currentModal.open();
		AjaxRequest.invoke(url, opt);
		
	},
	_addConsumerSetting:function(){
		
		var authenticationContentTable = $("authentication_contentTable");
		authenticationContentTable.firstChild.appendChild(
			this._createRow({'gadget_url':'http://','service_name':'','consumer_key':'','consumer_secret':'','signature_method':'HMAC-SHA1'}, new Date().getTime())
			);
	},
	_displayHeader: function(container){
		var controlDiv = document.createElement("div");
		controlDiv.style.textAlign = "right";
		
		var commitDiv = ISA_Admin.createIconButton(ISA_R.alb_changeApply, ISA_R.alb_changeApply, "database_save.gif", "right");
		controlDiv.appendChild(commitDiv);
		IS_Event.observe(commitDiv, "click", this._saveConsumerList.bind(this), false, "_adminAuthentication");
		
		var refreshDiv = ISA_Admin.createIconButton(ISA_R.alb_refresh, ISA_R.alb_reloadWithourSaving, "refresh.gif", "right");
		controlDiv.appendChild(refreshDiv);
		IS_Event.observe(refreshDiv, "click", this.build.bind(this), false, "_adminAuthentication");
		
		container.appendChild(controlDiv);
		
		var titleDiv = document.createElement("div");
		titleDiv.className = "proxyTitle";
		titleDiv.appendChild(document.createTextNode(ISA_R.alb_oauthConsumerSettings));
		container.appendChild(titleDiv);
		
		var addButton = ISA_Admin.createIconButton(ISA_R.alb_add, ISA_R.alb_add, "add.gif", "left");
		addButton.style.textAlign = "left"
		
		IS_Event.observe(addButton, "click", this._addConsumerSetting.bind(this), false, "_adminAuthentication");
		
		container.appendChild(addButton);
	},
	
	_display: function(oauthConsumerList){
		this._deletedConsumerList = [];
		IS_Event.unloadCache("_adminAuthentication");
		
		var container = document.getElementById("authentication");
		while(container.firstChild)
		  container.removeChild( container.firstChild );
		
		this._displayHeader( container );
		
		var table = ISA_Admin.buildTableHeader(
			[ ISA_R.alb_gadgetUrl, ISA_R.alb_oauthServiceName, ISA_R.alb_oauthConsumerKey, ISA_R.alb_oauthConsumerSecret, ISA_R.alb_oauthSignatureAlgorithm, ISA_R.alb_oauthPrivateKey, ISA_R.alb_delete],
			[ '30%', '10%', "20%", "20%", "10%", "5%", '5%']
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
		var editIcon = ISA_Admin.createIconButton("", ISA_R.alb_edit, "edit.gif");
		
		var modal = new Control.Modal(
			editIcon,
			{
			  beforeOpen:function(){
				  if( $F(elementId + '_signature_method') =='HMAC-SHA1'){
					  return false;
				  }
			  },
			  afterClose:function(){
				  alert("todo implementation save private key");
			  },
			  contents: "<textarea style='width:280px;height:180px;'></textarea>",
			  opacity: 0.5,
			  width: '300',
			  height:'200'
			}
			);
		
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
					$.TD({},
						 $.SELECT(
							 {id: elementId + '_signature_method',
							   onchange:{handler:
								   function(e){
									   var signatureMethodSelect = Event.element(e);
									   var signatureMethod = signatureMethodSelect.value;
									   var element = $(signatureMethodSelect.id + '_pkedit');
									   element.style.backgroundColor = getEditPrivateKeyBGColor(signatureMethod);
								 },
								 value:consumer['signature_method']}
							 },
							 $.OPTION({value:'HMAC-SHA1'}, 'HMAC-SHA1'),
							 $.OPTION({value:'RSA-SHA1'}, 'RSA-SHA1')
							   )),
					$.TD({id:elementId + '_signature_method_pkedit', style:"textAlign:center;backgroundColor:" + getEditPrivateKeyBGColor(consumer['signature_method']) }, editIcon ),
					$.TD({style:"textAlign:center;"}, deleteIcon )
			   );
		IS_Event.observe( deleteIcon,"click",function(tr, delObj){ tr.parentNode.removeChild(tr); this._deletedConsumerList.push(delObj);}.bind(this, tr, {gadgetUrl:consumer['gadget_url'],serviceName:consumer['service_name']}),true,"_adminAuthentication" );
		return tr;
	}
}