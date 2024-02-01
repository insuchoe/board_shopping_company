<%@ page import="com.brandedCompany.util.EmployeeControllerUtils" %>
<%@ page import="com.brandedCompany.serivce.PagingAndSortingService" %>
<%@ page import="com.brandedCompany.serivce.PagingAndSortingServiceImpl" %>
<%@ page import="org.springframework.web.context.ContextLoader" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="com.brandedCompany.domain.Employee" %>
<%@ page import="java.util.function.IntToLongFunction" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="code" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="employeeId" value='${employee.employeeId}'/>
<c:set var="name" value='${employee.fullName}'/>
<c:set var="firstName" value='${employee.firstName}'/>
<c:set var="lastName" value='${employee.lastName}'/>
<c:set var="phone" value='${employee.phone}'/>
<c:set var="email" value="${employee.email}"/>
<c:set var="jobTitle" value="${employee.jobTitle}"/>
<c:set var="hireDate" value="${employee.hireDate}"/>
<c:set var="cPage" value="commentPageHandler.PAGE"/>
<c:set var="isExistCPage" value="cPage neq null"/>
<c:set var="cTotalCount" value="commentPageHandler.TOTAL_COUNT"/>
<c:set var="isExistCTotalcount" value="cTotalCount neq null"/>


<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><spring:message code="employee.board.title"/></title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="<c:url value='/resources/css/employeeNavMenu.css'/>" type="text/css">
    <link rel="stylesheet" href="<c:url value='/resources/css/employeeBoard.css'/>" type="text/css">
    <script src="https://code.jquery.com/jquery-1.11.3.js"></script>
</head>
<body>

