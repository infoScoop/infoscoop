IS_SiteAggregationMenu ={
  menuItemList:{},
  topMenuIdList:[],
  menuItemTreeMap:{}
};
IS_SiteAggregationMenu.setMenu = function(url, a,b,c){
	IS_SiteAggregationMenu.menuItemList = Object.extend(IS_SiteAggregationMenu.menuItemList, a);
	IS_SiteAggregationMenu.topMenuIdList = IS_SiteAggregationMenu.topMenuIdList.concat(b);
	IS_SiteAggregationMenu.menuItemTreeMap = Object.extend(IS_SiteAggregationMenu.menuItemTreeMap, c);
}
IS_SiteAggregationMenu.init = function(){
	IS_SiteAggregationMenu.menuItemList = {};
	IS_SiteAggregationMenu.topMenuIdList = [];
	IS_SiteAggregationMenu.menuItemTreeMap = {};
}
IS_SidePanel = {};
IS_SidePanel.setMenu = function(url, a,b,c){
	IS_SiteAggregationMenu.menuItemList = Object.extend(IS_SiteAggregationMenu.menuItemList, a);
	IS_SiteAggregationMenu.topMenuIdList = IS_SiteAggregationMenu.topMenuIdList.concat(b);
	IS_SiteAggregationMenu.menuItemTreeMap = Object.extend(IS_SiteAggregationMenu.menuItemTreeMap, c);
}
  

/**
 * @element: HTML element to output menu explorer
 * @_callback: function that is called if select button is clicked
 *            {id:<menu ID>, title:<title>, type:<widget type>, properties:<widget property>}
 */
