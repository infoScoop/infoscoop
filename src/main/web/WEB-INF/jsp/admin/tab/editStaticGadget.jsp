<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<tiles:insertDefinition name="dialog.definition" flush="true">
	<tiles:putAttribute name="type" value="tab"/>
	<tiles:putAttribute name="title" value="tab.title"/>
	<tiles:putAttribute name="body" type="string">
<c:set var="action" value="submitGadgetSettings" scope="request"/>
<c:set var="type" value="tab" scope="request"/>
<h2>ガジェットの追加</h2>
<c:import url="/WEB-INF/jsp/admin/gadget/_form.jsp"/>
<script type="text/javascript">
$("#gadget_settings input").each(function(){
	//TODO ここでdatatypeに従ってinputタグを変換
});
$("#gadget_settings select").each(function(){
	if(this.className == "radio"){
		var name = this.name;
		var radioEl = $.SPAN({className:'radio'});
		$(this).find("option").each(function(){
			radioEl.appendChild($.INPUT({type:'radio', value:this.value, name:name, checked:this.selected?"checked":false}));
			radioEl.appendChild($.LABEL({}, this.innerHTML));
		});
		$(this).replaceWith(radioEl);
	}
});

$(function(){
	var href_value= "selectGadgetType?tabId=${gadget.tabTemplateId}" +
						 "&containerId=${gadget.containerId}";
	$("#change_type").attr("href", href_value);
});
</script>
	</tiles:putAttribute>
</tiles:insertDefinition>