<%--게시글, 댓글 컨테이너--%>
<div class="container">
<%-- 게시글 폼--%>
    <form id="form" class="frm">
        <%-- 쓰기 /수정 /읽기--%>
        <h2 class="writing-header">게시물 ${mode eq "new" ?  "쓰기" : "읽기" }</h2>
        <input type="hidden" name="boardId" value="${mode eq "new" ? "" : board.boardId}">
        <input type="hidden" name="employeeId" value="${mode eq "new" ? id : board.employeeId}">
        <label>
            <input name="publisher" type="${mode eq "new"  ? "hidden" : "text"}"
                   value="${mode eq "new" ? name : board.publisher}" readonly=readonly>
        </label>
        <label>
            <input name="publishedDate" readonly="readonly" type=
            ${mode eq "new" ? " hidden" : "datetime-local" }
                    value="${board.publishedDate}">
        </label>
        <label>
            <input type="text" name="title" value="${ mode eq "new" ? "" : board.title }"
                   placeholder="제목을 입력해주세요." ${mode eq "new"  ? "" : "readonly='readonly' " }>
        </label>
        <label>

        <textarea style="padding: 5px" name="content" rows="20" placeholder="내용을 입력해주세요."
        ${mode eq "new"  ? "" : "readonly='readonly' " }>${mode eq "new" ? "" : board.content }</textarea>
        </label>

        <c:choose>
            <c:when test="${mode eq 'new' }">
                <button type="button" id="writeBtn" class="btn btn-write"><i class="fa fa-pencil"></i> 등록</button>
            </c:when>
            <c:otherwise>
                <c:choose>
                    <c:when test="${employeeId eq board.employeeId}">
                        <%--                <button type="button" id="mdfNewBtn" class="btn btn-modify"><i class="fa fa-edit"></i>수정</button>--%>
                        <button type="button" id="mdfBtn" class="btn btn-modify"><i
                                class="fa fa-edit"></i>수정
                        </button>
                        <button type="button" id="removeBtn" class="btn btn-remove"><i class="fa fa-trash"></i> 삭제
                        </button>
                    </c:when>
                </c:choose>
            </c:otherwise>
        </c:choose>

        <button type="button" id="boardsBtn" class="btn btn-list"><i class="fa fa-bars">
            <a href='<c:url value=
            "/employee/${employeeId}/board${boardPageHandler.queryString}"/>'><spring:message code="employee.board.title"/></a></i></button>
    </form>
    <br/><br/>

    <%--댓글 컨테이너--%>
    <div id="comment-container">
        <%-- 댓글 --%>
        <%--작성 된 댓글이 있으면--%>
        <c:if test="${not empty items}">
            <ul style="background-color: #f9f9fa;">
                    <%-- 댓글 작성일(시간), 댓글 내용, 댓글 작성자 컨테이너--%>
                <c:forEach var="comment" items="${items}">
                    <%--댓글 데이터 컨테이너--%>
                    <%--  <c:choose>
                          <c:when test="${comment.commentId ne comment.parentCommentId}">
                              <li class="comment-item" data-comment-id="${comment.commentId}"
                                  data-board-id="${comment.boardId}" parent-comment-id="${comment.parentCommentId}">
                              </li>
                          </c:when>
                          <c:otherwise>--%>
                    <li class="comment-item" data-comment-id="${comment.commentId}"
                        data-board-id="${comment.boardId}" data-parent-comment-id="${comment.parentCommentId}"
                        style="${comment.commentId ne comment.parentCommentId ? "margin-left: 50px": ''} ">
                        <div class="comment-item-wrapper">
                                <%--댓글 작성자 이미지--%>
                            <div class="comment-img">
                                <img src="data:image/jpeg;base64,${comment.imageBase64}" alt="profile">
                            </div>
                            <div class="comment-area" id="comment-area">
                                    <%--댓글 작성자 렌더링--%>
                                <div class="publisher">${comment.publisher}</div>

                                    <%--댓글 내용 렌더링--%>
                                <textarea name="comment-content" <%--class="comment-content"--%>
                                          style="padding: 0" class="comment-content"
                                          readonly="readonly"
                                          data-comment-id="${comment.commentId}"
                                          data-board-id="${comment.boardId}"
                                          data-parent-comment-id="${comment.parentCommentId}">${comment.content}</textarea>

                                    <%--답글 적기, 수정, 삭제 컨테이너--%>
                                <div class="comment-bottom">
                                        <%--댓글 등록 일자 렌더링--%>
                                    <span class="comment-modify-date">
                        <c:choose>
                            <%-- 오늘 댓글을 작성 했다면  --%>
                            <c:when test="${comment.registrationDate.toLocalDate() eq today.toLocalDate()}">
                                <c:set var="today" value="${today}"/>
                                <c:set var="publishedDate"
                                       value="${comment.registrationDate.toLocalDate()}"/>
                                <c:set var="hour" value="${comment.registrationDate.toLocalTime().hour}"/>
                                <c:set var="minute"
                                       value="${comment.registrationDate.toLocalTime().minute}"/>
                                ${hour < 12  ? '오전': '오후'}
                                ${hour%13 < 10 ? '0': ''}${hour%13}:${minute<10?'0':''}${minute}
                            </c:when>
                            <c:otherwise>
                                <%--작성했던 날짜만 렌더링--%>
                                ${comment.registrationDate.toLocalDate()}
                            </c:otherwise>
                        </c:choose>
                      </span>

                                        <%--답글 적기--%>
                                    <button class="rply-btn btn-write btn"
                                            data-comment-id="${comment.commentId}"
                                            data-board-id="${comment.boardId}"
                                            data-parent-comment-id="${comment.parentCommentId}">답글
                                    </button>
                                        <%--작성한 댓글이 본인(로그인한) 이라면 수정, 삭제 기능 보이도록--%>
                                    <c:choose>
                                        <c:when test="${employeeId eq comment.employeeId}">

                                            <button class="cmm-rpy-edit-btn btn"
                                                    data-comment-id="${comment.commentId}"
                                                    data-board-id="${comment.boardId}"
                                                    data-parent-comment-id="${comment.parentCommentId}">수정
                                            </button>

                                            <button class="cmm-rpy-rmv-btn btn" data-comment-id="${comment.commentId}"
                                                    data-board-id="${comment.boardId}"
                                                    data-parent-comment-id="${comment.parentCommentId}">삭제
                                            </button>

                                        </c:when>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </li>

                </c:forEach>

                <div id="reply-writebox" style="display: none;">
                    <div class="publisher publisher-writebox">${name}</div>
                    <div class="rply-box">
                                <textarea id='reply-content' name="rply-cntnt" id="" cols="30" rows="3"
                                          placeholder="댓글을 남겨보세요"></textarea>
                    </div>
                    <div id="reply-writebox-bottom">
                        <div class="register-box">
                            <button class="btn" id="wrt-rply-btn">등록</button>
                            <button class="btn" id="cnc-rply-btn">취소</button>
                        </div>
                    </div>
                </div>
            </ul>
        </c:if>

        <%--댓글 테이블 태그 끝--%>
        <%--<h4>댓글 begin ${commentPageHandler.beginPage}</h4>
        <h4>댓글 end ${commentPageHandler.endPage}</h4>
        <h4>new eq mode "${'new' eq mode}"</h4>--%>

        <%--댓글 페이지 핸들링--%>
        <br/>


        <c:if test="${'new' ne mode}">
            <div class="paging-container">
                <div class="paging">

                    <c:if test="${commentPageHandler.showPrev}">
                        <a class="page"
                           href='<c:url value=
                           "/employee/${employeeId}/board${boardId}${boardPageHandler.queryString}commentPage=${commentPageHandler.beginPage-1}"/>'><</a>
                    </c:if>
                    <c:if test="${not empty items}">
                        <c:forEach var="page" begin="${commentPageHandler.beginPage}"
                                   end="${commentPageHandler.endPage}">
                            <a class="page" href='<c:url value=
                            "/employee/${employeeId}/board/${boardId}${boardPageHandler.queryString}${commentPageHandler.getQueryString2(page)}"/>'>${page}</a>
                        </c:forEach>
                    </c:if>
                    <c:if test="${commentPageHandler.showNext}">
                        <a class="page" href='<c:url value=
                        "/employee/${employeeId}/board${boardId}${searchCondition.queryString}commentPage=${commentPageHandler.endPage+1}"/>'>&gt;</a>
                    </c:if>

                </div>
            </div>
            <br/>
            <input type="hidden" name="parentCommentId">
            <div class="comment publisher-writebox">
                <div class="publisher publisher-writebox">${name}</div>
                <div class="comment-writebox-content">
                    <label><textarea name="add-cmt-texts" rows="3" cols="30" id="cmt-txt"
                                     placeholder="댓글을 입력해주세요."></textarea></label>
                </div>
                <div class="comment-writebox-bottom">
                    <button type="button" id="add-cmt-btn" class="btn btn-write"><i class="fa">등록</i></button>
                </div>
            </div>
        </c:if>
    </div>
