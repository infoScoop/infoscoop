<%@ page contentType="text/plain; charset=UTF-8" %>
<%@page import="java.util.*,java.text.*"%>
<%
SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
String uid = request.getParameter("uid");
String sdateText = request.getParameter("startdate");
String edateText = request.getParameter("endnddate");
if(sdateText == null)
	sdateText = request.getHeader("X-IS-STARTDATE");
if(edateText == null)
	edateText = request.getHeader("X-IS-ENDDATE");
System.out.println(sdateText);
Calendar sdate = Calendar.getInstance();
sdate.setTime(format.parse(sdateText));
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
UID:msd-portal/monthly-calendar/test/sdateText
DESCRIPTION:<a href="http://www.google.co.jp/">aaa</a>
DTSTART:<%= sdateText %>T090000
DTEND:<%= sdateText %>T170000
SUMMARY:<%= (uid == null) ? "" : uid + ":" %><%= sdateText %>のイベント
Location: ミーティングルームA
URL:http://www.google.co.jp/search?q=<%= sdateText %>
END:VEVENT
<%
sdate.add(Calendar.DATE, new Random().nextInt(5));
String eventStart = format.format(sdate.getTime());
sdate.add(Calendar.DATE, new Random().nextInt(5));
String eventEnd = format.format(sdate.getTime());
%>
BEGIN:VEVENT
UID:msd-portal/monthly-calendar/test/<%= eventStart %>
DESCRIPTION:<a href="http://www.yahoo.co.jp/">bbb</a>
DTSTART:<%= eventStart %>T090000
DTEND:<%= eventEnd %>T170000
SUMMARY:<%= eventStart %>から<%= eventEnd %>のイベント
Location: meeting_room_A
DESCRIPTION:<a href="http://www.google.co.jp/">bbb</a>
URL:http://www.google.co.jp/search?q=<%= eventStart %>
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