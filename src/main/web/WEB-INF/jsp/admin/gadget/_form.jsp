<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<form:form modelAttribute="gadget" method="post" action="${action}" class="cssform">
	<form:hidden path="id" />
	<c:if test="${type == 'menu'}">
		<form:hidden path="id" />
		<form:hidden path="menuId" />
		<c:if test="${gadget.fkMenuTree != null}">
			<form:hidden path="fkMenuTree.id" />
		</c:if>
		<c:if test="${gadget.fkParent != null}">
			<form:hidden path="fkParent.id" />
		</c:if>
		<c:if test="${conf != null}">
			<form:hidden path="gadgetInstance.id" />
			<form:hidden path="gadgetInstance.type" />
		</c:if>
		<form:hidden path="menuOrder" />
		<c:if test="${conf != null }">
		<fieldset>
			<legend><spring:message code="gadget._form.type" /></legend>
			<ul>
				<li>
					<c:if test="${conf != null}">
					<x:choose>
						<x:when select="$conf/widgetConfiguration/@title"><x:out select="$conf/widgetConfiguration/@title" /></x:when>
						<x:when select="$conf/Module/ModulePrefs/@directory_title"><x:out select="$conf/Module/ModulePrefs/@directory_title" /></x:when>
						<x:when select="$conf/Module/ModulePrefs/@title"><x:out select="$conf/Module/ModulePrefs/@title" /></x:when>
						<x:otherwise>${gadget.type}</x:otherwise>
					</x:choose>
					</c:if>
				</li>
			</ul>
		</fieldset>
		</c:if>
		<fieldset>
			<legend><spring:message code="gadget._form.common" /></legend>
			<ul>
				<li>
					<form:label for="title" path="title" cssErrorClass="error"><spring:message code="gadget._form.title" /></form:label>
					<form:input path="title" /><form:errors path="title" />
				</li>
				<li>
					<form:label for="href" path="href" cssErrorClass="error"><spring:message code="gadget._form.link" /></form:label>
					<form:input path="href" /><form:errors path="href" />
				</li>
				<li>
					<form:label for="gadgetInstance.icon" path="gadgetInstance.icon" cssErrorClass="error">アイコン画像URL</form:label>
					<form:input path="gadgetInstance.icon" /><form:errors path="gadgetInstance.icon" />
				</li>
				<li>
					<form:label for="accessLevel" path="accessLevel" cssErrorClass="error"><spring:message code="gadget._form.publish" /></form:label>
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
			</ul>
		</fieldset>
	</c:if>
	<c:if test="${type == 'tab'}">
		<form:hidden path="containerId" />
		<form:hidden path="tabTemplateId" />
		<form:hidden path="gadgetInstance.type" />
		<form:hidden path="instanceId"/>
		<fieldset>
			<legend>タイプ</legend>
			<ul>
				<li>
					<x:choose>
						<x:when select="$conf/widgetConfiguration/@title"><x:out select="$conf/widgetConfiguration/@title" /></x:when>
						<x:when select="$conf/Module/ModulePrefs/@directory_title"><x:out select="$conf/Module/ModulePrefs/@directory_title" /></x:when>
						<x:when select="$conf/Module/ModulePrefs/@title"><x:out select="$conf/Module/ModulePrefs/@title" /></x:when>
						<x:otherwise>${gadget.gadgetInstance.type}</x:otherwise>
					</x:choose>
				</li>
			</ul>
			<a id="change_type"/>Change Type</a>
		</fieldset>
		<fieldset>
			<legend>共通設定</legend>
			<ul>
				<li>
					<form:label for="gadgetInstance.title" path="gadgetInstance.title" cssErrorClass="error">タイトル</form:label>
					<form:input path="gadgetInstance.title" /><form:errors path="gadgetInstance.title" />
				</li>
				<li>
					<form:label for="gadgetInstance.href" path="gadgetInstance.href" cssErrorClass="error">リンク</form:label>
					<form:input path="gadgetInstance.href" /><form:errors path="gadgetInstance.href" />
				</li>
			</ul>
		</fieldset>
		<fieldset>
			<legend>固定エリア設定</legend>
			<ul>
				<li>
					<form:label for="ignoreHeaderBool" path="ignoreHeaderBool" cssErrorClass="error">ヘッダを表示しない</form:label>
					<form:checkbox path="ignoreHeaderBool" /><form:errors path="ignoreHeaderBool" />
				</li>
				<li>
					<form:label for="noBorderBool" path="noBorderBool" cssErrorClass="error">枠を表示しない</form:label>
					<form:checkbox path="noBorderBool" /><form:errors path="noBorderBool" />
				</li>
			</ul>
		</fieldset>
	</c:if>
	<c:if test="${conf != null}">
	<fieldset id="gadget_settings">
		<legend><spring:message code="gadget._form.gadget" /></legend>
		<ul>
		<c:if test="${conf != null}">
		<x:forEach var="userPref" select="$conf//UserPref">
			<x:if select="$userPref/@admin_datatype or not($userPref/@datatype) or $userPref/@datatype!='hidden'">
				<x:choose>
					<x:when select="$userPref/@admin_datatype">
						<c:set var="datatype"><x:out select="$userPref/@admin_datatype"/></c:set>
					</x:when>
					<x:otherwise>
						<c:set var="datatype"><x:out select="$userPref/@datatype"/></c:set>
					</x:otherwise>
				</x:choose>
				<li class="${datatype}">
					<label><x:out select="$userPref/@display_name"/></label>
					<c:set var="name"><x:out select="$userPref/@name"/></c:set>
					<x:choose>
						<x:when select="$userPref/EnumValue">
							<select name="gadgetInstance.userPrefs[${name}]" class="${datatype}">
							<x:forEach var="enum" select="$userPref/EnumValue">
								<c:set var="value"><x:out select="$enum/@value"/></c:set>
								<c:set var="display_value"><x:out select="$enum/@display_value"/></c:set>
								<c:choose>
									<c:when test="${gadget.gadgetInstance.userPrefs[name] == value}">
										<option value="${value}" selected="selected">${display_value}</option>
									</c:when>
									<c:otherwise>
										<option value="${value}">${display_value}</option>
									</c:otherwise>
								</c:choose>
							</x:forEach>
							</select>
						</x:when>
						<x:otherwise>
							<input type="${datatype}" name="gadgetInstance.userPrefs[${name}]" value="${gadget.gadgetInstance.userPrefs[name]}" class="${datatype}"/>
						</x:otherwise>
					</x:choose>
				</li>
			</x:if>
		</x:forEach>
		</c:if>
		</ul>
	</fieldset>
	</c:if>
	<li>
		<input type="submit" value="<spring:message code="gadget._form.button.create" />" class="button"/>
		<input type="reset" value="<spring:message code="gadget._form.button.reset" />" class="button" />
	</li>
</form:form>
<script type="text/javascript">
<c:if test="${gadget.gadgetInstance != null}">
rebuildGadgetUserPrefs();
</c:if>
<c:if test="${type == 'menu'}">
$("#gadget").ajaxForm(function(html){
	$("#menu_right").html(html);
});
</c:if>
$("#gadget input.button").button();
</script>
