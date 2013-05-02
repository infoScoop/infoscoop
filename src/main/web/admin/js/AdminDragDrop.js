var ISA_DragDrop = new Object();
ISA_DragDrop.draggableList = [];
ISA_DragDrop.SiteAggregationMenu = IS_Class.create();
ISA_DragDrop.SiteAggregationMenu.prototype.classDef = function() {
	var self = this;
	var dragId;
	var dropElement;
	var optDropzone;
	
	this.initialize = function(menuObj, menuItem, noDraggable) {
		this.menuObj = menuObj;
		this.menuItem = menuItem;
		this.noDraggable = noDraggable;
		dragId = "tc_" + this.menuItem.id;
		dropElement = "tc_" + this.menuItem.id;
		this.dropElement = dropElement;
		
		// Disable to drag if not change order mode and topmenu is selected
		this.noDraggable = (this.noDraggable)?
			true : (!menuObj.isOrderEditMode && isSiteTop(menuItem.id));
		
		if(!this.noDraggable){
			this.draggable = new Draggable(dragId, {
				// Restore after moving or not. true:Restore, false:Not restore
				revert:false,
				// A function called at start dragging
				starteffect: function(el) {
				},
				constraint:'vertical',
				ghosting: true,
				// A function called at finish dragging
				endeffect: function(element) {
					// Below is initial diplay setting as position=relative
					element.style.left = 0;
					element.style.top = 0;
					element.style.position = 'relative';
					//fix #478
//					new Effect.Highlight(element, {duration:3});
				}
			});
			ISA_DragDrop.draggableList.push(this.draggable);
		}
		
		optDropzone = {
			onDrop: dropProcess,
			onHover: hoveringProcess.bind(this)
		};
		
		if(this.menuItem.depth > 0){
			optDropzone.accept = 'siteChild';
		}else{
			//optDropzone.accept = 'siteTop';
		}
		
		Droppables.add(dropElement, optDropzone);
	};
	
	// Update function
	Droppables.deactivate = function(drop) {
		drop.element.className = drop.element.orgClassName;
		this.last_active = null;
	}
	
	function isSiteTop(id) {
		if(id == "dummy_top") return true;
		
		for(var i = 0; i < ISA_SiteAggregationMenu.topMenuIdList.length; i++){
			if(ISA_SiteAggregationMenu.topMenuIdList[i] == id)
				return true;
		}
		return false;
	}
	
	function hoveringProcess(dragItem, dropItem, overlap){
		// Position of dropItem
		var dropLeftx = findPosX(dropItem);
		var dropTopy = findPosY(dropItem);
		var dropRightX = dropLeftx + dropItem.offsetWidth;
		var dropBottomY = dropTopy + dropItem.offsetHeight;
		
		// Position of dragItem
		var dragx = findPosX(dragItem);
		var dragy = findPosY(dragItem);
		
		if(!dropItem.orgClassName)dropItem.orgClassName = dropItem.className;
		
		if(isSiteTop(dragItem.id.substring(3)) || dropTopy+5 > dragy && dropTopy > dragy ){
			// Case of moving to top
			dropItem.className = "hoverDragging_move";
			dropItem.endProcessMode = "move";
		}else{
			if($(dropItem.id.replace(/^tc_/,"t_")).className != "treeMenuTitle_edit"){ 
				dropItem.endProcessMode = "append";
				return; 
			}
		
			// More case
			dropItem.className = "hoverDragging_append";
			dropItem.endProcessMode = "append";
		}
	}
	
	function dropProcess(draggable, dropElement) {
		if(dropElement.endProcessMode=="append" && $(dropElement.id.replace(/^tc_/,"t_")).className != "treeMenuTitle_edit"){ return; }
		
		ISA_Admin.isUpdated = true;
		// Deleting line
		var endProcessMode = dropElement.endProcessMode;
		
		var dragNode = draggable.parentNode.parentNode.parentNode.parentNode.parentNode;
		var dropNode = dropElement.parentNode.parentNode.parentNode.parentNode.parentNode;
		
		var dropParentNode = dropNode.parentNode;
		// Can not drop from site-top to site
		/*
		if(isSiteTop(dragNode.id) && !isSiteTop(dropNode.id)){
			return;
		}
		*/
		// Can not drop from site-top to site
		/*
		if(!isSiteTop(dragNode.id) && isSiteTop(dropNode.id)){
			return;
		}
		*/
		// Parent menu can not be dropped to child menu of itself
		var tempDropParentNode = dropParentNode;
		while(tempDropParentNode){
			if(("tg_" + dragNode.id) == tempDropParentNode.id){
				return;
			}
			tempDropParentNode = tempDropParentNode.parentNode;
		}

		// Element at the last or not
		var dummyNode = false;
		var dragItem = ISA_SiteAggregationMenu.menuItemList[dragNode.id];
		var dropItem = ISA_SiteAggregationMenu.menuItemList[dropNode.id];
		if(!dropItem){
			dummyNode = dropNode;
			dropNode = dropNode.previousSibling;
			if(dropNode.id == dragItem.id)return;
			dropItem = ISA_SiteAggregationMenu.menuItemList[dropNode.id];
		}

		// Can not move from site to site-top
		if(endProcessMode == "move"){
			if(dropItem.depth == 0 && dragItem.depth != 0){
				alert(ISA_R.ams_notMoveMenuTree);
				return;
			}
		}

		// Obtain node to swap if it is at bottom 
		var nextLastNode = dropNode;
		var nextLastItem = dropItem;
		var brfore_nextLastItem_depth = dropItem.depth;
		var hasNextLastNode = true;
		if(dragItem.isLast){
			if(dragNode.previousSibling){
				nextLastNode = dragNode.previousSibling;
				nextLastItem = ISA_SiteAggregationMenu.menuItemList[nextLastNode.id];
				brfore_nextLastItem_depth = nextLastItem.depth;
			}else{
				// If the all node go outside and become empty
				var parentItem = ISA_SiteAggregationMenu.menuItemList[dragItem.parentId];
				var closeLineTd = $("i_"+parentItem.id);
				ISA_SiteAggregationMenu.treeMenu.subMenuClose(closeLineTd);

				$("i_"+parentItem.id).className = (parentItem.isLast)? "ygtvln" : "ygtvtn";
				
				var cloneTd = closeLineTd.cloneNode(true);
				
				closeLineTd.parentNode.replaceChild(cloneTd, $("i_"+parentItem.id));
				

				hasNextLastNode = false;
			}
		}
		
		// Expand if mode=append
		if(endProcessMode == "append"){
			var dropLineTd = $("i_" + dropItem.id);
			ISA_SiteAggregationMenu.treeMenu.subMenuOpen(dropLineTd, dropItem,
				(dropLineTd.className == 'ygtvln' || dropLineTd.className == 'ygtvtn'));
		}
		var beforeDepth;
		if(endProcessMode == "move"){
			// Update content of item
			beforeDepth = dragItem.depth;
			dragItem.depth = dropItem.depth;

			// Move
			if(dummyNode){	//ending
				// Update DB
				replaceSort(dragNode.id, dropItem.parentId, dummyNode.id);
				
				// Insert to the place of dropping ahead
				dropParentNode.insertBefore(dragNode, dummyNode);
			}else{
				replaceSort(dragNode.id, dropItem.parentId, dropNode.id);
				dropParentNode.insertBefore(dragNode, dropNode);
			}
			dragItem.parentId = dropItem.parentId;
		}else{
			// Update content of item
			beforeDepth = dragItem.depth;
			dragItem.depth = dropItem.depth+1;
			
			replaceSort(dragNode.id, dropItem.id, null);
			dragItem.parentId = dropItem.id;
			
			// Case of adding
			if(dropItem.id){
				menuGroup = $('tg_' + dropItem.id);
			}else{
				menuGroup = $('ygtvc0');
			}
			// Turn back isLast flag of belonging child
			var childNodes = menuGroup.childNodes;
			if(childNodes.length > 1){
				var childItem = ISA_SiteAggregationMenu.menuItemList[childNodes[childNodes.length-2].id];
				if(childItem){
					var lineTd = $('i_' + childItem.id);
					ISA_DragDrop.changeLastLineState(lineTd, childItem);
				}
			}
			
			// Always comes at bottom
			var dummy = $("dummy_" + dropItem.id);
			menuGroup.insertBefore(dragNode, dummy);
		}
		
		if(dragItem.isLast && ((dropItem.isLast && dummyNode) || endProcessMode == "append")){
			// Change if the next place to be inserted is not last. Leave alonge if it is last.
			if(!((dropItem.isLast && dummyNode) || endProcessMode == "append")){
				var dragLineTd = $('i_' + dragNode.id);
				ISA_DragDrop.changeLastLineState(dragLineTd, dragItem);
			}
			
			if(hasNextLastNode){
				nextLastItem = ISA_SiteAggregationMenu.menuItemList[nextLastNode.id];
				var nextLastLineTd = $('i_' + nextLastNode.id);
				ISA_DragDrop.changeLastLineState(nextLastLineTd, nextLastItem);
			}
			
			if(nextLastNode.id != dropNode.id && (dragItem.isLast && endProcessMode == "move")){
				var dropLineTd = $('i_' + dropNode.id);
				ISA_DragDrop.changeLastLineState(dropLineTd, dropItem);
			}
		}
		else if(dragItem.isLast){
			var dragLineTd = $('i_' + dragNode.id);
			ISA_DragDrop.changeLastLineState(dragLineTd, dragItem);
			
			nextLastItem = ISA_SiteAggregationMenu.menuItemList[nextLastNode.id];
			var nextLastLineTd = $('i_' + nextLastNode.id);
			// Prevent from hiding the line in dropNode if it moves to parent level
			if(hasNextLastNode)
				ISA_DragDrop.changeLastLineState(nextLastLineTd, nextLastItem);
		}else if((dropItem.isLast && dummyNode) || endProcessMode == "append"){
			var dragLineTd = $('i_' + dragNode.id);
			ISA_DragDrop.changeLastLineState(dragLineTd, dragItem);
			
			if(endProcessMode != "append"){
				var dropLineTd = $('i_' + dropNode.id);
				ISA_DragDrop.changeLastLineState(dropLineTd, dropItem);
			}
		}else if(dropItem.isLast){
			//do nothing
		}
		
		//Processing the line of parent
		updateLine(dragItem, dragNode);
		
		// Redraw the all line in expanded child node(Dragging side)
		var tds = dragNode.getElementsByTagName('td');
		var childMenuItemList = new Array();
		var childItemNodeList = new Array();
//		if(endProcessMode == "move"){
			for(var i=0;i<tds.length;i++){
				if(tds[i] && (tds[i].className == "ygtvblankdepthcell" || tds[i].className == "ygtvdepthcell")){
					var itemNode = tds[i].parentNode.parentNode.parentNode.parentNode;
					var childMenuItem = ISA_SiteAggregationMenu.menuItemList[itemNode.id];
					if(!childMenuItem) childMenuItem = ISA_SiteAggregationMenu.menuDummyItemList[itemNode.id];
					if(childMenuItem && childMenuItem.id != dragItem.id){
						childMenuItemList[childMenuItem.id] = childMenuItem;
						childItemNodeList[childMenuItem.id] = itemNode;
					}
				}
			}
			for(var i in childMenuItemList){
				if(childMenuItemList[i].id){
					var addDp = childMenuItemList[i].depth - beforeDepth;
					childMenuItemList[i].depth = dragItem.depth + addDp;
					updateLine(childMenuItemList[i], childItemNodeList[i]);
				}
			}
//		}
		
		// Redraw the all line in expanded child node(Last node as a result of dragging)
		if(nextLastNode){
			tds = nextLastNode.getElementsByTagName('td');
			childMenuItemList = new Array();
			childItemNodeList = new Array();
			for(var i=0;i<tds.length;i++){
				if(tds[i] && (tds[i].className == "ygtvblankdepthcell" || tds[i].className == "ygtvdepthcell")){
					var itemNode = tds[i].parentNode.parentNode.parentNode.parentNode;
					var childMenuItem = ISA_SiteAggregationMenu.menuItemList[itemNode.id];
					if(!childMenuItem) childMenuItem = ISA_SiteAggregationMenu.menuDummyItemList[itemNode.id];
					if((childMenuItem && childMenuItem.id != nextLastItem.id) || endProcessMode == "append"){
						childMenuItemList[childMenuItem.id] = childMenuItem;
						childItemNodeList[childMenuItem.id] = itemNode;
					}
				}
			}
			for(var i in childMenuItemList){
				if(childMenuItemList[i].id){
					var addDp = childMenuItemList[i].depth - brfore_nextLastItem_depth;
					childMenuItemList[i].depth = nextLastItem.depth + addDp;
					updateLine(childMenuItemList[i], childItemNodeList[i]);
				}
			}
		}
		
		// Redraw the all line in expanded child node(Dropping side)
		tds = dropNode.getElementsByTagName('td');
		childMenuItemList = new Array();
		childItemNodeList = new Array();
		for(var i=0;i<tds.length;i++){
			if(tds[i] && (tds[i].className == "ygtvblankdepthcell" || tds[i].className == "ygtvdepthcell")){
				var itemNode = tds[i].parentNode.parentNode.parentNode.parentNode;
				var childMenuItem = ISA_SiteAggregationMenu.menuItemList[itemNode.id];
				if(!childMenuItem) childMenuItem = ISA_SiteAggregationMenu.menuDummyItemList[itemNode.id];
				if((childMenuItem && childMenuItem.id != nextLastItem.id) || endProcessMode == "append"){
					childMenuItemList[childMenuItem.id] = childMenuItem;
					childItemNodeList[childMenuItem.id] = itemNode;
				}
			}
		}
		for(var i in childMenuItemList){
			if(childMenuItemList[i].id){
				var addDp = childMenuItemList[i].depth - brfore_nextLastItem_depth;
				childMenuItemList[i].depth = nextLastItem.depth + addDp;
				updateLine(childMenuItemList[i], childItemNodeList[i]);
			}
		}
		
		/**
		 * draw line again
		 * @param {Object} dropItem
		 * @param {Object} dragNode
		 */
		function updateLine(dropItem, dragNode){
			//Processing parent line
			var parents = [dropItem.depth];
			var parentMenu = dropItem;
			for(var i = dropItem.depth; i > 0; i--){
				parentMenu = ISA_SiteAggregationMenu.menuItemList[parentMenu.parentId];
				parents[i-1] = parentMenu;
			}
			
			var tds = dragNode.firstChild.getElementsByTagName('td');
			var lintTd;
			var removeTds = [];
			for(var i = 0; i < tds.length; i++){
				if(tds[i].className == 'ygtvblankdepthcell' || tds[i].className == 'ygtvdepthcell'){
					menuTr = tds[i].parentNode;
					removeTds.push(tds[i]);
				}
			}
			
			for(var i = 0 ; i < removeTds.length; i++){
				removeTds[i].parentNode.removeChild(removeTds[i]);
			}
	
			if(dropItem.depth > 0){
				var dragLineTd = menuTr.firstChild;
				for(var i = 0; i < dropItem.depth; i++){
					var depthTd = document.createElement('td');
					if(parents[i].isLast){

						depthTd.className = 'ygtvblankdepthcell';
					}else{
						depthTd.className = 'ygtvdepthcell';
					}
					menuTr.insertBefore(depthTd, dragLineTd);
					var depthDiv = document.createElement('div');
					depthDiv.className = 'ygtvspacer';
					depthTd.appendChild(depthDiv);
				}
			}
		}
	}

	function replaceTopSort(id, siblingId) {
		var url = adminHostPrefix + "/services/menu/replaceTopOrder";
		
		var menuItem = ISA_SiteAggregationMenu.menuItemList[id];
		
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			postBody: Object.toJSON([
				self.menuObj.menuType,
				id,
				siblingId ? siblingId: "null"
			]),
			asynchronous:true,
			onSuccess: function(response){
				self.menuObj.isUpdated = true;

				ISA_SiteAggregationMenu.topMenuIdList.splice(ISA_SiteAggregationMenu.topMenuIdList.indexOf(id),1);
				var siblingIndex = (siblingId=='dummy_top') ? ISA_SiteAggregationMenu.topMenuIdList.length : ISA_SiteAggregationMenu.topMenuIdList.indexOf(siblingId);
				ISA_SiteAggregationMenu.topMenuIdList.splice(siblingIndex,0,id);
			},
			onFailure: function(t) {
				var resTxt = ISA_SiteAggregationMenu.getErrorMessage(t);
				alert(ISA_R.ams_failedReplaceMenu + resTxt);
				msg.error(ISA_R.ams_failedReplaceMenu + t.status + " - " + t.statusText + " " + resTxt);
			},
			onException: function(r, t){
				alert(ISA_R.ams_failedReplaceMenu);
				msg.error(ISA_R.ams_failedReplaceMenu + getErrorMessage(t));
			}
		};
		AjaxRequest.invoke(url, opt);
	}
	
	function replaceSort(id, parentId, siblingId) {
		if(isSiteTop(id)){
			replaceTopSort(id, siblingId);
			return;
		}
		
		var url = adminHostPrefix + "/services/menu/replaceOrder";
		
		var menuItem = ISA_SiteAggregationMenu.menuItemList[id];
		var parentMenuItem = ISA_SiteAggregationMenu.menuItemList[parentId];
		var fromSitetopId = ISA_SiteAggregationMenu.getSitetopId(menuItem);
		var toSitetopId = ISA_SiteAggregationMenu.getSitetopId(parentMenuItem);
		
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			postBody: Object.toJSON([
				self.menuObj.menuType,
				id,
				parentId,
				siblingId ? siblingId: "null",
				fromSitetopId,
				toSitetopId
			]),
			asynchronous:true,
			onSuccess: function(response){
				self.menuObj.isUpdated = true;
			}.bind(this),
			onFailure: function(t) {
				var resTxt = ISA_SiteAggregationMenu.getErrorMessage(t);
				alert(ISA_R.ams_failedReplaceMenu + resTxt);
				msg.error(ISA_R.ams_failedReplaceMenu + t.status + " - " + t.statusText + " " + resTxt);
			},
			onException: function(r, t){
				alert(ISA_R.ams_failedReplaceMenu);
				msg.error(ISA_R.ams_failedReplaceMenu + getErrorMessage(t));
			}
		};
		AjaxRequest.invoke(url, opt);
	}
};
/**
 * Invert the lines; line at the bottom and the other
 * @param {Object} element
 * @param {Object} menuItem
 */
