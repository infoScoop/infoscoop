<%@ page contentType="text/plain; charset=UTF-8" %>
<%
String year = request.getParameter("year");
String month = request.getParameter("month");
if(month.length() == 1)
	month = "0" + month;
String date = String.valueOf((int)(Math.random() * 30) + 1);
if(date.length() == 1)
	date = "0" + date;
String hour = String.valueOf((int)(Math.random() * 24) + 1);
if(hour.length() == 1)
	hour = "0" + hour;
String minute = String.valueOf((int)(Math.random() * 60) + 1);
if(minute.length() == 1)
	minute = "0" + minute;
%>
BEGIN:VCALENDAR
PRODID:Monthly Calendarのテスト
VERSION:2.0
METHOD:PUBLISH
CALSCALE:GREGORIAN
X-WR-CALNAME:Monthly Calendarのテスト
X-WR-CALDESC:Monthly Calendarのテスト
X-WR-TIMEZONE:Asia/Tokyo
BEGIN:VEVENT
UID:msd-portal/monthly-calendar/test/<%= year %><%= month %><%= date %>
DESCRIPTION:<a href="http://www.google.co.jp/">aaa</a>
DTSTART:<%= year %><%= month %><%= date %>T<%= hour %><%= minute %>00
DTEND:<%= year %><%= month %><%= date %>T<%= hour %><%= minute %>00
SUMMARY:<%= year %><%= month %><%= date %>のイベント
URL:http://www.google.co.jp/search?q=<%= year %><%= month %><%= date %>
END:VEVENT
<%
date = String.valueOf((int)(Math.random() * 30) + 1);
if(date.length() == 1)
	date = "0" + date;
hour = String.valueOf((int)(Math.random() * 24) + 1);
if(hour.length() == 1)
	hour = "0" + hour;
minute = String.valueOf((int)(Math.random() * 60) + 1);
if(minute.length() == 1)
	minute = "0" + minute;
%>
BEGIN:VEVENT
UID:msd-portal/monthly-calendar/test/<%= year %><%= month %><%= date %>
DESCRIPTION:<a href="http://www.yahoo.co.jp/">bbb</a>
DTSTART:<%= year %><%= month %><%= date %>T<%= hour %><%= minute %>00
DTEND:<%= year %><%= month %><%= (Integer.parseInt(date) + 3) %>T<%= hour %><%= minute %>00
SUMMARY:<%= year %>年<%= month %>月<%= date %>日から<%= (Integer.parseInt(date) + 3) %>日のイベント
DESCRIPTION:<a href="http://www.google.co.jp/">bbb</a>
URL:http://www.google.co.jp/search?q=<%= year %><%= month %><%= date %>
END:VEVENT

BEGIN:VTIMEZONE
TZID:Asia/Tokyo
BEGIN:STANDARD
DTSTART:19700101T000000
TZOFFSETFROM:+0900
TZOFFSETTO:+0900
END:STANDARD
END:VTIMEZONE
END:VCALENDAR