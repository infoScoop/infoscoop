<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<h2><spring:message code="menu.selectGadgetInstance.title" /></h2>
<h4><spring:message code="menu.selectGadgetInstance.add.link" /></h4>
<p>
	<c:set var="arg1"><a href="#" onclick="showAddItem(false, '${parentId}');"></c:set>
	<c:set var="arg2"></a></c:set>
	<spring:message code="menu.selectGadgetInstance.add.link.description" arguments="${arg1}\t${arg2}" argumentSeparator="\t"/>
</p>
<c:if test="${parentId != ''}">
<h4><spring:message code="menu.selectGadgetInstance.add.gadget" /></h4>
<p>
	<c:set var="arg1"><a href="#" onclick="selectGadgetType();"></c:set>
	<c:set var="arg2"></a></c:set>
	<spring:message code="menu.selectGadgetInstance.add.gadget.description" arguments="${arg1}\t${arg2}" argumentSeparator="\t"/>
</p>
<c:choose>
<c:when test="${fn:length(instances) > 0}">
<div id="gadgetInstanceList">
	<ul>
		<c:forEach var="instance" items="${instances}">
		<li><a href="#" onclick="showEditInstance('${instance.id}', '${parentId}')">${instance.title}</a></li>
		</c:forEach>
	</ul>
</div>
</c:when>
<c:otherwise>
<p>
	<c:set var="arg1"><a href="#" onclick="selectGadgetType();"></c:set>
	<c:set var="arg2"></a></c:set>
	<spring:message code="menu.selectGadgetInstance.no.gadget" arguments="${arg1}\t${arg2}" argumentSeparator="\t"/>
</p>
</c:otherwise>
</c:choose>
</c:if>