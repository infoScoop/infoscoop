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
function showAddItem(id){
	selectItem(id);
	$("#menu_right").load("showAddItem", {id : id});
}
function addItem(){
	var params = {};
	$("#add_item :input").each(function(){
		params[this.name] = this.value;
	});
	$.post("addItem", params, function(html){
		$("#menu_right").html(html);
	});
}
function addItemToTree(parentId, id, title){
	$("#menu_tree").jstree("create", "#"+parentId, "last",
		{
			attr : { id : id},
			data : title
		}, function(target){
			target.append('<div class="menu_command" onclick="showAddItem(\''+id+'\')">追加</div>');
			target.append('<div class="menu_command" onclick="deleteItem(\''+id+'\')">削除</div>');
		},
	true);
}
function deleteItem(id){
	selectItem(id);
	$.post("removeItem", {id: id}, function(){
		$("#menu_tree").jstree("remove", "#"+id);
	});
}
$(function () {
	$("#menu_tree").jstree({
		"html_data" : {
			"ajax" : {
				"url" : "data",
				"data" : function (n) {
					return { id : n.attr ? n.attr("id") : 0 }; 
				}
			}
		},
		"ui" : {
			"select_limit" : 1
		},
		"plugins" : [ "themes", "html_data", "crrm", "dnd", "ui" ]
	});
	function resizeMenuTree(){
		var height = $(window).height() - $("#menu_tree").offset().top - $("#footer").height();
		$("#menu_tree").css("height", height);
		$("#menu_right").css("height", height);
	}
	$(window).resize(resizeMenuTree);
	resizeMenuTree();
});
</script>
<div id="menu">
	<div id="menu_tree">
		
	</div>
	<div id="menu_right">
		aaaaaaaaa
	</div>
	<div style="clear:both"></div>
</div>
	</tiles:putAttribute>
</tiles:insertDefinition>