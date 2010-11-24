<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<h2><spring:message code="menu.selectGadgetType.title" /></h2>
<p>
	<spring:message code="menu.selectGadgetType.selectType" />
</p>
<div id="gadget_type_list">
	<ul>
	</ul>
</div>
<p>
	<spring:message code="menu.selectGadgetType.inputURL" /><br/>
	<input type="text" id="gadget_url"><button id="gadget_add_button"><spring:message code="menu.selectGadgetType.button.add" /></button>
</p>
<script type="text/javascript">
var gadgetListUl = $("#gadget_type_list ul");
$.each(gadgetConfs, function(type, gadget){
	var title = getGadgetTitle(type);
	try{
		gadgetListUl.append(
			$.LI({},
				$.A(
					{
						href:"#",
						onclick:{handler:function(){showAddItem(false, type, title, '${parentId}')}}
					},
					title
				)
			)
		);
	}catch(e){
		console.error(e);
		return false;
	}
});
$("#gadget_add_button").button().click(function(){
	var url = $("#gadget_url").val();
	if(!url) alert("<spring:message code="menu.selectGadgetType.no.url" />");
	showAddItem(false, "g_"+url, "", '${parentId}');
});
</script>