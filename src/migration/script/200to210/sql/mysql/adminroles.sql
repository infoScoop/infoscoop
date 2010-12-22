alter table ${SCHEMA_NAME}IS_ADMINROLES rename to IS_ADMINROLES${BACKUP_TABLE_SUFFIX};

create table ${SCHEMA_NAME}IS_ADMINROLES (
  id bigint not null auto_increment primary key,
  roleid varchar(255) not null,
  name varchar(256) NOT NULL,
  permission varchar(256) NOT NULL,
  allowdelete int default 1 NOT NULL,
  constraint is_adminRoles_unique unique (roleid)
) ENGINE=InnoDB;

insert into ${SCHEMA_NAME}is_adminRoles 
  select id, roleid, name, permission, allowdelete
  from ${SCHEMA_NAME}is_adminroles${BACKUP_TABLE_SUFFIX};

UPDATE is_adminroles 
  SET permission='["menu", "menu_tree", "search", "widget", "defaultPanel", "portalLayout", "i18n", "properties", "proxy", "admins", "forbiddenURL", "authentication"]' 
  WHERE roleid='root'