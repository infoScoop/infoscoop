<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script>
parent.displayStaticGadget(
	{
		"id":"${gadget.containerId}",
		"tabId":"${tabTemplate.id}",
		"href":"${gadget.gadgetInstance.href}",
		"title":"${gadget.gadgetInstance.title}",
		"siblingId":"",
		"type":"${gadget.gadgetInstance.gadgetType}",
		"property":{
			<c:forEach items="${gadget.gadgetInstance.gadgetInstanceUserPrefs}" var="userPref" varStatus="istatus">
			  "${userPref.id.name}": "${userPref.value}" <c:if test="${!istatus.last}">,</c:if>
			</c:forEach>
		 }
	});

parent.$j("#static_gadget_modal").dialog("close");
</script>