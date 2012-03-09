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

IS_Widget.Ranking = IS_Class.create();
IS_Widget.Ranking.prototype.classDef = function() {
	var widget;
	var self = this;
	var contents;
	var rankings = [];
	var currentTabIdx = 0;
	var bodyDiv;
	
	this.disableSetSaticWidgetHeight = true;//Disable Widget._setStaticWidgetHeight
	
	this.initialize = function(widgetObj){
		widget = widgetObj;
		contents = widget.elm_widgetContent;
		//contents.style.width= "auto";
		if(!widget.widgetPref.dateTimeFormat.value) widget.widgetPref.dateTimeFormat.value = "";
		var urls = [];
		if(widget.widgetPref.urls){
			var urlsDom = is_createDocumentFromText(widget.widgetPref.urls.value);
			var urlElms = urlsDom.getElementsByTagName("url");
			for(var i = 0; i < urlElms.length; i++){
				urls.push(urlElms[i]);
			}
		}
		if(widget.getUserPref("urls")) {
			//var urlsDom = dojo.dom.createDocumentFromText(widget.getUserPref("urls"));
			var urlsDom = is_createDocumentFromText(widget.getUserPref("urls"));
			var urlElms = urlsDom.getElementsByTagName("url");
			for(var i = 0; i < urlElms.length; i++){
				urls.push(urlElms[i]);
			}
		}
		if(urls.length == 0){
			contents.innerHTML = 

				"<span style='font-size:90%;color:red;padding:5px;'>"+IS_R.lb_notfound +"</span>";
			return;
		}
		
		var contentsDiv = document.createElement("div");
		var tabsTable = document.createElement("table");
		tabsTable.className = "rankingTabs";
		if(Browser.isIE) tabsTable.style.width = "95%";
		tabsTable.cellSpacing = "0";
		tabsTable.cellPadding = "0";
		var tabsTbody = document.createElement("tbody");
		tabsTable.appendChild(tabsTbody);
		var tabsTr = document.createElement("tr");
		tabsTbody.appendChild(tabsTr);
		
		bodyDiv = document.createElement("div");
		var tabWidth = (100/rankings.length - 3);
		if(tabWidth > 50) tabWidth = 50;
		tabWidth = tabWidth + "%";
		for(var i = 0; i < urls.length; i++) {
			var url = urls[i].getAttribute("url");
			var title = urls[i].getAttribute("title");
			var noProxy = (urls[i].getAttribute("noProxy") === "true");
			tabsTr.appendChild(this.buildSpaceTab("1%"));
			tabsTr.appendChild(this.buildTabHeader(title, i, (i == currentTabIdx), tabWidth));
			var body = document.createElement("div");
			body.className = "rankingBody";
			if(i != currentTabIdx)
				body.style.display = "none";
			body.id = widget.id + "_" + i;
			bodyDiv.appendChild(body);
			rankings.push(new IS_Widget.Ranking.RankingRender(url, title, body, widget, i, noProxy));
		}
			
		tabsTr.appendChild(this.buildSpaceTab());
		
		contentsDiv.appendChild(tabsTable);
		contentsDiv.appendChild(bodyDiv);
		
		contents.replaceChild(contentsDiv, contents.firstChild);
		
	}
	
	this.loadContents = function () {		
		if(!bodyDiv) {
			IS_EventDispatcher.newEvent('loadComplete', widget.id, null);
			return;
		}
		
		try {
			var eventTargetList = new Array();
			for(var i=0; i<rankings.length; i++) {
				eventTargetList.push({type:"loadComplete", id:rankings[i].url});
			}
			IS_EventDispatcher.addComplexListener(eventTargetList, this.postLoadContents.bind(this), null, true);
			
			
			var tabWidth = (100/rankings.length - 3);
			if(tabWidth > 50) tabWidth = 50;
			tabWidth = tabWidth + "%";
			for(var i=0; i<rankings.length; i++) {
				rankings[i].loadContents();
			}
		} catch(e) {

			var message = IS_R.getResource( IS_R.ms_RankingCriticalerror,[e]);
			contents.innerHTML = "<span style='font-size:90%;color:red;padding:5px;'>" + message + "</span>";
			IS_EventDispatcher.newEvent('loadComplete', widget.id, null);
			throw e;
		}
	};
	
	this.postLoadContents = function() {
		IS_EventDispatcher.newEvent('loadComplete', widget.id, null);
	};
	
	this.loadContentsOption = {
		onSuccess : self.loadContents.bind(self)		
	};
	
	this.buildSpaceTab = function(width) {
		var spaceTab = document.createElement("td");
		spaceTab.className = "space";
		if(width)
			spaceTab.style.width = width;
		spaceTab.innerHTML = "&nbsp;";
		return spaceTab;
	}
	
	this.buildTabHeader = function(title, tabIdx, isActive, width){
		var tabsTd = document.createElement("td");
		tabsTd.className = isActive ? "active" : "nonactive";
		tabsTd.id = widget.id + "_" + tabIdx + "_tab";
		tabsTd.style.width = width;
		var tabsDiv = document.createElement("div");
		tabsTd.appendChild(tabsDiv);
		tabsDiv.appendChild(document.createTextNode(title));
		tabsDiv.title = title;
		tabsDiv.tabIdx = tabIdx;
		tabsDiv.style.lineHeight = "1.2em";
		tabsDiv.style.height = "1.15em";
		var tabsDivOnclick = function(e) {
			if(currentTabIdx != tabsDiv.tabIdx) {
				var currentTabId = widget.id + "_" + currentTabIdx;
				var tabId = widget.id + "_" + tabsDiv.tabIdx;
				$(currentTabId).style.display = "none";
				$(tabId).style.display = "";
				$(currentTabId + "_tab").className = "nonactive";
				$(tabId + "_tab").className = "active";
				currentTabIdx = tabsDiv.tabIdx;
				IS_Portal.behindIframe.show(widget.elm_widgetShade);
			}
		};
		IS_Event.observe(tabsDiv, "click", tabsDivOnclick, false, widget.closeId);
		return tabsTd;
	};
};

