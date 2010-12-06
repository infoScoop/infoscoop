<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
	<table class="tab_table" cellspacing="0" cellpadding="0" style="width:100%;">
		<thead>
			<tr>
				<th><spring:message code="role.selectRole.check"/></th>
				<th><spring:message code="role.selectRole.roleName"/></th>
				<th><spring:message code="role.selectRole.principalType"/></th>
				<th><spring:message code="role.selectRole.publishingRange"/></th>
			</tr>
		</thead>
		<tfoot></tfoot>
		<tbody>

		<c:forEach var="role" items="${roles}" varStatus="s">
			<c:set var="principalSize" value="${role.size}" />
			<c:forEach var="principal" items="${role.rolePrincipals}" varStatus="status">
				<tr id="role_id_${role.id}">
					<c:if test="${status.index == 0}">
	 					<td rowspan="${principalSize}"><input type="checkbox" name="select_role_checkbox" value="${role.id}"/></td>
	 					<td id="${role.id}" rowspan="${principalSize}">${role.name}</td>
 					</c:if>
					<td><spring:message code="role.index.principal.type.${principal.type}"/></td>
					<td>${principal.name}</td>
				</tr>
			</c:forEach>
		</c:forEach>

		</tbody>
	</table>
<input type="button" id="add_role_btn" value="追加"/>
