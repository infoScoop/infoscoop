var openerPanel = ISA_DefaultPanel.defaultPanel;

var areaType = 0;
var jsonRole;

IS_SidePanel.adjustPosition = function(){};
IS_Request.CommandQueue = {
	addCommand: function(){}
}

var msg = opener.msg;

// override 
IS_Portal.addTab = function(idNumber, name, type, numCol, columnsWidth, disabledDynamicPanel, isInitialize){
	IS_Portal.widgetLists["tab"+idNumber] = new Object();
	IS_Portal.columnsObjs["tab"+idNumber] = {};
//		IS_Portal.tabs["tab"+idNumber] = {"numCol":0,type:"static",adjustStaticHeight:adjustStaticHeight};
	IS_Portal.tabs["tab"+idNumber] = {"numCol":0,type:"static"};
	var panels = $("panels");
	if(panels){
		var panelDiv = IS_Portal.buildPanel( idNumber, type );
		panelDiv.style.display = "";
		panels.appendChild( panelDiv );
		IS_Portal.tabs["tab"+idNumber].panel = panelDiv;
		IS_WidgetsContainer.rebuildColumns("tab"+idNumber, numCol, columnsWidth, false, isInitialize);
	}
	adjustStaticWidgetHeight();
	prepareStaticArea();
}

// override 
IS_Portal.addWidget = function(widget, tabId){
	if(!tabId) tabId = IS_Portal.currentTabId;
	var widgetId = IS_Portal.getTrueId(widget.id);
	
	IS_Portal.widgetLists[tabId][widgetId] = widget;
	
	if(widget.widgetConf.parentId)
		widget.draggable = false;
	
	var widgetConf  = IS_WidgetConfiguration[widget.widgetType];
	if(widgetConf && widgetConf.Header){
		widgetConf.Header.icon = [{
			"alt": "close",
		    "imgUrl": "x.gif",
		    "staticDisabled": "true",
		    "type": "close"
		}];
		widgetConf.Header.refresh = "off";
		widgetConf.Header.minimize = "off";
		widgetConf.Header.maximize = "off";
		widgetConf.Header.disableMenu = true;
	}
	IS_EventDispatcher.addListener("closeWidget", widget.id.substring(2), saveDynamicPanel, true);
}

// override
IS_Portal.isChecked = function(menuItem){
	var isChecked = false;
	
	for(var tabId in IS_Portal.widgetLists){
		var widgetList = IS_Portal.widgetLists[tabId];
		for(var i in widgetList){
			if(!widgetList[i] || !widgetList[i].id) continue;
			
			if (/MultiRssReader/.test(widgetList[i].widgetType)) {
				if(!widgetList[i].isBuilt){
					// Judge subWidget by refering inside the feed if not build yet.
					var feed = widgetList[i].widgetConf.feed;
					for(var j in feed){
						var check = (feed[j].id && (feed[j].id.substring(2) == menuItem.id)
								&& (feed[j].property.relationalId != IS_Portal.getTrueId(widgetList[i].id) || feed[j].isChecked));
						if(/true/i.test(check)){
							isChecked = true;
							break;
						}
					}
				}
			}else{
				if(widgetList[i].id.substring(2) == menuItem.id){
					isChecked = true;
					break;
				}
			}
		}
	}
	return isChecked;
}

// override
IS_Widget.RssReader.dropGroup.add = function(){};

// override
IS_Widget.Message.checkNewMsg = function(){};

IS_Customization = {"commandbar":"<table cellpadding=\"0\" cellspacing=\"3px\" width=\"100%\">\r\n\t<tr>\r\n\t\t<td width=\"100%\"><div id=\"p_1_w_4\"><\/div><\/td>\r\n\t\t<td><div id=\"p_1_w_6\"><\/div><\/td>\r\n\t\t<td><div id=\"portal-go-home\"><\/div><\/td>\r\n\t\t<td><div id=\"disabled_portal-change-fontsize\" disabledCommand=\"true\"><!--&lt;div id=\"portal-change-fontsize\"&gt;&lt;/div&gt;--><\/div><\/td>\r\n\t\t<td><div id=\"portal-trash\"><\/div><\/td>\r\n\t\t<td><div id=\"portal-preference\"><div class=\"allPreference\"><\/div><\/div><\/td>\r\n\t\t<td><div id=\"disabled_portal-credential-list\" disabledCommand=\"true\"><!--&lt;div id=\"portal-credential-list\"&gt;&lt;/div&gt;--><\/div><\/td>\r\n\t\t<td><div id=\"portal-admin-link\"><\/div><\/td>\r\n\t\t<td><div id=\"disabled_portal-logout\" disabledCommand=\"true\"><!--&lt;div id=\"portal-logout\"&gt;&lt;/div&gt;--><\/div><\/td>\r\n\t<\/tr>\r\n<\/table>\r\n","contentFooter":[{"type":"mail"},{"type":"message"}],"css":"/* Custom CSS code is described here.  */","header":"<table width=\"100%\" height=\"53px\" cellspacing=\"0\" cellpadding=\"0\" style=\"background:url(skin/imgs/head_blue.png)\">\r\n\t<tbody>\r\n\t\t<tr>\r\n\t\t\t<td><a href=\"javascript:void(0)\" onclick=\"javascript:IS_Portal.goHome();return false;\"><img src=\"skin/imgs/infoscoop.gif\" alt=\"infoScoop\" border=\"0\" style=\"margin:0 0 0 20px;\" height=\"45\"/><\/a>\r\n\t\t\t<\/td>\r\n\t\t\t<td>\r\n\t\t\t\t<form name=\"searchForm\" onsubmit=\"javascript:IS_Portal.SearchEngines.buildSearchTabs(document.getElementById('searchTextForm').value);return false;\">\r\n\t\t\t\t<div style=\"float:right;margin-right:5px\">\r\n\t\t\t\t\t<table>\r\n\t\t\t\t\t\t<tbody>\r\n\t\t\t\t\t\t\t<tr>\r\n\t\t\t\t\t\t\t\t<td colspan=\"2\" align=\"right\" style=\"font-size:80%;\">\r\n\t\t\t\t\t\t\t\t\tWelcome,admin-\r\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<\/td>\r\n\t\t\t\t\t\t\t<\/tr>\r\n\t\t\t\t\t\t\t<tr>\r\n\t\t\t\t\t\t\t\t<td>\r\n\t\t\t\t\t\t\t\t\t<input id=\"searchTextForm\" type=\"text\" style=\"width:200px;height:23px;float:left;\"/>\r\n\t\t\t\t\t\t\t\t\t<input type=\"submit\" value=\"Search\" style=\"padding:0 0.4em;\"/>\r\n\t\t\t\t\t\t\t\t\t<span id=\"editsearchoption\">Search options<\/span>\r\n\t\t\t\t\t\t\t\t<\/td>\r\n\t\t\t\t\t\t\t<\/tr>\r\n\t\t\t\t\t\t<\/tbody>\r\n\t\t\t\t\t<\/table>\r\n\t\t\t\t<\/div>\r\n\t\t\t\t<\/form>\r\n\t\t\t<\/td>\r\n\t\t<\/tr>\r\n\t<\/tbody>\r\n<\/table>","title":"infoScoop"};

