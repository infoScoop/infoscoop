var ISA_InstantEdit = IS_Class.create();

ISA_InstantEdit.prototype.classDef = function() {
	var self = this;
	
	this.initialize = function(_actual, _service,_command, _args, _maxlength, _validations, isInitOnly) {
		this.changing = false;
		this.actual = _actual;
		this.service = _service;
		this.command = _command;
		this.args = _args;
		this.maxlength = _maxlength;
		this.validations = _validations;
		// Finish here if processing only initialze
		// Event is not registered
		if(isInitOnly) return;
		// Obtain child node type 
		this.childNodetype = this.actual.firstChild.nodeType;
		this.childNodename = this.actual.firstChild.nodeName.toLowerCase();
		// Condition is that there is TEXT_NODE and ELEMENT_NODE uder Div
		if(this.childNodetype == 3) {
			this.actual.title = ISA_R.alb_editByClick;
			this.actual.style.cursor = "pointer";
			Event.observe(this.actual, 'click', this.clickText.bind(this), false);
		} else {
		}
	};
};

ISA_InstantEdit.prototype.commitValue = function(value) {
	
	value = ISA_Admin.trim(value);
	if(this.validations){
		var error = IS_Validator.validate(value, this.validations);
		if(error) {
			ISA_InstantEdit.isShowingAlert = true;
			alert(error);
			ISA_InstantEdit.isShowingAlert = false;
			if(!Browser.isIE) {
				this.fieldBlur(value);
			}
			this.clickText();
			return;
		}
	}
	
	var url = adminHostPrefix + "/services/" + this.service+"/"+this.command;
	var args = [value];
	if( this.args && this.args instanceof Function ) {
		args = this.args( value );
	} else if( this.args && this.args instanceof Array ) {
		args = Object.clone( this.args ).push( value );
	}
	
	var opt = {
		method: 'post' ,
		contentType: "application/json",
		postBody: Object.toJSON(args),
		asynchronous:true,
		onSuccess: function(response){
		},
		onFailure: function(t) {
			alert(ISA_R.ams_failedSavingValueEdit);
			msg.error(ISA_R.ams_failedSavingValueEdit + t.status + " - " + t.statusText);
		},
		onException: function(r, t){
			alert(ISA_R.ams_failedSavingValueEdit);
			msg.error(ISA_R.ams_failedSavingValueEdit + getErrorMessage(t));
		}
	};
	AjaxRequest.invoke(url, opt);
	// If TEXT_NODE
	if(this.childNodetype == 3) {
		this.fieldBlur(value);
	}
	return false;
};

//edit field created
ISA_InstantEdit.prototype.clickText = function() {
	if(ISA_InstantEdit.isShowingAlert) return;
	var self = this;
	
	if(!this.changing){
		var width = this.widthEl();
		var height =this.heightEl() + 2;
		
		var input = document.createElement("input");
		input.id = this.actual.id + "_field";
		input.type = "text";
//		input.value = ISA_Admin.trim(this.actual.innerHTML.replace(/"/g, "&quot;"));
		input.value = ISA_Admin.trim(this.actual.firstChild? this.actual.firstChild.nodeValue : "");
		input.style.width = width + "px";
		input.style.height = height + "px";
		if(!this.maxlength)
			input.maxLength = "256";
		else
			input.maxLength = this.maxlength;
		input.autoComplete = "off";
		
		var inputOnFocus = function(e) {
			self.highLight(input);
		};
		var inputOnBlur = function(e) {
			self.noLight(input);
			self.commitValue(input.value);
		};
		var inputOnKeypress = function(e) {
			if(e.keyCode == 13)input.blur();
		};
		Event.observe(input, 'focus', inputOnFocus, false);
		Event.observe(input, 'blur', inputOnBlur, false);
		Event.observe(input, 'keypress', inputOnKeypress, false);
		
		var len = this.actual.childNodes.length;
		for(var i = 0; i < len; i++) {
			this.actual.removeChild(this.actual.lastChild);
		}
		this.actual.appendChild(input);
		this.changing = true;
	}
	this.actual.firstChild.focus();
};

ISA_InstantEdit.prototype.fieldBlur = function(commitvalue) {
	this.actual.innerHTML = (commitvalue != "") ? escapeHTMLEntity(String(commitvalue)) : "　";
	this.actual.style.padding = "0";
	this.changing = false;
};

//get width of text element
ISA_InstantEdit.prototype.widthEl = function() {
	var w;
	if (document.layers){
		w=document.layers[this.actual.id].clip.width;
	} else if (document.all && !document.getElementById){
		w=document.all[this.actual.id].offsetWidth;
	} else if(document.getElementById){
		w=document.getElementById(this.actual.id).offsetWidth;
	}
	return w;
};

//get height of text element
ISA_InstantEdit.prototype.heightEl = function() {
	var h;
	if (document.layers){
		h=document.layers[this.actual.id].clip.height;
	} else if (document.all && !document.getElementById){
		h=document.all[this.actual.id].offsetHeight;
	} else if(document.getElementById){
		h=document.getElementById(this.actual.id).offsetHeight;
	}
	return h;
};

ISA_InstantEdit.prototype.highLight = function(span) {
	span.parentNode.style.border = "2px solid #d1fdcd";
	span.parentNode.style.padding = "0";
	span.style.border = "1px solid #54ce43";
};

ISA_InstantEdit.prototype.noLight = function(span) {
	span.parentNode.style.border = "0px";
	span.parentNode.style.padding = "2px";
	span.style.border = "0px";
};
