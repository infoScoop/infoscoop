<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<h2><spring:message code="menu.showAddItem.title" /></h2>
<c:set var="action" value="addItem" scope="request"/>
<c:set var="type" value="menu" scope="request"/>
<c:set var="gadget" value="${menuItem}" scope="request"/>
<form:form modelAttribute="menuItem" method="post" action="${action}" class="cssform">
	<form:hidden path="fkMenuTree.id" />
	<c:if test="${menuItem.fkParent.id != null}">
		<form:hidden path="fkParent.id" />
	</c:if>
	<c:import url="/WEB-INF/jsp/admin/gadget/_form.jsp"/>
</form:form>
<script>
$("#menuItem").ajaxForm(function(html){
	$("#menu_right").html(html);
});
$("input[type='cancel']").click(function(){
	<c:if test="${conf != null}">
	$.get("selectGadgetType", {id:'${ menuItem.fkParent.id }'}, function(html){
		$("#menu_right").html(html);
	});
	</c:if>
	<c:if test="${conf == null}">
	$("#menu_right").html("<spring:message code="menu.editPage.description" /><br>");
	</c:if>
});
</script>