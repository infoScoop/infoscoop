<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="user"/>
	<tiles:putAttribute name="title" value="syncmaster.title"/>
	<tiles:putAttribute name="body" type="string">

<script type="text/javascript" class="source">
function index(){
	window.location.href = "index";
}
</script>

<p>
ユーザが${userCount}件、グループが${groupCount}件同期されました。
</p>
<input type="button" value="戻る" onclick="index()"/>
	
	</tiles:putAttribute>
</tiles:insertDefinition>