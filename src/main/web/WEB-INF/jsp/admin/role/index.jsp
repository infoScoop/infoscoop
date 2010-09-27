<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="role"/>
	<tiles:putAttribute name="title" value="role.title"/>
	<tiles:putAttribute name="body" type="string">

<script type="text/javascript" class="source">
function deleteRole(roleId){
	window.location.href = "delete?roleId=" +  roleId;
}
</script>

<div style="height:500px;">
	<a href="edit" id="add_button" class="button">追加</a>
	<table id="tab_table" class="tab_table" cellspacing="0" cellpadding="0">
		<thead>
			<tr>
				<th>名前</th>
				<th>タイプ</th>
				<th>対象範囲</th>
				<th>編集</th>
				<th>削除</th>
			</tr>
		</thead>
		<tfoot></tfoot>
		<tbody>

		<c:forEach var="role" items="${roles}">
			<c:set var="principalSize" value="${role.size}" />
			<c:forEach var="principal" items="${role.rolePrincipals}" varStatus="status">
				<tr id="${role.id}">
					<c:if test="${status.index == 0}">
	 					<td id="${role.id}" rowspan="${principalSize}">${role.name}</td>
 					</c:if>
					<td>${principal.type}</td>
					<td>${principal.name}</td>
					<c:if test="${status.index == 0}">
						<td rowspan="${principalSize}"><span class="edit"><a href="edit?id=${role.id}">編集アイコン</a></span></td>
						<td rowspan="${principalSize}"><span class="trash"  onclick="deleteRole('${role.id}')" ></span></td>
 					</c:if>
				</tr>
			</c:forEach>
		</c:forEach>

		</tbody>
	</table>

</div>
	</tiles:putAttribute>
</tiles:insertDefinition>