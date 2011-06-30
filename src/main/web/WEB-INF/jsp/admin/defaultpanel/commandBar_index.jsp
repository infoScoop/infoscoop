<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:insertDefinition name="defaultpanel.side.definition" flush="true">
<%-- 	<tiles:putAttribute name="type" value="defaultPanel"/> --%>
	<tiles:putAttribute name="type" value="defaultPanel_commandBar" />
	<tiles:putAttribute name="title" value="alb_defaultPanel"/>
	<tiles:putAttribute name="side_body" type="string">

<!-- <div id="command-bar"></div> -->
<div id="defaultPanel"></div>

<script>
	$jq(function(){
		ISA_DefaultPanel.defaultPanel = new ISA_DefaultPanel(true);
		IS_SiteAggregationMenu.init();
		ISA_loadProperties(ISA_DefaultPanel.defaultPanel.build);
	});
</script>
	</tiles:putAttribute>
</tiles:insertDefinition>