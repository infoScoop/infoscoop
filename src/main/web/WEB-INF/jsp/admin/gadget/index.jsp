<%--
# infoScoop OpenSource
# Copyright (C) 2010 Beacon IT Inc.
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Lesser General Public License version 3
# as published by the Free Software Foundation.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Lesser General Public License for more details.
# 
# You should have received a copy of the GNU Lesser General Public
# License along with this program.  If not, see
# <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="org.infoscoop.service.ForbiddenURLService" %>
<%@page import="org.infoscoop.service.PortalAdminsService" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:insertDefinition name="base.definition" flush="true">
<%-- <tiles:insertDefinition name="gadget.side.definition" flush="true"> --%>
	<tiles:putAttribute name="type" value="gadget"/>
	<tiles:putAttribute name="title" value="alb_widget"/>
	<tiles:putAttribute name="body" type="string">
<%-- 	<tiles:putAttribute name="side_body" type="string"> --%>

<style>
/* 	#gadgetAcc .accHeader ul {  */
/* 		list-style:none; */
/* 	}  */
/* 	#gadgetAcc #gadgetListLabel { */
/* 		font-weight: bold; */
/* 		padding: 5px 0; */
/* 		background-color: #CCCCCC;  */
/* 		background:url(); */
/* 		border: 1px solid #000; */
/* 		cursor: pointer; */
/* 	} */
/* 	#gadgetAcc #gadgetListLabel a{ */
/* 		margin-left: 10px; */
/* 	} */
/* 	#gadgetAcc li {  */
/* 		list-style-type: disc;  */
/* 		background-color: #FFF;  */
/* 	}  */
</style>

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