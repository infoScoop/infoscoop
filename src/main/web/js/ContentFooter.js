IS_Portal.ContentFooter = IS_Class.create();
IS_Portal.ContentFooter.prototype.classDef = function() {
	var self = this;
	this.initialize = function( opt ){
		this.id = opt.id;
		
		this.displayState = Object.extend( {
			isDisplay: function() {
				return false;
			},
			getUrl: function() {
				return "";
			},
			getTitle: function() {
				return "";
			}
		},opt );
		
		this.icons = IS_Customization.contentFooter.concat( opt.icons || []);
	}
	
	this.displayContents = function () {
		if( IS_Customization.contentFooter.length == 0 ) {
			this.elm_toolBar = document.createElement("div");
			return;
		}
		
		var toolBarTable = document.createElement("table");
		toolBarTable.setAttribute("width", "100%");
		toolBarTable.cellSpacing = "0";
		toolBarTable.cellPadding = "1";
		toolBarTbody = document.createElement("tbody");
		toolBarTable.appendChild(toolBarTbody);
		toolBarTr = document.createElement("tr");
		toolBarTbody.appendChild(toolBarTr);
		toolBarIconTd = document.createElement("td");
		toolBarIconTd.id = "mti_" + self.id;
		toolBarIconTd.style.align = "left";
		toolBarIconTable = document.createElement("table");
		toolBarIconTable.className = "toolBarTable";
		toolBarIconTable.id = self.id+"_tools";
		toolBarIconTable.style.width = "auto";
		toolBarIconTable.style.marginLeft = "2px";
		toolBarIconTable.cellPadding = "0";
		toolBarIconTable.cellSpacing = "0";
		toolBarIconTbody = document.createElement("tbody");
		toolBarIconTable.appendChild(toolBarIconTbody);
		toolBarIconTr = document.createElement("tr");
		toolBarIconTbody.appendChild(toolBarIconTr);
		toolBarIconTd.appendChild(toolBarIconTable);
		toolBarTr.appendChild(toolBarIconTd);

		this.elm_toolBar = toolBarTable;
		$( this.elm_toolBar ).addClassName("iframetoolbar");
		
		var buildTypes = [];
		for(var i=0;i<this.icons.length;i++){
			var icon = this.icons[i];
			if( icon.type ){
				if( !buildTypes.contains( icon.type ) ) {
					this.build( icon.type );
					
					buildTypes.push( icon.type );
				}
			} else if( icon.html ) {
				this.createHTML( icon.html );
			}
		}
		
		IS_Event.observe(toolBarIconTable, 'mousedown', this.common.bind(this, "this.dummy", false), false, toolBarIconTable.id);
		//IS_Widget.Maximize.elm_toolBar.appendChild(toolBarTable);
		
	}
	
	this.build = function(type, alt, imgUrl) {
		if (type == 'mail') {
			var divMail = this.createIcon(type, IS_R.lb_sendMail, 'email.gif');
			var td = divMail.parentNode;
			td.removeChild( divMail );
			
			var form = document.createElement("form");
			form.action = 'mailto:';
			form.method = 'post';
			form.style.margin = '0px';
			form.setAttribute('enctype',"text/plain");
			form.appendChild(divMail);
			
			form.id = divMail.id+"_form";
			
			td.appendChild(form);
			
		} else if( type == "message") {
			this.createIcon( type,IS_R.lb_sentMessageOfArticle, 'comment.gif');
		} else {
			msg.warn( IS_R.getResource( IS_R.lb_illegalToolbarItem,[type]));
		}
	}
	this.createHTML = function( html ) {
		var td = document.createElement("td");
		toolBarIconTr.appendChild( td );
		
		var nobr = document.createElement("nobr");
		td.appendChild( nobr );
		
		nobr.innerHTML = html;
	}
	
	this.createIcon = function(type, alt, imgUrl) {
		var div = $( document.createElement("div"));
		div.id = "mti_" + self.id + "_" + type;
		div.className = "toolbar-item "+type;
		
		var img = document.createElement("img");
		img.className = 'icon';
		img.title = alt;
		if(imgUrl){
			var url = '';
			if(/http:\/\//.test(imgUrl)){
				url = imgUrl;
			}else{
				url = imageURL+imgUrl;
			}
			img.src = url;
		}
		div.appendChild( img );
		
		var text = document.createElement("span");
		text.appendChild( document.createTextNode( alt ));
		div.appendChild( text );
		
		IS_Event.observe(div, 'mousedown', this.mousedown.bind(this, div), false, div.id);
		IS_Event.observe(div, 'mouseup', this.mouseup.bind(this, div), false, div.id);
		IS_Event.observe(div, 'mouseout', this.mouseup.bind(this, div), true, div.id);
		IS_Event.observe(div, 'click', this.common.bind(this, "this." + type), false, div.id);
		
		var td = document.createElement("td");
		td.appendChild(div);
		toolBarIconTr.appendChild(td);
		
		return div;
	}
	
	this.mouseup = function (div, e) {
		Element.removeClassName( div,"pressed");
	}
	this.mousedown = function (div, e) {
		Element.addClassName( div,"pressed");
	}
	
	this.common = function (func, e) {
		var callFunc = new Function("e", func + "(e);");
		callFunc.call(this, e);
	}
	
	this.mail = function(){
		if( !this.displayState.isDisplay() || !this.hasUrl() )
			return;
		
		var displayTitle = this.displayState.getTitle();
		if( displayTitle.length == 0 )
			displayTitle = IS_R.lb_notitle;
		
		var url = this.displayState.getUrl();
		
		function sendMail( response ){
			var title = response.responseText;
			title = eval("(" + title + ")");
			var docTitle = title[0];
			var pageTitle = title[1];
			
			var subject = IS_R.getResource(IS_R.lb_sendMailTitle,[docTitle, pageTitle]);
			
			var body = IS_R.getResource( IS_R.lb_maximizeSendMail,[pageTitle,encodeURIComponent( url )]);
			
			location.href = 'mailto:?subject='+subject+'&body='+body;
		}
		
		// Encoding of title
		var encode_opt = {
			method: 'get' ,
			asynchronous:true,
			onSuccess: sendMail,
			onFailure: function(t) {
				msg.error( IS_R.getResource( IS_R.ms_urlEncodingonException,[t.status,t.statusText,text,encoding ]));
			},
			onException: function(r, t){
				msg.error( IS_R.getResource( IS_R.ms_urlEncodingFailed,[t,text,encoding ]));
			}
		};
		
		var uriText = encodeURIComponent( displayTitle );
		var docTitleText = encodeURIComponent(document.title);
		
		var url2 = hostPrefix + "/encsrv?text=" + docTitleText + "&text=" + uriText + "&encoding=Windows-31J";
		
		AjaxRequest.invoke(url2, encode_opt);
	}
	
	this.message = function(e){
		if(!this.displayState.isDisplay()|| !this.hasUrl() ) return;
		if(!self.shareModal){
			function toggleMode( flag ) {
				$("articleShare").addClassName( flag ?"userSearch" : "comment");
				$("articleShare").removeClassName( flag ?"comment" : "userSearch");
				
				//Delete result of user search
				if( flag && $( self.id +"_userListDiv" )) $( self.id +"_userListDiv" ).innerHTML = "";
				
				self.shareModal.position();
			}
			self.toInput = $.INPUT({style:'width:100%'});
			self.userSearch = $.INPUT({
				Class: "userSearchButton",
				type:"button",
				value: IS_R.lb_userSearch,
				onclick: {
					id: self.id,
					handler: function() {
						toggleMode( true );
					}
				}
			});
			self.msgTextarea = $.TEXTAREA({style:'width:100%;height:50px'});
			self.toPublic = $.INPUT({
				type: "checkbox",
				onclick: {
					id: self.id,
					handler: function() {
						self.userSearch.disabled = self.toInput.disabled = self.toPublic.checked;
						
						toggleMode( false );
					}
				}
			});
			var messageWidgetConf = IS_Widget.getConfiguration("Message");
			var moduleConfs = eval("("+messageWidgetConf.WidgetPref.formConfig.content + ")");
			if( !moduleConfs || moduleConfs.length == 0 )
				self.userSearch.style.display = "none";
			
			var content = $.TABLE(
				{
					cellPadding:0,
					cellSpacing:0,
					width:'100%'
				},
				$.TBODY({},
					$.TR({},
						$.TD(
							{
								style:'whiteSpace:nowrap',
								vAlign:'top'
							},
							IS_R.lb_destination+':'
						),
						$.TD( { width:'100%' },
							$.TABLE({},$.TBODY({},$.TR({},
								$.TD({ width:'100%' },self.toInput),
								$.TD({},self.userSearch)
							)))
						)
					),
					$.TR({},
						$.TD({}),
						$.TD({},self.toPublic,IS_R.lb_postMessageAsPublic)
					),
					$.TR({ Class: "commentPanel"},
						$.TD(
							{
								style:'whiteSpace:nowrap',
								vAlign:'top'
							},
							IS_R.lb_comment+":"
						),
						$.TD({},
							self.msgTextarea
						)
					),
					$.TR({ Class: "userSearchPanel"},
						( function() {
							var td = $.TD({},
								$.HR(),
								self.buildSearchUserPanel( moduleConfs,function( users ) {
									for( var i=0;i<users.length;i++ ) {
										var uid = users[i].uid;
										//No responce if it is duplicated
										if(self.toInput.value.match(new RegExp('^'+uid+'$|^'+uid+',|,'+uid+',|,'+uid+'$')))
											return;
										self.toInput.value += (/^\s*$|,$/.test( self.toInput.value )? "":",")+uid;
									}
								})
							);
							td.setAttribute("colSpan",2 );
							return td;
						})()
					)
				)
			);
			
			var modalDiv = $.TABLE({ id:"articleShare",Class: "comment user-search-modal"},
				$.TBODY({},
					$.TR({},
						$.TD({ Class: "header" },IS_R.lb_sentMessageOfArticle )
					),
					$.TR({},
						$.TD({ Class: "content"},
							content
						)
					),
					$.TR({},
						$.TD({ Class: "footer" },
							$.INPUT(
								{
									type:'button',
									value: IS_R.lb_transmission,
									Class: "transmissionButton",
									onclick:{
										handler:self.sendLink.bind(self),
										id:self.id
									}
								}
							),
							$.INPUT({
								type:"button",
								value:IS_R.lb_endOfUserSearch,
								Class:"searchEndButton",
								onclick: {
									id: self.id,
									handler: function() {
										toggleMode( false );
									}
								}
							}),
							$.INPUT(
								{
									type:'button',
									value:IS_R.lb_cancel,
									Class: "cancelButton",
									onclick:{
										handler:function(){
											self.shareModal.close();
										},
										id:self.id
									}
								}
							) 
						)
					)
				)
			);
			
			var shareIcon = Event.element(e);
			self.shareModal = new Control.Modal(
				shareIcon,
				{
				  afterOpen:function(){
				  	self.toInput.value = self.msgTextarea.value = "";
				  	self.toInput.disabled = false;
					self.userSearch.disabled = false;
				  	self.toPublic.checked = false;
				  	
				  	setTimeout( function() {
				  		self.toInput.focus();
				  	},10 );
				  	
				  	//FIXME u-n ,,,
				  	if( IS_Widget.MaximizeWidget )
					  	IS_Widget.MaximizeWidget.keybind.enable = false;
				  },
				  beforeClose: function() {
				  	toggleMode( false );
				  	
				  	if( modalDiv.parentNode ) //CHECKIT modalDiv is emputy at update("") in IE6
					  	modalDiv.parentNode.removeChild( modalDiv );
				  },
				  afterClose: function(){
				  	if( IS_Widget.MaximizeWidget )
					  	IS_Widget.MaximizeWidget.keybind.enable = true;
				  },
				  contents: modalDiv,
				  width: ( Browser.isIE?400:"auto")
				}
			);
		}
		self.shareModal.open();
	}
	this.buildSearchUserPanel = function( moduleConfs,handler ) {
		var userSearchPanel = new IS_Portal.UserSearchPanel({
			id: this.id,
			moduleConfs: moduleConfs,
			addUserListener: handler
		});
		
		var container = document.createElement("div");
		userSearchPanel.render( container );
		
		return container;
	}
	
	this.sendLink = function(){
		if( !this.displayState.isDisplay())
			return;
		
		if(!self.toInput.value && !self.toPublic.checked){
			alert(IS_R.ms_noInputDestination);
			return;
		}
		
		var articleUrl = this.displayState.getUrl();
		var title = this.displayState.getTitle() || articleUrl;
		
		var msg = self.msgTextarea.value;
		if( !msg ) {
			alert(IS_R.ms_noInputComment);
			return;
		}
		
		var to = self.toInput.value.split(",").collect( function( t ) {
			return t.replace(/^[\s　]*/,"").replace(/[\s　]*$/,"")
		}).join(",");
		var toPublic = self.toPublic.checked;
		if( msg && ( to || toPublic )){
			var error = IS_Validator.validate(msg, {
				label: IS_R.lb_comment,
				maxBytes: 2048
			});
			
			if( error )
				return alert( error );
			
			var url = hostPrefix + '/msg';
			var opt = {
				method:'post',
				asynchronous:true,
				postBody:'command=addlink'
					+'&message='+encodeURIComponent(msg)
					+( !toPublic ? '&to='+to :'')
					+'&title='+encodeURIComponent( title )
					+'&url='+encodeURIComponent( articleUrl ),
				onSuccess:function(){
					self.msgTextarea.value = '';
					self.toInput.value = '';
					self.shareModal.close();
				}
			};
			AjaxRequest.invoke(url, opt);
		}
	}
	
	this.dummy = function (){};
	
	this.loadContents = function () {
		this.displayContents();
	};
	
	this.hasUrl = function() {
		var url = this.displayState.getUrl()
		return url && url.length > 0;
	}
	this.displayStateChanged = function() {
		if( !this.hasUrl()) {
			this.elm_toolBar.addClassName("no-url");
		} else {
			this.elm_toolBar.removeClassName("no-url");
		}
	}
}
