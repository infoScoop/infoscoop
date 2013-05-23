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

IS_Widget.activeEditForms = [];
IS_Widget.WidgetEdit = function (widget) {
	
	var self = this;
	var isStatic = widget.panelType == "StaticPanel";
	
	if(isStatic){
		var divWidgetEditHeader = widget.elm_widgetEditHeader;
		var editStyle = divWidgetEditHeader.style;
		editStyle.position = "absolute";
		editStyle.border = "1px solid gray";
		divWidgetEditHeader.parentNode.removeChild(divWidgetEditHeader);
		document.body.appendChild(divWidgetEditHeader);
		IS_Event.observe(document, "mouseup", function(e){
			if(this.style.display == "none") return;
			var target = Event.element(e);
			while(target != document.body){
				if(target == this)
					return;
				target = target.parentNode;
			}
			this.style.display = "none"
		}.bind(divWidgetEditHeader), false, this.closeId);
	}
	
	if( Browser.isSafari1 || Browser.isFirefox ) {
		// Form is unintentionally submitted; and move to unintended page
		IS_Event.observe( widget.elm_editForm,'submit',function( e ) {
			IS_Event.stop( e );
		},true );
	}
	
	this.displayContents = function () {
		this.clearContents();
		if(Browser.isIE)
			IS_Widget.activeEditForms.push(widget.elm_widgetEditHeader);

		if(isStatic){
			var elm_widgetContent = widget.elm_widgetContent;
			var contentWidth = elm_widgetContent.offsetWidth;
			if(!contentWidth || contentWidth < 200) contentWidth = 200;
			var editStyle = widget.elm_widgetEditHeader.style;
			editStyle.width = contentWidth;
			var widgetContentPos = Position.cumulativeOffset(elm_widgetContent);
			if(fixedPortalHeader) 
				widgetContentPos[1] -= IS_Portal.tabs[IS_Portal.currentTabId].panel.scrollTop;
			editStyle.top = widgetContentPos[1];
			editStyle.left = widgetContentPos[0];
		}
		
		var editNode = IS_WidgetConfiguration[widget.widgetType];
		
		var userPref = editNode.UserPref;
		self.autoBuild(userPref);

		if(widget.content && widget.content.buildEdit){
			widget.content.buildEdit(widget.elm_editForm);
		}

		var divEditCtrl = document.createElement('div');
		divEditCtrl.className = 'widgetEditCtrl';
		var divEditSave = document.createElement("span");
		divEditSave.className = "widgetSave";
		divEditSave.innerHTML = 'OK';
		divEditCtrl.appendChild(divEditSave);
		
		var divEditCancel = document.createElement('span');
		divEditCancel.className = "widgetCancel";
		divEditCancel.innerHTML = 'CANCEL';
		divEditCtrl.appendChild(divEditCancel);
		
		var cancelHandler = this.cancel.bind(this);
		Event.observe(divEditCancel, 'click', cancelHandler);
		var saveHandler = this.save.bind(this);
		Event.observe(divEditSave, 'click', saveHandler);
		
		// Not delete detail pop-up in opening and closing edit panel
		Event.observe(divEditCancel,'mousedown',Event.stop );
		Event.observe(divEditSave,'mousedown',Event.stop );

		//dojo.dom.insertBefore(divEditCtrl, widget.elm_editForm.firstChild);
		widget.elm_editForm.insertBefore(divEditCtrl, widget.elm_editForm.firstChild);
		
		if( hasRequiredPref() ) {
			var caution = document.createElement("div");
			caution.style.color = "red";
			caution.textAlign = "left";
			caution.fontSize = "75%";
			caution.style.fontFamily = "arial,sans-serif";
			caution.appendChild( createRequiredMark() );

			caution.appendChild( document.createTextNode(IS_R.ms_requiredItem));
			widget.elm_editForm.appendChild( caution );
		}
		
		var divEditCancel2 = document.createElement("div");
		divEditCancel2.className = "widgetCancel";
		divEditCancel2.innerHTML = 'CANCEL';
		Event.observe(divEditCancel2, 'click', cancelHandler);
		//dojo.dom.insertAfter(divEditCancel2, widget.elm_editForm.lastChild);
		widget.elm_editForm.appendChild(divEditCancel2);
		
		var divEditSave2 = document.createElement("div");
		divEditSave2.className = "widgetSave";
		divEditSave2.innerHTML = 'OK';
		Event.observe(divEditSave2, 'click', saveHandler);
		//dojo.dom.insertBefore(divEditSave2, widget.elm_editForm.lastChild);
		widget.elm_editForm.insertBefore(divEditSave2, widget.elm_editForm.lastChild);
		
		widget.elm_widgetEditHeader.appendChild(widget.elm_editForm);
		
		this.endBuild();
		IS_EventDispatcher.addListener("changeConnectionOfWidget", widget.id, self.hideEdit, null, true);
	}
	
	this.autoBuild = function(prefList){
		var editTable = document.createElement("table");
		var editBody = document.createElement("tbody");
		editTable.appendChild(editBody);
		editTable.style.fontSize = '80%';
		editTable.style.width = "100%";
		
		var titleEdit = widget.widgetPref && widget.widgetPref.titleEdit && getBooleanValue(widget.widgetPref.titleEdit.value) ||
			getBooleanValue( widget.getUserPref("titleEdit"));
		if(widget.panelType != "StaticPanel" && titleEdit ){
			
			var textboxTr = document.createElement("tr");
			var textboxLabelTd = document.createElement("td");
			textboxLabelTd.className = "widget_edit_pref_col_label";
			//Title
			textboxLabelTd.appendChild(document.createTextNode(IS_R.lb_widgetTitle + ':'));
			textboxTr.appendChild(textboxLabelTd);
			var textboxTd = document.createElement("td");
			textboxTd.className = "widget_edit_pref_col_value";
			var textbox = document.createElement('input');
			textbox.id = 'eb_' + widget.id + "_widget_title";
			textbox.type = "text";
			textbox.size = 30;
			textbox.maxLength = 80;
			textbox.name = "title";
			textbox.value = widget.title;
			textboxTd.appendChild(textbox);
			textboxTr.appendChild(textboxTd);
			editBody.appendChild(textboxTr);
		}
		
		var typeConf = IS_Widget.getConfiguration( widget.widgetType);
		for( id in prefList ){
			if(!(prefList[id] instanceof Function)){
				var pref = prefList[id];
				if(!pref.name) continue;
				var userPref = widget.getUserPref( pref.name );
				var displayName = pref.display_name;
				if(displayName == null){
					displayName = pref.name;
				}
				
				var userPrefConf = ( typeConf.UserPref || {} )[pref.name] || {};
				
				var inputType = IS_Widget.WidgetEdit.getInputType( pref );
				//displayName = escapeHTMLEntity(displayName);//No need for escape when createTextNode is used
				if(isStatic && (pref.name == "height" || getBooleanValue(pref.staticDisabled))){
					//do nothing.
				}else if( inputType == 'string' || inputType == 'location'){
					//Create text box
					var textboxTr = document.createElement("tr");
					var textboxLabelTd = document.createElement("td");
					textboxLabelTd.className = "widget_edit_pref_col_label";
					textboxLabelTd.appendChild(document.createTextNode(displayName + ':'));
					if( isRequiredPref( pref )) {
						textboxLabelTd.appendChild( createRequiredMark() );
					}
					textboxTr.appendChild(textboxLabelTd);
					var textboxTd = document.createElement("td");
					textboxTd.id = 'eb_' + widget.id + "_" + pref.name;
					textboxTd.className = "widget_edit_pref_col_value";
					textboxTd.innerHTML = '<input type="text" name="'+escapeHTMLEntity(pref.name)+'"'+
						' value="'+(userPref?escapeHTMLEntity( userPref ):"")+'"/>';
					
					textboxTr.appendChild(textboxTd);
					editBody.appendChild(textboxTr);
					
					IS_Widget.WidgetEdit.makeHelpIcon( textboxTd,userPrefConf );
				}else if( inputType == 'xml' || inputType == 'json' || inputType == 'textarea'){
					var textboxTr = document.createElement("tr");
					var textboxLabelTd = document.createElement("td");
					textboxLabelTd.className = "widget_edit_pref_col_label";
					textboxLabelTd.appendChild(document.createTextNode(displayName + ':'));
					if( isRequiredPref( pref )) {
						textboxLabelTd.appendChild( createRequiredMark() );
					}
					textboxTr.appendChild(textboxLabelTd);
					var textboxTd = document.createElement("td");
					textboxTd.id = 'eb_' + widget.id + "_" + pref.name;
					textboxTd.className = "widget_edit_pref_col_value";
					
					var textarea = document.createElement("textarea");
					textarea.name = pref.name;
					if( userPref )
						textarea.value = userPref;
					textboxTd.appendChild( textarea );
					
					textboxTr.appendChild(textboxTd);
					editBody.appendChild(textboxTr);
					
					IS_Widget.WidgetEdit.makeHelpIcon( textboxTd,userPrefConf );
				}else if( inputType == 'checkbox' || inputType == 'bool'){
					//Create check box
					var textboxTr = document.createElement("tr");
					var textboxLabelTd = document.createElement("td");
					textboxLabelTd.className = "widget_edit_pref_col_label";
					textboxLabelTd.appendChild(document.createTextNode(displayName + ':'));
					if( isRequiredPref( pref )) {
						textboxLabelTd.appendChild( createRequiredMark() );
					}
					textboxTr.appendChild(textboxLabelTd);
					var textboxTd = document.createElement("td");
					textboxTd.id = 'eb_' + widget.id + "_" + pref.name;
					textboxTd.className = "widget_edit_pref_col_value";
					
					var checkbox = "";
					if( /true/i.test( userPref ) ){
						checkbox += '<input type="checkbox" name="'+escapeHTMLEntity(pref.name)+'" checked>';
					}else{
						checkbox += '<input type="checkbox" name="'+escapeHTMLEntity(pref.name)+'">';
					}
					checkbox += '</input>';
					textboxTd.innerHTML = checkbox;
					textboxTr.appendChild(textboxTd);
					
					editBody.appendChild(textboxTr);
					
					IS_Widget.WidgetEdit.makeHelpIcon( textboxTd,userPrefConf );
				}else if( inputType == 'select' || inputType =='enum'){
					//Create pulldown menu
					var enumValues = pref.EnumValue;
					var pulldown = "";
					for ( var i=0; enumValues && i<enumValues.length; i++ ) {
						var value = enumValues[i].value;
						if( value == null ) continue;
						
						var name = enumValues[i].display_value;
						if (name == null || name.length == 0) {
							name = value;
						}
						
						pulldown += '<option value="' + escapeHTMLEntity( value ) +'"' ;
						if ( userPref == value )
							pulldown += ' selected'
						
						pulldown += '>' +escapeHTMLEntity( name );
					}
					var textboxTr = document.createElement("tr");
					var textboxLabelTd = document.createElement("td");
					textboxLabelTd.className = "widget_edit_pref_col_label";
					textboxLabelTd.appendChild(document.createTextNode(displayName + ':'));
					textboxTr.appendChild(textboxLabelTd);
					if( isRequiredPref( pref )) {
						textboxLabelTd.appendChild( createRequiredMark() );
					}
					var textboxTd = document.createElement("td");
					textboxTd.id = 'eb_' + widget.id + "_" + pref.name;
					textboxTd.className = "widget_edit_pref_col_value";
					textboxTd.innerHTML = '<select name="'+escapeHTMLEntity(pref.name)+'">'+pulldown+'</select>';
					textboxTr.appendChild(textboxTd);
					
					editBody.appendChild(textboxTr);
					
					IS_Widget.WidgetEdit.makeHelpIcon( textboxTd,userPrefConf );
				}else if( inputType == 'radio'){
					//Create radio button
					var textboxTr = document.createElement("tr");
					var textboxLabelTd = document.createElement("td");
					textboxLabelTd.className = "widget_edit_pref_col_label";
					textboxLabelTd.appendChild(document.createTextNode(displayName + ':'));
					if( isRequiredPref( pref )) {
						textboxLabelTd.appendChild( createRequiredMark() );
					}
					textboxTr.appendChild(textboxLabelTd);
					var textboxTd = document.createElement("td");
					textboxTd.id = 'eb_' + widget.id + "_" + pref.name;
					textboxTd.className = "widget_edit_pref_col_value";
					
					var enumValues = pref.EnumValue;
					var radioId = textboxTd.id+"_radio";
					var radioButton = '<div id="'+radioId+'">';
					for ( var i=0; enumValues && i<enumValues.length; i++ ) {
						var value = enumValues[i].value;
						if( value == null || value.length == 0 ) continue;
						
						var name = enumValues[i].display_value;
						if (!name || name.length == 0) {
							name = value;
						}
						
						radioButton += '<input type="radio" name="'+escapeHTMLEntity(pref.name)+'" value="' + value +'"';
						if ( userPref == value )
							radioButton += ' checked'
						
						radioButton += '>' + escapeHTMLEntity(name) +'</input>';
					}
					radioButton += '</div>';
					
					textboxTd.innerHTML = radioButton;
					textboxTr.appendChild(textboxTd);
					
					editBody.appendChild(textboxTr);
					
					IS_Widget.WidgetEdit.makeHelpIcon( textboxTd,userPrefConf );
				}else if( inputType == 'password'){
					//Create password box
					var textboxTr = document.createElement("tr");
					var textboxLabelTd = document.createElement("td");
					textboxLabelTd.className = "widget_edit_pref_col_label";
					textboxLabelTd.appendChild(document.createTextNode(displayName + ':'));
					if( isRequiredPref( pref )) {
						textboxLabelTd.appendChild( createRequiredMark() );
					}
					textboxTr.appendChild(textboxLabelTd);
					var textboxTd = document.createElement("td");
					textboxTd.id = 'eb_' + widget.id + "_" + pref.name;
					textboxTd.className = "widget_edit_pref_col_value";
					
					textboxTd.innerHTML = '<input type="password" name="'+escapeHTMLEntity(pref.name)+'"'+
						' value="'+(userPref?escapeHTMLEntity( userPref ):"")+'"/>';
					textboxTr.appendChild(textboxTd);
					
					editBody.appendChild(textboxTr);
					
					IS_Widget.WidgetEdit.makeHelpIcon( textboxTd,userPrefConf );
				}else if( inputType == 'list' ){
					
					var textboxTr = document.createElement("tr");
					var textboxLabelTd = document.createElement("td");
					textboxLabelTd.className = "widget_edit_pref_col_label";
					textboxLabelTd.appendChild(document.createTextNode(displayName + ':'));
					if( isRequiredPref( pref )) {
						textboxLabelTd.appendChild( createRequiredMark() );
					}
					textboxTr.appendChild(textboxLabelTd);
					var textboxTd = document.createElement("td");
					textboxTd.id = 'eb_' + widget.id + "_" + pref.name;
					
					//Create menu to edit array
					if( typeof( userPref ) == "string" ) {
						arrayData = is_toUserPrefArray(userPref);
					} else if( userPref instanceof Array ){
						arrayData = userPref;
					} else if( userPref ){
						arrayData = [userPref];
					} else {
						arrayData = [];
					}
					
					var arrayDiv = document.createElement("div");
					var listTable = document.createElement("table");
					listTable.className = 'widgetEditHeader';
					listTable.style.fontSize = '100%';
					listTable.cellPadding = 0;
					listTable.cellSpacing = 0;
					var tableBody = document.createElement("tbody");
					tableBody.id = textboxTd.id + "_arrayTbody";
					listTable.appendChild( tableBody );
					arrayDiv.appendChild( listTable );
					
					var deleteFunc = function(obj){
						var deleteNode = obj.parentNode.parentNode;
						deleteNode.parentNode.removeChild( deleteNode );
					};	
					
					for(j=0; j<arrayData.length; j++){
						var item = document.createElement("tr");
						item.style.height = "1.5em";
						var textTd = document.createElement("td");
						textTd.appendChild( document.createTextNode( arrayData[j] ) );
						
						var buttonTd = document.createElement("td");
						var deleteButton = document.createElement("span");

						deleteButton.appendChild( document.createTextNode( IS_R.lb_delete ) );
						//deleteButton.className = 'widgetCancel';
						//deleteButton.style.fontSize = '100%';
						deleteButton.style.color = '#7777cc';
						deleteButton.style.cursor = 'pointer';
						deleteButton.style.whiteSpace = 'nowrap';
						Event.observe(deleteButton, 'click', deleteFunc.bind(this, deleteButton), false);
						buttonTd.appendChild( deleteButton );
						
						item.appendChild( textTd );
						item.appendChild( buttonTd );
						tableBody.appendChild( item );
					}
					
					var addItem = document.createElement("tr");
					var addBoxTd = document.createElement("td");
					addBoxTd.innerHTML = '<input name="input" type="text" name="'+escapeHTMLEntity(pref.name)+'" value=""/>';
					var addButtonTd = document.createElement("td");
					var addButton = document.createElement("span");

					addButton.appendChild( document.createTextNode( IS_R.lb_add ) );
					//addButton.className = 'widgetSave';
					//addButton.style.fontSize = '100%';
					addButton.style.color = '#7777cc';
					addButton.style.cursor = 'pointer';
					addButton.style.whiteSpace = 'nowrap';
					var addFunc = function(addButtonObj){
						var addText = addButtonObj.parentNode.previousSibling.firstChild.value || "";
//						if(addText != ''){
							var addItem = document.createElement("tr");
							addItem.style.height = "1.5em";
							var textTd = document.createElement("td");
							textTd.appendChild( document.createTextNode( addText ) );
							
							var buttonTd = document.createElement("td");
							var deleteButton = document.createElement("span");

							deleteButton.appendChild( document.createTextNode( IS_R.lb_delete ) );
							//deleteButton.className = 'widgetCancel';
							//deleteButton.style.fontSize = '100%';
							deleteButton.style.color = '#7777cc';
							deleteButton.style.cursor = 'pointer';
							deleteButton.style.whiteSpace = 'nowrap';
							Event.observe(deleteButton, 'click', deleteFunc.bind(this, deleteButton), false);
							buttonTd.appendChild( deleteButton );
							
							addItem.appendChild( textTd );
							addItem.appendChild( buttonTd );
							
							addBoxTd.firstChild.value="";
							//dojo.dom.insertBefore(addItem, addButtonObj.parentNode.parentNode);
							addButtonObj.parentNode.parentNode.parentNode.insertBefore(addItem, addButtonObj.parentNode.parentNode);
//						}
					};
					Event.observe(addButton, 'click', addFunc.bind(this, addButton), false);
					addButtonTd.appendChild( addButton );
					
					addItem.appendChild( addBoxTd );
					addItem.appendChild( addButtonTd );
					tableBody.appendChild( addItem );
					
					textboxTd.appendChild(arrayDiv);
					
					textboxTr.appendChild(textboxTd);
					
					editBody.appendChild(textboxTr);
					
					IS_Widget.WidgetEdit.makeHelpIcon( addBoxTd,userPrefConf );
				}else if( inputType == 'hidden'){
					// do nothing
				} else if( inputType == 'calendar') {
					var textboxTr = document.createElement("tr");
					var textboxLabelTd = document.createElement("td");
					textboxLabelTd.className = "widget_edit_pref_col_label";
					textboxLabelTd.appendChild(document.createTextNode(displayName + ':'));
					if( isRequiredPref( pref )) {
						textboxLabelTd.appendChild( createRequiredMark() );
					}
					textboxTr.appendChild(textboxLabelTd);
					var textboxTd = document.createElement("td");
					textboxTd.id = 'eb_' + widget.id + "_" + pref.name;
					textboxTd.className = "widget_edit_pref_col_value";
					var textbox = document.createElement("input");
					textbox.type = "text";
					textbox.name = pref.name;
					textbox.value = userPref? escapeHTMLEntity( userPref ):"";
					textboxTd.appendChild( textbox );
					textboxTr.appendChild( textboxTd );
					
					editBody.appendChild( textboxTr );
					
					var dateFormat;
					//if( widget.widgetPref.dateFormat )
					//	dateFormat = widget.widgetPref.dateFormat.value;
					
					if( !dateFormat )
						dateFormat = "YYYY/MM/DD";
					
					if( userPref ) {
						var date = CalendarInput.parseDate("YYYY/MM/DD",userPref );
						if( date ) {
							textbox.value = CalendarInput.toDateString( dateFormat,date );
						}
					}
					pref.calendar = new CalendarInput( textbox,dateFormat );
					IS_EventDispatcher.addListener('closeWidget', widget.id.substring(2),function(){
						if( this.calendar ) {
							this.calendar.uninstall();
							this.calendar = null;
						}
					}.bind( pref ) );
				}
			}	
		}
		widget.elm_editForm.appendChild(editTable);
	}
		
	//Start to edit
	this.save = function () {
		if( !errorCheck() ) {
			//widget.elm_widgetEditHeader.style.display = "block";
			
			return;
		}
		
		widget.elm_widgetEditHeader.style.display = "none";
		IS_Portal.behindIframe.hide();
		
		var form = document.forms["frm_" + widget.id];
		if (form ) {
			this.autoEditSave(form);
			
			var editNode = IS_WidgetConfiguration[widget.widgetType];
			if(widget.content){
				if(widget.content.saveEdit){
					widget.content.saveEdit(widget, widget.elm_editForm);
				}
			}else{
				if(widget.saveEdit){
					widget.saveEdit(widget, widget.elm_editForm);
				}
			}
			
			if( widget.isGadget())
				widget.headerContent.updateTitle();
			
				/*
			if(editNode.editPanel) {
				var editInput = editNode.editPanel.input;
				for( id in editInput ){
					var data = editInput[id];
					
					if(data.type == "javascript"){
						//var funcName = IS_WidgetsContainer.funcList[widget.id];
						if(data.saveFunc){
							var funcName = data.saveFunc.content;
							if (funcName && widget.content) {
								var func = new Function("widget", "form", "widget.content." + funcName + "(widget, form);");
								func(widget, form);
							}else if(funcName){
								var func = new Function("widget", "form", "widget." + funcName + "(form);");
								func(widget, form);
							}
						}
					}else if(data.type == "html"){
						// Keep: the final decision is deferred
					}
				}
			}
					*/
		}
		
		if( widget.elm_title && widget.elm_title.firstChild && widget.headerContent ) {
			var displayTitle = widget.headerContent.getTitle();
			if( widget.elm_title.firstChild.firstChild ) {
				widget.elm_title.firstChild.replaceChild(
					document.createTextNode( displayTitle ),
					widget.elm_title.firstChild.firstChild );
			} else {
				widget.elm_title.replaceChild(
					document.createTextNode( displayTitle ),
					widget.elm_title.firstChild );
			}
		}
		
		if( !widget.isSuccess ) {
			widget.loadContents();
		} else {
			
			/*
			if(widget.content && widget.content.displayContentsWithoutLoad) {
				widget.content.displayContentsWithoutLoad();
			} else if (widget.content && widget.content.displayContents) {
				widget.content.displayContents();
			}*/
			// fix #14
			if (widget.content && widget.content.postEdit) {
				widget.content.postEdit();
			}
			else if (widget.content && widget.content.loadContents) {
				widget.content.loadContents();
			}
			else if (widget) {
				widget.loadContents();
			}
		}
		widget.headerContent.applyAllIconStyle();
		
		this.endSave();
	}
	this.hasError = function() {
		return ( getErrors() != null );
	};
	function getErrors() {
		var errors = [];
		
		var titleEdit = $("eb_"+widget.id+"_widget_title");
		if (titleEdit) {
			var newTitle = $F("eb_" + widget.id + "_widget_title");
			if(newTitle.replace(/[ 　\s]/g, "").length == 0)
				errors.push( IS_R.lb_widgetTitle + " : " + IS_R.lb_widgetTitle_empty );
		}
		var validator = widget.getContentObject("_IS_Validate");
		
		var widgetConf = IS_Widget.getConfiguration( widget.widgetType );
		var prefList = widgetConf.UserPref;
		for( id in prefList ){
			var pref = prefList[id];
			if(!( pref instanceof Function )) {
				var displayName = pref.display_name;
				if( !displayName )
					displayName = pref.name;
				
				if( isStatic && (pref.name == "height" || getBooleanValue(pref.staticDisabled)) )
					continue;
				
				if( !self.acceptRequired( pref ) ) {

					errors.push(IS_R.getResource(IS_R.ms_emptyAt,[displayName]))
				} else if(validator && validator[pref.name]){
					var value = getAutoEditValue( pref );
					var error = validator[pref.name](value);
					if(error) errors.push(error);
				}
			}
		}
		
		if( errors.length > 0 ) {
			return errors;
		}
		
		return null;
	}
	function errorCheck() {
		var errors = getErrors();
		if( errors ) {
			alert( errors.join("\n"));
			
			return false;
		}
		
		return true;
	}
	function createRequiredMark() {
		var sup = document.createElement("sup");
		sup.style.color = "red";
		sup.appendChild( document.createTextNode("*"));
		
		return sup;
	}
	function isRequiredPref( pref ) {
		// set always 'not required' if widget type is not Gadget
		if( !( widget.widgetType.indexOf("g_") == 0 ))
			return false;
		
		return ( pref.datatype != 'hidden' && pref.required && /true/i.test( pref.required ));
	}
	function hasRequiredPref() {
		var userPref = IS_WidgetConfiguration[widget.widgetType].UserPref;
		for( var i in userPref ) {
			var pref = userPref[i];
			if( !( pref instanceof Function ) ) {
				if( isRequiredPref( pref )) {
					return true;
				}
			}
		}
		
		return false;
	}
	this.acceptRequired = function( pref ) {
		if( isRequiredPref( pref )) {
			var value = getAutoEditValue( pref );
			if( value == undefined || value == "" || value.length == 0 )
				return false;
		}
		
		return true;
	}
	function getAutoEditValue( pref ) {
		var divId = "eb_"+widget.id+"_"+pref.name;
		var div = document.getElementById( divId );
		if( !div )
			return null;
		
		var value;
		var inputType = IS_Widget.WidgetEdit.getInputType( pref );
		if( inputType == 'string' || inputType == 'location'){
			value = div.firstChild.value;
		}else if( inputType == 'xml' || inputType == 'json' || inputType == 'textarea'){
			value = div.firstChild.value;
		}else if( inputType == 'checkbox'){
			value = div.firstChild.checked;
		}else if( inputType == 'select'){
			var select = div.firstChild;
			if(select.selectedIndex >= 0)
				value = select[select.selectedIndex].value;
		}else if( inputType == 'radio' && pref.EnumValue ){
			var radio = document.getElementById( div.id+"_radio" );
			for(var i=0; i<radio.childNodes.length; i++){
				if( radio.childNodes[i].checked ){
					value = radio.childNodes[i].value;
				}
			}
		}else if( inputType == 'password'){
			value = div.firstChild.value;
		}else if( inputType == 'list'){
			var arrayTbody = document.getElementById( div.id+"_arrayTbody");
			var data = arrayTbody.childNodes;
			
			var array = [];
			for(var j=0; j<(data.length-1); j++){
				var dataNode = data[j].firstChild.firstChild;
				
				array.push( String( dataNode.nodeValue ).replace(/\|/g, "%7C") );
			}
			
			value = array.join("|");
		} else if( inputType == 'calendar') {
			var dateFormat;
//			if( widget.widgetPref.dateFormat )
//				dateFormat = widget.widgetPref.dateFormat.value;
			
			if( !dateFormat )
				dateFormat = "YYYY/MM/DD";
			
			var inputValue = div.firstChild.firstChild.value;
			var date = CalendarInput.parseDate( dateFormat,inputValue );
			if( date ) {
				value = date.getFullYear()+"/"+(date.getMonth()+1)+"/"+date.getDate();
			} else {
				value = inputValue;
			}
		}
		
		if (value) {
			if(typeof value != "boolean")
				value = value.replace(/^\s*/, "").replace(/\s*$/, "");
		}
		return value;
	}
	this.autoEditSave = function(form){

		var titleEdit = $("eb_"+widget.id+"_widget_title");
		if(titleEdit){
			var newTitle = $F("eb_"+widget.id+"_widget_title");
			newTitle = newTitle.replace(/^[ 　\s]*/, "").replace(/[ 　\s]*$/,"");
			//Send to Server
			if(newTitle != widget.title){
				widget.title = newTitle;
				widget.widgetConf.title = widget.title;
				widget.headerContent.buildTitle();
				IS_Widget.setWidgetPrefernceCommand(widget, "title", widget.title);
			}
		}
		var editNode = IS_WidgetConfiguration[widget.widgetType];
		var prefList = editNode.UserPref;
		for( id in prefList ){
			if( !(prefList[id] instanceof Function) ){
				var pref = prefList[id];
				var divId = "eb_"+widget.id+"_"+pref.name;
				var div = document.getElementById( divId );
				if(!div) continue;
				var value = null;
				
				var inputType = IS_Widget.WidgetEdit.getInputType( pref );
				if( inputType == 'string' || inputType == 'location'){
					value = div.firstChild.value;
				}if( inputType == 'xml' || inputType == 'json' || inputType == 'textarea'){
					value = div.firstChild.value;
				}else if( inputType == 'checkbox'){
					value = div.firstChild.checked;
				}else if( inputType == 'select'){
					var select = div.firstChild;
					if(select.selectedIndex >= 0)
						value = select[select.selectedIndex].value;
				}else if( inputType == 'radio' && pref.EnumValue ){
					var radio = document.getElementById( div.id+"_radio" );
					for(var i=0; i<radio.childNodes.length; i++){
						if( radio.childNodes[i].checked ){
							value = radio.childNodes[i].value;
						}
					}
				}else if( inputType == 'password'){
					value = div.firstChild.value;
				}else if( inputType == 'list'){
					var arrayTbody = document.getElementById( div.id+"_arrayTbody");
					var data = arrayTbody.childNodes;
					
					var array = [];
					for(var j=0; j<(data.length-1); j++){
						var dataNode = data[j].firstChild.firstChild;
						
						array.push( String( dataNode.nodeValue ).replace(/\|/g, "%7C") );
					}
					
					value = array.join("|");
				} else if( inputType == 'calendar') {
					var dateFormat;
//					if( widget.widgetPref.dateFormat )
//						dateFormat = widget.widgetPref.dateFormat.value;
					
					if( !dateFormat )
						dateFormat = "YYYY/MM/DD";
					
					var inputValue = div.firstChild.firstChild.value;
					var date = CalendarInput.parseDate( dateFormat,inputValue );
					if( date ) {
						value = date.getFullYear()+"/"+(date.getMonth()+1)+"/"+date.getDate();
					} else {
						value = inputValue;
					}
				}
				
				if(value != null){
					if (value) {
						if(typeof value != "boolean")
							value = value.replace(/^\s*/, "").replace(/\s*$/, "");
					}
					widget.setUserPref(pref.name, value );
				}
			}
		}
	}
	
	/**
	 * Process after building completes
	 */
	this.endBuild = function(){
		setTimeout(IS_Portal.widgetDisplayUpdated, 1);
	}
	
	this.hideContents = function(){
		widget.elm_widgetEditHeader.style.display = "none";
		IS_Portal.behindIframe.hide();
		this.clearContents();
		//this.displayContents();
		
		setTimeout(IS_Portal.widgetDisplayUpdated, 1);
	}
	
	/**
	 * Process after saving completes
	 */
	this.endSave = function(){
		setTimeout(IS_Portal.widgetDisplayUpdated, 1);
		
		IS_EventDispatcher.removeListener("changeConnectionOfWidget", widget.id, self.hideEdit);
	}
	
	this.cancel = function () {
		IS_EventDispatcher.removeListener("changeConnectionOfWidget", widget.id, self.hideEdit);
		this.hideContents();
	}
	
	this.clearContents = function () {
		widget.elm_editForm.innerHTML ="";
		
		//Remove pop-up in UserPref[@inputType='calendar']
		var prefList = widget.widgetConf.UserPref;
		for( id in prefList ){
			var pref = prefList[id];
			if(!( pref instanceof Function )) {
				if( pref.inputType == "calendar") {
					if( pref.calendar ) {
						pref.calendar.uninstall();
						pref.calendar = null;
					}
				}
			}
		}
	}
	
	this.hideEdit = this.hideContents.bind(this);
	
//	this.displayContents();
	
//	IS_EventDispatcher.addListener("changeConnectionOfWidget", widget.id, hideEdit, null, true);

};

