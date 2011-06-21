<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- 初期画面 -->
<!-- TODO 権限制御とタブの動的生成 -->
<div id="side-menu" style="<c:if test="${type != 'defaultPanel'}">display:none</c:if>" >
<p>&nbsp;</p>
<p><a href=""><span>タブ</span></a></p>
<p><a href=""><span>コマンドバー</span></a></p>
<p><a href=""><span>画面その他</span></a></p>
</div>

<!-- （プロパティ） -->

<!-- 管理者 -->
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