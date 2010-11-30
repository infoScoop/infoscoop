<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="role"/>
	<tiles:putAttribute name="title" value="role.title"/>
	<tiles:putAttribute name="body" type="string">
<script type="text/javascript" class="source">
//
$(function () {
	$("#addButton").click(function(){
		var tbl = document.getElementById("table");
		var count = tbl.rows.length;
		var row = tbl.insertRow(count);
		var cell1 = row.insertCell(0);
		var cell2 = row.insertCell(1);
		var cell3 = row.insertCell(2);

		count--;
		cell1.innerHTML = "<span>"+ $("#roleType").val() +"</span><input name='rolePrincipals["+ count +"].type' type='hidden' value='"+ document.getElementById("roleType").value +"' />";
		cell2.innerHTML = "<span>"+ $("#principalId").val() +"</span><input name='rolePrincipals["+ count +"].name' type='hidden' value='"+ document.getElementById("target").value +"' />";
		cell3.innerHTML = "<span class='trash' onclick='deletePrincipal("+ count +")'></span><input name='rolePrincipals["+ count +"].id' type='hidden' value='' />";

		document.getElementById("roleType").value = "ユーザ";
		document.getElementById("target").value = "";
	});

	$(function() {
		var params = {
			output:  'json',
			results: 5,
			query:   undefined
		};
		var onselect = function(event, ui) {
			if(ui.item){
				setTimeout(function(){
					$('#target').val(ui.item.label);
					$('#principalId').val(ui.item.value);
				},1);
			}
		}
		$('#target').autocomplete( { source:
			function ( request, response )  {
				params.query = request.term;
				var type = document.getElementById("roleType").value;
				if (type == "UIDPrincipal")
					var url = 'autocompleteUser';
				else if (type == "OrganizationPrincipal")
					var url = 'autocompleteGroup';
				$.post( url, params, response, 'json');
			},
			select: onselect,
			change: onselect,
			focus: onselect
		});
	});
});

function deletePrincipal(index){
	$(document.forms[0]['rolePrincipals[' + index + '].id']).name="deletePrincipalId";
	$(document.forms[0]['rolePrincipals[' + index + '].name']).remove();
	$(document.forms[0]['rolePrincipals[' + index + '].type']).remove();
	document.forms[0].action = 'deleteRolePrincipal';
	document.forms[0].submit();
	//window.location.href = "deleteRolePrincipal?rolePrincipalId="+ rolePrincipalId +"&roleId="+ roleId;
};

function checkForm(){
	if(document.getElementById("roleName").value == ""){
		alert('グループ名を入力してください');
		document.getElementById("roleName").focus();
		return false;
	} else if(document.getElementById("table").rows.length == 1){
		alert("\"ユーザ\" または \"組織\"を追加してください。");
		document.getElementById("target").focus();
		return false;
	}
};


</script>

<div>
	<h3>グループ設定画面</h3>
	<form:form modelAttribute="role" id="add_group" method="post" action="save" onSubmit="return checkForm()" >
		<c:set var="principalSize" value="${role.size}" />
		<p>グループ名： <form:input id="roleName" path="name"/></p>
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
					<span><spring:message code="role.index.principal.type.${principal.type}"/></span>
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
		<input type="submit" name="button" value="保存" />
		<input type="button" value="キャンセル" onclick="javascript:window.location.href='index'" />
		<br />
		<br />


		<span>タイプ：</span>
		<select id="roleType">
			<option value='UIDPrincipal'>
				<spring:message code="role.index.principal.type.UIDPrincipal"/>
			</option>
			<option value='OrganizationPrincipal'>
				<spring:message code="role.index.principal.type.OrganizationPrincipal"/>
			</option>
		</select>
		<span>　　対象範囲：</span>
		<input id="principalId" type="hidden"></input>
		<input id="target" type="text"></input>

		<input id="addButton" type="button" value="追加" />

		<br />
		<br />
		<br />

	</form:form>
</div>



	</tiles:putAttribute>
</tiles:insertDefinition>