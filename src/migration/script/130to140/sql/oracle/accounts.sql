create table ${SCHEMA_NAME}.IS_ACCOUNTS (
  "UID" varchar(150 BYTE ) not null primary key,
  name varchar(255 BYTE ),
  password varchar(255 BYTE )
);