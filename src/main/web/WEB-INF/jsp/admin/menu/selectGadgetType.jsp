<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<h2>メニューの追加</h2>
<p>
	追加するメニューのタイプを選択してください。
</p>
<div id="gadgetTypeList">
	<ul>
		<li id="buildin"><span>組み込みガジェット</span>
			<ul></ul>
		</li>
		<li id="upload"><span>ガジェット</span>
			<ul></ul>
		</li>
	</ul>
</div>
<p>
	URLを指定してガジェットを追加する場合は下記にURLを入力して追加ボタンを押してください。<br/>
	<input type="text" id="gadgetUrl"><button id="gadgetAddButton">追加</button>
</p>
<script type="text/javascript">
$.each(gadgetConfs, function(key, gadgets){
	$.each(gadgets, function(type, gadget){
		var title = getGadgetTitle(type);
		try{
			$("#gadgetTypeList #"+key+" ul").append(
				$.LI({},
					$.A(
						{
							href:"#",
							onclick:{handler:function(){showAddItem(type, '${parentId}', title)}}
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
});
$("#gadgetAddButton").button().click(function(){
	var url = $("#gadgetUrl").val();
	if(!url) alert("URLを指定してください。");
	showAddItem("g_"+url, '${parentId}');
});
</script>