<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="administrator"/>
	<tiles:putAttribute name="title" value="alb_admin"/>
	<tiles:putAttribute name="body" type="string">

<div id="portalAdmin"></div>

<script>
	$jq(function(){
		ISA_PortalAdmins.portalAdmins = new ISA_PortalAdmins();
		ISA_PortalAdmins.portalAdmins.build();
	});
</script>
	</tiles:putAttribute>
</tiles:insertDefinition>