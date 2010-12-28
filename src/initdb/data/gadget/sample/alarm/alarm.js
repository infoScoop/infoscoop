
var prefs = new gadgets.Prefs();
prefs.setDontEscape_();

var table = $("table__WIDGET_ID__");
var smallDiv = $("small");
var titleDiv= $("title");
var timeDiv= $("time");

var alarmFunc = {
	alert : function() {

		alert( replaceAll( prefs.getMsg("message_alert"),{
				title: prefs.getString("title")
			}) );
	},
	shake : function() {
		var count = 10;
		var offset = 10;
		var counter = 0;
		function shakeWindow() {
			parent.moveBy(offset, 0);
			counter++;
			offset = offset * -1;
			if(counter < count) {
				setTimeout(shakeWindow, 50);
			}
		};
		shakeWindow();
	},
	nothing : function() {}
};
function init() {
	if( !validate() )
		return;
	
	gadgets.window.setTitle( replaceAll( prefs.getMsg("alarm_of"),{
		title: prefs.getString("title")
	}));
	
	try{
		var ymd = prefs.getString("ymd" );
		if( !ymd || ymd == "") {
			// Take over property of old version and convert
			ymd = prefs.getString("year")+"/"+
				prefs.getString("month")+"/"+
				prefs.getString("day");
			
			var ymdDate = new Date( ymd );
			if( !isNaN( ymdDate.getTime()) ) {
				prefs.set("ymd",ymd );
			} else {
				ymd = null;
			}
		}
		
		var date = new Date( ymd );
		if( !ymd || isNaN( date.getTime()) )
			throw "Illegal YMD format";
		
		date.setHours( toNumber(prefs.getString("hour")) );
		date.setMinutes( toNumber(prefs.getString("minute")) );
		date.setSeconds( 0 );
		date.setMilliseconds( 0 );
		
		this.date = date;
	} catch( e ) {
		this.date = null;
		//console.log(e);
		handleError();
		return;
	}
	
	if( this.date ) {
		var now = new Date();
		if( this.date.getTime() - now.getTime() > 0 ) {
			smallDiv.innerHTML = 
				replaceAll( prefs.getMsg("message_xday"),{
					date: formatDate( this.date )
				});
			
			titleDiv.appendChild( document.createTextNode(

				replaceAll( prefs.getMsg("message_countdown"),{
						title: prefs.getString("title")
					} ) ));
			checkTime();
		} else {
			alarm();
			//titleDiv.innerHTML = "PastDateIsSet";
		}
	} else {
		handleError();
	}
	
	gadgets.window.adjustHeight();
}
function checkTime() {
	if(this.timer) clearTimeout(this.timer);
	var now = new Date();
	var offset = this.date.getTime() - now.getTime();
	if(offset <= 0) {
		alarm();
	} else {
		var second = offset/1000;
		var minute = second/60;
		second = to2digits(Math.floor(second%60));
		var hour = minute/60;
		minute = to2digits(Math.floor(minute%60));
		var day = hour/24;
		hour = to2digits(Math.floor(hour%24));
		day = Math.floor(day);
		timeDiv.innerHTML = replaceAll( prefs.getMsg("message_displayTime"),{
			day: day,
			hour: hour,
			minute: minute,
			second: second })
		this.timer = setTimeout(checkTime, 1000);
	}
}
function formatDate(d) {
	var year = d.getFullYear();
	var month = to2digits(d.getMonth() + 1);
	var day = to2digits(d.getDate());
	var hour = to2digits(d.getHours());
	var minute = to2digits(d.getMinutes());
	
	return year +"/" +month +"/" +day +" " +hour +":" +minute;
}
function handleError() {
	if( this.timer )
		clearTimeout(this.timer);
	
	titleDiv.innerHTML = prefs.getMsg("message_invalidate");
	
	gadgets.window.adjustHeight();
}
function toNumber(s) {
	if(typeof s == "number") {
		return s;
	} else if( s == "") {
		return 0;
	}
	
	if(s.match(/^[0-9]+$/)) {
		if(s.length > 1 && s.indexOf("0") == 0)
			s = s.replace(/^[0]+/, "");
		if(!s) return 0;
		return parseInt(s);
	} else {
		throw new Error(s + " is not Number");
	}
};
function to2digits(i) {
	return i > 9 ? "" + i : "0" + i;
};
function alarm() {
	titleDiv.innerHTML = "";
	timeDiv.innerHTML = "";
	titleDiv.appendChild( document.createTextNode(
		replaceAll( prefs.getMsg("message_alert"),{
			title: prefs.getString("title")}) ));
	
	timeDiv.innerHTML = formatDate( this.date );
	
	if( !prefs.getBool("alarmed")) {
		alarmFunc[prefs.getString("method")]();
		
		Element.addClassName( document.body,"alarmed");
		
		prefs.set("alarmed", true);
	}
	
	gadgets.window.adjustHeight();
}

function replaceAll( msg,param ) {
	if( !msg )
		return "";
	
	for( var i in param ) if( param.hasOwnProperty( i )) {
		msg = msg.replace( new RegExp("\\$\\{"+i+"\\}","g"),param[i] );
	}
	
	return msg;
}

function validate() {
	var errors = [];
	errors.push( Validate.hour( prefs.getString("hour")));
	errors.push( Validate.minute( prefs.getString("minute")));
	
	errors = errors.compact();
	if( errors.length == 0 )
		return true;
	
	var message = document.createElement("div");
	message.className = "message";
	message.innerHTML = prefs.getMsg("message_invalidate");
	titleDiv.appendChild( message );
	
	for( var i=0;i<errors.length;i++ ) {
		var div = document.createElement("div");
		div.className = "error";
		div.appendChild( document.createTextNode( errors[i] ));
		
		titleDiv.appendChild( div );
	}
	
	gadgets.window.adjustHeight();
	
	gadgets.window.setTitle( prefs.getMsg("title"));
	
	return false;
}

Validate = {
	hour: function(value){
		if(!/^(0?\d|1\d|2[0-3])$/.test(value))
			return prefs.getMsg("hour_error");
	},
	minute: function(value){
		if(!/^(0?\d|[1-5]\d)$/.test(value))
			return prefs.getMsg("minute_error");
	}
}

init();
