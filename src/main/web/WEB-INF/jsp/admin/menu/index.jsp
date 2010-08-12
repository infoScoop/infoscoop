<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="menu"/>
	<tiles:putAttribute name="title" value="menu.title"/>
	<tiles:putAttribute name="body" type="string">
<script type="text/javascript" src="../../js/lib/jquery.js"></script>
<script type="text/javascript" src="../../js/lib/jsTree.v.1.0rc2/jquery.jstree.js"></script>
<script type="text/javascript" class="source">
function selectItem(id){
	$("#menu_tree").jstree("deselect_all");
	$("#menu_tree").jstree("select_node", "#"+id);
}
function getSelectedItem(){
	return $("#menu_tree").jstree("get_selected")[0];
}
function showAddItem(){
	var selectedItem = getSelectedItem();
	var id = selectedItem ? selectedItem.id : "";
	$.get("showAddItem", {id:id}, function(html){
		$("#menu_right").html(html);
	})
	$("#menu_item_command").hide();
}
function addItemToTree(parentId, id, title){
	$("#menu_tree").jstree("create",
		parentId ? "#"+parentId : -1,
		"last",
		{
			attr : { id : id},
			data : title
		},
		function(target){
			target.append('<span onclick="showMenuCommand(event, this, \''+id+'\')" class="menu_open">▼</span>');
		},
		true
	);
}
function deleteItem(id){
	var id = getSelectedItem().id;
	$.post("removeItem", {id: id}, function(){
		$("#menu_tree").jstree("remove", "#"+id);
	});
	$("#menu_item_command").hide();
}
function showMenuCommand(event, link, id){
	selectItem(id);
	$("#menu_item_command").css("top", $(link).position().top + $(link).height());
	$("#menu_item_command").css("left", $(link).position().left);
	$("#menu_item_command").show();
	event.stopPropagation();
}
$(function () {
	$("#menu_tree").jstree({
		"html_data" : {
			"ajax" : {
				"url" : "tree"
			}
		},
		"ui" : {
			"select_limit" : 1
		},
		"plugins" : [ "themes", "html_data", "crrm", "dnd", "ui" ]
	});
	$("#menu_tree").bind("move_node.jstree", function(event, data){
		var node = data.rslt.o;
		var parentNode = data.inst._get_parent(node);
		$.post("moveItem",
			{
				id: node.attr("id"),
				parentId: parentNode.attr ? parentNode.attr("id") : ""
			},
			function(response){
			}
		);
	});
	function resizeMenuTree(){
		var height = $(window).height() - $("#menu_tree").offset().top - $("#footer").height();
		$("#menu_tree").css("height", height);
		$("#menu_right").css("height", height);
	}
	$(window).resize(resizeMenuTree);
	resizeMenuTree();
	$(document.body).click(function(){
		$("#menu_item_command").hide();
	});
});
</script>
<div id="menu">
	<div id="menu_left">
		<div id="menu_command">
			<a onclick="showAddItem()">トップメニューを追加</a>
		</div>
		<div id="menu_tree">
			
		</div>
	</div>
	<div id="menu_right">
		aaaaaaaaa
	</div>
	<div style="clear:both"></div>
	<div id="menu_item_command" class="menu_item_command" style="display:none">
		<ul>
			<li><a onclick="showAddItem()">追加</a></li>
			<li><a onclick="deleteItem()">編集</a></li>
			<li><a onclick="deleteItem()">削除</a></li>
			<li><a onclick="deleteItem()">公開/非公開を切り替える</a></li>
			<li><a onclick="deleteItem()">公開範囲を設定する</a></li>
		</ul>
	</div>
</div>
	</tiles:putAttribute>
</tiles:insertDefinition>