var authorizations;
var ISA_CommonModals = IS_Class.create();

ISA_CommonModals.EditorForm = IS_Class.create();
ISA_CommonModals.EditorForm.prototype.classDef = function() {
	var self = this;
	var editorElement;
	var menuItem;
	var disabled;
	var disabledAttribute;
	var menuObj;

	var options;
	var callbackFunc;
	/**
	 * Specify option below
	 * 
	 * formDisabled: ability of edit form	(default=false)
	 * setDefaultValue: ability of replacing undifined value to defalut(default=true)
	 * displayWidgetFieldSet : ability of displaying field of selecting widget type (default=true)
	 */
	this.initialize = function(_editorElement, _callbackFunc){
		var defaults = {
		  formDisabled : false,
		  setDefaultValue : true,
		  displayMenuFieldSet : true,
		  menuFieldSetLegend : ISA_R.alb_menuLinksAndWHS,
		  displayWidgetFieldSet : true,
		  displayMenuTreeAdminsFieldSet : false,
		  displayACLFieldSet : true,
		  displayAlertFieldSet: false,
		  displayForceUpdatePropertyForm: false,
		  displayOK: true,
		  disabledAlertFieldSet: false,
		  generateId : false,
		  omitTypeList:[],
		  showServiceURL:false,
		  showIgnoreHeaderForm:false,
		  showNoBorderForm:false,
		  showMenuExplorer:false,
		  disableTypeEdit:false,
		  disableUploadGadget: false,
		  disableMiniBrowserHeight: false,
		  disableAuthType: false,
		  disableURL: false,
		  disableDetailDisplayMode: false,
		  disablePreview: false,
		  disableDisplayRadio:false
		};
		editorElement = _editorElement;
		
		options = Object.extend(defaults, arguments[2] || {});

		options.omitTypeList.push('notAvailable');
		
		callbackFunc = _callbackFunc;
		if(options.formDisabled) {
			disabled = "true";
			disabledAttribute = " disabled='disabled'";
		}
		
		this.initModal();
		
		authorizations = [];
		
		//TODO: Temporary response for the judgement of widgetType validation by clearing editWidgetType
		ISA_WidgetConf.widgetConf.editWidgetType = false;
	};
	
	this.initModal = function(){
		this.currentModal = new Control.Modal('', {
		  className:"adminTreeMenu",
		  afterClose: function(){
//		  	this.destroy();
			this.currentModal.destroy();
			this.currentModal = null;
  			IS_Event.unloadCache("_editorForm");
			IS_Event.unloadCache("_adminMenuEdit");
		  }.bind(this)
		});
	}
	
	//TODO:Valid only at widget setting
	function makeExecButton(){
		var buttonDiv = document.createElement('div');
		buttonDiv.style.textAlign = "center";
		
		if (options.displayOK) {
			var elementInput = document.createElement("input");
			elementInput.className = "modal_button";
			elementInput.type = "button";
			elementInput.id = "formExec";
			elementInput.name = "FORM_EXEC";
			elementInput.value = menuItem.isDelete? ISA_R.alb_delete : ISA_R.alb_entry;
			menuItem.isDelete = null;
			IS_Event.observe(elementInput, 'click', self.submitEditorForm.bind(self), false, "_adminMenu");
			buttonDiv.appendChild(elementInput);

		}
		
		var closeButton = document.createElement("input");
		closeButton.className = "modal_button";
		closeButton.type = "button";
		closeButton.id = "formCancel";
		closeButton.value = ISA_R.alb_cancel;
		IS_Event.observe(closeButton, 'click', self.hideWidgetEditorForm.bind(self), false, "_adminMenu");
		buttonDiv.appendChild(closeButton);
		return buttonDiv;
	}

	this.submitEditorForm = function() {
		ISA_Admin.isUpdated = true;
		// Disable execute button
		var formExecElm = $("formExec");
		var formCancelElm = $("formCancel");
		formExecElm.disabled = true;
		formCancelElm.disabled = true;
		
		if(!ISA_CommonModals.EditorForm.checkEditorForm()){
			formExecElm.disabled = false;
			formCancelElm.disabled = false;
			return;
		}
		
		if(callbackFunc.call(this, menuItem)){
			formExecElm.disabled = false;
			formCancelElm.disabled = false;
		}
		
	}
	
	this.loadEditorForm = function(editorFormFieldDiv) {
		var editorsFormDiv = document.createElement("div");
		editorsFormDiv.id = "editorsFormDiv";
		var showEditorsForm = document.createElement("form");
		showEditorsForm.id = "showEditorsForm";
		if(options.generateId)menuItem.id = "_" + new Date().getTime();

		if(options.showMenuExplorer){
			showEditorsForm.appendChild(
				ISA_CommonModals.EditorForm.makeWidgetInstListFieldSet(false, menuItem)
				);
		}
		if(options.displayMenuFieldSet){
			showEditorsForm.appendChild(
				ISA_CommonModals.EditorForm.makeMenuItemEditFieldSet(disabled, menuItem, options)
				);
		}
		
		if(options.displayWidgetFieldSet){
		  showEditorsForm.appendChild(
			  ISA_CommonModals.EditorForm.makeWidgetEditFieldSet(disabled, menuItem, options)
			  );
		}
		if(options.displayMenuTreeAdminsFieldSet){
			showEditorsForm.appendChild(
				ISA_CommonModals.EditorForm.makeMenuTreeAdminsFieldSet(disabled, menuItem)
				);
		}
		if(options.displayACLFieldSet){
			showEditorsForm.appendChild(
				ISA_CommonModals.EditorForm.makeMenuItemACLEditFieldSet(disabled, menuItem)
				);
		}
		if(options.displayAlertFieldSet){
			showEditorsForm.appendChild(ISA_CommonModals.EditorForm.makeMenuAlertEditFieldSet (disabled || options.disabledAlertFieldSet, menuItem));
		}
		
		if(options.displayForceUpdatePropertyForm){
			showEditorsForm.appendChild(ISA_CommonModals.EditorForm.makeMenuUpdateSettingFieldSet(disabled, menuItem));
		}
		editorsFormDiv.appendChild(showEditorsForm);
		editorFormFieldDiv.appendChild(editorsFormDiv);
		
		/* Execute button */
		editorFormFieldDiv.appendChild(makeExecButton());

	};

	this.hideWidgetEditorForm = function(){
		IS_Event.unloadCache("_editorForm");
		IS_Event.unloadCache("_adminMenuEdit");
		if( Control.Modal.current ) {
			Control.Modal.close();
		} else {
			Control.Modal.container.hide();
		}
	};

	this.showWidgetEditorForm = function (){
		if(!this.currentModal)
			this.initModal();
		
		var viewFormArea = function(){
			var editorFormFieldDiv = document.createElement("div");
			editorFormFieldDiv.id = 'editorFormFieldDiv';
			self.loadEditorForm(editorFormFieldDiv);
			self.currentModal.container.update(editorFormFieldDiv);
			self.currentModal.open();
		}

		setTimeout(viewFormArea, 10);
	};

	this.showEditorForm = function(_menuItem){
		menuItem = _menuItem;
		this.showWidgetEditorForm();
	}
};


ISA_CommonModals.EditorForm.isIgnoreHeader = function(){
	var ignoreHeader = $("ignoreHeaderCheckbox");
	if(ignoreHeader)
		return ignoreHeader.checked;
}
ISA_CommonModals.EditorForm.isNoBorder = function(){
	var noBorder = $("noBorderCheckbox");
	if(noBorder)
		return noBorder.checked;
}

