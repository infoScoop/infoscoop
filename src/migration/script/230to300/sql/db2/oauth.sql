rename table ${SCHEMA_NAME}IS_OAUTH_CONSUMERS to IS_OAUTH_CONSUMERS${BACKUP_TABLE_SUFFIX};
rename table ${SCHEMA_NAME}IS_OAUTH_TOKENS to IS_OAUTH_TOKENS${BACKUP_TABLE_SUFFIX};

--
-- OAUTH_CONSUMER
--
create table ${SCHEMA_NAME}is_oauth_consumers (
  id varchar(64) not null primary key,
  service_name varchar(255) not null,
  consumer_key varchar(255),
  consumer_secret varchar(255),
  signature_method varchar(20),
  description clob
) compress yes;
create index ${SCHEMA_NAME}is_oauth_consumers_service_name on ${SCHEMA_NAME}is_oauth_consumers(service_name);

--
-- OAUTH_GADGET_URL
--
create table ${SCHEMA_NAME}is_oauth_gadget_urls (
  id bigint not null generated always as identity (start with 1, increment by 1, no cache) primary key,
  fk_oauth_id varchar(64) not null,
  gadget_url varchar(1024) not null,
  gadget_url_key varchar(255) not null,
  foreign key (fk_oauth_id) references ${SCHEMA_NAME}is_oauth_consumers(id) on delete cascade
) compress yes;
create index ${SCHEMA_NAME}is_oauth_gadget_urls_gadget_url_key on ${SCHEMA_NAME}is_oauth_gadget_urls(gadget_url_key);

--
-- OAUTH_TOKEN
--
create table ${SCHEMA_NAME}is_oauth_tokens (
  fk_oauth_id varchar(64) not null,
  uid varchar(150) not null,
  request_token varchar(255),
  access_token varchar(255),
  token_secret varchar(255) not null,
  primary key (fk_oauth_id, uid),
  foreign key (fk_oauth_id) references ${SCHEMA_NAME}is_oauth_consumers(id) on delete cascade
) compress yes;

