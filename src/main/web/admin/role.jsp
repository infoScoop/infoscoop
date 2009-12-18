[
<%@page import="java.util.Iterator"%>
<%@page import="org.infoscoop.account.*"%>
<%@page import="org.infoscoop.util.*"%>
<%@page contentType="text/plain; charset=UTF-8" %>
<%
//	out.print("[");
	out.print("{type:'UIDPrincipal',displayName:'%{alb_userPrincipal}'}");

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