ISA_CommonModals.EditorForm.checkEditorForm = function(){
	var errorMessages = [];
	var titleForm = $("formTitle");
	var error = null;
	if(titleForm && (error = ISA_WidgetConf.validators.title($F(titleForm))))
	  errorMessages.push(error);
	
	var formHref = $("formHref");
	if(formHref && (error = ISA_WidgetConf.validators.titleLink($F(formHref))))
	  errorMessages.push(error);

	// Check with required
	var widgetType = ISA_CommonModals.EditorForm.getSelectType();
	
	validate_prefs:
	if(widgetType){
		if("MultiRssReader" == widgetType) widgetType = "RssReader";
		var conf = ISA_SiteAggregationMenu.widgetConfs[widgetType];
		
		if(!conf)
			break validate_prefs;
		
		var userPrefs = conf.UserPref;
		for( i in userPrefs){
			if( !( userPrefs[i] instanceof Function ) ){
				var userPref = userPrefs[i];
				var prefForm = $('UP_' + userPref.name);
				if(!prefForm)continue;
				
				var validator = null;
				if(widgetType == 'Gadget'){
					validator = ISA_WidgetConf.gadgetValidators[userPref.name];
				} else if(conf.Content && conf.Content.className){
					var validators = IS_Widget[conf.Content.className].validateUserPref;
					if(validators) validator = validators[userPref.name];
				}
				if(validator){
					var value = ISA_WidgetConf.getFormValue('UP', userPref);
					var error = validator(value);
					if(error){
						if (!prefForm.disabled) {
							prefForm.style.borderColor = "red";
							prefForm.style.backgroundColor = "mistyrose";
						}
						errorMessages.push(error);
					}
				}
			}
		}
	}
	if(errorMessages.length > 0){
		alert(errorMessages.join("\n"));
		return false;
	}else{
		return true;
	}
}

ISA_CommonModals.EditorForm.getSelectType = function(){
	var typeForm = $("widgetType");
	var widgetType = "";
	if(typeForm){
		var selectType = typeForm.value;
		if(selectType == ISA_R.alb_others){
			var widgetSelect = $("otherWidgetSelect");
			if(widgetSelect.selectedIndex < 0){
				alert(ISA_R.ams_selectWidget);
				// Disable execute button
				$("formExec").disabled = false;
				$("formCancel").disabled = false;
				return;
			}
			var selectedOption = widgetSelect.childNodes[ widgetSelect.selectedIndex ];
			var confType = selectedOption.value;
			var widgetConf = ISA_SiteAggregationMenu.widgetConfs[ confType ];
			if(widgetConf)
				widgetType = widgetConf.type;
		}else{
			if("MiniBrowser" == selectType){
				var formIsFragmentMiniBrowser = $("isFragmentMiniBrowser");
				if(formIsFragmentMiniBrowser && formIsFragmentMiniBrowser.checked ){
					widgetType = "FragmentMiniBrowser";
				}else{
					widgetType = "MiniBrowser";
				}
			}else{
				widgetType = selectType;
			}
		}
	}
	return widgetType;
}

ISA_CommonModals.EditorForm.getProperty = function(menuItem){
	if(!menuItem.type || !ISA_SiteAggregationMenu.widgetConfs[menuItem.type])return {};
	menuItem.properties = (menuItem.properties)? menuItem.properties : {};
	var properties = menuItem.properties;
	
	var widgetType = menuItem.type;
	if("MultiRssReader" == widgetType) widgetType = "RssReader";
	
	var conf = ISA_SiteAggregationMenu.widgetConfs[widgetType];
	if("MultiRssReader" == widgetType) widgetType = "RssReader";
	var widgetPrefList = conf.WidgetPref;
	for( id in widgetPrefList ){
		if( !( widgetPrefList[id] instanceof Function ) ){
			var widgetPref = widgetPrefList[id];
			var value = ISA_WidgetConf.getFormValue( 'MP', widgetPref );
			if(value != null){
				alert("not use block!");
				if(!widgetPref.isDefault){
//					delete properties[userPref.name];
					is_deleteProperty(properties, userPref.name);
				}else{
					properties[widgetPref.name] = value;
				}
			}
		}
	}
	
	var userPrefList = conf.UserPref;
	for( id in userPrefList ){
		if( !( userPrefList[id] instanceof Function ) ){
			var userPref = userPrefList[id];
			var value = ISA_WidgetConf.getFormValue( 'UP', userPref );
			if(value != null){
				if(userPref.isDefault){
//					delete properties[userPref.name];
					is_deleteProperty(properties, userPref.name);
				}else{
					properties[userPref.name] = value;
				}
			}
		}
	}
	
	return properties;
}

ISA_CommonModals.EditorForm.loadPreviewWidget = function(){
	var previewWidget = ISA_CommonModals.EditorForm.previewWidget;
	if(!previewWidget) return;
	IS_Event.unloadCache(previewWidget.id);
	IS_Event.unloadCache(previewWidget.closeId);
	IS_Event.unloadCache(previewWidget.id+"_sub");
	IS_Event.unloadCache(previewWidget.id+"_sub_close");
	previewWidget.loadContents();
	
	function onLoadComplete() {
		if(!previewWidget) return;
		IS_Event.unloadCache(previewWidget.id);
		IS_Event.unloadCache(previewWidget.closeId);
		IS_Event.unloadCache(previewWidget.id+"_sub");
		IS_Event.unloadCache(previewWidget.id+"_sub_close");
		if(!(previewWidget.elm_widget)) return;
		var aTags = previewWidget.elm_widget.getElementsByTagName( "a" );
		if(!aTags) return;
		for(var i=0; i < aTags.length; i++){
			aTags[i].target = "_blank";
		}
	}
	IS_EventDispatcher.addListener('loadComplete', previewWidget.id, onLoadComplete.bind(self), null, true);
}


ISA_CommonModals.EditorForm.resetPreview = function(){
	IS_Event.unloadCache("previewWidget");
	IS_Event.unloadCache("previewWidget_close");
	IS_Event.unloadCache("previewWidget_sub");
	IS_Event.unloadCache("previewWidget_sub_close");
	var previewDiv = $("widgetPreviewContainer");
	if(previewDiv){
		previewDiv.innerHTML = "";
	}
	var previewWidget = ISA_CommonModals.EditorForm.previewWidget;
	if(previewWidget){
		AjaxRequest.cancel(previewWidget.id);
		IS_Portal.widgetLists[IS_Portal.currentTabId][previewWidget.id] = null;
		previewWidget = null;
	}
}

ISA_CommonModals.EditorForm.preview = function(previewDiv, widgetObj, menuItem ){
	ISA_CommonModals.EditorForm.resetPreview();
	if(!widgetObj) return;
	var widgetBody;
	try{
		var subWidgetTitle = widgetObj.title;
		if(/MultiRssReader/.test(widgetObj.type) &&( !menuItem.properties ||!menuItem.properties.children ) ) {
			var parentId = menuItem.parentId;
			var parentMenu = ISA_SiteAggregationMenu.menuItemList[parentId]
			
			if( parentMenu ) {
				widgetObj.title = parentMenu.title;
				widgetObj.href = parentMenu.href;
			} else {
				widgetObj.title = "";
			}
		} else {
			widgetObj.title = widgetObj.title;
		}
		
		if(/FragmentMiniBrowser/.test(widgetObj.type) && widgetObj.property){
			widgetObj.property.cacheID = "";
		}
		
		ISA_CommonModals.EditorForm.previewWidget = new IS_Widget(false, widgetObj);
		IS_Portal.widgetLists[IS_Portal.currentTabId][ISA_CommonModals.EditorForm.previewWidget.id] = ISA_CommonModals.EditorForm.previewWidget;
		ISA_CommonModals.EditorForm.previewWidget.tabId = IS_Portal.currentTabId;
		if(/MultiRssReader/.test(widgetObj.type)){
			var subWidgetList;
			if( !menuItem.properties || !menuItem.properties.children ) {
				var subWidgetObj = eval("("+Object.toJSON(widgetObj)+")");
				subWidgetObj.id += "_sub";
				subWidgetObj.type = "RssReader";
				subWidgetObj.title = subWidgetTitle;
				var feedWidget = new IS_Widget(false, subWidgetObj);
				feedWidget.tabId = IS_Portal.currentTabId;
				feedWidget.parent = ISA_CommonModals.EditorForm.previewWidget;
				subWidgetList = [feedWidget];
			} else {
				subWidgetList = menuItem.properties.children.collect( function( childId ) {
					var child = IS_SiteAggregationMenu.menuItemList[childId];
					
					var feedWidget = new IS_Widget(false, {
						type: "RssReader",
						id: widgetObj.id+"_sub_"+child.id,
						title: child.title,
						href: child.href,
						property: Object.clone( child.properties )
					});
					feedWidget.tabId = IS_Portal.currentTabId;
					//ISA_CommonModals.EditorForm.previewWidget.addSubWidget(feedWidget);
					feedWidget.parent = ISA_CommonModals.EditorForm.previewWidget;
					
					return feedWidget;
				});
			}
			IS_Portal.getSubWidgetList = function() { return subWidgetList; }
		}
		
		ISA_CommonModals.EditorForm.previewWidget.build();
		widgetBody = ISA_CommonModals.EditorForm.previewWidget.elm_widget;
	}catch(e){
		ISA_CommonModals.EditorForm.previewWidget = null;
		widgetBody = document.createElement("div");
		widgetBody.className = "errorPreview";
		widgetBody.innerHTML = ISA_R.ams_notPreview;
		widgetBody.innerHTML += "<br/>"+ISA_R.ams_errorWhileWidget + e;
	}
	if(previewDiv.firstChild){
		previewDiv.replaceChild( widgetBody, previewDiv.firstChild);
	}else{
		previewDiv.appendChild( widgetBody );
	}
}

