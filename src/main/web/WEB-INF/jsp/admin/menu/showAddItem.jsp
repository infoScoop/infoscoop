<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<h2><spring:message code="menu.showAddItem.title" /></h2>
<c:set var="action" value="addItem" scope="request"/>
<c:set var="type" value="menu" scope="request"/>
<c:import url="/WEB-INF/jsp/admin/gadget/_form.jsp"/>