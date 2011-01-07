<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<ul>
<c:set var="hasChild" value="${fn:length(tree.childItems) > 0}"/>
<li <c:if test="${hasChild}">class="jstree-closed"</c:if> id="menuId_">
	<a href="#">${tree.title}<span onclick="showMenuCommand(event, this, 'menuId_', true)" class="menu_open" title="<spring:message code="menu.editPage.open.menu" />">▼</span></a>
	<div class="info">
		<c:choose>
			<c:when test="${tree.publish == 1}"><span class="publish"><spring:message code="menu.tree.publish" /></span></c:when>
			<c:otherwise><span class="publish un"><spring:message code="menu.tree.unpublish" /></span></c:otherwise>
		</c:choose>
	</div>
	<c:if test="${hasChild}">
		<c:set var="items" value="${tree.childItems}" scope="request"/>
		<c:import url="/WEB-INF/jsp/admin/menu/_childTree.jsp"/>
	</c:if>
</li>
</ul>