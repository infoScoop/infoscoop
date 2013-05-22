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
<%@ page contentType="text/html; charset=UTF8" %>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>%{lb_shortcutsKeyList}</title>
<style>
	*{font-size:12px;font-weight:normal;line-height:150%}
	.dec_solid{border:1px solid #888;padding:2px; background-color:#FFFFFF;}
	.dec_solid img{vertical-align:middle;}
	table{
	    border-top:1px dashed #DCDCDC;
	    border-left:1px dashed #DCDCDC;
	    border-collapse:collapse;
	    border-spacing:0;
	    background-color:#ffffff;
	    empty-cells:show;
	}
	th{
	    border-right:1px dashed #DCDCDC;
	    border-bottom:1px dashed #DCDCDC;
	    padding:0.2em 0.2em;
	}
	td{
	    border-right:1px dashed #DCDCDC;
	    border-bottom:1px dashed #DCDCDC;
	    padding:0.2em 0.2em;
	}

</style>
<script type="text/javascript"> 
/*
document.onkeydown = function(e) { 
    var shift, ctrl; 

    // Mozilla(Firefox, NN) and Opera 
    if (e != null) { 
        keycode = e.which; 
        ctrl = typeof e.modifiers == 'undefined' ? e.ctrlKey : e.modifiers & Event.CONTROL_MASK; 
        shift = typeof e.modifiers == 'undefined' ? e.shiftKey : e.modifiers & Event.SHIFT_MASK; 
    // Internet Explorer 
    } else { 
        keycode = event.keyCode; 
        ctrl = event.ctrlKey; 
        shift = event.shiftKey; 
    } 
	
	if(keycode == "72"){
		window.opener.focus();
		window.close();
	}
} 
*/
</script> 
</head>
<body>
<div style="font-weight:bold;">%{lb_shortcutsKeyList}</div><br>
<table>
	<tr>
		<th>%{lb_nextItem}</th>
		<td>
			<span class="dec_solid"><img src="../../../skin/imgs/arrow2_down.gif"></span>
		</td>
	</tr>
	<tr>
		<th>%{lb_prevItem}</th>
		<td>
			<span class="dec_solid"><img src="../../../skin/imgs/arrow2_up.gif"></span>
		</td>
	</tr>
	<tr>
		<th>%{lb_fullDisplay}</th>
		<td><span class="dec_solid">enter</span> or <span class="dec_solid"><img src="../../../skin/imgs/arrow2_right.gif"></span> %{lb_maximizeShortcutsitemAndSummary}</td>
	</tr>
	<tr>
		<th>%{lb_summaryView}</th>
		<td><span class="dec_solid"><img src="../../../skin/imgs/arrow2_left.gif"></span> %{lb_maximizeShortcutsItemAndFull}</td>
	</tr>
	<tr>
		<th>%{lb_sendMail}</th>
		<td>
			<span class="dec_solid">shift + M</span>
		</td>
	</tr>
	<tr>
		<th>%{lb_refresh}</th>
		<td>
			<span class="dec_solid">r</span> 
		</td>
	</tr>
	<tr>
		<th>%{lb_releaseMaximize}</th>
		<td>
			<span class="dec_solid">q</span>
		</td>
	</tr>
	<tr>
		<th>%{lb_dateDisplay}</th>
		<td>
			<span class="dec_solid">t</span>
		</td>
	</tr>
	<tr>
		<th>%{lb_summaryAllView}</th>
		<td>
			<span class="dec_solid">m</span>
		</td>
	</tr>
	<tr>
		<th>%{lb_showHelp}</th>
		<td>
			<span class="dec_solid">h</span>
		</td>
	</tr>
</table>
<br>
<p class="notice">%{ms_shortcutsNotice}<br>
</p>
</body>
</html>
