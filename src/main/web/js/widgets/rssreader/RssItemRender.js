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

IS_Widget.RssReaderDescriptionList = [];
IS_Widget.RssReaderDescriptionWithScrollList = [];
IS_Widget.RssReader.RssItemRender = function() {
	this.init = true;
	
	
};
IS_Widget.RssReader.RssItemRender.render = function (widget, tbodyNode, rssItem, br, pubDate, itemNumber) {
	var div = IS_Widget.RssReader.RssItemRender.getInstance().render({
		widget: widget,
		doLineFeed: function() { return br },
		showDatetime: function() { return pubDate } },rssItem,itemNumber );
	var tr = document.createElement("tr");
	var td = document.createElement("td");
	tr.appendChild( td );
	
	td.appendChild( div );
	
	if( tbodyNode )
		tbodyNode.appendChild( tr );
	
	return tr;
}
IS_Widget.RssReader.RssItemRender.createTable = function(row,col) {
	var table = document.createElement("table");
	table.style.width = "100%";
	table.style.height = "100%";
	table.style.padding = 0;
	table.style.margin = 0;
	table.style.borderCollapse = "collapse";
	var tBody= document.createElement("tbody");
	table.appendChild( tBody );
	for( var r=0;r<row;r++ ) {
		var tr = document.createElement("tr");
		tBody.appendChild( tr );
		for( var c=0;c<col;c++ ) {
			var td = document.createElement("td");
			tr.appendChild( td );
		}
	}
	
	return table;
}
IS_Widget.RssReader.RssItemRender.prototype.removeChildren = function( element ) {
	if( arguments.length == 0 )
		return;
	if( arguments.length > 1 ) {
		for( var i=0;i<arguments.length;i++ )
			this.removeChildren( arguments[i] );
	}
	while( element.firstChild )
		element.removeChild( element.firstChild )
}
IS_Widget.RssReader.RssItemRender.instances = {};
IS_Widget.RssReader.RssItemRender.availableKeys = [];
IS_Widget.RssReader.RssItemRender.no = 0;
IS_Widget.RssReader.RssItemRender.newInstance = function(){
	return new IS_Widget.RssReader.RssItemRender();
}
IS_Widget.RssReader.RssItemRender.getInstance = function(){
	var instance;
	if( this.availableKeys.length > 0 ) {// console.info("recycle");
		instance = this.instances[this.availableKeys.shift()];
	} else {// console.info("new");
		instance = this.newInstance();
		var key = this.no++;
		instance.key = key;
		
		this.instances[key] = instance;
	}
	
	return instance;
}
IS_Widget.RssReader.RssItemRender.releaseInstance = function( element ) {
	var key = element.__key__;
	if( element.parentNode )
		element.parentNode.removeChild( element );
	
	if( this.instances[ key ])
		this.availableKeys.push( key );
}
IS_Widget.RssReader.RssItemRender.getDefaultHeight = function( context ) {
	var fontSize = parseInt( IS_Portal.fontSize.substring( 0,IS_Portal.fontSize.length -1 ) );
	if( isNaN( fontSize ))
		fontSize = 100;
	
	var d = context.showDatetime();
	if( fontSize < 100 ) {
		h = 15 +( d ?16:2 ) -( Browser.isFirefox? 1:0.0 );
	} else if( fontSize == 100 ){
		h = 19 +( d ? 20:2 ) -( Browser.isFirefox ? 1.5:1 );
	} else {
		h = 21 +( d ? 23:2 ) -( Browser.isFirefox? 0:-0.5 );
	}
	
	return h;
}
IS_Widget.RssReader.RssItemRender.prototype.render = function ( context,rssItem, itemNumber) {
	var init = this.init;
	
	var widget = context.widget;
	var widgetObj = ( widget.parent )? widget.parent: widget;
	var br = context.doLineFeed();
	var pubDate = context.showDatetime();
	
	this.widget = widget;
	this.rssItem = rssItem;
	
	var opt = {
		rssItem : rssItem,
		br : br,
		pubDate : pubDate,
		itemNumber: itemNumber
	}
	
	if( init ) {
		this.itemDiv =document.createElement("div");
		this.itemDiv.style.width = "100%";
		this.itemDiv.style.clear = "both";
		//this.tr = document.createElement("tr");
		this.tr = this.itemDiv;
		this.tr.__key__ = this.key;
	}
	
	this.tr.id = widget.id + '_item_'+itemNumber;
	
	this.buildTitle( widget,opt );
	this.buildPubDate( widget,opt );
	
	this.changeLink = false;
	this.changeImg = false;
	
	this.buildRssDesc( widget,opt );
	
	if( !rssItem.description || ( ""+rssItem.description.replace(/\s/,"") ).length == 0 ) {
		this.rssDetail.style.display = this.rssDetail1.style.display = "none";
	} else {
		this.rssDetail.style.display = this.rssDetail1.style.display = "";
	}
	
	this.init = false;
	
	return this.tr;
};
IS_Widget.RssReader.RssItemRender.prototype.buildRssItemDiv = function( widget,opt,noLink ) {
	var init = this.init;
	
	var rssItem = opt.rssItem;
	var pubDate = opt.pubDate;
	var br = opt.br;
	var itemNumber = opt.itemNumber;
	
	var itemDisplay = widget.getUserPref("itemDisplay");
	
	if( init ) {
		//Prepare title link
		this.rssItemDiv = document.createElement("div");
		this.rssItemDiv.className = "rssItem";
		
		this.aTag = document.createElement('a');
		this.rssItemDiv.appendChild(this.aTag);
		IS_Event.observe(this.aTag, "click", this.aTagOnClick.bind( this ));
		
		this.textTitle = document.createElement("span");
		this.rssItemDiv.appendChild( this.textTitle );
		
		this.latestMark = document.createElement("img");
		this.latestMark.className = "latestMark";
		this.rssItemDiv.appendChild( this.latestMark );
	} else {
		this.removeChildren(
			this.textTitle,
			this.aTag );
	}
	
	if( init ) {
		this.customIconsDiv = document.createElement("div");
		this.customIconsDiv.style.display = "inline";
		this.rssItemDiv.appendChild( this.customIconsDiv );
	} else {
		this.removeChildren( this.customIconsDiv );
	}
	
	if( br && widget.content.rssItemCustomIcons ){
		var customIcons = rssItem.customIcons;
		if( !customIcons ) {
			customIcons = rssItem.customIcons = widget.content.rssItemCustomIcons(rssItem);
			
			for(var i = 0; i<customIcons.length; i++){
				customIcons[i].style.display = "inline";
				customIcons[i].innerHTML = "&nbsp;&nbsp;&nbsp;&nbsp;";
			}
		}
		
		for(var i = 0; i<customIcons.length; i++){
			this.customIconsDiv.appendChild(customIcons[i]);
		}
	}
	
	if( Browser.isIE ) {
		//Recalculate size forcibly because it is not recalculated
		if( this.rssItemDiv.style.width != "auto") {
			this.rssItemDiv.style.width = "auto";
		} else {
			this.rssItemDiv.style.width = "100%";
		}
	}
	

	this.rssTitle = (rssItem.title.length == 0)? IS_R.lb_notitle : rssItem.title;
	this.rssTitle = this.rssTitle.replace(/&nbsp;/g," ");	// For trouble with "&nbsp;" where line-break does not occur
	
	//this.rsslink.title = this.rssTitle;
	if(rssItem.link && rssItem.link.length > 0 && !noLink ) {
		this.aTag.style.display = "";
		this.textTitle.style.display = "none";
		
		this.aTag.href = rssItem.link;
//		aTag.innerHTML = rssTitle;
		this.aTag.appendChild(document.createTextNode(this.rssTitle));
		this.aTag.title = this.rssTitle;
		if( itemDisplay == "newwindow" )
			this.aTag.target="_blank";
		else if(itemDisplay == "inline")
			this.aTag.target="ifrm";
		else
			this.aTag.target = "";
	} else {
		this.aTag.style.display = "none";
		this.textTitle.style.display = "";
		this.textTitle.title = this.rssTitle;
		
		this.textTitle.appendChild( document.createTextNode( this.rssTitle ));
	}
	
	var isHotNews = IS_Widget.RssReader.isHotNews( widget.latestDatetime,rssItem );
	this.latestMark.src = imageURL +( isHotNews ? "sun_blink.gif":"sun.gif");
	
	this.latestMark.style.display = "none";
	var fontSize = parseInt( IS_Portal.fontSize.substring( 0,IS_Portal.fontSize.length -1 ) );
	if( isNaN( fontSize ))
		fontSize = 100;
	
	this.latestMark.style.overflow = "hidden"
	if( fontSize < 100 ) {
		this.latestMark.style.height = 13;
	} else {
		this.latestMark.style.height = "";
	}
	
	if( br ) {
		//this.rssItemDiv.style.display = "inline";
		this.rssItemDiv.style.lineHeight = "";
		this.rssItemDiv.style.height = "";
		this.rssItemDiv.style.overflow = "";
		
		if(IS_Widget.RssReader.isLatestNews(rssItem.rssDate))
			this.latestMark.style.display = "inline";
	} else {
		//this.rssItemDiv.style.display = "";
		this.rssItemDiv.style.lineHeight = "1.2em";
		this.rssItemDiv.style.height = "1.15em";
		this.rssItemDiv.style.overflow = "hidden";
	}
}
IS_Widget.RssReader.RssItemRender.prototype.aTagOnClick = function(e) {
	var rssItem = this.rssItem;
	var widget = this.widget;
	
	if(rssItem.rssUrls && widget.getUserPref("displayMode") != "category"){
		IS_Widget.mergeContentClicked(rssItem, this.aTag);
	}else{
		var startDateTime = (rssItem.rssDate)? rssItem.rssDate.getTime() : "";
		IS_Widget.contentClicked(rssItem.link,widget.getUserPref("url"),rssItem.title,
			startDateTime, this.aTag);
	}
}
IS_Widget.RssReader.RssItemRender.prototype.buildTitle = function( widget,opt ) {
	var init = this.init;
	
	var rssItem = opt.rssItem;
	var pubDate = opt.pubDate;
	var br = opt.br;
	var itemNumber = opt.itemNumber;
	
	this.buildRssItemDiv( widget,opt );
	
	if( init ) {
		this.latestMark1 = this.latestMark.cloneNode(true);
		this.latestMark1.style.display = "";
		this.latestMark1.className = "latestMark";
		
		this.rssDetail1 = this.createRssDetailLink();
		
		/*var row1 = document.createElement("div");
		this.itemDiv.appendChild( row1 );
		
		this.latestMark1.style.cssFloat = this.latestMark1.style.styleFloat = "right";
		this.latestMarkTd = this.latestMark1;
		this.rssDetail1.style.cssFloat = this.rssDetail1.style.styleFloat = "right";
		this.moreTd1 = this.rssDetail1;
		
		row1.appendChild( this.rssDetail1 );
		row1.appendChild( this.latestMark1 );
		row1.appendChild( this.rssItemDiv );*/
		
		var rssItemTable = IS_Widget.RssReader.RssItemRender.createTable( 1,2 );
		rssItemTable.cellPadding = 0;
		rssItemTable.cellSpacing = 0;
		rssItemTable.style.width = "auto";
		rssItemTable.style.height = "auto";
		this.rssItemTable = rssItemTable;
		
		var titleTd = rssItemTable.firstChild.firstChild.childNodes[0];
		titleTd.appendChild(this.rssItemDiv);
		
		this.latestMarkTd = rssItemTable.firstChild.firstChild.childNodes[1];
		this.latestMarkTd.style.width = '15px';
		this.latestMarkTd.appendChild(this.latestMark1);
		
		var titleTable = IS_Widget.RssReader.RssItemRender.createTable( 1,2 );
		titleTable.cellPadding = '1';
		titleTable.cellSpacing = 0;
		titleTable.style.height = "auto";
		titleTable.style.tableLayout = "fixed";
		this.itemDiv.appendChild( titleTable);
		
		titleTable.firstChild.firstChild.childNodes[0].appendChild( rssItemTable );
		this.moreTd1 = titleTable.firstChild.firstChild.childNodes[1];
		this.moreTd1.style.width = 2.5+"em"
		this.moreTd1.appendChild( this.rssDetail1 );
	} else {
		//this.removeChildren( this.moreTd1 );
	}
	
	var titleTr = this.rssItemTable.firstChild.firstChild;
	while( titleTr.childNodes.length > 2 )
		titleTr.removeChild( titleTr.lastChild );
	
	if( !br && widget.content.rssItemCustomIcons){
		var customIcons = rssItem.customIcons2;
		if( !customIcons ) {
			customIcons = rssItem.customIcons2 = widget.content.rssItemCustomIcons(rssItem);
			
			for(var i = 0; i<customIcons.length; i++){
				customIcons[i].style.display = "inline";
				customIcons[i].innerHTML = "&nbsp;&nbsp;&nbsp;&nbsp;";
			}
		}
		
		for( var i=0;i<customIcons.length;i++ ) {
			var customIconTd = document.createElement("td");
			customIconTd.appendChild( customIcons[i] );
			titleTr.appendChild(customIconTd);
		}
	}
	
	var isHotNews = IS_Widget.RssReader.isHotNews( widget.latestDatetime,rssItem );
	this.latestMark1.src = imageURL +( isHotNews ? "sun_blink.gif":"sun.gif");
	
	this.rssDetail1.firstChild.innerHTML = IS_R.lb_descLink;
	this.rssDetail1.id = widget.id + '_item_'+ itemNumber + '_more1';
	
	if(rssItem.description && rssItem.description.length > 0) {
//		rssDetailNobr.appendChild(document.createTextNode("description>>"));
		this.rssDetail1.style.display = "";
	} else {
		this.rssDetail1.style.display = "none";
	}
	
	this.latestMarkTd.style.display = "none";
	
	if( !br && IS_Widget.RssReader.isLatestNews(rssItem.rssDate))
		this.latestMarkTd.style.display = "";
	
	if(pubDate){
		this.moreTd1.style.display = "none";
		this.rssItemTable.style.marginBottom = "0px";
	}else{
		this.moreTd1.style.display = "";
		this.rssItemTable.style.marginBottom = "2px";
		
		if( br ) {
			//this.rssItemDiv.style.paddingRight = "3em";
		} else {
			this.rssItemDiv.style.paddingRight = 0;
		}
	}
}
IS_Widget.RssReader.RssItemRender.prototype.buildRssPubDate = function( widget,opt ) {
	var init = this.init;
	var rssItem = opt.rssItem;
	
	if( init ) {
		//Detailed time and creater
		this.rssPubDate = document.createElement("div");
		this.rssPubDate.className = "rssPubDate";
		this.rssPubDate.style.lineHeight = "1.2em";
		this.rssPubDate.style.height = "1.15em";
		this.rssPubDate.style.overflow = "hidden";
	} else {
		this.removeChildren( this.rssPubDate );
	}
	
	this.rssPubDate.appendChild(document.createTextNode(rssItem.date));
	this.rssPubDate.appendChild(document.createTextNode("  "));
	var creator = rssItem.creator.replace(/\s*(.+)/g,"$1");
	this.rssPubDate.appendChild(document.createTextNode( creator ));
}
IS_Widget.RssReader.RssItemRender.prototype.buildPubDate = function( widget,opt ) {
	var init = this.init;
	
	var rssItem = opt.rssItem;
	var pubDate = opt.pubDate;
	var br = opt.br;
	var itemNumber = opt.itemNumber;
	
	if( init ) {
		this.rssDetail = this.createRssDetailLink();
	}
	
	this.rssDetail.id = widget.id + '_item_'+ itemNumber + '_more';
	this.rssDetail.firstChild.innerHTML = IS_R.lb_descLink;
	if(rssItem.description && rssItem.description.length > 0) {
//		rssDetailNobr.appendChild(document.createTextNode("description>>"));
		
		this.rssDetail.style.display = "";
	} else {
		this.rssDetail.style.display = "none";
	}
	
	this.buildRssPubDate( widget,opt );
	
	if( init ) {
		this.itemTr2 = document.createElement("div");
		this.itemTr2.style.width = "100%";
		this.itemTr2.style.height = "1.25em";
		this.itemTr2.style.overflow = "hidden";
		this.itemTr2.style.position = "static"
		this.itemTr2.style.clear = "both";
		this.itemDiv.appendChild( this.itemTr2 );
		
		this.moreTd = this.rssDetail;
		this.moreTd.style.cssFloat = this.moreTd.style.styleFloat = "right"
		this.itemTr2.appendChild(this.moreTd)
		
		this.itemTr2.appendChild( this.rssPubDate );
	}
	//this.moreTd.id = widget.id + '_item_'+ itemNumber + '_more';
	
	if( pubDate ) {
		this.itemTr2.style.display = this.rssDetail.style.display = "";
	} else {
		this.itemTr2.style.display = this.rssDetail.style.display = "none";
	}
}
IS_Widget.RssReader.RssItemRender.prototype.createRssDetailLink = function() {
	// Prepare detail link
	// Change the way of showing in inline for 'detail' and 'close' to prevent the trouble below
	// If SwappableComponent is used, scrolling with mouse wheel causes a trouble: both of 'description' and 'close' with no style shows up in a blink 
	// This also causes change of widget size, so RssReader blinks going up and down under it
	//var rssDetail = document.createElement("span");
	var rssDetail = document.createElement("div");
	rssDetail.style.lineHeight = "1.2em";
	rssDetail.style.height = "1.15em";
	rssDetail.style.overflow = "hidden";
	rssDetail.className = "rssMore";
//	var rssDetailNobr = document.createElement("span");
	var rssDetailNobr = document.createElement("nobr");
	rssDetail.appendChild( rssDetailNobr);
	
	IS_Event.observe( rssDetail, "mouseout", this.rssDetailOnMouseout.bind( this ) );
	IS_Event.observe( rssDetail, "mouseover", this.rssDetailOnMouseover.bind( this ) );
	IS_Event.observe( rssDetail, "mousedown", this.rssDetailOnClicked.bind( this ) );
	IS_Event.observe( rssDetail, "click", IS_Event.stop );
	
	return rssDetail;
}
IS_Widget.RssReader.RssItemRender.prototype.buildRssDesc = function( widget,opt ) {
	var init = this.init;
	
	var rssItem = opt.rssItem;
	var pubDate = opt.pubDate;
	var br = opt.br;
	var itemNumber = opt.itemNumber;
	
	var popupDetail = widget.getUserPref("detailDisplayMode") == "popup";
	
	if( init ) {
		// Detail pop up
		this.rssFloat = document.createElement("div");
		this.rssFloat.className = 'rssDescFloat';
		
		this.rssDesc = document.createElement("div");
		this.rssDesc.className = "rssDesc";
		
		this.rssFloatMark = document.createElement("div");
		this.rssFloatMark.className = 'rssDescFloatMark';
		
		this.itemTr3 = document.createElement("div");
		this.itemTr3.style.clear = "both";
		
		this.itemTd3 = document.createElement("div");
		this.itemTd3.colSpan = "2";
		this.itemTd3.cellPadding = "0";
		this.itemTd3.cellSpacing = "0";
		this.itemTr3.appendChild(this.itemTd3);
		this.itemDiv.appendChild( this.itemTr3 );
	} else {
		this.removeChildren(
			this.rssFloat,
			this.rssDesc );
	}
	this.rssFloat.style.display = "none";
	this.rssFloatMark.style.display = "none";
	this.itemTr3.style.display = "none";
	
	this.descId = widget.id + '_item_'+ itemNumber + '_desc';
	var descMarkId = widget.id + '_item_'+ itemNumber + '_descMark';
	
	if( popupDetail ){
		var restFloat = $( this.descId );
		if(restFloat && restFloat.parentNode)
			restFloat.parentNode.removeChild( restFloat );
		
		var restFloatMark = $( descMarkId );
		if(restFloatMark && restFloatMark.parentNode)
			restFloatMark.parentNode.removeChild( restFloatMark );
		
		this.rssFloat.id = this.descId;
		this.rssFloat.appendChild( this.rssDesc );
		
		this.rssFloat.style.top = 0;
		document.body.appendChild( this.rssFloat );
		
		// Mark the location of detail pop up
		this.rssFloatMark.id = descMarkId;
		
		document.body.appendChild( this.rssFloatMark );
	}else{
		this.itemTr3.display = "";
		this.itemTd3.appendChild(this.rssDesc);
		
		if( rssItem.displayDesc ) {
			rssItem.displayDesc = false;
			
			this.buildDescContent( widget,rssItem );
		}
	}
}
IS_Widget.RssReader.RssItemRender.prototype.postRender = function( ctx,rssItem,index ) {
	var popupDetail = ctx.widget.getUserPref("detailDisplayMode") == "popup";
	
	if( IS_Widget.RssReader.RssItemRender.displayedRssDescId == this.descId && popupDetail ) {
		IS_Widget.RssReader.RssItemRender.hideRssDesc();
		this.buildDescContent( ctx.widget,rssItem );
	}
}
IS_Widget.RssReader.RssItemRender.prototype.buildDesc = function( widget,rssDesc,rssItem ) {
	var html = IS_Widget.RssReader.RssItemRender.getCategoryHtml(rssItem.category);
	html += IS_Widget.RssReader.RssItemRender.normalizeDesc(rssItem.description, widget.content.rss.isIntranet);
	rssDesc.innerHTML = html;
	var descLinks = rssDesc.getElementsByTagName("a");
	if(descLinks) {
		for(var i = 0; i < descLinks.length; i++) {
			if(!descLinks[i].target || descLinks[i].target == "_self"
				 || descLinks[i].target == "_top" || descLinks[i].target == "_parent") {
				var descClick = function( aTag) {
					var itemDisplay = widget.getUserPref("itemDisplay");
					if(itemDisplay){
						if(itemDisplay == "newwindow")
							aTag.target="_blank";
						else if(itemDisplay == "inline")
							aTag.target = "ifrm";
						else
							aTag.target = "";
					}
					
					IS_Widget.RssReader.RssItemRender.hideRssDesc();
					if(itemDisplay != "newwindow")
						IS_Portal.buildIFrame(aTag);
				}
				IS_Event.observe(descLinks[i], 'click', descClick.bind(null, descLinks[i]), false, widget.id);
			}
		}
	}
}
IS_Widget.RssReader.RssItemRender.createImageController = function() {
	var imgCtrl = document.createElement("a");
	imgCtrl.id = "imageController";
	imgCtrl.className = "imageController";
	imgCtrl.style.display = "none";
	IS_Portal.imageController.modal = new Control.Modal(imgCtrl, {
		image:true,
		afterOpen:function(){this.scrollTop = document.body.scrollTop; document.body.scrollTop = 0;},
		afterClose:function(){document.body.scrollTop = this.scrollTop;}
	});
	document.body.appendChild(imgCtrl);
	IS_Portal.imageController.element = imgCtrl;
	var showImgCtrl = function(e) {
		if(IS_Portal.imageController.timer){
			clearTimeout(IS_Portal.imageController.timer);
			IS_Portal.imageController.timer = false;
		}
		this.style.display = "block";
		this.isShowing = false;
	};
	var hideImgCtrl = function(e) {
		this.style.display = "none";
		this.isShowing = false;
	};
	Event.observe(imgCtrl, 'mouseover', showImgCtrl.bind(imgCtrl),false);
	Event.observe(imgCtrl, 'mouseout', hideImgCtrl.bind(imgCtrl),false);
}
IS_Widget.RssReader.RssItemRender.prototype.changeImages = function( widget,descImgs ) {
	if(!descImgs)
		return;
	
	var popupDetail = widget.getUserPref("detailDisplayMode") == "popup";
	var headerDiv = widget.parent ? widget.parent.elm_widgetHeader : widget.elm_widgetHeader;
	
	var headerWidth = headerDiv.offsetWidth;
	if(widget.elm_widgetContent.style.overflow == "auto" || widget.elm_widgetContent.style.overflow == "scroll")
		headerWidth -= 15;
	headerWidth = IS_Widget.getAdjustDescImgOffset(headerWidth, widget);
	var imgCtrl = IS_Portal.imageController.element;
	var this_ = this;
	var getOffsetY = (function(){
		function getScrollY(scrollElement, positionY){
			var _offsetY = scrollElement.scrollTop;
			var _contentY = findPosY(scrollElement) + scrollElement.offsetHeight;
			if(positionY - _offsetY > _contentY)
				return -1;
			return _offsetY;
		}
		if( popupDetail ){
//			return function(){
			return function(positionY){
//				return rssFloat ? rssFloat.scrollTop : 0;
				return getScrollY(this_.rssFloat, positionY);
			}
		} else if(widget.getUserPref("scrollMode") == "scroll") {
			return function(positionY) {
				return getScrollY(widget.content.rssContentView.elm_viewport, positionY);
			}
		} else if(widget.elm_widgetContent.style.overflow == "auto" || widget.elm_widgetContent.style.overflow == "scroll"){
			return function(positionY) {
				return getScrollY(widget.content.rssContentView.elm_viewport, positionY);
			}
		}
		return function(){return 0};
	}());
	for(var i = 0; i < descImgs.length; i++) {		
		var descImg = descImgs[i];
		var showImgCtrl = function() {
			var imgCtrl = IS_Portal.imageController.element;
			if(imgCtrl.isShowing) return false;
			imgCtrl.isShowing = true;
			IS_Portal.imageController.modal.src = this.src;
			imgCtrl.style.left = findPosX(this) + this.offsetWidth - 20 + 'px';
			var positionY = findPosY(this) + this.offsetHeight - 20;
			var offsetY = getOffsetY(positionY);
			if(offsetY < 0) {
				imgCtrl.isShowing = false;
				return false;
			}
			imgCtrl.style.top = positionY - offsetY + 'px';
			imgCtrl.style.display = "block";
		};
		var hideImgCtrl = function(e) {
			IS_Portal.imageController.timer = setTimeout(
				function(){
					var imgCtrl = IS_Portal.imageController.element;
					imgCtrl.style.display = "none";
					imgCtrl.isShowing = false;
					IS_Portal.imageController.timer = false;
				},
				100
			);
		};
		IS_Event.observe(descImgs[i], 'mouseover', showImgCtrl.bind(descImgs[i]), false, widget.id);
		IS_Event.observe(descImgs[i], 'mousemove', showImgCtrl.bind(descImgs[i]), false, widget.id);
		IS_Event.observe(descImgs[i], 'mouseout', hideImgCtrl.bind(descImgs[i]), false, widget.id);
		var adjustImgFunc = function(img, headerWidth){
			return function(){
				IS_Widget.adjustDescSingleImgWidth(img, headerWidth);
			}
		} 
		IS_Event.observe(descImgs[i], 'load', adjustImgFunc(descImgs[i], headerWidth), false, widget.id);
		descImgs[i].style.visibility = "hidden";
	}
}