/**
 * Generate widget selecting page
 *
 * @disabled Disable form or not
 * @obj {id:ID of widget,type:Type of widget,properties:Properties of widget}
 */
ISA_CommonModals.EditorForm.makeWidgetEditFieldSet = function(disabled, _menuItem, options ){
	var menuItem = Object.extend({},_menuItem);
	if(!menuItem.properties)
		menuItem.properties = {};
	
	var widgetFieldSet = document.createElement("div");
	widgetFieldSet.className = "modalConfigSet";
	var widgetFieldSetLegend = document.createElement("p");
	widgetFieldSetLegend.className = "modalConfigSetHeader";
	var widgetFieldSetTitle = options.title || ISA_R.alb_widgetSettings;
	widgetFieldSetLegend.appendChild(document.createTextNode( widgetFieldSetTitle ));
	widgetFieldSet.appendChild(widgetFieldSetLegend);
	
	var contentSubDiv = document.createElement("div");
	var editorFormSubTable = document.createElement("table");
	editorFormSubTable.className = "modalConfigSetContent";
	contentSubDiv.appendChild(editorFormSubTable);
	var editorFormSubTbody = document.createElement("tbody");
	editorFormSubTable.appendChild(editorFormSubTbody);
	if(options.showIgnoreHeaderForm){
		var subTr = document.createElement("tr");
		var subTd = document.createElement("td");
		subTd.style.width = "30%";
		subTd.style.textAlign = "right";
		subTd.appendChild(document.createTextNode(ISA_R.alb_noHeader));
		subTr.appendChild(subTd);
		subTd = document.createElement("td");
		subTd.style.width = "70%";
		var checkbox =ISA_Admin.createBaseCheckBox("ignoreHeader", menuItem.ignoreHeader, false, document);
		checkbox.id = "ignoreHeaderCheckbox";
		subTd.appendChild(checkbox);
		subTr.appendChild(subTd);
		editorFormSubTbody.appendChild(subTr);
	}
	if(options.showNoBorderForm){
		var subTr = document.createElement("tr");
		var subTd = document.createElement("td");
		subTd.style.width = "30%";
		subTd.style.textAlign = "right";
		subTd.appendChild(document.createTextNode(ISA_R.alb_noBorder));
		subTr.appendChild(subTd);
		subTd = document.createElement("td");
		subTd.style.width = "70%";
		var checkbox =ISA_Admin.createBaseCheckBox("noBorder", menuItem.noBorder, false, document);
		checkbox.id = "noBorderCheckbox";
		subTd.appendChild(checkbox);
		subTr.appendChild(subTd);
		editorFormSubTbody.appendChild(subTr);
	}
	// Input items:type
	
	var isWidgetDeleted = false;
	
	var widgetType = replaceUndefinedDefaultValue(menuItem.type);
	
	// Title information setting button
	var setWidgetTitleInfoButton;
	
	makeTypeSelect();
	
	widgetFieldSet.appendChild(contentSubDiv);
	
	if(menuItem.type){ 
		buildPropertyField();
		makePreviewButton();
		if(!disabled)
		  makeSetDefaultButton();
	}
	
	function clearPropertyField(){
		IS_Event.unloadCache("_widgetEditForm");
		IS_Event.unloadCache("previewWidget");
		IS_Event.unloadCache("previewWidget_close");
		IS_Event.unloadCache("previewWidget_sub");
		IS_Event.unloadCache("previewWidget_sub_close");
		
		var trs = editorFormSubTbody.childNodes;
		for(var i = 0; i < trs.length; i++){
			if(trs[i].className == "propertyTr" || trs[i].className == "multiTr"){
				editorFormSubTbody.removeChild(trs[i]);
				i--;
			}
		}
		
		menuItem.title = ISA_R.alb_noTitle;
		menuItem.href = "";
		menuItem.properties = {};
		
	}
	function typeSelect(e, isChangeMenuSetting){
		
		menuItem.type = $F('widgetType');

		if(isChangeMenuSetting) {
			var conf = ISA_SiteAggregationMenu.widgetConfs[menuItem.type];
			if(conf){
				isWidgetDeleted = false;
				var titleForm = $("formTitle");
				if(!titleForm) titleForm = $("formPropertyValueTitle");
			}else{
				isWidgetDeleted = true;
			}
		}
		clearPropertyField();
		
		ISA_CommonModals.EditorForm.resetPreview();
		var previewButtonTr = $("previewButtonTr");
		if(menuItem.type){
			buildPropertyField();
			if(previewButtonTr){
				Element.show("adminPreviewButton");
				editorFormSubTbody.appendChild(previewButtonTr);
				editorFormSubTbody.appendChild($('makeSetDefaultButtonTr'));
			}else{
				makePreviewButton();
				makeSetDefaultButton();
			}
			if($("formHeaderOnly"))
			  $("formHeaderOnly").disabled = false;
		}else{

			if(previewButtonTr){
				Element.remove("previewButtonTr");
				Element.remove('makeSetDefaultButtonTr');
			}
			if($("formHeaderOnly"))
			  $("formHeaderOnly").disabled = true;
		}
		if(options.displayAlertFieldSet)
		  ISA_CommonModals.EditorForm.makeMenuAlertEditFieldSet.setForceDropOpton(menuItem.type);
	}
	
	function makeTypeSelect(){
		var subTr = document.createElement("tr");
		var subTd = document.createElement("td");
		subTd.style.width = "30%";
		subTd.style.textAlign = "right";
		subTd.appendChild(document.createTextNode(ISA_R.alb_widgetTypeColon));
		subTr.appendChild(subTd);
		subTd = document.createElement("td");
		subTd.style.width = "70%";
		var widgetSelectInput = document.createElement("select");
		widgetSelectInput.id = "widgetType";
		widgetSelectInput.name = "widgetType";
		widgetSelectInput.disabled = (options.disabledTypeEdit) ? true : disabled;

		var emptyOpt = document.createElement("option");
		emptyOpt.value = "";
		emptyOpt.appendChild(document.createTextNode(""));
		widgetSelectInput.appendChild(emptyOpt);

		if(!ISA_SiteAggregationMenu.widgetConfs[ menuItem.type ])
			isWidgetDeleted = true;
		
		for(i in ISA_SiteAggregationMenu.widgetConfs){
			var conf = ISA_SiteAggregationMenu.widgetConfs[i];
			if( ( conf instanceof Function ) )continue;
			if(options.omitTypeList.contains(conf.type)) continue;
			var optionEl = document.createElement("option");
			optionEl.id = menuItem.id + '_optType' + i;
			optionEl.value = (conf.type) ? conf.type : i;
			var typeName;
			if(conf.ModulePrefs){
				typeName = conf.ModulePrefs.directory_title || conf.ModulePrefs.title || conf.type;
			}else{
				typeName = (conf.title) ? conf.title : conf.type;
			}

			optionEl.appendChild( document.createTextNode( typeName ));
				
			if(conf.type == widgetType){
				optionEl.selected = true;
			}
			widgetSelectInput.appendChild( optionEl );
		}
		subTd.appendChild(widgetSelectInput);
		
		setWidgetTitleInfoButton = document.createElement("input");
		setWidgetTitleInfoButton.type = "button";
		setWidgetTitleInfoButton.value = ISA_R.alb_setToTitle;
		setWidgetTitleInfoButton.id = "setWidgetTitleInfoButton";
		IS_Event.observe(setWidgetTitleInfoButton, "click", function(widgetSelectInput){
			var titleForm = $("formTitle");
			if(!titleForm) titleForm = $("formPropertyValueTitle");
			
			var selectedType = $F('widgetType');
			var conf = ISA_SiteAggregationMenu.widgetConfs[selectedType];
			if (conf) {
				if (conf.ModulePrefs)
					titleForm.value = conf.ModulePrefs.directory_title || conf.ModulePrefs.title || conf.type;
				else
					titleForm.value = (conf.title) ? conf.title : conf.type;
				
				var hrefForm = $("formHref");
				if (!hrefForm) 	hrefForm = $("formPropertyValueHref");
				hrefForm.value = conf.ModulePrefs && conf.ModulePrefs.title_url ? conf.ModulePrefs.title_url : '';
			}
		}.bind(this, widgetSelectInput), false, "_adminMenu");
		subTd.appendChild(setWidgetTitleInfoButton);
		setWidgetTitleInfoButton.style.display = "none";
		setWidgetTitleInfoButton.disabled = disabled;
		
		subTr.appendChild(subTd);
		
		IS_Event.observe(widgetSelectInput, "change", typeSelect.bindAsEventListener(this, true), false, "_adminMenu");
		
		editorFormSubTbody.appendChild(subTr);
		
	}
	
	function makePreviewButton(){

		var subTr = document.createElement("tr");
		subTr.id = "previewButtonTr";

		var subTd = document.createElement("td");
		subTd.style.verticalAlign = "top";
		subTd.style.textAlign = "right";
		var previewButton = document.createElement('input');
		previewButton.id = "adminPreviewButton";
		previewButton.type = "button";
		previewButton.value = ISA_R.alb_preview;
		subTd.appendChild(previewButton);
		
		IS_Event.observe(previewButton, "click", buildPreview.bind(self), false, "_adminMenuEdit");
		if(isWidgetDeleted || options.disablePreview ){
			Element.hide(previewButton);
		}
		subTr.appendChild(subTd);

		var subTd = document.createElement("td");
		subTd.id = "widgetPreviewContainer";
		if(isWidgetDeleted){
			var errorMsg = document.createElement("span");
			errorMsg.style.color = "#f00";
			errorMsg.innerHTML = ISA_R.ams_deleteSetWidget;
			subTd.appendChild( errorMsg );
		}
		subTr.appendChild(subTd);

		editorFormSubTbody.appendChild(subTr);

	}
	
	function makeSetDefaultButton(){
		
		var subTr = document.createElement("tr");
		subTr.id = 'makeSetDefaultButtonTr';
		var subTd = document.createElement("td");
		subTd.colSpan = 2;
		subTd.style.textAlign = "center";
		if(!isWidgetDeleted && !options.disablePreview ){
			var applyDefaultSpan = document.createElement('span');
			var applyDefaultBtn = document.createElement('input');
			applyDefaultBtn.type = "button";
			applyDefaultBtn.value = ISA_R.alb_backToDefault;
			applyDefaultSpan.appendChild(applyDefaultBtn);
			subTd.appendChild(applyDefaultSpan);
			IS_Event.observe(applyDefaultBtn, "click", typeSelect, false, "_adminMenuEdit");
		}
		subTr.appendChild(subTd);
		
		editorFormSubTbody.appendChild(subTr);
	}
	
	function buildPreview(){
		var previewDiv = $('widgetPreviewContainer');
		var widgetType = ISA_CommonModals.EditorForm.getSelectType();
		
		if( widgetType && 0 <= widgetType.length ){
			if( !ISA_CommonModals.EditorForm.checkEditorForm() )
				return;
			
			var widgetObj = new Object();
			widgetObj.id = "adminPreviewWidget";
			widgetObj.type = widgetType;
			var widgetProps = ISA_CommonModals.EditorForm.getProperty(menuItem);
			widgetObj.property = Object.extend({}, widgetProps);
			var titleForm = $("formTitle");
			if(!titleForm) titleForm = $("formPropertyValueTitle");
			if(titleForm)
				widgetObj.title = titleForm.value;
			
			var hrefForm = $("formHref");
			if(!hrefForm){
				hrefForm = $("formPropertyValueHref");
			}
			if(hrefForm)
			  widgetObj.href = hrefForm.value;
			
			ISA_CommonModals.EditorForm.preview(previewDiv, widgetObj, menuItem );
			ISA_CommonModals.EditorForm.loadPreviewWidget();
			
		}else{
			previewDiv.innerHTML = 	ISA_R.ams_notSet;
		}
	}
	
	function disable(element){
		var form = element.getElementsByTagName('input');
		for(var i = 0; i < form.length; i++)
		  form[i].disabled = true;
		
		var form = element.getElementsByTagName('select');
		for(var i = 0; i < form.length; i++)
		  form[0].disabled = true;
		
		var form = element.getElementsByTagName('textarea');
		for(var i = 0; i < form.length; i++)
		  form[0].disabled = true;
		
	}
	
	function buildPropertyField(){
		var hasURLDataType = false;
		var widgetType = menuItem.type;
		if("MultiRssReader" == widgetType) widgetType = "RssReader";
		var widgetConf = ISA_SiteAggregationMenu.widgetConfs[widgetType];
		//ISA_WidgetConf.EditWidgetConf.makePrefForm(editorFormSubTbody,widgetConf, menuItem,disabled);
		if(!widgetConf)return;
		var userPrefList = widgetConf.UserPref;
		for( i in userPrefList ){
			if( !( userPrefList[i] instanceof Function ) ){
				var prefConf = userPrefList[i];
				prefConf.isDefault = true;
				if(!prefConf.name) continue;
				if(prefConf.datatype == 'hidden' && !prefConf.admin_datatype )
				  continue;

				if(options.disableMiniBrowserHeight &&
				   (widgetType == 'FragmentMiniBrowser' || widgetType == 'MiniBrowser') &&
				   'height'==prefConf.name)
				  continue;
				
				var displayName = (prefConf.display_name) ? prefConf.display_name : prefConf.name;
				
				var subTr = document.createElement("tr");
				subTr.className = "propertyTr";
				var subTd = document.createElement("td");
				subTd.style.textAlign = "right";
				subTd.appendChild(document.createTextNode(displayName + "："));
				var nameField = document.createElement("input");
				nameField.type = "hidden";
				nameField.value = prefConf.name;
				nameField.className = "propertyName";
				subTd.appendChild(nameField);
				subTr.appendChild(subTd);
				var subTd = document.createElement("td");
				var inputField = ISA_WidgetConf.makeForm( 'UP', prefConf, widgetType, menuItem.properties[prefConf.name], true);
				if(disabled) disable(inputField);
				subTd.appendChild(inputField);
				subTr.appendChild(subTd);
				editorFormSubTbody.appendChild(subTr);
				
				var datatype = (prefConf.admin_datatype) ? prefConf.admin_datatype : prefConf.datatype;
				if(!hasURLDataType) hasURLDataType = (datatype == "url");
			}
		}

		// [Set for title information] button is not displayed if datatype=url
		if (setWidgetTitleInfoButton) {
			if (hasURLDataType) 
				setWidgetTitleInfoButton.style.display = "none";
			else 
				setWidgetTitleInfoButton.style.display = "";
		}
		
		var singleton;
		if(/^g_upload__/.test( menuItem.type ) || /^Gadget$/i.test( menuItem.type )) {
			singleton = widgetConf.ModulePrefs && /true/i.test( widgetConf.ModulePrefs.singleton )
		} else {
			//singleton = widgetConf.singleton;
			singleton = (menuItem.type == 'RssReader' || menuItem.type == 'MultiRssReader');
		}
		
//		var isDefaultPanelTabActive = ($('tab_defaultPanel') && $('tab_defaultPanel').className == 'tab active');
		var isDefaultPanelTabActive = (window["editRoleScreen"] && window["editRoleScreen"] == true);
		if( !singleton && !(isDefaultPanelTabActive)) {
			var row = document.createElement("tr");
			row.className = "multiTr";
			editorFormSubTbody.appendChild( row );
			var lb = document.createElement("td");
			lb.style.textAlign = "right";
			lb.innerHTML = ISA_R.alb_enalbeMultiDrop+":";
			row.appendChild( lb );
			
			var field = document.createElement("td");
			row.appendChild( field );
			
			var checkbox = document.createElement("input");
			checkbox.type = "checkbox";
			checkbox.id = "formMulti";
			checkbox.defaultChecked = /true/i.test( menuItem.multi );
			checkbox.disabled = disabled;
			field.appendChild( checkbox );
		}
		
	}
	
	function replaceUndefinedDefaultValue(val) {
		var value = val;
		return ISA_Admin.replaceUndefinedValue(value);
	}
	
	return widgetFieldSet;
}


