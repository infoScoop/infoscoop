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

var IS_Class = {
	create: function() {
		return function() {
			if(this.classDef) {
				this.classDef.prototype = this;
				var instance = new this.classDef();
				instance.initialize.apply(instance, arguments);
				return instance;
			}
			this.initialize.apply(this, arguments);
			return this;
		}
	},
	extend: function(superClass) {
		var subClass = function() {
			var tempClassDef = function(){};
			for(var i in this){
				if(i != "initialize")
					tempClassDef.prototype[i] = this[i];
			}
			var instance = new tempClassDef();
			if(superClass.prototype.classDef) {
				superClass.prototype.classDef.apply(instance);
			}
			var superClassDef = superClass.prototype.classDef ? superClass.prototype.classDef : function(){};
			for(var i in superClass.prototype) {
				if(i != "classDef") {
					superClassDef.prototype[i] = superClass.prototype[i];
				}
			}
			var superInstance = new superClassDef();
			instance._super = {};
			for(var i in superInstance) {
				(function(){
					var funcName = i;
					instance._super[i] = function() {
						superInstance[funcName].apply(instance, arguments);
					}
				})();
			}
			if(superInstance.initialize) {
				if(!instance.initialize)
					instance.initialize = superInstance.initialize;
				instance.initialize.apply(instance, arguments);
			}
			if(this.classDef)
				this.classDef.apply(instance);
			if(this.initialize)
				instance.initialize = this.initialize;
			instance.initialize.apply(instance, arguments);
			return instance;
		}
		for(var i in superClass.prototype) {
			if(i != "classDef") {
				subClass.prototype[i] = superClass.prototype[i];
			}
		}
		for(var i in superClass){
			if(i != 'prototype')
				subClass[i] = Object.clone(superClass[i]);
		}
		return subClass;
	}
}

function getWindowHeight(){
	return (window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight || 0);
}

function getNodeID(parent, id) {
	var ln = parent.childNodes.length;
	for (var z=0; z<ln; z++) {
		if (parent.childNodes[z].id == id) return parent.childNodes[z];
	}
	return null;
}

function findPosX(obj) {
    var pos = 0;
    while (obj != null) {
        pos += obj.offsetLeft;
        obj = (obj.offsetParent)? obj.offsetParent : null;
    }
    return pos;
}

function findPosY(obj) {
    var pos = 0;
    while (obj != null) {
        pos += obj.offsetTop;
        obj = (obj.offsetParent)? obj.offsetParent : null;
    }
    return pos;
}

function getWindowSize(flag) {
  var offset = 0;
  if( typeof( window.innerWidth ) == 'number' ) {
    offset = window["inner" + ((flag)? "Width" : "Height")];
  } else if( document.documentElement &&
      ( document.body.offsetWidth || document.body.offsetHeight ) ) {
    offset = document.body["offset" + ((flag)? "Width" : "Height")];
  }

  return offset;
}

function isInObject(x,y,id) {
	var obj = document.getElementById(id);
	if(!obj)return false;
	var posx = findPosX(obj);
	var posy = findPosY(obj);
	/*alert ("x :" + x + " y: " + y + " posx : " + posx + " posy :" + posy + " height :" + obj.offsetHeight + 
			" width :" + obj.offsetWidth);*/
	return (x>=posx && x<posx+obj.offsetWidth &&
			y>=posy && y<posy+obj.offsetHeight);
}

function findHostURL(flag) {
	var host = location.protocol + "//" + location.hostname;
	if(location.port && location.port != '')
		host += ":" + location.port;
	if ( !flag ) {
		var path = location.pathname;
		if ( path.charAt(0) == '/' ) {
			path = path.substr(1);
		}
		if( path.indexOf('/') == -1 )
		  return host;
		  
		if((p = path.lastIndexOf('/')) != -1){
			return host + "/" + path.substring(0,p);
		}
		/*if ( (p = path.indexOf('/')) != -1 ) {
			return host + "/" + path.substring(0,p);
		}*/
		return host + '/' + path;
	}else {
		return host;	
	}
}

function is_getProxyUrl(url, filter,opts ) {
	if(typeof IS_Preview != "undefined" && IS_Preview.rewriteUrl){
		url = IS_Preview.rewriteUrl(url);
	}
	
	if( !opts ) opts = {};
	if( typeof opts == "string")
		opts = { filterEncoding: opts };
	
	opts.filter = filter;
	opts.url = url;
	opts = $H( opts ).toQueryString();
//	return proxyServerURL + "?url=" + encodeURIComponent(url) + "&filter=" + filter;
	return proxyServerURL +"?" +opts;
}

