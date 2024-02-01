package com.brandedCompany.authentication.interceptor;

import com.brandedCompany.authentication.CustomerAuthentication;
import com.brandedCompany.authentication.CustomerAuthenticationManager;
import com.brandedCompany.authentication.exception.AuthenticationRuntimeException;
import com.brandedCompany.authentication.util.AuthenticationUtil;
import com.brandedCompany.domain.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.net.URI;

// 고객 인증 검증기
@Component
public class CustomerLoginAuthenticationInterceptor implements HandlerInterceptor
{
    final Logger logger = LoggerFactory.getLogger(CustomerLoginAuthenticationInterceptor.class);

    private final AuthenticationUtil util;
    private final CustomerAuthenticationManager manager;

    @Autowired
    public CustomerLoginAuthenticationInterceptor(AuthenticationUtil util,CustomerAuthenticationManager manager)
    {
        this.util = util;
        this.manager = manager;
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
//        if (!request.getRequestURI().equals("/customer/loginOut/login"))
//            return ;
        String uri = "/customer/loginOut/login";
        if(!(request.getRequestURI().equals(uri)) || (request.getRequestURI().equals(uri)&&request.getMethod().equalsIgnoreCase("GET")))
            return;

        BigInteger authenticationId = ((Customer) ((modelAndView.getModel()).get("customer"))).getCustomerId();
        CustomerAuthentication authentication = null;
        boolean isValid = false;

            try
            {
                authentication = (CustomerAuthentication) manager.getAuthentication(authenticationId);
                //인증 최신화
                manager.refreshAuthentication(authentication);
                // 인증 유효 여부
                isValid = util.isValidAuthentication(authentication);

                logger.info(authentication.getTarget().getCustomerId()+" 번 고객 인증 "+(isValid? "유효":"만료"));

                if (!isValid)
                {
                    manager.removeAuthentication(authentication);
                    throw new Exception();
                }
            }
            catch (AuthenticationRuntimeException e)
            {
                //  e.printStackTrace();
                throw new Exception("Refreshing customer authentication status",e);
            }


    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception
    {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        logger.info("afterCompletion");
    }

}
