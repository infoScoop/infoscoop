IS_Widget.Calendar = IS_Class.create();
IS_Widget.Calendar.prototype.classDef = function() {
	var self = this;
	var widget;
	var today;
	var date;
	var year;
	var month;
	var cacheHTML;
	var isInitialized = false;
	var id;
	
	this.initialize = function(widgetObj){
		widget = widgetObj;
		id = widgetObj.id;
		
		this.Locale = {

			MONTH_FORMAT : IS_R.lb_scheduleMonthFormat,
			EVENT_DATE_FORMAT : "yyyy/MM/dd",
			EVENT_TIME_FORMAT : "HH:mm",
			WEEKDAY : [
				IS_R.lb_weekdaySun,
				IS_R.lb_weekdayMon,
				IS_R.lb_weekdayTue,
				IS_R.lb_weekdayWed,
				IS_R.lb_weekdayThu,
				IS_R.lb_weekdayFri,
				IS_R.lb_weekdaySat
			]
		}
		
		this.widget = widgetObj;
		this.container;
		this.iCalConf = new Array();
		this.monthlyiCalConf = new Array();
		this.icals = new Array();
		this.monthlyiCals = new Array();
		this.dayShare = {};
		
		this.init();
		
		$( widget.elm_widgetContent ).addClassName("Calendar");
	}
	
	this.init = function() {
		today = new Date();
		today.setHours(0);
		today.setMinutes(0);
		today.setSeconds(0);
		today.setMilliseconds(0);
		this.changeDate(today);
		var conf = eval("(" + widget.widgetPref.calendarConfig.value + ")");
		if(!conf) conf = {};
		this.iCalConf = conf.iCals ? conf.iCals : [];
		
		// holiday
		this.iCalConf.push(
			{
				isHoliday : true,
				url : IS_Holiday,
				displayType : "font",
				color : "#F00"
			}
		);
		
		this.monthlyiCalConf = conf.monthlyiCals ? conf.monthlyiCals : [];
	}
		
	this.displayContents = function(response) {
		var html = response.responseText;
		html = html.replace(/\$\{id\}/g, id);
		this.container = widget.elm_widgetContent;
		cacheHTML = html;
		this.displayContentsCache();
	}
	
	this.displayContentsCache = function() {
		var eventTargetList = new Array();
		if(!isInitialized) {
			this.initCalendar(eventTargetList);
		}
		this.initMonthlyCalendar(eventTargetList);
		this.container.innerHTML = cacheHTML;
		this.renderCommon();
		this.renderBody();
		if(eventTargetList.length > 0) {
			IS_EventDispatcher.addComplexListener(eventTargetList, this.renderEvent.bind(this), null, true);
		} else {
			this.renderEvent();
		}
		if(!isInitialized) {
			for(var i=0; i<this.iCalConf.length; i++) {
				var ical = this.icals[this.iCalConf[i].url];
				if(!ical.isLoaded)
					ical.load();
			}
			isInitialized = true;
		}
		for(var i=0; i<this.monthlyiCalConf.length; i++) {
			var url = this.monthlyiCalConf[i].url;
			var ical = this.monthlyiCals[url][year + "-" + (month + 1)];
			if(!ical.isLoaded) {
				ical.load();
			}
		}
	}
	
	this.reloadContents = function() {
		isInitialized = false;
		this.icals = new Array();
		this.monthlyiCals = new Array();
		var eventTargetList = new Array();
		this.initCalendar(eventTargetList);
		this.initMonthlyCalendar(eventTargetList);
		if(eventTargetList.length > 0) {
			IS_EventDispatcher.addComplexListener(eventTargetList, this.reloadRender.bind(this), null, true);
		} else {
			this.reloadRender();
		}
		for(var i=0; i<this.iCalConf.length; i++) {
			var ical = this.icals[this.iCalConf[i].url];
			if(!ical.isLoaded)
				ical.load();
		}
		isInitialized = true;
		for(var i=0; i<this.monthlyiCalConf.length; i++) {
			var url = this.monthlyiCalConf[i].url;
			var ical = this.monthlyiCals[url][year + "-" + (month + 1)];
			if(!ical.isLoaded) {
				ical.load();
			}
		}
	}
	
	this.reloadRender = function() {
		this.container.innerHTML = cacheHTML;
		this.renderCommon();
		this.renderBody();
		this.renderEvent();
	}
	
	this.initCalendar = function(eventTargetList) {
		if(this.iCalConf.length == 0) return;
		for(var i=0; i<this.iCalConf.length; i++) {
			var calObj;
			if(this.iCalConf[i].isHoliday){
				calObj = IS_Holiday;
			}else{
				calObj = new IS_Widget.Calendar.iCalendar(this.iCalConf[i].url);
			}
			
			if(!calObj.isLoaded)
				eventTargetList.push(calObj.getEventTarget());
				
			this.icals[this.iCalConf[i].url] = calObj;
		}
	}
	
	this.initMonthlyCalendar = function(eventTargetList) {
		if(this.monthlyiCalConf.length == 0) return;
		for(var i=0; i<this.monthlyiCalConf.length; i++) {
			var url = this.monthlyiCalConf[i].url;
			if(!this.monthlyiCals[url]) 
				this.monthlyiCals[url] = new Array();
			if(!this.monthlyiCals[url][year + "-" + (month + 1)]) {
				var urlrep = url.replace(/\$\{year\}/g, year);
				urlrep = urlrep.replace(/\$\{month\}/g, month + 1);
				var calObj = new IS_Widget.Calendar.iCalendar(urlrep);
				calObj.computeEvents(year, month + 1);
				eventTargetList.push(calObj.getEventTarget());
				this.monthlyiCals[url][year + "-" + (month + 1)] = calObj;
			}
		}
	}
	
	this.renderCommon = function() {
		//Set previous months and next months
		var prevMonthHandler = this.prevMonth.bind(this);
		Event.observe($(id + "_cal_left"), "mousedown", prevMonthHandler, false);
		var nextMonthHandler = this.nextMonth.bind(this);
		Event.observe($(id + "_cal_right"), "mousedown", nextMonthHandler, false);
		//Set weekdays
		for (var i=0; i<this.Locale.WEEKDAY.length; i++) {
			$(id + "_cal_weekday_" + i).innerHTML = this.Locale.WEEKDAY[i];
		}
	}
	
	this.renderBody = function() {
		//Set months
		$(id + "_cal_month").innerHTML = formatDate(date, this.Locale.MONTH_FORMAT);
		//Set dates
		var tmpDate = this.getFirstSunday();
		for (var i=0; i<42; i++) {
			var cell = $(id + "_cal_day_cell" + i);
			var className = "calcell";
			if(tmpDate.getTime() == today.getTime()) {
				className += " today";
			}
			if(tmpDate.getMonth() != month) {
				className += " oom";
			}
			cell.className = className;
			
			if(tmpDate.getMonth() == month) {
				//the table does not show up if it is added in IE browser with DOM
				var table = "";
				table += '<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%">';
				table += '<tr><td colspan="4" class="date';
				if(tmpDate.getDay() == 0){
					table += ' sunday';
				} else if(tmpDate.getDay() == 6){
					table += ' saturday';
				}
				table += '" id="' + cell.id + '_date' + '">';
				table += tmpDate.getDate();
				table += '</td></tr>';
				table += '<tr>';
				for(var j=0; j<4; j++)	{
					table += '<td class="block" id="' + cell.id + '_block_' + j + '"></td>'
				}
				table += '</tr>';
				table += '</table>';
				cell.innerHTML = table;
			} else {
				cell.appendChild(document.createTextNode(tmpDate.getDate()));
			}
			tmpDate.setDate(tmpDate.getDate() + 1);
		}
	}
	
	this.renderEvent = function() {
		//Set dates
		var tmpDate = this.getFirstSunday();
		//Calculate Events
		for(var i=0; i<this.iCalConf.length; i++) {
			var ical = this.icals[this.iCalConf[i].url];
			ical.computeEvents(year, month + 1);
		}
		for (var i=0; i<42; i++) {
			var cell = $(id + "_cal_day_cell" + i);
			
			if(tmpDate.getMonth() == month) {
				new IS_Widget.Calendar.DayHandler(self, tmpDate, cell);
			}
			
			tmpDate.setDate(tmpDate.getDate() + 1);
		}
	}
	
	this.getEvent = function(date){
		var allEvents = new Array();
		for(var i=0; i<this.iCalConf.length; i++) {
			var conf = this.iCalConf[i];
			var ical = this.icals[conf.url];
			var events = ical.getEvent(date);
			if(events.length > 0){
				allEvents.push({
				  events:events,
				  displayType:conf.displayType,
				  color:conf.color
				});
			}
		}
		for(var i=0; i<this.monthlyiCalConf.length; i++) {
			var conf = this.monthlyiCalConf[i];
			var ical = this.monthlyiCals[conf.url][year + "-" + (month + 1)];
			var events = ical.getEvent(date);
			if(events.length > 0){
				allEvents.push({
				  events:events,
				  displayType:conf.displayType,
				  color:conf.color
				});
			}
		}
		return allEvents;
	}
	
	this.nextMonth = function(e) {
		var ndate = new Date(year, month + 1, 1);
		this.changeDate(ndate);
		this.displayContentsCache();
	}
	
	this.prevMonth = function(e) {
		var ndate = new Date(year, month - 1, 1);
		this.changeDate(ndate);
		this.displayContentsCache();
	}
	
	this.changeDate = function(dateArg) {
		date = dateArg;
		year = dateArg.getFullYear();
		month = dateArg.getMonth();
	}
	
	this.getFirstSunday = function() {
		var firstOfMonth = new Date(year, month, 1);
		var firstOfMonthDay = firstOfMonth.getDay();
		if(firstOfMonthDay == 0)
			return firstOfMonth;
		return new Date(firstOfMonth.getTime() - firstOfMonthDay * 24 * 3600 * 1000);
	}
	
	this.loadContentsOption = {
		url : hostPrefix + "/js/widgets/calendar/templates/Calendar.html",
		request : true,
		preLoad : function() {
			if(cacheHTML) {
				try {
					self.reloadContents();
				} catch(t) {
					
					msg.error( IS_R.getResource( IS_R.ms_iCalCreateFailed, [getText(t)]));
				}
				return false;
			}
			return true;
		},
		onSuccess : this.displayContents.bind(this)
	};
	
	//this.init();
	//this.loadContents();
}

