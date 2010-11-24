/*
 * ガジェットのUserPrefのdatatypeに応じた編集画面を構築します。
 */
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
			$.INPUT({type:"button", value:"追加"})
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