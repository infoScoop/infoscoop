alter table ${SCHEMA_NAME}is_i18n rename to is_i18n${BACKUP_TABLE_SUFFIX};

create table ${SCHEMA_NAME}is_i18n (
  type varchar(32 BYTE ) not null,
  id varchar(512 BYTE ) not null,
  country varchar(5 BYTE ) not null,
  lang varchar(5 BYTE ) not null,
  message varchar(2048 BYTE ) not null,
  primary key (type, id, country, lang)
);

insert into ${SCHEMA_NAME}is_i18n ( type,id,lang,country,message )
	select type,id,lang,country,message
	from ${SCHEMA_NAME}is_i18n${BACKUP_TABLE_SUFFIX}
	where type not in('js','widget');
