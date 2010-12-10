<%@ page contentType="text/json; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
[
{
	"uid":"${uid}",
	"defaultUid":"default",
	"tabId":"${tabTemplate.id}",
	"tabName":"Home",
	"tabNumber":"0",
	"tabType":"static",
	"widgetLastModified":"-",
	"disabledDynamicPanel":${tabTemplate.areaType != 0},
	"adjustStaticHeight":${tabTemplate.areaType == 2},
	"property":{"numCol":"","columnsWidth":"[\"32.5%\", \"32.5%\", \"32.5%\"]"},
	"staticPanel":{
	<c:forEach items="${tabTemplate.tabTemplateStaticGadgets}" var="gadget" varStatus="status">
		"${gadget.containerId}":{
			"id":"${gadget.containerId}",
			"ignoreHeader":${gadget.ignoreHeaderBool},
			"noBorder":${gadget.noBorderBool},
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
		}<c:if test="${!status.last}">,</c:if>
	</c:forEach>
	},
	"staticPanelLayout":${ tabTemplate.escapedLayout },
	"dynamicPanel":{
	<c:forEach items="${gadgets}" var="gadget" varStatus="status">
		"${gadget.widgetId}":{
			"id":"${gadget.widgetId}",
			"column":"${gadget.columnNum}",
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
		}<c:if test="${!status.last}">,</c:if>
	</c:forEach>
	}
}
]
