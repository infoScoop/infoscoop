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

IS_Widget.WidgetRanking = IS_Class.create();
IS_Widget.WidgetRanking.prototype.classDef = function() {
	this.isRequestWidget = true;
	var widget;
	var self = this;
	var rssItems = [];
	var contents;
	
	this.initialize = function(widgetObj){
		widget = widgetObj;
	}
	
	this.displayContents = function () {
		var nowTime = new Date().getTime();
		var logoffDatetime = parseInt( IS_Portal.logoffDateTime );
		// If logoffDatetime has initial value, login initially or login as guest user; no need to calculate previous logout
		var freshTime = (logoffDatetime <= 0 || isNaN( logoffDatetime ))? 0 : nowTime - logoffDatetime;
		var freshDaysTime = freshDays *24 *60 *60 *1000;
		var freshTime = nowTime -Math.max( freshTime,freshDaysTime );
		
		var contents = $.DIV();
		if(self.data.length > 0) {
			var ul = $.OL();
			ul.className = 'widgetRank';
			var cnt = 0;
			self.data.each(function(rank){
				if(rank.excepted) return;
				var menuId = rank.menuId;
				var menuItem = null;
				if(menuId){
					menuItem = IS_TreeMenu.findMenuItem(menuId);
					if(!menuItem) return;
				}
				var title = rank.title || menuItem && menuItem.title || IS_R.lb_notitle;
				var href = rank.href || menuItem && menuItem.href;
				
				var li = $.LI();
				
				var table = $.TABLE({
					cellPadding:0,
					cellSpacing:0,
					style:'tableLayout:fixed;width:100%'
				});
				li.appendChild(table);
				var tbody = $.TBODY();
				table.appendChild(tbody);
				var tr = $.TR();
				tbody.appendChild(tr);
				var leftTd = $.TD();
				tr.appendChild(leftTd);
				var rightTd = $.TD({style:'width:50px'});
				tr.appendChild(rightTd);
				
				table = $.TABLE({
					cellPadding:0,
					cellSpacing:0
				});
				li.appendChild(table);
				leftTd.appendChild(table);
				tbody = $.TBODY();
				table.appendChild(tbody);
				tr = $.TR();
				tbody.appendChild(tr);
				var rankTd = $.TD({width:'16',align:'center'});
				tr.appendChild(rankTd);
				var iconTd = $.TD({width:'16'});
				tr.appendChild(iconTd);
				var titleTd = $.TD();
				tr.appendChild(titleTd);
				var newTd = $.TD();
				tr.appendChild(newTd);
				
				if(cnt%2 == 1) li.className = 'even';
				/*var titleAttrs = {
					Class:"widgetRankTitle" + (cnt<3 ? " rank"+(cnt+1) : ""),
					title:title
				};
				var titleElm = href ? $.A(titleAttrs, title) : $.SPAN(titleAttrs, title);
				if(href) {
					titleElm.href = href;
					IS_Event.observe(titleElm, 'click', function(){
						IS_Portal.buildIFrame(this)
					}.bind(titleElm), widget.id);
				}*/
				var rankDiv = $.DIV({Class:"widgetRankRank" + (cnt<3 ? " rank"+(cnt+1) : "")}, cnt+1);
				rankTd.appendChild(rankDiv);
				var iconDiv = $.DIV({Class:'widgetRankIcon'});
				IS_Widget.setIcon(iconDiv, menuItem ? menuItem.type || rank.type : rank.type );
				iconTd.appendChild(iconDiv);
				var titleAttrs = {
					title:title,
					Class:'widgetRankTitle'
				}
				var titleElm = href ? $.A(titleAttrs, title) : $.SPAN(titleAttrs, title);
				if(href) {
					titleElm.href = href;
					IS_Event.observe(titleElm, 'click', function(){
						IS_Portal.buildIFrame(this)
					}.bind(titleElm), widget.id);
				}
				titleTd.appendChild(titleElm);
				
				//Judge new elements at client because shared cash for all user is used
				if(rank.rankinTime && rank.rankinTime > freshTime){
					var newElm = $.SPAN({Class:'widgetRankNew'},
						//'New!!'
						IS_R.lb_new
					);
					newTd.appendChild(newElm);
				}
				
				var addButton = $.DIV({Class:'widgetRankAdd'});
				if(!menuItem || !IS_Portal.isChecked(menuItem)){
					//Enable add button if the item is not menu item or is not dropped yet
					self.enableAddButton(addButton);
				} else {
					self.disableAddButton(addButton, menuItem);
				}
				IS_Event.observe(addButton, 'click', function(title, menuItem, rank){
					if(this.disabled) return;
					if(menuItem){
						IS_TreeMenu.addMenuItem(menuItem);
						if( !menuItem.multi)
							self.disableAddButton(this, menuItem);
					} else {
						var widgetId = "w_" + new Date().getTime();
						var properties = {url: rank.url};
						widgetConf = IS_WidgetsContainer.WidgetConfiguration.getConfigurationJSONObject(rank.type, widgetId, 1, title, rank.href, properties);
						var widget = IS_WidgetsContainer.addWidget( IS_Portal.currentTabId, widgetConf );
						IS_Widget.setWidgetLocationCommand(widget);
						IS_Portal.widgetDropped( widget );
					}
				}.bind(addButton, title, menuItem, rank), widget.id);
				rightTd.appendChild(addButton);
				
				ul.appendChild(li);
				cnt++;
			});
			contents.appendChild(ul);
		} else {
			contents.appendChild($.DIV({Class:'widgetRankNone'}, IS_R.lb_noDiplayItem));
		}
		
		var time = $.DIV({Class:'widgetRankTime'},

			IS_R.lb_lastmodified+":"+self.lastmodified
		);
		contents.appendChild(time);
		
		if(widget.elm_widgetContent.firstChild){
			widget.elm_widgetContent.replaceChild(contents, widget.elm_widgetContent.firstChild);
		}else{
			widget.elm_widgetContent.appendChild(contents);
		}
	};
	
	this.disableAddButton = function(addButton, menuItem){

		var text = document.createTextNode(IS_R.lb_added);
		if(addButton.firstChild) {
			addButton.replaceChild(text, addButton.firstChild);
		} else {
			addButton.appendChild(text);
		}
		addButton.style.color = 'gray';
		addButton.disabled = true;
		IS_EventDispatcher.addListener('closeWidget', menuItem.id, function(){
			self.enableAddButton(this);
		}.bind(addButton, menuItem));
	}
	
	this.enableAddButton = function(addButton){

		var text = document.createTextNode(IS_R.lb_add+">>");
		if(addButton.firstChild) {
			addButton.replaceChild(text, addButton.firstChild);
		} else {
			addButton.appendChild(text);
		}
		addButton.style.color = '#0000CC';
		addButton.disabled = false;
	}
	
	this.loadContentsOption = {
		preLoad: function(){
			self.loadContentsOption.headers = [
				'X-IS-MAXCOUNT', widget.widgetPref.maxCount.value,
				'X-IS_FRESHDAY', widget.widgetPref.freshDay.value,
				'X-IS-CACHELIFETIME', widget.widgetPref.cacheLifetime.value
			];
			return true;
		},
		request : true,
		url: 'widrank',
		onSuccess : function( response ) {
			var rank = response.responseText.evalJSON();
			this.data = rank.data;
			this.lastmodified = rank.lastmodified;
			this.displayContents();
		}.bind( this )
	};
};
