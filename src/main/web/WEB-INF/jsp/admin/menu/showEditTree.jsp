<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="menu"/>
	<tiles:putAttribute name="title" value="menu.title"/>
	<tiles:putAttribute name="body" type="string">
<script type="text/javascript" class="source">
$(function () {
	$("#menuTree :submit, #menuTree :button").button();
});
</script>
<p>メニューツリーを編集します。</p>
<c:set var="type" value="menu" scope="request"/>
<form:form modelAttribute="menuTree" method="post" action="updateTree" class="cssform">
	<form:hidden path="id" />
	<form:hidden path="orderIndex" />
	<input type="hidden" name="country" value="ALL">
	<input type="hidden" name="lang" value="ALL">
	<fieldset>
		<legend><spring:message code="gadget._form.common" /></legend>
		<ul>
			<li>
				<form:label for="title" path="title"><spring:message code="gadget._form.title" /></form:label>
				<form:input path="title" /><form:errors path="title" cssClass="error"/>
			</li>
			<li>
				<form:label for="href" path="href" ><spring:message code="gadget._form.link" /></form:label>
				<form:input path="href" /><form:errors path="href" cssClass="error"/>
			</li>
			<li>
				<form:label for="description" path="description">説明</form:label>
				<form:textarea path="description" /><form:errors path="description" cssClass="error"/>
			</li>
			<li>
				<label>メニュー表示場所</label>
				<span class="radio" style="display:inline-block;">
				<!--<form:checkbox path="topPos" label="トップに表示"/><form:errors path="top" cssClass="error"/>-->
				<form:hidden path="topPos" value="1"/>
				<form:checkbox path="sidePos" label="サイドに表示"/><form:errors path="side" cssClass="error"/>
			</li>
			<li>
				<form:label for="accessLevel" path="publish" cssErrorClass="error"><spring:message code="gadget._form.publish" /></form:label>
				<span id="access_level_radio" class="radio" style="display:inline-block;">
					<c:set var="unpublish"><spring:message code="gadget._form.publish.off" /></c:set>
					<c:set var="publish"><spring:message code="gadget._form.publish.on" /></c:set>
					<div>
						<form:radiobutton path="publish" value="0" label="${unpublish}" cssErrorClass="error"/>
						<form:radiobutton path="publish" value="1" label="${publish}" cssErrorClass="error" />
						<form:errors path="publish" />
					</div>
				</span>
			</li>
			<li>
				<form:label for="accessLevel" path="accessLevel" cssErrorClass="error"><spring:message code="gadget._form.publishingRange" /></form:label>
				<c:import url="/WEB-INF/jsp/admin/gadget/_listRole.jsp"/>
			</li>
			<li>
				<form:label for="alert" path="alert" cssErrorClass="error"><spring:message code="gadget._form.notify" /></form:label>
				<c:set var="notifyOff"><spring:message code="gadget._form.notify.off" /></c:set>
				<c:set var="notifyOn"><spring:message code="gadget._form.notify.on" /></c:set>
				<form:select path="alert">
					<form:option value="0" label="${notifyOff}"/>
					<form:option value="1" label="${notifyOn}" selected="true"/>
				</form:select>
				<form:errors path="alert" />
			</li>
			<li>
				<input type="submit" name="button" value="保存" />
				<input type="button" value="キャンセル" onclick="javascript:window.location.href='index'" />
			</li>
		</ul>
	</fieldset>
</form:form>
	<div id="select_role_dialog">
	</div>
	</tiles:putAttribute>
</tiles:insertDefinition>