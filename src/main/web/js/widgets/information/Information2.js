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

IS_Widget.InformationDescriptionList = {};
IS_Widget.Information2 = IS_Class.create();
IS_Widget.Information2.prototype.classDef = function() {
	this.isRequestWidget = true;
	var widget;
	var self = this;
	var scrolling;
	var rssItems = [];
	var contents;
	
	this.initialize = function(widgetObj){
		widget = widgetObj;
		contents = widget.elm_widgetContent;
		contents.style.width= "auto";
		contents.style.overflowX = "hidden";
		contents.style.overflowY = "scroll";
		scrolling = getBooleanValue(IS_WidgetConfiguration[widget.widgetType].scrolling);
		
		if(!widget.latestDatetime || widget.latestDatetime == ""){
			widget.latestDatetime = "";
		}
		if(!widget.widgetPref.dateTimeFormat.value) widget.widgetPref.dateTimeFormat.value = "";
		
		$( widget.elm_widgetContent ).addClassName("Information");
	}
	
	function buildRssItems(response){
		var rss = IS_Widget.parseRss(response);
		self.rss = rss;
		rssItems = (rss && rss.items) ? rss.items : [];
		
		self.displayContents();
		if(self.accessStatsIcon && widget.widgetPref.useAccessStat &&/true/i.test( widget.widgetPref.useAccessStat.value ))
			self.accessStatsIcon.style.display = "";
	}

	this.displayContents = this.repaint = function () {
		var container = widget.elm_widget.parentNode;
		if( container ) {
			if( !container.height && !container.style.height && !widget.staticWidgetHeight )
				widget.elm_widgetContent.style.height = '200px';
		}
		
		IS_Widget.InformationDescriptionList[widget.id] = [];
		var contentsDiv = document.createElement("div");
		contentsDiv.style.marginTop = "1px";
		contentsDiv.style.marginLeft = "1px";
		// show after IS_Widget.Information2.adjustDescWidth
		$(contentsDiv).hide();
		
		if (rssItems.length === 0) {
			var rsslink = document.createElement("div");
			rsslink.className = "rssItem";

			rsslink.innerHTML = IS_R.lb_noMessage;
			contentsDiv.appendChild(rsslink);
			
		} else {
			for ( var i=0; i<rssItems.length ; i++ ) {
				var contentsTable = document.createElement("table");
				contentsTable.cellPadding = "1px";
				contentsTable.cellSpacing = "0";
				
				if(Browser.isIE){
					contentsTable.style.wordBreak = "break-all";
				}
				
				contentsTable.setAttribute("width", "99%");
				contentsDiv.appendChild(contentsTable);
				var contentsTbody = document.createElement("tbody");
				contentsTable.appendChild(contentsTbody);
				var contentsTr = document.createElement("tr");
				contentsTbody.appendChild(contentsTr);

			 	var imgTd = null;
				var itemHeight = "60";
				if(rssItems[i].creatorImg.length > 0){
					imgTd = document.createElement("td");
					imgTd.style.verticalAlign = "top";
					imgTd.width = "1%";
					contentsTr.appendChild(imgTd);
					imgTd.innerHTML = rssItems[i].creatorImg;
					imgTd.firstChild.align = "left";
					//imgTd.firstChild.style.padding = "0 5px 5px 0";
					if(rssItems[i].creatorImg.match(/height=[\'\"]?([0-9a-z]+)[\'\"]?/i))
						itemHeight = RegExp.$1;
				}
				
				var latestMark = document.createElement("img");
				var isHotNews = IS_Widget.RssReader.isHotNews( widget.latestDatetime,rssItems[i] );
				latestMark.className = "latestMark";
				latestMark.src = imageURL +( isHotNews ? "sun_blink.gif":"sun.gif");
				
				var itemTd = document.createElement("td");
				itemTd.style.verticalAlign = "top";
				contentsTr.appendChild(itemTd);
				
				var itemTable = document.createElement("table");
				itemTable.cellSpacing = "0";
				itemTable.cellPadding = "1px";
				itemTable.setAttribute("width", "99%");
				itemTd.appendChild(itemTable);
				var itemTBody = document.createElement("tbody");
				itemTable.appendChild(itemTBody);
				
				var titleTr = document.createElement("tr");
				itemTBody.appendChild(titleTr);
				
				var titleTd = document.createElement("td");
				var titleDiv = document.createElement("div");
				titleDiv.style.lineHeight = "1.4em";
				titleDiv.style.height = "1.3em";
				
				if(!Browser.isIE){
					var focusA = document.createElement("a");
					focusA.style.visibility = "hidden";
					focusA.style.cssFloat = "left";
					focusA.style.overflow = "hidden";
					focusA.innerHTML = "";
					titleTd.appendChild(focusA);
				}
				
				if ( rssItems[i].link ) {
					var aTag = createLinkItem(rssItems[i]);
					titleDiv.appendChild(aTag);
				} else {
					
					var rssTitle = (rssItems[i].title.length == 0)? IS_R.lb_notitle : rssItems[i].title;
					rssTitle = rssTitle.replace(/&nbsp;/g," ");	// For trouble of "&nbsp;" where line-break does not occur
					titleDiv.appendChild(document.createTextNode(rssTitle));
				}
				
				titleDiv.title = rssItems[i].title;
				titleTd.appendChild(titleDiv);
				
				if(!getBooleanValue(widget.getUserPref("doLineFeed"))){
					var titleTdBox = document.createElement("td");
					titleTr.appendChild(titleTdBox);
					var titleTable = document.createElement("table");
					titleTable.cellPadding = "1px";
					titleTable.cellSpacing = "0";
					var titleTbody = document.createElement("tbody");
					titleTable.appendChild(titleTbody);
					var titleNobrTr = document.createElement("tr");
					titleTbody.appendChild(titleNobrTr);
					titleDiv.className = "rssItem";
					titleDiv.style.overflow = "hidden";
					titleNobrTr.appendChild(titleTd);
					
					if(IS_Widget.RssReader.isLatestNews(rssItems[i].rssDate)){
						var latestMarkTd = document.createElement("td");
						latestMarkTd.style.padding = "0";
						latestMarkTd.style.margin = "0";
						latestMarkTd.style.verticalAlign = "top";
						latestMarkTd.appendChild(latestMark);
						titleNobrTr.appendChild(latestMarkTd);
					}
					titleTdBox.appendChild(titleTable);
				}else{
					titleDiv.style.overflow = "visible";
					titleDiv.style.display = "inline";
					
					if(IS_Widget.RssReader.isLatestNews(rssItems[i].rssDate)){
						titleDiv.appendChild(latestMark);
					}
					
					titleDiv.className = "rssItem";
					titleTd.appendChild(titleDiv);
					titleTr.appendChild(titleTd);
				}
				
				var metaTr = document.createElement("tr");
				itemTBody.appendChild(metaTr);
				var metaTd = document.createElement("td");
				metaTr.appendChild(metaTd);
				
				var metaDiv = document.createElement("div");
				metaDiv.className = "rssPubDate";
				metaDiv.style.lineHeight = "1.03em";
				metaDiv.style.height = "1.03em";
				metaDiv.style.overflow = "hidden";
				if (rssItems[i].date && rssItems[i].date.length > 0) {
					metaDiv.innerHTML = rssItems[i].date;
				}
				
				if (rssItems[i].creator && rssItems[i].creator.length > 0){
					metaDiv.innerHTML += "&nbsp;&nbsp;" + rssItems[i].creator;
				}
				metaTd.appendChild(metaDiv);
				
				var descTr = document.createElement("tr");
				itemTBody.appendChild(descTr);
				var moreTr = document.createElement("tr");
				itemTBody.appendChild(moreTr);

				var descTd = document.createElement("td");
				descTr.appendChild(descTd);

				if(rssItems[i].description && rssItems[i].description.length > 0){
					var descDiv = document.createElement("div");
					descDiv.className = "information2Desc";
					var descHeightBefore = (parseInt(itemHeight) > 43 ? (parseInt(itemHeight) - 43) : 1);
					descDiv.style.height = descHeightBefore + "px";//"5em";
					descDiv.update(rssItems[i].description);
					var descLinks = descDiv.getElementsByTagName("a");
					if(descLinks) {
						for(var j = 0; j < descLinks.length; j++) {
							if(!descLinks[j].target || descLinks[j].target == "_self"
								|| descLinks[j].target == "_top" || descLinks[j].target == "_parent") {
								descLinks[j].target = "";
								var descClick = function() {
									IS_Portal.buildIFrame(this);
								}
								IS_Event.observe(descLinks[j], 'click', descClick.bind(descLinks[j]), false, widget.id);
							}
						}
					}
					descTd.appendChild(descDiv);
					
					var moreTd = document.createElement("td");
					moreTr.appendChild(moreTd);

					var more = document.createElement("div");
					more.className = "floatRight";
					moreTd.appendChild(more);

					var descMore = document.createElement("div");
					var descMoreNobr = document.createElement("span");
					descMore.appendChild(descMoreNobr);
					descMoreNobr.appendChild(document.createTextNode(IS_R.lb_continueLink ));
				
					var descDetail = document.createElement("div");
					var descDetailNobr = document.createElement("span");
					descDetail.appendChild(descDetailNobr);
					descDetailNobr.appendChild(document.createTextNode(IS_R.lb_closeLink ));

					var obj = {
						desc:descDiv,
						scrolling:scrolling,
					 	image:imgTd,
						focusEl:contentsTable,
						descHeightBefore:descHeightBefore,
						descHeight:descHeightBefore,
						rssItem : rssItems[i],
						descMoreOnClicked : function () {
							var tmpHeight = this.descHeight;
							this.desc.style.height = (this.desc.offsetHeight < tmpHeight) ? tmpHeight+'px' : '100%';
							//Save the height when it is firstly shown in FireFox because it cannot be shown at second or later actions even though 100% is set
							this.descHeight = this.desc.offsetHeight;
							var startDateTime = (this.rssItem.rssDate)? this.rssItem.rssDate.getTime() : "";
							IS_Widget.updateLog("1",this.rssItem.link,widget.getUserPref("url"));
							IS_Widget.updateRssMeta("0",this.rssItem.link,widget.getUserPref("url"),this.rssItem.title,startDateTime);
						},
						descDetailOnClicked : function(){
							this.desc.style.height = this.descHeightBefore + 'px';//"5em";
							this.desc.style.overflow = "hidden";
							if(Browser.isIE) {
								this.focusEl.focus();
							} else {
								var currentScrollTop = widget.elm_widgetContent.scrollTop;
								var targetScrollTop = this.focusEl.offsetTop - widget.elm_widgetContent.offsetTop;
								if(targetScrollTop < currentScrollTop)
									widget.elm_widgetContent.scrollTop = targetScrollTop;
							}
						}
					};
					IS_Widget.InformationDescriptionList[widget.id].push(obj);
					
					SwappableComponent.build ( {
						before:descMore,
						after:descDetail,
						before_onclick:obj.descMoreOnClicked.bind(obj),
						after_onclick:obj.descDetailOnClicked.bind(obj),
						style_out:"rssMore",
						style_over:"rssMore Over"}, widget.id);
					
					more.appendChild(descMore);
			 		more.appendChild(descDetail);
				}
			}
		}

		IS_Widget.InformationDescriptionList[widget.id].contentsDiv = widget.elm_widgetContent;
		IS_Widget.InformationDescriptionList[widget.id].widgetHeader = widget.elm_widgetHeader;

		if(widget.elm_widgetContent.firstChild){
			widget.elm_widgetContent.replaceChild(contentsDiv, widget.elm_widgetContent.firstChild);
		}else{
			widget.elm_widgetContent.appendChild(contentsDiv);
		}
		
		setTimeout(IS_Widget.Information2.adjustDescWidth, 5);
	};
	
	this.postEdit = this.displayContents;
	
	this.switchLineBreak = function () {
		if (rssItems.length > 0) {
			widget.toggleBoolUserPref("doLineFeed");
		    this.displayContents();
		}
	};

	function createLinkItem(rssItem){

		var rssTitle = (rssItem.title.length == 0)? IS_R.lb_notitle : rssItem.title;
		rssTitle = rssTitle.replace(/&nbsp;/g," ");	// For trouble of "&nbsp;" where line-break does not occur
		var aTag = document.createElement('a');
		aTag.href = rssItem.link;
		aTag.appendChild(document.createTextNode(rssTitle));
		
		var ctitle = rssItem.title;
		var aTagOnClick =function(e){
			var startDateTime = (rssItem.rssDate)? rssItem.rssDate.getTime() : "";
			IS_Widget.contentClicked(rssItem.link,widget.getUserPref("url"),ctitle,startDateTime,aTag);
		};
		IS_Event.observe(aTag, "click", aTagOnClick, false, widget.id);
		return aTag;
	}
	
	/** copy from Information */
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
	/** copy from RssReader.updateLog */
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

			msg.error( IS_R.getResource( IS_R.ms_lineChangeFailure,[widget.id,error]));
		}
	};
	
	this.accessStatsApplyIconStyle = function(div){
		this.accessStatsIcon = div;
		if(widget.isSuccess && widget.widgetPref.useAccessStat && /true/i.test( widget.widgetPref.useAccessStat.value ))
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

	//this.loadContents(); 
};

// fixes #478
IS_Widget.Information2.adjustDescWidthTimer = false;
IS_Widget.Information2.adjustDescWidth = function() {
	if(IS_Widget.Information2.adjustDescWidthTimer)
		clearTimeout(IS_Widget.Information2.adjustDescWidthTimer);
	
	IS_Widget.Information2.adjustDescWidthTimer = setTimeout(function(){
		for(var i in IS_Widget.InformationDescriptionList) {
			var descs = IS_Widget.InformationDescriptionList[i];
			if(descs && typeof descs != "function" && descs.contentsDiv.firstChild) {
				$(descs.contentsDiv.firstChild).hide();
				var contentsOffset = descs.widgetHeader.offsetWidth;
				for(var j = 0; j < descs.length; j++){
					var obj = descs[j];
					if(descs.contentsDiv && contentsOffset > 0){
						var offset = 30;
						var imgWidth = obj.image ? obj.image.offsetWidth : 0;
					
						obj.desc.style.width = (contentsOffset - imgWidth - offset) + "px";
					}
				}
				$(descs.contentsDiv.firstChild).show();
			}
		}
	}, 100);
};
//Event.observe(window, 'resize', IS_Widget.Information2.adjustDescWidth, false);
IS_EventDispatcher.addListener('windowResized', null, IS_Widget.Information2.adjustDescWidth);

IS_Widget.Information2.validateUserPref = IS_Widget.RssReader.validateUserPref;
