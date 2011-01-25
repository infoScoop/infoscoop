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
<%@ page contentType="text/html; charset=UTF8" %>
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<link rel="shortcut icon" href="favicon.ico" >
	<title>infoScoop for Google Apps</title>
	<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.js"></script>
	<link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.5/themes/smoothness/jquery-ui.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.5/jquery-ui.min.js"></script>
	<script type="text/javascript" src="http://alpha.infoscoop4g.com/js/lib/jquery.placeholder.js"></script>
	<script type="text/javascript">
		$(function(){
			$("#domain_field").placeholder();
			$("#submit").button();
			$(form).submit(function(){
				if($('input[name="hd"]').val().match(/^.*\@.*$/)){
					alert('Emailアドレスではなくドメインのみを指定してください。');
					return false;
				}else if(!$('input[name="hd"]').val().match(/^.*\.[a-zA-Z]*$/)){
					alert('正しいドメイン名を指定してください。');
					return false;
				}
			})
		});
	</script>
<style type="text/css">
body{
	font-family:'ヒラギノ角ゴ Pro W3','Hiragino Kaku Gothic Pro','ＭＳ Ｐゴシック',sans-serif;
}
#domain_field{
	-moz-border-radius:4px;
	-webkit-border-radius:4px;
	border-radius:4px;
	border:2px solid #CCC;
	font-size:17px;
	height:46px;
	margin-right:4px;
	padding:7px 0 7px 9px;
	vertical-align:bottom;
	width:310px;
	color:#414B56;
}

#domain_field.placeholder, #domain_field.placeholderActive{
	color:#AAA;
}
#submit{
	height:46px;
}
#main{
	background: #FFFFFF;
	border: 1px solid #B5E2F0;
	color: #414B56;
	margin-top:20px;
}
#main-header{
	text-align:center;
	margin-top:15px;

}
#form{
	text-align:left;
	width:420px;
	margin-top:40px;
	margin-left:auto;
	margin-right:auto;
	margin-bottom:30px;
}
#ex_domain{
	text-align:left;
}
#headline{
	font-size:2.2em;
}
#main-desc{
	text-align:left;
}
#description{
	line-height:1.3em;
	margin:0px 80px 50px;
}
</style>
<script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-19564588-2']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>
</head>
<body style="background:#D8F0F7;font-size:13px;text-align:center;">
<%
	if (request.getParameter("logout")!=null){
		session.removeAttribute("user");
		session.removeAttribute("Uid");
	}
%>
	<div id="inside" style="color:#414B56;margin-left:auto;margin-right:auto;width:860px;">
		<div id="header" style="text-align:center;">
			<img src="http://www.infoscoop4g.com/_/rsrc/1288869308415/config/customLogo.gif?revision=3" />
		</div>
		<div id="main">
			<div id="main-header">
				<p id="headline">infoScoop for Google AppsでGoogle Appsをもっと快適に</p>
			</div>
			<div id="form">
				<form method="POST" action="openid_login">
					<div>
						<input type="text" name="hd" placeholder="Google Appsのドメインを入力してください" title="Google Appsのドメインを入力してください" id="domain_field" value=""/>
						<input id="submit" type="submit" value="ログイン"/>
					</div>
					<div style="padding:4px;color:#999;font-size:18px;">例：infoscoop.org</div>
				</form>
			</div>
			<div id="main-desc">
				<div id="description">
					<div id="inline">
						<h2 style="margin-top:0;">infoScoop for Google Appsとは</h2>
						<div>
						infoScoop for Google Appsは、<a target="_blank" href="http://www.google.com/apps/intl/ja/business/index.html">Google Apps</a>をさらに使いやすくするサービスです。<br>
	Google Appsの各サービスをポータル上でまとめることで、散在しているGoogle Appsの各コンテンツを一箇所で一望することができます。<br>
	<br>企業情報ポータル<a rel="nofollow" target="_blank" href="http://www.infoscoop.org">infoScoop OpenSource</a>を利用したサービスであるため、Google Appsだけでなく、他のサービスのコンテンツも合わせて集約できます。<br>ガジェット標準仕様のOpenSocialに対応しているので、iGoogleガジェットを動かすこともでき、カスタムガジェットを作ればさらに独自サービスとも繋がります。
						</div>
						<h3>特徴</h3>
						<ul style="padding-left: 30px;"><li><font size="3" style="color: rgb(86, 156, 56);">Google ドキュメント</font> <br>
	検索条件に一致したコンテンツをガジェットとして保存。見たい情報だけを手元に。</li>
	<li><font size="3" style="color: rgb(86, 156, 56);">Gmail</font> <br>
	ラベルごとにガジェットを配置。効率的に情報を取得。</li>
	<li><font size="3"><span style="color: rgb(86, 156, 56);">Google カレンダー</span></font> <br>
	グループメンバーを検索してマイグループとして保存。皆のスケジュールを一度に概観<font size="1">。</font></li></ul>
					</div>
				</div>
			</div>
		</div>
		<div id="footer"></div>
	</div>
</body>
</html>