ISA_CommonModals.EditorForm.makeMenuAlertEditFieldSet = function(disabled, menuItem){
	
	var menuItemFieldSet = document.createElement("div");
	menuItemFieldSet.className = "modalConfigSet";
	var menuItemFieldSetLegend = document.createElement("p");
	menuItemFieldSetLegend.className = "modalConfigSetHeader";
	menuItemFieldSetLegend.appendChild(document.createTextNode(ISA_R.alb_alertSettings));
	menuItemFieldSet.appendChild(menuItemFieldSetLegend);
	
	menuItemFieldSet.appendChild(
		$.P({className:"modalConfigSetContent"}, ISA_R.alb_informNewMenu )
		);
	
	/* Create main */
	var editorFormTable = document.createElement("table");
	editorFormTable.style.width = "100%";
	var editorFormTbody = document.createElement("tbody");
	editorFormTable.appendChild(editorFormTbody);
	
	// Input items:title
	editorFormTbody.appendChild(makeForcedDropCheckbox());
	
	function makeForcedDropCheckbox(){
		var subTr = document.createElement("tr");
		subTr.id='forcedDropWidgetTr';
		var subTd = document.createElement("td");
		subTd.style.width = "30%";
		subTd.style.textAlign = "right";
		subTd.appendChild(document.createTextNode(ISA_R.alb_alertSettingsColon));
		subTr.appendChild(subTd);
		subTd = document.createElement("td");
		subTd.style.width = "70%";
		var alertSettingSelect = document.createElement('select');
		alertSettingSelect.id = 'allertSetting';
		alertSettingSelect.disabled = disabled;
		var alertSettingOption = document.createElement('option');
		alertSettingOption.value = '0';
		alertSettingOption.selected = menuItem.alert == '0';
		alertSettingOption.appendChild(document.createTextNode(ISA_R.alb_noAlert));
		alertSettingSelect.appendChild(alertSettingOption);
		alertSettingOption = document.createElement('option');
		alertSettingOption.value = '1';
		alertSettingOption.selected = (typeof menuItem.alert =='undefined') ? true : menuItem.alert == '1';
		alertSettingOption.appendChild(document.createTextNode(ISA_R.alb_displayMessage));
		alertSettingSelect.appendChild(alertSettingOption);
		if(menuItem.type){
			alertSettingOption = document.createElement('option');
			alertSettingOption.value = '2';
			alertSettingOption.selected = menuItem.alert == '2';
			alertSettingOption.appendChild(document.createTextNode(ISA_R.alb_setWidgetCompulsory));
			alertSettingSelect.appendChild(alertSettingOption);
		}
		subTd.appendChild(alertSettingSelect);
		subTr.appendChild(subTd);
		return subTr;
	}

	menuItemFieldSet.appendChild(editorFormTable);
	return menuItemFieldSet;
}
ISA_CommonModals.EditorForm.makeMenuAlertEditFieldSet.setForceDropOpton = function(menuType){
	var alertSettingSelect = $('allertSetting');
	if(alertSettingSelect.length == 3){
		if(!menuType)
			Element.remove( 'forcedDropOption' );
	}else{
		alertSettingOption = document.createElement('option');
		alertSettingOption.id = 'forcedDropOption';
		alertSettingOption.value = '2';
		alertSettingOption.appendChild(document.createTextNode(ISA_R.alb_setWidgetCompulsory));
		alertSettingSelect.appendChild(alertSettingOption);
	}
}

