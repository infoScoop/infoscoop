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

<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<!DOCTYPE HTML>

<%
response.setHeader("Pragma","no-cache");
response.setHeader("Cache-Control", "no-cache");
%>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<script src="./js/lib/prototype-1.7.1.js"></script>

<style><!--
.popup {
	position: absolute;
	top: 0px;
	left: 0px;
	width: 100%;
	height: 100%;
}
.overlay {
	position: fixed;
	background-color: black;
	opacity: 0.25;
	filter: alpha( opacity=25 );
}
.overlayContent {
	width: 90%;
	margin: 5%;
	margin-bottom: 0px;
	overflow: hidden;
}
.close {
	width:60px;
	height:20px;
	text-align:center;
	position: absolute;
	z-index:10;
	background-color: white;
	opacity: 0.7;
	cursor:pointer;
	filter: alpha( opacity=70 );
}
#iframe {
	background-color: white;
}
#wrapper {
	background-color: white;
	opacity: 0.5;
	z-index:5;
	filter: alpha( opacity=50 );
}
--></style>
<script type="text/javascript">

var preview;
var overlay;
var close;
var iframe;
var wrapper;
var timer;
function init() {
	preview = $("preview");
	
	overlay = $("overlay");
	Event.observe( overlay,"click",hidePreview,false,false );
	close = $("close");
	Event.observe( close,"click",hidePreview,false,false );
	
	iframe = $("iframe");
	wrapper = $("wrapper");
	
	hidePreview();
}
function showPreview( uid ) {
	iframe.style.height = wrapper.style.height = "90%";
	iframe.src = "mergeprofile?<%=org.infoscoop.web.CheckDuplicateUidFilter.IS_PREVIEW%>=true&Uid="+uid;
	preview.style.display = "";
	
	Event.observe( iframe,"load",function() {
		timer = setInterval( resize,100 );
	},false,true );
}
function resize() {
	var height;
	try {
		if( iframe.contentDocument && iframe.contentDocument.body ) {
			height = iframe.contentDocument.body.offsetHeight;
		} else {
			height = iframe.contentWindow.document.body.scrollHeight;
			overlay.style.height = "100%";
			if( overlay.offsetHeight < height *1.2 ){
				overlayHeight = height *1.2
				overlay.style.height = overlayHeight + 'px';
			}
		}
	} catch( ex ) {
		return;
	}
	
	iframe.style.height = wrapper.style.height = height;
	
	var offsets = Position.positionedOffset(iframe);
    close.style.top     = offsets[1] + 'px';
	close.style.left    = offsets[0] + iframe.offsetWidth - 62 + 'px';
}
function hidePreview() {
	preview.style.display = "none";
//	iframe.src = "about:blank";
	
	clearTimeout( timer );
}
function check(form){
	var isChecked = false;
	for(var i = 0; i < form.Uid.length; i++){ 
		if(form.Uid[i].checked)isChecked=true; 
	};
	if(!isChecked){
		alert('%{ms_selectProfile}');
		return false;
	}else{
		return true;
	}
}

</script>
</head>
<body style="margin:0;padding:0;" onload="init()">
<h3>%{ms_duplicateProfile}</h3>
<p>%{ms_selectValidProfile}</p>
<%
Map uidMap = ( Map )session.getAttribute("dupeIdMap");
%>
<form action="mergeprofile" method="get" onsubmit="return check(this);">
<table border="1">
<thead><tr><th>%{lb_select}</th><th>%{lb_userID}</th><th>%{lb_lastAccessDate}</th></tr></thead>
<% for( Iterator keys=uidMap.keySet().iterator();keys.hasNext();) {
	String uid = ( String )keys.next();
	String lastModified = ( String )uidMap.get( uid );
%>
	<tr><td align="center"><input type="radio" name="Uid" value="<%=uid%>"/></td><td><%=uid%></td><td><%=lastModified%></td><td><a id="<%=uid%>" href="javascript:showPreview('<%=uid%>');">%{lb_previewScreen}</a></td></tr>
<%} %>
	<tr><td><input type="submit" value="%{lb_select}"/></td><td colspan="2"></td></tr>
</table>
</form>
<div id="preview" class="popup">
	<div id="overlay" class="popup overlay"></div>
	<div id="close" class="popup close">%{lb_close}</div>
	<iframe id="iframe" class="popup overlayContent" frameBorder="0"></iframe>
	<iframe id="wrapper" class="popup overlayContent" frameBorder="0"></iframe>
</div>
</body>
</html>
