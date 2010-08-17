<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<form:form modelAttribute="menuItem" method="post" action="${action}" class="cssform">
	<form:hidden path="id" />
	<form:hidden path="parentId" />
	<form:hidden path="order" />
	<form:hidden path="type" />
	<fieldset>
		<legend>タイプ</legend>
		<p id="typeName">
		</p>
	</fieldset>
	<fieldset>
		<legend>共通設定</legend>
		<p>
			<form:label for="title" path="title" cssErrorClass="error">タイトル</form:label>
			<form:input path="title" /><form:errors path="title" />
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
	<fieldset id="gadget_settings">
		<legend>ガジェット設定</legend>
		
	</fieldset>
	<p>
		<input type="submit" value="作成" class="button"/>
		<input type="reset" value="リセット" class="button" />
	</p>
</form:form>
<script type="text/javascript">
var gadgetConf = getGadget("${menuItem.type}");

//UserPrefの値をJavaScriptで使えるようにJSONにする
var itemUserPref = {
<c:forEach var="userPref" items="${menuItem.userPref}" varStatus="status">
	<c:if test="${! status.first}">,</c:if>"${userPref.key}":"${userPref.value}"
</c:forEach>
}

//タイプ名を表示
$("#typeName").append(getGadgetTitle(gadgetConf));

//ガジェット設定用フォーム作成
$.each(gadgetConf.UserPref, function(name, userPref){
	if((userPref.admin_datatype || userPref.datatype) == "hidden")
		return true;
	try{
		var p = $.P({},
			$.LABEL(
				{for:"userPref["+name+"]"},
				userPref.display_name || name
			),
			$.INPUT(
				{
					name:"userPref["+name+"]",
					type:"text",
					value:itemUserPref[name] || ""
				}
			)
		);
		$("#gadget_settings").append(p);
	}catch(e){
		console.info(e);
		return false;
	}
});

$("#menuItem").ajaxForm(function(html){
	$("#menu_right").html(html);
});
$("#menuItem input.button").button();
</script>