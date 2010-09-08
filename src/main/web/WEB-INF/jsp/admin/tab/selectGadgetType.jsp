<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<tiles:insertDefinition name="tab_dialog.definition" flush="true">
	<tiles:putAttribute name="type" value="menu"/>
	<tiles:putAttribute name="title" value="tab.title"/>
	<tiles:putAttribute name="body" type="string">
<h2>メニューの追加</h2>
<p>
	追加するメニューのタイプを選択してください。
</p>
<div id="gadgetTypeList">
	<ul>
		<c:forEach var="conf" items="${gadgetConfs}">
			<li><a href="newStaticGadget?tabId=${tabId}&containerId=${containerId}&type=${conf.type}">${conf.title}</a></li>
		</c:forEach>
	</ul>

</div>
	</tiles:putAttribute>
</tiles:insertDefinition>