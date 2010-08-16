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
							onclick:{handler:function(){showAddItem2(type, '${parentId}')}}
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
</script>