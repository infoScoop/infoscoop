IS_Widget.Message = IS_Class.create();
IS_Widget.Message.prototype.classDef = function() {
	var widget;
	var self = this;
	var commandQueueWaitMs = commandQueueWait*1000;
	
	var rssUrl = {
		group: function(group){
			return localhostPrefix + '/msg?command=list&uids='+self.getGroupUids(group);
		},
		user: function(user){
			return localhostPrefix + '/msg?command=list&uids='+ user.uid;
		},
		recieved: localhostPrefix + '/msg?command=recieved',
		mylist: localhostPrefix + '/msg?command=mylist',
		all: localhostPrefix + '/msg?command=all',
		broadcast: localhostPrefix + '/msg?command=broadcast'
	};
	
	this.getGroupUids = function(group){
		var uids = group.value.collect(function(user){
			return user.uid;
		}).sort();
		return uids.join(',');
	}
	
	this.initialize = function(widgetObj){
		widget = widgetObj;
		this.widget = widget;
		this.widgetId = widget.id;
		
		this.groupSettingModal = new IS_Portal.groupSettingModal({
			moduleConfs: eval("("+widget.widgetPref.formConfig.value + ")"),
			afterClose : function(){
				widget.setUserPref("userList",self.groupSettingModal.getGroupConf() );
				widget.loadContents();
				
				//Change URL of previous group RSS
				//Cannot change group name because group name is set as key
				/*$H(self.userList).each(function(group){
					var oldUrl = self.rssUrls[group.key];
					if(oldUrl){
						var newUrl = rssUrl.group(group);
						if(oldUrl != newUrl){
							IS_EventDispatcher.newEvent('reloadWidget', oldUrl, newUrl);
						}
					}
				});
				self.resetRssUrls();*/
			}
		});
		//this.resetRssUrls();
		
		this.content = widget.elm_widgetContent;
		
		var isBroadcastAdminOnly = widget.widgetPref.broadcastAdminOnly && getBooleanValue(widget.widgetPref.broadcastAdminOnly.value);
		this.enableBroadcast = !isBroadcastAdminOnly || (isBroadcastAdminOnly && is_isAdministrator);
	}
	
	/*this.resetRssUrls = function(){
		self.rssUrls = {};
		$H(self.userList).each(function(group){
			self.rssUrls[group.key] = rssUrl.group(group);
		});
	}*/
	
	this.render = function(){
		this.userList = widget.getJSONUserPref("userList");
		this.groupCloseStatus = widget.getJSONUserPref("groupCloseStatus") || {};
		
		var content = this.content;
		content.innerHTML = "<table><tbody><tr><td/></tr></tbody></table>";
		content.firstChild.className = "constraintTable";
		content = content.firstChild.firstChild.firstChild.firstChild;
		
		content.appendChild(this.buildMsgInput());
		
		content.appendChild(this.buildPublicMsgButton());
		
		var systemUl = $.UL(
			{Class:'messageUsers'},
			$.LI({style:'backgroundImage:url('+imageURL+'inbox_document.gif)'},

				$.SPAN({}, IS_R.lb_incomingMessage ),

				self.buildRssButton(rssUrl.recieved,IS_R.lb_incomingMessage )
			),
			$.LI({style:'backgroundImage:url('+imageURL+'inbox_document.gif)'},

				$.SPAN({}, IS_R.lb_outboundMessage),

				self.buildRssButton(rssUrl.mylist,IS_R.lb_outboundMessage )
			),
			$.LI({style:'backgroundImage:url('+imageURL+'information.gif)'},

				$.SPAN({}, IS_R.lb_information ),

				self.buildRssButton(rssUrl.broadcast,IS_R.lb_information ),
				self.enableBroadcast ? self.buildBroadcastMsgButton() : ''
			),
			$.LI({},

				$.SPAN({}, IS_R.lb_allUsersPublicMessage ),

				self.buildRssButton(rssUrl.all,IS_R.lb_allUsersPublicMessage )
			)
		);
		

		var systemLabel = self.buildGroupLabel( IS_R.lb_system, systemUl);
		var systemGroup = $.DIV(
			{
				Class:'messageGroup',
				onclick:{

					handler: self.toggleGroup.bind(this, systemLabel, IS_R.lb_system, systemUl),
					id:widget.id
				},
				style:'backgroundColor:#EEEEEE;borderBottom:2px dotted #D0D0D0'
			},
			systemLabel,
			IS_R.lb_system
		);
		content.appendChild(systemGroup);
		content.appendChild(systemUl);
		
		$H(this.userList).each(function(group){
			var userUl = $.UL({Class:'messageUsers'});
			var groupLabel = self.buildGroupLabel(group.key, userUl);
			var groupDiv = $.DIV(
				{
					Class:'messageGroup',
					onclick:{
						handler: self.toggleGroup.bind(self, groupLabel, group.key, userUl),
						id:widget.id
					}
				},
				groupLabel,
				group.key,

				self.buildRssButton(rssUrl.group(group), IS_R.getResource( IS_R.lb_messageOfGroup,[group.key])),
				self.buildMsgButton({
					uid:self.getGroupUids(group),

					name: IS_R.getResource( IS_R.lb_group,[group.key])
				})
			);
			group.value.each(function(user){
				userUl.appendChild(
					$.LI({},
						user.name,

						self.buildRssButton(rssUrl.user(user), IS_R.getResource( IS_R.lb_messageOf,[user.name])),
						self.buildMsgButton(user)
					)
				);
			});
			content.appendChild(groupDiv);
			content.appendChild(userUl);
		});
		
		setTimeout(IS_Widget.Message.checkNewMsg, commandQueueWaitMs);
		IS_EventDispatcher.newEvent('loadComplete', widget.id, null);
	}
	
	this.renderUnlogin = function(){
		widget.disableMaximizeView = true;
		this.content.innerHTML = "";

		this.content.appendChild(document.createTextNode(IS_R.ms_needLoginForUseWidget));
		IS_EventDispatcher.newEvent('loadComplete', widget.id, null);
	}
	
	this.loadContentsOption = {
		onSuccess: (is_userId)? this.render.bind(this) : this.renderUnlogin.bind(this)
	}
	
	this.buildGroupLabel = function(groupName, userUl){
		var groupLabel = $.SPAN({});
		if(self.groupCloseStatus[groupName])
			self.closeGroup(groupLabel, userUl);
		else
			self.openGroup(groupLabel, userUl);
		return groupLabel;
	}
	
	this.toggleGroup = function(groupLabel, groupName, userUl){
		if(self.groupCloseStatus[groupName]) {
			self.openGroup(groupLabel, userUl);
			self.groupCloseStatus[groupName] = false;
		} else {
			self.closeGroup(groupLabel, userUl);
			self.groupCloseStatus[groupName] = true;
		}
		widget.setUserPref("groupCloseStatus", self.groupCloseStatus);
	}
	
	this.closeGroup = function(groupLabel, userUl){
		groupLabel.className = 'messageGroupClose';
		Element.hide(userUl);
	}
	
	this.openGroup = function(groupLabel, userUl){
		groupLabel.className = 'messageGroupOpen';
		Element.show(userUl);
	}
	
	this.buildRssButton = function(url, title){
		var rssButton = $.IMG({
			src:imageURL + 'feed_add.gif',
			style:'cursor:pointer;marginLeft:5px',

			title: IS_R.getResource( IS_R.lb_subscribes,[title]),
			onclick:{
				handler:function(e, uid){
					var widgetId = "w_" + new Date().getTime();
					var properties = {
						url: url,
						//isMessageList:true,
						doLineFeed:true
					};
					var widgetConf = IS_WidgetsContainer.WidgetConfiguration.getConfigurationJSONObject('RssReader', widgetId, 1, title, null, properties);
					var widget = IS_WidgetsContainer.addWidget( IS_Portal.currentTabId, widgetConf );
					IS_Widget.setWidgetLocationCommand(widget);
					IS_Portal.widgetDropped( widget );
					Event.stop(e);
				}.bindAsEventListener(this, url),
				id:widget.id
			}
		});
		return rssButton;
	}
	
	this.buildPublicMsgButton = function(){
		return $.DIV(
			{
				Class:'messageSelf',
				onclick:{
					handler: function(){
						self.to = '';
						self.isBroadcast = false;

						self.toLabel.innerHTML = IS_R.lb_postMessageToPublic;
						Element.show(self.msgContainer);
						self.msgTextarea.focus();
					},
					id:widget.id
				}
			},

			IS_R.lb_postMessageToPublic
		)
	}
	
	this.buildMsgButton = function(user){

		var label = IS_R.getResource( IS_R.lb_sentMessageTo,[user.name]);
		var msgButton = $.IMG({
			onclick:{
				handler: function(e, user, label){
					self.to = user.uid;
					self.isBroadcast = false;
					self.toLabel.innerHTML = label;
					Element.show(self.msgContainer);
					self.msgTextarea.focus();
					Event.stop(e);
				}.bindAsEventListener(this, user, label),
				id:widget.id
			},
			src:imageURL + 'email_edit.gif',
			style:'cursor:pointer;marginLeft:5px',
			title: label
		});
		return msgButton;
	}
	
	this.buildBroadcastMsgButton = function(){
		var msgButton = $.IMG({
			onclick:{
				handler: function(){
					self.to = '';
					self.isBroadcast = true;

					self.toLabel.innerHTML = IS_R.lb_sendBroadcastMsg;
					Element.show(self.msgContainer);
					self.msgTextarea.focus();
				},
				id:widget.id
			},
			src:imageURL + 'email_edit.gif',
			style:'cursor:pointer;marginLeft:5px',

			title: IS_R.lb_sendBroadcastMsg
		});
		return msgButton;
	}
	
	this.buildMsgInput = function(){
		this.toLabel = $.DIV({
			Class:'messageTo'
		});
		var msgButtonHandler = this.handleMsgButton.bind(this);
		this.msgTextarea = $.TEXTAREA({
			style: 'width:100%;height:50px',
			onkeyup:{
				handler:msgButtonHandler,
				id:widget.closeId
			},
			onclick:{
				handler:msgButtonHandler,
				id:widget.closeId
			},
			onblur:{
				handler:msgButtonHandler,
				id:widget.closeId
			}
		});
		this.sendMsgButton = $.INPUT({
			type:'button',

			value: IS_R.lb_transmission,
			disabled:true,
			onclick:{
				handler:this.addMessage.bind(this),
				id:widget.closeId
			}
		});
		this.latestMsg = $.SPAN();
		this.latestMsgPanel = $.DIV(
			{
				Class:'latestMsg',
				style:'display:none'
			},
			$.DIV(
				{style:'fontWeight:bold'},

				IS_R.lb_latestMessage + ' : '
			),
			this.latestMsg
		);
		this.msgContainer = $.DIV(
			{
				style:'display:none'
			},
			this.toLabel,
			this.msgTextarea,
			this.latestMsgPanel,
			$.DIV(
				{
					style:'textAlign:center'
				},
				this.sendMsgButton,
				$.INPUT({
					type:'button',
					value:IS_R.lb_close,
					onclick:{
						handler:this.hideMsgInput,
						id:widget.closeId
					}
				})
			)
		);
		this.msgContainer.style.borderBottom = '1px solid #555';
		return this.msgContainer;
	}
	
	this.handleMsgButton = function(){
		self.sendMsgButton.disabled = !self.msgTextarea.value;
	}
	
	this.hideMsgInput = function(){
		self.msgTextarea.value = '';
		self.to = null;
		self.isBroadcast = false;
		Element.hide(self.msgContainer);
	}
	
	this.addMessage = function(){
		self.sendMsgButton.disabled = true;
		this.toLabel.style.backgroundImage = 'url('+imageURL+'indicator.gif)';
		this.startTime = new Date().getTime();
		
		var body = this.msgTextarea.value;
		var error = IS_Validator.validate(body, {
			required: true,
			maxBytes: 2048
		});
		if(error){
			alert(error);
			self.toLabel.style.backgroundImage = '';
			self.handleMsgButton();
			self.msgTextarea.focus();
			return;
		}
		var url = hostPrefix + '/msg';
		var opt = {
			method:'post',
			asynchronous:true,
			postBody:'command='
				+(self.isBroadcast ? 'addbc' : 'add')
				+'&message='+encodeURIComponent(body)
				+(self.to ? '&to='+self.to : ''),
			onSuccess:function(){
				var waitTime = new Date().getTime() - self.startTime < 1000 ? 1000 : 0;
				setTimeout(function(){
					self.toLabel.style.backgroundImage = '';
					self.latestMsg.innerHTML = '';
					if(!Browser.isIE)
						self.latestMsgPanel.style.width = self.msgTextarea.offsetWidth;
					self.latestMsg.appendChild(document.createTextNode(self.msgTextarea.value));
					Element.show(self.latestMsgPanel)
					self.msgTextarea.value = '';
					self.handleMsgButton();
					self.msgTextarea.focus();
					//The group that includes self is not reload
					//IS_EventDispatcher.newEvent('reloadWidget', rssUrl.mylist);
					//IS_EventDispatcher.newEvent('reloadWidget', rssUrl.all);
					if(IS_Widget.MaximizeWidget)
						IS_EventDispatcher.newEvent('addMessage');
				}, waitTime);
			},
			onFailure:function(t){
				self.toLabel.style.backgroundImage = '';
				self.handleMsgButton();
				self.msgTextarea.focus();

				msg.error(IS_R.getResource(IS_R.ms_sendMessageFailed+'{0} -- {1}',[t.status, t.statusText]));

				alert(IS_R.ms_sendMessageFailed);
			},
			onException:function(r, t){
				msg.error(IS_R.getResource( IS_R.ms_sendMessageFailed +'{0}',[getErrorMessage(t)]));
				alert(IS_R.ms_sendMessageFailed);
			}
		};
		AjaxRequest.invoke(url, opt);
	}
	
	this.editGroupIconHandler = function(iconDiv){
		self.groupSettingModal.setGroupConf( widget.getJSONUserPref("userList") );
		self.groupSettingModal.buildModalBox(iconDiv);
		widget.headerContent.hiddenMenu.hide();
	}
	
	this.editGroupApplyIconStyle = function(iconDiv){
		if(!is_userId)
			iconDiv.style.display = 'none';
	}
};

