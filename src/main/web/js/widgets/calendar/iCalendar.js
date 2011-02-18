/* infoScoop OpenSource
 * Copyright (C) 2010 Beacon IT Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 */

IS_Widget.Calendar.iCalendar = IS_Class.create();
IS_Widget.Calendar.iCalendar.eventIdNum = 0;
IS_Widget.Calendar.iCalendar.prototype.classDef = function() {
	var NS_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	var eventTargetId = "[" + (++IS_Widget.Calendar.iCalendar.eventIdNum) + "]";
	
	/**
	 * @param urlStr: URL that gets ical
	 * @param format: specifies url; ical, CalDAV, or Feed
	 * @param headers: array of header that is sent when getting ical
	 */
	this.initialize = function(urlStr, _format, _authHeaders, _setAuthCredentialCallBack){
		this.url = urlStr;
		eventTargetId += this.url;
		if(_authHeaders){
			this.authHeaders = [].concat(_authHeaders);
		}
		//console.log([urlStr, _headers]);
		this.format = _format;
		this.events = [];
		this.nonRecurringEvents = new Array();
		this.recurringEvents = [];
		this.computedReccuringEvents;
		this.isLoaded = false;
		this.setAuthCredentialCallBack = _setAuthCredentialCallBack;
	}
	
	this.getEventTarget = function() {
		return {type:"loadCalendarComplete", id:eventTargetId};
	};
	
	this.loadCalendar = function(response) {
		var xml = response.responseXML;
		if(xml && xml.childNodes.length > 0) {
			var base_node;
			for ( var i=0; i<xml.childNodes.length; i++ ) {
				if (xml.childNodes[i].tagName) {
					base_node = xml.childNodes[i];
					break;
				}
			}
			
			var statusCode = base_node.getAttribute('statusCode');
			var jsonObj = (base_node.firstChild) ? base_node.firstChild.nodeValue : "";
			
			if (statusCode == 0) {
				try {
					this.events = eval('(' + jsonObj + ')');
					this.nonRecurringEvents = new Array();
					this.recurringEvents = [];
					this.computedReccuringEvents;
					this.sortEvents();
				} catch (e) {
					throw e;
				}
			} else {
				throw jsonObj;
			}
			this.isLoaded = true;
			IS_EventDispatcher.newEvent("loadCalendarComplete", eventTargetId, null);
		}else{
			var _authType = response.getResponseHeader("MSDPortal-AuthType");
			if(_authType){
				this.authType = _authType;
				var opt = {
				  method: 'post',
				  asynchronous: true,
				  parameters: { command: "try",url: this.url },
				  onSuccess:function(req, obj){
					  var credentialId = req.responseText;
					  if(new RegExp("[0-9]+").test(credentialId)){
						  var headers = [];
						  this.authCredentialId = credentialId;
						  headers.push("authCredentialId");
						  headers.push(credentialId);
						  if (this.authHeaders)
							  this.authHeaders.concat(headers);
						  else 
							  this.authHeaders = [].concat(headers);
						  this.setAuthCredentialCallBack(credentialId);
						  this.load();
					  }else{
						  IS_EventDispatcher.newEvent("loadCalendarComplete", eventTargetId, null);
					  }
				  }.bind(this),
				  onException:function(req, obj){
					  console.log(obj);
				  }
				}
				if( _authType ) opt.parameters.authType = _authType;
				
				AjaxRequest.invoke(hostPrefix + "/credsrv", opt, this.id);
			}else{
				IS_EventDispatcher.newEvent("loadCalendarComplete", eventTargetId, null);
			}
		}
	};

	/**
	 * @param
	 * {
	 * startdate:Date Object,
	 * enddate:Date Object,
	 * asynchronous: true|false
	 * }
	 */
	this.load = function(_opt) {
		_opt = _opt || {};
		
		var asynchronous = _opt.asynchronous;
		if(typeof asynchronous == "undefined"){
			asynchronous = true;
		}
			
		var requestUrl = this.url;
		var headers = [];
		if (this.authHeaders) {
			headers = headers.concat(this.authHeaders);
		}
		//?
		if(_opt.startdate != null && _opt.enddate != null){
			headers.push("X-IS-STARTDATE",  formatDate(_opt.startdate, "yyyyMMdd"));
			requestUrl = requestUrl.replace('${startDate}', formatDate(_opt.startdate, "yyyyMMdd"));
			headers.push("X-IS-ENDDATE",  formatDate( _opt.enddate, "yyyyMMdd"));
			requestUrl = requestUrl.replace('${endDate}',formatDate( _opt.enddate, "yyyyMMdd"));
		}
		var methodName = 'get';
		var reportBody = '';
		if(this.format == 'CalDAV'){
			headers.push("MSDPortal-method", "REPORT");
			reportBody ='<?xml version="1.0"?>';
			reportBody += '<C:calendar-query xmlns:D="DAV:" xmlns:C="urn:ietf:params:xml:ns:caldav">';
			reportBody += '<D:prop>';
			reportBody += '<C:calendar-data>';
			reportBody += '</C:calendar-data>';
			reportBody += '</D:prop>';
			reportBody += '<C:filter>';
			reportBody += '<C:comp-filter name="VCALENDAR">';
			reportBody += '<C:comp-filter name="VEVENT">';
			
			if(_opt.startdate != null && _opt.enddate != null) {
				//Consider Timezone
				var offsetMS = _opt.startdate.getTimezoneOffset() * 60 * 1000;
				var ds = new Date(_opt.startdate.getFullYear(),
						_opt.startdate.getMonth(),
						_opt.startdate.getDate()) ;
				ds.setTime(ds.getTime() + offsetMS);
				
				var de = new Date(_opt.enddate.getFullYear(),
						_opt.enddate.getMonth(),
						_opt.enddate.getDate());
				de.setTime(de.getTime() + offsetMS);
				reportBody += '<C:time-range start="'+formatDate(ds, "yyyyMMddTHHmmssZ")+'" end="'+formatDate(de, "yyyyMMddTHHmmssZ")+'"/>';
			}
			reportBody += '</C:comp-filter>';
			reportBody += '</C:comp-filter>';
			reportBody += '</C:filter>';
			reportBody += '</C:calendar-query>';
			methodName = 'post';
		}
		
		var opt = {
			method: methodName,
			asynchronous: asynchronous,
			contentType: "application/xml",
			requestHeaders: headers,
			retryCount : 2,
			postBody: reportBody,
			onSuccess: this.loadCalendar.bind(this),
			on304: function(){
				IS_EventDispatcher.newEvent("loadCalendarComplete", eventTargetId, null);
			},
			on403: function(){
				
				msg.error(IS_R.getResource(IS_R.ms_iCalNoPermission, [requestUrl]));
			},
			on10408: function(t,e) {
				
				msg.error( IS_R.getResource( IS_R.ms_iCalLoadTimeout,[requestUrl] ));
			},
			onFailure: function(t) {
				
				msg.error( IS_R.getResource( IS_R.ms_iCalLoadFailed,[requestUrl,t.status,t.statusText] ));
			},
			onException: function(r, t){
				
				msg.error( IS_R.getResource( IS_R.ms_iCalParseFailed,[requestUrl,getText(t)] ));
			},
		    onComplete: function(req, obj){
				IS_EventDispatcher.newEvent("loadCalendarComplete", eventTargetId, null);
			}
		};
		AjaxRequest.invoke(is_getProxyUrl(requestUrl, "Calendar"), opt);
	};
	
	this.parseText = function(icstxt) {
		var br = "\r\n";
		if(icstxt.indexOf(br) < 0){
			br = "\n";
		}
		var icsclms = icstxt.split(br);
		var event;
		for(var i=0; i<icsclms.length; i++){
			if(icsclms[i].indexOf("BEGIN:VEVENT") == 0){
				event = new Object();
				isEvent = true;
			} else if(icsclms[i].indexOf("END:VEVENT") == 0){
				this.events.push(event);
				event = null;
			} else if (event) {
				var idx = icsclms[i].indexOf(":");
				if(idx < 0 || icsclms[i].length -1 == idx) continue;
				var token0 = icsclms[i].substring(0, idx);
				var token1 = icsclms[i].substring(idx + 1);
				if(token0.indexOf("DTSTART") == 0) {
					event.dtstart = token1;
				} else if(token0.indexOf("DTEND") == 0) {
					event.dtend = token1;
				} else if(token0.indexOf("SUMMARY") == 0) {
					var next = getNextColumn(icsclms, i + 1);
					event.summary = token1 + next.text;
					i = next.index;
				} else if(token0.indexOf("DESCRIPTION") == 0) {
					var next = getNextColumn(icsclms, i + 1);
					token1 = IS_Widget.Calendar.iCalendar.encode(token1 + next.text);
					event.description = token1;
					i = next.index;
				} else if(token0.indexOf("LOCATION") == 0) {
					var next = getNextColumn(icsclms, i + 1);
					token1 = token1 + next.text;
					event.location = token1;
					i = next.index;
				} else if(token0.indexOf("URL") == 0) {
					var next = getNextColumn(icsclms, i + 1);
					event.url = token1 + next.text;
					i = next.index;
				} else if(token0.indexOf("RRULE") == 0) {
					event.rrule = new Object();
					var next = getNextColumn(icsclms, i + 1);
					token1 = token1 + next.text;
					i = next.index;
					var rules = token1.split(";");
					for(var j=0; j<rules.length; j++) {
						var rule = rules[j].split("=");
						var name = rule[0].toLowerCase();
						var value = rule[1];
						if(!(name == "freq" || name == "interval" || name == "until")) {
							value = value.split(",");
						}
						event.rrule[name] = value;
					}
				}
			}
			
		}
		
		function getNextColumn(icsclms, idx) {
			var i = idx;
			var txt = "";
			for(; i<icsclms.length;i++) {
				if(icsclms[i].length <= 1) break;
				var ch = icsclms[i].charAt(0);
				if(ch == " " || ch == "\t") {
					txt += icsclms[i].substring(1);
				} else {
					break;
				}
			}
			return {index:i-1, text:txt}
		}
	};
	
	
	this.sortEvents = function() {
		for(var i=0;i<this.events.length;i++){
			if(this.events[i].rrule){
				this.recurringEvents.push(this.events[i]);
			} else {
//				var dateString = dtstart.date.getFullYear() + "/" + (dtstart.date.getMonth() + 1) + "/" + dtstart.date.getDate();
				var dtStartStr = this.events[i].dtstart;
				if(!this.nonRecurringEvents[dtStartStr])
					this.nonRecurringEvents[dtStartStr] = new Array();
				this.nonRecurringEvents[dtStartStr].push(this.events[i]);
				if(dtStartStr != this.events[i].dtend){
					var dtStartDate = new Date(this.events[i].dtstart + ' 00:00:00');
					var dtEndDate = new Date(this.events[i].dtend + ' 00:00:00');
					if(!this.events[i].dtendTime || this.events[i].dtendTime == "00:00")
					  dtEndDate.setDate(dtEndDate.getDate() - 1);
					
					while(dtStartDate < dtEndDate ){
						dtStartDate.setDate(dtStartDate.getDate() + 1);
						var dtStartDateStr = IS_Widget.Calendar.iCalendar.getDateString(dtStartDate);
						if(!this.nonRecurringEvents[dtStartDateStr])
						  this.nonRecurringEvents[dtStartDateStr] = new Array();
						this.nonRecurringEvents[dtStartDateStr].push(this.events[i]);
					}
				}
			}
		}
	}
	
	this.computeEvents = function(year, month) {
		var computedEvents = [];
		for(var i=0; i<this.recurringEvents.length; i++) {
			var rrule = this.recurringEvents[i].rrule;
			var dtstart = this.recurringEvents[i].dtstart.date;
			var dates = IS_Widget.Calendar.iCalendar.parseRrule(dtstart, rrule, year, month);
			for(var j=0; j<dates.length; j++) {
				var dateString = dates[j].getFullYear() + "/" + (dates[j].getMonth() + 1) + "/" + dates[j].getDate();
				if(!computedEvents[dateString])
					computedEvents[dateString] = new Array();
				computedEvents[dateString].push(this.recurringEvents[i]);
			}
		}
		this.computedReccuringEvents = computedEvents;
	};
		
	this.getEvent = function(dateObj) {
		var dateString;
		var month = ((dateObj.getMonth()+1) < 10) ? "0" + (dateObj.getMonth() + 1) : dateObj.getMonth() + 1;
		var date = (dateObj.getDate() < 10) ? "0" + dateObj.getDate() : dateObj.getDate();
		dateString = dateObj.getFullYear() + "/" + month + "/" + date;
		var nonrec = [];
		var rec = [];
		if(this.nonRecurringEvents[dateString])
			nonrec = this.nonRecurringEvents[dateString];
		if(this.computedReccuringEvents[dateString])
		  rec = this.computedReccuringEvents[dateString];
		return  rec.concat(nonrec);
	};
	
	//this.load();
}