IS_Widget.adjustEditPanelTextWidth = function(element){
	if(!Browser.isIE) return;
	
	var forms = IS_Widget.activeEditForms;
	// Delete the reference of EDITPANEL element that is unshown
	for(var i=0;i<forms.length;i++){
		if(forms[i] && forms[i].style.display == "none"){
			forms[i].orgWidth = null;
			forms[i] = null;
		}
	}
	
	var setTextSize = function(){
		var form = element;
		var inputEls = form.getElementsByTagName("input");
		var adjustItems = [];
		
		for(var i=0;i<inputEls.length;i++){
			if(inputEls[i].type == "text"){
				var inputWidth = getActiveStyle(inputEls[i], "width");
				if(inputWidth){
					if(inputEls[i].orgWidth || inputWidth.indexOf("%") != -1){
						inputEls[i].style.display = "none";
							
						adjustItems.push(inputEls[i]);
					}
				}
			}
		}
		
		for(var i=0;i<adjustItems.length;i++){
			var per = (adjustItems[i].orgWidth)? parseInt(adjustItems[i].orgWidth) : parseInt(inputWidth);
			var parentWidth = adjustItems[i].parentNode.offsetWidth - 5;
			var resultWidth = parentWidth * (per/100);
			
			if(isNaN(resultWidth) || resultWidth <= 0 ){
				adjustItems[i].style.display = "block";
				continue;
			}
			
			adjustItems[i].style.width = resultWidth;
			
			if(!adjustItems[i].orgWidth)
				adjustItems[i].orgWidth = inputWidth;
			
			adjustItems[i].style.display = "block";
		}
	}
	setTimeout(setTextSize, 1);
}

