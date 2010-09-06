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

var IS_Commands =new Object();

/**
 * UpdatePropertyCommand
 */
IS_Commands.UpdatePropertyCommand = function(tabId, owner, field, value){
	this.obj = owner;
	this.tabId = tabId;
	this.id = tabId + "_" + owner.id + "_" + field;
	this.field = field;
	this.value = value;
};

IS_Commands.UpdatePropertyCommand.prototype.parseResponse = function(docEl){
	var attrs=docEl.attributes;
	var status=attrs.getNamedItem("status").value;
	if(status != "ok"){
		var reason = attrs.getNamedItem("message").value;
		msg.error("failed to update " + this.field + " to " + this.value + "\n\n" + reason);
	}
};

/**
 * UpdateWidgetProertyCommand
 */
IS_Commands.UpdateWidgetPropertyCommand = function(tabId, owner, field, value, parent){
	Object.extend(this, new IS_Commands.UpdatePropertyCommand(tabId, owner, field, value));
	this.type = "UpdateWidgetProperty";
	this.tabId = tabId;
	this.id = this.type + "_" + this.tabId + "_" + owner.id + "_" + field;
	
	this.widgetId = IS_Portal.getTrueId(owner.id);
	var tempParentId = parent ? parent : ((owner.parent)? owner.parent.id : "");
	this.parentId = IS_Portal.getTrueId(tempParentId);
};

IS_Commands.UpdateWidgetPropertyCommand.prototype.toRequestString = function(){
	return is_simpleXmlify({
		type:this.type,
		tabId:this.tabId,
		id:this.id,
		parent:this.parentId,
		widgetId:this.widgetId,
		field:this.field,
		value:this.value
	}, "command");
};

/**
 * RemoveWidgetProertyCommand
 */
IS_Commands.RemoveWidgetPropertyCommand = function(tabId, owner, field){
	this.type = "RemoveWidgetProperty";
	this.tabId = tabId;
	this.id = this.type + "_" + this.tabId + "_" + owner.id + "_" + field;
	this.widgetId = IS_Portal.getTrueId(owner.id);
	this.field = field;
};

IS_Commands.RemoveWidgetPropertyCommand.prototype.toRequestString = function(){
	return is_simpleXmlify({
		type:this.type,
		tabId:this.tabId,
		id:this.id,
		widgetId:this.widgetId,
		field:this.field
	}, "command");
};

/**
 * UpdateWidgetLocationCommand
 */
IS_Commands.UpdateWidgetLocationCommand = function(tabId, owner, targetColumn, sibling, parent){
	this.obj = owner;
	this.type = "UpdateWidgetLocation";
	this.tabId = tabId;
	this.id = this.type + "_" + tabId + "_" + owner.id;
	this.targetColumn = (targetColumn)  ? targetColumn : "";
	this.sibling = sibling;
	this.widgetId = IS_Portal.getTrueId(owner.id);
	var tempParentId = parent ? parent.id : "";
	this.parentId = IS_Portal.getTrueId(tempParentId);
};

IS_Commands.UpdateWidgetLocationCommand.prototype.parseResponse = function(docEl){
	var attrs=docEl.attributes;
	var status=attrs.getNamedItem("status").value;
	if(status != "ok"){
		var reason = attrs.getNamedItem("message").value;
		msg.error("move widget " + this.obj.id + " to after " + this.sibling + " of " + this.targetColumn + " column\n\n" + reason);
	}
};

IS_Commands.UpdateWidgetLocationCommand.prototype.toRequestString = function(){
	return is_simpleXmlify({
		type:this.type,
		tabId:this.tabId,
		id:this.id,
		parent:this.parentId,
		widgetId:this.widgetId,
		targetColumn:this.targetColumn,
		sibling:this.sibling
	}, "command");
};

/**
 * UpdateWidgetTabLocationCommand
 */
IS_Commands.UpdateWidgetTabLocationCommand = function(tabIdFrom, tabIdTo, owner){
	this.obj = owner;
	this.type = "UpdateWidgetTabLocation";
	this.tabIdFrom = tabIdFrom;
	this.tabIdTo = tabIdTo;
	this.id = this.type + "_" + tabIdFrom + "_" + tabIdTo + "_" + owner.id;
	this.widgetId = IS_Portal.getTrueId(owner.id);
};

