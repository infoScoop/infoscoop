<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:useAttribute name="title"/>
<tiles:useAttribute name="type" scope="request"/>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=8">
		<title><spring:message code="layouts.default.title" /> - <spring:message code="${title}" /></title>
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
		/*TODO: Following style is copied from mangaer.css*/
		/*- Tab--------------------------- */
		.tab_table {
			border:1px solid #CCCCCC;
			border-collapse:collapse;
			font-size:1.3em;
			text-align:center;
		}
		.tab_table tr td {
			border:1px solid #CCCCCC;
		}
		.tab_table tr th {
			background-color: #d3DADE;
			padding: 3px;
		}
		.tab_table tr.rowb {
			background-color:#EAf2FD;
		}
		.tab_table tr.filterColumns td {
			padding:2px;
		}
		
		.trash {
			background: url(../../skin/imgs/trash.gif ) no-repeat;
			font-size: 1.3em;
			padding-left:15px;
			cursor:pointer;
		}
		.edit {
			background: url(../../skin/imgs/edit.gif ) no-repeat;
			font-size: 1.3em;
			padding-left:15px;
			cursor:pointer;
		}
		.add {
			background: url(../../skin/imgs/add.gif ) no-repeat;
			font-size: 1.3em;
			padding-left:15px;
			cursor:pointer;
		}
		.icon{
			width:16px;
			height:16px;
			cursor:pointer;
		}
		</style>
	</head>
	<body>
		<div id="body"><tiles:insertAttribute name="body" /></div>
	</body>
</html>
