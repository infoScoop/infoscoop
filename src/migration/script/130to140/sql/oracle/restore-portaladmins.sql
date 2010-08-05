select * from ${SCHEMA_NAME}.portaladmins@BACKUP_TABLE_SUFFIX@;
alter table ${SCHEMA_NAME}.portaladmins@BACKUP_TABLE_SUFFIX@ rename to portaladmins;
drop table ${SCHEMA_NAME}.is_portaladmins;

drop sequence ${SCHEMA_NAME}.is_portalAdmins_id_seq;

drop table ${SCHEMA_NAME}.is_adminroles;

drop sequence ${SCHEMA_NAME}.is_adminRoles_id_seq;