IS_Portal.treeMenuObject = false;
/**
 * Manage opening and closing status of left tool bar
 * Here is the performance to be managed
 * １．Opening and closing of tool bar
 * ２．Adjustment of tool bar width
 * ３．Switching tabs
 * TODO:The three performance above is in one mehod so divide them.
 * TODO:Pass the parameter by dividing methods as the many conditions because of global variables
 * TODO:Stop the transmission of Event of tab icon while dislaying tool bar.
 */
IS_SidePanel = IS_Class.create();
IS_SidePanel.prototype.classDef = function () {
	var self = this;
	
	var isRssReaderBuild = false;
	
	var treeOpen = document.getElementById("siteMenuOpen");
	var siteMenu = document.getElementById("siteMenu");
	var container = document.getElementById("portal-tree-menucontainer");
	var sidemenu = false;
	var addContent = false;
	var mySiteMap = false;
	
	this.initialize = function() {
		if(sideMenuTabs.length == 0){
			treeOpen.style.display = "none";
			return;
		}
		Event.observe(treeOpen, 'mousedown', IS_SidePanel.Drag.dragStart, false);
		for(var i = 0; i < sideMenuTabs.length; i++){
			var sideMenuTab = sideMenuTabs[i];
			if (sideMenuTab == "sidemenu" && displaySideMenu)//sideMenuURL == undefined || sideMenuURL != ""
				sidemenu = true;
			
			if (sideMenuTab == "addContent")
				addContent = true;
				
			if (sideMenuTab == "mySiteMap")
				mySiteMap = true;
		}
		
		var sidePanelContent = document.createElement("div");
		while( container.firstChild )
			sidePanelContent.appendChild( container.firstChild );
		
		container.appendChild( sidePanelContent );
		
		var dummy = document.createElement("div");
		dummy.id = "portal-tree-menucontainer"+"-dummy";
		container.parentNode.appendChild( dummy );
		
		var d2 = document.createElement("div");
		d2.style.width = "20px";
		if( $("siteMenuOpenTd")) $("siteMenuOpenTd").appendChild( d2 );
		
		if(sidemenu) {
			addTab("sidemenu",{
				open: makeTabButton({
					open: true,
					id: "openImage",
					className: "siteMapOpen",
					title: IS_R.lb_openSiteMap,
					image: "sidemenu_nonactive"
				}),
				close: makeTabButton({
					id: "closeImage",
					className: "siteMapClose",
					title: IS_R.lb_closeSitemap,
					image: "sidemenu_active"
				}),
				onOpen: function() {
					if(!IS_Portal.treeMenuObject) {
						// fix #305
		//				document.getElementById("portal-tree-menu").innerHTML = "";
						IS_Portal.treeMenuObject =  new IS_SidePanel.SiteMap();
					}
					
					$("portal-tree-menu").style.display = "";
				},
				onClose: function() {
					$("portal-tree-menu").style.display = "none";
				}
			});
		} else {
			displaySideMenu = false;
		}

		if (addContent) {
			addTab("addContent",{
				open: makeTabButton( {
					open: true,
					id: "openRssSearch",
					className: "rssSearchOpen",
					title: IS_R.lb_openAddContents,
					image: "addContents_nonactive"
				}),
				close: makeTabButton( {
					id: "closeRssSearch",
					className: "rssSearchClose",
					title: IS_R.lb_closeAddContents,
					image: "addContents_active"
				}),
				onOpen: function() {
					if( !self.isAddContentsBuild ) {
						IS_SidePanel.buildAddContents();
						self.isAddContentsBuild = true;
					}
					
					$("portal-rss-search").style.display = "";
				},
				onClose: function() {
					$("portal-rss-search").style.display = "none";
				}
			})
		}
		
		if( mySiteMap ) {
			( function() {
				var mySiteMapContainer = $("portal-my-sitemap");
				
				addTab("mySiteMap",{
					open: makeTabButton( {
						open: true,
						className: "mySiteMapOpen",
						title: IS_R.lb_openMySiteMap,
						image: "mysitemap_nonactive"
					}),
					close: makeTabButton( {
						className: "mySiteMapClose",
						title: IS_R.ms_closeMySiteMap,
						image: "mysitemap_active"
					}),
					onOpen: function() {
						if( !self.isMySiteMapBuild ) {
							self.mySiteMap = new IS_MySiteMap();
							self.mySiteMap.build( mySiteMapContainer );
							self.isMySiteMapBuild = true;
						} else {
							self.mySiteMap.rebuild();
						}
						
						mySiteMapContainer.style.display = "";
					},
					onClose: function() {
						mySiteMapContainer.style.display = "none";
					}
				})
			})();
		}
		
		Event.observe( window,"scroll",IS_SidePanel.adjustPosition );
		Event.observe( window,"resize",IS_SidePanel.adjustPosition );
	}
	
	function makeTabButton( opt ) {
		var tabButton = document.createElement("div");
		
		if(opt.id)
			tabButton.id = opt.id;
		
		tabButton.className = opt.className;
		tabButton.title = opt.title;
		
		var openDiv = document.createElement("div");
		var openImg = document.createElement("img");
		
		openImg.src = imageURL + opt.image + "_"
			+ IS_Portal.lang + ((IS_Portal.country == "") ? "" : "_" + IS_Portal.country) + ".gif";
		openImg.onerror = function(){
			openImg.src = imageURL + opt.image + "_" + IS_Portal.lang + ".gif";
			
			openImg.onerror = function(){
				openImg.src = imageURL + opt.image + "_en.gif";
			};
		};
		
		openDiv.appendChild( openImg );
		tabButton.appendChild( openDiv );
		
		return tabButton;
	}
	
	if( Browser.isSafari1 ) {
		var initialize = this.initialize;
		
		this.initialize = function() {
			initialize.apply( this,arguments );
			
			treeOpen.style.overflow = "hidden";
			treeOpen.appendChild( IS_Widget.RssReader.RssItemRender.createTable(1,1) );
		}
	}
	
	var tabs = {};
	function addTab( id,opt ) {
		treeOpen.appendChild( opt.open );
		treeOpen.appendChild( opt.close );
		
		opt.close.style.display = "none";
		
		Event.observe( opt.open, "mousedown", function(e){ showTreeMenu(e,id )});
		Event.observe( opt.close, "mousedown", function(e){ hideTreeMenu(e, id )});
		
		tabs[id] = opt;
	}
	
	function showTreeMenu(e, tabName) {
		if(e && e.stopPropagation)e.stopPropagation();
		if(window.event) window.event.cancelBubble = true;
		
		IS_SidePanel.onMove = true;
		
		/*
		siteMenu.style.width = (IS_SidePanel.currentWidth-1)+"px";
		$( container.id +"-dummy").style.width = container.style.width = (IS_SidePanel.currentWidth-1)+"px";
		*/
		siteMenu.style.width = (IS_SidePanel.currentWidth)+"px";
		$( container.id +"-dummy").style.width = container.style.width = (IS_SidePanel.currentWidth)+"px";
		
		IS_SidePanel.siteMapOpened = true;
		
		var tab = tabs[ tabName ];
		if( !tab ) return;
		
		$H( tabs ).each( function( t ) {
			if( t.key == tabName ) return;
			
			t.value.open.style.display = "block";
			t.value.close.style.display = "none";
			
			if( t.value.onClose )
				t.value.onClose();
		});
		tab.open.style.display = "none";
		tab.close.style.display = "block";
		
		if( tab.onOpen )
			tab.onOpen();
		
		container.style.display = "block";
		
//		container.style.overflow = "visble";
		
		adjustSiteMap();
		IS_SidePanel.currentWidth =siteMenu.offsetWidth;
		IS_SidePanel.onMove = false;
		
		IS_EventDispatcher.newEvent('adjustedSiteMap');
		
		treeOpen.style.cursor = ( !Browser.isSafari1? "col-resize" : "e-resize" );
		treeOpen.title = IS_R.ms_customizeWidthByDrag;
		IS_EventDispatcher.newEvent('openSiteMap', "portal-tree-menu", null);
	}
	
	function hideTreeMenu(e, tabName) {
		if(e && e.stopPropagation)e.stopPropagation();
		if(window.event) window.event.cancelBubble = true;
		
		IS_SidePanel.onMove = true;
		container.style.display = "none";
		
		var panelsDiv = document.getElementById("panels");
		panelsDiv.style.marginLeft = 0;
		
		siteMenu.style.width = "0px";
		container.style.width = "0px";
		$( container.id+"-dummy").style.width = "0px";
		IS_SidePanel.siteMapOpened = false;
		
		$H( tabs ).each( function( t ) {
			t.value.open.style.display = "block";
			t.value.close.style.display = "none";
			
			if( t.value.onClose )
				t.value.onClose();
		});
		
//		container.style.overflow = "visible";
		
		adjustSiteMap();
		IS_SidePanel.onMove = false;
		
		IS_EventDispatcher.newEvent('adjustedSiteMap');
		
		treeOpen.style.cursor = "";
		treeOpen.title = "";
		
		IS_EventDispatcher.newEvent('closeSiteMap', "portal-side-menu", null);
	}
	
	function adjustSiteMap() {
		var divIFrame = $("portal-iframe");
		if(divIFrame.style.display == "none"){
//			IS_WidgetsContainer.adjustColumnWidth();
			IS_Portal.widgetDisplayUpdated();
			IS_Widget.adjustDescWidth();
			IS_Widget.Information2.adjustDescWidth();
			if(IS_Widget.MaximizeWidget != null){
				IS_Widget.Maximize.adjustMaximizeWidth();
			}
			IS_Portal.adjustGadgetHeight();
		}
		IS_Widget.Ticker.adjustTickerWidth();
		
		IS_SidePanel.adjustPosition();
	}
	
	IS_SidePanel.adjustPosition = function() {
		if(Browser.isIE) {//#2713 Some Flash can not be displayed properly if the window is scrolled.
			if(IS_SidePanel.overlayTimer) clearTimeout(IS_SidePanel.overlayTimer);
			else IS_Portal.getIfrmOverlay().show();
			IS_SidePanel.overlayTimer = setTimeout(function(){
				IS_Portal.getIfrmOverlay().hide();
				IS_SidePanel.overlayTimer = false;
			}, 100);
		}
		var y = findPosY( container.parentNode ) -document.body.scrollTop;
		if( y < 0 )
			y = 0;
		
		var height = getWindowHeight() -y;
		if( !isNaN( height ) && height >= 0 )
			treeOpen.style.height = container.style.height = height;
		
		var scrollbarOffset = ( height < container.firstChild.offsetHeight ? 18:0 );
		var width = container.parentNode.offsetWidth -scrollbarOffset - (Browser.isFirefox ? 15 : 0);
		if( width > 0 )
			treeOpen.left = container.firstChild.style.width = width +1;
		
		y += document.body.scrollTop;
		treeOpen.style.top = container.style.top = y;
	}
};