/** copy from is_getProxyUrl */
function is_getLocalhostProxyUrl(url, filter,filterEncoding ) {
	var proxyServerURL = localhostPrefix+"/proxy";
	
	if(typeof IS_Preview != "undefined" && IS_Preview.rewriteUrl){
		url = IS_Preview.rewriteUrl(url);
	}
//	return proxyServerURL + "?url=" + encodeURIComponent(url) + "&filter=" + filter;
	return proxyServerURL + "?filter=" + filter + 
		( filterEncoding ? "&filterEncoding="+filterEncoding : "")+
		"&url=" + encodeURIComponent(url);
}

function getChildrenByTagName (node, tagName) {
	var ln = node.hasChildNodes()? node.childNodes.length: 0;
	var arr = [];	
	for (var z=0; z<ln; z++) {
		if (node.childNodes[z].nodeName == tagName ||
			node.childNodes[z].nodeName == tagName.toUpperCase()) arr.push(node.childNodes[z]);
	}
	return arr;
}


getColonTag = function(node, tag, name) {
	return (Browser.isIE) ? node.getElementsByTagName(tag+":"+name)[0] : node.getElementsByTagName(name)[0];
}

var Browser = new Object();

Browser.isMozilla = (typeof document.implementation != 'undefined') && (typeof document.implementation.createDocument != 'undefined') && (typeof HTMLDocument!='undefined');
Browser.isIE = window.ActiveXObject ? true : false;
Browser.isFirefox = (navigator.userAgent.toLowerCase().indexOf("firefox")!=-1);
Browser.isFirefox3 = (navigator.userAgent.toLowerCase().indexOf('firefox/3.')>-1);
Browser.isOpera = (navigator.userAgent.toLowerCase().indexOf("opera")!=-1);

Browser.isSafari = (navigator.userAgent.toLowerCase().indexOf("safari")!=-1);
Browser.Safari = {};
Browser.Safari.version = (function() {
	var versions = navigator.userAgent.match(/.*AppleWebKit\/(\d+).+Safari\/(\d+).*/);
	if( !versions || versions.length < 2 )
		return NaN;
	
	var webkitVer = parseInt( versions[1] );
	var safariVer = parseInt( versions[2] );
	
	if( isNaN( webkitVer )|| isNaN( safariVer ))
		return NaN;
	
	if( webkitVer < 412 ) {
		return 1;
	} else if( webkitVer < 522 ) {
		return 2;
	}
	
	return 3;
})();
Browser.isSafari1 = ( Browser.isSafari && Browser.Safari.version == 1 );
Browser.Safari.responseText = function( text ) {
	var escapeText = escape( text );
	
	if( escapeText.indexOf("%u") < 0 && escapeText.indexOf("%") > -1)
		text = decodeURIComponent( escapeText )
	
	return text;
}

if (Browser.isMozilla) {
	HTMLElement.prototype.removeNode = function() {
		this.parentNode.removeChild(this);
	}
}

SwappableComponent = new Object();
SwappableComponent.build = function (data, id) {

	var div_before = data.before;
	var div_after = data.after;
	div_after.style.display = "none";
	var style_over = data.style_over;
	var style_out = data.style_out;
	var onclick_before = data.before_onclick;
	var onclick_after = data.after_onclick;

	div_before.className = style_out;
	div_after.className = style_out;

	swap = function (o1,o2) {
	    if ( o2 ) {
			o1.style.display = "none";
			o2.style.display = "block";
		}
	}
	
	onBeforeClicked = function () {
		swap(this,div_after);
		if(onclick_before) onclick_before(this);
	}
	
	onAfterClicked = function () {
		swap(this,div_before);
		if(onclick_after) onclick_after(this);
	}
	
	onMouseOut = function () {
		this.className = style_out;
		if (this.onMouseOut ) this.onMouseOut();
	}
	
	onMouseOver = function () {
		this.className = style_over;
		if (this.onMouseOver ) this.onMouseOver();
	}
	IS_Event.observe(div_before, "click", onBeforeClicked.bind(div_before), false, id);
	IS_Event.observe(div_before, "mouseover", onMouseOver.bind(div_before), false, id);
	IS_Event.observe(div_before, "mouseout", onMouseOut.bind(div_before), false, id);
	IS_Event.observe(div_after, "mouseover", onMouseOver.bind(div_after), false, id);
	IS_Event.observe(div_after, "click", onAfterClicked.bind(div_after), false, id);
	IS_Event.observe(div_after, "mouseout", onMouseOut.bind(div_after), false, id);
}

/*
* Convert string to boolean for the value, true/false
* @boolean :Srt object
* return :boolean object
*/
function getBooleanValue(value) {
	if(typeof value == "string") {
		return ( /true/i.test(value) || ( !isNaN( value ) && parseInt( value ) == 0 ) );
	}
	
	return !(!value);
}

