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
	if( path.indexOf('/') == -1 )
	  return host;
	
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

// Uncomment these to switch to a secure version
//
//"gadgets.securityTokenType" : "secure",
//"gadgets.securityTokenKeyFile" : "/path/to/key/file.txt",

// Config param to load Opensocial data for social
// preloads in data pipelining.  %host% will be
// substituted with the current host.
"gadgets.osDataUri" : "http://%host%/social/rpc", //FIXME

// OS 2.0 Gadget DOCTYPE: used in Gadgets with @specificationVersion 2.0 or greater and
// quirksmode on Gadget has not been set.
"gadgets.doctype_qname" : "HTML",  //HTML5 doctype
"gadgets.doctype_pubid" : "",
"gadgets.doctype_sysid" : "",


// Authority (host:port without scheme) for the default shindig test instance.
"defaultShindigTestAuthority":"%authority%",

// Authority (host:port without scheme) for the proxy and concat servlets.
"defaultShindigProxyConcatAuthority":"%authority%",

// Default Js Uri config: also must be overridden.
"gadgets.uri.js.host": "//${Cur['defaultShindigTestAuthority']}",
"gadgets.uri.js.path": "${CONTEXT_ROOT}/gadgets/js",

// Default concat Uri config; used for testing.
"gadgets.uri.concat.host" : "${Cur['defaultShindigProxyConcatAuthority']}",
"gadgets.uri.concat.path" : "${CONTEXT_ROOT}/gadgets/concat",
"gadgets.uri.concat.js.splitToken" : "false",

// Default proxy Uri config; used for testing.
"gadgets.uri.proxy.host" : "${Cur['defaultShindigProxyConcatAuthority']}",
"gadgets.uri.proxy.path" : "${CONTEXT_ROOT}/gadgets/proxy",

//Enables/Disables feature administration
"gadgets.admin.enableFeatureAdministration" : "false",

//Enables whitelist checks
"gadgets.admin.enableGadgetWhitelist" : "false",


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
    "default" : {
      "isOnlyVisible" : false,
      "urlTemplate" : "", //FIXME
      "aliases" : ["home", "profile", "canvas"]
    },
    "profile" : {
      "isOnlyVisible" : false,
      "urlTemplate" : "", //FIXME
      "aliases": ["DASHBOARD", "default"]
    },
    "canvas" : {
      "isOnlyVisible" : true,
      "urlTemplate" : "", //FIXME
      "aliases" : ["FULL_PAGE"]
    },
    "home" : {
      "isOnlyVisible" : false,
      "urlTemplate" : "", //FIXME
      "aliases": ["default"]
    }
  },
  "tabs": {
    "css" : [
      ".tablib_table {",
      "width: 100%;",
      "border-collapse: separate;",
      "border-spacing: 0px;",
      "empty-cells: show;",
      "font-size: 11px;",
      "text-align: center;",
    "}",
    ".tablib_emptyTab {",
      "border-bottom: 1px solid #676767;",
      "padding: 0px 1px;",
    "}",
    ".tablib_spacerTab {",
      "border-bottom: 1px solid #676767;",
      "padding: 0px 1px;",
      "width: 1px;",
    "}",
    ".tablib_selected {",
      "padding: 2px;",
      "background-color: #ffffff;",
      "border: 1px solid #676767;",
      "border-bottom-width: 0px;",
      "color: #3366cc;",
      "font-weight: bold;",
      "width: 80px;",
      "cursor: default;",
    "}",
    ".tablib_unselected {",
      "padding: 2px;",
      "background-color: #dddddd;",
      "border: 1px solid #aaaaaa;",
      "border-bottom-color: #676767;",
      "color: #000000;",
      "width: 80px;",
      "cursor: pointer;",
    "}",
    ".tablib_navContainer {",
      "width: 10px;",
      "vertical-align: middle;",
    "}",
    ".tablib_navContainer a:link, ",
    ".tablib_navContainer a:visited, ",
    ".tablib_navContainer a:hover {",
      "color: #3366aa;",
      "text-decoration: none;",
    "}"
    ]
  },
  "minimessage": {
      "css": [
        ".mmlib_table {",
        "width: 100%;",
        "font: bold 9px arial,sans-serif;",
        "background-color: #fff4c2;",
        "border-collapse: separate;",
        "border-spacing: 0px;",
        "padding: 1px 0px;",
      "}",
      ".mmlib_xlink {",
        "font: normal 1.1em arial,sans-serif;",
        "font-weight: bold;",
        "color: #0000cc;",
        "cursor: pointer;",
      "}"
     ]
  },
  "rpc" : {
    // Path to the relay file. Automatically appended to the parent
    /// parameter if it passes input validation and is not null.
    // This should never be on the same host in a production environment!
    // Only use this for TESTING!
    "parentRelayUrl" : hostPrefix+"/rpc_relay.html",

    // If true, this will use the legacy ifpc wire format when making rpc
    // requests.
    "useLegacyProtocol" : false,

    // Path to the cross-domain enabling SWF for rpc's Flash transport.
    "commSwf": "/xpc.swf",
    "passReferrer": "c2p:query"
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
  },
  "osapi.services" : {
    // Specifying a binding to "container.listMethods" instructs osapi to dynamicaly introspect the services
    // provided by the container and delay the gadget onLoad handler until that introspection is
    // complete.
    // Alternatively a container can directly configure services here rather than having them
    // introspected. Simply list out the available servies and omit "container.listMethods" to
    // avoid the initialization delay caused by gadgets.rpc
    // E.g. "gadgets.rpc" : ["activities.requestCreate", "messages.requestSend", "requestShareApp", "requestPermission"]
    "gadgets.rpc" : ["container.listMethods"]
//  "//%host%/rpc" : ["http.post", "http.delete", "http.head", "http.get", "http.put"]
  },
  "osapi" : {
    // The endpoints to query for available JSONRPC/REST services
    "endPoints" : [ "//%host%${CONTEXT_ROOT}/rpc" ]
  },
  "container" : {
    "relayPath": "${CONTEXT_ROOT}/gadgets/files/container/rpc_relay.html",

    //Enables/Disables the RPC arbitrator functionality in the common container
    "enableRpcArbitration": false
  }
