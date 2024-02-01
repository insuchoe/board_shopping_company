package com.brandedCompany.authentication.interceptor;

import com.brandedCompany.authentication.CustomerAuthenticationManager;
import com.brandedCompany.authentication.util.AuthenticationUtil;
import com.brandedCompany.domain.Customer;
import com.brandedCompany.domain.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.sql.Connection;
import java.util.regex.Pattern;

// 고객 인증 검증기
@Component
public class CustomerConnectorAuthenticationInterceptor implements HandlerInterceptor
{
    final Logger logger = LoggerFactory.getLogger(CustomerConnectorAuthenticationInterceptor.class);

    private final AuthenticationUtil util;
    private final CustomerAuthenticationManager manager;

    @Autowired
    public CustomerConnectorAuthenticationInterceptor(AuthenticationUtil util, CustomerAuthenticationManager manager)
    {
        this.util = util;
        this.manager = manager;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        logger.info("preHandle");
        String requestURI = request.getRequestURI();
//        System.out.println("requestURI = " + requestURI);
        //        if (requestURI.equals("/customer/loginOut/login"))
        //            return true;
        //        logger.info("request.getRequestURI() = " + requestURI);
        //        System.out.println("requestURI = " + requestURI);
        //        BigInteger connectorId = util.getConnectorId(requestURI);
        BigInteger connectorId = ((Customer) request.getSession()
                                                    .getAttribute("customer")).getCustomerId();

        if (manager.hasAuthentication(connectorId))
            return true;
        else
        {
            response.sendRedirect("/customer/loginOut/login");
            return false;
        }
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception
    {
        logger.info("postHandle");
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/loginOut/logout"))
            return;
        //        logger.info("requestURI = "+request.getRequestURI());
        //        if (requestURI.equals("/customer/loginOut/login"))
        //            return ;

        BigInteger connectorId  = ((Customer) request.getSession()
                                             .getAttribute("customer")).getCustomerId();
            if (manager.hasAuthentication(connectorId))
                return;
            else
                response.sendRedirect("/customer/loginOut/login");

    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception
    {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

}
