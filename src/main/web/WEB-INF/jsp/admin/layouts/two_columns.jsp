<!doctype HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html; charset=UTF-8" errorPage="/jspError.jsp" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

		<div id="admin-side" >
			<tiles:insertAttribute name="side_bar"/>
		</div>
		<div id="admin-side-body">
			<tiles:insertAttribute name="side_body" />
		</div>