package com.brandedCompany.repository;

import com.brandedCompany.domain.Board;
import com.brandedCompany.domain.Comment;
import com.brandedCompany.domain.OrderHistory;
import com.brandedCompany.domain.handler.BoardPageHandler;
import com.brandedCompany.domain.searchCondition.BoardSearchCondition;
import com.brandedCompany.domain.searchCondition.CommentSearchCondition;
import com.brandedCompany.domain.searchCondition.OrderHistorySearchCondition;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigInteger;
import java.util.*;

import static com.brandedCompany.domain.searchCondition.BoardSearchCondition.Option.*;
import static com.brandedCompany.domain.searchCondition.OrderHistorySearchCondition.OrderHistoryOrderStatus.*;
import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
@Sql({"classpath:schema/brandedCompany_truncate.sql",
        "classpath:schema/brandedCompany_data.sql"})
public class PagingAndSortingRepositoryImplTest {

    /*@Qualifier("pagingAndSortingRepositoryImpl")
    @Autowired
    PagingAndSortingRepository repository;


    @DisplayName("pagingAndSortingRepository#countTotal test")
    @Test
    public void countTotalBoard() {
        try {
            BigInteger resultCount = repository.count(
                    new BoardSearchCondition(1, BigInteger.ONE));
            assertEquals(BigInteger.valueOf(1000), resultCount);

        } catch (ClassNotFoundException e) {
       //     e.printStackTrace();
        }
    }

    @DisplayName("pagingAndSortingRepository#countTotal test")
    @Test
    public void countTotalComment() {
        try {
            BigInteger resultCount = repository.count(
                    new CommentSearchCondition(1, BigInteger.valueOf(613)));
            System.out.println(resultCount);
            assertEquals(BigInteger.ONE, resultCount);

        } catch (ClassNotFoundException e) {
      //      e.printStackTrace();
        }
    }

    @DisplayName("pagingAndSortingRepository#pageAndSort test")
    @Test
    public void pageAndSortBoard() {
//        try {
//            Collection<Board> boards = (Collection<Board>) repository.pageAndSort(
//                    new SearchRequirement(1, 10, "", ""));
//            assertEquals(10, boards.size());
//
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @DisplayName("pagingAndSortingRepository#pageAndSort test")
    @Test
    public void pageAndSortComment() {
//        try {
////            Collection<Comment> comments = (Collection<Comment>) repository.pageAndSort(
////                    COMMENTS, BigInteger.valueOf(613),
////                    new CommentSearchRequirement(1, 10, "", ""));
//            Collection<Comment> comments = (Collection<Comment>) repository.pageAndSort(
//                    COMMENTS, BigInteger.valueOf(613),
//                    new CommentSearchCondition(1, 10, "", ""));
//            assertEquals(1, comments.size());
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @DisplayName("모든 고객의 주문 내역")
    @Test
    public void orderHistory_allCustomer() {
        try {
            Collection<OrderHistory> orderHistory = (Collection<OrderHistory>) repository.pageAndSort(new OrderHistorySearchCondition());
            assertEquals(665, orderHistory.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("보류한 주문 내역")
    @Test
    public void orderHistory_allCustomer_pending() {
        try {
            Collection<OrderHistory> orderHistory = (Collection<OrderHistory>) repository.pageAndSort(new OrderHistorySearchCondition(1, 100, Pending));
            assertEquals(110, orderHistory.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("취소한 주문 내역")
    @Test
    public void orderHistory_allCustomer_canceled() {
        try {
            Collection<OrderHistory> orderHistory = (Collection<OrderHistory>) repository.pageAndSort(new OrderHistorySearchCondition(1, 100, Canceled));
            assertEquals(97, orderHistory.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("출고한 주문 내역")
    @Test
    public void orderHistory_allCustomer_shipped() {
        try {
            Collection<OrderHistory> orderHistory = (Collection<OrderHistory>) repository.pageAndSort(new OrderHistorySearchCondition(1, 100, Shipped));
            assertEquals(436, orderHistory.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("고객 번호 18번의 주문 내역 1페이지")
    @Test
    public void customerId18_orderHistory_1page() {
        try {
            Collection<OrderHistory> orderHistory = (Collection<OrderHistory>) repository.pageAndSort(
                    new OrderHistorySearchCondition(1, BigInteger.valueOf(18)));
            assertEquals(10, orderHistory.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @DisplayName("고객 번호 18번의 주문 내역5페이지")
    @Test
    public void customerId18_orderHistory_51Page() {
        try {
            Collection<OrderHistory> orderHistory = (Collection<OrderHistory>) repository.pageAndSort(
                    new OrderHistorySearchCondition(5, BigInteger.valueOf(18)));
            assertEquals(0, orderHistory.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("고객 번호 18번의 출고한 주문 내역 1페이지")
    @Test
    public void customerId18_orderHistory_shipped_1page() {
        try {
            Collection<OrderHistory> orderHistory = (Collection<OrderHistory>) repository.pageAndSort(
                    new OrderHistorySearchCondition(1, BigInteger.valueOf(18)));
            assertEquals(10, orderHistory.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("고객 번호 18번의 출고한 주문 내역 5페이지")
    @Test
    public void customerId18_orderHistory_shipped_5page() {
        try {
            Collection<OrderHistory> orderHistory = (Collection<OrderHistory>) repository.pageAndSort(
                    new OrderHistorySearchCondition(5, 10, Shipped, BigInteger.valueOf(18)));
            assertEquals(0, orderHistory.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("고객 번호 18번의 취소한 주문 내역 1페이지")
    @Test
    public void customerId18_orderHistory_canceled_1page() {
        try {
            Collection<OrderHistory> orderHistory = (Collection<OrderHistory>) repository.pageAndSort(
                    new OrderHistorySearchCondition(1, 10, Canceled, BigInteger.valueOf(18)));
            assertEquals(0, orderHistory.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @DisplayName("고객 번호 18번의 취소한 주문 내역 5페이지")
    @Test
    public void customerId18_orderHistory_canceled_5page() {
        try {
            Collection<OrderHistory> orderHistory = (Collection<OrderHistory>) repository.pageAndSort(
                    new OrderHistorySearchCondition(5, 10, Canceled, BigInteger.valueOf(18)));
            assertEquals(0, orderHistory.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("고객 번호 18번의 보류한 주문 내역 1페이지")
    @Test
    public void customerId18_orderHistory_pending_1page() {

        try {
            Collection<OrderHistory> orderHistory = (Collection<OrderHistory>) repository.pageAndSort(
                    new OrderHistorySearchCondition(1, 10, Pending, BigInteger.valueOf(18)));
            assertEquals(0, orderHistory.size());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @DisplayName("고객 번호 18번의 취소한 주문 내역 5페이지")
    @Test
    public void customerId18_orderHistory_pending_5page() {
        try {
            Collection<OrderHistory> orderHistory = (Collection<OrderHistory>) repository.pageAndSort(
                    new OrderHistorySearchCondition(5, 10, Pending, BigInteger.valueOf(18)));
            assertEquals(0, orderHistory.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("고객 번호 18번의 100 개월 전 부터 오늘까지 출고된 주문 내역 ")
    @Test
    public void pageAndSort_shipped() {
        try {
            Collection<OrderHistory> orderHistory = (Collection<OrderHistory>) repository.pageAndSort(
                    new OrderHistorySearchCondition(1, 100, Shipped, BigInteger.valueOf(18)));
            assertEquals(10, orderHistory.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("고객 번호 18번의 100 개월 전 부터 오늘까지 보류된 주문 내역 ")
    @Test
    public void pageAndSort_pending() {
        try {
            Collection<OrderHistory> orderHistory = (Collection<OrderHistory>) repository.pageAndSort(
                    new OrderHistorySearchCondition(1, 100, Pending, BigInteger.valueOf(18)));
            assertEquals(0, orderHistory.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @DisplayName("고객 번호 18번의 100 개월 전 부터 오늘까지 취소된 주문 내역 ")
    @Test
    public void pageAndSort_canceled() {
        try {
            Collection<OrderHistory> orderHistory = (Collection<OrderHistory>) repository.pageAndSort(
                    new OrderHistorySearchCondition(1, 100, Pending, BigInteger.valueOf(18)));
            assertEquals(0, orderHistory.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("고객 번호 18번의 100 개월 전 부터 오늘까지 출고된 주문 내역 2페이지")
    @Test
    public void pageAndSort_shipped_2page() {
        try {
            Collection<OrderHistory> orderHistory = (Collection<OrderHistory>) repository.pageAndSort(
                    new OrderHistorySearchCondition(2, 100, Shipped, BigInteger.valueOf(18)));
            assertEquals(5, orderHistory.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("고객 번호 18번의 100 개월 전 부터 오늘까지 보류된 주문 내역 2페이지 ")
    @Test
    public void pageAndSort_pending_2page() {
        try {
            Collection<OrderHistory> orderHistory = (Collection<OrderHistory>) repository.pageAndSort(
                    new OrderHistorySearchCondition(2, 100, Pending, BigInteger.valueOf(18)));
            assertEquals(0, orderHistory.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("고객 번호 18번의 100 개월 전 부터 오늘까지 취소된 주문 내역 2페이지 ")
    @Test
    public void pageAndSort_canceled_2page() {
        try {
            Collection<OrderHistory> orderHistory = (Collection<OrderHistory>) repository.pageAndSort(
                    new OrderHistorySearchCondition(2, 100, Pending, BigInteger.valueOf(18)));
            assertEquals(0, orderHistory.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    @DisplayName("게시판 1페이지 게시글 번호, 작성자 번호 ")
    public void paging_board1Page() {
        BoardSearchCondition searchCondition = new BoardSearchCondition(1);
        try {
            // 게시글 개수
            BigInteger count = repository.count(searchCondition);
            Collection<Board> boardEmpIdIn1Page = (Collection<Board>) repository.pageAndSort(searchCondition);

//            Map <게시글 번호, 직원 번호>
            Map<BigInteger, BigInteger> realBoardIdEmpIdIn1Page = new HashMap<>();
            realBoardIdEmpIdIn1Page.put(BigInteger.valueOf(84), BigInteger.valueOf(679));
            realBoardIdEmpIdIn1Page.put(BigInteger.valueOf(596), BigInteger.valueOf(544));
            realBoardIdEmpIdIn1Page.put(BigInteger.valueOf(689), BigInteger.valueOf(360));
            realBoardIdEmpIdIn1Page.put(BigInteger.valueOf(21), BigInteger.valueOf(2));
            realBoardIdEmpIdIn1Page.put(BigInteger.valueOf(830), BigInteger.valueOf(995));
            realBoardIdEmpIdIn1Page.put(BigInteger.valueOf(964), BigInteger.valueOf(891));
            realBoardIdEmpIdIn1Page.put(BigInteger.valueOf(215), BigInteger.valueOf(48));
            realBoardIdEmpIdIn1Page.put(BigInteger.valueOf(648), BigInteger.valueOf(714));
            realBoardIdEmpIdIn1Page.put(BigInteger.valueOf(129), BigInteger.valueOf(45));
            realBoardIdEmpIdIn1Page.put(BigInteger.valueOf(166), BigInteger.valueOf(294));

            for (Board board : boardEmpIdIn1Page) {
                BigInteger realEmpId = realBoardIdEmpIdIn1Page.get(board.getBoardId());
                assertEquals(board.getEmployeeId(), realEmpId);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("게시판 50 페이지 게시글 번호, 작성자 번호 ")
    public void paging_board50Page() {
        BoardSearchCondition searchCondition = new BoardSearchCondition(50);
        try {
            // 게시글 개수
            BigInteger count = repository.count(searchCondition);
            Collection<Board> boardIdEmpIdIn50Page = (Collection<Board>) repository.pageAndSort(searchCondition);

//            Map <게시글 번호, 직원 번호>
            Map<BigInteger, BigInteger> realBoardIdEmpIdIn50Page = new HashMap<>();
            realBoardIdEmpIdIn50Page.put(BigInteger.valueOf(637), BigInteger.valueOf(60));
            realBoardIdEmpIdIn50Page.put(BigInteger.valueOf(679), BigInteger.valueOf(435));
            realBoardIdEmpIdIn50Page.put(BigInteger.valueOf(157), BigInteger.valueOf(867));
            realBoardIdEmpIdIn50Page.put(BigInteger.valueOf(799), BigInteger.valueOf(944));
            realBoardIdEmpIdIn50Page.put(BigInteger.valueOf(436), BigInteger.valueOf(327));
            realBoardIdEmpIdIn50Page.put(BigInteger.valueOf(72), BigInteger.valueOf(102));
            realBoardIdEmpIdIn50Page.put(BigInteger.valueOf(988), BigInteger.valueOf(991));
            realBoardIdEmpIdIn50Page.put(BigInteger.valueOf(642), BigInteger.valueOf(237));
            realBoardIdEmpIdIn50Page.put(BigInteger.valueOf(591), BigInteger.valueOf(280));
            realBoardIdEmpIdIn50Page.put(BigInteger.valueOf(255), BigInteger.valueOf(448));

            for (Board board : boardIdEmpIdIn50Page) {
                BigInteger realEmpId = realBoardIdEmpIdIn50Page.get(board.getBoardId());
                assertEquals(board.getEmployeeId(), realEmpId);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("게시판 마지막 페이지 게시글 번호, 작성자 번호")
    public void paging_boardLastPage() {
        BoardSearchCondition searchCondition = new BoardSearchCondition(100);

        try {
            // 게시글 개수
            BigInteger count = repository.count(searchCondition);
            BoardPageHandler pageHandler = new BoardPageHandler(count, searchCondition);

            boolean isEndPage = pageHandler.getEndPage().equals(searchCondition.getPAGE());
            if (isEndPage) {
                Collection<Board> boardIdEmpIdInLastPage = (Collection<Board>) repository.pageAndSort(searchCondition);

//            Map <게시글 번호, 직원 번호>
                Map<BigInteger, BigInteger> realBoardIdEmpIdInLastPage = new HashMap<>();
                realBoardIdEmpIdInLastPage.put(BigInteger.valueOf(710), BigInteger.valueOf(179));
                realBoardIdEmpIdInLastPage.put(BigInteger.valueOf(970), BigInteger.valueOf(810));
                realBoardIdEmpIdInLastPage.put(BigInteger.valueOf(165), BigInteger.valueOf(598));
                realBoardIdEmpIdInLastPage.put(BigInteger.valueOf(644), BigInteger.valueOf(221));
                realBoardIdEmpIdInLastPage.put(BigInteger.valueOf(441), BigInteger.valueOf(639));
                realBoardIdEmpIdInLastPage.put(BigInteger.valueOf(690), BigInteger.valueOf(596));
                realBoardIdEmpIdInLastPage.put(BigInteger.valueOf(787), BigInteger.valueOf(966));
                realBoardIdEmpIdInLastPage.put(BigInteger.valueOf(640), BigInteger.valueOf(410));
                realBoardIdEmpIdInLastPage.put(BigInteger.valueOf(819), BigInteger.valueOf(909));
                realBoardIdEmpIdInLastPage.put(BigInteger.valueOf(416), BigInteger.valueOf(18));

                for (Board board : boardIdEmpIdInLastPage) {
                    BigInteger realEmpId = realBoardIdEmpIdInLastPage.get(board.getBoardId());
                    assertEquals(board.getEmployeeId(), realEmpId);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("게시판 제목과 내용에 'Black Nativity' 검색 1페이지")
    public void boardsTitleContentAsBlackNativity() {
        BoardSearchCondition searchCondition = new BoardSearchCondition(1, "Black Nativity", TITLE_CONTENT);
        try {
            BigInteger count = repository.count(searchCondition);

            Collection<Board> boards = (Collection<Board>) repository.pageAndSort(searchCondition);
            assertEquals(1, boards.size());
            Board board = boards.iterator().next();
            assertEquals(new Board(BigInteger.valueOf(84), BigInteger.valueOf(679)),
                    new Board(board.getBoardId(), board.getEmployeeId()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("게시판 제목에 'Black Nativity' 로 검색 1페이지")
    public void boardsTitleAsBlackNativity() {
        BoardSearchCondition searchCondition = new BoardSearchCondition(1, "Black Nativity", TITLE);
        try {
            BigInteger count = repository.count(searchCondition);

            Collection<Board> boards = (Collection<Board>) repository.pageAndSort(searchCondition);
            assertEquals(1, boards.size());
            Board board = boards.iterator().next();
            assertEquals(new Board(BigInteger.valueOf(84), BigInteger.valueOf(679)),
                    new Board(board.getBoardId(), board.getEmployeeId()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("게시판 내용에 'Black Nativity' 로 검색 1페이지")
    public void boardsContentAsBlackNativity() {
        BoardSearchCondition searchCondition = new BoardSearchCondition(1, "Black Nativity", CONTENT);
        Collection<Board> boards = null;
        try {
            BigInteger count = repository.count(searchCondition);

            boards = (Collection<Board>) repository.pageAndSort(searchCondition);
            assertEquals(0, boards.size());

        } catch (NoSuchElementException e) {
            assertNull(boards.iterator().next());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("게시판 작성자에 'Sienna Simpson' 로 검색 1페이지")
    public void boardsPublisherAsSiennaSimpson() {
        BoardSearchCondition searchCondition = new BoardSearchCondition(1, "Sienna Simpson", PUBLISHER);

        try {
            BigInteger count = repository.count(searchCondition);
            assertEquals(1, count);

            Collection<Board> boards = (Collection<Board>) repository.pageAndSort(searchCondition);
            assertEquals(1, boards.size());

            Board borad = boards.iterator().next();
            assertEquals(new Board(BigInteger.valueOf(128), BigInteger.valueOf(67)), borad);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("게시판 내용과 작성자에 'Mac' 로 검색 1페이지")
    public void isSame_1pageOf_boardsPublisherContentAsMac() {
        BoardSearchCondition searchCondition = new BoardSearchCondition(1, "Mac", PUBLISHER_CONTENT);
        List<Board> realPage1BoardIdEmpId = new ArrayList<>();
        realPage1BoardIdEmpId.add(new Board(BigInteger.valueOf(914), BigInteger.valueOf(241)));
        realPage1BoardIdEmpId.add(new Board(BigInteger.valueOf(937), BigInteger.valueOf(683)));
        realPage1BoardIdEmpId.add(new Board(BigInteger.valueOf(541), BigInteger.valueOf(186)));
        realPage1BoardIdEmpId.add(new Board(BigInteger.valueOf(108), BigInteger.valueOf(369)));
        realPage1BoardIdEmpId.add(new Board(BigInteger.valueOf(100), BigInteger.valueOf(801)));
        realPage1BoardIdEmpId.add(new Board(BigInteger.valueOf(348), BigInteger.valueOf(439)));
        realPage1BoardIdEmpId.add(new Board(BigInteger.valueOf(68), BigInteger.valueOf(1021)));
        realPage1BoardIdEmpId.add(new Board(BigInteger.valueOf(682), BigInteger.valueOf(221)));
        realPage1BoardIdEmpId.add(new Board(BigInteger.valueOf(524), BigInteger.valueOf(801)));
        realPage1BoardIdEmpId.add(new Board(BigInteger.valueOf(286), BigInteger.valueOf(645)));
        try {
            BigInteger count = repository.count(searchCondition);

            List<Board> boards = (List<Board>) repository.pageAndSort(searchCondition);
            for (int i = 0; i < boards.size(); i++) assertTrue(realPage1BoardIdEmpId.get(i).equals(boards.get(i)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("게시판 내용과 작성자에 'Mac'  로 검색 2페이지")
    public void isSame_2pageOf_boardsPublisherContentAsMac() {
        BoardSearchCondition searchCondition = new BoardSearchCondition(2, "Mac", PUBLISHER_CONTENT);
        List<Board> realPage1BoardIdEmpId = new ArrayList<>();
        realPage1BoardIdEmpId.add(new Board(BigInteger.valueOf(673), BigInteger.valueOf(829)));
        realPage1BoardIdEmpId.add(new Board(BigInteger.valueOf(495), BigInteger.valueOf(926)));
        realPage1BoardIdEmpId.add(new Board(BigInteger.valueOf(887), BigInteger.valueOf(926)));
        realPage1BoardIdEmpId.add(new Board(BigInteger.valueOf(483), BigInteger.valueOf(829)));
        realPage1BoardIdEmpId.add(new Board(BigInteger.valueOf(380), BigInteger.valueOf(683)));
        realPage1BoardIdEmpId.add(new Board(BigInteger.valueOf(333), BigInteger.valueOf(829)));
        realPage1BoardIdEmpId.add(new Board(BigInteger.valueOf(660), BigInteger.valueOf(926)));
        realPage1BoardIdEmpId.add(new Board(BigInteger.valueOf(364), BigInteger.valueOf(186)));
        realPage1BoardIdEmpId.add(new Board(BigInteger.valueOf(205), BigInteger.valueOf(683)));
        realPage1BoardIdEmpId.add(new Board(BigInteger.valueOf(322), BigInteger.valueOf(829)));
        try {
            BigInteger count = repository.count(searchCondition);

            List<Board> boards = (List<Board>) repository.pageAndSort(searchCondition);
            for (int i = 0; i < boards.size(); i++) assertTrue(realPage1BoardIdEmpId.get(i).equals(boards.get(i)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("모든 댓글 1페이지")
    public void PagingAllComments1Page() {
        CommentSearchCondition searchCondition = new CommentSearchCondition(1);
//        실제 모든 댓글 1 페이지의 댓글번호와 부모댓글번호(해당 댓글이 답글인경우)
        List<Comment> realCommentCommentIdAndParentCommentId = new ArrayList<>();
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(95), BigInteger.valueOf(95)));
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(94), BigInteger.valueOf(94)));
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(93), BigInteger.valueOf(93)));
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(92), BigInteger.valueOf(92)));
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(91), BigInteger.valueOf(91)));
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(90), BigInteger.valueOf(90)));

        try {
            BigInteger count = repository.count(searchCondition);
            List<Comment> comments = (List<Comment>) repository.pageAndSort(searchCondition);
            for (int i = 0; i < comments.size(); i++)
                assertTrue(realCommentCommentIdAndParentCommentId.get(i).equals(comments.get(i)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("모든 댓글 5페이지")
    public void PagingAllComments5Page() {
        CommentSearchCondition searchCondition = new CommentSearchCondition(55);
//        실제 모든 댓글 5 페이지의 댓글번호와 부모댓글번호(해당 댓글이 답글인경우)
        List<Comment> realCommentCommentIdAndParentCommentId = new ArrayList<>();
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(45), BigInteger.valueOf(45)));
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(44), BigInteger.valueOf(44)));
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(43), BigInteger.valueOf(43)));
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(42), BigInteger.valueOf(42)));
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(41), BigInteger.valueOf(41)));
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(40), BigInteger.valueOf(40)));
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(39), BigInteger.valueOf(39)));
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(38), BigInteger.valueOf(38)));
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(37), BigInteger.valueOf(37)));
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(36), BigInteger.valueOf(36)));

        try {
            BigInteger count = repository.count(searchCondition);
            List<Comment> comments = (List<Comment>) repository.pageAndSort(searchCondition);
            for (int i = 0; i < comments.size(); i++)
                assertTrue(realCommentCommentIdAndParentCommentId.get(i).equals(comments.get(i)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("모든 댓글 9페이지")
    public void PagingAllComments9Page() {
        CommentSearchCondition searchCondition = new CommentSearchCondition(55);
//        실제 모든 댓글 9 페이지의 댓글번호와 부모댓글번호(해당 댓글이 답글인경우)
        List<Comment> realCommentCommentIdAndParentCommentId = new ArrayList<>();
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(6), BigInteger.valueOf(6)));
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(5), BigInteger.valueOf(5)));
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(4), BigInteger.valueOf(4)));
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(3), BigInteger.valueOf(3)));
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(2), BigInteger.valueOf(2)));
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(1), BigInteger.valueOf(1)));

        try {
            BigInteger count = repository.count(searchCondition);
            List<Comment> comments = (List<Comment>) repository.pageAndSort(searchCondition);
            for (int i = 0; i < comments.size(); i++)
                assertTrue(realCommentCommentIdAndParentCommentId.get(i).equals(comments.get(i)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("게시글 번호 655의 댓글 1페이지")
    public void PagingComments1Page_of_BoardId655() {
        CommentSearchCondition searchCondition = new CommentSearchCondition(1, BigInteger.valueOf(655));
//        실제 모든 댓글 1 페이지의 댓글번호와 부모댓글번호(해당 댓글이 답글인경우)
        List<Comment> realCommentCommentIdAndParentCommentId = new ArrayList<>();
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(6), BigInteger.valueOf(6)));
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(5), BigInteger.valueOf(5)));
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(4), BigInteger.valueOf(4)));
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(3), BigInteger.valueOf(3)));
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(2), BigInteger.valueOf(2)));
        realCommentCommentIdAndParentCommentId.add(new Comment(BigInteger.valueOf(1), BigInteger.valueOf(1)));

        try {
            BigInteger count = repository.count(searchCondition);
            List<Comment> comments = (List<Comment>) repository.pageAndSort(searchCondition);
            for (int i = 0; i < comments.size(); i++)
                assertTrue(realCommentCommentIdAndParentCommentId.get(i).equals(comments.get(i)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("해당 게시글의 댓글 수")
    @Test
    public void isExistComment() {
        try {
            System.out.println(repository.isExistComment(BigInteger.valueOf(17)));
            System.out.println(repository.isExistComment(BigInteger.valueOf(16)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


}