IS_Widget.RssReader.RssItemRender.prototype.rssDetailOnMouseout = function(){
	this.rssDetail.className = "rssMore";
	this.rssDetail1.className = "rssMore";
}

IS_Widget.RssReader.RssItemRender.prototype.rssDetailOnMouseover = function(){
	this.rssDetail.className = "rssMoreOver";
	this.rssDetail1.className = "rssMoreOver";
}

IS_Widget.RssReader.RssItemRender.prototype.rssDetailOnClicked = function(e){
	this.buildDescContent( this.widget,this.rssItem );
	
	Event.stop(e);
}
IS_Widget.RssReader.RssItemRender.prototype.buildDescContent = function( widget,rssItem ) {
	if( !this.changeLink) {
		this.buildDesc( widget,this.rssDesc,rssItem );
		
		this.changeLink = true;
	}
	
	if( widget.getUserPref("detailDisplayMode") == "popup" ){
		if(this.descId != IS_Widget.RssReader.RssItemRender.displayedRssDescId){
			this.displayPopupDesc( widget,rssItem );
		}else{
			IS_Widget.RssReader.RssItemRender.hideRssDesc();
		}
	}else{
		if( !rssItem.displayDesc ){
			this.displayInlineDesc( widget,rssItem );
		}else{
			this.hideInlineDesc( widget,rssItem );
		}
		
		setTimeout(IS_Portal.widgetDisplayUpdated, 200);
	}
	
	if(!IS_Portal.imageController.element)
		IS_Widget.RssReader.RssItemRender.createImageController();
	
	if( !this.changeImg ) {
		this.changeImages( widget,this.rssDesc.getElementsByTagName("img") );
		
		this.changeImg = true;
	}
}
IS_Widget.RssReader.RssItemRender.prototype.displayInlineDesc = function( widget,rssItem ) {
	var descObj = {
//		headerDiv : widget.parent ? widget.parent.elm_widgetHeader : widget.elm_widgetHeader,
		widget : widget,
		tr : this.itemTr3,
		desc : this.rssDesc,
		more : this.rssDetail,
		more1 : this.rssDetail1,
		panelType : widget.panelType
	};
	
	var iframes = this.rssDesc.getElementsByTagName("iframe");
	for( var i=0;i<iframes.length;i++ ){
		var iframe = iframes[i];
		if( iframe.height == "100%")
			iframe.height = "";
		
		if( iframe.style.height == "100%")
			iframe.style.height = "auto";
	}
	
	var widgetType = widget.parent ? widget.parent.widgetType : widget.widgetType;
	var scrolling = IS_WidgetConfiguration[widgetType].scrolling ||
		(widget.getUserPref("scrollMode") == "scroll");
	
	var headerDiv = widget.parent ? widget.parent.elm_widgetHeader : widget.elm_widgetHeader;
	if(headerDiv && headerDiv.offsetWidth > 0){
		var offset = scrolling ? 16 : 0;
		if( Browser.isSafari1 )
			offset += 1;
		
		var browserOffset = Browser.isIE ? 2 : 1;
		//rssDesc.style.width = (headerDiv.offsetWidth - offset - browserOffset) + "px";
		var width = (headerDiv.offsetWidth - offset - browserOffset);
		this.rssDesc.style.width = width + "px";
	}
	this.itemTr3.style.display= "";
	
	if(scrolling)
		IS_Widget.RssReaderDescriptionWithScrollList.push(descObj);
	else
		IS_Widget.RssReaderDescriptionList.push(descObj);

	var startDateTime = (rssItem.rssDate)? rssItem.rssDate.getTime() : "";
	if(rssItem.rssUrls && widget.getUserPref("displayMode") != "category"){
		for(var i=0; i<rssItem.rssUrls.length ;i++){
			IS_Widget.updateLog("1",rssItem.link,rssItem.rssUrls[i]);
			IS_Widget.updateRssMeta("0",rssItem.link,rssItem.rssUrls[i],rssItem.title,startDateTime);
		}
	} else {
		IS_Widget.updateLog("1",rssItem.link,widget.getUserPref("url"));
		IS_Widget.updateRssMeta("0",rssItem.link,widget.getUserPref("url"),rssItem.title,startDateTime);
	}
			

	this.rssDetail.firstChild.innerHTML = escapeHTMLEntity(IS_R.lb_closeLink);
	this.rssDetail1.firstChild.innerHTML = escapeHTMLEntity(IS_R.lb_closeLink);
	this.rssItem.displayDesc = true;
	
	var itemHeight = this.tr.offsetHeight;
	if( itemHeight ) {
		rssItem.height = itemHeight;
		
		var rssContent = widget.content.rssContentView;
		var item = rssContent.itemList.findAll( function( item ) {
			return item.rss == rssItem;
		}).shift();
		
		if( item ) {
			item.height = itemHeight;
			
			setTimeout(function(){
				var itemHeight2 = this.tr.offsetHeight;
				if(itemHeight != itemHeight2)
					item.height = rssItem.height = itemHeight2;
			}.bind(this), 100);
			
			if( rssContent.onContentHeightChange )
				setTimeout( rssContent.onContentHeightChange.bind( rssContent ),150 );
		}
	}
}
IS_Widget.RssReader.RssItemRender.prototype.hideInlineDesc = function( widget,rssItem ) {
	var descObj = {
//		headerDiv : widget.parent ? widget.parent.elm_widgetHeader : widget.elm_widgetHeader,
		widget : widget,
		tr : this.itemTr3,
		desc : this.rssDesc,
		more : this.rssDetail,
		more1 : this.rssDetail1,
		panelType : widget.panelType
	};
	this.itemTr3.style.display = "none";
	var tmpArray = [];
	for(var j = 0; j < IS_Widget.RssReaderDescriptionList.length; j++){
		if( ( IS_Widget.RssReaderDescriptionList[j] == descObj) ){
		}else{
			tmpArray.push(IS_Widget.RssReaderDescriptionList[j]);
		}
	}
	IS_Widget.RssReaderDescriptionList = null;
	IS_Widget.RssReaderDescriptionList = tmpArray;
			

	[this.rssDetail,this.rssDetail1].each( function( m ){
		m.firstChild.innerHTML = IS_R.lb_descLink;
	});
	this.rssItem.displayDesc = false;
	
	var itemHeight = this.tr.offsetHeight;
	if( itemHeight ) {
		var rssContent = widget.content.rssContentView;
		var item = rssContent.itemList.findAll( function( item ) {
			return item.rss == rssItem;
		}).shift();
		
		if( item ) {
			item.height = itemHeight;
			
			if( rssContent.onContentHeightChange )
				setTimeout( rssContent.onContentHeightChange.bind( rssContent ),100);
		}
	}
}
			