function ISA_buildMenuExplorer(element, _callback){
	var selectMenu;
	var displayTopMenu = ISA_Properties.propertiesList.displayTopMenu.value;
	var displaySideMenu = ISA_Properties.propertiesList.displaySideMenu.value;

	var helpDiv = document.createElement("div");
	helpDiv.style.fontSize = "90%";
	helpDiv.innerHTML = ISA_R.alb_selectWidetOnTop+"<br/>"
		+ ISA_R.alb_selectWidgetFromList
	element.appendChild(helpDiv);
	var errorMsgDiv = document.createElement("div");
	errorMsgDiv.style.color = "red";
	element.appendChild(errorMsgDiv);
	
	var menuExplorerDiv = document.createElement("div");
	menuExplorerDiv.id = "menuExplorerDiv";
	element.appendChild(menuExplorerDiv);

	function buildMenuSelect(){
		
		var changeMenuDiv = document.createElement("div");
		changeMenuDiv.id = "changeMenu";

		if( !(getBooleanValue(displayTopMenu) && getBooleanValue(displaySideMenu)) )
			changeMenuDiv.style.display = "none";
		
		changeMenuDiv.style.width = "30%";
		changeMenuDiv.style.textAlign = "right";
		changeMenuDiv.style.cssFloat = "right";
		changeMenuDiv.style.styleFloat = "right";
		var menuSelect = document.createElement('select');
		var topmenuOption = document.createElement('option');
		topmenuOption.value = "topmenu";
		topmenuOption.appendChild(document.createTextNode(ISA_R.alb_topmenu));
		menuSelect.appendChild(topmenuOption);
		var sidemenuOption = document.createElement('option');
		sidemenuOption.value = "sidemenu";
		sidemenuOption.appendChild(document.createTextNode(ISA_R.alb_sideMenu));
		menuSelect.appendChild(sidemenuOption);
		IS_Event.observe(menuSelect, 'change', function(){
			IS_SiteAggregationMenu.init();
			selectMenu = menuSelect.value;
			ISA_loadMenuConf(menuSelect.value, buildMenuItemList, function(errorMsg){errorMsgDiv.innerHTML = errorMsg;});
		}, false, "_menuExplorer");
		menuSelect.value = selectMenu;
		changeMenuDiv.appendChild(menuSelect);
		
		return changeMenuDiv;
	}
	
	function buildMenuItemList(menuItem){

		var table = ISA_Admin.buildTableHeader(
			['',ISA_R.alb_title,ISA_R.alb_type, ISA_R.alb_contentURL],
			['4.5em','20%', '10%', '65%']
			);
		
		table.style.width = "100%";
		table.style.clear = "both";
		var tbody = document.createElement("tbody");
		table.appendChild(tbody);
		
		if(menuExplorerDiv.firstChild){
			IS_Event.unloadCache("_menuExplorer");
			menuExplorerDiv.innerHTML = "";
		}
		menuExplorerDiv.appendChild(buildMenuSelect());
		if(menuItem){
			var returnParent = document.createElement("div");
//			returnParent.style.width = "30%";
			returnParent.style.cursor = "pointer";
			returnParent.style.textAlign = "left";
			returnParent.style.cssFloat = "left";
			returnParent.style.styleFloat = "left";
			returnParent.style.fontSize = "14px";
			var returnImg = document.createElement('img');
			returnImg.src = imageURL + "arrow_undo.gif";
			returnParent.appendChild(returnImg);
			returnParent.appendChild(document.createTextNode( ISA_R.alb_backTop));
			var parentMenu;
			if(menuItem.parentId){
				parentMenu = IS_SiteAggregationMenu.menuItemList[menuItem.parentId];
			}
			IS_Event.observe(returnParent, 'click', buildMenuItemList.bind(this, parentMenu), false, "_menuExplorer");	
			
			menuExplorerDiv.appendChild(returnParent);

			var menuList = IS_SiteAggregationMenu.menuItemTreeMap[menuItem.id];
			
			for(var i = 0; i < menuList.length; i++){
				var _menuItem = IS_SiteAggregationMenu.menuItemList[menuList[i]];
				tbody.appendChild(buildMenuItemRow(_menuItem));
			}
		}else{
			for(var i = 0; i < IS_SiteAggregationMenu.topMenuIdList.length; i++){
				var menuItem = IS_SiteAggregationMenu.menuItemList[IS_SiteAggregationMenu.topMenuIdList[i]];
				tbody.appendChild(buildMenuItemRow(menuItem));
			}
		}
		
		var tr = document.createElement("tr");
		tr.className = "proxyConfigList dynamicPanelWidgetList";
		var td = document.createElement("td");
		td.style.textAlign = "center";
		var mergeButton = document.createElement("input");
		mergeButton.type = "button";
		mergeButton.value = ISA_R.alb_bundle;
		mergeButton.id = "mergeButton";
		mergeButton.disabled = true;
		
		IS_Event.observe( mergeButton,"click",mergeSelect.bind( this,menuItem ) );
		IS_Event.observe( table,"click",toggleMergeButton );
		
		td.appendChild( mergeButton );
		tr.appendChild( td );
		var td = document.createElement("td");
		td.colSpan = "3"
		td.appendChild( document.createTextNode(" "));
		tr.appendChild( td );
		tbody.appendChild( tr );
		
		menuExplorerDiv.appendChild(table);
	}
	
	function toggleMergeButton( menuItem ) {
		if( !menuItem.parentId )
			return;
		
		var checked = IS_SiteAggregationMenu.menuItemTreeMap[menuItem.parentId].find( function( menuItemId ) {
			return $( menuItemId+"_feeds_check").checked;
		});
		
		$("mergeButton").disabled = !checked;
	}
	function mergeSelect( menuItem ) {
		var item = Object.clone( menuItem );
		item.type = "MultiRssReader";
		item.title = item.title || ISA_R.alb_noTitle;
		
		item.properties = Object.clone( item.properties || {} );
		if( !item.properties.url )
			item.properties.url = "http://";
		
		item.properties.children = IS_SiteAggregationMenu.menuItemTreeMap[item.id].findAll( function( menuItemId ) {
			return $( menuItemId+"_feeds_check").checked;
		});
		
		_callback( item );
	}
	function widgetSelect( menuItem ) {
		if(/MultiRssReader/.test( menuItem.type )) {
			var item = Object.clone( IS_SiteAggregationMenu.menuItemList[menuItem.parentId] );
			item.type = "MultiRssReader";
			item.title = item.title || ISA_R.alb_noTitle;
			item.properties = Object.clone( item.properties || {} );
			if( !item.properties.url )
				item.properties.url = "http://";
			
			item.properties.children = [ menuItem.id ];
			
			menuItem = item;
		}
		
		_callback( menuItem );
	}
	function buildMenuItemRow(menuItem){
		var tr = document.createElement("tr");
		tr.className = "proxyConfigList dynamicPanelWidgetList";
		var td = document.createElement("td");
		td.style.textAlign = "center";
		
		var check = document.createElement("input");
		check.type = "checkbox";
		check.id = menuItem.id+"_feeds_check";
		check.value = menuItem.id;
		check.checked = false;
		td.appendChild( check );
		if(!/RssReader/.test( menuItem.type )) {
			check.style.visibility = "hidden";
		} else {
			IS_Event.observe( check,"click",toggleMergeButton.bind( this,menuItem ) );
		}
		
		tr.appendChild( td );
		
		var td = document.createElement("td");
		var button = document.createElement("input");
		button.type = "button";
		button.value =ISA_R.alb_select;
		if(!menuItem.type /*|| "MultiRssReader" == menuItem.type*/){
			button.disabled = true;
		}
		IS_Event.observe(button, 'click', widgetSelect.bind(this, menuItem), false, "_menuExplorer");
		td.appendChild(button);
		
		var titleA = document.createElement("a");
		titleA.appendChild( document.createTextNode( menuItem.directoryTitle || menuItem.title ));
		
		var menuList = IS_SiteAggregationMenu.menuItemTreeMap[menuItem.id];
		if(menuList && menuList.length > 0){
			titleA.href = "javascript:;";
			IS_Event.observe(titleA, 'click', buildMenuItemList.bind(this, menuItem), false, "_menuExplorer");
		}
		td.appendChild(titleA);
		tr.appendChild(td);

		
		var title = " ";
		var conf = ISA_SiteAggregationMenu.widgetConfs[menuItem.type];
		
		if (conf) {
			if(conf.ModulePrefs){
				title = conf.ModulePrefs.directory_title || conf.ModulePrefs.title || conf.type;
			}else{
				title = (conf.title && 0 < conf.title.length) ? conf.title : conf.type;
			}
		}
		
		var td = document.createElement("td");
		td.appendChild(document.createTextNode(title));
		tr.appendChild(td);

		var td = document.createElement("td");
		td.innerHTML = (menuItem.properties && menuItem.properties.url ) ? menuItem.properties.url : "&nbsp;" ;
		tr.appendChild(td);
		
		return tr;
	}
	
	IS_SiteAggregationMenu.init();
	if(getBooleanValue(displayTopMenu)){
		ISA_loadMenuConf("topmenu", buildMenuItemList,
					function(errorMsg){
						if( getBooleanValue(displaySideMenu) ){
							selectMenu = "sidemenu";
							ISA_loadMenuConf("sidemenu", buildMenuItemList,function(){alert(ISA_R.ams_noMenuSelect);Control.Modal.close();});
						}
					});
	}else if(getBooleanValue(displaySideMenu)){
		selectMenu = "sidemenu";
		ISA_loadMenuConf("sidemenu", buildMenuItemList,
					function(errorMsg){
						alert(ISA_R.ams_noMenuSelect);Control.Modal.close();
					});
	}else{
		alert(ISA_R.ams_noMenuSelect);
		function modalClose(){
			if(Control.Modal.current)
			  Control.Modal.close();
			else
			  setTimeout(modalClose, 100);
		}
		setTimeout(modalClose, 100);
	}
}

