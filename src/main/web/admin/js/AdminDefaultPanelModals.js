ISA_DefaultPanel.CommandItemEditor = IS_Class.create();
ISA_DefaultPanel.CommandItemEditor.prototype.classDef = function() {
	var iconElm, commandItem;
	var buildForm, onOK;
	var self = this;
	
	/**
	 * Modal window displaying edit page of command bar contents
	 * @param {HTMLElement} _iconElm Element to be clicked
	 * @param {Object} _commandItem Asigned Command bar contents
	 * @param {Function} _buildForm(commandItem) Callback function to create editting page
	 * @param {Function} _onOK(commandItem) Callback function called if OK button is pressed. Return false at error.
	 */
	this.initialize = function(_iconElm, _commandItem, _buildForm, _onOK){
		iconElm = _iconElm;
		commandItem = _commandItem;
		buildForm = _buildForm;
		onOK = _onOK;
		IS_Event.observe(iconElm, 'click', this.show.bind(this), false, "_adminPanel");
	}
	
	this.show = function(){
		var editor = this.buildEditorWindow(buildForm(commandItem));

		if(!this.modal){
			this.modal = new Control.Modal('',{
				afterClose:this.hide
			});
		}

		this.modal.container.update(editor);
		this.modal.open();
	}
	
	this.buildEditorWindow = function(form){
		var editorDiv = document.createElement("div");
		commandItem.title;
		var editorFieldSet = document.createElement("div");
		editorDiv.className = "modalConfigSet";
		var editorFieldSetLegend = document.createElement("p");
		editorFieldSetLegend.className = "modalConfigSetHeader";
		editorFieldSetLegend.appendChild(document.createTextNode(ISA_R.getResource(ISA_R.alb_settingOf,[commandItem.title])));
		editorFieldSet.appendChild(editorFieldSetLegend);
		editorFieldSet.appendChild(form);
		editorDiv.appendChild(editorFieldSet);
		
		var buttonDiv = document.createElement('div');
		buttonDiv.style.textAlign = "center";
		
		var elementInput = document.createElement("input");
		elementInput.className = "modal_button";
		elementInput.type = "button";
		elementInput.id = "formExec";
		elementInput.name = "FORM_EXEC";
		elementInput.value = ISA_R.alb_ok;
		IS_Event.observe(elementInput, 'click', self.submit.bind(self), false, "_adminPanel");
		buttonDiv.appendChild(elementInput);
		
		var closeButton = document.createElement("input");
		closeButton.className = "modal_button";
		closeButton.type = "button";
		closeButton.id = "formCancel";
		closeButton.value = ISA_R.alb_cancel;
		IS_Event.observe(closeButton, 'click', self.hide.bind(self), false, "_adminPanel");
		buttonDiv.appendChild(closeButton);
		editorDiv.appendChild(buttonDiv);
		
		return editorDiv;
	}
	
	this.submit = function() {
		if(onOK(commandItem))
			Control.Modal.close();
	}
	
	this.hide = function() {
		Control.Modal.close();
	}
}

/**
 * Adding CommandBarModal
 */
ISA_DefaultPanel.prototype.addCommandBarModal = {
	isaDefaultPanel: false,
	controlModal: false,
	init: function() {
		this.addCommandBarModal.isaDefaultPanel = this;
		if(!this.addCommandBarModal.controlModal){
			this.addCommandBarModal.controlModal = new Control.Modal('',{
				afterClose:this.addCommandBarModal.hide
			});
		}
		this.addCommandBarModal.load();
	},
	load: function() {
		var self = this;
		var viewForm = function() {
			var formDiv = document.createElement("div");
			formDiv.id = "panelCommandBarModal";
			self.build(formDiv);
			self.controlModal.container.update(formDiv);
			self.controlModal.open();
		}
		setTimeout(viewForm, 10);
	},
	build: function(formDiv) {
		var self = this;
		ISA_Admin.isUpdated = true;
		var messageLabel = document.createElement("div");
		messageLabel.style.clear = "both";
		messageLabel.appendChild(document.createTextNode(ISA_R.alb_sertLinkCommandbar));
		
		formDiv.appendChild(messageLabel);
		formDiv.appendChild(self.isaDefaultPanel.commandBarEditor.link.buildForm());
		
		var okDiv = document.createElement("div");
		okDiv.style.clear = "both";
		okDiv.style.textAlign = "center";
		var okA = document.createElement("input");
		okA.type = 'button';
		okA.value = ISA_R.alb_add;
		okDiv.appendChild(okA);
		var okClick = function(e) {
			self.isaDefaultPanel.commandBarEditor.link.onOK();
			self.hide();
		};
		IS_Event.observe(okA, "click", okClick, false, "_adminPanel");
		
		var closeA = document.createElement("input");
		closeA.type = "button";
		closeA.value = ISA_R.alb_cancel;
		okDiv.appendChild(closeA);
		IS_Event.observe(closeA, "click", this.hide.bind(this), false, "_adminPanel");
		formDiv.appendChild(okDiv);
	},
	hide: function() {
		Control.Modal.close();
	}
};

