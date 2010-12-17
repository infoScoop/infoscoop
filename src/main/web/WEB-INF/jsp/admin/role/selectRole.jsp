<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<div>
	<table id="role_edit_table" class="tab_table" cellspacing="0" cellpadding="0">
		<thead>
			<tr>
				<th><spring:message code="gadget._listRole.roleName" /></th>
				<th><spring:message code="gadget._listRole.principalType" /></th>
				<th><spring:message code="gadget._listRole.publishingRange" /></th>
				<th><spring:message code="gadget._listRole.delete" /></th>
			</tr>
		</thead>
		<tbody id="role_edit_table_body">
		</tbody>
	</table>
	<input type="button" value="設定" onclick="setRoles();"/>
</div>
<hr>
<div>
	<table id="selectRoleTable" class="display" cellspacing="0" cellpadding="0" style="width:100%;">
		<thead>
			<tr>
				<th><spring:message code="role.selectRole.check"/></th>
				<th><spring:message code="role.selectRole.roleName"/></th>
				<th><spring:message code="role.selectRole.principalType"/></th>
				<th><spring:message code="role.selectRole.publishingRange"/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach var="role" items="${roles}" varStatus="s">
			<c:set var="principalSize" value="${role.size}" />
				<tr id="append_role_id_${role.id}">
	 				<td><input type="button" onclick="addRole('${role.id}');" value="追加"/></td>
	 				<td id="${role.id}">${role.name}</td>
 					<td>
						<ul style="padding:0;margin:0;list-style-type:none;">
						<c:forEach var="principal" items="${role.rolePrincipals}" varStatus="status">
							<li style="${ (status.first ? "": "border-top:1px solid #CCC;") }"><spring:message code="role.index.principal.type.${principal.type}"/></li>
						</c:forEach>
						</ul>
					</td>
					<td>
					<ul style="padding:0;margin:0;list-style-type:none;">
						<c:forEach var="principal" items="${role.rolePrincipals}" varStatus="status">
							<li style="${ (status.first ? "": "border-top:1px solid #CCC;") }">${principal.name}</li>
						</c:forEach>
					</ul>
					</td>
				</tr>
		</c:forEach>
		</tbody>
	</table>
</div>
<script>
jQuery('#selectRoleTable').dataTable();
function deleteRole(trashSpan){
	var tr = jQuery(trashSpan).closest('tr');
	tr.remove();
}
function addRole(roleId){
	if(jQuery('#selected_role_id_' + roleId).length)return;
	var roleListTbody = jQuery('#role_edit_table').children('tbody');
	var selectedRoleRow = jQuery('#append_role_id_' + roleId);
	var roleRow = selectedRoleRow.clone(true);
	roleRow.attr('id', 'selected_role_id_' + roleId);
	roleRow.removeAttr('class');
	roleRow.children('td:first-child').remove();
	roleRow.children('td:first-child').append(jQuery('<input type="hidden" name="roles.id" value="' + roleId + '"/>'));
	roleRow.append(jQuery('<td class="deletetd"><span class="trash" onclick="deleteRole(this)" ></td>'))
	roleListTbody.append(roleRow);
}

function setRoles(){
	var roleListTbody = jQuery('#role_list_table').children('tbody');
	roleListTbody.children('tr').remove();
	jQuery('#role_edit_table tbody tr').each(function(){
		var roleTr = jQuery(this).clone(true);
		var rowSpan = roleTr.children('td:first-child').attr('rowSpan');
		if(rowSpan){
			var roleTrId = roleTr.attr('id');
			roleTr.attr('id', 'role_id_' + roleTrId.replace("selected_role_id_",""));
			roleTr.children('td.deletetd').remove();
		}
		roleListTbody.append(roleTr);
	});
	
	jQuery('#select_role_dialog').dialog('close');
}

jQuery('#role_list_table tbody tr').each(function(){
	var roleTr = jQuery(this).clone(true);
	var roleTrId = roleTr.attr('id');
	roleTr.attr('id', 'selected_role_id_' + roleTrId.replace("role_id_",""));
	roleTr.append('<td class="deletetd"><span class="trash" onclick="deleteRole(this);" ></td>');
	jQuery('#role_edit_table_body').append(roleTr);
});
</script>
