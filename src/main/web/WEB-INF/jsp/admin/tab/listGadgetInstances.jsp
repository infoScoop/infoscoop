<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<tiles:insertDefinition name="tab_dialog.definition" flush="true">
	<tiles:putAttribute name="type" value="menu"/>
	<tiles:putAttribute name="title" value="tab.title"/>
	<tiles:putAttribute name="body" type="string">
<h2>設定済みガジェット一覧</h2>
	
<c:choose>
	<c:when test="${fn:length(instances) > 0}">
		<div id="gadgetInstanceList">
			<ul>
				<c:forEach var="instance" items="${instances}">
				<li><a href="editInstance?tabId=${tabId}&containerId=${containerId}&instanceId=${instance.id}">${instance.title}</a></li>
				</c:forEach>
			</ul>
		</div>
	</c:when>
	<c:otherwise>
		<p>
		ガジェットが登録されていません。<br>
		<a href="#">こちら</a>から新しいがジェットを登録してください。
		</p>
	</c:otherwise>
</c:choose>
	
	
	
	
	</tiles:putAttribute>
</tiles:insertDefinition>