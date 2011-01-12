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

<%@ page session="true" %>
<html>
<head>
	<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.js"></script>
	<link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.5/themes/smoothness/jquery-ui.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.5/jquery-ui.min.js"></script>
	<script type="text/javascript">
		$(window).load(function(){
			$("#submit").button();
		});
	</script>
	<style type="text/css">
		
	</style>
</head>
<body style="background:#D8F0F7;font-size:13px;">
<%
	if (request.getParameter("logout")!=null){
		session.removeAttribute("user");
		session.removeAttribute("Uid");
%>
	<p>Logged out!</p>
<%
	}
	if (session.getAttribute("openid")==null) {
%>
	<div id="inside" style="color:#414B56;margin:auto;width:950px;">
		<div id="header" style="text-align:center;">
			<img src="http://www.infoscoop4g.com/_/rsrc/1288869308415/config/customLogo.gif?revision=3" />
		</div>
		<div id="main" style="background:#FFF;color:#414B56;border:1px solid #B5E2F0;">
			<div id="main-header" style="text-align:center;">
				<p id="headline" style="font-size:2.2em;margin:10px 0;">infoScoop for Google AppsでGoogle Appsをもっと快適に</p>
				<span id="somthing" style=""></span>
			</div>
			<div id="form" style="text-align:center;">
				<form method="POST" action="openid_login">
					<input type="text" name="hd" size="" title="Google Appsのドメインを入力してください" style="-moz-border-radius-bottomleft:4px;
-moz-border-radius-bottomright:4px;
-moz-border-radius-topleft:4px;
-moz-border-radius-topright:4px;
border:2px solid #CCC;
font-size:24px;
height:46px;
margin-right:4px;
padding:7px 0 7px 9px;
vertical-align:bottom;
width:270px;
color:#414B56;"/>
					<input id="submit" type="submit" value="Login" style="vertical-align:text-bottom;"/>
				</form>
			</div>
			<div id="main-desc" style="width:100%;height:400px;">
				<div id="presentation" style="float:left;width:49%;height:;text-align:center;">
					<iframe src="https://docs.google.com/present/embed?id=ddqxmtjc_485g32zx5hp" frameborder="0" width="410" height="342"></iframe>
				</div>
				<div id="description" style="float:right;width:49%;height:;line-height:1.3em;margin-right:15px;">
					<div id="inline" style="width:445px;">
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
<%	
	} else {
%>
	<p>Logged in as <%= session.getAttribute("openid") %></p>
	<a href="?logout=true">Log out</a>

<% } %>
</body>
</html>
