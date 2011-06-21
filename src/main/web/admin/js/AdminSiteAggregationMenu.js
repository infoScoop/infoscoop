var ISA_SiteAggregationMenu = IS_Class.create();

ISA_SiteAggregationMenu.treeMenu = false;
ISA_SiteAggregationMenu.menuItemList = false;
ISA_SiteAggregationMenu.menuDummyItemList = [];
ISA_SiteAggregationMenu.menuTreeAdminUsers = [];
ISA_SiteAggregationMenu.topMenuIdList = false;
ISA_SiteAggregationMenu.menuItemTreeMap = false;
ISA_SiteAggregationMenu.widgetConfs = false;

ISA_SiteAggregationMenu.isTreeAdminUser = false;

// Called by the value returned from server
ISA_SiteAggregationMenu.setMenu = function(a,b,c) {
	ISA_SiteAggregationMenu.menuItemList = a;
	ISA_SiteAggregationMenu.topMenuIdList = b;
	ISA_SiteAggregationMenu.menuItemTreeMap = c;
	
	return true;
}

ISA_SiteAggregationMenu.getSitetopId = function(menuItem){
	var parentItem = ISA_SiteAggregationMenu.menuItemList[menuItem.parentId];
	while(parentItem){
		var tempParentItem = ISA_SiteAggregationMenu.menuItemList[parentItem.parentId];
		if(!tempParentItem)
			break;
		
		parentItem = tempParentItem;
	}
	
	return (parentItem)? parentItem.id : menuItem.id;
}

ISA_SiteAggregationMenu.getErrorMessage = function(response){
	var resTxt = (response.responseText)? ("\n" + response.responseText) : "";
	try{
		var resultJson = eval("(" + response.responseText + ")");
		if(resultJson.errorType == "menusTimeout"){
			resTxt = ISA_R.ams_menusTimeout;
		}
		else if(resultJson.errorType == "menusIllegalEdit"){
			resTxt = ISA_R.ams_menusIllegalEdit;
			var errorIdList = resultJson.errorIdList;
			
			var sitetopTitles = [];
			errorIdList.each(function(sitetopId){
				var sitetopItem = ISA_SiteAggregationMenu.menuItemList[sitetopId];
				if(sitetopItem)
					this.push(sitetopItem.title);
			}.bind(sitetopTitles));
			
			resTxt += ("\n" + Object.toJSON(sitetopTitles));
		}
	}catch(e){
		// ignore
	};
	return resTxt;
}

ISA_SiteAggregationMenu.mergeMenu = function(menuItems, mapJson){
	var menuItemList = ISA_SiteAggregationMenu.menuItemList;
	for(var i in menuItems){
		if(typeof i == "function") continue;
		menuItemList[i] = menuItems[i];
	}
	
	var menuItemTreeMap = ISA_SiteAggregationMenu.menuItemTreeMap;
	for (var i in mapJson) {
		menuItemTreeMap[i] = mapJson[i];
	}
}

ISA_SiteAggregationMenu.forceUpdatePrefMap = {};
ISA_SiteAggregationMenu.forceDeleteList = [];

