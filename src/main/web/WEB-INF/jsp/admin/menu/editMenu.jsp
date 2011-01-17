<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="menu"/>
	<tiles:putAttribute name="title" value="menu.title"/>
	<tiles:putAttribute name="body" type="string">
<script type="text/javascript" src="../../js/lib/jsTree.v.1.0rc2/jquery.jstree.js"></script>
<script type="text/javascript" src="../../js/manager/gadget.js"></script>
<script type="text/javascript" class="source">
var hostPrefix = "/infoscoop";//TODO スクリプトで計算
//TODO propertiesテーブルから取得して補正する
var staticContentURL="../..";
var imageURL = staticContentURL + "/skin/imgs/"
var copiedItemId, gadgetConfs, menuId = "${menuId}";
function $loadContent(url, data, callback){
	$("#menu_right").html('<div class="loading_bar">');
	$.ajax({
		url:url,
		data:data,
		type:"POST",
		success: function(data, status, xhr){
			if(callback)
				callback(data, status, xhr);
			else
				$("#menu_right").html(data);
		},
		error: function(data, status, xhr){
			$("#menu_right").html('<div class="error">エラーが発生しました。</div>');
		}
	});
}
function getGadget(type){
	return gadgetConfs[type];
}
function getGadgetTitle(gadget){
	if(typeof gadget == "string")
		gadget = getGadget(gadget);
	return gadget.title
		 || gadget.ModulePrefs.directory_title
		 || gadget.ModulePrefs.title
		 || gadget.type;
}
function getIconUrl(type){
	if(!type)
		return imageURL + "manager/bullet_black.gif";
	var gadget = getGadget(type);
	try{
		if(gadget.icon)
			return imageURL + gadget.icon;
		else if(gadget.ModulePrefs.Icon.content)
			var icon = gadget.ModulePrefs.Icon.content;
			var realType = type.replace("upload__","");
			return icon.replace("__IS_GADGET_BASE_URL__", hostPrefix + '/gadget/' + realType).replace("__IS_IMAGE_URL__", imageURL);
	}catch(e){
		return imageURL + "widget_add.gif";
	}
}
function selectItem(menuId){
	$("#menu_tree").jstree("deselect_all");
	$("#menu_tree").jstree("select_node", "#"+menuId);
}
function domId2DBId(id){
	return id.replace(/^menuId_/, "");
}
function getSelectedItem(){
	return $("#menu_tree").jstree("get_selected")[0];
}
function getSelectedItemId(selectedItem){
	if(!selectedItem)
		return "";
	return domId2DBId(selectedItem.id);
}
function selectGadgetInstance(e, a, isTop){
	if(isTop)
		$("#menu_tree").jstree("deselect_all");
	var selectedItem = getSelectedItem(),
		id = getSelectedItemId(selectedItem);
	$loadContent("selectGadgetInstance", {id:id});
}
function selectGadgetType(){
	var selectedItem = getSelectedItem();
	var id = getSelectedItemId(selectedItem);
	$loadContent("selectGadgetType", {id:id});
}
function showAddItem(isTop, type, title, parentId){
	if(!parentId){
		if(isTop){
			parentId = "";
		}else{
			var selectedItem = getSelectedItem();
			parentId = getSelectedItemId(selectedItem);
		}
	}
	$loadContent("showAddItem", {menuId: menuId, id: parentId, type: type?type:"", title: title?title:""});
}
function showEditItem(){
	var selectedItem = getSelectedItem();
	if(!selectedItem){
		alert("<spring:message code="menu.editMenu.invalid.operation" />");
		return;
	}
	var id = getSelectedItemId(selectedItem);
	$loadContent("showEditItem", {menuId:id});
}
function showEditInstance(instanceId, parentId){
	$loadContent("showEditInstance", {instanceId:instanceId, id:parentId});
}
function addItemToTree(parentId, id, title, type, accessLevel){
	$("#menu_tree").jstree("create",
		parentId ? "#menuId_"+parentId : "#menuId_",
		"last",
		{
			attr : { id : "menuId_"+id},
			data : title
		},
		function(target){
			target.find("a:first").append('<span onclick="showMenuCommand(event, this, \'menuId_'+id+'\')" class="menu_open" title="<spring:message code="menu.editPage.open.menu" />">▼</span>');
			target.append('<div class="info"><span class="publish'+(accessLevel?'"><spring:message code="menu.editPage.publish" />':' un"><spring:message code="menu.editPage.unpublish" />')+'</span></div>');
			var icon = getIconUrl(type);
			$("a:first ins", target)
				.css("display", "inline-block")
				.css("background", "url("+icon+")");
		},
		true
	);
}
function updateItemInTree(id, title, accessLevel){
	try{
		/*var titleNode = $("#" + id + " a").contents().filter(function() { return this.nodeType == 3; })[0];
		titleNode.nodeValue = title;*/
		$("#menu_tree").jstree("set_text", "#menuId_"+id, title);
		var publishElm = $("#menuId_"+id+" .info span.publish").first();
		publishElm.toggleClass("un", !accessLevel).html(accessLevel? "<spring:message code="menu.editPage.publish" />":"<spring:message code="menu.editPage.unpublish" />");
	}catch(e){
		console.error(e);
	}
}
function copyItem(e, a){
	$("#menu_item_command .paste").removeClass("disabled");
	var item = getSelectedItem();
	copiedItemId = getSelectedItemId(item);
	var title = $("#menu_tree").jstree("get_text", item);
	$("#menu_right").html('<div class="success">「'+title+'」をコピーしました。</div>');
}
function pasteItem(a){
	if($(a).hasClass("disabled") || !copiedItemId) return;
	var id = getSelectedItemId(getSelectedItem());
/*
	$loadContent("copyItem", {parentId:id, id:copiedItemId}, function(data){
		addItemToTree(data.parentId, data.id, data.title, data.type, data.accessLevel);
		$("#menu_right").html('<div class="success">「'+data.title+'」をコピーしました。</div>');
	});
*/
	$.post("copyItem",  {parentId:id, id:copiedItemId}, function(data){
		alert(data.title);
		addItemToTree(data.parentId, data.id, data.title, data.type, data.accessLevel);
		$("#menu_right").html('<div class="success">「'+data.title+'」をコピーしました。</div>');
	});
}
function deleteItem(){
	if(confirm("<spring:message code="menu.editPage.confirm.delete" />")){
		var id = getSelectedItemId(getSelectedItem());
		$loadContent("removeItem", {id: id}, function(html){
			$("#menu_tree").jstree("remove", "#menuId_"+id);
			$("#menu_right").html(html);
		});
	}
}
function togglePublish(){
	var id = getSelectedItemId(getSelectedItem());
	$loadContent("togglePublish", {id: id}, function(html){
		var publishElm = $("#menuId_"+id+" .info span.publish").first();
		var accessLevel = publishElm.hasClass('un');//現在の反対にする
		publishElm.toggleClass("un", !accessLevel).html(accessLevel? "<spring:message code="menu.editPage.publish" />":"<spring:message code="menu.editPage.unpublish" />");
		$("#menu_right").html(html);
	});
}
function showMenuCommand(e, link, menuId, isTop){
	selectItem(menuId);
	$("#menu_item_command")
		.toggleClass("top", !!isTop)
		.css("top", $(link).position().top + $(link).height())
		.css("left", $(link).position().left)
		.show();
	if (!e) var e = window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();

}

