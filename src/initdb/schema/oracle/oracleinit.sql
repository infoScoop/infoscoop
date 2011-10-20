--
-- PREFERENCE
--
create table is_preferences (
  "UID" varchar(150 BYTE ) not null,
  data clob not null,
  primary key ("UID")
);

--
-- TAB
--
create table is_tabs (
  "UID" varchar(150 BYTE ) not null,
  defaultUid varchar(150 BYTE ),
  id varchar(32 BYTE ) not null,
  name varchar(256 BYTE ),
  "ORDER" int,
  type varchar(128 BYTE ),
  data clob,
  widgetLastModified varchar(32 BYTE ),
  disabledDynamicPanel int,
  primary key ("UID", id)
);

--
-- WIDGET
--

create sequence is_widgets_id_seq;

create table is_widgets (
  id integer not null primary key,
  "UID" varchar(150 BYTE ) not null,
  defaultUid varchar(150 BYTE ),
  tabId varchar(32 BYTE ) not null,
  widgetId varchar(256 BYTE ) not null,
  "COLUMN" int,
  siblingId varchar(256 BYTE ),
  parentId varchar(256 BYTE ),
  menuId varchar(256 BYTE ),
  href varchar(1024 BYTE ),
  title varchar(256 BYTE ),
  type varchar(1024 BYTE ),
  isStatic int,
  ignoreHeader int,
  noBorder int,
  createDate number(18) default 0 not null,
  deleteDate number(18) default 0 not null,
  constraint is_widgets_unique unique ("UID", tabid, widgetId, deleteDate)
);

create index is_widgets_tabId on is_widgets(tabId);
create index is_widgets_parentId on is_widgets(parentId);
create index is_widgets_deleteDate on is_widgets(deleteDate);
create index is_widgets_type on is_widgets(type);

--
-- USERPREFS
--

create table is_userprefs (
	fk_widget_id integer not null,
	name varchar(255 BYTE) not null,
	value varchar(4000 BYTE),
	long_value clob,
	constraint is_userprefs_uq unique (fk_widget_id,name),
	foreign key (fk_widget_id) references is_widgets(id) on delete cascade
);

create index is_userprefs_fk_widget_id on is_userprefs(fk_widget_id);
create index is_userprefs_name on is_userprefs(name);
create index is_userprefs_value on is_userprefs(value);

--
-- CACHE
--
create table is_caches (
  id varchar(64 BYTE ) not null primary key,
  "UID" varchar(150 BYTE ) not null,
  url varchar(1024 BYTE ) not null,
  url_key varchar(256 BYTE ) not null,
  timestamp timestamp not null,
  headers clob not null,
  body clob not null
);

create index is_caches_uid on is_caches("UID");
create index is_caches_url on is_caches(url_key);

create table is_rsscaches (
  "UID" varchar(150 BYTE ) not null,
  url_key varchar(256 BYTE ) not null,
  pageNum int not null,
  rss blob,
  primary key ("UID", url_key, pageNum)
);

create table is_menucaches (
  "UID" varchar(150 BYTE ) not null,
  url_key varchar(256 BYTE ) not null,
  menuIds blob,
  primary key ("UID", url_key)
);

--
-- LOGS
--

create sequence is_logs_id_seq;

create table is_logs (
  id number(18) not null primary key,
  "UID" varchar (150 BYTE ),
  type integer not null,
  url varchar (1024 BYTE ),
  url_key varchar (256 BYTE ),
  rssurl varchar (1024 BYTE ),
  rssurl_key varchar (256 BYTE ),
  "DATE" varchar (24) not null
);

create index is_logs_uid on is_logs("UID");
create index is_logs_type on is_logs(type);
create index is_logs_url on is_logs(url_key);
create index is_logs_rssurl on is_logs(rssurl_key);
create index is_logs_date on is_logs("DATE");

--
-- WIDGETCONF
--
create table is_widgetconfs (
  type varchar(50 BYTE ) not null primary key,
  data clob not null
);

--
-- KEYWORDLOG
--

create sequence is_keywords_id_seq;

create table is_keywords (
  id number(18) not null primary key,
  "UID" varchar (150) not null,
  type integer not null,
  keyword varchar (500)  not null,
  "DATE" varchar (24) not null
);

create index is_keywords_uid on is_keywords("UID");
create index is_keywords_type on is_keywords(type);
create index is_keywords_keyword on is_keywords(keyword);
create index is_keywords_date on is_keywords("DATE");

