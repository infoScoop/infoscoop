--
-- PREFERENCE
--
create table IS_PREFERENCES (
  `UID` varchar(150) not null,
  data text not null,
  primary key (`UID`)
) ENGINE=InnoDB;

--
-- TAB
--
create table IS_TABS (
  `UID` varchar(150) not null,
  defaultUid varchar(150),
  id varchar(32) not null,
  name varchar(256),
  `ORDER` int,
  type varchar(128),
  data text,
  widgetLastModified varchar(32),
  disabledDynamicPanel int,
  primary key (`UID`, id)
) ENGINE=InnoDB;

--
-- WIDGET
--

create table IS_WIDGETS (
  id bigint not null auto_increment primary key,
  `UID` varchar(75) not null,
  defaultUid varchar(150),
  tabId varchar(32) not null,
  widgetId varchar(128) not null,
  `COLUMN` int,
  siblingId varchar(256),
  parentId varchar(256),
  menuId varchar(256) not null default '',
  href varchar(1024),
  title varchar(256),
  type varchar(1024),
  isStatic int,
  ignoreHeader int,
  noBorder int,
  createDate bigint not null default 0,
  deleteDate bigint not null default 0,
  constraint is_widgets_unique unique (`UID`, tabid, widgetId, deleteDate)
) ENGINE=InnoDB;

create index is_widgets_tabId on IS_WIDGETS(tabId);
create index is_widgets_parentId on IS_WIDGETS(parentId);
create index is_widgets_deleteDate on IS_WIDGETS(deleteDate);
create index is_widgets_type on IS_WIDGETS(type);

--
-- USERPREFS
--

create table IS_USERPREFS (
	fk_widget_id bigint not null,
	name varchar(255) not null,
	value varchar(4000),
	long_value text,
	constraint is_userprefs_uq unique (fk_widget_id,name),
	foreign key (fk_widget_id) references IS_WIDGETS(id) on delete cascade
) ENGINE=InnoDB;

create index is_userprefs_fk_widget_id on IS_USERPREFS(fk_widget_id);
create index is_userprefs_name on IS_USERPREFS(name);
create index is_userprefs_value on IS_USERPREFS(value);

--
-- CACHE
--
create table IS_CACHES (
  id varchar(64) not null primary key,
  `UID` varchar(150) not null,
  url varchar(1024) not null,
  url_key varchar(256) not null,
  timestamp timestamp not null,
  headers text not null,
  body mediumtext not null
) ENGINE=MyISAM;

create index is_caches_uid on IS_CACHES(`UID`);
create index is_caches_url on IS_CACHES(url_key);

create table IS_RSSCACHES (
  `UID` varchar(75) not null,
  url_key varchar(128) not null,
  pageNum int not null,
  rss mediumblob,
  primary key (`UID`, url_key, pageNum)
) ENGINE=MyISAM;

create table IS_MENUCACHES (
  `UID` varchar(75) not null,
  url_key varchar(128) not null,
  menuIds blob,
  primary key (`UID`, url_key)
) ENGINE=MyISAM;

--
-- LOGS
--
create table IS_LOGS (
  id bigint not null auto_increment primary key,
  `UID` varchar (150) not null,
  type integer not null,
  url varchar (1024) not null,
  url_key varchar (256)  not null,
  rssurl varchar (1024) not null,
  rssurl_key varchar (256)  not null,
  `DATE` varchar (24) not null
) ENGINE=InnoDB;

create index is_logs_uid on IS_LOGS(`UID`);
create index is_logs_type on IS_LOGS(type);
create index is_logs_url on IS_LOGS(url_key);
create index is_logs_rssurl on IS_LOGS(rssurl_key);
create index is_logs_date on IS_LOGS(`DATE`);

--
-- WIDGETCONF
--
create table IS_WIDGETCONFS (
  type varchar(50) CHARACTER SET latin1 not null primary key,
  data mediumtext not null
) ENGINE=InnoDB;

--
-- KEYWORDLOG
--
create table IS_KEYWORDS (
  id bigint not null auto_increment primary key,
  `UID` varchar (150) not null,
  type integer not null,
  keyword varchar(500) not null,
  `DATE` varchar (24) not null
) ENGINE=InnoDB;

create index is_keywords_uid on IS_KEYWORDS(`UID`);
create index is_keywords_type on IS_KEYWORDS(type);
create index is_keywords_keyword on IS_KEYWORDS(keyword);
create index is_keywords_date on IS_KEYWORDS(`DATE`);

--
-- siteAggregationMenu
--
create table IS_MENUS (
  type varchar(150) CHARACTER SET latin1 null primary key,
  data mediumtext not null
) ENGINE=InnoDB;

