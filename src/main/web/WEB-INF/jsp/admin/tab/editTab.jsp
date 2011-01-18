<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<tiles:insertDefinition name="prototype.definition" flush="true">
	<tiles:putAttribute name="type" value="tab"/>
	<tiles:putAttribute name="title" value="tab.title"/>
	<tiles:putAttribute name="body" type="string">
<link rel="stylesheet" type="text/css" href="../../skin/styles.css">
<link rel="stylesheet" type="text/css" href="../../skin/siteaggregationmenu.css">
<link rel="stylesheet" type="text/css" href="../../skin/treemenu.css">
<link rel="stylesheet" type="text/css" href="../../skin/widget.css">
<link rel="stylesheet" type="text/css" href="../../skin/theme.css">

<link rel="stylesheet" type="text/css" href="../../skin/calendar.css">
<link rel="stylesheet" type="text/css" href="../../skin/rssreader.css">

<script type="text/javascript" src="../../js/lib/jquery.js"></script>
<link rel="stylesheet" type="text/css" href="../../js/lib/jquery-ui/css/smoothness/jquery-ui-1.8.4.custom.css">
<script type="text/javascript" src="../../js/lib/jquery-ui/jquery-ui-1.8.4.custom.min.js"></script>
<script type="text/javascript" src="../../js/lib/livequery-1.1.0/jquery.livequery.js"></script>
<link rel="stylesheet" type="text/css" href="../../js/lib/DataTables-1.7.4/css/demo_page.cs">
<link rel="stylesheet" type="text/css" href="../../js/lib/DataTables-1.7.4/css/demo_table.css">
<script src="../../js/lib/DataTables-1.7.4/js/jquery.dataTables.min.js"></script>
<style>
body{
	font-size:11px !important;
}
#staticAreaContainer{
	margin: 10px 0;
}
.staticLayout, .staticLayoutAdjustHeight{
	width: 22%;
	float: left;
	margin-top: 5px;
	margin-left: 5px;
	padding: 5px;
	border: solid 1px #000;
	cursor: pointer;
	width:120px;
	height:194px;
}
.static_column {
	border : dotted 1px gray;
	cursor: pointer;
	position:relative
}
.staticLayoutAdjustHeight .static_column{
	height:182px;
}
.edit_static_gadget{
	position: absolute;top: 0px;left: 0px;
	text-decoration: underline;
	color : blue;
	cursor: pointer;
	text-align:center;
	vertical-align:middle;
	-moz-opacity:0.7;
	opacity:0.7;
	filter:alpha(opacity=70);
	background-color: #F0F0F0;
	z-index: 10;
	width:100%;
	height:100%;
}

#access_level_radio {
	display:inline-block;
}
#infoscoop #portal-tree-menu{
	display:none;
}
#infoscoop #infoscoop-panel{
	width:100%;
}
#infoscoop.areaType0 #portal-tree-menu{
	display:block;
	width:20%;
}
#infoscoop.areaType0 #infoscoop-panel{
	width:80%;
}

/*- Form --------------------------- */
// TODO: this css is contained in manager.css too.
.cssform{
	margin-left: 5px;
	padding: 15px 15px 25px;
}
.cssform fieldset{
	border: 1px solid #AAAAAA;
	margin: 10px 0;
	padding: 10px;
}
.cssform legend{
	font-weight: bold;
	font-size:1.1em;
	_margin: 0 -7px; /* IE Win */
}
.cssform legend, .cssform label{
	padding-left: 0;
	color: #333;
}
.cssform ul{
	padding:0;
	list-style-type:none;	
}
.cssform li{
	display:block;
	margin: 0;
	padding: 5px 0;
	padding-left: 155px; /*width of left column containing the label elements*/
	/*height: 1%;*/
}

.cssform label{
	float: left;
	margin-left: -155px; /*width of left column*/
	width: 150px; /*width of labels. Should be smaller than left column (155px) to create some right margin*/
	text-align:right;
	padding-top: 2px;
}

