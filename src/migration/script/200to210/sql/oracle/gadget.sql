alter table ${SCHEMA_NAME}is_gadgets rename to is_gadgets${BACKUP_TABLE_SUFFIX};
alter table ${SCHEMA_NAME}is_gadgets${BACKUP_TABLE_SUFFIX} rename constraint is_gadgets_unique to is_gadgets_unique${BACKUP_TABLE_SUFFIX};
ALTER INDEX ${SCHEMA_NAME}IS_GADGETS_UNIQUE RENAME TO IS_GADGETS_UNIQUE${BACKUP_TABLE_SUFFIX};
ALTER INDEX ${SCHEMA_NAME}is_gadgets_type RENAME TO is_gadgets_type${BACKUP_TABLE_SUFFIX};
ALTER INDEX ${SCHEMA_NAME}is_gadgets_path RENAME TO is_gadgets_path${BACKUP_TABLE_SUFFIX};
ALTER INDEX ${SCHEMA_NAME}is_gadgets_name RENAME TO is_gadgets_name${BACKUP_TABLE_SUFFIX};

create table ${SCHEMA_NAME}is_gadgets (
  id number(18) not null primary key,
  type varchar(50 BYTE ) not null,
  path varchar(512 BYTE) not null,
  name varchar(255 BYTE),
  data blob,
  lastmodified timestamp,
  constraint is_gadgets_unique unique (type,path,name)
);

create index ${SCHEMA_NAME}is_gadgets_type on ${SCHEMA_NAME}is_gadgets(type);
create index ${SCHEMA_NAME}is_gadgets_path on ${SCHEMA_NAME}is_gadgets(path);
create index ${SCHEMA_NAME}is_gadgets_name on ${SCHEMA_NAME}is_gadgets(name);

create sequence ${SCHEMA_NAME}is_gadgets_id_seq;

insert into ${SCHEMA_NAME}is_gadgets(id,type, path, name, data, lastmodified)
  select ${SCHEMA_NAME}is_gadgets_id_seq.NEXTVAL - 1,type, path, name, data, lastmodified
  from ${SCHEMA_NAME}is_gadgets${BACKUP_TABLE_SUFFIX}
  where type not in('sticky','alarm','calc','blogparts','worldclock','todoList','schedule','groupschedule');
