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

var AjaxRequest = {
	__pool:{isLock:false, objects:[], currentInUse:0}, 
	__syncAjaxRpc:null, 
	__options:{poolSize:( window["maxConnectionsPerServer"] ) ? window["maxConnectionsPerServer"] : 6, retryInterval: 1000, timeout:-1, retryCount: 0},
	__cancels:{},
	__queue:{},
	
	invoke:function (url, options, id) {
		if(typeof IS_Preview != "undefined" && IS_Preview.rewriteUrl){
			url = IS_Preview.rewriteUrl(url);
			if(!url) return;
		}
		if(id && this.__cancels[id]) {
			this.__cancels[id] = null;
			if(this.__queue[id]) {
				this.__queue[id] = null;
				options.onComplete();
				msg.debug("cancel : " + id);
				return;
			}
		}
		var self = this;
		
		var headers = [
			"MSDPortal-Timeout", ajaxRequestTimeout,
			"MSDPortal-Ajax", "true"
		];
		if( window.is_sessionId ){
			headers.push("MSDPortal-SessionId");
			headers.push( is_sessionId );
		}
		if(options.requestHeaders){
			for(var i=0;i<headers.length;i++){
				options.requestHeaders.push(headers[i]);
			}
		}else{
			options.requestHeaders = headers;
		}
		
		doAsyncInvoke(url, options, id);
		
		function doAsyncInvoke(url, options, id) {
			try{
			var request = getRequest( options.asynchronous );
			
			if (request == null) {
				setTimeout(function() {AjaxRequest.invoke(url, options, id)}, self.__options.retryInterval);
				if(id) self.__queue[id] = true;
			}else {
				request.invoke(url, options, id);
			}
			}catch(e){
				alert(e);
				throw e;
			}
		}
		
		function getRequest( asynchronous ) {
			if (!self.__pool.isLock) {
				self.__pool.isLock = true;
			} else {
				return null;
			}
			var request = null;
			for (var i = 0; i < self.__pool.objects.length; i++) {

				if (!self.__pool.objects[i].buzy && self.__pool.objects[i].asynchronous == asynchronous ) {
					request = self.__pool.objects[i];
					request.buzy = true;
					break;
				}
			}
			if (request == null && self.__pool.objects.length < self.__options.poolSize || !asynchronous ) {
				request = new AjaxRpc();
				request.asynchronous = asynchronous;
				request.buzy = true;
				self.__pool.objects.push(request);
			}
			self.__pool.isLock = false;
			return request;
		}
	}, 
	setOptions:function (options) {
		Object.extend(this.__options, options || {});
	},
	cancel:function (id) {
		for (var i = 0; i < this.__pool.objects.length; i++) {
			var rpc = this.__pool.objects[i];
			if(rpc.id && rpc.id == id) {
				rpc.abort();
				this.__cancels[id] = null;
				msg.debug("abort : " + id);
				return;
			}
		}
		if(this.__queue[id]) {
			this.__cancels[id] = true;
		}
	}
};

