<%@ page contentType="text/html; charset=UTF-8" errorPage="/jspError.jsp" %>
<%@ page import="org.infoscoop.util.RSAKeyManager"%>
<%@ page import="org.infoscoop.service.ForbiddenURLService" %>
<%@ page import="org.infoscoop.properties.InfoScoopProperties"%>
<%@ page import="org.infoscoop.service.SquareService"%>
<%@page import="org.infoscoop.context.UserContext"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
	String uid = (String) session.getAttribute("Uid");
	boolean useMultitenantMode = Boolean.valueOf(InfoScoopProperties.getInstance().getProperty("useMultitenantMode"));
	request.setAttribute("useMultitenantMode", useMultitenantMode);
	String squareId = UserContext.instance().getUserInfo().getCurrentSquareId();
	String squareName = SquareService.getHandle().getSquareName(squareId);
%>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge" />
		<meta http-equiv="Pragma" content="no-cache">
		<meta http-equiv="Cache-Control" content="no-cache">

		<title>
	    <c:choose>
	        <c:when test="${useMultitenantMode}"><%= squareName %></c:when>
	        <c:otherwise>infoScoop</c:otherwise>
	    </c:choose>
		 %{alb_administration} - %{${title}}
		 </title>
		
		<link rel="stylesheet" type="text/css" href="../../skin/admin.css">
		<link rel="stylesheet" type="text/css" href="../../skin/admintreemenu.css">
		<link rel="stylesheet" type="text/css" href="../../skin/adminotherlayout.css">
		
		<!--start styles css-->
	    <link rel="stylesheet" type="text/css" href="../../skin/styles.css">
	    <link rel="stylesheet" type="text/css" href="../../skin/siteaggregationmenu.css">
	    <link rel="stylesheet" type="text/css" href="../../skin/treemenu.css">
	    <link rel="stylesheet" type="text/css" href="../../skin/calendar.css">
	    <link rel="stylesheet" type="text/css" href="../../skin/pulldown.css">
	    <link rel="stylesheet" type="text/css" href="../../skin/calendarinput.css">
	    <link rel="stylesheet" type="text/css" href="../../skin/mySiteMap.css">
	    <link rel="stylesheet" type="text/css" href="../../skin/commandbar.css">
	    <link rel="stylesheet" type="text/css" href="../../skin/tab.css">
	    <link rel="stylesheet" type="text/css" href="../../skin/widget.css">
	    
	    <link rel="stylesheet" type="text/css" href="../../skin/groupsettingmodal.css">
	    <link rel="stylesheet" type="text/css" href="../../skin/message.css">
	    <link rel="stylesheet" type="text/css" href="../../skin/minibrowser.css">
	    <link rel="stylesheet" type="text/css" href="../../skin/ranking.css">
	    <link rel="stylesheet" type="text/css" href="../../skin/widgetranking.css">
	    <link rel="stylesheet" type="text/css" href="../../skin/rssreader.css">
	    <link rel="stylesheet" type="text/css" href="../../skin/maximizerssreader.css">
	    <link rel="stylesheet" type="text/css" href="../../skin/information.css">
	    <link rel="stylesheet" type="text/css" href="../../skin/ticker.css">
		<!--end styles css-->

		<script>
			<jsp:include page="/prpsrv" flush="true" />
			var isTabView = false;
			var is_sessionId = null;
			var is_squareId = "<%= squareId %>";

			function getInfoScoopURL() {
				var currentUrl = location.href;
				return currentUrl.replace(/\/(manager)\/.*/, "");
			}
			var hostPrefix = getInfoScoopURL();
			
			staticContentURL = /^(http|https):\/\//.test(staticContentURL) ? staticContentURL : hostPrefix;
			var imageURL = staticContentURL + "/skin/imgs/";
			
			var IS_Portal = {};
			
			var d = new Date();
			IS_Portal.clientTimeZone = String(-d.getTimezoneOffset());
			
			var is_userId = <%=uid != null ? "\"" + uid + "\"" : "null" %>;

			var IS_forbiddenURLs = <%= ForbiddenURLService.getHandle().getForbiddenURLsJSON() %>;
			
			var localhostPrefix = "<%=request.getScheme()%>://localhost:<%=request.getServerPort()%><%=request.getContextPath()%>";
		</script>

		<script src="../../js/resources/resourceBundle.jsp"></script>
		<script src="../../admin/js/resources/resourceBundle.jsp"></script>
	    <script src="../../js/gadget/features/core:rpc:pubsub:pubsub-2:infoscoop.js?c=1"></script>
		
		<!--start script-->
	    <script src="../../js/lib/prototype-1.7.1.js"></script>
		<script src="../../js/lib/scriptaculous-js-1.9.0/effects.js"></script>
		<script src="../../js/lib/scriptaculous-js-1.9.0/dragdrop.js"></script>
		<script src="../../js/lib/syntacticx-livepipe-ui/livepipe.js"></script>
		<script src="../../js/lib/syntacticx-livepipe-ui/tabs.js"></script>
		<script src="../../js/lib/syntacticx-livepipe-ui/resizable.js"></script>
		<script src="../../js/lib/syntacticx-livepipe-ui/window.js"></script>

		<script src="../../admin/js/lib/popupmenu.js"></script>

		<script src="../../js/utils/utils.js"></script>
		<script src="../../js/utils/domhelper.js"></script>
		<script src="../../js/utils/ajaxpool/ajax.js"></script>
		<script src="../../js/utils/ajax304.js"></script>
		<script src="../../js/lib/date/date.js"></script>
		<script src="../../js/lib/rsa/jsbn.js"></script>
		<script src="../../js/lib/rsa/prng4.js"></script>
		<script src="../../js/lib/rsa/rng.js"></script>
		<script src="../../js/lib/rsa/rsa.js"></script>
		<script src="../../js/lib/extras-array.js"></script>
		<script src="../../js/utils/msg.js"></script>
		<script src="../../js/utils/EventDispatcher.js"></script>
		<script src="../../js/utils/CalendarInput.js"></script>
		<script src="../../js/utils/Request.js"></script>
		<script src="../../js/utils/Validator.js"></script>
		<script src="../../js/utils/groupSettingModal.js"></script>

		
		<script src="../../js/commands/UpdatePropertyCommand.js"></script>
		<script src="../../js/widgets/Widget.js"></script>
		<script src="../../js/widgets/WidgetHeader.js"></script>
		<script src="../../js/widgets/WidgetEdit.js"></script>
		<script src="../../js/DragWidget.js"></script>
		<script src="../../js/widgets/rssreader/RssReader.js"></script>
		<script src="../../js/widgets/rssreader/RssItemRender.js"></script>
		<script src="../../js/widgets/MultiRssReader/MultiRssReader.js"></script>
	    <script src="../../js/widgets/information/Information.js"></script>
	    <script src="../../js/widgets/information/Information2.js"></script>
	    <script src="../../js/widgets/calendar/Calendar.js"></script>
	    <script src="../../js/widgets/calendar/iCalendar.js"></script>
	    <script src="../../js/widgets/MiniBrowser/MiniBrowser.js"></script>
	    <script src="../../js/widgets/MiniBrowser/FragmentMiniBrowser.js"></script>
	    <script src="../../js/widgets/WidgetRanking/WidgetRanking.js"></script>
	    <script src="../../js/widgets/Message/Message.js"></script>
		
		<script src="../../admin/js/Admin.js"></script>
		<script src="../../admin/js/AdminDragDrop.js"></script>
		<script src="../../admin/js/AdminInstantEdit.js"></script>
		<script src="../../admin/js/AdminSiteAggregationMenu.js"></script>
		<script src="../../admin/js/AdminSearchEngine.js"></script>
		<script src="../../admin/js/AdminProperties.js"></script>
		<script src="../../admin/js/AdminProxyConf.js"></script>
		<script src="../../admin/js/AdminI18N.js"></script>
		<script src="../../admin/js/AdminCommonModals.js"></script>
		<script src="../../admin/js/AdminDefaultPanel.js"></script>
		<script src="../../admin/js/AdminDefaultPanelModals.js"></script>
		<script src="../../admin/js/AdminPortalLayout.js"></script>
		<script src="../../admin/js/AdminWidgetConf.js"></script>
		<script src="../../admin/js/AdminEditWidgetConf.js"></script>
		<script src="../../admin/js/AdminHTMLFragment.js"></script>
		<script src="../../admin/js/AdminPortalAdmins.js"></script>
		<script src="../../admin/js/AdminMenuExplorer.js"></script>
		<script src="../../admin/js/AdminForbiddenURL.js"></script>
		<script src="../../admin/js/AdminGadgetUploadForm.js"></script>
		<script src="../../admin/js/AdminInformation.js"></script>
		<script src="../../admin/js/AdminAuthentication.js"></script>
		<script src="../../admin/js/AdminUtil.js"></script>	
		<script src="../../admin/js/AdminExtApps.js"></script>
		<!--end script-->
		
		<script src="../../js/lib/jquery-1.9.1.min.js"></script>
		<script>
			jQuery.noConflict();
			$jq = jQuery;

			var rsaPK = new RSAKey();
			rsaPK.setPublic("<%= RSAKeyManager.getInstance().getModulus() %>", "<%= RSAKeyManager.getInstance().getPublicExponent() %>");
			
			IS_WidgetConfiguration = <jsp:include page="/widconf" flush="true" />;
			IS_WidgetIcons = <jsp:include page="/gadgeticon" flush="true" />;

			$jq(function(){
				$jq("#messageIcon").click(function(){
					msg.showPopupDialog(hostPrefix);
				});
				$jq("#admin-tabs .tab").click(function(){
					if(!ISA_Admin.checkUpdated())
						return false;
				});
				$jq("#admin-side .side-bar .checkUpdate").click(function(){
					if(!ISA_Admin.checkUpdated())
						return false;
				});
			});
		</script>
	</head>
