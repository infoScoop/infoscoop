/* infoScoop OpenSource
 * Copyright (C) 2010 Beacon IT Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 */

IS_Portal.groupSettingModal = Class.create();
IS_Portal.groupSettingModal.prototype = {
	CURRENT_GROUP_NAME:false,
	initialize: function( opt ) {
		this.moduleConfs = opt.moduleConfs || {};
		this.afterClose = opt.afterClose || function(){}

		//Initialize Modal
		this.currentModal = new Control.Modal('', {
			className: 'GroupSchedule user-search-modal',
		    afterClose: function(){
		    	this.afterClose.bind(this);
		    	this.currentModal.container.update('');
		    }.bind(this)
		});
	},
	getGroupConf: function() {
		var scheduleConf = {};
		for( var i=0;i<this.groupSettingPanel.groups.length;i++ ) {
			var group = this.groupSettingPanel.groups[i];
			
			scheduleConf[ group.name ] = group.users.concat([]);
		}
		
		return scheduleConf;
	},
	setGroupConf: function( scheduleConf ) {
		scheduleConf = scheduleConf || {};
		
		var groups = $H( scheduleConf ).keys().collect( function( key ){
			return {
				name: key,
				users: scheduleConf[ key ]
			}
		},this );
		
		if( groups.length == 0 ) {
			groups = [{

				name: IS_R.lb_newGroup,
				users: []
			}];
		}
		
		this.groups = groups;
	},
	//Initialize Modal
	buildModalBox : function( iconDiv ){
		// fix #480/#13731
//		if(this.currentModal)this.currentModal.open();
		IS_Widget.WidgetHeader.MenuPullDown.hide;

		var modalDiv = document.createElement("div");
		this.currentModal.container.update(modalDiv);
		this.currentModal.open();
		
		var header = document.createElement("div");
		header.className = "header";

		header.appendChild( document.createTextNode( IS_R.lb_editGroup ));
		modalDiv.appendChild( header );
		
		var content = document.createElement("div");
		content.className = "content";
		modalDiv.appendChild( content );
		
		var footer = document.createElement("div");
		footer.className = "footer";
		modalDiv.appendChild( footer );
		
		var closeButton = document.createElement("input");
		closeButton.type = "button";
		closeButton.value = IS_R.lb_close;
		footer.appendChild( closeButton );
		
		Event.observe( closeButton,"click",function(e) { Control.Modal.close();e.stop();});
		
		this.buildContents( content );
		
		this.currentModal.position();
	},
	buildContents: function( container ) {
		container.innerHTML = "<table><tbody><tr><td/></tr><tr><td/></tr></tbody></table>";
		
		container.firstChild.className = "modal-layout";
		//Show the view of all groups
		
		this.groupSettingPanel = new IS_Portal.GroupSettingPanel({
			id: this.widgetId,
			groups: this.groups,
			groupsChangeListener: this.handleGroupsChange.bind( this ),
			currentGroupChangeListener: this.handleCurrentGroupChange.bind( this )
		});
		this.groupSettingPanel.render(container.firstChild.firstChild.childNodes[0].firstChild);
		
		//Set "DIV" of search condition
		
		// Right side("Div" for box of searching users)
		this.userSearchPanel = new IS_Portal.UserSearchPanel({
			id: this.widgetId,
			moduleConfs: this.moduleConfs,
			addUserListener: this.handleAddUser.bind( this )
		});
		this.userSearchPanel.render( container.firstChild.firstChild.childNodes[1].firstChild );
		
		this.handleCurrentGroupChange( this.groups[0].name );
	},
	handleGroupsChange: function() {
		if( this.groupSettingPanel.groups.length == 0 )
			Control.Modal.close();
	},
	handleCurrentGroupChange: function( groupName ) {
		this.CURRENT_GROUP_NAME = groupName;
		if( !this.userSearchPanel )
			return;
		
		if( groupName ){

			this.userSearchPanel.description = IS_R.getResource( IS_R.ms_addCheckedUser,[groupName]);
		}else{
			this.userSearchPanel.description = IS_R.ms_targetGroupNotFound;
		}
		this.userSearchPanel.renderDescription();
	},
	handleAddUser : function( users ){
		// Create empty array for users of new groups
		if((this.CURRENT_GROUP_NAME == undefined) || (this.CURRENT_GROUP_NAME == '')) return;
		
		var group = this.groupSettingPanel.findGroupByName( this.CURRENT_GROUP_NAME );
		if( !group ) return;
		
		var uids = group.users.collect( function( user ) {
			return user.uid;
		});
		
		users.reject( function( user ) {
			return ( uids.contains( user.uid ));
		}).each( function( user ){
			group.users.push( user );
		},this );
		
		group.notifyUpdate();
	}
}

