alter table ${SCHEMA_NAME}.widgetconf rename to widgetconf@BACKUP_TABLE_SUFFIX@;

create table ${SCHEMA_NAME}.widgetconf (
  type varchar(50 BYTE) not null primary key,
  data clob not null
);

insert into ${SCHEMA_NAME}.widgetconf
	select
		*
	from
		${SCHEMA_NAME}.widgetconf@BACKUP_TABLE_SUFFIX@
	where
		type not in (
			'alarm',
			'calculator',
			'stickey',
			'todolist',
			'worldclock'
		);