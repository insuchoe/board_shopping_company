<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="code" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="customerId" value="${customer.customerId}"/>
<nav id="menuNav">
    <div id="navMenuBox">
        <div id="menu">
            <ul>
                <li><a href="<c:url value='/customer/${customerId}/myPage'/>">
                    <spring:message code="customerMyPage.title"/>
                </a></li>
                <li><a href="<c:url value='/customer/${customerId}/orderHistory?page=1'/>">
                    <spring:message code="customerOrderHistory.title"/>
                </a></li>
                <li><a href="<c:url value='/customer/${customerId}/product?page=1'/>"><spring:message code="customerProduct.title"/></a></li>
                <li><a href="<c:url value='/customer/${customerId}/cart?page=1'/>"><spring:message code="customer.cart"/></a></li>
                <li><a href="<c:url value='/customer/${customerId}/loginOut/logout'/>"><spring:message code="customerLogout"/></a></li>
            </ul>
        </div>
    </div>
</nav>
