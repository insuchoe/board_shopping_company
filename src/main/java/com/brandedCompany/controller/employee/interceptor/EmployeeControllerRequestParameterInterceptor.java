package com.brandedCompany.controller.employee.interceptor;

import com.brandedCompany.authentication.interceptor.EmployeeConnectorAuthenticationInterceptor;
import com.brandedCompany.util.EmployeeControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.plaf.TableUI;
import java.util.regex.Pattern;

@Component
public class EmployeeControllerRequestParameterInterceptor implements HandlerInterceptor
{
    final Logger logger = LoggerFactory.getLogger(EmployeeControllerRequestParameterInterceptor.class);

    EmployeeControllerUtils utils;

    @Autowired
    public EmployeeControllerRequestParameterInterceptor(EmployeeControllerUtils utils)
    {
        this.utils = utils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        logger.info("preHandle");
        if (!request.getMethod()
                    .equalsIgnoreCase("get"))
            return true;

        String requestURI = request.getRequestURI();
//        if (request.equals("/employee/loginOut/login"))
//            return true;
        //새 게시글 페이지 URI
        if (Pattern.matches("^/brandedCompany/employee/[0-9]+/board/new?\\S*$",requestURI)||
            // 게시글 페이지 URI
            Pattern.matches("^/brandedCompany/employee/[0-9]+/board?\\S*$",requestURI))
            utils.checkBoardRequestParameter(request);
            //인벤토리 페이지 요청
        else if (Pattern.matches("^/brandedCompany/employee/[0-9]+/inventory?\\S*$",requestURI))
            utils.checkInventoryRequestParameter(request);
            //주문 내역 페이지 요청
        else if (Pattern.matches("^/brandedCompany/employee/[0-9]+/orderHistory?\\S*$",requestURI))
            utils.checkAllCustomerOrderHistoryRequestParameter(request);
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
