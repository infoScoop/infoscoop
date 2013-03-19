// -------------------------------------------------------------------------------------------
// For creating preview widget
// -------------------------------------------------------------------------------------------
IS_Portal.widgetsContainer = {};
IS_Portal.widgetsContainer.widget = {};
IS_Portal.widgetsContainer.widget.RssReader = {};
IS_Portal.widgetsContainer.widgetConfiguration = {};
IS_Portal.SearchEngines = {};
IS_WidgetConfiguration = [];
IS_Portal.subWidgetMap = {};
IS_Portal.fontSize = "";
IS_Droppables = new Object();
IS_Droppables.add = function(a,b){};
IS_Droppables.replaceLocation = function(a, b, c){};
IS_Droppables.findWizPos = function(a){};
IS_Request.CommandQueue = new Object();
IS_Request.CommandQueue.addCommand = function(a){};
IS_Request.LogCommandQueue = new Object();
IS_Request.LogCommandQueue.addCommand = function(a){};
IS_Portal.imageController = new Object();
IS_Portal.imageController.element = new Object();
IS_Portal.widgetDisplayUpdated = function(){};
IS_Portal.currentTabId = "tab0";
IS_Portal.tabList = [{id:IS_Portal.currentTabId}];
IS_Portal.tabs = new Object();
IS_Portal.tabs[IS_Portal.currentTabId] = {id:IS_Portal.currentTabId};
IS_Portal.widgetLists = new Object();
IS_Portal.widgetLists[IS_Portal.currentTabId] = new Object();
IS_Portal.getTrueId = function( id ){
	if(!id.replace)return;
	return id.replace(/^tab[0-9]+_/, "");
}
IS_Portal.getWidget = function(widgetId) {
	return IS_Portal.widgetLists[IS_Portal.currentTabId][widgetId];
}
IS_Portal.lang = "ja";
var IS_DroppableOptions = {};

var freshDays = 1;

var adminHostPrefix = hostPrefix + "/admin";
var portalSSLURL = hostPrefix;
//var searchEngineURL = hostPrefix + "/schsrv";
var proxyServerURL = hostPrefix + "/proxy";
var useProxy = (typeof useProxy == "undefined") ? true : useProxy;

// Default value settings of private property
var useTab = is_getPropertyBoolean(useTab, true);

//Default value settings of public property
var commandQueueWait = is_getPropertyInt(commandQueueWait, 30);
var logCommandQueueWait = is_getPropertyInt(logCommandQueueWait, 3600);
var freshDays = is_getPropertyInt(freshDays, 1);
var refreshInterval = is_getPropertyInt(refreshInterval, 10);
var widgetRefreshInterval = is_getPropertyInt(widgetRefreshInterval, 20);
var subWidgetRefreshInterval = is_getPropertyInt(subWidgetRefreshInterval, 5);
var messagePriority = is_getPropertyInt(messagePriority, 1);
var ajaxRequestTimeout = is_getPropertyInt(ajaxRequestTimeout, 15000);
var ajaxRequestRetryCount = is_getPropertyInt(ajaxRequestRetryCount,2);
var displayInlineHost = is_getPropertyString(displayInlineHost, "");

if(/^\.\/(.+)$/.test( gadgetProxyURL ) )
	gadgetProxyURL = hostPrefix +"/" +RegExp.$1;

var sideMenuTabs = (typeof sideMenuTabs == "undefined") ? ["siteMap","addContent"] : sideMenuTabs;

IS_Portal.rssSearchBoxList = new Object();

// -------------------------------------------------------------------------------------------
// Common
// -------------------------------------------------------------------------------------------
var ISA_Admin = {};
ISA_Admin.requestComplete = true;

/**
 * Clearing evenr of object that is refered from IS_Event and dragdrop.js
 * Delete treemenuNavigator as well
 * Delete temp of menu
 */