IS_Widget.RssReader.RssItemRender.prototype.displayPopupDesc = function( widget,rssItem ) {
	if(rssItem.rssUrls && widget.getUserPref("displayMode") != "category"){
		for(var i=0; i<rssItem.rssUrls.length ;i++){
			var startDateTime = (rssItem.rssDate)? rssItem.rssDate.getTime() : "";
			IS_Widget.updateLog("1",rssItem.link,rssItem.rssUrls[i]);
			IS_Widget.updateRssMeta("0",rssItem.link,rssItem.rssUrls[i],rssItem.title,startDateTime);
		}
	}else{
		var startDateTime = (rssItem.rssDate)? rssItem.rssDate.getTime() : "";
		IS_Widget.updateLog("1",rssItem.link,widget.getUserPref("url"));
		IS_Widget.updateRssMeta("0",rssItem.link,widget.getUserPref("url"),rssItem.title,startDateTime);
	}
	
	IS_Widget.RssReader.RssItemRender.hideRssDesc();
	IS_Widget.RssReader.RssItemRender.displayedRssDescId = this.descId;
	if( IS_Widget.RssReader.RssItemRender.adjustRssDesc() )
		return; // error
	
	IS_Widget.adjustDescWidth();
	
	this.rssDesc.style.width = "";
	this.rssFloat.style.display = "";
	this.rssFloatMark.style.display = "";
	
	// Consider width of scroll bar when height exceeds maximum
	/*
	var offset = (rssFloat.offsetHeight >= 300) ? 30 : 10;
	rssFloat.firstChild.style.width = (rssFloat.offsetWidth - offset);
	*/
	

	[this.rssDetail,this.rssDetail1].each( function( m ){
		m.firstChild.innerHTML = IS_R.lb_closeLink;
	});
}

