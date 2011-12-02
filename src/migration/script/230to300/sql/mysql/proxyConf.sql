alter table ${SCHEMA_NAME}IS_PROXYCONFS rename to IS_PROXYCONFS${BACKUP_TABLE_SUFFIX};

create table ${SCHEMA_NAME}IS_PROXYCONFS (
  temp integer not null,
  data mediumtext not null,
  lastmodified timestamp
) ENGINE=InnoDB;

insert into ${SCHEMA_NAME}IS_PROXYCONFS(temp, data, lastmodified)
  select temp, data, lastmodified
  from ${SCHEMA_NAME}IS_PROXYCONFS${BACKUP_TABLE_SUFFIX};