rename table ${SCHEMA_NAME}.properties to properties@BACKUP_TABLE_SUFFIX@;

create table ${SCHEMA_NAME}.properties (
  id varchar(512) not null primary key,
  category varchar(128) not null,
  advanced integer not null,
  value varchar(1024),
  datatype varchar(128) not null,
  enumValue varchar(1024),
  required integer not null,
  regex varchar(1024),
  regexMsg varchar(1024)
) compress yes;

create index ${SCHEMA_NAME}.is_properties_advanced on ${SCHEMA_NAME}.properties(advanced);
