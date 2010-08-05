alter table ${SCHEMA_NAME}.i18nlocale rename to i18nlocale@BACKUP_TABLE_SUFFIX@;

create table ${SCHEMA_NAME}.i18nlocale (
  id number(18) not null primary key,
  type varchar(32 BYTE ) not null,
  country varchar(5 BYTE ) not null,
  lang varchar(5 BYTE ) not null,
  constraint is_locales_unique unique (type, country, lang)
);

insert into ${SCHEMA_NAME}.i18nlocale ( id,type,country,lang )
	select
		is_i18nlocales_id_seq.nextval as id,
		locale.type,
		locale.country,
		locale.lang
	from (
		select distinct
			type,
			country,
			lang
		from
			${SCHEMA_NAME}.is_i18n
		where
			type not in (
				'js',
				'widget'
			)
	) locale;