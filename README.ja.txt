infoScoop OpenSource 3.0.0
==========================

infoScoop OpenSourceとは
------------------------
「infoScoop」は個人のワークスタイルに合わせて進化する情報ポータルです。業務シス
テムや社内外の膨大な情報の中から個人にとって重要な情報の提供や、自由な配置と整理
を実現し、個人の情報処理スキルやワークスタイルに合わせた 「使いたくなる」 ポータ
ルを実現します。

詳細な説明は、以下のinfoScoop OpenSource公式サイトをご参照ください。
http://www.infoscoop.org/

バージョン2.2.0または2.2.1からの移行手順
--------------------------------------------------
2.2.0または2.2.1から本バージョンに移行するには以下の手順を実行します。

1. staticContentUrlプロパティを設定している場合は静的コンテンツを入れ替えます。
  静的コンテンツを配置しているディレクトリ以下をinfoscoop/staticContent以下のコ
  ンテンツに置換します。

2. データベースの内容を更新します。
  (1)tools/initdb/data/widgetconfigディレクトリのimport.csvを11行削除し、下記の
     ように1行だけになるよう編集します。

       "Message",<LOB FILE='Message.xml' />

  (2). コマンドプロンプトを開き、tools/initdbディレクトリに移動します。
  (3). 適切なJDBCドライバーをlibディレクトリにコピーします。
  (4). 以下のコマンドを実行します。
     >import.sh(bat) GADGET GADGETICON I18N WIDGETCONF

  ※上記手順を実行すると、以下のガジェットの設定が初期化されます。
    * calc
    * todoList
    * alarm
    * blogparts
    * sticky
    * worldclock
    * message

  (5). MySQLをご利用の場合、以下のSQLコマンドを実行してください。
     mysql>ALTER TABLE is_widgets MODIFY COLUMN `UID` VARCHAR(150) NOT NULL;

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

6. バックアップテーブルの削除

  移行ツールを実行すると、"_bak223"という接尾辞が付いたバックアップテーブルが作成されます。
  移行の確認が終了したら以下のコマンドを実行してバックアップテーブルを削除してください。

  $ cleanup_temp_table.bat(sh)

7. Webアプリケーションサーバーにinfoscoop.warを再デプロイしてください。

バージョン2.2.2または2.2.3からの移行手順
--------------------------------------------------
2.2.2または2.2.3から本バージョンに移行するには以下の手順を実行します。

1. staticContentUrlプロパティを設定している場合は静的コンテンツを入れ替えます。
  静的コンテンツを配置しているディレクトリ以下をinfoscoop/staticContent以下のコ
  ンテンツに置換します。

2. データベースの内容を更新します。
  (1). コマンドプロンプトを開き、tools/initdbディレクトリに移動します。
  (2). 適切なJDBCドライバーをlibディレクトリにコピーします。
  (3). 以下のコマンドを実行します。
     >import.sh(bat) GADGET I18N

  ※上記手順を実行すると、以下のガジェットの設定が初期化されます。
    * calc
    * todoList
    * alarm
    * blogparts
    * sticky
    * worldclock

  (4). MySQLをご利用の場合、以下のSQLコマンドを実行してください。
     mysql>ALTER TABLE is_widgets MODIFY COLUMN `UID` VARCHAR(150) NOT NULL;

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

6. バックアップテーブルの削除

  移行ツールを実行すると、"_bak223"という接尾辞が付いたバックアップテーブルが作成されます。
  移行の確認が終了したら以下のコマンドを実行してバックアップテーブルを削除してください。

  $ cleanup_temp_table.bat(sh)

7. Webアプリケーションサーバーにinfoscoop.warを再デプロイしてください。

インストール方法
----------------
以下のURLを参照してください。
http://www.infoscoop.org/index.php/ja/manual/installation-guide.html

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

2.2.1から2.2.2での変更点
------------------------
以下のURLを参照してください。
https://code.google.com/p/infoscoop/issues/list?can=1&q=milestone=2.2.2

2.2.2から2.2.3での変更点
------------------------
以下のURLを参照してください。
https://code.google.com/p/infoscoop/issues/list?can=1&q=milestone=2.2.3

2.2.3から3.0.0での変更点
------------------------
以下のURLを参照してください。
https://code.google.com/p/infoscoop/issues/list?can=1&q=milestone=3.0.0