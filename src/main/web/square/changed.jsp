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
<%@ page import="java.util.Map"%>
<%@page import="org.infoscoop.util.I18NUtil"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    Map i18n = I18NUtil.getResourceMap(I18NUtil.TYPE_JS, request.getLocale());
%>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Cache-Control" content="no-cache">
    <meta http-equiv="Expires" content="Thu, 01 Dec 1994 16:00:00 GMT">
    <script type="text/javascript">
    function reload(){
        if(top != window.self){
            alert( "<%= i18n.get("ms_on11000") %>" );
            top.window.location.reload( true );
        }else{
            alert( "<%= i18n.get("ms_square_changed_reload_adminpage") %>" );
        }
    }
    </script>

</head>
<body onload="reload();">
</body>
</html>