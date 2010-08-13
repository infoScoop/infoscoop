<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="tab"/>
	<tiles:putAttribute name="title" value="tab.title"/>
	<tiles:putAttribute name="body" type="string">
<script type="text/javascript" class="source">
//
$(function () {
	$("#add_tab").submit(function(){
		setTimeout(window.opener.location.reload(), 30000, true);
	});
});
</script>
<div>
タブ設定画面
	<form:form modelAttribute="tabTemplate" id="add_tab" method="post" action="addTab">
		<p>タイトル：<form:input path="tabName" /></p>
		<p>公開：<form:radiobutton path="published" value="1" label="公開"/>
				<form:radiobutton path="published" value="0" label="非公開"/>
		</p>
		<p>公開範囲：<form:radiobutton path="accessLevel" value="0" label="Public"/>
					<form:radiobutton path="accessLevel" value="1"label="Special"/>
		</p>
		<p><form:hidden path="id" /></p>
		<p><input type="submit" name="button" /></p>
	
	</form:form>
</div>
	</tiles:putAttribute>
</tiles:insertDefinition>