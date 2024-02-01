package com.brandedCompany.util;

import com.brandedCompany.domain.Employee;
import com.brandedCompany.domain.handler.*;
import com.brandedCompany.domain.searchCondition.*;
import com.brandedCompany.serivce.PagingAndSortingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

@Component
public class EmployeeControllerUtils
{
    private static final Logger logger = LoggerFactory.getLogger(EmployeeControllerUtils.class);

    @Autowired
    public EmployeeControllerUtils(
        @Qualifier("pagingAndSortingServiceImpl")
            PagingAndSortingService service)
    {
        this.service = service;
    }

    @Qualifier("pagingAndSortingServiceImpl") private final PagingAndSortingService service;

    public static boolean checkName(String checkName)
    {
        int numCount = 0;
        for (int i = 0; i < checkName.length(); i++)
        {
            char c = checkName.charAt(i);
            if (47 < Character.valueOf(c) && Character.valueOf(c) < 58)
                numCount += 1;
        }
        return 2 < numCount
            ? false
            : true;
    }

    // 쿠키 추가
    public void addCookie(String cookieName, String cookieValue, HttpServletResponse response)
    {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge(60 * 60 * 24);
        response.addCookie(cookie);
        logger.info(cookieValue + "번 고객 아이디 기억하기");
    }

    public void removeCookie(String cookieName, HttpServletRequest request, HttpServletResponse response)
    {

        Optional<Cookie[]> cookies = Optional.ofNullable(request.getCookies());
        if (cookies.isEmpty())
            return;
        else if (cookies.isPresent())
            for (Cookie cookie : request.getCookies())
            {
                if (cookieName.equals(cookie.getName()))
                {
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    return;
                }
            }

    }

    public void checkRememberId(Employee employee, boolean rememberId, HttpServletRequest request, HttpServletResponse response)
    {
        if (null != employee.getEmployeeId())
        {
            final String COOKIE_NAME = "rememberEmployeeId";
            if (rememberId)
                addCookie(COOKIE_NAME, String.valueOf(employee.getEmployeeId()), response);
            else
                removeCookie(COOKIE_NAME, request, response);
        }
    }

    public String findLastErrorCode(BindingResult result)
    {
        List<ObjectError> allErrors = result.getAllErrors();
        return allErrors.get(allErrors.size() - 1)
                        .getCode();

    }

    public ProductPageHandler getProductPageHandler(HttpServletRequest request) throws ClassNotFoundException
    {
        Optional<String> pagePt = Optional.ofNullable(request.getParameter("page"));
        Optional<String> targetPt = Optional.ofNullable(request.getParameter("target"));
        Optional<String> keywordPt = Optional.ofNullable(request.getParameter("keyword"));
        Optional<String> directionPt = Optional.ofNullable(request.getParameter("direction"));

        ProductSearchCondition.ProductSearchConditionBuilder builder = new ProductSearchCondition.ProductSearchConditionBuilder();

        pagePt.ifPresent(s -> builder.page(Integer.valueOf(s)));
        targetPt.ifPresent(s -> builder.target(ProductSearchCondition.ProductOrderTarget.valueOf(s)));
        if (keywordPt.isPresent() && !keywordPt.get()
                                               .isBlank())
            builder.keyword(keywordPt.get());

        directionPt.ifPresent(s -> builder.direction(ProductSearchCondition.ProductSortDirection.valueOf(s)));

        ProductSearchCondition searchCondition = builder.build();
        //        System.out.println("searchCondition.getQueryString() in controller= " + searchCondition.getQueryString());

        ProductPageHandler productPageHandler = new ProductPageHandler(service.count(searchCondition), searchCondition);
        //        System.out.println("orderHistoryPageHandler.getQueryString() in controller= " + productPageHandler.getQueryString());
        return productPageHandler;
    }

    public ProductPageHandler getProductPageHandler(Integer page, ProductSearchCondition searchCondition) throws ClassNotFoundException
    {
        return new ProductPageHandler(service.count(searchCondition), searchCondition);
    }


