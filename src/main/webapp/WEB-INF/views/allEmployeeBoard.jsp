<%@ page contentType="text/html;charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="code" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="employeeId" value="${employee.employeeId}"/>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><spring:message code="employee.board.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/employeeNavMenu.css'/>" type="text/css">
    <link rel="stylesheet" href="<c:url value='/resources/css/allEmployeeBoard.css'/>" type="text/css">
    <script src="https://code.jquery.com/jquery-1.11.3.js"></script>

</head>
<body>
<div class="container">

    <jsp:include page="employeeNavMenu.jsp"/>
    <div id="board-container">
        <h2 class="writing-header"><spring:message code="employee.board.title"/></h2>

        <div class="search-container">
            <form action='<c:url value="/employee/${employee.employeeId}/board"/>' method="get">
                <select id="search-option" name="option">

                    <option value="TITLE_CONTENT" ${boardPageHandler.searchCondition.option eq 'TITLE_CONTENT' or boardPageHandler.searchCondition.option eq null ? "selected" : ""}>
                        제목+내용
                    </option>
                    <option value="TITLE" ${boardPageHandler.searchCondition.option eq 'TITLE'  ? "selected" : ""}>제목
                    </option>
                    <option value="CONTENT" ${boardPageHandler.searchCondition.option eq 'CONTENT'? "selected" : ""}>내용
                    </option>
                    <option value="PUBLISHER" ${boardPageHandler.searchCondition.option eq 'PUBLISHER'? "selected" : ""}>
                        작성자
                    </option>
                    <option value="PUBLISHER_CONTENT" ${boardPageHandler.searchCondition.option eq 'PUBLISHER_CONTENT'? "selected" : ""}>
                        작성자+내용
                    </option>
                </select>

                <label for="search-keyword">
                    <input type="text" id="search-keyword" name="keyword"
                           value="${boardPageHandler.searchCondition.keyword}"
                           placeholder="검색어를 입력하세요">
                </label>
                <input type="submit" value="검색" class="btn">
                <label>
                    <input name="page" type="hidden" value="${boardPageHandler.searchCondition.PAGE}"/>
                </label>

            </form>


        </div>

        <table>
        <c:forEach var="board" items="${items}" varStatus="status">

                <c:if test="${0 eq status.index}">

                    <tr>
                        <th>번호</th>
                        <th>제목</th>
                        <th>작성자</th>
                        <th>등록일</th>
                        <th>조회수</th>
                    </tr>
                </c:if>

                <tr>
                    <td>${board.boardId}</td>

                    <td>
                        <c:choose>
                            <c:when test="${boardIdsWithAnyComment.contains(board.boardId)}">
                                <a href='<c:url value="/employee/${employeeId}/board/${board.boardId}${boardPageHandler.queryString}&commentPage=1"/>'>${board.title}</a>
                            </c:when>
                            <c:otherwise>
                                <a href='<c:url value="/employee/${employeeId}/board/${board.boardId}${boardPageHandler.queryString}"/>'>${board.title}</a>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>${board.publisher}</td>
                    <td>
                        <c:choose>
                            <c:when test="${board.publishedDate.toLocalDate() eq today.toLocalDate()}">
                                <c:set var="hour" value="${board.publishedDate.toLocalTime().hour}"/>
                                <c:set var="minute" value="${board.publishedDate.toLocalTime().minute}"/>
                                ${hour < 12  ? '오전': '오후'}
                                ${hour%12 < 10 ? '0': ''}${hour%12}:${minute<10?'0':''}${minute}
                            </c:when>
                            <c:otherwise>
                                ${board.publishedDate.toLocalDate()}
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>${board.views}</td>
                </tr>

        </c:forEach>
        </table>

        <footer>

            <div class="paging-container">
                <div class="paging">
                    <c:choose>
                        <c:when test="${null eq boardPageHandler.TOTAL_COUNT or 0 eq boardPageHandler.TOTAL_COUNT}">
                            <div>게시글이 없습니다.</div>
                        </c:when>
                        <c:otherwise>
                            <c:if test="${boardPageHandler.showPrev}">
                                <a href="<c:url value="/employee/${employee.employeeId}/board${boardPageHandler.getQueryString(boardPageHandler.beginPage-1)}"/>">
                                    &lt;
                                </a>
                            </c:if>

                            <c:forEach var="page" begin="${boardPageHandler.beginPage}"
                                       end="${boardPageHandler.endPage}">
                                <a href="<c:url value="/employee/${employee.employeeId}/board${boardPageHandler.getQueryString(page)}"/>">${page}</a>
                            </c:forEach>

                            <c:if test="${boardPageHandler.showNext}">
                                <a href="<c:url value="/employee/${employee.employeeId}/board${boardPageHandler.getQueryString(boardPageHandler.endPage+1)}"/> ">
                                    &gt;
                                </a>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <button id="writeBtn" class="btn" name="writeBtn"><a
                    href='<c:url value="/employee/${employeeId}/board/new${boardPageHandler.queryString}"/>'>게시글 쓰기</a>
            </button>
        </footer>

    </div>
</div>

<script>
    <%--$(document).ready(function () {--%>
    <%--    try {--%>
    <%--        $("#writeBtn").on("click", function () {--%>
    <%--            $.ajax({--%>
    <%--                type: 'get',--%>
    <%--                url: '/employee/board',--%>
    <%--                headers: {'content-type': 'application/json'},--%>
    <%--                data:--%>
    <%--                    {--%>
    <%--                        boardSearchCondition:"${boardPageHandler.searchCondition}"--%>

    <%--                    },--%>
    <%--                success:--%>
    <%--                    function () {--%>
    <%--                        console.log("ASD");--%>
    <%--                    },//success--%>
    <%--                error:--%>
    <%--                    function () {--%>
    <%--                        console.log("BCD");--%>

    <%--                    } // error--%>
    <%--            })--%>
    <%--        });--%>
    <%--    } catch (e) {--%>
    <%--        console.log(e);--%>
    <%--    }--%>
    <%--});--%>

</script>
</body>
</html>
