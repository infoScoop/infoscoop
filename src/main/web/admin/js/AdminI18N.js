var ISA_I18N = IS_Class.create();
ISA_I18N.idMap = {};//Map for cheching id duplication
ISA_I18N.i18nMsgs = null;//Message obatained from DB is added. Not updated later
ISA_I18N.i18nLocales = null;//List of local currently used in DB. Update if any changes occurerred in client
ISA_I18N.types = {
	"menu" : ISA_R.alb_menu,
	"search" : ISA_R.alb_searchForm,
	"widget" : ISA_R.alb_widget,
	"layout" : ISA_R.alb_otherLayout
};

var queryString = document.location.search;
queryString = queryString.replace(/^\?/, "").split("&");

if(queryString.contains("i18nJS=true"))
	ISA_I18N.types.js = "JavaScript";

ISA_I18N.defaultLocales = [{country:"ALL",lang:"ALL"}];

ISA_I18N.isDefaultLocale = function(country, lang){
	for(var i=0;i<ISA_I18N.defaultLocales.length;i++){
		if(ISA_I18N.defaultLocales[i].country == country && ISA_I18N.defaultLocales[i].lang == lang)
			return true;
	}
	return false;
}

ISA_I18N.prototype.classDef = function() {
	var self = this;
	var container;
	var loadingMessage;
	var i18nDiv;
	var i18nBody;
	
	// var loadingModal = new Control.Modal('',{
	// 		className:"commitDialog",
	// 		closeOnClick:false
	// 	});
	// loadinModal.container.update("Loading...");
	
	this.initialize = function() {
		container = document.getElementById("i18n");
		
		var len = container.childNodes.length;
		for(var i = 0; i < len; i++) {
			container.removeChild(container.lastChild);
		}
		
		loadingMessage = document.createElement('div');
		loadingMessage.innerHTML = "Loading...";
		loadingMessage.style.clear = "both";
		loadingMessage.style.cssFloat = "left";
		container.appendChild(loadingMessage);
		
		this.localeModal = new Control.Modal('');
	};
	
	this.display = function() {
		i18nBody = document.createElement("div");
		i18nBody.style.clear = "both";
		
//		var titleDiv = document.createElement("div");
//		titleDiv.id = "i18nTitle";
//		titleDiv.className = "i18nTitle";
//		titleDiv.appendChild(document.createTextNode(ISA_R.alb_i18n));
//		i18nBody.appendChild(titleDiv);
		
		var i18nField = document.createElement("div");
		i18nField.className = "i18nCategoryField";
		i18nBody.appendChild( i18nField );
		
		var messageTitielDiv = document.createElement("p");
		messageTitielDiv.className = "i18nCategoryTitle";
		messageTitielDiv.appendChild( document.createTextNode(ISA_R.alb_messageSettings));
		i18nField.appendChild( messageTitielDiv );
		
		i18nDiv = self.buildBody(ISA_I18N.types);
		i18nDiv.className = "i18nCategory";
		i18nField.appendChild(i18nDiv);
		
		var holidayField = document.createElement("div");
		holidayField.className = "i18nCategoryField";
		i18nBody.appendChild( holidayField );
		
		var holidayTitleDiv = document.createElement("p");
		holidayTitleDiv.className = "i18nCategoryTitle";
		holidayTitleDiv.appendChild( document.createTextNode(ISA_R.alb_holidaySettings));
		holidayField.appendChild( holidayTitleDiv );
		
		var holidayBody = self.buildBody({"holiday" : ISA_R.alb_holidaySettings});
		holidayBody.className = "i18nCategory";
		holidayField.appendChild( holidayBody );
		
//		container.replaceChild(i18nBody, loadingMessage);
		container.replaceChild(i18nBody, container.firstChild);
	};
	
	this.build = function() {
		var url = adminHostPrefix + "/services/i18n/getLocales";
		var opt = {
			method: 'get',
			asynchronous:true,
			onSuccess: function(response){
				var json = eval("(" + response.responseText + ")");
				var types = ISA_I18N.types;
				for(var type in types) {
					if(typeof types[type] == "function") continue;
					if(!json[type]) json[type] = ISA_I18N.defaultLocales;
				}
				ISA_I18N.i18nLocales = json;
				ISA_Holidays.fetchLocales( function( locales ) {
					ISA_I18N.i18nLocales.holiday = locales;
					
					self.display();
				});
			},
			on404: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_i18nNotFound+"</span>";
				msg.error(ISA_R.ams_i18nNotFound + t.status + " - " + t.statusText);
			},
			onFailure: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_i18nNotFound+"</span>";
				msg.error(ISA_R.ams_i18nNotFound + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_i18nNotFound+"</span>";
				msg.error(ISA_R.ams_i18nNotFound + getErrorMessage(t));
				throw t;
			},
			onComplete: function(req, obj){
				ISA_Admin.requestComplete = true;
			},
			onRequest: function() {
				ISA_Admin.requestComplete = false;
			}
		};
		AjaxRequest.invoke(url, opt);
	};
	
	this.buildBody = function( types ) {
		
		function buildLink(parentNode, text, func){
			parentNode.appendChild(document.createTextNode("["));
			var link = document.createElement("a");
			link.href = "#";
			link.innerHTML = text;
			parentNode.appendChild(link);
			if(func)
				IS_Event.observe(link, 'click', func, false, "_adminI18N");
			parentNode.appendChild(document.createTextNode("]"));
			
			return link;
		}
		
		var table = document.createElement("table");
		var tbody = document.createElement("tbody");
		table.appendChild(tbody);
		
		var i18nLocales = ISA_I18N.i18nLocales;
		for(var type in types) {
			if(typeof types[type] == "function") continue;
			var headerTr = document.createElement("tr");
			var headerTd = document.createElement("td");
			headerTd.className = "configTableHeaderTd";
			headerTd.style.bordeTop = "1px solid #666";
			headerTd.style.padding = "5px";
			headerTd.colSpan = 3;
			var headerLeft = document.createElement("div");
			headerLeft.style.cssFloat = "left";
			headerLeft.style.styleFloat = "left";
			
			if( type != "holiday" ) 
				headerLeft.appendChild(document.createTextNode(types[type]));
			
			headerTd.appendChild(headerLeft);
			var headerRight = document.createElement("div");
			headerRight.style.width = "100%";
			headerRight.style.textAlign = "right";
			
			buildLink(headerRight, ISA_R.alb_addLocale, self.showLocaleEditor.bind(this, this.localeModal, type));
			headerTd.appendChild(headerRight);
			headerTr.appendChild(headerTd);
			tbody.appendChild(headerTr);
			
			var locales = i18nLocales[type];
			for(var i=0; i<locales.length; i++) {
				var country = locales[i].country;
				var lang = locales[i].lang;
				var localesTr = document.createElement("tr");
				var localeTd = document.createElement("td");
				localeTd.style.padding = "3px";
				localeTd.style.width = "100px";
				localeTd.style.height = "20px";
				
				//localeTd.style.textAlign = "right";
				localeTd.appendChild(document.createTextNode(country + "_" + lang));
				localesTr.appendChild(localeTd);
				var linkTd = document.createElement("td");
				linkTd.className = "i18nLinkTd";
				var exportLink = buildLink(linkTd, ISA_R.alb_export);
				if( type != "holiday") {
					exportLink.href = adminHostPrefix + "/i18nexport?type=" + type + "&country=" + country + "&lang=" + lang;
				} else {
					exportLink.href = adminHostPrefix + "/services/holidays/downloadHoliday?country=" +country + "&lang=" + lang;
				}
				
				buildLink(linkTd, ISA_R.alb_import, self.showCSVImportForm.bind(this, this.localeModal, type, country, lang));
				
				var fileForm = document.createElement("input");
				fileForm.type = "file";
				
				var deleteTd = document.createElement("td");
				
				if (!ISA_I18N.isDefaultLocale(country, lang)) {
					var deleteImg = document.createElement("img");
					deleteImg.src = imageURL + "trash.gif";
					deleteImg.style.cursor = "pointer";
					deleteImg.title = ISA_R.alb_deleting;
					deleteTd.appendChild(deleteImg);
					/*
					var removeLink = buildLink(deleteTd, "delete");
					*/
					IS_Event.observe(deleteImg, 'click', function(type, country, lang){
						if (confirm(ISA_R.getResource(ISA_R.ams_deleteLocale, [country,lang]))){
							this.removeI18nLocale(type, country, lang);
						}
					}.bind(this, type, country, lang), false, "_adminI18N");
				}else{
					deleteTd.innerHTML ="&nbsp;";
				}
				if(i != locales.length-1){
					localeTd.style.borderBottom = "1px dashed #666";
					linkTd.style.borderBottom = "1px dashed #666";
					deleteTd.style.borderBottom = "1px dashed #666";
				}
				localesTr.appendChild(linkTd);
				localesTr.appendChild(deleteTd);
				
				tbody.appendChild(localesTr);
			}
		}
		
		return table;
	};
	
	/**
	 * Create element in CSV import modal
	 * 
	 * @param {Object} localeModal
	 * @param {Object} type
	 * @param {Object} country
	 * @param {Object} lang
	 */
	this.showCSVImportForm = function(localeModal, type, country, lang){
		var div = document.createElement("div");
		
		var closeDiv = document.createElement("div");
		closeDiv.style.textAlign = "right";
		var closeLink = document.createElement("a");
		closeLink.innerHTML = ISA_R.alb_close;
		closeLink.style.cursor = "pointer";
		IS_Event.observe(closeLink, "click", function(){localeModal.close();}, false, "_adminI18N");
		closeDiv.appendChild(closeLink);
		div.appendChild(closeDiv);
		
		var form;
		var fileForm;
		var radio_allReplace;
		var radio_insertUpdate 
		form = document.createElement("form")
		form.target = "csvResult";
		form.method = "POST";
		form.enctype="multipart/form-data";
		if(Browser.isIE)
			form.encoding="multipart/form-data";
		
		fileForm = document.createElement("input");
		fileForm.type = "file";
		fileForm.name = "csvFile";
		
		radio_allReplace = document.createElement("input");
		radio_allReplace.type = "radio";
		radio_allReplace.name = "mode";
		radio_insertUpdate = document.createElement("input");
		radio_insertUpdate.type = "radio";
		radio_insertUpdate.name = "mode";
		
		if( type != "holiday") {
			form.action = adminHostPrefix + "/i18nimport?type=" + type + "&country=" + country + "&lang=" + lang;
		} else {
			form.action = adminHostPrefix + "/services/holidays/uploadHoliday?country=" + country + "&lang=" + lang;
		}
		
		fileForm.style.height = "25px"
		fileForm.style.marginRight = "30px";
		
		var submit = document.createElement("input");
		submit.type = "button";
		submit.value = ISA_R.alb_executeImport;
		submit.style.height = "25px"
		
		radio_allReplace.checked = true;
		radio_allReplace.defaultChecked = true;
		radio_allReplace.value = "replace";
		
		radio_insertUpdate.value = "insertUpdate";
		
		var radio_allReplace_label = document.createElement("label");
		radio_allReplace_label.innerHTML = ISA_R.alb_replaceAll;
		
		var radio_insertUpdate_label = document.createElement("label");
		radio_insertUpdate_label.innerHTML = ISA_R.alb_fillDifference;
		
		var resultFrame
		
		resultFrame = document.createElement("iframe");
		resultFrame.name = "csvResult";
		resultFrame.style.width = "100%";
		resultFrame.style.height = "300px";
		resultFrame.frameBorder = 0;
		
		form.appendChild(fileForm);
		
		if( type != "holiday") {
			form.appendChild(radio_allReplace);
			form.appendChild(radio_allReplace_label);
			form.appendChild(radio_insertUpdate);
			form.appendChild(radio_insertUpdate_label);
		}

		form.appendChild(submit);
		
		IS_Event.observe(submit, "click", function(form, radio_allReplace, country, lang){
			if(ISA_I18N.isDefaultLocale(country, lang) && radio_allReplace.checked && type != "holiday"){
				if(confirm(ISA_R.ams_otherLocaleMessageDelete+"\n"+ISA_R.ams_continueDelete))
					form.submit();
			}else{
				form.submit();
			}
		}.bind(this, form, radio_allReplace, country, lang), true, "_adminI18N");
		
		if( type == "holiday") {
			var encMsg = document.createElement("div");
			encMsg.appendChild( document.createTextNode("※"+ISA_R.ams_iCalSavedUTF8));
			form.appendChild( encMsg );
		}
		
		form.appendChild(resultFrame);
		div.appendChild(form);
		
		localeModal.container.update(div);
		localeModal.open();

		var iframeDoc = Browser.isIE ? resultFrame.contentWindow.document : resultFrame.contentDocument;
		iframeDoc.write(ISA_R.alb_resultDisplaying);
	}
	
	this.showLocaleEditor = function(localeModal, type) {
		var div = document.createElement("div");
		
		var closeDiv = document.createElement("div");
		closeDiv.style.textAlign = "right";
		var closeLink = document.createElement("a");
		closeLink.innerHTML = ISA_R.alb_close;
		closeLink.style.cursor = "pointer";
		IS_Event.observe(closeLink, "click", function(){Control.Modal.close();}, false, "_adminI18N");
		closeDiv.appendChild(closeLink);
		div.appendChild(closeDiv);
		
		var table = document.createElement("table");
		table.className = "configTableHeader";
		div.appendChild(table);
		var caption = document.createElement("caption");
		caption.innerHTML = ISA_R.alb_addLocale;
		table.appendChild(caption);
		var tbody = document.createElement("tbody");
		table.appendChild(tbody);
		
		function createColumn(rows, isHeader){
			var tr = document.createElement("tr");
			for(var i=0;i<rows.length;i++){
				var td = document.createElement("td");
				if(isHeader){
					td.className = "configTableHeaderTd";
				}else{
					td.className = "configTableTd";
				}
				if(typeof rows[i] == "string"){
					td.innerHTML = rows[i];
				} else {
					td.appendChild(rows[i]);
				}
				tr.appendChild(td);
			}
			tbody.appendChild(tr);
		}
		
		createColumn([ISA_R.alb_country, ISA_R.alb_language, ""], true);
		
		function createSelect(options){
			var select = document.createElement("select");
			select.style.margin = "3px";
			for(var i in options){
			if(typeof options[i] == "function") continue;
				var option = document.createElement("option");
				option.value = i;
				option.innerHTML = options[i];
				select.appendChild( option );
			}
			return select;
		}
		
		var selectCountry = createSelect(ISA_Admin.countries);
		var selectLang = createSelect(ISA_Admin.langs);
		var button = document.createElement("input");
		button.type = "button";
		button.value = ISA_R.alb_add;
		button.style.margin = "3px";
		IS_Event.observe(button, "click", function(){
			var country = selectCountry.value;
			var lang = selectLang.value;
			
			var locales = ISA_I18N.i18nLocales[type];
			
			var contains = false;
			for(var i in locales){
				if(country == locales[i].country && lang == locales[i].lang){
					contains = true;
					break;
				}	
			}
			
			if (!contains) {
				// Register (synchronous)
				this.insertI18nLocale(type, country, lang);
			}else{
				alert(ISA_R.getResource(ISA_R.ams_localeAlreadyAdded, [country,lang]));
			}
		}.bind(this), false, "_adminI18N");

		createColumn([selectCountry, selectLang, button]);
		
		localeModal.container.update(div);
		localeModal.open();
		
	}
	
	this.insertI18nLocale = function(type, country, lang) {
		if( type == "holiday")
			return ISA_Holidays.insertHoliday( lang,country );
		
		var url = adminHostPrefix + "/services/i18n/insertI18nLocale";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			postBody: Object.toJSON([type,country,lang]),
			asynchronous:true,
			onSuccess: function(response){
				// Reload becuase there is no apply chanegs
				Control.Modal.close();
				ISA_Admin.clearAdminCache();
				ISA_I18N.i18n = new ISA_I18N();
				ISA_I18N.i18n.build();
			},
			onFailure: function(t) {
				alert(ISA_R.ams_failedUpdateLocale);
				msg.error(ISA_R.ams_failedUpdateLocale + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				alert(ISA_R.ams_failedUpdateLocale);
				msg.error(ISA_R.ams_failedUpdateLocale + getErrorMessage(t));
			}
		};
		AjaxRequest.invoke(url, opt);
	}
	
	this.removeI18nLocale = function(type, country, lang) {
		if( type == "holiday")
			return ISA_Holidays.deleteHoliday( lang,country );
		
		var url = adminHostPrefix + "/services/i18n/removeI18nLocale";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			postBody: Object.toJSON([type,country,lang]),
			asynchronous:true,
			onSuccess: function(response){
				// Reload becuase there is no apply chanegs
				ISA_Admin.clearAdminCache();
				ISA_I18N.i18n = new ISA_I18N();
				ISA_I18N.i18n.build();
			},
			onFailure: function(t) {
				alert(ISA_R.ams_failedDeleteLocale);
				msg.error(ISA_R.ams_failedDeleteLocale + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				alert(ISA_R.ams_failedDeleteLocale);
				msg.error(ISA_R.ams_failedDeleteLocale + getErrorMessage(t));
			}
		};
		AjaxRequest.invoke(url, opt);
	}
};

