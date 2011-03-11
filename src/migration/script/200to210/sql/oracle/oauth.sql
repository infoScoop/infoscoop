--
-- OAUTH_TOKEN
--
create table ${SCHEMA_NAME}is_oauth_tokens (
  "UID" varchar(150 BYTE) not null,
  gadget_url varchar(1024 BYTE) not null,
  gadget_url_key varchar(255 BYTE) not null,
  service_name varchar(255 BYTE) not null,
  request_token varchar(255 BYTE),
  access_token varchar(255 BYTE),
  token_secret varchar(255 BYTE) not null,
  primary key ("UID", gadget_url_key, service_name)
);

--
-- OAUTH_CONSUMER
--
create sequence ${SCHEMA_NAME}is_oauth_consumers_id_seq;
create table ${SCHEMA_NAME}is_oauth_consumers (
  id number(18) not null primary key,
  gadget_url varchar(1024 BYTE) not null,
  gadget_url_key varchar(255 BYTE) not null,
  service_name varchar(255 BYTE) not null,
  consumer_key varchar(255 BYTE),
  consumer_secret varchar(255 BYTE),
  signature_method varchar(20 BYTE),
  is_upload number(1) default 0 not null
);
--
-- OAUTH_CERTIFICATE
--
create table ${SCHEMA_NAME}is_oauth_certificate (
  consumer_key varchar(255 BYTE) not null primary key,
  private_key clob,
  certificate clob
);
