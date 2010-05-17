create table ${SCHEMA_NAME}.is_adminRoles (
  id bigint not null generated always as identity (start with 1, increment by 1, no cache) primary key,
  roleid varchar(256) not null,
  name VARCHAR(256) NOT NULL,
  permission VARCHAR(256) NOT NULL,
  allowdelete int default 1 NOT NULL,
  constraint is_adminRoles_uq unique (roleid)
) compress yes;