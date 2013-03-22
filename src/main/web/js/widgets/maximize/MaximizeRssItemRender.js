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


/*( function() {
	var render = IS_Class.create();
	render.instances = {};
	render.availableKeys = [];
	render.lock;
	render.no = 0;
	render.getInstance = IS_Widget.RssReader.RssItemRender.getInstance;
	render.releaseInstance = IS_Widget.RssReader.RssItemRender.releaseInstance;
	render.newInstance = function() {
		return new IS_Widget.MaximizeRssReader.RssItemRender();
	}
	render.tableRowRender = true;
	
	IS_Widget.MaximizeRssReader.RssItemRender = render;
})();*/

IS_Widget.MaximizeRssReader.RssItemRender = IS_Class.create();
IS_Widget.MaximizeRssReader.RssItemRender.instances = {};
IS_Widget.MaximizeRssReader.RssItemRender.availableKeys = [];
IS_Widget.MaximizeRssReader.RssItemRender.lock;
IS_Widget.MaximizeRssReader.RssItemRender.no = 0;
IS_Widget.MaximizeRssReader.RssItemRender.getInstance = IS_Widget.RssReader.RssItemRender.getInstance;
IS_Widget.MaximizeRssReader.RssItemRender.releaseInstance = IS_Widget.RssReader.RssItemRender.releaseInstance;
IS_Widget.MaximizeRssReader.RssItemRender.newInstance = function() {
	return new IS_Widget.MaximizeRssReader.RssItemRender();
}
IS_Widget.MaximizeRssReader.RssItemRender.tableRowRender = false;

