create table ${SCHEMA_NAME}.is_gadget_icons (
  type varchar(512) not null primary key,
  url varchar(1024) not null
) compress yes;