IS_Commands.UpdateWidgetTabLocationCommand.prototype.parseResponse = function(docEl){
	var attrs=docEl.attributes;
	var status=attrs.getNamedItem("status").value;
	if(status != "ok"){
		var reason = attrs.getNamedItem("message").value;
		msg.error("move widget from tab " + this.obj.id + " to after " + this.sibling + " of " + this.targetColumn + " column\n\n" + reason);
	}
};

IS_Commands.UpdateWidgetTabLocationCommand.prototype.toRequestString = function(){
	return is_simpleXmlify({
		type:this.type,
		tabIdFrom:this.tabIdFrom,
		tabIdTo:this.tabIdTo,
		id:this.id,
		widgetId:this.widgetId
	}, "command");
};

/**
 * UpdateWidgetPreference
 */
IS_Commands.UpdateWidgetPrefernceCommand = function(tabId, owner, field, value){
	this.obj = owner;
	this.type = "UpdateWidgetPreference";
	this.tabId = tabId;
	this.id = this.type + "_" + this.tabId + "_" + owner.id;
	this.field = field;
	this.value = value;
	
	this.widgetId = IS_Portal.getTrueId(owner.id);
};

IS_Commands.UpdateWidgetPrefernceCommand.prototype.parseResponse = function(docEl){
	var attrs=docEl.attributes;
	var status=attrs.getNamedItem("status").value;
	if(status != "ok"){
		var reason = attrs.getNamedItem("message").value;
		msg.error("modify display feed in MuliRSSWidget " + this.obj.id + ". \n\n" + reason);
	}
};

IS_Commands.UpdateWidgetPrefernceCommand.prototype.toRequestString = function(){
	return is_simpleXmlify({
		type:this.type,
		tabId:this.tabId,
		id:this.id,
		widgetId:this.widgetId,
		field:this.field,
		value:this.value
	}, "command");
};

/**
 * UpdateCheckedFeedCommand
 */
/*
IS_Commands.UpdateCheckedFeedCommand = function(tabId, owner, feed){
	this.obj = owner;
	this.type = "UpdateCheckedFeed";
	this.tabId = tabId;
	this.id = this.type + "_" + this.tabId + "_" + owner.id;
	this.checked = "";
	for(var i = 0; i < feed.length;i++){
		if(getBooleanValue(feed[i].isChecked) &&  feed[i].property.relationalId == IS_Portal.getTrueId(owner.id, owner.widgetType)){
			if(this.checked !== ""){
				this.checked += ",";
			}
			this.checked += feed[i].id;
		}
	}

	this.widgetId = IS_Portal.getTrueId(owner.id);
};

IS_Commands.UpdateCheckedFeedCommand.prototype.parseResponse = function(docEl){
	var attrs=docEl.attributes;
	var status=attrs.getNamedItem("status").value;
	if(status != "ok"){
		var reason = attrs.getNamedItem("message").value;
		msg.error("modify display feed in MuliRSSWidget " + this.obj.id + ". \n\n" + reason);
	}
};

IS_Commands.UpdateCheckedFeedCommand.prototype.toRequestString = function(){
	return is_simpleXmlify({
		type:this.type,
		tabId:this.tabId,
		id:this.id,
		widgetId:this.widgetId,
		checked:this.checked
	}, "command");
};
*/

/**
 * AddTabCommand
 */
IS_Commands.AddTabCommand = function(tabId, tabName, tabType, numCol){
	this.type = "AddTab";
	this.tabId = tabId;
	this.tabName = tabName;
	this.id = this.type + "_" + tabId;
	this.tabType = tabType;
	this.numCol = numCol;
};

IS_Commands.AddTabCommand.prototype.toRequestString = function(){
	return is_simpleXmlify({
		type:this.type,
		id:this.id,
		tabId:this.tabId,
		tabName:this.tabName,
		tabType:this.tabType,
		numCol:this.numCol
	}, "command");
};

IS_Commands.AddTabCommand.prototype.parseResponse = function(docEl){
	var attrs=docEl.attributes;
	var status=attrs.getNamedItem("status").value;
	if(status != "ok"){
		var reason = attrs.getNamedItem("message").value;
		msg.error("modify display feed in MuliRSSWidget " + this.obj.id + ". \n\n" + reason);
	}
};

/**
 * RemoveTabCommand
 */
IS_Commands.RemoveTabCommand = function(tabId){
	this.type = "RemoveTab";
	this.tabId = tabId;
	this.id = this.type + "_" + tabId;
};

