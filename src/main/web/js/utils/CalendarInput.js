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


var CalendarInput = Class.create();
CalendarInput.prototype = {
	/**
	 * <p>this is a format used to parse string representations
	 * <p>Apply Dates to the template indicated as format
	 * <ul>
	 * <li>year: four consecutive "Y" like "YYYY" or "yyyy"; year consists of four digits
	 * <li>month: "M" or two consecutive "M"; month consists of single or double digits
	 * In case of "MM": zero-fill the months of single figures
	 * <li>day: "D" or two consecutive "D"; day consists of single or double digits
	 * In case of "DD: zero-fill the days of single figures
	 * <p>sample
	 * <blockquote>
	 * <ul><li>
	 * <li>yyyy/mm/dd ... 2003/03/20
	 * </blockquote>
	 * <p>if you change dateFormat you need properly command to "contents" to reflect it to UI 
	 */
	dateFormat	:	"YYYY/MM/DD",
	_zIndex      :   1000,
	initialize	:	function( input,dateFormat,onchange,standbyNow ) {
		var self = this;
		
		var component = null;
		var controll = null;
		var contents = null;
		var focus = null;
		var visibility = false;
		
		var ready = false;
		
		function initialize( input,dateFormat ) {
			if( dateFormat )
				self.dateFormat = dateFormat;
			
			component = createComponent();
			contents = component.contents;
			self.contents = contents;
			
			self.install( input );
			
			if( standbyNow )
				standby();
		}
		
		self.install = function( input ) {
					
			input.disabled = "disabled";
			input.style.backgroundColor = "white";

			var parent = input.parentNode;
			parent.removeChild( input );
			
			var panel = document.createElement("div");
			panel.style.width = "120px";
			input.style.width = "120px";
			Element.addClassName( panel,"DatatypeCalendar");
			parent.appendChild( panel );
			controll = panel;
			this.controll = controll;
			
			Element.addClassName( input,"Calendar_Input");
			//input.style.width = "80%";
			panel.appendChild( input );
			IS_Event.observe( input,'change',handleInputChange );
			
			var button = document.createElement("a");
			button.href = "javascript:void(0)";
			Element.addClassName( button,"Calendar_Button");
			IS_Event.observe( button,'click',handleButtonAction );
			panel.appendChild( button );
			
			var buttonIcon = document.createElement("img");
			buttonIcon.src = imageURL+"calendar-day.png";
			Element.addClassName( buttonIcon,"Calendar_Button_Icon");
			button.appendChild( buttonIcon );
			
			document.body.appendChild( contents );
		};
		function handleInputChange() {
			var dateString = CalendarInput.toDateString(
				self.dateFormat,component.getSelectedDate());
			
			if( dateString == input.value )
				return;
			
			//alert( component.getSelectedDate() );
			component.setSelectedDate( CalendarInput.parseDate( self.dateFormat,input.value ) );
			//alert( component.getSelectedDate() );
		}
		function handleButtonAction( event ) {
			if( !ready )
				standby();
			
			self.toggleContentsVisibility();
			
			if( event && event.type == "click") {
				IS_Event.stop( event );
			}
		}
		function standby() {
			IS_Event.observe( document.body,'click',handleBodyClick,false );
			
			IS_Event.observe( controll,'mouseout',lazzyHide,false );
			IS_Event.observe( contents,'mouseout',lazzyHide,false );
			IS_Event.observe( controll,'mouseover',show,false );
			IS_Event.observe( contents,'mouseover',show,false );
			
			component.standby();
			
			component.setSelectedDate( CalendarInput.parseDate( self.dateFormat,input.value ) );
			
			ready = true;
		}
		function handleBodyClick( event ) {
			if( !visibility )
				return;
			
			if( event.type && !(/click/i.test( event.type )))
				return;
			
			var x = findPosX( contents );
			var y = findPosY( contents );
			var cx = Event.pointerX( event );
			var cy = Event.pointerY( event );
			
			if(!( x <= cx && cx <= x +contents.offsetWidth )||
				!( y <= cy && cy <= y +contents.offsetHeight )) {
				if( event.target &&( /option/i.test( event.target.tagName ) ))
					return;
				
				self.hideContents();
				
				if( event && /click/i.test( event.type )) {
					IS_Event.stop( event );
				}
			}
		}
		self.toggleContentsVisibility = function() {
			if( visibility ) {
				self.hideContents();
			} else {
				visibility = true;
				self.showContents();
			}
		};
		self.hideContents = function() {
			if( self.hideTimeout ) {
				clearTimeout( self.hideTimeout );
				self.hideTimeout = null;
			}
			
			if( IS_Portal.behindIframe && IS_Portal.behindIframe.current == contents )
				IS_Portal.behindIframe.hide();
			
			contents.hide();
			visibility = false;
		}
		self.showContents = function() {
			if( self.hideTimeout ) {
				clearTimeout( self.hideTimeout );
				self.hideTimeout = null;
			}
			
			if( !visibility )
				return;
			
			if( !contents.visible() ) {
				component.setScrollDate( component.getSelectedDate() );
				Element.show( contents );
				
				var xy = Position.cumulativeOffset(input.parentNode);
				
				if(fixedPortalHeader){
					xy[1] -= Position.realOffset(input.parentNode)[1];
				}
				
				/*
				contents.style.top = xy[1] +input.parentNode.offsetHeight;
				contents.style.left = xy[0] +input.parentNode.offsetWidth-contents.offsetWidth;
				*/
				// fix #13565
				contents.style.top = xy[1] +input.parentNode.offsetHeight + "px";
				contents.style.left = xy[0] +input.parentNode.offsetWidth-contents.offsetWidth + "px";
				
				if( IS_Portal.behindIframe )
					IS_Portal.behindIframe.show(contents);
			}
		}
		function lazzyHide( e ) {
			if( !self.hideTimeout ) {
				self.hideTimeout = setTimeout( self.hideContents.bind( self ),300 );
			}
			
			IS_Event.stop( e );
		}
		function show( event ) {
			self.showContents();
			
			IS_Event.stop( event );
		}
		
		function createComponent() {
			var component = new CalendarComponent();
			self.component = component;
			var contents = component.contents;
			component.ondatechange = function( c ) {
				var dateString = CalendarInput.toDateString(
					self.dateFormat,c.getSelectedDate());
				if( input.value != dateString ){
					input.value = dateString;
					if(onchange)
					  onchange();
				}
				
				if( c.contents.visible() )
					self.hideContents();
			};
			
			Element.hide( contents );
			contents.style.position = "absolute";
			contents.style.zIndex = self._zIndex;
			
			return component;
		}
		self.uninstall = function() {
			if( ready ) {
				IS_Event.stopObserving( controll,'mouseout',lazzyHide );
				IS_Event.stopObserving( contents,'mouseout',lazzyHide );
				IS_Event.stopObserving( controll,'mouseover',show );
				IS_Event.stopObserving( contents,'mouseover',show );
				
				IS_Event.stopObserving( document.body,'click',handleBodyClick );
			}
			
			document.body.removeChild( contents );
		};
		
		initialize( input,dateFormat );
	}
};
CalendarInput.toDateString = function ( format,date ) {
	if( !date )
		return "";
	
	var s = format +"";
	var y = date.getFullYear();
	s = s.replace(/YYYY/i,y);
	
	var m = date.getMonth() +1;
	if( s.match(/MM/i)) {
		s = s.replace(/MM/i,"M");
		if( m < 10 )
			m = "0"+m;
	}
	s = s.replace(/M/i,m );
	
	var d = date.getDate();
	if( s.match(/DD/i)) {
		s = s.replace(/DD/i,"D");
		if( d < 10 )
			d = "0"+d;
	}
	s = s.replace(/D/i,d );
	
	return s;
};
CalendarInput.parseDate = function( format,dateString ) {
	format = format.replace(/dd?/i,"(\\d+)" );
	format = format.replace(/yyyy/i,"(\\d+)");
	format = format.replace(/mm?/i,"(\\d+)" );
	
	var matches = dateString.match( format );
	var result = new Date();
	try {
		result.setFullYear( matches[1],matches[2] -1,matches[3] );
		result.setHours( 0 );
		result.setMinutes( 0 );
		result.setSeconds( 0 );
	} catch( ex ) {
		return null;
	}
	
	return result;
};

