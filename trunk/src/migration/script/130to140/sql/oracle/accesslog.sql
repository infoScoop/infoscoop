create table ${SCHEMA_NAME}.is_accesslogs (
  id number(18) not null primary key,
  "UID" varchar(150 BYTE) not null,
  "DATE" varchar(8 BYTE) not null,
  constraint is_accesslogs_uq unique ("UID", "DATE")
);

create sequence ${SCHEMA_NAME}.is_accesslogs_id_seq;