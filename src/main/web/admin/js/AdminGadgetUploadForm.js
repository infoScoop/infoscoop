ISA_Admin.buildInputBundleForm = function( type,isaWidgetConf ) {
	if(/^upload__(.+)/.test( type ))
		type = RegExp.$1;
	
	var eventId = isaWidgetConf.id+"_gadget";
	
	var inputBundleForm = document.createElement("div");
	inputBundleForm.className = "gadgetResources";
	
	var uploadFieldSet = document.createElement("div");
	uploadFieldSet.className = "configSet"
	inputBundleForm.appendChild( uploadFieldSet)
	
	var uploadFieldSetLabel = document.createElement("p");
	uploadFieldSetLabel.className = "configSetHeader";
	uploadFieldSetLabel.innerHTML = ISA_R.alb_uploadGadget;
	uploadFieldSet.appendChild( uploadFieldSetLabel );
	
	var descriptions = $( document.createElement("div") );
	descriptions.className = "upload-description";
	uploadFieldSet.appendChild( descriptions );
	
	var desc1 = $(document.createElement("div"));
	if( type ) {
		desc1.innerHTML = ISA_R.alb_gadgetResourcesUpdateDescription;
	} else {
		desc1.innerHTML = ISA_R.alb_gadgetResourcesCreateDescription;
	}
	descriptions.appendChild( desc1 );
	
	var desc2 = $( document.createElement("div") );
	desc2.setStyle({fontSize:"90%"});
	desc2.innerHTML =  ISA_R.alb_gadgetResourcesZipDescription;
	descriptions.appendChild( desc2 );
	
	uploadFieldSet.appendChild( ISA_GadgetUpload.buildForm( {
		parameters: {
			type: type || "",
			mode: "module"
		},
		eventId: eventId,
		handleUploadStart: function() {},
		handleUploadEnd: function( iframe ){
			var message = iframe.contentWindow.document.body.innerHTML;
			if( !/^\s*success\s*$/i.test( message ))
				return ISA_GadgetUpload.error( message );
			
			isaWidgetConf.buildGadgetConfs( function() {
				var uploadType = "upload__" +iframe.contentWindow.document.body.getAttribute("type");
				
				isaWidgetConf.displayEditWidgetConf( uploadType );
			});
		}.bind( this ),
		confirm: !!type
	} ));
	
	if( type ) {
		var resourcesFieldSet = document.createElement("div");
		resourcesFieldSet.className = "configSet";
		inputBundleForm.appendChild( resourcesFieldSet );
		
		var resourcesFieldSetLabel = document.createElement("p");
		resourcesFieldSetLabel.className = "configSetHeader";
		resourcesFieldSetLabel.innerHTML = ISA_R.alb_gadgetResources;
		resourcesFieldSet.appendChild( resourcesFieldSetLabel );
		
		var gadgetResourcesContainer = document.createElement("div");
		gadgetResourcesContainer.style.margin = "3px";
		resourcesFieldSet.appendChild( gadgetResourcesContainer );
		
		new ISA_GadgetResources( type,isaWidgetConf,eventId ).load( gadgetResourcesContainer );
	}
	
	var commandDiv = document.createElement("div");
	commandDiv.style.textAlign = "center";
	inputBundleForm.appendChild( commandDiv );
	
	var cancelButton = document.createElement('input');
	cancelButton.type = "button";
	cancelButton.value = ISA_R.alb_cancel;
	commandDiv.appendChild(cancelButton);
	IS_Event.observe(cancelButton, 'click',function(){
		isaWidgetConf.cancelEditWidgetConf();
		Element.hide('widgetConfGadgetUpload');
		
		isaWidgetConf.displayEditWidgetConf("upload__"+type );
	}.bind( this ), false, eventId);
	
	return inputBundleForm;
}
ISA_GadgetUpload = Class.create();
ISA_GadgetUpload.buildForm = function( opt ) {
	var form = document.createElement("form");
	form.method = "POST";
	form.encoding = "multipart/form-data";
	form.action = adminHostPrefix + "/uploadgadget";
	form.target = "upLoadDummyFrame";
	form.style.margin = "0.25em";
	
	var fileInput = document.createElement("input");
	fileInput.type = "file";
	fileInput.name = "data";
	form.appendChild( fileInput );
	
	for( var i in opt.parameters ) if( opt.parameters.hasOwnProperty( i )) {
		var input = document.createElement("input");
		input.name = i;
		input.type = 'hidden';
		input.value = opt.parameters[i];
		
		form.appendChild( input );
	}
	
	if( opt.mode != "resource") {
		var submit = document.createElement("input");
		submit.type = "submit";
		submit.value = ISA_R.alb_upload;
		form.appendChild( submit );
	}
	
	var iframe = $("upLoadDummyFrame");
	Event.observe( form,"submit",function() {
		if( opt.confirm && !confirm( ISA_R.ams_gadgetResourceUploadConfirm ) )
			return;
		
		var started = false;
		var startTimeout = setTimeout( function() {
			opt.handleUploadStart();
			started = true;
		},0 );
		
		Event.observe( iframe,"load",function() {
			clearTimeout( startTimeout );
			if( !started ) opt.handleUploadStart();
			
			opt.handleUploadEnd( iframe );
			
			Event.stopObserving( iframe,"load" );
		} );
		
		return true;
	});
	
	return form;
}
ISA_GadgetUpload.error = function( message ) {
	try {
		var exception = eval("["+message+"]")[0];
		var msgs = [ ISA_R.ams_gadgetResourceUploadFailed ];
		msgs.push( ISA_R[ exception.message ] );
		
		if( exception.cause && exception.path && exception.name ) {
			msgs.push("");
			msgs.push("Path: "+exception.path );
			msgs.push("Name: "+exception.name );
			msgs.push( ISA_R[ exception.cause ] );
		}
		
		message = msgs.join("\n");
		alert( message );
	} catch( ex ) {
		alert( ISA_R.ams_gadgetResourceUploadFailed+": "+message );
	}
	msg.error( message )
}
ISA_GadgetResource = Class.create();
ISA_GadgetResource.prototype = {
	initialize: function( type,r ) {
		this.type = type;
		this.path = r.path;
		this.name = r.name;
		this.resources = r.resources;
	},
	get: function( opt ) {
		var url = adminHostPrefix + "/services/gadgetResource/selectResource";
		
		AjaxRequest.invoke( url, Object.extend( opt || {},{
			method: 'post' ,
			contentType: "application/json",
			asynchronous: true,
			postBody: Object.toJSON([this.type,this.path,this.name,"UTF-8"])
		}) );
	},
	create: function( opt ) {
		var url = adminHostPrefix + "/services/gadgetResource/insertResource";
		
		AjaxRequest.invoke( url,Object.extend( opt || {},{
			method: 'post' ,
			contentType: "application/json",
			asynchronous: true,
			postBody: Object.toJSON([this.type,this.path,this.name])
		}) );
	},
	update: function( data,opt ) {
		var url = adminHostPrefix + "/services/gadgetResource/updateTextResource";
		
		AjaxRequest.invoke( url, Object.extend( opt || {},{
			method: 'post' ,
			contentType: "application/json",
			asynchronous: true,
			postBody: Object.toJSON([this.type,this.path,this.name,data])
		}));
	},
	remove: function( opt ) {
		var url = adminHostPrefix + "/services/gadgetResource/deleteResource";
		
		AjaxRequest.invoke( url, Object.extend( opt || {},{
			method: 'post' ,
			contentType: "application/json",
			asynchronous: true,
			postBody: Object.toJSON([this.type,this.path,this.name])
		}));
	},
	getAbsolutePath: function() {
		return this.type
				+( this.path == "/" ? "/" : this.path+"/")
				+( this.resources ? "":this.name );
	},
	isText: function() {
		return new RegExp("\.("+[
			"txt",
			"json",
			"xml",
			"html",
			"xhtml",
			"js",
			"css"
		].join("|")+")$").test( this.name );
	},
	isImage: function() {
		return new RegExp("\.("+[
			"gif",
			"jpg",
			"jpeg",
			"png"
		].join("|")+")$").test( this.name );
	},
	isGadget: function() {
		return this.name == this.type+".xml" && this.path == "/";
	},
	isDirectory: function() {
		return !!this.resources;
	}
}
ISA_GadgetResource.list = function( type,opt ) {
	var url = adminHostPrefix + "/services/gadgetResource/getResourceListJson";
	
	AjaxRequest.invoke(url, Object.extend( opt || {},{
		method: 'post' ,
		contentType: "application/json",
		postBody: Object.toJSON([type])
	}));
}

