alter table ${SCHEMA_NAME}.IS_GADGETS rename to IS_GADGETS@BACKUP_TABLE_SUFFIX@;

drop index is_gadgets_type on ${SCHEMA_NAME}.IS_GADGETS@BACKUP_TABLE_SUFFIX@;
drop index is_gadgets_path on ${SCHEMA_NAME}.IS_GADGETS@BACKUP_TABLE_SUFFIX@;
drop index is_gadgets_name on ${SCHEMA_NAME}.IS_GADGETS@BACKUP_TABLE_SUFFIX@;

create table ${SCHEMA_NAME}.IS_GADGETS (
  id bigint not null auto_increment primary key,
  type varchar(50) CHARACTER SET latin1 not null,
  path varchar(512) CHARACTER SET latin1 not null,
  name varchar(255) CHARACTER SET latin1 not null,
  data mediumblob,
  lastmodified timestamp,
  constraint is_gadgets_uq unique (type,path,name)
) ENGINE=InnoDB;

create index is_gadgets_type on ${SCHEMA_NAME}.IS_GADGETS(type);
create index is_gadgets_path on ${SCHEMA_NAME}.IS_GADGETS(path);
create index is_gadgets_name on ${SCHEMA_NAME}.IS_GADGETS(name);

insert into ${SCHEMA_NAME}.IS_GADGETS(type, path, name, data, lastmodified)
  select type, path, name, data, lastmodified
  from ${SCHEMA_NAME}.IS_GADGETS@BACKUP_TABLE_SUFFIX@
  where type not in('sticky','alarm','calc','blogparts','worldclock','todoList');