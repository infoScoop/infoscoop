alter table ${SCHEMA_NAME}.tablayout rename to tablayout@BACKUP_TABLE_SUFFIX@;

create table ${SCHEMA_NAME}.tablayout (
  tabId varchar(50 BYTE ) not null,
  roleOrder integer not null,
  role clob not null,
  rolename varchar(256 BYTE ) not null,
  principalType varchar(50 BYTE ) default 'OrganizationPrincipal' not null,
  defaultUid varchar (150 BYTE ),
  widgets clob not null,
  layout clob,
  widgetsLastmodified varchar(24 BYTE ),
  tabNumber integer,
  deleteFlag integer default 0 not null,
  temp integer default 0 not null,
  workingUid varchar (150),
  primary key (tabId, roleOrder, temp)
);

insert into ${SCHEMA_NAME}.tablayout ( tabId,roleOrder,role,rolename,principalType,defaultUid,widgets,layout,widgetsLastmodified,tabNumber,deleteFlag,temp )
	select tabId,roleOrder,role,rolename,principalType,defaultUid,widgets,layout,widgetsLastmodified,tabNumber,deleteFlag,0 as temp from ${SCHEMA_NAME}.tablayout@BACKUP_TABLE_SUFFIX@;
