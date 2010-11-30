<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
	<table id="role_list_table" class="tab_table" cellspacing="0" cellpadding="0">
		<thead>
			<tr>
				<th>ロール名前</th>
				<th>タイプ</th>
				<th>対象範囲</th>
				<th>削除</th>
			</tr>
		</thead>
		<tfoot></tfoot>
		<tbody>

		<c:forEach var="role" items="${roles}" varStatus="s">
			<c:set var="principalSize" value="${role.size}" />
			<c:forEach var="principal" items="${role.rolePrincipals}" varStatus="status">
				<tr id="${role.id}">
					<c:if test="${status.index == 0}">
	 					<td id="${role.id}" rowspan="${principalSize}"><input type="hidden" name="roles.id" value="${role.id}"/>${role.name}</td>
 					</c:if>
					<td><spring:message code="role.index.principal.type.${principal.type}"/></td>
					<td>${principal.name}</td>
					<c:if test="${status.index == 0}">
						<td rowspan="${principalSize}"><span class="trash"  onclick="deleteRole('${role.id}')" ></span></td>
 					</c:if>
				</tr>
			</c:forEach>
		</c:forEach>

		</tbody>
	</table>