ISA_CommonModals.EditorForm.makeMenuUpdateSettingFieldSet = function(disabled, menuItem){

	var menuItemFieldSet = document.createElement("div");
	menuItemFieldSet.className = "modalConfigSet";
	var menuItemFieldSetLegend = document.createElement("p");
	menuItemFieldSetLegend.className = "modalConfigSetHeader";
	menuItemFieldSetLegend.appendChild(document.createTextNode(ISA_R.alb_selectUpdateProperties));
	menuItemFieldSet.appendChild(menuItemFieldSetLegend);

	var forceUpdatePropertyDiv = document.createElement('div');
	forceUpdatePropertyDiv.className = "modalConfigSetContent";
//	forceUpdatePropertyDiv.style.paddingTop = '3px';
	forceUpdatePropertyDiv.appendChild(document.createTextNode(ISA_R.alb_forcedUpdatePrefsMessage));

	var properties = [];
	var widgetType = menuItem.type;//ISA_CommonModals.EditorForm.getSelectType();
	if(widgetType){
		if("MultiRssReader" == widgetType) widgetType = "RssReader";
		var conf = ISA_SiteAggregationMenu.widgetConfs[widgetType];
		if(conf){
			var userPrefs = conf.UserPref;
			for( i in userPrefs){
				if( !( userPrefs[i] instanceof Function ) ){
					var userPref = userPrefs[i];
					if(userPref.name && ( userPref.datatype != 'hidden' || userPref.admin_datatype) )
					  properties.push(userPref);
				}
			}
		}
	}

	var menuLinklegend = document.createElement('div');
	menuLinklegend.style.fontWeight = 'bold';
	menuLinklegend.style.margin = '2px 0';
	menuLinklegend.appendChild(document.createTextNode(ISA_R.alb_settingMenuLink));
	forceUpdatePropertyDiv.appendChild(menuLinklegend);

	var menuLinkFieldset = document.createElement('div');
	menuLinkFieldset.style.paddingLeft = '7px';
	var titleSpan = document.createElement('span');
	titleSpan.style.paddingRight = '5px';
	var titleCheckBox = document.createElement('input');
	titleCheckBox.type='checkbox';
	titleCheckBox.id = 'FUP_TITLE';
	titleSpan.appendChild(titleCheckBox);
	titleSpan.appendChild(document.createTextNode(ISA_R.alb_title));
	menuLinkFieldset.appendChild(titleSpan);

	var linkSpan = document.createElement('span');
	var linkCheckBox = document.createElement('input');
	linkCheckBox.type='checkbox';
	linkCheckBox.id = 'FUP_HREF';
	linkSpan.appendChild(linkCheckBox);
	linkSpan.appendChild(document.createTextNode(ISA_R.alb_titleLink));
	menuLinkFieldset.appendChild(linkSpan);
	forceUpdatePropertyDiv.appendChild(menuLinkFieldset);

	var userPrefslegend = document.createElement('div');
	userPrefslegend.style.fontWeight = 'bold';
	userPrefslegend.style.margin = '2px 0';
	userPrefslegend.appendChild(document.createTextNode(ISA_R.alb_widgetSettings));
	forceUpdatePropertyDiv.appendChild(userPrefslegend);

	var userPrefsFieldset = document.createElement('div');
	userPrefsFieldset.style.paddingLeft = '7px';
	$A(properties).each(function(pref){
		var propSpan = document.createElement('div');
		propSpan.style.whiteSpace = 'nowrap';
		propSpan.style.styleFloat = 'left';
		propSpan.style.cssFloat = 'left';
		propSpan.style.paddingRight = '5px';
		var propCheckBox = document.createElement('input');
		propCheckBox.type='checkbox';
		propCheckBox.id = 'FUP_' + pref.name;
		propSpan.appendChild(propCheckBox);
		propSpan.appendChild(document.createTextNode( pref.display_name || pref.name ));
		userPrefsFieldset.appendChild(propSpan);
	});
	var spaceDiv = document.createElement('div');
	spaceDiv.style.clear = 'both';
	userPrefsFieldset.appendChild(spaceDiv);
	forceUpdatePropertyDiv.appendChild(userPrefsFieldset);

	menuItemFieldSet.appendChild(forceUpdatePropertyDiv);
	return menuItemFieldSet;
}

