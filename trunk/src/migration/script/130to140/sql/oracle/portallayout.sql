alter table ${SCHEMA_NAME}.portalLayout rename to portalLayout@BACKUP_TABLE_SUFFIX@;

create table portalLayout (
  name varchar(50 BYTE ) not null,
  layout clob,
  primary key (name)
);
