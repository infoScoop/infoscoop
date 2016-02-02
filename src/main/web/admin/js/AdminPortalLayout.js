var ISA_PortalLayout = IS_Class.create();

ISA_PortalLayout.portalLayout = false;
ISA_PortalLayout.portalLayoutList = false;

// Called by returned value from server
ISA_PortalLayout.setPortalLayouts = function(_layoutList) {
	ISA_PortalLayout.portalLayoutList = _layoutList;
}

ISA_PortalLayout.prototype.classDef = function() {
	var self = this;
	var container;
	var loadingMessage;
	var controlModal;		// Apply Change dialog
	var logoImgSrc;
	var faviconSrc;
	var isUpdatedLogoImage;
	var currentDispSettings;

	this.initialize = function() {
		container = document.getElementById("portalLayout");
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
	
	this.displayPortalLayout = function() {
		// Layout ID currently displayed
		this.displayLayoutId = false;
		
		var portalLayoutDiv = document.createElement("div");
		
		var refreshAllDiv = document.createElement("div");
		refreshAllDiv.className = "refreshAll";
		refreshAllDiv.style.textAlign = "right";
		
		var commitDiv = ISA_Admin.createIconButton(ISA_R.alb_changeApply, ISA_R.alb_changeApply, "database_save.gif", "right");
		refreshAllDiv.appendChild(commitDiv);
		IS_Event.observe(commitDiv, 'click', this.commitPortalLayout.bind(this), "_adminPortal");
		
		var refreshDiv = ISA_Admin.createIconButton(ISA_R.alb_refresh, ISA_R.alb_reloadWithourSaving, "refresh.gif", "right");
		refreshAllDiv.appendChild(refreshDiv);
		IS_Event.observe(refreshDiv, 'click', function(){
			if( !ISA_Admin.checkUpdated() )
				return;

			ISA_Admin.isUpdate = false;
			isUpdatedLogoImage = false;
			ISA_PortalLayout.portalLayout = new ISA_PortalLayout();
			ISA_PortalLayout.portalLayout.build();
		}, false, "_adminPortal");
		
        var advancedDiv = ISA_Admin.createIconButton(ISA_R.alb_advancedSettings, ISA_R.alb_advancedSettings, "wrench.gif", "right");
        refreshAllDiv.appendChild(advancedDiv);
        IS_Event.observe(advancedDiv, 'click', function(){
            $jq(".advanced").show();
        }, false, "_adminPortal");
		
        var advancedMsg = document.createElement("div");
        $jq(advancedMsg).addClass("advanced-message").addClass("advanced");
        $jq(advancedMsg).append("<span>" + ISA_R.alb_notSupportedMessage + "</span>");
        
		portalLayoutDiv.appendChild(refreshAllDiv);
		portalLayoutDiv.appendChild(advancedMsg);
		portalLayoutDiv.appendChild(self.buildPortalLayouts());
		
		container.replaceChild(portalLayoutDiv, loadingMessage);
		
		// The first item is selected at the initial display
		for(var i in ISA_PortalLayout.portalLayoutList) {
			if( (ISA_PortalLayout.portalLayoutList[i] instanceof Function) ) continue;
			
			this.displayLayoutId = i;
			break;
		}
//		this.displayLayoutId = 'customTheme';
		this.changeLayout();

		// upload form
		this.buildUploadForm('upLoadDummyFrame', 'getPortalLogo');
		this.buildUploadForm('upLoadFaviconDummyFrame', 'getFavicon');

		// portal logo
		$jq.ajax({
			method: 'get',
			url: hostPrefix + '/logosrv/existsPortalLogo',
			success: function(data) {
				if(data == 'true') {
					logoImgSrc = hostPrefix + '/logosrv/getPortalLogo?' + (new Date()).getTime();
				} else {
					logoImgSrc = staticContentURL + '/skin/imgs/infoscoop_logo.png';
				}
			}
		});

		// favicon
		$jq.ajax({
			method: 'get',
			url: hostPrefix + '/logosrv/existsFavicon',
			success: function(data) {
				if(data == 'true') {
					faviconSrc = hostPrefix + '/logosrv/getFavicon?' + (new Date()).getTime();
				} else {
					faviconSrc = hostPrefix + '/favicon.ico';
				}
			}
		});
	}

	this.buildUploadForm = function(iframeId, imgUrl) {
			var iframe = $(iframeId);
			Event.observe( iframe,"load",function() {
				try {
					var result = $jq(this.contentWindow.document.body).text();
					if(result) {
						var status = eval("("+result+")").status;
						if(status == 500)
							throw new Error();
					} else {
						$jq('#logo-image-input').attr('upload', 'true');

						if(imgUrl == 'getFavicon') {
							faviconSrc = hostPrefix + '/logosrv/' + imgUrl + '?' + (new Date()).getTime();
						} else if(imgUrl == '') {
							logoImgSrc = hostPrefix + '/logosrv/' + imgUrl + '?' + (new Date()).getTime();
						}

						setTimeout(function(){
							Control.Modal.close();
						},500);
					}
				} catch(e) {
						alert(ISA_R.ams_gadgetResourceUpdateFailed);
						msg.error(ISA_R.ams_gadgetResourceUpdateFailed + " - " +" | 500");
						Control.Modal.close();
						$jq('#logo-image').removeAttr('src');
				}
			});
	}

	this.buildPortalLayouts = function() {
		var portalLayoutsDiv = document.createElement("div");
		portalLayoutsDiv.id = "portalLayouts";
		
		var portalLayoutsTable = document.createElement("table");
		portalLayoutsTable.id = "portalLayoutsTable";
		portalLayoutsTable.style.width = "100%";
		portalLayoutsTable.style.tableLayout = "fixed"
		
		var portalLayoutsTbody = document.createElement("tbody");
		portalLayoutsTable.appendChild(portalLayoutsTbody);
		
		var portalLayoutsTr = document.createElement("tr");
		portalLayoutsTbody.appendChild(portalLayoutsTr);
		
		var portalLayoutsTdLeft = document.createElement("td");
		portalLayoutsTdLeft.id = "layoutListTd";
		portalLayoutsTdLeft.style.width = "200px";
		portalLayoutsTdLeft.style.verticalAlign = "top";
		portalLayoutsTr.appendChild(portalLayoutsTdLeft);
		
		var portalLayoutsTdRight = document.createElement("td");
		portalLayoutsTdRight.id = "layoutEditTd";
		portalLayoutsTdRight.style.verticalAlign = "top";
		portalLayoutsTr.appendChild(portalLayoutsTdRight);
		
		var listDiv = this.buildLayoutList();
		portalLayoutsTdLeft.appendChild(listDiv);
		
		var editDiv = this.buildEditArea();
		portalLayoutsTdRight.appendChild(editDiv);
		
		portalLayoutsDiv.appendChild(portalLayoutsTable);
		
		return portalLayoutsDiv;
	}
	
	/**
		Create layout list
	*/
	this.buildLayoutList = function() {
		var layoutListDiv = document.createElement("div");
		
		var layoutGroupDiv = document.createElement("div");
		layoutGroupDiv.id = "layoutGroup";
		layoutListDiv.appendChild(layoutGroupDiv);

		var initDispFlg = false;
		for(var i in ISA_PortalLayout.portalLayoutList) {
			if( (ISA_PortalLayout.portalLayoutList[i] instanceof Function) ) continue;

			if(!initDispFlg) {
				currentDispSettings = i;
				initDispFlg = true;
			}
			var div = this.buildLayout(i);
			layoutGroupDiv.appendChild(div);
		}

		ISA_PortalLayout.portalLayoutList['logo'] = {'name': 'logo', 'layout':''};
		ISA_PortalLayout.portalLayoutList['favicon'] = {'name': 'favicon', 'layout': ''};
		layoutGroupDiv.appendChild(this.buildLayout('logo'));
		layoutGroupDiv.appendChild(this.buildLayout('favicon'));

		return layoutListDiv;
	}
	
	/**
		Create layout items
	*/
	this.buildLayout = function(layoutId) {
		var jsonLayout = ISA_PortalLayout.portalLayoutList[layoutId];
		var layoutDiv = document.createElement("div");
		layoutDiv.id = "layout_" + jsonLayout.name;
		if(jsonLayout.advanced)
		    $jq(layoutDiv).addClass("advanced");
		
		var table = document.createElement("table");
		table.className = "portalLayoutTable";
		layoutDiv.appendChild(table);
		
		var tbody = document.createElement("tbody");
		table.appendChild(tbody);
		
		var tr = document.createElement("tr");
		tr.className = "portalLayoutTr";
		tbody.appendChild(tr);
		
		var layoutNameTd = document.createElement("td");
		layoutNameTd.className = "portalLayoutTd";
		layoutNameTd.style.width = "100%";
		tr.appendChild(layoutNameTd);
		var layoutNameDiv = document.createElement("div");
		layoutNameTd.appendChild(layoutNameDiv);
		layoutNameDiv.appendChild(document.createTextNode(ISA_R["alb_"+jsonLayout.name]));
		layoutNameDiv.setAttribute("title", ISA_R["alb_"+jsonLayout.name+"_desc"]);

		var changeLayoutHandler = function(e){
			if((currentDispSettings=='logo' || currentDispSettings=='favicon') && isUpdatedLogoImage) {
				if( !confirm(ISA_R.ams_confirmChangeLost) ) {
					return false;
				}
			}
			isUpdatedLogoImage = false;
			currentDispSettings = layoutId;
			self.displayLayoutId = layoutId;
			self.changeLayout();
		};
		IS_Event.observe(layoutNameTd, "click", changeLayoutHandler, false, "_adminPortal");
		
		return layoutDiv;
	}
	
	/**
		Switch layout
	*/
	this.changeLayout = function() {
		var jsonLayout = ISA_PortalLayout.portalLayoutList[this.displayLayoutId];
		
		this.buildEditArea();
		
		var layoutElement = document.getElementById("portalLayoutTextarea");
		if(layoutElement)
			layoutElement.value = ISA_Admin.replaceUndefinedValue(jsonLayout.layout);
		
		this.selectLayout();
	}
	
	/**
		Select edit
	*/
	var currentSelectedLayout = false;
	this.selectLayout = function() {
		if(currentSelectedLayout){
		    $jq(currentSelectedLayout).removeClass("portalLayoutSelected");
		}
		currentSelectedLayout = document.getElementById("layout_" + this.displayLayoutId);
		$jq(currentSelectedLayout).addClass("portalLayoutSelected");
	}
	
	/**
		Create edit area
	*/
	var displayEditAreaDiv = false;
	this.buildEditArea = function() {
		if(!displayEditAreaDiv)
			displayEditAreaDiv = document.createElement("div");
		
		if(!displayEditAreaDiv.firstChild)
			displayEditAreaDiv.appendChild(this.buildEditLayout());
		else
			displayEditAreaDiv.replaceChild(this.buildEditLayout(), displayEditAreaDiv.firstChild);
		
		return displayEditAreaDiv;
	}
	
	/**
		Create edit setting area
	*/
	this.buildEditLayout = function() {
		var layoutName = "";
		if(this.displayLayoutId)
			layoutName = ISA_PortalLayout.portalLayoutList[this.displayLayoutId].name;
		
		var editLayoutDiv = document.createElement("div");
		
		var fieldset = document.createElement("div");
		fieldset.className = "modalConfigSet";
		editLayoutDiv.appendChild(fieldset);
		
		var legend = document.createElement("p");
		legend.className = "modalConfigSetHeader";
		fieldset.appendChild(legend);
		legend.appendChild(document.createTextNode(ISA_R.alb_editSettings));
		
		var editLayoutTextareaDiv = document.createElement("div");
		editLayoutTextareaDiv.className = "modalConfigSetBody modalConfigSetBody_" + this.displayLayoutId;
		fieldset.appendChild(editLayoutTextareaDiv);
		
		var editLayoutTextarea;
		var detailDiv = document.createElement("div");
		detailDiv.style.width = "99%";
		detailDiv.style.margin = "10px";
		detailDiv.style.marginBottom = "10px";
		detailDiv.style.fontSize = "14px";
		detailDiv.innerHTML = ISA_R["alb_"+layoutName+"_desc"];
		switch (String(layoutName).toLowerCase()) {
			case "title":
				editLayoutTextareaDiv.appendChild(detailDiv);

				editLayoutTextarea = document.createElement("input");
				editLayoutTextarea.style.width = "99%";
				editLayoutTextarea.style.margin = "5px";
				editLayoutTextarea.type = "text";
				break;
			case "customtheme":
				detailDiv.style.textAlign = "left"
				detailDiv.style.marginBottom = "0";

				var descDiv = document.createElement("div");
				descDiv.className = "customtheme-desc";
				var seeSampleA = document.createElement("a");
				seeSampleA.className = "customtheme-see-sample";
				seeSampleA.innerHTML = ISA_R.alb_settingExamples;
				seeSampleA.href = "#"
				editLayoutTextareaDiv.appendChild(descDiv);
				descDiv.appendChild(detailDiv);
				descDiv.appendChild(seeSampleA);
				
				editLayoutTextarea = document.createElement("textarea");
				editLayoutTextarea.className = "customtheme-textarea";
				editLayoutTextarea.rows = "20";
				editLayoutTextarea.setAttribute('wrap', 'off');

				IS_Event.observe(seeSampleA, 'click', this.openViewerOfThemeSamples.bind(this), false, "_adminPortal");
				break;
			case 'logo':
				editLayoutTextarea = document.createElement("div");
				editLayoutTextarea.style.width = "99%";
				editLayoutTextarea.style.margin = "10px";

				detailDiv.style.margin = "0";
				detailDiv.style.marginBottom= "20px";
				detailDiv.innerHTML = detailDiv.innerHTML + ISA_R.alb_logo_desc2
				editLayoutTextarea.appendChild(detailDiv);

				var form = document.createElement("form");
				form.id = "upload-logo-form";
				form.enctype = "multipart/form-data";
				form.action = hostPrefix + "/logosrv/postPortalLogo";
				form.target = "upLoadDummyFrame";
				form.method = "POST";

				var logoImage = document.createElement("img");
				logoImage.id = "logo-image";
				logoImage.src = logoImgSrc+'?'+(new Date()).getTime();
				logoImage.style.height = "26px";
				logoImage.style.maxWidth = "200px";
				logoImage.style.verticalAlign = "middle";
				logoImage.style.marginLeft = "20px";
				logoImage.style.marginRight = "20px";

				var fileInput = document.createElement("input");
				fileInput.id = 'logo-image-input'
				fileInput.style.fontSize = '13px';
				fileInput.name = "data";
				fileInput.type = "file";
				fileInput.accept="image/gif,image/jpeg,image/png"
				form.appendChild(logoImage);
				form.appendChild(fileInput);
				editLayoutTextarea.appendChild(form);
				IS_Event.observe(fileInput, 'change', this.setLogoImage.bind(fileInput, '^(image\\/(jpeg|png|gif))'), false, "_adminPortal");
				break;
			case 'favicon':
				editLayoutTextarea = document.createElement("div");
				editLayoutTextarea.style.width = "99%";
				editLayoutTextarea.style.margin = "10px";

				detailDiv.style.margin = "0";
				detailDiv.style.marginBottom= "20px";
				detailDiv.innerHTML = detailDiv.innerHTML + ISA_R.alb_favicon_desc2
				editLayoutTextarea.appendChild(detailDiv);

				var form = document.createElement("form");
				form.id = "upload-logo-form";
				form.enctype = "multipart/form-data";
				form.action = hostPrefix + "/logosrv/postFavicon";
				form.target = "upLoadFaviconDummyFrame";
				form.method = "POST";

				var logoImage = document.createElement("img");
				logoImage.id = "logo-image";
				logoImage.src = faviconSrc+'?'+(new Date()).getTime();
				logoImage.style.height = "64px";
				logoImage.style.maxWidth = "64px";
				logoImage.style.verticalAlign = "middle";
				logoImage.style.marginLeft = "20px";
				logoImage.style.marginRight = "20px";

				var fileInput = document.createElement("input");
				fileInput.id = 'logo-image-input'
				fileInput.style.fontSize = '13px';
				fileInput.name = "data";
				fileInput.type = "file";
				fileInput.accept="image/vnd.microsoft.icon,image/x-icon,image/gif,image/png"
				form.appendChild(logoImage);
				form.appendChild(fileInput);
				editLayoutTextarea.appendChild(form);
				IS_Event.observe(fileInput, 'change', this.setLogoImage.bind(fileInput, "^(image\\/(png|gif|x-icon|vnd\\.microsoft\\.icon))"), false, "_adminPortal");
				break;
			default:
				editLayoutTextareaDiv.appendChild(detailDiv);

				editLayoutTextarea = document.createElement("textarea");
				editLayoutTextarea.rows = "20";
				editLayoutTextarea.style.width = "99%";
				editLayoutTextarea.style.margin = "5px";
				editLayoutTextarea.setAttribute('wrap', 'off');
				break;
		}
		editLayoutTextarea.id = "portalLayoutTextarea";
		editLayoutTextareaDiv.appendChild(editLayoutTextarea);
		var changeLayoutHandler = function(e) {
			ISA_Admin.isUpdated = true;
			ISA_PortalLayout.portalLayoutList[self.displayLayoutId].layout = editLayoutTextarea.value;
		};
		IS_Event.observe(editLayoutTextarea, 'change', changeLayoutHandler, false, "_adminPortal");

		return editLayoutDiv;
	}
	
	this.openViewerOfThemeSamples = function(){
		this.popupWindow = window.open("../../manager/portallayout/themeSamples", "themeSamples", "width=800, height=600, scrollbars=yes, status=no");
		this.popupWindow.focus();
	}
	
	this.commitPortalLayout = function() {
		var portalLayouts = {};
		for(var i in ISA_PortalLayout.portalLayoutList) {
			if( (ISA_PortalLayout.portalLayoutList[i] instanceof Function) ) continue;
			// Obtain value
			var layout = ISA_Admin.trim(ISA_PortalLayout.portalLayoutList[i].layout);
			// Check with input

			// PostData
			var name = ISA_PortalLayout.portalLayoutList[i].name;
			ISA_Admin.isUpdated = false;
			isUpdatedLogoImage = false;
			portalLayouts[ name ] = layout;
		}
		
		// Update to DB
		if(!controlModal){
			controlModal = new Control.Modal('',{
				className:"commitDialog",
				closeOnClick:false
			});		
		}
		controlModal.container.update(ISA_R.ams_applyingChanges);
		controlModal.open();
		this.updatePortalLayout(portalLayouts);
	}
	
	this.updatePortalLayout = function(portalLayouts) {
		var url = adminHostPrefix + "/services/portalLayout/updatePortalLayout";
		delete portalLayouts['logo'];
		delete portalLayouts['favicon'];
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			postBody: Object.toJSON([portalLayouts]),
			asynchronous:true,
			onSuccess: function(response){
				var form = $jq('#upload-logo-form');
		        if(window.is_squareId){
		            var orgAction = form.attr("action");
		            form.attr("action", orgAction + "?MSDPortal-SquareId="+is_squareId);
		        }
				var input = form.children('input');
				if(form.length != 0 && !input.attr('upload')) {
					form.submit();
				} else {
					setTimeout(function(){
						Control.Modal.close();
					},500);
				}
				controlModal.container.update(ISA_R.ams_changeUpdated);
			},
			onFailure: function(t) {
				var errormsg = t.responseText && typeof t.responseText == "string" ? t.responseText.substr(0, 100) : "";
				alert(ISA_R.ams_failedUpdateOtherPotal+'\n' + errormsg );
				msg.error(ISA_R.ams_failedUpdateOtherPotal + t.status + " - " +" | " + t.statusText + errormsg );
				setTimeout(function(){
					Control.Modal.close();
				},500);
			},
			onException: function(r, t){
				msg.error(ISA_R.ams_failedUpdateOtherPotal + getErrorMessage(t));
				setTimeout(function(){
					Control.Modal.close();
				},500);
			}
		};
		AjaxRequest.invoke(url, opt);
	}
	
	this.build = function() {
		var url = adminHostPrefix + "/services/portalLayout/getPortalLayoutJson";
		var opt = {
			method: 'get' ,
			asynchronous:true,
			onSuccess: function(response){
				eval(response.responseText);
				self.displayPortalLayout();
			},
			onFailure: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedToloadPortalLayout+"</span>";
				msg.error(ISA_R.ams_failedToloadPortalLayout + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedToloadPortalLayout+"</span>";
				msg.error(ISA_R.ams_failedToloadPortalLayout + getErrorMessage(t));
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

	/**
	Set and Preview logo
	*/
	this.setLogoImage = function(expr) {
		$jq('#logo-image-input').removeAttr('upload');
		var regExp = new RegExp(expr);

		// set
		var file = this.files[0];
		if(!file.type.match(regExp)) {
			alert(ISA_R.ams_differentFileFormat);
			$jq(this).val('');
		}

		// preview
		if(!window.File || !window.FileReader || !this.files || !this.files.length) return;
		var file = this.files[0];
		if(file.type.match(regExp)) {
			var imageEle = $jq("#logo-image");
			var fileReader = new FileReader();
			fileReader.onload = function(e) {
				imageEle.attr('src', e.target.result);
			}
			fileReader.readAsDataURL(file);
		}

		isUpdatedLogoImage = true;
	}
};
