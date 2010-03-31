alter table ${SCHEMA_NAME}."SESSION" rename to session@BACKUP_TABLE_SUFFIX@;

drop index ${SCHEMA_NAME}.session_sessionId;

create table ${SCHEMA_NAME}."SESSION" (
  "UID" varchar(150 BYTE) not null,
  sessionId varchar(256 BYTE) not null,
  LOGINDATETIME TIMESTAMP,
  primary key ("UID")
);

create index ${SCHEMA_NAME}.is_sessions_sessionId on ${SCHEMA_NAME}."SESSION"(sessionId);
create index ${SCHEMA_NAME}.is_sessions_loginDateTime on ${SCHEMA_NAME}."SESSION"(loginDateTime);
