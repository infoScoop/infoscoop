alter table ${SCHEMA_NAME}.gadget rename to gadget@BACKUP_TABLE_SUFFIX@;

create table ${SCHEMA_NAME}.gadget (
  id number(18) not null primary key,
  type varchar(50 BYTE ) not null,
  path varchar(512 BYTE) not null,
  name varchar(255 BYTE),
  data blob,
  lastmodified timestamp,
  constraint is_gadgets_unique unique (type,path,name)
);

create index ${SCHEMA_NAME}.is_gadgets_type on ${SCHEMA_NAME}.gadget(type);
create index ${SCHEMA_NAME}.is_gadgets_path on ${SCHEMA_NAME}.gadget(path);
create index ${SCHEMA_NAME}.is_gadgets_name on ${SCHEMA_NAME}.gadget(name);

create sequence is_gadgets_id_seq;

insert into ${SCHEMA_NAME}.gadget ( id,type,path,name,data ) 
	(
		select
			is_gadgets_id_seq.nextval as id,
			replace(type,'upload__','') as type,
			'/' as path,
			concat( replace(type,'upload__',''),'.xml') as name,
			data
			from ${SCHEMA_NAME}.gadget@BACKUP_TABLE_SUFFIX@
			where filetype='gadget'
	)