ISA_Admin.clearAdminCache = function() {
	IS_Event.unloadCache("_adminMenu");
	IS_Event.unloadCache("_adminSearch");
	IS_Event.unloadCache("_adminWidgetConf");
	IS_Event.unloadCache("_adminWidgetConf_gadget");
	IS_Event.unloadCache("_adminProperties");
	IS_Event.unloadCache("_adminProxy");
	IS_Event.unloadCache("_adminI18N");
	//IS_Event.unloadCache("_adminPanel");
	//IS_Event.unloadCache("_adminPanelTab");
	IS_Event.unloadCache("_adminPortal");
	IS_Event.unloadCache("_adminAdmins");
	IS_Event.unloadCache("_adminAuthentication");
	IS_Event.unloadCache("_adminAuthenticationCert");
	for(var i=0;i<ISA_DragDrop.draggableList.length;i++){
		ISA_DragDrop.draggableList[i].destroy();
	}
	ISA_DragDrop.draggableList = new Array();
	
	var loopCount = Droppables.drops.length;
	for(var i=0;i<loopCount;i++){
		if(Droppables.drops[0])
			Droppables.remove(Droppables.drops[0].element);
	}
	
	loopCount = Sortable.sortables.length;
	for(var i=0;i<loopCount;i++){
		Sortable.destroy(Sortable.sortables[0].element);
	}
	var navigatorContainer = $("admin-menu-navigator");
	loopCount = navigatorContainer.childNodes.length;
	for(var i=0; i<loopCount; i++) {
		navigatorContainer.removeChild(navigatorContainer.lastChild);
	}
	
	if(ISA_SiteAggregationMenu && ISA_SiteAggregationMenu.treeMenu){
		ISA_SiteAggregationMenu.removeTemp();
	}
	IS_EventDispatcher.newEvent("clearAdminCache","");
}

var windowBeforeUnload = function(e) {
	IS_EventDispatcher.newEvent("deleteTemp","",true);
	if(ISA_WidgetConf.widgetConf.uploadData){
		ISA_WidgetConf.widgetConf.requestDeleteGadget(ISA_WidgetConf.widgetConf.uploadData.id);
	}
};

Event.observe(window, 'beforeunload',  windowBeforeUnload );
Event.observe(window, 'unload', function(){
	ISA_Admin.clearAdminCache();
	IS_Event.unloadAllCache();
});

ISA_loadProperties = function(_callback){
	
	var container = document.getElementById("properties");
	var url = adminHostPrefix + "/services/properties/getPropertiesJson";
	var opt = {
	  method: 'get' ,
	  asynchronous:true,
	  onSuccess: function(response){
	  	eval("ISA_Properties.setProperties("+response.responseText+");");
	  	_callback();
	  },
	  on404: function(t) {
		  if(!container.firstChild) container.appendChild(document.createElement("div"));
		  container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>" + ISA_R.ms_propertyNotFound + "</span>";
		  msg.error(IS_R.ms_propertyNotFound + " " + t.status + " - " + t.statusText);
		  
	  },
	  onFailure: function(t) {
		  if(!container.firstChild) container.appendChild(document.createElement("div"));
		  container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>" + IS_R.ms_propertyLoadonFailure + "</span>";
		  msg.error(IS_R.ms_propertyLoadonFailure + " " + t.status + " - " + t.statusText);

	  },
	  onException: function(r, t){
		  if(!container.firstChild) container.appendChild(document.createElement("div"));
		  container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>" + IS_R.ms_propertyLoadonFailure + "</span>";
		  msg.error(IS_R.ms_propertyLoadonFailure + " " + getErrorMessage(t));

	  },
	  onComplete: function(req, obj){
		  ISA_Admin.requestComplete = true;
	  },
	  onRequest: function() {
		  ISA_Admin.requestComplete = false;
	  }
	};
	AjaxRequest.invoke(url, opt);
}

ISA_Admin.createIconButton = function(text, title, imgName, floatValue) {
	//var div = document.createElement("div");
	var button = document.createElement("a");
	button.className = "iconButton";
	//div.type = "button";
	if(floatValue) {
		if(Browser.isIE)
			button.style.styleFloat = floatValue;
		else
			button.style.cssFloat = floatValue;
	}
	button.title = title;
	var img = document.createElement("img");
	img.src = imageURL + imgName;
	img.style.position = "relative";
	img.style.top = "2px";
	img.style.margin = "0 5 0 0";
//	var anc = document.createElement("a");
//	anc.appendChild(document.createTextNode(text));
//	anc.className = "button";
//	anc.href = "#";
	button.href = "#";
	button.appendChild(img);
	button.appendChild(document.createTextNode(text));
//	button.appendChild(anc);
	return button;
};

