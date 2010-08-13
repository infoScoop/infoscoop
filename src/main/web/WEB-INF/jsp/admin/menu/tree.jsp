<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:forEach var="item" items="${items}">
<c:set var="hasChild" value="${fn:length(item.childItems) > 0}"/>
<li <c:if test="${hasChild}">class="jstree-closed"</c:if> id="${item.id}">
	<a href="#">${item.title}<span onclick="showMenuCommand(event, this, '${item.id}')" class="menu_open">▼</span></a>
	<c:if test="${hasChild}">
		<ul>
			<c:set var="items" value="${item.childItems}" scope="request"/>
			<c:import url="/WEB-INF/jsp/admin/menu/tree.jsp"/>
		</ul>
	</c:if>
</li>
</c:forEach>