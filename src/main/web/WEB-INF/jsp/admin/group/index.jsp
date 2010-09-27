<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="group"/>
	<tiles:putAttribute name="title" value="group.title"/>
	<tiles:putAttribute name="body" type="string">

<script type="text/javascript" class="source">
function sync(){
	window.location.href = "save";
}
</script>

<div>
	<input id="syncButton" type="button" value="同期" onclick="sync()"/>
	<table id="tab_table" class="tab_table" cellspacing="0" cellpadding="0">
		<thead>
			<tr>
				<th>名前</th>
				<th>編集</th>
				<th>削除</th>
			</tr>
		</thead>
		<tfoot></tfoot>
		<tbody>

		<c:forEach var="group" items="${groups}">
			<tr id="${group.id}">
				<td id="${group.id}">${group.name}</td>
				<td><span class="edit"><a href="edit?id=${group.id}">編集アイコン</a></span></td>
				<td><span class="trash"  onclick="deleteRole('${group.id}')" ></span></td>
			</tr>
		</c:forEach>

		</tbody>
	</table>

</div>
	</tiles:putAttribute>
</tiles:insertDefinition>