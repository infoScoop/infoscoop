alter table ${SCHEMA_NAME}.IS_PROXYCONFS rename to IS_PROXYCONFS@BACKUP_TABLE_SUFFIX@;

create table ${SCHEMA_NAME}.IS_PROXYCONFS (
  temp integer not null,
  data text not null,
  lastmodified timestamp
) ENGINE=InnoDB;