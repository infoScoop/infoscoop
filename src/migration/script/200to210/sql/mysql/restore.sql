select * from ${SCHEMA_NAME}${table}@BACKUP_TABLE_SUFFIX@;
drop table ${SCHEMA_NAME}${table};
alter table ${SCHEMA_NAME}${table}@BACKUP_TABLE_SUFFIX@ rename to ${table};