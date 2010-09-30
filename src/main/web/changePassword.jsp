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
<%@ page import="org.infoscoop.service.PortalLayoutService, org.infoscoop.util.I18NUtil" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%
	String pageTitle = PortalLayoutService.getHandle().getPortalLayout("title");
	pageTitle = I18NUtil.resolve(I18NUtil.TYPE_LAYOUT, pageTitle, request.getLocale());
%>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<title><spring:message code="lb_changePassword" /></title>
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
						<td class="header"><spring:message code="lb_changePass" /></td>
					</tr>
					<tr>
					<td><hr/></td>
					</tr>
					<tr class="form">
						<td>
							<table border="0" cellpadding="3" cellspacing="0">
								<tr>
									<td align="right"><spring:message code="lb_userID" />&nbsp;</td>
									<td align="left"><input type="text" id="uid" name="uid"/></td>
								</tr>
								<tr>
									<td align="right"><spring:message code="lb_oldPassword" />&nbsp;</td>
									<td align="left"><input type="password" id="password" name="password"/></td>
								</tr>
								<tr>
									<td align="right"><spring:message code="lb_newpass" />&nbsp;</td>
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
                          %{<%= errorMsg %>}
                       </td>
                    </tr>
<%
}
%>
					<tr>
						<td align="center">
							<input type="submit" value="<spring:message code="lb_change" />"/>
							<input type="reset" value="<spring:message code="lb_reset" />"/>
						</td>
					</tr>
					<tr>
						<td align="center">
							<br/>
							<input type="button" onclick="javascript:location.href='./login.jsp';" value="<spring:message code="lb_toLogin" />"/>
						</td>
					</tr>
				</table>
				</form>
			</div>
		</center>
	</body>
</html>
