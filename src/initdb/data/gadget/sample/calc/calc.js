
var nowResult = 0;
var nowOperation = "";
var beforeValue = 0;
var beforeHandling = "clear";
var displayNode = $("display");
var operationNode = $("operation");

function numClick(num, e){
	if(beforeHandling == "operation"){
		nowResult = displayNode.value;
		displayNode.value = num;
	}else if(beforeHandling == "equal"){
		allClear();
		displayNode.value = num;
	}else if(displayNode.value == "" || displayNode.value == "0"){
		displayNode.value = num;
	}else{
		var newValue = displayNode.value + "" + num;
		displayNode.value = newValue;
	}
	beforeHandling = "setNum";
}

function dotClick(e){
	if(beforeHandling == "operation"){
		nowResult = displayNode.value;
		displayNode.value = "0.";
	}else if(beforeHandling == "equal"){
		allClear();
		displayNode.value += ".";
	}else if(displayNode.value == ""){
		displayNode.value += "0.";
	}else if(displayNode.value.indexOf(".") == -1){
		displayNode.value += ".";
	}
	beforeHandling = "setNum";
}
function displayKeyDown(e){
	
	var keycode = null;
	var ctrl = false;
	var alt = false;
	var shift = false;
	var keyc;
	// Mozilla(Firefox, NN) and Opera
	if (!Browser.isIE) {
		keycode = e.which;
		ctrl = typeof e.modifiers == 'undefined' ? e.ctrlKey : e.modifiers & Event.CONTROL_MASK;
		alt = typeof e.modifiers == 'undefined' ? e.altKey : e.modifiers & Event.ALT_MASK;
		shift = typeof e.modifiers == 'undefined' ? e.shiftKey : e.modifiers & Event.SHIFT_MASK;
	} else {
		keycode = event.keyCode;
		ctrl = event.ctrlKey;
		alt = event.altKey;
		shift = event.shiftKey;
	}
	
	keycode = parseInt( keycode );
	keychar = String.fromCharCode(keycode).toUpperCase(); 
	if(keycode == 107 || shift && (Browser.isIE? keycode == 187 : keycode == 61)){	// ten key：+, keyboard：shift and ;
		Event.stop(e);
		operationClick( "add" );
	}else if(!shift && (keycode == 109 || keycode == 189)){	// ten key：-, keyboard(IE)：-
		Event.stop(e);
		operationClick( "subtract" );
	}else if(keycode == 106 || shift && (Browser.isIE? keycode == 186 : keycode == 59)){	// ten key：*, keyboard：shift and :
		Event.stop(e);
		operationClick( "multiply" );
	}else if(keycode == 111 || keycode == 191){	// ten key：/, keyboard：/
		Event.stop(e);
		operationClick( "divide" );
	}else if(keycode == 13 || shift && (Browser.isIE? keycode == 189 : keycode == 109)){	// Enter
		Event.stop(e);
		equal();
	}else if(keycode == 110 || keycode == 190){	// ten key：.
		if(beforeHandling == "operation"){
			nowResult = displayNode.value;
			displayNode.value = ".";
			setTimeout( add0, 5);
		}else if(beforeHandling == "equal"){
			nowResult = 0;
			displayNode.value = ".";
			setOperation("");
			setTimeout( add0, 5);
		}else if(displayNode.value == ""){
			setTimeout( add0, 5);
		}else if(-1 < displayNode.value.indexOf(".")){
			Event.stop(e);
		}else{
			displayNode.value += ".";
		}
		beforeHandling = "setNum";
	}else if( (48 <= keycode && keycode <= 57) || (96 <= keycode && keycode <= 105) ){
		// from 0 to 9 at keyboard and ten key
		var keynum = (48 <= keycode && keycode <= 57)? keycode-48 : keycode-96;
		
		if(beforeHandling == "operation"){
			nowResult = displayNode.value;
			displayNode.value = keynum;
		}else if(beforeHandling == "equal"){
			nowResult = 0;
			displayNode.value = keynum;
			setOperation("");
		}else{
			var displayNum = "" + displayNode.value + keynum;
			// Discard 0 that is the first of the value
			displayNum = displayNum.replace(/^0*[^1-9.]/, "");
			displayNum = displayNum.replace(/^\./, "0.");
			if(displayNum.length == 0) displayNum = 0;
			displayNode.value = displayNum;
		}
		
		beforeHandling = "setNum";
	}else if( ((keycode == 32) || (keycode == 59) || (keycode == 61) || (65 <= keycode && keycode <=90)
			|| (186 <= keycode && keycode <= 192) || (219 <= keycode && keycode <= 222) || (keycode == 226))
			&& (!ctrl && !alt) ){
			// Alphabetical character, space, and special characters like ,.@[]
			// Not prevent double byte character from being input in FireFox; ime-mode and Event.stop(e) are both disabled
		Event.stop(e);
	}else if(!Browser.isIE && keycode == 229){
		// For input of double byte character in FireFox
		displayNode.blur();
	}else if(keycode == 46){	// del
		// Clear
		Event.stop(e);
		allClear();
	}
}

