rename table ${SCHEMA_NAME}is_searchEngines to is_searchEngines${BACKUP_TABLE_SUFFIX};

create table ${SCHEMA_NAME}is_searchEngines (
  temp integer not null,
  data xml not null
) compress yes;