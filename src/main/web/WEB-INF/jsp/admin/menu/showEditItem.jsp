<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<h2>メニューの編集</h2>
<c:set var="action" value="updateItem" scope="request"/>
<c:import url="/WEB-INF/jsp/admin/menu/_form.jsp"/>