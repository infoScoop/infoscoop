<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="user"/>
	<tiles:putAttribute name="title" value="syncmaster.title"/>
	<tiles:putAttribute name="body" type="string">

<script type="text/javascript" class="source">
function sync(){
	window.location.href = "sync";
}
</script>
<link href="http://jsajax.com/jQuery/prettyLoader/css/prettyLoader.css" rel="stylesheet" type="text/css" />
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js" type="text/javascript"></script>
<script type="text/javascript" src="http://jsajax.com/jQuery/prettyLoader/js/jquery.prettyLoader.js"></script>
<script type="text/javascript">

$(function(){
	$.prettyLoader({ loader: 'http://jsajax.com/jQuery/prettyLoader/images/prettyLoader/ajax-loader.gif' });

	$("#syncButton").click(function(){
		$.prettyLoader.show();
		$("#message").css("visibility","visible");
	});
});


/*
var n;
function start(){
  n++;
  $("#prog").progressbar("value",n*10);
  if (n==10){
    clearInterval(a);
  }
}
$(function(){
  $("#prog").progressbar();
  $("#addButton").click(function(){
    $("#message").css("visibility","visible");
    n=0;
    $("#prog").progressbar("value",0);
    $("#prog").css("visibility","visible");
    a = setInterval("start()",500);
  });
});
*/

</script>

<p>
Google Appsのユーザとグループ(メーリングリスト)をinfoScoopに取り込みます。<br>
この処理は、ユーザ数およびグループ数に応じて数分から数十分の時間がかかります。
</p>

<div>
	<input id="syncButton" type="button" value="同期" onclick="sync()"/>
	<span id="message" style="visibility:hidden">同期しています。</span>
	<div id="prog" style="width:250px; visibility:hidden"></div>
	
</div>

	</tiles:putAttribute>
</tiles:insertDefinition>