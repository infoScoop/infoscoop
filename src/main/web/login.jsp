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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html; charset=UTF8" %>
<%@ page import="org.infoscoop.account.AuthenticationService, org.infoscoop.service.PortalLayoutService, org.infoscoop.util.*" %>
<%
	String pageTitle = PortalLayoutService.getHandle().getPortalLayout("title");
	pageTitle = I18NUtil.resolve(I18NUtil.TYPE_LAYOUT, pageTitle, request.getLocale());
	AuthenticationService authService = AuthenticationService.getInstance();
%>

<%@page import="org.infoscoop.dao.PropertiesDAO"%><html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<title><%= pageTitle %> %{lb_loginTitle}</title>
		
		<!-- favicon.ico -->
		<link rel="shortcut icon" href="favicon.ico">
		
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
				<form id="loginform" method="post" action="authsrv/login">
				<table class="LoginForm">
					<tr>
						<td class="header"><%= pageTitle %> %{lb_loginTitle}</td>
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
									<td align="right">%{lb_password}&nbsp;</td>
									<td align="left"><input type="password" id="password" name="password"/></td>
								</tr>
<%
int keepPeriod = 14;
try {
	keepPeriod = Integer.parseInt( PropertiesDAO.newInstance()
			.findProperty("loginStateKeepPeriod").getValue());
} catch( Exception ex ) {
}

if( keepPeriod > 0 ) {
%>
								<tr>
									<td colspan=2" align="right"><input type="checkbox" name="saveLoginState"><label>%{lb_saveLoginState}</label></td>
								</tr>
<%}%>
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
                          %{<%= errorMsg %>}
                       </td>
                    </tr>
<%
}
%>
					<tr>
						<td align="center">
							<input type="submit" value="%{lb_login}"/>
							<input type="reset" value="%{lb_reset}"/>
						</td>
					</tr>
<%if(authService != null && authService.enableChangePassword()){%>
					<tr>
						<td align="center">
							<br/>
							<input type="button" onclick="javascript:location.href='./changePassword.jsp';" value="%{lb_changePassword}"/>
						</td>
					</tr>
<%}%>
				</table>
				</form>
			</div>
		</center>
		<script type="text/javascript">
			document.getElementById('uid').focus();
		</script>
	</body>
</html>
