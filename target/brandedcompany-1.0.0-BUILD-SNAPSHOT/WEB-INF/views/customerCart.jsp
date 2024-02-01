<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="code" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="customerId" value="${customer.customerId}"/>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><spring:message code="customer.cart"/></title>
    <script src="https://code.jquery.com/jquery-1.11.3.js"></script>
    <link rel="stylesheet" href="<c:url value='/resources/css/customerNavMenu.css'/>" type="text/css">
    <link rel="stylesheet" href="<c:url value='/resources/css/customerCart.css'/>" type="text/css">
</head>
<body>
<div class="container">
    <jsp:include page="customerNavMenu.jsp"/>

    <div class="item-container">
        <h2 class="writing-header"><spring:message code="customer.cart"/></h2>
        <c:choose>
        <c:when test="${empty items}">
            <div class="emptyBox">
                <h2>카트에 물건을 담아주세요.</h2>
            </div>
        </c:when>
        <c:otherwise>
        <table>
            <tr>
                <td><input type="checkbox" id="allChecker" onchange="checkAll()"></td>
                <td><spring:message code="customerCart.productName"/></td>
                <td><spring:message code="customerCart.productId"/></td>
                <td><spring:message code="customerCart.unitPrice"/></td>
                <td><spring:message code="customerCart.quantity"/></td>
                <td><spring:message code="customerCart.totalPrice"/></td>
            </tr>
            <c:forEach items="${items}" var="item">
                <tr>
                    <td>
                        <label name="itemCheckerLabel">
                            <input type="checkbox" class="itemChecker"
                                   data-customer-id="${item.customerId}"
                                   data-product-id="${item.productId}"/></label>
                    </td>
                    <td>${item.productName}</td>
                    <td>${item.productId}</td>
                    <td>${item.unitPrice}</td>
                    <td class="itemQuantity"
                        data-customer-id="${item.customerId}"
                        data-product-id="${item.productId}"
                        data-product-name="${item.productName}"
                        data-unit-price="${item.unitPrice}">
                        <input class="quantityInput" type="number" min="1" max="=255" value="${item.quantity}">
                    </td>
                    <td><fmt:formatNumber value="${item.unitPrice * item.quantity}" pattern=".00"/></td>
                </tr>
            </c:forEach>
        </table>
        <div class="paging-container">
            <div class="paging">
                <c:choose>
                    <c:when test="${pageHandler.showPrev}">
                        <a href="<c:url value="/customer/${customerId}/cart${pageHandler.getQueryString(pageHandler.beginPage-1)}"/>">
                            &lt;
                        </a>
                    </c:when>
                </c:choose>

                <c:forEach var="page" begin="${pageHandler.beginPage}"
                           end="${pageHandler.endPage}">
                    <a href="<c:url value="/customer/${customerId}/cart${pageHandler.getQueryString(page)}"/>">${page}</a>
                </c:forEach>

                <c:choose>
                    <c:when test="${pageHandler.showNext}">
                        <a href="<c:url value="/customer/${customerId}/cart${pageHandler.getQueryString(pageHandler.endPage+1)}"/> ">
                            &gt;
                        </a>
                    </c:when>
                </c:choose>
            </div>
        </div>
        <div class="btnBox">
            <button id="btnOrder" class="btn">주문하기</button>
            <button id="btnDelete" class="btn">삭제</button>
            <button id="btnAllDelete" class="btn">비우기</button>
        </div>
    </div>
    </c:otherwise>
    </c:choose>
</div>

