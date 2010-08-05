create table ${SCHEMA_NAME}.is_systemmessages (
  id bigint not null generated always as identity (start with 1, increment by 1, no cache) primary key,
  to varchar(150),
  body varchar(2048),
  resourceId varchar(512),
  replaceValues varchar(2048),
  isRead int default 0
) compress yes;
create index ${SCHEMA_NAME}.is_systemmessages_to on ${SCHEMA_NAME}.is_systemmessages(to);
create index ${SCHEMA_NAME}.is_systemmessages on ${SCHEMA_NAME}.is_systemmessages(isread);
