<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="menu"/>
	<tiles:putAttribute name="title" value="menu.title"/>
	<tiles:putAttribute name="body" type="string">
<p>
	<spring:message code="menu.index.description" />
</p>
<button id="add_menu"><spring:message code="menu.index.add.menu" /></button>
<table id="menu_list" class="tablesorter">
	<thead>
		<tr>
			<th><spring:message code="menu.index.header.name" /></th>
			<th width="100"><spring:message code="menu.index.header.top" /><div class="icon edit_icon" id="change_position_top"></div></th>
			<th width="100"><spring:message code="menu.index.header.side" /><div class="icon edit_icon" id="change_position_side"></div></th>
			<th width="100"><spring:message code="menu.index.header.delete" /></th>
		</tr>
	</thead>
	<tbody>
		<c:set var="display"><spring:message code="menu.index.display" /></c:set>
		<c:forEach var="menu" items="${menus}">
		<tr menu_id="${menu.id}">
			<td class="title">
				<a href="editMenu?id=${menu.id}">${menu.title}</a>
				<input type="text" value="${menu.title}" style="display:none">
				<div class="icon edit_icon">
			</td>
			<td class="radio_cell">
				<span>${menu.top ? display : ""}</span>
				<input type="radio" name="top" ${menu.top ? "checked=\"checked\"" : ""} style="display:none">
			</td>
			<td class="radio_cell">
				<span>${menu.side ? display : ""}</span>
				<input type="radio" name="side" ${menu.side ? "checked=\"checked\"" : ""} style="display:none">
			</td>
			<td class="icon_cell"><div class="icon delete_icon" menu_id="${menu.id}"></div></td>
		</tr>
		</c:forEach>
		<c:if test="${fn:length(menus) == 0}">
		<tr>
			<td colspan="4"><spring:message code="menu.index.no.menu" /></td>
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
			//console.info(html);
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
			//console.info(html);
		});
	}
	event.stopPropagation();
});
$("#menu_list .delete_icon").livequery("click", function(){
	var deleteIcon = $(this);
	if(confirm("<spring:message code="menu.index.confirm.delete" />")){
		$.post("deleteMenu", {id:deleteIcon.attr("menu_id")}, function(html){
			deleteIcon.parents("tr:first").remove();
			//console.info(html);
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