IS_Portal.CommandBar = {
  init:function(){}
}

function isHidePanel(){
	return false;
}
// mock for search engines
IS_Portal.searchWidgetAndFeedNode = function(){};
IS_Portal.SearchEngines = {
	matchRssSearch : function(){return false;},
	loadConf : function(){}
};

IS_Portal.behindIframe = {
	init:function(){
		//if(!Browser.isIE)return;
		this.behindIframe = $(document.createElement('iframe'));
		this.behindIframe.border = 0 + 'px';
		this.behindIframe.style.margin = 0 + 'px';
		this.behindIframe.style.padding = 0 + 'px';
		this.behindIframe.id = "is_portal_behind_iframe";
		this.behindIframe.frameBorder = 0;
		this.behindIframe.style.position = "absolute";
		this.behindIframe.src = "./blank.html";
		document.getElementsByTagName('body')[0].appendChild(this.behindIframe);
		this.behindIframe.hide();
	},
	
	show:function(element){
		//if(!Browser.isIE)return;
		Position.prepare();
		var pos = Position.cumulativeOffset(element);
		this.behindIframe.style.top = pos[1] + "px";
		this.behindIframe.style.left = pos[0] + "px";
		this.behindIframe.style.width = element.offsetWidth + 'px';
		this.behindIframe.style.height = element.offsetHeight + 'px';
		if(element.style.zIndex)
			this.behindIframe.style.zIndex = element.style.zIndex -1;
		else
			this.behindIframe.style.zIndex = 0;
		this.behindIframe.show();
		
		this.current = element;
	},
	
	hide:function(){
		//if(!Browser.isIE)return;
		this.behindIframe.style.left = 0 + "px";
		this.behindIframe.style.top = 0 + "px";
		this.behindIframe.style.width = 0 + 'px';
		this.behindIframe.style.height = 0 + 'px';
		this.behindIframe.hide();
	}
};

IS_Portal.setMouseMoveTimer;
IS_Portal.setMouseMoveEvent = function(){
	if(IS_Portal.setMouseMoveTimer) clearTimeout(IS_Portal.setMouseMoveTimer);
	var execFunc = function(){
		IS_Portal.unsetMouseMoveEvent();
		
		var portalTable = $("portal-maincontents-table");
		IS_Event.observe(portalTable, 'mousemove', function(){
			IS_Portal.closeIS_PortalObjects();
			IS_Portal.unsetMouseMoveEvent();
		}, false, "_portalclose");
	};
	IS_Portal.setMouseMoveTimer = setTimeout(execFunc, 100);
};

IS_Portal.unsetMouseMoveEvent = function(){
	IS_Event.unloadCache("_portalclose");
};

IS_Portal.closeIS_PortalObjects = function(){
	if(Browser.isIE) IS_SiteAggregationMenu.closeMenu();
};

IS_forbiddenURLs = {};

// mock for msgbar
IS_Portal.closeMsgBar = function(){};

var ISA_Principals = window.opener.ISA_Principals;

