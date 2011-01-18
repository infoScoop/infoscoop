<%@ page contentType="text/html; charset=UTF-8" %>
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
			<th width="18">&nbsp;</th>
			<th><spring:message code="menu.index.header.name" /></th>
			<th>説明</th>
			<th>公開</th>
			<th>公開範囲</th>
			<!--<th width="100"><spring:message code="menu.index.header.top" /></th>-->
			<th width="100"><spring:message code="menu.index.header.side" /></th>
			<th width="50"><spring:message code="tab.index.edit" /></th>
			<th width="50"><spring:message code="menu.index.header.delete" /></th>
		</tr>
	</thead>
	<tbody>
		<c:set var="display"><spring:message code="menu.index.display" /></c:set>
		<c:forEach var="menu" items="${menus}">
		<tr id="menuId_${menu.id}">
			<td align="center"><div class="sort_handle icon" title="ドラッグして順番変更"></div></td>
			<td class="title">
				<a href="editMenu?id=${menu.id}" title="メニューツリーを編集">${menu.title}</a>
				<input type="text" value="${menu.title}" style="display:none">
			</td>
			<td class="title">
				${menu.description}
			</td>
			<td><spring:message code="tab.index.publish${menu.publish}"/></td>
			<td>
				<c:forEach var="role" items="${menu.roles}">
					${role.name}
				</c:forEach>
			</td>
			<!--
			<td class="radio_cell">
				<span>${menu.topPos ? display : ""}</span>
			</td>
			-->
			<td class="radio_cell">
				<span>${menu.sidePos ? display : ""}</span>
			</td>
			<td class="icon_cell"><a href="showEditTree?id=${menu.id}" class="edit_link"><div class="edit icon" title="編集"></div></a></td>
			<td class="icon_cell"><div class="icon trash" menu_id="${menu.id}" title="削除"></div></td>
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
	location.href = "showEditTree";
});
$("#menu_list").tablesorter({
	headers: {
		0:{sorter:false},
		1:{sorter:false},
		2:{sorter:false},
		3:{sorter:false},
		4:{sorter:false},
		5:{sorter:false},
		6:{sorter:false},
		7:{sorter:false}
	}
});
$("#menu_list .trash").livequery("click", function(){
	if(confirm("<spring:message code="menu.index.confirm.delete" />")){
		location.href = "deleteMenu?id="+$(this).attr("menu_id");
	}
});
$("#menu_list tbody").sortable({
	axis:"y",
	handle:".sort_handle",
	update:function(event, ui){
		$.ajax({
			url: "sort",
			type:"POST",
			data: $("#menu_list tbody").sortable("serialize", {key:"menuId"}),
			success: function(data, status, xhr){
			},
			error: function(xhr, status, e){
				//TODO: handle error
			}
		});
	}
});
</script>
	</tiles:putAttribute>
</tiles:insertDefinition>