package com.brandedCompany.authentication.interceptor;

import com.brandedCompany.authentication.EmployeeAuthentication;
import com.brandedCompany.authentication.EmployeeAuthenticationManager;
import com.brandedCompany.authentication.exception.AuthenticationFailException;
import com.brandedCompany.authentication.exception.AuthenticationRuntimeException;
import com.brandedCompany.authentication.exception.EmployeeAuthenticationFailException;
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
@Component
public class EmployeeLoginAuthenticationInterceptor implements HandlerInterceptor
{
    final Logger logger = LoggerFactory.getLogger(EmployeeLoginAuthenticationInterceptor.class);

    private final AuthenticationUtil util;

    private final EmployeeAuthenticationManager manager;
    @Autowired
    public EmployeeLoginAuthenticationInterceptor(AuthenticationUtil util
                                                 , EmployeeAuthenticationManager manager)
    {
        this.util = util;
        this.manager=manager;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        logger.info("preHandle");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception
    {
        logger.info("postHandle");
//        if (!request.getRequestURI().equals("/employee/loginOut/login"))
//            return ;
        String uri = "/employee/loginOut/login";
        if(!(request.getRequestURI().equals(uri)) ||  (request.getRequestURI().equals(uri)&&request.getMethod().equalsIgnoreCase("GET")))return;

        BigInteger authenticationId = ((Employee) ((modelAndView.getModel()).get("employee"))).getEmployeeId();
        EmployeeAuthentication authentication = null;
            boolean isValid = false;

        try
        {
            authentication = (EmployeeAuthentication) manager.getAuthentication(authenticationId);
            //인증 최신화
            manager.refreshAuthentication(authentication);
            // 인증 유효 여부
            isValid = util.isValidAuthentication(authentication);

            logger.info(authentication.getTarget().getEmployeeId()+" 번 직원 인증 "+(isValid? "유효":"만료"));

            if (!isValid)
            {
                manager.removeAuthentication(authentication);
                throw new Exception();
            }
        }
        catch (AuthenticationRuntimeException e)
        {
            //    e.printStackTrace();
            throw new Exception("Refreshing employee authentication status",e);
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception
    {
        logger.info("afterCompletion");
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
