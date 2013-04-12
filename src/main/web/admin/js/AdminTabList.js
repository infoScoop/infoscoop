
(function($jq){
	$jq.fn.ISA_TabList = function(options) {
       	var self = this;
		var content = $jq(this);
		var deleteIdList = [];
		var controlModal;			// Apply Change dialog
		var tabAdminModal;			// tab admin
		var resetUserCustomizeModal;// resetUserCustomization

		this.build = function(){
			if(!controlModal){
				controlModal = new Control.Modal('',{
						className:"commitDialog",
						closeOnClick:false
					});
			}

			var propertiesTable = $jq("<table>");
			propertiesTable.attr("border", 1)
				.attr("cellSpacing", 0)
				.attr("cellPadding", 0)
				.addClass("tabListGroup");

			var propertiesTbody = $jq("<tbody>").attr("id", "propertiesTbody");
			propertiesTable.append(propertiesTbody);

			var propertiesTr = $jq("<tr>");
			propertiesTbody.append(propertiesTr);

			var propertiesTd;
			propertiesTd = $jq("<td>")
				.addClass("headerProperties")
				.css({"white-space":"nowrap", "width":"10%", "padding":"5px"});
			propertiesTd.text(ISA_R.alb_sequence);
			propertiesTr.append(propertiesTd);

			propertiesTd = $jq("<td>")
				.addClass("headerProperties")
				.css({"white-space":"nowrap", "width":"400px", "padding":"5px"});
			propertiesTd.text(ISA_R.alb_tabDesc);
			propertiesTr.append(propertiesTd);

			propertiesTd = $jq("<td>")
				.addClass("headerProperties")
				.css({"white-space":"nowrap", "width":"10%", "padding":"5px"});
			propertiesTd.text(ISA_R.alb_edit);
			propertiesTr.append(propertiesTd);

			propertiesTd = $jq("<td>")
				.addClass("headerProperties")
				.addClass("hiddenTabAdmin")
				.css({"white-space":"nowrap", "width":"10%", "padding":"5px"});
			propertiesTd.text(ISA_R.alb_admin);
			propertiesTr.append(propertiesTd);

			propertiesTd = $jq("<td>")
				.addClass("headerProperties")
				.addClass("hiddenTabAdmin")
				.css({"white-space":"nowrap", "width":"10%", "padding":"5px"});
			propertiesTd.text(ISA_R.alb_delete);
			propertiesTr.append(propertiesTd);
			
			$jq("#tabList").append(propertiesTable);
			
			$jq.each(tabListJSON, function(idx, tabObj){
				propertiesTable.append(buildTabTr(tabObj));
			});
		}
		
		function buildTabTr(tabObj) {
			
			var tr = $jq("<tr>").attr("id", "row_" + tabObj.id);
			
			if(!tabObj) return tr;
			
			var td;
			var commoncss = {"padding":"3px", "text-align":"center"};
			
			td = $jq("<td>").css(commoncss).text(tabObj.rowNo);
			tr.append(td);
			
			var tabDesc = tabObj.tabDesc ? escapeHTMLEntity(tabObj.tabDesc) : "";
			td = $jq("<td>")
				.css(commoncss)
				.css({"text-align":"left","word-break":"break-all"})
				.html(tabDesc.replace(/(\r\n|\n|\r)/g, '<br/>'));
			tr.append(td);
			
			var editButton = $jq("<img>")
				.attr("title", ISA_R.alb_editing)
				.attr("src", imageURL + "edit.gif")
				.css({"cursor":"pointer"});
			
			editButton.click({tabId: tabObj.id}, function(e){
				if(!ISA_Admin.checkUpdated())
					return false;
				window.location.href = "editTab?tabId=" + e.data.tabId;
			});
			
			td = $jq("<td>").css(commoncss).append(editButton);
			tr.append(td);
			
			var adminButton = $jq("<img>")
				.attr("title", ISA_R.alb_editing)
				.attr("src", imageURL + "edit.gif")
				.css({"cursor":"pointer"});
			
			adminButton.click({tabId: tabObj.id, adminUidList: tabObj.adminUidList}, function(e){
				var formContent = document.createElement("div");
				if(!tabAdminModal){
					tabAdminModal = new Control.Modal( '',{
							closeOnClick: 'overlay',
							width: 400
						});
				}
				$jq(formContent).ISA_TabAdminForm(tabObj);
				tabAdminModal.container.update(formContent);
				tabAdminModal.open();
			});
			
			td = $jq("<td>").css(commoncss).append(adminButton).addClass("hiddenTabAdmin");
			tr.append(td);
			
			if(tabObj.id != "0"){
				var deleteButton = $jq("<img>")
					.attr("title", ISA_R.alb_deleting)
					.attr("src", imageURL + "trash.gif")
					.css({"cursor":"pointer"});
				
				deleteButton.click({tabId: tabObj.id}, function(e){
					if( !confirm(ISA_R.ams_confirmDeleting) ) {
						return;
					}
					
					deleteIdList.push(e.data.tabId);
					$jq("#row_" + e.data.tabId).remove();
					ISA_Admin.isUpdated = true;
				});
				td = $jq("<td>").css(commoncss).append(deleteButton);
			}else{
				td = $jq("<td>").text(" ");
			}
			td.addClass("hiddenTabAdmin");
			tr.append(td);

			return tr;
		}
		
		this.resetUserCustomization = function() {
			var content = document.createElement("div");
			content.className = "resetConfigurations";

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
				Control.Modal.close();
				if( uid == "" || /^[ 　]+$/.test( uid ) )
					return;

				this._resetUserCustomization( uid );
			}.bind( this ),false,"_adminPanel");

			var cancelButton = document.createElement("input");
			cancelButton.type = "button";
			cancelButton.value = "Cancel";
			commands.appendChild( cancelButton );
			IS_Event.observe( cancelButton,"click",function() { Control.Modal.close() },false,"_adminPanel");

			if(!resetUserCustomizeModal){
				resetUserCustomizeModal = new Control.Modal( '',{
					closeOnClick: 'overlay'
				});
			}
			resetUserCustomizeModal.container.update(content);
			resetUserCustomizeModal.open();
		}
		this._resetUserCustomization = function( uid ) {
			controlModal.container.update(ISA_R.ams_applyingChanges);
			controlModal.open();
			var url = adminHostPrefix + "/services/tab/clearConfigurations";
			var opt = {
				method: 'post' ,
				contentType: "application/json",
				postBody: Object.toJSON([ uid ]),
				asynchronous:true,
				onSuccess: function(response){
					controlModal.container.update(ISA_R.ams_changeUpdated);
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
					throw t;
				},
				onComplete: function(){
					setTimeout(function(){
						Control.Modal.close();
					},500);
				}
			};
			AjaxRequest.invoke(url, opt);
		}

		var addTab = function(e) {
			var defaultPanel = ISA_DefaultPanel.defaultPanel;
			defaultPanel.displayRoleJsons = {};
			
			var datetime = new Date().getTime();
			var jsonObject = {
				id : String( datetime ),
				tabId : "",
				tabName : ISA_R.alb_newTab,
				columnsWidth : "",
				principalType : null,
				role : defaultPanel.defaultRoleRegex,
				roleName : defaultPanel.defaultRoleName,
				defaultUid : defaultPanel.defaultDefaultUid,
				layout : "",
				staticPanel : {},
				dynamicPanel : {},
				widgetsLastmodified : null,
				tabNumber : "",
				isDefault : true
			};
			
			jsonObject = defaultPanel.setColumnsArray(jsonObject);
			jsonObject.columnsWidth = jsonObject.columnsWidth.replace(/\"/g, "&quot;");
			// Set default fixed area
			jsonObject = defaultPanel.templates.setStaticLayout0(jsonObject);
			
			$jq("#addTabJson").val(Object.toJSON(jsonObject));

			if(!ISA_Admin.checkUpdated())
				return false;

			$jq("#addTabForm").submit();
		};
		
		var commitTabs = function(){
			var adminUidJson = {};
			$jq.each(tabListJSON, function(idx, tabObj){
				adminUidJson[tabObj.id] = $jq.grep(tabObj.adminUidList, function(val, idx){
					return val != null;
				});
			});
			
			var jsonObject = {
				deleteIdList : deleteIdList,
				adminUidJson : adminUidJson
			};

			// TODO: change ajax request process
			controlModal.container.update(ISA_R.ams_applyingChanges);
			controlModal.open();
			var opt = {
				method: 'post' ,
				contentType: "application/json",
				postBody: Object.toJSON(jsonObject),
				asynchronous:true,
				onSuccess: function(response){
					controlModal.container.update(ISA_R.ams_changeUpdated);
				}.bind(this),
				onFailure: function(t) {
					var errormsg = t.responseText && typeof t.responseText == "string" ? t.responseText.substr(0, 100) : "";
					alert(ISA_R.ams_failedToSaveTop+'\n' + errormsg);
					msg.error( ISA_R.ams_failedToSaveTop+ errormsg);
				},
				onComplete: function(){
					setTimeout(function(){
						Control.Modal.close();
						window.location.href = "index";
					},500);
				}
			};
			AjaxRequest.invoke("commitTab", opt);
		}
		
		var addTabDiv = $("addTab");
		if(addTabDiv)
			IS_Event.observe(addTabDiv,"click",addTab.bind( this ),false,"_adminPanel");
		
		var changeApplyDiv = $("changeApply");
		if(changeApplyDiv)
			IS_Event.observe(changeApplyDiv,"click", commitTabs.bind( this ),false,"_adminPanel");
		
		var resetDiv = $("clearConfigurationButton");
		if(resetDiv)
			IS_Event.observe(resetDiv,"click", this.resetUserCustomization.bind( this ),false,"_adminPanel");
		
		var previewDiv = $("previewTop");
		if(previewDiv)
			IS_Event.observe(previewDiv, 'click', ISA_previewFormModal.init, false, "_adminPanel");
		
		this.build();
	}

	$jq.fn.ISA_TabAdminForm = function(tabObj) {
		var content = $jq("<div>").addClass("tabAdminModal").appendTo($jq(this))
		var self = this;
		var tempAdminList = $jq.merge([], tabObj.adminUidList);
		
		this.init = function(){
			content.addClass("modalConfigSet");
			var title = $jq("<p>").addClass("modalConfigSetHeader").text(ISA_R.alb_tab + tabObj.rowNo + " - " + ISA_R.alb_adminSettings)
			content.append(title);
			
			var inputBox = $jq("<input>")
				.addClass("tabAdminInput")
				.attr("id", "addUid_" + tabObj.id)
				.css({"float":"left"});
			inputBox.autocomplete({
				source: tabAdminList
			});
			content.append(inputBox);
			
			var addButton = $jq(ISA_Admin.createIconButton(ISA_R.alb_add, ISA_R.alb_add, "add.gif", "left"));
			var addFunc = function(){
				var addUid = $jq("#addUid_" + tabObj.id).val();
				this.addAdmin(addUid);
				$jq("#addUid_" + tabObj.id).select();
			}.bind(this);
			addButton.click(addFunc);
			
			inputBox.keypress(function(event){
				if(event.which === 13)
					addFunc();
			});
			
			content.append(addButton);

			var propertiesTable = $jq("<table>");
			propertiesTable
				.attr("border", 1)
				.attr("cellSpacing", 0)
				.attr("cellPadding", 0)
				.attr("align", "center")
				.addClass("tabAdminsGroup");

			var propertiesTbody = $jq("<tbody>").attr("id", "tabAdminsTbody");
			propertiesTable.append(propertiesTbody);
			content.append(propertiesTable);
			
			var propertiesTr = $jq("<tr>");
			propertiesTbody.append(propertiesTr);

			var uidTd = $jq("<td>")
				.addClass("headerProperties")
				.css({"white-space":"nowrap", "padding":"5px"});
			uidTd.text(IS_R.lb_userID);
			propertiesTr.append(uidTd);
			
			var deleteTd = $jq("<td>")
				.addClass("headerProperties")
				.css({"width":"20%", "padding":"5px"});
			deleteTd.text(ISA_R.alb_delete);
			propertiesTr.append(deleteTd);
			
			$jq.each(tabObj.adminUidList, function(idx, userId){
				if($jq.inArray(userId, tabAdminList) > -1){
					propertiesTable.append(this.buildTr.bind(this, userId));
				}else{
					tabObj.adminUidList[idx] = null;
				}
			}.bind(this));

			var controlDiv = $jq("<div>").css({"text-align":"center"});

			var okButton = $jq("<input>").attr("type", "button");
			okButton.val(ISA_R.alb_save);
			okButton.click(this.commitAdmin.bind(this));
			controlDiv.append( okButton );

			var cancelButton = $jq("<input>").attr("type", "button");
			cancelButton.val(ISA_R.alb_cancel);
			cancelButton.click(Control.Modal.close);
			controlDiv.append(cancelButton);
			
			content.append(controlDiv);
		}
		
		this.buildTr = function(userId){
			var tr = $jq("<tr>").attr("id", "admin_row_" + userId);
			
			if(!tabObj) return tr;
			
			var td;
			var commoncss = {"padding":"3px", "text-align":"center"};
			
			td = $jq("<td>").css(commoncss).text(userId);
			tr.append(td);
			
			var deleteButton = $jq("<img>")
				.attr("title", ISA_R.alb_deleting)
				.attr("src", imageURL + "trash.gif")
				.css({"cursor":"pointer"});
			
			deleteButton.click(self.deleteAdmin.bind(this, userId));
			td = $jq("<td>").css(commoncss).append(deleteButton);
			
			tr.append(td);

			return tr;
		}
		
		this.addAdmin = function(userId){
			if($jq.inArray(userId, tempAdminList) > -1 || $jq.inArray(userId, tabAdminList) == -1)
				return;
			
			tempAdminList.push(userId);
			$jq("#tabAdminsTbody").append(this.buildTr(userId));
			
			ISA_Admin.isUpdated = true;
		}
		
		this.deleteAdmin = function(userId){
			tempAdminList = $jq.grep(tempAdminList, function(val, idx) {
				return (val != userId);
			});
			$jq("#admin_row_" + userId).remove();
			ISA_Admin.isUpdated = true;
		}
		
		this.commitAdmin = function(){
			// replace adminUidList
			tabObj.adminUidList = tempAdminList;
			Control.Modal.close();
			ISA_Admin.isUpdated = true;
		}
		
		this.init();
	}
})(jQuery);
