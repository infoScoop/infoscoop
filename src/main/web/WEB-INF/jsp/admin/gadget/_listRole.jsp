<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<script>
jQuery(function(){
	jQuery('input[name="accessLevel"]').click(function(){
	console.log((jQuery(this).attr('value') == 2 && jQuery(this).attr('checked')));
			if(jQuery(this).attr('value') == 2 && jQuery(this).attr('checked')){
			console.log(jQuery('#selected_security_role_panel'));
				jQuery('#selected_security_role_panel').show();
			}else{
				jQuery('#selected_security_role_panel').hide();
			}
		});
	jQuery('#add_role_btn').livequery(function(){
		jQuery(this).click(function(){
			jQuery('input[name="select_role_checkbox"]').each(function(){
				if(jQuery(this).attr('checked')){
					var roleListTbody = jQuery('#role_list_table').children('tbody');
					var selectedRoleRow = jQuery('#role_id_' + jQuery(this).val());
					var roleRow = selectedRoleRow.clone(true);
					roleRow.attr('id', '#selected_role_id_' + jQuery(this).val());
					roleRow.children('td:first-child').remove();
					var rowSpan = roleRow.children('td:first-child').attr('rowSpan');
					roleRow.children('td:first-child').append(jQuery('<input type="hidden" name="roles.id" value="' + jQuery(this).val() + '"/>'));
					roleRow.append(jQuery('<td rowSpan="'+ rowSpan + '"><span class="trash"  onclick="deleteRole()" ></td>'))
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
				jQuery('#select_role_dialog').dialog();
			});
			
		});		
	});
});
</script>
<span id="access_level_radio" class="radio" style="display:inline-block;">
	<c:set var="unpublish"><spring:message code="gadget._listRole.accessLevel0" /></c:set>
	<c:set var="publish"><spring:message code="gadget._listRole.accessLevel1" /></c:set>
	<c:set var="special"><spring:message code="gadget._listRole.accessLevel2" /></c:set>
	<form:radiobutton path="accessLevel" value="0" label="${unpublish}" cssErrorClass="error" />
	<form:radiobutton path="accessLevel" value="1" label="${publish}" cssErrorClass="error" />
	<form:radiobutton path="accessLevel" value="2" label="${special}" cssErrorClass="error" />
	<form:errors path="accessLevel" />
	<div id="selected_security_role_panel" style="display:${ ( tabTemplate.accessLevel== 2 ? "":"none" )};">
	<div id="add_role_button" class="add">追加</div>
	<table id="role_list_table" class="tab_table" cellspacing="0" cellpadding="0">
		<thead>
			<tr>
				<th>ロール名前</th>
				<th>タイプ</th>
				<th>対象範囲</th>
				<th>削除</th>
			</tr>
		</thead>
		<tfoot></tfoot>
		<tbody>

		<c:forEach var="role" items="${roles}" varStatus="s">
			<c:set var="principalSize" value="${role.size}" />
			<c:forEach var="principal" items="${role.rolePrincipals}" varStatus="status">
				<tr id="${role.id}">
					<c:if test="${status.index == 0}">
	 					<td id="${role.id}" rowspan="${principalSize}"><input type="hidden" name="roles.id" value="${role.id}"/>${role.name}</td>
 					</c:if>
					<td><spring:message code="role.index.principal.type.${principal.type}"/></td>
					<td>${principal.name}</td>
					<c:if test="${status.index == 0}">
						<td rowspan="${principalSize}"><span class="trash"  onclick="deleteRole('${role.id}')" ></span></td>
 					</c:if>
				</tr>
			</c:forEach>
		</c:forEach>

		</tbody>
	</table>
	</div>
</span>