ISA_Holidays = Class.create();
ISA_Holidays.fetchLocales = function( callback ) {
	var url = adminHostPrefix + "/services/holidays/getHolidayLocalesJSON";
	var opt = {
		method: 'get',
		asynchronous: true,
		onSuccess: function(response){
			callback.apply( this,[eval( response.responseText )]);
		},
		onFailure: function(t) {
			alert(ISA_R.ams_failedLoadingHolidays);
			msg.error(ISA_R.ams_failedLoadingHolidays + t.status + " - " + t.statusText);
		},
		onException: function(r, t){
			alert(ISA_R.ams_failedLoadingHolidays);
			msg.error(ISA_R.ams_failedLoadingHolidays + getErrorMessage(t));
			throw t;
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
ISA_Holidays.insertHoliday = function( lang,country ) {
	var url = adminHostPrefix + "/services/holidays/updateHoliday";
	var opt = {
		method: 'post' ,
		contentType: "application/json",
		postBody: Object.toJSON([lang,country,""]),
		asynchronous:true,
		onSuccess: function(response){
			// Reload becuase there is no apply chanegs
			Control.Modal.close();
			ISA_Admin.clearAdminCache();
			ISA_I18N.i18n = new ISA_I18N();
			ISA_I18N.i18n.build();
		},
		onFailure: function(t) {
			alert(ISA_R.ams_failedUpdateLocale);
			msg.error(ISA_R.ams_failedUpdateLocale + t.status + " - " + t.statusText);
		},
		onException: function(r, t){
			alert(ISA_R.ams_failedUpdateLocale);
			msg.error(ISA_R.ams_failedUpdateLocale + getErrorMessage(t));
		}
	};
	
	AjaxRequest.invoke(url, opt);
}
ISA_Holidays.deleteHoliday = function( lang,country ) {
	var url = adminHostPrefix + "/services/holidays/deleteHoliday";
	var opt = {
		method: 'post' ,
		contentType: "application/json",
		postBody: Object.toJSON([lang,country]),
		asynchronous:true,
		onSuccess: function(response){
			// Reload becuase there is no apply chanegs
			ISA_Admin.clearAdminCache();
			ISA_I18N.i18n = new ISA_I18N();
			ISA_I18N.i18n.build();
		},
		onFailure: function(t) {
			alert(ISA_R.ams_failedDeleteLocale);
			msg.error(ISA_R.ams_failedDeleteLocale + t.status + " - " + t.statusText);
		},
		onException: function(r, t){
			alert(ISA_R.ams_failedDeleteLocale);
			msg.error(ISA_R.ams_failedDeleteLocale + getErrorMessage(t));
		}
	};
	AjaxRequest.invoke(url, opt);
}