IS_Widget.Calendar.iCalendar.getDateString = function (dateObj){
	var dateString;
	var month = ((dateObj.getMonth()+1) < 10) ? "0" + (dateObj.getMonth() + 1) : dateObj.getMonth() + 1;
	var date = (dateObj.getDate() < 10) ? "0" + dateObj.getDate() : dateObj.getDate();
	dateString = dateObj.getFullYear() + "/" + month + "/" + date;
	return dateString;
}


/**
 * sort event lists gotten in getEvent by date
 */
IS_Widget.Calendar.iCalendar.sortEvent = function (eventList){
	var sortFunc = function(a, b){
		if(!a.dtstartTime && !b.dtstartTime){
			if(!a.dtendTime && !b.dtendTime) return -1;
			if(a.dtendTime && !b.dtendTime) return 1;
			if(!a.dtendTime && b.dtendTime) return -1;
			if(a.dtendTime < b.dtendTime) return -1;
			return 1;
		}else if(a.dtstartTime && !b.dtstartTime){
			return 1;
		}else if(!a.dtstartTime && b.dtstartTime){
			return -1;
		}else{
			if(a.dtstartTime < b.dtstartTime) return -1;
			return 1;
		}
	}
	eventList.sort( sortFunc );
}

IS_Widget.Calendar.iCalendar.formatDateISO8601 = function(dateString) {
	var comps = dateString.split('T');
	var dateObject = new Date(0);
	var hasTime = false;
	setISO8601Date(dateObject, comps[0]);
	if (comps.length == 2) {
		setISO8601Time(dateObject, comps[1]);
		hasTime = true;
	}
	return {date:dateObject, hasTime:hasTime};
	
	function setISO8601Date(dateObject, string) {
		var regexp = "^([0-9]{4})((-?([0-9]{2})(-?([0-9]{2}))?)|" +
				"(-?([0-9]{3}))|(-?W([0-9]{2})(-?([1-7]))?))?$";
		var d = string.match(new RegExp(regexp));
	
		var year = d[1];
		var month = d[4];
		var date = d[6];
		var dayofyear = d[8];
		var week = d[10];
		var dayofweek = (d[12]) ? d[12] : 1;
	
		dateObject.setYear(year);
		
		if (dayofyear) { IS_Widget.Calendar.iCalendar.setDayOfYear(dateObject, Number(dayofyear)); }
		else if (week) {
			dateObject.setMonth(0);
			dateObject.setDate(1);
			var gd = dateObject.getDay();
			var day =  (gd) ? gd : 7;
			var offset = Number(dayofweek) + (7 * Number(week));
			
			if (day <= 4) { dateObject.setDate(offset + 1 - day); }
			else { dateObject.setDate(offset + 8 - day); }
		} else {
			if (month) { 
				dateObject.setDate(1);
				dateObject.setMonth(month - 1); 
			}
			if (date) { dateObject.setDate(date); }
		}
	}
	
	function setISO8601Time(dateObject, string) {
		// first strip timezone info from the end
		var timezone = "Z|(([-+])([0-9]{2})(:?([0-9]{2}))?)$";
		var d = string.match(new RegExp(timezone));
	
		var offset = 0; // local time if no tz info
		if (d) {
			if (d[0] != 'Z') {
				offset = (Number(d[3]) * 60) + Number(d[5]);
				offset *= ((d[2] == '-') ? 1 : -1);
			}
			offset -= dateObject.getTimezoneOffset()
			string = string.substr(0, string.length - d[0].length);
		}
	
		// then work out the time
		var regexp = "^([0-9]{2})(:?([0-9]{2})(:?([0-9]{2})(\.([0-9]+))?)?)?$";
		var d = string.match(new RegExp(regexp));
	
		var hours = d[1];
		var mins = Number((d[3]) ? d[3] : 0) + offset;
		var secs = (d[5]) ? d[5] : 0;
		var ms = d[7] ? (Number("0." + d[7]) * 1000) : 0;
	
		dateObject.setHours(hours);
		dateObject.setMinutes(mins);
		dateObject.setSeconds(secs);
		dateObject.setMilliseconds(ms);
		
		return dateObject;
	}
}

