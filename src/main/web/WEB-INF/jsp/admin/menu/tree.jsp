<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<ul>
<c:forEach var="item" items="${items}">
<c:set var="hasChild" value="${fn:length(item.childItems) > 0}"/>
<li <c:if test="${hasChild}">class="jstree-closed"</c:if> id="${item.id}" type="${item.fkGadgetInstance.type}">
	<a href="#">${item.title}<span onclick="showMenuCommand(event, this, '${item.id}')" class="menu_open">▼</span></a>
	<div class="info">
		<c:choose>
			<c:when test="${item.publish == 1}"><span class="publish">公開</span></c:when>
			<c:otherwise><span class="publish un">非公開</span></c:otherwise>
		</c:choose>
	</div>
	<c:if test="${hasChild}">
		<c:set var="items" value="${item.childItems}" scope="request"/>
		<c:import url="/WEB-INF/jsp/admin/menu/tree.jsp"/>
	</c:if>
</li>
</c:forEach>
</ul>