    public InventoryPageHandler getInventoryPageHandler(HttpServletRequest request) throws ClassNotFoundException
    {
        Optional<String> sortDirectionPt = Optional.ofNullable(request.getParameter("sortDirection")); // 검색 조건 정렬 방향( 오름차순 혹은 내림차순)
        Optional<String> sortTargetPt = Optional.ofNullable(request.getParameter("sortTarget"));// 검색 조건 정렬 대상(나라,주소,가격....)
        Optional<String> keywordPt = Optional.ofNullable(request.getParameter("keyword")); // 검색 조건 키워드
        Optional<String> pagePt = Optional.ofNullable(request.getParameter("page")); // 검색 조건 페이지

        InventorySearchCondition.InventorySearchConditionBuilder builder = new InventorySearchCondition.InventorySearchConditionBuilder();

        if(keywordPt.isPresent()&&!keywordPt.get().isBlank())
            builder.keyword(keywordPt.get().trim());
//        keywordPt.ifPresent(s -> builder.keyword(s.trim()));
        sortDirectionPt.ifPresent(s -> builder.sortDirection(InventorySearchCondition.InventorySortDirection.valueOf(s)));
        sortTargetPt.ifPresent(s -> builder.sortTarget(InventorySearchCondition.InventorySortTarget.valueOf(s)));
        pagePt.ifPresent(s -> builder.page(Integer.valueOf(s)));

        InventorySearchCondition searchCondition = builder.build();
        //        System.out.println("searchCondition.getQueryString() = " + searchCondition.getQueryString());
//        System.out.println("service = " + service);
        InventoryPageHandler inventoryPageHandler = new InventoryPageHandler(service.count(searchCondition), searchCondition);
        //  System.out.println("inventoryPageHandler.getQueryString() = " + inventoryPageHandler.getQueryString());
        return inventoryPageHandler;
    }

    public BoardPageHandler getBoardPageHandler(HttpServletRequest request) throws ClassNotFoundException
    {
        Optional<String> pagePt = Optional.ofNullable(request.getParameter("page"));
        Optional<String> keywordPt = Optional.ofNullable(request.getParameter("keyword"));
        Optional<String> optionPt = Optional.ofNullable(request.getParameter("option"));

        BoardSearchCondition.BoardSearchConditionBuilder builder = new BoardSearchCondition.BoardSearchConditionBuilder();

        if (pagePt.isPresent() && 0 != Integer.valueOf(pagePt.get()))
            builder.page(Integer.valueOf(pagePt.get()));
        if (keywordPt.isPresent() && !keywordPt.get()
                                               .isBlank())
            builder.keyword(keywordPt.get());
        if (optionPt.isPresent() && !"".equals(optionPt.get()))
            builder.option(BoardSearchCondition.Option.valueOf(optionPt.get()));

        BoardSearchCondition searchCondition = builder.build();
        BoardPageHandler boardPgeHandler = new BoardPageHandler(service.count(searchCondition), searchCondition);
//        System.out.println("boardPgeHandler.getQueryString() = " + boardPgeHandler.getQueryString());
        return boardPgeHandler;
    }

    public CommentPageHandler getCommentPageHandler(BigInteger boardId, HttpServletRequest request) throws ClassNotFoundException
    {
        Optional<String> commentPagePt = Optional.ofNullable(request.getParameter("commentPage"));

        CommentSearchCondition.CommentSearchConditionBuilder builder = new CommentSearchCondition.CommentSearchConditionBuilder();

        if (commentPagePt.isPresent() && 0 != Integer.valueOf(commentPagePt.get()))
        {
            builder.page(Integer.valueOf(commentPagePt.get()));

        }
        builder.boardId(boardId);
        CommentSearchCondition searchCondition = builder.build();
        //        System.out.println("searchCondition.getQueryString() = " + searchCondition.getQueryString());
        return new CommentPageHandler(service.count(searchCondition), searchCondition);
    }

