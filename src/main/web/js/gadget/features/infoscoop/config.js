/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

function getContextPath() {
	var host = location.protocol + "//" + location.hostname;
	if(location.port && location.port != '')
		host += ":" + location.port;
	var path = location.pathname;
	if ( path.charAt(0) == '/' ) {
		path = path.substr(1);
	}
	if((p = path.lastIndexOf('/')) != -1){
		return host + "/" + path.substring(0,p);
	}
	return host + '/' + path;
}

( function(){
var params = gadgets.util.getUrlParameters();

if( !window.hostPrefix ) { 
	window.hostPrefix = params.__HOST_PREFIX__;
	window.widgetId = params.__MODULE_ID__;
	window.tabId = params.__TAB_ID__;
	window.features = {};
	window.i18nMsgs = {};
}

var config = {"gadgets.container" : ["default"],

// Set of regular expressions to validate the parent parameter. This is
// necessary to support situations where you want a single container to support
// multiple possible host names (such as for localized domains, such as
// <language>.example.org. If left as null, the parent parameter will be
// ignored; otherwise, any requests that do not include a parent
// value matching this set will return a 404 error.
"gadgets.parent" : null,

// Should all gadgets be forced on to a locked domain?
"gadgets.lockedDomainRequired" : false,

// DNS domain on which gadgets should render.
"gadgets.lockedDomainSuffix" : "-a.example.com:8080", //FIXME

// Various urls generated throughout the code base.
// iframeBaseUri will automatically have the host inserted
// if locked domain is enabled and the implementation supports it.
// query parameters will be added.
"gadgets.iframeBaseUri" : "/iframe.jsp", //FIXME

// jsUriTemplate will have %host% and %js% substituted.
// No locked domain special cases, but jsUriTemplate must
// never conflict with a lockedDomainSuffix.
"gadgets.jsUriTemplate" : "http://%host%/js/gadgets/%js%", //FIXME

// Use an insecure security token by default
"gadgets.securityTokenType" : "insecure",

// Config param to load Opensocial data for social
// preloads in data pipelining.  %host% will be
// substituted with the current host.
"gadgets.osDataUri" : "http://%host%/social/rpc", //FIXME

// Uncomment these to switch to a secure version
// 
//"gadgets.securityTokenType" : "secure",
//"gadgets.securityTokenKeyFile" : "/path/to/key/file.txt",

// This config data will be passed down to javascript. Please
// configure your object using the feature name rather than
// the javascript name.

// Only configuration for required features will be used.
// See individual feature.xml files for configuration details.
//"gadgets.features" : {
  "core.io" : {
    // Note: /proxy is an open proxy. Be careful how you expose this!
    "proxyUrl" : getContextPath()+"/proxy?filter=NoOperation&url=%url%",
    "jsonProxyUrl" : getContextPath()+"/jsonproxy"
  },
  "views" : {
    "home" : {
      "isOnlyVisible" : false,
      "urlTemplate" : "", //FIXME
      "aliases": ["default"]
    },
    "profile" : {
      "isOnlyVisible" : false,
      "urlTemplate" : "", //FIXME
      "aliases": ["DASHBOARD"]
    },
    "canvas" : {
      "isOnlyVisible" : true,
      "urlTemplate" : "", //FIXME
      "aliases" : ["FULL_PAGE"]
    }
  },
  "rpc" : {
    // Path to the relay file. Automatically appended to the parent
    /// parameter if it passes input validation and is not null.
    // This should never be on the same host in a production environment!
    // Only use this for TESTING!
    "parentRelayUrl" : hostPrefix+"/rpc_relay.html",

    // If true, this will use the legacy ifpc wire format when making rpc
    // requests.
    "useLegacyProtocol" : false
  },
  // Skin defaults
  "skins" : {
    "properties" : {
      "BG_COLOR": "white",
      "BG_IMAGE": "",
      "BG_POSITION": "",
      "BG_REPEAT": "",
      "FONT_COLOR": "black",
      "ANCHOR_COLOR": "blue"
    }
  }
//}
};

gadgets.Prefs.setMessages_( i18nMsgs );

var supportedFeatures = [
	/*"core",*/"drag","dynamic-height",
	"flash","minimessage","pubsub",
	/*"rpc"*/,"setprefs",/*"settitle",*/
	"skins","tabs","views"/*,"infoscoop"*/,"analytics"
];
var featureConf = {};
for( var i=0;i<supportedFeatures.length;i++ ) {
	var feature = supportedFeatures[i];
	
	featureConf[ feature ] = features[ feature ] || {};
}
config["core.util"] = featureConf;

gadgets.config.init( config );

gadgets.rpc.register("set_pref",function( key,value ) {
	gadgets.Prefs.setInternal_( key,value );
});

gadgets.rpc.register("ieSwapIFrame",function(){
	if( window.is_ieSwapIFrame ) is_ieSwapIFrame( window );
});
gadgets.rpc.register("adjustHeight",function( swap ) {
	if( gadgets.window && gadgets.window.adjustHeight )
		gadgets.window.adjustHeight();
});

gadgets.io.AuthorizationType.IS_SEND_PORTAL_UID_HEADER = "SEND_PORTAL_UID_HEADER";
gadgets.io.AuthorizationType.IS_POST_PORTAL_UID = "POST_PORTAL_UID";
gadgets.io.RequestParameters.IS_AUTH_UID_PARAM_NAME = "IS_AUTH_UID_PARAM_NAME";

gadgets.rpc.register("tabChanged",function( tid ) {
	tabId = tid;
});

try {
	//After Dragging in Firefox, frames may go away
	parent.frames[ window.name ] = window;
} catch( ex ) {
}

var readyContent = false;
var readyLoginUid = false;
var firedOnload = false;
function runOnLoadHandlers() {
	if( !readyContent || !readyLoginUid || firedOnload )
		return;
	
	gadgets.util.runOnLoadHandlers();
	
	firedOnload = true;
	if( window.removeEventListener ) {
		window.removeEventListener("load",handleContentOnLoad,false );
	} else if( window.detachEvent ){
		window.detachEvent("onload",handleContentOnLoad );
	}
}

function handleContentOnLoad() {
	readyContent = true;
	
	function handleGetLoginUid( uid ) {
		window.is_userId = uid;
		
		readyLoginUid = true;
		
		runOnLoadHandlers();
	}
	
	try {
		if( !window.is_userId ) window.is_userId = top.is_userId;
	} catch( ex ) { }
	
	if( !window.is_userId ) {
		setTimeout( function() {
			gadgets.rpc.call( null,"is_get_login_uid",handleGetLoginUid );
		},100 );
	} else {
		handleGetLoginUid( window.is_userId );
	}
	
	runOnLoadHandlers();
}

if( window.addEventListener ) {
	window.addEventListener("load",handleContentOnLoad,false );
} else if( window.attachEvent ){
	window.attachEvent("onload",handleContentOnLoad );
}

})();

var msg = ( function(){
	var log = gadgets;
	
	return {
		debug: function( message ) {
			log.log.logAtLevel( log.NONE,message );
		},
		info: log.log,
		warn: log.warn,
		error: log.error
	};
})();
