create table ${SCHEMA_NAME}.is_menus_temp (
  type varchar(150 BYTE) not null,
  siteTopId varchar(150 BYTE) not null,
  data clob not null,
  workingUid varchar(150 BYTE),
  lastmodified timestamp not null,
  primary key (type, siteTopId)
);

create index is_menus_temp_lastmodified on ${SCHEMA_NAME}.is_menus_temp(lastmodified);