IS_Commands.RemoveTabCommand.prototype.toRequestString = function(){
	return is_simpleXmlify({
		type:this.type,
		id:this.id,
		tabId:this.tabId
	}, "command");
};

IS_Commands.RemoveTabCommand.prototype.parseResponse = function(docEl){
	var attrs=docEl.attributes;
	var status=attrs.getNamedItem("status").value;
	if(status != "ok"){
		var reason = attrs.getNamedItem("message").value;
		msg.error("modify display feed in MuliRSSWidget " + this.obj.id + ". \n\n" + reason);
	}
};

/**
 * AddWidgetCommand
 */
IS_Commands.AddWidgetCommand = function(tabId, owner, targetColumn, sibling, widgetConf, parentId, menuId, ginstid){
	this.obj = owner;
	this.type = "AddWidget";
	this.tabId = tabId;
	this.id = this.type + "_" + tabId + "_" + owner.id;
	this.targetColumn = targetColumn;
	this.sibling = sibling;
	this.menuId = menuId || "";
	if(ginstid)this.ginstid  = ginstid;
	
	this.widgetId = IS_Portal.getTrueId(owner.id);
	this.widgetConf = widgetConf;
	//var tempParentId = parent ? parent : ((owner.parent)? owner.parent.id : "");
	//this.parentId = IS_Portal.getTrueId(tempParentId);
	this.parentId = parentId;
};

IS_Commands.AddWidgetCommand.prototype.toRequestString = function(){
	return is_simpleXmlify({
		type:this.type,
		id:this.id,
		parent:this.parentId,
		menuId:this.menuId,
		ginstid:this.ginstid,
		tabId:this.tabId,
		widgetId:this.widgetId,
		targetColumn:this.targetColumn,
		widgetConf:this.widgetConf
//		sibling:this.siblin
	}, "command");
};

IS_Commands.AddWidgetCommand.prototype.parseResponse = function(docEl){
	var attrs=docEl.attributes;
	var status=attrs.getNamedItem("status").value;
	if(status != "ok"){
		var reason = attrs.getNamedItem("message").value;
		msg.error("modify display feed in MuliRSSWidget " + this.obj.id + ". \n\n" + reason);
	}
};

/**
 * UpdateWidgetCommand
 */
IS_Commands.UpdateWidgetCommand = function(tabId, owner, targetColumn, sibling, widgetConf, parentId){
	this.obj = owner;
	this.type = "UpdateWidget";
	this.tabId = tabId + "";
	this.id = this.type + "_" + tabId + "_" + owner.id;
	this.targetColumn = targetColumn;
	this.sibling = sibling;
	
	this.widgetId = IS_Portal.getTrueId(owner.id);
	this.widgetConf = widgetConf;
	//var tempParentId = parent ? parent : ((owner.parent)? owner.parent.id : "");
	//this.parentId = IS_Portal.getTrueId(tempParentId);
	this.parentId = parentId;
};

IS_Commands.UpdateWidgetCommand.prototype.toRequestString = function(){
	return is_simpleXmlify({
		type:this.type,
		id:this.id,
		parent:this.parentId,
		tabId:this.tabId,
		widgetId:this.widgetId,
		targetColumn:this.targetColumn,
		widgetConf:this.widgetConf
//		sibling:this.siblin
	}, "command");
};

IS_Commands.UpdateWidgetCommand.prototype.parseResponse = function(docEl){
	var attrs=docEl.attributes;
	var status=attrs.getNamedItem("status").value;
	if(status != "ok"){
		var reason = attrs.getNamedItem("message").value;
		msg.error("modify display feed in MuliRSSWidget " + this.obj.id + ". \n\n" + reason);
	}
};
 

/**
 * AddMultiWidgetCommand
 */
IS_Commands.AddMultiWidgetCommand = function(tabId, owner, targetColumn, sibling, widgetConf, subWidgetConfList, parentId, menuId){
	this.obj = owner;
	this.type = "AddMultiWidget";
	this.tabId = tabId;
	this.id = this.type + "_" + tabId + "_" + owner.id;
	this.targetColumn = targetColumn;
	this.sibling = sibling;
	this.menuId = menuId || "";
	
	this.widgetId = IS_Portal.getTrueId(owner.id);
	this.widgetConf = widgetConf;
	this.subWidgetConfList = subWidgetConfList;
	//var tempParentId = parent ? parent : ((owner.parent)? owner.parent.id : "");
	//this.parentId = IS_Portal.getTrueId(tempParentId);
	this.parentId = parentId;
};

