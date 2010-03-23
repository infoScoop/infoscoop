alter table ${SCHEMA_NAME}.widget rename to widget@BACKUP_TABLE_SUFFIX@;

drop index ${SCHEMA_NAME}.widget_tabId;
drop index ${SCHEMA_NAME}.widget_parentId;
drop index ${SCHEMA_NAME}.widget_deleteDate;
drop index ${SCHEMA_NAME}.widget_type;

create table ${SCHEMA_NAME}.widget (
  id integer not null primary key,
  "UID" varchar(150 BYTE ) not null,
  defaultUid varchar(150 BYTE ),
  tabId varchar(32 BYTE ) not null,
  widgetId varchar(256 BYTE ) not null,
  "COLUMN" int,
  siblingId varchar(256 BYTE ),
  parentId varchar(256 BYTE ),
  menuId varchar(256 BYTE ),
  href varchar(1024 BYTE ),
  title varchar(256 BYTE ),
  type varchar(1024 BYTE ),
  isStatic int,
  ignoreHeader int,
  createDate number(18) default 0 not null,
  deleteDate number(18) default 0 not null,
  constraint is_widgets_unique unique ("UID", tabid, widgetId, deleteDate)
);

create index ${SCHEMA_NAME}.is_widget_tabId on ${SCHEMA_NAME}.widget(tabId);
create index ${SCHEMA_NAME}.is_widget_parentId on ${SCHEMA_NAME}.widget(parentId);
create index ${SCHEMA_NAME}.is_is_widget_deleteDate on ${SCHEMA_NAME}.widget(deleteDate);
create index ${SCHEMA_NAME}.is_widget_type on ${SCHEMA_NAME}.widget(type);
create index ${SCHEMA_NAME}.is_widget_menuId on ${SCHEMA_NAME}.widget(menuId);
create index ${SCHEMA_NAME}.is_widget_createDate on ${SCHEMA_NAME}.widget(createDate);
create index ${SCHEMA_NAME}.is_widget_isStatic on ${SCHEMA_NAME}.widget(isStatic);

insert into ${SCHEMA_NAME}.widget (id,"UID",defaultUid,tabId,widgetId,"COLUMN",siblingId,parentId,href,title,type,isStatic,ignoreHeader,deleteDate )
	select is_widgets_id_seq.nextval,"UID",defaultUid,tabId,widgetId,"COLUMN",siblingId,parentId,href,title,type,isStatic,ignoreHeader,deleteDate from ${SCHEMA_NAME}.widget@BACKUP_TABLE_SUFFIX@ where tabId = '-1' or tabId in (select distinct id from ${SCHEMA_NAME}.is_tabs where "UID"=${SCHEMA_NAME}.widget@BACKUP_TABLE_SUFFIX@."UID")