create sequence is_portalAdmins_id_seq;

alter table ${SCHEMA_NAME}.portalAdmins rename to portalAdmins@BACKUP_TABLE_SUFFIX@;

create table is_portalAdmins (
  id integer not null primary key,
  "UID" varchar(150 BYTE ) not null,
  ROLEID varchar(256 BYTE),
  FOREIGN KEY (ROLEID) REFERENCES IS_ADMINROLES (ROLEID) ON DELETE SET NULL,
  constraint IS_PORTALADMINS_unique unique ("UID")
);

insert into ${SCHEMA_NAME}.IS_PORTALADMINS ( id,"UID",ROLEID )
	select
		is_portalAdmins_id_seq.nextval,
		"UID",
		'root'
	from
		${SCHEMA_NAME}.portalAdmins@BACKUP_TABLE_SUFFIX@;
