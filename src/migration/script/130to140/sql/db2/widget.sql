rename table ${SCHEMA_NAME}.widget to widget@BACKUP_TABLE_SUFFIX@;

drop index ${SCHEMA_NAME}.widget_tabId;
drop index ${SCHEMA_NAME}.widget_parentId;
drop index ${SCHEMA_NAME}.widget_deleteDate;
drop index ${SCHEMA_NAME}.widget_type;

create table ${SCHEMA_NAME}.widget (
  id integer not null generated always as identity (start with 1, increment by 1, no cache) primary key,
  uid varchar(150) not null,
  defaultUid varchar(150),
  tabId varchar(32) not null,
  widgetId varchar(256) not null,
  column int,
  siblingId varchar(256),
  parentId varchar(256),
  menuId varchar(256) default '' not null,
  href varchar(1024),
  title varchar(256),
  type varchar(1024),
  isStatic int,
  ignoreHeader int,
  createDate bigint default 0 not null,
  deleteDate bigint default 0 not null,
  constraint is_widgets_unique unique (uid, tabid, widgetId, deleteDate)
) compress yes;

create index ${SCHEMA_NAME}.is_widgets_tabId on ${SCHEMA_NAME}.widget(tabId);
create index ${SCHEMA_NAME}.is_widgets_parentId on ${SCHEMA_NAME}.widget(parentId);
create index ${SCHEMA_NAME}.is_widgets_deleteDate on ${SCHEMA_NAME}.widget(deleteDate);
create index ${SCHEMA_NAME}.is_widgets_type on ${SCHEMA_NAME}.widget(type);
create index ${SCHEMA_NAME}.is_widgets_menuId on ${SCHEMA_NAME}.widget(menuId);
create index ${SCHEMA_NAME}.is_widgets_createDate on ${SCHEMA_NAME}.widget(createDate);
create index ${SCHEMA_NAME}.is_widgets_isStatic on ${SCHEMA_NAME}.widget(isStatic);

insert into ${SCHEMA_NAME}.widget (uid,defaultUid,tabId,widgetId,column,siblingId,parentId,href,title,type,isStatic,ignoreHeader,deleteDate )
	select
		uid,defaultUid,tabId,widgetId,column,siblingId,parentId,href,title,type,isStatic,ignoreHeader,deleteDate
	from
		${SCHEMA_NAME}.widget@BACKUP_TABLE_SUFFIX@
	where
		tabId = '-1'
		or tabId in (
			select distinct
				id
			from
				${SCHEMA_NAME}.is_tabs
			where
				uid=${SCHEMA_NAME}.widget@BACKUP_TABLE_SUFFIX@.uid
		)