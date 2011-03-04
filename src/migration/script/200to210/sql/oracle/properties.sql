alter table ${SCHEMA_NAME}is_properties rename to is_properties${BACKUP_TABLE_SUFFIX};
ALTER INDEX ${SCHEMA_NAME}is_properties_advanced RENAME TO is_properties_advanced${BACKUP_TABLE_SUFFIX};

create table ${SCHEMA_NAME}is_properties (
  id varchar(512 BYTE ) not null primary key,
  category varchar(128 BYTE ),
  advanced integer not null,
  value varchar(1024 BYTE ),
  datatype varchar(128 BYTE ),
  enumValue varchar(1024 BYTE ),
  required integer not null,
  regex varchar(1024 BYTE ),
  regexMsg varchar(1024 BYTE )
);

create index ${SCHEMA_NAME}is_properties_advanced on ${SCHEMA_NAME}is_properties(advanced);
