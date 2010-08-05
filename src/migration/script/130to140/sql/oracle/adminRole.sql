create table ${SCHEMA_NAME}.is_adminRoles (
  id integer not null primary key,
  roleid varchar(256 BYTE) not null,
  name VARCHAR(256 BYTE) NOT NULL,
  permission VARCHAR(256 BYTE) NOT NULL,
  allowdelete int default 1 NOT NULL,
  constraint is_adminRoles_unique unique (roleid)
);

create sequence is_adminRoles_id_seq;