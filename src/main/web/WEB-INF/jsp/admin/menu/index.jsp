<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="menu"/>
	<tiles:putAttribute name="title" value="menu.title"/>
	<tiles:putAttribute name="body" type="string">
<script type="text/javascript" src="../../js/lib/jquery.js"></script>
<script type="text/javascript" src="../../js/lib/jsTree.v.1.0rc2/jquery.jstree.js"></script>
<script type="text/javascript" class="source">
$(function () {
	$("#menu").jstree({ 
		"json_data" : {
			"ajax" : {
				"url" : "data",
				"data" : function (n) { 
					return { id : n.attr ? n.attr("id") : 0 }; 
				}
			}
		},
		"plugins" : [ "themes", "json_data" ]
	});
});
</script>
<div style="height:500px;">
	Menu
	<div id="menu">
	
	</div>
</div>
	</tiles:putAttribute>
</tiles:insertDefinition>