<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<form:form modelAttribute="menuItem" method="post" action="addItem">
	<fieldset>
		<legend>メニューアイテムの追加</legend>
		<form:hidden path="id" />
		<form:hidden path="parentId" />
		<form:hidden path="order" />
		<p>
			<form:label for="title" path="title" cssErrorClass="error">タイトル</form:label><br/>
			<form:input path="title" /><form:errors path="title" />
		</p>
		<p>
			<form:label for="type" path="type" cssErrorClass="error">タイプ</form:label><br/>
			<form:input path="type" /><form:errors path="type" />
		</p>
		<p>
			<form:label for="href" path="href" cssErrorClass="error">リンク</form:label><br/>
			<form:input path="href" /><form:errors path="href" />
		</p>
		<p>
			<form:label for="publish" path="publish" cssErrorClass="error">公開</form:label><br/>
			<form:input path="publish" /><form:errors path="publish" />
		</p>
		<p>
			<form:label for="alert" path="alert" cssErrorClass="error">通知方法</form:label><br/>
			<form:input path="alert" /><form:errors path="alert" />
		</p>
		<p>	
			<input id="create" type="submit" value="作成" />
		</p>
	</fieldset>
</form:form>
<script type="text/javascript">
$("#menuItem").submit(function() {
	try{
		var item = $(this).serialize();
		$.post('addItem', item, function(html) {
			$("#menu_right").html(html);
		});
	}catch(e){
		console.error(e);
	}
	return false;
});
</script>
