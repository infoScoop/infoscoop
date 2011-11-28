--
-- OAUTH2_TOKEN
--
create table ${SCHEMA_NAME}is_oauth2_tokens (
  fk_oauth_id varchar(64) not null,
  uid varchar(150) not null,
  token_type varchar(16),
  auth_code varchar(255),  
  access_token varchar(255),
  refresh_token varchar(255),
  validity_period_utc bigint,
  primary key (fk_oauth_id, uid),
  foreign key (fk_oauth_id) references ${SCHEMA_NAME}is_oauth_consumers(id) on delete cascade
) compress yes;
