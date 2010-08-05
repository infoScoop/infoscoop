alter table ${SCHEMA_NAME}IS_PORTALLAYOUTS rename to IS_PORTALLAYOUTS${BACKUP_TABLE_SUFFIX};

create table ${SCHEMA_NAME}IS_PORTALLAYOUTS (
  name varchar(50) CHARACTER SET latin1 not null,
  layout text not null,
  primary key (name)
) ENGINE=InnoDB;