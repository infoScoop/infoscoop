<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="org.infoscoop.service.PortalAdminsService" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="menu"/>
	<tiles:putAttribute name="title" value="alb_menu"/>
	<tiles:putAttribute name="body" type="string">

<div id="menu"></div>
<div id="menuTree"></div>

<script>
	$jq(function(){
		function buildFunc(){
			ISA_SiteAggregationMenu.treeMenu = new ISA_SiteAggregationMenu("topmenu", true);
			ISA_SiteAggregationMenu.treeMenu.build();
		}
		ISA_loadProperties(buildFunc);
	});
</script>
	</tiles:putAttribute>
</tiles:insertDefinition>