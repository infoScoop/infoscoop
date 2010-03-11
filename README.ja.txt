infoScoop OpenSource 2.0.1
==========================

infoScoop OpenSourceとは
------------------------
「infoScoop」は個人のワークスタイルに合わせて進化する情報ポータルです。業務システムや社内外の膨大な情報の中から個人にとって重要な情報の提供や、自由な配置と整理を実現し、個人の情報処理スキルやワークスタイルに合わせた 「使いたくなる」 ポータルを実現します。

For more information, please see the infoScoop OpenSource website at http://www.infoscoop.org/.


インストール方法
----------------
以下のURLを参照してください。
http://www.infoscoop.org/index.php/manual/quick-start.html


2.0.1へのバージョンアップ方法
-----------------------------
1. クイックスタートを参考にWebアプリケーションサーバーにinfoscoop.warを再デプロイしてください。

2. データベース内のガジェットを全て置き換えます。

  (1). SQL実行ツールを開きます。
  (2). 以下のSQLコマンドを実行します。
     > delete from IS_GADGETS where type in ('calc','blogparts','todoList','alarm','sticky','worldclock')
     > delete from IS_GADGET_ICONS where type in ('calc','blogparts','todoList','alarm','sticky','worldclock')
  (3). コマンドプロンプトを開き、tools/initdbディレクトリに移動します。
  (4). 適切なJDBCドライバーをlibディレクトリにコピーします。
  (5). 以下のコマンドを実行します。
     >import.sh(bat) GADGET,GADGETICON

※上記手順を実行すると、以下のガジェットの設定が初期化されます。
  * calc
  * todoList
  * alarm
  * blogparts
  * sticky
  * worldclock


ライセンス・著作権
------------------

This code is licensed under the **GNU Lesser General Public License (LGPL) v3**. Please see
LICENSE.txt for licensing and copyright information.


2.0.0から2.0.1での変更点
------------------------
以下のURLを参照してください。
http://code.google.com/p/infoscoop/issues/list?can=1&q=label%3DMilestone-2.0.1