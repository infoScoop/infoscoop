--
-- OAUTH2_TOKEN
--
create table ${SCHEMA_NAME}is_oauth2_tokens (
  fk_oauth_id varchar(64 BYTE) not null,
  "UID" varchar(150 BYTE) not null,
  token_type varchar(16 BYTE),
  auth_code varchar(255 BYTE), 
  access_token varchar(255 BYTE),
  refresh_token varchar(255 BYTE),
  validity_period_utc number(18),
  primary key (fk_oauth_id, "UID"),
  foreign key (fk_oauth_id) references ${SCHEMA_NAME}is_oauth_consumers(id) on delete cascade
);