AjaxRpc = function () {};
AjaxRpc.prototype = {

	invoke:function (url, options, id) {
		this.__options = {};
		Object.extend(this.__options, options || {});
		var self = this;
		this.__isAbort = false;
		this.__options.onComplete = handleComplete;
		this.__options.onSuccess = handleSuccess;
		this.__options.on500 = handle500;
		
		this.id = id;
		var retryCount = typeof options.retryCount != "undefined" ? options.retryCount : AjaxRequest.__options.retryCount;
		var timeout = options.timeout || AjaxRequest.__options.timeout;
		if(timeout > 0)
			this.timer = setTimeout(handleTimeout, timeout);
		if(self.__options.onRequest) self.__options.onRequest();
		fireAjaxRequest();
		
		function handleComplete(ajaxRequest, exception) {
			if(self.timer) clearTimeout(self.timer);
			try {
				if( !(self.__isAbort && ( !options.errorCount || options.errorCount <= retryCount)) && options.onComplete) {
					options.onComplete(ajaxRequest, exception);
				}
			}catch (e){
			}
			if(id) AjaxRequest.__queue[id] = null;
			self.buzy = false;
		}
		
		function handleSuccess(ajaxRequest, exception) {
			if(self.__isAbort) {
				return;
			}
			if(options.onSuccess)
				options.onSuccess(ajaxRequest, exception);
		}
		
		function handle500(ajaxRequest, exception){
			var status = ajaxRequest.getResponseHeader("MSDPortal-Status");
			switch(status) {
				case "10408": // infoscoop proxy timeout status code
					handleTimeout(ajaxRequest, exception);
					return;
				case "10997": // infoscoop auto reload status code
				case "10998": // infoscoop auto logoff status code
				case "10999": // infoscoop auto logoff status code
					handleLogoff(status, exception);
					return;
				default:
					break;
			}
			if(options.on500)
				options.on500(ajaxRequest, exception);
			else if(options.onFailure)
				options.onFailure(ajaxRequest, exception);
		}
		
		function handleTimeout() {
			var state = self.__ajaxRequest.transport.readyState;
			self.__isAbort = true;
			try{
				self.__ajaxRequest.transport.abort();
			}catch(e){}
			options.errorCount = options.errorCount ? options.errorCount + 1 : 1;
			msg.debug("timeout(" + options.errorCount + ") : " + url);
			if(options.errorCount <= retryCount) {
				setTimeout(function() {AjaxRequest.invoke(url, options, id)}, AjaxRequest.__options.retryInterval);
			} else if( self.__options.on10408 ) {

				options.on10408( self.__ajaxRequest.transport,IS_R.ms_timeOut )
			} else if(self.__options.onException) {

				self.__options.onException(self.__ajaxRequest.transport, IS_R.ms_timeOut);
			}
			handleComplete(self.__ajaxRequest.transport);
		}
		
		function handleLogoff(status, exception){
			if( window["ISA_Admin"] ) {
				alert( IS_R.ms_sessionTimeoutReLogin);
				return;
			}
			
			if(AjaxRequest.isAutoLogoff) return;
			AjaxRequest.isAutoLogoff = true;
			
			if( status == "10997" ) {
				alert( IS_R.ms_clearConfigurationForceReload );
				window.location.reload( true );
			}
			
		
			var msg = IS_R["ms_on" + status];
			if(!msg) msg = IS_R.ms_sessionTimeout;
			/*
			alert(msg);
			if(typeof autoLogoffUrl == "undefined")
				autoLogoffUrl = "logoff.html";
			location.href = autoLogoffUrl;
			*/
			var errorMsgBar = $('error-msg-bar');
			errorMsgBar.innerHTML = '';
			var errorMsgSpan = document.createElement('span');
			var errorImg = document.createElement('img');
			errorImg.style.position ="relative";
			errorImg.style.top = "2px";
			errorImg.style.paddingRight = "2px";
			errorImg.src = imageURL+"error12.gif";
			errorMsgSpan.appendChild(errorImg);
			errorMsgSpan.appendChild(document.createTextNode(msg));
			errorMsgBar.appendChild(errorMsgSpan);
			errorMsgBar.style.display = "";
		}
				
		function fireAjaxRequest() {
			try{
				if (self.__ajaxRequest) {
					self.__ajaxRequest._complete = false;
					self.__ajaxRequest.setOptions(self.__options)
					self.__ajaxRequest.request(url);
				} else {
					self.__ajaxRequest = new Ajax.Request(url, self.__options);
				}
				/*
				if ( !self.__options.asynchronous ) {
					self.__ajaxRequest.respondToReadyState(4);
				}
				*/
			}catch(e){
				if(self.__options.onException)
					self.__options.onException(null, e);
				handleComplete(null, e);
			}
		}
	},
	
	abort:function() {
		this.__isAbort = true;
		try{
			this.__ajaxRequest.transport.abort();
		}catch(e){}
		this.__options.onComplete(this.__ajaxRequest.transport);
	}
};