</body>
<script>
    //전체 선택/ 전체 선택 해제
    function checkAll() {
        const $itemCheckers = document.querySelectorAll('.itemChecker');

        if ($('#allChecker').prop("checked"))
            $itemCheckers.forEach(checker => checker.checked = true);
        else $itemCheckers.forEach(checker => checker.checked = false);


    }

    $(document).ready(function () {
        // 물품이 없으면 아래 스타일 적용
        ${empty items}
            ? $('.container').css('text-align', 'center') : '';
        const $itemCheckers = document.querySelectorAll('.itemChecker');

        // 삭제
        $('#btnDelete').on('click', function () {

            $.ajax({
                type: 'delete',
                url: '/brandedCompany/customer/${customerId}/cart',
                headers: {'content-type': 'application/json'},
                data: JSON.stringify([...$itemCheckers].filter(checker => checker.checked).map(val => val.dataset.productId)),
                success: function (id) {
                    location.href = '/brandedCompany/customer/'+id+'/cart${pageHandler.getQueryString(pageHandler.PAGE)}';
                },
                error: function (id) {
                    location.href = '/brandedCompany/customer/'+id+'/myPage';
                }
            });
        });

        // 비우기
        $('#btnAllDelete').on('click', function () {
            const itemCheckers = document.querySelectorAll('.itemChecker');
            $.ajax({
                type: 'delete',
                url: '/brandedCompany/customer/${customerId}/cart${pageHandler.queryString}',
                headers: {'content-type': 'application/json'},
                data: JSON.stringify([...itemCheckers].map(id => id.dataset.productId)),
                success: function (id) {
                    location.href = '/brandedCompany/customer/'+id+'/cart${pageHandler.getQueryString(pageHandler.PAGE)}';
                },
                error: function (id) {
                    location.href = '/brandedCompany/customer/'+id+'/myPage';
                }
            });
        });
        // 주문
        $('#btnOrder').on('click', function () {
            let itemCheckers = document.querySelectorAll('.itemChecker');
            let checkedMap = [...itemCheckers].filter(checker => checker.checked);
            if (0 === checkedMap.length) {
                alert('원하시는 상품을 선택해주세요.');
                return;
            }
            let itemQuantities = document.querySelectorAll(".itemQuantity");
            let checked = [];

            for (let i = 0; i < checkedMap.length; i++) {
                for (let j = 0; j < itemQuantities.length; j++) {
                    if (checkedMap[i].dataset.productId === itemQuantities[j].dataset.productId &&
                        checkedMap[i].dataset.customerId === itemQuantities[j].dataset.customerId) {
                        checked.push({
                            customerId: itemQuantities[j].dataset.customerId,
                            productId: itemQuantities[j].dataset.productId,
                            quantity: itemQuantities[j].firstElementChild.value,
                            unitPrice: itemQuantities[i].dataset.unitPrice
                        });
                    }
                }
            }

            let $order = [];
            let $orderItems = [];
            let $carts = [];

            checked.forEach((item, index) => {
                if (0 === index) {
                    $order.push({
                        customerId: item.customerId,
                        status: 'Pending',
                    });
                }
                $carts.push({
                    productId: item.productId,
                    customerId: item.customerId
                });
                $orderItems.push({
                    productId: item.productId,
                    quantity: item.quantity,
                    unitPrice: item.unitPrice
                });
            });

            $.ajax({
                type: 'post',
                url: '/brandedCompany/customer/${customerId}/order',
                headers: {'content-type': 'application/json'},
                data: JSON.stringify({
                    orders: $order,
                    orderItems: $orderItems,
                    carts: $carts
                }),
                success: function (id) {
                    /*// 주문 성공 -> 체크 해지
                    let checkedMap = [...itemCheckers].filter(checker => checker.checked = false);*/
                    // 주문 내역 페이지 이동 여부
                    if (confirm("주문내역 페이지로 이동하시겠습니까?"))
                        location.href = '/brandedCompany/customer/' + id + '/orderHistory?page=1';
                    else
                    location.href = '/brandedCompany/customer/'+id+'/cart${pageHandler.getQueryString(pageHandler.PAGE)}';
                },
                error: function (id) {
                    alert('주문 실패했습니다.')
                    location.href = '/brandedCompany/customer/'+id+'/myPage';
                }
            });
        });
    });
</script>

</html>