<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:useAttribute name="title"/>
<tiles:useAttribute name="type" scope="request"/>
<html>
  <head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title><spring:message code="manager.title" /> - <spring:message code="${title}" /></title>
    <link rel="stylesheet" type="text/css" href="../../skin/manager.css">
  </head>
  <body>
    <div><tiles:insertAttribute name="header" /></div>
    <div><tiles:insertAttribute name="menu" /></div>
    <div><tiles:insertAttribute name="body" /></div>
    <div><tiles:insertAttribute name="footer" /></div>
  </body>
</html>