    public OrderHistoryPageHandler getOrderHistoryPageHandler(HttpServletRequest request) throws ClassNotFoundException
    {
        Optional<String> pagePt = Optional.ofNullable(request.getParameter("page"));
        Optional<String> customerIdPt = Optional.ofNullable(request.getParameter("customerId")); // 고객 번호
        Optional<String> monthAgoPt = Optional.ofNullable(request.getParameter("monthAgo")); // 개월 수
        Optional<String> directionPt = Optional.ofNullable(request.getParameter("direction")); // 정렬 방향
        Optional<String> statusPt = Optional.ofNullable(request.getParameter("status"));// 주문 상태
        Optional<String> keywordPt = Optional.ofNullable(request.getParameter("keyword")); // 키워드
        Optional<String> targetPt = Optional.ofNullable(request.getParameter("target")); // 정렬 대상

        OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();

        pagePt.ifPresent(s -> builder.page(Integer.valueOf(s)));
        monthAgoPt.ifPresent(s -> builder.monthAgo(Integer.valueOf(s)));
        directionPt.ifPresent(s -> builder.direction(OrderHistorySearchCondition.OrderHistorySortDirection.valueOf(s)));
        statusPt.ifPresent(s -> builder.status(OrderHistorySearchCondition.OrderHistoryOrderStatus.valueOf(s)));
        //        keywordPt.ifPresent(builder::keyword);
        if (keywordPt.isPresent() && !keywordPt.get()
                                               .isBlank())
            builder.keyword(keywordPt.get());
        targetPt.ifPresent(s -> builder.target(OrderHistorySearchCondition.OrderHistorySortTarget.valueOf(s)));

        OrderHistorySearchCondition searchCondition = builder.build();
        //        System.out.println("searchCondition.getQueryString() = " + searchCondition.getQueryString());

        OrderHistoryPageHandler orderHistoryPageHandler = new OrderHistoryPageHandler(service.count(searchCondition), searchCondition);
        //        System.out.println("orderHistoryPageHandler.getQueryString() = " + orderHistoryPageHandler.getQueryString());
        return orderHistoryPageHandler;
    }

    public void checkAllCustomerOrderHistoryRequestParameter(HttpServletRequest request) throws IllegalArgumentException
    {
        List<String> acceptableParameters = new ArrayList<>();
        acceptableParameters.add("page");
        acceptableParameters.add("customerId");
        acceptableParameters.add("direction");
        acceptableParameters.add("status");
        acceptableParameters.add("keyword");
        acceptableParameters.add("target");

        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements())
        {
            String requestedParameter = parameterNames.nextElement();
            boolean isAcceptable = acceptableParameters.stream()
                                            .anyMatch(acceptableParameter -> acceptableParameter.equals(requestedParameter));
            if(!isAcceptable) throw new IllegalArgumentException("Not acceptable requested parameter.");
        }

    }

    public void checkBoardRequestParameter(HttpServletRequest request)
    {
        List<String> acceptableParameters = new ArrayList<>();
        acceptableParameters.add("page");
        acceptableParameters.add("keyword");
        acceptableParameters.add("option");
        acceptableParameters.add("commentPage");

        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements())
        {
            String requestedParameter = parameterNames.nextElement();
            boolean isAcceptable = acceptableParameters.stream()
                                                       .anyMatch(acceptableParameter -> acceptableParameter.equals(requestedParameter));
            if(!isAcceptable) throw new IllegalArgumentException("Not acceptable requested parameter.");
        }

    }

    public void checkInventoryRequestParameter(HttpServletRequest request)
    {
        List<String> acceptableParameters = new ArrayList<>();

        acceptableParameters.add("sortDirection"); // 검색 조건 정렬 방향( 오름차순 혹은 내림차순)
        acceptableParameters.add("sortTarget");// 검색 조건 정렬 대상(나라,주소,가격....)
        acceptableParameters.add("keyword"); // 검색 조건 키워드
        acceptableParameters.add("page"); // 검색 조건 페이지
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements())
        {
            String requestedParameter = parameterNames.nextElement();
            boolean isAcceptable = acceptableParameters.stream()
                                                       .anyMatch(acceptableParameter -> acceptableParameter.equals(requestedParameter));
            if(!isAcceptable) throw new IllegalArgumentException("Not acceptable requested parameter.");
        }

    }





}
