<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="success">「${menuItem.title}」を更新しました。</div>
<script type="text/javascript">
updateItemInTree("${menuItem.id}", "${menuItem.title}", ${menuItem.publish == 1});
</script>