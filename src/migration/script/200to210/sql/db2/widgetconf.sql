rename table ${SCHEMA_NAME}is_widgetconfs to is_widgetconfs${BACKUP_TABLE_SUFFIX};

create table ${SCHEMA_NAME}is_widgetconfs (
  type varchar(50) not null primary key,
  data xml not null
) compress yes;

insert into ${SCHEMA_NAME}is_widgetconfs(type, data)
  select type, data
  from ${SCHEMA_NAME}is_widgetconfs${BACKUP_TABLE_SUFFIX};