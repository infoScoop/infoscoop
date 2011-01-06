<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="home"/>
	<tiles:putAttribute name="title" value="home.title"/>
	<tiles:putAttribute name="body" type="string">
		<div>infoScoop for Google Apps</div>
	</tiles:putAttribute>
</tiles:insertDefinition>