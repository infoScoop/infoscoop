<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="defaultPanel"/>
	<tiles:putAttribute name="title" value="alb_defaultPanel"/>
	<tiles:putAttribute name="body" type="string">

<div id="defaultPanel"></div>
<div id="portalLayout"></div>

<script>
	$jq(function(){
		ISA_DefaultPanel.defaultPanel = new ISA_DefaultPanel();
		IS_SiteAggregationMenu.init();
		ISA_loadProperties(ISA_DefaultPanel.defaultPanel.build);

		ISA_PortalLayout.portalLayout = new ISA_PortalLayout();
		ISA_PortalLayout.portalLayout.build();
	});
</script>
	</tiles:putAttribute>
</tiles:insertDefinition>