<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="user"/>
	<tiles:putAttribute name="title" value="syncmaster.title"/>
	<tiles:putAttribute name="body" type="string">

<link href="http://jsajax.com/jQuery/prettyLoader/css/prettyLoader.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="http://jsajax.com/jQuery/prettyLoader/js/jquery.prettyLoader.js"></script>
<script type="text/javascript">

$(function(){
	$.prettyLoader({ loader: 'http://jsajax.com/jQuery/prettyLoader/images/prettyLoader/ajax-loader.gif' });

	$("#syncButton").button().click(function(){
		$.prettyLoader.show();
		$("#buttons input").attr("disabled", "disabled");
		$("#syncButton").button("disable");
		$("#message").css("visibility","visible");
		$("#message").html('<img src="../../skin/imgs/ajax-loader.gif"/>同期しています。');
		$.get('sync', {"acl":$("#acl:checked").length == 1}, function(data){
			$("#message").html(data);
			$("#buttons input").removeAttr("disabled");
			$("#syncButton").button("enable");
		});
	});
});

</script>

<p>
この画面では以下の処理を行うことができます。
<ul>
	<li>Google Appsのユーザとグループ(メーリングリスト)をinfoScoopに取り込みます。</li>
	<li>Google DocsにinfoScoop用の共有フォルダを作成します。</li>
	<li>共有フォルダにドメイン内の全ユーザの閲覧権限を付与します。</li>
</ul>
この処理は、ユーザ数およびグループ数に応じて数分から数十分の時間がかかります。<br>
初期セットアップ時やユーザ/グループに変更があった場合に実行してください。<br>
</p>
<br>
<div id="buttons">
	<input id="syncButton" type="button" value="同期"/>
	<input type="checkbox" id="acl" checked><label for="acl">共有フォルダに閲覧権限の自動付与を行う</label>
</div>
<div>
	<span id="message" style="visibility:hidden"></span>
	<div id="prog" style="width:250px; visibility:hidden"></div>
</div>
<br>
<p>
共有フォルダに閲覧権限の自動付与を行うと、全ユーザに共有された旨がメールされます。<br>
閲覧権限の自動付与は行いたくない場合は上のチェックを外してください。<br>
ただし、閲覧権限の自動付与を行わない場合は、閲覧権限を手動で付与する必要があります。
</p>
	</tiles:putAttribute>
</tiles:insertDefinition>