create table ${SCHEMA_NAME}.is_userprefs (
	fk_widget_id integer not null,
	name varchar(255) not null,
	value varchar(4000),
	long_value clob,
	constraint is_userprefs_uq unique (fk_widget_id,name),
	foreign key (fk_widget_id) references ${SCHEMA_NAME}.is_widgets(id) on delete cascade
) compress yes;

create index ${SCHEMA_NAME}.is_userprefs_fk_widget_id on ${SCHEMA_NAME}.is_userprefs(fk_widget_id);
create index ${SCHEMA_NAME}.is_userprefs_name on ${SCHEMA_NAME}.is_userprefs(name);
create index ${SCHEMA_NAME}.is_userprefs_value on ${SCHEMA_NAME}.is_userprefs(value);