alter table ${SCHEMA_NAME}IS_SEARCHENGINES rename to IS_SEARCHENGINES${BACKUP_TABLE_SUFFIX};

create table ${SCHEMA_NAME}IS_SEARCHENGINES (
  temp integer not null,
  data text not null
) ENGINE=InnoDB;