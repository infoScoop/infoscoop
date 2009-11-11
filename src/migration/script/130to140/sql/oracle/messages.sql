create table ${SCHEMA_NAME}.is_messages (
  id number(18) not null primary key,
  "FROM" varchar(150 BYTE) not null,
  displayFrom varchar(150 BYTE),
  "TO" varchar(150 BYTE),
  toJSON clob,
  body varchar(2048 BYTE),
  posted_time TIMESTAMP not null,
  type varchar(10 BYTE) not null,
  "OPTION" clob
);
create index ${SCHEMA_NAME}.is_messages_from on ${SCHEMA_NAME}.is_messages("FROM");
create index ${SCHEMA_NAME}.is_messages_to on ${SCHEMA_NAME}.is_messages("TO");
create index ${SCHEMA_NAME}.is_messages_posted_time on ${SCHEMA_NAME}.is_messages(posted_time);
create index ${SCHEMA_NAME}.is_messages_type on ${SCHEMA_NAME}.is_messages(type);