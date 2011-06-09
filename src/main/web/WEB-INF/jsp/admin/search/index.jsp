<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="org.infoscoop.service.ForbiddenURLService" %>
<%@page import="org.infoscoop.service.PortalAdminsService" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="search"/>
	<tiles:putAttribute name="title" value="alb_searchForm"/>
	<tiles:putAttribute name="body" type="string">

<div id="searchEngine"></div>

<script>
	$jq(function(){
		ISA_SearchEngine.searchEngine = new ISA_SearchEngine();
		ISA_SearchEngine.searchEngine.build();
	});
</script>
	</tiles:putAttribute>
</tiles:insertDefinition>