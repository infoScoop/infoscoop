alter table ${SCHEMA_NAME}IS_PROPERTIES rename to IS_PROPERTIES@BACKUP_TABLE_SUFFIX@;

drop index is_properties_advanced on ${SCHEMA_NAME}IS_PROPERTIES@BACKUP_TABLE_SUFFIX@;

create table ${SCHEMA_NAME}IS_PROPERTIES (
  id varchar(256) CHARACTER SET latin1 not null primary key,
  category varchar(128),
  advanced integer not null,
  value varchar(1024),
  datatype varchar(128),
  enumValue varchar(1024),
  required integer not null,
  regex varchar(1024),
  regexMsg varchar(1024)
) ENGINE=InnoDB;

create index is_properties_advanced on ${SCHEMA_NAME}IS_PROPERTIES(advanced);