function displayOnFocus(e){
//	displayNode.select();
	displayNode.focus();
}

function add0(){
	displayNode.value = '0' + displayNode.value;
}

function operationClick(operation, e){
	if(nowOperation != "" && beforeHandling == "setNum"){
		var result = null;
		try{
			var value1 = Number(nowResult);
			var value2 = Number(displayNode.value);
			result = calculate(value1, value2, nowOperation);
		}catch(e){
			result = null;
		}
		if(result != null){
			displayNode.value = result;
			nowResult = result;
		}
	}
	setOperation(operation);
//	displayNode.select();
	displayNode.focus();
	beforeHandling = "operation";
}
function setOperation( operation ){
	nowOperation = operation;
	if(operation == "add"){
		operationNode.innerHTML = "+";
	}else if(operation == "subtract"){
		operationNode.innerHTML = "-";
	}else if(operation == "multiply"){
		operationNode.innerHTML = "*";
	}else if(operation == "divide"){
		operationNode.innerHTML = "/";
	}else{
		operationNode.innerHTML = "　";
	}
}
function equal(e){
	if(nowOperation == ""){ return; }
	var result = null;
	try{
		var value1 = Number(nowResult);
		var value2 = (beforeHandling == "equal")? beforeValue : Number(displayNode.value);
		result = calculate(value1, value2, nowOperation);
	}catch(e){
		result = null;
	}
	
	if(result != null){
		beforeValue = value2;
/*
		displayNode.value = result;
		nowResult = result;
*/
		displayNode.value = errorRemoval(result);
		nowResult = errorRemoval(result);
		
//		displayNode.select();
		displayNode.focus();
		beforeHandling = "equal";
	}
	
}

function errorRemoval(result) {
	var precision = result.toPrecision(14);
	var strPrecision = precision.toString();
	var index = strPrecision.indexOf(".");
	
	if ( index != -1) {
		strPrecision = strPrecision.replace(/0*$/,"");
		if (strPrecision == ""){
			strPrecision = "0";
		}
		strPrecision = strPrecision.replace(/[.]$/,"");
	}
	return strPrecision;
}

function calculate(value1, value2, operation){
	var result = null;
	try{
		if(operation == "add"){
			result = value1 + value2;
		}else if(operation == "subtract"){
			result = value1 - value2;
		}else if(operation == "multiply"){
			result = value1 * value2;
		}else if(operation == "divide"){
			result = value1 / value2;
		}
	}catch(e){
		result = null;
	}
	
	return result;
}

function clear(e){
	displayNode.value = 0;
//	displayNode.select();
	displayNode.focus();
	beforeHandling = "setNum";
}

function allClear(e){
	displayNode.value = 0;
	nowResult = 0;
	setOperation("");
//	displayNode.select();
	displayNode.focus();
	beforeHandling = "clear";
}

function init() {
	for(var i=0; i < 10; i++){
		var numButton = $("num"+i+"");
		Event.observe(numButton, "click", numClick.bind(this, i), false, "" );
	}
	var dotButton = $("dot");
	Event.observe(dotButton, "click", dotClick, false, "" );
	var addButton = $("add");
	Event.observe(addButton, "click", operationClick.bind(this, "add"), false, "" );
	var subtractButton = $("subtract");
	Event.observe(subtractButton, "click", operationClick.bind(this, "subtract"), false, "" );
	var multiplyButton = $("multiply");
	Event.observe(multiplyButton, "click", operationClick.bind(this, "multiply"), false, "" );
	var divideButton = $("divide");
	Event.observe(divideButton, "click", operationClick.bind(this, "divide"), false, "" );
	var equalButton = $("equal");
	Event.observe(equalButton, "click", equal, false, "" );
	/*
	var clearButton = $("clear");
	Event.observe(clearButton, "click", clear, false, "" );
	*/
	var allClearButton = $("allClear");
	Event.observe(allClearButton, "click", allClear, false, "" );
	
	Event.observe(displayNode, "focus", displayOnFocus, false, "" );
	
	Event.observe(displayNode, "click", function(){displayNode.style.border='1px solid #FF6633';}, false, "" );
	Event.observe(displayNode, "blur", function(){displayNode.style.border='1px solid gray';}, false, "" );
	Event.observe(displayNode, "keydown", displayKeyDown, false, "" );
}
gadgets.window.adjustHeight();

init();
