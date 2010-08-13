<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<form:form modelAttribute="menuItem" method="post" action="${action}" class="cssform">
	<form:hidden path="id" />
	<form:hidden path="parentId" />
	<form:hidden path="order" />
	<fieldset>
		<legend>共通設定</legend>
		<p>
			<form:label for="title" path="title" cssErrorClass="error">タイトル</form:label>
			<form:input path="title" /><form:errors path="title" />
		</p>
		<p>
			<form:label for="type" path="type" cssErrorClass="error">タイプ</form:label>
			<form:input path="type" /><form:errors path="type" />
		</p>
		<p>
			<form:label for="href" path="href" cssErrorClass="error">リンク</form:label>
			<form:input path="href" /><form:errors path="href" />
		</p>
		<p>
			<label>公開設定</label>
			<span class="radio">
				<form:radiobutton path="publish" value="0" label="非公開" cssErrorClass="error" />
				<form:radiobutton path="publish" value="1" label="公開" cssErrorClass="error" />
				<form:errors path="publish" />
			</span>
		</p>
		<p>
			<form:label for="alert" path="alert" cssErrorClass="error">通知方法</form:label>
			<form:select path="alert">
				<form:option value="0" label="通知しない"/>
				<form:option value="1" label="通知する" selected="true"/>
				<form:option value="2" label="強制的に追加する"/>
			</form:select>
			<form:errors path="alert" />
		</p>
	</fieldset>
	<fieldset>
		<legend>ガジェット設定</legend>
		
	</fieldset>
	<p>
		<input type="submit" value="作成" class="button"/>
		<input type="reset" value="リセット" class="button" />
	</p>
</form:form>
<script type="text/javascript">
$("#menuItem").ajaxForm(function(html){
	$("#menu_right").html(html);
});
$("#menuItem input.button").button();
</script>