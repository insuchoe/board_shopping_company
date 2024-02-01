package com.brandedCompany.controller.employee;

import com.brandedCompany.domain.Board;
import com.brandedCompany.domain.Comment;
import com.brandedCompany.domain.handler.BoardPageHandler;
import com.brandedCompany.domain.handler.CommentPageHandler;
import com.brandedCompany.domain.searchCondition.BoardSearchCondition;
import com.brandedCompany.serivce.OptionService;
import com.brandedCompany.util.EmployeeControllerUtils;
import com.brandedCompany.util.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.brandedCompany.util.DomainUtils.TABLE.BOARDS;
import static com.brandedCompany.util.DomainUtils.TABLE.EMPLOYEES;

@Controller
@RequestMapping("/employee/{employeeId}/board")
public class EmployeeBoardController
{
    private static final Logger logger = LoggerFactory.getLogger(EmployeeBoardController.class);
    private final OptionService service;
    private final ImageUtils imageUtils;
    private final EmployeeControllerUtils conUtils;

    @Autowired
    public EmployeeBoardController(OptionService service, ImageUtils imageUtils, EmployeeControllerUtils conUtils)
    {
        this.service = service;
        this.imageUtils = imageUtils;
        this.conUtils = conUtils;
    }

    //게시글 수정
    @Transactional(rollbackFor = Exception.class)
    @PatchMapping("/{boardId}")
    public ResponseEntity<BigInteger[]> update(
        @PathVariable("employeeId")
            BigInteger employeeId,
        @PathVariable("boardId")
            BigInteger boardId,
        @RequestBody
            Board board, HttpServletRequest request, Model model) throws Exception
    {
        //        model.addAttribute("employeeId", employeeId);
        BigInteger[] ids = new BigInteger[]{employeeId, boardId};
        String log = "[" + employeeId + "]" + boardId + "번 게시글 수정";
        try
        {
            //            model.addAttribute("employeeId", employeeId);
            if (service.isExist(BOARDS, boardId) && boardId.equals(board.getBoardId()) && employeeId.equals(board.getEmployeeId()) && service.update(board))
            {
                BoardPageHandler boardPageHandler = conUtils.getBoardPageHandler(request);
                model.addAttribute("boardPageHandler", boardPageHandler);

                CommentPageHandler commentPageHandler = conUtils.getCommentPageHandler(boardId, request);
                model.addAttribute("commentPageHandler", commentPageHandler);

                logger.info(log);
                return new ResponseEntity<>(ids, HttpStatus.OK);
            }
        }
        catch (Exception e)
        {
            logger.error(log);
            model.addAttribute(service.select(BOARDS, boardId));
            //   e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 게시글 작성
    @GetMapping("/new")
    public String select(
        @PathVariable
            BigInteger employeeId, HttpServletRequest request, Model m)
    {

        final String log = "[" + employeeId + "] 게시글 작성 페이지";
        try
        {

            m.addAttribute("mode", "new");
            m.addAttribute("boardPageHandler", conUtils.getBoardPageHandler(request));
            logger.info(log);
            return "employeeBoard";
        }
        catch (Exception e)
        {
            m.addAttribute("mode", "");
            //     e.printStackTrace();
            logger.error(log);
        }
        return "redirect:/employee/" + employeeId + "/myPage";
    }

    //게시글 추가
    @PostMapping
    @ResponseBody
    public ResponseEntity<BigInteger> insert(
        @PathVariable
            BigInteger employeeId, HttpServletRequest request,
        @RequestBody
            Board board, Model m)
    {

        String log = "";
        //추가할 객체의 아이디(BOARDS 테이블 추가할 데이터의 식별번호)
        BigInteger nextId = null;
        try
        {
            nextId = service.selectNextBoardId();
            log = "[" + employeeId + "] " + nextId + " 번 게시글 추가";
            //            m.addAttribute("employee", (Employee)request.getSession().getAttribute("employee"));
            //            ObjectMapper objectMapper = new ObjectMapper();
            //추가 할 게시글
            //            Board board = objectMapper.treeToValue(objectNode.get("board"), Board.class);

            // 게시글 추가 성공
            if (service.insert(board))
            {
                //게시글 검색 조건
                BoardPageHandler boardPageHandler = conUtils.getBoardPageHandler(request);
                m.addAttribute("boarPageHandler", boardPageHandler);
                logger.info(log);
                //                System.out.println("boardPageHandler.getQueryString() = " + boardPageHandler.getQueryString());
                return new ResponseEntity<>(nextId, HttpStatus.OK);
            }
        }
        catch (Exception e)
        {
            m.addAttribute("mode", "new");
            logger.error(log);
            //      e.printStackTrace();
        }
        return new ResponseEntity<>(nextId, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/{boardId}")
    public String read(
        @PathVariable("employeeId")
            BigInteger employeeId,
        @PathVariable("boardId")
            BigInteger boardId, HttpServletRequest request, Model m) throws Exception
    {

        //        m.addAttribute("employeeId", employeeId);
        final String log = "[" + employeeId + "] " + boardId + "번 게시글";

        //        try
        //        {
        if (service.isExist(BOARDS, boardId))
        {

            //            게시글 페이징 핸들러
            BoardPageHandler boardPageHandler = conUtils.getBoardPageHandler(request);
            m.addAttribute("boardPageHandler", boardPageHandler);

            //          해당 게시글의 댓글 페이지 핸들러 등록
            CommentPageHandler commentPageHandler = conUtils.getCommentPageHandler(boardId, request);
            m.addAttribute("commentPageHandler", commentPageHandler);

            //            게시글 데이터
            Board board = (Board) service.select(BOARDS, boardId);
            m.addAttribute("board", board);

            //          댓글 데이터
            Collection<Comment> items = (Collection<Comment>) service.pageAndSort(commentPageHandler.getSearchCondition());
            Map<String, Object> modelAttrs = m.asMap();
            for (Comment item : items)
            {
                item.setImageBase64(imageUtils.getBase64ImageCode(EMPLOYEES, item.getEmployeeId()));
            }

            m.addAttribute("items", items);

            //          오늘일자
            m.addAttribute("today", LocalDateTime.now());

            logger.info(log);

            //          조회수 + 1
            Optional.ofNullable(board)
                    .ifPresent(board1 ->
                               {
                                   try
                                   {
                                       service.increaseViews(boardId);
                                   }
                                   catch (Exception e)
                                   {
                                       //                            e.printStackTrace();
                                   }
                               });
            return "employeeBoard";
        }
        BoardPageHandler boardPageHandler = conUtils.getBoardPageHandler(request);
        m.addAttribute("boardPageHandler", boardPageHandler);
        m.addAttribute("commentPageHandler", conUtils.getCommentPageHandler(boardId, request));

        return "redirect:/employee/" + employeeId + "/board" + boardPageHandler.getQueryString(); // 게시판 URL


        //        }
        //        catch (Exception e)
        //        {
        //            logger.error(log);
        //            e.printStackTrace();
        //        }
        //        return "/employee/" + employeeId + "/myPage";
    }


    @GetMapping
    public String goToBoardsPage(
        @PathVariable
            BigInteger employeeId, Model m, HttpServletRequest request) throws Exception
    {
        String log = "[" + employeeId + "] 게시판 페이지";

        //            m.addAttribute("employee", (Employee)request.getSession().getAttribute("employee"));

        BoardPageHandler boardPageHandler = conUtils.getBoardPageHandler(request);

        List<Board> items = (List<Board>) service.pageAndSort(boardPageHandler.getSearchCondition());
//        items.forEach(System.out::println);
        List<BigInteger> boardIdsWithAnyComment = items.stream()
                                                       .filter(board ->
                                                               {
                                                                   try
                                                                   {
                                                                       return service.isExistComment(board.getBoardId());
                                                                   }
                                                                   catch (Exception e)
                                                                   {
                                                                       //                                                e.printStackTrace();
                                                                   }
                                                                   return false;
                                                               })
                                                       .map(Board::getBoardId)
                                                       .collect(Collectors.toList());

        //          댓글이 존재하는 게시글
        m.addAttribute("boardIdsWithAnyComment", boardIdsWithAnyComment);

        m.addAttribute("items", items);
        m.addAttribute("boardPageHandler", boardPageHandler);
        m.addAttribute("today", LocalDateTime.now());

        return "allEmployeeBoard";
    }

    //게시글 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> delete(
        @PathVariable
            BigInteger employeeId,
        @PathVariable("boardId")
            BigInteger boardId, BoardSearchCondition searchCondition, Model model) throws Exception
    {
        //        model.addAttribute("employeeId", employeeId);

        final String log = "[" + employeeId + "] " + boardId + "번 게시글 삭제";

        //        try
        //        {
        Board board = (Board) service.select(BOARDS, boardId);
        //  본인이 작성한 글이 아니라면
        if (!board.getEmployeeId()
                  .equals(employeeId))
            return new ResponseEntity<>(HttpStatus.LOCKED);//423
        if (service.isExist(BOARDS, boardId) && service.delete(BOARDS, board.getBoardId()))
            return new ResponseEntity<>(HttpStatus.OK); // 200
        //        }
        //        catch (Exception e)
        //        {
        //            e.printStackTrace();
        //
        //        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); //402


    }


}