/**
 * Adding CommandBarModal(HTML)
 */
ISA_DefaultPanel.prototype.addHTMLCommandBarModal = {
	isaDefaultPanel: false,
	controlModal: false,
	init: function() {
		this.addHTMLCommandBarModal.isaDefaultPanel = this;
		if(!this.addHTMLCommandBarModal.controlModal){
			this.addHTMLCommandBarModal.controlModal = new Control.Modal('', {
				afterClose:this.addHTMLCommandBarModal.hide
			});
		}
		this.addHTMLCommandBarModal.load();
	},
	load: function() {
		var self = this;
		var viewForm = function() {
			var formDiv = document.createElement("div");
			formDiv.id = "panelHTMLCommandBarModal";
			self.build(formDiv);
			self.controlModal.container.update(formDiv);
			self.controlModal.open();
		}
		setTimeout(viewForm, 10);
	},
	build: function(formDiv) {
		var self = this;
		
		var messageLabel = document.createElement("div");
		messageLabel.style.clear = "both";
		messageLabel.innerHTML = ISA_R.alb_addHtmlMessage
		formDiv.appendChild(messageLabel);
		
		formDiv.appendChild(self.isaDefaultPanel.commandBarEditor.html.buildForm());
		
		var okDiv = document.createElement("div");
		okDiv.style.clear = "both";
		okDiv.style.textAlign = "center";
		var okA = document.createElement("input");
		okA.type = 'button';
		okA.value = ISA_R.alb_add;
		okDiv.appendChild(okA);
		var okClick = function(e) {
			self.isaDefaultPanel.commandBarEditor.html.onOK();
			self.hide();
		};
		IS_Event.observe(okA, "click", okClick, false, "_adminPanel");
		
		var closeA = document.createElement("input");
		closeA.type = "button";
		closeA.value = ISA_R.alb_cancel;
		okDiv.appendChild(closeA);
		IS_Event.observe(closeA, "click", this.hide.bind(this), false, "_adminPanel");
		formDiv.appendChild(okDiv);
	},
	hide: function() {
		Control.Modal.close();
	}
};

/**
 * Select layout
 */
