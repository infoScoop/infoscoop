create table ${SCHEMA_NAME}.is_messages (
  id bigint not null generated always as identity (start with 1, increment by 1, no cache) primary key,
  from varchar(150) not null,
  displayFrom varchar(150),
  to varchar(150),
  toJSON varchar(4096),
  body varchar(2048),
  posted_time TIMESTAMP not null,
  type varchar(10) not null,
  option varchar(4096)
) compress yes;

create index ${SCHEMA_NAME}.is_messages_from on ${SCHEMA_NAME}.is_messages(from);
create index ${SCHEMA_NAME}.is_messages_to on ${SCHEMA_NAME}.is_messages(to);
create index ${SCHEMA_NAME}.is_messages_posted_time on ${SCHEMA_NAME}.is_messages(posted_time);
create index ${SCHEMA_NAME}.is_messages_type on ${SCHEMA_NAME}.is_messages(type);