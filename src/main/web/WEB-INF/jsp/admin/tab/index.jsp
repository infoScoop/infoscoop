<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="tab"/>
	<tiles:putAttribute name="title" value="tab.title"/>
	<tiles:putAttribute name="body" type="string">
<script type="text/javascript" class="source">
$(function(){
	$('#add_button').click(function(){
		window.open("showAddTab", "タブ設定", 'width=800, height=600, menubar=no, toolbar=no, scrollbars=yes');
	});
});
</script>
<div style="height:500px;">
	<div>
		<a href="#" id="add_button" class="button" onclick="">追加</a>
	</div>
	<table id="tab_table" class="tab_table" cellspacing="0" cellpadding="0">
		<thead>
			<tr>
				<th>タイトル</th>
				<th>公開</th>
				<th>公開範囲</th>
				<th>削除</th>
			</tr>
		</thead>
		<tfoot></tfoot>
		<tbody>
<c:forEach var="tab" items="${tabs}">
			<tr>
				<td>${tab.name}</td>
				<td>${tab.published}</td>
				<td>${tab.accessLevel}</td>
				<td><span class="trash"></span></td>
			</tr>
</c:forEach>
		</tbody>
	</table>
</div>
	</tiles:putAttribute>
</tiles:insertDefinition>
