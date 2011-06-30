<%--
# infoScoop OpenSource
# Copyright (C) 2010 Beacon IT Inc.
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Lesser General Public License version 3
# as published by the Free Software Foundation.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Lesser General Public License for more details.
# 
# You should have received a copy of the GNU Lesser General Public
# License along with this program. If not, see
# <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>;.
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- defaultPanel -->
<!-- TODO ACL control and create menus dycamically and i18n -->
<div id="side-menu" style="<c:if test="${type != 'defaultPanel'}">display:none</c:if>" >
<p>&nbsp;</p>
<p><a href=""><span>タブ</span></a></p>
<p><a href=""><span>コマンドバー</span></a></p>
<p><a href=""><span>画面その他</span></a></p>
</div>

<!-- property if needed -->

<!-- administrator -->
<div id="side-menu" style="<c:if test="${type != 'administrator'}">display:none</c:if>" >
<p>&nbsp;</p>
<p><a href=""><span>管理者設定</span></a></p>
<p><a href=""><span>ロール設定</span></a></p>
</div>

<!-- OAuth -->
<div id="side-menu" style="<c:if test="${type != 'authentication'}">display:none</c:if>" >
<p>&nbsp;</p>
<p><a href=""><span>OAuthコンシューマー設定</span></a></p>
<p><a href=""><span>コンテナの証明書</span></a></p>
</div>