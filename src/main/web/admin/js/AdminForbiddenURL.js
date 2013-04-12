
ISA_PortalForbiddenURL = Class.create();
ISA_PortalForbiddenURL.prototype = {
	initialize: function() {
		this.temp = 0;
		this.controlModal;		// Apply Change dialog
	},
	createUrlInput: function(forbiddenUrl){
		var urlId = forbiddenUrl.id;
		
		var urlInput = document.createElement("input");
		urlInput.id = "forbiddenURL_"+urlId+"_url";
		urlInput.value = forbiddenUrl.url;
		urlInput.style.width = "100%";
		urlInput.className = "portalAdminInput";
		IS_Event.observe( urlInput,"change",function(){
			ISA_Admin.isUpdated = true;
		},true,"_adminForbiddenURL");
		IS_Event.observe( urlInput,"focus",	this.handleUrlInputFocus.bind( this,urlId ),false,"_adminForbiddenURL");
		IS_Event.observe( urlInput,"blur",this.handleUrlInputBlur.bind( this,urlId ),false,"_adminForbiddenURL");
		
		return urlInput;
	},
	createUrlRow: function(forbiddenUrl){
		var urlId = forbiddenUrl.id;
		
		var tr = document.createElement("tr");
		tr.forbiddenURL_ID = urlId;
		
		tr.appendChild(document.createElement("td"))
		tr.lastChild.style.padding = "3px";
		var urlInput = this.createUrlInput(forbiddenUrl);
		tr.lastChild.className = "configTableTd";
		tr.lastChild.appendChild( urlInput );
		
		tr.appendChild(document.createElement("td"));
		var removeForbiddenURLButton =
			$.IMG({
				id:"removeForbiddenURLButton_" + urlId,
				className: "deleteButton",
				src:"../../skin/imgs/trash.gif", 
				title:ISA_R.alb_delete,
				style: "cursor: pointer;"});
		tr.lastChild.className = "configTableTd";
		tr.lastChild.style.padding = "3px";
		tr.lastChild.style.textAlign = 'center';
		tr.lastChild.appendChild(removeForbiddenURLButton);
		
		return tr;
	},
	handleUrlInputFocus: function( urlId ) {

		var urlInput = $("forbiddenURL_"+urlId+"_url");
		urlInput.value = this.forbiddenURLs.get(urlId).url;
		urlInput.className = "portalAdminInput";
	},
	handleUrlInputBlur: function( urlId ) {
		var urlInput = $("forbiddenURL_"+urlId+"_url");
		if( !urlInput ) return;
		
		var newUrl = ISA_Admin.trim( urlInput.value );
		
		this.validate( urlId,ISA_Admin.trim( urlInput.value ) );
		this.forbiddenURLs.get(urlId).url = newUrl;
	},
	validate: function( urlId,newUrl ) {
		var urlInput = $("forbiddenURL_"+urlId+"_url");
		
		var error = false;
		if( newUrl.length == 0 ) {
			error = ISA_R.ams_blankRegularExpression;
		} else if(error = IS_Validator.validate(newUrl, {maxBytes:1024, format:'regexp'})){
		} else if( this.forbiddenURLs.detect( function(entry){
				return entry.value.id != urlId && entry.value.url == newUrl;
			})) {
			error = ISA_R.ams_regularExpressionAlreadyReg;
		}
		
		if (error) {
			urlInput.value = error + ": " + newUrl;
			urlInput.className = "i18nInputError portalAdminInput";
		}
		
		var isError = !(!( error ));
		this.forbiddenURLs.get(urlId).error = isError;
		
		return isError;
	},
	addForbiddenURL: function() {
		ISA_Admin.isUpdated = true;
		var table = $("forbiddenURL_contentTable");
		if( !table ) return;
		var tbody = table.firstChild;
		if( !tbody ) return;
		
		var id = "temp_"+( this.temp++ );
		var forbiddenUrl = {
			id: id,
			url: "^http[s]?://"
		};
		this.forbiddenURLs.set(id, forbiddenUrl);
		
		var tr = this.createUrlRow( forbiddenUrl );
		if (tbody.childNodes.length > 1) {
			tbody.insertBefore(tr, tbody.childNodes[1]);
		} else {
			tbody.appendChild(tr);
		}
		this.validate( id,forbiddenUrl.url );
	},
	removeForbiddenURL: function( event ) {
		var source = Event.element( event );
		while( source && source.className != "deleteButton")
			source = source.parentNode;
		
		if( !source || source.id.indexOf("removeForbiddenURLButton_") != 0 ) return;
		ISA_Admin.isUpdated = true;
		var tr = source.parentNode.parentNode;
		tr.parentNode.removeChild( tr );
		
		var id = source.id.substring("removeForbiddenURLButton_".length );
		this.forbiddenURLs.unset(id);
		
	},
	updateForbiddenURLs: function() {
		if( this.forbiddenURLs.values().find( function( forbiddenUrl ) {
			return this.validate( forbiddenUrl.id,forbiddenUrl.url );
		}.bind( this ) ) ) {
			alert(ISA_R.ams_changeCanNotApplied);
			return;
		}
		
		if(!this.controlModal){
			this.controlModal = new Control.Modal('',{
				className:"commitDialog",
				closeOnClick:false
			});
		}
		this.controlModal.container.update(ISA_R.ams_applyingChanges);
		this.controlModal.open();

		var url = adminHostPrefix + "/services/forbiddenUrls/updateForbiddenURLs";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			asynchronous:true,
			postBody: Object.toJSON([this.forbiddenURLs.toObject()]),
			onSuccess: function( resp ) {
				ISA_Admin.isUpdated = false;
				this.controlModal.container.update(ISA_R.ams_changeUpdated);
				setTimeout( function() {
					Control.Modal.close();
				},500 );
				
				this.build();
			}.bind( this ),
			onFailure: function(r, t) {
				alert(ISA_R.ams_failedToUpdateFURL);
				msg.error(ISA_R.ams_failedToUpdateFURL + t.status + " - " + t.statusText);
			},
			onException: function(r,t){
				alert(ISA_R.ams_failedToUpdateFURL);
				msg.error(ISA_R.ams_failedToUpdateFURL + getErrorMessage(t));
			},
			onComplete: function(){
				Control.Modal.close();
			}
		};
		AjaxRequest.invoke(url, opt);
	},
	build: function() {
		if(!ISA_Admin.checkUpdated())return;
		container = $("forbiddenURL");
		
		while (container.hasChildNodes())
			container.removeChild(container.firstChild);
		
		loadingMessage = document.createElement('div');
		loadingMessage.innerHTML = "Loading...";
		loadingMessage.style.clear = "both";
		loadingMessage.style.cssFloat = "left";
		container.appendChild(loadingMessage);
		
		var url = adminHostPrefix + "/services/forbiddenUrls/getForbiddenURLsJSON";
		var opt = {
			method: 'get' ,
			asynchronous:true,
			onSuccess: function( resp ) {
				this.forbiddenURLs = $H( eval("["+resp.responseText+"]")[0] );
				this.forbiddenURLs.values().each( function( forbiddenURL ) {
					forbiddenURL.url = forbiddenURL.url;
				});
				this.display();
			}.bind( this ),
			onComplete: function(){
				
			},
			onFailure: function(t) {
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedToGetFURL+"</span>";
				msg.error(ISA_R.ams_failedToGetFURL + t.status + " - " + t.statusText);
			},
			onException: function(r,t){
				if(!container.firstChild) container.appendChild(document.createElement("div"));
				container.firstChild.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>"+ISA_R.ams_failedToGetFURL+"</span>";
				msg.error(ISA_R.ams_failedToGetFURL + getErrorMessage(t));
			}
		};
		AjaxRequest.invoke(url, opt);
	},
	
	display: function( json ) {
		IS_Event.unloadCache("_adminForbiddenURL");
		
		var container = document.getElementById("forbiddenURL");
		container.removeChild( container.firstChild );
		
		this.displayHeader( container );
		
		var table = ISA_Admin.buildTableHeader(
			[ISA_R.alb_URLregularExpression, ISA_R.alb_delete],
			['85%', '5%']
			);
		table.id = "forbiddenURL_contentTable";
		container.appendChild( table );
		table.className = "configTableHeader";
		table.style.tableLayout = "fixed";
		table.style.clear ="both";
		//TODO:Function for generating table needs to be arranged
		table.style.borderLeft = "1px solid #EEEEEE";
		table.style.width = "90%";
		
		var tbody = table.firstChild;
		var this_ = this;
		this.forbiddenURLs.values().each( function( forbiddenURL ) {
			tbody.appendChild( this_.createUrlRow( forbiddenURL ));
		});
		
		IS_Event.observe( table,"click",this.removeForbiddenURL.bind( this ),true,"_adminForbiddenURL" );
	},
	displayHeader: function(container){
		var controlDiv = document.createElement("div");
		controlDiv.className ='refreshAll';
		
		var commitDiv = ISA_Admin.createIconButton(ISA_R.alb_changeApply, ISA_R.alb_changeApply, "database_save.gif", "right");
		controlDiv.appendChild(commitDiv);
		IS_Event.observe(commitDiv, "click", this.updateForbiddenURLs.bind(this), false, "_adminForbiddenURL");
		
		var refreshDiv = ISA_Admin.createIconButton(ISA_R.alb_refresh, ISA_R.alb_reloadWithourSaving, "refresh.gif", "right");
		controlDiv.appendChild(refreshDiv);
		IS_Event.observe(refreshDiv, "click", this.build.bind(this), false, "_adminForbiddenURL");
		
		container.appendChild(controlDiv);
		
		var addButton = ISA_Admin.createIconButton(ISA_R.alb_add, ISA_R.alb_add, "add.gif", "left");
		addButton.style.textAlign = "left"
		
		IS_Event.observe(addButton, "click", this.addForbiddenURL.bind(this), false, "_adminForbiddenURL");
		
		container.appendChild(addButton);
	}
	
}