/**
 * Reverse the indicated boolean value of the property for the object
 * @obj :JSON object
 * @name :property of the object that judges the value
 */
function reverseBooleanProperty(obj, name) {
	var b = getBooleanValue(obj[name]);
	if ( b ) {
		obj[name] = "false";
	} else {
		obj[name] = "true";
	}
}

/*
 * Create date object by using date string
 * @obj :rssItem object
 * @dateStr :data string
 * return :RssDate object {date, timezone}
*/
function createGMTFormat(date){
	if (!date || date == "Invalid Date" || date == "NaN" ) {
		return date;
	}
	
	var eee = date.toString().substr(0, 3);
	var mmm = date.toString().substr(4, 3);
	var dd = ((date.getDate() < 10) ? "0":"" ) + date.getDate();
	var yyyy = date.getFullYear();
	var hh = ((date.getHours() < 10) ? "0":"" ) + date.getHours();
	var mm = ((date.getMinutes() < 10) ? "0":"" ) + date.getMinutes();
	var ss = ((date.getSeconds() < 10) ? "0":"" ) + date.getSeconds();
	var z = date.getTimezoneOffset();
	if(z > 0) {
		z = z * -1;
		z = '-' + (((z/60 < 10) ? "0":"" ) + z/60) + '00';
	} else {
		z = z * -1;
		z = '+' + (((z/60 < 10) ? "0":"" ) + z/60) + '00';
	}
	
	return eee + ', ' + dd + ' ' + mmm + ' ' + yyyy + ' ' + hh + ':' + mm + ':' + ss + ' ' + z; 
	
}
/*
 * Create Data object by using string of W3CDTFDate format
 * @dateStr :data string
 * return {date, timezone}
 */
function parseW3CDTFDate(dateStr){
	var timezone = false;
	if(dateStr.charAt(19) && (dateStr.charAt(19) == "+" || dateStr.charAt(19) == "-") ){
		timezone = dateStr.substring(19);
		dateStr = dateStr.substring(0, 19);
	}
	var date = new Date();
	var year = parseInt(dateStr.substr(0, 4));
	var month = parseInt(dateStr.substr(5, 2), 10);
	var day = parseInt(dateStr.substr(8, 2), 10);
	var hour = parseInt(dateStr.substr(11, 2), 10);
	var minute = parseInt(dateStr.substr(14, 2), 10);
	var second = parseInt(dateStr.substr(17, 2), 10);
	date.setYear(year);
	date.setMonth((month - 1));
	date.setDate(day);
	date.setHours(hour);
	date.setMinutes(minute);
	date.setSeconds(second);
	return {date:date, timezone:timezone};
}

