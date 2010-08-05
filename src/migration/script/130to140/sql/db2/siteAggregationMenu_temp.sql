create table ${SCHEMA_NAME}.is_menus_temp (
  type varchar(150) not null,
  siteTopId varchar(150) not null,
  data clob not null not logged,
  workingUid varchar(150),
  lastmodified timestamp not null,
  primary key (type, siteTopId)
) compress yes;

create index is_menus_temp_lastmodified on ${SCHEMA_NAME}.is_menus_temp(lastmodified);
