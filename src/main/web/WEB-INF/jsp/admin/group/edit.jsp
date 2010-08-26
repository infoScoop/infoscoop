<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="group"/>
	<tiles:putAttribute name="title" value="group.title"/>
	<tiles:putAttribute name="body" type="string">
<script type="text/javascript" class="source">
//
$(function () {

	$("#addButton").click(function(){
		var count = (table).rows.length;
		var tbl = document.getElementById("table");
		var row = tbl.insertRow(count);
		var cell1 = row.insertCell(0);
		var cell2 = row.insertCell(1);
		var cell3 = row.insertCell(2);

		count--;
		cell1.innerHTML = "<select id='rolePrincipals["+ count +"].type' name='rolePrincipals[" + count + "].type'><option value='ユーザ'>ユーザ</option><option value='組織'>組織</option></select>";
		cell2.innerHTML = "<input id='rolePrincipals[" + count + "].name' name='rolePrincipals[" + count + "].name' type='text' value=''/>";
		cell3.innerHTML = "<span class='trash'></span>";

	});
});

function deletePrincipal(index){
	$(document.forms[0]['rolePrincipals[' + index + '].id']).name="deletePrincipalId";
	$(document.forms[0]['rolePrincipals[' + index + '].name']).remove();
	$(document.forms[0]['rolePrincipals[' + index + '].type']).remove();
	document.forms[0].action = 'deleteRolePrincipal';
	document.forms[0].submit();
	//window.location.href = "deleteRolePrincipal?rolePrincipalId="+ rolePrincipalId +"&roleId="+ roleId;
}

</script>

<div>
	<h3>グループ設定画面</h3>
	<form:form modelAttribute="role" id="add_group" method="post" action="save">
		<c:set var="principalSize" value="${role.size}" />
		<h2>グループ名： <form:input path="name"/></h2>
		<form:hidden path="id" />
		<table id="table" class="tab_table" cellspacing="0" cellpadding="0">
		<thead>
			<tr>
				<th>タイプ</th>
				<th>対象範囲</th>
				<th>削除</th>
			</tr>
		</thead>
		<tfoot></tfoot>
		<tbody>
		<c:forEach var="principalId" items="${role.deletePrincipalIdList}" varStatus="status">
			<input name="deletePrincipalIdList[<c:out value='${status.index}'/>]" type="hidden" value="${principalId}" />
		</c:forEach>

		<c:forEach var="principal" items="${role.rolePrincipals}" varStatus="status">
			<tr>
				<td>
					<span>${principal.type}</span>
					<input name="rolePrincipals[<c:out value='${status.index}'/>].type" type="hidden" value="${principal.type}" />
				</td>
				<td>
					<span>${principal.name}</span>
					<input name="rolePrincipals[<c:out value='${status.index}'/>].name" type="hidden" value="${principal.name}" />
				</td>
				<td>
					<span class="trash"  onclick="deletePrincipal(${status.index})"></span>
					<input name="rolePrincipals[<c:out value='${status.index}'/>].id" type="hidden" value="${principal.id}" />
				</td>
			</tr>
		</c:forEach>

		</table>
		<input id="addButton" type="button" value="追加" />
		<input type="submit" name="button" value="保存" />
		<input type="button" value="キャンセル" onclick="javascript:window.location.href='index'" />
	</form:form>
</div>



	</tiles:putAttribute>
</tiles:insertDefinition>