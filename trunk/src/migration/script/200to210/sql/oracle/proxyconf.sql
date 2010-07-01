alter table ${SCHEMA_NAME}is_proxyConfs rename to is_proxyConfs${BACKUP_TABLE_SUFFIX};

create table ${SCHEMA_NAME}is_proxyConfs (
  temp integer not null,
  data clob not null,
  lastmodified timestamp
);

insert into ${SCHEMA_NAME}is_proxyConfs(temp, data, lastmodified)
  select temp, data, lastmodified
  from ${SCHEMA_NAME}is_proxyConfs${BACKUP_TABLE_SUFFIX};