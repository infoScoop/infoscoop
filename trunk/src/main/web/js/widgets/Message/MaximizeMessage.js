IS_Widget.MaximizeMessage = IS_Class.extend(IS_Widget.Message);
IS_Widget.MaximizeMessage.prototype.classDef = function() {
	var widget;
	var self = this;
	var limit = 200;
	var eventId;
	
	var listUrl = this.listUrl = {
		group: function(group){
			return hostPrefix + '/msg?command=list&uids='+self.getGroupUids(group) + '&result=json&limit='+(limit+1);
		},
		user: function(uid){
			return hostPrefix + '/msg?command=list&uids='+ uid + '&result=json&limit='+(limit+1);
		},
		recieved: hostPrefix + '/msg?command=recieved&result=json&limit='+(limit+1),
		mylist: hostPrefix + '/msg?command=mylist&result=json&limit='+(limit+1),
		all: hostPrefix + '/msg?command=all&result=json&limit='+(limit+1),
		broadcast: hostPrefix + '/msg?command=broadcast&result=json&limit='+(limit+1)
	};
	
	this.initialize = function(widgetObj){
		widget = this.widget;
		eventId = 'MaximizeMessage_' + widget.id;
		IS_EventDispatcher.addListener("addMessage", null, function(){
			self.loadMsgList(self.url, self.title, true);
		});
	}
	
	this.render = function(){
		IS_Event.unloadCache(eventId);
		this.userList = widget.getJSONUserPref("userList");
		this.groupCloseStatus = widget.getJSONUserPref("groupCloseStatus") || {};
		
		this.leftContent = $.TD({Class:'maximizeMsgMenuTd'});
		this.rightContent = $.TD({Class:'maximizeMsgListTd'});
		this.contentTable = $.TABLE(
			{
				Class:'maximizeMsg',
				cellSpacing:0,
				cellPadding:2
			},
			$.TBODY({},
				$.TR({},
					this.leftContent,
					this.rightContent
				)
			)
		)
		
		this.content.innerHTML = "";
		this.content.appendChild(this.contentTable);
		
		this.buildLeftContent(this.leftContent);
		this.latestMsgPanel.style.width = this.leftContent.offsetWidth - 5;
		this.buildRightContent(this.rightContent);
		
		this.adjustMaximizeHeight();
		
		IS_EventDispatcher.newEvent('loadComplete', widget.id, null);
	}
	
	this.buildLeftContent = function(leftContent){
		var content = $.DIV({Class:'maximizeMsgMenu'});
		self.msgMenu = content;
		content.appendChild(this.buildMsgInput());
		
		content.appendChild(this.buildPublicMsgButton());
		

		self.label = self.buildLabel( IS_R.lb_incomingMessage, listUrl.recieved,IS_R.lb_incomingMessage);
		self.incommingLabel = self.label;
		var systemUl = $.UL(
			{Class:'messageUsers'},
			$.LI({style:'backgroundImage:url('+imageURL+'inbox_document.gif)'},
				self.label
			),
			$.LI({style:'backgroundImage:url('+imageURL+'inbox_document.gif)'},

				self.buildLabel( IS_R.lb_outboundMessage, listUrl.mylist, IS_R.lb_outboundMessage )
			),
			$.LI({style:'backgroundImage:url('+imageURL+'information.gif)'},

				self.buildLabel( IS_R.lb_information, listUrl.broadcast, IS_R.lb_information ),
				self.enableBroadcast ? self.buildBroadcastMsgButton() : ''
			),
			$.LI({},

				self.buildLabel( IS_R.lb_allUsersPublicMessage, listUrl.all, IS_R.lb_allUsersPublicMessage )
			)
		);
		

		var systemLabel = self.buildGroupLabel( IS_R.lb_system, systemUl);
		var systemGroup = $.DIV(
			{
				Class:'messageGroup',
				onclick:{

					handler: self.toggleGroup.bind(this, systemLabel, IS_R.lb_system, systemUl),
					id:eventId
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

			var groupLabel = self.buildGroupLabel(group.key, userUl, listUrl.group(group), IS_R.getResource( IS_R.lb_messageOfGroup,[group.key]));
			var groupDiv = $.DIV(
				{
					Class:'messageGroup',
					onclick:{
						handler: self.toggleGroup.bind(self, groupLabel, group.key, userUl),
						id:eventId
					}
				},
				groupLabel,
				( listUrl.group(group ) ? self.buildLabel(group.key, listUrl.group(group ), IS_R.getResource( IS_R.lb_messageOfGroup,[group.key])) : group.key ),
				self.buildMsgButton({
					uid:self.getGroupUids(group),

					name:IS_R.getResource( IS_R.lb_group,[group.key])
				})
			);
			group.value.each(function(user){
				userUl.appendChild(
					$.LI({},

						self.buildLabel(user.name, listUrl.user(user.uid), IS_R.getResource( IS_R.lb_messageOf,[user.name])),
						self.buildMsgButton(user)
					)
				);
			});
			content.appendChild(groupDiv);
			content.appendChild(userUl);
		});
		leftContent.appendChild(content);
	}
	
	this.buildRightContent = function(content){
		var content = self.rightContent;
		content.innerHTML = "";
		
		self.msgTitle = $.DIV({Class:'msgTitle'});
		content.appendChild(self.msgTitle);
		
		self.msgList = $.DIV();
		self.moreButton = $.INPUT(
			{
				Class:'maximizeMsgMore',
				style:'display:none',
				type:'button',

				value: IS_R.lb_readMore,
				onclick:{
					handler:function(){
						var url = self.url+'&offset='+self.oldestId;
						self.loadMsgList(url);
					}.bind(self),
					id:eventId
				}
			}
		);
		self.msgListBox = $.DIV({Class:'maximizeMsgList'}, self.msgList, self.moreButton);
		content.appendChild(self.msgListBox);
		

		self.loadMsgList(listUrl.recieved, IS_R.lb_incomingMessage, true);
	}
	
	this.buildGroupLabel = function(groupName, userUl, url, title){
		var groupLabel = $.SPAN({});
		if(self.groupCloseStatus[groupName])
			self.closeGroup(groupLabel, userUl);
		else
			self.openGroup(groupLabel, userUl);
		return groupLabel;
	}
	
	this.buildLabel = function(label, url, title){
		return $.A(
			{
				href:'javascript:void(0)',
				onclick:{
					handler:function(e, url, title){
						if(self.label)
							self.label.className = '';
						self.label = Event.element(e);
						self.loadMsgList(url, title, true);
						Event.stop(e);
					}.bindAsEventListener(self, url, title),
					id:eventId
				}
			},
			label
		);
	}
	
	function buildMsgBody(msg){
		var div = $.DIV({Class:'msgBody'});
		if(msg.type.startsWith('FYI') && msg.option){
			var link = buildLink(msg.option);
			if(link) div.appendChild(link);
		}
		var msgBody = $.SPAN();
		msgBody.innerHTML = msg.body;
		div.appendChild(msgBody);
		return div;
	}
	
	function buildLink(option){
		var json = option.evalJSON();
		if(json.url){
			return $.A(
				{
					style:'display:block',
					href:json.url,
					title:json.title,
					target:'_blank'
				},
				json.title
			)
		} else if(json.title) {
			return $.DIV({}, json.title)
		}
		return false;
	}
	
	function buildUidLink(uid, userName, className){
		if(!uid) return '';
		return $.A(
			{
				Class:className,
				href:'javascript:void(0)',
				onclick:{
					handler:function(uid, userName){

						self.loadMsgList(listUrl.user(uid), IS_R.getResource( IS_R.lb_messageOf,[userName]), true);
						if(self.label)
							self.label.className = '';
						self.label = null;
					}.bind(self, uid, userName),
					id:eventId
				}
			},
			userName
		);
	}
	
	function buildToLink(uids, className){
		if(!uids || uids.length == 0) return '';
		var links = $.DIV({Class:className}, 'to ');
		uids.each(function(uid, idx){
			if(idx > 0)
				links.appendChild(document.createTextNode(','+$.NBSP));
			links.appendChild(buildUidLink(uid.uid, uid.name));
		});
		return links;
	}
	
	function buildReply(uid, userName){
		if(!uid) return '';
		return $.DIV(
			{
				Class:'msgReply',
				onclick:{
					handler:function(uid, userName){
						self.to = uid;

						self.toLabel.innerHTML = IS_R.getResource( IS_R.lb_sentMessageTo,[userName] );
						Element.show(self.msgContainer);
						self.msgTextarea.focus();
					}.bind(self, uid, userName),
					id:eventId
				}
			},

			IS_R.lb_doReply
		);
	}
	
	this.loadMsgList = function(url, title, isInit){
		var opt = {
			method:'get',
			asynchronous:true,
			onSuccess:function(req){
				if(isInit){
					self.msgTitle.innerHTML = title;
					self.msgList.innerHTML = '';
					self.oldestId = Number.MAX_VALUE;
					self.url = url;
					self.title = title;
					if(self.label) {
						self.label.className = 'maximizeMsgLabelSelected';
					}
					
					if(url == listUrl.recieved){
						IS_Portal.msgLastViewTime = new Date().getTime();
						IS_Widget.setPreferenceCommand("msgLastViewTime", IS_Portal.msgLastViewTime);
					}
				}
				
				var msgs = req.responseText.evalJSON();
				if(msgs.length == 0){

					self.msgList.innerHTML = IS_R.lb_noMessage;
					self.oldestId = Number.MAX_VALUE;
				} else {
					function getClass(msg){
						if(msg.type == 'MSGBC')
							return 'msgBroadcast';
						else if(msg.to)
							return 'msgDirect';
						return 'msgPublic';
					}
					msgs.each(function(msg, idx){
						if(idx == limit) return;
						self.msgList.appendChild($.DIV(
							{
								Class:getClass(msg),
								style:'clear:both'
							},
							buildUidLink(msg.from, msg.displayFrom, 'msgAuthor'),
							buildToLink(msg.to, 'msgTo'),
							buildReply(msg.from, msg.displayFrom),
							$.DIV({Class:'msgDate'},msg.postedtime),
							buildMsgBody(msg)
						));
					});
					self.oldestId = msgs[ msgs.length -1 ].id;
				}
				
				if(msgs.length > limit){
					Element.show(self.moreButton);
				} else {
					Element.hide(self.moreButton);
				}
			},
			onFailure: function(t) {

				msg.error(IS_R.getResource( IS_R.ms_getMessageListFailure +'{0} -- {1}',[t.status, t.statusText]));
			},
			onException: function(r, t){

				msg.error(IS_R.getResource( IS_R.ms_getMessageListFailure +'{0}',[getErrorMessage(t)]));
			}
		};
		AjaxRequest.invoke(url, opt);
	}
	
	this.maximize = function(){
		this.render();
	}
	
	this.refresh = function(){
		self.loadMsgList(self.url, self.title, true);
	}
	
	this.loadContentsOption = {
		onSuccess: this.render.bind(this)
	}
	
	this.adjustMaximizeHeight = function() {
		function setElementHeight(elm, height){
			if(elm && elm.style)
				elm.style.height = height;
		}
		setElementHeight(self.msgMenu, 1);
		setElementHeight(self.msgListBox, 1);
		self.contentTable.style.height = getWindowSize(false) - findPosY( self.contentTable ) - 30;
		setElementHeight(self.msgMenu, self.leftContent.offsetHeight);
		setElementHeight(self.msgListBox, self.rightContent.offsetHeight - self.msgTitle.offsetHeight);
	}
	this.adjustMaximizeWidth = function() {
		Element.hide(self.msgListBox);
		Element.hide(self.msgTitle);
		self.msgListBox.style.width = 
		self.msgTitle.style.width =
				getWindowSize(true) -findPosX( this.rightContent ) -( Browser.isIE? 32:30 );
		Element.show(self.msgListBox);
		Element.show(self.msgTitle);
	}
};