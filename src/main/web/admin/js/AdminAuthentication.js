var emptyConsumerData = {'id':'', 'gadget_url':[],'service_name':'','consumer_key':'','consumer_secret':'','signature_method':'HMAC-SHA1','description':'', 'oauth_version':'1.0'};
var oauthConsumerList = [];
var uploadedGadgets = [];
var tempGadgetList = [];
var editModal = [];
ISA_Authentication = {
	build: function(){
		var container = document.getElementById("authentication");
		var sideBar = document.getElementById("authentication-side-bar");
		var tabUl =
			$.UL({id:"oauth_setting_tabs"},
				$.LI({id:"oauth_consumer_tab",className:"", style:"width:100%;"},
					$.A({href:"#oauth_consumer", className:"sideBarTab-ui"},
							$.SPAN({className:"title"},ISA_R.alb_oauthConsumerSettings))
						),
				$.LI({id:"oauth_container_tab",className:"", style:"width:100%;"},
					$.A({href:"#oauth_container", className:"sideBarTab-ui"},
						$.SPAN({className:"title"},ISA_R.alb_oauthContainerCertificate))
						)
				);
		sideBar.appendChild(tabUl);

		container.appendChild(
			$.DIV({style:"clear:both;"},
				$.DIV({id:"oauth_consumer"}),
				$.DIV({id:"oauth_container"})
			)
		);
		
		this.controlTabs = new Control.Tabs("oauth_setting_tabs", {
			beforeChange: function(old_container, new_container){
				Element.removeClassName(old_container.id + "_tab","selected active");
				Element.addClassName(new_container.id+ "_tab", "selected active");
			}.bind(this)
		});

		this.currentModal = new Control.Modal( '', {
			closeOnClick: false
		});
		
		//uploaded gadget list
		var gadgetList = [];
		var url = adminHostPrefix + "/services/gadget/getGadgetJson";
		var opt = {
			method: 'get' ,
			asynchronous:false,
			onSuccess: function(response){
				gadgetList = eval("(" + response.responseText + ")");
				var widgetConfList = [];
				for(var i in gadgetList){
					if( !(gadgetList[i] instanceof Function) ){
						var widgetConf = gadgetList[i];
						// Show except for maximize and notAvailable
						if( !(/true/i.test(widgetConf.systemWidget)) ){
							if(!widgetConf.type)widgetConf.type = i;//Gadget has no type
							widgetConfList.push( widgetConf );
						}
					}
				}
				for(var i = 0; i < widgetConfList.length; i++){
					var title;
					var conf = widgetConfList[i];
					if(widgetConfList[i].ModulePrefs.OAuth || widgetConfList[i].ModulePrefs.OAuth2){
						if(widgetConfList[i].ModulePrefs){
							title = conf.ModulePrefs.directory_title || conf.ModulePrefs.title || conf.type;
						}
						else{
							title = (conf.title && 0 < conf.title.length)? conf.title : conf.type;
						}
						uploadedGadgets.push({
							gadgetUrl: conf.type + '/gadget',
							gadgetTitle : title
						});
					}
				}
				if(uploadedGadgets.length == 0){
					uploadedGadgets.push({
						gadgetUrl: '',
						gadgetTitle: ISA_R.ams_noOauthGadgetUploaded
					});
				}
			},
			on404: function(t) {
				uploadedGadgets.push({
					gadgetUrl: '',
					gadgetTitle: ISA_R.ams_widgetNotFound
				});
				msg.error(ISA_R.ams_widgetNotFound + t.status + " - " + t.statusText);
			},
			onFailure: function(t) {
				uploadedGadgets.push({
					gadgetUrl: '',
					gadgetTitle: ISA_R.ams_failedLoadingWidget
				});
				msg.error(ISA_R.ams_failedLoadingWidget + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				uploadedGadgets.push({
					gadgetUrl: '',
					gadgetTitle: ISA_R.ams_failedLoadingWidget
				});
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
		
		this._displayConsumer();
		this._displayContainerCert();

	},

	_saveConsumerList:function(){

		var consumerList = [];
		for(var i = 0; i < oauthConsumerList.length; i++){
			var consumer = oauthConsumerList[i];
			if(consumer ==null)
				continue;
			var serviceName = consumer.service_name;
			var consumerKey = consumer.consumer_key;
			var consumerSecret = consumer.consumer_secret;
			var signatureMethod = consumer.signature_method;
			var description = consumer.description;
			var gadgetUrl = consumer.gadget_url;
			consumerList.push({
				id:consumer.id,
				serviceName:serviceName,
				consumerKey:consumerKey,
				consumerSecret:consumerSecret,
				signatureMethod:signatureMethod,
				description:description,
				gadgetUrl:gadgetUrl
			});
		}

		var url = adminHostPrefix + "/services/authentication/saveOAuthConsumerList";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			asynchronous:true,
			postBody: Object.toJSON([Object.toJSON(consumerList)]),
			onSuccess: function( resp ) {
				ISA_Admin.isUpdated = false;
				this.currentModal.container.update(ISA_R.ams_changeUpdated);
				setTimeout( function() {
					Control.Modal.close();
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
				Control.Modal.close();
			}.bind(this)
		};
		this.currentModal.container.update(ISA_R.ams_applyingChanges);
		this.currentModal.open();
		AjaxRequest.invoke(url, opt);
		
	},
	_validateUrl: function(gadgetId) {
		var urlInput =  $(gadgetId);
		var newUrl = urlInput.value;
		var error = false;
		var regex = '^http(s)?://.*';
		if( newUrl.length == 0 ) {
			error = ISA_R.ams_typeValidURL;
		} else if(error = IS_Validator.validate(newUrl, {maxBytes:1024, regex:regex})){
		} 
		if(error){
			alert("["+ISA_R.alb_gadgetUrl+"] " + error);
			urlInput.focus();
			return error;
		}
	},
	_validateServiceName: function(elementId) {
		var serviceInput = $(elementId + '_service_name');
		var serviceName = serviceInput.value;
		var error = false;
		if( serviceName.length == 0 ) {
			error = ISA_R.ams_requiredItem;
		} else if(error = IS_Validator.validate(serviceName, {maxBytes:255})){
		}
		if(error){
			alert("["+ISA_R.alb_oauthServiceName+"] " + error);
			serviceInput.focus();
			return error;
		}
	},
	_validateConsumerKey: function(elementId) {
		var keyInput = $(elementId + '_consumer_key');
		var serviceName = keyInput.value;
		var error = false;
		if( serviceName.length == 0 ) {
			error = ISA_R.ams_requiredItem;
		} else if(error = IS_Validator.validate(serviceName, {maxBytes:255})){
		}
		if(error){
			alert("["+ISA_R.alb_oauthConsumerKey+"] " + error);
			keyInput.focus();
			return error;
		}
	},
	_validateConsumerSecret: function(elementId) {
		var secInput = $(elementId + '_consumer_secret');
		var serviceName = secInput.value;
		var error = false;
		if( serviceName.length == 0 ) {
			error = ISA_R.ams_requiredItem;
		} else if(error = IS_Validator.validate(serviceName, {maxBytes:255})){
		}
		if(error){
			alert("["+ISA_R.alb_oauthConsumerSecret+"] " + error);
			secInput.focus();
			return error;
		}
			
	},
	
	_validateDescription : function (elementId){
		var description = $(elementId + '_description').value;
		var error = ISA_R.alb_i18nImportMessageInvalidLength;
		if(error = IS_Validator.validate(description, {maxBytes: 2048})){
		}if(error){
			alert("["+ISA_R.alb_description+"]"+ error);
			$(elementId + '_description').focus();
			return error;
		}
	},
	
	_validateSameServiceGadget : function(index){
		var elementId = 'oauth_consumer_setting_' + index;
		var serviceInput = $(elementId + '_service_name');
		var serviceName = serviceInput.value;
		var error = false;
		var highlightRowNum = [];
		for(var i=0; i<oauthConsumerList.length; i++){
			if(index == i || oauthConsumerList[i]==null)//exclude itself and deleted object
				continue;
			if(serviceName == oauthConsumerList[i].service_name){
				for(var j=0; j<tempGadgetList.length; j++){
					for(var k=0; k<oauthConsumerList[i].gadget_url.length; k++){
						if(tempGadgetList[j] == oauthConsumerList[i].gadget_url[k]){
							error = ISA_R.ams_sameGadgetRegistered;
							highlightRowNum.push(j);
						}
					}
				}
			}
		}
		if(error){
			alert(error);
			serviceInput.focus();
			for(var i=0; i<highlightRowNum.length; i++){
				var highlightTr = $('gadget_list_' + highlightRowNum[i]);
				highlightTr.style.backgroundColor = '#ffc0c0';
			}
			return error;
		}
	},
	
	_displayConsumer: function(){
		var url = adminHostPrefix + "/services/authentication/getOAuthConsumerListJson";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			asynchronous:true,
			onSuccess: function( resp ) {
				ISA_Admin.isUpdated = false;
				oauthConsumerList = eval( resp.responseText );
				this._renderConsumer(oauthConsumerList);
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
				Control.Modal.close();
			}.bind(this)
		};
		AjaxRequest.invoke(url, opt);
		
	},
	
	_renderConsumer: function(oauthConsumerList){
		IS_Event.unloadCache("_adminAuthentication");
		
		var container = document.getElementById("oauth_consumer");
		while(container.firstChild)
			container.removeChild( container.firstChild );
		
		this._renderHeader( container );
		
		var table = ISA_Admin.buildTableHeader(
			[ ISA_R.alb_oauthServiceName, ISA_R.alb_edit, ISA_R.alb_description, ISA_R.alb_delete],
			[ "30%","5%", "60%", "5%"]
			);
		table.id = "authentication_contentTable";
		table.cellPadding = "0";
		table.cellSpacing = "0";
		table.className = "configTableHeader";
		//TODO:Function for generating table needs to be arranged
		//table.style.borderLeft = "1px solid #EEEEEE";
		
		container.appendChild( table );

		oauthConsumerList.each( function( consumer, index ) {
			this._createRow( consumer, index);
		}.bind(this));
	},
	
	_renderHeader: function(container){
		var controlDiv = document.createElement("div");
		controlDiv.style.textAlign = "right";
		controlDiv.className= "refreshAll";
		
		var commitDiv = ISA_Admin.createIconButton(ISA_R.alb_changeApply, ISA_R.alb_changeApply, "database_save.gif", "right");
		controlDiv.appendChild(commitDiv);
		IS_Event.observe(commitDiv, "click", this._saveConsumerList.bind(this), false, "_adminAuthentication");
		
		var refreshDiv = ISA_Admin.createIconButton(ISA_R.alb_refresh, ISA_R.alb_reloadWithourSaving, "refresh.gif", "right");
		controlDiv.appendChild(refreshDiv);
		IS_Event.observe(refreshDiv, "click", this._displayConsumer.bind(this), false, "_adminAuthentication");
		
		container.appendChild(controlDiv);
		
		var addButton =ISA_Admin.createIconButton(ISA_R.alb_add, ISA_R.alb_add, "add.gif", "left");
		container.appendChild(addButton);
		
		//add Modal to addButton
		var modal = new Control.Modal('',{
			width: 650
		});
		IS_Event.observe(addButton, "click", function(modal){
			modal.container.update(this._createConsumerForm(emptyConsumerData, oauthConsumerList.length, true));
			$('gadget_opt_url').checked = true;
			modal.open();
			}.bind(this, modal), false, "_adminAuthentication"
		);
	},
	
	_createTextbox: function(id, value){
		return $.INPUT({
			id: id
			, value: value
			, type: "text"
			, width: "99%"
			, style: "width:99%;"
		});
	},
	
	_createTextArea: function(id, value){
		return $.TEXTAREA({
			id: id
			, value: value
			, rows: 5
			, style: "width:99%;"
		});
	},

	_displayConsumerKeySecret:function(elementId){
		var signatureMethod = $(elementId + '_signature_method').value;
		var element1 = $(elementId + '_consumer_key');
		var element2 = $(elementId + '_consumer_secret');
		if(signatureMethod == 'RSA-SHA1'){
			element1.disabled = element2.disabled = true;
			element1.style.backgroundColor = element2.style.backgroundColor = '#eee';
			element1.value = element2.value = '';
		}else{
			element1.disabled = element2.disabled = false;
			element1.style.backgroundColor = element2.style.backgroundColor= "";
		}
	},
	
	_createRow: function( consumer, index){
		var serviceNameDiv = $.DIV({style:'width:270px; white-space:normal; overflow:hidden;', title:consumer['service_name']}, consumer['service_name']);
		var descriptionDiv = $.DIV({style:'width:530px;white-space:pre; overflow:hidden;'});
		var description = escapeHTMLEntity(consumer['description']);
		descriptionDiv.innerHTML = description.replace(/(\r\n|\n|\r)/g, '<br/>');
		var consumerListTable = $('authentication_contentTable').firstChild;
		var elementId = 'oauth_consumer_setting_' + index;
		var deleteIcon = $.IMG({id:elementId +"_delete", src:"../../skin/imgs/trash.gif", title:ISA_R.alb_delete, style:'cursor:pointer'});
		var editImg = $.IMG({id:elementId +"_edit", src:"../../skin/imgs/edit.gif", title:ISA_R.alb_editing, style:'cursor:pointer'});
		var tr = $.TR({id:elementId}
			,$.TD({id:elementId+'_service_nameTd', className:"configTableTd", style:"textAlign:left; padding: 3px;"}, serviceNameDiv )
			,$.TD({id:elementId+'_editTd', className:"configTableTd", style:"textAlign:center;"}, editImg )
			,$.TD({id:elementId+'_descriptionTd', className:"configTableTd", style:"textAlign:left; padding: 3px;"}, descriptionDiv )
			,$.TD({className:"configTableTd", style:"textAlign:center;"}, deleteIcon )
		);
		IS_Event.observe( deleteIcon,"click",function(tr, delObj){
			tr.parentNode.removeChild(tr);
			oauthConsumerList[index] = null;
			ISA_Admin.isUpdated = true;
		}.bind(this, tr, {gadgetUrl:consumer['gadget_url'],serviceName:consumer['service_name']}),true,"_adminAuthentication" );
		consumerListTable.appendChild(tr);
		
		IS_Event.observe(editImg, "click", function(elementId){
			var modal = editModal[elementId];
			if(!modal){
				modal = new Control.Modal('', {
					width: 650
				});
				editModal[elementId] = modal;				
			}
			modal.container.update(this._createConsumerForm(consumer, index, false));
			this._displayConsumerKeySecret(elementId);
			$('gadget_opt_url').checked = true;
			modal.open();
			}.bind(this, elementId), false, "_adminAuthentication"
		);
	},
	
	_updateRow :function(consumer, index){
		var elementId = 'oauth_consumer_setting_' + index;
		var serviceNameDiv = $(elementId + '_service_nameTd').firstChild;
		var editTd = $(elementId + '_editTd');
		var descriptionTd = $(elementId + '_descriptionTd');
		
		serviceNameDiv.innerHTML = escapeHTMLEntity(consumer['service_name']);
		var editImg = $.IMG({id:elementId +"_edit", src:"../../skin/imgs/edit.gif", title:ISA_R.alb_editing, style:'cursor:pointer'});
		IS_Event.observe(editImg, "click", function(elementId){
			var modal = editModal[elementId];
			if(!modal){
				modal = new Control.Modal('', {
					width: 650
				});
				editModal[elementId] = modal;				
			}
			modal.container.update(this._createConsumerForm(consumer, index, false));
			this._displayConsumerKeySecret(elementId);
			$('gadget_opt_url').checked = true;
			modal.open();
		}.bind(this, elementId), false, "_adminAuthentication");
		editTd.replaceChild(editImg, editTd.firstChild);
		
		var descriptionDiv = $.DIV({style:'width:530px;white-space:normal; overflow:hidden;'});
		var description = escapeHTMLEntity(consumer['description']);
		descriptionDiv.innerHTML = description.replace(/(\r\n|\n|\r)/g, '<br/>');
		descriptionTd.replaceChild(descriptionDiv, descriptionTd.firstChild);
	},
	
	_createConsumerForm : function(consumer, index, isNew){
		var formContainer = $.DIV({});
		var consumerEditForm = $.DIV({className:"modalConfigSet", align:"center"}, 
			$.P({className:"modalConfigSetHeader"}, ISA_R.alb_oauthConsumerSettings)
		);
		var elementId = 'oauth_consumer_setting_' + index;
		
		var gadgetOptRadioUrl = $.INPUT({type:"radio", name:"gadget_opt", id:"gadget_opt_url"});
		var gadgetOptRadioUpload = $.INPUT({type:"radio", name:"gadget_opt", id:"gadget_opt_upload"});
		var gadgetUrlAddButton = $.INPUT({type:"button", value:ISA_R.alb_add});
		var uploadGadgetAddButton = $.INPUT({type:"button", value:ISA_R.alb_add});
		var gadgetSelect = $.SELECT({id:"gadgetSelect", name:"gadgetSelect", style:'width:80%;' });
		for(var h=0; h<uploadedGadgets.length; h++){
			if(uploadedGadgets[0].gadgetUrl == ''){
				gadgetSelect.disabled = true;
				uploadGadgetAddButton.disabled = true;
			}
			var option = $.OPTION({id:uploadedGadgets[h].gadgetUrl, value:uploadedGadgets[h].gadgetTitle}
				, uploadedGadgets[h].gadgetTitle
			);
			gadgetSelect.appendChild(option);
		}
		//create gadget list
		tempGadgetList = [];
		
		consumer.isOAuth2 = !(consumer['signature_method'] && consumer['signature_method'].length > 0);
		
		var gadgetTableBody = 
		$.TBODY({id:"gadgetTableBody"}
			,$.TR({style:"backgroundColor:#eee;fontWeight:bold;textAlign:center;"}
				,$.TD({style:"padding:2px"}, ISA_R.alb_oauthGadgetsUsingService)
				,$.TD({width:"50px"},ISA_R.alb_delete)
			)
		);
		var gadgetUrlList = consumer['gadget_url'].sort();
		for(var i=0; i<gadgetUrlList.length; i++){
			var gadget_url = gadgetUrlList[i];
			tempGadgetList.push(gadget_url);
			var gadgetTitle = gadget_url;
			//find upload gadget title by gadget url
			for(var j=0; j<uploadedGadgets.length; j++){
				if(uploadedGadgets[j].gadgetUrl.match(gadget_url)){
					gadgetTitle = uploadedGadgets[j].gadgetTitle;
				}
			}
			var deleteIcon = this._createDeleteIcon(i);
			gadgetTableBody.appendChild(
				$.TR({id:'gadget_list_' + i}
					, $.TD({style:'padding:2px;'}
						, $.DIV({style:"overflow:hidden"},gadgetTitle)
					)
					, $.TD({align:"center"}, deleteIcon)
				)
			);
		};
		
		var gadgetUrlTd = $.TD({ style:"padding:5px;"}
			,$.INPUT({id:elementId + '_gadget_url', value: 'http://', type:"text", style: 'width: 80%'})
			,gadgetUrlAddButton
		);
		
		var gadgetListTd = $.TD({ style:"padding:5px;"}
			,gadgetSelect
			, uploadGadgetAddButton
		);
		
		var gadgetOptTr = $.TR({}, $.TD({width:'35%'}), gadgetUrlTd);
		
		IS_Event.observe( gadgetOptRadioUrl, "click", function(){
			gadgetOptRadioUpload.checked = false; //to switch the checked status for only IE6 
			gadgetOptRadioUrl.checked = true;
			gadgetOptTr.replaceChild(gadgetUrlTd, gadgetOptTr.childNodes[1]);
		});
		IS_Event.observe( gadgetOptRadioUpload, "click", function(){
			gadgetOptRadioUrl.checked = false; //to switch the checked status for only IE6 
			gadgetOptRadioUpload.checked = true;
			gadgetOptTr.replaceChild(gadgetListTd, gadgetOptTr.childNodes[1]);
		});
		
		var gadgetId = elementId + '_gadget_url';
		IS_Event.observe( gadgetUrlAddButton, "click", function(){
			if(this._validateUrl(gadgetId))
				return;
			this._createGadgetRow(gadgetId, true);
		}.bind(this));
		IS_Event.observe( uploadGadgetAddButton, "click", function(){
			this._createGadgetRow(gadgetId, false);
		}.bind(this));
		
		var tbody = $.TBODY({},
			$.TR({}, 
				$.TD({width:"35%", style:"padding:5px;"}, ISA_R.alb_oauthServiceName),
				$.TD({} 
					,this._createTextbox(elementId + '_service_name', consumer['service_name'])
				)
			)
			,$.TR({}, 
				$.TD({width:"35%", style:"padding:5px;"}, "OAuth " + ISA_R.alb_versionNum),
				$.TD({},
					$.SELECT({
						id: elementId + '_oauth_version',
						onchange:{
							handler:function(elementId){
								$(elementId + '_signature_method').value = 'HMAC-SHA1';
								this._displayConsumerKeySecret(elementId);
								$jq('.oauth1_element').toggle();
								$jq('.oauth2_element').toggle();
							}.bind(this, elementId)
						}
					}
						,$.OPTION({value:'1.0', selected: !consumer.isOAuth2}, '1.0')
						,$.OPTION({value:'2.0', selected: consumer.isOAuth2}, '2.0')
					)
				)
			)
			,$.TR({"class":"oauth1_element", style:(consumer.isOAuth2)? 'display:none' : ''}, 
				$.TD({ style:"padding:5px;"}, ISA_R.alb_oauthSignatureAlgorithm),
				$.TD({}, 
					$.SELECT({
						id: elementId + '_signature_method',
						onchange:{handler:this._displayConsumerKeySecret.bind(this, elementId),
						value:consumer['signature_method']}
					}
						,$.OPTION({value:'HMAC-SHA1', selected: ('HMAC-SHA1' == consumer['signature_method']) }, 'HMAC-SHA1')
						,$.OPTION({value:'RSA-SHA1', selected: ('RSA-SHA1' == consumer['signature_method'])}, 'RSA-SHA1')
					)
				)
			)
			,$.TR({},
				$.TD({"class":"oauth1_element", style:"padding:5px;" + ((consumer.isOAuth2)? 'display:none' : '')}, ISA_R.alb_oauthConsumerKey),
				$.TD({"class":"oauth2_element", style:"padding:5px;" + ((!consumer.isOAuth2)? 'display:none' : '')}, ISA_R.alb_oauthClientId),
				$.TD({}
						,this._createTextbox(elementId + '_consumer_key', consumer['consumer_key'])
					)
				)
			,$.TR({}, 
				$.TD({"class":"oauth1_element", style:"padding:5px;" + ((consumer.isOAuth2)? 'display:none' : '')}, ISA_R.alb_oauthConsumerSecret),
				$.TD({"class":"oauth2_element", style:"padding:5px;" + ((!consumer.isOAuth2)? 'display:none' : '')}, ISA_R.alb_oauthClientSecret),
				$.TD({} 
					,this._createTextbox(elementId + '_consumer_secret', consumer['consumer_secret'])
				)
			)
			,$.TR({}, 
				$.TD({style:"padding:5px;"}, ISA_R.alb_description),
				$.TD({} 
					, this._createTextArea(elementId + '_description', consumer['description'])
				)
			)
			,$.TR({}, 
					$.TD({width:"", style:"padding:5px;"}, ISA_R.alb_oauthAddingGadget),
					$.TD({width: "", style:"padding:5px"}
						, gadgetOptRadioUrl
						, $.LABEL({htmlFor:"gadget_opt_url", style:"padding: 0 15px 0 0"}
							, ISA_R.alb_gadgetUrlSpecified
						)
						, gadgetOptRadioUpload
						, $.LABEL({htmlFor:"gadget_opt_upload"}
							, ISA_R.alb_upload, ISA_R.alb_widget
						)
					)
				)
				,gadgetOptTr
				,$.TR({}, 
					$.TD({colSpan:"2", style:"padding:5px;"}
						,$.TABLE({border:"1px", width:"100%", className:"gadgetUrlTable configTableHeader fixedTable"}
							,gadgetTableBody
						)
					)
				)
		);

		consumerEditForm.appendChild($.TABLE({id:elementId, width:'90%', className: 'fixedTable' }, tbody));
		
		var buttonsDiv = $.DIV({align:'center'});
		
		var okButton = buttonsDiv.appendChild(createButton(ISA_R.alb_entry));
		var consumerData = [];
		IS_Event.observe(okButton, 'click', function(){
			if(this._validateServiceName(elementId))
				return;
			if(this._validateDescription(elementId))
				return;
			var gadgetUrlList =[];
			for(var i=0; i<tempGadgetList.length; i++){
				if(tempGadgetList[i] ==null)
					continue;
				gadgetUrlList.push(tempGadgetList[i]);
			}
			if(this._validateSameServiceGadget(index))
				return;
			//add a new consumer data json
			consumerData.id = consumer.id;
			consumerData.gadget_url = gadgetUrlList;
			consumerData.service_name = $(elementId+ '_service_name').value;
			consumerData.consumer_key = $(elementId + '_consumer_key').value;
			consumerData.consumer_secret = $(elementId + '_consumer_secret').value;
			var signatureMethod = $(elementId + '_signature_method').value;
			if(signatureMethod != 'RSA-SHA1'){
				if(this._validateConsumerKey(elementId))
					return;
				if(this._validateConsumerSecret(elementId))
					return;
			}
			consumerData.oauth_version = $(elementId + '_oauth_version').value;
			consumerData.signature_method =	(consumerData.oauth_version == '1.0')? signatureMethod : '';
			var description = $(elementId + '_description').value;
			consumerData.description = description;
			if(isNew){
				oauthConsumerList.push(consumerData);
				this._createRow(consumerData, index);
			}
			else{
				oauthConsumerList.splice(index, 1, consumerData);
				this._updateRow(consumerData, index);
			}
			ISA_Admin.isUpdated = true;
			Control.Modal.close();
		}.bind(this));
	
		var cancelButton = buttonsDiv.appendChild(createButton(ISA_R.alb_cancel));
		IS_Event.observe(cancelButton, 'click', function(){
			Control.Modal.close();
			ISA_Admin.isUpdated = false;
		}.bind(this));
	
		formContainer.appendChild(consumerEditForm);
		formContainer.appendChild(buttonsDiv);

		return formContainer;
		
		function createButton(label, event){
			var button = document.createElement("input");
			button.type = "button";
			button.value = label;
			
			return button;
		}
	},
	
	_createGadgetRow: function(gadgetId, isUrlGadget){
		var gadgetTableBody = $('gadgetTableBody');
		var index = tempGadgetList.length;
		var gadgetUrl = "";
		var gadgetTitle = "";
		
		if(isUrlGadget){
			gadgetUrl = $(gadgetId).value;
			gadgetTitle = gadgetUrl;
			$(gadgetId).value = "http://";
		};
		if (!isUrlGadget){//for uploaded gadgets
			var selectedIndex = $("gadgetSelect").selectedIndex;
			gadgetUrl = $("gadgetSelect").options[selectedIndex].id;
			gadgetTitle = $("gadgetSelect").options[selectedIndex].text;
		};
		if(tempGadgetList.indexOf(gadgetUrl) > -1) {
			alert(ISA_R.ams_oauthAlreadyExistingGadget);
			if(isUrlGadget)
				$(gadgetId).focus();
			return;
		}
		var deleteIcon = this._createDeleteIcon(index);
		tempGadgetList.push(gadgetUrl);
		gadgetTableBody.appendChild(
			$.TR({id:'gadget_list_' + index}
				, $.TD({style:"padding:2px;"}
					, $.DIV({style:"overflow:hidden"}, gadgetTitle)
				)
				, $.TD({align:"center", style:"padding:0 3px 0 3px;"}, deleteIcon)
			)
		);
	},
	
	_createDeleteIcon: function(index){
		var deleteIcon = $.IMG({src:"../../skin/imgs/trash.gif", title:ISA_R.alb_delete, style:'cursor:pointer'});
		IS_Event.observe(deleteIcon, "click", function(index){
			deleteIcon.parentNode.parentNode.parentNode.removeChild(deleteIcon.parentNode.parentNode);
			tempGadgetList[index] = null;
		}.bind(this, index));
		return deleteIcon;
	},

	_saveContainerCert:function(){
		var consumerKey = $('oauth_container_consumer_key').value;
		if(this._validateContainerConsumerKey(consumerKey))return;
		var privateKey = $('oauth_container_private_key').value;
		var certificate = $('oauth_container_certificate').value;
		
		var url = adminHostPrefix + "/services/authentication/saveContainerCertificate";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			asynchronous:true,
			postBody: Object.toJSON([consumerKey, privateKey, certificate]),
			onSuccess: function( resp ) {
				ISA_Admin.isUpdated = false;
				this.currentModal.container.update(ISA_R.ams_changeUpdated);
				setTimeout( function() {
					Control.Modal.close();
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
				Control.Modal.close();
			}.bind(this)
		};
		this.currentModal.container.update(ISA_R.ams_applyingChanges);
		this.currentModal.open();
		AjaxRequest.invoke(url, opt);
	},
	
	_validateContainerConsumerKey: function(consumerKey) {
		var error = false;
		if( consumerKey.length == 0 ) {
			error = ISA_R.ams_requiredItem;
		} else if(error = IS_Validator.validate(consumerKey, {maxBytes:255})){
		}
		if(error)
			alert("[" + ISA_R.alb_oauthConsumerKey + "] " + error);
		return error;
	},

	_displayContainerCert: function(){
		var url = adminHostPrefix + "/services/authentication/getContainerCertificateJson";
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
				Control.Modal.close();
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
			$.DIV({className:"configSet"}, 
				$.P({className:"configSetHeader"},ISA_R.alb_oauthConsumerKey),
				consumerKeyNote,
				$.INPUT({id:'oauth_container_consumer_key', className:"configSetContent", type:"text", value:certificate.consumerKey})),
			$.DIV({className:"configSet"}, 
				$.P({className:"configSetHeader"},ISA_R.alb_oauthPrivateKey),
				privateKeyNote, 
				$.TEXTAREA({id:'oauth_container_private_key', className:"configSetContent", value:certificate.privateKey})),
			$.DIV({className:"configSet"}, 
				$.P({className:"configSetHeader"},ISA_R.alb_oauthCertificate),
				  certificateNote, 
				$.TEXTAREA({id:'oauth_container_certificate', className:"configSetContent",value:certificate.certificate}))
		);
		container.appendChild( forms );
	}
};