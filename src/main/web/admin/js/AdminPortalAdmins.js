var ISA_PortalAdmins = IS_Class.create();

ISA_PortalAdmins.portalAdmins = false;

ISA_PortalAdmins.portalRolesList = [];
ISA_PortalAdmins.myRoleId;

// Permission(Supporsed to be in DB)
ISA_PortalAdmins.portalPermissionTypeInfo = $H({
	menu: ISA_R.alb_menu,
	menu_tree: ISA_R.alb_menuTree,
	search: ISA_R.alb_searchForm,
	widget: ISA_R.alb_widget,
	defaultPanel: ISA_R.alb_defaultPanel,
	portalLayout: ISA_R.alb_otherLayout,
	i18n: ISA_R.alb_i18n,
	properties: ISA_R.alb_properties,
	proxy: ISA_R.alb_proxy,
	admins: ISA_R.alb_admin,
	forbiddenURL: ISA_R.alb_forbiddenURL,
	authentication: "OAuth"
});

ISA_PortalAdmins.prototype.classDef = function() {
	var self = this;
	var container;
	var loadingMessage;
	var controlModal;
	
	this.initialize = function() {
		container = document.getElementById("portalAdmin");
		
		/**
		 * Remove trash if it remains
		 */
		while (container.hasChildNodes())
			container.removeChild(container.firstChild);
		
		loadingMessage = document.createElement('div');
		loadingMessage.innerHTML = "Loading...";
		loadingMessage.style.clear = "both";
		loadingMessage.style.cssFloat = "left";
		container.appendChild(loadingMessage);
	};
	
	this.displayPortalAdmins = function(portalAdminsList) {
		var portalAdminsDiv = document.createElement("div");
		portalAdminsDiv.style.clear = "both";
		
		var refreshAllDiv = document.createElement("div");
		refreshAllDiv.className = "refreshAll";
//		refreshAllDiv.style.textAlign = "right";
//		refreshAllDiv.style.width = "100%";
		
		var commitDiv = ISA_Admin.createIconButton(ISA_R.alb_changeApply, ISA_R.alb_changeApply, "database_save.gif", "right");
		refreshAllDiv.appendChild(commitDiv);
		controlModal = new Control.Modal(
			false,
			{
				contents: ISA_R.ams_applyingChanges,
				opacity: 0.2,
				containerClassName:"commitDialog",
				overlayCloseOnClick:false
			}
		);
		IS_Event.observe(commitDiv, 'click', self.commitPortalAdmins.bind(this), "_adminAdmins");
		
		var refreshDiv = ISA_Admin.createIconButton(ISA_R.alb_refresh, ISA_R.alb_reloadWithourSaving, "refresh.gif", "right");
		refreshAllDiv.appendChild(refreshDiv);
		IS_Event.observe( refreshDiv , 'click', function() {
			ISA_Admin.AdminTabs.setActiveTab("portalAdmin")
		}, false, "_adminAdmins");
		
		/*
		var titleDiv = document.createElement("div");
		titleDiv.id = "portalAdminsTitle";
		titleDiv.className = "portalAdminsTitle";
		titleDiv.appendChild(document.createTextNode(ISA_R.alb_adminList));
		*/
		
		portalAdminsDiv.appendChild(refreshAllDiv);
//		portalAdminsDiv.appendChild(titleDiv);
		
		var tabContainer = document.createElement("div");
		tabContainer.id = "tabContainer";
		//tabContainer.className= "side-bar";
		var sideBar = document.getElementById("administrator-side-bar");
		var tabsUl = document.createElement("ul");
		tabsUl.className = "side-bar";
		var tabLiAdmins = document.createElement("li");
		var tabLiAdminsA = document.createElement("a");
		var tabLiRoles = document.createElement("li");
		var tabLiRolesA = document.createElement("a");
		
		tabsUl.id = "adminsTab";
		//tabsUl.className = "subsection_tabs tabs";
		tabsUl.className = "subsection_tabs";
		
		tabLiAdmins.className="tab";
		tabLiAdmins.id = "tab_admins";
		
		tabLiAdminsA.href = "#admins";
//		tabLiAdminsA.className = "tab";
//		tabLiAdminsA.id = "tab_admins";
		tabLiAdminsASpan = document.createElement("span");
		tabLiAdminsASpan.className = "title";
		tabLiAdminsASpan.appendChild(document.createTextNode(ISA_R.alb_adminSettings));
		tabLiAdminsA.appendChild( tabLiAdminsASpan );
		
		tabLiRoles.className= "tab";
		tabLiRoles.id = "tab_roles";
		
		tabLiRolesA.href = "#roles";
//		tabLiRolesA.className = "tab";
//		tabLiRolesA.className = "side-bar";
//		tabLiRolesA.id = "tab_roles";
		tabLiRolesASpan = document.createElement("span");
		tabLiRolesASpan.className = "title";
		tabLiRolesASpan.appendChild(document.createTextNode(ISA_R.alb_roleSettings));
		tabLiRolesA.appendChild( tabLiRolesASpan );
		
		tabsUl.appendChild(tabLiAdmins);
		tabsUl.appendChild(tabLiRoles);
		tabLiAdmins.appendChild(tabLiAdminsA);
		tabLiRoles.appendChild(tabLiRolesA);
		
		var adminsPanel = document.createElement("div");
		var rolesPanel = document.createElement("div");
		
		adminsPanel.id = "admins";
		rolesPanel.id = "roles";
		adminsPanel.appendChild(self.buildPortalAdmins(portalAdminsList));
		rolesPanel.appendChild(self.buildAdminRole());
		
		sideBar.appendChild(tabsUl);
		//tabContainer.appendChild(tabsUl);
		tabContainer.appendChild(adminsPanel);
		tabContainer.appendChild(rolesPanel);
		portalAdminsDiv.appendChild(tabContainer);
		container.replaceChild(portalAdminsDiv,loadingMessage);
		
		this.controlTabs = new Control.Tabs("adminsTab", {
			beforeChange: function(old_container, new_container){
				if(old_container.id == "roles"){
					this.reBuildAdminComboBoxList(new_container);
				}
				
				Element.removeClassName("tab_"+old_container.id,"selected");
				Element.addClassName("tab_"+new_container.id,"selected");
			}.bind(this)
		});
	}
	
	this.buildAdminRole = function(){
//		var rolesField = document.createElement("fieldSet");
//		var rolesLabel = document.createElement("legend");
//		rolesLabel.innerHTML = ISA_R.alb_roleSettings;
//		rolesField.appendChild(rolesLabel);
		
		var rolesField = document.createElement("div");
		rolesField.className= "configSet";
		var rolesLabel = document.createElement("p");
		rolesLabel.className = "configSetHeader";
		rolesLabel.innerHTML = ISA_R.alb_roleSettings;
		rolesField.appendChild(rolesLabel);
		
		var rolesDiv = document.createElement("div");
		
		// [Add] button
		var addroleDiv = ISA_Admin.createIconButton(ISA_R.alb_add, ISA_R.alb_add, "add.gif", "left");
		var addAClick = function(e){
			ISA_Admin.isUpdated = true;
			var tbodyElement = $("rolesTbody");
			if(tbodyElement) {
				var newRole = {
					id: (is_userId + new Date().getTime()),
					name: ISA_R.alb_newRole,
					permission:"[]",
					isAllowDelete:true
				};
				tbodyElement.appendChild(self.buildRolesList(newRole, true));
			}
		}
		rolesDiv.appendChild(addroleDiv);
		IS_Event.observe(addroleDiv, 'click', addAClick, false, "_adminAdmins");
		
		
		var rolesTable = document.createElement("table");
		rolesTable.border = "1";
		rolesTable.cellSpacing = "1";
		rolesTable.cellPadding = "1";
		rolesTable.className = "portalAdminsGroup";
		rolesTable.style.width = "900px";

		var rolesTbody = document.createElement("tbody");
		rolesTbody.id = "rolesTbody";
		rolesTable.appendChild(rolesTbody);

		var roleHeaderTr = document.createElement("tr");
		rolesTbody.appendChild(roleHeaderTr);

		var roleNameTd;
		roleNameTd = document.createElement("td");
		roleNameTd.className = "headerPortalAdmins";
		roleNameTd.style.whiteSpace = "nowrap";
		roleNameTd.style.width = "30%";
		roleNameTd.appendChild(document.createTextNode(ISA_R.alb_roleName2));
		roleHeaderTr.appendChild(roleNameTd);

		var permissionsTd;
		permissionsTd = document.createElement("td");
		permissionsTd.className = "headerPortalAdmins";
		permissionsTd.style.whiteSpace = "nowrap";
		permissionsTd.style.width = "50%";
		permissionsTd.appendChild(document.createTextNode(ISA_R.alb_permission));
		roleHeaderTr.appendChild(permissionsTd);

		var roleEditTd;
		roleEditTd = document.createElement("td");
		roleEditTd.className = "headerPortalAdmins";
		roleEditTd.style.whiteSpace = "nowrap";
		roleEditTd.style.width = "10%";
		roleEditTd.appendChild(document.createTextNode(ISA_R.alb_permission + IS_R.lb_edit));
		roleHeaderTr.appendChild(roleEditTd);
		
		var roleDeleteTd;
		roleDeleteTd = document.createElement("td");
		roleDeleteTd.rowspan = 2;
		roleDeleteTd.className = "headerPortalAdmins";
		roleDeleteTd.style.whiteSpace = "nowrap";
		roleDeleteTd.style.width = "10%";
		roleDeleteTd.appendChild(document.createTextNode(ISA_R.alb_delete));
		roleHeaderTr.appendChild(roleDeleteTd);
		
		// PortalAdminsList build
		var portalRolesList = ISA_PortalAdmins.portalRolesList;
		for(var i in portalRolesList){
			if( !(portalRolesList[i] instanceof Function) )
				rolesTbody.appendChild(self.buildRolesList(portalRolesList[i]));
		}
		
		rolesDiv.appendChild(rolesTable);
		rolesField.appendChild(rolesDiv);
		return rolesField;
	}
	
	this.buildRolesList = function(role, isNew){
		var tr = document.createElement("tr");
		
		var td;
		td = document.createElement("td");
		td.className = "adminRoles";
		$(td).setStyle({whiteSpace:"nowrap",padding:"5px"});
		var roleTitleInput = document.createElement("input");
		roleTitleInput.className = "portalAdminInput";
		roleTitleInput.type = "text";
		roleTitleInput.style.width = "100%";
		roleTitleInput.id = ISA_Admin.replaceUndefinedValue(role.id);
		roleTitleInput.value = ISA_Admin.replaceUndefinedValue(role.name);
		IS_Event.observe(roleTitleInput, 'change', function(){ISA_Admin.isUpdated = true}, false, "_adminAdmins");
		
		td.appendChild(roleTitleInput);
		tr.appendChild(td);
		
		var permissionTd;
		permissionTd = document.createElement("td");
		$(permissionTd).setStyle({fontSize:"12px"});
		
		// Display specified permission
		var myPermissionList = [];
		try{
			myPermissionList = eval(role.permission);
		}catch(e){
			msg.error(e)
		}
		
		var myPermissionNameList = getMyPermissionNameList(myPermissionList);
		var permissionNameDiv = document.createElement("div");
		permissionNameDiv.appendChild(document.createTextNode(myPermissionNameList.join(", ")));
		permissionTd.appendChild(permissionNameDiv);
		tr.appendChild(permissionTd);

		var editTd;
		editTd = document.createElement("td");
		$(editTd).setStyle({textAlign:"center"});
		tr.appendChild(editTd);
		
		var deleteTd;
		deleteTd = document.createElement("td");
		$(deleteTd).setStyle({textAlign :"center",whiteSpace:"nowrap"});
		tr.appendChild(deleteTd);
		
		if (role.isAllowDelete && ISA_PortalAdmins.myRoleId != role.id) {
			var editImg = document.createElement("img");
			editImg.src = imageURL + "edit.gif";
			editImg.style.cursor = "pointer";
			editImg.title = ISA_R.alb_editing;
			editTd.appendChild(editImg);
			
			new Control.Modal(editImg, {
				contents: this.buildRoleConfigPane.bind(role),
				opacity: 0.5,
				width: 480,
				afterClose : function(permissionNameDiv){
					permissionNameDiv.innerHTML = "";
					var newPermission = eval(role.permission);
					var newPermissionNameList = getMyPermissionNameList(newPermission);
					permissionNameDiv.appendChild(document.createTextNode(newPermissionNameList.join(", ")));
				}.bind(this, permissionNameDiv)
			});
			
			
			var deleteImg = document.createElement("img");
			deleteImg.src = imageURL + "trash.gif";
			deleteImg.style.cursor = "pointer";
			deleteImg.title = ISA_R.alb_deleting;
			var deleteImgClick = function(e){
				tr.parentNode.removeChild(tr);
			}
			IS_Event.observe(deleteImg, 'click', function(tr){
				ISA_Admin.isUpdated = true;
				Element.remove(tr);
				ISA_PortalAdmins.portalRolesList.remove(this);
			}.bind(role, tr), false, "_adminAdmins");
			deleteTd.appendChild(deleteImg);
		}
		
		if (isNew) {
			ISA_PortalAdmins.portalRolesList.push(role);
		}
		
		return tr;
	}
	
	function getMyPermissionNameList(myPermissionList){
		var myPermissionNameList = [];
		ISA_PortalAdmins.portalPermissionTypeInfo.each(function(permissionInfo){
			if(myPermissionList.contains(permissionInfo.key)){
				myPermissionNameList.push(permissionInfo.value);
			}
		});
		return myPermissionNameList;
	}
	
	this.buildRoleConfigPane = function(){
		var myPermissionList = [];
		try{
			myPermissionList = eval(this.permission);
		}catch(e){
			msg.error(e)
		}
		
		var container = document.createElement("div");
		var roleEditField = document.createElement("div");
		roleEditField.className = "modalConfigSet";
		var roleEditLabel = document.createElement("p");
		roleEditLabel.className = "modalConfigSetHeader";
		roleEditLabel.innerHTML = ISA_R.alb_editRole;
		roleEditField.appendChild(roleEditLabel);
		container.appendChild(roleEditField);
		
		var checkButtonsDiv = roleEditField.appendChild($.DIV());
		
		var authDivListDiv = document.createElement("div");
		var checkboxList = [];
		var count = 0;
		ISA_PortalAdmins.portalPermissionTypeInfo.each(function(permissionInfo){
			var authDiv = createCheckBoxSet(myPermissionList.contains(permissionInfo.key), permissionInfo.value, permissionInfo.key, count);
			checkboxList.push(authDiv.firstChild);
			authDivListDiv.appendChild(authDiv);
			count++;
		});
		roleEditField.appendChild(authDivListDiv);
		
		var buttonsDiv = document.createElement("div");
		$(buttonsDiv).setStyle({clear:"both",width:"100%",textAlign:"center"});
		
		var checkAll = checkButtonsDiv.appendChild(createButton(ISA_R.alb_checkAllItems));
		IS_Event.observe(checkAll, 'click', function(checkboxList){
			checkboxList.each(function(check){check.checked = true;});
		}.bind(this, checkboxList), false, "_adminAdmins");
		
		var uncheckAll = checkButtonsDiv.appendChild(createButton(ISA_R.alb_uncheckAllItems));
		IS_Event.observe(uncheckAll, 'click', function(checkboxList){
			checkboxList.each(function(check){check.checked = false;});
		}.bind(this, checkboxList), false, "_adminAdmins");
		
		var ok = buttonsDiv.appendChild(createButton(ISA_R.alb_save));
		IS_Event.observe(ok, 'click', function(checkboxList){
			var newPermission = [];
			checkboxList.each(function(check){
				if(check.checked) newPermission.push(check.value);
			});
			this.permission = Object.toJSON(newPermission);
			ISA_Admin.isUpdated = true;
			
			Control.Modal.close();
		}.bind(this, checkboxList), false, "_adminAdmins");
		
		var cancel = buttonsDiv.appendChild(createButton(ISA_R.alb_cancel));
		IS_Event.observe(cancel, 'click', function(checkboxList){
			Control.Modal.close();
		}.bind(this), false, "_adminAdmins");
		
		container.appendChild(buttonsDiv);
		
		function createButton(label, event){
			var button = document.createElement("input");
			button.type = "button"
			button.value = label;
			
			return button;
		}
		
		function createCheckBoxSet(checked, label, value, count){
			var authDiv = document.createElement("div");
			if(count == ISA_PortalAdmins.portalPermissionTypeInfo.values().length-1){
				$(authDiv).setStyle({width:'100px', 'clear' :'both',whiteSpace:'nowrap'});
			}else{
				$(authDiv).setStyle({width:'100px', 'float' :'left',whiteSpace:'nowrap'});
			}
			
			var authName = document.createElement("label");
			var authCheck = document.createElement("input");
			authCheck.type = "checkbox";
			authCheck.defaultChecked = checked;
			authCheck.value = value;
			
			authName.appendChild(document.createTextNode(label));
			
			authDiv.appendChild(authCheck);
			authDiv.appendChild(authName);
			return authDiv;
		}
		
		return container;
	}
	
	this.buildPortalAdmins = function(portalAdminsList) {
//		var portalAdminsField = document.createElement("fieldSet");
//		var portalAdminsLabel = document.createElement("legend");
//		portalAdminsLabel.innerHTML = ISA_R.alb_adminSettings;
//		portalAdminsField.appendChild(portalAdminsLabel);
		
		var portalAdminsField = document.createElement("div");
		portalAdminsField.className ="configSet";
		var portalAdminsLabel = document.createElement("p");
		portalAdminsLabel.className = "configSetHeader";
		portalAdminsLabel.innerHTML = ISA_R.alb_adminSettings;
		portalAdminsField.appendChild(portalAdminsLabel);
		
		var portalAdminsDiv = document.createElement("div");
		
		// [Add] button
		var addPortalAdminDiv = ISA_Admin.createIconButton(ISA_R.alb_add, ISA_R.alb_add, "add.gif", "left");
		var addAClick = function(e){
			ISA_Admin.isUpdated = true;
			var tbodyElement = $("portalAdminsTbody");
			if(tbodyElement) {
				tbodyElement.appendChild(self.buildPortalAdminsList({uid:"", roleId:ISA_PortalAdmins.portalRolesList[0].id}));
			}
		}
		portalAdminsDiv.appendChild(addPortalAdminDiv);
		IS_Event.observe(addPortalAdminDiv, 'click', addAClick, false, "_adminAdmins");
		
		portalAdminsDiv.id = "portalAdmins";
		
		var portalAdminsTable = document.createElement("table");
		portalAdminsTable.border = "1";
		portalAdminsTable.cellSpacing = "1";
		portalAdminsTable.cellPadding = "1";
		portalAdminsTable.className = "portalAdminsGroup";
		portalAdminsTable.style.width = "900px";

		var portalAdminsTbody = document.createElement("tbody");
		portalAdminsTbody.id = "portalAdminsTbody";
		portalAdminsTable.appendChild(portalAdminsTbody);

		var portalAdminsTr = document.createElement("tr");
		portalAdminsTbody.appendChild(portalAdminsTr);

		var portalAdminsTd;
		portalAdminsTd = document.createElement("td");
		portalAdminsTd.className = "headerPortalAdmins";
		portalAdminsTd.style.whiteSpace = "nowrap";
		portalAdminsTd.style.width = "40%";
		portalAdminsTd.appendChild(document.createTextNode(ISA_R.alb_admin));
		portalAdminsTr.appendChild(portalAdminsTd);

		var adminRoleTd;
		adminRoleTd = document.createElement("td");
		adminRoleTd.className = "headerPortalAdmins";
		adminRoleTd.style.whiteSpace = "nowrap";
		adminRoleTd.style.width = "40%";
		adminRoleTd.appendChild(document.createTextNode(ISA_R.alb_role));
		portalAdminsTr.appendChild(adminRoleTd);
		
		portalAdminsTd = document.createElement("td");
		portalAdminsTd.rowspan = 2;
		portalAdminsTd.className = "headerPortalAdmins";
		portalAdminsTd.style.whiteSpace = "nowrap";
		portalAdminsTd.style.width = "20%";
		portalAdminsTd.appendChild(document.createTextNode(ISA_R.alb_delete));
		portalAdminsTr.appendChild(portalAdminsTd);

		portalAdminsDiv.appendChild(portalAdminsTable);

		// PortalAdminsList build
		for(var i in portalAdminsList){
			if( !(portalAdminsList[i] instanceof Function) )
				portalAdminsTbody.appendChild(self.buildPortalAdminsList(portalAdminsList[i]));
		}
		this.portalAdminsTbody = portalAdminsTbody;
		
		portalAdminsField.appendChild(portalAdminsDiv);
		return portalAdminsField;
	}
	
	this.buildPortalAdminsList = function(portalAdminObj) {
		var portalAdminUid = (portalAdminObj)? portalAdminObj.uid : "";
		var roleId = (portalAdminObj)?portalAdminObj.roleId : "";
		
		var tr = document.createElement("tr");
		
		var td;
		td = document.createElement("td");
		td.className = "portalAdmins";
		$(td).setStyle({whiteSpace:"nowrap",padding:"5px"});
		var portalAdminValueInput = document.createElement("input");
		portalAdminValueInput.className = "portalAdminInput";
		portalAdminValueInput.type = "text";
		portalAdminValueInput.style.width = "100%";
		portalAdminValueInput.value = ISA_Admin.replaceUndefinedValue(portalAdminUid);
		td.appendChild(portalAdminValueInput);
		tr.appendChild(td);
		
		td = document.createElement("td");
		td.className = "roleSelectTd";
		
		this.buildAdminComboBox(td, roleId, (is_userId == portalAdminUid));
		tr.appendChild(td);
		
		td = document.createElement("td");
		td.style.textAlign = "center";
		td.style.whiteSpace = "nowrap";
		tr.appendChild(td);
		
		if(is_userId != portalAdminUid){
			var deleteImg = document.createElement("img");
			deleteImg.src = imageURL + "trash.gif";
			deleteImg.style.cursor = "pointer";
			deleteImg.title = ISA_R.alb_deleting;
			var deleteImgClick = function(e){
				ISA_Admin.isUpdated = true;
				tr.parentNode.removeChild(tr);
			}
			IS_Event.observe(deleteImg, 'click', deleteImgClick.bind(deleteImg), false, "_adminAdmins");
			td.appendChild(deleteImg);
		}else{
			td.innerHTML  = " ";
			portalAdminValueInput.disabled = true;
			ISA_PortalAdmins.myRoleId = roleId;
		}
		IS_Event.observe(portalAdminValueInput, 'change',function(){ISA_Admin.isUpdated = true}, false, "_adminAdmins");
		tr.appendChild(td);
		
		return tr;
	}
	
	this.reBuildAdminComboBoxList = function(adminContainer){
		// Obtain title information
		inputs = $$(".adminRoles .portalAdminInput");
		inputs.each(function(input){
			var name = input.value;
			this.getRole(input.id).name = name;
		}.bind(this));

		$$(".roleSelectTd").each(function(selectTd){
			var comboBox = selectTd.firstChild;
			var selectedValue = comboBox.value;
			
			Element.remove(comboBox);
			this.buildAdminComboBox(selectTd, selectedValue, selectTd.disabled);
		}.bind(this));
	}
	
	this.buildAdminComboBox = function(renderTo, roleId, disabled){
		var roleSelect = document.createElement("select");
		roleSelect.style.width = "100%";
		roleSelect.disabled = disabled;
		
		var roleIdList = [];
		ISA_PortalAdmins.portalRolesList.each(function(role){
			var roleOption = document.createElement("option");
			roleOption.value = role.id;
			roleOption.appendChild(document.createTextNode(role.name));
			this.appendChild(roleOption);
			roleIdList.push(role.id);
		}.bind(roleSelect));
		
		// If the role is deleted
		if(!roleIdList.contains(roleId)){
			var deletedOpt = document.createElement("option");
			deletedOpt.appendChild(document.createTextNode(ISA_R.ams_roleDeleted));
			deletedOpt.value = "";
			if (roleSelect.firstChild) {
				roleSelect.insertBefore(deletedOpt, roleSelect.firstChild);
			}else{
				roleSelect.appendChild(deletedOpt);
			}
			roleSelect.value = "";
			deletedOpt.style.color = "red";
		}else{
			roleSelect.value = roleId;
		}
		IS_Event.observe(roleSelect, 'change',function(){ISA_Admin.isUpdated = true}, false, "_adminAdmins");
		renderTo.appendChild(roleSelect);
	}
		
	this.commitPortalAdmins = function() {
		ISA_Admin.isUpdated = false
		var sendData = {};
		sendData.admins = [];
		sendData.roles = ISA_PortalAdmins.portalRolesList;
		
		var valuesArray = [];
		var inputs = $$(".portalAdmins .portalAdminInput");
		for(var i=0;i<inputs.length;i++){
			// Obtain value
			var uid = ISA_Admin.trim(inputs[i].value);
			var roleId = ISA_Admin.trim(inputs[i].parentNode.nextSibling.firstChild.value);
			
			if(valuesArray.contains(uid)){
				alert(ISA_R.ams_duplicatedID);
				inputs[i].select();
				return;
			}
			
			valuesArray.push(uid);
			
			// Check with input of administrator setting 
			if(!uid || uid.length == 0) {
				alert(ISA_R.getResource(ISA_R.ams_typeAdminSettings, [i+1]));
				this.controlTabs.setActiveTab("admins");
				inputs[i].select();
				return;
			}else{
				var error = IS_Validator.validate(uid, {maxBytes:150, label:ISA_R.alb_admin});
				if(error){
					alert(error);
					this.controlTabs.setActiveTab("admins");
					inputs[i].select();
					return;
				}
			}
			
			sendData.admins.push( {
				uid: uid,
				roleId: roleId
			} );
		}
		
		// Check with input of role settings
		inputs = $$(".adminRoles .portalAdminInput");
		
		var roleIndex = 0;
		var isSuccess = true;
		inputs.each(function(input){
			var name = input.value;
			roleIndex++;
			if(name.length == 0) {
				alert(ISA_R.getResource(ISA_R.ams_typeRoleSetting, [roleIndex]));
				isSuccess = false;
				this.controlTabs.setActiveTab("roles");
				input.select();
			}else{
				var error = IS_Validator.validate(name, {maxBytes:256, label:ISA_R.alb_roleName2});
				if(error){
					alert(error);
					isSuccess = false;
					this.controlTabs.setActiveTab("roles");
					input.select();
				}
			}
			this.getRole(input.id).name = name;
		}.bind(this));
		if(!isSuccess) return;
		
		// Update to DB
		self.updatePortalAdmins(sendData);
	}
	
	this.updatePortalAdmins = function(sendData) {
		controlModal.open();
		var url = adminHostPrefix + "/services/portalAdmins/updatePortalAdmins";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			postBody: Object.toJSON([sendData]),
			asynchronous:true,
			onSuccess: function(response){
				controlModal.update(ISA_R.ams_changeUpdated);
			},
			onFailure: function(t) {
				alert(ISA_R.ams_failedUpdateAdmin+'\n' + t.responseText);
				msg.error(ISA_R.ams_failedUpdateAdmin + t.status + " - " + t.statusText + "-" + t.responseText);
				ISA_Admin.TabBuilders.portalAdmin.build();
			},
			onException: function(r, t){
				alert(ISA_R.ams_failedUpdateAdmin);
				msg.error(ISA_R.ams_failedUpdateAdmin + getErrorMessage(t));
			},
			onComplete: function(){
				setTimeout(function(){
					controlModal.close();
				},500);
			}
		};
		AjaxRequest.invoke(url, opt);
	}
	
	this.build = function() {
		var url = adminHostPrefix + "/services/portalAdmins/getPortalAdminsJson";
		var opt = {
			method: 'get' ,
			asynchronous:true,
			onSuccess: function(response){
				var adminsData = eval("(" + response.responseText + ")");
				
				ISA_PortalAdmins.portalRolesList = adminsData.roles;
				self.displayPortalAdmins(adminsData.admins);
			},
			on404: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_adminNotFound+"</span>";
				msg.error(ISA_R.ams_adminNotFound + t.status + " - " + t.statusText);
			},
			onFailure: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingAdmin+"</span>";
				msg.error(ISA_R.ams_failedLoadingAdmin + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedLoadingAdmin+"</span>";
				msg.error(ISA_R.ams_failedLoadingAdmin + getErrorMessage(t));
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

	this.getRole = function(roleId){
		return ISA_PortalAdmins.portalRolesList.detect(function(role){
			return role.id == roleId;
		});
	}
};
