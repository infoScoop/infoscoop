<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="group"/>
	<tiles:putAttribute name="title" value="group.title"/>
	<tiles:putAttribute name="body" type="string">

<script type="text/javascript" class="source">
$(function(){
	$('#add_button').click(function(){
		window.open("editGroup", "グループ設定", 'width=800, height=600, menubar=no, toolbar=no, scrollbars=yes');
	});

	$('.edit').click(function(){
		var roleID = this.parentNode.parentNode.id;
		window.open("updateGroup?id="+ roleID,"グループ編集画面", 'width=800, height=600, menubar=no, toolbar=no, scrollbars=yes');
	});
/*
	$('.trash').click(function(){
		var roleID = this.parentNode.parentNode.id;
		alert("このグループを削除しますか？");

	});
	*/
});
function deleteRole(roleId){
	window.location.href = "delete?roleId=" +  roleId;
}
</script>

<div style="height:500px;">
	<input id="add_button" type="button" value="追加"/>
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
						<td rowspan="${principalSize}"><span class="edit">編集アイコン</span></td>
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