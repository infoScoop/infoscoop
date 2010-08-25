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
	$("#add_group").submit(function(){
		setTimeout(window.opener.location.reload(), 30000, true);
	});

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

function deletePrincipal(rolePrincipalId, roleId){
	window.location.href = "deleteRolePrincipal?rolePrincipalId="+ rolePrincipalId +"&roleId="+ roleId;
}

</script>

<div>
	<h3>グループ設定画面</h3>
	<form:form modelAttribute="role" id="add_group" method="post" action="update">
		<c:set var="principalSize" value="${role.size}" />
		<h2>グループ名： ${role.name}<form:hidden path="name"/></h2>
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
					<span class="trash"  onclick="deletePrincipal('${principal.id}', '${role.id}')"></span>
					<input name="rolePrincipals[<c:out value='${status.index}'/>].id" type="hidden" value="${principal.id}" />
				</td>
			</tr>
		</c:forEach>

		</table>
		<input id="addButton" type="button" value="追加" />
		<input type="submit" name="button" value="保存" />
		<input type="button" value="閉じる" onclick="window.close()" />
	</form:form>
</div>



	</tiles:putAttribute>
</tiles:insertDefinition>