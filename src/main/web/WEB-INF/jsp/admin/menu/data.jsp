<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:forEach var="item" items="${items}">
<li class="jstree-closed" id="${item.id}">
	<a href="#">${item.title}</a>
	<div class="menu_command" onclick="showAddItem('${item.id}')">追加</div>
	<div class="menu_command" onclick="deleteItem('${item.id}')">削除</div>
</li>
</c:forEach>