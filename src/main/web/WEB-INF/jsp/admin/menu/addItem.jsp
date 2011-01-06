<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="success">「${menuItem.title}」を追加しました。</div>
<script type="text/javascript">
addItemToTree("${menuItem.fkParent.id}", "${menuItem.id}", "${menuItem.title}", "${menuItem.gadgetInstance.type}", ${menuItem.accessLevel == 1});
</script>