<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="tab"/>
	<tiles:putAttribute name="title" value="tab.title"/>
	<tiles:putAttribute name="body" type="string">
<script type="text/javascript" class="source">
$(function(){
	$('#add_button').button().click(function(){
		window.open("newTab", "<spring:message code="tab.index.editTab.title" />", 'width=800, height=600, menubar=no, toolbar=no, scrollbars=yes');
	});
	$('a.edit_link').click(function(event){
		window.open(this.href, "<spring:message code="tab.index.editTab.title" />", 'width=800, height=600, menubar=no, toolbar=no, scrollbars=yes');
		event.preventDefault();
	});
	$("#tab_table").tablesorter({
		headers: {
			0:{sorter:false},
			1:{sorter:false},
			2:{sorter:false},
			3:{sorter:false},
			4:{sorter:false},
			5:{sorter:false},
			6:{sorter:false},
			7:{sorter:false}
		}
	});
	$("#tab_table tbody").sortable({
		axis:"y",
		handle:".sort_handle",
		update:function(event, ui){
			$.ajax({
				url: "sort",
				type:"POST",
				data: $("#tab_table tbody").sortable("serialize", {key:"tabId"}),
				success: function(data, status, xhr){
				},
				error: function(xhr, status, e){
					//TODO: handle error
				}
			});
		}
	});
	$('a.delete_link').click(function(event){
		return confirm("タブを削除します。履歴も全て削除されます。\n復元することはできませんが、よろしいですか？");
	});
});
</script>
<div style="height:500px;">
	<p>
		この画面ではユーザに初期表示するタブを管理することができます。<br>
		ユーザのポータル画面ではこの画面で設定したタブのうちそのユーザで参照可能なタブが表示されます。
	</p>
	<div>
		<a href="#" id="add_button" class="button" onclick="">新しいタブを追加</a>
	</div>
	<table id="tab_table" class="tablesorter">
		<thead>
			<tr>
				<th width="18">&nbsp;</th>
				<th><spring:message code="tab.index.title" /></th>
				<th width="130">更新日時</th>
				<th width="130">更新者</th>
				<th width="100"><spring:message code="tab.index.publish" /></th>
				<th><spring:message code="tab.index.publishingRange" /></th>
				<th width="50"><spring:message code="tab.index.edit" /></th>
				<th width="50"><spring:message code="tab.index.delete" /></th>
				<th width="50">履歴</th>
			</tr>
		</thead>
		<tfoot></tfoot>
		<tbody>
<c:forEach var="tab" items="${tabs}">
			<tr id="tabId_${tab.id}">
				<td align="center"><div class="sort_handle icon" title="ドラッグして順番変更"></div></td>
				<td>${tab.name}</td>
				<td>${tab.updatedAt}</td>
				<td>${tab.editor.name}</td>
				<td><spring:message code="tab.index.publish${tab.publish}"/></td>
				<td>
					<c:forEach var="role" items="${tab.roles}">
						${role.name}
					</c:forEach>
				</td>
				<td><a href="editTab?id=${tab.id}" class="edit_link"><div class="edit icon" title="編集"></div></a></td>
				<td><a href="deleteTab?id=${tab.tabId}" class="delete_link"><div class="trash icon" title="削除"></div></a></td>
				<td><a href="history?id=${tab.tabId}"><spring:message code="tab.index.showHistory"/></a></td>
			</tr>
</c:forEach>
		</tbody>
	</table>
</div>
	</tiles:putAttribute>
</tiles:insertDefinition>