ISA_Admin.createBaseRadio = function(name, isChecked, isDisabled, d) {
	var doc = d ? d : document;
	
	var radio = doc.createElement("input");
	radio.type = "radio";
	radio.name = name;
	if(isChecked)
		radio.checked = String(isChecked);
	if(isDisabled)
		radio.disabled = String(isDisabled);
	/*	fix #13565
	if(Browser.isIE) {
		var inputElement = "";
		inputElement += "<";
		inputElement += "input type='radio' name='" + name + "'";
		if(isChecked)
			inputElement += " checked";
		if(isDisabled)
			inputElement += " disabled";
		inputElement += ">";
		radio = doc.createElement(inputElement);
	}
	*/
	return radio;
};

ISA_Admin.createBaseCheckBox = function(name, isChecked, isDisabled, d) {
	var doc = d ? d : document;
	
	var checkbox = doc.createElement("input");
	checkbox.type = "checkbox";
	checkbox.name = name;
	if(isChecked)
		checkbox.checked = String(isChecked);
	if(isDisabled)
		checkbox.disabled = String(isDisabled);
	
	/*	fix #13565
	if(Browser.isIE) {
		var inputElement = "";
		inputElement += "<";
		inputElement += "input type='checkbox' name='" + name + "'";
		if(isChecked)
			inputElement += " checked";
		if(isDisabled)
			inputElement += " disabled";
		inputElement += ">";
		checkbox = doc.createElement(inputElement);
	}
	*/
	return checkbox;
};

/**
 * [{name:"Display", callback:function(){}, selected:true}, {name:...}]
 **/
ISA_Admin.SelectLabel = function(renderTo, options, d) {
	var doc = d ? d : document;
	
	options = $jq(options);
	renderTo = $jq(renderTo).addClass("SelectLabel");
	var div = $jq("<div/>").appendTo(renderTo);
	
	options.each(function(idx, option){
		var label = $jq("<span/>").text(option.name);
		label.addClass((option.selected)? "selected" : "label");
		
		label.click({callback:option.callback}, function(event){
			var self = $jq(this);
			if(self.hasClass("selected"))
				return;
			
			event.data.callback(self);
			
			$jq("span", self.parent()).each(function(idx, element){
				$jq(element).removeClass("selected").addClass("label");
			});
			
			self.removeClass("label").removeClass("hover");
			self.addClass("selected");
		});
		label.mouseover(function(){
			if($jq(this).hasClass("label"))
				$jq(this).addClass("hover");
		});
		label.mouseout(function(){
			if($jq(this).hasClass("hover"))
				$jq(this).removeClass("hover");
		});
		div.append(label).append("&nbsp;&nbsp;");
	})
	
	return div;
};


ISA_Admin.trim = function (val) {
	return String(val).replace(/^[ 　]*/gim, "").replace(/[ 　]*$/gim, "");
};

ISA_Admin.replaceUndefinedValue = function (val) {
	var value = val;
	return ((value != null) && (typeof value != 'undefined') ? value : "");
};

