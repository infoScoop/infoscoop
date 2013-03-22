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


IS_Widget.MaximizeRssCategory = IS_Class.create();
IS_Widget.MaximizeRssCategory.prototype.classDef = function() {
	var widget;
	var self;
	//var userPref;
	
	var pageCount;
	
	var pageSize = is_getPropertyInt(rssPageSize, -1);
	
	this.initialize = function(widgetObj){
		self = this;
		widget = widgetObj;
	}
	
	this.setSelectedItem = function( no,withoutScroll ) {
		var rssContentView = this.rssContentView;
		
		var oldItem = this.selectedItem;
		if( oldItem ) {
			oldItem.selected = false;
			
			this.repaintItem( oldItem );
		}
		
		var rssItem = widget.content.rssContent.rssItems[no]
		if( rssItem ) {
			rssItem.selected = true;
			this.selectedItem = rssItem;
		}
		
		if( rssContentView ) {
			var viewportPos = rssContentView.elm_viewport.scrollTop;
			var itemDiv = $( widget.id+"_item_"+no );
			if( itemDiv )
				this.repaintItem( rssItem );
			
			if( !withoutScroll ) {
				var itemPos = rssContentView.getItemPosition( no );
				if( !rssContentView.itemList[no] ) return;
				var itemHeight = ( rssContentView.itemList[no].height || 50) +( Browser.isIE?0:6);
				var viewportHeight = rssContentView.elm_viewport.offsetHeight;
				
				if( !( viewportPos < itemPos && itemPos +itemHeight < viewportPos +viewportHeight )) {
					var scrollPos;
					if( viewportPos -itemPos < 0 ) {
						scrollPos = itemPos -( viewportHeight -itemHeight );
					} else {
						scrollPos = itemPos;
					}
					rssContentView.elm_viewport.scrollTop = scrollPos +'px';
				}
			}
		}
	}
	
	this.repaintItem = function( item ) {
		var view = this.rssContentView;
		
		var itemNo = widget.content.rssContent.rssItems.indexOf( item );
		var itemDiv = $( widget.id+"_item_"+itemNo );
		if( itemDiv  ) {
			itemDiv.style.backgroundColor =
				( item.selected ? "#BCCCE7":( itemNo %2 == 0 ? "#FFFFFF":"#FFFFFF"));
		}
	}
	
	this.isNoDisplayItem = function() {
		if( widget.content.rssContent && widget.content.rssContent.filter
			&& !widget.content.rssContent.isLoadPageCompleted() )
			return false;
		
		return ( widget.content.rssContent.rssItems.length == 0 );
	}
	
	this.showNoDisplayItemError = function(rss) {
		var contents = document.createElement("div");
		contents.className = "rssItem";
		
		var errorContent;
		if( rss && (!rss.rssItems || !rss.rssItems.length == 0) ) {
			if( widget.isError && !widget.isSuccess ) {
				errorContent = widget.elm_widgetContent.innerHTML;
			} else {

				errorContent = IS_R.lb_noNewInfo;
			}
		} else {

			errorContent = IS_R.lb_noDiplayItem;
			if(widget.isAuthenticationFailed())
			  errorContent += "<br/>" + IS_R.ms_pleaseTurnbackAndSetAuthInfo;
		}
		
		if( errorContent )
			contents.innerHTML = errorContent;
		
		if(widget.elm_widgetMaximizeContent.firstChild)
			widget.elm_widgetMaximizeContent.removeChild( widget.elm_widgetMaximizeContent.firstChild );
		
		widget.elm_widgetMaximizeContent.appendChild(contents);

		widget.maximize.content.clearDetail();
	}
	
	this.displayContents = function(){
		//Button to operate several things at once: Corresponds AtomPub AtomPub
		var maximizeButtons = $("MaximizeButtons_"+widget.id );
		if( !maximizeButtons ) {
			var buttonsDiv = document.createElement("div");
			buttonsDiv.id = "MaximizeButtons_"+widget.id;
			buttonsDiv.className = "maximizeButtons";
			maximizeButtons = buttonsDiv;
			maximizeButtons.style.display = "none";
			
			if( widget.firstChild ) {
				widget.elm_widgetMaximizeContent.insertBefore( maximizeButtons,widget.elm_widgetContent.firstChild );
			} else {
				widget.elm_widgetMaximizeContent.appendChild( maximizeButtons );
			}
		}
		maximizeButtons.innerHTML = "";
		IS_Event.unloadCache(widget.id);
		
		var rss = widget.content.rss;//widget.originalWidget.content.rss;at 2009/6/25 9:40
		//widget.rss = rss;at 2009/6/25 9:40
		if (rss && rss.atompub_buttons && !widget.isMulti) {
			//var buttonsDom = dojo.dom.createDocumentFromText("<buttons>" + this.rssContent.rss.msd_buttons + "</buttons>");
			var buttonsDom = is_createDocumentFromText(rss.atompub_buttons);
			var buttonsElm = buttonsDom.documentElement;
			var all = buttonsElm.getAttribute("all");
			var each = buttonsElm.getAttribute("each");
			widget.atomPubAll = (!all || all == "true");
			widget.atomPubEach = (each && each == "true");
			var buttonElms = buttonsDom.getElementsByTagName("button");
			widget.buttonElms = buttonElms;
			
			if( !widget.content.rss.entriesXml )
				widget.content.rss.entriesXml = IS_Widget.MaximizeRssReader.getEntriesXml( widget );
			
			if(widget.atomPubAll) {
				for (var i = 0; i < buttonElms.length; i++) {
					var buttonDef = IS_Widget.MaximizeRssReader.extractButtonDef( buttonElms[i] );
					
					var button = document.createElement("input");
					button.type = "button";
					button.value = buttonDef.title;
					maximizeButtons.appendChild(button);
					
					IS_Event.observe(button, "click", IS_Widget.MaximizeRssReader.postAtomPub.bind(this,widget, false,buttonDef ), false, "maximize_event" + widget.id);
				}
				maximizeButtons.style.display = "";
			}
		} else {
			maximizeButtons.style.display = "none";
		}
		
		if( widget.content.rssContent && widget.content.rssContent.rssItems ) {
			widget.content.rssContent.rssItems.each( function( rssItem ) {
				if( rssItem ) {
					delete rssItem.atompub_checked;
				}
			});
		}
		
		if( this.isNoDisplayItem() )
			return this.showNoDisplayItemError(rss);
		
		var viewportHeight = this.viewportHeight || 200;
		this.rssContentView = new IS_Widget.RssReader.RssContentView( widget,widget.content.rssContent,{
			height :  viewportHeight,
			render : IS_Widget.MaximizeRssReader.RssItemRender,
			isCanvas: true,
			renderContext: {
				doLineFeed : function() { return widget.getBoolUserPref("doLineFeed") },
				showDatetime : function() { return widget.getBoolUserPref("showDatetime") }
			}
		} );

		this.rssContentView.isStatic = false;
		widget.elm_widgetMaximizeContent.appendChild( this.rssContentView.elm_root );
		this.rssContentView.onContentHeightChange();
		this.rssContentView.view();
	};
	this.handleLoadPageCompleted = function( pageNo,page ) {
		var view = this.rssContentView;
		var content = widget.content.rssContent;
		if( view && view.elm_viewport.offsetHeight !== undefined ) {
			if( content.isLoadPageCompleted() )
				view.clearContents( true );
			
			view.view();
			if( !view.stopAutoPageLoad )
				view.onContentHeightChange();
		}
		
		if( content && content.isLoadPageCompleted() && content.filter &&
			content.rssItems.length == 0 ) {
//				this.showNoDisplayItemError();
				this.displayContents();
		}
	}
}
