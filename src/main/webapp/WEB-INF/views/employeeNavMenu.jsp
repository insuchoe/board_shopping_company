<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="code" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="employeeId" value="${employee.employeeId}"/>
<nav id="menuNav">
    <div id="navMenuBox">
        <div id="menu">

            <ul>
                <li><a href="<c:url value='/employee/${employeeId}/myPage'/>"><spring:message code="employeeMyPage.title"/></a></li>
                <li><a href="<c:url value='/employee/${employeeId}/board?page=1'/>"><spring:message code="employee.board.title"/></a></li>
                <li><a href="<c:url value='/employee/${employeeId}/inventory?page=1'/>"><spring:message code="inventory.title"/></a></li>
                <li><a href="<c:url value='/employee/${employeeId}/orderHistory?page=1'/>"><spring:message code="employee.orderHistory"/></a></li>
                <li><a href="<c:url value='/employee/${employeeId}/loginOut/logout'/>"><spring:message code="employee.logOut"/></a></li>
            </ul>
        </div>
    </div>
</nav>
