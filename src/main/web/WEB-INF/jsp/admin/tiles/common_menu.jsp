<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div id="tabs">
	<ul>
		<li <c:if test="${type == 'menu'}">class="current"</c:if>><a href="../menu/index"><span><spring:message code="menu.tab" /></span></a></li>
		<li <c:if test="${type == 'tab'}">class="current"</c:if>><a href="../tab/index"><span><spring:message code="tab.tab" /></span></a></li>
		<li <c:if test="${type == 'group'}">class="current"</c:if>><a href="../group/index"><span><spring:message code="group.tab" /></span></a></li>
	</ul>
</div>