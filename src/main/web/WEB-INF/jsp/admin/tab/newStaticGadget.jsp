<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<tiles:insertDefinition name="tab_dialog.definition" flush="true">
	<tiles:putAttribute name="type" value="menu"/>
	<tiles:putAttribute name="title" value="tab.title"/>
	<tiles:putAttribute name="body" type="string">
<c:import url="/WEB-INF/jsp/admin/tab/_formStaticGadget.jsp"/>
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
$("#menuItem").ajaxForm(function(html){
	$("#menu_right").html(html);
});
$("#menuItem input.button").button();
</script>
	</tiles:putAttribute>
</tiles:insertDefinition>