<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="org.infoscoop.properties.InfoScoopProperties"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<tiles:insertDefinition name="prepare.definition" flush="true">
    <tiles:putAttribute name="title" value="lb_loginTitle" />
    <tiles:putAttribute name="body" type="string">

<h2 class="text-primary">%{lb_loginTitle}</h2>

<c:if test="${!empty errorMsg}" >
    <div class="alert alert-danger" role="alert">%{${errorMsg}}</div>
    <c:remove var="errorMsg" scope="session" />
</c:if>

    <form id="loginForm" name="loginForm" action="<c:url value="/login.do"/>" method="post">
        <div class="well col-sm-12">
            <div class="form-group">
                <label for="userId">%{lb_ee_uid_email}</label>
        <input class="form-control"  type="text" id="uid" name="j_username" placeholder="Enter email"/>
            </div>
            <div class="form-group">
                <label for="password">%{lb_password}</label>
        <input class="form-control" type="password" id="password" name="j_password" placeholder="Enter password" required />
            </div>
        </div>
        <div class="form-group col-sm-12">
            <input type="hidden" name="uid" class="form-control" value=${properties.userId} required/>
            <input type="hidden" id="key" name="key" value=${properties.key} required/>
            <button type="submit" class="btn btn-primary">%{lb_login}</button>
        </div>
    </form>

<script type="text/javascript">
    document.getElementById('uid').focus();
</script>

    </tiles:putAttribute>
</tiles:insertDefinition>
