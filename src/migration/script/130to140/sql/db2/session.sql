rename table ${SCHEMA_NAME}.session to session@BACKUP_TABLE_SUFFIX@;

drop index ${SCHEMA_NAME}.session_sessionId;

create table ${SCHEMA_NAME}.session (
  uid varchar(150) not null,
  sessionId varchar(256) not null,
  LOGINDATETIME TIMESTAMP,
  primary key (uid)
) compress yes;

create index ${SCHEMA_NAME}.is_sessions_sessionId on ${SCHEMA_NAME}.session(sessionId);
create index ${SCHEMA_NAME}.is_sessions_loginDateTime on ${SCHEMA_NAME}.session(loginDateTime);