IS_Commands.AddMultiWidgetCommand.prototype.toRequestString = function(){
	return is_simpleXmlify({
		type:this.type,
		id:this.id,
		parent:this.parentId,
		menuId:this.menuId,
		tabId:this.tabId,
		widgetId:this.widgetId,
		targetColumn:this.targetColumn,
		widgetConf:this.widgetConf,
		subWidgetConfList:this.subWidgetConfList
//		sibling:this.siblin
	}, "command");
};

IS_Commands.AddMultiWidgetCommand.prototype.parseResponse = function(docEl){
	var attrs=docEl.attributes;
	var status=attrs.getNamedItem("status").value;
	if(status != "ok"){
		var reason = attrs.getNamedItem("message").value;
		msg.error("modify display feed in MuliRSSWidget " + this.obj.id + ". \n\n" + reason);
	}
};

/**
 * UpdateWidgetCommand
IS_Commands.UpdateWidgetCommand = function(tabId, owner, targetColumn, sibling, widgetConf){
	Object.extend(this, new IS_Commands.AddWidgetCommand(tabId, owner, targetColumn, sibling, widgetConf));
	this.type = "UpdateWidget";
	this.id = this.type + "_" + tabId + "_" + owner.id;
}
 */

/**
 * RemoveWidgetCommand
 */
IS_Commands.RemoveWidgetCommand = function(tabId, owner, parent){
	this.obj = owner;
	this.type = "RemoveWidget";
	this.tabId = tabId;
	this.id = this.type + "_" + this.tabId + "_" + owner.id;
	if(owner.widgetConf.deleteDate && owner.widgetConf.deleteDate != 0)
	  this.deleteDate = owner.widgetConf.deleteDate;
	
	this.widgetId = IS_Portal.getTrueId(owner.id);
	var tempParentId = (parent)? parent.id : "";
	this.parentId = IS_Portal.getTrueId(tempParentId);
};

IS_Commands.RemoveWidgetCommand.prototype.parseResponse = function(docEl){
	var attrs=docEl.attributes;
	var status=attrs.getNamedItem("status").value;
	if(status != "ok"){
		var reason = attrs.getNamedItem("message").value;
		msg.error("remove widget "+ this.id +" is failed.\n\n" + reason);
	}
};

IS_Commands.RemoveWidgetCommand.prototype.toRequestString = function(){
	return is_simpleXmlify({
		type:this.type,
		tabId:this.tabId,
		parent:this.parentId,
		id:this.id,
		deleteDate:this.deleteDate,
		isRelated:this.isRelated,
		widgetId:this.widgetId
	}, "command");
};

/**
 * EmptyWidgetCommand
 */
IS_Commands.EmptyWidgetCommand = function(widgetConf, tabId){
	this.type = "EmptyWidget";
	this.tabId = tabId;
	this.deleteDate = widgetConf.deleteDate ? widgetConf.deleteDate : 0;
	this.id = this.type + "_" + this.tabId + "_" + widgetConf.id;
	this.widgetId = IS_Portal.getTrueId(widgetConf.id);
};

IS_Commands.EmptyWidgetCommand.prototype.parseResponse = function(docEl){
	var attrs=docEl.attributes;
	var status=attrs.getNamedItem("status").value;
	if(status != "ok"){
		var reason = attrs.getNamedItem("message").value;
		msg.error("remove widget "+ this.id +" is failed.\n\n" + reason);
	}
};

IS_Commands.EmptyWidgetCommand.prototype.toRequestString = function(){
	return is_simpleXmlify({
		type:this.type,
		tabId:this.tabId,
		deleteDate:this.deleteDate,
		id:this.id,
		widgetId:this.widgetId
	}, "command");
};

/**
 * EmptyAllWidgetCommand
 */
IS_Commands.EmptyAllWidgetCommand = function(){
	this.type = "EmptyAllWidget";
	this.id = this.type;
};

IS_Commands.EmptyAllWidgetCommand.prototype.parseResponse = function(docEl){
	var attrs=docEl.attributes;
	var status=attrs.getNamedItem("status").value;
	if(status != "ok"){
		var reason = attrs.getNamedItem("message").value;
		msg.error("remove widget "+ this.id +" is failed.\n\n" + reason);
	}
};

IS_Commands.EmptyAllWidgetCommand.prototype.toRequestString = function(){
	return is_simpleXmlify({
		type:this.type,
		id:this.id
	}, "command");
};

