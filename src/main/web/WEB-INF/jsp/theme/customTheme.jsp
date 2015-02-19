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
<%@ page contentType="text/css; charset=UTF8" %>
<%@ page import="org.infoscoop.service.PropertiesService"%>
<%@ page import="org.infoscoop.service.PortalLayoutService"%>
<%@ page import="org.json.JSONException"%>
<%@ page import="org.json.JSONObject"%>
<%@page import="org.apache.commons.logging.Log"%>
<%@page import="org.apache.commons.logging.LogFactory"%>

<%
Log logger = LogFactory.getLog(this.getClass());

response.setHeader("Pragma","no-cache");
response.setHeader("Cache-Control", "no-cache");

String staticContentURL = (String) request.getAttribute("staticContentURL");
String customTheme = (String) request.getAttribute("customTheme");

String tabbarFontColor = "";
String tabbarBackgroundColor = "";
String tabbarBackgroundGradationTop = "";
String tabbarBackgroundGradationBottom = "";
String tabbarBorderBottom = "";
String tabbarIconClass = "";
String tabbarIconSuffix = "";
String activetabType = "";
String activetabFontColor = "";
String activetabBackgroundColor = "";
String activetabFontWeight = "";
String searchFormButtonBackgroundColor = "";
String searchFormButtonBackgroundGradationTop = "";
String searchFormButtonBackgroundGradationBottom = "";
String searchFormButtonBorderColor = "";
String searchFormButtonIconClass = "";
String searchFormButtonIconSuffix = "";

try {
	JSONObject json = new JSONObject(customTheme);
	if (!json.isNull("tabbar")){
		JSONObject tabbar = json.getJSONObject("tabbar");
		tabbarFontColor = tabbar.isNull("fontColor") ? "" : (String) tabbar.get("fontColor");
		tabbarBackgroundColor = tabbar.isNull("backgroundColor") ? "" : (String) tabbar.get("backgroundColor");
		tabbarBackgroundGradationTop = tabbar.isNull("backgroundGradationTop") ? "" : (String) tabbar.get("backgroundGradationTop");
		tabbarBackgroundGradationBottom = tabbar.isNull("backgroundGradationBottom") ? "" : (String) tabbar.get("backgroundGradationBottom");
		tabbarBorderBottom = tabbar.isNull("borderBottom") ? "" : (String) tabbar.get("borderBottom");
		tabbarIconClass = tabbar.isNull("iconClass") ? "" : (String) tabbar.get("iconClass");
		if (!tabbarIconClass.isEmpty()){
			tabbarIconSuffix = getIconClassSuffix(tabbarIconClass);
		}
		
		if (!tabbar.isNull("activetab")){
			JSONObject activetab = tabbar.getJSONObject("activetab");
			activetabType = activetab.isNull("type") ? "" : (String) activetab.get("type");
			activetabFontColor = activetab.isNull("fontColor") ? "" : (String) activetab.get("fontColor");
			activetabBackgroundColor = activetab.isNull("backgroundColor") ? "" : (String) activetab.get("backgroundColor");
			activetabFontWeight = activetab.isNull("fontWeight") ? "" : (String) activetab.get("fontWeight");
		}
	}
	
	if (!json.isNull("commandbar")){
		JSONObject commandbar = json.getJSONObject("commandbar");
		if (!commandbar.isNull("searchFormButton")){
			JSONObject searchFormButton = commandbar.getJSONObject("searchFormButton");
			searchFormButtonBackgroundColor = searchFormButton.isNull("backgroundColor") ? "" : (String) searchFormButton.get("backgroundColor");
			searchFormButtonBackgroundGradationTop = searchFormButton.isNull("backgroundGradationTop") ? "" : (String) searchFormButton.get("backgroundGradationTop");
			searchFormButtonBackgroundGradationBottom = searchFormButton.isNull("backgroundGradationBottom") ? "" : (String) searchFormButton.get("backgroundGradationBottom");
			searchFormButtonBorderColor = searchFormButton.isNull("borderColor") ? "" : (String) searchFormButton.get("borderColor");
			searchFormButtonIconClass = searchFormButton.isNull("iconClass") ? "" : (String) searchFormButton.get("iconClass");
			if (!searchFormButtonIconClass.isEmpty()) {
				searchFormButtonIconSuffix = getIconClassSuffix(searchFormButtonIconClass);
			}
		}
	}
} catch (final JSONException e) {
	response.sendError(203, "No Contents" );
	logger.error( "Invalid json format: " + e);
}
%>
<%!
String getIconClassSuffix (String iconClass){
		String suffix = "";
		if( iconClass.equalsIgnoreCase("white")){
			suffix = "-white";
		} else if (iconClass.equalsIgnoreCase("gray")){
			suffix = "";
		} else {//use default color icons in case of the other text.
			suffix = "-white";
		}
		return suffix;
	}
