<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<form id="add_item">
	<input type="hidden" name="parentId" value="${parentId}">
	<input type="text" name="title"><br>
	<input type="button" value="保存" onclick="addItem()">
</form>