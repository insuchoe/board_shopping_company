<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><spring:message code="customerMyPage.title"/></title>
    <script src="https://code.jquery.com/jquery-1.11.3.js"></script>

</head>
<link rel="stylesheet" href="<c:url value='/resources/css/customerNavMenu.css'/>" type="text/css">
<link rel="stylesheet" href="<c:url value='/resources/css/customerMyPage.css'/>" type="text/css">
<body>
<script>
    const allExtension=["jpg","JPG","JPEG","jpeg","PNG","png"];

    function uploadStatus() {
        let originalFilename = $('input[type=file]').val();
        let from='';

        let extensionInvalided = allExtension.some(extension=>{
            let commExtension = '.'+extension;
            from= originalFilename.lastIndexOf(commExtension);
            return  commExtension === originalFilename.substr(from,originalFilename.length);
        });
        if (!extensionInvalided)
            alert('<spring:message code="employee.image.invalidExtension"/>');
        else if (''===originalFilename)
            alert('이미지 파일을 등록해주세요.');
        else
            $('.btnUpdateLoadImg').prop('disabled', false);
    }
</script>

<div class="container">
    <jsp:include page="customerNavMenu.jsp"/>
    <div class="myPage-container">
        <h2 class="writing-header"><spring:message code="customerMyPage.title"/></h2>
        <div class="profile-box">
            <div class="profile-img"><img src="data:image/jpeg;base64,${customer.imageBase64}" alt="profile"></div>
            <div class="profile-update-img-box">
                <form class="frm" action='<c:url value="/customer/${customer.customerId}/myPage/uploadImg"/>' method="post"
                      enctype="multipart/form-data">
                    <input type="file" name="uploadFile" multiple onchange="uploadStatus()">
                    <button class="btnUpdateLoadImg btn" disabled > 저장</button>
                </form>
                <form class="removeFrm frm" action='<c:url value="/customer/${customer.customerId}/myPage/removeImg"/>' method="post">
                    <button class="btnRemoveImg btn" type="submit" ${customer.imageBase64 eq noImg ? 'disabled' : ''}>삭제</button>
                </form>
            </div>
            <div class="profile-contents">
                <div class="profile-info">
                    <div id="id">
                        <h4>번호<br/>${customer.customerId}</h4>
                    </div>
                    <div id="name">
                        <h4>이름<br/>${customer.name}</h4>
                    </div>
                </div>
                <div class="profile-info2">
                    <div id="hireDate">
                        <h4>주소<br/>${customer.address}</h4>
                    </div>
                    <div id="jobTitle">
                        <h4>신용한도<br/>${customer.creditLimit}</h4>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    <c:if test="${not empty msg}">
    alert('<spring:message code="${msg}"/>');
    </c:if>
</script>
</body>

</html>