$(function () {
	var menuTree = $("#menu_tree").jstree({
		"html_data" : {
			"ajax" : {
				"url" : "tree?id="+menuId
			}
		},
		"themes" : {
			"icons" : false
		},
		"ui" : {
			"select_limit" : 1
		},
		"core" : {
			"initially_open" : [ "menuId_" ],
			"animation" : 100
		},
		"crrm" : {
			"move" : {
				"check_move": function(m){
					var p = this._get_parent(m.o);
					//p: original parent, m.np: parent of moved item
					//deny dropping child item to top
					if(p != -1 && m.np === this.get_container())
						return false;
					//deny dropping top item to child
					if(p === -1 && m.np != this.get_container())
						return false;
					return true;
				}
			}
		},
		"plugins" : [ "themes", "html_data", "crrm", "dnd", "ui" ]
	});
	menuTree.bind("move_node.jstree", function(event, data){
		var node = data.rslt.o,
			nodeId = domId2DBId(node.attr("id")),
			parentNode = data.inst._get_parent(node),
			refNode = data.rslt.r,
			refId = refNode ? refNode.attr("id") : "",
			position = data.rslt.p;//last, after, before
		var ids = [];
		node.siblings().each(function(){
			if(this.id == refId && position == "before")
				ids.push(nodeId);
			ids.push(domId2DBId(this.id));
			if(this.id == refId && position == "after")
				ids.push(nodeId);
		});
		if(position == "last")
			ids.push(nodeId);
		$loadContent("moveItem",
			{
				id: nodeId,
				sibling: ids,
				parentId: parentNode.attr ? domId2DBId(parentNode.attr("id")) : ""
			}
		);
	});
	$("#menu_command a").button();
	function resizeMenuTree(){
		var baseHeight = $(window).height() - $("#footer").height() - 13;
		var height = baseHeight - $("#menu_tree").offset().top;
		$("#menu_tree").css("height", height);
		height = baseHeight - $("#menu_right").offset().top;
		$("#menu_right").css("height", height);
	}
	$(window).resize(resizeMenuTree);
	resizeMenuTree();
	$(document.body).click(function(){
		$("#menu_item_command").hide();
	});
	
	
	//ガジェット設定を読み込む
	$.post("getGadgetConf", null, function(json){
		gadgetConfs = json;
		//TODO 以下の処理はサーバーサイドでやりたい
		$.each(gadgetConfs, function(type, gadget){
			gadgetConfs[type].type = type;
		});
		$("li", menuTree).each(function(){
			var icon = getIconUrl(this.type);
			$("a ins", this).first()
				.css("display", "inline-block")
				.css("background", "url("+icon+")");
		});
	});
});