ISA_DragDrop.changeLastLineState = function(element, menuItem) {
	if(element.className == 'ygtvlp'){
		menuItem.isLast = false;
		element.className = 'ygtvtp';
	}
	else if(element.className == "ygtvlm"){
		menuItem.isLast = false;
		element.className = "ygtvtm"
	}
	else if(element.className == 'ygtvln'){
		menuItem.isLast = false;
		element.className = 'ygtvtn';
	}
	else if(element.className == 'ygtvtp'){
		menuItem.isLast = true;
		element.className = 'ygtvlp';
	}
	else if(element.className == 'ygtvtn'){
		menuItem.isLast = true;
		element.className = 'ygtvln';
	}
	else if(element.className == "ygtvtm"){
		menuItem.isLast = true;
		element.className = "ygtvlm"
	}
}

ISA_DragDrop.destroy = function(menuId){
	ISA_DragDrop.destroyDragEvent(menuId);
	ISA_DragDrop.destroyDropEvent(menuId);
}

ISA_DragDrop.destroyDragEvent = function(menuId){
	for(var i=0;i<ISA_DragDrop.draggableList.length;i++){
		var draggable = ISA_DragDrop.draggableList[i];
		if(draggable.element.id == "tc_" + menuId || draggable.element.id == "tc_dummy_" + menuId){
			ISA_DragDrop.draggableList[i].destroy();
			ISA_DragDrop.draggableList.splice(i,1);
		}
	}
}

