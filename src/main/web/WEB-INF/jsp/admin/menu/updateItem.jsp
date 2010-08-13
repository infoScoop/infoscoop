<%@ page contentType="text/html; charset=UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
「${menuItem.title}」を更新しました。
<script type="text/javascript">
renameItem("${menuItem.id}", "${menuItem.title}");
</script>