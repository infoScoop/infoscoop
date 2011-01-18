<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<tiles:insertDefinition name="dialog.definition" flush="true">
	<tiles:putAttribute name="type" value="tab"/>
	<tiles:putAttribute name="title" value="tab.title"/>
	<tiles:putAttribute name="body" type="string">
<c:set var="action" value="submitGadgetSettings" scope="request"/>
<c:set var="type" value="tab" scope="request"/>
<c:set var="gadget" value="${tabTemplateStaticGadget}" scope="request"/>
<form:form modelAttribute="tabTemplateStaticGadget" method="post" action="${action}" class="cssform">
<c:import url="/WEB-INF/jsp/admin/gadget/_form.jsp"/>
</form:form>
<script type="text/javascript">

$(function(){
	var href_value= "selectGadgetType?tabId=${gadget.tabTemplateId}" +
						 "&containerId=${gadget.containerId}";
	$("#change_type").attr("href", href_value);
	$("input#cancel").click(function(){
		parent.$j("#static_gadget_modal").dialog("close");
	});
});
</script>
	</tiles:putAttribute>
</tiles:insertDefinition>