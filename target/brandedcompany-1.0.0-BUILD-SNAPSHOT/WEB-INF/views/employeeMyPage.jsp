<%@ page import="com.brandedCompany.util.ImageUtils" %>
<%@ page import="com.brandedCompany.util.DomainUtils" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><spring:message code="employeeMyPage.title"/></title>
    <script src="https://code.jquery.com/jquery-1.11.3.js"></script>

</head>
<link rel="stylesheet" href="<c:url value='/resources/css/employeeNavMenu.css'/>" type="text/css">
<link rel="stylesheet" href="<c:url value='/resources/css/employeeMyPage.css'/>" type="text/css">
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
    <jsp:include page="employeeNavMenu.jsp"/>
    <div class="myPage-container">
        <h2 class="writing-header"><spring:message code="employeeMyPage.title"/></h2>
        <div class="profile-box">
            <div class="profile-img"><img src="data:image/jpeg;base64,${employee.imageBase64}"/></div>
            <div class="profile-update-img-box">
                <form class="frm" action='<c:url value="/employee/${employee.employeeId}/myPage/uploadImg"/>' method="post"
                      enctype="multipart/form-data">
                    <input type="file" name="uploadFile"  onchange="uploadStatus()"
                           accept="image/png, image/jpg, image/PNG, image/JPG, image/jpeg, image/JPEG">
                    <button class="btnUpdateLoadImg btn" type="submit" disabled>저장</button>
                </form>
                <form class="removeFrm frm" action='<c:url value="/employee/${employee.employeeId}/myPage/removeImg"/>' method="post">
                <button class="btnRemoveImg btn" type="submit" ${employee.imageBase64 eq noImg ? 'disabled' : ''}>삭제</button>
                </form>
            </div>
            <div class="profile-contents">
                <div class="profile-info">
                    <div id="id">
                        <h4>번호<br/>${employee.employeeId}</h4>
                    </div>
                    <div id="name">
                        <h4>이름<br/>${employee.firstName} ${employee.lastName}</h4>
                    </div>
                </div>
                <div class="profile-info2">
                    <div id="hireDate">
                        <h4>입사일<br/>${employee.hireDate}</h4>
                    </div>
                    <div id="jobTitle">
                        <h4>포지션<br/>${employee.jobTitle}</h4>
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
