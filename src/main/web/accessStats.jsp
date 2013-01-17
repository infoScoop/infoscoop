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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"><%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="org.infoscoop.service.PropertiesService"%>
<%String staticContentURL = PropertiesService.getHandle().getProperty("staticContentURL"); %>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Cache-Control" content="no-cache">
    <meta http-equiv="Expires" content="Thu, 01 Dec 1994 16:00:00 GMT">
<style type="text/css"><!--
html, body, table {
	margin:0;
	padding:0;
}
body {
	font-size: 0.9em;
}
a, a:link {
	color:#d42945;
	text-decoration:none;
	border-bottom:1px dotted #ffbac8;
	cursor:pointer;
}
a:visited {
	color:#d42945;
	border-bottom:none;
	text-decoration:none;
}
a:hover,
a:focus {
	color:#f03b58;
	border-bottom:1px solid #f03b58;
	text-decoration:none;
}
a.nolink {
	color:#f4f9fe;
	border-bottom:0;
	text-decoration:none;
	cursor:auto;
}
tr.head th{
	background-color:#eeeeee;
}
th{
	border:1px solid;
	padding:2px;
	border-color:#FDFDFD #93AFBA #93AFBA #FDFDFD;
	background-color:transparent;
	font-family:Tahoma;
	font-size:13px;
	font-weight:bold;
	color:#055A78;
	vertical-align:middle;
	text-align:center;
}
td{
	border:1px solid;
	border-color:#FDFDFD #93AFBA #93AFBA #FDFDFD;
	background-color:transparent;
	font-family:Tahoma;
	font-size:12px;
	word-break:break-all; 
	text-align:left;
}
tr.odd td{
	background:#f7fbfd;
}
.paging th{
	border:0;
}
.paging div{
	width:16px;
	height:16px;
	cursor:pointer;
	position:relative;
	margin:0 3px;
}
.paging div.nolink{
	visibility:hidden;
}
#firstlink{
	background: url(<%=staticContentURL%>/skin/imgs/control_start_blue.png) top left no-repeat;
	float:right;
}
#prevlink{
	background: url(<%=staticContentURL%>/skin/imgs/control_prevDay.png) top left no-repeat;
	float:right;
}
#nextlink{
	background: url(<%=staticContentURL%>/skin/imgs/control_nextDay.png) top left no-repeat;
	float:left;
}
#lastlink{
	background: url(<%=staticContentURL%>/skin/imgs/control_end_blue.png) top left no-repeat;
	float:left;
}
-->
</style>
<!--start script-->
<script src="<%=staticContentURL%>/js/lib/prototype-1.6.0.3.js"></script>
<script src="<%=staticContentURL%>/js/lib/date/date.js"></script>
<!--end script-->
<script src="js/resources/resourceBundle.jsp"></script>
<script type="text/javascript">
var rssUrl = "<%=request.getParameter("rssUrl")%>";
var limit = 20;
var pageno = 1;
var totalPage = 0;
var start = 0;
var isLoading = false;
function init(){
	if( window.top.Browser.isSafari1 ) {
		var statsElm = $("statslist");
		if( statsElm && statsElm.parentNode ) {
			statsElm.style.tableLayout = "";
			statsElm.parentNode.style.overflow = "auto";
			statsElm.style.width = "380px";
		}
		
		var statsHead = $("statsheader");
		if( statsHead ) {
			statsHead.style.width = "380px";
		}
	}
	
	loadStats(0, true);
}
function loadStats(_start, isInitialize){
	if(!isInitialize && (_start < 0 || _start >= (totalPage*limit))) return;

	isLoading = true;
	
	start = _start;
	
	var d = new Date();
	var headers = ["X-IS-TIMEZONE", String(-d.getTimezoneOffset())];
	var opt = {
		parameters:"rssUrl=" + encodeURIComponent(rssUrl) + "&start=" + start + "&limit=" + limit,
		requestHeaders:headers,
		onSuccess:function(req){
			var stats = eval("("+req.responseText+")");
			
			var data = stats.data;
			var statsElm = $("statslist");
			var tbody = document.createElement("tbody");
			for(var i = 0; i < data.length; i++){
				var tr = document.createElement("tr");
				if(i%2 == 1) tr.className = "odd";
				var nameTd = document.createElement("td");
				nameTd.style.overflow = "hidden";
				nameTd.width = "40%";
				
				var diaplayName = (data[i].name && data[i].name.length > 0)? data[i].name : "Guest user";
				nameTd.appendChild(document.createTextNode(diaplayName));
				tr.appendChild(nameTd);
				var dateTd = document.createElement("td");
				dateTd.width = "30%";
				dateTd.appendChild(document.createTextNode(formatDate(data[i].accessDate, "yyyy/MM/dd")));
				tr.appendChild(dateTd);
				var countTd = document.createElement("td");
				countTd.width = "30%";
				countTd.appendChild(document.createTextNode(data[i].count));
				tr.appendChild(countTd);
				tbody.appendChild(tr);
				
				if( window.top.Browser.isSafari1 ) {
					dateTd.style.overflow = "hidden";
					countTd.style.overflow = "hidden";
				}
			}
			statsElm.replaceChild(tbody, statsElm.firstChild);
			
			if(isInitialize) {
				var count = stats.results;
				totalPage = Math.ceil(count/limit);
				Event.observe($("firstlink"), "click", function(){loadStats(0)});
				Event.observe($("lastlink"), "click", function(){loadStats((totalPage-1)*limit)});
				$("totalpage").innerHTML = (totalPage > 0)? totalPage : 1;
				Event.observe($("prevlink"), "click", function(e){
					if(Event.element(e).className == "nolink") return;
					loadStats(start-limit);
				});
				Event.observe($("nextlink"), "click", function(e){
					if(Event.element(e).className == "nolink") return;
					loadStats(start+limit);
				});
			}
			
			pageno = start/limit + 1;
			$("pageno").innerHTML = pageno;
			if(pageno > 1)
				$("prevlink").className = "";
			else
				$("prevlink").className = "nolink";
			if(pageno < totalPage)
				$("nextlink").className = "";
			else
				$("nextlink").className = "nolink";
		},
		onException: function(req, e){
			console.log(e);
		},
		onComplete: function(){
			isLoading = false;
		}
	};
	new Ajax.Request("accessstatslist", opt);
}
</script>
</head>
<body onload="init()">
	<div>
	<table style="width:400px;" id="statsheader">
		<thead>
			<tr class="head">
				<th scope="col" width="40%" id="headerName"></th>
				<th scope="col" width="30%" id="headerDate"></th>
				<th scope="col" width="30%" id="headerCount"></th>
			</tr>
		</thead>
	</table>
	</div>
	<div style="width:400px;height:290px;overflow-y:auto;overflow-x:hidden;">
	<table style="width:400px;table-layout:fixed;" id="statslist">
		<tbody>
		</tbody>
	</table>
	</div>
	<div>
	<table style="width:400px;">
		<tfoot>
			<tr class="head">
				<th>
					<table style="width:370px;" class="paging" cellpadding="0" cellspacing="0" align="center">
						<th style="text-align:right;width:150px;">
							<div id="prevlink" title=""></div>
							<div id="firstlink" title=""></div>
						</th>
						<th style="width:70px">
							<span id="pageno"></span>/<span id="totalpage"></span>
						</th>
						<th style="text-align:left;width:150px;">
							<div id="nextlink" title=""></div>
							<div id="lastlink" title=""></div>
						</th>
					</table>
				</th>
			</tr>
		</tfoot>
	</table>
	</div>
	<table style="position:absolute;top:0;left:410px;width:180px;">
		<thead>
			<tr class="head">
				<th scope="col" id="countHeaderSpan"></th>
				<th scope="col" id="countHeaderCount"></th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td id="countOneDay"></td>
				<td><%= request.getAttribute("onedaycnt") %></td>
			</tr>
			<tr class="odd">
				<td id="countOneWeek"></td>
				<td><%= request.getAttribute("oneweekcnt") %></td>
			</tr>
			<tr>
				<td id="countOneMonth"></td>
				<td><%= request.getAttribute("onemonthcnt") %></td>
			</tr>
			<tr class="odd">
				<td id="countSixMonth"></td>
				<td><%= request.getAttribute("sixmonthcnt") %></td>
			</tr>
			<tr>
				<td id="countAll"></td>
				<td><%= request.getAttribute("allcnt") %></td>
			</tr>
		</tbody>
	</table>
</body>
<script type="text/javascript">
$("headerName").innerHTML = IS_R.lb_name;
$("headerDate").innerHTML = IS_R.lb_date;
$("headerCount").innerHTML = IS_R.lb_inspectionCount;
$("countHeaderSpan").innerHTML = IS_R.lb_span;
$("countHeaderCount").innerHTML = IS_R.lb_inspectionCount;
$("countOneDay").innerHTML = IS_R.lb_countOneDay;
$("countOneWeek").innerHTML = IS_R.lb_recentOneWeek;
$("countOneMonth").innerHTML = IS_R.lb_recentOneMonth;
$("countSixMonth").innerHTML = IS_R.lb_recentSixMonth;
$("countAll").innerHTML = IS_R.lb_all;
$("firstlink").title = IS_R.lb_firstPage;
$("prevlink").title = IS_R.lb_prev;
$("nextlink").title = IS_R.lb_next;
$("lastlink").title = IS_R.lb_lastPage;
</script>
</html>
