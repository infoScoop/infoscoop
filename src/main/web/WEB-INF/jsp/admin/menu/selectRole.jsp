<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<c:set var="action" value="updateItem" scope="request"/>
<c:set var="type" value="menu" scope="request"/>
<c:import url="/WEB-INF/jsp/admin/gadget/_selectRole.jsp"/>