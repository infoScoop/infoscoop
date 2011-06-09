<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="forbiddenurl"/>
	<tiles:putAttribute name="title" value="alb_forbiddenURL"/>
	<tiles:putAttribute name="body" type="string">

<div id="forbiddenURL"></div>

<script>
	$jq(function(){
		ISA_PortalAdmins.portalForbiddenURL = new ISA_PortalForbiddenURL();
		ISA_PortalAdmins.portalForbiddenURL.build();
	});
</script>
	</tiles:putAttribute>
</tiles:insertDefinition>