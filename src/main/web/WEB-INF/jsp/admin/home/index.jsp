<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="home"/>
	<tiles:putAttribute name="title" value="home.title"/>
	<tiles:putAttribute name="body" type="string">
<div id="home">
	<div id="top">
		<h1>infoScoop for Google AppsでGoogle Appsをもっと快適に</h1>
		<p>infoScoop for Google Appsの管理画面では、Google Appsの管理者がポータル画面のカスタマイズを行うことができます。<br />
		ここではGoogle Appsのコンテンツをより快適に、より柔軟に利用するための4つの機能をご用意しています。</p>
	</div>
	<div id="middle">
		<div id="left">
			<div>
				<h3><a href="../menu/index">メニュー管理</a></h3>
				<div>この画面ではメニューツリーを追加/編集/削除できます。<br />
				メニューツリーにガジェットを追加すると、ポータル画面左のメニュータブよりガジェットをドロップできます。</div>
			</div>
			<div>
				<h3><a href="../tab/index">タブ管理</a></h3>
				<div>この画面ではユーザに初期表示するタブを管理することができます。<br />
				ユーザのポータル画面ではこの画面で設定したタブのうちそのユーザで参照可能なタブが表示されます。
				</div>
			</div>
		</div>
		<div id="right">
			<div>
				<h3><a href="../role/index">役割グループ</a></h3>
				<div>メニューやタブの公開範囲を限定する場合にこの画面で作成した役割グループを割り当てます。<br />
				役割グループにはGoogle Appsのユーザ/グループを追加することができます。</div>
			</div>
			<div>
				<h3><a href="../user/index">マスター同期</a></h3>
				<div>Google Appsのユーザとグループ(メーリングリスト)をinfoScoopに取り込みます。<br />
				この処理は、ユーザ数およびグループ数に応じて数分から数十分の時間がかかります。</div>
			</div>
		</div>
	</div>
	<br />
	<div id="bottom">
		<h2><a href="http://tiny.cc/infoscoop4g-faq#h.r9g1b7hf5ig1" target="_blank">Google Apps用ガジェットの使い方</a></h2>
		<div>infoScoopのポータル画面で初期配置されているGoogle Apps用ガジェットのほとんどは、Google Apps側で設定を行うことなくそのままお使いいただけます。<br />
		ただし、お知らせガジェットとリンク集ガジェットは使用前に別途設定が必要です。<br />
		設定方法についてはこちらをご参照ください。</div>

		<h2><a href="http://tiny.cc/infoscoop4g-faq" target="_blank">FAQ</a></h2>
		<div>infoScoop for Google Apps管理画面について、よくある質問をFAQにまとめました。<br />
		こちらをご参照ください。</div>
	</div>
<div>
	</tiles:putAttribute>
</tiles:insertDefinition>