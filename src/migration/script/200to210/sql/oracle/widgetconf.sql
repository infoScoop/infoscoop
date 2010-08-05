alter table ${SCHEMA_NAME}is_widgetconfs rename to is_widgetconfs${BACKUP_TABLE_SUFFIX};

create table ${SCHEMA_NAME}is_widgetconfs (
  type varchar(50 BYTE ) not null primary key,
  data clob not null
);

insert into ${SCHEMA_NAME}is_widgetconfs(type, data)
  select type, data
  from ${SCHEMA_NAME}is_widgetconfs${BACKUP_TABLE_SUFFIX};