IS_Widget.Message.checkNewMsgRepeat = function(){
	if(!is_userId) return;
	
	var refreshIntervalMs = refreshInterval*60*1000;
	//Check new message in hided tub by automatic updating
	//Timer keeps on working in current tub
	//if(IS_Portal.currentTabId != widget.tabId)
	IS_Widget.Message.checkNewMsg();
	setTimeout(IS_Widget.Message.checkNewMsgRepeat, refreshIntervalMs);
}

IS_Widget.Message.checkNewMsg = function(){
	var opt = {
		method:'get',
		asynchronous:true,
		onSuccess:function(req){
			var result = req.responseText.evalJSON();
			if(!result) return;
			
			var msgListDiv = $('message-list');
			
			if(!$('message-newmsg')){
				var newMenuItemMsgSpan = $.DIV(
					{
						id:'message-newmsg',
						style:'cursor:pointer',
						onclick:{
							handler:function(e){
								if(msgListDiv.childNodes.length == 1) {
									IS_Portal.closeMsgBar();
								} else {
									Event.element(e).remove();
								}
								
								var widget = IS_Portal.findWidgetByType("Message");
								if(!widget){
									var menuItem = IS_TreeMenu.findMenuItemByType("Message");
									menuItem = menuItem ? menuItem[0] : false;
									if(menuItem){
										widget = IS_TreeMenu.addMenuItem(menuItem);
									} else {
										var widgetId = "w_" + new Date().getTime();
										var typeConf = IS_Widget.getConfiguration("Message");
										widgetConf = IS_WidgetsContainer.WidgetConfiguration.getConfigurationJSONObject(typeConf.type, widgetId, 1, typeConf.title, typeConf.href, {});
										
										widget = IS_WidgetsContainer.addWidget( IS_Portal.currentTabId, widgetConf );
										IS_Widget.setWidgetLocationCommand(widget);
										IS_Portal.widgetDropped(widget);
									}
								}
								
								if( widget.maximize ) {
									var maxContent = widget.maximize.content;
									if(maxContent.label)
										maxContent.label.className = '';
									
									maxContent.url = maxContent.listUrl.recieved;
									maxContent.label = maxContent.incommingLabel;
									maxContent.title = IS_R.lb_incomingMessage;
								} else if(IS_Widget.MaximizeWidget) {
									IS_Widget.MaximizeWidget.turnbackMaximize();
								}
								
								widget.changeMaximize();
							}
						}
					},
					$.IMG(
						{
							style:'position:relative;top:2px;paddingRight:2px',
							src:imageURL+"information.gif"
						}
					),

					IS_R.lb_newArriedMessage
				);
				msgListDiv.appendChild(newMenuItemMsgSpan);
			}
			
			$('message-bar').style.display = "";
			IS_EventDispatcher.newEvent("adjustedMessageBar");
		},
		onFailure: function(t) {

			msg.error(IS_R.getResource( IS_R.lb_checkNewArriedMessageFailure +'{0} -- {1}',[t.status, t.statusText]));
		},
		onException: function(r, t){

			msg.error(IS_R.getResource( IS_R.lb_checkNewArriedMessageFailure +'{0}',[getErrorMessage(t)]));
		}
	};
	AjaxRequest.invoke(hostPrefix + '/msg?command=check&lastviewtime='+IS_Portal.msgLastViewTime, opt);
}