/**
 * AddLogCommand
 */
IS_Commands.AddLogCommand = function(logType,url,rssUrl){
	this.type = "AddLog";
	this.logType = logType;
	this.url = url;
	this.rssUrl = rssUrl;
	
//	this.id = this.type + "_" + this.logType + "_" + this.url;
	this.id = this.type + "_" + this.logType + "_" + this.url + "_" + this.rssUrl;
};

IS_Commands.AddLogCommand.prototype.parseResponse = function(docEl){
	var attrs=docEl.attributes;
	var status=attrs.getNamedItem("status").value;
	if(status != "ok"){
		var reason = attrs.getNamedItem("message").value;
		msg.error("add log "+ this.id +" is failed.\n\n" + reason);
	}
};

IS_Commands.AddLogCommand.prototype.toRequestString = function(){
	return is_simpleXmlify({
		type:this.type,
		id:this.id,
		logType:this.logType,
		url:this.url,
		rssUrl:this.rssUrl
	}, "command");
};

/**
 * UpdatePreferenceCommand
 */
IS_Commands.UpdatePreferenceCommand = function(field, value){
	this.type = "UpdatePreference";
	this.id = this.type + "_" + field;
	this.field = field;
	this.value = value;
};

IS_Commands.UpdatePreferenceCommand.prototype.parseResponse = function(docEl){
	var attrs=docEl.attributes;
	var status=attrs.getNamedItem("status").value;
	if(status != "ok"){
		var reason = attrs.getNamedItem("message").value;
		msg.error("update preference "+ this.id +" is failed.\n\n" + reason);
	}
};

IS_Commands.UpdatePreferenceCommand.prototype.toRequestString = function(){
	return is_simpleXmlify({
		type:this.type,
		id:this.id,
		field:this.field,
		value:this.value
	}, "command");
};

/**
 * UpdateTabPreferenceCommand
 */
IS_Commands.UpdateTabPreferenceCommand = function(tabId, field, value){
	this.type = "UpdateTabPreference";
	this.id = this.type + "_" + tabId + "_" + field;
	this.tabId = (tabId)? tabId : "";
	this.field = field;
	this.value = value;
};

IS_Commands.UpdateTabPreferenceCommand.prototype.parseResponse = function(docEl){
	var attrs=docEl.attributes;
	var status=attrs.getNamedItem("status").value;
	if(status != "ok"){
		var reason = attrs.getNamedItem("message").value;
		msg.error("update tabPreference "+ this.id +" is failed.\n\n" + reason);
	}
};

IS_Commands.UpdateTabPreferenceCommand.prototype.toRequestString = function(){
	return is_simpleXmlify({
		type:this.type,
		id:this.id,
		tabId:this.tabId,
		field:this.field,
		value:this.value
	}, "command");
};

/**
 * ExecLogoffProcessCommand
 */
IS_Commands.ExecLogoffProcessCommand = function(){
	this.type = "ExecLogoffProcess";
	this.id = this.type;
	this.field = "logoffDateTime";
};

IS_Commands.ExecLogoffProcessCommand.prototype.parseResponse = function(docEl){
	var attrs=docEl.attributes;
	var status=attrs.getNamedItem("status").value;
	if(status != "ok"){
		var reason = attrs.getNamedItem("message").value;
		msg.error("execute logoffProcess "+ this.id +" is failed.\n\n" + reason);
	}
};

IS_Commands.ExecLogoffProcessCommand.prototype.toRequestString = function(){
	return is_simpleXmlify({
		type:this.type,
		id:this.id,
		field:this.field
	}, "command");
};

/**
 * UpdateRssMetaCommand
 */
IS_Commands.UpdateRssMetaCommand = function(contentType, url, rssUrl, title, pubDate){
	/*
	this.type = "UpdateRssMeta";
	this.contentType = contentType;
	this.url = url;
	this.rssUrl = rssUrl;
	this.title = title;
	this.pubDate = pubDate;
	*/
//	this.id = this.type + "_" + this.url + "_" + this.rssUrl;
};

IS_Commands.UpdateRssMetaCommand.prototype.parseResponse = function(docEl){
	var attrs=docEl.attributes;
	var status=attrs.getNamedItem("status").value;
	if(status != "ok"){
		var reason = attrs.getNamedItem("message").value;
		msg.error("update rssmeta "+ this.id +" is failed.\n\n" + reason);
	}
};

