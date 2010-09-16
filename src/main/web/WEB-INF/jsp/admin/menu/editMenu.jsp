<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<tiles:insertDefinition name="base.definition" flush="true">
	<tiles:putAttribute name="type" value="menu"/>
	<tiles:putAttribute name="title" value="menu.title"/>
	<tiles:putAttribute name="body" type="string">
<script type="text/javascript" src="../../js/lib/jsTree.v.1.0rc2/jquery.jstree.js"></script>
<script type="text/javascript" class="source">
var hostPrefix = "/infoscoop";//TODO スクリプトで計算
//TODO propertiesテーブルから取得して補正する
var staticContentURL="../..";
var imageURL = staticContentURL + "/skin/imgs/"
var copiedItemId, gadgetConfs, menuId = "${menuId}";
function getGadget(type){
	return gadgetConfs.builtin[type] || gadgetConfs.upload[type];
}
function getGadgetTitle(gadget){
	if(typeof gadget == "string")
		gadget = getGadget(gadget);
	return gadget.title
		 || gadget.ModulePrefs.directory_title
		 || gadget.ModulePrefs.title
		 || gadget.type;
}
function getIconUrl(type){
	if(!type)
		return imageURL + "manager/bullet_black.gif";
	var gadget = getGadget(type);
	try{
		if(gadget.icon)
			return imageURL + gadget.icon;
		else if(gadget.ModulePrefs.Icon.content)
			var icon = gadget.ModulePrefs.Icon.content;
			var realType = type.replace("upload__","");
			return icon.replace("__IS_GADGET_BASE_URL__", hostPrefix + '/gadget/' + realType);
	}catch(e){
		return imageURL + "widget_add.gif";
	}
}
function selectItem(id){
	$("#menu_tree").jstree("deselect_all");
	$("#menu_tree").jstree("select_node", "#"+id);
}
function getSelectedItem(){
	return $("#menu_tree").jstree("get_selected")[0];
}
function selectGadgetInstance(e, a, isTop){
	if(isTop)
		$("#menu_tree").jstree("deselect_all");
	var selectedItem = getSelectedItem(),
		id = selectedItem ? selectedItem.id : "";
	$.get("selectGadgetInstance", {
			id:id
		}, function(html){
			$("#menu_right").html(html);
		}
	);
}
function selectGadgetType(){
	var selectedItem = getSelectedItem();
	var id = selectedItem ? selectedItem.id : "";
	$.get("selectGadgetType", {id:id}, function(html){
		$("#menu_right").html(html);
	});
}
function showAddItem(type, parentId, title){
	$.get("showAddItem", {menuId: menuId, id: parentId, type: type?type:"", title: title?title:""}, function(html){
		$("#menu_right").html(html);
	});
}
function showEditItem(){
	var selectedItem = getSelectedItem();
	if(!selectedItem){
		alert("<spring:message code="menu.editMenu.invalid.operation" />");
		return;
	}
	var id = selectedItem.id;
	$.get("showEditItem", {id:id}, function(html){
		$("#menu_right").html(html);
	});
}
function showEditInstance(instanceId, parentId){
	$.post("showEditInstance", {instanceId:instanceId, id:parentId}, function(html){
		$("#menu_right").html(html);
	});
}
function addItemToTree(parentId, id, title, type, publish){
	$("#menu_tree").jstree("create",
		parentId ? "#"+parentId : -1,
		"last",
		{
			attr : { id : id},
			data : title
		},
		function(target){
			target.find("a:first").append('<span onclick="showMenuCommand(event, this, \''+id+'\')" class="menu_open">▼</span>');
			target.append('<div class="info"><span class="publish'+(publish?'"><spring:message code="menu.editPage.publish" />':' un"><spring:message code="menu.editPage.unpublish" />')+'</span></div>');
			var icon = getIconUrl(type);
			$("a:first ins", target)
				.css("display", "inline-block")
				.css("background", "url("+icon+")");
		},
		true
	);
}
function updateItemInTree(id, title, publish){
	try{
		/*var titleNode = $("#" + id + " a").contents().filter(function() { return this.nodeType == 3; })[0];
		titleNode.nodeValue = title;*/
		$("#menu_tree").jstree("set_text", "#"+id, title);
		var publishElm = $("#"+id+" .info span.publish").first();
		publishElm.toggleClass("un", !publish).html(publish? "<spring:message code="menu.editPage.publish" />":"<spring:message code="menu.editPage.unpublish" />");
	}catch(e){
		console.error(e);
	}
}
function copyItem(e, a){
	$("#menu_item_command .paste").removeClass("disabled");
	copiedItemId = getSelectedItem().id;
	e.stopPropagation();
}
function pasteItem(a){
	if($(a).hasClass("disabled") || !copiedItemId) return;
	var id = getSelectedItem().id;
	$.post("copyItem", {parentId:id, id:copiedItemId}, function(data){
		addItemToTree(data.parentId, data.id, data.title, data.type, data.publish);
	}, "json");
}
function deleteItem(){
	if(confirm("<spring:message code="menu.editPage.confirm.delete" />")){
		var id = getSelectedItem().id;
		$.post("removeItem", {id: id}, function(){
			$("#menu_tree").jstree("remove", "#"+id);
		});
	}
}
function togglePublish(){
	var id = getSelectedItem().id;
	$.post("togglePublish", {id: id}, function(){
		var publishElm = $("#"+id+" .info span.publish").first();
		var publish = publishElm.hasClass('un');//現在の反対にする
		publishElm.toggleClass("un", !publish).html(publish? "<spring:message code="menu.editPage.publish" />":"<spring:message code="menu.editPage.unpublish" />");
	});
}
function showMenuCommand(event, link, id){
	selectItem(id);
	$("#menu_item_command").css("top", $(link).position().top + $(link).height());
	$("#menu_item_command").css("left", $(link).position().left);
	$("#menu_item_command").show();
	event.stopPropagation();
}
function rebuildGadgetUserPrefs(){
	function replaceListForm(input){
		input.type = "hidden";
		var listElm = $($.DIV());
		function createListValueElm(value){
			return $.DIV({},
				$.INPUT({className:"value", type:"text", value:value}),
				$.DIV({className:"remove"}, "x")
			)
		}
		function fixListValues(){
			var values = "";
			$("input.value", listElm).each(function(i){
				if(i > 0) values += "|"
				values += $(this).val();
			});
			$(input).val(values);
		}
		if(input.value != ""){
			var values = input.value.split("|");
			$.each(values, function(i){
				listElm.append(createListValueElm(values[i]));
			})
		}
		$("input.value", listElm).live("focus", function(){
			$(this).toggleClass("selected", true);
		});
		$("input.value", listElm).live("blur", function(){
			$(this).toggleClass("selected", false);
			fixListValues();
		});
		$(".remove", listElm).live("click", function(){
			$(this).parent().remove();
			fixListValues();
		});
		listElm.append($.DIV({},
			$.INPUT({type:"text"}),
			$.INPUT({type:"button", value:"<spring:message code="menu.editPage.userPref.list.add" />"})
		));
		$("input[type=button]", listElm).button().click(function(){
			var value = $(this).prev().val();
			$(this).parent().before(createListValueElm(value));
			$(this).prev().val("");
			fixListValues();
			return false;
		});
		$(input).after(listElm);
	}
	$("#gadget_settings input").each(function(){
		var datatype = this.className;
		switch(datatype){
			case "bool":
				this.type = "checkbox";
				var boolFalse = $.INPUT({type:"hidden", value:"false", name:this.name});
				$(this).after(boolFalse);
				if(this.value == "true"){
					this.checked = "checked";
					boolFalse.disabled = "disabled";
				} else {
					this.value = "true";
				}
				$(this).change(function(){
					$(this).next().attr("disabled", this.checked ? "disabled":"");
				});
				break;
			case "list":
				replaceListForm(this);
				break;
			case "calendar":
				this.type = "text";
				$(this).datepicker({dateFormat: "yy/mm/dd"});
				break;
			case "url":
				break;
			case "xml":
			case "textarea":
				$(this).replaceWith($.TEXTAREA(
					{
						name: this.name,
						className: this.className
					},
					this.value)
				);
				break;
			case "string":
			default:
				this.type = "text"
		}
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
}
$(function () {
	var menuTree = $("#menu_tree").jstree({
		"html_data" : {
			"ajax" : {
				"url" : "tree?id="+menuId
			}
		},
		"themes" : {
			"icons" : false
		},
		"ui" : {
			"select_limit" : 1
		},
		"core" : {
			"animation" : 100
		},
		"crrm" : {
			"move" : {
				"check_move": function(m){
					var p = this._get_parent(m.o);
					//p: original parent, m.np: parent of moved item
					//deny dropping child item to top
					if(p != -1 && m.np === this.get_container())
						return false;
					//deny dropping top item to child
					if(p === -1 && m.np != this.get_container())
						return false;
					return true;
				}
			}
		},
		"plugins" : [ "themes", "html_data", "crrm", "dnd", "ui" ]
	});
	menuTree.bind("move_node.jstree", function(event, data){
		var node = data.rslt.o,
			parentNode = data.inst._get_parent(node);
			refNode = data.rslt.r,
			position = data.rslt.p;//last, after, before
		$.post("moveItem",
			{
				id: node.attr("id"),
				parentId: parentNode.attr ? parentNode.attr("id") : "",
				refId: refNode ? refNode.attr("id") : "",
				position: position
			},
			function(response){
			}
		);
	});
	$("#menu_command a").button();
	function resizeMenuTree(){
		var baseHeight = $(window).height() - $("#footer").height() - 13;
		var height = baseHeight - $("#menu_tree").offset().top;
		$("#menu_tree").css("height", height);
		height = baseHeight - $("#menu_right").offset().top;
		$("#menu_right").css("height", height);
	}
	$(window).resize(resizeMenuTree);
	resizeMenuTree();
	$(document.body).click(function(){
		$("#menu_item_command").hide();
	});
	
	
	//ガジェット設定を読み込む
	$.getJSON("getGadgetConf", null, function(json, status){
		gadgetConfs = json;
		//TODO 以下の処理はサーバーサイドでやりたい
		$.each(gadgetConfs.upload, function(type, gadget){
			gadgetConfs.upload[type].type = type;
		});
		$("li", menuTree).each(function(){
			var icon = getIconUrl(this.type);
			$("a ins", this).first()
				.css("display", "inline-block")
				.css("background", "url("+icon+")");
		});
	});
});
</script>
<div id="menu">
	<div id="menu_left">
		<div id="menu_command">
			<a onclick="showAddItem(false, '')"><spring:message code="menu.editPage.add.top" /></a>
		</div>
		<div id="menu_tree">
			
		</div>
	</div>
	<div id="menu_right">
		<spring:message code="menu.editPage.description" /><br>
	</div>
	<div style="clear:both"></div>
	<div id="menu_item_command" class="menu_item_command" style="display:none">
		<ul>
			<li><a onclick="selectGadgetInstance(event, this)"><spring:message code="menu.editPage.command.add" /></a></li>
			<li><a onclick="showEditItem(event, this)"><spring:message code="menu.editPage.command.edit" /></a></li>
			<li><a onclick="copyItem(event, this)"><spring:message code="menu.editPage.command.copy" /></a></li>
			<li><a onclick="pasteItem(event, this)" class="paste disabled"><spring:message code="menu.editPage.command.paste" /></a></li>
			<li><a onclick="deleteItem(event, this)"><spring:message code="menu.editPage.command.delete" /></a></li>
			<li><a onclick="togglePublish(event, this)"><spring:message code="menu.editPage.command.publish" /></a></li>
			<li><a onclick="togglePublish(event, this)"><spring:message code="menu.editPage.command.access" /></a></li>
		</ul>
	</div>
</div>
	</tiles:putAttribute>
</tiles:insertDefinition>