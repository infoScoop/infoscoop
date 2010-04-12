var ISA_SearchEngine = IS_Class.create();

ISA_SearchEngine.searchEngine = false;
ISA_SearchEngine.defaultSearchList = false;
ISA_SearchEngine.rssSearchList = false;

// Called by the value returned from server
ISA_SearchEngine.setSearchEngine = function(_defaultSearchList, _rssSearchList) {
	ISA_SearchEngine.defaultSearchList = _defaultSearchList;
	ISA_SearchEngine.rssSearchList = _rssSearchList;
}

ISA_SearchEngine.prototype.classDef = function() {
	var self = this;
	var container;
	var loadingMessage;
	
	this.initialize = function() {
		container = document.getElementById("searchEngine");
		
		/**
		 * Remove trash if it remains
		 */
		var len = container.childNodes.length;
		for(var i = 0; i < len; i++) {
			container.removeChild(container.lastChild);
		}
		
		loadingMessage = document.createElement('div');
		loadingMessage.innerHTML = "Loading...";
		loadingMessage.style.clear = "both";
		loadingMessage.style.cssFloat = "left";
		container.appendChild(loadingMessage);
	};
	
	this.displaySearchEngine = function() {

		var searchEngineDiv = document.createElement("div");
		searchEngineDiv.style.clear = "both";

		var refreshAllDiv = document.createElement("div");
		refreshAllDiv.id = "refreshAll";
		refreshAllDiv.style.textAlign = "right";
		refreshAllDiv.style.width = "920px";
	
		var commitDiv = ISA_Admin.createIconButton(ISA_R.alb_changeApply, ISA_R.alb_changeApply, "database_save.gif", "right");
		refreshAllDiv.appendChild(commitDiv);
		var currentModal = new Control.Modal(
			commitDiv,
			{
			  contents: ISA_R.ams_applyingChanges,
			  opacity: 0.2,
			  containerClassName:"commitDialog",
			  overlayCloseOnClick:false
			}
		);
		IS_Event.observe(commitDiv, 'click', commitSearchEngine.bind(this, currentModal), "_adminSearch");
		
		var refreshDiv = ISA_Admin.createIconButton(ISA_R.alb_refresh, ISA_R.alb_reloadWithourSaving, "refresh.gif", "right");
		refreshAllDiv.appendChild(refreshDiv);
		var refreshAClick = function(e){
			if(!ISA_Admin.checkUpdated())return;
			if(!ISA_Admin.requestComplete) return;
			ISA_Admin.clearAdminCache();
			new ISA_SearchEngine().build();
		};
		IS_Event.observe(refreshDiv, 'click', refreshAClick, false, "_adminSearch");

		searchEngineDiv.appendChild(refreshAllDiv);
		
		var searchEngineFieldSet = document.createElement("fieldset");
		searchEngineFieldSet.style.padding = "7px";
		searchEngineFieldSet.style.marginBottom = "10px";
		searchEngineFieldSet.style.clear = "both";
		searchEngineFieldSet.style.width = "920px";
		var label = document.createElement("legend");
		label.innerHTML = ISA_R.alb_serchSiteSetting;
		searchEngineFieldSet.appendChild( label );

		//searchEngineFieldSet.appendChild(titleDiv1);
		searchEngineFieldSet.appendChild(self.buildDefaultSearchEngine());
		searchEngineFieldSet.appendChild(ISA_Admin.buildTableHeader(
			[ISA_R.alb_title,ISA_R.alb_searchAdress,ISA_R.alb_encoding,ISA_R.alb_numberOfItems,ISA_R.alb_publicSettings,ISA_R.alb_delete],
			['220px', '320px', '100px', '40px', '80px', '40px']
			));
		// DefaultSearch build
		var defaultSearchDiv = document.createElement("div");
		defaultSearchDiv.id = "defaultSearchEngineList";
		for(i in ISA_SearchEngine.defaultSearchList){
			if( !(ISA_SearchEngine.defaultSearchList[i] instanceof Function) )
			  defaultSearchDiv.appendChild(self.buildDefaultSearchList(ISA_SearchEngine.defaultSearchList[i]));
		}
		searchEngineFieldSet.appendChild(defaultSearchDiv);
		searchEngineDiv.appendChild(searchEngineFieldSet);

		var searchEngineFieldSet = document.createElement("fieldset");
		searchEngineFieldSet.style.padding = "7px";
		searchEngineFieldSet.style.width = "920px";
		var label = document.createElement("legend");
		label.innerHTML = ISA_R.alb_insiteSearchSettings;
		searchEngineFieldSet.appendChild( label );
		searchEngineFieldSet.appendChild(self.buildRssSearchEngine());
		searchEngineFieldSet.appendChild(ISA_Admin.buildTableHeader(
			[ISA_R.alb_rssPattern,ISA_R.alb_searchAdress,ISA_R.alb_encoding,ISA_R.alb_numberOfItems,ISA_R.alb_publicSettings,ISA_R.alb_delete],
			['260px', '280px', '100px', '40px', '80px', '40px']
			));
		
		var rssSearchDiv = document.createElement("div");
		rssSearchDiv.id = "rssSearchEngineList";

		// RssSearch build
		for(i in ISA_SearchEngine.rssSearchList){
			if( !(ISA_SearchEngine.rssSearchList[i] instanceof Function) )
			  rssSearchDiv.appendChild(self.buildRssSearchList(ISA_SearchEngine.rssSearchList[i]));
		}
		searchEngineFieldSet.appendChild(rssSearchDiv);
		searchEngineDiv.appendChild(searchEngineFieldSet);
		
		container.replaceChild(searchEngineDiv,loadingMessage);

		// Drag&Drop
		new ISA_DragDrop.SearchEngineDragDrop("defaultSearchEngineList");
		new ISA_DragDrop.SearchEngineDragDrop("rssSearchEngineList");
	}

	function commitSearchEngine(currentModal) {
		var url = findHostURL() + "/services/searchEngine/commitSearch";
		var opt = {
			method: 'get' ,
			asynchronous:true,
			onSuccess: function(response){
				ISA_Admin.isUpdated = false;
				currentModal.update(ISA_R.ams_changeUpdated);
			},
			onFailure: function(t) {
				alert(ISA_R.ams_failedCommitSearchForm);
				msg.error(ISA_R.ams_failedCommitSearchForm + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				alert(ISA_R.ams_failedCommitSearchForm);
				msg.error(ISA_R.ams_failedCommitSearchForm + getErrorMessage(t));
			},
			onComplete: function(){
				setTimeout(function(){
					currentModal.close();
				},500);
			}
		};
		AjaxRequest.invoke(url, opt);
	}

	function insertSearchEngine(searchEngine) {
		var url = findHostURL() + "/services/searchEngine/addSearchEngine";
		
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			postBody: Object.toJSON([
				searchEngine.parentTagName,
				String( replaceUndefinedValue(searchEngine.id, "") ),
				replaceUndefinedValue(searchEngine.title, ""),
				replaceUndefinedValue(searchEngine.retrieveUrl, ""),
				replaceUndefinedValue(searchEngine.encoding, "")]),
			asynchronous:true,
			onSuccess: function(response){},
			onFailure: function(t) {
				alert(ISA_R.ams_failedAddSearchEngine);
				msg.error(ISA_R.ams_failedAddSearchEngine + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				alert(ISA_R.ams_failedAddSearchEngine);
				msg.error(ISA_R.ams_failedAddSearchEngine + getErrorMessage(t));
			}
		};
		AjaxRequest.invoke(url, opt);
	}

	this.buildDefaultSearchEngine = function() {
		var defaultSearchEngineDiv = document.createElement("div");
		defaultSearchEngineDiv.id = "defaultSearchEngine";
		defaultSearchEngineDiv.style.width = '900px';

		var addDefaultDiv = ISA_Admin.createIconButton(ISA_R.alb_add, ISA_R.alb_add, "add.gif", "left");
		addDefaultDiv.id = "addDefault";
		var addAClick = function(e){
			ISA_Admin.isUpdated = true;
			var jsonObj = {
				id : String(new Date().getTime()),
				title : ISA_R.alb_newTitle,
				retrieveUrl : "http://",
				encoding : "",
				parentTagName : "defaultSearch"
			};
			insertSearchEngine(jsonObj);
			$('defaultSearchEngineList').appendChild(self.buildDefaultSearchList(jsonObj));
			// Rebuild Drag&Drop
			new ISA_DragDrop.SearchEngineDragDrop("defaultSearchEngineList");
		}
		IS_Event.observe(addDefaultDiv, 'click', addAClick.bind(addDefaultDiv), false, "_adminSearch");

		var annotateDiv = document.createElement("div");
		annotateDiv.style.cssFloat = "right";
		annotateDiv.style.styleFloat = "right";
		annotateDiv.style.textAlign = "right";
		var font = document.createElement("font");
		font.size = "-1";
		font.color = "#ff0000";
		font.appendChild(document.createTextNode(ISA_R.alb_matchingFromTop));
		annotateDiv.appendChild(font);

		defaultSearchEngineDiv.appendChild(addDefaultDiv);
		defaultSearchEngineDiv.appendChild(annotateDiv);

		return defaultSearchEngineDiv;
	}

	/**
	 * defaultSearchItem.id
	 * defaultSearchItem.title
	 * defaultSearchItem.retrieveUrl
	 * defaultSearchItem.encoding
	 * defaultSearchItem.countRule.method
	 * defaultSearchItem.countRule.value
	 */
	this.buildDefaultSearchList = function(defaultSearchItem) {
		defaultSearchItem.parentTagName = "defaultSearch";

		var engineDiv = document.createElement("div");
		engineDiv.id = "row_" + defaultSearchItem.id;
		//engineDiv.className = "rowSearchEngine";
		engineDiv.className = "proxyConfigList";

		var engineTable = document.createElement("table");
		engineTable.id = defaultSearchItem.id;
		engineTable.width = "900px";
		engineTable.style.tableLayout = "fixed";
		engineTable.cellSpacing = "0";
		engineTable.cellPadding = "0";
		engineDiv.appendChild(engineTable);

		var engineTbody = document.createElement("tbody");
		engineTable.appendChild(engineTbody);

		var engineTr = document.createElement("tr");
		//engineTr.style.height = "20px";
		engineTbody.appendChild(engineTr);
		var postJSON;
		var engineTd;
		var contentDiv;

		// Icon for Drag
		engineTd = document.createElement("td");
		engineTd.style.width = "20px";
		engineTr.appendChild(engineTd);
		contentDiv = document.createElement("div");
		contentDiv.className = "handle";
		var engineA = document.createElement("a");
		engineA.style.cursor = "move";
		var engineImg = document.createElement("img");
		engineImg.src = imageURL + "drag.gif";
		engineA.appendChild(engineImg);
		contentDiv.appendChild(engineA);
		engineTd.appendChild(contentDiv);

		// Title
		engineTd = document.createElement("td");
		engineTd.style.width = "200px";
		engineTr.appendChild(engineTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "100%";
		contentDiv.className = "contentsSearchEngine";
		contentDiv.id = "ttl_" + defaultSearchItem.id;
		contentDiv.appendChild(document.createTextNode(replaceUndefinedValue(defaultSearchItem.title, "　", true)));
		engineTd.appendChild(contentDiv);
		new ISA_InstantEdit(contentDiv,"searchEngine", "updateSearchEngineItem", function( value ) {
			ISA_Admin.isUpdated = true;
			return [ defaultSearchItem.id, {
				title: value
			}]
		}, 128,{ required: true,label: ISA_R.alb_title });	// Enable input by clicking

		// Address for search
		engineTd = document.createElement("td");
		engineTd.style.width = "320px";
		engineTr.appendChild(engineTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "100%";
		contentDiv.className = "contentsSearchEngine";
		contentDiv.id = "url_" + defaultSearchItem.id;
		contentDiv.appendChild(document.createTextNode(replaceUndefinedValue(defaultSearchItem.retrieveUrl, "　", true)));
		engineTd.appendChild(contentDiv);
		new ISA_InstantEdit(contentDiv,"searchEngine", "updateSearchEngineItem", function( value ) {
			ISA_Admin.isUpdated = true;
			return [ defaultSearchItem.id, {
				retrieveUrl: value
			}]
		}, 512,{ required: true,label: ISA_R.alb_searchAdress});	// Enable input by clicking

		// Encoding
		engineTd = document.createElement("td");
		engineTd.style.width = "100px";
		engineTr.appendChild(engineTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "100%";
		contentDiv.className = "contentsSearchEngine";
		contentDiv.id = "enc_" + defaultSearchItem.id;
		contentDiv.appendChild(document.createTextNode(replaceUndefinedValue(defaultSearchItem.encoding, "　", true)));
		engineTd.appendChild(contentDiv);
		new ISA_InstantEdit(contentDiv,"searchEngine", "updateSearchEngineItem", function( value ) {
			ISA_Admin.isUpdated = true;
			return [ defaultSearchItem.id, {
				encoding: value
			}]
		}, 32, {format:'charset',label:ISA_R.alb_encoding});	// Enable input by clicking

		// Number of items
		engineTd = document.createElement("td");
		engineTd.style.width = "40px";
		engineTd.style.textAlign = "center";
		engineTr.appendChild(engineTd);
		contentDiv = document.createElement("div");
		contentDiv.className = "contentsSearchEngine";
		contentDiv.id = "cnt_" + defaultSearchItem.id;
		var editA = document.createElement("a");
		editA.style.cursor = "pointer";
		var editImg = document.createElement("img");
		editImg.src = imageURL + "edit.gif";
		editA.appendChild(editImg);
		contentDiv.appendChild(editA);
		engineTd.appendChild(contentDiv);
		new ISA_SearchEngine.EditorForm(contentDiv, defaultSearchItem, {count: true});

		// Access Control Setting
		engineTd = document.createElement("td");
		engineTd.style.width = "80px";
		engineTd.style.textAlign = "center";
		engineTr.appendChild(engineTd);
		contentDiv = document.createElement("div");
		contentDiv.className = "contentsSearchEngine";
		contentDiv.id = "acl_" + defaultSearchItem.id;
		var editA = document.createElement("a");
		editA.style.cursor = "pointer";
		editA.appendChild($.SPAN(
			{id:"acl_label_"+defaultSearchItem.id},
			defaultSearchItem.auths?ISA_R.alb_restricted:ISA_R.alb_public
		));
		var editImg = document.createElement("img");
		editImg.src = imageURL + "edit.gif";
		editA.appendChild(editImg);
		contentDiv.appendChild(editA);
		engineTd.appendChild(contentDiv);
		new ISA_SearchEngine.EditorForm(contentDiv, defaultSearchItem, {acl: true});

		// "Delete" icon
		engineTd = document.createElement("td");
		engineTd.style.width = "40px";
		engineTd.style.textAlign = "center";
		engineTr.appendChild(engineTd);
		contentDiv = document.createElement("div");
		contentDiv.className = "contentsSearchEngine";
		var deleteImg = document.createElement("img");
		deleteImg.src = imageURL + "trash.gif";
		deleteImg.style.cursor = "pointer";
		deleteImg.alt = ISA_R.alb_deleting;
		var deleteImgClick = function(e){
			ISA_Admin.isUpdated = true;
			self.removeSearchEngine(defaultSearchItem);
			engineDiv.parentNode.removeChild(engineDiv);
		}
		IS_Event.observe(deleteImg, 'click', deleteImgClick.bind(deleteImg), false, "_adminSearch");
		contentDiv.appendChild(deleteImg);
		engineTd.appendChild(contentDiv);

		return engineDiv;
	}

	this.buildRssSearchEngine = function() {
		var rssSearchEngineDiv = document.createElement("div");
		rssSearchEngineDiv.id = "rssSearchEngine";
		rssSearchEngineDiv.style.width = "900px";

		var rssSearchDiv = null;
		var addRssDiv = ISA_Admin.createIconButton(ISA_R.alb_add, ISA_R.alb_add, "add.gif", "left");
		addRssDiv.id = "addRss";
		var addAClick = function(e){
			ISA_Admin.isUpdated = true;
			var jsonObj = {
				id : String( new Date().getTime() ),
				title : ISA_R.alb_newRssPattern,
				rssPattern: "^http://",
				retrieveUrl : "http://",
				encoding : "",
				parentTagName : "rssSearch"
			};
			insertSearchEngine(jsonObj);
			$("rssSearchEngineList").appendChild(self.buildRssSearchList(jsonObj));
			// Rebuild Drag&Drop
			new ISA_DragDrop.SearchEngineDragDrop("rssSearchEngineList");
		}
		IS_Event.observe(addRssDiv, 'click', addAClick.bind(addRssDiv), false, "_adminSearch");

		var annotateDiv = document.createElement("div");
		annotateDiv.style.cssFloat = "right";
		annotateDiv.style.styleFloat = "right";
		annotateDiv.style.textAlign = "right";
		var font = document.createElement("font");
		font.size = "-1";
		font.color = "#ff0000";
		font.appendChild(document.createTextNode(ISA_R.alb_matchingFromTop));
		annotateDiv.appendChild(font);

		rssSearchEngineDiv.appendChild(addRssDiv);
		rssSearchEngineDiv.appendChild(annotateDiv);

		return rssSearchEngineDiv;
	}

	/**
	 * rssSearchItem.id
	 * rssSearchItem.retrieveUrl
	 * rssSearchItem.rssPattern
	 * rssSearchItem.countRule.method
	 * rssSearchItem.countRule.value
	 */
	this.buildRssSearchList = function(rssSearchItem) {
		rssSearchItem.parentTagName = "rssSearch";

		var engineDiv = document.createElement("div");
		engineDiv.id = "row_" + rssSearchItem.id;
		//engineDiv.className = "rowSearchEngine";
		engineDiv.className = "proxyConfigList";

		var engineTable = document.createElement("table");
		engineTable.id = rssSearchItem.id;
		engineTable.width = "900px";
		engineTable.style.tableLayout = "fixed";
		engineTable.cellSpacing = "0";
		engineTable.cellPadding = "0";
		engineDiv.appendChild(engineTable);

		var engineTbody = document.createElement("tbody");
		engineTable.appendChild(engineTbody);

		var engineTr = document.createElement("tr");
		//engineTr.style.height = "20px";
		engineTbody.appendChild(engineTr);
		var postJSON;
		var engineTd;
		var contentDiv;

		// Icon for Drag
		engineTd = document.createElement("td");
		engineTd.style.width = "20px";
		engineTr.appendChild(engineTd);
		contentDiv = document.createElement("div");
		contentDiv.className = "handle";
		var engineA = document.createElement("a");
		engineA.style.cursor = "move";
		var engineImg = document.createElement("img");
		engineImg.src = imageURL + "drag.gif";
		engineA.appendChild(engineImg);
		contentDiv.appendChild(engineA);
		engineTd.appendChild(contentDiv);

		// RSS Pattern
		engineTd = document.createElement("td");
		engineTd.style.width = "240px";
		engineTr.appendChild(engineTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "100%";
		contentDiv.className = "contentsSearchEngine";
		contentDiv.id = "ttl_" + rssSearchItem.id;
		contentDiv.appendChild(document.createTextNode(replaceUndefinedValue(rssSearchItem.rssPattern, "　", true)));
		engineTd.appendChild(contentDiv);
		new ISA_InstantEdit(contentDiv,"searchEngine", "updateSearchEngineItem", function( value ) {
			ISA_Admin.isUpdated = true;
			return [ rssSearchItem.id, {
				rssPattern: value
			},"rssPattern"]
		}, 512, {format:"regexp", required:true, label:ISA_R.alb_rssPattern});	// Enable input by clicking

		// Address for search
		engineTd = document.createElement("td");
		engineTd.style.width = "280px";
		engineTr.appendChild(engineTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "100%";
		contentDiv.className = "contentsSearchEngine";
		contentDiv.id = "url_" + rssSearchItem.id;
		contentDiv.appendChild(document.createTextNode(replaceUndefinedValue(rssSearchItem.retrieveUrl, "　", true)));
		engineTd.appendChild(contentDiv);
		new ISA_InstantEdit(contentDiv,"searchEngine", "updateSearchEngineItem", function( value ) {
			ISA_Admin.isUpdated = true;
			return [ rssSearchItem.id, {
				retrieveUrl: value
			}]
		}, 512,{ required: true,label: ISA_R.alb_searchAdress });	// Enable input by clicking

		// Encoding
		engineTd = document.createElement("td");
		engineTd.style.width = "100px";
		engineTr.appendChild(engineTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "100%";
		contentDiv.className = "contentsSearchEngine";
		contentDiv.id = "enc_" + rssSearchItem.id;
		contentDiv.appendChild(document.createTextNode(replaceUndefinedValue(rssSearchItem.encoding, "　", true)));
		engineTd.appendChild(contentDiv);
		new ISA_InstantEdit(contentDiv,"searchEngine", "updateSearchEngineItem", function( value ) {
			ISA_Admin.isUpdated = true;
			return [ rssSearchItem.id, {
				encoding: value
			}]
		}, 32, {format:"charset",label:ISA_R.alb_encoding});	// Enable input by clicking

		// Number of items
		engineTd = document.createElement("td");
		engineTd.style.width = "40px";
		engineTd.style.textAlign = "center";
		engineTr.appendChild(engineTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "100%";
		contentDiv.className = "contentsSearchEngine";
		contentDiv.id = "cnt_" + rssSearchItem.id;
		var editA = document.createElement("a");
		editA.style.cursor = "pointer";
		var editImg = document.createElement("img");
		editImg.src = imageURL + "edit.gif";
		editA.appendChild(editImg);
		contentDiv.appendChild(editA);
		engineTd.appendChild(contentDiv);
		new ISA_SearchEngine.EditorForm(contentDiv, rssSearchItem, {count: true});

		// Access Control Setting
		engineTd = document.createElement("td");
		engineTd.style.width = "80px";
		engineTd.style.textAlign = "center";
		engineTr.appendChild(engineTd);
		contentDiv = document.createElement("div");
		contentDiv.className = "contentsSearchEngine";
		contentDiv.id = "acl_" + rssSearchItem.id;
		var editA = document.createElement("a");
		editA.style.cursor = "pointer";
		editA.appendChild($.SPAN(
			{id:"acl_label_"+rssSearchItem.id},
			rssSearchItem.auths?ISA_R.alb_restricted:ISA_R.alb_public
		));
		var editImg = document.createElement("img");
		editImg.src = imageURL + "edit.gif";
		editA.appendChild(editImg);
		contentDiv.appendChild(editA);
		engineTd.appendChild(contentDiv);
		new ISA_SearchEngine.EditorForm(contentDiv, rssSearchItem, {acl: true});

		// "Delete" icon
		engineTd = document.createElement("td");
		engineTd.style.width = "40px";
		engineTd.style.textAlign = "center";
		engineTr.appendChild(engineTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "100%";
		contentDiv.className = "contentsSearchEngine";
		var deleteImg = document.createElement("img");
		deleteImg.src = imageURL + "trash.gif";
		deleteImg.style.cursor = "pointer";
		deleteImg.title = ISA_R.alb_deleting;
		var deleteImgClick = function(e){
			ISA_Admin.isUpdated = true;
			self.removeSearchEngine(rssSearchItem);
			engineDiv.parentNode.removeChild(engineDiv);
		}
		IS_Event.observe(deleteImg, 'click', deleteImgClick.bind(deleteImg), false, "_adminSearch");
		contentDiv.appendChild(deleteImg);
		engineTd.appendChild(contentDiv);

		return engineDiv;
	}

	this.build = function() {
		var url = findHostURL() + "/services/searchEngine/getSearchEngineJson";
		var opt = {
			method: 'get' ,
			asynchronous:true,
			onSuccess: function(response){
				eval(response.responseText);
				self.displaySearchEngine();
			},
			on404: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_searchEngineNotFound+"</span>";
				msg.error(ISA_R.ams_searchEngineNotFound + t.status + " - " + t.statusText);
			},
			onFailure: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingSearchEngine+"</span>";
				msg.error(ISA_R.ams_failedLoadingSearchEngine + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingSearchEngine+"</span>";
				msg.error(ISA_R.ams_failedLoadingSearchEngine + getErrorMessage(t));
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

	this.removeSearchEngine = function( searchEngine) {
		var url = findHostURL() + "/services/searchEngine/removeSearchEngine";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			postBody: Object.toJSON([replaceUndefinedValue(searchEngine.id, "", false)]),
			asynchronous:true,
			onSuccess: function(response){
			},
			onFailure: function(t) {
				alert(ISA_R.ams_failedSaveSearchEngine);
				msg.error(ISA_R.ams_failedSaveSearchEngine + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				alert(ISA_R.ams_failedSaveSearchEngine);
				msg.error(ISA_R.ams_failedSaveSearchEngine + getErrorMessage(t));
			}
		};
		AjaxRequest.invoke(url, opt);
	}

	function replaceUndefinedValue(val, rep, flg) {
		var value = val;
		value = (value != undefined ? value : rep);
		value = ((!flg || value != "") ? value : rep);
		return value;
	}

};

ISA_SearchEngine.EditorForm = IS_Class.create();
ISA_SearchEngine.EditorForm.prototype.classDef = function() {
	var self = this;
	var editorElement;
	var searchEngine;
	var option;
	var disabled;
	
	this.initialize = function(_editorElement, _searchEngine, _option) {
		editorElement = _editorElement;
		searchEngine = _searchEngine;
		option = _option;
		this.buildTitleEditorForm();
		authorizations = [];
	};
	
	this.submitEditorForm = function() {
		var updateData;
		if(option.count) {
			var fMethod = $("formMethod").value;
			var fValue = $("formValue").value;

			if(fMethod == "regexp"){
				var error = IS_Validator.validate(fValue, {format:'regexp'}); 
				if(error){
					alert(error);
					return;
				}
			}
			
			// Disable execute button
			$("formExec").disabled = true;
			$("formCancel").disabled = true;
			
			updateData = Object.toJSON([
				ISA_Admin.replaceUndefinedValue(searchEngine.id),{
					method: fMethod,
					value: fValue
				},"countRule"
			]);
		}
		if(option.acl) {
			if($("formIsPublic").checked == true){
				searchEngine.auths = undefined;
			}else{
				searchEngine.auths = authorizations;
			}
			$("acl_label_"+searchEngine.id).innerHTML = searchEngine.auths?ISA_R.alb_restricted:ISA_R.alb_public;
			updateData = Object.toJSON([
				ISA_Admin.replaceUndefinedValue(searchEngine.id),
				{auths:searchEngine.auths},
				"auths"
			]);
		}

		var url = findHostURL() + "/services/searchEngine/updateSearchEngineItem";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			postBody: updateData,
			asynchronous:true,
			onSuccess: function(response){
				ISA_Admin.isUpdated = true;
				// Update values of array
				if(!searchEngine.countRule){
					var jsonStr,jsonObj;
					jsonStr = "{";
					jsonStr += "method:" + "\"" + fMethod + "\"";
					jsonStr += ",";
					jsonStr += "value:" + "\"" + fValue + "\"";
					jsonStr += "}";
					eval("jsonObj="+jsonStr);
					searchEngine["countRule"] = jsonObj;
				}else{
					searchEngine.countRule.method = fMethod;
					searchEngine.countRule.value = fValue;
				}
				self.currentModal.close();
			},
			onFailure: function(t) {
				alert(ISA_R.ams_failedSaveSearchEngine);
				msg.error(ISA_R.ams_failedSaveSearchEngine + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				alert(ISA_R.ams_failedSaveSearchEngine);
				msg.error(ISA_R.ams_failedSaveSearchEngine + getErrorMessage(t));
			}
		};
		AjaxRequest.invoke(url, opt);
	}

	this.loadEditorForm = function(editorFormFieldDiv) {
		var editorsFormDiv = document.createElement("div");
//		editorsFormDiv.id = "editorsFormDiv";
		var showEditorsForm = document.createElement("div");
//		showEditorsForm.id = "showEditorsForm";

		if(option.count) {
			var countForm = ISA_SearchEngine.EditorForm.makeCountEditForm(searchEngine);
			showEditorsForm.appendChild(
				countForm
			);
		}
		if(option.acl) {
			showEditorsForm.appendChild(
				ISA_CommonModals.EditorForm.makeMenuItemACLEditFieldSet(false, searchEngine)
			);
		}

		/* Execute button */
		showEditorsForm.appendChild(makeExecButton());

		editorsFormDiv.appendChild(showEditorsForm);
		
		editorFormFieldDiv.appendChild(editorsFormDiv);

		function makeExecButton(){
			var buttonDiv = document.createElement("div");
			buttonDiv.style.textAlign = "center";
			
			var elementInput = document.createElement("input");
			elementInput.className = "modal_button";
			elementInput.type = "button";
			elementInput.id = "formExec";
			elementInput.name = "FORM_EXEC";
			elementInput.value = ISA_R.alb_ok;
			IS_Event.observe(elementInput, 'click', self.submitEditorForm.bind(self), false, "_adminSearch");
			buttonDiv.appendChild(elementInput);
			
			var closeButton = document.createElement("input");
			closeButton.className = "modal_button";
			closeButton.type = "button";
			closeButton.id = "formCancel";
			closeButton.value = ISA_R.alb_cancel;
			IS_Event.observe(closeButton, 'click', self.hideTitleEditorForm.bind(self), false, "_adminSearch");
			buttonDiv.appendChild(closeButton);
			return buttonDiv;
		}
	};

	this.hideTitleEditorForm = function(){
		IS_Event.unloadCache("_editorForm");
		Control.Modal.close();
	};

	this.showTitleEditorForm = function (){
		var viewFormArea = function(){
			var editorFormFieldDiv = document.createElement("div");
			
			self.loadEditorForm(editorFormFieldDiv);

			self.currentModal.update(editorFormFieldDiv);
		}

		setTimeout(viewFormArea, 10);
	};

	this.buildTitleEditorForm = function (){
		if(editorElement){
			IS_Event.observe(editorElement, 'click', this.showTitleEditorForm.bind(this), false, "_adminSearch");

			this.currentModal = new Control.Modal(
				editorElement,
				{
					contents: "",
					opacity: 0.2,
					containerClassName:"adminSearchEngine",
					afterClose:this.hideTitleEditorForm.bind(this)
				});
		}
	};

};

ISA_SearchEngine.EditorForm.makeCountEditForm = function(searchEngine){
	var disabled;

	function makeMethodSelect(){
		var subTr = document.createElement("tr");
		var subTd = document.createElement("td");
		subTd.style.width = "30%";
		subTd.style.textAlign = "right";
		subTd.appendChild(document.createTextNode(ISA_R.alb_method));
		subTr.appendChild(subTd);

		subTd = document.createElement("td");
		subTd.style.width = "70%";
		var subInput = document.createElement("select");
		subInput.id = "formMethod";
		subInput.name = "FORM_METHOD";
		subInput.disabled = disabled;

		var methodList = new Array(["regexp",ISA_R.alb_regularExpression], ["id","ID"]);

		for(var i = 0; i < methodList.length; i++){
			var opt = document.createElement("option");
			opt.id = searchEngine.id + '_optName' + i;
			opt.value = methodList[i][0];
//				opt.innerHTML = methodList[i][1];
			opt.appendChild(document.createTextNode(methodList[i][1]));
			if(searchEngine.countRule){
				if(methodList[i][0] == searchEngine.countRule.method){
					opt.selected = true;
				}
			}
			subInput.appendChild( opt );
		}
		subTd.appendChild(subInput);
		subTr.appendChild(subTd);
		return subTr;
	}

	function makeValueText(){
		var elementTr = document.createElement("tr");
		var elementTd = document.createElement("td");
		elementTd.style.width = "30%";
		elementTd.style.textAlign = "right";
		elementTd.appendChild(document.createTextNode(ISA_R.alb_valueColon));
		elementTr.appendChild(elementTd);

		elementTd = document.createElement("td");
		elementTd.style.width = "70%";
		var elementInput = document.createElement("input");
//			elementInput.setAttribute('autocomplete','off'); 
		elementInput.type = "text";
		elementInput.id = "formValue";
		elementInput.name = "FORM_VALUE";
		elementInput.size = "50";
		elementInput.maxLength = "256";
		if(searchEngine.countRule){
			elementInput.value = ISA_Admin.replaceUndefinedValue(searchEngine.countRule.value);
		}
		elementInput.disabled = disabled;
		elementTd.appendChild(elementInput);
		elementTr.appendChild(elementTd);
		return elementTr;
	}
	
	/* Create outer box*/
	var contentTable = document.createElement("table");
	contentTable.style.width = "100%";
	contentTable.setAttribute("cellpadding","0");
	contentTable.setAttribute("cellspacing","0");

	var contentTbody = document.createElement("tbody");
	var contentTr = document.createElement("tr");
	var contentTd = document.createElement("td");
	var contentDiv = document.createElement("div");
//		contentDiv.id = "";
	contentTable.appendChild(contentTbody);
	contentTbody.appendChild(contentTr);
	contentTr.appendChild(contentTd);
	contentTd.appendChild(contentDiv);

	/* Create main */
	var editorFormTable = document.createElement("table");
	editorFormTable.style.width = "100%";
	contentDiv.appendChild(editorFormTable);
	var editorFormTbody = document.createElement("tbody");
	editorFormTable.appendChild(editorFormTbody);

	// Input item:method
	editorFormTbody.appendChild(makeMethodSelect());

	// Input item:value
	editorFormTbody.appendChild(makeValueText());
	
	return $.FIELDSET({},
		$.LEGEND({}, ISA_R.alb_numberOfItems),
		contentTable
	);
}