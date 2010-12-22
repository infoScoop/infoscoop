<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="role"/>
	<tiles:putAttribute name="title" value="role.title"/>
	<tiles:putAttribute name="body" type="string">
<script type="text/javascript" class="source">
function deleteRole(roleId){
	window.location.href = "delete?roleId=" +  roleId;
}
$(function () {
	$("#add_button").button();
});
</script>

<div style="height:500px;">
	<p>
		メニューやタブの公開範囲を限定する場合にこの画面で作成した役割グループを割り当てます。<br>
		役割グループにはGoogle Appsのユーザ/グループを追加することができます。
	</p>
	<a href="edit" id="add_button"><div class="add label_icon">追加</div></a>
	<table class="tablesorter">
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
					<td><spring:message code="role.index.principal.type.${principal.type}"/></td>
					<td>${principal.name}</td>
					<c:if test="${status.index == 0}">
						<td rowspan="${principalSize}"><a href="edit?id=${role.id}"><div class="edit icon" title="編集"></div></a></td>
						<td rowspan="${principalSize}"><div class="trash icon"  onclick="deleteRole('${role.id}')" ></div></td>
 					</c:if>
				</tr>
			</c:forEach>
		</c:forEach>

		</tbody>
	</table>

</div>
	</tiles:putAttribute>
</tiles:insertDefinition>