IS_SidePanel.sideMenuAdjusted = function(e){
	var treeOpen = document.getElementById("siteMenuOpen");
	IS_SidePanel.nowWidth = parseInt(findPosX(treeOpen));
	IS_SidePanel.currentWidth = parseInt(findPosX(treeOpen));
	var container = document.getElementById("portal-tree-menucontainer");
//	container.style.overflow = "visible";
	
	var divIFrame = $("portal-iframe");
	if(divIFrame.style.display == "none"){
//		IS_WidgetsContainer.adjustColumnWidth();
		IS_Portal.widgetDisplayUpdated();
		IS_Widget.adjustDescWidth();
		IS_Widget.Information2.adjustDescWidth();
		if(IS_Widget.MaximizeWidget != null){
			IS_Widget.Maximize.adjustMaximizeWidth();
		}
		IS_Portal.adjustGadgetHeight();
	}
	IS_Widget.Ticker.adjustTickerWidth();
	IS_SidePanel.onMove = false;
	
//	IS_EventDispatcher.newEvent('adjustedSiteMap', "portal-tree-menu",null );

	IS_SidePanel.adjustPosition();

	IS_EventDispatcher.newEvent('adjustedSiteMap');
}

IS_SidePanel.siteMapOpened = false;
IS_SidePanel.onMove;
IS_SidePanel.nowWidth;
IS_SidePanel.defaultWidth = (Browser.isIE)? 200:180;	//Initial width
IS_SidePanel.currentWidth = IS_SidePanel.defaultWidth;
IS_SidePanel.Drag = new Object();
IS_SidePanel.Drag.barGhost = document.createElement("div");
IS_SidePanel.Drag.barGhost.id = "siteMenuGhost";