IS_Widget.RssReader.RssItemRender.displayedRssDescId = null;
/*
	Hide detail pop-up
*/
IS_Widget.RssReader.RssItemRender.hideRssDesc = function(){
	var descId = IS_Widget.RssReader.RssItemRender.displayedRssDescId;
	if( descId == null) return;
	
	var rssFloat = $( descId );
	var rssFloatMark = $( descId+'Mark' );
	var rssDetailId_pre = descId.substring(0, descId.lastIndexOf('_desc'));
	if(!rssFloat){
		IS_Widget.RssReader.RssItemRender.displayedRssDescId = null;
		return;
	}
	
	var itemId = descId.substring(0, descId.lastIndexOf('_desc'));
	// Remove, if parent item is deleted
	/*if(!$(itemId) && rssFloat.parentNode && rssFloatMark.parentNode){
		rssFloat.parentNode.removeChild( rssFloat );
		rssFloatMark.parentNode.removeChild( rssFloatMark );
	}else{*/
		rssFloat.style.display = "none";
		rssFloatMark.style.display = "none";
		//$("rssDescFloatIframe").style.display = "none";
		IS_Portal.behindIframe.hide();
		[
			$(rssDetailId_pre+"_more"),
			$(rssDetailId_pre+"_more1")].each( function( m ){
			if( m && m.firstChild )
				m.firstChild.innerHTML = IS_R.lb_descLink;
		});
	//}
	IS_Widget.RssReader.RssItemRender.displayedRssDescId = null;
}

