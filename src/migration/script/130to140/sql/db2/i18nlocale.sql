rename table ${SCHEMA_NAME}.i18nlocale to i18nlocale@BACKUP_TABLE_SUFFIX@;

create table ${SCHEMA_NAME}.i18nlocale (
  id bigint not null generated always as identity (start with 1, increment by 1, no cache) primary key,
  type varchar(32) not null,
  country varchar(5) not null,
  lang varchar(5) not null,
  constraint is_locales_unique unique (type, country, lang)
) compress yes;

insert into ${SCHEMA_NAME}.i18nlocale ( type,country,lang )
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
		);