IS_Widget.Ranking.buildCommandBar = function( widgetId ){
	var containerDiv = document.createElement("div");
	
	var rankingWidgetDiv = $( widgetId );
	if(!rankingWidgetDiv) return;
	
	var attrs = rankingWidgetDiv.attributes;
	for(var i=0;i<attrs.length;i++){
		containerDiv.setAttribute(attrs[i].nodeName, attrs[i].nodeValue);
	}
	containerDiv.id = "s_" + widgetId + "_container";
	// copy class name and remove from original (original is for easier migration)
	containerDiv.className = rankingWidgetDiv.className;
	rankingWidgetDiv.className ='';
	
	var commandBarTd = rankingWidgetDiv.parentNode;
	commandBarTd.appendChild(containerDiv);
	
	commandBarTd.removeChild(rankingWidgetDiv);
	var commandBarRankingDiv = document.createElement("a");
	commandBarRankingDiv.className = "portal-user-menu-link";
	commandBarRankingDiv.href = 'javascript:void(0);';
	commandBarRankingDiv.title = IS_R.lb_ranking;
	commandBarRankingDiv.appendChild(
		$.DIV({id: 'command-ranking', className:'portal-user-menu-item-label'}
			, IS_R.lb_ranking
		)
	);
	Event.observe(commandBarRankingDiv, "click", IS_Widget.Ranking.toggleRanking.bindAsEventListener(this, commandBarRankingDiv, widgetId ));
	containerDiv.appendChild(commandBarRankingDiv);
	var rankingOuterDiv = document.createElement("div");
	rankingOuterDiv.id = widgetId+"_div";
	var rankingOuterDivStyle = rankingOuterDiv.style;
	rankingOuterDivStyle.display = "none";
	rankingOuterDivStyle.position = "absolute";
	rankingOuterDivStyle.zIndex = "999";
	rankingOuterDivStyle.width = "300px";
	rankingOuterDiv.appendChild(rankingWidgetDiv);
	
	Event.observe(rankingOuterDiv, "click", function(e){Event.stop(e);}, false);
	
	containerDiv.appendChild(rankingOuterDiv);
	if(commandBarRankingDiv.offsetWidth)
		commandBarTd.style.width = commandBarRankingDiv.offsetWidth;
}