ISA_GadgetResources = Class.create();
ISA_GadgetResources.prototype = {
	initialize: function( type,isaWidgetConf,eventId ) {
		this.type = type;
		this.isaWidgetConf = isaWidgetConf;
		this.eventId = eventId;
	},
	load: function( container ) {
		if( !container || !container.nodeName ) {
			container = this.container;
		} else {
			this.container = container;
		}
		
		while( container.firstChild )
			container.removeChild( container.firstChild );
		
		this.renderZipDownload( container );
		
		ISA_GadgetResource.list( this.type,{
			onSuccess: function( response ) {
				var resources = eval( response.responseText );
				
				new ISA_GadgetResourceTreeRenderer( this,this.eventId ).renderResource( container,
					this.makeResourceTree( this.type,resources ));
			}.bind( this ),
			onFailure: function( resp ) {
				alert( ISA_R.ams_gadgetResourceListFailed+"\n"+resp.responseText );
				msg.error( ISA_R.ams_gadgetResourceListFailed+"\n"+resp.responseText );
			},
			onException: function( resp,ex ) {
				alert( ISA_R.ams_gadgetResourceListFailed );
				msg.error( ISA_R.ams_gadgetResourceListFailed +getErrorMessage( ex ));
			}
		});
	},
	renderZipDownload: function( container ) {
		var layout = $( document.createElement("div"));
		layout.setStyle({ textAlign: "right"});
		var link = document.createElement("a");
		link.className = "button download";
		link.href = adminHostPrefix + "/uploadgadget?"+$H({
			type: this.type
		}).toQueryString();
		link.title = ISA_R.alb_gadgetResourcesZipDownloadDescription;
		link.appendChild( document.createTextNode( ISA_R.alb_gadgetResourcesZipDownload ));
		layout.appendChild( link );
		
		container.appendChild( layout );
	},
	makeResourceTree: function( type,resources ) {
		var root = new ISA_GadgetResource( type,{
			name: type,
			path: "/",
			resources: []
		});
		for( var i=0;i<resources.length;i++ ) {
			var resource = resources[i];
			
			var path = resource.path.split("/");
			var p = "";
			var directory = root;
			for( var j=0;j<path.length;j++ ) {
				var pathSegment = path[j];
				if( !pathSegment ) continue;
				
				p += "/"+pathSegment;
				
				var pathDirectory = directory.resources.find( function( resource ) {
					return resource.name == pathSegment;
				});
				if( !pathDirectory ) {
					directory.resources.push( ( pathDirectory = new ISA_GadgetResource( type,{
						name: pathSegment,
						path: p,
						resources: []
					}) ) );
				}
				
				directory = pathDirectory;
			}
			
			if( resource.name && resource.name != "" )
				directory.resources.push( new ISA_GadgetResource( type,resource ) );
		}
		
		return root;
	},
	reloadGadgetConf: function( hage ) {
			var t = "upload__"+this.type;
		var isaWidgetConf = this.isaWidgetConf;
		isaWidgetConf.buildGadgetConfs( function() {
			isaWidgetConf.displayEditWidgetConf( t );
			!hage && isaWidgetConf._updateGadget( t );
		});
	},
	handleResourceUpload: function( renderer,resource,create ) {
		var modalContent = document.createElement("div");
		modalContent.className = "gadgetResources";
		var editPanel = new ISA_GadgetResourceModalRenderer( {
			handleUploadStart: function() {
				this.createSimpleModal( ISA_R.ams_gadgetResourceUploading,'overlay' );
			}.bind( this ),
			handleUploadEnd: function( iframe ) {
				Control.Modal.close();
				
				var message = iframe.contentWindow.document.body.innerHTML;
				if( !/^\s*success\s*$/i.test( message ))
					return ISA_GadgetUpload.error( message );
				
				if( resource.isGadget() ) {
					this.reloadGadgetConf( true );
				} else if( create ) {
					this.load();
				}
			}.bind( this ),
			handleOk: function( upload ) {
				upload.uploader.submit();
			}.bind( this ),
			handleCancel: function() { Control.Modal.close(); }.bind( this )
		},resource,this.eventId );
		editPanel.renderUpload( modalContent,create );
		
		this.createSimpleModal( modalContent,'overlay' );
	},
	handleResourceEdit: function( renderer,resource ) {
		var modalContent = document.createElement("div");
		modalContent.className = "gadgetResources";
		var editPanel = new ISA_GadgetResourceModalRenderer( {
			handleReload: this.handleResourceEditModalReload.bind( this ),
			handleOk: this.handleResourceEditModalSave.bind( this ),
			handleCancel: function() { Control.Modal.close(); }.bind( this )
		},resource,this.eventId );
		
		editPanel.renderEdit( modalContent );
		
		this.createSimpleModal( modalContent,'overlay' );
	},
	handleResourceDelete: function( renderer,resource ) {
		if( !confirm( ISA_R.getResource( ISA_R.ams_gadgetResourceDeleteConfirm,[resource.getAbsolutePath()])) )
			return;
		
		this.createSimpleModal( ISA_R.ams_gadgetResourceDeleting,false );
		
		resource.remove({
			onSuccess: function( response ) {
				modal.close();
				
				this.reloadGadgetConf();
			}.bind( this ),
			onFailure: function( response ) {
				modal.close();
				
				alert( ISA_R.ams_gadgetResourceDeleteFailed +"\n"+response.responseText );
				msg.error( ISA_R.ams_gadgetResourceDeleteFailed +"\n"+response.responseText );
			},
			onException: function( resp,ex ) {
				modal.close();
				
				alert( ISA_R.ams_gadgetResourceDeleteFailed );
				msg.error( ISA_R.ams_gadgetResourceDeleteFailed +getErrorMessage( ex ));
			}
		});
	},
	handleResourceEditModalSave: function( renderer,resource ) {
		var value = renderer.textarea.value;
		this.createSimpleModal( ISA_R.ams_gadgetResourceUpdating,false );
		
		resource.update( value,{
			onSuccess: function( response ) {
				Control.Modal.close();
				
				this.reloadGadgetConf();
			}.bind( this ),
			onFailure: function( response ) {
				Control.Modal.close();
				
				alert( ISA_R.ams_gadgetResourceUpdateFailed +"\n"+response.responseText );
				msg.error( ISA_R.ams_gadgetResourceUpdateFailed +"\n"+response.responseText);
			},
			onException: function( resp,ex ) {
				modal.close();
				
				alert( ISA_R.ams_gadgetResourceUpdateFailed );
				msg.error( ISA_R.ams_gadgetResourceUpdateFailed +getErrorMessage( ex ));
			}
		} );
	},
	handleResourceEditModalReload: function( renderer,resource ) {
		resource.get( {
			onSuccess: function( response ) {
				renderer.textarea.value = response.responseText;
			}.bind( this ),
			onFailure: function( response ) {
				alert( ISA_R.ams_gadgetResourceFetchFailed +"\n"+response.responseText);
				msg.error( ISA_R.ams_gadgetResourceFetchFailed +"\n"+response.responseText);
			},
			onException: function( resp,ex ) {
				alert( ISA_R.ams_gadgetResourceFetchFailed );
				msg.error( ISA_R.ams_gadgetResourceFetchFailed +getErrorMessage( ex ));
			}
		});
	},
	createSimpleModal: function( content,closable ) {
		if( !this.modal ){
			this.modal = new Control.Modal( '',{
				closeOnClick: false,
				afterClose: function(){
					this.destroy();
				}
			});			
		}
		this.modal.options.closeOnClick = closable;
		this.modal.container.update(content);
		this.modal.position();
		this.modal.open();
	}
}

