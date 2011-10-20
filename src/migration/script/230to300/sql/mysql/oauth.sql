alter table ${SCHEMA_NAME}IS_OAUTH_CONSUMERS rename to IS_OAUTH_CONSUMERS${BACKUP_TABLE_SUFFIX};
alter table ${SCHEMA_NAME}IS_OAUTH_TOKENS rename to IS_OAUTH_TOKENS${BACKUP_TABLE_SUFFIX};

--
-- OAUTH_CONSUMER
--
create table ${SCHEMA_NAME}IS_OAUTH_CONSUMERS (
  id varchar(64) not null primary key,
  service_name varchar(255) not null,
  consumer_key varchar(255),
  consumer_secret varchar(255),
  signature_method varchar(20),
  description text
) ENGINE=InnoDB;
create index ${SCHEMA_NAME}is_oauth_service_name on ${SCHEMA_NAME}IS_OAUTH_CONSUMERS(service_name);

--
-- OAUTH_GADGET_URL
--
create table ${SCHEMA_NAME}IS_OAUTH_GADGET_URLS (
  id bigint not null auto_increment primary key,
  fk_oauth_id varchar(64) not null,
  gadget_url varchar(1024) not null,
  gadget_url_key varchar(255) not null,
  foreign key (fk_oauth_id) references ${SCHEMA_NAME}IS_OAUTH_CONSUMERS(id) on delete cascade
) ENGINE=InnoDB;
create index ${SCHEMA_NAME}is_oauth_gadget_url_key on ${SCHEMA_NAME}IS_OAUTH_GADGET_URLS(gadget_url_key);

--
-- OAUTH_TOKEN
--
create table ${SCHEMA_NAME}IS_OAUTH_TOKENS (
  fk_oauth_id varchar(64) not null,
  `UID` varchar(150) not null,
  request_token varchar(255),
  access_token varchar(255),
  token_secret varchar(255) not null,
  primary key (fk_oauth_id,`UID`),
  foreign key (fk_oauth_id) references ${SCHEMA_NAME}IS_OAUTH_CONSUMERS(id) on delete cascade
) ENGINE=InnoDB;

