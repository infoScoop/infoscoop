infoScoop OpenSource 2.2.1
==========================

infoScoop OpenSourceとは
------------------------
「infoScoop」は個人のワークスタイルに合わせて進化する情報ポータルです。業務シス
テムや社内外の膨大な情報の中から個人にとって重要な情報の提供や、自由な配置と整理
を実現し、個人の情報処理スキルやワークスタイルに合わせた 「使いたくなる」 ポータ
ルを実現します。

詳細な説明は、以下のinfoScoop OpenSource公式サイトを参照ください。
http://www.infoscoop.org/

バージョン2.1.0からの移行手順
-----------------------------
2.1.0から本バージョンに移行するには以下の手順を実行します。

1. データベース内のガジェットを置き換え、国際化リソースを追加します。

  (1). SQL実行ツールを開きます。
  (2). 以下のSQLコマンドを実行します。
     > delete from IS_GADGETS where type in ('calc','blogparts','todoList','alarm','sticky','worldclock')
     > delete from IS_GADGET_ICONS where type in ('calc','blogparts','todoList','alarm','sticky','worldclock')
  (3). コマンドプロンプトを開き、tools/initdbディレクトリに移動します。
  (4). 適切なJDBCドライバーをlibディレクトリにコピーします。
  (5). 以下のコマンドを実行します。
     >import.sh(bat) GADGET GADGETICON I18N

  ※上記手順を実行すると、以下のガジェットの設定が初期化されます。
    * calc
    * todoList
    * alarm
    * blogparts
    * sticky
    * worldclock

2. 静的コンテンツを設定している場合は、静的コンテンツを入れ替えます。
  静的コンテンツを配置しているディレクトリをinfoscoop/staticContentで入れ替えてください。

3. Webアプリケーションサーバーにinfoscoop.warを再デプロイしてください。

バージョン2.1.1からの移行手順
-----------------------------
2.1.1およびそれ以降のパッチバージョン(バージョン番号の3桁目)から本バージョンに
移行するには以下の手順を実行します。

1. データベースに本バージョンで追加された機能の国際化リソースを追加します。

  (1). コマンドプロンプトを開き、tools/initdbディレクトリに移動します。
  (2). 適切なJDBCドライバーをlibディレクトリにコピーします。
  (3). 以下のコマンドを実行します。
     >import.sh(bat) I18N

2. 静的コンテンツを設定している場合は、静的コンテンツを入れ替えます。
  静的コンテンツを配置しているディレクトリをinfoscoop/staticContentで入れ替えてください。

3. Webアプリケーションサーバーにinfoscoop.warを再デプロイしてください。


バージョン2.2.0からの移行手順
-----------------------------
2.2.0から本バージョンに移行するには以下の手順を実行します。

1. 静的コンテンツを設定している場合は、静的コンテンツを入れ替えます。
   静的コンテンツを配置しているディレクトリをinfoscoop/staticContentで入れ替えてください。

2. Webアプリケーションサーバーにinfoscoop.warを再デプロイしてください。


インストール方法
----------------
以下のURLを参照してください。
http://www.infoscoop.org/index.php/manual/quick-start.html


ライセンス・著作権
------------------

本ソフトウェアは、**GNU Lesser General Public License (LGPL) v3**. に定められた
ライセンスに基づいて公開します。
ライセンスおよびコピーライト情報は LICENSE.txt を参照ください。


2.1.1から2.2での変更点
------------------------
以下のURLを参照してください。
https://code.google.com/p/infoscoop/issues/list?can=1&q=milestone=2.2.0

2.2から2.2.1での変更点
------------------------
以下のURLを参照してください。
https://code.google.com/p/infoscoop/issues/list?can=1&q=milestone=2.2.1