<%@ page contentType="text/html; charset=UTF-8" %>
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
	var count = $("#table tbody tr").length;
	$("#add_button").button().click(function(){
		$("#search_dialog").slideToggle("fast");
		return false;
	});
	function addUser(){
		var principalId = $('#principalId').val();
		if(!principalId) {
			return;
		}
		//var count = $("#table tbody tr").length;
		count++;
		$('<tr id="principalTr'+count+'"/>')
			.append($('<td/>')
				.append($('<span/>').text($("#roleType option:selected").text()))
				.append($('<input type="hidden"/>')
					.attr("name","rolePrincipals["+count+"].type")
					.val($('#roleType').val())
				)
			).append($('<td/>')
				.append($('<span/>').text(principalId))
				.append($('<input type="hidden"/>')
					.attr("name","rolePrincipals["+count+"].name")
					.val(principalId)
				)
			).append($('<td align="center"/>')
				.append($('<div class="trash icon" onclick="deletePrincipal('+count+')"/>'))
				.append($('<input type="hidden" value=""/>')
					.attr("name","rolePrincipals["+count+"].id")
				)
			)
			.appendTo($("#table tbody"));

		//$("#roleType").val("UIDPrincipal");
		$("#target").val("");
		$("#principalId").val("");
	}

	$(function() {
		var params = {
			output:  'json',
			results: 5,
			query:   undefined
		};
		var onselect = function(event, ui) {
			onchange(event, ui);
			setTimeout(addUser, 100);//ui.item.value is set several milliseconds later
		}
		var onchange = function(event, ui){
			if(ui.item){
				$('#principalId').val(ui.item.mail);
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
			change: onchange,
			focus: onchange
		});
	});
	
	$("#add_group :submit, #add_group :button").button();
});

function deletePrincipal(index){
	$('#principalTr'+index).remove();
};

function checkForm(){
	if($("#roleName").val() == ""){
		alert('グループ名を入力してください');
		$("#roleName").focus();
		return false;
	} else if($("#table tbody tr").length == 0){
		alert("\"ユーザ\" または \"グループ\"を追加してください。");
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
				<input id="principalId" type="hidden">
				<input id="target" type="text">
			</div>
			
			<c:forEach var="principalId" items="${role.deletePrincipalIdList}" varStatus="status">
				<input name="deletePrincipalIdList[<c:out value='${status.index}'/>]" type="hidden" value="${principalId}" />
			</c:forEach>
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

				<c:forEach var="principal" items="${role.rolePrincipals}" varStatus="status">
					<tr id="principalTr${status.index}">
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