IS_Widget.Calendar.iCalendar.encode = function (str) {
	str = str.replace(/\\n/ig, "<br>");
	str = str.replace(/\\\\/ig, "\\");
	str = str.replace(/\\;/ig, ";");
	str = str.replace(/\\,/ig, ",");
	return str;
}

IS_Widget.Calendar.iCalendar.setDayOfYear = function (dateObject, dayofyear) {
	dateObject.setMonth(0);
	dateObject.setDate(dayofyear);
	return dateObject;
}

IS_Widget.Calendar.iCalendar.getDaysInMonth = function (dateObject) {
	var month = dateObject.getMonth();
	var days = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
	if (month == 1 && IS_Widget.Calendar.iCalendar.isLeapYear(dateObject)) { return 29; }
	else { return days[month]; }
}

/**
	Get text for the date of the event for showing
	@param date: date for showing
	@param event: event data that gets date
	@return: text that is created
*/
IS_Widget.Calendar.iCalendar.getTimeText = function ( date, event) {
	date = new Date(date.getFullYear(), date.getMonth(), date.getDate());
	
	var timeText = '';
	
	var dtstart = event.dtstart;
	var dtend = event.dtend;
	var dtendTime = (event.dtendTime && event.dtendTime != "00:00") ? event.dtendTime : '';
	
	/*
	The format below is usual for the all day event at iCalender
	DTSTART:20071202
	DTEND:20071203
	*/
	var overDateEvent = false;
	var tmpStartDate = new Date(event.dtstart + ' 00:00:00');
	if(event.dtend){
		var tmpEndDate = new Date(event.dtend + ' 00:00:00');
		//if(event.dtstart != event.dtend && !event.dtendTime){
		//	tmpStartDate.setDate( tmpStartDate.getDate() + 1 );
		//}
		overDateEvent = (tmpStartDate.getDate() != tmpEndDate.getDate()
						|| tmpStartDate.getMonth() != tmpEndDate.getMonth()
						|| tmpStartDate.getYear() != tmpEndDate.getYear() );
		
		if(!event.dtendTime || event.dtendTime == "00:00"){
			tmpEndDate.setDate((tmpEndDate.getDate()-1));
			dtend = IS_Widget.Calendar.iCalendar.getDateString(tmpEndDate);
		}
		if(tmpStartDate.getFullYear() == tmpEndDate.getFullYear()){
			dtstart = dtstart.substr(5);
			dtend = dtend.substr(5);
		}
	}
	
	var startText = "";
	if( tmpStartDate.getTime() == date.getTime()){
		if(event.dtstartTime.length > 0 && event.dtstartTime != "00:00") {
			startText = event.dtstartTime;
		} else {
			startText = overDateEvent ? "&nbsp;" : "00:00";
		}
	} else {
		startText = dtstart;
	}
	
	var endText = "";
	
	var tmpEndDate = new Date(event.dtend + ' 00:00:00');
	if (dtend && dtendTime.length <= 0 || dtendTime == "00:00") {
		dtendTime = "";
		
		tmpEndDate.setDate(tmpEndDate.getDate() - 1);
	}
	
	if(tmpEndDate.getTime() == date.getTime()){
		if( dtend && dtendTime.length > 0) {
			endText = dtendTime;
		} else {
			endText = overDateEvent ? "&nbsp;" : "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		}
	} else {
		endText = dtend;
	}
	
	timeText = startText+" - "+endText+" ";
	if (timeText == "&nbsp;" + " - " + "&nbsp;" + " ")
		timeText = dtstart + " ";
	return timeText;
}

IS_Widget.Calendar.iCalendar.isLeapYear = function (dateObject) {
	/*
	 * Leap years are years with an additional day YYYY-02-29, where the year
	 * number is a multiple of four with the following exception: If a year
	 * is a multiple of 100, then it is only a leap year if it is also a
	 * multiple of 400. For example, 1900 was not a leap year, but 2000 is one.
	 */
	var year = dateObject.getFullYear();
	return (year%400 == 0) ? true : (year%100 == 0) ? false : (year%4 == 0) ? true : false;
}

IS_Widget.Calendar.iCalendar.parseRrule = function(dtstart, rrule, year, month) {
	var recurranceSet = [];
	var weekdays=["su","mo","tu","we","th","fr","sa"];
	var order = { 
		"daily": 1, "weekly": 2, "monthly": 3, "yearly": 4,
		"byday": 1, "bymonthday": 1, "byweekno": 2, "bymonth": 3, "byyearday": 4};

	var freq = rrule.freq.toLowerCase();
	var interval = 1;

	if (rrule.interval > interval) {
		interval = rrule.interval;
	}

	var set = [];
	var freqInt = order[freq];

	var until = new Date(year, month, 1);
	if (rrule.until) {
		var tmpUntil = IS_Widget.Calendar.iCalendar.formatDateISO8601(rrule.until);
		if(tmpUntil < until)
			until = tmpUntil;
	}

	if(dtstart > tmpUntil) {
		return;
	}

	var startdate = new Date(year, month - 1, 1);

	var expandingRules = function(){};
	var cullingRules = function(){};
	expandingRules.length=0;
	cullingRules.length =0;

	switch(freq) {
		case "yearly":
			nextDate = new Date(dtstart);
			while(nextDate < until) {
				tmpDate = new Date(nextDate);
				if(tmpDate < until && tmpDate >= startdate) {
					set.push(tmpDate);
				}
				nextDate.setYear(nextDate.getFullYear()+interval);
			}
			break;
		case "monthly":
			nextDate = new Date(dtstart);
			while(nextDate < until) {
				var tmpDate = new Date(nextDate);
				if (tmpDate < until && tmpDate >= startdate) {
					set.push(tmpDate);
				}
				nextDate.setMonth(nextDate.getMonth()+interval);
			}
			break;
		case "weekly":
			nextDate = new Date(dtstart);
			while(nextDate < until) {
				var tmpDate = new Date(nextDate);
				if (tmpDate < until && tmpDate >= startdate) {
					set.push(tmpDate);
				}
				nextDate.setDate(nextDate.getDate()+(7*interval));
			}
			break;	
		case "daily":
			nextDate = new Date(dtstart);
			while(nextDate < until) {
				var tmpDate = new Date(nextDate);
				if (tmpDate < until && tmpDate >= startdate) {
					set.push(tmpDate);
				}
				nextDate.setDate(nextDate.getDate()+interval);
			}
			break;

	}

	if ((rrule["bymonth"]) && (order["bymonth"]<freqInt))	{
		for (var z=0; z<rrule["bymonth"].length; z++) {
			if (z==0) {
				for (var zz=0; zz < set.length; zz++) {
					set[zz].setMonth(rrule["bymonth"][z]-1);
				}
			} else {
				var subset=[];
				for (var zz=0; zz < set.length; zz++) {
					var newDate = new Date(set[zz]);
					newDate.setMonth(rrule[z]);
					subset.push(newDate);
				}
				tmp = set.concat(subset);
				set = tmp;
			}
		}
	}

	
	// while the spec doesn't prohibit it, it makes no sense to have a bymonth and a byweekno at the same time
	// and if i'm wrong then i don't know how to apply that rule.  This is also documented elsewhere on the web
	if (rrule["byweekno"] && !rrule["bymonth"]) {	
		//TODO: no support for byweekno yet
	}


	// while the spec doesn't prohibit it, it makes no sense to have a bymonth and a byweekno at the same time
	// and if i'm wrong then i don't know how to apply that rule.  This is also documented elsewhere on the web
	if (rrule["byyearday"] && !rrule["bymonth"] && !rrule["byweekno"] ) {	
		if (rrule["byyearday"].length > 1) {
			var regex = "([+-]?)([0-9]{1,3})";
			for (var z=1; x<rrule["byyearday"].length; z++) {
				var regexResult = rrule["byyearday"][z].match(regex);
				if (z==1) {
					for (var zz=0; zz < set.length; zz++) {
						if (regexResult[1] == "-") {
							IS_Widget.Calendar.iCalendar.setDayOfYear(set[zz],366-regexResult[2]);
						} else {
							IS_Widget.Calendar.iCalendar.setDayOfYear(set[zz],regexResult[2]);
						}
					}
				}	else {
					var subset=[];
					for (var zz=0; zz < set.length; zz++) {
						var newDate = new Date(set[zz]);
						if (regexResult[1] == "-") {
							IS_Widget.Calendar.iCalendar.setDayOfYear(newDate,366-regexResult[2]);
						} else {
							IS_Widget.Calendar.iCalendar.setDayOfYear(newDate,regexResult[2]);
						}
						subset.push(newDate);
					}
					tmp = set.concat(subset);
					set = tmp;
				}
			}
		}
	}

	if (rrule["bymonthday"]  && (order["bymonthday"]<freqInt)) {	
		if (rrule["bymonthday"].length > 0) {
			var regex = "([+-]?)([0-9]{1,3})";
			for (var z=0; z<rrule["bymonthday"].length; z++) {
				var regexResult = rrule["bymonthday"][z].match(regex);
				if (z==0) {
					for (var zz=0; zz < set.length; zz++) {
						if (regexResult[1] == "-") {
							if (regexResult[2] < IS_Widget.Calendar.iCalendar.getDaysInMonth(set[zz])) {
								set[zz].setDate(IS_Widget.Calendar.iCalendar.getDaysInMonth(set[zz]) - regexResult[2]);
							}
						} else {
							if (regexResult[2] < IS_Widget.Calendar.iCalendar.getDaysInMonth(set[zz])) {
								set[zz].setDate(regexResult[2]);
							}
						}
					}
				}	else {
					var subset=[];
					for (var zz=0; zz < set.length; zz++) {
						var newDate = new Date(set[zz]);
						if (regexResult[1] == "-") {
							if (regexResult[2] < IS_Widget.Calendar.iCalendar.getDaysInMonth(set[zz])) {
								newDate.setDate(IS_Widget.Calendar.iCalendar.getDaysInMonth(set[zz]) - regexResult[2]);
							}
						} else {
							if (regexResult[2] < IS_Widget.Calendar.iCalendar.getDaysInMonth(set[zz])) {
								newDate.setDate(regexResult[2]);
							}
						}
						subset.push(newDate);
					}
					tmp = set.concat(subset);
					set = tmp;
				}
			}
		}
	}

	if (rrule["byday"]  && (order["byday"]<freqInt)) {
		if (rrule["bymonth"]) {
			if (rrule["byday"].length > 0) {
				var regex = "([+-]?)([0-9]{0,1}?)([A-Za-z]{1,2})";
				for (var z=0; z<rrule["byday"].length; z++) {
					var regexResult = rrule["byday"][z].match(regex);;
					var occurance = regexResult[2];
					day = regexResult[3].toLowerCase();


					if (z==0) {
						for (var zz=0; zz < set.length; zz++) {
							if (regexResult[1] == "-") {
								//find the nth to last occurance of date 
								var numDaysFound = 0;
								var lastDayOfMonth = IS_Widget.Calendar.iCalendar.getDaysInMonth(set[zz]);
								daysToSubtract = 1;
								set[zz].setDate(lastDayOfMonth); 
								if (weekdays[set[zz].getDay()] == day) {
									numDaysFound++;
									daysToSubtract=7;
								}
								daysToSubtract = 1;
								while (numDaysFound < occurance) {
									set[zz].setDate(set[zz].getDate()-daysToSubtract);	
									if (weekdays[set[zz].getDay()] == day) {
										numDaysFound++;
										daysToSubtract=7;	
									}
								}
							} else {
								if (occurance) {
									var numDaysFound=0;
									set[zz].setDate(1);
									daysToAdd=1;

									if(weekdays[set[zz].getDay()] == day) {
										numDaysFound++;
										daysToAdd=7;
									}

									while(numDaysFound < occurance) {
										set[zz].setDate(set[zz].getDate()+daysToAdd);
										if(weekdays[set[zz].getDay()] == day) {
											numDaysFound++;
											daysToAdd=7;
										}
									}
								} else {
									//we're gonna expand here to add a date for each of the specified days for each month
									var numDaysFound=0;
									var subset = [];

									lastDayOfMonth = new Date(set[zz]);
									daysInMonth = IS_Widget.Calendar.iCalendar.getDaysInMonth(set[zz]);
									lastDayOfMonth.setDate(daysInMonth);

									set[zz].setDate(1);
								
									if (weekdays[set[zz].getDay()] == day) {
										numDaysFound++;
									}
									var tmpDate = new Date(set[zz]);
									daysToAdd = 1;
									while(tmpDate.getDate() < lastDayOfMonth) {
										if (weekdays[tmpDate.getDay()] == day) {
											numDaysFound++;
											if (numDaysFound==1) {
												set[zz] = tmpDate;
											} else {
												subset.push(tmpDate);
												tmpDate = new Date(tmpDate);
												daysToAdd=7;	
												tmpDate.setDate(tmpDate.getDate() + daysToAdd);
											}
										} else {
											tmpDate.setDate(tmpDate.getDate() + daysToAdd);
										}
									}
									var t = set.concat(subset);
									set = t; 
								}
							}
						}
					}	else {
						var subset=[];
						for (var zz=0; zz < set.length; zz++) {
							var newDate = new Date(set[zz]);
							if (regexResult[1] == "-") {
								if (regexResult[2] < IS_Widget.Calendar.iCalendar.getDaysInMonth(set[zz])) {
									newDate.setDate(IS_Widget.Calendar.iCalendar.getDaysInMonth(set[zz]) - regexResult[2]);
								}
							} else {
								if (regexResult[2] < IS_Widget.Calendar.iCalendar.getDaysInMonth(set[zz])) {
									newDate.setDate(regexResult[2]);
								}
							}
							subset.push(newDate);
						}
						tmp = set.concat(subset);
						set = tmp;
					}
				}
			}
		} else {
			//TODO: byday within a yearly rule without a bymonth
		}
	}

	//add this set of events to the complete recurranceSet	
	var tmp = recurranceSet.concat(set);
	recurranceSet = tmp;

	return recurranceSet;
}