ISA_Admin.langs = {
	"ALL":"ALL(" + IS_R.lb_default + ")",
	"da":"da(" + IS_R.lb_langDA + ")",
	"de":"de(" + IS_R.lb_langDE + ")",
	"en":"en(" + IS_R.lb_langEN + ")",
	"es":"es(" + IS_R.lb_langES + ")",
	"fi":"fi(" + IS_R.lb_langFI + ")",
	"fr":"fr(" + IS_R.lb_langFR + ")",
	"it":"it(" + IS_R.lb_langIT + ")",
	"ja":"ja(" + IS_R.lb_langJA + ")",
	"ko":"ko(" + IS_R.lb_langKO + ")",
	"nl":"nl(" + IS_R.lb_langNL + ")",
	"no":"no(" + IS_R.lb_langNO + ")",
	"pt-BR":"pt-BR(" + IS_R.lb_langPtBR + ")",
	"sv":"sv(" + IS_R.lb_langSV + ")",
	"ru":"ru(" + IS_R.lb_langRU + ")",
	"zh":"zh(" + IS_R.lb_langZH + ")"
};
ISA_Admin.countries = {
	"ALL":"ALL(" + IS_R.lb_default + ")",
	"AU":"AU(" + IS_R.lb_countryAU + ")",
	"BR":"BR(" + IS_R.lb_countryBR + ")",
	"CA":"CA(" + IS_R.lb_countryCA + ")",
	"CH":"CH(" + IS_R.lb_countryCH + ")",
	"CN":"CN(" + IS_R.lb_countryCN + ")",
	"DE":"DE(" + IS_R.lb_countryDE + ")",
	"DK":"DK(" + IS_R.lb_countryDK + ")",
	"ES":"ES(" + IS_R.lb_countryES + ")",
	"FI":"FI(" + IS_R.lb_countryFI + ")",
	"FR":"FR(" + IS_R.lb_countryFR + ")",
	"IE":"IE(" + IS_R.lb_countryIE + ")",
	"IN":"IN(" + IS_R.lb_countryIN + ")",
	"IT":"IT(" + IS_R.lb_countryIT + ")",
	"JP":"JP(" + IS_R.lb_countryJP + ")",
	"KR":"KR(" + IS_R.lb_countryKR + ")",
	"MX":"MX(" + IS_R.lb_countryMX + ")",
	"NL":"NL(" + IS_R.lb_countryNL + ")",
	"NO":"NO(" + IS_R.lb_countryNO + ")",
	"NZ":"NZ(" + IS_R.lb_countryNZ + ")",
	"RU":"RU(" + IS_R.lb_countryRU + ")",
	"SE":"SE(" + IS_R.lb_countrySE + ")",
	"UK":"UK(" + IS_R.lb_countryUK + ")",
	"US":"US(" + IS_R.lb_countryUS + ")"
};

ISA_Admin.initIndicator = function() {
	var indicatorDiv = document.createElement("div");
	document.body.appendChild(indicatorDiv);
	ISA_Admin.indicatorDiv = indicatorDiv;
	
	var overlay = document.createElement("div");
	overlay.className = "indicatorOverlay";
	overlay.id = "drag-overlay";
	indicatorDiv.appendChild(overlay);
	ISA_Admin.overlay = overlay;
	
	LoadingDiv = document.createElement("div");
	LoadingDiv.id = "divOverlay";
	LoadingDiv.className = "nowLoading";
	indicatorDiv.appendChild(LoadingDiv);
	
	LoadingDiv.style.top = findPosY(document.body) + 200;
	LoadingDiv.style.left = findPosX(document.body) + document.body.offsetWidth/2 - divOverlay.offsetWidth/2;

}

ISA_Admin.startIndicator = function() {
	if(!ISA_Admin.indicatorDiv)
		ISA_Admin.initIndicator();
//	var overlay = ISA_Admin.indicatorDiv;
	var overlay = ISA_Admin.overlay;
	
	overlay.style.width = Math.max(document.body.scrollWidth, document.body.clientWidth);
	overlay.style.height = Math.max(document.body.scrollHeight, document.body.clientHeight);
//	overlay.style.display = "";
	ISA_Admin.indicatorDiv.style.display = "";
}

ISA_Admin.stopIndicator = function() {
	if(!ISA_Admin.indicatorDiv)
		ISA_Admin.initIndicator();
	var indicatorDiv = ISA_Admin.indicatorDiv;
	indicatorDiv.style.display = "none";
}


/**
 * Create common table header of administration page
 */
ISA_Admin.buildTableHeader = function(labels, widths){
	
	var configTable = document.createElement("table");
	configTable.className ="configTableHeader";
	configTable.width = "900px";
	configTable.cellSpacing = "0";
	configTable.cellPadding = "0";
	var configTbody = document.createElement("tbody");
	configTable.appendChild(configTbody);
	
	var configTr;
	configTr = document.createElement("tr");
//	configTr.id = "proxyConfigHeader";
	configTbody.appendChild(configTr);

	var configTh;
	var configTd;
	for(var i = 0; i < labels.length; i++){
		configTh = document.createElement("td");
		configTh.className = "configTableHeaderTd";
//		configTh.style.whiteSpace = "nowrap";
		if(widths && widths[i])
		  configTh.style.width = widths[i];
		configTh.appendChild(document.createTextNode(labels[i]));
		configTr.appendChild(configTh);
	}
	return configTable;
}


