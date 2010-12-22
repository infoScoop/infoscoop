alter table ${SCHEMA_NAME}is_adminRoles drop constraint is_adminRoles_uq;
rename table ${SCHEMA_NAME}is_adminRoles to is_adminRoles${BACKUP_TABLE_SUFFIX};

create table ${SCHEMA_NAME}is_adminRoles (
  id bigint not null generated always as identity (start with 1, increment by 1, no cache) primary key,
  roleid varchar(255) not null,
  name VARCHAR(256) NOT NULL,
  permission VARCHAR(256) NOT NULL,
  allowdelete int default 1 NOT NULL,
  constraint is_adminRoles_uq unique (roleid)
) compress yes;

insert into ${SCHEMA_NAME}is_adminRoles(roleid, name, permission, allowdelete) 
  select roleid, name, permission, allowdelete
  from ${SCHEMA_NAME}is_adminroles${BACKUP_TABLE_SUFFIX};

UPDATE is_adminroles 
  SET permission='["menu", "menu_tree", "search", "widget", "defaultPanel", "portalLayout", "i18n", "properties", "proxy", "admins", "forbiddenURL", "authentication"]' 
  WHERE roleid='root'
