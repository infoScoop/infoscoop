alter table ${SCHEMA_NAME}IS_WIDGETCONFS rename to IS_WIDGETCONFS${BACKUP_TABLE_SUFFIX};

create table ${SCHEMA_NAME}IS_WIDGETCONFS (
  type varchar(50) CHARACTER SET latin1 not null primary key,
  data mediumtext not null
) ENGINE=InnoDB;

insert into ${SCHEMA_NAME}IS_WIDGETCONFS(type, data)
  select type, data
  from ${SCHEMA_NAME}IS_WIDGETCONFS${BACKUP_TABLE_SUFFIX};