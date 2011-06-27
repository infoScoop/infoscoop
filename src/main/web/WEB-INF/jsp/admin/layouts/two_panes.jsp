<!doctype HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html; charset=UTF-8" errorPage="/jspError.jsp" %>

<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tiles:useAttribute name="title" scope="request"/>
<tiles:useAttribute name="type" scope="request"/>
<html>
	<tiles:insertAttribute name="page_head" />
	<body class="infoScoop admin">
		<div id="admin-menu-navigator"></div>
		
		<div id="admin-header">
			<tiles:insertAttribute name="header" />
		</div>
		<div id="admin-tabs">
			<tiles:insertAttribute name="menu" />
		</div>
		<div id="properties"></div>
		<div id="admin-side" >
				<tiles:insertAttribute name="side_bar"/>
		</div>
		<div id="admin-side-body">
			<tiles:insertAttribute name="side_body" />
		</div>
<!-- 		<div id="admin-footer"> -->
<%-- 			<tiles:insertAttribute name="footer" /> --%>
<!-- 		</div> -->
	</body>
</html>