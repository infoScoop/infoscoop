rename table ${SCHEMA_NAME}.portalLayout to portalLayout@BACKUP_TABLE_SUFFIX@;

create table portalLayout (
  name varchar(50) not null,
  layout clob not null,
  primary key (name)
) compress yes;