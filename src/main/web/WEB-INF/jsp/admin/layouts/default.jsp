<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:useAttribute name="title"/>
<tiles:useAttribute name="type" scope="request"/>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<title><spring:message code="layouts.default.title" /> - <spring:message code="${title}" /></title>
		<link rel="stylesheet" type="text/css" href="../../skin/manager.css">
		<script type="text/javascript" src="../../js/lib/jquery.js"></script>
		<link rel="stylesheet" type="text/css" href="../../js/lib/jquery-ui/css/smoothness/jquery-ui-1.8.4.custom.css">
		<script type="text/javascript" src="../../js/lib/jquery-ui/jquery-ui-1.8.4.custom.min.js"></script>
		<script type="text/javascript" src="../../js/lib/livequery-1.1.0/jquery.livequery.js"></script>
		<script type="text/javascript" src="../../js/lib/jquery.form.js"></script>
		<script type="text/javascript" src="../../js/utils/domhelper.js"></script>
		<link rel="stylesheet" type="text/css" href="../../js/lib/tablesorter/themes/blue/style.css">
		<script type="text/javascript" src="../../js/lib/tablesorter/jquery.tablesorter.min.js"></script>
		<link rel="stylesheet" type="text/css" href="../../js/lib/DataTables-1.7.4/css/demo_page.cs">
		<link rel="stylesheet" type="text/css" href="../../js/lib/DataTables-1.7.4/css/demo_table.css">
		<script src="../../js/lib/DataTables-1.7.4/js/jquery.dataTables.min.js"></script>
<script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-19564588-2']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>
	</head>
	<body>
		<div id="header"><tiles:insertAttribute name="header" /></div>
		<div id="common_menu"><tiles:insertAttribute name="menu" /></div>
		<div id="body"><tiles:insertAttribute name="body" /></div>
		<div id="footer"><tiles:insertAttribute name="footer" /></div>
	</body>
</html>