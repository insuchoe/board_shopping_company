<%@ page import="com.fasterxml.jackson.annotation.JsonTypeInfo" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="code" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="empId" value="${cookie.rememberEmployeeId.value}"/>
<c:set var="isRmrEmpId" value="${not empty empId}"/>
<%--<c:set var="err" value="${sessionScope.employeeError}"/>--%>
<%--<c:set var="hasErr" value="${not empty err}"/>--%>
<c:set var="err" value="${empLgnErr}"/>
<c:set var="hasErr" value="${not empty err}"/>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><spring:message code="employeeLogin.title"/></title>

    <link rel="stylesheet" href="<c:url value='/resources/css/employeeLogin.css'/>" type="text/css">

    <script src="https://code.jquery.com/jquery-1.11.3.js"></script>

</head>
<body>
<form action="<c:url value="/employee/loginOut/login"/>" method="post">
    <h3 id="title"><spring:message code="employeeLogin.title"/></h3>
    <div id="message" class="message">
        <c:if test="${hasErr}">
            <h3><spring:message code="${err}"/></h3>
        </c:if>
    </div>
    <input type="text" name="employeeId" value="${isRmrEmpId ?
    empId : '' }" placeholder="<spring:message code='eIdInput'/>" autofocus>
    <input type="text" name="name" placeholder="<spring:message code='eNmInput'/>">
    <input type="hidden" name="firstName">
    <input type="hidden" name="lastName">

    <button type="submit"><spring:message code="lgnBtn"/></button>
    <div>
        <label><input type="checkbox" name="rememberId" value="on"
        ${isRmrEmpId ? 'checked' : '' }><spring:message code="rmrEId"/></label>
    </div>
    <ul>
        <li><a href="<c:url value='/customer/loginOut/login'/>"><spring:message code="customerLogin.title"/></a></li>
    </ul>
</form>

<script>

    let $name = $("input[name=name]");
    // hidden tag
    let $firstName = $("input[name=firstName]");
    let $lastName = $("input[name=lastName]");

    //아이디 기억하기 && 에러가 있으면
    if (${isRmrEmpId and hasErr}) {
        //name 에 에러가 있으면
        let $err = "${err}";
        if (-1 !== $err.lastIndexOf('firstName')
            || -1 !== $err.lastIndexOf('lastName')
            || -1 !== $err.lastIndexOf('fullName')
            || -1 !== $err.lastIndexOf('name'))
            $name.focus();
        //유효하지 않은 고객이라면
        if (-1 !== $err.indexOf('notFound'))
            $name.focus();
    }


    //접속 버튼 클릭
    document.querySelector("button[type=submit]").addEventListener("click", function () {
        // 새로운 값을 받기 위해 firstName,lastName 초기화
        $firstName.val('');
        $lastName.val('');
        try {
            // $firstName.val('');
            // $lastName.val('');
            // [firstName lastName] 형식 띄어 썻다면
            // name value 에 띄어쓰기 기준으로 앞은 firstName ,뒤는 lastName 구분
            if (-1 !== $name.val().indexOf(' ')) {
                let $firstNameVal, $lastNameVal;
                [$firstNameVal, $lastNameVal] = [...$name.val().split(' ')];
                $firstName.val($firstNameVal);
                $lastName.val($lastNameVal);
                return;
            } else {
                $firstName.val($name.val());
            }

        } catch (error) {
            console.log(error);
        }
    });

</script>
</body>
</html>