function escapeXMLEntity(str){
	if ( str == '' || str == null ) {
		return str;
	}
	str = str +"";
	str = str.replace(/&/g, "&amp;");
	str = str.replace(/</g, "&lt;");
	str = str.replace(/>/g, "&gt;");
	str = str.replace(/"/g, "&quot;");
	str = str.replace(/'/g, "&apos;");
	return str;
}

function escapeHTMLEntity(str){
	if ( str == '' || str == null ) {
		return str;
	}
	str = str +"";
	str = str.replace(/&/g, "&amp;");
	str = str.replace(/</g, "&lt;");
	str = str.replace(/>/g, "&gt;");
	str = str.replace(/"/g, "&quot;");
	return str;
}

function unescapeHTMLEntity(str){
	if ( str == '' || str == null ) {
		return str;
	}
	str = str.replace(/&amp;/g, "&");
	str = str.replace(/&lt;/g, "<");
	str = str.replace(/&gt;/g, ">");
	str = str.replace(/&quot;/g, "\"");
	return str;
}

function getText(obj) {
	if(typeof obj == "string")
		return obj
	var text = "";
	var isFirst = true;
	for(var i in obj) {
		try{
			if(typeof obj[i] == "string") {
				if(!isFirst) text += ",";
				else isFirst = false;
				text += i + "=" + obj[i];
			}
		}catch(e){
		}
	}
	return text;
}

function getErrorMessage(e){
	if(e) {
		if(e.description)
			return e.name + " : " + e.description;
		else if(e.message)
			return e.name + " : " + e.message + " (" + e.fileName + " : " + e.lineNumber + ")";
		return e;
	}
	return "";
}

function encodeURL(str) {
	if(!str) return "";
	// Unicode to URL encoded UTF-8
	var i, encoded_str, char_code, padded_str;
	encoded_str = "";
	for (i = 0; i < str.length; i++) {
		char_code = str.charCodeAt(i);
		if (char_code == 0x20) {
			// space -> "+"
			encoded_str += "+";
		}
		else {
			// else 1
			if (((0x30 <= char_code) && (char_code <= 0x39)) || ((0x41 <= char_code) && (char_code <= 0x5a)) || ((0x61 <= char_code) && (char_code <= 0x7a))) {
				// [0-9a-z-A-Z]
				// no escape
				encoded_str += str.charAt(i);
			}
			else if ((char_code == 0x2a) || (char_code == 0x2e) || (char_code == 0x2d) || (char_code == 0x5f)) {
				// [.-_]
				// no escape
				encoded_str += str.charAt(i);
			}
			else {
				// else 2
				// for internal unicode to UTF-8
				// Ref. http://homepage3.nifty.com/aokura/jscript/utf8.html
				// Ref. http://homepage1.nifty.com/nomenclator/unicode/ucs_utf.htm
				if (char_code > 0xffff) {
					encoded_str += "%" + ((char_code >> 18) | 0xf0).toString(16).toUpperCase();
					encoded_str += "%" + (((char_code >> 12) & 0x3f) | 0x80).toString(16).toUpperCase();
					encoded_str += "%" + (((char_code >> 6) & 0x3f) | 0x80).toString(16).toUpperCase();
					encoded_str += "%" + ((char_code & 0x3f) | 0x80).toString(16).toUpperCase();
				}
				else if (char_code > 0x7ff) {
					encoded_str += "%" + ((char_code >> 12) | 0xe0).toString(16).toUpperCase();
					encoded_str += "%" + (((char_code >> 6) & 0x3f) | 0x80).toString(16).toUpperCase();
					encoded_str += "%" + ((char_code & 0x3f) | 0x80).toString(16).toUpperCase();
				}
				else if (char_code > 0x7f) {
					encoded_str += "%" + (((char_code >> 6) & 0x1f) | 0xc0).toString(16).toUpperCase();
					encoded_str += "%" + ((char_code & 0x3f) | 0x80).toString(16).toUpperCase();
				}
				else {
					// for ascii
					padded_str = "0" + char_code.toString(16).toUpperCase();
					encoded_str += "%" + padded_str.substr(padded_str.length - 2, 2);
				}
			}
			// else 2
		}
		// else 1
	}
	// for
	return encoded_str;
}

function getActiveStyle( element, property, pseudo ) {
	if( element.currentStyle ) {          //IE
		property = ( property.match( /-/ ) ) ? camelize(property) : property;
		return element.currentStyle[ camelize(property) ];
	}
	else if( document.defaultView.getComputedStyle ) {    //Mozilla
		property = ( property.match( /-/ ) == null ) ? deCamelize(property) : property;
		return document.defaultView.getComputedStyle( element, pseudo ).getPropertyValue( property );
	}
	return "";
}

function camelize(str) {
	return str.replace( /-([a-z])/g,function( $0, $1 ) { return $1.toUpperCase( ) } );
}
function deCamelize(str) {
	return str.replace( /[A-Z]/g, function( $0 ) { return "-" + $0.toLowerCase( ) } );
}


function isUndefined(varName){
    if(eval("typeof " + varName) == "undefined")
        return true;
    
    var variable = eval( varName );
    if( variable == null )
        return true;
    
    if( typeof variable == "string" )
    	return ( variable.replace(/\s/,"").length == 0 )
    
    return false
}

function PullDown(opt){
	var self = this;
	var map = opt.map;
	var evtObserv = (typeof IS_Event != "undefined") && IS_Event.observe ?
		IS_Event.observe.bind(IS_Event) : Event.observe.bind(Event);
	var callback = opt.onChange;
	opt.width = (opt.width) ? opt.width : '100%';
	
	this.build = function( container ){
		var pulldown = document.createElement("div");
		pulldown.className = "pulldown";
		container.appendChild(pulldown);
		this.elm_pulldown = pulldown;
		
		this.buildPulldown( pulldown );
		var list = document.createElement("ul");
		if( Browser.isIE )
			list.style.width = opt.width;
		
		Element.addClassName( list,"pulldown_list");
		document.body.appendChild( list );
		this.buildPulldownList( list );
		this.elm_list = list;
		evtObserv(pulldown, "mouseover",this.showChildUl.bind( this ),false, opt.eventId);
		evtObserv(pulldown, "mouseout", this.hideChildUl.bind( this ), false, opt.eventId);
		evtObserv(pulldown, "click",this.showHideChildUl.bind( this ),true, opt.eventId);
		evtObserv(list, "mouseout",this.hideChildUl.bind( this ),false, opt.eventId);
		evtObserv(list, "mouseover",this.showChildUl.bind( this ),false, opt.eventId);
		
		if( opt.selected !== undefined ) {
			this.selectedKey = opt.selected;
			this.setField( opt.selected );
		}
		
	}
	
	this.buildPulldown = function( pulldown ) {
		pulldown.style.width = opt.width;
		
		var li = document.createElement("li");
		if( Browser.isIE )
			li.style.width = opt.width;
		
		pulldown.appendChild( li );
		
		var table = document.createElement("table");
		table.border = 0;
		table.cellSpacing = 0;
		table.cellPading = 0;
		//table.style.tableLayout = "fixed";
		table.style.width = "100%";
		
		var tbody = document.createElement("tbody");
		table.appendChild(tbody);
		li.appendChild(table);
		
		var tr = document.createElement("tr");
		tbody.appendChild(tr);
		
		var labelTd = document.createElement("td");
		tr.appendChild(labelTd);
		
		var field = document.createElement("div");
		labelTd.appendChild(field);
		field.style.overflow = "hidden";
		this.elm_field = field;
		
		var icon = document.createElement("td");
		icon.className = "pulldown_icon";
		tr.appendChild(icon);
		this.elm_icon = icon;
	}
	
	if( Browser.isSafari1 ) {
		this.buildPulldown = ( function( method ) {
			return function( pulldown ) {
				method.apply( this,[pulldown] );
				
				this.elm_field.style.height = "1.4em"
			}
		})( this.buildPulldown );
	}
	
	this.rebuildPulldownList = function() {
		while( this.elm_list.firstChild )
			this.elm_list.removeChild( this.elm_list.firstChild );
		
		this.buildPulldownList( this.elm_list );
		this.setField( this.selectedKey );
	}
	this.buildPulldownList = function( list ) {
		var this_ = this;
		$H( map ).each( function( entry ) {
			var listItem = document.createElement("li");
			listItem.style.width = "100%";
			listItem.style.display = "block";
			list.appendChild( listItem );
			
			evtObserv( listItem, "mouseover", function(){
					listItem.className = "hover";
				},false, opt.eventId);
			evtObserv( listItem, "mouseout", function(){
					listItem.className = "";
				},false, opt.eventId);
			
			var itemLabel = document.createElement("div");
			this_.renderItem( entry.key,itemLabel );
			listItem.appendChild( itemLabel );
			
			evtObserv( listItem, "click",
				this_.setSelectedKey.bind( this_,entry.key ),false, opt.eventId);
			
			if( Browser.isSafari1 ) {
				evtObserv( listItem,"click",function() { listItem.className = ""; },false,opt.eventId );
			}
		});
	}
	this.setSelectedKey = function( key ) {
		if( this.selectedKey !== undefined && this.selectedKey == key ) {
			Element.removeClassName( this.elm_list,"pulldown_list_popup");
			return;
		}
		
		var oldSelectedKey = this.selectedKey;
		this.selectedKey = key;
		var value = map[key];
		
		var cancelSelection = false;
		if( callback )
			cancelSelection = callback( key,value );
		
		if( !cancelSelection ) {
			Element.removeClassName( this.elm_list,"pulldown_list_popup");
			
			this.setField( key );
		}
	}
	this.setField = function( key ) {
		this.elm_field.innerHTML = "";
		this.renderItem( key,this.elm_field );
	}
	this.renderItem = function( key,div ) {
		var value = map[key];
		if( !value ) return;
		
		if( typeof value == "string") {
			div.innerHTML = map[key];
		} else if( value instanceof Function ) {
			value( this,key,div );
		} else if( value.nodeType ) {
			div.appendChild( value );
		} else if( value.renderItem && value.renderItem instanceof Function ) {
			value.renderItem( this,key,div );
		}
	}
	
	this.hideChildUl = function() {
		if( !this.hideTimeout ) {
			var this_ = this;
			this.hideTimeout = setTimeout(function(){
				this_.elm_list.className = "pulldown_list";
			}, 500 );
		}
	}
	this.showChildUl = function() {
		this.hideTimeout = clearTimeout( this.hideTimeout )
	}
	this.showHideChildUl = function () {
		this.hideTimeout = clearTimeout( this.hideTimeout );
		
		if( this.elm_list.className.indexOf("pulldown_list_popup") < 0 ) {
			Element.addClassName( this.elm_list,"pulldown_list_popup");
		} else {
			this.elm_list.className = "pulldown_list";
		}
		
		this.adjustItemList();
	}
	this.adjustItemList = function() {
		this.elm_list.style.left = findPosX( this.elm_pulldown ) + 'px';
		this.elm_list.style.top = findPosY( this.elm_pulldown ) +this.elm_pulldown.offsetHeight + 'px';
		this.elm_list.style.width = this.elm_pulldown.offsetWidth + 'px';
		
		var posY = findPosY( this.elm_list ) -
			( document.documentElement.scrollTop || document.body.scrollTop );
		var height = this.elm_list.offsetHeight;
		var viewportHeight = getWindowSize( false );
		if( ( posY +height ) > viewportHeight ) {
			this.elm_list.style.height = viewportHeight -posY -4 + 'px';
			this.elm_list.style.overflowY = "auto";
			this.elm_list.style.overflowX = "hidden";
		} else {
			this.elm_list.style.height = "auto";
			this.elm_list.style.overflow = "hidden";
		}
	}
	this.destruct = function() {
		if( this.elm_list && this.elm_list.parentNode ) {
			this.elm_list.parentNode.removeChild( this.elm_list );
		} else if( self.elm_list.parentNode ) {
			self.elm_list.parentNode.removeChild( self.elm_list );
		}
		
		IS_Event.unloadCache( opt.eventId );
	}
}

/**
 * Return defaultParam if the value of param object is undefined
 * 
 * @param {Object} param
 */
function is_getPropertyBoolean(param, defaultParam){
	if(typeof param == "undefined"){
		return defaultParam;
	}else{
		return getBooleanValue(param);
	}
}

/**
 * Return defaultParamu,if the result of checking the value is false
 * 
 * @param {Object} param
 * @param {Object} defaultParam
 */
function is_getPropertyInt(param, defaultParam){
	tempParam = (param + "");
	if(typeof tempParam == "undefined" || !tempParam.match(/^[.0-9]+$/)){
		return defaultParam;
	}else{
		return param;
	}
}

/**
 * Return defaultParam if the value of param object is undefined
 * 
 * @param {Object} param
 */
function is_getPropertyString(param, defaultParam){
	if(typeof param == "undefined"){
		return defaultParam;
	}else{
		return param;
	}
}

/**
 * if eval fails, return defaultParam
 * 
 * @param {Object} param
 */
function is_getPropertyEval(param, defaultParam){
	try{
		return eval(param);
	}catch(e){
		return defaultParam;
	}
}

/**
 * Process the result of Detect by using callback function 
 * @param {Object} url
 * @param {Object} func
 */
function is_processUrlContents(inputURL, func, _finallyFunc, _headers, _method){
	var url;
	if(/^upload_/.test(inputURL)){
		url = is_getProxyUrl(hostPrefix + "/gadgetsrv/" + inputURL, "Detect");
	}else{
		url = is_getProxyUrl(inputURL, "Detect");
	}
	var headers = ["MSDPortal-Cache", "No-Cache"];
	if(_headers)headers = _headers.concat(headers);
	var opt = {
		method: _method ? _method : 'get' ,
		asynchronous:true,
		requestHeaders: headers,
		onSuccess: func,
	  on404: function(t) {
			
		  var errorMsg = IS_R.getResource(IS_R.ms_urlGetInfoException1,[t.status,t.statusText]);
			msg.error(errorMsg);
			func({responseText:"[{type:'MiniBrowser', url:'"+ inputURL +"', href:'"+ inputURL +"',isError:true,errorMsg:'" + errorMsg + "'}]"});
		},
		on10408: function(r,t) {
			
			alert(IS_R.getResource(IS_R.ms_urlGetInfoException1,[t.status,t.statusText]));
		},
		onFailure: function(t) {
			
			var errorMsg = IS_R.getResource(IS_R.ms_urlGetInfoException1,[t.status,t.statusText]);
			msg.error(errorMsg);
			func({responseText:"[{type:'MiniBrowser', url:'"+ inputURL +"', href:'"+ inputURL +"',isError:true,errorMsg:'" + errorMsg + "'}]"});
		},
		onException: function(r, t){
			
			var errorMsg = IS_R.getResource(IS_R.ms_urlGetInfoException1,[getErrorMessage(t)]);
			msg.error(errorMsg);
			func({responseText:"[{type:'MiniBrowser', url:'"+ inputURL +"', href:'"+ inputURL +"',isError:true,errorMsg:'" + errorMsg + "'}]"});
		},
		onComplete: function(){
			if(_finallyFunc)
			  _finallyFunc.call(this);
		}
	};
	AjaxRequest.invoke(url, opt);
}

function is_getURLByIFrame(inputUrl, _callback, _eventId, parameters){
	var proxyKeywordUrl = is_getProxyUrl(inputUrl, "NoOperation");
	
	for(var i = 0;parameters && i < parameters.length;i += 2){
		proxyKeywordUrl += "&" + parameters[i] + "=" + parameters[i + 1];
	}

	if(IS_Widget.MiniBrowser.isForbiddenURL(inputUrl)){
		proxyKeywordUrl = "about:blank";
	}
		
	var getTitleIFrame = $('getTitleInnerFrame');
	if(!getTitleIFrame){
		getTitleIFrame = document.createElement('iframe');
		getTitleIFrame.name = 'getTitleInnerFrame';
		getTitleIFrame.id = 'getTitleInnerFrame';
		getTitleIFrame.src = "./blank.html";
		if( Browser.isSafari1 ) {
			getTitleIFrame.style.width = getTitleIFrame.style.height = 0;
			getTitleIFrame.style.visibility = "hidden";
		} else {
			getTitleIFrame.style.display = 'none';
		}
		
		document.body.appendChild(getTitleIFrame);
		if(!_eventId)
		  IS_Event.observe(getTitleIFrame, "load", _callback, false, _eventId);
	}
	if(_eventId)
	  IS_Event.observe(getTitleIFrame, "load", _callback, false, _eventId);
	var oldUrl = getTitleIFrame.src;
	if( ( oldUrl && oldUrl != "")&& proxyKeywordUrl == oldUrl ) {
		getTitleIFrame.contentWindow.location.reload();
	} else {
		getTitleIFrame.src = proxyKeywordUrl;
	}
}

/**
 * Get lengh of string
 * Count lengh of double byte character set as two
 * @param {Object} str
 */
function is_jlength(str) {
   var len = 0;
   str = escape(str);
   for (var i = 0; i < str.length; i++, len++) {
      if (str.charAt(i) == "%") {
         if (str.charAt(++i) == "u") {
            i += 3;
            len++;
         }
         i++;
      }
   }
   return len;
}

/**
 * Set CSS
 */
var is_addCssRule = ( Browser.isIE )
    ? (function(sheet){
          return function(selector, declaration){
              sheet.addRule(selector, declaration);
          };
      })(document.createStyleSheet())
    : (function(sheet){
          return function(selector, declaration){
              sheet.insertRule(selector + '{' + declaration + '}', sheet.cssRules.length);
          };
      })((function(e){
            e.appendChild(document.createTextNode(''));
            (document.getElementsByTagName('head')[0] || (function(h){
                document.documentElement.insertBefore(h, this.firstChild);
                return h;
            })(document.createElement('head'))).appendChild(e);
            return e.sheet;
        })(document.createElement('style')))
    ;

if (Browser.isSafari1) {
	is_addCssRule = function(){}
}

/**
 * Replace UserPref[@datatype='list'] with Array
 * @param {String} arrayStr
 */
function is_toUserPrefArray(arrayStr){
	var arrayData = ( arrayStr.length > 0 ? arrayStr.split('|') : [] );
	for( var i=0;i<arrayData.length;i++ )
		arrayData[i] = arrayData[i].replace(/%7C/ig, "|");
	return arrayData;
}

if( Browser.isSafari1 ) {
	( function() {
		var _setFullYear = Date.prototype.setFullYear;
		
		Date.prototype.setFullYear = function ( year,month,date) {
			if( month < 0 ) {
				year--;
				month += 12;
			}
			
			return _setFullYear.apply( this,[year,month,
				( arguments.length >= 2 ) ? date : this.getDate()
			]);
		};
	})();
	
	
	var _Date = Date;
	Date = ( function() {
		//Obfuscation prevents eval
		//var _Date = Date;
		
		return function() {
			var args = $A( arguments );
			if( args.length == 0 ) {
				return new _Date();
			} else if( args.length != 1 ) {
				if( args.length >= 2 ) {
					if( args[1] < 0 ) {
						args[0] = args[0] -1;
						args[1] = args[1] +12;
					}
				}
				
				return eval("new _Date("+args.join(",")+")");
			}
			
			if( args[0].getTime ) {
				return new _Date( args[0].getTime() );
			} else if( typeof args[0] == "string" && /\d+\/\d+\/\d+(?:\s\d+:\d+:\d+)?/.test( args[0] )) {
				var matches;
				
				var dateTimeString = args[0].split(" ");
				if( dateTimeString.length >= 1 )
					matches = dateTimeString[0].split("/");
				
				if( dateTimeString.length == 2 ) {
					matches = matches.concat( dateTimeString[1].split(":"));
					
					return new _Date( matches[0],matches[1] -1,matches[2],
						matches[3],matches[4],matches[5] );
				} else {
					return new _Date( matches[0],matches[1] -1,matches[2]);
				}
			}
			
			var date = new _Date();
			if( !( /\d+/.test( args[0]+"" )))
				args[0] = _Date.parse( args[0] );
			
			date.setTime( parseInt( args[0] ));
			
			return date;
		}
	})();
	
	//for( var i in _Date )
	//	Date[i] = _Date[i];
}

function isHidePanel(){
	if(
		$("maximize-panel").style.display == "none" &&
		$("portal-iframe").style.display == "none" &&
		$("search-iframe").style.display == "none"
		)return false;
	else
	  return true;
}

/*
 * alternative dojo
 */
is_createDocumentFromText = function(str, mimetype){
	if(!mimetype) { mimetype = "text/xml"; }
	if(typeof DOMParser != "undefined") {
		var parser = new DOMParser();
		return parser.parseFromString(str, mimetype);
	}else if(typeof ActiveXObject != "undefined"){
		var domDoc = new ActiveXObject("Microsoft.XMLDOM");
		if(domDoc) {
			domDoc.async = false;
			domDoc.loadXML(str);
			return domDoc;
		}else{
			msg.debug("toXml didn't work?");
		}
	/*
	}else if((dojo.render.html.capable)&&(dojo.render.html.safari)){
		// FIXME: this doesn't appear to work!
		// from: http://web-graphics.com/mtarchive/001606.php
		// var xml = '<?xml version="1.0"?>'+str;
		var mtype = "text/xml";
		var xml = '<?xml version="1.0"?>'+str;
		var url = "data:"+mtype+";charset=utf-8,"+encodeURIComponent(xml);
		var req = new XMLHttpRequest();
		req.open("GET", url, false);
		req.overrideMimeType(mtype);
		req.send(null);
		return req.responseXML;
	*/
	}else if(document.createElement){
		// FIXME: this may change all tags to uppercase!
		var tmp = document.createElement("xml");
		tmp.innerHTML = str;
		if(document.implementation && document.implementation.createDocument) {
			var xmlDoc = document.implementation.createDocument("foo", "", null);
			for(var i = 0; i < tmp.childNodes.length; i++) {
				xmlDoc.importNode(tmp.childNodes.item(i), true);
			}
			return xmlDoc;
		}
		// FIXME: probably not a good idea to have to return an HTML fragment
		// FIXME: the tmp.doc.firstChild is as tested from IE, so it may not
		// work that way across the board
		return tmp.document && tmp.document.firstChild ?
			tmp.document.firstChild : tmp;
	}
	return null;
}
is_insertBefore = function(node, ref, force){
	if (force != true &&
		(node === ref || node.nextSibling === ref)){ return false; }
	var parent = ref.parentNode;
	parent.insertBefore(node, ref);
	return true;
}
is_insertAfter = function(node, ref, force){
	var pn = ref.parentNode;
	if(ref == pn.lastChild){
		if((force != true)&&(node === ref)){
			return false;
		}
		pn.appendChild(node);
	}else{
		return is_insertBefore(node, ref.nextSibling, force);
	}
	return true;
}
is_innerXML = function(node){
	if(node.innerXML){
		return node.innerXML;
	}else if(node.xml){
		return node.xml;
	}else if(typeof XMLSerializer != "undefined"){
		return (new XMLSerializer()).serializeToString(node);
	}
}
is_createElementNS = function(doc, tagName, namespaceUri){
	if(typeof doc.createElementNS != 'undefined')
		return doc.createElementNS(namespaceUri, tagName);
	else
		return doc.createNode(1, tagName, namespaceUri);
}

// This function is used when MiniBrowser or FragmentMiniBrowser is moved
is_swapIFrameSRC = function( iframe ) {
	if( !iframe ) return;
	
	iframe.contentWindow.location.reload();
	/*
	var temp = iframe.src;
	iframe.src = "about:blank";
	
	setTimeout( function() {
		iframe.src = temp;
	},200 );
	*/
}
// This function is used When Gadget is moved
is_ieSwapIFrame = function( window ) {
	var doc = window.document;
	var selectTags = doc.getElementsByTagName("select");
	for( var i=0;i<selectTags.length;i++ ) {
		var selectTag = selectTags[i];
		var parentNode = selectTag.parentNode;
		var temp = doc.createElement("div");
		
		// z-order is updated if the node is removed and inserted
		parentNode.replaceChild( temp,selectTag );
		parentNode.replaceChild( selectTag,temp );
	}
}

is_getTruncatedString = function( str,length ) {
	var result = str;
	AjaxRequest.invoke( hostPrefix+"/strsrv", {
		method: 'post' ,
		asynchronous:false,
		parameters: "text=" + encodeURIComponent(str) + "&length=" + length,
		onSuccess: function(response){
			result = response.responseText;
		},
		onFailure: function(t) {
			msg.error(IS_R.ms_failedCheckCharLength + t.status + " - " + t.statusText);
		},
		onException: function(r, t){
			msg.error(IS_R.ms_failedCheckCharLength + getErrorMessage(t));
		}
	});
	
	return result;
}

is_loadScript = function( src ) {
	var head = document.getElementsByTagName("head")[0];
	var script = document.createElement("script");
	script.type = "text/javascript";
	script.src = src;
	
	head.appendChild( script );
}

is_deleteProperty = function(object, propertyName){
	if(!Object.isUndefined(object[propertyName]))
		delete object[propertyName];
}

if( !Object.hasOwnProperty ) {
	Object.prototype.hasOwnProperty = function( property ) {
		return ( this[property] && !this.constructor.prototype[property] );
	}
}
