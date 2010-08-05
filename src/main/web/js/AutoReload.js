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

/**
 * Class execute auto updating processing
 */
var IS_AutoReload = IS_Class.create();
IS_AutoReload.prototype.classDef = function(){
	var self = this;
	//Auto update interval of overall (mili seconds)
	var refreshIntervalMs = refreshInterval*60*1000;
	//Auto update interval between widgets
	var widgetRefreshIntervalSec = widgetRefreshInterval*1000;
	//Manage auto update information of each tab
	//Keep objects below in the map keyed by tabId
	//{
	//  nextWidgetId : ID of widget that is updated automatically next
	//                 Used for auto updating at the middle if the tab is switched
	//  nextWidgetIndex : The index of next widget updated automatically in widgetList
	//                    Used for recovery if the widget in nextWidgetId is deleted
	//  lastReloadTime : Date at the latest auto update is done(miliseconds)
	//                   Used for judging whether auto update is started just after switching tab or not.
	//}
	this.tabReloadInfo = {};
	//Timer of auto update for next widget
	this.widgetTimer = null;
	//Widget currently auto updating
	this.reloadWidgets = {};
	//Tab ID currently auto updating
	this.reloadTabId = null;
	
	this.initialize = function(){
		//console.log("new IS_AutoReload");
		this.startTime = new Date().getTime();
		this.timer = setTimeout(self.start.bind(self), refreshIntervalMs);
	}
	
	//Maximized widget is treated in same way as one tab and processing in same logic
	//getCurrentTabInfo is a wrapper to treat maximized widget in same way as the tab 
	this.getCurrentTabInfo = function(){
		if(IS_Widget.MaximizeWidget != null){
			return {
				tabId : IS_Widget.MaximizeWidget.id,
				widgetList : [IS_Widget.MaximizeWidget]
			}
		}
		return {
			tabId : IS_Portal.currentTabId,
			widgetList : IS_Portal.widgetLists[IS_Portal.currentTabId]
		}
	}
	
	this.next = function() {
		var currentTabInfo = self.getCurrentTabInfo();
		var currentTabId = currentTabInfo.tabId;
		var widgets = currentTabInfo.widgetList;
		var widgetId = null;
		var widgetIdx = 0;
		var lastReloadTime = null;
		if(self.tabReloadInfo[currentTabId]){
			widgetId = self.tabReloadInfo[currentTabId].nextWidgetId;
			widgetIdx = self.tabReloadInfo[currentTabId].nextWidgetIndex;
			lastReloadTime = self.tabReloadInfo[currentTabId].lastReloadTime;
		} else {
			self.tabReloadInfo[currentTabId] = {};
		}
		var tabReloadInfo = self.tabReloadInfo[currentTabId];
		//Stop auto updating of original tab if the tab is changed
		if (self.reloadTabId && currentTabId != self.reloadTabId) {
			self.cancel();
			var now = new Date().getTime();
			if(!lastReloadTime) lastReloadTime = now;
			var tabIntervalTime = now - lastReloadTime;
			//Next auto updating is started in a moment if the time passed more than refreshInterval property from the end of last auto updating
			var nextInterval = 0;
			//If reloading occurs during refreshInterval after previous auto refresh, wait for the refreshInterval (Not for case of reloading on the way of initial auto reload)
			//This function cause auto reload not to occur for 2 minutes and 20 seconds at max if widgetRefreshInterval is included
			if (!widgetId && tabIntervalTime < refreshIntervalMs)
				nextInterval = refreshIntervalMs - tabIntervalTime;
			//console.log("changeTab:" + currentTabId + ", lastReloadTime=" + lastReloadTime + ", nextInterval=" + nextInterval);
			self.widgetTimer = setTimeout(self.next.bind(self), nextInterval);
			self.reloadTabId = currentTabId;
			return;
		}
		self.reloadTabId = currentTabId;
		tabReloadInfo.nextWidgetId = null;
		var isNextWidget = false;
		var counter = 0;
		for(var i in widgets){
			if(typeof widgets[i] == "function") continue;
			if(isNextWidget){
				tabReloadInfo.nextWidgetId = i;
				tabReloadInfo.nextWidgetIndex = counter;
				break;
			}
			// The last condition is remedy for the widget of nextWidgetId is deleted
			//Reload the number if the widget of nextWidgetId is not found by nextWidgetIndex
			if(widgetId == null || i == widgetId || counter >= widgetIdx){
				var reloadWidget = widgets[i];
				if( !reloadWidget.parent && reloadWidget.autoRefresh) {
					self.reloadWidgets[i] = reloadWidget;
					IS_EventDispatcher.addListener("loadComplete", i, self.endNext.bind(self, i), null, true);
					
					reloadWidget.autoReloadContents();
					isNextWidget = true;
				}
			}
			counter++;
		}
		self.startWidgetTime = new Date().getTime();
		//console.log("next:currentTabId=" + currentTabId + ", widgetId=" + widgetId + ", nextWidgetId=" + tabReloadInfo.nextWidgetId);
		if(!tabReloadInfo.nextWidgetId) //End of auto updating
			self.end();
		else //Set the timer of auto updating of next widget
			self.widgetTimer = setTimeout(self.next.bind(self), widgetRefreshIntervalSec);
	};
	
	this.endNext = function(widgetId) {
		//console.log("endNext:" + widgetId);
		delete self.reloadWidgets[widgetId];
	}
	
	this.start = function() {
		self.timer = null;
		self.startTime = new Date().getTime();
		//console.log("start:"+self.startTime);
		self.next();
	}
	
	this.end = function() {
		if(menuAutoRefresh) {
			IS_Portal.closeMsgBar();
			IS_SiteAggregationMenu.refreshMenu();
			if( displaySideMenu && displaySideMenu != "reference_top_menu" )
				IS_SidePanel.SiteMap.refreshTreeMenu();
		}
		self.widgetTimer = null;
		var endTime = new Date().getTime();
		self.tabReloadInfo[self.getCurrentTabInfo().tabId].lastReloadTime = endTime;
		var refreshTime = endTime - self.startTime;
		//The next auto updating is started in a momemnt if auto updating takes time more than refreshInterval property
		var nextInterval = 0;
		//Wait for value in refreshInterval property from last auto updating if auto updating ends within refreshInterval
		if(refreshTime < refreshIntervalMs)
			nextInterval = refreshIntervalMs - refreshTime;
		//console.log("end: refreshTime=" + refreshTime + ", nextInterval=" + nextInterval);
		self.timer = setTimeout(self.start.bind(self), nextInterval);
	};
	
	this.cancel = function() {
		//Stop auto updating of next widget
		if(self.widgetTimer) {
			//console.log("cancel: widget " + self.widgetTimer);
			clearTimeout(self.widgetTimer);
			self.widgetTimer = null;
			self.isWidgetInterval = true;
		}
		//Stop if the timer of auto updating for overall is working
		if(self.timer) {
			//console.log("cancel: All " + self.timer);
			clearTimeout(self.timer);
			self.timer = null;
			self.isWidgetInterval = false;
		}
		//Stop Ajax request if there is widget on auto updating
		var reloadWidgets = self.reloadWidgets;
		if(!reloadWidgets) return;
		for(var i in reloadWidgets){
			if(typeof reloadWidgets[i] == "function") continue;
			if(reloadWidgets[i].type == "MultiRssReader"){
				var rssReaders = reloadWidgets[i].content.getRssReaders();
				for(var j = 0; j < rssReaders.length; j++) {
					AjaxRequest.cancel(rssReaders[j].id);
				}
			} else {
				AjaxRequest.cancel(reloadWidgets[i].id);
			}
		}
	};
	
	//Resume is called only if ifram is closed so far.
	this.resume = function() {
		var nextInterval = 0;
		//Next widget is updated if the time is passed mroe than updating interval after previous widget is loaded.
		//If the time is not passed, wait for the differecnce
		//Auto update and refresh for overall if auto refresh for overall is canceled
		//Calculate by starting time
		if(self.isWidgetInterval){
			var cancelTime = new Date().getTime() - self.startWidgetTime;
			if(cancelTime < widgetRefreshIntervalSec)
				nextInterval = widgetRefreshIntervalSec - cancelTime;
			//console.log("resume: widget " + nextInterval + "ms");
			self.widgetTimer = setTimeout(self.next.bind(self), nextInterval);
		} else {
			var refreshTime = new Date().getTime() - self.startTime;
			if(refreshTime < refreshIntervalMs)
				nextInterval = refreshIntervalMs - refreshTime;
			//console.log("resume: All " + nextInterval + "ms");
			self.timer = setTimeout(self.start.bind(self), nextInterval);
		}
	};
}
