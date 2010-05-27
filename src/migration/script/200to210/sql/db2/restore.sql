select * from ${SCHEMA_NAME}.${table}@BACKUP_TABLE_SUFFIX@;
drop table ${SCHEMA_NAME}.${table};
rename table ${SCHEMA_NAME}.${table}@BACKUP_TABLE_SUFFIX@ to ${table};