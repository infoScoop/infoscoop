<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="group"/>
	<tiles:putAttribute name="title" value="group.title"/>
	<tiles:putAttribute name="body" type="string">
<div style="height:500px;">
	Group
</div>
	</tiles:putAttribute>
</tiles:insertDefinition>