<%@ page contentType="text/html;charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="code" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="employeeId" value="${employee.employeeId}"/>

<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=chrome">
    <title><spring:message code="allCustomer.orderHistory"/></title>

    <script src="https://code.jquery.com/jquery-1.11.3.js"></script>
    <link rel="stylesheet" href="<c:url value='/resources/css/allCustomerOrderHistory.css'/>" type="text/css">
    <link rel="stylesheet" href="<c:url value='/resources/css/employeeNavMenu.css'/>" type="text/css">
</head>
<body>
<div class="container">
    <jsp:include page="employeeNavMenu.jsp"/>
    <div class="orderHistory-container">
        <h2 class="writing-header"><spring:message code="employee.orderHistory"/></h2>

        <div class="search-container">
            <form action='<c:url value="/employee/${employeeId}/orderHistory"/>' method="get">
                <input name="page" type="hidden" value="${orderHistoryPageHandler.PAGE}"/>
                <label for="search-target">
                    <select id="search-target" name="target" onchange="selectSearchStatus()">
                        <option value="ORDER_DATE" ${orderHistoryPageHandler.searchCondition.target eq 'ORDER_DATE' or orderHistoryPageHandler.searchCondition.target eq null ? "selected" : ""}>
                            주문날짜
                        </option>
                        <option value="ORDER_STATUS" ${orderHistoryPageHandler.searchCondition.target eq 'ORDER_STATUS'  ? "selected" : ""}>
                            주문상태
                        </option>
                        <option value="ORDERER" ${orderHistoryPageHandler.searchCondition.target eq 'ORDERER'  ? "selected" : ""}>
                            주문자
                        </option>
                    </select>
                </label>

                <label for="search-keyword">
                    <input type="text" id="search-keyword" name="keyword"
                           value="${orderHistoryPageHandler.searchCondition.keyword}"
                           placeholder="상품을 입력하세요">
                </label>
                <label>
                    <select id="search-status" name="status">

                        <option value="Pending" ${orderHistoryPageHandler.searchCondition.status eq 'Pending' or
                                orderHistoryPageHandler.searchCondition.status eq null ? "selected" : ""}>
                            보류
                        </option>
                        <option value="Shipped" ${orderHistoryPageHandler.searchCondition.status eq 'Shipped'
                                ? "selected" : ""}>
                            출하
                        </option>
                        <option value="Canceled" ${orderHistoryPageHandler.searchCondition.status eq 'Canceled'
                                ? "selected" : ""}>
                            취소
                        </option>
                    </select>
                </label>


                <label for="search-direction">
                    <select id="search-direction" name="direction">

                        <option value="ASC" ${orderHistoryPageHandler.searchCondition.direction eq 'ASC' or
                                orderHistoryPageHandler.searchCondition.direction eq null ? "selected" : ""}>
                            오름차순
                        </option>
                        <option value="DESC" ${orderHistoryPageHandler.searchCondition.direction eq 'DESC'
                                ? "selected" : ""}>
                            내림차순
                        </option>
                    </select>
                </label>
                <input type="submit" value="검색" class="btn">
            </form>
        </div>

