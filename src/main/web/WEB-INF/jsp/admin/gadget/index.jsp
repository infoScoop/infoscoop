<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="org.infoscoop.service.ForbiddenURLService" %>
<%@page import="org.infoscoop.service.PortalAdminsService" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="gadget"/>
	<tiles:putAttribute name="title" value="alb_widget"/>
	<tiles:putAttribute name="body" type="string">

<div id="widgetConf"></div>
<iframe id="upLoadDummyFrame" name="upLoadDummyFrame"></iframe>

<script>
	$jq(function(){
		if(ISA_WidgetConf.widgetConf.uploadData){
			ISA_WidgetConf.widgetConf.requestDeleteGadget(ISA_WidgetConf.widgetConf.uploadData.id);
		}
		ISA_WidgetConf.widgetConf = new ISA_WidgetConf();
		ISA_WidgetConf.widgetConf.build();
	});
</script>
	</tiles:putAttribute>
</tiles:insertDefinition>