IS_Widget.adjustEditPanelsTextWidth = function(){
	if(!Browser.isIE) return;
	
	var forms = IS_Widget.activeEditForms;
	for(var i=0;i<forms.length;i++){
		if(forms[i] && forms[i].style.display != "none")
			IS_Widget.adjustEditPanelTextWidth(forms[i]);
	} 
}

IS_Widget.WidgetEdit.getInputType = function(pref){
	
	var datatype = pref.datatype;

	switch (datatype){
	  case 'bool':
		return 'checkbox';
	  case 'list':
		return 'list';
	  case 'enum':
		return 'select';
	  case 'xml':
	  case 'json':
	  case 'textarea':
		return 'textarea';
	  case 'radio':
		return 'radio';
	  case 'password':
		return 'password';
	  case 'calendar':
		return 'calendar';
	  case 'hidden':
		return 'hidden';
	  default:
		return 'string';
	}
}

IS_Widget.WidgetEdit.makeHelpIcon = function( container,userPref ) {
	if( !userPref.description )
		return;
	
	var a = $( document.createElement("a"));
	a.href = "javascript:void(0)";
	a.innerHTML = "?";
	a.className = "help";
	container.appendChild( a );
	
	var help = $( document.createElement("div"));
	help.className = "widgetEditHelp";
	document.body.appendChild( help );
	
	var descHeader = $( document.createElement("div"));
	descHeader.className = "header";
	help.appendChild( descHeader );
	descHeader.appendChild( document.createTextNode( IS_R.lb_widgetEditHelp ));
	
	var description = $( document.createElement("div"));
	description.className = "description";
	description.innerHTML = userPref.description;
	help.appendChild( description );
	help.style.top = 0;
	help.style.left = 0;
	
	a.observe("mouseover",function( event ) {
		help.style.display = "block";
		if( help.offsetWidth > 300 )
			help.style.width = 300+'px';

		var x = event.pointerX();
		var y = event.pointerY();
		var x_limit = getWindowSize(true) +document.body.scrollLeft;
		var y_limit = getWindowSize(false) +document.body.scrollTop;
		if( x +help.offsetWidth > x_limit )
			x = x -help.offsetWidth -48;
		
		if( y +help.offsetHeight > y_limit )
			y = y_limit -help.offsetHeight -10;
		
		help.style.top = y + 'px';
		help.style.left = x + 'px';
	});
	a.observe("mouseout",function(){
		help.style.display = "none";
		help.style.top = help.style.left = 0;
	})
}
