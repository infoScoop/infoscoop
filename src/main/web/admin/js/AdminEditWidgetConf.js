ISA_WidgetConf.EditWidgetConf = new Object();

ISA_WidgetConf.EditWidgetConf.render = function(editConfNode, type, conf){
	var isExistEditPref = false;
	
	// Add edit of WidgetPref
	var editWidgetPref = document.createElement("div");
	editWidgetPref.className = "configSet";
	editWidgetPref.style.marginBottom = "10px";
	var editLabel = document.createElement("p");
	editLabel.className = "configSetHeader"
	editWidgetPref.appendChild( editLabel );
	editLabel.innerHTML = ISA_R.alb_editWidgetSettings;
	
	var editTable = document.createElement("table");
	editTable.className = "editWidgetConfTable";
	editTable.style.width = "100%";
	editWidgetPref.appendChild( editTable );
	
	var editWidgetPrefTbody = document.createElement("tbody");
	editTable.appendChild( editWidgetPrefTbody );

	if(conf.autoRefresh != null){
		editWidgetPrefTbody.appendChild(
			ISA_WidgetConf.EditWidgetConf.makeEditPref(
				'ATTR',
				{name:'autoRefresh', datatype:'bool', display_name:ISA_R.alb_autoRefresh, value:conf.autoRefresh}
				)
			);
	}
	if(conf.backgroundColor != null){
		editWidgetPrefTbody.appendChild(
			ISA_WidgetConf.EditWidgetConf.makeEditPref(
				'ATTR',
				{name:'backgroundColor', datatype:'string', display_name:ISA_R.alb_bgcolor, value:conf.backgroundColor}
				)
			);
	}
	
	//TODO:To edit the scrolling property that is bool type in ModulePrefs
	var editableModulePref = {
		autoRefresh: {
			datatype: "bool",
			display_name: ISA_R.alb_autoRefresh
		},
		title: {
			display_name: ISA_R.alb_title
		},
		directory_title: {
			display_name: ISA_R.alb_directoryTitle
		},
		title_url: {
			display_name: ISA_R.alb_titleUrl
		},
		height: {
			display_name: ISA_R.alb_height
		},
		//"scrolling"
		singleton: {
			datatype: "bool",
			display_name: ISA_R.alb_singleton
		},
		resource_url: {
			display_name: ISA_R.alb_resourceUrl
		}
	};
	
	var widgetPrefList = {};
	if(conf.ModulePrefs ) {
		for( var i in conf.ModulePrefs ) if( conf.ModulePrefs.hasOwnProperty( i )) {
			if( editableModulePref[i] ) {
				widgetPrefList[i] = Object.extend({
					name: i,
					value: conf.ModulePrefs[i]
				},editableModulePref[i]);
			}
		}
	}
	widgetPrefList = Object.extend( widgetPrefList,conf.WidgetPref );
	
	for( id in widgetPrefList ){
		if( !( widgetPrefList[id] instanceof Function ) ){
			existWidgetPrefs = true;
			var widgetPref = widgetPrefList[id];
			if(!widgetPref.name){
				widgetPref = {
				  name:id,
				  value:widgetPref
				}
			}
			
			var widgetPrefEl =  ISA_WidgetConf.EditWidgetConf.makeEditPref( 'MP', widgetPref, type );
			if(widgetPrefEl)editWidgetPrefTbody.appendChild(widgetPrefEl);
		}
	}
	
	if(editWidgetPrefTbody.childNodes.length > 0){
		isExistEditPref = true;
		editConfNode.appendChild( editWidgetPref );
	}
	
	// Adding edit of UserPref
	var existUserPrefs = false;
	var editUserPref = document.createElement("div");
	editUserPref.className = "configSet";
	//editUserPref.style.marginBottom = "10px";
	var editLabel = document.createElement("p");
	editLabel.className = "configSetHeader";
	editUserPref.appendChild( editLabel );
	editLabel.innerHTML = ISA_R.alb_editUserSettings;
	
	var editTable = document.createElement("table");
	editTable.className = "editWidgetConfTable";
	editTable.style.width = "100%";
	editUserPref.appendChild( editTable );
	
	var editUserPrefTbody = document.createElement("tbody");
	editTable.appendChild( editUserPrefTbody );
	
	var userPrefList = conf.UserPref;
	for( id in userPrefList ){
		if( !( userPrefList[id] instanceof Function ) ){
			var userPref = userPrefList[id];
			var editTr = ISA_WidgetConf.EditWidgetConf.makeEditPref( 'UP', userPref, type );
			if(editTr){
				editUserPrefTbody.appendChild( editTr );
			}
		}
	}
	
	if(editUserPrefTbody.childNodes.length > 0){
		isExistEditPref = true;
		editConfNode.appendChild( editUserPref );
	}
	
	if(conf.ModulePrefs && conf.ModulePrefs.OAuth){
		isExistEditPref = true;
			
		var serviceList = conf.ModulePrefs.OAuth.Service;
		var oauthFieldSet = $.DIV({className:"configSet"}, $.P({className: "configSetHeader"},ISA_R.alb_oauthConsumerSettings));

		for(var i = 0; i < serviceList.length; i++){
			var serviceName = serviceList[i].name;
			oauthFieldSet.appendChild(
				$.TABLE({width:"100%"},
						$.TBODY({},
								$.TR({style:"backgroundColor:#eeeeee;"}, $.TH({colSpan:2,style:"textAlign:left;padding:2px 20px;"},serviceName)),
								$.TR({},
									 $.TD({width:"30%",style:"textAlign:right;"},ISA_R.alb_oauthSignatureAlgorithm),
									 $.TD({},$.SELECT({id:'oauth_signature_method_' + serviceName, onchange:{
									   handler:function(serviceName){
										   var oauthConsumerKeyInput = $('oauth_consumer_key_' + serviceName);
										   var oauthConsumerSecretInput = $('oauth_consumer_secret_' + serviceName)
										   if( 'HMAC-SHA1' == $F('oauth_signature_method_' + serviceName) ){
											   oauthConsumerKeyInput.disabled = oauthConsumerSecretInput.disabled = false;
										   }else{
											   oauthConsumerKeyInput.disabled = oauthConsumerSecretInput.disabled = true;
											   oauthConsumerKeyInput.value = oauthConsumerSecretInput.value = '';
										   }
									   }.bind(this, serviceName)}},
										  $.OPTION({value:'HMAC-SHA1'}, 'HMAC-SHA1'),
										  $.OPTION({value:'RSA-SHA1'}, 'RSA-SHA1'))
									   )
									   ),
								$.TR({},
									 $.TD({style:"textAlign:right;"},ISA_R.alb_oauthConsumerKey),
									 $.TD({},$.INPUT({id:'oauth_consumer_key_' + serviceName, style:"width:200px;"}))
									   ),
								$.TR({},
									 $.TD({style:"textAlign:right;"},ISA_R.alb_oauthConsumerSecret),
									 $.TD({},$.INPUT({id:'oauth_consumer_secret_' + serviceName, style:"width:200px;"}))
									   )
								  )
						  )
				);
			

			editConfNode.appendChild( oauthFieldSet );
		}
		
		var url = adminHostPrefix + "/services/authentication/getGetConsumerListJsonByUrl";
		var opt = {
		  method: 'post',
		  contentType: "application/json",
		  postBody: Object.toJSON([ String( type + "/gadget" ) ]),
		  asynchronous:true,
		  onSuccess: function(response){
			  var consumerSettingList = eval( response .responseText );
			  for(var i = 0; i < consumerSettingList.length; i++){
				  var consumer = consumerSettingList[i];
				  var serviceName = consumerSettingList[i].service_name;
				  $('oauth_signature_method_' + serviceName).value = consumer.signature_method;
				  var oauthConsumerKeyInput = $('oauth_consumer_key_' + serviceName);
				  var oauthConsumerSecretInput = $('oauth_consumer_secret_' + serviceName)
				  if('HMAC-SHA1' == consumer.signature_method){
					  oauthConsumerKeyInput.value = consumer.consumer_key;
					  oauthConsumerSecretInput.value = consumer.consumer_secret;
				  }else{
					  oauthConsumerKeyInput.disabled = oauthConsumerSecretInput.disabled = true;
				  }
			  }
		  }.bind(this),
		  onFailure: function(t) {
			  alert(ISA_R.ams_failedDeleteGadget );
			  msg.error(ISA_R.ams_failedDeleteGadget + t.status + " - " + t.statusText);
		  }
		}
		AjaxRequest.invoke(url, opt);
		
	}
	
	if(!isExistEditPref){
		var noEdit = document.createElement("div");
		noEdit.innerHTML = ISA_R.ams_noPossibleSettings;
		editConfNode.appendChild( noEdit );
	}

	return isExistEditPref;
}

