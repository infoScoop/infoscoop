<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<form:form modelAttribute="tabTemplate" id="add_tab" method="post" action="${action}">
	<div class="infoScoop">
		<h1>タブ設定画面</h1>
		<p><label>タイトル：</label><form:input path="name"/></p>
		<p><label>公開：</label><form:radiobutton path="published" value="1" label="公開"/>
				<form:radiobutton path="published" value="0" label="非公開"/>
		</p>
		<p><label>公開範囲：</label><form:radiobutton path="accessLevel" value="0" label="Public"/>
					<form:radiobutton path="accessLevel" value="1"label="Special"/>
		</p>
		<form:hidden path="id" />
		<form:hidden path="tabId" />
		<form:hidden path="layout"/>
		<div id="portal-site-aggregation-menu"></div>
		<div id="portal-tree-menu" style="float:left;width:20%;"> </div>
		<div style="float:left;width:80%;">
			<h2>固定エリア</h2>
			<a href="#select_layout_modal" id="select_layout_link">Select Layout</a> <a href="#edit_layout_modal" id="edit_layout_link">Edit tamplate</a>
			<div id="staticAreaContainer">${tabTemplate.layout}</div>
			<h2>パーソナライズエリア</h2>
			<div id="personarizeAreaContainer"><div id="panels"><div id="tab-container"></div></div></div>
		</div>
		<div style="clear:both;text-align:center;"><input id="submit_button" type="submit" name="button" /></div>
	</div>
</form:form>