--
-- siteAggregationMenu
--
create table is_menus (
  type varchar(150 BYTE) not null primary key,
  data clob not null
);

--
-- siteAggregationMenu_temp
--
create table is_menus_temp (
  type varchar(150 BYTE) not null,
  siteTopId varchar(150 BYTE) not null,
  data clob not null,
  workingUid varchar(150 BYTE),
  lastmodified timestamp not null,
  primary key (type, siteTopId)
);

create index is_menus_temp_lastmodified on is_menus_temp(lastmodified);

--
-- searchEngine
--
create table is_searchEngines (
  temp integer not null,
  data clob not null
);

--
-- gadget
--
create sequence is_gadgets_id_seq;

create table is_gadgets (
  id number(18) not null primary key,
  type varchar(50 BYTE ) not null,
  path varchar(512 BYTE) not null,
  name varchar(255 BYTE),
  data blob,
  lastmodified timestamp,
  constraint is_gadgets_unique unique (type,path,name)
);

create index is_gadgets_type on is_gadgets(type);
create index is_gadgets_path on is_gadgets(path);
create index is_gadgets_name on is_gadgets(name);

--
-- gadget_icons
--
create table is_gadget_icons (
  type varchar(512 BYTE ) not null primary key,
  url varchar(1024 BYTE )
);

--
-- properties
--
create table is_properties (
  id varchar(512 BYTE ) not null primary key,
  category varchar(128 BYTE ),
  advanced integer not null,
  value varchar(1024 BYTE ),
  datatype varchar(128 BYTE ),
  enumValue varchar(1024 BYTE ),
  required integer not null,
  regex varchar(1024 BYTE ),
  regexMsg varchar(1024 BYTE )
);

create index is_properties_advanced on is_properties(advanced);

--
-- proxyConf
--
create table is_proxyConfs (
  temp integer not null,
  data clob not null,
  lastmodified timestamp
);

--
-- i18n
--
create table is_i18n (
  type varchar(32 BYTE ) not null,
  id varchar(512 BYTE ) not null,
  country varchar(5 BYTE ) not null,
  lang varchar(5 BYTE ) not null,
  message varchar(2048 BYTE ) not null,
  primary key (type, id, country, lang)
);

--
-- i18nLastmodified
--
create table is_i18nLastmodified (
  type varchar(32 BYTE ) not null primary key,
  lastmodified timestamp
);

--
-- i18nLocale
--

create sequence is_i18nlocales_id_seq;

create table is_i18nLocales (
  id number(18) not null primary key,
  type varchar(32 BYTE ) not null,
  country varchar(5 BYTE ) not null,
  lang varchar(5 BYTE ) not null,
  constraint is_locales_unique unique (type, country, lang)
);

--
-- tabLayout
--
create table is_tabLayouts (
  tabId varchar(50 BYTE ) not null,
  roleOrder integer not null,
  role clob not null,
  rolename varchar(256 BYTE ) not null,
  principalType varchar(50 BYTE ) default 'OrganizationPrincipal' not null,
  defaultUid varchar (150 BYTE ),
  widgets clob not null,
  layout clob,
  widgetsLastmodified varchar(24 BYTE ),
  tabNumber integer,
  deleteFlag integer default 0 not null,
  temp integer default 0 not null,
  workingUid varchar (150),
  primary key (tabId, roleOrder, temp)
);

--
-- portalLayout
--
create table is_portalLayouts (
  name varchar(50 BYTE ) not null,
  layout clob,
  primary key (name)
);

--
-- adminRole
--
create table is_adminRoles (
  id integer not null primary key,
  roleid varchar(256 BYTE) not null,
  name VARCHAR(256 BYTE) NOT NULL,
  permission VARCHAR(256 BYTE) NOT NULL,
  allowdelete int default 1 NOT NULL,
  constraint is_adminRoles_unique unique (roleid)
);
create sequence is_adminRoles_id_seq;

--
-- portalAdmins
--
create table is_portalAdmins (
  id integer not null primary key,
  "UID" varchar(150 BYTE ) not null,
  ROLEID varchar(256 BYTE),
  FOREIGN KEY (ROLEID) REFERENCES IS_ADMINROLES (ROLEID) ON DELETE SET NULL,
  constraint IS_PORTALADMINS_unique unique ("UID")
);
create sequence is_portalAdmins_id_seq;