ISA_WidgetConf.EditWidgetConf.makeEditPref = function(prefType, pref, widgetType){
	if(!pref.name) return false;
	var displayName = pref.display_name;
	if(!displayName){
		displayName = pref.name;
	}
	
	var textboxTr = document.createElement("tr");
	
	if(pref.datatype == 'hidden'){
		return null;
	}
	
	var textboxLabelTd = document.createElement("td");
	textboxLabelTd.style.textAlign = 'right';
	textboxLabelTd.style.width = '25%';
	textboxLabelTd.appendChild(document.createTextNode(displayName + ':'));
	textboxTr.appendChild(textboxLabelTd);
	
	var textboxTd = document.createElement("td");
	textboxTd.appendChild(ISA_WidgetConf.makeForm(prefType, pref, widgetType));
	textboxTr.appendChild(textboxTd);
	
	return textboxTr;
}

/**
 * prefType: From ID of Prefix
 * prefConf: WidgetPref/ModulePrefs/UserPref objects
 * prefValue: Widget setting values of menu and initial display settings
 * isDefaultHilitht: The function to highlight not default form is enabled or not
 */
ISA_WidgetConf.makeForm = function(prefType, prefConf, widgetType, prefValue, isDefaultHilight){
	var formContainer = document.createElement('span');
	
	var datatype = (prefConf.admin_datatype) ? prefConf.admin_datatype : prefConf.datatype;
	var defaultValue = (typeof prefConf.default_value != 'undefined') ? prefConf.default_value : (typeof prefConf.value != 'undefined') ? prefConf.value + '' : (datatype == 'bool') ? 'false' : '';

	if(typeof prefValue == 'undefined'){
		prefValue = defaultValue;
		if(!prefValue)prefValue = "";
	}
	
	var onChange = function(prefConf){
		/*var validator = ISA_WidgetConf.validators[prefConf.name];
		if(validator){
			var errors = validator(this);
			if( errors.length > 0){
				alert(errors.join("\n"));
				this.style.borderColor = "red";
				this.style.backgroundColor = "mistyrose";
				this.title = errors.join("\n");
				return;
			}
		}
		*/
		ISA_Admin.isUpdated = true;
		var value = '' + ISA_WidgetConf.getFormValue(prefType, prefConf);
		/*
		if (prefConf.regex || prefConf.required || prefConf.maxLength || prefConf.maxBytes) {
			var error = IS_Validator.validate(value, prefConf);
			if(error){
				alert(error);
				this.style.borderColor = "red";
				this.style.backgroundColor = "mistyrose";
				this.title = error;
				return;
			}
		}*/
		this.style.borderColor = '';
		this.style.backgroundColor = '';
		this.title = "";
		setDefaultHilight(this, value);

		checkFUP();
	};
	
	function checkFUP(){
		if(prefConf.datatype == 'hidden'){
			var fupHref = $('FUP_' + prefConf.name);
			if(fupHref)
			  fupHref.checked = true;
		}
	}

	function setDefaultHilight(element, value){
		if(!isDefaultHilight)return;
		if(defaultValue != value){
			prefConf.isDefault = false;
			element.parentNode.style.backgroundColor = 'gold';//for checkbox
			element.style.backgroundColor = 'lightyellow';
		}else{
			prefConf.isDefault = true;
			element.parentNode.style.backgroundColor = '';//for checkbox
			element.style.backgroundColor = (datatype =='list') ? '#efefef' :'';
		}
	}
	
	switch (datatype){
	  case 'xml':
	  case 'json':
	  case 'textarea':
		textarea = document.createElement("textarea");
		textarea.id = prefType + '_' + prefConf.name;
		textarea.rows = "12";
		textarea.wrap = "off";
		textarea.style.width = "100%";
		//Content may be inserted if prefConf is WidgetPref...
		textarea.value = prefValue || "";
		IS_Event.observe(textarea, 'change', onChange.bind(textarea, prefConf), false, "_widgetEditForm");
		formContainer.appendChild(textarea);
		setDefaultHilight(textarea, escapeHTMLEntity( prefValue ) );
		break;
		
	  case 'bool':
		var checkboxSpan = document.createElement("span");
		checkboxSpan.style.padding = '1px';
		
		var checkbox = document.createElement("input");
		checkbox.type = "checkbox";
		checkbox.id = prefType + '_' + prefConf.name;
		var checked = false;
		try {
			checked = eval( prefValue );
		} catch( ex ) {
			// ignore
		}
		checkbox.checked = checked;
		checkbox.defaultChecked = checked;
		
		checkboxSpan.appendChild(checkbox);
		
		IS_Event.observe(checkbox, 'change', onChange.bind(checkbox, prefConf), false, "_widgetEditForm");
		
		formContainer.appendChild(checkboxSpan);
		setDefaultHilight(checkbox, '' + prefValue);
		break;

	  case 'enum':
		
		var select = document.createElement("select");
		select.id = prefType + '_' + prefConf.name
		  
		var enumValues = prefConf.EnumValue;
		for ( var i=0; enumValues && i<enumValues.length; i++ ) {
			var option = document.createElement('option');
			var value = enumValues[i].value;
			if( value == null ) continue;
			var dValue;
			var name = enumValues[i].display_value;
			if (name == null || name.length == 0) {
				name = value;
			}
			if(prefConf.category){
				dValue = ISA_R["alb_"+prefConf.name+"_"+value];
				if(!dValue){
					dValue = name;
				}
			}else dValue = name;
			
			option.appendChild(document.createTextNode(dValue));
			option.value = value;
			
			if ( prefValue == value ) {
				option.selected = true;
			}
			select.appendChild(option);
		}
		IS_Event.observe(select, 'change', onChange.bind(select, prefConf), false, "_widgetEditForm");
		formContainer.appendChild(select);
		setDefaultHilight(select, prefValue);
		break;
		
	  case 'radio':
		//Create radio button
		var enumValues = prefConf.EnumValue;
		if(!enumValues)break;
		var radioDiv = document.createElement("span");
		radioDiv.id = prefType + '_' + prefConf.name;
		for ( var i=0; enumValues && i<enumValues.length; i++ ) {
			var value = enumValues[i].value;
			if( value == null || value.length == 0 ) continue;
			
			var name = enumValues[i].display_value;
			if (name ==null || name.length == 0) {
				name = value;
			}
			
			var radio = null;
			if(Browser.isIE) {
				radio = document.createElement('<input type="radio" name="'+prefType + '_' + prefConf.name +'>');
			} else {
				radio = document.createElement('input');
				radio.type = 'radio';
				radio.name = prefType + '_' + prefConf.name;
			}
			radio.value = value;
			radio.defaultChecked = prefValue == value;
			radioDiv.appendChild(radio);
			radioDiv.appendChild(document.createTextNode(name));
			IS_Event.observe(radio, 'change', onChange.bind(radioDiv, prefConf), false, "_widgetEditForm");
		}
		formContainer.appendChild(radioDiv);
		
		setDefaultHilight(radioDiv, prefValue);
		break;

	  case 'password':
		//Create password box
		var password = document.createElement("input");
		password.id = prefType + '_' + prefConf.name;
		password.type = 'password';
		password.value = prefValue;
		
		IS_Event.observe(password, 'change', onChange.bind(password, prefConf), false, "_widgetEditForm");
		
		formContainer.appendChild(password);
		setDefaultHilight(password, prefValue);
		break;

	  case 'list':
		formContainer.style.display = 'block'
		//Create menu for edit array
		var arrayData;
		try {
			arrayData = ( prefValue.length > 0 ? prefValue.split("|") : [] );//TODO:Separaeted by | for specification of gadget
		} catch (e) { }
		if( !arrayData || !( arrayData instanceof Array ) )
			arrayData = [prefValue]
		
		if(prefValue == undefined || prefValue == ''){
			arrayData = [];
		}
		
		for( var i=0;i<arrayData.length;i++ )
			arrayData[i] = arrayData[i].replace(/%7C/ig, "|");
		
		var arrayDiv = document.createElement("div");
		arrayDiv.style.textAlign = 'left';
		var listTable = document.createElement("table");
		var tableBody = document.createElement("tbody");
		tableBody.id = prefType + '_' + prefConf.name;
		listTable.appendChild( tableBody );
		arrayDiv.appendChild( listTable );
		
		var deleteFunc = function(obj){
			ISA_Admin.isUpdated = true;
			var deleteNode = obj.parentNode.parentNode;
			deleteNode.parentNode.removeChild( deleteNode );
			var value = '' + ISA_WidgetConf.getFormValue(prefType, prefConf);
			setDefaultHilight(arrayDiv, value);
			checkFUP();
		};
		
		for(j=0; j<arrayData.length; j++){
			var item = document.createElement("tr");
			var textTd = document.createElement("td");
			textTd.appendChild( document.createTextNode( arrayData[j] ) );
			
			var buttonTd = document.createElement("td");
			var deleteButton = document.createElement("span");
			deleteButton.appendChild( document.createTextNode( ISA_R.alb_delete ) );
			deleteButton.style.color = '#7777cc';
			deleteButton.style.cursor = 'pointer';
			IS_Event.observe(deleteButton, 'click', deleteFunc.bind(this, deleteButton), false, '_editorForm');
			buttonTd.appendChild( deleteButton );
			
			item.appendChild( textTd );
			item.appendChild( buttonTd );
			tableBody.appendChild( item );
		}
		
		var addItem = document.createElement("tr");
		var addBoxTd = document.createElement("td");
		addBoxTd.innerHTML = '<input name="input" type="text" name="'+prefType + '_' +prefConf.name+'" value=""/>';
		var addButtonTd = document.createElement("td");
		var addButton = document.createElement("span");
		addButton.appendChild( document.createTextNode( ISA_R.alb_add ) );
		addButton.style.color = '#7777cc';
		addButton.style.cursor = 'pointer';
		var addFunc = function(prefConf){
			ISA_Admin.isUpdated = true;
			var addButtonObj = this;
			var addText = addButtonObj.parentNode.previousSibling.firstChild.value || "";
//			if(addText != ''){
				var addItem = document.createElement("tr");
				var textTd = document.createElement("td");
				textTd.appendChild( document.createTextNode( addText ) );
				
				var buttonTd = document.createElement("td");
				var deleteButton = document.createElement("span");
				deleteButton.appendChild( document.createTextNode( ISA_R.alb_delete ) );
				deleteButton.style.color = '#7777cc';
				deleteButton.style.cursor = 'pointer';
				IS_Event.observe(deleteButton, 'click', deleteFunc.bind(this, deleteButton), false, '_editorForm');
				buttonTd.appendChild( deleteButton );
				
				addItem.appendChild( textTd );
				addItem.appendChild( buttonTd );
				
				//dojo.dom.insertBefore(addItem, addButtonObj.parentNode.parentNode);
				
				var siblingNode = addButtonObj.parentNode.parentNode;
				siblingNode.parentNode.insertBefore(addItem, siblingNode);
				addBoxTd.firstChild.value="";
				
				var value = '' + ISA_WidgetConf.getFormValue(prefType, prefConf);
				setDefaultHilight(arrayDiv, value);
				checkFUP();
//			}
		};
		IS_Event.observe(addButton, 'click', addFunc.bind(addButton, prefConf), false, '_editorForm');
		addButtonTd.appendChild( addButton );
		
		addItem.appendChild( addBoxTd );
		addItem.appendChild( addButtonTd );
		tableBody.appendChild( addItem );
		
		formContainer.appendChild(arrayDiv);
		setDefaultHilight(arrayDiv, prefValue);
		break;
		
	  case 'calendar':
		var textbox = document.createElement("input");
		textbox.type = "text";
		textbox.id = prefType + '_' + prefConf.name;
		textbox.value = prefValue;
		
		formContainer.appendChild(textbox);
		
		var dateFormat;
		//if( widget.widgetPref.dateFormat )
		//  dateFormat = widget.widgetPref.dateFormat.value;
		
		if( !dateFormat )
		  dateFormat = "YYYY/MM/DD";
		
		if( prefValue ) {
			var date = CalendarInput.parseDate("YYYY/MM/DD",prefValue );
			if( date ) {
				textbox.value = CalendarInput.toDateString( dateFormat,date );
			}
		}
		
		prefConf.calendar = new CalendarInput( textbox, dateFormat, onChange.bind(textbox, prefConf) );
		prefConf.calendar.contents.style.zIndex = 10000;
		setDefaultHilight(textbox, prefValue);
		break;

	  case 'url':
		formContainer.style.whiteSpace = "nowrap";

		var textboxSpan = document.createElement("span");
		var textbox = document.createElement("input");
		textbox.type = "text";
		textbox.id = prefType + '_' + prefConf.name;
		textbox.value = prefValue;
		textbox.size = "74";
		textbox.maxLength = "1024";
		
		IS_Event.observe(textbox, 'change', onChange.bind(textbox, prefConf), false, "_widgetEditForm");
		textboxSpan.appendChild(textbox);
		formContainer.appendChild(textboxSpan);
		setDefaultHilight(textbox, prefValue);
		
		// Button for obtaining header information
		var getHeaderButton = document.createElement("input");
		getHeaderButton.value = ISA_R.alb_getTtitleInfromation;
		getHeaderButton.type = "button";
		formContainer.appendChild(getHeaderButton);
		IS_Event.observe( 
			getHeaderButton, 
			'click',
			function() {
				var inputUrl = $F(prefType + "_url");
				
				var indicator = $("indicatorMini");
				if (!indicator) {
					var indicator = document.createElement("img");
					indicator.src = imageURL + "ajax-loader.gif";
					indicator.style.top = "3px";
					indicator.id = "indicatorMini";
					formContainer.appendChild(indicator);
				}
				indicator.style.visibility = "visible";
				
				var getTitleFunc = function(response){
					try{//if(response.getRequestHeader) always return false in IE
						var _authType = response.getResponseHeader("MSDPortal-AuthType");
					}catch(e){}
					if(_authType){
						IS_Request.createModalAuthFormDiv(
							ISA_R.alb_getTtitleInfromation,
							getHeaderButton,
							function (_authUid, _authPassword){
								if( !(_authUid) ) {
									var indicatorMini = $("indicatorMini");
									if(indicatorMini)indicatorMini.style.visibility = "hidden";
									return;
								}
								authUid = _authUid;
								authPassword = (_authPassword) ? _authPassword : " ";
								is_processUrlContents(inputUrl, getTitleFunc, function(){}, ["authType", _authType, "authuserid",authUid,"authpassword",authPassword]);
							},
							false //isModal
							  );
						return;
					}
					
					// Obtain TITLE,HREF
					try{
						var dataList = eval("("+response.responseText+")");
						var length = dataList.length;
						if(length > 0 && !/true/.test(dataList[0].isError)){
							var titleForm = $("formPropertyValueTitle");
							if(!titleForm) titleForm = $("formTitle");
							
							var title = dataList[0].directoryTitle || dataList[0].title || ""
							if( titleForm ) {
								if( !title || title.length == 0 )
									title = ISA_R.alb_noTitle;
								
								title = (""+title).substring(0,80);
								
								titleForm.value = title;
								
								if( dataList[0].directoryTitle ) {
									var input = $("formOriginalTitle");
									if( !input ) {
										input = document.createElement("input");
										input.id = "formOriginalTitle";
										input.style.display = "none";
										
										titleForm.parentNode.appendChild( input );
									}
									
									input.value = (""+( dataList[0].title || "")).substring(0,80);
								}
							}
							
							var hrefForm = $("formPropertyValueHref");
							if(!hrefForm) hrefForm = $("formHref");
							
							var href = dataList[0].href || "";
							if( href.length > 256 )
							  href = is_getTruncatedString( href,1024 );
							
							if(hrefForm){
								hrefForm.value = href;
							}else if( /true/.test(dataList[0].isError )) {
								alert(ISA_R.ams_failedGetTitleInfo+"\n\n"
									  +"「"+dataList[0].errorMsg+"」");
							}
						}
					}catch(e){msg.error(e)}
					$("indicatorMini").style.visibility = "hidden";
				};
				is_processUrlContents(inputUrl, getTitleFunc );
			}.bind(this),false );
		
		
		IS_Event.observe( textbox,"change",function() {
			var inputXPathField = $( prefType+"_xPath");
			if( !inputXPathField ) return;
			
			inputXPathField.value = "";
			inputXPathField.style.borderColor = '';
			inputXPathField.style.backgroundColor = '';
			inputXPathField.title = "";
			
			var selectXPathPanel = $("selectXPathPanel");
			if(selectXPathPanel) selectXPathPanel.style.display = "none";
		},false,"_widgetEditForm");
		
		break;
		
	  case 'xPath':
		formContainer.style.whiteSpace = "nowrap";
		
		var textboxSpan = document.createElement("span");
		var inputXPathField = document.createElement("input");
		inputXPathField.type = "text";
		inputXPathField.disabled = true;
		inputXPathField.id = prefType + '_' + prefConf.name;
		inputXPathField.value = prefValue;
		inputXPathField.size = "74";
		textboxSpan.appendChild(inputXPathField);
		formContainer.appendChild(textboxSpan);
		setDefaultHilight(inputXPathField, prefValue);
		
		var inputSelectXPathButton = document.createElement("input");
		inputSelectXPathButton.type = 'button';
		inputSelectXPathButton.id = 'inputSelectXPathButton';
		inputSelectXPathButton.value = ISA_R.alb_selectFragmentArea;
		formContainer.appendChild(inputSelectXPathButton);
		
		var selectXPathPanel = document.createElement('div');
		selectXPathPanel.id = "selectXPathPanel";
		selectXPathPanel.style.height = "400px";
		selectXPathPanel.style.display = "none";
		formContainer.appendChild(selectXPathPanel);
		
		function displaySelectFragmentHTML(){
			var widgetConf = IS_WidgetConfiguration[widgetType];
			if(widgetConf.Content && widgetConf.Content.className) {
				var validator = IS_Widget[widgetConf.Content.className].validateUserPref;
				if(validator) {
					var errors = [];
					['url','charset'].each(function(prefName){
						if(validator[prefName]) {
							var value = $F(prefType + '_' + prefName);
							var error = validator[prefName](value);
							if(error) {
								var prefForm = $(prefType + '_' + prefName);
								if (!prefForm.disabled) {
									prefForm.style.borderColor = "red";
									prefForm.style.backgroundColor = "mistyrose";
								}
								selectXPathPanel.style.display = 'none';
								errors.push(error);
							}
						}
					});
					if(errors.length > 0){
						alert(errors.join('\n'));
						return;
					}
				}
			}
			
			var authType = $F(prefType + "_authType");
			
			ISA_WidgetConf.EditWidgetConf.displayFragmentModal(prefType, $F(prefType + '_url'), $F(prefType + '_charset'), authType,
															   function(){
																   	var value = '' + ISA_WidgetConf.getFormValue(prefType, prefConf);
																   setDefaultHilight(inputXPathField, value);
															   } );
		}
		IS_Event.observe(inputSelectXPathButton, 'mousedown',  function() {
			setTimeout( displaySelectFragmentHTML,300 );
		}, false, "_widgetEditForm");
		break;
	  case 'cacheID':
		
		var checkboxSpan = document.createElement("span");
		checkboxSpan.style.padding = '1px';
		
		var isCacheCheck = document.createElement("input");
		isCacheCheck.type = "checkbox";
		isCacheCheck.id = "formPropertyValueIsCache";
		isCacheCheck.checked = prefValue ? true : false;
		isCacheCheck.defaultChecked = prefValue ? true : false;
		checkboxSpan.appendChild(isCacheCheck);
		  
		var cacheIDForm = document.createElement("input");
		cacheIDForm.type = "hidden";
		cacheIDForm.id = prefType + '_' + prefConf.name;
		cacheIDForm.value = prefValue; 
		
		IS_Event.observe(isCacheCheck, "change", function(){
			if(this.checked ){
				var urlValue = $F(prefType + '_url');
				var xpathValue = $F(prefType + '_xPath');
				if( urlValue.length>0 && xpathValue.length>0){
					$('UP_cacheID').value = encodeURIComponent(urlValue + xpathValue);
				}else{
					this.checked = false;
					alert(ISA_R.ams_typeWebURL);
				}
			}else{
				$('UP_cacheID').value = "";
			}
			setDefaultHilight(isCacheCheck, $('UP_cacheID').value);
		}.bind(isCacheCheck, prefConf), false, "_widgetEditForm");
		formContainer.appendChild(checkboxSpan);
		formContainer.appendChild(cacheIDForm);
		setDefaultHilight(isCacheCheck, prefValue);
		break;
		
	  case 'authType':
		
		var select = document.createElement("select");
		select.id = prefType + '_' + prefConf.name;
		  
		var enumValues = prefConf.EnumValue;
		var authType = prefValue.split(' ')[0];
		for ( var i=0; enumValues && i<enumValues.length; i++ ) {
			var option = document.createElement('option');
			var value = enumValues[i].value;
			if( value == null ) continue;
			var dValue;
			var name = enumValues[i].display_value;
			if (name == null || name.length == 0) {
				name = value;
			}
			if(prefConf.category){
				dValue = ISA_R["alb_"+prefConf.name+"_"+value];
				if(!dValue){
					dValue = name;
				}
			}else dValue = name;
			
			option.appendChild(document.createTextNode(dValue));
			option.value = value;
			
			if ( authType == value ) {
				option.selected = true;
			}
			select.appendChild(option);
		}
		
		var authUidPasswdParamParams = prefValue.split(' ')[1];
		var authUidParamName = false;
		var authPasswdParamName = false;
		if(authUidPasswdParamParams){
			authUidParamName = authUidPasswdParamParams.split(':')[0];
			authPasswdParamName = authUidPasswdParamParams.split(':')[1];
			if(authUidParamName) authUidParamName =decodeURIComponent( authUidParamName );
			if(authPasswdParamName) authPasswdParamName = decodeURIComponent(authPasswdParamName);
		}
		var authUidParamSpan = document.createElement("span");
		authUidParamSpan.appendChild(document.createElement("br"));
		authUidParamSpan.id = 'auth_uid_name_' + prefType + '_' + prefConf.name;
		authUidParamSpan.style.fontSize = '90%';
		authUidParamSpan.style.backgroundColor = '#FFF';
		//'User ID parameter/Header name：'
		authUidParamSpan.appendChild(document.createTextNode(ISA_R.alb_paramNameOfUserId));
		var authUidParamTextbox = document.createElement("input");
		authUidParamTextbox.id = 'auth_uid_name_form_' + prefType + '_' + prefConf.name;
		if(	authType && (authType.indexOf('post') == 0 || authType.indexOf('sendPortalCredentialHeader')==0)){
			authUidParamSpan.style.display = '';
		}else{
			authUidParamSpan.style.display = 'none';
		}

		if(authUidParamName)
		  authUidParamTextbox.value = authUidParamName;
		authUidParamSpan.appendChild(authUidParamTextbox);
		
		var authPasswdParamSpan = document.createElement("span");
		authPasswdParamSpan.appendChild(document.createElement("br"));
		authPasswdParamSpan.id = 'auth_passwd_name_' + prefType + '_' + prefConf.name;
		authPasswdParamSpan.style.fontSize = '90%';
		authPasswdParamSpan.style.backgroundColor = '#FFF';
		//'Password parmeter name：'
		authPasswdParamSpan.appendChild(document.createTextNode(ISA_R.alb_paramNameOfPasswd));
		var authPasswdParamTextbox = document.createElement("input");
		authPasswdParamTextbox.id = 'auth_passwd_name_form_' + prefType + '_' + prefConf.name;
		if(authType.indexOf('postCredential') != 0)
			authPasswdParamSpan.style.display = 'none';
		if(authPasswdParamName)
		  authPasswdParamTextbox.value = authPasswdParamName;
		authPasswdParamSpan.appendChild(authPasswdParamTextbox);
		
		function onChangeAuthType(prefType, prefConf){
			var authType = $F(prefType + '_' + prefConf.name);
			var authUidParamSpan = $('auth_uid_name_' + prefType + '_' +  prefConf.name);
			$('auth_uid_name_form_' + prefType + '_' +  prefConf.name).value = '';
			var authPasswdParamSpan = $('auth_passwd_name_' + prefType + '_' +  prefConf.name);
			$('auth_passwd_name_form_' + prefType + '_' +  prefConf.name).value = '';
			if(authType.indexOf('postCredential') == 0){
				Element.show(authUidParamSpan);
				Element.show(authPasswdParamSpan);
			}else if(authType.indexOf('postPortalCredential') == 0 || authType.indexOf('sendPortalCredentialHeader') == 0){
				Element.show(authUidParamSpan);
				Element.hide(authPasswdParamSpan);
			}else{
				Element.hide(authUidParamSpan);
				Element.hide(authPasswdParamSpan);
			}
			onChange.bind(this)(prefConf);
		}
		IS_Event.observe(select, 'change', onChangeAuthType.bind(select, prefType, prefConf), false, "_widgetEditForm");
		IS_Event.observe(authUidParamTextbox, 'change', onChange.bind(select, prefConf), false, "_widgetEditForm");
		IS_Event.observe(authPasswdParamTextbox, 'change', onChange.bind(select, prefConf), false, "_widgetEditForm");
		formContainer.appendChild(select);
		formContainer.appendChild(authUidParamSpan);
		formContainer.appendChild(authPasswdParamSpan);
		setDefaultHilight(select, prefValue);
		break;
		
	  default://Nothing|hidden|string
		var textbox = document.createElement("input");
		textbox.type = "text";
		textbox.style.width = '200px';
		textbox.id = prefType + '_' + prefConf.name;
		textbox.value = prefValue ? prefValue : "";
		IS_Event.observe(textbox, 'change', onChange.bind(textbox, prefConf), false, "_widgetEditForm");
		formContainer.appendChild(textbox);
		setDefaultHilight(textbox, prefValue);
		break;
	}
	
	return formContainer;
}