function prepareStaticArea(){
	var tabId = IS_Portal.currentTabId.replace("tab","");
	
	if($jq('#staticAreaContainer .static_column').size() == 0){
		var modified = false;
		$jq('#staticAreaContainer .column[id]').each(function(j){
			modified = true;
			$jq(this).addClass("static_column");
		});
		
		if(modified)
			openerPanel.setNewValue("layout", $jq('#staticAreaContainer').html(), jsonRole.id);
	}
	
	$jq('#staticAreaContainer .static_column').each(function(j){
		var containerId = $jq(this).attr("id");
		div = $jq(this).data("containerId", $jq(this).attr("id"));
		
		var widgetJSON = jsonRole.staticPanel[containerId];
		if(!widgetJSON)
			widgetJSON = {type:"notAvailable", id: containerId};
		var editorFormObj =	new ISA_CommonModals.EditorForm(div.get(0), function(widgetJSON){
			
			var selectType = ISA_CommonModals.EditorForm.getSelectType();
			if( widgetJSON.type != selectType )
			widgetJSON.properties = {};

			var oldId = widgetJSON.id;
			widgetJSON.id = "w_"+new Date().getTime();
			widgetJSON.type = ISA_CommonModals.EditorForm.getSelectType();
			widgetJSON.properties = ISA_CommonModals.EditorForm.getProperty(widgetJSON);
			widgetJSON.ignoreHeader = ISA_CommonModals.EditorForm.isIgnoreHeader();
			if(!widgetJSON.ignoreHeader) is_deleteProperty(widgetJSON, "ignoreHeader"); //delete widgetJSON.ignoreHeader;
			widgetJSON.noBorder = ISA_CommonModals.EditorForm.isNoBorder();
			if(!widgetJSON.noBorder) is_deleteProperty(widgetJSON, "noBorder"); //delete widgetJSON.noBorder;

			widgetJSON.title = ISA_Admin.trim($("formTitle").value);
			widgetJSON.href =  $("formHref").value;

			//delete jsonRole.staticPanel[oldId];
			is_deleteProperty(jsonRole.staticPanel, oldId);
			jsonRole.staticPanel[widgetJSON.id] = widgetJSON;
			
 			// When converting the object of a parent window to a jsonString in IE8, it is necessary to perform a deep copy. Otherwise, "undifined" will be returned. 
			openerPanel.setNewValue("staticpanel", Object.toJSON($jq.extend(true,{},jsonRole.staticPanel)), jsonRole.id);
//			openerPanel.setNewValue("staticpanel", Object.toJSON(jsonRole.staticPanel), jsonRole.id);
			
			jsonRole.layout = jsonRole.layout.replace( escapeHTMLEntity( oldId ),widgetJSON.id );
			$jq("#" + oldId).attr("id", widgetJSON.id);
			$jq("#s_" + oldId).attr("id", "s_" + widgetJSON.id);
			
			displayStaticGadget(
				{
					id: widgetJSON.id,
					tabId : jsonRole.tabId,
					href : $("formHref").value,
					title : ISA_Admin.trim($("formTitle").value),
					siblingId :"",
					ignoreHeader : ISA_CommonModals.EditorForm.isIgnoreHeader(),
					noBorder : ISA_CommonModals.EditorForm.isNoBorder(),
					type : ISA_CommonModals.EditorForm.getSelectType(),
					property : $jq.extend(true,{}, widgetJSON.properties)
				});
				
			if( Control.Modal.current ) {
				Control.Modal.close();
			} else {
				Control.Modal.container.hide();
			}
		},{
			menuFieldSetLegend:ISA_R.alb_widgetHeaderSettings,
			setDefaultValue: false,
			disableMiniBrowserHeight: true,
			showIgnoreHeaderForm:true,
			showNoBorderForm:true,
			displayACLFieldSet:false,
			disableDisplayRadio:true,
			omitTypeList:['Ranking','Ticker','MultiRssReader']
		});
		
		var edit_cover = $jq("<div></div>")
			.attr("id", "edit_div_" + j)
			.addClass("edit_static_gadget")
			.hide()
			.click(function(e){
				return function(){
					editorFormObj.showEditorForm(e.value);
				}
			}({value:widgetJSON})).appendTo(div);
		div.mouseover(function(){
			var $this = $jq(this);
			edit_cover
				.text(($this.attr("id") == $this.data("containerId")) ? "New" : "Edit")
				.show();
		})
		.mouseout(function(){
			edit_cover.hide();
		});
	});
	$jq("#layout").val($jq("#staticAreaContainer").html());
};

function adjustStaticWidgetHeight(){
	if(areaType != 2) return;
	IS_Portal.adjustStaticWidgetHeight();
	var columns = $$("#staticAreaContainer .static_column");
	var windowHeight = getWindowSize(false) - findPosY($("staticAreaContainer")) - 36;
	for(var i =0; i < columns.length; i++){
		columns[i].style.height = windowHeight + "px";
	}
}

