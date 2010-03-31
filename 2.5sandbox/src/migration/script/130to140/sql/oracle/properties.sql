alter table ${SCHEMA_NAME}.properties rename to properties@BACKUP_TABLE_SUFFIX@;

create table properties (
  id varchar(512 BYTE ) not null primary key,
  category varchar(128 BYTE ) not null,
  advanced integer not null,
  value varchar(1024 BYTE ),
  datatype varchar(128 BYTE ) not null,
  enumValue varchar(1024 BYTE ),
  required integer not null,
  regex varchar(1024 BYTE ),
  regexMsg varchar(1024 BYTE )
);

create index ${SCHEMA_NAME}.properties_advanced on ${SCHEMA_NAME}.properties(advanced);