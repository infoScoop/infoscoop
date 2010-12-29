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
			5:{sorter:false}
		}
	});
	$('a.delete_link').click(function(event){
		return confirm("この履歴を削除します。よろしいですか？");
	});
});
</script>
<div style="height:500px;">
	<a href="index">戻る</a>
	<h2>「${currentTab.name}」の履歴一覧</h2>
	<p>
		タブの履歴が最大30リビジョンまで保存されています。あるリビジョンのタブを編集してカレントタブにすることができます。
	</p>
	<table id="tab_table" class="tablesorter">
		<thead>
			<tr>
				<th>タイトル</th>
				<th width="130">更新日時</th>
				<th width="130">更新者</th>
				<th width="100">公開設定</th>
				<th>公開範囲</th>
				<th width="50"><spring:message code="tab.index.edit" /></th>
				<th width="50"><spring:message code="tab.index.delete" /></th>
			</tr>
		</thead>
		<tfoot></tfoot>
		<tbody>
<c:forEach var="tab" items="${tabs}">
			<tr>
				<td>${tab.name}</td>
				<td>${tab.updatedAt}</td>
				<td>${tab.editor.name}</td>
				<td><spring:message code="tab.index.publish${tab.publish}"/></td>
				<td>
					<c:forEach var="role" items="${tab.roles}">
						${role.name}
					</c:forEach>
				</td>
				<td><a href="editTab?id=${tab.id}" class="edit_link"><div class="edit icon" title="このリビジョンを元に編集"></div></a></td>
				<td><a href="deleteHistory?id=${tab.id}" class="delete_link"><div class="trash icon"></div></a></td>
			</tr>
</c:forEach>
		</tbody>
	</table>
</div>
	</tiles:putAttribute>
</tiles:insertDefinition>
