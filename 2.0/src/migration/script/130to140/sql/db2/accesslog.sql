create table ${SCHEMA_NAME}.is_accesslogs (
  id bigint not null generated always as identity (start with 1, increment by 1, no cache) primary key,
  uid varchar(150) not null,
  date varchar(8) not null,
  constraint is_accesslogs_uq unique (uid, date)
) compress yes;