ISA_GadgetResourceTreeRenderer = Class.create();
ISA_GadgetResourceTreeRenderer.prototype = {
	initialize: function( controller,eventId ) {
		this.controller = controller;
		this.eventId = eventId;
	},
	renderResource: function( container,resource ) {
		var item;
		if(/ul|ol/i.test( container.tagName) ) {
			item = $( document.createElement("li"));
			container.appendChild( item );
		} else {
			item = container;
		}
		
		var itemDiv = $( document.createElement("div"));
		itemDiv.className = "resource";
		item.appendChild( itemDiv );
		
		var commands = $( document.createElement("div"));
		commands.className = "commands";
		itemDiv.appendChild( commands );
		
		if( resource.resources ) {
			this.renderDirectoryCommands( commands,resource );
		} else {
			this.renderResourceCommands( commands,resource );
		}
		
		if( resource.resources ) {
			var lb = $( document.createElement("span"));
			lb.className = "directory";
			lb.appendChild( document.createTextNode( resource.name ));
			itemDiv.appendChild( lb );
		} else {
			var lb = $( document.createElement( "span" ));
			lb.className = "file";
			lb.appendChild( document.createTextNode( resource.name ));
			itemDiv.appendChild( lb );
			
			if( resource.isImage() ) {
				lb.className += " image";
			} else if( resource.isGadget() ) {
				lb.className += " gadget";
			} else if( resource.isText() ){
				lb.className += " text";
			} else {
				lb.className += " text";
			}
		}
		
		if( resource.resources ) {
			this.renderResources( item,resource );
		}
	},
	renderDirectoryCommands: function( container,resource ) {
		var addButton = $( document.createElement("a"));
		addButton.className = "button add";
		addButton.appendChild( document.createTextNode( ISA_R.alb_gadgetResourceCreate ));
		addButton.title = ISA_R.alb_gadgetResourceCreateDescrption;
		Event.observe( addButton,"click",
			this.controller.handleResourceUpload.bind( this.controller,this,resource,true ));
		container.appendChild( addButton );
	},
	renderResourceCommands: function( container,resource ) {
		if( resource.isText()) {
			var updateButton = $( document.createElement("a"));
			updateButton.className = "button update";
			updateButton.appendChild( document.createTextNode( ISA_R.alb_edit ));
			updateButton.title = ISA_R.alb_gadgetResourceEditDescription;
			Event.observe( updateButton,"click",
				this.controller.handleResourceEdit.bind( this.controller,this,resource ),false,this.eventId );
			container.appendChild( updateButton );
		}
		
		var uploadButton = $( document.createElement("a"));
		uploadButton.className = "button upload";
		uploadButton.appendChild( document.createTextNode( ISA_R.alb_upload ));
		Event.observe( uploadButton,"click",
			this.controller.handleResourceUpload.bind( this.controller,this,resource ),false,this.eventId );
		container.appendChild( uploadButton );
		
		if( !resource.isGadget() ) {
			var deleteButton = $( document.createElement("a"));
			deleteButton.className = "button delete";
			deleteButton.title = ISA_R.alb_gadgetResourceDeleteDescription;
			deleteButton.appendChild( document.createTextNode( ISA_R.alb_delete ));
			Event.observe( deleteButton,"click",
				this.controller.handleResourceDelete.bind( this.controller,this,resource ),false,this.eventId );
			container.appendChild( deleteButton );
		}
	},
	renderResources: function( container,resource ) {
		var list = $( document.createElement("ul") );
		container.appendChild( list );
		
		for( var i=0;i<resource.resources.length;i++ ) {
			this.renderResource( list,resource.resources[i] );
		}
	}
}
ISA_GadgetResourceModalRenderer = Class.create();
ISA_GadgetResourceModalRenderer.prototype = {
	initialize: function( controller,resource,eventId ) {
		this.controller = controller;
		this.resource = resource;
		this.eventId = eventId;
	},
	renderHeader: function( container ) {
		while( container.firstChild )
			container.removeChild( container.firstChild );
		
		var headerPanel = $( document.createElement("div"));
		headerPanel.className = "header";
		headerPanel.appendChild( document.createTextNode( this.resource.getAbsolutePath() ));
		container.appendChild( headerPanel );
	},
	renderEdit: function( container ) {
		this.renderHeader( container );
		
		var textarea = this.textarea = $( document.createElement("textarea"));
		container.appendChild( textarea );
		
		this.renderOkCancel( container ,{
			ok: { text: ISA_R.alb_saveSettings }
		} );
		
		this.controller.handleReload( this,this.resource );
	},
	renderOkCancel: function( container,opt ) {
		opt = opt || {};
		
		var okCancelPanel = $( document.createElement("div") );
		okCancelPanel.className = "ok-cancel";
		container.appendChild( okCancelPanel );
		
		if( opt.ok ) {
			var okButton = document.createElement("input");
			okButton.type = opt.ok.submit ? "submit" : "button";
			okButton.value = opt.ok.text || "OK";
			okCancelPanel.appendChild( okButton );
			
			Event.observe( okButton,"click",function() {
				this.controller.handleOk( this,this.resource );
			}.bind( this ),false,this.eventId );
		}
		
		var cancelButton = document.createElement("input");
		cancelButton.type = "button";
		cancelButton.value = ISA_R.alb_cancel;
		okCancelPanel.appendChild( cancelButton );
		
		Event.observe( cancelButton,"click",function() {
			this.controller.handleCancel( this,this.resource );
		}.bind( this ),false,this.eventId );
	},
	renderUpload: function( container,doCreate ) {
		this.renderHeader( container );
		
		var uploadPanel = document.createElement("div");
		uploadPanel.className = "uploadPanel";
		container.appendChild( uploadPanel );
		
		var description = document.createElement("div");
		description.className = "description";
		description.appendChild( document.createTextNode( ISA_R.alb_selectUploadingGadget ));
		uploadPanel.appendChild( description );
		
		if( doCreate ) {
			var encodeDesc = document.createElement("div");
			encodeDesc.style.fontSize = "9pt";
			encodeDesc.style.color = "red";
			encodeDesc.appendChild( document.createTextNode(
				ISA_R.alb_gadgetResourceUploadFileEncodeDescription ));
			uploadPanel.appendChild( encodeDesc );
		}
		
		this.uploader = ISA_GadgetUpload.buildForm( {
			parameters: {
				type: this.resource.type,
				path: this.resource.path,
				name: this.resource.name,
				create: doCreate
			},
			mode: "resource",
			eventId: this.eventId,
			handleUploadStart: this.controller.handleUploadStart,
			handleUploadEnd: this.controller.handleUploadEnd
		} );
		
		uploadPanel.appendChild( this.uploader );
		
		this.renderOkCancel( this.uploader,{
			ok: { text: ISA_R.alb_upload,submit: true }
		} );
	}
}