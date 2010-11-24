<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:useAttribute name="title"/>
<tiles:useAttribute name="type" scope="request"/>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<title><spring:message code="layouts.default.title" /> - <spring:message code="${title}" /></title>
		<script type="text/javascript" src="../../js/lib/jquery.js"></script>
		<link rel="stylesheet" type="text/css" href="../../skin/manager.css">
		<style type="text/css">
		body{background:#FFF;}
		</style>
		<link rel="stylesheet" type="text/css" href="../../js/lib/jquery-ui/css/smoothness/jquery-ui-1.8.4.custom.css">
		<script type="text/javascript" src="../../js/lib/jquery-ui/jquery-ui-1.8.4.custom.min.js"></script>
		<script type="text/javascript" src="../../js/lib/jquery.form.js"></script>
		<script type="text/javascript" src="../../js/utils/domhelper.js"></script>
		<script type="text/javascript" src="../../js/manager/gadget.js"></script>
	</head>
	<body>
		<div id="body"><tiles:insertAttribute name="body" /></div>
	</body>
</html>