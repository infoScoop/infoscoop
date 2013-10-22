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

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML>
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
	    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
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
				<form id="loginForm" name="loginForm" action="<c:url value="/login.do"/>" method="post">
					<table class="LoginForm">
						<tr>
							<td class="header"><%= pageTitle %> %{lb_loginTitle}</td>
						</tr>
						<tr><td><hr/></td></tr>
						<tr class="form">
							<td>
								<table border="0" cellpadding="3" cellspacing="0">
									<tr>
										<td align="right">%{lb_userID}&nbsp;</td>
										<td align="left"><input type="text" id="uid" name="j_username" value="admin" /></td>
									</tr>
									<tr>
										<td align="right">%{lb_password}&nbsp;</td>
										<td align="left"><input type="password" id="password" name="j_password" value="admin" /></td>
									</tr>
								</table>
							</td>
						</tr>
						<c:if test="${not empty param.authentication_error}">
	                       <td style="color:red;">
	                       	Your login attempt was not successful.
	                       </td>
						</c:if>
						<c:if test="${not empty param.authorization_error}">
		                <tr>
	                       <td style="color:red;">
	                       	You are not permitted to access that resource.
	                       </td>
	                    </tr>
						</c:if>
						<tr>
							<td align="center">
								<input type="submit" name="login" value="%{lb_login}"/>
								<input type="reset" value="%{lb_reset}"/>
							</td>
						</tr>
					</table>
				</form>
			</div>
		</center>
		<script type="text/javascript">
			document.getElementById('uid').focus();
		</script>
	</body>
</html>