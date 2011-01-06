<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div id="tabs">
	<ul>
		<li <c:if test="${type == 'home'}">class="current"</c:if>><a href="../home/index"><span><spring:message code="tiles.common_menu.home" /></span></a></li>
		<li <c:if test="${type == 'menu'}">class="current"</c:if>><a href="../menu/index"><span><spring:message code="tiles.common_menu.menu" /></span></a></li>
		<li <c:if test="${type == 'tab'}">class="current"</c:if>><a href="../tab/index"><span><spring:message code="tiles.common_menu.tab" /></span></a></li>
		<li <c:if test="${type == 'role'}">class="current"</c:if>><a href="../role/index"><span><spring:message code="tiles.common_menu.role" /></span></a></li>
		<li <c:if test="${type == 'user'}">class="current"</c:if>><a href="../user/index"><span><spring:message code="tiles.common_menu.syncmaster" /></span></a></li>
	</ul>
</div>