/*
	Hide detail pop-up shown in the selected widget
*/
IS_Widget.RssReader.RssItemRender.hideWidgetRssDesc = function(widgetId){
	var descId = IS_Widget.RssReader.RssItemRender.displayedRssDescId;
	if( descId == null) return;
	
	var reg = new RegExp(widgetId + '_item_[0-9]+_desc');
	
	if(reg.test(IS_Widget.RssReader.RssItemRender.displayedRssDescId)){
		IS_Widget.RssReader.RssItemRender.hideRssDesc();
	}
}

/*
	Insert check mark from the point of click so that the shown pop-up is hided
*/
IS_Widget.RssReader.RssItemRender.checkHideRssDesc = function(e){
	var descId = IS_Widget.RssReader.RssItemRender.displayedRssDescId;
	if( descId == null) return;
	else if(Control.Modal.current) return;
	
	var mouseX = Event.pointerX(e);
	var mouseY = Event.pointerY(e);
	
	var isInWindow = true;
	var scrollY = (document.documentElement.scrollTop || document.body.scrollTop);
	var scrollX = (document.documentElement.scrollLeft || document.body.scrollLeft);
	if(Browser.isIE
			&& ( (document.documentElement.clientWidth + scrollX) < mouseX || (document.documentElement.clientHeight + scrollY) < mouseY )){
		// For trouble in the operation of (IE) window scroll whose description dissapears
		isInWindow = false;
	}
	
	if(!isInWindow) return;
	
	var rssFloat = $( descId );
	var rssFloatMark = $( descId+'Mark' );
	var rssDetailId_pre = descId.substring(0, descId.lastIndexOf('_desc'));
	
	if(!rssFloat){
		IS_Widget.RssReader.RssItemRender.displayedRssDescId = null;
		return;
	}
	
	if( rssFloat.style.display != "none" &&
		!isInObject( mouseX, mouseY, descId ) &&
		!isInObject( mouseX, mouseY, rssDetailId_pre+"_more" ) &&
		!isInObject( mouseX, mouseY, rssDetailId_pre+"_more1") ){
		rssFloat.style.display = "none";
		rssFloatMark.style.display = "none";
		//$("rssDescFloatIframe").style.display = "none";
		IS_Portal.behindIframe.hide();
		[
			$(rssDetailId_pre+"_more"),
			$(rssDetailId_pre+"_more1")].each( function( more ){
			more.firstChild.innerHTML = IS_R.lb_descLink;
		});
		IS_Widget.RssReader.RssItemRender.displayedRssDescId = null;
	}else{
		// Remove it, if the parent item is removed
		var itemId = descId.substring(0, descId.lastIndexOf('_desc'));
		if(!$(itemId) && rssFloat.parentNode && rssFloatMark.parentNode){
			rssFloat.parentNode.removeChild( rssFloat );
			rssFloatMark.parentNode.removeChild( rssFloatMark );
			IS_Widget.RssReader.RssItemRender.displayedRssDescId = null;
		}
	}
}