ISA_DefaultPanel.prototype.selectLayoutModal = {
	isaDefaultPanel: false,
	templates: false,
	controlModal: false,
	init: function() {
		this.selectLayoutModal.isaDefaultPanel = this;
		this.selectLayoutModal.templates = this.templates;
		if(!this.selectLayoutModal.controlModal){
			this.selectLayoutModal.controlModal = new Control.Modal('', {
				className:"adminDefaultPanel",
				afterClose:this.selectLayoutModal.hide
			});			
		}
		this.selectLayoutModal.load();
	},
	load: function() {
		var self = this;
		var viewForm = function() {
			var formDiv = document.createElement("div");
			formDiv.id = "panelStaticLayoutModal";
			self.build(formDiv);
			self.controlModal.container.update(formDiv);
			self.controlModal.open();
		}
		setTimeout(viewForm, 10);
	},
	build: function(formDiv) {
		formDiv.appendChild(
			$.DIV({style:"clear:both;padding:3px;"},
				  $.DIV({}, ISA_R.alb_selectTemplate),
				  $.DIV({style:"color:red;fontWeight:bold"},ISA_R.alb_destroyOldSettings)
					)
			);
		
		// This must be increased if any file is added to admin/staticPanel
		var adjustToWindowHeight = (this.isaDefaultPanel.displayRoleJsons[this.isaDefaultPanel.displayRoleId] && this.isaDefaultPanel.displayRoleJsons[this.isaDefaultPanel.displayRoleId].adjustToWindowHeight ? true : false);
		for(var i=0; i < 8; i++){
			var json = {};
			json = this.templates.setStaticLayout(json, i);
			if(!json) continue;
			var nothing = (!adjustToWindowHeight && i == 0);
			formDiv.appendChild(this.buildLayout(json, nothing,i));
		}
		
		var buttonDiv = document.createElement("div");
		buttonDiv.style.textAlign = 'center';
		buttonDiv.style.clear = "both";
		buttonDiv.style.paddingTop = "10px";
		var closeButton = document.createElement("input");
		closeButton.type = 'button';
		closeButton.value = ISA_R.alb_cancel;
		buttonDiv.appendChild(closeButton);
		IS_Event.observe(closeButton, "click", this.hide.bind(this), false, "_adminPanel");
		formDiv.appendChild(buttonDiv);
	},
	buildLayout: function(json, isNothing, i) {
		var self = this;
		var layoutDiv = document.createElement("div");
		layoutDiv.className = "staticLayout";
		layoutDiv.innerHTML = json.layout;
		this.drawOutLine(layoutDiv);
		var layoutMouseOver = function(i) {
			layoutDiv.style.backgroundColor = "#7777cc";
		};
		var layoutMouseOut = function(i) {
			layoutDiv.style.backgroundColor = "";
		};
		var layoutClick = function(e) {
			if(isNothing) json.layout = "";
			self.isaDefaultPanel.changeStaticLayout(json);
			self.hide();
		};
		var eventCancelBubble = function(e) {
			if(window.event){
				window.event.cancelBubble = true;
			}
			if(e && e.stopPropagation){
				e.stopPropagation();
			}
		};
		IS_Event.observe(layoutDiv, 'mouseover', layoutMouseOver.bind(this,i), false, "_adminPanel");
		IS_Event.observe(layoutDiv, 'mouseout', layoutMouseOut.bind(this,i), false, "_adminPanel");
		IS_Event.observe(layoutDiv, 'click', layoutClick.bind(this,i), false, "_adminPanel");
		IS_Event.observe(layoutDiv, 'click', eventCancelBubble, false, "_adminPanel");
		return layoutDiv;
	},
	drawOutLine: function(elem) {
		var childDivs = elem.getElementsByTagName('div');
		for(var i = 0; i < childDivs.length; i++){
			if(!childDivs[i].id) continue;
			childDivs[i].style.border = "dotted 1px #000";
		}
	},
	hide: function() {
		Control.Modal.close();
	}
};

/**
 * Edit HTML
 */
ISA_DefaultPanel.prototype.editHTMLModal = {
	isaDefaultPanel: false,
	layoutHTML: false,
	controlModal: false,
	init: function(layout) {
		this.editHTMLModal.isaDefaultPanel = this;
		this.editHTMLModal.layoutHTML = layout;
		if(!this.editHTMLModal.controlModal){
			this.editHTMLModal.controlModal = new Control.Modal('', {
				className:"adminDefaultPanel",
				afterClose:this.editHTMLModal.hide.bind(this.editHTMLModal)
			});			
		}
		this.editHTMLModal.load();
	},
	load: function() {
		var self = this;
		var viewForm = function() {
			var formDiv = document.createElement("div");
			formDiv.id = "panelStaticHTMLModal";
			self.build(formDiv);
			self.controlModal.container.update(formDiv);
			self.controlModal.open();
		}
		setTimeout(viewForm, 10);
	},
	build: function(formDiv) {
		var self = this;
		
		var messageLabel = document.createElement("div");
		messageLabel.style.clear = "both";
		messageLabel.appendChild(document.createTextNode(ISA_R.alb_editHTMLandOk));
		formDiv.appendChild(messageLabel);
		formDiv.appendChild(document.createElement("br"));
		formDiv.appendChild(this.buildTextArea());
		formDiv.appendChild(document.createElement("br"));
		
		var buttonDiv = document.createElement("div");
		buttonDiv.style.textAlign = 'center';
		var okButton = document.createElement("input");
		okButton.type = 'button';
		okButton.value = ISA_R.alb_ok;
		buttonDiv.appendChild(okButton);
		var okClick = function(e) {
			self.isaDefaultPanel.changeHTMLLayout($("panelEditHTMLTextarea").value);
			self.hide();
		};
		IS_Event.observe(okButton, "click", okClick, false, "_adminPanel");
		
		var closeButton = document.createElement("input");
		closeButton.type = 'button';
		closeButton.value = ISA_R.alb_cancel;
		buttonDiv.appendChild(closeButton);
		IS_Event.observe(closeButton, "click", this.hide.bind(this), false, "_adminPanel");
		formDiv.appendChild(buttonDiv);
	},
	buildTextArea: function() {
		var editHTMLTextareaDiv = document.createElement("div");
		editHTMLTextareaDiv.id = "panelEditHTMLTextareaWrap";
		editHTMLTextareaDiv.style.textAlign = 'center';
		var editHTMLTextarea = document.createElement("textarea");
		editHTMLTextarea.id = "panelEditHTMLTextarea";
		editHTMLTextarea.rows = "20";
		editHTMLTextarea.style.width = "90%";
		editHTMLTextarea.setAttribute('wrap', 'off');
		editHTMLTextarea.value = this.layoutHTML;
		editHTMLTextareaDiv.appendChild(editHTMLTextarea);
		
		return editHTMLTextareaDiv;
	},
	hide: function() {
		Control.Modal.close();
	}
};