var CalendarComponent = Class.create();
CalendarComponent.prototype = {
//	dayOfWeeks	:	["Sun","Mon","Tue","Fri","Wed","Thu","Sat"],
	dayOfWeeks	:	[
		IS_R.lb_weekdaySun,
		IS_R.lb_weekdayMon,
		IS_R.lb_weekdayTue,
		IS_R.lb_weekdayWed,
		IS_R.lb_weekdayThu,
		IS_R.lb_weekdayFri,
		IS_R.lb_weekdaySat
	],
	//dayOfWeeks	:	["Sun","Mon","Tue","Fri","Wed","Thu","Sat"],
	ondatechange	:	null,
	initialize	:	function() {
		var self = this;
		
		var dateCells = [];
		var cellDates = [];
		
		var scrollDate = null;
		var selectedDate = null;
		var focusDate = null;
		
		var panel = null;
		var table = null;
		var head = null;
		var titleCell = null;
		var body = null;
		
		var previousMonth = null;
		var nextMonth = null;
		
		var ready = false;
		function initialize() {
			panel = document.createElement("div");
			Element.addClassName( panel,"widgetBoxNoHeader" );
			Element.addClassName( panel,"CalendarComponent_Panel");
			Element.addClassName( panel,"CalendarComponent");
			self.contents = panel;
			
			//self.setSelectedDate( new Date() );
		}
		self.standby = function() {
			table = document.createElement("table");
			table.cellSpacing = 0;
			table.className = "CalendarComponent_Table";
			Element.addClassName( table,"calendar" );
			Element.addClassName( table,"widgetContent" );
			panel.appendChild( table );
			head = document.createElement("thead");
			
			titleCell = null;
			
			buildHeader( head );
			table.appendChild( head );
			body = document.createElement("tbody");
			table.appendChild( body );
			
			IS_Event.observe( body,"mouseup",handleClick );
			
			IS_Event.observe( body,"mouseover",handleMouseOver );
			IS_Event.observe( body,"mouseout",handleMouseOut );
			
			IS_Event.observe( previousMonth,'click',function(e){
				if( e ) Event.stop( e );

			});
			IS_Event.observe( nextMonth,'click',function(e){
				if( e ) Event.stop( e );
			});
			IS_Event.observe( previousMonth,'mouseup',self.previousMonth.bindAsEventListener( self ));
			IS_Event.observe( nextMonth,'mouseup',self.nextMonth.bindAsEventListener( self ));
			ready = true;
		}
		
		self.getScrollDate = function() {
			return scrollDate;
		};
		self.setScrollDate = function( newValue ) {
			if( !newValue )
				newValue = new Date();
			
			newValue.setSeconds( 0 );
			
			if( scrollDate && scrollDate.toString() == newValue.toString())
				return;
			
			var oldValue = scrollDate;
			scrollDate = newValue;
			
			if( oldValue && oldValue.getYear() == newValue.getYear() &&
				oldValue.getMonth() == newValue.getMonth() )
				return;
			
			updateBody( body );
		};
		self.getSelectedDate = function() {
			return selectedDate;
		};
		self.setSelectedDate = function( newValue ) {
			//if( selectedDate && selectedDate.toString() == newValue.toString() )
			//	return;
			
			if( selectedDate && dateCells[selectedDate.toString()])
				Element.removeClassName( dateCells[selectedDate.toString()],"CalendarComponent_Day_Selected");
			
			if( newValue ) {
				newValue.setHours( 0 );
				newValue.setMinutes( 0 );
				newValue.setSeconds( 0 );
			}
			selectedDate = newValue;
			if( !newValue )
				return;
			
			self.setScrollDate( newValue );
			
			if( dateCells[newValue.toString()] ) {
				Element.addClassName( dateCells[newValue.toString()],"CalendarComponent_Day_Selected");
			} else {
				//alert("dateCell not found !");
			}
			
			if( self.ondatechange )
				self.ondatechange.call( null,self );
		};
		
		self.previousMonth = function( e ) {
			if( e )
				Event.stop( e );
			scrollMonth( -1 );
		};
		self.nextMonth = function( e ) {
			if( e )
				Event.stop( e );
			scrollMonth( 1 );
		};
		
		function updateBody( tBody ) {
			dateCells = [];
			cellDates = [];
			
			if( !ready )
				return;
			
			var tableContents = tBody.childNodes;
			while(  0<tableContents.length ) {
				tBody.removeChild( tableContents.item(0) );
			}
			
			var y = scrollDate.getFullYear();
			var m = scrollDate.getMonth();
			
			var cd = new Date();
			cd.setFullYear( y,m,1 )
			cd.setHours( 0 );
			cd.setMinutes( 0 );
			cd.setSeconds( 0 );
			
			var currentWeek = [];
			while( cd.getDay() > 0 ) {
				cd.setDate( cd.getDate() -1 );
			}
			
			var weekOfMonth = 0;
			while( cd.getMonth() == m ||(( cd.getMonth() == m -1 )||(
					( cd.getFullYear() == y -1 )&&( cd.getMonth() == 11 ))
				)||(
					( cd.getDate() < 7 )&&( cd.getDay() > 0 )&&(
					( cd.getMonth() == m +1 )||(
						( cd.getFullYear() == y +1 )&&( cd.getMonth() == 0 ))
					)
				)) {
				var dateCell = createDateElement( new Date( cd ) );
				dateCells[cd.toString()] = dateCell.firstChild;
				cellDates[dateCell.firstChild.id] = new Date( cd );
				currentWeek.push( dateCell );
				
				if( cd.getDay() == 6 ) {
					tBody.appendChild( createWeekElement( currentWeek ) );
					
					currentWeek = [];
					weekOfMonth++;
				}
				cd.setDate( cd.getDate() +1 );
			}
			titleCell.innerHTML = y+"/"+( m +1 );
			
			tBody.appendChild( createWeekElement( currentWeek ) );
		}
		
		function handleClick( event ) {
			if( focusDate )
				self.setSelectedDate( focusDate );
			
			IS_Event.stop( event );
		}
		
		function buildHeader( tHeader ) {
			var header = tHeader;
			
			var headerRow = document.createElement("tr");
			header.appendChild( headerRow );
			
			var headerData = document.createElement("th");
			headerData.colSpan = 7;
			Element.addClassName( headerData,"calhead");
			headerRow.appendChild( headerData );
			
			var headerCell = document.createElement("div");
			Element.addClassName( headerCell,"calheader");
			Element.addClassName( headerCell,"CalendarComponent_Header");
			headerData.appendChild( headerCell );
			
			previousMonth = document.createElement("a");
			previousMonth.href = "javascript:void(0)";
			Element.addClassName( previousMonth,"calnavleft");
			headerCell.appendChild( previousMonth );
			
			titleCell = document.createElement("span");
			titleCell.colSpan = "5";
			//titleCell.appendChild( document.createTextNode("2007/10"));
			headerCell.appendChild( titleCell );
			
			nextMonth = document.createElement("a");
			nextMonth.href = "javascript:void(0);";
			Element.addClassName( nextMonth,"calnavright");
			headerCell.appendChild( nextMonth );
			
			var weekHeader = createWeekHeader();
			header.appendChild( weekHeader );
		}
		function createWeekHeader() {
			var weekHeader = document.createElement("tr");
			Element.addClassName( weekHeader,"calweekdayrow");
			for( var i=0;i<7;i++ ) {
				var column = document.createElement("th");
				column.appendChild( document.createTextNode( self.dayOfWeeks[i] ));
				column.style.textAlign = "center";
				Element.addClassName( column,"calweekdaycell" );
				
				weekHeader.appendChild( column );
			}
			
			return weekHeader;
		}
		function createDateElement( cellDate ) {
			var dayOfMonth = cellDate.getDate();
			
			var cell = document.createElement("td");
			Element.addClassName ( cell,"CalendarComponent_Day_Cell");
			Element.addClassName ( cell,"calcell");
			
			if( cellDate.getMonth() != self.getScrollDate().getMonth() ) {
				Element.addClassName( cell,"oom");
			}
			
			var e = document.createElement("div");
			Element.addClassName( e,"CalendarComponent_Day");
			if( self.getSelectedDate() && cellDate.toString() == self.getSelectedDate().toString() ) {
				Element.addClassName( e,"CalendarComponent_Day_Selected");
			}
			cell.appendChild( e );
			e.id = cellDate.getTime();
			
			if( cellDate.getMonth() == self.getScrollDate().getMonth() ) {
				var dayClass = "";
				if( cellDate.getDay() == 0 ) {
					dayClass = "sunday";
				} else if( cellDate.getDay() == 6 ) {
					dayClass = "saturday";
				}
				Element.addClassName( e,dayClass );
			}
			
			e.appendChild( document.createTextNode( dayOfMonth ));
			
			return cell;
		}
		function handleMouseOut( event ) {
			var e = IS_Event.element( event );
			var cellDate = cellDates[e.id];
			if( e && cellDate && focusDate && focusDate == cellDate ) {
				focusDate = null;
				
				Element.removeClassName( e,"CalendarComponent_Day_Focus");
			}
		}
		function handleMouseOver( event ) {
			var e = IS_Event.element( event );
			//In case of Safari 1, text node enters e			
			if( Browser.isSafari1 ) {
				if( e && !e.id && e.parentNode )
					e = e.parentNode;
			}
			
			var cellDate = cellDates[e.id];
			if( e && cellDate && !focusDate || focusDate != cellDate ) {
				if( focusDate ) {
					Element.removeClassName( dateCells[focusDate.toString()],
						"CalendarComponent_Day_Focus");
				}
				
				focusDate = cellDate;
				
				Element.addClassName( e,"CalendarComponent_Day_Focus");
			}
		}
		function createWeekElement( week ) {
			var e = document.createElement("tr");
			week.each( function( value,key ) {
				e.appendChild( value );
			});
			
			return e;
		}
		
		function scrollMonth( step ) {
			var cm = self.getScrollDate().getMonth();
			var newDate = new Date();
			newDate.setFullYear( scrollDate.getFullYear(),cm +step,1 );
			var sm = newDate.getMonth();
			newDate.setDate( scrollDate.getDate());
			
			var axis = ( step < 0 )? -1:1;
			while( sm != newDate.getMonth())
				newDate.setDate( newDate.getDate() +axis );
			
			self.setScrollDate( newDate );
		}
		
		initialize();
	}
};

