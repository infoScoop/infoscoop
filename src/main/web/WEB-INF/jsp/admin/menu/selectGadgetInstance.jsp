<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<h2>メニューの追加</h2>
<h4>リンクメニューの追加</h4>
<p>
	リンクメニューを追加する場合は<a href="#" onclick="showAddItem(false, '${parentId}');">こちら</a>をクリックしてください。
</p>
<c:if test="${parentId != ''}">
<h4>ガジェットの追加</h4>
<p>
	ガジェットを追加する場合は以下から追加するがジェットを選択してください。<br>
	新しいガジェットを追加するには<a href="#" onclick="selectGadgetType();">こちら</a>をクリックしてください。
</p>
<c:choose>
<c:when test="${fn:length(instances) > 0}">
<div id="gadgetInstanceList">
	<ul>
		<c:forEach var="instance" items="${instances}">
		<li><a href="#" onclick="showEditInstance('${instance.id}', '${parentId}')">${instance.title}</a></li>
		</c:forEach>
	</ul>
</div>
</c:when>
<c:otherwise>
<p>
ガジェットが登録されていません。<br>
<a href="#" onclick="selectGadgetType();">こちら</a>から新しいがジェットを登録してください。
</p>
</c:otherwise>
</c:choose>
</c:if>