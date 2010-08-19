<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<tiles:insertDefinition name="tab_dialog.definition" flush="true">
	<tiles:putAttribute name="type" value="menu"/>
	<tiles:putAttribute name="title" value="tab.title"/>
	<tiles:putAttribute name="body" type="string">
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
var gadgetConfs = {'upload' :{'RssReader':{title:'RssReader'}}};
$.each(gadgetConfs, function(key, gadgets){
	$.each(gadgets, function(type, gadget){
		var title =gadget.title;
		try{
			$("#gadgetTypeList #"+key+" ul").append(
				$.LI({},
					$.A(
						{
							href:"showGadgetDialog?type=" + type
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
	</tiles:putAttribute>
</tiles:insertDefinition>