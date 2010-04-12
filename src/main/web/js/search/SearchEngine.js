IS_Portal.searchEngines = new Array();
IS_Portal.rssSearchEngines = new Array();

var IS_SearchEngine = IS_Class.create();
IS_SearchEngine.prototype.classDef = function() {
	var self = this;
	var pageEncoding, countRule, useProxySearch, useProxyRedirect;
	
	this.initialize = function(name, searchUrl, originalUrl, encoding, _pageEncoding, _countRule, _useProxySearch, _useProxyRedirect, _searchId){
		this.name = name;
		this.searchUrl = searchUrl;
		this.originalUrl = originalUrl;
		this.encoding = encoding;
		this.countRule = countRule;
		this.iframe;
		this.indicator;
		this.tab;
		this.tabA;
		this.count;
		this.tabContent;
		this.renderCompleted = false;
		
		this.container;
		this.isDisplay;
		this.keyword;
		this.isRender;
		this.titles = [name];
		
		this.searchId = _searchId;
		
		pageEncoding = _pageEncoding;
		countRule = _countRule;
		useProxySearch = _useProxySearch;
		useProxyRedirect = _useProxyRedirect;
	}
	
	this.buildTabHeader = function(isActive){
		var tabsLi = document.createElement("li");
		this.tab = tabsLi;
		tabsLi.className = "search_tabs";
		var tabsA = document.createElement("a");
		this.tabA = tabsA;
		tabsA.id = "tab_"+encodeURIComponent( this.name );
		tabsA.className = "tab" +( isActive ? " selected":"");
		tabsA.href = "#"+encodeURIComponent( this.name );
		tabsLi.appendChild(tabsA);
		
		var tabsIndicator = document.createElement("img");
		tabsIndicator.src = imageURL+"indicator.gif";
		this.indicator = tabsIndicator;
		tabsIndicator.className = "indicator";
		tabsA.appendChild(tabsIndicator);
		
		var tabTitle = document.createElement("span");
		tabTitle.className = "title";
		tabsA.appendChild( tabTitle );
		
		tabTitle.appendChild(document.createTextNode(this.name));
		if(this.titles.length > 1) {
//			var titleMore = document.createElement("span");
//			titleMore.innerHTML = "&nbsp;▼&nbsp;";
//			titleMore.innerHTML = IS_R.search_SearchEngine_titleMore;
//			titleMore.style.fontSize = "80%";
			tabsA.className += " multi";
			
			var titleMore = document.createElement("div");
			titleMore.className = "titleMore";
//			titleMore.src = imageURL + "bullet_arrow_down.gif";
			IS_Event.observe(tabsLi, "mouseover", this.showTitles.bind(this), false, "_search");
			IS_Event.observe(tabsLi, "mouseout", this.hideTitles.bind(this), false, "_search");
			tabsA.appendChild(titleMore);
		}
		
		return tabsLi;
	}
	this.addTitle = function(title) {
		this.titles.push(title);
	}
	this.showTitles = function(e) {
		var id = this.name + "_titles";
		var div = document.getElementById(id);
		if(!div) {
			div = document.createElement("div");
			div.id = this.name + "_titles";
			div.className = "search_titles";
			div.style.display = "none";
			for(var i = 0; i < this.titles.length; i++){
				div.appendChild(document.createTextNode(this.titles[i]));
				div.appendChild(document.createElement("br"));
			}
			document.body.appendChild(div);
		}
		if(div.style.display != "none") {
			div.style.display = "none";
		} else {
			div.style.width = this.tabA.offsetWidth - (Browser.isIE ? 0 : 6);
			div.style.left = findPosX(this.tabA);
			div.style.top = findPosY(this.tabA) + this.tabA.offsetHeight;
			div.style.display = "block";
		}
	}
	this.hideTitles = function(e) {
		var id = this.name + "_titles";
		var div = document.getElementById(id);
		if(div)	div.style.display = "none";
	}
	this.buildTabContent = function(){
		this.renderCompleted = false;
		var resultDiv = document.createElement("div");
		this.tabContent = resultDiv;
		resultDiv.id = encodeURIComponent( this.name );
		resultDiv.className="search_tabs";
		resultDiv.style.display = this.isDisplay;
		resultDiv.style.border = "1px solid gray";
		var searchIframe = document.createElement("IFrame");
		searchIframe.frameBorder = 0;
		this.iframe = searchIframe;
		searchIframe.id = this.name + "_frame";
		searchIframe.name = this.name + "_frame";
		searchIframe.style.width = "100%";
		searchIframe.style.height = "768px";
		searchIframe.style.margin = "2px";
		searchIframe.scrolling = "auto";
		searchIframe.style.border = "0 none white";
		resultDiv.appendChild(searchIframe);
		this.container.appendChild(resultDiv);
	}
	this.processSearch = function() {
		if( !this.shield ) {
			this.shield = document.createElement("div");
			this.shield.className = "shield";
			this.tab.appendChild( this.shield );
		}
		
		var keyword = this.encodedKeyword;
		var searchUrl;
		if (/\${KEYWORD}/.test(this.originalUrl)) {
			searchUrl = this.originalUrl.replace(/\${KEYWORD}/g, keyword);
		}else{
			searchUrl = this.originalUrl + keyword;
		}
		var searchResultUrl = is_getProxyUrl(searchUrl, "SearchResult",this.encoding );
		
		this.redirectUrl = useProxyRedirect ? searchResultUrl : searchUrl;
		if(useProxySearch && countRule && countRule.method && countRule.value){
			var headers = ["MSDPortal-Cache", "Cache-NoResponse"];
			headers.push("MSDPortal-Select", countRule.method + "=" + encodeURIComponent(countRule.value));
			
			var opt = {
				method: 'get' ,
				asynchronous:true,
				requestHeaders: headers,
				timeout: -1,
				onSuccess: this.endSearch.bind(this),
				on1223: this.endSearch.bind(this),
				on404: function(t) {

					msg.error(IS_R.getResource(IS_R.ms_searchEngineLoadon404, [self.name]));
					self.endSearchCommon();
				},
				on10408: function(t) {

					msg.error(IS_R.ms_searchEngineLoadon10408);
					self.endSearchCommon();
				},
				onFailure: function(t) {

					msg.error(IS_R.getResource(IS_R.ms_searchEngineLoadonFailure, [self.name, t.status, t.statusText]));
					self.endSearchCommon();
				},
				onException: function(r, t){

					msg.error(IS_R.getResource(IS_R.ms_searchEngineLoadonException, [self.name, getText(t)]));
					self.endSearchCommon();
				}
			};
			
			this.searchReqId = this.searchId + "_searchReq_" + new Date().getTime();
			AjaxRequest.invoke( searchResultUrl, opt, this.searchReqId);
		} else {
			this.endSearchCommon();
		}
	}
	this.endSearchCommon = function() {
		this.indicator.style.display = "none";
		this.tabA.style.cursor = "pointer";
//		IS_Event.observe(this.tabA, "click", IS_SearchEngines.switchTab.bind(this.tabA), false, "_search");
		Element.remove( this.shield );
		this.shield = undefined;
		
		if(this.isRender) {
			this.renderResult();
		}
		IS_Portal.SearchEngines.next();
	}
	this.endSearch = function(req) {
		this.cacheID = req.getResponseHeader("MSDPortal-Cache-ID");
		var countText = req.getResponseHeader("MSDPortal-Match");
		this.endSearchCommon();
		
		var font = $(this.name + "Count");
		if(!font) {
			font = document.createElement("font");
			font.id = this.name + "Count";
		} else {
			font.innerHTML = "";
		}
		
		var count = 0;
		if(countRule && countText && countText != "") {
			try{
				count = parseInt(countText);
				if(count && count > 0) {
					font.className = "hit";
				}
			}catch(e){
			}
		}
		
		if( !font.className )
			font.className = "nohit";
		
		var text = " ( " +( count == 0 ? count : countText ) +" )";
		font.appendChild(document.createTextNode( text ));
		$("tab_"+encodeURIComponent( this.name )).appendChild( font );
		this.adjustTabWidth();
	}
	this.renderResult = function() {
		if(!this.renderCompleted) {
			if(this.cacheID)
				this.iframe.src = hostPrefix + "/cacsrv?id=" + this.cacheID + "&url=" + encodeURIComponent(this.redirectUrl);
			else
				this.iframe.src = this.redirectUrl;
			
			//if(Browser.isIE) {
				if( !this.reanderCompleteHandler )
					this.reanderCompleteHandler = this.reanderComplete.bind( this );
				
				Event.observe(this.iframe, "load", this.reanderCompleteHandler);
			//} else {
			//	this.reanderComplete();
			//}
		}
	}
	this.reanderComplete = function() {
		this.renderCompleted = true;
		
		if( this.reanderCompleteHandler )
			Event.stopObserving( this.iframe,"load",this.reanderCompleteHandler );
		
		try{
			var iframeDoc = Browser.isIE ? this.iframe.contentWindow.document : this.iframe.contentDocument;
			var current = this.iframe.contentWindow.location.href;
			var base = iframeDoc.getElementById("baseUrl");
			if( base ) {
				var baseUrl = base.href;
				function absolute( url ) {
					var i = url.indexOf( baseUrl );
					var q = url.substring( baseUrl.length );
					if( i == 0 && /^\s*\#/.test( q ))
						url = current +q;
					
					return url;
				}
				
				$A( iframeDoc.body.getElementsByTagName("a") ).each( function( anchor ) {
					anchor.href = absolute( anchor.href );
				});
			}
		}catch(e){
			msg.warn(getText(e));
			return;
		}
	}
	this.adjustTabWidth = function() {
		this.tabA.style.width = "auto";
		var nonactive = false;
		if( !Element.hasClassName( this.tabA,"selected")) {
			Element.addClassName( this.tabA,"selected");
			nonactive = true;
		}
		var width = this.tabA.offsetWidth;
		if(nonactive)
			Element.removeClassName( this.tabA,"selected");
		if(width < 100)
			width = 100;
		this.tabA.style.width = width + "px";
	}
}
var IS_SearchEngines = IS_Class.create();
IS_SearchEngines.prototype.classDef = function(){
	var self = this;
	var number = 0;
	
	this.initialize = function() {
		this.loadConfig(true);
	}
	
	this.readSearchEngineConfig = function(req){
		var xml = req.responseXML;
		var root = xml.documentElement;
		var defaultSearch = root.getElementsByTagName("defaultSearch")[0];
		var configs = defaultSearch.getElementsByTagName("searchEngine");
		for(var i = 0; i < configs.length;i++){
			var title = configs[i].getAttribute("title");
			var retrieveUrl = configs[i].getAttribute("retrieveUrl");
			var originalUrl = retrieveUrl;
			var useProxyRedirectAtt = configs[i].getAttribute("useProxyRedirect");
			var useProxyRedirect = (useProxyRedirectAtt && ( /true/.test(useProxyRedirectAtt) || /TRUE/.test(useProxyRedirectAtt) ) ) ? true: false;
			var encoding = configs[i].getAttribute("encoding");
			var pageEncoding = configs[i].getAttribute("pageEncoding");
			var useProxyAtt = configs[i].getAttribute("useProxy");
			var useProxySearch = (useProxyAtt &&  ( /false/.test(useProxyAtt) || /FALSE/.test(useProxyAtt) ) ) ? false: true;
//			if (useProxySearch) {
//				retrieveUrl = is_getProxyUrl(retrieveUrl, "URLReplace");
//			}
			var countEle = configs[i].getElementsByTagName("countRule")[0];
			var countRule = null;
			if(countEle){
				var countMethod = countEle.getAttribute("method");
				var countValue = countEle.getAttribute("value");
				countRule = {method:countMethod, value:countValue};
			}
			var searchId = "portal_search_" + i;
			IS_Portal.searchEngines[i] = new IS_SearchEngine(title, retrieveUrl, originalUrl, encoding, pageEncoding, countRule, useProxySearch, useProxyRedirect, searchId);
		}
		
		var rssSearch = root.getElementsByTagName("rssSearch")[0];
		if(rssSearch) {
			var rssSearchs = rssSearch.getElementsByTagName("searchEngine");
			for(var i = 0; i < rssSearchs.length;i++){
				var rssSearchEngine = {};
				rssSearchEngine.retrieveUrl = rssSearchs[i].getAttribute("retrieveUrl");
				rssSearchEngine.originalUrl = rssSearchEngine.retrieveUrl;
				var useProxyRedirectAtt = rssSearchs[i].getAttribute("useProxyRedirect");
				rssSearchEngine.useProxyRedirect = (useProxyRedirectAtt &&  ( /true/.test(useProxyRedirectAtt) || /TRUE/.test(useProxyRedirectAtt) ) ) ? true: false;
				rssSearchEngine.encoding = rssSearchs[i].getAttribute("encoding");
				rssSearchEngine.pageEncoding = rssSearchs[i].getAttribute("pageEncoding");
				var useProxyAtt = rssSearchs[i].getAttribute("useProxy");
				rssSearchEngine.useProxySearch = (useProxyAtt &&  ( /false/.test(useProxyAtt) || /FALSE/.test(useProxyAtt) ) ) ? false: true;
//				if (useProxySearch && rssSearchEngine.retrieveUrl) {
//					rssSearchEngine.retrieveUrl = is_getProxyUrl(rssSearchEngine.retrieveUrl, "URLReplace");
//				}
				var countEle = rssSearchs[i].getElementsByTagName("countRule")[0];
				var countRule = null;
				if(countEle){
					var countMethod = countEle.getAttribute("method");
					var countValue = countEle.getAttribute("value");
					rssSearchEngine.countRule = {method:countMethod, value:countValue};
				}
				var rssPatternEle = rssSearchs[i].getElementsByTagName("rssPattern")[0];
				if(rssPatternEle && rssPatternEle.firstChild)
					rssSearchEngine.rssPattern = new RegExp(rssPatternEle.firstChild.nodeValue);
				IS_Portal.rssSearchEngines.push(rssSearchEngine);
			}
		}
	}
	
	this.matchRssSearch = function(url) {
		if(!url) return;
		for(var j = 0; j < IS_Portal.rssSearchEngines.length; j++) {
			if(url.match(IS_Portal.rssSearchEngines[j].rssPattern)) {
				if(!IS_Portal.rssSearchEngines[j].retrieveUrl)
					return false;
				return IS_Portal.rssSearchEngines[j];
			}
		}
		return false;
	}
	
	this.buildSearchTabs = function(keyword, urllist){
		if(IS_Portal.searchEngines.length == 0){

			alert(IS_R.ms_searchNotAvailable);
			return;
		}
		if(Browser.isSafari1 && IS_Portal.isTabLoading()){
			return;
		}
		
		IS_Portal.CommandBar.changeIframeView();
		
		if(this.buildTabTimer)
			clearTimeout(this.buildTabTimer);
		
		this.buildTabTimer = setTimeout(this._buildSearchTabs.bind(this, keyword, urllist), 100);
//		this._buildSearchTabs(keyword, urllist);

		var searchEngines = IS_Portal.searchEngines;
		for(var i=0;i<searchEngines.length;i++){
			if(searchEngines[i].iframe)
				searchEngines[i].iframe.src = "about:blank";
			
			searchEngines[i].isRender = false;
			searchEngines[i].renderCompleted = false;
			
			if (searchEngines[i].searchReqId) 
				AjaxRequest.cancel(searchEngines[i].searchReqId);
			if (searchEngines[i].encodeReqId) 
				AjaxRequest.cancel(searchEngines[i].encodeReqId);
		}
	}
	
	this._buildSearchTabs = function(keyword, urllist){
		if(is_jlength(keyword) > 256){

			alert(IS_R.lb_keywordTooLong);
			return false;
		}
		IS_Event.unloadCache("_search");
		IS_Portal.widgetDisplayUpdated();
		/*
		var divIFrame = $("portal-iframe");
		if ( divIFrame ) {
			divIFrame.style.display = "none";
		}
		*/
		IS_Portal.closeIFrame();

		document.getElementById("panels").style.display='none';
		if(!configExists()){
			// Reload if loading config file for searching fails
			this.loadConfig(false);
			if(!configExists()) return;
		}
		
		function configExists(){
			if(!urllist && IS_Portal.searchEngines.length == 0){
				return false;
			}
			if(urllist && IS_Portal.rssSearchEngines.length == 0) {
				return false;
			}
			return true;
		}
		/*
		if(!urllist && IS_Portal.searchEngines.length == 0){
			return;
		}
		if(urllist && IS_Portal.rssSearchEngines.length == 0) {
			return;
		}
		*/
		
//		if(IS_Portal.refreshObj.isRefreshing) {
//			IS_Portal.cancelRefresh();
//			//Not run automatically updating program right after closing search window
//			//IS_Portal.refreshObj.cancelByIframe = true;
//		}
		IS_Portal.refresh.cancel();
		
		this.clearTemp();
		
		var searchPanel = document.getElementById("search-iframe");
		searchPanel.className = "SearchEngine";
		searchPanel.style.display="";
		
		var defaultTabsUl = $("search-tabs");
		var defaultResult = $("search-result");
		
		var tempEngines = null;
		var tabsID;
		if(urllist) {
			var searchUrls = {};
			if(defaultTabsUl)
				defaultTabsUl.style.display = "none";
			if(defaultResult)
				defaultResult.style.display = "none";
		
			tempEngines = [];
			this.tempEngines = tempEngines;
			for(var i = 0; i < urllist.length; i++) {
				var url = urllist[i].url;
				var rssSearchEngine = this.matchRssSearch(url);
				if(rssSearchEngine) {
					var originalUrl = url.replace(rssSearchEngine.rssPattern, rssSearchEngine.originalUrl);
					if(searchUrls[originalUrl]) {
						searchUrls[originalUrl].addTitle(urllist[i].title);
					} else {
						var retrieveUrl = url.replace(rssSearchEngine.rssPattern, rssSearchEngine.retrieveUrl);
						var engine = new IS_SearchEngine(
							urllist[i].title,
							retrieveUrl,
							originalUrl,
							rssSearchEngine.encoding,
							rssSearchEngine.pageEncoding,
							rssSearchEngine.countRule,
							rssSearchEngine.useProxySearch,
							rssSearchEngine.useProxyRedirect
						);
						tempEngines.push(engine);
						searchUrls[originalUrl] = engine;
					}
				}
			}
			tabsID = "temp_search-tabs";
			this.buildTabsUl( tabsID, searchPanel);
			this.buildResult("temp_search-result", searchPanel, keyword);
		} else {
			if(defaultTabsUl)
				defaultTabsUl.style.display = "block";
			if(defaultResult)
				defaultResult.style.display = "block";
			
			tempEngines = IS_Portal.searchEngines;
			this.tempEngines = tempEngines;
			
			var tabsUl = defaultTabsUl;
			if(!tabsUl) {
				tabsID = "search-tabs";
				this.buildTabsUl( tabsID, searchPanel);
			} else {
				tabsID = tabsUl.id;
				for(var i = 0; i < tempEngines.length; i++){
					if( i == 0 ) {
						Element.addClassName( tempEngines[i].tabA,"selected");
					} else {
						Element.removeClassName( tempEngines[i].tabA,"selected");
					}
					tempEngines[i].tabA.style.cursor = "";
					tempEngines[i].indicator.style.display = "";
					var countEle = $(tempEngines[i].name + "Count");
					if(countEle) {
						countEle.innerHTML = "";
						countEle.className = "";
					}
					tempEngines[i].adjustTabWidth();
				}
			}
			this.buildResult("search-result", searchPanel, keyword);
		}
		
		this.tab = new Control.Tabs( tabsID,{
			afterChange: function( container ) {
				IS_SearchEngines.switchTab( $("tab_"+container.id ) );
			}
		});
		
		number = 0;
		var parallelCount = Browser.isIE ? 2 : 3;
		for(var i = 0; i < parallelCount; i++) {
			this.next(tempEngines);
		}
		/*for(var i = 0; i < tempEngines.length; i++) {
			tempEngines[i].processSearch();
		}*/
		if(tempEngines.length > 0)
			IS_Portal.adjustIframeHeight(null, tempEngines[0].iframe);
//		IS_Portal.sidePanel.destroyDragEvent();
		
		//Register keywords on database when searching
		if (keywordEntry && getBooleanValue(keywordEntry)) {
			var cmd = new IS_Commands.AddKeywordCommand(keyword);
			IS_Request.LogCommandQueue.addCommand(cmd);
		}
	}
	
/**
 * Corresponds to Safari
 * If you search with maximized browser, it may shut down
 */
	if( Browser.isSafari1 ) {
		this._buildSearchTabs = ( function() {
			var _buildSearchTabs = this._buildSearchTabs;
			
			return function() {
				if( IS_Widget.MaximizeWidget )
					IS_Widget.MaximizeWidget.turnbackMaximize();
				
				IS_Portal.disableCommandBar();
				
				_buildSearchTabs.apply( this,$A( arguments ));
				
				IS_Portal.currentTabId = "_"+IS_Portal.currentTabId;
			}
		}).apply( this );
	}
	
	this.buildTabsUl = function(tabsID, searchPanel) {
		var tempEngines = this.tempEngines;
		var tabsUl = document.createElement("ul");
		tabsUl.id = tabsID;
		tabsUl.className = "search_tabs tabs";
		for(var i = 0; i < tempEngines.length; i++){
			tabsUl.appendChild(tempEngines[i].buildTabHeader( i == 0 ));
		}
		searchPanel.appendChild(tabsUl);
		for(var i = 0; i < tempEngines.length; i++){
			tempEngines[i].adjustTabWidth();
		}
	}
	
	this.buildResult = function(resultID, searchPanel, keyword) {
		var tempEngines = this.tempEngines;
		var searchResult = document.getElementById(resultID);
		if(!searchResult) {
			searchResult = document.createElement("div");
			searchResult.id = resultID;
			searchPanel.appendChild(searchResult);
		}
		searchResult.innerHTML = "";
		
		var texts = {};
		for(var i = 0; i < tempEngines.length; i++){
			tempEngines[i].container = searchResult;
			tempEngines[i].isDisplay = (i == 0) ? "" : "none";
			tempEngines[i].keyword = keyword;
			tempEngines[i].isRender = (i == 0) ? true : false;
			tempEngines[i].buildTabContent();
			
			var encoding = tempEngines[i].encoding;
			if( !texts[encoding] )
				texts[encoding] = this.encode( keyword,encoding );
			
			tempEngines[i].encodedKeyword = texts[encoding];
		}
	}
	
	this.encode = function(text, encoding) {
		text = encodeURIComponent(text);
		if(!encoding || encoding.toLowerCase() == "utf-8")
			return text;
		var opt = {
			method: 'get' ,
			asynchronous:false,
			retryCount: 0,
			onSuccess: function(res){ text = res.responseText; },
			onFailure: function(t) {
				msg.error(IS_R.getResource(IS_R.ms_urlEncodeonFailureAt, [self.name, t.status, t.statusText, text, encoding]));
			},
			onException: function(r, t){
				msg.error(IS_R.getResource(IS_R.ms_urlEncodeonExceptionAt, [self.name, t, text, encoding]));
			}
		};
		
		AjaxRequest.invoke(hostPrefix + "/encsrv?text=" + text + "&encoding=" + encoding, opt);
		return text;
	}
	this.next = function() {
		var tempEngines = this.tempEngines;
		if(number < tempEngines.length) {
			tempEngines[number++].processSearch();
		}
	}
	
	this.clearTemp = function() {
		var tabsUl = document.getElementById("temp_search-tabs");
		if(tabsUl)
			tabsUl.parentNode.removeChild(tabsUl);
		var searchResult = document.getElementById("temp_search-result");
		if(searchResult)
			searchResult.parentNode.removeChild(searchResult);
	}
	
	this.loadConfig = function( isSync ) {
		var url = is_getProxyUrl( searchEngineURL, "NoOperation");
		var opt = {
			method: 'get' ,
			asynchronous:isSync,
			onSuccess: this.readSearchEngineConfig,
			on404: function(t) {
				alert(IS_R.getResource(IS_R.lb_searchConfigOn404,[searchEngineURL]));
			},
			on10408: function(t,e){
				alert(IS_R.lb_searchConfigOn10408);				
			},
			onFailure: function(t) {
				alert(IS_R.getResource(IS_R.lb_searchConfigOnFailure,[t.status,(t.statusText ? t.statusText : "")]));				
			},
			onException: function(r, t) {
				alert(IS_R.getResource(IS_R.lb_searchConfigException, [getText(t)]));
			},
			onComplete: function() {
				self.isLoaded = true;
			}
		};
		
		AjaxRequest.invoke(url, opt);
	}
}

IS_SearchEngines.switchTab = function( switchTab ) {
	if( Element.hasClassName( switchTab,"selected")) return false;
	var tempEngines = IS_Portal.SearchEngines.tempEngines;
	var tab;
	var tabContent;
	for(var i = 0; i < tempEngines.length; i++){
		tab = tempEngines[i].tabA;
		if(!tab) return;
		tabContent = tempEngines[i].tabContent;
		if(tab.id == switchTab.id){
			if( !tempEngines[i].isRender ) 
				tempEngines[i].renderResult();
			Element.addClassName( tab,"selected");
			tabContent.style.display = "";
			IS_Portal.adjustIframeHeight(null, tempEngines[i].iframe);
		}else{
			Element.removeClassName( tab,"selected");
			tabContent.style.display = "none";
		}
	}
}
