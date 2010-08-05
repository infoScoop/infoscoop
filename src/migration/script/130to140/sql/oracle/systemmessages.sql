create table ${SCHEMA_NAME}.IS_SYSTEMMESSAGES (
  id number(18) not null primary key,
  "TO" varchar(150 BYTE) not null,
  body varchar(2048 BYTE),
  resourceId varchar(512 BYTE),
  replaceValues varchar(2048 BYTE),
  isRead number(1) default 0
);
create index ${SCHEMA_NAME}.is_systemmessages_to on ${SCHEMA_NAME}.is_systemmessages("TO");
create index ${SCHEMA_NAME}.is_systemmessages on ${SCHEMA_NAME}.is_systemmessages(isread);