IS_Portal.GroupSettingPanel = Class.create();
IS_Portal.GroupSettingPanel.Group = Class.create();
IS_Portal.GroupSettingPanel.Group.unique = new Date().getTime();
IS_Portal.GroupSettingPanel.Group.prototype = {
	initialize: function( json ) {
		Object.extend( this,json );
		this.id = String( IS_Portal.GroupSettingPanel.Group.unique++ );
		
		this.deleteListeners = [];
		this.updateListeners = [];
		this.editNameListeners = [];
	},
	notifyDelete: function() {
		for( var i=0;i<this.deleteListeners.length;i++ )
			this.deleteListeners[i]( this );
	},
	notifyUpdate: function() {
		for( var i=0;i<this.updateListeners.length;i++ )
			this.updateListeners[i]( this );
	},
	notifyStartEditName: function() {
		for( var i=0;i<this.editNameListeners.length;i++ )
			this.editNameListeners[i].onStart();
	},
	notifyEndEditName: function() {
		for( var i=0;i<this.editNameListeners.length;i++ )
			this.editNameListeners[i].onEnd();
	}
}
IS_Portal.GroupSettingPanel.prototype = {
	initialize: function( opt ) {
		this.id = opt.id || "";
		this.groups = [];
		this.groupsChangeListener = opt.groupsChangeListener || function() {};
		this.notifyCurrentGroupChange = opt.currentGroupChangeListener || function() {};
		
		this._handleDeleteGroup = this.handleDeleteGroup.bind( this );
		this._notifyGroupsChange = this.notifyGroupsChange.bind( this );
		this._editNameHandler = {
			onStart: this.editNameOnStart.bind( this ),
			onEnd: this.editNameOnEnd.bind( this )
		};
		
		( opt.groups || [] ).each( function( groupJson ) {
			var group = new IS_Portal.GroupSettingPanel.Group( groupJson );
			group.deleteListeners.push( this._handleDeleteGroup );
			group.updateListeners.push( this._notifyGroupsChange );
			group.editNameListeners.push( this._editNameHandler );
			
			this.groups.push( group );
		},this );
	},
	findGroupById: function( gid ) {
		return this.groups.find( function( g ) {
			return g.id == gid;
		},this );
	},
	findGroupByName: function( gname ) {
		return this.groups.find( function( g ) {
			return g.name == gname;
		},this );
	},
	render: function( container ) {
		if( !container && this.groupSetting && this.groupSetting.parentNode ) {
			container = this.groupSetting.parentNode;
			container.removeChild( this.groupSetting );
		}
		
		var div = this.groupSetting = document.createElement("div");
		div.className = "group-setting";
		container.appendChild( div );
		
		var groupTabs = this.elm_groupTabs = document.createElement("ul");
		groupTabs.id = "group-setting-"+this.id+"-tabs";
		groupTabs.className = "tabs";
		div.appendChild( groupTabs );
		
		var groupContents = this.elm_groupContents = document.createElement("div");
		groupContents.className = "contents";
		div.appendChild( groupContents );
		
		var groupContentsLayout = this.elm_groupContentsLayout = $( document.createElement("div") );
		groupContentsLayout.className = "layout";
		groupContents.appendChild( groupContentsLayout );
		
		$H( this.groupTabs || {} ).values().concat( $H( this.groupContents || {} ).values())
				.each( function( renderer ) {
					renderer.clean();
				});
		
		var db = this.dragBlock = document.createElement("div");
		db.className = "drag-block";
		groupContentsLayout.appendChild( db );	
			
		this.groupTabs = {};
		this.groupContents = {};
		//Show the view of all groups
		for(var i=0;i<this.groups.length;i++)
			this.addGroup( this.groups[i] );
		
		this.tabs = new Control.Tabs( groupTabs,{
			linkSelector: "li", // selector bug ?
			activeClassName: "selected",
			afterChange: function( tab ) {
				var gid = tab.id.substring( 0,tab.id.length -5 );
				var group = this.findGroupById( gid );
				if( !group ) return;
				
				this.currentGroup = group;
				this.notifyCurrentGroupChange( group.name );
			}.bind( this )
		});
		
		this.renderAddGroup( groupTabs );
		
		if( this.groups.length > 0 )
			this.tabs.setActiveTab( this.groups[0].id+"_user" );
	},
	renderAddGroup: function( container ) {
		var item = this.elm_addGroup = document.createElement("li");
		item.className = "add-group";
		item.appendChild( document.createTextNode(">> "+IS_R.lb_addGroup ));
		container.appendChild( item );
		
		IS_Event.observe( item, 'mousedown', this.addNewGroup.bind(this), false, this.id);
	},
	
	addGroup : function( group ){
		var groupTab = this.groupTabs[group.id] = new IS_Portal.GroupSettingPanel.GroupTab( group );
		groupTab.render( this.elm_groupTabs );
		
		var groupContent = this.groupContents[group.id] = new IS_Portal.GroupSettingPanel.GroupContent( group );
		groupContent.render( this.elm_groupContentsLayout );
		groupContent.groupUserDiv.style.display = "none";
	},
	addNewGroup: function() {

		var groupName = IS_R.lb_newGroup;
		var n = 1;
		while( this.groups.find( function( g ) { return g.name == groupName }))

			groupName = IS_R.lb_newGroup+"("+(n++)+")";
		
		group = new IS_Portal.GroupSettingPanel.Group({
			name: groupName,
			users: []
		});
		
		this.addGroup( group );
		this.groups.push( group );
		this.notifyGroupsChange();
		
		group.deleteListeners.push( this._handleDeleteGroup );
		group.updateListeners.push( this._notifyGroupsChange );
		group.editNameListeners.push( this._editNameHandler );
		
		var groupTab = this.groupTabs[ group.id ];
		this.elm_addGroup.parentNode.insertBefore( groupTab.item,this.elm_addGroup );
		
		this.tabs.addTab( $( groupTab.elm_tab ));
		this.tabs.setActiveTab( group.id+"_user");
	},
	// Delete Groups
	handleDeleteGroup : function( group ){
		this.groups = this.groups.reject( function( g ) {
			return g.id == group.id;
		});
		
		setTimeout( function() {
			group.deleteListeners.remove( this._handleDeleteGroup );
			group.updateListeners.remove( this._notifyGroupsChange );
			group.editNameListeners.remove( this._editNameHandler );
		}.bind( this ),10 );
		
		delete this.groupTabs[group.id];
		delete this.groupContents[group.id];
		
		if( this.groups.length > 0 )
			this.tabs.setActiveTab( this.groups[0].id+"_user");
		
		this.notifyGroupsChange();
	},
	notifyGroupsChange: function() {
		var groupNames = this.groups.collect( function( group ) {
			return group.name;
		});
		
		if( groupNames.length != groupNames.uniq().length )
			throw new Error();
		
		if( this.groups.find( function( group ) {
			return group.name.replace(/\s/g,"").length == 0;
		})) throw new Error();
		
		this.groupsChangeListener();
		
		this.notifyCurrentGroupChange( this.currentGroup.name );
	},
	editNameOnStart: function() {
		this.elm_groupContentsLayout.addClassName("editing");
		if( Browser.isIE ) {
			setTimeout( function() {
				this.dragBlock.style.width = this.elm_groupContents.offsetWidth;
				this.dragBlock.style.height = this.elm_groupContents.offsetHeight;
			}.bind( this ),10 );
		}
	},
	editNameOnEnd: function() {
		this.elm_groupContentsLayout.removeClassName("editing");
	}
}

