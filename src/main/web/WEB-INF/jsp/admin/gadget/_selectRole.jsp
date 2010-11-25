<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
	<table id="tab_table" class="tablesorter" cellspacing="0" cellpadding="0">
		<thead>
			<tr>
				<th>選択</th>
				<th>ロール名前</th>
				<th>タイプ</th>
				<th>対象範囲</th>
			</tr>
		</thead>
		<tfoot></tfoot>
		<tbody>

		<c:forEach var="role" items="${roles}" varStatus="s">
			<c:set var="principalSize" value="${role.size}" />
			<c:forEach var="principal" items="${role.rolePrincipals}" varStatus="status">
				<tr id="role_id_${role.id}">
					<c:if test="${status.index == 0}">
	 					<td><input type="checkbox" name="select_role_checkbox" value="${role.id}"/></td>
	 					<td id="${role.id}" rowspan="${principalSize}">${role.name}</td>
 					</c:if>
					<td>${principal.type}</td>
					<td>${principal.name}</td>
				</tr>
			</c:forEach>
		</c:forEach>

		</tbody>
	</table>
<input type="button" id="add_role_btn" value="追加"/>