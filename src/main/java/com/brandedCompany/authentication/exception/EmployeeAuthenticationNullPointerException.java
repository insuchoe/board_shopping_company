package com.brandedCompany.authentication.exception;

import com.brandedCompany.authentication.interceptor.EmployeeLoginAuthenticationInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmployeeAuthenticationNullPointerException extends AuthenticationRuntimeException
{


    private static final long serialVersionUID = 116071984083956393L;
    final Logger logger = LoggerFactory.getLogger(EmployeeLoginAuthenticationInterceptor.class);
    public EmployeeAuthenticationNullPointerException()
    {
    }

    public EmployeeAuthenticationNullPointerException(String s)
    {
        super(s);
    }
}
