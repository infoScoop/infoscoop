<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html; charset=UTF8" %>
<%@ page import="org.infoscoop.service.PortalLayoutService, org.infoscoop.util.I18NUtil" %>
<%
	String pageTitle = PortalLayoutService.getHandle().getPortalLayout("title");
	pageTitle = I18NUtil.resolve(I18NUtil.TYPE_LAYOUT, pageTitle, request.getLocale());
%>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<title>%{lb_changePassword}</title>
        <style>
.header{
  font-size:14px;
  color:gray;
  font-weight:bold;
}
		</style>
 	</head>
	<body style="margin-top:0;padding-top:160px;">
		<center>
			<div>
				<form id="loginform" method="post" action="authsrv/changePassword">
				<table class="LoginForm">
					<tr>
						<td class="header">%{lb_changePass}</td>
					</tr>
					<tr>
					<td><hr/></td>
					</tr>
					<tr class="form">
						<td>
							<table border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td align="right">%{lb_userID}&nbsp;</td>
									<td align="left"><input type="text" id="uid" name="uid"/></td>
								</tr>
								<tr>
									<td align="right">%{lb_oldPassword}&nbsp;</td>
									<td align="left"><input type="password" id="password" name="password"/></td>
								</tr>
								<tr>
									<td align="right">%{lb_newpass}&nbsp;</td>
									<td align="left"><input type="password" id="new_password" name="new_password"/></td>
								</tr>
							</table>
						</td>
					</tr>
<%
String errorMsg = (String)session.getAttribute("errorMsg");
if(errorMsg != null){
  session.removeAttribute("errorMsg");
%>
                    <tr>
                       <td style="color:red;">
                          <%= errorMsg %>
                       </td>
                    </tr>
<%
}
%>
					<tr>
						<td align="center">
							<input type="submit" value="%{lb_change}"/>
							<input type="reset" value="%{lb_reset}"/>
						</td>
					</tr>
					<tr>
						<td align="center">
							<br/>
							<input type="button" onclick="javascript:location.href='./login.jsp';" value="%{lb_toLogin}"/>
						</td>
					</tr>
				</table>
				</form>
			</div>
		</center>
	</body>
</html>
