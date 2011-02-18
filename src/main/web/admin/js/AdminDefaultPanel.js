var ISA_DefaultPanel = IS_Class.create();
IS_EventDispatcher.addListener("deleteTemp", "", function(all){
	if(all || ISA_DefaultPanel.defaultPanel){
		// Deleting Temp data
		var url = findHostURL() + "/services/tabLayout/deleteTemp";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			asynchronous:true,
			onSuccess: function(response){
				msg.info(ISA_R.ams_deleteEdittingMenuData);
			},
			onFailure: function(t) {
				var errormsg = t.responseText && typeof t.responseText == "string" ? t.responseText.substr(0, 100) : "";
				msg.error(ISA_R.ams_failedDeleteEdittingData + errormsg);
			},
			onException: function(r, t){
				msg.error(ISA_R.ams_failedDeleteEdittingData + getErrorMessage(t));
			}
		};
		AjaxRequest.invoke(url, opt);
	}
	ISA_DefaultPanel.defaultPanel = null;
});
IS_EventDispatcher.addListener("clearAdminCache", "", function(){
	IS_Event.unloadCache("_adminPanel");
	IS_Event.unloadCache("_adminPanelTab");
});
ISA_DefaultPanel.prototype.classDef = function() {
	var self = this;
	var container;
	var loadingMessage;
	var controlModal;

	var commandBarTabId = "commandbar";
	var ignoreTabIdList = [commandBarTabId, "0"];
	var defaultRoleRegex = "default";
	var defaultRoleName = "defaultRole";
	var defaultDefaultUid = "default";
	var commandBarMap = {
		"Ticker":{id:"p_1_w_4", title:ISA_R.alb_Ticker, type:"Ticker", togglable:true, undeletable:true},
		"Ranking":{id:"p_1_w_6", title:ISA_R.alb_ranking, type:"Ranking", togglable:true, undeletable:true},
		"portal-go-home":{id:"portal-go-home", title:ISA_R.alb_toTopPage, togglable:true, undeletable:true,
			togglableConfirm:function(element){
				if(!element.checked){
					return confirm(ISA_R.ams_confirmNoTop1+"\n"+
					ISA_R.ams_confirmNoTop2+"\n"+
					ISA_R.ams_confirmNoTop3);
				}
				return true;
			}
		},
		"portal-change-fontsize":{id:"portal-change-fontsize", title:ISA_R.alb_changeFont, togglable:true, undeletable:true},
		"portal-trash":{id:"portal-trash", title:ISA_R.alb_trashBox, togglable:true, undeletable:true},
		"portal-preference":{id:"portal-preference", title:ISA_R.alb_setupAll, togglable:true, undeletable:true},
		"portal-credential-list":{id:"portal-credential-list", title:ISA_R.alb_credentialList, togglable:true, undeletable:true},
		"portal-admin-link":{id:"portal-admin-link", title:ISA_R.alb_adminLink, togglable:true, undeletable:true},
		"portal-logout":{id:"portal-logout",title:ISA_R.alb_logout,togglable:true,undeletable:true}
	};
	this.commandBarEditor = {
		Ticker : {
			buildForm : function(commandItem){
				if(commandItem){
					var jsonObj = self.displayRoleJsons[self.displayRoleId].staticPanel[commandItem.id];
					url = jsonObj.properties.url;
				}
				if(!url) {
					url = "";
				}

				var editAreaDiv = document.createElement("div");
				editAreaDiv.id = "panelAddCommandBarInputWrap";

				var dialogTable = new ISA_RightLeftAlignTable()
				var editUrlInput = document.createElement("input");
				editUrlInput.id = "panelAddCommandBarURL";
				editUrlInput.type = "text";
				editUrlInput.size = 70;
				editUrlInput.value = url;
				editUrlInput.maxLength = 1024;
				dialogTable.addRow(ISA_R.alb_rssFeedUrl, editUrlInput);

				editAreaDiv.appendChild(dialogTable.build());
				return editAreaDiv;
			},
			onOK : function(commandItem){
				var roleJson = self.displayRoleJsons[self.displayRoleId];
				var url = ISA_Admin.trim( $("panelAddCommandBarURL").value);
				if(!ISA_Admin.trim(url)){
					alert(ISA_R.ams_typeRSSfeedingURL);
					return false;
				}
				var jsonObj = roleJson.staticPanel[commandItem.id];
				jsonObj.properties.url = url;
				self.replaceCommandBarWidget( roleJson,jsonObj );
				return true;
			}
		},
		Ranking :{
			buildForm : function(commandItem){
				if(commandItem){
					var jsonObj = self.displayRoleJsons[self.displayRoleId].staticPanel[commandItem.id];
					urls = jsonObj.properties.urls;
				}
				if(!urls) {
					urls = "<urls></urls>";
				}

				var editAreaDiv = document.createElement("div");
				editAreaDiv.id = "panelAddCommandBarInputWrap";

				var dialogTable = new ISA_RightLeftAlignTable()
				var editHTMLInput = document.createElement("textarea");
				editHTMLInput.id = "panelAddHTMLCommandBarURLs";
				editHTMLInput.style.width = "300px";
				editHTMLInput.style.height = "200px";
				editHTMLInput.value = urls;
				dialogTable.addRow(ISA_R.alb_rankingSetting, editHTMLInput);

				editAreaDiv.appendChild(dialogTable.build());
				return editAreaDiv;
			},
			onOK : function(commandItem){
				var roleJson = self.displayRoleJsons[self.displayRoleId];
				var urls = ISA_Admin.trim( $("panelAddHTMLCommandBarURLs").value);
				if(!ISA_Admin.trim(urls)){
					alert(ISA_R.ams_typeRankingSettings);
					return false;
				}
				var jsonObj = roleJson.staticPanel[commandItem.id];
				jsonObj.properties.urls = urls;
				self.replaceCommandBarWidget( roleJson,jsonObj );
				return true;
			}
		},
		link :{
			buildForm : function(commandItem){
				if(commandItem){
					var commandDiv = $(commandItem.id);
					var commandA = commandDiv.firstChild;
					title = commandDiv.getAttribute("label");
					link = commandA.href;
					isNewwindow = commandA.target == "_blank";
					isIframe = commandA.target == "ifrm";
				} else {
					title = ISA_R.alb_newLink;
					link = "http://";
					isNewwindow = true;
					isIframe = false;
				}

				var editAreaDiv = document.createElement("div");
				editAreaDiv.id = "panelAddCommandBarInputWrap";

				var dialogTable = new ISA_RightLeftAlignTable()
				var editTitleInput = document.createElement("input");
				editTitleInput.id = "panelAddCommandBarTitle";
				editTitleInput.type = "text";
				editTitleInput.size = 30;
				editTitleInput.value = title;
				dialogTable.addRow(ISA_R.alb_displayingName, editTitleInput);

				var editURLInput = document.createElement("input");
				editURLInput.id = "panelAddCommandBarUrl";
				editURLInput.type = "text";
				editURLInput.size = 60;
				editURLInput.value = link;
				editURLInput.maxLength = 1024;
				dialogTable.addRow(ISA_R.alb_linkUrl, editURLInput);

				var windowTargetDiv = document.createElement('div');
				var newWindowRadio = ISA_Admin.createBaseRadio("panelAddCommandBarTarget", isNewwindow);
				newWindowRadio.id = "panelAddCommandBarNewwindow";
				windowTargetDiv.appendChild(newWindowRadio);
				windowTargetDiv.appendChild(document.createTextNode(ISA_R.alb_newWindow));

				var iFrameRadio = ISA_Admin.createBaseRadio("panelAddCommandBarTarget", isIframe);
				iFrameRadio.id = "panelAddCommandBarIFrame";
				windowTargetDiv.appendChild(iFrameRadio);
				windowTargetDiv.appendChild(document.createTextNode(ISA_R.alb_portalframe));

				dialogTable.addRow(ISA_R.alb_linkDisplayOn, windowTargetDiv);

				editAreaDiv.appendChild(dialogTable.build());
				return editAreaDiv;
			},
			onOK : function(commandItem){
				var commandDiv = document.createElement("div");
				commandDiv.id = commandItem ? commandItem.id : "p_" + new Date().getTime() + "_w_1";
				var title = ISA_Admin.trim( $("panelAddCommandBarTitle").value);
				commandDiv.setAttribute("label", title);
				commandDiv.setAttribute("type", "link");
				commandDiv.style.whiteSpace = "nowrap";

				var commandA = document.createElement("a");
				commandA.appendChild(document.createTextNode(title));
				commandA.href = ISA_Admin.trim( $("panelAddCommandBarUrl").value );
				if($("panelAddCommandBarNewwindow").checked){
					commandA.target = "_blank";
				} else {
					commandA.target = "ifrm";
					commandA.setAttribute("onclick","IS_Portal.buildIFrame();");
				}

				commandDiv.appendChild(commandA);

				self.addCommandBar(commandDiv);
				return true;
			}
		},
		html :{
			buildForm : function(commandItem){
				if(commandItem) {
					title = commandItem.title;
					html = $(commandItem.id).innerHTML;
				}else{
					title = ISA_R.alb_newContents;
					html = "";
				}
				var editAreaDiv = document.createElement("div");
				editAreaDiv.id = "panelAddHTMLCommandBarInputWrap";

				var dialogTable = new ISA_RightLeftAlignTable()
				var editTitleInput = document.createElement("input");
				editTitleInput.id = "panelAddHTMLCommandBarTitle";
				editTitleInput.type = "text";
				editTitleInput.size = 30;
				editTitleInput.value = title;
				dialogTable.addRow(ISA_R.alb_displayingName, editTitleInput);

				var editHTMLInput = document.createElement("textarea");
				editHTMLInput.id = "panelAddHTMLCommandBarHTML";
				editHTMLInput.style.width = "500px";
				editHTMLInput.style.height = "400px";
				editHTMLInput.value = html;
				dialogTable.addRow("HTML", editHTMLInput);

				editAreaDiv.appendChild(dialogTable.build());

				return editAreaDiv;
			},
			onOK : function(commandItem){
				var title = $("panelAddHTMLCommandBarTitle").value;
				if(!ISA_Admin.trim(title)){
					alert(ISA_R.ams_typeTitle);
					return false;
				}
				var htmlInput = $("panelAddHTMLCommandBarHTML");
				var commandDiv = document.createElement("div");
				commandDiv.id = commandItem ? commandItem.id : "p_" + new Date().getTime() + "_w_1";
				commandDiv.setAttribute("label", title);
				commandDiv.setAttribute("type", "html");
				commandDiv.innerHTML = htmlInput.value;

				self.addCommandBar(commandDiv);
				return true;
			}
		}
	}

	this.replaceCommandBarWidget = function( roleJson,jsonObj ) {
		var oldId = jsonObj.id;
		jsonObj.id = "w_"+ new Date().getTime();

		delete roleJson.staticPanel[oldId];
		roleJson.staticPanel[jsonObj.id] = jsonObj;
		roleJson.layout = roleJson.layout.replace( new RegExp( oldId,"g"),jsonObj.id );

		this.isUpdated = true;
		ISA_Admin.isUpdated = true;
		this.buildCommandWidgetsList( roleJson );
		this.addSortableEventCommand();
	}

	this.initialize = function() {
		//
		controlModal = new Control.Modal(
			false,
			{
				contents: ISA_R.ams_applyingChanges,
				opacity: 0.2,
				containerClassName:"commitDialog",
				overlayCloseOnClick:false
			}
		);
		//
		container = document.getElementById("defaultPanel");

		/**
		 * Delete trush
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

		// Tab ID currently displayed
		this.displayTabId = false;
		// Tab Number currently displayed
		this.displayTabNumber = false;
	};

	this.displayDefaultPanel = function() {
		// Permission ID currently displayed
		this.displayRoleId = false;
		this.displayRoleOrder = false;
		// Flag for update
		this.isUpdated = false;

		// Dsiplaying Top tab initialy
		this.buildTabs();
		container.replaceChild(this.panelTabsContainer.tabContainer, loadingMessage);

		var previewDivWrap = document.createElement("div");
		previewDivWrap.style.clear = "both";
		previewDivWrap.style.width = "98%";

		container.insertBefore(previewDivWrap, this.panelTabsContainer.tabContainer);
		var previewDiv = ISA_Admin.createIconButton(ISA_R.alb_previewTop, ISA_R.alb_previewTop, "minibrowser.gif", "right");
		previewDivWrap.appendChild(previewDiv);
		IS_Event.observe(previewDiv, 'click', ISA_previewFormModal.init, false, "_adminPanel");

		var commitDiv = ISA_Admin.createIconButton(ISA_R.alb_changeApply, ISA_R.alb_changeApply, "database_save.gif", "right");
		previewDivWrap.appendChild(commitDiv);
		IS_Event.observe(commitDiv, 'click', self.commitPanel.bind(this), false, "_adminPanel");

		var resetDiv = ISA_Admin.createIconButton(
//			"Initialization of customizing information",
			ISA_R.alb_clearConfigurationButton,
//			"Initializing customizing information",
			ISA_R.alb_clearConfigurationDesc,
			"database_refresh.gif","right");
		previewDivWrap.appendChild( resetDiv );
		IS_Event.observe( resetDiv,"click",this.resetUserCustomization.bind( this ),false,"_adminPanel");
		this.tab = new Control.Tabs("panelTabs",{
			defaultTab: "tab_"+commandBarTabId,
			beforeChange: function( old_container,container ) {
				if(self.changeTab( container.id.substring(4),false ))
				throw $break;
			}
		});
	}

	this.commitPanel = function(){
		if(!self.updatePanel(true)) return;

		controlModal.open();

		var url = findHostURL() + "/services/tabLayout/commitDefaultPanel";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			asynchronous:true,
			onSuccess: function(response){
				controlModal.update(ISA_R.ams_changeUpdated);
				ISA_Admin.isUpdated = false;
			},
			onFailure: function(t) {
				var errormsg = t.responseText && typeof t.responseText == "string" ? t.responseText.substr(0, 100) : "";
				alert(ISA_R.ams_failedToSaveTop+'\n' + errormsg);
				msg.error( ISA_R.ams_failedToSaveTop+ errormsg);
			},
			onException: function(r, t){
				alert(ISA_R.ams_failedToSaveTop);
				msg.error(ISA_R.ams_failedToSaveTop + getErrorMessage(t));
			},
			onComplete: function(){
				setTimeout(function(){
					controlModal.close();
				},500);
			}
		};
		AjaxRequest.invoke(url, opt);
	};

	this.resetUserCustomization = function() {
		var content = document.createElement("div");
		content.className = "resetConfigurations";

		var modal = new Control.Modal( false,{
			contents: content,
			opacity: 0.2,
			overlayCloseOnClick: true
		});

		var description = document.createElement("div");
		description.className = "resetConfigurations-description"
		description.innerHTML = ISA_R.alb_clearConfigurationChooseUser;
		content.appendChild( description );

		var form = document.createElement("div");
		form.className = "resetConfigurations-form";
		var formLabel = document.createElement("span");
		formLabel.innerHTML = ISA_R.alb_userName +":";
		form.appendChild( formLabel );
		var formInput = document.createElement("input");
		form.appendChild( formInput );
		content.appendChild( form );

		var commands = document.createElement("div");
		commands.className = "resetConfigurations-commands";
		content.appendChild( commands );

		var okButton = document.createElement("input");
		okButton.type = "button";
		okButton.value = "OK";
		commands.appendChild( okButton );
		IS_Event.observe( okButton,"click",function() {
			var uid = formInput.value;
			modal.close();
			if( uid == "" || /^[ 　]+$/.test( uid ) )
				return;

			this._resetUserCustomization( uid );
		}.bind( this ),false,"_adminPanel");

		var cancelButton = document.createElement("input");
		cancelButton.type = "button";
		cancelButton.value = "Cancel";
		commands.appendChild( cancelButton );
		IS_Event.observe( cancelButton,"click",function() { modal.close() },false,"_adminPanel");

		modal.open();
	}
	this._resetUserCustomization = function( uid ) {
		var url = findHostURL() + "/services/tab/clearConfigurations";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			postBody: Object.toJSON([ uid ]),
			asynchronous:true,
			onSuccess: function(response){
			},
			onFailure: function(t) {
				var errMsg = IS_R.ms_clearConfigurationFailed+ "\n" +
					((t.responseText && typeof t.responseText == "string") ? t.responseText.substr(0, 100) : "");
				alert( errMsg );
				msg.error( errMsg );
			},
			onException: function(r, t){
				var errMsg = IS_R.ms_clearConfigurationFailed +getErrorMessage(t);
				alert( errMsg );
				msg.error( errMsg );
			},
			onComplete: function(){
				setTimeout(function(){
					controlModal.close();
				},500);
			}
		};
		AjaxRequest.invoke(url, opt);
	}

	/**
		Create a tab
	*/
	this.buildTabs = function() {
		this.panelTabsContainer = null;

		var tabsDiv = document.createElement("div");
		tabsDiv.style.clear = "both";

		var tabsUl = document.createElement("ul");
		tabsUl.id = "panelTabs";
		tabsUl.className = "tabs";

		for(var i=0; i<this.tabIdList.length; i++){
			tabsUl.appendChild(this.buildTab(this.tabIdList[i]));
		}
		// Adding tab
		var addTabDiv = document.createElement("li");
		addTabDiv.noWrap = "-1";
		addTabDiv.className = "addatab";
		addTabDiv.id = "addTabDiv";
		var addA = document.createElement("a");
		addA.innerHTML = ISA_R.alb_addTab;
		addTabDiv.appendChild(addA);
		tabsUl.appendChild(addTabDiv);
		var addTabHandler = function(e) {
			//Commiting update
			self.updatePanel( true );

			var datetime = new Date().getTime();
			var jsonObject = {
				id : String( datetime ),
				tabId : "",
			  tabName : ISA_R.alb_newTab,
				columnsWidth : "",
				principalType : null,
				role : defaultRoleRegex,
				roleName : defaultRoleName,
				defaultUid : defaultDefaultUid,
				layout : "",
				staticPanel : {},
				dynamicPanel : {},
				widgetsLastmodified : null,
				tabNumber : "",
				isDefault : true
			};
			self.displayRoleJsons = {};
			self.displayRoleJsons[datetime] = jsonObject;
			self.displayRoleId = jsonObject.id;
			self.displayRoleOrder = jsonObject.roleOrder;
			// Register event
			IS_EventDispatcher.addListener('updatePanelOnSuccess', 'default', self.addAfter, true);
			jsonObject = self.setColumnsArray(jsonObject);
			self.isUpdated = true;
			
			// Set default fixed area
			jsonObject = self.templates.setStaticLayout0(jsonObject);
			
			self.updatePanel( false );
			
			ISA_Admin.isUpdated = true;
		};
		IS_Event.observe(addA, 'click', addTabHandler, false, "_adminPanel");
		tabsDiv.appendChild(tabsUl);

		// Create displaying area of tab contents
		var tabContentsDiv = document.createElement("div");
		tabContentsDiv.id = "panelTabContents";
		tabContentsDiv.className = "panelTabContents";
		tabsDiv.appendChild(tabContentsDiv);
		
		for(var i=0; i<this.tabIdList.length; i++){
			var tabContentDiv = document.createElement("div");
			tabContentDiv.id = "tab_"+this.tabIdList[i];
			
			tabContentsDiv.appendChild( tabContentDiv );
		}
		
		this.panelTabsContainer = {tabContainer:tabsDiv, tabContents:tabContentsDiv};
	}

	this.addAfter = function() {
		var tabsUl = $("panelTabs");
		if(!tabsUl) return;

		var tabid = self.displayTabId;
		var tabnum = self.displayTabNumber;
		// Set the values obatined from response
		self.displayRoleJsons[self.displayRoleId].tabId = tabid;
		self.displayRoleJsons[self.displayRoleId].tabNumber = tabnum;
		self.tabIdList.push(tabid);
		self.tabNumberJson[tabid] = {id: tabnum};
		// Insert before [Add tab] 
		tabsUl.insertBefore(self.buildTab(tabid,true), tabsUl.lastChild);
		
		var tabContentsDiv = $("panelTabContents");
		var tabContentDiv = document.createElement("div");
		tabContentDiv.id = "tab_"+tabid;
		
		tabContentsDiv.appendChild( tabContentDiv );
		
		// Change display to the tab added at the last minute
		self.tab.addTab($("panelTab_"+tabid ));
		self.tab.setActiveTab("tab_"+tabid );
	}

	/**
		Create a tab
	*/
	var currentMaxTabNumber = 0;
	this.buildTab = function(tabId,add) {
		var tabDiv = document.createElement("li");

		var tabAnchor = document.createElement("a");
		tabAnchor.id = "panelTab_" + tabId;
		tabAnchor.className = "tab";
		tabAnchor.href = "#tab_"+tabId;
		
		var tabTitleSpan = document.createElement("span");
		tabTitleSpan.className = 'title';
		tabTitleSpan.id = "panelTab_" + tabId + "_Title";
		// Genarate tab name
		var tabName;
		if(!isNaN(tabId)) {
			tabName = 'tab' + currentMaxTabNumber;
			currentMaxTabNumber ++;
		} else {
			tabName = tabId;
		}
		tabTitleSpan.appendChild(document.createTextNode(tabName));
		tabAnchor.appendChild(tabTitleSpan);
		tabDiv.appendChild(tabAnchor);
		
		if( !add )
			this.setTabAttribute(tabAnchor, tabId);

		return tabDiv;
	}

	/**
		Switch tab
	*/
	this.changeTab = function(activeTabId, addTab) {
		if(!self.updatePanel()) return true;
		
		var link = $("panelTab_"+activeTabId );
		if( !link || link.hasClassName("selected"))
			return;
		
		// Replace to the tab ID currently displayed
		this.displayTabId = activeTabId;
		// Replace to the tab Number currently displayed
		this.displayTabNumber = this.tabNumberJson[activeTabId].id;
		IS_Event.unloadCache("_adminPanelTab");
		for(var i=0; i<this.tabIdList.length; i++){
			var tabDiv = $("panelTab_" + this.tabIdList[i]);
			this.setTabAttribute(tabDiv, this.tabIdList[i]);
		}
		
		self.panelTabsContainer.tabContents = $("tab_"+this.displayTabId );
//		if(addTab != true){
			this.getDefaultPanelJSONByTabId( this.displayTabId,this.buildTabContents.bind( this ) );
//		}else{
//			this.buildTabContents();
//		}
	}

	/**
		Set atrribute of tab
	*/
	var displayDeleteImgSpan = false;
	this.setTabAttribute = function(targetTabDiv, targetTabId) {
		// Create delete button
		if(!displayDeleteImgSpan){
			displayDeleteImgSpan = document.createElement("span");
			var deleteImg = document.createElement("img");
			deleteImg.src = imageURL+"x.gif";
			deleteImg.style.verticalAlign = 'middle';
			deleteImg.style.cursor = 'pointer';
			deleteImg.title = ISA_R.ams_deleteThisTab;
			displayDeleteImgSpan.appendChild(deleteImg);
		}
		//
		if(targetTabId != this.displayTabId){
			targetTabDiv.className = "tab";
//			IS_Event.observe(targetTabDiv, "click", this.changeTab.bind(this, targetTabId), false, ["_adminPanelTab","_adminPanel"]);
		}else{
			targetTabDiv.className = "tab selected";
			// The tab can diplay delete button or not.
			var isIgnored = false;
			for(var i = 0; i < ignoreTabIdList.length; i++) {
				if(ignoreTabIdList[i] == this.displayTabId) {
					isIgnored = true;
					break;
				}
			}
			if(!isIgnored) {
				targetTabDiv.className += " deletable";
				targetTabDiv.firstChild.appendChild(displayDeleteImgSpan);
				IS_Event.observe(displayDeleteImgSpan, "click", this.deleteTab.bind(this, targetTabDiv, targetTabId), true, ["_adminPanelTab","_adminPanel"]);
			} else {
				if(displayDeleteImgSpan.parentNode)
					displayDeleteImgSpan.parentNode.removeChild(displayDeleteImgSpan);
			}
		}
	}

	/**
		Delete tab
	*/
	this.deleteTab = function(targetTabDiv, targetTabId,event ) {
		//self.isUpdated = true;
		if( event ) Event.stop( event );
		if( !confirm(ISA_R.ams_confirmDeleting) ) {
			return;
		}
		//controlModal.open();
		Element.remove(targetTabDiv);
		Element.remove($("tab_"+targetTabId ) );
		this.tabIdList = this.tabIdList.without(targetTabId);
		delete this.tabNumberJson[targetTabId];

		this.removeDefaultPanel(this.displayTabId );
		this.displayRoleJsons = null;
		
		currentMaxTabNumber = 0;
		this.tabIdList.each( function( tabId ) {
			var tabTitleSpan = $("panelTab_" + tabId + "_Title");
			
			var tabName;
			if(!isNaN(tabId)) {
				tabName = 'tab' + currentMaxTabNumber;
				currentMaxTabNumber ++;
			} else {
				tabName = tabId;
			}
			tabTitleSpan.firstChild.nodeValue = tabName;
		})
		this.tab.setActiveTab("tab_"+commandBarTabId );
		ISA_Admin.isUpdated = true;
	}

	/**
		Create tab contents
	*/
	this.buildTabContents = function() {

		var defaultPanelDiv = document.createElement("div");
		defaultPanelDiv.style.clear = "both";

		var refreshAllDiv = document.createElement("div");
		refreshAllDiv.id = "refreshAll";
		refreshAllDiv.style.textAlign = "right";
		refreshAllDiv.style.width = "100%";
		defaultPanelDiv.appendChild(refreshAllDiv);

		var dummyDiv = document.createElement("div");
		dummyDiv.style.clear = "both";
		defaultPanelDiv.appendChild(dummyDiv);

		var backDiv = ISA_Admin.createIconButton(ISA_R.alb_backToList, ISA_R.alb_backToList, "back.gif", "right");
		backDiv.id = "tab_"+this.displayTabId+"_backListPanel";
		backDiv.style.display = "none";
		refreshAllDiv.appendChild(backDiv);
		IS_Event.observe(backDiv, 'click', self.backToList.bind(self, true), ["_adminPanelTab","_adminPanel"]);

		var defaultPanelTable = document.createElement("table");
		defaultPanelTable.id = "panelTabContentsTable";
		defaultPanelTable.style.width = "100%";
		defaultPanelDiv.appendChild(defaultPanelTable);

		var defaultPanelTbody = document.createElement("tbody");
		defaultPanelTable.appendChild(defaultPanelTbody);

		var defaultPanelTr = document.createElement("tr");
		defaultPanelTbody.appendChild(defaultPanelTr);

		var defaultPanelTdLeft = document.createElement("td");
		defaultPanelTdLeft.id = "roleListTd";
		defaultPanelTdLeft.style.padding = "5px";
		//defaultPanelTdLeft.style.width = "40%";
		defaultPanelTdLeft.style.verticalAlign = "top";
		defaultPanelTr.appendChild(defaultPanelTdLeft);

		var roleListDiv = self.buildRoleList();

		defaultPanelTdLeft.appendChild(roleListDiv);

		var roleEditDiv = document.createElement("div");
		roleEditDiv.id = "tab_"+this.displayTabId+"_roleEditPanel";
		roleEditDiv.style.display = "none";

		var editAreaDiv = self.buildEditArea();
		roleEditDiv.appendChild(editAreaDiv);
		defaultPanelTdLeft.appendChild(roleEditDiv);

		if(self.panelTabsContainer.tabContents.firstChild) {
			self.panelTabsContainer.tabContents.replaceChild(defaultPanelDiv, self.panelTabsContainer.tabContents.firstChild);
		} else {
			self.panelTabsContainer.tabContents.appendChild(defaultPanelDiv);
		}

		this.addSortableEvent();
	}

	/**
		Create permission list
	*/
	this.buildRoleList = function() {
		var roleListContainer = document.createElement("div");
		roleListContainer.id = "tab_"+this.displayTabId+"_roleListPanel";

		var addDefaultDiv = ISA_Admin.createIconButton(ISA_R.alb_add, ISA_R.alb_add, "add.gif", "left");
		addDefaultDiv.id = "addDefault";
		addDefaultDiv.style.cssFloat = "left";
		addDefaultDiv.style.styleFloat = "left";
		var addRoleHandler = function(e) {
			var datetime = new Date().getTime();

			var principalMap = ISA_Principals.get();
			var jsonObject = {
				id : String(datetime),
				tabId : self.displayTabId,
				tabName : (( self.displayTabId == 0 )? ISA_R.alb_home : ISA_R.alb_newTab),
				columnsWidth : "",
				principalType : (principalMap.length > 1) ? principalMap[1].type : principalMap[0].type,
				role : ISA_R.alb_newRegularExpression,
				roleName : ISA_R.alb_newRole,
				roleOrder : ++self.maxRoleOrder,
				defaultUid : String(datetime),
				layout : "",
				staticPanel : {},
				dynamicPanel : {},
				widgetsLastmodified : null,
				tabNumber : self.tabNumberJson[self.displayTabId].id
			};
			// Defalut fixed area setting.
			if(jsonObject.tabId == commandBarTabId) {
				jsonObject = self.templates.setCommandLayout(jsonObject);
			} else {
				jsonObject = self.templates.setStaticLayout0(jsonObject);
				jsonObject = self.setColumnsArray(jsonObject);
			}

			var newJsons = {};
			var oldJsons = self.displayRoleJsons;
			var defaultRole = null;
			for(var i in oldJsons) {
				if( !(oldJsons[i] instanceof Function) ){
					if( oldJsons[i].defaultUid == defaultDefaultUid){
						defaultRole = oldJsons[i];
						continue;
					}
					newJsons[i] = oldJsons[i];
				}
			}
			newJsons[datetime] = jsonObject;
			newJsons[defaultRole.id] = defaultRole;
			self.displayRoleJsons = newJsons;

			var roleParent = document.getElementById("tab_"+self.displayTabId+"_roleGroup");
			if(!roleParent) return;
			// Added at last
			roleParent.appendChild(self.buildRole(jsonObject));
			// Change display to the permission added at the last minute
			self.editRole(jsonObject);
			self.isUpdated = true;
			ISA_Admin.isUpdated = true;
		};
		IS_Event.observe(addDefaultDiv, 'click', addRoleHandler, false, ["_adminPanelTab","_adminPanel"]);

		roleListContainer.appendChild(addDefaultDiv);

		var defaultRoleJson;
		for(var i in this.displayRoleJsons) {
			if( (this.displayRoleJsons[i] instanceof Function) ) continue;
			if(this.displayRoleJsons[i].isDefault)
			  defaultRoleJson = this.displayRoleJsons[i];
		}
		if( !(self.displayTabId == 'commandbar' || self.displayTabId == '0') ){
			var disabledDefualtDiv = document.createElement('div');
			disabledDefualtDiv.className = 'iconButton';
			disabledDefualtDiv.style.cssFloat = "left";
			disabledDefualtDiv.style.styleFloat = "left";
			var disableDefaultCheckbox = document.createElement('input');
			disableDefaultCheckbox.type = 'checkbox';
			disableDefaultCheckbox.defaultChecked = defaultRoleJson.disabledDefault
			  disabledDefualtDiv.appendChild(disableDefaultCheckbox);
			disabledDefualtDiv.appendChild(document.createTextNode(ISA_R.alb_noDefault));
			IS_Event.observe(disableDefaultCheckbox, "click", function(checkbox, defaultRoleJson){
				// Update to public object
				defaultRoleJson.disabledDefault = checkbox.checked;
				if(checkbox.checked)
				  Element.hide("tab_"+self.displayTabId+'_role_' + defaultRoleJson.roleOrder);
				else
				  Element.show("tab_"+self.displayTabId+'_role_' + defaultRoleJson.roleOrder);
				this.isUpdated = true;
				ISA_Admin.isUpdated = true;
			}.bind(this, disableDefaultCheckbox, defaultRoleJson), false, ["_adminPanelTab","_adminPanel"]);

			roleListContainer.appendChild(disabledDefualtDiv);
		}

		var annotateDiv = document.createElement("div");
		annotateDiv.style.cssFloat = "right";
		annotateDiv.style.styleFloat = "right";
		annotateDiv.style.textAlign = "right";
		var font = document.createElement("font");
		font.size = "-1";
		font.color = "#ff0000";
		font.appendChild(document.createTextNode(ISA_R.alb_matchingFromTop));
		annotateDiv.appendChild(font);

		roleListContainer.appendChild(annotateDiv);


		roleListContainer.appendChild(ISA_Admin.buildTableHeader(
			[ISA_R.alb_order, ISA_R.alb_roleName, ISA_R.alb_subject, ISA_R.alb_regularExpression,ISA_R.alb_edit,ISA_R.alb_delete],
			['40px', '175px', '85px','350px', '40px', '40px']
			));

		var roleListDiv = document.createElement("div");
		roleListDiv.id = "tab_"+this.displayTabId+"_roleGroup";


		var defaultRoleListDiv = document.createElement("div");
		//defaultRoleListDiv.id = "defaultRoleGroup";

		annotateDiv.style.width = "400px";

		//roleListContainer.appendChild(roleListTable);
		roleListContainer.appendChild(roleListDiv);
		roleListContainer.appendChild(defaultRoleListDiv);

		self.maxRoleOrder = 0;
		// DefaultSearch build
		for(var i in this.displayRoleJsons) {
			if( (this.displayRoleJsons[i] instanceof Function) ) continue;

			var div = this.buildRole(this.displayRoleJsons[i]);
//			if(i == this.displayTabId + "_" + defaultRoleRegex){
			if(this.displayRoleJsons[i].isDefault){
				defaultRoleListDiv.appendChild(div);
			}else{
				roleListDiv.appendChild(div);
			}
			self.maxRoleOrder = Math.max(this.displayRoleJsons[i].roleOrder, self.maxRoleOrder);
		}

		return roleListContainer;
	}

	this.addSortableEvent = function(){
		var draggingDivId = false;
		Sortable.create($("tab_"+this.displayTabId+"_roleGroup"),
			{
				tag: 'div',
				handle: 'handle',
				onChange: function(div){
					draggingDivId = div.id;
				},
				onUpdate:function(div){
					var divIdPrefix = "tab_"+self.displayTabId+"_role_";
					var newJsons = {};
					var oldJsons = self.displayRoleJsons;
					var draggedDiv = $(draggingDivId);
					var draggedRoleId = draggedDiv.roleId;
					var siblingDivId = (draggedDiv.previousSibling) ? draggedDiv.previousSibling.id : false;

					if(!siblingDivId){
						newJsons[draggedRoleId] = oldJsons[draggedRoleId];
					}
					for(var i in oldJsons) {
						if( !(oldJsons[i] instanceof Function) ){
							var divId = divIdPrefix + oldJsons[i].roleOrder;
							if( siblingDivId && divId == draggingDivId)continue;
							newJsons[i] = oldJsons[i];
							if( siblingDivId && divId == siblingDivId){
								newJsons[draggedRoleId] = oldJsons[draggedRoleId];
							}
						}
					}
					self.displayRoleJsons = newJsons;
					self.isUpdated = true;
					ISA_Admin.isUpdated = true;
				}
			}
		);
	}
	/**
		Generate permission
	*/
	this.buildRole = function(jsonRole) {
		jsonRole = this.setColumnsArray(jsonRole);

		var roleDiv = document.createElement("div");
		roleDiv.id = "tab_"+this.displayTabId+"_role_" + jsonRole.roleOrder;
		roleDiv.roleId = jsonRole.id;

		var table = document.createElement("table");
		table.className = "proxyConfigList";
		table.cellPadding = "0";
		table.cellSpacing = "0";
		table.border = "0";
		table.style.width = "900px";
		table.style.tableLayout = "fixed";
		roleDiv.appendChild(table);

		var tbody = document.createElement("tbody");
		table.appendChild(tbody);

		var tr = document.createElement("tr");
		//tr.className = "panelRoleTr";
		tbody.appendChild(tr);

		var sortableTd = document.createElement("td");
		//sortableTd.className = "panelRoleTd";
		sortableTd.style.width = "40px";
		sortableTd.style.textAlign = 'center';
//		if(jsonRole.id != this.displayTabId + "_" + defaultRoleRegex){
		if(!jsonRole.isDefault){
			var imgSrc = imageURL +"drag.gif";

			var sortableImg;
			if( Browser.isIE ) {
				sortableImg = document.createElement("span");
				sortableImg.style.backgroundImage = "url( "+imgSrc+" )";
			} else {
				sortableImg = document.createElement("img");
				sortableImg.src = imgSrc;
			}
			sortableImg.style.width = 16;
			sortableImg.style.height = 16;
			sortableImg.style.cursor = "pointer";
			sortableImg.title = ISA_R.alb_changingOrder;

			sortableTd.appendChild( sortableImg );
		}
		tr.appendChild(sortableTd);

		var roleNameTd = document.createElement("td");
		//roleNameTd.className = "panelRoleTd";
		roleNameTd.style.width = "175px";
		tr.appendChild(roleNameTd);
		var textDiv1 = document.createElement("div");
		roleNameTd.appendChild(textDiv1);
		textDiv1.appendChild(document.createTextNode(jsonRole.roleName));
		var clickTextHandler = function(e) {
			ISA_Admin.isUpdated = true;
			self.displayRoleId = jsonRole.id;
			self.displayRoleOrder = jsonRole.roleOrder;
			self.buildInput(textDiv1, "roleName", self.displayRoleId,{
				label: ISA_R.alb_roleName,
				maxBytes:256
			});
		};
		if(jsonRole.defaultUid != defaultDefaultUid)
			IS_Event.observe(textDiv1, 'click', clickTextHandler, false, ["_adminPanelTab","_adminPanel"]);

		var roleTypeTd = document.createElement("td");
		roleTypeTd.style.width = "85px";

		if(jsonRole.defaultUid != defaultDefaultUid){
			var selectPrincipal = document.createElement("select");
			selectPrincipal.className = "panelPrincipalName";
			selectPrincipal.style.width = '100px';
			var principalMap = ISA_Principals.get();
			for(var i = 0; i < principalMap.length; i++) {
				var opt = document.createElement("option");
				opt.value = principalMap[i].type;
				opt.innerHTML = principalMap[i].displayName;
				if(principalMap[i].type == jsonRole.principalType) {
					opt.selected = true;
				}
				selectPrincipal.appendChild( opt );
			}

			IS_Event.observe(selectPrincipal,
						 'change',
						 function (e){
						 	ISA_Admin.isUpdated = true;
						 	self.displayRoleId = jsonRole.id;
							self.displayRoleOrder = jsonRole.roleOrder;
							self.setNewValue("principalType", selectPrincipal.value);
						 },
						 false,
						 ["_adminPanelTab","_adminPanel"]);
			roleTypeTd.appendChild(selectPrincipal);
		}else{
			roleTypeTd.appendChild(document.createTextNode("　"));
		}
		tr.appendChild(roleTypeTd);

		var roleTd = document.createElement("td");
		//roleTd.className = "panelRoleTd";
		roleTd.style.width = "350px";
		tr.appendChild(roleTd);
		var textDiv2 = document.createElement("div");
		roleTd.appendChild(textDiv2);
		textDiv2.appendChild(document.createTextNode(jsonRole.role));
		var clickTextHandler = function(e) {
			ISA_Admin.isUpdated = true;
			self.displayRoleId = jsonRole.id;
			self.displayRoleOrder = jsonRole.roleOrder;
			self.buildInput(textDiv2, "role", self.displayRoleId, {
				label: ISA_R.alb_regularExpression,
				format:"regexp"
			});
		};
		if(jsonRole.defaultUid != defaultDefaultUid)
			IS_Event.observe(textDiv2, 'click', clickTextHandler, false, ["_adminPanelTab","_adminPanel"]);

		var editTd = document.createElement("td");
		//editTd.className = "panelRoleTd";
		editTd.style.width = "40px";
		editTd.style.textAlign = "center";
		tr.appendChild(editTd);
		var editImg = document.createElement("img");
		editImg.src = imageURL + "edit.gif";
		editImg.style.cursor = "pointer";
		editImg.title = ISA_R.alb_editing;
		editTd.appendChild(editImg);
		IS_Event.observe(editImg, "click", this.editRole.bind(this, jsonRole, roleDiv), false, ["_adminPanelTab","_adminPanel"]);

		var deleteTd = document.createElement("td");
		//deleteTd.className = "panelRoleTd";
		deleteTd.style.width = "40px";
		deleteTd.style.textAlign = "center";
		tr.appendChild(deleteTd);
		if(jsonRole.defaultUid != defaultDefaultUid) {
			var deleteImg = document.createElement("img");
			deleteImg.src = imageURL + "trash.gif";
			deleteImg.style.cursor = "pointer";
			deleteImg.title = ISA_R.alb_deleting;
			deleteTd.appendChild(deleteImg);
			IS_Event.observe(deleteImg, "click", this.deleteRole.bind(this, jsonRole), false, ["_adminPanelTab","_adminPanel"]);
		}else if(jsonRole.disabledDefault){
		  Element.hide(roleDiv);
		}

		return roleDiv;
	}

	/**
		Edit permission
	*/
	this.editRole = function(jsonRole) {
		ISA_Admin.isUpdated = true;
		// To edit page
		this.backToList(false);

		var roleNameText = $("tab_"+this.displayTabId+"_panelRoleNameText");
		if(roleNameText) roleNameText.value = ISA_Admin.replaceUndefinedValue(jsonRole.roleName);
		if(jsonRole.defaultUid == defaultDefaultUid)
			roleNameText.disabled = true;
		else
			roleNameText.disabled = false;

		var roleTypeSelect = $("tab_"+this.displayTabId+"_panelPrincipalType");
		if(roleTypeSelect) roleTypeSelect.value = ISA_Admin.replaceUndefinedValue(jsonRole.principalType);
		if(jsonRole.defaultUid == defaultDefaultUid)
			roleTypeSelect.disabled = true;
		else
			roleTypeSelect.disabled = false;

		var regExpText = $("tab_"+this.displayTabId+"_panelRegExpText");
		if(regExpText) regExpText.value = ISA_Admin.replaceUndefinedValue(jsonRole.role);
		if(jsonRole.defaultUid == defaultDefaultUid)
			regExpText.disabled = true;
		else
			regExpText.disabled = false;

		var tabNameText = $("tab_"+this.displayTabId+"_panelTabNameText");
		if(tabNameText)
			tabNameText.value = ISA_Admin.replaceUndefinedValue(jsonRole.tabName);

		var dynamicDiv = $("tab_"+this.displayTabId+"_panelDynamicDiv");
		var tabNameDiv = $("tab_"+this.displayTabId+"_tabNameDiv");
			
		// Replace to the permission ID currently displayed
		this.displayRoleId = jsonRole.id;
		this.displayRoleOrder = jsonRole.roleOrder;
		
		if(jsonRole.tabId == commandBarTabId) {
			this.buildCommandWidgetsList(jsonRole);
			this.addSortableEventCommand();
			if(dynamicDiv) dynamicDiv.style.display = "none";
			if(tabNameDiv) tabNameDiv.style.display = "none";
		} else {
			this.buildStaticWidgetsList(jsonRole.layout, jsonRole.staticPanel);
			this.buildDynamicWidgetsList(jsonRole);
			if(dynamicDiv) dynamicDiv.style.display = "";
			if(tabNameDiv) tabNameDiv.style.display = "";
			
			var panelUsage = ( jsonRole.disabledDynamicPanel ? (jsonRole.adjustToWindowHeight ? 'useStaticOnly_adjustHeight' : 'useStaticOnly' ) : 'useBothArea');
			$('panelUsageSelect').value = panelUsage;
			jsonRole.panelUsage = panelUsage;
			this.switchPanelDisplay(panelUsage);
		
		}

	}

	this.switchPanelDisplay =function(panelUsage){
		
		switch(panelUsage){
		  case 'useStaticOnly_adjustHeight':
			self.setNewValue("disabledDynamicPanel", true);
			self.setNewValue('adjustToWindowHeight', true);
			$("tab_"+self.displayTabId+"_personalizedAreaFieldSet").hide();
			break;
		  case 'useStaticOnly':
			self.setNewValue("disabledDynamicPanel", true);
			self.setNewValue('adjustToWindowHeight', false);
			$("tab_"+self.displayTabId+"_personalizedAreaFieldSet").hide();
			break;
		  default:
			self.setNewValue("disabledDynamicPanel", false);
			self.setNewValue('adjustToWindowHeight', false);
			$("tab_"+self.displayTabId+"_personalizedAreaFieldSet").show();
		}
	}
	
	/**
		Delete permission
	*/
	this.deleteRole = function(jsonRole) {
		// Update public object 
		delete this.displayRoleJsons[jsonRole.id];
		// Delete itself
		var deleteElement = document.getElementById("tab_"+this.displayTabId+"_role_" + jsonRole.roleOrder);
		if(deleteElement && deleteElement.parentNode)
			deleteElement.parentNode.removeChild(deleteElement);
		// Initialize permission ID currently displayed
		this.displayRoleId = false;
		this.displayRoleOrder = false;
		this.isUpdated = true;
		ISA_Admin.isUpdated = true;
	}

	/**
		Create editting area
	*/
	this.buildEditArea = function() {
		var editAreaDiv = document.createElement("div");

		// Name setting
		var editNamesDiv = document.createElement("div");
		editNamesDiv.style.width = "80%";
		editAreaDiv.appendChild(editNamesDiv);
		
		var editRoleNameDiv = document.createElement("div");
		editRoleNameDiv.style.padding = "3px";
		editRoleNameDiv.appendChild(document.createTextNode(ISA_R.alb_roleNameColon));
		var editRoleNameInput = document.createElement("input");
		editRoleNameInput.id = "tab_"+this.displayTabId+"_panelRoleNameText";
		editRoleNameInput.type = "text";
		editRoleNameInput.size = 30;
		var beforeNameText = false;
		IS_Event.observe(editRoleNameInput,
						 'change',
						 function (e){
							var nowText = ISA_Admin.trim( editRoleNameInput.value );
							if(nowText.length == 0) {
								editRoleNameInput.value = beforeNameText;
								editRoleNameInput.focus();
								return false;
							}
							var error = IS_Validator.validate(nowText, {maxBytes:256, label:ISA_R.alb_roleName});
							if(error){
								alert(error);
								editRoleNameInput.focus();
								return false;
							}
							self.setNewValue("roleName", nowText);
							var roleNameDiv = $("tab_"+self.displayTabId+"_role_" + self.displayRoleOrder).getElementsByTagName('td')[1];
							roleNameDiv.firstChild.innerHTML = nowText;
						 },
						 false,
						 ["_adminPanelTab","_adminPanel"]);
		IS_Event.observe(editRoleNameInput,
						 'focus',
						 function (e){
							beforeNameText = editRoleNameInput.value;
						 },
						 false,
						 ["_adminPanelTab","_adminPanel"]);
		editRoleNameDiv.appendChild(editRoleNameInput);
		editNamesDiv.appendChild(editRoleNameDiv);


		var editRoleTypeDiv = document.createElement("div");
		editRoleTypeDiv.style.padding = "3px";
		editRoleTypeDiv.appendChild(document.createTextNode(ISA_R.alb_subjectColon));
		var selectPrincipal = document.createElement("select");
		selectPrincipal.className = "panelPrincipalType";
		selectPrincipal.id = "tab_"+this.displayTabId+"_panelPrincipalType";
		var principalMap = ISA_Principals.get();
		var principalLength = principalMap.length;

		for(var i = 0; i < principalLength; i++) {
			var opt = document.createElement("option");
			opt.value = principalMap[i].type;
			opt.innerHTML = principalMap[i].displayName;
			selectPrincipal.appendChild( opt );
		}
		IS_Event.observe(selectPrincipal,
						 'change',
						 function (e){
							self.setNewValue("principalType", selectPrincipal.value);
							var roleTypeDiv = $("tab_"+self.displayTabId+"_role_" + self.displayRoleOrder).getElementsByTagName('select')[0];
							roleTypeDiv.value = selectPrincipal.value;
						 },
						 false,
						 ["_adminPanelTab","_adminPanel"]);
		editRoleTypeDiv.appendChild(selectPrincipal);
		editNamesDiv.appendChild(editRoleTypeDiv);

		var editRoleDiv = document.createElement("div");
		editRoleDiv.style.padding = "3px";
		editRoleDiv.appendChild(document.createTextNode(ISA_R.alb_regularExpressionColon));
		var editRoleInput = document.createElement("input");
		editRoleInput.id = "tab_"+this.displayTabId+"_panelRegExpText";
		editRoleInput.type = "text";
		editRoleInput.size = 100;
		var beforeRoleText = false;
		IS_Event.observe(editRoleInput,
						 'change',
						 function (e){
							var nowText = ISA_Admin.trim( editRoleInput.value );
							if(nowText.length == 0) {
								editRoleInput.value = beforeRoleText;
								editRoleInput.focus();
								return false;
							}
							var error = IS_Validator.validate(nowText, {format:'regexp', label:ISA_R.alb_regularExpression});
							if(error){
								alert(error);
								editRoleInput.focus();
								return false;
							}
							self.setNewValue("role", editRoleInput.value);
							var roleNameDiv = $("tab_"+self.displayTabId+"_role_" + self.displayRoleOrder).getElementsByTagName('td')[3];
							roleNameDiv.firstChild.innerHTML = editRoleInput.value;
						 },
						 false,
						 ["_adminPanelTab","_adminPanel"]);

		IS_Event.observe(editRoleInput,
						 'focus',
						 function (e){
							beforeRoleText = editRoleInput.value;
						 },
						 false,
						 ["_adminPanelTab","_adminPanel"]);
		editRoleDiv.appendChild(editRoleInput);
		editNamesDiv.appendChild(editRoleDiv);

		var editTabNameDiv = document.createElement("div");
		editTabNameDiv.id = "tab_"+this.displayTabId+"_tabNameDiv";
		editTabNameDiv.style.padding = "3px";
		editTabNameDiv.appendChild(document.createTextNode(ISA_R.alb_tabName+":"));
		var editTabNameInput = document.createElement("input");
		editTabNameInput.id = "tab_"+this.displayTabId+"_panelTabNameText";
		editTabNameInput.type = "text";
		editTabNameInput.size = 30;
		editTabNameInput.maxLength = 80;
		var beforeTabNameText = false;
		IS_Event.observe(editTabNameInput,
						 'change',
						 function (e){
							var nowText = ISA_Admin.trim( editTabNameInput.value );
							if(nowText.length == 0) {
								editTabNameInput.value = beforeTabNameText;
								editTabNameInput.focus();
								return false;
							}
							var error = IS_Validator.validate(nowText, {maxBytes:256, label:ISA_R.alb_tabName});
							if(error){
								alert(error);
								editTabNameInput.focus();
								return false;
							}
							self.setNewValue("tabName", nowText);
						 },
						 false,
						 ["_adminPanelTab","_adminPanel"]);
		IS_Event.observe(editTabNameInput,
						 'focus',
						 function (e){
							beforeTabNameText = editTabNameInput.value;
						 },
						 false,
						 ["_adminPanelTab","_adminPanel"]);
		editTabNameDiv.appendChild(editTabNameInput);
		editNamesDiv.appendChild(editTabNameDiv);

		function changePanelUsage(e){
			var select = Event.element(e);
			var jsonRole = self.displayRoleJsons[self.displayRoleId];
			if(jsonRole.panelUsage != select.value){
				if(!confirm(ISA_R.ams_confirmClearStaticAreaSetting))
					return false;
			};
			
			jsonRole.panelUsage = select.value;
			self.switchPanelDisplay(select.value);
			self.templates.setStaticLayout0(jsonRole,0);
			self.changeStaticLayout(jsonRole);
		}
		
		if("commandbar" != self.displayTabId){
			editAreaDiv.appendChild(
				$.DIV({style:"padding:3px;"},
					  ISA_R.alb_selectDisplayArea,
					  $.SELECT({id:'panelUsageSelect',name:'panelUsage',onchange:{handler:changePanelUsage}},
							   $.OPTION({value: 'useBothArea'},ISA_R.alb_useBothArea),
							   $.OPTION({value: 'useStaticOnly'},ISA_R.alb_disableCustomizedArea),
							   $.OPTION({value: 'useStaticOnly_adjustHeight'},ISA_R.alb_adjustToWindowHeight)
							 )
						)
				);
		}

		var labelSetStaticDiv = document.createElement("fieldset");
		var labelSetStaticLegend = document.createElement("legend");
		labelSetStaticLegend.appendChild(document.createTextNode(ISA_R.alb_fixedArea));
		labelSetStaticDiv.appendChild(labelSetStaticLegend);
		editAreaDiv.appendChild(labelSetStaticDiv);

		var editStaticDiv = this.editStaticDiv = document.createElement("div");
		editStaticDiv.id = "panelStaticDiv";
		//editStaticDiv.appendChild(labelSetStaticDiv);
		editStaticDiv.appendChild(this.buildStaticContainer());
		labelSetStaticDiv.appendChild(editStaticDiv);

		// Customized area settings
		if("commandbar" != self.displayTabId){
			editAreaDiv.appendChild(document.createElement("br"));

			var labelSetDynamicDiv = $.FIELDSET({id:'tab_'+self.displayTabId+'_personalizedAreaFieldSet'},
												$.LEGEND({},ISA_R.alb_customizedArea));
			editAreaDiv.appendChild(labelSetDynamicDiv);
			
			var editDynamicDiv = document.createElement("div");
			editDynamicDiv.id = "tab_"+this.displayTabId+"_panelDynamicDiv";
			//editDynamicDiv.appendChild(labelSetDynamicDiv);
			editDynamicDiv.appendChild(this.buildDynamicContainer());
			labelSetDynamicDiv.appendChild(editDynamicDiv);
		}
		// Work area setting (Always not display)
		var editWorkDiv = document.createElement("div");
		editWorkDiv.id = "panelWorkDiv";
		editWorkDiv.style.display = "none";
		editWorkDiv.appendChild(this.buildWorkContainer());
		editAreaDiv.appendChild(editWorkDiv);

		return editAreaDiv;
	}

	/**
		Back to list
		isBack=true  : To list
		isBack=false : To edit page
	*/
	this.backToList = function(isBack) {
		var listElement = $("tab_"+this.displayTabId+"_roleListPanel");
		var editElement = $("tab_"+this.displayTabId+"_roleEditPanel");
		var backElement = $("tab_"+this.displayTabId+"_backListPanel");
		var listDisplay = "none";
		var editDisplay = "none";
		var backDisplay = "none";

		if(!isBack) {
			editDisplay = "block";
			backDisplay = "block";
		} else {
			listDisplay = "block";
		}

		if(listElement)
			listElement.style.display = listDisplay;
		if(editElement)
			editElement.style.display = editDisplay;
		if(backElement)
			backElement.style.display = backDisplay;

		if(isBack)
			this.buildTabContents();
	}

	/**
		Create editting area of Static widget
	*/
	this.buildStaticContainer = function() {
		this.staticContainer = false;
		this.staticContainer = document.createElement("div");
		this.staticContainer.id = "panelStaticContainer";
		return this.staticContainer;
	}

	this.clearStaticContainer = function() {
		if(!this.staticContainer) return;
		while (this.staticContainer.hasChildNodes())
			this.staticContainer.removeChild(this.staticContainer.firstChild);
	}

	/**
		Create editting area of Dynamic widget
	*/
	this.buildDynamicContainer = function() {
		this.dynamicContainer = false;
		this.dynamicContainer = document.createElement("div");
		this.dynamicContainer.id = "panelDynamicContainer";
		return this.dynamicContainer;
	}

	this.clearDynamicContainer = function() {
		if(!this.dynamicContainer) return;
		while (this.dynamicContainer.hasChildNodes())
			this.dynamicContainer.removeChild(this.dynamicContainer.firstChild);
	}

	/**
		Create Work area
	*/
	this.buildWorkContainer = function() {
		this.workContainer = false;
		this.workContainer = document.createElement("div");
		this.workContainer.id = "panelWorkContainer";
		return this.workContainer;
	}

	this.clearWorkContainer = function() {
		if(!this.workContainer) return;
		while (this.workContainer.hasChildNodes())
			this.workContainer.removeChild(this.workContainer.firstChild);
	}

	/**
		Create commandBar Widget List
	*/
	this.buildCommandWidgetsList = function(jsonRole) {
		// Paste layout to hidden Work area
		if(!this.workContainer) return;
		// Remove if the item displayed last time is remained
		this.clearWorkContainer();
		this.workContainer.innerHTML = ISA_Admin.replaceUndefinedValue(jsonRole.layout);

		if(!this.staticContainer) return;
		// Remove if the item displayed last time is remained
		this.clearStaticContainer();

		var commandBarDiv = null;
		var addCommandBarDiv = ISA_Admin.createIconButton(ISA_R.alb_addLink, ISA_R.alb_addLinkMessage, "add.gif", "left");
		addCommandBarDiv.id = "addCommandBarDefault";
		this.staticContainer.appendChild(addCommandBarDiv);
		IS_Event.observe(addCommandBarDiv, 'click', this.addCommandBarModal.init.bind(this), false, ["_adminPanelTab","_adminPanel"]);

		var commandBarDiv = null;
		var addCommandBarDiv = ISA_Admin.createIconButton(ISA_R.alb_addHtml, ISA_R.alb_addHtmlMessage, "add.gif", "left");
		addCommandBarDiv.id = "addHTMLCommandBarDefault";
		this.staticContainer.appendChild(addCommandBarDiv);
		IS_Event.observe(addCommandBarDiv, 'click', this.addHTMLCommandBarModal.init.bind(this), false, ["_adminPanelTab","_adminPanel"]);

		var commandBarListTable = document.createElement("table");
		commandBarListTable.border = "0";
		commandBarListTable.cellSpacing = "0";
		commandBarListTable.cellPadding = "0";
		commandBarListTable.className = "defaultPanelWidgetsGroup";

		var commandBarListTbody = document.createElement("tbody");
		commandBarListTable.appendChild(commandBarListTbody);

		var commandBarListTr;
		commandBarListTr = document.createElement("tr");
		commandBarListTbody.appendChild(commandBarListTr);

		var commandBarListTh;
		var commandBarListTd;
		commandBarListTh = document.createElement("td");
		commandBarListTh.className = "headerDefaultPanel";
		commandBarListTh.style.whiteSpace = "nowrap";
		commandBarListTh.style.width = "100px";
		commandBarListTh.appendChild(document.createTextNode(ISA_R.alb_order));
		commandBarListTr.appendChild(commandBarListTh);

		commandBarListTh = document.createElement("td");
		commandBarListTh.className = "headerDefaultPanel";
		commandBarListTh.style.whiteSpace = "nowrap";
		commandBarListTh.style.width = "150px";
		commandBarListTh.appendChild(document.createTextNode(ISA_R.alb_displayOrNot));
		commandBarListTr.appendChild(commandBarListTh);

		commandBarListTh = document.createElement("td");
		commandBarListTh.className = "headerDefaultPanel";
		commandBarListTh.style.whiteSpace = "nowrap";
		commandBarListTh.style.width = "350px";
		commandBarListTh.appendChild(document.createTextNode(ISA_R.alb_name));
		commandBarListTr.appendChild(commandBarListTh);

		commandBarListTh = document.createElement("td");
		commandBarListTh.className = "headerDefaultPanel";
		commandBarListTh.style.whiteSpace = "nowrap";
		commandBarListTh.style.width = "50px";
		commandBarListTh.appendChild(document.createTextNode(ISA_R.alb_edit));
		commandBarListTr.appendChild(commandBarListTh);

		commandBarListTh = document.createElement("td");
		commandBarListTh.className = "headerDefaultPanel";
		commandBarListTh.style.whiteSpace = "nowrap";
		commandBarListTh.style.width = "50px";
		commandBarListTh.appendChild(document.createTextNode(ISA_R.alb_delete));
		commandBarListTr.appendChild(commandBarListTh);

		commandBarListTr = document.createElement("tr");
		commandBarListTbody.appendChild(commandBarListTr);

		commandBarListTd = document.createElement("td");
		commandBarListTd.colSpan = "5";
		commandBarListTr.appendChild(commandBarListTd);

		commandBarDiv = document.createElement("div");
		commandBarDiv.id = "panelCommandGroup";
		commandBarListTd.appendChild(commandBarDiv);

		this.staticContainer.appendChild(commandBarListTable);

		// CommandWidget build
		var childDivs = this.workContainer.getElementsByTagName('div');
		for(var i = 0; i < childDivs.length; i++){
			if(!childDivs[i].id) continue;
			var id = childDivs[i].getAttribute('disabledCommand') ? childDivs[i].id.replace(/^disabled_/,"") : childDivs[i].id;
			var commandmap = commandBarMap[id];

			var widget = jsonRole.staticPanel[id];
			if( !commandmap && widget && widget.type && commandBarMap[widget.type]) {
				commandmap = commandBarMap[widget.type];
				commandmap.id = widget.id;
			}

			// If it is not exist in commandBarMap and added to link, it is added to the list
			if( !commandmap ) {
				var childDivId = childDivs[i].id;
				var childTitle = childDivs[i].getAttribute("label");
				var childType = childDivs[i].getAttribute("type");
				//Title is set as label attribute. However, use the existing logic below if there is not title.
				if(!childTitle) {
					var childAs = childDivs[i].getElementsByTagName('a');
					try{
						childTitle = childAs[0].firstChild.nodeValue;
					}catch(e){
						
					}
				}
				if(childTitle)
					commandmap = {id:childDivId, title:childTitle, type:childType};
			}

			if( commandmap ) {
				findParentTdElement(childDivs[i]).id = "td_" + id;
				commandBarDiv.appendChild(this.buildCommandWidget(commandmap));
			}
		}
	}

	/**
		Create commandBar widget
	*/
	this.buildCommandWidget = function(commandBarItem) {
		var commandBarDiv = document.createElement("div");
		commandBarDiv.id = "commnad_" + commandBarItem.id;
		commandBarDiv.className = "rowDefaultPanel";

		var commandBarTable = document.createElement("table");
		commandBarTable.border = "0";
		commandBarTable.cellSpacing = "0";
		commandBarTable.cellPadding = "0";
		commandBarDiv.appendChild(commandBarTable);

		var commandBarTbody = document.createElement("tbody");
		commandBarTable.appendChild(commandBarTbody);

		var commandBarTr = document.createElement("tr");
		commandBarTr.style.height = "20px";
		commandBarTbody.appendChild(commandBarTr);
		var commandBarTd;
		var contentDiv;

		// Order
		commandBarTd = document.createElement("td");
		commandBarTd.style.textAlign = "center";
		commandBarTr.appendChild(commandBarTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "90px";
		contentDiv.className = "handle";
		var commandBarA = document.createElement("a");
		commandBarA.style.cursor = "move";
		contentDiv.appendChild(commandBarA);
		var commandBarImg = document.createElement("img");
		commandBarImg.src = imageURL + "drag.gif";
		commandBarA.appendChild(commandBarImg);
		commandBarTd.appendChild(contentDiv);

		// spacer
		commandBarTd = document.createElement("td");
		commandBarTr.appendChild(commandBarTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "10px";
		commandBarTd.appendChild(contentDiv);

		// Display/Not Display
		commandBarTd = document.createElement("td");
		commandBarTd.style.textAlign = "center";
		commandBarTr.appendChild(commandBarTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "140px";
		contentDiv.className = "contentsDefaultPanel";
		contentDiv.id = "disp_" + commandBarItem.id;
		var td = getParentTdElement(commandBarItem.id);
		if(commandBarItem.togglable){
			var checkedCheckBox = true;
			var disabledDiv = $("disabled_"+commandBarItem.id);
			if(disabledDiv && disabledDiv.getAttribute("disabledCommand")){
				checkedCheckBox = false;
			}
			var checkBox = ISA_Admin.createBaseCheckBox("", checkedCheckBox, false);
			contentDiv.appendChild(checkBox);
			var clickCheckHandler = function(e) {
				if(commandBarItem.togglableConfirm && !commandBarItem.togglableConfirm(checkBox)){
					checkBox.checked = true;
					return;
				}

				var elementTd = getParentTdElement(commandBarItem.id);
				var roleJSON = self.displayRoleJsons[self.displayRoleId];
				var widgetJSON = roleJSON.staticPanel[commandBarItem.id];
				if(!checkBox.checked) {
					var disabledDiv = document.createElement("div");
					disabledDiv.id = 'disabled_' + commandBarItem.id;
					disabledDiv.setAttribute('disabledCommand','true');
					disabledDiv.appendChild(document.createComment(escapeHTMLEntity(elementTd.innerHTML)));
					elementTd.innerHTML = "";
					elementTd.appendChild(disabledDiv);
					if(widgetJSON)widgetJSON.disabled = true;
				} else {
					var disabledDiv = $('disabled_'+commandBarItem.id);
					if(disabledDiv && disabledDiv.firstChild)
						elementTd.innerHTML = unescapeHTMLEntity(disabledDiv.firstChild.nodeValue);
					if(widgetJSON)widgetJSON.disabled = false;
				}
				if(Browser.isIE) {
					self.commandDispMap[commandBarItem.id] = checkBox.checked;
				}
				self.changeCommandBarLayout();
			};
			IS_Event.observe(checkBox, 'click', clickCheckHandler, false, ["_adminPanelTab","_adminPanel"]);
		}
		commandBarTd.appendChild(contentDiv);

		// spacer
		commandBarTd = document.createElement("td");
		commandBarTr.appendChild(commandBarTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "10px";
		commandBarTd.appendChild(contentDiv);

		// Name
		commandBarTd = document.createElement("td");
		commandBarTr.appendChild(commandBarTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "350px";
		contentDiv.className = "contentsDefaultPanel";
		contentDiv.id = "name_" + commandBarItem.id;
		contentDiv.appendChild(document.createTextNode(ISA_Admin.replaceUndefinedValue(commandBarItem.title)));
		commandBarTd.appendChild(contentDiv);

		// Edit
		commandBarTd = document.createElement("td");
		commandBarTd.style.textAlign = "center";
		commandBarTr.appendChild(commandBarTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "40px";
		contentDiv.className = "contentsDefaultPanel";
		if(self.commandBarEditor[commandBarItem.type]){
			var editor = self.commandBarEditor[commandBarItem.type];
			var commandBarA = document.createElement("a");
			commandBarA.style.cursor = "pointer";
			contentDiv.appendChild(commandBarA);
			var commandBarImg = document.createElement("img");
			commandBarImg.src = imageURL + "edit.gif";
			commandBarA.appendChild(commandBarImg);
			new ISA_DefaultPanel.CommandItemEditor(commandBarA, commandBarItem, editor.buildForm, editor.onOK);
		}
		commandBarTd.appendChild(contentDiv);

		// Delete
		commandBarTd = document.createElement("td");
		commandBarTd.style.textAlign = "center";
		commandBarTr.appendChild(commandBarTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "40px";
		contentDiv.className = "contentsDefaultPanel";
		if(!commandBarItem.undeletable){
			var commandBarA = document.createElement("a");
			commandBarA.style.cursor = "pointer";
			contentDiv.appendChild(commandBarA);
			var commandBarImg = document.createElement("img");
			commandBarImg.src = imageURL + "trash.gif";
			commandBarA.appendChild(commandBarImg);
			var deleteCommandItemHandler = function(e){
				try{
					var elementTd = getParentTdElement(commandBarItem.id);
					elementTd.parentNode.removeChild(elementTd);
					self.changeCommandBarLayout();
					var jsonRole = self.displayRoleJsons[self.displayRoleId];
					self.buildCommandWidgetsList(jsonRole);
					self.addSortableEventCommand();
				}catch(e){
					msg.error(ISA_R.ams_failedDeleteCommandBar + getErrorMessage(e));
				}
			};
			IS_Event.observe(commandBarA, 'click', deleteCommandItemHandler, false, ["_adminPanelTab","_adminPanel"]);
		}
		commandBarTd.appendChild(contentDiv);

		return commandBarDiv;
	}

	/**
		Sorting CommandBar list
	*/
	this.addSortableEventCommand = function(){
		var draggingDivId = false;
		if(Browser.isIE && !self.commandDispMap){
			//Dealing with the problem that unchecked checkbox in IE at sorting.
			self.commandDispMap = {};
			var commandDivs = $("panelCommandGroup").childNodes;
			for(var i=0;i<commandDivs.length;i++){
				var commandWidgetId = commandDivs[i].id.substring(8);
				var dispCheckbox = $("disp_" + commandWidgetId).firstChild;
				if(dispCheckbox)
					self.commandDispMap[commandWidgetId] = dispCheckbox.checked;
			}
		}
		Sortable.create($("panelCommandGroup"),
			{
				tag: 'div',
				handle: 'handle',

				onChange: function(div){
					draggingDivId = div.id;
				},
				onUpdate:function(div){
					var draggedDiv = $(draggingDivId);
					var draggedWidgetId = draggingDivId.substring(8);
					var siblingWidgetId = (draggedDiv.nextSibling) ? draggedDiv.nextSibling.id.substring(8) : false;
					// Operate layout not displayed
					var dragTd = getParentTdElement(draggedWidgetId);
					var dropTd = getParentTdElement(siblingWidgetId);
					var parent = dragTd.parentNode;
					if(!parent) return;
					if(!siblingWidgetId) {
						parent.appendChild(dragTd);
					} else {
						parent.insertBefore(dragTd, dropTd);
					}
					if(self.commandDispMap){
						var dispCheckbox = $("disp_" + draggedWidgetId).firstChild;
						if(dispCheckbox)
							dispCheckbox.checked = self.commandDispMap[draggedWidgetId];
					}
					self.changeCommandBarLayout();
				}
			}
		);
	}

	/**
		Adding CommandBar(link)
	*/
	this.addCommandBar = function(commandDiv) {
		var existDiv = $(commandDiv.id);
		if(existDiv) {
			existDiv.parentNode.replaceChild(commandDiv, existDiv);
		} else {
			var commandTd = document.createElement("td");
			commandTd.appendChild(commandDiv);

			var childTrs = this.workContainer.getElementsByTagName('tr');
			// There should be only one TR tag
			childTrs[0].appendChild(commandTd);
		}

		this.changeCommandBarLayout();
		var jsonRole = this.displayRoleJsons[this.displayRoleId];
		this.buildCommandWidgetsList(jsonRole);
		this.addSortableEventCommand();
	}

	/**
		Change layout of hiding CommandBar
	*/
	this.changeCommandBarLayout = function() {
		this.displayRoleJsons[this.displayRoleId].layout = this.workContainer.innerHTML;
		this.isUpdated = true;
		ISA_Admin.isUpdated = true;
	}

	/**
		Creating Static widget list
	this.buildStaticWidgetsList = function(layout, staticJson) {
		if(!this.staticContainer) return;

		while( this.editStaticDiv.firstChild )
			this.editStaticDiv.removeChild( this.editStaticDiv.firstChild );

		this.editStaticDiv.appendChild( this.staticContainer );

		var buttonDiv = document.createElement("div");
		buttonDiv.style.clear = "both";
		this.editStaticDiv.insertBefore(buttonDiv, this.staticContainer);
		// Selecting layout button
		var selectButton = document.createElement("input");
		selectButton.type = "button";
		selectButton.value = ISA_R.alb_selectLayout;
		IS_Event.observe(selectButton, 'click', this.selectLayoutModal.init.bind(this), false, ["_adminPanelTab","_adminPanel"]);
		buttonDiv.appendChild(selectButton);
		buttonDiv.appendChild(document.createTextNode("　"));
		// Editting HTML button
		var htmlButton = document.createElement("input");
		htmlButton.type = "button";
		htmlButton.value = ISA_R.alb_editHTML;
		IS_Event.observe(htmlButton, 'click', this.editHTMLModal.init.bind(this, layout), false, ["_adminPanelTab","_adminPanel"]);
		buttonDiv.appendChild(htmlButton);

		// Remove the item displayed ay last time
		this.clearStaticContainer();

		this.staticContainer.innerHTML = ISA_Admin.replaceUndefinedValue(layout);
		var childDivs = this.staticContainer.getElementsByTagName('div');
		for(var i = 0; i < childDivs.length; i++){
			if(!childDivs[i].id) continue;
			childDivs[i].style.border = "solid 1px #000";
			var json = staticJson[childDivs[i].id];
			if( !json ) continue;

			var editImgSpan = document.createElement('span');//It does not work if the handler of Control modal is added to img element in IE
			var editImg = document.createElement("img");
			editImg.src = imageURL + "edit.gif";
			editImg.style.cursor = "pointer";
			editImg.title = ISA_R.alb_editing;
			editImgSpan.appendChild(editImg);
			if(!childDivs[i].firstChild) {
				childDivs[i].appendChild(editImgSpan);
			} else {
				childDivs[i].insertBefore(editImgSpan, childDivs[i].firstChild);
			}
//			IS_Event.observe(editImg, 'click', this.selectWidgetModal.init.bind(this, json.id, "static"), false, ["_adminPanelTab","_adminPanel"]);
			var editorFormObj =
			  new ISA_CommonModals.EditorForm(
				  editImgSpan,
				  function(widgetJSON){
					  var selectType = ISA_CommonModals.EditorForm.getSelectType();
					  if( widgetJSON.type != selectType )
						  widgetJSON.properties = {};
					  
					  var roleJSON = self.displayRoleJsons[self.displayRoleId];
					  var oldId = widgetJSON.id;
					  //var widgetJSON = roleJSON.staticPanel[widgetObj.id];
					  //var widgetJSON = Object.clone(roleJSON.staticPanel[widgetObj.id]);
					  widgetJSON.id = "w_"+new Date().getTime();
					  widgetJSON.type = ISA_CommonModals.EditorForm.getSelectType();
					  widgetJSON.properties = ISA_CommonModals.EditorForm.getProperty(widgetJSON);
					  widgetJSON.ignoreHeader = ISA_CommonModals.EditorForm.isIgnoreHeader();
					  if(!widgetJSON.ignoreHeader) delete widgetJSON.ignoreHeader;
					  widgetJSON.noBorder = ISA_CommonModals.EditorForm.isNoBorder();
					  if(!widgetJSON.noBorder) delete widgetJSON.noBorder;

					  widgetJSON.title = ISA_Admin.trim($("formTitle").value);
					  widgetJSON.href =  $("formHref").value;
					  var _widgetTitleDiv = $("title_" + oldId);
					  _widgetTitleDiv.innerHTML = widgetJSON.title;

					  delete roleJSON.staticPanel[oldId];
					  roleJSON.staticPanel[widgetJSON.id] = widgetJSON;
					  roleJSON.layout = roleJSON.layout.replace( escapeHTMLEntity( oldId ),widgetJSON.id );

					  self.changeStaticLayout( roleJSON );

					  if( Control.Modal.current ) {
						  Control.Modal.close();
					  } else {
						  Control.Modal.container.hide();
					  }
				  },{
					menuFieldSetLegend:ISA_R.alb_widgetHeaderSettings,
					setDefaultValue: false,
					disableMiniBrowserHeight: true,
					showIgnoreHeaderForm:true,
					showNoBorderForm:true,
					displayACLFieldSet:false,
					disableDisplayRadio:true,
					omitTypeList:['Ranking','Ticker','MultiRssReader']
				  });
			IS_Event.observe(editImgSpan, 'click', function(editorFormObj,json){editorFormObj.showEditorForm(json);}.bind(editImgSpan,editorFormObj,json), false, ["_adminPanelTab","_adminPanel"]);


			var widgetTypeDiv = document.createElement("div");
			widgetTypeDiv.style.paddingTop = '10px';
			widgetTypeDiv.style.paddingBottom = '20px';
			widgetTypeDiv.style.paddingLeft = '20px';
			widgetTypeDiv.style.paddingRight = '20px';
			widgetTypeDiv.style.textAlign = 'center';
			var widgetTitleDiv = document.createElement("span");
			widgetTitleDiv.id = "title_" + json.id;

			var iconDiv = ISA_Admin.buildWidgetTypeIconDiv(json.type);
			iconDiv.style.cssFloat = iconDiv.style.styleFloat = "left";

			widgetTitleDiv.appendChild(iconDiv);
			if(json.title && json.title.length > 0){
				widgetTitleDiv.appendChild(document.createTextNode(json.title));
			}else{
				widgetTitleDiv.appendChild(document.createTextNode(ISA_R.alb_noneSetting));
			}
			widgetTypeDiv.appendChild(widgetTitleDiv);
			childDivs[i].appendChild(widgetTypeDiv);

			childDivs[i].style.overflow = "hidden";
		}
	}
	*/
	
	/**
		Creating Static widget list
	*/
	this.buildStaticWidgetsList = function(layout, staticJson) {
		console.log("bbbb");
		if(!this.staticContainer) return;

		while( this.editStaticDiv.firstChild )
			this.editStaticDiv.removeChild( this.editStaticDiv.firstChild );

		this.editStaticDiv.appendChild( this.staticContainer );

		var buttonDiv = document.createElement("div");
		buttonDiv.style.clear = "both";
		this.editStaticDiv.insertBefore(buttonDiv, this.staticContainer);
		// Selecting layout button
		var selectButton = document.createElement("input");
		selectButton.type = "button";
		selectButton.value = ISA_R.alb_selectLayout;
		IS_Event.observe(selectButton, 'click', this.selectLayoutModal.init.bind(this), false, ["_adminPanelTab","_adminPanel"]);
		buttonDiv.appendChild(selectButton);
		buttonDiv.appendChild(document.createTextNode("　"));

		console.log(this.displayRoleJsons[this.displayRoleId]);
		if('useStaticOnly_adjustHeight' != this.displayRoleJsons[this.displayRoleId].panelUsage){
			// Editting HTML button
			var htmlButton = document.createElement("input");
			htmlButton.type = "button";
			htmlButton.value = ISA_R.alb_editHTML;
			IS_Event.observe(htmlButton, 'click', this.editHTMLModal.init.bind(this, layout), false, ["_adminPanelTab","_adminPanel"]);
			buttonDiv.appendChild(htmlButton);
		}

		// Remove the item displayed ay last time
		this.clearStaticContainer();

		this.staticContainer.innerHTML = ISA_Admin.replaceUndefinedValue(layout);
		var childDivs = this.staticContainer.getElementsByTagName('div');
		for(var i = 0; i < childDivs.length; i++){
			if(!childDivs[i].id) continue;
			childDivs[i].style.border = "solid 1px #000";
			var json = staticJson[childDivs[i].id];
			if( !json ) continue;

			var editImgSpan = document.createElement('span');//It does not work if the handler of Control modal is added to img element in IE
			var editImg = document.createElement("img");
			editImg.src = imageURL + "edit.gif";
			editImg.style.cursor = "pointer";
			editImg.title = ISA_R.alb_editing;
			editImgSpan.appendChild(editImg);
			if(!childDivs[i].firstChild) {
				childDivs[i].appendChild(editImgSpan);
			} else {
				childDivs[i].insertBefore(editImgSpan, childDivs[i].firstChild);
			}
//			IS_Event.observe(editImg, 'click', this.selectWidgetModal.init.bind(this, json.id, "static"), false, ["_adminPanelTab","_adminPanel"]);
			var editorFormObj =
			  new ISA_CommonModals.EditorForm(
				  editImgSpan,
				  function(widgetJSON){
					  var selectType = ISA_CommonModals.EditorForm.getSelectType();
					  if( widgetJSON.type != selectType )
						  widgetJSON.properties = {};
					  
					  var roleJSON = self.displayRoleJsons[self.displayRoleId];
					  var oldId = widgetJSON.id;
					  //var widgetJSON = roleJSON.staticPanel[widgetObj.id];
					  //var widgetJSON = Object.clone(roleJSON.staticPanel[widgetObj.id]);
					  widgetJSON.id = "w_"+new Date().getTime();
					  widgetJSON.type = ISA_CommonModals.EditorForm.getSelectType();
					  widgetJSON.properties = ISA_CommonModals.EditorForm.getProperty(widgetJSON);
					  widgetJSON.ignoreHeader = ISA_CommonModals.EditorForm.isIgnoreHeader();
					  if(!widgetJSON.ignoreHeader) delete widgetJSON.ignoreHeader;
					  widgetJSON.noBorder = ISA_CommonModals.EditorForm.isNoBorder();
					  if(!widgetJSON.noBorder) delete widgetJSON.noBorder;

					  widgetJSON.title = ISA_Admin.trim($("formTitle").value);
					  widgetJSON.href =  $("formHref").value;
					  var _widgetTitleDiv = $("title_" + oldId);
					  _widgetTitleDiv.innerHTML = widgetJSON.title;

					  delete roleJSON.staticPanel[oldId];
					  roleJSON.staticPanel[widgetJSON.id] = widgetJSON;
					  roleJSON.layout = roleJSON.layout.replace( escapeHTMLEntity( oldId ),widgetJSON.id );

					  self.changeStaticLayout( roleJSON );

					  if( Control.Modal.current ) {
						  Control.Modal.close();
					  } else {
						  Control.Modal.container.hide();
					  }
				  },{
					menuFieldSetLegend:ISA_R.alb_widgetHeaderSettings,
					setDefaultValue: false,
					disableMiniBrowserHeight: true,
					showIgnoreHeaderForm:true,
					showNoBorderForm:true,
					displayACLFieldSet:false,
					disableDisplayRadio:true,
					omitTypeList:['Ranking','Ticker','MultiRssReader']
				  });
			IS_Event.observe(editImgSpan, 'click', function(editorFormObj,json){editorFormObj.showEditorForm(json);}.bind(editImgSpan,editorFormObj,json), false, ["_adminPanelTab","_adminPanel"]);


			var widgetTypeDiv = document.createElement("div");
			widgetTypeDiv.style.paddingTop = '10px';
			widgetTypeDiv.style.paddingBottom = '20px';
			widgetTypeDiv.style.paddingLeft = '20px';
			widgetTypeDiv.style.paddingRight = '20px';
			widgetTypeDiv.style.textAlign = 'center';
			var widgetTitleDiv = document.createElement("span");
			widgetTitleDiv.id = "title_" + json.id;

			var iconDiv = ISA_Admin.buildWidgetTypeIconDiv(json.type);
			iconDiv.style.cssFloat = iconDiv.style.styleFloat = "left";

			widgetTitleDiv.appendChild(iconDiv);
			if(json.title && json.title.length > 0){
				widgetTitleDiv.appendChild(document.createTextNode(json.title));
			}else{
				widgetTitleDiv.appendChild(document.createTextNode(ISA_R.alb_noneSetting));
			}
			widgetTypeDiv.appendChild(widgetTitleDiv);
			childDivs[i].appendChild(widgetTypeDiv);

			childDivs[i].style.overflow = "hidden";
		}
	}
	/**
		Change layout of Static widget
	*/
	this.changeStaticLayout = function(json) {
		this.displayRoleJsons[this.displayRoleId].layout = json.layout;
		this.displayRoleJsons[this.displayRoleId].staticPanel = json.staticPanel;
		this.buildStaticWidgetsList(json.layout, json.staticPanel);
		this.isUpdated = true;
		ISA_Admin.isUpdated = true;
	}

	/**
		Change HTML
	*/
	this.changeHTMLLayout = function(newLayout) {
		var jsonRole = {};
		var newStaticPanel = {};
		var oldStaticPanel = this.displayRoleJsons[this.displayRoleId].staticPanel;
		this.clearWorkContainer();
		this.workContainer.innerHTML = ISA_Admin.replaceUndefinedValue(newLayout);
		var ids = [];
		var childDivs = this.workContainer.getElementsByTagName('div');
		for(var i = 0; i < childDivs.length; i++){
			if(!childDivs[i].id) continue;
			var divId = childDivs[i].id;
			var json = false;
			if( !oldStaticPanel[divId] || ids.contains( divId )) {
				json = this.templates.getDefaultWidgetJson();
				childDivs[i].id = json.id = "w_"+new Date().getTime()+"_"+i;
			} else {
				json = oldStaticPanel[divId];
			}
			newStaticPanel[json.id] = json;
			ids.push( json.id );
		}
		jsonRole.layout = this.workContainer.innerHTML;
		jsonRole.staticPanel = newStaticPanel;
		// Delete immidiately becuase id of Div is dupulicated
		this.clearWorkContainer();

		this.changeStaticLayout(jsonRole);
	}


	/**
		Create Dynamic Widget List
	*/
	this.buildDynamicWidgetsList = function(jsonRole) {
		if(!this.dynamicContainer) return;
		// Remove if the item diplayed at the last time is remained
		this.clearDynamicContainer();

		var dynamicWidgetDiv = null;
		var addDynamicDiv = ISA_Admin.createIconButton(ISA_R.alb_add, ISA_R.alb_add, "add.gif", "left");
		addDynamicDiv.id = "addDynamicDefault";
		var addDynamicHandler = function(e){
			var jsonObj = self.templates.getDefaultWidgetJson();
			jsonObj.id = "p_" + new Date().getTime() + "_w_1";
			jsonObj.title = ISA_R.alb_noneSetting;
			jsonObj.column = "1";
			dynamicWidgetDiv.appendChild(self.buildDynamicWidget(jsonObj, jsonRole.columnsArray.length));

			// Update to public object
			jsonRole.dynamicPanel[jsonObj.id] = jsonObj;
			// Rebuild Drag&Drop
			self.addSortableEventDynamic();
			self.isUpdated = true;
		}
		IS_Event.observe(addDynamicDiv, 'click', addDynamicHandler.bind(addDynamicDiv), false, ["_adminPanelTab","_adminPanel"]);

		var dynamicListTable = document.createElement("table");
		dynamicListTable.border = "0";
		dynamicListTable.cellSpacing = "0";
		dynamicListTable.cellPadding = "0";
		dynamicListTable.className = "defaultPanelWidgetsGroup";

		var dynamicListTbody = document.createElement("tbody");
		dynamicListTable.appendChild(dynamicListTbody);

		var dynamicListTr;
		dynamicListTr = document.createElement("tr");
		dynamicListTbody.appendChild(dynamicListTr);

		var dynamicListTh;
		var dynamicListTd;
		dynamicListTh = document.createElement("td");
		dynamicListTh.className = "headerDefaultPanel";
		dynamicListTh.style.whiteSpace = "nowrap";
		dynamicListTh.style.width = "100px";
		dynamicListTh.appendChild(document.createTextNode(ISA_R.alb_changeOrder));
		dynamicListTr.appendChild(dynamicListTh);

		dynamicListTh = document.createElement("td");
		dynamicListTh.className = "headerDefaultPanel";
		dynamicListTh.style.whiteSpace = "nowrap";
		dynamicListTh.style.width = "350px";
		dynamicListTh.appendChild(document.createTextNode(ISA_R.alb_title));
		dynamicListTr.appendChild(dynamicListTh);

		dynamicListTh = document.createElement("td");
		dynamicListTh.className = "headerDefaultPanel";
		dynamicListTh.style.whiteSpace = "nowrap";
		dynamicListTh.style.width = "150px";
		dynamicListTh.appendChild(document.createTextNode(ISA_R.alb_column));
		var editColumnImg = document.createElement("img");
		editColumnImg.src = imageURL + "edit.gif";
		editColumnImg.style.cursor = "pointer";
		editColumnImg.title = ISA_R.alb_editColumnAndWidth;
		dynamicListTh.appendChild(editColumnImg);
		IS_Event.observe(editColumnImg, 'click', this.selectColumnModal.init.bind(this), false, ["_adminPanelTab","_adminPanel"]);
		dynamicListTr.appendChild(dynamicListTh);

		dynamicListTh = document.createElement("td");
		dynamicListTh.className = "headerDefaultPanel";
		dynamicListTh.style.whiteSpace = "nowrap";
		dynamicListTh.style.width = "50px";
		dynamicListTh.appendChild(document.createTextNode(ISA_R.alb_edit));
		dynamicListTr.appendChild(dynamicListTh);

		dynamicListTh = document.createElement("td");
		dynamicListTh.className = "headerDefaultPanel";
		dynamicListTh.style.whiteSpace = "nowrap";
		dynamicListTh.style.width = "50px";
		dynamicListTh.appendChild(document.createTextNode(ISA_R.alb_delete));
		dynamicListTr.appendChild(dynamicListTh);

		dynamicListTr = document.createElement("tr");
		dynamicListTbody.appendChild(dynamicListTr);

		dynamicListTd = document.createElement("td");
		dynamicListTd.colSpan = "5";
		dynamicListTr.appendChild(dynamicListTd);

		dynamicWidgetDiv = document.createElement("div");
		dynamicWidgetDiv.id = "panelDynamicGroup";
		dynamicListTd.appendChild(dynamicWidgetDiv);

		var errorMsgDiv = document.createElement("div");
		errorMsgDiv.style.color = "red";
		this.dynamicContainer.appendChild(errorMsgDiv);
		this.dynamicContainer.appendChild(addDynamicDiv);
		this.dynamicContainer.appendChild(dynamicListTable);


		var displayTopMenu = ISA_Properties.propertiesList.displayTopMenu.value;
		var displaySideMenu = ISA_Properties.propertiesList.displaySideMenu.value;

		// DynamicWidget build

		var buildDynamicPanelWidgets = function() {
			for(var i in jsonRole.dynamicPanel){
				if( !(jsonRole.dynamicPanel[i] instanceof Function) )
				  dynamicWidgetDiv.appendChild(self.buildDynamicWidget(jsonRole.dynamicPanel[i], jsonRole.columnsArray.length));
			}
			self.addSortableEventDynamic();
		}
		var loadSideMenuURL = function(){
			ISA_loadMenuConf(
				"sidemenu",
				buildDynamicPanelWidgets,
				function(errorMsg){
					errorMsgDiv.appendChild( document.createTextNode( errorMsg) );
					buildDynamicPanelWidgets();
				}.bind(this)
				  );
		}.bind(this);

		if( getBooleanValue(displayTopMenu) ){
			if( !getBooleanValue(displaySideMenu) ){//Other character than "true"
				ISA_loadMenuConf(
					"topmenu",
					function(){
						buildDynamicPanelWidgets();
					}.bind(this),
					function(errorMsg){
						errorMsgDiv.appendChild( document.createTextNode( errorMsg) );
						buildDynamicPanelWidgets();
					}.bind(this)
					  );
			}else{
				ISA_loadMenuConf(
					"topmenu",
					loadSideMenuURL,
					function(errorMsg){
						errorMsgDiv.appendChild( document.createTextNode( errorMsg) );
						loadSideMenuURL();
					}.bind(this)
					  );
			}
		} else if(getBooleanValue(displaySideMenu)){
			loadSideMenuURL();
		} else {
			buildDynamicPanelWidgets();
		}
	}

	/**
		Create Dynamic Widget
	*/
	this.buildDynamicWidget = function(dynamicPanelItem, dynamicColumnsLength) {
		var menuItem = IS_SiteAggregationMenu.menuItemList[dynamicPanelItem.id.substring(2)];
		for(var i in menuItem){
			if(i == "id" || typeof menuItem[i] == "function") continue;
			if( i == "properties") {
				for( var j in menuItem.properties )
					dynamicPanelItem.properties[j] = menuItem.properties[j];
			} else {
				dynamicPanelItem[i] = menuItem[i];
			}
		}
		var dynamicWidgetDiv = document.createElement("div");
		dynamicWidgetDiv.id = dynamicPanelItem.id;
		dynamicWidgetDiv.className = "rowDefaultPanel";

		var dynamicWidgetTable = document.createElement("table");
		dynamicWidgetTable.border = "0";
		dynamicWidgetTable.cellSpacing = "0";
		dynamicWidgetTable.cellPadding = "0";
		dynamicWidgetDiv.appendChild(dynamicWidgetTable);

		var dynamicWidgetTbody = document.createElement("tbody");
		dynamicWidgetTable.appendChild(dynamicWidgetTbody);

		var dynamicWidgetTr = document.createElement("tr");
		dynamicWidgetTr.style.height = "20px";
		dynamicWidgetTbody.appendChild(dynamicWidgetTr);
		var dynamicWidgetTd;
		var contentDiv;

		// Order
		dynamicWidgetTd = document.createElement("td");
		dynamicWidgetTd.style.textAlign = "center";
		dynamicWidgetTr.appendChild(dynamicWidgetTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "90px";
		contentDiv.className = "handle";
		var dynamicA = document.createElement("a");
		dynamicA.style.cursor = "move";
		contentDiv.appendChild(dynamicA);
		var dynamicImg = document.createElement("img");
		dynamicImg.src = imageURL + "drag.gif";
		dynamicA.appendChild(dynamicImg);
		dynamicWidgetTd.appendChild(contentDiv);

		// spacer
		dynamicWidgetTd = document.createElement("td");
		dynamicWidgetTr.appendChild(dynamicWidgetTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "10px";
		dynamicWidgetTd.appendChild(contentDiv);

		// Title
		dynamicWidgetTd = document.createElement("td");
		dynamicWidgetTr.appendChild(dynamicWidgetTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "340px";
		contentDiv.className = "contentsDefaultPanel";
		contentDiv.id = "title_" + dynamicPanelItem.id;

		var _title = ISA_R.alb_noneSetting;
		if(menuItem){
			if(menuItem.directoryTitle){
				_title = ISA_Admin.replaceUndefinedValue(menuItem.directoryTitle);
			} else if(menuItem.title && menuItem.title.length > 0){
				_title = ISA_Admin.replaceUndefinedValue(menuItem.title);
			} else if(menuItem.type){
				_title = ISA_SiteAggregationMenu.widgetConfs[ menuItem.type ].title;
				if(!_title){
					_title = menuItem.type;
				}
			}
		}
		contentDiv.appendChild(document.createTextNode(_title));

		dynamicWidgetTd.appendChild(contentDiv);

		// spacer
		dynamicWidgetTd = document.createElement("td");
		dynamicWidgetTr.appendChild(dynamicWidgetTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "10px";
		dynamicWidgetTd.appendChild(contentDiv);

		// Column
		dynamicWidgetTd = document.createElement("td");
		dynamicWidgetTr.appendChild(dynamicWidgetTd);
		contentDiv = document.createElement("div");
		contentDiv.style.textAlign = "center";
		contentDiv.style.width = "140px";
		contentDiv.className = "contentsDefaultPanel";
		contentDiv.id = "col_" + dynamicPanelItem.id;
		this.buildPullDown(dynamicPanelItem.id, contentDiv, dynamicPanelItem.column, dynamicColumnsLength);
		dynamicWidgetTd.appendChild(contentDiv);

		// spacer
		dynamicWidgetTd = document.createElement("td");
		dynamicWidgetTr.appendChild(dynamicWidgetTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "10px";
		dynamicWidgetTd.appendChild(contentDiv);

		// Edit icon
		dynamicWidgetTd = document.createElement("td");
		dynamicWidgetTd.style.textAlign = "center";
		dynamicWidgetTr.appendChild(dynamicWidgetTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "50px";
		contentDiv.className = "contentsDefaultPanel";
		var editImgSpan = document.createElement("span");
		var editImg = document.createElement("img");
		editImg.src = imageURL + "edit.gif";
		editImg.style.cursor = "pointer";
		editImg.title = ISA_R.alb_editing;
		editImgSpan.appendChild(editImg);
		contentDiv.appendChild(editImgSpan);
		dynamicWidgetTd.appendChild(contentDiv);

		var editorFormObj = new ISA_CommonModals.EditorForm(
			editImgSpan,
			function( widgetObj ){
				//var url = findHostURL() + "/adminserv/" + ( (widgetObj.instId) ? "updateWidgetInst" : "insertWidgetInst" );

				var widgetType = ISA_CommonModals.EditorForm.getSelectType();
				if(!widgetType || !widgetObj.menuItem){
					alert(ISA_R.ams_selectWidget);
					$("formExec").disabled = false;
					$("formCancel").disabled = false;
					return;
				}

				//if(!widgetObj.instId)widgetObj.instId = "w_d_" + new Date().getTime();
				var menuItem = widgetObj.menuItem;
				var widgetId = "w_" + menuItem.id;
				var roleJSON = self.displayRoleJsons[self.displayRoleId];
				if( roleJSON.dynamicPanel[widgetId]){
					alert(ISA_R.ams_widgetAlreadySelected);
					$("formExec").disabled = false;
					$("formCancel").disabled = false;
					return;
				}
				var widgetType = ISA_CommonModals.EditorForm.getSelectType();
				if(!widgetType){
					alert(ISA_R.ams_selectWidget);
					$("formExec").disabled = false;
					$("formCancel").disabled = false;
					return;
				}
				var widgetJSON = {
				  id : widgetId,
					column :widgetObj.column,
				  type : ISA_CommonModals.EditorForm.getSelectType(),
				  properties : ISA_CommonModals.EditorForm.getProperty(menuItem)
				};
				if(/MultiRssReader/.test( menuItem.type ) ||
				   ( menuItem.parentId && menuItem.properties.children )) {
					widgetJSON.id = "p_"+widgetJSON.id.substring(2);
					widgetJSON.properties.children = menuItem.properties.children;
					delete widgetJSON.properties.url;
				}
				var widgetsTemp = {};
				for( var i in roleJSON.dynamicPanel ) {
					if( roleJSON.dynamicPanel[i] instanceof Function ) continue;

					if( i == widgetObj.id) {
						widgetsTemp[widgetJSON.id] = widgetJSON;
					} else {
						widgetsTemp[i] = roleJSON.dynamicPanel[i];
					}
				}
				roleJSON.dynamicPanel = widgetsTemp;
				//TODO:Need to be fixed more. by endoh 20090312

				var _widgetTitleDiv = $("title_" + widgetObj.id);
				_widgetTitleDiv.innerHTML = "";
				if(widgetJSON.properties.title){
					widgetJSON.title = widgetJSON.properties.title;
					_widgetTitleDiv.appendChild( document.createTextNode(widgetJSON.title) );
				}else if(!widgetJSON.type){
					_widgetTitleDiv.appendChild(document.createTextNode(ISA_R.alb_noneSetting));
				}else{
					var _title = ISA_SiteAggregationMenu.widgetConfs[ widgetJSON.type ].title;
					if(_title){
						widgetJSON.title = _title;
					}else{
						widgetJSON.title = widgetJSON.type;
					}
					_widgetTitleDiv.appendChild( document.createTextNode( widgetJSON.title ));

				}
				widgetJSON.href = ("MiniBrowser" == widgetJSON.type) ? widgetJSON.properties.url : (widgetJSON.properties.href) ? widgetJSON.properties.href : "";

				delete widgetJSON.properties.title;
				delete widgetJSON.properties.href;

				var oldWidgetTr = $(widgetObj.id);
				oldWidgetTr.parentNode.replaceChild(this.buildDynamicWidget(widgetJSON, dynamicColumnsLength), oldWidgetTr);

				dynamicPanelItem.id = widgetJSON.id;
				// Rebuild Drag&Drop
				this.addSortableEventDynamic();

				this.isUpdated = true;
				ISA_Admin.isUpdated = true;

				Control.Modal.close();
			}.bind(this),
			{
			  formDisabled:true,
			  displayMenuFieldSet:false,
			  displayWidgetFieldSet:false,
			  displayACLFieldSet :false,
			  showMenuExplorer:true,
			  setDefaultValue: false
			});
		IS_Event.observe(editImgSpan, 'click', function(editorFormObj,json){editorFormObj.showEditorForm(json);}.bind(editImgSpan,editorFormObj,dynamicPanelItem), false, ["_adminPanelTab","_adminPanel"]);

		// Delete icon
		dynamicWidgetTd = document.createElement("td");
		dynamicWidgetTd.style.textAlign = "center";
		dynamicWidgetTr.appendChild(dynamicWidgetTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "50px";
		contentDiv.className = "contentsDefaultPanel";
		var deleteImg = document.createElement("img");
		deleteImg.src = imageURL + "trash.gif";
		deleteImg.style.cursor = "pointer";
		deleteImg.alt = ISA_R.alb_deleting;
		var deleteImgClick = function(e){
			dynamicWidgetDiv.parentNode.removeChild(dynamicWidgetDiv);
			// Delete from public object
			delete self.displayRoleJsons[self.displayRoleId].dynamicPanel[dynamicPanelItem.id];
			self.isUpdated = true;
		}
		IS_Event.observe(deleteImg, 'click', deleteImgClick.bind(deleteImg), false, ["_adminPanelTab","_adminPanel"]);
		contentDiv.appendChild(deleteImg);
		dynamicWidgetTd.appendChild(contentDiv);

		return dynamicWidgetDiv;
	}

	/**
		Change column number of Dynamic widget
	*/
	this.changeDynamicColumns = function(columnsWidth) {
		var jsonObject = this.displayRoleJsons[this.displayRoleId];
		jsonObject.columnsWidth = columnsWidth;
		jsonObject = this.setColumnsArray(jsonObject);
		this.buildDynamicWidgetsList(jsonObject);
		this.isUpdated = true;
		ISA_Admin.isUpdated = true;
	}

	/**
		Sorting Dynamic widget list
	*/
	this.addSortableEventDynamic = function(){
		var draggingDivId = false;
		Sortable.create($("panelDynamicGroup"),
			{
				tag: 'div',
				handle: 'handle',
				onChange: function(div){
					draggingDivId = div.id;
				},
				onUpdate:function(div){
					var newJsons = {};
					var oldJsons = self.displayRoleJsons[self.displayRoleId].dynamicPanel;
					var draggedDiv = $(draggingDivId);
					var draggedWidgetId = draggingDivId;
					var siblingDivId = (draggedDiv.previousSibling) ? draggedDiv.previousSibling.id : false;

					if(!siblingDivId){
						newJsons[draggedWidgetId] = oldJsons[draggedWidgetId];
					}
					for(var i in oldJsons) {
						if( !(oldJsons[i] instanceof Function) ){
							if( siblingDivId && i == draggingDivId)continue;
							newJsons[i] = oldJsons[i];
							if( siblingDivId && i == siblingDivId){
								newJsons[draggedWidgetId] = oldJsons[draggedWidgetId];
							}
						}
					}
					self.displayRoleJsons[self.displayRoleId].dynamicPanel = newJsons;

					self.isUpdated = true;
				}
			}
		);
	}

	/**
		Change widget
	*/
	this.changeWidget = function(oldId, type, json){
		var jsonRole = this.displayRoleJsons[this.displayRoleId];
		var newJsons = {};
		var oldJsons = false;
		if(type.toLowerCase() == "static") {
			oldJsons = jsonRole.staticPanel;
		} else if(type.toLowerCase() == "dynamic") {
			oldJsons = jsonRole.dynamicPanel;
			json.column = oldJsons[oldId].column;
		}

		//console.log("before: "+ $H( jsonRole.dynamicPanel ).keys() );
		for(var i in oldJsons) {
			if( !(oldJsons[i] instanceof Function) ){
				if( i != oldId) {
					newJsons[i] = oldJsons[i];
				} else {
					newJsons[json.id] = json;
				}
			}
		}
		//console.log("after: "+ $H( jsonRole.dynamicPanel ).keys() );
		if(type.toLowerCase() == "static") {
			jsonRole.staticPanel = newJsons;
			this.clearWorkContainer();
			this.workContainer.innerHTML = ISA_Admin.replaceUndefinedValue(jsonRole.layout);
			var childDivs = this.workContainer.getElementsByTagName('div');
			for(var i = 0; i < childDivs.length; i++){
				if(!childDivs[i].id) continue;
				if( childDivs[i].id == oldId ) {
					childDivs[i].id = json.id;
				}
			}
			jsonRole.layout = this.workContainer.innerHTML;
			// Delete immidiately because id of Div is duplicated
			this.clearWorkContainer();
			this.buildStaticWidgetsList(jsonRole.layout, jsonRole.staticPanel);
		} else if(type.toLowerCase() == "dynamic") {
			jsonRole.dynamicPanel = newJsons;
			var oldDiv = $(oldId);
			var newDiv = this.buildDynamicWidget(json, jsonRole.columnsArray.length);
			oldDiv.parentNode.replaceChild(newDiv, oldDiv);
			// Rebuild Drag&Drop
			this.addSortableEventDynamic();
		}

		this.isUpdated = true;
		ISA_Admin.isUpdated = true;
	}

	/**
		Create pull down
	*/
	this.buildPullDown = function(id, colDiv, selectedVal, maxCol){
		if(!colDiv || !selectedVal) return;
		var selected = false;
		var valueMap = {};
		for(var i = 1; i <= maxCol; i++){
			if(i == selectedVal) {
				selected = true;
			}
			// Select the last item if nothing is selected
			if(i == maxCol && !selected) {
				selectedVal = maxCol;
				setColumnValue(maxCol);
			}
			valueMap[i] = String(i);
		}
		var opt = {
			map : valueMap,
			selected : selectedVal,
			width : "50%",
			onChange : function(value){
				setColumnValue(value);
			}
		};
		(new PullDown(opt)).build(colDiv);

		function setColumnValue(v){
			self.displayRoleJsons[self.displayRoleId].dynamicPanel[id].column = String(v);
			self.isUpdated = true;
		}
	}

	/**
		Set column width of array
	*/
	this.setColumnsArray = function(jsonRole) {
		// Set initial value if nothing
		if(!jsonRole.columnsWidth) {
			if(jsonRole.tabId != commandBarTabId) {
				jsonRole.columnsWidth = '["33%","33%","34%"]';
			}
		}
		if(!jsonRole.columnsWidth) {
			jsonRole.columnsArray = false;
		} else {
			var value1 = jsonRole.columnsWidth.substring(1, jsonRole.columnsWidth.length - 1);
			value1 = value1.replace(/\"/g, "");
			var value2 = value1.split(",");
			jsonRole.columnsArray = value2;
		}

		return jsonRole;
	}

	/**
		Generate value of editting area
		TODO:Merge InstantEdit
	*/
	this.buildInput = function(targetNode, itemName, displayRoleId, validations) {
		if(!targetNode || !targetNode.firstChild) return;

		var beforeElement = targetNode.firstChild;
		var beforeText = ISA_Admin.trim( beforeElement.nodeValue );

		var editForm = document.createElement("input");
		editForm.setAttribute('autocomplete', 'off');
		editForm.type = "text";
		editForm.value = beforeText;
		editForm.style.width = (targetNode.offsetWidth - 5) + "px";
		if(!targetNode.firstChild) {
			targetNode.appendChild( editForm );
		} else {
			targetNode.replaceChild( editForm, targetNode.firstChild );
		}

		editForm.select();
		editForm.focus();
		editForm.select();

		IS_Event.observe(editForm, 'keydown', editFormOnKeydown, false, ["_adminPanelTab","_adminPanel"]);
		IS_Event.observe(editForm, 'blur', editFormOnBlur, false, ["_adminPanelTab","_adminPanel"]);
		IS_Event.observe(editForm, 'click', editFormEventCancel, false, ["_adminPanelTab","_adminPanel"]);
		IS_Event.observe(editForm, 'mousedown', editFormEventCancel, false, ["_adminPanelTab","_adminPanel"]);

		function editFormOnBlur(e){
			var nowText = ISA_Admin.trim( editForm.value );

			// Turn back to the previous value if empty is entered
			if(nowText.length == 0) {
				beforeElement.nodeValue = beforeText;
			} else {
				beforeElement.nodeValue = nowText;
			}
			if(validations){
				var error = IS_Validator.validate(nowText, validations);
				if(error) {
					alert(error);
					setTimeout( function() {
						editForm.focus();
						editForm.select();
					},10 );
					return;
				}
			}

			if(nowText != beforeText) {
				self.setNewValue(itemName, nowText, displayRoleId);
			}

			if(!targetNode.firstChild) {
				targetNode.appendChild( beforeElement );
			} else {
				targetNode.replaceChild( beforeElement, targetNode.firstChild );
			}

			IS_Event.stopObserving(editForm, 'keydown', editFormOnKeydown, false);
			IS_Event.stopObserving(editForm, 'blur', editFormOnBlur, false);
			IS_Event.stopObserving(editForm, 'click', editFormEventCancel, false);
			IS_Event.stopObserving(editForm, 'mousedown', editFormEventCancel, false);
			
			self.isUpdated = true;
			ISA_Admin.isUpdated = true;
		}
		function editFormOnKeydown(e){
			if (Browser.isIE) {	/* for IE */
				e = window.event;
			}

			if(e.keyCode == 13){
				editFormOnBlur();
			}
		}
		function editFormEventCancel(e){
			if(window.event){
				window.event.cancelBubble = true;
			}
			if(e && e.stopPropagation){
				e.stopPropagation();
			}
		}
	}

	/**
		Set value
		This function may not be necessary
	*/
	this.setNewValue = function(itemId, newValue, roleId) {
		if(!this.displayRoleId && !roleId) return;
		var displayRoleId = (roleId? roleId : this.displayRoleId);
		var displayRole = this.displayRoleJsons[displayRoleId];
		switch (String(itemId).toLowerCase()) {
			case "tabname":
				displayRole.tabName = newValue;
				break;
			case "role":
				displayRole.role = newValue;
				break;
			case "principaltype":
				displayRole.principalType = newValue;
				break;
			case "rolename":
				displayRole.roleName = newValue;
				break;
			case "layout":
				displayRole.layout = newValue;
				break;
			case "staticpanel":
				displayRole.staticPanel = newValue;
				break;
			case "dynamicpanel":
				displayRole.dynamicPanel = newValue;
				break;
			case "disableddynamicpanel":
				displayRole.disabledDynamicPanel = newValue;
				break;
			case "adjusttowindowheight":
				displayRole.adjustToWindowHeight = newValue;
				break;
			default:
				break;
		}
		this.isUpdated = true;
		ISA_Admin.isUpdated = true;
	}

	/**
		Update
	*/
	this.updatePanel = function(isSync) {
		if(!this.isUpdated) return true;
		if(!this.displayRoleJsons) {//If deleted
			this.isUpdated = false;
			return true;
		}
		var value = "";
		var errorMessages = [];

		var tabId;
		var tabNumber;
		var roleJsons = {};
//		var roles = [];
		var roleOrder = 0;
		var duplicateCheckArray = [];	// fix #174
		for(var i in this.displayRoleJsons) {
			if( (this.displayRoleJsons[i] instanceof Function) ) continue;

			var roleJson = Object.clone( this.displayRoleJsons[i]);

			var duplicateCheckKey = roleJson.role + roleJson.principalType;
			if( duplicateCheckArray.contains( duplicateCheckKey ) ){
				alert(ISA_R.ams_sameSubjectForRE);
				msg.error(ISA_R.ams_sameSubjectForRE);
				return false;
			}
			duplicateCheckArray.push( duplicateCheckKey );

			var error = null;
			if(error = IS_Validator.validate(roleJson.roleName, {maxBytes:256, label:ISA_R.alb_roleName})){
				errorMessages.push(error);
			}
			if(error = IS_Validator.validate(roleJson.role, {format:'regexp', label:ISA_R.alb_regularExpression})){
				errorMessages.push(error);
			}

			// It need to cast if it is Numeber
			roleJson.roleOrder = roleOrder++;
			roleJson.principalType = String( roleJson.principalType ); // null is needed to be entered as characters
			roleJson.id = String( roleJson.id );
			roleJson.tabId = String( roleJson.tabId );
			roleJson.defaulUid = String( roleJson.defaultUid );
			roleJson.columnsWidth = roleJson.columnsWidth.replace(/\"/g, "&quot;");
			roleJson.numCol = String( roleJson.columnsArray.length );
			if(typeof roleJson.disabledDefault != 'undefined')
			  roleJson.disabledDefault = String( roleJson.disabledDefault );
			delete roleJson.columnsArray;

			roleJson.staticPanel = !roleJson.staticPanel?"":
				ISA_Admin.trim(ISA_Admin.replaceUndefinedValue( Object.toJSON(roleJson.staticPanel) ));
			roleJson.layout = !roleJson.layout?"":
				ISA_Admin.trim(ISA_Admin.replaceUndefinedValue(roleJson.layout));
			roleJson.dynamicPanel = !roleJson.dynamicPanel?"":
				ISA_Admin.trim(ISA_Admin.replaceUndefinedValue( Object.toJSON(roleJson.dynamicPanel) ));

			roleJson.tabNumber = String(/[0-9]+/.test( roleJson.tabNumber ) ? roleJson.tabNumber : "");

			roleJsons[ roleJson.id ] = roleJson;
			if( !tabId && !tabNumber ) {
				tabId = roleJson.tabId;
				tabNumber = roleJson.tabNumber;
			}
		}

		if( errorMessages.length != 0) {
			alert(ISA_R.ams_failedToSaveTop+"\n"+
				errorMessages.join("\n"));
			return false;
		}

		//controlModal.open();

		var url = findHostURL() + "/services/tabLayout/updateDefaultPanel";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			postBody: Object.toJSON([ tabId,tabNumber,roleJsons]),
			asynchronous:!isSync,
			onSuccess: function(response){
				var array = eval(response.responseText);
				if(!self.tabIdList.contains(array[0])){//It is needed to be set only if tab is added.
					self.displayTabId = array[0];
					self.displayTabNumber = array[1];
				}
				// Call registering event
				IS_EventDispatcher.newEvent('updatePanelOnSuccess', 'default', null);
			},
			onFailure: function(t) {
				var errormsg = t.responseText && typeof t.responseText == "string" ? t.responseText.substr(0, 100) : "";
				alert(ISA_R.ams_failedToSaveTop+'\n' + errormsg);
				msg.error( ISA_R.ams_failedToSaveTop+ errormsg);
			},
			onException: function(r, t){
				alert(ISA_R.ams_failedToSaveTop);
				msg.error(ISA_R.ams_failedToSaveTop + getErrorMessage(t));
			},
			onComplete: function(){
				// Delete registered event when tab is added
				IS_EventDispatcher.removeListener('updatePanelOnSuccess', 'default', self.addAfter);
			}
		};
		AjaxRequest.invoke(url, opt);

		this.isUpdated = false;
		return true;
	}
	this.removeDefaultPanel = function(tabId) {
		var url = findHostURL() + "/services/tabLayout/removeDefaultPanel";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			postBody: Object.toJSON([tabId]),
			asynchronous:true,
			onFailure: function(t) {
				var errormsg = t.responseText && typeof t.responseText == "string" ? t.responseText.substr(0, 100) : "";
				alert(ISA_R.ams_failedToSaveTop+'\n' + errormsg);
				msg.error(ISA_R.ams_failedToSaveTop + errormsg);
			},
			onException: function(r, t){
				alert(ISA_R.ams_failedToSaveTop);
				msg.error(ISA_R.ams_failedToSaveTop + getErrorMessage(t));
			}
		};
		AjaxRequest.invoke(url, opt);
	}

	this.build = function() {

		function getWidgetConf() {
			var url = findHostURL() + "/services/widgetConf/getWidgetConfJson";
			var opt = {
				method: 'get' ,
				asynchronous:true,
				onSuccess: function(response){
					eval("ISA_SiteAggregationMenu.setWidgetConf("+response.responseText+")");
					self.getDefaultPanelJSONByTabId( self.tabIdList[0],self.displayDefaultPanel.bind( self ) );
				},
				on404: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
					container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_widgetNotFound+"</span>";
					msg.error(ISA_R.ams_widgetNotFound + t.status + " - " + t.statusText);
				},
				onFailure: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
					container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_FailedLoadingWidget+"</span>";
					msg.error(ISA_R.ams_FailedLoadingWidget + t.status + " - " + t.statusText);
				},
				onException: function(r, t){
				if(!container.firstChild) container.appendChild(document.createElement("div"));
					container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_FailedLoadingWidget+"。</span>";
					msg.error(ISA_R.ams_FailedLoadingWidget + getErrorMessage(t));
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

		function getTabIdListJson(){
			var url = findHostURL() + "/services/tabLayout/getTabIdListJson";
			var opt = {
				method: 'get' ,
				asynchronous:true,
				onSuccess: function(response){
					var array = eval(response.responseText);
					self.tabIdList = array[0];
					self.tabNumberJson = array[1];
					getWidgetConf();
				},
				on404: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
					container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_toppageSettingsNF+"</span>";
					msg.error(ISA_R.ams_toppageSettingsNF + t.status + " - " + t.statusText);
				},
				onFailure: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
					container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedToloadTS+"</span>";
					msg.error(ISA_R.ams_failedToloadTS + t.status + " - " + t.statusText);
				},
				onException: function(r, t){
				if(!container.firstChild) container.appendChild(document.createElement("div"));
					container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedToloadTS+"</span>";
					msg.error(ISA_R.ams_failedToloadTS + getErrorMessage(t));
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

		var url = findHostURL() + "/services/tabLayout/getLockingUid";
		var opt = {
			method: 'get' ,
			asynchronous:true,
			onSuccess: function(response){
				var lockedUid = response.responseText;
				var conflictMsg = IS_R.getResource(ISA_R.alb_editByOtherUser, [lockedUid]);
				if(lockedUid && lockedUid != is_userId
					 && !confirm(conflictMsg + "\n"+ISA_R.alb_multiUserEdit2)){
					loadingMessage.innerHTML = "";
					loadingMessage.appendChild(document.createTextNode(conflictMsg));
					return;
				}
				getTabIdListJson();
			},
			on404: function(t) {
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_toppageSettingsNF+"</span>";
				msg.error(ISA_R.ams_toppageSettingsNF + t.status + " - " + t.statusText);
			},
			onFailure: function(t) {
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedToloadTS+"</span>";
				msg.error(ISA_R.ams_failedToloadTS + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedToloadTS+"</span>";
				msg.error(ISA_R.ams_failedToloadTS + getErrorMessage(t));
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

	this.getDefaultPanelJSONByTabId = function(tabId,callback ){
		var url = findHostURL() + "/services/tabLayout/getDefaultPanelJson";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			postBody: Object.toJSON([tabId]),
			asynchronous:true,
			onSuccess: function(response){
				self.displayRoleJsons = eval("(" + response.responseText + ")");
				if( callback )
					callback();
			},
			on404: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_toppageSettingsNF+"</span>";
				msg.error(ISA_R.ams_toppageSettingsNF + t.status + " - " + t.statusText);
			},
			onFailure: function(t) {
		  		if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedToloadTS+"</span>";
				msg.error(ISA_R.ams_failedToloadTS + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedToloadTS+"</span>";
				msg.error(ISA_R.ams_failedToloadTS + getErrorMessage(t));
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

	function findParentTdElement(element) {
		if(!element) return null;
		if(element.tagName.toLowerCase() == "td" ) return element;

		var parent = element;
		do {
			parent = parent.parentNode;
			if(!parent) return null;
		} while(parent.tagName.toLowerCase() != "td")

		return parent;
	}

	function getParentTdElement(commandItemId){
		return $("td_"+commandItemId);
	}
};
