var IS_SearchEngine = IS_Class.create();
IS_SearchEngine.prototype.classDef = function() {
	var self = this;
	var pageEncoding, countRule, useProxySearch, useProxyRedirect;
	
	this.initialize = function(_searchId, name, searchUrl, originalUrl, encoding, _pageEncoding, _countRule, _useProxySearch, _useProxyRedirect){
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

IS_Portal.SearchEngines = {
	_searchEngines : [],
	_rssSearchEngines : [],
	_defaultSearchOption : {
	  newwindow:false,
	  defaultSelectedList:[]
	},
	_selectedList:[],
	_number: 0,
	_searchEngineConfs : {},
	_needRebuildTabs : false,
	_openWindowList : [],
	init : function() {
		this.isNewWindow = $('panels') ? false : true;
		var editoption = $('editsearchoption');
		if(editoption){
			IS_Event.observe(editoption, 'mousedown', this._buildEditSearchOption.bind(this));
		}
		
		Event.observe(window, 'unload',  function(){
			for(var i = 0; i < this._openWindowList.length; i++)
			  this._openWindowList[i].close();
		}.bind(this));
	},

	_buildEditSearchOption:function(){
		if(!this.isLoaded){
			this.loadConf();
			setTimeout(this._buildEditSearchOption.bind(this), 200);
			return;
		}
		var editoption = $('editsearchoption');
		IS_Event.stopObserving(editoption, 'mousedown', this._buildEditSearchOption.bind(this));
		
		searchTd = editoption.parentNode;
		
		var searchoption = $.DIV({id:"searchoption",style:"fontSize:14px;border:1px solid gray;width:300px;backgroundColor:#FFF;display:none;position:absolute;zIndex:999;padding:5px;"});
		
		if(Browser.isIE){
			var pos = Position.cumulativeOffset(searchTd);
			searchoption.style.top = pos[1] + searchTd.offsetHeight;
			searchoption.style.left = pos[0];
			IS_Event.observe(window, 'resize', function(){
				var pos = Position.cumulativeOffset(searchTd);
				searchoption.style.left = pos[0];
			}, true);
		
		}
		searchoption.appendChild( $.DIV({style:"borderBottom:solid 1px #ddd;"}, IS_R.lb_searchOption) );
		var selectsearchsitediv = $.FIELDSET({id:"selectsearchsitefieldset", style:"margin:5px;"}, $.LEGEND({}, IS_R.lb_selectSearchSite));
		searchoption.appendChild(selectsearchsitediv);
		searchoption.appendChild(
			$.DIV(
				{style:"margin:5px;"},
				$.INPUT(
					{id:"displaySearchResultsOnNewWindow",type:'checkbox', defaultChecked:this.searchOption.displayNewWindow }),
					IS_R.lb_searchResultsOnNewWindow
				 )
			);
		searchoption.appendChild(
			$.DIV(
				{style:"margin:5px;"},
				$.INPUT({type:'button',value:IS_R.lb_saveSettings,onclick:{handler:this._saveSearchOptions.bind(this)}}),
				$.INPUT({type:'button',value:IS_R.lb_resetToDefaultSettings,onclick:{handler:this._resetSearchOptions.bind(this)}}),
				$.INPUT({type:'button',value:IS_R.lb_cancel,onclick:{handler:this._closeSearchOption.bind(this)}})
				  )
			);

		var configs = this._searchEngineConfigEls;
		for(var i = 0; i < configs.length;i++){
			var searchId = configs[i].getAttribute("id");
			var title = configs[i].getAttribute("title");
			selectsearchsitediv.appendChild($.DIV({},$.INPUT({type:'checkbox', value:searchId, defaultChecked:this._selectedList.include(searchId) }),title));
		}
		searchTd.appendChild(searchoption);

		IS_Event.observe(editoption, 'mousedown', this._showSearchOption.bind(this));

		var closer = $.DIV({
			  id:'searchOptionCloser',
			  className:'widgetMenuCloser'
			});
		document.body.appendChild( closer );
		IS_Event.observe(closer, 'mousedown', this._closeSearchOption.bind(this), true);
		
		this._showSearchOption();
	},
	
	loadConf:function(){
		if(this.isLoading)return;
		this.isLoading = true;
		this._loadConfig(true);
	},

	_showSearchOption:function(){
		var winX = Math.max(document.body.scrollWidth, document.body.clientWidth);
		var winY = Math.max(document.body.scrollHeight, document.body.clientHeight);

		var closer = $('searchOptionCloser');
		closer.style.width = winX;
		closer.style.height = winY;
		closer.style.display = "";

		var searchOptionSpan = $('searchoption');
		Element.show(searchOptionSpan);
		IS_Portal.behindIframe.show(searchOptionSpan);
	},

	_closeSearchOption:function(){
		Element.hide('searchoption');
		Element.hide('searchOptionCloser');
		IS_Portal.behindIframe.hide();
	},
	
	_readSearchEngineConfig : function(req){
		var xml = req.responseXML;
		var root = xml.documentElement;

		var newwindowAttr = root.getAttribute('newwindow');
		this._defaultSearchOption['newwindow'] = newwindowAttr ? /true/i.test(newwindowAttr) : false;
		if(!this.searchOption.displayNewWindow)
		  this.searchOption.displayNewWindow = this._defaultSearchOption['newwindow'];

		var prefsSitelist = this.searchOption.sitelist;
		
		var defaultSearch = root.getElementsByTagName("defaultSearch")[0];
		var configs = defaultSearch.getElementsByTagName("searchEngine");
		this._searchEngineConfigEls = configs;
		for(var i = 0; i < configs.length;i++){
			
			var title = configs[i].getAttribute("title");
			var retrieveUrl = configs[i].getAttribute("retrieveUrl");
			var originalUrl = retrieveUrl;
			var useProxyRedirectAtt = configs[i].getAttribute("useProxyRedirect");
			var useProxyRedirect = (useProxyRedirectAtt && /true/i.test(useProxyRedirectAtt) ) ? true: false;
			var encoding = configs[i].getAttribute("encoding");
			var pageEncoding = configs[i].getAttribute("pageEncoding");
			var useProxyAtt = configs[i].getAttribute("useProxy");
			var useProxySearch = (useProxyAtt && /false/i.test(useProxyAtt) ) ? false: true;
			
			var countEle = configs[i].getElementsByTagName("countRule")[0];
			var countRule = null;
			if(countEle){
				var countMethod = countEle.getAttribute("method");
				var countValue = countEle.getAttribute("value");
				countRule = {method:countMethod, value:countValue};
			}
			var searchId = configs[i].getAttribute("id");
			
			var defaultSelectedAttr = configs[i].getAttribute("defaultSelected");
			var defaultSelected = false;
			if(defaultSelectedAttr && /true/i.test(defaultSelectedAttr)){
				this._defaultSearchOption.defaultSelectedList.push(searchId);
				defaultSelected = true;
			}
			this._searchEngineConfs[searchId] = [searchId, title, retrieveUrl, originalUrl, encoding, pageEncoding, countRule, useProxySearch, useProxyRedirect];
			var searchEngineEnable = prefsSitelist ? prefsSitelist.include(searchId) : defaultSelected ;
			if( searchEngineEnable ){
				this._searchEngines.push( new IS_SearchEngine(searchId, title, retrieveUrl, originalUrl, encoding, pageEncoding, countRule, useProxySearch, useProxyRedirect) );
				this._selectedList.push(searchId);
			}
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
				this._rssSearchEngines.push(rssSearchEngine);
			}
		}
	},

	_saveSearchOptions : function(){
		var searchSiteCheckBoxList = $('selectsearchsitefieldset').getElementsByTagName('input');
		var selectSiteList = [];
		var newSearchEngines = [];
		var enableSearchEngines = this._searchEngines;

		var emptySearchSite = true;
		for(var i = 0; i < searchSiteCheckBoxList.length; i++){
			if(searchSiteCheckBoxList[i].checked){
				emptySearchSite = false;
				break;
			}
		}
		if(emptySearchSite){
			alert(IS_R.ms_selectSearchSite);
			return;
		}
		
		for(var i = 0; i < searchSiteCheckBoxList.length; i++){
			var searchId = searchSiteCheckBoxList[i].value;

			var searchEngine = false;
			for(var j = 0; j < enableSearchEngines.length; j++) {
				if( searchId == enableSearchEngines[j].searchId)
				  searchEngine = enableSearchEngines[j]
			}
			if(searchSiteCheckBoxList[i].checked){
				selectSiteList.push(searchId);
				var args = this._searchEngineConfs[searchId];
				if(!searchEngine)
				    searchEngine = new IS_SearchEngine(args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9]) ;
				newSearchEngines.push(searchEngine);
			}
		}
		
		this._searchEngines = newSearchEngines;
		var displayNewWindow = $('displaySearchResultsOnNewWindow').checked;
		IS_Widget.setPreferenceCommand('searchOption', Object.toJSON({sitelist: selectSiteList, displayNewWindow:displayNewWindow}));
		IS_Request.CommandQueue.fireRequest();
		
		this._selectedList = selectSiteList;
		this.searchOption.displayNewWindow = displayNewWindow;
		this._needRebuildTabs = true;
		this._closeSearchOption();
	},

	_resetSearchOptions : function(){
		var searchSiteCheckBoxList = $('selectsearchsitefieldset').getElementsByTagName('input');
		var defaultSelectedList = this._defaultSearchOption.defaultSelectedList;
		for(var i = 0; i < searchSiteCheckBoxList.length; i++){
			searchSiteCheckBoxList[i].checked = defaultSelectedList.include(searchSiteCheckBoxList[i].value);
		}
		$('displaySearchResultsOnNewWindow').checked = this._defaultSearchOption.newwindow;
	},
	
	
	matchRssSearch : function(url) {
		if(!url) return;
		var rssSearchEngines = this._rssSearchEngines;
		for(var j = 0; j < rssSearchEngines.length; j++) {
			if(url.match(rssSearchEngines[j].rssPattern)) {
				if(!rssSearchEngines[j].retrieveUrl)
					return false;
				return rssSearchEngines[j];
			}
		}
		return false;
	},
	
	buildSearchTabs : function(keyword, urllist){
		if(!this.isNewWindow && this.searchOption.displayNewWindow){
			var searchResultWindow = window.open("js/search/searchEngine.jsp?keyword=" + encodeURIComponent(keyword));
			this._openWindowList.push(searchResultWindow);
			return;
		}
		if(!this.isLoaded){
			IS_Portal.SearchEngines.loadConf();
			setTimeout(this.buildSearchTabs.bind(this, keyword,urllist), 200);
			return;
		}

		if(this._searchEngines.length == 0){
			alert(IS_R.ms_searchNotAvailable);
			return;
		}
		
		if(Browser.isSafari1 && IS_Portal.isTabLoading()){
			return;
		}
		if(!this.isNewWindow)
			IS_Portal.CommandBar.changeIframeView();
		
		if(this.buildTabTimer)
			clearTimeout(this.buildTabTimer);
		
		this.buildTabTimer = setTimeout(this._buildSearchTabs.bind(this, keyword, urllist), 100);
//		this._buildSearchTabs(keyword, urllist);

		var searchEngines = this._searchEngines;
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
	}.bind(IS_Portal.SearchEngines),
	
	_buildSearchTabs : function(keyword, urllist){
		if(is_jlength(keyword) > 256){

			alert(IS_R.lb_keywordTooLong);
			return false;
		}
		IS_Event.unloadCache("_search");
		if(!this.isNewWindow){
			IS_Portal.widgetDisplayUpdated();
			IS_Portal.closeIFrame();
			Element.hide('panels');
		}

		if(!urllist && this._searchEngines.length == 0 || urllist && this._rssSearchEngines.length == 0){
			// Reload if loading config file for searching fails
			this._loadConfig(false);
			return;
		}
		
		if(!this.isNewWindow)
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
			searchPanel.appendChild( this._buildTabsUl( tabsID, searchPanel) );
			this._buildResult("temp_search-result", searchPanel, keyword);
		} else {
			if(defaultTabsUl)
				defaultTabsUl.style.display = "block";
			if(defaultResult)
				defaultResult.style.display = "block";
			
			tempEngines = this._searchEngines;
			this.tempEngines = tempEngines;
			
			var tabsUl = defaultTabsUl;
			if(this._needRebuildTabs || !tabsUl) {
				tabsID = "search-tabs";
				var newTabsUl = this._buildTabsUl( tabsID, searchPanel);
				if(!tabsUl)
				  searchPanel.appendChild(newTabsUl);
				else{
					searchPanel.replaceChild(newTabsUl, tabsUl);
					this._needRebuildTabs = false;
				}
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
			this._buildResult("search-result", searchPanel, keyword);
		}
		
		this.tab = new Control.Tabs( tabsID,{
			afterChange: function( container ) {
				IS_Portal.SearchEngines.switchTab( $("tab_"+container.id ) );
			}
		});
		
		this._number = 0;
		var parallelCount = Browser.isIE ? 2 : 3;
		for(var i = 0; i < parallelCount; i++) {
			this.next(tempEngines);
		}
		/*for(var i = 0; i < tempEngines.length; i++) {
			tempEngines[i].processSearch();
		}*/
		if(!this.isNewWindow && tempEngines.length > 0)
			IS_Portal.adjustIframeHeight(null, tempEngines[0].iframe);
		
		//Register keywords on database when searching
		if (keywordEntry && getBooleanValue(keywordEntry)) {
			var cmd = new IS_Commands.AddKeywordCommand(keyword);
			IS_Request.LogCommandQueue.addCommand(cmd);
		}
	},
	
	_buildTabsUl : function(tabsID, searchPanel) {
		var tempEngines = this.tempEngines;
		var tabsUl = document.createElement("ul");
		tabsUl.id = tabsID;
		tabsUl.className = "search_tabs tabs";
		for(var i = 0; i < tempEngines.length; i++){
			tabsUl.appendChild(tempEngines[i].buildTabHeader( i == 0 ));
		}
		for(var i = 0; i < tempEngines.length; i++){
			tempEngines[i].adjustTabWidth();
		}
		return tabsUl;
	},
	
	_buildResult : function(resultID, searchPanel, keyword) {
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
				texts[encoding] = this._encode( keyword,encoding );
			
			tempEngines[i].encodedKeyword = texts[encoding];
		}
	},
	
	_encode : function(text, encoding) {
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
	},
	
	next : function() {
		var tempEngines = this.tempEngines;
		if(this._number < tempEngines.length) {
			tempEngines[this._number++].processSearch();
		}
	},

	clearTemp : function() {
		var tabsUl = document.getElementById("temp_search-tabs");
		if(tabsUl)
			tabsUl.parentNode.removeChild(tabsUl);
		var searchResult = document.getElementById("temp_search-result");
		if(searchResult)
			searchResult.parentNode.removeChild(searchResult);
	},
	
	_loadConfig : function( isSync ) {
		var url = is_getProxyUrl( searchEngineURL, "NoOperation");
		var opt = {
			method: 'get' ,
			asynchronous:isSync,
			onSuccess: this._readSearchEngineConfig.bind(this),
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
				this.isLoaded = true;
			}.bind(this)
		};
		
		AjaxRequest.invoke(url, opt);
	},
	
	switchTab : function( switchTab ) {
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
				if($('panel'))
				  IS_Portal.adjustIframeHeight(null, tempEngines[i].iframe);
			}else{
				Element.removeClassName( tab,"selected");
				tabContent.style.display = "none";
			}
		}
	},

	clearIFrames: function(){
		var searchEngineList = this._searchEngines;
		for(var i = 0; i <searchEngineList.length; i++){
			var sIframe = searchEngineList[i].iframe;
			if(sIframe) sIframe.src = "./blank.html";
		}
	}
}

/**
 * Corresponds to Safari
 * If you search with maximized browser, it may shut down
 */
if( Browser.isSafari1 ) {
	IS_SearchEngines._buildSearchTabs = ( function() {
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