.cssform input[type="text"]{ /*width of text boxes. IE6 does not understand this attribute*/
	width: 180px;
}
.cssform input[type=text],
.cssform input[type=password],
.cssform input.text,
.cssform input.title,
.cssform textarea,
.cssform select {
	background-color:#fff;
	border:1px solid #bbb;
}
.cssform input[type=text]:focus,
.cssform input[type=password]:focus,
.cssform input.text:focus,
.cssform input.title:focus,
.cssform textarea:focus,
.cssform select:focus {
	border-color:#666;
}

.cssform textarea{
	width: 250px;
	height: 150px;
}
.cssform .radio label{
	float: none;
	width: auto;
	margin-left: 0;
	text-align:left;
}

/*- Security group list  --------------------------- */
#role_list_table ul, #role_edit_table ul, #selectRoleTable ul{
	padding:0;
	margin:0;
	list-style-type:none;
}

#role_list_table li, #role_edit_table li, #selectRoleTable li{
	padding:2;
}

</style>

<script src="../../js/resources/resources_ja.js"></script>
<script>
jQuery.noConflict();
$j = jQuery;

function getInfoScoopURL() {
	var currentUrl = location.href;
	return currentUrl.replace(/\/manager\/.*/, "");
}
var staticContentURL=getInfoScoopURL();
var ajaxRequestTimeout=15000;
var messagePriority = 4;
var localhostPrefix = staticContentURL;
var hostPrefix = staticContentURL + "/manager"
var proxyServerURL = hostPrefix + "/proxy";
var imageURL = staticContentURL + "/skin/imgs/";
var maxColumnNum=10;
var displayTopMenu  = true;
var displaySideMenu  = true;
var fixedPortalHeader = false;
var useTab = false;
var isTabView = false;
var is_userId = "test";
var refreshInterval=10;
var rssPageSize = 25;
var ajaxRequestRetryCount=2;
var freshDays = 1;
var rssMaxCount = 100;
var commandQueueWait = 15;
var gadgetProxyURL = localhostPrefix + "/gadgetsrv";

IS_R['getResource'] = function(s){return s;}
var gadgets = {'rpc':{'setRelayUrl':function(){},'setAuthToken':function(){}}};
IS_Widget = {};
IS_Widget.getIcon = function() {
	return imageURL + 'widget_add.gif'; 
};
IS_WidgetIcons = {
	'RssReader':imageURL + 'widget_add.gif'
}
var displayTabOrder = "0";
IS_Portal = {
	tabs: {},
	fontSize: '14px',
	isItemDragging: false,
	columnsObjs: {},
	rssSearchBoxList: {},
	isChecked: function(){return false},
	showDragOverlay: function(){},
	hideDragOverlay: function() {},
	displayMsgBar: function(){},
	unDisplayMsgBar: function(){},
	adjustPanelHeight: function(){},
	adjustIframeHeight: function(){},
	adjustGadgetHeight: function(){},
	adjustMsgBar: function(){},
	deleteCacheByUrl:function(){},
	endIndicator: function(){},
	widgetDisplayUpdated: function(){},
	Trash:{
		add:function(){}
	}
}
</script>
<script src="../../js/utils/utils.js"></script>
<script src="../../js/utils/ajax304.js"></script>
<script src="../../js/lib/control.modal.js"></script>
<script src="../../js/lib/extras-array.js"></script>
<script src="../../js/lib/date/date.js"></script>
<script src="../../js/utils/domhelper.js"></script>
<script src="../../js/SiteAggregationMenu.js"></script>
<script src="../../js/SiteMap.js"></script>
<script src="../../js/TreeMenu.js"></script>
<script src="../../js/utils/EventDispatcher.js"></script>
<script src="../../js/Tab.js"></script>	
<script src="../../js/WidgetsContainer.js"></script>
<script src="../../js/utils/ajaxpool/ajax.js"></script>
<script src="../../js/utils/Request.js"></script>
<script src="../../js/utils/msg.js"></script>
<script src="../../js/DragWidget.js"></script>
<script src="../../js/widgets/Widget.js"></script>
<script src="../../js/widgets/WidgetHeader.js"></script>
<script src="../../js/widgets/WidgetEdit.js"></script>
<script src="../../js/commands/UpdatePropertyCommand.js"></script>

