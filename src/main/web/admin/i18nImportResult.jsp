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

<!doctype HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="org.infoscoop.admin.web.I18NImport" %>

<%
	List errorList = (List)request.getAttribute("errorList");
	Map countMap = (Map)request.getAttribute("countMap");
	String errorMessage = (String)request.getAttribute("errorMessage");
%>

<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Cache-Control" content="no-cache">
    <meta http-equiv="Expires" content="Thu, 01 Dec 1994 16:00:00 GMT">

	<title>import result</title>
	
	<style>
		.errorHeader{
			background-color:#DCDCDC;
			border: 1px solid #FFF;
			text-align:center;
			float:left;
		}
		.errorRow{
			float:left;
			overflow:hidden;
			word-break:break-all;
		}
	</style>
</head>
<body>
	<div>
		<%
			if(errorList.size() > 0){
		%>
				<div style="width:100%;">%{ams_i18nImportFailed}</div>
				<br>
				
				<div style="width:100%;">
					<div class="errorHeader" style="width:7%;">%{alb_row}</div>
					<div class="errorHeader" style="width:30%;">%{alb_id}</div>
					<div class="errorHeader" style="width:60%;">%{alb_message}</div>
				</div>
		<%
			}else{
		%>
				<br>
				<div style="width:100%;"><%= countMap.get("insertCount") %>%{alb_i18nImportDataRegistered}</div>
				<div style="width:100%;"><%= countMap.get("updateCount") %>%{alb_i18nImportDataUpdated}</div>
				<br>
		<%
			}
		%>
		
		<%
			I18NImport importObj;
			for(Iterator ite=errorList.iterator();ite.hasNext();){
				importObj = (I18NImport)ite.next();
				
				int lineNumber = importObj.getLineNumber();
				String id = importObj.getId();
				String messageId = importObj.getStatusMessageId();
				String resultMessage = "";
				if(I18NImport.I18N_IMPORT_MESSAGE_EMPTY.equals(messageId)){
					resultMessage = "alb_i18nImportMessageEmpty";
				}
				else if(I18NImport.I18N_IMPORT_ID_EMPTY.equals(messageId)){
					resultMessage = "alb_i18nImportIdEmpty";
				}
				else if(I18NImport.I18N_IMPORT_ID_INVALID.equals(messageId)){
					resultMessage = "alb_i18nImportIdInvalid";
				}
				else if(I18NImport.I18N_IMPORT_DEFAULT_NOTFOUND.equals(messageId)){
					resultMessage = "alb_i18nImportDefaultLocaleNotFound";
				}
				else if(I18NImport.I18N_IMPORT_ID_INVALID_LENGTH.equals(messageId)){
					resultMessage = "alb_i18nImportIdInvalidLength";
				}
				else if(I18NImport.I18N_IMPORT_MESSAGE_INVALID_LENGTH.equals(messageId)){
					resultMessage = "alb_i18nImportMessageInvalidLength";
				}
				
				if(!"".equals( resultMessage ))
					resultMessage = "%{"+resultMessage+"}";
		%>
		
		<div style="width:100%;">
			<div class="errorRow" style="text-align:center;width:7%;"title="<%= lineNumber %>"><%= lineNumber %></div>
			<div class="errorRow" style="text-align:center;width:30%;" title="<%= id %>"><%= id %>&nbsp;</div>
			<div class="errorRow" style="width:60%;" title="<%= resultMessage %>"><%= resultMessage %></div>
		</div>
		
		<%
			}
		%>
	</div>
</body>
</html>