/**
 * Generate menu item edit page 
 *
 * @disabled Disable form or not
 * @obj {id:ID of widget,type:Type of widget,properties:Properties of widget}
 */
ISA_CommonModals.EditorForm.makeMenuItemEditFieldSet = function(disabled, menuItem, options){
	
	var menuItemFieldSet = document.createElement("div");
	menuItemFieldSet.className = "modalConfigSet";
	var menuItemFieldSetLegend = document.createElement("p");
	menuItemFieldSetLegend.className = "modalConfigSetHeader";
	menuItemFieldSetLegend.appendChild(document.createTextNode(options.menuFieldSetLegend));
	menuItemFieldSet.appendChild(menuItemFieldSetLegend);
	
	var showEditorsForm = document.createElement("form");
	showEditorsForm.id = "showEditorsForm";
	
	/* Create main */
	var editorFormTable = document.createElement("table");
	editorFormTable.className = "modalConfigSetContent";
//	editorFormTable.style.width = "100%";
	var editorFormTbody = document.createElement("tbody");
	editorFormTable.appendChild(editorFormTbody);
	
	// Input item:title
	editorFormTbody.appendChild(makeTitleText());
	
	// Input item:href
	editorFormTbody.appendChild(makeHrefText());
	
	// Input item:display
	if(!options.disableDisplayRadio)
	  editorFormTbody.appendChild(makeDisplayRadio());
	
	// Input item:headerOnly
	if(menuItem.parentId)
		 editorFormTbody.appendChild(makeHeaderOnlyCheckBox());
	
	// External service URL:serviceURL
	if(!menuItem.parentId && (options.showServiceURL || menuItem.serviceURL)) {
		editorFormTbody.appendChild(makeServiceURLText());
		editorFormTbody.appendChild(makeServiceAuthTypeSelect());
	}
	
	function makeTitleText(){
		var elementTr = document.createElement("tr");
		var elementTd = document.createElement("td");
		elementTd.style.width = "30%";
		elementTd.style.textAlign = "right";
		elementTd.appendChild(document.createTextNode(ISA_R.alb_titleColon));
		elementTr.appendChild(elementTd);
		
		elementTd = document.createElement("td");
		elementTd.style.width = "70%";
		var elementInput = document.createElement("input");
		elementInput.type = "text";
		elementInput.id = "formTitle";
		elementInput.name = "FORM_TITLE";
		elementInput.size = "50";
		elementInput.maxLength = "80";
		elementInput.value = replaceUndefinedDefaultValue(menuItem.directoryTitle || menuItem.title);
		elementInput.disabled = disabled;
		elementTd.appendChild(elementInput);
		
		if( menuItem.directoryTitle ) {
			var input = document.createElement("input");
			input.id = "formOriginalTitle";
			input.value = menuItem.title;
			input.style.display = "none";
			elementTd.appendChild( input );
		}
		
		elementTr.appendChild(elementTd);
		return elementTr;
	}
	
	function makeHrefText(){
		var elementTr = document.createElement("tr");
		var elementTd = document.createElement("td");
		elementTd.style.width = "30%";
		elementTd.style.textAlign = "right";
		elementTd.appendChild(document.createTextNode(ISA_R.alb_titleLinkColon));
		elementTr.appendChild(elementTd);
		
		elementTd = document.createElement("td");
		elementTd.style.width = "70%";
		var elementInput = document.createElement("input");
		elementInput.type = "text";
		elementInput.id = "formHref";
		elementInput.name = "FORM_HREF";
		elementInput.size = "80";
		elementInput.maxLength = "1024";
		elementInput.value = replaceUndefinedDefaultValue(menuItem.href);
		elementInput.disabled = disabled;
		elementTd.appendChild(elementInput);
		elementTr.appendChild(elementTd);
		IS_Event.observe(elementInput, 'change', function(){
			var fupHref = $('FUP_HREF');
			if(fupHref)
			  fupHref.checked = true;
		}, false, '_adminMenuEdit');
		return elementTr;
	}
	
	function makeDisplayRadio(){
		var elementTr = document.createElement("tr");
		var elementTd = document.createElement("td");
		elementTd.style.width = "30%";
		elementTd.style.textAlign = "right";
		elementTd.appendChild(document.createTextNode(ISA_R.alb_displayLinkSettings));
		elementTr.appendChild(elementTd);
		
		elementTd = document.createElement("td");
		elementTd.style.width = "70%";
		
		var elementInput = document.createElement("select");
		elementInput.id = "formDisplayInline";
		elementInput.name = "FORM_DISPLAY";
		elementInput.disabled = disabled;
		
		var itemDisplayList = [ISA_R.alb_targetAutoSelect, ISA_R.alb_portalframe, ISA_R.alb_newWindow];
		var itemValueList = ["", "inline", "newwindow"];
		
		var displayValue = replaceUndefinedDefaultValue(menuItem.display);
		for(var i = 0; i < itemDisplayList.length; i++){
			var opt = document.createElement("option");
			opt.value = itemValueList[i];
			opt.innerHTML = itemDisplayList[i];
			if(displayValue && itemValueList[i] == displayValue){
				opt.selected = true;
			}
			elementInput.appendChild( opt );
		}
		elementTd.appendChild(elementInput);
		elementTr.appendChild(elementTd);
		return elementTr;
	}
	
	function makeServiceURLText(){
		var elementTr = document.createElement("tr");
		var elementTd = document.createElement("td");
		elementTd.style.width = "30%";
		elementTd.style.textAlign = "right";
		elementTd.appendChild(document.createTextNode(ISA_R.alb_externalServiceURL));
		elementTr.appendChild(elementTd);
		
		elementTd = document.createElement("td");
		elementTd.style.width = "70%";
		var elementInput = document.createElement("input");
		elementInput.type = "text";
		elementInput.id = "formServiceURL";
		elementInput.name = "FORM_SERVICE_URL";
		elementInput.size = "100";
		elementInput.value = replaceUndefinedDefaultValue(menuItem.serviceURL);
		elementInput.disabled = disabled;
		elementTd.appendChild(elementInput);
		elementTr.appendChild(elementTd);
		return elementTr;
	}
	
	function makeServiceAuthTypeSelect(){
		var elementTr = document.createElement("tr");
		var elementTd = document.createElement("td");
		elementTd.style.width = "30%";
		elementTd.style.textAlign = "right";
		elementTd.appendChild(document.createTextNode(ISA_R.alb_externalServiceAuthType));
		elementTr.appendChild(elementTd);
		
		elementTd = document.createElement("td");
		elementTd.style.width = "70%";
		var elementSelect = document.createElement("select");
		elementSelect.id = "formServiceAuthType";
		elementSelect.name = "FORM_SERVICE_AUTH_TYPE";
		elementSelect.disabled = disabled;
		
		var authType = menuItem.serviceAuthType ? menuItem.serviceAuthType.split(' ')[0] : '';
		
		[["",ISA_R.alb_none],["postPortalCredential","postPortalCredential"],["sendPortalCredentialHeader","sendPortalCredentialHeader"]].each(function(optionConf){
			var elementOption = document.createElement("option");
			elementOption.value = optionConf[0];
			elementOption.appendChild(document.createTextNode(optionConf[1]));
			if(optionConf[0] == authType){
				elementOption.selected = true;
			}
			elementSelect.appendChild(elementOption);
		});

		var authUidParamName = menuItem.serviceAuthType ? menuItem.serviceAuthType.split(' ')[1] : '';
		var formServiceAuthParamNameSpan = $.SPAN({id:'formServiceAuthParamNameSpan', style:'display:none'},
			   ISA_R.alb_paramNameOfUserId,
												  $.INPUT({id: 'formServiceAuthParamName', value:(authUidParamName ? decodeURIComponent(authUidParamName) : '')})
				 )
		if(authType){
			Element.show(formServiceAuthParamNameSpan);
		}

		IS_Event.observe(elementSelect, 'change', function(){
			var inputSpan = $('formServiceAuthParamNameSpan');
			if(this.value){
				Element.show(inputSpan);
			}else{
				Element.hide(inputSpan);
			}
		}.bind(elementSelect), false, "_widgetEditForm");
		
		elementTd.appendChild(elementSelect);
		elementTd.appendChild(formServiceAuthParamNameSpan);
		elementTr.appendChild(elementTd);
		return elementTr;
	}
	
	function replaceUndefinedDefaultValue(val) {
		var value = val;
		return ISA_Admin.replaceUndefinedValue(value);
	}
	
	function makeHeaderOnlyCheckBox(){
		var elementTr = document.createElement("tr");
		var elementTd = document.createElement("td");
		elementTd.style.width = "30%";
		elementTd.style.textAlign = "right";
		elementTd.appendChild(document.createTextNode(ISA_R.alb_noLinkMenuColon));
		elementTr.appendChild(elementTd);
		
		elementTd = document.createElement("td");
		elementTd.style.width = "70%";
		var elementInput = document.createElement("input");
		elementInput.type = "checkbox";
		elementInput.id = "formHeaderOnly";
		elementInput.name = "FORM_HEADER_ONLY";
		elementInput.size = "100";
		elementInput.disabled = disabled;
		elementInput.defaultChecked = (menuItem.linkDisabled) ? true : false;
		elementTd.appendChild(elementInput);
		elementTr.appendChild(elementTd);
		return elementTr;
	}
	
	menuItemFieldSet.appendChild(editorFormTable);
	return menuItemFieldSet;
}

