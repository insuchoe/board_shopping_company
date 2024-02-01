package com.brandedCompany.controller.customer.interceptor;

import com.brandedCompany.util.CustomerControllerUtils;
import org.checkerframework.checker.index.qual.LessThan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

@Component
public class CustomerControllerRequestParameterInterceptor implements HandlerInterceptor
{
    final Logger logger = LoggerFactory.getLogger(CustomerControllerRequestParameterInterceptor.class);

    CustomerControllerUtils utils;

    @Autowired
    public CustomerControllerRequestParameterInterceptor(CustomerControllerUtils utils)
    {
        this.utils = utils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        logger.info("preHandle");
//        System.out.println("request.getRequestURI() = " + request.getRequestURI());
        if (!request.getMethod()
                    .equalsIgnoreCase("get"))
            return true;

        String requestURI = request.getRequestURI();
//        if (requestURI.equals("/customer/loginOut/login"))
//            return true;

        //장바구니 페이지 요청
         if (Pattern.matches("^/brandedCompany/customer/[0-9]+/cart?\\S*$",requestURI))
            utils.checkCartRequestParameter(request);
        //상품 페이지 요청
         else if(Pattern.matches("^/brandedCompany/customer/[0-9]+/product?\\S*$",requestURI))
            utils.checkProductRequestParameter(request);
        //주문내역 페이지 요청
         else if(Pattern.matches("^/brandedCompany/customer/[0-9]+/orderHistory?\\S*$",requestURI))
            utils.checkOrderHistoryRequestParmeter(request);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception
    {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception
    {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