<script src="../../js/widgets/rssreader/RssReader.js"></script> 
<script src="../../js/widgets/MultiRssReader/MultiRssReader.js"></script> 
<script src="../../js/widgets/rssreader/RssItemRender.js"></script>
<script src="../../js/widgets/calendar/Calendar.js"></script>
<script src="../../js/widgets/calendar/iCalendar.js"></script>
<script src="../../js/widgets/MiniBrowser/MiniBrowser.js"></script>
<script src="../../js/widgets/MiniBrowser/FragmentMiniBrowser.js"></script>

<script type="text/javascript" class="source">
IS_SidePanel.adjustPosition = function(){};
IS_Request.CommandQueue = new IS_Request.Queue("/tab/comsrv", commandQueueWait, !is_userId);
IS_Request.CommandQueue.unrepeat();
IS_Request.CommandQueue._fireRequest = IS_Request.CommandQueue.fireRequest;
IS_Request.CommandQueue.fireRequest = function(){
	IS_Request.CommandQueue.addCommand({
		id: "UpdateTabTemplateTimestamp_${tabTemplate.id}",
		toRequestString: function(){
			return '<command type="UpdateTabTemplateTimestamp" id="'+this.id+'" tabId="${tabTemplate.id}"/>';
		},
		parseResponse: function(){}
	});
	IS_Request.CommandQueue._fireRequest();
};
IS_Request.CommandQueue.repeat(commandQueueWait);

