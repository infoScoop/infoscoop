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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html; charset=UTF8" %>
<%@page import="org.infoscoop.admin.web.PreviewImpersonationFilter"%>
<%@page import="org.infoscoop.service.PropertiesService"%>
<%@page import="org.infoscoop.service.ForbiddenURLService" %>
<%@page import="org.infoscoop.service.PreferenceService" %>
<%@page import="org.infoscoop.util.RSAKeyManager"%>
<%@page import="org.infoscoop.web.SessionManagerFilter"%>
<%@page import="org.infoscoop.util.I18NUtil"%>
<%
	response.setHeader("Pragma","no-cache");
	response.setHeader("Cache-Control","no-cache");
%> 
<%
	String staticContentURL = PropertiesService.getHandle().getProperty("staticContentURL");
	Boolean isPreview = (Boolean) request.getAttribute(PreviewImpersonationFilter.IS_PREVIEW);
	if( isPreview == null )
		isPreview = Boolean.FALSE;
	
	String userAgent = request.getHeader("user-agent");
	boolean pngSupport = true;
	if(userAgent != null)
		pngSupport = (userAgent.toLowerCase().indexOf("msie 6.") == -1);
%>
<%@page import="org.w3c.util.UUID"%>
<%@page import="org.infoscoop.dao.SessionDAO"%>
<%@page import="org.apache.commons.logging.Log"%><html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Cache-Control" content="no-cache">
    <meta http-equiv="Expires" content="Thu, 01 Dec 1994 16:00:00 GMT">
	<title></title>
	<!--start styles css-->
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/styles.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/siteaggregationmenu.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/treemenu.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/calendar.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/pulldown.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/calendarinput.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/mySiteMap.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/treemenu.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/commandbar.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/tab.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/widget.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/groupsettingmodal.css">

    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/message.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/minibrowser.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/ranking.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/widgetranking.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/rssreader.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/maximizerssreader.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/information.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/ticker.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/skin/searchengine.css">
	<!--end styles css-->

	<!-- prototype-window -->
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/js/lib/prototype-window-1.3/themes/default.css">
    <link rel="stylesheet" type="text/css" href="<%=staticContentURL%>/js/lib/prototype-window-1.3/themes/alphacube.css">
    <link rel="stylesheet" type="text/css" href="portallayout?type=css">

	<!-- favicon.ico -->
	<link rel="shortcut icon" href="favicon.ico">
	
    <script src="js/resources/resourceBundle.jsp"></script>

    <script src="js/gadget/features/core:rpc:pubsub:pubsub-2:infoscoop.js?c=1"></script>
	<%
		//org.infoscoop.web.SessionManagerFilter.LOGINUSER_ID_ATTR_NAME
		String displayName = (String) session.getAttribute("loginUserName");
		String uid = (String) session.getAttribute("Uid");
		if(displayName == null || "".equals(displayName)){
			displayName = uid;
		}
		//org.infoscoop.web.SessionManagerFilter.LOGINUSER_NAME_ATTR_NAME
		Boolean isAdmin = (Boolean) request.getAttribute("isAdministrator");
	%>
    <script>
		<jsp:include page="/prpsrv" flush="true" />
		
		var isTabView = false;
		var imageURL = staticContentURL + "/skin/imgs/";

		var IS_Portal = {
			lang : "<%=request.getLocale().getLanguage() %>",
			country : "<%=request.getLocale().getCountry() %>",
			japaneseOnly : false
		};
		var is_userId = <%=uid != null ? "\"" + uid.replace("\\", "\\\\") + "\"" : "null" %>;
		var is_userName = <%=displayName != null ?  "\"" + displayName + "\"" : "null" %>;
		var is_isAdministrator = <%=isAdmin != null ? isAdmin.booleanValue() : false%>;
		//dojo.require("dojo.dom");

		var localhostPrefix = "<%=request.getScheme()%>://localhost:<%=request.getServerPort()%><%=request.getContextPath()%>"

		var IS_forbiddenURLs = <%= ForbiddenURLService.getHandle().getForbiddenURLsJSON() %>;

	</script>

	<!--start script-->
    <script src="<%=staticContentURL%>/js/lib/prototype-1.6.0.3.js"></script>
    <script src="<%=staticContentURL%>/js/lib/control.modal.js"></script>
    <script src="<%=staticContentURL%>/js/utils/ajax304.js"></script>
	<script src="<%=staticContentURL%>/js/lib/scriptaculous-js-1.8.2/effects.js"></script>
	<script src="<%=staticContentURL%>/js/lib/scriptaculous-js-1.8.2/dragdrop.js"></script>
	<script src="<%=staticContentURL%>/js/lib/scriptaculous-js-1.8.2/controls.js"></script>
	<script src="<%=staticContentURL%>/js/lib/syntacticx-livepipe-ui/livepipe.js"></script>
	<script src="<%=staticContentURL%>/js/lib/syntacticx-livepipe-ui/tabs.js"></script>
    <!--script src="<%=staticContentURL%>/js/lib/json.js"></script-->
	<script src="<%=staticContentURL%>/js/lib/date/date.js"></script>
	<script src="<%=staticContentURL%>/js/lib/rsa/jsbn.js"></script>
	<script src="<%=staticContentURL%>/js/lib/rsa/prng4.js"></script>
	<script src="<%=staticContentURL%>/js/lib/rsa/rng.js"></script>
	<script src="<%=staticContentURL%>/js/lib/rsa/rsa.js"></script>
    <script src="<%=staticContentURL%>/js/lib/extras-array.js"></script>
    <script src="<%=staticContentURL%>/js/lib/html-sanitizer-minified.js"></script>
    <script src="<%=staticContentURL%>/js/utils/utils.js"></script>
    <script src="<%=staticContentURL%>/js/utils/domhelper.js"></script>
    <script src="<%=staticContentURL%>/js/utils/ajaxpool/ajax.js"></script>
    <script src="<%=staticContentURL%>/js/utils/Request.js"></script>
    <script src="<%=staticContentURL%>/js/utils/msg.js"></script>
    <script src="<%=staticContentURL%>/js/Portal.js"></script>
    <script src="<%=staticContentURL%>/js/DragWidget.js"></script>
    <!--script src="<%=staticContentURL%>/js/lib/xpath.js"></script-->
    <script src="<%=staticContentURL%>/js/utils/CalendarInput.js"></script>
    <script src="<%=staticContentURL%>/js/commands/UpdatePropertyCommand.js"></script>
    <script src="<%=staticContentURL%>/js/utils/EventDispatcher.js"></script>
    <script src="<%=staticContentURL%>/js/utils/Validator.js"></script>
    <script src="<%=staticContentURL%>/js/utils/groupSettingModal.js"></script>
    <script src="<%=staticContentURL%>/js/Tab.js"></script>
	<script src="<%=staticContentURL%>/js/WidgetsMap.js"></script>
    <script src="<%=staticContentURL%>/js/WidgetsContainer.js"></script>
    <script src="<%=staticContentURL%>/js/AutoReload.js"></script>
    <script src="<%=staticContentURL%>/js/SiteAggregationMenu.js"></script>
    <script src="<%=staticContentURL%>/js/SiteMap.js"></script>
    <script src="<%=staticContentURL%>/js/TreeMenu.js"></script>
    <script src="<%=staticContentURL%>/js/AddContentPane.js"></script>
    <script src="<%=staticContentURL%>/js/search/SearchEngine.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/Widget.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/WidgetHeader.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/WidgetEdit.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/maximize/Maximize.js"></script>
    <script src="<%=staticContentURL%>/js/ContentFooter.js"></script>
    <script src="<%=staticContentURL%>/js/GlobalSetting.js"></script>
    <script src="<%=staticContentURL%>/js/Theme.js"></script>
    <!-- prototype-window -->
    <script type="text/javascript" src="<%=staticContentURL%>/js/lib/prototype-window-1.3/window.js"></script>
    <script type="text/javascript" src="<%=staticContentURL%>/js/lib/prototype-window-1.3/window_ext.js"></script>

    <script src="<%=staticContentURL%>/js/widgets/rssreader/RssReader.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/rssreader/RssItemRender.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/MultiRssReader/MultiRssReader.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/maximize/MaximizeRssReader.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/maximize/MaximizeRssItemRender.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/maximize/MaximizeRssItemSelection.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/maximize/MaximizeRssCategory.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/information/Information.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/information/Information2.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/calendar/Calendar.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/calendar/iCalendar.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/ticker/Ticker.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/ranking/Ranking.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/ranking/RankingRender.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/MiniBrowser/MiniBrowser.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/MiniBrowser/FragmentMiniBrowser.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/WidgetRanking/WidgetRanking.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/Message/Message.js"></script>
    <script src="<%=staticContentURL%>/js/widgets/Message/MaximizeMessage.js"></script>
    <!--end script-->
    
   	<script type="text/javascript">
		var rsaPK = new RSAKey();
		rsaPK.setPublic("<%= RSAKeyManager.getInstance().getModulus() %>", "<%= RSAKeyManager.getInstance().getPublicExponent() %>");

		IS_WidgetConfiguration = <jsp:include page="/widconf" flush="true" />;
		IS_WidgetIcons = <jsp:include page="/gadgeticon" flush="true" />;

		var preference = <%= PreferenceService.getHandle().getPreferenceJSON(uid) %>
		if(preference.property){
			IS_Portal.logoffDateTime = new Date( preference.property.logoffDateTime ? preference.property.logoffDateTime : "").getTime();
			IS_Portal.fontSize = preference.property.fontSize ? preference.property.fontSize : IS_Portal.defaultFontSize;
			if(preference.property.freshDays) IS_Portal.freshDays = preference.property.freshDays;
			IS_Portal.lastSaveFailed = preference.property.failed ? getBooleanValue(preference.property.failed) : false;
			IS_Portal.mergeconfirm = preference.property.mergeconfirm ? getBooleanValue(preference.property.mergeconfirm) : true;
			IS_Portal.msgLastViewTime = preference.property.msgLastViewTime || -1;
			if(preference.property.theme)
				IS_Portal.theme.currentTheme = eval( '(' + preference.property.theme + ')' );
			IS_Portal.preference = preference.property;
			IS_Portal.SearchEngines.searchOption = preference.property.searchOption ? eval('(' + preference.property.searchOption+ ')') : {};
		}
	</script>

    <%
    if(isPreview != null && isPreview.booleanValue()){
	    java.util.List principalParams = (java.util.List) request.getAttribute(PreviewImpersonationFilter.PRINCIPAL_PARAMS);
	%>
	<script type="text/javascript">
    var IS_Preview = {
    	replaceRegExp : new RegExp("(/widsrv|/customization|/mnusrv)"),
    	cancelRegExp : new RegExp("/comsrv|/mnuchksrv"),
		rewriteUrl: function(url){
			if(this.replaceRegExp.test(url) && !/\/adminpreview/.test(url)) {
				var newurl = url.replace(this.replaceRegExp, "/adminpreview$1");
				<%
				for(int i = 0; i < principalParams.size(); i++){
					String princial = (String)principalParams.get(i);
				%>
					newurl += <%= ( i % 2 == 0 ) ? "(newurl.indexOf('?') > 0 ? '&' : '?')" : "'='" %> + encodeURIComponent("<%= princial.replaceAll("\\\"", "\\\\\\\"") %>");
				<%
				}
				%>
				return newurl;
			} else if(this.cancelRegExp.test(url)) {
				return false;
			}
			return url;
		}
	};
    </script>
	<%}%>
	<%
	String isSelectProfilePreview = request.getParameter(org.infoscoop.web.CheckDuplicateUidFilter.IS_PREVIEW);
	String isSelectProfilePreviewUid = request.getParameter("Uid");
    if(isSelectProfilePreview != null && "true".equalsIgnoreCase( isSelectProfilePreview ) ){
    %>
    <script type="text/javascript">
    var IS_Preview = {
        	replaceRegExp : new RegExp("(/widsrv|/customization|/mnusrv)"),
        	cancelRegExp : new RegExp("/comsrv"),
    		rewriteUrl: function(url){
    			if(this.replaceRegExp.test(url)) {
    				var newurl = url + (url.indexOf("?") > 0 ? "&" : "?") + "<%= org.infoscoop.web.CheckDuplicateUidFilter.IS_PREVIEW %>=true&Uid=<%= isSelectProfilePreviewUid%>";
    				return newurl;
    			} else if(this.cancelRegExp.test(url)) {
    				return false;
    			}
    			return url;
    		}
    	};
    </script>
    <%
    }
	%>
 </head>

	<body style="margin-top:0;padding-top:0;" class="infoScoop">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" id="command-bar">
			<tbody>
				<tr>
					<td id="td-portal-logo">
						<div id="portal-logo" outside="true">
							<a href="javascript:void(0)"><img id="portal-logo-img" src="<%=staticContentURL%>/skin/imgs/infoscoop_logo.<%= (pngSupport)? "png":"gif" %>" border="0" /></a>
						</div>
					</td>
					<td width="100%"><div id="portal-command" style="visibility:hidden;position:absolute;left:9999px;"></div></td>
					<td>
						<div id="portal-user-menu">
							<div id="portal-user-menu-label">
							    <% if(uid != null){ %>
									<%= displayName %>
								<% } else { %>
									<a id="portal-loginLink" href="login.jsp"><%= I18NUtil.resolve(I18NUtil.TYPE_JS, "%{lb_login}", request.getLocale()) %></a>
								<% } %>
							</div>
						</div>
					</td>
					<td width="16px"><img id="messageIcon" src="<%=staticContentURL%>/skin/imgs/information.gif" style="cursor:pointer;" onclick="javascript:msg.showPopupDialog();"/></td>
				</tr>
			</tbody>
		</table>
		<div id="portal-header"></div>
		<div id="portal-body">
		<div id="error-msg-bar" style="display:none;"></div>
		<div id="message-bar" style="display:none;"><div id="message-list"></div><div id="message-list-more" style="display:none;"></div><div id="message-bar-controles"></div></div>
		<table width="100%" border="0" cellspacing="0" cellpadding="0"><tbody>
			<tr>
				<td id="portal-site-aggregation-menu"></td>
				<td style="display:none;"><div id="portal-go-home"></div></td>
			</tr>
			</tbody></table>
		<table style="clear:both;" cellpadding="0" cellspacing="0" width="100%" id="portal-maincontents-table">
			<tbody>
				<tr>
					<td id="siteMenu" valign="top">
						<div id="portal-tree-menucontainer">
							<div id="portal-tree-menu" ></div>
							<div id="portal-rss-search" style="padding:1px;"></div>
							<div id="portal-my-sitemap" ></div>
						</div>
					</td>
					<td id="siteMenuOpenTd" align="left"><div id="siteMenuOpen"/></td>
					<td colspan="3" valign="top" align="left">
						<div id="portal-iframe-url"></div>
						<div id="panels" style="display:;">
						  <div id="tab-container"></div>
						  <div id="maximize-panel" style="display:none;"></div>
						</div>
						<div id="portal-iframe" style="display:none;">
							<iframe id="ifrm" name="ifrm" src="./blank.html" FrameBorder="0" style="width:100%;height768px;border:none;scrolling:auto;"></iframe>
						</div>
						<div id="iframe-tool-bar"></div>
						<div id="search-iframe" style="display:none;"></div>
					</td>
				</tr>
			</tbody>
		</table>
		</div>
	</body>
	<script>

		var sessionopt = {
			asynchronous:true,
			onSuccess: function(response){
				eval('(' + response.responseText + ')');
			},
			onFailure: function(t) {
				alert('Retrieving session id failed. ' + t.status + ' -- ' + t.statusText);
			},
			onExcepti: function(t) {
				alert('Retrieving session id failed. ' + t);
			}
		};
		AjaxRequest.invoke(hostPrefix + "/sessionid<% if(isPreview.booleanValue()) { %>?isPreview=<%= isPreview %><% } %>", sessionopt);
		
		var scriptElm = document.createElement('script');
		scriptElm.src = 'portallayout?type=javascript';
		document.getElementsByTagName('head')[0].appendChild(scriptElm);
	</script>
</html>
