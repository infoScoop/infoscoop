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
		<li id="builtin"><span><spring:message code="menu.selectGadgetType.builtin" /></span>
			<ul></ul>
		</li>
		<li id="upload"><span><spring:message code="menu.selectGadgetType.gadgets" /></span>
			<ul></ul>
		</li>
	</ul>
</div>
<p>
	<spring:message code="menu.selectGadgetType.inputURL" /><br/>
	<input type="text" id="gadget_url"><button id="gadget_add_button"><spring:message code="menu.selectGadgetType.button.add" /></button>
</p>
<script type="text/javascript">
$.each(gadgetConfs, function(key, gadgets){
	$.each(gadgets, function(type, gadget){
		var title = getGadgetTitle(type);
		try{
			$("#gadget_type_list #"+key+" ul").append(
				$.LI({},
					$.A(
						{
							href:"#",
							onclick:{handler:function(){showAddItem(type, '${parentId}', title)}}
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
});
$("#gadget_add_button").button().click(function(){
	var url = $("#gadget_url").val();
	if(!url) alert("<spring:message code="menu.selectGadgetType.no.url" />");
	showAddItem("g_"+url, '${parentId}');
});
</script>