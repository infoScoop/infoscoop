<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="org.infoscoop.service.PortalAdminsService" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%
	PortalAdminsService adminService = PortalAdminsService.getHandle();
	boolean isTreeAdminUser = !adminService.isPermitted("menu") && adminService.isPermitted("menu_tree");
%>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="home" />
	<tiles:putAttribute name="title" value="alb_home" />
	<tiles:putAttribute name="body" type="string">
<div id="home-menus">
	<p id="home-menus-title" class="proxyTitle">管理メニュー</p>
	<div class="home-menus-column">
		<!-- 各メニュー -->
<!-- 		TODO 権限管理 -->
<%-- 		<%if(adminService.isPermitted("menu") || adminService.isPermitted("menu_tree")){%> --%>
		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<a href="../menu/index" class="home-menu-icon-link"><img src="../../skin/imgs/home_icons/kmenuedit.gif"></a>
			</div>
			<div class="home-menu-content">
				<a href="../menu/index" class="home-menu-header">メニュー</a>
				<p class="home-menu-definition">トップメニューとサイドメニューをエクスプローラ風の操作で設定します。</p>
			</div>
		</div>
<%-- 		<%}%> --%>
<%-- 		<%if(adminService.isPermitted("search")){%> --%>
		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<a href="../search/index"><img src="../../skin/imgs/home_icons/search.gif"></a>
			</div>
			<div class="home-menu-content">
				<a href="../search/index" class="home-menu-header">検索フォーム</a>
				<p class="home-menu-definition">検索フォームから検索する際の検索サイトの登録、設定します。</p>
			</div>
		</div>
<%-- 		<%}%> --%>
<%-- 		<%if( adminService.isPermitted("widget") ){%> --%>
		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<a href="../gadget/index"><img src="../../skin/imgs/home_icons/kpersonalizer.gif"></a>
			</div>
			<div class="home-menu-content">
				<a href="../gadget/index" class="home-menu-header">ガジェット</a>
				<p class="home-menu-definition">組み込みガジェットの管理、新規ガジェットの追加・編集・削除を行います。</p>
			</div>
		</div>
<%-- 		<%}%> --%>
<%-- 		<%if( adminService.isPermitted("defaultPanel") || adminService.isPermitted("portalLayout")){%> --%>
		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<a href="../defaultpanel/index"><img src="../../skin/imgs/home_icons/mycomputer.gif"></a>
			</div>
			<div class="home-menu-content">
<!-- 			TODO ロールによってリンク先のindexを変える -->
				<a href="../defaultpanel/index" class="home-menu-header">初期画面</a>
				<p class="home-menu-definition">画面の共通設定とユーザが初めてログインした際に表示する画面構成を設定します。</p>
			</div>
		</div>
<%-- 		<%}%> --%>
<%-- 		<%if( adminService.isPermitted("i18n") ){%> --%>
		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<a href="../i18n/index"><img src="../../skin/imgs/home_icons/package_network.gif"></a>
			</div>
			<div class="home-menu-content">
				<a href="../i18n/index" class="home-menu-header">国際化</a>
				<p class="home-menu-definition">infoScoopに表示されるコンポーネントを国際化します。</p>
			</div>
		</div>
<%-- 		<%}%> --%>
	</div>
	<div class="home-menus-column">
<%-- 		<%if( adminService.isPermitted("properties") ){%> --%>
		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<a href="../properties/index"><img src="../../skin/imgs/home_icons/advancedsettings.gif"></a>
			</div>
			<div class="home-menu-content">
				<a href="../properties/index" class="home-menu-header">プロパティ</a>
				<p class="home-menu-definition">各種システムプロパティの説明と各プロパティを設定します。</p>
			</div>
		</div>
<%-- 		<%}%> --%>
<%-- 		<%if( adminService.isPermitted("proxy") ){%> --%>
		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<a href="../proxy/index"><img src="../../skin/imgs/home_icons/network.gif"></a>
			</div>
			<div class="home-menu-content">
				<a href="../proxy/index" class="home-menu-header">プロキシ</a>
				<p class="home-menu-definition">Ajaxプロキシを設定します。</p>
			</div>
		</div>
<%-- 		<%}%> --%>
<%-- 		<%if( adminService.isPermitted("admins") ){%> --%>
		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<a href="../administrator/index"><img src="../../skin/imgs/home_icons/kdmconfig.gif"></a>
			</div>
			<div class="home-menu-content">
				<a href="../administrator/index" class="home-menu-header">管理者</a>
				<p class="home-menu-definition">管理画面にアクセスできるユーザと管理ロールを設定します。</p>
			</div>
		</div>
<%-- 		<%}%> --%>
<%-- 		<%if( adminService.isPermitted("forbiddenURL") ){%> --%>
		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<a href="../forbiddenurl/index"><img src="../../skin/imgs/home_icons/cnrdelete-all.gif"></a>
			</div>
			<div class="home-menu-content">
				<a href="../forbiddenurl/index" class="home-menu-header">禁止URL</a>
				<p class="home-menu-definition">ミニブラウザウィジェットなどポータル内に表示されるインナーフレームへの表示を制限するWebサイトを登録します。</p>
			</div>
		</div>
<%-- 		<%}%> --%>
<%-- 		<%if( adminService.isPermitted("authentication")){%> --%>
		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<a href="../authentication/index"><img src="../../skin/imgs/home_icons/unlock.gif"></a>
			</div>
			<div class="home-menu-content">
				<a href="../authentication/index" class="home-menu-header">OAuth</a>
				<p class="home-menu-definition">OAuth及びOAuth関連仕様についての設定します。</p>
			</div>
		</div>
<%-- 		<%}%> --%>
	</div>
	<p id="home-menus-title" class="proxyTitle" class="home-menu-header">infoScoop 情報</p>
	<div class="home-menus-column">
		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<img src="../../skin/imgs/home_icons/goto.gif">
			</div>
			<div id="information"></div>
		</div>
	</div>
	<div class="home-menus-column">
		<div id="" class="home-menu-box">
			<div class="home-menu-icon">
				<img src="../../skin/imgs/home_icons/info.gif">
			</div>
			<div id="version"></div>
		</div>
	</div>
</div>
		<script>
			/*
			$jq(function() {
				ISA_PortalAdmins.information = new ISA_Information();
				ISA_PortalAdmins.information.build();
			});
			*/
		</script>
	</tiles:putAttribute>
</tiles:insertDefinition>