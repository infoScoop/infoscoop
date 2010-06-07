rename table ${SCHEMA_NAME}is_portalLayouts to is_portalLayouts${BACKUP_TABLE_SUFFIX};

create table ${SCHEMA_NAME}is_portalLayouts (
  name varchar(50) not null,
  layout clob not null,
  primary key (name)
) compress yes;