function init() {
	IS_Customization["staticPanel" + jsonRole.tabId] = "<DIV>\r\n\t<DIV style=\"FLOAT: left; WIDTH: 74.5%\">\r\n\t\t<DIV style=\"HEIGHT: 178px\">\r\n\t\t\t<DIV style=\"FLOAT: left; WIDTH: 100%; HEIGHT: 100%\">\r\n\t\t\t\t<DIV id=\"p_1_w_1\" class=\"static_column\" style=\"MARGIN-LEFT: 2px; HEIGHT: 170px\"><\/DIV>\r\n\t\t\t<\/DIV>\r\n\t\t<\/DIV>\r\n\t<\/DIV>\r\n\t<DIV style=\"FLOAT: right; WIDTH: 25%; HEIGHT: 178px\">\r\n\t\t<DIV id=\"p_1_w_5\" class=\"static_column\" style=\"MARGIN-LEFT: 2px; HEIGHT: 170px\"><\/DIV>\r\n\t<\/DIV>\r\n<\/DIV>\r\n<DIV style=\"CLEAR: both; display:none;\"/>\r\n";
	IS_Portal.behindIframe.init();
	
	areaType = jsonRole.disabledDynamicPanel ? 1 : 0;
	areaType = jsonRole.adjustToWindowHeight ? 2 : areaType;
	
	IS_Portal.currentTabId = "tab" + jsonRole.tabId;
	IS_Portal.trueTabId = "tab" + jsonRole.tabId;
	
	var principalMap = ISA_Principals.get();
	var principalLength = principalMap.length;

	for(var i = 0; i < principalLength; i++) {
		if(principalMap[i].type == jsonRole.principalType){
			$jq("#principalTypeDiv").text(principalMap[i].displayName);
			break;
		}
	}
	
	//init params
	$jq("#roleName").text(jsonRole.roleName);
	$jq("#role").text(jsonRole.role);
	
	$jq("#tabName").val(jsonRole.tabName)
		.change(function(e){
			var nowText = ISA_Admin.trim( this.value );
			if(nowText.length == 0) {
				this.value = $jq(this).data("beforeNameText");
				this.focus();
				return false;
			}
			var error = IS_Validator.validate(nowText, {maxBytes:256, label:ISA_R.alb_tabName});
			if(error){
				alert(error);
				this.select();
				return false;
			}
			openerPanel.setNewValue("tabName", nowText);
		})
		.focus(function(e){
			$jq(this).data("beforeNameText", this.value);
		});
	
	$jq("#areaType").val(areaType);
	$jq("#numberOfColumns")
		.val(String( jsonRole.columnsArray.length ))
		.change(function(){
			IS_WidgetsContainer.rebuildColumns(IS_Portal.currentTabId, parseInt(jQuery(this).val()));

			var colNumber = this.value;
			// A column width is the number of columns devided by 100
			var colWidth = parseInt(100 / colNumber * 10) / 10;
			var colArray = [];
			var colWidthSum = 0;
			for(var i = 1; i <= colNumber; i++) {
				var width = colWidth;
				// Fraction goes to the last column -> Because it must go to 100 if it is added
				// Subtract from 1000 because the width gets ten times, then devided by 10.
				if(i == colNumber) {
					width = 1000 - colWidthSum;
					width /= 10;
				}
				// Decuple and cast to integer to prevent from rounding error.
				colWidthSum += width * 10;
				colArray.push( String(width) + "%" );
			}
			
			jsonRole.columnsWidth = Object.toJSON(colArray);
			openerPanel.setColumnsArray(jsonRole);
			
			openerPanel.isUpdated = true;
			ISA_Admin.isUpdated = true;
			
			ISA_DefaultPanel.updateRaws.push("tab_"+openerPanel.displayTabId+"_role_" + openerPanel.displayRoleId);
			openerPanel.updateRawStyle();
			saveDynamicPanel();
		});
	
	//set static container
	$jq("#staticAreaContainer").html(jsonRole.layout);
	
	$jq(".submit_button").click(function(){window.close();});
//		$jq(".cancel_button").click(function(){window.close();});

	//Holiday information
	IS_Holiday = new IS_Widget.Calendar.iCalendar(hostPrefix + "/holidaysrv");
	IS_Holiday.noProxy = true;
	IS_Holiday.load(false);

	if(areaType == 0) {
		$jq("#infoscoop").addClass("areaType0");
		$("customizedArea").show();
		
		IS_SiteAggregationMenu.isAdminMode = IS_SiteAggregationMenu.ignoreService = true;
		new IS_SiteAggregationMenu(true);
		new IS_SidePanel.SiteMap(true);
	}
	
	var currentUrl = location.href;
	var srvUrl = currentUrl.replace(/.*\/(manager\/[^\/]*\/).*/, "$1");
	
	new IS_WidgetsContainer(srvUrl + "widsrv?tabId=" + jsonRole.tabId + "&roleOrder=" + jsonRole.roleOrder);

	//menuItem to panel
	var panelBody = document.body;
	var widopt = {
	  accept: function(element, widgetType, classNames){
		  return (classNames.detect( 
			  function(v) { return ["widget"].include(v) } ) &&
				  (widgetType != "mapWidget") );
	  },
	  onHover: function(element, dropElement, dragMode, point) {
		var x = point[0] - element.boxLeftDiff;
		var y = point[1];//Leave y axis as getNearDropTarget

		var min = 10000000;
		var nearGhost = null;// widget near widget ghost
		var widgetGhost = IS_Draggable.ghost;
		widgetGhost.style.display = "block";//for Safari
		var scrollOffset = IS_Portal.tabs[IS_Portal.currentTabId].panel.scrollTop;//IS_Portal.columnsObjs must be in the panel.
		for ( var i=1; i <= IS_Portal.tabs[IS_Portal.currentTabId].numCol; i++ ) {
			var col = IS_Portal.columnsObjs[IS_Portal.currentTabId]["col_dp_" + i];
			for (var j=0; j<col.childNodes.length; j++ ) {
				var div = col.childNodes[j];
				if (div == widgetGhost) {
					continue;
				}
				
				var left = div.posLeft;//Coordinate exclude ghost
				var top = div.posTop - scrollOffset;
				
				var tmp = Math.sqrt(Math.pow(x-left,2)+ Math.pow(y-top,2));
				if (isNaN(tmp)) {
					continue;
				}
				
				if ( tmp < min ) {
					min = tmp;
					nearGhost = div;
					nearGhost.col = col;
				}
				
			}
		}
		
		if (nearGhost != null && widgetGhost.nextSibling != nearGhost) {
			widgetGhost.style.display = "block";
			nearGhost.parentNode.insertBefore(widgetGhost,nearGhost);
			widgetGhost.col = nearGhost.col;
		}
	  },
	  onDrop : function(element, lastActiveElement, widget, event) {
		if(!IS_Portal.canAddWidget()) return;
		var widgetGhost = IS_Draggable.ghost;
		if( !Browser.isSafari ||( widgetGhost && widgetGhost.style.display != "none")){
			var ghostColumnNum = (widgetGhost.col)? widgetGhost.col.getAttribute("colNum"):1;
			widget.widgetConf.column = ghostColumnNum;
			widgetGhost.col.replaceChild(element, widgetGhost);
		} else {
		    widgetGhost.col.removeChild( widgetGhost );
		}
		element.style.position = "";

		var tmpParent =false;
		if(widget.parent){// If sub widget is dropped on the panel.
			element.className="widget";
			widget.tabId = IS_Portal.currentTabId;

			tmpParent = widget.parent;
			IS_Portal.removeSubWidget(widget.parent.id, widget.id);
			//widget.parent.content.removeRssReader(widget.id, false, true);
			//The item deleted from Multi is not removeWidget but setWidgetLocationCommand.
			IS_EventDispatcher.newEvent("applyIconStyle", widget.id);
			IS_EventDispatcher.newEvent("changeConnectionOfWidget", widget.id);
			IS_EventDispatcher.newEvent("applyIconStyle", tmpParent.id);
			
			tmpParent.content.mergeRssReader.isComplete = false;
		}
		
		//Send to Server
		IS_Widget.setWidgetLocationCommand(widget);

		if(tmpParent)
			tmpParent.content.checkAllClose(true);
		
		// TODO: Processing of removing edit tip of title. Processing should be within WidgetHeader
		if(widget.headerContent && widget.headerContent.titleEditBox){
			widget.headerContent.titleEditBox.style.display = "none";
		}
		
		if( widget.isGadget()) {
			if( Browser.isIE8 ) {
				IS_Portal.adjustGadgetHeight( widget,true );
			} else {
				widget.loadContents();
			}
		}
		
		IS_EventDispatcher.newEvent("moveWidget", widget.id);
		saveDynamicPanel();
	  }
	}
	IS_Droppables.add(panelBody, widopt);
	
	var menuopt = {};
	menuopt.accept = "menuItem";
	menuopt.onDrop = function(element, lastActiveElement, menuItem, event) {
		if(!IS_Portal.canAddWidget()) return;
		
		var widgetGhost = IS_Draggable.ghost;
		var ghostColumnNum = (widgetGhost.col)? widgetGhost.col.getAttribute("colNum"):1;
		
		var parentItem = menuItem.parent;
		var p_id;
		var divParent;
		
		if(parentItem){
			p_id = IS_Portal.currentTabId+"_p_" + parentItem.id;
			divParent = $(p_id);
		}

		var widgetConf;
		var subWidgetConf;
		if(/MultiRssReader/.test(menuItem.type)){
			if(!divParent){
				// TODO: Processing of cooperative menu
				var parentItem = menuItem.parent;
				var w_id = IS_Portal.currentTabId + "_p_" + parentItem.id;

				var childMenuList = [];
				var children = parentItem.children;
				for(var i = 0; i < children.length ;i++){
					var feedNode = children[i];
					if(feedNode.type && /MultiRssReader/.test(feedNode.type)){
						childMenuList.push(feedNode.id);
					}
				}
				if(!parentItem.properties)parentItem.properties = [];
				parentItem.properties.children = childMenuList;

				parentItem.properties["itemDisplay"] = parentItem["display"];
				widgetConf = IS_WidgetsContainer.WidgetConfiguration.getConfigurationJSONObject(
					"MultiRssReader", w_id, ghostColumnNum, parentItem.title, parentItem.href, parentItem.properties);
				
				subWidgetConf = IS_WidgetsContainer.WidgetConfiguration.getFeedConfigurationJSONObject(
								"RssReader", "w_" + menuItem.id, menuItem.title, menuItem.href, "false", menuItem.properties);
				subWidgetConf.menuId = menuItem.id;
				subWidgetConf.parentId = "p_" + menuItem.parentId;
			}
		}else{
			/* Recreate config everytime because menu can be changed */
			// Create JSONObject from menuItem
			widgetConf = IS_SiteAggregationMenu.getConfigurationFromMenuItem(menuItem, ghostColumnNum);
		}
		
		var widget;
		if(/MultiRssReader/.test(menuItem.type) && divParent){//Drop cooperative menu from menu
			var parentItem = menuItem.parent;
//			var targetWidget = IS_Portal.widgetLists[IS_Portal.currentTabId][p_id];
			var targetWidget = IS_Portal.getWidget(p_id, IS_Portal.currentTabId);
			
			// Head at order display of time.
			var siblingId;
			var nextSiblingId;
			if(targetWidget.getUserPref("displayMode") == "time"){
				siblingId = "";
				nextSiblingId = "";
			}else{
				siblingId = (widgetGhost.previousSibling) ? widgetGhost.previousSibling.id : "";
				nextSiblingId = (widgetGhost.nextSibling) ? widgetGhost.nextSibling.id : "";
			}
			var w_id = "w_" + menuItem.id;
//			menuItem.type="RssReader";
			var widgetConf = IS_SiteAggregationMenu.getConfigurationFromMenuItem(menuItem, ghostColumnNum);
			widgetConf.type = "RssReader";
			
			// subWidget in the same tab is always built
			var currentTabId = IS_Portal.currentTabId;
			if( Browser.isSafari1 && targetWidget.content.isTimeDisplayMode())
				IS_Portal.currentTabId = "temp";
			
			widgetConf.parentId = "p_" + menuItem.parentId;
			widget = IS_WidgetsContainer.addWidget( currentTabId, widgetConf , true, function(w){
				w.elm_widget.className = "subWidget";
				widgetGhost.parentNode.replaceChild(w.elm_widget, widgetGhost);
			});//TODO: The way of passing sub widget.
			
			IS_Portal.widgetDropped( widget );
			
			if( Browser.isSafari1 && targetWidget.content.isTimeDisplayMode())
				IS_Portal.currentTabId = currentTabId;
			
//			IS_Portal.subWidgetMap[targetWidget.id].push(widget.id);
			IS_Portal.addSubWidget(targetWidget.id, widget.id);
			targetWidget.content.addSubWidget(widget, nextSiblingId);
			if(widget.isBuilt)widget.blink();
			
			//Send to Server
			IS_Widget.setWidgetLocationCommand(widget);

			if( targetWidget.content.isTimeDisplayMode() ) {
				IS_EventDispatcher.addListener("loadComplete",targetWidget.id,function() {
					targetWidget.elm_widgetBox.className = "widgetBox";
					targetWidget.headerContent.applyAllIconStyle();
				},null,true );
				
				targetWidget.loadContents();
			}
		}else{
			addWidgetFunc( IS_Portal.currentTabId,widgetGhost );
		}
		
		function addWidgetFunc( tabId,target ) {
			widget = IS_WidgetsContainer.addWidget( tabId, widgetConf , false, function(w){
					target.parentNode.replaceChild( w.elm_widget,target );
				}, (subWidgetConf)? [subWidgetConf] : null);//TODO: The way of passing sub widget.
			
			//Send to Server
			IS_Widget.setWidgetLocationCommand(widget); //Add SiblingId
			
			var menuId;
			if(/MultiRssReader/.test(menuItem.type)){
				var subWidgets = IS_Portal.getSubWidgetList(widget.id);
				for (var i=0; i < subWidgets.length; i++){
					var feedWidget = subWidgets[i];
					if(feedWidget)
						IS_Portal.widgetDropped( feedWidget );
				}
			}else{
				IS_Portal.widgetDropped( widget );
			}
		}
	}
	menuopt.onHover = function(element, dropElement, dragMode, point) {
		var widgetGhost = IS_Draggable.ghost;
		if(widgetGhost.menuItem && /MultiRssReader/.test(widgetGhost.menuItem.type)){
			var parentItem = widgetGhost.menuItem.parent;
			var p_id = IS_Portal.currentTabId+"_p_" + parentItem.id;
			var divParent = $(p_id);
			if( divParent ) return;
		}
		
		var x = point[0] - element.boxLeftDiff;
		var y = point[1] - element.boxTopDiff;

		var min = 10000000;
		var nearGhost = null;
		var scrollOffset = IS_Portal.tabs[IS_Portal.currentTabId].panel.scrollTop;//IS_Portal.columnsObjs must be in the panel.
		for ( var i=1; i <= IS_Portal.tabs[IS_Portal.currentTabId].numCol; i++ ) {
			var col = IS_Portal.columnsObjs[IS_Portal.currentTabId]["col_dp_" + i];
			for (var j=0; j<col.childNodes.length; j++ ) {
				var div = col.childNodes[j];
				if (div == widgetGhost) {
					continue;
				}
				
				if(dragMode == "menu"){
					var left = findPosX(div);
					var top = findPosY(div) - scrollOffset;
				}else{
					var left = div.posLeft;
					var top = div.posTop - scrollOffset;
				}
				
				var tmp = Math.sqrt(Math.pow(x-left,2)+ Math.pow(y-top,2));
				if (isNaN(tmp)) {
					continue;
				}
				
				if ( tmp < min ) {
					min = tmp;
					nearGhost = div;
					nearGhost.col = col;
				}
				
			}
		}
		if (nearGhost != null && widgetGhost.nextSibling != nearGhost) {
			widgetGhost.style.height = 20 + "px";
			widgetGhost.style.display = "block";
			nearGhost.parentNode.insertBefore(widgetGhost,nearGhost);
			widgetGhost.col = nearGhost.col;
		}
	}
	IS_Droppables.add(panelBody, menuopt);
	
	
	/** init layoutSelect **/
	var currentStaticColCount = $jq("#staticAreaContainer .static_column").length;
	
	$jq("#staticLayouts"+(areaType != 2 ? "AdjustHeight":"")).hide();
	var targetAreaId = "staticLayouts"+(areaType == 2 ? "AdjustHeight":"");
	$jq("#" + targetAreaId+ ">div").hide();
	
	// create gadgets-num button
	var columnsCountList = [];
	$jq("#" + targetAreaId+ ">div").each(function(idx, staticLayout){
		staticLayout = $jq(staticLayout);
		
		var columnLength = $jq(".static_column", $jq(">:not(.template)", staticLayout)).length;
		
		staticLayout.addClass("gadgets-" + columnLength);
		
		if($jq.inArray(columnLength, columnsCountList) == -1)
			columnsCountList.push(columnLength);
	});
	
	columnsCountList.sort(function(a,b){return a>b});
	
	$jq.each(columnsCountList, function(idx, gadgetsNum){
		var parent = $jq("#gadgetsnum_buttonset")
		var idPrefix = "gadgetsnum_buttonset_";
		;
		var radioButton = $jq("<input>")
			.attr("id", idPrefix + gadgetsNum)
			.attr("type", "radio")
			.attr("name", idPrefix + "radio")
			.appendTo(parent);
		
		if(gadgetsNum == 0){
			radioButton.attr("checked", true);
		}
		else if(gadgetsNum == currentStaticColCount){
			$jq("input", parent).attr("checked", false);
			radioButton.attr("checked", true);
		}
		
		$jq("<label>").attr("for", idPrefix + gadgetsNum).text(gadgetsNum).appendTo(parent);
		
		radioButton.click(function(){
			var id = $jq(this).attr("id");
			var number = id.replace(/^gadgetsnum_buttonset_(.+)$/, "$1");
			
			$jq("#" + targetAreaId+ ">div").hide();
			$jq("#" + targetAreaId + " .gadgets-" + number).show();
		});
	});
	
	$jq("#gadgetsnum_buttonset").buttonset();
	
	// show default
	if($jq("#" + targetAreaId + " .gadgets-" + currentStaticColCount).length > 0){
		$jq("#" + targetAreaId + " .gadgets-" + currentStaticColCount).show();
	}else{
		$jq("#" + targetAreaId + " .gadgets-" + columnsCountList[0]).show();
	}
	
	$jq("#select_layout_link").click(function(){		
		$jq("#select_layout_modal").dialog({
			modal:true,
			width: 600,
			height: 500,
			resizable: false,
			draggable: false,
			open: function(){
				IS_Portal.behindIframe.show(this.parentNode);
				
				var dialog = $jq(this);
				if(dialog.data("init")) return;
				
				// init process
				$jq("#select_layout_modal .staticLayout"+(areaType == 2 ? "AdjustHeight":""))
					.mouseover(function(){$jq(this).css("background-color","#9999cc")})
					.mouseout(function(){$jq(this).css("background-color","")})
					.click(function(){
						if(!confirm(ISA_R.alb_destroyOldSettings))
							return;

						var selectedContent = $jq(this);
						var layoutTemplate = $jq($jq.parseHTML(selectedContent.html())).closest(".template");
						var newNode = (layoutTemplate.length > 0) ? layoutTemplate : selectedContent.clone(true);
						setIdentifier(newNode);
						openerPanel.setNewValue("layout", newNode.html(), jsonRole.id);
						openerPanel.setNewValue("staticPanel", "{}", jsonRole.id);
						
						$jq("#staticAreaContainer").html(jsonRole.layout);
						
						prepareStaticArea();
						
						reloadStaticGadgets();
						adjustStaticWidgetHeight();
						dialog.dialog("close");
					});
				$jq("#select_layout_cancel").click(function(){
					dialog.dialog("close");
				});
				
				dialog.data("init", true);
			},
			close:function(){
				IS_Portal.behindIframe.hide();
			}
		});
	});
	
	$jq("#edit_layout_link").click(function(){
		$jq("#edit_layout_modal").dialog({
			modal:true,
			width: 580,
			height: 400,
			resizable: false,
			draggable: false,
			open:function(){
				IS_Portal.behindIframe.show(this.parentNode);
				
				$jq("#edit_layout_textarea").val(jsonRole.layout);
				
				var dialog = $jq(this);
				if(dialog.data("init")) return;
				
				$jq("#edit_layout_ok").click(function(){
					/*
					openerPanel.setNewValue("layout", $jq("#edit_layout_textarea").val(), jsonRole.id);
					$jq('#staticAreaContainer').html(jsonRole.layout);
					*/
					var staticAreaContainer = $jq('#staticAreaContainer');
					staticAreaContainer.html($jq("#edit_layout_textarea").val());
					setIdentifier(staticAreaContainer);
					openerPanel.setNewValue("layout", staticAreaContainer.html(), jsonRole.id);
					
					prepareStaticArea();
					
					reloadStaticGadgets();
					dialog.dialog("close");
				});
				
				$jq("#edit_layout_cancel").click(function(){
					dialog.dialog("close");
				});
				dialog.data("init", true);
			},
			close:function(){
				IS_Portal.behindIframe.hide();
			}
		});
	});
	
	//handle areaType
	Event.observe($("areaType"), 'change', function(){
		//change areaType from static and personalized area to only static area.
		if(areaType != this.value){
//			if(!confirm("表示エリアを変更するにはリロードする必要があります。よろしいですか？")){
			var confirmMessage;
			var isReset = false;
			if((this.value == "2" && areaType != "2") || (this.value != "2" && areaType == "2")){
				confirmMessage = ISA_R.alb_changeStaticAreaType_confirm_2;
				isReset = true;
			}else{
				confirmMessage = ISA_R.alb_changeStaticAreaType_confirm_1;
			}
			
			if(!confirm(confirmMessage)){
				$jq("#areaType option[value="+ areaType.toString() +"]").attr("selected","selected");
				return;
			}
			
			if(isReset){
				// reset
				var target = $jq("#staticLayout" + ((this.value == "2")? "AdjustHeight" : "") + "_tpl_default");
				var layoutTemplate = $jq($jq.parseHTML(target.html())).closest(".template");
				layoutTemplate = (layoutTemplate.length > 0) ? layoutTemplate : $jq(target).clone(true);
				setIdentifier(layoutTemplate);
				openerPanel.setNewValue("layout", layoutTemplate.html(), jsonRole.id);
				openerPanel.setNewValue("staticPanel", "{}", jsonRole.id);
			}
			
			switch(this.value){
				case "0" :
					openerPanel.setNewValue("disableddynamicpanel", false, jsonRole.id);
					openerPanel.setNewValue("adjusttowindowheight", false, jsonRole.id);
					break;
				case "1" :
					openerPanel.setNewValue("disableddynamicpanel", true, jsonRole.id);
					openerPanel.setNewValue("adjusttowindowheight", false, jsonRole.id);
					break;
				case "2" :
					openerPanel.setNewValue("disableddynamicpanel", true, jsonRole.id);
					openerPanel.setNewValue("adjusttowindowheight", true, jsonRole.id);
					break;
			}
			
			location.reload();
		}
	});
};