--
-- siteAggregationMenu_temp
--
create table IS_MENUS_TEMP (
  type varchar(150) CHARACTER SET latin1 not null,
  siteTopId varchar(150) CHARACTER SET latin1 not null,
  data mediumtext not null,
  workingUid varchar(150),
  lastmodified timestamp not null,
  primary key (type, siteTopId)
) ENGINE=InnoDB;

create index is_menus_temp_lastmodified on IS_MENUS_TEMP(lastmodified);

--
-- searchEngine
--
create table IS_SEARCHENGINES (
  temp integer not null,
  data text not null
) ENGINE=InnoDB;

--
-- gadget
--
create table IS_GADGETS (
  id bigint not null auto_increment primary key,
  type varchar(50) CHARACTER SET latin1 not null,
  path varchar(512) CHARACTER SET latin1 not null,
  name varchar(255) CHARACTER SET latin1 not null,
  data mediumblob,
  lastmodified timestamp,
  constraint is_gadgets_uq unique (type,path,name)
) ENGINE=InnoDB;

create index is_gadgets_type on IS_GADGETS(type);
create index is_gadgets_path on IS_GADGETS(path);
create index is_gadgets_name on IS_GADGETS(name);

--
-- gadget_icons
--
create table IS_GADGET_ICONS (
  type varchar(256) CHARACTER SET latin1 not null primary key,
  url varchar(1024) not null
) ENGINE=InnoDB;

--
-- properties
--
create table IS_PROPERTIES (
  id varchar(256) CHARACTER SET latin1 not null primary key,
  category varchar(128),
  advanced integer not null,
  value varchar(1024),
  datatype varchar(128),
  enumValue varchar(1024),
  required integer not null,
  regex varchar(1024),
  regexMsg varchar(1024)
) ENGINE=InnoDB;
create index is_properties_advanced on IS_PROPERTIES(advanced);

--
-- proxyConf
--
create table IS_PROXYCONFS (
  temp integer not null,
  data mediumtext not null,
  lastmodified timestamp
) ENGINE=InnoDB;

--
-- i18n
--
create table IS_I18N (
  type varchar(32) CHARACTER SET latin1 not null,
  id varchar(256) CHARACTER SET latin1 not null,
  country varchar(5) CHARACTER SET latin1 not null,
  lang varchar(5) CHARACTER SET latin1 not null,
  message varchar(2048) not null,
  primary key (type, id, country, lang)
) ENGINE=InnoDB;

--
-- i18nLastmodified
--
create table IS_I18NLASTMODIFIED (
  type varchar(32) CHARACTER SET latin1 not null primary key,
  lastmodified timestamp
) ENGINE=InnoDB;

--
-- i18nLocale
--
create table IS_I18NLOCALES (
  id bigint not null auto_increment primary key,
  type varchar(32) CHARACTER SET latin1 not null,
  country varchar(5) CHARACTER SET latin1 not null,
  lang varchar(5) CHARACTER SET latin1 not null,
  constraint is_locales_unique unique (type, country, lang)
) ENGINE=InnoDB;

--
-- tabLayout
--
create table IS_TABLAYOUTS (
  tabId varchar(50) CHARACTER SET latin1 not null,
  roleOrder integer not null,
  role text not null,
  rolename varchar(256) not null,
  principalType varchar(50) default 'OrganizationPrincipal',
  defaultUid varchar (150),
  widgets text not null,
  layout text,
  widgetsLastmodified varchar(24),
  tabNumber integer,
  deleteFlag integer not null default 0,
  temp bit not null default 0,
  workingUid varchar (150),
  primary key (tabId, roleOrder, temp)
) ENGINE=InnoDB;

--
-- portalLayout
--
create table IS_PORTALLAYOUTS (
  name varchar(50) CHARACTER SET latin1 not null,
  layout text not null,
  primary key (name)
) ENGINE=InnoDB;

--
-- adminRole
--
create table IS_ADMINROLES (
  id bigint not null auto_increment primary key,
  roleid varchar(255) not null,
  name varchar(256) NOT NULL,
  permission varchar(256) NOT NULL,
  allowdelete int default 1 NOT NULL,
  constraint is_adminRoles_unique unique (roleid)
) ENGINE=InnoDB;

--
-- portalAdmins
--
create table IS_PORTALADMINS (
  id bigint not null auto_increment primary key,
  `UID` varchar(150) not null,
  ROLEID VARCHAR (255),
  FOREIGN KEY (ROLEID) REFERENCES IS_ADMINROLES (ROLEID) ON DELETE SET NULL,
  constraint is_portaladmins_uq unique (`UID`)
) ENGINE=InnoDB;

--
-- session
--
create table IS_SESSIONS (
  `UID` varchar(150) not null,
  sessionId varchar(256) not null,
  LOGINDATETIME TIMESTAMP,
  primary key (`UID`)
) ENGINE=InnoDB;

