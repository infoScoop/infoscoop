rename table ${SCHEMA_NAME}.gadget to gadget@BACKUP_TABLE_SUFFIX@;

create table ${SCHEMA_NAME}.gadget (
  id bigint not null generated always as identity (start with 1, increment by 1, no cache) primary key,
  type varchar(50) not null,
  path varchar(512) not null,
  name varchar(255) not null,
  data blob,
  lastmodified timestamp,
  constraint is_gadgets_unique unique (type,path,name)
) compress yes;

create index ${SCHEMA_NAME}.is_gadgets_type on ${SCHEMA_NAME}.gadget(type);
create index ${SCHEMA_NAME}.is_gadgets_path on ${SCHEMA_NAME}.gadget(path);
create index ${SCHEMA_NAME}.is_gadgets_name on ${SCHEMA_NAME}.gadget(name);

insert into ${SCHEMA_NAME}.gadget ( type,path,name,data ) 
	(
		select
			replace(type,'upload__','') as type,
			'/' as path,
			concat( replace(type,'upload__',''),'.xml') as name,
			data
			from ${SCHEMA_NAME}.gadget@BACKUP_TABLE_SUFFIX@
			where filetype='gadget'
	)