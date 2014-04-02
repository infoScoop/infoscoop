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

( function(){
gadgets.config.init( is_gadgetConfiguration );

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
