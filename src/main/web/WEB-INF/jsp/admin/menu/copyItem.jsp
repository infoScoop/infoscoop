<%@ page contentType="application/json; charset=UTF8" %>
{
	"id": "${menuItem.id}",
	"parentId": "${menuItem.fkParent.id}",
	"title": "${menuItem.title}",
	"type": "${menuItem.fkGadgetInstance.type}",
	"publish": ${menuItem.publish == 1}
}