infoScoop OpenSource 4.0.0
==========================

infoScoop OpenSourceとは
------------------------
「infoScoop」は個人のワークスタイルに合わせて進化する情報ポータルです。業務シス
テムや社内外の膨大な情報の中から個人にとって重要な情報の提供や、自由な配置と整理
を実現し、個人の情報処理スキルやワークスタイルに合わせた「使いたくなる」ポータル
を実現します。

詳細な説明は、以下のinfoScoop OpenSource公式サイトをご参照ください。
http://www.infoscoop.org/


インストール方法
----------------
以下のURLを参照してください。
https://github.com/infoScoop/infoscoop-documents/blob/master/ja/index.md


バージョン3.4.0以前からの移行手順
--------------------------------------------------
3.4.0以前から本バージョンに移行するには以下の手順を実行します。

	1. Webアプリケーションサーバーにinfoscoop.warを再デプロイしてください。

        2. ガジェットの更新を行ってください。更新方法については以下のURLを参照してください。
           https://github.com/infoScoop/infoscoop-documents/blob/master/ja/administration-guide/gadget-settings.md#522-%E3%82%AC%E3%82%B8%E3%82%A7%E3%83%83%E3%83%88%E3%81%AE%E6%9B%B4%E6%96%B0

           最新のガジェットは、\tools\initdb\gadget_files 配下にzipファイルとして配置しています。
           ガジェットファイル（ZIP形式）は以下になります。
	************************************
        アラーム
        \tools\initdb\gadget_files\alarm.zip
        ブログパーツ
        \tools\initdb\gadget_files\blogparts.zip
        電卓
        \tools\initdb\gadget_files\calc.zip
        付箋紙
        \tools\initdb\gadget_files\sticky.zip
        TODOリスト
        \tools\initdb\gadget_files\todoList.zip
        標準時時計
        \tools\initdb\gadget_files\worldclock.zip
	************************************

* 運用サーバーがLinuxかつリポジトリデータベースがMySQLの場合、
  移行手順が以下のように変更されます。

	1. リポジトリデータベースに格納されているテーブルの名前を全て小文字に変更してください。
	   以下にSQL例を示します。
	************************************
	RENAME TABLE IS_ACCESSLOGS TO is_accesslogs;
	RENAME TABLE IS_ACCOUNTS TO is_accounts;
	RENAME TABLE IS_ADMINROLES TO is_adminroles;
	RENAME TABLE IS_AUTHCREDENTIALS TO is_authcredentials;
	RENAME TABLE IS_CACHES TO is_caches;
	RENAME TABLE IS_FORBIDDENURLS TO is_forbiddenurls;
	RENAME TABLE IS_GADGETS TO is_gadgets;
	RENAME TABLE IS_GADGET_ICONS TO is_gadget_icons;
	RENAME TABLE IS_HOLIDAYS TO is_holidays;
	RENAME TABLE IS_I18N TO is_i18n;
	RENAME TABLE IS_I18NLASTMODIFIED TO is_i18nlastmodified;
	RENAME TABLE IS_I18NLOCALES TO is_i18nlocales;
	RENAME TABLE IS_KEYWORDS TO is_keywords;
	RENAME TABLE IS_LOGS TO is_logs;
	RENAME TABLE IS_MENUCACHES TO is_menucaches;
	RENAME TABLE IS_MENUS TO is_menus;
	RENAME TABLE IS_MENUS_TEMP TO is_menus_temp;
	RENAME TABLE IS_MESSAGES TO is_messages;
	RENAME TABLE IS_OAUTH2_TOKENS TO is_oauth2_tokens;
	RENAME TABLE IS_OAUTH_CERTIFICATE TO is_oauth_certificate;
	RENAME TABLE IS_OAUTH_CONSUMERS TO is_oauth_consumers;
	RENAME TABLE IS_OAUTH_GADGET_URLS TO is_oauth_gadget_urls;
	RENAME TABLE IS_OAUTH_TOKENS TO is_oauth_tokens;
	RENAME TABLE IS_PORTALADMINS TO is_portaladmins;
	RENAME TABLE IS_PORTALLAYOUTS TO is_portallayouts;
	RENAME TABLE IS_PREFERENCES TO is_preferences;
	RENAME TABLE IS_PROPERTIES TO is_properties;
	RENAME TABLE IS_PROXYCONFS TO is_proxyconfs;
	RENAME TABLE IS_RSSCACHES TO is_rsscaches;
	RENAME TABLE IS_SEARCHENGINES TO is_searchengines;
	RENAME TABLE IS_SESSIONS TO is_sessions;
	RENAME TABLE IS_SYSTEMMESSAGES TO is_systemmessages;
	RENAME TABLE IS_TABLAYOUTS TO is_tablayouts;
	RENAME TABLE IS_TABS TO is_tabs;
	RENAME TABLE IS_TAB_ADMINS TO is_tab_admins;
	RENAME TABLE IS_USERPREFS TO is_userprefs;
	RENAME TABLE IS_WIDGETCONFS TO is_widgetconfs;
	RENAME TABLE IS_WIDGETS TO is_widgets;
	************************************

	2. MySQLの設定ファイル「my.cnf」に以下の設定を追加してください。
           変更後はMySQLの再起動が必要です。
	************************************
	[mysql]
	default-character-set = utf8

	[mysqld]
	default-character-set = utf8
	lower_case_table_names = 1
	************************************

	3. Webアプリケーションサーバーにinfoscoop.warを再デプロイしてください。

        4. ガジェットの更新を行ってください。更新方法については以下のURLを参照してください。
           https://github.com/infoScoop/infoscoop-documents/blob/master/ja/administration-guide/gadget-settings.md#522-%E3%82%AC%E3%82%B8%E3%82%A7%E3%83%83%E3%83%88%E3%81%AE%E6%9B%B4%E6%96%B0

           最新のガジェットは、\tools\initdb\gadget_files 配下にzipファイルとして配置しています。
           ガジェットファイル（ZIP形式）は以下になります。
	************************************
        アラーム
        \tools\initdb\gadget_files\alarm.zip
        ブログパーツ
        \tools\initdb\gadget_files\blogparts.zip
        電卓
        \tools\initdb\gadget_files\calc.zip
        付箋紙
        \tools\initdb\gadget_files\sticky.zip
        TODOリスト
        \tools\initdb\gadget_files\todoList.zip
        標準時時計
        \tools\initdb\gadget_files\worldclock.zip
	************************************


ライセンス・著作権
------------------

本ソフトウェアは、**GNU Lesser General Public License (LGPL) v3**. に定められた
ライセンスに基づいて公開します。
ライセンスおよびコピーライト情報は LICENSE.txt を参照ください。


3.4.0から4.0.0での変更点
------------------------
以下のURLを参照してください。
https://github.com/infoScoop/infoscoop/issues?q=milestone%3AMilestone-4.0.0+is%3Aclosed