%>
/*###########
 Theme css
#############*/

/*-------
 tabBar
---------*/

<%
	if (!tabbarFontColor.isEmpty()) {
%>
/*fontColor*/
.infoScoop #tabs .tab .tabTitle {
	color: <%=tabbarFontColor%>;
}
<%
	}
	if (!tabbarBackgroundColor.isEmpty()) {
%>
/*backgroundColor*/
.infoScoop #tab-container {
	background-color: <%=tabbarBackgroundColor%>;
	background-image: none;
	-ms-filter: "progid:DXImageTransform.Microsoft.gradient(enabled = false)";
}

#portal-maincontents-table.maximized, 
#portal-maincontents-table.hiding-tab {
	border-top-color: <%=tabbarBackgroundColor%>;
}

<%
	}
	if (!tabbarBackgroundGradationTop.isEmpty() && !tabbarBackgroundGradationBottom.isEmpty()) {
		
%>
/*backgroundGradationTop*/
/*backgroundGradationBottom*/
.infoScoop #tab-container {
	background-image: -webkit-linear-gradient(top,<%=tabbarBackgroundGradationTop%> 0,<%=tabbarBackgroundGradationBottom%> 100%); /* Chrome10+, Safari5.1+ */
	background-image: -moz-linear-gradient(top, <%=tabbarBackgroundGradationTop%> 0, <%=tabbarBackgroundGradationBottom%> 100%);/* FF3.6+ */
	-ms-filter: "progid:DXImageTransform.Microsoft.gradient(startColorstr='<%=tabbarBackgroundGradationTop%>', endColorstr='<%=tabbarBackgroundGradationBottom%>', GradientType=0)";/* IE8,9 */
	background-image: linear-gradient(to bottom, <%=tabbarBackgroundGradationTop%> 0, <%=tabbarBackgroundGradationBottom%> 100%);/* IE10+, W3C */
}

<%
	}
	if (!tabbarBorderBottom.isEmpty()) {
%>
/*borderBottomColor*/
.infoScoop #tabs {
	border-bottom-color: <%=tabbarBorderBottom%>;
}

<%
	}
	if (!tabbarIconClass.isEmpty()) {
%>
/*iconClass*/
.infoScoop #tabs .addatab a.tabbar-icon {
	background: url(<%=staticContentURL%>/skin/imgs/plus<%=tabbarIconSuffix%>.png ) no-repeat center left;
}
.infoScoop #tabs .tab.selected .selectMenu.menu {
	background: url(<%=staticContentURL%>/skin/imgs/arrow-circle-o-down<%=tabbarIconSuffix%>.png ) no-repeat center left;
}
.infoScoop #tabs #tabsRefresh a.tabbar-icon {
	background: url(<%=staticContentURL%>/skin/imgs/refresh<%=tabbarIconSuffix%>.png ) no-repeat center left;
}
.infoScoop #tabs #tabsRefreshStop a.tabbar-icon {
	background: url(<%=staticContentURL%>/skin/imgs/times-circle<%=tabbarIconSuffix%>.png ) no-repeat center left;
}
.infoScoop #tabs .tab.selected .selectMenu.refresh {
	background: url(<%=staticContentURL%>/skin/imgs/refresh<%=tabbarIconSuffix%>.png ) no-repeat center left;
}
.infoScoop #tabs li .css.bounce-ball-indicator {
	background-color: #737373;
}
.infoScoop #tabs li .gif.bounce-ball-indicator {
	background-image: url(<%=staticContentURL%>/skin/imgs/ajax-loader<%=tabbarIconSuffix%>.gif);
	background-repeat: no-repeat;
	background-position: top center;
}
<%
	}
