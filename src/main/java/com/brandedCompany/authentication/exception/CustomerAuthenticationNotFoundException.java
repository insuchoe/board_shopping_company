package com.brandedCompany.authentication.exception;

import org.apache.ibatis.javassist.NotFoundException;

public class CustomerAuthenticationNotFoundException extends AuthenticationNotFoundException
{
    private static final long serialVersionUID = 1519329899479269518L;

    public CustomerAuthenticationNotFoundException()
    {
    }

    public CustomerAuthenticationNotFoundException(String message)
    {
        super(message);
    }
}
