--
-- PREFERENCE
--
create table is_preferences (
  uid varchar(150) not null,
  data xml not null,
  primary key (uid)
) compress yes;

--
-- TAB
--
create table is_tabs (
  uid varchar(150) not null,
  defaultUid varchar(150),
  id varchar(32) not null,
  name varchar(256),
  order int,
  type varchar(128),
  data clob,
  widgetLastModified varchar(32),
  disabledDynamicPanel int,
  primary key (uid, id)
) compress yes;

--
-- WIDGET
--
create table is_widgets (
  id integer not null generated always as identity (start with 1, increment by 1, no cache) primary key,
  uid varchar(150) not null,
  defaultUid varchar(150),
  tabId varchar(32) not null,
  widgetId varchar(256) not null,
  column int,
  siblingId varchar(256),
  parentId varchar(256),
  menuId varchar(256) default '' not null,
  href varchar(1024),
  title varchar(256),
  type varchar(1024),
  isStatic int,
  ignoreHeader int,
  noBorder int,
  createDate bigint default 0 not null,
  deleteDate bigint default 0 not null,
  constraint is_widgets_unique unique (uid, tabid, widgetId, deleteDate)
) compress yes;

create index is_widgets_tabId on is_widgets(tabId);
create index is_widgets_parentId on is_widgets(parentId);
create index is_widgets_deleteDate on is_widgets(deleteDate);
create index is_widgets_type on is_widgets(type);
create index is_widgets_menuId on is_widgets(menuId);
create index is_widgets_createDate on is_widgets(createDate);
create index is_widgets_isStatic on is_widgets(isStatic);

--
-- USERPREFS
--

create table is_userprefs (
	fk_widget_id integer not null,
	name varchar(255) not null,
	value varchar(4000),
	long_value clob,
	constraint is_userprefs_uq unique (fk_widget_id,name),
	foreign key (fk_widget_id) references is_widgets(id) on delete cascade
) compress yes;

create index is_userprefs_fk_widget_id on is_userprefs(fk_widget_id);
create index is_userprefs_name on is_userprefs(name);
create index is_userprefs_value on is_userprefs(value);

--
-- CACHE
--
-- drop table cache;
create table is_caches (
  id varchar(64) not null primary key,
  uid varchar(150) not null,
  url varchar(1024) not null,
  url_key varchar(256) not null,
  timestamp timestamp not null,
  headers xml not null,
  body clob(10M) not null not logged
) compress yes;
--db2 alter table cache alter column uid  set data type varchar(150); 1.1.1to1.2.0
--db2 alter table cache alter column body set data type clob(10M); 1.2.0to1.2.1

create index is_caches_uid on is_caches(uid);
create index is_caches_url on is_caches(url_key);

create table is_rsscaches (
  uid varchar(150) not null,
  url_key varchar(256) not null,
  pageNum int not null,
  rss blob(10M) not logged,
  primary key (uid, url_key, pageNum)
) compress yes;
--db2 alter table rsscache alter column rss set data type blob(10M); 1.2.0to1.2.1

create table is_menucaches (
  uid varchar(150) not null,
  url_key varchar(256) not null,
  menuIds blob(10M) not logged,
  primary key (uid, url_key)
) compress yes;

--
-- LOGS
--
create table is_logs (
  id bigint not null generated always as identity (start with 1, increment by 1, no cache) primary key,
  uid varchar (150) not null,
  type integer not null,
  url varchar (1024) not null,
  url_key varchar (256)  not null,
  rssurl varchar (1024) not null,
  rssurl_key varchar (256)  not null,
  date varchar (24) not null
) compress yes;

create index is_logs_uid on is_logs(uid);
create index is_logs_type on is_logs(type);
create index is_logs_url on is_logs(url_key);
create index is_logs_rssurl on is_logs(rssurl_key);
create index is_logs_date on is_logs(date);

--
-- WIDGETCONF
--
create table is_widgetconfs (
  type varchar(50) not null primary key,
  data xml not null
) compress yes;

--
-- KEYWORDLOG
--
create table is_keywords (
  id bigint not null generated always as identity (start with 1, increment by 1, no cache) primary key,
  uid varchar (150) not null,
  type integer not null,
  keyword varchar (500)  not null,
  date varchar (24) not null
) compress yes;

