--
-- OAUTH2_TOKEN
--
create table ${SCHEMA_NAME}IS_OAUTH2_TOKENS (
  fk_oauth_id varchar(64) not null,
  `UID` varchar(150) not null,
  token_type varchar(16),
  auth_code varchar(255),
  access_token varchar(255),
  refresh_token varchar(255),
  validity_period_utc bigint,
  primary key (fk_oauth_id,`UID`),
  foreign key (fk_oauth_id) references ${SCHEMA_NAME}IS_OAUTH_CONSUMERS(id) on delete cascade
) ENGINE=InnoDB;
