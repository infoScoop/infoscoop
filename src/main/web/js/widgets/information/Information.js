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

IS_Widget.Information = IS_Class.create();
IS_Widget.Information.prototype.classDef = function() {
	this.isRequestWidget = true;
	var widget;
	var self = this;
	var rssItems = [];
	var contents;
	
	this.initialize = function(widgetObj){
		widget = widgetObj;
		contents = widget.elm_widgetContent;
		contents.style.width= "auto";
		contents.style.overflowX = "hidden";
		contents.style.overflowY = "scroll";
		
		if(!widget.latestDatetime || widget.latestDatetime == ""){
			widget.latestDatetime = "";
		}
		if(!widget.widgetPref.dateTimeFormat.value) widget.widgetPref.dateTimeFormat.value = "";
	}
	
	function buildRssItems(response){
		var rss = IS_Widget.parseRss(response);
		self.rss = rss;
		rssItems = (rss && rss.items) ? rss.items : [];
		self.rssContent = {
		  repaint:function(){
			  self.displayContents();
		  }
		};
		self.rssContent.rssItems = rssItems;
		self.displayContents();
		if(self.accessStatsIcon && widget.widgetPref.useAccessStat && /true/i.test( widget.widgetPref.useAccessStat.value ) )
			self.accessStatsIcon.style.display = "";
	}
	
	this.displayContents = function () {
		var container = widget.elm_widget.parentNode;
		if( container ) {
			if( !container.height && !container.style.height && !widget.staticWidgetHeight )
				widget.elm_widgetContent.style.height = '200px';
		}
		
		var contentsTable = document.createElement("table");
		contentsTable.cellPadding = "1px";
		contentsTable.cellSpacing = "0";
		contentsTable.setAttribute("width", "99%");
		
		var tbodyEl = document.createElement("tbody");
		contentsTable.appendChild(tbodyEl);
		
		if (rssItems.length === 0) {
			var rsslink = document.createElement("div");
			rsslink.className = "rssItem";

			rsslink.innerHTML = IS_R.lb_noMessage;
			
			var itemTr = document.createElement("tr");
			tbodyEl.appendChild(itemTr);
			var itemTd = document.createElement("td");
			itemTr.appendChild(itemTd);
			itemTd.appendChild(rsslink);
			
		} else {
			for ( var i=0; i<rssItems.length ; i++ ) {
				var itemTr = document.createElement("tr");
				tbodyEl.appendChild(itemTr);
				var itemTd = document.createElement("td");
				itemTr.appendChild(itemTd);
				
				var rssdate = document.createElement("div");
				rssdate.className = "rssItem";
				if (rssItems[i].date && rssItems[i].date.length > 0) {
					rssdate.innerHTML = rssItems[i].date + "  :  ";
					rssdate.style.whiteSpace = "nowrap";
				}
				
				var rsslink = document.createElement("div");
				rsslink.className = "rssItem";
				if ( rssItems[i].link ) {
					var aTag = createLinkItem(rssItems[i]);
					rsslink.appendChild(aTag);
				} else {

					var itemTitle = (rssItems[i].title.length == 0)? IS_R.lb_notitle : rssItems[i].title;
					itemTitle = itemTitle.replace(/&nbsp;/g," ");	// For trouble with "&nbsp;" where line-break does not occur
					rsslink.appendChild(document.createTextNode(itemTitle));
				}
	
				var latestMark = document.createElement("img");
				var isHotNews = IS_Widget.RssReader.isHotNews( widget.latestDatetime,rssItems[i] );
				latestMark.className = "latestMark";
				latestMark.src = imageURL +( isHotNews ? "sun_blink.gif":"sun.gif");
				
				var titleTable = document.createElement("table");
				titleTable.cellPadding = "0";
				titleTable.cellSpacing = "0";
				
				var titleTBody= document.createElement("tbody");
				titleTable.appendChild(titleTBody);
				
				var titleTr = document.createElement("tr");
				titleTBody.appendChild(titleTr);
				
				if(!getBooleanValue(widget.getUserPref("doLineFeed"))){
					var titleTr = document.createElement("tr");
					titleTBody.appendChild(titleTr);
					
					if(rssItems[i].date && rssItems[i].date.length > 0) {
						var dateTd = document.createElement("td");
						dateTd.appendChild(rssdate);
						titleTr.appendChild(dateTd);
					}
					
					var titleTd = document.createElement("td");
					rsslink.style.lineHeight = "1.03em";
					rsslink.style.height = "1.0em";
					rsslink.style.overflow = "hidden";
					rsslink.title = rssItems[i].title;
					titleTd.appendChild(rsslink);
					titleTr.appendChild(titleTd);
					
					if(IS_Widget.RssReader.isLatestNews(rssItems[i].rssDate)){
						var latestMarkTd = document.createElement("td");
						latestMarkTd.style.padding = "0";
						latestMarkTd.style.margin = "0";
						latestMarkTd.style.verticalAlign = "top";
						latestMarkTd.appendChild(latestMark);
						titleTr.appendChild(latestMarkTd);
					}
				}else{
					var rssItemDiv = document.createElement("div");
					rsslink.style.display = "inline";
					rssItemDiv.appendChild(rsslink);
					
					if(IS_Widget.RssReader.isLatestNews(rssItems[i].rssDate)){
						rssItemDiv.appendChild(latestMark);
					}
					
					if(rssItems[i].date && rssItems[i].date.length > 0) {
						var dateTd = document.createElement("td");
						dateTd.style.verticalAlign = "top";
						dateTd.appendChild(rssdate);
						titleTr.appendChild(dateTd);
					}
					
					var titleTd = document.createElement("td");
					titleTd.appendChild(rssItemDiv);
					titleTr.appendChild(titleTd);
				}
				itemTd.appendChild(titleTable);
			}
		}
	
		if(rssItems.length > 0){
			widget.latestDatetime = createGMTFormat(rssItems[0].rssDate);
		}else{
			widget.latestDatetime = createGMTFormat(new Date(1970, 0, 0));
		}
		
		if(widget.elm_widgetContent.firstChild){
			widget.elm_widgetContent.replaceChild(contentsTable, widget.elm_widgetContent.firstChild);
		}else{
			widget.elm_widgetContent.appendChild(contentsTable);
		}
	};
	
	this.postEdit = this.displayContents;
	
	this.switchLineBreak = function () {
		if (rssItems.length > 0) {
			widget.toggleBoolUserPref("doLineFeed");
		    this.displayContents();
		}
	};

	function createLinkItem(rssItem){

		var itemTitle = (rssItem.title.length == 0)? IS_R.lb_notitle : rssItem.title;
		itemTitle = itemTitle.replace(/&nbsp;/g," ");	//For trouble with "&nbsp;" where line-break does not occur
		var aTag = document.createElement('a');
		aTag.href = rssItem.link;
		aTag.appendChild(document.createTextNode(itemTitle));
		var ctitle = rssItem.title;
		var aTagOnclick =function(e){
			var startDateTime = (rssItem.rssDate)? rssItem.rssDate.getTime() : "";
			IS_Widget.contentClicked(rssItem.link,widget.getUserPref("url"),ctitle,startDateTime,aTag);
		}
		IS_Event.observe(aTag, "click", aTagOnclick, false, widget.id);
		return aTag;
	}
	
	this.stopLatestMarkRotate = function(){
		if( widget.title )
			msg.info( widget.title+" is not modified.");
		
		var imgs = widget.elm_widgetContent.getElementsByTagName( "img" );
		// content
		for(var i=0; i<imgs.length; i++){
			if(/sun_blink.gif$/.test( imgs[i].src )) {
				imgs[i].src = imageURL +"sun.gif";
			}
		}
	}
	/* copy from RssReader.updateLog */
	this.updateLog = function(){
		// Update log only if access succeeds
		if (widget.isSuccess) {
			var rssUrl = widget.getUserPref("url");
			if (rssUrl && !widget.isAuthenticationFailed()) {
				IS_Widget.updateLog("2", rssUrl, rssUrl);
			}
		}
	}
	this.loadContentsOption = {
		preLoad : function(){
			if(rssItems.length > 0 && rssItems[0].rssDate )
				widget.latestDatetime = rssItems[0].rssDate.getTime();
			
			this.url = IS_Widget.getRssUrl(widget);
			return true;
		},
		request : true,
		onSuccess : function( response ) {
			buildRssItems( response );
			this.updateLog();
		}.bind( this ),
		on304 : function() {
			this.stopLatestMarkRotate();
			this.updateLog();
		}.bind( this )
	};
	this.autoReloadContentsOption = {
		preLoad : function(){
			if(rssItems.length > 0 && rssItems[0].rssDate )
				widget.latestDatetime = rssItems[0].rssDate.getTime();
			
			this.url = IS_Widget.getRssUrl(widget);
			return true;
		},
		request : true,
		onSuccess : function( response ) {
			buildRssItems( response );
		}.bind( this ),
		on304 : function() {
			this.stopLatestMarkRotate();
		}.bind( this )
	}

	//Setting of line break
	this.lineFeedIconHandler = function (e) {
		try{
			this.switchLineBreak();
		}catch(error){
			msg.error( IS_R.getResource( IS_R.ms_lineChangeFailure,[widget.id,error]) );
		}
	};
	
	this.accessStatsApplyIconStyle = function(div){
		this.accessStatsIcon = div;
		if(widget.isSuccess && widget.widgetPref.useAccessStat && /true/i.test( widget.widgetPref.useAccessStat.value ) )
			div.style.display = "";
		else
			div.style.display = "none";
	};
	
	this.accessStatsIconHandler = function(div){
		IS_Widget.RssReader.showAccessStats(widget);
		if( widget.headerContent && widget.headerContent.hiddenMenu )
			widget.headerContent.hiddenMenu.hide();
	}
	
	this.getRssItems = function () {
		return rssItems;
	}
	this.isError = function () {
		return (self.rssItemLength == 0)
	}

};

IS_Widget.Information.validateUserPref = IS_Widget.RssReader.validateUserPref;