ISA_WidgetConf.EditWidgetConf.save = function(type, conf, onSuccess, onError){
	
	var attributes = [
		{name:'autoRefresh', datatype:'bool'},
		{name:'backgroundColor', datatype:'string'}
	]
	attributes.each(function(attribute){
		conf[attribute.name] = ISA_WidgetConf.getFormValue( 'ATTR', attribute);
	});
	
	var errorMsgs = [];
	var error = false;
	
	var widgetPrefList = conf.WidgetPref;
	for( id in widgetPrefList ){
		if( !( widgetPrefList[id] instanceof Function ) ){
			var widgetPref = widgetPrefList[id];
			var value = ISA_WidgetConf.getFormValue( 'MP', widgetPref );
			if(value != null)
				widgetPref.value = value;
			
			if(error = ISA_WidgetConf.EditWidgetConf.validate(conf, 'MP', widgetPref, value)){
				errorMsgs.push(error);
			}
		}
	}
	var modulePrefList = conf.ModulePrefs;
	for( id in modulePrefList ){
		if( !( modulePrefList[id] instanceof Function ) ){
			var widgetPref = modulePrefList[id];
			if( !widgetPref.name ) {
				widgetPref = {
					name: id
				};
			}
			if( id == "autoRefresh" || id == "singleton")
				widgetPref.datatype = "bool";
			
			var value = ISA_WidgetConf.getFormValue( 'MP',widgetPref );
			if(value != null){
				if( id == "title" &&( !value.replace || value.replace(/ |　/g, "") == 0 )) {
					errorMsgs.push(ISA_R.ams_typeTitle);
					continue;
				}
				
				modulePrefList[id] = value;
			}
			if(error = ISA_WidgetConf.EditWidgetConf.validate(conf, 'MP', widgetPref, value)){
				errorMsgs.push(error);
			}
		}
	}
	
	var userPrefList = conf.UserPref;
	for( id in userPrefList ){
		if( !( userPrefList[id] instanceof Function ) ){
			var userPref = userPrefList[id];
			var value = ISA_WidgetConf.getFormValue( 'UP', userPref );
			if(value != null)
				userPref.default_value = value;
			
			if(error = ISA_WidgetConf.EditWidgetConf.validate(conf, 'UP', userPref, value)){
				errorMsgs.push(error);
			}
		}
	}

	var oauthServiceList = false;
	if(conf.ModulePrefs && conf.ModulePrefs.OAuth){
		oauthServiceList = [];
		serviceList = conf.ModulePrefs.OAuth.Service
		for(var i = 0; i < serviceList.length; i++){
			var serviceName = serviceList[i].name;
			oauthServiceList.push({
			  serviceName: serviceName,
			  signatureMethod: $F('oauth_signature_method_' + serviceName),
			  consumerKey: $F('oauth_consumer_key_' + serviceName),
			  consumerSecret: $F('oauth_consumer_secret_' + serviceName)
			});
		}
		
	}
	if(errorMsgs.length > 0){
		alert(errorMsgs.join('\n'));
		return;
	}
	
	ISA_WidgetConf.EditWidgetConf.requestSaveConf(type, conf, oauthServiceList, onSuccess, onError);

}

