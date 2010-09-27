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

IS_Portal.ContentFooter = IS_Class.create();
IS_Portal.ContentFooter.prototype.classDef = function() {
	var self = this;
	this.initialize = function( opt ){
		this.id = opt.id;
		
		this.displayState = Object.extend( {
			isDisplay: function() {
				return false;
			},
			getUrl: function() {
				return "";
			},
			getTitle: function() {
				return "";
			}
		},opt );
		
		this.icons = IS_Customization.contentFooter.concat( opt.icons || []);
	}
	
	this.displayContents = function () {
		if( IS_Customization.contentFooter.length == 0 ) {
			this.elm_toolBar = document.createElement("div");
			return;
		}
		
		var toolBarTable = document.createElement("table");
		toolBarTable.setAttribute("width", "100%");
		toolBarTable.cellSpacing = "0";
		toolBarTable.cellPadding = "1";
		toolBarTbody = document.createElement("tbody");
		toolBarTable.appendChild(toolBarTbody);
		toolBarTr = document.createElement("tr");
		toolBarTbody.appendChild(toolBarTr);
		toolBarIconTd = document.createElement("td");
		toolBarIconTd.id = "mti_" + self.id;
		toolBarIconTd.style.align = "left";
		toolBarIconTable = document.createElement("table");
		toolBarIconTable.className = "toolBarTable";
		toolBarIconTable.id = self.id+"_tools";
		toolBarIconTable.style.width = "auto";
		toolBarIconTable.style.marginLeft = "2px";
		toolBarIconTable.cellPadding = "0";
		toolBarIconTable.cellSpacing = "0";
		toolBarIconTbody = document.createElement("tbody");
		toolBarIconTable.appendChild(toolBarIconTbody);
		toolBarIconTr = document.createElement("tr");
		toolBarIconTbody.appendChild(toolBarIconTr);
		toolBarIconTd.appendChild(toolBarIconTable);
		toolBarTr.appendChild(toolBarIconTd);

		this.elm_toolBar = toolBarTable;
		$( this.elm_toolBar ).addClassName("iframetoolbar");
		
		var buildTypes = [];
		for(var i=0;i<this.icons.length;i++){
			var icon = this.icons[i];
			if( icon.type ){
				if( !buildTypes.contains( icon.type ) ) {
					this.build( icon.type );
					
					buildTypes.push( icon.type );
				}
			} else if( icon.html ) {
				this.createHTML( icon.html );
			}
		}
		
		IS_Event.observe(toolBarIconTable, 'mousedown', this.common.bind(this, "this.dummy", false), false, toolBarIconTable.id);
		//IS_Widget.Maximize.elm_toolBar.appendChild(toolBarTable);
		
	}
	
	this.build = function(type, alt, imgUrl) {
		if (type == 'mail') {
			var divMail = this.createIcon(type, IS_R.lb_sendMail, 'email.gif');
			var td = divMail.parentNode;
			td.removeChild( divMail );
			
			var form = document.createElement("form");
			form.action = 'mailto:';
			form.method = 'post';
			form.style.margin = '0px';
			form.setAttribute('enctype',"text/plain");
			form.appendChild(divMail);
			
			form.id = divMail.id+"_form";
			
			td.appendChild(form);
		} else {
			msg.warn( IS_R.getResource( IS_R.lb_illegalToolbarItem,[type]));
		}
	}
	this.createHTML = function( html ) {
		var td = document.createElement("td");
		toolBarIconTr.appendChild( td );
		
		var nobr = document.createElement("nobr");
		td.appendChild( nobr );
		
		nobr.innerHTML = html;
	}
	
	this.createIcon = function(type, alt, imgUrl) {
		var div = $( document.createElement("div"));
		div.id = "mti_" + self.id + "_" + type;
		div.className = "toolbar-item "+type;
		
		var img = document.createElement("img");
		img.className = 'icon';
		img.title = alt;
		if(imgUrl){
			var url = '';
			if(/http:\/\//.test(imgUrl)){
				url = imgUrl;
			}else{
				url = imageURL+imgUrl;
			}
			img.src = url;
		}
		div.appendChild( img );
		
		var text = document.createElement("span");
		text.appendChild( document.createTextNode( alt ));
		div.appendChild( text );
		
		IS_Event.observe(div, 'mousedown', this.mousedown.bind(this, div), false, div.id);
		IS_Event.observe(div, 'mouseup', this.mouseup.bind(this, div), false, div.id);
		IS_Event.observe(div, 'mouseout', this.mouseup.bind(this, div), true, div.id);
		IS_Event.observe(div, 'click', this.common.bind(this, "this." + type), false, div.id);
		
		var td = document.createElement("td");
		td.appendChild(div);
		toolBarIconTr.appendChild(td);
		
		return div;
	}
	
	this.mouseup = function (div, e) {
		Element.removeClassName( div,"pressed");
	}
	this.mousedown = function (div, e) {
		Element.addClassName( div,"pressed");
	}
	
	this.common = function (func, e) {
		var callFunc = new Function("e", func + "(e);");
		callFunc.call(this, e);
	}
	
	this.mail = function(){
		if( !this.displayState.isDisplay() || !this.hasUrl() )
			return;
		
		var displayTitle = this.displayState.getTitle();
		if( displayTitle.length == 0 )
			displayTitle = IS_R.lb_notitle;
		
		var url = this.displayState.getUrl();
		
		function sendMail( response ){
			var title = response.responseText;
			title = eval("(" + title + ")");
			var docTitle = title[0];
			var pageTitle = title[1];
			
			var subject = IS_R.getResource(IS_R.lb_sendMailTitle,[docTitle, pageTitle]);
			
			var body = IS_R.getResource( IS_R.lb_maximizeSendMail,[pageTitle,encodeURIComponent( url )]);
			
			location.href = 'mailto:?subject='+subject+'&body='+body;
		}
		
		// Encoding of title
		var encode_opt = {
			method: 'get' ,
			asynchronous:true,
			onSuccess: sendMail,
			onFailure: function(t) {
				msg.error( IS_R.getResource( IS_R.ms_urlEncodingonException,[t.status,t.statusText,text,encoding ]));
			},
			onException: function(r, t){
				msg.error( IS_R.getResource( IS_R.ms_urlEncodingFailed,[t,text,encoding ]));
			}
		};
		
		var uriText = encodeURIComponent( displayTitle );
		var docTitleText = encodeURIComponent(document.title);
		
		var url2 = hostPrefix + "/encsrv?text=" + docTitleText + "&text=" + uriText + "&encoding=Windows-31J";
		
		AjaxRequest.invoke(url2, encode_opt);
	}
	
	this.dummy = function (){};
	
	this.loadContents = function () {
		this.displayContents();
	};
	
	this.hasUrl = function() {
		var url = this.displayState.getUrl()
		return url && url.length > 0;
	}
	this.displayStateChanged = function() {
		if( !this.hasUrl()) {
			this.elm_toolBar.addClassName("no-url");
		} else {
			this.elm_toolBar.removeClassName("no-url");
		}
	}
}
