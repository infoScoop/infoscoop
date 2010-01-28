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
