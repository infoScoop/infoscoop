<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<form:form modelAttribute="menuTree" method="post" action="saveMenu" class="cssform">
	<form:hidden path="id" />
	<fieldset>
		<legend>メニュー設定</legend>
		<p>
			<form:label for="title" path="title" cssErrorClass="error">タイトル</form:label>
			<form:input path="title" /><form:errors path="title" />
		</p>
		<p>このタイトルは分類にのみ使用します。ポータル画面に表示されることはありません。</p>
		<p>
			<form:label for="position" path="position" cssErrorClass="error">表示位置</form:label>
			<form:select path="position">
				<form:option value="" label="表示しない" selected="true"/>
				<form:option value="top" label="トップに表示"/>
				<form:option value="side" label="サイドに表示"/>
			</form:select>
			<form:errors path="position" />
		</p>
	</fieldset>
	<p>
		<input type="submit" value="更新" class="button"/>
		<input type="reset" value="リセット" class="button" />
	</p>
</form:form>
<script type="text/javascript">
$("#menuTree").ajaxForm(function(html){
	$("#menu_right").html(html);
});
$("#menuItem input.button").button();
</script>