ISA_DragDrop.destroyDropEvent = function(menuId){
	var loopCount = Droppables.drops.length;
	for(var i=0;i<loopCount;i++){
		var droppable = Droppables.drops[i];
		if (droppable && ("tc_" + menuId == droppable.element.id)) {
			Droppables.remove(Droppables.drops[i].element);
		}
	}
}

ISA_DragDrop.SearchEngineDragDrop = IS_Class.create();
ISA_DragDrop.SearchEngineDragDrop.prototype.classDef = function() {
	var id;
	var nextsibling;
	
	this.initialize = function(searchEngineList) {
		Sortable.create(searchEngineList,
			{
				tag: 'div',
				handle: 'handle',
				starteffect: function(div) {
					// opacity effect drag start
					div.style.opacity = 0.7;
					div.style.filter = 'alpha(opacity=70)';
				},
				onChange: function(div){
					id = div.firstChild.id;
					nextsibling = (div.nextSibling) ? div.nextSibling.firstChild.id : "";
				},
				onUpdate:function(){
					replaceSort(id, nextsibling);
				}
			}
		);
	};
	
	// Overwrite function
	Droppables.deactivate = function(drop) {
		// Because it is overwritten by SiteAggregationMenuDragDrop, make it not to affect other....
//		if(drop.hoverclass)
//			Element.removeClassName(drop.element, drop.hoverclass);
		this.last_active = null;
	}
	
	function replaceSort(id, siblingid) {
		var url = adminHostPrefix + "/services/searchEngine/replaceSort";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			postBody: Object.toJSON([id,siblingid]),
			asynchronous:true,
			onSuccess: function(response){
				ISA_Admin.isUpdated = true;
			},
			onFailure: function(t) {
				alert(ISA_R.ams_failedReplaceSearchEngine);
				msg.error(ISA_R.ams_failedReplaceSearchEngine + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				alert(ISA_R.ams_failedReplaceSearchEngine);
				msg.error(ISA_R.ams_failedReplaceSearchEngine + getErrorMessage(t));
			}
		};
		AjaxRequest.invoke(url, opt);
	}
};

