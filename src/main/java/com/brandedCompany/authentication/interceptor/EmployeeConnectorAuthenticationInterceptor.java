package com.brandedCompany.authentication.interceptor;

import com.brandedCompany.authentication.Authentication;
import com.brandedCompany.authentication.CustomerAuthenticationManager;
import com.brandedCompany.authentication.EmployeeAuthenticationManager;
import com.brandedCompany.authentication.exception.AuthenticationNullPointerException;
import com.brandedCompany.authentication.util.AuthenticationUtil;
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

// 고객 인증 검증기
@Component
public class EmployeeConnectorAuthenticationInterceptor implements HandlerInterceptor
{
    final Logger logger = LoggerFactory.getLogger(EmployeeConnectorAuthenticationInterceptor.class);

    private final AuthenticationUtil util;
    private final EmployeeAuthenticationManager manager;

    @Autowired
    public EmployeeConnectorAuthenticationInterceptor(AuthenticationUtil util, EmployeeAuthenticationManager manager)
    {
        this.util = util;
        this.manager = manager;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        logger.info("preHandle");
        String requestURI = request.getRequestURI();
//        if (requestURI.equals("/employee/loginOut/login"))
//            return true;

//        BigInteger connectorId = util.getConnectorId(requestURI);
        BigInteger connectorId=((Employee)request.getSession().getAttribute("employee")).getEmployeeId();
        if (manager.hasAuthentication(connectorId))
            return true;
        else
        {
            response.sendRedirect("/employee/loginOut/login");
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
//        if (requestURI.equals("/employee/loginOut/login"))
//            return;

//        BigInteger connectorId = util.getConnectorId(requestURI);
        BigInteger connectorId=((Employee)request.getSession().getAttribute("employee")).getEmployeeId();
         if (manager.hasAuthentication(connectorId))
            return;
        else
            response.sendRedirect("/employee/loginOut/login");
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception
    {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

}
