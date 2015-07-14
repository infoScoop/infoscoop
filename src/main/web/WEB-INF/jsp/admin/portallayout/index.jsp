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
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:insertDefinition name="defaultpanel.side.definition" flush="true">
<%-- 	<tiles:putAttribute name="type" value="defaultPanel"/> --%>
	<tiles:putAttribute name="type" value="generallayout" />
	<tiles:putAttribute name="title" value="alb_otherLayout"/>
	<tiles:putAttribute name="side_body" type="string">

<div id="portalLayout"></div>
<iframe id="upLoadDummyFrame" name="upLoadDummyFrame"></iframe>
<script>
	$jq(function(){
		ISA_PortalLayout.portalLayout = new ISA_PortalLayout();
		ISA_PortalLayout.portalLayout.build();
	});
</script>
	</tiles:putAttribute>
</tiles:insertDefinition>