IS_Widget.Calendar.DayHandler = IS_Class.create();
IS_Widget.Calendar.DayHandler.prototype.classDef = function() {
	var calendar;
	var cell;
	var detailDiv;
	var date;
	var events;
	var sortedEvents;
	var divWidth;
	var divHeight;
	var divX;
	var divY;
	
	this.initialize = function(calendarObj, odate, cellObj){
		calendar = calendarObj;
		cell = cellObj;
		date = new Date(odate.getFullYear(), odate.getMonth(), odate.getDate());
		events = calendar.getEvent(date);
		
		this.build();
	}
	
	this.computeCoordinate = function(e) {
		if(!e) return;
		var y = e.pageY || e.clientY + document.body.scrollTop;
		var x = e.pageX || e.clientX + document.body.scrollLeft;
		if(!x) return;
		detailDiv.style.visibility = "hidden";
		detailDiv.style.display = "block";
		divWidth = parseInt(detailDiv.offsetWidth);
		divHeight = parseInt(detailDiv.offsetHeight);
		detailDiv.style.visibility = "visible";
		detailDiv.style.display = "none";
		divWidth = divWidth > 300 ? 300 : divWidth;
		detailDiv.style.width = divWidth + "px";
		var winX = getWindowSize(true) - 25;//25 is required for scroll bar
		var x2 = winX - divWidth/2;
		if(x2 < x) {
			x = winX - divWidth;
		} else {
			x = x - divWidth/2;
		}
		divX = x;
		divY = y - 5;
//		detailDiv.style.top = y - 3;//Adjust cursor to be on div
		detailDiv.style.top = findPosY(cell) + cell.offsetHeight;
		
		detailDiv.style.left = x;
	}
	
	this.showSlowly = function(e) {
		if(!detailDiv) {
			this.buildDetail();
		}
		var currentObj = calendar.dayShare.currentObj;
		if(currentObj && detailDiv != currentObj){
			this._hide(currentObj);
			if(calendar.dayShare.showTimeout)
				clearTimeout(calendar.dayShare.showTimeout);
		}
		if(calendar.dayShare.hideTimeout)
			clearTimeout(calendar.dayShare.hideTimeout);
		
		if(detailDiv != currentObj)
			this.computeCoordinate(e);
		
		calendar.dayShare.currentObj = detailDiv;
		var self = this;
		calendar.dayShare.showTimeout = setTimeout(function(){ self.show(); }, 300);
		
		if(window.event){
			window.event.cancelBubble = true;
		}
		if(e && e.stopPropagation){
			e.stopPropagation();
		}
	}
	
	this.show = function(e) {
		var currentObj = calendar.dayShare.currentObj;
		if(currentObj && detailDiv != currentObj) {
			this._hide(currentObj);
			if(calendar.dayShare.showTimeout)
				clearTimeout(calendar.dayShare.showTimeout);
		}
		
		if(detailDiv != currentObj)
			this.computeCoordinate(e);
		
		this._show();
		calendar.dayShare.currentObj = detailDiv;
		if(calendar.dayShare.showTimeout)
			clearTimeout(calendar.dayShare.showTimeout);
		if(calendar.dayShare.hideTimeout)
			clearTimeout(calendar.dayShare.hideTimeout);
		
		if(window.event){
			window.event.cancelBubble = true;
		}
		if(e && e.stopPropagation){
			e.stopPropagation();
		}
	}
	
	this.hideSlowly = function(e) {
		var self = this;
		if(calendar.dayShare.showTimeout)
			clearTimeout(calendar.dayShare.showTimeout);
		calendar.dayShare.hideTimeout = setTimeout(self.hide.bind(this), 300);
		
		Event.stop(e);
	}
	
	this.hide = function(e) {
		if(calendar.dayShare.showTimeout)
			clearTimeout(calendar.dayShare.showTimeout);
		this._hide();
		calendar.dayShare.currentObj = null;
	}
	
	this._show = function() {
		if(!calendar.dayShare.isShowing) {
			calendar.dayShare.isShowing = true;
			if(Browser.isIE){
				detailDiv.style.display = "block";
			}else{
				new Effect.Appear(detailDiv, {duration:0.3});
			}
			calendar.dayShare.isShowing = false;
		}
	}
	
	this._hide = function(obj) {
		obj = obj ? obj : detailDiv;
		obj.style.display = "none";
	}
	
	this.continueShow = function(e) {
		clearTimeout(calendar.dayShare.hideTimeout);
		calendar.dayShare.currentObj = detailDiv;
		detailDiv.style.display = "block";
		
		if(window.event){
			window.event.cancelBubble = true;
		}
		if(e && e.stopPropagation){
			e.stopPropagation();
		}
	}
	
	this.buildDetail = function() {
		detailDiv = document.createElement("div");
		detailDiv.id = cell.id + "_detail";
		detailDiv.className = "caldaydetail";
		detailDiv.style.display = "none";
		detailDiv.style.position = "absolute";
		detailDiv.innerHTML = '<div class="caleventdate">' + formatDate(date, calendar.Locale.EVENT_DATE_FORMAT) + '</div>';

		
		for(var i=0; i<sortedEvents.length; i++) {
			this.buildEventDiv(sortedEvents[i]);
		}
		document.body.appendChild(detailDiv);//This element is shown in pop-up
		var showHandler = this.show.bind(this);
		var continueShowHandler = this.continueShow.bind(this);
		Event.observe(cell, "mousedown", showHandler, false);
		Event.observe(detailDiv, "mouseover", continueShowHandler, false);
		var hideSlowlyHandler = this.hideSlowly.bind(this);
		var hideHandler = this.hide.bind(this);
//		Event.observe(cell, "mouseout", hideHandler, false);
		Event.observe(cell, "mouseout", hideSlowlyHandler, false);
		Event.observe(detailDiv, "mouseout", hideSlowlyHandler, false);
		Event.observe(detailDiv, "mousedown", hideSlowlyHandler, false);
	}
	
	this.buildEventDiv = function(event) {
		//for(var i=0; i<event.length; i++){
		var eventDiv = document.createElement("div");
		eventDiv.className = "caleventsummary";
		var title;
		if(event.url) {
			title = document.createElement("a");
			title.href = event.url;
			//title.target="ifrm";
			title.onclick = function(e){
				IS_Portal.buildIFrame(title);
			}
		} else {
			title = document.createElement("span");
		}
		color = event.color ? event.color : "#fff";
		title.style.borderLeft = "5px solid " + color;
		title.appendChild(document.createTextNode(event.summary));
		var calTime = document.createElement("span");
		calTime.className = 'caleventtime';
		calTime.innerHTML = '('+IS_Widget.Calendar.iCalendar.getTimeText(date, event)+')';
		title.appendChild(calTime);
		
		eventDiv.appendChild(title);
		detailDiv.appendChild(eventDiv);
		var eventDescDiv = document.createElement("div");
		eventDescDiv.className = "caleventdesc";
		if(event.description){
			var description = event.description.replace(/\n/g,"<br/>");
			eventDescDiv.innerHTML = description;
			var descLinks = eventDescDiv.getElementsByTagName("a");
			if(descLinks) {
				for(var j = 0; j < descLinks.length; j++) {
					if(!descLinks[j].target || descLinks[j].target == "_self"
						|| descLinks[j].target == "_top" || descLinks[j].target == "_parent") {
						// itemDisplay: since user settings does not exist
						descLinks[j].target = "";
						
						var itemDisplay = calendar.widget.getUserPref("itemDisplay");
						if(itemDisplay){
							if(itemDisplay == "newwindow")
							  descLinks[j].target="_blank";
							else if(itemDisplay == "inline")
							  descLinks[j].target = "ifrm";
							else
							  descLinks[j].target = "";
						}
						if(itemDisplay != "newwindow") {
							var descClick = function() {
								IS_Portal.buildIFrame(this);
							}
							IS_Event.observe(descLinks[j], 'click', descClick.bind(descLinks[j]), false, calendar.widget.id);
						}
					}
				}
			}
		}
		detailDiv.appendChild(eventDescDiv);
		//}
	}
	
	this.build = function() {
		sortedEvents = [];
		for(var i=0; i<events.length ; i++) {
			if(events[i].displayType == 'font'){
				var date = $(cell.id + "_date");
				date.style.color = events[i].color;
				sortedEvents =sortedEvents.concat( events[i].events );
			}
		}

		var blockCount = 0;
		for(var i=0; i<events.length ; i++) {
			if(events[i].displayType != 'block')continue;
			if(blockCount < 4){
				var block = $(cell.id + "_block_" + blockCount);
				block.style.backgroundColor = events[i].color;
			}
			events[i].events.each( function(event){
				event.color  = events[i].color;
				sortedEvents.push( event );
			});
			blockCount++;
		}
		IS_Widget.Calendar.iCalendar.sortEvent(sortedEvents);
		
		if(events.length > 0) {
			cell.style.cursor = "pointer";
			var showSlowlyHandler = this.showSlowly.bind(this);
			Event.observe(cell, "mouseover", showSlowlyHandler, false);
		}
	}
}