function setIdentifier(htmlEl){
	htmlEl = $jq(htmlEl);
	$jq(".static_column", htmlEl).each(function(idx, el){
		el = $jq(el);
		if(!el.attr("id")){
			var datetime = new Date().getTime();
			var idPrefix = "p_" + datetime + "_w_";
			
			el.attr("id", idPrefix + idx);
		}
	});
	return htmlEl;
}

IS_Portal.widgetDropped = function( widget ) {
	if( IS_TreeMenu.isMenuItem( widget.id ) )
		IS_EventDispatcher.newEvent( IS_Widget.DROP_WIDGET, IS_TreeMenu.getMenuId( widget.id ) );
	
	saveDynamicPanel();
}

function displayStaticGadget(widgetOpt){
	var containerId = widgetOpt.id;
	var container = $(containerId);
	if(!container) {
		IS_Portal.widgetLists[IS_Portal.currentTabId][containerId] = false;
		return;
	}
	var realContainer = $("s_"+containerId);
	if(realContainer) {
		container.parentNode.removeChild(container);
		container = realContainer;
	}else{
		container.id = "s_" + containerId;
	}
	var widget = new IS_Widget(false, widgetOpt);
	widget.panelType = "StaticPanel";
	widget.tabId = IS_Portal.currentTabId;
	
	IS_Portal.widgetLists[IS_Portal.currentTabId][widget.id] = widget;
	
	widget.build();
	container.appendChild(widget.elm_widget);
	
	widget.loadContents();
}
function reloadStaticGadgets(){
	var widgets = IS_Portal.widgetLists[IS_Portal.currentTabId];
	for(var id in widgets){
		var widget = widgets[id];
		if(widget.panelType != "StaticPanel") continue;
		displayStaticGadget(widget.widgetConf);
	}
}