/**
 * Adjusting width between column
 */
ISA_DefaultPanel.adjustColumns = {
	columnModal : false,
	start : function(adjustDiv, e){
		ISA_DefaultPanel.adjustColumns.columnModal = this;
		ISA_DefaultPanel.adjustColumns.isDragging = true;
		
		var targetEl1 = adjustDiv.previousSibling;
		var targetEl2 = adjustDiv.nextSibling;
		
		ISA_DefaultPanel.adjustColumns.targetEl1 = targetEl1;
		ISA_DefaultPanel.adjustColumns.targetEl2 = targetEl2;
		ISA_DefaultPanel.adjustColumns.totalWidth = targetEl1.offsetWidth + targetEl2.offsetWidth;
		ISA_DefaultPanel.adjustColumns.targetEl1_offsetWidth = targetEl1.offsetWidth;
		ISA_DefaultPanel.adjustColumns.parentWidth = targetEl1.parentNode.offsetWidth;
		
		ISA_DefaultPanel.adjustColumns.startX = Event.pointerX(e);
		
		Event.observe(document, "mousemove", ISA_DefaultPanel.adjustColumns.move, false);
		Event.observe(document, "mouseup", ISA_DefaultPanel.adjustColumns.end, false);
		
		// Prevent from spreding of event to upper level
		Event.stop(e);
	},
	move : function(e){
		if(ISA_DefaultPanel.adjustColumns.isChanging) return;
		
		// effect
		if(ISA_DefaultPanel.adjustColumns.timer){
			clearTimeout(ISA_DefaultPanel.adjustColumns.timer);
		}
		
		ISA_DefaultPanel.adjustColumns.endX = Event.pointerX(e);
		
		if(!Browser.isIE){
			ISA_DefaultPanel.adjustColumns.changeWidth();
		}else{
			ISA_DefaultPanel.adjustColumns.timer = 
				setTimeout(ISA_DefaultPanel.adjustColumns.changeWidth, 5);
		}
		
		// Prevent from spreding of event to upper level
		Event.stop(e);
	},
	end : function(e){
		if(ISA_DefaultPanel.adjustColumns.timer){
			clearTimeout(ISA_DefaultPanel.adjustColumns.timer);
		}
		Event.stopObserving(document, "mousemove", ISA_DefaultPanel.adjustColumns.move, false);
		Event.stopObserving(document, "mouseup", ISA_DefaultPanel.adjustColumns.end, false);
		
		// Fix to %
		var targetEl1 = ISA_DefaultPanel.adjustColumns.targetEl1;
		var targetEl2 = ISA_DefaultPanel.adjustColumns.targetEl2;
		var parentWidth = ISA_DefaultPanel.adjustColumns.parentWidth;
		
		ISA_DefaultPanel.adjustColumns.isDragging = false;
		
		var p = (targetEl1.offsetWidth * 100)/parentWidth;
		targetEl1.style.width = p + "%";
		
		p = (targetEl2.offsetWidth * 100)/parentWidth;
		targetEl2.style.width = p + "%";
		
		// Replace to columnsArray that is called from
		ISA_DefaultPanel.adjustColumns.columnModal.columnsArray = [];
		var columns = targetEl1.parentNode.childNodes;
		for(var i=0;i<columns.length;i++){
			if(columns[i].className != "column") continue;
			var colWidth = Number(columns[i].style.width.replace(/%/g, "")) + 1;
			ISA_DefaultPanel.adjustColumns.columnModal.columnsArray.push( String(colWidth) + "%" );
		}
	},
	changeWidth : function(e){
		ISA_DefaultPanel.adjustColumns.isChanging = true;
		
		var targetEl1 = ISA_DefaultPanel.adjustColumns.targetEl1;
		var targetEl2 = ISA_DefaultPanel.adjustColumns.targetEl2;
		var totalWidth = ISA_DefaultPanel.adjustColumns.totalWidth;
		var startx = ISA_DefaultPanel.adjustColumns.startX;
		var endx = ISA_DefaultPanel.adjustColumns.endX;
		var startOffsetWidth = ISA_DefaultPanel.adjustColumns.targetEl1_offsetWidth;
		
		var setWidth = (endx - startx);
		if(startOffsetWidth + setWidth < totalWidth-10 && startOffsetWidth + setWidth > 0){
			targetEl1.style.width = (startOffsetWidth + setWidth);
		}else{
			targetEl1.style.width = (startOffsetWidth + setWidth > 0)? (totalWidth-10) : 10;
		}
		
		var setWidth2 = (totalWidth - targetEl1.offsetWidth);
		if(totalWidth - setWidth2 > 0){
			targetEl2.style.width = setWidth2;
		}
		
		ISA_DefaultPanel.adjustColumns.isChanging = false;
	}
};

