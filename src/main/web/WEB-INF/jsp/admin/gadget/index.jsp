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
	
	function switchGadgetList(id){
		var gadgetListTab = document.getElementById('gadgetList');
		var widgetListTab = document.getElementById('widgetList');
		var gadgetList = document.getElementById('gadgetList_div');
		var widgetList = document.getElementById('widgetList_div');
		if(id == 'gadgetList'){
			gadgetList.style.display = 'block';
			gadgetListTab.className ='gadgetListTabAselected';
			widgetList.style.display = 'none';
			widgetListTab.className = 'gadgetListTabA';
		}if(id == 'widgetList'){
			gadgetList.style.display = 'none';
			gadgetListTab.className = 'gadgetListTabA';
			widgetList.style.display = 'block';
			widgetListTab.className = 'gadgetListTabAselected';
		}
	};
</script>
	</tiles:putAttribute>
</tiles:insertDefinition>