create index is_keywords_uid on is_keywords(uid);
create index is_keywords_type on is_keywords(type);
create index is_keywords_keyword on is_keywords(keyword);
create index is_keywords_date on is_keywords(date);

--
-- siteAggregationMenu
--
create table is_menus (
  type varchar(150) not null primary key,
  data xml not null
) compress yes;
-- db2 alter table siteAggregationMenu add workingUid varchar(150); 1.2.0 to 1.2.1

--
-- siteAggregationMenu_temp
--
create table is_menus_temp (
  type varchar(150) not null,
  siteTopId varchar(150) not null,
  data clob not null not logged,
  workingUid varchar(150),
  lastmodified timestamp not null,
  primary key (type, siteTopId)
) compress yes;

create index is_menus_temp_lastmodified on is_menus_temp(lastmodified);

--
-- searchEngine
--
create table is_searchEngines (
  temp integer not null,
  data xml not null
) compress yes;

--
-- gadget
--
create table is_gadgets (
  id bigint not null generated always as identity (start with 1, increment by 1, no cache) primary key,
  type varchar(50) not null,
  path varchar(512) not null,
  name varchar(255) not null,
  data blob,
  lastmodified timestamp,
  constraint is_gadgets_unique unique (type,path,name)
) compress yes;

create index is_gadgets_type on is_gadgets(type);
create index is_gadgets_path on is_gadgets(path);
create index is_gadgets_name on is_gadgets(name);

--
-- gadget_icons
--
create table is_gadget_icons (
  type varchar(512) not null primary key,
  url varchar(1024) not null
) compress yes;

--
-- properties
--
create table is_properties (
  id varchar(512) not null primary key,
  category varchar(128) not null,
  advanced integer not null,
  value varchar(1024),
  datatype varchar(128) not null,
  enumValue varchar(1024),
  required integer not null,
  regex varchar(1024),
  regexMsg varchar(1024)
) compress yes;
create index is_properties_advanced on is_properties(advanced);

--
-- proxyConf
--
create table is_proxyConfs (
  temp integer not null,
  data xml not null,
  lastmodified timestamp
) compress yes;

--
-- i18n
--
create table is_i18n (
  type varchar(32) not null,
  id varchar(512) not null,
  country varchar(5) not null,
  lang varchar(5) not null,
  message varchar(2048) not null,
  primary key (type, id, country, lang)
) compress yes;
-- db2 ALTER TABLE i18n DROP COLUMN number; 1.2.0 to 1.2.1

--
-- i18nLastmodified
--
create table is_i18nLastmodified (
  type varchar(32) not null primary key,
  lastmodified timestamp
) compress yes;

--
-- i18nLocale
--
create table is_i18nLocales (
  id bigint not null generated always as identity (start with 1, increment by 1, no cache) primary key,
  type varchar(32) not null,
  country varchar(5) not null,
  lang varchar(5) not null,
  constraint is_locales_unique unique (type, country, lang)
) compress yes;

--
-- tabLayout
--
create table is_tabLayouts (
  tabId varchar(50) not null,
  roleOrder integer not null,
  role clob not null,
  rolename varchar(256) not null,
  principalType varchar(50) default 'OrganizationPrincipal' not null,
  defaultUid varchar (150),
  widgets xml not null,
  layout clob not null,
  widgetsLastmodified varchar(24),
  tabNumber integer,
  deleteFlag integer default 0 not null,
  temp integer default 0 not null,
  workingUid varchar (150),
  primary key (tabId, roleOrder, temp)
) compress yes;
--db2 alter table tablayout alter column defaultUid set data type varchar(150); 1.1.1to1.2.0

--
-- portalLayout
--
create table is_portalLayouts (
  name varchar(50) not null,
  layout clob not null,
  primary key (name)
) compress yes;

--
-- adminRole
--
create table is_adminRoles (
  id bigint not null generated always as identity (start with 1, increment by 1, no cache) primary key,
  roleid varchar(255) not null,
  name VARCHAR(256) NOT NULL,
  permission VARCHAR(256) NOT NULL,
  allowdelete int default 1 NOT NULL,
  constraint is_adminRoles_uq unique (roleid)
) compress yes;

