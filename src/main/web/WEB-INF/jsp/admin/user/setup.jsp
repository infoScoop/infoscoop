<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tiles:insertDefinition name="setup.definition" flush="true">
	<tiles:putAttribute name="body" type="string">
<style type="text/css">
#common_menu{
	height:33px;
}
#setupBody{
	padding:5px 15px;
	margin:5px;
	font-size:14px;
	background-color:#FFF;
}
#setupBody h2{
	font-size:20px;
}
#setupBody p{
	line-height:19px;
}
#setupBody ul{
	list-style:none;
	padding-left:20px;
	margin:0;
}
#setupBox{
	border:1px solid #CCC;
	margin:10px 0;
}
ul#setupItems{
	list-style-type:decimal;
	padding:20px 20px 20px 40px;
}
#setupItems li{
	position:relative;
}
#setupItems li input{
}
#setupItems li .detail{
	position:absolute;
	top:0;
	left:25px;
	color:#999;
}
#setupItems li .detail.enable{
	color:#000;
}
#setupItems li.current .detail.enable{
	background-color:yellow;
	font-weight:bold;
}
#setupItems li .detail .done{
	font-weight:bold;
	margin-left:10px;
}
#setupBody dd{
	margin-left:10px;
	font-size:12px;
}
#setupBody #loading{
	background:url(../../skin/imgs/manager/bouncing-ball.gif);
	width:16px;
	height:16px;
	margin-bottom:10px;
	margin-left:20px;
	display:none;
	float:left;
}
#setupBody #message{
	margin-bottom:10px;
	margin-left:20px;
	padding-left:16px;
	display:none;
	font-weight:bold;
}
#setupBody #error{
	color: #F00;
	margin-left:10px;
}
</style>
<script type="text/javascript">
$(function(){
	var steps;
	function execStep(){
		$("#setupItems li").removeClass("current");
		var step = steps.shift();
		if(step){
			var li = $("#"+step.id).parent().addClass("current");
			$("#message").show();
			$("#notice").text(step.msg);
			$("#error").text("");
			$.ajax({
				url:step.url,
				success:function(){
					li.find(".enable .done").text("(Done)");
					execStep();
				},
				error:function(){
					$("#loading").hide();
					$("#execButton").button("enable");
					$("#error").text("エラーが発生しました。").show();
				}
			});
		} else {
			$("#notice").text("セットアップが完了しました。");
			$("#loading").hide();
			$("#portalButton").button("enable");
		}
	}
	$("#itemFolder").click(function(){
		if(this.checked){
			$("#itemAcl").attr("checked", "checked").removeAttr("disabled")
				.siblings(".detail").addClass("enable");
			$(this).siblings(".detail").addClass("enable");
		} else {
			$("#itemAcl").removeAttr("checked").attr("disabled","disabled")
				.siblings(".detail").removeClass("enable");
			$(this).siblings(".detail").removeClass("enable");
		}
	});
	$("#itemAcl").click(function(){
		$(this).siblings(".detail").toggleClass("enable", !!this.checked);
	});
	$("#execButton").button().click(function(){
		$("#setupItems input").attr("disabled", "disabled");
		$("#execButton").button("disable");
		$("#loading").show();
		steps = [
			{url:"initDomain", id:"itemDomain", msg:"初期データを作成しています..."},
			{url:"syncUser", id:"itemUser", msg:"ユーザ/グループを同期しています..."}
		];
		if($("#itemFolder").attr("checked")){
			var url = "initFolder";
			if($("#itemAcl").attr("checked"))
				url += "?acl=true"
			steps.push({url:url, id:"itemFolder", msg:"共有フォルダを作成しています..."});
		}
		execStep();
		/*$.get('sync', {"acl":$("#acl:checked").length == 1}, function(data){
			$("#message").html(data);
			$("#portalButton").button("enable");
		})*/;
	});
	$("#portalButton").button({disabled:true}).click(function(){
		location.href = "../..";
	});
});

</script>

<h2>infoScoop for Google Appsをご利用いただきありがとうございます。</h2>
<p>
この画面でinfoScoop for Google Appsの初期セットアップを行います。<br>
以下の項目からセットアップする項目を選択し、「実行」ボタンをクリックしてください。<br>
この処理は、ユーザ数およびグループ数に応じて数分から数十分の時間がかかります。<br>
</p>
<div id="setupBox">
	<ul id="setupItems">
		<li>
			<input type="checkbox" id="itemDomain" checked disabled>
			<div class="detail enable">あなたのドメイン用の初期データを作成します。<span class="done"></span></div>
		</li>
		<li>
			<input type="checkbox" id="itemUser" checked disabled>
			<div class="detail enable">Google Appsのユーザとグループ(メーリングリスト)をinfoScoopに取り込みます。<span class="done"></span></div>
		</li>
		<li>
			<input type="checkbox" id="itemFolder" checked>
			<div class="detail enable">Google DocsにinfoScoop用の共有フォルダを作成します。(※1)<span class="done"></span></div>
			<ul>
				<li>
					<input type="checkbox" id="itemAcl" checked>
					<div class="detail enable">共有フォルダにドメイン内の全ユーザの閲覧権限を付与します。(※2)<span class="done"></span></div>
				</li>
			</ul>
		</li>
	</ul>
	<div id="loading"></div>
	<div id="message"><span id="notice"></span><span id="error"></span></div>
</div>
<div id="buttons">
	<input id="execButton" type="button" value="実行"/>
	<input id="portalButton" type="button" value="ポータル画面へ"/>
</div>
<p>
<dl>
	<dt>※1</dt>
	<dd>
		この処理を行わなかった場合は、お知らせガジェットおよびリンク集ガジェットが利用できません。<br>
		ただし、後から管理画面で行うこともできます。
	</dd>
</dl>
<dl>
	<dt>※2</dt>
	<dd>
		共有フォルダに閲覧権限の自動付与を行うと、全ユーザに共有された旨がメールされます。<br>
		閲覧権限の自動付与は行いたくない場合は上のチェックを外してください。<br>
		ただし、閲覧権限の自動付与を行わない場合は、閲覧権限を手動で付与する必要があります。
	</dd>
</dl>
</p>
	</tiles:putAttribute>
</tiles:insertDefinition>