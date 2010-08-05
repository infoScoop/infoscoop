<%--
# infoScoop Calendar
# Copyright (C) 2010 Beacon IT Inc.
# 
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see
# <http://www.gnu.org/licenses/old-licenses/gpl-2.0.html>.
--%>

<%@ page import="org.infoscoop.util.I18NUtil" %>
<%@ page contentType="text/javascript; charset=UTF8" %>var ISA_R = {
<%
	java.util.Map i18nMap = I18NUtil.getResourceMap(I18NUtil.TYPE_ADMINJS, request.getLocale());
for(java.util.Iterator it = i18nMap.entrySet().iterator(); it.hasNext();){
	java.util.Map.Entry entry = (java.util.Map.Entry)it.next();
	String value = (String)entry.getValue();
%>
	"<%= entry.getKey() %>": "<%= value %>",
<%
}
%>
	
	getResource: function(message, array){
		if(message && array){
			for(var i=0;i<array.length;i++){
				message = message.replace("{" + i + "}", array[i]);
			}
		}
		return message;
	}
}