/**
 * Select column number
 */
ISA_DefaultPanel.prototype.selectColumnModal = {
	isaDefaultPanel: false,
	controlModal: false,
	columnsArray: false,
	editColumnsDiv: false,
	init: function() {
		this.selectColumnModal.isaDefaultPanel = this;
		this.selectColumnModal.columnsArray = this.displayRoleJsons[this.displayRoleId].columnsArray;
		if(!this.selectColumnModal.controlModal){
			this.selectColumnModal.controlModal = new Control.Modal('',{
			  className:"adminDefaultPanel",
			  afterClose:this.selectColumnModal.hide.bind(this.selectColumnModal)
			});			
		}
		this.selectColumnModal.load();
	},
	load: function() {
		var self = this;
		var viewForm = function() {
			var formDiv = document.createElement("div");
			formDiv.id = "panelDynamicColumnModal";
			self.build(formDiv);
			self.controlModal.container.update(formDiv);
			self.controlModal.open();
		}
		setTimeout(viewForm, 10);
	},
	build: function(formDiv) {
		var self = this;
		
		var messageLabel = document.createElement("div");
		messageLabel.style.clear = "both";
		messageLabel.appendChild(document.createTextNode(ISA_R.alb_selectColumnNumber));
		formDiv.appendChild(messageLabel);
		formDiv.appendChild(document.createElement("br"));
		formDiv.appendChild(this.buildSelect());
		formDiv.appendChild(document.createElement("br"));
		formDiv.appendChild(this.buildEditColumn());
		formDiv.appendChild(document.createElement("br"));
		
		var buttonDiv = document.createElement("div");
		buttonDiv.style.textAlign = 'center';
		var okButton = document.createElement("input");
		okButton.type = 'button';
		okButton.value = ISA_R.alb_ok;
		buttonDiv.appendChild(okButton);
		var okClick = function(e) {
			var columnsWidth = "";
			var isFirst = true;
			for(var i = 0; i < self.columnsArray.length; i++) {
				if(!isFirst) columnsWidth += ",";
				columnsWidth += "\"" + self.columnsArray[i] + "\"";
				isFirst = false;
			}
			self.isaDefaultPanel.changeDynamicColumns("[" + columnsWidth + "]");
			self.hide();
		};
		IS_Event.observe(okButton, "click", okClick, false, "_adminPanel");
		
		var closeButton = document.createElement("input");
		closeButton.type = 'button';
		closeButton.value = ISA_R.alb_cancel;
		buttonDiv.appendChild(closeButton);
		IS_Event.observe(closeButton, "click", this.hide.bind(this), false, "_adminPanel");
		formDiv.appendChild(buttonDiv);
	},
	buildSelect: function() {
		var self = this;
		var selectedVal = 3;
		if(this.columnsArray) {
			selectedVal = this.columnsArray.length;
		}
		var selectedDiv = document.createElement("div");
		var selectElement = document.createElement("select");
		for(var i = 1; i <= 10; i++){
			var opt = document.createElement("option");
			opt.value = i;
			opt.innerHTML = i;
			if(i == selectedVal){
				opt.selected = true;
			}
			selectElement.appendChild( opt );
		}
		var changeColumn = function() {
			var colNumber = selectElement[selectElement.selectedIndex].value;
			// A column width is the number of columns devided by 100
			var colWidth = parseInt(100 / colNumber * 10) / 10;
			var colArray = [];
			var colWidthSum = 0;
			for(var i = 1; i <= colNumber; i++) {
				var width = colWidth;
				// Fraction goes to the last column→ Because it must go to 100 if it is added
				// Subtract from 1000 because the width gets ten times, then devided by 10.
				if(i == colNumber) {
					width = 1000 - colWidthSum;
					width /= 10;
				}
				// Decuple and cast to integer to prevent from rounding error.
				colWidthSum += width * 10;
				colArray.push( String(width) + "%" );
			}
			self.columnsArray = colArray;
			self.buildEditColumn();
		};
		selectedDiv.appendChild(document.createTextNode(ISA_R.alb_columnNumber));
		selectedDiv.appendChild(selectElement);
		IS_Event.observe(selectElement, 'change', changeColumn.bind(selectElement), false, "_adminPanel");
		
		return selectedDiv;
	},
	buildEditColumn: function() {
		var colNumber = this.columnsArray.length;
		if(!this.editColumnsDiv) {
			this.editColumnsDiv = document.createElement("div");
			this.editColumnsDiv.style.height = "155px";
			this.editColumnsDiv.style.width = "99%";
			this.editColumnsDiv.style.border = "dotted 1px #000";
		} else {
			while (this.editColumnsDiv.hasChildNodes())
				this.editColumnsDiv.removeChild(this.editColumnsDiv.firstChild);
		}
		
		for(var i = 0; i < colNumber; i++) {
			// Take away 1% of width of adjusting bar and make the width of a column
			var colWidth = Number(this.columnsArray[i].replace(/%/g, "")) - 1;
			var columnDiv = document.createElement("div");
			columnDiv.className = "column";
			columnDiv.style.width = String(colWidth) + "%";
			columnDiv.style.height = "1px";
			this.editColumnsDiv.appendChild(columnDiv);
			if(i + 1 != colNumber) {
				var lineDiv = document.createElement("div");
				lineDiv.className = "adjustBarOver";
				lineDiv.style.cursor = "default";
				this.editColumnsDiv.appendChild(lineDiv);
				//congelation
				//IS_Event.observe(lineDiv, 'mousedown', ISA_DefaultPanel.adjustColumns.start.bind(this, lineDiv), false, "_adminPanel");
			}
		}
		
		return this.editColumnsDiv;
	},
	hide: function() {
		Control.Modal.close();
	}
};


