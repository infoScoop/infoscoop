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
<%@page import="org.infoscoop.service.PropertiesService"%>
<%@page import="org.infoscoop.service.SquareService"%>
<%
    String staticContentURL = PropertiesService.getHandle().getProperty("staticContentURL");
    if(".".equals(staticContentURL))
    	staticContentURL = "../";
	String uid = (String) session.getAttribute("Uid");
	boolean isReachMaxSquare = SquareService.getHandle().isReachMaxSquare(uid);
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
		.required-label {
			color: #ff0000;
		}
	</style>

<!--start script-->
<script src="<%=staticContentURL%>/js/lib/jquery-1.9.1.min.js"></script>
<script src="<%=staticContentURL%>/js/lib/bootstrap-3.3.4-dist/js/bootstrap.min.js"></script>
<!--end script-->
<script src="../js/resources/resourceBundle.jsp"></script>
<script type="text/javascript">
$(document).ready(function() {
	if(<%=isReachMaxSquare%>) {
		$('#alert').addClass("alert alert-danger").attr('role', 'alert').text('%{lb_error_max_square}');
	}

	$('#quick-build').submit(function(event){
		event.preventDefault();
		$(this).find('button[type=submit]').attr('disabled', 'disabled');
		parent.IS_Portal.startIndicator();

		var deferred = $.post("../squaresrv/doCreate",{
			'square-name': $('#square-name').val(),
			'square-description': $('#square-description').val(),
			'square-source': $('#square-source').val(),
			'square-member': $('#square-member').val()
		});

		deferred.success(function (data, status, xhr) {
			parent.IS_SquareInstance.finish();
			var squareId = xhr.getResponseHeader("X-IS-SQUAREID");
			parent.location.href = '../squaresrv/doChange?square-id=' + squareId;
		});

		deferred.error(function (error) {
			var errorMessage;
			switch(error.status){
				case 400:
					errorMessage = '%{lb_error_invalid_email}';
					break;
				case 500:
					if(error.responseText == '500_01') {
						errorMessage = '%{lb_error_max_square}';
					} else {
						errorMessage = '%{lb_error_on_server}';
					}
					break;
			}
			$('#alert').addClass("alert alert-danger").attr('role', 'alert').text(errorMessage);
		});

		deferred.complete(function() {
			parent.IS_Portal.endIndicator();
			enabledBtn();
		});
	});
	disabledBtn();
});

function checkValue($this) {
	if(!$this.value || <%=isReachMaxSquare%>){
		disabledBtn();
	} else {
		enabledBtn();
	}
}

function disabledBtn() {
	$(document).find('button[type=submit]').attr('disabled', 'disabled');
}

function enabledBtn() {
	$(document).find('button[type=submit]').removeAttr('disabled');
}

function close() {
	parent.IS_SquareInstance.finish();
}
</script>
</head>
<body>
	<div class="container">
		<div id="alert" style="margin-top:20px;margin-bottom:-5px;"></div>
		<div class="row">
			<div class="col-sm-12">
				<h2>%{lb_create_square2}</h2>
				<hr style="margin-top:10px">
			</div>
		</div>
		<div class="row">
			<div class="col-sm-3">
			    <ul class="nav nav-pills nav-stacked" role="tablist">
					<li class="active"><a href="#tab1" data-toggle="pill">%{lb_quick_build}</a></li>
					<jsp:include page="jsp/navbar.jsp" flush="true"/>
			    </ul>
			</div>
			<div class="col-sm-9">
				<div id="square-tab-content" class="tab-content">
					<div class="tab-pane active" id="tab1">
						<jsp:include page="jsp/quick-build.jsp" flush="true"/>
					</div>
					<jsp:include page="jsp/tab-panes.jsp" flush="true"/>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
