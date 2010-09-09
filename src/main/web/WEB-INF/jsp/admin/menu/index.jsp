<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="menu"/>
	<tiles:putAttribute name="title" value="menu.title"/>
	<tiles:putAttribute name="body" type="string">
<p>
	この画面では、メニューツリーを追加/編集/削除できます。<br>
	トップに表示、サイドに表示を選択した場合のみ、そのメニューがポータル画面に表示されます。<br>
</p>
<button id="add_menu">メニューを追加</button>
<table id="menu_list" class="tablesorter">
	<thead>
		<tr>
			<th>名前</th>
			<th width="100">トップに表示<div class="icon edit_icon" id="change_position_top"></div></th>
			<th width="100">サイドに表示<div class="icon edit_icon" id="change_position_side"></div></th>
			<th width="100">削除</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="menu" items="${menus}">
		<tr menu_id="${menu.id}">
			<td class="title">
				<a href="editMenu?id=${menu.id}">${menu.title}</a>
				<input type="text" value="${menu.title}" style="display:none">
				<div class="icon edit_icon">
			</td>
			<td class="radio_cell">
				<span>${menu.top ? "表示" : ""}</span>
				<input type="radio" name="top" ${menu.top ? "checked=\"checked\"" : ""} style="display:none">
			</td>
			<td class="radio_cell">
				<span>${menu.side ? "表示" : ""}</span>
				<input type="radio" name="side" ${menu.side ? "checked=\"checked\"" : ""} style="display:none">
			</td>
			<td class="icon_cell"><div class="icon delete_icon" menu_id="${menu.id}"></div></td>
		</tr>
		</c:forEach>
		<c:if test="${fn:length(menus) == 0}">
		<tr>
			<td colspan="4">メニューを追加してください。</td>
		</tr>
		</c:if>
	</tbody>
</table>
<script type="text/javascript">
$("#add_menu").button().click(function(){
	$.post("newMenu", {}, function(html){
		var tbody = $("#menu_list tbody");
		//if a count of menu trees is 0, a message should be deleted.
		if(!$("tr:first", tbody).attr("menu_id"))
			tbody.empty();
		tbody.append(html);
	});
});
$("#menu_list").tablesorter({
	headers: {1:{sorter:false},2:{sorter:false},3:{sorter:false}}
});
$("#menu_list .title .icon").livequery("click", function(event){
	var icon = $(this);
	if(icon.hasClass("edit_icon")){
		icon.prev().show().focus().prev().hide();
		icon.removeClass("edit_icon").addClass("save_icon");
		event.stopPropagation();
	} else {
		var title = icon.prev().val();
		var menuId = icon.parents("tr:first").attr("menu_id");
		$.post("saveTitle", {id:menuId, title:title}, function(html){
			icon.prev().hide().prev().text(title).show();
			icon.removeClass("save_icon").addClass("edit_icon");
			console.info(html);
		});
	}
});
$("#menu_list input").livequery("click", function(event){
	event.stopPropagation();
});
$("#change_position_top, #change_position_side").click(function(event){
	var name = this.id === "change_position_top" ? "top" : "side",
		icon = $(this),
		radios = $("#menu_list td.radio_cell input[name="+name+"]");
	if(icon.hasClass("edit_icon")){
		radios.show().prev().hide();
		icon.removeClass("edit_icon").addClass("save_icon");
	} else {
		var menuId = radios.filter(":radio:checked").parents("tr:first").attr("menu_id");
		$.post("changePosition", {id:menuId, position:name}, function(html){
			radios.hide().prev().each(function(){
				if($(this).parents("tr:first").attr("menu_id") === menuId) $(this).text("表示").show();
				else $(this).empty().show();
			});
			icon.removeClass("save_icon").addClass("edit_icon");
			console.info(html);
		});
	}
	event.stopPropagation();
});
$("#menu_list .delete_icon").livequery("click", function(){
	var deleteIcon = $(this);
	if(confirm("削除してよろしいですか？")){
		$.post("deleteMenu", {id:deleteIcon.attr("menu_id")}, function(html){
			deleteIcon.parents("tr:first").remove();
			console.info(html);
		});
	}
});
$(document.body).click(function(){
	$("#menu_list .title a").each(function(){
		var a = $(this);
		a.show().next().hide().val(a.text());
	});
	$("#menu_list td.radio_cell span").each(function(){
		var span = $(this);
		span.show().next().hide().attr("checked", span.text() ? "checked":null);
	});
	$("#menu_list .icon.save_icon").removeClass("save_icon").addClass("edit_icon");
});
</script>
	</tiles:putAttribute>
</tiles:insertDefinition>