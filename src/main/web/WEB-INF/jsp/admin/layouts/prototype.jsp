<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:useAttribute name="title"/>
<tiles:useAttribute name="type" scope="request"/>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<title><spring:message code="manager.title" /> - <spring:message code="${title}" /></title>
		<script type="text/javascript" src="../../js/lib/prototype-1.6.0.3.js"></script>
		<script src="../../js/lib/scriptaculous-js-1.8.2/effects.js"></script>
		<script src="../../js/lib/scriptaculous-js-1.8.2/dragdrop.js"></script>
		<script src="../../js/lib/scriptaculous-js-1.8.2/controls.js"></script>
		<style>
		body{
			margin:0;
			padding:0;
			font-size:11px;
			font-family:Arial,Helvetica,sans-serif;
			background-color: #F0F0F0;
		}
		#body{
			background-color:#FFF;
			margin:5px;
			padding:5px;
		}
		</style>
	</head>
	<body>
		<div id="body"><tiles:insertAttribute name="body" /></div>
	</body>
</html>