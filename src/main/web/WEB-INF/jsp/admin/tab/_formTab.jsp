<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<form:form modelAttribute="tabTemplate" id="add_tab" method="post" action="updateTab" class="cssform">
	<div id="infoscoop" class="infoScoop">
		<fieldset>
			<legend><spring:message code="tab._formTab.title" /></legend>
			<ul>
				<li>
					<label><spring:message code="tab._formTab.tabTitle" />：</label>
					<form:input path="name"/>
				</li>
				<li>
					<form:label for="accessLevel" path="publish" cssErrorClass="error"><spring:message code="gadget._form.publish" />：</form:label>
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
					<label><spring:message code="gadget._form.publishingRange" />：</label>
					<c:import url="/WEB-INF/jsp/admin/gadget/_listRole.jsp"/>
				</li>
				<li>
					<label>表示エリア：</label>
					<form:select path="areaType">
						<form:option value="0" label="固定エリアとパーソナライズエリアをどちらも表示する"/>
						<form:option value="1" label="パーソナライズエリアを使用しない"/>
						<form:option value="2" label="パーソナライズエリアを使用せず、固定エリアの高さをブラウザの高さに合わせて表示する"/>
					</form:select>
				</li>
			</ul>
		</fieldset>
		<div style="clear:both;text-align:center;"><input class="submit_button" type="submit" name="button" value="保存" /><input class="cancel_button" type="button" name="button" value="キャンセル" /></div>
		<form:hidden path="id" />
		<form:hidden path="tabId" />
		<form:hidden path="layout" />
		<form:hidden path="layoutModified"/>
		<c:if test="${tabTemplate.areaType == 0}">
		<div id="portal-site-aggregation-menu"></div>
		</c:if>
		<div id="portal-tree-menu" style="float:left;"> </div>
		<div id="infoscoop-panel" style="float:left;">
			<fieldset>
				<legend><spring:message code="tab._formTab.staticArea" /></legend>
				<a href="#select_layout_modal" id="select_layout_link">レイアウトの選択</a> <a href="#edit_layout_modal" id="edit_layout_link">レイアウトの高度な編集</a>
				<div id="staticAreaContainer">${tabTemplate.layout}</div>
			</fieldset>
			<c:if test="${tabTemplate.areaType == 0}">
			<fieldset>
				<legend><spring:message code="tab._formTab.personalizedArea" /></legend>
				<div id="personarizeAreaContainer"><div id="panels"><div id="tab-container"></div></div></div>
			</fieldset>
			</c:if>
		</div>
		<div style="clear:both;text-align:center;"><input class="submit_button" type="submit" name="button" value="保存" /><input class="cancel_button" type="button" name="button" value="キャンセル" /></div>
	</div>
</form:form>
