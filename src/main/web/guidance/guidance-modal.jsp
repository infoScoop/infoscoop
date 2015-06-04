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
<%
    String staticContentURL = PropertiesService.getHandle().getProperty("staticContentURL");
    if(".".equals(staticContentURL))
    	staticContentURL = "../";
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
	<link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/js/lib/twitter-bootstrap-wizard/prettify.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/guidance.css?Aaafe">

<!--start script-->
<script src="<%=staticContentURL%>/js/lib/jquery-1.9.1.min.js"></script>
<script src="<%=staticContentURL%>/js/lib/bootstrap-3.3.4-dist/js/bootstrap.min.js"></script>
<script src="<%=staticContentURL%>/js/lib/twitter-bootstrap-wizard/jquery.bootstrap.wizard.min.js"></script>
<script src="<%=staticContentURL%>/js/lib/twitter-bootstrap-wizard/prettify.js"></script>
<script src="<%=staticContentURL%>/js/guidance/GuidanceWindow.js?esesasa"></script>
<!--end script-->
<script src="../js/resources/resourceBundle.jsp"></script>
<script type="text/javascript">

IS_Portal = parent.IS_Portal;
IS_SiteAggregationMenu = parent.IS_SiteAggregationMenu;
IS_WidgetsContainer = parent.IS_WidgetsContainer;
IS_Widget = parent.IS_Widget;

$(document).ready(function() {
    $('#rootwizard').bootstrapWizard({onTabShow: function(tab, navigation, index) {
        
        switch(index){
            case 1: 
                if(!IS_Guidance.GuidanceWindow.initialized && $("#contents-main").length > 0){
                    var guidanceWindow = IS_Guidance.GuidanceWindow;
                    guidanceWindow.initialize("contents-main");
                    guidanceWindow.render();
                }
                break;
        }
        
        var $total = navigation.find('li').length;
        var $current = index+1;
        var $percent = ($current/$total) * 100;
        $('#rootwizard').find('.bar').css({width:$percent+'%'});
        
        // If it's the last tab then hide the last button and show the finish instead
        if($current >= $total) {
            $('#rootwizard').find('.pager .next').hide();
            $('#rootwizard').find('.pager .finish').show();
            $('#rootwizard').find('.pager .finish').removeClass('disabled');
        }
        else {
            $('#rootwizard').find('.pager .next').show();
            $('#rootwizard').find('.pager .finish').hide();
        }
        
        if($current == 1){
            $('#rootwizard').find('.pager .previous').hide();
        }
        else{
            $('#rootwizard').find('.pager .previous').show();
        }
    }});
    $('#rootwizard .finish').click(function() {
        parent.IS_GuidanceInstance.finish();
    });
});
</script>
</head>
<body>

<div id="rootwizard">
    <div class="navbar">
      <div class="navbar-inner">
        <div class="container">
		    <ul>
		        <li><a href="#tab1" data-toggle="tab">ようこそ</a></li>
		        <li><a href="#tab2" data-toggle="tab">コンテンツの追加</a></li>
		        <jsp:include page="jsp/navbar.jsp" flush="true"/>
		    </ul>
         </div>
      </div>
    </div>
    <div class="tab-content">
        <div class="tab-pane" id="tab1">
          <jsp:include page="jsp/welcome.jsp" flush="true"/>
        </div>
        <div class="tab-pane" id="tab2">

<h2 class="text-primary">興味のあるコンテンツをポータルに追加してみましょう。</h2>
<div id="contents-main">
    <div data-example-id="nav-tabs-with-dropdown" class="tab-bar">
        <ul class="nav nav-tabs"></ul>
    </div>
    <div class="tab-content"></div>
</div>

        </div>
        <jsp:include page="jsp/tab-panes.jsp" flush="true"/>
        <ul class="pager wizard">
            <li class="previous"><a href="javascript:;">Previous</a></li>
            <li class="next"><a href="javascript:;">Next</a></li>
            <li class="next finish" style="display:none;"><a href="javascript:;">Finish</a></li>
        </ul>
    </div>  
</div>

</body>
</html>