ISA_Admin.buildWidgetTypeIconDiv = function(type){
	var typeIcon = document.createElement("div");
	typeIcon.style.height = "16px";
	typeIcon.style.width = "16px";
	IS_Widget.setIcon(typeIcon, type);
	return typeIcon;
}

/**
 * Generate right/left aligned table for dailog box
 */
var ISA_RightLeftAlignTable = IS_Class.create();
ISA_RightLeftAlignTable.prototype.classDef = function() {

	var configTable;
	var configTbody;
	this.initialize = function(){
		configTable = document.createElement("table");
		configTable.cellSpacing = "5";
		configTable.cellPadding = "1";
		configTable.style.fontSize = '12px';
		configTbody = document.createElement("tbody");
		configTable.appendChild(configTbody);
		
	}


	this.addRow = function(label, inputDiv){
		
		var configTr = document.createElement("tr");

		var leftTd = document.createElement("td");
		leftTd.style.textAlign = 'right';
		leftTd.style.fontWeight = 'bold';
		leftTd.style.backgroundColor = "#eeeeee";
		leftTd.appendChild(document.createTextNode(label + ": "));
		configTr.appendChild(leftTd);

		var rightTd = document.createElement("td");
		rightTd.style.textAlign = 'left';
		rightTd.appendChild(inputDiv);
		configTr.appendChild(rightTd);
		
		configTbody.appendChild(configTr);
	}

	this.build  = function(){
		return configTable;
	}
}

var ISA_Principals = {
  principalObjs : false,
	
  get: function(){
	  if(this.principalObjs) return this.principalObjs;
	var url = adminHostPrefix + "/role.jsp";
	var opt = {
	  method: 'get' ,
	  asynchronous:false,
	  onSuccess: function(response){
		  this.principalObjs = eval(response.responseText);
	  }.bind(this),
	  onFailure: function(t) {
		  alert(ISA_R.ams_failedToGetRole);
		  msg.error(ISA_R.ams_failedToGetRole + t.status + " - " + t.statusText);
	  },
	  onException: function(r, t){
		  alert(ISA_R.ams_failedToGetRole);
		  msg.error(ISA_R.ams_failedToGetRole + getErrorMessage(t));
	  }
	};
	  AjaxRequest.invoke(url, opt);
	  return this.principalObjs;
  }
}

/**
 * Preview form
 */
