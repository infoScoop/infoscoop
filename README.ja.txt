infoScoop OpenSource 3.1.0
==========================

infoScoop OpenSourceとは
------------------------
「infoScoop」は個人のワークスタイルに合わせて進化する情報ポータルです。業務シス
テムや社内外の膨大な情報の中から個人にとって重要な情報の提供や、自由な配置と整理
を実現し、個人の情報処理スキルやワークスタイルに合わせた 「使いたくなる」 ポータ
ルを実現します。

詳細な説明は、以下のinfoScoop OpenSource公式サイトをご参照ください。
http://www.infoscoop.org/

バージョン3.0.0からの移行手順
--------------------------------------------------
3.0.0から本バージョンに移行するには以下の手順を実行します。

1. staticContentUrlプロパティを設定している場合は静的コンテンツを入れ替えます。
  静的コンテンツを配置しているディレクトリ以下をinfoscoop/staticContent以下のコ
  ンテンツに置換します。

2. データベースの内容を更新します。
  (1). コマンドプロンプトを開き、tools/initdbディレクトリに移動します。
  (2). 適切なJDBCドライバーをlibディレクトリにコピーします。
  (3). 以下のコマンドを実行します。
     >import.sh(bat) I18N

3. tools/migration/migration.propertiesを編集して、データベース接続設定をします。

  DBMS=mysql
  DATABASE_URL=jdbc:mysql://localhost:3306/iscoop
  #SCHEMA=iscoop
  USER=root
  PASSWORD=
  #TABLESPACE=

  1)DBMS: mysql、oracle、db2のいずれかを指定します。
  2)DATABASE_URL: JDBC接続するURLを指定します。
  3)SCHEMA: 省略した場合は、ユーザ名と同じスキーマに適用されます。MySQLでは指定しないでください。
  4)USER: 接続ユーザを指定します。
  5)PASSWORD: 接続パスワードを指定します。
  6)TABLESPACE: DB2専用のオプションです。テーブルスペースを指定します。

4. 利用しているDMBSのJDBCドライバーをlibディレクトリにコピーします。
  (MySQLのドライバーは予め含まれて居るのでこの手順は省略してください。)

5. 移行ツールの実行

  $ migration.bat(sh)を実行します。

6. Webアプリケーションサーバーにinfoscoop.warを再デプロイしてください。

7. 本バージョンから検索フォーム、ユーザID(ユーザ名)表示はコマンドバーに移動しています。カスタムヘッダ領域から
   該当箇所を削除することを推奨いたします。
   管理画面->レイアウト->画面その他->header から、以下の箇所を削除してください。

   <td>
	<form name="searchForm" onsubmit="javascript:IS_Portal.SearchEngines.buildSearchTabs(document.getElementById('searchTextForm').value);return false;">
	<div style="float:right;margin-right:5px">
		<table>
			<tbody>
				<tr>
					<td colspan="2" align="right" style="font-size:80%;">
						<#if session.getAttribute("Uid")??>%{lb_welcome}${session.getAttribute("loginUserName")}%{lb_san}
						<#else><a href="login.jsp">%{lb_login}</a>
						</#if>
					</td>
				</tr>
				<tr>
					<td>
						<input id="searchTextForm" type="text" style="width:200px;height:23px;float:left;"/>
						<input type="submit" value="%{lb_search}" style="padding:0 0.4em;"/>
						<span id="editsearchoption">%{lb_searchOption}</span>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	</form>
   </td>

   上記はデフォルトの場合となりますので、カスタマイズされている場合は該当部分を削除してください。

インストール方法
----------------
以下のURLを参照してください。
http://www.infoscoop.org/index.php/ja/manual/installation-guide.html

ライセンス・著作権
------------------

本ソフトウェアは、**GNU Lesser General Public License (LGPL) v3**. に定められた
ライセンスに基づいて公開します。
ライセンスおよびコピーライト情報は LICENSE.txt を参照ください。


3.0.0から3.1.0での変更点
------------------------
以下のURLを参照してください。
https://code.google.com/p/infoscoop/issues/list?can=1&q=milestone=3.1.0