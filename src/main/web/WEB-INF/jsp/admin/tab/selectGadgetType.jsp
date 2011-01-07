<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:insertDefinition name="dialog.definition" flush="true">
	<tiles:putAttribute name="type" value="menu"/>
	<tiles:putAttribute name="title" value="tab.title"/>
	<tiles:putAttribute name="body" type="string">
<p>
	<spring:message code="tab.selectGadgetType.selectType" />
</p>
<div id="gadgetTypeList">
	<ul>
		<c:forEach var="conf" items="${gadgetConfs}">
			<c:if test="${conf.type != 'MultiRssReader'}">
			<li><a href="newStaticGadget?tabId=${tabId}&containerId=${containerId}&type=${conf.type}">${conf.title}</a></li>
			</c:if>
		</c:forEach>
	</ul>

</div>
<p>
	<spring:message code="menu.selectGadgetType.inputURL" /><br/>
	<input type="text" id="gadget_url"><button id="gadget_add_button"><spring:message code="menu.selectGadgetType.button.add" /></button>
</p>
<script type="text/javascript">
$("#gadget_add_button").button().click(function(){
	var url = $("#gadget_url").val();
	if(!url) alert("<spring:message code="menu.selectGadgetType.no.url" />");
	location.href = "newStaticGadget?tabId=${tabId}&containerId=${containerId}&type=g_"+url;
});
</script>
	</tiles:putAttribute>
</tiles:insertDefinition>