--
-- session
--
create table is_sessions (
  "UID" varchar(150 BYTE ) not null,
  sessionId varchar(256 BYTE ) not null,
  LOGINDATETIME TIMESTAMP,
  primary key ("UID")
);

create index is_sessions_sessionId on is_sessions(sessionId);
create index is_sessions_loginDateTime on is_sessions(loginDateTime);

--
-- forbiddenURLs
--

create sequence is_forbiddenURLs_id_seq;

create table is_forbiddenURLs (
  id number(18) not null primary key,
  url varchar(1024 BYTE ) not null
);

--
-- authCredential
--

create sequence is_authCredentials_id_seq;

create table is_authCredentials (
  id number(18) not null primary key,
  "UID" varchar(300 BYTE ) not null,
  sysNum integer default 0 not null,
  authType varchar(16 BYTE ) not null,
  authDomain varchar(64 BYTE ),
  authUid varchar(300 BYTE ) not null,
  authPasswd varchar(512 BYTE )
);

create index is_authCredentials_uid on is_authCredentials("UID");

--
-- holidays
--
create table is_holidays (
  country varchar(5 BYTE ) not null,
  lang varchar(5 BYTE ) not null,
  data clob,
  updated_at timestamp,
  primary key (country, lang)
);

--
-- accesslog
--

create sequence is_accesslogs_id_seq;

create table is_accesslogs (
  id number(18) not null primary key,
  "UID" varchar(150 BYTE) not null,
  "DATE" varchar(8 BYTE) not null,
  constraint is_accesslogs_uq unique ("UID", "DATE")
);

--
-- messages
--

create sequence is_messages_id_seq;

create table is_messages (
  id number(18) not null primary key,
  "FROM" varchar(150 BYTE) not null,
  displayFrom varchar(150 BYTE),
  "TO" varchar(150 BYTE),
  toJSON clob,
  body varchar(2048 BYTE),
  posted_time TIMESTAMP not null,
  type varchar(10 BYTE) not null,
  "OPTION" clob
);
create index is_messages_from on is_messages("FROM");
create index is_messages_to on is_messages("TO");
create index is_messages_posted_time on is_messages(posted_time);
create index is_messages_type on is_messages(type);

--
-- system messages
--
create table IS_SYSTEMMESSAGES (
  id number(18) not null primary key,
  "TO" varchar(150 BYTE) not null,
  body varchar(2048 BYTE),
  resourceId varchar(512 BYTE),
  replaceValues varchar(2048 BYTE),
  isRead number(1) default 0
);
create index is_systemmessages_to on is_systemmessages("TO");
create index is_systemmessages on is_systemmessages(isread);

--
-- account
--
create table IS_ACCOUNTS (
  "UID" varchar(150 BYTE ) not null primary key,
  name varchar(255 BYTE ),
  password varchar(255 BYTE )
);

--
-- OAUTH_CONSUMER
--
create table is_oauth_consumers (
  id varchar(64 BYTE) not null primary key,
  service_name varchar(255 BYTE) not null,
  consumer_key varchar(255 BYTE),
  consumer_secret varchar(255 BYTE),
  signature_method varchar(20 BYTE),
  description clob
);
create index is_oauth_consumers_service_name on is_oauth_consumers(service_name);

--
-- OAUTH_GADGET_URL
--
CREATE sequence is_oauth_gadget_urls_id_seq;
create table is_oauth_gadget_urls (
  id number(18) not null primary key,
  fk_oauth_id varchar(64 BYTE) not null,
  gadget_url varchar(1024 BYTE) not null,
  gadget_url_key varchar(255 BYTE) not null,
  foreign key (fk_oauth_id) references is_oauth_consumers(id) on delete cascade
);
create index is_oauth_gadget_urls_gadget_url_key on is_oauth_gadget_urls(gadget_url_key);

--
-- OAUTH_TOKEN
--
create table is_oauth_tokens (
  fk_oauth_id varchar(64 BYTE) not null,
  "UID" varchar(150 BYTE) not null,
  service_name varchar(255 BYTE) not null,
  request_token varchar(255 BYTE),
  access_token varchar(255 BYTE),
  token_secret varchar(255 BYTE) not null,
  primary key (fk_oauth_id, "UID"),
  foreign key (fk_oauth_id) references is_oauth_consumers(id) on delete cascade
);

--
-- OAUTH_CERTIFICATE
--
create table is_oauth_certificate (
  consumer_key varchar(255 BYTE) not null primary key,
  private_key clob,
  certificate clob
);