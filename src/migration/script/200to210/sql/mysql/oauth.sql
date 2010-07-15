--
-- OAUTH_TOKEN
--
create table IS_OAUTH_TOKENS (
  `UID` varchar(150) not null,
  gadget_url varchar(1024) not null,
  gadget_url_key varchar(255) not null,
  service_name varchar(255) not null,
  request_token varchar(255),
  access_token varchar(255),
  token_secret varchar(255) not null,
  primary key (`UID`, gadget_url_key, service_name)
) ENGINE=InnoDB;

--
-- OAUTH_CONSUMER
--
create table IS_OAUTH_CONSUMERS (
  gadget_url varchar(1024) not null,
  gadget_url_key varchar(255) not null,
  service_name varchar(255) not null,
  consumer_key varchar(255),
  consumer_secret varchar(255),
  signature_method varchar(20),
  is_upload int(1) not null default 0,
  primary key (gadget_url_key, service_name)
) ENGINE=InnoDB;

--
-- OAUTH_CERTIFICATE
--
create table IS_OAUTH_CERTIFICATE (
  consumer_key varchar(255) not null primary key,
  private_key text,
  certificate text
) ENGINE=InnoDB;