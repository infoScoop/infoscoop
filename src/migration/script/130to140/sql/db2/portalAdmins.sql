rename table ${SCHEMA_NAME}.portalAdmins to portalAdmins@BACKUP_TABLE_SUFFIX@;

CREATE TABLE ${SCHEMA_NAME}.IS_PORTALADMINS (
  id bigint not null generated always as identity (start with 1, increment by 1, no cache) primary key,
  UID VARCHAR (150) NOT NULL,
  ROLEID VARCHAR (256),
  FOREIGN KEY (ROLEID) REFERENCES ${SCHEMA_NAME}.IS_ADMINROLES (ROLEID) ON DELETE SET NULL,
  constraint IS_PORTALADMINS_uq unique (UID)
) compress yes;

insert into ${SCHEMA_NAME}.IS_PORTALADMINS (UID)
	select UID from ${SCHEMA_NAME}.portalAdmins@BACKUP_TABLE_SUFFIX@;

update ${SCHEMA_NAME}.IS_PORTALADMINS set roleid='root';