IS_SidePanel.Drag.dragStart = function(e) {
	if(!IS_SidePanel.siteMapOpened) return;
	
//	var bar = document.getElementById("siteMenuOpen").parentNode;
	var bar = document.getElementById("siteMenuOpen").parentNode;
	var barGhost = IS_SidePanel.Drag.barGhost;
	
	IS_Portal.showDragOverlay(Element.getStyle(bar, "cursor"));
	
	// init ghost
	if(Browser.isIE){
		bar.appendChild(barGhost);
	}else{
		document.body.appendChild(barGhost);
	}
	barGhost.innerHTML = "";
	barGhost.style.height = ( !Browser.isSafari1 ? bar.offsetHeight : bar.style.height );
	barGhost.style.width = bar.offsetWidth;
	barGhost.style.top = findPosY(bar);
	barGhost.style.left = findPosX(bar);
	
	barGhost.innerHTML = bar.firstChild.innerHTML;
	barGhost.style.filter = "alpha(opacity=50)";
	barGhost.style.opacity = 0.5;
	
	Event.observe(document, "mousemove", IS_SidePanel.Drag.dragging, false);
	Event.observe(document, "mouseup", IS_SidePanel.Drag.dragEnd, false);

	if (!Browser.isIE) { 
		// Prevent from transmitting event to upper class.
		e.preventDefault(); 
		e.stopPropagation(); 
	} else { 
		// Prevent from transmitting event to upper class.
		event.returnValue = false; 
		event.cancelBubble = true; 
	} 

};