ISA_SiteAggregationMenu.setWidgetConf = function(_widgetConfList, isAddMulti) {
	ISA_SiteAggregationMenu.widgetConfs = _widgetConfList;
	
	function sortConf(a, b){
		if( !b.type ) return -1;
		if( !a.type ) return 1;
		if( a.type >= b.type ) return 1;
		if( a.type < b.type ) return -1;
	}
	var setGadgets = function(response){
		var gadgetList = eval("(" + response.responseText + ")");
		for(i in gadgetList){
			gadgetList[i].type = 'g_' + i+"/gadget";
			ISA_SiteAggregationMenu.widgetConfs['g_' +i +"/gadget"] = gadgetList[i];
		}
		ISA_SiteAggregationMenu.widgetConfs['Gadget'] = {
			"type": "Gadget",
			"title": ISA_R.alb_gadgetUrlSpecified,
			"UserPref": {
				"url": {
					"default_value": "",
					"display_name": ISA_R.alb_gadgetUrl,
					"datatype": "hidden",
					"name": "url",
					"admin_datatype":"url"
				  }
			}
		}
	}
	
	var url = adminHostPrefix + "/services/gadget/getGadgetJson";
	var opt = {
	  method: 'get' ,
	  asynchronous:true,
	  onSuccess: setGadgets,
	  onFailure: function(t) {
		  if(!$('menu').firstChild) $('menu').appendChild(document.createElement("div"));
		  $('menu').firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedloadingGadgetFile+"</span>";
		  msg.error(ISA_R.ams_failedloadingGadgetFile + t.status + " - " + t.statusText);
	  },
	  onException: function(r, t){
		  if(!$('menu').firstChild) $('menu').appendChild(document.createElement("div"));
		  $('menu').firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedAnalysisGadgetFile+"</span>";
		  msg.error(ISA_R.ams_failedAnalysisGadgetFile + getErrorMessage(t));
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

ISA_SiteAggregationMenu.prototype.classDef = function() {
	var self = this;
	var container;
	var addSiteTop;
	var showAll;
	var hideAll;
	var loadingMessage;
	var topMenuType;
	var sideMenuType;

	this.menuType;
	this.editSitetopIdList = [];

	/**
		Check updates
	*/
	this.checkUpdated = function() {
		if(this.isUpdated) {
			if( !confirm(ISA_R.ams_confirmChangeLost) ) {
				return false;
			}
		}
		this.isUpdated = false;
		return true;
	}
	
	this.menuTypeConfig = {topmenu:{}, sidemenu:{}};
	
	this.initialize = function(_menuType, isTreeAdminUser) {
		ISA_SiteAggregationMenu.isTreeAdminUser = isTreeAdminUser;
		
		var sidemenupath = "/mnusrv/sidemenu";
		
		if(ISA_Properties.propertiesList["displayTopMenu"]
				&& 0 < ISA_Properties.propertiesList["displayTopMenu"].value.length){
			var property = ISA_Properties.propertiesList["displayTopMenu"];
			var topmenuURL = property.value;
			if(topmenuURL=='true'){
				this.menuTypeConfig.topmenu.display = true;
			}else{
				this.menuTypeConfig.topmenu.display = false;
			}

		}
		if(ISA_Properties.propertiesList["displaySideMenu"]
				&& 0 < ISA_Properties.propertiesList["displaySideMenu"].value.length){
			var property = ISA_Properties.propertiesList["displaySideMenu"];
			var sidemenuURL = property.value;
			if(sidemenuURL == 'true'){
				this.menuTypeConfig.sidemenu.display = true;
			}
			else if(sidemenuURL == 'reference_top_menu'){
				this.menuTypeConfig.sidemenu.display = sidemenuURL;
			}else{
				this.menuTypeConfig.sidemenu.display = false;
			}
			
		}
		
		this.menuType = (!_menuType)? "topmenu" : _menuType;
		container = $( !isTreeAdminUser ? "menu":"menuTree");
		
		var refreshAllDiv = document.createElement("div");
		refreshAllDiv.className = "refreshAll";
		//$(refreshAllDiv).setStyle({clear:"both", width:"98%", whiteSpace:"nowrap"});
		
		var changeMenuDiv = document.createElement("div");
		changeMenuDiv.id = "changeMenu";
		$(changeMenuDiv).setStyle({'textAlign':'left', 'float':'left'});
		var menuSelect = document.createElement('select');
		var topmenuOption = document.createElement('option');
		topmenuOption.value = "topmenu";
		topmenuOption.appendChild(document.createTextNode(ISA_R.alb_topmenu));
		menuSelect.appendChild(topmenuOption);
		var sidemenuOption = document.createElement('option');
		sidemenuOption.value = "sidemenu";
		sidemenuOption.appendChild(document.createTextNode(ISA_R.alb_sideMenu));
		menuSelect.appendChild(sidemenuOption);
		
		if(this.menuType == "topmenu"){
			topmenuOption.selected = true;
		}else{
			sidemenuOption.selected = true;
		}
		IS_Event.observe(menuSelect, 'change', function(){
			if(!self.checkUpdated()) return;

			ISA_Admin.clearAdminCache();
            //TODO:unnecessary to make yourself new from the first
			ISA_SiteAggregationMenu.treeMenu = new ISA_SiteAggregationMenu(menuSelect.value, ISA_SiteAggregationMenu.isTreeAdminUser);
			ISA_SiteAggregationMenu.treeMenu.build();
			
		}, false, "_adminPanel");
		changeMenuDiv.appendChild(menuSelect);
		refreshAllDiv.appendChild(changeMenuDiv);
		
		// Preview the whole
		var previewDiv = ISA_Admin.createIconButton(ISA_R.alb_previewTop, ISA_R.alb_previewTop, "minibrowser.gif", "right");
		if(ISA_SiteAggregationMenu.isTreeAdminUser)
			Element.hide(previewDiv);
			
		IS_Event.observe(previewDiv, 'click', ISA_previewFormModal.init.bind(this), false, "_adminPanel");
		refreshAllDiv.appendChild(previewDiv);
		
		// Apply changes
		var commitDiv = ISA_Admin.createIconButton(ISA_R.alb_changeApply, ISA_R.alb_changeApply, "database_save.gif", "right");
		commitDiv.id = "adminmenu_commit";
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
		IS_Event.observe(commitDiv, 'click', commitMenu.bind(this, currentModal), "_adminMenu");
		Element.hide(commitDiv);
		
		// Reread
		var refreshDiv = ISA_Admin.createIconButton(ISA_R.alb_refresh, ISA_R.alb_reloadWithourSaving, "refresh.gif", "right");
		refreshAllDiv.appendChild(refreshDiv);
		refreshDiv.id = "adminmenu_refresh";
		var refreshAClick = function(e){
			if(!ISA_Admin.checkUpdated())return;
			ISA_Admin.isUpdated = false;
			self.isUpdated = false;
			
			this.editSitetopIdList = [];
			ISA_SiteAggregationMenu.removeTemp();
			ISA_Admin.TabBuilders.menu.build(this.menuType, ISA_SiteAggregationMenu.isTreeAdminUser);
		}.bind(this);
		IS_Event.observe(refreshDiv, 'click', refreshAClick.bind(this), false, "_adminMenu");
		
		// Unlock all
		var lockReleaseDiv = ISA_Admin.createIconButton(ISA_R.alb_allLockRelease, ISA_R.alb_allLockRelease, "refresh.gif", "right");
		lockReleaseDiv.id = "adminmenu_lockRelease";
		refreshAllDiv.appendChild(lockReleaseDiv);
		IS_Event.observe(lockReleaseDiv, 'click', refreshAClick.bind(this), false, "_adminMenu");
		Element.hide(lockReleaseDiv);
		
		// Add menu tree
		addSiteTop = ISA_Admin.createIconButton(ISA_R.alb_addMenuTree, ISA_R.alb_addMenuTree, "add.gif", "right");
		addSiteTop.id = "adminmenu_addSiteTop";
		Element.hide(addSiteTop);
		refreshAllDiv.appendChild(addSiteTop);

		if (!ISA_SiteAggregationMenu.isTreeAdminUser) {
			// Change order/Mode of changing topmenu
			var changeOrderEdit = ISA_Admin.createIconButton(ISA_R.alb_changeLockOrder, ISA_R.alb_changeLockOrder, "add.gif", "right");
			changeOrderEdit.id = "adminmenu_changeOrederEdit";
			refreshAllDiv.appendChild(changeOrderEdit);
			
			IS_Event.observe(changeOrderEdit, 'click', this.changeOrderEditMode.bind(this, false), false, "_adminMenu");
		}
		
		showAll = ISA_Admin.createIconButton(ISA_R.alb_deployAll, ISA_R.alb_deployAll, "chart_organisation_add.gif", "right");
		showAll.style.display = "block";
		refreshAllDiv.appendChild(showAll);		

		hideAll = ISA_Admin.createIconButton(ISA_R.alb_closeAll, ISA_R.alb_closeAll, "chart_organisation_delete.gif", "right");
		hideAll.style.display = "none";
		refreshAllDiv.appendChild(hideAll);
		
		/**
		 * Delete trashes if they still exists
		 */
		var len = container.childNodes.length;
		for(var i = 0; i < len; i++) {
			container.removeChild(container.lastChild);
		}
		
		container.appendChild(refreshAllDiv);
		
		var messageDiv = document.createElement("div");
		messageDiv.id = 'menuMessageDiv';
		messageDiv.style.clear = "both";
		messageDiv.style.display = "none";
		messageDiv.style.fontWeight = "bold";
		messageDiv.style.padding = "5px";
		messageDiv.style.fontSize = "90%";
		messageDiv.style.color = "#FF0000";
		if(this.menuTypeConfig[this.menuType].display == 'reference_top_menu'){
			messageDiv.innerHTML = ISA_R.alb_referToTopURL;
			messageDiv.style.display = "block";

			addSiteTop.style.display = "none";
			showAll.style.display = "none";

			this.disableMenu = true;
		}
		else if(!this.menuTypeConfig[this.menuType].display){
			messageDiv.innerHTML = ISA_R.alb_notDisplaySetting;
			messageDiv.style.display = "block";

			addSiteTop.style.display = "none";
			showAll.style.display = "none";

			this.disableMenu = true;
		}
		
		this.messageDiv = messageDiv;
		
		container.appendChild(messageDiv);
		
		loadingMessage = document.createElement('div');
		loadingMessage.innerHTML = "Loading...";
		loadingMessage.style.clear = "both";
		loadingMessage.style.cssFloat = "left";
		container.appendChild(loadingMessage);
		
		this.loadingMessage = loadingMessage;
	};
	
	this.changeOrderEditMode = function(isForce){
		// Request
		var url = adminHostPrefix + "/services/menu/orderLock";
		var opt = {
			method: 'post',
			contentType: "application/json",
			postBody: Object.toJSON([this.menuType, (isForce)? true : false, this.editSitetopIdList]),
			asynchronous: true,
			onSuccess: function(response){
				var resultJson = eval("(" + response.responseText + ")");
				
				if(resultJson.conflict){
					// If they conflict
					if(ISA_SiteAggregationMenu.isTreeAdminUser){
						alert(ISA_R.alb_multiUserEdit + "\n\n" + resultJson.conflictUser);
					}
					else if(confirm(ISA_R.alb_multiUserEdit+"\n\n" + resultJson.conflictUser + "\n\n"+ISA_R.alb_multiUserEdit2)){
						this.changeOrderEditMode(true);
					}
				}else{
					// Permit changes for order
					this.isOrderEditMode = true;
					
					Element.show($("adminmenu_addSiteTop"));
					Element.show("adminmenu_commit");
					Element.show("adminmenu_lockRelease");
					Element.hide("adminmenu_refresh");
					Element.hide($("adminmenu_changeOrederEdit"));
					
					// Destroy all drag event of site-top and put it again with the state of "isOrderEditMode=true" 
					var topMenuIdList = ISA_SiteAggregationMenu.topMenuIdList;
					var menuItemList = ISA_SiteAggregationMenu.menuItemList;
					topMenuIdList.each(function(topMenuId){
						var topMenuItem = menuItemList[topMenuId];
						if (topMenuItem.dragdrop) {
							if (topMenuItem.dragdrop.draggable) 
								topMenuItem.dragdrop.draggable.destroy();
							
							if(topMenuItem.dragdrop.dropElement)
								Droppables.remove(topMenuItem.dragdrop.dropElement);
						}
						
						ISA_SiteAggregationMenu.enableDragDrop(this, topMenuItem);
						
						var sortableHandleTd = $("sh_" + topMenuId);
						if(sortableHandleTd)
							Element.show(sortableHandleTd);
					}.bind(this));
					
					// dummy_top
					var dummyTopItem = ISA_SiteAggregationMenu.menuDummyItemList["dummy_top"];
					ISA_SiteAggregationMenu.enableDragDrop(this, dummyTopItem, true);
				}
			}.bind(this),
			onFailure: function(t){
				var resTxt = ISA_SiteAggregationMenu.getErrorMessage(t);
				alert(ISA_R.ams_failedLockOrder + resTxt);
				msg.error(ISA_R.ams_failedLockOrder + t.status + " - " + t.statusText + " " + resTxt);
			}.bind(this),
			onException: function(r, t){
				alert(ISA_R.ams_failedLockOrder);
				msg.error(ISA_R.ams_failedLockOrder + getErrorMessage(t));
			}.bind(this)
		};
		AjaxRequest.invoke(url, opt);		
	}
	
	this.displayMenu = function() {
		var treeMenuDiv = document.createElement("div");
		treeMenuDiv.id = 'siteaggregationmenu_treeMenu';
		if(this.disableMenu) treeMenuDiv.className = "menu_disable";
		
		var menuHeader = ISA_Admin.buildTableHeader([ISA_R.alb_title, ISA_R.alb_link], ['400px', '500px']);
		treeMenuDiv.appendChild(menuHeader);

		var menuTop = document.createElement('div');
		menuTop.className = 'ygtvchildren';
		menuTop.id = 'ygtvc0';
		
		for(var i = 0; i < ISA_SiteAggregationMenu.topMenuIdList.length; i++){
			var menuItem = ISA_SiteAggregationMenu.menuItemList[ISA_SiteAggregationMenu.topMenuIdList[i]];
			menuItem.depth = 0;
			if( i == ISA_SiteAggregationMenu.topMenuIdList.length -1) menuItem.isLast = true;
			menuTop.appendChild(this.buildMenuTree(menuItem, true));
		}

		treeMenuDiv.appendChild(menuTop);
		container.replaceChild(treeMenuDiv,loadingMessage);

		// Set the handler to control menu, here
		IS_Event.observe(showAll, 'click', function() { attachShowHideEventHandler(true, treeMenuDiv); }, "_adminMenu");
		IS_Event.observe(hideAll, 'click', function() { attachShowHideEventHandler(false, treeMenuDiv); }, "_adminMenu");
		var editorFormObj = 
			new ISA_CommonModals.EditorForm(
				addSiteTop,
				function(menuItem){
					var url = adminHostPrefix + "/services/menu/addTopMenuItem";
					var newMenuItem = ISA_SiteAggregationMenu.getUpdMenuItem(menuItem, self.menuType);
					newMenuItem.add = true;
					var allertSetting = $F('allertSetting');
					var opt = {
					  method: 'post',
					  contentType: "application/json",
					  postBody: Object.toJSON([
					  	newMenuItem.id,
					  	newMenuItem.title,
					  	newMenuItem.href || "",
					  	newMenuItem.display  || "",
					  	newMenuItem.serviceURL  || "",
					  	newMenuItem.serviceAuthType  || "",
						allertSetting,
					  	newMenuItem.menuType,
					  	newMenuItem.auths || null,
						newMenuItem.menuTreeAdmins || null,
					  ]),
					  asynchronous:true,
					  onSuccess: function(response){
						  self.isUpdated = true;
						  self.editSitetopIdList.push(newMenuItem.id);
						  ISA_SiteAggregationMenu.addMenuTree(self, newMenuItem, menuItem);
					  },
					  onFailure: function(t) {
					  	  var resTxt = ISA_SiteAggregationMenu.getErrorMessage(t);
						  alert(ISA_R.ams_failedAddMenu + resTxt);
						  msg.error(ISA_R.ams_failedAddMenu  + t.status + " - " + t.statusText + " " + resTxt);
					  },
					  onException: function(r, t){
						  alert(ISA_R.ams_failedAddMenu );
						  msg.error(ISA_R.ams_failedAddMenu  + getErrorMessage(t));
					  },
					  contentType: 'text/plain'
					};
					AjaxRequest.invoke(url, opt);
				},{
					showServiceURL:true,
					displayWidgetFieldSet:false,
					displayAlertFieldSet:true,
					setDefaultValue: false,
					generateId: true,
					menuFieldSetLegend: ISA_R.alb_settingMenuLink,
					displayMenuTreeAdminsFieldSet:true
				});
		
		IS_Event.observe(addSiteTop, 'click', function(){editorFormObj.showEditorForm({title:ISA_R.alb_newMenu});}, false, "_adminMenu");
		
		var div = document.createElement("div");
		div.style.visibility = "hidden";
		div.style.width = "100px";
		container.appendChild(div);

		for(var i = 0; i < ISA_SiteAggregationMenu.topMenuIdList.length; i++){
			var menuItem = ISA_SiteAggregationMenu.menuItemList[ISA_SiteAggregationMenu.topMenuIdList[i]];
			
			ISA_SiteAggregationMenu.enableDragDrop(this, menuItem);
		}
		
		var dummyMenuItem = {
		  id: 'dummy_top',
		  title: '',
		  parentId: null,
		  depth: 0
		};
		ISA_SiteAggregationMenu.menuDummyItemList[dummyMenuItem.id] = dummyMenuItem;
		var dummyDiv = self.buildMenuTree(dummyMenuItem, true);
		menuTop.appendChild(dummyDiv);
		$('i_' + dummyMenuItem.id).className = 'ygtvspacer';
		dummyDiv.style.height = '5px';
		dummyDiv.style.overflow = 'hidden';
		$('tc_' + dummyMenuItem.id).parentNode.style.verticalAlign = 'top';
		$('tc_' + dummyMenuItem.id).style.height = '5px';
		
		ISA_SiteAggregationMenu.enableDragDrop(this, dummyMenuItem, true);
	}

	/**
	 * Make menu items on sitemap
	 * t_:  Prefix of the ID for menu box
	 * tc_: Prefix of the ID for DIV which shows menu and abbreviation of menu container
	 * ti_: Prefix of the ID for DIV which includes icons which set handler for abbreviation of menu icons and drop
	 */
	this.makeMenu = function(menuItem, isSiteTop) {
		var menuDiv = document.createElement('div');
		menuDiv.id = menuItem.id;
		menuDiv.style.clear = 'both';
		menuDiv.className = 'ygtvitem';
		var menuTable = document.createElement('table');
		menuTable.className = 'menuItemTable';
		menuTable.cellPadding = 0;
		menuTable.cellSpacing = 0;
		//menuTable.style.width = '100%';//Without this line, the Title will unexpectedly have a break in the middle after dropping
		menuDiv.appendChild(menuTable);
		var menuTbody = document.createElement('tbody');
		menuTable.appendChild(menuTbody);
		var menuTr = document.createElement('tr');
		menuTbody.appendChild(menuTr);

		var parents = [menuItem.depth];
		var parentMenu = menuItem;
		for(var i = menuItem.depth; i > 0; i--){
			parentMenu = ISA_SiteAggregationMenu.menuItemList[parentMenu.parentId];
			parents[i-1] = parentMenu;
		}
		
		if(menuItem.depth > 0){
			for(var i = 0; i < menuItem.depth; i++){
				var depthTd = document.createElement('td');
				if(parents[i].isLast){
					depthTd.className = 'ygtvblankdepthcell';
				}else{
					depthTd.className = 'ygtvdepthcell';
				}
				menuTr.appendChild(depthTd);
				var depthDiv = document.createElement('div');
				depthDiv.className = 'ygtvspacer';
				depthTd.appendChild(depthDiv);
			}
		}
		
		var lineTd = document.createElement('td');
		lineTd.id = "i_" + menuItem.id;
		var lineDiv = document.createElement('div');
		lineDiv.className = 'ygtvspacer';
		lineTd.appendChild(lineDiv);
		
		menuTr.appendChild(lineTd);

		var itemTd = document.createElement('td');
		menuTr.appendChild(itemTd);
		
		if ( ISA_SiteAggregationMenu.menuItemTreeMap[menuItem.id] && ISA_SiteAggregationMenu.menuItemTreeMap[menuItem.id].length > 0) {
			if(menuItem.isLast){
				lineTd.className = 'ygtvlp';
			}else{
				lineTd.className = 'ygtvtp';
			}
			IS_Event.observe(lineTd, 'click', getClickHandler(lineTd, menuItem), false, "_adminMenu");
		} else {
			if(menuItem.isLast){
				lineTd.className = 'ygtvln';
			}else{
				lineTd.className = 'ygtvtn';
			}
		}
		var divMenuItem = document.createElement("div");
		divMenuItem.id = "tc_" + menuItem.id;

		if(isSiteTop){
			divMenuItem.className = "siteTop";
		}else{
			divMenuItem.className = "siteChild";
		}
		
		var menuItemTable = document.createElement('table');//If this is not 'table', scriptaculous does not work
		menuItemTable.cellPadding = 0;
		menuItemTable.cellSpacing = 0;
		
		var menuItemTBody = document.createElement('tbody');
		menuItemTable.appendChild(menuItemTBody);
		var menuItemTr = document.createElement('tr');
		menuItemTBody.appendChild(menuItemTr);

		var menuItemIconTd = document.createElement('td');
		menuItemTr.appendChild(menuItemIconTd);
		
		var divMenuItemIcon = document.createElement("div");
		divMenuItemIcon.id = "ti_" + menuItem.id;
		
		if ( menuItem.type ){
			divMenuItemIcon.className = 'treemenuItemIcon';
			IS_Widget.setIcon(divMenuItemIcon, menuItem.type, {multi:getBooleanValue(menuItem.multi)});
		}
		menuItemIconTd.appendChild(divMenuItemIcon);
		
		if(!ISA_SiteAggregationMenu.menuDummyItemList[menuItem.id] && !menuItem.parentId){
			var sortableHandleTd = document.createElement('td');
			var handleImage = document.createElement("img");
			handleImage.src = imageURL + "drag.gif";
			handleImage.style.cursor = "pointer";
			sortableHandleTd.appendChild(handleImage);
			menuItemTr.appendChild(sortableHandleTd);
			Element.hide(sortableHandleTd);
			sortableHandleTd.id = "sh_" + menuItem.id;
		}
		
		var menuItemTitleTd = document.createElement('td');
		menuItemTr.appendChild(menuItemTitleTd);
		
		var divMenuTitle = document.createElement("div");
		divMenuTitle.id = "t_" + menuItem.id;
		divMenuTitle.className = (menuItem.isEditMode)?"treeMenuTitle_edit" : "treeMenuTitle";
		divMenuTitle.appendChild(document.createTextNode(menuItem.directoryTitle || menuItem.title));
		divMenuTitle.style.cursor ="pointer";
		
		//divMenuItem.appendChild(divMenuTitle);
		menuItemTitleTd.appendChild(divMenuTitle);
		divMenuItem.appendChild(menuItemTable);
		// Make navigator
		createNavigator(menuItem, menuItemTr, this);
		
		itemTd.appendChild(divMenuItem);

		var mdiv = document.createElement('div');
		mdiv.id = "ml_" + menuItem.id;
		mdiv.className = 'menuItemLink';
		if (menuItem.href) {
			var aTag = document.createElement('a');
			aTag.id = 'tl_' + menuItem.id;
			aTag.href = menuItem.href;
			aTag.appendChild(document.createTextNode(menuItem.href));
			aTag.target="_blank";
			mdiv.appendChild(aTag);
		}
		menuDiv.appendChild(mdiv);
		   
		return menuDiv;
	}

	function commitMenu(currentModal) {
		var isForceUpdate = false;
		for(i in ISA_SiteAggregationMenu.forceUpdatePrefMap)isForceUpdate = true;
		if(isForceUpdate){
			if(!confirm(ISA_R.ams_confirmForceUpdateUserPrefs)){
				setTimeout(function(){
					currentModal.close();
				},100);
				return;
			}
		}
		var currentTopUrl = ISA_Properties.propertiesList["displayTopMenu"].value;
		var currentSideUrl = ISA_Properties.propertiesList["displaySideMenu"].value;

		var url = adminHostPrefix + "/services/menu/commitMenu";
		var opt = {
			method: 'post' ,
			contentType: "application/x-www-form-urlencoded",
			postBody: 'menuType=' + this.menuType
			  + '&forceUpdateMap=' + Object.toJSON(ISA_SiteAggregationMenu.forceUpdatePrefMap)
				+ '&forceDeleteList=' + Object.toJSON(ISA_SiteAggregationMenu.forceDeleteList)
				+ '&editSitetopIdList=' + Object.toJSON(this.editSitetopIdList),
			asynchronous:true,
			onSuccess: function(response){
				ISA_TempGadgetsConfs = [];
				currentModal.update(ISA_R.ams_changeUpdated);
				ISA_Admin.isUpdated = false;
				self.isUpdated = false;
				ISA_SiteAggregationMenu.forceUpdatePrefMap = {};
			},
			onFailure: function(t) {
				var resTxt = ISA_SiteAggregationMenu.getErrorMessage(t);
				alert(ISA_R.ams_failedCommitMenu + resTxt);
				msg.error(ISA_R.ams_failedCommitMenu + t.status + " - " + t.statusText + " " + resTxt);
			},
			onException: function(r, t){
				alert(ISA_R.ams_failedCommitMenu );
				msg.error(ISA_R.ams_failedCommitMenu  + getErrorMessage(t));
			},
			onComplete: function(){
				setTimeout(function(){
					currentModal.close();
				},500);
			}
		};
		AjaxRequest.invoke(url, opt);
		
	}

	function attachShowHideEventHandler( doShow, treeMenuDiv ) {
		treeMenuDiv.style.display = "none";
		treeMenuDiv.parentNode.appendChild(loadingMessage);
		var execAllShowHide = function(){
			for(var i = 0; i < ISA_SiteAggregationMenu.topMenuIdList.length; i++){

				for (mItem in ISA_SiteAggregationMenu.menuItemList) {
					if(typeof mItem == "function") continue;
					var menuGroupId = "tg_" + ISA_SiteAggregationMenu.menuItemList[mItem].id;
					var node = $(menuGroupId);
					
					if (node) {
						var mgi = $("i_" + ISA_SiteAggregationMenu.menuItemList[mItem].id);
						if (doShow) {
							self.subMenuOpen(mgi, ISA_SiteAggregationMenu.menuItemList[mItem]);
							showAll.style.display = "none";
							hideAll.style.display = "block";
						} else {
							self.subMenuClose(mgi);
							showAll.style.display = "block";
							hideAll.style.display = "none";
						}
					}
				}
			}
			loadingMessage.parentNode.removeChild(loadingMessage);
			treeMenuDiv.style.display = "block";
		}
		setTimeout(execAllShowHide,1);
	}

	this.buildMenuTree = function (menuItem, isSiteTop) {
		var menuDiv = this.makeMenu(menuItem, isSiteTop);
		var childList = ISA_SiteAggregationMenu.menuItemTreeMap[menuItem.id];
		
		var childrenDiv = document.createElement('div');
		childrenDiv.id = "tg_" + menuItem.id;
		childrenDiv.className = 'ygtvchildren';

		if(childList){
			for(var j = 0; j < childList.length;j++){
				var child = ISA_SiteAggregationMenu.menuItemList[childList[j]];
				if( j == childList.length - 1 ) child.isLast = true;
				child.isChildrenBuildSiteMap = false;
			}
		}
		menuDiv.appendChild(childrenDiv);
		return menuDiv;
	}

	this.buildMenuTreeChild = function (menuItem){
		var childList = ISA_SiteAggregationMenu.menuItemTreeMap[menuItem.id];
		var childrenDiv = document.getElementById("tg_" + menuItem.id);
		if(childList){
//			var childrenDiv = document.getElementById("tg_" + menuItem.id);
			for(var j = 0; j < childList.length;j++){
				var child = ISA_SiteAggregationMenu.menuItemList[childList[j]];
				child.depth = menuItem.depth + 1;
				
				childrenDiv.appendChild(self.buildMenuTree(child));
			}
		}
		return childrenDiv;
	}
	
	this.refreshTreeChild = function (menuItem){
		var menuDiv  = $(menuItem.id);
		var childrenDiv  = $('tg_' + menuItem.id);
		menuDiv.removeChild(childrenDiv);
		
		var childList = ISA_SiteAggregationMenu.menuItemTreeMap[menuItem.id];
		if(childList){
			var childrenDiv = document.createElement('div');
			childrenDiv.className = 'ygtvchildren';
			childrenDiv.id = "tg_" + menuItem.id;
			for(var j = 0; j < childList.length;j++){
				var child = ISA_SiteAggregationMenu.menuItemList[childList[j]];
				child.depth = menuItem.depth + 1;
				childrenDiv.appendChild(self.buildMenuTree(child));
			}
			menuDiv.appendChild(childrenDiv);
		}
	}

	this.rebuildMenuTree = function(sitetopId, replaceTo){
		var sitetopItem = ISA_SiteAggregationMenu.menuItemList[sitetopId];
		sitetopItem.depth = 0;
		var topMenuIdList = ISA_SiteAggregationMenu.topMenuIdList;
		if (topMenuIdList.indexOf(sitetopItem.id) == (topMenuIdList.length - 1)) 
			sitetopItem.isLast = true;

		ISA_DragDrop.destroyDropEvent(sitetopId);

		var newTopMenu = this.buildMenuTree(sitetopItem, true);
		replaceTo.parentNode.replaceChild(newTopMenu, replaceTo);
		
		var sortableHandleTd = $("sh_" + sitetopId);
		if(sortableHandleTd && this.isOrderEditMode)
			Element.show(sortableHandleTd);
		
		ISA_SiteAggregationMenu.enableDragDrop(this, sitetopItem);
	}

	function getClickHandler(icon, menuItem){
		return function(e){ subMenuOpenClose(e, icon, menuItem);} ;
	}

	function subMenuOpenClose(e, icon, menuItem) {
		var el = window.event ? icon : e ? e.currentTarget : null;
		if (!el) return;
		
		if(el.className == 'ygtvtp' || el.className == 'ygtvlp'){
			self.subMenuOpen(el, menuItem);
		}else{
			self.subMenuClose(el);
		}
	}

	this.subMenuOpen = function(el, menuItem, isForcible){
		if(isForcible){
			// Forced Open
			el.className = (menuItem.isLast)? 'ygtvlp' : 'ygtvtp';
			IS_Event.observe(el, 'click', getClickHandler(el, menuItem), false, "_adminMenu");
		}
		
		if(el.className == "ygtvtm" || el.className == 'ygtvlm') return;
		if(el.className == "ygtvtn" || el.className == 'ygtvln') return;
		if(el.className == 'ygtvtp'){
			el.className = 'ygtvtm';
		}else{
			el.className = 'ygtvlm';
		}
		
//		var liNode = el.parentNode.parentNode;
		var menuDiv = el.parentNode.parentNode.parentNode.parentNode;

		if (!menuItem.isChildrenBuildSiteMap) {
			var childrenDiv = this.buildMenuTreeChild(menuItem);
			menuDiv.appendChild(childrenDiv);
			menuItem.isChildrenBuildSiteMap = true;
			var childList = ISA_SiteAggregationMenu.menuItemTreeMap[menuItem.id];
			if(childList){
				for(var j = 0; j < childList.length;j++){
					var child = ISA_SiteAggregationMenu.menuItemList[childList[j]];
					ISA_SiteAggregationMenu.enableDragDrop(this, child);
				}
			}
			
			var dummyMenuItem = {
			  id: 'dummy_' + menuItem.id,
			  title: '',
			  parentId: menuItem.id,
			  depth: (menuItem.depth + 1),
			  isEditMode: menuItem.isEditMode
			};
			ISA_SiteAggregationMenu.menuDummyItemList[dummyMenuItem.id] = dummyMenuItem;
			var dummyDiv = this.makeMenu(dummyMenuItem);
			childrenDiv.appendChild(dummyDiv);
			$('i_' + dummyMenuItem.id).className = 'ygtvspacer';
			dummyDiv.style.height = '5px';
			dummyDiv.style.overflow = 'hidden';
			$('tc_' + dummyMenuItem.id).parentNode.style.verticalAlign = 'top';
			$('tc_' + dummyMenuItem.id).style.height = '5px';
			ISA_SiteAggregationMenu.enableDragDrop(this, dummyMenuItem, true);
		}

		for (var i = 0; i < menuDiv.childNodes.length; i++) {
			var node = menuDiv.childNodes[i];
			if (node.nodeName.toLowerCase() == 'div') {
				node.style.display = 'block';
			}
		}
	}

	this.subMenuClose = function(el){
		if(el.className == 'ygtvtp' || el.className == 'ygtvlp') return;
		if(el.className == "ygtvtn" || el.className == 'ygtvln') return;
		
		if(el.className == 'ygtvtm'){
			el.className = "ygtvtp";
		}else{
			el.className = 'ygtvlp';
		}
		var menuDiv = el.parentNode.parentNode.parentNode.parentNode;
		
		for (var i = 0; i < menuDiv.childNodes.length; i++) {
			var node = menuDiv.childNodes[i];
			// Not remove menu link
			if(node.className == "menuItemLink") continue;
			if (node.nodeName.toLowerCase() == 'div') {
				node.style.display = 'none';
			}
		}
	}

	function createNavigator(menuItem, menuItemElement) {
		new ISA_SiteAggregationMenu.Navigator(self, menuItem, menuItemElement, self);
	}
	
	this.build = function() {
		var url = adminHostPrefix + "/services/menu/getMenuTree";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			postBody: Object.toJSON([
				this.menuType
			]),
			asynchronous:true,
			onSuccess: function(response){
				loadGadgetIcons();
				if (eval(response.responseText)) {
					getWidgetConf();
					getTreeAdminUsers();
				}
			}.bind(this),
			on404: function(t) {
				msg.error(ISA_R.ams_menuNotFound + t.status + " - " + t.statusText);
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_menuNotFound+"</span>";
			},
			onFailure: function(t) {
				msg.error(ISA_R.ams_failedLoadingMenu + t.status + " - " + t.statusText);
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingMenu+"</span>";
			},
			onException: function(r, t){
				msg.error(ISA_R.ams_failedLoadingMenu + getErrorMessage(t));
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingMenu+"</span>";
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
	
	function loadGadgetIcons(){
		var opt = {
			onSuccess:function(req){
				IS_WidgetIcons = req.responseText.evalJSON();
			},
			onFailure:function(r, e){
				msg.error(ISA_R.ams_failedLoadIcons+r.status+" - "+r.statusText);
			},
			onException:function(r, e){
				msg.error(ISA_R.ams_failedLoadIcons+getErrorMessage(e));
			}
			
		};
		AjaxRequest.invoke(hostPrefix + "/gadgeticon" , opt);
	}
	
	function getTreeAdminUsers(){
		ISA_SiteAggregationMenu.menuTreeAdminUsers = [];
		var url = adminHostPrefix + "/services/menu/getMenuTreeAdminUsers";
		var opt = {
			method: 'get' ,
			asynchronous:true,
			onSuccess: function(response){
				ISA_SiteAggregationMenu.menuTreeAdminUsers = eval(response.responseText);
			},
			on404: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_treeMenuAdminsNotFound+"</span>";
				msg.error(ISA_R.ams_treeMenuAdminsNotFound + t.status + " - " + t.statusText);
			},
			onFailure: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingTreeMenuAdmins+"</span>";
				msg.error(ISA_R.ams_failedLoadingTreeMenuAdmins + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingTreeMenuAdmins+"</span>";
				msg.error(ISA_R.ams_failedLoadingTreeMenuAdmins + getErrorMessage(t));
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
	
	function getWidgetConf() {
		var url = adminHostPrefix + "/services/widgetConf/getWidgetConfJson";
		var opt = {
			method: 'get' ,
			asynchronous:true,
			onSuccess: function(response){eval("ISA_SiteAggregationMenu.setWidgetConf("+response.responseText+", true)"); self.displayMenu(); ISA_SiteAggregationMenu.forceUpdatePrefMap = {}},
			on404: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_widgetNotFound+"</span>";
				msg.error(ISA_R.ams_widgetNotFound + t.status + " - " + t.statusText);
			},
			onFailure: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingWidget+"</span>";
				msg.error(ISA_R.ams_failedLoadingWidget + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingWidget+"</span>";
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
	}
};

ISA_SiteAggregationMenu.removeTemp = function(){
	var url = adminHostPrefix + "/services/menu/removeTempMenu";
	var opt = {
		method: 'delete' ,
		asynchronous: false,
		timeout: ajaxRequestTimeout,
		onSuccess: function(response){
			msg.info(ISA_R.ams_deleteEdittingMenuData);
		}.bind(this),
		onFailure: function(t) {
			msg.error(ISA_R.ams_failedDeleteEdittingMenuData + t.status + " - " + t.statusText);
		}
	};
	AjaxRequest.invoke(url, opt);
}

ISA_SiteAggregationMenu.getUpdMenuItem = function(menuItem, menuType){
	//var newMenuItem = {};
	//newMenuItem.id = menuItem.id;
	//newMenuItem.parentId = menuItem.parentId;
	menuItem.type = ISA_CommonModals.EditorForm.getSelectType();
	
	var title = ISA_Admin.trim($("formTitle").value);
	var conf = ISA_SiteAggregationMenu.widgetConfs[menuItem.type];
	if(conf && conf.ModulePrefs && conf.ModulePrefs.directory_title){
		// upload gadget only
		menuItem.directoryTitle = title;
		menuItem.title = conf.ModulePrefs.title || title;
	} else {
		var originalTitle;
		if( $("formOriginalTitle"))
			originalTitle = ISA_Admin.trim($F("formOriginalTitle"));
		
		if( originalTitle && originalTitle.length > 0 ) {
			menuItem.title = originalTitle;
			menuItem.directoryTitle = title;
		} else {
			menuItem.title = title;
			delete menuItem.directoryTitle;
		}
	}
	
	menuItem.href =  $("formHref").value;
	menuItem.display = $("formDisplayInline").value;
	var formServiceURL = $("formServiceURL");
	if(formServiceURL) {
		menuItem.serviceURL = formServiceURL.value;
		var formServiceAuthType = $("formServiceAuthType");
		if(formServiceAuthType) menuItem.serviceAuthType = formServiceAuthType.value;
		var formServiceAuthParamName = $('formServiceAuthParamName');
		if(formServiceAuthParamName && formServiceAuthParamName.value) menuItem.serviceAuthType += ' ' + encodeURIComponent(formServiceAuthParamName.value);
	}
	
	var formHeaderOnly = $("formHeaderOnly");
	if (formHeaderOnly) {
		menuItem.linkDisabled = formHeaderOnly.checked;
	}else{
		menuItem.linkDisabled = false;
	}
	
	var formMulti = $("formMulti");
	menuItem.multi = formMulti ? formMulti.checked : false;
	
	menuItem.properties = ISA_CommonModals.EditorForm.getProperty(menuItem);
	if($("formIsPublic").checked == true){
		menuItem.auths = undefined;
	}else{
		menuItem.auths = authorizations;
	}
	if(typeof menuTreeAdmins != 'undefined')
		menuItem.menuTreeAdmins = menuTreeAdmins;
	
	menuItem.menuType = menuType;//For sending to server
	return menuItem;
}

ISA_SiteAggregationMenu.getForceUpdPrefs = function(menuItem){
	var forceUpdatePrefs = {};
	menuItem.type = ISA_CommonModals.EditorForm.getSelectType();

	var forceUpdateTitle =  $F('FUP_TITLE');
	if(forceUpdateTitle) forceUpdatePrefs['__MENU_TITLE__'] = {implied:false};
	var forceUpdateHref = $F('FUP_HREF');
	if(forceUpdateHref) forceUpdatePrefs['__MENU_HREF__'] = {implied:true}

	var widgetType = menuItem.type;//ISA_CommonModals.EditorForm.getSelectType();
	if(widgetType){
		if("MultiRssReader" == widgetType) widgetType = "RssReader";
		var conf = ISA_SiteAggregationMenu.widgetConfs[widgetType];

		var userPrefs = conf.UserPref;
		for( i in userPrefs){
			if( !( userPrefs[i] instanceof Function ) ){
				var userPref = userPrefs[i];
				var fupPrefNode = $('FUP_' + userPref.name);
				if(fupPrefNode){
					var forceUpdatePref = $F('FUP_' + userPref.name);
					if(forceUpdatePref)
					  forceUpdatePrefs[userPref.name] = {implied: (userPrefs[i].datatype == 'hidden' ? true: false)}; 
				}
			}
		}
	}
	return forceUpdatePrefs;
}

ISA_SiteAggregationMenu.addMenuTree = function(menuObj, newMenuItem, menuItem){
	// Necessary to add new items at last
	newMenuItem.isLast = true;
	
	// Necessary to add new items in edit mode 
	newMenuItem.isEditMode = true;

	// Judge whether it is SiteTop or not
	var isSiteTop = newMenuItem.parentId ? false : true;
	if(!isSiteTop){
		newMenuItem.depth = menuItem.depth + 1;
		var menuLineTd = $("i_" + menuItem.id);
		ISA_SiteAggregationMenu.treeMenu.subMenuOpen(menuLineTd, menuItem,
			(menuLineTd.className == 'ygtvln' || menuLineTd.className == 'ygtvtn'));
	}else{
		newMenuItem.depth = 0;
		ISA_SiteAggregationMenu.topMenuIdList.push(newMenuItem.id);
	}
	
	var newMenuDiv = ISA_SiteAggregationMenu.treeMenu.buildMenuTree(newMenuItem, isSiteTop);
	var menuGroup;
	var dummy;
	if(!isSiteTop){
		menuGroup = $('tg_' + menuItem.id);
		dummy = $("dummy_" + menuItem.id);
	}else{
		menuGroup = $('ygtvc0');
		dummy = $("dummy_top");
	}
	
	// Return the flag of 'isLast' for children belonging the parent
	var childNodes = menuGroup.childNodes;
	if(childNodes.length > 1){
		var childItem = ISA_SiteAggregationMenu.menuItemList[childNodes[childNodes.length-2].id];
		if(childItem){
			var lineTd = $('i_' + childItem.id);
			ISA_DragDrop.changeLastLineState(lineTd, childItem);
		}
	}
	
	menuGroup.insertBefore(newMenuDiv,dummy);
	
	// Draw lines
	if(newMenuDiv.previousSibling)
		ISA_SiteAggregationMenu.updateLine(newMenuDiv.previousSibling.id);
	
	ISA_SiteAggregationMenu.enableDragDrop(menuObj, newMenuItem);
	
	ISA_SiteAggregationMenu.menuItemList[newMenuItem.id] = newMenuItem;

	var sortableHandleTd = $("sh_" + newMenuItem.id);
	if(sortableHandleTd && menuObj.isOrderEditMode)
		Element.show(sortableHandleTd);

	Control.Modal.close();
}

ISA_SiteAggregationMenu.updateMenuTree = function(newMenuItem, menuItem){
	newMenuItem.depth = menuItem.depth;
	$('t_' + menuItem.id).innerHTML = escapeHTMLEntity(newMenuItem.directoryTitle || newMenuItem.title);
	var hrefTag = $('tl_' + menuItem.id);
	if(!hrefTag){
		hrefTag = document.createElement('a');
		hrefTag.id = 'tl_' + menuItem.id;
		hrefTag.target="_blank";
		$('ml_' + menuItem.id).appendChild(hrefTag);
	}
	hrefTag.href = newMenuItem.href;
	hrefTag.innerHTML = newMenuItem.href;
	
	//Disapprove of changing types
	//Likely to be changed to a state where you can drop sevral
	var typeIcon = $('ti_' + menuItem.id);
	if(typeIcon){
		if(newMenuItem.type){
			IS_Widget.setIcon(typeIcon, newMenuItem.type, {multi:newMenuItem.multi});
		}else{
			typeIcon.className = "";
		}
	}

	ISA_SiteAggregationMenu.menuItemList[newMenuItem.id] = newMenuItem;
	Control.Modal.close();
}

ISA_SiteAggregationMenu.removeMenuTree = function(removeItem){
	var removeNode = $(removeItem.id);
	// If you delete 'Last' of the tree, replace 'Last' at the previous node
	if(removeItem.isLast){
		if(removeNode.previousSibling){
			var nextLastNode = removeNode.previousSibling;
			var nextLastItem = ISA_SiteAggregationMenu.menuItemList[nextLastNode.id];
			var nextLastIcon = $("i_" + nextLastNode.id);
			ISA_DragDrop.changeLastLineState(nextLastIcon, nextLastItem);
			nextLastItem.isLast = true;
			ISA_SiteAggregationMenu.menuItemList[nextLastNode.id] = null;
			ISA_SiteAggregationMenu.menuItemList[nextLastNode.id] = nextLastItem;
			// Remove unnecessary lines
			ISA_SiteAggregationMenu.updateLine(nextLastNode.id);
		}else{
			var parentItem = ISA_SiteAggregationMenu.menuItemList[removeItem.parentId];
			if (parentItem) {
				var closeLineTd = $("i_" + parentItem.id);
				ISA_SiteAggregationMenu.treeMenu.subMenuClose(closeLineTd);
			}
		}
	}

	// Remove from Drag&Drop
	ISA_DragDrop.destroy(removeItem.id);

	// Remove from Object variables
	if(removeItem.parentId) {
		var childList = ISA_SiteAggregationMenu.menuItemTreeMap[removeItem.parentId];
		if(childList){
			for(var j = 0; j < childList.length;j++){
				if(childList[j] == removeItem.id){
					delete ISA_SiteAggregationMenu.menuItemList[childList[j]];
					childList.splice(j,1);
					ISA_SiteAggregationMenu.menuItemTreeMap[removeItem.parentId] = null;
					ISA_SiteAggregationMenu.menuItemTreeMap[removeItem.parentId] = childList;
					break;
				}
			}
		}
		// if all children disappear, remove the icon, '-'
		childList = ISA_SiteAggregationMenu.menuItemTreeMap[removeItem.parentId];
		if(childList == null || childList == ""){
			var parentElement = $("i_" + removeItem.parentId);
			if(parentElement.className == "ygtvlm"){
				parentElement.className = "ygtvln";
			}else{
				parentElement.className = "ygtvtn";
			}
		}
	}

	// Remove from HTMLDocuments
	removeNode.parentNode.removeChild(removeNode);

	Control.Modal.close();
}

ISA_SiteAggregationMenu.refreshMenuTree = function(treeJson){
	
	function setSubMenu(list, map){
		/*
		var childList = ISA_SiteAggregationMenu.menuItemTreeMap[menuItem.id];
		for(var i = 0; i < childList.length ; i++){
			if(!list[childList[i]]){
				ISA_SiteAggregationMenu.menuItemList[childList[i]] = undefined;
			}
		}*/
		for(i in list){
			ISA_SiteAggregationMenu.menuItemList[i] = list[i];
		}
		for(i in map){
			ISA_SiteAggregationMenu.menuItemTreeMap[i] = map[i];
		}
		ISA_SiteAggregationMenu.menuItemList[menuItem.id].parentId = menuItem.parentId;
		ISA_SiteAggregationMenu.treeMenu.refreshTreeChild(menuItem);
		
		self.currentModal.close();
	}
	eval(treeJson);
	
}

ISA_SiteAggregationMenu.updateLine = function(parentId){
	var parentNode = $("tg_" + parentId);
	if(!parentNode) return;
	var firstChildNode = parentNode.firstChild;
	var siblingChildNode;
	if(firstChildNode){
		siblingChildNode = firstChildNode;
		while(siblingChildNode){
			var iconTd = $("i_" + siblingChildNode.id);
			if(iconTd.previousSibling){
				var removeTd = iconTd.previousSibling;
				var menuTr = removeTd.parentNode;
				var depthTd = document.createElement('td');
				if(removeTd.className != 'ygtvdepthcell'){
					// Draw lines
					depthTd.className = 'ygtvdepthcell';
				}else{
					// Remove Lines
					depthTd.className = 'ygtvblankdepthcell';
				}
				menuTr.removeChild(removeTd);
				menuTr.insertBefore(depthTd, iconTd);
				var depthDiv = document.createElement('div');
				depthDiv.className = 'ygtvspacer';
				depthTd.appendChild(depthDiv);
			}

			siblingChildNode = siblingChildNode.nextSibling;
		}
	}
}

ISA_SiteAggregationMenu.enableDragDrop = function(menuObj, menuItem, noDraggable){
	if (menuItem.isEditMode || (!menuItem.parentId && menuObj.isOrderEditMode)) {
		menuItem.dragdrop = new ISA_DragDrop.SiteAggregationMenu(menuObj, menuItem, noDraggable);
	}
}

/**
 * menuItem.id
 * menuItem.title
 * menuItem.href
 * menuItem.display
 * menuItem.type
 * menuItem.parentId
 * menuItem.properties
 */
ISA_SiteAggregationMenu.Navigator = IS_Class.create();
ISA_SiteAggregationMenu.Navigator.prototype.classDef = function() {
	var self = this;
	var isBuilt;
	var binder;
	var menuItem;
	var menuItemElement;
	var menuObj;
	
	this.initialize = function(_binder, _menuItem, _menuItemElement, _menuObj) {
		binder = _binder;
		menuItem = _menuItem;
		menuItemElement = _menuItemElement;
		menuObj = _menuObj;
		
		IS_Event.observe(document, 'click', this.hideTitleEditor.bind(this), false, "_adminMenu");
		
		IS_Event.observe(menuItemElement, 'contextmenu', this.showTitleEditor.bind(this), false, "_adminMenu");
	};
	
	this.buildNavigator = function (e){
		isBuilt = true;
		var typeList = new Array("Information","Information2","Ranking","Ticker");
		
		this.titleEditBox = document.createElement("div");
//		this.titleEditBox.className = (widget.parent)? "subWidgetTitleEditor" : "widgetTitleEditor";
		this.titleEditBox.style.position = "absolute";
		this.titleEditBox.style.display = "none";

		var editTitleTable = document.createElement("table");
		editTitleTable.cellSpacing = "0";
		editTitleTable.cellPadding = "3";
		editTitleTable.className = "treemenuNavigator";

		var editTitleTbody = document.createElement("tbody");
		editTitleTable.appendChild(editTitleTbody);

		var editTitleTr = document.createElement("tr");
		editTitleTbody.appendChild(editTitleTr);

		var editTitleTd = document.createElement("td");
		editTitleTr.appendChild(editTitleTd);

		var editDiv;
		var editA;
		var editImg;
		
		if (menuItem.isEditMode) {
			editDiv = ISA_Admin.createIconButton(ISA_R.alb_edit, ISA_R.alb_editMenu, "edit.gif");
			editDiv.id = "upd_" + menuItem.id;
			editDiv.style.margin = "0";
			editTitleTd.appendChild(editDiv);
			
			var editorFormObj = new ISA_CommonModals.EditorForm(editDiv, function(menuItem){
				var url = adminHostPrefix + "/services/menu/updateMenuItem";
				var newMenuItem = ISA_SiteAggregationMenu.getUpdMenuItem(menuItem, menuObj.menuType);
				if (menuItem.serviceURL && !newMenuItem.serviceURL) {
					alert(ISA_R.ams_externalServiceDataNotDelete);
					return true;
				}
				var allertSetting = $F('allertSetting');
				var forceUpdatePrefs = ISA_SiteAggregationMenu.getForceUpdPrefs(newMenuItem);
				var opt = {
					method: 'post',
					contentType: "application/json",
					postBody: Object.toJSON([
						newMenuItem.id, 
						newMenuItem.title || "", 
						newMenuItem.href || "", 
						newMenuItem.display, 
						newMenuItem.type, 
						newMenuItem.serviceURL || "", 
						newMenuItem.serviceAuthType || "", 
						newMenuItem.properties, allertSetting, 
						newMenuItem.menuType, 
						newMenuItem.auths || null, 
						newMenuItem.menuTreeAdmins || null, 
						newMenuItem.linkDisabled, 
						newMenuItem.directoryTitle || "", 
						ISA_SiteAggregationMenu.getSitetopId(menuItem),
						newMenuItem.multi || false
					]),
					asynchronous: true,
					onSuccess: function(response){
						binder.isUpdated = true;
						ISA_SiteAggregationMenu.updateMenuTree(newMenuItem, menuItem);
						ISA_SiteAggregationMenu.forceUpdatePrefMap[newMenuItem.id] = forceUpdatePrefs;
						Object.extend(menuItem, newMenuItem);
						if (menuItem.serviceURL) 
							self.addEditDiv.style.display = "none";
						else 
							self.addEditDiv.style.display = "block";
					},
					onFailure: function(t){
						var resTxt = ISA_SiteAggregationMenu.getErrorMessage(t);
						alert(ISA_R.ams_failedUpdatingMenu + resTxt);
						msg.error(ISA_R.ams_failedUpdatingMenu + t.status + " - " + t.statusText + " " + resTxt);
					},
					onException: function(r, t){
						alert(ISA_R.ams_failedUpdatingMenu);
						msg.error(ISA_R.ams_failedUpdatingMenu + getErrorMessage(t));
					},
					onComplete: function(){
						Control.Modal.close();
					},
					contentType: 'text/plain'
				};
				AjaxRequest.invoke(url, opt);
			}, Object.extend({
				omitTypeList: typeList,
				displayWidgetFieldSet: (menuItem.depth > 0),
				disabledTypeEdit: true,
				displayAlertFieldSet: true,
				displayForceUpdatePropertyForm: true,
				disabledAlertFieldSet: !(menuItem.serviceURL && menuItem.depth == 0)
			}, menuItem.depth == 0 ? {
				menuFieldSetLegend: ISA_R.alb_settingMenuLink,
				displayMenuTreeAdminsFieldSet: true
			} : {}));
			IS_Event.observe(editDiv, 'click', function(){
				editorFormObj.showEditorForm(menuItem);
			}, false, "_adminMenu");
			
			// Only super-user can remove site-top
			if ((ISA_SiteAggregationMenu.isTreeAdminUser && menuItem.parentId) || !ISA_SiteAggregationMenu.isTreeAdminUser) {
				editDiv = ISA_Admin.createIconButton(ISA_R.alb_delete, ISA_R.alb_deleteMenu, "delete.gif");
				editDiv.id = "del_" + menuItem.id;
				editDiv.style.margin = "0";
				editTitleTd.appendChild(editDiv);
				
				var deleteEditorFormObj = new ISA_CommonModals.EditorForm(editDiv, function(menuItem){
					
					var url = adminHostPrefix + "/services/menu/"
						+ ((menuItem.parentId)? "removeMenuItem" : "removeTopMenuItem");
					var opt = {
						method: 'post',
						contentType: "application/json",
						postBody: Object.toJSON([menuObj.menuType, menuItem.id, ISA_SiteAggregationMenu.getSitetopId(menuItem)]),
						asynchronous: true,
						onSuccess: function(response){
							binder.isUpdated = true;
							if(menuItem.forceDelete)
							  ISA_SiteAggregationMenu.forceDeleteList = ISA_SiteAggregationMenu.forceDeleteList.concat(eval(response.responseText));
							ISA_SiteAggregationMenu.removeMenuTree(menuItem);
						},
						onFailure: function(t){
							var resTxt = ISA_SiteAggregationMenu.getErrorMessage(t);
							alert(ISA_R.ams_failedDeleteMenu + resTxt);
							msg.error(ISA_R.ams_failedDeleteMenu + t.status + " - " + t.statusText + " " + resTxt);
						},
						onException: function(r, t){
							alert(ISA_R.ams_failedDeleteMenu);
							msg.error(ISA_R.ams_failedDeleteMenu + getErrorMessage(t));
						},
						onComplete: function(){
							Control.Modal.close();
						}
					};
					AjaxRequest.invoke(url, opt);
				}, Object.extend({
					formDisabled: true,
					displayAlertFieldSet: true
				}, menuItem.depth == 0 ? {
					menuFieldSetLegend: ISA_R.alb_settingMenuLink,
					displayMenuTreeAdminsFieldSet: true
				} : {}));
				IS_Event.observe(editDiv, 'click', function(){
					menuItem.isDelete = true;
					deleteEditorFormObj.showEditorForm(menuItem);
				}, false, "_adminMenu");
			}
			
			editDiv = ISA_Admin.createIconButton(ISA_R.alb_add, ISA_R.alb_addMenu, "add.gif");
			editDiv.id = "ins_" + menuItem.id;
			this.addEditDiv = editDiv;
			editDiv.style.margin = "0";
			if (menuItem.serviceURL) 
				editDiv.style.display = "none";
			editTitleTd.appendChild(editDiv);
			
			var parentMenuItem = menuItem;
			var addEditorFormObj = new ISA_CommonModals.EditorForm(editDiv, function(menuItem){
				var url = adminHostPrefix + "/services/menu/addMenuItem";
				var newMenuItem = ISA_SiteAggregationMenu.getUpdMenuItem(menuItem, menuObj.menuType);
				newMenuItem.add = true;
				
				var allertSetting = $F('allertSetting');
				var opt = {
					method: 'post',
					contentType: "application/json",
					postBody: Object.toJSON([
						String(newMenuItem.id), 
						String(newMenuItem.parentId), 
						newMenuItem.title, 
						newMenuItem.href || "", 
						newMenuItem.display || "", 
						newMenuItem.type || "", 
						newMenuItem.properties ||{}, 
						allertSetting, 
						newMenuItem.menuType, 
						newMenuItem.auths || null, 
						newMenuItem.linkDisabled, 
						newMenuItem.directoryTitle || "", 
						ISA_SiteAggregationMenu.getSitetopId(parentMenuItem),
						newMenuItem.multi || false
					]),
					asynchronous: true,
					onSuccess: function(response){
						binder.isUpdated = true;
						ISA_SiteAggregationMenu.addMenuTree(menuObj, newMenuItem, parentMenuItem);
					},
					onFailure: function(t){
						var resTxt = ISA_SiteAggregationMenu.getErrorMessage(t);
						alert(ISA_R.ams_failedAddMenu + resTxt);
						msg.error(ISA_R.ams_failedAddMenu + t.status + " - " + t.statusText + " " + resTxt);
					},
					onException: function(r, t){
						alert(ISA_R.ams_failedAddMenu);
						msg.error(ISA_R.ams_failedAddMenu + getErrorMessage(t));
					},
					onComplete: function(){
						Control.Modal.close();
					},
					contentType: 'text/plain'
				};
				AjaxRequest.invoke(url, opt);
			}, {
				omitTypeList: typeList,
				setDefaultValue: false,
				generateId: true,
				disableTypeEdit: true,
				displayAlertFieldSet: true
			});
			IS_Event.observe(editDiv, 'click', function(){
				addEditorFormObj.showEditorForm({
					title: ISA_R.alb_newMenu,
					parentId: menuItem.id,
					properties: {}
				});
			}, false, "_adminMenu");
		}else{
			if (!menuItem.parentId) {
				editDiv = ISA_Admin.createIconButton(ISA_R.alb_lockAndEdit, ISA_R.alb_lockAndEdit, "edit.gif");
				editDiv.id = "lockedit_" + menuItem.id;
				editDiv.style.margin = "0";
				editTitleTd.appendChild(editDiv);
				
				IS_Event.observe(editDiv, 'click', changeEditMode.bind(this, menuObj, menuItem, false), false, "_adminMenu");
			}
			
			editDiv = ISA_Admin.createIconButton(IS_R.lb_ref, IS_R.lb_ref, "ref.gif");
			editDiv.id = "ref_" + menuItem.id;
			editDiv.style.margin = "0";
			editTitleTd.appendChild(editDiv);
			
			var refEditorFormObj = new ISA_CommonModals.EditorForm(editDiv, function(menuItem){}, Object.extend({
				formDisabled: true,
				displayAlertFieldSet: true,
				displayOK: false
			}, menuItem.depth == 0 ? {
				menuFieldSetLegend: ISA_R.alb_settingMenuLink,
				displayMenuTreeAdminsFieldSet: true
			} : {}));
			IS_Event.observe(editDiv, 'click', function(){
				refEditorFormObj.showEditorForm(menuItem);
			}, false, "_adminMenu");
		}
		var titleEditBox = this.titleEditBox;

		this.titleEditBox.appendChild(editTitleTable);

		if(Browser.isIE)
			this.titleEditBox.style.width = editTitleTable.offsetWidth;

		$("admin-menu-navigator").appendChild(this.titleEditBox);
	};
	
	function changeEditMode(menuObj, topMenuItem, isForce){
		// Change the tree to edit mode
		
		var oldSiteTop = $(topMenuItem.id);
		var loadingDiv = document.createElement("div");
		loadingDiv.style.clear = "both";
		loadingDiv.innerHTML = "Loading...";
		
		oldSiteTop.parentNode.replaceChild(loadingDiv, oldSiteTop);
		
		// Request
		var url = adminHostPrefix + "/services/menu/menuLock";
		var opt = {
			method: 'post',
			contentType: "application/json",
			postBody: Object.toJSON([menuObj.menuType, topMenuItem.id, (isForce)? true : false, menuObj.editSitetopIdList]),
			asynchronous: true,
			onSuccess: function(oldSiteTop, loadingDiv, menuObj, response){
				var resultJson = eval("(" + response.responseText + ")");
				
				if(resultJson.conflict){
					// If they conflict
					loadingDiv.parentNode.replaceChild(oldSiteTop, loadingDiv);
					if(ISA_SiteAggregationMenu.isTreeAdminUser){
						alert(ISA_R.alb_multiUserEdit + "\n\n" + resultJson.conflictUser);
					}
					else if(confirm(ISA_R.alb_multiUserEdit+"\n\n" + resultJson.conflictUser + "\n\n"+ISA_R.alb_multiUserEdit2)){
						changeEditMode(menuObj, topMenuItem, true);
						return;
					}
				}else{
					Element.show("adminmenu_commit");
					Element.show("adminmenu_lockRelease");
					Element.hide("adminmenu_refresh");
					
					// Merge siteJson
					ISA_SiteAggregationMenu.mergeMenu(resultJson.menuItems, resultJson.mapJson);
					
					menuObj.editSitetopIdList.push(oldSiteTop.id);
					
					// Rebuild as menu tree of edit mode
					menuObj.rebuildMenuTree(oldSiteTop.id, loadingDiv);
				}
			}.bind(this, oldSiteTop, loadingDiv, menuObj),
			onFailure: function(oldSiteTop, loadingDiv, t){
				var resTxt = ISA_SiteAggregationMenu.getErrorMessage(t);
				alert(ISA_R.ams_failedLockMenu + resTxt);
				msg.error(ISA_R.ams_failedLockMenu + t.status + " - " + t.statusText + " " + resTxt);
				
				loadingDiv.parentNode.replaceChild(oldSiteTop, loadingDiv);
			}.bind(this, oldSiteTop, loadingDiv),
			onException: function(oldSiteTop, loadingDiv, r, t){
				alert(ISA_R.ams_failedLockMenu);
				msg.error(ISA_R.ams_failedLockMenu + getErrorMessage(t));
				loadingDiv.parentNode.replaceChild(oldSiteTop, loadingDiv);
			}.bind(this, oldSiteTop, loadingDiv)
		};
		AjaxRequest.invoke(url, opt);
	}
	
	this.showTitleEditor = function (e){
		Event.stop(e);
		if(!isBuilt)
		  this.buildNavigator();
		if(ISA_SiteAggregationMenu.Navigator.displayNavi) {
			var currentNavi = ISA_SiteAggregationMenu.Navigator.displayNavi;
			currentNavi.style.display = "none";
		}
		self.titleEditBox.style.top = Event.pointerY(e);
		self.titleEditBox.style.left = Event.pointerX(e);
		self.titleEditBox.style.display = "block";
		ISA_SiteAggregationMenu.Navigator.displayNavi = self.titleEditBox;
		return false;
	};
	
	this.hideTitleEditor = function (e){
		if(self.titleEditBox)
		  self.titleEditBox.style.display = "none";
		ISA_SiteAggregationMenu.Navigator.displayNavi = null;
	};
};