[
	"buildRssItemDiv",
	"buildRssPubDate",
	"aTagOnClick",
	"removeChildren"
].each( function( v ) {
		IS_Widget.MaximizeRssReader.RssItemRender.prototype[v] =
			IS_Widget.RssReader.RssItemRender.prototype[v];
});
IS_Widget.MaximizeRssReader.RssItemRender.getDefaultHeight = function( context ) {
	
	return 17+1+( context.showDatetime() ? 15:0 );
}
IS_Widget.MaximizeRssReader.RssItemRender.prototype.classDef = function() {
	this.initialize = function() {
		this.init = true;
	}
	this.render = function ( context,rssItem,itemNumber ) {
		var init = this.init;
		
		var widgetObj = context.widget;
		this.widget = widgetObj;
		this.rssItem = rssItem;
		this.itemNumber = itemNumber;
		
		var widget = this.widget;
		var br = true;
		var pubDate = context.showDatetime();
		
		var opt = {
			widget : widget,
			rssItem : rssItem,
			itemNumber : itemNumber,
			br : true,
			pubDate : pubDate
		}
		
		if( init ) {
			this.containerTr = document.createElement("div");
			this.containerTr.style.paddingLeft = '2px';
			this.containerTr.style.paddingBottom = this.containerTr.style.paddingTop = 0;
			this.containerTr.style.borderBottom = "1px solid #CCC";
			this.containerTr.__key__ = this.key;
			
			this.imgTd = document.createElement("div");
			this.imgTd.style.verticalAlign = "top";
			this.imgTd.width = "1%";
			this.imgTd.style.cssFloat = this.imgTd.style.styleFloat = "left";
			this.containerTr.appendChild( this.imgTd);
		} else {
			this.removeChildren( this.imgTd );
		}
		
		this.containerTr.id = widget.id + '_item_'+itemNumber;
		
		var bgColor;
		if( rssItem.selected ) {
			bgColor = "#BCCCE7";
		} else {
			bgColor = ( itemNumber %2 == 0 ? "#FFFFFF":"#FFFFFF")
		}
		this.containerTr.style.backgroundColor = bgColor;
		
		if(pubDate && rssItem.creatorImg.length > 0 && widget.widgetType == "Information2"){
			this.imgTd.innerHTML = rssItem.creatorImg;
			
			if( this.imgTd.firstChild ) {
				this.imgTd.firstChild.align = "left";
				this.imgTd.firstChild.style.height = "2em";
				this.imgTd.firstChild.style.width = "auto";
			}
			
			this.imgTd.style.display = "";
		} else {
			this.imgTd.style.display = "none";
		}
		
		//Title
		this.buildRssItemDiv( widget,opt,true );
			
		if( init ) {
			this.textTitle.style.cursor = 'pointer';
			this.textTitle.style.color = '#0000dd';
			
			this.atomPubCheckbox = document.createElement("input");
			this.atomPubCheckbox.type = "checkbox";
			this.atomPubCheckbox.className = "atomPubCheckbox";
			this.rssItemDiv.insertBefore(this.atomPubCheckbox, this.textTitle);
			
			this.rssItemTd = document.createElement("div");
			this.rssItemTd.style.overflow = "hidden";
			this.rssItemTd.style.width = "100%";
			this.rssItemTd.appendChild( this.rssItemDiv );
			this.containerTr.appendChild( this.rssItemTd );
		}

		//if (widget.originalWidget.content.rss.atompub_buttons) {
		if(widget.atomPubAll && rssItem.link_edit) {
			this.atomPubCheckbox.style.display = "";
			this.atomPubCheckbox.value = rssItem.link;
			if( Browser.isIE ) {
				this.atomPubCheckbox.defaultChecked = rssItem.atompub_checked;
			} else {
				this.atomPubCheckbox.checked = rssItem.atompub_checked;
			}
		} else {
			this.atomPubCheckbox.style.display = "none";
		}
		
		//Detailed time and creator
		this.buildRssPubDate( widget,opt );
		if( init ) {
			this.rssPubDateTd = document.createElement("div");
//			this.rssPubDateTd.style.textAlign = "right";
			this.rssPubDateTd.style.paddingLeft = '2px';
			this.rssPubDateTd.style.clear = "both";
			this.rssPubDateTd.appendChild( this.rssPubDate );
			this.containerTr.appendChild( this.rssPubDateTd );
		}
		
		if( !pubDate ) {
			this.rssPubDateTd.style.display = "none";
		} else {
			this.rssPubDateTd.style.display = "";
		}
		
		if( (this.widget != null) && (rssItem == this.widget.content.displayRssItem)){
			//containerTr.style.backgroundColor = '#CCCCF7';
			this.containerTr.style.backgroundColor = '#BCCCE7';
		}
		
		if( init ) {
			IS_Event.observe(this.containerTr, 'mouseover', this.mouseover.bind(this));
			IS_Event.observe(this.containerTr, 'mouseout', this.mouseout.bind(this));
			
			IS_Event.observe(this.containerTr, 'click', this.itemClicked.bind(this), false, "maximize_event" + widget.id);
			IS_Event.observe(this.atomPubCheckbox, 'click',this.atompubCheckChanged.bind(this), false, "maximize_event" + widget.id);
		}
		
		this.init = false;
		
		return this.containerTr;
	}
	this.itemClicked = function(e ) {
		//this.widget.content.setSelectedItem( this.itemNumber,true );
		
		this.widget.maximize.content.displayItem( this.itemNumber );
		//this.widget.parent.content.displayItem( this.itemNumber );
	}
	this.mouseover = function(){
		if( this.widget && this.widget.maximizeRender.selectedItem != this.rssItem ){
			this.tempBgColor = this.containerTr.style.backgroundColor;
			this.containerTr.style.backgroundColor = '#E0E0F7';
		}
	}
	
	this.mouseout = function(){
		if( this.widget && this.widget.maximizeRender.selectedItem != this.rssItem && this.tempBgColor ){
			this.containerTr.style.backgroundColor = this.tempBgColor;
			this.tempBgColor = "#E0E0F7";
		}
	}
	
	this.atompubCheckChanged = function() {
		if( this.rssItem ) {
			this.rssItem.atompub_checked = this.atomPubCheckbox.checked;
		}
	}
}
IS_Widget.MaximizeRssReader.RssItemRender.forceFocusControl = true;
IS_Widget.MaximizeRssReader.RssItemRender.detailIframe = null;
IS_Widget.MaximizeRssReader.RssItemRender.getDetailIframe = function() {
	var maxIframe = IS_Widget.MaximizeRssReader.RssItemRender.detailIframe;
	if( maxIframe ) {
		maxIframe.src = "./blank.html";
		
		return maxIframe;
	}
	var maxIframe;
	maxIframe = document.createElement("iframe");
	maxIframe.name = "maximize_ifrm";
	maxIframe.style.border = "0px";
	maxIframe.id = "maximize_ifrm";
	maxIframe.style.display = "none";
	maxIframe.style.width = "100%";
	maxIframe.style.height = "100%";
	maxIframe.src = "./blank.html";
	var maxIframeOnload = function(){
		if(Browser.isIE){
			if( IS_Widget.MaximizeRssReader.RssItemRender.forceFocusControl )
				window.focus();
		}else{
			maxIframe.blur();
		}
	}
	IS_Event.observe(maxIframe, 'load', maxIframeOnload );
	
	IS_Widget.MaximizeRssReader.RssItemRender.detailIframe = maxIframe;
	
	return maxIframe;
}
