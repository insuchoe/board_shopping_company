package com.brandedCompany.controller.employee;

import com.brandedCompany.domain.Comment;
import com.brandedCompany.domain.handler.BoardPageHandler;
import com.brandedCompany.domain.handler.CommentPageHandler;
import com.brandedCompany.serivce.PagingAndSortingService;
import com.brandedCompany.util.EmployeeControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;

import static com.brandedCompany.util.DomainUtils.TABLE.COMMENTS;

@Controller
@RequestMapping("/employee/{employeeId}/board/{boardId}")
public class EmployeeCommentController
{
    private final Logger logger = LoggerFactory.getLogger(EmployeeCommentController.class);

    private final PagingAndSortingService service;
    private final EmployeeControllerUtils util;

    @Autowired
    public EmployeeCommentController(
        @Qualifier("pagingAndSortingServiceImpl")
            PagingAndSortingService service, EmployeeControllerUtils util)
    {
        this.service = service;
        this.util = util;
    }

    @PostMapping("/parentComment/{parentCommentId}/comment")
    public ResponseEntity<?> insert(
        @PathVariable("employeeId")
            BigInteger employeeId,
        @PathVariable("boardId")
            BigInteger boardId,
        @PathVariable("parentCommentId")
            BigInteger parentCommentId, HttpServletRequest request,
        @RequestBody
            Comment comment, Model model)
    {
        BigInteger[] ids = new BigInteger[]{employeeId, boardId, parentCommentId};
        String log = "[" + employeeId + "] " + boardId + "번 게시글에 " +
            parentCommentId + "번 댓글에 대댓글 추가";
        try
        {
            if (service.insert(comment))
            {
                BoardPageHandler boardPageHandler = util.getBoardPageHandler(request);
                model.addAttribute("boardPageHandler", boardPageHandler);
                CommentPageHandler commentPageHandler = util.getCommentPageHandler(boardId, request);
                model.addAttribute("commentPageHandler", commentPageHandler);
                logger.info(log);
                return new ResponseEntity<>(ids, HttpStatus.OK);
            }
        }
        catch (Exception e)
        {
            logger.error(log);
//            e.printStackTrace();
        }
        return new ResponseEntity<>(ids,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PatchMapping("/parentComment/{parentCommentId}/comment/{commentId}")
    public ResponseEntity<?> update(
        @PathVariable("employeeId")
            BigInteger employeeId,
        @PathVariable("boardId")
            BigInteger boardId,
        @PathVariable("parentCommentId")
            BigInteger parentCommentId,
        @PathVariable("commentId")
            BigInteger commentId, HttpServletRequest request,
        @RequestBody
            Comment comment, Model model)
    {
        BigInteger[] ids = new BigInteger[]{employeeId, boardId, commentId};
        String log = "[" + employeeId + "] " + boardId + "번 게시글에 "+
            (parentCommentId.equals(commentId)? parentCommentId+"번 댓글 수정" :
            parentCommentId+"번 댓글에 " + commentId + "번 대댓글 수정");
        try
        {
            if (service.update(comment))
            {
                BoardPageHandler boardPageHandler = util.getBoardPageHandler(request);
                model.addAttribute("boardPageHandler", boardPageHandler);
                CommentPageHandler commentPageHandler = util.getCommentPageHandler(boardId, request);
                model.addAttribute("commentPageHandler", commentPageHandler);
                logger.info(log);
                return new ResponseEntity<>(ids, HttpStatus.OK);
            }
        }
        catch (Exception e)
        {
            logger.error(log);
//            e.printStackTrace();
        }
        return new ResponseEntity<>(ids,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/comment")
    public ResponseEntity<BigInteger[]> insertComment(
        @PathVariable("employeeId")
            BigInteger employeeId,
        @PathVariable("boardId")
            BigInteger boardId, HttpServletRequest request,
        @RequestBody
            Comment comment, Model model)
    {
        String log = "[" + employeeId + "] " + boardId + "번 게시글에 댓글 추가";
        BigInteger[] ids=new BigInteger[]{employeeId,boardId};
        try
        {
            if (service.insert(comment))
            {
                BoardPageHandler boardPageHandler = util.getBoardPageHandler(request);
                model.addAttribute("boardPageHandler", boardPageHandler);
                CommentPageHandler commentPageHandler = util.getCommentPageHandler(boardId, request);
                model.addAttribute("commentPageHandler", commentPageHandler);
                logger.info(log);
                return new ResponseEntity<>(ids, HttpStatus.OK);
            }
        }
        catch (Exception e)
        {
//            e.printStackTrace();
            logger.error(log);
        }
        return new ResponseEntity<>(ids,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Transactional(rollbackFor = Exception.class)
    @DeleteMapping("/parentComment/{parentCommentId}/comment/{commentId}")
    public ResponseEntity<?> deleteComment(
        @PathVariable("employeeId")
            BigInteger employeeId,
        @PathVariable("boardId")
            BigInteger boardId,
        @PathVariable("parentCommentId")
            BigInteger parentCommentId,
        @PathVariable("commentId")
            BigInteger commentId, HttpServletRequest request, Model model)
    {
        String log = "[" + employeeId + "] " + boardId + "번 게시글에 " +
            (parentCommentId.equals(commentId) ?
            parentCommentId+ "번 댓글 삭제" :
            parentCommentId+"번 댓글에 "+commentId+"번 대댓글 삭제");
        BigInteger[] ids=new BigInteger[]{employeeId,boardId,parentCommentId,commentId};
        try
        {
            //  데이터베이스로 해당 댓글 삭제 요청
            if (service.delete(COMMENTS, commentId))
            {
                //게시판 페이지 핸들러
                BoardPageHandler boardPageHandler = util.getBoardPageHandler(request);
                model.addAttribute("boardPageHandler", boardPageHandler);
                //댓글 페이지 핸들러
                CommentPageHandler commentPageHandler = util.getCommentPageHandler(boardId, request);
                model.addAttribute("commentPageHandler", commentPageHandler);

                logger.info(log);
                return new ResponseEntity<>(ids,HttpStatus.OK);
            }
        }
        catch (Exception e)
        {
           // e.printStackTrace();
            logger.error(log);

        }
        return new ResponseEntity<>(ids,HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }

}