var processAdjustRssDescId = null;

/*
	Adjust location and size of description
*/
IS_Widget.RssReader.RssItemRender.adjustRssDesc = function(){
	processAdjustRssDescId = null;
	
	var descId = IS_Widget.RssReader.RssItemRender.displayedRssDescId;
	var rssFloat = $( descId );
	var rssFloatMark = $( descId+'Mark' );
	if( !descId || !rssFloat || !rssFloatMark)
		return true ;
	
	var widgetId = descId.substring(0, descId.lastIndexOf('_item_'));
	var widgetObj = null;
//	var widgetData = IS_Portal.widgetLists[ IS_Portal.currentTabId ][ widgetId ];
	var widgetData = IS_Portal.getWidget(widgetId);
	if(widgetData ){
		if( widgetData.widgetType != "MultiRssReader" ) {
			widgetObj = widgetData;
		} else {
			widgetObj = widgetData.content.mergeRssReader;
		}
		
		if( widgetData.tabId != IS_Portal.currentTabId )//for Safari
			return;
	}
	
	if( !widgetObj ||
		$("panels").style.display == "none" || // Search in iFrame
		widgetObj.getUserPref("detailDisplayMode") != "popup") {
		// Not show when widget in description is different from widget in current tub
		// For bug in tub switching: description remains
		IS_Widget.RssReader.RssItemRender.hideRssDesc();
		return true;
	}
	
	var itemId = descId.substring(0, descId.lastIndexOf('_desc'));
	var itemNode = $( itemId );
	var moreTd = [$( itemId + '_more' ),$( itemId + '_more1' )].findAll( function( more ){
		return more && more.style.display != "none";
	}).shift();
	
	if(!itemNode){
		//For a case that item is hided by description that scrolled in controller mood
		IS_Widget.RssReader.RssItemRender.hideRssDesc();
		return true;
	}
	
	var scrollY = (document.documentElement.scrollTop || document.body.scrollTop);
	var scrollX = (document.documentElement.scrollLeft || document.body.scrollLeft);
	var windowInnerWidth = (Browser.isIE)? document.documentElement.clientWidth : window.innerWidth;
	var windowInnerHeight = (Browser.isIE)? document.documentElement.clientHeight : window.innerHeight;
	var windowInnerRight = (Browser.isIE)? (windowInnerWidth + scrollX) : (windowInnerWidth + scrollX - 25);
	var windowInnerBottom = (Browser.isIE)? (windowInnerHeight + scrollY) : (windowInnerHeight + scrollY - 15);
	
	var panelScrollOffset = fixedPortalHeader ? IS_Portal.tabs[IS_Portal.currentTabId].panel.scrollTop : 0;
	
	var xy = Position.cumulativeOffset(itemNode);
	var itemsTop = xy[1] - panelScrollOffset;
	var itemsLeft = xy[0];
	if(itemsTop == 0 && itemsLeft == 0){
		// For a case that item is unshown (order by time and category)
		IS_Widget.RssReader.RssItemRender.hideRssDesc();
		return true;
	}
	
	var itemsRight = itemsLeft + itemNode.offsetWidth;
	if(widgetObj.getUserPref("scrollMode") != "none") itemsRight += 17;	// for scroll bar
	
	var restWindowWidth = windowInnerRight - itemsRight;
	
	var maxDescHeight = 300;
	var minDescWidth = 150;
	var maxDescWidth = 300;
	
	var descWidth = 300;
	
	// Get y-coordinate after correcting it
	var rssContentView = widgetObj.content.rssContentView;
	var contentScrollTop = rssContentView.elm_viewport.scrollTop;
	var descTop = itemsTop - contentScrollTop;
	var markTop = itemsTop - contentScrollTop + (moreTd.offsetHeight > 0 ? (moreTd.offsetHeight * 0.625 - 8) : 0);	// Place cursor on the middle of first line of title
	var contentTop = findPosY(rssContentView.elm_content ) - panelScrollOffset;
	var contentHeight = rssContentView.elm_viewport.offsetHeight;
	
	if( (descTop+itemNode.offsetHeight) <= contentTop || (contentTop+contentHeight) <= descTop ){
		// For a case that item is hided by description that scrolled in classic scroll
		IS_Widget.RssReader.RssItemRender.hideRssDesc();
		
		return true;
	}
	
	if(descTop < (contentTop - 8)){
		// Place cursor on the top of contents if the bottom of article is shown in classic mood
		descTop = contentTop - 8;
		markTop = contentTop - 8;
	}
	
	var rightWidth = windowInnerWidth - itemsRight ;	// right margin
	var leftWidth = windowInnerWidth - (rightWidth + itemNode.offsetWidth);	// left margin
	
	// Get width, x-coordinate after correcting it
	var descLeft;
	var markLeft;
	if( (restWindowWidth < minDescWidth && restWindowWidth < itemsLeft && minDescWidth < itemsLeft) || rightWidth+minDescWidth < leftWidth ){
		// Pop-up on the left
		//if( itemsLeft < maxDescWidth ) descWidth = itemsLeft;
		descWidth = itemsLeft;
		descLeft = itemsLeft - descWidth + ((Browser.isIE)? 13 : 4);
		markLeft = itemsLeft - 10;
		
		descWidth -= 20;
		
		rssFloatMark.style.backgroundImage = 'url(' + imageURL + 'resultset_next.gif)';
	}else{
		// Pop-up on the right
		if(1 < IS_Portal.tabs[IS_Portal.currentTabId].numCol) {
			descWidth = restWindowWidth;
			
			descLeft = itemsRight + 10;
			markLeft = itemsRight - 2;
			
			descWidth -= 15;
		}else{
			//Show at the middle
			var moreLeft = itemNode.offsetWidth -moreTd.offsetWidth;
			descLeft = parseInt( itemNode.offsetWidth / 3 );
			markLeft = descLeft - 12;
			
			descWidth = moreLeft - descLeft - 15;
		}
		
		rssFloatMark.style.backgroundImage = 'url(' + imageURL + 'resultset_previous.gif)';
	}
	// Modify the lateral location of description
	rssFloat.style.left = descLeft + 'px';
	rssFloat.style.width = descWidth + 'px';
	
	// Get the height after modifying
	var setHeight = false;
	rssFloat.zIndex = '-100';
	rssFloat.style.vizibility = "hidden";
	var display = rssFloat.style.display;
	rssFloat.style.display = "block";
	var rssFloatScrollTop = rssFloat.scrollTop;
	rssFloat.style.height = "";
	// Show it once because offsetHeight is 0 in 'display:none'
	var descHeight = rssFloat.offsetHeight;
	rssFloat.style.display = display;
	rssFloat.style.vizibility = "";
	rssFloat.style.zIndex = "";
	
	if(maxDescHeight < descHeight){
		setHeight = true;
		descHeight = maxDescHeight;
		
		// Consider width of scroll bar when height exceeds maximum
		var offset = (rssFloat.offsetHeight >= 300) ? 30 : 10;
		if((rssFloat.offsetWidth - offset) > 0){
			rssFloat.firstChild.style.width = (rssFloat.offsetWidth - offset) + 'px';
		}
	}
	
	// Modify the size if it exceeds the bottm of window(Modify only if item top does not exceed the bottom of the window)
	if(windowInnerBottom > itemsTop && windowInnerBottom < (descTop + descHeight) ){
		descTop = windowInnerBottom - descHeight;
	}
	
	// Modify the longitudinal location of description
	rssFloat.style.top = descTop + 'px';
	if(setHeight){
		rssFloat.style.height = descHeight + 'px';
	}
	
	//Modify the location of Iframe to hide pull-down and Flash
	IS_Portal.behindIframe.show(rssFloat);
	
	// Modify the location of mark
	rssFloatMark.style.top = markTop + 'px';
	rssFloatMark.style.left = markLeft + 'px';
	
	rssFloat.scrollTop = rssFloatScrollTop + 'px';
	
	// Modify the width of image
	if(rssFloat.style.display != "none")
		IS_Widget.adjustDescImgWidth(rssFloat, descWidth, widgetObj);
}

