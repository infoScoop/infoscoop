infoScoop OpenSource 2.1.0
==========================

infoScoop OpenSourceとは
------------------------
「infoScoop」は個人のワークスタイルに合わせて進化する情報ポータルです。業務シス
テムや社内外の膨大な情報の中から個人にとって重要な情報の提供や、自由な配置と整理
を実現し、個人の情報処理スキルやワークスタイルに合わせた 「使いたくなる」 ポータ
ルを実現します。

詳細な説明は、以下のinfoScoop OpenSource公式サイトを参照ください。http://www.infoscoop.org/

バージョン2.0からの移行手順
-----------------------------
2.0から本バージョンに移行するには以下の手順を実行します。

[データの移行]
以下の作業は、データベースのバックアップを取得してから実行することを強くお勧めします。

データ移行ツールを実行すると、以下のデータが初期データで上書きされます。
・その他画面のヘッダー
・その他画面のCSS
・アラーム、ブログパーツ、電卓、付箋紙、TODOリストガジェットの設定
これらのデータに関しては手動で移行する必要があります。
移行前に管理画面で参照の上、コピーして保存しておいてください。
移行後に再び管理画面で適切に編集し直してください。

1. migration.propertiesを編集して、データベース接続設定をします。

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

2. 利用しているDMBSのJDBCドライバーをlibディレクトリにコピーします。
(MySQLのドライバーは予め含まれて居るのでこの手順は省略してください。)

3. 移行ツールの実行

$ migration.bat(sh)を実行します。

4. バックアップテーブルの削除

移行ツールを実行すると、"_bak20"という接尾辞が付いたバックアップテーブルが作成されます。
移行の確認が終了したら以下のコマンドを実行してバックアップテーブルを削除してください。

$ cleanup_temp_table.bat(sh)

[アプリケーションの更新]
1. WARファイルの置き換え。Webアプリケーションの更新方法については各Webアプリケー
  ションサーバーのマニュアルに従ってください。
2. 静的コンテンツを設定している場合は、静的コンテンツを入れ替えます。
  静的コンテンツを配置しているディレクトリをinfoscoop/staticContentで入れ替えてください。

インストール方法
----------------
以下のURLを参照してください。
http://www.infoscoop.org/index.php/manual/quick-start.html

ライセンス・著作権
------------------

本ソフトウェアは、**GNU Lesser General Public License (LGPL) v3**. に定められた
ライセンスに基づいて公開します。
ライセンスおよびコピーライト情報は LICENSE.txt を参照ください。

2.0.1から2.1.0での変更点
------------------------
以下のURLを参照してください。
http://code.google.com/p/infoscoop/issues/list?can=1&q=label%3AMilestone-2.1.0+label%3ADefect+OR+label%3AMilestone-2.1.0++label%3AEnhancement