var rankingOuterDiv;
var rankingShowTimer;
IS_Widget.Ranking.toggleRanking = function(e, obj, widgetId) {
	rankingOuterDiv = $(widgetId+"_div");
	if(rankingOuterDiv.style.display == 'none') {
		rankingOuterDiv.style.display = '';
		if(!rankingOuterDiv.parentNode.getAttribute('outside')){
			Element.setStyle(rankingOuterDiv, {
				left : - rankingOuterDiv.offsetWidth
				, top: 0
			});
		}else{
			var targetPosition = Position.page($("command-ranking"));
			Element.setStyle(rankingOuterDiv, {
				left : targetPosition[0]
				, top: targetPosition[1] + $("command-ranking").offsetHeight + 1
			});
		}
		//rankingOuterDiv.style.left = getWindowSize(true) - rankingOuterDiv.offsetWidth - 25;
		Event.observe(window, "resize", IS_Widget.Ranking.handleResize, false);
		Event.observe(document, "click", IS_Widget.Ranking.handleWindowClick, false);
		Event.observe(rankingOuterDiv, "mouseover", IS_Widget.Ranking.handleMouseover, false);
		Event.observe(rankingOuterDiv, "mouseout", IS_Widget.Ranking.handleMouseout, false);
		Event.observe(obj, "mouseout", IS_Widget.Ranking.hide, false);
		Event.observe(obj, "mouseover", IS_Widget.Ranking.show, false);
		Event.observe(rankingOuterDiv, "mouseout", IS_Widget.Ranking.hide, false);
		Event.observe(rankingOuterDiv, "mouseover", IS_Widget.Ranking.show, false);

		IS_Portal.behindIframe.show($( widgetId ).firstChild);
	} else {
		rankingOuterDiv.style.display = 'none';
		Event.stopObserving(window, "resize", IS_Widget.Ranking.handleResize, false);
		Event.stopObserving(document, "click", IS_Widget.Ranking.handleWindowClick, false);
		Event.stopObserving(obj, "mouseout", IS_Widget.Ranking.hide, false);
		Event.stopObserving(obj, "mouseover", IS_Widget.Ranking.show, false);
		Event.stopObserving(rankingOuterDiv, "mouseout", IS_Widget.Ranking.hide, false);
		Event.stopObserving(rankingOuterDiv, "mouseover", IS_Widget.Ranking.show, false);

		IS_Portal.behindIframe.hide();
	}
	Event.stop(e);
}

IS_Widget.Ranking.handleResize = function(e) {
//	if(rankingOuterDiv) {
//		rankingOuterDiv.style.left = getWindowSize(true) - rankingOuterDiv.offsetWidth - 25;
//	}
	IS_Widget.Ranking.hide();
}

IS_Widget.Ranking.hide = function(e, force) {
	var hideEvent = function() {
		rankingOuterDiv.style.display = 'none';
		IS_Portal.behindIframe.hide();
	}
	
	if(rankingOuterDiv && !rankingShowTimer) {
		rankingShowTimer = setTimeout(hideEvent, 500);
	}
	else if(rankingOuterDiv && force === true){
		hideEvent();
	}
}

IS_Widget.Ranking.show = function(e) {
	if(rankingOuterDiv) {
		clearTimeout(rankingShowTimer);
				
		rankingShowTimer = null;
	}
}

IS_Widget.Ranking.handleWindowClick = function(e) {
//	if(rankingOuterDiv) {
//		rankingOuterDiv.style.display = 'none';
//	}
	IS_Widget.Ranking.hide();
}
IS_Widget.Ranking.handleMouseover = function(e) {
	if(rankingOuterDiv) {
		Event.stopObserving(document, "click", IS_Widget.Ranking.handleWindowClick, false);
	}
}
IS_Widget.Ranking.handleMouseout = function(e) {
	if(rankingOuterDiv) {
		Event.observe(document, "click", IS_Widget.Ranking.handleWindowClick, false);
	}
}
