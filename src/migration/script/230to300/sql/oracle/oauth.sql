alter table ${SCHEMA_NAME}IS_OAUTH_CONSUMERS rename to IS_OAUTH_CONSUMERS${BACKUP_TABLE_SUFFIX};
alter table ${SCHEMA_NAME}IS_OAUTH_TOKENS rename to IS_OAUTH_TOKENS${BACKUP_TABLE_SUFFIX};
rename ${SCHEMA_NAME}is_oauth_consumers_id_seq${BACKUP_TABLE_SUFFIX} to is_oauth_consumers_id_seq;

--
-- OAUTH_CONSUMER
--
create table ${SCHEMA_NAME}is_oauth_consumers (
  id varchar(64 BYTE) not null primary key,
  service_name varchar(255 BYTE) not null,
  consumer_key varchar(255 BYTE),
  consumer_secret varchar(255 BYTE),
  signature_method varchar(20 BYTE),
  description clob
);
create index ${SCHEMA_NAME}is_oauth_consumers_service_name on ${SCHEMA_NAME}is_oauth_consumers(service_name);

--
-- OAUTH_GADGET_URL
--
CREATE sequence ${SCHEMA_NAME}is_oauth_gadget_urls_id_seq increment BY 1 start WITH 1;
create table ${SCHEMA_NAME}is_oauth_gadget_urls (
  id number(18) not null primary key,
  fk_oauth_id varchar(64 BYTE) not null,
  gadget_url varchar(1024 BYTE) not null,
  gadget_url_key varchar(255 BYTE) not null,
  foreign key (fk_oauth_id) references ${SCHEMA_NAME}is_oauth_consumers(id) on delete cascade
);
create index ${SCHEMA_NAME}is_oauth_gadget_urls_gadget_url_key on ${SCHEMA_NAME}is_oauth_gadget_urls(gadget_url_key);

--
-- OAUTH_TOKEN
--
create table ${SCHEMA_NAME}is_oauth_tokens (
  fk_oauth_id varchar(64 BYTE) not null,
  "UID" varchar(150 BYTE) not null,
  service_name varchar(255 BYTE) not null,
  request_token varchar(255 BYTE),
  access_token varchar(255 BYTE),
  token_secret varchar(255 BYTE) not null,
  primary key (fk_oauth_id, "UID"),
  foreign key (fk_oauth_id) references ${SCHEMA_NAME}is_oauth_consumers(id) on delete cascade
);