</div>


<%--게시글 CRUD--%>
<script>
    $(document).ready(function () {
        // 제목 or 내용이 적히지 않았다면 게시글 작성 실패
        let formCheck = function () {
            let title = $("input[name=title]").val();
            let content = $("input[name=content]").val();

            if (title === "") {
                alert("제목을 입력해 주세요.");
                title.focus();
                return false;
            }
            if (content === "") {
                alert("내용을 입력해 주세요.");
                content.focus();
                return false;
            }
            return true;
        }
        <%--게시글 삭제--%>
        $(document).ready(function () {
            $("#removeBtn").click(function () {
                let $boardId = $("input[name=boardId]").val();
                $.ajax({
                    type: 'delete',
                    url: '/brandedCompany/employee/${employeeId}/board/' + $boardId,
                    headers: {'content-type': 'application/json'},
                    success: function () {
                        location.href = '/brandedCompany/employee/${employeeId}/board${boardPageHandler.queryString}'
                    },//success
                    error:
                        function () {
                            alert("게시글 삭제 실패했습니다.");
                        } // error
                });
            });
        });
        <%-- 게시글 쓰기--%>

        $("#writeBtn").click(function () {
            if (!formCheck()) return; // 제목/내용 텍스트 비었는지
            $.ajax({
                type: 'post',
                url: "/brandedCompany/employee/${employeeId}/board${boardPageHandler.queryString}",
                headers: {'content-type': 'application/json'},
                data: JSON.stringify({employeeId:${employeeId},
                        publisher: "${name}",
                        title: $("input[name=title]").val(),
                        content: $("textarea[name=content]").val()
                }),
                success:
                    function (boardId) {
                        location.href = "/brandedCompany/employee/${employeeId}/board/" + boardId + "?page=1";
                    },
                error: function () {
                    alert("다시 입력해주세요 !.");
                }
            }); // $.ajax()
        });


        <%-- 게시글 수정 --%>

        $("#mdfBtn").on("click", function () {
            let $boardId = $("input[name=boardId]");
            let $title = $("input[name=title]");
            let $content = $("textarea[name=content]");
            let isTitleReadonly = $title.attr('readonly');
            let isContentReadonly = $content.attr('readonly');

            if (isTitleReadonly === 'readonly' && isContentReadonly === 'readonly') {
                $(".writing-header").html("게시물 수정");
                $title.attr("readonly", false);
                $content.attr("readonly", false);

            } else {
                $.ajax({
                    type: 'patch',
                    url: "/brandedCompany/employee/${employeeId}/board/" + $boardId.val() + "${boardPageHandler.queryString}",
                    headers: {'content-type': 'application/json'},
                    data: JSON.stringify({
                        employeeId:${employeeId},
                        boardId: $boardId.val(),
                        title: $title.val(),
                        content: $content.val(),
                    }),
                    success: function (ids) {

                        location.href = '/brandedCompany/employee/'+ids[0]+'/board/' + ids[1] +'${boardPageHandler.queryString}';
                    },
                    error: function () {
                        alert("게시물 수정에 실패했습니다.");
                        // location.href = '/employee/boards' + body.getQueryString();
                    }
                }); // ajax}
            }
        }); // mdf Btn click
    }); // document.ready.function....