/**
 * Generate menu tree administrator setting page
 * 
 * @disabled Disable form or not
 * @obj {id:ID of widget,type:Type of widget,properties:Properties of widget}
 */

ISA_CommonModals.EditorForm.makeMenuTreeAdminsFieldSet = function(disabled, menuItem){
	menuTreeAdmins = [];
	
	var fieldSet = document.createElement("div");
	fieldSet.className = "modalConfigSet";
	var fieldSetLegend = document.createElement("p");
	fieldSetLegend.className = "modalConfigSetHeader";
	fieldSetLegend.appendChild(document.createTextNode(ISA_R.alb_adminSettings));
	fieldSet.appendChild(fieldSetLegend);
	
	var content = document.createElement("div");
	content.className = "modalConfigSetContent";
	var formField = document.createElement("div");
	var valueField = document.createElement("div");
	valueField.style.padding = "10px";
	
	var uidLabel = document.createElement("label");
	uidLabel.appendChild(document.createTextNode(IS_R.lb_userID));
	
	var uidSelect = document.createElement("select");
	ISA_SiteAggregationMenu.menuTreeAdminUsers.each(function(uid){
		var opt = document.createElement("option");
		opt.value = uid;
		opt.appendChild(document.createTextNode(uid));
		uidSelect.appendChild(opt);
	}.bind(this));
	
	uidSelect.disabled = disabled;
	
	var addUid = document.createElement("input");
	addUid.type = "button";
	addUid.value = IS_R.lb_add;
	addUid.disabled= disabled;
	
	var dummy = document.createElement("div");
	dummy.innerHTML = '<table class="authorizationTable"><tbody></tbody></table>';
	var valueTable = dummy.firstChild;
	var valueTbody = valueTable.firstChild;
	
	var headerTr = document.createElement("tr");
	
	var uidHeader = document.createElement("th");
	uidHeader.appendChild(document.createTextNode(IS_R.lb_userID));
	headerTr.appendChild(uidHeader);
	
	var deleteHeader = document.createElement("th");
	deleteHeader.appendChild(document.createTextNode(IS_R.lb_delete));
	headerTr.appendChild(deleteHeader);
	
	valueTbody.appendChild(headerTr);
	
	IS_Event.observe(addUid, 'click', addAdmin.bind(this, valueTbody, uidSelect, menuTreeAdmins));
	
	formField.appendChild(uidLabel);
	formField.appendChild(uidSelect);
	formField.appendChild(addUid);
	valueField.appendChild(valueTable);
	
	content.appendChild(formField);
	content.appendChild(valueField);
	fieldSet.appendChild(content);
	
	if(menuItem && menuItem.menuTreeAdmins){
		menuItem.menuTreeAdmins.each(function(adminUid){
			_addAdmin(valueTbody, adminUid, menuTreeAdmins, disabled);
		});
	}
	
	return fieldSet;
	
	function addAdmin(valueTbody, uidSelect, menuTreeAdmins){
		return _addAdmin(valueTbody, uidSelect.value, menuTreeAdmins);
	}
	
	function _addAdmin(valueTbody, adminUid, menuTreeAdmins, disabled){
		var tr = document.createElement("tr");
		
		var uidTd = document.createElement("td");
		uidTd.appendChild(document.createTextNode(adminUid));
		
		var deleteTd = document.createElement("td");
		var trashImg = document.createElement('img');
		trashImg.src = imageURL + 'trash.gif';
		trashImg.style.cursor = "pointer";
		
		if (!disabled) {
			IS_Event.observe(trashImg, 'click', function(tr, adminUid, menuTreeAdmins){
				Element.remove(tr);
				menuTreeAdmins.remove(adminUid);
			}.bind(this, tr, adminUid, menuTreeAdmins));
		}
		
		deleteTd.appendChild(trashImg);
		
		tr.appendChild(uidTd);
		tr.appendChild(deleteTd);
		
		valueTbody.appendChild(tr);
		menuTreeAdmins.push(adminUid);
	}
}

/**
 * Generate editting restriction of menu item acess page
 *
 * @disabled Disable form or not
 * @obj {id:ID of widget,type:Type of widget,properties:Properties of widget}
 */

