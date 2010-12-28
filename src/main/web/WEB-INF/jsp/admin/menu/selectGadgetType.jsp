<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<style>
.selectGadgetTypeForm{
	background-color:#FAFAFA;
	margin-left: 5px;
	padding: 15px 15px 25px;
	border: 1px solid #E4E4E4;
}
.selectGadgetTypeForm fieldset{
	border: none;
	border-top: 1px solid #AAA;
	padding: 5px;
	margin: 0 0 10px 10px;
}
.selectGadgetTypeForm legend{
	font-weight: bold;
	font-size:1.1em;
	_margin: 0 -7px; /* IE Win */
}
.selectGadgetTypeForm legend{
	padding-left: 0;
	color: #333;
}
.selectGadgetTypeForm center{
	padding:10px;
}

</style>
<h2><spring:message code="menu.selectGadgetType.title" /></h2>
<div class="selectGadgetTypeForm">
<fieldset>
	<legend>組み込みガジェット</legend>
	<spring:message code="menu.selectGadgetType.selectType" />
	<div id="gadget_type_list">
		<ul>
		</ul>
	</div>
</fieldset>
<fieldset>
	<legend>外部ガジェット - URL指定</legend>
	<spring:message code="menu.selectGadgetType.inputURL" /><br/>
	<input type="text" style="width:300px" id="gadget_url"><button id="gadget_add_button"><spring:message code="menu.selectGadgetType.button.add" /></button>
</fieldset>
<center><input type="cancel" value="キャンセル" class="button" /></center>
</div>

<script type="text/javascript">
var gadgetListUl = $("#gadget_type_list ul");
$.each(gadgetConfs, function(type, gadget){
	var title = getGadgetTitle(type);
	try{
		gadgetListUl.append(
			$.LI({},
				$.A(
					{
						href:"#",
						onclick:{handler:function(){showAddItem(false, type, title, '${parentId}')}}
					},
					title
				)
			)
		);
	}catch(e){
		console.error(e);
		return false;
	}
});
$("#gadget_add_button").button().click(function(){
	var url = $("#gadget_url").val();
	if(!url) alert("<spring:message code="menu.selectGadgetType.no.url" />");
	showAddItem(false, "g_"+url, "", '${parentId}');
});
$("input[type='cancel']").click(function(){
	$("#menu_right").html("<spring:message code="menu.editPage.description" /><br>");
});

$('input[type="cancel"]').button();
</script>