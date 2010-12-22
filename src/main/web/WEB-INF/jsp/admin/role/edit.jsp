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
$(function () {
	$("#add_button").button().click(function(){
		$("#search_dialog").slideToggle("fast");
		return false;
	});
	$("#add_user").click(function(){
		var count = $("#table tbody tr").length;
		$('<tr/>')
			.append($('<td/>')
				.append($('<span/>').text($("#roleType option:selected").text()))
				.append($('<input type="hidden"/>')
					.attr("name","rolePrincipals["+count+"].type")
					.val($('#roleType').val())
				)
			).append($('<td/>')
				.append($('<span/>').text($("#target").val()))
				.append($('<input type="hidden"/>')
					.attr("name","rolePrincipals["+count+"].name")
					.val($('#principalId').val())
				)
			).append($('<td align="center"/>')
				.append($('<div class="trash icon" onclick="deletePrincipal('+count+')"/>'))
				.append($('<input type="hidden" value=""/>')
					.attr("name","rolePrincipals["+count+"].id")
				)
			)
			.appendTo($("#table tbody"));

		$("#roleType").val("ユーザ");
		$("#target").val("");
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
				var type = $("#roleType").val();
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
	
	$("#add_group :submit, #add_group :button").button();
});

function deletePrincipal(index){
	var form = document.forms[0];
	$(form['rolePrincipals[' + index + '].id']).name="deletePrincipalId";
	$(form['rolePrincipals[' + index + '].name']).remove();
	$(form['rolePrincipals[' + index + '].type']).remove();
	form.action = 'deleteRolePrincipal';
	form.submit();
};

function checkForm(){
	if($("#roleName").val() == ""){
		alert('グループ名を入力してください');
		$("#roleName").focus();
		return false;
	} else if($("#table tbody tr").length == 0){
		alert("\"ユーザ\" または \"組織\"を追加してください。");
		document.getElementById("target").focus();
		return false;
	}
};


</script>
<style type="text/css">
	#search_dialog{
		border-radius:10px;
		-webkit-border-radius:10px;
		-moz-border-radius:10px;
		border:1px solid #AAA;
		margin:5px 0;
		padding:10px;
	}
	#add_user{
		padding:1px 10px;
	}
</style>
<div>
	<p>役割グループの詳細設定を行います。Google Appsのユーザ/グループを役割グループに割り当てることができます。</p>
	<form:form modelAttribute="role" id="add_group" method="post" action="save" onSubmit="return checkForm()" class="cssform">
		<form:hidden path="id" />
		<c:set var="principalSize" value="${role.size}" />
		<fieldset>
			<legend>グループ設定画面</legend>
			<ul>
				<li>
					<label>グループ名：</label>
					<form:input id="roleName" path="name"/>
				</li>
				<li>
					<label>ユーザ/グループ：</label>
			<button id="add_button"><div class="add label_icon">ユーザ/グループ追加</div></button>

		<div id="search_dialog" style="display:none">
			<p>
				タイプを選択し、名前に追加したいユーザ/グループ名を入力してください<br>
				対象範囲はインクリメンタルサーチが可能ですので、頭文字を入力すれば対象のユーザ/グループ名をリストから選択することができます。
			</p>
			<span>タイプ：</span>
			<select id="roleType">
				<option value='UIDPrincipal'>
					<spring:message code="role.index.principal.type.UIDPrincipal"/>
				</option>
				<option value='OrganizationPrincipal'>
					<spring:message code="role.index.principal.type.OrganizationPrincipal"/>
				</option>
			</select>
			<span style="margin-left:10px;">名前：</span>
			<input id="principalId" type="hidden"></input>
			<input id="target" type="text"></input>
			<input type="button" id="add_user" value="追加">
		</div>
			<table id="table" class="tablesorter">
				<thead>
					<tr>
						<th>タイプ</th>
						<th>対象範囲</th>
						<th width="40">削除</th>
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
						<td align="center">
							<div class="trash icon" onclick="deletePrincipal(${status.index})"></div>
							<input name="rolePrincipals[<c:out value='${status.index}'/>].id" type="hidden" value="${principal.id}" />
						</td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
			<input type="submit" name="button" value="保存" />
			<input type="button" value="キャンセル" onclick="javascript:window.location.href='index'" />

	</form:form>
</div>



	</tiles:putAttribute>
</tiles:insertDefinition>