</script>
<script>
    <%--댓글 CRUD--%>

    $(document).ready(function () {
        <%--  게시글 --%>
        const $boardId = $("input[name=boardId]").val();
        let boardPage =${boardPageHandler.PAGE};
        let boardTotalCount =${boardPageHandler.TOTAL_COUNT};
        let keyword = "${boardPageHandler.keyword}";

        // node 의 readonly attribute 핸들링
        function readOnly(node, boolean) {

            // ex) readOnly ($textarea1,false)  -> textarea 수정 O
            // ex) readOnly ($textarea1,true)   -> textarea 수정 X

            if (typeof boolean !== 'boolean') {
                alert('ture/false 로 입력해주세요.');
                return;
            }
            if (!boolean) node.removeAttribute('readonly');
            else if (boolean) node.setAttribute('readonly', 'readonly');

        }

        // 내용이 비었는지
        function formCheck(form) {

            if (form.trim() === '') {
                alert('내용을 입력해주세요');
                return false;
            }
            return true;
        }

        function strLength(str) {
            if (str.trim().length > 1000) {
                alert('1000자 이내로 입력해주세요');
                return false;
            }
            return true;
        }

        // 게시글 검색 조건 JSON 배열로
        function convertBrdSrhCndArr(keywordValue, optionValue) {
            return {
                keyword: keywordValue,
                option: optionValue
            }
        }

        // 댓글 JSON 배열로
        function convertCmmArr(boardId, employeeId, parentCommentId, publisher, content) {
            return {
                boardId: boardId,
                employeeId: employeeId,
                publisher: publisher,
                parentCommentId: parentCommentId,
                content: content
            }
        }

        // 댓글 JSON 배열로
        function convertCmmArr3(boardId, employeeId, publisher, content) {
            return {
                boardId: boardId,
                employeeId: employeeId,
                content: content,
                publisher: publisher
            }
        }

        function convertCmmArr2(boardId, employeeId, commentId, parentCommentId, publisher, content) {
            return {
                boardId: boardId,
                commentId: commentId,
                employeeId: employeeId,
                publisher: publisher,
                parentCommentId: parentCommentId,
                content: content
            };
        }

        // 댓글 검색 조건 JSON 배열로
        function convertCmmSrhArr(brdId, empId) {
            return {
                boardId: brdId,
                employeeId: empId,
            };
        }

        // 댓글 달기
        $("#add-cmt-btn").click(function () {
            <%-- 댓글 --%>
            let $commentVal = $("#cmt-txt").val();

            // 댓글 내용 비었는지
            if (!formCheck($commentVal)) return;
            if (!strLength($commentVal)) return;
            $.ajax({
                type: 'post',
                url: '/brandedCompany/employee/${employeeId}/board/' + $boardId +
                    "/comment${boardPageHandler.queryString}${commentPageHandler.queryString2}",
                headers: {'content-type': 'application/json'},
                data: JSON.stringify(convertCmmArr3($boardId, ${employeeId}, '${name}', $commentVal)),
                success: function () {
                    location.href = '/brandedCompany/employee/${employeeId}/board/' + $boardId +
                        "${boardPageHandler.queryString}${commentPageHandler.queryString2}";
                }, error: function () {
                    alert("댓글을 등록 못 했습니다.");
                }
            });
        });

        // 댓글수정/답글수정
        $(".cmm-rpy-edit-btn").on("click", function (e) {
            /*let target = e.target;// 클릭된 버튼
            let li = target.parentNode.parentNode.parentNode.parentNode;//부모 li 노드
            let $commentContent = [...target.parentNode.parentNode.parentNode.childNodes].find(
                node => node.tagName === 'TEXTAREA'); // 댓글창

            if ($commentContent.readOnly) {
                readOnly($commentContent, false);
                // 댓글 창에 텍스트 바로 입력받도록
                $commentContent.focus();
                return;

          let $dataBoardId = li.getAttribute('data-board-id');//게시물 번호
            let $dataCommentId = li.getAttribute('data-comment-id');//댓글 번호
            let $dataParentCommentId = li.getAttribute('parent-comment-id');//부모 댓글 번호
            }*/
            let $target = e.target;

            let $dataBoardId;
            let $dataCommentId;
            let $dataParentCommentId;
            let $commentContent;

            let $contents = $('.comment-content');
            let index = 0;

            for (let i = 0; i < $contents.length; i++) {
                let dataset = $contents[i].dataset;
                let dataset2 = $target.dataset;

                if (dataset2.commentId === dataset.commentId &&
                    dataset2.boardId === dataset.boardId &&
                    dataset2.parentCommentId === dataset.parentCommentId) {

                    $dataBoardId = dataset2.boardId;
                    $dataCommentId = dataset2.commentId;
                    $dataParentCommentId = dataset2.parentCommentId;
                    $commentContent = $contents[i].textContent;
                    index = i;

                    //수정할 textArea readOnly 해제
                    if ($contents[i].readOnly) {
                        $contents[i].readOnly = false;
                        // 댓글 창에 텍스트 바로 입력받도록
                        $contents[index].focus();
                        return;
                    }
                }
            }
            console.log($contents[index].value);

            // 댓글이 빈 칸이라면
            if ($contents[index].value.trim() === '') {
                alert("수정 될 내용이 필요합니다.");
                return false;
            }

            $.ajax({
                type: 'patch',
                url: '/brandedCompany/employee/${employeeId}/board/' + $boardId + '/parentComment/' + $dataParentCommentId + "/comment/" + $dataCommentId + "${boardPageHandler.queryString}${commentPageHandler.queryString2}",
                headers: {'content-type': 'application/json'},
                data: JSON.stringify(convertCmmArr2($dataBoardId, ${employeeId}, $dataCommentId, $dataParentCommentId, '${name}', $contents[index].value)),
                success: function () {
                    $contents[index].readOnly = true;
                    location.href = '/brandedCompany/employee/${employeeId}/board/' + $boardId + '${boardPageHandler.queryString}${commentPageHandler.queryString2}';
                },
                error: function () {
                    alert('다시 시도해주세요.');
                }
            });

        });


        // 댓글 / 답글 (공통) 삭제
        $(".cmm-rpy-rmv-btn").on("click", function (e) {
            if (!confirm("정말로 삭제하시겠습니까?")) return;
            let $target = e.target;// 클릭된 버튼

            let $dataBoardId;
            let $dataCommentId;
            let $dataParentCommentId;

            let commentItems = document.getElementsByClassName('comment-item');
            [...commentItems].forEach(e => {
                let dataset = e.dataset;
                let dataset2 = $target.dataset;

                if (dataset2.commentId === dataset.commentId &&
                    dataset2.boardId === dataset.boardId &&
                    dataset2.parentCommentId === dataset.parentCommentId) {
                    $dataBoardId = dataset2.boardId;
                    $dataCommentId = dataset2.commentId;
                    $dataParentCommentId = dataset2.parentCommentId;
                }
            });

            console.log($dataBoardId + ", " + $dataCommentId + " ," + $dataParentCommentId);

            $.ajax({
                type: 'delete',
                url: '/brandedCompany/employee/${employeeId}/board/' + $dataBoardId + '/parentComment/' + $dataParentCommentId + "/comment/" + $dataCommentId + "${boardPageHandler.queryString}${commentPageHandler.queryString2}",
                headers: {'content-type': 'application/json'},
                success: function () {
                    location.href = '/brandedCompany/employee/${employeeId}/board/' + $dataBoardId + "${boardPageHandler.queryString}${commentPageHandler.queryString2}";
                },
                error: function () {
                    alert('다시 시도해주세요.');
                }

            });

        });
        // 답글 보이기 버튼
        $(".rply-btn").on("click", function (e) {

            let $target = e.target;
            let $boardId = $target.getAttribute("data-board-id");
            let $commentId = $target.getAttribute("data-comment-id");
            let $parentCommentId = $target.getAttribute("data-parent-comment-id");
            let $replyForm = $("#reply-writebox");

            if ($replyForm.css('display') === 'none') $replyForm.css('display', 'block');
            else $replyForm.css('display', 'none');

            $replyForm.textContent='';

            $replyForm.appendTo($("li[data-comment-id=" + $commentId + "]"));
            $replyForm.attr("data-board-id", $boardId);
            $replyForm.attr("data-comment-id", $commentId);
            $replyForm.attr("data-parent-comment-id", $parentCommentId);

        });

        //답글 달기
        $("#wrt-rply-btn").on("click", function (e) {
            let $dataBoardId = document.getElementById('reply-writebox').getAttribute("data-board-id");
            // let $commentId = document.getElementById('reply-writebox').getAttribute("data-comment-id");
            let $dataParentCommentId = document.getElementById('reply-writebox').getAttribute("data-parent-comment-id");
            let $replyContent = $("textarea[name=rply-cntnt]").val();

            formCheck($replyContent);

            if ($replyContent.length > 1000) {
                alert('최대 1000자까지 입니다.');
                return;
            }
            $.ajax({
                type: 'post',
                url: '/brandedCompany/employee/${employeeId}/board/' + $dataBoardId + '/parentComment/' + $dataParentCommentId + "/comment${boardPageHandler.queryString}${commentPageHandler.queryString2}",
                headers: {'content-type': 'application/json'},
                data: JSON.stringify(convertCmmArr($dataBoardId, ${employeeId}, $dataParentCommentId, '${name}', $replyContent)),
                success: function () {
                    // 내용 수정 창 readonly 활성화
                    // readOnly($replyContent, true);
                    location.href = '/brandedCompany/employee/${employeeId}/board/' + $boardId + '${boardPageHandler.queryString}${commentPageHandler.queryString2}';
                },
                error: function () {
                    alert('다시 시도해주세요.');
                }
            });
        });

        // 답글 닫기
        $("#cnc-rply-btn").on("click", function () {
            $("textarea[name=rply-cntnt]").val('');
            $("#reply-writebox").hide();
        });
    });
</script>
</body>

</html>