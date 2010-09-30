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

[
<%@page import="java.util.Iterator"%>
<%@page import="org.infoscoop.account.*"%>
<%@page import="org.infoscoop.util.*"%>
<%@page contentType="text/plain; charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%
//	out.print("[");
	out.print("{type:'UIDPrincipal',displayName:'<spring:message code="alb_userPrincipal" />'}");

	for( Iterator ite=SessionCreateConfig.getInstance().getPrincipalDefs().iterator();ite.hasNext();){
		PrincipalDef def = ( PrincipalDef )ite.next();
		out.println(",");
		out.print("{");
		out.print("type:'" + def.getType() + "'");
		out.print(",");
		out.print("displayName:'" + def.getLabel() + "'");
		out.print("}");
	}
	AuthenticationService service= AuthenticationService.getInstance();

	if(service != null) {
		for( Iterator ite=service.getPrincipalDefs().iterator();ite.hasNext();){
			PrincipalDef def = ( PrincipalDef )ite.next();
			out.println(",");
			out.print("{");
			out.print("type:'" + def.getType() + "'");
			out.print(",");
			out.print("displayName:'" + def.getLabel() + "'");
			out.print("}");
		}
	}
//	out.print("]");
%>
]