ISA_CommonModals.EditorForm.makeMenuItemACLEditFieldSet = function(disabled, menuItem){
	authorizations = [];
	/* ACL settings */
//	var publicFieldSet = document.createElement("fieldset");
//	var publicFieldSetLegend = document.createElement("legend");
//	publicFieldSetLegend.appendChild(document.createTextNode(ISA_R.alb_publicSettings));
//	publicFieldSet.appendChild(publicFieldSetLegend);
	
	var publicFieldSet = document.createElement("div");
	publicFieldSet.className = "modalConfigSet";
	var publicFieldSetLegend = document.createElement("p");
	publicFieldSetLegend.className = "modalConfigSetHeader";
	publicFieldSetLegend.appendChild(document.createTextNode(ISA_R.alb_publicSettings));
	publicFieldSet.appendChild(publicFieldSetLegend);
	
	// Public:public
	publicFieldSet.appendChild(makePublicCheckBox());
	
	function makePublicCheckBox(){
		var publicDiv = document.createElement("div");
		publicDiv.className = "modalConfigSetContent";
		publicDiv.appendChild(document.createTextNode(ISA_R.alb_pulic));
		
		var elementInput = document.createElement("input");
		elementInput.type = "checkbox";
		elementInput.id = "formIsPublic";
		elementInput.name = "FORM_IS_PUBLIC";
		elementInput.disabled = disabled;
		elementInput.defaultChecked = (menuItem.auths) ? false : true;
		
		publicDiv.appendChild(elementInput);
		
		var aclEditorFormDiv = document.createElement('div');
		aclEditorFormDiv.style.padding = '10px';
		if(!menuItem.auths){
			aclEditorFormDiv.style.display = 'none';
		}
		
		var principalSelect = document.createElement('select');
		principalSelect.id = 'principalSelect';
		principalSelect.disabled = disabled;
		
		var principals = ISA_Principals.get();
		for(var i = 0; i < principals.length; i++){
			var principalOption = document.createElement('option');
			principalOption.appendChild(document.createTextNode(principals[i].displayName));
			principalOption.value = principals[i].type;
			principalSelect.appendChild(principalOption);
		}
		aclEditorFormDiv.appendChild(principalSelect);
		
		aclEditorFormDiv.appendChild(document.createTextNode(ISA_R.alb_regularExpression));
		
		var regxInput = document.createElement('input');
		regxInput.id = 'authRegxInput';
		regxInput.disabled = disabled;
		aclEditorFormDiv.appendChild(regxInput);
		
		function addAuthorization(){
			var principalType = $('principalSelect').value;
			var principalLabel = $("principalSelect").options[$("principalSelect").selectedIndex].text;
			var regx = $('authRegxInput').value;
			for(var i = 0; i < authorizations.length; i++){
				if( authorizations[i].type == principalType && authorizations[i].regx == regx ){
					alert(ISA_R.ams_setAlreadyExist);
					return;
				}
			}
			var authTbody = $('authorizationList');
			var authTr = document.createElement('tr');
			
			var principalTd = document.createElement('td');
			principalTd.appendChild(document.createTextNode(principalLabel));
			authTr.appendChild(principalTd);
			
			var regxTd = document.createElement('td');
			regxTd.appendChild(document.createTextNode(regx));
			authTr.appendChild(regxTd);
			
			var trashTd = document.createElement('td');
			var trashImg = document.createElement('img');
			trashImg.src = imageURL + 'trash.gif';
			trashImg.style.cursor = "pointer";
			trashTd.appendChild(trashImg);
			authTr.appendChild(trashTd);
			
			authTbody.appendChild(authTr);
			var authObj = {type:principalType, regx:regx, actions:['read']}
			
			if (!disabled)
				IS_Event.observe(trashImg, 'click', makeDelFunc(authObj, authTr));
			authorizations.push(authObj);
			
			jQuery("#authorizationTable").trigger("updateTable");
		}
		function makeDelFunc(authObj, delAuthTr){
			return function(e){
				var tempAuths = [];
				for(var i = 0; i < authorizations.length; i++){
					if( ! (authorizations[i].type == authObj.type && authorizations[i].regx == authObj.regx) ){
						tempAuths.push(authorizations[i]);
					}
				}
				authorizations = tempAuths;
				delAuthTr.parentNode.removeChild(delAuthTr);
				$jq("#authorizationTable").trigger("updateTable");
			}
			
		}
		var addButton = document.createElement('input');
		addButton.type = 'button';
		addButton.value =ISA_R.alb_add;
		addButton.disabled = disabled;
		IS_Event.observe(addButton, 'click', addAuthorization, false);
		aclEditorFormDiv.appendChild(addButton);
		
		var authTable = document.createElement('table');
		authTable.id = 'authorizationTable';
		authTable.className = "authorizationTable";
	    authThead = document.createElement('thead');
		authTable.appendChild(authThead);
		authTbody = document.createElement('tbody');
		authTbody.id = 'authorizationList';
		authTable.appendChild(authTbody);
		var authHeadTr = document.createElement('tr');
		var principalTh = document.createElement('th');
		principalTh.appendChild(document.createTextNode(ISA_R.alb_subject));
		authHeadTr.appendChild(principalTh);
		var regxTh = document.createElement('th');
		regxTh.className = "nosort regexpheader";
		regxTh.appendChild(document.createTextNode(ISA_R.alb_regularExpression));
		authHeadTr.appendChild(regxTh);
		var trushTd = document.createElement('th');
		trushTd.appendChild(document.createTextNode(ISA_R.alb_delete));
		authHeadTr.appendChild(trushTd);
		authThead.appendChild(authHeadTr);
		if(menuItem && menuItem.auths){
			for(var i = 0; i < menuItem.auths.length; i++){
				var authTr = document.createElement('tr');
				var principalTd = document.createElement('td');
                var principalLabel=menuItem.auths[i].type;
              
				for ( var j = 0; j < principals.length; j++) {
					if (menuItem.auths[i].type == principals[j].type) {
						principalLabel=principals[j].displayName;
						break;
					} 
				}
				
				principalTd.appendChild(document
								.createTextNode(principalLabel));
				
				authTr.appendChild(principalTd);
				var regxTd = document.createElement('td');
				regxTd.appendChild(document.createTextNode(menuItem.auths[i].regx));
				authTr.appendChild(regxTd);
				var trashTd = document.createElement('td');
				var trashImg = document.createElement('img');
				trashImg.src = imageURL + 'trash.gif';
				trashImg.style.cursor = "pointer";
				trashTd.appendChild(trashImg);
				authTr.appendChild(trashTd);
				authTbody.appendChild(authTr);
				var authObj = {type:menuItem.auths[i].type, regx:menuItem.auths[i].regx, actions:['read']};
				
				if (!disabled)
					IS_Event.observe(trashImg, 'click', makeDelFunc(authObj, authTr));
				authorizations.push(authObj);
				
			}
		}
		if($jq(authTable).tablesorter){
			$jq(authTable).tablesorter({
				sortList : [ [ 0, 0 ] ],
				headers : {
					0 : {
						sorter : "text"
					},
					1 : {
						sorter : "text"
					},
					2 : {
						sorter : false
					}
				}
			});
		}
		
		aclEditorFormDiv.appendChild(authTable);
		publicDiv.appendChild(aclEditorFormDiv);
		
		function displayAclForm(){
			if(elementInput.checked){
				aclEditorFormDiv.style.display = 'none';
			}else{
				aclEditorFormDiv.style.display = 'block';
			}
		}
		
		IS_Event.observe(elementInput, 'click', displayAclForm);
		
		return publicDiv;
	}
	return  publicFieldSet;
}

/**
 * Initial display settings for Dynamic panel *
 * @disabled Disable form or not
 * @obj {id:ID of widget,type:Type of widget,properties:Properties of widget}
 */

ISA_CommonModals.EditorForm.makeWidgetInstListFieldSet = function(disabled, widgetInst, opt){
	var selectDynamicPanelWidget = document.createElement('div');
	
//	var menuListFieldSet = document.createElement("fieldset");
//	var menuListFieldSetLegend = document.createElement("legend");
//	menuListFieldSetLegend.appendChild(document.createTextNode(ISA_R.alb_menuItemlist));
//	menuListFieldSet.appendChild(menuListFieldSetLegend);
	
	var menuListFieldSet = document.createElement("div");
	menuListFieldSet.className = "modalConfigSet";
	var menuListFieldSetLegend = document.createElement("p");
	menuListFieldSetLegend.className = "modalConfigSetHeader";
	menuListFieldSetLegend.appendChild(document.createTextNode(ISA_R.alb_menuItemlist));
	menuListFieldSet.appendChild(menuListFieldSetLegend);
	
	ISA_buildMenuExplorer(
		menuListFieldSet,
		function(menuItem){
			widgetInst.menuItem = menuItem
			var element;
			if( !/MultiRssReader/.test( menuItem.type ) ) {
				element =ISA_CommonModals.EditorForm.makeWidgetEditFieldSet(true, menuItem,{
				  omitTypeList: [],
				  disableDetailDisplayMode: true,
				  title: menuItem.title });
			} else {//TODO:MultiRss
				element = document.createElement("div");
				element.appendChild( ISA_CommonModals.EditorForm.makeWidgetEditFieldSet(true,  menuItem,{
				  omitTypeList: [],
				  disableURL: true,
				  disableAuthType: true
				} ) );
				menuItem.properties.children.each( function( childId ) {
					var child = Object.extend( Object.clone(IS_SiteAggregationMenu.menuItemList[ childId ] ),{
					  type: "RssReader"
					});
					element.firstChild.appendChild( ISA_CommonModals.EditorForm.makeWidgetEditFieldSet(true, child, {
					  omitTypeList: ["","Calendar","RssReader","MultiRssReader","MiniBrowser","Gadget",ISA_R.alb_others],
					  disablePreview: true,
					  disableDetailDisplayMode: true,
					  title: child.title
					}));
				}.bind( this ));
			}
			
			if(selectDynamicPanelWidget.childNodes.length > 1){
				selectDynamicPanelWidget.replaceChild( element, selectDynamicPanelWidget.childNodes[1]);
			}else{
				selectDynamicPanelWidget.appendChild( element );
			}
			
		});
	
	selectDynamicPanelWidget.appendChild(menuListFieldSet);
	
	return selectDynamicPanelWidget;
}