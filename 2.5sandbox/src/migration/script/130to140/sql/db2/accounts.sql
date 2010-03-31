create table ${SCHEMA_NAME}.is_accounts (
  uid varchar(150) not null primary key,
  name varchar(255),
  password varchar(255)
) compress yes;