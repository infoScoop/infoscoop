rename index ${SCHEMA_NAME}is_gadgets_type to is_gadgets_type${BACKUP_TABLE_SUFFIX};
rename index ${SCHEMA_NAME}is_gadgets_path to is_gadgets_path${BACKUP_TABLE_SUFFIX};
rename index ${SCHEMA_NAME}is_gadgets_name to is_gadgets_name${BACKUP_TABLE_SUFFIX};

rename table ${SCHEMA_NAME}is_gadgets to is_gadgets${BACKUP_TABLE_SUFFIX};

create table ${SCHEMA_NAME}is_gadgets (
  id bigint not null generated always as identity (start with 1, increment by 1, no cache) primary key,
  type varchar(50) not null,
  path varchar(512) not null,
  name varchar(255) not null,
  data blob,
  lastmodified timestamp,
  constraint is_gadgets_unique unique (type,path,name)
) compress yes;

create index is_gadgets_type on ${SCHEMA_NAME}is_gadgets(type);
create index is_gadgets_path on ${SCHEMA_NAME}is_gadgets(path);
create index is_gadgets_name on ${SCHEMA_NAME}is_gadgets(name);

insert into ${SCHEMA_NAME}is_gadgets(type, path, name, data, lastmodified)
  select type, path, name, data, lastmodified
  from ${SCHEMA_NAME}is_gadgets${BACKUP_TABLE_SUFFIX}
  where type not in('sticky','alarm','calc','blogparts','worldclock','todoList');