</script>
<div class="footprint"><a href="./index">メニュー一覧に戻る</a></div>
<div id="menu">
	<div id="menu_left">
		<!--
		<div id="menu_command">
			<a onclick="showAddItem(true)"><spring:message code="menu.editPage.add.top" /></a>
		</div>
		-->
		<div id="menu_tree">
			
		</div>
	</div>
	<div id="menu_right">
		<spring:message code="menu.editPage.description" /><br>
	</div>
	<div style="clear:both"></div>
	<div id="menu_item_command" class="menu_item_command" style="display:none">
		<ul>
			<li><a onclick="showAddItem(false)"><spring:message code="menu.editPage.command.add.link" /></a></li>
			<li><a onclick="selectGadgetType()"><spring:message code="menu.editPage.command.add.gadget" /></a></li>
			<li class="child_only"><a onclick="showEditItem(event, this)"><spring:message code="menu.editPage.command.edit" /></a></li>
			<li class="child_only"><a onclick="copyItem(event, this)"><spring:message code="menu.editPage.command.copy" /></a></li>
			<li class="child_only"><a onclick="pasteItem(event, this)" class="paste disabled"><spring:message code="menu.editPage.command.paste" /></a></li>
			<li class="child_only"><a onclick="deleteItem(event, this)"><spring:message code="menu.editPage.command.delete" /></a></li>
			<li class="child_only"><a onclick="togglePublish(event, this)"><spring:message code="menu.editPage.command.publish" /></a></li>
			<!--li><a onclick=""><spring:message code="menu.editPage.command.access" /></a></li-->
		</ul>
	</div>
	<div id="select_role_dialog">
	</div>
</div>
	</tiles:putAttribute>
</tiles:insertDefinition>
