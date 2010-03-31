
_args = gadgets.util.getUrlParameters;

function _IG_GetImage(url){
	var img = document.createElement("img");
	img.src = _IG_GetImageUrl(url);
	
	return img;
}
function _IG_GetImageUrl(url){
	return url;
}
function _IG_GetCachedUrl(url){
	return url;
}

function _toggle(elem){
	if (elem.style.display == "" || elem.style.display == "block") {
		elem.style.display = "none";
	}
	else if (elem.style.display == "none") {
		elem.style.display = "block";
	}
}
function _uc(str){
	return str.toUpperCase();
}

( function() {
	function getAuthType( sendUidParam ) {
		switch( sendUidParam.toLowerCase()) {
		case "post":
			return gadgets.io.AuthorizationType.IS_POST_PORTAL_UID;
		case "header":
			return gadgets.io.AuthorizationType.IS_SEND_PORTAL_UID_HEADER;
		}
	}
	
	var fetchContent = _IG_FetchContent;
	_IG_FetchContent = function( url,callback,opt_params ) {
		if( opt_params && opt_params.sendUid )
			opt_params.AUTHORIZATION = getAuthType( opt_params.sendUid );
		
		return fetchContent.apply( this,[url,callback,opt_params]);
	}
	
	var fetchXmlContent = _IG_FetchXmlContent;
	_IG_FetchXmlContent = function( url,callback,opt_params ) {
		if( opt_params && opt_params.sendUid )
			opt_params.AUTHORIZATION = getAuthType( opt_params.sendUid );
		
		return fetchXmlContent.apply( this,[url,callback,opt_params]);
	}
	
	var fetchFeedAsJson = _IG_FetchFeedAsJSON;
	_IG_FetchFeedAsJSON = function( url,callback,num_entries,get_summaries,opt_params ) {
		if( opt_params && opt_params.sendUid )
			opt_params.AUTHORIZATION = getAuthType( opt_params.sendUid );
		
		return fetchFeedAsJson.apply( this,[url,callback,num_entries,get_summaries,opt_params]);
	}
})();

function _IS_GetLoginUid() {
	return window.is_userId;
}

/*
  Drop RssReader to User Panel.
  Without synchronized to menu config.
  @param url RSS Feed URL
  @param title RSS title
 */
function _IS_DropRssReader(url, title, href) {
	gadgets.rpc.call( null,"is_add_widget_to_panel",null,'RssReader',url, title, href );
}
/*
  Drop Minibrowser to User Panel.
  Without synchronized to menu config.
  @param url WebSite URL
  @param title WebSite title
 */
function _IS_DropMiniBrowser(url, title, href) {
	gadgets.rpc.call( null,"is_add_widget_to_panel",null,'MiniBrowser',url, title, href );
}

/*
  Drop URL specified Gadget to User Panel.
  Without synchronized to menu config.
  @param url Gadget URL
  @param title Gadget title
 */
function _IS_DropGadget(url, title, href) {
	gadgets.rpc.call( null,"is_add_widget_to_panel",null, 'Gadget', url, title, href );
}
/*
 * Open link in inline frame of infoScoop.
 * @param url target url
 */
function _IS_OpenPortalIframe(url){
	gadgets.rpc.call(null,"is_open_portal_iframe",null, url);
}