IS_Portal.GroupSettingPanel.GroupTab = Class.create();
IS_Portal.GroupSettingPanel.GroupTab.prototype = {
	initialize: function( group ) {
		this.group = group || {};
		
		this.group.deleteListeners.push( ( this._clean = this.clean.bind( this ) ));
		this.group.updateListeners.push( ( this._renderTitle = this.renderTitle.bind( this ) ));
	},
	render: function( container ){
		var item = this.elm_tab = this.item = document.createElement('li');
		item.id = "#"+this.group.id +'_tab';
		item.className = "tab";
		item.href = "#"+this.group.id + '_user';
		item.setAttribute("href",item.href );
		container.appendChild( item );
		
		//Create Group names
//		tab.appendChild( this.makeGroup(groupName, index) );
		var span = document.createElement("span");
		span.className = "title";
		item.appendChild( span );
		
		this.renderTitle( span )
		
		var input = this.elm_input = document.createElement("input");
		Event.observe( input,"blur",this.editCommitGroup.bind( this ) );
		span.appendChild( input );
		
		this.renderEditCommitIcon( span );
		this.renderEditIcon( span );
		this.renderDeleteIcon( span );
	},
	renderTitle: function( container ) {
		if( !container && !this.elm_title )
			return;
		
		if( !this.elm_title ) {
			this.elm_title = document.createTextNode( this.group.name );
			container.appendChild( this.elm_title );
		}
		
		var gn = this.group.name;
		if( gn.length > 40 )
			gn = gn.substring( 0,40 )+"...";
		
		this.elm_title.nodeValue = gn;
	},
	renderEditIcon: function( container ) {
		var icon = this.elm_editIcon = document.createElement("span");
		icon.className = "icon edit";
		container.appendChild( icon );
		
		Event.observe( icon,"click",this.editGroup.bind( this ),true);
	},
	renderEditCommitIcon: function( container ) {
		var icon = this.elm_editCommitIcon = document.createElement("span");
		icon.className = "icon edit-commit";
		container.appendChild( icon );
		
		Event.observe( icon,"click",this.editCommitGroup.bind( this ),true);
	},
	renderDeleteIcon: function( container ) {
		var icon = this.elm_deleteIcon = document.createElement("span");
		icon.className = "icon delete";
		container.appendChild( icon );
		
		Event.observe( icon,"mousedown",this.handleDeleteGroup.bind( this ),true );
	},
	editGroup: function() {
		if( this.elm_tab.hasClassName("editing"))
			return;
		
		this.elm_title.nodeValue = "";
		
		this.elm_tab.addClassName("editing");
		
		this.elm_input.value = this.group.name;
		this.elm_input.focus();
		
		this.group.notifyStartEditName();
	},
	editCommitGroup: function() {
		this.elm_tab.removeClassName("editing");
		
		var oldValue = this.group.name;
		var newValue = this.elm_input.value;
		
		this.group.name = newValue;
		this.elm_input.value = "";
		
		try {
			this.group.notifyUpdate();
		} catch( ex ) {
			this.group.name = oldValue;
		}
		
		this.renderTitle();
		
		this.group.notifyEndEditName();
	},
	handleDeleteGroup: function( event ) {

		if( !confirm( IS_R.ms_confirmDeleteGroup ))
			return;
		
		this.group.notifyDelete();
		
		if( event )
			event.stop();
	},
	clean: function() {
		if( this.item.parentNode )
			this.item.parentNode.removeChild( this.item );
		
		Event.stopObserving( this.elm_input );
		Event.stopObserving( this.elm_editIcon );
		Event.stopObserving( this.elm_editCommitIcon );
		Event.stopObserving( this.elm_deleteIcon );
		
		setTimeout( function() {
			this.group.updateListeners.remove( this._renderTitle );
			this.group.deleteListeners.remove( this._clean );
		}.bind( this ),10 );
	}
}

