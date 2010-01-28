rename table ${SCHEMA_NAME}.widgetconf to widgetconf@BACKUP_TABLE_SUFFIX@;

create table ${SCHEMA_NAME}.widgetconf (
  type varchar(50) not null primary key,
  data xml not null
) compress yes;

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