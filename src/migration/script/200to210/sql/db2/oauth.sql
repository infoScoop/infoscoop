--
-- OAUTH_TOKEN
--
create table ${SCHEMA_NAME}is_oauth_tokens (
  uid varchar(150) not null,
  gadget_url varchar(1024) not null,
  gadget_url_key varchar(255) not null,
  service_name varchar(255) not null,
  request_token varchar(255),
  access_token varchar(255),
  token_secret varchar(255) not null,
  primary key (uid, gadget_url_key, service_name)
) compress yes;

--
-- OAUTH_CONSUMER
--
create table ${SCHEMA_NAME}is_oauth_consumers (
  id integer not null generated always as identity (start with 1, increment by 1, no cache) primary key,
  gadget_url varchar(1024) not null,
  gadget_url_key varchar(255) not null,
  service_name varchar(255) not null,
  consumer_key varchar(255),
  consumer_secret varchar(255),
  signature_method varchar(20),
  is_upload int not null default 0
) compress yes;

--
-- OAUTH_CERTIFICATE
--
create table ${SCHEMA_NAME}is_oauth_certificate (
  consumer_key varchar(255) not null primary key,
  private_key clob,
  certificate clob
) compress yes;