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
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Lesser General Public License for more details.
# 
# You should have received a copy of the GNU Lesser General Public
# License along with this program. If not, see
# <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>;.
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="org.infoscoop.service.PortalAdminsService" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:insertDefinition name="menu.side.definition" flush="true">
	<tiles:putAttribute name="type" value="menu"/>
	<tiles:putAttribute name="title" value="alb_menu"/>
	<tiles:putAttribute name="side_body" type="string">

<div id="menu"></div>
<div id="menuTree"></div>
<script src="../../js/lib/jquery.tablesorter/jquery.tablesorter.js"></script>
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