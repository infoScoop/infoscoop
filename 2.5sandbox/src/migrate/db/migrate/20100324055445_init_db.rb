class InitDb < ActiveRecord::Migration
  def self.up
    options = 'ENGINE=InnoDB DEFAULT CHARSET=utf8'
    
    create_table "is_accesslogs", :force => true, :options => options do |t|
      t.string "UID",  :limit => 150, :null => false
      t.string "DATE", :limit => 8,   :null => false
    end

    add_index "is_accesslogs", ["UID", "DATE"], :name => "is_accesslogs_uq", :unique => true

    create_table "is_accounts", :force => true, :options => options do |t|
      t.string "uid",      :limit => 150, :null => false
      t.string "name"
      t.string "password"
    end

    add_index "is_accounts", ["uid"], :name => "is_accounts_uid", :unique => true

    create_table "is_adminroles", :force => true, :options => options do |t|
      t.string  "roleid",                                    :null => false
      t.string  "name",        :limit => 256,                :null => false
      t.string  "permission",  :limit => 256,                :null => false
      t.integer "allowdelete", :limit => 11,  :default => 1, :null => false
    end

    add_index "is_adminroles", ["roleid"], :name => "is_adminRoles_unique", :unique => true

    create_table "is_authcredentials", :force => true, :options => options do |t|
      t.string  "UID",        :limit => 300,                :null => false
      t.integer "sysNum",     :limit => 11,  :default => 0, :null => false
      t.string  "authType",   :limit => 16,                 :null => false
      t.string  "authDomain", :limit => 64
      t.string  "authUid",    :limit => 300,                :null => false
      t.string  "authPasswd", :limit => 512
    end

    add_index "is_authcredentials", ["UID"], :name => "is_authCredentials_uid"

    create_table "is_caches", :force => true, :options => options do |t|
      t.string    "UID",       :limit => 150,  :null => false
      t.string    "url",       :limit => 1024, :null => false
      t.string    "url_key",   :limit => 256,  :null => false
      t.timestamp "timestamp",                 :null => false
      t.text      "headers",                   :null => false
      t.text      "body",                      :null => false
    end

    add_index "is_caches", ["UID"], :name => "is_caches_uid"
    add_index "is_caches", ["url_key"], :name => "is_caches_url"

    create_table "is_forbiddenurls", :force => true, :options => options do |t|
      t.string "url", :limit => 1024, :null => false
    end

    create_table "is_gadget_icons", :force => true, :options => options do |t|
      t.string "type", :limit => 255,  :null => false
      t.string "url",  :limit => 1024, :null => false
    end

    add_index "is_gadget_icons", ["type"], :name => "is_gadget_icons_type", :unique => true

    create_table "is_gadgets", :force => true, :options => options do |t|
      t.string    "type",         :limit => 50,  :null => false
      t.string    "path",         :limit => 512, :null => false
      t.string    "name",                        :null => false
      t.binary    "data"
      t.timestamp "lastmodified",                :null => false
    end

    add_index "is_gadgets", ["name"], :name => "is_gadgets_name"
    add_index "is_gadgets", ["path"], :name => "is_gadgets_path"
    #add_index "is_gadgets", ["type", "path", "name"], :name => "is_gadgets_uq", :unique => true
    add_index "is_gadgets", ["type"], :name => "is_gadgets_type"

    create_table "is_holidays", :force => true, :options => options do |t|
      t.string    "country",    :limit => 5, :null => false
      t.string    "lang",       :limit => 5, :null => false
      t.text      "data"
      t.timestamp "updated_at",              :null => false
    end

    add_index "is_holidays", ["country", "lang"], :name => "is_holidays_uq", :unique => true

    create_table "is_i18n", :force => true, :options => options do |t|
      t.string "type",    :limit => 10,   :null => false
      t.string "name",    :limit => 239,  :null => false
      t.string "country", :limit => 3,    :null => false
      t.string "lang",    :limit => 3,    :null => false
      t.string "message", :limit => 2048, :null => false
    end

    add_index "is_i18n", ["type", "name", "country", "lang"], :name => "is_i18n_uq", :unique => true

    create_table "is_i18nlastmodified", :force => true, :options => options do |t|
      t.string "type",            :limit => 32,   :null => false
      t.timestamp "lastmodified", :null => false
    end

    add_index "is_i18nlastmodified", ["type"], :name => "is_i18nlastmodified_type", :unique => true

    create_table "is_i18nlocales", :force => true, :options => options do |t|
      t.string "type",    :limit => 32, :null => false
      t.string "country", :limit => 5,  :null => false
      t.string "lang",    :limit => 5,  :null => false
    end

    add_index "is_i18nlocales", ["type", "country", "lang"], :name => "is_locales_unique", :unique => true

    create_table "is_keywords", :force => true, :options => options do |t|
      t.string  "UID",     :limit => 150, :null => false
      t.integer "type",    :limit => 11,  :null => false
      t.string  "keyword", :limit => 500, :null => false
      t.string  "DATE",    :limit => 24,  :null => false
    end

    add_index "is_keywords", ["DATE"], :name => "is_keywords_date"
    add_index "is_keywords", ["UID"], :name => "is_keywords_uid"
    add_index "is_keywords", ["keyword"], :name => "is_keywords_keyword"
    add_index "is_keywords", ["type"], :name => "is_keywords_type"

    create_table "is_logs", :force => true, :options => options do |t|
      t.string  "UID",        :limit => 150,  :null => false
      t.integer "type",       :limit => 11,   :null => false
      t.string  "url",        :limit => 1024, :null => false
      t.string  "url_key",    :limit => 256,  :null => false
      t.string  "rssurl",     :limit => 1024, :null => false
      t.string  "rssurl_key", :limit => 256,  :null => false
      t.string  "DATE",       :limit => 24,   :null => false
    end

    add_index "is_logs", ["DATE"], :name => "is_logs_date"
    add_index "is_logs", ["UID"], :name => "is_logs_uid"
    add_index "is_logs", ["rssurl_key"], :name => "is_logs_rssurl"
    add_index "is_logs", ["type"], :name => "is_logs_type"
    add_index "is_logs", ["url_key"], :name => "is_logs_url"

    create_table "is_menucaches", :force => true, :options => options do |t|
      t.string "UID",     :limit => 150,  :null => false
      t.string "url_key", :limit => 128, :null => false
      t.binary "menuIds"
    end

    add_index "is_menucaches", ["UID", "url_key"], :name => "is_menucaches_uq", :unique => true

    create_table "is_menus", :force => true, :options => options do |t|
      t.string "type",     :limit => 150,  :null => false
      t.text   "data",                     :null => false
    end

    add_index "is_menus", ["type"], :name => "is_menus_type", :unique => true

    create_table "is_menus_temp", :force => true, :options => options do |t|
      t.string    "type",         :limit => 150, :null => false
      t.string    "siteTopId",    :limit => 150, :null => false
      t.text      "data",                        :null => false
      t.string    "workingUid",   :limit => 150
      t.timestamp "lastmodified",                :null => false
    end

    add_index "is_menus_temp", ["type", "siteTopId"], :name => "is_menus_temp_uq", :unique => true
    add_index "is_menus_temp", ["lastmodified"], :name => "is_menus_temp_lastmodified"

    create_table "is_messages", :force => true, :options => options do |t|
      t.string    "FROM",        :limit => 150,  :null => false
      t.string    "displayFrom", :limit => 150
      t.string    "TO",          :limit => 150
      t.text      "toJSON"
      t.string    "body",        :limit => 2048
      t.timestamp "posted_time",                 :null => false
      t.string    "type",        :limit => 10,   :null => false
      t.text      "OPTION"
    end

    add_index "is_messages", ["FROM"], :name => "is_messages_from"
    add_index "is_messages", ["TO"], :name => "is_messages_to"
    add_index "is_messages", ["posted_time"], :name => "is_messages_posted_time"
    add_index "is_messages", ["type"], :name => "is_messages_type"

    create_table "is_portaladmins", :force => true, :options => options do |t|
      t.string "UID",    :limit => 150, :null => false
      t.string "ROLEID"
    end

    add_index "is_portaladmins", ["ROLEID"], :name => "is_portaladmins_roleid"
    add_index "is_portaladmins", ["UID"], :name => "is_portaladmins_uq", :unique => true
    execute('ALTER TABLE is_portaladmins ADD FOREIGN KEY (ROLEID) REFERENCES IS_ADMINROLES (ROLEID) ON DELETE SET NULL')

    create_table "is_portallayouts", :force => true, :options => options do |t|
      t.string "name",    :limit => 50,  :null => false
      t.text   "layout",                 :null => false
    end

    add_index "is_portallayouts", ["name"], :name => "is_portallayouts_name", :unique => true

    create_table "is_preferences", :force => true, :options => options do |t|
      t.string "UID",    :limit => 150, :null => false
      t.text "data",     :null => false
    end

    add_index "is_preferences", ["UID"], :name => "is_preferences_uid", :unique => true

    create_table "is_properties", :force => true, :options => options do |t|
      t.string  "name",      :limit => 255,   :null => false
      t.string  "category",  :limit => 128
      t.integer "advanced",  :limit => 11,   :null => false
      t.string  "value",     :limit => 1024
      t.string  "datatype",  :limit => 128
      t.string  "enumValue", :limit => 1024
      t.integer "required",  :limit => 11,   :null => false
      t.string  "regex",     :limit => 1024
      t.string  "regexMsg",  :limit => 1024
    end

    add_index "is_properties", ["name"], :name => "is_properties_name", :unique => true
    add_index "is_properties", ["advanced"], :name => "is_properties_advanced"

    create_table "is_proxyconfs", :id => false, :force => true, :options => options do |t|
      t.integer   "temp",         :limit => 11, :null => false
      t.text      "data",                       :null => false
      t.timestamp "lastmodified",               :null => false
    end

    create_table "is_rsscaches", :force => true, :options => options do |t|
      t.string  "UID",     :limit => 150,  :null => false
      t.string  "url_key", :limit => 128, :null => false
      t.integer "pageNum", :limit => 11,  :null => false
      t.binary  "rss"
    end
    
    add_index "is_rsscaches", ["UID", "url_key", "pageNum"], :name => "is_rsscaches_uq", :unique => true

    create_table "is_searchengines", :id => false, :force => true, :options => options do |t|
      t.integer "temp", :limit => 11, :null => false
      t.text    "data",               :null => false
    end

    create_table "is_sessions", :force => true, :options => options do |t|
      t.string    "UID",           :limit => 150, :null => false
      t.string    "sessionId",     :limit => 256, :null => false
      t.timestamp "LOGINDATETIME",                :null => false
    end

    add_index "is_sessions", ["UID"], :name => "is_sessions_uid", :unique => true
    add_index "is_sessions", ["LOGINDATETIME"], :name => "is_sessions_loginDateTime"
    add_index "is_sessions", ["sessionId"], :name => "is_sessions_sessionId"

    create_table "is_systemmessages", :force => true, :options => options do |t|
      t.string  "TO",            :limit => 150,                 :null => false
      t.string  "body",          :limit => 2048
      t.string  "resourceId",    :limit => 512
      t.string  "replaceValues", :limit => 2048
      t.integer "isRead",        :limit => 11,   :default => 0
    end

    add_index "is_systemmessages", ["TO"], :name => "is_systemmessages_to"
    add_index "is_systemmessages", ["isRead"], :name => "is_systemmessages"

    create_table "is_tablayouts", :force => true, :options => options do |t|
      t.string  "tabId",               :limit => 50,                                       :null => false
      t.integer "roleOrder",           :limit => 11,                                       :null => false
      t.text    "role",                                                                    :null => false
      t.string  "rolename",            :limit => 256,                                      :null => false
      t.string  "principalType",       :limit => 50,  :default => "OrganizationPrincipal"
      t.string  "defaultUid",          :limit => 150
      t.text    "widgets",                                                                 :null => false
      t.text    "layout"
      t.string  "widgetsLastmodified", :limit => 24
      t.integer "tabNumber",           :limit => 11
      t.integer "deleteFlag",          :limit => 11,  :default => 0,                       :null => false
      t.boolean "temp",                               :default => false,                   :null => false
      t.string  "workingUid",          :limit => 150
    end

    add_index "is_tablayouts", ["tabId", "roleOrder", "temp"], :name => "is_tablayouts_uq", :unique => true

    create_table "is_tabs", :force => true, :options => options do |t|
      t.string  "UID",     :limit => 150,  :null => false
      t.string  "defaultUid",         :limit => 150
      t.string  "tabId",                 :limit => 32,  :null => false
      t.string  "name",               :limit => 256
      t.integer "ORDER",              :limit => 11
      t.string  "type",               :limit => 128
      t.text    "data"
      t.string  "widgetLastModified", :limit => 32
    end

    add_index "is_tabs", ["UID", "tabId"], :name => "is_tabs_uq", :unique => true

    create_table "is_userprefs", :id => false, :force => true, :options => options do |t|
      t.integer "fk_widget_id", :limit => 11,   :null => false
      t.string  "name",                         :null => false
      t.string  "value",        :limit => 4000
      t.text    "long_value"
    end

    add_index "is_userprefs", ["fk_widget_id", "name"], :name => "is_userprefs_uq", :unique => true
    add_index "is_userprefs", ["fk_widget_id"], :name => "is_userprefs_fk_widget_id"
    add_index "is_userprefs", ["name"], :name => "is_userprefs_name"
    add_index "is_userprefs", ["value"], :name => "is_userprefs_value"

    create_table "is_widgetconfs", :force => true, :options => options do |t|
      t.string  "type",    :limit => 50,   :null => false
      t.text "data",       :null => false
    end

    add_index "is_widgetconfs", ["type"], :name => "is_widgetconfs_type", :unique => true

    create_table "is_widgets", :force => true, :options => options do |t|
      t.string  "UID",          :limit => 75,                   :null => false
      t.string  "defaultUid",   :limit => 150
      t.string  "tabId",        :limit => 32,                   :null => false
      t.string  "widgetId",     :limit => 128,                  :null => false
      t.integer "COLUMN",       :limit => 11
      t.string  "siblingId",    :limit => 256
      t.string  "parentId",     :limit => 256
      t.string  "menuId",       :limit => 256,  :default => "", :null => false
      t.string  "href",         :limit => 1024
      t.string  "title",        :limit => 256
      t.string  "type",         :limit => 1024
      t.integer "isStatic",     :limit => 11
      t.integer "ignoreHeader", :limit => 11
      t.integer "createDate",   :limit => 20,   :default => 0,  :null => false
      t.integer "deleteDate",   :limit => 20,   :default => 0,  :null => false
    end

    add_index "is_widgets", ["UID", "tabId", "widgetId", "deleteDate"], :name => "is_widgets_unique", :unique => true
    add_index "is_widgets", ["deleteDate"], :name => "is_widgets_deleteDate"
    add_index "is_widgets", ["parentId"], :name => "is_widgets_parentId"
    add_index "is_widgets", ["tabId"], :name => "is_widgets_tabId"
    add_index "is_widgets", ["type"], :name => "is_widgets_type"

    execute('ALTER TABLE is_userprefs ADD FOREIGN KEY (fk_widget_id) REFERENCES IS_WIDGETS(id) ON DELETE CASCADE')

  end
  def self.down
    drop_table :IS_PREFERENCES
    drop_table :IS_TABS
    drop_table :IS_WIDGETS
    drop_table :IS_USERPREFS
    drop_table :IS_CACHES
    drop_table :IS_RSSCACHES
    drop_table :IS_MENUCACHES
    drop_table :IS_LOGS
    drop_table :IS_WIDGETCONFS
    drop_table :IS_KEYWORDS
    drop_table :IS_MENUS
    drop_table :IS_MENUS_TEMP
    drop_table :IS_SEARCHENGINES
    drop_table :IS_GADGETS
    drop_table :IS_GADGET_ICONS
    drop_table :IS_PROPERTIES
    drop_table :IS_PROXYCONFS
    drop_table :IS_I18N
    drop_table :IS_I18NLASTMODIFIED
    drop_table :IS_I18NLOCALES
    drop_table :IS_TABLAYOUTS
    drop_table :IS_PORTALLAYOUTS
    drop_table :IS_ADMINROLES
    drop_table :IS_PORTALADMINS
    drop_table :IS_SESSIONS
    drop_table :IS_FORBIDDENURLS
    drop_table :IS_AUTHCREDENTIALS
    drop_table :IS_HOLIDAYS
    drop_table :IS_ACCESSLOGS
    drop_table :IS_MESSAGES
    drop_table :IS_SYSTEMMESSAGES
    drop_table :IS_ACCOUNTS
  end
end
