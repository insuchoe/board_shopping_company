<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<html>
<head>
    <title>request parameter error</title>
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
    <h2>해당 요청은 올바르지 않습니다.</h2>
    <a href="<c:url value='/employee/${employee.employeeId}/myPage'/> ">
        마이 페이지
    </a>
    </div>
</div>

</body>
</html>
