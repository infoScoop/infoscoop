<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html; charset=UTF8" %>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<title>%{ms_sessionTimeout}</title>
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
						<td class="header">%{ms_sessionTimeout}</td>
					</tr>
					<tr>
					<td><hr/></td>
					</tr>
					<tr class="form">
						<td>
							<p>
								%{ms_sessionTimeoutByOtherLogin}
							</p>
						</td>
					</tr>
					<tr>
						<td align="center">
							<input type="button" value="%{lb_relogin}" onclick="location.href='index.jsp'"/>
						</td>
					</tr>
				</table>
				</form>
			</div>
		</center>
	</body>
</html>