IS_Portal.GroupSettingPanel.GroupContent = Class.create();
IS_Portal.GroupSettingPanel.GroupContent.prototype = {
	initialize: function( group ) {
		this.group = group || [];
		
		this.group.updateListeners.push( ( this._render = this.render.bind( this ) ));
		this.group.deleteListeners.push( ( this._clean = this.clean.bind( this ) ));
	},
	//Create user list
	render : function( container ){
		var div = this.groupUserDiv;
		if( !div ) {
			div = this.groupUserDiv = $( document.createElement("div"));
			div.id = this.group.id +'_user';
			div.className = 'content';
			container.appendChild( div );
		} else {
			Sortable.destroy( div );
			div.innerHTML = "";
		}
		
		if( this.group.users.length > 0 ) {
			for(var i=0; i<this.group.users.length; i++){
				var user = this.group.users[i];
				
				this.makeUser( user,div );
			}
			
			Sortable.create(div, { tag : "div", onUpdate:this.updateUserOrder.bind(this)});
		} else {
			this.makeNoUser();
		}
	},
	makeNoUser: function() {
		var div = this.groupUserDiv;if( !div ) return;
		div.innerHTML = "";
		
		var noUser = document.createElement("div");
		noUser.className = "no-user";

		noUser.innerHTML = IS_R.lb_userNotAddedGroup;
		div.appendChild( noUser );
	},
	//In case of adding users
	makeUser : function( user,container ){
		var div = container || this.groupUserDiv;
		if( div.firstChild && !div.firstChild.hasClassName("user") )
			div.innerHTML = "";
		
		var userDiv = document.createElement("div");
		userDiv.className = "user";
		userDiv.id = this.group.id + '_' + user.uid;
		div.appendChild(userDiv);
		
		this.renderUserName( userDiv,user );
		this.renderUserAttr( userDiv,user,"mail" );
		this.renderUserAttr( userDiv,user,"belong" );
		userDiv.appendChild( this.makeUserDel( user ));
		
		IS_Event.observe( userDiv, 'mouseover', this.mouseover.bind(this, userDiv), false, this.group.id + "_userList");
		IS_Event.observe( userDiv, 'mouseout', this.mouseout.bind(this, userDiv), false, this.group.id + "_userList");
		
		if( div.childNodes.length % 2 == 1 )
			userDiv.addClassName("odd");
	},
		
	//In case of viewing all users
	renderUserName : function( container,user ){
		var value = user.name || "";
		if( value.length > 40 )
			value = String( value ).substring(0,40)+"...";
		
		if( user.uid && value != user.uid ) {
			var uid = user.uid || "";
			if( uid.length > 40 )
				uid = String( uid ).substring(0,40)+"...";
			
			value += "("+uid +")";
		}
		
		var span = document.createElement("span");
		span.appendChild( document.createTextNode( value ) );
		container.appendChild( span );
	},

	//In case of user organizations
	renderUserAttr : function( container,user,property ){
		var value = user[property];
		if( !value )
			return;
		
		var span = document.createElement("span");
//		span.appendChild( document.createTextNode( "《" + userObj.orgName + "》" ) );
		span.appendChild( document.createTextNode(IS_R.getResource( IS_R.lb_any,[ value ] )));
		container.appendChild( span );
	},
	
	//In case of deleting users
	makeUserDel : function( userObj){
		var u_delButton = document.createElement("img");

		u_delButton.title = IS_R.lb_deleteUser;
		u_delButton.src = imageURL + 'trash.gif';
		u_delButton.style.cursor = 'pointer';
		IS_Event.observe( u_delButton, 'click', this.deleteUser.bind(this,  userObj), false, u_delButton.id, this.group.id + "_userList");
		return u_delButton;
	},

	// Delete users
	deleteUser : function( user ){

		if( !confirm( IS_R.ms_confirmDeleteUser ))
			return;
		
		this.group.users.remove( user );
		
		//reset the array with deleted users for the intended group
		this.group.notifyUpdate();
	},
	// Function called if the order changes by rearraging
	updateUserOrder : function(element){
		// Create empty array for the user who belongs to the rearranged group
		var modified = [];
		for(var i=0; i<element.childNodes.length; i++){
			var userDiv = element.childNodes[i];
			
			var uid = userDiv.id.substring( this.group.id.length +1 );
			var user = this.group.users.find( function( user ) {
				return ( user && user.uid == uid );
			});
			
			modified.push( user );
		}
		
		this.group.users.clear();
		for( var i=0;i<modified.length;i++ )
			this.group.users.push( modified[i] );
		
		setTimeout( this.group.notifyUpdate.bind( this.group ),0 );
	},
	mouseover : function( item ){
		item.addClassName("mouseover");
	},
	mouseout : function( item ){
		item.removeClassName("mouseover");
	},
	clean: function() {
		Sortable.destroy( this.groupUserDiv );
		
		if( this.groupUserDiv.parentNode )
			this.groupUserDiv.parentNode.removeChild( this.groupUserDiv );
		
		IS_Event.unloadCache( this.group.id + "_userList" );
		
		setTimeout( function() {
			this.group.updateListeners.remove( this._render );
			this.group.deleteListeners.remove( this._clean );
			this.group.editNameListeners.remove( this._editNameHandler );
		}.bind( this ),10 );
	}
}

