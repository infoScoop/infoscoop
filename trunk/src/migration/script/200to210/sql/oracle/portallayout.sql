alter table ${SCHEMA_NAME}is_portalLayouts rename to is_portalLayouts${BACKUP_TABLE_SUFFIX};

create table ${SCHEMA_NAME}is_portalLayouts (
  name varchar(50 BYTE ) not null,
  layout clob,
  primary key (name)
);