IS_SidePanel.Drag.dragging = function(e) {
	var mousex = Event.pointerX(e);
	
	IS_SidePanel.Drag.barGhost.style.left = mousex - 6;
}

IS_SidePanel.Drag.dragEnd = function(e) {
	Event.stopObserving(document, "mousemove", IS_SidePanel.Drag.dragging, false);
	Event.stopObserving(document, "mouseup", IS_SidePanel.Drag.dragEnd, false);
	
	var barGhost = IS_SidePanel.Drag.barGhost;
	
	var list = document.getElementById("siteMenu");
	var listX = findPosX(list);
	var barGhostX = findPosX(barGhost);
	var nowWidth = (barGhostX-listX > 0)? (barGhostX-listX) : 1;
	
	// Do not make narrowwer than default.
	if(nowWidth < IS_SidePanel.defaultWidth){
		nowWidth = IS_SidePanel.defaultWidth;
	}
	list.style.width = nowWidth;
	
	var container = $("portal-tree-menucontainer");
	if(container) $( container.id+"-dummy").style.width = container.style.width = nowWidth;
	
	IS_SidePanel.currentWidth = nowWidth;
	
	var bar = document.getElementById("siteMenuOpen");
//	IS_SidePanel.Drag.barGhost.innerHTML = "";
	IS_SidePanel.Drag.barGhost.parentNode.removeChild(IS_SidePanel.Drag.barGhost);

	IS_Portal.hideDragOverlay();
	IS_SidePanel.sideMenuAdjusted();
}