//}
};

config["osapi.services"][getContextPath() + "/rpc"] = ["people.get", "http.post", "http.delete", "http.head", "http.get", "http.put"];
gadgets.Prefs.setMessages_( i18nMsgs );

var supportedFeatures = [
	/*"core",*/"drag","dynamic-height",
	"flash","minimessage","pubsub",
	/*"rpc"*/,"setprefs",/*"settitle",*/
	"skins","tabs","views"/*,"infoscoop"*/,"analytics",
	"oauthpopup"
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

gadgets.io.AuthorizationType.IS_BASIC = "BASIC";
gadgets.io.RequestParameters.IS_AUTH_BASIC_USERNAME = "IS_AUTH_BASIC_USERNAME";
gadgets.io.RequestParameters.IS_AUTH_BASIC_PASSWORD = "IS_AUTH_BASIC_PASSWORD";

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
var readySessionId = false;
var firedOnload = false;
function runOnLoadHandlers() {
	if( !readyContent || !readyLoginUid || !readySessionId || firedOnload )
		return;
	
	gadgets.util.runOnLoadHandlers();
	
	firedOnload = true;
	if( window.removeEventListener ) {
		window.removeEventListener("load",handleContentOnLoad,false );
	} else if( window.detachEvent ){
		window.detachEvent("onload",handleContentOnLoad );
	}
}

function getSessionId(){
	function handleGetSessionId( sessionId ) {
		window.is_sessionId = sessionId;
		
		readySessionId = true;
		
		runOnLoadHandlers();
	}
	
	try {
		if( !window.is_sessionId ) window.is_sessionId = top.is_sessionId;
	} catch( ex ) { }
	
	if( !window.is_sessionId ) {
		setTimeout( function() {
			gadgets.rpc.call( null,"is_get_session_id",handleGetSessionId );
		},100 );
	} else {
		handleGetSessionId( window.is_sessionId );
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
	
	getSessionId();
	
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
