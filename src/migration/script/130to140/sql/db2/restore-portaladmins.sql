drop table ${SCHEMA_NAME}.is_adminroles;

select * from ${SCHEMA_NAME}.portaladmins@BACKUP_TABLE_SUFFIX@;
rename table ${SCHEMA_NAME}.portaladmins@BACKUP_TABLE_SUFFIX@ to portaladmins;
drop table ${SCHEMA_NAME}.is_portaladmins;
