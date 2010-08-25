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

		cell1.innerHTML = "<select id='rolePrincipals"+ count +".type' name='rolePrincipals[" + count + "].type'><option value='ユーザ'>ユーザ</option><option value='組織'>組織</option></select>";
		cell2.innerHTML = "<input id='rolePrincipals" + count + ".name' name='rolePrincipals[" + count + "].name' type='text' value=''/>";
	});

	var count = 0
	$("#searchButton").click(function(){
		count++;
		var tbl = document.getElementById("search");
		var tr = document.createElement("tr");
		var td1 = document.createElement("td");
		var td2 = document.createElement("td");
		var select = "タイプ：<select id='rolePrincipals"+ count +".type' name='rolePrincipals[" + count + "].type'><option value='ユーザ'>ユーザ</option><option value='組織'>組織</option></select>";
		var input = "対象範囲<input id='rolePrincipals" + count + ".name' name='rolePrincipals[" + count + "].name' type='text' value=''/>";

		td1.innerHTML = select;
		td2.innerHTML = input;

		tr.appendChild(td1);
		tr.appendChild(td2);
		tbl.appendChild(tr);
	});
});
</script>

<div>
グループ設定画面
	<form:form modelAttribute="role" id="add_group" method="post" action="saveGroup">

		<p>名前：<form:input path="name" /></p>
		<table id="table">
		<tr>
			<td>
				<form:select path="rolePrincipals[0].type">
					<form:option value="ユーザ" label="ユーザ" />
					<form:option value="組織" label="組織" />
				</form:select>
			</td>
			<td>
				<form:input path="rolePrincipals[0].name" />
				<form:errors path="rolePrincipals[0].name" />
			</td>
			<td><span class="trash"></span></td>
		</tr>
		</table>
		<input id="addButton" type="button" value="追加" />
		<input type="submit" name="button" value="保存" />

	</form:form>
</div>

<div>
	<input id="searchButton" type="button" value="追加"/>
	<table id="search" class="tab_table">
	</table>
</div>


	</tiles:putAttribute>
</tiles:insertDefinition>