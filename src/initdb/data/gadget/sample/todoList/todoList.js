Browser = {'isIE' : window.ActiveXObject ? true : false};

TodoList = IS_Class.create();
TodoList.prototype.classDef = function() {
	var self = this;
	
	var prefs = new gadgets.Prefs();
	prefs.setDontEscape_();
	var imageURL = "./skin/default/imgs/";
	
//	var priorityList = new Array('hegh','normal','low');
	var priorityList = [
		prefs.getMsg("priority_high"),
		prefs.getMsg("priority_normal"),
		prefs.getMsg("priority_low")
	];
	var priorityColorList = [
		'#ff0000',
		'#090',
		'#228'
	];
	var defaultPriority = 1;
	
	this.initialize = function(){
		this.elm_todoListTable;
		this.selectPriorityNode = null;
		
		if( !prefs.getString("todoList") || prefs.getString("todoList") == "" ){
			prefs.set("todoList", gadgets.json.stringify( []) );
		}else{
//			widget.initUserPref("todoList", eval('(' + prefs.getString("todoList") + ')'));
		}
		
		if( prefs.getString("fontSize") == "large")
			$( document.body ).addClassName("large");
		buildContents();
	}
	
	function buildContents(){
		buildAddTodoBox();
		
		var todoListTable = document.createElement("table");
		self.elm_todoListTable = todoListTable;
		todoListTable.className = 'todoListTable';
		var todoListTbody = document.createElement("tbody");
		todoListTbody.id = widgetId + '_list';
		todoListTable.appendChild( todoListTbody );
		
		document.body.appendChild( todoListTable );
	};
	
	// Show add button to add textbox
	function buildAddTodoBox(){
		var addTable = document.createElement("table");
		addTable.className = 'todoAddTable';
		document.body.appendChild( addTable );
		var addTbody = document.createElement("tbody");
		addTable.appendChild( addTbody );
		var addTr = document.createElement("tr");
		addTbody.appendChild( addTr );
		
		var textBoxTd = document.createElement("td");
		textBoxTd.className = 'todoAddTableTd';
		addTr.appendChild( textBoxTd );
		var addTextBox = document.createElement("input");
		addTextBox.className = 'todoAddTextBox';
		addTextBox.id = widgetId + "_inputText";
		addTextBox.type = "text";
		textBoxTd.appendChild( addTextBox );
		
		var addButtonTd = document.createElement("td");
		addTr.appendChild( addButtonTd );
		var addButton = document.createElement("input");
		addButton.type = "button";
//		addButton.value = "add";
		addButton.value = prefs.getMsg("add");
		addButtonTd.appendChild( addButton );
		
		Event.observe( addTextBox, 'keydown', textOnKeyDown, false, widgetId );
		Event.observe( addTextBox, 'focus', textOnFocus, false, widgetId );
		Event.observe( addButton, 'click', addItem, false, widgetId );
	};
	
	function textOnFocus(e){
		var target = Event.element(e);
		target.select();
	}
	
	function textOnKeyDown(e){
		if (Browser.isIE) {	/* for IE */
			e = event;
		}
		
		if(e.keyCode == 13){
			addItem(e);
		}
	}
	
	function addTodoObj(todoObj){
		var todoList = gadgets.json.parse( gadgets.util.unescapeString( prefs.getString("todoList")) );
		todoList.push(todoObj);
		prefs.set("todoList", gadgets.json.stringify( todoList) );
	}
	
	function setTodoListParam(num, name, value){
		var todoList = gadgets.json.parse( gadgets.util.unescapeString( prefs.getString("todoList")) );
		todoList[num][name] = value;
		prefs.set("todoList", gadgets.json.stringify( todoList) );
	}
	
	function getTodoListParam(num, name){
		var todoList = gadgets.json.parse( gadgets.util.unescapeString( prefs.getString("todoList")) );
		return todoList[num][name];
	}
	
	// Add new TODO
	function addItem(e){
		
		var addTextBox = $(widgetId + "_inputText");
		var todoText = addTextBox.value;
		
		var tmpText = todoText.replace(/　| /g, "");
		if(tmpText.length == 0){
			//alert("InputContentsInTextBox");
			return false;
		}
		
		var todoObj = new Object();
		todoObj.id = new Date().getTime();
		todoObj.priority = defaultPriority;
		todoObj.checked = false;
		todoObj.todoText = todoText;
//		widget.userPref.todoList.push( todoObj );
		addTodoObj(todoObj);
		
		self.makeItem(todoObj);
		
		addTextBox.value = "";
		
		gadgets.window.adjustHeight();
	}
	
	// Add TODOlist node
	this.makeItem = function(todoObj){
		
		var id = todoObj.id;
		var list = $( widgetId + '_list' );
		
		if(/true/i.test(todoObj.checked)){
			todoObj.checked = true;
		}else{
			todoObj.checked = false;
		}
		
		var newItem = document.createElement('tr');
		newItem.id = widgetId + '_' + id + '_item';
		newItem.name = (todoObj.checked)? 'checked' : '';
		if(list.childNodes.length % 2 == 1){
			newItem.className = 'todoItemEven';
		}else{
			newItem.className = 'todoItemOdd';
		}
		
		// Create the part that shows priority
		newItem.appendChild( makePriorityText(id, todoObj.priority) );
		// Create the part that shows contents of TODO
		newItem.appendChild( makeTodoText(id, todoObj.todoText, todoObj.checked) );
		// Create checkbox
		newItem.appendChild( makeCheckBox(id, todoObj.checked) );
		// Create delete button
		newItem.appendChild( makeDeleteButton(id) );
		
		Event.observe( newItem, 'mousedown', self.dragTodoItem.bind(self, newItem), false, newItem.id);
		
		//self.setDragTodoItem(newItem);
		
		if(newItem){
			list.appendChild( newItem );
			if(list.style.display != "block")
				self.elm_todoListTable.style.display = "block";
		}
		
		if(Browser.isIE){
			// For trouble that cause IE to have unchecked checkbox when it is initially displayed
			var checkbox = $( newItem.id.replace('item', 'check') );
			checkbox.checked = todoObj.checked;
		}
		
		return newItem;
	}
	
	
	// Create node of priority part
	function makePriorityText(id, priority){
		var priorityTd = document.createElement("td");
		priorityTd.className = 'todoPriorityTd';
		var priorityNode = document.createElement("div");
		priorityTd.appendChild( priorityNode );
		priorityNode.id = widgetId + '_' + id + '_priority';
		priorityNode.name = priority;
		priorityNode.className = 'todoPriority';
		if(priority < priorityColorList.length){
			priorityNode.style.color = priorityColorList[priority];
		}else{
			priorityNode.style.color = priorityColorList[ priorityColorList.length-1 ];
		}
		
		var priorityText = document.createTextNode( priorityList[priority] );
		priorityNode.appendChild( priorityText );
		Event.observe( priorityNode, 'mousedown', stopEvent, false, priorityNode.id);
		Event.observe( priorityNode, 'click', self.displayPriorityList.bind(self, priorityNode), false, priorityNode.id);
		
		return priorityTd;
	}
	
	function stopEvent(e){
		Event.stop(e);
	}
	
	// Create node of contents part of TODO
	function makeTodoText(id, todoText, checked){
		var textTd = document.createElement("td");
		textTd.className = 'todoTextTd';
		var todoNode = document.createElement("div");
		textTd.appendChild( todoNode );
		todoNode.id = widgetId + '_' + id + '_text';
		todoNode.title = todoText;
		todoNode.className = 'todoTextDiv';
		
		var title = document.createElement("span");
		title.className = 'todoText';
		title.appendChild( document.createTextNode(todoText) );
		todoNode.appendChild( title );
		
		self.changeTextChecked( todoNode,checked )
		
		Event.observe( title, 'mousedown', stopEvent, false, todoNode.id);
		Event.observe( title, 'click', self.displayTextEditForm.bind(self, todoNode), false, todoNode.id);
		
		return textTd;
	}
	
	// Create checkbox node
	function makeCheckBox(id, checked){
		var checkTd = document.createElement("td");
		checkTd.className = 'todoCheckTd';
		var checkBox = document.createElement("input");
		checkBox.id = widgetId + '_' + id + '_check';
		checkBox.type = 'checkbox';
		checkBox.value = 'check';
		checkBox.checked = checked;
		checkBox.className = 'todoCheck';
		checkTd.appendChild( checkBox );
		
		Event.observe( checkBox, 'mousedown', stopEvent, false, checkBox.id);
		Event.observe( checkBox, 'click', self.checkBoxOnClicked.bind(self, checkBox), false, checkBox.id);
		
		return checkTd;
	}
	
	// Create delete button node
	function makeDeleteButton(id){
		var deleteTd = document.createElement("td");
		deleteTd.className = 'todoDeleteTd';
		var deleteButton = document.createElement("img");
		deleteTd.appendChild( deleteButton );
		deleteButton.id = widgetId + '_' + id + '_del';
		deleteButton.className = 'todoDelete';
//		deleteButton.title = 'delete';
		deleteButton.title = prefs.getMsg("delete");
		deleteButton.src = is_gadgetBaseUrl + '/imgs/trash.gif';
		
		Event.observe( deleteButton, 'mousedown', self.iconDown.bind(self, deleteButton), false, deleteButton.id);
		Event.observe( deleteButton, 'mouseup', self.iconUp.bind(self, deleteButton), false, deleteButton.id);
		Event.observe( deleteButton, 'click', self.deleteItem.bind(self, id), false, deleteButton.id);
		
		return deleteTd;
	}
	
	this.iconUp = function (icon, e) {
		icon.style.marginTop = "2px";
		icon.style.marginBottom = "1px";
	}
	
	this.iconDown = function (icon, e) {
		icon.style.marginTop = "3px";
		icon.style.marginBottom = "0px";
		// Stop event to be passed to upper level
		if (!Browser.isIE) { 
			e.preventDefault(); 
			e.stopPropagation(); 
		} else { 
			event.returnValue = false; 
			event.cancelBubble = true; 
		}
	}
	
	// Show select menu of priority
	this.displayPriorityList = function( priorityNode ){
		self.removeSelectPriority();
		
		// For adjusting the width when pull-down menu is displayed
		if( !Browser.isIE ){
			this.elm_todoListTable.style.tableLayout = 'auto';
			var firstTodo = this.elm_todoListTable.firstChild.childNodes[0];
			var firstPriorityTdNode = firstTodo.childNodes[0];
			firstPriorityTdNode.style.width = '3em';
		}else{
			// text-align:center is applied only to the items whose style is adjusted
			var items = this.elm_todoListTable.firstChild.childNodes;
			for(var i=0; i<items.length; i++){
				items[i].childNodes[0].style.width = '3em';
			}
		}
		
		priorityNode.style.display = 'none';
		
		var selectNode = document.createElement('select');
		Event.stopObserving( priorityNode.id );
		
		var i;
		for(i=0; i<priorityList.length; i++){
			var opt = document.createElement('option');
			opt.id = widgetId + '_opt' + i;
			opt.value = i;
			opt.innerHTML = priorityList[i];
			if(priorityNode.name == i){
				opt.selected = true;
			}
			selectNode.appendChild( opt );
		}
		Event.observe( selectNode, 'mousedown', function(e){
			if(e && e.stopPropagation)e.stopPropagation();
		}, false, priorityNode.id);
		Event.observe( selectNode, 'change', this.selectedPriority.bind(this, selectNode), false, priorityNode.id);
		Event.observe( selectNode, 'blur', this.selectedPriority.bind(this, selectNode), false, priorityNode.id);
		
		priorityNode.parentNode.appendChild( selectNode );
		
		this.selectPriorityNode = selectNode;
		
		selectNode.focus();
	}
	
	// Reflect the selected priority
	this.selectedPriority = function( selectNode ){
		// Restore width
		if( !Browser.isIE ){
			this.elm_todoListTable.style.tableLayout = '';
			var firstTodo = this.elm_todoListTable.firstChild.childNodes[0];
			var firstPriorityTdNode = firstTodo.childNodes[0];
			firstPriorityTdNode.style.width = '';
		}else{
			var items = this.elm_todoListTable.firstChild.childNodes;
			for(var i=0; i<items.length; i++){
				items[i].childNodes[0].style.width = '';
			}
		}
		
		var selectPriority = selectNode.selectedIndex;
		var itemId = selectNode.parentNode.parentNode.id.replace("priority","item");
		var number = this.getItemNumberFromId( itemId );
		
		if(-1 < number){
//			widget.userPref.todoList[number].priority = selectPriority;
			setTodoListParam(number, "priority", selectPriority);
		}
		
		var priorityNode = selectNode.parentNode.firstChild;
		// Deactivate event listener
		Event.stopObserving( priorityNode.id );
		
		selectNode.parentNode.removeChild( selectNode );
		priorityNode.firstChild.nodeValue = priorityList[selectPriority];
		priorityNode.name = selectPriority;
		if(selectPriority < priorityColorList.length){
			priorityNode.style.color = priorityColorList[selectPriority];
		}else{
			priorityNode.style.color = priorityColorList[ priorityColorList.length-1 ];
		}
		priorityNode.style.display = '';
		
		this.selectPriorityNode = null;
		
		Event.observe( priorityNode, 'mousedown', stopEvent, false, priorityNode.id);
		Event.observe( priorityNode, 'click', this.displayPriorityList.bind(this, priorityNode), false, priorityNode.id);
		
	}
	
	// Close the shown menu where priority is selected
	this.removeSelectPriority = function(){
		if( this.selectPriorityNode != null ){
			this.selectedPriority( this.selectPriorityNode );
		}
	}
	
	// Show textbox to edit todo
	this.displayTextEditForm = function( todoNode ){
		var beforeText = todoNode.firstChild.firstChild.nodeValue;
		
		var editForm = document.createElement("input");
		editForm.setAttribute('autocomplete','off'); 
		editForm.id = todoNode.id + "_edit";
		editForm.type = "text";
		editForm.className = 'todoTextEdit';
		todoNode.parentNode.replaceChild( editForm,todoNode );
		
		setTimeout( function() {
			// Width is decided by initial value in IE
			editForm.value = beforeText;
			
			editForm.select();
			editForm.focus();
			editForm.select();
			
			Event.observe( editForm, 'keydown', editFormOnKeydown, false, todoNode.id);
			Event.observe( editForm, 'blur', editFormOnBlur, false, todoNode.id);
			TodoList.todoEdit = true;
		},( Browser.isIE? 100 : 10 ) );
		
		function editFormOnBlur(e){
			var nowText = editForm.value.replace(/　| /g, "");
			if(nowText.length == 0) {
				editForm.value = beforeText;
			}
			
			todoNode.firstChild.firstChild.nodeValue = editForm.value;
			todoNode.title = editForm.value;
			
			var itemId = todoNode.id.replace('text','item');
			
			var itemNumber = self.getItemNumberFromId( itemId );
			if( 0 <= itemNumber ){
//				widget.userPref.todoList[ itemNumber ].todoText = editForm.value;
				setTodoListParam(itemNumber, "todoText", editForm.value);
			}
						
			Event.stopObserving( editForm, 'keydown', editFormOnKeydown, false );
			Event.stopObserving( editForm, 'blur', editFormOnBlur, false );
			
			editForm.parentNode.replaceChild( todoNode,editForm );
			editForm = null;
			TodoList.todoEdit = false;
		}
		
		function editFormOnKeydown(e){
			if (Browser.isIE) {	/* for IE */
				e = event;
			}
			
			if(e.keyCode == 13){
				editFormOnBlur();
				
				Event.stop( e );
			}
		}
		
	}
	
	// Change check mode
	this.checkBoxOnClicked = function( checkBox ){
		var itemId = checkBox.id.replace("check","item");
		var itemNode = $( itemId );
		var itemNumber = this.getItemNumberFromId( itemId );
		
		var check = checkBox.checked;
		if(check){
			if(-1 < itemNumber){
//				widget.userPref.todoList[itemNumber].checked = true;
				setTodoListParam(itemNumber, "checked", true);
			}
			itemNode.name = 'checked';
		}else{
			if(-1 < itemNumber){
//				widget.userPref.todoList[itemNumber].checked = false;
				setTodoListParam(itemNumber, "checked", false);
			}
			itemNode.name = '';
		}
		
		var textId = checkBox.id.replace("check","text");
		var textNode = $( textId );
		this.changeTextChecked( textNode, checkBox.checked );
	}
	
	// Switch check mode of TODO text: checked or uncheked 
	this.changeTextChecked = function( textNode, check ){
		var text = textNode.firstChild;
		if(check){
			text.style.textDecoration = "line-through";
		}else{
			text.style.textDecoration = "none";
		}
	}
	
	// Delete TODO
	this.deleteItem = function( id ){
		var deleteButton = $( widgetId+'_'+id+'_del' );
		var deleteId = deleteButton.id.replace("del","item");
		var deleteNode = $( deleteId );
		var deleteNumber = self.getItemNumberFromId( deleteId );
		if(-1 < deleteNumber){
			var todoList = gadgets.json.parse( gadgets.util.unescapeString( prefs.getString("todoList")) );
			todoList.splice(deleteNumber, 1);
			prefs.set("todoList", gadgets.json.stringify( todoList) );
//			widget.userPref.todoList.splice(deleteNumber, 1);
		}
		
		this.unloadItemCache( id );
		var list = $( widgetId + '_list' );
		list.removeChild( deleteNode );
		
		if(!list.hasChildNodes())
			self.elm_todoListTable.style.display = "none";
		self.refreshItemStyle();
		gadgets.window.adjustHeight();
	}
	
	this.unloadItemCache = function( id ){
		Event.stopObserving( widgetId+'_'+id+'_item' );
		Event.stopObserving( widgetId+'_'+id+'_priority' );
		Event.stopObserving( widgetId+'_'+id+'_text' );
		Event.stopObserving( widgetId+'_'+id+'_check' );
		Event.stopObserving( widgetId+'_'+id+'_del' );
	}
	
	this.displayContents = function(){
		var todoList = gadgets.json.parse( gadgets.util.unescapeString( prefs.getString("todoList")) );
		for(var i=0; i<todoList.length; i++){
			this.makeItem( todoList[i] );
		}
//		EventDispatcher.newEvent('loadComplete', widgetId, true);
	}
	
	this.postEdit = this.displayContents;
	
	this.loadContents = function(){
		this.clearContents();
		if(prefs.getArray("todoList")){
			this.displayContents();
		}else{
//			EventDispatcher.newEvent('loadComplete', widgetId, true);
		}
		
		gadgets.window.adjustHeight();
	}
	
	this.loadContentsOption = {
		onSuccess : self.loadContents.bind(self)
	};
	
	this.clearContents = function(){
		//Event.unloadCache(widgetId);
		var list = $(widgetId + '_list');
		if(list && list.childNodes){
			var items = list.childNodes;
			for(var i=0; i<items.length; i++){
				var id = this.getItemId( items[i] );
				this.unloadItemCache( id );
			}
			
			for(var i=items.length-1; 0 <= i; i--){
				list.removeChild( items[i] );
			}
		}
	}
	
	this.close = function(e){
		this.clearContents();
	}
	
	// Get id from TODO node
	this.getItemId = function( item ){
		var start = (widgetId + '_').length;
		var end = item.id.indexOf( '_item' );
		return item.id.substring( start, end );
	}
	
	// Get item number of TODO from id; return -1 if it is not found
	this.getItemNumberFromId = function( id ){
		var list = $( widgetId + '_list' );
		
		var number = -1;
		for(var i=0; i<list.childNodes.length; i++){
			if(list.childNodes[i].id == id){
				number = i;
				break;
			}
		}
		
		return number;
	}
	
	// Refresh background color of each item
	this.refreshItemStyle = function(){
		var list = $(widgetId + '_list');
		if(list){
			var items = list.childNodes;
			var count = 1;
			for(var i=0; i<items.length; i++){
				if(/todoItemOdd*/.test(items[i].className)){
					if(count % 2 == 0){
						items[i].className = items[i].className.replace('Odd','Even');
					}
					count++;
				}else if(/todoItemEven*/.test(items[i].className)){
					if(count % 2 == 1){
						items[i].className = items[i].className.replace('Even','Odd');
					}
					count++;
				}
			}
		}
	}
	
	var todoItemDragging = false;
	this.floatItem = document.createElement("div");
	this.floatItem.className = 'todoFloatItem';
	this.floatItem.style.display = "none";
	document.body.appendChild(this.floatItem);
	this.dragTodoItem = function( itemNode, e ){
		var dummyItem;
		var dummyItemTr;
		var floatItemTbody;
		
		var itemLeftDiff;
		var itemTopDiff;
		
		var list = $( widgetId + '_list' );
		var beforeNumber = this.getItemNumberFromId( itemNode.id );
		if( beforeNumber == -1 ) return;
		
		self.removeSelectPriority();
		if(TodoList.todoEdit) return;
		
		function makeDummyItem(){
			dummyItem = document.createElement("tr");
			dummyItem.className = itemNode.className;
			
			var priorityTd = document.createElement("td");
			priorityTd.className = 'todoPriorityTd';
			var textTd = document.createElement("td");
			textTd.className = 'todoTextTd';
			var checkTd = document.createElement("td");
			checkTd.className = 'todoCheckTd';
			var deleteTd = document.createElement("td");
			deleteTd.className = 'todoDeleteTd';
			
			priorityTd.innerHTML = itemNode.childNodes[0].innerHTML;
			dummyItem.appendChild( priorityTd );
			textTd.innerHTML = itemNode.childNodes[1].innerHTML;
			dummyItem.appendChild( textTd );
			
			checkTd.innerHTML = itemNode.childNodes[2].innerHTML;
			
			dummyItem.appendChild( checkTd );
			deleteTd.innerHTML = itemNode.childNodes[3].innerHTML;
			dummyItem.appendChild( deleteTd );
		}
		
		function makeFloatItem(){
			
			self.floatItem.style.display = "block";
			self.floatItem.className = 'todoFloatItem';
			self.floatItem.style.width = itemNode.offsetWidth;
			var element = itemNode ;
			var scr = Position.realOffset( element );
			self.floatItem.style.top = findPosY( element ) -scr[1];
			self.floatItem.style.left = findPosX( element ) -scr[0];
			var table = document.createElement("table");
			table.className = 'todoListTable';
			table.style.width = itemNode.offsetWidth;
			self.floatItem.appendChild( table );
			floatItemTbody = document.createElement("tbody");
			table.appendChild( floatItemTbody );
		}
		
		start(e);
		
		function start(e){
			if ( todoItemDragging ) return;
			
			var mouseX = Event.pointerX(e);
			var mouseY = Event.pointerY(e);
			var element =itemNode;
			var scr = Position.realOffset( element );
			var itemLeft = findPosX( element ) -scr[0];
			var itemTop = findPosY( element ) -scr[1];
			itemLeftDiff = mouseX - itemLeft;
			itemTopDiff = mouseY - itemTop;
			
			makeDummyItem();
			makeFloatItem();
			
			itemNode.className = itemNode.className + 'Mark';
			floatItemTbody.appendChild( dummyItem );
			
			// filter: Layout breakes if the node specified as alpha is appended later on
//			document.body.appendChild( floatItem );
			
			Event.observe(document, "mousemove", dragging, false);
			Event.observe(document, "mouseup", dragEnd, false);
			
			todoItemDragging = true;
			
			// Stop event to be passed to upper level
			Event.stop(e);
			
		}
		
		function dragging(e) {
			if ( !todoItemDragging ) return;
			
			var mouseX = Event.pointerX(e);
			var mouseY = Event.pointerY(e);
			
			// Find insert location
			var floatTop = mouseY - itemTopDiff;
			var floatCenter = floatTop + (self.floatItem.offsetHeight/2);
			
			var items = list.childNodes;
			var insertNumber = -1;
			for(var i=0; i<items.length; i++){
				var item = items[i];
				var scr = Position.realOffset( item );
				
				var top = findPosY( item ) -scr[1];
				var center = top + (item.offsetHeight/2);
				if( floatCenter < center ){
					insertNumber = i;
					break;
				}
			}
			if(insertNumber == -1){
				if(itemNode.id != items[items.length-1].id){
					list.appendChild( itemNode );
					self.refreshItemStyle();
				}
			}else if(itemNode.id != items[insertNumber].id){
				list.insertBefore( itemNode, items[insertNumber] );
				self.refreshItemStyle();
			}
			
			if(Browser.isIE){
				// For trouble that cause IE to have the checkbox with the check removed after moving
				var afterNumber = self.getItemNumberFromId( itemNode.id );
				var checkbox = $( itemNode.id.replace('item','check') );
//				checkbox.checked = widget.userPref.todoList[ afterNumber ].checked;
				checkbox.checked = getTodoListParam(afterNumber, "checked");
			}
			
			
			//floatItem.style.left = mouseX - itemLeftDiff;
			self.floatItem.style.top = mouseY - itemTopDiff;
			
			// Stop event to be passed to upper level
			Event.stop(e);
			
		}
		
		function dragEnd(e) {
			Event.stopObserving(document, "mousemove", dragging, false);
			Event.stopObserving(document, "mouseup", dragEnd, false);
			
			if ( !todoItemDragging ) return;
			
			// Change order of userPref
			var insertNumber = self.getItemNumberFromId( itemNode.id );
			if(-1 < insertNumber && insertNumber != beforeNumber){
				var todoList = gadgets.json.parse( gadgets.util.unescapeString( prefs.getString("todoList")) );
				var itemObj = todoList[ beforeNumber ];
				
				todoList.splice( beforeNumber, 1);
				if(todoList.length <= insertNumber){
					todoList.push( itemObj );
				}else{
					todoList.splice( insertNumber, 0, itemObj );
				}
				
				prefs.set("todoList", gadgets.json.stringify( todoList) );
			}
			
			if(Browser.isIE){
				// For trouble that cause IE to have the checkbox with the check removed after moving
				var checkbox = $( itemNode.id.replace('item','check') );
//				checkbox.checked = widget.userPref.todoList[ insertNumber ].checked;
				checkbox.checked = getTodoListParam(insertNumber, "checked");
			}
			
			itemNode.className = dummyItem.className;
			dummyItem.parentNode.removeChild( dummyItem );
//			floatItem.parentNode.removeChild( floatItem );
			self.floatItem.style.display = "none";
			
			self.refreshItemStyle();
			
			todoItemDragging = false;
		}
	}
}

var instance = new TodoList();
instance.loadContents();