var timeout = false;;
function saveDynamicPanel(){
	if(timeout)	clearTimeout(timeout);
	timeout = setTimeout(_saveDynamicPanel, 100);
}

function _saveDynamicPanel(){
	var newDynamicPanel = {};
	
	var numCol = $jq("numberOfColumns").val();
	
	var columns = $jq("#dynamic-portal-widgets" + jsonRole.tabId + " .column");
	$jq.each(columns, function(index, column){
		
		$jq.each($jq(".widget", column), function(index, widgetEl){
			var wid = IS_Portal.getTrueId(widgetEl.id);
			var widget = IS_Portal.getWidget(wid, IS_Portal.currentTabId);
			var menuId = (widget.widgetConf.menuId)? widget.widgetConf.menuId : wid.substring(2);
			
			var targetMenu = (IS_TreeMenu.types.topmenu)? IS_TreeMenu.types.topmenu : IS_TreeMenu.types.sidemenu;
			var menuItem = {};
			if(!targetMenu){
				menuItem = {
					id: +new Date(),
					type: "notAvailable"
				}
			}else{
				menuItem = targetMenu.menuItemList[menuId];
				if(!menuItem)
					return true;
			}
			
			var colnum = $jq(widget.elm_widget).parent().attr('colnum');
			var widgetJSON = {
				id : "w_" + (menuItem.multi? menuItem.id + "_" + (+new Date()) + "_" + colnum + "_" + index : menuItem.id),
				menuId : menuItem.id,
				column : new String($jq(widget.elm_widget).parent().attr('colnum')),
				type : menuItem.type? menuItem.type : widget.widgetType,
				properties: (menuItem.properties && typeof menuItem.properties == "Object" )?
								menuItem.properties : {}
			};
			
			if(/MultiRssReader/.test( widgetJSON.type )) {
				widgetJSON.id = "p_"+widgetJSON.id.substring(2);
				var subWidgets = IS_Portal.getSubWidgetList(widgetJSON.id);
				
				var childrenList = [];
				for(var i=0;i<subWidgets.length;i++){
					childrenList.push(subWidgets[i].id.substring(2));
				}
				widgetJSON.properties.children = Object.toJSON(childrenList);
				//delete widgetJSON.properties.url;
				is_deleteProperty(widgetJSON.properties, "url");
			}
			
			if(menuItem.title){
				widgetJSON.title = menuItem.title;
			}else{
				var _title = ISA_SiteAggregationMenu.widgetConfs[ widgetJSON.type ].title;
				if(_title){
					widgetJSON.title = _title;
				}else{
					widgetJSON.title = widgetJSON.type;
				}
			}
			widgetJSON.href = ("MiniBrowser" == widgetJSON.type) ? 
				widgetJSON.properties.url : (menuItem.href) ? menuItem.href : "";

			//delete widgetJSON.properties.title;
			is_deleteProperty(widgetJSON.properties, "title");
			//delete widgetJSON.properties.href;
			is_deleteProperty(widgetJSON.properties, "href");
			
			newDynamicPanel[widgetJSON.id] = widgetJSON
		});
	});
	
//		jsonRole.dynamicPanel = newDynamicPanel;
	openerPanel.setNewValue("dynamicPanel", Object.toJSON(newDynamicPanel), jsonRole.id);
}

function updatePanel(){
	openerPanel.isUpdated = true;
	
	// sync
	for(var i in jsonRole.staticPanel){
		if(jsonRole.staticPanel[i] && jsonRole.staticPanel[i].id){
			var widgetId = jsonRole.staticPanel[i].id;
			if($jq("#" + widgetId).size()  == 0){
				is_deleteProperty(jsonRole.staticPanel, widgetId)
			}
		}
	}
	
	openerPanel.updatePanel(true);
}
Event.observe(window, 'beforeunload', updatePanel);

if(displayTopMenu && IS_SiteAggregationMenu.topMenuIdList)
	Event.observe(window, "scroll",  IS_SiteAggregationMenu.resetMenu, false);