IS_Widget.processAdjustRssDesc = function(){
	if(IS_Widget.RssReader.RssItemRender.displayedRssDescId == null) return;
	
	if( processAdjustRssDescId != null ){
		clearTimeout( processAdjustRssDescId );
	}
	processAdjustRssDescId = setTimeout( IS_Widget.RssReader.RssItemRender.adjustRssDesc, 100 );
}

IS_Widget.adjustDescSingleImgWidth = function(img, headerWidth) {
	if(!img.originalWidth && img.offsetWidth)
		img.originalWidth = img.offsetWidth + 'px';
	if(!img.originalHeight && img.offsetHeight)
		img.originalHeight = img.offsetHeight + 'px';
	var isAdjust = false;
	if(img.originalWidth > headerWidth) {
		img.style.width = headerWidth + 'px';
		isAdjust = true;
	} else if(img.originalWidth > img.offsetWidth) {
		img.style.width = img.originalWidth;
		isAdjust = true;
	}
	
	if(isAdjust) {
		var descImgHeght = img.offsetWidth * (img.originalHeight / img.originalWidth);
		img.style.height = descImgHeght + 'px';
	}
	
	img.style.visibility = "";
}

IS_Widget.getAdjustDescImgOffset = function(headerWidth, widget){
	var offset = headerWidth -2;
	if(widget.getUserPref("detailDisplayMode") == "popup"){
//		offset = 265;
		offset = headerWidth - 35;
	} else if(widget.getUserPref("scrollMode") == "scroll") {
		offset -= 17;
	}
	return offset;
}

