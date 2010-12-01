<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<form:form modelAttribute="tabTemplate" id="add_tab" method="post" action="updateTab">
	<div class="infoScoop">
		<h1>タブ設定画面</h1>
		<p><label>タイトル：</label><form:input path="name"/></p>
		<p><label>公開：</label><form:radiobutton path="published" value="1" label="公開"/>
				<form:radiobutton path="published" value="0" label="非公開"/>
		</p>
		<p>
			<label>公開設定：</label>
			<span class="radio">
				<c:set var="unpublish"><spring:message code="gadget._form.publish.off" /></c:set>
				<c:set var="publish"><spring:message code="gadget._form.publish.on" /></c:set>
				<form:radiobutton path="accessLevel" value="0" label="${unpublish}" cssErrorClass="error" />
				<form:radiobutton path="accessLevel" value="1" label="${publish}" cssErrorClass="error" />
				<form:radiobutton path="accessLevel" value="2" label="限定公開" cssErrorClass="error" />
				<form:errors path="accessLevel" />
				<div id="selected_security_role_panel" style="display:${ ( tabTemplate.accessLevel== 2 ? "":"none" )};">
				<c:import url="/WEB-INF/jsp/admin/gadget/_listRole.jsp"/>
				</div>
			</span>
		</p>
		<form:hidden path="id" />
		<form:hidden path="tabId" />
		<form:hidden path="layout" />
		<form:hidden path="layoutModified"/>
		<div id="portal-site-aggregation-menu"></div>
		<div id="portal-tree-menu" style="float:left;width:20%;"> </div>
		<div style="float:left;width:80%;">
			<h2>固定エリア</h2>
			<a href="#select_layout_modal" id="select_layout_link">Select Layout</a> <a href="#edit_layout_modal" id="edit_layout_link">Edit tamplate</a>
			<div id="staticAreaContainer">${tabTemplate.layout}</div>
			<h2>パーソナライズエリア</h2>
			<div id="personarizeAreaContainer"><div id="panels"><div id="tab-container"></div></div></div>
		</div>
		<div style="clear:both;text-align:center;"><input id="submit_button" type="submit" name="button" value="保存" /></div>
	</div>
</form:form>