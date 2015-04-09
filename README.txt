infoScoop OpenSource 4.0.0
========================

About infoScoop OpenSource
--------------------------
"infoScoop OpenSource" is information portal that evolve according with
personal work style. This portal provides important information for individuals
from business system and huge information in or out of the company. It implements
free arrangement of information and fits to individual information processing
skill and work style.

For more information, please see the infoScoop OpenSource web site at
http://www.infoscoop.org/.


How to Install
--------------
Refer to the URL below.
https://github.com/infoScoop/infoscoop-documents/blob/master/en/index.md


How to migrate from version 3.4.0 or earlier
--------------------------------------------------
To migrate from version 3.4.0 or earlier to 4.0.0, follow the steps below.

	1. Redeploy infoscoop.war to WebApplication Server.

        2. Please refer to the following URL and update gadgets. 
           https://github.com/infoScoop/infoscoop-documents/blob/master/en/administration-guide/gadget-settings.md#522-updating-a-gadget

           The following is a ZIP file of a gadget. 
	************************************
        Alarm
        \tools\initdb\gadget_files\alarm.zip
        Blogparts
        \tools\initdb\gadget_files\blogparts.zip
        Calculator
        \tools\initdb\gadget_files\calc.zip
        Sticky
        \tools\initdb\gadget_files\sticky.zip
        TODO List
        \tools\initdb\gadget_files\todoList.zip
        World clock
        \tools\initdb\gadget_files\worldclock.zip
	************************************


* When employment server is Linux and a repository database is MySQL,
  a shift procedure is changed as follows. 

	1. Change into a small letter all the names of the table stored in the repository database.
	   The example of SQL is shown below.
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

	2. Add the following setup to the configuration file "my.cnf" of MySQL.
	************************************
	[mysqld]
	lower_case_table_names = 1
	************************************

	3. Redeploy infoscoop.war to WebApplication Server.

        4. Please refer to the following URL and update gadgets. 
           https://github.com/infoScoop/infoscoop-documents/blob/master/en/administration-guide/gadget-settings.md#522-updating-a-gadget

           The following is a ZIP file of a gadget. 
	************************************
        Alarm
        \tools\initdb\gadget_files\alarm.zip
        Blogparts
        \tools\initdb\gadget_files\blogparts.zip
        Calculator
        \tools\initdb\gadget_files\calc.zip
        Sticky
        \tools\initdb\gadget_files\sticky.zip
        TODO List
        \tools\initdb\gadget_files\todoList.zip
        World clock
        \tools\initdb\gadget_files\worldclock.zip
	************************************


License and Copyright
---------------------

This code is licensed under the **GNU Lesser General Public License (LGPL) v3**.
Please see LICENSE.txt for licensing and copyright information.


Changes from Version 3.4.0 to 4.0.0
-----------------------------------
Refer to the URL below.
https://github.com/infoScoop/infoscoop/issues?q=milestone%3AMilestone-4.0.0+is%3Aclosed