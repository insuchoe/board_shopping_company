<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="code" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="customerId" value="${customer.customerId}"/>


<!doctype html>
<html lang="en">
<head>
    <title><spring:message code="customerOrderHistory.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/customerNavMenu.css'/>" type="text/css">
    <link rel="stylesheet" href="<c:url value='/resources/css/customerOrderHistory.css'/>" type="text/css"/>
    <script src="https://code.jquery.com/jquery-1.11.3.js"></script>
</head>
<body>
<div class="container">
    <jsp:include page="customerNavMenu.jsp"/>
    <div class="orderHistory-container">
        <h2 class="writing-header"><spring:message code="customerOrderHistory.title"/></h2>
        <c:choose>
            <c:when test="${0 ne pageHandler.TOTAL_COUNT}">
                <div class="search-container">
                        <%--1개월,3개월,6개월전부터 오늘까지 주문내역 조회--%>
                    <form action="<c:url value='/customer/${customerId}/orderHistory?page=1'/> "
                          method="get">
                        <label for="search-page">
                            <input id="search-page" name="page" type="hidden" value="${pageHandler.PAGE}">
                        </label>
                        <label for="search-monthAgo" onchange="selectSearchMonthAgo()">
                            <select id="search-monthAgo" name="monthAgo" onchange="removeName()">
                                <option value="1" ${pageHandler.searchCondition.monthAgo eq 1 ? "selected" : ""}>
                                    1개월
                                </option>
                                <option value="3" ${pageHandler.searchCondition.monthAgo eq 3 ? "selected" : ""}>
                                    3개월
                                </option>
                                <option value="6" ${pageHandler.searchCondition.monthAgo eq 6 ? "selected" : ""}>
                                    6개월
                                </option>
                                <option ${pageHandler.searchCondition.monthAgo eq null ? 'selected' : ''}>
                                    전체
                                </option>
                            </select>
                        </label>
                    </form>

                </div>

                <table>
                    <tr>
                        <td></td>
                        <td></td>
                    </tr>
                    <c:forEach items="${items}" var="history" varStatus="status">
                        <%--현재 인덱스가 다음 주문인덱스와 다른 주문내역일 때  or 현재 인덱스가 0 일 때--%>
                        <c:if test="${items[status.index-1].orderId ne history.orderId or 0 eq status.index}">
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
                                    <div>
                                        <c:if test="${'Pending' eq history.orderStatus}">
                                            <button class="btn" data-order-id="${history.orderId}">
                                                <spring:message code="customer.orderCancel"/>
                                            </button>
                                        </c:if>
                                    </div>

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
                            <c:when test="${pageHandler.showPrev}">
                                <a href="<c:url value="/customer/${customerId}/orderHistory${pageHandler.getQueryString(pageHandler.beginPage-1)}"/>">
                                    &lt;
                                </a>
                            </c:when>
                        </c:choose>

                        <c:forEach var="page" begin="${pageHandler.beginPage}"
                                   end="${pageHandler.endPage}">

                            <a href="<c:url value='/customer/${customerId}/orderHistory${pageHandler.getQueryString(page)}'/>">${page}</a>
                        </c:forEach>

                        <c:choose>
                            <c:when test="${pageHandler.showNext}">
                                <a href="<c:url
                                value="/customer/${customerId}/orderHistory${pageHandler.getQueryString(pageHandler.endPage+1)}"/> ">
                                    &gt;
                                </a>
                            </c:when>
                        </c:choose>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="emptyBox">
                    <h2>등록한 상품이 없습니다.</h2>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
<script>


    function selectSearchMonthAgo() {
        $("form").submit();
    }

    function removeName() {
        if($('option:selected').val()==='전체') $('select[name=monthAgo]').removeAttr('name');

    }

    $(document).ready(function () {
        $('.btn').on('click', function (e) {
            let $target = e.target;
            let $orderId = $target.dataset.orderId;
            $.ajax({
                type: 'patch',
                url: '/brandedCompany/customer/${customerId}/order/' + $orderId + '?status=Canceled',
                headers: {'content-type': 'application/json'},
                data: JSON.stringify($orderId),
                success: function (id) {
                    location.href = '/brandedCompany/customer/'+id+'/orderHistory${pageHandler.queryString}'
                },
                error: function (id) {
                    alert('서버내부오류입니다.. \n잠시 후 다시 시도해주세요.');
                }
            });
        });
    });

</script>
</body>
</html>
