<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script>
parent.displayStaticGadget(
	{
		"id":"${gadget.containerId}",
		"tabId":"${tabTemplate.id}",
		"href":"${gadget.fkGadgetInstance.href}",
		"title":"${gadget.fkGadgetInstance.title}",
		"siblingId":"",
		"type":"${gadget.fkGadgetInstance.gadgetType}",
		"property":{
			<c:forEach items="${gadget.fkGadgetInstance.gadgetInstanceUserPrefs}" var="userPref" varStatus="istatus">
			  "${userPref.id.name}": "${userPref.value}" <c:if test="${!istatus.last}">,</c:if>
			</c:forEach>
		 }
	});

parent.Control.Modal.close();
</script>