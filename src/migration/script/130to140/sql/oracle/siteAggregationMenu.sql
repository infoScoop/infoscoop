alter table ${SCHEMA_NAME}.siteAggregationMenu rename to siteAggregationMenu@BACKUP_TABLE_SUFFIX@;

create table ${SCHEMA_NAME}.siteAggregationMenu (
  type varchar(150 BYTE) not null primary key,
  data clob not null
);

insert into ${SCHEMA_NAME}.siteAggregationMenu (type,data)
	select type,data from ${SCHEMA_NAME}.siteAggregationMenu@BACKUP_TABLE_SUFFIX@
