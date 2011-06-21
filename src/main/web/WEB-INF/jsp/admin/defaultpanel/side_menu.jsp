<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="org.infoscoop.service.PortalAdminsService" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
	PortalAdminsService adminService = PortalAdminsService.getHandle();
	boolean isTreeAdminUser = !adminService.isPermitted("menu") && adminService.isPermitted("menu_tree");
%>

<div id="defaultPanel-side-bar" class="side-bar">
<ul >
<%if( adminService.isPermitted("defaultPanel") ){%>
<!-- TODO 国際化 -->
	<li class="tab <c:if test="${type == 'defaultPanel_tabs'}">selected</c:if>"><a href="../defaultpanel/index"><span>タブ</span></a></li>
	<li class="tab <c:if test="${type == 'defaultPanel_commandBar'}">selected</c:if>"><a href="../defaultpanel/commandBar_index"><span>コマンドバー</span></a></li>
<%}%>
<%if( adminService.isPermitted("portalLayout") ){%>
	<li class="tab <c:if test="${type == 'defaultPanel_portalLayout'}">selected</c:if>">
		<a href="../defaultpanel/portalLayout_index"><span>%{alb_portalLayout}</span></a>
	</li>
<%}%>

</ul>
</div>