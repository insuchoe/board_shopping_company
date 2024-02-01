<%@ page contentType="text/html;charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="code" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="customerId" value="${customer.customerId}"/>


<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title><spring:message code="customerProduct.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/customerNavMenu.css'/>" type="text/css">
    <link rel="stylesheet" href="<c:url value='/resources/css/customerProduct.css'/>" type="text/css">
    <script src="https://code.jquery.com/jquery-1.11.3.js"></script>
</head>
<body>
<div class="container">
    <jsp:include page="customerNavMenu.jsp"/>

    <div class="product-container">
        <h2 class="writing-header"><spring:message code="customerProduct.title"/></h2>
        <c:choose>
        <c:when test="${0 ne pageHandler.TOTAL_COUNT}">
        <div class="search-container">
            <form action="<c:url value='/customer/${customerId}/product'/>" method="get">
                    <%--페이지--%>
                <label for="search-page">
                    <input id="search-page" name="page" type="hidden" value="${pageHandler.PAGE}">
                </label>
                <label for="search-target" onchange="selectSearchTarget()">
                    <select id="search-target" name="target"><%--정렬 대상 선택--%>
                        <option value="PRODUCT_NAME" ${pageHandler.target eq 'PRODUCT_NAME' or pageHandler.target eq null ? "selected" : ""}>
                            상품
                        </option>
                        <option value="LIST_PRICE" ${pageHandler.target eq 'LIST_PRICE' ? "selected" : ""}>
                            가격
                        </option>
                    </select>
                </label>
                <label for="search-direction" onchange="selectSearchDirection()">
                    <select id="search-direction" name="direction"><%--정렬 대상 선택--%>
                        <option value="ASC" ${pageHandler.direction eq 'ASC' or pageHandler.direction eq null ? "selected" : ""}>
                            오름차순
                        </option>
                        <option value="DESC" ${pageHandler.direction eq 'DESC' ? "selected" : ""}>
                            내림차순
                        </option>
                    </select>
                </label>
            </form>
        </div>
            <%--상품--%>
        <table>
            <c:forEach items="${items}" var="item">
                <tr>
                    <td><input type="checkbox" class="itemChecker"
                               data-product-id="${item.productId}"
                               data-product-category-id="${item.categoryId}"
                               data-product-name="${item.productName}"
                               data-product-description="${item.description}"
                               data-product-standard-cost="${item.standardCost}"
                               data-product-list-price="${item.listPrice}">
                    </td>
                    <td>${item.productId}</td>
                    <td>${item.productName}</td>
                    <td>${item.description}</td>
                    <td>${item.listPrice}</td>
                    <td>
                        <li><input type="number" min="1" max="255" value="1"
                                   data-product-quantity-id="${item.productId}"/></li>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <div class="paging-container">
            <div class="paging">
                <c:choose>
                    <c:when test="${pageHandler.showPrev}">
                        <a href="<c:url value="/customer/${customerId}/product${pageHandler.getQueryString(pageHandler.beginPage-1)}"/>">
                            <
                        </a>
                    </c:when>
                </c:choose>

                <c:forEach var="page" begin="${pageHandler.beginPage}"
                           end="${pageHandler.endPage}">
                    <a href="<c:url value="/customer/${customerId}/product${pageHandler.getQueryString(page)}"/>">${page}</a>
                </c:forEach>

                <c:choose>
                    <c:when test="${pageHandler.showNext}">
                        <a href="<c:url value="/customer/${customerId}/product${pageHandler.getQueryString(pageHandler.endPage+1)}"/> ">
                            >
                        </a>
                    </c:when>
                </c:choose>
            </div>
        </div>
        <div id="btnBox">
            <button id="btnAddCart" class="btn">카트담기</button>
            <button id="btnOrder" class="btn">주문하기</button>
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
<script>
    function selectSearchTarget() {
        $("form").submit();
    }

    function selectSearchDirection() {
        $("form").submit();
    }

    $(document).ready(function () {
        $('#btnAddCart').on('click', function () {

            //체크한 체크 박스
            let $checked = [...document.querySelectorAll('.itemChecker')]
                .filter(itemChecker => itemChecker.checked === true);

            let $carts = [];

            $checked.forEach(c => {
                    let dataProductId = c.getAttribute("data-product-id");
                    $carts.push(
                        {
                            customerId: ${customerId},
                            productId: dataProductId,
                            productName: c.getAttribute("data-product-name"),
                            quantity: $('input[data-product-quantity-id=' + dataProductId + ']').val(),
                            unitPrice: c.getAttribute("data-product-list-price"),
                        });
                }
            );

            // $carts.forEach(console.log);

            $.ajax({
                type: 'post',
                url: '/brandedCompany/customer/${customerId}/cart',
                headers: {'content-type': 'application/json'},
                data: JSON.stringify($carts),
                success: function (id) {
                    if (confirm("카트 페이지로 이동하시겠습니까?")) {
                        <%--alert('${pageHandler.queryString}');--%>
                        location.href = '/brandedCompany/customer/'+id+'/cart?page=1';
                    } else
                        location.href = '/brandedCompany/customer/'+id+'/product${pageHandler.queryString}';
                },
                error: function (id) {
                    alert("현재 점검중 입니다. 잠시 후 다시 이용해주세요.");
                    location.href = '/brandedCompany/customer/'+id+'/myPage';
                }
            });
        });

        // 주문
        $('#btnOrder').on('click', function () {
            let itemCheckers = document.querySelectorAll('.itemChecker');
            let $checked = [...itemCheckers].filter(checker => checker.checked);

            let checked = [];

            $checked.forEach(c => {
                let dataProductId = c.getAttribute("data-product-id");

                checked.push({
                    customerId: ${customerId},
                    productId: dataProductId,
                    quantity: $('input[data-product-quantity-id=' + dataProductId + ']').val(),
                    unitPrice: c.getAttribute("data-product-list-price")
                });
            });

            let $order = [];
            let $orderItems = [];
            let $carts = [];

            checked.forEach((item, index) => {
                if (0 === index) {
                    $order.push({
                        customerId: ${customerId},
                        status: 'Pending',
                    });
                }

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
                    orderItems: $orderItems
                }),
                success: function (id) {
                    /*// 주문 성공 -> 체크 해지
                    let checkedMap = [...itemCheckers].filter(checker => checker.checked = false);*/
                    // 주문 내역 페이지 이동 여부 판단
                    if (confirm("주문내역 페이지로 이동하시겠습니까?")) {
                        location.href = '/brandedCompany/customer/'+id+'/orderHistory?page=1';
                    } else {
                        location.href = '/brandedCompany/customer/'+id+'/product?page=1';
                    }
                },
                error: function (id) {
                    alert('주문 실패했습니다.')
                    location.href = '/brandedCompany/customer/'+id+'/myPage';
                }
            });
        });
    });


</script>
</body>
</html>
