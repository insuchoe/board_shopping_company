<%@ page import="com.fasterxml.jackson.annotation.JsonTypeInfo" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="code" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="cusId" value="${cookie.rememberCustomerId.value}"/>
<c:set var="isRmrCusId" value="${not empty cusId}"/>
<c:set var="err" value="${cusLgnErr}"/>
<c:set var="hasErr" value="${not empty err}"/>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><spring:message code="customerLogin.title"/></title>

    <script src="https://code.jquery.com/jquery-1.11.3.js"></script>
    <link rel="stylesheet" href="<c:url value='/resources/css/customerLogin.css'/>" type="text/css">

</head>
<body>
<form action="<c:url value="/customer/loginOut/login"/>" method="post">
    <h3 id="title"><spring:message code="customerLogin.title"/></h3>
    <div id="message" class="message">
        <c:if test="${hasErr}">
            <spring:message code="${err}"/></h3>
        </c:if>
    </div>
    <input type="text" name="customerId" value="${isRmrCusId ? cusId : '' }"
           placeholder="<spring:message code='cIdInput'/>" autofocus>
    <input type="text" name="name" placeholder="<spring:message code='cNmInput'/>">
    <button type="submit"><spring:message code="lgnBtn"/></button>
    <div>
        <label><input type="checkbox" name="rememberId" value="on"
        ${isRmrCusId ? 'checked' : '' }><spring:message code="rmrCId"/></label>
    </div>
    <ul>
        <li><a href="<c:url value='/employee/loginOut/login'/>">직원 로그인</a></li>
    </ul>
</form>
</body>
<script>
    let $name = $("input[name=name]");
    //아이디 기억하기 && 에러가 있으면
    if (${isRmrCusId and hasErr}) {
        //name 에 에러가 있으면 || 유효하지 않은 직원이라면
        let $err = "${err}";
        if (-1 !== $err.lastIndexOf('name') || -1 !== $err.indexOf('notFound'))
            $name.focus();
    }
</script>
</html>