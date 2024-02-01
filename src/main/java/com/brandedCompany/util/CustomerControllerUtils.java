package com.brandedCompany.util;

import com.brandedCompany.domain.Customer;
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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

@Component
public class CustomerControllerUtils
{
    private static final Logger logger = LoggerFactory.getLogger(CustomerControllerUtils.class);

    @Qualifier("pagingAndSortingServiceImpl")
    private PagingAndSortingService service;

    @Autowired
    public CustomerControllerUtils(
        @Qualifier("pagingAndSortingServiceImpl")
            PagingAndSortingService service)
    {
        this.service = service;
    }

    public static boolean checkName(String checkName) {
        int numCount = 0;
        for (int i = 0; i < checkName.length(); i++) {
            char c = checkName.charAt(i);
            if (47 < Character.valueOf(c) && Character.valueOf(c) < 58) numCount += 1;
        }
        return 2 < numCount ? false : true;
    }

    // 쿠키 추가
    public void addCookie(String cookieName, String cookieValue, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge(60 * 60 * 24);
        response.addCookie(cookie);
        logger.info(cookieValue+ "번 고객 아이디 기억하기");
    }

    public void removeCookie(String cookieName, HttpServletRequest request,HttpServletResponse response) {

        Optional<Cookie[]> cookies = Optional.ofNullable(request.getCookies());
        if (cookies.isEmpty()) return;
        else if (cookies.isPresent())
            for (Cookie cookie : request.getCookies())
                if (cookieName.equals(cookie.getName())) {
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    return;
                }
    }
    public  String findLastErrorCode(BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        return  allErrors.get(allErrors.size()-1).getCode();

    }
    // 아이디 기억하기
    public void checkRememberId(Customer customer, boolean rememberId, HttpServletRequest request, HttpServletResponse response)
    {
        if (null != customer.getCustomerId())
        {
            final String COOKIE_NAME = "rememberCustomerId";
            if (rememberId)
                addCookie(COOKIE_NAME, String.valueOf(customer.getCustomerId()), response);
            else
                removeCookie(COOKIE_NAME, request, response);
        }
    }
    public OrderHistoryPageHandler getOrderHistoryPageHandler(HttpServletRequest request, BigInteger customerId) throws ClassNotFoundException {

        Optional<String> pagePt = Optional.ofNullable(request.getParameter("page"));
        Optional<String> monthAgoPt = Optional.ofNullable(request.getParameter("monthAgo")); // 개월 수

        OrderHistorySearchCondition.OrderHistorySearchConditionBuilder builder = new OrderHistorySearchCondition.OrderHistorySearchConditionBuilder();

        if (pagePt.isPresent())
            builder.page(Integer.valueOf(request.getParameter("page")));
        if (monthAgoPt.isPresent())
        {
            //            if(!"전체".equals(monthAgoPt.get()))
            builder.monthAgo(Integer.valueOf(request.getParameter("monthAgo")));
        }
        builder.customerId(customerId);

        OrderHistorySearchCondition searchCondition = builder.build();
        //        System.out.println("searchCondition.getQueryString() in controller= " + searchCondition.getQueryString());

        //        System.out.println("orderHistoryPageHandler.getQueryString() in controller= " + orderHistoryPageHandler.getQueryString());
        return new OrderHistoryPageHandler(service.count(searchCondition), searchCondition);
    }

    public CartPageHandler getCartPageHandler(BigInteger customerId, HttpServletRequest request) throws ClassNotFoundException
    {

        Optional<String> pagePt = Optional.ofNullable(request.getParameter("page"));

        CartSearchCondition.CartSearchConditionBuilder builder = new CartSearchCondition.CartSearchConditionBuilder();
        if (null != customerId)
            builder.customerId(customerId);
        if (pagePt.isPresent())
            if (Integer.valueOf(pagePt.get()).equals(0))
                builder.page(Integer.valueOf(request.getParameter("page")));

        SearchCondition searchCondition = builder.build();

        return new CartPageHandler(service.count(searchCondition), searchCondition);
    }

    public ProductPageHandler getProductPageHandler(HttpServletRequest request) throws ClassNotFoundException
    {
        ProductSearchCondition searchCondition = null;

        Optional<String> pagePt = Optional.ofNullable(request.getParameter("page"));
        Optional<String> targetPt = Optional.ofNullable(request.getParameter("target"));
        Optional<String> directionPt = Optional.ofNullable(request.getParameter("direction"));

        ProductSearchCondition.ProductSearchConditionBuilder builder = new ProductSearchCondition.ProductSearchConditionBuilder();

        pagePt.ifPresent(s -> builder.page(Integer.valueOf(s)));
        targetPt.ifPresent(s -> builder.target(ProductSearchCondition.ProductOrderTarget.valueOf(s)));
        directionPt.ifPresent(s -> builder.direction(ProductSearchCondition.ProductSortDirection.valueOf(s)));

        searchCondition = builder.build();
        //        System.out.println("searchCondition.getQueryString() in controller= " + searchCondition.getQueryString());

        ProductPageHandler productPageHandler = new ProductPageHandler(service.count(searchCondition), searchCondition);
        //        System.out.println("orderHistoryPageHandler.getQueryString() in controller= " + productPageHandler.getQueryString());
        return productPageHandler;
    }


    public void checkCartRequestParmeter(HttpServletRequest request)
    {

        List<String> acceptableParameters = new ArrayList<>();
        acceptableParameters.add("page");

        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements())
        {
            String requestedParameter = parameterNames.nextElement();
            boolean isAcceptable = acceptableParameters.stream()
                                                       .anyMatch(acceptableParameter -> acceptableParameter.equals(requestedParameter));
            if(!isAcceptable) throw new IllegalArgumentException("Not acceptable requested parameter.");
        }
    }

    public void checkProductRequestParameter(HttpServletRequest request)
    {
        List<String> acceptableParameters = new ArrayList<>();
        acceptableParameters.add("page");
        acceptableParameters.add("target");
        acceptableParameters.add("direction");

        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements())
        {
            String requestedParameter = parameterNames.nextElement();
            boolean isAcceptable = acceptableParameters.stream()
                                                       .anyMatch(acceptableParameter -> acceptableParameter.equals(requestedParameter));
            if(!isAcceptable) throw new IllegalArgumentException("Not acceptable requested parameter.");
        }
    }



    public void checkOrderHistoryRequestParmeter(HttpServletRequest request)
    {
        List<String> acceptableParameters = new ArrayList<>();
        acceptableParameters.add("page");
        acceptableParameters.add("monthAgo");

        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements())
        {
            String requestedParameter = parameterNames.nextElement();
            boolean isAcceptable = acceptableParameters.stream()
                                                       .anyMatch(acceptableParameter -> acceptableParameter.equals(requestedParameter));
            if(!isAcceptable) throw new IllegalArgumentException("Not acceptable requested parameter.");
        }
    }

    public void checkCartRequestParameter(HttpServletRequest request)
    {
        List<String> acceptableParameters = new ArrayList<>();
        acceptableParameters.add("page");
        acceptableParameters.add("customerId");

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
