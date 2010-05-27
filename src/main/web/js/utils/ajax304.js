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

//Override Ajax.Reuqest
//Ajax.Request can treat 304 correctly.
//   required : prototype.js 1.4.0
if(typeof Ajax != "undefined" && typeof Ajax.Request != "undefined") {
	Ajax.Request.lastModified = [];
	Ajax.Request.etag = {};
	Ajax.Request.cache = [];
	Ajax.Request.prototype.initialize = function(url, options) {
		this.transport = Ajax.getTransport();
		this.setOptions(options, url);
		this.request(url);
	};
	Ajax.Request.prototype.setOptions = function(options, url) {
	    this.options = {
	      method:       'post',
	      asynchronous: true,
	      contentType:  'application/x-www-form-urlencoded',
	      encoding:     'UTF-8',
	      parameters:   ''
	    }
	    Object.extend(this.options, options || {});
	
	    this.options.method = this.options.method.toLowerCase();
	    if (typeof this.options.parameters == 'string')
	      this.options.parameters = this.options.parameters.toQueryParams();
		var key = options.key ? options.key : url;
		if(options.clearCache) {
			Ajax.Request.lastModified[key] = null;
			Ajax.Request.etag[key] = null;
			Ajax.Request.cache[url] = null;
		}
		var lastModified = options.ifModified && Ajax.Request.lastModified[key] ? Ajax.Request.lastModified[key] : "Thu, 01 Jun 1970 00:00:00 GMT";
		var headers = ["If-Modified-Since", lastModified];
		var etag = options.ifModified && Ajax.Request.etag[key] ? Ajax.Request.etag[key] : false;
		if(etag){
			headers.push("If-None-Match");
			headers.push(etag);
		}
		
		if(this.options.requestHeaders)
			headers.push.apply(headers, this.options.requestHeaders);
		this.options.requestHeaders = headers;
		if(options.ifModified) {
			this.options.onSuccess = function(req, obj) {
				var lastModified = req.getResponseHeader("Last-Modified");
				if(Ajax.Request.lastModified[key] && Ajax.Request.lastModified[key] == lastModified) {
					//Firefox always returns 200. check Last-Modified date
					if(options.on304)
						options.on304(req, obj);
				} else {
					options.onSuccess(req, obj);
				}
				if(lastModified)
					Ajax.Request.lastModified[key] = lastModified;
				
				var etag = req.getResponseHeader("Etag");
				if(etag) Ajax.Request.etag[key] = etag;
				
				if(options.cache) Ajax.Request.cache[url] = req;
			};
			this.options.on304 = function(req, obj) {
				if(options.cache && Ajax.Request.cache[url])
					req = Ajax.Request.cache[url];
				if(options.on304)
					options.on304(req, obj);
			};
		}
	};
}
