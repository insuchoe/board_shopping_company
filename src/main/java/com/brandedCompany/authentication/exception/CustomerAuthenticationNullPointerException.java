package com.brandedCompany.authentication.exception;

import com.brandedCompany.authentication.interceptor.EmployeeLoginAuthenticationInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

public class CustomerAuthenticationNullPointerException extends AuthenticationRuntimeException
{
    private static final long serialVersionUID = 5957538833859231516L;
    private String message;
    final Logger logger = LoggerFactory.getLogger(EmployeeLoginAuthenticationInterceptor.class);

    @Override
    public String getMessage()
    {
        return message;
    }

    public CustomerAuthenticationNullPointerException(String message)
    {
        this.message = message;
    }

    public CustomerAuthenticationNullPointerException(String s, String message)
    {
        super(s);
        this.message = message;
    }
}
