<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
「${menuItem.title}」を更新しました。
<script type="text/javascript">
updateItemInTree("${menuItem.id}", "${menuItem.title}", ${menuItem.accessLevel == 1});
</script>