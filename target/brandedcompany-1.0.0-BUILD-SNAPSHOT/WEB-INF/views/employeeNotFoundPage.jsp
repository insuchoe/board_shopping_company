<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<html>
<head>
    <title>404 page not found</title>
    <style>

        * {
            border: 0;
            padding: 0;
            margin: 0;
            font-family: "Malgun Gothic", sans-serif;
        }
a{
    text-decoration: none;
    color: black;
}
        .container{
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }
.subContainer{
    min-width: 25%;
    min-height: 25%;
    text-align: center;
}
.subContainer>*{
    margin-top: 5px;
    margin-bottom: 5px;
}
    </style>
</head>
<body>
<div class="container">
    <div class="subContainer">
    <h2>해당 페이지는 존재하지 않습니다.</h2>
        <c:choose>
            <c:when test="${not empty employee}">
                <a href="<c:url value='/employee/${employee.employeeId}/myPage'/> ">
                    마이 페이지
                </a>
            </c:when>
            <c:otherwise>
                <a href="<c:url value='/employee/loginOut/login'/> ">
                    로그인 페이지
                </a>
            </c:otherwise>
        </c:choose>
    </div>
</div>

</body>
</html>
