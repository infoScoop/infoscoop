rename table ${SCHEMA_NAME}is_proxyConfs to is_proxyConfs${BACKUP_TABLE_SUFFIX};

create table ${SCHEMA_NAME}is_proxyConfs (
  temp integer not null,
  data xml not null,
  lastmodified timestamp
) compress yes;

insert into ${SCHEMA_NAME}is_proxyConfs(temp, data, lastmodified)
  select temp, data, lastmodified
  from ${SCHEMA_NAME}is_proxyConfs${BACKUP_TABLE_SUFFIX};