IS_Commands.UpdateRssMetaCommand.prototype.toRequestString = function(){
	return is_simpleXmlify({
		type:this.type,
		id:this.id,
		contentType:this.contentType,
		url:this.url,
		rssUrl:this.rssUrl,
		title:this.title,
		pubDate:this.pubDate
	}, "command");
};

/**
 * UpdateRssMetaRefreshCommand
 */
IS_Commands.UpdateRssMetaRefreshCommand = function(contentType, url, title){
	/*
	this.type = "UpdateRssMetaRefresh";
	this.contentType = contentType;
	this.url = url;
	this.title = title;
	this.count = IS_Portal.autoRefCountList[this.url];
	*/
//	this.id = this.type + "_" + this.url;
};

IS_Commands.UpdateRssMetaRefreshCommand.prototype.parseResponse = function(docEl){
	var attrs=docEl.attributes;
	var status=attrs.getNamedItem("status").value;
	if(status != "ok"){
		var reason = attrs.getNamedItem("message").value;
		msg.error("update rssmetaRefresh "+ this.id +" is failed.\n\n" + reason);
	}
};

IS_Commands.UpdateRssMetaRefreshCommand.prototype.toRequestString = function(){
	IS_Portal.autoRefCountList[this.url] = 0;
	
	return is_simpleXmlify({
		type:this.type,
		id:this.id,
		contentType:this.contentType,
		url:this.url,
		title:this.title,
		count:this.count
	}, "command");
};

/**
 * AddKeywordCommand
 */

IS_Commands.AddKeywordCommand = function(keyword){
	var keywordSep = [];

/*
	for (var i=0; i<keyword.length; i++) {
		keyword = keyword.replace("　", " ");
	}
	
	var tempArray = keyword.split(" ");

	for(var i = 0; i < tempArray.length; i++){
		if(tempArray[i].length > 0 && tempArray[i]!=" ") {keywordSep.push(tempArray[i]);}
	}
*/
	
	for (var i=0; i<keyword.length; i++) {
		keyword = keyword.replace(/　/g, " ");
	}
	var tempArray = keyword.split(" ");
	for(var i = 0; i < tempArray.length; i++){
		if(tempArray[i].length > 0) {keywordSep.push(tempArray[i]);}
	}
	
	

	keywordSep.sort();
	var keywords = "";
	for (var i=0; i<keywordSep.length; i++) {
		if (i==0) keywords = keywordSep[i];
		else keywords = keywords + " " + keywordSep[i];
	}
	
	this.type = "AddKeyword";
	this.keyword = keywords;
	this.id = keywords;
};

IS_Commands.AddKeywordCommand.prototype.parseResponse = function(docEl){
	var attrs=docEl.attributes;
	var status=attrs.getNamedItem("status").value;
	if(status != "ok"){
		var reason = attrs.getNamedItem("message").value;
		msg.error("add keyword "+ this.id +" is failed.\n\n" + reason);
	}
};

IS_Commands.AddKeywordCommand.prototype.toRequestString = function(){
	return is_simpleXmlify({
		type:this.type,
		keyword:this.keyword,
		id:this.id
	}, "command");
};

function is_simpleXmlify(obj, tagname){
	var xml="<"+tagname;
	for (i in obj){
		if ( !(obj[i] instanceof Function) ){
			var value = obj[i];
			if(value == null || value == undefined)
				continue;
			if(typeof value == "string")
				value = escapeXMLEntity(value);
			xml+=" "+i+"=\""+value+"\"";
		}
	}
	xml+="/>";
	return xml;
}

/*
IS_Commands.xmlToString = function(node){
	if(node.nodeType == 3){
		return escapeXMLEntity(node.nodeValue);
	}
	var xml = '<' + node.nodeName ;
	if(node.attributes.length > 0){
		for(var i = 0; i < node.attributes.length;i++){
			var attr = node.attributes[i];
			xml += ' ' + attr.nodeName + '="' + escapeXMLEntity(attr.nodeValue) + '"';
		}
	}
	xml += '>';
	if(node.childNodes.length > 0){
		for(var i = 0; i < node.childNodes.length; i++){
			var child = node.childNodes[i];
			if(child.nodeType == 1){
				xml += IS_Commands.xmlToString(child);
			}else if(child.nodeType == 3){
				xml += escapeXMLEntity(child.nodeValue);
			}
		}
	}
	xml += '</' + node.nodeName + '>';
	return xml;
}
*/