var areaType = ${tabTemplate.areaType};
IS_Portal.addTab = function(idNumber, name, type, layout, numCol, columnsWidth, disabledDynamicPanel, adjustStaticHeight, isInitialize){
	IS_Portal.widgetLists["tab"+idNumber] = new Object();
	IS_Portal.columnsObjs["tab"+idNumber] = {};
	IS_Portal.tabs["tab"+idNumber] = {"numCol":0,type:"static",adjustStaticHeight:adjustStaticHeight};
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
IS_Customization = {"commandbar":"<table cellpadding=\"0\" cellspacing=\"3\" width=\"100%\">\r\n\t<tr>\r\n\t\t<td width=\"100%\"><div id=\"p_1_w_4\"><\/div><\/td>\r\n\t\t<td><div id=\"p_1_w_6\"><\/div><\/td>\r\n\t\t<td><div id=\"portal-go-home\"><\/div><\/td>\r\n\t\t<td><div id=\"disabled_portal-change-fontsize\" disabledCommand=\"true\"><!--&lt;div id=\"portal-change-fontsize\"&gt;&lt;/div&gt;--><\/div><\/td>\r\n\t\t<td><div id=\"portal-trash\"><\/div><\/td>\r\n\t\t<td><div id=\"portal-preference\"><div class=\"allPreference\"><\/div><\/div><\/td>\r\n\t\t<td><div id=\"disabled_portal-credential-list\" disabledCommand=\"true\"><!--&lt;div id=\"portal-credential-list\"&gt;&lt;/div&gt;--><\/div><\/td>\r\n\t\t<td><div id=\"portal-admin-link\"><\/div><\/td>\r\n\t\t<td><div id=\"disabled_portal-logout\" disabledCommand=\"true\"><!--&lt;div id=\"portal-logout\"&gt;&lt;/div&gt;--><\/div><\/td>\r\n\t<\/tr>\r\n<\/table>\r\n","staticPanel0":"<DIV>\r\n\t<DIV style=\"FLOAT: left; WIDTH: 74.5%\">\r\n\t\t<DIV style=\"HEIGHT: 178px\">\r\n\t\t\t<DIV style=\"FLOAT: left; WIDTH: 100%; HEIGHT: 100%\">\r\n\t\t\t\t<DIV id=\"p_1_w_1\" class=\"static_column\" style=\"MARGIN-LEFT: 2px; HEIGHT: 170px\"><\/DIV>\r\n\t\t\t<\/DIV>\r\n\t\t<\/DIV>\r\n\t<\/DIV>\r\n\t<DIV style=\"FLOAT: right; WIDTH: 25%; HEIGHT: 178px\">\r\n\t\t<DIV id=\"p_1_w_5\" class=\"static_column\" style=\"MARGIN-LEFT: 2px; HEIGHT: 170px\"><\/DIV>\r\n\t<\/DIV>\r\n<\/DIV>\r\n<DIV style=\"CLEAR: both; display:none;\"/>\r\n","contentFooter":[{"type":"mail"},{"type":"message"}],"css":"/* Custom CSS code is described here.  */","header":"<table width=\"100%\" height=\"53px\" cellspacing=\"0\" cellpadding=\"0\" style=\"background:url(skin/imgs/head_blue.png)\">\r\n\t<tbody>\r\n\t\t<tr>\r\n\t\t\t<td><a href=\"javascript:void(0)\" onclick=\"javascript:IS_Portal.goHome();return false;\"><img src=\"skin/imgs/infoscoop.gif\" alt=\"infoScoop\" border=\"0\" style=\"margin:0 0 0 20px;\" height=\"45\"/><\/a>\r\n\t\t\t<\/td>\r\n\t\t\t<td>\r\n\t\t\t\t<form name=\"searchForm\" onsubmit=\"javascript:IS_Portal.SearchEngines.buildSearchTabs(document.getElementById('searchTextForm').value);return false;\">\r\n\t\t\t\t<div style=\"float:right;margin-right:5px\">\r\n\t\t\t\t\t<table>\r\n\t\t\t\t\t\t<tbody>\r\n\t\t\t\t\t\t\t<tr>\r\n\t\t\t\t\t\t\t\t<td colspan=\"2\" align=\"right\" style=\"font-size:80%;\">\r\n\t\t\t\t\t\t\t\t\tWelcome,admin-\r\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<\/td>\r\n\t\t\t\t\t\t\t<\/tr>\r\n\t\t\t\t\t\t\t<tr>\r\n\t\t\t\t\t\t\t\t<td>\r\n\t\t\t\t\t\t\t\t\t<input id=\"searchTextForm\" type=\"text\" style=\"width:200px;height:23px;float:left;\"/>\r\n\t\t\t\t\t\t\t\t\t<input type=\"submit\" value=\"Search\" style=\"padding:0 0.4em;\"/>\r\n\t\t\t\t\t\t\t\t\t<span id=\"editsearchoption\">Search options<\/span>\r\n\t\t\t\t\t\t\t\t<\/td>\r\n\t\t\t\t\t\t\t<\/tr>\r\n\t\t\t\t\t\t<\/tbody>\r\n\t\t\t\t\t<\/table>\r\n\t\t\t\t<\/div>\r\n\t\t\t\t<\/form>\r\n\t\t\t<\/td>\r\n\t\t<\/tr>\r\n\t<\/tbody>\r\n<\/table>","title":"infoScoop"};
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
// mock for behindIframe
IS_Portal.behindIframe = {
	show:function(){},
	hide:function(){}
}
IS_forbiddenURLs = {};

//
IS_Portal.currentTabId = "tab${tabTemplate.id}";
IS_Portal.trueTabId = "tab${tabTemplate.tabId}";
IS_Portal.deleteTempTabFlag = 1;//1-- excute beforeunload, 0-- don't excute beforeunload

function deleteTempTabTemplate(){
	try{
		IS_Request.asynchronous = false;
		IS_Request.CommandQueue.fireRequest();
	}catch(e){
	}
	if(isTemp(IS_Portal.deleteTempTabFlag)){
		var a = new Ajax.Request(
			"deleteTempTab",
			{
				"method": "get",
				"parameters": "id=${tabTemplate.id}",
				asynchronous: false,
				onSuccess: function(request) {
					//alert('temp=1のタブを削除しました');
				},
				onFailure: function(request) {
					alert('読み込みに失敗しました');
				},
				onException: function (request) {
					alert('読み込み中にエラーが発生しました');
				}
			}
		);
	}
}

Event.observe(window, "beforeunload", deleteTempTabTemplate, false);

var editors = ${editors};
if(editors.length > 0){
	if(!confirm(editors.join("/")+"がこのタブを編集中です。\n他のユーザによって修正が上書きされる恐れがありますが、編集を続けてよろしいですか？")){
		window.close();
	}
}

function prepareStaticArea(){
	IS_Portal.deleteTempTabFlag = 1;
	var tabId = IS_Portal.currentTabId.replace("tab","");
	$j('#staticAreaContainer .static_column').each(function(j){
		var containerId = IS_Portal.trueTabId + '_static_column_' + j;
		div = $j(this).attr("id", containerId).data("containerId", containerId);
		var edit_cover = $j('<div/>')
			.attr("id", "edit_div_" + j)
			.addClass("edit_static_gadget")
			.hide()
			.click(function(){
				var staticGadgetModal = $j("#static_gadget_modal");
				if(staticGadgetModal.length == 0){
					staticGadgetModal = $j('<iframe id="static_gadget_modal"/>')
						.css({width:"600px", height:"480px", border:"none"})
						.dialog({
							modal:true,
							width:600,
							height:480,
							resizable:false,
							draggable:false,
							autoOpen:false,
							open:function(){
								$j(this).width(590);
							}
						});
				}
				staticGadgetModal
					.attr("src", hostPrefix + "/tab/editStaticGadget?tabId=" + tabId + "&containerId=" + containerId)
					.dialog("option", "title", "ガジェットの編集")
					.dialog("open");
			})
			.appendTo(div);
		div
			.mouseover(function(){
				var $this = $j(this);
				edit_cover
					.text(($this.attr("id") == $this.data("containerId")) ? "New" : "Edit")
					.show();
			})
			.mouseout(function(){
				edit_cover.hide();
			});
	});
	$j("#layout").val($j("#staticAreaContainer").html());
};

function adjustStaticWidgetHeight(){
	if(areaType != 2) return;
	IS_Portal.adjustStaticWidgetHeight();
	var columns = $$("#staticAreaContainer .static_column");
	var windowHeight = getWindowSize(false) - findPosY($("staticAreaContainer")) - 36;
	for(var i =0; i < columns.length; i++){
		columns[i].style.height = windowHeight;
	}
}

function init() {
	document.body.className = "infoScoop";
	jQuery(".submit_button").button().click(changeFlag);
	jQuery(".cancel_button").button().click(function(){window.close();});
	//Event.observe('submit_button', 'click', changeFlag, false);

	//Holiday information
	IS_Holiday = new IS_Widget.Calendar.iCalendar(localhostPrefix + "/holidaysrv");
	IS_Holiday.load(false);

	new IS_WidgetsContainer("/tab/widsrv");
	if(areaType == 0) {
		$("infoscoop").addClassName("areaType0");
		new IS_SiteAggregationMenu();
		new IS_SidePanel.SiteMap();
	}

	//menuItem to panel
	var panelBody = document.body;
	var widopt = {
	  accept: function(element, widgetType, classNames){
		  return (classNames.detect( 
			  function(v) { return ["widget", "subWidget"].include(v) } ) &&
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
			if( Browser.isIE ) {
				IS_Portal.adjustGadgetHeight( widget,true );
			} else {
				widget.loadContents();
			}
		}
		
		IS_EventDispatcher.newEvent("moveWidget", widget.id);
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
			/* Recreate config everytime becasue menu can be changed */
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
			widgetGhost.style.height = 20;
			widgetGhost.style.display = "block";
			nearGhost.parentNode.insertBefore(widgetGhost,nearGhost);
			widgetGhost.col = nearGhost.col;
		}
	}
	IS_Droppables.add(panelBody, menuopt);
	
	$j("#select_layout_link").button().click(function(){
		$j("#select_layout_modal").dialog({
			modal:true,
			width:600,
			height:500,
			open: function(){
				var dialog = $j(this);
				if(dialog.data("init")) return;
				$j("#staticLayouts"+(areaType != 2 ? "AdjustHeight":"")).hide();
				$j("#select_layout_modal .staticLayout"+(areaType == 2 ? "AdjustHeight":""))
					.mouseover(function(){$j(this).css("background-color","#9999cc")})
					.mouseout(function(){$j(this).css("background-color","")})
					.click(function(){
						$j("#staticAreaContainer").html($j(this).html());
						prepareStaticArea();
						$j('#layoutModified').val("true");
						reloadStaticGadgets();
						clearStaticGadgets();
						adjustStaticWidgetHeight();
						dialog.dialog("close");
					});
				$j("#select_layout_cancel").click(function(){
					dialog.dialog("close");
				});
				dialog.data("init", true);
			}
		});
	});
	
	$j("#edit_layout_link").button().click(function(){
		$j("#edit_layout_modal").dialog({
			modal:true,
			width:580,
			height:400,
			open:function(){
				$j("#edit_layout_textarea").val($j("#layout").val());
				$j("#edit_layout_ok").click(function(){
					$j('#staticAreaContainer').html($j("#edit_layout_textarea").val());
					prepareStaticArea();
					$j('#layoutModified').val("true");
					reloadStaticGadgets();
					clearStaticGadgets();
					dialog.dialog("close");
				});
				var dialog = $j(this);
				if(dialog.data("init")) return;
				$j("#edit_layout_cancel").click(function(){
					dialog.dialog("close");
				});
				dialog.data("init", true);
			}
		});
	});
	
	//handle areaType
	Event.observe($("areaType"), 'change', function(){
		if(!confirm("表示エリアを変更するにはリロードする必要があります。よろしいですか？"))
			return;
		changeFlag();
		var addTabButton = $("add_tab");
		addTabButton.action = "updateTemp";
		addTabButton.submit();
	});
	
	jQuery("#numberOfColumns").change(function(){
		IS_WidgetsContainer.rebuildColumns(IS_Portal.currentTabId, parseInt(jQuery(this).val()));
	});
};

function changeFlag(){
	IS_Portal.deleteTempTabFlag =0;//Don't excute beforeunload
}

function isTemp(flag){
	return flag == 1;
}

function clearStaticGadgets(){
	var widgets = IS_Portal.widgetLists[IS_Portal.currentTabId];
	var removeIds = [];
	for(var id in widgets){
		if(!widgets[id]) removeIds.push(id);
	};
	if(removeIds.length == 0) return;
	new Ajax.Request(
		"clearStaticGadgets",
		{
			"method": "post",
			"parameters": {tabid: '${tabTemplate.id}', widgetids:removeIds},
			asynchronous: false,
			onFailure: function(request) {
				alert('読み込みに失敗しました');
			},
			onException: function (request) {
				alert('読み込み中にエラーが発生しました');
			}
		}
	);
}

Event.observe(window, "load", init, false);


IS_Portal.widgetDropped = function( widget ) {
	if( IS_TreeMenu.isMenuItem( widget.id ) )
		IS_EventDispatcher.newEvent( IS_Widget.DROP_WIDGET, IS_TreeMenu.getMenuId( widget.id ) );
}

IS_WidgetConfiguration = <jsp:include page="/widconf" flush="true" />;

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
	widget.containerId = containerId;
	widget.build();
	container.appendChild(widget.elm_widget);
	
	widget.loadContents();
	IS_Portal.widgetLists[IS_Portal.currentTabId][widget.id] = widget;
}
function reloadStaticGadgets(){
	var widgets = IS_Portal.widgetLists[IS_Portal.currentTabId];
	for(var id in widgets){
		var widget = widgets[id];
		if(widget.panelType != "StaticPanel") return;
		displayStaticGadget(widget.widgetConf);
	}
}

</script>

<c:import url="/WEB-INF/jsp/admin/tab/_formTab.jsp"/>

<div style="display:none" id='edit_layout_modal'>
	<div>HTMLの編集を行い、[OK]ボタンをクリックしてください。</div>
	<div style="text-align:center;"><textarea id='edit_layout_textarea' rows='20' style='width:90%'></textarea></div>
	<center><input id='edit_layout_ok' type="button" value="OK"/><input id='edit_layout_cancel' type="button" value="キャンセル"/></center>
</div>
<div style="display:none" id='select_layout_modal'>
	<c:import url="/WEB-INF/jsp/admin/tab/_layoutTemplates.jsp"/>
	<div style="clear:both;text-align:center;"><input id='select_layout_cancel' type="button" value="キャンセル"/></div>
</div>
	<div id="select_role_dialog">
	</div>
	</tiles:putAttribute>
</tiles:insertDefinition>