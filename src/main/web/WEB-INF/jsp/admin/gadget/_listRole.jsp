<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<script>
jQuery(function(){
	function toggleSecurityRolePanel(){
		if(jQuery(this).attr('value') == 1 && jQuery(this).attr('checked')){
			jQuery('#selected_security_role_panel').show();
		}else{
			jQuery('#selected_security_role_panel').hide();
			jQuery('#role_list_table tbody').children('tr').remove();
		}
	}
	jQuery('input[name="accessLevel"]').click(toggleSecurityRolePanel);
	
	toggleSecurityRolePanel.apply(jQuery('#accessLevel2')[0]);
	
	jQuery('#add_role_btn').livequery(function(){
		jQuery(this).click(function(){
			jQuery('input[name="select_role_checkbox"]').each(function(){
				if(jQuery(this).attr('checked')){
					var roleListTbody = jQuery('#role_edit_table').children('tbody');
					var selectedRoleRow = jQuery('#role_id_' + jQuery(this).val());
					var roleRow = selectedRoleRow.clone(true);
					roleRow.attr('id', '#selected_role_id_' + jQuery(this).val());
					roleRow.children('td:first-child').remove();
					var rowSpan = roleRow.children('td:first-child').attr('rowSpan');
					roleRow.children('td:first-child').append(jQuery('<input type="hidden" name="roles.id" value="' + jQuery(this).val() + '"/>'));
					roleRow.append(jQuery('<td rowSpan="'+ rowSpan + '"><div class="trash icon" onclick="deleteRole()" ></div></td>'))
					roleListTbody.append(roleRow);
					for(i = 1; i < rowSpan ; i++){
						selectedRoleRow = selectedRoleRow.next();
						roleListTbody.append(selectedRoleRow.clone(true));
					}
				}
			});
		});
	});

	jQuery('#add_role_button').livequery(function(){
		jQuery(this).click(function(){
			jQuery.get("../role/selectRole", {}, function(html){
				jQuery('#select_role_dialog').html(html);
				jQuery('#select_role_dialog').dialog({'title':'<spring:message code="gadget._listRole.selectRoleDialogTitle" />', 'width':680,'height':600});
			});
			
		});
	});
});
</script>
<span id="access_level_radio" class="radio" style="display:inline-block;">
	<c:set var="everyone"><spring:message code="gadget._listRole.access.everyone" /></c:set>
	<c:set var="special"><spring:message code="gadget._listRole.access.special" /></c:set>
	<div>
		<form:radiobutton path="accessLevel" value="0" label="${everyone}" cssErrorClass="error" />
		<form:radiobutton path="accessLevel" value="1" label="${special}" cssErrorClass="error" />
	</div>
	<div id="selected_security_role_panel" style="display:${ ( publish== 1 ? "":"none" )};">
		<input type="button" id="add_role_button" value="編集"/>
		<table id="role_list_table" class="tab_table" cellspacing="0" cellpadding="0">
			<thead>
				<tr>
					<th><spring:message code="gadget._listRole.roleName" /></th>
					<th><spring:message code="gadget._listRole.principalType" /></th>
					<th><spring:message code="gadget._listRole.publishingRange" /></th>
				</tr>
			</thead>
			<tfoot></tfoot>
			<tbody>

			<c:forEach var="role" items="${roles}" varStatus="s">
				<tr id="role_id_${role.id}">
					<td id="${role.id}"><input type="hidden" name="roles.id" value="${role.id}"/>${role.name}</td>
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
</span>