ISA_DragDrop.ProxyConfigDragDrop = IS_Class.create();
ISA_DragDrop.ProxyConfigDragDrop.prototype.classDef = function() {
	var self = this;
	var id;
	var nextsibling;
	
	this.initialize = function(proxyConfigList) {
		if(Browser.isIE && !self.proxyCheckMap){
			//fix1212 Dealing with the problem that unckeck checkbox at sortng in IE
			self.proxyCheckMap = {id:new Date().getTime()};
			var proxyDivs = $("caseProxyConfigList").childNodes;
			for(var i=0;i<proxyDivs.length;i++){
				var proxyId = proxyDivs[i].id.substring(4);
				var proxyCheckbox = $("typ_" + proxyId).previousSibling;
				if(proxyCheckbox)
					self.proxyCheckMap[proxyId] = proxyCheckbox.checked;
			}
		}
		Sortable.create(proxyConfigList,
			{
				tag: 'div',
				handle: 'handle',
				starteffect: function(div) {
					// opacity effect drag start
					div.style.opacity = 0.7;
					div.style.filter = 'alpha(opacity=70)';
				},
				onChange: function(div){
					id = div.firstChild.id;
					nextsibling = (div.nextSibling) ? div.nextSibling.firstChild.id : "";
				},
				onUpdate:function(div){
					console.log("test")
					if(self.proxyCheckMap){
						var proxyCheckbox = $("typ_" + id).previousSibling;
						if(proxyCheckbox)
							proxyCheckbox.checked = self.proxyCheckMap[id];
					}
					replaceSort(id, nextsibling);
				}
			}
		);
	};
	
	// Update function
	Droppables.deactivate = function(drop) {
		// Because it is overwritten by SiteAggregationMenuDragDrop, make it not to affect other....
//		if(drop.hoverclass)
//			Element.removeClassName(drop.element, drop.hoverclass);
		this.last_active = null;
	}
	
	function replaceSort(id, siblingid) {
		var url = adminHostPrefix + "/services/proxyConf/replaceSort";
		var opt = {
			method: 'post' ,
			contentType: "application/json",
			postBody: Object.toJSON([id,siblingid]),
			asynchronous:true,
			onSuccess: function(response){
				ISA_Admin.isUpdated = true;
				
			},
			onFailure: function(t) {
				alert(ISA_R.ams_failedReplaceProxySettings);
				msg.error(ISA_R.ams_failedReplaceProxySettings + t.status + " - " + t.statusText);
			},
			onException: function(r, t){
				alert(ISA_R.ams_failedReplaceProxySettings);
				msg.error(ISA_R.ams_failedReplaceProxySettings + getErrorMessage(t));
			}
		};
		AjaxRequest.invoke(url, opt);
	}
};