ISA_previewFormModal = {
	controlModal: false,
	principalDefault: "OrganizationPrincipal",
	init: function() {
		ISA_previewFormModal.controlModal = new Control.Modal(
			false,
			{
				contents: "&nbsp;",
				opacity: 0.2,
				containerClassName:"",
				afterClose:ISA_previewFormModal.hide
			}
		);
		ISA_previewFormModal.load();
	},
	load: function() {
		var self = ISA_previewFormModal;
		var viewForm = function() {
			var formDiv = document.createElement("div");
			formDiv.id = "panelPreviewFormModal";
			self.controlModal.open();
			self.build(formDiv);
			self.controlModal.update(formDiv);
		}
		setTimeout(viewForm, 10);
	},
	build: function(formDiv) {
		var self = ISA_previewFormModal;
		var closeDiv = document.createElement("div");
		closeDiv.style.clear = "both";
		closeDiv.style.textAlign = "right";
		var closeA = document.createElement("a");
		closeA.innerHTML = ISA_R.alb_close;
		closeA.style.cursor = "pointer";
		closeDiv.appendChild(closeA);
		IS_Event.observe(closeA, "click", this.hide.bind(this), false, "_adminPanel");
		formDiv.appendChild(closeDiv);
		
		var messageLabel = document.createElement("div");
		messageLabel.style.clear = "both";
		messageLabel.style.marginBottom = "5px";
		messageLabel.appendChild(document.createTextNode(ISA_R.alb_selectPermissionLevel));
		formDiv.appendChild(document.createElement("br"));
		formDiv.appendChild(messageLabel);
		formDiv.appendChild(this.buildInputArea());
		formDiv.appendChild(document.createElement("br"));
		
		var previewDiv = document.createElement("div");
		previewDiv.style.clear = "both";
		previewDiv.style.textAlign = "right";
		var previewA = document.createElement("a");
		previewA.innerHTML = ISA_R.alb_preview;
		previewA.style.cursor = "pointer";
		previewDiv.appendChild(previewA);
		var previewClick = function(e) {
			
			var isFirst = true;
			var url = hostPrefix + "/adminpreview";
			var principalValues = document.getElementsByClassName("panelPrincipalValue", $("panelPreviewFormInputWrap"));
			var principalNames = document.getElementsByClassName("panelPrincipalName", $("panelPreviewFormInputWrap"));
			for(var i = 0, n = principalValues.length; i < n; i++) {
				if(!isFirst) {
					url += "&";
				} else {
					url += "?";
				}
				url += ISA_Admin.trim(principalNames[i].value) + "=" + encodeURIComponent(ISA_Admin.trim(principalValues[i].value));
				isFirst = false;
			}
			window.open(url);
//			self.hide();
		};
		IS_Event.observe(previewA, "click", previewClick, false, "_adminPanel");
		formDiv.appendChild(previewDiv);
	},
	buildInputArea: function() {
		var self = this;
		var editAreaDiv = document.createElement("div");
		editAreaDiv.id = "panelPreviewFormInputWrap";
		
		var addPrincipalDiv = ISA_Admin.createIconButton(ISA_R.alb_add, ISA_R.alb_add, "add.gif", "");
		var addPrincipalHandler = function(e) {
			editAreaDiv.appendChild(self.buildInput());
		};
		IS_Event.observe(addPrincipalDiv, 'click', addPrincipalHandler, false, "_adminPanel");
		editAreaDiv.appendChild(addPrincipalDiv);
		
		editAreaDiv.appendChild(this.buildInput());
		
		return editAreaDiv;
	},
	buildInput: function() {
		var editDiv = document.createElement("div");
		editDiv.style.marginTop = "8px";
		var inputDiv = document.createElement("div");
		inputDiv.style.marginTop = "5px";
		var selectPrincipal = document.createElement("select");
		selectPrincipal.className = "panelPrincipalName";
		var principalMap = ISA_Principals.get();
		for(var i = 0; i < principalMap.length; i++) {
			var opt = document.createElement("option");
			opt.value = principalMap[i].type;
			opt.innerHTML = principalMap[i].displayName;
			if(principalMap[i].type == this.principalDefault) {
				opt.selected = true;
			}
			selectPrincipal.appendChild( opt );
		}
		inputDiv.appendChild(selectPrincipal);
		// 
		var editRoleInput = document.createElement("input");
		editRoleInput.className = "panelPrincipalValue";
		editRoleInput.type = "text";
		editRoleInput.size = 30;
		inputDiv.appendChild(editRoleInput);
		editDiv.appendChild(inputDiv);
		return editDiv;
	},
	hide: function() {
		Control.Modal.close();
	}
};

/**
 * Adding CommandBarModal
 */
ISA_AddCommandBarModal = {
	isaDefaultPanel: false,
	controlModal: false,
	init: function() {
		this.addCommandBarModal.isaDefaultPanel = this;
		this.addCommandBarModal.controlModal = new Control.Modal(
			false,
			{
				contents: "&nbsp;",
				opacity: 0.2,
				containerClassName:"",
				afterClose:this.addCommandBarModal.hide.bind(this.addCommandBarModal)
			}
		);
		this.addCommandBarModal.load();
	},
	load: function() {
		var self = this;
		var viewForm = function() {
			var formDiv = document.createElement("div");
			formDiv.id = "panelCommandBarModal";
			self.controlModal.open();
			self.build(formDiv);
			self.controlModal.update(formDiv);
		}
		setTimeout(viewForm, 10);
	},
	build: function(formDiv) {
		var self = this;
		
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

ISA_Admin.checkUpdated = function() {
	if(this.isUpdated) {
		if( !confirm(ISA_R.ams_confirmChangeLost) ) {
			return false;
		}
	}
	IS_EventDispatcher.newEvent("deleteTemp","");
	this.isUpdated = false;
	return true;
};