--
-- portalAdmins
--
CREATE TABLE IS_PORTALADMINS (
  id bigint not null generated always as identity (start with 1, increment by 1, no cache) primary key,
  UID VARCHAR (150) NOT NULL,
  ROLEID VARCHAR (255),
  FOREIGN KEY (ROLEID) REFERENCES IS_ADMINROLES (ROLEID) ON DELETE SET NULL,
  constraint IS_PORTALADMINS_uq unique (UID)
) compress yes;

--
-- session
--
create table is_sessions (
  uid varchar(150) not null,
  sessionId varchar(256) not null,
  LOGINDATETIME TIMESTAMP,
  primary key (uid)
) compress yes;

create index is_sessions_sessionId on is_sessions(sessionId);
create index is_sessions_loginDateTime on is_sessions(loginDateTime);

--
-- forbiddenURLs
--

create table is_forbiddenURLs (
	id bigint not null generated always as identity (start with 1, increment by 1, no cache) primary key,
	url varchar(1024) not null
) compress yes;

--
-- authCredential
--
create table is_authCredentials (
  id bigint not null generated always as identity (start with 1, increment by 1, no cache) primary key,
  uid varchar(300) not null,
  sysNum integer default 0 not null,
  authType varchar(16) not null,
  authDomain varchar(64),
  authUid varchar(300) not null,
  authPasswd varchar(512) not null
) compress yes;

create index is_authCredentials_uid on is_authCredentials(uid);

--
-- holidays
--
create table is_holidays (
  country varchar(5) not null,
  lang varchar(5) not null,
  data clob(10M),
  updated_at timestamp,
  primary key (country, lang)
) compress yes;

--
-- accesslog
--
create table is_accesslogs (
  id bigint not null generated always as identity (start with 1, increment by 1, no cache) primary key,
  uid varchar(150) not null,
  date varchar(8) not null,
  constraint is_accesslogs_uq unique (uid, date)
) compress yes;

--
-- messages
--
create table is_messages (
  id bigint not null generated always as identity (start with 1, increment by 1, no cache) primary key,
  from varchar(150) not null,
  displayFrom varchar(150),
  to varchar(150),
  toJSON varchar(4096),
  body varchar(2048),
  posted_time TIMESTAMP not null,
  type varchar(10) not null,
  option varchar(4096)
) compress yes;
create index is_messages_from on is_messages(from);
create index is_messages_to on is_messages(to);
create index is_messages_posted_time on is_messages(posted_time);
create index is_messages_type on is_messages(type);

--
-- system messages
--
create table is_systemmessages (
  id bigint not null generated always as identity (start with 1, increment by 1, no cache) primary key,
  to varchar(150),
  body varchar(2048),
  resourceId varchar(512),
  replaceValues varchar(2048),
  isRead int default 0
) compress yes;
create index is_systemmessages_to on is_systemmessages(to);
create index is_systemmessages on is_systemmessages(isread);

--
-- account
--
create table is_accounts (
  uid varchar(150) not null primary key,
  name varchar(255),
  password varchar(255)
) compress yes;

--
-- OAUTH_CONSUMER
--
create table is_oauth_consumers (
  id varchar(64) not null primary key,
  service_name varchar(255) not null,
  consumer_key varchar(255),
  consumer_secret varchar(255),
  signature_method varchar(20),
  is_upload int not null default 0
) compress yes;
create index is_oauth_consumers_service_name on is_oauth_consumers(service_name);

--
-- OAUTH_GADGET_URL
--
create table is_oauth_gadget_urls (
  id bigint not null generated always as identity (start with 1, increment by 1, no cache) primary key,
  fk_oauth_id varchar(64) not null,
  gadget_url varchar(1024) not null,
  gadget_url_key varchar(255) not null,
  foreign key (fk_oauth_id) references is_oauth_consumers(id) on delete cascade
) compress yes;
create index is_oauth_gadget_urls_gadget_url_key on is_oauth_gadget_urls(gadget_url_key);

--
-- OAUTH_TOKEN
--
create table is_oauth_tokens (
  fk_oauth_id varchar(64) not null,
  uid varchar(150) not null,
  request_token varchar(255),
  access_token varchar(255),
  token_secret varchar(255) not null,
  primary key (fk_oauth_id, uid),
  foreign key (fk_oauth_id) references is_oauth_consumers(id) on delete cascade
) compress yes;

--
-- OAUTH_CERTIFICATE
--
create table is_oauth_certificate (
  consumer_key varchar(255) not null primary key,
  private_key clob,
  certificate clob
) compress yes;