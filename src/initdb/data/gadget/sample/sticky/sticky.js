
var prefs = new gadgets.Prefs();
prefs.setDontEscape_();

var fontSize = prefs.getInt("fontSize");
if( isNaN( fontSize ))
	fontSize = 12;

var textBody = $("editor");
Event.observe( textBody, "keyup", setRows );
Event.observe( textBody, "blur", saveSticky );
Event.observe( window, "resize", setCols );

displaySticky();

function displaySticky() {
	var sticky_data = prefs.getString("sticky_data");
	
	var specials = {
		"/":"/",
		r: "\r",
		n: "\n",
		t: "\t"
	}
	var results = [];
	for( var i=0;i<sticky_data.length;i++ ) {
		var c = sticky_data.charAt(i);
		if( c == "/") {
			var c2 = sticky_data.charAt(i+1);
			var special = specials[c2];
			if( special ) {
				c = special;
				i++;
			}
		}
		
		results.push( c );
	}
	sticky_data = results.join("");
	
	textBody.value = gadgets.util.unescapeString( sticky_data );
	
	setCols();
}

function saveSticky() {
	var text = $("editor").value;
	// Replace texts because line break becomes empty character in xml
	text = text.replace(/\//g,"//");
	text = text.replace(/\r/g,"/r");
	text = text.replace(/\n/g,"/n");
	text = text.replace(/\t/g,"/t");
	
	prefs.set("sticky_data", text);
}
function setCols() {
	var textarea = $("editor");
	textarea.setAttribute("cols", Math.floor( textarea.clientWidth / fontSize));
	setRows();
}

function setRows() {
	var textarea = $("editor");
	var colNum = 2;
	var chars = 0;

	var cols = textarea.cols;
	var areaValue = textarea.value;
	areaValue = areaValue.replace(/\r\n?/, "\n");
	for (i = 0; i < areaValue.length; i++) {
		var c = areaValue.charAt(i);
		chars++;
		if (c == "\n" || chars == cols) {
			colNum ++;
			chars = 0;
		}
	}
	textarea.rows = colNum;
	textarea.style.height = ( colNum *fontSize ) + "px";
	
	gadgets.window.adjustHeight();
}