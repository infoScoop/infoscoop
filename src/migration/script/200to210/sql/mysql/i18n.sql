alter table ${SCHEMA_NAME}IS_I18N rename to IS_I18N${BACKUP_TABLE_SUFFIX};

create table ${SCHEMA_NAME}IS_I18N (
  type varchar(32) CHARACTER SET latin1 not null,
  id varchar(256) CHARACTER SET latin1 not null,
  country varchar(5) CHARACTER SET latin1 not null,
  lang varchar(5) CHARACTER SET latin1 not null,
  message varchar(2048) not null,
  primary key (type, id, country, lang)
) ENGINE=InnoDB;

insert into ${SCHEMA_NAME}IS_I18N ( type,id,lang,country,message )
	select type,id,lang,country,message
	from ${SCHEMA_NAME}IS_I18N${BACKUP_TABLE_SUFFIX}
	where type not in('js','widget');
