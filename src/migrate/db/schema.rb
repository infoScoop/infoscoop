# This file is auto-generated from the current state of the database. Instead of editing this file, 
# please use the migrations feature of Active Record to incrementally modify your database, and
# then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your database schema. If you need
# to create the application database on another system, you should be using db:schema:load, not running
# all the migrations from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended to check this file into your version control system.

ActiveRecord::Schema.define(:version => 20100324055445) do

  create_table "is_accesslogs", :force => true do |t|
    t.string "UID",  :limit => 150, :null => false
    t.string "DATE", :limit => 8,   :null => false
  end

  add_index "is_accesslogs", ["UID", "DATE"], :name => "is_accesslogs_uq", :unique => true

  create_table "is_accesslogs_2_0", :force => true do |t|
    t.string "UID",  :limit => 150, :null => false
    t.string "DATE", :limit => 8,   :null => false
  end

  add_index "is_accesslogs_2_0", ["UID", "DATE"], :name => "is_accesslogs_uq", :unique => true

  create_table "is_accounts", :force => true do |t|
    t.string "uid",      :limit => 150, :null => false
    t.string "name"
    t.string "password"
  end

  add_index "is_accounts", ["uid"], :name => "is_accounts_uid", :unique => true

  create_table "is_accounts_2_0", :primary_key => "uid", :force => true do |t|
    t.string "name"
    t.string "password"
  end

  create_table "is_adminroles", :force => true do |t|
    t.string  "roleid",                                    :null => false
    t.string  "name",        :limit => 256,                :null => false
    t.string  "permission",  :limit => 256,                :null => false
    t.integer "allowdelete", :limit => 11,  :default => 1, :null => false
  end

  add_index "is_adminroles", ["roleid"], :name => "is_adminRoles_unique", :unique => true

  create_table "is_adminroles_2_0", :force => true do |t|
    t.string  "roleid",                                    :null => false
    t.string  "name",        :limit => 256,                :null => false
    t.string  "permission",  :limit => 256,                :null => false
    t.integer "allowdelete", :limit => 11,  :default => 1, :null => false
  end

  add_index "is_adminroles_2_0", ["roleid"], :name => "is_adminRoles_unique", :unique => true

  create_table "is_authcredentials", :force => true do |t|
    t.string  "UID",        :limit => 300,                :null => false
    t.integer "sysNum",     :limit => 11,  :default => 0, :null => false
    t.string  "authType",   :limit => 16,                 :null => false
    t.string  "authDomain", :limit => 64
    t.string  "authUid",    :limit => 300,                :null => false
    t.string  "authPasswd", :limit => 512
  end

  add_index "is_authcredentials", ["UID"], :name => "is_authCredentials_uid"

  create_table "is_authcredentials_2_0", :force => true do |t|
    t.string  "UID",        :limit => 300,                :null => false
    t.integer "sysNum",     :limit => 11,  :default => 0, :null => false
    t.string  "authType",   :limit => 16,                 :null => false
    t.string  "authDomain", :limit => 64
    t.string  "authUid",    :limit => 300,                :null => false
    t.string  "authPasswd", :limit => 512
  end

  create_table "is_caches", :force => true do |t|
    t.string   "UID",       :limit => 150,  :null => false
    t.string   "url",       :limit => 1024, :null => false
    t.string   "url_key",   :limit => 256,  :null => false
    t.datetime "timestamp",                 :null => false
    t.text     "headers",                   :null => false
    t.text     "body",                      :null => false
  end

  add_index "is_caches", ["UID"], :name => "is_caches_uid"
  add_index "is_caches", ["url_key"], :name => "is_caches_url"

  create_table "is_caches_2_0", :force => true do |t|
    t.string    "UID",       :limit => 150,  :null => false
    t.string    "url",       :limit => 1024, :null => false
    t.string    "url_key",   :limit => 256,  :null => false
    t.timestamp "timestamp",                 :null => false
    t.text      "headers",                   :null => false
    t.text      "body",                      :null => false
  end

  create_table "is_forbiddenurls", :force => true do |t|
    t.string "url", :limit => 1024, :null => false
  end

  create_table "is_forbiddenurls_2_0", :force => true do |t|
    t.string "url", :limit => 1024, :null => false
  end

  create_table "is_gadget_icons", :force => true do |t|
    t.string "type",                 :null => false
    t.string "url",  :limit => 1024, :null => false
  end

  add_index "is_gadget_icons", ["type"], :name => "is_gadget_icons_type", :unique => true

  create_table "is_gadget_icons_2_0", :primary_key => "type", :force => true do |t|
    t.string "url", :limit => 1024, :null => false
  end

  create_table "is_gadgets", :force => true do |t|
    t.string   "type",         :limit => 50,  :null => false
    t.string   "path",         :limit => 512, :null => false
    t.string   "name",                        :null => false
    t.binary   "data"
    t.datetime "lastmodified",                :null => false
  end

  add_index "is_gadgets", ["name"], :name => "is_gadgets_name"
  add_index "is_gadgets", ["path"], :name => "is_gadgets_path"
  add_index "is_gadgets", ["type"], :name => "is_gadgets_type"

  create_table "is_gadgets_2_0", :force => true do |t|
    t.string    "type",         :limit => 50,  :null => false
    t.string    "path",         :limit => 512, :null => false
    t.string    "name",                        :null => false
    t.binary    "data"
    t.timestamp "lastmodified",                :null => false
  end

  add_index "is_gadgets_2_0", ["type", "path", "name"], :name => "is_gadgets_uq", :unique => true

  create_table "is_holidays", :force => true do |t|
    t.string   "country",    :limit => 5, :null => false
    t.string   "lang",       :limit => 5, :null => false
    t.text     "data"
    t.datetime "updated_at",              :null => false
  end

  add_index "is_holidays", ["country", "lang"], :name => "is_holidays_uq", :unique => true

  create_table "is_holidays_2_0", :primary_key => "country", :force => true do |t|
    t.string    "lang",       :limit => 5, :null => false
    t.text      "data"
    t.timestamp "updated_at",              :null => false
  end

  create_table "is_i18n", :force => true do |t|
    t.string "type",    :limit => 10,   :null => false
    t.string "name",    :limit => 239,  :null => false
    t.string "country", :limit => 3,    :null => false
    t.string "lang",    :limit => 3,    :null => false
    t.string "message", :limit => 2048, :null => false
  end

  add_index "is_i18n", ["type", "name", "country", "lang"], :name => "is_i18n_uq", :unique => true

  create_table "is_i18n_2_0", :primary_key => "country", :force => true do |t|
    t.string "type",    :limit => 32,   :null => false
    t.string "id",      :limit => 256,  :null => false
    t.string "lang",    :limit => 5,    :null => false
    t.string "message", :limit => 2048, :null => false
  end

  create_table "is_i18nlastmodified", :force => true do |t|
    t.string   "type",         :limit => 32, :null => false
    t.datetime "lastmodified",               :null => false
  end

  add_index "is_i18nlastmodified", ["type"], :name => "is_i18nlastmodified_type", :unique => true

  create_table "is_i18nlastmodified_2_0", :primary_key => "type", :force => true do |t|
    t.timestamp "lastmodified", :null => false
  end

  create_table "is_i18nlocales", :force => true do |t|
    t.string "type",    :limit => 32, :null => false
    t.string "country", :limit => 5,  :null => false
    t.string "lang",    :limit => 5,  :null => false
  end

  add_index "is_i18nlocales", ["type", "country", "lang"], :name => "is_locales_unique", :unique => true

  create_table "is_i18nlocales_2_0", :force => true do |t|
    t.string "type",    :limit => 32, :null => false
    t.string "country", :limit => 5,  :null => false
    t.string "lang",    :limit => 5,  :null => false
  end

  add_index "is_i18nlocales_2_0", ["type", "country", "lang"], :name => "is_locales_unique", :unique => true

  create_table "is_keywords", :force => true do |t|
    t.string  "UID",     :limit => 150, :null => false
    t.integer "type",    :limit => 11,  :null => false
    t.string  "keyword", :limit => 500, :null => false
    t.string  "DATE",    :limit => 24,  :null => false
  end

  add_index "is_keywords", ["DATE"], :name => "is_keywords_date"
  add_index "is_keywords", ["UID"], :name => "is_keywords_uid"
  add_index "is_keywords", ["keyword"], :name => "is_keywords_keyword"
  add_index "is_keywords", ["type"], :name => "is_keywords_type"

  create_table "is_keywords_2_0", :force => true do |t|
    t.string  "UID",     :limit => 150, :null => false
    t.integer "type",    :limit => 11,  :null => false
    t.string  "keyword", :limit => 500, :null => false
    t.string  "DATE",    :limit => 24,  :null => false
  end

  create_table "is_logs", :force => true do |t|
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

  create_table "is_logs_2_0", :force => true do |t|
    t.string  "UID",        :limit => 150,  :null => false
    t.integer "type",       :limit => 11,   :null => false
    t.string  "url",        :limit => 1024, :null => false
    t.string  "url_key",    :limit => 256,  :null => false
    t.string  "rssurl",     :limit => 1024, :null => false
    t.string  "rssurl_key", :limit => 256,  :null => false
    t.string  "DATE",       :limit => 24,   :null => false
  end

  create_table "is_menucaches", :force => true do |t|
    t.string "UID",     :limit => 150, :null => false
    t.string "url_key", :limit => 128, :null => false
    t.binary "menuIds"
  end

  add_index "is_menucaches", ["UID", "url_key"], :name => "is_menucaches_uq", :unique => true

  create_table "is_menucaches_2_0", :primary_key => "UID", :force => true do |t|
    t.string "url_key", :limit => 128, :null => false
    t.binary "menuIds"
  end

  create_table "is_menus", :force => true do |t|
    t.string "type", :limit => 150, :null => false
    t.text   "data",                :null => false
  end

  add_index "is_menus", ["type"], :name => "is_menus_type", :unique => true

  create_table "is_menus_2_0", :primary_key => "type", :force => true do |t|
    t.text "data", :null => false
  end

  create_table "is_menus_temp", :force => true do |t|
    t.string   "type",         :limit => 150, :null => false
    t.string   "siteTopId",    :limit => 150, :null => false
    t.text     "data",                        :null => false
    t.string   "workingUid",   :limit => 150
    t.datetime "lastmodified",                :null => false
  end

  add_index "is_menus_temp", ["lastmodified"], :name => "is_menus_temp_lastmodified"
  add_index "is_menus_temp", ["type", "siteTopId"], :name => "is_menus_temp_uq", :unique => true

  create_table "is_menus_temp_2_0", :primary_key => "siteTopId", :force => true do |t|
    t.string    "type",         :limit => 150, :null => false
    t.text      "data",                        :null => false
    t.string    "workingUid",   :limit => 150
    t.timestamp "lastmodified",                :null => false
  end

  create_table "is_messages", :force => true do |t|
    t.string   "FROM",        :limit => 150,  :null => false
    t.string   "displayFrom", :limit => 150
    t.string   "TO",          :limit => 150
    t.text     "toJSON"
    t.string   "body",        :limit => 2048
    t.datetime "posted_time",                 :null => false
    t.string   "type",        :limit => 10,   :null => false
    t.text     "OPTION"
  end

  add_index "is_messages", ["FROM"], :name => "is_messages_from"
  add_index "is_messages", ["TO"], :name => "is_messages_to"
  add_index "is_messages", ["posted_time"], :name => "is_messages_posted_time"
  add_index "is_messages", ["type"], :name => "is_messages_type"

  create_table "is_messages_2_0", :force => true do |t|
    t.string    "FROM",        :limit => 150,  :null => false
    t.string    "displayFrom", :limit => 150
    t.string    "TO",          :limit => 150
    t.text      "toJSON"
    t.string    "body",        :limit => 2048
    t.timestamp "posted_time",                 :null => false
    t.string    "type",        :limit => 10,   :null => false
    t.text      "OPTION"
  end

  create_table "is_portaladmins", :force => true do |t|
    t.string "UID",    :limit => 150, :null => false
    t.string "ROLEID"
  end

  add_index "is_portaladmins", ["ROLEID"], :name => "is_portaladmins_roleid"
  add_index "is_portaladmins", ["UID"], :name => "is_portaladmins_uq", :unique => true

  create_table "is_portaladmins_2_0", :force => true do |t|
    t.string "UID",    :limit => 150, :null => false
    t.string "ROLEID"
  end

  add_index "is_portaladmins_2_0", ["ROLEID"], :name => "ROLEID"
  add_index "is_portaladmins_2_0", ["UID"], :name => "is_portaladmins_uq", :unique => true

  create_table "is_portallayouts", :force => true do |t|
    t.string "name",   :limit => 50, :null => false
    t.text   "layout",               :null => false
  end

  add_index "is_portallayouts", ["name"], :name => "is_portallayouts_name", :unique => true

  create_table "is_portallayouts_2_0", :primary_key => "name", :force => true do |t|
    t.text "layout", :null => false
  end

  create_table "is_preferences", :force => true do |t|
    t.string "UID",  :limit => 150, :null => false
    t.text   "data",                :null => false
  end

  add_index "is_preferences", ["UID"], :name => "is_preferences_uid", :unique => true

  create_table "is_preferences_2_0", :primary_key => "UID", :force => true do |t|
    t.text "data", :null => false
  end

  create_table "is_properties", :force => true do |t|
    t.string  "name",                      :null => false
    t.string  "category",  :limit => 128
    t.integer "advanced",  :limit => 11,   :null => false
    t.string  "value",     :limit => 1024
    t.string  "datatype",  :limit => 128
    t.string  "enumValue", :limit => 1024
    t.integer "required",  :limit => 11,   :null => false
    t.string  "regex",     :limit => 1024
    t.string  "regexMsg",  :limit => 1024
  end

  add_index "is_properties", ["advanced"], :name => "is_properties_advanced"
  add_index "is_properties", ["name"], :name => "is_properties_name", :unique => true

  create_table "is_properties_2_0", :force => true do |t|
    t.string  "category",  :limit => 128
    t.integer "advanced",  :limit => 11,   :null => false
    t.string  "value",     :limit => 1024
    t.string  "datatype",  :limit => 128
    t.string  "enumValue", :limit => 1024
    t.integer "required",  :limit => 11,   :null => false
    t.string  "regex",     :limit => 1024
    t.string  "regexMsg",  :limit => 1024
  end

  create_table "is_proxyconfs", :id => false, :force => true do |t|
    t.integer  "temp",         :limit => 11, :null => false
    t.text     "data",                       :null => false
    t.datetime "lastmodified",               :null => false
  end

  create_table "is_proxyconfs_2_0", :id => false, :force => true do |t|
    t.integer   "temp",         :limit => 11, :null => false
    t.text      "data",                       :null => false
    t.timestamp "lastmodified",               :null => false
  end

  create_table "is_rsscaches", :force => true do |t|
    t.string  "UID",     :limit => 150, :null => false
    t.string  "url_key", :limit => 128, :null => false
    t.integer "pageNum", :limit => 11,  :null => false
    t.binary  "rss"
  end

  add_index "is_rsscaches", ["UID", "url_key", "pageNum"], :name => "is_rsscaches_uq", :unique => true

  create_table "is_rsscaches_2_0", :primary_key => "UID", :force => true do |t|
    t.string  "url_key", :limit => 128, :null => false
    t.integer "pageNum", :limit => 11,  :null => false
    t.binary  "rss"
  end

  create_table "is_searchengines", :id => false, :force => true do |t|
    t.integer "temp", :limit => 11, :null => false
    t.text    "data",               :null => false
  end

  create_table "is_searchengines_2_0", :id => false, :force => true do |t|
    t.integer "temp", :limit => 11, :null => false
    t.text    "data",               :null => false
  end

  create_table "is_sessions", :force => true do |t|
    t.string   "UID",           :limit => 150, :null => false
    t.string   "sessionId",     :limit => 256, :null => false
    t.datetime "LOGINDATETIME",                :null => false
  end

  add_index "is_sessions", ["LOGINDATETIME"], :name => "is_sessions_loginDateTime"
  add_index "is_sessions", ["UID"], :name => "is_sessions_uid", :unique => true
  add_index "is_sessions", ["sessionId"], :name => "is_sessions_sessionId"

  create_table "is_sessions_2_0", :primary_key => "UID", :force => true do |t|
    t.string    "sessionId",     :limit => 256, :null => false
    t.timestamp "LOGINDATETIME",                :null => false
  end

  create_table "is_systemmessages", :force => true do |t|
    t.string  "TO",            :limit => 150,                 :null => false
    t.string  "body",          :limit => 2048
    t.string  "resourceId",    :limit => 512
    t.string  "replaceValues", :limit => 2048
    t.integer "isRead",        :limit => 11,   :default => 0
  end

  add_index "is_systemmessages", ["TO"], :name => "is_systemmessages_to"
  add_index "is_systemmessages", ["isRead"], :name => "is_systemmessages"

  create_table "is_systemmessages_2_0", :force => true do |t|
    t.string  "TO",            :limit => 150,                 :null => false
    t.string  "body",          :limit => 2048
    t.string  "resourceId",    :limit => 512
    t.string  "replaceValues", :limit => 2048
    t.integer "isRead",        :limit => 11,   :default => 0
  end

  create_table "is_tablayouts", :force => true do |t|
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

  create_table "is_tablayouts_2_0", :primary_key => "roleOrder", :force => true do |t|
    t.string  "tabId",               :limit => 50,                                       :null => false
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

  create_table "is_tabs", :force => true do |t|
    t.string  "UID",                :limit => 150, :null => false
    t.string  "defaultUid",         :limit => 150
    t.string  "tabId",              :limit => 32,  :null => false
    t.string  "name",               :limit => 256
    t.integer "ORDER",              :limit => 11
    t.string  "type",               :limit => 128
    t.text    "data"
    t.string  "widgetLastModified", :limit => 32
  end

  add_index "is_tabs", ["UID", "tabId"], :name => "is_tabs_uq", :unique => true

  create_table "is_tabs_2_0", :primary_key => "UID", :force => true do |t|
    t.string  "defaultUid",         :limit => 150
    t.string  "id",                 :limit => 32,  :null => false
    t.string  "name",               :limit => 256
    t.integer "ORDER",              :limit => 11
    t.string  "type",               :limit => 128
    t.text    "data"
    t.string  "widgetLastModified", :limit => 32
  end

  create_table "is_userprefs", :id => false, :force => true do |t|
    t.integer "fk_widget_id", :limit => 11,   :null => false
    t.string  "name",                         :null => false
    t.string  "value",        :limit => 4000
    t.text    "long_value"
  end

  add_index "is_userprefs", ["fk_widget_id", "name"], :name => "is_userprefs_uq", :unique => true
  add_index "is_userprefs", ["fk_widget_id"], :name => "is_userprefs_fk_widget_id"
  add_index "is_userprefs", ["name"], :name => "is_userprefs_name"
  add_index "is_userprefs", ["value"], :name => "is_userprefs_value"

  create_table "is_userprefs_2_0", :id => false, :force => true do |t|
    t.integer "fk_widget_id", :limit => 20,   :null => false
    t.string  "name",                         :null => false
    t.string  "value",        :limit => 4000
    t.text    "long_value"
  end

  add_index "is_userprefs_2_0", ["fk_widget_id", "name"], :name => "is_userprefs_uq", :unique => true

  create_table "is_widgetconfs", :force => true do |t|
    t.string "type", :limit => 50, :null => false
    t.text   "data",               :null => false
  end

  add_index "is_widgetconfs", ["type"], :name => "is_widgetconfs_type", :unique => true

  create_table "is_widgetconfs_2_0", :primary_key => "type", :force => true do |t|
    t.text "data", :null => false
  end

  create_table "is_widgets", :force => true do |t|
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

  create_table "is_widgets_2_0", :force => true do |t|
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

  add_index "is_widgets_2_0", ["UID", "tabId", "widgetId", "deleteDate"], :name => "is_widgets_unique", :unique => true

end
