rename table ${SCHEMA_NAME}.siteAggregationMenu to siteAggregationMenu@BACKUP_TABLE_SUFFIX@;

create table ${SCHEMA_NAME}.siteAggregationMenu (
  type varchar(150) not null primary key,
  data xml not null
) compress yes;

insert into ${SCHEMA_NAME}.siteAggregationMenu (type,data)
	select type,data from ${SCHEMA_NAME}.siteAggregationMenu@BACKUP_TABLE_SUFFIX@