ISA_WidgetConf.EditWidgetConf.validate = function(conf, prefix, pref, value){
	if(conf.Content && conf.Content.className && IS_Widget[conf.Content.className]){
		var validator = IS_Widget[conf.Content.className].validateUserPref;
		if(validator && validator[pref.name]) {
			var prefForm = $(prefix + '_' + pref.name);
			var error = false;
			if (prefForm && (error = validator[pref.name](value))) {
				if (!prefForm.disabled) {
					prefForm.style.borderColor = "red";
					prefForm.style.backgroundColor = "mistyrose";
				}
				return error;
			}
		}
	}
	return false;
}

ISA_WidgetConf.EditWidgetConf.requestSaveConf = function(type, conf, oauthServiceList, onSuccess, onError){
	var url = adminHostPrefix + "/services/" + ( (conf.ModulePrefs) ? "gadget/updateGadget" :  "widgetConf/updateWidgetConf");
	var opt = {
		method: 'post' ,
		contentType: "application/json",
		asynchronous:true,
		postBody: Object.toJSON([type, Object.toJSON(conf), Object.toJSON(oauthServiceList)]),
		onSuccess: function(response){
			if(onSuccess){
				onSuccess(response);
			}
		},
		onFailure: function(t) {
			alert(ISA_R.ams_failedCommitWidget);
			msg.error(ISA_R.ams_failedCommitWidget + t.status + " - " + t.statusText);
			if(onError){
				onError(ISA_R.ams_failedCommitWidget + t.status + " - " + t.statusText);
			}
		},
		onException: function(r, t){
			alert(ISA_R.ISA_R.ams_failedCommitWidget);
			msg.error(ISA_R.ISA_R.ams_failedCommitWidget + getErrorMessage(t));
			if(onError){
				onError(ISA_R.ISA_R.ams_failedCommitWidget + getErrorMessage(t));
			}
		}
	};
	AjaxRequest.invoke(url, opt);
}