IS_Widget.adjustDescImgWidth = function(rssDesc, headerWidth, widget) {
	headerWidth = IS_Widget.getAdjustDescImgOffset(headerWidth, widget);
	if(headerWidth <= 0) return;
	
	var descImgs = rssDesc.getElementsByTagName("img");
	if(descImgs) {
		var adjustFunc = IS_Widget.adjustDescSingleImgWidth;
		for(var i = 0; i < descImgs.length; i++) {
			adjustFunc(descImgs[i], headerWidth);
		}
	}
}
//Event.observe(window, 'resize', IS_Widget.processAdjustRssDesc, false);
/*
IS_Widget.adjustDescWidth = function() {
	var offset = Browser.isIE ? 5 : 0;
	for(var j = 0; j < IS_Widget.RssReaderDescriptionList.length; j++){
		var obj = IS_Widget.RssReaderDescriptionList[j];
		var headerWidth = obj.headerDiv.offsetWidth;
		if(obj.headerDiv && obj.headerDiv.offsetWidth > 0){
			obj.desc.style.width = (headerWidth - 10 - offset) + "px";
		}
		IS_Widget.adjustDescImgWidth(obj.desc, headerWidth, obj.widget);
	}
	for(var j = 0; j < IS_Widget.RssReaderDescriptionWithScrollList.length; j++){
		var obj = IS_Widget.RssReaderDescriptionWithScrollList[j];
		var headerWidth = obj.headerDiv.offsetWidth;
		if(obj.headerDiv && obj.headerDiv.offsetWidth > 0){
			obj.desc.style.width = (headerWidth - 30 - offset) + "px";
		}
		IS_Widget.adjustDescImgWidth(obj.desc, headerWidth, obj.widget);
	}
	IS_Widget.processAdjustRssDesc();
};
*/

IS_Widget.adjustDescWidth = function() {
	var objList = new Array();
	
	var offset = Browser.isIE ? 6 : 2;
	for(var j = 0; j < IS_Widget.RssReaderDescriptionList.length; j++){
		adjustDescObjWidth( IS_Widget.RssReaderDescriptionList[j],0 +offset );
	}
	for(var j = 0; j < IS_Widget.RssReaderDescriptionWithScrollList.length; j++){
		adjustDescObjWidth( IS_Widget.RssReaderDescriptionWithScrollList[j],16 +offset );
	}
	
	function adjustDescObjWidth( obj,offset ) {
		if( Browser.isSafari1 ) {
			var widget = obj.widget;
			if( widget.tabId != IS_Portal.currentTabId ) 
				return;
		}
		
		obj.headerDiv = obj.widget.parent? obj.widget.parent.elm_widgetHeader : obj.widget.elm_widgetHeader;
		
		var headerWidth = obj.headerDiv.offsetWidth;
		if(Browser.isIE && obj.panelType == "StaticPanel"){
			obj.desc.style.display = "none";
			obj.headerWidth = headerWidth;
			objList.push(obj);
		}
		
		if(obj.headerDiv && obj.headerDiv.offsetWidth > 0){
			if( (headerWidth - offset ) >= 0 ) 
				obj.desc.style.width = (headerWidth - offset ) + "px";
		}
		if(!Browser.isIE || obj.panelType != "StaticPanel")
			IS_Widget.adjustDescImgWidth(obj.desc, headerWidth, obj.widget);
	}
	IS_Widget.processAdjustRssDesc();
	
	var resizeDescs = function(){
		for(var i=0;i<objList.length;i++){
			objList[i].desc.style.display = "block";
//			IS_Widget.adjustDescImgWidth(obj.desc, objList[i].headerWidth, obj.widget);
			IS_Widget.adjustDescImgWidth(objList[i].desc, objList[i].headerWidth, objList[i].widget);
		}
	}
	if(Browser.isIE && objList.length > 0){
		setTimeout(resizeDescs, 1);
	}
};

Event.observe(window, 'resize', IS_Widget.adjustDescWidth, false);

IS_Widget.RssReader.RssItemRender.getCategoryHtml = function(category) {
	if(!category)
		return "";
	var html = '<div class="rssCategoryList">';
	html += category.inject("", function(s, value, index){
		return s + "["+escapeHTMLEntity(value)+"]" + (index == category.length - 1? "<br>":"");
	});
	html += '</div>';
	return html;
}
IS_Widget.RssReader.RssItemRender.normalizeDesc = function( desc, isIntranet ) {
	if( /<body>/i.test( desc )) {
		desc = desc.replace(/^[\s\S]*<body>/mi,"");
	} else if( /<\/head>/i.test( desc )) {
		desc = desc.replace(/[\s\S]*<\/head>/mi,"");
	} else  if( /<html>/i.test( desc )) {
		desc = desc.replace(/[\s\S]*<html>/mi,"");
	}
	
	if( /<\/body>/i.test( desc )) {
		desc = desc.replace(/<\/body>[\s\S]*/mi,"");
	} else if( /<\/html>/i.test( desc )) {
		desc = desc.replace(/<\/html>[\s\S]*/mi,"");
	}
	if(isIntranet)
		return desc;
	else
		return html_sanitize(desc, function(url){return url;},function(id){return id;});
}