create index is_sessions_sessionId on IS_SESSIONS(sessionId);
create index is_sessions_loginDateTime on IS_SESSIONS(loginDateTime);

--
-- forbiddenURLs
--

create table IS_FORBIDDENURLS (
  id bigint not null auto_increment primary key,
  url varchar(1024) not null
) ENGINE=InnoDB;

--
-- authCredential
--
create table IS_AUTHCREDENTIALS (
  id bigint not null auto_increment primary key,
  `UID` varchar(300) not null,
  sysNum integer default 0 not null,
  authType varchar(16) not null,
  authDomain varchar(64),
  authUid varchar(300) not null,
  authPasswd varchar(512)
) ENGINE=InnoDB;

create index is_authCredentials_uid on IS_AUTHCREDENTIALS(`UID`);

--
-- holidays
--
create table IS_HOLIDAYS (
  country varchar(5) CHARACTER SET latin1 not null,
  lang varchar(5) CHARACTER SET latin1 not null,
  data mediumtext,
  updated_at timestamp,
  primary key (country, lang)
) ENGINE=InnoDB;

--
-- accesslog
--
create table IS_ACCESSLOGS (
  id bigint not null auto_increment primary key,
  `UID` varchar(150) not null,
  `DATE` varchar(8) not null,
  constraint is_accesslogs_uq unique (`UID`, `DATE`)
) ENGINE=InnoDB;

--
-- messages
--

create table IS_MESSAGES (
  id bigint not null auto_increment primary key,
  `FROM` varchar(150) not null,
  displayFrom varchar(150),
  `TO` varchar(150),
  toJSON text,
  body varchar(2048),
  posted_time TIMESTAMP not null,
  type varchar(10) not null,
  `OPTION` text
) ENGINE=InnoDB;
create index is_messages_from on IS_MESSAGES(`FROM`);
create index is_messages_to on IS_MESSAGES(`TO`);
create index is_messages_posted_time on IS_MESSAGES(posted_time);
create index is_messages_type on IS_MESSAGES(type);

--
-- system messages
--
create table IS_SYSTEMMESSAGES (
  id bigint not null auto_increment primary key,
  `TO` varchar(150) not null,
  body varchar(2048),
  resourceId varchar(512),
  replaceValues varchar(2048),
  isRead int default 0
) ENGINE=InnoDB;
create index is_systemmessages_to on IS_SYSTEMMESSAGES(`TO`);
create index is_systemmessages on IS_SYSTEMMESSAGES(isread);

--
-- account
--
create table IS_ACCOUNTS (
  uid varchar(150) not null primary key,
  name varchar(255),
  password varchar(255)
) ENGINE=InnoDB;

--
-- OAUTH_CONSUMER
--
create table IS_OAUTH_CONSUMERS (
  id varchar(64) not null primary key,
  service_name varchar(255) not null,
  consumer_key varchar(255),
  consumer_secret varchar(255),
  signature_method varchar(20),
  description text
) ENGINE=InnoDB;
create index is_oauth_service_name on IS_OAUTH_CONSUMERS(service_name);

--
-- OAUTH_GADGET_URL
--
create table IS_OAUTH_GADGET_URLS (
  id bigint not null auto_increment primary key,
  fk_oauth_id varchar(64) not null,
  gadget_url varchar(1024) not null,
  gadget_url_key varchar(255) not null,
  foreign key (fk_oauth_id) references IS_OAUTH_CONSUMERS(id) on delete cascade
) ENGINE=InnoDB;
create index is_oauth_gadget_url_key on IS_OAUTH_GADGET_URLS(gadget_url_key);

--
-- OAUTH_TOKEN
--
create table IS_OAUTH_TOKENS (
  fk_oauth_id varchar(64) not null,
  `UID` varchar(150) not null,
  request_token varchar(255),
  access_token varchar(255),
  token_secret varchar(255) not null,
  primary key (fk_oauth_id,`UID`),
  foreign key (fk_oauth_id) references IS_OAUTH_CONSUMERS(id) on delete cascade
) ENGINE=InnoDB;

--
-- OAUTH2_TOKEN
--
create table IS_OAUTH2_TOKENS (
  fk_oauth_id varchar(64) not null,
  `UID` varchar(150) not null,
  token_type varchar(16),
  auth_code varchar(255),
  access_token varchar(255),
  refresh_token varchar(255),
  validity_period_utc bigint,
  primary key (fk_oauth_id,`UID`),
  foreign key (fk_oauth_id) references IS_OAUTH_CONSUMERS(id) on delete cascade
) ENGINE=InnoDB;

--
-- OAUTH_CERTIFICATE
--
create table IS_OAUTH_CERTIFICATE (
  consumer_key varchar(255) not null primary key,
  private_key text,
  certificate text
) ENGINE=InnoDB;