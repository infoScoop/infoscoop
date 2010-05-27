drop sequence ${SCHEMA_NAME}is_gadgets_id_seq;

alter table ${SCHEMA_NAME}is_gadgets rename to is_gadgets${BACKUP_TABLE_SUFFIX};

drop index is_gadgets_type on ${SCHEMA_NAME}is_gadgets${BACKUP_TABLE_SUFFIX};
drop index is_gadgets_path on ${SCHEMA_NAME}is_gadgets${BACKUP_TABLE_SUFFIX};
drop index is_gadgets_name on ${SCHEMA_NAME}is_gadgets${BACKUP_TABLE_SUFFIX};

create sequence ${SCHEMA_NAME}is_gadgets_id_seq;

create table ${SCHEMA_NAME}is_gadgets (
  id number(18) not null primary key,
  type varchar(50 BYTE ) not null,
  path varchar(512 BYTE) not null,
  name varchar(255 BYTE),
  data blob,
  lastmodified timestamp,
  constraint is_gadgets_unique unique (type,path,name)
);

create index is_gadgets_type on ${SCHEMA_NAME}is_gadgets(type);
create index is_gadgets_path on ${SCHEMA_NAME}is_gadgets(path);
create index is_gadgets_name on ${SCHEMA_NAME}is_gadgets(name);

insert into ${SCHEMA_NAME}is_gadgets(type, path, name, data, lastmodified)
  select type, path, name, data, lastmodified
  from ${SCHEMA_NAME}is_gadgets${BACKUP_TABLE_SUFFIX}
  where type not in('sticky','alarm','calc','blogparts','worldclock','todoList');