<%--        =>${orderHistoryPageHandler.queryString}--%>

        <c:choose>
            <c:when test="${0 ne orderHistoryPageHandler.TOTAL_COUNT}">

                <table>
                    <tr>
                        <td></td>
                        <td>상품</td>
                        <td>개수</td>
                        <td>가격</td>
                        <td>총 가격</td>
                        <td>주문 상태</td>
                    </tr>
                    <c:forEach items="${orderHistory}" var="history" varStatus="status">

                        <%--현재 인덱스가 다음 주문인덱스와 다른 주문내역일 때  or 현재 인덱스가 0 일 때--%>
                        <c:if test="${orderHistory[status.index-1].orderId ne history.orderId or 0 eq status.index}">
                            <td class="separator"></td>
                            <tr>
                            <th rowspan="${idsRowSpans.get(history.orderId)}" class="orderInfo-headerBox">
                                <div class="orderInfo-header">
                                    <div><span class="orderId">주문번호</span><br/><span
                                            class="historyOrderId">${history.orderId}</span></div>
                                    <div><span class="orderDate">주문일자</span><br/><span
                                            class="historyOrderDate">${history.orderDate}</span></div>
                                    <div><span class="orderer">주문자</span><br/><span
                                            class="historyOrderer">${history.orderer}</span></div>
                                    <div><span class="orderTotalPrice">결제금액</span><br/><span
                                            class="historyOrderTotalPrice">${orderIdTotalPrice.get(history.orderId)}
                                    </span></div>
                                    <div><span class="orderAddress">주소</span><br/><span
                                            class="historyOrderAddress">${history.address}
                                    </span></div>

                                </div>
                            </th>
                        </c:if>
                        <td class="productName">${history.productName}</td>
                        <td>${history.quantity}</td>
                        <td>${history.unitPrice}</td>
                        <td>${history.totalPrice}</td>
                        <td>${history.orderStatus}</td>
                        </tr>
                    </c:forEach>

                </table>

                <div class="paging-container">
                    <div class="paging">
                        <c:choose>
                            <c:when test="${orderHistoryPageHandler.showPrev}">
                                <a href="<c:url value="/employee/${employeeId}/orderHistory${orderHistoryPageHandler.getQueryString(orderHistoryPageHandler.beginPage-1)}"/>">
                                    &lt;
                                </a>
                            </c:when>
                        </c:choose>

                        <c:forEach var="page" begin="${orderHistoryPageHandler.beginPage}"
                                   end="${orderHistoryPageHandler.endPage}">
                            <a href="<c:url value="/employee/${employeeId}/orderHistory${orderHistoryPageHandler.getQueryString(page)}"/>">${page}</a>
                        </c:forEach>

                        <c:choose>
                            <c:when test="${orderHistoryPageHandler.showNext}">
                                <a href="<c:url value="/employee/${employeeId}/orderHistory${orderHistoryPageHandler.getQueryString(orderHistoryPageHandler.endPage+1)}"/> ">
                                    &gt;
                                </a>
                            </c:when>
                        </c:choose>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="emptyBox">
                <h2>주문내역이 없습니다.</h2>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
<script>


    function selectSearchStatus() {
        let $selectedTarget = $('#search-target option:selected').val();
        let $status = $('#search-status');
        let $direction = $("#search-direction");
        let $keyword = $("input[name=keyword]");


        console.log($selectedTarget);

        $keyword.attr('placeholder', '');
        $status.attr('disabled', false);

        // $status.attr('disabled', true);
        $keyword.attr('disabled', false);

        $direction.attr('disabled', false);
        // $status.attr('disabled', false);


        if ('ORDER_DATE' === $selectedTarget) {
            $keyword.attr('placeholder', '- 없이 년월일을 적어주세요.');
            $status.attr('disabled', true);
            $keyword.attr('type','number');
        }
            // else {
            //     $("input[name=keyword]").attr('placeholder', '');
            //     $status.attr('disabled', false);
        // }

        else if ('ORDER_STATUS' === $selectedTarget) {
            $status.attr('disabled', false);
            $keyword.attr('disabled', true);
            $keyword.val('');
        }
            // else {
            //     $status.attr('disabled', true);
            //     $("input[name=keyword]").attr('disabled', false);
        // }

        else if ('ORDERER' === $selectedTarget) {
            $keyword.attr('placeholder', '주문자를 입력하세요.');
            $status.attr('disabled', true);
            $keyword.attr('type','text');

        }
        // else {
        //     $direction.attr('disabled', false);
        //     $status.attr('disabled', false);
        // }
    }

    selectSearchStatus();
</script>
</body>
</html>