ISA_WidgetConf.validators = {
	title:function(value){
		return IS_Validator.validate(value, {
			label: ISA_R.alb_title,
			required: true,
			regex: '.*[^ 　].*',
			regexMsg: ISA_R.ams_requiredItem,
			maxBytes: 80
		});
	},
	titleLink:function(value){
		return IS_Validator.validate(value, {
			label: ISA_R.alb_titleLink,
			maxBytes: 1024
		});
	}
}
ISA_WidgetConf.gadgetValidators = {
	url:function(value){
		return IS_Validator.validate(value, {
			label: ISA_R.alb_gadgetUrl,
			required: true,
			regex: '^((?:http)|(?:https)|(?:ftp))://',
			regexMsg: ISA_R.ams_typeValidURL,
			maxBytes: 1022
		});
	}
}

ISA_WidgetConf.getFormValue = function(prefType, pref){
	
	var div = $( prefType + '_' + pref.name );
	if(!div) return;
	
	var datatype = (pref.admin_datatype) ? pref.admin_datatype : pref.datatype;
	switch ( datatype ){
      case 'bool':
		return div.checked;
      case 'radio':
		var radios = div.childNodes;
		for(var i=0; i<radios.length; i++){
			if( radios[i].checked ){
				return radios[i].value;
			}
		}
		return;
	  case 'calendar':
		var dateFormat = "YYYY/MM/DD";
		
		var inputValue = $F(prefType + '_' + pref.name);
		var date = CalendarInput.parseDate( dateFormat,inputValue );
		if( date ) {
			return date.getFullYear()+"/"+(date.getMonth()+1)+"/"+date.getDate();
		} else {
			return inputValue;
		}
	  case 'list':
		var array = [];
		var trList = div.getElementsByTagName('tr');
		for(var j=0; j<(trList.length -1); j++){
			var value = trList[j].firstChild.firstChild.nodeValue;
			array.push( String( value ).replace(/\|/g, "%7C") );
		}
		return array.join("|");
	  case 'authType':
		var authType = $F(prefType + '_' + pref.name);
		var authUid = $F('auth_uid_name_form_' + prefType + '_' +  pref.name);
		var authPasswd = $F('auth_passwd_name_form_' + prefType + '_' +  pref.name);
		return authType  +  (authUid ? ' ' + encodeURIComponent(authUid) : '') + (authPasswd ? ':' + encodeURIComponent(authPasswd) : '');
      default:
		return $F(prefType + '_' + pref.name);
	}
}