%>

/*-------
 tabbar - activeTab
---------*/

<%
	if (!activetabFontColor.isEmpty()) {
%>
/*fontColor*/
.infoScoop #tabs .tab.selected .tabTitle {
	color: <%=activetabFontColor%>;
}
<%
	}
	if (!activetabFontWeight.isEmpty()) {
%>
/*fontWeight*/
.infoScoop #tabs .tab.selected .tabTitle {
	font-weight: <%=activetabFontWeight%>;
}
<%
	}
	if (!activetabType.isEmpty() && !activetabBackgroundColor.isEmpty()) {
%>
/*backgroundColor*/
<%
		if (activetabType.equalsIgnoreCase("fill")) {
%>
.infoScoop #tabs .tab.selected {
	background-color: <%=activetabBackgroundColor%>;
}

<%
		} else if (activetabType.equalsIgnoreCase("border")) {
%>

.infoScoop #tabs .tab.selected {
	background-color: transparent;
}
.infoScoop #tabs .tab.selected div.inner {
	border-bottom: 3px solid <%=activetabBackgroundColor%>;
	margin-bottom: -3px;
}
.infoScoop #tabs {
	border-bottom-width: 3px;
}

<%
		}
	}
%>

/*-------
 commandbar - searchFormButton
---------*/
<%
	if (!searchFormButtonBackgroundColor.isEmpty()) {
%>
/*backgroundColor*/
#search-button {
	background-color: <%=searchFormButtonBackgroundColor%>;
	background-image: none;
	-ms-filter: "progid:DXImageTransform.Microsoft.gradient(enabled = false)";
}
<%
	}
	if (!searchFormButtonBackgroundGradationTop.isEmpty() && !searchFormButtonBackgroundGradationBottom.isEmpty()) {
%>
/*backgroundGradationTop*/
/*backgroundGradationBottom*/
#search-button {
	background-image: -webkit-linear-gradient(top,<%=searchFormButtonBackgroundGradationTop%> 0,<%=searchFormButtonBackgroundGradationBottom%> 100%); /* Chrome10+, Safari5.1+ */
	background-image: -moz-linear-gradient(top, <%=searchFormButtonBackgroundGradationTop%> 0, <%=searchFormButtonBackgroundGradationBottom%> 100%);/* FF3.6+ */
	-ms-filter: "progid:DXImageTransform.Microsoft.gradient(startColorstr='<%=searchFormButtonBackgroundGradationTop%>', endColorstr='<%=searchFormButtonBackgroundGradationBottom%>', GradientType=0)";/* IE8,9 */
	background-image: linear-gradient(to bottom, <%=searchFormButtonBackgroundGradationTop%> 0, <%=searchFormButtonBackgroundGradationBottom%> 100%);/* IE10+, W3C */	
}

<%
	}
	if (!searchFormButtonBorderColor.isEmpty()) {
%>
/*borderColor*/
#search-button {
	border-color: <%=searchFormButtonBorderColor%>;
}
<%
	}
	if (!searchFormButtonIconClass.isEmpty()) {
%>

/*iconClass*/
#search-icon {
	background: url(<%=staticContentURL%>/skin/imgs/search<%=searchFormButtonIconSuffix%>.png) no-repeat;
	background-position: 57% 60%;
}
<%
	}
%>