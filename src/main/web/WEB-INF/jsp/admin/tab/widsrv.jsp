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
	"property":{"numCol":"","columnsWidth":"[\"32.5%\", \"32.5%\", \"32.5%\"]"},
	"staticPanel":{"p_1_w_1":{"id":"p_1_w_1","column":"1","tabId":"${tabTemplate.id}","href":"","title":"infoScoop OpenSource site","siblingId":"","parentId":"","menuId":"","type":"RssReader","property":{"url":"http://www.infoscoop.org/index.php/en/news.feed"},"longProperty":[],"createDate":0,"deleteDate":0,"ignoreHeader":false,"noBorder":false},"p_1_w_4":{"id":"p_1_w_4","column":"3","tabId":"${tabTemplate.id}","href":"","title":"Ticker","siblingId":"","parentId":"","menuId":"","type":"Ticker","property":{"url":"http://www.infoscoop.org/index.php/en/news.feed"},"longProperty":[],"createDate":0,"deleteDate":0,"ignoreHeader":false,"noBorder":false},"p_1_w_5":{"id":"p_1_w_5","column":"3","tabId":"${tabTemplate.id}","href":"","title":"Calendar","siblingId":"","parentId":"","menuId":"","type":"Calendar","property":{},"longProperty":[],"createDate":0,"deleteDate":0,"ignoreHeader":true,"noBorder":false},"p_1_w_6":{"id":"p_1_w_6","column":"3","tabId":"${tabTemplate.id}","href":"","title":"Ranking","siblingId":"","parentId":"","menuId":"","type":"Ranking","property":{"urls":"\n\t\t\t\t<urls>\n\t\t\t\t\t<url title='Search Keyword Ranking' url='http://localhost:8080/infoscoop/kwdsrv?baseDate=TODAY&amp;period=30&amp;rankingNum=20'/>\n\t\t\t\t<\/urls>\n\t\t\t"},"longProperty":[],"createDate":0,"deleteDate":0,"ignoreHeader":false,"noBorder":false}},
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
