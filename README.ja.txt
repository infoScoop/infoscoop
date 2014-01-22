infoScoop OpenSource 3.3.0-beta
==========================

infoScoop OpenSourceとは
------------------------
「infoScoop」は個人のワークスタイルに合わせて進化する情報ポータルです。業務シス
テムや社内外の膨大な情報の中から個人にとって重要な情報の提供や、自由な配置と整理
を実現し、個人の情報処理スキルやワークスタイルに合わせた 「使いたくなる」 ポータ
ルを実現します。

詳細な説明は、以下のinfoScoop OpenSource公式サイトをご参照ください。
http://www.infoscoop.org/


インストール方法
----------------
以下のURLを参照してください。
http://www.infoscoop.org/index.php/ja/manual/installation-guide.html


バージョン3.1.1からの移行手順
--------------------------------------------------
3.1.1から本バージョンに移行するには以下の手順を実行します。

1. Webアプリケーションサーバーにinfoscoop.warを再デプロイしてください。

* 運用サーバーがLinuxかつリポジトリデータベースがMySQLの場合、
  移行手順が以下のように変更されます。

1. リポジトリデータベースに格納されているテーブルの名前を全て小文字に変更してください。

2. MySQLの設定ファイル「my.cnf」に以下の設定を追加してください。
***************
[mysqld]
lower_case_table_names = 1
***************

3. Webアプリケーションサーバーにinfoscoop.warを再デプロイしてください。


ライセンス・著作権
------------------

本ソフトウェアは、**GNU Lesser General Public License (LGPL) v3**. に定められた
ライセンスに基づいて公開します。
ライセンスおよびコピーライト情報は LICENSE.txt を参照ください。


3.1.1から3.3.0-betaでの変更点
------------------------
以下のURLを参照してください。
https://github.com/infoScoop/infoscoop/issues?milestone=24&state=closed