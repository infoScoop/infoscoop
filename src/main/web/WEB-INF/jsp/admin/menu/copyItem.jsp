<%@ page contentType="application/json; charset=UTF8" %>
{
	"id": "${menuItem.id}",
	"parentId": "${menuItem.fkParent.id}",
	"title": "${menuItem.title}",
	"type": "${menuItem.gadgetInstance.type}",
	"accessLevel": ${menuItem.accessLevel == 1}
}