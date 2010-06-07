alter table ${SCHEMA_NAME}is_searchEngines rename to is_searchEngines${BACKUP_TABLE_SUFFIX};

create table ${SCHEMA_NAME}is_searchEngines (
  temp integer not null,
  data clob not null
);