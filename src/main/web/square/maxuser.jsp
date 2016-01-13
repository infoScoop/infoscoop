<%--
# infoScoop Calendar
# Copyright (C) 2010 Beacon IT Inc.
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see
# <http://www.gnu.org/licenses/old-licenses/gpl-2.0.html>.
--%>
<!DOCTYPE HTML>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="org.infoscoop.service.PropertiesService"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    String staticContentURL = PropertiesService.getHandle().getProperty("staticContentURL");
    if(".".equals(staticContentURL) || staticContentURL == null)
        staticContentURL = "../";
%>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Cache-Control" content="no-cache">
    <meta http-equiv="Expires" content="Thu, 01 Dec 1994 16:00:00 GMT">

    <link href="<%=staticContentURL%>/js/lib/bootstrap-3.3.4-dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%=staticContentURL%>/js/lib/bootstrap-3.3.4-dist/css/bootstrap-theme.min.css" rel="stylesheet">

    <style>
        body {
            margin: 5px;
        }
    </style>

	<script src="<%=staticContentURL%>/js/lib/jquery-1.9.1.min.js"></script>
	<script src="<%=staticContentURL%>/js/lib/bootstrap-3.3.4-dist/js/bootstrap.min.js"></script>
	<script type="text/javascript">
	function toTop(){
	    location.href = "../squaresrv/mySquare";
	}
	</script>

</head>
<body>
    <h2 class="text-primary">%{lb_square_maxuser}</h2>
    <p>%{lb_square_maxuser_desc}</p>
    <p>%{lb_call_square_admin}</p>
	<div style="text-align:center;">
	    <button style="margin:50px;" onclick="toTop();">%{lb_return_to_my_square}</button>
	</div>
</body>
</html>