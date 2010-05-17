select * from ${SCHEMA_NAME}.SESSION@BACKUP_TABLE_SUFFIX@;
drop table ${SCHEMA_NAME}."SESSION";
rename table ${SCHEMA_NAME}.SESSION@BACKUP_TABLE_SUFFIX@ to "SESSION";

create index ${SCHEMA_NAME}.session_sessionId on ${SCHEMA_NAME}."SESSION"(sessionId);