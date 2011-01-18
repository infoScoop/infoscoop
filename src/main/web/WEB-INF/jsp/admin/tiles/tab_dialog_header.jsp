<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<ul class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
	<li class="ui-state-default ui-corner-top <c:if test="${type == 'menu'}">ui-tabs-selected ui-state-active</c:if>">
		<a href="selectGadgetType?tabId=${tabId}&containerId=${containerId}">
			<span><spring:message code="tab.staticGadget.new" /></span>
		</a>
	</li>
	<li class="ui-state-default ui-corner-top <c:if test="${type == 'tab'}">ui-tabs-selected ui-state-active</c:if>">
		<a href="listGadgetInstances?tabId=${tabId}&containerId=${containerId}">
			<span><spring:message code="tab.staticGadget.list" /></span>
		</a>
	</li>
</ul>