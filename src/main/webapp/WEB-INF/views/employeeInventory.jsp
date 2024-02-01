<%@ page contentType="text/html;charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="code" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="employeeId" value="${employee.employeeId}"/>

<!DOCTYPE html>
<html>
<!DOCTYPE html>
<head>
    <meta charset="UTF-8">
    <title><spring:message code="inventory.title"/></title>

    <link rel="stylesheet" href="<c:url value='/resources/css/employeeNavMenu.css'/>" type="text/css">
    <link rel="stylesheet" href="<c:url value='/resources/css/employeeInventory.css'/>" type="text/css">
    <script src="https://code.jquery.com/jquery-1.11.3.js"></script>
</head>
<body>
<div class="container">
    <jsp:include page="employeeNavMenu.jsp"/>
    <div class="inventory-container">
        <h2 class="writing-header"><spring:message code="inventory.title"/></h2>
        <div class="search-container">
            <c:choose>
            <c:when test="${0 eq inventoryPageHandler.TOTAL_COUNT }">
                <div class="inventory-empty">
                    <h1>인벤토리에 등록된 정보가 없습니다.</h1>
                </div>
            </c:when>
            <c:otherwise>
            <form action='<c:url value="/employee/${employeeId}/inventory"/>' method="get">
                    <%--                <input name="page" type="hidden" value="${inventoryPageHandler.searchCondition.PAGE}"/>--%>
                <label for="search-target" >
                    <select id="search-target" name="sortTarget">

                        <option value="PRODUCT_NAME" ${inventoryPageHandler.searchCondition.sortTarget.name() eq 'PRODUCT_NAME' or
                                inventoryPageHandler.searchCondition.sortTarget eq null ? "selected" : ""}>
                            상품
                        </option>

                        <option value="LIST_PRICE" ${inventoryPageHandler.searchCondition.sortTarget.name() eq 'LIST_PRICE'  ? "selected" : ""}>
                            가격
                        </option>

                        <option value="QUANTITY" ${inventoryPageHandler.searchCondition.sortTarget.name() eq 'QUANTITY'  ? "selected" : ""}>
                            수량
                        </option>

                        <option value="REGION_NAME" ${inventoryPageHandler.searchCondition.sortTarget.name() eq 'REGION_NAME'  ? "selected" : ""}>
                            대륙
                        </option>

                        <option value="COUNTRY_NAME" ${inventoryPageHandler.searchCondition.sortTarget.name() eq 'COUNTRY_NAME'  ? "selected" : ""}>
                            나라
                        </option>

                        <option value="ADDRESS" ${inventoryPageHandler.searchCondition.sortTarget.name() eq 'ADDRESS'  ? "selected" : ""}>
                            주소
                        </option>

                        <option value="CITY" ${inventoryPageHandler.searchCondition.sortTarget.name() eq 'CITY'  ? "selected" : ""}>
                            도시
                        </option>
                        <option value="WAREHOUSE_NAME" ${inventoryPageHandler.searchCondition.sortTarget.name() eq 'WAREHOUSE_NAME'  ? "selected" : ""}>
                            창고
                        </option>


                    </select>
                </label>

                <label for="search-keyword">
                    <input type="text" name="keyword" id="search-keyword"
                           value="${inventoryPageHandler.searchCondition.keyword}"
                           placeholder="키워드를 입력해주세요">
                </label>


                <label for="search-direction">
                    <select id="search-direction" name="sortDirection">
                        <option value="ASC" ${inventoryPageHandler.searchCondition.sortDirection.name() eq 'ASC' or
                                inventoryPageHandler.searchCondition.sortDirection eq null ? "selected" : ""}>
                            오름차순
                        </option>
                        <option value="DESC" ${inventoryPageHandler.searchCondition.sortDirection.name() eq 'DESC'
                                ? "selected" : ""}>
                            내림차순
                        </option>
                    </select>
                </label>
                <input type="submit" value="검색" class="btn">
            </form>

        </div>
        <table>
            <tr>
                <th>나라</th>
                <th>창고</th>
                <th>주소</th>
                <th>상품</th>
                <th>수량</th>
                <th>유닛가격</th>
            </tr>

            <c:forEach var="inventoryDetail" items="${inventoryDetails}">
                <tr>
                    <td>${inventoryDetail.countryName}<br>(${inventoryDetail.regionName})</td>
                    <td>${inventoryDetail.warehouseName}<br>(${inventoryDetail.city})</td>
                    <td>${inventoryDetail.address}</td>
                    <td>${inventoryDetail.productName}</td>
                    <td>${inventoryDetail.quantity}</td>
                    <td>${inventoryDetail.listPrice}</td>
                </tr>
            </c:forEach>
        </table>
        <footer>

            <div class="paging-container">
                <div class="paging">

                    <c:if test="${inventoryPageHandler.showPrev}">
                        <a href="<c:url value=
                        "/employee/${employeeId}/inventory${inventoryPageHandler.getQueryString(inventoryPageHandler.beginPage-1)}"/>">
                            &lt;
                        </a>
                    </c:if>

                    <c:forEach var="page" begin="${inventoryPageHandler.beginPage}"
                               end="${inventoryPageHandler.endPage}">
                        <a href="<c:url value=
                        "/employee/${employeeId}/inventory${inventoryPageHandler.getQueryString(page)}"/>">${page}</a>
                    </c:forEach>

                    <c:if test="${inventoryPageHandler.showNext}">
                        <a href="<c:url value=
                        "/employee/${employeeId}/inventory${inventoryPageHandler.getQueryString(inventoryPageHandler.endPage+1)}"/> ">
                            &gt;
                        </a>
                    </c:if>
                </div>
            </div>
        </footer>

        </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
