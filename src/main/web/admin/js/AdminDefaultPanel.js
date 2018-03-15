var ISA_DefaultPanel = IS_Class.create();
IS_EventDispatcher.addListener("deleteTemp", "", function(all){
	if((all || ISA_DefaultPanel.defaultPanel) && window["displayTabId"]){
		// Deleting Temp data
		var url = adminHostPrefix + "/services/tabLayout/deleteTemp";
		var opt = {
			method: 'post',
			postBody: Object.toJSON([ displayTabId ]),
			contentType: "application/json",
			asynchronous:false,
			onSuccess: function(response){
				msg.info(ISA_R.ams_deleteEdittingMenuData);
			},
			onFailure: function(t) {
				var errormsg = t.responseText && typeof t.responseText == "string" ? t.responseText.substr(0, 100) : "";
				msg.info(ISA_R.ams_failedDeleteEdittingData + errormsg);
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
	var portalHeaderTabId = "header";
	var ignoreTabIdList = [commandBarTabId, portalHeaderTabId, "0"];
	
	this.defaultRoleRegex = "default";
	this.defaultRoleName = "defaultRole";
	this.defaultDefaultUid = "default";
	
	var defaultDefaultUid = this.defaultDefaultUid;
	var commandBarMap = {
		"portal-logo":{id:"portal-logo", type:"logo", title:ISA_R.alb_logo, togglable:false, undeletable:true, onlyoutside:true, hidden:true},
		"portal-searchform":{id:"portal-searchform", title:ISA_R.alb_searchForm, togglable:true, undeletable:true, onlyoutside:true},
		"Ticker":{id:"p_1_w_4", title:ISA_R.alb_Ticker, type:"Ticker", togglable:true, undeletable:true, onlyoutside:true},
		"Ranking":{id:"p_1_w_6", title:ISA_R.alb_ranking, type:"Ranking", togglable:true, undeletable:true},
		"portal-change-fontsize":{id:"portal-change-fontsize", title:ISA_R.alb_changeFont, togglable:true, undeletable:true},
		"portal-trash":{id:"portal-trash", title:ISA_R.alb_trashBox, togglable:true, undeletable:true},
		"portal-preference":{id:"portal-preference", title:ISA_R.alb_setupAll, togglable:true, undeletable:true},
		"portal-credential-list":{id:"portal-credential-list", title:ISA_R.alb_credentialList, togglable:true, undeletable:true},
		"portal-admin-link":{id:"portal-admin-link", title:ISA_R.alb_adminLink, togglable:true, undeletable:true},
		"portal-logout":{id:"portal-logout",title:ISA_R.alb_logout,togglable:true,undeletable:true},
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
				var newWindowRadio = ISA_Admin.createBaseRadio("panelAddCommandBarTarget", isNewwindow, false, document);
				newWindowRadio.id = "panelAddCommandBarNewwindow";
				windowTargetDiv.appendChild(newWindowRadio);
				windowTargetDiv.appendChild(document.createTextNode(ISA_R.alb_newWindow));

				var iFrameRadio = ISA_Admin.createBaseRadio("panelAddCommandBarTarget", isIframe, false, document);
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
		},
		logo :{
			buildForm : function(commandItem){
				var src = "http://";
				if(commandItem){
					var commandDiv = $(commandItem.id);
					src = $jq("img", commandDiv).attr("src");
				}
				
				var editAreaDiv = document.createElement("div");
				editAreaDiv.id = "panelAddCommandBarInputWrap";

				var dialogTable = new ISA_RightLeftAlignTable()
				var editUrlInput = document.createElement("input");
				editUrlInput.id = "panelAddCommandBarURL";
				editUrlInput.type = "text";
				editUrlInput.size = 70;
				editUrlInput.value = src;
				editUrlInput.maxLength = 1024;
				dialogTable.addRow(ISA_R.alb_ImageUrl, editUrlInput);

				editAreaDiv.appendChild(dialogTable.build());
				return editAreaDiv;
			},
			onOK : function(commandItem){
				var commandDiv = $(commandItem.id);
				$jq("img", commandDiv).attr("src", $("panelAddCommandBarURL").value);
				
				self.changeCommandBarLayout();
				return true;
			}
		},
		gadget :{
			buildForm : function(commandItem){
				var jsonRole = defaultPanelJson[self.displayRoleId];
				var widgetJSON = jsonRole.staticPanel[commandItem.id];
				this.commandBarEditorFormObj.showEditorForm(widgetJSON);
			}.bind(this),
			onOK : function(widgetJSON, oldId){
				var commandDivTitle = $jq("#name_" + oldId);
				
				if(commandDivTitle.length == 0){
					var commandDiv = document.createElement("div");
					commandDiv.id = widgetJSON.id;
					commandDiv.setAttribute("label", widgetJSON.title);
					commandDiv.setAttribute("type", "gadget");
					commandDiv.setAttribute("outside", "true");
					self.addCommandBar(commandDiv);
				}else{
					// unbind edit event 
					$jq("#commnad_" + oldId + " a.item-edit").unbind();
					
					// replace oldId to newId
					commandDivTitle.attr("id", "name_" + widgetJSON.id);
					commandDivTitle.text(widgetJSON.title);
					$jq("#commnad_" + oldId).attr("id", "commnad_" + widgetJSON.id)
					$jq("#disp_" + oldId).attr("id", "disp_" + widgetJSON.id)
					$jq("#td_" + oldId).attr("id", "td_" + widgetJSON.id)
					$jq("#" + oldId).attr("id", widgetJSON.id)
					
					// renew edit event
					var commandItem = {
						id: widgetJSON.id,
						onlyoutside: true,
						togglable: false,
						title: widgetJSON.title,
						type: "gadget"
					};
					$jq("#commnad_" + widgetJSON.id + " a.item-edit").click(function(){
						self.commandBarEditor[this.type].buildForm(this);
					}.bind(commandItem));
					
					$jq("#" + widgetJSON.id).attr("label", widgetJSON.title);
				}
				
				self.changeCommandBarLayout();
				return true;
			}
		}
	}

	this.commandBarEditorFormObj =	new ISA_CommonModals.EditorForm(null, function(widgetJSON){
		var selectType = ISA_CommonModals.EditorForm.getSelectType();
		if( widgetJSON.type != selectType )
		widgetJSON.properties = {};

		var oldId = widgetJSON.id
		widgetJSON.id = "w_c_" + new Date().getTime();
		widgetJSON.type = ISA_CommonModals.EditorForm.getSelectType();
		widgetJSON.properties = ISA_CommonModals.EditorForm.getProperty(widgetJSON);
		widgetJSON.ignoreHeader = true;
		widgetJSON.noBorder = true;

		widgetJSON.title = ISA_Admin.trim($("formTitle").value);
		widgetJSON.href =  $("formHref").value;
	    var formUseRefreshInterval = $jq("#formUseRefreshInterval").prop("checked");
	    widgetJSON.refreshInterval = (formUseRefreshInterval)? parseInt($jq("#formRefreshInterval").val()) : null;

	    var jsonRole = defaultPanelJson[self.displayRoleId];
		is_deleteProperty(jsonRole.staticPanel, oldId);
		jsonRole.staticPanel[widgetJSON.id] = widgetJSON;
		
	    this.commandBarEditor.gadget.onOK(widgetJSON, oldId);
	    
		if( Control.Modal.current ) {
			Control.Modal.close();
		} else {
			Control.Modal.container.hide();
		}
	}.bind(this),{
		menuFieldSetLegend:ISA_R.alb_widgetHeaderSettings,
		disableMiniBrowserHeight: true,
		showIgnoreHeaderForm:false,
		showNoBorderForm:false,
		displayACLFieldSet:false,
		disableDisplayRadio:true,
		widgetStyle:"commandbar-item",
		// gadet only
		omitTypeList:['Ranking','Ticker','MultiRssReader','MiniBrowser',
		              'FragmentMiniBrowser','Message','Information','Information2',
		              'Information2','WidgetRanking','Calendar','RssReader']
	});

	
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
		container = document.getElementById("defaultPanel");

		loadingMessage = document.createElement('div');
		loadingMessage.innerHTML = "Loading...";
		loadingMessage.style.clear = "both";
		loadingMessage.style.cssFloat = "left";
		container.appendChild(loadingMessage);
		
		if(window["displayTabId"])
			this.displayTabId = displayTabId;
		if(window["defaultPanelJson"])
			this.displayRoleJsons = defaultPanelJson;
	};

	this.displayDefaultPanel = function() {
		// Permission ID currently displayed
		this.displayRoleId = false;
		this.displayRoleOrder = false;
		// Flag for update
		this.isUpdated = false;

		$jq(loadingMessage).remove();
		
		var commitDiv = $("changeApply");
		IS_Event.observe(commitDiv, 'click', self.commitPanel.bind(this), false, "_adminPanel");
		
		var tabContent = $jq("<div>").attr("id", "tabContent");
		$jq(container).append(tabContent);
		this.buildTabContents();
	}

	this.commitPanel = function(){
		if(!self.updatePanel(true)) return;
		
		var tabDesc = $jq("#tabDesc").val();
		var error = IS_Validator.validate(tabDesc, {maxBytes:256, label:ISA_R.alb_tabDesc});
		if(error){
			alert(error);
			$jq("#tabDesc").select();
			return false;
		}

		if(!controlModal){
			controlModal = new Control.Modal('',{
				className:"commitDialog",
				closeOnClick:false
			});			
		}
		controlModal.container.update(ISA_R.ams_applyingChanges);
		controlModal.open();
		
		var disableDefault = $jq('#disableDefaultCheck').prop('checked')? true : false;
		
		var url = adminHostPrefix + "/services/tabLayout/commitDefaultPanel";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			postBody: Object.toJSON([ this.displayTabId, tabDesc? tabDesc : "", disableDefault]),
			asynchronous:true,
			onSuccess: function(response){
				controlModal.container.update(ISA_R.ams_changeUpdated);
				ISA_Admin.isUpdated = false;
				ISA_DefaultPanel.updateRaws = [];
				this.updateRawStyle();
			}.bind(this),
			onFailure: function(t) {
				var errormsg = t.responseText && typeof t.responseText == "string" ? t.responseText.substr(0, 100) : "";
				alert(ISA_R.ams_failedToSaveTop+'\n' + errormsg);
				msg.error( ISA_R.ams_failedToSaveTop+ errormsg);
			},
			onException: function(r, t){
				alert(ISA_R.ams_failedToSaveTop);
				msg.error(ISA_R.ams_failedToSaveTop + getErrorMessage(t));
				throw t;
			},
			onComplete: function(){
				setTimeout(function(){
					Control.Modal.close();
				},500);
			}
		};
		AjaxRequest.invoke(url, opt);
	};

	/**
		Create tab contents
	*/
	this.buildTabContents = function() {

		var defaultPanelDiv = document.createElement("div");
		defaultPanelDiv.style.clear = "both";

		var refreshAllDiv = document.createElement("div");
		refreshAllDiv.id = "refreshAll";
		refreshAllDiv.style.textAlign = "left";
		refreshAllDiv.style.width = "100%";

		var dummyDiv = document.createElement("div");
		dummyDiv.style.clear = "both";
		defaultPanelDiv.appendChild(dummyDiv);

		var backDiv = ISA_Admin.createIconButton(ISA_R.alb_backToList, ISA_R.alb_backToList, "back.gif", "right");
		backDiv.id = "tab_"+this.displayTabId+"_backListPanel";
		$(backDiv).setStyle({"float":"left"});
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
		defaultPanelTdLeft.appendChild(refreshAllDiv);
		defaultPanelTdLeft.appendChild(roleListDiv);
		
		if(commandBarTabId == self.displayTabId){
			var roleEditDiv = document.createElement("div");
			roleEditDiv.id = "tab_"+this.displayTabId+"_roleEditPanel";
			roleEditDiv.style.display = "none";

			var editAreaDiv = self.buildEditArea();
			roleEditDiv.appendChild(editAreaDiv);
			defaultPanelTdLeft.appendChild(roleEditDiv);
		}

		var tabContent = $("tabContent");
		if(tabContent.firstChild) {
			tabContent.replaceChild(defaultPanelDiv, tabContent.firstChild);
		} else {
			tabContent.appendChild(defaultPanelDiv);
		}

		this.addSortableEvent();
		
		this.updateRawStyle();
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
		IS_Event.observe(addDefaultDiv, 'click', this.addRole.bind(this, false), false, ["_adminPanelTab","_adminPanel"]);

		roleListContainer.appendChild(addDefaultDiv);

		var defaultRoleJson;
		for(var i in this.displayRoleJsons) {
			if( (this.displayRoleJsons[i] instanceof Function) ) continue;
			if(this.displayRoleJsons[i].isDefault)
			  defaultRoleJson = this.displayRoleJsons[i];
		}
		if( !(self.displayTabId == commandBarTabId || self.displayTabId == portalHeaderTabId || self.displayTabId == '0') ){
			var disabledDefualtDiv = document.createElement('div');
			disabledDefualtDiv.className = 'iconButton';
			disabledDefualtDiv.style.cssFloat = "left";
			disabledDefualtDiv.style.styleFloat = "left";
			var disableDefaultCheckbox = document.createElement('input');
			disableDefaultCheckbox.id = "disableDefaultCheck";
			disableDefaultCheckbox.type = 'checkbox';
			disableDefaultCheckbox.defaultChecked = defaultRoleJson.disabledDefault;
			disabledDefualtDiv.appendChild(disableDefaultCheckbox);
			disabledDefualtDiv.appendChild(document.createTextNode(ISA_R.alb_noDefault));
			IS_Event.observe(disableDefaultCheckbox, "click", function(checkbox, defaultRoleJson){
				// Update to public object
				defaultRoleJson.disabledDefault = checkbox.checked;
				if(checkbox.checked)
				  Element.hide("tab_"+self.displayTabId+'_role_' + defaultRoleJson.id);
				else
				  Element.show("tab_"+self.displayTabId+'_role_' + defaultRoleJson.id);
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
			[ISA_R.alb_order, ISA_R.alb_roleName, ISA_R.alb_subject, ISA_R.alb_regularExpression, ISA_R.alb_edit, ISA_R.alb_copy, ISA_R.alb_delete],
			['40px', '165px', '85px','320px', '40px', '40px', '40px']
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

	this.addRole = function(copyObject){
        var datetime = new Date().getTime();

        var principalMap = ISA_Principals.get();
        var jsonObject;
        
        if(copyObject){
            jsonObject = $jq.extend(true, {}, copyObject);
            jsonObject.id = String(datetime);
            jsonObject.defaultUid = String(datetime);
            jsonObject.isDefault = false;
            jsonObject.roleName += " - " + ISA_R.alb_copy;
            
            var principalExists = $jq.grep(principalMap, function(principal){
                return principal.type == jsonObject.principalType;
            }).length > 0;
            
            if(!principalExists){
                jsonObject.principalType = (principalMap.length > 1) ? principalMap[1].type : principalMap[0].type;
            }
        }else{
            jsonObject = {
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
                tabNumber : displayTabNumber
            };
            
            // Defalut fixed area setting.
            if(jsonObject.tabId == commandBarTabId) {
                jsonObject = self.templates.setCommandLayout(jsonObject);
            }
            else if(jsonObject.tabId == portalHeaderTabId) {
                jsonObject = self.templates.setPortalHeaderLayout(jsonObject);
            }
            else {
                jsonObject = self.templates.setStaticLayout0(jsonObject);
                jsonObject = self.setColumnsArray(jsonObject);
            }
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
        
        self.addSortableEvent();
        
        self.isUpdated = true;
        ISA_Admin.isUpdated = true;
        
        var rid = "tab_"+self.displayTabId+"_role_" + datetime;
        if(!ISA_DefaultPanel.updateRaws.contains(rid))
            ISA_DefaultPanel.updateRaws.push(rid);
        this.updateRawStyle();
	}
	
	this.addSortableEvent = function(){
		var draggingDivId = false;
		Sortable.create($("tab_"+this.displayTabId+"_roleGroup"),
			{
				tag: 'div',
				handle: 'handle',
				//className: 'configListDiv',
				onChange: function(div){
					draggingDivId = div.id;
				},
				starteffect: function(div) {
					// opacity effect drag start
					div.style.opacity = 0.7;
					div.style.filter = 'alpha(opacity=70)';
				},
				endeffect: function(div) {
					// opacity effect drag start
					div.style.opacity = 1.0;
					div.style.filter = 'alpha(opacity=100)';
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
//							var divId = divIdPrefix + oldJsons[i].roleOrder;
							var divId = divIdPrefix + oldJsons[i].id;
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
		roleDiv.id = "tab_"+this.displayTabId+"_role_" + jsonRole.id;
		roleDiv.roleId = jsonRole.id;
		roleDiv.className = "configTableDiv";

		var table = document.createElement("table");
		table.className = "configTableHeader";
		table.cellPadding = "0";
		table.cellSpacing = "0";
		table.border = "0";
		table.style.width = "900px";
		table.style.tableLayout = "fixed";
		table.style.margin = "0";
		roleDiv.appendChild(table);

		var tbody = document.createElement("tbody");
		table.appendChild(tbody);

		var tr = document.createElement("tr");
		//tr.className = "panelRoleTr";
		tbody.appendChild(tr);

		var sortableTd = document.createElement("td");
		sortableTd.className = "configTableTd";
		sortableTd.style.width = "40px";
		sortableTd.style.textAlign = 'center';
		if(!jsonRole.isDefault){
			var imgSrc = imageURL +"drag.gif";
			
			var sortableImg = document.createElement("img");
			sortableImg.src = imgSrc;
			
			sortableImg.style.width = "16px";
			sortableImg.style.height = "16px";
			sortableImg.style.cursor = "move";
			sortableImg.title = ISA_R.alb_changingOrder;

			sortableTd.appendChild( sortableImg );
		}
		tr.appendChild(sortableTd);

		var roleNameTd = document.createElement("td");
		roleNameTd.className = "configTableTd";
		roleNameTd.style.width = "165px";
		roleNameTd.style.textAlign = "left";
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
		roleTypeTd.className = "configTableTd";
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
		roleTd.className = "configTableTd";
		roleTd.style.width = "320px";
		roleTd.style.textAlign = "left";
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
		editTd.className = "configTableTd";
		editTd.style.width = "40px";
		editTd.style.textAlign = "center";
		tr.appendChild(editTd);
		var editImg = document.createElement("img");
		editImg.src = imageURL + "edit.gif";
		editImg.style.cursor = "pointer";
		editImg.title = ISA_R.alb_editing;
		editTd.appendChild(editImg);
		
		if(self.displayTabId == commandBarTabId){
			IS_Event.observe(editImg, "click", this.editCommandBarRole.bind(this, jsonRole, roleDiv), false, ["_adminPanelTab","_adminPanel"]);
		}
		else if(self.displayTabId == portalHeaderTabId){
			IS_Event.observe(editImg, "click", function(jsonRole, roleDiv){
				this.displayRoleId = jsonRole.id;
				if(!this.portalHeaderHtmlModal){
					this.portalHeaderHtmlModal = new Control.Modal('', {
						className: "adminDefaultPanel",
						width: 580,
						afterClose: function(){
							this.portalHeaderHtmlModal.container.update('');
							this.displayRoleId = false;
						}.bind(this)
					});	
				}
				var formDiv = $jq("<div>").addClass("modalConfigSet").css({"text-align":"center"});
				var messageLabel = $jq("<p>").addClass("modalConfigSetHeader").css({clear:"both"}).text(ISA_R.alb_editHTML);
				formDiv.append(messageLabel);
				var editArea = $jq("<textarea>").css({margin:"5px", width:"90%"}).attr("rows", 20).val(jsonRole.layout);
				formDiv.append(editArea);
				
				var okDiv = $jq("<div>").css({clear:"both", textAlign:"center"});
				var okButton = $jq("<input>").addClass("modal_button").attr("type", "button").val(ISA_R.alb_ok);
				okDiv.append(okButton);
				okButton.click(function(jsonRole, editArea, e) {
					this.setNewValue("layout", editArea.val());
					this.updatePanel();
					this.portalHeaderHtmlModal.close();
				}.bind(this, jsonRole, editArea));
				
				var closeButton = $jq("<input>").addClass("modal_button").attr("type", "button").val(ISA_R.alb_cancel);
				okDiv.append(closeButton);
				closeButton.click(function(){
					this.close();
				}.bind(this.portalHeaderHtmlModal));
				formDiv.append(okDiv);
				
				this.portalHeaderHtmlModal.container.update(formDiv.get(0));
				this.portalHeaderHtmlModal.open();
			}.bind(this, jsonRole, roleDiv), false, ["_adminPanelTab","_adminPanel"]);
		}
		else{
			IS_Event.observe(editImg, "click", function(jsonRole){
				this.displayRoleId = jsonRole.id;
				this.displayRoleOrder = jsonRole.roleOrder;
				
				if(!self.updatePanel())
					return;
				
				this.editRoleWin = null;
				this.editRoleWin = window.open("editRole?id=" + jsonRole.id, "editRoleWin", 'width=800, height=600, menubar=no, toolbar=no, scrollbars=yes, resizable=yes');
			}.bind(this, jsonRole));
		}
		
        var copyTd = document.createElement("td");
        copyTd.className = "configTableTd";
        copyTd.style.width = "40px";
        copyTd.style.textAlign = "center";
        tr.appendChild(copyTd);
        
        var copyImg = document.createElement("img");
        copyImg.src = imageURL + "page_copy.png";
        copyImg.style.cursor = "pointer";
        copyImg.title = IS_R.lb_createCopy;
        copyTd.appendChild(copyImg);
        IS_Event.observe(copyImg, "click", this.addRole.bind(this, jsonRole), false, ["_adminPanelTab","_adminPanel"]);
        
        var deleteTd = document.createElement("td");
		deleteTd.className = "configTableTd";
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
	
	Event.observe(window, 'unload', function(e){
		if(this.editRoleWin)
			this.editRoleWin.close();
	}.bind(this));
	
	/**
		Edit permission
	*/
	this.editCommandBarRole = function(jsonRole) {
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

		// Replace to the permission ID currently displayed
		this.displayRoleId = jsonRole.id;
		this.displayRoleOrder = jsonRole.roleOrder;
		
		this.buildCommandWidgetsList(jsonRole);
		this.addSortableEventCommand();
	}

	/**
		Delete permission
	*/
	this.deleteRole = function(jsonRole) {
		// Update public object 
		delete this.displayRoleJsons[jsonRole.id];
		// Delete itself
//		var deleteElement = document.getElementById("tab_"+this.displayTabId+"_role_" + jsonRole.roleOrder);
		var deleteElement = document.getElementById("tab_"+this.displayTabId+"_role_" + jsonRole.id);
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
//							var roleNameDiv = $("tab_"+self.displayTabId+"_role_" + self.displayRoleOrder).getElementsByTagName('td')[1];
							var roleNameDiv = $("tab_"+self.displayTabId+"_role_" + self.displayRoleId).getElementsByTagName('td')[1];
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
//							var roleTypeDiv = $("tab_"+self.displayTabId+"_role_" + self.displayRoleOrder).getElementsByTagName('select')[0];
							var roleTypeDiv = $("tab_"+self.displayTabId+"_role_" + self.displayRoleId).getElementsByTagName('select')[0];
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
//							var roleNameDiv = $("tab_"+self.displayTabId+"_role_" + self.displayRoleOrder).getElementsByTagName('td')[3];
							var roleNameDiv = $("tab_"+self.displayTabId+"_role_" + self.displayRoleId).getElementsByTagName('td')[3];
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

		var labelSetStaticDiv = document.createElement("div");
		labelSetStaticDiv.className = "configSet";
		var labelSetStaticLegend = document.createElement("p");
		labelSetStaticLegend.className = "configSetHeader";
		labelSetStaticLegend.appendChild(document.createTextNode(ISA_R.alb_fixedArea));
		labelSetStaticDiv.appendChild(labelSetStaticLegend);
		editAreaDiv.appendChild(labelSetStaticDiv);

		var editStaticDiv = this.editStaticDiv = document.createElement("div");
		editStaticDiv.id = "panelStaticDiv";
		//editStaticDiv.appendChild(labelSetStaticDiv);
		editStaticDiv.appendChild(this.buildStaticContainer());
		labelSetStaticDiv.appendChild(editStaticDiv);

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
		
		if(Browser.isIE){
			$jq("img", this.workContainer).each(function(idx, img){
				img = $jq(img);
				var src = img.attr("src");
				var location = String(window.location).substring(0, String(window.location).lastIndexOf("/") + 1);
				if(src.indexOf(location) == 0)
					img.attr("src", src.substring(location.length, src.length));
				
			});
		}
		
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

		var addGadget = ISA_Admin.createIconButton(ISA_R.alb_addGadget, ISA_R.alb_addGadget, "add.gif", "left");
		this.staticContainer.appendChild(addGadget);
		
		$jq(addGadget).click(function(){
			this.commandBarEditorFormObj.showEditorForm({});
		}.bind(this));
		
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
		commandBarListTh.style.width = "60px";
		commandBarListTh.appendChild(document.createTextNode(ISA_R.alb_order));
		commandBarListTr.appendChild(commandBarListTh);

		commandBarListTh = document.createElement("td");
		commandBarListTh.className = "headerDefaultPanel";
		commandBarListTh.style.whiteSpace = "nowrap";
		commandBarListTh.style.width = "240px";
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
				
				if(widget && widget.type && (widget.type.indexOf("g_") == 0 || widget.type == "Gadget")){
					commandmap.id = id;
					commandmap.onlyoutside = true;
					commandmap.togglable = false
				}
				
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
		contentDiv.style.width = "50px";
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
		commandBarTr.appendChild(commandBarTd);
		contentDiv = document.createElement("div");
		contentDiv.style.width = "230px";
		contentDiv.className = "contentsDefaultPanel";
		contentDiv.id = "disp_" + commandBarItem.id;
		var td = getParentTdElement(commandBarItem.id);
		
		var selectLabelOptions = [];
		
		var enabled = true;
		var outside = false;
		var disabledDiv = $("disabled_"+commandBarItem.id);
		if(disabledDiv && disabledDiv.getAttribute("disabledCommand")){
			enabled = false;
		}
		var enabledDiv = $(commandBarItem.id);
		if(commandBarItem.onlyoutside || (enabledDiv && enabledDiv.getAttribute("outside"))){
			outside = true;
		}
		
		var elementTd = getParentTdElement(commandBarItem.id);
		var roleJSON = this.displayRoleJsons[this.displayRoleId];
		var widgetJSON = roleJSON.staticPanel[commandBarItem.id];
		
		if(!commandBarItem.onlyoutside){
			selectLabelOptions.push({
				selected : enabled && !outside,
				name : ISA_R.alb_display_true,
				callback : function(elementTd, widgetJSON){
					var disabledDiv = $('disabled_'+commandBarItem.id);
					if(disabledDiv && disabledDiv.firstChild)
						elementTd.innerHTML = unescapeHTMLEntity(disabledDiv.firstChild.nodeValue);
					if(widgetJSON)widgetJSON.disabled = false;
					
					$jq("#" + commandBarItem.id).removeAttr("outside");
					
					this.changeCommandBarLayout();
				}.bind(this, elementTd, widgetJSON)
			});
		}
		
		selectLabelOptions.push({
			selected: enabled && outside,
			name: ISA_R.alb_display_outside,
			callback:function(elementTd, widgetJSON){
				var disabledDiv = $('disabled_'+commandBarItem.id);
				if(disabledDiv && disabledDiv.firstChild)
					elementTd.innerHTML = unescapeHTMLEntity(disabledDiv.firstChild.nodeValue);
				if(widgetJSON)widgetJSON.disabled = false;
				
				$jq("#" + commandBarItem.id).attr("outside", true);
				
				this.changeCommandBarLayout();
			}.bind(this, elementTd, widgetJSON)
		});
		
		if(commandBarItem.togglable){
			selectLabelOptions.push({
				selected : !enabled,
				name : ISA_R.alb_display_false,
				callback : function(elementTd, widgetJSON){
					
					var orgDiv = $jq("div.commandbar-item", elementTd);
					
					var disabledDiv = document.createElement("div");
					if(orgDiv){
						$jq(disabledDiv).attr("label", orgDiv.attr("label"));
						$jq(disabledDiv).attr("type", orgDiv.attr("type"));
					}
					disabledDiv.id = 'disabled_' + commandBarItem.id;
					disabledDiv.setAttribute('disabledCommand','true');
					disabledDiv.appendChild(document.createComment(escapeHTMLEntity(elementTd.innerHTML)));
					elementTd.innerHTML = "";
					elementTd.appendChild(disabledDiv);
					if(widgetJSON)widgetJSON.disabled = true;
					
					this.changeCommandBarLayout();
				}.bind(this, elementTd, widgetJSON)
			});
		}
		new ISA_Admin.SelectLabel(contentDiv, selectLabelOptions);

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
		contentDiv.style.width = "50px";
		contentDiv.className = "contentsDefaultPanel";
		if(self.commandBarEditor[commandBarItem.type]){
			var editor = self.commandBarEditor[commandBarItem.type];
			var commandBarA = document.createElement("a");
			commandBarA.style.cursor = "pointer";
			$jq(commandBarA).addClass("item-edit");
			contentDiv.appendChild(commandBarA);
			var commandBarImg = document.createElement("img");
			commandBarImg.src = imageURL + "edit.gif";
			commandBarA.appendChild(commandBarImg);
			
			if(commandBarItem.type == "gadget"){
				$jq(commandBarA).click(function(){
					editor.buildForm(this);
				}.bind(commandBarItem));
			}
			else {
				new ISA_DefaultPanel.CommandItemEditor(commandBarA, commandBarItem, editor.buildForm, editor.onOK);
			}
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
					var itemId = $jq(this).parents(".rowDefaultPanel").attr("id").substring(8);
					var elementTd = getParentTdElement(itemId);
					elementTd.parentNode.removeChild(elementTd);
					self.changeCommandBarLayout();
					var jsonRole = self.displayRoleJsons[self.displayRoleId];
					self.buildCommandWidgetsList(jsonRole);
					self.addSortableEventCommand();
					
					is_deleteProperty(jsonRole.staticPanel, itemId);
					self.setNewValue("staticpanel", Object.toJSON($jq.extend(true,{},jsonRole.staticPanel)), jsonRole.id);
				}catch(e){
					msg.error(ISA_R.ams_failedDeleteCommandBar + getErrorMessage(e));
				}
			}.bind(commandBarImg);
			IS_Event.observe(commandBarA, 'click', deleteCommandItemHandler, false, ["_adminPanelTab","_adminPanel"]);
		}
		commandBarTd.appendChild(contentDiv);

		if(commandBarItem.hidden)
			$jq(commandBarDiv).hide();

		return commandBarDiv;
	}

	/**
		Sorting CommandBar list
	*/
	this.addSortableEventCommand = function(){
		var draggingDivId = false;
		/*
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
		*/
		Sortable.create($("panelCommandGroup"),
			{
				tag: 'div',
				handle: 'handle',
				format: /^commnad_(.*)/,

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
					/*
					if(self.commandDispMap){
						var dispCheckbox = $("disp_" + draggedWidgetId).firstChild;
						if(dispCheckbox)
							dispCheckbox.checked = self.commandDispMap[draggedWidgetId];
					}
					*/
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
			var outside = existDiv.getAttribute("outside");
			if(getBooleanValue(outside))
				commandDiv.setAttribute("outside", "true");
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
		
		var commandBarItems = this.workContainer.getElementsByTagName('tr')[0].childNodes;
		for(var i = 0; i < commandBarItems.length; i++){
			if(commandBarItems[i].nodeType != 1)
				continue;
			var itemDiv = commandBarItems[i].getElementsByTagName('div')[0];
			itemDiv.className = 'commandbar-item';
		}
		
		this.displayRoleJsons[this.displayRoleId].layout = this.workContainer.innerHTML;
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
			if(jsonRole.tabId != commandBarTabId && jsonRole.tabId != portalHeaderTabId) {
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
            IS_Event.stopObserving(editForm, 'keydown', editFormOnKeydown, false);
            IS_Event.stopObserving(editForm, 'blur', editFormOnBlur, false);
            
		    var nowText = ISA_Admin.trim( editForm.value );

			// Turn back to the previous value if empty is entered
			if(nowText.length == 0) {
				beforeElement.nodeValue = beforeText;
				nowText = beforeText;
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
				if(typeof newValue == "string"){
					newValue = eval("(" + newValue + ")");
				}
				displayRole.staticPanel = newValue;
				break;
			case "dynamicpanel":
				if(typeof newValue == "string"){
					newValue = eval("(" + newValue + ")");
				}
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
		
//		var rid = "tab_"+this.displayTabId+"_role_" + this.displayRoleOrder;
		var rid = "tab_"+this.displayTabId+"_role_" + this.displayRoleId;
		if(!ISA_DefaultPanel.updateRaws.contains(rid) && newValue)
			ISA_DefaultPanel.updateRaws.push(rid);
		
		this.updateRawStyle();
	}

	this.updateRawStyle = function(){
		var roleGroupId = "#tab_" + this.displayTabId+"_roleGroup";
		$jq(roleGroupId + ">div" + "," + roleGroupId + "+div>div").each(function(idx, div){
			if(ISA_DefaultPanel.updateRaws.contains(div.id)){
				$jq(div).addClass("updateRaw");
			}else{
				$jq(div).removeClass("updateRaw");
			}
		})
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
			
			var error = null;
			if(error = IS_Validator.validate(roleJson.roleName, {maxBytes:256, label:ISA_R.alb_roleName})){
				errorMessages.push(error);
			}
			if(error = IS_Validator.validate(roleJson.role, {format:'regexp', label:ISA_R.alb_regularExpression})){
				errorMessages.push(error);
			}

			// It need to cast if it is Number
			roleJson.roleOrder = roleOrder++;
			this.displayRoleJsons[i].roleOrder = roleJson.roleOrder;
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

		var url = adminHostPrefix + "/services/tabLayout/updateDefaultPanel";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			postBody: Object.toJSON([ tabId, roleJsons, false]),
			asynchronous:!isSync,
			onSuccess: function(response){
				var array = eval(response.responseText);
				
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
				throw t;
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
		var url = adminHostPrefix + "/services/tabLayout/removeDefaultPanel";
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
			var url = adminHostPrefix + "/services/widgetConf/getWidgetConfJson";
			var opt = {
				method: 'get' ,
				asynchronous:true,
				onSuccess: function(response){
					eval("ISA_SiteAggregationMenu.setWidgetConf("+response.responseText+")");
					self.displayDefaultPanel();
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
		getWidgetConf();

	};

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

ISA_DefaultPanel.updateRaws = [];