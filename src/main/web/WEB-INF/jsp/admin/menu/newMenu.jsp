<%@ page contentType="text/html; charset=UTF-8" %>
<tr menu_id="${menu.id}">
	<td class="title">
		<a href="editMenu?id=${menu.id}">${menu.title}</a>
		<input type="text" value="${menu.title}" style="display:none">
		<div class="icon edit_icon">
	</td>
	<td class="radio_cell">
		<span>${menu.top ? "表示" : ""}</span>
		<input type="radio" name="top" ${menu.top ? "checked=\"checked\"" : ""} style="display:none">
	</td>
	<td class="radio_cell">
		<span>${menu.side ? "表示" : ""}</span>
		<input type="radio" name="side" ${menu.side ? "checked=\"checked\"" : ""} style="display:none">
	</td>
	<td class="icon_cell"><div class="icon delete_icon" menu_id="${menu.id}"></div></td>
</tr>