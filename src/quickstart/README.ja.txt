infoScoop OpenSource 2.1.0
==========================

infoScoop OpenSourceとは
------------------------
「infoScoop」は個人のワークスタイルに合わせて進化する情報ポータルです。業務シス
テムや社内外の膨大な情報の中から個人にとって重要な情報の提供や、自由な配置と整理
を実現し、個人の情報処理スキルやワークスタイルに合わせた 「使いたくなる」 ポータ
ルを実現します。

詳細な説明は、以下のinfoScoop OpenSource公式サイトを参照ください。http://www.infoscoop.org/

セットアップ方法
----------------
infoscoop-2.1.0-quickstart.zipを解凍し以下の手順を実行してください。

1. リポジトリデータベースの作成

mysqlのコンソールを呼び出し、infoScoop OpenSourceのリポジトリ用のデータベースを作成
します。

$mysql -uroot
mysql>create database iscoop character set utf8;
mysql>exit

2. リポジトリデータベースへ初期データを投入

以下のコマンドを実行し、作成したデータベースに初期データを投入します。

$ mysql -uroot iscoop < infoscoop-2.1.0-quickstart/init_infoscoop.sql

3. データベース接続設定

クイックスタートはデフォルト設定では、MySQLが同じサーバー、デフォルトポート3306、
データベースへの接続ユーザIDが"root"、パスワードは無しになっています。
これ以外の場合は、データベース設定を変更する必要があります。
設定を変更する場合は、以下のファイルを編集します。

apache-tomcat-6.0.28/conf/Catalina/localhost/infoscoop.xml

以下の属性を適切な値に変更してください。

・username: データベース接続ユーザID
・password: データベース接続パスワード
・url: データベース接続URL、別サーバーのMySQLに接続する場合はlocalhostを適切なホ
スト名に、デフォルトポートを使用していない場合は3306を適切な値に変更します。

4. infoScoop OpenSourceサーバーの起動

以下のコマンドを実行してください。

$ startup.bat(sh)

以上で、infoScoop OpenSourceクイックスタートのセットアップは終了です。

停止は以下のコマンドを実行します。

$ shutdown.bat(sh)

5. infoScoop OpenSourceの起動

ブラウザを起動し以下のアドレスを表示してください。

http://<ホスト名>:8080/infoscoop/

ログイン画面が表示されます。初期状態ではユーザはadmin/adminのみです。

ユーザの追加は管理画面から行います。
管理画面は以下のアドレスを表示し、ユーザを追加してください。

http://<ホスト名>:8080/infoscoop/admin


ライセンス・著作権
------------------

本ソフトウェアは、**GNU Lesser General Public License (LGPL) v3**. に定められた
ライセンスに基づいて公開します。
ライセンスおよびコピーライト情報は LICENSE.txt を参照ください。

2.0.1から2.1.0での変更点
------------------------
以下のURLを参照してください。
http://code.google.com/p/infoscoop/issues/list?can=1&q=label%3AMilestone-2.1.0+label%3ADefect+OR+label%3AMilestone-2.1.0++label%3AEnhancement