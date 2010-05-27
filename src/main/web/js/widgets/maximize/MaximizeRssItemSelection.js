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


IS_Widget.MaximizeRssReader.RssItemSelection = function( widgetObj ){
	this.widget = widgetObj;
	
	this.sortProperty;
	this.nowCategoryIdx = -1;
	this.nowItemIdx = -1;
	this.nowCategory;	// Currently focused Category widget 
	this.onCategory;		// Current focus is on category or not
	this.nowDivItem;		// Currently focused RssItem
	this.nowTab;	//Currently shown tub
	this.nowTabIdx = -1;	//Currently shown tub index
	this.onTab;	//Current focus is on category or not
	this.activeRssReaders;
	
	this.keyBind();
}

IS_Widget.MaximizeRssReader.RssItemSelection.prototype = {
	/**
	 * 	↓↑		detail/move opened iframe
	 *  shift-↑↓　Category jump
	 * 	enter		detail/iframe/open category/close category
	 * 	→			detail/iframe/open category
	 * 	←			detail/iframe/close category
	 *  ctrl-→←     change tub (right)
	 * 	shift-m		send mail
	 * 	t			show detailed date and time/hide
	 * 	m			detail/switch iframe
	**/
	keyBind : function() {
		var self = this;
		/* key: ↓ */
		this.widget.keybind.keyFuncArray["40"] = function(e){
			self.itemMoveAction(1);
		}
		/* key: ↑ */
		this.widget.keybind.keyFuncArray["38"] = function(e){
			self.itemMoveAction( -1 );
		}
		/* key: ← */
		this.widget.keybind.keyFuncArray["37"] = function(e){
			self.viewAction("desc");
		}
		/* key: → */
		this.widget.keybind.keyFuncArray["39"] = function(e){
			self.viewAction("iframe");
		}
		/* key: ENTER */
		this.widget.keybind.keyFuncArray["13"] = function(e){
			self.viewAction("iframe");
		}
		/* M */
		this.widget.keybind.keyFuncArray["77"] = function(e){
			if(e.shift){
				self.widget.content.toolBarContent.mail();
			}
			else{
				if(self.widget.getBoolUserPref("iframeview")){
					self.widget.content.iframeview_offIconHandler();
				}else{
					self.widget.content.iframeview_onIconHandler();
				}
			}
		}
		/* T */
		this.widget.keybind.keyFuncArray["84"] = function(e){
			self.widget.content.dateIconHandler();
		}
		/* N */
		this.widget.keybind.keyFuncArray["78"] = function(e){
			self.itemMoveAction(1);
		}
		/* P */
		this.widget.keybind.keyFuncArray["80"] = function(e){
			self.itemMoveAction(-1);
		}
		/* H */
		this.widget.keybind.keyFuncArray["72"] = function(e){
			self.widget.content.showShortcutsIconHandler();
		}
		/* R */
		this.widget.keybind.keyFuncArray["82"] = function(e){
			self.widget.headerContent.refresh();
		}
		/* Q */
		this.widget.keybind.keyFuncArray["81"] = function(e){
			self.widget.turnbackMaximize();
		}
	},
	initialize : function () {
	},
	viewAction : function(viewType){
		this.widget.content.displayItem( this.nowItemIdx,viewType );
	},
	itemMoveAction : function(i) {
		var nowCategory = this.widget.content.currentCategory;
		var rssItems = nowCategory.content.getRssItems();
		var nextItemIdx = this.nowItemIdx +i;
		if( rssItems[nextItemIdx] ) {
			nowCategory.content.ignoreScroll = true;
			this.widget.content.displayItem(nextItemIdx);
		}
	}
}