/**
 * Layout template
 */
ISA_DefaultPanel.prototype.templates = {
	layouts:{},
	/**
		Set fixed area
	*/
	setStaticLayout:function(jsonObject, number){
		var html = this.getStaticLayout(number);
		if(!html) return null;

		var datetime = new Date().getTime();
		var idPrefix = "p_" + datetime + "_w_";
		//Give id attribute to HTML
		//TODO Is class="column" suite for Widget?
		var regexp = new RegExp("class=\"static_column\"");
		if(Browser.isIE){
			regexp = new RegExp("class=static_column");
		}
		var newhtml = "";
		var s = html;
		var cnt = 0;
		for(cnt=0;s.match(regexp);cnt++){
			newhtml += RegExp.leftContext;
			newhtml += RegExp.lastMatch;
			newhtml += " id=\"" + idPrefix + cnt + "\"";
			s = RegExp.rightContext;
		}
		newhtml += RegExp.rightContext;
		if(!newhtml) newhtml = html;
		
		// Replace to json
		jsonObject.layout = newhtml;
		
		var xmlJson = {};
		for(var i = 0; i < cnt; i++) {
			var id = idPrefix + i;
			var json = this.getDefaultWidgetJson();
			json.id = id;
			xmlJson[id] = json;
		}
		
		// Replace to json
		jsonObject.staticPanel = xmlJson;
		
		return jsonObject;
	},
	getStaticLayout: function(number){
		if(this.layouts[number]) return this.layouts[number];
		
		var defaultPanel = ISA_DefaultPanel.defaultPanel;
		var targetClass = (defaultPanel.displayRoleJsons[defaultPanel.displayRoleId] && defaultPanel.displayRoleJsons[defaultPanel.displayRoleId].adjustToWindowHeight)
			? 'staticLayoutAdjustHeight' : 'staticLayout';
		$jq("#select_layout_modal ." + targetClass).each(function(idx, element){
//			this.layouts[idx] = $jq(element).html();
			element = $jq(element);
			
			var template = (ISA_Admin.trim(element.html()).length > 0)? $jq.parseHTML(element.html()) : element;
			var layoutTemplate = $jq(template).closest(".template");
			this.layouts[idx] = (layoutTemplate.length > 0) ? layoutTemplate.html() : element.html();
			if(element.attr("id") == "staticLayout_tpl_default"){
				this.staticLayoutDefaultIdx = idx;
			}
		}.bind(this));
		
		return this.layouts[number];
	},
	// Set for default fixed area
	setStaticLayout0: function(jsonObject, number){
		this.getStaticLayout();
		
		return this.setStaticLayout(jsonObject, (number ? number : this.staticLayoutDefaultIdx));
	},
	/**
		Set fixed area of command bar
	*/
	setCommandLayout: function(jsonObject) {
		var datetime = new Date().getTime();
		var html = "";
		html += '<table cellpadding="0" cellspacing="0" width="100%">\n';
		html += '  <tr>\n';
		html += '    <td width="100%"><div id="p_1_w_4" class="commandbar-item" outside="true"></div></td>\n';
		html += '    <td><div id="p_1_w_6" class="commandbar-item"></div></td>\n';
		html += '    <td><div id="disabled_portal-change-fontsize" disabledCommand="true" class="commandbar-item"><!--&lt;div id="portal-change-fontsize"&gt;&lt;/div&gt;--></div></td>\n';
		html += '    <td><div id="portal-trash" class="commandbar-item"></div></td>\n';
		html += '    <td><div id="portal-preference" class="commandbar-item"><div class="allPreference"></div></div></td>\n';
		html += '    <td><div id="disabled_portal-credential-list" disabledCommand="true" class="commandbar-item"><!--&lt;div id="portal-credential-list"&gt;&lt;/div&gt;--></div></td>\n';
		html += '    <td><div id="portal-admin-link" class="commandbar-item"></div></td>\n';
		html += '    <td><div id="portal-logout" class="commandbar-item"></div></td>\n';
		html += '    <td><div id="portal-searchform" class="commandbar-item" outside="true"/></td>\n';
		html += '  </tr>\n';
		html += '</table>\n';

		// Replace to json
		jsonObject.layout = html;
		
		var xmlJson = {};
		xmlJson["p_1_w_4"] = {id:"p_1_w_4",href:"",title:"Ticker",type:"Ticker",properties:{url:"http://www.infoscoop.org/index.php/en/news.feed"}};
		xmlJson["p_1_w_6"] = {id:"p_1_w_6",href:"",title:ISA_R.alb_ranking,type:"Ranking",properties:{urls:"<urls><url title='"+ISA_R.alb_keyWordSearchRanking+"' url='http://localhost:8080/infoscoop/kwdsrv?baseDate=TODAY&amp;period=30&amp;rankingNum=20'/></urls>"}};
		
		// Replace to json
		jsonObject.staticPanel = xmlJson;
		
		return jsonObject;
	},
	getDefaultWidgetJson: function() {
		return {id:"",href:"",title:"",type:"",properties:{}};
	}
};
