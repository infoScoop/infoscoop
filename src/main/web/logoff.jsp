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
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<title><spring:message code="ms_sessionTimeout" /></title>
		<style type="text/css">
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
				<form>
				<table class="LoginForm">
					<tr>
						<td class="header"><spring:message code="ms_sessionTimeout" /></td>
					</tr>
					<tr>
					<td><hr/></td>
					</tr>
					<tr class="form">
						<td>
							<p>
								<spring:message code="ms_sessionTimeoutByOtherLogin" />
							</p>
						</td>
					</tr>
					<tr>
						<td align="center">
							<input type="button" value="<spring:message code="lb_relogin" />" onclick="location.href='index.jsp'"/>
						</td>
					</tr>
				</table>
				</form>
			</div>
		</center>
	</body>
</html>
