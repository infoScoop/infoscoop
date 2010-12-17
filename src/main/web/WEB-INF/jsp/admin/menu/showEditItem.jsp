<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<h2><spring:message code="menu.showEditItem.title" /></h2>
<c:set var="action" value="updateItem" scope="request"/>
<c:set var="type" value="menu" scope="request"/>
<form:form modelAttribute="menuItem" method="post" action="${action}" class="cssform">
	<form:hidden path="fkMenuTree.id" />
	<form:hidden path="fkParent.id" />
	<c:import url="/WEB-INF/jsp/admin/gadget/_form.jsp"/>
</form:form>
<script>
$("#menuItem").ajaxForm(function(html){
	$("#menu_right").html(html);
});
</script>