IS_Portal.UserSearchPanel = Class.create();
IS_Portal.UserSearchPanel.prototype = {
	initialize: function( opt ) {
		this.id = opt.id || "";
		this.moduleConfs = opt.moduleConfs || {}
		$H( this.moduleConfs ).keys().each( function( key ) {
			var moduleConf = Object.extend( {
				labels: {

					name: IS_R.lb_name,

					mail: IS_R.lb_mail,

					belong: IS_R.lb_belong
				}
			},this.moduleConfs[key] );
			
			moduleConf.conditions = [
				"name",
				( this.moduleConfs[key].mailOmit ? undefined : "mail" ),
				( this.moduleConfs[key].belongOmit ? undefined : "belong")
			];
			
			delete moduleConf["mailOmit"];
			delete moduleConf["belongOmit"];
			
			this.moduleConfs[ key ] = moduleConf;
		},this );
		
		this.groupName = opt.CURRENT_GROUP_NAME || "";
		this.description = opt.description || "";
		
		this.notifyAddUser = opt.addUserListener || function() {};
		this.notifySearchComplete = opt.searchCompleteListener || function() {};
	},
	render: function( container ) {
		var div = document.createElement("div");
		div.className = "user-search";
		container.appendChild( div );
		
		var form = document.createElement("div");
		form.className = "form";
		div.appendChild( form );
		
		if( this.moduleConfs.length > 0 ) {
			this.renderUserSearchBox( form );
		} else {
			this.renderUserAddBox( form );
		}
		
		var userResultBox = document.createElement("div");
		userResultBox.className = "results";
		userResultBox.id = this.id +"_userListDiv";
		div.appendChild( userResultBox );
	},
	//Create box to search users
	renderUserSearchBox : function(userSearchDiv){


		if(this.moduleConfs.length > 1)
			this.renderUserSearchModuleSelect( userSearchDiv,this.moduleConfs );
		
		// Right side: for search button
		var userSearchImgTd = document.createElement("span");
		var userSearchImg = document.createElement("input");
		userSearchImg.id = this.id + '_userSearchIcon';
		//userSearchImg.src = imageURL + "zoom.gif";
		userSearchImg.type = 'button';
		//alert(this.moduleConfs[0].tabname);
		userSearchImg.value = IS_R.lb_search;//Search users
		userSearchImg.style.cursor = 'pointer';
		//userSearchImg.style.display = 'none';
		var userSearchingImg = document.createElement("img");
		userSearchingImg.id = this.id + '_userSearchingIcon';
		userSearchingImg.src = imageURL + 'ajax-loader.gif';
		userSearchingImg.style.display = 'none';
		userSearchImgTd.appendChild(userSearchImg);
		userSearchImgTd.appendChild(userSearchingImg);

		this.GROUP_CAL_FORM_INDEX = 0;
		var userSearchTxtTd = document.createElement("span");
		for(var j =0 ; j < this.moduleConfs.length ; j++){
			var moduleConf = this.moduleConfs[j];
			this.renderUserSearchModuleForm( userSearchTxtTd,moduleConf,((j == 0) ? "" : "none") );
		}
		
		userSearchDiv.appendChild(userSearchTxtTd);
		userSearchDiv.appendChild(userSearchImgTd);
		
		IS_Event.observe( userSearchImg,'click',this.handleUserSearch.bind(this),false,this.id);
	},
	
	renderUserSearchModuleSelect: function( container,moduleConfs ) {
		var searchSelect = document.createElement('select');
		searchSelect.id = this.id + "_userSearchSelect";
		
		for(var i = 0; i < moduleConfs.length; i++){
			var moduleConf = moduleConfs[i];
			
			var searchOption = document.createElement('option');
			searchOption.value = moduleConf.name;
			searchOption.innerHTML = moduleConf.name;
			searchSelect.appendChild(searchOption);
		}
		
		IS_Event.observe( searchSelect,'change', this.userSearchModuleChanged.bind(this),false,this.id);
		
		container.appendChild( searchSelect );
	},
	userSearchModuleChanged: function( event ) {
		var options = $( this.id + "_userSearchSelect").childNodes;
		for(var i = 0; i < options.length; i++){
			var moduleForm = $(this.id + "_userSearchDiv_" + options[i].value);
			if(options[i].selected){
				moduleForm.style.display = "";
				this.GROUP_CAL_FORM_INDEX = i;
			}else{
				moduleForm.style.display = "none";
			}
		}
	},
	renderUserSearchModuleForm: function( container,moduleConf,display ) {
		var module = moduleConf.module;
		var conditions = moduleConf.conditions;
		
		var userSearchDiv = document.createElement("span");
		userSearchDiv.className = "conditions";
		userSearchDiv.id = this.id + "_userSearchDiv_" +moduleConf.name;
		userSearchDiv.style.display = display;
		userSearchDiv.style.whiteSpace = "nowrap";
		for(var i = 0; i < conditions.length; i++){
			var condition = conditions[i];
			if( condition === undefined )
				continue;
			
			var conditionName = moduleConf.labels[ condition ];
			
			var userSearchSpan = document.createElement("span");
			userSearchSpan.className = "condition";
			userSearchSpan.appendChild( document.createTextNode( conditionName +": "));
			userSearchDiv.appendChild(userSearchSpan);
			
			var userSearchInput = document.createElement("input");
			userSearchInput.id = this.id + "_userSearch_"+module +"_" + condition;
			userSearchInput.type = "text";
			userSearchInput.style.width = "100px";
			userSearchSpan.appendChild(userSearchInput);
		}
		container.appendChild(userSearchDiv);
	},
	getUserSearchModuleFormData: function( moduleConf,doClear ) {
		var moduleName = moduleConf.module;
		var data = {
			_hasSearchCondition: false
		};
		
		var conditions = moduleConf.conditions;
		for( var i=0;i<conditions.length;i++ ) {
			var condition = conditions[i];
			if( condition === undefined )
				continue;
			
			var input = $( this.id + "_userSearch_" +moduleName+"_"+ condition);
			if( !input )
				continue;
			
			data[ condition ] = input.value || "";
			if( input.value )
				data._hasSearchCondition = true;
			
			if( doClear )
				input.value = "";
		}
		
		return data;
	},
	
	handleUserSearch : function() {
		var moduleConf = this.moduleConfs[this.GROUP_CAL_FORM_INDEX];
		var data = this.getUserSearchModuleFormData( moduleConf,true );
		
		if( !data._hasSearchCondition ) {

			alert( IS_R.lb_searchConditionNotSet );
			return false;
		} else {
			delete data._hasSearchCondition;
		}
		
		var userSearchingIcon = $(this.id + '_userSearchingIcon');
		if( userSearchingIcon )
			userSearchingIcon.style.display = "inline";
		
		this.userSearchRequest( moduleConf,data,this.makeResultBox.bind( this,moduleConf ) );
		
		return true;
	},
	userSearchRequest: function( moduleConf,conditions,handler ) {
		var opt = {
		    method: 'post' ,
		    asynchronous:true,
			parameters: {
				module: moduleConf.module,
				conditions: Object.toJSON( conditions )
			},
		    onSuccess: function(req){
				var res = req.responseText;
				if( res.length > 0 ){
					//View search results
					try {
						var users = eval( res );
						handler( users );
						
						msg.info( IS_R.ms_userSearchComplete );
						this.notifySearchComplete();
						
						return;
					} catch( ex ) {
						msg.warn( IS_R.ms_userSearchonException+":"+getText(ex));
						return;
					}
				}
				
				
				msg.warn( IS_R.ms_userSearchonException );
			}.bind( this ),
		    on404: function(t) {

		        alert( IS_R.ms_getdatafailed);
//		        msg.error('Error 404: location "' + hostPrefix + "/grp" + "srv" + '" was not found.');
		        msg.error( IS_R.getResource( IS_R.ms_getdataerror404,[hostPrefix+"/grpsrv"]));
		    },
		    onFailure: function(t) {

		        alert( IS_R.ms_getdatafailed );
//		        msg.error('Error ' + t.status + ' -- ' + t.statusText);
		        msg.error( IS_R.getResource( IS_R.ms_getdatafailure,[t.status,t.statusText]));
		    },
		    onException:function(r,t){

		        alert( IS_R.ms_getdatafailed);
//		    	msg.error(getText(t));
		    	msg.error( IS_R.getResource( IS_R.lb_any,[getText(t)]) );
		    },
			onComplete:function(r,t){
				$(this.id + "_userSearchingIcon").style.display = 'none';
			}.bind(this)
		};
		//AjaxRequest.invoke(hostPrefix + "/default_widgets.xml", opt);
		AjaxRequest.invoke(hostPrefix + "/usersearch", opt);
	},
	// Create box for registering users
	makeResultBox : function( moduleConf,users ){
		var userListDiv = $( this.id +"_userListDiv" );
		//Initialize box for registering users
		userListDiv.innerHTML = "";
		
		if( users.length > 0 ) {
			this.renderResultUsers( userListDiv,moduleConf,users );
			this.renderAddUserButton( userListDiv );
		} else {
			this.renderNoResult( userListDiv );
		}
		
		this.currentUsers = users;
	},
	renderResultUsers: function( container,moduleConf,users ) {
		var table = document.createElement("table");
		table.style.width = '100%';
		container.appendChild(table);
		var tbody = document.createElement("tbody");
		table.appendChild(tbody);
		
		var headers = [""].concat( moduleConf.conditions );
		
		var headerRow = document.createElement("tr");
		tbody.appendChild( headerRow );
		
		for( var i=0;i<headers.length;i++ ) {
			var header = headers[i];
			if( header === undefined ) continue;
			
			var headerName = moduleConf.labels[ header ];
			
			var headerCell = document.createElement("th");
			if( i == 0 ) {
				var checkbox = document.createElement("input");
				checkbox.type = "checkbox";
				headerCell.appendChild( checkbox );
				
				Event.observe( checkbox,"click",this.checkAllUsers.bind( this ));
			} else {
				headerCell.appendChild( document.createTextNode( headerName ) );
			}
			
			headerRow.appendChild( headerCell );
		}
		
		for(var i=0;i<users.length;i++){
			var row = document.createElement('tr');
			row.className = ( i % 2 == 0)?'groupItemOdd':'groupItemEven';
			tbody.appendChild( row );
			
			this.renderUser( row,headers,users[i] );
		}
	},
	renderAddUserButton: function( container ) {
		var addUserDiv = document.createElement("div");
		addUserDiv.className = "add-user";
		container.appendChild( addUserDiv );
		
		var button = this.elm_addUserButton = document.createElement("input");
		button.id = this.id +"user-search-add-user-button";
		button.type = 'button';

		button.value = IS_R.lb_addUser;
		addUserDiv.appendChild(button);
		
		IS_Event.observe( button, 'click', this.handleAddUser.bind(this), false, this.id);
		
		this.renderDescription( addUserDiv );
	},
	renderNoResult: function( container ) {

		container.appendChild( document.createTextNode( IS_R.lb_userSearchNoResult ))
	},
	//For Viewing CurrentGroupDisplay
	renderDescription : function( container ){
		var span = this.elm_description;
		if( container ) {
			span = this. elm_description = document.createElement("span");
			span.className = "description";
			container.appendChild( span );
		} else {
			if( !span )
				return;
			
			span.innerHTML = "";
		}
		
		span.appendChild( document.createTextNode( this.description ));
	},
	renderUser: function( container,headers,user ) {
		for( var i=0;i<headers.length;i++ ) {
			var header = headers[i];
			if( header === undefined ) continue;
			
			if( header == "") {
				var th = document.createElement("th");
				container.appendChild( th );
				
				var checkBox = document.createElement("input");
				checkBox.id = this.id+"_"+user.uid +"_check";
				checkBox.type = 'checkbox';
				th.appendChild( checkBox );
				
				th.className = "check";
			} else {
				var td = document.createElement("td");
				td.className = "property";
				container.appendChild( td );
				
				var value = user[header];
				if( header == "name" && value != user.uid )
					value = value+"("+user.uid+")";
				
				var span = document.createElement("span");
				span.appendChild( document.createTextNode( value || "" ));
				td.appendChild( span );
			}
		}
	},
	checkAllUsers: function( event ) {
		var checked = event.element().checked;
		for(var i=0; i<this.currentUsers.length; i++){
			var user = this.currentUsers[i];
			
			var check = $( this.id + '_' + user.uid + '_check'  );
			if( check )
				check.checked = checked;
		}
	},
	// Add users
	handleAddUser : function(){
		var users = [];
		var uids = [];
		
		for(var i=0; i<this.currentUsers.length; i++){
			var user = this.currentUsers[i];
			
			var check = $( this.id + '_' + user.uid + '_check'  );
			if( check.checked && !uids.contains( user.uid )){
				users.push( user );
				uids.push( user.uid );
			}
		}
		
		if( this.notifyAddUser )
			this.notifyAddUser( users );
	},
	renderUserAddBox: function( container ) {
		var input = this.elm_uidInput = document.createElement("input");
		container.appendChild( input );
		
		var addUserButton = document.createElement("input");
		addUserButton.type = "button";

		addUserButton.value = IS_R.lb_addUser;
		container.appendChild( addUserButton );
		
		Event.observe( addUserButton,"click",this.handleAddUid.bind( this ));
	},
	handleAddUid: function() {
		var uid = this.elm_uidInput.value || "";
		this.elm_uidInput.value = "";
		
		if( uid.replace(/[\s　]/g,"").length == 0 )
			return;
		
		if( this.notifyAddUser )
			this.notifyAddUser( [{
				uid: uid,
				name: uid,
				email: ""
			}] );
	}
}
