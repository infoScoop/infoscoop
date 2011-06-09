<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="org.infoscoop.service.ForbiddenURLService" %>
<%@page import="org.infoscoop.service.PortalAdminsService" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="i18n"/>
	<tiles:putAttribute name="title" value="alb_i18n"/>
	<tiles:putAttribute name="body" type="string">

<div id="i18n"></div>
<iframe id="upLoadDummyFrame" name="upLoadDummyFrame"></iframe>

<script>
	$jq(function(){
		ISA_I18N.i18n = new ISA_I18N();
		ISA_I18N.i18n.build();
	});
</script>
	</tiles:putAttribute>
</tiles:insertDefinition>