function ISA_loadMenuConf(menutype, _callback, _errorCallback){
	var url = hostPrefix + "/mnusrv/" + menutype;
	var opt = {
	  method: 'get' ,
	  asynchronous:true,
	  requestHeaders:[
	  	"If-Modified-Since", "Thu, 01 Jun 1970 00:00:00 GMT",
	  	"Ignore-Access-Control","true"
	  ],
	  onSuccess: function(response){ eval(response.responseText); _callback();},
	  on404: function(t) {
		  msg.error(ISA_R.ams_menuNotFound + t.status + " - " + t.statusText);
		  _errorCallback(ISA_R.getResource(ISA_R.ams_notFoundMessage,[menuURL]));
	  },
	  onFailure: function(t) {
		  msg.error(ISA_R.ams_failedLoadingMenu + t.status + " - " + t.statusText);
		  _errorCallback(ISA_R.getResource(ISA_R.ams_failedLoadingMessage,[menuURL]));
	  },
	  onException: function(r, t){
		  msg.error(ISA_R.ams_failedLoadingMenu  + getErrorMessage(t));
		  _errorCallback(ISA_R.getResource(ISA_R.ams_failedLoadingMessage,[menuURL]));
	  },
	  onComplete: function(req, obj){
		  ISA_Admin.requestComplete = true;
	  },
	  onRequest: function() {
		  ISA_Admin.requestComplete = false;
	  }
	};
	AjaxRequest.invoke(url, opt);
	
}