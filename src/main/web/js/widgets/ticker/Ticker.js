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

IS_Widget.OpenedTickerList = [];
IS_Widget.Ticker = IS_Class.create();
IS_Widget.Ticker.prototype.classDef = function() {
	var self = this;
	var widget;
	var id;
	var selfContent;
	var ticker_speed;
	var ticker_pause_onhover = 1;
	
	var ticker_speed;
	var copyspeed;
	var pausespeed;
	var actualwidth='';
	
	var currentRssNum = 0;

	var divTicker;
	var divParent;
	var rssItems = [];
	var newRssItems = [];
	var lefttime;
	var success = false;
	var onMove = false;
	
	var reloadTime;
	
	var isReloaded = false;
	var exist = false;
	
	this.disableSetSaticWidgetHeight = true;
	
	this.initialize = function(widgetObj){
		widget = widgetObj;
		id = widget.id;
		selfContent = widget.elm_widgetContent;
		selfContent.style.border = '0px';
		ticker_speed = widget.getUserPref("speed");
		ticker_speed = Math.max(1, ticker_speed-1);
		copyspeed = ticker_speed;
		pausespeed = (ticker_pause_onhover == 0) ? copyspeed : 0;
		reloadTime = is_getPropertyInt(widget.getUserPref("refreshInterval"), 3)*60*1000;
		
		if(!widget.widgetPref.dateTimeFormat.value) widget.widgetPref.dateTimeFormat.value = "";
		
		IS_Widget.OpenedTickerList.push(this);

	}

	function buildRssItems(response) {
		var xml = response.responseXML;
		var count = 0;
		var titleLength;
		if(self.onMove){
			var rss = IS_Widget.parseRss(response);
			if(rss && rss.items) newRssItems = rss.items;
			else newRssItems = [];
			isReloaded = true;
			var preExist = exist;
			exist = newRssItems.length > 0;
			if(!preExist) self.displayContents();
		}else{
			selfContent.innerHTML = '';
			var rss = IS_Widget.parseRss(response);
			if(rss && rss.items) rssItems = rss.items;
			exist = rssItems.length > 0;
			self.displayContents();
		}
	};

	this.displayContents = function(response) {
		self.onMove = true;
		
		selfContent.innerHTML = '';
		selfContent.style.overflow = "hidden";
		selfContent.style.width = "auto";
		
		divParent = document.createElement("div");
		divParent.style.overflow = "hidden";
		divParent.style.position = "absolute";
		divParent.style.width = selfContent.offsetWidth + 'px';
		divParent.style.height = selfContent.offsetHeight + 'px';
		
		divTicker = document.createElement("span");
		if ( Browser.isIE ) {
			divTicker.className = "ticker-base";
		}else {
			divTicker.className = "ticker";
		}
		
		var onMouseHandler = this.onmouse.bind(this);
		Event.observe(divTicker, 'mouseover', onMouseHandler);
		Event.observe(selfContent, 'mouseover', onMouseHandler);
		
		var outMouseHandler = this.mouseout.bind(this);
		Event.observe(divTicker, 'mouseout', outMouseHandler);
		Event.observe(selfContent, 'mouseout', outMouseHandler);
		
		if ( Browser.isIE ) {
			divMarquee = document.createElement("marquee");
			divMarquee.id = "marquee";
			divMarquee.width = "100%";
			divMarquee.scrollDelay = 50;
			divMarquee.scrollAmount = copyspeed*2;
			divMarquee.loop = 1;
			divMarquee.style.display = "none";
			divMarquee.appendChild(divTicker);
			divParent.appendChild(divMarquee);
			Event.observe(divMarquee, 'finish' , stopScrollWithMarqueeTag );
		}else {
			divParent.appendChild(divTicker);
		}
		selfContent.appendChild(divParent);
		
		showTicker();
	};
	
	function showTicker(){
		if ( !Browser.isIE )  {
			clearTimeout(lefttime);
		}
		
		if(!rssItems[currentRssNum]){
			//Reload if items to display go away
			currentRssNum = 0;
			if(isReloaded) {
				isReloaded = false;
				IS_Event.unloadCache("_tickermessage");
					
				//Change rssItems
				if(newRssItems){
					for(var i = 0; i < rssItems.length; i++){
						rssItems[i] = null;
					}
					rssItems = new Array();
					
					for(var i=0;i<newRssItems.length;i++){
						rssItems[i] = newRssItems[i];
					}
				}
			}
		}
		if(!exist){
			if(divTicker) divTicker.innerHTML = "";
			return;
		}
		
		var marqueecontent = "";
		
		//date
		if(widget.getBoolUserPref("date")) marqueecontent = addMsg(marqueecontent,rssItems[currentRssNum].date);
		//title
		var itemTitle = (rssItems[currentRssNum].title.length == 0)? IS_R.lb_notitle : escapeHTMLEntity(rssItems[currentRssNum].title);
		if(widget.getBoolUserPref("title")) marqueecontent = addMsg(marqueecontent,"[" + itemTitle + "]");
		//description
		if(widget.getBoolUserPref("desc")) marqueecontent = addMsg(marqueecontent,rssItems[currentRssNum].description);
		
		var startDateTime = (rssItems[currentRssNum].rssDate)? rssItems[currentRssNum].rssDate.getTime() : "";
		var contentDiv = addURL(rssItems[currentRssNum].link,marqueecontent,rssItems[currentRssNum].title,startDateTime);
		
		divTicker.title = rssItems[currentRssNum].title;
		
		divTicker.innerHTML = "";
		divTicker.appendChild(contentDiv);
		
		
		
		if ( Browser.isIE ) {
			scrollWithMarqueeTag();
		}else {
			//Position of start
			divTicker.style.left = parseInt(selfContent.offsetWidth)+2+"px";
			actualwidth = divTicker.offsetWidth * (-1)  ;
			scrollmarquee();
		}
	}
	
	function addURL(url, msg, title, startDateTime) {
		var tickerNobr = null;
		
		if(!url || url.length == 0){
			tickerNobr = document.createElement("span");
			tickerNobr.id = widget.id + "_tickerTxt";
			tickerNobr.innerHTML = msg;
			tickerNobr.style.lineHeight = "1.5em";
			tickerNobr.style.whiteSpace = "nowrap"
		}else{
			
			tickerNobr = document.createElement("span");
			tickerNobr.id = widget.id + "_tickerTxt";
			tickerNobr.style.whiteSpace = "nowrap"
			
			var aTag = document.createElement("a");
			aTag.style.lineHeight = "1.5em";
			aTag.href = url;
			aTag.innerHTML = msg;
			
			var aTagOnclick =function(aTag){
				return function(e){
					IS_Portal.buildIFrame(aTag);
				}
			}(aTag);
			tickerNobr.appendChild(aTag);
			
			IS_Event.observe(aTag, "click", aTagOnclick, false, "_tickermessage");
		}
		return tickerNobr;
	}
	
	function addMsg(msg1,msg2) {
		return (msg1.length == 0)? (msg2) : (msg1 + " " + msg2);
	}
	
	function scrollmarquee(){
		clearTimeout(lefttime);
		var styleLeft = parseInt(divTicker.style.left);
		if (styleLeft > actualwidth) {
			divTicker.style.left=styleLeft-copyspeed+"px";
		} else {
			currentRssNum++;
			
			showTicker();
			return;
		}
		
		lefttime = setTimeout(scrollmarquee,25);
	}
	
	function scrollWithMarqueeTag() {
		var div = $("marquee");
		if ( div ) {
			div.style.display="";
		}
	}
	
	function stopScrollWithMarqueeTag() {
		var div = $("marquee");
		if ( div ) {
			div.style.display="none";
			currentRssNum++;
			showTicker();
		}
	}
	
	this.onmouse = function () {
		copyspeed=pausespeed;
	};
	
	this.mouseout = function () {
		copyspeed=ticker_speed;
	};
	
	function handleError() {
		if(widget.isSuccess){
			showTicker();
		}
	};
	
	function handleComplete() {
		setTimeout(function(){
			IS_Event.unloadCache("_tickermessage");
			widget.loadContents();
			return;
		}, reloadTime);
	};
	
	this.loadContentsOption = {
		request : true,
		preLoad : function() {
			this.url = IS_Widget.getRssUrl(widget);
			return true;
		},
		onSuccess : buildRssItems,
		//on304: showTicker,
		on404: handleError,
		on403: handleError,
		onFailure: handleError,
		onException: handleError,
		onComplete: handleComplete
	};
	
	this.adjustTickerWidth = function (){
		if(divParent){
			divParent.style.width = selfContent.offsetWidth + 'px';
			divParent.style.height = selfContent.offsetHeight + 'px';
		}
	};
	
	Event.observe(window, 'resize', this.adjustTickerWidth.bind(this), false);
}

IS_Widget.Ticker.adjustTickerWidth = function(){
	for(var i=0;i<IS_Widget.OpenedTickerList.length;i++){
		IS_Widget.OpenedTickerList[i].adjustTickerWidth();
	}
}

IS_Widget.Ticker.reloadContents = function (ticker){
	ticker.loadContents();
}